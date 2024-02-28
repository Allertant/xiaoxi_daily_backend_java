create table plan
(
    id          bigint auto_increment comment '主键'
        primary key,
    name        varchar(50)   not null comment '计划名',
    user_id     varchar(50)   null comment '用户id',
    create_time datetime      null comment '创建时间',
    update_time datetime      null comment '更新时间',
    is_on       int default 0 not null comment '该计划是否正在被使用，1表示正在被使用，0表示没有使用中',
    is_deleted  int default 0 not null comment '是否被删除 0-否 1-是'
);

create table plan_detail
(
    id         bigint auto_increment comment '主键'
        primary key,
    plan_id    bigint        not null comment '计划id',
    begin_time time          not null comment '开始时间',
    end_time   time          not null comment '结束时间',
    order_num  int           not null comment '该计划项在整个计划中的位置',
    is_deleted int default 0 not null comment '是否被删除 0-否 1-是'
);

create table plan_record
(
    id             bigint auto_increment comment '主键'
        primary key,
    user_id        bigint        not null comment '用户id',
    plan_id        bigint        not null comment '计划id',
    plan_detail_id bigint        not null comment '计划项id',
    create_time    datetime      not null comment '记录时间',
    is_deleted     int default 0 not null comment '是否被删除 0-否 1-是'
);

create table user
(
    id         bigint auto_increment comment '主键'
        primary key,
    username   varchar(50)   not null comment '用户名',
    password   varchar(50)   not null comment '密码',
    phone      varchar(50)   null comment '手机号',
    status     int default 1 not null comment '用户状态：1-正常 2-封号中',
    is_deleted int default 0 null comment '是否被删除 0-否 1-是'
);