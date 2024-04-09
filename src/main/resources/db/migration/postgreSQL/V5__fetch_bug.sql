ALTER TABLE member_profile
    DROP COLUMN caption;
ALTER TABLE place_block_image
    ADD COLUMN caption VARCHAR(255);
