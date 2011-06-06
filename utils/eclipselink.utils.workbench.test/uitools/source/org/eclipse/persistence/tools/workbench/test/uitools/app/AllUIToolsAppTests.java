/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.uitools.app.adapters.AllUIToolsAppAdaptersTests;
import org.eclipse.persistence.tools.workbench.test.uitools.app.swing.AllUIToolsAppSwingTests;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;


/**
 * 
 */
public class AllUIToolsAppTests {
	
	public static Test suite() {
		TestSuite suite = new TestSuite(ClassTools.packageNameFor(AllUIToolsAppTests.class));

		suite.addTest(AllUIToolsAppAdaptersTests.suite());
		suite.addTest(AllUIToolsAppSwingTests.suite());
		
		suite.addTest(BufferedPropertyValueModelTests.suite());
		suite.addTest(CollectionAspectAdapterTests.suite());
		suite.addTest(CollectionListValueModelAdapterTests.suite());
		suite.addTest(CollectionPropertyValueModelAdapterTests.suite());
		suite.addTest(CompositeCollectionValueModelTests.suite());
		suite.addTest(ExtendedListValueModelWrapperTests.suite());
		suite.addTest(FilteringCollectionValueModelTests.suite());
		suite.addTest(FilteringPropertyValueModelTests.suite());
		suite.addTest(ItemCollectionListValueModelAdapterTests.suite());
		suite.addTest(ItemListListValueModelAdapterTests.suite());
		suite.addTest(ItemPropertyListValueModelAdapterTests.suite());
		suite.addTest(ItemStateListValueModelAdapterTests.suite());
		suite.addTest(ListAspectAdapterTests.suite());
		suite.addTest(ListCollectionValueModelAdapterTests.suite());
		suite.addTest(ListCuratorTests.suite());
		suite.addTest(NullCollectionValueModelTests.suite());
		suite.addTest(NullListValueModelTests.suite());
		suite.addTest(NullPropertyValueModelTests.suite());
		suite.addTest(PropertyAspectAdapterTests.suite());
		suite.addTest(PropertyCollectionValueModelAdapterTests.suite());
		suite.addTest(ReadOnlyCollectionValueModelTests.suite());
		suite.addTest(ReadOnlyListValueModelTests.suite());
		suite.addTest(SimpleCollectionValueModelTests.suite());
		suite.addTest(SimpleListValueModelTests.suite());
		suite.addTest(SimplePropertyValueModelTests.suite());
		suite.addTest(SortedListValueModelAdapterTests.suite());
		suite.addTest(StaticValueModelTests.suite());
		suite.addTest(TransformationListValueModelAdapterTests.suite());
		suite.addTest(TransformationPropertyValueModelTests.suite());
		suite.addTest(TreeAspectAdapterTests.suite());
		suite.addTest(ValueCollectionPropertyValueModelAdapterTests.suite());
		suite.addTest(ValueListPropertyValueModelAdapterTests.suite());
		suite.addTest(ValuePropertyPropertyValueModelAdapterTests.suite());
		suite.addTest(ValueStatePropertyValueModelAdapterTests.suite());
	
		return suite;
	}
	
	private AllUIToolsAppTests() {
		super();
	}
	
}
