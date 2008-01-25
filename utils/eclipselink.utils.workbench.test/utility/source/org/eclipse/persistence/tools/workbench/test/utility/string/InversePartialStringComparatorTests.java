/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.utility.string;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.swingui.TestRunner;

import org.eclipse.persistence.tools.workbench.utility.string.CaseInsensitivePartialStringComparator;
import org.eclipse.persistence.tools.workbench.utility.string.InversePartialStringComparator;
import org.eclipse.persistence.tools.workbench.utility.string.PartialStringComparator;

public class InversePartialStringComparatorTests extends TestCase {
	
	public static void main(String[] args) {
		TestRunner.main(new String[] {"-c", InversePartialStringComparatorTests.class.getName()});
	}
	
	public static Test suite() {
		return new TestSuite(InversePartialStringComparatorTests.class);
	}
	
	public InversePartialStringComparatorTests(String name) {
		super(name);
	}

	public void testSameCase() {
		assertEquals(0.0, this.buildComparator().compare("FooBar", "FooBar"), 0.0);
	}

	public void testDifferentCase() {
		assertEquals(0.0, this.buildComparator().compare("FOOBAR", "FooBar"), 0.0);
	}

	public void testMismatch() {
		assertEquals(1.0, this.buildComparator().compare("FooBar1", "FooBar2"), 0.0);
	}

	private PartialStringComparator buildComparator() {
		return new InversePartialStringComparator(CaseInsensitivePartialStringComparator.instance());
	}
}
