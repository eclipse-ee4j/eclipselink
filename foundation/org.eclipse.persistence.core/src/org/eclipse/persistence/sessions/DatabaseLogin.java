/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.sessions;

import java.io.*;
import java.sql.Connection;
import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.databaseaccess.DatabaseAccessor;
import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.internal.localization.*;
import org.eclipse.persistence.platform.database.*;
import org.eclipse.persistence.platform.database.converters.StructConverter;
import org.eclipse.persistence.platform.database.partitioning.DataPartitioningCallback;
import org.eclipse.persistence.sequencing.NativeSequence;
import org.eclipse.persistence.exceptions.*;

/**
 * <p>
 * <b>Purpose</b>:
 * Hold the configuration information necessary to connect to a JDBC driver.
 * <p>
 * <b>Description</b>:
 * A DatabaseLogin is used by an EclipseLink database session to connect to a
 * JDBC server.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Hold the driver class name and URL header
 * <li> Hold the database URL
 * <li> Hold any driver-specific database connection properties (e.g. "user", "database")
 * <li> Build the JDBC driver connect string
 * <li> Hold the database platform (e.g. Oracle, DB2)
 * <li> Hold the message logging stream
 * <li> Hold other assorted configuration settings
 * </ul>
 */
public class DatabaseLogin extends DatasourceLogin {

    /**
     * Transaction isolation levels used in setTransactionIsolation().
     * These constants are from java.sql.Connection.
     */
    /** Transactions are not supported. */
    public static final int TRANSACTION_NONE = Connection.TRANSACTION_NONE;

    /** Dirty reads, non-repeatable reads and phantom reads can occur. */
    public static final int TRANSACTION_READ_UNCOMMITTED = Connection.TRANSACTION_READ_UNCOMMITTED;

    /** Dirty reads are prevented; non-repeatable reads and phantom reads can occur. */
    public static final int TRANSACTION_READ_COMMITTED = Connection.TRANSACTION_READ_COMMITTED;

    /** Dirty reads and non-repeatable reads are prevented; phantom reads can occur. */
    public static final int TRANSACTION_REPEATABLE_READ = Connection.TRANSACTION_REPEATABLE_READ;

    /** Dirty reads, non-repeatable reads and phantom reads are prevented. */
    public static final int TRANSACTION_SERIALIZABLE = Connection.TRANSACTION_SERIALIZABLE;


    /** Stores the value for the number of time EclipseLink will attempt to reconnect the connection on a comm failure
     *  in the case EclipseLink is attempting to retry a query.  EclipseLink will retry a read query outside of a transaction
     *  if EclipseLink can determine that a communication error occurred with the database.  
     */
    protected int queryRetryAttemptCount;
    
    /** Stores the number of milliseconds that EclipseLink will wait between attempts to reconnect a DatabaseConnection
     *  in the case EclipseLink is attempting to retry a query.  EclipseLink will retry a read query outside of a transaction
     *  if EclipseLink can determine that a communication error occurred with the database.
     */
    protected int delayBetweenConnectionAttempts;
    
    /**
     * On an SQL Exception EclipseLink will ping the database to determine
     * if the connection used can continue to be used for queries.  This should have no impact on applications
     * unless the user is using pessimistic locking queries with 'no wait' or are using a query timeout feature.
     * If that is the case and the application is experiencing a performance impact from the health check then
     * this feature can be turned off. Turning this feature off will prevent EclipseLink from being able to
     * retry queries in the case of database failure.
     * By default (null) connection health is validate if the query does not have a timeout, and there is a ping string.
     * Setting to true or false overrides this.
     */
    protected Boolean connectionHealthValidatedOnError;
    
    /**
     * PUBLIC:
     * Create a new login.
     */
    public DatabaseLogin() {
        this(new DatabasePlatform());
    }

    /**
     * ADVANCED:
     * Create a new login for the given platform.
     */
    public DatabaseLogin(DatabasePlatform databasePlatform) {
        super(databasePlatform);
        this.useDefaultDriverConnect();
        this.delayBetweenConnectionAttempts = 5000;
        this.queryRetryAttemptCount = 3;
    }

    /**
     * ADVANCED:
     * Set the database platform to be custom platform.
     */
    public void usePlatform(DatabasePlatform platform) {
        super.usePlatform(platform);
    }

    /**
     * ADVANCED:
     * Add a StructConverter
     * @see org.eclipse.persistence.platform.database.converters.StructConverter
     * @param converter
     */
    public void addStructConverter(StructConverter converter){
        getPlatform().addStructConverter(converter);
    }
    
    /**
     * PUBLIC:
     * Bind all arguments to any SQL statement.
     */
    public void bindAllParameters() {
        setShouldBindAllParameters(true);
    }

    /**
     * INTERNAL:
     * Build and return an appropriate Accessor.
     * The default is a DatabaseAccessor.
     */
    public Accessor buildAccessor() {
        return new DatabaseAccessor();
    }

    /**
     * PUBLIC:
     * Cache all prepared statements, this requires full parameter binding as well.
     * @see #bindAllParameters()
     */
    public void cacheAllStatements() {
        setShouldCacheAllStatements(true);
    }

    /**
     * PUBLIC:
     * Do not bind all arguments to any SQL statement.
     */
    public void dontBindAllParameters() {
        setShouldBindAllParameters(false);
    }

    /**
     * PUBLIC:
     * Do not cache all prepared statements.
     */
    public void dontCacheAllStatements() {
        setShouldCacheAllStatements(false);
    }

    /**
     * PUBLIC:
     * Disable driver level data conversion optimization.
     * This can be disabled as some drivers perform data conversion themselves incorrectly.
     */
    public void dontOptimizeDataConversion() {
        setShouldOptimizeDataConversion(false);
    }

    /**
     * PUBLIC:
     * EclipseLink can be configured to use batch writing. This facility allows multiple write operations to be
     * submitted to a database for processing at once. Submitting multiple updates together, instead of
     * individually, can greatly improve performance in some situations.
     */
    public void dontUseBatchWriting() {
        setUsesBatchWriting(false);
    }

    /**
     * PUBLIC:
     * EclipseLink can be configured to use parameter binding for large binary data.
     * By default EclipseLink will print this data as hex through the JDBC binary escape clause.
     * Both binding and printing have various limits on all databases (e.g. 5k - 32k).
     */
    public void dontUseByteArrayBinding() {
        setUsesByteArrayBinding(false);
    }

    /**
     * PUBLIC: Indicate to EclipseLink that the JDBC driver does not support batch writing.
     * This will revert to the default behavior which is to delegate to EclipseLink's
     * internal batch writing.
         * @see #useJDBCBatchWriting
         * @see #setUsesJDBCBatchWriting(boolean usesJDBCBatchWriting)
     */
    public void dontUseJDBCBatchWriting() {
        setUsesJDBCBatchWriting(false);
    }

    /**
     * PUBLIC:
     * EclipseLink can be configured to use database-specific SQL grammar,
     * as opposed to the JDBC standard grammar.
     * This is because, unfortunately, some drivers to not support the full JDBC standard.
     * By default EclipseLink uses the JDBC SQL grammar.
     */
    public void dontUseNativeSQL() {
        setUsesNativeSQL(false);
    }

    /**
     * PUBLIC:
     * EclipseLink can be configured to use streams to store large binary data.
     */
    public void dontUseStreamsForBinding() {
        setUsesStreamsForBinding(false);
    }

    /**
     * PUBLIC:
     * Do not bind strings of any size.
     */
    public void dontUseStringBinding() {
        getPlatform().setStringBindingSize(0);
        getPlatform().setUsesStringBinding(false);
    }

    /**
     * PUBLIC:
     * Used for table creation. Most databases do not create an index automatically for
     * foreign key columns.  Normally it is recommended to index foreign key columns.
     * This allows for foreign key indexes to be configured, by default foreign keys are not indexed.
     * 
     * @return whether an index should be created explicitly for foreign key constraints
     */
    public boolean shouldCreateIndicesOnForeignKeys() {
        return getPlatform().shouldCreateIndicesOnForeignKeys();
    }

    /**
     * PUBLIC:
     * Used for table creation. Most databases do not create an index automatically for
     * foreign key columns.  Normally it is recommended to index foreign key columns.
     * This allows for foreign key indexes to be configured, by default foreign keys are not indexed.
     */
    public void setShouldCreateIndicesOnForeignKeys(boolean shouldCreateIndicesOnForeignKeys) {
        getPlatform().setShouldCreateIndicesOnForeignKeys(shouldCreateIndicesOnForeignKeys);
    }

    /**
     * INTERNAL:
     * Return whether the specified driver is being used.
     */
    protected boolean driverIs(String driverName) {
        try {
            return getDriverClassName().equals(driverName);
        } catch (ValidationException e) {
            // this exception will be thrown if we are using something other than a DefaultConnector
            return false;
        }
    }

    /**
     * PUBLIC:
     * Return the JDBC connection string.
     * This is a combination of the driver-specific URL header and the database URL.
     */
    public String getConnectionString() throws ValidationException {
        return getDefaultConnector().getConnectionString();
    }

    /**
     * ADVANCED:
     * Return the code for preparing cursored output
     * parameters in a stored procedure
     */
    public int getCursorCode() {
        return getPlatform().getCursorCode();
    }

    /**
     * PUBLIC:
     * The database name is required when connecting to databases that support
     * multiple databases within a single server instance (e.g. Sybase, SQL Server).
     * This is ONLY used when connecting through ODBC type JDBC drivers.
     * This is NEVER used with Oracle.
     */
    public String getDatabaseName() {
        return properties.getProperty("database");
    }

    /**
     * PUBLIC:
     * The database URL is the JDBC URL for the database server.
     * The driver header is <i>not</i> be included in this URL
     * (e.g. "dbase files"; not "jdbc:odbc:dbase files").
     */
    public String getDatabaseURL() {
        if (!(getConnector() instanceof DefaultConnector)) {
            return "";
        }
        return getDefaultConnector().getDatabaseURL();
    }

    /**
     * PUBLIC:
     * The data source name is required if connecting through ODBC (JDBC-ODBC, etc.).
     * This is the ODBC name given in the ODBC Data Source Administrator.
     * This is just the database part of the URL.
     */
    public String getDataSourceName() throws ValidationException {
        return getDatabaseURL();
    }
    
    /**
     * PUBLIC:
     * Return the datasource platform specific information.
     * This allows EclipseLink to configure certain advanced features for the datasource desired.
     */
    public Platform getDatasourcePlatform() {
        if (this.platform == null) {
            this.platform = new DatabasePlatform();
        }
        return platform;
    }

    /**
     * INTERNAL:
     * Return the connector that will instantiate the java.sql.Connection.
     */
    protected DefaultConnector getDefaultConnector() throws ValidationException {
        try {
            return (DefaultConnector)getConnector();
        } catch (ClassCastException e) {
            throw ValidationException.invalidConnector(connector);
        }
    }

    /**
     * PUBLIC:
     * The driver class is the name of the Java class for the JDBC driver being used
     * (e.g. "sun.jdbc.odbc.JdbcOdbcDriver").
     */
    public String getDriverClassName() {
        if (!(getConnector() instanceof DefaultConnector)) {
            return "";
        }
        return getDefaultConnector().getDriverClassName();
    }

    /**
     * PUBLIC:
     * The driver URL header is the string predetermined by the JDBC driver to be
     * part of the URL connection string, (e.g. "jdbc:odbc:").
     * This is required to connect to the database.
     */
    public String getDriverURLHeader() {
        if (!(getConnector() instanceof DefaultConnector)) {
            return "";
        }
        return getDefaultConnector().getDriverURLHeader();
    }

    /**
     * PUBLIC:
     * Allow for the max batch writing size to be set.
     * This allows for the batch size to be limited as most database have strict limits.
     * If returns 0 then default size value is used.
     * The size is in characters, the default is 32000 but the real value depends on the database configuration.
     */
    public int getMaxBatchWritingSize() {
        return getPlatform().getMaxBatchWritingSize();
    }

    /**
     * PUBLIC:
     * EclipseLink will attempt to test a connection if it encounters an exception on the connection
     * when executing SQL.  This attribute represents the SQL query that will be executed by EclipseLink.
     * By default EclipseLink uses a query that should be correct for the specified platform but users
     * may need or want to override that query.
     */
    public String getPingSQL(){
        return getPlatform().getPingSQL();
    }
    

    /**
     * PUBLIC:
     * Return the number of attempts EclipseLink should make to re-connect to a database and re-execute 
     * a query after a query has failed because of a communication issue.
     * EclipseLink will only attempt to reconnect when EclipseLink can determine that a communication failure occurred
     * on a read query executed outside of a transaction.  By default EclipseLink will attempt to retry the
     * query 3 times, by setting this value to 0 EclipseLink will not retry queries.
     */
    public int getQueryRetryAttemptCount() {
        return queryRetryAttemptCount;
    }

    /**
     * PUBLIC:
     * The server name is the name of the database instance.
     * This is ONLY required if using an ODBC JDBC driver
     * and overriding the server name specified in the ODBC
     * Data Source Administrator.
     */
    public String getServerName() {
        return properties.getProperty("server");
    }

    /**
     * PUBLIC:
     * Used to help bean introspection.
     */
    public boolean getShouldBindAllParameters() {
        return shouldBindAllParameters();
    }

    /**
     * PUBLIC:
     * Used to help bean introspection.
     */
    public boolean getShouldCacheAllStatements() {
        return shouldCacheAllStatements();
    }

    /**
     * PUBLIC:
     * Used to help bean introspection.
     */
    public boolean getShouldOptimizeDataConversion() {
        return shouldOptimizeDataConversion();
    }

    /**
     * PUBLIC:
     * Used to help bean introspection.
     */
    public boolean getShouldTrimStrings() {
        return shouldTrimStrings();
    }

    /**
     * PUBLIC:
     * If prepared statement caching is used, return the cache size.
     * The default is 50.
     */
    public int getStatementCacheSize() {
        return getPlatform().getStatementCacheSize();
    }

    /**
     * PUBLIC:
     * Used to help bean introspection.
     */
    public int getStringBindingSize() {
        return getPlatform().getStringBindingSize();
    }

    /**
     * PUBLIC: 
     * Get the String used on all table creation statements generated from the DefaultTableGenerator
     * with a session using this project (DDL generation).  This value will be appended to CreationSuffix strings
     * stored on the DatabaseTable or TableDefinition.  
     */
    public String getTableCreationSuffix(){
        return getPlatform().getTableCreationSuffix();
    }

    /**
     * PUBLIC:
     * Return the transaction isolation setting for the connection.
     * Return -1 if it has not been set.
     */
    public int getTransactionIsolation() {
        return getPlatform().getTransactionIsolation();
    }

    /**
     * PUBLIC:
     * Used to help bean introspection.
     */
    public boolean getUsesBinding() {
        return shouldUseByteArrayBinding();
    }

    /**
     * PUBLIC:
     * Used to help bean introspection.
     */
    public boolean getUsesNativeSequencing() {
        return shouldUseNativeSequencing();
    }

    /**
     * PUBLIC:
     * Used to help bean introspection.
     */
    public boolean getUsesNativeSQL() {
        return shouldUseNativeSQL();
    }

    /**
     * PUBLIC:
     * Used to help bean introspection.
     */
    public boolean getUsesStreamsForBinding() {
        return shouldUseStreamsForBinding();
    }

    /**
     * PUBLIC:
     * Used to help bean introspection.
     */
    public boolean getUsesStringBinding() {
        return getPlatform().usesStringBinding();
    }

    /**
     * PUBLIC:
     * Force EclipseLink to manually begin transactions instead of using autoCommit.
     * Although autoCommit should be used, and work, under JDBC,
     * some drivers (e.g. Sybase JConnect)
     * do not correctly map autoCommit to transactions, so stored procedures
     * may not work correctly.
     * This property should only be used as a workaround for the
     * Sybase JConnect transaction problem.
     */
    public void handleTransactionsManuallyForSybaseJConnect() {
        getPlatform().setSupportsAutoCommit(false);
    }

    /**
     * PUBLIC:
     * Return whether an Oracle JDBC driver is being used.
     */
    public boolean isAnyOracleJDBCDriver() {
        return oracleDriverIs("jdbc:oracle:");
    }

    /**
     * PUBLIC:
     * Return whether a Cloudscape JDBC driver is being used.
     */
    public boolean isCloudscapeJDBCDriver() {
        return driverIs("COM.cloudscape.core.JDBCDriver");
    }

    /**
     * PUBLIC:
     * Return whether an IBM DB2 native client JDBC driver is being used.
     */
    public boolean isDB2JDBCDriver() {
        return driverIs("COM.ibm.db2.jdbc.app.DB2Driver");
    }

    /**
     * PUBLIC:
     * Return whether an Intersolv SeqeLink JDBC driver is being used.
     */
    public boolean isIntersolvSequeLinkDriver() {
        return driverIs("intersolv.jdbc.sequelink.SequeLinkDriver");
    }

    /**
     * PUBLIC:
     * Return whether a Sybase JConnect JDBC driver is being used.
     */
    public boolean isJConnectDriver() {
        return driverIs("com.sybase.jdbc.SybDriver");
    }

    /**
     * PUBLIC:
     * Return whether a Borland JDBCConnect JDBC driver is being used.
     */
    public boolean isJDBCConnectDriver() {
        return driverIs("borland.jdbc.Bridge.LocalDriver");
    }

    /**
     * PUBLIC:
     * Return whether a Borland JDBCConnect JDBC driver is being used.
     */
    public boolean isJDBCConnectRemoteDriver() {
        return driverIs("borland.jdbc.Broker.RemoteDriver");
    }

    /**
     * PUBLIC:
     * Return whether a Sun/Merant JDBC-ODBC bridge driver is being used.
     */
    public boolean isJDBCODBCBridge() {
        return driverIs("sun.jdbc.odbc.JdbcOdbcDriver");
    }

    /**
     * PUBLIC:
     * Return whether an Oracle native 7.x OCI JDBC driver is being used.
     */
    public boolean isOracle7JDBCDriver() {
        return oracleDriverIs("jdbc:oracle:oci7:@");
    }

    /**
     * PUBLIC:
     * Return whether an Oracle 8.x native OCI JDBC driver is being used.
     */
    public boolean isOracleJDBCDriver() {
        return oracleDriverIs("jdbc:oracle:oci8:@");
    }

    /**
     * PUBLIC:
     * Return whether an Oracle thin JDBC driver is being used.
     */
    public boolean isOracleServerJDBCDriver() {
        return oracleDriverIs("jdbc:oracle:kprb:");
    }

    /**
     * PUBLIC:
     * Return whether an Oracle thin JDBC driver is being used.
     */
    public boolean isOracleThinJDBCDriver() {
        return oracleDriverIs("jdbc:oracle:thin:@");
    }

    /**
     * PUBLIC:
     * Return whether a WebLogic Oracle OCI JDBC driver is being used.
     */
    public boolean isWebLogicOracleOCIDriver() {
        return driverIs("weblogic.jdbc.oci.Driver");
    }

    /**
     * PUBLIC:
     * Return whether a WebLogic SQL Server dblib JDBC driver is being used.
     */
    public boolean isWebLogicSQLServerDBLibDriver() {
        return driverIs("weblogic.jdbc.dblib.Driver");
    }

    /**
     * PUBLIC:
     * Return whether a WebLogic SQL Server JDBC driver is being used.
     */
    public boolean isWebLogicSQLServerDriver() {
        return driverIs("weblogic.jdbc.mssqlserver4.Driver");
    }

    /**
     * PUBLIC:
     * Return whether a WebLogic Sybase dblib JDBC driver is being used.
     */
    public boolean isWebLogicSybaseDBLibDriver() {
        return driverIs("weblogic.jdbc.dblib.Driver");
    }

    /**
     * PUBLIC:
     * Return whether a WebLogic thin client JDBC driver is being used.
     */
    public boolean isWebLogicThinClientDriver() {
        return driverIs("weblogic.jdbc.t3Client.Driver");
    }

    /**
     * PUBLIC:
     * Return whether a WebLogic thin JDBC driver is being used.
     */
    public boolean isWebLogicThinDriver() {
        return driverIs("weblogic.jdbc.t3.Driver");
    }

    /**
     * PUBLIC:
     * Enable driver level data conversion optimization.
     * This can be disabled as some drivers perform data conversion themselves incorrectly.
     */
    public void optimizeDataConversion() {
        setShouldOptimizeDataConversion(true);
    }

    /**
     * INTERNAL:
     * Return whether the specified Oracle JDBC driver is being used.
     */
    protected boolean oracleDriverIs(String urlPrefix) {
        try {
            if (getDriverURLHeader().length() != 0) {
                return getDriverURLHeader().indexOf(urlPrefix) != -1;
            } else {
                return getDatabaseURL().indexOf(urlPrefix) != -1;
            }
        } catch (ValidationException e) {
            // this exception will be thrown if we are using something other than a DefaultConnector
            return false;
        }
    }

    /**
     * PUBLIC:
     * Set the JDBC connection string.
     * This is the full JDBC connect URL. Normally EclipseLink breaks this into two parts to
     * allow for the driver header to be automatically set, however sometimes it is easier just to set the
     * entire URL at once.
     */
    public void setConnectionString(String url) throws ValidationException {
        setDriverURLHeader("");
        setDatabaseURL(url);
    }

    /**
     * PUBLIC:
     * Set the JDBC URL.
     * This is the full JDBC connect URL. Normally EclipseLink breaks this into two parts to
     * allow for the driver header to be automatically set, however sometimes it is easier just to set the
     * entire URL at once.
     */
    public void setURL(String url) {
        setDriverURLHeader("");
        setDatabaseURL(url);
    }

    /**
     * PUBLIC:
     * Return the JDBC URL.
     * This is the full JDBC connect URL. Normally EclipseLink breaks this into two parts to
     * allow for the driver header to be automatically set, however sometimes it is easier just to set the
     * entire URL at once.
     */
    public String getURL() {
        return getConnectionString();
    }

    /**
     * ADVANCED:
     * Set the code for preparing cursored output
     * parameters in a stored procedure
     */
    public void setCursorCode(int cursorCode) {
        getPlatform().setCursorCode(cursorCode);
    }

    /**
     * PUBLIC:
     * The database name is required when connecting to databases that support
     * multiple databases within a single server instance (e.g. Sybase, SQL Server).
     * This is ONLY used when connecting through ODBC type JDBC drivers.
     * This is NEVER used with Oracle.
     */
    public void setDatabaseName(String databaseName) {
        setProperty("database", databaseName);
    }

    /**
     * PUBLIC:
     * The database URL is the JDBC URL for the database server.
     * The driver header should <i>not</i> be included in this URL
     * (e.g. "dbase files"; not "jdbc:odbc:dbase files").
     */
    public void setDatabaseURL(String databaseURL) throws ValidationException {
        getDefaultConnector().setDatabaseURL(databaseURL);
    }

    
    /**
     * PUBLIC:
     * The data source name is required if connecting through ODBC (JDBC-ODBC, etc.).
     * This is the ODBC name given in the ODBC Data Source Administrator.
     * This is just the database part of the URL.
     */
    public void setODBCDataSourceName(String dataSourceName) {
        setDatabaseURL(dataSourceName);
    }

    /**
     * PUBLIC:
     * EclipseLink will attempt to test a connection if it encounters an exception on the connection
     * when executing SQL.  This attribute represents the SQL query that will be executed by EclipseLink.
     * By default EclipseLink uses a query that should be correct for the specified platform but users
     * may need or want to override that query.
     */
    public void setPingSQL(String pingSQL){
        getPlatform().setPingSQL(pingSQL);
    }
    
    /**
     * PUBLIC:
     * Set the number of attempts EclipseLink should make to re-connect to a database and re-execute 
     * a query after a query has failed because of a communication issue.
     * EclipseLink will only attempt to reconnect when EclipseLink can determine that a communication failure occurred
     * on a read query executed outside of a transaction.  By default EclipseLink will attempt to retry the
     * query 3 times, by setting this value to 0 EclipseLink will not retry queries.
     */
    public void setQueryRetryAttemptCount(int queryRetryAttemptCount) {
        this.queryRetryAttemptCount = queryRetryAttemptCount;
    }

    /**
     * PUBLIC:
     * The default value to substitute for database NULLs can be configured
     * on a per-class basis.
     * Example: login.setDefaultNullValue(long.class, new Long(0))
     */
    public void setDefaultNullValue(Class type, Object value) {
        getPlatform().getConversionManager().setDefaultNullValue(type, value);
    }

    /**
     * PUBLIC:
     * The driver class is the Java class for the JDBC driver to be used
     * (e.g. sun.jdbc.odbc.JdbcOdbcDriver.class).
     */
    public void setDriverClass(Class driverClass) {
        setDriverClassName(driverClass.getName());
    }

    /**
     * PUBLIC:
     * The name of the JDBC driver class to be used
     * (e.g. "sun.jdbc.odbc.JdbcOdbcDriver").
     */
    public void setDriverClassName(String driverClassName) throws ValidationException {
        getDefaultConnector().setDriverClassName(driverClassName);
    }

    /**
     * PUBLIC:
     * The driver URL header is the string predetermined by the JDBC driver to be
     * part of the URL connection string, (e.g. "jdbc:odbc:").
     * This is required to connect to the database.
     */
    public void setDriverURLHeader(String driverURLHeader) throws ValidationException {
        getDefaultConnector().setDriverURLHeader(driverURLHeader);
    }

    /**
     * PUBLIC:
     * Allow for the max batch writing size to be set.
     * This allows for the batch size to be limited as most database have strict limits.
     * If set to 0 then default value is used.
     * The size is in characters, the default is 32000 but the real value depends on the database configuration.
     */
    public void setMaxBatchWritingSize(int maxBatchWritingSize) {
        getPlatform().setMaxBatchWritingSize(maxBatchWritingSize);
    }


    /**
     * PUBLIC:
     * The server name is the name of the database instance.
     * This is ONLY used when connecting through ODBC type JDBC drivers,
     * and only if the data source does not specify it already.
     */
    public void setServerName(String name) {
        setProperty("server", name);
    }

    /**
     * PUBLIC:
     * Set whether to bind all arguments to any SQL statement.
     */
    public void setShouldBindAllParameters(boolean shouldBindAllParameters) {
        getPlatform().setShouldBindAllParameters(shouldBindAllParameters);
    }

    /**
     * PUBLIC:
     * Set whether prepared statements should be cached.
     */
    public void setShouldCacheAllStatements(boolean shouldCacheAllStatements) {
        getPlatform().setShouldCacheAllStatements(shouldCacheAllStatements);
    }

    /**
     * ADVANCED:
     * This setting can be used if the application expects upper case
     * but the database does not return consistent case (e.g. different databases).
     */
    public void setShouldForceFieldNamesToUpperCase(boolean shouldForceFieldNamesToUpperCase) {
        getPlatform().setShouldForceFieldNamesToUpperCase(shouldForceFieldNamesToUpperCase);
    }

    /**
     * ADVANCED:
     * Allow for case in field names to be ignored as some databases are not case sensitive.
     * When using custom this can be an issue if the fields in the descriptor have a different case.
     */
    public static void setShouldIgnoreCaseOnFieldComparisons(boolean shouldIgnoreCaseOnFieldComparisons) {
        DatabasePlatform.setShouldIgnoreCaseOnFieldComparisons(shouldIgnoreCaseOnFieldComparisons);
    }

    /**
     * PUBLIC:
     * Set whether driver level data conversion optimization is enabled.
     * This can be disabled as some drivers perform data conversion themselves incorrectly.
     */
    public void setShouldOptimizeDataConversion(boolean value) {
        getPlatform().setShouldOptimizeDataConversion(value);
    }

    /**
     * PUBLIC:
     * By default CHAR field values have trailing blanks trimmed, this can be configured.
     */
    public void setShouldTrimStrings(boolean shouldTrimStrings) {
        getPlatform().setShouldTrimStrings(shouldTrimStrings);
    }

    /**
     * PUBLIC:
     * If prepared statement caching is used this configures the cache size.
     * The default is 50.
     */
    public void setStatementCacheSize(int size) {
        getPlatform().setStatementCacheSize(size);
    }

    /**
     * PUBLIC:
     * Used to help bean introspection.
     */
    public void setStringBindingSize(int stringBindingSize) {
        getPlatform().setStringBindingSize(stringBindingSize);
    }

    /**
     * PUBLIC: 
     * Get the String used on all table creation statements generated from the DefaultTableGenerator
     * with a session using this project (DDL generation).  This value will be appended to CreationSuffix strings
     * stored on the DatabaseTable or TableDefinition.  
     * ie setTableCreationSuffix("engine=InnoDB");
     */
    public void setTableCreationSuffix(String tableCreationSuffix){
        this.getPlatform().setTableCreationSuffix(tableCreationSuffix);
    }

    /**
     * PUBLIC:
     * Set the default qualifier for all tables.
     * This can be the creator of the table or database name the table exists on.
     * This is required by some databases such as Oracle and DB2.
     */
    public void setTableQualifier(String qualifier) {
        getPlatform().setTableQualifier(qualifier);
    }

    /**
     * PUBLIC:
     * Set the transaction isolation setting for the connection.
     * This is an optional setting. The default isolation level
     * set on the database will apply if it is not set here.
     * Use one of the TRANSACTION_* constants for valid input values.
     * Note: This setting will only take effect upon connection.
     */
    public void setTransactionIsolation(int isolationLevel) {
        getPlatform().setTransactionIsolation(isolationLevel);
    }

    /**
     * PUBLIC:
     * EclipseLink can be configured to use batch writing. This facility allows multiple write operations to be
     * submitted to a database for processing at once. Submitting multiple updates together, instead of
     * individually, can greatly improve performance in some situations.
     */
    public void setUsesBatchWriting(boolean value) {
        getPlatform().setUsesBatchWriting(value);
    }


    /**
     * PUBLIC:
     * EclipseLink can be configured to use parameter binding for large binary data.
     * By default EclipseLink will print this data as hex through the JDBC binary excape clause.
     * Both binding and printing have various limits on all databases (e.g. 5k - 32k).
     */
    public void setUsesByteArrayBinding(boolean value) {
        getPlatform().setUsesByteArrayBinding(value);
    }

    /**
     * PUBLIC: Calling this method with an argument of true indicates to EclipseLink that
     * the JDBC driver supports batch writing. EclipseLink's internal batch writing is disabled.<p></p>
     * Calling this method with an argument of false indicates to EclipseLink that the
     * JDBC driver does not support batch writing. This will revert to the default
     * behavior which is to delegate to EclipseLink's internal batch writing.
     * @param usesJDBCBatchWriting boolean true delegates batch writing to the
     * JDBC driver and false delegates batch writing to EclipseLink.
     */
    public void setUsesJDBCBatchWriting(boolean usesJDBCBatchWriting) {
        getPlatform().setUsesJDBCBatchWriting(usesJDBCBatchWriting);
    }

    /**
     * PUBLIC:
     * EclipseLink can be configured to use database specific sql grammar not JDBC specific.
     * This is because unfortunately some bridges to not support the full JDBC standard.
     * By default EclipseLink uses the JDBC sql grammar.
     */
    public void setUsesNativeSQL(boolean value) {
        getPlatform().setUsesNativeSQL(value);
    }

    /**
     * PUBLIC:
     * EclipseLink can be configured to use streams to store large binary data.
     * This can improve the max size for reading/writing on some JDBC drivers.
     */
    public void setUsesStreamsForBinding(boolean value) {
        getPlatform().setUsesStreamsForBinding(value);
    }

    /**
     * PUBLIC:
     * Used to help bean introspection.
     */
    public void setUsesStringBinding(boolean usesStringBindingSize) {
        getPlatform().setUsesStringBinding(usesStringBindingSize);
    }
    
    /**
     * PUBLIC:
     * Return callback.
     * Used to integrate with data partitioning in an external DataSource such as UCP.
     */
    public DataPartitioningCallback getPartitioningCallback() {
        return getPlatform().getPartitioningCallback();
    }

    /**
     * PUBLIC:
     * Set callback.
     * Used to integrate with data partitioning in an external DataSource such as UCP.
     */
    public void setPartitioningCallback(DataPartitioningCallback partitioningCallback) {
        getPlatform().setPartitioningCallback(partitioningCallback);
    }

    /**
     * PUBLIC:
     * Bind all arguments to any SQL statement.
     */
    public boolean shouldBindAllParameters() {
        return getPlatform().shouldBindAllParameters();
    }

    /**
     * PUBLIC:
     * Cache all prepared statements, this requires full parameter binding as well.
     */
    public boolean shouldCacheAllStatements() {
        return getPlatform().shouldCacheAllStatements();
    }

    /**
     * ADVANCED:
     * Can be used if the app expects upper case but the database is not return consistent case, i.e. different databases.
     */
    public boolean shouldForceFieldNamesToUpperCase() {
        return getPlatform().shouldForceFieldNamesToUpperCase();
    }

    /**
     * ADVANCED:
     * Allow for case in field names to be ignored as some databases are not case sensitive.
     * When using custom this can be an issue if the fields in the descriptor have a different case.
     */
    public static boolean shouldIgnoreCaseOnFieldComparisons() {
        return DatabasePlatform.shouldIgnoreCaseOnFieldComparisons();
    }

    /**
     * PUBLIC:
     * Return if our driver level data conversion optimization is enabled.
     * This can be disabled as some drivers perform data conversion themselves incorrectly.
     */
    public boolean shouldOptimizeDataConversion() {
        return getPlatform().shouldOptimizeDataConversion();
    }

    /**
     * PUBLIC:
     * By default CHAR field values have trailing blanks trimmed, this can be configured.
     */
    public boolean shouldTrimStrings() {
        return getPlatform().shouldTrimStrings();
    }

    /**
     * PUBLIC:
     * EclipseLink can be configured to use batch writing. This facility allows multiple write operations to be
     * submitted to a database for processing at once. Submitting multiple updates together, instead of
     * individually, can greatly improve performance in some situations.
     */
    public boolean shouldUseBatchWriting() {
        return getPlatform().usesBatchWriting();
    }

    /**
     * PUBLIC:
     * EclipseLink can be configured to use parameter binding for large binary data.
     * By default EclipseLink will print this data as hex through the JDBC binary escape clause.
     * Both binding and printing have various limits on all databases (e.g. 5k - 32k).
     */
    public boolean shouldUseByteArrayBinding() {
        return getPlatform().usesByteArrayBinding();
    }

    /**
     * PUBLIC: Answers true if EclipseLink has JDBC batch writing enabled.
     * This is the case if setUsesJDBCBatchWriting(true) has been called.
     * @return boolean true if batch writing is delegated to the JDBC driver.
     * Returns false if delegated to EclipseLink.
     * @see #useJDBCBatchWriting
     * @see #dontUseJDBCBatchWriting
     * @see #setUsesJDBCBatchWriting(boolean usesJDBCBatchWriting)
     */
    public boolean shouldUseJDBCBatchWriting() {
        return getPlatform().usesJDBCBatchWriting();
    }

    /**
     * PUBLIC:
     * EclipseLink can be configured to use a sequence table
     * or native sequencing to generate unique object IDs.
     * Native sequencing uses the ID generation service provided by the database
     * (e.g. SEQUENCE objects on Oracle and IDENTITY columns on Sybase).
     * By default a sequence table is used. Using a sequence table is recommended
     * as it supports preallocation.
     * (Native sequencing on Sybase/SQL Server/Informix does not support preallocation.
     * Preallocation can be supported on Oracle by setting the increment size of the
     * SEQUENCE object to match the preallocation size.)
     */
    public boolean shouldUseNativeSequencing() {
        return getPlatform().getDefaultSequence() instanceof NativeSequence;
    }

    /**
     * PUBLIC:
     * EclipseLink can be configured to use database-specific SQL grammar,
     * as opposed to the JDBC standard grammar.
     * This is because, unfortunately, some drivers to not support the full JDBC standard.
     * By default EclipseLink uses the JDBC SQL grammar.
     */
    public boolean shouldUseNativeSQL() {
        return getPlatform().usesNativeSQL();
    }

    /**
     * PUBLIC:
     * EclipseLink can be configured to use streams to store large binary data.
     */
    public boolean shouldUseStreamsForBinding() {
        return getPlatform().usesStreamsForBinding();
    }

    /**
     * PUBLIC:
     * EclipseLink can be configured to bind large strings.
     */
    public boolean shouldUseStringBinding() {
        return getPlatform().usesStringBinding();
    }

    /**
     * PUBLIC:
     * Print all of the connection information.
     */
    public String toString() {
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        writer.println("DatabaseLogin(");
        writer.println("\t" + ToStringLocalization.buildMessage("platform", (Object[])null) + "=>" + getDatasourcePlatform());
        writer.println("\t" + ToStringLocalization.buildMessage("user_name", (Object[])null) + "=> \"" + getUserName() + "\"");
        writer.print("\t");
        getConnector().toString(writer);
        if (getServerName() != null) {
            writer.println("\t" + ToStringLocalization.buildMessage("server_name", (Object[])null) + "=> \"" + getServerName() + "\"");
        }
        if (getDatabaseName() != null) {
            writer.println("\t" + ToStringLocalization.buildMessage("database_name", (Object[])null) + "=> \"" + getDatabaseName() + "\"");
        }
        writer.write(")");
        return stringWriter.toString();
    }

    /**
     * PUBLIC:
     * Set the database platform to be Access.
     */
    public void useAccess() {
        if (getPlatform().isAccess()) {
            return;
        }

        DatabasePlatform newPlatform = new AccessPlatform();
        getPlatform().copyInto(newPlatform);
        setPlatform(newPlatform);
    }

    /**
     * PUBLIC:
     * EclipseLink can be configured to use batch writing. This facility allows multiple write operations to be
     * submitted to a database for processing at once. Submitting multiple updates together, instead of
     * individually, can greatly improve performance in some situations.
     */
    public void useBatchWriting() {
        setUsesBatchWriting(true);
    }

    /**
     * PUBLIC:
     * EclipseLink can be configured to use parameter binding for large binary data.
     * By default EclipseLink will print this data as hex through the JDBC binary excape clause.
     * Both binding and printing have various limits on all databases (e.g. 5k - 32k).
     */
    public void useByteArrayBinding() {
        setUsesByteArrayBinding(true);
    }

    /**
     * PUBLIC:
     * Set the database platform to be Cloudscape.
     */
    public void useCloudscape() {
        if (getPlatform().isCloudscape()) {
            return;
        }

        DatabasePlatform newPlatform = new CloudscapePlatform();
        getPlatform().copyInto(newPlatform);
        setPlatform(newPlatform);
    }

    public void useDerby() {
        if (getPlatform().isDerby()) {
            return;
        }

        DatabasePlatform newPlatform = new DerbyPlatform();
        getPlatform().copyInto(newPlatform);
        setPlatform(newPlatform);
    }


    /**
     * PUBLIC:
     * Use the Cloudscape JDBC driver.
     */
    public void useCloudscapeDriver() {
        useCloudscape();
        setDriverClassName("COM.cloudscape.core.JDBCDriver");
        setDriverURLHeader("jdbc:cloudscape:");
    }

    /**
     * PUBLIC:
     * Set the database platform to be DB2.
     */
    public void useDB2() {
        if (getPlatform().isDB2()) {
            return;
        }

        DatabasePlatform newPlatform = new DB2Platform();
        getPlatform().copyInto(newPlatform);
        setPlatform(newPlatform);
    }

    /**
     * PUBLIC:
     * Use the IBM DB2 native client interface.
     */
    public void useDB2JDBCDriver() {
        useDB2();
        setDriverClassName("COM.ibm.db2.jdbc.app.DB2Driver");
        setDriverURLHeader("jdbc:db2:");
        useStreamsForBinding();// Works best with IBM driver
    }

    /**
     * PUBLIC:
     * Use the IBM DB2 thin JDBC driver.
     */
    public void useDB2NetJDBCDriver() {
        useDB2();
        setDriverClassName("COM.ibm.db2.jdbc.net.DB2Driver");
        setDriverURLHeader("jdbc:db2:");
        useStreamsForBinding();// Works best with IBM driver
    }

    /**
     * PUBLIC:
     * Set the database platform to be DBase.
     */
    public void useDBase() {
        if (getPlatform().isDBase()) {
            return;
        }

        DatabasePlatform newPlatform = new DBasePlatform();
        getPlatform().copyInto(newPlatform);
        setPlatform(newPlatform);
    }

    /**
     * PUBLIC:
     * Connect to the JDBC driver via DriverManager.
     * @see #useDirectDriverConnect()
     */
    public void useDefaultDriverConnect() {
        setConnector(new DefaultConnector());
    }

    /**
     * PUBLIC:
     * Connect to the JDBC driver via DriverManager.
     * @see #useDirectDriverConnect(String, String, String)
     */
    public void useDefaultDriverConnect(String driverClassName, String driverURLHeader, String databaseURL) {
        setConnector(new DefaultConnector(driverClassName, driverURLHeader, databaseURL));
    }

    /**
     * PUBLIC:
     * Some JDBC drivers don't support connecting correctly (via DriverManager),
     * but do support connecting incorrectly (e.g. Castanet).
     * @see #useDirectDriverConnect()
     */
    public void useDirectDriverConnect() {
        setConnector(new DirectConnector());
    }
    
    /**
     * PUBLIC:
     * Specify the J2EE DataSource name to connect to.
     * Also enable external connection pooling.
     * @see JNDIConnector
     */
    public void useDataSource(String dataSource) {
        setConnector(new JNDIConnector(dataSource));
        useExternalConnectionPooling();
    }
    
    /**
     * PUBLIC:
     * Specify the J2EE JTA enabled DataSource name to connect to.
     * Also enable external transaction control and connection pooling.
     * @see JNDIConnector
     */
    public void useJTADataSource(String dataSource) {
        useDataSource(dataSource);
        useExternalTransactionController();
    }

    /**
     * PUBLIC:
     * Some JDBC drivers don't support connecting correctly (via DriverManager),
     * but do support connecting incorrectly (e.g. Castanet).
     * @see #useDefaultDriverConnect(String, String, String)
     */
    public void useDirectDriverConnect(String driverClassName, String driverURLHeader, String databaseURL) {
        setConnector(new DirectConnector(driverClassName, driverURLHeader, databaseURL));
    }

    /**
     * PUBLIC:
     * Use external connection pooling, such as WebLogic's JTS driver.
     *
     * @see #dontUseExternalConnectionPooling()
     * @see #shouldUseExternalConnectionPooling()
     */
    public void useExternalConnectionPooling() {
        setUsesExternalConnectionPooling(true);
    }

    /**
     * PUBLIC:
     * Use an external transaction controller such as a JTS service
     *
     * @see #dontUseExternalTransactionController()
     * @see #shouldUseExternalTransactionController()
     */
    public void useExternalTransactionController() {
        setUsesExternalTransactionController(true);
    }

    /**
     * PUBLIC:
     * Use the HSQL JDBC driver.
     */
    public void useHSQL() {
        if (getPlatform().isHSQL()) {
            return;
        }

        DatabasePlatform newPlatform = new HSQLPlatform();
        getPlatform().copyInto(newPlatform);
        setPlatform(newPlatform);
    }

    /**
     * PUBLIC:
     * Use the HSQL JDBC driver.
     */
    public void useHSQLDriver() {
        useHSQL();
        setDriverClassName("org.hsqldb.jdbcDriver");
        setDriverURLHeader("jdbc:hsqldb:");
    }

    /**
     * PUBLIC:
     * Use the i-net SQL Server JDBC driver.
     */
    public void useINetSQLServerDriver() {
        setDriverClassName("com.inet.tds.TdsDriver");
        setDriverURLHeader("jdbc:inetdae:");
    }

    /**
     * PUBLIC:
     * Set the database platform to be Informix.
     */
    public void useInformix() {
        if (getPlatform().isInformix()) {
            return;
        }

        DatabasePlatform newPlatform = new InformixPlatform();
        getPlatform().copyInto(newPlatform);
        setPlatform(newPlatform);
    }

    /**
     * PUBLIC:
     * Use the Intersolv/Merant SequeLink JDBC driver.
     */
    public void useIntersolvSequeLinkDriver() {
        setDriverClassName("intersolv.jdbc.sequelink.SequeLinkDriver");
        setDriverURLHeader("jdbc:sequelink:");
    }

    /**
     * PUBLIC:
     * Use the Sybase JConnect JDBC driver.
     */
    public void useJConnect50Driver() {
        useSybase();
        setDriverClassName("com.sybase.jdbc2.jdbc.SybDriver");
        setDriverURLHeader("jdbc:sybase:Tds:");
        // JConnect does not support the JDBC SQL grammar
        useNativeSQL();
    }

    /**
     * PUBLIC:
     * Use the Sybase JConnect JDBC driver.
     */
    public void useJConnectDriver() {
        useSybase();
        setDriverClassName("com.sybase.jdbc.SybDriver");
        setDriverURLHeader("jdbc:sybase:Tds:");
        // JConnect does not support the JDBC SQL grammar
        useNativeSQL();
    }

    /**
     * PUBLIC:
     * Set the database platform to be JDBC.
     */
    public void useJDBC() {
        DatabasePlatform newPlatform = new DatabasePlatform();
        getPlatform().copyInto(newPlatform);
        setPlatform(newPlatform);
    }

    /**
     * PUBLIC:
     * EclipseLink support batch writing in both JDK 1.1 abd JDK 1.2.
     * In JDK 1.2 either the batch support provided by the JDBC driver can be used,
     * or EclipseLink's built-in support, this allow for this to be set.
     * By default the driver is used in JDK 1.2.
     * Some JDBC 2 drivers to not support batching, so this lets are own batching be used.
     */
    public void useJDBCBatchWriting() {
        setUsesJDBCBatchWriting(true);
    }

    /**
     * PUBLIC:
     * Use the Borland JDBCConnect JDBC driver.
     */
    public void useJDBCConnectDriver() {
        setDriverClassName("borland.jdbc.Bridge.LocalDriver");
        setDriverURLHeader("jdbc:BorlandBridge:");
    }

    /**
     * PUBLIC:
     * Use the Borland JDBCConnect JDBC driver.
     */
    public void useJDBCConnectRemoteDriver() {
        setDriverClassName("borland.jdbc.Broker.RemoteDriver");
        setDriverURLHeader("jdbc:BorlandBridge:");
    }

    /**
     * PUBLIC:
     * User the Sun/Merant JDBC-ODBC bridge driver.
     */
    public void useJDBCODBCBridge() {
        setDriverClassName("sun.jdbc.odbc.JdbcOdbcDriver");
        setDriverURLHeader("jdbc:odbc:");
    }

    /**
     * PUBLIC:
     * Set the database platform to be MySQL.
     */
    public void useMySQL() {
        if (getPlatform().isMySQL()) {
            return;
        }

        DatabasePlatform newPlatform = new MySQLPlatform();
        getPlatform().copyInto(newPlatform);
        setPlatform(newPlatform);
    }

    /**
     * PUBLIC:
     * EclipseLink can be configured to use a sequence table
     * or native sequencing to generate unique object IDs.
     * Native sequencing uses the ID generation service provided by the database
     * (e.g. SEQUENCE objects on Oracle and IDENTITY columns on Sybase).
     * By default a sequence table is used. Using a sequence table is recommended
     * as it supports preallocation.
     * (Native sequencing on Sybase/SQL Server/Informix does not support preallocation.
     * Preallocation can be supported on Oracle by setting the increment size of the
     * SEQUENCE object to match the preallocation size.)
     */
    public void useNativeSequencing() {
        if(!shouldUseNativeSequencing()) {
            getPlatform().setDefaultSequence(new NativeSequence(getPlatform().getDefaultSequence().getName(), 
                    getPlatform().getDefaultSequence().getPreallocationSize(),
                    getPlatform().getDefaultSequence().getInitialValue()));
        }
    }

    /**
     * PUBLIC:
     * EclipseLink can be configured to use database-specific SQL grammar,
     * as opposed to the JDBC standard grammar.
     * This is because, unfortunately, some drivers to not support the full JDBC standard.
     * By default EclipseLink uses the JDBC SQL grammar.
     */
    public void useNativeSQL() {
        setUsesNativeSQL(true);
    }

    /**
     * PUBLIC:
     * Set the database platform to be Oracle.
     */
    public void useOracle() {
        if (getPlatform().isOracle()) {
            return;
        }

        DatabasePlatform newPlatform = new OraclePlatform();
        getPlatform().copyInto(newPlatform);
        setPlatform(newPlatform);
    }

    /**
     * PUBLIC:
     * Use the Oracle 7.x native OCI JDBC driver.
     */
    public void useOracle7JDBCDriver() {
        useOracle();
        setDriverClassName("oracle.jdbc.OracleDriver");
        setDriverURLHeader("jdbc:oracle:oci7:@");
        // Oracle works best with stream binding.
        useByteArrayBinding();
        useStreamsForBinding();
    }

    /**
     * PUBLIC:
     * Use the Oracle 8.x native OCI JDBC driver.
     */
    public void useOracleJDBCDriver() {
        useOracle();
        setDriverClassName("oracle.jdbc.OracleDriver");
        setDriverURLHeader("jdbc:oracle:oci8:@");
        // Oracle works best with stream binding.
        useByteArrayBinding();
        useStreamsForBinding();
    }

    /**
     * PUBLIC:
     * Use the Oracle server JDBC driver.
     */
    public void useOracleServerJDBCDriver() {
        useOracle();
        setDriverClassName("oracle.jdbc.OracleDriver");
        setDriverURLHeader("jdbc:oracle:kprb:");
        // Oracle works best with stream binding.
        useByteArrayBinding();
    }

    /**
     * PUBLIC:
     * Use the Oracle thin JDBC driver.
     */
    public void useOracleThinJDBCDriver() {
        useOracle();
        setDriverClassName("oracle.jdbc.OracleDriver");
        setDriverURLHeader("jdbc:oracle:thin:@");
        // Oracle works best with stream binding.
        useByteArrayBinding();
        useStreamsForBinding();
    }

    /**
     * PUBLIC:
     * Set the database platform to be PointBase.
     */
    public void usePointBase() {
        if (getPlatform().isPointBase()) {
            return;
        }

        DatabasePlatform newPlatform = new PointBasePlatform();
        getPlatform().copyInto(newPlatform);
        setPlatform(newPlatform);
    }

    /**
     * PUBLIC:
     * Use the PointBase JDBC driver.
     */
    public void usePointBaseDriver() {
        usePointBase();
        setDriverClassName("com.pointbase.jdbc.jdbcUniversalDriver");
        setDriverURLHeader("jdbc:pointbase:");
    }

    /**
     * PUBLIC:
     * Set the database platform to be SQL Server.
     */
    public void useSQLServer() {
        if (getPlatform().isSQLServer()) {
            return;
        }

        DatabasePlatform newPlatform = new SQLServerPlatform();
        getPlatform().copyInto(newPlatform);
        setPlatform(newPlatform);
    }

    /**
     * PUBLIC:
     * Set the database platform to be Symfoware.
     */
    public void useSymfoware() {
        if (getPlatform().isSymfoware()) {
            return;
        }

        DatabasePlatform newPlatform = new SymfowarePlatform();
        getPlatform().copyInto(newPlatform);
        setPlatform(newPlatform);
    }

    /**
     * PUBLIC:
     * EclipseLink can be configured to use streams to store large binary data.
     */
    public void useStreamsForBinding() {
        setUsesStreamsForBinding(true);
    }

    /**
     * PUBLIC:
     * Bind strings larger than 255 characters.
     */
    public void useStringBinding() {
        this.useStringBinding(255);
    }

    /**
     * PUBLIC:
     * Bind strings that are larger than the specified size.
     * Strings that are smaller will not be bound.
     */
    public void useStringBinding(int size) {
        getPlatform().setStringBindingSize(size);
        getPlatform().setUsesStringBinding(true);
    }

    /**
     * PUBLIC:
     * Set the database platform to be Sybase.
     */
    public void useSybase() {
        if (getPlatform().isSybase()) {
            return;
        }

        DatabasePlatform newPlatform = new SybasePlatform();
        getPlatform().copyInto(newPlatform);
        setPlatform(newPlatform);
    }

    /**
     * PUBLIC:
     * Set the prepare cursor code to what the WebLogic
     * Oracle OCI JDBC driver expects.
     */
    public void useWebLogicDriverCursoredOutputCode() {
        setCursorCode(1111);
    }

    /**
     * PUBLIC:
     * Set a WebLogic JDBC connection pool (a pool must be defined for the entity beans that are to be deployed)
     */
    public void useWebLogicJDBCConnectionPool(String poolName) {
        setDriverClassName("weblogic.jdbc.jts.Driver");
        setConnectionString("jdbc:weblogic:jts:" + poolName);
    }

    /**
     * PUBLIC:
     * Use the WebLogic Oracle OCI JDBC driver.
     */
    public void useWebLogicOracleOCIDriver() {
        useOracle();
        setDriverClassName("weblogic.jdbc.oci.Driver");
        setDriverURLHeader("jdbc:weblogic:oracle:");
        // WebLogic has a bug converting dates to strings, which our optimizations require.
        dontOptimizeDataConversion();
        useWebLogicDriverCursoredOutputCode();
    }

    /**
     * PUBLIC:
     * Use the WebLogic SQL Server dblib JDBC driver.
     */
    public void useWebLogicSQLServerDBLibDriver() {
        useSQLServer();
        setDriverClassName("weblogic.jdbc.dblib.Driver");
        setDriverURLHeader("jdbc:weblogic:mssqlserver:");
        // WebLogic has a bug converting dates to strings, which our optimizations require.
        dontOptimizeDataConversion();
    }

    /**
     * PUBLIC:
     * Use the WebLogic SQL Server JDBC driver.
     */
    public void useWebLogicSQLServerDriver() {
        useSQLServer();
        setDriverClassName("weblogic.jdbc.mssqlserver4.Driver");
        setDriverURLHeader("jdbc:weblogic:mssqlserver4:");
        // WebLogic has a bug converting dates to strings, which our optimizations require.
        dontOptimizeDataConversion();
    }

    /**
     * PUBLIC:
     * Use the WebLogic Sybase dblib JDBC driver.
     */
    public void useWebLogicSybaseDBLibDriver() {
        useSybase();
        setDriverClassName("weblogic.jdbc.dblib.Driver");
        setDriverURLHeader("jdbc:weblogic:sybase:");
        // WebLogic has a bug converting dates to strings, which our optimizations require.
        dontOptimizeDataConversion();
    }

    /**
     * PUBLIC:
     * Use the WebLogic thin client JDBC driver.
     */
    public void useWebLogicThinClientDriver() {
        setDriverClassName("weblogic.jdbc.t3Client.Driver");
        setDriverURLHeader("jdbc:weblogic:t3Client:");
    }

    /**
     * PUBLIC:
     * Use the WebLogic thin JDBC driver.
     */
    public void useWebLogicThinDriver() {
        setDriverClassName("weblogic.jdbc.t3.Driver");
        setDriverURLHeader("jdbc:weblogic:t3:");
    }
    /** 
     * PUBLIC:
     * Returns the number of milliseconds that EclipseLink will wait between attempts to reconnect a DatabaseConnection
     * in the case EclipseLink is attempting to retry a query, the default is 5000.  EclipseLink will retry a read query outside of a transaction
     * if EclipseLink can determine that a communication error occured with the database.
     */
    public int getDelayBetweenConnectionAttempts() {
        return delayBetweenConnectionAttempts;
    }

    /** 
     * PUBLIC:
     * Stores the number of milliseconds that EclipseLink will wait between attempts to reconnect a DatabaseConnection
     * in the case EclipseLink is attempting to retry a query.  EclipseLink will retry a read query outside of a transaction
     * if EclipseLink can determine that a communication error occurred with the database.
     */
    public void setDelayBetweenConnectionAttempts(int delayBetweenConnectionAttempts) {
        this.delayBetweenConnectionAttempts = delayBetweenConnectionAttempts;
    }

    /**
     * INTERNAL:
     * Validate if set, or no timeout.
     */
    public boolean isConnectionHealthValidatedOnError(DatabaseCall call) {
        if (this.connectionHealthValidatedOnError == null) {
            return (getPingSQL() == null) || (call == null) || (call.getQueryTimeout() == 0);
        }
        return this.connectionHealthValidatedOnError;
    }

    /**
     * PUBLIC:
     * On an SQL Exception EclipseLink will ping the database to determine
     * if the connection used can continue to be used for queries.  This should have no impact on applications
     * unless the user is using pessimistic locking queries with 'no wait' or are using a query timeout feature.
     * If that is the case and the application is experiencing a performance impact from the health check then
     * this feature can be turned off. Turning this feature off will prevent EclipseLink from being able to
     * retry queries in the case of database failure. 
     * By default (null) connection health is validate if the query does not have a timeout, and there is a ping string.
     * Setting to true or false overrides this.
     */
    public boolean isConnectionHealthValidatedOnError() {
        if (this.connectionHealthValidatedOnError == null) {
            return (getPingSQL() == null);
        }
        return this.connectionHealthValidatedOnError;
    }

    /**
     * PUBLIC:
     * On an SQL Exception EclipseLink will ping the database to determine
     * if the connection used can continue to be used for queries.  This should have no impact on applications
     * unless the user is using pessimistic locking queries with 'no wait' or are using a query timeout feature.
     * If that is the case and the application is experiencing a performance impact from the health check then
     * this feature can be turned off. Turning this feature off will prevent EclipseLink from being able to
     * retry queries in the case of database failure. 
     * By default (null) connection health is validate if the query does not have a timeout, and there is a ping string.
     * Setting to true or false overrides this.
     */
    public void setConnectionHealthValidatedOnError(boolean isConnectionHealthValidatedOnError) {
        this.connectionHealthValidatedOnError = isConnectionHealthValidatedOnError;
    }
}
