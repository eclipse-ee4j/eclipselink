/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.unwrappedconnection;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;

import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleOCIFailover;
import oracle.jdbc.OracleSavepoint;
import oracle.jdbc.pool.OracleConnectionCacheCallback;

/**
 * This class provid wrapper around real Oracle Connection to allow toplink
 * unwrap functionality being tested.
 */

public class TestOracleConnection implements java.sql.Connection{
    
    OracleConnection conn;
    public TestOracleConnection(OracleConnection connection){
        conn = connection;
    }
    
    public Connection _getPC() {
        return conn._getPC();
    }
    public void applyConnectionAttributes(Properties arg0) throws SQLException {
        conn.applyConnectionAttributes(arg0);
        
    }
    public void archive(int arg0, int arg1, String arg2) throws SQLException {
        conn.archive(arg0, arg1, arg2);
    }
    
    public void close(int arg0) throws SQLException {
        conn.close(arg0);
    }
    public void close(Properties arg0) throws SQLException {
        conn.close(arg0);
    }

    public boolean getAutoClose() throws SQLException {
        return conn.getAutoClose();
    }
    public CallableStatement getCallWithKey(String arg0) throws SQLException {
        return conn.getCallWithKey(arg0);
    }
    public Properties getConnectionAttributes() throws SQLException {
        return conn.getConnectionAttributes();
    }
    
    public int getConnectionReleasePriority() throws SQLException {
        return conn.getConnectionReleasePriority();
    }
    
    public boolean getCreateStatementAsRefCursor() {
        return conn.getCreateStatementAsRefCursor();
    }
    
    public int getDefaultExecuteBatch() {
        return conn.getDefaultExecuteBatch();
    }
    
    public int getDefaultRowPrefetch() {
        return conn.getDefaultRowPrefetch();
    }
    
    public Object getDescriptor(String arg0) {
        return conn.getDescriptor(arg0);
    }
    public short getEndToEndECIDSequenceNumber() throws SQLException {
        return conn.getEndToEndECIDSequenceNumber();
    }
    public String[] getEndToEndMetrics() throws SQLException {
        return conn.getEndToEndMetrics();
    }
    public boolean getExplicitCachingEnabled() throws SQLException {
        return conn.getExplicitCachingEnabled();
    }
    public boolean getImplicitCachingEnabled() throws SQLException {
        return conn.getImplicitCachingEnabled();
    }
    public boolean getIncludeSynonyms() {
        return conn.getIncludeSynonyms();
    }
    public Object getJavaObject(String arg0) throws SQLException {
        return conn.getIncludeSynonyms();
    }
    public Properties getProperties() {
        return conn.getProperties();
    }
    public boolean getRemarksReporting() {
        return conn.getRemarksReporting();
    }
    public boolean getRestrictGetTables() {
        return conn.getRestrictGetTables();
    }
    public String getSessionTimeZone() {
        return conn.getSessionTimeZone();
    }
    public String getSQLType(Object arg0) throws SQLException {
        return conn.getSQLType(arg0);
    }
    public int getStatementCacheSize() throws SQLException {
        return conn.getStatementCacheSize();
    }
    public PreparedStatement getStatementWithKey(String arg0) throws SQLException {
        return conn.getStatementWithKey(arg0);
    }
    public int getStmtCacheSize() {
        return conn.getStmtCacheSize();
    }
    public short getStructAttrCsId() throws SQLException {
        return conn.getStructAttrCsId();
    }
    public Properties getUnMatchedConnectionAttributes() throws SQLException {
        return conn.getUnMatchedConnectionAttributes();
    }
    public String getUserName() throws SQLException {
        return conn.getUserName();
    }
    public boolean getUsingXAFlag() {
        return conn.getUsingXAFlag();
    }
    public boolean getXAErrorFlag() {
        return conn.getXAErrorFlag();
    }
    public boolean isLogicalConnection() {
        return conn.isLogicalConnection();
    }
    public boolean isProxySession() {
        return conn.isProxySession();
    }
    public void openProxySession(int arg0, Properties arg1) throws SQLException {
        conn.openProxySession(arg0, arg1);
    }
    public void oracleReleaseSavepoint(OracleSavepoint arg0) throws SQLException {
        conn.oracleReleaseSavepoint(arg0);
    }
    public void oracleRollback(OracleSavepoint arg0) throws SQLException {
        conn.oracleRollback(arg0);
    }
    public OracleSavepoint oracleSetSavepoint() throws SQLException {
        return conn.oracleSetSavepoint();
    }
    public OracleSavepoint oracleSetSavepoint(String arg0) throws SQLException {
        return conn.oracleSetSavepoint(arg0);
    }
    public oracle.jdbc.internal.OracleConnection physicalConnectionWithin() {
        return conn.physicalConnectionWithin();
    }
    public int pingDatabase(int arg0) throws SQLException {
        return conn.pingDatabase(arg0);
    }
    public CallableStatement prepareCallWithKey(String arg0) throws SQLException {
        return conn.prepareCallWithKey(arg0);
    }
    public PreparedStatement prepareStatementWithKey(String arg0) throws SQLException {
        return conn.prepareStatementWithKey(arg0);
    }
    public void purgeExplicitCache() throws SQLException {
        conn.purgeExplicitCache();
    }
    public void purgeImplicitCache() throws SQLException {
        conn.purgeImplicitCache();
    }
    public void putDescriptor(String arg0, Object arg1) throws SQLException {
        conn.putDescriptor(arg0, arg1);
    }
    public void registerConnectionCacheCallback(OracleConnectionCacheCallback arg0, Object arg1, int arg2) throws SQLException {
        conn.registerConnectionCacheCallback(arg0, arg1, arg2);
    }
    public void registerSQLType(String arg0, Class arg1) throws SQLException {
        conn.registerSQLType(arg0, arg1);
    }
    public void registerSQLType(String arg0, String arg1) throws SQLException {
        conn.registerSQLType(arg0, arg1);
    }
    public void registerTAFCallback(OracleOCIFailover arg0, Object arg1) throws SQLException {
        conn.registerTAFCallback(arg0, arg1);
    }
    public void setAutoClose(boolean arg0) throws SQLException {
        conn.setAutoClose(arg0);
    }
    public void setConnectionReleasePriority(int arg0) throws SQLException {
        conn.setConnectionReleasePriority(arg0);
    }
    public void setCreateStatementAsRefCursor(boolean arg0) {
        conn.setCreateStatementAsRefCursor(arg0);
    }
    public void setDefaultExecuteBatch(int arg0) throws SQLException {
        conn.setDefaultExecuteBatch(arg0);
    }
    public void setDefaultRowPrefetch(int arg0) throws SQLException {
        conn.setDefaultRowPrefetch(arg0);
    }
    public void setEndToEndMetrics(String[] arg0, short arg1) throws SQLException {
        conn.setEndToEndMetrics(arg0, arg1);
    }
    public void setExplicitCachingEnabled(boolean arg0) throws SQLException {
        conn.setExplicitCachingEnabled(arg0);
    }
    public void setImplicitCachingEnabled(boolean arg0) throws SQLException {
        conn.setImplicitCachingEnabled(arg0);
    }
    public void setIncludeSynonyms(boolean arg0) {
        conn.setIncludeSynonyms(arg0);
    }
    public void setPlsqlWarnings(String arg0) throws SQLException {
    }
    public void setRemarksReporting(boolean arg0) {
        conn.setRemarksReporting(arg0);
    }
    public void setRestrictGetTables(boolean arg0) {
        conn.setRestrictGetTables(arg0);
    }
    public void setSessionTimeZone(String arg0) throws SQLException {
        conn.setSessionTimeZone(arg0);
    }
    public void setStatementCacheSize(int arg0) throws SQLException {
        conn.setStatementCacheSize(arg0);
    }
    public void setStmtCacheSize(int arg0, boolean arg1) throws SQLException {
        conn.setStmtCacheSize(arg0, arg1);
    }
    public void setStmtCacheSize(int arg0) throws SQLException {
        conn.setStmtCacheSize(arg0);
    }
    public void setUsingXAFlag(boolean arg0) {
        conn.setUsingXAFlag(arg0);
    }
    public void setWrapper(OracleConnection arg0) {
        conn.setWrapper(arg0);
    }
    public void setXAErrorFlag(boolean arg0) {
        conn.setXAErrorFlag(arg0);
    }
    public void shutdown(int arg0) throws SQLException {
        conn.shutdown(arg0);
    }
    public void startup(String arg0, int arg1) throws SQLException {
        conn.startup(arg0, arg1);
    }
    public OracleConnection unwrap() {
        return conn.unwrap();
    }

    public void clearWarnings() throws SQLException {
       conn.clearWarnings();
    }
    public void close() throws SQLException {
        conn.close();
    }
    public void commit() throws SQLException {
        conn.commit();
    }
    public Statement createStatement() throws SQLException {
        return new TestStatement(conn.createStatement());
    }
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return new TestStatement(conn.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability));
    }
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return new TestStatement(conn.createStatement(resultSetType, resultSetConcurrency));
    }
    public boolean getAutoCommit() throws SQLException {
        return conn.getAutoCommit();
    }
    public String getCatalog() throws SQLException {
        return conn.getCatalog();
    }
    public int getHoldability() throws SQLException {
        return conn.getHoldability();
        
    }
    public DatabaseMetaData getMetaData() throws SQLException {
        return conn.getMetaData();
    }
    public int getTransactionIsolation() throws SQLException {
        return conn.getTransactionIsolation();
    }
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return getTypeMap();
    }
    public SQLWarning getWarnings() throws SQLException {
        return conn.getWarnings();
    }
    public boolean isClosed() throws SQLException {
        return conn.isClosed();
    }
    public boolean isReadOnly() throws SQLException {
        return conn.isReadOnly();
    }
    public String nativeSQL(String sql) throws SQLException {
        return conn.nativeSQL(sql);
    }
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return conn.prepareCall(sql,resultSetType,resultSetConcurrency,resultSetHoldability);
    }
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return conn.prepareCall(sql,resultSetType,resultSetConcurrency);
    }
    public CallableStatement prepareCall(String sql) throws SQLException {
        return conn.prepareCall(sql);
    }
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return new TestPreparedStatement(conn.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability));
    }
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return new TestPreparedStatement(conn.prepareStatement(sql, resultSetType, resultSetConcurrency));
    }
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return new TestPreparedStatement(conn.prepareStatement(sql, autoGeneratedKeys));
    }
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return new TestPreparedStatement(conn.prepareStatement(sql, columnIndexes));
    }
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return new TestPreparedStatement(conn.prepareStatement(sql, columnNames));
    }
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return new TestPreparedStatement(conn.prepareStatement(sql));
    }
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        conn.releaseSavepoint(savepoint);
    }
    public void rollback() throws SQLException {
        conn.rollback();
    }
    public void rollback(Savepoint savepoint) throws SQLException {
        conn.rollback(savepoint);
    }
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        conn.setAutoCommit(autoCommit);
    }
    public void setCatalog(String catalog) throws SQLException {
        conn.setCatalog(catalog);
    }
    public void setHoldability(int holdability) throws SQLException {
        conn.setHoldability(holdability);
    }
    public void setReadOnly(boolean readOnly) throws SQLException {
        conn.setReadOnly(readOnly);
    }
    public Savepoint setSavepoint() throws SQLException {
        return conn.setSavepoint();
    }

    public Savepoint setSavepoint(String name) throws SQLException {
        return conn.setSavepoint(name);
    }

    public void setTransactionIsolation(int level) throws SQLException {
        conn.setTransactionIsolation(level);
        
    }

    public void setTypeMap(Map<String, Class<?>> arg0) throws SQLException {
        conn.setTypeMap(arg0);
    }
    
    public Connection getPhysicalConnection(){
        return conn;
    }
    
}
