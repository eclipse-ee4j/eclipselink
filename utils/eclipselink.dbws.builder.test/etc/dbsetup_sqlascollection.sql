CREATE TABLE IF NOT EXISTS sqlascollection (
  ID NUMERIC NOT NULL,
  NAME VARCHAR(25),	
  SINCE DATE, 
  PRIMARY KEY (ID) 
)|
INSERT INTO sqlascollection (ID, NAME, SINCE) VALUES (1, 'mike', '2001-12-25')|
INSERT INTO sqlascollection (ID, NAME, SINCE) VALUES (2, null,'2001-12-25')|
INSERT INTO sqlascollection (ID, NAME, SINCE) VALUES (3, 'rick',NULL)|