/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.framework.ui;

import java.util.*;
import java.awt.event.*;
import org.eclipse.persistence.logging.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * UI frame for the load query tool.
 */
public class LoadBuildBrowserFrame extends javax.swing.JFrame implements ActionListener, ItemListener, WindowListener, javax.swing.event.ChangeListener {
    private LoadBuildSystem loadBuildSystem = null;
    private javax.swing.JPanel ivjJFrameContentPane = null;
    private LoadBuildDisplayPanel ivjLoadBuildDisplayPanel1 = null;
    private javax.swing.JCheckBox ivjInCheckBox = null;
    private javax.swing.JLabel ivjJLabel22 = null;
    private javax.swing.JLabel ivjJLabel4 = null;
    private javax.swing.JLabel ivjJLabel5 = null;
    private javax.swing.JPanel ivjJPanel1 = null;
    private javax.swing.JButton ivjQueryButton = null;
    private javax.swing.JCheckBox ivjBetweenCheckBox1 = null;
    private javax.swing.JCheckBox ivjErrorCheckBox1 = null;
    private javax.swing.JLabel ivjJLabel111 = null;
    private javax.swing.JLabel ivjJLabel13 = null;
    private javax.swing.JLabel ivjJLabel211 = null;
    private javax.swing.JLabel ivjJLabel23 = null;
    private javax.swing.JPanel ivjJPanel3 = null;
    private javax.swing.JCheckBox ivjTesterCheckBox1 = null;
    private javax.swing.JCheckBox ivjWarningCheckBox1 = null;
    private javax.swing.JComboBox ivjMonth1 = null;
    private javax.swing.JComboBox ivjMonth2 = null;
    private javax.swing.JComboBox ivjMonth3 = null;
    private javax.swing.JComboBox ivjYear1 = null;
    private javax.swing.JComboBox ivjYear2 = null;
    private javax.swing.JComboBox ivjYear3 = null;
    private javax.swing.JTextField ivjTesterNameText = null;
    private javax.swing.JLabel ivjJLabel1111 = null;
    private javax.swing.JCheckBox ivjLogMessageCheckBox = null;

    /**
     * Constructor
     */
    public LoadBuildBrowserFrame() {
        super();
        initialize();
    }

    /**
     * LoadBuildBrowserFrame constructor comment.
     * @param title java.lang.String
     */
    public LoadBuildBrowserFrame(String title) {
        super(title);
    }

    /**
     * Method to handle events for the ActionListener interface.
     * @param e java.awt.event.ActionEvent
     */
    public void actionPerformed(java.awt.event.ActionEvent e) {
        if (e.getSource() == getQueryButton()) {
            connEtoC1(e);
        }
    }

    /**
     * Build readObjects query base on the selected info
     */
    public Expression buildExpression() {
        // Query all model result summaries.
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("name").like(getTesterNameText().getText().trim() + "%");

        // build expression for load build that has error
        if (getErrorCheckBox1().isSelected()) {
            Expression expression1 = builder.get("errors").greaterThan(0);
            Expression expression2 = builder.get("fatalErrors").greaterThan(0);
            Expression expression3 = builder.get("problems").greaterThan(0);
            expression = expression.and(expression1).or(expression2).or(expression3);
        }

        // build expression for load build that has waring
        if (getWarningCheckBox1().isSelected()) {
            expression = expression.and(builder.get("warnings").greaterThan(0));
        }

        // build expression for load build that has timestamp in the month
        if (getInCheckBox().isSelected()) {
            int month = Integer.parseInt((String)getMonth3().getSelectedItem());
            int year = Integer.parseInt((String)getYear3().getSelectedItem());
            Calendar ts1 = Calendar.getInstance();
            ts1.clear();
            ts1.set(Calendar.YEAR, year);
            ts1.set(Calendar.MONTH, month - 1);
            ts1.set(Calendar.DAY_OF_MONTH, 1);
            Calendar ts2 = Calendar.getInstance();
            ts1.clear();
            ts1.set(Calendar.YEAR, year);
            ts1.set(Calendar.MONTH, month - 1);
            ts1.set(Calendar.DAY_OF_MONTH, 31);

            expression = expression.and(builder.get("loadBuildSummary").get("timestamp").between(ts1, ts2));
        }

        // build expression for load build that has timestamp between the two months
        if (getBetweenCheckBox1().isSelected()) {
            int month1 = Integer.parseInt((String)getMonth1().getSelectedItem());
            int year1 = Integer.parseInt((String)getYear1().getSelectedItem());
            int month2 = Integer.parseInt((String)getMonth2().getSelectedItem());
            int year2 = Integer.parseInt((String)getYear2().getSelectedItem());
            Calendar ts1 = Calendar.getInstance();
            ts1.clear();
            ts1.set(Calendar.YEAR, year1);
            ts1.set(Calendar.MONTH, month1 - 1);
            ts1.set(Calendar.DAY_OF_MONTH, 1);
            Calendar ts2 = Calendar.getInstance();
            ts1.clear();
            ts1.set(Calendar.YEAR, year2);
            ts1.set(Calendar.MONTH, month2 - 1);
            ts1.set(Calendar.DAY_OF_MONTH, 31);
            expression = expression.and(builder.get("loadBuildSummary").get("timestamp").between(ts1, ts2));
        }
        return expression;
    }


    /**
     * Build readObjects query base on the selected info
     */
    public Expression buildTestExpression() {
        // Query all model result summaries.
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("name").like(getTesterNameText().getText().trim());

        // build expression for load build that has error
        if (getErrorCheckBox1().isSelected()) {
            Expression expression1 = builder.get("exception").notNull();
            expression = expression.and(expression1);
        }

        // build expression for load build that has waring
        if (getWarningCheckBox1().isSelected()) {
            expression = expression.and(builder.get("outcome").equal("warning"));
        }

        // build expression for load build that has timestamp in the month
        if (getInCheckBox().isSelected()) {
            int month = Integer.parseInt((String)getMonth3().getSelectedItem());
            int year = Integer.parseInt((String)getYear3().getSelectedItem());
            Calendar ts1 = Calendar.getInstance();
            ts1.clear();
            ts1.set(Calendar.YEAR, year);
            ts1.set(Calendar.MONTH, month - 1);
            ts1.set(Calendar.DAY_OF_MONTH, 1);
            Calendar ts2 = Calendar.getInstance();
            ts1.clear();
            ts1.set(Calendar.YEAR, year);
            ts1.set(Calendar.MONTH, month - 1);
            ts1.set(Calendar.DAY_OF_MONTH, 31);

            expression = expression.and(builder.get("loadBuildSummary").get("timestamp").between(ts1, ts2));
        }

        // build expression for load build that has timestamp between the two months
        if (getBetweenCheckBox1().isSelected()) {
            int month1 = Integer.parseInt((String)getMonth1().getSelectedItem());
            int year1 = Integer.parseInt((String)getYear1().getSelectedItem());
            int month2 = Integer.parseInt((String)getMonth2().getSelectedItem());
            int year2 = Integer.parseInt((String)getYear2().getSelectedItem());
            Calendar ts1 = Calendar.getInstance();
            ts1.clear();
            ts1.set(Calendar.YEAR, year1);
            ts1.set(Calendar.MONTH, month1 - 1);
            ts1.set(Calendar.DAY_OF_MONTH, 1);
            Calendar ts2 = Calendar.getInstance();
            ts1.clear();
            ts1.set(Calendar.YEAR, year2);
            ts1.set(Calendar.MONTH, month2 - 1);
            ts1.set(Calendar.DAY_OF_MONTH, 31);
            expression = expression.and(builder.get("loadBuildSummary").get("timestamp").between(ts1, ts2));
        }
        return expression;
    }

    /**
     * connEtoC1:  (QueryButton.action.actionPerformed(java.awt.event.ActionEvent) --> LoadBuildBrowserFrame.query()V)
     * @param arg1 java.awt.event.ActionEvent
     */
    private void connEtoC1(java.awt.event.ActionEvent arg1) {
        try {
            this.query();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC2:  (BetweenCheckBox1.change.stateChanged(javax.swing.event.ChangeEvent) --> LoadBuildBrowserFrame.setComponentStates()V)
     * @param arg1 javax.swing.event.ChangeEvent
     */
    private void connEtoC2(javax.swing.event.ChangeEvent arg1) {
        try {
            this.timeCheckBoxChange();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC3:  (TesterCheckBox1.change.stateChanged(javax.swing.event.ChangeEvent) --> LoadBuildBrowserFrame.testerChange(Ljavax.swing.event.ChangeEvent;)V)
     * @param arg1 javax.swing.event.ChangeEvent
     */
    private void connEtoC3(javax.swing.event.ChangeEvent arg1) {
        try {
            this.testerChange(arg1);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC4:  (LoadBuildBrowserFrame.window.windowClosing(java.awt.event.WindowEvent) --> LoadBuildBrowserFrame.loadBuildBrowserFrame_WindowClosing()V)
     * @param arg1 java.awt.event.WindowEvent
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connEtoC4(java.awt.event.WindowEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.loadBuildBrowserFrame_WindowClosing();

            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC5:  (LogMessageCheckBox.item.itemStateChanged(java.awt.event.ItemEvent) --> LoadBuildBrowserFrame.logMessageCheckBoxhanged()V)
     * @param arg1 java.awt.event.ItemEvent
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connEtoC5(java.awt.event.ItemEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.logMessageCheckBoxhanged();

            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC6:  (InCheckBox.change.stateChanged(javax.swing.event.ChangeEvent) --> LoadBuildBrowserFrame.setComponentStates()V)
     * @param arg1 javax.swing.event.ChangeEvent
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connEtoC6(javax.swing.event.ChangeEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.timeCheckBoxChange();

            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * Return the BetweenCheckBox1 property value.
     * @return javax.swing.JCheckBox
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JCheckBox getBetweenCheckBox1() {
        if (ivjBetweenCheckBox1 == null) {
            try {
                ivjBetweenCheckBox1 = new javax.swing.JCheckBox();
                ivjBetweenCheckBox1.setName("BetweenCheckBox1");
                ivjBetweenCheckBox1.setText("Between");
                ivjBetweenCheckBox1.setBackground(java.awt.SystemColor.controlHighlight);
                ivjBetweenCheckBox1.setBounds(18, 19, 79, 25);
                ivjBetweenCheckBox1.setForeground(java.awt.SystemColor.activeCaption);

                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjBetweenCheckBox1;
    }

    /**
     * Return the ErrorCheckBox1 property value.
     * @return javax.swing.JCheckBox
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JCheckBox getErrorCheckBox1() {
        if (ivjErrorCheckBox1 == null) {
            try {
                ivjErrorCheckBox1 = new javax.swing.JCheckBox();
                ivjErrorCheckBox1.setName("ErrorCheckBox1");
                ivjErrorCheckBox1.setText("Error");
                ivjErrorCheckBox1.setBackground(java.awt.SystemColor.controlHighlight);
                ivjErrorCheckBox1.setBounds(18, 138, 79, 25);
                ivjErrorCheckBox1.setForeground(java.awt.SystemColor.activeCaption);

                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjErrorCheckBox1;
    }

    /**
     * Return the InCheckBox property value.
     * @return javax.swing.JCheckBox
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JCheckBox getInCheckBox() {
        if (ivjInCheckBox == null) {
            try {
                ivjInCheckBox = new javax.swing.JCheckBox();
                ivjInCheckBox.setName("InCheckBox");
                ivjInCheckBox.setText("In");
                ivjInCheckBox.setBackground(java.awt.SystemColor.controlHighlight);
                ivjInCheckBox.setBounds(343, 19, 41, 25);
                ivjInCheckBox.setForeground(java.awt.SystemColor.activeCaption);

                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjInCheckBox;
    }

    /**
     * Return the JFrameContentPane property value.
     * @return javax.swing.JPanel
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JPanel getJFrameContentPane() {
        if (ivjJFrameContentPane == null) {
            try {
                ivjJFrameContentPane = new javax.swing.JPanel();
                ivjJFrameContentPane.setName("JFrameContentPane");
                ivjJFrameContentPane.setOpaque(true);
                ivjJFrameContentPane.setLayout(new java.awt.GridBagLayout());
                ivjJFrameContentPane.setBackground(java.awt.SystemColor.control);
                ivjJFrameContentPane.setMaximumSize(new java.awt.Dimension(100, 50));
                ivjJFrameContentPane.setPreferredSize(new java.awt.Dimension(30, 10));
                ivjJFrameContentPane.setMinimumSize(new java.awt.Dimension(20, 10));

                java.awt.GridBagConstraints constraintsLoadBuildDisplayPanel1 = new java.awt.GridBagConstraints();
                constraintsLoadBuildDisplayPanel1.gridx = 0;
                constraintsLoadBuildDisplayPanel1.gridy = 2;
                constraintsLoadBuildDisplayPanel1.gridwidth = 2;
                constraintsLoadBuildDisplayPanel1.fill = java.awt.GridBagConstraints.BOTH;
                constraintsLoadBuildDisplayPanel1.weightx = 1.0;
                constraintsLoadBuildDisplayPanel1.weighty = 1.0;
                constraintsLoadBuildDisplayPanel1.ipadx = -302;
                constraintsLoadBuildDisplayPanel1.ipady = -257;
                constraintsLoadBuildDisplayPanel1.insets = new java.awt.Insets(5, 5, 5, 5);
                getJFrameContentPane().add(getLoadBuildDisplayPanel1(), constraintsLoadBuildDisplayPanel1);

                java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
                constraintsJPanel1.gridx = 1;
                constraintsJPanel1.gridy = 0;
                constraintsJPanel1.ipadx = 78;
                constraintsJPanel1.ipady = 75;
                constraintsJPanel1.insets = new java.awt.Insets(10, 5, 16, 11);
                getJFrameContentPane().add(getJPanel1(), constraintsJPanel1);

                java.awt.GridBagConstraints constraintsJPanel3 = new java.awt.GridBagConstraints();
                constraintsJPanel3.gridx = 0;
                constraintsJPanel3.gridy = 0;
                constraintsJPanel3.gridheight = 2;
                constraintsJPanel3.fill = java.awt.GridBagConstraints.BOTH;
                constraintsJPanel3.weightx = 1.0;
                constraintsJPanel3.ipadx = 517;
                constraintsJPanel3.ipady = 176;
                constraintsJPanel3.insets = new java.awt.Insets(5, 5, 0, 5);
                getJFrameContentPane().add(getJPanel3(), constraintsJPanel3);

                java.awt.GridBagConstraints constraintsLogMessageCheckBox = new java.awt.GridBagConstraints();
                constraintsLogMessageCheckBox.gridx = 1;
                constraintsLogMessageCheckBox.gridy = 1;
                getJFrameContentPane().add(getLogMessageCheckBox(), constraintsLogMessageCheckBox);

                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJFrameContentPane;
    }

    /**
     * Return the JLabel111 property value.
     * @return javax.swing.JLabel
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JLabel getJLabel111() {
        if (ivjJLabel111 == null) {
            try {
                ivjJLabel111 = new javax.swing.JLabel();
                ivjJLabel111.setName("JLabel111");
                ivjJLabel111.setText("Month:");
                ivjJLabel111.setBounds(205, 24, 45, 15);
                ivjJLabel111.setForeground(java.awt.Color.darkGray);

                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJLabel111;
    }

    /**
     * Return the JLabel1111 property value.
     * @return javax.swing.JLabel
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JLabel getJLabel1111() {
        if (ivjJLabel1111 == null) {
            try {
                ivjJLabel1111 = new javax.swing.JLabel();
                ivjJLabel1111.setName("JLabel1111");
                ivjJLabel1111.setText("Month:");
                ivjJLabel1111.setBounds(396, 24, 45, 15);
                ivjJLabel1111.setForeground(java.awt.Color.darkGray);

                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJLabel1111;
    }

    /**
     * Return the JLabel13 property value.
     * @return javax.swing.JLabel
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JLabel getJLabel13() {
        if (ivjJLabel13 == null) {
            try {
                ivjJLabel13 = new javax.swing.JLabel();
                ivjJLabel13.setName("JLabel13");
                ivjJLabel13.setText("Month:");
                ivjJLabel13.setBounds(110, 27, 45, 9);
                ivjJLabel13.setForeground(java.awt.Color.darkGray);

                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJLabel13;
    }

    /**
     * Return the JLabel211 property value.
     * @return javax.swing.JLabel
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JLabel getJLabel211() {
        if (ivjJLabel211 == null) {
            try {
                ivjJLabel211 = new javax.swing.JLabel();
                ivjJLabel211.setName("JLabel211");
                ivjJLabel211.setText("Year:");
                ivjJLabel211.setBounds(206, 69, 45, 15);
                ivjJLabel211.setForeground(java.awt.Color.darkGray);

                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJLabel211;
    }

    /**
     * Return the JLabel22 property value.
     * @return javax.swing.JLabel
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JLabel getJLabel22() {
        if (ivjJLabel22 == null) {
            try {
                ivjJLabel22 = new javax.swing.JLabel();
                ivjJLabel22.setName("JLabel22");
                ivjJLabel22.setText("Year:");
                ivjJLabel22.setBounds(397, 69, 45, 15);
                ivjJLabel22.setForeground(java.awt.Color.darkGray);

                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJLabel22;
    }

    /**
     * Return the JLabel23 property value.
     * @return javax.swing.JLabel
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JLabel getJLabel23() {
        if (ivjJLabel23 == null) {
            try {
                ivjJLabel23 = new javax.swing.JLabel();
                ivjJLabel23.setName("JLabel23");
                ivjJLabel23.setText("Year:");
                ivjJLabel23.setBounds(111, 69, 45, 15);
                ivjJLabel23.setForeground(java.awt.Color.darkGray);

                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJLabel23;
    }

    /**
     * Return the JLabel4 property value.
     * @return javax.swing.JLabel
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JLabel getJLabel4() {
        if (ivjJLabel4 == null) {
            try {
                ivjJLabel4 = new javax.swing.JLabel();
                ivjJLabel4.setName("JLabel4");
                ivjJLabel4.setFont(new java.awt.Font("dialog", 1, 24));
                ivjJLabel4.setText("Load");
                ivjJLabel4.setBounds(8, 5, 62, 31);
                ivjJLabel4.setForeground(new java.awt.Color(197, 50, 197));

                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJLabel4;
    }

    /**
     * Return the JLabel5 property value.
     * @return javax.swing.JLabel
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JLabel getJLabel5() {
        if (ivjJLabel5 == null) {
            try {
                ivjJLabel5 = new javax.swing.JLabel();
                ivjJLabel5.setName("JLabel5");
                ivjJLabel5.setFont(new java.awt.Font("dialog", 1, 24));
                ivjJLabel5.setText("Build");
                ivjJLabel5.setBounds(6, 38, 66, 31);
                ivjJLabel5.setForeground(new java.awt.Color(197, 50, 197));

                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJLabel5;
    }

    /**
     * Return the JPanel1 property value.
     * @return javax.swing.JPanel
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JPanel getJPanel1() {
        if (ivjJPanel1 == null) {
            try {
                ivjJPanel1 = new javax.swing.JPanel();
                ivjJPanel1.setName("JPanel1");
                ivjJPanel1.setToolTipText("");
                ivjJPanel1.setLayout(null);
                ivjJPanel1.setBackground(java.awt.SystemColor.control);
                ivjJPanel1.setForeground(new java.awt.Color(182, 71, 189));
                getJPanel1().add(getJLabel4(), getJLabel4().getName());
                getJPanel1().add(getJLabel5(), getJLabel5().getName());

                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJPanel1;
    }

    /**
     * Return the JPanel3 property value.
     * @return javax.swing.JPanel
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JPanel getJPanel3() {
        if (ivjJPanel3 == null) {
            try {
                ivjJPanel3 = new javax.swing.JPanel();
                ivjJPanel3.setName("JPanel3");
                ivjJPanel3.setLayout(null);
                ivjJPanel3.setBackground(java.awt.SystemColor.controlHighlight);
                getJPanel3().add(getYear2(), getYear2().getName());
                getJPanel3().add(getYear1(), getYear1().getName());
                getJPanel3().add(getMonth1(), getMonth1().getName());
                getJPanel3().add(getJLabel111(), getJLabel111().getName());
                getJPanel3().add(getJLabel23(), getJLabel23().getName());
                getJPanel3().add(getJLabel211(), getJLabel211().getName());
                getJPanel3().add(getJLabel13(), getJLabel13().getName());
                getJPanel3().add(getBetweenCheckBox1(), getBetweenCheckBox1().getName());
                getJPanel3().add(getInCheckBox(), getInCheckBox().getName());
                getJPanel3().add(getJLabel22(), getJLabel22().getName());
                getJPanel3().add(getYear3(), getYear3().getName());
                getJPanel3().add(getErrorCheckBox1(), getErrorCheckBox1().getName());
                getJPanel3().add(getWarningCheckBox1(), getWarningCheckBox1().getName());
                getJPanel3().add(getTesterCheckBox1(), getTesterCheckBox1().getName());
                getJPanel3().add(getTesterNameText(), getTesterNameText().getName());
                getJPanel3().add(getMonth2(), getMonth2().getName());
                getJPanel3().add(getMonth3(), getMonth3().getName());
                getJPanel3().add(getJLabel1111(), getJLabel1111().getName());
                getJPanel3().add(getQueryButton(), getQueryButton().getName());

                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJPanel3;
    }

    /**
     * Return the LoadBuildDisplayPanel1 property value.
     * @return org.eclipse.persistence.testing.framework.ui.LoadBuildDisplayPanel
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private LoadBuildDisplayPanel getLoadBuildDisplayPanel1() {
        if (ivjLoadBuildDisplayPanel1 == null) {
            try {
                ivjLoadBuildDisplayPanel1 = new org.eclipse.persistence.testing.framework.ui.LoadBuildDisplayPanel();
                ivjLoadBuildDisplayPanel1.setName("LoadBuildDisplayPanel1");
                ivjLoadBuildDisplayPanel1.setBackground(java.awt.SystemColor.control);

                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjLoadBuildDisplayPanel1;
    }

    /**
     * Return the LogMessageCheckBox property value.
     * @return javax.swing.JCheckBox
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JCheckBox getLogMessageCheckBox() {
        if (ivjLogMessageCheckBox == null) {
            try {
                ivjLogMessageCheckBox = new javax.swing.JCheckBox();
                ivjLogMessageCheckBox.setName("LogMessageCheckBox");
                ivjLogMessageCheckBox.setText("Log Messages");

                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjLogMessageCheckBox;
    }

    /**
     * Return the JComboBox14 property value.
     * @return javax.swing.JComboBox
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JComboBox getMonth1() {
        if (ivjMonth1 == null) {
            try {
                ivjMonth1 = new javax.swing.JComboBox();
                ivjMonth1.setName("Month1");
                ivjMonth1.setBackground(java.awt.Color.white);
                ivjMonth1.setBounds(110, 43, 80, 21);

                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjMonth1;
    }

    /**
     * Return the JComboBox121 property value.
     * @return javax.swing.JComboBox
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JComboBox getMonth2() {
        if (ivjMonth2 == null) {
            try {
                ivjMonth2 = new javax.swing.JComboBox();
                ivjMonth2.setName("Month2");
                ivjMonth2.setBackground(java.awt.Color.white);
                ivjMonth2.setBounds(205, 43, 80, 21);

                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjMonth2;
    }

    /**
     * Return the JComboBox13 property value.
     * @return javax.swing.JComboBox
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JComboBox getMonth3() {
        if (ivjMonth3 == null) {
            try {
                ivjMonth3 = new javax.swing.JComboBox();
                ivjMonth3.setName("Month3");
                ivjMonth3.setBackground(java.awt.Color.white);
                ivjMonth3.setBounds(396, 43, 80, 21);

                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjMonth3;
    }

    /**
     * Return the QueryButton property value.
     * @return javax.swing.JButton
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JButton getQueryButton() {
        if (ivjQueryButton == null) {
            try {
                ivjQueryButton = new javax.swing.JButton();
                ivjQueryButton.setName("QueryButton");
                ivjQueryButton.setText("Query");
                ivjQueryButton.setBackground(java.awt.SystemColor.control);
                ivjQueryButton.setBounds(585, 75, 78, 25);

                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjQueryButton;
    }

    private javax.swing.JCheckBox getTesterCheckBox1() {
        if (ivjTesterCheckBox1 == null) {
            try {
                ivjTesterCheckBox1 = new javax.swing.JCheckBox();
                ivjTesterCheckBox1.setName("TesterCheckBox1");
                ivjTesterCheckBox1.setText("Model or Suite/Test name:");
                ivjTesterCheckBox1.setBounds(246, 138, 180, 25);
                ivjTesterCheckBox1.setForeground(java.awt.SystemColor.activeCaption);
                ivjTesterCheckBox1.setActionCommand("Loadbuild Name");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjTesterCheckBox1;
    }

    /**
     * Return the JTextField11 property value.
     * @return javax.swing.JTextField
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JTextField getTesterNameText() {
        if (ivjTesterNameText == null) {
            try {
                ivjTesterNameText = new javax.swing.JTextField();
                ivjTesterNameText.setName("TesterNameText");
                ivjTesterNameText.setBounds(440, 141, 198, 19);

                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjTesterNameText;
    }

    /**
     * Return the WarningCheckBox1 property value.
     * @return javax.swing.JCheckBox
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JCheckBox getWarningCheckBox1() {
        if (ivjWarningCheckBox1 == null) {
            try {
                ivjWarningCheckBox1 = new javax.swing.JCheckBox();
                ivjWarningCheckBox1.setName("WarningCheckBox1");
                ivjWarningCheckBox1.setText("Warning");
                ivjWarningCheckBox1.setBackground(java.awt.SystemColor.controlHighlight);
                ivjWarningCheckBox1.setBounds(124, 138, 79, 25);
                ivjWarningCheckBox1.setForeground(java.awt.SystemColor.activeCaption);

                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjWarningCheckBox1;
    }

    /**
     * Return the JComboBox113 property value.
     * @return javax.swing.JComboBox
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JComboBox getYear1() {
        if (ivjYear1 == null) {
            try {
                ivjYear1 = new javax.swing.JComboBox();
                ivjYear1.setName("Year1");
                ivjYear1.setOpaque(true);
                ivjYear1.setBackground(java.awt.Color.white);
                ivjYear1.setBounds(110, 88, 80, 21);

                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjYear1;
    }

    /**
     * Return the JComboBox1111 property value.
     * @return javax.swing.JComboBox
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JComboBox getYear2() {
        if (ivjYear2 == null) {
            try {
                ivjYear2 = new javax.swing.JComboBox();
                ivjYear2.setName("Year2");
                ivjYear2.setBackground(java.awt.Color.white);
                ivjYear2.setBounds(206, 88, 80, 21);

                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjYear2;
    }

    /**
     * Return the JComboBox112 property value.
     * @return javax.swing.JComboBox
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JComboBox getYear3() {
        if (ivjYear3 == null) {
            try {
                ivjYear3 = new javax.swing.JComboBox();
                ivjYear3.setName("Year3");
                ivjYear3.setBackground(java.awt.Color.white);
                ivjYear3.setBounds(396, 88, 80, 21);

                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjYear3;
    }

    /**
     * Called whenever the part throws an exception.
     * @param exception java.lang.Throwable
     */
    private void handleException(Throwable exception) {
        exception.printStackTrace(System.out);
    }

    /**
     * Initializes connections
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void initConnections() throws java.lang.Exception {
        // user code begin {1}
        // user code end
        getQueryButton().addActionListener(this);
        getInCheckBox().addChangeListener(this);
        getTesterCheckBox1().addChangeListener(this);
        getBetweenCheckBox1().addChangeListener(this);
        this.addWindowListener(this);
        getLogMessageCheckBox().addItemListener(this);
    }

    /**
     * Initialize the class.
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void initialize() {
        try {
            // user code begin {1}
            // user code end
            setName("LoadBuildBrowserFrame");
            setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
            setSize(826, 585);
            setContentPane(getJFrameContentPane());
            initConnections();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }

        // user code begin {2}
        loadBuildSystem = new LoadBuildSystem();
        getMonth1().setEnabled(false);
        getMonth2().setEnabled(false);
        getMonth3().setEnabled(false);

        getYear1().setEnabled(false);
        getYear2().setEnabled(false);
        getYear3().setEnabled(false);

        getMonth1().addItem("1");
        getMonth1().addItem("2");
        getMonth1().addItem("3");
        getMonth1().addItem("4");
        getMonth1().addItem("5");
        getMonth1().addItem("6");
        getMonth1().addItem("7");
        getMonth1().addItem("8");
        getMonth1().addItem("9");
        getMonth1().addItem("10");
        getMonth1().addItem("11");
        getMonth1().addItem("12");

        getMonth2().addItem("1");
        getMonth2().addItem("2");
        getMonth2().addItem("3");
        getMonth2().addItem("4");
        getMonth2().addItem("5");
        getMonth2().addItem("6");
        getMonth2().addItem("7");
        getMonth2().addItem("8");
        getMonth2().addItem("9");
        getMonth2().addItem("10");
        getMonth2().addItem("11");
        getMonth2().addItem("12");

        getMonth3().addItem("1");
        getMonth3().addItem("2");
        getMonth3().addItem("3");
        getMonth3().addItem("4");
        getMonth3().addItem("5");
        getMonth3().addItem("6");
        getMonth3().addItem("7");
        getMonth3().addItem("8");
        getMonth3().addItem("9");
        getMonth3().addItem("10");
        getMonth3().addItem("11");
        getMonth3().addItem("12");

        int year = Calendar.getInstance().get(Calendar.YEAR) + 1;
        while (year >= 2000) {
            getYear1().addItem(String.valueOf(year));
            getYear2().addItem(String.valueOf(year));
            getYear3().addItem(String.valueOf(year));
            year--;
        }
    }

    /**
     * Method to handle events for the ItemListener interface.
     * @param e java.awt.event.ItemEvent
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    public void itemStateChanged(java.awt.event.ItemEvent e) {
        // user code begin {1}
        // user code end
        if (e.getSource() == getLogMessageCheckBox()) {
            connEtoC5(e);
        }

        // user code begin {2}
        // user code end
    }

    /**
     * Comment
     */
    public void loadBuildBrowserFrame_WindowClosing() {
        loadBuildSystem.logout();

    }

    /**
     * Comment
     */
    public void logMessageCheckBoxhanged() {
        if (getLogMessageCheckBox().isSelected()) {
            loadBuildSystem.getSession().setLog(new java.io.PrintWriter(System.out));
            loadBuildSystem.getSession().setLogLevel(SessionLog.FINE);
        } else {
            loadBuildSystem.getSession().dontLogMessages();
        }
    }

    /**
     * Launch Test Result Database browsing tool.
     */
    public static void main(java.lang.String[] args) {
        try {
            LoadBuildBrowserFrame aLoadBuildBrowserFrame;
            aLoadBuildBrowserFrame = new LoadBuildBrowserFrame();
            aLoadBuildBrowserFrame.setVisible(true);
        } catch (Throwable exception) {
            System.err.println("Exception occurred in main() of javax.swing.JFrame");
            exception.printStackTrace(System.out);
        }
    }

    /**
     * Build readObjects query base on the selected info
     */
    public void query() {
        try {
            if (!loadBuildSystem.isConnected()) {
                loadBuildSystem.login();
                getLoadBuildDisplayPanel1().setSession(loadBuildSystem.getSession());
            }
            if (getTesterCheckBox1().isSelected()) {
                java.util.Vector loadBuilds = loadBuildSystem.readAllTestModelSummaries(buildExpression());
                getLoadBuildDisplayPanel1().poppulateLoadBuildTable(loadBuilds);
            } else {
                java.util.Vector loadBuilds = loadBuildSystem.readAllTests(buildTestExpression());
                getLoadBuildDisplayPanel1().poppulateTestResultTable(loadBuilds);
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    public void stateChanged(javax.swing.event.ChangeEvent e) {
        if (e.getSource() == getInCheckBox()) {
            connEtoC6(e);
        }
        if (e.getSource() == getTesterCheckBox1()) {
            connEtoC3(e);
        }
        if (e.getSource() == getBetweenCheckBox1()) {
            connEtoC2(e);
        }
    }

    public void testerChange(javax.swing.event.ChangeEvent stateChangeEvent) {
    }

    /**
     * Enable/disable appropriate components
     */
    public void timeCheckBoxChange() {
        if (getBetweenCheckBox1().isSelected()) {
            getMonth1().setEnabled(true);
            getYear1().setEnabled(true);
            getMonth2().setEnabled(true);
            getYear2().setEnabled(true);

            getInCheckBox().setEnabled(false);
            getMonth3().setEnabled(false);
            getYear3().setEnabled(false);
            return;
        } else if (getBetweenCheckBox1().isEnabled()) {
            getMonth1().setEnabled(false);
            getYear1().setEnabled(false);
            getMonth2().setEnabled(false);
            getYear2().setEnabled(false);
            getInCheckBox().setEnabled(true);
        }

        if (getInCheckBox().isSelected()) {
            getMonth3().setEnabled(true);
            getYear3().setEnabled(true);

            getBetweenCheckBox1().setEnabled(false);
            getMonth1().setEnabled(false);
            getYear1().setEnabled(false);
            getMonth2().setEnabled(false);
            getYear2().setEnabled(false);
            return;
        } else if (getInCheckBox().isEnabled()) {
            getMonth3().setEnabled(false);
            getYear3().setEnabled(false);

            getBetweenCheckBox1().setEnabled(true);
        }
    }

    /**
     * Method to handle events for the WindowListener interface.
     * @param e java.awt.event.WindowEvent
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    public void windowActivated(java.awt.event.WindowEvent e) {
        // user code begin {1}
        // user code end
        // user code begin {2}
        // user code end
    }

    /**
     * Method to handle events for the WindowListener interface.
     * @param e java.awt.event.WindowEvent
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    public void windowClosed(java.awt.event.WindowEvent e) {
        // user code begin {1}
        // user code end
        // user code begin {2}
        // user code end
    }

    /**
     * Method to handle events for the WindowListener interface.
     * @param e java.awt.event.WindowEvent
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    public void windowClosing(java.awt.event.WindowEvent e) {
        // user code begin {1}
        // user code end
        if (e.getSource() == this) {
            connEtoC4(e);
        }

        // user code begin {2}
        this.dispose();
        // user code end
    }

    /**
     * Method to handle events for the WindowListener interface.
     * @param e java.awt.event.WindowEvent
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    public void windowDeactivated(java.awt.event.WindowEvent e) {
        // user code begin {1}
        // user code end
        // user code begin {2}
        // user code end
    }

    /**
     * Method to handle events for the WindowListener interface.
     * @param e java.awt.event.WindowEvent
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    public void windowDeiconified(java.awt.event.WindowEvent e) {
        // user code begin {1}
        // user code end
        // user code begin {2}
        // user code end
    }

    /**
     * Method to handle events for the WindowListener interface.
     * @param e java.awt.event.WindowEvent
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    public void windowIconified(java.awt.event.WindowEvent e) {
        // user code begin {1}
        // user code end
        // user code begin {2}
        // user code end
    }

    /**
     * Method to handle events for the WindowListener interface.
     * @param e java.awt.event.WindowEvent
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    public void windowOpened(java.awt.event.WindowEvent e) {
        // user code begin {1}
        // user code end
        // user code begin {2}
        // user code end
    }
}
