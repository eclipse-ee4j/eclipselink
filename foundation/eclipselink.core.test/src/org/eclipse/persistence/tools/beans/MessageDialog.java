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

import javax.swing.*;

/**
 * Used to display error messages.
 */
public class MessageDialog extends JDialog {
    private JPanel ivjJDialogContentPane = null;
    private JScrollPane ivjMessageScrollPane = null;
    private JTextPane ivjMessageTextPane = null;
    private JButton ivjOKButton = null;
    IvjEventHandler ivjEventHandler = new IvjEventHandler();

    public MessageDialog() {
        super();
        initialize();
    }

    /**
     * MessageDialog constructor comment.
     * @param owner java.awt.Frame
     */
    public MessageDialog(java.awt.Frame owner) {
        super(owner);
    }

    /**
     * MessageDialog constructor comment.
     * @param owner java.awt.Frame
     * @param title java.lang.String
     */
    public MessageDialog(java.awt.Frame owner, String title) {
        super(owner, title);
    }

    /**
     * MessageDialog constructor comment.
     * @param owner java.awt.Frame
     * @param title java.lang.String
     * @param modal boolean
     */
    public MessageDialog(java.awt.Frame owner, String title, boolean modal) {
        super(owner, title, modal);
    }

    /**
     * MessageDialog constructor comment.
     * @param owner java.awt.Frame
     * @param modal boolean
     */
    public MessageDialog(java.awt.Frame owner, boolean modal) {
        super(owner, modal);
    }

    /**
     * Center a component with relation to another component.
     */
    public static void centerComponent(java.awt.Component component, 
                                       java.awt.Component parent) {
        while (parent.getParent() != null) {
            parent = parent.getParent();
        }
        java.awt.Dimension parentSize = parent.getSize();
        java.awt.Dimension size = component.getSize();

        int xOffset = parent.getLocation().x;
        int yOffset = parent.getLocation().y;

        parentSize.height = parentSize.height / 2;
        parentSize.width = parentSize.width / 2;

        size.height = size.height / 2;
        size.width = size.width / 2;

        component.setLocation(parentSize.width - size.width + xOffset, 
                              parentSize.height - size.height + yOffset);
    }

    /**
     * connEtoM1:  (OKButton.action.actionPerformed(java.awt.event.ActionEvent) --> MessageDialog.dispose()V)
     * @param arg1 java.awt.event.ActionEvent
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    void connEtoM1(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.dispose();
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * main entrypoint - starts the part when it is run as an application
     * @param args java.lang.String[]
     */
    public static void displayException(Throwable exception) {
        displayMessage(exception.toString());
    }

    /**
     * main entrypoint - starts the part when it is run as an application
     * @param args java.lang.String[]
     */
    public static void displayException(Throwable exception, 
                                        java.awt.Container parent) {
        displayMessage(exception.toString(), parent);
    }

    /**
     * main entrypoint - starts the part when it is run as an application
     * @param args java.lang.String[]
     */
    public static void displayMessage(String message) {
        try {
            MessageDialog aMessageDialog;
            aMessageDialog = new MessageDialog();
            aMessageDialog.setVisible(true);
            aMessageDialog.setMessage(message);
        } catch (Throwable exception) {
            System.err.println("Exception occurred in main() of javax.swing.JDialog");
            exception.printStackTrace(System.out);
        }
    }

    /**
     * main entrypoint - starts the part when it is run as an application
     * @param args java.lang.String[]
     */
    public static void displayMessage(String message, 
                                      java.awt.Container parent) {
        try {
            MessageDialog aMessageDialog;
            aMessageDialog = new MessageDialog();
            centerComponent(aMessageDialog, parent);
            aMessageDialog.setVisible(true);
            aMessageDialog.setMessage(message);
        } catch (Throwable exception) {
            System.err.println("Exception occurred in main() of javax.swing.JDialog");
            exception.printStackTrace(System.out);
        }
    }

    /**
     * Return the JDialogContentPane property value.
     * @return javax.swing.JPanel
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JPanel getJDialogContentPane() {
        if (ivjJDialogContentPane == null) {
            try {
                ivjJDialogContentPane = new javax.swing.JPanel();
                ivjJDialogContentPane.setName("JDialogContentPane");
                ivjJDialogContentPane.setLayout(new java.awt.GridBagLayout());
                ivjJDialogContentPane.setBackground(java.awt.SystemColor.control);

                java.awt.GridBagConstraints constraintsOKButton = 
                    new java.awt.GridBagConstraints();
                constraintsOKButton.gridx = 0;
                constraintsOKButton.gridy = 1;
                constraintsOKButton.weightx = 1.0;
                constraintsOKButton.ipadx = 20;
                constraintsOKButton.insets = new java.awt.Insets(0, 0, 4, 0);
                getJDialogContentPane().add(getOKButton(), 
                                            constraintsOKButton);

                java.awt.GridBagConstraints constraintsMessageScrollPane = 
                    new java.awt.GridBagConstraints();
                constraintsMessageScrollPane.gridx = 0;
                constraintsMessageScrollPane.gridy = 0;
                constraintsMessageScrollPane.fill = 
                        java.awt.GridBagConstraints.BOTH;
                constraintsMessageScrollPane.weightx = 1.0;
                constraintsMessageScrollPane.weighty = 1.0;
                constraintsMessageScrollPane.insets = 
                        new java.awt.Insets(4, 4, 4, 4);
                getJDialogContentPane().add(getMessageScrollPane(), 
                                            constraintsMessageScrollPane);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJDialogContentPane;
    }

    public String getMessage() {
        return getMessageTextPane().getText();
    }

    /**
     * Return the MessageScrollPane property value.
     * @return javax.swing.JScrollPane
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JScrollPane getMessageScrollPane() {
        if (ivjMessageScrollPane == null) {
            try {
                ivjMessageScrollPane = new javax.swing.JScrollPane();
                ivjMessageScrollPane.setName("MessageScrollPane");
                getMessageScrollPane().setViewportView(getMessageTextPane());
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjMessageScrollPane;
    }

    /**
     * Return the MessageTextPane property value.
     * @return javax.swing.JTextPane
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JTextPane getMessageTextPane() {
        if (ivjMessageTextPane == null) {
            try {
                ivjMessageTextPane = new javax.swing.JTextPane();
                ivjMessageTextPane.setName("MessageTextPane");
                ivjMessageTextPane.setBounds(0, 0, 10, 10);
                ivjMessageTextPane.setEnabled(false);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjMessageTextPane;
    }

    /**
     * Return the OKButton property value.
     * @return javax.swing.JButton
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JButton getOKButton() {
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
        /* Uncomment the following lines to print uncaught exceptions to stdout */
        exception.printStackTrace();
    }

    /**
     * Initializes connections
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    void initConnections() throws java.lang.Exception {
        // user code begin {1}
        // user code end
        getOKButton().addActionListener(ivjEventHandler);
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
            setName("MessageDialog");
            setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
            setSize(500, 300);
            setTitle("Message");
            setContentPane(getJDialogContentPane());
            initConnections();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }

        // user code begin {2}
        // user code end
    }

    public void setMessage(String message) {
        getMessageTextPane().setText(message);
    }

    class IvjEventHandler implements java.awt.event.ActionListener {
        public void actionPerformed(java.awt.event.ActionEvent e) {
            if (e.getSource() == MessageDialog.this.getOKButton()) {
                connEtoM1(e);
            }
        }
    }
}
