create table department
(
    id   bigserial not null primary key,
    name varchar(255)
);

create table employee
(
    id            bigserial not null primary key,
    name          varchar(255),
    department_id bigint,
    FOREIGN KEY (department_id) references department (id)
);

insert into department (name)
values ('Organization 1');
insert into department (name)
values ('Organization 2');
insert into employee (name, department_id)
values ('Kolya', 1);
insert into employee (name, department_id)
values ('Peter', 1);
insert into employee (name, department_id)
values ('Dima', 1);
insert into employee (name, department_id)
values ('Andrew', 2);
insert into employee (name, department_id)
values ('Jacob', 2);
