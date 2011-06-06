/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.tools.beans;

import java.util.*;

import javax.swing.*;

import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;

/**
 * Reusable visual component for editing expressions.
 */
public class ExpressionPanel extends JPanel {
    protected Expression expression;
    protected ClassDescriptor descriptor;
    private JButton ivjAndButton = null;
    private JComboBox ivjAttributeCombo = null;
    private JLabel ivjAttributeLabel = null;
    private JButton ivjClearButton = null;
    private JTree ivjExpressionTree = null;
    private JScrollPane ivjExpressionTreeScroll = null;
    private JButton ivjNotBuuton = null;
    private JComboBox ivjOperatorCombo = null;
    private JLabel ivjOperatorLabel = null;
    private JButton ivjOrButton = null;
    private JToolBar ivjToolBar = null;
    private JLabel ivjValueLabel = null;
    private JTextField ivjValueText = null;
    IvjEventHandler ivjEventHandler = new IvjEventHandler();

    /**
     * Constructor
     */
    public ExpressionPanel() {
        super();
        initialize();
    }

    /**
     * ExpressionPanel constructor comment.
     * @param layout java.awt.LayoutManager
     */
    public ExpressionPanel(java.awt.LayoutManager layout) {
        super(layout);
    }

    /**
     * ExpressionPanel constructor comment.
     * @param layout java.awt.LayoutManager
     * @param isDoubleBuffered boolean
     */
    public ExpressionPanel(java.awt.LayoutManager layout, 
                           boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
    }

    /**
     * ExpressionPanel constructor comment.
     * @param isDoubleBuffered boolean
     */
    public ExpressionPanel(boolean isDoubleBuffered) {
        super(isDoubleBuffered);
    }

    public void andExpression() {
        Expression newExpression = buildExpressionNode();
        if (newExpression == null) {
            return;
        }

        if (getExpression() == null) {
            setExpression(newExpression);
        } else {
            setExpression(getExpression().and(newExpression));
        }
    }

    public Expression buildExpressionNode() {
        ExpressionBuilder builder;
        if (getExpression() == null) {
            builder = new ExpressionBuilder();
        } else {
            builder = getExpression().getBuilder();
        }

        String attribute = (String)getAttributeCombo().getSelectedItem();
        if (attribute == null) {
            return null;
        }
        String operator = (String)getOperatorCombo().getSelectedItem();
        String method = ExpressionNode.getMethod(operator);
        String value = getValueText().getText();
        Class[] types = new Class[1];
        types[0] = Object.class;
        Object[] arguments = new Object[1];
        arguments[0] = value;
        Expression queryKey = builder.get(attribute);
        try {
            return (Expression)queryKey.getClass().getMethod(method, 
                                                             types).invoke(queryKey, 
                                                                           arguments);
        } catch (Exception exception) {
            try {
                types[0] = String.class;
                return (Expression)queryKey.getClass().getMethod(method, 
                                                                 types).invoke(queryKey, 
                                                                               arguments);
            } catch (Exception e) {
                handleException(e);
                return null;
            }
        }
    }

    public void clearExpression() {
        setExpression(null);
    }

    /**
     * connEtoC1:  (OrButton.action.actionPerformed(java.awt.event.ActionEvent) --> ExpressionPanel.orExpression()V)
     * @param arg1 java.awt.event.ActionEvent
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    void connEtoC1(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.orExpression();
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC2:  (AndButton.action.actionPerformed(java.awt.event.ActionEvent) --> ExpressionPanel.andExpression()V)
     * @param arg1 java.awt.event.ActionEvent
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    void connEtoC2(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.andExpression();
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC3:  (NotBuuton.action.actionPerformed(java.awt.event.ActionEvent) --> ExpressionPanel.notExpression()V)
     * @param arg1 java.awt.event.ActionEvent
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    void connEtoC3(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.notExpression();
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC4:  (ClearButton.action.actionPerformed(java.awt.event.ActionEvent) --> ExpressionPanel.clearExpression()V)
     * @param arg1 java.awt.event.ActionEvent
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    void connEtoC4(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.clearExpression();
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * Return the AddButton property value.
     * @return javax.swing.JButton
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JButton getAndButton() {
        if (ivjAndButton == null) {
            try {
                ivjAndButton = new javax.swing.JButton();
                ivjAndButton.setName("AndButton");
                ivjAndButton.setText("And");
                ivjAndButton.setBackground(java.awt.SystemColor.control);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjAndButton;
    }

    /**
     * Return the AttributeCombo property value.
     * @return javax.swing.JComboBox
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JComboBox getAttributeCombo() {
        if (ivjAttributeCombo == null) {
            try {
                ivjAttributeCombo = new javax.swing.JComboBox();
                ivjAttributeCombo.setName("AttributeCombo");
                ivjAttributeCombo.setBackground(java.awt.SystemColor.window);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjAttributeCombo;
    }

    /**
     * Return the AttributeLabel property value.
     * @return javax.swing.JLabel
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JLabel getAttributeLabel() {
        if (ivjAttributeLabel == null) {
            try {
                ivjAttributeLabel = new javax.swing.JLabel();
                ivjAttributeLabel.setName("AttributeLabel");
                ivjAttributeLabel.setText("Attribute:");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjAttributeLabel;
    }

    /**
     * Return the RemoveButton property value.
     * @return javax.swing.JButton
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JButton getClearButton() {
        if (ivjClearButton == null) {
            try {
                ivjClearButton = new javax.swing.JButton();
                ivjClearButton.setName("ClearButton");
                ivjClearButton.setText("Clear");
                ivjClearButton.setBackground(java.awt.SystemColor.control);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjClearButton;
    }

    public ClassDescriptor getDescriptor() {
        return descriptor;
    }

    public Expression getExpression() {
        return expression;
    }

    /**
     * Return the ExpressionTree property value.
     * @return javax.swing.JTree
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JTree getExpressionTree() {
        if (ivjExpressionTree == null) {
            try {
                ivjExpressionTree = new javax.swing.JTree();
                ivjExpressionTree.setName("ExpressionTree");
                //ivjExpressionTree.setBorder(new javax.swing.plaf.basic.BasicBorders.MarginBorder());
                ivjExpressionTree.setBounds(0, 0, 76, 36);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjExpressionTree;
    }

    /**
     * Return the ExpressionTreeScroll property value.
     * @return javax.swing.JScrollPane
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JScrollPane getExpressionTreeScroll() {
        if (ivjExpressionTreeScroll == null) {
            try {
                ivjExpressionTreeScroll = new javax.swing.JScrollPane();
                ivjExpressionTreeScroll.setName("ExpressionTreeScroll");
                getExpressionTreeScroll().setViewportView(getExpressionTree());
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjExpressionTreeScroll;
    }

    /**
     * Return the NotBuuton property value.
     * @return javax.swing.JButton
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JButton getNotBuuton() {
        if (ivjNotBuuton == null) {
            try {
                ivjNotBuuton = new javax.swing.JButton();
                ivjNotBuuton.setName("NotBuuton");
                ivjNotBuuton.setText("Not");
                ivjNotBuuton.setBackground(java.awt.SystemColor.control);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjNotBuuton;
    }

    /**
     * Return the OperatorCombo property value.
     * @return javax.swing.JComboBox
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JComboBox getOperatorCombo() {
        if (ivjOperatorCombo == null) {
            try {
                ivjOperatorCombo = new javax.swing.JComboBox();
                ivjOperatorCombo.setName("OperatorCombo");
                ivjOperatorCombo.setBackground(java.awt.SystemColor.window);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjOperatorCombo;
    }

    /**
     * Return the OperatorLabel property value.
     * @return javax.swing.JLabel
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JLabel getOperatorLabel() {
        if (ivjOperatorLabel == null) {
            try {
                ivjOperatorLabel = new javax.swing.JLabel();
                ivjOperatorLabel.setName("OperatorLabel");
                ivjOperatorLabel.setText("Operator:");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjOperatorLabel;
    }

    /**
     * Return the OrButton property value.
     * @return javax.swing.JButton
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JButton getOrButton() {
        if (ivjOrButton == null) {
            try {
                ivjOrButton = new javax.swing.JButton();
                ivjOrButton.setName("OrButton");
                ivjOrButton.setText("Or");
                ivjOrButton.setBackground(java.awt.SystemColor.control);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjOrButton;
    }

    /**
     * Return the ToolBar property value.
     * @return javax.swing.JToolBar
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JToolBar getToolBar() {
        if (ivjToolBar == null) {
            try {
                ivjToolBar = new javax.swing.JToolBar();
                ivjToolBar.setName("ToolBar");
                ivjToolBar.setBackground(java.awt.SystemColor.control);
                getToolBar().add(getAndButton(), getAndButton().getName());
                getToolBar().add(getOrButton(), getOrButton().getName());
                getToolBar().add(getNotBuuton(), getNotBuuton().getName());
                ivjToolBar.addSeparator();
                getToolBar().add(getClearButton(), getClearButton().getName());
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjToolBar;
    }

    /**
     * Return the ValueLabel property value.
     * @return javax.swing.JLabel
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JLabel getValueLabel() {
        if (ivjValueLabel == null) {
            try {
                ivjValueLabel = new javax.swing.JLabel();
                ivjValueLabel.setName("ValueLabel");
                ivjValueLabel.setText("Value:");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjValueLabel;
    }

    /**
     * Return the ValueText property value.
     * @return javax.swing.JTextField
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JTextField getValueText() {
        if (ivjValueText == null) {
            try {
                ivjValueText = new javax.swing.JTextField();
                ivjValueText.setName("ValueText");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjValueText;
    }

    /**
     * Called whenever the part throws an exception.
     * @param exception java.lang.Throwable
     */
    private void handleException(Throwable exception) {
        MessageDialog.displayException(exception, this);
    }

    /**
     * Initializes connections
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    void initConnections() throws java.lang.Exception {
        // user code begin {1}
        // user code end
        getOrButton().addActionListener(ivjEventHandler);
        getAndButton().addActionListener(ivjEventHandler);
        getNotBuuton().addActionListener(ivjEventHandler);
        getClearButton().addActionListener(ivjEventHandler);
    }

    /**
     * Initialize the class.
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    void initialize() {
        try {
            // user code begin {1}
            // user code end
            setName("ExpressionPanel");
            setLayout(new java.awt.GridBagLayout());
            setBackground(java.awt.SystemColor.control);
            setSize(742, 472);

            java.awt.GridBagConstraints constraintsAttributeLabel = 
                new java.awt.GridBagConstraints();
            constraintsAttributeLabel.gridx = 0;
            constraintsAttributeLabel.gridy = 0;
            constraintsAttributeLabel.anchor = 
                    java.awt.GridBagConstraints.SOUTHWEST;
            constraintsAttributeLabel.insets = new java.awt.Insets(2, 2, 0, 2);
            add(getAttributeLabel(), constraintsAttributeLabel);

            java.awt.GridBagConstraints constraintsValueText = 
                new java.awt.GridBagConstraints();
            constraintsValueText.gridx = 2;
            constraintsValueText.gridy = 1;
            constraintsValueText.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsValueText.weightx = 0.2;
            constraintsValueText.ipadx = 20;
            constraintsValueText.insets = new java.awt.Insets(0, 2, 2, 2);
            add(getValueText(), constraintsValueText);

            java.awt.GridBagConstraints constraintsAttributeCombo = 
                new java.awt.GridBagConstraints();
            constraintsAttributeCombo.gridx = 0;
            constraintsAttributeCombo.gridy = 1;
            constraintsAttributeCombo.fill = 
                    java.awt.GridBagConstraints.HORIZONTAL;
            constraintsAttributeCombo.weightx = 0.2;
            constraintsAttributeCombo.insets = new java.awt.Insets(0, 2, 2, 2);
            add(getAttributeCombo(), constraintsAttributeCombo);

            java.awt.GridBagConstraints constraintsOperatorCombo = 
                new java.awt.GridBagConstraints();
            constraintsOperatorCombo.gridx = 1;
            constraintsOperatorCombo.gridy = 1;
            constraintsOperatorCombo.fill = 
                    java.awt.GridBagConstraints.HORIZONTAL;
            constraintsOperatorCombo.weightx = 0.2;
            constraintsOperatorCombo.insets = new java.awt.Insets(0, 2, 2, 2);
            add(getOperatorCombo(), constraintsOperatorCombo);

            java.awt.GridBagConstraints constraintsOperatorLabel = 
                new java.awt.GridBagConstraints();
            constraintsOperatorLabel.gridx = 1;
            constraintsOperatorLabel.gridy = 0;
            constraintsOperatorLabel.anchor = 
                    java.awt.GridBagConstraints.SOUTHWEST;
            constraintsOperatorLabel.insets = new java.awt.Insets(2, 2, 0, 2);
            add(getOperatorLabel(), constraintsOperatorLabel);

            java.awt.GridBagConstraints constraintsValueLabel = 
                new java.awt.GridBagConstraints();
            constraintsValueLabel.gridx = 2;
            constraintsValueLabel.gridy = 0;
            constraintsValueLabel.anchor = 
                    java.awt.GridBagConstraints.SOUTHWEST;
            constraintsValueLabel.insets = new java.awt.Insets(2, 2, 0, 2);
            add(getValueLabel(), constraintsValueLabel);

            java.awt.GridBagConstraints constraintsExpressionTreeScroll = 
                new java.awt.GridBagConstraints();
            constraintsExpressionTreeScroll.gridx = 0;
            constraintsExpressionTreeScroll.gridy = 3;
            constraintsExpressionTreeScroll.gridwidth = 10;
            constraintsExpressionTreeScroll.fill = 
                    java.awt.GridBagConstraints.BOTH;
            constraintsExpressionTreeScroll.weightx = 1.0;
            constraintsExpressionTreeScroll.weighty = 0.6;
            constraintsExpressionTreeScroll.insets = 
                    new java.awt.Insets(2, 2, 2, 2);
            add(getExpressionTreeScroll(), constraintsExpressionTreeScroll);

            java.awt.GridBagConstraints constraintsToolBar = 
                new java.awt.GridBagConstraints();
            constraintsToolBar.gridx = 0;
            constraintsToolBar.gridy = 2;
            constraintsToolBar.gridwidth = 3;
            constraintsToolBar.fill = java.awt.GridBagConstraints.BOTH;
            add(getToolBar(), constraintsToolBar);
            initConnections();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }

        // user code begin {2}
        reset();
        // user code end
    }

    public void notExpression() {
        if (getExpression() != null) {
            setExpression(getExpression().not());
        }
    }

    public void orExpression() {
        Expression newExpression = buildExpressionNode();
        if (newExpression == null) {
            return;
        }

        if (getExpression() == null) {
            setExpression(newExpression);
        } else {
            setExpression(getExpression().or(newExpression));
        }
    }

    public void reset() {
        resetAttributes();
        if (getOperatorCombo().getItemCount() == 0) {
            resetOperators();
        }
        setExpression(null);
    }

    public void resetAttributes() {
        if (getAttributeCombo().getItemCount() > 0) {
            getAttributeCombo().removeAllItems();
        }
        if (getDescriptor() != null) {
            for (Enumeration mappingsEnum = 
                 getDescriptor().getMappings().elements(); 
                 mappingsEnum.hasMoreElements(); ) {
                DatabaseMapping mapping = 
                    (DatabaseMapping)mappingsEnum.nextElement();
                if (mapping.isDirectToFieldMapping()) {
                    getAttributeCombo().addItem(mapping.getAttributeName());
                }
            }
        }
    }

    public void resetOperators() {
        getOperatorCombo().addItem(ExpressionNode.getOperator(ExpressionOperator.Equal));
        getOperatorCombo().addItem(ExpressionNode.getOperator(ExpressionOperator.NotEqual));
        getOperatorCombo().addItem(ExpressionNode.getOperator(ExpressionOperator.LessThan));
        getOperatorCombo().addItem(ExpressionNode.getOperator(ExpressionOperator.LessThanEqual));
        getOperatorCombo().addItem(ExpressionNode.getOperator(ExpressionOperator.GreaterThan));
        getOperatorCombo().addItem(ExpressionNode.getOperator(ExpressionOperator.GreaterThanEqual));
        getOperatorCombo().addItem(ExpressionNode.getOperator(ExpressionOperator.Like));
        getOperatorCombo().addItem(ExpressionNode.getOperator(ExpressionOperator.NotLike));
        //getOperatorCombo().addItem(ExpressionNode.getOperator(ExpressionOperator.In));
        //getOperatorCombo().addItem(ExpressionNode.getOperator(ExpressionOperator.NotIn));
        //getOperatorCombo().addItem(ExpressionNode.getOperator(ExpressionOperator.Between));
        //getOperatorCombo().addItem(ExpressionNode.getOperator(ExpressionOperator.NotBetween));
        getOperatorCombo().addItem(ExpressionNode.KeyWordAll);
        getOperatorCombo().addItem(ExpressionNode.KeyWordAny);
    }

    public void setDescriptor(ClassDescriptor descriptor) {
        this.descriptor = descriptor;
        reset();
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
        ExpressionTreeModel model = 
            new ExpressionTreeModel(new ExpressionNode(expression));
        getExpressionTree().setModel(model);
        for (int index = 0; index < getExpressionTree().getRowCount(); 
             index++) {
            getExpressionTree().expandRow(index);
        }
        getExpressionTree().repaint();
    }

    class IvjEventHandler implements java.awt.event.ActionListener {
        public void actionPerformed(java.awt.event.ActionEvent e) {
            if (e.getSource() == ExpressionPanel.this.getOrButton()) {
                connEtoC1(e);
            }
            if (e.getSource() == ExpressionPanel.this.getAndButton()) {
                connEtoC2(e);
            }
            if (e.getSource() == ExpressionPanel.this.getNotBuuton()) {
                connEtoC3(e);
            }
            if (e.getSource() == ExpressionPanel.this.getClearButton()) {
                connEtoC4(e);
            }
        }
    }
}
