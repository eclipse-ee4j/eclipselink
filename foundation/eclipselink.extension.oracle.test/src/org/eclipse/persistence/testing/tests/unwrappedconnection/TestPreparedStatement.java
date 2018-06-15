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

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;


/**
 * This class provides a wrapper around preparedStatement to allow eclipselink
 * unwrap functionality to be tested.
 */

public class TestPreparedStatement implements PreparedStatement{
    private PreparedStatement prepareStatement;

    public TestPreparedStatement(PreparedStatement prepareStatement){
        this.prepareStatement = prepareStatement;
    }

    @Override
    public void addBatch() throws SQLException {
        prepareStatement.addBatch();
    }

    @Override
    public void clearParameters() throws SQLException {
        prepareStatement.clearParameters();
    }

    @Override
    public boolean execute() throws SQLException {
        return prepareStatement.execute();
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        return new TestResultSet(prepareStatement.executeQuery());
    }

    @Override
    public int executeUpdate() throws SQLException {
        return prepareStatement.executeUpdate();
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return prepareStatement.getMetaData();
    }

    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        return prepareStatement.getParameterMetaData();
    }

    @Override
    public void setArray(int i, Array x) throws SQLException {
        prepareStatement.setArray(i, x);
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
        prepareStatement.setAsciiStream(parameterIndex, x, length);
    }

    @Override
    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        prepareStatement.setBigDecimal(parameterIndex, x);
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
        prepareStatement.setBinaryStream(parameterIndex, x, length);
    }

    @Override
    public void setBlob(int i, Blob x) throws SQLException {
        prepareStatement.setBlob(i, x);
    }

    @Override
    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        prepareStatement.setBoolean(parameterIndex, x);
    }

    @Override
    public void setByte(int parameterIndex, byte x) throws SQLException {
        prepareStatement.setByte(parameterIndex, x);
    }

    @Override
    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
        prepareStatement.setBytes(parameterIndex, x);
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
        prepareStatement.setCharacterStream(parameterIndex, reader, length);
    }

    @Override
    public void setClob(int i, Clob x) throws SQLException {
        prepareStatement.setClob(i, x);
    }

    @Override
    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
        prepareStatement.setDate(parameterIndex, x, cal);
    }

    @Override
    public void setDate(int parameterIndex, Date x) throws SQLException {
        prepareStatement.setDate(parameterIndex, x);
    }

    @Override
    public void setDouble(int parameterIndex, double x) throws SQLException {
        prepareStatement.setDouble(parameterIndex, x);
    }

    @Override
    public void setFloat(int parameterIndex, float x) throws SQLException {
        prepareStatement.setFloat(parameterIndex, x);
    }

    @Override
    public void setInt(int parameterIndex, int x) throws SQLException {
        prepareStatement.setInt(parameterIndex, x);
    }

    @Override
    public void setLong(int parameterIndex, long x) throws SQLException {
        prepareStatement.setLong(parameterIndex, x);
    }

    @Override
    public void setNull(int paramIndex, int sqlType, String typeName) throws SQLException {
        prepareStatement.setNull(paramIndex, sqlType, typeName);
    }

    @Override
    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        prepareStatement.setNull(parameterIndex, sqlType);
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType, int scale) throws SQLException {
        prepareStatement.setObject(parameterIndex, x, targetSqlType, scale);
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
        prepareStatement.setObject(parameterIndex, x, targetSqlType);
    }

    @Override
    public void setObject(int parameterIndex, Object x) throws SQLException {
        prepareStatement.setObject(parameterIndex, x);
    }

    @Override
    public void setRef(int i, Ref x) throws SQLException {
        prepareStatement.setRef(i, x);
    }

    @Override
    public void setShort(int parameterIndex, short x) throws SQLException {
        prepareStatement.setShort(parameterIndex, x);
    }

    @Override
    public void setString(int parameterIndex, String x) throws SQLException {
        prepareStatement.setString(parameterIndex, x);
    }

    @Override
    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
        prepareStatement.setTime(parameterIndex, x, cal);
    }

    @Override
    public void setTime(int parameterIndex, Time x) throws SQLException {
        prepareStatement.setTime(parameterIndex, x);
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
        prepareStatement.setTimestamp(parameterIndex, x, cal);
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        prepareStatement.setTimestamp(parameterIndex, x);
    }

    @Override
    public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
        prepareStatement.setAsciiStream(parameterIndex, x, length);
    }

    @Override
    public void setURL(int parameterIndex, URL x) throws SQLException {
        prepareStatement.setURL(parameterIndex, x);
    }

    @Override
    public void addBatch(String sql) throws SQLException {
        prepareStatement.addBatch(sql);
    }

    @Override
    public void cancel() throws SQLException {
        prepareStatement.cancel();
    }

    @Override
    public void clearBatch() throws SQLException {
        prepareStatement.clearBatch();
    }

    @Override
    public void clearWarnings() throws SQLException {
        prepareStatement.clearWarnings();
    }

    @Override
    public void close() throws SQLException {
        prepareStatement.close();
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        return prepareStatement.execute(sql, autoGeneratedKeys);
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        return prepareStatement.execute(sql, columnIndexes);
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        return prepareStatement.execute(sql, columnNames);
    }

    @Override
    public boolean execute(String sql) throws SQLException {
        return prepareStatement.execute(sql);
    }

    @Override
    public int[] executeBatch() throws SQLException {
        return prepareStatement.executeBatch();
    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        return new TestResultSet(prepareStatement.executeQuery(sql));
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        return prepareStatement.executeUpdate(sql, autoGeneratedKeys);
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        return prepareStatement.executeUpdate(sql, columnIndexes);
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        return prepareStatement.executeUpdate(sql, columnNames);
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        return prepareStatement.executeUpdate(sql);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return prepareStatement.getConnection();
    }

    @Override
    public int getFetchDirection() throws SQLException {
        return prepareStatement.getFetchDirection();
    }

    @Override
    public int getFetchSize() throws SQLException {
        return prepareStatement.getFetchSize();
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        return new TestResultSet(prepareStatement.getGeneratedKeys());
    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        return prepareStatement.getMaxFieldSize();
    }

    @Override
    public int getMaxRows() throws SQLException {
        return prepareStatement.getMaxRows();
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        return prepareStatement.getMoreResults();
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        return prepareStatement.getMoreResults(current);
    }

    @Override
    public int getQueryTimeout() throws SQLException {
        return prepareStatement.getQueryTimeout();
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        return new TestResultSet(prepareStatement.getResultSet());
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        return prepareStatement.getResultSetConcurrency();
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        return prepareStatement.getResultSetHoldability();
    }

    @Override
    public int getResultSetType() throws SQLException {
        return prepareStatement.getResultSetType();
    }

    @Override
    public int getUpdateCount() throws SQLException {
        return prepareStatement.getUpdateCount();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return prepareStatement.getWarnings();
    }

    @Override
    public void setCursorName(String name) throws SQLException {
        prepareStatement.setCursorName(name);
    }

    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {
        prepareStatement.setEscapeProcessing(enable);
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        prepareStatement.setFetchDirection(direction);
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        prepareStatement.setFetchSize(rows);
    }

    @Override
    public void setMaxFieldSize(int max) throws SQLException {
        prepareStatement.setMaxRows(max);
    }

    @Override
    public void setMaxRows(int max) throws SQLException {
        prepareStatement.setMaxRows(max);
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {
        prepareStatement.setQueryTimeout(seconds);
    }

    @Override
    public void setAsciiStream(int columnIndex, InputStream stream, long length)  throws SQLException {
        prepareStatement.setAsciiStream(columnIndex, stream, length);
    }

    @Override
    public void setAsciiStream(int columnIndex, InputStream stream)  throws SQLException {
        prepareStatement.setAsciiStream(columnIndex, stream);
    }

    @Override
    public void setBlob(int columnIndex, InputStream stream, long length)  throws SQLException {
    }

    @Override
    public void setBlob(int columnIndex, InputStream stream)  throws SQLException {
        prepareStatement.setBlob(columnIndex, stream);
    }

    @Override
    public void setBinaryStream(int columnIndex, InputStream stream, long length)  throws SQLException {
        prepareStatement.setBinaryStream(columnIndex, stream, length);
    }

    @Override
    public void setBinaryStream(int columnIndex, InputStream stream)  throws SQLException {
        prepareStatement.setBinaryStream(columnIndex, stream);
    }

    @Override
    public void setCharacterStream(int columnIndex, Reader reader, long length)  throws SQLException {
        prepareStatement.setCharacterStream(columnIndex, reader, length);
    }

    @Override
    public void setCharacterStream(int columnIndex, Reader reader)  throws SQLException {
        prepareStatement.setCharacterStream(columnIndex, reader);
    }

    @Override
    public void setClob(int columnIndex, Reader reader, long length)  throws SQLException {
        prepareStatement.setClob(columnIndex, reader, length);
    }

    @Override
    public void setClob(int columnIndex, Reader reader)  throws SQLException {
        prepareStatement.setClob(columnIndex, reader);
    }

    @Override
    public void setNCharacterStream(int columnIndex, Reader reader, long length)  throws SQLException {
        prepareStatement.setNCharacterStream(columnIndex, reader, length);
    }

    @Override
    public void setNCharacterStream(int columnIndex, Reader reader)  throws SQLException {
        prepareStatement.setNCharacterStream(columnIndex, reader);
    }

    @Override
    public void setNClob(int columnIndex, Reader reader, long length)  throws SQLException {
        prepareStatement.setNClob(columnIndex, reader, length);
    }

    @Override
    public void setNClob(int columnIndex, Reader reader)  throws SQLException {
        prepareStatement.setNClob(columnIndex, reader);
    }

    @Override
    public void setNClob(int columnIndex, NClob nclob)  throws SQLException {
        prepareStatement.setNClob(columnIndex, nclob);
    }

    @Override
    public void setNString(int columnIndex, String nString)  throws SQLException {
        prepareStatement.setNString(columnIndex, nString);
    }

    @Override
    public void setSQLXML(int columnIndex, SQLXML sqlxml)  throws SQLException {
        prepareStatement.setSQLXML(columnIndex, sqlxml);
    }

    @Override
    public void setRowId(int columnIndex, RowId rowid)  throws SQLException {
        prepareStatement.setRowId(columnIndex, rowid);
    }

    @Override
    public boolean isClosed()  throws SQLException {
        return prepareStatement.isClosed();
    }

    @Override
    public boolean isPoolable()  throws SQLException {
        return prepareStatement.isPoolable();
    }

    @Override
    public void setPoolable(boolean poolable)  throws SQLException {
        prepareStatement.setPoolable(poolable);
    }

    @Override
    public boolean isWrapperFor(Class<?> iFace) throws SQLException{
        return prepareStatement.isWrapperFor(iFace);
    }

    @Override
    public <T>T unwrap(Class<T> iFace)  throws SQLException {
        return prepareStatement.unwrap(iFace);
    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException{
        return prepareStatement.isCloseOnCompletion();
    }

    @Override
    public void closeOnCompletion() throws SQLException {
        prepareStatement.closeOnCompletion();
    }
}
