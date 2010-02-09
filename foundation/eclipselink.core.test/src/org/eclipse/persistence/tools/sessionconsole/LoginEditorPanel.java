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
package org.eclipse.persistence.tools.sessionconsole;

import javax.swing.*;

import org.eclipse.persistence.sequencing.NativeSequence;
import org.eclipse.persistence.sequencing.TableSequence;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DefaultConnector;
import org.eclipse.persistence.tools.beans.*;

/**
 * Generic login panel.
 */
public class LoginEditorPanel extends JPanel implements javax.swing.event.CaretListener {
    public static String password;
    public static String bridge = null;
    private DatabaseLogin fieldLogin = new DatabaseLogin();
    private JPanel ivjBindingPage = null;
    private JCheckBox ivjBlobBindingCheckbox = null;
    private JCheckBox ivjBlobStreamBinding = null;
    private JComboBox ivjBridgeChoice = null;
    private JCheckBox ivjCacheStatementsCheckbox = null;
    private boolean ivjConnPtoP1Aligning = false;
    private JLabel ivjCreatorLabel = null;
    private JTextField ivjCreatorText = null;
    private JLabel ivjDatabasePlatformLabel = null;
    private JLabel ivjDatabaseURLLabel = null;
    private JTextField ivjDatabaseURLText = null;
    private JComboBox ivjDriverChoice = null;
    private JLabel ivjDriverClassNameLabel = null;
    private JTextField ivjDriverURLText = null;
    private JPanel ivjFillerPanel1 = null;
    private JPanel ivjFillerPanel11 = null;
    private JPanel ivjFillerPanel2 = null;
    private DatabaseLogin ivjLoginBean = null;
    private JCheckBox ivjNativeSequencingCheckbox = null;
    private JCheckBox ivjNativeSQLCheckbox = null;
    private JCheckBox ivjOptimizeDataConversionCheckbox = null;
    private JPanel ivjOtherPage = null;
    private JCheckBox ivjParameterizedSQLCheckbox = null;
    private JLabel ivjPasswordLabel = null;
    private JPasswordField ivjPasswordText = null;
    private JComboBox ivjPlatformChoice = null;
    private JLabel ivjPreallocationSizeLabel = null;
    private JTabbedPane ivjPropertiesBook = null;
    private JLabel ivjSequenceCounterNameLabel = null;
    private JTextField ivjSequenceCounterNameText = null;
    private JLabel ivjSequenceFieldNameLabel = null;
    private JTextField ivjSequenceFieldNameText = null;
    private JTextField ivjSequencePreallocationSizeText = null;
    private JLabel ivjSequenceTableNameLabel = null;
    private JTextField ivjSequenceTableNameText = null;
    private JPanel ivjSequencingPage = null;
    private JLabel ivjStatementCacheSizeLabel = null;
    private JTextField ivjStatementCacheSizeText = null;
    private JCheckBox ivjStringBindingCheckbox = null;
    private JLabel ivjStringBindSizeLable = null;
    private JTextField ivjStringBindSizeText = null;
    private JCheckBox ivjTrimStringsCheckbox = null;
    private JLabel ivjUserNameLabel = null;
    private JTextField ivjUserText = null;
    private JCheckBox ivjBatchWritingCheckBox = null;
    private JCheckBox ivjDirectConnectCheckBox = null;
    private JCheckBox ivjForceCaseCheckBox = null;
    private JCheckBox ivjJTSCheckBox = null;
    private JCheckBox ivjPoolingCheckBox = null;
    IvjEventHandler ivjEventHandler = new IvjEventHandler();
    private JPanel ivjDriverPage = null;
    private JPanel ivjFillerPanel = null;
    private JLabel ivjDriverLabel = null;

    /**
     * Constructor
     */
    public LoginEditorPanel() {
        super();
        initialize();
    }

    /**
     * LoginEditorPanel constructor comment.
     * @param layout java.awt.LayoutManager
     */
    public LoginEditorPanel(java.awt.LayoutManager layout) {
        super(layout);
    }

    /**
     * LoginEditorPanel constructor comment.
     * @param layout java.awt.LayoutManager
     * @param isDoubleBuffered boolean
     */
    public LoginEditorPanel(java.awt.LayoutManager layout,
                            boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
    }

    /**
     * LoginEditorPanel constructor comment.
     * @param isDoubleBuffered boolean
     */
    public LoginEditorPanel(boolean isDoubleBuffered) {
        super(isDoubleBuffered);
    }

    /**
     * Reset the login info for the driver.
     */
    public void bridgeChanged() {
        if (getBridgeChoice().getItemCount() > 1) {
            bridge = String.valueOf(getBridgeChoice().getSelectedItem());
        }

        // Config the login to the selected driver.
        String driver = String.valueOf(getBridgeChoice().getSelectedItem());
        DatabaseLogin login = (DatabaseLogin)getLogin().clone();

        /*if (driver.equals("HSQL")) {
			login.useHSQLDriver();
		} else if (driver.equals("PointBase JDBC")) {
			login.usePointBaseDriver();
		} else */
        if (driver.equals("Sun JDBC-ODBC")) {
            login.useJDBCODBCBridge();
        } else if (driver.equals("Oracle thin")) {
            login.useOracle();
            login.useOracleThinJDBCDriver();
        } else if (driver.equals("Oracle 8 OCI")) {
            login.useOracle();
            login.useOracleJDBCDriver();
        } /* else if (driver.equals("Oracle 7 OCI")) {
			login.useOracle();
			login.useOracleJDBCDriver();
		}*/ else if (driver.equals("DB2 (App)")) {
            login.useDB2();
            login.useDB2JDBCDriver();
        } else if (driver.equals("DB2 (Net)")) {
            login.useDB2();
            login.setDriverClassName("COM.ibm.db2.jdbc.net.DB2Driver");
        } else if (driver.equals("DB2 (Universal Driver)")) {
            login.useDB2();
            login.setDriverClassName("com.ibm.db2.jcc.DB2Driver");
            login.setDriverURLHeader("jdbc:db2://");
        } else if (driver.equals("DataDirect for DB2")) {
            login.useDB2();
            login.setDriverClassName("com.oracle.ias.jdbc.db2.DB2Driver");
            login.setDriverURLHeader("jdbc:oracle:db2://");
        } else if (driver.equals("Sybase JConnect")) {
            login.useSybase();
            login.useJConnect50Driver();
        } else if (driver.equals("DataDirect for Sybase")) {
            login.useSybase();
            login.setDriverClassName("com.oracle.ias.jdbc.sybase.SybaseDriver");
            login.setDriverURLHeader("jdbc:oracle:sybase://");
        } else if (driver.equals("Weblogic Thin")) {
            login.useSQLServer();
            login.useWebLogicSQLServerDriver();
        } else if (driver.equals("DataDirect for SQLServer")) {
            login.useSQLServer();
            login.setDriverClassName("com.oracle.ias.jdbc.sqlserver.SQLServerDriver");
            login.setDriverURLHeader("jdbc:oracle:sqlserver://");
        } else if (driver.equals("MS JDBC")) {
            login.useSQLServer();
            login.setDriverClassName("com.microsoft.jdbc.sqlserver.SQLServerDriver");
            login.setDriverURLHeader("jdbc:microsoft:sqlserver://");
        }
        login.setDatabaseURL("");
        login.setUserName("");
        login.setPassword("");
        setLogin(login);
    }

    /**
     * Method to handle events for the CaretListener interface.
     * @param e javax.swing.event.CaretEvent
     */
    public void caretUpdate(javax.swing.event.CaretEvent e) {
        // user code begin {1}
        // user code end
        if ((e.getSource() == getPasswordText())) {
            connEtoC7(e);
        }

        // user code begin {2}
        // user code end
    }

    /**
     * connEtoC2:  (NativeSequencingCheckbox.action.actionPerformed(java.awt.event.ActionEvent) --> LoginEditorPanel.resetSequenceButtons()V)
     * @param arg1 java.awt.event.ActionEvent
     */
    private void connEtoC2(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.resetSequenceButtons();
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC3:  (BridgeChoice.item.itemStateChanged(java.awt.event.ItemEvent) --> LoginEditorPanel.bridgeChanged()V)
     * @param arg1 java.awt.event.ItemEvent
     */
    private void connEtoC3(java.awt.event.ItemEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.bridgeChanged();
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC4:  (StringBindingCheckbox.action.actionPerformed(java.awt.event.ActionEvent) --> LoginEditorPanel.resetStringButtons()V)
     * @param arg1 java.awt.event.ActionEvent
     */
    private void connEtoC4(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.resetStringButtons();
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC5:  (BlobBindingCheckbox.action.actionPerformed(java.awt.event.ActionEvent) --> LoginEditorPanel.resetStringButtons()V)
     * @param arg1 java.awt.event.ActionEvent
     */
    private void connEtoC5(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.resetStringButtons();
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC7:  (PasswordText.caret.caretUpdate(javax.swing.event.CaretEvent) --> LoginEditorPanel.passwordChanged()V)
     * @param arg1 javax.swing.event.CaretEvent
     */
    private void connEtoC7(javax.swing.event.CaretEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.passwordChanged();
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connPtoP10SetSource:  (LoginBean.usesStreamsForBinding <--> BlobStreamBinding.selected)
     */
    private void connPtoP10SetSource() {
        /* Set the source from the target */
        try {
            if ((getLoginBean() != null)) {
                getLoginBean().setUsesByteArrayBinding(getBlobBindingCheckbox().isSelected());
            }

            // user code begin {1}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connPtoP10SetTarget:  (LoginBean.usesStreamsForBinding <--> BlobStreamBinding.selected)
     */
    private void connPtoP10SetTarget() {
        /* Set the target from the source */
    }

    /**
     * connPtoP11SetSource:  (LoginBean.usesNativeSequencing <--> NativeSequencingCheckbox.selected)
     */
    private void connPtoP11SetSource() {
        /* Set the source from the target */
        try {
            if ((getLoginBean() != null)) {
                if (getNativeSequencingCheckbox().isSelected() &&
                    !getLoginBean().shouldUseNativeSequencing()) {
                    getLoginBean().setDefaultSequence(new NativeSequence());
                } else if (!getNativeSequencingCheckbox().isSelected() &&
                           getLoginBean().shouldUseNativeSequencing()) {
                    getLoginBean().setDefaultSequence(new TableSequence());
                }
            }

            // user code begin {1}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connPtoP11SetTarget:  (LoginBean.usesNativeSequencing <--> NativeSequencingCheckbox.selected)
     */
    private void connPtoP11SetTarget() {
        /* Set the target from the source */
        try {
            if ((getLoginBean() != null)) {
                getNativeSequencingCheckbox().setSelected(getLoginBean().getUsesNativeSequencing());
            }

            // user code begin {1}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connPtoP12SetSource:  (LoginBean.sequencePreallocationSize <--> SequencePreallocationSizeText.text)
     */
    private void connPtoP12SetSource() {
        /* Set the source from the target */
        try {
            if ((getLoginBean() != null)) {
                getLoginBean().getDefaultSequence().setPreallocationSize(Integer.parseInt(getSequencePreallocationSizeText().getText()));
            }

            // user code begin {1}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connPtoP12SetTarget:  (LoginBean.sequencePreallocationSize <--> SequencePreallocationSizeLabel.text)
     */
    private void connPtoP12SetTarget() {
        /* Set the target from the source */
        try {
            if ((getLoginBean() != null)) {
                getSequencePreallocationSizeText().setText(String.valueOf(getLoginBean().getDefaultSequence().getPreallocationSize()));
            }

            // user code begin {1}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connPtoP13SetSource:  (LoginBean.shouldOptimizeDataConversion <--> OptimizeDataConversionCheckbox.selected)
     */
    private void connPtoP13SetSource() {
        /* Set the source from the target */
        try {
            if ((getLoginBean() != null)) {
                getLoginBean().setShouldOptimizeDataConversion(getOptimizeDataConversionCheckbox().isSelected());
            }

            // user code begin {1}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connPtoP13SetTarget:  (LoginBean.shouldOptimizeDataConversion <--> OptimizeDataConversionCheckbox.selected)
     */
    private void connPtoP13SetTarget() {
        /* Set the target from the source */
        try {
            if ((getLoginBean() != null)) {
                getOptimizeDataConversionCheckbox().setSelected(getLoginBean().getShouldOptimizeDataConversion());
            }

            // user code begin {1}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    private void connPtoP14SetSource() {
        try {
            if ((getLoginBean() != null)) {
                if (!getLoginBean().shouldUseNativeSequencing()) {
                    ((TableSequence)getLoginBean().getDefaultSequence()).setCounterFieldName(getSequenceCounterNameText().getText());
                }
            }
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    private void connPtoP14SetTarget() {
        try {
            if ((getLoginBean() != null) &&
                (!getLoginBean().shouldUseNativeSequencing())) {
                getSequenceCounterNameText().setText(((TableSequence)getLoginBean().getDefaultSequence()).getCounterFieldName());
            }
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    private void connPtoP15SetSource() {
        try {
            if ((getLoginBean() != null) &&
                (!getLoginBean().shouldUseNativeSequencing())) {
                ((TableSequence)getLoginBean().getDefaultSequence()).setNameFieldName(getSequenceFieldNameText().getText());
            }
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    private void connPtoP15SetTarget() {
        try {
            if ((getLoginBean() != null) &&
                (!getLoginBean().shouldUseNativeSequencing())) {
                getSequenceFieldNameText().setText(((TableSequence)getLoginBean().getDefaultSequence()).getNameFieldName());
            }
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    private void connPtoP16SetSource() {
        try {
            if ((getLoginBean() != null) &&
                (!getLoginBean().shouldUseNativeSequencing())) {
                ((TableSequence)getLoginBean().getDefaultSequence()).setTableName(getSequenceTableNameText().getText());
            }
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    private void connPtoP16SetTarget() {
        try {
            if ((getLoginBean() != null) &&
                (!getLoginBean().shouldUseNativeSequencing())) {
                getSequenceTableNameText().setText(((TableSequence)getLoginBean().getDefaultSequence()).getTableName());
            }
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    /**
     * connPtoP17SetSource:  (LoginBean.shouldOptimizeDataConversion <--> OptimizeDataConversionCheckbox.selected)
     */
    private void connPtoP17SetSource() {
        /* Set the source from the target */
        try {
            if ((getLoginBean() != null)) {
                getLoginBean().setShouldBindAllParameters(getParameterizedSQLCheckbox().isSelected());
            }

            // user code begin {1}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connPtoP17SetTarget:  (LoginBean.shouldOptimizeDataConversion <--> OptimizeDataConversionCheckbox.selected)
     */
    private void connPtoP17SetTarget() {
        /* Set the target from the source */
        try {
            if ((getLoginBean() != null)) {
                getParameterizedSQLCheckbox().setSelected(getLoginBean().getShouldBindAllParameters());
            }

            // user code begin {1}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connPtoP18SetSource:  (LoginBean.tableQualifier <--> CreatorText.text)
     */
    private void connPtoP18SetSource() {
        /* Set the source from the target */
        try {
            if ((getLoginBean() != null)) {
                getLoginBean().setTableQualifier(getCreatorText().getText());
            }

            // user code begin {1}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connPtoP18SetTarget:  (LoginBean.tableQualifier <--> CreatorText.text)
     */
    private void connPtoP18SetTarget() {
        /* Set the target from the source */
        try {
            if ((getLoginBean() != null)) {
                getCreatorText().setText(getLoginBean().getTableQualifier());
            }

            // user code begin {1}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connPtoP19SetSource:  (LoginBean.shouldCacheAllStatements <--> CacheStatementsCheckbox.selected)
     */
    private void connPtoP19SetSource() {
        /* Set the source from the target */
        try {
            if ((getLoginBean() != null)) {
                getLoginBean().setShouldCacheAllStatements(getCacheStatementsCheckbox().isSelected());
            }

            // user code begin {1}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connPtoP19SetTarget:  (LoginBean.shouldCacheAllStatements <--> CacheStatementsCheckbox.selected)
     */
    private void connPtoP19SetTarget() {
        /* Set the target from the source */
        try {
            if ((getLoginBean() != null)) {
                getCacheStatementsCheckbox().setSelected(getLoginBean().getShouldCacheAllStatements());
            }

            // user code begin {1}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connPtoP1SetSource:  (LoginEditorPanel.login <--> LoginBean.this)
     */
    private void connPtoP1SetSource() {
        /* Set the source from the target */
        try {
            if (ivjConnPtoP1Aligning == false) {
                // user code begin {1}
                // user code end
                ivjConnPtoP1Aligning = true;
                if ((getLoginBean() != null)) {
                    this.setLogin(getLoginBean());
                }

                // user code begin {2}
                // user code end
                ivjConnPtoP1Aligning = false;
            }
        } catch (java.lang.Throwable ivjExc) {
            ivjConnPtoP1Aligning = false;
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connPtoP1SetTarget:  (LoginEditorPanel.login <--> LoginBean.this)
     */
    private void connPtoP1SetTarget() {
        /* Set the target from the source */
        try {
            if (ivjConnPtoP1Aligning == false) {
                // user code begin {1}
                // user code end
                ivjConnPtoP1Aligning = true;
                setLoginBean(this.getLogin());
                // user code begin {2}
                // user code end
                ivjConnPtoP1Aligning = false;
            }
        } catch (java.lang.Throwable ivjExc) {
            ivjConnPtoP1Aligning = false;
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connPtoP20SetSource:  (LoginBean.statementCacheSize <--> StatementCacheSizeText.text)
     */
    private void connPtoP20SetSource() {
        /* Set the source from the target */
        try {
            if ((getLoginBean() != null)) {
                getLoginBean().setStatementCacheSize(Integer.parseInt(getStatementCacheSizeText().getText()));
            }

            // user code begin {1}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connPtoP20SetTarget:  (LoginBean.statementCacheSize <--> StatementCacheSizeText.text)
     */
    private void connPtoP20SetTarget() {
        /* Set the target from the source */
        try {
            if ((getLoginBean() != null)) {
                getStatementCacheSizeText().setText(String.valueOf(getLoginBean().getStatementCacheSize()));
            }

            // user code begin {1}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connPtoP21SetSource:  (LoginBean.stringBindingSize <--> StringBindSizeText.text)
     */
    private void connPtoP21SetSource() {
        /* Set the source from the target */
        try {
            if ((getLoginBean() != null)) {
                getLoginBean().setStringBindingSize(Integer.parseInt(getStringBindSizeText().getText()));
            }

            // user code begin {1}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connPtoP21SetTarget:  (LoginBean.stringBindingSize <--> StringBindSizeText.text)
     */
    private void connPtoP21SetTarget() {
        /* Set the target from the source */
        try {
            if ((getLoginBean() != null)) {
                getStringBindSizeText().setText(String.valueOf(getLoginBean().getStringBindingSize()));
            }

            // user code begin {1}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connPtoP22SetSource:  (LoginBean.usesStringBinding <--> StringBindingCheckbox.selected)
     */
    private void connPtoP22SetSource() {
        /* Set the source from the target */
        try {
            if ((getLoginBean() != null)) {
                getLoginBean().setUsesStringBinding(getStringBindingCheckbox().isSelected());
            }

            // user code begin {1}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connPtoP22SetTarget:  (LoginBean.usesStringBinding <--> StringBindingCheckbox.selected)
     */
    private void connPtoP22SetTarget() {
        /* Set the target from the source */
        try {
            if ((getLoginBean() != null)) {
                getStringBindingCheckbox().setSelected(getLoginBean().getUsesStringBinding());
            }

            // user code begin {1}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connPtoP23SetSource:  (LoginBean.shouldTrimStrings <--> TrimStringsCheckbox.selected)
     */
    private void connPtoP23SetSource() {
        /* Set the source from the target */
        try {
            if ((getLoginBean() != null)) {
                getLoginBean().setShouldTrimStrings(getTrimStringsCheckbox().isSelected());
            }

            // user code begin {1}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connPtoP23SetTarget:  (LoginBean.shouldTrimStrings <--> TrimStringsCheckbox.selected)
     */
    private void connPtoP23SetTarget() {
        /* Set the target from the source */
        try {
            if ((getLoginBean() != null)) {
                getTrimStringsCheckbox().setSelected(getLoginBean().getShouldTrimStrings());
            }

            // user code begin {1}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connPtoP24SetSource:  (LoginBean.usesDirectDriverConnect <--> DirectConnectCheckBox.selected)
     */
    private void connPtoP24SetSource() {
        /* Set the source from the target */
        try {
            if ((getLoginBean() != null)) {
                if (getDirectConnectCheckBox().isSelected()) {
                    getLoginBean().useDirectDriverConnect();
                } else {
                    getLoginBean().useDefaultDriverConnect();
                }
            }

            // user code begin {1}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connPtoP24SetTarget:  (LoginBean.usesDirectDriverConnect <--> DirectConnectCheckBox.selected)
     */
    private void connPtoP24SetTarget() {
        /* Set the target from the source */
    }

    /**
     * connPtoP25SetSource:  (LoginBean.shouldForceFieldNamesToUpperCase <--> ForceCaseCheckBox.selected)
     */
    private void connPtoP25SetSource() {
        /* Set the source from the target */
        try {
            if ((getLoginBean() != null)) {
                getLoginBean().setShouldForceFieldNamesToUpperCase(getForceCaseCheckBox().isSelected());
            }

            // user code begin {1}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connPtoP25SetTarget:  (LoginBean.shouldForceFieldNamesToUpperCase <--> ForceCaseCheckBox.selected)
     */
    private void connPtoP25SetTarget() {
        /* Set the target from the source */
    }

    /**
     * connPtoP2SetSource:  (LoginBean.driverClassName <--> DriverChoice.selectedItem)
     */
    private void connPtoP2SetSource() {
        /* Set the source from the target */
        try {
            if ((getLoginBean() != null)) {
                if (getLoginBean().getConnector() instanceof
                    DefaultConnector) {
                    getLoginBean().setDriverClassName((String)getDriverChoice().getSelectedItem());
                }
            }

            // user code begin {1}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connPtoP2SetTarget:  (LoginBean.driverClassName <--> DriverChoice.selectedItem)
     */
    private void connPtoP2SetTarget() {
        /* Set the target from the source */
        try {
            if ((getLoginBean() != null)) {
                getDriverChoice().setSelectedItem(getLoginBean().getDriverClassName());
            }

            // user code begin {1}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connPtoP3SetSource:  (LoginBean.driverURLHeader <--> DriverURLText.text)
     */
    private void connPtoP3SetSource() {
        /* Set the source from the target */
        try {
            if ((getLoginBean() != null)) {
                if (getLoginBean().getConnector() instanceof
                    DefaultConnector) {
                    getLoginBean().setDriverURLHeader(getDriverURLText().getText());
                }
            }

            // user code begin {1}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connPtoP3SetTarget:  (LoginBean.driverURLHeader <--> DriverURLText.text)
     */
    private void connPtoP3SetTarget() {
        /* Set the target from the source */
        try {
            if ((getLoginBean() != null)) {
                getDriverURLText().setText(getLoginBean().getDriverURLHeader());
            }

            // user code begin {1}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connPtoP4SetSource:  (LoginBean.usesBatchWriting <--> BatchWritingCheckBox.selected)
     */
    private void connPtoP4SetSource() {
        /* Set the source from the target */
        try {
            if ((getLoginBean() != null)) {
                getLoginBean().setUsesBatchWriting(getBatchWritingCheckBox().isSelected());
            }

            // user code begin {1}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connPtoP4SetTarget:  (LoginBean.usesBatchWriting <--> BatchWritingCheckBox.selected)
     */
    private void connPtoP4SetTarget() {
        /* Set the target from the source */
    }

    /**
     * connPtoP5SetSource:  (LoginBean.databaseURL <--> DatabaseURLText.text)
     */
    private void connPtoP5SetSource() {
        /* Set the source from the target */
        try {
            if ((getLoginBean() != null)) {
                if (getLoginBean().getConnector() instanceof
                    DefaultConnector) {
                    getLoginBean().setDatabaseURL(getDatabaseURLText().getText());
                }
            }

            // user code begin {1}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connPtoP5SetTarget:  (LoginBean.databaseURL <--> DatabaseURLText.text)
     */
    private void connPtoP5SetTarget() {
        /* Set the target from the source */
        try {
            if ((getLoginBean() != null)) {
                getDatabaseURLText().setText(getLoginBean().getDatabaseURL());
            }

            // user code begin {1}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connPtoP6SetSource:  (LoginBean.platformClassName <--> PlatformChoice.selectedItem)
     */
    private void connPtoP6SetSource() {
        /* Set the source from the target */
        try {
            if ((getLoginBean() != null) &&
                (getPlatformChoice().getSelectedItem() != null)) {
                getLoginBean().setPlatformClassName((String)getPlatformChoice().getSelectedItem());
            }

            // user code begin {1}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connPtoP6SetTarget:  (LoginBean.platformClassName <--> PlatformChoice.selectedItem)
     */
    private void connPtoP6SetTarget() {
        /* Set the target from the source */
        try {
            if ((getLoginBean() != null)) {
                getPlatformChoice().setSelectedItem(getLoginBean().getPlatformClassName());
            }

            // user code begin {1}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connPtoP7SetSource:  (LoginBean.userName <--> UserText.text)
     */
    private void connPtoP7SetSource() {
        /* Set the source from the target */
        try {
            if ((getLoginBean() != null)) {
                getLoginBean().setUserName(getUserText().getText());
            }

            // user code begin {1}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connPtoP7SetTarget:  (LoginBean.userName <--> UserText.text)
     */
    private void connPtoP7SetTarget() {
        /* Set the target from the source */
        try {
            if ((getLoginBean() != null)) {
                getUserText().setText(getLoginBean().getUserName());
            }

            // user code begin {1}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connPtoP8SetSource:  (LoginBean.usesBinding <--> BlobBindingCheckbox.selected)
     */
    private void connPtoP8SetSource() {
        /* Set the source from the target */
        try {
            if ((getLoginBean() != null)) {
                getLoginBean().setUsesStreamsForBinding(getBlobStreamBinding().isSelected());
            }

            // user code begin {1}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connPtoP8SetTarget:  (LoginBean.usesBinding <--> BlobBindingCheckbox.selected)
     */
    private void connPtoP8SetTarget() {
        /* Set the target from the source */
        try {
            if ((getLoginBean() != null)) {
                getBlobStreamBinding().setSelected(getLoginBean().getUsesStreamsForBinding());
            }

            // user code begin {1}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connPtoP9SetSource:  (LoginBean.usesNativeSequencing <--> NativeSQLCheckbox.selected)
     */
    private void connPtoP9SetSource() {
        /* Set the source from the target */
        try {
            if ((getLoginBean() != null)) {
                getLoginBean().setUsesNativeSQL(getNativeSQLCheckbox().isSelected());
            }

            // user code begin {1}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connPtoP9SetTarget:  (LoginBean.usesNativeSequencing <--> NativeSQLCheckbox.selected)
     */
    private void connPtoP9SetTarget() {
        /* Set the target from the source */
        try {
            if ((getLoginBean() != null)) {
                getNativeSQLCheckbox().setSelected(getLoginBean().getUsesNativeSQL());
            }

            // user code begin {1}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * Return the BatchWritingCheckBox property value.
     * @return javax.swing.JCheckBox
     */
    private javax.swing.JCheckBox getBatchWritingCheckBox() {
        if (ivjBatchWritingCheckBox == null) {
            try {
                ivjBatchWritingCheckBox = new javax.swing.JCheckBox();
                ivjBatchWritingCheckBox.setName("BatchWritingCheckBox");
                ivjBatchWritingCheckBox.setText("Batch Writing");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjBatchWritingCheckBox;
    }

    /**
     * Return the BindingPage property value.
     * @return javax.swing.JPanel
     */
    private javax.swing.JPanel getBindingPage() {
        if (ivjBindingPage == null) {
            try {
                ivjBindingPage = new javax.swing.JPanel();
                ivjBindingPage.setName("BindingPage");
                ivjBindingPage.setLayout(new java.awt.GridBagLayout());
                ivjBindingPage.setBackground(java.awt.SystemColor.control);

                java.awt.GridBagConstraints constraintsStringBindSizeLable =
                    new java.awt.GridBagConstraints();
                constraintsStringBindSizeLable.gridx = 0;
                constraintsStringBindSizeLable.gridy = 2;
                constraintsStringBindSizeLable.anchor =
                        java.awt.GridBagConstraints.WEST;
                constraintsStringBindSizeLable.insets =
                        new java.awt.Insets(2, 2, 2, 2);
                getBindingPage().add(getStringBindSizeLable(),
                                     constraintsStringBindSizeLable);

                java.awt.GridBagConstraints constraintsStringBindingCheckbox =
                    new java.awt.GridBagConstraints();
                constraintsStringBindingCheckbox.gridx = 0;
                constraintsStringBindingCheckbox.gridy = 1;
                constraintsStringBindingCheckbox.anchor =
                        java.awt.GridBagConstraints.NORTHWEST;
                constraintsStringBindingCheckbox.insets =
                        new java.awt.Insets(2, 2, 2, 2);
                getBindingPage().add(getStringBindingCheckbox(),
                                     constraintsStringBindingCheckbox);

                java.awt.GridBagConstraints constraintsBlobBindingCheckbox =
                    new java.awt.GridBagConstraints();
                constraintsBlobBindingCheckbox.gridx = 0;
                constraintsBlobBindingCheckbox.gridy = 0;
                constraintsBlobBindingCheckbox.anchor =
                        java.awt.GridBagConstraints.NORTHWEST;
                constraintsBlobBindingCheckbox.insets =
                        new java.awt.Insets(2, 2, 2, 2);
                getBindingPage().add(getBlobBindingCheckbox(),
                                     constraintsBlobBindingCheckbox);

                java.awt.GridBagConstraints constraintsBlobStreamBinding =
                    new java.awt.GridBagConstraints();
                constraintsBlobStreamBinding.gridx = 1;
                constraintsBlobStreamBinding.gridy = 0;
                constraintsBlobStreamBinding.anchor =
                        java.awt.GridBagConstraints.NORTHWEST;
                constraintsBlobStreamBinding.insets =
                        new java.awt.Insets(2, 2, 2, 2);
                getBindingPage().add(getBlobStreamBinding(),
                                     constraintsBlobStreamBinding);

                java.awt.GridBagConstraints constraintsStringBindSizeText =
                    new java.awt.GridBagConstraints();
                constraintsStringBindSizeText.gridx = 1;
                constraintsStringBindSizeText.gridy = 2;
                constraintsStringBindSizeText.fill =
                        java.awt.GridBagConstraints.HORIZONTAL;
                constraintsStringBindSizeText.weightx = 1.0;
                constraintsStringBindSizeText.insets =
                        new java.awt.Insets(2, 2, 2, 2);
                getBindingPage().add(getStringBindSizeText(),
                                     constraintsStringBindSizeText);

                java.awt.GridBagConstraints constraintsFillerPanel2 =
                    new java.awt.GridBagConstraints();
                constraintsFillerPanel2.gridx = 0;
                constraintsFillerPanel2.gridy = 5;
                constraintsFillerPanel2.gridwidth = 2;
                constraintsFillerPanel2.gridheight = 2;
                constraintsFillerPanel2.fill =
                        java.awt.GridBagConstraints.BOTH;
                constraintsFillerPanel2.weightx = 1.0;
                constraintsFillerPanel2.weighty = 1.0;
                getBindingPage().add(getFillerPanel2(),
                                     constraintsFillerPanel2);

                java.awt.GridBagConstraints constraintsParameterizedSQLCheckbox =
                    new java.awt.GridBagConstraints();
                constraintsParameterizedSQLCheckbox.gridx = 0;
                constraintsParameterizedSQLCheckbox.gridy = 3;
                constraintsParameterizedSQLCheckbox.anchor =
                        java.awt.GridBagConstraints.NORTHWEST;
                constraintsParameterizedSQLCheckbox.insets =
                        new java.awt.Insets(2, 2, 2, 2);
                getBindingPage().add(getParameterizedSQLCheckbox(),
                                     constraintsParameterizedSQLCheckbox);

                java.awt.GridBagConstraints constraintsStatementCacheSizeText =
                    new java.awt.GridBagConstraints();
                constraintsStatementCacheSizeText.gridx = 1;
                constraintsStatementCacheSizeText.gridy = 4;
                constraintsStatementCacheSizeText.fill =
                        java.awt.GridBagConstraints.HORIZONTAL;
                constraintsStatementCacheSizeText.weightx = 1.0;
                constraintsStatementCacheSizeText.insets =
                        new java.awt.Insets(2, 2, 2, 2);
                getBindingPage().add(getStatementCacheSizeText(),
                                     constraintsStatementCacheSizeText);

                java.awt.GridBagConstraints constraintsStatementCacheSizeLabel =
                    new java.awt.GridBagConstraints();
                constraintsStatementCacheSizeLabel.gridx = 0;
                constraintsStatementCacheSizeLabel.gridy = 4;
                constraintsStatementCacheSizeLabel.anchor =
                        java.awt.GridBagConstraints.WEST;
                constraintsStatementCacheSizeLabel.insets =
                        new java.awt.Insets(2, 2, 2, 2);
                getBindingPage().add(getStatementCacheSizeLabel(),
                                     constraintsStatementCacheSizeLabel);

                java.awt.GridBagConstraints constraintsCacheStatementsCheckbox =
                    new java.awt.GridBagConstraints();
                constraintsCacheStatementsCheckbox.gridx = 1;
                constraintsCacheStatementsCheckbox.gridy = 3;
                constraintsCacheStatementsCheckbox.anchor =
                        java.awt.GridBagConstraints.NORTHWEST;
                constraintsCacheStatementsCheckbox.insets =
                        new java.awt.Insets(2, 2, 2, 2);
                getBindingPage().add(getCacheStatementsCheckbox(),
                                     constraintsCacheStatementsCheckbox);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjBindingPage;
    }

    /**
     * Return the BlobBindingCheckbox property value.
     * @return javax.swing.JCheckBox
     */
    private javax.swing.JCheckBox getBlobBindingCheckbox() {
        if (ivjBlobBindingCheckbox == null) {
            try {
                ivjBlobBindingCheckbox = new javax.swing.JCheckBox();
                ivjBlobBindingCheckbox.setName("BlobBindingCheckbox");
                ivjBlobBindingCheckbox.setText("Byte Array Binding");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjBlobBindingCheckbox;
    }

    /**
     * Return the BlobStreamBinding property value.
     * @return javax.swing.JCheckBox
     */
    private javax.swing.JCheckBox getBlobStreamBinding() {
        if (ivjBlobStreamBinding == null) {
            try {
                ivjBlobStreamBinding = new javax.swing.JCheckBox();
                ivjBlobStreamBinding.setName("BlobStreamBinding");
                ivjBlobStreamBinding.setText("Byte Array Stream Binding");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjBlobStreamBinding;
    }

    /**
     * Return the JComboBox1 property value.
     * @return javax.swing.JComboBox
     */
    private javax.swing.JComboBox getBridgeChoice() {
        if (ivjBridgeChoice == null) {
            try {
                ivjBridgeChoice = new javax.swing.JComboBox();
                ivjBridgeChoice.setName("BridgeChoice");
                ivjBridgeChoice.setBackground(java.awt.SystemColor.window);
                ivjBridgeChoice.setEditable(false);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjBridgeChoice;
    }

    /**
     * Return the CacheStatementsCheckbox property value.
     * @return javax.swing.JCheckBox
     */
    private javax.swing.JCheckBox getCacheStatementsCheckbox() {
        if (ivjCacheStatementsCheckbox == null) {
            try {
                ivjCacheStatementsCheckbox = new javax.swing.JCheckBox();
                ivjCacheStatementsCheckbox.setName("CacheStatementsCheckbox");
                ivjCacheStatementsCheckbox.setText("Cache All Statements");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjCacheStatementsCheckbox;
    }

    /**
     * Return the CreatorLabel property value.
     * @return javax.swing.JLabel
     */
    private javax.swing.JLabel getCreatorLabel() {
        if (ivjCreatorLabel == null) {
            try {
                ivjCreatorLabel = new javax.swing.JLabel();
                ivjCreatorLabel.setName("CreatorLabel");
                ivjCreatorLabel.setText("Creator/Table Qualifier:");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjCreatorLabel;
    }

    /**
     * Return the CreatorText property value.
     * @return javax.swing.JTextField
     */
    private javax.swing.JTextField getCreatorText() {
        if (ivjCreatorText == null) {
            try {
                ivjCreatorText = new javax.swing.JTextField();
                ivjCreatorText.setName("CreatorText");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjCreatorText;
    }

    /**
     * Return the DatabasePlatformLabel property value.
     * @return javax.swing.JLabel
     */
    private javax.swing.JLabel getDatabasePlatformLabel() {
        if (ivjDatabasePlatformLabel == null) {
            try {
                ivjDatabasePlatformLabel = new javax.swing.JLabel();
                ivjDatabasePlatformLabel.setName("DatabasePlatformLabel");
                ivjDatabasePlatformLabel.setText("Database Platform:");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjDatabasePlatformLabel;
    }

    /**
     * Return the DatabaseURLLabel property value.
     * @return javax.swing.JLabel
     */
    private javax.swing.JLabel getDatabaseURLLabel() {
        if (ivjDatabaseURLLabel == null) {
            try {
                ivjDatabaseURLLabel = new javax.swing.JLabel();
                ivjDatabaseURLLabel.setName("DatabaseURLLabel");
                ivjDatabaseURLLabel.setText("Database URL:");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjDatabaseURLLabel;
    }

    /**
     * Return the DatabaseURLText property value.
     * @return javax.swing.JTextField
     */
    private javax.swing.JTextField getDatabaseURLText() {
        if (ivjDatabaseURLText == null) {
            try {
                ivjDatabaseURLText = new javax.swing.JTextField();
                ivjDatabaseURLText.setName("DatabaseURLText");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjDatabaseURLText;
    }

    /**
     * Return the DirectConnectCheckBox property value.
     * @return javax.swing.JCheckBox
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JCheckBox getDirectConnectCheckBox() {
        if (ivjDirectConnectCheckBox == null) {
            try {
                ivjDirectConnectCheckBox = new javax.swing.JCheckBox();
                ivjDirectConnectCheckBox.setName("DirectConnectCheckBox");
                ivjDirectConnectCheckBox.setText("Direct Driver Connection");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjDirectConnectCheckBox;
    }

    /**
     * Return the DriverChoice property value.
     * @return javax.swing.JComboBox
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JComboBox getDriverChoice() {
        if (ivjDriverChoice == null) {
            try {
                ivjDriverChoice = new javax.swing.JComboBox();
                ivjDriverChoice.setName("DriverChoice");
                ivjDriverChoice.setBackground(java.awt.SystemColor.window);
                ivjDriverChoice.setEditable(true);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjDriverChoice;
    }

    /**
     * Return the DriverClassNameLabel property value.
     * @return javax.swing.JLabel
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JLabel getDriverClassNameLabel() {
        if (ivjDriverClassNameLabel == null) {
            try {
                ivjDriverClassNameLabel = new javax.swing.JLabel();
                ivjDriverClassNameLabel.setName("DriverClassNameLabel");
                ivjDriverClassNameLabel.setText("Driver Class Name:");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjDriverClassNameLabel;
    }

    /**
     * Return the BridgeLabel property value.
     * @return javax.swing.JLabel
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JLabel getDriverLabel() {
        if (ivjDriverLabel == null) {
            try {
                ivjDriverLabel = new javax.swing.JLabel();
                ivjDriverLabel.setName("DriverLabel");
                ivjDriverLabel.setText("Driver:");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjDriverLabel;
    }

    /**
     * Return the DriverPage property value.
     * @return javax.swing.JPanel
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JPanel getDriverPage() {
        if (ivjDriverPage == null) {
            try {
                ivjDriverPage = new javax.swing.JPanel();
                ivjDriverPage.setName("DriverPage");
                ivjDriverPage.setLayout(new java.awt.GridBagLayout());
                ivjDriverPage.setBackground(java.awt.SystemColor.control);

                java.awt.GridBagConstraints constraintsDriverLabel =
                    new java.awt.GridBagConstraints();
                constraintsDriverLabel.gridx = 0;
                constraintsDriverLabel.gridy = 0;
                constraintsDriverLabel.anchor =
                        java.awt.GridBagConstraints.WEST;
                constraintsDriverLabel.insets =
                        new java.awt.Insets(4, 4, 4, 4);
                getDriverPage().add(getDriverLabel(), constraintsDriverLabel);

                java.awt.GridBagConstraints constraintsBridgeChoice =
                    new java.awt.GridBagConstraints();
                constraintsBridgeChoice.gridx = 1;
                constraintsBridgeChoice.gridy = 0;
                constraintsBridgeChoice.gridwidth = 2;
                constraintsBridgeChoice.fill =
                        java.awt.GridBagConstraints.HORIZONTAL;
                constraintsBridgeChoice.weightx = 1.0;
                constraintsBridgeChoice.insets =
                        new java.awt.Insets(4, 4, 4, 4);
                getDriverPage().add(getBridgeChoice(),
                                    constraintsBridgeChoice);

                java.awt.GridBagConstraints constraintsDatabasePlatformLabel =
                    new java.awt.GridBagConstraints();
                constraintsDatabasePlatformLabel.gridx = 0;
                constraintsDatabasePlatformLabel.gridy = 1;
                constraintsDatabasePlatformLabel.anchor =
                        java.awt.GridBagConstraints.WEST;
                constraintsDatabasePlatformLabel.insets =
                        new java.awt.Insets(4, 4, 4, 4);
                getDriverPage().add(getDatabasePlatformLabel(),
                                    constraintsDatabasePlatformLabel);

                java.awt.GridBagConstraints constraintsPlatformChoice =
                    new java.awt.GridBagConstraints();
                constraintsPlatformChoice.gridx = 1;
                constraintsPlatformChoice.gridy = 1;
                constraintsPlatformChoice.gridwidth = 2;
                constraintsPlatformChoice.fill =
                        java.awt.GridBagConstraints.HORIZONTAL;
                constraintsPlatformChoice.weightx = 1.0;
                constraintsPlatformChoice.insets =
                        new java.awt.Insets(4, 4, 4, 4);
                getDriverPage().add(getPlatformChoice(),
                                    constraintsPlatformChoice);

                java.awt.GridBagConstraints constraintsDriverClassNameLabel =
                    new java.awt.GridBagConstraints();
                constraintsDriverClassNameLabel.gridx = 0;
                constraintsDriverClassNameLabel.gridy = 2;
                constraintsDriverClassNameLabel.anchor =
                        java.awt.GridBagConstraints.WEST;
                constraintsDriverClassNameLabel.insets =
                        new java.awt.Insets(4, 4, 4, 4);
                getDriverPage().add(getDriverClassNameLabel(),
                                    constraintsDriverClassNameLabel);

                java.awt.GridBagConstraints constraintsDriverChoice =
                    new java.awt.GridBagConstraints();
                constraintsDriverChoice.gridx = 1;
                constraintsDriverChoice.gridy = 2;
                constraintsDriverChoice.gridwidth = 2;
                constraintsDriverChoice.fill =
                        java.awt.GridBagConstraints.HORIZONTAL;
                constraintsDriverChoice.weightx = 1.0;
                constraintsDriverChoice.insets =
                        new java.awt.Insets(4, 4, 4, 4);
                getDriverPage().add(getDriverChoice(),
                                    constraintsDriverChoice);

                java.awt.GridBagConstraints constraintsDatabaseURLLabel =
                    new java.awt.GridBagConstraints();
                constraintsDatabaseURLLabel.gridx = 0;
                constraintsDatabaseURLLabel.gridy = 3;
                constraintsDatabaseURLLabel.anchor =
                        java.awt.GridBagConstraints.WEST;
                constraintsDatabaseURLLabel.insets =
                        new java.awt.Insets(4, 4, 4, 4);
                getDriverPage().add(getDatabaseURLLabel(),
                                    constraintsDatabaseURLLabel);

                java.awt.GridBagConstraints constraintsDriverURLText =
                    new java.awt.GridBagConstraints();
                constraintsDriverURLText.gridx = 1;
                constraintsDriverURLText.gridy = 3;
                constraintsDriverURLText.fill =
                        java.awt.GridBagConstraints.HORIZONTAL;
                constraintsDriverURLText.weightx = 1.0;
                constraintsDriverURLText.insets =
                        new java.awt.Insets(4, 4, 4, 4);
                getDriverPage().add(getDriverURLText(),
                                    constraintsDriverURLText);

                java.awt.GridBagConstraints constraintsDatabaseURLText =
                    new java.awt.GridBagConstraints();
                constraintsDatabaseURLText.gridx = 2;
                constraintsDatabaseURLText.gridy = 3;
                constraintsDatabaseURLText.fill =
                        java.awt.GridBagConstraints.HORIZONTAL;
                constraintsDatabaseURLText.weightx = 1.0;
                constraintsDatabaseURLText.insets =
                        new java.awt.Insets(4, 4, 4, 4);
                getDriverPage().add(getDatabaseURLText(),
                                    constraintsDatabaseURLText);

                java.awt.GridBagConstraints constraintsUserText =
                    new java.awt.GridBagConstraints();
                constraintsUserText.gridx = 1;
                constraintsUserText.gridy = 4;
                constraintsUserText.gridwidth = 2;
                constraintsUserText.fill =
                        java.awt.GridBagConstraints.HORIZONTAL;
                constraintsUserText.weightx = 1.0;
                constraintsUserText.insets = new java.awt.Insets(4, 4, 4, 4);
                getDriverPage().add(getUserText(), constraintsUserText);

                java.awt.GridBagConstraints constraintsUserNameLabel =
                    new java.awt.GridBagConstraints();
                constraintsUserNameLabel.gridx = 0;
                constraintsUserNameLabel.gridy = 4;
                constraintsUserNameLabel.anchor =
                        java.awt.GridBagConstraints.WEST;
                constraintsUserNameLabel.insets =
                        new java.awt.Insets(4, 4, 4, 4);
                getDriverPage().add(getUserNameLabel(),
                                    constraintsUserNameLabel);

                java.awt.GridBagConstraints constraintsPasswordText =
                    new java.awt.GridBagConstraints();
                constraintsPasswordText.gridx = 1;
                constraintsPasswordText.gridy = 5;
                constraintsPasswordText.gridwidth = 2;
                constraintsPasswordText.fill =
                        java.awt.GridBagConstraints.HORIZONTAL;
                constraintsPasswordText.weightx = 1.0;
                constraintsPasswordText.insets =
                        new java.awt.Insets(4, 4, 4, 4);
                getDriverPage().add(getPasswordText(),
                                    constraintsPasswordText);

                java.awt.GridBagConstraints constraintsPasswordLabel =
                    new java.awt.GridBagConstraints();
                constraintsPasswordLabel.gridx = 0;
                constraintsPasswordLabel.gridy = 5;
                constraintsPasswordLabel.anchor =
                        java.awt.GridBagConstraints.WEST;
                constraintsPasswordLabel.insets =
                        new java.awt.Insets(4, 4, 4, 4);
                getDriverPage().add(getPasswordLabel(),
                                    constraintsPasswordLabel);

                java.awt.GridBagConstraints constraintsFillerPanel =
                    new java.awt.GridBagConstraints();
                constraintsFillerPanel.gridx = 0;
                constraintsFillerPanel.gridy = 7;
                constraintsFillerPanel.gridwidth = 3;
                constraintsFillerPanel.fill = java.awt.GridBagConstraints.BOTH;
                constraintsFillerPanel.weightx = 1.0;
                constraintsFillerPanel.weighty = 1.0;
                getDriverPage().add(getFillerPanel(), constraintsFillerPanel);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjDriverPage;
    }

    /**
     * Return the DriverURLText property value.
     * @return javax.swing.JTextField
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JTextField getDriverURLText() {
        if (ivjDriverURLText == null) {
            try {
                ivjDriverURLText = new javax.swing.JTextField();
                ivjDriverURLText.setName("DriverURLText");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjDriverURLText;
    }

    /**
     * Return the FillerPanel property value.
     * @return javax.swing.JPanel
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JPanel getFillerPanel() {
        if (ivjFillerPanel == null) {
            try {
                ivjFillerPanel = new javax.swing.JPanel();
                ivjFillerPanel.setName("FillerPanel");
                ivjFillerPanel.setLayout(null);
                ivjFillerPanel.setBackground(java.awt.SystemColor.control);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjFillerPanel;
    }

    /**
     * Return the FillerPanel1 property value.
     * @return javax.swing.JPanel
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JPanel getFillerPanel1() {
        if (ivjFillerPanel1 == null) {
            try {
                ivjFillerPanel1 = new javax.swing.JPanel();
                ivjFillerPanel1.setName("FillerPanel1");
                ivjFillerPanel1.setLayout(null);
                ivjFillerPanel1.setBackground(java.awt.SystemColor.control);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjFillerPanel1;
    }

    /**
     * Return the FillerPanel11 property value.
     * @return javax.swing.JPanel
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JPanel getFillerPanel11() {
        if (ivjFillerPanel11 == null) {
            try {
                ivjFillerPanel11 = new javax.swing.JPanel();
                ivjFillerPanel11.setName("FillerPanel11");
                ivjFillerPanel11.setLayout(null);
                ivjFillerPanel11.setBackground(java.awt.SystemColor.control);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjFillerPanel11;
    }

    /**
     * Return the FillerPanel2 property value.
     * @return javax.swing.JPanel
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JPanel getFillerPanel2() {
        if (ivjFillerPanel2 == null) {
            try {
                ivjFillerPanel2 = new javax.swing.JPanel();
                ivjFillerPanel2.setName("FillerPanel2");
                ivjFillerPanel2.setLayout(null);
                ivjFillerPanel2.setBackground(java.awt.SystemColor.control);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjFillerPanel2;
    }

    /**
     * Return the ForceCaseCheckBox property value.
     * @return javax.swing.JCheckBox
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JCheckBox getForceCaseCheckBox() {
        if (ivjForceCaseCheckBox == null) {
            try {
                ivjForceCaseCheckBox = new javax.swing.JCheckBox();
                ivjForceCaseCheckBox.setName("ForceCaseCheckBox");
                ivjForceCaseCheckBox.setText("Force Field Names to Upper Case");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjForceCaseCheckBox;
    }

    /**
     * Return the JTSCheckBox property value.
     * @return javax.swing.JCheckBox
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JCheckBox getJTSCheckBox() {
        if (ivjJTSCheckBox == null) {
            try {
                ivjJTSCheckBox = new javax.swing.JCheckBox();
                ivjJTSCheckBox.setName("JTSCheckBox");
                ivjJTSCheckBox.setText("External Transaction Control");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJTSCheckBox;
    }

    /**
     * Gets the login property (org.eclipse.persistence.sessions.JDBCLogin) value.
     * @return The login property value.
     * @see #setLogin
     */
    public DatabaseLogin getLogin() {
        return fieldLogin;
    }

    /**
     * Return the LoginBean property value.
     * @return org.eclipse.persistence.sessions.JDBCLogin
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    DatabaseLogin getLoginBean() {
        // user code begin {1}
        // user code end
        return ivjLoginBean;
    }

    /**
     * Return the NativeSequencingCheckbox property value.
     * @return javax.swing.JCheckBox
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JCheckBox getNativeSequencingCheckbox() {
        if (ivjNativeSequencingCheckbox == null) {
            try {
                ivjNativeSequencingCheckbox = new javax.swing.JCheckBox();
                ivjNativeSequencingCheckbox.setName("NativeSequencingCheckbox");
                ivjNativeSequencingCheckbox.setText("Native Sequencing");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjNativeSequencingCheckbox;
    }

    /**
     * Return the NativeSQLCheckbox property value.
     * @return javax.swing.JCheckBox
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JCheckBox getNativeSQLCheckbox() {
        if (ivjNativeSQLCheckbox == null) {
            try {
                ivjNativeSQLCheckbox = new javax.swing.JCheckBox();
                ivjNativeSQLCheckbox.setName("NativeSQLCheckbox");
                ivjNativeSQLCheckbox.setText("Native SQL");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjNativeSQLCheckbox;
    }

    /**
     * Return the OptimizeDataConversionCheckbox property value.
     * @return javax.swing.JCheckBox
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JCheckBox getOptimizeDataConversionCheckbox() {
        if (ivjOptimizeDataConversionCheckbox == null) {
            try {
                ivjOptimizeDataConversionCheckbox =
                        new javax.swing.JCheckBox();
                ivjOptimizeDataConversionCheckbox.setName("OptimizeDataConversionCheckbox");
                ivjOptimizeDataConversionCheckbox.setText("Optimize Data Conversion");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjOptimizeDataConversionCheckbox;
    }

    /**
     * Return the OtherPage property value.
     * @return javax.swing.JPanel
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JPanel getOtherPage() {
        if (ivjOtherPage == null) {
            try {
                ivjOtherPage = new javax.swing.JPanel();
                ivjOtherPage.setName("OtherPage");
                ivjOtherPage.setLayout(new java.awt.GridBagLayout());
                ivjOtherPage.setBackground(java.awt.SystemColor.control);

                java.awt.GridBagConstraints constraintsOptimizeDataConversionCheckbox =
                    new java.awt.GridBagConstraints();
                constraintsOptimizeDataConversionCheckbox.gridx = 1;
                constraintsOptimizeDataConversionCheckbox.gridy = 3;
                constraintsOptimizeDataConversionCheckbox.anchor =
                        java.awt.GridBagConstraints.NORTHWEST;
                constraintsOptimizeDataConversionCheckbox.insets =
                        new java.awt.Insets(2, 2, 2, 2);
                getOtherPage().add(getOptimizeDataConversionCheckbox(),
                                   constraintsOptimizeDataConversionCheckbox);

                java.awt.GridBagConstraints constraintsCreatorText =
                    new java.awt.GridBagConstraints();
                constraintsCreatorText.gridx = 1;
                constraintsCreatorText.gridy = 2;
                constraintsCreatorText.fill =
                        java.awt.GridBagConstraints.HORIZONTAL;
                constraintsCreatorText.weightx = 1.0;
                constraintsCreatorText.insets =
                        new java.awt.Insets(2, 2, 2, 2);
                getOtherPage().add(getCreatorText(), constraintsCreatorText);

                java.awt.GridBagConstraints constraintsCreatorLabel =
                    new java.awt.GridBagConstraints();
                constraintsCreatorLabel.gridx = 0;
                constraintsCreatorLabel.gridy = 2;
                constraintsCreatorLabel.anchor =
                        java.awt.GridBagConstraints.WEST;
                constraintsCreatorLabel.insets =
                        new java.awt.Insets(2, 2, 2, 2);
                getOtherPage().add(getCreatorLabel(), constraintsCreatorLabel);

                java.awt.GridBagConstraints constraintsFillerPanel11 =
                    new java.awt.GridBagConstraints();
                constraintsFillerPanel11.gridx = 0;
                constraintsFillerPanel11.gridy = 5;
                constraintsFillerPanel11.gridwidth = 2;
                constraintsFillerPanel11.gridheight = 2;
                constraintsFillerPanel11.fill =
                        java.awt.GridBagConstraints.BOTH;
                constraintsFillerPanel11.weightx = 1.0;
                constraintsFillerPanel11.weighty = 1.0;
                getOtherPage().add(getFillerPanel11(),
                                   constraintsFillerPanel11);

                java.awt.GridBagConstraints constraintsNativeSQLCheckbox =
                    new java.awt.GridBagConstraints();
                constraintsNativeSQLCheckbox.gridx = 1;
                constraintsNativeSQLCheckbox.gridy = 1;
                constraintsNativeSQLCheckbox.anchor =
                        java.awt.GridBagConstraints.WEST;
                constraintsNativeSQLCheckbox.insets =
                        new java.awt.Insets(2, 2, 2, 2);
                getOtherPage().add(getNativeSQLCheckbox(),
                                   constraintsNativeSQLCheckbox);

                java.awt.GridBagConstraints constraintsTrimStringsCheckbox =
                    new java.awt.GridBagConstraints();
                constraintsTrimStringsCheckbox.gridx = 1;
                constraintsTrimStringsCheckbox.gridy = 4;
                constraintsTrimStringsCheckbox.anchor =
                        java.awt.GridBagConstraints.WEST;
                constraintsTrimStringsCheckbox.insets =
                        new java.awt.Insets(2, 2, 2, 2);
                getOtherPage().add(getTrimStringsCheckbox(),
                                   constraintsTrimStringsCheckbox);

                java.awt.GridBagConstraints constraintsBatchWritingCheckBox =
                    new java.awt.GridBagConstraints();
                constraintsBatchWritingCheckBox.gridx = 0;
                constraintsBatchWritingCheckBox.gridy = 1;
                constraintsBatchWritingCheckBox.anchor =
                        java.awt.GridBagConstraints.NORTHWEST;
                constraintsBatchWritingCheckBox.insets =
                        new java.awt.Insets(2, 2, 2, 2);
                getOtherPage().add(getBatchWritingCheckBox(),
                                   constraintsBatchWritingCheckBox);

                java.awt.GridBagConstraints constraintsDirectConnectCheckBox =
                    new java.awt.GridBagConstraints();
                constraintsDirectConnectCheckBox.gridx = 0;
                constraintsDirectConnectCheckBox.gridy = 3;
                constraintsDirectConnectCheckBox.anchor =
                        java.awt.GridBagConstraints.NORTHWEST;
                constraintsDirectConnectCheckBox.insets =
                        new java.awt.Insets(2, 2, 2, 2);
                getOtherPage().add(getDirectConnectCheckBox(),
                                   constraintsDirectConnectCheckBox);

                java.awt.GridBagConstraints constraintsForceCaseCheckBox =
                    new java.awt.GridBagConstraints();
                constraintsForceCaseCheckBox.gridx = 0;
                constraintsForceCaseCheckBox.gridy = 4;
                constraintsForceCaseCheckBox.anchor =
                        java.awt.GridBagConstraints.WEST;
                constraintsForceCaseCheckBox.insets =
                        new java.awt.Insets(2, 2, 2, 2);
                getOtherPage().add(getForceCaseCheckBox(),
                                   constraintsForceCaseCheckBox);

                java.awt.GridBagConstraints constraintsJTSCheckBox =
                    new java.awt.GridBagConstraints();
                constraintsJTSCheckBox.gridx = 0;
                constraintsJTSCheckBox.gridy = 0;
                constraintsJTSCheckBox.anchor =
                        java.awt.GridBagConstraints.NORTHWEST;
                constraintsJTSCheckBox.insets =
                        new java.awt.Insets(2, 2, 2, 2);
                getOtherPage().add(getJTSCheckBox(), constraintsJTSCheckBox);

                java.awt.GridBagConstraints constraintsPoolingCheckBox =
                    new java.awt.GridBagConstraints();
                constraintsPoolingCheckBox.gridx = 1;
                constraintsPoolingCheckBox.gridy = 0;
                constraintsPoolingCheckBox.anchor =
                        java.awt.GridBagConstraints.NORTHWEST;
                constraintsPoolingCheckBox.insets =
                        new java.awt.Insets(2, 2, 2, 2);
                getOtherPage().add(getPoolingCheckBox(),
                                   constraintsPoolingCheckBox);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjOtherPage;
    }

    /**
     * Return the ParameterizedSQLCheckbox property value.
     * @return javax.swing.JCheckBox
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JCheckBox getParameterizedSQLCheckbox() {
        if (ivjParameterizedSQLCheckbox == null) {
            try {
                ivjParameterizedSQLCheckbox = new javax.swing.JCheckBox();
                ivjParameterizedSQLCheckbox.setName("ParameterizedSQLCheckbox");
                ivjParameterizedSQLCheckbox.setText("Parameterize All SQL");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjParameterizedSQLCheckbox;
    }

    /**
     * Return the PasswordLabel property value.
     * @return javax.swing.JLabel
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JLabel getPasswordLabel() {
        if (ivjPasswordLabel == null) {
            try {
                ivjPasswordLabel = new javax.swing.JLabel();
                ivjPasswordLabel.setName("PasswordLabel");
                ivjPasswordLabel.setText("Password:");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjPasswordLabel;
    }

    /**
     * Return the PasswordText property value.
     * @return javax.swing.JPasswordField
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JPasswordField getPasswordText() {
        if (ivjPasswordText == null) {
            try {
                ivjPasswordText = new javax.swing.JPasswordField();
                ivjPasswordText.setName("PasswordText");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjPasswordText;
    }

    /**
     * Return the PlatformChoice property value.
     * @return javax.swing.JComboBox
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JComboBox getPlatformChoice() {
        if (ivjPlatformChoice == null) {
            try {
                ivjPlatformChoice = new javax.swing.JComboBox();
                ivjPlatformChoice.setName("PlatformChoice");
                ivjPlatformChoice.setBackground(java.awt.SystemColor.window);
                ivjPlatformChoice.setEditable(true);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjPlatformChoice;
    }

    /**
     * Return the PoolingCheckBox property value.
     * @return javax.swing.JCheckBox
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JCheckBox getPoolingCheckBox() {
        if (ivjPoolingCheckBox == null) {
            try {
                ivjPoolingCheckBox = new javax.swing.JCheckBox();
                ivjPoolingCheckBox.setName("PoolingCheckBox");
                ivjPoolingCheckBox.setText("External Connection Pooling");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjPoolingCheckBox;
    }

    /**
     * Return the PreallocationSizeLabel property value.
     * @return javax.swing.JLabel
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JLabel getPreallocationSizeLabel() {
        if (ivjPreallocationSizeLabel == null) {
            try {
                ivjPreallocationSizeLabel = new javax.swing.JLabel();
                ivjPreallocationSizeLabel.setName("PreallocationSizeLabel");
                ivjPreallocationSizeLabel.setText("Sequence Preallocation Size:");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjPreallocationSizeLabel;
    }

    /**
     * Return the PropertiesBook property value.
     * @return javax.swing.JTabbedPane
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JTabbedPane getPropertiesBook() {
        if (ivjPropertiesBook == null) {
            try {
                ivjPropertiesBook = new javax.swing.JTabbedPane();
                ivjPropertiesBook.setName("PropertiesBook");
                ivjPropertiesBook.setTabPlacement(javax.swing.JTabbedPane.RIGHT);
                ivjPropertiesBook.setBackground(java.awt.SystemColor.control);
                ivjPropertiesBook.insertTab("Driver", null, getDriverPage(),
                                            null, 0);
                ivjPropertiesBook.insertTab("Binding", null, getBindingPage(),
                                            null, 1);
                ivjPropertiesBook.insertTab("Sequencing", null,
                                            getSequencingPage(), null, 2);
                ivjPropertiesBook.insertTab("Other", null, getOtherPage(),
                                            null, 3);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjPropertiesBook;
    }

    /**
     * Return the SequenceCounterNameLabel property value.
     * @return javax.swing.JLabel
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JLabel getSequenceCounterNameLabel() {
        if (ivjSequenceCounterNameLabel == null) {
            try {
                ivjSequenceCounterNameLabel = new javax.swing.JLabel();
                ivjSequenceCounterNameLabel.setName("SequenceCounterNameLabel");
                ivjSequenceCounterNameLabel.setText("Sequence Counter Name:");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjSequenceCounterNameLabel;
    }

    /**
     * Return the SequenceCounterNameText property value.
     * @return javax.swing.JTextField
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JTextField getSequenceCounterNameText() {
        if (ivjSequenceCounterNameText == null) {
            try {
                ivjSequenceCounterNameText = new javax.swing.JTextField();
                ivjSequenceCounterNameText.setName("SequenceCounterNameText");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjSequenceCounterNameText;
    }

    /**
     * Return the SequenceFieldNameLabel property value.
     * @return javax.swing.JLabel
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JLabel getSequenceFieldNameLabel() {
        if (ivjSequenceFieldNameLabel == null) {
            try {
                ivjSequenceFieldNameLabel = new javax.swing.JLabel();
                ivjSequenceFieldNameLabel.setName("SequenceFieldNameLabel");
                ivjSequenceFieldNameLabel.setText("Sequence Field Name:");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjSequenceFieldNameLabel;
    }

    /**
     * Return the SequenceFieldNameText property value.
     * @return javax.swing.JTextField
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JTextField getSequenceFieldNameText() {
        if (ivjSequenceFieldNameText == null) {
            try {
                ivjSequenceFieldNameText = new javax.swing.JTextField();
                ivjSequenceFieldNameText.setName("SequenceFieldNameText");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjSequenceFieldNameText;
    }

    /**
     * Return the SequencePreallocationSizeLabel property value.
     * @return javax.swing.JTextField
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JTextField getSequencePreallocationSizeText() {
        if (ivjSequencePreallocationSizeText == null) {
            try {
                ivjSequencePreallocationSizeText =
                        new javax.swing.JTextField();
                ivjSequencePreallocationSizeText.setName("SequencePreallocationSizeText");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjSequencePreallocationSizeText;
    }

    /**
     * Return the SequenceTableNameLabel property value.
     * @return javax.swing.JLabel
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JLabel getSequenceTableNameLabel() {
        if (ivjSequenceTableNameLabel == null) {
            try {
                ivjSequenceTableNameLabel = new javax.swing.JLabel();
                ivjSequenceTableNameLabel.setName("SequenceTableNameLabel");
                ivjSequenceTableNameLabel.setText("Sequence Table Name:");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjSequenceTableNameLabel;
    }

    /**
     * Return the SequenceTableNameText property value.
     * @return javax.swing.JTextField
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JTextField getSequenceTableNameText() {
        if (ivjSequenceTableNameText == null) {
            try {
                ivjSequenceTableNameText = new javax.swing.JTextField();
                ivjSequenceTableNameText.setName("SequenceTableNameText");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjSequenceTableNameText;
    }

    /**
     * Return the SequencingPage property value.
     * @return javax.swing.JPanel
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JPanel getSequencingPage() {
        if (ivjSequencingPage == null) {
            try {
                ivjSequencingPage = new javax.swing.JPanel();
                ivjSequencingPage.setName("SequencingPage");
                ivjSequencingPage.setLayout(new java.awt.GridBagLayout());
                ivjSequencingPage.setBackground(java.awt.SystemColor.control);

                java.awt.GridBagConstraints constraintsNativeSequencingCheckbox =
                    new java.awt.GridBagConstraints();
                constraintsNativeSequencingCheckbox.gridx = 0;
                constraintsNativeSequencingCheckbox.gridy = 0;
                constraintsNativeSequencingCheckbox.anchor =
                        java.awt.GridBagConstraints.NORTHWEST;
                constraintsNativeSequencingCheckbox.insets =
                        new java.awt.Insets(2, 2, 2, 2);
                getSequencingPage().add(getNativeSequencingCheckbox(),
                                        constraintsNativeSequencingCheckbox);

                java.awt.GridBagConstraints constraintsSequencePreallocationSizeText =
                    new java.awt.GridBagConstraints();
                constraintsSequencePreallocationSizeText.gridx = 1;
                constraintsSequencePreallocationSizeText.gridy = 1;
                constraintsSequencePreallocationSizeText.fill =
                        java.awt.GridBagConstraints.HORIZONTAL;
                constraintsSequencePreallocationSizeText.weightx = 1.0;
                constraintsSequencePreallocationSizeText.insets =
                        new java.awt.Insets(2, 2, 2, 2);
                getSequencingPage().add(getSequencePreallocationSizeText(),
                                        constraintsSequencePreallocationSizeText);

                java.awt.GridBagConstraints constraintsPreallocationSizeLabel =
                    new java.awt.GridBagConstraints();
                constraintsPreallocationSizeLabel.gridx = 0;
                constraintsPreallocationSizeLabel.gridy = 1;
                constraintsPreallocationSizeLabel.anchor =
                        java.awt.GridBagConstraints.WEST;
                constraintsPreallocationSizeLabel.insets =
                        new java.awt.Insets(2, 2, 2, 2);
                getSequencingPage().add(getPreallocationSizeLabel(),
                                        constraintsPreallocationSizeLabel);

                java.awt.GridBagConstraints constraintsFillerPanel1 =
                    new java.awt.GridBagConstraints();
                constraintsFillerPanel1.gridx = 0;
                constraintsFillerPanel1.gridy = 5;
                constraintsFillerPanel1.gridwidth = 2;
                constraintsFillerPanel1.gridheight = 2;
                constraintsFillerPanel1.fill =
                        java.awt.GridBagConstraints.BOTH;
                constraintsFillerPanel1.weightx = 1.0;
                constraintsFillerPanel1.weighty = 1.0;
                getSequencingPage().add(getFillerPanel1(),
                                        constraintsFillerPanel1);

                java.awt.GridBagConstraints constraintsSequenceTableNameText =
                    new java.awt.GridBagConstraints();
                constraintsSequenceTableNameText.gridx = 1;
                constraintsSequenceTableNameText.gridy = 2;
                constraintsSequenceTableNameText.fill =
                        java.awt.GridBagConstraints.HORIZONTAL;
                constraintsSequenceTableNameText.weightx = 1.0;
                constraintsSequenceTableNameText.insets =
                        new java.awt.Insets(2, 2, 2, 2);
                getSequencingPage().add(getSequenceTableNameText(),
                                        constraintsSequenceTableNameText);

                java.awt.GridBagConstraints constraintsSequenceTableNameLabel =
                    new java.awt.GridBagConstraints();
                constraintsSequenceTableNameLabel.gridx = 0;
                constraintsSequenceTableNameLabel.gridy = 2;
                constraintsSequenceTableNameLabel.anchor =
                        java.awt.GridBagConstraints.WEST;
                constraintsSequenceTableNameLabel.insets =
                        new java.awt.Insets(2, 2, 2, 2);
                getSequencingPage().add(getSequenceTableNameLabel(),
                                        constraintsSequenceTableNameLabel);

                java.awt.GridBagConstraints constraintsSequenceCounterNameText =
                    new java.awt.GridBagConstraints();
                constraintsSequenceCounterNameText.gridx = 1;
                constraintsSequenceCounterNameText.gridy = 4;
                constraintsSequenceCounterNameText.fill =
                        java.awt.GridBagConstraints.HORIZONTAL;
                constraintsSequenceCounterNameText.weightx = 1.0;
                constraintsSequenceCounterNameText.insets =
                        new java.awt.Insets(2, 2, 2, 2);
                getSequencingPage().add(getSequenceCounterNameText(),
                                        constraintsSequenceCounterNameText);

                java.awt.GridBagConstraints constraintsSequenceFieldNameLabel =
                    new java.awt.GridBagConstraints();
                constraintsSequenceFieldNameLabel.gridx = 0;
                constraintsSequenceFieldNameLabel.gridy = 3;
                constraintsSequenceFieldNameLabel.anchor =
                        java.awt.GridBagConstraints.WEST;
                constraintsSequenceFieldNameLabel.insets =
                        new java.awt.Insets(2, 2, 2, 2);
                getSequencingPage().add(getSequenceFieldNameLabel(),
                                        constraintsSequenceFieldNameLabel);

                java.awt.GridBagConstraints constraintsSequenceCounterNameLabel =
                    new java.awt.GridBagConstraints();
                constraintsSequenceCounterNameLabel.gridx = 0;
                constraintsSequenceCounterNameLabel.gridy = 4;
                constraintsSequenceCounterNameLabel.anchor =
                        java.awt.GridBagConstraints.WEST;
                constraintsSequenceCounterNameLabel.insets =
                        new java.awt.Insets(2, 2, 2, 2);
                getSequencingPage().add(getSequenceCounterNameLabel(),
                                        constraintsSequenceCounterNameLabel);

                java.awt.GridBagConstraints constraintsSequenceFieldNameText =
                    new java.awt.GridBagConstraints();
                constraintsSequenceFieldNameText.gridx = 1;
                constraintsSequenceFieldNameText.gridy = 3;
                constraintsSequenceFieldNameText.fill =
                        java.awt.GridBagConstraints.HORIZONTAL;
                constraintsSequenceFieldNameText.weightx = 1.0;
                constraintsSequenceFieldNameText.insets =
                        new java.awt.Insets(2, 2, 2, 2);
                getSequencingPage().add(getSequenceFieldNameText(),
                                        constraintsSequenceFieldNameText);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjSequencingPage;
    }

    /**
     * Return the StatementCacheSizeLabel property value.
     * @return javax.swing.JLabel
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JLabel getStatementCacheSizeLabel() {
        if (ivjStatementCacheSizeLabel == null) {
            try {
                ivjStatementCacheSizeLabel = new javax.swing.JLabel();
                ivjStatementCacheSizeLabel.setName("StatementCacheSizeLabel");
                ivjStatementCacheSizeLabel.setText("Statement Cache Size:");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjStatementCacheSizeLabel;
    }

    /**
     * Return the StatementCacheSizeText property value.
     * @return javax.swing.JTextField
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JTextField getStatementCacheSizeText() {
        if (ivjStatementCacheSizeText == null) {
            try {
                ivjStatementCacheSizeText = new javax.swing.JTextField();
                ivjStatementCacheSizeText.setName("StatementCacheSizeText");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjStatementCacheSizeText;
    }

    /**
     * Return the CLOBBindingCheckbox property value.
     * @return javax.swing.JCheckBox
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JCheckBox getStringBindingCheckbox() {
        if (ivjStringBindingCheckbox == null) {
            try {
                ivjStringBindingCheckbox = new javax.swing.JCheckBox();
                ivjStringBindingCheckbox.setName("StringBindingCheckbox");
                ivjStringBindingCheckbox.setText("String Binding");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjStringBindingCheckbox;
    }

    /**
     * Return the StringBindSizeLable property value.
     * @return javax.swing.JLabel
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JLabel getStringBindSizeLable() {
        if (ivjStringBindSizeLable == null) {
            try {
                ivjStringBindSizeLable = new javax.swing.JLabel();
                ivjStringBindSizeLable.setName("StringBindSizeLable");
                ivjStringBindSizeLable.setText("String Bind Size:");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjStringBindSizeLable;
    }

    /**
     * Return the CLOBBindSizeText property value.
     * @return javax.swing.JTextField
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JTextField getStringBindSizeText() {
        if (ivjStringBindSizeText == null) {
            try {
                ivjStringBindSizeText = new javax.swing.JTextField();
                ivjStringBindSizeText.setName("StringBindSizeText");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjStringBindSizeText;
    }

    /**
     * Return the TrimStringsCheckbox property value.
     * @return javax.swing.JCheckBox
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JCheckBox getTrimStringsCheckbox() {
        if (ivjTrimStringsCheckbox == null) {
            try {
                ivjTrimStringsCheckbox = new javax.swing.JCheckBox();
                ivjTrimStringsCheckbox.setName("TrimStringsCheckbox");
                ivjTrimStringsCheckbox.setText("Trim Strings");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjTrimStringsCheckbox;
    }

    /**
     * Return the UserNameLabel property value.
     * @return javax.swing.JLabel
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JLabel getUserNameLabel() {
        if (ivjUserNameLabel == null) {
            try {
                ivjUserNameLabel = new javax.swing.JLabel();
                ivjUserNameLabel.setName("UserNameLabel");
                ivjUserNameLabel.setText("User Name:");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjUserNameLabel;
    }

    /**
     * Return the UserText property value.
     * @return javax.swing.JTextField
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JTextField getUserText() {
        if (ivjUserText == null) {
            try {
                ivjUserText = new javax.swing.JTextField();
                ivjUserText.setName("UserText");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjUserText;
    }


    /**
     * Called whenever the part throws an exception.
     * @param exception java.lang.Throwable
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
    void initConnections() throws java.lang.Exception {
        // user code begin {1}
        // user code end
        this.addPropertyChangeListener(ivjEventHandler);
        getDriverURLText().addKeyListener(ivjEventHandler);
        getDatabaseURLText().addKeyListener(ivjEventHandler);
        getUserText().addKeyListener(ivjEventHandler);
        getSequencePreallocationSizeText().addKeyListener(ivjEventHandler);
        getBridgeChoice().addItemListener(ivjEventHandler);
        getDriverChoice().addItemListener(ivjEventHandler);
        getPlatformChoice().addItemListener(ivjEventHandler);
        getSequenceCounterNameText().addKeyListener(ivjEventHandler);
        getSequenceFieldNameText().addKeyListener(ivjEventHandler);
        getSequenceTableNameText().addKeyListener(ivjEventHandler);
        getCreatorText().addKeyListener(ivjEventHandler);
        getNativeSQLCheckbox().addActionListener(ivjEventHandler);
        getNativeSequencingCheckbox().addActionListener(ivjEventHandler);
        getBlobStreamBinding().addActionListener(ivjEventHandler);
        getBlobBindingCheckbox().addActionListener(ivjEventHandler);
        getParameterizedSQLCheckbox().addActionListener(ivjEventHandler);
        getOptimizeDataConversionCheckbox().addActionListener(ivjEventHandler);
        getCacheStatementsCheckbox().addActionListener(ivjEventHandler);
        getStringBindSizeText().addKeyListener(ivjEventHandler);
        getStringBindingCheckbox().addActionListener(ivjEventHandler);
        getStatementCacheSizeText().addKeyListener(ivjEventHandler);
        getTrimStringsCheckbox().addActionListener(ivjEventHandler);
        getPasswordText().addCaretListener(ivjEventHandler);
        getBatchWritingCheckBox().addActionListener(ivjEventHandler);
        getDirectConnectCheckBox().addActionListener(ivjEventHandler);
        getForceCaseCheckBox().addActionListener(ivjEventHandler);
        connPtoP1SetTarget();
        connPtoP3SetTarget();
        connPtoP5SetTarget();
        connPtoP7SetTarget();
        connPtoP12SetTarget();
        connPtoP2SetTarget();
        connPtoP6SetTarget();
        connPtoP14SetTarget();
        connPtoP15SetTarget();
        connPtoP16SetTarget();
        connPtoP18SetTarget();
        connPtoP9SetTarget();
        connPtoP11SetTarget();
        connPtoP8SetTarget();
        connPtoP10SetTarget();
        connPtoP17SetTarget();
        connPtoP13SetTarget();
        connPtoP19SetTarget();
        connPtoP21SetTarget();
        connPtoP22SetTarget();
        connPtoP20SetTarget();
        connPtoP23SetTarget();
        connPtoP4SetTarget();
        connPtoP24SetTarget();
        connPtoP25SetTarget();
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
            setName("LoginEditorPanel");
            setLayout(new java.awt.GridBagLayout());
            setBackground(java.awt.SystemColor.control);
            setSize(590, 428);

            java.awt.GridBagConstraints constraintsPropertiesBook =
                new java.awt.GridBagConstraints();
            constraintsPropertiesBook.gridx = 0;
            constraintsPropertiesBook.gridy = 0;
            constraintsPropertiesBook.gridwidth = 3;
            constraintsPropertiesBook.fill = java.awt.GridBagConstraints.BOTH;
            constraintsPropertiesBook.weightx = 1.0;
            constraintsPropertiesBook.weighty = 1.0;
            constraintsPropertiesBook.insets = new java.awt.Insets(0, 2, 2, 2);
            add(getPropertiesBook(), constraintsPropertiesBook);
            initConnections();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }

        // user code begin {2}
        reset();
        // user code end
    }

    public void passwordChanged() {
        password = new String(getPasswordText().getPassword());
        //set the encrypted password will cause toplink to use the plain text password as is
        getLogin().setEncryptedPassword(password);
    }

    public void reset() {
        resetLogin();
        resetSequenceButtons();
        resetStringButtons();
    }

    /**
     * Set default login information.
     */
    public void resetLogin() {
        getDriverChoice().removeAllItems();
        getDriverChoice().addItem("oracle.jdbc.OracleDriver");
        getDriverChoice().addItem("COM.ibm.db2.jdbc.app.DB2Driver");
        getDriverChoice().addItem("COM.ibm.db2.jdbc.net.DB2Driver");
        getDriverChoice().addItem("com.ibm.db2.jcc.DB2Driver");
        getDriverChoice().addItem("com.sybase.jdbc2.jdbc.SybDriver");
        getDriverChoice().addItem("com.oracle.ias.jdbc.db2.DB2Driver");
        getDriverChoice().addItem("org.apache.derby.EmbeddedDriver");
        getDriverChoice().addItem("com.oracle.ias.jdbc.sybase.SybaseDriver");
        getDriverChoice().addItem("com.oracle.ias.jdbc.sqlserver.SQLServerDriver");
        getDriverChoice().addItem("com.microsoft.jdbc.sqlserver.SQLServerDriver");
        getDriverChoice().addItem("weblogic.jdbc.mssqlserver4.Driver");
        getDriverChoice().addItem("com.mysql.jdbc.Driver");
        getDriverChoice().addItem("sun.jdbc.odbc.JdbcOdbcDriver");

        getPlatformChoice().removeAllItems();
        getPlatformChoice().addItem("org.eclipse.persistence.platform.database.OraclePlatform");
        getPlatformChoice().addItem("org.eclipse.persistence.platform.database.oracle.Oracle8Platform");
        getPlatformChoice().addItem("org.eclipse.persistence.platform.database.oracle.Oracle9Platform");
        getPlatformChoice().addItem("org.eclipse.persistence.platform.database.DB2Platform");
        getPlatformChoice().addItem("org.eclipse.persistence.platform.database.InformixPlatform");
        getPlatformChoice().addItem("org.eclipse.persistence.platform.database.DerbyPlatform");
        getPlatformChoice().addItem("org.eclipse.persistence.platform.database.JavaDBPlatform");
        getPlatformChoice().addItem("org.eclipse.persistence.platform.database.HSQLPlatform");
        getPlatformChoice().addItem("org.eclipse.persistence.platform.database.PostgreSQLPlatform");
        getPlatformChoice().addItem("org.eclipse.persistence.platform.database.SybasePlatform");
        getPlatformChoice().addItem("org.eclipse.persistence.platform.database.SQLAnywherePlatform");
        getPlatformChoice().addItem("org.eclipse.persistence.platform.database.SQLServerPlatform");
        getPlatformChoice().addItem("org.eclipse.persistence.platform.database.TimesTenPlatform");
        getPlatformChoice().addItem("org.eclipse.persistence.platform.database.TimesTen7Platform");
        getPlatformChoice().addItem("org.eclipse.persistence.platform.database.AccessPlatform");
        getPlatformChoice().addItem("org.eclipse.persistence.platform.database.MySQLPlatform");
        getPlatformChoice().addItem("org.eclipse.persistence.platform.database.DatabasePlatform");

        getBridgeChoice().removeAllItems();
        getBridgeChoice().addItem("Oracle thin");
        getBridgeChoice().addItem("Oracle 8 OCI");
        getBridgeChoice().addItem("DB2 (App)");
        getBridgeChoice().addItem("DB2 (Net)");
        getBridgeChoice().addItem("DB2 (Universal Driver)");
        getBridgeChoice().addItem("Sybase JConnect");
        getBridgeChoice().addItem("DataDirect for DB2");
        getBridgeChoice().addItem("DataDirect for Sybase");
        getBridgeChoice().addItem("DataDirect for SQLServer");
        getBridgeChoice().addItem("MS JDBC");
        getBridgeChoice().addItem("Weblogic Thin for SQLServer");
        getBridgeChoice().addItem("Sun JDBC-ODBC");

        getBridgeChoice().setSelectedItem(bridge);
        getPasswordText().setText(password);
    }

    public void resetSequenceButtons() {
        getSequenceCounterNameText().setEnabled(!getNativeSequencingCheckbox().isSelected());
        getSequenceFieldNameText().setEnabled(!getNativeSequencingCheckbox().isSelected());
        getSequenceTableNameText().setEnabled(!getNativeSequencingCheckbox().isSelected());
        getSequencePreallocationSizeText().setText(new Integer(getLogin().getDefaultSequence().getPreallocationSize()).toString());
    }

    public void resetStringButtons() {
        getBlobStreamBinding().setEnabled(getBlobBindingCheckbox().isSelected());
        getStringBindSizeText().setEnabled(getStringBindingCheckbox().isSelected());
    }

    /**
     * Sets the login property (org.eclipse.persistence.sessions.JDBCLogin) value.
     * @param login The new value for the property.
     * @see #getLogin
     */
    public void setLogin(DatabaseLogin login) {
        DatabaseLogin oldValue = fieldLogin;
        fieldLogin = login;
        firePropertyChange("login", oldValue, login);
    }

    /**
     * Set the LoginBean to a new value.
     * @param newValue org.eclipse.persistence.sessions.JDBCLogin
     */
    private

    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    void setLoginBean(DatabaseLogin newValue) {
        if (ivjLoginBean != newValue) {
            try {
                DatabaseLogin oldValue = getLoginBean();
                ivjLoginBean = newValue;
                connPtoP1SetSource();
                connPtoP3SetTarget();
                connPtoP5SetTarget();
                connPtoP7SetTarget();
                connPtoP12SetTarget();
                connPtoP2SetTarget();
                connPtoP6SetTarget();
                connPtoP14SetTarget();
                connPtoP15SetTarget();
                connPtoP16SetTarget();
                connPtoP18SetTarget();
                connPtoP9SetTarget();
                connPtoP11SetTarget();
                connPtoP8SetTarget();
                connPtoP10SetTarget();
                connPtoP17SetTarget();
                connPtoP13SetTarget();
                connPtoP19SetTarget();
                connPtoP21SetTarget();
                connPtoP22SetTarget();
                connPtoP20SetTarget();
                connPtoP23SetTarget();
                connPtoP4SetTarget();
                connPtoP24SetTarget();
                connPtoP25SetTarget();
                firePropertyChange("login", oldValue, newValue);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        ;
        // user code begin {3}
        // user code end
    }

    class IvjEventHandler implements java.awt.event.ActionListener,
                                     java.awt.event.ItemListener,
                                     java.awt.event.KeyListener,
                                     java.beans.PropertyChangeListener,
                                     javax.swing.event.CaretListener {
        public void actionPerformed(java.awt.event.ActionEvent e) {
            if (e.getSource() ==
                LoginEditorPanel.this.getNativeSQLCheckbox()) {
                connPtoP9SetSource();
            }
            if (e.getSource() ==
                LoginEditorPanel.this.getNativeSequencingCheckbox()) {
                connPtoP11SetSource();
            }
            if (e.getSource() ==
                LoginEditorPanel.this.getBlobStreamBinding()) {
                connPtoP8SetSource();
            }
            if (e.getSource() ==
                LoginEditorPanel.this.getBlobBindingCheckbox()) {
                connPtoP10SetSource();
            }
            if (e.getSource() ==
                LoginEditorPanel.this.getParameterizedSQLCheckbox()) {
                connPtoP17SetSource();
            }
            if (e.getSource() ==
                LoginEditorPanel.this.getOptimizeDataConversionCheckbox()) {
                connPtoP13SetSource();
            }
            if (e.getSource() ==
                LoginEditorPanel.this.getCacheStatementsCheckbox()) {
                connPtoP19SetSource();
            }
            if (e.getSource() ==
                LoginEditorPanel.this.getStringBindingCheckbox()) {
                connPtoP22SetSource();
            }
            if (e.getSource() ==
                LoginEditorPanel.this.getNativeSequencingCheckbox()) {
                connEtoC2(e);
            }
            if (e.getSource() ==
                LoginEditorPanel.this.getStringBindingCheckbox()) {
                connEtoC4(e);
            }
            if (e.getSource() ==
                LoginEditorPanel.this.getTrimStringsCheckbox()) {
                connPtoP23SetSource();
            }
            if (e.getSource() ==
                LoginEditorPanel.this.getBlobBindingCheckbox()) {
                connEtoC5(e);
            }
            if (e.getSource() ==
                LoginEditorPanel.this.getBatchWritingCheckBox()) {
                connPtoP4SetSource();
            }
            if (e.getSource() ==
                LoginEditorPanel.this.getDirectConnectCheckBox()) {
                connPtoP24SetSource();
            }
            if (e.getSource() ==
                LoginEditorPanel.this.getForceCaseCheckBox()) {
                connPtoP25SetSource();
            }
        }

        public void caretUpdate(javax.swing.event.CaretEvent e) {
            if (e.getSource() == LoginEditorPanel.this.getPasswordText()) {
                connEtoC7(e);
            }
        }

        public void itemStateChanged(java.awt.event.ItemEvent e) {
            if (e.getSource() == LoginEditorPanel.this.getBridgeChoice()) {
                connEtoC3(e);
            }
            if (e.getSource() == LoginEditorPanel.this.getDriverChoice()) {
                connPtoP2SetSource();
            }
            if (e.getSource() == LoginEditorPanel.this.getPlatformChoice()) {
                connPtoP6SetSource();
            }
        }

        public void keyPressed(java.awt.event.KeyEvent e) {
            if (e.getSource() == LoginEditorPanel.this.getDriverURLText()) {
                connPtoP3SetSource();
            }
            if (e.getSource() == LoginEditorPanel.this.getDatabaseURLText()) {
                connPtoP5SetSource();
            }
            if (e.getSource() == LoginEditorPanel.this.getUserText()) {
                connPtoP7SetSource();
            }
            if (e.getSource() ==
                LoginEditorPanel.this.getSequencePreallocationSizeText()) {
                connPtoP12SetSource();
            }
            if (e.getSource() ==
                LoginEditorPanel.this.getSequenceCounterNameText()) {
                connPtoP14SetSource();
            }
            if (e.getSource() ==
                LoginEditorPanel.this.getSequenceFieldNameText()) {
                connPtoP15SetSource();
            }
            if (e.getSource() ==
                LoginEditorPanel.this.getSequenceTableNameText()) {
                connPtoP16SetSource();
            }
            if (e.getSource() == LoginEditorPanel.this.getCreatorText()) {
                connPtoP18SetSource();
            }
            if (e.getSource() ==
                LoginEditorPanel.this.getStringBindSizeText()) {
                connPtoP21SetSource();
            }
            if (e.getSource() ==
                LoginEditorPanel.this.getStatementCacheSizeText()) {
                connPtoP20SetSource();
            }
        }

        public void keyReleased(java.awt.event.KeyEvent e) {
            if (e.getSource() == LoginEditorPanel.this.getDriverURLText()) {
                connPtoP3SetSource();
            }
            if (e.getSource() == LoginEditorPanel.this.getDatabaseURLText()) {
                connPtoP5SetSource();
            }
            if (e.getSource() == LoginEditorPanel.this.getUserText()) {
                connPtoP7SetSource();
            }
            if (e.getSource() ==
                LoginEditorPanel.this.getSequencePreallocationSizeText()) {
                connPtoP12SetSource();
            }
            if (e.getSource() ==
                LoginEditorPanel.this.getSequenceCounterNameText()) {
                connPtoP14SetSource();
            }
            if (e.getSource() ==
                LoginEditorPanel.this.getSequenceFieldNameText()) {
                connPtoP15SetSource();
            }
            if (e.getSource() ==
                LoginEditorPanel.this.getSequenceTableNameText()) {
                connPtoP16SetSource();
            }
            if (e.getSource() == LoginEditorPanel.this.getCreatorText()) {
                connPtoP18SetSource();
            }
            if (e.getSource() ==
                LoginEditorPanel.this.getStringBindSizeText()) {
                connPtoP21SetSource();
            }
            if (e.getSource() ==
                LoginEditorPanel.this.getStatementCacheSizeText()) {
                connPtoP20SetSource();
            }
        }

        public void keyTyped(java.awt.event.KeyEvent e) {
            if (e.getSource() == LoginEditorPanel.this.getDriverURLText()) {
                connPtoP3SetSource();
            }
            if (e.getSource() == LoginEditorPanel.this.getDatabaseURLText()) {
                connPtoP5SetSource();
            }
            if (e.getSource() == LoginEditorPanel.this.getUserText()) {
                connPtoP7SetSource();
            }
            if (e.getSource() ==
                LoginEditorPanel.this.getSequencePreallocationSizeText()) {
                connPtoP12SetSource();
            }
            if (e.getSource() ==
                LoginEditorPanel.this.getSequenceCounterNameText()) {
                connPtoP14SetSource();
            }
            if (e.getSource() ==
                LoginEditorPanel.this.getSequenceFieldNameText()) {
                connPtoP15SetSource();
            }
            if (e.getSource() ==
                LoginEditorPanel.this.getSequenceTableNameText()) {
                connPtoP16SetSource();
            }
            if (e.getSource() == LoginEditorPanel.this.getCreatorText()) {
                connPtoP18SetSource();
            }
            if (e.getSource() ==
                LoginEditorPanel.this.getStringBindSizeText()) {
                connPtoP21SetSource();
            }
            if (e.getSource() ==
                LoginEditorPanel.this.getStatementCacheSizeText()) {
                connPtoP20SetSource();
            }
        }

        public void propertyChange(java.beans.PropertyChangeEvent evt) {
            if ((evt.getSource() == LoginEditorPanel.this) &&
                (evt.getPropertyName().equals("login"))) {
                connPtoP1SetTarget();
            }
        }
    }
}
