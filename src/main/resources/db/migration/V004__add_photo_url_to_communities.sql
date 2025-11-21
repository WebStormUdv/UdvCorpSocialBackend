-- V004__add_photo_url_to_communities.sql
-- Добавляет поле photo_url в таблицу communities

ALTER TABLE communities
    ADD COLUMN photo_url VARCHAR(255) DEFAULT NULL;