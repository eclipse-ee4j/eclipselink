/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     dclarke - Dynamic Persistence INCUBATION - Enhancement 200045
 *     			 http://wiki.eclipse.org/EclipseLink/Development/Dynamic
 *     mnorman - tweaks to work from Ant command-line,
 *               et database properties from System, etc.
 *     
 * This code is being developed under INCUBATION and is not currently included 
 * in the automated EclipseLink build. The API in this code may change, or 
 * may never be included in the product. Please provide feedback through mailing 
 * lists or the bug database.
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.dynamic;

//JUnit4 imports
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
    org.eclipse.persistence.testing.tests.dynamic.dynamicclassloader.DynamicClassLoaderTestSuite.class,
    org.eclipse.persistence.testing.tests.dynamic.dynamichelper.DynamicHelperTestSuite.class,
    org.eclipse.persistence.testing.tests.dynamic.entitytype.AllTests.class,
    org.eclipse.persistence.testing.tests.dynamic.simple.AllTests.class,
    org.eclipse.persistence.testing.tests.dynamic.simple.mappings.AllTests.class,
    org.eclipse.persistence.testing.tests.dynamic.simple.sequencing.AllTests.class,
    org.eclipse.persistence.testing.tests.dynamic.projectxml.AllTests.class,
    org.eclipse.persistence.testing.tests.dynamic.employee.AllTests.class,
    org.eclipse.persistence.testing.tests.dynamic.sessionsxml.AllTests.class
    }
)
public class AllTests {}