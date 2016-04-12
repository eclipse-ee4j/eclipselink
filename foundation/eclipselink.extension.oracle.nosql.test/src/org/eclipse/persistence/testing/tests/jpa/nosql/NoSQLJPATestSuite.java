/*******************************************************************************
 * Copyright (c) 2016 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     04/11/2016-2.7 Tomas Kraus
 *       - 490677: Allow Oracle NoSQL connection to be specified in build properties bundle
 ******************************************************************************/
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
