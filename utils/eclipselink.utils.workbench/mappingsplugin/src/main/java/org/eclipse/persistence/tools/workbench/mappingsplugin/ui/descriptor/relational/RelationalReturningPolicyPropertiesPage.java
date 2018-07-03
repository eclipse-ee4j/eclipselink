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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.relational;


import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.ReturningPolicyPropertiesPage;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.relational.RelationalReturningInsertFieldsPanel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;



final class RelationalReturningPolicyPropertiesPage extends ReturningPolicyPropertiesPage
{
    public static final int EDITOR_WEIGHT = 12;

    /**
     * Creates a new <code>ReturningPolicyPropertiesPage</code>.
     */
    RelationalReturningPolicyPropertiesPage( PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder) {
        super( nodeHolder, contextHolder);
        addHelpTopicId( this, "descriptor.relational.returningPolicy");
    }


    protected AbstractPanel insertFieldsPanel(PropertyValueModel returningPolicyHolder) {
        return new RelationalReturningInsertFieldsPanel(returningPolicyHolder, getWorkbenchContextHolder());
    }

    protected AbstractPanel updateFieldsPanel(PropertyValueModel returningPolicyHolder) {
        return new RelationalReturningUpdateFieldsPanel(returningPolicyHolder, getWorkbenchContextHolder());
    }

}
