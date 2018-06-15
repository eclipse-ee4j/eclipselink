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
package org.eclipse.persistence.tools.workbench.framework.ui.view;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;


/**
 * This subclass of PropertiesPage adds a scroll pane to the page.
 * This is most useful for the individual pages in a TabbedPropertiesPage.
 *
 * Subclasses must implement #createPage() and return the component
 * to be placed in the scroll pane.
 *
 * @see TabbedPropertiesPage
 */
public abstract class ScrollablePropertiesPage
    extends AbstractPropertiesPage
{

    protected ScrollablePropertiesPage(PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder) {
        super(nodeHolder, contextHolder);
    }

    protected void initializeLayout() {
        JScrollPane scrollPane = new JScrollPane(this.buildPage());
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setViewportBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);
    }

    protected abstract Component buildPage();

}
