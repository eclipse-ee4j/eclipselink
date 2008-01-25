/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.mappingsplugin.ui;

import org.eclipse.persistence.tools.workbench.test.framework.TestWorkbenchContext;
import org.eclipse.persistence.tools.workbench.test.models.projects.EmployeeOXProject;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsplugin.MappingsPluginIconResourceFileNameMap;
import org.eclipse.persistence.tools.workbench.mappingsplugin.MappingsPluginResourceBundle;
import org.eclipse.persistence.tools.workbench.mappingsplugin.NonDescriptorClassManagementDialog;

public class NonDescriptorClassManagementDialogTest
{
	private WorkbenchContext context;
	
	
	public static void main(String[] args) throws Exception {
		new NonDescriptorClassManagementDialogTest().exec(args);
	}
	
	
	public NonDescriptorClassManagementDialogTest() {
		super();
	}
	
	private void exec(String[] args) 
		throws Exception 
	{
		new NonDescriptorClassManagementDialog(this.buildWorkbenchContext(), this.buildProject()).show();
		System.exit(0);
	}
	
	private WorkbenchContext buildWorkbenchContext() {
		return new TestWorkbenchContext(MappingsPluginResourceBundle.class, MappingsPluginIconResourceFileNameMap.class.getName());
	}
	
	private MWProject buildProject() {
		return new EmployeeOXProject().getProject();
	}
}
