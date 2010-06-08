/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

import org.eclipse.persistence.Version;

public class TestingBrowserFrame extends javax.swing.JFrame implements java.awt.event.ActionListener {
    private javax.swing.JPanel ivjJFrameContentPane = null;
    private TestingBrowserPanel ivjTestingBrowserPanel1 = null;
    private javax.swing.JMenuItem ivjExitMenuItem = null;
    private javax.swing.JMenu ivjFile = null;
    private javax.swing.JMenuBar ivjTestingBrowserFrameJMenuBar = null;
    private javax.swing.JSeparator ivjJSeparator1 = null;
    private javax.swing.JSeparator ivjJSeparator11 = null;
    private javax.swing.JSeparator ivjJSeparator2 = null;
    private javax.swing.JMenuItem ivjKillMenuItem = null;
    private javax.swing.JMenu ivjLoadBuildMenu = null;
    private javax.swing.JMenuItem ivjLogResultsMenuItem = null;
    private javax.swing.JMenuItem ivjQueryResultsMenuItem = null;
    private javax.swing.JMenuItem ivjRebuildTestsMenuItem = null;
    private javax.swing.JMenuItem ivjResetMenuItem = null;
    private javax.swing.JMenuItem ivjResetModelsMenuItem = null;
    private javax.swing.JMenuItem ivjRunMenuItem = null;
    private javax.swing.JMenuItem ivjSaveResultsMenuItem = null;
    private javax.swing.JMenuItem ivjSetupMenuItem = null;
    private javax.swing.JMenuItem ivjStopMenuItem = null;
    private javax.swing.JMenu ivjTestMenu = null;

    /**
     * Constructor
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    public TestingBrowserFrame() {
        super();
        initialize();
    }

    /**
     * TestingBrowserFrame constructor comment.
     * @param title java.lang.String
     */
    public TestingBrowserFrame(String title) {
        super(title);
    }

    /**
     * Method to handle events for the ActionListener interface.
     * @param e java.awt.event.ActionEvent
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    public void actionPerformed(java.awt.event.ActionEvent e) {
        // user code begin {1}
        if (e.getSource() == getExitMenuItem()) {
            this.setVisible(false);
            this.dispose();
            System.exit(0);
        }

        // user code end
        if (e.getSource() == getRunMenuItem()) {
            connEtoC1(e);
        }
        if (e.getSource() == getSetupMenuItem()) {
            connEtoC2(e);
        }
        if (e.getSource() == getResetMenuItem()) {
            connEtoC3(e);
        }
        if (e.getSource() == getStopMenuItem()) {
            connEtoC4(e);
        }
        if (e.getSource() == getQueryResultsMenuItem()) {
            connEtoC5(e);
        }
        if (e.getSource() == getSaveResultsMenuItem()) {
            connEtoC6(e);
        }
        if (e.getSource() == getResetModelsMenuItem()) {
            connEtoC7(e);
        }
        if (e.getSource() == getRebuildTestsMenuItem()) {
            connEtoC8(e);
        }
        if (e.getSource() == getKillMenuItem()) {
            connEtoC9(e);
        }
        if (e.getSource() == getLogResultsMenuItem()) {
            connEtoC10(e);
        }

        // user code begin {2}
        // user code end
    }

    /**
     * connEtoC1:  (RunMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> TestingBrowserFrame.testingBrowserPanel1RunTest()V)
     * @param arg1 java.awt.event.ActionEvent
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connEtoC1(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.testingBrowserPanel1RunTest();

            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC10:  (LogResultsMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> TestingBrowserFrame.testingBrowserPanel1LogTestResults()V)
     * @param arg1 java.awt.event.ActionEvent
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connEtoC10(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.testingBrowserPanel1LogTestResults();

            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC2:  (SetupMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> TestingBrowserFrame.testingBrowserPanel1SetupTest()V)
     * @param arg1 java.awt.event.ActionEvent
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connEtoC2(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.testingBrowserPanel1SetupTest();

            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC3:  (ResetMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> TestingBrowserFrame.testingBrowserPanel1ResetTest()V)
     * @param arg1 java.awt.event.ActionEvent
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connEtoC3(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.testingBrowserPanel1ResetTest();

            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC4:  (StopMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> TestingBrowserFrame.testingBrowserPanel1StopTest()V)
     * @param arg1 java.awt.event.ActionEvent
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connEtoC4(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.testingBrowserPanel1StopTest();

            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC5:  (QueryResultsMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> TestingBrowserFrame.testingBrowserPanel1QueryLoadBuild()V)
     * @param arg1 java.awt.event.ActionEvent
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connEtoC5(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.testingBrowserPanel1QueryLoadBuild();

            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC6:  (SaveResultsMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> TestingBrowserFrame.testingBrowserPanel1SaveLoadBuild()V)
     * @param arg1 java.awt.event.ActionEvent
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connEtoC6(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.testingBrowserPanel1SaveLoadBuild();

            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC7:  (ResetModelsMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> TestingBrowserFrame.testingBrowserPanel1RefreshModels()V)
     * @param arg1 java.awt.event.ActionEvent
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connEtoC7(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.testingBrowserPanel1RefreshModels();

            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC8:  (RebuildTestsMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> TestingBrowserFrame.testingBrowserPanel1RefreshTests()V)
     * @param arg1 java.awt.event.ActionEvent
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connEtoC8(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.testingBrowserPanel1RefreshTests();

            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC9:  (KillMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> TestingBrowserFrame.testingBrowserPanel1KillTest()V)
     * @param arg1 java.awt.event.ActionEvent
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connEtoC9(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.testingBrowserPanel1KillTest();

            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * Return the ExitMenuItem property value.
     * @return javax.swing.JMenuItem
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JMenuItem getExitMenuItem() {
        if (ivjExitMenuItem == null) {
            try {
                ivjExitMenuItem = new javax.swing.JMenuItem();
                ivjExitMenuItem.setName("ExitMenuItem");
                ivjExitMenuItem.setText("Exit");
                ivjExitMenuItem.setBackground(java.awt.SystemColor.control);

                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjExitMenuItem;
    }

    /**
     * Return the File property value.
     * @return javax.swing.JMenu
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JMenu getFile() {
        if (ivjFile == null) {
            try {
                ivjFile = new javax.swing.JMenu();
                ivjFile.setName("File");
                ivjFile.setText("File");
                ivjFile.setBackground(java.awt.SystemColor.control);
                ivjFile.setActionCommand("FileMenu");
                ivjFile.add(getExitMenuItem());

                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjFile;
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
                ivjJFrameContentPane.setLayout(new java.awt.GridBagLayout());
                ivjJFrameContentPane.setBackground(java.awt.SystemColor.control);

                java.awt.GridBagConstraints constraintsTestingBrowserPanel1 = new java.awt.GridBagConstraints();
                constraintsTestingBrowserPanel1.gridx = 1;
                constraintsTestingBrowserPanel1.gridy = 1;
                constraintsTestingBrowserPanel1.fill = java.awt.GridBagConstraints.BOTH;
                constraintsTestingBrowserPanel1.weightx = 1.0;
                constraintsTestingBrowserPanel1.weighty = 1.0;
                constraintsTestingBrowserPanel1.insets = new java.awt.Insets(2, 2, 2, 2);
                getJFrameContentPane().add(getTestingBrowserPanel1(), constraintsTestingBrowserPanel1);

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
     * Return the JSeparator1 property value.
     * @return javax.swing.JSeparator
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JSeparator getJSeparator1() {
        if (ivjJSeparator1 == null) {
            try {
                ivjJSeparator1 = new javax.swing.JSeparator();
                ivjJSeparator1.setName("JSeparator1");

                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJSeparator1;
    }

    /**
     * Return the JSeparator11 property value.
     * @return javax.swing.JSeparator
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JSeparator getJSeparator11() {
        if (ivjJSeparator11 == null) {
            try {
                ivjJSeparator11 = new javax.swing.JSeparator();
                ivjJSeparator11.setName("JSeparator11");

                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJSeparator11;
    }

    /**
     * Return the JSeparator2 property value.
     * @return javax.swing.JSeparator
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JSeparator getJSeparator2() {
        if (ivjJSeparator2 == null) {
            try {
                ivjJSeparator2 = new javax.swing.JSeparator();
                ivjJSeparator2.setName("JSeparator2");

                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJSeparator2;
    }

    /**
     * Return the KillMenuItem property value.
     * @return javax.swing.JMenuItem
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JMenuItem getKillMenuItem() {
        if (ivjKillMenuItem == null) {
            try {
                ivjKillMenuItem = new javax.swing.JMenuItem();
                ivjKillMenuItem.setName("KillMenuItem");
                ivjKillMenuItem.setText("Kill");
                ivjKillMenuItem.setBackground(java.awt.SystemColor.control);

                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjKillMenuItem;
    }

    /**
     * Return the LoadBuildMenu property value.
     * @return javax.swing.JMenu
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JMenu getLoadBuildMenu() {
        if (ivjLoadBuildMenu == null) {
            try {
                ivjLoadBuildMenu = new javax.swing.JMenu();
                ivjLoadBuildMenu.setName("LoadBuildMenu");
                ivjLoadBuildMenu.setText("Test Results");
                ivjLoadBuildMenu.setBackground(java.awt.SystemColor.control);
                ivjLoadBuildMenu.setActionCommand("FileMenu");
                ivjLoadBuildMenu.add(getRebuildTestsMenuItem());
                ivjLoadBuildMenu.add(getResetModelsMenuItem());
                ivjLoadBuildMenu.add(getJSeparator11());
                ivjLoadBuildMenu.add(getLogResultsMenuItem());
                ivjLoadBuildMenu.add(getJSeparator2());
                ivjLoadBuildMenu.add(getSaveResultsMenuItem());
                ivjLoadBuildMenu.add(getQueryResultsMenuItem());

                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjLoadBuildMenu;
    }

    /**
     * Return the LogResultsMenuItem property value.
     * @return javax.swing.JMenuItem
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JMenuItem getLogResultsMenuItem() {
        if (ivjLogResultsMenuItem == null) {
            try {
                ivjLogResultsMenuItem = new javax.swing.JMenuItem();
                ivjLogResultsMenuItem.setName("LogResultsMenuItem");
                ivjLogResultsMenuItem.setText("Log Results");
                ivjLogResultsMenuItem.setBackground(java.awt.SystemColor.control);

                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjLogResultsMenuItem;
    }

    /**
     * Return the QueryResultsMenuItem property value.
     * @return javax.swing.JMenuItem
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JMenuItem getQueryResultsMenuItem() {
        if (ivjQueryResultsMenuItem == null) {
            try {
                ivjQueryResultsMenuItem = new javax.swing.JMenuItem();
                ivjQueryResultsMenuItem.setName("QueryResultsMenuItem");
                ivjQueryResultsMenuItem.setText("Query Test Results");
                ivjQueryResultsMenuItem.setBackground(java.awt.SystemColor.control);

                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjQueryResultsMenuItem;
    }

    /**
     * Return the RebuildTestsMenuItem property value.
     * @return javax.swing.JMenuItem
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JMenuItem getRebuildTestsMenuItem() {
        if (ivjRebuildTestsMenuItem == null) {
            try {
                ivjRebuildTestsMenuItem = new javax.swing.JMenuItem();
                ivjRebuildTestsMenuItem.setName("RebuildTestsMenuItem");
                ivjRebuildTestsMenuItem.setText("Rebuild Tests");
                ivjRebuildTestsMenuItem.setBackground(java.awt.SystemColor.control);

                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjRebuildTestsMenuItem;
    }

    /**
     * Return the ResetMenuItem property value.
     * @return javax.swing.JMenuItem
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JMenuItem getResetMenuItem() {
        if (ivjResetMenuItem == null) {
            try {
                ivjResetMenuItem = new javax.swing.JMenuItem();
                ivjResetMenuItem.setName("ResetMenuItem");
                ivjResetMenuItem.setText("Reset");
                ivjResetMenuItem.setBackground(java.awt.SystemColor.control);

                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjResetMenuItem;
    }

    /**
     * Return the ResetModelsMenuItem property value.
     * @return javax.swing.JMenuItem
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JMenuItem getResetModelsMenuItem() {
        if (ivjResetModelsMenuItem == null) {
            try {
                ivjResetModelsMenuItem = new javax.swing.JMenuItem();
                ivjResetModelsMenuItem.setName("ResetModelsMenuItem");
                ivjResetModelsMenuItem.setText("Reset Models");
                ivjResetModelsMenuItem.setBackground(java.awt.SystemColor.control);

                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjResetModelsMenuItem;
    }

    /**
     * Return the RunMenuItem property value.
     * @return javax.swing.JMenuItem
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JMenuItem getRunMenuItem() {
        if (ivjRunMenuItem == null) {
            try {
                ivjRunMenuItem = new javax.swing.JMenuItem();
                ivjRunMenuItem.setName("RunMenuItem");
                ivjRunMenuItem.setText("Run");
                ivjRunMenuItem.setBackground(java.awt.SystemColor.control);

                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjRunMenuItem;
    }

    /**
     * Return the SaveResultsMenuItem property value.
     * @return javax.swing.JMenuItem
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JMenuItem getSaveResultsMenuItem() {
        if (ivjSaveResultsMenuItem == null) {
            try {
                ivjSaveResultsMenuItem = new javax.swing.JMenuItem();
                ivjSaveResultsMenuItem.setName("SaveResultsMenuItem");
                ivjSaveResultsMenuItem.setText("Save Results");
                ivjSaveResultsMenuItem.setBackground(java.awt.SystemColor.control);

                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjSaveResultsMenuItem;
    }

    /**
     * Return the SetupMenuItem property value.
     * @return javax.swing.JMenuItem
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JMenuItem getSetupMenuItem() {
        if (ivjSetupMenuItem == null) {
            try {
                ivjSetupMenuItem = new javax.swing.JMenuItem();
                ivjSetupMenuItem.setName("SetupMenuItem");
                ivjSetupMenuItem.setText("Setup");
                ivjSetupMenuItem.setBackground(java.awt.SystemColor.control);

                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjSetupMenuItem;
    }

    /**
     * Return the StopMenuItem property value.
     * @return javax.swing.JMenuItem
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JMenuItem getStopMenuItem() {
        if (ivjStopMenuItem == null) {
            try {
                ivjStopMenuItem = new javax.swing.JMenuItem();
                ivjStopMenuItem.setName("StopMenuItem");
                ivjStopMenuItem.setText("Stop");
                ivjStopMenuItem.setBackground(java.awt.SystemColor.control);

                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjStopMenuItem;
    }

    /**
     * Return the TestingBrowserFrameJMenuBar property value.
     * @return javax.swing.JMenuBar
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JMenuBar getTestingBrowserFrameJMenuBar() {
        if (ivjTestingBrowserFrameJMenuBar == null) {
            try {
                ivjTestingBrowserFrameJMenuBar = new javax.swing.JMenuBar();
                ivjTestingBrowserFrameJMenuBar.setName("TestingBrowserFrameJMenuBar");
                ivjTestingBrowserFrameJMenuBar.setBackground(java.awt.SystemColor.control);
                ivjTestingBrowserFrameJMenuBar.add(getFile());
                ivjTestingBrowserFrameJMenuBar.add(getTestMenu());
                ivjTestingBrowserFrameJMenuBar.add(getLoadBuildMenu());

                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjTestingBrowserFrameJMenuBar;
    }

    /**
     * Return the TestingBrowserPanel1 property value.
     * @return org.eclipse.persistence.testing.framework.ui.TestingBrowserPanel
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private TestingBrowserPanel getTestingBrowserPanel1() {
        if (ivjTestingBrowserPanel1 == null) {
            try {
                ivjTestingBrowserPanel1 = new org.eclipse.persistence.testing.framework.ui.TestingBrowserPanel();
                ivjTestingBrowserPanel1.setName("TestingBrowserPanel1");

                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjTestingBrowserPanel1;
    }

    /**
     * Return the TestMenu property value.
     * @return javax.swing.JMenu
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JMenu getTestMenu() {
        if (ivjTestMenu == null) {
            try {
                ivjTestMenu = new javax.swing.JMenu();
                ivjTestMenu.setName("TestMenu");
                ivjTestMenu.setText("Test");
                ivjTestMenu.setBackground(java.awt.SystemColor.control);
                ivjTestMenu.setActionCommand("FileMenu");
                ivjTestMenu.add(getRunMenuItem());
                ivjTestMenu.add(getSetupMenuItem());
                ivjTestMenu.add(getResetMenuItem());
                ivjTestMenu.add(getJSeparator1());
                ivjTestMenu.add(getKillMenuItem());
                ivjTestMenu.add(getStopMenuItem());

                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjTestMenu;
    }

    /**
     * Called whenever the part throws an exception.
     * @param exception java.lang.Throwable
     */
    private void handleException(Throwable exception) {

        /* Uncomment the following lines to print uncaught exceptions to stdout */
        System.out.println("--------- UNCAUGHT EXCEPTION ---------");
        exception.printStackTrace(System.out);
    }

    /**
     * Initializes connections
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void initConnections() throws java.lang.Exception {
        // user code begin {1}
        getExitMenuItem().addActionListener(this);
        // user code end
        getRunMenuItem().addActionListener(this);
        getSetupMenuItem().addActionListener(this);
        getResetMenuItem().addActionListener(this);
        getStopMenuItem().addActionListener(this);
        getQueryResultsMenuItem().addActionListener(this);
        getSaveResultsMenuItem().addActionListener(this);
        getResetModelsMenuItem().addActionListener(this);
        getRebuildTestsMenuItem().addActionListener(this);
        getKillMenuItem().addActionListener(this);
        getLogResultsMenuItem().addActionListener(this);
    }

    /**
     * Initialize the class.
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void initialize() {
        try {
            // user code begin {1}
            // user code end
            setName("TestingBrowserFrame");
            setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
            setJMenuBar(getTestingBrowserFrameJMenuBar());
            setSize(901, 574);
            setTitle("Testing Browser" + ":  EclipseLink " + Version.getVersionString());
            setContentPane(getJFrameContentPane());
            initConnections();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }

        // user code begin {2}
        // user code end
    }

    /**
     * build a window event handler (anonymous class)
     * this handler closes the window *and* shuts down the system
     */
    protected static java.awt.event.WindowListener getWindowShutdown() {
        return new java.awt.event.WindowAdapter() {
                public void windowClosing(java.awt.event.WindowEvent e) {
                    java.awt.Window window = e.getWindow();
                    window.setVisible(false);
                    window.dispose();
                    System.exit(0);
                }
            };
    }

    /**
     * main entrypoint - starts the part when it is run as an application
     * @param args java.lang.String[]
     */
    public static void main(java.lang.String[] args) {
    try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
            TestingBrowserFrame aTestingBrowserFrame;
            aTestingBrowserFrame = new TestingBrowserFrame();
            aTestingBrowserFrame.addWindowListener(getWindowShutdown());
            aTestingBrowserFrame.setVisible(true);
        } catch (Throwable exception) {
            System.err.println("Exception occurred in main() of javax.swing.JFrame");
            exception.printStackTrace(System.out);
        }
    }

    public void testingBrowserPanel1KillTest() {
        getTestingBrowserPanel1().killTest();
    }

    public void testingBrowserPanel1LogTestResults() {
        getTestingBrowserPanel1().logTestResults();
    }

    public void testingBrowserPanel1QueryLoadBuild() {
        getTestingBrowserPanel1().queryLoadBuild();
    }

    public void testingBrowserPanel1RefreshModels() {
        getTestingBrowserPanel1().refreshModels();
    }

    public void testingBrowserPanel1RefreshTests() {
        getTestingBrowserPanel1().refreshTests();
    }

    public void testingBrowserPanel1ResetTest() {
        getTestingBrowserPanel1().resetTest();
    }

    public void testingBrowserPanel1RunTest() {
        getTestingBrowserPanel1().runTest();
    }

    public void testingBrowserPanel1SaveLoadBuild() {
        getTestingBrowserPanel1().saveLoadBuild();
    }

    public void testingBrowserPanel1SetupTest() {
        getTestingBrowserPanel1().setupTest();
    }

    public void testingBrowserPanel1StopTest() {
        getTestingBrowserPanel1().stopTest();
    }
}
