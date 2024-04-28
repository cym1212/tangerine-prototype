ALTER TABLE member_profile
    ALTER COLUMN name DROP NOT NULL;
ALTER TABLE member_profile
    ALTER COLUMN gender DROP NOT NULL;
ALTER TABLE member_profile
    ALTER COLUMN nickname SET NOT NULL;