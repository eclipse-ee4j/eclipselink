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
package org.eclipse.persistence.tools.workbench.scplugin.ui.pool.write;

// JDK
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.scplugin.ui.pool.basic.ConnectionCountPane;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;

// Mapping Workbench

/**
 * This pane shows the General properties for Write Connection Pool.
 * <p>
 * Here the layout:
 * <pre>
 * _______________________________
 * |                             |
 * | --------------------------- |
 * | |                         | |
 * | | {@link ConnectionPane}          | |
 * | |                         | |
 * | --------------------------- |
 * |                             |
 * -------------------------------</pre>
 *
 * @see org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ConnectionPoolAdapter
 *
 * @version 10.1.3
 * @author Pascal Filion
 */
public class PoolGeneralPropertiesPage extends ScrollablePropertiesPage
{
    /**
     * Creates a new <code>PoolGeneralPropertiesPage</code>.
     *
     * @param nodeHolder The holder of {@link PoolNode}
     */
    public PoolGeneralPropertiesPage( PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder)
    {
        super( nodeHolder, contextHolder);
        addHelpTopicId(this, "connectionPool.write.general");
    }

    /**
     * Creates the panel that holds all the widgets of this page.
     *
     * @return The fully initialized container
     */
    protected Component buildPage()
    {
        GridBagConstraints constraints = new GridBagConstraints();

        // Create the container
        JPanel panel = new JPanel( new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Connection Count pane
        ConnectionCountPane connectionCountPane = new ConnectionCountPane(getSelectionHolder(), getApplicationContext());

        constraints.gridx       = 0;
        constraints.gridy       = 0;
        constraints.gridwidth   = 1;
        constraints.gridheight  = 1;
        constraints.weightx     = 1;
        constraints.weighty     = 1;
        constraints.fill        = GridBagConstraints.NONE;
        constraints.anchor      = GridBagConstraints.FIRST_LINE_START;
        constraints.insets      = new Insets(0, 0, 0, 0);

        panel.add(connectionCountPane, constraints);

        return panel;
    }
}
