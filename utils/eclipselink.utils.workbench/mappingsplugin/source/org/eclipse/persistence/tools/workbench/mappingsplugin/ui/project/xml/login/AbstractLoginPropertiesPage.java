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

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;


abstract class AbstractLoginPropertiesPage extends ScrollablePropertiesPage
{
    /**
     * Creates a new <code>AbstractEisLoginPropertiesPage</code>.
     *
     * @param nodeHolder The holder of {@link DatabaseSessionNode}
     */
    public AbstractLoginPropertiesPage(PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder)
    {
        super(nodeHolder, contextHolder);
    }

    /**
     * Creates the selection holder that will hold the user object to be edited
     * by this page.
     *
     * @param nodeHolder The holder of {@link DatabaseSessionNode}
     * @return The <code>PropertyValueModel</code> containing the {@link EisLoginAdapter}
     * to be edited by this page
     */
    protected PropertyValueModel buildSelectionHolder()
    {
        return super.buildSelectionHolder();
//        return new PropertyAspectAdapter(super.buildSelectionHolder(),
//                                                    DatabaseSessionAdapter.LOGIN_CONFIG_PROPERTY)
//        {
//            protected Object getValueFromSubject()
//            {
//                DatabaseSessionAdapter session = (DatabaseSessionAdapter) subject;
//                return session.getLogin();
//            }
//        };
    }
}
