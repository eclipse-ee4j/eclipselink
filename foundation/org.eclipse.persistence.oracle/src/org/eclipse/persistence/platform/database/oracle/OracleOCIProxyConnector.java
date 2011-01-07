/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.platform.database.oracle;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.NamingException;

import oracle.jdbc.pool.OracleOCIConnectionPool;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.sessions.JNDIConnector;

public class OracleOCIProxyConnector extends JNDIConnector {
    /**
     * PUBLIC:
     * Construct a Connector with no settings.
     * The datasource name will still need to be set.
     */
    public OracleOCIProxyConnector() {
        super();
    }

    /**
     * PUBLIC:
     * Construct a Connector with the datasource name.
     */
    public OracleOCIProxyConnector(Context context, String name) throws ValidationException {
        super(context, name);
    }

    /**
     * PUBLIC:
     * Construct a Connector with the datasource name.
     */
    public OracleOCIProxyConnector(String name) {
        super(name);
    }

    /**
     * PUBLIC:
     * Construct a Connector with OracleOCIConnectionPool.
     */
    public OracleOCIProxyConnector(OracleOCIConnectionPool oracleOCIConnectionPool) {
        super(oracleOCIConnectionPool);
    }

    /**
     * INTERNAL:
     * In case "proxytype" property is specified connects using proxy connection,
     * otherwise calls its superclass.
     */
    public Connection connect(Properties properties) throws DatabaseException, ValidationException {
        String proxytype = properties.getProperty(OracleOCIConnectionPool.PROXYTYPE);
        if(proxytype == null || proxytype.length() == 0) {
            return super.connect(properties);
        } else {
            try {
                OracleOCIConnectionPool oracleOCIConnectionPool = (OracleOCIConnectionPool)getDataSource();
                if (oracleOCIConnectionPool == null) {
                    try {
                        oracleOCIConnectionPool = (OracleOCIConnectionPool)getContext().lookup(getName());
                        this.setDataSource(oracleOCIConnectionPool);
                    } catch (NamingException exception) {
                        throw ValidationException.cannotAcquireDataSource(getName(), exception);
                    }
                }
                return oracleOCIConnectionPool.getProxyConnection(proxytype, properties);
            } catch (SQLException exception) {
                throw DatabaseException.sqlException(exception, true);
            } catch (ClassCastException classCastException) {
                throw ValidationException.oracleOCIProxyConnectorRequiresOracleOCIConnectionPool();
            }
        }
    }    
}
