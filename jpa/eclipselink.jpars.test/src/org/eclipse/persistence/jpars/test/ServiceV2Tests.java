/****************************************************************************
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
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
        LinksTest.class
})
public class ServiceV2Tests {

}
