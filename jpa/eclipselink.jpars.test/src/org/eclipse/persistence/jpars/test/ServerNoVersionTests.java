/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//      Dmitry Kornilov - Initial implementation
package org.eclipse.persistence.jpars.test;

import org.eclipse.persistence.jpars.test.server.noversion.ServerCrudTest;
import org.eclipse.persistence.jpars.test.server.noversion.ServerEmployeeTest;
import org.eclipse.persistence.jpars.test.server.noversion.ServerTravelerTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        ServerEmployeeTest.class,
        ServerCrudTest.class,
        ServerTravelerTest.class
})
public class ServerNoVersionTests {

}
