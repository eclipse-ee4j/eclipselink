/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     21/08/2013-2.6 Chris Delahunt
package org.eclipse.persistence.testing.tests;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.ArrayRecord;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.testing.framework.TestCase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

/**
 * This test is used to allow clearing the schema before running automated tests.
 * It is not intended to test behavior, but might be included in test runs where problems can be recorded.
 *
 */
public class ClearDatabaseSchemaTest extends TestCase {

    public ClearDatabaseSchemaTest() {
        setDescription("Clears the database for MYSQL, Oracle DB, Derby, MSSQL, HSQL, Postgres.");
    }

    @Override
    public void test() {
        //TODO: add missing platforms, currently supported are:
        //MySQL, MariaDB, Oracle DB, Derby, HSQLDB, PostgreSQL, MSSQL
        AbstractSession session = (AbstractSession) getSession();
        Platform platform = session.getDatasourcePlatform();
        if (platform.isMySQL() || platform.isMariaDB()) {
            resetMySQL(session);
        } else if (platform.isOracle()) {
            resetOracle(session);
        } else if (platform.isDerby()) {
            resetDerby(session);
        } else if (platform.isSQLServer()) {
            resetMSSQL(session);
        } else if (platform.isH2()) {
            resetH2(session);
        } else if (platform.isHSQL()) {
            resetHsql(session);
        } else if (platform.isPostgreSQL()) {
            resetPostgres(session);
        } else if (platform.isDB2()) {
            resetDB2(session);
        } else {
            fail("Clear DB test run on unsupported DB");
        }
        getDatabaseSession().logout();
        getDatabaseSession().login();
    }

    private void resetMySQL(AbstractSession session) {
        ArrayRecord record = null;
        try {
            record = (ArrayRecord) session.executeSQL("select DATABASE()").get(0);
            session.executeNonSelectingSQL("drop database " + record.get("DATABASE()"));
        } catch (DatabaseException x) {
            AbstractSessionLog.getLog().warning("Failed to drop database");
            // Using System.err since session log may not print out the stack trace
            x.printStackTrace(System.err);
        } finally {
            if (record != null) {
                session.executeNonSelectingSQL("create database " + record.get("DATABASE()"));
            } else {
                DatabaseLogin databaseLogin = (DatabaseLogin) session.getDatasourceLogin();
                String url = databaseLogin.getDatabaseURL();

                Properties properties = new Properties();
                properties.put("user", databaseLogin.getUserName());
                properties.put("password", databaseLogin.getPassword());

                int databaseNameSeparatorIndex = url.lastIndexOf('/');
                String databaseName = url.substring(databaseNameSeparatorIndex + 1);
                int propertiesIndex = databaseName.indexOf('?');
                if (propertiesIndex > 0) {
                    for (String propertyString : databaseName.substring(propertiesIndex + 1).split("&")) {
                        String[] propertyDetails = propertyString.split("=");
                        properties.put(propertyDetails[0].trim(), propertyDetails[1].trim());
                    }
                    databaseName = databaseName.substring(0, propertiesIndex);
                }
                url = url.substring(0, databaseNameSeparatorIndex);

                try (Connection connection = DriverManager.getConnection(url, properties)) {
                    connection.prepareStatement("create database " + databaseName).execute();
                } catch (SQLException e) {
                    // Using System.err since session log may not print out the stack trace
                    e.printStackTrace(System.err);
                }
            }
        }
        //unused for now but kept here for alternate option
//        Vector<ArrayRecord> result = session.executeSQL("SELECT concat('ALTER TABLE ', C.TABLE_SCHEMA, '.', C.TABLE_NAME, ' DROP FOREIGN KEY ', C.CONSTRAINT_NAME) "
//                + "FROM information_schema.TABLE_CONSTRAINTS C "
//                + "WHERE C.TABLE_SCHEMA = (SELECT SCHEMA()) and C.CONSTRAINT_TYPE = 'FOREIGN KEY'");
//        List<String> toRetry = execStatements(session, result);
//        result = session.executeSQL("SELECT concat('DROP TABLE IF EXISTS ', T.TABLE_SCHEMA, '.', T.TABLE_NAME) "
//                + "FROM information_schema.TABLES T WHERE T.TABLE_SCHEMA = (SELECT SCHEMA())");
//        toRetry.addAll(execStatements(session, result));
//        assertTrue(toRetry + " statements failed", toRetry.isEmpty());
    }

    private void resetOracle(AbstractSession session) {
        session.executeNonSelectingSQL("""
                BEGIN FOR cur_rec IN (
                SELECT object_name, object_type FROM user_objects WHERE object_type IN ('TABLE', 'VIEW', 'PACKAGE', 'PROCEDURE', 'FUNCTION', 'SEQUENCE', 'TYPE'))
                LOOP BEGIN IF cur_rec.object_type = 'TABLE' THEN EXECUTE IMMEDIATE 'DROP ' || cur_rec.object_type || ' "' || cur_rec.object_name || '" CASCADE CONSTRAINTS'; ELSIF cur_rec.object_type = 'TYPE' THEN EXECUTE IMMEDIATE 'DROP ' || cur_rec.object_type || ' "' || cur_rec.object_name || '" FORCE'; ELSE EXECUTE IMMEDIATE 'DROP ' || cur_rec.object_type || ' "' || cur_rec.object_name || '"'; END IF;
                EXCEPTION WHEN OTHERS THEN DBMS_OUTPUT.put_line ('FAILED: DROP ' || cur_rec.object_type || ' "' || cur_rec.object_name || '"');END;
                END LOOP; END;""");
        session.executeNonSelectingSQL("PURGE user_recyclebin");
        session.executeNonSelectingSQL("PURGE recyclebin");
    }

    @SuppressWarnings("unchecked")
    private void resetDB2(AbstractSession session) {
        // 1) Determine target schema (fallback to USER), TRIM padding and quote
        Vector<ArrayRecord> rows = session.executeSQL("VALUES COALESCE(CURRENT SCHEMA, USER)");
        String schema = ((String) rows.get(0).getValues().get(0)).trim().replace("\"", "\"\"");

        // Safety guard: never touch system schemas
        switch (schema) {
            case "SYSIBM": case "SYSIBMADM": case "SYSCAT": case "SYSTOOLS":
            case "SYSSTAT": case "SYSFUN": case "SYSPROC": case "SYSIBMTS":
            case "NULLID":
                throw new IllegalStateException("Refusing to drop system schema: " + schema);
        }

        // Remove error table up front, so SYSPROC.ADMIN_DROP_SCHEMA can create it
        // without issues
        try {
           session.executeNonSelectingSQL("DROP TABLE SYSTOOLS.DROP_ERRORS");
        } catch (Exception ignore) {
           // no-op
        }

        // 2) Drop the schema and everything in it.
        session.executeNonSelectingSQL("""
            BEGIN ATOMIC
              DECLARE v_schema     VARCHAR(128);
              DECLARE v_errschema  VARCHAR(128);
              DECLARE v_errtable   VARCHAR(128);

              -- Recompute from session to avoid literal issues; Db2 will use the variable values
              SET v_schema = COALESCE(CURRENT_SCHEMA, USER);
              SET v_errschema = 'SYSTOOLS';
              SET v_errtable  = 'DROP_ERRORS';

              CALL SYSPROC.ADMIN_DROP_SCHEMA(v_schema, NULL, v_errschema, v_errtable);
            END
        """);

        // 3) Re-create the schema (quote & escape)
        session.executeNonSelectingSQL("CREATE SCHEMA \"" + schema + "\"");

        // 4) Re-assert CURRENT SCHEMA
        session.executeNonSelectingSQL("SET CURRENT SCHEMA \"" + schema + "\"");

        // 5) Optional: clean up the error table (ignore if it’s locked or missing)
        try {
           session.executeNonSelectingSQL("DROP TABLE SYSTOOLS.DROP_ERRORS");
        } catch (Exception ignore) {
           // no-op
        }
    }

    @SuppressWarnings({"unchecked"})
    private void resetDerby(AbstractSession session) {
        Vector<ArrayRecord> result = session.executeSQL("""
                SELECT 'ALTER TABLE '||S.SCHEMANAME||'.'||T.TABLENAME||' DROP CONSTRAINT "'||C.CONSTRAINTNAME||'"'
                FROM SYS.SYSCONSTRAINTS C, SYS.SYSSCHEMAS S, SYS.SYSTABLES T
                WHERE C.SCHEMAID = S.SCHEMAID AND C.TABLEID = T.TABLEID AND S.SCHEMANAME = CURRENT SCHEMA ORDER BY C.REFERENCECOUNT DESC""");
        List<String> toRetry = execStatements(session, result);
        final int MAX_ROUNDS = 3;
        int round = 0;
        while (!toRetry.isEmpty() && round < MAX_ROUNDS ) {
            for (Iterator<String> i = toRetry.iterator(); i.hasNext();) {
                String stmt = i.next();
                try {
                    session.executeNonSelectingSQL(stmt);
                    i.remove();
                } catch (DatabaseException de) {
                    //ignore, next round may be successful
                }
            }
            round++;
        }
        result = session.executeSQL("""
                SELECT 'DROP TABLE ' || schemaname ||'.' || tablename FROM SYS.SYSTABLES
                INNER JOIN SYS.SYSSCHEMAS ON SYS.SYSTABLES.SCHEMAID = SYS.SYSSCHEMAS.SCHEMAID
                WHERE schemaname = CURRENT SCHEMA""");
        toRetry.addAll(execStatements(session, result));
        assertTrue(toRetry + " statements failed", toRetry.isEmpty());
    }

    private void resetMSSQL(AbstractSession session) {
        session.executeNonSelectingSQL(
                """
                        DECLARE @name VARCHAR(256)
                        DECLARE @subName VARCHAR(256)
                        DECLARE @statement VARCHAR(256)
                        WHILE((SELECT COUNT(1) FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS WHERE constraint_catalog=DB_NAME()) > 0)
                        BEGIN
                        \tSELECT TOP 1 @name=TABLE_NAME, @subName=CONSTRAINT_NAME FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS WHERE constraint_catalog=DB_NAME() ORDER BY CONSTRAINT_TYPE
                            SELECT @statement = 'ALTER TABLE [dbo].[' + RTRIM(@name) +'] DROP CONSTRAINT [' + RTRIM(@subName) +']'
                            EXEC (@statement)
                        END
                        WHILE((SELECT COUNT(1) FROM sysobjects WHERE [type] IN ('P', 'V', N'FN', N'IF', N'TF', N'FS', N'FT', 'U') AND category = 0) > 0)
                        BEGIN
                        \tSELECT TOP 1 @name=[name], @subName=[type] FROM sysobjects WHERE [type] IN ('P', 'V', N'FN', N'IF', N'TF', N'FS', N'FT', 'U') AND category = 0
                            SELECT @statement = 'DROP ' + CASE @subName WHEN 'P' THEN 'PROCEDURE' WHEN 'V' THEN 'VIEW' WHEN 'U' THEN 'TABLE' ELSE 'FUNCTION' END + ' [dbo].[' + RTRIM(@name) +']'
                            EXEC (@statement)
                        END""");
    }

    private void resetHsql(AbstractSession session) {
        @SuppressWarnings({"unchecked"})
        Vector<ArrayRecord> result = session.executeSQL("select 'DROP TABLE \"' || table_name || '\" CASCADE' FROM INFORMATION_SCHEMA.system_tables "
                + "WHERE table_type = 'TABLE' and table_schem = CURRENT_SCHEMA");
        List<String> toRetry = execStatements(session, result);
        assertTrue(toRetry + " statements failed", toRetry.isEmpty());
    }

    private void resetPostgres(AbstractSession session) {
        @SuppressWarnings({"unchecked"})
        Vector<ArrayRecord> result = getSession().executeSQL("SELECT 'DROP TABLE \"' || tablename || '\" CASCADE;' "
                + "FROM pg_tables WHERE schemaname = current_schema();");
        List<String> toRetry = execStatements(session, result);
        assertTrue(toRetry + " statements failed", toRetry.isEmpty());
    }

    private void resetH2(AbstractSession session) {
        session.executeNonSelectingSQL("DROP ALL OBJECTS;");
    }

    private List<String> execStatements(AbstractSession session, Vector<ArrayRecord> records) {
        List<String> failures = new ArrayList<>();
        for (ArrayRecord ar : records) {
            for (Object o : ar.values()) {
                String stmt = (String) o;
                try {
                    session.executeNonSelectingSQL(stmt);
                } catch (DatabaseException t) {
                    failures.add(stmt);
                }
            }
        }
        return failures;
    }
}
