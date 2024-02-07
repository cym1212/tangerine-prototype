INSERT INTO member (id, email, role, provider, follow_cnt, follow_member_cnt)
VALUES (1, 'hyh9510@nate.com', 'ADMIN', 'KAKAO', 0, 0);
INSERT INTO member_profile (name, birthday, gender, phone, nickname, thumbnail, member_id)
VALUES ('한창희', '1999-01-07', 'M', '01012345678', '송눈섭', 'https://www.naver.com', 1);
INSERT INTO member (id, email, role, provider, follow_cnt, follow_member_cnt)
VALUES (2, 'leech@gmail.com', 'ADMIN', 'KAKAO', 0, 0);
INSERT INTO member_profile (name, birthday, gender, phone, nickname, thumbnail, member_id)
VALUES ('이찬호', '1999-01-29', 'M', '01012345678', '리자노', 'https://www.naver.com', 2);
INSERT INTO member (id, email, role, provider, follow_cnt, follow_member_cnt)
VALUES (3, 'bflook08307@gmail.com', 'ADMIN', 'KAKAO', 0, 0);
INSERT INTO member_profile (name, birthday, gender, phone, nickname, thumbnail, member_id)
VALUES ('최민성', '2000-08-30', 'M', '01012345678', '문공표', 'https://www.naver.com', 3);
INSERT INTO member (id, email, role, provider, follow_cnt, follow_member_cnt)
VALUES (4, 'cym1212@nate.com', 'ADMIN', 'KAKAO', 0, 0);
INSERT INTO member_profile (name, birthday, gender, phone, nickname, thumbnail, member_id)
VALUES ('최영민', '2000-12-12', 'M', '01012345678', 'cym', 'https://www.naver.com', 4);

INSERT INTO place_category (id, name)
VALUES (1, '음식점');
INSERT INTO place_category (id, name)
VALUES (2, '카페');
INSERT INTO place_category (id, name)
VALUES (3, '관광지');
INSERT INTO place_category (id, name)
VALUES (4, '숙박');
INSERT INTO place_category (id, name)
VALUES (5, '쇼핑');
INSERT INTO place_category (id, name)
VALUES (6, '자연');
INSERT INTO place_category (id, name)
VALUES (7, '문화/예술');
INSERT INTO place_category (id, name)
VALUES (8, '놀이/오락');
INSERT INTO place_category (id, name)
VALUES (9, '교통');
INSERT INTO place_category (id, name)
VALUES (10, '축제/이벤트');
INSERT INTO place_category (id, name)
VALUES (11, '팝업');

INSERT INTO place (name, coordinates, thumbnail, address_province, address_city, address_district, address_detail,
                   road_address, description, place_search_provider)
VALUES ('강남대학교', ST_SetSRID(ST_MakePoint(37.27574, 127.13249), 4326), null, '경기도', '용인시', '기흥구', '구갈동 111',
        '경기 용인시 기흥구 강남로 40',
        null, 'MEMBER');


-- 게시글 조회 테스트
-- Inserting test data for the given Post entity structure

-- Post 1
INSERT INTO post (id, title, visit_start_date, visit_end_date, comment_cnt, favorite_cnt, place_block_cnt, representative_region,created_at,member_id)
VALUES (1, '테스트 제목 1', '2024-02-03', '2024-02-10', 0, 0, 0, '테스트 지역 1','2024-02-11',1);

-- Post 2
INSERT INTO post (id, title, visit_start_date, visit_end_date, comment_cnt, favorite_cnt, place_block_cnt, representative_region,created_at,member_id)
VALUES (2, '테스트 제목 2', '2024-02-10', '2024-02-17', 0, 0, 0, '테스트 지역 2','2024-02-18',2);

-- Post 3
INSERT INTO post (id, title, visit_start_date, visit_end_date, comment_cnt, favorite_cnt, place_block_cnt, representative_region,created_at,member_id)
VALUES (3, '테스트 제목 3', '2024-02-17', '2024-02-24', 0, 0, 0, '테스트 지역 3','2024-02-25',3);
