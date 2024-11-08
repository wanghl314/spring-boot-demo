CREATE TABLE sbd_user (
    id bigint IDENTITY NOT NULL,
    username varchar(255) DEFAULT NULL,
    age int DEFAULT NULL,
    PRIMARY KEY (id)
);

CREATE UNIQUE INDEX idx_u_u ON sbd_user (username);