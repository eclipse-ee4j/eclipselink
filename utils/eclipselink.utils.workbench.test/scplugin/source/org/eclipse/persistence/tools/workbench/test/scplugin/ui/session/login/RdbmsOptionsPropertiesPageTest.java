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
package org.eclipse.persistence.tools.workbench.test.scplugin.ui.session.login;

import javax.swing.JComponent;

import org.eclipse.persistence.tools.workbench.test.scplugin.ui.session.basic.AbstractSessionPanelTest;

import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseLoginAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseSessionAdapter;


/**
 * @author Pascal Filion
 * @version 1.0a
 */
public class RdbmsOptionsPropertiesPageTest extends AbstractSessionPanelTest
{
	private int index;

	public RdbmsOptionsPropertiesPageTest(String name)
	{
		super(name);
	}

	public static void main(String[] args) throws Exception
	{
		new RdbmsOptionsPropertiesPageTest("RdbmsOptionsPropertiesPageTest").execute(args);
	}

	protected void _testComponentEntryTableQualifier() throws Exception
	{
		simulateMnemonic("LOGIN_TABLE_QUALIFIER_FIELD");
		simulateTextInput("Table Qualifier");

		DatabaseSessionAdapter session = (DatabaseSessionAdapter) getSession();
		DatabaseLoginAdapter login = (DatabaseLoginAdapter) session.getLogin();
		assertEquals("Table Qualifier", login.getTableQualifier());
	}

	protected void _testFocusTransferBatchWriting() throws Throwable
	{
		testFocusTransferByMnemonic("LOGIN_BATCH_WRITING_COMBO_BOX", COMPONENT_COMBO_BOX);
	}

	protected void _testFocusTransferByteArrayBinding() throws Throwable
	{
		testFocusTransferByMnemonic("LOGIN_BYTE_ARRAY_BINDING_CHECK_BOX", COMPONENT_CHECK_BOX);
	}

	protected void _testFocusTransferCacheAllStatements() throws Throwable
	{
		testFocusTransferByMnemonic("LOGIN_CACHE_ALL_STATEMENTS_CHECK_BOX", COMPONENT_CHECK_BOX);
	}

	protected void _testFocusTransferForceFieldNamesToUppercase() throws Throwable
	{
		testFocusTransferByMnemonic("LOGIN_FORCE_FIELD_NAMES_TO_UPPERCASE_CHECK_BOX", COMPONENT_CHECK_BOX);
	}

	protected void _testFocusTransferNativeSQL() throws Throwable
	{
		testFocusTransferByMnemonic("LOGIN_NATIVE_SQL_CHECK_BOX", COMPONENT_CHECK_BOX);
	}

	protected void _testFocusTransferOptimizeDataConversion() throws Throwable
	{
		testFocusTransferByMnemonic("LOGIN_OPTIMIZE_DATA_CONVERSION_CHECK_BOX", COMPONENT_CHECK_BOX);
	}

	protected void _testFocusTransferQueriesShouldBindAllParameters() throws Throwable
	{
		testFocusTransferByMnemonic("LOGIN_QUERIES_SHOULD_BIND_ALL_PARAMETERS_CHECK_BOX", COMPONENT_CHECK_BOX);
	}

	protected void _testFocusTransferStreamsForBinding() throws Throwable
	{
		testFocusTransferByMnemonic("LOGIN_STREAMS_FOR_BINDING_CHECK_BOX", COMPONENT_CHECK_BOX);
	}

	protected void _testFocusTransferStringBinding() throws Throwable
	{
		testFocusTransferByMnemonic("LOGIN_STRING_BINDING_CHECK_BOX", COMPONENT_CHECK_BOX);
	}

	protected void _testFocusTransferStringSize() throws Throwable
	{
		// Make sure the spinner is enabled
		DatabaseSessionAdapter session = (DatabaseSessionAdapter) getSelectionHolder().getValue();
		((DatabaseLoginAdapter) session.getLogin()).setStringBinding(true);

		testFocusTransferByMnemonic("LOGIN_STRING_SIZE_SPINNER", COMPONENT_SPINNER);
	}

	protected void _testFocusTransferTableQualifier() throws Throwable
	{
		testFocusTransferByMnemonic("LOGIN_TABLE_QUALIFIER_FIELD", COMPONENT_TEXT_FIELD);
	}

	protected void _testFocusTransferTrimStrings() throws Throwable
	{
		testFocusTransferByMnemonic("LOGIN_TRIM_STRING_CHECK_BOX", COMPONENT_CHECK_BOX);
	}

	protected JComponent buildPane() throws Exception
	{
		return buildPage("org.eclipse.persistence.tools.workbench.scplugin.ui.session.login.RdbmsOptionsPropertiesPage", getNodeHolder());
	}

	protected boolean canContinueTestingDuplicateMnemonic()
	{
		if (index == 0)
		{
			DatabaseSessionAdapter session = (DatabaseSessionAdapter) getSelectionHolder().getValue();
			((DatabaseLoginAdapter) session.getLogin()).setStringBinding(true);
		}

		return index++ == 0;
	}

	protected void printModel()
	{
	}

	protected void resetProperty()
	{
	}

	protected String windowTitle()
	{
		return "Testing Login Options Page";
	}
}
