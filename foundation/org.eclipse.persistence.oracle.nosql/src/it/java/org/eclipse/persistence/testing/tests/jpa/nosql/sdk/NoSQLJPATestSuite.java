/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation.
package org.eclipse.persistence.testing.tests.jpa.nosql.sdk;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Oracle NoSQL JPA database tests suite.
 */
@RunWith(Suite.class)
@SuiteClasses({
    NoSQLJPATest.class,
    NoSQLJPAMappedTest.class,
    NoSQLJPADataTypesTest.class
})
public class NoSQLJPATestSuite {

}
