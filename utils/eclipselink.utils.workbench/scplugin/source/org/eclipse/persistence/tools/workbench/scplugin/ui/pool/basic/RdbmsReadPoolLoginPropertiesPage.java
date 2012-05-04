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
package org.eclipse.persistence.tools.workbench.scplugin.ui.pool.basic;

// JDK
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ButtonModel;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ConnectionPoolAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseLoginAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ReadConnectionPoolAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.ui.login.EisLoginPane;
import org.eclipse.persistence.tools.workbench.scplugin.ui.login.RdbmsPoolLoginPane;
import org.eclipse.persistence.tools.workbench.scplugin.ui.pool.PoolNode;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;

// Mapping Workbench

/**
 * This page shows the information regarding the Database login which is
 * specific for {@link org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseLoginAdapter DatabaseLoginAdapter}.
 * <p>
 * Here the layout:
 * <pre>
 * _____________________________________________
 * |                                           |
 * | x Use Non-Transactional Read Login        |<- Enable/Disable the RdbmsPoolLoginPane
 * |                                           |
 * |   --------------------------------------- |
 * |   |                                     | |
 * |   | {@link RdbmsPoolLoginPane}                  | |
 * |   |                                     | |
 * |   --------------------------------------- |
 * |                                           |
 * ---------------------------------------------</pre>
 *
 * @see ConnectionPoolAdapter
 * @see ReadConnectionPoolAdapter
 *
 * @version 10.1.3
 * @author Pascal Filion
 */
public class RdbmsReadPoolLoginPropertiesPage extends AbstractReadPoolLoginPropertiesPage
{
	/**
	 * Creates a new <code>RdbmsReadPoolLoginPropertiesPage</code>.
	 *
	 * @param nodeHolder The holder of {@link PoolNode}
	 */
	public RdbmsReadPoolLoginPropertiesPage(PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder)
	{
		super(nodeHolder, contextHolder);
	}

	private ButtonModel buildExternalConnectionPoolingCheckBoxModel()
	{
		return new CheckBoxModelAdapter(buildExternalConnectionPoolingHolder());
	}

	private PropertyValueModel buildExternalConnectionPoolingHolder()
	{
		return new PropertyAspectAdapter(buildLoginHolder(), DatabaseLoginAdapter.EXTERNAL_CONNECTION_POOLING_PROPERTY)
		{
			@Override
			protected Object getValueFromSubject()
			{
				DatabaseLoginAdapter adapter = (DatabaseLoginAdapter) subject;
				return adapter.usesExternalConnectionPooling();
			}

			@Override
			protected void setValueOnSubject(Object value)
			{
				DatabaseLoginAdapter adapter = (DatabaseLoginAdapter) subject;
				adapter.setExternalConnectionPooling((Boolean) value);
			}
		};
	}

	private ButtonModel buildExternalTransactionControllerCheckBoxModel()
	{
		return new CheckBoxModelAdapter(buildExternalTransactionControllerHolder());
	}

	private PropertyValueModel buildExternalTransactionControllerHolder()
	{
		return new PropertyAspectAdapter(buildLoginHolder(), DatabaseLoginAdapter.EXTERNAL_TRANSACTION_CONTROLLER_PROPERTY)
		{
			@Override
			protected Boolean getValueFromSubject()
			{
				DatabaseLoginAdapter adapter = (DatabaseLoginAdapter) subject;
				return adapter.usesExternalTransactionController();
			}

			@Override
			protected void setValueOnSubject(Object value)
			{
				DatabaseLoginAdapter adapter = (DatabaseLoginAdapter) subject;
				adapter.setUsesExternalTransactionController((Boolean) value);
			}
		};
	}

	/**
	 * Creates the pane that will show the login information.
	 *
	 * @return {@link EisLoginPane}
	 */
	protected JComponent buildLoginPane()
	{
		GridBagConstraints constraints = new GridBagConstraints();
		JPanel container = new JPanel(new GridBagLayout());

		// EIS login pane
		RdbmsPoolLoginPane loginPane = new RdbmsPoolLoginPane
		(
			buildLoginHolder(),
			getWorkbenchContextHolder()
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

		container.add(loginPane, constraints);

		// External Connection Pooling check box
		JCheckBox externalConnectionPoolingCheckBox = buildCheckBox
		(
			"RDBMS_POOL_LOGIN_PANE_EXTERNAL_CONNECTION_POOLING_CHECKBOX",
			buildExternalConnectionPoolingCheckBoxModel()
		);

		constraints.gridx      = 0;
		constraints.gridy      = 1;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(5, 0, 0, 0);

		container.add(externalConnectionPoolingCheckBox, constraints);

		// External Transaction Controller check box
		JCheckBox externalTransactionControllerCheckBox = buildCheckBox
		(
			"RDBMS_POOL_LOGIN_PANE_EXTERNAL_TRANSACTION_CONTROLLER_CHECKBOX",
			buildExternalTransactionControllerCheckBoxModel()
		);

		constraints.gridx      = 0;
		constraints.gridy      = 2;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 1;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.FIRST_LINE_START;
		constraints.insets     = new Insets(0, 0, 0, 0);

		container.add(externalTransactionControllerCheckBox, constraints);

		return container;
	}
}
