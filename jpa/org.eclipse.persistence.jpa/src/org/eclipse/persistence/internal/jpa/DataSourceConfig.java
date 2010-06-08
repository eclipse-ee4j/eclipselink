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
package org.eclipse.persistence.internal.jpa;

import java.io.StringWriter;

/**
 * Simplest of all possible holder objects for all of the data source 
 * info required by the Entity test environment. Use the constructor to 
 * simultaneously create the instance and set the fields. 
 * <p>
 * At least one (transactional) data source must be specified and 
 * potentially a non-transactional if such a data source is appropriate 
 * (e.g. for non-transactional operations).
 * <p>
 * @see ContainerConfig
 * @see EntityContainer
 */
public class DataSourceConfig {

    /** Identifier to name this data source (must be Container-unique) */
    public String dsName;

    /** JNDI name that data source should be bound to */
    public String jndiName;

    /** URL that is passed to the driver to determine db */
    public String url;

    /** Driver class name string */
    public String driver;

    /** User name to use when connecting to the db */
    public String user;

    /** Password to use when connecting to the db */
    public String password;
    
    /**
     * Constructor used to create a DataSourceConfig
     *
     * @param dsName Data source identifier 
     * @param jndiName Name that the data source should be bound to in JNDI
     * @param url Passed to the driver to determine db
     * @param driver The class name for the db driver
     * @param user User name to use when connecting to the db
     * @param password Password to use when connecting to the db
     */
    public DataSourceConfig(String dsName, String jndiName, String url, String driver, String user, String password) {
        this.dsName = dsName;
        this.jndiName = jndiName;
        this.url = url;
        this.driver = driver;
        this.user = user;
        this.password = password;
    }

    /**
     * INTERNAL:
     */
    public String toString() {
        StringWriter writer = new StringWriter();
        if(dsName != null) {
            writer.write("dsName = " + dsName + '\n');
        }
        if(jndiName != null) {
            writer.write("jndiName = " + jndiName + '\n');
        }
        if(url != null) {
            writer.write("url = " + url + '\n');
        }
        if(driver != null) {
            writer.write("driver = " + driver + '\n');
        }
        if(user != null) {
            writer.write("user = " + user + '\n');
        }
        return writer.toString();
    }
}
