CREATE TABLE sbd_user (
    id bigint(20) NOT NULL AUTO_INCREMENT,
    username varchar(255) DEFAULT NULL,
    age int(11) DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY username (username(191))
);