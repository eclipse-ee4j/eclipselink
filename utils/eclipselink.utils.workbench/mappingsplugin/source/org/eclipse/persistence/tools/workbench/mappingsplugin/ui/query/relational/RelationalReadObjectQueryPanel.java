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

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWJoinedItem;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWQueryItem;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.QueryGeneralPanel;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.QueryQuickViewItem;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.QuickViewPanel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;



/**
 * DescriptorPropertiesQueryPage holds on to QueryPanel
 * QueryPanel is just used to set up the tab panels for a query
 * 
 */
final class RelationalReadObjectQueryPanel 
	extends AbstractPanel
{
	private PropertyValueModel queryHolder;

	private JTabbedPane queryPropertiesPane;
	private JoinedAttributesPanel joinedAttributesPanel;
	private QueryGeneralPanel queryGeneralPanel;
	
	RelationalReadObjectQueryPanel(PropertyValueModel queryHolder, ObjectListSelectionModel querySelectionModel, WorkbenchContextHolder contextHolder){
		super(contextHolder);
		this.queryHolder = queryHolder;
		initializeLayout(querySelectionModel);
	}

	private void initializeLayout(ObjectListSelectionModel querySelectionModel) {
		GridBagConstraints constraints = new GridBagConstraints();
		
		this.queryPropertiesPane = new JTabbedPane();
		this.queryGeneralPanel = new QueryGeneralPanel(this.queryHolder, querySelectionModel, getWorkbenchContextHolder());
		JPanel queryFormatPanel = new QuerySelectionCriteriaPanel(this.queryHolder,  getWorkbenchContextHolder());
		this.joinedAttributesPanel = new JoinedAttributesPanel(this.queryHolder, getWorkbenchContextHolder());
		JPanel queryOptionsPanel = new RelationalQueryOptionsPanel(this.queryHolder,  getWorkbenchContextHolder());
		//this.partialAttributesPanel = new MWQueryPartialAttributesPanel(this);
		
		this.queryPropertiesPane.addTab(resourceRepository().getString("GENERAL_TAB"), this.queryGeneralPanel);
		this.queryPropertiesPane.addTab(resourceRepository().getString("FORMAT_TAB"), queryFormatPanel);
		this.queryPropertiesPane.addTab(resourceRepository().getString("OPTIMIZATION_TAB"), this.joinedAttributesPanel);
		this.queryPropertiesPane.addTab(resourceRepository().getString("OPTIONS_TAB"), queryOptionsPanel);
		
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(0, 0, 0, 0);
		add(this.queryPropertiesPane, constraints);
	}
	
	protected void selectQueryOptimizationTab() {
		this.queryPropertiesPane.setSelectedComponent(this.joinedAttributesPanel);
	}
	
	protected QueryGeneralPanel getQueryGeneralPanel() {
		return this.queryGeneralPanel;
	}

	protected JTabbedPane getQueryTabbedPane() {
		return this.queryPropertiesPane;
	}	
	
	protected QuickViewPanel.QuickViewItem buildJoinedAttributeQuickViewItem(MWJoinedItem queryItem) {
		return new QueryQuickViewItem(queryItem) {
			public void select() {
				RelationalReadObjectQueryPanel.this.queryPropertiesPane.setSelectedComponent(RelationalReadObjectQueryPanel.this.joinedAttributesPanel);
				RelationalReadObjectQueryPanel.this.joinedAttributesPanel.select((MWQueryItem) getValue());
			}
		};
	}
	
	/*protected void setQueryIsPartialAttributeQuery(boolean isPartialAttributeQuery)
	{
		if (isPartialAttributeQuery)
			this.queryPropertiesPane.addTab("Partial Attributes", this.partialAttributesPanel);
			//might need to populate the panel from here.  I could not remove the partial attribute info if the user
			//unchecks the check box.  Then when they recheck it, we could populate the panel again.  At conversion time,
			//I would check if the isPartialAttributeQuery boolean is true or false.  I would specify the partial attributes
			//in the toplink project based on that
		else
			this.queryPropertiesPane.removeTabAt(4);		
	}*/
	
}
