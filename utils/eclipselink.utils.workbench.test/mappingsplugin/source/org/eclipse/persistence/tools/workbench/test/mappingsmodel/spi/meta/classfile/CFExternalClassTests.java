/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.spi.meta.classfile;

import org.eclipse.persistence.tools.workbench.test.mappingsmodel.spi.meta.ExternalClassTests;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.swingui.TestRunner;

import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.classfile.CFExternalClassRepositoryFactory;


public class CFExternalClassTests extends ExternalClassTests {

	public static void main(String[] args) {
		TestRunner.main(new String[] {"-c", CFExternalClassTests.class.getName()});
	}

	public static Test suite() {
		return new TestSuite(CFExternalClassTests.class);
	}

	public CFExternalClassTests(String name) {
		super(name);
	}

	protected ExternalClassRepository buildRepository() {
		return CFExternalClassRepositoryFactory.instance().buildClassRepository(AllModelSPIMetaClassFileTests.buildMinimumSystemClasspath());
	}

}
