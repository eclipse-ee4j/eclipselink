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

import java.awt.Component;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ComponentBuilder;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.UiCommonBundle;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.EventsPolicyPropertiesPage;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.MappingDescriptorTabbedPropertiesPage;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.UiDescriptorBundle;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.UiQueryBundle;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.xml.UiXmlBundle;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;


final class EisCompositeDescriptorTabbedPropertiesPage extends MappingDescriptorTabbedPropertiesPage {

    // this value is queried reflectively during plug-in initialization
    private static final Class[] REQUIRED_RESOURCE_BUNDLES = new Class[] {
        UiCommonBundle.class,
        UiXmlBundle.class,
        UiDescriptorBundle.class,
        UiQueryBundle.class,
        UiDescriptorXmlBundle.class
    };


    public EisCompositeDescriptorTabbedPropertiesPage(WorkbenchContext context) {
        super(context);
    }

    protected void initializeTabs() {
        super.initializeTabs();
        addTab(new EisCompositeDescriptorInfoPropertiesPage(getNodeHolder(), getWorkbenchContextHolder()), "DESCRIPTOR_INFO_TAB");
        addTab(buildClassInfoPropertiesPage(), "CLASS_INFO_TAB");
        addTab(buildEventsPolicyValueModel(), EventsPolicyPropertiesPage.EDITOR_WEIGHT, buildEventsPolicyPropertiesPage(), "XML_DESCRIPTOR_EVENTS_TAB");
    }

    protected Component buildInheritancePolicyPropertiesPage() {
        return new EisInheritancePolicyPropertiesPage(getNodeHolder(), getWorkbenchContextHolder());
    }

    protected ComponentBuilder buildInheritancePolicyPageBuilder()
    {
        return new ComponentBuilder()
        {
            private XmlInheritancePolicyPropertiesPage inheritancePolicyPage;

            public Component buildComponent(PropertyValueModel nodeHolder)
            {
                if (inheritancePolicyPage == null)
                {
                    inheritancePolicyPage = new EisInheritancePolicyPropertiesPage(nodeHolder, getWorkbenchContextHolder());
                }
                return inheritancePolicyPage;
            }
        };
    }
}
