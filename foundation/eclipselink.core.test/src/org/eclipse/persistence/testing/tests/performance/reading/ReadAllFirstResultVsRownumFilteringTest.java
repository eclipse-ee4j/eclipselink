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
package org.eclipse.persistence.testing.tests.performance.reading;

import org.eclipse.persistence.platform.database.OraclePlatform;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.testing.framework.PerformanceComparisonTestCase;
import org.eclipse.persistence.testing.models.performance.toplink.Employee;

/**
 * Compares ReadAllQuery returning all rows to a Query using Oracle's Rownum support
 * to filter out all but the last row using FirstResults=size-1.
 */
public class ReadAllFirstResultVsRownumFilteringTest extends PerformanceComparisonTestCase {
    public ReadAllFirstResultVsRownumFilteringTest() {
        setDescription("This test compares the performance of Oracle pagination vs non pagination.");
        addReadAllRownumFilteringTest();
        addReadAllFirstResultTest();
    }
    int size;
    public void setup() throws Throwable {
        super.setup();
        size = (getSession().readAllObjects(Employee.class)).size();
    }
    
    /**
     * Read all employees with cursored stream.
     */
    public void test() throws Exception {
        ReadAllQuery query = new ReadAllQuery(Employee.class);
        getSession().executeQuery(query);
    }

    /**
     * Read all employees with scrollable cursor.
     */
    public void addReadAllRownumFilteringTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void test() {
                ReadAllQuery query = new ReadAllQuery(Employee.class);
                query.setFirstResult(size-1);
                getSession().executeQuery(query);
            }
            
                        
        };
        test.setName("ReadAllRownumFilteringTest");
        test.setAllowableDecrease(350);
        addTest(test);
    }
    
    /**
     * Read all employees, base test for comparison.
     */
    public void addReadAllFirstResultTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void test() {
                ReadAllQuery query = new ReadAllQuery(Employee.class);
                query.setFirstResult(size-1);
                getSession().executeQuery(query);
            }
            public void startTest() {
                ((OraclePlatform)getSession().getPlatform()).setShouldUseRownumFiltering(false);
            }

            /**
             * Allows any test specific setup before starting the test run.
             */
            public void endTest() {
                ((OraclePlatform)getSession().getPlatform()).setShouldUseRownumFiltering(true);
            }
                        
        };
        test.setName("FirstResultTest");
        addTest(test);
    }
}
