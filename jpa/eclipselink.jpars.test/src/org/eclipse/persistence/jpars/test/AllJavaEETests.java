/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//      tware - Initial implementation
package org.eclipse.persistence.jpars.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * All server JPARS tests. test application installed on the server is required for running.
 */
@RunWith(Suite.class)
@SuiteClasses({
        ServerV2Tests.class,
        ServerV1Tests.class,
        ServerNoVersionTests.class,
        ServerLatestTests.class
})
public class AllJavaEETests {

}
