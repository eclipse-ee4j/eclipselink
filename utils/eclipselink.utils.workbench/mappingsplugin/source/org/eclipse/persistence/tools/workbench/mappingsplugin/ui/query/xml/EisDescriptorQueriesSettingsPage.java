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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.xml;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.TransactionalDescriptorComponentFactory;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;


public final class EisDescriptorQueriesSettingsPage
extends ScrollablePropertiesPage
{

	public EisDescriptorQueriesSettingsPage(PropertyValueModel eisDescriptorNodeHolder, WorkbenchContextHolder contextHolder) {
		super(eisDescriptorNodeHolder, contextHolder);
	}

	protected void initialize(PropertyValueModel nodeHolder) {
		super.initialize(nodeHolder);
	}


	protected Component buildPage() {
		setName(resourceRepository().getString("SETTINGS_PANEL_NAME"));

		JPanel mainPanel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		
       
        // Refreshing cache options - panel
        constraints.gridx      = 0;
        constraints.gridy      = 0;
        constraints.gridwidth  = 1;
        constraints.gridheight = 1;
        constraints.weightx    = 1;
        constraints.weighty    = 1;
        constraints.fill       = GridBagConstraints.HORIZONTAL;
        constraints.anchor     = GridBagConstraints.PAGE_START;
        constraints.insets     = new Insets(5, 5, 5, 5);
        mainPanel.add(TransactionalDescriptorComponentFactory.buildRefreshCachePolicyPanel(getSelectionHolder(), getApplicationContext()), constraints);
        
        
		addHelpTopicId(mainPanel, helpTopicId());
		
		return mainPanel;
	}

	public String helpTopicId() {
		return "descriptor.eis.queries.settings";
	}
}
