CREATE TABLE IF NOT EXISTS attachedbinary (
  ID DECIMAL(7,0) NOT NULL, 
  NAME VARCHAR(80),
  B BLOB,
  PRIMARY KEY (ID) 
)|
insert into attachedbinary(ID, NAME, B) values (1, 'one', 0x010101010101010101010101010101)|
insert into attachedbinary(ID, NAME, B) values (2, 'two', 0x020202020202020202020202020202)|
insert into attachedbinary(ID, NAME, B) values (3, 'three', 0x030303030303030303030303030303)|
CREATE FUNCTION getBLOBById(pk numeric(7)) RETURNS BLOB
  READS SQL DATA
BEGIN
  DECLARE blb BLOB;
  SELECT B into blb FROM attachedbinary WHERE ID=PK;
  return(blb);
end|