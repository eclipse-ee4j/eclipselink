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
package org.eclipse.persistence.eis;

import java.io.*;
import java.util.*;
import javax.naming.*;
import javax.resource.*;
import javax.resource.cci.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.helper.*;

/**
 * <p>An <code>EISConnectionSpec</code> specifies how the 
 * <code>javax.resource.cci.Connection</code> is accessed.  There are three ways 
 * to connect to an EIS datasource through JCA:
 * <ul>
 * <li>Provide a JNDI name to the ConnectionFactory and use the default 
 * getConnection
 * <li>Provide a JNDI name to the ConnectionFactory, and a driver specific 
 * ConnectionSpec to pass to the getConnection
 * <li>Connect in a non-managed way directly to the driver specific 
 * ConnectionFactory
 *
 * @see EISLogin
 *
 * @author James
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public class EISConnectionSpec implements Connector {
    public static final String USER = "user";
    public static final String PASSWORD = "password";
    protected ConnectionSpec connectionSpec;
    protected ConnectionFactory connectionFactory;
    protected Context context;
    protected Name name;
    protected Writer log;

    /**
     * PUBLIC:
     * Construct a EISConnectionSpec with no settings.
     * The ConnectionFactory name will still need to be set.
     */
    public EISConnectionSpec() {
        super();
    }

    /**
     * PUBLIC:
     * Construct a EISConnectionSpec with the specified settings.
     */
    public EISConnectionSpec(Context context, String name) throws ValidationException {
        this.context = context;
        setName(name);
    }

    /**
     * PUBLIC:
     * Construct a EISConnectionSpec with the specified settings.
     */
    public EISConnectionSpec(String name) throws ValidationException {
        this();
        setName(name);
    }

    /**
     * PUBLIC:
     * Construct a EISConnectionSpec with the specified settings.
     */
    public EISConnectionSpec(Context context, Name name) {
        this.context = context;
        this.name = name;
    }

    /**
     * PUBLIC:
     * Construct a EISConnectionSpec with the specified settings.
     */
    public EISConnectionSpec(Name name) {
        this();
        this.name = name;
    }

    /**
     * PUBLIC:
     * Returns the attunity adapter message log.
     */
    public Writer getLog() {
        return log;
    }

    /**
     * PUBLIC:
     * Sets the attunity adapter message log.
     */
    public void setLog(Writer log) {
        this.log = log;
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
     * Required for interface, but never used, for JDBC.
     */
    public java.sql.Connection connect(Properties properties, Session session) {
        throw EISException.incorrectLoginInstanceProvided(EISLogin.class);
    }

    /**
     * Connect with the specified properties and return the Connection.
     */
    public Connection connectToDataSource(EISAccessor accessor, Properties properties) throws DatabaseException, ValidationException {
        ConnectionFactory connectionFactory = getConnectionFactory();
        if (connectionFactory == null) {
            try {
                connectionFactory = (ConnectionFactory)getContext().lookup(getName());
                setConnectionFactory(connectionFactory);
            } catch (Exception exception) {
                throw ValidationException.cannotAcquireDataSource(getName(), exception);
            }
        }

        try {
            accessor.setRecordFactory(connectionFactory.getRecordFactory());
            if (getConnectionSpec() == null) {
                return connectionFactory.getConnection();
            } else {
                return connectionFactory.getConnection(getConnectionSpec());
            }
        } catch (ResourceException exception) {
            throw EISException.resourceException(exception, accessor, null);
        }
    }

    /**
     * PUBLIC:
     * Return the JNDI Context that can supplied the named ConnectionFactory.
     */
    public Context getContext() {
        // Lazy initialize the context to avoid context error when a factory is not used.
        if (context == null) {
            try {
                context = new InitialContext();
            } catch (Exception exception) {
                // Ignore/leave blank for specification}
            }
        }
        return context;
    }

    /**
     * INTERNAL:
     * Retrieve the password property from the supplied Properties object
     */
    public String getPasswordFromProperties(Properties properties) {
        Object passwordObject = properties.get(PASSWORD);
        // Bug 4117441 - Secure programming practices, store password in char[]
        String password = null;
        if (passwordObject instanceof char[]) {
            password = new String((char[]) passwordObject);
        } else if (passwordObject instanceof String) {
            // password could potentially be a String if Properties object has been modified outside of TopLink
            password = (String) passwordObject;
        }
        return password;
    }

    /**
     * PUBLIC:
     * Return the javax.resource.cci.ConnectionFactory.
     */
    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    /**
     * PUBLIC:
     * Return the javax.resource.cci.ConnectionSpec.
     */
    public ConnectionSpec getConnectionSpec() {
        return connectionSpec;
    }

    /**
     * PUBLIC:
     * Return the name of the ConnectionFactory within the
     * JNDI Context.
     */
    public Name getName() {
        return name;
    }

    /**
     * PUBLIC:
     * Provide the details of my connection information. This is primarily for JMX runtime services.
     * @return java.lang.String
     */
    public String getConnectionDetails() {
        return this.getName().toString();
    }

    /**
     * PUBLIC:
     * Set the JNDI Context that can supply the named ConnectionFactory.
     */
    public void setContext(Context context) {
        this.context = context;
    }

    /**
     * PUBLIC:
     * Set the javax.resource.cci.ConnectionFactory.
     */
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    /**
     * PUBLIC:
     * Set the javax.resource.cci.ConnectionFactory.
     */
    public void setConnectionFactoryObject(Object connectionFactory) {
        setConnectionFactory((ConnectionFactory)connectionFactory);
    }

    /**
     * PUBLIC:
     * Set the javax.resource.cci.ConnectionSpec.
     * This is only required if the default getConnection() on the connection factory is not used.
     * This must be set to the EIS adapter specific connection spec.
     */
    public void setConnectionSpec(ConnectionSpec connectionSpec) {
        this.connectionSpec = connectionSpec;
    }

    /**
     * PUBLIC:
     * Set the javax.resource.cci.ConnectionSpec.
     * This is only required if the default getConnection() on the connection factory is not used.
     * This must be set to the EIS adapter specific connection spec.
     */
    public void setConnectionSpecObject(Object connectionFactory) {
        setConnectionSpec((ConnectionSpec)connectionFactory);
    }

    /**
     * PUBLIC:
     * Set the name of the ConnectionFactory within the
     * JNDI Context.
     */
    public void setName(String name) throws ValidationException {
        try {
            this.name = new CompositeName(name);
        } catch (InvalidNameException e) {
            throw ValidationException.invalidDataSourceName(name, e);
        }
    }

    /**
     * PUBLIC:
     * Set the name of the ConnectionFactory within the
     * JNDI Context.
     */
    public void setName(Name name) {
        this.name = name;
    }

    /**
     * PUBLIC:
     * Print data source info.
     */
    public String toString() {
        if (getName() != null) {
            return Helper.getShortClassName(getClass()) + "(connection manager url => " + getName() + ")";
        } else {
            return Helper.getShortClassName(getClass()) + "()";
        }
    }

    /**
     * INTERNAL:
     * Print something useful on the log.
     */
    public void toString(java.io.PrintWriter writer) {
        writer.println(toString());
    }
}
