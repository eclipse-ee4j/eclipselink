/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.automap;

import org.eclipse.persistence.tools.workbench.test.models.projects.EmployeeProject;

import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import junit.framework.TestCase;

public class AutomapTests extends TestCase
{
	public AutomapTests(String name)
	{
		super(name);
	}

	private void startTest(MWProject mwProject,
								  AutomapVerifier verifier) throws Exception
	{
		new AutomapProject(mwProject).startTest(verifier);
	}

	public void testEmployeeProject() throws Exception
	{
		startTest(new EmployeeProject().getProject(),
					 new EmployeeProjectVerifier());
	}
}