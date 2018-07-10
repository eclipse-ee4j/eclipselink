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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.framework.ui.view.TitledPropertiesPage;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlFragmentCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.UiCommonBundle;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.MappingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.MethodAccessingPanel;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.UiMappingBundle;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.xml.UiXmlBundle;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;


public final class XmlFragmentCollectionMappingPropertiesPage extends TitledPropertiesPage {

    @SuppressWarnings("unused")
    private static final Class[] REQUIRED_RESOURCE_BUNDLES = new Class[] {
        UiCommonBundle.class,
        UiXmlBundle.class,
        UiMappingBundle.class,
        UiMappingXmlBundle.class
    };

    // **************** Constructors ******************************************

    public XmlFragmentCollectionMappingPropertiesPage(WorkbenchContext context) {
        super(context);
    }

    // **************** Initialization ****************************************

    @Override
    protected Component buildPage() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        GridBagConstraints constraints = new GridBagConstraints();
        Insets offset = BorderFactory.createTitledBorder("m").getBorderInsets(this);
        offset.left += 5; offset.right += 5;

        // xml field panel
        AbstractPanel pane = this.buildXmlFieldPanel();
        constraints.gridx      = 0;
        constraints.gridy      = 0;
        constraints.gridwidth  = 1;
        constraints.gridheight = 1;
        constraints.weightx    = 1;
        constraints.weighty    = 0;
        constraints.fill       = GridBagConstraints.HORIZONTAL;
        constraints.anchor     = GridBagConstraints.CENTER;
        constraints.insets     = new Insets(0, 0, 0, 0);
        panel.add(pane, constraints);
        this.addPaneForAlignment(pane);

        // method accessing panel
        pane = this.buildMethodAccessingPanel();
        constraints.gridx      = 0;
        constraints.gridy      = 1;
        constraints.gridwidth  = 1;
        constraints.gridheight = 1;
        constraints.weightx    = 1;
        constraints.weighty    = 0;
        constraints.fill       = GridBagConstraints.HORIZONTAL;
        constraints.anchor     = GridBagConstraints.CENTER;
        constraints.insets     = new Insets(0, 0, 0, 0);
        panel.add(pane, constraints);
        this.addPaneForAlignment(pane);

        // read only check box
        constraints.gridx        = 0;
        constraints.gridy        = 2;
        constraints.gridwidth    = 1;
        constraints.gridheight    = 1;
        constraints.weightx        = 1;
        constraints.weighty        = 0;
        constraints.fill        = GridBagConstraints.NONE;
        constraints.anchor        = GridBagConstraints.LINE_START;
        constraints.insets        = new Insets(5, 5, 0, 0);
        panel.add(this.buildReadOnlyCheckBox(), constraints);

        // Collection Options Advanced button
        JComponent advancedPanel = MappingComponentFactory.buildCollectionContainerPolicyOptionsBrowser(
            getWorkbenchContextHolder(),
            getSelectionHolder(),
            "mapping.directCollection.options");
        constraints.gridx       = 0;
        constraints.gridy       = 3;
        constraints.gridwidth   = 1;
        constraints.gridheight  = 1;
        constraints.weightx     = 1;
        constraints.weighty     = 0;
        constraints.fill        = GridBagConstraints.HORIZONTAL;
        constraints.anchor      = GridBagConstraints.LINE_START;
        constraints.insets      = new Insets(5, offset.left, 0, 0);
        panel.add(advancedPanel, constraints);

        // comment
        JComponent commentPanel = SwingComponentFactory.buildCommentPanel(getSelectionHolder(), resourceRepository());
        constraints.gridx        = 0;
        constraints.gridy        = 4;
        constraints.gridwidth    = 1;
        constraints.gridheight    = 1;
        constraints.weightx        = 1;
        constraints.weighty        = 1;
        constraints.fill        = GridBagConstraints.HORIZONTAL;
        constraints.anchor        = GridBagConstraints.FIRST_LINE_START;
        constraints.insets        = new Insets(5, offset.left, 0, offset.right);
        panel.add(commentPanel, constraints);
        this.addHelpTopicId(commentPanel, "mapping.comment");

        this.addHelpTopicId(panel, "mapping.xmlFragmentCollectionMapping");

        return panel;
    }

    private AbstractXmlFieldPanel buildXmlFieldPanel() {
        return new BasicXmlFieldPanel(this.buildXmlFieldHolder(), this.getWorkbenchContextHolder());
    }

    private ValueModel buildXmlFieldHolder() {
        return new PropertyAspectAdapter(this.getSelectionHolder()) {
            @Override
            protected Object getValueFromSubject() {
                return ((MWXmlFragmentCollectionMapping) this.subject).getXmlField();
            }
        };
    }

    private MethodAccessingPanel buildMethodAccessingPanel() {
        return new MethodAccessingPanel(this.getSelectionHolder(), this.getWorkbenchContextHolder());
    }

    private Component buildReadOnlyCheckBox() {
        return MappingComponentFactory.buildReadOnlyCheckBox(this.getSelectionHolder(), this.getApplicationContext());
    }
}
