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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.relational;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWBatchReadItem;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWJoinedItem;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;



public final class ReadAllQueryOptimizationPanel extends AbstractPanel {

	private PropertyValueModel queryHolder;
	
	private JoinedAttributesPanel joinedAttributesPanel;
	private BatchReadAttributesPanel batchReadAttributesPanel;
	
	public ReadAllQueryOptimizationPanel(PropertyValueModel queryHolder, WorkbenchContextHolder contextHolder) {
		super(contextHolder);
		this.queryHolder = queryHolder;
		initializeLayout();
	}
	
	private void initializeLayout() {
		
		GridBagConstraints constraints = new GridBagConstraints();
		
		this.batchReadAttributesPanel = new BatchReadAttributesPanel(this.queryHolder, getWorkbenchContextHolder());
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(0, 0, 0, 0);	
		add(this.batchReadAttributesPanel, constraints);
		
		this.joinedAttributesPanel = new JoinedAttributesPanel(this.queryHolder, getWorkbenchContextHolder());
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(0, 0, 0, 0);				
		add(this.joinedAttributesPanel, constraints);
	}
	
	protected void selectJoinedItem(MWJoinedItem item) {
		this.joinedAttributesPanel.select(item);
	}
	
	protected void selectBatchReadItem(MWBatchReadItem item) {
		this.batchReadAttributesPanel.select(item);
	}
}
