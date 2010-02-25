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
package org.eclipse.persistence.testing.framework;

import java.util.*;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.platform.database.TimesTenPlatform;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCaseHelper;
import org.eclipse.persistence.tools.schemaframework.*;

/**
 * Used in a test model to configure the database and session.
 * This class also store every database login that we use.
 */
public class TestSystem {
    protected DatabaseLogin login;
    public Project project;
    
    public Map<String, String> properties;

    /**
     * Add all of the descriptors.
     */
    public void addDescriptors(DatabaseSession session) {
        session.addDescriptors(buildDescriptors());
    }
        
    /**
     * Return all descriptor for the system.
     * This can be used so that subclasses do not have to add descriptors themselves.
     */
    public Vector buildDescriptors() {
        return new Vector();
    }
    
    /**
     * Return all tables for the system.
     * This can be used so that subclasses do not have to create the tables themselves.
     */
    public Vector buildTables() {
        return new Vector();
    }

    /**
     * Define the table creation pattern.
     */
    public void createTables() throws Exception {
        DatabaseSession session;

        session = login();
        try {
            createTables(session);
        } finally {
            session.logout();
        }
    }

    /**
     * Default method to create/recreate the database.
     * First drop constraints ignoring errors if tables did not exist.
     * Second recreate the tables.
     * Then add the constraints back.
     */
    public void createTables(DatabaseSession session) throws Exception {
        Vector tables = buildTables();
        SchemaManager schemaManager = new SchemaManager(session);
        for (Enumeration dropForeignKeyEnum = tables.elements();
                 dropForeignKeyEnum.hasMoreElements();) {
            TableDefinition table = (TableDefinition)dropForeignKeyEnum.nextElement();
            try {
                schemaManager.dropConstraints(table);
            } catch (DatabaseException exception) {
                // Ignore
            }
        }
        for (Enumeration replaceEnum = tables.elements(); replaceEnum.hasMoreElements();) {
            TableDefinition table = (TableDefinition)replaceEnum.nextElement();
            schemaManager.replaceObject(table);
        }
        for (Enumeration createForeignKeyEnum = tables.elements();
                 createForeignKeyEnum.hasMoreElements();) {
            TableDefinition table = (TableDefinition)createForeignKeyEnum.nextElement();
            schemaManager.createConstraints(table);
        }
    }

    /**
     * Return the default database login to be used.
     */
    public DatabaseLogin getLogin() {
        if (login == null) {
            loadLoginFromProperties();
        }
        return login;
    }

    /**
     * Load the default login from the test.properties file.
     * This file must be on the classpath, or system property set.
     */
    public void loadLoginFromProperties() {        
        this.properties = JUnitTestCaseHelper.getDatabaseProperties();
        login = new DatabaseLogin();
        login.setDriverClassName(this.properties.get(PersistenceUnitProperties.JDBC_DRIVER));
        login.setConnectionString(this.properties.get(PersistenceUnitProperties.JDBC_URL));
        login.setUserName(this.properties.get(PersistenceUnitProperties.JDBC_USER));
        // This avoids encrypting the password, as some tests require it non-encrypted.
        login.setEncryptedPassword(this.properties.get(PersistenceUnitProperties.JDBC_PASSWORD));
        String platform = this.properties.get("eclipselink.target-database");
        if (platform != null) {
            login.setPlatformClassName(platform);
        }
    }

    /**
     * Return a connected session using the default login.
     */
    public DatabaseSession login() {
        DatabaseSession session;

        session = new Project(getLogin()).createDatabaseSession();
        addDescriptors(session);
        session.setLogLevel(AbstractSessionLog.translateStringToLoggingLevel(properties.get(PersistenceUnitProperties.LOGGING_LEVEL)));
        session.login();

        return session;
    }

    /**
     * Define the database population pattern.
     */
    public void populate() throws Exception {
        DatabaseSession session;

        session = login();
        try {
            populate(session);
        } finally {
            session.logout();
        }
    }

    /**
     * Abstract method for database population.
     */
    public void populate(DatabaseSession session) throws Exception {
        // Nothing by default.
    }

    /**
     * Used for configuration during testing.
     */
    public void run(Session session) throws Exception {
        // Use old API to be able to run on 9.0.4 for perf testing.
        session.getIdentityMapAccessor().initializeIdentityMaps();
        this.addDescriptors((DatabaseSession)session);
        if (session.getDatasourceLogin().getDatasourcePlatform().isTimesTen()) {
            ((TimesTenPlatform)session.getDatasourceLogin().getDatasourcePlatform()).setSupportsForeignKeyConstraints(false);
        }
        this.createTables((DatabaseSession)session);
        this.populate((DatabaseSession)session);
        // Use old API to be able to run on 9.0.4 for perf testing.
        session.getIdentityMapAccessor().initializeIdentityMaps();
    }

    /**
     * Set the default database login to be used.
     */
    public void setLogin(DatabaseLogin login) {
        this.login = login;
    }

    /**
     * You must have MS Access installed an the MS ODBC setup to a local database.
     */
    public void useAccessJDBCODBC() {
        DatabaseLogin login = new DatabaseLogin();
        login.useAccess();
        login.setODBCDataSourceName("MSACCESS");
        setLogin(login);
    }

    /**
     * You must have the Attunity Connect driver loaded.
     */
    public void useAttunityConnect() {
        DatabaseLogin login = new DatabaseLogin(new org.eclipse.persistence.platform.database.AttunityPlatform());
        login.setDatabaseURL("mercury2;deftdpname=oracle");
        login.setDriverClassName("com.attunity.jdbc.NvDriver");
        login.setDriverURLHeader("jdbc:attconnect://");
        setLogin(login);
    }

    /**
     * You must have the Derby driver loaded.
     */
    public void useDerby() {
        DatabaseLogin login = new DatabaseLogin(new org.eclipse.persistence.platform.database.DerbyPlatform());
        login.setDriverClassName("org.apache.derby.jdbc.EmbeddedDriver");
        login.setDriverURLHeader("jdbc:derby:");
        login.setDatabaseURL("test;create=true");
        setLogin(login);
    }

    /**
     * You must have the H2 driver loaded.
     */
    public void useH2() {
        DatabaseLogin login = new DatabaseLogin(new org.eclipse.persistence.platform.database.H2Platform());
        login.setDriverClassName("org.h2.Driver");
        login.setDriverURLHeader("jdbc:h2:");
        login.setDatabaseURL("test");
        login.setUserName("sa");
        login.setPassword("");
        setLogin(login);
    }

    /**
     * You must have the Postgres driver loaded.
     */
    public void usePostgres() {
        DatabaseLogin login = new DatabaseLogin(new org.eclipse.persistence.platform.database.PostgreSQLPlatform());
        login.setDriverClassName("org.postgresql.Driver");
        login.setDriverURLHeader("jdbc:postgresql:");
        login.setDatabaseURL("//qaott40.ca.oracle.com/toplink");
        setLogin(login);
    }

    /**
     * You must have the Universal drivers and license files loaded.
     */
    public void useDB2UniversalDriver() {
        DatabaseLogin login = new DatabaseLogin();
        login.useDB2();
        login.setDriverClassName("com.ibm.db2.jcc.DB2Driver");
        login.setDriverURLHeader("jdbc:db2://");
        login.setDatabaseURL("tlsvrdb7.ca.oracle.com:50001/TOPLINK");
        login.setUserName("");
        //set the encrypted password will enable toplink to use the plain text password as is
        login.setEncryptedPassword("password");
        setLogin(login);
    }

    /**
     * You must have the DataDirect drivers and oc4j.jar loaded.
     */
    public void useDB2DataDirect() {
        DatabaseLogin login = new DatabaseLogin();
        login.useDB2();
        login.setDriverClassName("com.oracle.ias.jdbc.db2.DB2Driver");
        login.setDriverURLHeader("jdbc:oracle:db2://");
        login.setDatabaseURL("tlsvrdb7.ca.oracle.com:50001;DatabaseName=TOPLINK");
        login.setUserName("");
        //set the encrypted password will enable toplink to use the plain text password as is
        login.setEncryptedPassword("password");
        setLogin(login);
    }

    /**
     * You must have DB2 client installed and the IBM JDBC driver loaded.
     */
    public void useDB2App() {
        DatabaseLogin login = new DatabaseLogin();

        //login.useDB2JDBCDriver();
        login.useDB2();
        login.setDriverClassName("COM.ibm.db2.jdbc.app.DB2Driver");
        login.setDriverURLHeader("jdbc:db2:");
        login.useStreamsForBinding();
        login.setUserName("");
        //set the encrypted password will enable toplink to use the plain text password as is
        login.setEncryptedPassword("password");
        login.setDatabaseURL("TOPLINK");
        login.useByteArrayBinding();
        setLogin(login);
    }

    /**
     * You must have the IBM JDBC driver loaded.
     */
    public void useDB2Net() {
        DatabaseLogin login = new DatabaseLogin();

        login.useDB2();
        login.setDriverClassName("COM.ibm.db2.jdbc.net.DB2Driver");
        login.setDriverURLHeader("jdbc:db2:");
        login.useStreamsForBinding();
        login.setUserName("");
        //set the encrypted password will enable toplink to use the plain text password as is
        login.setEncryptedPassword("password");
        login.setDatabaseURL("tlsvrdb7.ca.oracle.com:TOPLINK");
        login.useByteArrayBinding();
        setLogin(login);
    }

    /**
     * You must have the MySQL Connector/J JDBC driver loaded.
     * @param userName java.lang.String
     * Creation date: (03/17/05 4:50:00 PM)
     * Creator: Edwin Tang
     */
    public void useMySQL(String userName) {
        DatabaseLogin login = new DatabaseLogin();

        login.useMySQL();
        login.setDriverClassName("com.mysql.jdbc.Driver");
        login.setDriverURLHeader("jdbc:mysql:");
        login.useStreamsForBinding();
        login.setUserName(userName);
        //set the encrypted password will enable toplink to use the plain text password as is
        login.setEncryptedPassword("password");
        login.setDatabaseURL("//qaott51.ca.oracle.com:3309/"+userName);
        login.useByteArrayBinding();
        setLogin(login);
    }

    /**
     * You must have the HSQL driver loaded.  You must have the HSQL server running.
     */
    public void useHSQL() {
        DatabaseLogin login = new DatabaseLogin(new org.eclipse.persistence.platform.database.HSQLPlatform());
        login.useHSQLDriver();
        login.setDatabaseURL("file:test");
        login.setUserName("sa");
        setLogin(login);
    }

    /**
     * You must have the Informix thin driver loaded.
     */
    public void useInformixJDBC20() {
        DatabaseLogin login = new DatabaseLogin(new org.eclipse.persistence.platform.database.InformixPlatform());
        login.useInformix();
        login.useByteArrayBinding();
        login.setDriverClassName("com.informix.jdbc.IfxDriver");
        login.setDriverURLHeader("jdbc:informix-sqli://");
        login.setDatabaseURL("mercury2:1526/toplinkj:INFORMIXSERVER=toplinkj");
        login.setUserName("informix");
        //set the encrypted password will enable toplink to use the plain text password as is
        login.setEncryptedPassword("informix");
        setLogin(login);
    }

    /**
     * You must have the Informix drivers loaded.
     */
    public void useInformix11() {
        DatabaseLogin dbLogin = new DatabaseLogin(new org.eclipse.persistence.platform.database.InformixPlatform());
        dbLogin.useInformix();
        dbLogin.setDriverClassName("com.informix.jdbc.IfxDriver");
        dbLogin.setDriverURLHeader("jdbc:informix-sqli://");
        dbLogin.setDatabaseURL("tlsvrdb6.ca.oracle.com:9088/toplink:INFORMIXSERVER=informix");
        dbLogin.setUserName("informix"); 
        //set the encrypted password will enable toplink to use the plain text password as is
        dbLogin.setEncryptedPassword("password");
        setLogin(dbLogin);
    }
    
    /**
     * This is a generic configuration, would should test this occationally to test generic drivers.
     * A "JDBC" ODBC entry must be setup, however table creation will fail for most databases, I think
     * if it is setup to DB2 table creation will pass.
     */
    public void useJDBCODBC() {
        DatabaseLogin login = new DatabaseLogin();
        login.setODBCDataSourceName("JDBC");
        setLogin(login);
    }

    /**
     * You must have the Oracle client installed and the Oracle JDBC driver loaded.
     */
    public void useOracleOCI() {
        DatabaseLogin login = new DatabaseLogin(OracleDBPlatformHelper.getInstance().getOracle9Platform());
        login.useOracleJDBCDriver();
        login.setDatabaseURL("toplink");
        login.setUserName("");
        //set the encrypted password will enable toplink to use the plain text password as is
        login.setEncryptedPassword("password");
        setLogin(login);
    }

    /**
     * You must have the MS JDBC driver for SQL Server loaded.
     */
    public void useSQLServerMSJDBC() {
        DatabaseLogin login = new DatabaseLogin();
        login.useSQLServer();
        login.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        login.setDriverURLHeader("jdbc:sqlserver://");
        login.setDatabaseURL("tlsvrdb6");
        login.setUserName("QA10");
        //set the encrypted password will enable toplink to use the plain text password as is
        login.setEncryptedPassword("password");
        login.useByteArrayBinding();
        setLogin(login);
    }

    /**
     * You must have the SQL Server and the Weblogic drivers installed.
     */
    public void useSQLServerWeblogicDBLib() {
        DatabaseLogin login = new DatabaseLogin();
        login.useWebLogicSQLServerDBLibDriver();
        login.setODBCDataSourceName("tlsvrdb6");
        login.setUserName("QA10");
        //set the encrypted password will enable toplink to use the plain text password as is
        login.setEncryptedPassword("password");
        login.useByteArrayBinding();
        setLogin(login);
    }

    /**
     * You must have the DataDirect drivers and oc4j.jar loaded..
     */
    public void useSQLServerDataDirect() {
        DatabaseLogin login = new DatabaseLogin();
        login.useSQLServer();
        login.setDriverClassName("com.oracle.ias.jdbc.sqlserver.SQLServerDriver");
        login.setDriverURLHeader("jdbc:oracle:sqlserver://");
        login.setDatabaseURL("localhost\\\\sql2000:1433");
        login.setUserName("QA10");
        //set the encrypted password will enable toplink to use the plain text password as is
        login.setEncryptedPassword("password");
        login.useByteArrayBinding();
        setLogin(login);
    }

    /**
     * You must have the Sybase JConnect thin driver loaded.
     */
    public void useSybaseJConnect() {
        DatabaseLogin login = new DatabaseLogin();
        login.useSybase();
        login.setDriverClassName("com.sybase.jdbc3.jdbc.SybDriver");
        login.setDriverURLHeader("jdbc:sybase:Tds:");
        login.setDatabaseURL("tlsvrdb1.ca.oracle.com:5000/TLDEV1");
        login.setUserName("TLDEV1");
        //set the encrypted password will enable toplink to use the plain text password as is
        login.setEncryptedPassword("password");
        setLogin(login);
    }

    /**
     * You must have the DataDirect drivers and oc4j.jar loaded.
     */
    public void useSybaseDataDirect() {
        DatabaseLogin login = new DatabaseLogin();
        login.useSybase();
        login.setDriverClassName("com.oracle.ias.jdbc.sybase.SybaseDriver");
        login.setDriverURLHeader("jdbc:oracle:sybase://");
        login.setDatabaseURL("tlsvrdb1.ca.oracle.com:5000"); 
        login.setUserName("");
        //set the encrypted password will enable toplink to use the plain text password as is
        login.setEncryptedPassword("password");
        setLogin(login);
    }

    /**
     * You must have the Oracle thin driver loaded.
     * @param userName java.lang.String
     * Creation date: (12/7/00 4:03:00 PM)
     * Creator: Edwin Tang
     */
    public void useOracleThin(String databaseURL, String userName, String password) {
    	DatabasePlatform platform = null;
    	try{
    		Class platformClass = Class.forName("org.eclipse.persistence.platform.database.oracle.Oracle9Platform");
    		platform = (DatabasePlatform)platformClass.newInstance();
    	} catch (Exception e){
    		platform = new org.eclipse.persistence.platform.database.OraclePlatform();
    	}
        DatabaseLogin login = new DatabaseLogin(platform);

    	login.useOracleThinJDBCDriver();
        login.setDatabaseURL(databaseURL);
        login.setUserName(userName);
        //set the encrypted password will enable toplink to use the plain text password as is
        login.setEncryptedPassword(password);
        setLogin(login);
    }

    /**
     * You must have the Oracle 8 thin driver loaded.
     * @param userName java.lang.String
     * Creation date: (12/7/00 4:03:00 PM)
     * Creator: Praba Vijayaratnam
     */
    public void useOracle8Thin(String databaseURL, String userName, String password) {
    	DatabasePlatform platform = null;
    	try{
    		Class platformClass = Class.forName("org.eclipse.persistence.platform.database.oracle.Oracle8Platform");
    		platform = (DatabasePlatform)platformClass.newInstance();
    	} catch (Exception e){
    		platform = new org.eclipse.persistence.platform.database.OraclePlatform();
    	}
        DatabaseLogin login = new DatabaseLogin(platform);
        login.useOracleThinJDBCDriver();
        login.setDatabaseURL(databaseURL);
        login.setUserName(userName);
        //set the encrypted password will enable toplink to use the plain text password as is
        login.setEncryptedPassword(password);
        setLogin(login);
    }

    /**
    * You must have DB2 installed with the IBM DB2 ODBC driver setup as "DB2".
    */
    public void useDB2JDBCODBC() {
        DatabaseLogin login = new DatabaseLogin();
        login.useDB2();
        login.setODBCDataSourceName("DB2");
        login.setUserName("");
        login.setTableQualifier("");
        //set the encrypted password will enable toplink to use the plain text password as is
        login.setEncryptedPassword("password");
        setLogin(login);
    }

    /**
     * You must have DB2 installed with a TopLink database created and the IBM JDBC driver loaded.
     */
    public void useDB2JDBC() {
        DatabaseLogin login = new DatabaseLogin();
        login.useDB2JDBCDriver();
        login.useByteArrayBinding();
        login.useStreamsForBinding();
        login.setUserName("");
        //set the encrypted password will enable toplink to use the plain text password as is
        login.setEncryptedPassword("password");
        login.setDatabaseURL("TOPLINK");
        setLogin(login);
    }

    /**
    * You must have the SQL Server and the Weblogic drivers installed.
    */
    public void useSQLServerWeblogicThin() {
        DatabaseLogin login = new DatabaseLogin();
        login.useSQLServer();
        login.useWebLogicSQLServerDriver();
        login.setODBCDataSourceName("localhost");
        login.setUserName("");
        //set the encrypted password will enable toplink to use the plain text password as is
        login.setEncryptedPassword("password");
        login.useByteArrayBinding();
        setLogin(login);
    }

    /**
     * You must have the Sybase JConnect thin driver loaded.
     */
    public void useSybaseJConnect50() {
        DatabaseLogin login = new DatabaseLogin();
        login.setDriverClassName("");

        login.setDatabaseURL("tlsvrdb1.ca.oracle.com:5000/COREDEV1");
        login.setUserName("");
        //set the encrypted password will enable toplink to use the plain text password as is
        login.setEncryptedPassword("password");
        setLogin(login);
    }

    /**
     * You must have the Sybase client installed and the Weblogic JDBC driver loaded.
     */
    public void useSybaseWeblogicDBLib() {
        DatabaseLogin login = new DatabaseLogin();
        login.useWebLogicSybaseDBLibDriver();
        login.setDatabaseURL("");
        login.setUserName("");
        //set the encrypted password will enable toplink to use the plain text password as is
        login.setEncryptedPassword("password");
        setLogin(login);
    }

    /**
     * You must have the SQL Server and the i-net jdbc driver for java 2 installed. This will run on TM2.
     */
    public void useSQLServerThin() {
        DatabaseLogin login = new DatabaseLogin();
        login.useSQLServer();
        login.useINetSQLServerDriver();
        login.setODBCDataSourceName("localhost");
        login.setUserName("");
        //set the encrypted password will enable toplink to use the plain text password as is
        login.setEncryptedPassword("password");
        login.useByteArrayBinding();
        setLogin(login);
    }

    /**
     * You must have the TimesTen JDBC driver loaded.
     * @param userName java.lang.String
     * Creation date: (11/10/05 4:55:00 PM)
     * Creator: Edwin Tang
     */
    public void useTimesTen(String userName) {
        DatabaseLogin login = new DatabaseLogin();
        try {
			login.usePlatform((org.eclipse.persistence.internal.databaseaccess.DatabasePlatform)Class.forName("org.eclipse.persistence.platform.database.TimesTenPlatform").newInstance());
        } catch (Exception e) {}
        login.setDriverClassName("com.timesten.jdbc.TimesTenDriver");
        login.setDriverURLHeader("jdbc:timesten:client:");
        login.useStreamsForBinding();
        login.setUserName(userName);
        //set the encrypted password will enable toplink to use the plain text password as is
        login.setEncryptedPassword("password");
        login.setDatabaseURL(userName);
        login.useByteArrayBinding();
        setLogin(login);
    }

    /**
     * You must have the Symfoware JDBC driver loaded.
     */
    public void useSymfowareRDB2_TCP() {
        DatabaseLogin login = new DatabaseLogin();
        try {
            login.usePlatform((org.eclipse.persistence.internal.databaseaccess.DatabasePlatform)Class.forName("org.eclipse.persistence.platform.database.SymfowarePlatform").newInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }
        login.setDriverClassName("com.fujitsu.symfoware.jdbc.SYMDriver");
        login.setDriverURLHeader("jdbc:symford:");
        // codeselect setting reduces garbled characters when DB and client's
        // character encoding settings do not match
        login.setDatabaseURL("TESTDB");
        //set the encrypted password will enable toplink to use the plain text password as is
        login.setEncryptedPassword("password");
        setLogin(login);
    }
}
