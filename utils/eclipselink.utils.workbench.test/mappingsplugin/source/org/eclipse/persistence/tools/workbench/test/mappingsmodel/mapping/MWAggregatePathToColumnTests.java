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
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.mapping;

import java.util.Collection;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWAggregatePathToColumn;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;

public class MWAggregatePathToColumnTests extends TestCase {

	public static Test suite() {
		return new TestSuite(MWAggregatePathToColumnTests.class);
	}
	
	public MWAggregatePathToColumnTests(String name) {
		super(name);
	}
	
	public static Collection associationsInPersonAddressMapping() {
		return CollectionTools.sort(MWAggregateMappingTests.personAddressMapping().pathsToFields());
	}

	public void testGetFullDescription() {
		MWAggregatePathToColumn association = (MWAggregatePathToColumn) associationsInPersonAddressMapping().iterator().next();
		String tersePathDescription = association.getPathDescription() + association.getAggregateRuntimeFieldNameGenerator().fieldNameForRuntime();
		assertEquals("Address.cityDIRECT", tersePathDescription);
	}
	
	public void testGetPathDescription() {
		MWAggregatePathToColumn association = (MWAggregatePathToColumn) associationsInPersonAddressMapping().iterator().next();
		String tersePathDescription = association.getPathDescription();
		assertEquals("Address.city", tersePathDescription);
	}
}
