ALTER TABLE notification
    ALTER COLUMN member_id SET NOT NULL;

ALTER TABLE notification
    ADD COLUMN read BOOLEAN DEFAULT FALSE NOT NULL;

ALTER TABLE member
    ADD COLUMN unread_notifications_cnt INT DEFAULT 0 NOT NULL;