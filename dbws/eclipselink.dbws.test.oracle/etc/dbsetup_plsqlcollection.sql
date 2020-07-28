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

CREATE OR REPLACE PACKAGE SOMEPACKAGE AS
  TYPE TBL1 IS TABLE OF VARCHAR2(111) INDEX BY BINARY_INTEGER;
  PROCEDURE P1(SIMPLARRAY IN TBL1, FOO IN VARCHAR2);
END SOMEPACKAGE;
|
CREATE OR REPLACE PACKAGE BODY SOMEPACKAGE AS
  PROCEDURE P1(SIMPLARRAY IN TBL1, FOO IN VARCHAR2) AS
  BEGIN
    NULL;
  END P1;
END SOMEPACKAGE;
|
CREATE OR REPLACE TYPE SOMEPACKAGE_TBL1 AS TABLE OF VARCHAR2(111)|