/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.simultaneous;

import java.util.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.descriptors.DescriptorQueryManager;
import org.eclipse.persistence.expressions.*;

/**
 * Bug 3355199
 * Test to ensure the multiple addQuery()s to DescriptorQueryManager in multiple threads
 * will not result in null queries being return by getQuery.
 */
public class DescriptorQueryManagerMultithreadedTest extends MultithreadTestCase {
    protected Vector queries = null;
    public static final String QUERY_STRING = "TestQuery";

    /**
     * Inner class to test DescriptorQueryManager
     * Defined as an inner class because this class cannot properly clean up the DescriptorQueryManager
     * without using the methods in Multithreaded test.
     */
    public class DescriptorQueryManagerAddQueryTest extends AutoVerifyTestCase {
        protected DescriptorQueryManager queryManager = null;

        public DescriptorQueryManagerAddQueryTest() {
            setDescription("Ensure DescriptorQueryManager works well with concurrent getQuery() and addQuery()");
        }

        public void test() {
            queryManager = getSession().getDescriptor(Employee.class).getQueryManager();

            // get the query to see if it exists and then sleep to allow other threads to get past this
            // line of code - all threads should find the query as null
            ReadObjectQuery query = (ReadObjectQuery)queryManager.getQuery(QUERY_STRING);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException exc) {
            }
            ;
            if (query == null) {
                ExpressionBuilder bld = new org.eclipse.persistence.expressions.ExpressionBuilder();
                Expression exp = bld.get("firstName").equal(bld.getParameter("myFirstName"));

                query = new ReadObjectQuery(Employee.class, exp);
                query.addArgument("myFirstName");
                // Add a query.  For the first thread, this should do a simple add, for all subsequent adds,
                // this should do a replace
                queryManager.addQuery(QUERY_STRING, query);
            }
        }

        public void verify() {
            // getQuery() will return null if either there is no query in the list, or if there
            // are multiple queries in the list since we are not providing a list of arguments.
            if (queryManager.getQuery(QUERY_STRING) == null) {
                throw new TestErrorException("QueryManager.addQuery() is having concurrency problems.");
            }
        }
    }

    // End of inner class definition
    public DescriptorQueryManagerMultithreadedTest() {
        super();
        setDescription("Tests adding queris to a DescriptorQueryManager with multiple simultaneous threads");
        Vector tests = new Vector();
        tests.add(new DescriptorQueryManagerAddQueryTest());
        tests.add(new DescriptorQueryManagerAddQueryTest());
        tests.add(new DescriptorQueryManagerAddQueryTest());
        tests.add(new DescriptorQueryManagerAddQueryTest());
        tests.add(new DescriptorQueryManagerAddQueryTest());
        tests.add(new DescriptorQueryManagerAddQueryTest());
        tests.add(new DescriptorQueryManagerAddQueryTest());
        tests.add(new DescriptorQueryManagerAddQueryTest());
        tests.add(new DescriptorQueryManagerAddQueryTest());
        tests.add(new DescriptorQueryManagerAddQueryTest());
        setTests(tests);
    }

    public void setup() {
        super.setup();
        // save the current queries
        ClassDescriptor descriptor = getSession().getClassDescriptor(Employee.class);
        Map queryTable = descriptor.getQueryManager().getQueries();
        if (queryTable != null) {
            queries = (Vector)queryTable.get(QUERY_STRING);
        }
    }

    public void reset() {
        super.reset();
        // restore the current queries
        DescriptorQueryManager queryManager = getSession().getDescriptor(Employee.class).getQueryManager();
        queryManager.removeQuery(QUERY_STRING);
        if (queries != null) {
            Enumeration enumtr = queries.elements();
            while (enumtr.hasMoreElements()) {
                queryManager.addQuery((DatabaseQuery)enumtr.nextElement());
            }
        }
    }
}
