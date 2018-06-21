--
-- Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
--
-- This program and the accompanying materials are made available under the
-- terms of the Eclipse Public License v. 2.0 which is available at
-- http://www.eclipse.org/legal/epl-2.0,
-- or the Eclipse Distribution License v. 1.0 which is available at
-- http://www.eclipse.org/org/documents/edl-v10.php.
--
-- SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
--

DROP TABLE ORAREC_EMP|
CREATE TABLE ORAREC_EMP ( 
  EMPNO NUMBER(4) NOT NULL, 
  ENAME VARCHAR2(10), 
  JOB VARCHAR2(9), 
  MGR NUMBER(4), 
  HIREDATE DATE, 
  SAL NUMBER(7,2), 
  COMM NUMBER(7,2), 
  DEPTNO NUMBER(2), 
  CONSTRAINT ORAREC_PK_EMP PRIMARY KEY (EMPNO)
)|
CREATE OR REPLACE PROCEDURE REC_TEST(Z IN ORAREC_EMP%ROWTYPE) AS
BEGIN
  NULL;
END;
|
CREATE OR REPLACE PROCEDURE REC_TEST_OUT(Z OUT ORAREC_EMP%ROWTYPE) AS
BEGIN
  Z.EMPNO := 1234;
  Z.ENAME := 'GOOFY';
  Z.JOB := 'ACTOR';
  Z.SAL := 6000;
  Z.DEPTNO := 20;
END;
|
CREATE OR REPLACE PROCEDURE REC_TEST_INOUT(Z IN OUT ORAREC_EMP%ROWTYPE) AS
BEGIN
 Z.EMPNO := 1234;
 Z.ENAME := 'GOOFY';
 Z.JOB := 'ACTOR';
 Z.SAL := Z.SAL + 500;
 Z.DEPTNO := 20;
END;
|
CREATE OR REPLACE TYPE ORAREC_EMP_TYPE AS OBJECT (
  EMPNO NUMBER(4,0),
  ENAME VARCHAR2(10),
  JOB VARCHAR2(9),
  MGR NUMBER(4,0),
  HIREDATE DATE,
  SAL NUMBER(7,2),
  COMM NUMBER(7,2),
  DEPTNO NUMBER(2,0)
)
|