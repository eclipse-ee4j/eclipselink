/*
 * Copyright (c) 2008, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.tools.workbench.scplugin.ui.session.login;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.TabbedPropertiesPage;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;

public class XMLLoginTabbedPropertiesPage extends TabbedPropertiesPage {

    public XMLLoginTabbedPropertiesPage( PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder) {
        super( nodeHolder, contextHolder);
    }

    protected Component buildOptionsPropertiesPage() {
        return new XMLOptionsPropertiesPage( getNodeHolder(), getWorkbenchContextHolder());
    }

    protected String buildOptionsPropertiesPageTitle() {
        return "LOGIN_OPTIONS_TAB_TITLE";
    }

    protected Component buildSequencingPropertiesPage() {
        return new SequencingPropertiesPage( getNodeHolder(), getWorkbenchContextHolder());
    }

    protected String buildSequencingPropertiesPageTitle() {
        return "LOGIN_SEQUENCING_TAB_TITLE";
    }

    protected Component buildTitlePanel() {
        return new JComponent() { };
    }

    protected JTabbedPane buildTabbedPane()
    {
        JTabbedPane tabbedPane = super.buildTabbedPane();
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        return tabbedPane;
    }

    protected void initializeTabs() {
        addTab( buildOptionsPropertiesPage(), buildOptionsPropertiesPageTitle());
    }
}
