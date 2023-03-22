create table public.inflight_mesaages
(
    id         uuid                    not null
        primary key,
    queue_id   uuid                    not null
        constraint fk_queues
            references public.queues,
    message_id uuid                    not null,
    created_at timestamp default now(),
    processed  boolean   default false not null
);

alter table public.inflight_mesaages
    owner to azure_admin;

create table public.queues
(
    id                      uuid        not null
        constraint queues_pk
            primary key,
    name                    varchar(50) not null,
    max_retries             integer     not null
        constraint max_retries_range
            check ((max_retries >= 0) AND (max_retries <= 9)),
    visibility_timeout_secs integer     not null
        constraint visibility_timeout_secs_check
            check (visibility_timeout_secs > 0),
    created_at              timestamp default now()
);

alter table public.queues
    owner to azure_admin;


