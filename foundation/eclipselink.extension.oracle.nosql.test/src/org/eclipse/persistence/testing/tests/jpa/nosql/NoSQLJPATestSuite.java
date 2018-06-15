/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     04/11/2016-2.7 Tomas Kraus
//       - 490677: Allow Oracle NoSQL connection to be specified in build properties bundle
package org.eclipse.persistence.testing.tests.jpa.nosql;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Oracle NoSQL JPA database tests suite.
 */
@RunWith(Suite.class)
@SuiteClasses({
    NoSQLJPATest.class,
    NoSQLJPAMappedTest.class
})
public class NoSQLJPATestSuite {

}
