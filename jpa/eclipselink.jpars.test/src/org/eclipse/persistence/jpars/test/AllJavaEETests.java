/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      gonural - initial 
 ******************************************************************************/
package org.eclipse.persistence.jpars.test;

import org.eclipse.persistence.jpars.test.server.ServerCrudTest;
import org.eclipse.persistence.jpars.test.server.ServerEmployeeTest;
import org.eclipse.persistence.jpars.test.server.ServerEmployeeTestV2;
import org.eclipse.persistence.jpars.test.server.ServerTravelerTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(VersionedTestSuite.class)
@SuiteClasses({ ServerEmployeeTestV2.class, ServerCrudTest.class, ServerEmployeeTest.class, ServerTravelerTest.class })
public class AllJavaEETests {

}
