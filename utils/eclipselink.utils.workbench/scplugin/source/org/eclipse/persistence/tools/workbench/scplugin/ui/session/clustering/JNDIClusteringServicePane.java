/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.scplugin.ui.session.clustering;

// JDK
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JComponent;
import javax.swing.text.Document;

import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.JNDIClusteringServiceAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;

// Mapping Workbench

/**
 * This page shows the information about the {@link JNDIClusteringServiceAdapter}.
 * <p>
 * Here the layout:</pre>
 * ________________________________________________________________
 * |                                         ___________________  |
 * | JNDI Username:                          | I               |  |
 * |                                         ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯  |
 * |                                         ___________________  |
 * | JNDI Password:                          | I               |  |
 * |                                         ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯  |
 * |                                         ___________________  |
 * | Naming Service Initial Context Factory: | I               |  |
 * |                                         ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯  |
 * ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯</pre>
 *
 * Known container of this pane:<br>
 * - {@link CacheSynchronizationManagerPane}
 *
 * @see JNDIClusteringServiceAdapter
 *
 * @version 10.1.3
 * @author Pascal Filion
 */
final class JNDIClusteringServicePane extends AbstractSubjectPanel
{
	/**
	 * Creates a new <code>JNDIClusteringServicePane</code>.
	 *
	 * @param subjectHolder The holder of {@link JNDIClusteringServiceAdapter}
	 * @param context
	 */
	JNDIClusteringServicePane(ValueModel subjectHolder,
									  ApplicationContext context)
	{
		super(subjectHolder, context);
	}

	/**
	 * Creates the <code>Document</code> that keeps the value from the text
	 * field in sync with the JNDI Password value in the model and vice versa.
	 *
	 * @return A new <code>Document</code>
	 */
	private Document buildJNDIPasswordDocumentAdapter()
	{
		return new DocumentAdapter(buildJNDIPasswordHolder());
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * JNDI Password property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildJNDIPasswordHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), JNDIClusteringServiceAdapter.JNDI_PASSWORD_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				JNDIClusteringServiceAdapter csm = (JNDIClusteringServiceAdapter) subject;
				return csm.getJNDIPassword();
			}

			protected void setValueOnSubject(Object value)
			{
				JNDIClusteringServiceAdapter csm = (JNDIClusteringServiceAdapter) subject;
				csm.setJNDIPassword((String) value);
			}
		};
	}

	/**
	 * Creates the <code>Document</code> that keeps the value from the text
	 * field in sync with the JNDI Username value in the model and vice versa.
	 *
	 * @return A new <code>Document</code>
	 */
	private Document buildJNDIUsernameDocumentAdapter()
	{
		return new DocumentAdapter(buildJNDIUsernameHolder());
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * JNDI Username property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildJNDIUsernameHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), JNDIClusteringServiceAdapter.JNDI_USER_NAME_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				JNDIClusteringServiceAdapter csm = (JNDIClusteringServiceAdapter) subject;
				return csm.getJNDIUsername();
			}

			protected void setValueOnSubject(Object value)
			{
				JNDIClusteringServiceAdapter csm = (JNDIClusteringServiceAdapter) subject;
				csm.setJNDIUsername((String) value);
			}
		};
	}

	/**
	 * Creates the <code>Document</code> that keeps the value from the text
	 * field in sync with the Naming Service Initial Context Factory Name value
	 * in the model and vice versa.
	 *
	 * @return A new <code>Document</code>
	 */
	private Document buildNamingServiceInitialContextFactoryNameDocumentAdapter()
	{
		return new DocumentAdapter(buildNamingServiceInitialContextFactoryNameHolder());
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Naming Service Initial Context Factory Name property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildNamingServiceInitialContextFactoryNameHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), JNDIClusteringServiceAdapter.NAMING_SERVICE_INITIAL_CONTEXT_FACTORY_NAME_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				JNDIClusteringServiceAdapter csm = (JNDIClusteringServiceAdapter) subject;
				return csm.getNamingServiceInitialContextFactoryName();
			}

			protected void setValueOnSubject(Object value)
			{
				JNDIClusteringServiceAdapter csm = (JNDIClusteringServiceAdapter) subject;
				csm.setNamingServiceInitialContextFactoryName((String) value);
			}
		};
	}

	/**
	 * Initializes the layout of this pane.
	 */
	protected void initializeLayout()
	{
		GridBagConstraints constraints = new GridBagConstraints();

		// Clustering Service pane
		ClusteringServicePane pane = new ClusteringServicePane((PropertyValueModel) getSubjectHolder(), getApplicationContext());

		constraints.gridx       = 0;
		constraints.gridy       = 0;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(0, 0, 0, 0);

		add(pane, constraints);
		addPaneForAlignment(pane);

		// JNDI Username widgets
		JComponent jndiUsernameWidgets = buildLabeledTextField
		(
			"CSM_JNDI_USERNAME",
			buildJNDIUsernameDocumentAdapter()
		);

		constraints.gridx       = 0;
		constraints.gridy       = 1;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(5, 0, 0, 0);

		add(jndiUsernameWidgets, constraints);
		addHelpTopicId(jndiUsernameWidgets, "session.clustering.csm.jndiUsername");

		// JNDI Password widgets
		JComponent jndiPasswordWidgets = buildLabeledTextField
		(
			"CSM_JNDI_PASSWORD",
			buildJNDIPasswordDocumentAdapter()
		);

		constraints.gridx       = 0;
		constraints.gridy       = 2;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(5, 0, 0, 0);

		add(jndiPasswordWidgets, constraints);
		addHelpTopicId(jndiPasswordWidgets, "session.clustering.csm.jndiPassword");

		// Naming Service Initial Context Factory Name widgets
		JComponent namingServiceInitialContextFactoryNameWidgets = buildLabeledTextField
		(
			"CSM_NAMING_SERVICE_INITIAL_CONTEXT_FACTORY_NAME",
			buildNamingServiceInitialContextFactoryNameDocumentAdapter()
		);

		constraints.gridx       = 0;
		constraints.gridy       = 3;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(5, 0, 0, 0);

		add(namingServiceInitialContextFactoryNameWidgets, constraints);
		addHelpTopicId(namingServiceInitialContextFactoryNameWidgets, "session.clustering.csm.namingServiceInitialContextFactory");
	}
}