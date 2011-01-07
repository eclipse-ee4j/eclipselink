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
package org.eclipse.persistence.tools.workbench.test.scplugin.model.read;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.internal.sessions.factories.XMLSessionConfigProject;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseSessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.LoginAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.TopLinkSessionsAdapter;
import org.eclipse.persistence.tools.workbench.test.scplugin.AllSCTests;


public class SequenceTest extends TestCase
{
	public SequenceTest(String name)
	{
		super(name);
	}

	public static Test suite()
	{
		return new TestSuite(SequenceTest.class, "Sequence Tests");
	}

	private LoginAdapter loadTopLinkSessions(String path) throws Exception
	{
		TopLinkSessionsAdapter topLinkSessions = AllSCTests.loadSessions(path, getClass());
		DatabaseSessionAdapter session = (DatabaseSessionAdapter) topLinkSessions.sessionNamed("Session");
		assertNotNull(session);

		LoginAdapter login = session.getLogin();
		assertNotNull(login);

		return login;
	}

	public void test_DefaultSequence_DefaultSequence() throws Exception
	{
		LoginAdapter login = loadTopLinkSessions("/sequence/DefaultSequence_DefaultSequence.xml");

		assertFalse(login.sequencingIsDefault());
		assertFalse(login.sequencingIsNative());
		assertFalse(login.sequencingIsCustom());
		assertTrue (login.sequencesSize() == 0);
		assertTrue (login.getSequencePreallocationSize() == 20);
		assertTrue(login.getSequenceTable().equals( XMLSessionConfigProject.SEQUENCE_TABLE_DEFAULT));
		assertTrue(login.getSequenceNameField().equals( XMLSessionConfigProject.SEQUENCE_NAME_FIELD_DEFAULT));
		assertTrue(login.getSequenceCounterField().equals( XMLSessionConfigProject.SEQUENCE_COUNTER_FIELD_DEFAULT));
		assertNotNull(login.getDefaultSequence());
	}

	public void test_DefaultSequence_MultipleSequence() throws Exception
	{
		LoginAdapter login = loadTopLinkSessions("/sequence/DefaultSequence_MultipleSequence.xml");

		assertFalse(login.sequencingIsDefault());
		assertTrue(login.sequencingIsNative());
		assertFalse(login.sequencingIsCustom());
		assertTrue(login.sequencesSize() == 2);
		assertTrue(login.getSequencePreallocationSize() == 10);
		assertNull(login.getSequenceTable());
		assertNull(login.getSequenceNameField());
		assertNull(login.getSequenceCounterField());
		assertNotNull(login.getDefaultSequence());
	}

	public void test_DefaultSequence_NativeSequence() throws Exception
	{
		LoginAdapter login = loadTopLinkSessions("/sequence/DefaultSequence_NativeSequence.xml");

		assertFalse(login.sequencingIsDefault());
		assertTrue(login.sequencingIsNative());
		assertFalse(login.sequencingIsCustom());
		assertTrue(login.sequencesSize() == 0);
		assertTrue(login.getSequencePreallocationSize() == 30);
		assertNull(login.getSequenceTable());
		assertNull(login.getSequenceNameField());
		assertNull(login.getSequenceCounterField());
		assertNotNull(login.getDefaultSequence());
	}

	public void test_DefaultSequence_TableSequence() throws Exception
	{
		LoginAdapter login = loadTopLinkSessions("/sequence/DefaultSequence_TableSequence.xml");

		assertFalse(login.sequencingIsDefault());
		assertFalse(login.sequencingIsNative());
		assertFalse(login.sequencingIsCustom());
		assertTrue(login.sequencesSize() == 0);
		assertTrue(login.getSequencePreallocationSize() == 2);
		assertEquals(login.getSequenceTable(),        "MY_SEQUENCE");
		assertEquals(login.getSequenceNameField(),    "MY_SEQ_NAME");
		assertEquals(login.getSequenceCounterField(), "MY_SEQ_COUNT");
		assertNotNull(login.getDefaultSequence());
	}

	public void test_DefaultSequence_UnaryTableSequence() throws Exception
	{
		LoginAdapter login = loadTopLinkSessions("/sequence/DefaultSequence_UnaryTableSequence.xml");
		assertFalse(login.sequencingIsDefault());
		assertFalse(login.sequencingIsNative());
		assertFalse(login.sequencingIsCustom());
		assertTrue(login.sequencesSize() == 1);
		assertTrue(login.getSequencePreallocationSize() == 25);
		assertNotNull(login.getDefaultSequence());
		assertTrue(login.getSequenceTable().equals( XMLSessionConfigProject.SEQUENCE_TABLE_DEFAULT));
		assertTrue(login.getSequenceNameField().equals( XMLSessionConfigProject.SEQUENCE_NAME_FIELD_DEFAULT));
		assertTrue(login.getSequenceCounterField().equals( XMLSessionConfigProject.SEQUENCE_COUNTER_FIELD_DEFAULT));
	}

	public void test_NoDefaultSequence_MultipleSequence() throws Exception
	{
		LoginAdapter login = loadTopLinkSessions("/sequence/NoDefaultSequence_MultipleSequence.xml");

		assertTrue (login.sequencingIsDefault()); // Should be false but not for now
		assertFalse(login.sequencingIsNative());
		assertFalse(login.sequencingIsCustom());
		assertTrue (login.sequencesSize() == 2);
		assertTrue (login.getSequencePreallocationSize() == XMLSessionConfigProject.SEQUENCE_PREALLOCATION_SIZE_DEFAULT);
		assertNotNull(login.getDefaultSequence()); // Should be null but not for now
		assertNull (login.getSequenceTable());
		assertNull (login.getSequenceNameField());
		assertNull (login.getSequenceCounterField());
	}

	public void test_NoSequence() throws Exception
	{
		LoginAdapter login = loadTopLinkSessions("/sequence/NoSequence.xml");

		assertTrue (login.sequencingIsDefault()); // Should be false but not for now
		assertFalse(login.sequencingIsNative());
		assertFalse(login.sequencingIsCustom());
		assertTrue (login.sequencesSize() == 0);
		assertTrue (login.getSequencePreallocationSize() == XMLSessionConfigProject.SEQUENCE_PREALLOCATION_SIZE_DEFAULT);
		assertNotNull(login.getDefaultSequence()); // Should be null for not for now
		assertNull (login.getSequenceTable());
		assertNull (login.getSequenceNameField());
		assertNull (login.getSequenceCounterField());
	}

	public void test_UnaryTableSequence() throws Exception
	{
		LoginAdapter login = loadTopLinkSessions("/sequence/UnaryTableSequence.xml");

		assertTrue (login.sequencingIsDefault()); // Should be false but not for now
		assertFalse(login.sequencingIsNative());
		assertFalse(login.sequencingIsCustom());
		assertTrue (login.sequencesSize() == 1);
		assertTrue (login.getSequencePreallocationSize() == XMLSessionConfigProject.SEQUENCE_PREALLOCATION_SIZE_DEFAULT);
		assertNotNull(login.getDefaultSequence()); // Should be null but not for now
		assertNull (login.getSequenceTable());
		assertNull (login.getSequenceNameField());
		assertNull (login.getSequenceCounterField());
	}
}
