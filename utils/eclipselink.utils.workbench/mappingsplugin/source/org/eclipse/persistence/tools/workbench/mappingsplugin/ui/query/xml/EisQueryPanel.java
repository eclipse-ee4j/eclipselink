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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.xml;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.xml.MWAbstractEisReadQuery;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.QueryGeneralPanel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;



/**
 * DescriptorPropertiesQueryPage holds on to QueryPanel
 * QueryPanel is just used to set up the tab panels for a query
 *
 */
 final class EisQueryPanel
    extends AbstractPanel
{

    private PropertyValueModel queryHolder;

    private JTabbedPane queryPropertiesPane;
    private QueryGeneralPanel queryGeneralPanel;

    EisQueryPanel(PropertyValueModel queryHolder,
                  ObjectListSelectionModel querySelectionModel,
                  WorkbenchContextHolder contextHolder) {
        super(contextHolder);
        this.queryHolder = queryHolder;
        initializeLayout(querySelectionModel);
    }


    private void initializeLayout(ObjectListSelectionModel querySelectionModel) {
        GridBagConstraints constraints = new GridBagConstraints();

        this.queryPropertiesPane = new JTabbedPane();
        this.queryGeneralPanel = new QueryGeneralPanel(this.queryHolder, querySelectionModel, getWorkbenchContextHolder());
        JPanel queryCallPanel = new InteractionPanel(getApplicationContext(), buildInteractionHolder(), buildComponentEnablerBooleanHolder(), "descriptor.eis.query");
        JPanel queryOptionsPanel = new EisQueryOptionsPanel(this.queryHolder,  getWorkbenchContextHolder());

        this.queryPropertiesPane.addTab(resourceRepository().getString("GENERAL_TAB"), this.queryGeneralPanel);
        this.queryPropertiesPane.addTab(resourceRepository().getString("CALL_TAB"), queryCallPanel);
        this.queryPropertiesPane.addTab(resourceRepository().getString("OPTIONS_TAB"), queryOptionsPanel);

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(0, 0, 0, 0);
        add(this.queryPropertiesPane, constraints);
    }

    private PropertyValueModel buildInteractionHolder() {
        return new PropertyAspectAdapter(this.queryHolder) {
            protected Object getValueFromSubject() {
                return ((MWAbstractEisReadQuery) this.subject).getEisInteraction();
            }
        };
    }

    protected PropertyValueModel buildComponentEnablerBooleanHolder() {
        return new PropertyAspectAdapter(this.queryHolder) {
            protected Object getValueFromSubject() {
                return Boolean.valueOf(true);
            }
        };
    }

    protected QueryGeneralPanel getQueryGeneralPanel() {
        return this.queryGeneralPanel;
    }

    protected JTabbedPane getQueryTabbedPane() {
        return this.queryPropertiesPane;
    }
}
