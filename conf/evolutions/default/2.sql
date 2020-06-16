
# --- !Ups

CREATE SEQUENCE user_id_seq;

CREATE TABLE users (
    user_id                                     BIGINT NOT NULL DEFAULT NEXTVAL('user_id_seq'),
    username                                    VARCHAR NOT NULL UNIQUE,
    password                                    VARCHAR NOT NULL UNIQUE,
    name                                        VARCHAR NOT NULL,
    email                                       VARCHAR NOT NULL UNIQUE,
    phone_number                                BIGINT NOT NULL UNIQUE,
    created_at                                  TIMESTAMPTZ DEFAULT NOW() NOT NULL,
    updated_at                                  TIMESTAMPTZ DEFAULT NOW() NOT NULL,
    PRIMARY KEY (user_id)
);

CREATE TRIGGER set_timestamp
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE PROCEDURE trigger_set_timestamp();

# --- !Downs

DROP TRIGGER set_timestamp ON users;
DROP TABLE users;
DROP SEQUENCE user_id_seq;
