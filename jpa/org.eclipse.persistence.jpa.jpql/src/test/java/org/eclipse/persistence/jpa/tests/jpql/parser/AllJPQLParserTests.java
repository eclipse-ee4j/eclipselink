/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.tests.jpql.parser;

import org.eclipse.persistence.jpa.tests.jpql.JPQLTestRunner;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/**
 * The root test suite containing the test suites that define how to test the parser with various
 * JPA versions and extensions.
 *
 * @version 2.5.1
 * @since 2.4
 * @author Pascal Filion
 */
@SuiteClasses({
    AllJPQLParserTests1_0.class,
    AllJPQLParserTests2_0.class,
    AllJPQLParserTests2_1.class,
    AllEclipseLinkJPQLParserTests.class,
    AllEclipseLinkJPQLParserTests2_1.class,
    AllEclipseLinkJPQLParserTests2_4.class,
    AllEclipseLinkJPQLParserTests2_5.class,
    AllJPQLParserConcurrentTests.class
})
@RunWith(JPQLTestRunner.class)
public final class AllJPQLParserTests {

    private AllJPQLParserTests() {
        super();
    }
}
