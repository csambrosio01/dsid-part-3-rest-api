# --- !Ups

CREATE SEQUENCE address_id_seq;

CREATE TABLE address (
    address_id                              BIGINT NOT NULL DEFAULT NEXTVAL('address_id_seq'),
    address                                 VARCHAR NOT NULL,
    complement                              VARCHAR,
    neighborhood                            VARCHAR NOT NULL,
    city                                    VARCHAR NOT NULL,
    state                                   VARCHAR NOT NULL,
    zip_code                                CHAR(8) NOT NULL,
    country                                 VARCHAR NOT NULL,
    number                                  VARCHAR NOT NULL,
    PRIMARY KEY (address_id)
);

ALTER TABLE users ADD COLUMN address_id BIGINT;

ALTER TABLE users ADD CONSTRAINT FK_Address_id FOREIGN KEY (address_id) REFERENCES address(address_id);

# --- !Downs

ALTER TABLE users DROP CONSTRAINT FK_Address_id;

ALTER TABLE users DROP COLUMN address_id;

DROP TABLE address;
