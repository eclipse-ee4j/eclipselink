/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.query;


import org.eclipse.persistence.tools.workbench.test.mappingsmodel.ModelProblemsTestCase;

import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWUserDefinedQueryKey;


import junit.framework.Test;
import junit.framework.TestSuite;

public class MWUserDefinedQueryKeyTests extends ModelProblemsTestCase {

	public static void main(String[] args) {
		junit.swingui.TestRunner.main(new String[] {"-c", MWUserDefinedQueryKeyTests.class.getName()});
	}
	
	public static Test suite() {
		return new TestSuite(MWUserDefinedQueryKeyTests.class);
	}

	/**
	 * Constructor for MWQueryableTests.
	 * @param name
	 */
	public MWUserDefinedQueryKeyTests(String name){
		super(name);
	}
	public void testFieldExistsProblem() {

		String problem = ProblemConstants.DESCRIPTOR_QUERY_KEY_NO_COLUMN_SPECIFIED;

		MWUserDefinedQueryKey qKey = getPersonDescriptor().addQueryKey("qKey", (MWColumn)getPersonDescriptor().getPrimaryTable().columns().next());
		
		assertTrue("The query key should not have the problem: " + problem, !hasProblem(problem, getPersonDescriptor()));
		
		qKey.setColumn(null);
		
		assertTrue("The query key should have the problem: " + problem, hasProblem(problem, getPersonDescriptor()));
	}
}
