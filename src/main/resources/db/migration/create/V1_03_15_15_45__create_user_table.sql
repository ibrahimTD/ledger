create table if not exists users (
    user_id   UUID         primary key,
    user_name varchar(255) not null unique,
    email     varchar(255) not null unique,
    password  varchar(255) not null,
    created_at timestamp   not null
);