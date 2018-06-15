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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.relational;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;

import org.eclipse.persistence.tools.workbench.framework.context.DefaultWorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractDialog;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWCompoundExpression;




/**
 * This dialog is launched from the NamedQueries Format tab if the user chooses to specify
 * an expression for their query instead of writing the SQL themselves
 */
final class ExpressionBuilderDialog
    extends AbstractDialog
{

    private MWCompoundExpression expression;
    private int selectionRow;

    ExpressionBuilderDialog(MWCompoundExpression expression, int selectionRow, WorkbenchContext context) {
        super(context);
        this.expression = expression;
        this.selectionRow = selectionRow;
    }

    public String helpTopicId() {
        return "dialog.expressionBuilder";
    }

    protected Component buildMainPanel() {
        JPanel panel = new JPanel(new GridBagLayout());

        setTitle(resourceRepository().getString("EXPRESSION_BUILDER_DIALOG.title"));

        GridBagConstraints constraints = new GridBagConstraints();

        ExpressionTreePanel expressionTreePanel = new ExpressionTreePanel(new DefaultWorkbenchContextHolder(getWorkbenchContext()), helpTopicId());
        expressionTreePanel.setExpression(expression, selectionRow);
        constraints.gridx        = 0;
        constraints.gridy        = 0;
        constraints.gridwidth    = 1;
        constraints.gridheight    = 1;
        constraints.weightx        = 1;
        constraints.weighty        = 1;
        constraints.fill        = GridBagConstraints.BOTH;
        constraints.anchor        = GridBagConstraints.CENTER;
        constraints.insets        = new Insets(5, 5, 5, 5);
        panel.add(expressionTreePanel, constraints);

        return panel;
    }
}
