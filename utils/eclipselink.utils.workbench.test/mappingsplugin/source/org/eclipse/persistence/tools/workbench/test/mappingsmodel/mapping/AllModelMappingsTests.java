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
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.mapping;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;

/**
 * decentralize test creation code
 */
public class AllModelMappingsTests {

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
