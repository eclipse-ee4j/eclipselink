/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.sessions;

import java.util.*;
import java.io.*;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.text.MessageFormat;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sequencing.Sequence;
import org.eclipse.persistence.Version;
import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.databaseaccess.DatasourcePlatform;
import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.localization.*;
import org.eclipse.persistence.internal.security.SecurableObjectHolder;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedNewInstanceFromClass;

/**
 * <p>
 * <b>Purpose</b>:
 * Hold the configuration information necessary to connect to a datasource.
 * <p>
 * <b>Description</b>:
 * This is an abstract class that defines the common datasource independent connection configuration.
 * It is extended to support specific datasources such as JDBC, JCA, XML, etc.
 */
public abstract class DatasourceLogin implements org.eclipse.persistence.sessions.Login, Serializable, Cloneable {

    /** Version info */
    private static final String versionStringTemplate = "{0} - {1} (Build {2})";
    public static String versionString = null;

    /** Connection properties (e.g. user, password, and driver-specific settings) */
    protected Properties properties;

    /** Implementation of platform-specific behaviors. */
    protected Platform platform;

    /** findbugs: removed the encrypted String that holds the expiry key */

    /** The securable object holder and flag*/
    private boolean isEncryptedPasswordSet;
    private transient SecurableObjectHolder securableObjectHolder;

    /** Provide a little flexibility for connecting to a database */
    protected Connector connector;

    /** True if we use an external connection pool such as WebLogic's JTS driver */
    protected boolean usesExternalConnectionPooling;

    /** True if we should use some external transaction service such as JTS. */
    protected boolean usesExternalTransactionController;

    /**
     * By default concurrency is optimized and the cache is not locked during reads or writes,
     * This allows for concurrent reading and writing and should never cause any problems.  If the application
     * uses no form of locking the last unit of work to merge changes will win, with no locking it is possible
     * only under this senerio for two unit of works to merge changes different than the database although highly unlikely
     * and if occured is the entire purpose of locking.  This property allows for the isolation level of changes to the
     * cache to be configured for sever situation and it is not suggest that this be changed.
     */
    protected int cacheTransactionIsolation = SYNCRONIZED_OBJECT_LEVEL_READ_WRITE_DATABASE; 

    /** Reads and unit of work merges can occur concurrently. */
    public static final int CONCURRENT_READ_WRITE = 1;

    /** Reads can occur concurrently but unit of work merges will be serialized. */
    public static final int SYNCHRONIZED_WRITE = 2;

    /** Reads and unit of work merges will be serialized. */
    public static final int SYNCHRONIZED_READ_ON_WRITE = 3;
    
    /** Writes to the cache (merge, object build/refresh will be synchronized
     * as will cache access (cloning) based on when access is required.
     */
    public static final int SYNCRONIZED_OBJECT_LEVEL_READ_WRITE = 4;

    /** Writes to the cache (merge, object build/refresh will be synchronized
     * as will cache access (cloning) based on database transaction.
     */
    public static final int SYNCRONIZED_OBJECT_LEVEL_READ_WRITE_DATABASE = 5;

    /**
     * PUBLIC:
     * Create a new login.
     */
    public DatasourceLogin() {
        this(new DatasourcePlatform());
    }

    /**
     * ADVANCED:
     * Create a new login for the given platform.
     */
    public DatasourceLogin(Platform databasePlatform) {
        this.platform = databasePlatform;

        this.dontUseExternalConnectionPooling();
        this.dontUseExternalTransactionController();

        this.properties = new Properties();
        this.properties.put("user", "");
        // Bug 4117441 - Secure programming practices, store password in char[]
        this.properties.put("password", new char[0]);
        this.isEncryptedPasswordSet = false;
        this.securableObjectHolder = new SecurableObjectHolder();
    }

    /**
     * INTERNAL:
     * Return the encryption securable holder.
     * Lazy initialize to handle serialization.
     */
    protected SecurableObjectHolder getSecurableObjectHolder() {
        if (securableObjectHolder == null) {
            securableObjectHolder = new SecurableObjectHolder();
            securableObjectHolder.getSecurableObject();
        }
        return securableObjectHolder;
    }

    /**
     * INTERNAL:
     * Clone the login.
     * This also clones the platform as it is internal to the login.
     */
    public Object clone() {
        DatasourceLogin clone = null;
        try {
            clone = (DatasourceLogin)super.clone();
        } catch (Exception exception) {
            // should not happen...do nothing
        }
        if (getConnector() != null) {
            clone.setConnector((Connector)getConnector().clone());
        }
        clone.setDatasourcePlatform((Platform)getDatasourcePlatform().clone());
        clone.setProperties((Properties)properties.clone());
        return clone;
    }

    /**
     * INTERNAL:
     * Connect to the datasource, and return the driver level connection object.
     */
    public Object connectToDatasource(Accessor accessor, Session session) throws DatabaseException {
        return getConnector().connect(prepareProperties(properties), session);
    }

    /**
     * ADVANCED:
     * By default concurrency is optimized and the cache is not locked more than required during reads or writes,
     * This allows for virtual concurrent reading and writing and should never cause any problems.  If the application
     * uses no form of locking the last unit of work to merge changes will win, with no locking it is possible
     * only under this senerio for two unit of works to merge changes different than the database although highly unlikely
     * and if occured is the entire purpose of locking and locking is the suggested solution if this is a problem.
     * This property allows for the isolation level of changes to the
     * cache to be configured for sever situations and it is not suggest that this be changed.
     * <p>Setting are:<ul>
     * <li>ConcurrentReadWrite - default
     * <li>SynchronizedWrite - only allow a single writer (i.e. unit of work merge) to the cache at once
     * <li>SynchronizedReadOnWrite - do not allow reading or other unit of work merge ehile a unit of work is in merge
     */
    public int getCacheTransactionIsolation() {
        return cacheTransactionIsolation;
    }

    /**
     * ADVANCED:
     * Return the connector that will instantiate the connection.
     */
    public Connector getConnector() {
        return connector;
    }

    /**
     * INTERNAL:
     * Return the database platform specific information.
     * This allows TopLink to configure certain advanced features for the database desired.
     * NOTE: this must only be used for relational specific usage and will not work for
     * non-relational datasources.
     */
    public DatabasePlatform getPlatform() {
        try {
            return (DatabasePlatform)getDatasourcePlatform();
        } catch (ClassCastException wrongType) {
            throw ValidationException.notSupportedForDatasource();
        }
    }

    /**
     * PUBLIC:
     * Return the datasource platform specific information.
     * This allows TopLink to configure certain advanced features for the datasource desired.
     */
    public Platform getDatasourcePlatform() {
        return platform;
    }

    /**
     * INTERNAL:
     * The properties are additional, driver-specific, connection information
     * to be passed to the driver.<p>
     * NOTE: Do not set the password directly by getting the properties and
     * setting the "password" property directly. Use the method DatabaseLogin.setPassword(String).
     */
    public Properties getProperties() {
        return properties;
    }

    /**
     * PUBLIC:
     * The properties are additional, driver-specific, connection information
     * to be passed to the driver.<p>
     * NOTE: Do not set the password directly by getting the properties and
     * setting the "password" property directly. Use the method DatabaseLogin.setPassword(String).
     */
    public Object getProperty(String name) {
        return getProperties().get(name);
    }

    /**
     * OBSOLETE:
     * TopLink supports sequence number preallocation.
     * This improves the performance and concurrency of inserts by
     * selecting new object IDs in batches and caching them on the client.
     * The preallocation size can be configured, and the default is 50.
     * By default a sequence table is used. Using a sequence table is recommended
     * as it supports preallocation.
     * (Native sequencing on Sybase/SQL Server/Informix does not support preallocation.
     * Preallocation can be supported on Oracle by setting the increment size of the
     * SEQUENCE object to match the preallocation size.)
     * @deprecated use getDefaultSequence().getPreallocationSize() instead
     */
    public int getSequencePreallocationSize() {
        return getDatasourcePlatform().getSequencePreallocationSize();
    }

    /**
     * PUBLIC:
     * Return the qualifier for the all of the tables referenced by TopLink.
     * This can be the creator of the table or database name the table exists on.
     * This is required by some databases such as Oracle and DB2.
     * This should only be used if all of the tables have the same qualifier.
     * It can also be set on each descriptor when the table name is specified.
     */
    public String getTableQualifier() {
        return getDatasourcePlatform().getTableQualifier();
    }

    /**
     * PUBLIC:
     * The user name is the database login name.
     * Some databases do not require a user name or the user is obtained from the OS,
     * in this case the user name not be specified.
     */
    public String getUserName() {
        return properties.getProperty("user");
    }


    /**
     * PUBLIC:
     * This value defaults to false when not on a DatabaseLogin as the functionality has not been implemented
     * for other datasource type.  On an SQL Exception TopLink will ping the database to determine
     * if the connection used can continue to be used for queries.  This should have no impact on applications
     * unless the user is using pessimistic locking queries with 'no wait' or are using a query timeout feature.
     * If that is the case and the application is experiencing a performance impact from the health check then
     * this feature can be turned off. Turning this feature off will prevent TopLink from being able to
     * retry queries in the case of database failure. 
     */
    public boolean isConnectionHealthValidatedOnError(){
        return false;
    }
    
    /**
     * PUBLIC:
     * Return the TopLink version.
     * @return version of TopLink
     */
    public static String getVersion() {
        if (versionString == null) {
            Object[] args = { Version.getProduct(), Version.getVersion(), Version.getBuildNumber() };
            versionString = MessageFormat.format(versionStringTemplate, args);
        }
        return versionString;
    }

    /**
     * SECURE:
     * The password in the login properties is encrypted. Return a clone
     * of the properties with the password decrypted.
     */
    private Properties prepareProperties(Properties properties) {
        Properties result = (Properties)properties.clone();
        Object passwordObject = result.get("password");
        if (passwordObject != null) {
            // Fix for bug # 2700529
            // The securable object is initialized on first call of
            // getSecurableObject. When setting an encrypted password
            // we don't make this call since it is already encrypted, hence,
            // we do not initialize the securable object on the holder.
            //
            // If neither setPassword or setEncryptedPassword is called 
            // (example, user sets properties via the setProperties method),
            // when the user tries to connect they  will get a null pointer 
            // exception. So if the holder does not hold 
            // a securable object or the setEncryptedPassword flag is not true, 
            // don't bother trying to decrypt.
            if (getSecurableObjectHolder().hasSecurableObject() || isEncryptedPasswordSet) {
                // Bug 4117441 - Secure programming practices, store password in char[]
                // If isEncryptedPasswordSet is true, or we have a SecurableObject then we stored 
                // the password as a char[], and we need to convert it into a String for the 
                // prepared Properties object.
                String passwordString = null;
                if (passwordObject instanceof char[]) {
                    passwordString = new String((char[])passwordObject);
                } else if (passwordObject instanceof String) {
                    passwordString = (String)passwordObject;
                }
                result.put("password", getSecurableObjectHolder().getSecurableObject().decryptPassword(passwordString));
            }
        }

        return result;
    }

    /**
     * PUBLIC:
     * Some drivers don't like the "user" and "password" properties.
     * They can be removed with this method.
     */
    public void removeProperty(String propertyName) {
        properties.remove(propertyName);
    }

    /**
     * ADVANCED:
     * By default concurrency is optimized and the cache is not locked more than required during reads or writes,
     * This allows for virtual concurrent reading and writing and should never cause any problems.  If the application
     * uses no form of locking the last unit of work to merge changes will win, with no locking it is possible
     * only under this senerio for two unit of works to merge changes different than the database although highly unlikely
     * and if occured is the entire purpose of locking and locking is the suggested solution if this is a problem.
     * This property allows for the isolation level of changes to the
     * cache to be configured for sever situations and it is not suggest that this be changed.
     * <p>Setting are:<ul>
     * <li>ConcurrentReadWrite - default
     * <li>SynchronizedWrite - only allow a single writer (i.e. unit of work merge) to the cache at once
     * <li>SynchronizedReadOnWrite - do not allow reading or other unit of work merge ehile a unit of work is in merge
     */
    public void setCacheTransactionIsolation(int cacheTransactionIsolation) {
        this.cacheTransactionIsolation = cacheTransactionIsolation;
    }

    /**
     * PUBLIC:
     * Set the connector that will instantiate the connection.
     * As an example, to use a JNDI-supplied <code>DataSource</code>, use code
     * something like the following:
     * <blockquote><code>
     * session.getLogin().setConnector(new JNDIConnector(context, dataSourceName));<br>
     * session.login();
     * </code></blockquote>
     * where the <code>context</code> is an instance of a <code>javax.naming.Context</code> and
     * the <code>dataSourceName</code> refers to the name of the <code>DataSource</code>
     * within the context.
     */
    public void setConnector(Connector connector) {
        this.connector = connector;
    }

    /**
     * PUBLIC:
     * The default value to substitute for database NULLs can be configured
     * on a per-class basis.
     * Example: login.setDefaultNullValue(long.class, new Long(0))
     */
    public void setDefaultNullValue(Class type, Object value) {
        getDatasourcePlatform().getConversionManager().setDefaultNullValue(type, value);
    }

    /**
     * Set the password.
     */
    public void setPassword(String password) {
        if (password != null) {
            // PERF: Do not encrypt empty string.
            if (password == "") {
                // empty char[] for empty String
                setProperty("password", new char[0]);
            } else {
                // first call to get will initialize the securable object
                 // Bug 4117441 - Secure programming practices, store password in char[]
                String encryptedPassword = getSecurableObjectHolder().getSecurableObject().encryptPassword(password); 
                setProperty("password", encryptedPassword.toCharArray());
            }
        } else {
            // is null so remove the property
            removeProperty("password");
        }
    }

    /**
     * Return the password. It will be encrypted.
     */
    public String getPassword() {
        // Bug 4117441 - Secure programming practices, store password in char[]
        char[] passwordObject = (char[]) properties.get("password");
        if (passwordObject != null) {
            return new String(passwordObject);
        } else {
             // respect explicit de-referencing of password
            return null;
        }
    }

    /**
     * Set the encrypted password.
     */
    public void setEncryptedPassword(String password) {
        // remember that we set an encrypted password
        // flag will be needed in prepareProperties.
        isEncryptedPasswordSet = true;

        if (password != null) {
            // Bug 4117441 - Secure programming practices, store password in char[]
            setProperty("password", password.toCharArray());
        } else {// is null so remove the property
            removeProperty("password");
        }
    }

    /**
     * Sets the encryption class name
     */
    public void setEncryptionClassName(String encryptionClassName) {
        getSecurableObjectHolder().setEncryptionClassName(encryptionClassName);
    }

    /**
     * INTERNAL:
     * Set the database platform specific information.
     * This allows TopLink to configure certain advanced features for the database desired.
     */
    public void setPlatform(Platform platform) {
        setDatasourcePlatform(platform);
    }

    /**
     * PUBLIC:
     * Set the database platform specific information.
     * This allows TopLink to configure certain advanced features for the database desired.
     */
    public void setDatasourcePlatform(Platform platform) {
        this.platform = platform;
    }

    /**
     * INTERNAL:
     * Return the name of the database platform class.
     */
    public String getPlatformClassName() {
        return getDatasourcePlatform().getClass().getName();
    }

    /**
     * INTERNAL:
     * Set the name of the Platform to be used.
     * Creates a new instance of the specified Class.
     */
    public void setPlatformClassName(String platformClassName) throws ValidationException {
        Class platformClass = null;
        try {
            //First try loading with the Login's class loader
            platformClass = this.getClass().getClassLoader().loadClass(platformClassName);
            Platform platform = null;
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                try {
                    platform = (Platform)AccessController.doPrivileged(new PrivilegedNewInstanceFromClass(platformClass));
                } catch (PrivilegedActionException exception) {
                    throw exception.getException();
                }
            } else {
                platform = (Platform)PrivilegedAccessHelper.newInstanceFromClass(platformClass);
            }
            usePlatform(platform);
        } catch(Exception cne) {
            //next try using ConversionManager
            try {
                platformClass = ConversionManager.loadClass(platformClassName);           
                Platform platform = null;
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    try {
                        platform = (Platform)AccessController.doPrivileged(new PrivilegedNewInstanceFromClass(platformClass));
                    } catch (PrivilegedActionException exception) {
                        throw ValidationException.platformClassNotFound(exception.getException(), platformClassName);  
                    }
                } else {
                    platform = (Platform)PrivilegedAccessHelper.newInstanceFromClass(platformClass);
                }
                usePlatform(platform);
            } catch(Exception cne2) {
                //if still not found, throw exception
                throw ValidationException.platformClassNotFound(cne2, platformClassName);                
            }
        }
    }

    /**
     * ADVANCED:
     * Set the database platform to be custom platform.
     */
    public void usePlatform(Platform platform) {
        if (getDatasourcePlatform() != null) {
            getDatasourcePlatform().copyInto(platform);
        }
        setPlatform(platform);
    }

    /**
     * PUBLIC:
     * The properties are additional, driver-specific, connection information
     * to be passed to the JDBC driver.
     */
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    /**
     * PUBLIC:
     * Some JDBC drivers require additional, driver-specific, properties.
     * Add the specified property to those to be passed to the JDBC driver.
     */
    public void setProperty(String propertyName, Object propertyValue) {
        properties.put(propertyName, propertyValue);
    }

    /**
     * OBSOLETE:
     * Override the default query for reading the sequence numbers.
     * This query must be a valid query that has one parameter,
     * the sequence name. The query should only return the new value.
     * It will be called after the sequence update query.
     * If TopLink is using native sequencing, then the query must both update and
     * return the new sequence value.
     * Additionally, if TopLink is using native sequencing,
     * the query should not define any parameters.
     * @deprecated use ((QuerySequence)getDefaultSequence()).setSelectQuery(ValueReadQuery) instead
     */
    public void setSelectSequenceNumberQuery(ValueReadQuery selectSequenceNumberQuery) {
        getDatasourcePlatform().setSelectSequenceNumberQuery(selectSequenceNumberQuery);
    }

    /**
     * OBSOLETE:
     * TopLink supports sequence number preallocation.
     * This improves the performance and concurrency of inserts by
     * selecting new object IDs in batches and caching them on the client.
     * The preallocation size can be configured, and the default is 50.
     * By default a sequence table is used. Using a sequence table is recommended
     * as it supports preallocation.
     * This MUST be 1 if native sequencing is used on Sybase, Informix, or SQL Server.
     * This MUST match the SEQUENCE increment if Oracle native sequencing is used.
     * @deprecated use getDefaultSequence().setPreallocationSize(int) instead
     */
    public void setSequencePreallocationSize(int size) {
        getDatasourcePlatform().setSequencePreallocationSize(size);
    }

    /**
     * PUBLIC:
     * Set the default qualifier for all tables.
     * This can be the creator of the table or database name the table exists on.
     * This is required by some databases such as Oracle and DB2.
     */
    public void setTableQualifier(String qualifier) {
        getDatasourcePlatform().setTableQualifier(qualifier);
    }

    /**
     * PUBLIC:
     * Override the default query for returning a timestamp from the server.
     */
    public void setTimestampQuery(ValueReadQuery timestampQuery) {
        getDatasourcePlatform().setTimestampQuery(timestampQuery);
    }

    /**
     * OBSOLETE:
     * Override the default query for updating sequence numbers.
     * This allows another query to be used instead of the default, TopLink-generated, one.
     * This is called before the sequence select query and only
     * if TopLink is using non-native sequencing.
     * @deprecated use ((QuerySequence)getDefaultSequence()).setUpdateQuery(DataModifyQuery) instead
     */
    public void setUpdateSequenceQuery(DataModifyQuery updateSequenceNumberQuery) {
        getDatasourcePlatform().setUpdateSequenceQuery(updateSequenceNumberQuery);
    }

    /**
     * PUBLIC:
     * The user name is the database login name.
     * Some databases do not require a user name or the user is obtained from the OS,
     * in this case this should not be specified.
     */
    public void setUserName(String name) {
        if (name != null) {
            setProperty("user", name);
        }
    }

    /**
     * PUBLIC:
     * Return whether TopLink uses some external connection pooling service such as a JDBC 2.0 driver.
     */
    public void setUsesExternalConnectionPooling(boolean usesExternalConnectionPooling) {
        this.usesExternalConnectionPooling = usesExternalConnectionPooling;
    }

    /**
     * PUBLIC:
     * Return whether TopLink uses some external transaction service such as JTS.
     */
    public void setUsesExternalTransactionController(boolean usesExternalTransactionController) {
        this.usesExternalTransactionController = usesExternalTransactionController;
    }

    /**
     * PUBLIC:
     * Do not use external connection pooling. This is appropriate if using regular
     * TopLink connection pooling and regular JDBC drivers.
     *
     * @see #useExternalConnectionPooling()
     */
    public void dontUseExternalConnectionPooling() {
        setUsesExternalConnectionPooling(false);
    }

    /**
     * PUBLIC:
     * Let TopLink control transactions instead of some external transaction
     * service such as JTS.
     *
     * @see #useExternalTransactionController()
     */
    public void dontUseExternalTransactionController() {
        setUsesExternalTransactionController(false);
    }

    /**
     * INTERNAL:
     * Used for cache isolation.
     */
    public boolean shouldAllowConcurrentReadWrite() {
        return getCacheTransactionIsolation() == CONCURRENT_READ_WRITE;
    }

    /**
     * INTERNAL:
     * Used for cache isolation.
     */
    public boolean shouldSynchronizedReadOnWrite() {
        return getCacheTransactionIsolation() == SYNCHRONIZED_READ_ON_WRITE;
    }

    /**
     * INTERNAL:
     * Used for Cache Isolation.  Causes TopLink to lock at the class level on
     * cache updates.
     */
    public boolean shouldSynchronizeWrites() {
        return getCacheTransactionIsolation() == SYNCHRONIZED_WRITE;
    }
    
    /**
     * INTERNAL:
     * Used for Cache Isolation.  Causes TopLink to lock at the object level on
     * cache updates and cache access.
     */
    public boolean shouldSynchronizeObjectLevelReadWrite(){
        return getCacheTransactionIsolation() == SYNCRONIZED_OBJECT_LEVEL_READ_WRITE;
    }
    
    /**
     * INTERNAL:
     * Used for Cache Isolation.  Causes TopLink to lock at the object level on
     * cache updates and cache access, based on database transaction.
     */
    public boolean shouldSynchronizeObjectLevelReadWriteDatabase(){
        return getCacheTransactionIsolation() == SYNCRONIZED_OBJECT_LEVEL_READ_WRITE_DATABASE;
    }
    
    /**
     * PUBLIC:
     * Return whether TopLink uses some external connection pooling
     * (e.g. WebLogic's JTS driver).
     *
     * @see #useExternalConnectionPooling()
     * @see #dontUseExternalConnectionPooling()
     */
    public boolean shouldUseExternalConnectionPooling() {
        return usesExternalConnectionPooling;
    }

    /**
     * PUBLIC:
     * Return whether TopLink uses some external transaction service such as JTS.
     *
     * @see #useExternalTransactionController()
     * @see #dontUseExternalTransactionController()
     */
    public boolean shouldUseExternalTransactionController() {
        return usesExternalTransactionController;
    }

    /**
     * PUBLIC:
     * Use external connection pooling.
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
     * Print all of the connection information.
     */
    public String toString() {
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        writer.write(Helper.getShortClassName(getClass()));
        writer.println("(");
        writer.println("\t" + ToStringLocalization.buildMessage("platform", (Object[])null) + "=> " + getDatasourcePlatform());
        if (!shouldUseExternalConnectionPooling()) {
            writer.println("\t" + ToStringLocalization.buildMessage("user_name", (Object[])null) + "=> \"" + getUserName() + "\"");
        }
        writer.print("\t");
        getConnector().toString(writer);
        writer.write(")");
        return stringWriter.toString();
    }

    /**
     * Get default sequence
     */
    public Sequence getDefaultSequence() {
        return getDatasourcePlatform().getDefaultSequence();
    }

    /**
     * Set default sequence
     */
    public void setDefaultSequence(Sequence sequence) {
        getDatasourcePlatform().setDefaultSequence(sequence);
    }

    /**
     * Add sequence corresponding to the name
     */
    public void addSequence(Sequence sequence) {
        getDatasourcePlatform().addSequence(sequence);
    }

    /**
     * Get sequence corresponding to the name
     */
    public Sequence getSequence(String seqName) {
        return getDatasourcePlatform().getSequence(seqName);
    }
		
    /**
     * Returns a map of sequence names to Sequences (may be null).
     */
    public Map getSequences() {
        return getDatasourcePlatform().getSequences();
    }

    /**
     * Remove sequence corresponding to name.
     * Doesn't remove default sequence.
     */
    public Sequence removeSequence(String seqName) {
        return getDatasourcePlatform().removeSequence(seqName);
    }

    /**
     * Remove all sequences but the default one.
     */
    public void removeAllSequences() {
        getDatasourcePlatform().removeAllSequences();
    }

    /**
     * INTERNAL:
     * Used only for writing the login into XML or Java.
     */
    public Sequence getDefaultSequenceToWrite() {
        return getDatasourcePlatform().getDefaultSequenceToWrite();
    }

    /**
     * INTERNAL:
     * Used only for writing the login into XML or Java.
     */
    public Map getSequencesToWrite() {
        return getDatasourcePlatform().getSequencesToWrite();
    }

    /**
     * INTERNAL:
     * Used only for reading the login from XML.
     */
    public void setSequences(Map sequences) {
        getDatasourcePlatform().setSequences(sequences);
    }
}
