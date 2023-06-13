CREATE TABLE user_tb
(
    id          Bigint AUTO_INCREMENT PRIMARY KEY,
    username    varchar(255) NOT NULL UNIQUE,
    nickname    varchar(255),
    email       varchar(255) NOT NULL,
    password    text         NOT NULL,
    profile     text,
    rate_id     Bigint       DEFAULT '1',
    rate_point  int          DEFAULT '20',
    role        varchar(255) DEFAULT 'ROLE_USER',
    provider    varchar(255),
    provider_id varchar(255),
    status_code int          DEFAULT '100',
    created_at  timestamp    NOT NULL
);

CREATE TABLE rate
(
    id         Bigint AUTO_INCREMENT PRIMARY KEY,
    rate_name  varchar(255) NOT NULL,
    created_at timestamp    NOT NULL
);

CREATE TABLE category
(
    id         Bigint AUTO_INCREMENT PRIMARY KEY,
    name       varchar(255) UNIQUE NOT NULL,
    created_at timestamp           NOT NULL
);


CREATE TABLE board
(
    id           Bigint AUTO_INCREMENT PRIMARY KEY,
    category_id  Bigint,
    title        varchar(255) NOT NULL,
    organizer_id Bigint       NOT NULL,
    content      text,
    img          text,
    event_id     Bigint       NOT NULL,
    status_code  int DEFAULT '200',
    views        int          NOT NULL,
    recommend    boolean,
    state        varchar(255) NOT NULL,
    city         varchar(255) NOT NULL,
    town         varchar(255) NOT NULL,
    created_at   timestamp    NOT NULL
);

CREATE TABLE event
(
    id           Bigint AUTO_INCREMENT PRIMARY KEY,
    latitude     double       NOT NULL,
    longtitude   double       NOT NULL,
    qty          int          NOT NULL,
    payment_type varchar(255) NOT NULL,
    start_at     timestamp    NOT NULL,
    end_at       timestamp    NOT NULL,
    price        int          NOT NULL,
    created_at   timestamp    NOT NULL
);

CREATE TABLE tag
(
    id         Bigint AUTO_INCREMENT PRIMARY KEY,
    board_id   Bigint    NOT NULL,
    comment    text,
    created_at timestamp NOT NULL
);

CREATE TABLE participant
(
    id          Bigint AUTO_INCREMENT PRIMARY KEY,
    event_id    Bigint    NOT NULL,
    user_id     Bigint    NOT NULL,
    qty         int       NOT NULL,
    limit_time  timestamp,
    status_code int DEFAULT '300',
    created_at  timestamp NOT NULL
);

CREATE TABLE payment
(
    id              Bigint AUTO_INCREMENT PRIMARY KEY,
    event_id        Bigint       NOT NULL,
    user_id         Bigint       NOT NULL,
    payment_info_id Bigint       NOT NULL,
    payment_type    varchar(255) NOT NULL,
    status_code     int DEFAULT '400',
    confirmed       boolean      NOT NULL,
    created_at      timestamp    NOT NULL
);

CREATE TABLE wishlist
(
    id         Bigint AUTO_INCREMENT PRIMARY KEY,
    user_id    Bigint    NOT NULL,
    board_id   Bigint    NOT NULL,
    created_at timestamp NOT NULL
);

CREATE TABLE my_category
(
    id          Bigint AUTO_INCREMENT PRIMARY KEY,
    user_id     Bigint    NOT NULL,
    category_id Bigint    NOT NULL,
    created_at  timestamp NOT NULL
);

CREATE TABLE chat_room
(
    id          Bigint AUTO_INCREMENT PRIMARY KEY,
    event_id    Bigint,
    creator_id  Bigint       NOT NULL,
    user_id     Bigint       NOT NULL,
    chat_type   varchar(255) NOT NULL,
    status_code int DEFAULT '500',
    created_at  timestamp NOT NULL
);

CREATE TABLE chat_message
(
    id          Bigint AUTO_INCREMENT PRIMARY KEY,
    chatroom_id Bigint    NOT NULL,
    sender_id   Bigint    NOT NULL,
    message     text      NOT NULL,
    created_at  timestamp NOT NULL
);

CREATE TABLE my_location
(
    id         Bigint AUTO_INCREMENT PRIMARY KEY,
    user_id    Bigint       NOT NULL UNIQUE,
    state      varchar(255) NOT NULL,
    city       varchar(255) NOT NULL,
    town       varchar(255) NOT NULL,
    created_at timestamp    NOT NULL
);

CREATE TABLE blacklist
(
    id              Bigint PRIMARY KEY AUTO_INCREMENT,
    user_id         Bigint    NOT NULL,
    blocked_user_id Bigint    NOT NULL,
    created_at      timestamp NOT NULL
);

CREATE TABLE report
(
    id          Bigint PRIMARY KEY AUTO_INCREMENT,
    reporter_id Bigint       NOT NULL,
    reported_id Bigint       NOT NULL,
    board_id    Bigint,
    title       varchar(255) NOT NULL,
    content     text         NOT NULL,
    report_type varchar(255) NOT NULL,
    status_code int DEFAULT '600',
    created_at  timestamp    NOT NULL
);

CREATE TABLE review
(
    id          Bigint PRIMARY KEY AUTO_INCREMENT,
    reviewer_id Bigint    NOT NULL,
    reviewed_id Bigint    NOT NULL,
    score       int       NOT NULL,
    comment     varchar(255),
    created_at  timestamp NOT NULL
);

CREATE TABLE my_account
(
    id             Bigint PRIMARY KEY AUTO_INCREMENT,
    user_id        Bigint       NOT NULL,
    brand          varchar(255) NOT NULL,
    account_number varchar(255) NOT NULL UNIQUE,
    created_at     timestamp    NOT NULL
);

CREATE TABLE payment_info
(
    id                   BIGINT AUTO_INCREMENT,
    event_id             Bigint NOT NULL,
    user_id              Bigint NOT NULL,
    event                VARCHAR(255),
    receipt_id           VARCHAR(255),
    order_id             VARCHAR(255),
    price                BIGINT,
    tax_free             BIGINT,
    cancelled_price      BIGINT,
    cancelled_tax_free   BIGINT,
    order_name           VARCHAR(255),
    company_name         VARCHAR(255),
    gateway_url          VARCHAR(255),
    metadata             TEXT,
    sandbox              BOOLEAN,
    pg                   VARCHAR(255),
    method               VARCHAR(255),
    method_symbol        VARCHAR(255),
    method_origin        VARCHAR(255),
    method_origin_symbol VARCHAR(255),
    currency             VARCHAR(255),
    receipt_url          VARCHAR(255),
    purchased_at         timestamp,
    cancelled_at         timestamp,
    requested_at         timestamp,
    escrow_status_locale VARCHAR(255),
    escrow_status        VARCHAR(255),
    status_locale        VARCHAR(255),
    status               BIGINT,
    card_data            TEXT,
    phone_data           TEXT,
    bank_data            TEXT,
    vbank_data           TEXT,
    created_at           timestamp
);


