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
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.sql.*;
import java.io.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.localization.*;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.internal.security.PrivilegedNewInstanceFromClass;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.logging.SessionLog;

/**
 * <p>
 * <b>Purpose</b>:Use this Connector to build a java.sql.Connection in the
 * "standard" fashion, via the DriverManager.
 *
 * @author Big Country
 * @since TOPLink/Java 2.1
 */
public class DefaultConnector implements Connector {
    protected String driverClassName;
    protected String driverURLHeader;
    protected String databaseURL;
    /** cache up the driver class to speed up reconnects */
    protected Class driverClass;
    /** cache up the instantiated Driver to speed up reconnects */
    protected Driver driver;

    /** Save whether we have switched to direct connect (sql.Driver.connect()) mode */
    private boolean connectDirectly = false;
    
    /**
     * PUBLIC:
     * Construct a Connector with default settings
     * The database URL will still need to be set.
     */
    public DefaultConnector() {
    }

    /**
     * PUBLIC:
     * Construct a Connector with the specified settings.
     */
    public DefaultConnector(String driverClassName, String driverURLHeader, String databaseURL) {
        this.initialize(driverClassName, driverURLHeader, databaseURL);
    }

    /**
     * INTERNAL:
     * Clone the connector.
     */
    public Object clone() {
        try {
            return super.clone();
        } catch (Exception exception) {
            throw new InternalError("Clone failed");
        }
    }

    /**
     * INTERNAL:
     * Connect with the specified properties and session. Return the Connection.
     * @return java.sql.Connection
     */
    public Connection connect(Properties properties, Session session) throws DatabaseException {
        // if driver class name is given, ensure the driver has been loaded and registered
        if (this.driverClassName != null && this.driverClass == null) {
             this.loadDriverClass(session);
        }
         
        String connectionString = this.getConnectionString();
        // verify if connection URL is not null and empty
        if(connectionString == null || connectionString.trim().equals("")) {
            throw DatabaseException.unableToAcquireConnectionFromDriverException(
                    this.driverClassName, null, connectionString);
        }
        
        SQLException driverManagerException = null;
        if (this.shouldUseDriverManager(properties, session)) {
            try {
                return DriverManager.getConnection(this.getConnectionString(), properties);
            } catch (SQLException sqlException) {
                driverManagerException = sqlException;
                if (session != null) {
                    ((AbstractSession)session).log(SessionLog.FINER, SessionLog.CONNECTION, "connect_drivermanager_fail");
                    ((AbstractSession)session).logThrowable(SessionLog.FINER, SessionLog.CONNECTION, sqlException);
                }
            }
        }
        
        if (this.driverClass == null) {
            throw DatabaseException.sqlException(driverManagerException, (org.eclipse.persistence.internal.sessions.AbstractSession) session, true);
        }
        
        // try driver class directly 
        // Save secondary exception state and don't clear original exception
		boolean wrongDriverExceptionOccurred = false;
		try {
			// A return of null indicates wrong type of driver, an SQLException means connection problems
			Connection directConnection = this.directConnect(properties);
			if (null == directConnection) {
			    // This connection failed specifically because of a wrong or unknown driver
			    if (null != driverManagerException) {
					wrongDriverExceptionOccurred = true;
					// Nest root exception
					throw DatabaseException.unableToAcquireConnectionFromDriverException(driverManagerException,
							getDriverClassName(), properties.getProperty("user"), getDatabaseURL());
				} else {
					throw DatabaseException.unableToAcquireConnectionFromDriverException(
							getDriverClassName(), driverClass.getCanonicalName(), getDatabaseURL());
				}
			} else {
				if (null != driverManagerException) {
					// If this connection succeeded where the previous DriverManager connection failed - save state and continue
					connectDirectly = true;
				}
			}
			return directConnection;
		} catch (DatabaseException directConnectException) {
			if (driverManagerException != null && !wrongDriverExceptionOccurred) {
                throw DatabaseException.sqlException(driverManagerException, (AbstractSession) session, true);
            } else {
                throw directConnectException;
            }
        }
    }
        

    /**
     * INTERNAL: 
     * Indicates whether DriverManager should be used.
     * @return boolean 
     */
     public boolean shouldUseDriverManager(Properties properties, Session session) {
         return  !connectDirectly && ((session == null) || session.getServerPlatform().shouldUseDriverManager());
     }
     
    /**
     * INTERNAL:
     * Connect directly - without using DriverManager. Return the Connection.
     * @return java.sql.Connection
     */
    protected Connection directConnect(Properties properties) throws DatabaseException {
        if(this.driver == null) {
            this.instantiateDriver();
        }
        try {
            return this.driver.connect(this.getConnectionString(), properties);
        } catch (SQLException exception) {
            this.clearDriverClassAndDriver();
            throw DatabaseException.sqlException(exception, true);
        }
    }

    /**
     * PUBLIC:
     * Return the JDBC connection string.
     * This is a combination of the driver-specific URL header and the database URL.
     */
     public String getConnectionString() {
        if(this.getDriverURLHeader() == null) {
            return this.getDatabaseURL();
        } else if(this.getDatabaseURL() == null) {
            return this.getDriverURLHeader();
        }
         return this.getDriverURLHeader() + this.getDatabaseURL();
     }

    /**
     * PUBLIC:
     * Provide the details of my connection information. This is primarily for JMX runtime services.
     * @return java.lang.String
     */
    public String getConnectionDetails() {
        return this.getConnectionString();
    }

    /**
     * PUBLIC:
     * The database URL is the JDBC URL for the database server.
     * The driver header is <i>not</i> be included in this URL
     * (e.g. "dbase files"; not "jdbc:odbc:dbase files").
     */
    public String getDatabaseURL() {
        return databaseURL;
    }

    /**
     * PUBLIC:
     * The driver class is the name of the Java class for the JDBC driver being used
     * (e.g. "sun.jdbc.odbc.JdbcOdbcDriver").
     */
    public String getDriverClassName() {
        return driverClassName;
    }

    /**
     * PUBLIC:
     * The driver URL header is the string predetermined by the JDBC driver to be
     * part of the URL connection string, (e.g. "jdbc:odbc:").
     * This is required to connect to the database.
     */
    public String getDriverURLHeader() {
        return driverURLHeader;
    }

    /**
     * INTERNAL:
     * Initialize the connector with the specified settings.
     */
    protected void initialize(String driverClassName, String driverURLHeader, String databaseURL) {
        this.setDriverClassName(driverClassName);
        this.setDriverURLHeader(driverURLHeader);
        this.setDatabaseURL(databaseURL);
    }

    /**
     * INTERNAL:
     * Ensure that the driver has been loaded and registered with the
     * DriverManager. Just loading the class should cause the static
     * initialization code to do the necessary registration.
     * Return the loaded driver Class.
     */
    protected void loadDriverClass(Session session) throws DatabaseException {
        // CR#... The correct class loader must be used to load the class,
        // not that Class.forName must be used to initialize the class a simple loadClass may not.
        try {
            if(session != null) {
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    try {
                        driverClass = (Class)AccessController.doPrivileged(new PrivilegedClassForName(this.getDriverClassName(), true, session.getPlatform().getConversionManager().getLoader()));
                    } catch (PrivilegedActionException exception) {
                        throw DatabaseException.configurationErrorClassNotFound(this.getDriverClassName());
                    }
                } else {
                    driverClass = org.eclipse.persistence.internal.security.PrivilegedAccessHelper.getClassForName(this.getDriverClassName(), true, session.getPlatform().getConversionManager().getLoader());
                }
            } else {
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    try {
                        driverClass = (Class)AccessController.doPrivileged(new PrivilegedClassForName(this.getDriverClassName(), true, ConversionManager.getDefaultManager().getLoader()));
                    } catch (PrivilegedActionException exception) {
                        throw DatabaseException.configurationErrorClassNotFound(this.getDriverClassName());                }
                } else {
                    driverClass = org.eclipse.persistence.internal.security.PrivilegedAccessHelper.getClassForName(this.getDriverClassName(), true, ConversionManager.getDefaultManager().getLoader());
                }
            }
        } catch (ClassNotFoundException exception) {
            clearDriverClassAndDriver();
            throw DatabaseException.configurationErrorClassNotFound(this.getDriverClassName());
        }
    }

    /**
     * PUBLIC:
     * The database URL is the JDBC URL for the database server.
     * The driver header is <i>not</i> be included in this URL
     * (e.g. "dbase files"; not "jdbc:odbc:dbase files").
     */
    public void setDatabaseURL(String databaseURL) {
        this.databaseURL = databaseURL;
    }

    /**
     * PUBLIC:
     * The driver class is the name of the Java class for the JDBC driver being used
     * (e.g. "sun.jdbc.odbc.JdbcOdbcDriver").
     */
    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
        // if driver class name changed then discard the cached driver class and driver.
        clearDriverClassAndDriver();
    }

    /**
     * PUBLIC:
     * The driver URL header is the string predetermined by the JDBC driver to be
     * part of the URL connection string, (e.g. "jdbc:odbc:").
     * This is required to connect to the database.
     */
    public void setDriverURLHeader(String driverURLHeader) {
        this.driverURLHeader = driverURLHeader;
    }

    /**
     * PUBLIC:
     * Print connection string.
     */
    public String toString() {
        return Helper.getShortClassName(getClass()) + "(" + getConnectionString() + ")";
    }

    /**
     * INTERNAL:
     * Print something useful on the log.
     */
    public void toString(PrintWriter writer) {
        writer.println(ToStringLocalization.buildMessage("datasource_URL", (Object[])null) + "=> \"" + this.getConnectionString() + "\"");
    }

    /**
     * INTERNAL:
     * Instantiate the Driver.
     * @return void
     */
    protected void instantiateDriver() throws DatabaseException {
        try {
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                try{
                    this.driver = (Driver)AccessController.doPrivileged(new PrivilegedNewInstanceFromClass(this.driverClass));
                }catch (PrivilegedActionException ex){
                    if (ex.getCause() instanceof IllegalAccessException){
                        throw (IllegalAccessException)ex.getCause();
                    }else if (ex.getCause() instanceof InstantiationException){
                        throw (InstantiationException)ex.getCause();
                    }
                    throw (RuntimeException)ex.getCause();
                }
            }else{
                this.driver = (Driver)PrivilegedAccessHelper.newInstanceFromClass(this.driverClass);
            }
       } catch (InstantiationException ie) {
            this.clearDriverClassAndDriver();
            throw DatabaseException.configurationErrorNewInstanceInstantiationException(ie, driverClass);
        } catch (IllegalAccessException iae) {
            this.clearDriverClassAndDriver();
            throw DatabaseException.configurationErrorNewInstanceIllegalAccessException(iae, driverClass);
        }
    }

    /**
     * INTERNAL:
     * Discard the cached driver class and driver.
    * @return void
     */
    public void clearDriverClassAndDriver() {
        this.driverClass = null;
        this.driver = null;
    }
}
