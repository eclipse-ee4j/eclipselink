/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.performance.jdbc;

import java.util.*;
import java.sql.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.testing.framework.*;

/**
 * Compares the performance between accessing timestamps as objects vs as strings.
 */
public class TimestampTest extends PerformanceComparisonTestCase {
    protected Connection connection;
    protected Number id;
    protected String sql;

    public TimestampTest() {
        setName("getTimestamp vs getString PerformanceComparisonTest");
        setDescription("Compares the performance between accessing timestamps as objects vs as strings.");
        addGetString();
        addGetDATE();
    }

    public void setup() throws Exception {
        connection = (Connection)((AbstractSession)getSession()).getAccessor().getDatasourceConnection();
        sql = "SELECT START_DATE FROM EMPLOYEE";
    }

    /**
     * getTimestamp and convert to Calendar.
     */
    public void test() throws Exception {
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet result = statement.executeQuery();
        int size = result.getMetaData().getColumnCount();
        Vector rows = new Vector();
        while (result.next()) {
            Vector row = new Vector(size);
            for (int column = 1; column <= size; column++) {
                row.add(result.getObject(column));
            }
            rows.add(row);
        }
        result.close();
        statement.close();
    }

    /**
     * getString and convert to Calendar.
     */
    public void addGetString() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void test() throws Exception {
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet result = statement.executeQuery();
                int size = result.getMetaData().getColumnCount();
                Vector rows = new Vector();
                while (result.next()) {
                    Vector row = new Vector(size);
                    for (int column = 1; column <= size; column++) {
                        row.add(result.getString(column));
                    }
                    rows.add(row);
                }
                result.close();
                statement.close();
            }
        };
        test.setAllowableDecrease(-30);
        test.setName("GetStringTest");
        addTest(test);
    }

    /**
     * getDATE and convert to Calendar.
     */
    public void addGetDATE() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void test() throws Exception {
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet result = statement.executeQuery();
                int size = result.getMetaData().getColumnCount();
                Vector rows = new Vector();
                while (result.next()) {
                    Vector row = new Vector(size);
                    for (int column = 1; column <= size; column++) {
                        row.add(result.getDate(column));
                    }
                    rows.add(row);
                }
                result.close();
                statement.close();
            }
        };
        test.setName("GetDATETest");
        addTest(test);
    }
}
