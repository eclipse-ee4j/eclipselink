/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
import javax.sql.DataSource;
import java.util.Properties;
import javax.naming.Context;
import oracle.jdbc.OracleConnection;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.sessions.JNDIConnector;

public class OracleJDBC10_1_0_2ProxyConnector extends JNDIConnector {
    /**
     * PUBLIC:
     * Construct a Connector with no settings.
     * The datasource name will still need to be set.
     */
    public OracleJDBC10_1_0_2ProxyConnector() {
        super();
    }

    /**
     * PUBLIC:
     * Construct a Connector with the datasource name.
     */
    public OracleJDBC10_1_0_2ProxyConnector(Context context, String name) throws ValidationException {
        super(context, name);
    }

    /**
     * PUBLIC:
     * Construct a Connector with the datasource name.
     */
    public OracleJDBC10_1_0_2ProxyConnector(String name) {
        super(name);
    }

    /**
     * PUBLIC:
     * Construct a Connector with OracleOCIConnectionPool.
     */
    public OracleJDBC10_1_0_2ProxyConnector(DataSource dataSource) {
        super(dataSource);
    }

    /**
     * INTERNAL:
     * In case "proxytype" property is specified connects using proxy connection,
     * otherwise calls its superclass.
     */
    public Connection connect(Properties properties) throws DatabaseException, ValidationException {
        Connection conn = super.connect(properties);
        String proxytype = properties.getProperty("proxytype");
        if(proxytype != null && proxytype.length() > 0) {
            int proxytype_int;
            try {
                proxytype_int = Integer.valueOf(proxytype).intValue();
            } catch (ClassCastException classCastException) {
                throw ValidationException.oracleJDBC10_1_0_2ProxyConnectorRequiresIntProxytype();
            }
            try {
                ((OracleConnection)conn).openProxySession(proxytype_int, properties);
            } catch (SQLException exception) {
                throw DatabaseException.sqlException(exception);
            } catch (ClassCastException classCastException) {
                throw ValidationException.oracleJDBC10_1_0_2ProxyConnectorRequiresOracleConnection();
            } catch (NoSuchMethodError noSuchMethodError) {
                throw ValidationException.oracleJDBC10_1_0_2ProxyConnectorRequiresOracleConnectionVersion();
            }
        }
        return conn;
    }    
}
