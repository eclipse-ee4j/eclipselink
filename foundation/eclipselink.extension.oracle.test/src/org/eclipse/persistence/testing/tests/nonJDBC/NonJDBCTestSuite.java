/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.testing.tests.nonJDBC;

//JUnit imports
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
    Ni1TestSet.class,
    Ni2TestSet.class,
    Ni3TestSet.class,
    Ni4TestSet.class,
    Ni5TestSet.class,
    Ni6TestSet.class,
    Ni7TestSet.class,
    Ni8TestSet.class,
    Ni9TestSet.class,
    Ni10TestSet.class,
    NijiTestSet.class,
    jiNiTestSet.class,
    NijiNiTestSet.class,
    jiNijiTestSet.class,
    NijijiNiTestSet.class,
    jiNiNijiTestSet.class,
    NoTestSet.class,
    NojiTestSet.class,
    jiNoTestSet.class,
    NojiNoTestSet.class,
    jiNioTestSet.class,
    jiNojiTestSet.class,
    joNijoTestSet.class,
    joNijiNoTestSet.class,
    joNijioTestSet.class,
    jiNiojiTestSet.class,
    NojijioNoTestSet.class,
    NijiojiNiTestSet.class,
    NijioNijioTestSet.class
})
public class NonJDBCTestSuite {
}
