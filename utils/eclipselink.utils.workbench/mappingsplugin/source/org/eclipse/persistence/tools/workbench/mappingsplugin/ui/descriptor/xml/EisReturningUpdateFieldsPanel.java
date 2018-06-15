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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.xml;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWEisDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWEisReturningPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXmlField;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.ReturningUpdateFieldsPanel;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.xml.XpathChooserDialog;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;



final class EisReturningUpdateFieldsPanel
    extends ReturningUpdateFieldsPanel
{
    /**
     * Creates a new <code>ReturningUpdateFieldsPanel</code>.
     *
     */
    EisReturningUpdateFieldsPanel( PropertyValueModel subjectHolder, WorkbenchContextHolder contextHolder) {
        super( subjectHolder, contextHolder);
    }

    protected void addField() {
        MWEisReturningPolicy returningPolicy = (MWEisReturningPolicy) this.returningPolicy();
        MWXmlField xmlField = returningPolicy.buildEmptyUpdateField();
        XpathChooserDialog.promptToSelectXpath(xmlField, this.getWorkbenchContext());

        if (xmlField.isSpecified()) {
            returningPolicy.addUpdateField(xmlField);
        }
    }

    private MWEisDescriptor eisDescriptor() {
        return (MWEisDescriptor) returningPolicy().getOwningDescriptor();
    }
}
