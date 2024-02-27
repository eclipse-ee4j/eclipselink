/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.framework.ui;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.LoadBuildSummary;
import org.eclipse.persistence.testing.framework.TestResult;
import org.eclipse.persistence.testing.framework.TestResultsSummary;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

/**
 * Displays the loadbuild test results.
 */
public class LoadBuildDisplayPanel extends JPanel implements ActionListener, MouseListener {
    private Vector loadBuildsCache;
    private Vector<TestResult> testResultsCahce;
    private Vector<TestResultsSummary> testSummaryCahce;
    private LoadBuildSummary selectedLoadBuild;
    private final int LOADBUILD = 1;// table selection
    private final int SUMMARY = 2;
    private final int RESULT = 3;
    private int tableSelection;
    private DatabaseSession session;
    private JTable loadBuildTable;
    private JTable testResultTable;
    private JTable testSummaryTable;
    private JTabbedPane ivjLoadBuildTabbedPanel = null;
    private JPanel ivjViewPage = null;
    private JPanel ivjLoadBuildPage = null;
    private JButton ivjLoadErrorTestResultButton = null;
    private JTextArea ivjViewTextArea = null;
    private JButton ivjDeleteButton = null;
    private JScrollPane ivjViewScrollPane = null;
    private JScrollPane ivjLoadBuildScrollPane = null;
    private JTable ivjSelectedTable = null;
    private JButton ivjInspectButton = null;
    private JButton ivjUpButton = null;
    private JButton ivjViewButton = null;

    /**
     * Constructor
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    public LoadBuildDisplayPanel() {
        super();
        initialize();
    }

    /**
     * LoadBuildPanel constructor comment.
     * @param layout java.awt.LayoutManager
     */
    public LoadBuildDisplayPanel(java.awt.LayoutManager layout) {
        super(layout);
    }

    /**
     * LoadBuildPanel constructor comment.
     * @param layout java.awt.LayoutManager
     * @param isDoubleBuffered boolean
     */
    public LoadBuildDisplayPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
    }

    /**
     * LoadBuildPanel constructor comment.
     * @param isDoubleBuffered boolean
     */
    public LoadBuildDisplayPanel(boolean isDoubleBuffered) {
        super(isDoubleBuffered);
    }

    /**
     * Method to handle events for the ActionListener interface.
     * @param e java.awt.event.ActionEvent
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        // user code begin {1}
        // user code end
        if (e.getSource() == getLoadErrorTestResultButton()) {
            connEtoC2(e);
        }
        if (e.getSource() == getDeleteButton()) {
            connEtoC9(e);
        }
        if (e.getSource() == getUpButton()) {
            connEtoC1(e);
        }
        if (e.getSource() == getInspectButton()) {
            connEtoC3(e);
        }
        if (e.getSource() == getViewButton()) {
            connEtoC4(e);
        }

        // user code begin {2}
        // user code end
    }

    public void buildErrorTestResultForSummary() {
        int index = getSelectedTable().getSelectedRow();

        TestResultsSummary selectedSummary = testSummaryCahce.get(index);

        Vector<TestResult> testResults = new Vector<>();

        if ((selectedSummary.getResults() != null) && (!selectedSummary.getResults().isEmpty())) {
            for (Iterator<TestResult> iterator = selectedSummary.getResults().iterator();
                 iterator.hasNext();) {
                TestResult result = iterator.next();
                if (result.hasError() || result.hasFatalError() || result.hasProblem()) {
                    testResults.add(result);
                }
            }
        } else {
            Vector<TestResultsSummary> summariesHasResult = new Vector<>();
            for (Iterator<TestResultsSummary> iterator = selectedSummary.getLoadBuildSummary().getSummaries().iterator();
                 iterator.hasNext();) {
                TestResultsSummary summary = iterator.next();
                if ((summary.getResults() != null) && (!summary.getResults().isEmpty())) {
                    summariesHasResult.add(summary);
                }
            }
            for (Iterator<TestResultsSummary> iterator1 = summariesHasResult.iterator(); iterator1.hasNext();) {
                TestResultsSummary summary = iterator1.next();
                TestResultsSummary temp = summary;
                while (temp.getParent() != null) {
                    if (temp.getParent() == selectedSummary) {
                        for (Iterator<TestResult> iterator = summary.getResults().iterator();
                             iterator.hasNext();) {
                            TestResult result = iterator.next();
                            if (result.hasError() || result.hasFatalError() || result.hasProblem()) {
                                testResults.add(result);
                            }
                        }
                        break;
                    }
                    temp = temp.getParent();
                }
            }
        }
        testResultsCahce = testResults;
    }

    public void clearTable(JTable table) {
        table.setModel(new NonEditableDefaultTableModel());
        table.repaint();
    }

    /**
     * connEtoC1:  (UpButton.action.actionPerformed(java.awt.event.ActionEvent) --{@literal >} LoadBuildDisplayPanel.up()V)
     * @param arg1 java.awt.event.ActionEvent
     */
    private void connEtoC1(java.awt.event.ActionEvent arg1) {
        try {
            this.up();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC2:  (LoadErrorTestResultButton.action.actionPerformed(java.awt.event.ActionEvent) --{@literal >} LoadBuildDisplayPanel.loadErrorTestResults()V)
     * @param arg1 java.awt.event.ActionEvent
     */
    private void connEtoC2(java.awt.event.ActionEvent arg1) {
        try {
            this.loadErrorTestResults();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC3:  (InspectButton1.action.actionPerformed(java.awt.event.ActionEvent) --{@literal >} LoadBuildDisplayPanel.inspectSelectedLoadBuild()V)
     * @param arg1 java.awt.event.ActionEvent
     */
    private void connEtoC3(java.awt.event.ActionEvent arg1) {
        try {
            this.inspectDatabaseLogin();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC4:  (JButton1.action.actionPerformed(java.awt.event.ActionEvent) --{@literal >} LoadBuildDisplayPanel.viewStackTrace()V)
     * @param arg1 java.awt.event.ActionEvent
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connEtoC4(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.viewStackTrace();

            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC9:  (DeleteButton.action.actionPerformed(java.awt.event.ActionEvent) --{@literal >} LoadBuildDisplayPanel.deleteLoadBuild()V)
     * @param arg1 java.awt.event.ActionEvent
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connEtoC9(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.deleteLoadBuild();

            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * Comment
     */
    public void deleteLoadBuild() {
        int index = getSelectedTable().getSelectedRow();
        if (index < 0) {
            return;
        }
        int option = JOptionPane.showConfirmDialog(this, "Are you sure that  you want to remove this load build?", "", JOptionPane.OK_CANCEL_OPTION);
        if (option != JOptionPane.OK_OPTION) {
            return;
        }

        LoadBuildSummary loadBuild = (LoadBuildSummary)loadBuildsCache.get(index);
        try {
            if (loadBuild.databaseLogin == null) {
                loadBuild = (LoadBuildSummary)session.readObject(loadBuild);
            }
            session.deleteObject(loadBuild);
            loadBuildsCache.remove(index);
            poppulateLoadBuildTable(loadBuildsCache);
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    public void down() {
        int index = getSelectedTable().getSelectedRow();
        if (index < 0) {
            return;
        }
        if (tableSelection == LOADBUILD) {
            TestResultsSummary summary = (TestResultsSummary)loadBuildsCache.get(index);
            initilaizeTestSummaryCache(summary);
            if (tableSelection == RESULT) {
                poppulateTestResultTable(testResultsCahce);
            } else {
                poppulateTestSummaryTable(testSummaryCahce);
            }
        } else if (tableSelection == SUMMARY) {
            initilaizeTestSummaryCache(testSummaryCahce.get(index));
            if (tableSelection == RESULT) {
                poppulateTestResultTable(testResultsCahce);
            } else {
                poppulateTestSummaryTable(testSummaryCahce);
            }
        }
    }

    /**
     * Return the DeleteButton property value.
     * @return javax.swing.JButton
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JButton getDeleteButton() {
        if (ivjDeleteButton == null) {
            try {
                ivjDeleteButton = new javax.swing.JButton();
                ivjDeleteButton.setName("DeleteButton");
                ivjDeleteButton.setText("Delete");

                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjDeleteButton;
    }

    /**
     * Return the InspectButton1 property value.
     * @return javax.swing.JButton
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JButton getInspectButton() {
        if (ivjInspectButton == null) {
            try {
                ivjInspectButton = new javax.swing.JButton();
                ivjInspectButton.setName("InspectButton");
                ivjInspectButton.setText("Inspect Login");
                ivjInspectButton.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjInspectButton;
    }

    /**
     * Return the LoadBuildPage property value.
     * @return javax.swing.JPanel
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JPanel getLoadBuildPage() {
        if (ivjLoadBuildPage == null) {
            try {
                ivjLoadBuildPage = new javax.swing.JPanel();
                ivjLoadBuildPage.setName("LoadBuildPage");
                ivjLoadBuildPage.setLayout(new java.awt.GridBagLayout());

                java.awt.GridBagConstraints constraintsLoadBuildScrollPane = new java.awt.GridBagConstraints();
                constraintsLoadBuildScrollPane.gridx = 0;
                constraintsLoadBuildScrollPane.gridy = 0;
                constraintsLoadBuildScrollPane.gridwidth = 7;
                constraintsLoadBuildScrollPane.fill = java.awt.GridBagConstraints.BOTH;
                constraintsLoadBuildScrollPane.weightx = 1.0;
                constraintsLoadBuildScrollPane.weighty = 1.0;
                constraintsLoadBuildScrollPane.ipadx = 176;
                constraintsLoadBuildScrollPane.ipady = -67;
                constraintsLoadBuildScrollPane.insets = new java.awt.Insets(0, 0, 5, 0);
                getLoadBuildPage().add(getLoadBuildScrollPane(), constraintsLoadBuildScrollPane);

                java.awt.GridBagConstraints constraintsUpButton = new java.awt.GridBagConstraints();
                constraintsUpButton.gridx = 0;
                constraintsUpButton.gridy = 1;
                constraintsUpButton.anchor = java.awt.GridBagConstraints.WEST;
                constraintsUpButton.insets = new java.awt.Insets(0, 10, 5, 5);
                getLoadBuildPage().add(getUpButton(), constraintsUpButton);

                java.awt.GridBagConstraints constraintsDeleteButton = new java.awt.GridBagConstraints();
                constraintsDeleteButton.gridx = 2;
                constraintsDeleteButton.gridy = 1;
                constraintsDeleteButton.anchor = java.awt.GridBagConstraints.WEST;
                constraintsDeleteButton.insets = new java.awt.Insets(0, 5, 5, 5);
                getLoadBuildPage().add(getDeleteButton(), constraintsDeleteButton);

                java.awt.GridBagConstraints constraintsLoadErrorTestResultButton = new java.awt.GridBagConstraints();
                constraintsLoadErrorTestResultButton.gridx = 1;
                constraintsLoadErrorTestResultButton.gridy = 1;
                constraintsLoadErrorTestResultButton.anchor = java.awt.GridBagConstraints.WEST;
                constraintsLoadErrorTestResultButton.insets = new java.awt.Insets(0, 5, 5, 5);
                getLoadBuildPage().add(getLoadErrorTestResultButton(), constraintsLoadErrorTestResultButton);

                java.awt.GridBagConstraints constraintsInspectButton = new java.awt.GridBagConstraints();
                constraintsInspectButton.gridx = 4;
                constraintsInspectButton.gridy = 1;
                constraintsInspectButton.anchor = java.awt.GridBagConstraints.WEST;
                constraintsInspectButton.insets = new java.awt.Insets(0, 5, 5, 5);
                getLoadBuildPage().add(getInspectButton(), constraintsInspectButton);

                java.awt.GridBagConstraints constraintsViewButton = new java.awt.GridBagConstraints();
                constraintsViewButton.gridx = 6;
                constraintsViewButton.gridy = 1;
                constraintsViewButton.anchor = java.awt.GridBagConstraints.WEST;
                constraintsViewButton.insets = new java.awt.Insets(0, 5, 5, 5);
                getLoadBuildPage().add(getViewButton(), constraintsViewButton);

                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjLoadBuildPage;
    }

    /**
     * Return the JScrollPane1 property value.
     * @return javax.swing.JScrollPane
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JScrollPane getLoadBuildScrollPane() {
        if (ivjLoadBuildScrollPane == null) {
            try {
                ivjLoadBuildScrollPane = new javax.swing.JScrollPane();
                ivjLoadBuildScrollPane.setName("LoadBuildScrollPane");
                ivjLoadBuildScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                ivjLoadBuildScrollPane.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
                getLoadBuildScrollPane().setViewportView(getSelectedTable());

                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjLoadBuildScrollPane;
    }

    /**
     * Return the LoadBuildTabbedPanel property value.
     * @return javax.swing.JTabbedPane
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JTabbedPane getLoadBuildTabbedPanel() {
        if (ivjLoadBuildTabbedPanel == null) {
            try {
                ivjLoadBuildTabbedPanel = new javax.swing.JTabbedPane();
                ivjLoadBuildTabbedPanel.setName("LoadBuildTabbedPanel");
                ivjLoadBuildTabbedPanel.setBackground(java.awt.SystemColor.control);
                ivjLoadBuildTabbedPanel.insertTab("    Result   ", null, getLoadBuildPage(), null, 0);
                ivjLoadBuildTabbedPanel.insertTab("     View    ", null, getViewPage(), null, 1);

                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjLoadBuildTabbedPanel;
    }

    /**
     * Return the LoadErrorTestResultButton property value.
     * @return javax.swing.JButton
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JButton getLoadErrorTestResultButton() {
        if (ivjLoadErrorTestResultButton == null) {
            try {
                ivjLoadErrorTestResultButton = new javax.swing.JButton();
                ivjLoadErrorTestResultButton.setName("LoadErrorTestResultButton");
                ivjLoadErrorTestResultButton.setText("Load Error Tests");
                ivjLoadErrorTestResultButton.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjLoadErrorTestResultButton;
    }

    /**
     * Return the ScrollPaneTable property value.
     * @return javax.swing.JTable
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JTable getSelectedTable() {
        if (ivjSelectedTable == null) {
            try {
                ivjSelectedTable = new javax.swing.JTable();
                ivjSelectedTable.setName("SelectedTable");
                getLoadBuildScrollPane().setColumnHeaderView(ivjSelectedTable.getTableHeader());
                ivjSelectedTable.setBounds(0, 0, 200, 200);

                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjSelectedTable;
    }

    /**
     * Return the UpButton property value.
     * @return javax.swing.JButton
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JButton getUpButton() {
        if (ivjUpButton == null) {
            try {
                ivjUpButton = new javax.swing.JButton();
                ivjUpButton.setName("UpButton");
                ivjUpButton.setText("Up");

                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjUpButton;
    }

    public int getUpIndex(TestResult theResult) {
        if (theResult.getSummary() == null) {
            for (int i = 0; i < loadBuildsCache.size(); i++) {
                LoadBuildSummary loadBuild = (LoadBuildSummary)loadBuildsCache.get(i);
                if (loadBuild.timestamp.equals(theResult.getLoadBuildSummary().timestamp)) {
                    return i;
                }
            }
        } else if (theResult.getSummary().getParent() == null) {
            initilaizeTestSummaryCache(theResult.getLoadBuildSummary());
        } else {
            initilaizeTestSummaryCache(theResult.getSummary().getParent());
        }

        if (theResult.getSummary() != null) {
            for (int i = 0; i < testSummaryCahce.size(); i++) {
                TestResultsSummary summary = testSummaryCahce.get(i);
                if (summary.getName().equals(theResult.getSummary().getName())) {
                    return i;
                }
            }
        }
        return -1;
    }

    public int getUpIndex(TestResultsSummary theSummary) {
        if (theSummary.getParent() == null) {
            for (int i = 0; i < loadBuildsCache.size(); i++) {
                LoadBuildSummary loadBuild = (LoadBuildSummary)loadBuildsCache.get(i);
                if (loadBuild.timestamp.equals(theSummary.getLoadBuildSummary().timestamp)) {
                    return i;
                }
            }
        } else {
            if (theSummary.getParent().getParent() == null) {
                initilaizeTestSummaryCache(theSummary.getLoadBuildSummary());
            } else {
                initilaizeTestSummaryCache(theSummary.getParent().getParent());
            }

            for (int i = 0; i < testSummaryCahce.size(); i++) {
                TestResultsSummary summary = testSummaryCahce.get(i);
                if (summary.getName().equals(theSummary.getParent().getName())) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Return the JButton1 property value.
     * @return javax.swing.JButton
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JButton getViewButton() {
        if (ivjViewButton == null) {
            try {
                ivjViewButton = new javax.swing.JButton();
                ivjViewButton.setName("ViewButton");
                ivjViewButton.setText("View Exception");

                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjViewButton;
    }

    /**
     * Return the ViewPage property value.
     * @return javax.swing.JPanel
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JPanel getViewPage() {
        if (ivjViewPage == null) {
            try {
                ivjViewPage = new javax.swing.JPanel();
                ivjViewPage.setName("ViewPage");
                ivjViewPage.setLayout(new java.awt.GridBagLayout());
                ivjViewPage.setBackground(java.awt.SystemColor.control);
                ivjViewPage.setForeground(java.awt.SystemColor.controlText);

                java.awt.GridBagConstraints constraintsViewScrollPane = new java.awt.GridBagConstraints();
                constraintsViewScrollPane.gridx = 1;
                constraintsViewScrollPane.gridy = 1;
                constraintsViewScrollPane.fill = java.awt.GridBagConstraints.BOTH;
                constraintsViewScrollPane.weightx = 1.0;
                constraintsViewScrollPane.weighty = 1.0;
                constraintsViewScrollPane.ipadx = 947;
                constraintsViewScrollPane.ipady = 323;
                getViewPage().add(getViewScrollPane(), constraintsViewScrollPane);

                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjViewPage;
    }

    /**
     * Return the JScrollPane1 property value.
     * @return javax.swing.JScrollPane
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JScrollPane getViewScrollPane() {
        if (ivjViewScrollPane == null) {
            try {
                ivjViewScrollPane = new javax.swing.JScrollPane();
                ivjViewScrollPane.setName("ViewScrollPane");
                getViewScrollPane().setViewportView(getViewTextArea());

                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjViewScrollPane;
    }

    /**
     * Return the ViewTextArea property value.
     * @return javax.swing.JTextArea
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JTextArea getViewTextArea() {
        if (ivjViewTextArea == null) {
            try {
                ivjViewTextArea = new javax.swing.JTextArea();
                ivjViewTextArea.setName("ViewTextArea");
                ivjViewTextArea.setBounds(0, 0, 237, 19);

                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjViewTextArea;
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
    private void initConnections() {
        // user code begin {1}
        getSelectedTable().addMouseListener(this);
        // user code end
        getLoadErrorTestResultButton().addActionListener(this);
        getDeleteButton().addActionListener(this);
        getUpButton().addActionListener(this);
        getInspectButton().addActionListener(this);
        getViewButton().addActionListener(this);
    }

    /**
     * Initialize the class.
     */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void initialize() {
        try {
            // user code begin {1}
            // user code end
            setName("LoadBuildPanel");
            setLayout(new java.awt.GridBagLayout());
            setBackground(java.awt.SystemColor.control);
            setSize(640, 417);

            java.awt.GridBagConstraints constraintsLoadBuildTabbedPanel = new java.awt.GridBagConstraints();
            constraintsLoadBuildTabbedPanel.gridx = 0;
            constraintsLoadBuildTabbedPanel.gridy = 0;
            constraintsLoadBuildTabbedPanel.gridwidth = 6;
            constraintsLoadBuildTabbedPanel.gridheight = 6;
            constraintsLoadBuildTabbedPanel.fill = java.awt.GridBagConstraints.BOTH;
            constraintsLoadBuildTabbedPanel.weightx = 1.0;
            constraintsLoadBuildTabbedPanel.weighty = 1.0;
            constraintsLoadBuildTabbedPanel.insets = new java.awt.Insets(1, 1, 1, 1);
            add(getLoadBuildTabbedPanel(), constraintsLoadBuildTabbedPanel);
            initConnections();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }

        // user code begin {2}
        getUpButton().setEnabled(false);
        getLoadErrorTestResultButton().setEnabled(false);
        getDeleteButton().setEnabled(false);
        getInspectButton().setEnabled(false);
        getViewButton().setEnabled(false);
        // user code end
    }

    public void initilaizeTestSummaryCache(LoadBuildSummary loadBuild) {
        Vector<TestResultsSummary> rootSummaries = new Vector<>();

        for (Iterator<TestResultsSummary> iterator = loadBuild.getSummaries().iterator(); iterator.hasNext();) {
            TestResultsSummary summary = iterator.next();
            if (summary.getParent() == null) {
                rootSummaries.add(summary);
            }
        }
        testSummaryCahce = rootSummaries;
    }

    public void initilaizeTestSummaryCache(TestResultsSummary theSummary) {
        if ((theSummary.getResults() != null) && (!theSummary.getResults().isEmpty())) {
            tableSelection = RESULT;
            testResultsCahce = theSummary.getResults();
            return;
        }

        Vector<TestResultsSummary> children = new Vector<>();

        for (Iterator<TestResultsSummary> iterator = theSummary.getLoadBuildSummary().getSummaries().iterator();
             iterator.hasNext();) {
            TestResultsSummary summary = iterator.next();
            if ((summary.getParent() != null) && summary.getParent().getName().equals(theSummary.getName())) {
                children.add(summary);
            }
        }
        testSummaryCahce = children;
    }

    public void inspect(Object object) {
        try {
            Class<?>[] argTypes = new Class<?>[1];
            argTypes[0] = Object.class;
            Object[] args = new Object[1];
            args[0] = object;
            (Class.forName("com.ibm.uvm.tools.DebugSupport")).getMethod("inspect", argTypes).invoke(null, args);
        } catch (Exception ignore) {
        }
    }

    public void inspectDatabaseLogin() {
        int index = getSelectedTable().getSelectedRow();
        if (index < 0) {
            return;
        }
        LoadBuildSummary loadBuild = (LoadBuildSummary)loadBuildsCache.get(index);

        // inspect object if databaseLogin already exists
        if ((session == null) || (loadBuild.databaseLogin != null)) {
            inspect(loadBuild.databaseLogin);
            return;
        }

        // read in a full object
        loadBuild = (LoadBuildSummary)session.readObject(loadBuild);
        if ((loadBuildsCache != null) && (index <= loadBuildsCache.size())) {
            loadBuildsCache.remove(index);
            loadBuildsCache.add(index, loadBuild);
        }
        inspect(loadBuild.databaseLogin);
    }

    public void loadErrorTestResults() {
        buildErrorTestResultForSummary();
        poppulateTestResultTable(testResultsCahce);
    }

    /**
     * Invoked when the mouse has been clicked on a component.
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() > 1) {
            down();
        }
    }

    /**
     * Invoked when the mouse enters a component.
     */
    @Override
    public void mouseEntered(MouseEvent e) {
    }

    /**
     * Invoked when the mouse exits a component.
     */
    @Override
    public void mouseExited(MouseEvent e) {
    }

    /**
     * Invoked when a mouse button has been pressed on a component.
     */
    @Override
    public void mousePressed(MouseEvent e) {
    }

    /**
     * Invoked when a mouse button has been released on a component.
     */
    @Override
    public void mouseReleased(MouseEvent e) {
    }

    public void poppulateLoadBuildTable(Vector summaries) {
        loadBuildsCache = summaries;
        tableSelection = LOADBUILD;
        NonEditableDefaultTableModel tableModel = new NonEditableDefaultTableModel(new String[] { "Name", "Time", "Database", "OS", "JVM", "Machine", "TopLink Version", "Tests", "Setup Failures", "Errors", "Fatal Errors", "Problems", "Total Time" }, 0);
        for (Iterator iterator = summaries.iterator(); iterator.hasNext();) {
            TestResultsSummary summary = (TestResultsSummary) iterator.next();

            Vector<Object> row = new Vector<>();
            row.add(summary.getName());
            row.add(summary.getLoadBuildSummary().timestamp);
            row.add(summary.getLoadBuildSummary().loginChoice);
            row.add(summary.getLoadBuildSummary().os);
            row.add(summary.getLoadBuildSummary().jvm);
            row.add(summary.getLoadBuildSummary().machine);
            row.add(summary.getLoadBuildSummary().toplinkVersion);
            row.add(summary.getTotalTests());
            row.add(summary.getSetupFailures());
            row.add(summary.getErrors());
            row.add(summary.getFatalErrors());
            row.add(summary.getProblems());
            row.add(summary.getTotalTime());
            tableModel.addRow(row);
        }
        getSelectedTable().setModel(tableModel);
        selectFirstRow();
        resetButtons();
        getSelectedTable().repaint();
    }

    public void poppulateTestResultTable(Vector<TestResult> results) {
        Collections.sort(results);
        testResultsCahce = results;
        tableSelection = RESULT;

        resetButtons();

        NonEditableDefaultTableModel tableModel = new NonEditableDefaultTableModel(new String[] { "Name", "Outcome", "Test Time", "Total Time", "Has Exception", "Time", "Database", "OS", "JVM", "Machine", "TopLink Version" }, 0);
        for (Iterator<TestResult> iterator = results.iterator(); iterator.hasNext();) {
            TestResult result = iterator.next();
            Vector<Object> row = new Vector<>();
            row.add(result.getName());
            row.add(result.getOutcome());
            row.add(result.getTestTime());
            row.add(result.getTotalTime());
            row.add(result.getException() != null);
            row.add(result.getLoadBuildSummary().timestamp);
            row.add(result.getLoadBuildSummary().loginChoice);
            row.add(result.getLoadBuildSummary().os);
            row.add(result.getLoadBuildSummary().jvm);
            row.add(result.getLoadBuildSummary().machine);
            row.add(result.getLoadBuildSummary().toplinkVersion);
            tableModel.addRow(row);
        }
        getSelectedTable().setModel(tableModel);
        selectFirstRow();
        resetButtons();
        getSelectedTable().repaint();
    }

    public void poppulateTestSummaryTable(Vector<TestResultsSummary> summaries) {
        Collections.sort(summaries);
        testSummaryCahce = summaries;
        tableSelection = SUMMARY;

        resetButtons();

        NonEditableDefaultTableModel tableModel = new NonEditableDefaultTableModel(new String[] { "Name", "Total Tests", "Setup Failures", "Passed", "Errors", "Fatal Errors", "Problems", "Warnings", "Total Time" }, 0);
        for (Iterator<TestResultsSummary> iterator = summaries.iterator(); iterator.hasNext();) {
            TestResultsSummary summary = iterator.next();
            Vector<Object> row = new Vector<>();
            row.add(summary.getName());
            row.add(summary.getTotalTests());
            row.add(summary.getSetupFailures());
            row.add(summary.getPassed());
            row.add(summary.getErrors());
            row.add(summary.getFatalErrors());
            row.add(summary.getProblems());
            row.add(summary.getWarnings());
            row.add(summary.getTotalTime());
            tableModel.addRow(row);
        }
        getSelectedTable().setModel(tableModel);
        selectFirstRow();
        resetButtons();
        getSelectedTable().repaint();
    }

    public void resetButtons() {
        if (getSelectedTable().getRowCount() <= 0) {
            getUpButton().setEnabled(false);
            getLoadErrorTestResultButton().setEnabled(false);
            getDeleteButton().setEnabled(false);
            getInspectButton().setEnabled(false);
            getViewButton().setEnabled(false);
            return;
        }
        if (tableSelection == LOADBUILD) {
            getUpButton().setEnabled(false);
            getLoadErrorTestResultButton().setEnabled(true);
            getDeleteButton().setEnabled(true);
            getInspectButton().setEnabled(true);
            getViewButton().setEnabled(false);
            return;
        }
        if (tableSelection == SUMMARY) {
            getUpButton().setEnabled(true);
            getLoadErrorTestResultButton().setEnabled(true);
            getDeleteButton().setEnabled(false);
            getInspectButton().setEnabled(false);
            getViewButton().setEnabled(false);
            return;
        }
        if (tableSelection == RESULT) {
            getUpButton().setEnabled(true);
            getLoadErrorTestResultButton().setEnabled(false);
            getDeleteButton().setEnabled(false);
            getInspectButton().setEnabled(false);
            getViewButton().setEnabled(true);
        }
    }

    public void selectFirstRow() {
        if (getSelectedTable().getRowCount() > 0) {
            getSelectedTable().setRowSelectionInterval(0, 0);
        }
    }

    /**
     *
     * @param session org.eclipse.persistence.sessions.DatabaseSession
     */
    public void setSession(DatabaseSession session) {
        this.session = session;
    }

    public void up() {
        int index = getSelectedTable().getSelectedRow();
        int upIndex;
        if (index < 0) {
            return;
        }
        if (tableSelection == SUMMARY) {
            TestResultsSummary summary = testSummaryCahce.get(index);
            upIndex = getUpIndex(summary);
            if (summary.getParent() == null) {
                poppulateLoadBuildTable(loadBuildsCache);
            } else {
                poppulateTestSummaryTable(testSummaryCahce);
            }
            getSelectedTable().setRowSelectionInterval(upIndex, upIndex);
        } else if (tableSelection == RESULT) {
            TestResult result = testResultsCahce.get(index);
            upIndex = getUpIndex(result);
            if (result.getSummary() != null) {
                poppulateTestSummaryTable(testSummaryCahce);
            } else {
                poppulateLoadBuildTable(loadBuildsCache);
            }

            getSelectedTable().setRowSelectionInterval(upIndex, upIndex);
        }
    }

    /**
     * Comment
     */
    public void viewStackTrace() {
        getViewTextArea().setText("");
        int index = getSelectedTable().getSelectedRow();
        if (index < 0) {
            return;
        }
        TestResult result = testResultsCahce.get(index);
        getViewTextArea().setText(result.getExceptionStackTrace());
        getLoadBuildTabbedPanel().setSelectedComponent(getViewPage());
        getViewTextArea().setCaretPosition(1);
    }
}
