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
public class LargeRowFetchTest extends PerformanceComparisonTestCase {
    protected Connection connection;
    protected String sql;

    public LargeRowFetchTest() {
        setName("Default row fetch vs set row fetch (50, 100 objects) PerformanceComparisonTest");
        setDescription("Compares the performance between using default and set row fetch, query 100 rows, row fetch set to 50.");
        addSetRowFetch();
    }

    public void setup() throws Exception {
        connection = (Connection)((AbstractSession)getSession()).getAccessor().getDatasourceConnection();

        sql = "SELECT * FROM EMPLOYEE";
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
     * 50 row fetch.
     */
    public void addSetRowFetch() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void test() throws Exception {
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setFetchSize(50);
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
        test.setAllowableDecrease(10);
        addTest(test);
    }
}
