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

import java.awt.Component;

import javax.swing.BorderFactory;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;


public class EisLoginPropertiesPropertiesPage extends AbstractLoginPropertiesPage
{
    /**
     * Creates a new <code>LoginPropertiesPropertiesPage</code>.
     *
     * @param nodeHolder The holder of {@link LoginAdapter}
     */
    public EisLoginPropertiesPropertiesPage(PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder)
    {
        super(nodeHolder, contextHolder);
    }

    /**
     * Initializes the layout of this pane.
     *
     * @return The container with all its widgets
     */
    protected Component buildPage()
    {
        PropertyPane propertyPane = new PropertyPane(getSelectionHolder(), getWorkbenchContextHolder());
        propertyPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        addHelpTopicId(propertyPane, "session.login.properties");
        return propertyPane;
    }
}
