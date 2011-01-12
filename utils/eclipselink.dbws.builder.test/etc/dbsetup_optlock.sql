CREATE TABLE IF NOT EXISTS optlock (
  ID NUMERIC NOT NULL,
  NAME VARCHAR(25),
  DESCRIPT VARCHAR(20),
  VERSION NUMERIC,
  PRIMARY KEY (ID) 
)|
insert into optlock (ID, NAME, DESCRIPT, VERSION) values (1, 'name', 'this is ver 3', 3)|