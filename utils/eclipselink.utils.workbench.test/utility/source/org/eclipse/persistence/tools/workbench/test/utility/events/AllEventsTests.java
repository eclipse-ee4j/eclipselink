/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.utility.events;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.swingui.TestRunner;

public class AllEventsTests {
	
	public static void main(String[] args) {
		TestRunner.main(new String[] {"-c", AllEventsTests.class.getName()});
	}
	
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
