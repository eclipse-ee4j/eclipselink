/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.spi.meta.classfile;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.classfile.CFExternalClassRepositoryFactory;
import org.eclipse.persistence.tools.workbench.test.mappingsmodel.spi.meta.ExternalMethodTests;


public class CFExternalMethodTests extends ExternalMethodTests {

	public static Test suite() {
		return new TestSuite(CFExternalMethodTests.class);
	}
	
	public CFExternalMethodTests(String name) {
		super(name);
	}
	
	protected ExternalClassRepository buildRepository() {
		return CFExternalClassRepositoryFactory.instance().buildClassRepository(AllModelSPIMetaClassFileTests.buildMinimumSystemClasspath());
	}

}
