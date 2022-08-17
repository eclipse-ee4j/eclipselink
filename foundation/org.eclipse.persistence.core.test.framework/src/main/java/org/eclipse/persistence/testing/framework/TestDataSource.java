/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.framework;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
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

    @Override
    public Connection getConnection() throws SQLException {
        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException exception) {
            throw new RuntimeException("Driver Class not found: " + driverName);
        }

        return DriverManager.getConnection(url, properties);
    }

    @Override
    public Connection getConnection(String user, String password) throws SQLException {
        properties.put("user", user);
        properties.put("password", password);
        return this.getConnection();
    }

    @Override
    public int getLoginTimeout() {
        return 0;
    }

    @Override
    public PrintWriter getLogWriter() {
        return null;
    }

    @Override
    public void setLoginTimeout(int arg1) {
    }

    @Override
    public void setLogWriter(PrintWriter arg1) {
    }


    @Override
    public boolean isWrapperFor(Class<?> iFace) {
        return false;
    }

    @Override
    public <T>T unwrap(Class<T> iFace) {
        return iFace.cast(this);
    }

    @Override
    public Logger getParentLogger(){return null;}
}
