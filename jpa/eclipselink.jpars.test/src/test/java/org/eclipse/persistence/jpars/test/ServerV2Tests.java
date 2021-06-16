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

import org.eclipse.persistence.jpars.test.server.v2.ServerCrudV2Test;
import org.eclipse.persistence.jpars.test.server.v2.ServerEmployeeV2Test;
import org.eclipse.persistence.jpars.test.server.v2.ServerFieldsFilteringTest;
import org.eclipse.persistence.jpars.test.server.v2.ServerLinksTest;
import org.eclipse.persistence.jpars.test.server.v2.ServerPageableTest;
import org.eclipse.persistence.jpars.test.server.v2.ServerTravelerV2Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        ServerCrudV2Test.class,
        ServerEmployeeV2Test.class,
        ServerTravelerV2Test.class,
        ServerPageableTest.class,
        ServerFieldsFilteringTest.class,
        ServerLinksTest.class
})
public class ServerV2Tests {

}
