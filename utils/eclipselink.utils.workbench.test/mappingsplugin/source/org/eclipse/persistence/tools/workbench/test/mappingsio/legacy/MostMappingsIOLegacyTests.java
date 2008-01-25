/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.mappingsio.legacy;

import junit.framework.Test;
import junit.swingui.TestRunner;

/**
 * decentralize test creation code
 */
public class MostMappingsIOLegacyTests {

	public static void main(String[] args) {
		TestRunner.main(new String[] {"-c", MostMappingsIOLegacyTests.class.getName()});
	}

	public static Test suite() {
		return AllMappingsIOLegacyTests.suite(false);
	}

	private MostMappingsIOLegacyTests() {
		super();
	}

}
