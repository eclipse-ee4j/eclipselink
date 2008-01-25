/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.mapping;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.swingui.TestRunner;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;

/**
 * decentralize test creation code
 */
public class AllModelMappingsTests {

	public static void main(String[] args) {
		TestRunner.main(new String[] {"-c", AllModelMappingsTests.class.getName()});
	}
	
	public static Test suite() {
		TestSuite suite = new TestSuite(ClassTools.packageNameFor(AllModelMappingsTests.class));
		
		suite.addTest(MWMappingTests.suite());
		suite.addTest(MWDirectMappingTests.suite());
		suite.addTest(MWDirectToFieldMappingTests.suite());
		suite.addTest(MWObjectTypeConverterTests.suite());
		suite.addTest(MWTypeConversionConverterTests.suite());
		suite.addTest(MWDirectToXmlTypeMappingTests.suite());
		suite.addTest(MWXmlDirectMappingTests.suite());
		suite.addTest(MWRelationalDirectCollectionMappingTests.suite());
		suite.addTest(MWRelationalDirectMapMappingTests.suite());
		suite.addTest(MWXmlDirectCollectionMappingTests.suite());
		suite.addTest(MWCompositeObjectMappingTests.suite());
		suite.addTest(MWCompositeCollectionMappingTests.suite());
		suite.addTest(MWAggregateMappingTests.suite());
		suite.addTest(MWAggregatePathToColumnTests.suite());
		suite.addTest(MWCollectionMappingTests.suite());
		suite.addTest(MWManyToManyMappingTests.suite());
		suite.addTest(MWOneToManyMappingTests.suite());
		suite.addTest(MWReferenceMappingTests.suite());
		suite.addTest(MWTableReferenceMappingTests.suite());
		suite.addTest(MWTransformationMappingTests.suite());
		suite.addTest(MWVariableOneToOneMappingTests.suite());
	
		return suite;
	}
	
	/**
	 * suppress instantiation
	 */
	private AllModelMappingsTests() {
		super();
	}
}
