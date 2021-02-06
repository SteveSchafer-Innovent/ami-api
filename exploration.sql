select count(*) from thing where words_updated = 1;
select * from word;
select * from word_thing;
select count(*) from word_thing;
select count(*) from word;
select w.id, w.word, count(*) from word w inner join word_thing wt on wt.word_id = w.id group by w.id order by w.word;
select * from string_attribute where thing_id = 14336;
select * from attribute_defn where id = 155;
select * from attribute_defn where name = 'text';
-- insert into attribute_defn (name, handler, type_id) values( 'html', 'rich-text', 23);
select * from attribute_defn where name = 'html'; -- 159
select * from attribute_defn where id = 159;
select * from string_attribute where attribute_defn_id = 155 and value like 'text/html%';
select * from string_attribute 
	where thing_id in (select thing_id from string_attribute where attribute_defn_id = 155 and value like 'text/html%') 
	and attribute_defn_id = 156;
-- create temporary table temp_things as select thing_id from string_attribute where attribute_defn_id = 155 and value like 'text/html%';
select * from temp_things;
-- update string_attribute set attribute_defn_id = 159
-- 	where thing_id in (select thing_id from temp_things) 
-- 	and attribute_defn_id = 156;
select * from string_attribute 
	where thing_id in (select thing_id from string_attribute where attribute_defn_id = 155 and value like 'text/html%') 
	and attribute_defn_id = 159;
select * from string_attribute where thing_id = 5741 and attribute_defn_id = 159;
select * from file_attribute where thing_id  in (select thing_id from string_attribute where attribute_defn_id = 155);
-- update thing set words_updated = 0 where id in (select thing_id from string_attribute where attribute_defn_id = 155);
select * from attribute_defn where name = 'html';
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
select * from timestamp_attribute;

-- insert into attribute_defn (name, handler, type_id) values ('filename', 'string', 2);

-- ALTER TABLE ami.attribute RENAME TO string_attribute;
-- alter table ami.link rename to link_attribute;

select * from type where name = 'email-folder';
select * from type where name = 'email-message';
select * from attribute_defn where type_id = (select id from type where name = 'email-folder');
select * from attribute_defn where type_id = (select id from type where name = 'email-message'); -- 143
select * from link_attribute where attribute_defn_id = 143;
select target_thing_id, count(*) from link_attribute where attribute_defn_id = 143 group by target_thing_id order by 2 desc;
select * from link_attribute where target_thing_id = 7192;

select * from thing where id = 139;
select * from type where id = 15;
select * from string_attribute where thing_id = 144;
select * from link_attribute where attribute_defn_id = 142;

select * from string_attribute where value like '%VNC viewer%';
select * from link_defn;
select * from type;
select * from thing where type_id=14;
select * from attribute_defn where id = 47;
select * from string_attribute where thing_id >= 26410;
select * from link_attribute;
-- delete from thing where id = 26413;

select * from string_attribute where thing_id = 15625;
update thing set words_updated = 0 where id = 15625;