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
 *     David McCann - Aug.08, 2012 - 2.4.1 - Initial implementation
 ******************************************************************************/
package dbws.testing;

//JUnit4 imports
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import dbws.testing.attachedbinary.AttachedBinaryServiceTestSuite;
import dbws.testing.inlinebinary.InlineBinaryServiceTestSuite;
import dbws.testing.mtom.MTOMServiceTestSuite;
import dbws.testing.simpleplsql.SimplePLSQLServiceTestSuite;
import dbws.testing.simplesp.SimpleSPServiceTestSuite;
import dbws.testing.simplesql.SimpleSQLServiceTestSuite;
import dbws.testing.simpletable.SimpleTableServiceTestSuite;

@RunWith(Suite.class)
@SuiteClasses({
    AttachedBinaryServiceTestSuite.class,
    InlineBinaryServiceTestSuite.class,
    MTOMServiceTestSuite.class,
    SimplePLSQLServiceTestSuite.class,
    SimpleSPServiceTestSuite.class,
    SimpleSQLServiceTestSuite.class,
    SimpleTableServiceTestSuite.class
  }
)
public class AllServiceTests {
}