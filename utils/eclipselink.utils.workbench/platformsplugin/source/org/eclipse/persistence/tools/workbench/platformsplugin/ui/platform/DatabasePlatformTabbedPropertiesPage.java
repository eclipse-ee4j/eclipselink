/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.platformsplugin.ui.platform;

import java.awt.Component;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.TabbedPropertiesPage;


/**
 * just a couple of tabs:
 *     - general properties
 *     - JDBC mappings
 */
final class DatabasePlatformTabbedPropertiesPage extends TabbedPropertiesPage {

    public DatabasePlatformTabbedPropertiesPage(WorkbenchContext context) {
        super(context);
    }

    protected void initializeTabs() {
        this.addTab(this.buildGeneralPropertiesPage(), this.buildGeneralPropertiesPageTitle());
        this.addTab(this.buildJDBCPropertiesPage(), this.buildJDBCPropertiesPageTitle());
    }

    private Component buildGeneralPropertiesPage() {
        return new DatabasePlatformGeneralPropertiesPage(this.getNodeHolder(), this.getWorkbenchContextHolder());
    }

    private String buildGeneralPropertiesPageTitle() {
        return "DATABASE_PLATFORM_GENERAL_TAB_TITLE";
    }

    private Component buildJDBCPropertiesPage() {
        return new DatabasePlatformJDBCPropertiesPage(this.getNodeHolder(), this.getWorkbenchContextHolder());
    }

    private String buildJDBCPropertiesPageTitle() {
        return "DATABASE_PLATFORM_JDBC_MAPPINGS_TAB_TITLE";
    }

}
