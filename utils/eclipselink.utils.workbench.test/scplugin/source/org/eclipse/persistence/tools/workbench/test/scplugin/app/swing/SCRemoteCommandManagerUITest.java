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
package org.eclipse.persistence.tools.workbench.test.scplugin.app.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseSessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.RemoteCommandManagerAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;

public class SCRemoteCommandManagerUITest extends SCAbstractUITest
{
	private CheckBoxModelAdapter checkBoxModelAdapter;
	private DocumentAdapter documentAdapter;
	private RemoteCommandManagerAdapter subject;
	private PropertyValueModel subjectHolder;

	public static void main(String[] args) throws Exception
	{
		new SCRemoteCommandManagerUITest().exec(args);
	}

	private DocumentAdapter buildChannelDocumentAdapter()
	{
		return new DocumentAdapter(buildChannelHolder());
	}

	private PropertyValueModel buildChannelHolder()
	{
		return new PropertyAspectAdapter(this.subjectHolder, RemoteCommandManagerAdapter.CHANNEL_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				RemoteCommandManagerAdapter manager = (RemoteCommandManagerAdapter) subject;
				return manager.getChannel();
			}

			protected void setValueOnSubject(Object value)
			{
				RemoteCommandManagerAdapter manager = (RemoteCommandManagerAdapter) subject;
				manager.setChannel((String) value);
			}
		};
	}

	protected Component buildPropertyTestingPanel()
	{
		JCheckBox checkBox = new JCheckBox("Cache Synchronization");
		checkBox.setMnemonic('C');
		checkBox.setModel(this.checkBoxModelAdapter);

		JTextField textField = new JTextField();
		textField.setDocument(this.documentAdapter);

		JPanel panel = new JPanel(new BorderLayout(0, 5));
		panel.add(checkBox, BorderLayout.NORTH);
		panel.add(textField, BorderLayout.CENTER);

		return panel;
	}

	protected void clearModel()
	{
		this.subjectHolder.setValue(null);
	}

	private void exec(String[] args) throws Exception
	{
		setUp();
		openWindow();
	}

	protected void printModel()
	{
		System.out.println(subject());
	}

	protected void resetProperty()
	{
	}

	protected void restoreModel()
	{
		this.subjectHolder.setValue(subject());
	}

	protected void setUp()
	{
		super.setUp();

		// Load sessions.xml
		DatabaseSessionAdapter session = (DatabaseSessionAdapter) getTopLinkSessions().sessionNamed("SC-EmployeeTest");

		// Retrieve our subject
		this.subject = session.getRemoteCommandManager();
		this.subjectHolder = new SimplePropertyValueModel(this.subject);

		// Create the adapters and holders
		this.documentAdapter = buildChannelDocumentAdapter();
	}

	protected Object subject()
	{
		return this.subject;
	}

	protected PropertyValueModel subjectHolder()
	{
		return this.subjectHolder;
	}

	protected String windowTitle()
	{
		return "RemoteCommandManager";
	}
}
