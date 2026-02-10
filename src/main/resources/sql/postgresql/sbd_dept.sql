CREATE TABLE sbd_dept (
    id bigserial NOT NULL,
    name VARCHAR(255) DEFAULT NULL,
    showorder VARCHAR(32) DEFAULT NULL,
    PRIMARY KEY (id)
);

CREATE INDEX idx_d_n ON sbd_dept (name);