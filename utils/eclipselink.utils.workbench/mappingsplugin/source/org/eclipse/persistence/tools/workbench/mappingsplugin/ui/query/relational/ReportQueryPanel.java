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

import java.awt.BorderLayout;
import javax.swing.JTabbedPane;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWGroupingItem;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWReportAttributeItem;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.Ordering;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.QueryGeneralPanel;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.QueryQuickViewItem;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.QuickViewPanel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;


/**
 * DescriptorPropertiesQueryPage holds on to QueryPanel
 * QueryPanel is just used to set up the tab panels for a query
 */
final class ReportQueryPanel 
	extends AbstractPanel
{
	private PropertyValueModel queryHolder;

	private JTabbedPane queryPropertiesPane;
		private ReportQueryAttributesPanel attributesPanel;
		private QuerySelectionCriteriaPanel queryFormatPanel;
		private QueryGeneralPanel queryGeneralPanel;
		private ReportQueryGroupingOrderingPanel queryGroupingOrderingPanel;
		private ReportQueryOptionsPanel queryOptionsPanel;


	ReportQueryPanel(PropertyValueModel queryHolder,
	                 ObjectListSelectionModel querySelectionModel,
	                 WorkbenchContextHolder contextHolder) {
		super(new BorderLayout(), contextHolder);
		this.queryHolder = queryHolder;
		initializeLayout(querySelectionModel);
	}


	private void initializeLayout(ObjectListSelectionModel querySelectionModel) {		
		this.queryGeneralPanel     = new QueryGeneralPanel(this.queryHolder, querySelectionModel, getWorkbenchContextHolder());
		this.attributesPanel       = new ReportQueryAttributesPanel(this.queryHolder,  getWorkbenchContextHolder());
		this.queryFormatPanel      = new QuerySelectionCriteriaPanel(this.queryHolder,  getWorkbenchContextHolder());
		this.queryGroupingOrderingPanel = new ReportQueryGroupingOrderingPanel(this.queryHolder, getWorkbenchContextHolder());
		this.queryOptionsPanel     = new ReportQueryOptionsPanel(this.queryHolder,  getWorkbenchContextHolder());

		this.queryPropertiesPane = new JTabbedPane();
		this.queryPropertiesPane.addTab(resourceRepository().getString("GENERAL_TAB"),     this.queryGeneralPanel);
		this.queryPropertiesPane.addTab(resourceRepository().getString("ATTRIBUTES_TAB"),  this.attributesPanel);
		this.queryPropertiesPane.addTab(resourceRepository().getString("FORMAT_TAB"),      this.queryFormatPanel);
		this.queryPropertiesPane.addTab(resourceRepository().getString("GROUP_ORDER_TAB"), this.queryGroupingOrderingPanel);
		this.queryPropertiesPane.addTab(resourceRepository().getString("OPTIONS_TAB"),     this.queryOptionsPanel);

		add(this.queryPropertiesPane, BorderLayout.CENTER);
	}

	protected void selectReportQueryAttributesTab() {
		this.queryPropertiesPane.setSelectedComponent(this.attributesPanel);
	}
	protected void selectReportQueryGroupingOrderingTab() {
		this.queryPropertiesPane.setSelectedComponent(this.queryGroupingOrderingPanel);
	}	
	
	protected QueryGeneralPanel getQueryGeneralPanel() {
		return this.queryGeneralPanel;
	}

	protected JTabbedPane getQueryTabbedPane() {
		return this.queryPropertiesPane;
	}	
	
	protected QuickViewPanel.QuickViewItem buildReportAttributeQuickViewItem(MWReportAttributeItem attributeItem) {
		return new QueryQuickViewItem(attributeItem) {
			public void select() {
				ReportQueryPanel.this.queryPropertiesPane.setSelectedComponent(ReportQueryPanel.this.attributesPanel);
				ReportQueryPanel.this.attributesPanel.select((MWReportAttributeItem) getValue());
			}
		};	
	}
	
	protected QuickViewPanel.QuickViewItem buildGroupingAttributeQuickViewItem(MWGroupingItem groupingItem) {
		return new QueryQuickViewItem(groupingItem) {
			public void select() {
				ReportQueryPanel.this.queryPropertiesPane.setSelectedComponent(ReportQueryPanel.this.queryGroupingOrderingPanel);
				ReportQueryPanel.this.queryGroupingOrderingPanel.selectGroupingItem((MWGroupingItem) getValue());
			}
		};	
	}
	
	protected QuickViewPanel.QuickViewItem buildOrderingAttributeQuickViewItem(Ordering orderingItem) {
		return new QueryQuickViewItem(orderingItem) {
			public void select() {
				ReportQueryPanel.this.queryPropertiesPane.setSelectedComponent(ReportQueryPanel.this.queryGroupingOrderingPanel);
				ReportQueryPanel.this.queryGroupingOrderingPanel.selectOrderingItem((Ordering) getValue());
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
