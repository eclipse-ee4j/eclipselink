/****************************************************************************
 * Copyright (c) 2011, 2014 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      tware - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.jpars.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * All JPARS service tests. Server is not required to run these tests.
 */
@RunWith(Suite.class)
@SuiteClasses({
        ServiceNoVersionTests.class,
        ServiceV1Tests.class,
        ServiceV2Tests.class
})
public class AllJavaSETests {

}
