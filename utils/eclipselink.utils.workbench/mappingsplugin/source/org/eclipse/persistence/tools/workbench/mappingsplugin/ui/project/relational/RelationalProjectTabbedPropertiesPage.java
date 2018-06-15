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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.relational;

import java.awt.Component;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.UiCommonBundle;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.ProjectTabbedPropertiesPage;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.UiProjectBundle;


//TODO update so that the displayed title includes the type of project
// Employee (EIS Project) ~KFM
final class RelationalProjectTabbedPropertiesPage extends ProjectTabbedPropertiesPage {

    // this value is queried reflectively during plug-in initialization
    private static final Class[] REQUIRED_RESOURCE_BUNDLES = new Class[] {
        UiCommonBundle.class,
        UiProjectBundle.class
    };


    RelationalProjectTabbedPropertiesPage(WorkbenchContext context) {
        super(context);
    }

    protected void initializeTabs() {
        addTab(buildProjectGeneralPropertiesPage(), "GENERAL_TAB_TITLE");
        addTab(buildProjectSequencingPropertiesPage(), "SEQUENCING_TAB_TITLE");
        addTab(buildProjectDefaultsPropertiesPage(), "DEFAULTS_TAB_TITLE");
        addTab(buildProjectOptionsPropertiesPage(), "OPTIONS_TAB_TITLE");
    }

    private Component buildProjectSequencingPropertiesPage() {
        return new RelationalProjectSequencingPropertiesPage(getNodeHolder(), getWorkbenchContextHolder());
    }

    protected Component buildProjectOptionsPropertiesPage() {
        return new RelationalProjectOptionsPropertiesPage(getNodeHolder(), getWorkbenchContextHolder());
    }

    protected Component buildProjectDefaultsPropertiesPage()
    {
        return new RelationalProjectDefaultsPropertiesPage(getNodeHolder(), getWorkbenchContextHolder());
    }
}
