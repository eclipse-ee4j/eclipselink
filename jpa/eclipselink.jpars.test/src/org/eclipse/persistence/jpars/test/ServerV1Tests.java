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

import org.eclipse.persistence.jpars.test.server.v1.ServerCrudV1Test;
import org.eclipse.persistence.jpars.test.server.v1.ServerEmployeeV1Test;
import org.eclipse.persistence.jpars.test.server.v1.ServerTravelerV1Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        ServerEmployeeV1Test.class,
        ServerCrudV1Test.class,
        ServerTravelerV1Test.class
})
public class ServerV1Tests {

}
