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
//     David McCann - Aug.02, 2012 - 2.4.1 - Initial implementation
package dbws.testing;

//JUnit4 imports
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import dbws.testing.attachedbinary.AttachedBinaryBuilderTestSuite;
import dbws.testing.inlinebinary.InlineBinaryBuilderTestSuite;
import dbws.testing.mtom.MTOMBuilderTestSuite;
import dbws.testing.simpleplsql.SimplePLSQLBuilderTestSuite;
import dbws.testing.simplesp.SimpleSPBuilderTestSuite;
import dbws.testing.simplesql.SimpleSQLBuilderTestSuite;
import dbws.testing.simpletable.SimpleTableBuilderTestSuite;

@RunWith(Suite.class)
@SuiteClasses({
    AttachedBinaryBuilderTestSuite.class,
    InlineBinaryBuilderTestSuite.class,
    MTOMBuilderTestSuite.class,
    SimplePLSQLBuilderTestSuite.class,
    SimpleSPBuilderTestSuite.class,
    SimpleSQLBuilderTestSuite.class,
    SimpleTableBuilderTestSuite.class
  }
)
public class AllBuilderTests {
}
