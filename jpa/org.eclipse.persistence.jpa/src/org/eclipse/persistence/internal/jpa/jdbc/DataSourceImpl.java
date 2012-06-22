/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.jpa.jdbc;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import javax.sql.DataSource;
import org.eclipse.persistence.internal.jpa.transaction.TransactionManagerImpl;

/**
 * A stubbed out impl of DataSource that can be used for testing.
 *
 * Does not support multiple threads or multiple usernames/passwords.
 */
public class DataSourceImpl implements DataSource {
    String dsName;
    String url;
    String userName;
    String password;

    // When a transaction is active we need to get the right connection.
    // This should not be set (will be null) when the data source is non-JTA (non-tx).
    TransactionManagerImpl tm;

    /************************/
    /***** Internal API *****/
    /************************/
    private void debug(String s) {
        System.out.println(s);
    }

    /*
     * Use this constructor to create a new datasource
     */
    public DataSourceImpl(String dsName, String url, String userName, String password) {
        this.dsName = dsName;
        this.url = url;
        this.userName = userName;
        this.password = password;
    }

    /*
     * Return the unique name of this data source
     */
    public String getName() {
        return this.dsName;
    }

    /*
     * This must be called right after initialization if data source is transactional.
     * Must not get set if data source is a non-transactional data source.
     */
    public void setTransactionManager(TransactionManagerImpl tm) {
        this.tm = tm;
    }

    /*
     * Get all connections from the DriverManager.
     */
    public Connection internalGetConnection(String userName, String password) throws SQLException {
        return DriverManager.getConnection(this.url, userName, password);
    }

    /*
     * Get all connections from the DriverManager.
     */
    public Connection internalGetConnection() throws SQLException {
        return internalGetConnection(this.userName, this.password);
    }

    /*
     * Return true if this data source is transactional, false if not
     */
    public boolean isTransactional() {
        return tm != null;
    }

    /************************************************************/
    /***** Supported DataSource  API *****/
    /************************************************************/

    /*
     * Forward to the other method.
     */
    public Connection getConnection() throws SQLException {
        return getConnection(this.userName, this.password);
    }

    /*
     * Go to the Transaction Manager to get a connection
     */
    public Connection getConnection(String userName, String password) throws SQLException {
        if (isTransactional() && tm.isTransactionActive()) {
            // This will actually eventually call back into this class, but allows
            // the connection to be cached in the transaction first
            return tm.getConnection(this, userName, password);
        } else {//{
            debug("Ds - Allocating new non-tx connection");
        }
        return internalGetConnection(userName, password);
    }

    /*
     * Forward to the DriverManager.
     */
    public PrintWriter getLogWriter() throws SQLException {
        return DriverManager.getLogWriter();
    }

    /*
     * Forward to the DriverManager.
     */
    public void setLogWriter(PrintWriter out) throws SQLException {
        DriverManager.setLogWriter(out);
    }

    /*
     * Forward to the DriverManager.
     */
    public int getLoginTimeout() throws SQLException {
        return DriverManager.getLoginTimeout();
    }

    /*
     * Forward to the DriverManager.
     */
    public void setLoginTimeout(int seconds) throws SQLException {
        DriverManager.setLoginTimeout(seconds);
    }

    /*
     * JDBC 4.0
     */
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new SQLException();
    }

    /*
     * JDBC 4.0
     */
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

}
