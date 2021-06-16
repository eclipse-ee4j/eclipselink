/*
 * Copyright (c) 2014, 2021 Oracle and/or its affiliates. All rights reserved.
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
