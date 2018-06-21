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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor;

import javax.swing.BorderFactory;
import javax.swing.JTabbedPane;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPropertiesPage;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethod;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;


public final class DescriptorClassInfoTabbedPropertiesPage extends AbstractPropertiesPage {

    private JTabbedPane tabbedPane;
    private MethodsPropertiesPage methodsPropertiesPage;

    public DescriptorClassInfoTabbedPropertiesPage(PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder) {
        super(nodeHolder, contextHolder);
    }

    protected void initializeLayout() {
        this.tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        this.tabbedPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        this.methodsPropertiesPage = new MethodsPropertiesPage(getNodeHolder(), getWorkbenchContextHolder());

        this.tabbedPane.addTab(resourceRepository().getString("CLASS_TAB"), new ClassPropertiesPage(getNodeHolder(), getWorkbenchContextHolder()));
        tabbedPane.addTab(resourceRepository().getString("ATTRIBUTES_TAB"), new ClassAttributesPropertiesPage(getNodeHolder(), getWorkbenchContextHolder()));
        this.tabbedPane.addTab(resourceRepository().getString("METHODS_TAB"), this.methodsPropertiesPage);

        add(this.tabbedPane);
    }

    void selectMethod(MWMethod method) {
        this.tabbedPane.setSelectedComponent(this.methodsPropertiesPage);
        this.methodsPropertiesPage.selectMethod(method);
    }

}
