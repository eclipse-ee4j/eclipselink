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
package org.eclipse.persistence.tools.sessionconsole;

import java.lang.reflect.Method;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.identitymaps.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.tools.beans.*;
import org.eclipse.persistence.tools.profiler.*;
import org.eclipse.persistence.sessions.factories.*;

/**
 * Main panel for the session console. The session console is used by the
 * testing browser.
 */
public class SessionConsolePanel extends JPanel implements ActionListener,
        ItemListener, MouseListener, java.beans.PropertyChangeListener,
        ListSelectionListener {
    protected int compileCount = 0;
    protected Vector results;
    protected Vector cacheResults;
    private Session fieldSession = null;
    private JButton mBrowseProfileButton = null;
    private JMenuItem mBrowseProfileMenuItem = null;
    // SQL Page.
    private JPanel sqlPage = null;
    private JPopupMenu sqlPagePopupMenu = null;
    private JTable sqlPageTable = null;
    private JMenuItem mClearSQLMenuItem = null;
    private JMenuItem mCopySQLMenuItem = null;
    private JMenuItem mCutSQLMenuItem = null;
    private JMenuItem mDeleteSQLMenuItem = null;
    private JMenuItem mInsertSQLMenuItem = null;
    private JMenuItem mSelectSQLMenuItem = null;
    private JButton mSQLExecuteButton = null;
    private JPopupMenu mSQLMenu = null;
    private JScrollPane mSQLScrollPane = null;
    private JTextPane mSQLText = null;
    // JPQL Page.
    private JButton jpqlExecuteButton = null;
    private JTextPane jpqlText = null;

    // Cache Page.
    private JPanel cachePage = null;
    private JButton mInspectCacheButton = null;
    private JMenuItem mInspectCacheMenuItem = null;
    private JButton mClearCacheButton = null;
    private JMenuItem mClearCacheMenuItem = null;
    private JMenuItem mSQLSelectMenuItem = null;
    private JMenu mSQLTemplateMenu = null;
    private JMenuItem mSQLUpdateMenuItem = null;
    private JScrollPane cacheScrollPane = null;

    private JList mClassList = null;
    private JScrollPane mClassScrollPane = null;
    private JButton mClearButton = null;
    private JMenuItem mClearDescriptorsMenuItem = null;
    private JButton mClearLogButton = null;
    private JMenuItem cutMenuItem = null;
    private JMenuItem copyMenuItem = null;
    private JMenuItem pasteMenuItem = null;
    private JMenuItem mClearLogMenuItem = null;
    private JMenuItem mClearResultsMenuItem = null;
    private JPopupMenu mDescriptorMenu = null;
    private JMenuItem mExecuteQueryMenuItem = null;
    private ExpressionPanel mExpressionPanel = null;
    private JCheckBoxMenuItem mFullNamesMenuItem = null;
    private JMenuItem mInspectDescriptorMenuItem = null;
    private JMenuItem mInspectResultMenuItem = null;
    private JMenuItem mInspectSessionMenuItem1 = null;
    private JPopupMenu mJPopupMenu1 = null;
    private JSeparator mJSeparator4 = null;
    private JSeparator mJSeparator5 = null;
    private JSeparator mJSeparator6 = null;
    private JSeparator mJSeparator7 = null;
    private JButton mLoadProjectButton = null;
    private JTabbedPane mLogBook = null;
    private JComboBox logLevelChoice = null;
    private JButton mLoginButton = null;
    private LoginEditorPanel mLoginEditorPanel = null;
    private JPopupMenu mLoginMenu = null;
    private JMenuItem mLoginMenuItem = null;
    private JPanel mLoginPage = null;
    private JPopupMenu mLogMenu = null;
    private JButton mLogoutButton = null;
    private JMenuItem mLogoutMenuItem = null;
    private JPanel mLogPage = null;
    private JCheckBox mLogProfileCheckbox = null;
    private JCheckBoxMenuItem mLogProfileMenuItem = null;
    private JCheckBoxMenuItem mLogProfileMenuItem1 = null;
    private JScrollPane mLogScrollPane = null;
    private JSplitPane mLogSplitter = null;
    private JTextArea mLogText = null;
    private JMenuItem mPasteSQLMenuItem = null;
    private JCheckBox mProfileCheckbox = null;
    private JButton mQueryButton = null;
    private JPopupMenu mQueryMenu = null;
    private JPanel mQueryPage = null;
    private JMenuItem mResetDescriptorsMenuItem = null;
    private JPanel mResultPage = null;
    private JScrollPane mResultsScrollPane = null;
    private JTable mResultsTable = null;
    private JButton mSelectButton = null;

    // Java Page.
    private JPanel javaPage = null;
    private JScrollPane javaScrollPane = null;
    private JTextPane javaText = null;
    private JButton javaExecuteButton = null;
    private JButton javaClearButton = null;

    private JPanel mTopPanel = null;
    private JSplitPane mTopSplitPane = null;
    private JMenuItem mUpdateSQLMenuItem = null;
    private JTabbedPane mWorkspaceBook = null;

    // This attribute is used only for debugging and currently it's not
    // accessible from the ui.
    // If set to true it indicates that the DefaultConnector
    // should be substituted for JNDIConnector, and externalConnectionPooling
    // should be used.
    // Works only on Oracle, ignored on other platforms.
    private boolean shouldUseJndiConnector = false;

    /**
     * Constructor
     */
    public SessionConsolePanel() {
        super();
        initialize();
    }

    /**
     * Method to handle events for the ActionListener interface.
     */
    public void actionPerformed(java.awt.event.ActionEvent e) {
        try {
            if (e.getSource() == getCutMenuItem()) {
                cutLog();
            }
            if (e.getSource() == getCopyMenuItem()) {
                cutLog();
            }
            if (e.getSource() == getPasteMenuItem()) {
                cutLog();
            }
            if (e.getSource() == getLoginButton()) {
                login();
            }
            if (e.getSource() == getLogoutButton()) {
                logout();
            }
            if (e.getSource() == getLoadProjectButton()) {
                loadProject();
            }
            if (e.getSource() == getClearLogButton()) {
                clearLog();
            }
            if (e.getSource() == getBrowseProfileButton()) {
                browseProfile();
            }
            if (e.getSource() == getSelectButton()) {
                selectSQL();
            }
            if (e.getSource() == getSQLExecuteButton()) {
                executeSQL();
            }
            if (e.getSource() == getJPQLExecuteButton()) {
                executeJPQL();
            }
            if (e.getSource() == getJavaExecuteButton()) {
                executeJava();
            }
            if (e.getSource() == getClearButton()) {
                clearSQL();
            }
            if (e.getSource() == getJavaClearButton()) {
                clearJava();
            }
            if (e.getSource() == getClearCacheButton()) {
                clearCache();
            }
            if (e.getSource() == getInspectCacheButton()) {
                inspectFromCache();
            }
            if (e.getSource() == getQueryButton()) {
                executeQuery();
            }
            if (e.getSource() == getClearCacheMenuItem()) {
                clearCache();
            }
            if (e.getSource() == getInspectCacheMenuItem()) {
                inspectFromCache();
            }
            if (e.getSource() == getClearResultsMenuItem()) {
                clearResults();
            }
            if (e.getSource() == getInspectResultMenuItem()) {
                inspectResult();
            }
            if (e.getSource() == getInspectDescriptorMenuItem()) {
                inspectDescriptor();
            }
            if (e.getSource() == getClearDescriptorsMenuItem()) {
                clearDescriptors();
            }
            if (e.getSource() == getResetDescriptorsMenuItem()) {
                resetDescriptors();
            }
            if (e.getSource() == getInsertSQLMenuItem()) {
                templateSQLInsert();
            }
            if (e.getSource() == getUpdateSQLMenuItem()) {
                templateSQLUpdate();
            }
            if (e.getSource() == getDeleteSQLMenuItem()) {
                templateSQLDelete();
            }
            if (e.getSource() == getSelectSQLMenuItem()) {
                templateSQLSelect();
            }
            if (e.getSource() == getSQLUpdateMenuItem()) {
                executeSQL();
            }
            if (e.getSource() == getSQLSelectMenuItem()) {
                selectSQL();
            }
            if (e.getSource() == getClearSQLMenuItem()) {
                clearSQL();
            }
            if (e.getSource() == getCutSQLMenuItem()) {
                cutSQL();
            }
            if (e.getSource() == getCopySQLMenuItem()) {
                copySQL();
            }
            if (e.getSource() == getPasteSQLMenuItem()) {
                pasteSQL();
            }
            if (e.getSource() == getLoginMenuItem()) {
                login();
            }
            if (e.getSource() == getLogoutMenuItem()) {
                logout();
            }
            if (e.getSource() == getInspectSessionMenuItem1()) {
                inspectSession();
            }
            if (e.getSource() == getExecuteQueryMenuItem()) {
                executeQuery();
            }
            if (e.getSource() == getClearLogMenuItem()) {
                clearLog();
            }
            if (e.getSource() == getBrowseProfileMenuItem()) {
                browseProfile();
            }
            if (e.getSource() == getFullNamesMenuItem()) {
                resetDescriptors();
            }
        } catch (Throwable error) {
            handleException(error);
        }
    }

    public void browseProfile() {
        if ((getSession()).isInProfile()) {
            ProfileBrowser.browseProfiles(((PerformanceProfiler) (getSession())
                    .getProfiler()).getProfiles());
        }
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

    public void clearCache() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        ;
        resetCache();
    }

    public void clearDescriptors() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        ((AbstractSession) getSession())
                .setProject(new org.eclipse.persistence.sessions.Project());
        resetDescriptors();
    }

    public void clearLog() {
        getLogText().setText("");
    }

    public void copyLog() {
        getLogText().copy();
    }

    public void cutLog() {
        getLogText().cut();
    }

    public void pasteLog() {
        getLogText().paste();
    }

    public void clearResults() {
        setResults(new Vector());
        DefaultTableModel model = new DefaultTableModel();
        getResultsTable().setModel(model);
        getResultsTable().repaint();
    }

    public void clearSQL() {
        getSQLText().setText("");
    }

    public void clearJava() {
        getJavaText().setText("");
    }

    /**
     * connEtoC14:
     * (LogProfileCheckbox.item.itemStateChanged(java.awt.event.ItemEvent) -->
     * SessionInspectorPanel.logProfileChanged()V)
     * 
     * @param arg1
     *                java.awt.event.ItemEvent
     */
    private void connEtoC14(java.awt.event.ItemEvent arg1) {
        try {
            this.logProfileChanged();
        } catch (Throwable exception) {
            handleException(exception);
        }
    }

    /**
     * connEtoC15:
     * (ProfileCheckbox.item.itemStateChanged(java.awt.event.ItemEvent) -->
     * SessionInspectorPanel.profileChanged()V)
     * 
     * @param arg1
     *                java.awt.event.ItemEvent
     */
    private void connEtoC15(java.awt.event.ItemEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.profileChanged();
            // user code begin {2}
            // user code end
        } catch (Throwable exception) {
            // user code begin {3}
            // user code end
            handleException(exception);
        }
    }

    /**
     * connEtoC16: (LogCheckbox.item.itemStateChanged(java.awt.event.ItemEvent)
     * --> SessionInspectorPanel.loggingChanged()V)
     * 
     * @param arg1
     *                java.awt.event.ItemEvent
     */
    private void connEtoC16(java.awt.event.ItemEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.loggingChanged();
            // user code begin {2}
            // user code end
        } catch (Throwable exception) {
            // user code begin {3}
            // user code end
            handleException(exception);
        }
    }

    /**
     * connEtoC18: (SQLText.mouse.mouseReleased(java.awt.event.MouseEvent) -->
     * SessionInspectorPanel.genericPopupDisplay(Ljava.awt.event.MouseEvent;LJPopupMenu;)V)
     * 
     * @param arg1
     *                java.awt.event.MouseEvent
     */
    private void connEtoC18(java.awt.event.MouseEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.genericPopupDisplay(arg1, getSQLMenu());
            // user code begin {2}
            // user code end
        } catch (Throwable exception) {
            // user code begin {3}
            // user code end
            handleException(exception);
        }
    }

    /**
     * connEtoC19: (CacheTable.mouse.mouseReleased(java.awt.event.MouseEvent)
     * -->
     * SessionInspectorPanel.genericPopupDisplay(Ljava.awt.event.MouseEvent;LJPopupMenu;)V)
     * 
     * @param arg1
     *                java.awt.event.MouseEvent
     */
    private void connEtoC19(java.awt.event.MouseEvent arg1) {
        try {
            this.genericPopupDisplay(arg1, getCachePopupMenu());
        } catch (Throwable exception) {
            handleException(exception);
        }
    }

    /**
     * connEtoC20:
     * (ResultsScrollPane.mouse.mouseReleased(java.awt.event.MouseEvent) -->
     * SessionInspectorPanel.genericPopupDisplay(Ljava.awt.event.MouseEvent;LJPopupMenu;)V)
     * 
     * @param arg1
     *                java.awt.event.MouseEvent
     */
    private void connEtoC20(java.awt.event.MouseEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.genericPopupDisplay(arg1, getJPopupMenu1());
            // user code begin {2}
            // user code end
        } catch (Throwable exception) {
            // user code begin {3}
            // user code end
            handleException(exception);
        }
    }

    /**
     * connEtoC21: (ClassList.mouse.mouseReleased(java.awt.event.MouseEvent) -->
     * SessionInspectorPanel.genericPopupDisplay(Ljava.awt.event.MouseEvent;LJPopupMenu;)V)
     * 
     * @param arg1
     *                java.awt.event.MouseEvent
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    void connEtoC21(java.awt.event.MouseEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.genericPopupDisplay(arg1, getDescriptorMenu());
            // user code begin {2}
            // user code end
        } catch (Throwable exception) {
            // user code begin {3}
            // user code end
            handleException(exception);
        }
    }

    /**
     * connEtoC3:
     * (ClassList.listSelection.valueChanged(event.ListSelectionEvent) -->
     * SessionInspectorPanel.resetCache()V)
     * 
     * @param arg1
     *                event.ListSelectionEvent
     */
    private void connEtoC3(ListSelectionEvent arg1) {
        try {
            this.descriptorChanged();
        } catch (Throwable exception) {
            handleException(exception);
        }
    }

    /**
     * connEtoC40: (QueryPage.mouse.mouseReleased(java.awt.event.MouseEvent) -->
     * SessionInspectorPanel.genericPopupDisplay(Ljava.awt.event.MouseEvent;LJPopupMenu;)V)
     * 
     * @param arg1
     *                java.awt.event.MouseEvent
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    void connEtoC40(java.awt.event.MouseEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.genericPopupDisplay(arg1, getQueryMenu());
            // user code begin {2}
            // user code end
        } catch (Throwable exception) {
            // user code begin {3}
            // user code end
            handleException(exception);
        }
    }

    /**
     * connEtoC41: (LogText.mouse.mouseReleased(java.awt.event.MouseEvent) -->
     * SessionInspectorPanel.genericPopupDisplay(Ljava.awt.event.MouseEvent;LJPopupMenu;)V)
     * 
     * @param arg1
     *                java.awt.event.MouseEvent
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    void connEtoC41(java.awt.event.MouseEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.genericPopupDisplay(arg1, getLogMenu());
            // user code begin {2}
            // user code end
        } catch (Throwable exception) {
            // user code begin {3}
            // user code end
            handleException(exception);
        }
    }

    /**
     * connEtoC42: (LoginPage.mouse.mouseReleased(java.awt.event.MouseEvent) -->
     * SessionInspectorPanel.genericPopupDisplay(Ljava.awt.event.MouseEvent;LJPopupMenu;)V)
     * 
     * @param arg1
     *                java.awt.event.MouseEvent
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    void connEtoC42(java.awt.event.MouseEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.genericPopupDisplay(arg1, getLoginMenu());
            // user code begin {2}
            // user code end
        } catch (Throwable exception) {
            // user code begin {3}
            // user code end
            handleException(exception);
        }
    }

    /**
     * connPtoP1SetTarget: (BrowseProfileMenuItem.enabled <-->
     * BrowseProfileButton.enabled)
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    void connPtoP1SetTarget() {
        /* Set the target from the source */
        try {
            getBrowseProfileButton().setEnabled(
                    getBrowseProfileMenuItem().isEnabled());
            // user code begin {1}
            // user code end
        } catch (Throwable exception) {
            // user code begin {3}
            // user code end
            handleException(exception);
        }
    }

    /**
     * connPtoP2SetTarget: (LogProfileMenuItem.selected <-->
     * LogProfileCheckbox.selected)
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    void connPtoP2SetTarget() {
        /* Set the target from the source */
        try {
            getLogProfileCheckbox().setSelected(
                    getLogProfileMenuItem().isSelected());
            // user code begin {1}
            // user code end
        } catch (Throwable exception) {
            // user code begin {3}
            // user code end
            handleException(exception);
        }
    }

    /**
     * connPtoP3SetTarget: (ProfileCheckbox.selected <-->
     * LogProfileMenuItem1.selected)
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    void connPtoP3SetTarget() {
        /* Set the target from the source */
        try {
            getLogProfileMenuItem1().setSelected(
                    getProfileCheckbox().isSelected());
            // user code begin {1}
            // user code end
        } catch (Throwable exception) {
            // user code begin {3}
            // user code end
            handleException(exception);
        }
    }

    /**
     * connPtoP4SetTarget: (LogSQLMenuItem.selected <--> LogCheckbox.selected)
     */
    private void connPtoP4SetTarget() {
        try {
            getLogLevelChoice().setSelectedIndex(
                    getSession().getSessionLog().getLevel());
        } catch (Throwable exception) {
            handleException(exception);
        }
    }

    public void copySQL() {
        getSQLText().copy();
    }

    public void createTables() {
        // //Helper.toDo("table creators");
    }

    public void cutSQL() {
        getSQLText().cut();
    }

    public void descriptorChanged() {
        resetCache();
        ClassInfo info = (ClassInfo) getClassList().getSelectedValue();
        if (info != null) {
            getExpressionPanel().setDescriptor(info.descriptor);
        }
    }

    public void executeQuery() {
        showBusyCursor();
        try {
            ClassInfo info = (ClassInfo) getClassList().getSelectedValue();
            if (info == null) {
                return;
            }

            ReadAllQuery query = new ReadAllQuery(getExpressionPanel()
                    .getDescriptor().getJavaClass());
            query.setSelectionCriteria(getExpressionPanel().getExpression());

            setResultObjects((Vector) getSession().executeQuery(query),
                    getExpressionPanel().getDescriptor());

            getLogBook().setSelectedComponent(getResultPage());
        } finally {
            showNormalCursor();
        }
    }

    public void executeSQL() {
        showBusyCursor();
        try {
            String sql = getSQLText().getSelectedText();
            if ((sql == null) || (sql.length() == 0)) {
                return;
            }
            getSession().executeNonSelectingCall(new SQLCall(sql));
        } finally {
            showNormalCursor();
        }
    }
    
    public void executeJPQL() {
        showBusyCursor();
        try {
            getSession().getProject().getJPQLParseCache().getCache().clear();
            
            String jpql = getJPQLText().getSelectedText();
            if ((jpql == null) || (jpql.length() == 0)) {
                return;
            }
            ReportQuery query = new ReportQuery();
            query.setCall(new JPQLCall(jpql));

            setResultReports((Vector) getSession().executeQuery(query));
            getLogBook().setSelectedComponent(getResultPage());
        } finally {
            showNormalCursor();
        }
    }

    /**
     * Execute the selected Java code. This is done through compiling and
     * executing the code through sun.tools. TopLink imports are auto defined,
     * "session" variable can be used for the connected session.
     */
    public void executeJava() {
        showBusyCursor();
        try {
            String java = getJavaText().getSelectedText();
            if ((java == null) || (java.length() == 0)) {
                return;
            }

            try {
                compileCount++;
                String className = "JavaCode" + compileCount;
                String[] source = { className + ".java" };
                File file = new File(className + ".java");
                file.createNewFile();
                FileWriter writer = new FileWriter(file);
                writer.write("import java.util.*;\n");
                writer.write("import org.eclipse.persistence.sessions.*;\n");
                writer.write("import org.eclipse.persistence.expressions.*;\n");
                writer.write("import org.eclipse.persistence.queries.*;\n");
                writer.write("public class " + className + " {\n");
                writer.write("public Session session;\n");
                writer.write("public Object exec() {\n");
                writer.write(java);
                if (java.indexOf("return") < 0) {
                    writer.write("return \"success\";\n");
                }
                writer.write("}\n");
                writer.write("}\n");
                writer.flush();
                writer.close();

                // done reflectively to remove dependency on tools jar
                Object[] params = new Object[1];
                params[0] = source;
                Class mainClass = Class.forName("com.sun.tools.javac.Main");
                Class[] parameterTypes = new Class[1];
                parameterTypes[0] = String[].class;
                Method method = mainClass.getMethod("compile", parameterTypes);
                int result = ((Integer) method.invoke(null, params)).intValue();
                if (result != 0) {
                    throw new RuntimeException(
                            "Java code compile failed. This could either be a legitimate compile "
                                    + "failure, or could result if you do not have the tools.jar from your JDK on the classpath.");
                }
                Class newClass = Class.forName(className);
                Object newInstance = newClass.newInstance();
                newClass.getField("session").set(newInstance, getSession());
                Object value;
                try {
                    value = newClass.getMethod("exec", (Class[]) null).invoke(
                            newInstance, (Object[]) null);
                } catch (java.lang.reflect.InvocationTargetException exception) {
                    throw exception.getCause();
                }
                inspect(value);
            } catch (Throwable exception) {
                if (exception instanceof RuntimeException) {
                    throw (RuntimeException) exception;
                } else {
                    throw new RuntimeException(exception.toString());
                }
            }
        } finally {
            showNormalCursor();
        }
    }

    public void genericPopupDisplay(MouseEvent mouseEvent, JPopupMenu menu) {
        if (mouseEvent.isPopupTrigger()) {
            menu.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent
                    .getY());
        }
    }

    /**
     * Return the BrowseProfileButton property value.
     * 
     * @return JButton
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    JButton getBrowseProfileButton() {
        if (mBrowseProfileButton == null) {
            try {
                mBrowseProfileButton = new JButton();
                mBrowseProfileButton.setName("BrowseProfileButton");
                mBrowseProfileButton.setText("Browse Profile");
                mBrowseProfileButton
                        .setBackground(java.awt.SystemColor.control);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return mBrowseProfileButton;
    }

    /**
     * Return the BrowseProfileMenuItem property value.
     * 
     * @return JMenuItem
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    JMenuItem getBrowseProfileMenuItem() {
        if (mBrowseProfileMenuItem == null) {
            try {
                mBrowseProfileMenuItem = new JMenuItem();
                mBrowseProfileMenuItem.setName("BrowseProfileMenuItem");
                mBrowseProfileMenuItem.setText("Browse Profile");
                mBrowseProfileMenuItem.setBackground(java.awt.SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return mBrowseProfileMenuItem;
    }

    private JPanel getSQLPage() {
        if (sqlPage == null) {
            try {
                sqlPage = new JPanel();
                sqlPage.setName("SQL");
                sqlPage.setLayout(new java.awt.GridBagLayout());
                sqlPage.setBackground(java.awt.SystemColor.control);

                java.awt.GridBagConstraints constraintsSQLExecuteButton = new java.awt.GridBagConstraints();
                constraintsSQLExecuteButton.gridx = 1;
                constraintsSQLExecuteButton.gridy = 1;
                constraintsSQLExecuteButton.insets = new java.awt.Insets(2, 2,
                        2, 2);
                getSQLPage().add(getSQLExecuteButton(),
                        constraintsSQLExecuteButton);

                java.awt.GridBagConstraints constraintsClearButton = new java.awt.GridBagConstraints();
                constraintsClearButton.gridx = 2;
                constraintsClearButton.gridy = 1;
                constraintsClearButton.insets = new java.awt.Insets(2, 2, 2, 2);
                getSQLPage().add(getClearButton(), constraintsClearButton);

                java.awt.GridBagConstraints constraintsSelectButton = new java.awt.GridBagConstraints();
                constraintsSelectButton.gridx = 0;
                constraintsSelectButton.gridy = 1;
                constraintsSelectButton.insets = new java.awt.Insets(2, 2, 2, 2);
                getSQLPage().add(getSelectButton(), constraintsSelectButton);

                java.awt.GridBagConstraints constraintsSQLScrollPane = new java.awt.GridBagConstraints();
                constraintsSQLScrollPane.gridx = 0;
                constraintsSQLScrollPane.gridy = 0;
                constraintsSQLScrollPane.gridwidth = 5;
                constraintsSQLScrollPane.fill = java.awt.GridBagConstraints.BOTH;
                constraintsSQLScrollPane.weightx = 1.0;
                constraintsSQLScrollPane.weighty = 1.0;
                constraintsSQLScrollPane.insets = new java.awt.Insets(0, 0, 2,
                        0);
                getSQLPage().add(getSQLScrollPane(), constraintsSQLScrollPane);
            } catch (Throwable exception) {
                handleException(exception);
            }
        }
        return sqlPage;
    }

    private JPanel getJPQLPage() {
        JPanel page = new JPanel();
        page.setName("JPQL");
        page.setLayout(new GridBagLayout());
        page.setBackground(SystemColor.control);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.insets = new Insets(2, 2, 2, 2);
        page.add(getJPQLExecuteButton(), constraints);

        constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 5;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.insets = new Insets(0, 0, 2, 0);
        page.add(getJPQLScrollPane(), constraints);
        return page;
    }

    private JPanel getJavaPage() {
        if (javaPage == null) {
            try {
                javaPage = new JPanel();
                javaPage.setName("Java");
                javaPage.setLayout(new GridBagLayout());
                javaPage.setBackground(SystemColor.control);

                GridBagConstraints constraints = new GridBagConstraints();
                constraints.gridx = 1;
                constraints.gridy = 1;
                constraints.insets = new Insets(2, 2, 2, 2);
                getJavaPage().add(getJavaExecuteButton(), constraints);

                constraints = new GridBagConstraints();
                constraints.gridx = 2;
                constraints.gridy = 1;
                constraints.insets = new Insets(2, 2, 2, 2);
                getJavaPage().add(getJavaClearButton(), constraints);

                constraints = new GridBagConstraints();
                constraints.gridx = 0;
                constraints.gridy = 0;
                constraints.gridwidth = 5;
                constraints.fill = GridBagConstraints.BOTH;
                constraints.weightx = 1.0;
                constraints.weighty = 1.0;
                constraints.insets = new Insets(0, 0, 2, 0);
                getJavaPage().add(getJavaScrollPane(), constraints);
            } catch (Throwable exception) {
                handleException(exception);
            }
        }
        return javaPage;
    }

    /**
     * Return the SQLPage property value.
     * 
     * @return JPanel
     */
    private JPanel getCachePage() {
        if (cachePage == null) {
            try {
                cachePage = new JPanel();
                cachePage.setName("CachePage");
                cachePage.setLayout(new GridBagLayout());
                cachePage.setBackground(SystemColor.control);

                GridBagConstraints constraintsClearCacheButton = new GridBagConstraints();
                constraintsClearCacheButton.gridx = 0;
                constraintsClearCacheButton.gridy = 1;
                constraintsClearCacheButton.insets = new Insets(2, 2, 2, 2);
                getCachePage().add(getClearCacheButton(),
                        constraintsClearCacheButton);

                GridBagConstraints constraintsCacheScrollPane = new GridBagConstraints();
                constraintsCacheScrollPane.gridx = 0;
                constraintsCacheScrollPane.gridy = 0;
                constraintsCacheScrollPane.gridwidth = 6;
                constraintsCacheScrollPane.fill = GridBagConstraints.BOTH;
                constraintsCacheScrollPane.weightx = 1.0;
                constraintsCacheScrollPane.weighty = 1.0;
                constraintsCacheScrollPane.insets = new Insets(0, 0, 2, 0);
                getCachePage().add(getCacheScrollPane(),
                        constraintsCacheScrollPane);

                GridBagConstraints constraintsInspectCacheButton = new GridBagConstraints();
                constraintsInspectCacheButton.gridx = 1;
                constraintsInspectCacheButton.gridy = 1;
                constraintsInspectCacheButton.insets = new Insets(2, 2, 2, 2);
                getCachePage().add(getInspectCacheButton(),
                        constraintsInspectCacheButton);
            } catch (Throwable exception) {
                handleException(exception);
            }
        }
        return cachePage;
    }

    /**
     * Return the CachePopupMenu property value.
     * 
     * @return JPopupMenu
     */
    private JPopupMenu getCachePopupMenu() {
        if (sqlPagePopupMenu == null) {
            try {
                sqlPagePopupMenu = new JPopupMenu();
                sqlPagePopupMenu.setName("CachePopupMenu");
                sqlPagePopupMenu.add(getClearCacheMenuItem());
                sqlPagePopupMenu.add(getInspectCacheMenuItem());
            } catch (Throwable exception) {
                handleException(exception);
            }
        }
        return sqlPagePopupMenu;
    }

    public Vector getCacheResults() {
        return cacheResults;
    }

    /**
     * Return the CacheScrollPane property value.
     * 
     * @return JScrollPane
     */
    private JScrollPane getCacheScrollPane() {
        if (cacheScrollPane == null) {
            try {
                cacheScrollPane = new JScrollPane();
                cacheScrollPane.setName("CacheScrollPane");
                getCacheScrollPane().setViewportView(getCacheTable());
            } catch (Throwable exception) {
                handleException(exception);
            }
        }
        return cacheScrollPane;
    }

    /**
     * Return the CacheTable property value.
     * 
     * @return JTable
     */
    private JTable getCacheTable() {
        if (sqlPageTable == null) {
            try {
                sqlPageTable = new JTable();
                sqlPageTable.setName("CacheTable");
                getCacheScrollPane().setColumnHeaderView(
                        sqlPageTable.getTableHeader());
                sqlPageTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
                sqlPageTable.setPreferredSize(new Dimension(600, 300));
                sqlPageTable.setBounds(0, 0, 583, 370);
                sqlPageTable.setPreferredScrollableViewportSize(new Dimension(
                        600, 300));
            } catch (Throwable exception) {
                handleException(exception);
            }
        }
        return sqlPageTable;
    }

    /**
     * Return the ClassList property value.
     * 
     * @return JList
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    JList getClassList() {
        if (mClassList == null) {
            try {
                mClassList = new JList();
                mClassList.setName("ClassList");
                mClassList
                        .setBorder(new javax.swing.plaf.basic.BasicBorders.MarginBorder());
                mClassList.setBounds(0, 0, 160, 428);
                mClassList.setMinimumSize(new Dimension(1, 1));
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return mClassList;
    }

    /**
     * Return the ClassScrollPane property value.
     * 
     * @return JScrollPane
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    JScrollPane getClassScrollPane() {
        if (mClassScrollPane == null) {
            try {
                mClassScrollPane = new JScrollPane();
                mClassScrollPane.setName("ClassScrollPane");
                mClassScrollPane.setMaximumSize(new Dimension(0, 0));
                mClassScrollPane.setPreferredSize(new Dimension(120, 0));
                mClassScrollPane.setMinimumSize(new Dimension(100, 0));
                getClassScrollPane().setViewportView(getClassList());
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return mClassScrollPane;
    }

    /**
     * Return the ClearButton property value.
     * 
     * @return JButton
     */
    private JButton getClearButton() {
        if (mClearButton == null) {
            try {
                mClearButton = new JButton();
                mClearButton.setName("ClearButton");
                mClearButton.setText("Clear");
                mClearButton.setBackground(SystemColor.control);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return mClearButton;
    }

    private JButton getJavaClearButton() {
        if (javaClearButton == null) {
            try {
                javaClearButton = new JButton();
                javaClearButton.setName("JavaClearButton");
                javaClearButton.setText("Clear");
                javaClearButton.setBackground(SystemColor.control);
            } catch (Throwable exception) {
                handleException(exception);
            }
        }
        return javaClearButton;
    }

    /**
     * Return the ClearCacheButton property value.
     * 
     * @return JButton
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    JButton getClearCacheButton() {
        if (mClearCacheButton == null) {
            try {
                mClearCacheButton = new JButton();
                mClearCacheButton.setName("ClearCacheButton");
                mClearCacheButton.setText("Clear Cache");
                mClearCacheButton.setBackground(SystemColor.control);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return mClearCacheButton;
    }

    /**
     * Return the ClearCacheMenuItem property value.
     * 
     * @return JMenuItem
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    JMenuItem getClearCacheMenuItem() {
        if (mClearCacheMenuItem == null) {
            try {
                mClearCacheMenuItem = new JMenuItem();
                mClearCacheMenuItem.setName("ClearCacheMenuItem");
                mClearCacheMenuItem.setText("Clear Cache");
                mClearCacheMenuItem.setBackground(SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return mClearCacheMenuItem;
    }

    /**
     * Return the ClearDescriptorsMenuItem property value.
     * 
     * @return JMenuItem
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    JMenuItem getClearDescriptorsMenuItem() {
        if (mClearDescriptorsMenuItem == null) {
            try {
                mClearDescriptorsMenuItem = new JMenuItem();
                mClearDescriptorsMenuItem.setName("ClearDescriptorsMenuItem");
                mClearDescriptorsMenuItem.setText("Clear Descriptors");
                mClearDescriptorsMenuItem.setBackground(SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return mClearDescriptorsMenuItem;
    }

    /**
     * Return the ClearLogButton property value.
     * 
     * @return JButton
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    JButton getClearLogButton() {
        if (mClearLogButton == null) {
            try {
                mClearLogButton = new JButton();
                mClearLogButton.setName("ClearLogButton");
                mClearLogButton.setText("Clear");
                mClearLogButton.setBackground(SystemColor.control);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return mClearLogButton;
    }

    /**
     * Return the ClearLogMenuItem property value.
     * 
     * @return JMenuItem
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    JMenuItem getClearLogMenuItem() {
        if (mClearLogMenuItem == null) {
            try {
                mClearLogMenuItem = new JMenuItem();
                mClearLogMenuItem.setName("ClearLogMenuItem");
                mClearLogMenuItem.setText("Clear Log");
                mClearLogMenuItem.setBackground(SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return mClearLogMenuItem;
    }

    private JMenuItem getCutMenuItem() {
        if (cutMenuItem == null) {
            try {
                cutMenuItem = new JMenuItem();
                cutMenuItem.setName("CutMenuItem");
                cutMenuItem.setText("Cut");
                cutMenuItem.setBackground(SystemColor.menu);
            } catch (Throwable exception) {
                handleException(exception);
            }
        }
        return cutMenuItem;
    }

    private JMenuItem getCopyMenuItem() {
        if (copyMenuItem == null) {
            try {
                copyMenuItem = new JMenuItem();
                copyMenuItem.setName("CopyMenuItem");
                copyMenuItem.setText("Copy");
                copyMenuItem.setBackground(SystemColor.menu);
            } catch (Throwable exception) {
                handleException(exception);
            }
        }
        return copyMenuItem;
    }

    private JMenuItem getPasteMenuItem() {
        if (pasteMenuItem == null) {
            try {
                pasteMenuItem = new JMenuItem();
                pasteMenuItem.setName("PasteMenuItem");
                pasteMenuItem.setText("Paste");
                pasteMenuItem.setBackground(SystemColor.menu);
            } catch (Throwable exception) {
                handleException(exception);
            }
        }
        return pasteMenuItem;
    }

    /**
     * Return the ClearResultsMenuItem property value.
     * 
     * @return JMenuItem
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    JMenuItem getClearResultsMenuItem() {
        if (mClearResultsMenuItem == null) {
            try {
                mClearResultsMenuItem = new JMenuItem();
                mClearResultsMenuItem.setName("ClearResultsMenuItem");
                mClearResultsMenuItem.setText("Clear Results");
                mClearResultsMenuItem.setBackground(SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return mClearResultsMenuItem;
    }

    /**
     * Return the ClearSQLMenuItem property value.
     * 
     * @return JMenuItem
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    JMenuItem getClearSQLMenuItem() {
        if (mClearSQLMenuItem == null) {
            try {
                mClearSQLMenuItem = new JMenuItem();
                mClearSQLMenuItem.setName("ClearSQLMenuItem");
                mClearSQLMenuItem.setText("Clear SQL");
                mClearSQLMenuItem.setBackground(SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return mClearSQLMenuItem;
    }

    /**
     * Return the CopySQLMenuItem property value.
     * 
     * @return JMenuItem
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    JMenuItem getCopySQLMenuItem() {
        if (mCopySQLMenuItem == null) {
            try {
                mCopySQLMenuItem = new JMenuItem();
                mCopySQLMenuItem.setName("CopySQLMenuItem");
                mCopySQLMenuItem.setText("Copy");
                mCopySQLMenuItem.setBackground(SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return mCopySQLMenuItem;
    }

    /**
     * Return the CutSQLMenuItem property value.
     * 
     * @return JMenuItem
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    JMenuItem getCutSQLMenuItem() {
        if (mCutSQLMenuItem == null) {
            try {
                mCutSQLMenuItem = new JMenuItem();
                mCutSQLMenuItem.setName("CutSQLMenuItem");
                mCutSQLMenuItem.setText("Cut");
                mCutSQLMenuItem.setBackground(SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return mCutSQLMenuItem;
    }

    /**
     * Return the DeleteSQLMenuItem property value.
     * 
     * @return JMenuItem
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    JMenuItem getDeleteSQLMenuItem() {
        if (mDeleteSQLMenuItem == null) {
            try {
                mDeleteSQLMenuItem = new JMenuItem();
                mDeleteSQLMenuItem.setName("DeleteSQLMenuItem");
                mDeleteSQLMenuItem.setText("Delete SQL");
                mDeleteSQLMenuItem.setBackground(SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return mDeleteSQLMenuItem;
    }

    /**
     * Return the DescriptorMenu property value.
     * 
     * @return JPopupMenu
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    JPopupMenu getDescriptorMenu() {
        if (mDescriptorMenu == null) {
            try {
                mDescriptorMenu = new JPopupMenu();
                mDescriptorMenu.setName("DescriptorMenu");
                mDescriptorMenu.add(getResetDescriptorsMenuItem());
                mDescriptorMenu.add(getClearDescriptorsMenuItem());
                mDescriptorMenu.add(getFullNamesMenuItem());
                mDescriptorMenu.add(getInspectDescriptorMenuItem());
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return mDescriptorMenu;
    }

    /**
     * Return the ExecuteQueryMenuItem property value.
     * 
     * @return JMenuItem
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    JMenuItem getExecuteQueryMenuItem() {
        if (mExecuteQueryMenuItem == null) {
            try {
                mExecuteQueryMenuItem = new JMenuItem();
                mExecuteQueryMenuItem.setName("ExecuteQueryMenuItem");
                mExecuteQueryMenuItem.setText("Execute Query");
                mExecuteQueryMenuItem.setBackground(SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return mExecuteQueryMenuItem;
    }

    /**
     * Return the ExpressionPanel property value.
     * 
     * @return ExpressionPanel
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    org.eclipse.persistence.tools.beans.ExpressionPanel getExpressionPanel() {
        if (mExpressionPanel == null) {
            try {
                mExpressionPanel = new org.eclipse.persistence.tools.beans.ExpressionPanel();
                mExpressionPanel.setName("ExpressionPanel");
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return mExpressionPanel;
    }

    /**
     * Return the FullNamesMenuItem property value.
     * 
     * @return JCheckBoxMenuItem
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    JCheckBoxMenuItem getFullNamesMenuItem() {
        if (mFullNamesMenuItem == null) {
            try {
                mFullNamesMenuItem = new JCheckBoxMenuItem();
                mFullNamesMenuItem.setName("FullNamesMenuItem");
                mFullNamesMenuItem.setText("Display Package Names");
                mFullNamesMenuItem.setBackground(SystemColor.control);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return mFullNamesMenuItem;
    }

    /**
     * Return the InsertSQLMenuItem property value.
     * 
     * @return JMenuItem
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    JMenuItem getInsertSQLMenuItem() {
        if (mInsertSQLMenuItem == null) {
            try {
                mInsertSQLMenuItem = new JMenuItem();
                mInsertSQLMenuItem.setName("InsertSQLMenuItem");
                mInsertSQLMenuItem.setText("Insert SQL");
                mInsertSQLMenuItem.setBackground(SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return mInsertSQLMenuItem;
    }

    /**
     * Return the InspectCacheButton property value.
     * 
     * @return JButton
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    JButton getInspectCacheButton() {
        if (mInspectCacheButton == null) {
            try {
                mInspectCacheButton = new JButton();
                mInspectCacheButton.setName("InspectCacheButton");
                mInspectCacheButton.setText("Inspect");
                mInspectCacheButton.setBackground(SystemColor.control);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return mInspectCacheButton;
    }

    /**
     * Return the InspectCacheMenuItem property value.
     * 
     * @return JMenuItem
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    JMenuItem getInspectCacheMenuItem() {
        if (mInspectCacheMenuItem == null) {
            try {
                mInspectCacheMenuItem = new JMenuItem();
                mInspectCacheMenuItem.setName("InspectCacheMenuItem");
                mInspectCacheMenuItem.setText("Inspect Cache");
                mInspectCacheMenuItem.setBackground(SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return mInspectCacheMenuItem;
    }

    /**
     * Return the InspectDescriptorMenuItem property value.
     * 
     * @return JMenuItem
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    JMenuItem getInspectDescriptorMenuItem() {
        if (mInspectDescriptorMenuItem == null) {
            try {
                mInspectDescriptorMenuItem = new JMenuItem();
                mInspectDescriptorMenuItem.setName("InspectDescriptorMenuItem");
                mInspectDescriptorMenuItem.setText("Inspect Descriptor");
                mInspectDescriptorMenuItem.setBackground(SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return mInspectDescriptorMenuItem;
    }

    /**
     * Return the InspectResultMenuItem property value.
     * 
     * @return JMenuItem
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    JMenuItem getInspectResultMenuItem() {
        if (mInspectResultMenuItem == null) {
            try {
                mInspectResultMenuItem = new JMenuItem();
                mInspectResultMenuItem.setName("InspectResultMenuItem");
                mInspectResultMenuItem.setText("Inspect Result");
                mInspectResultMenuItem.setBackground(SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return mInspectResultMenuItem;
    }

    /**
     * Return the InspectSessionMenuItem1 property value.
     * 
     * @return JMenuItem
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    JMenuItem getInspectSessionMenuItem1() {
        if (mInspectSessionMenuItem1 == null) {
            try {
                mInspectSessionMenuItem1 = new JMenuItem();
                mInspectSessionMenuItem1.setName("InspectSessionMenuItem1");
                mInspectSessionMenuItem1.setText("Inspect Session");
                mInspectSessionMenuItem1.setBackground(SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return mInspectSessionMenuItem1;
    }

    /**
     * Return the JPopupMenu1 property value.
     * 
     * @return JPopupMenu
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    JPopupMenu getJPopupMenu1() {
        if (mJPopupMenu1 == null) {
            try {
                mJPopupMenu1 = new JPopupMenu();
                mJPopupMenu1.setName("JPopupMenu1");
                mJPopupMenu1.add(getClearResultsMenuItem());
                mJPopupMenu1.add(getInspectResultMenuItem());
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return mJPopupMenu1;
    }

    /**
     * Return the JSeparator4 property value.
     * 
     * @return JSeparator
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    JSeparator getJSeparator4() {
        if (mJSeparator4 == null) {
            try {
                mJSeparator4 = new JSeparator();
                mJSeparator4.setName("JSeparator4");
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return mJSeparator4;
    }

    /**
     * Return the JSeparator5 property value.
     * 
     * @return JSeparator
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    JSeparator getJSeparator5() {
        if (mJSeparator5 == null) {
            try {
                mJSeparator5 = new JSeparator();
                mJSeparator5.setName("JSeparator5");
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return mJSeparator5;
    }

    /**
     * Return the JSeparator6 property value.
     * 
     * @return JSeparator
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    JSeparator getJSeparator6() {
        if (mJSeparator6 == null) {
            try {
                mJSeparator6 = new JSeparator();
                mJSeparator6.setName("JSeparator6");
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return mJSeparator6;
    }

    /**
     * Return the JSeparator7 property value.
     * 
     * @return JSeparator
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    JSeparator getJSeparator7() {
        if (mJSeparator7 == null) {
            try {
                mJSeparator7 = new JSeparator();
                mJSeparator7.setName("JSeparator7");
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return mJSeparator7;
    }

    /**
     * Return the LoadProjectButton property value.
     * 
     * @return JButton
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    JButton getLoadProjectButton() {
        if (mLoadProjectButton == null) {
            try {
                mLoadProjectButton = new JButton();
                mLoadProjectButton.setName("LoadProjectButton");
                mLoadProjectButton.setText("Load Project...");
                mLoadProjectButton.setBackground(SystemColor.control);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return mLoadProjectButton;
    }

    /**
     * Return the LogBook property value.
     * 
     * @return JTabbedPane
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    JTabbedPane getLogBook() {
        if (mLogBook == null) {
            try {
                mLogBook = new JTabbedPane();
                mLogBook.setName("LogBook");
                mLogBook.insertTab("Log", null, getLogPage(), null, 0);
                mLogBook.setBackgroundAt(0, SystemColor.control);
                mLogBook
                        .insertTab("ResultPage", null, getResultPage(), null, 1);
                mLogBook.setBackgroundAt(1, SystemColor.control);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return mLogBook;
    }

    private JComboBox getLogLevelChoice() {
        if (logLevelChoice == null) {
            try {
                logLevelChoice = new JComboBox();
                logLevelChoice.setName("LogLevelChoice");
                logLevelChoice.addItem("All");
                logLevelChoice.addItem("Finest");
                logLevelChoice.addItem("Finer");
                logLevelChoice.addItem("Fine");
                logLevelChoice.addItem("Config");
                logLevelChoice.addItem("Info");
                logLevelChoice.addItem("Warning");
                logLevelChoice.addItem("Sever");
                logLevelChoice.addItem("None");
                logLevelChoice.setSelectedItem("None");
            } catch (Throwable exception) {
                handleException(exception);
            }
        }
        return logLevelChoice;
    }

    /**
     * Return the LoginButton property value.
     * 
     * @return JButton
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    JButton getLoginButton() {
        if (mLoginButton == null) {
            try {
                mLoginButton = new JButton();
                mLoginButton.setName("LoginButton");
                mLoginButton.setText("Login");
                mLoginButton.setBackground(SystemColor.control);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return mLoginButton;
    }

    public LoginEditorPanel getLoginEditor() {
        return getLoginEditorPanel();
    }

    /**
     * Return the LoginEditorPanel property value.
     * 
     * @return LoginEditorPanel
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    LoginEditorPanel getLoginEditorPanel() {
        if (mLoginEditorPanel == null) {
            try {
                mLoginEditorPanel = new org.eclipse.persistence.tools.sessionconsole.LoginEditorPanel();
                mLoginEditorPanel.setName("LoginEditorPanel");
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return mLoginEditorPanel;
    }

    /**
     * Return the LoginMenu property value.
     * 
     * @return JPopupMenu
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    JPopupMenu getLoginMenu() {
        if (mLoginMenu == null) {
            try {
                mLoginMenu = new JPopupMenu();
                mLoginMenu.setName("LoginMenu");
                mLoginMenu.add(getLoginMenuItem());
                mLoginMenu.add(getLogoutMenuItem());
                mLoginMenu.add(getInspectSessionMenuItem1());
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return mLoginMenu;
    }

    /**
     * Return the LoginMenuItem property value.
     * 
     * @return JMenuItem
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    JMenuItem getLoginMenuItem() {
        if (mLoginMenuItem == null) {
            try {
                mLoginMenuItem = new JMenuItem();
                mLoginMenuItem.setName("LoginMenuItem");
                mLoginMenuItem.setText("Login");
                mLoginMenuItem.setBackground(SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return mLoginMenuItem;
    }

    /**
     * Return the LoginPage property value.
     * 
     * @return JPanel
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    JPanel getLoginPage() {
        if (mLoginPage == null) {
            try {
                mLoginPage = new JPanel();
                mLoginPage.setName("LoginPage");
                mLoginPage.setLayout(new GridBagLayout());
                mLoginPage.setBackground(SystemColor.control);

                GridBagConstraints constraintsLoginEditorPanel = new GridBagConstraints();
                constraintsLoginEditorPanel.gridx = 0;
                constraintsLoginEditorPanel.gridy = 0;
                constraintsLoginEditorPanel.gridwidth = 6;
                constraintsLoginEditorPanel.fill = GridBagConstraints.BOTH;
                constraintsLoginEditorPanel.weightx = 1.0;
                constraintsLoginEditorPanel.weighty = 1.0;
                constraintsLoginEditorPanel.insets = new Insets(2, 2, 2, 2);
                getLoginPage().add(getLoginEditorPanel(),
                        constraintsLoginEditorPanel);

                GridBagConstraints constraintsLoginButton = new GridBagConstraints();
                constraintsLoginButton.gridx = 0;
                constraintsLoginButton.gridy = 1;
                constraintsLoginButton.insets = new Insets(2, 2, 2, 2);
                getLoginPage().add(getLoginButton(), constraintsLoginButton);

                GridBagConstraints constraintsLogoutButton = new GridBagConstraints();
                constraintsLogoutButton.gridx = 1;
                constraintsLogoutButton.gridy = 1;
                constraintsLogoutButton.insets = new Insets(2, 2, 2, 2);
                getLoginPage().add(getLogoutButton(), constraintsLogoutButton);

                GridBagConstraints constraintsLoadProjectButton = new GridBagConstraints();
                constraintsLoadProjectButton.gridx = 2;
                constraintsLoadProjectButton.gridy = 1;
                constraintsLoadProjectButton.insets = new Insets(2, 2, 2, 2);
                getLoginPage().add(getLoadProjectButton(),
                        constraintsLoadProjectButton);

                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return mLoginPage;
    }

    /**
     * Return the LogMenu property value.
     * 
     * @return JPopupMenu
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    JPopupMenu getLogMenu() {
        if (mLogMenu == null) {
            try {
                mLogMenu = new JPopupMenu();
                mLogMenu.setName("LogMenu");
                mLogMenu.add(getCutMenuItem());
                mLogMenu.add(getCopyMenuItem());
                mLogMenu.add(getPasteMenuItem());
                mLogMenu.add(getJSeparator7());
                mLogMenu.add(getClearLogMenuItem());
                mLogMenu.add(getJSeparator7());
                mLogMenu.add(getLogProfileMenuItem1());
                mLogMenu.add(getLogProfileMenuItem());
                mLogMenu.add(getBrowseProfileMenuItem());
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return mLogMenu;
    }

    /**
     * Return the LogoutButton property value.
     * 
     * @return JButton
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    JButton getLogoutButton() {
        if (mLogoutButton == null) {
            try {
                mLogoutButton = new JButton();
                mLogoutButton.setName("LogoutButton");
                mLogoutButton.setText("Logout");
                mLogoutButton.setBackground(SystemColor.control);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return mLogoutButton;
    }

    /**
     * Return the LogoutMenuItem property value.
     * 
     * @return JMenuItem
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    JMenuItem getLogoutMenuItem() {
        if (mLogoutMenuItem == null) {
            try {
                mLogoutMenuItem = new JMenuItem();
                mLogoutMenuItem.setName("LogoutMenuItem");
                mLogoutMenuItem.setText("Logout");
                mLogoutMenuItem.setBackground(SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return mLogoutMenuItem;
    }

    /**
     * Return the LogPage property value.
     * 
     * @return JPanel
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    JPanel getLogPage() {
        if (mLogPage == null) {
            try {
                mLogPage = new JPanel();
                mLogPage.setName("LogPage");
                mLogPage.setLayout(new GridBagLayout());
                mLogPage.setBackground(SystemColor.control);

                GridBagConstraints constraintsLogScrollPane = new GridBagConstraints();
                constraintsLogScrollPane.gridx = 0;
                constraintsLogScrollPane.gridy = 0;
                constraintsLogScrollPane.gridwidth = 13;
                constraintsLogScrollPane.fill = GridBagConstraints.BOTH;
                constraintsLogScrollPane.weightx = 1.0;
                constraintsLogScrollPane.weighty = 1.0;
                constraintsLogScrollPane.insets = new Insets(2, 2, 2, 2);
                getLogPage().add(getLogScrollPane(), constraintsLogScrollPane);

                GridBagConstraints constraintsClearLogButton = new GridBagConstraints();
                constraintsClearLogButton.gridx = 12;
                constraintsClearLogButton.gridy = 1;
                constraintsClearLogButton.anchor = GridBagConstraints.EAST;
                constraintsClearLogButton.insets = new Insets(2, 2, 2, 2);
                getLogPage()
                        .add(getClearLogButton(), constraintsClearLogButton);

                GridBagConstraints constraintsLogLabel = new GridBagConstraints();
                constraintsLogLabel.gridx = 0;
                constraintsLogLabel.gridy = 1;
                constraintsLogLabel.anchor = GridBagConstraints.WEST;
                constraintsLogLabel.insets = new Insets(2, 2, 2, 2);
                getLogPage().add(new JLabel("Log Level:"), constraintsLogLabel);

                GridBagConstraints constraintsLogCheckbox = new GridBagConstraints();
                constraintsLogCheckbox.gridx = 1;
                constraintsLogCheckbox.gridy = 1;
                constraintsLogCheckbox.fill = GridBagConstraints.HORIZONTAL;
                constraintsLogCheckbox.anchor = GridBagConstraints.WEST;
                constraintsLogCheckbox.insets = new Insets(2, 2, 2, 2);
                getLogPage().add(getLogLevelChoice(), constraintsLogCheckbox);

                GridBagConstraints constraintsProfileCheckbox = new GridBagConstraints();
                constraintsProfileCheckbox.gridx = 0;
                constraintsProfileCheckbox.gridy = 2;
                constraintsProfileCheckbox.anchor = GridBagConstraints.WEST;
                constraintsProfileCheckbox.insets = new Insets(2, 2, 2, 2);
                getLogPage().add(getProfileCheckbox(),
                        constraintsProfileCheckbox);

                GridBagConstraints constraintsLogProfileCheckbox = new GridBagConstraints();
                constraintsLogProfileCheckbox.gridx = 1;
                constraintsLogProfileCheckbox.gridy = 2;
                constraintsLogProfileCheckbox.anchor = GridBagConstraints.WEST;
                constraintsLogProfileCheckbox.insets = new Insets(2, 2, 2, 2);
                getLogPage().add(getLogProfileCheckbox(),
                        constraintsLogProfileCheckbox);

                GridBagConstraints constraintsBrowseProfileButton = new GridBagConstraints();
                constraintsBrowseProfileButton.gridx = 12;
                constraintsBrowseProfileButton.gridy = 2;
                constraintsBrowseProfileButton.anchor = GridBagConstraints.EAST;
                constraintsBrowseProfileButton.insets = new Insets(2, 2, 2, 2);
                getLogPage().add(getBrowseProfileButton(),
                        constraintsBrowseProfileButton);

            } catch (Throwable exception) {
                handleException(exception);
            }
        }
        return mLogPage;
    }

    private JCheckBox getLogProfileCheckbox() {
        if (mLogProfileCheckbox == null) {
            try {
                mLogProfileCheckbox = new JCheckBox();
                mLogProfileCheckbox.setName("LogProfileCheckbox");
                mLogProfileCheckbox.setText("Log Profile");
                mLogProfileCheckbox.setBackground(SystemColor.control);
            } catch (Throwable exception) {
                handleException(exception);
            }
        }
        return mLogProfileCheckbox;
    }

    /**
     * Return the LogProfileMenuItem property value.
     * 
     * @return JCheckBoxMenuItem
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    JCheckBoxMenuItem getLogProfileMenuItem() {
        if (mLogProfileMenuItem == null) {
            try {
                mLogProfileMenuItem = new JCheckBoxMenuItem();
                mLogProfileMenuItem.setName("LogProfileMenuItem");
                mLogProfileMenuItem.setText("Log Profile");
                mLogProfileMenuItem.setBackground(SystemColor.menu);
                mLogProfileMenuItem.setActionCommand("LogSQLMenuItem");
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return mLogProfileMenuItem;
    }

    /**
     * Return the LogProfileMenuItem1 property value.
     * 
     * @return JCheckBoxMenuItem
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    JCheckBoxMenuItem getLogProfileMenuItem1() {
        if (mLogProfileMenuItem1 == null) {
            try {
                mLogProfileMenuItem1 = new JCheckBoxMenuItem();
                mLogProfileMenuItem1.setName("LogProfileMenuItem1");
                mLogProfileMenuItem1.setText("Profile");
                mLogProfileMenuItem1.setBackground(SystemColor.menu);
                mLogProfileMenuItem1.setActionCommand("ProfileMenuItem");
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return mLogProfileMenuItem1;
    }

    /**
     * Return the LogScrollPane property value.
     * 
     * @return JScrollPane
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    JScrollPane getLogScrollPane() {
        if (mLogScrollPane == null) {
            try {
                mLogScrollPane = new JScrollPane();
                mLogScrollPane.setName("LogScrollPane");
                mLogScrollPane.setAutoscrolls(true);
                getLogScrollPane().setViewportView(getLogText());
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return mLogScrollPane;
    }

    /**
     * Return the LogSplitter property value.
     * 
     * @return JSplitPane
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    JSplitPane getLogSplitter() {
        if (mLogSplitter == null) {
            try {
                mLogSplitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
                mLogSplitter.setName("LogSplitter");
                mLogSplitter.setAutoscrolls(true);
                mLogSplitter.setDividerSize(8);
                mLogSplitter.setMaximumSize(new Dimension(0, 0));
                mLogSplitter.setDividerLocation(300);
                mLogSplitter.setOneTouchExpandable(true);
                mLogSplitter.setMinimumSize(new Dimension(0, 0));
                getLogSplitter().add(getLogBook(), "bottom");
                getLogSplitter().add(getTopPanel(), "top");
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return mLogSplitter;
    }

    /**
     * Return the LogText property value.
     * 
     * @return JTextArea
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    JTextArea getLogText() {
        if (mLogText == null) {
            try {
                mLogText = new JTextArea();
                mLogText.setName("LogText");
                mLogText.setAutoscrolls(true);
                mLogText.setBounds(0, 0, 10, 10);
                mLogText.setEditable(true);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return mLogText;
    }

    /**
     * Return the PasteSQLMenuItem property value.
     * 
     * @return JMenuItem
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    JMenuItem getPasteSQLMenuItem() {
        if (mPasteSQLMenuItem == null) {
            try {
                mPasteSQLMenuItem = new JMenuItem();
                mPasteSQLMenuItem.setName("PasteSQLMenuItem");
                mPasteSQLMenuItem.setText("Paste");
                mPasteSQLMenuItem.setBackground(SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return mPasteSQLMenuItem;
    }

    private JCheckBox getProfileCheckbox() {
        if (mProfileCheckbox == null) {
            try {
                mProfileCheckbox = new JCheckBox();
                mProfileCheckbox.setName("ProfileCheckbox");
                mProfileCheckbox.setText("Profile");
                mProfileCheckbox.setBackground(SystemColor.control);
            } catch (Throwable exception) {
                handleException(exception);
            }
        }
        return mProfileCheckbox;
    }

    private JButton getQueryButton() {
        if (mQueryButton == null) {
            try {
                mQueryButton = new JButton();
                mQueryButton.setName("QueryButton");
                mQueryButton.setText("Execute Query");
                mQueryButton.setBackground(SystemColor.control);
            } catch (Throwable exception) {
                handleException(exception);
            }
        }
        return mQueryButton;
    }

    /**
     * Return the QueryMenu property value.
     * 
     * @return JPopupMenu
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    JPopupMenu getQueryMenu() {
        if (mQueryMenu == null) {
            try {
                mQueryMenu = new JPopupMenu();
                mQueryMenu.setName("QueryMenu");
                mQueryMenu.add(getExecuteQueryMenuItem());
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return mQueryMenu;
    }

    /**
     * Return the QueryPanel property value.
     * 
     * @return JPanel
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    JPanel getQueryPage() {
        if (mQueryPage == null) {
            try {
                mQueryPage = new JPanel();
                mQueryPage.setName("QueryPage");
                mQueryPage.setLayout(new GridBagLayout());
                mQueryPage.setBackground(SystemColor.control);

                GridBagConstraints constraintsQueryButton = new GridBagConstraints();
                constraintsQueryButton.gridx = 0;
                constraintsQueryButton.gridy = 1;
                constraintsQueryButton.anchor = GridBagConstraints.WEST;
                constraintsQueryButton.insets = new Insets(2, 2, 2, 2);
                getQueryPage().add(getQueryButton(), constraintsQueryButton);

                GridBagConstraints constraintsExpressionPanel = new GridBagConstraints();
                constraintsExpressionPanel.gridx = 0;
                constraintsExpressionPanel.gridy = 0;
                constraintsExpressionPanel.fill = GridBagConstraints.BOTH;
                constraintsExpressionPanel.weightx = 1.0;
                constraintsExpressionPanel.weighty = 1.0;
                getQueryPage().add(getExpressionPanel(),
                        constraintsExpressionPanel);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return mQueryPage;
    }

    /**
     * Return the ResetDescriptorsMenuItem property value.
     * 
     * @return JMenuItem
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    JMenuItem getResetDescriptorsMenuItem() {
        if (mResetDescriptorsMenuItem == null) {
            try {
                mResetDescriptorsMenuItem = new JMenuItem();
                mResetDescriptorsMenuItem.setName("ResetDescriptorsMenuItem");
                mResetDescriptorsMenuItem.setText("Reset Descriptors");
                mResetDescriptorsMenuItem.setBackground(SystemColor.menu);
                mResetDescriptorsMenuItem.setForeground(Color.black);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return mResetDescriptorsMenuItem;
    }

    /**
     * Return the ResultPage property value.
     * 
     * @return JPanel
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    JPanel getResultPage() {
        if (mResultPage == null) {
            try {
                mResultPage = new JPanel();
                mResultPage.setName("Query Results");
                mResultPage.setLayout(new GridBagLayout());
                mResultPage.setBackground(SystemColor.control);

                GridBagConstraints constraintsResultsScrollPane = new GridBagConstraints();
                constraintsResultsScrollPane.gridx = 0;
                constraintsResultsScrollPane.gridy = 0;
                constraintsResultsScrollPane.fill = GridBagConstraints.BOTH;
                constraintsResultsScrollPane.weightx = 1.0;
                constraintsResultsScrollPane.weighty = 1.0;
                constraintsResultsScrollPane.insets = new Insets(2, 2, 2, 2);
                getResultPage().add(getResultsScrollPane(),
                        constraintsResultsScrollPane);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return mResultPage;
    }

    public Vector getResults() {
        return results;
    }

    /**
     * Return the ResultsScrollPane property value.
     * 
     * @return JScrollPane
     */
    private JScrollPane getResultsScrollPane() {
        if (mResultsScrollPane == null) {
            try {
                mResultsScrollPane = new JScrollPane();
                mResultsScrollPane.setName("ResultsScrollPane");
                mResultsScrollPane
                        .setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                mResultsScrollPane
                        .setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
                // mResultsScrollPane.setMaximumSize(new Dimension(0, 0));
                // mResultsScrollPane.setPreferredSize(new Dimension(0, 0));
                // mResultsScrollPane.setMinimumSize(new Dimension(0, 0));
                getResultsScrollPane().setViewportView(getResultsTable());
            } catch (Throwable exception) {
                handleException(exception);
            }
        }
        return mResultsScrollPane;
    }

    /**
     * Return the ResultsTable property value.
     * 
     * @return JTable
     */
    private JTable getResultsTable() {
        if (mResultsTable == null) {
            try {
                mResultsTable = new JTable();
                mResultsTable.setName("ResultsTable");
                getResultsScrollPane().setColumnHeaderView(
                        mResultsTable.getTableHeader());
                mResultsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                // mResultsTable.setPreferredSize(new Dimension(700, 400));
                // mResultsTable.setBounds(0, 0, 200, 200);
                // mResultsTable.setPreferredScrollableViewportSize(new
                // Dimension(700, 400));
            } catch (Throwable exception) {
                handleException(exception);
            }
        }
        return mResultsTable;
    }

    /**
     * Return the SelectButton property value.
     * 
     * @return JButton
     */
    private JButton getSelectButton() {
        if (mSelectButton == null) {
            try {
                mSelectButton = new JButton();
                mSelectButton.setName("SelectButton");
                mSelectButton.setText("Select");
                mSelectButton.setBackground(SystemColor.control);
            } catch (Throwable exception) {
                handleException(exception);
            }
        }
        return mSelectButton;
    }

    /**
     * Return the SelectSQLMenuItem property value.
     * 
     * @return JMenuItem
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    JMenuItem getSelectSQLMenuItem() {
        if (mSelectSQLMenuItem == null) {
            try {
                mSelectSQLMenuItem = new JMenuItem();
                mSelectSQLMenuItem.setName("SelectSQLMenuItem");
                mSelectSQLMenuItem.setText("Select SQL");
                mSelectSQLMenuItem.setBackground(SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return mSelectSQLMenuItem;
    }

    /**
     * Gets the session property (org.eclipse.persistence.sessions.Session)
     * value.
     * 
     * @return The session property value.
     * @see #setSession
     */
    public Session getSession() {
        return fieldSession;
    }

    /**
     * Return the SQLExecuteButton property value.
     * 
     * @return JButton
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    JButton getSQLExecuteButton() {
        if (mSQLExecuteButton == null) {
            try {
                mSQLExecuteButton = new JButton();
                mSQLExecuteButton.setName("SQLExecuteButton");
                mSQLExecuteButton.setText("Update");
                mSQLExecuteButton.setBackground(SystemColor.control);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return mSQLExecuteButton;
    }

    private JButton getJPQLExecuteButton() {
        if (jpqlExecuteButton == null) {
            try {
                jpqlExecuteButton = new JButton();
                jpqlExecuteButton.setName("JPQLExecuteButton");
                jpqlExecuteButton.setText("Execute");
                jpqlExecuteButton.setBackground(SystemColor.control);
            } catch (Throwable exception) {
                handleException(exception);
            }
        }
        return jpqlExecuteButton;
    }

    private JButton getJavaExecuteButton() {
        if (javaExecuteButton == null) {
            try {
                javaExecuteButton = new JButton();
                javaExecuteButton.setName("JavaExecuteButton");
                javaExecuteButton.setText("Execute");
                javaExecuteButton.setBackground(SystemColor.control);
            } catch (Throwable exception) {
                handleException(exception);
            }
        }
        return javaExecuteButton;
    }

    /**
     * Return the SQLMenu property value.
     * 
     * @return JPopupMenu
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    JPopupMenu getSQLMenu() {
        if (mSQLMenu == null) {
            try {
                mSQLMenu = new JPopupMenu();
                mSQLMenu.setName("SQLMenu");
                mSQLMenu.add(getSQLUpdateMenuItem());
                mSQLMenu.add(getSQLSelectMenuItem());
                mSQLMenu.add(getJSeparator4());
                mSQLMenu.add(getClearSQLMenuItem());
                mSQLMenu.add(getJSeparator6());
                mSQLMenu.add(getCutSQLMenuItem());
                mSQLMenu.add(getCopySQLMenuItem());
                mSQLMenu.add(getPasteSQLMenuItem());
                mSQLMenu.add(getJSeparator5());
                mSQLMenu.add(getSQLTemplateMenu());
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return mSQLMenu;
    }

    /**
     * Return the SQLScrollPane property value.
     * 
     * @return JScrollPane
     */
    private JScrollPane getSQLScrollPane() {
        if (mSQLScrollPane == null) {
            try {
                mSQLScrollPane = new JScrollPane();
                mSQLScrollPane.setName("SQLScrollPane");
                getSQLScrollPane().setViewportView(getSQLText());
            } catch (Throwable exception) {
                handleException(exception);
            }
        }
        return mSQLScrollPane;
    }

    private JScrollPane getJPQLScrollPane() {
        JScrollPane pane = new JScrollPane();
        pane.setName("JPQLScrollPane");
        pane.setViewportView(getJPQLText());
        return pane;
    }

    private JScrollPane getJavaScrollPane() {
        if (javaScrollPane == null) {
            try {
                javaScrollPane = new JScrollPane();
                javaScrollPane.setName("JavaScrollPane");
                getJavaScrollPane().setViewportView(getJavaText());
            } catch (Throwable exception) {
                handleException(exception);
            }
        }
        return javaScrollPane;
    }

    /**
     * Return the SQLSelectMenuItem property value.
     * 
     * @return JMenuItem
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    JMenuItem getSQLSelectMenuItem() {
        if (mSQLSelectMenuItem == null) {
            try {
                mSQLSelectMenuItem = new JMenuItem();
                mSQLSelectMenuItem.setName("SQLSelectMenuItem");
                mSQLSelectMenuItem.setText("Execute SQL Select");
                mSQLSelectMenuItem.setBackground(SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return mSQLSelectMenuItem;
    }

    /**
     * Return the SQLTemplateMenu property value.
     * 
     * @return JMenu
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    JMenu getSQLTemplateMenu() {
        if (mSQLTemplateMenu == null) {
            try {
                mSQLTemplateMenu = new JMenu();
                mSQLTemplateMenu.setName("SQLTemplateMenu");
                mSQLTemplateMenu.setText("SQL");
                mSQLTemplateMenu.setBackground(SystemColor.menu);
                mSQLTemplateMenu.add(getInsertSQLMenuItem());
                mSQLTemplateMenu.add(getUpdateSQLMenuItem());
                mSQLTemplateMenu.add(getDeleteSQLMenuItem());
                mSQLTemplateMenu.add(getSelectSQLMenuItem());
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return mSQLTemplateMenu;
    }

    /**
     * Return the SQLText property value.
     * 
     * @return JTextPane
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    JTextPane getSQLText() {
        if (mSQLText == null) {
            try {
                mSQLText = new JTextPane();
                mSQLText.setName("SQLText");
                mSQLText
                        .setBorder(new javax.swing.plaf.basic.BasicBorders.MarginBorder());
                mSQLText.setBounds(0, 0, 467, 156);
                mSQLText.setMaximumSize(new Dimension(0, 0));
                mSQLText.setMinimumSize(new Dimension(1, 1));
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return mSQLText;
    }

    private JTextPane getJPQLText() {
        if (jpqlText == null) {
            jpqlText = new JTextPane();
            jpqlText.setName("JPQLText");
            jpqlText.setBorder(new javax.swing.plaf.basic.BasicBorders.MarginBorder());
            jpqlText.setBounds(0, 0, 467, 156);
            jpqlText.setMaximumSize(new Dimension(0, 0));
            jpqlText.setMinimumSize(new Dimension(1, 1));
        }
        return jpqlText;
    }

    private JTextPane getJavaText() {
        if (javaText == null) {
            try {
                javaText = new JTextPane();
                javaText.setName("JavaText");
                javaText
                        .setBorder(new javax.swing.plaf.basic.BasicBorders.MarginBorder());
                javaText.setBounds(0, 0, 467, 156);
                javaText.setMaximumSize(new Dimension(0, 0));
                javaText.setMinimumSize(new Dimension(1, 1));
            } catch (Throwable exception) {
                handleException(exception);
            }
        }
        return javaText;
    }

    /**
     * Return the SQLUpdateMenuItem property value.
     * 
     * @return JMenuItem
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    JMenuItem getSQLUpdateMenuItem() {
        if (mSQLUpdateMenuItem == null) {
            try {
                mSQLUpdateMenuItem = new JMenuItem();
                mSQLUpdateMenuItem.setName("SQLUpdateMenuItem");
                mSQLUpdateMenuItem.setText("Execute SQL Update");
                mSQLUpdateMenuItem.setBackground(SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return mSQLUpdateMenuItem;
    }

    /**
     * Return the TopPanel property value.
     * 
     * @return JPanel
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    JPanel getTopPanel() {
        if (mTopPanel == null) {
            try {
                mTopPanel = new JPanel();
                mTopPanel.setName("TopPanel");
                mTopPanel.setLayout(new GridBagLayout());
                mTopPanel.setBackground(SystemColor.control);

                GridBagConstraints constraintsTopSplitPane = new GridBagConstraints();
                constraintsTopSplitPane.gridx = 0;
                constraintsTopSplitPane.gridy = 0;
                constraintsTopSplitPane.fill = GridBagConstraints.BOTH;
                constraintsTopSplitPane.weightx = 1.0;
                constraintsTopSplitPane.weighty = 1.0;
                constraintsTopSplitPane.ipady = 300;
                getTopPanel().add(getTopSplitPane(), constraintsTopSplitPane);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return mTopPanel;
    }

    /**
     * Return the TopSplitPane property value.
     * 
     * @return JSplitPane
     */
    private JSplitPane getTopSplitPane() {
        if (mTopSplitPane == null) {
            try {
                mTopSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
                mTopSplitPane.setName("TopSplitPane");
                mTopSplitPane.setDividerSize(8);
                mTopSplitPane.setOneTouchExpandable(true);
                mTopSplitPane.setDividerLocation(150);
                getTopSplitPane().add(getClassScrollPane(), "left");
                getTopSplitPane().add(getWorkspaceBook(), "right");
            } catch (Throwable exception) {
                handleException(exception);
            }
        }
        return mTopSplitPane;
    }

    /**
     * Return the UpdateSQLMenuItem property value.
     * 
     * @return JMenuItem
     */
    private JMenuItem getUpdateSQLMenuItem() {
        if (mUpdateSQLMenuItem == null) {
            try {
                mUpdateSQLMenuItem = new JMenuItem();
                mUpdateSQLMenuItem.setName("UpdateSQLMenuItem");
                mUpdateSQLMenuItem.setText("Update SQL");
                mUpdateSQLMenuItem.setBackground(SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return mUpdateSQLMenuItem;
    }

    /**
     * Return the WorkspaceBook property value.
     * 
     * @return JTabbedPane
     */
    public JTabbedPane getWorkspaceBook() {
        if (mWorkspaceBook == null) {
            try {
                mWorkspaceBook = new JTabbedPane();
                mWorkspaceBook.setName("WorkspaceBook");
                mWorkspaceBook.setAutoscrolls(false);
                mWorkspaceBook.setBackground(SystemColor.control);
                mWorkspaceBook.setMaximumSize(new Dimension(0, 0));
                mWorkspaceBook.setMinimumSize(new Dimension(1, 1));
                mWorkspaceBook
                        .insertTab("Login", null, getLoginPage(), null, 0);
                mWorkspaceBook
                        .insertTab("Cache", null, getCachePage(), null, 1);
                mWorkspaceBook
                        .insertTab("Query", null, getQueryPage(), null, 2);
                mWorkspaceBook.insertTab("JPQL", null, getJPQLPage(), null, 3);
                mWorkspaceBook.insertTab("SQL", null, getSQLPage(), null, 4);
                mWorkspaceBook.insertTab("Java", null, getJavaPage(), null, 5);
            } catch (Throwable exception) {
                handleException(exception);
            }
        }
        return mWorkspaceBook;
    }

    /**
     * Called whenever the part throws an exception.
     */
    private void handleException(Throwable exception) {
        /* Uncomment the following lines to print uncaught exceptions to stdout */
        System.out.println("--------- UNCAUGHT EXCEPTION ---------");
        exception.printStackTrace(System.out);
        MessageDialog.displayException(exception, this);
    }

    /**
     * Initializes connections
     */
    private void initConnections() throws Exception {
        getClassList().addListSelectionListener(this);
        getLoginButton().addActionListener(this);
        getLogoutButton().addActionListener(this);
        getLoadProjectButton().addActionListener(this);
        getClearLogButton().addActionListener(this);
        getBrowseProfileButton().addActionListener(this);
        getLogProfileCheckbox().addItemListener(this);
        getProfileCheckbox().addItemListener(this);
        getLogLevelChoice().addItemListener(this);
        getSelectButton().addActionListener(this);
        getSQLExecuteButton().addActionListener(this);
        getJPQLExecuteButton().addActionListener(this);
        getJavaExecuteButton().addActionListener(this);
        getClearButton().addActionListener(this);
        getJavaClearButton().addActionListener(this);
        getClearCacheButton().addActionListener(this);
        getInspectCacheButton().addActionListener(this);
        getQueryButton().addActionListener(this);
        getClearCacheMenuItem().addActionListener(this);
        getInspectCacheMenuItem().addActionListener(this);
        getSQLText().addMouseListener(this);
        getCacheTable().addMouseListener(this);
        getClassList().addMouseListener(this);
        getResultsTable().addMouseListener(this);
        getClearResultsMenuItem().addActionListener(this);
        getInspectResultMenuItem().addActionListener(this);
        getInspectDescriptorMenuItem().addActionListener(this);
        getClearDescriptorsMenuItem().addActionListener(this);
        getResetDescriptorsMenuItem().addActionListener(this);
        getInsertSQLMenuItem().addActionListener(this);
        getUpdateSQLMenuItem().addActionListener(this);
        getDeleteSQLMenuItem().addActionListener(this);
        getSelectSQLMenuItem().addActionListener(this);
        getSQLUpdateMenuItem().addActionListener(this);
        getSQLSelectMenuItem().addActionListener(this);
        getClearSQLMenuItem().addActionListener(this);
        getCutSQLMenuItem().addActionListener(this);
        getCopySQLMenuItem().addActionListener(this);
        getPasteSQLMenuItem().addActionListener(this);
        getLoginMenuItem().addActionListener(this);
        getLogoutMenuItem().addActionListener(this);
        getInspectSessionMenuItem1().addActionListener(this);
        getQueryPage().addMouseListener(this);
        getLogText().addMouseListener(this);
        getLoginPage().addMouseListener(this);
        getExecuteQueryMenuItem().addActionListener(this);
        getClearLogMenuItem().addActionListener(this);
        getBrowseProfileMenuItem().addActionListener(this);
        getBrowseProfileMenuItem().addPropertyChangeListener(this);
        getFullNamesMenuItem().addActionListener(this);
        connPtoP1SetTarget();
        connPtoP2SetTarget();
        connPtoP3SetTarget();
        connPtoP4SetTarget();
    }

    /**
     * Initialize the class.
     */
    private/* WARNING: THIS METHOD WILL BE REGENERATED. */
    void initialize() {
        try {
            // user code begin {1}
            setSession(new Project(new DatabaseLogin()).createDatabaseSession());
            // user code end
            setName("SessionInspectorPanel");
            setLayout(new GridBagLayout());
            setBackground(SystemColor.control);
            setSize(849, 588);

            GridBagConstraints constraintsLogSplitter = new GridBagConstraints();
            constraintsLogSplitter.gridx = 0;
            constraintsLogSplitter.gridy = 0;
            constraintsLogSplitter.fill = GridBagConstraints.BOTH;
            constraintsLogSplitter.weightx = 1.0;
            constraintsLogSplitter.weighty = 1.0;
            add(getLogSplitter(), constraintsLogSplitter);
            initConnections();
        } catch (Throwable exception) {
            handleException(exception);
        }

        setup();
    }

    public void inspect(Object object) {
        getLogText().append(String.valueOf(object));
        getLogText().append(Helper.cr());
        getLogBook().setSelectedComponent(getLogPage());
    }

    public void inspectDescriptor() {
        ClassInfo classInfo = ((ClassInfo) getClassList().getSelectedValue());
        if (classInfo == null) {
            return;
        }

        inspect(classInfo.descriptor);
    }

    public void inspectFromCache() {
        int index = getCacheTable().getSelectedRow();
        if (index < 0) {
            return;
        }
        Object result = getCacheResults().elementAt(index);
        if (result == null) {
            return;
        }

        inspect(result);
    }

    public void inspectResult() {
        int index = getResultsTable().getSelectedRow();
        if (index < 0) {
            return;
        }
        Object result = getResults().elementAt(index);
        if (result == null) {
            return;
        }

        inspect(result);
    }

    public void inspectSession() {
        inspect(getSession());
    }

    /**
     * Method to handle events for the ItemListener interface.
     * 
     * @param e
     *                event.ItemEvent
     */
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == getLogProfileCheckbox()) {
            connEtoC14(e);
        }
        if (e.getSource() == getProfileCheckbox()) {
            connEtoC15(e);
        }
        if (e.getSource() == getLogLevelChoice()) {
            connEtoC16(e);
        }
    }

    public void loadLoginFromFile() {
        DatabaseLogin login = null;
        try {
            FileInputStream file = new FileInputStream("console.login");
            ObjectInputStream stream = new ObjectInputStream(file);
            DatabaseLogin fileLogin = (DatabaseLogin) stream.readObject();
            // Only copy over the connection properties.
            login = new DatabaseLogin();
            login.setDriverURLHeader(fileLogin.getDriverURLHeader());
            login.setDatabaseURL(fileLogin.getDatabaseURL());
            login.setDriverClassName(fileLogin.getDriverClassName());
            login.setPlatformClassName(fileLogin.getPlatformClassName());
            login.setUserName(fileLogin.getUserName());
            login.setEncryptedPassword(fileLogin.getPassword());
            stream.close();
            file.close();
        } catch (Throwable exception) {
            // Ignore file access errors if file access is not allowed then the
            // login will not be stored.
        }

        if (login == null) {
            return;
        }
        try {
            getLoginEditorPanel().setLogin(login);
        } catch (Exception exception) {
            // Incase the file is bad.
            getLoginEditorPanel().setLogin(new DatabaseLogin());
        }
    }

    public void loadProject() {
        JFileChooser dialog = new JFileChooser();
        dialog
                .setDialogTitle("Select Mapping Workbench project deployment XML file");
        javax.swing.filechooser.FileFilter filer = new javax.swing.filechooser.FileFilter() {
            public boolean accept(File file) {
                return (file.getName().indexOf(".xml") != -1)
                        || file.isDirectory();
            }

            public String getDescription() {
                return "XML Files (*.xml)";
            }
        };
        dialog.setFileFilter(filer);
        dialog.setCurrentDirectory(new File("."));
        int status = dialog.showOpenDialog(this);
        if (status == JFileChooser.CANCEL_OPTION) {
            return;
        }
        if (dialog.getSelectedFile() == null) {
            MessageDialog
                    .displayMessage("Invalid project file selected.", this);
            return;
        }

        showBusyCursor();
        try {
            String path = dialog.getSelectedFile().getPath();
            Project project = XMLProjectReader.read(path);
            if (getSession().isConnected()) {
                ((DatabaseSession) getSession()).addDescriptors(project);
            } else {
                ((AbstractSession) getSession()).setProject(project);
                getLoginEditorPanel().setLogin(
                        (DatabaseLogin) project.getDatasourceLogin());
            }
            resetDescriptors();
        } finally {
            showNormalCursor();
        }
    }

    public void loadSQLFromFile() {
        String sql = "";

        try {
            FileInputStream file = new FileInputStream("console.sql");
            byte[] data = new byte[file.available()];
            file.read(data);
            sql = new String(data);
            file.close();
        } catch (Exception exception) {
            // Ignore file access errors if file access is not allowed then the
            // login will not be stored.
        }

        getSQLText().setText(sql);
    }

    public void loggingChanged() {
        int logLevel = getLogLevelChoice().getSelectedIndex();
        if (logLevel != getSession().getLogLevel()) {
            getSession().setLogLevel(logLevel);
            getSession().getSessionLog().setShouldDisplayData(true);
            if (logLevel > SessionLog.FINEST) {
                getSession().getSessionLog().setShouldPrintConnection(false);
                getSession().getSessionLog().setShouldPrintDate(false);
                getSession().getSessionLog().setShouldPrintSession(false);
                getSession().getSessionLog().setShouldPrintThread(false);
            } else {
                getSession().getSessionLog().setShouldPrintConnection(true);
                getSession().getSessionLog().setShouldPrintDate(true);
                getSession().getSessionLog().setShouldPrintSession(true);
                getSession().getSessionLog().setShouldPrintThread(true);
            }
            // Also update all sessions in session manager.
            Iterator iterator = SessionManager.getManager().getSessions().values().iterator();
            while (iterator.hasNext()) {
                ((Session)iterator.next()).setSessionLog(getSession().getSessionLog());
            }
        }
    }

    public void login() {
        showBusyCursor();
        try {
            storeLoginToFile();

            updateLogin();

            ((DatabaseSession) getSession()).setLogin(getLoginEditorPanel()
                    .getLogin());
            ((DatabaseSession) getSession()).login();

            resetButtons();
        } finally {
            showNormalCursor();
        }
    }

    public void logout() {
        showBusyCursor();
        try {
            ((DatabaseSession) getSession()).logout();
            resetButtons();
            storeSQLToFile();
        } finally {
            showNormalCursor();
        }
    }

    public void logProfileChanged() {
        if ((getSession()).isInProfile()) {
            ((PerformanceProfiler) (getSession()).getProfiler())
                    .setShouldLogProfile(getLogProfileCheckbox().isSelected());
        }
    }

    /**
     * Method to handle events for the MouseListener interface.
     * 
     * @param e
     *                event.MouseEvent
     */
    public void mouseClicked(MouseEvent e) {
    }

    /**
     * Method to handle events for the MouseListener interface.
     * 
     * @param e
     *                event.MouseEvent
     */
    public void mouseEntered(MouseEvent e) {
    }

    /**
     * Method to handle events for the MouseListener interface.
     * 
     * @param e
     *                event.MouseEvent
     */
    public void mouseExited(MouseEvent e) {
    }

    /**
     * Method to handle events for the MouseListener interface.
     * 
     * @param e
     *                event.MouseEvent
     */
    public void mousePressed(MouseEvent e) {
    }

    /**
     * Method to handle events for the MouseListener interface.
     * 
     * @param e
     *                event.MouseEvent
     */
    public void mouseReleased(MouseEvent e) {
        if (e.getSource() == getSQLText()) {
            connEtoC18(e);
        }
        if (e.getSource() == getCacheTable()) {
            connEtoC19(e);
        }
        if (e.getSource() == getClassList()) {
            connEtoC21(e);
        }
        if (e.getSource() == getResultsTable()) {
            connEtoC20(e);
        }
        if (e.getSource() == getQueryPage()) {
            connEtoC40(e);
        }
        if (e.getSource() == getLogText()) {
            connEtoC41(e);
        }
        if (e.getSource() == getLoginPage()) {
            connEtoC42(e);
        }
    }

    public void pasteSQL() {
        getSQLText().paste();
    }

    public void profileChanged() {
        if ((getSession()).isInProfile()) {
            getSession().clearProfile();
        }
        if (getProfileCheckbox().isSelected()) {
            getSession().setProfiler(new PerformanceProfiler(getLogProfileCheckbox().isSelected()));
            getBrowseProfileButton().setEnabled(true);
            getLogProfileCheckbox().setEnabled(true);
        } else {
            getBrowseProfileButton().setEnabled(false);
            getLogProfileCheckbox().setEnabled(false);
        }
    }

    /**
     * Method to handle events for the PropertyChangeListener interface.
     * 
     * @param evt
     *                java.beans.PropertyChangeEvent
     */
    public void propertyChange(java.beans.PropertyChangeEvent evt) {
        if ((evt.getSource() == getBrowseProfileMenuItem())
                && (evt.getPropertyName().equals("enabled"))) {
            connPtoP1SetTarget();
        }
    }

    public void resetButtons() {
        boolean isConnected = (getSession() == null)
                || (getSession().isConnected());
        getLoginButton().setEnabled(!isConnected);
        getLogoutButton().setEnabled(isConnected);
        getQueryButton().setEnabled(isConnected);
        getSQLExecuteButton().setEnabled(isConnected);
        getSelectButton().setEnabled(isConnected);
    }

    public void resetCache() {
        Vector cacheResults = new Vector();
        setCacheResults(cacheResults);
        ClassInfo info = (ClassInfo) getClassList().getSelectedValue();
        DefaultTableModel model = new DefaultTableModel();
        if (info == null) {
            getCacheTable().setModel(model);
            getCacheTable().repaint();
            return;
        }

        IdentityMap map = ((AbstractSession) getSession())
                .getIdentityMapAccessorInstance().getIdentityMap(
                        info.descriptor.getJavaClass());
        for (Enumeration cacheEnum = map.keys(); cacheEnum.hasMoreElements();) {
            CacheKey key = (CacheKey) cacheEnum.nextElement();
            if (info.descriptor.getJavaClass().isInstance(key.getObject())) {
                cacheResults.addElement(key);
            }
        }

        String[] columns = new String[4];
        columns[0] = "Key";
        columns[1] = "Hash";
        columns[2] = "Version";
        columns[3] = "Object";

        model.setColumnIdentifiers(columns);
        for (Enumeration cacheEnumeration = cacheResults.elements(); cacheEnumeration
                .hasMoreElements();) {
            CacheKey key = (CacheKey) cacheEnumeration.nextElement();
            String[] values = new String[4];
            values[0] = key.getKey().toString();
            values[1] = new Integer(key.getObject().hashCode()).toString();
            values[2] = String.valueOf(key.getWriteLockValue());
            values[3] = key.getObject().toString();
            model.addRow(values);
        }

        getCacheTable().setModel(model);
        getCacheTable().repaint();
    }

    public void resetDescriptors() {
        if (getSession() == null) {
            return;
        }

        boolean useFullNames = getFullNamesMenuItem().isSelected();
        ClassInfo[] classes = new ClassInfo[(getSession()).getDescriptors()
                .size()];
        int index = 0;
        for (Iterator iterator = (getSession()).getDescriptors().values()
                .iterator(); iterator.hasNext();) {
            classes[index] = new ClassInfo((ClassDescriptor) iterator.next(),
                    useFullNames);
            index = index + 1;
        }
        Arrays.sort(classes, new ClassInfoCompare());

        DefaultListModel list = new DefaultListModel();
        for (index = 0; index < classes.length; index++) {
            list.addElement(classes[index]);
        }
        getClassList().setModel(list);
        getClassList().repaint();
        resetCache();
    }

    public void selectSQL() {
        showBusyCursor();
        try {
            String sql = getSQLText().getSelectedText();
            if ((sql == null) || (sql.length() == 0)) {
                return;
            }
            Vector rows = getSession().executeSelectingCall(new SQLCall(sql));
            setResultRows(rows);

            getLogBook().setSelectedComponent(getResultPage());
        } finally {
            showNormalCursor();
        }
    }

    public void setCacheResults(Vector cacheResults) {
        this.cacheResults = cacheResults;
    }

    public void setLog() {
        boolean autoscroll = false;

        // Check if from testing, then set manual autoscroll.
        if (getParent() instanceof JSplitPane) {
            autoscroll = true;
        }
        Writer log = new OutputStreamWriter(new TextAreaOutputStream(
                getLogText(), autoscroll));
        if (getSession() == null) {
            return;
        }

        getSession().setLog(log);
    }

    public void setResultObjects(Vector resultObjects,
            ClassDescriptor descriptor) {
        setResults(resultObjects);
        DefaultTableModel model = new DefaultTableModel();
        if (resultObjects.isEmpty()) {
            getResultsTable().setModel(model);
            getResultsTable().repaint();
            return;
        }

        String[] columns = new String[descriptor.getMappings().size()];
        for (int index = 0; index < descriptor.getMappings().size(); index++) {
            columns[index] = (descriptor.getMappings().elementAt(index))
                    .getAttributeName();
        }

        model.setColumnIdentifiers(columns);
        for (Enumeration objectsEnumeration = resultObjects.elements(); objectsEnumeration
                .hasMoreElements();) {
            Object object = objectsEnumeration.nextElement();
            String[] values = new String[descriptor.getMappings().size()];
            for (int index = 0; index < descriptor.getMappings().size(); index++) {
                DatabaseMapping mapping = descriptor.getMappings().elementAt(
                        index);
                values[index] = String.valueOf(mapping
                        .getAttributeValueFromObject(object));
            }
            model.addRow(values);
        }

        getResultsTable().setModel(model);
        getResultsTable().repaint();
    }

    public void setResultRows(Vector resultRows) {
        setResults(resultRows);
        DefaultTableModel model = new DefaultTableModel();
        if (resultRows.isEmpty()) {
            getResultsTable().setModel(model);
            getResultsTable().repaint();
            return;
        }
        DatabaseRecord firstRow = (DatabaseRecord) resultRows.firstElement();
        String[] columns = new String[firstRow.getFields().size()];
        for (int index = 0; index < firstRow.getFields().size(); index++) {
            columns[index] = ((DatabaseField) firstRow.getFields().elementAt(
                    index)).getName();
        }
        model.setColumnIdentifiers(columns);
        for (Enumeration rowsEnumeration = resultRows.elements(); rowsEnumeration
                .hasMoreElements();) {
            DatabaseRecord row = (DatabaseRecord) rowsEnumeration.nextElement();
            String[] values = new String[row.getValues().size()];
            for (int index = 0; index < row.getValues().size(); index++) {
                values[index] = String
                        .valueOf(row.getValues().elementAt(index));
            }
            model.addRow(values);
        }

        getResultsTable().setModel(model);
        getResultsTable().repaint();
    }
    
    public void setResultReports(Vector results) {
        setResults(results);
        DefaultTableModel model = new DefaultTableModel();
        if (results.isEmpty()) {
            getResultsTable().setModel(model);
            getResultsTable().repaint();
            return;
        }
        Object first = results.get(0);
        if (first instanceof ReportQueryResult) {
            ReportQueryResult result = (ReportQueryResult) results.get(0);
            String[] columns = new String[result.getNames().size()];
            for (int index = 0; index < result.getNames().size(); index++) {
                columns[index] = result.getNames().get(index);
            }
            model.setColumnIdentifiers(columns);
            for (Iterator iterator = results.iterator(); iterator.hasNext();) {
                result = (ReportQueryResult) iterator.next();
                String[] values = new String[result.getResults().size()];
                for (int index = 0; index < result.getResults().size(); index++) {
                    values[index] = String.valueOf(result.getByIndex(index));
                }
                model.addRow(values);
            }
        } else if (first instanceof Object[]) {
            Object[] result = (Object[]) results.get(0);
            String[] columns = new String[result.length];
            for (int index = 0; index < result.length; index++) {
                columns[index] = String.valueOf(index);
            }
            model.setColumnIdentifiers(columns);
            for (Iterator iterator = results.iterator(); iterator.hasNext();) {
                result = (Object[]) iterator.next();
                String[] values = new String[result.length];
                for (int index = 0; index < result.length; index++) {
                    values[index] = String.valueOf(result[index]);
                }
                model.addRow(values);
            }
        } else {
            String[] columns = new String[1];
            columns[0] = "value";
            model.setColumnIdentifiers(columns);
            for (Iterator iterator = results.iterator(); iterator.hasNext();) {
                Object result = iterator.next();
                String[] values = new String[1];
                values[0] = String.valueOf(result);
                model.addRow(values);
            }
        }

        getResultsTable().setModel(model);
        getResultsTable().repaint();
    }

    public void setResults(Vector results) {
        this.results = results;
    }

    /**
     * Sets the session property (org.eclipse.persistence.sessions.Session)
     * value.
     * 
     * @param session
     *                The new value for the property.
     * @see #getSession
     */
    public void setSession(Session session) {
        Session oldValue = fieldSession;
        fieldSession = session;
        firePropertyChange("session", oldValue, session);

        getSession().setLogLevel(getLogLevelChoice().getSelectedIndex());

        // Do not set if not a DatabaseLogin, i.e. EIS login.
        if (getSession().getDatasourceLogin() instanceof DatabaseLogin) {
            getLoginEditorPanel().setLogin(
                    (DatabaseLogin) (getSession()).getDatasourceLogin());
        }
        resetDescriptors();
        resetButtons();
        setLog();
    }

    public void setup() {
        resetButtons();
        logProfileChanged();
        profileChanged();
        getExpressionPanel().reset();
        getLoginEditorPanel().reset();
        loadLoginFromFile();
        loadSQLFromFile();
    }

    /**
     * Change to the busy cursor.
     */
    public void showBusyCursor() {
        Container parent = this;
        while (parent != null) {
            parent.setCursor(java.awt.Cursor
                    .getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
            parent = parent.getParent();
        }
    }

    /**
     * Change from the busy cursor.
     */
    public void showNormalCursor() {
        Container parent = this;
        while (parent != null) {
            parent.setCursor(java.awt.Cursor
                    .getPredefinedCursor(java.awt.Cursor.DEFAULT_CURSOR));
            parent = parent.getParent();
        }
    }

    public void storeLoginToFile() {
        DatabaseLogin login = getLoginEditorPanel().getLogin();
        try {
            FileOutputStream file = new FileOutputStream("console.login");
            ObjectOutputStream stream = new ObjectOutputStream(file);
            stream.writeObject(login);
            stream.flush();
            file.flush();
            stream.close();
            file.close();
        } catch (Exception exception) {
            // Ignore file access errors if file access is not allowed then the
            // login will not be stored.
        }
    }

    public void storeSQLToFile() {
        String sql = getSQLText().getText();

        try {
            FileOutputStream file = new FileOutputStream("console.sql");
            file.write(sql.getBytes());
            file.flush();
            file.close();
        } catch (Exception exception) {
            // Ignore file access errors if file access is not allowed then the
            // login will not be stored.
        }
    }

    public void templateSQLDelete() {
        getSQLText().setText(
                getSQLText().getText() + "\n\nDelete from <table> where <exp>");
    }

    public void templateSQLInsert() {
        getSQLText()
                .setText(
                        getSQLText().getText()
                                + "\n\nInsert into <table> (<fields>) values (<values>)");
    }

    public void templateSQLSelect() {
        getSQLText()
                .setText(
                        getSQLText().getText()
                                + "\n\nSelect <fields> from <tables> where <exp> order by <exp> group by <exp>");
    }

    public void templateSQLUpdate() {
        getSQLText()
                .setText(
                        getSQLText().getText()
                                + "\n\nUpdate <table> set <<field> = <value>> where <exp>");
    }

    /**
     * Method to handle events for the ListSelectionListener interface.
     * 
     * @param e
     *                event.ListSelectionEvent
     */
    public void valueChanged(ListSelectionEvent e) {
        if (e.getSource() == getClassList()) {
            connEtoC3(e);
        }
    }

    private void updateLogin() {
        if (shouldUseJndiConnector
                && getLoginEditorPanel().getLogin().getPlatform().isOracle()) {
            try {
                Class connectionPoolHelperClass = Class
                        .forName("org.eclipse.persistence.tools.sessionconsole.OracleConnectionPoolHelper");
                Class[] params = new Class[] { java.lang.String.class };
                java.lang.reflect.Method method = connectionPoolHelperClass
                        .getDeclaredMethod("getOracleDataSource", params);
                Object[] args = new Object[] { getLoginEditorPanel().getLogin()
                        .getConnectionString() };
                javax.sql.DataSource datasource = (javax.sql.DataSource) method
                        .invoke(null, args);
                if (datasource == null) {
                    return;
                }
                org.eclipse.persistence.sessions.JNDIConnector jndiConnector = new org.eclipse.persistence.sessions.JNDIConnector();
                jndiConnector.setDataSource(datasource);
                getLoginEditorPanel().getLogin().setConnector(jndiConnector);
                getLoginEditorPanel().getLogin()
                        .setUsesExternalConnectionPooling(true);
            } catch (Exception e) {
                System.out
                        .println("Got exception while updating Session console login.  This usually results from running on a system not compiled with "
                                + " Oracle dependencies.  If you are not running on Oracle, this is not a problem.");
                handleException(e);
            }
        }
    }
}
