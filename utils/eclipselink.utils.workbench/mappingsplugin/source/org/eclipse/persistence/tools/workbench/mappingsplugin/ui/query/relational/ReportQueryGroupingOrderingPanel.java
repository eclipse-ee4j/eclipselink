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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.relational;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWAttributeItem;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWGroupingItem;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWReportOrderingItem;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWReportQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.Ordering;
import org.eclipse.persistence.tools.workbench.uitools.app.FilteringPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.utility.filters.Filter;


final class ReportQueryGroupingOrderingPanel
	extends AbstractPanel
{

	private PropertyValueModel queryHolder;
	private OrderingAttributesPanel orderingPanel;
	private GroupingAttributesPanel groupingAttributesPanel;
	
	public ReportQueryGroupingOrderingPanel(PropertyValueModel queryHolder, WorkbenchContextHolder contextHolder) {
		super(contextHolder);
		this.queryHolder = buildReportQueryHolder(queryHolder);
		initializeLayout();
	}
	
	private PropertyValueModel buildReportQueryHolder(PropertyValueModel queryHolder) {
		return new FilteringPropertyValueModel(queryHolder) {
			protected boolean accept(Object value) {
				return value instanceof MWReportQuery;
			}
		};
	}

	private void initializeLayout() {
		GridBagConstraints constraints = new GridBagConstraints();
		
		this.groupingAttributesPanel = 
			new GroupingAttributesPanel(this.queryHolder, getWorkbenchContextHolder());
		
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(0, 0, 0, 0);
		add(this.groupingAttributesPanel, constraints);

		
		this.orderingPanel = 
		    new OrderingAttributesPanel(
		        this.queryHolder, 
		        Filter.NULL_INSTANCE, 
		        buildOrderingChooseableFilter(), 
		        getWorkbenchContextHolder()) {
		    AttributeItemDialog buildAttributeItemDialog(MWAttributeItem item) {
				return new ReportQueryOrderingAttributeDialog(
						(MWReportQuery) getQuery(),
						(MWReportOrderingItem) item,
						getTraversableFilter(), 
						getChooseableFilter(), 
						getWorkbenchContext());
           }
		};
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(0, 0, 0, 0);
		add(this.orderingPanel, constraints);
		
		
	}
	
	private Filter buildOrderingChooseableFilter() {
		return Filter.NULL_INSTANCE;
	}
	
	void selectOrderingItem(Ordering orderingItem) {
		this.orderingPanel.select(orderingItem);
	}
	
	void selectGroupingItem(MWGroupingItem groupingItem) {
		this.groupingAttributesPanel.select(groupingItem);
	}
}
