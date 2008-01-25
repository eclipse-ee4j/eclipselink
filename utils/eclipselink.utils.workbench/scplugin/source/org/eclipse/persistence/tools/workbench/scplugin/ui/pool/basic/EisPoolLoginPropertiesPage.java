/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.scplugin.ui.pool.basic;

// JDK
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ConnectionPoolAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.ui.login.EisLoginPane;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;

// Mapping Workbench

/**
 * This page shows the information regarding the Database login which is
 * specific for {@link org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseLoginAdapter DatabaseLoginAdapter}.
 * <p>
 * Here the layout:
 * <pre>
 * _______________________________________
 * |                                     |
 * | ----------------------------------- |
 * | |                                 | |
 * | | {@link EisLoginPane}                       | |
 * | |                                 | |
 * | ----------------------------------- |
 * |                                     |
 * ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯</pre>
 *
 * @see DatabaseLoginAdapter
 * @see LoginAdapter
 *
 * @version 10.0.3
 * @author Pascal Filion
 */
public class EisPoolLoginPropertiesPage extends ScrollablePropertiesPage
{
	/**
	 * Creates a new <code>RdbmsPoolLoginPropertiesPage</code>.
	 *
	 * @param nodeHolder
	 */
	public EisPoolLoginPropertiesPage( PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder)
	{
		super(nodeHolder, contextHolder);
		addHelpTopicId(this, "connectionPool.login.eis");
	}

	/**
	 * Creates the selection holder that will hold the user object to be edited
	 * by this page.
	 *
	 * @return The <code>PropertyValueModel</code> containing the {@link EisLoginAdapter}
	 * to be edited by the {@link EisLoginPane}
	 */
	protected PropertyValueModel buildLoginHolder()
	{
		return new PropertyAspectAdapter(getSelectionHolder(), ConnectionPoolAdapter.LOGIN_CONFIG_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				ConnectionPoolAdapter pool = (ConnectionPoolAdapter) subject;
				return pool.getLogin();
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

		// Login pane
		EisLoginPane loginPane = new EisLoginPane(buildLoginHolder(), getWorkbenchContextHolder());

		constraints.gridx       = 0;
		constraints.gridy       = 0;
		constraints.gridwidth   = 3;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 1;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.PAGE_START;
		constraints.insets      = new Insets(0, 0, 0, 0);

		panel.add(loginPane, constraints);
      
		return panel;
	}
}