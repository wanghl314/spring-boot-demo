CREATE TABLE sbd_dept (
    id bigint NOT NULL,
    name varchar(255) DEFAULT NULL,
    showorder varchar(32) DEFAULT NULL,
    PRIMARY KEY (id)
);

CREATE INDEX idx_d_n ON sbd_dept (name);