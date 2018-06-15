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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping;

import java.awt.BorderLayout;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPropertiesPage;


/**
 * simple panel containing an icon and the attribute name and type
 */
public final class UnmappedMappingPropertiesPage
    extends AbstractPropertiesPage
{

    // this value is queried reflectively during plug-in initialization
    private static final Class[] REQUIRED_RESOURCE_BUNDLES = new Class[0];

    public UnmappedMappingPropertiesPage(WorkbenchContext context) {
        super(context);
    }

    protected void initializeLayout() {
        this.add(this.buildTitlePanel(), BorderLayout.PAGE_START);
    }

}
