/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.xml.login;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;


public class EisConnectionPropertiesPage extends AbstractLoginPropertiesPage
{
	/**
	 * Creates a new <code>RdbmsConnectionPropertiesPage</code>.
	 *
	 * @param nodeHolder The holder of {@link org.eclipse.persistence.tools.workbench.scplugin.ui.session.DatabaseSessionNode DatabaseSessionNode}
	 */
	public EisConnectionPropertiesPage(PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder)
	{
		super(nodeHolder, contextHolder);
		addHelpTopicId(this, "project.eis.connectionSpecification.connection");
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
		EisLoginPane loginPane = new EisLoginPane(getSelectionHolder(), getWorkbenchContextHolder());

		constraints.gridx       = 0;
		constraints.gridy       = 1;
		constraints.gridwidth   = 3;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 1;
		constraints.fill        = GridBagConstraints.BOTH;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(0, 0, 0, 0);

		panel.add(loginPane, constraints);
		addPaneForAlignment(loginPane);

		return panel;
	}

}
