/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.testing.tests.jpa.persistence32;

import java.util.Set;

import junit.framework.TestSuite;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.jpa.JpaEntityManagerFactory;
import org.eclipse.persistence.testing.framework.ReflectionHelper;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.tools.schemaframework.TableCreator;

/**
 * Test {@link jakarta.persistence.SchemaManager} implementation in EclipseLink.
 * This is an abstract class with all common code for child classes with individual tests.
 * All those tests are based on database schema modifications, so they shall not run in parallel.
 * Each child class shall contain just a single test.
 */
public abstract class AbstractSchemaManager extends JUnitTestCase  {
    private org.eclipse.persistence.tools.schemaframework.SchemaManager schemaManager;
    JpaEntityManagerFactory emf = null;

    public AbstractSchemaManager() {
    }

    public AbstractSchemaManager(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    @Override
    public String getPersistenceUnitName() {
        return "persistence32";
    }

    @Override
    public void setUp() {
        super.setUp();
        emf = getEntityManagerFactory(getPersistenceUnitName())
                .unwrap(EntityManagerFactoryImpl.class);
        schemaManager = new org.eclipse.persistence.tools.schemaframework.SchemaManager(emf.getDatabaseSession());
        dropTables();
    }

    @Override
    public void tearDown() {
        dropTables();
    }

    /**
     * Build test suite.
     * Schema manager suites may contain just a single test.
     *
     * @param name name of the suite
     * @param test test to add to the suite
     * @return collection of tests to execute
     */
    static TestSuite suite(String name, AbstractSchemaManager test) {
        TestSuite suite = new TestSuite();
        suite.setName(name);
        suite.addTest(test);
        return suite;
    }

    TableCreator getDefaultTableCreator() {
        // getDefaultTableCreator has protected access
        try {
            return ReflectionHelper.invokeMethod("getDefaultTableCreator",
                                          schemaManager,
                                          new Class<?>[] {boolean.class},
                                          TableCreator.class,
                    Boolean.TRUE);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Invocation of getDefaultTableCreator failed", e);
        }
    }

    void dropTables() {
        try {
            schemaManager.dropDefaultTables();
        } catch (DatabaseException de) {
            emf.getDatabaseSession().logMessage(de.getLocalizedMessage());
        }
    }

    void createTables() {
        try {
            schemaManager.createDefaultTables(true);
        } catch (DatabaseException de) {
            emf.getDatabaseSession().logMessage(de.getLocalizedMessage());
        }
    }

    void checkMissingTable(Set<String> initialMissingTablesSet, Set<String> missingTablesSet, Set<String> checked, String table) {
        if (missingTablesSet.contains(table)) {
            missingTablesSet.remove(table);
            checked.add(table);
        } else {
            boolean first = true;
            StringBuilder sb = new StringBuilder();
            sb.append('[');
            for (String missingTable : initialMissingTablesSet) {
                if (first) {
                    first = false;
                } else {
                    sb.append(',');
                }
                sb.append(missingTable);
            }
            sb.append(']');
            if (checked.contains(table)) {
                fail(String.format("Duplicate table %s entry was found in expected tables Set %s", table, sb));
            } else {
                fail(String.format("Table %s was not found in expected tables Set %s", table, sb));
            }
        }
    }

}
