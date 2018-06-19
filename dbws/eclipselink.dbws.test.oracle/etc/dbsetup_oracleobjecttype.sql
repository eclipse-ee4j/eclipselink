--
-- Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
--
-- This program and the accompanying materials are made available under the
-- terms of the Eclipse Public License 2.0 which is available at
-- http://www.eclipse.org/legal/epl-2.0.
--
-- SPDX-License-Identifier: EPL-2.0
--

CREATE TYPE XR_ADDRESS_TYPE AS OBJECT (
 STREET VARCHAR2(40),
 CITY VARCHAR2(40),
 PROV VARCHAR2(40)
)|
CREATE TABLE XR_EMP_ADDR ( 
  EMPNO NUMBER(4) NOT NULL, 
  FNAME VARCHAR2(40), 
  LNAME VARCHAR2(40), 
  ADDRESS XR_ADDRESS_TYPE, 
  PRIMARY KEY (EMPNO) 
)|
CREATE PROCEDURE GET_EMPLOYEES_BY_PROV(X IN XR_ADDRESS_TYPE, Y OUT SYS_REFCURSOR) AS
BEGIN
    OPEN Y FOR SELECT * FROM XR_EMP_ADDR xrea WHERE xrea.ADDRESS.PROV LIKE X.PROV;
END;
|
INSERT INTO XR_EMP_ADDR (EMPNO, FNAME, LNAME, ADDRESS) VALUES (1, 'Mike', 'Norman', XR_ADDRESS_TYPE('Pinetrail','Nepean','Ont'))|
INSERT INTO XR_EMP_ADDR (EMPNO, FNAME, LNAME, ADDRESS) VALUES (2, 'Rick', 'Barkhouse', XR_ADDRESS_TYPE('Davis Side Rd','Carleton Place','Ont'))|
INSERT INTO XR_EMP_ADDR (EMPNO, FNAME, LNAME, ADDRESS) VALUES (3, 'Merrick', 'Schincariol', XR_ADDRESS_TYPE('do','not','know'))|