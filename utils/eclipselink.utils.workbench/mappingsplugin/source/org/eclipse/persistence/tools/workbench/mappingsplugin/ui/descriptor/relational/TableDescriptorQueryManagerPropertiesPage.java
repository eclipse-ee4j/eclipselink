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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.relational;

import javax.swing.BorderFactory;
import javax.swing.JTabbedPane;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPropertiesPage;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWQueryKey;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.relational.RelationalCustomSqlPropertiesPage;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.relational.RelationalDescriptorQueriesSettingsPage;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.relational.RelationalDescriptorQueryKeysPropertiesPage;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.relational.RelationalQueriesPropertiesPage;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;



final class TableDescriptorQueryManagerPropertiesPage extends AbstractPropertiesPage {
	
    private JTabbedPane tabbedPane;
    private RelationalDescriptorQueryKeysPropertiesPage queryKeysPage;
    
	TableDescriptorQueryManagerPropertiesPage(PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder) {
		super(nodeHolder, contextHolder);
	}
	

	protected void initializeLayout() {
		this.tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        this.tabbedPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));		

        this.queryKeysPage = new RelationalDescriptorQueryKeysPropertiesPage(getNodeHolder(), getWorkbenchContextHolder());
        
        this.tabbedPane.addTab(resourceRepository().getString("NAMES_QUERIES_TAB"), new RelationalQueriesPropertiesPage(getNodeHolder(), getWorkbenchContextHolder()));
        this.tabbedPane.addTab(resourceRepository().getString("CUSTOM_SQL_TAB"), new RelationalCustomSqlPropertiesPage(getNodeHolder(), getWorkbenchContextHolder()));
        this.tabbedPane.addTab(resourceRepository().getString("SETTINGS_TAB"), new RelationalDescriptorQueriesSettingsPage(getNodeHolder(), getWorkbenchContextHolder()));
        this.tabbedPane.addTab(resourceRepository().getString("RELATIONAL_DESCRIPTOR_QUERY_KEYS_TAB"), this.queryKeysPage);

		add(this.tabbedPane);
	}
	//TODO order index for all tabs?
//	protected int getOrderIndex() {
//		return this.QUERIES_PAGE_ORDER_INDEX;
//	}
    
    public void selectQueryKey(MWQueryKey queryKey) {
        this.tabbedPane.setSelectedComponent(this.queryKeysPage);
        this.queryKeysPage.selectQueryKey(queryKey);
    }
	
}
