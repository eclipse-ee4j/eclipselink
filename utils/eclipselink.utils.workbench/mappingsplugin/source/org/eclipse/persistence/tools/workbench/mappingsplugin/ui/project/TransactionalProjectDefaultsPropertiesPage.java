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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project;

import java.awt.Component;
import java.util.List;
import javax.swing.BorderFactory;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProjectDefaultsPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWTransactionalProjectDefaultsPolicy;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;


/**
 * UI editor for transactional based project defaults.
 *
 * @version 10.1.3
 */
public class TransactionalProjectDefaultsPropertiesPage extends ProjectDefaultsPropertiesPage
{
    public TransactionalProjectDefaultsPropertiesPage(
            PropertyValueModel projectNodeHolder,
            WorkbenchContextHolder contextHolder)
    {
        super(projectNodeHolder, contextHolder);
    }

    protected List buildAdvancedPropertyHolders()
    {
        List holders = super.buildAdvancedPropertyHolders();
        holders.add(new UIAdvancedPolicyHolder(MWTransactionalProjectDefaultsPolicy.EVENTS_POLICY,
                                                        "TRANSACTIONAL_PROJECT_DEFAULTS_POLICY_EVENTS_POLICY"));
        holders.add(new UIAdvancedPolicyHolder(MWTransactionalProjectDefaultsPolicy.RETURNING_POLICY,
                                                        "TRANSACTIONAL_PROJECT_DEFAULTS_POLICY_RETURNING_POLICY"));

        return holders;
    }

    protected Component buildCachingDefaultsPanel()
    {
        ProjectCachingPolicyPanel panel = new ProjectCachingPolicyPanel
        (
            buildCachingPolicyHolder(),
            getWorkbenchContextHolder()
        );

        panel.setBorder(BorderFactory.createCompoundBorder
        (
            buildTitledBorder("TRANSACTIONAL_PROJECT_DEFAULTS_PROJECT_CACHING"),
            BorderFactory.createEmptyBorder(0, 5, 5, 5)
        ));

        return panel;
    }

    private PropertyValueModel buildCachingPolicyHolder()
    {
        return new TransformationPropertyValueModel(getSelectionHolder())
        {
            protected Object transform(Object value)
            {
                if (value == null)
                    return null;

                return ((MWProjectDefaultsPolicy) value).getCachingPolicy();
            }
        };
    }
}
