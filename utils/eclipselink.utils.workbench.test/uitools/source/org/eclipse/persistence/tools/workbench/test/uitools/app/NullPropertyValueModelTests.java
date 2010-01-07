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
package org.eclipse.persistence.tools.workbench.test.uitools.app;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.uitools.app.NullPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;


public class NullPropertyValueModelTests extends TestCase {
	private PropertyValueModel valueHolder;

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
