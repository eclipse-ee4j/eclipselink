DROP TABLE XR_SIMPLETABLE|
CREATE TABLE XR_SIMPLETABLE (
  id NUMBER NOT NULL,
  name VARCHAR(25),	
  since DATE, 
  PRIMARY KEY (id)
)|
INSERT INTO XR_SIMPLETABLE (id, name, since) VALUES (1, 'mike', to_date('2001-12-25','YYYY-MM-DD'))|
INSERT INTO XR_SIMPLETABLE (id, name, since) VALUES (2, 'merrick',to_date('2001-12-25','YYYY-MM-DD'))|
INSERT INTO XR_SIMPLETABLE (id, name, since) VALUES (3, 'rick',to_date('2001-12-25','YYYY-MM-DD'))|
