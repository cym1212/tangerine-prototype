CREATE TABLE comment_report
(
    id                  BIGSERIAL PRIMARY KEY,
    comment_id             BIGINT      NOT NULL,
    reporting_member_id BIGINT      NOT NULL,
    report_type_id      BIGINT      NOT NULL,
    content             VARCHAR(100),
    resolution_status   VARCHAR(10) NOT NULL
        CONSTRAINT comment_report_resolution_status_check
            CHECK ((resolution_status)::TEXT = ANY
                   ((ARRAY ['PENDING'::CHARACTER VARYING, 'RESOLVED'::CHARACTER VARYING, 'REJECTED'::CHARACTER VARYING])::TEXT[])),
    created_at          TIMESTAMP(6),
    FOREIGN KEY (comment_id) REFERENCES comment (id),
    FOREIGN KEY (reporting_member_id) REFERENCES member (id),
    FOREIGN KEY (report_type_id) REFERENCES report_type (id)
);

CREATE INDEX idx_comment_report_resolution_status ON comment_report (resolution_status);

CREATE INDEX idx_comment_report_comment_id ON comment_report (comment_id);

CREATE INDEX idx_comment_status ON comment (status);