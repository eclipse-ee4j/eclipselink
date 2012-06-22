/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
import org.eclipse.persistence.testing.framework.*;

/**
 * This test compares the performance between using multiple queries vs single outer join.
 */
public class InheritanceOuterJoinTest extends PerformanceComparisonTestCase {
    protected Connection connection;
    protected PreparedStatement selectTypeStatement;
    protected PreparedStatement selectSmallProjectStatement;
    protected PreparedStatement selectLargeProjectStatement;
    protected PreparedStatement selectAllProjectStatement;

    public InheritanceOuterJoinTest() {
        setDescription("Compares the performance of multiple queries vs single outer join.");
        addOuterJoinTest();
        //addOuterJoinFromClauseTest();
    }

    public void setup() throws Exception {
        connection = (Connection)getAbstractSession().getAccessor().getDatasourceConnection();
        selectTypeStatement = connection.prepareStatement("Select PROJ_TYPE from PROJECT");
        selectSmallProjectStatement = connection.prepareStatement("Select * from PROJECT where PROJ_TYPE = 'S'");
        selectLargeProjectStatement = connection.prepareStatement("Select * from PROJECT P, LPROJECT L where ((PROJ_TYPE = 'L') and (L.PROJ_ID = P.PROJ_ID))");
        selectAllProjectStatement = connection.prepareStatement("Select * from PROJECT P, LPROJECT L where L.PROJ_ID (+) = P.PROJ_ID");
    }
    
    public void reset() throws Exception {
        selectTypeStatement.close();
        selectSmallProjectStatement.close();
        selectLargeProjectStatement.close();
        selectAllProjectStatement.close();
    }
    
    /**
     * Fetch rows.
     */
    public void fetchRows(ResultSet result) throws Exception {
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
    }
    
    /**
     * Multiple queries.
     */
    public void test() throws Exception {
        fetchRows(selectTypeStatement.executeQuery());
        fetchRows(selectSmallProjectStatement.executeQuery());
        fetchRows(selectLargeProjectStatement.executeQuery());
    }

    /**
     * Outer join.
     */
    public void addOuterJoinTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void test() throws Exception {
                fetchRows(selectAllProjectStatement.executeQuery());
            }
        };
        test.setName("OuterJoinTest");
        test.setAllowableDecrease(10);
        addTest(test);
    }
    
    /**
     * Outer join from clause.
     *
    public void addOuterJoinFromClauseTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void test() throws Exception {
                PreparedStatement statement = connection.prepareStatement("Select * from PROJECT P left outer join LPROJECT L on (L.PROJ_ID = P.PROJ_ID)");
                fetchRows(statement.executeQuery());
                statement.close();
            }
        };
        test.setName("OuterJoinFromClauseTest");
        test.setAllowableDecrease(10);
        addTest(test);
    }*/
}
