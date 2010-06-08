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
package org.eclipse.persistence.testing.tests.unwrappedconnection;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import oracle.jdbc.OracleConnection;

/**
 * Dummy DataSource that returns a OracleConnection wrapper.
 */

public class TestOracleDataSource implements DataSource {
    private String driverName;
    private String url;
    private Properties properties;

    public TestOracleDataSource(String driverName, String url, Properties properties) {
        this.driverName = driverName;
        this.url = url;
        this.properties = properties;
    }

    public Connection getConnection() throws SQLException {
        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException exception) {
            throw new RuntimeException("Driver Class not found: " + driverName);
        }

        return new TestOracleConnection((OracleConnection)DriverManager.getConnection(url, properties));
    }

    public Connection getConnection(String user, String password) throws SQLException {
        properties.put("user", user);
        properties.put("password", password);
        return getConnection();
    }

    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    public void setLoginTimeout(int arg1) throws SQLException {
    }

    public void setLogWriter(PrintWriter arg1) throws SQLException {
    }

    // From java.sql.Wrapper
    public boolean isWrapperFor(Class<?> iFace) throws SQLException{
        return false;
    }

    public <T>T unwrap(Class<T> iFace)  throws SQLException {
        return iFace.cast(this);
    }    
    
}
