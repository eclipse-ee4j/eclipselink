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
package org.eclipse.persistence.tools.workbench.scplugin.ui.pool.basic;

// JDK
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ConnectionPoolAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseLoginAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.EISLoginAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.LoginAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.ui.login.EisLoginPane;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;

// Mapping Workbench

/**
 * This page shows the information regarding the Database login which is
 * specific for {@link org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseLoginAdapter DatabaseLoginAdapter}.
 * <p>
 * Here the layout:
 * <pre>
 * _______________________________________
 * |                                     |
 * | ----------------------------------- |
 * | |                                 | |
 * | | {@link EisLoginPane}                       | |
 * | |                                 | |
 * | ----------------------------------- |
 * |                                     |
 * ---------------------------------------</pre>
 *
 * @see DatabaseLoginAdapter
 * @see LoginAdapter
 *
 * @version 10.0.3
 * @author Pascal Filion
 */
public class EisPoolLoginPropertiesPage extends ScrollablePropertiesPage
{
    /**
     * Creates a new <code>RdbmsPoolLoginPropertiesPage</code>.
     *
     * @param nodeHolder
     */
    public EisPoolLoginPropertiesPage( PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder)
    {
        super(nodeHolder, contextHolder);
        addHelpTopicId(this, "connectionPool.login.eis");
    }

    private ButtonModel buildExternalConnectionPoolingCheckBoxModel()
    {
        return new CheckBoxModelAdapter(buildExternalConnectionPoolingHolder());
    }

    private PropertyValueModel buildExternalConnectionPoolingHolder()
    {
        return new PropertyAspectAdapter(buildLoginHolder(), EISLoginAdapter.EXTERNAL_CONNECTION_POOLING_PROPERTY)
        {
            @Override
            protected Object getValueFromSubject()
            {
                EISLoginAdapter adapter = (EISLoginAdapter) subject;
                return adapter.usesExternalConnectionPooling();
            }

            @Override
            protected void setValueOnSubject(Object value)
            {
                EISLoginAdapter adapter = (EISLoginAdapter) subject;
                adapter.setExternalConnectionPooling((Boolean) value);
            }
        };
    }

    private ButtonModel buildExternalTransactionControllerCheckBoxModel()
    {
        return new CheckBoxModelAdapter(buildExternalTransactionControllerHolder());
    }

    private PropertyValueModel buildExternalTransactionControllerHolder()
    {
        return new PropertyAspectAdapter(buildLoginHolder(), EISLoginAdapter.EXTERNAL_TRANSACTION_CONTROLLER_PROPERTY)
        {
            @Override
            protected Object getValueFromSubject()
            {
                EISLoginAdapter adapter = (EISLoginAdapter) subject;
                return adapter.usesExternalTransactionController();
            }

            @Override
            protected void setValueOnSubject(Object value)
            {
                EISLoginAdapter adapter = (EISLoginAdapter) subject;
                adapter.setUsesExternalTransactionController((Boolean) value);
            }
        };
    }

    /**
     * Creates the selection holder that will hold the user object to be edited
     * by this page.
     *
     * @return The <code>PropertyValueModel</code> containing the {@link EisLoginAdapter}
     * to be edited by the {@link EisLoginPane}
     */
    protected PropertyValueModel buildLoginHolder()
    {
        return new PropertyAspectAdapter(getSelectionHolder(), ConnectionPoolAdapter.LOGIN_CONFIG_PROPERTY)
        {
            protected Object getValueFromSubject()
            {
                ConnectionPoolAdapter pool = (ConnectionPoolAdapter) subject;
                return pool.getLogin();
            }
        };
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
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Login pane
        EisLoginPane loginPane = new EisLoginPane(buildLoginHolder(), getWorkbenchContextHolder());

        constraints.gridx       = 0;
        constraints.gridy       = 0;
        constraints.gridwidth   = 3;
        constraints.gridheight  = 1;
        constraints.weightx     = 1;
        constraints.weighty     = 0;
        constraints.fill        = GridBagConstraints.HORIZONTAL;
        constraints.anchor      = GridBagConstraints.CENTER;
        constraints.insets      = new Insets(0, 0, 0, 0);

        panel.add(loginPane, constraints);

        // External Connection Pooling check box
        JCheckBox externalConnectionPoolingCheckBox = buildCheckBox
        (
            "RDBMS_POOL_LOGIN_PANE_EXTERNAL_CONNECTION_POOLING_CHECKBOX",
            buildExternalConnectionPoolingCheckBoxModel()
        );

        constraints.gridx      = 0;
        constraints.gridy      = 2;
        constraints.gridwidth  = 1;
        constraints.gridheight = 1;
        constraints.weightx    = 1;
        constraints.weighty    = 0;
        constraints.fill       = GridBagConstraints.NONE;
        constraints.anchor     = GridBagConstraints.LINE_START;
        constraints.insets     = new Insets(5, 0, 0, 0);

        panel.add(externalConnectionPoolingCheckBox, constraints);

        // External Transaction Controller check box
        JCheckBox externalTransactionControllerCheckBox = buildCheckBox
        (
            "RDBMS_POOL_LOGIN_PANE_EXTERNAL_TRANSACTION_CONTROLLER_CHECKBOX",
            buildExternalTransactionControllerCheckBoxModel()
        );

        constraints.gridx      = 0;
        constraints.gridy      = 3;
        constraints.gridwidth  = 1;
        constraints.gridheight = 1;
        constraints.weightx    = 1;
        constraints.weighty    = 1;
        constraints.fill       = GridBagConstraints.NONE;
        constraints.anchor     = GridBagConstraints.FIRST_LINE_START;
        constraints.insets     = new Insets(0, 0, 0, 0);

        panel.add(externalTransactionControllerCheckBox, constraints);

        return panel;
    }
}
