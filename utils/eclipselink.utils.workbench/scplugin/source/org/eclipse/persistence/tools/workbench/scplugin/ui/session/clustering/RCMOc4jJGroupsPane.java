/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
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

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.ButtonModel;
import javax.swing.JCheckBox;
import javax.swing.text.Document;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.Oc4jJGroupsTransportManagerAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.RMITransportManagerAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;


public final class RCMOc4jJGroupsPane extends AbstractTransportManagerPane {

	/**
	 * Creates a new <code>RCMSunCORBAPane</code>.
	 *
	 * @param subjectHolder The holder of {@link SunCORBATransportManagerAdapter}
	 * @param contextHolder The holder of the context to be used by this pane
	 */
	public RCMOc4jJGroupsPane(ValueModel subjectHolder,
								  WorkbenchContextHolder contextHolder)
	{
		super(subjectHolder, contextHolder);
	}

	/**
	 * Initializes this pane.
	 */
	protected void initializeLayout()
	{
		GridBagConstraints constraints = new GridBagConstraints();

		// Topic Name widgets
		Component topicNameWidgets = buildLabeledTextField
		(
			"OC4J_TOPIC_NAME_FIELD",
			buildTopicNameDocumentAdapter()
		);

		constraints.gridx       = 0;
		constraints.gridy       = 0;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(5, 0, 0, 0);

		add(topicNameWidgets, constraints);
		addHelpTopicId(topicNameWidgets, "session.clustering.rcm.oc4j.topicName");

		// Remove Connection On Error check box
		JCheckBox removeConnectionOnErrorCheckBox = buildRemoveConnectionOnError();

		constraints.gridx       = 0;
		constraints.gridy       = 1;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.NONE;
		constraints.anchor      = GridBagConstraints.LINE_START;
		constraints.insets      = new Insets(0, 0, 0, 0);

		add(removeConnectionOnErrorCheckBox, constraints);
		
		// Use single threaded notification check box
		JCheckBox useSingleThreadedCheckBox = buildCheckBox
		(
			"OC4J_USE_SINGLE_THREADED_CHECK_BOX",
			buildUseSingleThreadNotificationButtonModelAdapter()
		);

		constraints.gridx       = 0;
		constraints.gridy       = 2;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 0;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.NONE;
		constraints.anchor      = GridBagConstraints.LINE_START;
		constraints.insets      = new Insets(0, 0, 0, 0);

		add(useSingleThreadedCheckBox, constraints);
		addHelpTopicId(useSingleThreadedCheckBox, "session.clustering.rcm.oc4j.usesSingleThreadNotification");
	}

	private ButtonModel buildUseSingleThreadNotificationButtonModelAdapter()
	{
		return new CheckBoxModelAdapter(buildUseSingleThreadedNotificationHolder());
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Uses single thread notification property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyAspectAdapter buildUseSingleThreadedNotificationHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), Oc4jJGroupsTransportManagerAdapter.USE_SINGLE_THREADED_NOTIFICATION_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				Oc4jJGroupsTransportManagerAdapter transport = (Oc4jJGroupsTransportManagerAdapter) subject;
				return Boolean.valueOf(transport.usesSingleThreadedNotification());
			}

			protected void setValueOnSubject(Object value)
			{
				Oc4jJGroupsTransportManagerAdapter transport = (Oc4jJGroupsTransportManagerAdapter) subject;
				transport.setUseSingleThreadedNotification(((Boolean)value).booleanValue());
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
		return new PropertyAspectAdapter(getSubjectHolder(), Oc4jJGroupsTransportManagerAdapter.TOPIC_NAME_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				Oc4jJGroupsTransportManagerAdapter adapter = (Oc4jJGroupsTransportManagerAdapter) subject;
				return adapter.getTopicName();
			}

			protected void setValueOnSubject(Object value)
			{
				Oc4jJGroupsTransportManagerAdapter adapter = (Oc4jJGroupsTransportManagerAdapter) subject;
				adapter.setTopicName((String) value);
			}
		};
	}

}
