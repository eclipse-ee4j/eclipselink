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
package org.eclipse.persistence.tools.workbench.scplugin.ui.session.clustering;

// JDK
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.text.Document;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractDialog;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.JNDINamingService;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SCAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.ui.tools.ClassChooserTools;
import org.eclipse.persistence.tools.workbench.scplugin.ui.tools.PropertyPane;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;

// Mapping Workbench

/**
 * This pane shows the information for <code>TransportManagerAdapter</code> that
 * are implementing {@link JNDINamingService}.
 * <p>
 * Here the layout:<pre>
 * _____________________________________________________________
 * |                          ____________________             |
 * | URL:                     | I                |             |
 * |                          --------------------             |
 * |                          ____________________             |
 * | Username:                | I                |             |
 * |                          --------------------             |
 * |                          ____________________             |
 * | Password:                | I                |             |
 * |                          --------------------             |
 * |                          ____________________ ___________ |
 * | Initial Context Factory: | I                | |Browse...| |
 * |                          -------------------- ----------- |
 * | ______________                                            |
 * | | Properties |                                            |
 * | --------------                                            |
 * -------------------------------------------------------------</pre>
 *
 * Known containers of this pane:<br>
 * {@link JMSCluteringPane}<br>
 * {@link RCMJMSPane}<br>
 * {@link RCMRMIPane}
 *
 * @see JNDINamingService
 *
 * @version 10.1.3
 * @author Pascal Filion
 */
class JNDINamingServicePane extends AbstractSubjectPanel
{
	/**
	 * Creates a new <code>JNDINamingServicePane</code>.
	 *
	 * @param subjectHolder The holder of {@link JNDINamingService}
	 * @param context
	 */
	JNDINamingServicePane(ValueModel subjectHolder,
											WorkbenchContextHolder contextHolder)
	{
		super(subjectHolder, contextHolder);
		addHelpTopicId(this, "session.clustering.jndiNamingService");
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to return the
	 * Class Repository.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private ValueModel buildClassRepositoryHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder())
		{
			protected Object getValueFromSubject()
			{
				SCAdapter adapter = (SCAdapter) subject;
				return adapter.getClassRepository();
			}
		};
	}

	/**
	 * Creates the <code>DocumentAdapter</code> that keeps the value from the
	 * text field in sync with the URL value in the model and vice
	 * versa.
	 *
	 * @return A new <code>DocumentAdapter</code>
	 */
	private Document buildInitialContextFactoryAdapter()
	{
		return new DocumentAdapter(buildInitialContextFactoryHolder());
	}

	/**
	 * Creates a Browse button that will take care to show the class chooser.
	 *
	 * @return A new <code>JButton</code>
	 */
	private JButton buildInitialContextFactoryBrowseButton()
	{
		return ClassChooserTools.buildBrowseButton
		(
			getWorkbenchContextHolder(),
			"JNDI_INITIAL_CONTEXT_FACTORY_BROWSE_BUTTON",
			buildClassRepositoryHolder(),
			buildInitialContextFactoryHolder()
		);
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * URL property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildInitialContextFactoryHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), JNDINamingService.INITIAL_CONTEXT_FACTORY_NAME_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				JNDINamingService adapter = (JNDINamingService) subject;
				return adapter.getInitialContextFactoryName();
			}

			protected void setValueOnSubject(Object value)
			{
				JNDINamingService adapter = (JNDINamingService) subject;
				adapter.setInitialContextFactoryName((String) value);
			}
		};
	}

	/**
	 * Creates the <code>DocumentAdapter</code> that keeps the value from the
	 * text field in sync with the Password value in the model and vice
	 * versa.
	 *
	 * @return A new <code>DocumentAdapter</code>
	 */
	private Document buildPasswordDocumentAdapter()
	{
		return new DocumentAdapter(buildPasswordHolder());
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Password property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildPasswordHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), JNDINamingService.JNDI_PASSWORD_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				JNDINamingService adapter = (JNDINamingService) subject;
				return adapter.getPassword();
			}

			protected void setValueOnSubject(Object value)
			{
				JNDINamingService adapter = (JNDINamingService) subject;
				adapter.setPassword((String) value);
			}
		};
	}

	/**
	 * Creates the <code>DocumentAdapter</code> that keeps the value from the
	 * text field in sync with the URL value in the model and vice
	 * versa.
	 *
	 * @return A new <code>DocumentAdapter</code>
	 */
	private Document buildURLDocumentAdapter()
	{
		return new DocumentAdapter(buildURLHolder());
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * URL property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildURLHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), JNDINamingService.JNDI_URL_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				JNDINamingService adapter = (JNDINamingService) subject;
				return adapter.getURL();
			}

			protected void setValueOnSubject(Object value)
			{
				JNDINamingService adapter = (JNDINamingService) subject;
				adapter.setURL((String) value);
			}
		};
	}

	/**
	 * Creates the <code>DocumentAdapter</code> that keeps the value from the
	 * text field in sync with the Username value in the model and vice
	 * versa.
	 *
	 * @return A new <code>DocumentAdapter</code>
	 */
	private Document buildUsernameAdapter()
	{
		return new DocumentAdapter(buildUsernameHolder());
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Username property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildUsernameHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), JNDINamingService.JNDI_USER_NAME_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				JNDINamingService adapter = (JNDINamingService) subject;
				return adapter.getUserName();
			}

			protected void setValueOnSubject(Object value)
			{
				JNDINamingService adapter = (JNDINamingService) subject;
				adapter.setUserName((String) value);
			}
		};
	}

	/**
	 * Initializes the layout of this pane.
	 */
	protected void initializeLayout()
	{
		setBorder(BorderFactory.createCompoundBorder
		(
			buildTitledBorder("JNDI_NAMING_SERVICE_TITLE"),
			BorderFactory.createEmptyBorder(5, 5, 5, 5))
		);

		GridBagConstraints constraints = new GridBagConstraints();

		// URL widgets
		Component buildURLWidgets = buildLabeledTextField
		(
			jndiUrlKey(),
			buildURLDocumentAdapter()
		);

		constraints.gridx       = 0;
		constraints.gridy       = 0;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(0, 0, 0, 0);

		add(buildURLWidgets, constraints);

		// Username widgets
		Component usernameWidgets = buildLabeledTextField
		(
			jndiUsernameKey(),
			buildUsernameAdapter()
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

		add(usernameWidgets, constraints);

		// Password widgets
		Component passwordWidgets = buildLabeledTextField
		(
			jndiPasswordKey(),
			buildPasswordDocumentAdapter()
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

		add(passwordWidgets, constraints);

		// Initial Context Factory widgets
		Component initialContextFactoryWidgets = buildLabeledTextField
		(
			jndiInitialContextFactoryName(),
			buildInitialContextFactoryAdapter(),
			buildInitialContextFactoryBrowseButton()
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

		add(initialContextFactoryWidgets, constraints);

		// Properties browse button
		JButton showPropertiesButton = buildButton("JNDI_SHOW_PROPERTIES_BUTTON");
		showPropertiesButton.addActionListener(new ShowPropertiesAction());

		constraints.gridx       = 0;
		constraints.gridy       = 4;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 0;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.NONE;
		constraints.anchor      = GridBagConstraints.LINE_START;
		constraints.insets      = new Insets(5, 0, 0, 0);

		add(showPropertiesButton, constraints);
	}

	/**
	 * Requests the key that will be used to retrieve the localized text of
	 * the label Initial Context Factory Name.
	 *
	 * @return "JNDI_INITIAL_CONTEXT_FACTORY_FIELD"
	 */
	protected String jndiInitialContextFactoryName()
	{
		return "JNDI_INITIAL_CONTEXT_FACTORY_FIELD";
	}

	/**
	 * Requests the key that will be used to retrieve the localized text of
	 * the label Password.
	 *
	 * @return "JNDI_PASSWORD_FIELD"
	 */
	protected String jndiPasswordKey()
	{
		return "JNDI_PASSWORD_FIELD";
	}

	/**
	 * Requests the key that will be used to retrieve the localized text of
	 * the label URL.
	 *
	 * @return "JNDI_URL_FIELD"
	 */
	protected String jndiUrlKey()
	{
		return "JNDI_URL_FIELD";
	}

	/**
	 * Requests the key that will be used to retrieve the localized text of
	 * the label Username.
	 *
	 * @return "JNDI_USER_NAME_FIELD"
	 */
	protected String jndiUsernameKey()
	{
		return "JNDI_USER_NAME_FIELD";
	}

	/**
	 * Shows the JNDI's properties into a dialog.
	 */
	private void showProperties()
	{
		AbstractDialog dialog = new AbstractDialog(getWorkbenchContext(), resourceRepository().getString("JNDI_SHOW_PROPERTIES_TITLE"))
		{
			protected Component buildMainPanel()
			{
				PropertyPane pane = new PropertyPane(getSubjectHolder(), getWorkbenchContextHolder());
				addHelpTopicId(pane, helpTopicId());
				pane.setPreferredSize(new Dimension(400, 200));
				return pane;
			}

			protected String helpTopicId()
			{
				return "dialog.jndiNamingService.properties";
			}
		};

		dialog.setVisible(true);
	}

	/**
	 * This action shows the {@link org.eclipse.persistence.tools.workbench.scplugin.ui.tools.PropertyPane}
	 * into a dialog.
	 */
	private class ShowPropertiesAction implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			showProperties();
		}
	}
}
