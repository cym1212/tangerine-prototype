ALTER TABLE member_profile
    ADD CONSTRAINT nickname_format
        CHECK (nickname ~ '^[ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9_]+$');