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

import javax.resource.cci.*;
import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.sessions.DatasourceLogin;
import org.eclipse.persistence.sessions.Session;

/**
 * <p>An <code>EISLogin</code> defines connection information and datasource 
 * properties.  There are three ways to connect to an EIS datasource through 
 * EclipseLink EIS:
 * <ul>
 * <li>Provide a JNDI name to the ConnectionFactory and use the default 
 * getConnection
 * <li>Provide a JNDI name to the ConnectionFactory and a driver specific 
 * ConnectionSpec to pass to the getConnection
 * <li>Connect in a non-managed way directly to the driver specific 
 * ConnectionFactory
 * </ul>
 *  
 * <p> A <code>EISConnectionSpec</code> must be provided to define how to 
 * connect to the EIS adapter.
 *
 * <p> The EIS platform can be used to provide datasource/driver specific 
 * behavior such as InteractionSpec and Record conversion.
 *
 * @see EISConnectionSpec
 *
 * @author James
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public class EISLogin extends DatasourceLogin {

    /**
     * Default constructor.
     */
    public EISLogin() {
        this(new EISPlatform());
    }

    /**
     * Constructor.
     */
    public EISLogin(Platform platform) {
        super(platform);
        this.connector = new EISConnectionSpec();
    }

    /**
     * Build and return the EIS accessorr.
     */
    public Accessor buildAccessor() {
        return new EISAccessor();
    }

    /**
     * Connect to the EIS adapter and return the Connection.
     */
    public Object connectToDatasource(Accessor accessor, Session session) {
        return getConnectionSpec().connectToDataSource((EISAccessor)accessor, getProperties());
    }

    /**
     * PUBLIC:
     * Set the password.
     */
    public void setPassword(String password) {
        // Avoid encryption
        // Bug 4117441 - Secure programming practices, store password in char[]        
        if (password != null) {
            setProperty("password", password.toCharArray());
        } else {
            // is null so remove the property
            // respect explicit de-referencing of password
            removeProperty("password");
        }
    }

    /**
     * PUBLIC:
     * Return the JNDI URL for the managed connection factory for the JCA adapter connecting to.
     */
    public String getConnectionFactoryURL() {
        if ((getConnectionSpec().getName() == null) || (getConnectionSpec().getName().size() == 0)) {
            return null;
        }
        return getConnectionSpec().getName().get(0);
    }

    /**
     * PUBLIC:
     * Set the JNDI URL for the managed connection factory for the JCA adapter connecting to.
     */
    public void setConnectionFactoryURL(String url) {
        if ((url == null) || (url.length() == 0)) {
            return;
        }
        getConnectionSpec().setName(url);
    }

    /**
     * Return the connector.
     * The connector defines how the connection is created.
     */
    public EISConnectionSpec getConnectionSpec() {
        return (EISConnectionSpec)getConnector();
    }

    /**
     * PUBLIC:
     * Set the EclipseLink connection spec.
     * The connection spec defines how to connect to the EIS adapter.
     */
    public void setConnectionSpec(EISConnectionSpec connectionSpec) {
        setConnector(connectionSpec);
    }

    /**
     * PUBLIC:
     * Configure the login to connect through a JDNI managed connection factory and the default getConnection().
     */
    public void configureConnectionSpec(String jndiName) {
        setConnectionSpec(new EISConnectionSpec(jndiName));
    }

    /**
     * PUBLIC:
     * Configure the login to connect through a non-managed connection factory and the default getConnection().
     */
    public void configureConnectionSpec(ConnectionFactory connectionFactory) {
        EISConnectionSpec spec = new EISConnectionSpec();
        spec.setConnectionFactory(connectionFactory);
        setConnectionSpec(spec);
    }

    /**
     * PUBLIC:
     * Configure the login to connect through a JDNI managed connection factory and adapter connection spec.
     */
    public void configureConnectionSpec(String jndiName, ConnectionSpec connectionSpec) {
        EISConnectionSpec spec = new EISConnectionSpec(jndiName);
        spec.setConnectionSpec(connectionSpec);
        setConnectionSpec(spec);
    }

    /**
     * PUBLIC:
     * Configure the login to connect through a non-managed connection factory and adapter connection spec.
     */
    public void configureConnectionSpec(ConnectionFactory connectionFactory, ConnectionSpec connectionSpec) {
        EISConnectionSpec spec = new EISConnectionSpec();
        spec.setConnectionFactory(connectionFactory);
        spec.setConnectionSpec(connectionSpec);
        setConnectionSpec(spec);
    }
}
