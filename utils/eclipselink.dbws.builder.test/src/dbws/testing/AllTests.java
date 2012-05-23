/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Mike Norman - June 10 2011, created DDL parser package
 *     David McCann - July 2011, visit tests
 ******************************************************************************/
package dbws.testing;

//javase imports
import java.sql.Connection;
import java.sql.SQLException;

//JUnit4 imports
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

//testing imports
import dbws.testing.attachedbinary.AttachedBinaryTestSuite;
import dbws.testing.batchsql.BatchSQLTestSuite;
import dbws.testing.bindingmodel.BindingModelTestSuite;
import dbws.testing.crud.CRUDTestSuite;
import dbws.testing.customsql.CustomSQLTestSuite;
import dbws.testing.inlinebinary.InlineBinaryTestSuite;
import dbws.testing.invalidinput.InvalidInputTestSuite;
import dbws.testing.loglevelvalidation.LogLevelValidationTestSuite;
import dbws.testing.mtom.MTOMTestSuite;
import dbws.testing.optlock.OptLockTestSuite;
import dbws.testing.rootcause.RootCauseTestSuite;
import dbws.testing.secondarysql.SecondarySQLTestSuite;
import dbws.testing.simplesp.SimpleSPTestSuite;
import dbws.testing.simpletable.SimpleTableTestSuite;
import dbws.testing.simpletablewithnestedsql.SimpleTableWithNestedSQLTestSuite;
import dbws.testing.soap12.SOAP12TestSuite;
import dbws.testing.sqlascollection.SQLAsCollectionTestSuite;
import dbws.testing.updatefault.UpdateFaultTestSuite;
import static dbws.testing.DBWSTestSuite.buildConnection;

@RunWith(Suite.class)
@SuiteClasses({
    AttachedBinaryTestSuite.class,
    BatchSQLTestSuite.class,
    BindingModelTestSuite.class,
    CRUDTestSuite.class,
    CustomSQLTestSuite.class,
    InlineBinaryTestSuite.class,
	InvalidInputTestSuite.class,
    LogLevelValidationTestSuite.class,
    MTOMTestSuite.class,
    OptLockTestSuite.class,
    RootCauseTestSuite.class,
    SecondarySQLTestSuite.class,
    SimpleSPTestSuite.class,
    SimpleTableTestSuite.class,
    SimpleTableWithNestedSQLTestSuite.class,
    SOAP12TestSuite.class,
    SQLAsCollectionTestSuite.class,
    UpdateFaultTestSuite.class
})
public class AllTests {

    //shared JUnit fixtures
    public static Connection conn = null;

    @BeforeClass
    public static void setUp() throws ClassNotFoundException, SQLException {
        conn = buildConnection();
    }
}