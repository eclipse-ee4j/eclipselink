/*******************************************************************************
 * Copyright (c) 2014, 2015  Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      Dmitry Kornilov - Initial implementation
 ******************************************************************************/
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
