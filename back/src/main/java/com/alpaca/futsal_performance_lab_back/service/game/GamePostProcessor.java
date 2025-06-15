package com.alpaca.futsal_performance_lab_back.service.game;

import com.alpaca.futsal_performance_lab_back.entity.AppUser;
import com.alpaca.futsal_performance_lab_back.entity.SetAssign;
import com.alpaca.futsal_performance_lab_back.entity.Summary;
import com.alpaca.futsal_performance_lab_back.repository.SetAssignRepository;
import com.alpaca.futsal_performance_lab_back.repository.SummaryRepository;
import com.alpaca.futsal_performance_lab_back.service.utils.ScoreNormalizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GamePostProcessor {

    /** ClickHouse 연결 (네임드 파라미터 지원) */
    private final NamedParameterJdbcTemplate clickHouseJdbc;
    private final ScoreNormalizer norm;

    /** Postgres용 JPA 리포지터리 */
    private final SummaryRepository summaryRepository;
    private final SetAssignRepository setAssignRepository;

    private static final String SUMMARY_SQL = """
WITH
/* ① 프레임별 거리·속도 계산 ----------------------------------*/
frames AS (
    SELECT
        tagId               AS tag_id,
        time, x, y,
        /* 거리(m) */
        sqrt(pow(x - lagX, 2) + pow(y - lagY, 2)) / 100.0            AS dist_m,
        /* 속도(m/s) */
        sqrt(pow(x - lagX, 2) + pow(y - lagY, 2))
          / greatest((time - lagTime) / 1000.0, 0.0001) / 100.0      AS spd_mps
    FROM (
        SELECT
            tagId, time, x, y,
            lagInFrame(x)    OVER w AS lagX,
            lagInFrame(y)    OVER w AS lagY,
            lagInFrame(time) OVER w AS lagTime
        FROM measurements
        WHERE gameCode = :gameCode          -- ★ 파라미터
          AND setCode  = :setCode           -- ★ 파라미터
        WINDOW w AS (PARTITION BY tagId ORDER BY time)
    )
    WHERE lagX IS NOT NULL
      AND dist_m < 3.72
      AND spd_mps * 3.6 < 40
),

/* ② 이동 통계 -----------------------------------------------*/
base AS (
    SELECT
        :gameCode  AS game_code,
        :setCode   AS set_code,
        tag_id,
        round((max(time) - min(time)) / 60000.0 , 2)  AS play_time_minutes,
        round(sum(dist_m)            / 1000.0 , 4)    AS total_distance_km,
        round(max(spd_mps) * 3.6     , 2)             AS max_speed_kmph,
        round(avg(spd_mps) * 3.6     , 2)             AS avg_speed_kmph,
        round((sum(dist_m) / 1000.0) * 100 , 2)       AS calories_burned_kcal
    FROM frames
    GROUP BY tag_id
),

/* ③ 스프린트 -----------------------------------------------*/
spr_sessions AS (
    SELECT *, spd_mps >= 5.56 AS is_fast,
           lagInFrame(spd_mps) OVER (PARTITION BY tag_id ORDER BY time) AS prev_speed,
           toUInt8(is_fast AND (prev_speed < 5.56 OR prev_speed IS NULL)) AS is_start
    FROM frames
),
spr_grouped AS (
    SELECT *, sum(is_start) OVER (PARTITION BY tag_id ORDER BY time) AS sess
    FROM spr_sessions
),
spr_stats AS (
    SELECT tag_id, sess,
           count()     AS frame_cnt,
           sum(dist_m) AS total_dist_m
    FROM spr_grouped
    WHERE is_fast
    GROUP BY tag_id, sess
),
sprints AS (
    SELECT tag_id,
           countIf(frame_cnt >= 15)               AS sprint_count,
           sumIf(total_dist_m, frame_cnt >= 15)   AS sprint_dist_m
    FROM spr_stats
    GROUP BY tag_id
),

/* ④ COD -----------------------------------------------------*/
cod_raw AS (
    SELECT
        tagId AS tag_id, x, y,
        lagInFrame(x, 1) OVER w AS lagX1,
        lagInFrame(y, 1) OVER w AS lagY1,
        lagInFrame(x, 5) OVER w AS lagX5,
        lagInFrame(y, 5) OVER w AS lagY5
    FROM measurements
    WHERE gameCode = :gameCode
      AND setCode  = :setCode
    WINDOW w AS (PARTITION BY tagId ORDER BY time)
),
cods AS (
    SELECT tag_id,
           sum(
               (sqrt(pow(x - lagX5, 2) + pow(y - lagY5, 2)) > 50) AND
               (sqrt(pow(x - lagX1, 2) + pow(y - lagY1, 2)) > 50) AND
               abs(
                   atan2((y - lagY5)/100 , (x - lagX5)/100) -
                   atan2((y - lagY1)/100 , (x - lagX1)/100)
               ) > 0.7854
           ) AS cod_count
    FROM cod_raw
    GROUP BY tag_id
)

/* ⑤ Coverage(수비) -----------------------------------------*/
coverage AS (
    /* 25 cm × 25 cm 격자(100단위) 셀을 얼마나 밟았는가 */
    SELECT
        tagId AS tag_id,
        least( count(DISTINCT intDiv(x,100), intDiv(y,100)) * 2 , 100 ) AS coverage_score
        -- 1 셀 = 2점, 최대 100점
    FROM measurements
    WHERE gameCode = :gameCode
      AND setCode  = :setCode
    GROUP BY tag_id
)

/* ⑥ 최종 결과 ---------------------------------------------*/
SELECT
    b.tag_id,
    /* ─ 원본 지표 ─ */          … 생략 …
    coalesce(c.cod_count ,0)          AS cod_count,

    /* ─ 점수 매핑 ─ */
    b.avg_speed_kmph                  AS attack_score,
    b.max_speed_kmph                  AS speed_score,
    coalesce(s.sprint_count,0)        AS aggression_score,
    coalesce(c.cod_count   ,0)        AS agility_score,
    b.total_distance_km               AS stamina_score,
    coalesce(cv.coverage_score,0)     AS defense_score        -- ★ NEW
FROM   base      AS b
LEFT JOIN sprints   AS s  USING (tag_id)
LEFT JOIN cods      AS c  USING (tag_id)
LEFT JOIN coverage  AS cv USING (tag_id)          -- ★ NEW
ORDER BY b.tag_id;
""";


    /**
     * 경기 종료 직후 호출되는 비동기 후처리
     */
    @Async
    @Transactional
    public void processAfterGameEnd(int gameId) {

        log.info("[Async] 게임 {} 종료 후 ClickHouse 집계 시작", gameId);

        // 1) 세트 조회
        List<SetAssign> sets = setAssignRepository.findByGame_GameId(gameId);
        if (sets.isEmpty()) {
            log.warn("[Async] 게임 {}: 세트가 없습니다. 작업 종료", gameId);
            return;
        }

        List<Summary> batch = new ArrayList<>();

        for (SetAssign set : sets) {

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("gameCode", gameId)
                    .addValue("setCode",  set.getSetAssignId());

            clickHouseJdbc.query(SUMMARY_SQL, params, (rs, rowNum) -> {

                // FK 프록시
                AppUser userProxy = AppUser.builder()
                        .userId(rs.getString("tag_id"))
                        .build();

                /* ─── 1. 원본 지표 읽기 ─── */
                double attackRaw  = rs.getDouble("attack_score");      // avg_speed  km/h
                double speedRaw   = rs.getDouble("speed_score");       // max_speed  km/h
                double aggrRaw    = rs.getDouble("aggression_score");  // sprint 횟수
                double agiliRaw   = rs.getDouble("agility_score");     // COD  횟수
                double defenseRaw = rs.getDouble("defense_score");     // coverage 0~100
                double stamRaw    = rs.getDouble("stamina_score");     // distance km

                /* ─── 2. 0~100 정규화 (min/max는 예시) ─── */
                double attackN  = norm.toTenScale(attackRaw, 0, 8);    // 20 km/h →
                double speedN   = norm.toTenScale(speedRaw, 0, 40);    // 40 km/h →
                double aggrN    = norm.toTenScale(aggrRaw, 0, 15);    // 30회 →
                double agiliN   = norm.toTenScale(agiliRaw, 0, 120);   // 120회 →
                double defenseN = defenseRaw;                      // 이미 0~100
                double stamN    = norm.toTenScale(stamRaw, 0, 10);     // 10 km →

                /* ─── 3. 600점 만점 gameScore ─── */
                int gameScore = (int) Math.round(
                        attackN + speedN + aggrN + agiliN + defenseN + stamN);

                /* ─── 4. Summary 엔티티 ─── */
                Summary summary = Summary.builder()
                        .playTimeMinutes   (rs.getDouble("play_time_minutes"))
                        .sprintCount       (rs.getInt   ("sprint_count"))
                        .caloriesBurnedKcal(rs.getDouble("calories_burned_kcal"))

                        .gameScore      (gameScore)
                        .attackScore    (attackN)
                        .speedScore     (speedN)
                        .aggressionScore(aggrN)
                        .agilityScore   (agiliN)
                        .defenseScore   (defenseN)
                        .staminaScore   (stamN)

                        .appUser  (userProxy)
                        .game     (set.getGame())
                        .setAssign(set)
                        .build();

                batch.add(summary);
                return null; // RowMapper
            });
        }

        // 5) 저장
        summaryRepository.saveAll(batch);

        log.info("[Async] 게임 {}: 세트 {}개, Summary {}건 저장 완료",
                gameId, sets.size(), batch.size());
    }
}
