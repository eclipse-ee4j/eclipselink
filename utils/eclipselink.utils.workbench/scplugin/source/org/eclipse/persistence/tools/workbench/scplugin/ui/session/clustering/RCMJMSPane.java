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
package org.eclipse.persistence.tools.workbench.scplugin.ui.session.clustering;

// JDK
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.text.Document;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.JMSTopicTransportManagerAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;

// Mapping Workbench

/**
 * This page shows the information about the {@link org.eclipse.persistence.tools.workbench.scplugin.model.adapter.JMSTopicTransportManagerAdapter
 * JMSTopicTransportManagerAdapter}.
 * <p>
 * Here the layout:</pre>
 * _______________________________________________________________
 * |                                ____________________________ |
 * | JMS Topic Name:                | I                        | |
 * |                                ---------------------------- |
 * |                                ____________________________ |
 * | Topic Connection Factory Name: | I                        | |
 * |                                ---------------------------- |
 * | -JNDI Naming Service--------------------------------------- |
 * | |                                                         | |
 * | | {@link JNDINamingServicePane}                                   | |
 * | |                                                         | |
 * | ----------------------------------------------------------- |
 * ---------------------------------------------------------------<pre>
 *
 * Known container of this pane:<br>
 * - {@link RemoteCommandManagerPane}
 *
 * @see JMSTopicTransportManagerAdapter
 *
 * @version 10.1.3
 * @author Pascal Filion
 */
final class RCMJMSPane extends AbstractTransportManagerPane
{
	/**
	 * Creates a new <code>RCMJMSPane</code>.
	 *
	 * @param subjectHolder The holder of {@link JMSTopicTransportManagerAdapter}
	 * @param context
	 */
	RCMJMSPane(PropertyValueModel subjectHolder,
				  WorkbenchContextHolder contextHolder)
	{
		super(subjectHolder, contextHolder);
	}

	/**
	 * Creates the <code>DocumentAdapter</code> that keeps the value from the
	 * text field in sync with the Topic Connection Factory Name value in the
	 * model and vice versa.
	 *
	 * @return A new <code>DocumentAdapter</code>
	 */
	private Document buildTopicConnectionFactoryNameDocumentAdapter()
	{
		return new DocumentAdapter(buildTopicConnectionFactoryNameHolder());
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Topic Connection Factory Name property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildTopicConnectionFactoryNameHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), JMSTopicTransportManagerAdapter.TOPIC_CONNECTION_FACTORY_NAME_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				JMSTopicTransportManagerAdapter adapter = (JMSTopicTransportManagerAdapter) subject;
				return adapter.getTopicConnectionFactoryName();
			}

			protected void setValueOnSubject(Object value)
			{
				JMSTopicTransportManagerAdapter adapter = (JMSTopicTransportManagerAdapter) subject;
				adapter.setTopicConnectionFactoryName((String) value);
			}
		};
	}

	/**
	 * Creates the <code>DocumentAdapter</code> that keeps the value from the
	 * text field in sync with the Topic Name value in the model and vice versa.
	 *
	 * @return A new <code>DocumentAdapter</code>
	 */
	private Document buildTopicNameDocumentAdapter()
	{
		return new DocumentAdapter(buildTopicNameHolder());
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Topic Name property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildTopicNameHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), JMSTopicTransportManagerAdapter.TOPIC_NAME_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				JMSTopicTransportManagerAdapter adapter = (JMSTopicTransportManagerAdapter) subject;
				return adapter.getTopicName();
			}

			protected void setValueOnSubject(Object value)
			{
				JMSTopicTransportManagerAdapter adapter = (JMSTopicTransportManagerAdapter) subject;
				adapter.setTopicName((String) value);
			}
		};
	}

	/**
	 * Creates the <code>DocumentAdapter</code> that keeps the value from the
	 * text field in sync with the TopicHostURL value in the model and vice versa.
	 *
	 * @return A new <code>DocumentAdapter</code>
	 */
	private Document buildTopicHostURLDocumentAdapter()
	{
		return new DocumentAdapter(buildTopicHostURLHolder());
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * TopicHostURL property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildTopicHostURLHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), JMSTopicTransportManagerAdapter.TOPIC_HOST_URL_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				JMSTopicTransportManagerAdapter adapter = (JMSTopicTransportManagerAdapter) subject;
				return adapter.getTopicHostURL();
			}

			protected void setValueOnSubject(Object value)
			{
				JMSTopicTransportManagerAdapter adapter = (JMSTopicTransportManagerAdapter) subject;
				adapter.setTopicHostURL((String) value);
			}
		};
	}

	/**
	 * Initializes the layout of this pane.
	 */
	protected void initializeLayout()
	{
		GridBagConstraints constraints = new GridBagConstraints();

		// Topic Name widgets
		Component jmsTopicNameWidgets = buildLabeledTextField
		(
			"RMI_JMS_TOPIC_NAME_FIELD",
			buildTopicNameDocumentAdapter()
		);

		constraints.gridx       = 0;
		constraints.gridy       = 0;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 0;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(0, 0, 0, 0);

		add(jmsTopicNameWidgets, constraints);
		addHelpTopicId(jmsTopicNameWidgets, "session.clustering.rcm.jms.topicName");

		// Topic Connection Factory Name widgets
		Component topicConnectionFactoryNameWidgets = buildLabeledTextField
		(
			"RMI_JMS_TOPIC_CONNECTION_FACTORY_NAME_FIELD",
			buildTopicConnectionFactoryNameDocumentAdapter()
		);

		constraints.gridx       = 0;
		constraints.gridy       = 1;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 0;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(5, 0, 0, 0);

		add(topicConnectionFactoryNameWidgets, constraints);
		addHelpTopicId(topicConnectionFactoryNameWidgets, "session.clustering.rcm.jms.topicConnectionFactoryName");
//----
		// TopicHostURL widgets
		Component jmsTopicHostURLWidgets = buildLabeledTextField
		(
			"RMI_JMS_TOPIC_HOST_URL_FIELD",
			buildTopicHostURLDocumentAdapter()
		);

		constraints.gridx       = 0;
		constraints.gridy       = 2;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 0;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(5, 0, 0, 0);

		add(jmsTopicHostURLWidgets, constraints);
		addHelpTopicId(jmsTopicHostURLWidgets, "session.clustering.rcm.jms.topicHostURL");
//----		
		// Remove Connection On Error check box
		JCheckBox removeConnectionOnErrorCheckBox = buildRemoveConnectionOnError();

		constraints.gridx       = 0;
		constraints.gridy       = 3;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 0;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.NONE;
		constraints.anchor      = GridBagConstraints.LINE_START;
		constraints.insets      = new Insets(5, 0, 0, 0);

		add(removeConnectionOnErrorCheckBox, constraints);

		// JNDI Naming Service pane
		JNDINamingServicePane jndiNamingServicePane = new CustomizedJNDINamingServicePane();

		constraints.gridx       = 0;
		constraints.gridy       = 4;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 1;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.PAGE_START;
		constraints.insets      = new Insets(5, 0, 0, 0);

		add(jndiNamingServicePane, constraints);

		addHelpTopicId(this, "session.clustering.rcm.jms");

	}

	/**
	 * This extension over {@link JNDINamingServicePane} simply changes
	 * the key used to retrieve the localized text.
	 */
	private class CustomizedJNDINamingServicePane extends JNDINamingServicePane
	{
		/**
		 * Creates a new <code>CustomizedJNDINamingServicePane</code>.
		 */
		private CustomizedJNDINamingServicePane()
		{
			super(RCMJMSPane.this.getSubjectHolder(),
					RCMJMSPane.this.getWorkbenchContextHolder());
		}

		/**
		 * Requests the key that will be used to retrieve the localized text of
		 * the label Initial Context Factory Name.
		 *
		 * @return "JNDI_INITIAL_CONTEXT_FACTORY_NAME_FIELD_OPTIONAL"
		 */
		protected String jndiInitialContextFactoryName()
		{
			return "JNDI_INITIAL_CONTEXT_FACTORY_NAME_FIELD_OPTIONAL";
		}

		/**
		 * Requests the key that will be used to retrieve the localized text of
		 * the label Password.
		 *
		 * @return "JNDI_PASSWORD_FIELD_OPTIONAL"
		 */
		protected String jndiPasswordKey()
		{
			return "JNDI_PASSWORD_FIELD_OPTIONAL";
		}

		/**
		 * Requests the key that will be used to retrieve the localized text of
		 * the label URL.
		 *
		 * @return "JNDI_URL_FIELD_OPTIONAL"
		 */
		protected String jndiUrlKey()
		{
			return "JNDI_URL_FIELD_OPTIONAL";
		}

		/**
		 * Requests the key that will be used to retrieve the localized text of
		 * the label Username.
		 *
		 * @return "JNDI_USER_NAME_FIELD_OPTIONAL"
		 */
		protected String jndiUsernameKey()
		{
			return "JNDI_USER_NAME_FIELD_OPTIONAL";
		}
	}
}
