DROP TABLE SIMPLETABLE|
CREATE TABLE SIMPLETABLE (
  id NUMBER NOT NULL,
  name VARCHAR2(25),
  since DATE,
  PRIMARY KEY (id)
)|
INSERT INTO SIMPLETABLE (id, name, since) VALUES (1, 'mike', to_date('2001-12-25','YYYY-MM-DD'))|
INSERT INTO SIMPLETABLE (id, name, since) VALUES (2, 'blaise',to_date('2001-12-25','YYYY-MM-DD'))|
INSERT INTO SIMPLETABLE (id, name, since) VALUES (3, 'rick',to_date('2001-12-25','YYYY-MM-DD'))|