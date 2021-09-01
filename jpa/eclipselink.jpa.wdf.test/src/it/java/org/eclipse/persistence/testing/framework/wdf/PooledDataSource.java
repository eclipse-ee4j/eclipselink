/*
 * Copyright (c) 2005, 2021 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2005, 2015 SAP. All rights reserved.
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
//     SAP - initial API and implementation

package org.eclipse.persistence.testing.framework.wdf;

import java.io.PrintWriter;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;

import javax.sql.DataSource;
import java.util.logging.Logger;
import java.util.concurrent.Executor;

class PooledDataSource implements DataSource {

    private final DataSource other;

    private final Stack<Connection> connections = new Stack<Connection>();

    PooledDataSource(DataSource ds) {
        other = ds;
    }

    @Override
    public Connection getConnection() throws SQLException {
        final Connection conn;
        if (connections.isEmpty()) {
            conn = other.getConnection();
        } else {
            conn = connections.pop();
        }
        return new PooledConnection(conn);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return other.getConnection(username, password);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return other.getLoginTimeout();
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return other.getLogWriter();
    }

    @Override
    public Logger getParentLogger() {return null;}

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return other.isWrapperFor(iface);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        other.setLoginTimeout(seconds);
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        other.setLogWriter(out);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return other.unwrap(iface);
    }

    private class PooledConnection implements Connection {

        private Connection other;

        @Override
        public void clearWarnings() throws SQLException {
            assertOpen();
            other.clearWarnings();
        }

        @Override
        public void close() throws SQLException {
            assertOpen();
            connections.push(other);
            other = null;
        }

        @Override
        public void commit() throws SQLException {
            assertOpen();
            other.commit();
        }

        @Override
        public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
            assertOpen();
            return other.createArrayOf(typeName, elements);
        }

        @Override
        public Blob createBlob() throws SQLException {
            assertOpen();
            return other.createBlob();
        }

        @Override
        public Clob createClob() throws SQLException {
            assertOpen();
            return other.createClob();
        }

        @Override
        public NClob createNClob() throws SQLException {
            assertOpen();
            return other.createNClob();
        }

        @Override
        public SQLXML createSQLXML() throws SQLException {
            assertOpen();
            return other.createSQLXML();
        }

        @Override
        public Statement createStatement() throws SQLException {
            assertOpen();
            return other.createStatement();
        }

        @Override
        public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
                throws SQLException {
            assertOpen();
            return other.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
        }

        @Override
        public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
            assertOpen();
            return other.createStatement(resultSetType, resultSetConcurrency);
        }

        @Override
        public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
            assertOpen();
            return other.createStruct(typeName, attributes);
        }

        @Override
        public boolean getAutoCommit() throws SQLException {
            assertOpen();
            return other.getAutoCommit();
        }

        @Override
        public String getCatalog() throws SQLException {
            assertOpen();
            return other.getCatalog();
        }

        @Override
        public Properties getClientInfo() throws SQLException {
            assertOpen();
            return other.getClientInfo();
        }

        @Override
        public String getClientInfo(String name) throws SQLException {
            assertOpen();
            return other.getClientInfo(name);
        }

        @Override
        public int getHoldability() throws SQLException {
            assertOpen();
            return other.getHoldability();
        }

        @Override
        public DatabaseMetaData getMetaData() throws SQLException {
            assertOpen();
            return other.getMetaData();
        }

        @Override
        public int getTransactionIsolation() throws SQLException {
            assertOpen();
            return other.getTransactionIsolation();
        }

        @Override
        public Map<String, Class<?>> getTypeMap() throws SQLException {
            assertOpen();
            return other.getTypeMap();
        }

        @Override
        public SQLWarning getWarnings() throws SQLException {
            assertOpen();
            return other.getWarnings();
        }

        @Override
        public boolean isClosed() throws SQLException {
            return other.isClosed();
        }

        @Override
        public boolean isReadOnly() throws SQLException {
            assertOpen();
            return other.isReadOnly();
        }

        @Override
        public boolean isValid(int timeout) throws SQLException {
            assertOpen();
            return other.isValid(timeout);
        }

        @Override
        public boolean isWrapperFor(Class<?> iface) throws SQLException {
            assertOpen();
            return other.isWrapperFor(iface);
        }

        @Override
        public String nativeSQL(String sql) throws SQLException {
            assertOpen();
            return other.nativeSQL(sql);
        }

        @Override
        public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
                throws SQLException {
            assertOpen();
            return other.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        }

        @Override
        public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
            assertOpen();
            return other.prepareCall(sql, resultSetType, resultSetConcurrency);
        }

        @Override
        public CallableStatement prepareCall(String sql) throws SQLException {
            assertOpen();
            return other.prepareCall(sql);
        }

        @Override
        public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
                                                  int resultSetHoldability) throws SQLException {
            assertOpen();
            return other.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        }

        @Override
        public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
            assertOpen();
            return other.prepareStatement(sql, resultSetType, resultSetConcurrency);
        }

        @Override
        public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
            assertOpen();
            return other.prepareStatement(sql, autoGeneratedKeys);
        }

        @Override
        public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
            assertOpen();
            return other.prepareStatement(sql, columnIndexes);
        }

        @Override
        public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
            assertOpen();
            return other.prepareStatement(sql, columnNames);
        }

        @Override
        public PreparedStatement prepareStatement(String sql) throws SQLException {
            assertOpen();
            return other.prepareStatement(sql);
        }

        @Override
        public void releaseSavepoint(Savepoint savepoint) throws SQLException {
            assertOpen();
            other.releaseSavepoint(savepoint);
        }

        @Override
        public void rollback() throws SQLException {
            assertOpen();
            other.rollback();
        }

        @Override
        public void rollback(Savepoint savepoint) throws SQLException {
            assertOpen();
            other.rollback(savepoint);
        }

        @Override
        public void setAutoCommit(boolean autoCommit) throws SQLException {
            assertOpen();
            other.setAutoCommit(autoCommit);
        }

        @Override
        public void setCatalog(String catalog) throws SQLException {
            assertOpen();
            other.setCatalog(catalog);
        }

        @Override
        public void setClientInfo(Properties properties) throws SQLClientInfoException {
            other.setClientInfo(properties);
        }

        @Override
        public void setClientInfo(String name, String value) throws SQLClientInfoException {
            other.setClientInfo(name, value);
        }

        @Override
        public void setHoldability(int holdability) throws SQLException {
            assertOpen();
            other.setHoldability(holdability);
        }

        @Override
        public void setReadOnly(boolean readOnly) throws SQLException {
            assertOpen();
            other.setReadOnly(readOnly);
        }

        @Override
        public Savepoint setSavepoint() throws SQLException {
            assertOpen();
            return other.setSavepoint();
        }

        @Override
        public Savepoint setSavepoint(String name) throws SQLException {
            return other.setSavepoint(name);
        }

        @Override
        public void setTransactionIsolation(int level) throws SQLException {
            assertOpen();
            other.setTransactionIsolation(level);
        }

        @Override
        public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
            assertOpen();
            other.setTypeMap(map);
        }

        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException {
            assertOpen();
            return other.unwrap(iface);
        }

        PooledConnection(Connection conn) throws SQLException {
            other = conn;
            other.setAutoCommit(true);
        }

        private void assertOpen() throws SQLException {
            if (other == null) {
                throw new SQLException("connection is closed");
            }
        }

        @Override
        public int getNetworkTimeout(){return 0;}

        @Override
        public void setNetworkTimeout(Executor executor, int milliseconds){}

        @Override
        public void abort(Executor executor){}

        @Override
        public String getSchema(){return null;}

        @Override
        public void setSchema(String schema){}
    }

}
