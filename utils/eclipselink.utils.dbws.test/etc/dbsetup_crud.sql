CREATE TABLE IF NOT EXISTS crud_table (
  ID NUMERIC NOT NULL,
  NAME VARCHAR(80),
  PRIMARY KEY (ID) 
)|
insert into crud_table values (1, 'crud1')|
insert into crud_table values (2, 'crud2')|
insert into crud_table values (3, 'other')|