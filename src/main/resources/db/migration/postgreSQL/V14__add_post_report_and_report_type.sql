CREATE TABLE report_type
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(20) NOT NULL
);

CREATE TABLE post_report
(
    id                  BIGSERIAL PRIMARY KEY,
    post_id             BIGINT      NOT NULL,
    reporting_member_id BIGINT      NOT NULL,
    report_type_id      BIGINT      NOT NULL,
    content             VARCHAR(100),
    resolution_status   VARCHAR(10) NOT NULL
        CONSTRAINT post_reports_resolution_status_check
            CHECK ((resolution_status)::TEXT = ANY
                   ((ARRAY ['PENDING'::CHARACTER VARYING, 'RESOLVED'::CHARACTER VARYING, 'REJECTED'::CHARACTER VARYING])::TEXT[])),
    created_at          TIMESTAMP(6),
    FOREIGN KEY (post_id) REFERENCES post (id),
    FOREIGN KEY (reporting_member_id) REFERENCES member (id),
    FOREIGN KEY (report_type_id) REFERENCES report_type (id)
);

CREATE INDEX idx_post_report_resolution_status ON post_report (resolution_status);

CREATE INDEX idx_post_report_post_id ON post_report (post_id);

INSERT INTO report_type (name) VALUES ('스팸');
INSERT INTO report_type (name) VALUES ('유료 광고 포함');
INSERT INTO report_type (name) VALUES ('거짓 정보');
INSERT INTO report_type (name) VALUES ('지식재산권 침해');
INSERT INTO report_type (name) VALUES ('괴롭힘 또는 개인 정보 침해');
INSERT INTO report_type (name) VALUES ('성인용 콘텐츠');
INSERT INTO report_type (name) VALUES ('기타');
