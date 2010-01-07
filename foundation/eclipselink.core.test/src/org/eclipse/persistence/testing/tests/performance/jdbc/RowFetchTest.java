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
 * This test compares the performance between using a set rowfetch or the default.
 */
public class RowFetchTest extends PerformanceComparisonTestCase {
    protected Connection connection;
    protected Number id;
    protected String sql;

    public RowFetchTest() {
        setName("Default row fetch vs set row fetch (2, 1 object) PerformanceComparisonTest");
        setDescription("Compares the performance between using default and set row fetch, query 1 row, row fetch set to 2.");
        addSetRowFetch();
    }

    public void setup() throws Exception {
        connection = (Connection)((AbstractSession)getSession()).getAccessor().getDatasourceConnection();

        PreparedStatement statement = connection.prepareStatement("SELECT EMP_ID FROM EMPLOYEE");
        ResultSet result = statement.executeQuery();
        int size = result.getMetaData().getColumnCount();
        result.next();
        id = (Number)result.getObject(1);
        result.close();
        statement.close();

        sql = "SELECT * FROM EMPLOYEE where emp_id = " + id;
    }

    /**
     * Default row fetch (10).
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
     * Static.
     */
    public void addSetRowFetch() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void test() throws Exception {
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setFetchSize(2);
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
        };
        test.setName("SetRowFetchTest");
        addTest(test);
    }
}
