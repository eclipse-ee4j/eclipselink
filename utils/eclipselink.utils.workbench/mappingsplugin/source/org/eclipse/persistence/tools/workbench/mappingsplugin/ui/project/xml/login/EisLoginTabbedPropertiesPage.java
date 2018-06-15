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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.xml.login;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JTabbedPane;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPropertiesPage;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;


public class EisLoginTabbedPropertiesPage extends AbstractPropertiesPage
{
    public EisLoginTabbedPropertiesPage(PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder)
    {
        super(nodeHolder, contextHolder);
    }

    protected Component buildConnectionPropertiesPage()
    {
        return new EisConnectionPropertiesPage(this.getNodeHolder(), getWorkbenchContextHolder());
    }

    protected String buildConnectionPropertiesPageTitle()
    {
        return "LOGIN_CONNECTION_TAB_TITLE";
    }

    protected Component buildPropertiesPropertiesPage()
    {
        return new EisLoginPropertiesPropertiesPage(this.getNodeHolder(), getWorkbenchContextHolder());
    }

    protected String buildPropertiesPropertiesPageTitle()
    {
        return "LOGIN_PROPERTIES_TAB_TITLE";
    }

    protected void initializeLayout()
    {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(tabbedPane, BorderLayout.CENTER);

        tabbedPane.addTab(resourceRepository().getString(buildConnectionPropertiesPageTitle()), buildConnectionPropertiesPage());
        tabbedPane.addTab(resourceRepository().getString(buildPropertiesPropertiesPageTitle()), buildPropertiesPropertiesPage());
    }
}
