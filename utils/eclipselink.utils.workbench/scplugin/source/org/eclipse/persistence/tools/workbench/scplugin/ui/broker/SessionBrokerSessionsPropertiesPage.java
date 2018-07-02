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
package org.eclipse.persistence.tools.workbench.scplugin.ui.broker;

// JDK
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JPanel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;

// Mapping Workbench

/**
 * This pane shows the list of {@link SessionsAdapter}s contained in a
 * {@link SessionBrokerAdapter}.
 * <p>
 * Here the layout:
 * <pre>
 * _______________________________
 * |                             |
 * |  {@link SessionsListPane}           |
 * |                             |
 * -------------------------------</pre>
 *
 * @see SessionBrokerAdapter
 *
 * @version 10.1.3
 * @author Tran Le
 */
public final class SessionBrokerSessionsPropertiesPage extends ScrollablePropertiesPage
{
    /**
     * Creates a new <code>SessionBrokerSessionsPropertiesPage</code>.
     *
     * @param nodeHolder The holder of {@link SessionBrokerAdapter}
     * @param contextHolder
     */
    SessionBrokerSessionsPropertiesPage(PropertyValueModel nodeHolder,
                                                    WorkbenchContextHolder contextHolder)
    {
        super(nodeHolder, contextHolder);
        addHelpTopicId(this, "session.broker.general.sessions");
    }

    /**
     * Initializes the content of this page.
     *
     * @return The fully initialized container with all its widgets
     */
    protected Component buildPage()
    {
        GridBagConstraints constraints = new GridBagConstraints();

        // Create the container
        JPanel panel = new JPanel(new GridBagLayout());

        // Sessions list panel
        SessionsListPane sessionListPane = new SessionsListPane(getSelectionHolder(), getWorkbenchContextHolder());

        constraints.gridx      = 0;
        constraints.gridy      = 2;
        constraints.gridwidth  = 3;
        constraints.gridheight = 1;
        constraints.weightx    = 1;
        constraints.weighty    = 1;
        constraints.fill       = GridBagConstraints.HORIZONTAL;
        constraints.anchor     = GridBagConstraints.PAGE_START;
        constraints.insets     = new Insets(5, 5, 5, 5);

        panel.add(sessionListPane, constraints);

        return panel;
    }
}
