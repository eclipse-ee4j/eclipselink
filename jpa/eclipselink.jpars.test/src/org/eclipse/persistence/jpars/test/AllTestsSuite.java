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
//      gonural - Initial implementation
package org.eclipse.persistence.jpars.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * All JPARS tests. Includes all server and service tests.
 *
 * @author gonural
 */
@RunWith(Suite.class)
@SuiteClasses({
        AllJavaSETests.class,
        AllJavaEETests.class
})
public class AllTestsSuite {

}

