/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.performance.jdbc;

import java.util.*;
import java.sql.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.testing.framework.*;

/**
 * This test compares the performance between using getObject and getDate for Timestamps.
 */
public class TimestampDateTest extends PerformanceComparisonTestCase {
    protected Connection connection;
    protected Number id;
    protected String sql;

    public TimestampDateTest() {
        setName("getObject vs getDate DateConversionPerformanceComparisonTest");
        setDescription("Compares the performance between accessing timestamps as objects and converting them or directly as dates them.");
        addGetDate();
    }

    public void setup() throws Exception {
        connection = (Connection)((AbstractSession)getSession()).getAccessor().getDatasourceConnection();
        sql = "SELECT START_DATE FROM EMPLOYEE";
    }

    /**
     * getObject.
     */
    public void test() throws Exception {
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet result = statement.executeQuery();
        int size = result.getMetaData().getColumnCount();
        Vector rows = new Vector();
        while (result.next()) {
            Vector row = new Vector(size);
            for (int column = 1; column <= size; column++) {
                Object value = result.getObject(column);
                value = ConversionManager.getDefaultManager().convertObject(value, ClassConstants.SQLDATE);
                row.add(value);
            }
            rows.add(row);
        }
        result.close();
        statement.close();
    }

    /**
     * getDate.
     */
    public void addGetDate() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void test() throws Exception {
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet result = statement.executeQuery();
                int size = result.getMetaData().getColumnCount();
                Vector rows = new Vector();
                while (result.next()) {
                    Vector row = new Vector(size);
                    for (int column = 1; column <= size; column++) {
                        Object value = result.getDate(column);
                        value = ConversionManager.getDefaultManager().convertObject(value, ClassConstants.SQLDATE);
                        row.add(value);
                    }
                    rows.add(row);
                }
                result.close();
                statement.close();
            }
        };
        test.setAllowableDecrease(5);
        test.setName("GetDateTest");
        addTest(test);
    }
}
