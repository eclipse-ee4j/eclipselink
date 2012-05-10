/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.eis.adapters.jms;

import javax.resource.cci.*;

/**
 * INTERNAL:
 * Provides the behavior of instantiating a JMS ConnectionSpec.
 *
 * @author Dave McCann
 * @since OracleAS TopLink 10<i>g</i> (10.1.3)
 */
public class CciJMSConnectionSpec implements ConnectionSpec {
    protected String username;// username for queue connection
    protected char[] password;// password for queue connection
    protected String connectionFactoryURL;// JNDI name of the connection factory
    protected javax.jms.ConnectionFactory connectionFactory;// if no JNDI, the provider class

    /**
     * The default constructor.
     */
    public CciJMSConnectionSpec() {
        username = "";
        password = new char[0];
        connectionFactoryURL = "";
        connectionFactory = null;
    }

    /**
     * This constructor sets the connection factory url.
     *
     * @param url - the JNDI lookup name of the connection factory
     */
    public CciJMSConnectionSpec(String url) {
        username = "";
        password = new char[0];
        connectionFactoryURL = url;
        connectionFactory = null;
    }

    /**
     * This constructor sets the connection factory.
     *
     * @param factory - the connection factory class
     */
    public CciJMSConnectionSpec(javax.jms.ConnectionFactory factory) {
        username = "";
        password = new char[0];
        connectionFactoryURL = "";
        connectionFactory = factory;
    }

    /**
     * This constructor sets the connection factory url, username and password.
     *
     * @param url - the JNDI lookup name of the connection factory
     * @param name - the username to be used when obtaining the queue connection
     * @param pass - the password to be used when obtaining the queue connection
     */
    public CciJMSConnectionSpec(String url, String name, String pass) {
        username = name;
        setPassword(pass);
        connectionFactoryURL = url;
        connectionFactory = null;
    }

    /**
     * This constructor sets the connection factory, username and password/
     *
     * @param factory - the connection factory class
     * @param name - the username to be used when obtaining the queue connection
     * @param pass - the password to be used when obtaining the queue connection
     */
    public CciJMSConnectionSpec(javax.jms.ConnectionFactory factory, String name, String pass) {
        username = name;
        setPassword(pass);
        connectionFactoryURL = "";
        connectionFactory = factory;
    }

    /**
     * Return the username used when getting a queue connection.
     *
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Set the username to be used when getting a queue connection.
     *
     * @param name
     */
    public void setUsername(String name) {
        username = name;
    }

    /**
     * Return the password used when getting a queue connection.
     *
     * @return password
     */
    public String getPassword() {
        // Bug 4117441 - Secure programming practices, store password in char[]
        if (password != null) {
            return new String(password);
        } else {
            return null;
        }
    }

    /**
     * Set the password to be used when getting a queue connection.
     *
     * @param pass
     */
    public void setPassword(String pass) {
        // Bug 4117441 - Secure programming practices, store password in char[]
        if (pass != null) {
            password = pass.toCharArray();
        } else {
            // respect explicit de-referencing of the password
            password = null;
        }
    }

    /**
    * Indicates if the username property has been set.
    *
    * @return true if the username property is non-null and has a length greater than 0
    */
    public boolean hasUsername() {
        return (username != null) && (username.length() > 0);
    }

    /**
     * Indicates if JNDI is to be used to get the connection factory.
     *
     * @return true if a JNDI lookup name has been set, false otherwise.
     */
    public boolean hasConnectionFactoryURL() {
        return (connectionFactoryURL != null) && (connectionFactoryURL.length() > 0);
    }

    /**
     * Returns the JNDI lookup name of the connection factory.
     *
     * @return connection factory lookup name in JNDI
     */
    public String getConnectionFactoryURL() {
        return connectionFactoryURL;
    }

    /**
     * Set the JNDI lookup name of the connection factory.
     *
     * @param jndiName
     */
    public void setConnectionFactoryURL(String jndiName) {
        connectionFactoryURL = jndiName;
    }

    /**
     * Set the connection factory class to be used.
     *
     * @param connFactory - the factory class to be used
     */
    public void setConnectionFactory(javax.jms.ConnectionFactory connFactory) {
        connectionFactory = connFactory;
    }

    /**
     * Return the connection factory class.
     *
     * @return connection factory class
     */
    public javax.jms.ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }
}
