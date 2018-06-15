/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     David McCann - Dec.05, 2012 - 2.4.2 - Initial Implementation
package dbws.testing;

//JUnit4 imports
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.StringTokenizer;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

//testing imports
import dbws.testing.keymappings.KeyMappingsTestSuite;
import dbws.testing.relationships.RelationshipsTestSuite;
import dbws.testing.xrdynamicentity.XRDynamicEntityTestSuite;
import static dbws.testing.DBWSTestHelper.DATABASE_PASSWORD_KEY;
import static dbws.testing.DBWSTestHelper.DATABASE_URL_KEY;
import static dbws.testing.DBWSTestHelper.DATABASE_USERNAME_KEY;
import static dbws.testing.DBWSTestHelper.DEFAULT_DATABASE_DRIVER;
import static dbws.testing.DBWSTestHelper.DEFAULT_DATABASE_PASSWORD;
import static dbws.testing.DBWSTestHelper.DEFAULT_DATABASE_URL;
import static dbws.testing.DBWSTestHelper.DEFAULT_DATABASE_USERNAME;

@RunWith(Suite.class)
@SuiteClasses({
    KeyMappingsTestSuite.class,
    RelationshipsTestSuite.class,
    XRDynamicEntityTestSuite.class
  }
)

/**
 * Responsible for running the suite of non-oracle (MYSQL) DBWS runtime tests.
 *
 */
public class AllTests {
    public static final String DELIM = "|";

    /**
     * Build a MYSQL database connection to use when executing create and
     * drop DDL statements.
     */
    public static Connection buildConnection() throws ClassNotFoundException, SQLException {
        String username = System.getProperty(DATABASE_USERNAME_KEY, DEFAULT_DATABASE_USERNAME);
        String password = System.getProperty(DATABASE_PASSWORD_KEY, DEFAULT_DATABASE_PASSWORD);
        String url = System.getProperty(DATABASE_URL_KEY, DEFAULT_DATABASE_URL);
        Class.forName(DEFAULT_DATABASE_DRIVER);
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * Execute the given DDL string.  If more than one statement is contained within the
     * given string, it is expected that the statements are separated by '|'.
     */
    public static void runDdl(String ddl, boolean printStackTrace) throws ClassNotFoundException, SQLException {
        try {
            Statement stmt = buildConnection().createStatement();
            StringTokenizer stok = new StringTokenizer(ddl, DELIM);
            while (stok.hasMoreTokens()) {
                stmt.addBatch(stok.nextToken());
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            if (printStackTrace) {
                e.printStackTrace();
            }
        }
    }
}
