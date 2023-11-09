DROP TYPE IF EXISTS ARTICLE_STATUS CASCADE ;
CREATE TYPE ARTICLE_STATUS AS ENUM ('PUBLISHED', 'CREATED', 'MODERATING', 'REJECTED');

DROP TYPE IF EXISTS ROLE CASCADE ;
CREATE TYPE ROLE AS ENUM ('USER', 'ADMIN');

CREATE TABLE IF NOT EXISTS users
(
    user_id    BIGINT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name  VARCHAR(50) NOT NULL,
    username   VARCHAR(50) NOT NULL UNIQUE,
    email      VARCHAR(50) NOT NULL UNIQUE,
    birthdate  DATE        NOT NULL,
    role       ROLE        NOT NULL,
    about      TEXT CHECK ( char_length(about) <= 1000 ),
    is_banned  BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS messages
(
    message_id   BIGINT PRIMARY KEY,
    message      TEXT CHECK ( char_length(message) >= 1 ),
    CHECK ( char_length(message) <= 500 ),
    sender_id    BIGINT    NOT NULL REFERENCES users (user_id),
    recipient_id BIGINT    NOT NULL REFERENCES users (user_id),
    created      TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS articles
(
    article_id BIGINT PRIMARY KEY,
    title      VARCHAR(250)   NOT NULL,
    content    TEXT           NOT NULL,
    author_id  BIGINT         NOT NULL REFERENCES users (user_id),
    created    TIMESTAMP      NOT NULL,
    published  TIMESTAMP,
    likes      BIGINT,
    status     ARTICLE_STATUS NOT NULL
);

CREATE TABLE IF NOT EXISTS comments
(
    comment_id BIGINT PRIMARY KEY,
    comment    TEXT      NOT NULL CHECK ( char_length(comment) >= 1 ),
    CHECK ( char_length(comment) <= 500 ),
    created    TIMESTAMP NOT NULL,
    article_id BIGINT    NOT NULL REFERENCES articles (article_id) ON DELETE CASCADE,
    user_id    BIGINT    NOT NULL REFERENCES users (user_id)
);

CREATE TABLE IF NOT EXISTS tags
(
    tag_id BIGINT PRIMARY KEY,
    name   VARCHAR(50) NOT NULL
);


CREATE TABLE IF NOT EXISTS articles_tags
(
    article_id BIGINT REFERENCES articles (article_id),
    tag_id     BIGINT REFERENCES tags (tag_id),
    CONSTRAINT articles_tags_pk PRIMARY KEY (article_id, tag_id)
);