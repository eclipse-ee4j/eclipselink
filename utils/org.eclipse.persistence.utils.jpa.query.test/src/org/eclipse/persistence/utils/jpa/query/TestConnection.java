/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.persistence.utils.jpa.query;

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

/**
 * A fake {@link Connection} so that EclipseLink can think it can connect to a database.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
final class TestConnection implements Connection {

	/**
	 * Creates a new <code>TestConnection</code>.
	 */
	TestConnection() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	public void clearWarnings() throws SQLException {
	}

	/**
	 * {@inheritDoc}
	 */
	public void close() throws SQLException {
	}

	/**
	 * {@inheritDoc}
	 */
	public void commit() throws SQLException {
	}

	/**
	 * {@inheritDoc}
	 */
	public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Blob createBlob() throws SQLException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Clob createClob() throws SQLException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public NClob createNClob() throws SQLException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public SQLXML createSQLXML() throws SQLException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Statement createStatement() throws SQLException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean getAutoCommit() throws SQLException {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getCatalog() throws SQLException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Properties getClientInfo() throws SQLException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getClientInfo(String name) throws SQLException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getHoldability() throws SQLException {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	public DatabaseMetaData getMetaData() throws SQLException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getTransactionIsolation() throws SQLException {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<String, Class<?>> getTypeMap() throws SQLException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public SQLWarning getWarnings() throws SQLException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isClosed() throws SQLException {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isReadOnly() throws SQLException {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isValid(int timeout) throws SQLException {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public String nativeSQL(String sql) throws SQLException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public CallableStatement prepareCall(String sql) throws SQLException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
	}

	/**
	 * {@inheritDoc}
	 */
	public void rollback() throws SQLException {
	}

	/**
	 * {@inheritDoc}
	 */
	public void rollback(Savepoint savepoint) throws SQLException {
	}

	/**
	 * {@inheritDoc}
	 */
	public void setAutoCommit(boolean autoCommit) throws SQLException {
	}

	/**
	 * {@inheritDoc}
	 */
	public void setCatalog(String catalog) throws SQLException {
	}

	/**
	 * {@inheritDoc}
	 */
	public void setClientInfo(Properties properties) throws SQLClientInfoException {
	}

	/**
	 * {@inheritDoc}
	 */
	public void setClientInfo(String name, String value) throws SQLClientInfoException {
	}

	/**
	 * {@inheritDoc}
	 */
	public void setHoldability(int holdability) throws SQLException {
	}

	/**
	 * {@inheritDoc}
	 */
	public void setReadOnly(boolean readOnly) throws SQLException {
	}

	/**
	 * {@inheritDoc}
	 */
	public Savepoint setSavepoint() throws SQLException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Savepoint setSavepoint(String name) throws SQLException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setTransactionIsolation(int level) throws SQLException {
	}

	/**
	 * {@inheritDoc}
	 */
	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return null;
	}
}