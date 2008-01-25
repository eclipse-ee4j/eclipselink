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
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.text.Document;

import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.JMSClusteringAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;

// Mapping Workbench

/**
 * This pane shows the information regarding the JMS clustering, which is one of
 * the Cache Synchronization's clustering service.
 * <p>
 * Here the layout:</pre>
 * _______________________________________________________________
 * | ----------------------------------------------------------- |
 * | |                                                         | |
 * | | {@link JNDIClusteringServicePane}                               | |
 * | |                                                         | |
 * | ----------------------------------------------------------- |
 * |                                ____________________________ |
 * | Topic Name:                    | I                        | |
 * |                                ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯ |
 * |                                ____________________________ |
 * | Topic Connection Factory Name: | I                        | |
 * |                                ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯ |
 * ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯<pre>
 *
 * Known containers of this pane:<br>
 * - {@link CacheSynchronizationManagerPane}
 *
 * @see JMSClusteringAdapter
 *
 * @version 10.1.3
 * @author Pascal Filion
 */
final class JMSClusteringPane extends AbstractSubjectPanel
{
	/**
	 * Creates a new <code>JMSClusteringServicePane</code>.
	 *
	 * @param subjectHolder The holder of {@link JMSClusteringAdapter}
	 * @param context
	 */
	JMSClusteringPane(PropertyValueModel subjectHolder,
									 ApplicationContext context)
	{
		super(subjectHolder, context);
		addHelpTopicId(this, "session.clustering.csm.jms");
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
		return new PropertyAspectAdapter(getSubjectHolder(), JMSClusteringAdapter.TOPIC_CONNECTION_FACTORY_NAME_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				JMSClusteringAdapter adapter = (JMSClusteringAdapter) subject;
				return adapter.getTopicConnectionFactoryName();
			}

			protected void setValueOnSubject(Object value)
			{
				JMSClusteringAdapter adapter = (JMSClusteringAdapter) subject;
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
		return new PropertyAspectAdapter(getSubjectHolder(), JMSClusteringAdapter.TOPIC_NAME_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				JMSClusteringAdapter adapter = (JMSClusteringAdapter) subject;
				return adapter.getTopicName();
			}

			protected void setValueOnSubject(Object value)
			{
				JMSClusteringAdapter adapter = (JMSClusteringAdapter) subject;
				adapter.setTopicName((String) value);
			}
		};
	}

	/**
	 * Initializes the layout of this pane.
	 */
	protected void initializeLayout()
	{
		GridBagConstraints constraints = new GridBagConstraints();

		// JNDI Clustering Service properties
		JNDIClusteringServicePane pane = new JNDIClusteringServicePane(getSubjectHolder(), getApplicationContext());

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

		// Topic Name widgets
		Component topicNameWidgets = buildLabeledTextField
		(
			"JMS_CLUSTERING_TOPIC_NAME_FIELD",
			buildTopicNameDocumentAdapter()
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

		add(topicNameWidgets, constraints);
		addHelpTopicId(topicNameWidgets, "session.clustering.csm.jms.topicName");

		// Topic Connection Factory Name widgets
		Component topicConnectionFactoryNameWidgets = buildLabeledTextField
		(
			"JMS_CLUSTERING_TOPIC_CONNECTION_FACTORY_NAME_FIELD",
			buildTopicConnectionFactoryNameDocumentAdapter()
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

		add(topicConnectionFactoryNameWidgets, constraints);
		addHelpTopicId(topicConnectionFactoryNameWidgets, "session.clustering.csm.jms.topicConnectionFactoryName");
	}
}