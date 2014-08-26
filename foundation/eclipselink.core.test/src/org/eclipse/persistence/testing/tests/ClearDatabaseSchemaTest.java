/*******************************************************************************
 * Copyright (c) 1998, 2014 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     21/08/2013-2.6 Chris Delahunt
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.ArrayRecord;
import org.eclipse.persistence.testing.framework.TestCase;

/**
 * This test is used to allow clearing the schema before running automated tests.  
 * It is not intended to test behavior, but might be included in test runs where problems can be recorded.
 *
 */
public class ClearDatabaseSchemaTest extends TestCase {
    
    public ClearDatabaseSchemaTest() {
        setDescription("Clears the database for MYSQL.");
    }
    
    public void test() {
        //TODO: add missing platforms, currently supported are MySQL, Oracle DB, Derby
        Platform platform = getSession().getDatasourcePlatform();
        if (platform.isMySQL()) {
            ArrayRecord record = (ArrayRecord)getSession().executeSQL("select DATABASE()").get(0);
            getSession().executeNonSelectingSQL("drop database "+record.get("DATABASE()"));
            getSession().executeNonSelectingSQL("create database "+record.get("DATABASE()"));
        } else if (platform.isOracle()) {
            getSession().executeNonSelectingSQL("BEGIN FOR cur_rec IN (\n"
                    + "SELECT object_name, object_type FROM user_objects WHERE object_type IN "
                    + "('TABLE', 'VIEW', 'PACKAGE', 'PROCEDURE', 'FUNCTION', 'SEQUENCE'))\n"
                    + "LOOP BEGIN IF cur_rec.object_type = 'TABLE' "
                    + "THEN EXECUTE IMMEDIATE 'DROP ' || cur_rec.object_type || ' \"' || cur_rec.object_name || '\" CASCADE CONSTRAINTS'; "
                    + "ELSE EXECUTE IMMEDIATE 'DROP ' || cur_rec.object_type || ' \"' || cur_rec.object_name || '\"'; "
                    + "END IF;\nEXCEPTION WHEN OTHERS "
                    + "THEN DBMS_OUTPUT.put_line ('FAILED: DROP ' || cur_rec.object_type || ' \"' || cur_rec.object_name || '\"');"
                    + "END;\nEND LOOP; END;");
        } else if (platform.isDerby()) {
            Connection connection = ((AbstractSession) getSession()).getAccessor().getConnection();
            DatabaseMetaData metaData;
            String url = null;
            try {
                metaData = connection.getMetaData();
                url = metaData.getURL();
            } catch (SQLException e) {
                fail(e.getMessage());
            }
            assertNotNull("Connection URL cannot be null", url);
            int idx = url.lastIndexOf('/') + 1;
            String schemaToDrop = url.substring(idx);
            if ((idx = schemaToDrop.indexOf(';')) > -1) {
                schemaToDrop = schemaToDrop.substring(0, idx);
            }
            Vector<ArrayRecord> result = getSession().executeSQL("SELECT 'ALTER TABLE '||S.SCHEMANAME||'.'||T.TABLENAME||' DROP CONSTRAINT '||C.CONSTRAINTNAME\n"
                    + "FROM SYS.SYSCONSTRAINTS C, SYS.SYSSCHEMAS S, SYS.SYSTABLES T\n"
                    + "WHERE C.SCHEMAID = S.SCHEMAID AND C.TABLEID = T.TABLEID AND S.SCHEMANAME = '" + schemaToDrop.toUpperCase() + "' ORDER BY C.REFERENCECOUNT DESC");
            List<String> toRetry = new ArrayList<String>();
            for (ArrayRecord ar : result) {
                for (Object o : ar.values()) {
                    String stmt = (String) o;
                    try {
                        getSession().executeNonSelectingSQL(stmt);
                    } catch (DatabaseException t) {
                        toRetry.add(stmt);
                    }
                }
            }
            final int MAX_ROUNDS = 3;
            int round = 0;
            while (!toRetry.isEmpty() && round < MAX_ROUNDS ) {
                for (Iterator<String> i = toRetry.iterator(); i.hasNext();) {
                    String stmt = i.next();
                    try {
                        getSession().executeNonSelectingSQL(stmt);
                        i.remove();
                    } catch (DatabaseException de) {
                        //ignore, next round may be successful
                    }
                }
                round++;
            }
            result = getSession().executeSQL("SELECT 'DROP TABLE ' || schemaname ||'.' || tablename FROM SYS.SYSTABLES\n"
                    + "INNER JOIN SYS.SYSSCHEMAS ON SYS.SYSTABLES.SCHEMAID = SYS.SYSSCHEMAS.SCHEMAID\n"
                    + "WHERE schemaname='" + schemaToDrop.toUpperCase() + "'");
            for (ArrayRecord ar : result) {
                for (Object o : ar.values()) {
                    String stmt = (String) o;
                    try {
                        getSession().executeNonSelectingSQL(stmt);
                    } catch (DatabaseException ee) {
                        toRetry.add(stmt);
                    }
                }
            }
            assertTrue(toRetry + " statements failed", toRetry.isEmpty());
        } else {
            fail("Clear DB test run on unsupported DB");
        }
        getDatabaseSession().logout();
        getDatabaseSession().login();
    }
}
