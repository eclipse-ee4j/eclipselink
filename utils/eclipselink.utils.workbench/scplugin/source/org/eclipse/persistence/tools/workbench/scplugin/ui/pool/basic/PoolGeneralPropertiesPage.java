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
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;

// Mapping Workbench

/**
 * This page shows the information regarding {@link ConnectionPoolAdapter}.
 * <p>
 * Here the layout:
 * <pre>
 * _______________________________
 * |                             |
 * | --------------------------- |
 * | |                         | |
 * | | {@link ConnectionCountPane}     | |
 * | |                         | |
 * | --------------------------- |
 * |                             |
 * -------------------------------</pre>
 *
 * @see ConnectionPoolAdapter
 *
 * @version 10.0.3
 * @author Pascal Filion
 */
public class PoolGeneralPropertiesPage extends ScrollablePropertiesPage
{
	/**
	 * Creates a new <code>PoolGeneralPropertiesPage</code>.
	 *
	 * @param nodeHolder The holder of {@link PoolNode}
	 */
	public PoolGeneralPropertiesPage( PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder)
	{
		super( nodeHolder, contextHolder);
		addHelpTopicId(this, "connectionPool.general");
	}

	/**
	 * Creates the panel that holds all the widgets of this page.
	 *
	 * @return The fully initialized container
	 */
	protected Component buildPage()
	{
		GridBagConstraints constraints = new GridBagConstraints();

		// Create the container
		JPanel panel = new JPanel( new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		// Connection Count pane
		ConnectionCountPane connectionCountPane = new ConnectionCountPane(getSelectionHolder(), getApplicationContext());

		constraints.gridx       = 0;
		constraints.gridy       = 1;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 1;
		constraints.fill        = GridBagConstraints.NONE;
		constraints.anchor      = GridBagConstraints.FIRST_LINE_START;
		constraints.insets      = new Insets(0, 0, 0, 0);

		panel.add(connectionCountPane, constraints);

		return panel;
	}
}
