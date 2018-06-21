/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.db;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.TabbedPropertiesPage;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWReference;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.UiCommonBundle;


public class TableTabbedPropertiesPage extends TabbedPropertiesPage {

    private ColumnsPropertiesPage columnsPropertiesPage;
    private ReferencesPropertiesPage referencesPropertiesPage;

    // this value is queried reflectively during plug-in initialization
    private static final Class[] REQUIRED_RESOURCE_BUNDLES = new Class[] {
        UiCommonBundle.class,
        UiDbBundle.class
    };


    public TableTabbedPropertiesPage(WorkbenchContext context) {
        super(context);
    }

    protected void initializeTabs() {
        this.columnsPropertiesPage = new ColumnsPropertiesPage(getNodeHolder(), getWorkbenchContextHolder());
        this.referencesPropertiesPage = new ReferencesPropertiesPage(getNodeHolder(), getWorkbenchContextHolder());
        addTab(this.columnsPropertiesPage, "COLUMNS_TAB_TEXT");
        addTab(this.referencesPropertiesPage, "REFERENCES_TAB_TEXT");
    }

    void selectColumn(MWColumn column) {
        setSelectedTab(this.columnsPropertiesPage);
        this.columnsPropertiesPage.setSelectedColumn(column);
    }

    void selectReference(MWReference reference) {
        setSelectedTab(this.referencesPropertiesPage);
        this.referencesPropertiesPage.setSelectedReference(reference);
    }

}
