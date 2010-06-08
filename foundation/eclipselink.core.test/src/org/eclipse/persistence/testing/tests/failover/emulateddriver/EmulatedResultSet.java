/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
 package org.eclipse.persistence.testing.tests.failover.emulateddriver;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Vector;

import org.eclipse.persistence.sessions.DatabaseRecord;

public class EmulatedResultSet implements ResultSet {
	protected Vector rows;
	protected int index;

	public EmulatedResultSet(Vector rows) {
		this.rows = rows;
		this.index = 0;
	}

	public Vector getRows() {
		return rows;
	}

	public boolean next() {
		this.index++;
		return this.index <= this.rows.size();
	}

	public void close() {
	}

	public boolean wasNull() {
		return false;
	}

	public String getString(int columnIndex) {
		return (String) getObject(columnIndex);
	}

	public boolean getBoolean(int columnIndex) {
		return ((Boolean) getObject(columnIndex)).booleanValue();
	}

	public byte getByte(int columnIndex) {
		return ((Number) getObject(columnIndex)).byteValue();
	}

	public short getShort(int columnIndex) {
		return ((Number) getObject(columnIndex)).shortValue();
	}

	public int getInt(int columnIndex) {
		return ((Number) getObject(columnIndex)).intValue();
	}

	public long getLong(int columnIndex) {
		Number value = (Number) getObject(columnIndex);
		if (value == null) {
			return 0;
		} else {
			return value.longValue();
		}
	}

	public float getFloat(int columnIndex) {
		return ((Number) getObject(columnIndex)).floatValue();
	}

	public double getDouble(int columnIndex) {
		return ((Number) getObject(columnIndex)).doubleValue();
	}

	public BigDecimal getBigDecimal(int columnIndex, int scale) {
		return (BigDecimal) getObject(columnIndex);
	}

	public byte[] getBytes(int columnIndex) {
		return (byte[]) getObject(columnIndex);
	}

	public java.sql.Date getDate(int columnIndex) {
		return (java.sql.Date) getObject(columnIndex);
	}

	public java.sql.Time getTime(int columnIndex) {
		return (java.sql.Time) getObject(columnIndex);
	}

	public java.sql.Timestamp getTimestamp(int columnIndex) {
		return (java.sql.Timestamp) getObject(columnIndex);
	}

	public java.io.InputStream getAsciiStream(int columnIndex) {
		return (java.io.InputStream) getObject(columnIndex);
	}

	public java.io.InputStream getUnicodeStream(int columnIndex) {
		return (java.io.InputStream) getObject(columnIndex);
	}

	public java.io.InputStream getBinaryStream(int columnIndex) {
		return (java.io.InputStream) getObject(columnIndex);
	}

	public String getString(String columnName) {
		return (String) getObject(columnName);
	}

	public boolean getBoolean(String columnName) {
		return ((Boolean) getObject(columnName)).booleanValue();
	}

	public byte getByte(String columnName) {
		return ((Number) getObject(columnName)).byteValue();
	}

	public short getShort(String columnName) {
		return ((Number) getObject(columnName)).shortValue();
	}

	public int getInt(String columnName) {
		return ((Number) getObject(columnName)).intValue();
	}

	public long getLong(String columnName) {
		return ((Number) getObject(columnName)).longValue();
	}

	public float getFloat(String columnName) {
		return ((Number) getObject(columnName)).floatValue();
	}

	public double getDouble(String columnName) {
		return ((Number) getObject(columnName)).doubleValue();
	}

	public BigDecimal getBigDecimal(String columnName, int scale) {
		return (BigDecimal) getObject(columnName);
	}

	public byte[] getBytes(String columnName) {
		return (byte[]) getObject(columnName);
	}

	public java.sql.Date getDate(String columnName) {
		return (java.sql.Date) getObject(columnName);
	}

	public java.sql.Time getTime(String columnName) {
		return (java.sql.Time) getObject(columnName);
	}

	public java.sql.Timestamp getTimestamp(String columnName) {
		return (java.sql.Timestamp) getObject(columnName);
	}

	public java.io.InputStream getAsciiStream(String columnName) {
		return (java.io.InputStream) getObject(columnName);
	}

	public java.io.InputStream getUnicodeStream(String columnName) {
		return (java.io.InputStream) getObject(columnName);
	}

	public java.io.InputStream getBinaryStream(String columnName) {
		return (java.io.InputStream) getObject(columnName);
	}

	public SQLWarning getWarnings() {
		return null;
	}

	public void clearWarnings() {
	}

	public String getCursorName() {
		return null;
	}

	public ResultSetMetaData getMetaData() {
		return new EmulatedResultSetMetaData(this);
	}

	public Object getObject(int columnIndex) {
		return ((DatabaseRecord) this.rows.get(this.index - 1)).getValues().get(columnIndex - 1);
	}

	public Object getObject(String columnName) {
		return ((DatabaseRecord) this.rows.get(this.index - 1)).get(columnName);
	}

	// ----------------------------------------------------------------

	public int findColumn(String columnName) {
		return 0;
	}

	// --------------------------JDBC 2.0-----------------------------------
	// ---------------------------------------------------------------------
	// Getters and Setters
	// ---------------------------------------------------------------------

	public java.io.Reader getCharacterStream(int columnIndex) {
		return (java.io.Reader) getObject(columnIndex);
	}

	public java.io.Reader getCharacterStream(String columnName) {
		return (java.io.Reader) getObject(columnName);
	}

	public BigDecimal getBigDecimal(int columnIndex) {
		return (BigDecimal) getObject(columnIndex);
	}

	public BigDecimal getBigDecimal(String columnName) {
		return (BigDecimal) getObject(columnName);
	}

	// ---------------------------------------------------------------------
	// Traversal/Positioning
	// ---------------------------------------------------------------------

	public boolean isBeforeFirst() {
		return this.index == 0;
	}

	public boolean isAfterLast() {
		return (this.index - 1) == this.rows.size();
	}

	public boolean isFirst() {
		return this.index == 1;
	}

	public boolean isLast() {
		return this.index == this.rows.size();
	}

	public void beforeFirst() {
		this.index = 0;
	}

	public void afterLast() {
		this.index = this.rows.size() + 1;
	}

	public boolean first() {
		this.index = 1;
		return true;
	}

	public boolean last() {
		this.index = this.rows.size();
		return true;
	}

	public int getRow() {
		return index;
	}

	public boolean absolute(int row) {
		this.index = row;
		return true;
	}

	public boolean relative(int rows) {
		this.index = index + rows;
		return true;
	}

	public boolean previous() {
		this.index--;
		return true;
	}

	// ---------------------------------------------------------------------
	// Properties
	// ---------------------------------------------------------------------

	public void setFetchDirection(int direction) {
	}

	public int getFetchDirection() {
		return 0;
	}

	public void setFetchSize(int rows) {
	}

	public int getFetchSize() {
		return 0;
	}

	public int getType() {
		return 0;
	}

	public int getConcurrency() {
		return 0;
	}

	// ---------------------------------------------------------------------
	// Updates
	// ---------------------------------------------------------------------

	public boolean rowUpdated() {
		return false;
	}

	public boolean rowInserted() {
		return false;
	}

	public boolean rowDeleted() {
		return false;
	}

	public void updateNull(int columnIndex) {
	}

	public void updateBoolean(int columnIndex, boolean x) {
	}

	public void updateByte(int columnIndex, byte x) {
	}

	public void updateShort(int columnIndex, short x) {
	}

	public void updateInt(int columnIndex, int x) {
	}

	public void updateLong(int columnIndex, long x) {
	}

	public void updateFloat(int columnIndex, float x) {
	}

	public void updateDouble(int columnIndex, double x) {
	}

	public void updateBigDecimal(int columnIndex, BigDecimal x) {
	}

	public void updateString(int columnIndex, String x) {
	}

	public void updateBytes(int columnIndex, byte[] x) {
	}

	public void updateDate(int columnIndex, java.sql.Date x) {
	}

	public void updateTime(int columnIndex, java.sql.Time x) {
	}

	public void updateTimestamp(int columnIndex, java.sql.Timestamp x) {
	}

	public void updateAsciiStream(int columnIndex, java.io.InputStream x,
			int length) {
	}

	public void updateBinaryStream(int columnIndex, java.io.InputStream x,
			int length) {
	}

	public void updateCharacterStream(int columnIndex, java.io.Reader x,
			int length) {
	}

	public void updateObject(int columnIndex, Object x, int scale) {
	}

	public void updateObject(int columnIndex, Object x) {
	}

	public void updateNull(String columnName) {
	}

	public void updateBoolean(String columnName, boolean x) {
	}

	public void updateByte(String columnName, byte x) {
	}

	public void updateShort(String columnName, short x) {
	}

	public void updateInt(String columnName, int x) {
	}

	public void updateLong(String columnName, long x) {
	}

	public void updateFloat(String columnName, float x) {
	}

	public void updateDouble(String columnName, double x) {
	}

	public void updateBigDecimal(String columnName, BigDecimal x) {
	}

	public void updateString(String columnName, String x) {
	}

	public void updateBytes(String columnName, byte[] x) {
	}

	public void updateDate(String columnName, java.sql.Date x) {
	}

	public void updateTime(String columnName, java.sql.Time x) {
	}

	public void updateTimestamp(String columnName, java.sql.Timestamp x) {
	}

	public void updateAsciiStream(String columnName, java.io.InputStream x,
			int length) {
	}

	public void updateBinaryStream(String columnName, java.io.InputStream x,
			int length) {
	}

	public void updateCharacterStream(String columnName, java.io.Reader reader,
			int length) {
	}

	public void updateObject(String columnName, Object x, int scale) {
	}

	public void updateObject(String columnName, Object x) {
	}

	public void insertRow() {
	}

	public void updateRow() {
	}

	public void deleteRow() {
	}

	public void refreshRow() {
	}

	public void cancelRowUpdates() {
	}

	public void moveToInsertRow() {
	}

	public void moveToCurrentRow() {
	}

	public Statement getStatement() {
		return null;
	}

	public Object getObject(int i, java.util.Map map) {
		return getObject(i);
	}

	public Ref getRef(int i) {
		return null;
	}

	public Blob getBlob(int i) {
		return (Blob) getObject(i);
	}

	public Clob getClob(int i) {
		return (Clob) getObject(i);
	}

	public Array getArray(int i) {
		return null;
	}

	public Object getObject(String colName, java.util.Map map) {
		return getObject(colName);
	}

	public Ref getRef(String colName) {
		return null;
	}

	public Blob getBlob(String colName) {
		return (Blob) getObject(colName);
	}

	public Clob getClob(String colName) {
		return (Clob) getObject(colName);
	}

	public Array getArray(String colName) {
		return null;
	}

	public java.sql.Date getDate(int columnIndex, Calendar cal) {
		return null;
	}

	public java.sql.Date getDate(String columnName, Calendar cal) {
		return null;
	}

	public java.sql.Time getTime(int columnIndex, Calendar cal) {
		return null;
	}

	public java.sql.Time getTime(String columnName, Calendar cal) {
		return null;
	}

	public java.sql.Timestamp getTimestamp(int columnIndex, Calendar cal) {
		return null;
	}

	public java.sql.Timestamp getTimestamp(String columnName, Calendar cal) {
		return null;
	}

	// -------------------------- JDBC 3.0
	// ----------------------------------------

	public java.net.URL getURL(int columnIndex) {
		return null;
	}

	public java.net.URL getURL(String columnName) {
		return null;
	}

	public void updateRef(int columnIndex, java.sql.Ref x) {
	}

	public void updateRef(String columnName, java.sql.Ref x) {
	}

	public void updateBlob(int columnIndex, java.sql.Blob x) {
	}

	public void updateBlob(String columnName, java.sql.Blob x) {
	}

	public void updateClob(int columnIndex, java.sql.Clob x) {
	}

	public void updateClob(String columnName, java.sql.Clob x) {
	}

	public void updateArray(int columnIndex, java.sql.Array x) {
	}

	public void updateArray(String columnName, java.sql.Array x) {
	}

	// 236070: Methods introduced in JDK 1.6
	public int getHoldability()  throws SQLException {
	    return 0;
	}
	
	public Reader getNCharacterStream(int columnIndex)  throws SQLException {
	    return null;
	}

	public Reader getNCharacterStream(String columnLabel)  throws SQLException {
	    return null;
	}

	public NClob getNClob(int columnIndex)  throws SQLException {
	    return null;
	}

   public NClob getNClob(String columnLabel)  throws SQLException {
        return null;
    }

   public String getNString(int columnIndex)  throws SQLException {
       return null;       
   }

   public String getNString(String columnLabel)  throws SQLException {
       return null;       
   }
   
   public RowId getRowId(int columnIndex)  throws SQLException {
       return null;
   }
   
   public RowId getRowId(String columnLabel)  throws SQLException {
       return null;
   }
   
   public SQLXML getSQLXML(int columnIndex)  throws SQLException {
       return null;
   }
   
   public SQLXML getSQLXML(String columnLabel)  throws SQLException {
       return null;
   }
   
   public boolean isClosed()  throws SQLException {
       return false;       
   }
   
   public void updateAsciiStream(int columnIndex, InputStream stream, long length)  throws SQLException {       
   }

   public void updateAsciiStream(int columnIndex, InputStream stream)  throws SQLException {       
   }

   public void updateAsciiStream(String columnLabel, InputStream stream, long length)  throws SQLException {       
   }

   public void updateAsciiStream(String columnLabel, InputStream stream)  throws SQLException {       
   }
   
   public void updateBlob(int columnIndex, InputStream stream, long length)  throws SQLException {       
   }

   public void updateBlob(int columnIndex, InputStream stream)  throws SQLException {       
   }

   public void updateBlob(String columnLabel, InputStream stream, long length)  throws SQLException {       
   }

   public void updateBlob(String columnLabel, InputStream stream)  throws SQLException {       
   }
   
   public void updateBinaryStream(int columnIndex, InputStream stream, long length)  throws SQLException {       
   }

   public void updateBinaryStream(int columnIndex, InputStream stream)  throws SQLException {       
   }

   public void updateBinaryStream(String columnLabel, InputStream stream, long length)  throws SQLException {       
   }

   public void updateBinaryStream(String columnLabel, InputStream stream)  throws SQLException {       
   }
   
   public void updateCharacterStream(int columnIndex, Reader reader, long length)  throws SQLException {       
   }

   public void updateCharacterStream(int columnIndex, Reader reader)  throws SQLException {       
   }

   public void updateCharacterStream(String columnLabel, Reader reader, long length)  throws SQLException {       
   }

   public void updateCharacterStream(String columnLabel, Reader reader)  throws SQLException {       
   }
   
   public void updateClob(int columnIndex, Reader reader, long length)  throws SQLException {       
   }

   public void updateClob(int columnIndex, Reader reader)  throws SQLException {       
   }

   public void updateClob(String columnLabel, Reader reader, long length)  throws SQLException {       
   }

   public void updateClob(String columnLabel, Reader reader)  throws SQLException {       
   }
   
   public void updateNCharacterStream(int columnIndex, Reader reader, long length)  throws SQLException {       
   }

   public void updateNCharacterStream(int columnIndex, Reader reader)  throws SQLException {       
   }

   public void updateNCharacterStream(String columnLabel, Reader reader, long length)  throws SQLException {       
   }

   public void updateNCharacterStream(String columnLabel, Reader reader)  throws SQLException {       
   }

   public void updateNClob(int columnIndex, Reader reader, long length)  throws SQLException {       
   }

   public void updateNClob(int columnIndex, Reader reader)  throws SQLException {       
   }

   public void updateNClob(String columnLabel, Reader reader, long length)  throws SQLException {       
   }

   public void updateNClob(String columnLabel, Reader reader)  throws SQLException {       
   }
  
   public void updateNClob(int columnIndex, NClob nclob)  throws SQLException {       
   }

   public void updateNClob(String columnLabel, NClob nclob)  throws SQLException {       
   }
   
   public void updateNString(int columnIndex, String nString)  throws SQLException {       
   }

   public void updateNString(String columnLabel, String nString)  throws SQLException {       
   }

   public void updateSQLXML(String columnLabel, SQLXML sqlxml)  throws SQLException {      
   }

   public void updateSQLXML(int columnIndex, SQLXML sqlxml)  throws SQLException {       
   }
   
   public void updateRowId(int columnIndex, RowId rowid)  throws SQLException {       
   }
   
   public void updateRowId(String columnLabel, RowId rowid)  throws SQLException {       
   }
   
    public boolean isWrapperFor(Class<?> iFace) throws SQLException{
        return false;
    }

    public <T>T unwrap(Class<T> iFace)  throws SQLException {
        return iFace.cast(this);
    }
	
}
