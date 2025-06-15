package com.alpaca.futsal_performance_lab_back.service.game;

import com.alpaca.futsal_performance_lab_back.dto.Internal.LineupWithTags;
import com.alpaca.futsal_performance_lab_back.entity.AppUser;
import com.alpaca.futsal_performance_lab_back.entity.SetAssign;
import com.alpaca.futsal_performance_lab_back.entity.Summary;
import com.alpaca.futsal_performance_lab_back.repository.AppUserRepository;
import com.alpaca.futsal_performance_lab_back.repository.SetAssignRepository;
import com.alpaca.futsal_performance_lab_back.repository.SummaryRepository;
import com.alpaca.futsal_performance_lab_back.service.utils.ScoreNormalizer;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GamePostProcessor {

    /**
     * ClickHouse 연결 (네임드 파라미터 지원)
     */
    private final NamedParameterJdbcTemplate clickHouseJdbc;
    private final ScoreNormalizer norm;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Postgres용 JPA 리포지터리
     */
    private final SummaryRepository summaryRepository;
    private final SetAssignRepository setAssignRepository;
    private final AppUserRepository appUserRepository;

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
            any(x) OVER (PARTITION BY tagId ORDER BY time ROWS BETWEEN 1 PRECEDING AND 1 PRECEDING) AS lagX,
            any(y) OVER (PARTITION BY tagId ORDER BY time ROWS BETWEEN 1 PRECEDING AND 1 PRECEDING) AS lagY,
            any(time) OVER (PARTITION BY tagId ORDER BY time ROWS BETWEEN 1 PRECEDING AND 1 PRECEDING) AS lagTime
        FROM measurements
        WHERE gameCode = :gameCode          -- ★ 파라미터
          AND setCode  = :setCode           -- ★ 파라미터
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
           any(spd_mps) OVER (PARTITION BY tag_id ORDER BY time ROWS BETWEEN 1 PRECEDING AND 1 PRECEDING) AS prev_speed,
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
        any(x) OVER (PARTITION BY tagId ORDER BY time ROWS BETWEEN 1 PRECEDING AND 1 PRECEDING) AS lagX1,
        any(y) OVER (PARTITION BY tagId ORDER BY time ROWS BETWEEN 1 PRECEDING AND 1 PRECEDING) AS lagY1,
        any(x) OVER (PARTITION BY tagId ORDER BY time ROWS BETWEEN 5 PRECEDING AND 5 PRECEDING) AS lagX5,
        any(y) OVER (PARTITION BY tagId ORDER BY time ROWS BETWEEN 5 PRECEDING AND 5 PRECEDING) AS lagY5
    FROM measurements
    WHERE gameCode = :gameCode
      AND setCode  = :setCode
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
),

/* ⑤ Coverage(수비) -----------------------------------------*/
coverage AS (
    /* 4000cm × 2000cm 필드 기준으로 격자 커버리지 계산 */
    SELECT
        tagId AS tag_id,
        least(
            count(DISTINCT (intDiv(x,100) * 10000 + intDiv(y,100))) * 100.0 / (40 * 20), 
            100
        ) AS coverage_score
        -- 필드 크기: 4000cm × 2000cm = 40개 × 20개 격자 = 800개 셀
        -- 전체 커버시 100점이 되도록 정규화
    FROM measurements
    WHERE gameCode = :gameCode
      AND setCode  = :setCode
    GROUP BY tag_id
)

/* ⑥ 최종 결과 ---------------------------------------------*/
SELECT
    b.tag_id                    AS tag_id,
    b.game_code                 AS game_code,
    b.set_code                  AS set_code,
    b.play_time_minutes         AS play_time_minutes,
    b.total_distance_km         AS total_distance_km,
    b.max_speed_kmph            AS max_speed_kmph,
    b.avg_speed_kmph            AS avg_speed_kmph,
    b.calories_burned_kcal      AS calories_burned_kcal,
    coalesce(s.sprint_count,0)  AS sprint_count,
    coalesce(s.sprint_dist_m,0) AS sprint_dist_m,
    coalesce(c.cod_count,0)     AS cod_count,

    /* ─ 점수 매핑 ─ */
    b.avg_speed_kmph            AS attack_score,
    b.max_speed_kmph            AS speed_score,
    coalesce(s.sprint_count,0)  AS aggression_score,
    coalesce(c.cod_count,0)     AS agility_score,
    b.total_distance_km         AS stamina_score,
    coalesce(cv.coverage_score,0) AS defense_score
FROM base AS b
LEFT JOIN sprints  AS s  ON b.tag_id = s.tag_id
LEFT JOIN cods     AS c  ON b.tag_id = c.tag_id
LEFT JOIN coverage AS cv ON b.tag_id = cv.tag_id
ORDER BY b.tag_id;

""";



    /**
     * 경기 종료 직후 호출되는 비동기 후처리
     */
    @Async
    @Transactional
    public void processAfterGameEnd(int gameId) throws JsonProcessingException {
        log.info("[Async] 게임 {} 종료 후 ClickHouse 집계 시작", gameId);

        // 1) 세트 조회
        List<SetAssign> sets = setAssignRepository.findByGame_GameId(gameId);
        if (sets.isEmpty()) {
            log.warn("[Async] 게임 {}: 세트가 없습니다. 작업 종료", gameId);
            return;
        }

        List<Summary> batch = new ArrayList<>();

        for (SetAssign set : sets) {
            // –––––––––––––––––––––––––––––––––––––––––––––––––––––––––
            // 2) 이 세트에 할당된 tagId ↔ userId 매핑 준비
            //    LineupWithTags: JSON 구조에 tagId, userId 함께 담겨 있다고 가정
            LineupWithTags lwt = objectMapper.readValue(
                    set.getLineup(), LineupWithTags.class);

            Map<String, String> tagToUser = new HashMap<>();
            // red team
            lwt.getRedTeam().forEach((position, pi) ->
                    tagToUser.put(pi.getTagId(), pi.getUserId()));
            // blue team
            lwt.getBlueTeam().forEach((position, pi) ->
                    tagToUser.put(pi.getTagId(), pi.getUserId()));

            // 3) ClickHouse 쿼리 실행
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("gameCode", gameId)
                    .addValue("setCode",  set.getSetAssignId());

            clickHouseJdbc.query(SUMMARY_SQL, params, (rs, rowNum) -> {
                // 4) 결과에서 tag_id 추출 → 세트별 매핑으로 userId 획득
                String tagId  = rs.getString("tag_id");
                String userId = tagToUser.get(tagId);
                if (userId == null) {
                    return null;
                }

                // 5) AppUser 로드
                AppUser realUser = appUserRepository.findById(userId)
                        .orElseThrow(() ->
                                new RuntimeException("사용자를 찾을 수 없음: " + userId));

                // 6) 원본 지표 읽기
                double attackRaw  = rs.getDouble("attack_score");
                double speedRaw   = rs.getDouble("speed_score");
                double aggrRaw    = rs.getDouble("aggression_score");
                double agiliRaw   = rs.getDouble("agility_score");
                double defenseRaw = rs.getDouble("defense_score");
                double stamRaw    = rs.getDouble("stamina_score");

                // 7) 정규화
                double attackN  = norm.toTenScale(attackRaw, 0, 8);
                double speedN   = norm.toTenScale(speedRaw, 0, 40);
                double aggrN    = norm.toTenScale(aggrRaw, 0, 15);
                double agiliN   = norm.toTenScale(agiliRaw, 0, 120);
                double defenseN = defenseRaw;
                double stamN    = norm.toTenScale(stamRaw, 0, 10);

                int gameScore = (int) Math.round(
                        attackN + speedN + aggrN + agiliN + defenseN + stamN);

                // 8) Summary 엔티티 생성
                Summary summary = Summary.builder()
                        .playTimeMinutes   (rs.getDouble("play_time_minutes"))
                        .sprintCount       (rs.getInt   ("sprint_count"))
                        .caloriesBurnedKcal(rs.getDouble("calories_burned_kcal"))

                        .gameScore        (gameScore)
                        .attackScore      (attackN)
                        .speedScore       (speedN)
                        .aggressionScore  (aggrN)
                        .agilityScore     (agiliN)
                        .defenseScore     (defenseN)
                        .staminaScore     (stamN)

                        .appUser    (realUser)
                        .game       (set.getGame())
                        .setAssign  (set)
                        .build();

                batch.add(summary);
                return null;
            });
        }

        // 9) 일괄 저장
        summaryRepository.saveAll(batch);
        log.info("[Async] 게임 {}: 세트 {}개, Summary {}건 저장 완료",
                gameId, sets.size(), batch.size());
    }
}
