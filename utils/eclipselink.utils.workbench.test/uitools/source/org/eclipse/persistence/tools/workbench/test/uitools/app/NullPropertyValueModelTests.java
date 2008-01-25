/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.uitools.app;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.swingui.TestRunner;

import org.eclipse.persistence.tools.workbench.uitools.app.NullPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;


public class NullPropertyValueModelTests extends TestCase {
	private PropertyValueModel valueHolder;

	public static void main(String[] args) {
		TestRunner.main(new String[] {"-c", NullPropertyValueModelTests.class.getName()});
	}
	
	public static Test suite() {
		return new TestSuite(NullPropertyValueModelTests.class);
	}
	
	public NullPropertyValueModelTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.valueHolder = NullPropertyValueModel.instance();
	}

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testSetValue() {
		boolean exCaught = false;
		try {
			this.valueHolder.setValue("foo");
		} catch (UnsupportedOperationException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);
	}

	public void testGetValue() {
		assertNull(this.valueHolder.getValue());
	}

}
