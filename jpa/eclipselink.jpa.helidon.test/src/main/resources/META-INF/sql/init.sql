--
-- Copyright (c) 2024 Oracle and/or its affiliates. All rights reserved.
--
-- This program and the accompanying materials are made available under the
-- terms of the Eclipse Public License v. 2.0 which is available at
-- http://www.eclipse.org/legal/epl-2.0,
-- or the Eclipse Distribution License v. 1.0 which is available at
-- http://www.eclipse.org/org/documents/edl-v10.php.
--
-- SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
--
-- Contributors:
--     Oracle - initial API and implementation

DROP TABLE HELIDON_TAB_DETAIL
DROP TABLE HELIDON_TAB_MASTER
CREATE TABLE HELIDON_TAB_MASTER (ID INTEGER PRIMARY KEY, NAME VARCHAR(200))
CREATE TABLE HELIDON_TAB_DETAIL (ID INTEGER PRIMARY KEY, MASTER_ID_FK INTEGER, NAME VARCHAR(200))
ALTER TABLE HELIDON_TAB_DETAIL ADD CONSTRAINT TEST_TAB_FK_DETAIL FOREIGN KEY ( MASTER_ID_FK ) REFERENCES HELIDON_TAB_MASTER (ID)

INSERT INTO HELIDON_TAB_MASTER (ID, NAME)VALUES (1, 'Master 1')
INSERT INTO HELIDON_TAB_MASTER (ID, NAME)VALUES (2, 'Master 2')
INSERT INTO HELIDON_TAB_DETAIL (ID, MASTER_ID_FK, NAME)VALUES (101, 1, 'Detail 101')
INSERT INTO HELIDON_TAB_DETAIL (ID, MASTER_ID_FK, NAME)VALUES (102, 1, 'Detail 102')
INSERT INTO HELIDON_TAB_DETAIL (ID, MASTER_ID_FK, NAME)VALUES (201, 2, 'Detail 201')
INSERT INTO HELIDON_TAB_DETAIL (ID, MASTER_ID_FK, NAME)VALUES (202, 2, 'Detail 202')
