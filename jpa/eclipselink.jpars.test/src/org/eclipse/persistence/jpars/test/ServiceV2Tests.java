/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//      Dmitry Kornilov - Initial implementation
package org.eclipse.persistence.jpars.test;

import org.eclipse.persistence.jpars.test.service.v2.ContextsTest;
import org.eclipse.persistence.jpars.test.service.v2.EmployeeV2Test;
import org.eclipse.persistence.jpars.test.service.v2.LinksTest;
import org.eclipse.persistence.jpars.test.service.v2.MarshalUnmarshalV2Test;
import org.eclipse.persistence.jpars.test.service.v2.MetadataTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        MetadataTest.class,
        MarshalUnmarshalV2Test.class,
        EmployeeV2Test.class,
        LinksTest.class,
        ContextsTest.class
})
public class ServiceV2Tests {

}
