create table department
(
    id   bigserial not null primary key,
    name varchar(255)
);

insert into department (name)
values ('Organization 1');
insert into department (name)
values ('Organization 2');
