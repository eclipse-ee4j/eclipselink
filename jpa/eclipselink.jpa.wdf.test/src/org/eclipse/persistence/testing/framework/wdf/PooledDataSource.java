/*******************************************************************************
 * Copyright (c) 2005, 2009 SAP. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     SAP - initial API and implementation
 ******************************************************************************/

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

class PooledDataSource implements DataSource {

    private final DataSource other;

    private final Stack<Connection> connections = new Stack<Connection>();

    PooledDataSource(DataSource ds) {
        other = ds;
    }

    public Connection getConnection() throws SQLException {
        final Connection conn;
        if (connections.isEmpty()) {
            conn = other.getConnection();
        } else {
            conn = connections.pop();
        }
        return new PooledConnection(conn);
    }

    public Connection getConnection(String username, String password) throws SQLException {
        return other.getConnection(username, password);
    }

    public int getLoginTimeout() throws SQLException {
        return other.getLoginTimeout();
    }

    public PrintWriter getLogWriter() throws SQLException {
        return other.getLogWriter();
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return other.isWrapperFor(iface);
    }

    public void setLoginTimeout(int seconds) throws SQLException {
        other.setLoginTimeout(seconds);
    }

    public void setLogWriter(PrintWriter out) throws SQLException {
        other.setLogWriter(out);
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        return other.unwrap(iface);
    }

    private class PooledConnection implements Connection {

        private Connection other;

        public void clearWarnings() throws SQLException {
            assertOpen();
            other.clearWarnings();
        }

        public void close() throws SQLException {
            assertOpen();
            connections.push(other);
            other = null;
        }

        public void commit() throws SQLException {
            assertOpen();
            other.commit();
        }

        public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
            assertOpen();
            return other.createArrayOf(typeName, elements);
        }

        public Blob createBlob() throws SQLException {
            assertOpen();
            return other.createBlob();
        }

        public Clob createClob() throws SQLException {
            assertOpen();
            return other.createClob();
        }

        public NClob createNClob() throws SQLException {
            assertOpen();
            return other.createNClob();
        }

        public SQLXML createSQLXML() throws SQLException {
            assertOpen();
            return other.createSQLXML();
        }

        public Statement createStatement() throws SQLException {
            assertOpen();
            return other.createStatement();
        }

        public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
                throws SQLException {
            assertOpen();
            return other.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
        }

        public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
            assertOpen();
            return other.createStatement(resultSetType, resultSetConcurrency);
        }

        public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
            assertOpen();
            return other.createStruct(typeName, attributes);
        }

        public boolean getAutoCommit() throws SQLException {
            assertOpen();
            return other.getAutoCommit();
        }

        public String getCatalog() throws SQLException {
            assertOpen();
            return other.getCatalog();
        }

        public Properties getClientInfo() throws SQLException {
            assertOpen();
            return other.getClientInfo();
        }

        public String getClientInfo(String name) throws SQLException {
            assertOpen();
            return other.getClientInfo(name);
        }

        public int getHoldability() throws SQLException {
            assertOpen();
            return other.getHoldability();
        }

        public DatabaseMetaData getMetaData() throws SQLException {
            assertOpen();
            return other.getMetaData();
        }

        public int getTransactionIsolation() throws SQLException {
            assertOpen();
            return other.getTransactionIsolation();
        }

        public Map<String, Class<?>> getTypeMap() throws SQLException {
            assertOpen();
            return other.getTypeMap();
        }

        public SQLWarning getWarnings() throws SQLException {
            assertOpen();
            return other.getWarnings();
        }

        public boolean isClosed() throws SQLException {
            return other.isClosed();
        }

        public boolean isReadOnly() throws SQLException {
            assertOpen();
            return other.isReadOnly();
        }

        public boolean isValid(int timeout) throws SQLException {
            assertOpen();
            return other.isValid(timeout);
        }

        public boolean isWrapperFor(Class<?> iface) throws SQLException {
            assertOpen();
            return other.isWrapperFor(iface);
        }

        public String nativeSQL(String sql) throws SQLException {
            assertOpen();
            return other.nativeSQL(sql);
        }

        public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
                throws SQLException {
            assertOpen();
            return other.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        }

        public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
            assertOpen();
            return other.prepareCall(sql, resultSetType, resultSetConcurrency);
        }

        public CallableStatement prepareCall(String sql) throws SQLException {
            assertOpen();
            return other.prepareCall(sql);
        }

        public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
                int resultSetHoldability) throws SQLException {
            assertOpen();
            return other.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        }

        public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
            assertOpen();
            return other.prepareStatement(sql, resultSetType, resultSetConcurrency);
        }

        public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
            assertOpen();
            return other.prepareStatement(sql, autoGeneratedKeys);
        }

        public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
            assertOpen();
            return other.prepareStatement(sql, columnIndexes);
        }

        public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
            assertOpen();
            return other.prepareStatement(sql, columnNames);
        }

        public PreparedStatement prepareStatement(String sql) throws SQLException {
            assertOpen();
            return other.prepareStatement(sql);
        }

        public void releaseSavepoint(Savepoint savepoint) throws SQLException {
            assertOpen();
            other.releaseSavepoint(savepoint);
        }

        public void rollback() throws SQLException {
            assertOpen();
            other.rollback();
        }

        public void rollback(Savepoint savepoint) throws SQLException {
            assertOpen();
            other.rollback(savepoint);
        }

        public void setAutoCommit(boolean autoCommit) throws SQLException {
            assertOpen();
            other.setAutoCommit(autoCommit);
        }

        public void setCatalog(String catalog) throws SQLException {
            assertOpen();
            other.setCatalog(catalog);
        }

        public void setClientInfo(Properties properties) throws SQLClientInfoException {
            other.setClientInfo(properties);
        }

        public void setClientInfo(String name, String value) throws SQLClientInfoException {
            other.setClientInfo(name, value);
        }

        public void setHoldability(int holdability) throws SQLException {
            assertOpen();
            other.setHoldability(holdability);
        }

        public void setReadOnly(boolean readOnly) throws SQLException {
            assertOpen();
            other.setReadOnly(readOnly);
        }

        public Savepoint setSavepoint() throws SQLException {
            assertOpen();
            return other.setSavepoint();
        }

        public Savepoint setSavepoint(String name) throws SQLException {
            return other.setSavepoint(name);
        }

        public void setTransactionIsolation(int level) throws SQLException {
            assertOpen();
            other.setTransactionIsolation(level);
        }

        public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
            assertOpen();
            other.setTypeMap(map);
        }

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

    }

}
