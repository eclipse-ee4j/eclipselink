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
package org.eclipse.persistence.tools.workbench.scplugin.ui.session.basic;

// JDK
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;

// Mapping Workbench

/**
 * This page shows the Mapping Project information, which is ...
 * <p>
 * Here the layout of this page:<br>
 * <pre>
 * ____________________________
 * |                          |
 * |  {@link SessionProjectPane}      |
 * |                          |
 * ----------------------------</pre>
 *
 * @see DatabaseSessionAdapter
 *
 * @version 10.1.3
 * @author Pascal Filion
 */
public final class SessionProjectPropertiesPage extends ScrollablePropertiesPage
{
    /**
     * Creates a new <code>SessionGeneralPropertiesPage</code>.
     *
     * @param nodeHolder The holder of {@link SessionNode}
     * @param contextHolder The holder of {@link org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext}
     */
    public SessionProjectPropertiesPage(PropertyValueModel nodeHolder,
                                                    WorkbenchContextHolder contextHolder)
    {
        super(nodeHolder, contextHolder);
        addHelpTopicId(this, "session.general.project");
    }

    /**
     * Initializes the layout of this pane.
     *
     * @return The container with all its widgets
     */
    protected Component buildPage()
    {
        GridBagConstraints constraints = new GridBagConstraints();

        // Create the container
        JPanel container = new JPanel(new GridBagLayout());
        container.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Project sub-panel
        SessionProjectPane generalPane = new SessionProjectPane(getSelectionHolder(), getWorkbenchContextHolder());

        constraints.gridx      = 0;
        constraints.gridy      = 0;
        constraints.gridwidth  = 1;
        constraints.gridheight = 1;
        constraints.weightx    = 1;
        constraints.weighty    = 1;
        constraints.fill       = GridBagConstraints.HORIZONTAL;
        constraints.anchor     = GridBagConstraints.PAGE_START;
        constraints.insets     = new Insets(0, 0, 0, 0);

        container.add(generalPane, constraints);

        return container;
    }
}
