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
package org.eclipse.persistence.tools.workbench.test.scplugin.ui.project;

import javax.swing.JComponent;

import org.eclipse.persistence.tools.workbench.test.scplugin.ui.SCAbstractPanelTest;

import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.ShellWorkbenchContext;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SCAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.ui.project.ProjectNode;
import org.eclipse.persistence.tools.workbench.scplugin.ui.project.ProjectPropertiesPage;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;

/**
 * @version 10.0.3
 * @author Pascal Filion
 */
public class ProjectPropertiesPageTest extends SCAbstractPanelTest
{
	public ProjectPropertiesPageTest(String name)
	{
		super(name);
	}

	public static void main(String[] args) throws Exception
	{
		new ProjectPropertiesPageTest("ProjectPropertiesPageTest").execute(args);
	}

	protected void _testFocusTransferClasspath()
	{
//		testFocusTransferByMnemonic("CLASSPATH_LIST_PANEL_LABEL",
//											 COMPONENT_TREE);
	}

	protected void _testFocusTransferLocation() throws Exception
	{
		testFocusTransferByMnemonic("PROJECT_LOCATION_FIELD", COMPONENT_TEXT_FIELD);
	}

	protected void _testFocusTransferSessionsList()
	{
//		testFocusTransferByMnemonic("PROJECT_SESSIONS_LIST",
//											 COMPONENT_LIST);
	}

	protected PropertyValueModel buildNodeHolder(ApplicationNode projectNode)
	{
		return new SimplePropertyValueModel(projectNode);
	}

	protected JComponent buildPane() throws Exception
	{
		return new ProjectPropertiesPage(new ShellWorkbenchContext(((ProjectNode) getNodeHolder().getValue()).getApplicationContext()));
	}

	protected SCAdapter buildSelection()
	{
		return getTopLinkSessions();
	}

	protected void clearModel()
	{
	}

	protected void printModel()
	{
	}

	protected void resetProperty()
	{
	}

	protected void restoreModel()
	{
	}

	protected String windowTitle()
	{
		return "Test Project Page";
	}
}
