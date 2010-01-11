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

import java.util.Vector;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.models.performance.toplink.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * This test compares the performance between a prepared and dynamic query.
 */
public class ReadObjectPreparedvsDynamicTest extends PerformanceComparisonTestCase {
    protected ReadObjectQuery query;
    protected ReadObjectQuery ejbQuery;

    public ReadObjectPreparedvsDynamicTest() {
        setDescription("Compares the performance between a prepared and dynamic query.");
        addTest(buildPreparedTest());
        addTest(buildPreparedEJBQLTest());
        addTest(buildDynamicEJBQLTest());
        addTest(buildDynamicNoParseCacheEJBQLTest());
        addTest(buildDynamicExpressionCachedExpressionTest());
    }

    /**
     * Find any employee.
     */
    public void setup() {
        query = new ReadObjectQuery(Employee.class);
        query.setSelectionCriteria(query.getExpressionBuilder().get("firstName").equal(query.getExpressionBuilder().getParameter("name")));
        query.addArgument("name");
        
        ejbQuery = new ReadObjectQuery(Employee.class);
        ejbQuery.setEJBQLString("Select Object(employee) from Employee employee where employee.firstName = :name");
        ejbQuery.addArgument("name");
        
        Vector args = new Vector();
        args.add("Bob");
        getSession().executeQuery(query, args);
        getSession().executeQuery(ejbQuery, args);
    }

    /**
     * Execute expression query dynamically, disabling the expression cache.
     */
    public void test() throws Exception {
        ClassDescriptor descriptor = getSession().getClassDescriptor(Employee.class);
        int size = descriptor.getQueryManager().getExpressionQueryCacheMaxSize();
        descriptor.getQueryManager().setExpressionQueryCacheMaxSize(0);
        ReadObjectQuery query = new ReadObjectQuery(Employee.class);
        query.setSelectionCriteria(query.getExpressionBuilder().get("firstName").equal("Bob"));
        
        Object result = getSession().executeQuery(query);
        descriptor.getQueryManager().setExpressionQueryCacheMaxSize(size);
    }

    /**
     * Execute prepared query.
     */
    public PerformanceComparisonTestCase buildPreparedTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void test() {
                Vector args = new Vector();
                args.add("Bob");
                Object result = getSession().executeQuery(query, args);
            }
        };
        test.setName("PreparedReadObjectTest");
        test.setAllowableDecrease(5);
        return test;
    }

    /**
     * Execute prepared EJBQL query.
     */
    public PerformanceComparisonTestCase buildPreparedEJBQLTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void test() {
                Vector args = new Vector();
                args.add("Bob");
                Object result = getSession().executeQuery(ejbQuery, args);
            }
        };
        test.setName("PreparedEJBQLReadObjectTest");
        test.setAllowableDecrease(5);
        return test;
    }

    /**
     * Execute dynamic EJBQL query.
     */
    public PerformanceComparisonTestCase buildDynamicEJBQLTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void test() {
                ReadObjectQuery ejbQuery = new ReadObjectQuery(Employee.class);
                ejbQuery.setEJBQLString("Select Object(employee) from Employee employee where employee.firstName = :name");
                ejbQuery.addArgument("name");
                
                Vector args = new Vector();
                args.add("Bob");
                Object result = getSession().executeQuery(ejbQuery, args);
            }
        };
        test.setName("DynamicParseCachedEJBQLReadObjectTest");
        test.setAllowableDecrease(5);
        return test;
    }

    /**
     * Execute dynamic EJBQL query disabling parse cache.
     */
    public PerformanceComparisonTestCase buildDynamicNoParseCacheEJBQLTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void test() {
                int size = getSession().getProject().getJPQLParseCacheMaxSize();
                getSession().getProject().setJPQLParseCacheMaxSize(0);
                ReadObjectQuery ejbQuery = new ReadObjectQuery(Employee.class);
                ejbQuery.setEJBQLString("Select Object(employee) from Employee employee where employee.firstName = :name");
                ejbQuery.addArgument("name");
                
                Vector args = new Vector();
                args.add("Bob");
                Object result = getSession().executeQuery(ejbQuery, args);
                getSession().getProject().setJPQLParseCacheMaxSize(size);
            }
        };
        test.setName("DynamicDisabledParseCacheEJBQLReadObjectTest");
        test.setAllowableDecrease(-25);
        return test;
    }

    /**
     * Execute dynamic expression query using the expression cache.
     */
    public PerformanceComparisonTestCase buildDynamicExpressionCachedExpressionTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void test() {
                ReadObjectQuery query = new ReadObjectQuery(Employee.class);                
                query.setSelectionCriteria(query.getExpressionBuilder().get("firstName").equal("Bob"));
                
                Object result = getSession().executeQuery(query);
            }
        };
        test.setName("DynamicExpressionCachedExpressionReadObjectTest");
        test.setAllowableDecrease(5);
        return test;
    }

}
