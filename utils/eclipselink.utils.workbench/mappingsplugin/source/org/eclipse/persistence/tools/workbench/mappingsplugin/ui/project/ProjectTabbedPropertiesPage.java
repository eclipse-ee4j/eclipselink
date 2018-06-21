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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project;

import java.awt.Component;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.TabbedPropertiesPage;


public abstract class ProjectTabbedPropertiesPage extends TabbedPropertiesPage {

    protected ProjectTabbedPropertiesPage(WorkbenchContext context) {
        super(context.buildExpandedResourceRepositoryContext(UiProjectBundle.class));
    }

    protected Component buildProjectGeneralPropertiesPage() {
        return new ProjectGeneralPropertiesPage(getNodeHolder(), getWorkbenchContextHolder());
    }

    protected Component buildProjectDefaultsPropertiesPage() {
        return new ProjectDefaultsPropertiesPage(getNodeHolder(), getWorkbenchContextHolder());
    }

}
