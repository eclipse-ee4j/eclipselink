/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     David McCann - Aug.08, 2012 - 2.4.1 - Initial implementation
package dbws.testing;

//JUnit4 imports
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import dbws.testing.attachedbinary.AttachedBinaryServiceTestSuite;
import dbws.testing.inlinebinary.InlineBinaryServiceTestSuite;
import dbws.testing.legacysimpletable.LegacySimpleTableServiceTestSuite;
import dbws.testing.mtom.MTOMServiceTestSuite;
import dbws.testing.simpleplsql.SimplePLSQLServiceTestSuite;
import dbws.testing.simplesp.SimpleSPServiceTestSuite;
import dbws.testing.simplesql.SimpleSQLServiceTestSuite;
import dbws.testing.simpletable.SimpleTableServiceTestSuite;

@RunWith(Suite.class)
@SuiteClasses({
    AttachedBinaryServiceTestSuite.class,
    InlineBinaryServiceTestSuite.class,
    LegacySimpleTableServiceTestSuite.class,
    MTOMServiceTestSuite.class,
    SimplePLSQLServiceTestSuite.class,
    SimpleSPServiceTestSuite.class,
    SimpleSQLServiceTestSuite.class,
    SimpleTableServiceTestSuite.class
  }
)
public class AllServiceTests {
}
