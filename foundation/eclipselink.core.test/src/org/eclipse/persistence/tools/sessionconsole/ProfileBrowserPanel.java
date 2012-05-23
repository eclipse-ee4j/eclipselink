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

import java.util.*;

import javax.swing.*;
import javax.swing.table.*;

import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.tools.profiler.*;
import org.eclipse.persistence.tools.beans.*;
import org.eclipse.persistence.sessions.SessionProfiler;

/**
 * This panel allows for the browsing of performance profiles.
 */
public class ProfileBrowserPanel extends JPanel {
    private List<Profile> fieldProfiles = new ArrayList();
    private JScrollPane ivjProfileScrollPane = null;
    private JTable ivjProfilesTable = null;
    private JComboBox ivjGroupByChoice = null;
    private JLabel ivjGroupByLabel = null;
    private JCheckBox ivjQualifyClassNameCheckbox = null;
    IvjEventHandler ivjEventHandler = new IvjEventHandler();

    class IvjEventHandler implements java.awt.event.ActionListener, 
                                     java.awt.event.ItemListener {
        public void actionPerformed(java.awt.event.ActionEvent e) {
            if (e.getSource() == 
                ProfileBrowserPanel.this.getQualifyClassNameCheckbox())
                connEtoC1(e);
        }

        public void itemStateChanged(java.awt.event.ItemEvent e) {
            if (e.getSource() == ProfileBrowserPanel.this.getGroupByChoice())
                connEtoC2(e);
        }
    }

    public ProfileBrowserPanel() {
        super();
        initialize();
    }

    public ProfileBrowserPanel(java.awt.LayoutManager layout) {
        super(layout);
    }

    public ProfileBrowserPanel(java.awt.LayoutManager layout, 
                               boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
    }

    public ProfileBrowserPanel(boolean isDoubleBuffered) {
        super(isDoubleBuffered);
    }

    public static void browseProfiles(Vector profiles) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            JFrame frame = new JFrame();
            ProfileBrowserPanel aProfileBrowserPanel;
            aProfileBrowserPanel = new ProfileBrowserPanel();
            frame.getContentPane().add("Center", aProfileBrowserPanel);
            frame.setSize(aProfileBrowserPanel.getSize());
            frame.setVisible(true);
            aProfileBrowserPanel.setProfiles(profiles);
        } catch (Throwable exception) {
            System.err.println("Exception occurred in main() of javax.swing.JPanel");
            exception.printStackTrace(System.out);
        }
    }

    /**
     * INTERNAL:
     * Return a map of summary profiles reporting on the profile contained.
     */
    public Vector buildProfileSummaryByClass() {
        Hashtable summaries = new Hashtable();

        for (Profile profile : getProfiles()) {
            Class domainClass = profile.getDomainClass();
            if (domainClass == null) {
                domainClass = Void.class;
            }

            Profile summary = (Profile)summaries.get(domainClass);
            if (summary == null) {
                summary = new Profile();
                summary.setDomainClass(domainClass);
                summaries.put(domainClass, summary);
            }
            summary.setTotalTime(summary.getTotalTime() + 
                                 profile.getTotalTime());
            summary.setLocalTime(summary.getLocalTime() + 
                                 profile.getLocalTime());
            summary.setNumberOfInstancesEffected(summary.getNumberOfInstancesEffected() + 
                                                 profile.getNumberOfInstancesEffected());
            for (Enumeration operationNames = 
                 profile.getOperationTimings().keys(); 
                 operationNames.hasMoreElements(); ) {
                String name = (String)operationNames.nextElement();
                Long oldTime = (Long)summary.getOperationTimings().get(name);
                long profileTime = 
                    ((Long)profile.getOperationTimings().get(name)).longValue();
                long newTime;
                if (oldTime == null) {
                    newTime = profileTime;
                } else {
                    newTime = oldTime.longValue() + profileTime;
                }
                summary.getOperationTimings().put(name, new Long(newTime));
            }
        }

        Vector summary = new Vector();
        for (Enumeration profilesEnum = summaries.elements(); 
             profilesEnum.hasMoreElements(); ) {
            summary.addElement(profilesEnum.nextElement());
        }
        return summary;
    }

    /**
     * INTERNAL:
     * Return a map of summary profiles reporting on the profile contained.
     */
    public Vector buildProfileSummaryByQuery() {
        Hashtable summaries = new Hashtable();

        for (Profile profile : getProfiles()) {
            Class queryType = profile.getQueryClass();
            //CR 3050 PWK - If we don't know the query class, we need to use void.
            //This avoids a null pointer exception when adding to the hashtable.
            if (queryType == null) {
                queryType = Void.class;
            }
            Profile summary = (Profile)summaries.get(queryType);
            if (summary == null) {
                summary = new Profile();
                summary.setQueryClass(queryType);
                summaries.put(queryType, summary);
            }
            summary.setTotalTime(summary.getTotalTime() + 
                                 profile.getTotalTime());
            summary.setLocalTime(summary.getLocalTime() + 
                                 profile.getLocalTime());
            summary.setNumberOfInstancesEffected(summary.getNumberOfInstancesEffected() + 
                                                 profile.getNumberOfInstancesEffected());
            for (Enumeration operationNames = 
                 profile.getOperationTimings().keys(); 
                 operationNames.hasMoreElements(); ) {
                String name = (String)operationNames.nextElement();
                Long oldTime = (Long)summary.getOperationTimings().get(name);
                long profileTime = 
                    ((Long)profile.getOperationTimings().get(name)).longValue();
                long newTime;
                if (oldTime == null) {
                    newTime = profileTime;
                } else {
                    newTime = oldTime.longValue() + profileTime;
                }
                summary.getOperationTimings().put(name, new Long(newTime));
            }
        }

        Vector summary = new Vector();
        for (Enumeration profilesEnum = summaries.elements(); 
             profilesEnum.hasMoreElements(); ) {
            summary.addElement(profilesEnum.nextElement());
        }
        return summary;
    }

    /**
     * INTERNAL:
     * Return a map of summary profiles reporting on the profile contained.
     */
    public Vector buildProfileSummaryByQueryAndClass() {
        Hashtable summaries = new Hashtable();

        for (Profile profile : getProfiles()) {
            Class queryType = profile.getQueryClass();
            Class queryClass = profile.getDomainClass();
            if (queryClass == null) {
                queryClass = Void.class;
            }
            //CR 3050 PWK - If we don't know the query class, we need to use void.
            //This avoids a null pointer exception when adding to the hashtable.
            if (queryType == null) {
                queryType = Void.class;
            }


            Hashtable summaryByQuery = (Hashtable)summaries.get(queryType);
            if (summaryByQuery == null) {
                summaryByQuery = new Hashtable();
                summaries.put(queryType, summaryByQuery);
            }
            Profile summary = (Profile)summaryByQuery.get(queryClass);
            if (summary == null) {
                summary = new Profile();
                summary.setQueryClass(queryType);
                summary.setDomainClass(queryClass);
                summaryByQuery.put(queryClass, summary);
            }
            summary.setTotalTime(summary.getTotalTime() + 
                                 profile.getTotalTime());
            summary.setLocalTime(summary.getLocalTime() + 
                                 profile.getLocalTime());
            summary.setNumberOfInstancesEffected(summary.getNumberOfInstancesEffected() + 
                                                 profile.getNumberOfInstancesEffected());
            for (Enumeration operationNames = 
                 profile.getOperationTimings().keys(); 
                 operationNames.hasMoreElements(); ) {
                String name = (String)operationNames.nextElement();
                Long oldTime = (Long)summary.getOperationTimings().get(name);
                long profileTime = 
                    ((Long)profile.getOperationTimings().get(name)).longValue();
                long newTime;
                if (oldTime == null) {
                    newTime = profileTime;
                } else {
                    newTime = oldTime.longValue() + profileTime;
                }
                summary.getOperationTimings().put(name, new Long(newTime));
            }
        }

        Vector summary = new Vector();
        for (Enumeration profilesEnum = summaries.elements(); 
             profilesEnum.hasMoreElements(); ) {
            for (Enumeration byQueryEnum = 
                 ((Hashtable)profilesEnum.nextElement()).elements(); 
                 byQueryEnum.hasMoreElements(); ) {
                summary.addElement(byQueryEnum.nextElement());
            }
        }
        return summary;
    }

    /**
     * connEtoC1:  (QualifyClassNameCheckbox.action.actionPerformed(java.awt.event.ActionEvent) --> ProfileBrowserPanel.resetProfiles()V)
     * @param arg1 java.awt.event.ActionEvent
     */
    private void connEtoC1(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.resetProfiles();
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC2:  (GroupByChoice.item.itemStateChanged(java.awt.event.ItemEvent) --> ProfileBrowserPanel.resetProfiles()V)
     * @param arg1 java.awt.event.ItemEvent
     */
    private void connEtoC2(java.awt.event.ItemEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.resetProfiles();
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * Return the GroupByChoice property value.
     * @return javax.swing.JComboBox
     */
    private javax.swing.JComboBox getGroupByChoice() {
        if (ivjGroupByChoice == null) {
            try {
                ivjGroupByChoice = new javax.swing.JComboBox();
                ivjGroupByChoice.setName("GroupByChoice");
                ivjGroupByChoice.setBackground(java.awt.SystemColor.window);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjGroupByChoice;
    }

    /**
     * Return the GroupByLabel property value.
     * @return javax.swing.JLabel
     */
    private javax.swing.JLabel getGroupByLabel() {
        if (ivjGroupByLabel == null) {
            try {
                ivjGroupByLabel = new javax.swing.JLabel();
                ivjGroupByLabel.setName("GroupByLabel");
                ivjGroupByLabel.setText("Group By:");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjGroupByLabel;
    }

    public List<Profile> getProfiles() {
        return fieldProfiles;
    }

    /**
     * Return the ProfileTable property value.
     * @return javax.swing.JScrollPane
     */
    private javax.swing.JScrollPane getProfileScrollPane() {
        if (ivjProfileScrollPane == null) {
            try {
                ivjProfileScrollPane = new javax.swing.JScrollPane();
                ivjProfileScrollPane.setName("ProfileScrollPane");
                ivjProfileScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                ivjProfileScrollPane.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
                ivjProfileScrollPane.setBackground(java.awt.SystemColor.window);
                getProfileScrollPane().setViewportView(getProfilesTable());
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjProfileScrollPane;
    }

    /**
     * Return the ScrollPaneTable property value.
     * @return javax.swing.JTable
     */
    private javax.swing.JTable getProfilesTable() {
        if (ivjProfilesTable == null) {
            try {
                ivjProfilesTable = new javax.swing.JTable();
                ivjProfilesTable.setName("ProfilesTable");
                getProfileScrollPane().setColumnHeaderView(ivjProfilesTable.getTableHeader());
                ivjProfilesTable.setCellSelectionEnabled(true);
                ivjProfilesTable.setColumnSelectionAllowed(true);
                ivjProfilesTable.setBounds(0, 0, 200, 200);
                ivjProfilesTable.setAutoCreateColumnsFromModel(true);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjProfilesTable;
    }

    /**
     * Return the ShortClassNameCheckbox property value.
     * @return javax.swing.JCheckBox
     */
    private javax.swing.JCheckBox getQualifyClassNameCheckbox() {
        if (ivjQualifyClassNameCheckbox == null) {
            try {
                ivjQualifyClassNameCheckbox = new javax.swing.JCheckBox();
                ivjQualifyClassNameCheckbox.setName("QualifyClassNameCheckbox");
                ivjQualifyClassNameCheckbox.setText("Qualify Class Name");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjQualifyClassNameCheckbox;
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
        getQualifyClassNameCheckbox().addActionListener(ivjEventHandler);
        getGroupByChoice().addItemListener(ivjEventHandler);
    }

    /**
     * Initialize the class.
     */
    private void initialize() {
        try {
            // user code begin {1}
            // user code end
            setName("ProfileBrowserPanel");
            setLayout(new java.awt.GridBagLayout());
            setBackground(java.awt.SystemColor.control);
            setSize(808, 522);

            java.awt.GridBagConstraints constraintsProfileScrollPane = 
                new java.awt.GridBagConstraints();
            constraintsProfileScrollPane.gridx = 0;
            constraintsProfileScrollPane.gridy = 2;
            constraintsProfileScrollPane.gridwidth = 6;
            constraintsProfileScrollPane.fill = 
                    java.awt.GridBagConstraints.BOTH;
            constraintsProfileScrollPane.weightx = 1.0;
            constraintsProfileScrollPane.weighty = 1.0;
            add(getProfileScrollPane(), constraintsProfileScrollPane);

            java.awt.GridBagConstraints constraintsQualifyClassNameCheckbox = 
                new java.awt.GridBagConstraints();
            constraintsQualifyClassNameCheckbox.gridx = 0;
            constraintsQualifyClassNameCheckbox.gridy = 1;
            constraintsQualifyClassNameCheckbox.insets = 
                    new java.awt.Insets(0, 0, 2, 0);
            add(getQualifyClassNameCheckbox(), 
                constraintsQualifyClassNameCheckbox);

            java.awt.GridBagConstraints constraintsGroupByLabel = 
                new java.awt.GridBagConstraints();
            constraintsGroupByLabel.gridx = 0;
            constraintsGroupByLabel.gridy = 0;
            constraintsGroupByLabel.anchor = java.awt.GridBagConstraints.WEST;
            add(getGroupByLabel(), constraintsGroupByLabel);

            java.awt.GridBagConstraints constraintsGroupByChoice = 
                new java.awt.GridBagConstraints();
            constraintsGroupByChoice.gridx = 5;
            constraintsGroupByChoice.gridy = 0;
            constraintsGroupByChoice.anchor = java.awt.GridBagConstraints.WEST;
            add(getGroupByChoice(), constraintsGroupByChoice);
            initConnections();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
        // user code begin {2}
        // user code end
    }

    public void resetProfiles() {
        DefaultTableModel model = new DefaultTableModel();
        Vector columns = new Vector();
        columns.addElement("Query");
        columns.addElement("Class");
        columns.addElement("Total Time");
        columns.addElement("Local Time");
        columns.addElement("# of Objects");
        columns.addElement("Object/Second");
        columns.addElement("SQL Prepare");
        columns.addElement("SQL Execute");
        columns.addElement("Row Fetch");
        columns.addElement("Cache");
        columns.addElement("Object Building");
        columns.addElement("Query Prepare");
        columns.addElement("SQL Generation");
        model.setColumnIdentifiers(columns);

        List<Profile> profiles = getProfiles();
        if (getGroupByChoice().getModel().getSelectedItem().equals("Group By Query")) {
            profiles = buildProfileSummaryByQuery();
        } else if (getGroupByChoice().getModel().getSelectedItem().equals("Group By Class")) {
            profiles = buildProfileSummaryByClass();
        } else if (getGroupByChoice().getModel().getSelectedItem().equals("Group By Class and Query")) {
            profiles = buildProfileSummaryByQueryAndClass();
        }

        for (Profile profile : profiles) {
            String items[] = new String[13];
            if (profile.getQueryClass() == null) {
                items[0] = "";
            } else {
                items[0] = Helper.getShortClassName(profile.getQueryClass());
            }
            if (profile.getDomainClass() == null) {
                items[1] = "";
            } else {
                if (getQualifyClassNameCheckbox().getModel().isSelected()) {
                    items[1] = profile.getDomainClass().getName();
                } else {
                    items[1] = 
                            Helper.getShortClassName(profile.getDomainClass());
                }
            }
            items[2] = new Long(profile.getTotalTime()).toString();
            items[3] = new Long(profile.getLocalTime()).toString();
            items[4] = 
                    new Long(profile.getNumberOfInstancesEffected()).toString();
            items[5] = new Long(profile.getObjectsPerSecond()).toString();
            if (profile.getOperationTimings().containsKey(SessionProfiler.SqlPrepare)) {
                items[6] = 
                        profile.getOperationTimings().get(SessionProfiler.SqlPrepare).toString();
            } else {
                items[6] = "";
            }
            if (profile.getOperationTimings().containsKey(SessionProfiler.StatementExecute)) {
                items[7] = 
                        profile.getOperationTimings().get(SessionProfiler.StatementExecute).toString();
            } else {
                items[7] = "";
            }
            if (profile.getOperationTimings().containsKey(SessionProfiler.RowFetch)) {
                items[8] = 
                        profile.getOperationTimings().get(SessionProfiler.RowFetch).toString();
            } else {
                items[8] = "";
            }
            if (profile.getOperationTimings().containsKey(SessionProfiler.Caching)) {
                items[9] = 
                        profile.getOperationTimings().get(SessionProfiler.Caching).toString();
            } else {
                items[9] = "";
            }
            if (profile.getOperationTimings().containsKey(SessionProfiler.ObjectBuilding)) {
                items[10] = 
                        profile.getOperationTimings().get(SessionProfiler.ObjectBuilding).toString();
            } else {
                items[10] = "";
            }
            if (profile.getOperationTimings().containsKey(SessionProfiler.QueryPreparation)) {
                items[11] = 
                        profile.getOperationTimings().get(SessionProfiler.QueryPreparation).toString();
            } else {
                items[11] = "";
            }
            if (profile.getOperationTimings().containsKey(SessionProfiler.SqlGeneration)) {
                items[12] = 
                        profile.getOperationTimings().get(SessionProfiler.SqlGeneration).toString();
            } else {
                items[12] = "";
            }

            model.addRow(items);
        }
        getProfilesTable().setModel(model);
    }

    /**
     * Sets the profiles property (java.util.Vector) value.
     * @param profiles The new value for the property.
     * @see #getProfiles
     */
    public void setProfiles(List<Profile> profiles) {
        List<Profile> oldValue = fieldProfiles;
        fieldProfiles = profiles;
        firePropertyChange("profiles", oldValue, profiles);

        resetProfiles();
    }

    protected void setup() {
        getGroupByChoice().addItem("None");
        getGroupByChoice().addItem("Group By Class");
        getGroupByChoice().addItem("Group By Query");
        getGroupByChoice().addItem("Group By Class and Query");
    }
}
