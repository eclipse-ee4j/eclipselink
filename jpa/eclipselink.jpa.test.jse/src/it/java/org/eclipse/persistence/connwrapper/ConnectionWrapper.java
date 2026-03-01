/*
 * Copyright (c) 2017, 2024 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2017, 2024 IBM Corporation and/or its affiliates. All rights reserved.
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
//     04/24/2017-2.6 Jody Grassel
//       - 515712: ServerSession numberOfNonPooledConnectionsUsed can become invalid when Exception is thrown connecting accessor
package org.eclipse.persistence.connwrapper;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Properties;
import java.util.concurrent.Executor;
/*
 * DriverWrapper works together with ConnectionWrapper.
 * This pair of classes allows to intercept calls to Driver and Connection methods.
 * DriverWrapper can imitate both the db going down (or losing network connection to it),
 * and coming back up (or network connection restored).
 * ConnectionWrapper have the same functionality, but applied to a single connection.
 *
 * There's an example of ConnectionWrapper usage in EntityManagerJUnitTestSuite:
 * testEMCloseAndOpen and testEMFactoryCloseAndOpen.
 *
 * To use DriverWrapper in jpa, initialize DriverWrapper - all methods are static - with the original driver name.
 *
 * Then create EMF, using PersistenceUnit properties, substitute:
 * the original driver class for DriverWrapper (optional) and
 * the original url for "coded" (':' substituted for'*') url (otherwise DriverWrapper would not be called - the original driver would).
 * If created EMF uses DriverWrapper it will print out connection string that looks like "jdbc*oracle*thin*@localhost*1521*orcl".
 *
 * DriverWrapper just passes all the calls to the wrapped driver (the one passed to initialize method):
 * connect method wraps the created connection into ConnectionWrapper and caches them.
 *
 * Unless any of "break" methods called it should function in exactly the same way as original driver, the same for connections, too.
 *
 * But of course real fun is in breaking:
 * breakDriver breaks all the methods (that throw SQLException) of the Driver;
 * brealOldConnections breaks all connections produced so far;
 * breakNewConnections ensures that all newly produced connections are broken.
 *
 * Any method called on broken connection results in SQLException.
 *
 * The simple scenarios used in both EntityManagerJUnitTestSuite tests is imitation of db going down, then coming back:
 * going down: call breakDriver and breakOldConnections (calling breakNewConnections is possible but won't add anything -
 * as long as driver is broken there will be no new connections);
 * coming back: call repairDriver - now new functional new connections could be created, but all old connections are still broken.
 *
 * Also you can also break / repair individual connection.
 * If, say breakOldConnections was performed on DriwerWrapper and repair on ConnectionWrapper the chronologically last call wins.
 * There's no harm in breaking (or repairing) several times in a row.
 *
 * You can pass custom exception string to each break method, otherwise defaults used (the string will be in SQLException, also visible in debugger).
 *
 * Another usage that seems useful: stepping through the code in debugger you can trigger SQLException
 * to be thrown by any Connection method at will
 * be setting broken flag on ConnectionWrapper to true.
 *
 * After the EMF using DriverWrapper is closed, call DriverWrapper.clear() to forget the wrapped driver and clear all the cached ConnectionWrappers.
 *
 * * NOTE: Copied into eclipselink.jpa.test.jse from /eclipselink.core.test/src/org/eclipse/persistence/testing/framework/ConnectionWrapper.java
 */
public class ConnectionWrapper implements Connection {

    Connection conn;
    boolean broken;
    String exceptionString;
    public static String defaultExceptionString = "ConnectionWrapper: broken";

    public ConnectionWrapper(Connection conn) {
        this.conn = conn;
        this.broken = DriverWrapper.newConnectionsBroken;
        this.exceptionString = DriverWrapper.newConnectionsBrokenExceptionString;
    }

    public void breakConnection() {
        breakConnection(defaultExceptionString);
    }
    public void breakConnection(String newExceptionString) {
        broken = true;
        exceptionString = newExceptionString;
    }
    public void repairConnection() {
        broken = false;
        exceptionString = null;
    }

    public boolean broken() {
        return broken;
    }
    public String getExceptionString() {
        return exceptionString;
    }

    /*
     * The following methods implement Connection interface
     */
    @Override
    public Statement createStatement() throws SQLException {
        if(broken) {
            throw new SQLException(getExceptionString());
        }
        return conn.createStatement();
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        if(broken) {
            throw new SQLException(getExceptionString());
        }
        return conn.prepareStatement(sql);
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        if(broken) {
            throw new SQLException(getExceptionString());
        }
        return conn.prepareCall(sql);
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        if(broken) {
            throw new SQLException(getExceptionString());
        }
        return conn.nativeSQL(sql);
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        if(broken) {
            throw new SQLException(getExceptionString());
        }
        conn.setAutoCommit(autoCommit);
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        if(broken) {
            throw new SQLException(getExceptionString());
        }
        return conn.getAutoCommit();
    }

    @Override
    public void commit() throws SQLException {
        if(broken) {
            throw new SQLException(getExceptionString());
        }
        conn.commit();
    }

    @Override
    public void rollback() throws SQLException {
        conn.rollback();
        if(broken) {
            throw new SQLException(getExceptionString());
        }
    }

    @Override
    public void close() throws SQLException {
        conn.close();
        if(broken) {
            throw new SQLException(getExceptionString());
        }
    }

    @Override
    public boolean isClosed() throws SQLException {
        if(broken) {
            throw new SQLException(getExceptionString());
        }
        return conn.isClosed();
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return conn.getMetaData();
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        if(broken) {
            throw new SQLException(getExceptionString());
        }
        conn.setReadOnly(readOnly);
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        if(broken) {
            throw new SQLException(getExceptionString());
        }
        return conn.isReadOnly();
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        if(broken) {
            throw new SQLException(getExceptionString());
        }
        conn.setCatalog(catalog);
    }

    @Override
    public String getCatalog() throws SQLException {
        if(broken) {
            throw new SQLException(getExceptionString());
        }
        return conn.getCatalog();
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        if(broken) {
            throw new SQLException(getExceptionString());
        }
        conn.setTransactionIsolation(level);
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        if(broken) {
            throw new SQLException(getExceptionString());
        }
        return conn.getTransactionIsolation();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        if(broken) {
            throw new SQLException(getExceptionString());
        }
        return conn.getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException {
        if(broken) {
            throw new SQLException(getExceptionString());
        }
        conn.clearWarnings();
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        if(broken) {
            throw new SQLException(getExceptionString());
        }
        return conn.createStatement(resultSetType, resultSetConcurrency);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType,
                                              int resultSetConcurrency)
    throws SQLException {
        if(broken) {
            throw new SQLException(getExceptionString());
        }
        return conn.prepareStatement(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType,
                                         int resultSetConcurrency) throws SQLException {
        if(broken) {
            throw new SQLException(getExceptionString());
        }
        return conn.prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public java.util.Map<String,Class<?>> getTypeMap() throws SQLException {
        if(broken) {
            throw new SQLException(getExceptionString());
        }
        return conn.getTypeMap();
    }

    @Override
    public void setTypeMap(java.util.Map<String,Class<?>> map) throws SQLException {
        if(broken) {
            throw new SQLException(getExceptionString());
        }
        conn.setTypeMap(map);
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        if(broken) {
            throw new SQLException(getExceptionString());
        }
        conn.setHoldability(holdability);
    }

    @Override
    public int getHoldability() throws SQLException {
        if(broken) {
            throw new SQLException(getExceptionString());
        }
        return conn.getHoldability();
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        if(broken) {
            throw new SQLException(getExceptionString());
        }
        return conn.setSavepoint();
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        if(broken) {
            throw new SQLException(getExceptionString());
        }
        return conn.setSavepoint(name);
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        if(broken) {
            throw new SQLException(getExceptionString());
        }
        conn.rollback();
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        if(broken) {
            throw new SQLException(getExceptionString());
        }
        conn.releaseSavepoint(savepoint);
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency,
                                     int resultSetHoldability) throws SQLException {
        if(broken) {
            throw new SQLException(getExceptionString());
        }
        return conn.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType,
                                              int resultSetConcurrency, int resultSetHoldability)
    throws SQLException {
        if(broken) {
            throw new SQLException(getExceptionString());
        }
        return conn.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType,
                                         int resultSetConcurrency,
                                         int resultSetHoldability) throws SQLException {
        if(broken) {
            throw new SQLException(getExceptionString());
        }
        return conn.prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        if(broken) {
            throw new SQLException(getExceptionString());
        }
        return conn.prepareStatement(sql, autoGeneratedKeys);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        if(broken) {
            throw new SQLException(getExceptionString());
        }
        return conn.prepareStatement(sql, columnIndexes);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        if(broken) {
            throw new SQLException(getExceptionString());
        }
        return conn.prepareStatement(sql, columnNames);
    }

    // 236070: Methods introduced in JDK 1.6 (stolen from EmulatedConnection).
    // Those *must* be no-op as long as this code should compile under jdk 1.5
    @Override
    public Array createArrayOf(String typeName, Object[] elements)  throws SQLException {
        return null;
    }

    @Override
    public Blob createBlob() throws SQLException {
        return null;
    }

    @Override
    public Clob createClob() throws SQLException {
        return null;
    }

    @Override
    public NClob createNClob()  throws SQLException {
        return null;
    }

    @Override
    public SQLXML createSQLXML()  throws SQLException {
        return null;
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes)  throws SQLException {
        return null;
    }

    @Override
    public Properties getClientInfo()  throws SQLException {
        return null;
    }

    @Override
    public String getClientInfo(String name)  throws SQLException {
        return null;
    }

    @Override
    public boolean isValid(int timeout)  throws SQLException {
        return false;
    }

    @Override
    public void setClientInfo(String name, String value) {
    }

    @Override
    public void setClientInfo(Properties properties) {
    }

    // From java.sql.Wrapper

    @Override
    public boolean isWrapperFor(Class<?> iFace) throws SQLException {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> iFace) throws SQLException {
        return iFace.cast(this);
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
