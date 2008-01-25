/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.mappingsio;

import junit.framework.Test;
import junit.swingui.TestRunner;

/**
 * decentralize test creation code
 */
public class MostMappingsIOTests {

	public static void main(String[] args) {
		TestRunner.main(new String[] {"-c", MostMappingsIOTests.class.getName()});
	}

	public static Test suite() {
		return AllMappingsIOTests.suite(false);
	}

	private MostMappingsIOTests() {
		super();
	}

}
