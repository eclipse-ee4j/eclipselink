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


package org.eclipse.persistence.testing.tests.jpa.validation;

import javax.persistence.Query;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;

public class QueryParameterValidationTestSuite extends JUnitTestCase {

    public QueryParameterValidationTestSuite() {
    }

    public QueryParameterValidationTestSuite(String name) {
        super(name);
    }

    public void setUp () {
        super.setUp();
        clearCache();
    }

    public void testParameterNameValidation(){
        Query query = createEntityManager().createQuery("Select e from Employee e where e.lastName like :name ");
        try{
            query.setParameter("l", "%ay");
            query.getResultList();
        }catch (IllegalArgumentException ex){
            assertTrue("Failed to throw expected IllegalArgumentException, when incorrect parameter name is used", ex.getMessage().contains("using a name"));
            return;
        }
        fail("Failed to throw expected IllegalArgumentException, when incorrect parameter name is used");
    }


    public void testParameterPositionValidation(){
        Query query = createEntityManager().createQuery("Select e from Employee e where e.firstName like ?1 ");
        try{
            query.setParameter(2, "%ay");
            query.getResultList();
        }catch (IllegalArgumentException ex){
            assertTrue("Failed to throw expected IllegalArgumentException, when incorrect parameter name is used", ex.getMessage().contains("parameter at position"));
            return;
        }
        fail("Failed to throw expected IllegalArgumentException, when incorrect parameter position is used");
    }

    public void testParameterPositionValidation2() {

        Query query = createEntityManager().createQuery("Select e from Employee e where e.firstName = ?1 AND e.lastName = ?3 ");
        try {
            query.setParameter(1, "foo");
            query.setParameter(2, "");
            query.setParameter(3, "bar");
            query.getResultList();
        } catch (IllegalArgumentException ex) {
            assertTrue("Failed to throw expected IllegalArgumentException, when incorrect parameter name is used", ex.getMessage().contains("parameter at position"));
            return;
        }
        fail("Failed to throw expected IllegalArgumentException, when incorrect parameter position is used");
    }


    public static Test suite() {
        TestSuite suite = new TestSuite(QueryParameterValidationTestSuite.class);

        suite.setName("QueryParameterValidationTestSuite");

        return suite;
    }


}
