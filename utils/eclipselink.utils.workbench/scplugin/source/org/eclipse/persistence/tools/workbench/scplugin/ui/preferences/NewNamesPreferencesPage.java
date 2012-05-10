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
package org.eclipse.persistence.tools.workbench.scplugin.ui.preferences;

// JDK
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.Document;

import org.eclipse.persistence.tools.workbench.framework.context.PreferencesContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.scplugin.SCPlugin;
import org.eclipse.persistence.tools.workbench.scplugin.model.meta.SCSessionsPropertiesManager;
import org.eclipse.persistence.tools.workbench.uitools.app.BufferedPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.adapters.PreferencePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;

// Mapping Workbench

/**
 * Preferences page for general (non-plug-in-specific) settings used by
 * the Sessions Configuration.
 *
 * @author Pascal Filion
 * @version 10.1.3
 */
final class NewNamesPreferencesPage extends AbstractPanel
{
	/**
	 * Creates a new <code>SCPreferencesPage</code>.
	 *
	 * @param context
	 */
	NewNamesPreferencesPage(PreferencesContext context)
	{
		super(new BorderLayout(), context);
		intializeLayout();
		addHelpTopicId(this, "preferences.sessions.newNames");
	}

	private JComponent buildDefaultNewNamesPane()
	{
		GridBagConstraints constraints = new GridBagConstraints();

		JPanel container = new JPanel(new GridBagLayout());
		container.setBorder(BorderFactory.createCompoundBorder
		(
			buildTitledBorder("PREFERENCES_DEFAULT_NEW_NAME_GROUP_BOX"),
			BorderFactory.createEmptyBorder(0, 5, 5, 5)
		));

		// Session Configuration
		JComponent sessionsXmlWidgets = buildLabeledTextField
		(
			"PREFERENCES_DEFAULT_NEW_NAME_TOPLINK_CONFIGURATION_FIELD",
			buildNewNameTopLinkConfigurationDocumentAdapter(),
			new JLabel(".xml")
		);

		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(0, 0, 0, 0);

		container.add(sessionsXmlWidgets, constraints);

		// Session
		JComponent sessionWidgets = buildLabeledTextField
		(
			"PREFERENCES_DEFAULT_NEW_NAME_SESSION_FIELD",
			buildNewNameSessionDocumentAdapter()
		);

		constraints.gridx      = 0;
		constraints.gridy      = 1;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(5, 0, 0, 0);

		container.add(sessionWidgets, constraints);

		// Broker
		JComponent brokerWidgets = buildLabeledTextField
		(
			"PREFERENCES_DEFAULT_NEW_NAME_BROKER_FIELD",
			buildNewNameBrokerDocumentAdapter()
		);

		constraints.gridx      = 0;
		constraints.gridy      = 2;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(5, 0, 0, 0);

		container.add(brokerWidgets, constraints);

		// Connection Pool
		JComponent poolWidgets = buildLabeledTextField
		(
			"PREFERENCES_DEFAULT_NEW_NAME_POOL_FIELD",
			buildNewNamePoolDocumentAdapter()
		);

		constraints.gridx      = 0;
		constraints.gridy      = 3;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(5, 0, 0, 0);

		container.add(poolWidgets, constraints);

		return container;
	}

	private Document buildNewNameBrokerDocumentAdapter()
	{
		return new DocumentAdapter(buildNewNameBrokerHolder());
	}

	private PropertyValueModel buildNewNameBrokerHolder()
	{
		return new BufferedPropertyValueModel
		(
			buildNewNameBrokerHolderImp(),
			getPreferencesContext().getBufferTrigger()
		);
	}

	private PropertyValueModel buildNewNameBrokerHolderImp()
	{
		return new PreferencePropertyValueModel
		(
			preferences(),
			SCPlugin.NEW_NAME_BROKER_PREFERENCE,
			resourceRepository().getString("BROKER_CREATION_DIALOG_NEW_NAME")
		);
	}

	private Document buildNewNamePoolDocumentAdapter()
	{
		return new DocumentAdapter(buildNewNamePoolHolder());
	}

	private PropertyValueModel buildNewNamePoolHolder()
	{
		return new BufferedPropertyValueModel
		(
			buildNewNamePoolHolderImp(),
			getPreferencesContext().getBufferTrigger()
		);
	}

	private PropertyValueModel buildNewNamePoolHolderImp()
	{
		return new PreferencePropertyValueModel
		(
			preferences(),
			SCPlugin.NEW_NAME_POOL_PREFERENCE,
			resourceRepository().getString("POOL_CREATION_DIALOG_NEW_POOL_NAME")
		);
	}

	private Document buildNewNameSessionDocumentAdapter()
	{
		return new DocumentAdapter(buildNewNameSessionHolder());
	}

	private PropertyValueModel buildNewNameSessionHolder()
	{
		return new BufferedPropertyValueModel
		(
			buildNewNameSessionHolderImp(),
			getPreferencesContext().getBufferTrigger()
		);
	}

	private PropertyValueModel buildNewNameSessionHolderImp()
	{
		return new PreferencePropertyValueModel
		(
			preferences(),
			SCPlugin.NEW_NAME_SESSION_PREFERENCE,
			resourceRepository().getString("SESSION_CREATION_DIALOG_NEW_SESSION_NAME")
		);
	}

	private Document buildNewNameTopLinkConfigurationDocumentAdapter()
	{
		return new DocumentAdapter(buildNewNameTopLinkConfigurationHolder());
	}

	private PropertyValueModel buildNewNameTopLinkConfigurationHolder()
	{
		return new BufferedPropertyValueModel
		(
			buildNewNameTopLinkConfigurationHolderImp(),
			getPreferencesContext().getBufferTrigger()
		);
	}

	private PropertyValueModel buildNewNameTopLinkConfigurationHolderImp()
	{
		return new PreferencePropertyValueModel
		(
			preferences(),
			SCPlugin.NEW_NAME_SESSIONS_CONFIGURATION_PREFERENCE,
			SCSessionsPropertiesManager.UNTITLED_FILE_NAME
		);
	}

	private void intializeLayout()
	{
		GridBagConstraints constraints = new GridBagConstraints();

		JPanel container = new JPanel(new GridBagLayout());
		JScrollPane scrollPane = new JScrollPane(container);
		scrollPane.getVerticalScrollBar().setBlockIncrement(10);
		scrollPane.setBorder(null);
		scrollPane.setViewportBorder(null);
		add(scrollPane, BorderLayout.CENTER);

		// Default New Names
		JComponent defaultNames = buildDefaultNewNamesPane();

		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 1;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.PAGE_START;
		constraints.insets     = new Insets(5, 5, 5, 5);

		container.add(defaultNames, constraints);
	}
}
