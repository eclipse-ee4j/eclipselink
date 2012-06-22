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
package org.eclipse.persistence.tools.workbench.scplugin.ui.session.basic;

// JDK
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ServerSessionAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;

// Mapping Workbench

/**
 * This shows the options for Connection Policy, this tab is only shown for
 * server sessions that have EIS or Database as the data source.
 * <p>
 * Here the layout of this pane:
 * <pre>
 * __________________________________________
 * |                                        |
 * | x Acquire Exclusive Connection         |
 * |                                        |
 * | x Acquire Connections Lazily           |
 * |                                        |
 * ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯
 * </pre>
 *
 * @version 10.1.3
 * @author Pascal Filion
 */
public class SessionConnectionPolicyPropertiesPage extends ScrollablePropertiesPage
{
	/**
	 * Creates a new <code>SessionConnectionPolicyPropertiesPage</code>.
	 *
	 * @param nodeHolder The holder of <code>XXX</code>
	 * @param contextHolder The holder of the context
	 */
	public SessionConnectionPolicyPropertiesPage(PropertyValueModel nodeHolder,
	                                             WorkbenchContextHolder contextHolder)
	{
		super(nodeHolder, contextHolder);
	}

	private PropertyValueModel buildAcquireConnectionsLazilyCheckboxHolder()
	{
		return new PropertyAspectAdapter(getSelectionHolder(), ServerSessionAdapter.LAZY_CONNECTION_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				ServerSessionAdapter session = (ServerSessionAdapter) subject;
				return Boolean.valueOf(session.usesLazyConnection());
			}

			protected void setValueOnSubject(Object value)
			{
				ServerSessionAdapter session = (ServerSessionAdapter) subject;
				session.setLazyConnection(Boolean.TRUE.equals(value));
			}
		};
	}

	private PropertyValueModel buildAcquireExclusiveConnectionHolder()
	{
		return new PropertyAspectAdapter(getSelectionHolder(), ServerSessionAdapter.USE_EXCLUSIVE_CONNECTION_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				ServerSessionAdapter session = (ServerSessionAdapter) subject;
				return Boolean.valueOf(session.usesExclusiveConnection());
			}

			protected void setValueOnSubject(Object value)
			{
				ServerSessionAdapter session = (ServerSessionAdapter) subject;
				session.setUseExclusiveConnection(Boolean.TRUE.equals(value));
			}
		};
	}

	/**
	 * Initializes the layout of this pane.
	 *
	 * @return The container with all its widgets
	 */
	protected Component buildPage()
	{
		GridBagConstraints constraints = new GridBagConstraints();

		// Create the container
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		// Acquire Exclusive Connection
		JCheckBox acquireExclusiveConnectionCheckbox = buildCheckBox
		(
			"SESSION_CONNECTION_POLICY_ACQUIRE_EXCLUSIVE_CONNECTION_CHECKBOX",
			new CheckBoxModelAdapter(buildAcquireExclusiveConnectionHolder())
		);

		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(0, 0, 0, 0);

		panel.add(acquireExclusiveConnectionCheckbox, constraints);

		// Acquire Connections Lazily
		JCheckBox acquireConnectionsLazilyCheckbox = buildCheckBox
		(
			"SESSION_CONNECTION_POLICY_ACQUIRE_CONNECTIONS_LAZILY_CHECKBOX",
			new CheckBoxModelAdapter(buildAcquireConnectionsLazilyCheckboxHolder())
		);

		constraints.gridx      = 0;
		constraints.gridy      = 1;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 1;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.FIRST_LINE_START;
		constraints.insets     = new Insets(0, 0, 0, 0);

		panel.add(acquireConnectionsLazilyCheckbox, constraints);

		addHelpTopicId(panel, "session.connectionPolicy");
		return panel;
	}
}
