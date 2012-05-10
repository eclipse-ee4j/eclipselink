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
package org.eclipse.persistence.internal.eis.adapters.aq;

import javax.resource.cci.*;

/**
 * Connection spec for AQ JCA adapter.
 *
 * @author James
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public class AQConnectionSpec implements ConnectionSpec {

    /** AQ database user name. */
    protected String user;

    /** AQ database user password. */
    protected char[] password;

    /** Database JDBC URL. */
    protected String url;

    /** DataSource JNDI name. */
    protected String datasource;

    /**
     * PUBLIC:
     * Default constructor.
     */
    public AQConnectionSpec() {
        this.user = "";
        this.password = new char[0];
        this.url = "jdbc:oracle:thin@localhost:1521:orcl";
        this.datasource = "";
    }

    /**
     * PUBLIC:
     * Construct the spec with the default directory.
     */
    public AQConnectionSpec(String user, String password, String url) {
        this.user = user;
        setPassword(password);
        this.url = url;
    }

    /**
     * PUBLIC:
     * Return the JDBC url.
     */
    public String getURL() {
        return url;
    }

    /**
     * PUBLIC:
     * Set the JDBC url.
     */
    public void setURL(String url) {
        this.url = url;
    }

    /**
     * PUBLIC:
     * Return the DataSource JNDI name.
     */
    public String getDatasource() {
        return datasource;
    }

    /**
     * PUBLIC:
     * Set the DataSource JNDI name.
     */
    public void setDatasource(String datasource) {
        this.datasource = datasource;
    }

    /**
     * PUBLIC:
     * Return the database user name.
     */
    public String getUser() {
        return user;
    }

    /**
     * PUBLIC:
     * Set the database user name.
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * PUBLIC:
     * Return the database user password.
     */
    public String getPassword() {
        // Bug 4117441 - Secure programming practices, store password in char[]
        if (this.password != null) {
            return new String(this.password);
        } else {
            return null;
        }
    }

    /**
     * PUBLIC:
     * Return if a datasource is used.
     */
    public boolean hasDatasource() {
        return (datasource != null) && (datasource.length() > 0);
    }

    /**
     * PUBLIC:
     * Set the database user password.
     */
    public void setPassword(String password) {
        // Bug 4117441 - Secure programming practices, store password in char[]
        if (password != null) {
            this.password = password.toCharArray();
        } else {
            // respect explicit de-referencing of password
            this.password = null;
        }
    }

    public String toString() {
        return "AQConnectionSpec(" + getURL() + ")";
    }
}
