/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
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

//JUnit4 imports
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

//testing imports
import dbws.testing.customsql.CustomSQLTestSuite;
import dbws.testing.iottype.IOTTypeTestSuite;
import dbws.testing.objecttype.ObjectTypeTestSuite;
import dbws.testing.plsqlcollection.PLSQLCollectionTestSuite;
import dbws.testing.plsqlrecord.PLSQLRecordTestSuite;
import dbws.testing.secondarysql.SecondarySQLTestSuite;
import dbws.testing.simpleplsqlsf.SimplePLSQLSFTestSuite;
import dbws.testing.simpleplsqlsp.SimplePLSQLSPTestSuite;
import dbws.testing.simplesf.SimpleSFTestSuite;
import dbws.testing.simplesp.SimpleSPTestSuite;
import dbws.testing.simpletablewithnestedsql.SimpleTableWithNestedSQLTestSuite;
import dbws.testing.tabletype.TableTypeTestSuite;
import dbws.testing.varray.VArrayTestSuite;

@RunWith(Suite.class)
@SuiteClasses({
    CustomSQLTestSuite.class,
    SecondarySQLTestSuite.class,
    SimpleSFTestSuite.class,
    SimpleSPTestSuite.class,
    IOTTypeTestSuite.class,
    TableTypeTestSuite.class,
    SimplePLSQLSPTestSuite.class,
    SimplePLSQLSFTestSuite.class,
    SimpleTableWithNestedSQLTestSuite.class,
    PLSQLRecordTestSuite.class,
    PLSQLCollectionTestSuite.class,
    VArrayTestSuite.class,
    ObjectTypeTestSuite.class
  }
)
public class AllTests {
}