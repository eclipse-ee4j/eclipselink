CREATE TABLE IF NOT EXISTS inlinebinary (
  ID DECIMAL(7,0) NOT NULL, 
  NAME VARCHAR(80),
  B BLOB,
  PRIMARY KEY (ID) 
)|
insert into inlinebinary(ID, NAME, B) values (1, 'one', 0x010101010101010101010101010101)|
insert into inlinebinary(ID, NAME, B) values (2, 'two', 0x020202020202020202020202020202)|
insert into inlinebinary(ID, NAME, B) values (3, 'three', 0x030303030303030303030303030303)|