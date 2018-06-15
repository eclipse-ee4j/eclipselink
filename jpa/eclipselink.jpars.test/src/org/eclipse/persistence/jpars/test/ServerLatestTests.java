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

import org.eclipse.persistence.jpars.test.server.latest.ServerCrudLatestTest;
import org.eclipse.persistence.jpars.test.server.latest.ServerEmployeeLatestTest;
import org.eclipse.persistence.jpars.test.server.latest.ServerFieldsFilteringLatestTest;
import org.eclipse.persistence.jpars.test.server.latest.ServerLinksLatestTest;
import org.eclipse.persistence.jpars.test.server.latest.ServerPageableLatestTest;
import org.eclipse.persistence.jpars.test.server.latest.ServerTravelerLatestTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        ServerCrudLatestTest.class,
        ServerEmployeeLatestTest.class,
        ServerTravelerLatestTest.class,
        ServerPageableLatestTest.class,
        ServerFieldsFilteringLatestTest.class,
        ServerLinksLatestTest.class
})
public class ServerLatestTests {

}
