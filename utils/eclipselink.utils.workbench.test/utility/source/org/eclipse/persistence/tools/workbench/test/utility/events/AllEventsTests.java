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
 *     Oracle - initial API and implementation from Oracle TopLink
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.utility.events;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;

public class AllEventsTests {
	
	public static Test suite() {
		TestSuite suite = new TestSuite(ClassTools.packageNameFor(AllEventsTests.class));

		suite.addTest(ReflectiveCollectionChangeListenerTests.suite());
		suite.addTest(ReflectiveListChangeListenerTests.suite());
		suite.addTest(ReflectivePropertyChangeListenerTests.suite());
		suite.addTest(ReflectiveStateChangeListenerTests.suite());
		suite.addTest(ReflectiveTreeChangeListenerTests.suite());
	
		return suite;
	}
	
	private AllEventsTests() {
		super();
		throw new UnsupportedOperationException();
	}
	
}
