/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.mappingsmodel;

import org.eclipse.persistence.tools.workbench.test.mappingsmodel.automap.AllModelAutomapTests;
import org.eclipse.persistence.tools.workbench.test.mappingsmodel.db.AllModelDatabaseTests;
import org.eclipse.persistence.tools.workbench.test.mappingsmodel.descriptor.AllModelDescriptorTests;
import org.eclipse.persistence.tools.workbench.test.mappingsmodel.mapping.AllModelMappingsTests;
import org.eclipse.persistence.tools.workbench.test.mappingsmodel.meta.AllModelMetaTests;
import org.eclipse.persistence.tools.workbench.test.mappingsmodel.query.AllModelQueryTests;
import org.eclipse.persistence.tools.workbench.test.mappingsmodel.schema.AllModelSchemaTests;
import org.eclipse.persistence.tools.workbench.test.mappingsmodel.spi.meta.AllModelSPIMetaTests;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.swingui.TestRunner;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;


/**
 * decentralize test creation code
 */
public class AllMappingsModelTests {

	public static void main(String[] args) {
		TestRunner.main(new String[] {"-c", AllMappingsModelTests.class.getName()});
	}

	public static Test suite() {
		return suite(true);
	}
	
	public static Test suite(boolean all) {
		TestSuite suite = new TestSuite(ClassTools.packageNameFor(AllMappingsModelTests.class));

		suite.addTest(AllModelAutomapTests.suite());
		suite.addTest(AllModelDatabaseTests.suite());
		suite.addTest(AllModelDescriptorTests.suite());
		suite.addTest(AllModelMappingsTests.suite());
		suite.addTest(AllModelMetaTests.suite());
		suite.addTest(AllModelQueryTests.suite());
		suite.addTest(AllModelSchemaTests.suite());
		suite.addTest(AllModelSPIMetaTests.suite());
		
		suite.addTest(DeploymentXMLTests.suite());
		suite.addTest(ExportRuntimeProjectJavaSourceTests.suite());
		suite.addTest(MWProjectTests.suite());
		suite.addTest(ProjectConverterTests.suite());
	
		return suite;
	}

	/**
	 * suppress instantiation
	 */
	private AllMappingsModelTests() {
		super();
		throw new UnsupportedOperationException();
	}

}
