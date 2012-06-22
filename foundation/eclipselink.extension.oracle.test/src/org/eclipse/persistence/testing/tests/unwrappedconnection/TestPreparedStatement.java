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
package org.eclipse.persistence.testing.tests.unwrappedconnection;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
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

    public void addBatch() throws SQLException {
        prepareStatement.addBatch();
    }

    public void clearParameters() throws SQLException {
        prepareStatement.clearParameters();
    }

    public boolean execute() throws SQLException {
        return prepareStatement.execute();
    }

    public ResultSet executeQuery() throws SQLException {
        return new TestResultSet(prepareStatement.executeQuery());
    }

    public int executeUpdate() throws SQLException {
        return prepareStatement.executeUpdate();
    }

    public ResultSetMetaData getMetaData() throws SQLException {
        return prepareStatement.getMetaData();
    }

    public ParameterMetaData getParameterMetaData() throws SQLException {
        return prepareStatement.getParameterMetaData();
    }

    public void setArray(int i, Array x) throws SQLException {
        prepareStatement.setArray(i, x);
    }

    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
        prepareStatement.setAsciiStream(parameterIndex, x, length);
    }

    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        prepareStatement.setBigDecimal(parameterIndex, x);
    }

    public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
        prepareStatement.setBinaryStream(parameterIndex, x, length);
    }

    public void setBlob(int i, Blob x) throws SQLException {
        prepareStatement.setBlob(i, x);
    }

    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        prepareStatement.setBoolean(parameterIndex, x);
    }

    public void setByte(int parameterIndex, byte x) throws SQLException {
        prepareStatement.setByte(parameterIndex, x);
    }

    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
        prepareStatement.setBytes(parameterIndex, x);
    }

    public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
        prepareStatement.setCharacterStream(parameterIndex, reader, length);
    }

    public void setClob(int i, Clob x) throws SQLException {
        prepareStatement.setClob(i, x);
    }

    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
        prepareStatement.setDate(parameterIndex, x, cal);
    }

    public void setDate(int parameterIndex, Date x) throws SQLException {
        prepareStatement.setDate(parameterIndex, x);
    }

    public void setDouble(int parameterIndex, double x) throws SQLException {
        prepareStatement.setDouble(parameterIndex, x);
    }

    public void setFloat(int parameterIndex, float x) throws SQLException {
        prepareStatement.setFloat(parameterIndex, x);
    }

    public void setInt(int parameterIndex, int x) throws SQLException {
        prepareStatement.setInt(parameterIndex, x);
    }

    public void setLong(int parameterIndex, long x) throws SQLException {
        prepareStatement.setLong(parameterIndex, x);
    }

    public void setNull(int paramIndex, int sqlType, String typeName) throws SQLException {
        prepareStatement.setNull(paramIndex, sqlType, typeName);
    }

    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        prepareStatement.setNull(parameterIndex, sqlType);
    }

    public void setObject(int parameterIndex, Object x, int targetSqlType, int scale) throws SQLException {
        prepareStatement.setObject(parameterIndex, x, targetSqlType, scale);
    }

    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
        prepareStatement.setObject(parameterIndex, x, targetSqlType);
    }

    public void setObject(int parameterIndex, Object x) throws SQLException {
        prepareStatement.setObject(parameterIndex, x);
    }

    public void setRef(int i, Ref x) throws SQLException {
        prepareStatement.setRef(i, x);
    }

    public void setShort(int parameterIndex, short x) throws SQLException {
        prepareStatement.setShort(parameterIndex, x);
    }

    public void setString(int parameterIndex, String x) throws SQLException {
        prepareStatement.setString(parameterIndex, x);
    }

    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
        prepareStatement.setTime(parameterIndex, x, cal);
    }

    public void setTime(int parameterIndex, Time x) throws SQLException {
        prepareStatement.setTime(parameterIndex, x);
    }

    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
        prepareStatement.setTimestamp(parameterIndex, x, cal);
    }

    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        prepareStatement.setTimestamp(parameterIndex, x);
    }

    public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
        prepareStatement.setAsciiStream(parameterIndex, x, length);
    }

    public void setURL(int parameterIndex, URL x) throws SQLException {
        prepareStatement.setURL(parameterIndex, x);
    }

    public void addBatch(String sql) throws SQLException {
        prepareStatement.addBatch(sql);
    }

    public void cancel() throws SQLException {
        prepareStatement.cancel();
    }

    public void clearBatch() throws SQLException {
        prepareStatement.clearBatch();
    }

    public void clearWarnings() throws SQLException {
        prepareStatement.clearWarnings();
    }

    public void close() throws SQLException {
        prepareStatement.close();
    }

    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        return prepareStatement.execute(sql, autoGeneratedKeys);
    }

    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        return prepareStatement.execute(sql, columnIndexes);
    }

    public boolean execute(String sql, String[] columnNames) throws SQLException {
        return prepareStatement.execute(sql, columnNames);
    }

    public boolean execute(String sql) throws SQLException {
        return prepareStatement.execute(sql);
    }

    public int[] executeBatch() throws SQLException {
        return prepareStatement.executeBatch();
    }

    public ResultSet executeQuery(String sql) throws SQLException {
        return new TestResultSet(prepareStatement.executeQuery(sql));
    }

    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        return prepareStatement.executeUpdate(sql, autoGeneratedKeys);
    }

    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        return prepareStatement.executeUpdate(sql, columnIndexes);
    }

    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        return prepareStatement.executeUpdate(sql, columnNames);
    }

    public int executeUpdate(String sql) throws SQLException {
        return prepareStatement.executeUpdate(sql);
    }

    public Connection getConnection() throws SQLException {
        return prepareStatement.getConnection();
    }

    public int getFetchDirection() throws SQLException {
        return prepareStatement.getFetchDirection();
    }

    public int getFetchSize() throws SQLException {
        return prepareStatement.getFetchSize();
    }

    public ResultSet getGeneratedKeys() throws SQLException {
        return new TestResultSet(prepareStatement.getGeneratedKeys());
    }

    public int getMaxFieldSize() throws SQLException {
        return prepareStatement.getMaxFieldSize();
    }

    public int getMaxRows() throws SQLException {
        return prepareStatement.getMaxRows();
    }

    public boolean getMoreResults() throws SQLException {
        return prepareStatement.getMoreResults();
    }

    public boolean getMoreResults(int current) throws SQLException {
        return prepareStatement.getMoreResults(current);
    }

    public int getQueryTimeout() throws SQLException {
        return prepareStatement.getQueryTimeout();
    }

    public ResultSet getResultSet() throws SQLException {
        return new TestResultSet(prepareStatement.getResultSet());
    }

    public int getResultSetConcurrency() throws SQLException {
        return prepareStatement.getResultSetConcurrency();
    }

    public int getResultSetHoldability() throws SQLException {
        return prepareStatement.getResultSetHoldability();
    }

    public int getResultSetType() throws SQLException {
        return prepareStatement.getResultSetType();
    }

    public int getUpdateCount() throws SQLException {
        return prepareStatement.getUpdateCount();
    }

    public SQLWarning getWarnings() throws SQLException {
        return prepareStatement.getWarnings();
    }

    public void setCursorName(String name) throws SQLException {
        prepareStatement.setCursorName(name);
    }

    public void setEscapeProcessing(boolean enable) throws SQLException {
        prepareStatement.setEscapeProcessing(enable);
    }

    public void setFetchDirection(int direction) throws SQLException {
        prepareStatement.setFetchDirection(direction);
    }

    public void setFetchSize(int rows) throws SQLException {
        prepareStatement.setFetchSize(rows);
    }

    public void setMaxFieldSize(int max) throws SQLException {
        prepareStatement.setMaxRows(max);
    }

    public void setMaxRows(int max) throws SQLException {
        prepareStatement.setMaxRows(max);
    }

    public void setQueryTimeout(int seconds) throws SQLException {
        prepareStatement.setQueryTimeout(seconds);
    }

    // 236070: Methods introduced in JDK 1.6
    
    public void setAsciiStream(int columnIndex, InputStream stream, long length)  throws SQLException {       
    }

    public void setAsciiStream(int columnIndex, InputStream stream)  throws SQLException {       
    }

    public void setAsciiStream(String columnLabel, InputStream stream, long length)  throws SQLException {       
    }

    public void setAsciiStream(String columnLabel, InputStream stream)  throws SQLException {       
    }

    public void setBlob(int columnIndex, InputStream stream, long length)  throws SQLException {       
    }

    public void setBlob(int columnIndex, InputStream stream)  throws SQLException {       
    }

    public void setBlob(String columnLabel, InputStream stream, long length)  throws SQLException {       
    }

    public void setBlob(String columnLabel, InputStream stream)  throws SQLException {       
    }

    public void setBinaryStream(int columnIndex, InputStream stream, long length)  throws SQLException {       
    }

    public void setBinaryStream(int columnIndex, InputStream stream)  throws SQLException {       
    }

    public void setBinaryStream(String columnLabel, InputStream stream, long length)  throws SQLException {       
    }

    public void setBinaryStream(String columnLabel, InputStream stream)  throws SQLException {       
    }
    
    public void setCharacterStream(int columnIndex, Reader reader, long length)  throws SQLException {       
    }

    public void setCharacterStream(int columnIndex, Reader reader)  throws SQLException {       
    }

    public void setCharacterStream(String columnLabel, Reader reader, long length)  throws SQLException {       
    }

    public void setCharacterStream(String columnLabel, Reader reader)  throws SQLException {       
    }
    
    public void setClob(int columnIndex, Reader reader, long length)  throws SQLException {       
    }

    public void setClob(int columnIndex, Reader reader)  throws SQLException {       
    }

    public void setClob(String columnLabel, Reader reader, long length)  throws SQLException {       
    }

    public void setClob(String columnLabel, Reader reader)  throws SQLException {       
    }
    
    public void setNCharacterStream(int columnIndex, Reader reader, long length)  throws SQLException {       
    }

    public void setNCharacterStream(int columnIndex, Reader reader)  throws SQLException {       
    }

    public void setNCharacterStream(String columnLabel, Reader reader, long length)  throws SQLException {       
    }

    public void setNCharacterStream(String columnLabel, Reader reader)  throws SQLException {       
    }

    public void setNClob(int columnIndex, Reader reader, long length)  throws SQLException {       
    }

    public void setNClob(int columnIndex, Reader reader)  throws SQLException {       
    }

    public void setNClob(String columnLabel, Reader reader, long length)  throws SQLException {       
    }

    public void setNClob(String columnLabel, Reader reader)  throws SQLException {       
    }
   
    public void setNClob(int columnIndex, NClob nclob)  throws SQLException {       
    }

    public void setNClob(String columnLabel, NClob nclob)  throws SQLException {       
    }
       
    public void setNString(int columnIndex, String nString)  throws SQLException {       
    }

    public void setNString(String columnLabel, String nString)  throws SQLException {       
    }

    public void setSQLXML(String columnLabel, SQLXML sqlxml)  throws SQLException {      
    }

    public void setSQLXML(int columnIndex, SQLXML sqlxml)  throws SQLException {       
    }
    
    public void setRowId(int columnIndex, RowId rowid)  throws SQLException {       
    }
    
    public void setRowId(String columnLabel, RowId rowid)  throws SQLException {       
    }
          
    public boolean isClosed()  throws SQLException {
        return false;       
    }

    public boolean isPoolable()  throws SQLException {
        return false;       
    }

    public void setPoolable(boolean poolable)  throws SQLException {
    }

    public boolean isWrapperFor(Class<?> iFace) throws SQLException{
        return false;
    }

    public <T>T unwrap(Class<T> iFace)  throws SQLException {
        return iFace.cast(this);
    }    
}
