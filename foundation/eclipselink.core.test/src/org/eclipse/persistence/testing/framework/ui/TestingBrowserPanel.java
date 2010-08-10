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

import java.awt.*;
import java.io.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.*;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;
import org.eclipse.persistence.tools.sessionconsole.SessionConsolePanel;

/**
 * Main panel for the testing browser.
 * This was original done in the VA UI builder, so code is ugly, but is maintain by hand now.
 */
public class TestingBrowserPanel extends JPanel implements ItemListener, junit.framework.TestListener, SynchronizedTester {
    protected SynchronizedTestExecutor executionThread;
    protected TestExecutor executor;
    protected Vector models;
    protected junit.framework.Test currentRun;
    private JButton theKillButton = null;
    private JButton theResetButton = null;
    private JButton theRunTestButton = null;
    private SessionConsolePanel theSessionInspectorPanel = null;
    private JButton theSetupButton = null;
    private JButton theStopButton = null;
    private JScrollPane theTestsScrollPane = null;
    private JTree theTestsTree = null;
    private JSplitPane theSplitPane = null;
    private JPanel theTestPanel = null;
    private JToolBar theToolBar = null;
    private JCheckBox theHandleErrorsCheckBox = null;
    private JCheckBox runFastCheckBox = null;
    private JCheckBox logErrorsOnlyCheckBox = null;
    private JComboBox theLoginChoice = null;
    private JLabel theQuickLoginLabel = null;
    private JButton theloadBuildButton = null;
    private JLabel theCurrentTestLabel = null;
    private JTextField theCurrentTestTextField = null;
    private JProgressBar theRunModelProgressBar = null;
    private JProgressBar theRunProgressBar = null;
    private JProgressBar errorsProgressBar = null;
    private JLabel theStatusLabel = null;
    private JLabel theCurrentSuiteLabel = null;
    private JTextField theCurrentSuiteTextField = null;
    private JLabel theRunStatusLabel = null;
    private JLabel errorsLabel = null;
    TestBrowserEventHandler theEventHandler = new TestBrowserEventHandler();

    public TestingBrowserPanel() {
        super();
        initialize();
    }

    public TestingBrowserPanel(LayoutManager layout) {
        super(layout);
    }

    public TestingBrowserPanel(LayoutManager layout, boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
    }

    public TestingBrowserPanel(boolean isDoubleBuffered) {
        super(isDoubleBuffered);
    }

    /**
     * Stop the selected test.
     */
    public void finishedTest() {
        getStopButton().setEnabled(false);
        getKillButton().setEnabled(false);

        if ((getExecutionThread() != null) && getExecutionThread().shouldRunSetupOnly()) {
            TreePath path = getTestsTree().getSelectionPath();
            resetModels();
            getTestsTree().setExpandsSelectedPaths(true);
            getTestsTree().setSelectionPath(new TreePath(path.getPath()));
            getTestsTree().expandPath(path);
            getTestsTree().makeVisible(path);
            getTestsTree().invalidate();
            getTestsTree().validate();
            getTestsTree().repaint();
        } else {
            if (getSelectedEntity() instanceof TestModel) {
                Thread thread = new Thread() {
                        public void run() {
                            saveLoadBuild();
                        }
                    };
                thread.start();
            }
        }
        // Reset the session inspectors session as test model reset builds clean session.
        getSessionInspectorPanel().setSession(getExecutor().getSession());

        if (getExecutor().getSession() != null) {
            while (getExecutor().getAbstractSession().isInTransaction()) {
                getExecutor().getAbstractSession().rollbackTransaction();
            }
        }
        getSessionInspectorPanel().resetDescriptors();
        setCurrentRun(null);
        getRunProgressBar().setValue(0);
        getRunModelProgressBar().setValue(0);
        getCurrentTestTextField().setText("");
        getCurrentSuiteTextField().setText("");

        showNormalCursor();
    }

    /**
     * The run is the currently executing test model.
     */
    public junit.framework.Test getCurrentRun() {
        return currentRun;
    }

    /**
     * Return the CurrentSuiteLabel property value.
     * @return JLabel
     */
    private JLabel getCurrentSuiteLabel() {
        if (theCurrentSuiteLabel == null) {
            try {
                theCurrentSuiteLabel = new JLabel();
                theCurrentSuiteLabel.setName("CurrentSuiteLabel");
                theCurrentSuiteLabel.setText("Current Suite:");
                theCurrentSuiteLabel.setBackground(SystemColor.control);
            } catch (Throwable exception) {
                handleException(exception);
            }
        }
        return theCurrentSuiteLabel;
    }

    /**
     * Return the CurrentSuiteTextField property value.
     * @return JTextField
     */
    private JTextField getCurrentSuiteTextField() {
        if (theCurrentSuiteTextField == null) {
            try {
                theCurrentSuiteTextField = new JTextField();
                theCurrentSuiteTextField.setName("CurrentSuiteTextField");
                theCurrentSuiteTextField.setBackground(SystemColor.control);
                theCurrentSuiteTextField.setEditable(false);
            } catch (Throwable exception) {
                handleException(exception);
            }
        }
        return theCurrentSuiteTextField;
    }

    /**
     * Return the CurrentTestLabel property value.
     * @return JLabel
     */
    private JLabel getCurrentTestLabel() {
        if (theCurrentTestLabel == null) {
            try {
                theCurrentTestLabel = new JLabel();
                theCurrentTestLabel.setName("CurrentTestLabel");
                theCurrentTestLabel.setText("Current Test:");
                theCurrentTestLabel.setBackground(SystemColor.control);
            } catch (Throwable exception) {
                handleException(exception);
            }
        }
        return theCurrentTestLabel;
    }

    /**
     * Return the CurrentTestTextField property value.
     * @return JTextField
     */
    private JTextField getCurrentTestTextField() {
        if (theCurrentTestTextField == null) {
            try {
                theCurrentTestTextField = new JTextField();
                theCurrentTestTextField.setName("CurrentTestTextField");
                theCurrentTestTextField.setBackground(SystemColor.control);
                theCurrentTestTextField.setEditable(false);
            } catch (Throwable exception) {
                handleException(exception);
            }
        }
        return theCurrentTestTextField;
    }

    /**
     * Return the execution thread.
     */
    protected SynchronizedTestExecutor getExecutionThread() {
        return executionThread;
    }

    /**
     * Return the test executor.
     */
    public TestExecutor getExecutor() {
        return executor;
    }

    /**
     * Return the JCheckBox1 property value.
     * @return JCheckBox
     */
    private JCheckBox getHandleErrorsCheckBox() {
        if (theHandleErrorsCheckBox == null) {
            try {
                theHandleErrorsCheckBox = new JCheckBox();
                theHandleErrorsCheckBox.setName("HandleErrorsCheckBox");
                theHandleErrorsCheckBox.setText("Handle Errors");
                theHandleErrorsCheckBox.setBackground(SystemColor.control);
            } catch (Throwable exception) {
                handleException(exception);
            }
        }
        return theHandleErrorsCheckBox;
    }

    private JCheckBox getRunFastCheckBox() {
        if (runFastCheckBox == null) {
            try {
                runFastCheckBox = new JCheckBox();
                runFastCheckBox.setName("RunFastCheckBox");
                runFastCheckBox.setText("Fast");
                runFastCheckBox.setBackground(SystemColor.control);
            } catch (Throwable exception) {
                handleException(exception);
            }
        }
        return runFastCheckBox;
    }

    private JCheckBox getLogOnlyErrorsCheckBox() {
        if (logErrorsOnlyCheckBox == null) {
            try {
                logErrorsOnlyCheckBox = new JCheckBox();
                logErrorsOnlyCheckBox.setName("LogErrorsOnlyCheckBox");
                logErrorsOnlyCheckBox.setText("Log Errors Only");
                logErrorsOnlyCheckBox.setBackground(SystemColor.control);
            } catch (Throwable exception) {
                handleException(exception);
            }
        }
        return logErrorsOnlyCheckBox;
    }

    /**
     * Return the KillButton property value.
     * @return JButton
     */
    private JButton getKillButton() {
        if (theKillButton == null) {
            try {
                theKillButton = new JButton();
                theKillButton.setName("KillButton");
                theKillButton.setText("Kill");
                theKillButton.setBackground(SystemColor.control);
            } catch (Throwable exception) {
                handleException(exception);
            }
        }
        return theKillButton;
    }

    /**
     * Return the loadBuildButton property value.
     * @return JButton
     */
    private JButton getLoadBuildButton() {
        if (theloadBuildButton == null) {
            try {
                theloadBuildButton = new JButton();
                theloadBuildButton.setName("loadBuildButton");
                theloadBuildButton.setText("Query Test Results");
                theloadBuildButton.setBackground(SystemColor.control);
            } catch (Throwable exception) {
                handleException(exception);
            }
        }
        return theloadBuildButton;
    }

    /**
     * Return the LoginComboBox property value.
     * @return JComboBox
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JComboBox getLoginChoice() {
        if (theLoginChoice == null) {
            try {
                theLoginChoice = new JComboBox();
                theLoginChoice.setName("LoginChoice");
                theLoginChoice.setBackground(SystemColor.window);

                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return theLoginChoice;
    }

    /**
     * Return the loaded models.
     */
    public Vector getModels() {
        return models;
    }

    /**
     * Return the QuickLoginButton property value.
     * @return JLabel
     */
    private JLabel getQuickLoginLabel() {
        if (theQuickLoginLabel == null) {
            try {
                theQuickLoginLabel = new JLabel();
                theQuickLoginLabel.setName("QuickLoginLabel");
                theQuickLoginLabel.setText("Quick Login:");
                theQuickLoginLabel.setBackground(SystemColor.control);
            } catch (Throwable exception) {
                handleException(exception);
            }
        }
        return theQuickLoginLabel;
    }

    /**
     * Return the ResetButton property value.
     * @return JButton
     */
    private JButton getResetButton() {
        if (theResetButton == null) {
            try {
                theResetButton = new JButton();
                theResetButton.setName("ResetButton");
                theResetButton.setText("Reset");
                theResetButton.setBackground(SystemColor.control);
            } catch (Throwable exception) {
                handleException(exception);
            }
        }
        return theResetButton;
    }

    /**
     * Return the frame.
     */
    public Container getRoot() {
        Container parent = this;
        while (!(parent instanceof Frame)) {
            parent = parent.getParent();
            if (parent == null) {
                return this;
            }
        }
        return parent;
    }

    /**
     * Return the RunModelProgressBar property value.
     * @return JProgressBar
     */
    private JProgressBar getRunModelProgressBar() {
        if (theRunModelProgressBar == null) {
            try {
                theRunModelProgressBar = new JProgressBar();
                theRunModelProgressBar.setName("RunModelProgressBar");
                theRunModelProgressBar.setBackground(SystemColor.control);
            } catch (Throwable exception) {
                handleException(exception);
            }
        }
        return theRunModelProgressBar;
    }

    /**
     * Return the ProgressBar property value.
     * @return JProgressBar
     */
    private JProgressBar getRunProgressBar() {
        if (theRunProgressBar == null) {
            try {
                theRunProgressBar = new JProgressBar();
                theRunProgressBar.setName("RunProgressBar");
                theRunProgressBar.setBackground(SystemColor.control);
            } catch (Throwable exception) {
                handleException(exception);
            }
        }
        return theRunProgressBar;
    }

    private JProgressBar getErrorsProgressBar() {
        if (errorsProgressBar == null) {
            try {
                errorsProgressBar = new JProgressBar();
                errorsProgressBar.setName("ErrorsProgressBar");
                errorsProgressBar.setForeground(Color.RED);
                errorsProgressBar.setBackground(SystemColor.control);
            } catch (Throwable exception) {
                handleException(exception);
            }
        }
        return errorsProgressBar;
    }

    /**
     * Return the RunStatusLabel property value.
     * @return JLabel
     */
    private JLabel getRunStatusLabel() {
        if (theRunStatusLabel == null) {
            try {
                theRunStatusLabel = new JLabel();
                theRunStatusLabel.setName("RunStatusLabel");
                theRunStatusLabel.setText("Run Status:");
                theRunStatusLabel.setBackground(SystemColor.control);
            } catch (Throwable exception) {
                handleException(exception);
            }
        }
        return theRunStatusLabel;
    }

    /**
     * Return the ErrorsLabel property value.
     * @return JLabel
     */
    private JLabel getErrorsLabel() {
        if (errorsLabel == null) {
            try {
                errorsLabel = new JLabel();
                errorsLabel.setName("RrrorsLabel");
                errorsLabel.setText("Errors:");
                errorsLabel.setBackground(SystemColor.control);
            } catch (Throwable exception) {
                handleException(exception);
            }
        }
        return errorsLabel;
    }

    /**
     * Return the RunTestButton property value.
     * @return JButton
     */
    private JButton getRunTestButton() {
        if (theRunTestButton == null) {
            try {
                theRunTestButton = new JButton();
                theRunTestButton.setName("RunTestButton");
                theRunTestButton.setText("Run Test");
                theRunTestButton.setBackground(SystemColor.control);
            } catch (Throwable exception) {
                handleException(exception);
            }
        }
        return theRunTestButton;
    }

    /**
     * Return what ever is selected.
     */
    public junit.framework.Test getSelectedTest() {
        return (junit.framework.Test)getTestsTree().getSelectionPath().getLastPathComponent();
    }

    /**
     * Return what ever is selected, return null if not a TestEntity.
     */
    public TestEntity getSelectedEntity() {
        junit.framework.Test test = getSelectedTest();
        if (!(test instanceof TestEntity)) {
            return null;
        }
        return (TestEntity)test;
    }

    /**
     * Return the SessionInspectorPanel property value.
     * @return org.eclipse.persistence.tools.sessionconsole.SessionConsolePanel
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    org.eclipse.persistence.tools.sessionconsole.SessionConsolePanel getSessionInspectorPanel() {
        if (theSessionInspectorPanel == null) {
            try {
                theSessionInspectorPanel = new org.eclipse.persistence.tools.sessionconsole.SessionConsolePanel();
                theSessionInspectorPanel.setName("SessionInspectorPanel");
            } catch (Throwable exception) {
                handleException(exception);
            }
        }
        return theSessionInspectorPanel;
    }

    /**
     * Return the SetupButton property value.
     * @return JButton
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JButton getSetupButton() {
        if (theSetupButton == null) {
            try {
                theSetupButton = new JButton();
                theSetupButton.setName("SetupButton");
                theSetupButton.setText("Setup");
                theSetupButton.setBackground(SystemColor.control);

                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return theSetupButton;
    }

    /**
     * Return the SplitPane property value.
     * @return JSplitPane
     */
    private JSplitPane getSplitPane() {
        if (theSplitPane == null) {
            try {
                theSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
                theSplitPane.setName("SplitPane");
                theSplitPane.setDividerSize(8);
                theSplitPane.setOneTouchExpandable(true);
                theSplitPane.setDividerLocation(200);
                getSplitPane().add(getSessionInspectorPanel(), "right");
                getSplitPane().add(getTestPanel(), "left");
            } catch (Throwable exception) {
                handleException(exception);
            }
        }
        return theSplitPane;
    }

    /**
     * Return the StatusLabel property value.
     * @return JLabel
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JLabel getStatusLabel() {
        if (theStatusLabel == null) {
            try {
                theStatusLabel = new JLabel();
                theStatusLabel.setName("StatusLabel");
                theStatusLabel.setText("Suite Status:");
                theStatusLabel.setBackground(SystemColor.control);

                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return theStatusLabel;
    }

    /**
     * Return the StopButton property value.
     * @return JButton
     */
    private JButton getStopButton() {
        if (theStopButton == null) {
            try {
                theStopButton = new JButton();
                theStopButton.setName("StopButton");
                theStopButton.setText("Stop");
                theStopButton.setBackground(SystemColor.control);
            } catch (Throwable exception) {
                handleException(exception);
            }
        }
        return theStopButton;
    }

    /**
     * Return the TestPanel property value.
     * @return JPanel
     */
    private JPanel getTestPanel() {
        if (theTestPanel == null) {
            try {
                theTestPanel = new JPanel();
                theTestPanel.setName("TestPanel");
                theTestPanel.setLayout(new GridBagLayout());
                theTestPanel.setBackground(SystemColor.control);
                theTestPanel.setPreferredSize(new Dimension(300, 0));

                GridBagConstraints constraintsHandleErrorsCheckBox = new GridBagConstraints();
                constraintsHandleErrorsCheckBox.gridx = 0;
                constraintsHandleErrorsCheckBox.gridy = 7;
                constraintsHandleErrorsCheckBox.anchor = GridBagConstraints.WEST;
                constraintsHandleErrorsCheckBox.insets = new Insets(0, 2, 0, 0);
                getTestPanel().add(getHandleErrorsCheckBox(), constraintsHandleErrorsCheckBox);

                GridBagConstraints constraintsRunFastCheckBox = new GridBagConstraints();
                constraintsRunFastCheckBox.gridx = 0;
                constraintsRunFastCheckBox.gridy = 8;
                constraintsRunFastCheckBox.anchor = GridBagConstraints.WEST;
                constraintsRunFastCheckBox.insets = new Insets(0, 2, 0, 0);
                getTestPanel().add(getRunFastCheckBox(), constraintsRunFastCheckBox);
                
                GridBagConstraints constraintsLogOnlyErrorsCheckBox = new GridBagConstraints();
                constraintsLogOnlyErrorsCheckBox.gridx = 0;
                constraintsLogOnlyErrorsCheckBox.gridy = 9;
                constraintsLogOnlyErrorsCheckBox.anchor = GridBagConstraints.WEST;
                constraintsLogOnlyErrorsCheckBox.insets = new Insets(0, 2, 0, 0);
                getTestPanel().add(getLogOnlyErrorsCheckBox(), constraintsLogOnlyErrorsCheckBox);

                GridBagConstraints constraintsTestsScrollPane = new GridBagConstraints();
                constraintsTestsScrollPane.gridx = 0;
                constraintsTestsScrollPane.gridy = 0;
                constraintsTestsScrollPane.gridwidth = 2;
                constraintsTestsScrollPane.fill = GridBagConstraints.BOTH;
                constraintsTestsScrollPane.weightx = 1.0;
                constraintsTestsScrollPane.weighty = 1.0;
                getTestPanel().add(getTestsScrollPane(), constraintsTestsScrollPane);

                GridBagConstraints constraintsRunProgressBar = new GridBagConstraints();
                constraintsRunProgressBar.gridx = 1;
                constraintsRunProgressBar.gridy = 1;
                constraintsRunProgressBar.gridwidth = 2;
                constraintsRunProgressBar.fill = GridBagConstraints.HORIZONTAL;
                constraintsRunProgressBar.insets = new Insets(2, 2, 2, 2);
                getTestPanel().add(getRunProgressBar(), constraintsRunProgressBar);

                GridBagConstraints constraintsQuickLoginLabel = new GridBagConstraints();
                constraintsQuickLoginLabel.gridx = 0;
                constraintsQuickLoginLabel.gridy = 6;
                constraintsQuickLoginLabel.anchor = GridBagConstraints.WEST;
                constraintsQuickLoginLabel.insets = new Insets(0, 7, 0, 2);
                getTestPanel().add(getQuickLoginLabel(), constraintsQuickLoginLabel);

                GridBagConstraints constraintsLoginChoice = new GridBagConstraints();
                constraintsLoginChoice.gridx = 1;
                constraintsLoginChoice.gridy = 6;
                constraintsLoginChoice.fill = GridBagConstraints.HORIZONTAL;
                constraintsLoginChoice.insets = new Insets(2, 2, 2, 2);
                getTestPanel().add(getLoginChoice(), constraintsLoginChoice);

                GridBagConstraints constraintsRunModelProgressBar = new GridBagConstraints();
                constraintsRunModelProgressBar.gridx = 1;
                constraintsRunModelProgressBar.gridy = 4;
                constraintsRunModelProgressBar.gridwidth = 2;
                constraintsRunModelProgressBar.fill = GridBagConstraints.HORIZONTAL;
                constraintsRunModelProgressBar.insets = new Insets(0, 2, 0, 2);
                getTestPanel().add(getRunModelProgressBar(), constraintsRunModelProgressBar);
                
                GridBagConstraints constraintsErrorsLabel = new GridBagConstraints();
                constraintsErrorsLabel.gridx = 0;
                constraintsErrorsLabel.gridy = 2;
                constraintsErrorsLabel.anchor = GridBagConstraints.WEST;
                constraintsErrorsLabel.insets = new Insets(0, 7, 0, 2);
                getTestPanel().add(getErrorsLabel(), constraintsErrorsLabel);
                
                GridBagConstraints constraintsErrorsProgressBar = new GridBagConstraints();
                constraintsErrorsProgressBar.gridx = 1;
                constraintsErrorsProgressBar.gridy = 2;
                constraintsErrorsProgressBar.gridwidth = 2;
                constraintsErrorsProgressBar.fill = GridBagConstraints.HORIZONTAL;
                constraintsErrorsProgressBar.insets = new Insets(0, 3, 0, 2);
                getTestPanel().add(getErrorsProgressBar(), constraintsErrorsProgressBar);

                GridBagConstraints constraintsStatusLabel = new GridBagConstraints();
                constraintsStatusLabel.gridx = 0;
                constraintsStatusLabel.gridy = 4;
                constraintsStatusLabel.anchor = GridBagConstraints.WEST;
                constraintsStatusLabel.insets = new Insets(0, 7, 0, 2);
                getTestPanel().add(getStatusLabel(), constraintsStatusLabel);

                GridBagConstraints constraintsCurrentTestLabel = new GridBagConstraints();
                constraintsCurrentTestLabel.gridx = 0;
                constraintsCurrentTestLabel.gridy = 5;
                constraintsCurrentTestLabel.anchor = GridBagConstraints.WEST;
                constraintsCurrentTestLabel.insets = new Insets(0, 7, 0, 2);
                getTestPanel().add(getCurrentTestLabel(), constraintsCurrentTestLabel);

                GridBagConstraints constraintsCurrentTestTextField = new GridBagConstraints();
                constraintsCurrentTestTextField.gridx = 1;
                constraintsCurrentTestTextField.gridy = 5;
                constraintsCurrentTestTextField.fill = GridBagConstraints.HORIZONTAL;
                constraintsCurrentTestTextField.insets = new Insets(2, 2, 2, 2);
                getTestPanel().add(getCurrentTestTextField(), constraintsCurrentTestTextField);

                GridBagConstraints constraintsRunStatusLabel = new GridBagConstraints();
                constraintsRunStatusLabel.gridx = 0;
                constraintsRunStatusLabel.gridy = 1;
                constraintsRunStatusLabel.anchor = GridBagConstraints.WEST;
                constraintsRunStatusLabel.insets = new Insets(0, 7, 0, 2);
                getTestPanel().add(getRunStatusLabel(), constraintsRunStatusLabel);

                GridBagConstraints constraintsCurrentSuiteLabel = new GridBagConstraints();
                constraintsCurrentSuiteLabel.gridx = 0;
                constraintsCurrentSuiteLabel.gridy = 3;
                constraintsCurrentSuiteLabel.anchor = GridBagConstraints.WEST;
                constraintsCurrentSuiteLabel.insets = new Insets(0, 7, 0, 2);
                getTestPanel().add(getCurrentSuiteLabel(), constraintsCurrentSuiteLabel);

                GridBagConstraints constraintsCurrentSuiteTextField = new GridBagConstraints();
                constraintsCurrentSuiteTextField.gridx = 1;
                constraintsCurrentSuiteTextField.gridy = 3;
                constraintsCurrentSuiteTextField.fill = GridBagConstraints.HORIZONTAL;
                constraintsCurrentSuiteTextField.insets = new Insets(2, 2, 2, 2);
                getTestPanel().add(getCurrentSuiteTextField(), constraintsCurrentSuiteTextField);
            } catch (Throwable exception) {
                handleException(exception);
            }
        }
        return theTestPanel;
    }

    /**
     * Return the TestsScrollPane property value.
     * @return JScrollPane
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JScrollPane getTestsScrollPane() {
        if (theTestsScrollPane == null) {
            try {
                theTestsScrollPane = new JScrollPane();
                theTestsScrollPane.setName("TestsScrollPane");
                theTestsScrollPane.setMinimumSize(new Dimension(0, 0));
                theTestsScrollPane.setMaximumSize(new Dimension(0, 0));
                getTestsScrollPane().setViewportView(getTestsTree());

                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return theTestsScrollPane;
    }

    /**
     * Return the JTree1 property value.
     * @return JTree
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JTree getTestsTree() {
        if (theTestsTree == null) {
            try {
                theTestsTree = new JTree();
                theTestsTree.setName("TestsTree");
                theTestsTree.setBounds(0, 0, 76, 36);
                theTestsTree.setMaximumSize(new Dimension(0, 0));
                theTestsTree.setRootVisible(false);

                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return theTestsTree;
    }

    /**
     * Return the JToolBar1 property value.
     * @return JToolBar
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JToolBar getToolBar() {
        if (theToolBar == null) {
            try {
                theToolBar = new JToolBar();
                theToolBar.setName("ToolBar");
                theToolBar.setBackground(SystemColor.control);
                theToolBar.add(getRunTestButton());
                theToolBar.addSeparator();
                theToolBar.add(getSetupButton());
                theToolBar.addSeparator();
                theToolBar.add(getResetButton());
                theToolBar.addSeparator();
                theToolBar.add(getStopButton());
                theToolBar.addSeparator();
                theToolBar.add(getKillButton());
                theToolBar.addSeparator();
                getToolBar().add(getLoadBuildButton(), getLoadBuildButton().getName());

                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return theToolBar;
    }

    /**
     * Toggle the error handling logging.
     */
    public void handleErrorsChanged() {
        getExecutor().setShouldHandleErrors(getHandleErrorsCheckBox().isSelected());
    }

    /**
     * Toggle the run fast/recreate system option.
     */
    public void runFastChanged() {
        if (getRunFastCheckBox().isSelected()) {
            SchemaManager.FAST_TABLE_CREATOR = true;
        } else {
            SchemaManager.FAST_TABLE_CREATOR = false;
        }
        //TestModel.setShouldResetSystemAfterEachTestModel(!getRunFastCheckBox().isSelected());
    }

    /**
     * Toggle the run fast/recreate system option.
     */
    public void logOnlyErrorsChanged() {
        getExecutor().setShouldLogOnlyErrors(getLogOnlyErrorsCheckBox().isSelected());
    }

    /**
     * Called whenever the part throws an exception.
     * @param exception Throwable
     */
    private void handleException(Throwable exception) {
        try {
            getExecutor().getLog().write("--------- UNCAUGHT EXCEPTION ---------" + 
                                         org.eclipse.persistence.internal.helper.Helper.cr());
            exception.printStackTrace(new PrintWriter(getExecutor().getLog()));
            getExecutor().getLog().flush();
        } catch (IOException e) {
        }
    }

    /**
     * Initializes connections
     */
    private void initConnections() throws Exception {
        getRunTestButton().addActionListener(theEventHandler);
        getSetupButton().addActionListener(theEventHandler);
        getStopButton().addActionListener(theEventHandler);
        getKillButton().addActionListener(theEventHandler);
        getHandleErrorsCheckBox().addItemListener(theEventHandler);
        getRunFastCheckBox().addItemListener(theEventHandler);
        getLogOnlyErrorsCheckBox().addItemListener(theEventHandler);
        getLoginChoice().addItemListener(theEventHandler);
        getResetButton().addActionListener(theEventHandler);
        getLoadBuildButton().addActionListener(theEventHandler);
    }

    /**
     * Initialize the class.
     */
    private void initialize() {
        try {
            setName("TestingBrowserPanel");
            setLayout(new GridBagLayout());
            setBackground(SystemColor.control);
            setSize(708, 524);

            GridBagConstraints constraintsToolBar = new GridBagConstraints();
            constraintsToolBar.gridx = 0;
            constraintsToolBar.gridy = 0;
            constraintsToolBar.gridwidth = 3;
            constraintsToolBar.fill = GridBagConstraints.BOTH;
            constraintsToolBar.weightx = 1.0;
            add(getToolBar(), constraintsToolBar);

            GridBagConstraints constraintsSplitPane = new GridBagConstraints();
            constraintsSplitPane.gridx = 0;
            constraintsSplitPane.gridy = 1;
            constraintsSplitPane.fill = GridBagConstraints.BOTH;
            constraintsSplitPane.weightx = 1.0;
            constraintsSplitPane.weighty = 1.0;
            add(getSplitPane(), constraintsSplitPane);
            initConnections();
        } catch (Throwable exception) {
            handleException(exception);
        }
        setup();
    }

    /**
     * Method to handle events for the ItemListener interface.
     * @param e event.ItemEvent
     */
    public void itemStateChanged(ItemEvent e) {
        try {
            if ((e.getSource() == getHandleErrorsCheckBox())) {
                handleErrorsChanged();
            }
            if ((e.getSource() == getRunFastCheckBox())) {
                runFastChanged();
            }
            if ((e.getSource() == getLogOnlyErrorsCheckBox())) {
                logOnlyErrorsChanged();
            }
            if ((e.getSource() == getLoginChoice())) {
                loginChanged();
            }
        } catch (Throwable exception) {
            handleException(exception);
        }
    }

    /**
     * Kill the test thread.
     */
    @SuppressWarnings("deprecation")
    public void killTest() {
        if (getExecutionThread() != null) {
            getExecutionThread().stop();
            setExecutionThread(null);
        }

        finishedTest();
        // Need to use old API to be able to run with 9.0.4 jar for perf testing.
        getExecutor().getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    /**
     * Reset the login info for the platform.
     */
    public void loginChanged() {
        TestSystem system = new TestSystem();

        // Config the login to the selected platform.
        String platform = (String)getLoginChoice().getSelectedItem();
        if (platform.equals("Last Login Used")) {
            try {
                getSessionInspectorPanel().loadLoginFromFile();
                system.setLogin(getSessionInspectorPanel().getLoginEditor().getLogin());
            } catch (Throwable error) {
            }
        } else if (platform.equals("Local Oracle DB (thin)")) {
            system.useOracleThin("localhost:1521:orcl", "scott", "tiger");
        } else if (platform.equals("Oracle 11gR2 (thin)")) {
            system.useOracleThin("qaott11.ca.oracle.com:1521:toplink", "", "password");
        } else if (platform.equals("Oracle 11gR1 (thin)")) {
            system.useOracleThin("tlsvrdb7.ca.oracle.com:1521:toplink", "", "password");
        } else if (platform.equals("Oracle 10gR2 (thin)")) {
            system.useOracleThin("tlsvrdb3.ca.oracle.com:1521:toplink", "", "password");
        } else if (platform.equals("Oracle 10g (thin)")) {
            system.useOracleThin("tlsvrdb5.ca.oracle.com:1521:toplink", "", "password");
        } else if (platform.equals("Oracle 9.2 (thin)")) {
            system.useOracleThin("tlsvrdb1.ca.oracle.com:1521:toplink", "", "password");
        } else if (platform.equals("Oracle (OCI)")) {
            system.useOracleOCI();
        } else if (platform.equals("TimesTen - coredev1")) {
            system.useTimesTen("coredev1");
        } else if (platform.equals("TimesTen - coredev2")) {
            system.useTimesTen("coredev2");
        } else if (platform.equals("DB2 (App)")) {
            system.useDB2App();
        } else if (platform.equals("DB2 (Net)")) {
            system.useDB2Net();
        } else if (platform.equals("DB2 (Universal Driver)")) {
            system.useDB2UniversalDriver();
        } else if (platform.equals("DB2 (DataDirect)")) {
            system.useDB2DataDirect();
        } else if (platform.equals("Derby")) {
            system.useDerby();
        } else if (platform.equals("H2")) {
            system.useH2();
        } else if (platform.equals("HSQL")) {
            system.useHSQL();
        } else if (platform.equals("PostgreSQL")) {
            system.usePostgres();
        } else if (platform.equals("Informix IDS 11.1")) {
            system.useInformix11();
        } else if (platform.equals("Sybase (JConnect)")) {
            system.useSybaseJConnect();
        } else if (platform.equals("Sybase (DataDirect)")) {
            system.useSybaseDataDirect();
        } else if (platform.equals("MySQL (Connector/J) - COREDEV1")) {
            system.useMySQL("qa3");
        } else if (platform.equals("MySQL (Connector/J) - COREDEV2")) {
            system.useMySQL("qa4");
        } else if (platform.equals("SQLServer (Weblogic Thin)")) {
            system.useSQLServerWeblogicThin();
        } else if (platform.equals("SQLServer (MS JDBC)")) {
            system.useSQLServerMSJDBC();
        } else if (platform.equals("SQLServer (DataDirect)")) {
            system.useSQLServerDataDirect();
        } else if (platform.equals("MS Access (JDBCODBC)")) {
            system.useAccessJDBCODBC();
        } else if (platform.equals("Symfoware (RDB2_TCP)")) {
            system.useSymfowareRDB2_TCP();
        }

        DatabaseLogin login = system.getLogin();
        getExecutor().getAbstractSession().setLogin(login);
        getSessionInspectorPanel().getLoginEditor().setLogin(login);

        if ((LoadBuildSystem.loadBuild == null)) {
            return;
        }
        if(getExecutor().getSession().getLogin().getConnector() instanceof DefaultConnector) {
            LoadBuildSystem.loadBuild.loginChoice = login.getConnectionString();
        }
        LoadBuildSystem.loadBuild.databaseLogin = 
                getSessionInspectorPanel().getSession().getProject().getLogin();
    }

    /**
     * Log result for the current test running.
     */
    public void logTestResults() {
        if (getExecutionThread() != null) {
            showBusyCursor();
            try {
                getExecutor().logResultForTestEntity(getExecutionThread().getTest());
            } finally {
                showNormalCursor();
            }
        }
    }

    /**
     * Called whenever the part throws an exception.
     * @param exception Throwable
     */
    public void notifyException(Throwable exception) {
        try {
            getExecutor().getLog().write("--------- UNCAUGHT EXCEPTION ---------" + 
                                         org.eclipse.persistence.internal.helper.Helper.cr());
            exception.printStackTrace(new PrintWriter(getExecutor().getLog()));
            getExecutor().getLog().flush();
        } catch (IOException e) {
        }
    }

    /**
     * Launch the Test Result Database query tool.
     */
    public void queryLoadBuild() {
        LoadBuildBrowserFrame loadBuildBrowser = new LoadBuildBrowserFrame();
        loadBuildBrowser.setVisible(true);
        loadBuildBrowser.repaint();
        //LoadBuildBrowserFrame.main(new String[0]);
    }

    /**
     * Depending upon the login state and what test entity is selected disable
     * or enable the appropriate buttons.
     */
    public void refreshButtons() {
        boolean isLoggedIn = ((getExecutor().getSession() != null) && getExecutor().getSession().isConnected());
        TestEntity entity = getSelectedEntity();

        if (isLoggedIn || ((entity == null) || !entity.requiresDatabase())) {
            getSetupButton().setEnabled(true);
            getResetButton().setEnabled(true);
            getRunTestButton().setEnabled(true);
        } else {
            getSetupButton().setEnabled(false);
            getResetButton().setEnabled(false);
            getRunTestButton().setEnabled(false);
        }
        if (isLoggedIn) {
            getLoginChoice().setEnabled(false);
        } else {
            getLoginChoice().setEnabled(true);
        }
    }

    /**
     * Reset the models from code.
     */
    public void refreshModels() {
        showBusyCursor();
        try {
            setupDefaultModels();
            resetModels();
            getExecutor().initializeConfiguredSystems();
            getExecutor().resetLoadedModels();
            getExecutor().addLoadedModels(getModels());
        } finally {
            showNormalCursor();
        }
    }

    /**
     * Reset the models from code.
     */
    public void refreshTests() {
        showBusyCursor();
        try {
            setupDefaultModels();
            resetModels();
            getExecutor().resetLoadedModels();
            getExecutor().addLoadedModels(getModels());
        } finally {
            showNormalCursor();
        }
    }

    /**
     * Set default login information.
     */
    public void resetLogin() {
        getLoginChoice().addItem("Last Login Used");
        getLoginChoice().addItem("Local Oracle DB (thin)");
        getLoginChoice().addItem("Oracle 11gR2 (thin)");
        getLoginChoice().addItem("Oracle 11gR1 (thin)");
        getLoginChoice().addItem("Oracle 10gR2 (thin)");
        getLoginChoice().addItem("Oracle 10g (thin)");
        getLoginChoice().addItem("Oracle 9.2 (thin)");
        getLoginChoice().addItem("Oracle (OCI)");
        getLoginChoice().addItem("TimesTen - coredev1");
        getLoginChoice().addItem("TimesTen - coredev2");
        getLoginChoice().addItem("DB2 (App)");
        getLoginChoice().addItem("DB2 (Net)");
        getLoginChoice().addItem("DB2 (Universal Driver)");
        getLoginChoice().addItem("DB2 (DataDirect)");
        getLoginChoice().addItem("Derby");
        getLoginChoice().addItem("HSQL");
        getLoginChoice().addItem("H2");
        getLoginChoice().addItem("PostgreSQL");
        getLoginChoice().addItem("Informix IDS 11.1");        
        getLoginChoice().addItem("Sybase (JConnect)");
        getLoginChoice().addItem("Sybase (DataDirect)");
        getLoginChoice().addItem("MySQL (Connector/J) - COREDEV1");
        getLoginChoice().addItem("MySQL (Connector/J) - COREDEV2");
        getLoginChoice().addItem("SQLServer (DataDirect)");
        getLoginChoice().addItem("SQLServer (MS JDBC)");
        getLoginChoice().addItem("SQLServer (Weblogic Thin)");
        getLoginChoice().addItem("Symfoware (RDB2_TCP)");

        loginChanged();
    }

    /**
     * Reset the list panes on the subclasses of TestModel.
     */
    public void resetModels() {
        TestEntityTreeModel model = new TestEntityTreeModel(getModels());
        getTestsTree().setModel(model);
        getTestsTree().repaint();
    }

    /**
     * Setup the model to allow its test cases to become visible.
     */
    public void resetTest() {
        showBusyCursor();

        try {
            TestEntity test = getSelectedEntity();
            if ((test == null) || (!(test instanceof TestCollection))) {
                return;
            }
            test.resetEntity();
            resetModels();
            // Reset the session inspectors session as test model reset builds clean session.
            getSessionInspectorPanel().setSession(getExecutor().getSession());
        } finally {
            showNormalCursor();
        }
    }

    /**
     * Perform the selected test.
     */
    public void runTest() {
        junit.framework.Test test;
        showBusyCursor();

        if ((test = getSelectedTest()) == null) {
            return;
        }

        // Set the login to ensure correct in load build system.
        try {
            if(getExecutor().getSession().getDatasourceLogin() instanceof DatabaseLogin && getExecutor().getSession().getLogin().getConnector() instanceof DefaultConnector) {
                LoadBuildSystem.loadBuild.loginChoice = getExecutor().getSession().getLogin().getConnectionString();
            }
        } catch (Exception isDatasourceLogin) {
            // Ignore, but can't check first because need to support running 9.0.4.
        }
        // Configure the JPA tests login and logging.
        Map properties = org.eclipse.persistence.testing.framework.junit.JUnitTestCaseHelper.getDatabaseProperties();
        if(getExecutor().getSession().getDatasourceLogin() instanceof DatabaseLogin && getExecutor().getSession().getLogin().getConnector() instanceof DefaultConnector) {
            properties.put(PersistenceUnitProperties.JDBC_DRIVER, getExecutor().getSession().getLogin().getDriverClassName());
            properties.put(PersistenceUnitProperties.JDBC_URL, getExecutor().getSession().getLogin().getConnectionString());
            properties.put(PersistenceUnitProperties.TARGET_DATABASE, getExecutor().getSession().getDatasourceLogin().getPlatform().getClass().getName());
            properties.put(PersistenceUnitProperties.JDBC_USER, getExecutor().getSession().getDatasourceLogin().getUserName());
            properties.put(PersistenceUnitProperties.JDBC_PASSWORD, getExecutor().getSession().getDatasourceLogin().getPassword());
            properties.put(PersistenceUnitProperties.LOGGING_LEVEL, getExecutor().getSession().getSessionLog().getLevelString());
        }
        TestExecutor.setDefaultJUnitTestResult(null);
        TestExecutor.setJUnitTestResults(null);
        setExecutionThread(new SynchronizedTestExecutor(getExecutor(), test, this));
        getExecutionThread().start();
        getStopButton().setEnabled(true);
        getKillButton().setEnabled(true);
    }

    public void saveLoadBuild() {
        if ((LoadBuildSystem.loadBuild == null) || LoadBuildSystem.loadBuild.isEmpty()) {
            return;
        }
        showBusyCursor();
        LoadBuildSystem.loadBuild.userName = getSelectedEntity().getName();
        if (getExecutor().getSession().getDatasourceLogin() instanceof DatabaseLogin) {
            if(getExecutor().getSession().getLogin().getConnector() instanceof DefaultConnector) {
                LoadBuildSystem.loadBuild.loginChoice = getExecutor().getSession().getLogin().getConnectionString();
            }
            LoadBuildSystem.loadBuild.databaseLogin = getSessionInspectorPanel().getSession().getProject().getLogin();
        }
     /** TODO: Save these results to a DB available to Eclipse
        LoadBuildSystem loadBuildSystem = new LoadBuildSystem();
        loadBuildSystem.saveLoadBuild();
        showNormalCursor();
        */
    }

    /**
     * The run is the currently executing test model.
     */
    public void setCurrentRun(junit.framework.Test currentRun) {
        this.currentRun = currentRun;
    }

    /**
     * Set the execution thread.
     */
    protected void setExecutionThread(SynchronizedTestExecutor thread) {
        executionThread = thread;
    }

    /**
     * Set the test executor.
     */
    public void setExecutor(TestExecutor executor) {
        this.executor = executor;
    }

    /**
     * Return the loaded models.
     */
    public void setModels(Vector models) {
        this.models = models;
    }

    /**
     * The lists must be refreshed when first open.
     */
    public void setup() {
        showBusyCursor();
        try {
            DatabaseSession session = new Project(new DatabaseLogin()).createDatabaseSession();
            setExecutor(new TestExecutor());
            getExecutor().setListener(this);
            getExecutor().setSession(session);
            getSessionInspectorPanel().setSession(session);
            setupDefaultModels();
            resetModels();
            resetLogin();
            handleErrorsChanged();
            loginChanged();
            getLogOnlyErrorsCheckBox().setSelected(true);
        } finally {
            showNormalCursor();
        }
    }

    /**
     * Reset the models from code.
     */
    public void setupDefaultModels() {
        Vector allModels = new Vector();

        Class testModelClass;

        // Look for standard tests.
        try {
            testModelClass = Class.forName("org.eclipse.persistence.testing.tests.TestRunModel");
            java.lang.reflect.Method buildTestsMethod = testModelClass.getMethod("buildAllModels", new Class[0]);
            Vector result = (Vector)buildTestsMethod.invoke(null, new Object[0]);
            Helper.addAllToVector(allModels, result);
        } catch (Exception exception) {
            System.out.println("Problems loading BasicTestModel " + exception.toString());
            exception.printStackTrace();
        }

        setModels(allModels);
    }

    /**
     * Setup the model to allow its test cases to become visible.
     */
    public void setupTest() {
        showBusyCursor();

        TestEntity entity = getSelectedEntity();
        if ((entity == null) || (!(entity instanceof TestCollection))) {
            showNormalCursor();
            return;
        }
        TestCollection test = (TestCollection)entity;

        test.setExecutor(getExecutor());
        setExecutionThread(new SynchronizedTestExecutor(getExecutor(), test, this));
        getExecutionThread().setShouldRunSetupOnly(true);
        getExecutionThread().start();
        getStopButton().setEnabled(true);
        getKillButton().setEnabled(true);
    }

    /**
     * Change to the busy cursor.
     */
    public void showBusyCursor() {
        Container parent = this;
        while (parent != null) {
            parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            parent = parent.getParent();
        }
    }

    /**
     * Change from the busy cursor.
     */
    public void showNormalCursor() {
        Container parent = this;
        while (parent != null) {
            parent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            parent = parent.getParent();
        }
    }

    /**
     * Stop the selected test.
     */
    public void stopTest() {
        if (getExecutionThread() != null) {
            getExecutionThread().stopExecution();
        } else {
            finishedTest();
        }
    }

    /**
     * Increment the errors bar.
     */
    public void addError(junit.framework.Test test, Throwable error) {
        if (!getExecutor().shouldHandleErrors()) {
            throw new TestErrorException(error.getMessage(), error);
        }
        getErrorsProgressBar().setValue(getErrorsProgressBar().getValue() + 1);
        // Expand the errors size if maxed out.
        if (getErrorsProgressBar().getValue() >= getErrorsProgressBar().getMaximum()) {
            getErrorsProgressBar().setMaximum(getErrorsProgressBar().getMaximum() * 2);
        }
    }

    /**
     * Increment the errors bar.
     */
    public void addFailure(junit.framework.Test test, junit.framework.AssertionFailedError error) {
        if (!getExecutor().shouldHandleErrors()) {
            throw new TestErrorException(error.getMessage(), error);
        }
        getErrorsProgressBar().setValue(getErrorsProgressBar().getValue() + 1);
        // Expand the errors size if maxed out.
        if (getErrorsProgressBar().getValue() >= getErrorsProgressBar().getMaximum()) {
            getErrorsProgressBar().setMaximum(getErrorsProgressBar().getMaximum() * 2);
        }
    }
    
    /**
     * Move the progress bar.
     */
    public void endTest(junit.framework.Test test) {
        getRunModelProgressBar().setValue(getRunModelProgressBar().getValue() + 1);        
        if (test instanceof TestEntity) {
            TestEntity testEntity = (TestEntity)test;
            if ((testEntity instanceof TestCase) && (((TestCase)testEntity).getTestResult().hasFailed())) {
                getErrorsProgressBar().setValue(getErrorsProgressBar().getValue() + 1);
            } else if ((testEntity instanceof TestCollection) && ((TestResultsSummary)testEntity.getReport()).didSetupFail()) {
                getErrorsProgressBar().setValue(getErrorsProgressBar().getValue() + 1);
            }
            // Expand the errors size if maxed out.
            if (getErrorsProgressBar().getValue() >= getErrorsProgressBar().getMaximum()) {
                getErrorsProgressBar().setMaximum(getErrorsProgressBar().getMaximum() * 2);
            }
            if (testEntity.getContainer() == getCurrentRun()) {
                getRunProgressBar().setMaximum(((TestCollection)testEntity.getContainer()).getTests().size());
                getRunProgressBar().setValue(getRunProgressBar().getValue() + 1);                
            }
        }
        // Reset the session inspectors session as test model reset builds clean session.
        getSessionInspectorPanel().setSession(getExecutor().getSession());
    }

    /**
     * Move the progress bar.
     */
    public void startTest(junit.framework.Test test) {
        if (test instanceof junit.framework.TestSuite) {
            junit.framework.TestSuite suite = (junit.framework.TestSuite)test;
            getRunModelProgressBar().setValue(0);
            getRunModelProgressBar().setMaximum(suite.countTestCases());
            getCurrentSuiteTextField().setText(suite.getName());
            if (getCurrentRun() == null) {
                setCurrentRun(test);
                getRunProgressBar().setValue(0);
                getRunProgressBar().setMaximum(suite.countTestCases());
                getErrorsProgressBar().setMaximum(10);
                getErrorsProgressBar().setValue(0);
            }
            getCurrentTestTextField().setText(suite.getName());
        } else if (test instanceof junit.framework.TestCase) {
            getCurrentTestTextField().setText(((junit.framework.TestCase)test).getName());
            if (!(test instanceof TestEntity)) {
                getCurrentSuiteTextField().setText(Helper.getShortClassName(test.getClass()));
            }
        }
    }

    class TestBrowserEventHandler implements ActionListener, ItemListener {
        public void actionPerformed(ActionEvent e) {
            try {
                if (e.getSource() == TestingBrowserPanel.this.getRunTestButton()) {
                    runTest();
                }
                if (e.getSource() == TestingBrowserPanel.this.getSetupButton()) {
                    setupTest();
                }
                if (e.getSource() == TestingBrowserPanel.this.getStopButton()) {
                    stopTest();
                }
                if (e.getSource() == TestingBrowserPanel.this.getKillButton()) {
                    killTest();
                }
                if (e.getSource() == TestingBrowserPanel.this.getResetButton()) {
                    resetTest();
                }
                if (e.getSource() == TestingBrowserPanel.this.getLoadBuildButton()) {
                    queryLoadBuild();
                }
            } catch (Throwable exception) {
                handleException(exception);
            }
        }

        public void itemStateChanged(ItemEvent e) {
            try {
                if (e.getSource() == TestingBrowserPanel.this.getHandleErrorsCheckBox()) {
                    handleErrorsChanged();
                }
                if (e.getSource() == TestingBrowserPanel.this.getRunFastCheckBox()) {
                    runFastChanged();
                }
                if (e.getSource() == TestingBrowserPanel.this.getLogOnlyErrorsCheckBox()) {
                    logOnlyErrorsChanged();
                }
                if (e.getSource() == TestingBrowserPanel.this.getLoginChoice()) {
                    loginChanged();
                }
            } catch (Throwable exception) {
                handleException(exception);
            }
        }
    }
}
