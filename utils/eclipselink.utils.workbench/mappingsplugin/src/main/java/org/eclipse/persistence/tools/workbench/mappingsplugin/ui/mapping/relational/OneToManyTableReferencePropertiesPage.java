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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.relational;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;


/**
 * @version 10.1.3
 * @author Pascal Filion
 */
final class OneToManyTableReferencePropertiesPage extends ScrollablePropertiesPage
{
    OneToManyTableReferencePropertiesPage(PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder)
    {
        super(nodeHolder, contextHolder);
    }

    protected Component buildPage()
    {
        GridBagConstraints constraints = new GridBagConstraints();

        JPanel container = new JPanel(new GridBagLayout());
        container.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        OneToManyTableReferencePanel referencePanel = new OneToManyTableReferencePanel
        (
            getSelectionHolder(),
            getWorkbenchContextHolder()
        );

        constraints.gridx       = 0;
        constraints.gridy       = 2;
        constraints.gridwidth   = 1;
        constraints.gridheight  = 1;
        constraints.weightx     = 1;
        constraints.weighty     = 1;
        constraints.fill        = GridBagConstraints.HORIZONTAL;
        constraints.anchor      = GridBagConstraints.PAGE_START;
        constraints.insets      = new Insets(0, 0, 0, 0);

        container.add(referencePanel, constraints);
        addPaneForAlignment(referencePanel);

        return container;
    }
}
