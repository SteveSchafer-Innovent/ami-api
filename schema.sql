drop database if exists ami;
create database ami;
use ami;
-- show engines;
-- show plugins;

drop table if exists user;
create table user (
	id int not null auto_increment,
	firstName varchar(255),
	lastName varchar(255),
	password varchar(255),
	username varchar(255),
	context text,
	PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- alter table ami.user add context text;
-- select * from user;

drop table if exists type;
create table type (
	id int not null auto_increment,
	name varchar(64) not null,
	created timestamp not null,
	creator int not null,
	primary key (id),
	key name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- alter table type add creator int not null;
-- alter table type add created timestamp not null;
-- select * from type;
-- update type set created = '2020-12-31';

drop table if exists user_type;
create table user_type (
	user_id int not null,
	type_id int not null,
	sort_order int not null,
	foreign key (user_id) references user(id) on delete restrict,
	foreign key (type_id) references type(id) on delete restrict
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

drop table if exists thing;
create table thing (
	id int not null auto_increment,
	type_id int not null,
	created timestamp not null,
	creator int not null,
	primary key (id),
	foreign key (creator) references user(id) on delete restrict,
	foreign key (type_id) references type(id) on delete restrict
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- select * from thing;

drop table if exists user_type_thing;
create table user_type_thing (
	user_id int not null,
	type_id int not null,
	thing_id int not null,
	sort_order int not null,
	primary key (user_id, type_id, thing_id),
	foreign key (user_id) references user(id) on delete restrict,
	foreign key (type_id) references type(id) on delete restrict,
	foreign key (thing_id) references thing(id) on delete restrict
);

-- select * from user_type_thing;

drop table if exists user_type_context_thing;
create table user_type_context_thing (
	user_id int not null,
	type_id int not null,
	context_thing_id int not null,
	thing_id int not null,
	sort_order int not null,
	primary key (user_id, type_id, context_thing_id, thing_id),
	foreign key (user_id) references user(id) on delete restrict,
	foreign key (type_id) references type(id) on delete restrict,
	foreign key (context_thing_id) references thing(id) on delete restrict,
	foreign key (thing_id) references thing(id) on delete restrict
);

-- select * from user_type_context_thing;

insert into type (name) values ('person');
select last_insert_id() into @type_id_person;
insert into type (name) values ('document');
select last_insert_id() into @type_id_document;
insert into type (name) values ('quote');
select last_insert_id() into @type_id_quote;
insert into type (name) values ('concept');
select last_insert_id() into @type_id_concept;

drop table if exists type_type;
create table type_type (
	type_id int not null,
	super_type_id int not null,
	foreign key (type_id) references type(id) on delete restrict,
	foreign key (super_type_id) references type(id) on delete restrict
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

drop table if exists attribute_defn;
create table attribute_defn (
	id int not null auto_increment,
	name varchar(64) not null,
	handler varchar(16) not null,
	type_id int not null,
	multiple tinyint(1) not null default 0;
	show_in_list tinyint(1) not null default 0;
	edit_in_list tinyint(1) not null default 0;
	sort_order float not null default 0;
	primary key (id),
	key name (name),
	foreign key (type_id) references type(id) on delete restrict
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- describe attribute_defn;
-- alter table attribute_defn add show_in_list tinyint(1) default 0 not null;
-- alter table attribute_defn add edit_in_list tinyint(1) default 0 not null;
-- alter table attribute_defn add sort_order float default 0 not null;
-- alter table attribute_defn drop `order`;

CREATE UNIQUE INDEX unique_name 
	ON ami.attribute_defn (name, type_id);

insert into attribute_defn (name, handler, type_id) values ('name', 'string', @type_id_document);
select last_insert_id() into @name_attr_defn_id;
insert into attribute_defn (name, handler, type_id) values ('text', 'rich-text', @type_id_document);
insert into attribute_defn (name, handler, type_id) values ('file', 'file', @type_id_document);
insert into attribute_defn (name, handler, type_id) values ('mime_type', 'string', @type_id_document);
insert into attribute_defn (name, handler, type_id) values ('name', 'string', @type_id_concept);
insert into attribute_defn (name, handler, type_id) values ('name', 'string', @type_id_person);
insert into attribute_defn (name, handler, type_id) values ('text', 'string', @type_id_quote);

-- drop table if exists link_defn_backup;
-- create table link_defn_backup like link_defn;
-- insert into link_defn_backup select * from link_defn;
drop table if exists link_defn;
create table link_defn (
	attribute_defn_id int not null,
	target_type_id int,
	primary key (attribute_defn_id),
	foreign key (attribute_defn_id) references attribute_defn(id) on delete restrict,
	foreign key (target_type_id) references type(id) on delete restrict
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- insert into link_defn select * from link_defn_backup;
-- select * from link_defn;
insert into link_defn values(31, null);

drop table if exists string_attribute;
create table string_attribute (
	thing_id int not null,
	attribute_defn_id int not null,
	value text not null,
	foreign key (thing_id) references thing(id) on delete restrict,
	foreign key (attribute_defn_id) references attribute_defn(id) on delete restrict
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- select * from string_attribute;

drop table if exists integer_attribute;
create table integer_attribute (
	thing_id int not null,
	attribute_defn_id int not null,
	value int not null,
	foreign key (thing_id) references thing(id) on delete restrict,
	foreign key (attribute_defn_id) references attribute_defn(id) on delete restrict
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

drop table if exists timestamp_attribute;
create table timestamp_attribute (
	thing_id int not null,
	attribute_defn_id int not null,
	value timestamp not null,
	foreign key (thing_id) references thing(id) on delete restrict,
	foreign key (attribute_defn_id) references attribute_defn(id) on delete restrict
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

insert into timestamp_attribute select * from datetime_attribute;
select * from timestamp_attribute;

drop table if exists boolean_attribute;
create table boolean_attribute (
	thing_id int not null,
	attribute_defn_id int not null,
	value tinyint not null,
	foreign key (thing_id) references thing(id) on delete restrict,
	foreign key (attribute_defn_id) references attribute_defn(id) on delete restrict
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

drop table if exists float_attribute;
create table float_attribute (
	thing_id int not null,
	attribute_defn_id int not null,
	value float not null,
	foreign key (thing_id) references thing(id) on delete restrict,
	foreign key (attribute_defn_id) references attribute_defn(id) on delete restrict
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

drop table if exists link_attribute;
create table link_attribute (
	thing_id int not null,
	attribute_defn_id int not null,
	target_thing_id int not null,
	primary key (thing_id, attribute_defn_id, target_thing_id),
	foreign key (thing_id) references thing(id) on delete restrict,
	foreign key (attribute_defn_id) references attribute_defn(id) on delete restrict,
	foreign key (target_thing_id) references thing(id) on delete restrict
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

drop table if exists file_attribute;
create table file_attribute (
	thing_id int not null,
	attribute_defn_id int not null,
	filename text,
	mime_type text,
	foreign key (thing_id) references thing(id) on delete restrict,
	foreign key (attribute_defn_id) references attribute_defn(id) on delete restrict
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

drop table if exists word_thing;
drop table if exists word;
create table word (
	id int not null primary key auto_increment,
	word varchar(32) not null unique
);
create table word_thing (
	word_id int not null,
	attribute_defn_id int not null,
	thing_id int not null,
	foreign key (word_id) references word(id) on delete cascade,
	foreign key (attribute_defn_id) references attribute_defn(id) on delete cascade,
	foreign key (thing_id) references thing(id) on delete cascade,
	primary key (word_id, attribute_defn_id, thing_id)
);

-- select * from word;
-- select * from word_thing;
-- ''

use ami;
select * from user;
select * from type;
select * from thing;
select * from attribute_defn;
select * from attribute;
select * from link_defn;
select * from string_attribute;
select * from float_attribute;
select * from boolean_attribute;
select * from link_attribute;
select * from file_attribute;
insert into attribute_defn (name, handler, type_id) values ('filename', 'string', 2);

-- ALTER TABLE ami.attribute RENAME TO string_attribute;
-- alter table ami.link rename to link_attribute;

select * from handprint.fingerprint;
describe handprint.hand_finger;
select * from handprint.hand_finger;
select * from handprint.person;

select * from link_defn;
select * from attribute_defn;
select * from type;
select * from link_attribute;
select * from timestamp_attribute;

select * from type where name = 'email-part';
select * from thing where type_id = 23;
delete from link_attribute where thing_id in (select id from thing where type_id = 23);
delete from string_attribute where thing_id in (select id from thing where type_id = 23);
delete from file_attribute where thing_id in (select id from thing where type_id = 23);
delete from thing where type_id = 23;
select * from type where name = 'email-message';
select * from thing where type_id = 22;
delete from link_attribute where thing_id in (select id from thing where type_id = 22);
delete from string_attribute where thing_id in (select id from thing where type_id = 22);
delete from file_attribute where thing_id in (select id from thing where type_id = 22);
delete from timestamp_attribute where thing_id in (select id from thing where type_id = 22);
delete from thing where type_id = 22;
select * from type where name = 'email-address';
select * from thing where type_id = 24;
delete from link_attribute where thing_id in (select id from thing where type_id = 24);
delete from string_attribute where thing_id in (select id from thing where type_id = 24);
delete from file_attribute where thing_id in (select id from thing where type_id = 24);
delete from timestamp_attribute where thing_id in (select id from thing where type_id = 24);
delete from thing where type_id = 24;
