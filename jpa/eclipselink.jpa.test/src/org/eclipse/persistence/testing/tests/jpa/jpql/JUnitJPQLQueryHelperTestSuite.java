/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.testing.tests.jpa.jpql;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.jpa.JPAQuery;
import org.eclipse.persistence.internal.jpa.jpql.ConstructorQueryMappings;
import org.eclipse.persistence.internal.jpa.jpql.JPQLQueryHelper;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.Address;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee.SalaryRate;

/**
 * Tests for {@link JPQLQueryHelper}.
 *
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
public class JUnitJPQLQueryHelperTestSuite extends JUnitTestCase {

    public JUnitJPQLQueryHelperTestSuite() {
        super();
    }

    public JUnitJPQLQueryHelperTestSuite(String name) {
        super(name);
    }

    public static Test suite() {

        TestSuite suite = new TestSuite(JUnitJPQLQueryHelperTestSuite.class.getSimpleName());

        for (Method method : JUnitJPQLQueryHelperTestSuite.class.getMethods()) {
            String name = method.getName();
            if (name.startsWith("test_")) {
                suite.addTest(new JUnitJPQLQueryHelperTestSuite(name));
            }
        }

        return suite;
    }

    private int size(Iterable<DatabaseMapping> mappings) {

        int count = 0;

        for (DatabaseMapping mapping : mappings) {
            count++;
        }

        return count;
    }

    @Override
    public void tearDown() {
        super.tearDown();
        clearCache();
    }

    public void test_getClassDescriptors_01() {

        String jpqlQuery = "SELECT e FROM Employee e";

        Collection<String> entityNames = new ArrayList<String>();
        entityNames.add("Employee");

        testGetClassDescriptors(jpqlQuery, entityNames);
    }

    public void test_getClassDescriptors_02() {

        String jpqlQuery = "SELECT e FROM Employee e, Address a";

        Collection<String> entityNames = new ArrayList<String>();
        entityNames.add("Employee");
        entityNames.add("Address");

        testGetClassDescriptors(jpqlQuery, entityNames);
    }

    public void test_getClassDescriptors_03() {

        String jpqlQuery = "SELECT e FROM Employee e JOIN e.projects p JOIN e.phoneNumbers pn";

        Collection<String> entityNames = new ArrayList<String>();
        entityNames.add("Employee");
        entityNames.add("Project");
        entityNames.add("PhoneNumber");

        testGetClassDescriptors(jpqlQuery, entityNames);
    }

    public void test_getClassDescriptors_04() {

        String jpqlQuery = "SELECT e FROM Employee e, IN(e.dealers) d";

        Collection<String> entityNames = new ArrayList<String>();
        entityNames.add("Employee");
        entityNames.add("Dealer");

        testGetClassDescriptors(jpqlQuery, entityNames);
    }

    public void test_getClassDescriptors_05() {

        String jpqlQuery = "SELECT e FROM Employee e, IN(e.dealers) d WHERE e.name = (SELECT a.version FROM e.address a)";

        Collection<String> entityNames = new ArrayList<String>();
        entityNames.add("Employee");
        entityNames.add("Dealer");
        entityNames.add("Address");

        testGetClassDescriptors(jpqlQuery, entityNames);
    }

    public void test_getClassDescriptors_06() {

        String jpqlQuery = "UPDATE Employee e SET e.name = 'JPQL'";

        Collection<String> entityNames = new ArrayList<String>();
        entityNames.add("Employee");

        testGetClassDescriptors(jpqlQuery, entityNames);
    }

    public void test_getClassDescriptors_07() {

        String jpqlQuery = "UPDATE Employee e SET e.name = 'JPQL' WHERE NOT EXIST (SELECT e FROM Project p)";

        Collection<String> entityNames = new ArrayList<String>();
        entityNames.add("Employee");
        entityNames.add("Project");

        testGetClassDescriptors(jpqlQuery, entityNames);
    }

    public void test_getClassDescriptors_08() {

        String jpqlQuery = "DELETE FROM Employee e WHERE e.name = 'JPQL'";

        Collection<String> entityNames = new ArrayList<String>();
        entityNames.add("Employee");

        testGetClassDescriptors(jpqlQuery, entityNames);
    }

    public void test_getClassDescriptors_09() {

        String jpqlQuery = "DELETE FROM Employee e "+
                           "WHERE     (SELECT e.id FROM Project p) > 2 " +
                           "      AND " +
                           "          NOT EXIST (SELECT b FROM Buyer b)";

        Collection<String> entityNames = new ArrayList<String>();
        entityNames.add("Employee");
        entityNames.add("Project");
        entityNames.add("Buyer");

        testGetClassDescriptors(jpqlQuery, entityNames);
    }

    public void test_getConstructorQueryMappings_01() {

        String jpqlQuery = "SELECT e FROM Employee e";
        JPAQuery query = new JPAQuery(jpqlQuery);
        query.setSession(getDatabaseSession());

        JPQLQueryHelper helper = new JPQLQueryHelper();
        ConstructorQueryMappings constructorQuery = helper.getConstructorQueryMappings(query.getSession(), query);

        assertNotNull("ConstructorQueryMappings should not be null", constructorQuery);
        assertSame   ("The query was not cached correctly",          query, constructorQuery.getQuery());
        assertFalse  ("The query is not a constructor query",        constructorQuery.isConstructorQuery());
        assertNull   ("The class name should be null",               constructorQuery.getClassName());
        assertNotNull("The list of mappings cannot be null",         constructorQuery.mappings());
        assertFalse  ("The list of mappings should be empty",        constructorQuery.mappings().iterator().hasNext());
    }

    public void test_getConstructorQueryMappings_02() {

        String jpqlQuery = "SELECT NEW org.eclipse.persistence.testing.tests.jpa.jpql.JUnitJPQLQueryHelperTestSuite.MyConstructorClass(" +
                           "   e.lastName, e.id, e.address, e.payScale" +
                           ") " +
                           "FROM Employee e";

        JPAQuery query = new JPAQuery(jpqlQuery);
        query.setSession(getDatabaseSession());

        JPQLQueryHelper helper = new JPQLQueryHelper();
        ConstructorQueryMappings constructorQuery = helper.getConstructorQueryMappings(query.getSession(), query);

        assertNotNull("ConstructorQueryMappings should not be null", constructorQuery);
        assertSame   ("The query was not cached correctly",          query, constructorQuery.getQuery());
        assertTrue   ("The query is a constructor query",            constructorQuery.isConstructorQuery());

        String expectedClassName = MyConstructorClass.class.getName().replace('$', '.');
        assertEquals ("The class name should be null", expectedClassName, constructorQuery.getClassName());

        Iterable<DatabaseMapping> mappings = constructorQuery.mappings();

        assertNotNull("The list of mappings cannot be null",  mappings);
        assertTrue   ("The list of mappings should be empty", mappings.iterator().hasNext());
        assertEquals ("The count of mappings should be 4", 4, size(mappings));

        int index = 0;

        for (DatabaseMapping mapping : mappings) {
            switch (index++) {
                case 0: {
                    assertEquals("lastName", mapping.getAttributeName());
                    break;
                }
                case 1: {
                    assertEquals("id", mapping.getAttributeName());
                    break;
                }
                case 2: {
                    assertEquals("address", mapping.getAttributeName());
                    break;
                }
                case 3: {
                    assertEquals("payScale", mapping.getAttributeName());
                    break;
                }
            }
        }
    }

    private void testGetClassDescriptors(String jpqlQuery, Collection<String> entityNames) {

        JPQLQueryHelper helper = new JPQLQueryHelper();

        List<ClassDescriptor> descriptors = helper.getClassDescriptors(jpqlQuery, getDatabaseSession());
        assertNotNull("The list of ClassDescriptors cannot be null", descriptors);
        assertEquals(entityNames.size(), descriptors.size());

        for (ClassDescriptor descriptor : descriptors) {
            String alias = descriptor.getAlias();
            assertTrue(alias + " is not expected", entityNames.remove(alias));
        }

        assertTrue(entityNames + " are expected", entityNames.isEmpty());
    }

    private static final class MyConstructorClass {

        private MyConstructorClass(String name, Integer id, Address address, SalaryRate payScale) {
            super();
        }
    }
}
