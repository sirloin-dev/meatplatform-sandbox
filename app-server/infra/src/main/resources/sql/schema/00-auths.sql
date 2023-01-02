CREATE TABLE IF NOT EXISTS `rsa_certificates`
(
    `seq`          BIGINT     NOT NULL AUTO_INCREMENT,
    `id`           BINARY(16) NOT NULL PRIMARY KEY,
    `is_enabled`   BOOLEAN    NOT NULL,
    `key_size`     INT        NOT NULL,
    `public_key`   TEXT       NOT NULL,
    `private_key`  TEXT       NOT NULL,
    `issued_at`    DATETIME   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `active_until` DATETIME   NOT NULL,

    CONSTRAINT `uk_rsa_certificates_seq` UNIQUE (`seq`)
);
