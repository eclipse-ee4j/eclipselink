/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink


package org.eclipse.persistence.testing.tests.jpa.validation;

import jakarta.persistence.Query;

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
