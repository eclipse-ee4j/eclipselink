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

import java.net.URL;

import javax.swing.*;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.tools.beans.*;

/**
 * The session console allows for a session to be visually inspected.
 */
public class SessionConsole extends JFrame {
    private JMenuItem ivjBrowseProfileMenuItem = null;
    private JMenu ivjCacheMenu = null;
    private JMenuItem ivjClearCacheMenuItem = null;
    private JMenuItem ivjClearDescriptorsMenuItem = null;
    private JMenuItem ivjClearLogMenuItem = null;
    private JMenuItem ivjClearResultsMenuItem = null;
    private JMenuItem ivjClearSQLMenuItem = null;
    private JMenuItem ivjCloseMenuItem = null;
    private JMenuItem ivjCopySQLMenuItem = null;
    private JMenuItem ivjCutSQLMenuItem = null;
    private JMenuItem ivjDeleteSQLMenuItem = null;
    private JMenu ivjDescriptorsMenu = null;
    private JMenuItem ivjExecuteQueryMenuItem = null;
    private JMenuItem ivjExitMenuItem = null;
    private JMenu ivjFileMenu = null;
    private JMenuItem ivjInsertSQLMenuItem = null;
    private JMenuItem ivjInspectCacheMenuItem = null;
    private JMenuItem ivjInspectDescriptorMenuItem = null;
    private JMenuItem ivjInspectResultMenuItem = null;
    private JMenuItem ivjInspectSessionMenuItem1 = null;
    private JSeparator ivjJSeparator1 = null;
    private JSeparator ivjJSeparator3 = null;
    private JSeparator ivjJSeparator4 = null;
    private JSeparator ivjJSeparator5 = null;
    private JSeparator ivjJSeparator6 = null;
    private JSeparator ivjJSeparator7 = null;
    private JMenuItem ivjLoadProjectMenuItem = null;
    private JMenu ivjLoginMenu = null;
    private JMenuItem ivjLoginMenuItem = null;
    private JMenu ivjLogMenu = null;
    private JMenuItem ivjLogoutMenuItem = null;
    private JCheckBoxMenuItem ivjLogProfileMenuItem = null;
    private JCheckBoxMenuItem ivjLogProfileMenuItem1 = null;
    private JCheckBoxMenuItem ivjLogSQLMenuItem = null;
    private JPanel ivjMainPanel = null;
    private JMenuItem ivjPasteSQLMenuItem = null;
    private JMenu ivjQueryMenu = null;
    private JMenuItem ivjResetDescriptorsMenuItem = null;
    private JMenu ivjResultsMenu = null;
    private JMenuItem ivjSelectSQLMenuItem = null;
    private JMenuBar ivjSessionInspectorFrameJMenuBar = null;
    private SessionConsolePanel ivjSessionInspectorPanel = null;
    private JMenu ivjSQLMenu = null;
    private JMenuItem ivjSQLSelectMenuItem = null;
    private JMenu ivjSQLTemplateMenu = null;
    private JMenuItem ivjSQLUpdateMenuItem = null;
    private JMenuItem ivjUpdateSQLMenuItem = null;
    private boolean shouldCallSystemExit = true;
    IvjEventHandler ivjEventHandler = new IvjEventHandler();

    /**
     * Constructor
     */
    public SessionConsole() {
        super();
        initialize();
    }

    /**
     * SessionInspectorFrame constructor comment.
     * @param title java.lang.String
     */
    public SessionConsole(String title) {
        super(title);
    }

    public void browseProfile() {
        getSessionInspectorPanel().browseProfile();
    }

    /**
     * PUBLIC:
     * Open a browser on the session.
     */
    public static void browseSession(Session session) {
        browseSession(session, true);
    }

    /**
     * PUBLIC:
     * Open a browser on the session.
     */
    public static void browseSession(Session session, 
                                     boolean shouldCallSystemExit) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SessionConsole aSessionInspectorFrame;
            aSessionInspectorFrame = new SessionConsole();
            aSessionInspectorFrame.shouldCallSystemExit = shouldCallSystemExit;
            aSessionInspectorFrame.setVisible(true);
            aSessionInspectorFrame.setSession(session);
        } catch (Throwable exception) {
            System.err.println("Exception occurred in main() of javax.swing.JFrame");
            exception.printStackTrace(System.out);
        }
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

    public void clearCache() {
        getSessionInspectorPanel().clearCache();
    }

    public void clearDescriptors() {
        getSessionInspectorPanel().clearDescriptors();
    }

    public void clearLog() {
        getSessionInspectorPanel().clearLog();
    }

    public void clearResults() {
        getSessionInspectorPanel().clearResults();
    }

    public void clearSQL() {
        getSessionInspectorPanel().clearSQL();
    }

    /**
     * connEtoC1:  (LoadProjectMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> SessionInspectorFrame.loadProject()V)
     * @param arg1 java.awt.event.ActionEvent
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    void connEtoC1(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.loadProject();
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC10:  (ClearSQLMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> SessionInspectorFrame.clearSQL()V)
     * @param arg1 java.awt.event.ActionEvent
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    void connEtoC10(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.clearSQL();
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC11:  (LoginMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> SessionInspectorFrame.login()V)
     * @param arg1 java.awt.event.ActionEvent
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    void connEtoC11(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.login();
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC12:  (LogoutMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> SessionInspectorFrame.logout()V)
     * @param arg1 java.awt.event.ActionEvent
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    void connEtoC12(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.logout();
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC13:  (InspectSessionMenuItem1.action.actionPerformed(java.awt.event.ActionEvent) --> SessionInspectorFrame.inspectSession()V)
     * @param arg1 java.awt.event.ActionEvent
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    void connEtoC13(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.inspectSession();
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC14:  (ExecuteQueryMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> SessionInspectorFrame.executeQuery()V)
     * @param arg1 java.awt.event.ActionEvent
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    void connEtoC14(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.executeQuery();
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC15:  (CutSQLMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> SessionInspectorFrame.cutSQL()V)
     * @param arg1 java.awt.event.ActionEvent
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    void connEtoC15(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.cutSQL();
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC16:  (CopySQLMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> SessionInspectorFrame.copySQL()V)
     * @param arg1 java.awt.event.ActionEvent
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    void connEtoC16(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.copySQL();
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC17:  (PasteSQLMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> SessionInspectorFrame.pasteSQL()V)
     * @param arg1 java.awt.event.ActionEvent
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    void connEtoC17(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.pasteSQL();
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC18:  (ClearLogMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> SessionInspectorFrame.clearLog()V)
     * @param arg1 java.awt.event.ActionEvent
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    void connEtoC18(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.clearLog();
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC19:  (ClearResultsMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> SessionInspectorFrame.clearResults()V)
     * @param arg1 java.awt.event.ActionEvent
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    void connEtoC19(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.clearResults();
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC2:  (ExitMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> SessionInspectorFrame.exit()V)
     * @param arg1 java.awt.event.ActionEvent
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    void connEtoC2(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.exit();
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC20:  (InspectResultMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> SessionInspectorFrame.inspectResult()V)
     * @param arg1 java.awt.event.ActionEvent
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    void connEtoC20(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.inspectResult();
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC21:  (InsertSQLMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> SessionInspectorFrame.templateSQLInsert()V)
     * @param arg1 java.awt.event.ActionEvent
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    void connEtoC21(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.templateSQLInsert();
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC22:  (UpdateSQLMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> SessionInspectorFrame.templateSQLUpdate()V)
     * @param arg1 java.awt.event.ActionEvent
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    void connEtoC22(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.templateSQLUpdate();
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC23:  (DeleteSQLMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> SessionInspectorFrame.templateSQLDelete()V)
     * @param arg1 java.awt.event.ActionEvent
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    void connEtoC23(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.templateSQLDelete();
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC24:  (SelectSQLMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> SessionInspectorFrame.templateSQLSelect()V)
     * @param arg1 java.awt.event.ActionEvent
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    void connEtoC24(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.templateSQLSelect();
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC25:  (BrowseProfileMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> SessionInspectorFrame.browseProfile()V)
     * @param arg1 java.awt.event.ActionEvent
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    void connEtoC25(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.browseProfile();
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC28:  (SessionInspectorFrame.window.windowClosing(java.awt.event.WindowEvent) --> SessionConsole.exit()V)
     * @param arg1 java.awt.event.WindowEvent
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    void connEtoC28(java.awt.event.WindowEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.exit();
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC3:  (ResetDescriptorsMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> SessionInspectorFrame.resetDescriptors()V)
     * @param arg1 java.awt.event.ActionEvent
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    void connEtoC3(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.resetDescriptors();
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC4:  (ClearDescriptorsMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> SessionInspectorFrame.clearDescriptors()V)
     * @param arg1 java.awt.event.ActionEvent
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    void connEtoC4(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.clearDescriptors();
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC5:  (InspectDescriptorMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> SessionInspectorFrame.inspectDescriptor()V)
     * @param arg1 java.awt.event.ActionEvent
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    void connEtoC5(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.inspectDescriptor();
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC6:  (ClearCacheMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> SessionInspectorFrame.clearCache()V)
     * @param arg1 java.awt.event.ActionEvent
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    void connEtoC6(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.clearCache();
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC7:  (InspectCacheMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> SessionInspectorFrame.inspectCache()V)
     * @param arg1 java.awt.event.ActionEvent
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    void connEtoC7(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.inspectCache();
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC8:  (SQLUpdateMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> SessionInspectorFrame.executeSQLUpdate()V)
     * @param arg1 java.awt.event.ActionEvent
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    void connEtoC8(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.executeSQLUpdate();
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC9:  (SQLSelectMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> SessionInspectorFrame.executeSQLSelect()V)
     * @param arg1 java.awt.event.ActionEvent
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    void connEtoC9(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.executeSQLSelect();
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoM1:  (CloseMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> SessionInspectorFrame.dispose()V)
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

    public void copySQL() {
        getSessionInspectorPanel().copySQL();
    }

    public void createTables() {
        getSessionInspectorPanel().createTables();
    }

    public void cutSQL() {
        getSessionInspectorPanel().cutSQL();
    }

    public void executeQuery() {
        getSessionInspectorPanel().executeQuery();
    }

    public void executeSQLSelect() {
        getSessionInspectorPanel().selectSQL();
    }

    public void executeSQLUpdate() {
        getSessionInspectorPanel().executeSQL();
    }

    public void exit() {
        if (shouldCallSystemExit) {
            System.exit(0);
        }
    }

    /**
     * Return the BrowseProfileMenuItem property value.
     * @return javax.swing.JMenuItem
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JMenuItem getBrowseProfileMenuItem() {
        if (ivjBrowseProfileMenuItem == null) {
            try {
                ivjBrowseProfileMenuItem = new javax.swing.JMenuItem();
                ivjBrowseProfileMenuItem.setName("BrowseProfileMenuItem");
                ivjBrowseProfileMenuItem.setText("Browse Profile");
                ivjBrowseProfileMenuItem.setBackground(java.awt.SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjBrowseProfileMenuItem;
    }

    /**
     * Return the CacheMenu property value.
     * @return javax.swing.JMenu
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JMenu getCacheMenu() {
        if (ivjCacheMenu == null) {
            try {
                ivjCacheMenu = new javax.swing.JMenu();
                ivjCacheMenu.setName("CacheMenu");
                ivjCacheMenu.setText("Cache");
                ivjCacheMenu.setBackground(java.awt.SystemColor.menu);
                ivjCacheMenu.add(getClearCacheMenuItem());
                //			ivjCacheMenu.add(getInspectCacheMenuItem());
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjCacheMenu;
    }

    /**
     * Return the ClearCacheMenuItem property value.
     * @return javax.swing.JMenuItem
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JMenuItem getClearCacheMenuItem() {
        if (ivjClearCacheMenuItem == null) {
            try {
                ivjClearCacheMenuItem = new javax.swing.JMenuItem();
                ivjClearCacheMenuItem.setName("ClearCacheMenuItem");
                ivjClearCacheMenuItem.setText("Clear Cache");
                ivjClearCacheMenuItem.setBackground(java.awt.SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjClearCacheMenuItem;
    }

    /**
     * Return the ClearDescriptorsMenuItem property value.
     * @return javax.swing.JMenuItem
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JMenuItem getClearDescriptorsMenuItem() {
        if (ivjClearDescriptorsMenuItem == null) {
            try {
                ivjClearDescriptorsMenuItem = new javax.swing.JMenuItem();
                ivjClearDescriptorsMenuItem.setName("ClearDescriptorsMenuItem");
                ivjClearDescriptorsMenuItem.setText("Clear Descriptors");
                ivjClearDescriptorsMenuItem.setBackground(java.awt.SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjClearDescriptorsMenuItem;
    }

    /**
     * Return the ClearLogMenuItem property value.
     * @return javax.swing.JMenuItem
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JMenuItem getClearLogMenuItem() {
        if (ivjClearLogMenuItem == null) {
            try {
                ivjClearLogMenuItem = new javax.swing.JMenuItem();
                ivjClearLogMenuItem.setName("ClearLogMenuItem");
                ivjClearLogMenuItem.setText("Clear Log");
                ivjClearLogMenuItem.setBackground(java.awt.SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjClearLogMenuItem;
    }

    /**
     * Return the ClearResultsMenuItem property value.
     * @return javax.swing.JMenuItem
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JMenuItem getClearResultsMenuItem() {
        if (ivjClearResultsMenuItem == null) {
            try {
                ivjClearResultsMenuItem = new javax.swing.JMenuItem();
                ivjClearResultsMenuItem.setName("ClearResultsMenuItem");
                ivjClearResultsMenuItem.setText("Clear Results");
                ivjClearResultsMenuItem.setBackground(java.awt.SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjClearResultsMenuItem;
    }

    /**
     * Return the ClearSQLMenuItem property value.
     * @return javax.swing.JMenuItem
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JMenuItem getClearSQLMenuItem() {
        if (ivjClearSQLMenuItem == null) {
            try {
                ivjClearSQLMenuItem = new javax.swing.JMenuItem();
                ivjClearSQLMenuItem.setName("ClearSQLMenuItem");
                ivjClearSQLMenuItem.setText("Clear SQL");
                ivjClearSQLMenuItem.setBackground(java.awt.SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjClearSQLMenuItem;
    }

    /**
     * Return the CloseMenuItem property value.
     * @return javax.swing.JMenuItem
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JMenuItem getCloseMenuItem() {
        if (ivjCloseMenuItem == null) {
            try {
                ivjCloseMenuItem = new javax.swing.JMenuItem();
                ivjCloseMenuItem.setName("CloseMenuItem");
                ivjCloseMenuItem.setText("Close");
                ivjCloseMenuItem.setBackground(java.awt.SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjCloseMenuItem;
    }

    /**
     * Return the CopySQLMenuItem property value.
     * @return javax.swing.JMenuItem
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JMenuItem getCopySQLMenuItem() {
        if (ivjCopySQLMenuItem == null) {
            try {
                ivjCopySQLMenuItem = new javax.swing.JMenuItem();
                ivjCopySQLMenuItem.setName("CopySQLMenuItem");
                ivjCopySQLMenuItem.setText("Copy");
                ivjCopySQLMenuItem.setBackground(java.awt.SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjCopySQLMenuItem;
    }

    /**
     * Return the CutSQLMenuItem property value.
     * @return javax.swing.JMenuItem
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JMenuItem getCutSQLMenuItem() {
        if (ivjCutSQLMenuItem == null) {
            try {
                ivjCutSQLMenuItem = new javax.swing.JMenuItem();
                ivjCutSQLMenuItem.setName("CutSQLMenuItem");
                ivjCutSQLMenuItem.setText("Cut");
                ivjCutSQLMenuItem.setBackground(java.awt.SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjCutSQLMenuItem;
    }

    /**
     * Return the DeleteSQLMenuItem property value.
     * @return javax.swing.JMenuItem
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JMenuItem getDeleteSQLMenuItem() {
        if (ivjDeleteSQLMenuItem == null) {
            try {
                ivjDeleteSQLMenuItem = new javax.swing.JMenuItem();
                ivjDeleteSQLMenuItem.setName("DeleteSQLMenuItem");
                ivjDeleteSQLMenuItem.setText("Delete SQL");
                ivjDeleteSQLMenuItem.setBackground(java.awt.SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjDeleteSQLMenuItem;
    }

    /**
     * Return the DescriptorsMenu property value.
     * @return javax.swing.JMenu
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JMenu getDescriptorsMenu() {
        if (ivjDescriptorsMenu == null) {
            try {
                ivjDescriptorsMenu = new javax.swing.JMenu();
                ivjDescriptorsMenu.setName("DescriptorsMenu");
                ivjDescriptorsMenu.setText("Descriptors");
                ivjDescriptorsMenu.setBackground(java.awt.SystemColor.menu);
                ivjDescriptorsMenu.add(getResetDescriptorsMenuItem());
                ivjDescriptorsMenu.add(getClearDescriptorsMenuItem());
                //			ivjDescriptorsMenu.add(getInspectDescriptorMenuItem());
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjDescriptorsMenu;
    }

    /**
     * Return the ExecuteQueryMenuItem property value.
     * @return javax.swing.JMenuItem
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JMenuItem getExecuteQueryMenuItem() {
        if (ivjExecuteQueryMenuItem == null) {
            try {
                ivjExecuteQueryMenuItem = new javax.swing.JMenuItem();
                ivjExecuteQueryMenuItem.setName("ExecuteQueryMenuItem");
                ivjExecuteQueryMenuItem.setText("Execute Query");
                ivjExecuteQueryMenuItem.setBackground(java.awt.SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjExecuteQueryMenuItem;
    }

    /**
     * Return the JMenuItem1 property value.
     * @return javax.swing.JMenuItem
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JMenuItem getExitMenuItem() {
        if (ivjExitMenuItem == null) {
            try {
                ivjExitMenuItem = new javax.swing.JMenuItem();
                ivjExitMenuItem.setName("ExitMenuItem");
                ivjExitMenuItem.setText("Exit");
                ivjExitMenuItem.setBackground(java.awt.SystemColor.menu);
                ivjExitMenuItem.setActionCommand("ExitMenuItem");
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
     * Return the JMenu1 property value.
     * @return javax.swing.JMenu
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JMenu getFileMenu() {
        if (ivjFileMenu == null) {
            try {
                ivjFileMenu = new javax.swing.JMenu();
                ivjFileMenu.setName("FileMenu");
                ivjFileMenu.setText("File");
                ivjFileMenu.setBackground(java.awt.SystemColor.menu);
                ivjFileMenu.add(getLoadProjectMenuItem());
                ivjFileMenu.add(getJSeparator3());
                ivjFileMenu.add(getCloseMenuItem());
                ivjFileMenu.add(getJSeparator1());
                ivjFileMenu.add(getExitMenuItem());
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjFileMenu;
    }

    /**
     * Return the InsertSQLMenuItem property value.
     * @return javax.swing.JMenuItem
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JMenuItem getInsertSQLMenuItem() {
        if (ivjInsertSQLMenuItem == null) {
            try {
                ivjInsertSQLMenuItem = new javax.swing.JMenuItem();
                ivjInsertSQLMenuItem.setName("InsertSQLMenuItem");
                ivjInsertSQLMenuItem.setText("Insert SQL");
                ivjInsertSQLMenuItem.setBackground(java.awt.SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjInsertSQLMenuItem;
    }

    /**
     * Return the InspectCacheMenuItem property value.
     * @return javax.swing.JMenuItem
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JMenuItem getInspectCacheMenuItem() {
        if (ivjInspectCacheMenuItem == null) {
            try {
                ivjInspectCacheMenuItem = new javax.swing.JMenuItem();
                ivjInspectCacheMenuItem.setName("InspectCacheMenuItem");
                ivjInspectCacheMenuItem.setText("Inspect Cache (VA)");
                ivjInspectCacheMenuItem.setBackground(java.awt.SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjInspectCacheMenuItem;
    }

    /**
     * Return the InspectDescriptorMenuItem property value.
     * @return javax.swing.JMenuItem
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JMenuItem getInspectDescriptorMenuItem() {
        if (ivjInspectDescriptorMenuItem == null) {
            try {
                ivjInspectDescriptorMenuItem = new javax.swing.JMenuItem();
                ivjInspectDescriptorMenuItem.setName("InspectDescriptorMenuItem");
                ivjInspectDescriptorMenuItem.setText("Inspect Descriptor (VA)");
                ivjInspectDescriptorMenuItem.setBackground(java.awt.SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjInspectDescriptorMenuItem;
    }

    /**
     * Return the InspectResultMenuItem property value.
     * @return javax.swing.JMenuItem
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JMenuItem getInspectResultMenuItem() {
        if (ivjInspectResultMenuItem == null) {
            try {
                ivjInspectResultMenuItem = new javax.swing.JMenuItem();
                ivjInspectResultMenuItem.setName("InspectResultMenuItem");
                ivjInspectResultMenuItem.setText("Inspect Result (VA)");
                ivjInspectResultMenuItem.setBackground(java.awt.SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjInspectResultMenuItem;
    }

    /**
     * Return the InspectSessionMenuItem1 property value.
     * @return javax.swing.JMenuItem
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JMenuItem getInspectSessionMenuItem1() {
        if (ivjInspectSessionMenuItem1 == null) {
            try {
                ivjInspectSessionMenuItem1 = new javax.swing.JMenuItem();
                ivjInspectSessionMenuItem1.setName("InspectSessionMenuItem1");
                ivjInspectSessionMenuItem1.setText("Inspect Session (VA)");
                ivjInspectSessionMenuItem1.setBackground(java.awt.SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjInspectSessionMenuItem1;
    }

    /**
     * Return the JSeparator1 property value.
     * @return javax.swing.JSeparator
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JSeparator getJSeparator1() {
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
     * Return the JSeparator3 property value.
     * @return javax.swing.JSeparator
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JSeparator getJSeparator3() {
        if (ivjJSeparator3 == null) {
            try {
                ivjJSeparator3 = new javax.swing.JSeparator();
                ivjJSeparator3.setName("JSeparator3");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJSeparator3;
    }

    /**
     * Return the JSeparator4 property value.
     * @return javax.swing.JSeparator
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JSeparator getJSeparator4() {
        if (ivjJSeparator4 == null) {
            try {
                ivjJSeparator4 = new javax.swing.JSeparator();
                ivjJSeparator4.setName("JSeparator4");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJSeparator4;
    }

    /**
     * Return the JSeparator5 property value.
     * @return javax.swing.JSeparator
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JSeparator getJSeparator5() {
        if (ivjJSeparator5 == null) {
            try {
                ivjJSeparator5 = new javax.swing.JSeparator();
                ivjJSeparator5.setName("JSeparator5");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJSeparator5;
    }

    /**
     * Return the JSeparator6 property value.
     * @return javax.swing.JSeparator
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JSeparator getJSeparator6() {
        if (ivjJSeparator6 == null) {
            try {
                ivjJSeparator6 = new javax.swing.JSeparator();
                ivjJSeparator6.setName("JSeparator6");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJSeparator6;
    }

    /**
     * Return the JSeparator7 property value.
     * @return javax.swing.JSeparator
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JSeparator getJSeparator7() {
        if (ivjJSeparator7 == null) {
            try {
                ivjJSeparator7 = new javax.swing.JSeparator();
                ivjJSeparator7.setName("JSeparator7");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJSeparator7;
    }

    /**
     * Return the LoadProjectMenuItem property value.
     * @return javax.swing.JMenuItem
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JMenuItem getLoadProjectMenuItem() {
        if (ivjLoadProjectMenuItem == null) {
            try {
                ivjLoadProjectMenuItem = new javax.swing.JMenuItem();
                ivjLoadProjectMenuItem.setName("LoadProjectMenuItem");
                ivjLoadProjectMenuItem.setText("Load Project...");
                ivjLoadProjectMenuItem.setBackground(java.awt.SystemColor.menu);
                ivjLoadProjectMenuItem.setForeground(java.awt.Color.black);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjLoadProjectMenuItem;
    }

    /**
     * Return the LoginMenu property value.
     * @return javax.swing.JMenu
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JMenu getLoginMenu() {
        if (ivjLoginMenu == null) {
            try {
                ivjLoginMenu = new javax.swing.JMenu();
                ivjLoginMenu.setName("LoginMenu");
                ivjLoginMenu.setText("Login");
                ivjLoginMenu.setBackground(java.awt.SystemColor.menu);
                ivjLoginMenu.add(getLoginMenuItem());
                ivjLoginMenu.add(getLogoutMenuItem());
                //			ivjLoginMenu.add(getInspectSessionMenuItem1());
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjLoginMenu;
    }

    /**
     * Return the LoginMenuItem property value.
     * @return javax.swing.JMenuItem
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JMenuItem getLoginMenuItem() {
        if (ivjLoginMenuItem == null) {
            try {
                ivjLoginMenuItem = new javax.swing.JMenuItem();
                ivjLoginMenuItem.setName("LoginMenuItem");
                ivjLoginMenuItem.setText("Login");
                ivjLoginMenuItem.setBackground(java.awt.SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjLoginMenuItem;
    }

    /**
     * Return the LogMenu property value.
     * @return javax.swing.JMenu
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JMenu getLogMenu() {
        if (ivjLogMenu == null) {
            try {
                ivjLogMenu = new javax.swing.JMenu();
                ivjLogMenu.setName("LogMenu");
                ivjLogMenu.setText("Log");
                ivjLogMenu.setBackground(java.awt.SystemColor.menu);
                ivjLogMenu.add(getClearLogMenuItem());
                ivjLogMenu.add(getLogSQLMenuItem());
                ivjLogMenu.add(getJSeparator7());
                ivjLogMenu.add(getLogProfileMenuItem1());
                ivjLogMenu.add(getLogProfileMenuItem());
                ivjLogMenu.add(getBrowseProfileMenuItem());
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjLogMenu;
    }

    /**
     * Return the LogoutMenuItem property value.
     * @return javax.swing.JMenuItem
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JMenuItem getLogoutMenuItem() {
        if (ivjLogoutMenuItem == null) {
            try {
                ivjLogoutMenuItem = new javax.swing.JMenuItem();
                ivjLogoutMenuItem.setName("LogoutMenuItem");
                ivjLogoutMenuItem.setText("Logout");
                ivjLogoutMenuItem.setBackground(java.awt.SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjLogoutMenuItem;
    }

    /**
     * Return the LogProfileMenuItem property value.
     * @return javax.swing.JCheckBoxMenuItem
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JCheckBoxMenuItem getLogProfileMenuItem() {
        if (ivjLogProfileMenuItem == null) {
            try {
                ivjLogProfileMenuItem = new javax.swing.JCheckBoxMenuItem();
                ivjLogProfileMenuItem.setName("LogProfileMenuItem");
                ivjLogProfileMenuItem.setText("Log Profile");
                ivjLogProfileMenuItem.setBackground(java.awt.SystemColor.menu);
                ivjLogProfileMenuItem.setActionCommand("LogSQLMenuItem");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjLogProfileMenuItem;
    }

    /**
     * Return the LogProfileMenuItem1 property value.
     * @return javax.swing.JCheckBoxMenuItem
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JCheckBoxMenuItem getLogProfileMenuItem1() {
        if (ivjLogProfileMenuItem1 == null) {
            try {
                ivjLogProfileMenuItem1 = new javax.swing.JCheckBoxMenuItem();
                ivjLogProfileMenuItem1.setName("LogProfileMenuItem1");
                ivjLogProfileMenuItem1.setText("Profile");
                ivjLogProfileMenuItem1.setBackground(java.awt.SystemColor.menu);
                ivjLogProfileMenuItem1.setActionCommand("ProfileMenuItem");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjLogProfileMenuItem1;
    }

    /**
     * Return the LogSQLMenuItem property value.
     * @return javax.swing.JCheckBoxMenuItem
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JCheckBoxMenuItem getLogSQLMenuItem() {
        if (ivjLogSQLMenuItem == null) {
            try {
                ivjLogSQLMenuItem = new javax.swing.JCheckBoxMenuItem();
                ivjLogSQLMenuItem.setName("LogSQLMenuItem");
                ivjLogSQLMenuItem.setText("Log SQL");
                ivjLogSQLMenuItem.setBackground(java.awt.SystemColor.menu);
                ivjLogSQLMenuItem.setActionCommand("LogSQLMenuItem");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjLogSQLMenuItem;
    }

    /**
     * Return the MainPanel property value.
     * @return javax.swing.JPanel
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JPanel getMainPanel() {
        if (ivjMainPanel == null) {
            try {
                ivjMainPanel = new javax.swing.JPanel();
                ivjMainPanel.setName("MainPanel");
                ivjMainPanel.setLayout(new java.awt.GridBagLayout());
                ivjMainPanel.setBackground(java.awt.SystemColor.control);

                java.awt.GridBagConstraints constraintsSessionInspectorPanel = 
                    new java.awt.GridBagConstraints();
                constraintsSessionInspectorPanel.gridx = 0;
                constraintsSessionInspectorPanel.gridy = 0;
                constraintsSessionInspectorPanel.fill = 
                        java.awt.GridBagConstraints.BOTH;
                constraintsSessionInspectorPanel.weightx = 1.0;
                constraintsSessionInspectorPanel.weighty = 1.0;
                constraintsSessionInspectorPanel.insets = 
                        new java.awt.Insets(2, 2, 2, 2);
                getMainPanel().add(getSessionInspectorPanel(), 
                                   constraintsSessionInspectorPanel);
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
     * Return the PasteSQLMenuItem property value.
     * @return javax.swing.JMenuItem
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JMenuItem getPasteSQLMenuItem() {
        if (ivjPasteSQLMenuItem == null) {
            try {
                ivjPasteSQLMenuItem = new javax.swing.JMenuItem();
                ivjPasteSQLMenuItem.setName("PasteSQLMenuItem");
                ivjPasteSQLMenuItem.setText("Paste");
                ivjPasteSQLMenuItem.setBackground(java.awt.SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjPasteSQLMenuItem;
    }

    /**
     * Return the QueryMenu property value.
     * @return javax.swing.JMenu
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JMenu getQueryMenu() {
        if (ivjQueryMenu == null) {
            try {
                ivjQueryMenu = new javax.swing.JMenu();
                ivjQueryMenu.setName("QueryMenu");
                ivjQueryMenu.setText("Query");
                ivjQueryMenu.setBackground(java.awt.SystemColor.menu);
                ivjQueryMenu.add(getExecuteQueryMenuItem());
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjQueryMenu;
    }

    /**
     * Return the ResetDescriptorsMenuItem property value.
     * @return javax.swing.JMenuItem
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JMenuItem getResetDescriptorsMenuItem() {
        if (ivjResetDescriptorsMenuItem == null) {
            try {
                ivjResetDescriptorsMenuItem = new javax.swing.JMenuItem();
                ivjResetDescriptorsMenuItem.setName("ResetDescriptorsMenuItem");
                ivjResetDescriptorsMenuItem.setText("Reset Descriptors");
                ivjResetDescriptorsMenuItem.setBackground(java.awt.SystemColor.menu);
                ivjResetDescriptorsMenuItem.setForeground(java.awt.Color.black);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjResetDescriptorsMenuItem;
    }

    /**
     * Return the ResultsMenu property value.
     * @return javax.swing.JMenu
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JMenu getResultsMenu() {
        if (ivjResultsMenu == null) {
            try {
                ivjResultsMenu = new javax.swing.JMenu();
                ivjResultsMenu.setName("ResultsMenu");
                ivjResultsMenu.setText("Results");
                ivjResultsMenu.setBackground(java.awt.SystemColor.menu);
                ivjResultsMenu.add(getClearResultsMenuItem());
                //			ivjResultsMenu.add(getInspectResultMenuItem());
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjResultsMenu;
    }

    /**
     * Return the SelectSQLMenuItem property value.
     * @return javax.swing.JMenuItem
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JMenuItem getSelectSQLMenuItem() {
        if (ivjSelectSQLMenuItem == null) {
            try {
                ivjSelectSQLMenuItem = new javax.swing.JMenuItem();
                ivjSelectSQLMenuItem.setName("SelectSQLMenuItem");
                ivjSelectSQLMenuItem.setText("Select SQL");
                ivjSelectSQLMenuItem.setBackground(java.awt.SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjSelectSQLMenuItem;
    }

    /**
     * Return the SessionInspectorFrameJMenuBar property value.
     * @return javax.swing.JMenuBar
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JMenuBar getSessionInspectorFrameJMenuBar() {
        if (ivjSessionInspectorFrameJMenuBar == null) {
            try {
                ivjSessionInspectorFrameJMenuBar = new javax.swing.JMenuBar();
                ivjSessionInspectorFrameJMenuBar.setName("SessionInspectorFrameJMenuBar");
                ivjSessionInspectorFrameJMenuBar.setBackground(java.awt.SystemColor.menu);
                ivjSessionInspectorFrameJMenuBar.add(getFileMenu());
                ivjSessionInspectorFrameJMenuBar.add(getDescriptorsMenu());
                ivjSessionInspectorFrameJMenuBar.add(getCacheMenu());
                ivjSessionInspectorFrameJMenuBar.add(getQueryMenu());
                ivjSessionInspectorFrameJMenuBar.add(getSQLMenu());
                ivjSessionInspectorFrameJMenuBar.add(getLoginMenu());
                ivjSessionInspectorFrameJMenuBar.add(getLogMenu());
                ivjSessionInspectorFrameJMenuBar.add(getResultsMenu());
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjSessionInspectorFrameJMenuBar;
    }

    /**
     * Return the SessionInspectorPanel property value.
     * @return SessionInspectorPanel
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    SessionConsolePanel getSessionInspectorPanel() {
        if (ivjSessionInspectorPanel == null) {
            try {
                ivjSessionInspectorPanel = 
                        new org.eclipse.persistence.tools.sessionconsole.SessionConsolePanel();
                ivjSessionInspectorPanel.setName("SessionInspectorPanel");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjSessionInspectorPanel;
    }

    /**
     * Return the SQLMenu property value.
     * @return javax.swing.JMenu
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JMenu getSQLMenu() {
        if (ivjSQLMenu == null) {
            try {
                ivjSQLMenu = new javax.swing.JMenu();
                ivjSQLMenu.setName("SQLMenu");
                ivjSQLMenu.setText("SQL");
                ivjSQLMenu.setBackground(java.awt.SystemColor.menu);
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
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjSQLMenu;
    }

    /**
     * Return the SQLSelectMenuItem property value.
     * @return javax.swing.JMenuItem
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JMenuItem getSQLSelectMenuItem() {
        if (ivjSQLSelectMenuItem == null) {
            try {
                ivjSQLSelectMenuItem = new javax.swing.JMenuItem();
                ivjSQLSelectMenuItem.setName("SQLSelectMenuItem");
                ivjSQLSelectMenuItem.setText("Execute SQL Select");
                ivjSQLSelectMenuItem.setBackground(java.awt.SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjSQLSelectMenuItem;
    }

    /**
     * Return the SQLTemplateMenu property value.
     * @return javax.swing.JMenu
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JMenu getSQLTemplateMenu() {
        if (ivjSQLTemplateMenu == null) {
            try {
                ivjSQLTemplateMenu = new javax.swing.JMenu();
                ivjSQLTemplateMenu.setName("SQLTemplateMenu");
                ivjSQLTemplateMenu.setText("SQL");
                ivjSQLTemplateMenu.setBackground(java.awt.SystemColor.menu);
                ivjSQLTemplateMenu.add(getInsertSQLMenuItem());
                ivjSQLTemplateMenu.add(getUpdateSQLMenuItem());
                ivjSQLTemplateMenu.add(getDeleteSQLMenuItem());
                ivjSQLTemplateMenu.add(getSelectSQLMenuItem());
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjSQLTemplateMenu;
    }

    /**
     * Return the SQLUpdateMenuItem property value.
     * @return javax.swing.JMenuItem
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JMenuItem getSQLUpdateMenuItem() {
        if (ivjSQLUpdateMenuItem == null) {
            try {
                ivjSQLUpdateMenuItem = new javax.swing.JMenuItem();
                ivjSQLUpdateMenuItem.setName("SQLUpdateMenuItem");
                ivjSQLUpdateMenuItem.setText("Execute SQL Update");
                ivjSQLUpdateMenuItem.setBackground(java.awt.SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjSQLUpdateMenuItem;
    }

    /**
     * Return the UpdateSQLMenuItem property value.
     * @return javax.swing.JMenuItem
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JMenuItem getUpdateSQLMenuItem() {
        if (ivjUpdateSQLMenuItem == null) {
            try {
                ivjUpdateSQLMenuItem = new javax.swing.JMenuItem();
                ivjUpdateSQLMenuItem.setName("UpdateSQLMenuItem");
                ivjUpdateSQLMenuItem.setText("Update SQL");
                ivjUpdateSQLMenuItem.setBackground(java.awt.SystemColor.menu);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjUpdateSQLMenuItem;
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
        getCloseMenuItem().addActionListener(ivjEventHandler);
        getLoadProjectMenuItem().addActionListener(ivjEventHandler);
        getExitMenuItem().addActionListener(ivjEventHandler);
        getResetDescriptorsMenuItem().addActionListener(ivjEventHandler);
        getClearDescriptorsMenuItem().addActionListener(ivjEventHandler);
        getInspectDescriptorMenuItem().addActionListener(ivjEventHandler);
        getClearCacheMenuItem().addActionListener(ivjEventHandler);
        getInspectCacheMenuItem().addActionListener(ivjEventHandler);
        getSQLUpdateMenuItem().addActionListener(ivjEventHandler);
        getSQLSelectMenuItem().addActionListener(ivjEventHandler);
        getClearSQLMenuItem().addActionListener(ivjEventHandler);
        getLoginMenuItem().addActionListener(ivjEventHandler);
        getLogoutMenuItem().addActionListener(ivjEventHandler);
        getInspectSessionMenuItem1().addActionListener(ivjEventHandler);
        getExecuteQueryMenuItem().addActionListener(ivjEventHandler);
        getCutSQLMenuItem().addActionListener(ivjEventHandler);
        getCopySQLMenuItem().addActionListener(ivjEventHandler);
        getPasteSQLMenuItem().addActionListener(ivjEventHandler);
        getClearLogMenuItem().addActionListener(ivjEventHandler);
        getClearResultsMenuItem().addActionListener(ivjEventHandler);
        getInspectResultMenuItem().addActionListener(ivjEventHandler);
        getInsertSQLMenuItem().addActionListener(ivjEventHandler);
        getUpdateSQLMenuItem().addActionListener(ivjEventHandler);
        getDeleteSQLMenuItem().addActionListener(ivjEventHandler);
        getSelectSQLMenuItem().addActionListener(ivjEventHandler);
        getBrowseProfileMenuItem().addActionListener(ivjEventHandler);
        this.addWindowListener(ivjEventHandler);
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
            setName("SessionInspectorFrame");
            setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
            setJMenuBar(getSessionInspectorFrameJMenuBar());
            setSize(800, 600);
            setTitle("TopLink Session Console");

            URL iconURL = getClass().getResource("/tl_icon16.gif");
            if (iconURL != null) {
                setIconImage(new ImageIcon(iconURL).getImage());
            }
            setContentPane(getMainPanel());
            initConnections();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }

        // user code begin {2}
        centerComponent(this);
        // user code end
    }

    public void inspectCache() {
        getSessionInspectorPanel().inspectFromCache();
    }

    public void inspectDescriptor() {
        getSessionInspectorPanel().inspectDescriptor();
    }

    public void inspectResult() {
        getSessionInspectorPanel().inspectResult();
    }

    public void inspectSession() {
        getSessionInspectorPanel().inspectSession();
    }

    public void loadProject() {
        getSessionInspectorPanel().loadProject();
    }

    public void login() {
        getSessionInspectorPanel().setup();
        getSessionInspectorPanel().login();
    }

    public void logout() {
        getSessionInspectorPanel().logout();
    }

    /**
     * main entrypoint - starts the part when it is run as an application
     * @param args java.lang.String[]
     */
    public static void main(java.lang.String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SessionConsole aSessionInspectorFrame;
            aSessionInspectorFrame = new SessionConsole();
            try {
                Class aCloserClass = 
                    Class.forName("com.ibm.uvm.abt.edit.WindowCloser");
                Class[] parmTypes = { java.awt.Window.class };
                Object[] parms = { aSessionInspectorFrame };
                java.lang.reflect.Constructor aCtor = 
                    aCloserClass.getConstructor(parmTypes);
                aCtor.newInstance(parms);
            } catch (java.lang.Throwable exc) {
            }
            ;
            aSessionInspectorFrame.setVisible(true);
        } catch (Throwable exception) {
            System.err.println("Exception occurred in main() of javax.swing.JFrame");
            exception.printStackTrace(System.out);
        }
    }

    public void pasteSQL() {
        getSessionInspectorPanel().pasteSQL();
    }

    public void resetDescriptors() {
        getSessionInspectorPanel().resetDescriptors();
    }

    public void setSession(Session session) {
        getSessionInspectorPanel().setSession(session);
    }

    public void templateSQLDelete() {
        getSessionInspectorPanel().templateSQLDelete();
    }

    public void templateSQLInsert() {
        getSessionInspectorPanel().templateSQLInsert();
    }

    public void templateSQLSelect() {
        getSessionInspectorPanel().templateSQLSelect();
    }

    public void templateSQLUpdate() {
        getSessionInspectorPanel().templateSQLUpdate();
    }

    class IvjEventHandler implements java.awt.event.ActionListener, 
                                     java.awt.event.WindowListener {
        public void actionPerformed(java.awt.event.ActionEvent e) {
            if (e.getSource() == SessionConsole.this.getCloseMenuItem()) {
                connEtoM1(e);
            }
            if (e.getSource() == 
                SessionConsole.this.getLoadProjectMenuItem()) {
                connEtoC1(e);
            }
            if (e.getSource() == SessionConsole.this.getExitMenuItem()) {
                connEtoC2(e);
            }
            if (e.getSource() == 
                SessionConsole.this.getResetDescriptorsMenuItem()) {
                connEtoC3(e);
            }
            if (e.getSource() == 
                SessionConsole.this.getClearDescriptorsMenuItem()) {
                connEtoC4(e);
            }
            if (e.getSource() == 
                SessionConsole.this.getInspectDescriptorMenuItem()) {
                connEtoC5(e);
            }
            if (e.getSource() == SessionConsole.this.getClearCacheMenuItem()) {
                connEtoC6(e);
            }
            if (e.getSource() == 
                SessionConsole.this.getInspectCacheMenuItem()) {
                connEtoC7(e);
            }
            if (e.getSource() == SessionConsole.this.getSQLUpdateMenuItem()) {
                connEtoC8(e);
            }
            if (e.getSource() == SessionConsole.this.getSQLSelectMenuItem()) {
                connEtoC9(e);
            }
            if (e.getSource() == SessionConsole.this.getClearSQLMenuItem()) {
                connEtoC10(e);
            }
            if (e.getSource() == SessionConsole.this.getLoginMenuItem()) {
                connEtoC11(e);
            }
            if (e.getSource() == SessionConsole.this.getLogoutMenuItem()) {
                connEtoC12(e);
            }
            if (e.getSource() == 
                SessionConsole.this.getInspectSessionMenuItem1()) {
                connEtoC13(e);
            }
            if (e.getSource() == 
                SessionConsole.this.getExecuteQueryMenuItem()) {
                connEtoC14(e);
            }
            if (e.getSource() == SessionConsole.this.getCutSQLMenuItem()) {
                connEtoC15(e);
            }
            if (e.getSource() == SessionConsole.this.getCopySQLMenuItem()) {
                connEtoC16(e);
            }
            if (e.getSource() == SessionConsole.this.getPasteSQLMenuItem()) {
                connEtoC17(e);
            }
            if (e.getSource() == SessionConsole.this.getClearLogMenuItem()) {
                connEtoC18(e);
            }
            if (e.getSource() == 
                SessionConsole.this.getClearResultsMenuItem()) {
                connEtoC19(e);
            }
            if (e.getSource() == 
                SessionConsole.this.getInspectResultMenuItem()) {
                connEtoC20(e);
            }
            if (e.getSource() == SessionConsole.this.getInsertSQLMenuItem()) {
                connEtoC21(e);
            }
            if (e.getSource() == SessionConsole.this.getUpdateSQLMenuItem()) {
                connEtoC22(e);
            }
            if (e.getSource() == SessionConsole.this.getDeleteSQLMenuItem()) {
                connEtoC23(e);
            }
            if (e.getSource() == SessionConsole.this.getSelectSQLMenuItem()) {
                connEtoC24(e);
            }
            if (e.getSource() == 
                SessionConsole.this.getBrowseProfileMenuItem()) {
                connEtoC25(e);
            }
        }

        public void windowActivated(java.awt.event.WindowEvent e) {
        }

        public void windowClosed(java.awt.event.WindowEvent e) {
        }

        public void windowClosing(java.awt.event.WindowEvent e) {
            if (e.getSource() == SessionConsole.this) {
                connEtoC28(e);
            }
        }

        public void windowDeactivated(java.awt.event.WindowEvent e) {
        }

        public void windowDeiconified(java.awt.event.WindowEvent e) {
        }

        public void windowIconified(java.awt.event.WindowEvent e) {
        }

        public void windowOpened(java.awt.event.WindowEvent e) {
        }
    }
}
