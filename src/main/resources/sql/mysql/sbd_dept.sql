CREATE TABLE sbd_dept (
    id bigint(20) NOT NULL,
    name varchar(255) DEFAULT NULL,
    showorder varchar(32) DEFAULT NULL,
    PRIMARY KEY (id),
    KEY name (name(191))
);