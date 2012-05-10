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
package org.eclipse.persistence.tools.workbench.test.scplugin.ui.session.login;

// JDK
import javax.swing.JComponent;

import org.eclipse.persistence.tools.workbench.test.scplugin.ui.session.basic.AbstractSessionPanelTest;

// Mapping Workbench
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseLoginAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseSessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.ui.session.login.SequencingPropertiesPage;

// Mapping Workbench Test

/**
 * Tests for {@link org.eclipse.persistence.tools.workbench.scplugin.ui.session.login.SequencingPropertiesPage}.
 *
 * @author Pascal Filion
 * @version 10.0.3
 */
public class RdbmsSequencingPropertiesPageTest extends AbstractSessionPanelTest
{
	public RdbmsSequencingPropertiesPageTest(String name)
	{
		super(name);
	}

	public static void main(String[] args) throws Exception
	{
		new RdbmsSequencingPropertiesPageTest("RdbmsSequencingPropertiesPage").execute(args);
	}

	protected void _testComponentEntryCounterField() throws Exception
	{
		simulateMnemonic("LOGIN_COUNTER_FIELD_FIELD");
		simulateTextInput("MY_COUNTER_FIELD");

		assertEquals("MY_COUNTER_FIELD", getLogin().getSequenceCounterField());
	}

	protected void _testComponentEntryNameField() throws Exception
	{
		simulateMnemonic("LOGIN_NAME_FIELD_FIELD");
		simulateTextInput("MY_NAME_FIELD");

		assertEquals("MY_NAME_FIELD", getLogin().getSequenceNameField());
	}

	protected void _testComponentEntryPreallocationSize() throws Exception
	{

		simulateMnemonic("LOGIN_PREALLOCATION_SIZE_SPINNER");
		simulateSpinnerInput(123);

		assertEquals(123, getLogin().getSequencePreallocationSize());
	}

	protected void _testComponentEntryTable() throws Exception
	{
		simulateMnemonic("LOGIN_TABLE_FIELD");
		simulateTextInput("MY_TABLE");

		assertEquals("MY_TABLE", getLogin().getSequenceTable());
	}

	protected void _testFocusTransferCounterField() throws Exception
	{
		testFocusTransferByMnemonic("LOGIN_COUNTER_FIELD_FIELD", COMPONENT_TEXT_FIELD);
	}

	protected void _testFocusTransferCustomSequenceTable() throws Exception
	{
		testFocusTransferByMnemonic("LOGIN_CUSTOM_SEQUENCE_TABLE_RADIO_BUTTON", COMPONENT_RADIO_BUTTON);
	}

	protected void _testFocusTransferDefaultSequenceTable() throws Exception
	{
		testFocusTransferByMnemonic("LOGIN_DEFAULT_SEQUENCE_TABLE_RADIO_BUTTON", COMPONENT_RADIO_BUTTON);
	}

	protected void _testFocusTransferNameField() throws Exception
	{
		testFocusTransferByMnemonic("LOGIN_NAME_FIELD_FIELD", COMPONENT_TEXT_FIELD);
	}

	protected void _testFocusTransferNativeSequencing() throws Exception
	{
		testFocusTransferByMnemonic("LOGIN_NATIVE_SEQUENCING_RADIO_BUTTON", COMPONENT_RADIO_BUTTON);
	}

	protected void _testFocusTransferPreallocationSize() throws Exception
	{
		testFocusTransferByMnemonic("LOGIN_PREALLOCATION_SIZE_SPINNER", COMPONENT_SPINNER);
	}

	protected void _testFocusTransferTable() throws Exception
	{
		testFocusTransferByMnemonic("LOGIN_TABLE_FIELD", COMPONENT_TEXT_FIELD);
	}

	public void _textExtraDefaultValuesForCustomSequenceTable()
	{
		setDefaultSequenceTable();

		String entry = retrieveFieldEntry("LOGIN_COUNTER_FIELD_FIELD");
		assertEquals("SEQ_COUNT", entry);

		entry = retrieveFieldEntry("LOGIN_NAME_FIELD_FIELD");
		assertEquals("SEQ_NAME", entry);

		entry = retrieveFieldEntry("LOGIN_TABLE_FIELD");
		assertEquals("SEQUENCE", entry);
	}

	protected JComponent buildPane() throws Exception
	{
		return buildPage(SequencingPropertiesPage.class, getNodeHolder());
	}

	private DatabaseLoginAdapter getLogin()
	{
		DatabaseSessionAdapter session = (DatabaseSessionAdapter) getSession();
		return (DatabaseLoginAdapter) session.getLogin();
	}

	protected void printModel()
	{
	}

	protected void resetProperty()
	{
	}

	private void setDefaultSequenceTable()
	{
		getLogin().setDefaultTableSequenceTable();
	}

	protected String windowTitle()
	{
		return "Testing Login Sequencing Page";
	}
}
