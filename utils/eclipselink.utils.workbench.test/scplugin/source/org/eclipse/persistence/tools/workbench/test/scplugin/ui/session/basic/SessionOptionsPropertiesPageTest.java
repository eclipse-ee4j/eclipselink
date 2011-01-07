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
package org.eclipse.persistence.tools.workbench.test.scplugin.ui.session.basic;

import javax.swing.JComponent;

/**
 * @author Pascal Filion
 * @version 1.0a
 */
public class SessionOptionsPropertiesPageTest extends AbstractSessionPanelTest
{
	public SessionOptionsPropertiesPageTest(String name)
	{
		super(name);
	}

	public static void main(String[] args) throws Exception
	{
		new SessionOptionsPropertiesPageTest("SessionOptionsPropertiesPage").execute(args);
	}

	protected void _testComponentEntryExceptionHandler() throws Exception
	{
		simulateMnemonic("OPTIONS_EXCEPTION_HANDLER_FIELD");
		simulateTextInput("oracle.my.exception");

		assertEquals("oracle.my.exception", getSession().getExceptionHandlerClass());
	}

	protected void _testComponentEntryProfiler() throws Exception
	{
		simulateMnemonic("OPTIONS_PROFILER_COMBO_BOX");
		simulateComboBoxSelectionByRenderer(getResourceRepository().getString("OPTIONS_PROFILER_DMS_CHOICE"));

		assertEquals("dms", getSession().getProfiler());
	}

	protected void _testComponentEntrySessionCustomizerClass() throws Exception
	{
		simulateMnemonic("OPTIONS_SESSION_CUSTOMIZER_CLASS_FIELD");
		simulateTextInput("session.customizer.class");

		assertEquals("session.customizer.class", getSession().getSessionCustomizerClass());
	}

	protected void _testFocusTransferEventListeners()
	{
		// TODO: For some reason, the focus is not properly transfered
//		testFocusTransferByMnemonic(getResourceRepository().getMnemonic("OPTIONS_EVENT_LISTENERS_LIST"),
//			 								 getResourceRepository().getString("OPTIONS_EVENT_LISTENERS_LIST"),
//											 COMPONENT_LIST);
	}

	protected void _testFocusTransferExceptionHandler() throws Exception
	{
		testFocusTransferByMnemonic("OPTIONS_EXCEPTION_HANDLER_FIELD",
											 COMPONENT_TEXT_FIELD);
	}

	protected void _testFocusTransferExternalTransactionController() throws Exception
	{
		testFocusTransferByMnemonic("OPTIONS_EXTERNAL_TRANSACTION_CONTROLLER_FIELD",
											 COMPONENT_TEXT_FIELD);
	}

	protected void _testFocusTransferProfiler() throws Exception
	{
		testFocusTransferByMnemonic("OPTIONS_PROFILER_COMBO_BOX",
											 COMPONENT_COMBO_BOX);
	}

	protected void _testFocusTransferSessionCustomizerClass() throws Exception
	{
		testFocusTransferByMnemonic("OPTIONS_SESSION_CUSTOMIZER_CLASS_FIELD",
											 COMPONENT_TEXT_FIELD);
	}

	protected JComponent buildPane() throws Exception
	{
		return buildPage("org.eclipse.persistence.tools.workbench.scplugin.ui.session.basic.SessionOptionsPropertiesPage", getNodeHolder());
	}

	protected void printModel()
	{
	}

	protected void resetProperty()
	{
	}

	protected String windowTitle()
	{
		return "Testing Session Options Page";
	}
}
