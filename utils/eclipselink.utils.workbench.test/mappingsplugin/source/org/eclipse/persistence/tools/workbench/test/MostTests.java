/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test;

import junit.framework.Test;
import junit.textui.TestRunner;

/**
 * Run all the tests, minus the most painful ones.
 */
public class MostTests {

	public static void main(String[] args) {
		TestRunner.main(new String[] {"-c", MostTests.class.getName()});
	}

	public static Test suite() {
		return AllTests.suite(false);
	}

	private MostTests() {
		super();
		throw new UnsupportedOperationException();
	}
	
}
