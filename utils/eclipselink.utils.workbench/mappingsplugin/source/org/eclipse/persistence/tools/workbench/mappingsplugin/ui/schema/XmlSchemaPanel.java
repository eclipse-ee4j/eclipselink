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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.schema;

import org.eclipse.persistence.tools.workbench.framework.context.DefaultWorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.TabbedPropertiesPage;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;


final class XmlSchemaPanel
    extends TabbedPropertiesPage
{
    /**
     * unlike most properties pages, this properties page is constructed
     * with a node because it is never pre-built - a new page is built for
     * every workbench window so we can preserve the expansion state
     * of the XML schema tree
     */
    XmlSchemaPanel(XmlSchemaNode node, WorkbenchContext context) {
        // build the "holders" here because the values will never change
        super(new SimplePropertyValueModel(node), new DefaultWorkbenchContextHolder(context));
    }

    protected void initializeTabs() {
        this.addTab(this.buildSchemaDocumentInfoPanel(), "SCHEMA_DOCUMENT_INFO_PANEL_TAB");
        this.addTab(this.buildSchemaStructurePanel(), "SCHEMA_STRUCTURE_PANEL_TAB");
    }

    private SchemaDocumentInfoPanel buildSchemaDocumentInfoPanel() {
        return new SchemaDocumentInfoPanel(this.getNodeHolder(), getWorkbenchContextHolder());
    }

    private SchemaStructurePanel buildSchemaStructurePanel() {
        return new SchemaStructurePanel(this.getNodeHolder(), getWorkbenchContextHolder());
    }
}
