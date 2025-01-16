create table delivery_code
(
    id            varchar(255) not null
        primary key,
    international bit          not null,
    name          varchar(255) not null
);

create table delivery_policy
(
    id        bigint auto_increment
        primary key,
    min_price int          not null,
    name      varchar(255) not null,
    price     int          not null
);

create table default_delivery_policy
(
    id                   bigint auto_increment
        primary key,
    delivery_policy_type enum ('DEFAULT', 'ERROR', 'EVENT') not null,
    delivery_policy_id   bigint                             not null,
    constraint UKl7qo9vkbm8e5fruiyttqw7s6n
        unique (delivery_policy_type),
    constraint FKc5ylb9pxw7rhyv5w1qk08fvr5
        foreign key (delivery_policy_id) references delivery_policy (id)
);

create table pay_type
(
    id   bigint auto_increment
        primary key,
    name varchar(255) not null
);

create table point_history
(
    id         bigint auto_increment
        primary key,
    amount     bigint                 not null,
    changed_at datetime(6)            null,
    comment    varchar(255)           null,
    member_id  bigint                 null,
    types      enum ('EARN', 'SPEND') null
);

create table point_policy
(
    id     bigint auto_increment
        primary key,
    amount int            not null,
    name   varchar(255)   not null,
    rate   decimal(38, 2) null
);

create table default_point_policy
(
    id                bigint auto_increment
        primary key,
    point_policy_type enum ('CONTENT_REVIEW', 'DEFAULT_BUY', 'ERROR', 'NO_CONTENT_REVIEW', 'REGISTER') not null,
    point_policy_id   bigint                                                                           not null,
    constraint UK45q23t4vkyo5oatf756fm8oof
        unique (point_policy_type),
    constraint FKmvttxmf1wrxk7g59g7swmdrp
        foreign key (point_policy_id) references point_policy (id)
);

create table wrapping
(
    id    bigint auto_increment
        primary key,
    name  varchar(255) not null,
    price int          not null
);

create table order_group
(
    id                   bigint auto_increment
        primary key,
    address              varchar(255) not null,
    delivery_price       int          not null,
    ordered_at           date         not null,
    ordered_name         varchar(255) not null,
    recipient_home_phone varchar(255) null,
    recipient_name       varchar(255) not null,
    recipient_phone      varchar(255) not null,
    user_id              bigint       null,
    wrapping_id          bigint       null,
    constraint FKthto9t09xng0eknv731x8a22f
        foreign key (wrapping_id) references wrapping (id)
);

create table delivery_info
(
    order_group_id bigint       not null
        primary key,
    arrived_at     date         null,
    invoice_number int          not null,
    name           varchar(255) null,
    shipping_at    date         null,
    constraint FKhr1u8pbg4w73xu68wksg0per3
        foreign key (order_group_id) references order_group (id)
);

create table order_detail
(
    id             bigint auto_increment
        primary key,
    amount         int                                                                                                                               not null,
    book_id        bigint                                                                                                                            not null,
    discount_price bigint                                                                                                                            not null,
    order_status   enum ('DELIVERED', 'ERROR', 'ORDER_CANCELED', 'PAYMENT_COMPLETED', 'PAYMENT_PENDING', 'RETURNED', 'RETURNED_PENDING', 'SHIPPING') null,
    prime_price    bigint                                                                                                                            not null,
    order_group_id bigint                                                                                                                            not null,
    constraint FKtgyj06r2y49ohq800h51witkk
        foreign key (order_group_id) references order_group (id),
    check (`amount` >= 1)
);

create table order_group_point_history
(
    id               bigint auto_increment
        primary key,
    order_group_id   bigint not null,
    point_history_id bigint not null,
    constraint FKm4u84i22s336tq9jjjsul334q
        foreign key (order_group_id) references order_group (id),
    constraint FKtfgs7k8ylcmbnsosuufskhypt
        foreign key (point_history_id) references point_history (id)
);

create table pay
(
    id             bigint auto_increment
        primary key,
    order_id       bigint                                                                                                             null,
    payment_key    varchar(255)                                                                                                       null,
    price          bigint                                                                                                             not null,
    requested_at   date                                                                                                               null,
    status         enum ('ABORTED', 'CANCELED', 'DONE', 'EXPIRED', 'IN_PROGRESS', 'PARTIAL_CANCELED', 'READY', 'WAITING_FOR_DEPOSIT') null,
    order_group_id bigint                                                                                                             null,
    pay_type_id    bigint                                                                                                             null,
    constraint FKpjkccc8lx7ej7sdlv98uubm8a
        foreign key (order_group_id) references order_group (id),
    constraint FKq33vq57w5eaa2vlldet4t2yiq
        foreign key (pay_type_id) references pay_type (id)
);

