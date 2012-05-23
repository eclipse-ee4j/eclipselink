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
package org.eclipse.persistence.tools.sessionconsole;

import javax.swing.*;

import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.tools.beans.*;

/**
 * Generic login dialog.
 */
public class LoginEditorDialog extends JDialog {
    private boolean wasCanceled = true;
    private JButton ivjCancelButton = null;
    private LoginEditorPanel ivjLoginEditorPanel1 = null;
    private JPanel ivjMainPanel = null;
    private JButton ivjOKButton = null;
    IvjEventHandler ivjEventHandler = new IvjEventHandler();

    public LoginEditorDialog() {
        super();
        initialize();
    }

    public LoginEditorDialog(java.awt.Frame owner) {
        super(owner);
    }

    public LoginEditorDialog(java.awt.Frame owner, String title) {
        super(owner, title);
    }

    public LoginEditorDialog(java.awt.Frame owner, String title, 
                             boolean modal) {
        super(owner, title, modal);
    }

    public LoginEditorDialog(java.awt.Frame owner, boolean modal) {
        super(owner, modal);
    }

    /**
     * Center a component in the middle of the screen.
     */
    public static void centerComponent(java.awt.Component component) {
        java.awt.Dimension screenSize = 
            java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        java.awt.Dimension size = component.getSize();

        screenSize.height = screenSize.height / 2;
        screenSize.width = screenSize.width / 2;

        size.height = size.height / 2;
        size.width = size.width / 2;

        component.setLocation(screenSize.width - size.width, 
                              screenSize.height - size.height);
    }

    /**
     * connEtoC1:  (CancelButton.action.actionPerformed(java.awt.event.ActionEvent) --> LoginEditorDialog.markCanceled()V)
     * @param arg1 java.awt.event.ActionEvent
     */
    private void connEtoC1(java.awt.event.ActionEvent arg1) {
        try {
            this.markCanceled();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC2:  (OKButton.action. --> LoginEditorDialog.loginOK()V)
     */
    private void connEtoC2() {
        try {
            // user code begin {1}
            // user code end
            this.loginOK();
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * Return the CancelButton property value.
     * @return javax.swing.JButton
     */
    private javax.swing.JButton getCancelButton() {
        if (ivjCancelButton == null) {
            try {
                ivjCancelButton = new javax.swing.JButton();
                ivjCancelButton.setName("CancelButton");
                ivjCancelButton.setText("Cancel");
                ivjCancelButton.setBackground(java.awt.SystemColor.control);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjCancelButton;
    }

    public DatabaseLogin getLogin() {
        return getLoginEditorPanel1().getLogin();
    }

    /**
     * Return the LoginEditorPanel1 property value.
     * @return LoginEditorPanel
     */
    private LoginEditorPanel getLoginEditorPanel1() {
        if (ivjLoginEditorPanel1 == null) {
            try {
                ivjLoginEditorPanel1 = 
                        new org.eclipse.persistence.tools.sessionconsole.LoginEditorPanel();
                ivjLoginEditorPanel1.setName("LoginEditorPanel1");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjLoginEditorPanel1;
    }

    /**
     * Return the MainPanel property value.
     * @return javax.swing.JPanel
     */
    private javax.swing.JPanel getMainPanel() {
        if (ivjMainPanel == null) {
            try {
                ivjMainPanel = new javax.swing.JPanel();
                ivjMainPanel.setName("MainPanel");
                ivjMainPanel.setLayout(new java.awt.GridBagLayout());
                ivjMainPanel.setBackground(java.awt.SystemColor.control);

                java.awt.GridBagConstraints constraintsLoginEditorPanel1 = 
                    new java.awt.GridBagConstraints();
                constraintsLoginEditorPanel1.gridx = 0;
                constraintsLoginEditorPanel1.gridy = 0;
                constraintsLoginEditorPanel1.fill = 
                        java.awt.GridBagConstraints.BOTH;
                constraintsLoginEditorPanel1.weightx = 1.0;
                constraintsLoginEditorPanel1.weighty = 1.0;
                getMainPanel().add(getLoginEditorPanel1(), 
                                   constraintsLoginEditorPanel1);

                java.awt.GridBagConstraints constraintsOKButton = 
                    new java.awt.GridBagConstraints();
                constraintsOKButton.gridx = 0;
                constraintsOKButton.gridy = 0;
                constraintsOKButton.anchor = java.awt.GridBagConstraints.WEST;
                constraintsOKButton.ipadx = 20;
                constraintsOKButton.insets = new java.awt.Insets(0, 2, 2, 0);
                getMainPanel().add(getOKButton(), constraintsOKButton);

                java.awt.GridBagConstraints constraintsCancelButton = 
                    new java.awt.GridBagConstraints();
                constraintsCancelButton.gridx = 0;
                constraintsCancelButton.gridy = 0;
                constraintsCancelButton.gridheight = 2;
                constraintsCancelButton.fill = 
                        java.awt.GridBagConstraints.VERTICAL;
                constraintsCancelButton.anchor = 
                        java.awt.GridBagConstraints.EAST;
                constraintsCancelButton.insets = 
                        new java.awt.Insets(0, 0, 2, 2);
                getMainPanel().add(getCancelButton(), constraintsCancelButton);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjMainPanel;
    }

    /**
     * Return the OKButton property value.
     * @return javax.swing.JButton
     */
    private javax.swing.JButton getOKButton() {
        if (ivjOKButton == null) {
            try {
                ivjOKButton = new javax.swing.JButton();
                ivjOKButton.setName("OKButton");
                ivjOKButton.setText("OK");
                ivjOKButton.setBackground(java.awt.SystemColor.control);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjOKButton;
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
    private void initConnections() throws java.lang.Exception {
        // user code begin {1}
        // user code end
        getCancelButton().addActionListener(ivjEventHandler);
        getOKButton().addActionListener(ivjEventHandler);
    }

    /**
     * Initialize the class.
     */
    private void initialize() {
        try {
            // user code begin {1}
            // user code end
            setName("LoginEditorDialog");
            setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
            setSize(609, 387);
            setModal(false);
            setTitle("TopLink Login Editor");
            setContentPane(getMainPanel());
            initConnections();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }

        // user code begin {2}
        centerComponent(this);
        // user code end
    }

    public static DatabaseLogin launch(java.awt.Frame parent, 
                                       DatabaseLogin login) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            LoginEditorDialog aLoginEditorDialog;
            aLoginEditorDialog = new LoginEditorDialog();
            aLoginEditorDialog.getLoginEditorPanel1().setLogin(login);
            aLoginEditorDialog.setModal(true);
            aLoginEditorDialog.setVisible(true);

            if (aLoginEditorDialog.wasCanceled()) {
                return null;
            } else {
                return aLoginEditorDialog.getLogin();
            }
        } catch (Throwable exception) {
            System.err.println("Exception occurred in main() of javax.swing.JDialog");
            exception.printStackTrace(System.out);
        }
        return null;
    }

    public void loginOK() {
        setWasCanceled(false);
        dispose();
    }

    public void markCanceled() {
        setWasCanceled(true);
        dispose();
    }

    protected void setWasCanceled(boolean wasCanceled) {
        this.wasCanceled = wasCanceled;
    }

    public boolean wasCanceled() {
        return wasCanceled;
    }

    class IvjEventHandler implements java.awt.event.ActionListener {
        public void actionPerformed(java.awt.event.ActionEvent e) {
            if (e.getSource() == LoginEditorDialog.this.getCancelButton()) {
                connEtoC1(e);
            }
            if (e.getSource() == LoginEditorDialog.this.getOKButton()) {
                connEtoC2();
            }
        }
    }
}
