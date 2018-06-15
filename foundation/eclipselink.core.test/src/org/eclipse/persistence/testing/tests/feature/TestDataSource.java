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
package org.eclipse.persistence.testing.tests.feature;

import java.util.*;
import java.io.*;
import java.sql.*;

import javax.sql.*;
import java.util.logging.Logger;
/**
 * dummy DataSource that returns a Connection
 */
public class TestDataSource implements DataSource {
    private String driverName;
    private String url;
    private Properties properties;

    public TestDataSource(String driverName, String url, Properties properties) {
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

        return DriverManager.getConnection(url, properties);
    }

    public Connection getConnection(String user, String password) throws SQLException {
        properties.put("user", user);
        properties.put("password", password);
        return this.getConnection();
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


    public boolean isWrapperFor(Class<?> iFace) throws SQLException{
        return false;
    }

    public <T>T unwrap(Class<T> iFace)  throws SQLException {
        return iFace.cast(this);
    }

    public Logger getParentLogger(){return null;}
}
