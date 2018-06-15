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
package org.eclipse.persistence.tools.workbench.scplugin.ui.session.login;

// JDK
import java.awt.Component;
import javax.swing.BorderFactory;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.scplugin.ui.tools.PropertyPane;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;

// Mapping Workbench

/**
 * This page shows the {@link PropertyPane} which edits the login's properties.
 * <p>
 * Here the layout of this pane:
 * <pre>
 * _______________________
 * |                     |
 * |    {@link PropertyPane}     |
 * |                     |
 * -----------------------</pre>
 *
 * @see LoginAdapter
 *
 * @version 10.1.3
 * @author Pascal Filion
 */
public class LoginPropertiesPropertiesPage extends AbstractLoginPropertiesPage
{
    /**
     * Creates a new <code>LoginPropertiesPropertiesPage</code>.
     *
     * @param nodeHolder The holder of {@link LoginAdapter}
     */
    public LoginPropertiesPropertiesPage(PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder)
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
