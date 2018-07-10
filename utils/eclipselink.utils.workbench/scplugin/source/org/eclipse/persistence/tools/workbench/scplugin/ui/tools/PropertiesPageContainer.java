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
package org.eclipse.persistence.tools.workbench.scplugin.ui.tools;

// JDK
import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JPanel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;

// Mapping Workbench

/**
 * This page shows another component.
 * <p>
 * Here the layout:
 * <pre>
 * _______________________
 * |                     |
 * |    <A sub-pane>     |
 * |                     |
 * -----------------------</pre>
 *
 * @see ConnectionPoolAdapter
 *
 * @version 10.0.3
 * @author Pascal Filion
 */
public class PropertiesPageContainer extends ScrollablePropertiesPage
{
    /**
     * Keeps a reference so we can add the sub-pane.
     */
    private JPanel container;

    /**
     * Creates a new <code>InfoPropertiesPage</code>.
     *
     * @param nodeHolder The holder of any node
     * @param subPane The pane to be shown by this properties page
     */
    public PropertiesPageContainer(PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder,
                                             Component subPane)
    {
        super(nodeHolder, contextHolder);
        container.add(subPane, BorderLayout.CENTER);
    }

    /**
     * Initializes the layout of this pane.
     *
     * @return The container with all its widgets
     */
    protected Component buildPage()
    {
        container = new JPanel(new BorderLayout());
        return container;
    }
}
