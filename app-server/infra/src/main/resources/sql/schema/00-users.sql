CREATE TABLE IF NOT EXISTS `users`
(
    `seq`               BIGINT      NOT NULL AUTO_INCREMENT,
    `id`                BINARY(16)  NOT NULL PRIMARY KEY,
    `nickname`          VARCHAR(64) NOT NULL,
    `profile_image_url` TEXT,
    `is_deleted`        BOOLEAN     NOT NULL DEFAULT FALSE,
    `created_at`        DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`        DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `version`           BIGINT      NOT NULL,

    CONSTRAINT `uk_users_seq` UNIQUE (`seq`)
);


CREATE TABLE IF NOT EXISTS `users_authentications`
(
    `seq`         BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `user_id`     BINARY(16)  NOT NULL,
    `type`        VARCHAR(4)  NOT NULL,
    `provider_id` VARCHAR(64) NOT NULL,
    `password`    TEXT,
    `name`        TEXT,
    `version`     BIGINT      NOT NULL,

    CONSTRAINT `uk_users_authentications_seq` UNIQUE (`seq`),
    CONSTRAINT `fk_authentications_to_users_by_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
        ON DELETE CASCADE
);
