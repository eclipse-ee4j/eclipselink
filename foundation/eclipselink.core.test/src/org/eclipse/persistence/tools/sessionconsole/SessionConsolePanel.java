/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
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
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.tools.beans.*;
import org.eclipse.persistence.tools.profiler.*;
import org.eclipse.persistence.sessions.factories.*;

/**
 * Main panel for the session console.
 * The session console is used by the testing browser.
 */
public class SessionConsolePanel extends JPanel implements ActionListener, 
                                                           ItemListener, 
                                                           MouseListener, 
                                                           java.beans.PropertyChangeListener, 
                                                           ListSelectionListener {
    protected int compileCount = 0;
    protected Vector results;
    protected Vector cacheResults;
    private Session fieldSession = null;
    private JButton ivjBrowseProfileButton = null;
    private JMenuItem ivjBrowseProfileMenuItem = null;
    private JPanel sqlPage = null;
    private JPanel sqlPagePage = null;
    private JPopupMenu sqlPagePopupMenu = null;
    private JScrollPane sqlPageScrollPane = null;
    private JTable sqlPageTable = null;
    private JList ivjClassList = null;
    private JScrollPane ivjClassScrollPane = null;
    private JButton ivjClearButton = null;
    private JButton ivjClearCacheButton = null;
    private JMenuItem ivjClearCacheMenuItem = null;
    private JMenuItem ivjClearDescriptorsMenuItem = null;
    private JButton ivjClearLogButton = null;
    private JMenuItem cutMenuItem = null;
    private JMenuItem copyMenuItem = null;
    private JMenuItem pasteMenuItem = null;
    private JMenuItem ivjClearLogMenuItem = null;
    private JMenuItem ivjClearResultsMenuItem = null;
    private JMenuItem ivjClearSQLMenuItem = null;
    private JMenuItem ivjCopySQLMenuItem = null;
    private JMenuItem ivjCutSQLMenuItem = null;
    private JMenuItem ivjDeleteSQLMenuItem = null;
    private JPopupMenu ivjDescriptorMenu = null;
    private JMenuItem ivjExecuteQueryMenuItem = null;
    private ExpressionPanel ivjExpressionPanel = null;
    private JCheckBoxMenuItem ivjFullNamesMenuItem = null;
    private JMenuItem ivjInsertSQLMenuItem = null;
    private JButton ivjInspectCacheButton = null;
    private JMenuItem ivjInspectCacheMenuItem = null;
    private JMenuItem ivjInspectDescriptorMenuItem = null;
    private JMenuItem ivjInspectResultMenuItem = null;
    private JMenuItem ivjInspectSessionMenuItem1 = null;
    private JPopupMenu ivjJPopupMenu1 = null;
    private JSeparator ivjJSeparator4 = null;
    private JSeparator ivjJSeparator5 = null;
    private JSeparator ivjJSeparator6 = null;
    private JSeparator ivjJSeparator7 = null;
    private JButton ivjLoadProjectButton = null;
    private JTabbedPane ivjLogBook = null;
    private JComboBox logLevelChoice = null;
    private JButton ivjLoginButton = null;
    private LoginEditorPanel ivjLoginEditorPanel = null;
    private JPopupMenu ivjLoginMenu = null;
    private JMenuItem ivjLoginMenuItem = null;
    private JPanel ivjLoginPage = null;
    private JPopupMenu ivjLogMenu = null;
    private JButton ivjLogoutButton = null;
    private JMenuItem ivjLogoutMenuItem = null;
    private JPanel ivjLogPage = null;
    private JCheckBox ivjLogProfileCheckbox = null;
    private JCheckBoxMenuItem ivjLogProfileMenuItem = null;
    private JCheckBoxMenuItem ivjLogProfileMenuItem1 = null;
    private JScrollPane ivjLogScrollPane = null;
    private JSplitPane ivjLogSplitter = null;
    private JTextArea ivjLogText = null;
    private JMenuItem ivjPasteSQLMenuItem = null;
    private JCheckBox ivjProfileCheckbox = null;
    private JButton ivjQueryButton = null;
    private JPopupMenu ivjQueryMenu = null;
    private JPanel ivjQueryPage = null;
    private JMenuItem ivjResetDescriptorsMenuItem = null;
    private JPanel ivjResultPage = null;
    private JScrollPane ivjResultsScrollPane = null;
    private JTable ivjResultsTable = null;
    private JButton ivjSelectButton = null;
    private JMenuItem ivjSelectSQLMenuItem = null;
    private JButton ivjSQLExecuteButton = null;
    private JPopupMenu ivjSQLMenu = null;
    private JScrollPane ivjSQLScrollPane = null;

    private JPanel javaPage = null;
    private JScrollPane javaScrollPane = null;
    private JTextPane javaText = null;
    private JButton javaExecuteButton = null;
    private JButton javaClearButton = null;

    private JMenuItem ivjSQLSelectMenuItem = null;
    private JMenu ivjSQLTemplateMenu = null;
    private JTextPane ivjSQLText = null;
    private JMenuItem ivjSQLUpdateMenuItem = null;
    private JPanel ivjTopPanel = null;
    private JSplitPane ivjTopSplitPane = null;
    private JMenuItem ivjUpdateSQLMenuItem = null;
    private JTabbedPane ivjWorkspaceBook = null;
    private JButton ivjGenerateProjectButton = null;
    
    // This attribute is used only for debugging and currently it's not accessible from the ui.
    // If set to true it indicates that the DefaultConnector
    // should be substituted for JNDIConnector, and externalConnectionPooling should be used.
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
     * @param e java.awt.event.ActionEvent
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
        if (((org.eclipse.persistence.sessions.Session)getSession()).isInProfile()) {
            ProfileBrowser.browseProfiles(((PerformanceProfiler)((org.eclipse.persistence.sessions.Session)getSession()).getProfiler()).getProfiles());
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
        ((AbstractSession)getSession()).setProject(new org.eclipse.persistence.sessions.Project());
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
     * connEtoC14:  (LogProfileCheckbox.item.itemStateChanged(java.awt.event.ItemEvent) --> SessionInspectorPanel.logProfileChanged()V)
     * @param arg1 java.awt.event.ItemEvent
     */
    private void connEtoC14(java.awt.event.ItemEvent arg1) {
        try {
            this.logProfileChanged();
        } catch (Throwable exception) {
            handleException(exception);
        }
    }

    /**
     * connEtoC15:  (ProfileCheckbox.item.itemStateChanged(java.awt.event.ItemEvent) --> SessionInspectorPanel.profileChanged()V)
     * @param arg1 java.awt.event.ItemEvent
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
     * connEtoC16:  (LogCheckbox.item.itemStateChanged(java.awt.event.ItemEvent) --> SessionInspectorPanel.loggingChanged()V)
     * @param arg1 java.awt.event.ItemEvent
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
     * connEtoC18:  (SQLText.mouse.mouseReleased(java.awt.event.MouseEvent) --> SessionInspectorPanel.genericPopupDisplay(Ljava.awt.event.MouseEvent;LJPopupMenu;)V)
     * @param arg1 java.awt.event.MouseEvent
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
     * connEtoC19:  (CacheTable.mouse.mouseReleased(java.awt.event.MouseEvent) --> SessionInspectorPanel.genericPopupDisplay(Ljava.awt.event.MouseEvent;LJPopupMenu;)V)
     * @param arg1 java.awt.event.MouseEvent
     */
    private void connEtoC19(java.awt.event.MouseEvent arg1) {
        try {
            this.genericPopupDisplay(arg1, getCachePopupMenu());
        } catch (Throwable exception) {
            handleException(exception);
        }
    }

    /**
     * connEtoC20:  (ResultsScrollPane.mouse.mouseReleased(java.awt.event.MouseEvent) --> SessionInspectorPanel.genericPopupDisplay(Ljava.awt.event.MouseEvent;LJPopupMenu;)V)
     * @param arg1 java.awt.event.MouseEvent
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
     * connEtoC21:  (ClassList.mouse.mouseReleased(java.awt.event.MouseEvent) --> SessionInspectorPanel.genericPopupDisplay(Ljava.awt.event.MouseEvent;LJPopupMenu;)V)
     * @param arg1 java.awt.event.MouseEvent
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
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
     * connEtoC3:  (ClassList.listSelection.valueChanged(event.ListSelectionEvent) --> SessionInspectorPanel.resetCache()V)
     * @param arg1 event.ListSelectionEvent
     */
    private void connEtoC3(ListSelectionEvent arg1) {
        try {
            this.descriptorChanged();
        } catch (Throwable exception) {
            handleException(exception);
        }
    }

    /**
     * connEtoC40:  (QueryPage.mouse.mouseReleased(java.awt.event.MouseEvent) --> SessionInspectorPanel.genericPopupDisplay(Ljava.awt.event.MouseEvent;LJPopupMenu;)V)
     * @param arg1 java.awt.event.MouseEvent
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
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
     * connEtoC41:  (LogText.mouse.mouseReleased(java.awt.event.MouseEvent) --> SessionInspectorPanel.genericPopupDisplay(Ljava.awt.event.MouseEvent;LJPopupMenu;)V)
     * @param arg1 java.awt.event.MouseEvent
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
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
     * connEtoC42:  (LoginPage.mouse.mouseReleased(java.awt.event.MouseEvent) --> SessionInspectorPanel.genericPopupDisplay(Ljava.awt.event.MouseEvent;LJPopupMenu;)V)
     * @param arg1 java.awt.event.MouseEvent
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
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
     * connEtoC48:  (LogSession.item.itemStateChanged(java.awt.event.ItemEvent) --> SessionConsolePanel.loggingChanged()V)
     * @param arg1 java.awt.event.ItemEvent
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    void connEtoC48(java.awt.event.ItemEvent arg1) {
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
     * connEtoC49:  (LogConnection.item.itemStateChanged(java.awt.event.ItemEvent) --> SessionConsolePanel.loggingChanged()V)
     * @param arg1 java.awt.event.ItemEvent
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    void connEtoC49(java.awt.event.ItemEvent arg1) {
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
     * connEtoC50:  (LogThread.item.itemStateChanged(java.awt.event.ItemEvent) --> SessionConsolePanel.loggingChanged()V)
     * @param arg1 java.awt.event.ItemEvent
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    void connEtoC50(java.awt.event.ItemEvent arg1) {
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
     * connEtoC51:  (LogTime.item.itemStateChanged(java.awt.event.ItemEvent) --> SessionConsolePanel.loggingChanged()V)
     * @param arg1 java.awt.event.ItemEvent
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    void connEtoC51(java.awt.event.ItemEvent arg1) {
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
     * connEtoC52:  (LogExceptions.item.itemStateChanged(java.awt.event.ItemEvent) --> SessionConsolePanel.loggingChanged()V)
     * @param arg1 java.awt.event.ItemEvent
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    void connEtoC52(java.awt.event.ItemEvent arg1) {
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
     * connEtoC53:  (LogExceptionStack.item.itemStateChanged(java.awt.event.ItemEvent) --> SessionConsolePanel.loggingChanged()V)
     * @param arg1 java.awt.event.ItemEvent
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    void connEtoC53(java.awt.event.ItemEvent arg1) {
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
     * connPtoP1SetTarget:  (BrowseProfileMenuItem.enabled <--> BrowseProfileButton.enabled)
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    void connPtoP1SetTarget() {
        /* Set the target from the source */
        try {
            getBrowseProfileButton().setEnabled(getBrowseProfileMenuItem().isEnabled());
            // user code begin {1}
            // user code end
        } catch (Throwable exception) {
            // user code begin {3}
            // user code end
            handleException(exception);
        }
    }

    /**
     * connPtoP2SetTarget:  (LogProfileMenuItem.selected <--> LogProfileCheckbox.selected)
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    void connPtoP2SetTarget() {
        /* Set the target from the source */
        try {
            getLogProfileCheckbox().setSelected(getLogProfileMenuItem().isSelected());
            // user code begin {1}
            // user code end
        } catch (Throwable exception) {
            // user code begin {3}
            // user code end
            handleException(exception);
        }
    }

    /**
     * connPtoP3SetTarget:  (ProfileCheckbox.selected <--> LogProfileMenuItem1.selected)
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    void connPtoP3SetTarget() {
        /* Set the target from the source */
        try {
            getLogProfileMenuItem1().setSelected(getProfileCheckbox().isSelected());
            // user code begin {1}
            // user code end
        } catch (Throwable exception) {
            // user code begin {3}
            // user code end
            handleException(exception);
        }
    }

    /**
     * connPtoP4SetTarget:  (LogSQLMenuItem.selected <--> LogCheckbox.selected)
     */
    private void connPtoP4SetTarget() {
        try {
            getLogLevelChoice().setSelectedIndex(getSession().getSessionLog().getLevel());
        } catch (Throwable exception) {
            handleException(exception);
        }
    }

    public void copySQL() {
        getSQLText().copy();
    }

    public void createTables() {
        ////Helper.toDo("table creators");
    }

    public void cutSQL() {
        getSQLText().cut();
    }

    public void descriptorChanged() {
        resetCache();
        ClassInfo info = (ClassInfo)getClassList().getSelectedValue();
        if (info != null) {
            getExpressionPanel().setDescriptor(info.descriptor);
        }
    }

    public void executeQuery() {
        showBusyCursor();
        try {
            ClassInfo info = (ClassInfo)getClassList().getSelectedValue();
            if (info == null) {
                return;
            }

            ReadAllQuery query = 
                new ReadAllQuery(getExpressionPanel().getDescriptor().getJavaClass());
            query.setSelectionCriteria(getExpressionPanel().getExpression());

            setResultObjects((Vector)getSession().executeQuery(query), 
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

    /**
     * Execute the selected Java code.
     * This is done through compiling and executing the code through sun.tools.
     * TopLink imports are auto defined,
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

                // done reflectively to remove dependancy on tools jar
            	Object[] params = new Object[1];
                params[0] = source;
                Class mainClass = Class.forName("com.sun.tools.javac.Main");
                Class[] parameterTypes = new Class[1];
                parameterTypes[0] = String[].class;
                Method method = mainClass.getMethod("compile", parameterTypes);
                int result = ((Integer)method.invoke(null, params)).intValue();
                if (result != 0) {
                    throw new RuntimeException("Java code compile failed. This could either be a legitimate compile " +
                    		"failure, or could result if you do not have the tools.jar from your JDK on the classpath.");
                }
                Class newClass = Class.forName(className);
                Object newInstance = newClass.newInstance();
                newClass.getField("session").set(newInstance, getSession());
                Object value;
                try {
                    value = 
                            newClass.getMethod("exec", (Class[])null).invoke(newInstance, 
                                                                    (Object[])null);
                } catch (java.lang.reflect.InvocationTargetException exception) {
                    throw exception.getCause();
                }
                inspect(value);
            } catch (Throwable exception) {
                if (exception instanceof RuntimeException) {
                    throw (RuntimeException)exception;
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
            menu.show(mouseEvent.getComponent(), mouseEvent.getX(), 
                      mouseEvent.getY());
        }
    }

    /**
     * Return the BrowseProfileButton property value.
     * @return JButton
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JButton getBrowseProfileButton() {
        if (ivjBrowseProfileButton == null) {
            try {
                ivjBrowseProfileButton = new JButton();
                ivjBrowseProfileButton.setName("BrowseProfileButton");
                ivjBrowseProfileButton.setText("Browse Profile");
                ivjBrowseProfileButton.setBackground(java.awt.SystemColor.control);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return ivjBrowseProfileButton;
    }

    /**
     * Return the BrowseProfileMenuItem property value.
     * @return JMenuItem
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JMenuItem getBrowseProfileMenuItem() {
        if (ivjBrowseProfileMenuItem == null) {
            try {
                ivjBrowseProfileMenuItem = new JMenuItem();
                ivjBrowseProfileMenuItem.setName("BrowseProfileMenuItem");
                ivjBrowseProfileMenuItem.setText("Browse Profile");
                ivjBrowseProfileMenuItem.setBackground(java.awt.SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return ivjBrowseProfileMenuItem;
    }

    private JPanel getSQLPage() {
        if (sqlPage == null) {
            try {
                sqlPage = new JPanel();
                sqlPage.setName("SQL");
                sqlPage.setLayout(new java.awt.GridBagLayout());
                sqlPage.setBackground(java.awt.SystemColor.control);

                java.awt.GridBagConstraints constraintsSQLExecuteButton = 
                    new java.awt.GridBagConstraints();
                constraintsSQLExecuteButton.gridx = 1;
                constraintsSQLExecuteButton.gridy = 1;
                constraintsSQLExecuteButton.insets = 
                        new java.awt.Insets(2, 2, 2, 2);
                getSQLPage().add(getSQLExecuteButton(), 
                                 constraintsSQLExecuteButton);

                java.awt.GridBagConstraints constraintsClearButton = 
                    new java.awt.GridBagConstraints();
                constraintsClearButton.gridx = 2;
                constraintsClearButton.gridy = 1;
                constraintsClearButton.insets = 
                        new java.awt.Insets(2, 2, 2, 2);
                getSQLPage().add(getClearButton(), constraintsClearButton);

                java.awt.GridBagConstraints constraintsSelectButton = 
                    new java.awt.GridBagConstraints();
                constraintsSelectButton.gridx = 0;
                constraintsSelectButton.gridy = 1;
                constraintsSelectButton.insets = 
                        new java.awt.Insets(2, 2, 2, 2);
                getSQLPage().add(getSelectButton(), constraintsSelectButton);

                java.awt.GridBagConstraints constraintsSQLScrollPane = 
                    new java.awt.GridBagConstraints();
                constraintsSQLScrollPane.gridx = 0;
                constraintsSQLScrollPane.gridy = 0;
                constraintsSQLScrollPane.gridwidth = 5;
                constraintsSQLScrollPane.fill = 
                        java.awt.GridBagConstraints.BOTH;
                constraintsSQLScrollPane.weightx = 1.0;
                constraintsSQLScrollPane.weighty = 1.0;
                constraintsSQLScrollPane.insets = 
                        new java.awt.Insets(0, 0, 2, 0);
                getSQLPage().add(getSQLScrollPane(), constraintsSQLScrollPane);
            } catch (Throwable exception) {
                handleException(exception);
            }
        }
        return sqlPage;
    }

    private JPanel getJavaPage() {
        if (javaPage == null) {
            try {
                javaPage = new JPanel();
                javaPage.setName("Java");
                javaPage.setLayout(new java.awt.GridBagLayout());
                javaPage.setBackground(java.awt.SystemColor.control);

                java.awt.GridBagConstraints constraints = 
                    new java.awt.GridBagConstraints();
                constraints.gridx = 1;
                constraints.gridy = 1;
                constraints.insets = new java.awt.Insets(2, 2, 2, 2);
                getJavaPage().add(getJavaExecuteButton(), constraints);

                constraints = new java.awt.GridBagConstraints();
                constraints.gridx = 2;
                constraints.gridy = 1;
                constraints.insets = new java.awt.Insets(2, 2, 2, 2);
                getJavaPage().add(getJavaClearButton(), constraints);

                constraints = new java.awt.GridBagConstraints();
                constraints.gridx = 0;
                constraints.gridy = 0;
                constraints.gridwidth = 5;
                constraints.fill = java.awt.GridBagConstraints.BOTH;
                constraints.weightx = 1.0;
                constraints.weighty = 1.0;
                constraints.insets = new java.awt.Insets(0, 0, 2, 0);
                getJavaPage().add(getJavaScrollPane(), constraints);
            } catch (Throwable exception) {
                handleException(exception);
            }
        }
        return javaPage;
    }

    /**
     * Return the SQLPage property value.
     * @return JPanel
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JPanel getCachePage() {
        if (sqlPagePage == null) {
            try {
                sqlPagePage = new JPanel();
                sqlPagePage.setName("CachePage");
                sqlPagePage.setLayout(new java.awt.GridBagLayout());
                sqlPagePage.setBackground(java.awt.SystemColor.control);

                java.awt.GridBagConstraints constraintsClearCacheButton = 
                    new java.awt.GridBagConstraints();
                constraintsClearCacheButton.gridx = 0;
                constraintsClearCacheButton.gridy = 1;
                constraintsClearCacheButton.insets = 
                        new java.awt.Insets(2, 2, 2, 2);
                getCachePage().add(getClearCacheButton(), 
                                   constraintsClearCacheButton);

                java.awt.GridBagConstraints constraintsCacheScrollPane = 
                    new java.awt.GridBagConstraints();
                constraintsCacheScrollPane.gridx = 0;
                constraintsCacheScrollPane.gridy = 0;
                constraintsCacheScrollPane.gridwidth = 6;
                constraintsCacheScrollPane.fill = 
                        java.awt.GridBagConstraints.BOTH;
                constraintsCacheScrollPane.weightx = 1.0;
                constraintsCacheScrollPane.weighty = 1.0;
                constraintsCacheScrollPane.insets = 
                        new java.awt.Insets(0, 0, 2, 0);
                getCachePage().add(getCacheScrollPane(), 
                                   constraintsCacheScrollPane);

                java.awt.GridBagConstraints constraintsInspectCacheButton = 
                    new java.awt.GridBagConstraints();
                constraintsInspectCacheButton.gridx = 1;
                constraintsInspectCacheButton.gridy = 1;
                constraintsInspectCacheButton.insets = 
                        new java.awt.Insets(2, 2, 2, 2);
                getCachePage().add(getInspectCacheButton(), 
                                   constraintsInspectCacheButton);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return sqlPagePage;
    }

    /**
     * Return the CachePopupMenu property value.
     * @return JPopupMenu
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JPopupMenu getCachePopupMenu() {
        if (sqlPagePopupMenu == null) {
            try {
                sqlPagePopupMenu = new JPopupMenu();
                sqlPagePopupMenu.setName("CachePopupMenu");
                sqlPagePopupMenu.add(getClearCacheMenuItem());
                sqlPagePopupMenu.add(getInspectCacheMenuItem());
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
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
     * @return JScrollPane
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JScrollPane getCacheScrollPane() {
        if (sqlPageScrollPane == null) {
            try {
                sqlPageScrollPane = new JScrollPane();
                sqlPageScrollPane.setName("CacheScrollPane");
                getCacheScrollPane().setViewportView(getCacheTable());
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return sqlPageScrollPane;
    }

    /**
     * Return the CacheTable property value.
     * @return JTable
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JTable getCacheTable() {
        if (sqlPageTable == null) {
            try {
                sqlPageTable = new JTable();
                sqlPageTable.setName("CacheTable");
                getCacheScrollPane().setColumnHeaderView(sqlPageTable.getTableHeader());
                sqlPageTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
                sqlPageTable.setPreferredSize(new java.awt.Dimension(600, 
                                                                     300));
                sqlPageTable.setBounds(0, 0, 583, 370);
                sqlPageTable.setPreferredScrollableViewportSize(new java.awt.Dimension(600, 
                                                                                       300));
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return sqlPageTable;
    }

    /**
     * Return the ClassList property value.
     * @return JList
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JList getClassList() {
        if (ivjClassList == null) {
            try {
                ivjClassList = new JList();
                ivjClassList.setName("ClassList");
                ivjClassList.setBorder(new javax.swing.plaf.basic.BasicBorders.MarginBorder());
                ivjClassList.setBounds(0, 0, 160, 428);
                ivjClassList.setMinimumSize(new java.awt.Dimension(1, 1));
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return ivjClassList;
    }

    /**
     * Return the ClassScrollPane property value.
     * @return JScrollPane
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JScrollPane getClassScrollPane() {
        if (ivjClassScrollPane == null) {
            try {
                ivjClassScrollPane = new JScrollPane();
                ivjClassScrollPane.setName("ClassScrollPane");
                ivjClassScrollPane.setMaximumSize(new java.awt.Dimension(0, 
                                                                         0));
                ivjClassScrollPane.setPreferredSize(new java.awt.Dimension(120, 
                                                                           0));
                ivjClassScrollPane.setMinimumSize(new java.awt.Dimension(100, 
                                                                         0));
                getClassScrollPane().setViewportView(getClassList());
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return ivjClassScrollPane;
    }

    /**
     * Return the ClearButton property value.
     * @return JButton
     */
    private JButton getClearButton() {
        if (ivjClearButton == null) {
            try {
                ivjClearButton = new JButton();
                ivjClearButton.setName("ClearButton");
                ivjClearButton.setText("Clear");
                ivjClearButton.setBackground(java.awt.SystemColor.control);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return ivjClearButton;
    }

    private JButton getJavaClearButton() {
        if (javaClearButton == null) {
            try {
                javaClearButton = new JButton();
                javaClearButton.setName("JavaClearButton");
                javaClearButton.setText("Clear");
                javaClearButton.setBackground(java.awt.SystemColor.control);
            } catch (Throwable exception) {
                handleException(exception);
            }
        }
        return javaClearButton;
    }

    /**
     * Return the ClearCacheButton property value.
     * @return JButton
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JButton getClearCacheButton() {
        if (ivjClearCacheButton == null) {
            try {
                ivjClearCacheButton = new JButton();
                ivjClearCacheButton.setName("ClearCacheButton");
                ivjClearCacheButton.setText("Clear Cache");
                ivjClearCacheButton.setBackground(java.awt.SystemColor.control);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return ivjClearCacheButton;
    }

    /**
     * Return the ClearCacheMenuItem property value.
     * @return JMenuItem
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JMenuItem getClearCacheMenuItem() {
        if (ivjClearCacheMenuItem == null) {
            try {
                ivjClearCacheMenuItem = new JMenuItem();
                ivjClearCacheMenuItem.setName("ClearCacheMenuItem");
                ivjClearCacheMenuItem.setText("Clear Cache");
                ivjClearCacheMenuItem.setBackground(java.awt.SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return ivjClearCacheMenuItem;
    }

    /**
     * Return the ClearDescriptorsMenuItem property value.
     * @return JMenuItem
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JMenuItem getClearDescriptorsMenuItem() {
        if (ivjClearDescriptorsMenuItem == null) {
            try {
                ivjClearDescriptorsMenuItem = new JMenuItem();
                ivjClearDescriptorsMenuItem.setName("ClearDescriptorsMenuItem");
                ivjClearDescriptorsMenuItem.setText("Clear Descriptors");
                ivjClearDescriptorsMenuItem.setBackground(java.awt.SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return ivjClearDescriptorsMenuItem;
    }

    /**
     * Return the ClearLogButton property value.
     * @return JButton
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JButton getClearLogButton() {
        if (ivjClearLogButton == null) {
            try {
                ivjClearLogButton = new JButton();
                ivjClearLogButton.setName("ClearLogButton");
                ivjClearLogButton.setText("Clear");
                ivjClearLogButton.setBackground(java.awt.SystemColor.control);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return ivjClearLogButton;
    }

    /**
     * Return the ClearLogMenuItem property value.
     * @return JMenuItem
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JMenuItem getClearLogMenuItem() {
        if (ivjClearLogMenuItem == null) {
            try {
                ivjClearLogMenuItem = new JMenuItem();
                ivjClearLogMenuItem.setName("ClearLogMenuItem");
                ivjClearLogMenuItem.setText("Clear Log");
                ivjClearLogMenuItem.setBackground(java.awt.SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return ivjClearLogMenuItem;
    }

    private JMenuItem getCutMenuItem() {
        if (cutMenuItem == null) {
            try {
                cutMenuItem = new JMenuItem();
                cutMenuItem.setName("CutMenuItem");
                cutMenuItem.setText("Cut");
                cutMenuItem.setBackground(java.awt.SystemColor.menu);
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
                copyMenuItem.setBackground(java.awt.SystemColor.menu);
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
                pasteMenuItem.setBackground(java.awt.SystemColor.menu);
            } catch (Throwable exception) {
                handleException(exception);
            }
        }
        return pasteMenuItem;
    }

    /**
     * Return the ClearResultsMenuItem property value.
     * @return JMenuItem
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JMenuItem getClearResultsMenuItem() {
        if (ivjClearResultsMenuItem == null) {
            try {
                ivjClearResultsMenuItem = new JMenuItem();
                ivjClearResultsMenuItem.setName("ClearResultsMenuItem");
                ivjClearResultsMenuItem.setText("Clear Results");
                ivjClearResultsMenuItem.setBackground(java.awt.SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return ivjClearResultsMenuItem;
    }

    /**
     * Return the ClearSQLMenuItem property value.
     * @return JMenuItem
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JMenuItem getClearSQLMenuItem() {
        if (ivjClearSQLMenuItem == null) {
            try {
                ivjClearSQLMenuItem = new JMenuItem();
                ivjClearSQLMenuItem.setName("ClearSQLMenuItem");
                ivjClearSQLMenuItem.setText("Clear SQL");
                ivjClearSQLMenuItem.setBackground(java.awt.SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return ivjClearSQLMenuItem;
    }

    /**
     * Return the CopySQLMenuItem property value.
     * @return JMenuItem
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JMenuItem getCopySQLMenuItem() {
        if (ivjCopySQLMenuItem == null) {
            try {
                ivjCopySQLMenuItem = new JMenuItem();
                ivjCopySQLMenuItem.setName("CopySQLMenuItem");
                ivjCopySQLMenuItem.setText("Copy");
                ivjCopySQLMenuItem.setBackground(java.awt.SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return ivjCopySQLMenuItem;
    }

    /**
     * Return the CutSQLMenuItem property value.
     * @return JMenuItem
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JMenuItem getCutSQLMenuItem() {
        if (ivjCutSQLMenuItem == null) {
            try {
                ivjCutSQLMenuItem = new JMenuItem();
                ivjCutSQLMenuItem.setName("CutSQLMenuItem");
                ivjCutSQLMenuItem.setText("Cut");
                ivjCutSQLMenuItem.setBackground(java.awt.SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return ivjCutSQLMenuItem;
    }

    /**
     * Return the DeleteSQLMenuItem property value.
     * @return JMenuItem
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JMenuItem getDeleteSQLMenuItem() {
        if (ivjDeleteSQLMenuItem == null) {
            try {
                ivjDeleteSQLMenuItem = new JMenuItem();
                ivjDeleteSQLMenuItem.setName("DeleteSQLMenuItem");
                ivjDeleteSQLMenuItem.setText("Delete SQL");
                ivjDeleteSQLMenuItem.setBackground(java.awt.SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return ivjDeleteSQLMenuItem;
    }

    /**
     * Return the DescriptorMenu property value.
     * @return JPopupMenu
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JPopupMenu getDescriptorMenu() {
        if (ivjDescriptorMenu == null) {
            try {
                ivjDescriptorMenu = new JPopupMenu();
                ivjDescriptorMenu.setName("DescriptorMenu");
                ivjDescriptorMenu.add(getResetDescriptorsMenuItem());
                ivjDescriptorMenu.add(getClearDescriptorsMenuItem());
                ivjDescriptorMenu.add(getFullNamesMenuItem());
                ivjDescriptorMenu.add(getInspectDescriptorMenuItem());
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return ivjDescriptorMenu;
    }

    /**
     * Return the ExecuteQueryMenuItem property value.
     * @return JMenuItem
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JMenuItem getExecuteQueryMenuItem() {
        if (ivjExecuteQueryMenuItem == null) {
            try {
                ivjExecuteQueryMenuItem = new JMenuItem();
                ivjExecuteQueryMenuItem.setName("ExecuteQueryMenuItem");
                ivjExecuteQueryMenuItem.setText("Execute Query");
                ivjExecuteQueryMenuItem.setBackground(java.awt.SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return ivjExecuteQueryMenuItem;
    }

    /**
     * Return the ExpressionPanel property value.
     * @return ExpressionPanel
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    org.eclipse.persistence.tools.beans.ExpressionPanel getExpressionPanel() {
        if (ivjExpressionPanel == null) {
            try {
                ivjExpressionPanel = 
                        new org.eclipse.persistence.tools.beans.ExpressionPanel();
                ivjExpressionPanel.setName("ExpressionPanel");
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return ivjExpressionPanel;
    }

    /**
     * Return the FullNamesMenuItem property value.
     * @return JCheckBoxMenuItem
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JCheckBoxMenuItem getFullNamesMenuItem() {
        if (ivjFullNamesMenuItem == null) {
            try {
                ivjFullNamesMenuItem = new JCheckBoxMenuItem();
                ivjFullNamesMenuItem.setName("FullNamesMenuItem");
                ivjFullNamesMenuItem.setText("Display Package Names");
                ivjFullNamesMenuItem.setBackground(java.awt.SystemColor.control);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return ivjFullNamesMenuItem;
    }

    /**
     * Return the InsertSQLMenuItem property value.
     * @return JMenuItem
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JMenuItem getInsertSQLMenuItem() {
        if (ivjInsertSQLMenuItem == null) {
            try {
                ivjInsertSQLMenuItem = new JMenuItem();
                ivjInsertSQLMenuItem.setName("InsertSQLMenuItem");
                ivjInsertSQLMenuItem.setText("Insert SQL");
                ivjInsertSQLMenuItem.setBackground(java.awt.SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return ivjInsertSQLMenuItem;
    }

    /**
     * Return the InspectCacheButton property value.
     * @return JButton
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JButton getInspectCacheButton() {
        if (ivjInspectCacheButton == null) {
            try {
                ivjInspectCacheButton = new JButton();
                ivjInspectCacheButton.setName("InspectCacheButton");
                ivjInspectCacheButton.setText("Inspect");
                ivjInspectCacheButton.setBackground(java.awt.SystemColor.control);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return ivjInspectCacheButton;
    }

    /**
     * Return the InspectCacheMenuItem property value.
     * @return JMenuItem
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JMenuItem getInspectCacheMenuItem() {
        if (ivjInspectCacheMenuItem == null) {
            try {
                ivjInspectCacheMenuItem = new JMenuItem();
                ivjInspectCacheMenuItem.setName("InspectCacheMenuItem");
                ivjInspectCacheMenuItem.setText("Inspect Cache");
                ivjInspectCacheMenuItem.setBackground(java.awt.SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return ivjInspectCacheMenuItem;
    }

    /**
     * Return the InspectDescriptorMenuItem property value.
     * @return JMenuItem
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JMenuItem getInspectDescriptorMenuItem() {
        if (ivjInspectDescriptorMenuItem == null) {
            try {
                ivjInspectDescriptorMenuItem = new JMenuItem();
                ivjInspectDescriptorMenuItem.setName("InspectDescriptorMenuItem");
                ivjInspectDescriptorMenuItem.setText("Inspect Descriptor");
                ivjInspectDescriptorMenuItem.setBackground(java.awt.SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return ivjInspectDescriptorMenuItem;
    }

    /**
     * Return the InspectResultMenuItem property value.
     * @return JMenuItem
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JMenuItem getInspectResultMenuItem() {
        if (ivjInspectResultMenuItem == null) {
            try {
                ivjInspectResultMenuItem = new JMenuItem();
                ivjInspectResultMenuItem.setName("InspectResultMenuItem");
                ivjInspectResultMenuItem.setText("Inspect Result");
                ivjInspectResultMenuItem.setBackground(java.awt.SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return ivjInspectResultMenuItem;
    }

    /**
     * Return the InspectSessionMenuItem1 property value.
     * @return JMenuItem
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JMenuItem getInspectSessionMenuItem1() {
        if (ivjInspectSessionMenuItem1 == null) {
            try {
                ivjInspectSessionMenuItem1 = new JMenuItem();
                ivjInspectSessionMenuItem1.setName("InspectSessionMenuItem1");
                ivjInspectSessionMenuItem1.setText("Inspect Session");
                ivjInspectSessionMenuItem1.setBackground(java.awt.SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return ivjInspectSessionMenuItem1;
    }

    /**
     * Return the JPopupMenu1 property value.
     * @return JPopupMenu
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JPopupMenu getJPopupMenu1() {
        if (ivjJPopupMenu1 == null) {
            try {
                ivjJPopupMenu1 = new JPopupMenu();
                ivjJPopupMenu1.setName("JPopupMenu1");
                ivjJPopupMenu1.add(getClearResultsMenuItem());
                ivjJPopupMenu1.add(getInspectResultMenuItem());
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return ivjJPopupMenu1;
    }

    /**
     * Return the JSeparator4 property value.
     * @return JSeparator
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JSeparator getJSeparator4() {
        if (ivjJSeparator4 == null) {
            try {
                ivjJSeparator4 = new JSeparator();
                ivjJSeparator4.setName("JSeparator4");
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return ivjJSeparator4;
    }

    /**
     * Return the JSeparator5 property value.
     * @return JSeparator
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JSeparator getJSeparator5() {
        if (ivjJSeparator5 == null) {
            try {
                ivjJSeparator5 = new JSeparator();
                ivjJSeparator5.setName("JSeparator5");
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return ivjJSeparator5;
    }

    /**
     * Return the JSeparator6 property value.
     * @return JSeparator
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JSeparator getJSeparator6() {
        if (ivjJSeparator6 == null) {
            try {
                ivjJSeparator6 = new JSeparator();
                ivjJSeparator6.setName("JSeparator6");
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return ivjJSeparator6;
    }

    /**
     * Return the JSeparator7 property value.
     * @return JSeparator
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JSeparator getJSeparator7() {
        if (ivjJSeparator7 == null) {
            try {
                ivjJSeparator7 = new JSeparator();
                ivjJSeparator7.setName("JSeparator7");
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return ivjJSeparator7;
    }

    /**
     * Return the LoadProjectButton property value.
     * @return JButton
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JButton getLoadProjectButton() {
        if (ivjLoadProjectButton == null) {
            try {
                ivjLoadProjectButton = new JButton();
                ivjLoadProjectButton.setName("LoadProjectButton");
                ivjLoadProjectButton.setText("Load Project...");
                ivjLoadProjectButton.setBackground(java.awt.SystemColor.control);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return ivjLoadProjectButton;
    }

    /**
     * Return the LogBook property value.
     * @return JTabbedPane
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JTabbedPane getLogBook() {
        if (ivjLogBook == null) {
            try {
                ivjLogBook = new JTabbedPane();
                ivjLogBook.setName("LogBook");
                ivjLogBook.insertTab("Log", null, getLogPage(), null, 0);
                ivjLogBook.setBackgroundAt(0, java.awt.SystemColor.control);
                ivjLogBook.insertTab("ResultPage", null, getResultPage(), null, 
                                     1);
                ivjLogBook.setBackgroundAt(1, java.awt.SystemColor.control);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return ivjLogBook;
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
     * @return JButton
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JButton getLoginButton() {
        if (ivjLoginButton == null) {
            try {
                ivjLoginButton = new JButton();
                ivjLoginButton.setName("LoginButton");
                ivjLoginButton.setText("Login");
                ivjLoginButton.setBackground(java.awt.SystemColor.control);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return ivjLoginButton;
    }

    public LoginEditorPanel getLoginEditor() {
        return getLoginEditorPanel();
    }

    /**
     * Return the LoginEditorPanel property value.
     * @return LoginEditorPanel
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    LoginEditorPanel getLoginEditorPanel() {
        if (ivjLoginEditorPanel == null) {
            try {
                ivjLoginEditorPanel = 
                        new org.eclipse.persistence.tools.sessionconsole.LoginEditorPanel();
                ivjLoginEditorPanel.setName("LoginEditorPanel");
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return ivjLoginEditorPanel;
    }

    /**
     * Return the LoginMenu property value.
     * @return JPopupMenu
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JPopupMenu getLoginMenu() {
        if (ivjLoginMenu == null) {
            try {
                ivjLoginMenu = new JPopupMenu();
                ivjLoginMenu.setName("LoginMenu");
                ivjLoginMenu.add(getLoginMenuItem());
                ivjLoginMenu.add(getLogoutMenuItem());
                ivjLoginMenu.add(getInspectSessionMenuItem1());
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return ivjLoginMenu;
    }

    /**
     * Return the LoginMenuItem property value.
     * @return JMenuItem
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JMenuItem getLoginMenuItem() {
        if (ivjLoginMenuItem == null) {
            try {
                ivjLoginMenuItem = new JMenuItem();
                ivjLoginMenuItem.setName("LoginMenuItem");
                ivjLoginMenuItem.setText("Login");
                ivjLoginMenuItem.setBackground(java.awt.SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return ivjLoginMenuItem;
    }

    /**
     * Return the LoginPage property value.
     * @return JPanel
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JPanel getLoginPage() {
        if (ivjLoginPage == null) {
            try {
                ivjLoginPage = new JPanel();
                ivjLoginPage.setName("LoginPage");
                ivjLoginPage.setLayout(new java.awt.GridBagLayout());
                ivjLoginPage.setBackground(java.awt.SystemColor.control);

                java.awt.GridBagConstraints constraintsLoginEditorPanel = 
                    new java.awt.GridBagConstraints();
                constraintsLoginEditorPanel.gridx = 0;
                constraintsLoginEditorPanel.gridy = 0;
                constraintsLoginEditorPanel.gridwidth = 6;
                constraintsLoginEditorPanel.fill = 
                        java.awt.GridBagConstraints.BOTH;
                constraintsLoginEditorPanel.weightx = 1.0;
                constraintsLoginEditorPanel.weighty = 1.0;
                constraintsLoginEditorPanel.insets = 
                        new java.awt.Insets(2, 2, 2, 2);
                getLoginPage().add(getLoginEditorPanel(), 
                                   constraintsLoginEditorPanel);

                java.awt.GridBagConstraints constraintsLoginButton = 
                    new java.awt.GridBagConstraints();
                constraintsLoginButton.gridx = 0;
                constraintsLoginButton.gridy = 1;
                constraintsLoginButton.insets = 
                        new java.awt.Insets(2, 2, 2, 2);
                getLoginPage().add(getLoginButton(), constraintsLoginButton);

                java.awt.GridBagConstraints constraintsLogoutButton = 
                    new java.awt.GridBagConstraints();
                constraintsLogoutButton.gridx = 1;
                constraintsLogoutButton.gridy = 1;
                constraintsLogoutButton.insets = 
                        new java.awt.Insets(2, 2, 2, 2);
                getLoginPage().add(getLogoutButton(), constraintsLogoutButton);

                java.awt.GridBagConstraints constraintsLoadProjectButton = 
                    new java.awt.GridBagConstraints();
                constraintsLoadProjectButton.gridx = 2;
                constraintsLoadProjectButton.gridy = 1;
                constraintsLoadProjectButton.insets = 
                        new java.awt.Insets(2, 2, 2, 2);
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
        return ivjLoginPage;
    }

    /**
     * Return the LogMenu property value.
     * @return JPopupMenu
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JPopupMenu getLogMenu() {
        if (ivjLogMenu == null) {
            try {
                ivjLogMenu = new JPopupMenu();
                ivjLogMenu.setName("LogMenu");
                ivjLogMenu.add(getCutMenuItem());
                ivjLogMenu.add(getCopyMenuItem());
                ivjLogMenu.add(getPasteMenuItem());
                ivjLogMenu.add(getJSeparator7());
                ivjLogMenu.add(getClearLogMenuItem());
                ivjLogMenu.add(getJSeparator7());
                ivjLogMenu.add(getLogProfileMenuItem1());
                ivjLogMenu.add(getLogProfileMenuItem());
                ivjLogMenu.add(getBrowseProfileMenuItem());
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return ivjLogMenu;
    }

    /**
     * Return the LogoutButton property value.
     * @return JButton
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JButton getLogoutButton() {
        if (ivjLogoutButton == null) {
            try {
                ivjLogoutButton = new JButton();
                ivjLogoutButton.setName("LogoutButton");
                ivjLogoutButton.setText("Logout");
                ivjLogoutButton.setBackground(java.awt.SystemColor.control);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return ivjLogoutButton;
    }

    /**
     * Return the LogoutMenuItem property value.
     * @return JMenuItem
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JMenuItem getLogoutMenuItem() {
        if (ivjLogoutMenuItem == null) {
            try {
                ivjLogoutMenuItem = new JMenuItem();
                ivjLogoutMenuItem.setName("LogoutMenuItem");
                ivjLogoutMenuItem.setText("Logout");
                ivjLogoutMenuItem.setBackground(java.awt.SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return ivjLogoutMenuItem;
    }

    /**
     * Return the LogPage property value.
     * @return JPanel
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JPanel getLogPage() {
        if (ivjLogPage == null) {
            try {
                ivjLogPage = new JPanel();
                ivjLogPage.setName("LogPage");
                ivjLogPage.setLayout(new java.awt.GridBagLayout());
                ivjLogPage.setBackground(java.awt.SystemColor.control);

                java.awt.GridBagConstraints constraintsLogScrollPane = 
                    new java.awt.GridBagConstraints();
                constraintsLogScrollPane.gridx = 0;
                constraintsLogScrollPane.gridy = 0;
                constraintsLogScrollPane.gridwidth = 13;
                constraintsLogScrollPane.fill = 
                        java.awt.GridBagConstraints.BOTH;
                constraintsLogScrollPane.weightx = 1.0;
                constraintsLogScrollPane.weighty = 1.0;
                constraintsLogScrollPane.insets = 
                        new java.awt.Insets(2, 2, 2, 2);
                getLogPage().add(getLogScrollPane(), constraintsLogScrollPane);

                java.awt.GridBagConstraints constraintsClearLogButton = 
                    new java.awt.GridBagConstraints();
                constraintsClearLogButton.gridx = 12;
                constraintsClearLogButton.gridy = 1;
                constraintsClearLogButton.anchor = 
                        java.awt.GridBagConstraints.EAST;
                constraintsClearLogButton.insets = 
                        new java.awt.Insets(2, 2, 2, 2);
                getLogPage().add(getClearLogButton(), 
                                 constraintsClearLogButton);

                GridBagConstraints constraintsLogLabel = 
                    new GridBagConstraints();
                constraintsLogLabel.gridx = 0;
                constraintsLogLabel.gridy = 1;
                constraintsLogLabel.anchor = GridBagConstraints.WEST;
                constraintsLogLabel.insets = new Insets(2, 2, 2, 2);
                getLogPage().add(new JLabel("Log Level:"), 
                                 constraintsLogLabel);

                GridBagConstraints constraintsLogCheckbox = 
                    new GridBagConstraints();
                constraintsLogCheckbox.gridx = 1;
                constraintsLogCheckbox.gridy = 1;
                constraintsLogCheckbox.fill = GridBagConstraints.HORIZONTAL;
                constraintsLogCheckbox.anchor = GridBagConstraints.WEST;
                constraintsLogCheckbox.insets = new Insets(2, 2, 2, 2);
                getLogPage().add(getLogLevelChoice(), constraintsLogCheckbox);

                java.awt.GridBagConstraints constraintsProfileCheckbox = 
                    new java.awt.GridBagConstraints();
                constraintsProfileCheckbox.gridx = 0;
                constraintsProfileCheckbox.gridy = 2;
                constraintsProfileCheckbox.anchor = 
                        java.awt.GridBagConstraints.WEST;
                constraintsProfileCheckbox.insets = 
                        new java.awt.Insets(2, 2, 2, 2);
                getLogPage().add(getProfileCheckbox(), 
                                 constraintsProfileCheckbox);

                java.awt.GridBagConstraints constraintsLogProfileCheckbox = 
                    new java.awt.GridBagConstraints();
                constraintsLogProfileCheckbox.gridx = 1;
                constraintsLogProfileCheckbox.gridy = 2;
                constraintsLogProfileCheckbox.anchor = 
                        java.awt.GridBagConstraints.WEST;
                constraintsLogProfileCheckbox.insets = 
                        new java.awt.Insets(2, 2, 2, 2);
                getLogPage().add(getLogProfileCheckbox(), 
                                 constraintsLogProfileCheckbox);

                java.awt.GridBagConstraints constraintsBrowseProfileButton = 
                    new java.awt.GridBagConstraints();
                constraintsBrowseProfileButton.gridx = 12;
                constraintsBrowseProfileButton.gridy = 2;
                constraintsBrowseProfileButton.anchor = 
                        java.awt.GridBagConstraints.EAST;
                constraintsBrowseProfileButton.insets = 
                        new java.awt.Insets(2, 2, 2, 2);
                getLogPage().add(getBrowseProfileButton(), 
                                 constraintsBrowseProfileButton);

            } catch (Throwable exception) {
                handleException(exception);
            }
        }
        return ivjLogPage;
    }

    private JCheckBox getLogProfileCheckbox() {
        if (ivjLogProfileCheckbox == null) {
            try {
                ivjLogProfileCheckbox = new JCheckBox();
                ivjLogProfileCheckbox.setName("LogProfileCheckbox");
                ivjLogProfileCheckbox.setText("Log Profile");
                ivjLogProfileCheckbox.setBackground(java.awt.SystemColor.control);
            } catch (Throwable exception) {
                handleException(exception);
            }
        }
        return ivjLogProfileCheckbox;
    }

    /**
     * Return the LogProfileMenuItem property value.
     * @return JCheckBoxMenuItem
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JCheckBoxMenuItem getLogProfileMenuItem() {
        if (ivjLogProfileMenuItem == null) {
            try {
                ivjLogProfileMenuItem = new JCheckBoxMenuItem();
                ivjLogProfileMenuItem.setName("LogProfileMenuItem");
                ivjLogProfileMenuItem.setText("Log Profile");
                ivjLogProfileMenuItem.setBackground(java.awt.SystemColor.menu);
                ivjLogProfileMenuItem.setActionCommand("LogSQLMenuItem");
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return ivjLogProfileMenuItem;
    }

    /**
     * Return the LogProfileMenuItem1 property value.
     * @return JCheckBoxMenuItem
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JCheckBoxMenuItem getLogProfileMenuItem1() {
        if (ivjLogProfileMenuItem1 == null) {
            try {
                ivjLogProfileMenuItem1 = new JCheckBoxMenuItem();
                ivjLogProfileMenuItem1.setName("LogProfileMenuItem1");
                ivjLogProfileMenuItem1.setText("Profile");
                ivjLogProfileMenuItem1.setBackground(java.awt.SystemColor.menu);
                ivjLogProfileMenuItem1.setActionCommand("ProfileMenuItem");
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return ivjLogProfileMenuItem1;
    }

    /**
     * Return the LogScrollPane property value.
     * @return JScrollPane
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JScrollPane getLogScrollPane() {
        if (ivjLogScrollPane == null) {
            try {
                ivjLogScrollPane = new JScrollPane();
                ivjLogScrollPane.setName("LogScrollPane");
                ivjLogScrollPane.setAutoscrolls(true);
                getLogScrollPane().setViewportView(getLogText());
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return ivjLogScrollPane;
    }

    /**
     * Return the LogSplitter property value.
     * @return JSplitPane
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JSplitPane getLogSplitter() {
        if (ivjLogSplitter == null) {
            try {
                ivjLogSplitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
                ivjLogSplitter.setName("LogSplitter");
                ivjLogSplitter.setAutoscrolls(true);
                ivjLogSplitter.setDividerSize(8);
                ivjLogSplitter.setMaximumSize(new java.awt.Dimension(0, 0));
                ivjLogSplitter.setDividerLocation(300);
                ivjLogSplitter.setOneTouchExpandable(true);
                ivjLogSplitter.setMinimumSize(new java.awt.Dimension(0, 0));
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
        return ivjLogSplitter;
    }

    /**
     * Return the LogText property value.
     * @return JTextArea
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JTextArea getLogText() {
        if (ivjLogText == null) {
            try {
                ivjLogText = new JTextArea();
                ivjLogText.setName("LogText");
                ivjLogText.setAutoscrolls(true);
                ivjLogText.setBounds(0, 0, 10, 10);
                ivjLogText.setEditable(true);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return ivjLogText;
    }

    /**
     * Return the PasteSQLMenuItem property value.
     * @return JMenuItem
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JMenuItem getPasteSQLMenuItem() {
        if (ivjPasteSQLMenuItem == null) {
            try {
                ivjPasteSQLMenuItem = new JMenuItem();
                ivjPasteSQLMenuItem.setName("PasteSQLMenuItem");
                ivjPasteSQLMenuItem.setText("Paste");
                ivjPasteSQLMenuItem.setBackground(java.awt.SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return ivjPasteSQLMenuItem;
    }

    private JCheckBox getProfileCheckbox() {
        if (ivjProfileCheckbox == null) {
            try {
                ivjProfileCheckbox = new JCheckBox();
                ivjProfileCheckbox.setName("ProfileCheckbox");
                ivjProfileCheckbox.setText("Profile");
                ivjProfileCheckbox.setBackground(java.awt.SystemColor.control);
            } catch (Throwable exception) {
                handleException(exception);
            }
        }
        return ivjProfileCheckbox;
    }

    private JButton getQueryButton() {
        if (ivjQueryButton == null) {
            try {
                ivjQueryButton = new JButton();
                ivjQueryButton.setName("QueryButton");
                ivjQueryButton.setText("Execute Query");
                ivjQueryButton.setBackground(java.awt.SystemColor.control);
            } catch (Throwable exception) {
                handleException(exception);
            }
        }
        return ivjQueryButton;
    }

    /**
     * Return the QueryMenu property value.
     * @return JPopupMenu
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JPopupMenu getQueryMenu() {
        if (ivjQueryMenu == null) {
            try {
                ivjQueryMenu = new JPopupMenu();
                ivjQueryMenu.setName("QueryMenu");
                ivjQueryMenu.add(getExecuteQueryMenuItem());
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return ivjQueryMenu;
    }

    /**
     * Return the QueryPanel property value.
     * @return JPanel
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JPanel getQueryPage() {
        if (ivjQueryPage == null) {
            try {
                ivjQueryPage = new JPanel();
                ivjQueryPage.setName("QueryPage");
                ivjQueryPage.setLayout(new java.awt.GridBagLayout());
                ivjQueryPage.setBackground(java.awt.SystemColor.control);

                java.awt.GridBagConstraints constraintsQueryButton = 
                    new java.awt.GridBagConstraints();
                constraintsQueryButton.gridx = 0;
                constraintsQueryButton.gridy = 1;
                constraintsQueryButton.anchor = 
                        java.awt.GridBagConstraints.WEST;
                constraintsQueryButton.insets = 
                        new java.awt.Insets(2, 2, 2, 2);
                getQueryPage().add(getQueryButton(), constraintsQueryButton);

                java.awt.GridBagConstraints constraintsExpressionPanel = 
                    new java.awt.GridBagConstraints();
                constraintsExpressionPanel.gridx = 0;
                constraintsExpressionPanel.gridy = 0;
                constraintsExpressionPanel.fill = 
                        java.awt.GridBagConstraints.BOTH;
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
        return ivjQueryPage;
    }

    /**
     * Return the ResetDescriptorsMenuItem property value.
     * @return JMenuItem
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JMenuItem getResetDescriptorsMenuItem() {
        if (ivjResetDescriptorsMenuItem == null) {
            try {
                ivjResetDescriptorsMenuItem = new JMenuItem();
                ivjResetDescriptorsMenuItem.setName("ResetDescriptorsMenuItem");
                ivjResetDescriptorsMenuItem.setText("Reset Descriptors");
                ivjResetDescriptorsMenuItem.setBackground(java.awt.SystemColor.menu);
                ivjResetDescriptorsMenuItem.setForeground(java.awt.Color.black);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return ivjResetDescriptorsMenuItem;
    }

    /**
     * Return the ResultPage property value.
     * @return JPanel
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JPanel getResultPage() {
        if (ivjResultPage == null) {
            try {
                ivjResultPage = new JPanel();
                ivjResultPage.setName("Query Results");
                ivjResultPage.setLayout(new java.awt.GridBagLayout());
                ivjResultPage.setBackground(java.awt.SystemColor.control);

                java.awt.GridBagConstraints constraintsResultsScrollPane = 
                    new java.awt.GridBagConstraints();
                constraintsResultsScrollPane.gridx = 0;
                constraintsResultsScrollPane.gridy = 0;
                constraintsResultsScrollPane.fill = 
                        java.awt.GridBagConstraints.BOTH;
                constraintsResultsScrollPane.weightx = 1.0;
                constraintsResultsScrollPane.weighty = 1.0;
                constraintsResultsScrollPane.insets = 
                        new java.awt.Insets(2, 2, 2, 2);
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
        return ivjResultPage;
    }

    public Vector getResults() {
        return results;
    }

    /**
     * Return the ResultsScrollPane property value.
     * @return JScrollPane
     */
    private JScrollPane getResultsScrollPane() {
        if (ivjResultsScrollPane == null) {
            try {
                ivjResultsScrollPane = new JScrollPane();
                ivjResultsScrollPane.setName("ResultsScrollPane");
                ivjResultsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                ivjResultsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
                //ivjResultsScrollPane.setMaximumSize(new java.awt.Dimension(0, 0));
                //ivjResultsScrollPane.setPreferredSize(new java.awt.Dimension(0, 0));
                //ivjResultsScrollPane.setMinimumSize(new java.awt.Dimension(0, 0));
                getResultsScrollPane().setViewportView(getResultsTable());
            } catch (Throwable exception) {
                handleException(exception);
            }
        }
        return ivjResultsScrollPane;
    }

    /**
     * Return the ResultsTable property value.
     * @return JTable
     */
    private JTable getResultsTable() {
        if (ivjResultsTable == null) {
            try {
                ivjResultsTable = new JTable();
                ivjResultsTable.setName("ResultsTable");
                getResultsScrollPane().setColumnHeaderView(ivjResultsTable.getTableHeader());
                ivjResultsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                //ivjResultsTable.setPreferredSize(new java.awt.Dimension(700, 400));
                //ivjResultsTable.setBounds(0, 0, 200, 200);
                //ivjResultsTable.setPreferredScrollableViewportSize(new java.awt.Dimension(700, 400));
            } catch (Throwable exception) {
                handleException(exception);
            }
        }
        return ivjResultsTable;
    }

    /**
     * Return the SelectButton property value.
     * @return JButton
     */
    private JButton getSelectButton() {
        if (ivjSelectButton == null) {
            try {
                ivjSelectButton = new JButton();
                ivjSelectButton.setName("SelectButton");
                ivjSelectButton.setText("Select");
                ivjSelectButton.setBackground(java.awt.SystemColor.control);
            } catch (Throwable exception) {
                handleException(exception);
            }
        }
        return ivjSelectButton;
    }

    /**
     * Return the SelectSQLMenuItem property value.
     * @return JMenuItem
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JMenuItem getSelectSQLMenuItem() {
        if (ivjSelectSQLMenuItem == null) {
            try {
                ivjSelectSQLMenuItem = new JMenuItem();
                ivjSelectSQLMenuItem.setName("SelectSQLMenuItem");
                ivjSelectSQLMenuItem.setText("Select SQL");
                ivjSelectSQLMenuItem.setBackground(java.awt.SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return ivjSelectSQLMenuItem;
    }

    /**
     * Gets the session property (org.eclipse.persistence.sessions.Session) value.
     * @return The session property value.
     * @see #setSession
     */
    public Session getSession() {
        return fieldSession;
    }

    /**
     * Return the SQLExecuteButton property value.
     * @return JButton
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JButton getSQLExecuteButton() {
        if (ivjSQLExecuteButton == null) {
            try {
                ivjSQLExecuteButton = new JButton();
                ivjSQLExecuteButton.setName("SQLExecuteButton");
                ivjSQLExecuteButton.setText("Update");
                ivjSQLExecuteButton.setBackground(java.awt.SystemColor.control);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return ivjSQLExecuteButton;
    }

    private JButton getJavaExecuteButton() {
        if (javaExecuteButton == null) {
            try {
                javaExecuteButton = new JButton();
                javaExecuteButton.setName("JavaExecuteButton");
                javaExecuteButton.setText("Execute");
                javaExecuteButton.setBackground(java.awt.SystemColor.control);
            } catch (Throwable exception) {
                handleException(exception);
            }
        }
        return javaExecuteButton;
    }

    /**
     * Return the SQLMenu property value.
     * @return JPopupMenu
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JPopupMenu getSQLMenu() {
        if (ivjSQLMenu == null) {
            try {
                ivjSQLMenu = new JPopupMenu();
                ivjSQLMenu.setName("SQLMenu");
                ivjSQLMenu.add(getSQLUpdateMenuItem());
                ivjSQLMenu.add(getSQLSelectMenuItem());
                ivjSQLMenu.add(getJSeparator4());
                ivjSQLMenu.add(getClearSQLMenuItem());
                ivjSQLMenu.add(getJSeparator6());
                ivjSQLMenu.add(getCutSQLMenuItem());
                ivjSQLMenu.add(getCopySQLMenuItem());
                ivjSQLMenu.add(getPasteSQLMenuItem());
                ivjSQLMenu.add(getJSeparator5());
                ivjSQLMenu.add(getSQLTemplateMenu());
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return ivjSQLMenu;
    }

    /**
     * Return the SQLScrollPane property value.
     * @return JScrollPane
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JScrollPane getSQLScrollPane() {
        if (ivjSQLScrollPane == null) {
            try {
                ivjSQLScrollPane = new JScrollPane();
                ivjSQLScrollPane.setName("SQLScrollPane");
                getSQLScrollPane().setViewportView(getSQLText());
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return ivjSQLScrollPane;
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
     * @return JMenuItem
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JMenuItem getSQLSelectMenuItem() {
        if (ivjSQLSelectMenuItem == null) {
            try {
                ivjSQLSelectMenuItem = new JMenuItem();
                ivjSQLSelectMenuItem.setName("SQLSelectMenuItem");
                ivjSQLSelectMenuItem.setText("Execute SQL Select");
                ivjSQLSelectMenuItem.setBackground(java.awt.SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return ivjSQLSelectMenuItem;
    }

    /**
     * Return the SQLTemplateMenu property value.
     * @return JMenu
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JMenu getSQLTemplateMenu() {
        if (ivjSQLTemplateMenu == null) {
            try {
                ivjSQLTemplateMenu = new JMenu();
                ivjSQLTemplateMenu.setName("SQLTemplateMenu");
                ivjSQLTemplateMenu.setText("SQL");
                ivjSQLTemplateMenu.setBackground(java.awt.SystemColor.menu);
                ivjSQLTemplateMenu.add(getInsertSQLMenuItem());
                ivjSQLTemplateMenu.add(getUpdateSQLMenuItem());
                ivjSQLTemplateMenu.add(getDeleteSQLMenuItem());
                ivjSQLTemplateMenu.add(getSelectSQLMenuItem());
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return ivjSQLTemplateMenu;
    }

    /**
     * Return the SQLText property value.
     * @return JTextPane
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JTextPane getSQLText() {
        if (ivjSQLText == null) {
            try {
                ivjSQLText = new JTextPane();
                ivjSQLText.setName("SQLText");
                ivjSQLText.setBorder(new javax.swing.plaf.basic.BasicBorders.MarginBorder());
                ivjSQLText.setBounds(0, 0, 467, 156);
                ivjSQLText.setMaximumSize(new java.awt.Dimension(0, 0));
                ivjSQLText.setMinimumSize(new java.awt.Dimension(1, 1));
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return ivjSQLText;
    }

    private JTextPane getJavaText() {
        if (javaText == null) {
            try {
                javaText = new JTextPane();
                javaText.setName("JavaText");
                javaText.setBorder(new javax.swing.plaf.basic.BasicBorders.MarginBorder());
                javaText.setBounds(0, 0, 467, 156);
                javaText.setMaximumSize(new java.awt.Dimension(0, 0));
                javaText.setMinimumSize(new java.awt.Dimension(1, 1));
            } catch (Throwable exception) {
                handleException(exception);
            }
        }
        return javaText;
    }

    /**
     * Return the SQLUpdateMenuItem property value.
     * @return JMenuItem
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JMenuItem getSQLUpdateMenuItem() {
        if (ivjSQLUpdateMenuItem == null) {
            try {
                ivjSQLUpdateMenuItem = new JMenuItem();
                ivjSQLUpdateMenuItem.setName("SQLUpdateMenuItem");
                ivjSQLUpdateMenuItem.setText("Execute SQL Update");
                ivjSQLUpdateMenuItem.setBackground(java.awt.SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return ivjSQLUpdateMenuItem;
    }

    /**
     * Return the TopPanel property value.
     * @return JPanel
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JPanel getTopPanel() {
        if (ivjTopPanel == null) {
            try {
                ivjTopPanel = new JPanel();
                ivjTopPanel.setName("TopPanel");
                ivjTopPanel.setLayout(new java.awt.GridBagLayout());
                ivjTopPanel.setBackground(java.awt.SystemColor.control);

                java.awt.GridBagConstraints constraintsTopSplitPane = 
                    new java.awt.GridBagConstraints();
                constraintsTopSplitPane.gridx = 0;
                constraintsTopSplitPane.gridy = 0;
                constraintsTopSplitPane.fill = 
                        java.awt.GridBagConstraints.BOTH;
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
        return ivjTopPanel;
    }

    /**
     * Return the TopSplitPane property value.
     * @return JSplitPane
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JSplitPane getTopSplitPane() {
        if (ivjTopSplitPane == null) {
            try {
                ivjTopSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
                ivjTopSplitPane.setName("TopSplitPane");
                ivjTopSplitPane.setDividerSize(8);
                ivjTopSplitPane.setOneTouchExpandable(true);
                ivjTopSplitPane.setDividerLocation(150);
                getTopSplitPane().add(getClassScrollPane(), "left");
                getTopSplitPane().add(getWorkspaceBook(), "right");
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return ivjTopSplitPane;
    }

    /**
     * Return the UpdateSQLMenuItem property value.
     * @return JMenuItem
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JMenuItem getUpdateSQLMenuItem() {
        if (ivjUpdateSQLMenuItem == null) {
            try {
                ivjUpdateSQLMenuItem = new JMenuItem();
                ivjUpdateSQLMenuItem.setName("UpdateSQLMenuItem");
                ivjUpdateSQLMenuItem.setText("Update SQL");
                ivjUpdateSQLMenuItem.setBackground(java.awt.SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (Throwable exception) {
                // user code begin {2}
                // user code end
                handleException(exception);
            }
        }
        return ivjUpdateSQLMenuItem;
    }

    /**
     * Return the WorkspaceBook property value.
     * @return JTabbedPane
     */
    public

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    JTabbedPane getWorkspaceBook() {
        if (ivjWorkspaceBook == null) {
            try {
                ivjWorkspaceBook = new JTabbedPane();
                ivjWorkspaceBook.setName("WorkspaceBook");
                ivjWorkspaceBook.setAutoscrolls(false);
                ivjWorkspaceBook.setBackground(java.awt.SystemColor.control);
                ivjWorkspaceBook.setMaximumSize(new java.awt.Dimension(0, 0));
                ivjWorkspaceBook.setMinimumSize(new java.awt.Dimension(1, 1));
                ivjWorkspaceBook.insertTab("Login", null, getLoginPage(), null, 
                                           0);
                ivjWorkspaceBook.insertTab("Cache", null, getCachePage(), null, 
                                           1);
                ivjWorkspaceBook.insertTab("Query", null, getQueryPage(), null, 
                                           2);
                ivjWorkspaceBook.insertTab("SQL", null, getSQLPage(), null, 3);
                ivjWorkspaceBook.insertTab("Java", null, getJavaPage(), null, 
                                           4);
            } catch (Throwable exception) {
                handleException(exception);
            }
        }
        return ivjWorkspaceBook;
    }

    /**
     * Called whenever the part throws an exception.
     * @param exception Throwable
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
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    void initConnections() throws Exception {
        // user code begin {1}
        // user code end
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
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    void initialize() {
        try {
            // user code begin {1}
            setSession(new Project(new DatabaseLogin()).createDatabaseSession());
            // user code end
            setName("SessionInspectorPanel");
            setLayout(new java.awt.GridBagLayout());
            setBackground(java.awt.SystemColor.control);
            setSize(849, 588);

            java.awt.GridBagConstraints constraintsLogSplitter = 
                new java.awt.GridBagConstraints();
            constraintsLogSplitter.gridx = 0;
            constraintsLogSplitter.gridy = 0;
            constraintsLogSplitter.fill = java.awt.GridBagConstraints.BOTH;
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
        ClassInfo classInfo = ((ClassInfo)getClassList().getSelectedValue());
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
     * @param e java.awt.event.ItemEvent
     */
    public

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    void itemStateChanged(java.awt.event.ItemEvent e) {
        // user code begin {1}
        // user code end
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
            DatabaseLogin fileLogin = (DatabaseLogin)stream.readObject();
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
            // Ignore file access errors if file access is not allowed then the login will not be stored.
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
        dialog.setDialogTitle("Select Mapping Workbench project deployment XML file");
        javax.swing.filechooser.FileFilter filer = 
            new javax.swing.filechooser.FileFilter() {
                public boolean accept(File file) {
                    return (file.getName().indexOf(".xml") != -1) || 
                        file.isDirectory();
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
            MessageDialog.displayMessage("Invalid project file selected.", 
                                         this);
            return;
        }

        showBusyCursor();
        try {
            String path = dialog.getSelectedFile().getPath();
            Project project = XMLProjectReader.read(path);
            if (getSession().isConnected()) {
                ((DatabaseSession)getSession()).addDescriptors(project);
            } else {
                ((AbstractSession)getSession()).setProject(project);
                getLoginEditorPanel().setLogin((DatabaseLogin)project.getDatasourceLogin());
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
            // Ignore file access errors if file access is not allowed then the login will not be stored.
        }

        getSQLText().setText(sql);
    }

    public void loggingChanged() {
        int logLevel = getLogLevelChoice().getSelectedIndex();
        getSession().setLogLevel(logLevel);
    }

    public void login() {
        showBusyCursor();
        try {
            storeLoginToFile();

            updateLogin();
            
            ((DatabaseSession)getSession()).setLogin(getLoginEditorPanel().getLogin());
            ((DatabaseSession)getSession()).login();

            resetButtons();
        } finally {
            showNormalCursor();
        }
    }

    public void logout() {
        showBusyCursor();
        try {
            ((DatabaseSession)getSession()).logout();
            resetButtons();
            storeSQLToFile();
        } finally {
            showNormalCursor();
        }
    }

    public void logProfileChanged() {
        if (((org.eclipse.persistence.sessions.Session)getSession()).isInProfile()) {
            ((PerformanceProfiler)((org.eclipse.persistence.sessions.Session)getSession()).getProfiler()).setShouldLogProfile(getLogProfileCheckbox().isSelected());
        }
    }

    /**
     * Method to handle events for the MouseListener interface.
     * @param e java.awt.event.MouseEvent
     */
    public

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    void mouseClicked(java.awt.event.MouseEvent e) {
        // user code begin {1}
        // user code end
        // user code begin {2}
        // user code end
    }

    /**
     * Method to handle events for the MouseListener interface.
     * @param e java.awt.event.MouseEvent
     */
    public

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    void mouseEntered(java.awt.event.MouseEvent e) {
        // user code begin {1}
        // user code end
        // user code begin {2}
        // user code end
    }

    /**
     * Method to handle events for the MouseListener interface.
     * @param e java.awt.event.MouseEvent
     */
    public

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    void mouseExited(java.awt.event.MouseEvent e) {
        // user code begin {1}
        // user code end
        // user code begin {2}
        // user code end
    }

    /**
     * Method to handle events for the MouseListener interface.
     * @param e java.awt.event.MouseEvent
     */
    public

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    void mousePressed(java.awt.event.MouseEvent e) {
        // user code begin {1}
        // user code end
        // user code begin {2}
        // user code end
    }

    /**
     * Method to handle events for the MouseListener interface.
     * @param e java.awt.event.MouseEvent
     */
    public

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    void mouseReleased(java.awt.event.MouseEvent e) {
        // user code begin {1}
        // user code end
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

        // user code begin {2}
        // user code end
    }

    public void pasteSQL() {
        getSQLText().paste();
    }

    public void profileChanged() {
        if (((org.eclipse.persistence.sessions.Session)getSession()).isInProfile()) {
            getSession().clearProfile();
        }
        if (getProfileCheckbox().isSelected()) {
            getSession().setProfiler(new org.eclipse.persistence.tools.profiler.PerformanceProfiler(getSession(), 
                                                                                           getLogProfileCheckbox().isSelected()));
            getBrowseProfileButton().setEnabled(true);
            getLogProfileCheckbox().setEnabled(true);
        } else {
            getBrowseProfileButton().setEnabled(false);
            getLogProfileCheckbox().setEnabled(false);
        }
    }

    /**
     * Method to handle events for the PropertyChangeListener interface.
     * @param evt java.beans.PropertyChangeEvent
     */
    public

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    void propertyChange(java.beans.PropertyChangeEvent evt) {
        // user code begin {1}
        // user code end
        if ((evt.getSource() == getBrowseProfileMenuItem()) && 
            (evt.getPropertyName().equals("enabled"))) {
            connPtoP1SetTarget();
        }

        // user code begin {2}
        // user code end
    }

    public void resetButtons() {
        boolean isConnected = 
            (getSession() == null) || (getSession().isConnected());
        getLoginButton().setEnabled(!isConnected);
        getLogoutButton().setEnabled(isConnected);
        getQueryButton().setEnabled(isConnected);
        getSQLExecuteButton().setEnabled(isConnected);
        getSelectButton().setEnabled(isConnected);
    }

    public void resetCache() {
        Vector cacheResults = new Vector();
        setCacheResults(cacheResults);
        ClassInfo info = (ClassInfo)getClassList().getSelectedValue();
        DefaultTableModel model = new DefaultTableModel();
        if (info == null) {
            getCacheTable().setModel(model);
            getCacheTable().repaint();
            return;
        }

        IdentityMap map = 
            ((AbstractSession)getSession()).getIdentityMapAccessorInstance().getIdentityMap(info.descriptor.getJavaClass());
        for (Enumeration cacheEnum = map.keys(); cacheEnum.hasMoreElements(); 
        ) {
            CacheKey key = (CacheKey)cacheEnum.nextElement();
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
        for (Enumeration cacheEnumeration = cacheResults.elements(); 
             cacheEnumeration.hasMoreElements(); ) {
            CacheKey key = (CacheKey)cacheEnumeration.nextElement();
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
        ClassInfo[] classes = 
            new ClassInfo[((org.eclipse.persistence.sessions.Session)getSession()).getDescriptors().size()];
        int index = 0;
        for (Iterator iterator = 
             ((org.eclipse.persistence.sessions.Session)getSession()).getDescriptors().values().iterator(); 
             iterator.hasNext(); ) {
            classes[index] = 
                    new ClassInfo((ClassDescriptor)iterator.next(), useFullNames);
            index = index + 1;
        }
        TOPSort.quicksort(classes, new ClassInfoCompare());

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
        Writer log = 
            new OutputStreamWriter(new TextAreaOutputStream(getLogText(), 
                                                            autoscroll));
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
        Object firstObject = resultObjects.firstElement();

        String[] columns = new String[descriptor.getMappings().size()];
        for (int index = 0; index < descriptor.getMappings().size(); index++) {
            columns[index] = 
                    ((DatabaseMapping)descriptor.getMappings().elementAt(index)).getAttributeName();
        }

        model.setColumnIdentifiers(columns);
        for (Enumeration objectsEnumeration = resultObjects.elements(); 
             objectsEnumeration.hasMoreElements(); ) {
            Object object = objectsEnumeration.nextElement();
            String[] values = new String[descriptor.getMappings().size()];
            for (int index = 0; index < descriptor.getMappings().size(); 
                 index++) {
                DatabaseMapping mapping = 
                    (DatabaseMapping)descriptor.getMappings().elementAt(index);
                values[index] = 
                        String.valueOf(mapping.getAttributeValueFromObject(object));
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
        DatabaseRecord firstRow = (DatabaseRecord)resultRows.firstElement();
        String[] columns = new String[firstRow.getFields().size()];
        for (int index = 0; index < firstRow.getFields().size(); index++) {
            columns[index] = 
                    ((DatabaseField)firstRow.getFields().elementAt(index)).getName();
        }
        model.setColumnIdentifiers(columns);
        for (Enumeration rowsEnumeration = resultRows.elements(); 
             rowsEnumeration.hasMoreElements(); ) {
            DatabaseRecord row = (DatabaseRecord)rowsEnumeration.nextElement();
            String[] values = new String[row.getValues().size()];
            for (int index = 0; index < row.getValues().size(); index++) {
                values[index] = 
                        String.valueOf(row.getValues().elementAt(index));
            }
            model.addRow(values);
        }

        getResultsTable().setModel(model);
        getResultsTable().repaint();
    }

    public void setResults(Vector results) {
        this.results = results;
    }

    /**
     * Sets the session property (org.eclipse.persistence.sessions.Session) value.
     * @param session The new value for the property.
     * @see #getSession
     */
    public void setSession(Session session) {
        Session oldValue = fieldSession;
        fieldSession = session;
        firePropertyChange("session", oldValue, session);

        getSession().setLogLevel(getLogLevelChoice().getSelectedIndex());

        // Do not set if not a DatabaseLogin, i.e. EIS login.
        if (getSession().getDatasourceLogin() instanceof DatabaseLogin) {
            getLoginEditorPanel().setLogin((DatabaseLogin)((org.eclipse.persistence.sessions.Session)getSession()).getDatasourceLogin());
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
            parent.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
            parent = parent.getParent();
        }
    }

    /**
     * Change from the busy cursor.
     */
    public void showNormalCursor() {
        Container parent = this;
        while (parent != null) {
            parent.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.DEFAULT_CURSOR));
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
            // Ignore file access errors if file access is not allowed then the login will not be stored.
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
            // Ignore file access errors if file access is not allowed then the login will not be stored.
        }
    }

    public void templateSQLDelete() {
        getSQLText().setText(getSQLText().getText() + 
                             "\n\nDelete from <table> where <exp>");
    }

    public void templateSQLInsert() {
        getSQLText().setText(getSQLText().getText() + 
                             "\n\nInsert into <table> (<fields>) values (<values>)");
    }

    public void templateSQLSelect() {
        getSQLText().setText(getSQLText().getText() + 
                             "\n\nSelect <fields> from <tables> where <exp> order by <exp> group by <exp>");
    }

    public void templateSQLUpdate() {
        getSQLText().setText(getSQLText().getText() + 
                             "\n\nUpdate <table> set <<field> = <value>> where <exp>");
    }

    /**
     * Method to handle events for the ListSelectionListener interface.
     * @param e event.ListSelectionEvent
     */
    public void valueChanged(ListSelectionEvent e) {
        if (e.getSource() == getClassList()) {
            connEtoC3(e);
        }
    }
    
    private void updateLogin() {
        if(shouldUseJndiConnector && getLoginEditorPanel().getLogin().getPlatform().isOracle()) {
            try{
                Class connectionPoolHelperClass = Class.forName("org.eclipse.persistence.tools.sessionconsole.OracleConnectionPoolHelper");
                Class[] params = new Class[]{java.lang.String.class};
                java.lang.reflect.Method method = connectionPoolHelperClass.getDeclaredMethod("getOracleDataSource", params);
                Object[] args = new Object[]{getLoginEditorPanel().getLogin().getConnectionString()};
                javax.sql.DataSource datasource = (javax.sql.DataSource)method.invoke(null, args);
                if (datasource == null){
                    return;
                }
                org.eclipse.persistence.sessions.JNDIConnector jndiConnector = new org.eclipse.persistence.sessions.JNDIConnector();
                jndiConnector.setDataSource(datasource);
                getLoginEditorPanel().getLogin().setConnector(jndiConnector);
                getLoginEditorPanel().getLogin().setUsesExternalConnectionPooling(true);
            } catch (Exception e){
                System.out.println("Got exception while updating Session console login.  This usually results from running on a system not compiled with " +
                        " Oracle dependancies.  If you are not running on Oracle, this is not a problem.");
                handleException(e);
            }
        }
    }
}
