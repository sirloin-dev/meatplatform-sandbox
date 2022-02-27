/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */;

CREATE TABLE IF NOT EXISTS `users`
(
    `id`                BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `uuid`              BINARY(16)  NOT NULL,
    `nickname`          VARCHAR(64) NOT NULL,
    `profile_image_url` TEXT        NOT NULL,
    `deleted_at`        DATETIME,
    `created_at`        DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`        DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `version`           BIGINT      NOT NULL,

    UNIQUE KEY UK_USERS_UUID (`uuid`)
);
