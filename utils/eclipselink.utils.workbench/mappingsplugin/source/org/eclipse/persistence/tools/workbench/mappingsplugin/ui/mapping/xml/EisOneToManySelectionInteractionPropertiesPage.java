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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml;

import java.awt.Component;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWEisReferenceMapping;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.xml.InteractionPanel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;


final class EisOneToManySelectionInteractionPropertiesPage extends ScrollablePropertiesPage {

    EisOneToManySelectionInteractionPropertiesPage(PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder) {
        super(nodeHolder, contextHolder);
        addHelpTopicId(this, "mappings.eis.selectionInteraction");
    }

    protected Component buildPage(){
        return new InteractionPanel(getApplicationContext(), buildInteractionAdapter(), "mappings.eis.selectionInteraction");
    }

    private PropertyValueModel buildInteractionAdapter() {
        return new PropertyAspectAdapter(getSelectionHolder(), MWEisReferenceMapping.SELECTION_INTERACTION_PROPERTY) {
            protected Object getValueFromSubject() {
                return ((MWEisReferenceMapping) this.subject).getSelectionInteraction();
            }
        };
    }

}
