create table users (
    id bigint generated always as identity,
    email varchar(255) not null,
    password_hash varchar(255) not null,
    role varchar(20) not null,
    enabled boolean not null default true,
    created_at timestamp with time zone not null default now(),
    updated_at timestamp with time zone not null default now(),
    constraint pk_users primary key (id),
    constraint uk_users_email unique (email),
    constraint chk_users_role check (role in ('STUDENT', 'ADMIN'))
);

create table student_profiles (
    id bigint generated always as identity,
    user_id bigint not null,
    full_name varchar(160) not null,
    group_name varchar(80) not null,
    constraint pk_student_profiles primary key (id),
    constraint uk_student_profiles_user unique (user_id),
    constraint fk_student_profiles_user foreign key (user_id)
        references users (id)
        on delete cascade
);

create table courses (
    id bigint generated always as identity,
    user_id bigint not null,
    title varchar(160) not null,
    teacher_name varchar(160),
    semester smallint,
    color varchar(16),
    created_at timestamp with time zone not null default now(),
    updated_at timestamp with time zone not null default now(),
    constraint pk_courses primary key (id),
    constraint fk_courses_user foreign key (user_id)
        references users (id)
        on delete cascade,
    constraint uk_courses_user_title unique (user_id, title),
    constraint chk_courses_semester check (semester is null or semester between 1 and 10)
);

create table assignments (
    id bigint generated always as identity,
    user_id bigint not null,
    course_id bigint not null,
    title varchar(200) not null,
    description text,
    due_at timestamp with time zone not null,
    priority varchar(20) not null,
    status varchar(20) not null,
    created_at timestamp with time zone not null default now(),
    updated_at timestamp with time zone not null default now(),
    completed_at timestamp with time zone,
    constraint pk_assignments primary key (id),
    constraint fk_assignments_user foreign key (user_id)
        references users (id)
        on delete cascade,
    constraint fk_assignments_course foreign key (course_id)
        references courses (id)
        on delete restrict,
    constraint chk_assignments_priority check (priority in ('LOW', 'MEDIUM', 'HIGH')),
    constraint chk_assignments_status check (status in ('NEW', 'IN_PROGRESS', 'DONE', 'OVERDUE', 'ARCHIVED')),
    constraint chk_assignments_title_not_blank check (length(trim(title)) > 0)
);

create table reminders (
    id bigint generated always as identity,
    assignment_id bigint not null,
    remind_at timestamp with time zone not null,
    message varchar(500),
    enabled boolean not null default true,
    sent_at timestamp with time zone,
    created_at timestamp with time zone not null default now(),
    constraint pk_reminders primary key (id),
    constraint fk_reminders_assignment foreign key (assignment_id)
        references assignments (id)
        on delete cascade
);

create index idx_courses_user on courses (user_id);
create index idx_assignments_user_due on assignments (user_id, due_at);
create index idx_assignments_user_status on assignments (user_id, status);
create index idx_assignments_user_priority on assignments (user_id, priority);
create index idx_assignments_course on assignments (course_id);
create index idx_reminders_assignment on reminders (assignment_id);
