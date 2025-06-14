-- Stadium 삽입 (이미 존재하면 무시)
INSERT INTO stadium (stadium_id, size, address, photo_url, operating_hour)
VALUES (1, '4000x2000', '충남 천안시 동남구 병천면 충절로 1600', 'https://www.koreatech.ac.kr', '08:00~22:00')
    ON CONFLICT (stadium_id) DO NOTHING;

-- Tag 삽입 (이미 존재하면 무시)
INSERT INTO tag (tag_id, assigned, stadium_id) VALUES ('AAAA1111', false, 1)
    ON CONFLICT (tag_id) DO NOTHING;

INSERT INTO tag (tag_id, assigned, stadium_id) VALUES ('BBBB1111', false, 1)
    ON CONFLICT (tag_id) DO NOTHING;

INSERT INTO tag (tag_id, assigned, stadium_id) VALUES ('CCCC1111', false, 1)
    ON CONFLICT (tag_id) DO NOTHING;

INSERT INTO tag (tag_id, assigned, stadium_id) VALUES ('DDDD1111', false, 1)
    ON CONFLICT (tag_id) DO NOTHING;

INSERT INTO tag (tag_id, assigned, stadium_id) VALUES ('FFFF1111', false, 1)
    ON CONFLICT (tag_id) DO NOTHING;


