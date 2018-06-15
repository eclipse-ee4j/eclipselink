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

import javax.swing.JCheckBox;

import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWAbstractReadQuery;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;



public class QueryComponentFactory extends SwingComponentFactory {

    //************ refresh identity map **********

    public static JCheckBox buildRefreshIdentityMapCheckBox(PropertyValueModel queryHolder, ResourceRepository resourceRepository) {
        return buildCheckBox("REFRESH_IDENTITY_MAP_RESULTS_CHECK_BOX",new CheckBoxModelAdapter(buildRefreshIdentityMapHolder(queryHolder)), resourceRepository);
    }

    private static PropertyValueModel buildRefreshIdentityMapHolder(PropertyValueModel queryHolder) {
        return new PropertyAspectAdapter(queryHolder, MWAbstractReadQuery.REFRESH_IDENTITY_MAP_RESULT_PROPERTY) {
            protected Object getValueFromSubject() {
                return Boolean.valueOf(((MWAbstractReadQuery) this.subject).isRefreshIdentityMapResult());
            }

            protected void setValueOnSubject(Object value) {
                ((MWAbstractReadQuery) this.subject).setRefreshIdentityMapResult(((Boolean) value).booleanValue());
            }
        };
    }


}
