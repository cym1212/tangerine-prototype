create table if not exists place_category
(
    created_at  timestamp(6),
    deleted_at  timestamp(6),
    id          bigserial   not null,
    modified_at timestamp(6),
    name        varchar(20) not null,
    primary key (id)
);

create table if not exists comment
(
    favorite_cnt      integer      not null,
    group_number      integer      not null,
    created_at        timestamp(6),
    deleted_at        timestamp(6),
    id                bigserial    not null,
    member_id         bigint       not null,
    modified_at       timestamp(6),
    parent_comment_id bigint,
    post_id           bigint       not null,
    reply_comment_id  bigint,
    content           varchar(255) not null,
    primary key (id)
);

create table if not exists favorite_comment
(
    comment_id  bigint,
    created_at  timestamp(6),
    deleted_at  timestamp(6),
    id          bigserial not null,
    member_id   bigint,
    modified_at timestamp(6),
    primary key (id)
);

create table if not exists favorite_post
(
    created_at  timestamp(6),
    deleted_at  timestamp(6),
    id          bigserial not null,
    member_id   bigint,
    modified_at timestamp(6),
    post_id     bigint,
    primary key (id)
);

create table if not exists follow
(
    created_at       timestamp(6),
    deleted_at       timestamp(6),
    follow_member_id bigint,
    id               bigserial not null,
    member_id        bigint,
    modified_at      timestamp(6),
    primary key (id)
);

create table if not exists member
(
    created_at    timestamp(6),
    deleted_at    timestamp(6),
    id            bigserial   not null,
    modified_at   timestamp(6),
    provider      varchar(10) not null check (provider in ('KAKAO', 'NAVER', 'GOOGLE', 'APPLE')),
    role          varchar(10) not null check (role in ('ADMIN', 'MEMBER', 'MANAGER', 'GUEST')),
    email         varchar(50) not null unique,
    refresh_token varchar(255),
    primary key (id)
);

create table if not exists member_profile
(
    birthday    date,
    gender      char(1)     not null check (gender in ('M', 'F')),
    created_at  timestamp(6),
    deleted_at  timestamp(6),
    id          bigserial   not null,
    member_id   bigint      not null unique,
    modified_at timestamp(6),
    phone       varchar(15),
    name        varchar(20) not null,
    nickname    varchar(20),
    thumbnail   varchar(255),
    primary key (id)
);

create table if not exists notification
(
    created_at  timestamp(6),
    deleted_at  timestamp(6),
    id          bigserial not null,
    member_id   bigint,
    modified_at timestamp(6),
    primary key (id)
);

create table if not exists place
(
    created_at            timestamp(6),
    deleted_at            timestamp(6),
    id                    bigserial             not null,
    modified_at           timestamp(6),
    provider_id           bigint,
    description           varchar(500),
    address_city          varchar(255),
    address_detail        varchar(255),
    address_district      varchar(255),
    address_province      varchar(255),
    name                  varchar(255)          not null,
    place_search_provider varchar(10)           not null check (place_search_provider in ('KAKAO', 'MEMBER')),
    road_address          varchar(255),
    thumbnail             varchar(255),
    coordinates           geometry(Point, 4326) not null,
    primary key (id)
);

create table if not exists place_block
(
    order_number smallint  not null,
    rating       smallint  not null,
    place_category_id  bigint    not null,
    created_at   timestamp(6),
    deleted_at   timestamp(6),
    id           bigserial not null,
    modified_at  timestamp(6),
    place_id     bigint    not null,
    post_id      bigint    not null,
    content      TEXT      not null,
    primary key (id)
);

create table if not exists place_block_image
(
    order_number    smallint     not null,
    created_at      timestamp(6),
    deleted_at      timestamp(6),
    id              bigserial    not null,
    modified_at     timestamp(6),
    place_block_id  bigint       not null,
    image_mime_type varchar(10)  not null check (image_mime_type in ('JPEG', 'PNG', 'GIF', 'BMP')),
    image_url       varchar(255) not null,
    primary key (id)
);

create table if not exists post
(
    block_cnt    smallint     not null,
    comment_cnt  integer      not null,
    favorite_cnt integer      not null,
    visited_at   date         not null,
    created_at   timestamp(6),
    deleted_at   timestamp(6),
    id           bigserial    not null,
    member_id    bigint,
    modified_at  timestamp(6),
    city         varchar(255),
    district     varchar(255),
    province     varchar(255),
    title        varchar(255) not null,
    primary key (id)
);

create table if not exists scrap_post
(
    created_at  timestamp(6),
    deleted_at  timestamp(6),
    id          bigserial not null,
    member_id   bigint,
    modified_at timestamp(6),
    post_id     bigint,
    primary key (id)
);

create table if not exists text_block
(
    order_number smallint  not null,
    created_at   timestamp(6),
    deleted_at   timestamp(6),
    id           bigserial not null,
    modified_at  timestamp(6),
    post_id      bigint    not null,
    content      TEXT      not null,
    primary key (id)
);

create table if not exists trending_post
(
    created_at  timestamp(6),
    deleted_at  timestamp(6),
    id          bigserial not null,
    modified_at timestamp(6),
    post_id     bigint unique,
    primary key (id)
);