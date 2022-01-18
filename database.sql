create table users
(
    id            serial
        primary key,
    username      varchar(255)            not null
        unique,
    status        integer      default 0,
    password      varchar(255)            not null,
    token         varchar(255),
    coins         integer      default 20 not null,
    total_battles integer      default 0,
    won_battles   integer      default 0,
    lost_battles  integer      default 0,
    elo           integer      default 100,
    bio           varchar(255),
    image         varchar(255) default 'No Image'::character varying,
    draws         integer      default 0,
    name          varchar(255)
);

alter table users
    owner to postgres;

create table packages
(
    id      serial
        primary key,
    name    varchar(255)  default 'Card Package'::character varying not null,
    price   numeric(6, 2) default 5                                 not null,
    is_sold boolean       default false
);

alter table packages
    owner to postgres;

create table cards
(
    id           serial
        primary key,
    name         varchar(255)  not null,
    damage       numeric(6, 2) not null,
    element_type varchar(255)  not null,
    card_type    varchar(255)  not null,
    package_id   integer
        constraint fk_package
            references packages
            on update cascade on delete cascade,
    user_id      integer
        constraint fk_user
            references users
            on update cascade on delete cascade,
    in_deck      boolean default false,
    is_locked    boolean default false,
    token        varchar(255)  not null
);

alter table cards
    owner to postgres;

create unique index cards_token_uindex
    on cards (token);

create table battles
(
    id       serial
        primary key,
    player_a integer
        constraint fk_player_a
            references users,
    player_b integer
        constraint fk_player_b
            references users,
    winner   integer
        constraint winner
            references users,
    finished boolean default false
);

alter table battles
    owner to postgres;

create table battle_rounds
(
    id          serial
        primary key,
    battle_id   integer not null
        constraint fk_battle
            references battles,
    card_a      integer not null
        constraint fk_card_a
            references cards,
    card_b      integer not null
        constraint fk_card_b
            references cards,
    winner_card integer
        constraint fk_winner_card
            references cards
);

alter table battle_rounds
    owner to postgres;

create table trades
(
    id       serial
        primary key,
    card_a   integer not null
        constraint fk_card_a
            references cards,
    card_b   integer
        constraint fk_card_b
            references cards,
    coins    integer default 0,
    accepted boolean
);

alter table trades
    owner to postgres;