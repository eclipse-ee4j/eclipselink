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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query;

import java.util.ListIterator;

import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWAbstractQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryParameter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWQueryItem;
import org.eclipse.persistence.tools.workbench.uitools.app.ItemPropertyListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;


/**
 * The section that manages the query parameters list.
 */
public final class ParametersQuickViewSection extends AbstractQuickViewSection {
    private ValueModel queryHolder;

    private QueriesPropertiesPage queriesPropertiesPage;

    public ParametersQuickViewSection(QueriesPropertiesPage page, ResourceRepository resourceRepository, ValueModel queryHolder) {
        super(resourceRepository, "QUICK_VIEW_PARAMETERS_LABEL", "QUICK_VIEW_PARAMETERS_ACCESSIBLE");
        this.queryHolder = queryHolder;
        this.queriesPropertiesPage = page;
    }

    public ListValueModel buildItemsHolder() {
        return new TransformationListValueModelAdapter(buildListValueModel()) {
            protected Object transformItem(Object item) {
                return ParametersQuickViewSection.this.queriesPropertiesPage.buildQueryParameterQuickViewItem((MWQueryItem) item);
            }
        };
    }

    private ListValueModel buildListValueModel() {
        return new ItemPropertyListValueModelAdapter(
                        buildParameterListHolder(),
                        MWQueryParameter.NAME_PROPERTY,
                        MWQueryParameter.TYPE_PROPERTY);
    }

    private ListValueModel buildParameterListHolder() {
        return new ListAspectAdapter(this.queryHolder, MWAbstractQuery.PARAMETERS_LIST) {
            protected ListIterator getValueFromSubject() {
                return ((MWAbstractQuery) this.subject).parameters();
            }

            protected int sizeFromSubject() {
                return ((MWAbstractQuery) this.subject).parametersSize();
            }
        };
    }

    public void select() {
        this.queriesPropertiesPage.selectGeneralPanel();
    }
}
