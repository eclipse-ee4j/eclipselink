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
    private static final String versionStringTemplate = "{0} - {1}";
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
     * only under this scenario for two unit of works to merge changes different than the database although highly unlikely
     * and if occurred is the entire purpose of locking.  This property allows for the isolation level of changes to the
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

        this.properties = new NonSynchronizedProperties(4);
        this.properties.put("user", "");
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
    public DatasourceLogin clone() {
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
     * only under this scenario for two unit of works to merge changes different than the database although highly unlikely
     * and if occurred is the entire purpose of locking and locking is the suggested solution if this is a problem.
     * This property allows for the isolation level of changes to the
     * cache to be configured for sever situations and it is not suggest that this be changed.
     * <p>Setting are:<ul>
     * <li>ConcurrentReadWrite - default
     * <li>SynchronizedWrite - only allow a single writer (i.e. unit of work merge) to the cache at once
     * <li>SynchronizedReadOnWrite - do not allow reading or other unit of work merge while a unit of work is in merge
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
     * This allows EclipseLink to configure certain advanced features for the database desired.
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
     * This allows EclipseLink to configure certain advanced features for the datasource desired.
     */
    public Platform getDatasourcePlatform() {
        if (this.platform == null) {
            this.platform = new DatasourcePlatform();
        }
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
     * PUBLIC:
     * Return the qualifier for the all of the tables referenced by EclipseLink.
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
     * for other datasource type.  On an SQL Exception EclipseLink will ping the database to determine
     * if the connection used can continue to be used for queries.  This should have no impact on applications
     * unless the user is using pessimistic locking queries with 'no wait' or are using a query timeout feature.
     * If that is the case and the application is experiencing a performance impact from the health check then
     * this feature can be turned off. Turning this feature off will prevent EclipseLink from being able to
     * retry queries in the case of database failure. 
     */
    public boolean isConnectionHealthValidatedOnError(){
        return false;
    }
    
    /**
     * PUBLIC:
     * Return the EclipseLink version.
     * @return version of EclipseLink
     */
    public static String getVersion() {
        if (versionString == null) {
            Object[] args = { Version.getProduct(), Version.getVersionString() };
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
        Properties result = properties;
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
                result = (Properties)properties.clone();
                // Bug 4117441 - Secure programming practices, store password in char[]
                // If isEncryptedPasswordSet is true, or we have a SecurableObject then we stored 
                // the password as a char[], and we need to convert it into a String for the 
                // prepared Properties object.
                if (passwordObject instanceof char[]) {
                    result.put("password", getSecurableObjectHolder().getSecurableObject().decryptPassword(new String((char[])passwordObject)));
                } else if (passwordObject instanceof String) {
                    result.put("password", getSecurableObjectHolder().getSecurableObject().decryptPassword((String)passwordObject));
                } else {
                    result.put("password", null);
                }
            } else if ((passwordObject instanceof char[]) && (((char[])passwordObject).length == 0)) {
                // Bug 236726 - deal with empty string for passwords
                result.put("password", "");
            }
        }

        return result;
    }

    /**
     * PUBLIC:
     * Some drivers don't like the "user" and "password" properties.
     * They can be removed with this method, or by using setUserName(null) / setPassword(null)
     */
    public void removeProperty(String propertyName) {
        properties.remove(propertyName);
    }

    /**
     * ADVANCED:
     * By default concurrency is optimized and the cache is not locked more than required during reads or writes,
     * This allows for virtual concurrent reading and writing and should never cause any problems.  If the application
     * uses no form of locking the last unit of work to merge changes will win, with no locking it is possible
     * only under this scenario for two unit of works to merge changes different than the database although highly unlikely
     * and if occurred is the entire purpose of locking and locking is the suggested solution if this is a problem.
     * This property allows for the isolation level of changes to the
     * cache to be configured for sever situations and it is not suggest that this be changed.
     * <p>Setting are:<ul>
     * <li>ConcurrentReadWrite - default
     * <li>SynchronizedWrite - only allow a single writer (i.e. unit of work merge) to the cache at once
     * <li>SynchronizedReadOnWrite - do not allow reading or other unit of work merge while a unit of work is in merge
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
                setProperty("password", "");
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
        Object password = properties.get("password");
        if (password instanceof String) {
            return (String)password;
        }
        char[] passwordObject = (char[]) password;
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
     * This allows EclipseLink to configure certain advanced features for the database desired.
     */
    public void setPlatform(Platform platform) {
        setDatasourcePlatform(platform);
    }

    /**
     * PUBLIC:
     * Set the database platform specific information.
     * This allows EclipseLink to configure certain advanced features for the database desired.
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
        // Handle old Oracle platform conversion.
        if (platformClassName.equals("org.eclipse.persistence.platform.database.oracle.OraclePlatform")) {
            platformClassName = "org.eclipse.persistence.platform.database.OraclePlatform";
        }
        Class platformClass = null;
        try {
            //First try loading with the Login's class loader
            platformClass = this.getClass().getClassLoader().loadClass(platformClassName);
            DatasourcePlatform platform = null;
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                try {
                    platform = (DatasourcePlatform)AccessController.doPrivileged(new PrivilegedNewInstanceFromClass(platformClass));
                } catch (PrivilegedActionException exception) {
                    throw exception.getException();
                }
            } else {
                platform = (DatasourcePlatform)PrivilegedAccessHelper.newInstanceFromClass(platformClass);
            }
            usePlatform(platform);
        } catch(Throwable cne) {
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
            } catch(Throwable cne2) {
                //if still not found, throw exception
                throw ValidationException.platformClassNotFound(cne2, platformClassName);                
            }
        }
    }
    /**
     * INTERNAL:
     * Set the name of the Platform to be created using the 
     * passed in class loader.  If no class loader is passed
     * in, of if an exception is thrown, call the 
     * setPlatformClassName method with no classloader.
     * @see setPlatformClassName(String platformClassName)
     */
    public void setPlatformClassName(String platformClassName, ClassLoader loader) throws ValidationException {
        boolean exceptionCaught = false;
        Class platformClass = null;
        try {
            Platform platform = null;
            if (loader != null) {
                platformClass = loader.loadClass(platformClassName);
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    try {
                       platform = (Platform)AccessController.doPrivileged(new PrivilegedNewInstanceFromClass(platformClass));
                  } catch (PrivilegedActionException exception) {
                      throw ValidationException.platformClassNotFound(exception.getException(), platformClassName);  
                  }
                } else {
                    platform = (Platform)PrivilegedAccessHelper.newInstanceFromClass(platformClass);
                }
            }
            usePlatform(platform);
        } catch(Exception cne) {
            exceptionCaught = true;
        }
        if (exceptionCaught || (loader == null))
        {
            //attempt to load with default classloader
            this.setPlatformClassName(platformClassName);
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
     * PUBLIC:
     * The user name is the database login name.
     * Some databases do not require a user name or the user is obtained from the OS,
     * in this case this should not be specified.
     */
    public void setUserName(String name) {
        if (name != null) {
            setProperty("user", name);
        } else {
            removeProperty("user");
        }
    }

    /**
     * PUBLIC:
     * Return whether EclipseLink uses some external connection pooling service such as a JDBC 2.0 driver.
     */
    public void setUsesExternalConnectionPooling(boolean usesExternalConnectionPooling) {
        this.usesExternalConnectionPooling = usesExternalConnectionPooling;
    }

    /**
     * PUBLIC:
     * Return whether EclipseLink uses some external transaction service such as JTS.
     */
    public void setUsesExternalTransactionController(boolean usesExternalTransactionController) {
        this.usesExternalTransactionController = usesExternalTransactionController;
    }

    /**
     * PUBLIC:
     * Do not use external connection pooling. This is appropriate if using regular
     * EclipseLink connection pooling and regular JDBC drivers.
     *
     * @see #useExternalConnectionPooling()
     */
    public void dontUseExternalConnectionPooling() {
        setUsesExternalConnectionPooling(false);
    }

    /**
     * PUBLIC:
     * Let EclipseLink control transactions instead of some external transaction
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
        return this.cacheTransactionIsolation == CONCURRENT_READ_WRITE;
    }

    /**
     * INTERNAL:
     * Used for cache isolation.
     */
    public boolean shouldSynchronizedReadOnWrite() {
        return this.cacheTransactionIsolation == SYNCHRONIZED_READ_ON_WRITE;
    }

    /**
     * INTERNAL:
     * Used for Cache Isolation.  Causes EclipseLink to lock at the class level on
     * cache updates.
     */
    public boolean shouldSynchronizeWrites() {
        return this.cacheTransactionIsolation == SYNCHRONIZED_WRITE;
    }
    
    /**
     * INTERNAL:
     * Used for Cache Isolation.  Causes EclipseLink to lock at the object level on
     * cache updates and cache access.
     */
    public boolean shouldSynchronizeObjectLevelReadWrite(){
        return this.cacheTransactionIsolation == SYNCRONIZED_OBJECT_LEVEL_READ_WRITE;
    }
    
    /**
     * INTERNAL:
     * Used for Cache Isolation.  Causes EclipseLink to lock at the object level on
     * cache updates and cache access, based on database transaction.
     */
    public boolean shouldSynchronizeObjectLevelReadWriteDatabase(){
        return this.cacheTransactionIsolation == SYNCRONIZED_OBJECT_LEVEL_READ_WRITE_DATABASE;
    }
    
    /**
     * PUBLIC:
     * Return whether EclipseLink uses some external connection pooling
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
     * Return whether EclipseLink uses some external transaction service such as JTS.
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
     * Don't use if the session is connected.
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
     * Don't use if the session is connected.
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
