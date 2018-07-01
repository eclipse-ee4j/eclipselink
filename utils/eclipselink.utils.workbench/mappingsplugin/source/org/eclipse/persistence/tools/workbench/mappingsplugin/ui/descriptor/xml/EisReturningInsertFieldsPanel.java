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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.xml;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWEisDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWEisReturningPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWEisReturningPolicyInsertFieldReturnOnlyFlag;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.ReturningInsertFieldsPanel;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.xml.XpathChooserDialog;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;


final class EisReturningInsertFieldsPanel
    extends ReturningInsertFieldsPanel
{
    EisReturningInsertFieldsPanel( PropertyValueModel subjectHolder, WorkbenchContextHolder contextHolder) {
        super( subjectHolder, contextHolder);
    }

    protected void addField() {
        MWEisReturningPolicy returningPolicy = (MWEisReturningPolicy) this.returningPolicy();
        MWEisReturningPolicyInsertFieldReturnOnlyFlag insertField =
            returningPolicy.buildEmptyInsertFieldReadOnlyFlag();

        XpathChooserDialog.promptToSelectXpath(
            insertField.getXmlField(),
            this.getWorkbenchContext()
        );

        if (insertField.getXmlField().isSpecified()) {
            returningPolicy.addInsertFieldReadOnlyFlag(insertField);
        }
    }

    private MWEisDescriptor eisDescriptor() {
        return (MWEisDescriptor) returningPolicy().getOwningDescriptor();
    }
}

