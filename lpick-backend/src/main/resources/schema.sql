CREATE TABLE IF NOT EXISTS album (
                                     album_id       varchar(40) NOT NULL,
                                     name           varchar(100) NOT NULL,
                                     profile        varchar(200),
                                     release_date   timestamp,
                                     release_country varchar(50),
                                     label          varchar(50),
                                     wiki_id        varchar(40)
);

CREATE TABLE IF NOT EXISTS genre (
                                     genre_id varchar(40) NOT NULL,
                                     name     varchar(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS user_gear (
                                         user_gear_id varchar(40) NOT NULL,
                                         oauth_id     varchar(40) NOT NULL,
                                         eq_id        varchar(40) NOT NULL
);

CREATE TABLE IF NOT EXISTS page_revision (
                                             revision_id      varchar(40) NOT NULL,
                                             content          text         NOT NULL,
                                             revision_number  varchar(50) NOT NULL,
                                             created_at       timestamp    NOT NULL,
                                             wiki_id          varchar(40) NOT NULL,
                                             oauth_id         varchar(40) NOT NULL
);

CREATE TABLE IF NOT EXISTS tier (
                                    tier_id     varchar(40) NOT NULL,
                                    name        varchar(50) NOT NULL,
                                    point_scope integer       NOT NULL
);

CREATE TABLE IF NOT EXISTS comment (
                                       comment_id        varchar(40) NOT NULL,
                                       content           text         NOT NULL,
                                       created_at        timestamp    NOT NULL,
                                       modified_at       timestamp,
                                       is_del            varchar(10)  NOT NULL,
                                       article_id        varchar(40)  NOT NULL,
                                       parent_comment_id varchar(40)
);

CREATE TABLE IF NOT EXISTS wiki_page (
                                         wiki_id          varchar(40) NOT NULL,
                                         title            varchar(50) NOT NULL,
                                         current_revision varchar(50),
                                         status           varchar(10) NOT NULL
);

CREATE TABLE IF NOT EXISTS artist_like (
                                           artist_like_id varchar(40) NOT NULL,
                                           oauth_id       varchar(40) NOT NULL,
                                           artist_id      varchar(40) NOT NULL
);

CREATE TABLE IF NOT EXISTS article_bookmark (
                                                article_bookmark_id varchar(40) NOT NULL,
                                                oauth_id            varchar(40) NOT NULL,
                                                article_id          varchar(40) NOT NULL
);

CREATE TABLE IF NOT EXISTS album_genre (
                                           album_genre_id varchar(40) NOT NULL,
                                           album_id       varchar(40) NOT NULL,
                                           genre_id       varchar(40) NOT NULL
);

CREATE TABLE IF NOT EXISTS artist_album (
                                            artist_album_id varchar(40) NOT NULL,
                                            artist_id       varchar(40) NOT NULL,
                                            album_id        varchar(40) NOT NULL
);

CREATE TABLE IF NOT EXISTS debate_chat (
                                           dsc_id      varchar(40) NOT NULL,
                                           content     text         NOT NULL,
                                           created_at  timestamp    NOT NULL,
                                           is_blind    varchar(10)  NOT NULL,
                                           dt_id       varchar(40)  NOT NULL,
                                           oauth_id    varchar(40)  NOT NULL
);

CREATE TABLE IF NOT EXISTS user_info (
                                         oauth_id    varchar(40) NOT NULL,
                                         nickname    varchar(50) NOT NULL,
                                         profile     varchar(200),
                                         point       integer DEFAULT 0 NOT NULL,
                                         stack_point integer DEFAULT 0 NOT NULL,
                                         about       text,
                                         LPTI        char(4),
                                         tier_id     varchar(40) NOT NULL
);

CREATE TABLE IF NOT EXISTS user_album (
                                          user_album_id varchar(40) NOT NULL,
                                          record_file   varchar(200),
                                          album_id      varchar(40)  NOT NULL,
                                          oauth_id      varchar(40)  NOT NULL
);

CREATE TABLE IF NOT EXISTS review_like (
                                           review_like_id varchar(40) NOT NULL,
                                           oauth_id       varchar(40) NOT NULL,
                                           review_id      varchar(40) NOT NULL
);

CREATE TABLE IF NOT EXISTS article (
                                       article_id varchar(40) NOT NULL,
                                       title      varchar(50) NOT NULL,
                                       content    text         NOT NULL,
                                       created_at timestamp    NOT NULL,
                                       modified_at timestamp,
                                       is_del     varchar(10)  NOT NULL,
                                       oauth_id   varchar(40)  NOT NULL
);

CREATE TABLE IF NOT EXISTS comment_like (
                                            comment_like_id varchar(40) NOT NULL,
                                            oauth_id        varchar(40) NOT NULL,
                                            comment_id      varchar(40) NOT NULL
);

CREATE TABLE IF NOT EXISTS user_auth (
                                         auth_id  varchar(50) NOT NULL,
                                         oauth_id varchar(40) NOT NULL
);

CREATE TABLE IF NOT EXISTS wiki_bookmark (
                                             wiki_bookmark_id varchar(40) NOT NULL,
                                             oauth_id         varchar(40) NOT NULL,
                                             wiki_id          varchar(40) NOT NULL
);

CREATE TABLE IF NOT EXISTS artist (
                                      artist_id  varchar(40) NOT NULL,
                                      name       varchar(50) NOT NULL,
                                      debut_at   timestamp,
                                      group_name varchar(50),
                                      company    varchar(50),
                                      wiki_id    varchar(40)
);

CREATE TABLE IF NOT EXISTS review (
                                      review_id varchar(40) NOT NULL,
                                      star      real         NOT NULL,
                                      content   text         NOT NULL,
                                      oauth_id  varchar(40)  NOT NULL,
                                      wiki_id   varchar(40)  NOT NULL
);

CREATE TABLE IF NOT EXISTS gear_class (
                                          class_id   varchar(40) NOT NULL,
                                          class_name varchar(50)
);

CREATE TABLE IF NOT EXISTS debate (
                                      dt_id      varchar(40) NOT NULL,
                                      created_at timestamp    NOT NULL,
                                      is_end     varchar(10)  NOT NULL,
                                      wiki_id    varchar(40)  NOT NULL,
                                      oauth_id   varchar(40)  NOT NULL
);

CREATE TABLE IF NOT EXISTS auth (
                                    auth_id varchar(40) NOT NULL,
                                    name    varchar(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS article_like (
                                            article_like_id varchar(40) NOT NULL,
                                            oauth_id        varchar(40) NOT NULL,
                                            article_id      varchar(40) NOT NULL
);

CREATE TABLE IF NOT EXISTS gear (
                                    eq_id     varchar(40) NOT NULL,
                                    name      varchar(50) NOT NULL,
                                    model_name varchar(100),
                                    brand      varchar(50),
                                    eq_class   varchar(50) NOT NULL,
                                    wiki_id    varchar(40)
);

CREATE TABLE IF NOT EXISTS report (
                                      report_id      varchar(40) NOT NULL,
                                      oauth_id       varchar(40) NOT NULL,
                                      report_why     varchar(50) NOT NULL,
                                      report_explain text         NOT NULL,
                                      created_at     timestamp    NOT NULL
);

COMMENT ON COLUMN report.oauth_id IS '신고대상 id';

-- PK 제약조건 추가 (IF NOT EXISTS 미지원, 필요시 마이그레이션 도구로 관리)
ALTER TABLE album ADD CONSTRAINT PK_ALBUM PRIMARY KEY (album_id);
ALTER TABLE genre ADD CONSTRAINT PK_GENRE PRIMARY KEY (genre_id);
ALTER TABLE user_gear ADD CONSTRAINT PK_USER_GEAR PRIMARY KEY (user_gear_id);
ALTER TABLE page_revision ADD CONSTRAINT PK_PAGE_REVISION PRIMARY KEY (revision_id);
ALTER TABLE tier ADD CONSTRAINT PK_TIER PRIMARY KEY (tier_id);
ALTER TABLE comment ADD CONSTRAINT PK_COMMENT PRIMARY KEY (comment_id);
ALTER TABLE wiki_page ADD CONSTRAINT PK_WIKI_PAGE PRIMARY KEY (wiki_id);
ALTER TABLE artist_like ADD CONSTRAINT PK_ARTIST_LIKE PRIMARY KEY (artist_like_id);
ALTER TABLE article_bookmark ADD CONSTRAINT PK_ARTICLE_BOOKMARK PRIMARY KEY (article_bookmark_id);
ALTER TABLE album_genre ADD CONSTRAINT PK_ALBUM_GENRE PRIMARY KEY (album_genre_id);
ALTER TABLE artist_album ADD CONSTRAINT PK_ARTIST_ALBUM PRIMARY KEY (artist_album_id);
ALTER TABLE debate_chat ADD CONSTRAINT PK_DEBATE_CHAT PRIMARY KEY (dsc_id);
ALTER TABLE user_info ADD CONSTRAINT PK_USER_INFO PRIMARY KEY (oauth_id);
ALTER TABLE user_album ADD CONSTRAINT PK_USER_ALBUM PRIMARY KEY (user_album_id);
ALTER TABLE review_like ADD CONSTRAINT PK_REVIEW_LIKE PRIMARY KEY (review_like_id);
ALTER TABLE article ADD CONSTRAINT PK_ARTICLE PRIMARY KEY (article_id);
ALTER TABLE comment_like ADD CONSTRAINT PK_COMMENT_LIKE PRIMARY KEY (comment_like_id);
ALTER TABLE user_auth ADD CONSTRAINT PK_USER_AUTH PRIMARY KEY (auth_id, oauth_id);
ALTER TABLE wiki_bookmark ADD CONSTRAINT PK_WIKI_BOOKMARK PRIMARY KEY (wiki_bookmark_id);
ALTER TABLE artist ADD CONSTRAINT PK_ARTIST PRIMARY KEY (artist_id);
ALTER TABLE review ADD CONSTRAINT PK_REVIEW PRIMARY KEY (review_id);
ALTER TABLE gear_class ADD CONSTRAINT PK_GEAR_CLASS PRIMARY KEY (class_id);
ALTER TABLE debate ADD CONSTRAINT PK_DEBATE PRIMARY KEY (dt_id);
ALTER TABLE auth ADD CONSTRAINT PK_AUTH PRIMARY KEY (auth_id);
ALTER TABLE article_like ADD CONSTRAINT PK_ARTICLE_LIKE PRIMARY KEY (article_like_id);
ALTER TABLE gear ADD CONSTRAINT PK_GEAR PRIMARY KEY (eq_id);
ALTER TABLE report ADD CONSTRAINT PK_REPORT PRIMARY KEY (report_id);

ALTER TABLE user_auth ADD CONSTRAINT FK_Auth_TO_User_Auth_1 FOREIGN KEY (auth_id)
    REFERENCES auth (auth_id);
ALTER TABLE user_auth ADD CONSTRAINT FK_User_TO_User_Auth_1 FOREIGN KEY (oauth_id)
    REFERENCES user_info (oauth_id);
