/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.unwrappedconnection;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

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

    @Override
    public Connection getConnection() throws SQLException {
        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException exception) {
            throw new RuntimeException("Driver Class not found: " + driverName);
        }

        return new TestOracleConnection((OracleConnection)DriverManager.getConnection(url, properties));
    }

    @Override
    public Connection getConnection(String user, String password) throws SQLException {
        properties.put("user", user);
        properties.put("password", password);
        return getConnection();
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    @Override
    public Logger getParentLogger() {return null;}

    @Override
    public void setLoginTimeout(int arg1) throws SQLException {
    }

    @Override
    public void setLogWriter(PrintWriter arg1) throws SQLException {
    }

    @Override
    public boolean isWrapperFor(Class<?> iFace) throws SQLException{
        return false;
    }

    @Override
    public <T>T unwrap(Class<T> iFace)  throws SQLException {
        return iFace.cast(this);
    }

}
