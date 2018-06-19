--
-- Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
--
-- This program and the accompanying materials are made available under the
-- terms of the Eclipse Public License 2.0 which is available at
-- http://www.eclipse.org/legal/epl-2.0.
--
-- SPDX-License-Identifier: EPL-2.0
--

# This script creates EclipseLink performance tests database.
# MySQL version.

Create table LOADBUILD (
        id int not null auto_increment,
        lbtimestamp date,
        lberrors int,
        fatalErrors int,
        loginChoice varchar(100),
        os varchar(100),
        toplink_version varchar(100),
        jvm varchar(100),
        machine varchar(100),
        numberOfTests int,
        lbuserName varchar(50),
        primary key (id));

Create table RESULT (
        id int not null auto_increment,
        description varchar(2000),
        exception varchar(2000),
        name varchar(1000),
        outcome varchar(100),
        test_time int,
        total_time int,
        summaryId int,
        lbuildId int,
        primary key (id));

Create table SUMMARY (
        id int not null auto_increment,
        description varchar(2000),
        setup_failures int,
        errors int,
        fatalErrors int,
        name varchar(1000),
        passed int,
        problems int,
        setupException varchar(2000),
        total_time int,
        totalTests int,
        warnings int,
        lbuildId int,
        parentId int,
        primary key (id));

ALTER TABLE RESULT ADD CONSTRAINT FK_RESULT_summaryId FOREIGN KEY (summaryId) REFERENCES SUMMARY (id);
ALTER TABLE RESULT ADD CONSTRAINT FK_RESULT_lbuildId FOREIGN KEY (lbuildId) REFERENCES LOADBUILD (id);
ALTER TABLE SUMMARY ADD CONSTRAINT FK_LOADBUILD_lbuildId FOREIGN KEY (lbuildId) REFERENCES LOADBUILD (id);
ALTER TABLE SUMMARY ADD CONSTRAINT FK_LOADBUILD_parentId FOREIGN KEY (parentId) REFERENCES SUMMARY (id);

CREATE INDEX IX_LOADBUILD_lbtimestamp ON LOADBUILD (lbtimestamp);
CREATE INDEX IX_LOADBUILD_loginChoice ON LOADBUILD (loginChoice);
CREATE INDEX IX_LOADBUILD_toplink_version ON LOADBUILD (toplink_version);
CREATE INDEX IX_LOADBUILD_machine ON LOADBUILD (machine);
CREATE INDEX IX_LOADBUILD_lbuserName ON LOADBUILD (lbuserName);
CREATE INDEX IX_RESULT_name ON RESULT (name);
CREATE INDEX IX_RESULT_summaryId ON RESULT (summaryId);
CREATE INDEX IX_RESULT_lbuildId ON RESULT (lbuildId);
CREATE INDEX IX_SUMMARY_lbuildId on SUMMARY (lbuildId);
CREATE INDEX IX_SUMMARY_name on SUMMARY (name);
CREATE INDEX IX_SUMMARY_parentId on SUMMARY (parentId);

