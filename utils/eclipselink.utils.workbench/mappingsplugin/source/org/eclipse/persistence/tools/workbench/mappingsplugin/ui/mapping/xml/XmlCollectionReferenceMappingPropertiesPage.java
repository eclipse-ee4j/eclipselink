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
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.framework.ui.view.TitledPropertiesPage;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.UiCommonBundle;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.MappingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.MethodAccessingPanel;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.UiMappingBundle;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.xml.UiXmlBundle;
import org.eclipse.persistence.tools.workbench.uitools.chooser.ListChooser;


public final class XmlCollectionReferenceMappingPropertiesPage extends TitledPropertiesPage {

    private static final Class[] REQUIRED_RESOURCE_BUNDLES = new Class[] {
        UiCommonBundle.class,
        UiXmlBundle.class,
        UiMappingBundle.class,
        UiMappingXmlBundle.class
    };

    // **************** Constructors ******************************************

    public XmlCollectionReferenceMappingPropertiesPage(WorkbenchContext context) {
        super(context);
    }

    @Override
    protected Component buildPage() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        GridBagConstraints constraints = new GridBagConstraints();
        Insets offset = BorderFactory.createTitledBorder("m").getBorderInsets(this);
        offset.left += 5; offset.right += 5;

        // Reference Descriptor chooser
        JComponent referenceDescriptorWidgets =
            buildLabeledComponent("REFERENCE_DESCRIPTOR_CHOOSER_LABEL", buildReferenceDescriptorChooser());

        constraints.gridx       = 0;
        constraints.gridy       = 0;
        constraints.gridwidth   = 1;
        constraints.gridheight  = 1;
        constraints.weightx     = 1;
        constraints.weighty     = 0;
        constraints.fill        = GridBagConstraints.HORIZONTAL;
        constraints.anchor      = GridBagConstraints.CENTER;
        constraints.insets      = new Insets(0, offset.left, 0, offset.right);

        panel.add(referenceDescriptorWidgets, constraints);
        addHelpTopicId(referenceDescriptorWidgets, "mapping.referenceDescriptor");

        // Foreign Keys sub-pane
        FieldPairsSubPane fieldPairsSubPane = new FieldPairsSubPane();

        constraints.gridx       = 0;
        constraints.gridy       = 1;
        constraints.gridwidth   = 1;
        constraints.gridheight  = 1;
        constraints.weightx     = 1;
        constraints.weighty     = 0;
        constraints.fill        = GridBagConstraints.HORIZONTAL;
        constraints.anchor      = GridBagConstraints.CENTER;
        constraints.insets      = new Insets(5, 0, 0, 0);

        panel.add(fieldPairsSubPane, constraints);
        addPaneForAlignment(fieldPairsSubPane);

        // Use Method accessing widgets
        MethodAccessingPanel methodAccessingPanel =
            new MethodAccessingPanel(getSelectionHolder(), getWorkbenchContextHolder());

        constraints.gridx       = 0;
        constraints.gridy       = 2;
        constraints.gridwidth   = 1;
        constraints.gridheight  = 1;
        constraints.weightx     = 1;
        constraints.weighty     = 0;
        constraints.fill        = GridBagConstraints.HORIZONTAL;
        constraints.anchor      = GridBagConstraints.CENTER;
        constraints.insets      = new Insets(5, 0, 0, 0);
        panel.add(methodAccessingPanel, constraints);
        addPaneForAlignment(methodAccessingPanel);

        // read only check box
        constraints.gridx        = 0;
        constraints.gridy        = 3;
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
        constraints.gridy       = 4;
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
        constraints.gridy        = 5;
        constraints.gridwidth    = 1;
        constraints.gridheight    = 1;
        constraints.weightx        = 1;
        constraints.weighty        = 1;
        constraints.fill        = GridBagConstraints.HORIZONTAL;
        constraints.anchor        = GridBagConstraints.FIRST_LINE_START;
        constraints.insets        = new Insets(5, offset.left, 0, offset.right);
        panel.add(commentPanel, constraints);
        this.addHelpTopicId(commentPanel, "mapping.comment");

        this.addHelpTopicId(panel, "mapping.xmlCollectionReferenceMapping");

        return panel;
    }

    private Component buildReadOnlyCheckBox() {
        return MappingComponentFactory.buildReadOnlyCheckBox(this.getSelectionHolder(), this.getApplicationContext());
    }

    private ListChooser buildReferenceDescriptorChooser() {
        return MappingComponentFactory.buildReferenceDescriptorChooser(getSelectionHolder(), getWorkbenchContextHolder());
    }

    private class FieldPairsSubPane extends AbstractSubjectPanel {
        private FieldPairsSubPane() {
            super(
                XmlCollectionReferenceMappingPropertiesPage.this.getSelectionHolder(),
                XmlCollectionReferenceMappingPropertiesPage.this.getWorkbenchContextHolder()
            );
        }

        @Override
        protected void initializeLayout() {
            this.setLayout(new GridBagLayout());

            GridBagConstraints constraints = new GridBagConstraints();

            // Field Pairs label
            JLabel fieldPairsLabel = this.buildFieldPairsLabel();
            constraints.gridx       = 0;
            constraints.gridy       = 1;
            constraints.gridwidth   = 2;
            constraints.gridheight  = 1;
            constraints.weightx     = 0;
            constraints.weighty     = 0;
            constraints.fill        = GridBagConstraints.NONE;
            constraints.anchor      = GridBagConstraints.LINE_START;
            constraints.insets      = new Insets(5, 0, 0, 0);
            this.add(fieldPairsLabel, constraints);
            this.addAlignLeft(fieldPairsLabel);
            addHelpTopicId(fieldPairsLabel, "mapping.xmlCollectionReference.fieldPairs");

            // Field Pairs table
            AbstractPanel fieldPairsPanel = this.buildFieldPairsPanel();
            constraints.gridx       = 0;
            constraints.gridy       = 2;
            constraints.gridwidth   = 2;
            constraints.gridheight  = 1;
            constraints.weightx     = 1;
            constraints.weighty     = 1;
            constraints.fill        = GridBagConstraints.BOTH;
            constraints.anchor      = GridBagConstraints.CENTER;
            constraints.insets      = new Insets(1, 0, 0, 0);
            this.add(fieldPairsPanel, constraints);
            this.addPaneForAlignment(fieldPairsPanel);
            addHelpTopicId(fieldPairsPanel, "mapping.xmlCollectionReference.fieldPairs");
        }

        private JLabel buildFieldPairsLabel() {
            JLabel label = this.buildLabel("FIELD_PAIR_TABLE");
            this.addHelpTopicId(label, "mapping.xmlCollectionReference.fieldPairs");
            return label;
        }

        private AbstractPanel buildFieldPairsPanel() {
            AbstractPanel panel =
                new XmlReferenceMappingFieldPairsPanel(
                    XmlCollectionReferenceMappingPropertiesPage.this.getSelectionHolder(),
                    this.getWorkbenchContextHolder()
                );
            this.addHelpTopicId(panel, "mapping.xmlCollectionReference.foreignKeys");
            return panel;
        }

        @Override
        public void setEnabled(boolean enabled) {
            super.setEnabled(enabled);

            for (int i = this.getComponentCount() - 1; i >= 0; i --) {
                this.getComponent(i).setEnabled(enabled);
            }
        }
    }

}
