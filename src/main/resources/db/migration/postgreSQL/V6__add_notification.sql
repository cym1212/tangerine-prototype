ALTER TABLE notification
    ADD COLUMN related_member_id bigint,
    ADD COLUMN related_post_id bigint,
    ADD COLUMN related_comment_id bigint,
    ADD CONSTRAINT fk_related_member_id
        FOREIGN KEY (related_member_id)
            REFERENCES member(id),
    ADD CONSTRAINT fk_related_post_id
        FOREIGN KEY (related_post_id)
            REFERENCES post(id),
    ADD CONSTRAINT fk_related_comment_id
        FOREIGN KEY (related_comment_id)
            REFERENCES comment(id);