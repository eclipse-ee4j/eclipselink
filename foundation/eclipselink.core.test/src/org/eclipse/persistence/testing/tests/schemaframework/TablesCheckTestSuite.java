/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.schemaframework;

import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestModel;
import org.eclipse.persistence.testing.framework.TestSuite;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;
import org.eclipse.persistence.tools.schemaframework.TableCreator;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

/**
 * Table existence check tests suite.
 */
public final class TablesCheckTestSuite extends TestSuite {

    /** Table definition of existing table. */
    private static final TableDefinition BEER_TD = tableDefinition("TCT_BEER");

    /** Table definition of not existing table. */
    private static final TableDefinition WATER_TD = tableDefinition("TCT_WATER");

    /**
     * Creates test suite with this test.
     * @return suite with this test
     */
    public static TestSuite suite() {
        TablesCheckTestSuite suite = new TablesCheckTestSuite();
        suite.setName("Test table existence check");
        suite.addTest(new CurrentPlatformExistingTableTablesCheckTest());
        suite.addTest(new CurrentPlatformNonExistingTableTablesCheckTest());
        suite.addTest(new DefaultPlatformExistingTableTablesCheckTest());
        suite.addTest(new DefaultPlatformNonExistingTableTablesCheckTest());
        return suite;
    }

    /**
     * Creates table definition with given table name for the test.
     * @param name table name
     * @return table definition with given table name
     */
    private static final TableDefinition tableDefinition(final String name) {
        TableDefinition table = new TableDefinition();
        table.setName(name);
        table.addField("NAME", Integer.class);
        table.addField("BRAND", String.class);
        return table;
    }

    /**
     * Tests suite setup: Creates database table for the tests.
     */
    @Override
    public void setup() {
        final TableCreator creator = new TableCreator();
        creator.addTableDefinition(BEER_TD);
        final DatabaseSession session = getDatabaseSession();
        final SchemaManager manager = new SchemaManager(getDatabaseSession());
        // Table life cycle is related to this test life cycle.
        creator.createTables(session, manager);
    }

    /**
     * Test model class for this test suite.
     * This model class is registered in {@link org.eclipse.persistence.testing.tests.TestRunModel}.
     */
    public static final class Model extends TestModel {
        @Override
        public void addTests() {
            addTest(suite());
        }
    }

    /**
     * Table existence check test common code.
     */
    public static abstract class TablesCheckTest extends TestCase {

        /** Database session used in this test. */
        protected DatabaseSession session;

        /** Schema manager used in this test. */
        protected SchemaManager manager;

        /**
         * Creates an instance of table existence check test.
         */
        TablesCheckTest() {
            this.session = null;
            this.manager = null;
        }

        /**
         * Table existence check test setup: Initializes session and schema manager.
         */
        @Override
        public void setup() {
            session = getDatabaseSession();
            manager = new SchemaManager(getDatabaseSession());
       }

        /**
         * Table existence check test reset: Clears session and schema manager.
         */
        @Override
        public void reset() {
            manager = null;
            session = null;
        }

    }

    /**
     * Table existence check test: Verify on current database platform and existing database table.
     */
    public static final class CurrentPlatformExistingTableTablesCheckTest extends TablesCheckTest {

        /**
         * Creates an instance of table existence check test.
         */
        CurrentPlatformExistingTableTablesCheckTest() {
            super();
        }

        /**
         * Test table existence check on existing table using current database platform instance.
         */
        @Override
        public void test() {
            final boolean beerExists = manager.checkTableExists(BEER_TD, false);
            assertTrue(beerExists);
        }

    }

    /**
     * Table existence check test: Verify on current database platform and non existing database table.
     */
    public static final class CurrentPlatformNonExistingTableTablesCheckTest extends TablesCheckTest {

        /**
         * Creates an instance of table existence check test.
         */
        CurrentPlatformNonExistingTableTablesCheckTest() {
            super();
        }

        /**
         * Test table existence check on non existing table using current database platform instance.
         */
        @Override
        public void test() {
            final boolean waterExists = manager.checkTableExists(WATER_TD, false);
            assertFalse(waterExists);
        }

    }

    /**
     * Table existence check test: Verify on default database platform and existing database table.
     */
    public static final class DefaultPlatformExistingTableTablesCheckTest extends TablesCheckTest {

        /**
         * Creates an instance of table existence check test.
         */
        DefaultPlatformExistingTableTablesCheckTest() {
            super();
        }

        /**
         * Test table existence check on existing table using default database platform instance.
         */
        @Override
        public void test() {
            final DatabasePlatform defaultPlatform = new DatabasePlatform();
            final boolean beerExists = defaultPlatform.checkTableExists((DatabaseSessionImpl) session, BEER_TD, false);
            assertTrue(beerExists);
        }

    }

    /**
     * Table existence check test: Verify on default database platform and non existing database table.
     */
    public static final class DefaultPlatformNonExistingTableTablesCheckTest extends TablesCheckTest {

        /**
         * Creates an instance of table existence check test.
         */
        DefaultPlatformNonExistingTableTablesCheckTest() {
            super();
        }

        /**
         * Test table existence check on non existing table using default database platform instance.
         */
        @Override
        public void test() {
            final DatabasePlatform defaultPlatform = new DatabasePlatform();
            final boolean waterExists = defaultPlatform.checkTableExists((DatabaseSessionImpl) session, WATER_TD, false);
            assertFalse(waterExists);
        }

    }

}
