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
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWBatchReadItem;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWJoinedItem;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWQueryItem;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.Ordering;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.QueryGeneralPanel;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.QueryQuickViewItem;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.QuickViewPanel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;
import org.eclipse.persistence.tools.workbench.utility.filters.Filter;



/**
 * DescriptorPropertiesQueryPage holds on to QueryPanel
 * QueryPanel is just used to set up the tab panels for a query
 * 
 */
final class RelationalReadAllQueryPanel 
	extends AbstractPanel
{
	private PropertyValueModel queryHolder;

	private JTabbedPane tabbedPane;
	private QueryGeneralPanel queryGeneralPanel;
	private JPanel queryFormatPanel;
	private OrderingAttributesPanel queryOrderingPanel;
	private ReadAllQueryOptimizationPanel queryOptimizationPanel;
	private RelationalQueryOptionsPanel queryOptionsPanel;

	
	RelationalReadAllQueryPanel(PropertyValueModel queryHolder, ObjectListSelectionModel querySelectionModel, WorkbenchContextHolder contextHolder){
		super(contextHolder);
		this.queryHolder = queryHolder;
		initializeLayout(querySelectionModel);
	}

	private Filter buildOrderingChooseableFilter() {
		return new Filter() {
			public boolean accept(Object o) {
				return ((MWQueryable) o).isValidForReadAllQueryOrderable();
			}
		};
	}

	private Filter buildOrderingTraversableFilter() {
		return new Filter() {
			public boolean accept(Object o) {
				return ((MWQueryable) o).isTraversableForReadAllQueryOrderable();
			}
		};
	}

	private void initializeLayout(ObjectListSelectionModel querySelectionModel) {
		GridBagConstraints constraints = new GridBagConstraints();
		
		this.tabbedPane = new JTabbedPane();
		this.queryGeneralPanel = new QueryGeneralPanel(this.queryHolder, querySelectionModel, getWorkbenchContextHolder());
		this.queryFormatPanel = new QuerySelectionCriteriaPanel(this.queryHolder,  getWorkbenchContextHolder());
		this.queryOrderingPanel = new OrderingAttributesPanel(this.queryHolder, buildOrderingTraversableFilter(), buildOrderingChooseableFilter(), getWorkbenchContextHolder());
		this.queryOptimizationPanel = new ReadAllQueryOptimizationPanel(this.queryHolder, getWorkbenchContextHolder());
		this.queryOptionsPanel = new RelationalQueryOptionsPanel(this.queryHolder,  getWorkbenchContextHolder());
		//this.partialAttributesPanel = new MWQueryPartialAttributesPanel(this);
		
		this.tabbedPane.addTab(resourceRepository().getString("GENERAL_TAB"), this.queryGeneralPanel);
		this.tabbedPane.addTab(resourceRepository().getString("FORMAT_TAB"), this.queryFormatPanel);
		this.tabbedPane.addTab(resourceRepository().getString("ORDER_TAB"), this.queryOrderingPanel);
		this.tabbedPane.addTab(resourceRepository().getString("OPTIMIZATION_TAB"), this.queryOptimizationPanel);
		this.tabbedPane.addTab(resourceRepository().getString("OPTIONS_TAB"), this.queryOptionsPanel);
		
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(0, 0, 0, 0);
		add(this.tabbedPane, constraints);
	}
	
	protected QuickViewPanel.QuickViewItem buildBatchReadAttributeQuickViewItem(MWBatchReadItem queryItem) {
		return new QueryQuickViewItem(queryItem) {
			public void select() {
				RelationalReadAllQueryPanel.this.tabbedPane.setSelectedComponent(RelationalReadAllQueryPanel.this.queryOptimizationPanel);
				RelationalReadAllQueryPanel.this.queryOptimizationPanel.selectBatchReadItem((MWBatchReadItem) getValue());
			}
		};
	}
	protected QuickViewPanel.QuickViewItem buildJoinedAttributeQuickViewItem(MWJoinedItem queryItem) {
		return new QueryQuickViewItem(queryItem) {
			public void select() {
				RelationalReadAllQueryPanel.this.tabbedPane.setSelectedComponent(RelationalReadAllQueryPanel.this.queryOptimizationPanel);
				RelationalReadAllQueryPanel.this.queryOptimizationPanel.selectJoinedItem((MWJoinedItem) getValue());
			}
		};
	}

	protected QuickViewPanel.QuickViewItem buildOrderingAttributeQuickViewItem(Ordering queryItem) {
		return new QueryQuickViewItem(queryItem) {
			public void select() {
				RelationalReadAllQueryPanel.this.tabbedPane.setSelectedComponent(RelationalReadAllQueryPanel.this.queryOrderingPanel);
				RelationalReadAllQueryPanel.this.queryOrderingPanel.select((MWQueryItem) getValue());
			}
		};
	}
	
	protected void selectQueryOptimizationTab() {
		this.tabbedPane.setSelectedComponent(this.queryOptimizationPanel);
	}
	
	protected void selectQueryOrderingTab() {
		this.tabbedPane.setSelectedComponent(this.queryOrderingPanel);
	}

	protected QueryGeneralPanel getQueryGeneralPanel() {
		return this.queryGeneralPanel;
	}
	
	protected JTabbedPane getQueryTabbedPane() {
		return this.tabbedPane;
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
