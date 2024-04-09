create table member
(
    id                bigserial
        primary key,
    created_at        timestamp(6),
    deleted_at        timestamp(6),
    modified_at       timestamp(6),
    email             varchar(50)  not null
        constraint uk_mbmcqelty0fbrvxp1q58dn57t
            unique,
    follow_cnt        integer      not null,
    follow_member_cnt integer      not null,
    provider          varchar(10)  not null
        constraint member_provider_check
            check ((provider)::text = ANY
                   ((ARRAY ['KAKAO'::character varying, 'GOOGLE'::character varying, 'APPLE'::character varying])::text[])),
    provider_id       varchar(255) not null
        constraint uk_q4jvd8lnevoqq74bkjcm3p6ub
            unique,
    refresh_token     varchar(255),
    role              varchar(10)  not null
        constraint member_role_check
            check ((role)::text = ANY
                   ((ARRAY ['ADMIN'::character varying, 'MEMBER'::character varying, 'MANAGER'::character varying, 'GUEST'::character varying])::text[]))
);

create table member_profile
(
    id            bigserial
        primary key,
    created_at    timestamp(6),
    deleted_at    timestamp(6),
    modified_at   timestamp(6),
    birthday      date,
    gender        char        not null
        constraint member_profile_gender_check
            check (gender = ANY (ARRAY ['M'::bpchar, 'F'::bpchar])),
    name          varchar(20) not null,
    nickname      varchar(20)
        constraint uk_fomebbbuowrk1nbkdbowg8ao8
            unique,
    phone         varchar(15),
    profile_image varchar(255),
    member_id     bigint      not null
        constraint uk_c87j55l2co10d3t5m5xmbutu1
            unique
        constraint fkkrtcs7wdtv954e99w88uga9mq
            references member
);

create table follow
(
    id               bigserial
        primary key,
    created_at       timestamp(6),
    deleted_at       timestamp(6),
    modified_at      timestamp(6),
    follow_member_id bigint
        constraint fkn8p72aaurx3gchlx2ybs6x98j
            references member,
    member_id        bigint
        constraint fkla8lvflaauks5sw7s0u44q6x0
            references member
);

create table notification
(
    id          bigserial
        primary key,
    created_at  timestamp(6),
    deleted_at  timestamp(6),
    modified_at timestamp(6),
    message     varchar(255),
    member_id   bigint
        constraint fk1xep8o2ge7if6diclyyx53v4q
            references member
);

create table post
(
    id               bigserial
        primary key,
    created_at       timestamp(6),
    deleted_at       timestamp(6),
    modified_at      timestamp(6),
    comment_cnt      integer      not null,
    favorite_cnt     integer      not null,
    place_block_cnt  smallint     not null,
    status           varchar(10)  not null
        constraint post_status_check
            check ((status)::text = ANY
                   ((ARRAY ['DRAFT'::character varying, 'PUBLISHED'::character varying, 'ARCHIVED'::character varying, 'DELETED'::character varying, 'FLAGGED'::character varying])::text[])),
    thumbnail        varchar(255),
    title            varchar(255) not null,
    visit_end_date   date         not null,
    visit_region     varchar(255) not null,
    visit_start_date date         not null,
    member_id        bigint
        constraint fk83s99f4kx8oiqm3ro0sasmpww
            references member
);

create table text_block
(
    id           bigserial
        primary key,
    created_at   timestamp(6),
    deleted_at   timestamp(6),
    modified_at  timestamp(6),
    content      text     not null,
    order_number smallint not null,
    post_id      bigint   not null
        constraint fk7i07eflq7yn0y64c13ebpnjx2
            references post
);

create table place
(
    id                    bigserial
        primary key,
    created_at            timestamp(6),
    deleted_at            timestamp(6),
    modified_at           timestamp(6),
    address_city          varchar(20)           not null,
    address_detail        varchar(20),
    address_district      varchar(20)           not null,
    address_province      varchar(20)           not null,
    coordinate            geometry(Point, 4326) not null,
    description           varchar(500),
    link                  varchar(255),
    name                  varchar(255)          not null,
    place_search_provider varchar(10)           not null
        constraint place_place_search_provider_check
            check ((place_search_provider)::text = ANY
                   ((ARRAY ['KAKAO'::character varying, 'MEMBER'::character varying])::text[])),
    provider_id           bigint
        constraint uk_j3it2wiq1brhhusjbmmqwo3un
            unique,
    road_address          varchar(255),
    thumbnail             varchar(255)
);

create table place_category
(
    id          bigserial
        primary key,
    created_at  timestamp(6),
    deleted_at  timestamp(6),
    modified_at timestamp(6),
    name        varchar(20) not null
);

create table place_block
(
    id                                  bigserial
        primary key,
    created_at                          timestamp(6),
    deleted_at                          timestamp(6),
    modified_at                         timestamp(6),
    content                             text     not null,
    order_number                        smallint not null,
    rating                              smallint not null,
    representative_place_block_image_id bigint,
    visit_end_date                      date,
    visit_start_date                    date,
    member_id                           bigint   not null
        constraint fkh390wag8fqy7naoityuhlxey3
            references member,
    place_id                            bigint   not null
        constraint fkg36ma5lb278ltmid1vla5bgri
            references place,
    place_category_id                   bigint   not null
        constraint fkd2o3b386cf2nqhxktf02kdmk5
            references place_category,
    post_id                             bigint   not null
        constraint fkkmwqq6qwygnc7mflk5arebucy
            references post
);

create table place_block_image
(
    id             bigserial
        primary key,
    created_at     timestamp(6),
    deleted_at     timestamp(6),
    modified_at    timestamp(6),
    order_number   smallint     not null,
    storage_key    varchar(255) not null,
    place_block_id bigint       not null
        constraint fk4m36domyj1dyiv573rnf40lrd
            references place_block
);

create table favorite_post
(
    id          bigserial
        primary key,
    created_at  timestamp(6),
    deleted_at  timestamp(6),
    modified_at timestamp(6),
    member_id   bigint
        constraint fk32d173u8yfwpms2b59t0gncrg
            references member,
    post_id     bigint
        constraint fkjepmb3qlm4yoo9hygcqmc414y
            references post
);

create table scrap_post
(
    id          bigserial
        primary key,
    created_at  timestamp(6),
    deleted_at  timestamp(6),
    modified_at timestamp(6),
    member_id   bigint
        constraint fk41koo60ogey5bi3rwvtj6p7dn
            references member,
    post_id     bigint
        constraint fkqywepkqg86dejaasd43oi6fkn
            references post
);

create table trending_post
(
    id      bigint           not null
        primary key,
    score   double precision not null,
    post_id bigint
        constraint uk_ktgo38kyeisixhenvfnkptp30
            unique
        constraint fkjgxl3o0bypu8wghlttq3ibgvp
            references post
);

create table comment
(
    id                bigserial
        primary key,
    created_at        timestamp(6),
    deleted_at        timestamp(6),
    modified_at       timestamp(6),
    content           varchar(255) not null,
    favorite_cnt      integer      not null,
    group_number      integer      not null,
    status            varchar(10)  not null
        constraint comment_status_check
            check ((status)::text = ANY
                   ((ARRAY ['PUBLISHED'::character varying, 'DELETED'::character varying, 'FLAGGED'::character varying])::text[])),
    member_id         bigint       not null
        constraint fkmrrrpi513ssu63i2783jyiv9m
            references member,
    parent_comment_id bigint
        constraint fkhvh0e2ybgg16bpu229a5teje7
            references comment,
    post_id           bigint       not null
        constraint fks1slvnkuemjsq2kj4h3vhx7i1
            references post,
    reply_comment_id  bigint
        constraint fk925lwh0h0xx0e7es4syqdvcr9
            references comment
);

create table favorite_comment
(
    id          bigserial
        primary key,
    created_at  timestamp(6),
    deleted_at  timestamp(6),
    modified_at timestamp(6),
    comment_id  bigint not null
        constraint fkby1ld6gm61k4bbnttg6qa8uyo
            references comment,
    member_id   bigint not null
        constraint fke31apoup9w08nantu3w86oytw
            references member,
    post_id     bigint not null
        constraint fkjoiunug9hvikf2ewwd1loiw6q
            references post
);