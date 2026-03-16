create table if not exists transactions (
    id                  UUID           primary key,
    user_id             UUID           not null,
    amount              NUMERIC(19, 4) not null,
    currency            varchar(3)     not null,
    description         TEXT,
    counter_party_iban  varchar(34)    not null,
    created_at          timestamp      not null,
    constraint fk_user_id foreign key (user_id) references users(user_id)
);

create index idx_transaction_user_date on transactions (user_id, created_at);