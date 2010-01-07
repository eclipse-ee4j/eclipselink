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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.xml;

import javax.swing.BorderFactory;
import javax.swing.JTabbedPane;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPropertiesPage;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWEisDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.xml.EisCustomCallsPropertiesPage;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.xml.EisDescriptorQueriesSettingsPage;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.xml.EisQueriesPropertiesPage;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;


public class EisDescriptorQueryManagerPropertiesPage
	extends AbstractPropertiesPage 
{	
	private ValueModel queryManagerHolder;
	
	
	public EisDescriptorQueryManagerPropertiesPage(PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder) {
		super(nodeHolder, contextHolder);
	}
	
	protected void initialize(PropertyValueModel selectionNodeHolder) {
		super.initialize(selectionNodeHolder);
		this.queryManagerHolder = this.buildQueryManagerHolder();
	}
	
	protected ValueModel buildQueryManagerHolder() {
		return new PropertyAspectAdapter(this.getSelectionHolder()) {
			protected Object getValueFromSubject() {
				return ((MWEisDescriptor) this.subject).getQueryManager();
			}
		};
	}
	
	protected void initializeLayout() {
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
		tabbedPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));		
		
		tabbedPane.addTab(
			this.resourceRepository().getString("NAMES_QUERIES_TAB"), 
			new EisQueriesPropertiesPage(this.getNodeHolder(), this.getWorkbenchContextHolder())
		);
        tabbedPane.addTab(
        	this.resourceRepository().getString("CUSTOM_CALL_TAB"), 
			new EisCustomCallsPropertiesPage(this.queryManagerHolder, this.getWorkbenchContextHolder())
		);
        tabbedPane.addTab(
        	this.resourceRepository().getString("SETTINGS_TAB"), 
			new EisDescriptorQueriesSettingsPage(this.getNodeHolder(), this.getWorkbenchContextHolder())
		);
		
		this.add(tabbedPane);
	}
	
//	TODO order index for all tabs?
//	protected int getOrderIndex() {
//		return this.QUERIES_PAGE_ORDER_INDEX;
//	}
}
