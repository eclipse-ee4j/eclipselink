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
package org.eclipse.persistence.tools.workbench.scplugin.ui.session;

// Mapping Workbench
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPropertiesPage;
import org.eclipse.persistence.tools.workbench.scplugin.ui.session.login.EisLoginTabbedPropertiesPage;


/**
 * @version 10.1.3
 * @author Pascal Filion
 */
final class EisSessionTabbedPropertiesPage extends SessionTabbedPropertiesPage
{

    EisSessionTabbedPropertiesPage(WorkbenchContext context)
    {
        super(context);
    }

    protected AbstractPropertiesPage buildLoginPropertiesPage()
    {
        return new EisLoginTabbedPropertiesPage(getNodeHolder(), getWorkbenchContextHolder());
    }

    protected String buildLoginPropertiesPageTitle()
    {
        return "SESSION_LOGIN_TAB_TITLE";
    }

    protected void initializeTabs()
    {
        addTab(buildGeneralPropertiesPage(),    buildGeneralPropertiesPageTitle());
        addTab(buildOptionsPropertiesPage(),    buildOptionsPropertiesPageTitle());
        addTab(buildLoginPropertiesPage(),      buildLoginPropertiesPageTitle());
        addTab(buildLoggingPropertiesPage(),    buildLoggingPropertiesPageTitle());
        addTab(buildClusteringPropertiesPage(), buildClusteringPropertiesPageTitle());
        addTab(buildConnectionPolicyVisibleHolder(), 5, buildConnectionPolicyPropertiesPage(), buildConnectionPolicyPropertiesPageTitle());
    }
}
