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
package org.eclipse.persistence.testing.tests.feature;

import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Properties;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.databaseaccess.*;
import org.eclipse.persistence.platform.database.OraclePlatform;

public class DatabaseLoginCodeCoverageTest extends AutoVerifyTestCase {
    private DatabaseLogin login;
    private String testFailures;

    public void setup() {
        login = new DatabaseLogin();
        testFailures = "";
    }

    public void test() {
        runTests();
    }

    public void verify() {
        if (testFailures.length() > 0) {
            throw new TestErrorException("Tests failures from DatabaseLogin: " + testFailures);
        }
    }

    private void addToTestFailures(String fail) {
        testFailures += ("\n - " + fail);
    }

    public void runTests() {
        ////////////////////////////////////////////////////
        login.bindAllParameters();
        if (!login.getShouldBindAllParameters()) {
            addToTestFailures("bindAllParameters");
        }

        ////////////////////////////////////////////////////
        login.dontBindAllParameters();
        if (login.getShouldBindAllParameters()) {
            addToTestFailures("dontBindAllParameters");
        }

        ////////////////////////////////////////////////////
        login.cacheAllStatements();
        if (!login.getShouldCacheAllStatements()) {
            addToTestFailures("cacheAllStatements");
        }

        ////////////////////////////////////////////////////
        login.dontCacheAllStatements();
        if (login.getShouldCacheAllStatements()) {
            addToTestFailures("dontCacheAllStatements");
        }

        ////////////////////////////////////////////////////
        login.optimizeDataConversion();
        if (!login.getShouldOptimizeDataConversion()) {
            addToTestFailures("optimizeDataConversion");
        }

        ////////////////////////////////////////////////////
        login.dontOptimizeDataConversion();
        if (login.getShouldOptimizeDataConversion()) {
            addToTestFailures("dontOptimizeDataConversion");
        }

        ////////////////////////////////////////////////////
        login.useBatchWriting();
        if (!login.shouldUseBatchWriting()) {
            addToTestFailures("useBatchWriting");
        }

        ////////////////////////////////////////////////////
        login.dontUseBatchWriting();
        if (login.shouldUseBatchWriting()) {
            addToTestFailures("dontUseBatchWriting");
        }

        ////////////////////////////////////////////////////
        login.useByteArrayBinding();
        if (!login.shouldUseByteArrayBinding()) {
            addToTestFailures("useByteArrayBinding");
        }

        ////////////////////////////////////////////////////
        login.dontUseByteArrayBinding();
        if (login.shouldUseByteArrayBinding()) {
            addToTestFailures("dontUseByteArrayBinding");
        }

        ////////////////////////////////////////////////////
        login.useExternalConnectionPooling();
        if (!login.shouldUseExternalConnectionPooling()) {
            addToTestFailures("useExternalConnectionPooling");
        }

        ////////////////////////////////////////////////////
        login.dontUseExternalConnectionPooling();
        if (login.shouldUseExternalConnectionPooling()) {
            addToTestFailures("dontUseExternalConnectionPooling");
        }

        ////////////////////////////////////////////////////
        login.useJDBCBatchWriting();
        if (!login.shouldUseJDBCBatchWriting()) {
            addToTestFailures("useJDBCBatchWriting");
        }

        ////////////////////////////////////////////////////
        login.dontUseJDBCBatchWriting();
        if (login.shouldUseJDBCBatchWriting()) {
            addToTestFailures("dontUseJDBCBatchWriting");
        }

        ////////////////////////////////////////////////////
        login.useNativeSequencing();
        if (!login.shouldUseNativeSequencing()) {
            addToTestFailures("useNativeSequencing");
        }

        ////////////////////////////////////////////////////
        login.useNativeSQL();
        if (!login.shouldUseNativeSQL()) {
            addToTestFailures("useNativeSQL");
        }

        ////////////////////////////////////////////////////
        login.dontUseNativeSQL();
        if (login.shouldUseNativeSQL()) {
            addToTestFailures("dontUseNativeSQL");
        }

        ////////////////////////////////////////////////////
        login.useStreamsForBinding();
        if (!login.shouldUseStreamsForBinding()) {
            addToTestFailures("useStreamsForBinding");
        }

        ////////////////////////////////////////////////////
        login.dontUseStreamsForBinding();
        if (login.shouldUseStreamsForBinding()) {
            addToTestFailures("dontUseStreamsForBinding");
        }

        ////////////////////////////////////////////////////
        login.useStringBinding();
        if (!login.shouldUseStringBinding()) {
            addToTestFailures("useStringBinding");
        }

        ////////////////////////////////////////////////////
        login.dontUseStringBinding();
        if (login.shouldUseStringBinding()) {
            addToTestFailures("dontUseStringBinding");
        }

        ////////////////////////////////////////////////////
        login.setMaxBatchWritingSize(1492);
        if (login.getMaxBatchWritingSize() != 1492) {
            addToTestFailures("maxBatchWritingSize");
        }

        ////////////////////////////////////////////////////
        login.setProperty("aProperty", "junk");
        if (login.getProperty("aProperty") != "junk") {
            addToTestFailures("property");
        }

        ////////////////////////////////////////////////////
        login.setStatementCacheSize(1492);
        if (login.getStatementCacheSize() != 1492) {
            addToTestFailures("statementCacheSize");
        }

        ////////////////////////////////////////////////////
        login.setStringBindingSize(1492);
        if (login.getStringBindingSize() != 1492) {
            addToTestFailures("stringBindingSize");
        }

        ////////////////////////////////////////////////////
        login.setTransactionIsolation(2);
        if (login.getTransactionIsolation() != 2) {
            addToTestFailures("transactionIsolation");
        }

        ////////////////////////////////////////////////////
        login.dontUseByteArrayBinding();
        if (login.shouldUseByteArrayBinding()) {
            addToTestFailures("dontUseBinding");
        }

        ////////////////////////////////////////////////////
        login.dontUseExternalConnectionPooling();
        if (login.shouldUseExternalConnectionPooling()) {
            addToTestFailures("dontUseExternalConnectionPooling");
        }

        ////////////////////////////////////////////////////
        login.setDriverClassName("oracle.jdbc.OracleDriver");
        if (!(login.getDriverClassName().equals("oracle.jdbc.OracleDriver"))) {
            addToTestFailures("driverClassName");
        }

        ////////////////////////////////////////////////////
        login.useCloudscapeDriver();
        if (!login.isCloudscapeJDBCDriver()) {
            addToTestFailures("cloudscapeJDBCDriver");
        }

        ////////////////////////////////////////////////////
        login.useDB2JDBCDriver();
        if (!login.isDB2JDBCDriver()) {
            addToTestFailures("DB2JDBCDriver");
        }

        ////////////////////////////////////////////////////
        login.useIntersolvSequeLinkDriver();
        if (!login.isIntersolvSequeLinkDriver()) {
            addToTestFailures("intersolvSequeLinkDriver");
        }

        ////////////////////////////////////////////////////
        login.useJConnectDriver();
        if (!login.isJConnectDriver()) {
            addToTestFailures("JConnectDriver");
        }

        ////////////////////////////////////////////////////
        login.useJDBCConnectDriver();
        if (!login.isJDBCConnectDriver()) {
            addToTestFailures("JDBCConnectDriver");
        }

        ////////////////////////////////////////////////////
        login.useJDBCConnectRemoteDriver();
        if (!login.isJDBCConnectRemoteDriver()) {
            addToTestFailures("JDBCConnectRemoteDriver");
        }

        ////////////////////////////////////////////////////
        login.useOracle7JDBCDriver();
        if (!login.isOracle7JDBCDriver()) {
            addToTestFailures("oracle7JDBCDriver");
        }

        ////////////////////////////////////////////////////
        login.useOracleJDBCDriver();
        if (!login.isOracleJDBCDriver()) {
            addToTestFailures("oracleJDBCDriver()");
        }

        ////////////////////////////////////////////////////
        login.useOracleServerJDBCDriver();
        if (!login.isOracleServerJDBCDriver()) {
            addToTestFailures("oracleServerJDBCDriver");
        }

        ////////////////////////////////////////////////////
        login.useOracleThinJDBCDriver();
        if (!login.isOracleThinJDBCDriver()) {
            addToTestFailures("oracleThinJDBCDriver");
        }

        ////////////////////////////////////////////////////
        login.useWebLogicOracleOCIDriver();
        if (!login.isWebLogicOracleOCIDriver()) {
            addToTestFailures("webLogicOracleOCIDriver");
        }

        ////////////////////////////////////////////////////
        login.useWebLogicSQLServerDBLibDriver();
        if (!login.isWebLogicSQLServerDBLibDriver()) {
            addToTestFailures("webLogicSQLServerDBLibDriver");
        }

        ////////////////////////////////////////////////////
        login.useWebLogicSQLServerDriver();
        if (!login.isWebLogicSQLServerDriver()) {
            addToTestFailures("weblogicSQLServerDriver");
        }

        ////////////////////////////////////////////////////
        login.useWebLogicSybaseDBLibDriver();
        if (!login.isWebLogicSybaseDBLibDriver()) {
            addToTestFailures("webLogicSybaseDBLibDriver");
        }

        ////////////////////////////////////////////////////
        login.useWebLogicThinClientDriver();
        if (!login.isWebLogicThinClientDriver()) {
            addToTestFailures("weblogicThinClientDriver");
        }

        ////////////////////////////////////////////////////
        login.useWebLogicThinDriver();
        if (!login.isWebLogicThinDriver()) {
            addToTestFailures("webLogicThinDriver");
        }

        ////////////////////////////////////////////////////
        login.useAccess();
        if (!login.getPlatform().isAccess()) {
            addToTestFailures("useAccess");
        }

        ////////////////////////////////////////////////////
        login.useDBase();
        if (!login.getPlatform().isDBase()) {
            addToTestFailures("useDBase");
        }

        ////////////////////////////////////////////////////
        login.useInformix();
        if (!login.getPlatform().isInformix()) {
            addToTestFailures("useInformix");
        }

        ////////////////////////////////////////////////////
        login.useJDBCODBCBridge();
        if (!login.isJDBCODBCBridge()) {
            addToTestFailures("JDBCODBCBridge");
        }

        ////////////////////////////////////////////////////
        login.useWebLogicJDBCConnectionPool("martinsPool");
        if (login.getConnectionString().indexOf("martinsPool") == -1) {
            addToTestFailures("useWebLogicJDBCConnectionPool");
        }

        ////////////////////////////////////////////////////
        login.useWebLogicDriverCursoredOutputCode();
        if (login.getCursorCode() != 1111) {
            addToTestFailures("useWeblogicDriverCursoredOutputCode");
        }

        ////////////////////////////////////////////////////
        login.useDirectDriverConnect();
        if ((login.getConnector() == null) || !(login.getConnector() instanceof DirectConnector)) {
            addToTestFailures("useDirectDriverConnect()");
        }

        ////////////////////////////////////////////////////
        login.useSymfoware();
        if (!login.getPlatform().isSymfoware()) {
            addToTestFailures("useSymfoware");
        }

        ////////////////////////////////////////////////////
        login.useDirectDriverConnect("myDriverClassName", "myDriverURLHeader", "myDatabaseURL");
        DefaultConnector connector = (DefaultConnector)login.getConnector();

        if (connector == null) {
            addToTestFailures("useDirectDriverConnect() == null");
        } else {
            if (!connector.getDriverClassName().equals("myDriverClassName")) {
                addToTestFailures("useDirectDriverConnect() - driverClassName wrong ");
            }
            if (!connector.getDriverURLHeader().equals("myDriverURLHeader")) {
                addToTestFailures("useDirectDriverConnect() - driverURLHeader wrong");
            }
            if (!connector.getDatabaseURL().equals("myDatabaseURL")) {
                addToTestFailures("useDirectDriverConnect() - databaseURL wrong");
            }
        }

        ////////////////////////////////////////////////////
        login.setUsesNativeSQL(false);
        if (login.getUsesNativeSQL()) {
            addToTestFailures("usesNativeSQL");
        }

        ////////////////////////////////////////////////////
        login.setUsesStringBinding(false);
        if (login.getUsesStringBinding()) {
            addToTestFailures("usesStringBinding");
        }

        ////////////////////////////////////////////////////
        login.setUsesStreamsForBinding(false);
        if (login.getUsesStreamsForBinding()) {
            addToTestFailures("usesStreamsForBinding");
        }

        ////////////////////////////////////////////////////
        login.setProperty("completelyUseless", "evenMoreUseless");
        if (!(login.getProperty("completelyUseless").equals("evenMoreUseless"))) {
            addToTestFailures("setProperty");
        }

        ////////////////////////////////////////////////////
        login.removeProperty("completelyUseless");
        if (login.getProperty("completelyUseless") != null) {
            addToTestFailures("removeProperty");
        }

        ////////////////////////////////////////////////////
        DatabaseLoginWrapper dbWrapper = new DatabaseLoginWrapper();
        if (dbWrapper.driverIs("complete.non.existent.moronic.driver.class")) {
            addToTestFailures("driverIs (true when should be false)");
        }

        dbWrapper.useDB2NetJDBCDriver();
        if (!dbWrapper.driverIs("COM.ibm.db2.jdbc.net.DB2Driver")) {
            addToTestFailures("DB2NetJDBCDriver");
        }

        dbWrapper.useHSQLDriver();
        if (!dbWrapper.driverIs("org.hsqldb.jdbcDriver")) {
            addToTestFailures("HSQLDriver");
        }

        dbWrapper.useINetSQLServerDriver();
        if (!dbWrapper.driverIs("com.inet.tds.TdsDriver")) {
            addToTestFailures("INetSQLServerDriver");
        }

        dbWrapper.useJConnect50Driver();
        if (!dbWrapper.driverIs("com.sybase.jdbc2.jdbc.SybDriver")) {
            addToTestFailures("JConnect50Driver");
        }

        dbWrapper.usePointBaseDriver();
        if (!dbWrapper.driverIs("com.pointbase.jdbc.jdbcUniversalDriver")) {
            addToTestFailures("PointBaseDriver");
        }

        dbWrapper.useOracleJDBCDriver();
        if (!dbWrapper.oracleDriverIs("oci8")) {
            addToTestFailures("oracleDriverIs");
        }

        ////////////////////////////////////////////////////
        login.setCacheTransactionIsolation(9);
        if (login.getCacheTransactionIsolation() != 9) {
            addToTestFailures("cacheTransactionIsolation");
        }

        ////////////////////////////////////////////////////
        login.setCursorCode(9);
        if (login.getCursorCode() != 9) {
            addToTestFailures("cursorCode");
        }

        ////////////////////////////////////////////////////
        login.setODBCDataSourceName("fakeDataSourceName");
        if (!login.getDataSourceName().equals("fakeDataSourceName")) {
            addToTestFailures("dataSourceName");
        }

        ////////////////////////////////////////////////////
        login.setShouldTrimStrings(false);
        if (login.getShouldTrimStrings()) {
            addToTestFailures("getShouldTrimStrings");
        }

        ////////////////////////////////////////////////////
        login.setDatabaseName("oneCompleteLie");
        if (!login.getDatabaseName().equals("oneCompleteLie")) {
            addToTestFailures("databaseName");
        }

        if (login.toString().indexOf("oneCompleteLie") == -1) {
            addToTestFailures("databaseName - toString failed");
        }

        ////////////////////////////////////////////////////
        login.setServerName("homer");
        if (!login.getServerName().equals("homer")) {
            addToTestFailures("serverName");
        }

        if (login.toString().indexOf("homer") == -1) {
            addToTestFailures("serverName - toString failed");
        }

        ////////////////////////////////////////////////////
        DatabaseLogin.setShouldIgnoreCaseOnFieldComparisons(false);
        if (DatabaseLogin.shouldIgnoreCaseOnFieldComparisons()) {
            addToTestFailures("shouldIgnoreCaseOnFieldComparisons");
        }

        ////////////////////////////////////////////////////
        login.setTableQualifier("tableQualifier");
        if (!login.getTableQualifier().equals("tableQualifier")) {
            addToTestFailures("tableQualifier");
        }

        ////////////////////////////////////////////////////
        // must do this one before the any setPassword calls
        // that is, we don't want the encryption object initialized
        // before we run this test.
        login.setEncryptionClassName("class.does.not.exist");
        boolean didNotCatchException = true;

        try {
            login.setPassword("password");
        } catch (ValidationException e) {
            didNotCatchException = false;
        }

        if (didNotCatchException) {
            addToTestFailures("encryptionClassName should have thrown an exception");
        }

        login.setEncryptionClassName(null);// reset it for the next test

        ////////////////////////////////////////////////////
        login.setPassword(null);
        if (login.getPassword() != null) {
            addToTestFailures("setPassword to null, did not remove it from the properties");
        }

        ////////////////////////////////////////////////////
        DatabasePlatform originalPlatform = login.getPlatform();
        DatabasePlatform platform = new DatabasePlatform();
        String defaultSeqTableName = platform.getSequenceTableName();
        String newSeqTableName = defaultSeqTableName.concat("_NEW");
        platform.setSequenceTableName(newSeqTableName);
        login.setPlatform(platform);
        login.usePlatform(new OraclePlatform());

        String name = login.getPlatform().getSequenceTableName();
        if (!name.equals(newSeqTableName)) {
            addToTestFailures("usePlatform() method hasn't copied SequenceTableName from the existing DatabasePlatform");
        }
        login.setPlatform(originalPlatform);

        ////////////////////////////////////////////////////
        originalPlatform = login.getPlatform();
        login.setPlatform(null);
        didNotCatchException = true;

        try {
            login.usePlatform(new DatabasePlatform());
        } catch (NullPointerException e) {
            didNotCatchException = false;
        }

        if (!didNotCatchException) {
            addToTestFailures("usePlatform() method throws a NullPointerException when there is no platform in the login");
        }
        login.setPlatform(originalPlatform);

        ////////////////////////////////////////////////////
        originalPlatform = login.getPlatform();
        platform = new DatabasePlatform();
        defaultSeqTableName = platform.getSequenceTableName();
        newSeqTableName = defaultSeqTableName.concat("_NEW");
        platform.setSequenceTableName(newSeqTableName);
        login.setPlatform(platform);
        login.setPlatformClassName("org.eclipse.persistence.platform.database.OraclePlatform");

        name = login.getPlatform().getSequenceTableName();
        if (!name.equals(newSeqTableName)) {
            addToTestFailures("setPlatformClassName() method hasn't copied settings from the existing platform");
        }
        login.setPlatform(originalPlatform);

        ////////////////////////////////////////////////////
        didNotCatchException = true;

        try {
            login.setPlatformClassName("one.big.dumb.platform");
        } catch (ValidationException e) {
            didNotCatchException = false;
        }

        if (didNotCatchException) {
            addToTestFailures("setting false platform name didn't throw an exception");
        }

        ////////////////////////////////////////////////////
        didNotCatchException = true;

        try {
            DatabaseLoginWrapper wrap = new DatabaseLoginWrapper();
            wrap.getStupidConnector();
        } catch (ValidationException e) {
            didNotCatchException = false;
        }

        if (didNotCatchException) {
            addToTestFailures("did not catch validation exception on invalid database connector");
        }
    }
}

/*
 * class used to access protected methods in DatabaseLogin
 */
class DatabaseLoginWrapper extends DatabaseLogin {
    public DatabaseLoginWrapper() {
        setDriverClassName("oracle.jdbc.OracleDriver");
    }

    public boolean driverIs(String name) {
        return super.driverIs(name);
    }

    public Connector getStupidConnector() {
        setConnector(new StupidConnector());
        return super.getDefaultConnector();
    }

    public boolean oracleDriverIs(String urlPrefix) {
        return super.oracleDriverIs(urlPrefix);
    }
}

/*
 * class used for testing invalid Connectors
 */
class StupidConnector implements Connector {
    public StupidConnector() {
        super();
    }

    public Object clone() {
        return null;
    }

    public Connection connect(Properties properties, Session session) {
        return null;
    }

    public String getConnectionDetails() {
        return "N/A";
    }

    public void toString(PrintWriter writer) {
        writer.println("StupidConnector");
    }
}
