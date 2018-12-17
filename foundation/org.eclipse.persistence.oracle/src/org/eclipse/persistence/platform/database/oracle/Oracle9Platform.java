/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     09/14/2011-2.3.1 Guy Pelletier
//       - 357533: Allow DDL queries to execute even when Multitenant entities are part of the PU
package org.eclipse.persistence.platform.database.oracle;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.TimeZone;
import java.util.Vector;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.expressions.ExpressionOperator;
import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.databaseaccess.BindCallCustomParameter;
import org.eclipse.persistence.internal.databaseaccess.ConnectionCustomizer;
import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.internal.databaseaccess.FieldTypeDefinition;
import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.internal.expressions.SpatialExpressionOperators;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.platform.database.XMLTypePlaceholder;
import org.eclipse.persistence.internal.platform.database.oracle.TIMESTAMPHelper;
import org.eclipse.persistence.internal.platform.database.oracle.TIMESTAMPLTZWrapper;
import org.eclipse.persistence.internal.platform.database.oracle.TIMESTAMPTZWrapper;
import org.eclipse.persistence.internal.platform.database.oracle.TIMESTAMPTypes;
import org.eclipse.persistence.internal.platform.database.oracle.XMLTypeFactory;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.internal.security.PrivilegedGetConstructorFor;
import org.eclipse.persistence.internal.security.PrivilegedInvokeConstructor;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.Call;
import org.eclipse.persistence.queries.ValueReadQuery;

import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleOpaque;
import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.OracleTypes;
import oracle.sql.TIMESTAMP;
import oracle.sql.TIMESTAMPLTZ;
import oracle.sql.TIMESTAMPTZ;

/**
 * <p><b>Purpose:</b>
 * Supports usage of certain Oracle JDBC specific APIs.
 * <p> Supports binding NCHAR, NVARCHAR, NCLOB types as required by Oracle JDBC drivers.
 * <p> Supports Oracle JDBC TIMESTAMP, TIMESTAMPTZ, TIMESTAMPLTZ types.
 */
public class Oracle9Platform extends Oracle8Platform {

    public static final Class NCHAR = NCharacter.class;
    public static final Class NSTRING = NString.class;
    public static final Class NCLOB = NClob.class;
    public static final Class XMLTYPE = XMLTypePlaceholder.class;

    /* Driver version set to connection.getMetaData.getDriverVersion() */
    protected transient String driverVersion;
    /* Indicates whether printCalendar should be used when creating TIMESTAMPTZ.
     * Bug5614674.  It used to be a driver bug and Helper.printCalendar(cal, false) was used to make it work.
     * It has been fixed in 11.  Separate the newer version from the old ones.
     * */
    protected transient boolean shouldPrintCalendar;
    /* Indicates whether TIMESTAMPTZ.timestampValue returns Timestamp in GMT.
     * The flag is set to false unless
     * Oracle jdbc version is 11.1.0.7 or later and
     * OracleConnection's "oracle.jdbc.timestampTzInGmt" property is set to "true".
     * Though the property is defined per connection it is safe to assume that all connections
     * used with the platform are identical because they all created by the same DatabaseLogin
     * with the same properties.
     * */
    protected transient boolean isTimestampInGmt;
    /* Indicates whether TIMESTAMPLTZ.toTimestamp returns Timestamp in GMT.
     * true for version 11.2.0.2 or later.
     */
    protected transient boolean isLtzTimestampInGmt;
    /* Indicates whether driverVersion, shouldPrintCalendar, isTimestampInGmt have been initialized.
     * To re-initialize connection data call clearConnectionData method.
     */
    protected transient boolean isConnectionDataInitialized;

    /** Indicates whether time component of java.sql.Date should be truncated (hours, minutes, seconds all set to zero)
     * before been passed as a parameter to PreparedStatement.
     * Starting with version 12.1 oracle jdbc Statement.setDate no longer zeroes sql.Date's entire time component (only milliseconds).
     * Set this flag to true to make the platform to truncate days/hours/minutes before passing the date to Statement.setDate method.
     */
    protected boolean shouldTruncateDate;

    private XMLTypeFactory xmlTypeFactory;

    /** User name retrieved from JDBC connection in {@link #initializeConnectionData(Connection)}. */
    private transient String connectionUserName;

    /**
     * Please ensure that following declarations stay as it is. Having them ensures that oracle jdbc driver available
     * in classpath when this class loaded.
     * If driver is not available, this class will not be initialized and will fail fast instead of failing later on
     * when classes from driver are utilized
     */
    private static final Class ORACLE_SQL_TIMESTAMP    = oracle.sql.TIMESTAMP.class;
    private static final Class ORACLE_SQL_TIMESTAMPTZ  = oracle.sql.TIMESTAMPTZ.class;
    private static final Class ORACLE_SQL_TIMESTAMPLTZ = oracle.sql.TIMESTAMPLTZ.class;


    public Oracle9Platform(){
        super();
        connectionUserName = null;
    }

    /**
     * INTERNAL:
     * This class used for binding of NCHAR, NSTRING, NCLOB types.
     */
    protected static class NTypeBindCallCustomParameter extends BindCallCustomParameter {
        public NTypeBindCallCustomParameter(Object obj) {
            super(obj);
        }
        //Bug5200836, use unwrapped connection if it is NType parameter.
        @Override
        public boolean shouldUseUnwrappedConnection() {
            return true;
        }

        /**
        * INTERNAL:
        * Binds the custom parameter (obj) into  the passed PreparedStatement
        * for the passed DatabaseCall.
        * Note that parameter numeration for PreparedStatement starts with 1,
        * therefore statement.set...(index + 1, ...) should be used.
        * DatabaseCall will carry this object as its parameter: call.getParameters().elementAt(index).
        * The reason for passing DatabaseCall and DatabasePlatform into this method
        * is that this method may set obj as a new value of index parameter:
        *   call.getParameters().setElementAt(obj, index);
        * and call again the method which has called it:
        *   platform.setParameterValueInDatabaseCall(call, statement, index);
        * so obj will be bound.
        *
        * Called only by DatabasePlatform.setParameterValueInDatabaseCall method
        */
        @Override
        public void set(DatabasePlatform platform, PreparedStatement statement, int index, AbstractSession session) throws SQLException {
            // Binding starts with a 1 not 0. Make sure that index > 0
            ((oracle.jdbc.OraclePreparedStatement)statement).setFormOfUse(index, oracle.jdbc.OraclePreparedStatement.FORM_NCHAR);

            super.set(platform, statement, index, session);
        }
    }

    /**
     * Copy the state into the new platform.
     */
    @Override
    public void copyInto(Platform platform) {
        super.copyInto(platform);
        if (!(platform instanceof Oracle9Platform)) {
            return;
        }
        Oracle9Platform oracle9Platform = (Oracle9Platform)platform;
        oracle9Platform.setShouldTruncateDate(shouldTruncateDate());
    }

    /**
     * INTERNAL:
     * Get a timestamp value from a result set.
     * Overrides the default behavior to specifically return a timestamp.  Added
     * to overcome an issue with the oracle 9.0.1.4 JDBC driver.
     */
    @Override
    public Object getObjectFromResultSet(ResultSet resultSet, int columnNumber, int type, AbstractSession session) throws java.sql.SQLException {
        //Bug#3381652 10G Drivers return sql.Date instead of timestamp on DATE field
        if ((type == Types.TIMESTAMP) || (type == Types.DATE)) {
            return resultSet.getTimestamp(columnNumber);
        } else if (type == oracle.jdbc.OracleTypes.TIMESTAMPTZ) {
            return getTIMESTAMPTZFromResultSet(resultSet, columnNumber, type, session);
        } else if (type == oracle.jdbc.OracleTypes.TIMESTAMPLTZ) {
            return getTIMESTAMPLTZFromResultSet(resultSet, columnNumber, type, session);
        } else if (type == OracleTypes.ROWID) {
            return resultSet.getString(columnNumber);
        } else if (type == Types.SQLXML) {
            SQLXML sqlXml = resultSet.getSQLXML(columnNumber);
            String str = null;
            if (sqlXml != null) {
                str = sqlXml.getString();
                sqlXml.free();
            }
            // Oracle 12c appends a \n character to the xml string
            return (str != null && str.endsWith("\n")) ? str.substring(0, str.length() - 1) : str;
        } else if (type == OracleTypes.OPAQUE) {
            try {
                Object result = resultSet.getObject(columnNumber);
                if (!(result instanceof OracleOpaque)) {
                    // Report Queries can cause result to not be an instance of OPAQUE.
                    return result;
                } else {
                    return getXMLTypeFactory().getString((OracleOpaque) result);
                }
            } catch (SQLException ex) {
                throw DatabaseException.sqlException(ex, null, session, false);
            }
        } else {
            return super.getObjectFromResultSet(resultSet, columnNumber, type, session);
        }
    }

    /**
     * INTERNAL:
     * Get a TIMESTAMPTZ value from a result set.
     */
    public Object getTIMESTAMPTZFromResultSet(ResultSet resultSet, int columnNumber, int type, AbstractSession session) throws java.sql.SQLException {
        TIMESTAMPTZ tsTZ = (TIMESTAMPTZ)resultSet.getObject(columnNumber);
        //Need to call timestampValue once here with the connection to avoid null point
        //exception later when timestampValue is called in converObject()
        if ((tsTZ != null) && (tsTZ.getLength() != 0)) {
            Connection connection = getConnection(session, resultSet.getStatement().getConnection());
            //Bug#4364359  Add a wrapper to overcome TIMESTAMPTZ not serializable as of jdbc 9.2.0.5 and 10.1.0.2.
            //It has been fixed in the next version for both streams
            Timestamp timestampToWrap = tsTZ.timestampValue(connection);
            TimeZone timezoneToWrap = TIMESTAMPHelper.extractTimeZone(tsTZ.toBytes());
            return new TIMESTAMPTZWrapper(timestampToWrap, timezoneToWrap, this.isTimestampInGmt);
        }
        return null;
    }

    /**
     * INTERNAL:
     * Get a TIMESTAMPLTZ value from a result set.
     */
    public Object getTIMESTAMPLTZFromResultSet(ResultSet resultSet, int columnNumber, int type, AbstractSession session) throws java.sql.SQLException {
        //TIMESTAMPLTZ needs to be converted to Timestamp here because it requires the connection.
        //However the java object is not know here.  The solution is to store Timestamp and the
        //session timezone in a wrapper class, which will be used later in converObject().
        TIMESTAMPLTZ tsLTZ = (TIMESTAMPLTZ)resultSet.getObject(columnNumber);
        if ((tsLTZ != null) && (tsLTZ.getLength() != 0)) {
            Connection connection = getConnection(session, resultSet.getStatement().getConnection());
            Timestamp timestampToWrap = TIMESTAMPLTZ.toTimestamp(connection, tsLTZ.toBytes());
            String sessionTimeZone = ((OracleConnection)connection).getSessionTimeZone();
            //Bug#4364359  Add a separate wrapper for TIMESTAMPLTZ.
            return new TIMESTAMPLTZWrapper(timestampToWrap, sessionTimeZone, this.isLtzTimestampInGmt);
        }
        return null;
    }

    /**
     * INTERNAL
     * Used by SQLCall.appendModify(..)
     * If the field should be passed to customModifyInDatabaseCall, retun true,
     * otherwise false.
     * Methods shouldCustomModifyInDatabaseCall and customModifyInDatabaseCall should be
     * kept in sync: shouldCustomModifyInDatabaseCall should return true if and only if the field
     * is handled by customModifyInDatabaseCall.
     */
    @Override
    public boolean shouldUseCustomModifyForCall(DatabaseField field) {
        Class type = field.getType();
        if ((type != null) && isOracle9Specific(type)) {
            return true;
        }
        return super.shouldUseCustomModifyForCall(field);
    }

    /**
     * INTERNAL:
     * Allow the use of XMLType operators on this platform.
     */
    @Override
    protected void initializePlatformOperators() {
        super.initializePlatformOperators();
        addOperator(ExpressionOperator.extractXml());
        addOperator(ExpressionOperator.extractValue());
        addOperator(ExpressionOperator.existsNode());
        addOperator(ExpressionOperator.isFragment());
        addOperator(ExpressionOperator.getStringVal());
        addOperator(ExpressionOperator.getNumberVal());
        addOperator(SpatialExpressionOperators.withinDistance());
        addOperator(SpatialExpressionOperators.relate());
        addOperator(SpatialExpressionOperators.filter());
        addOperator(SpatialExpressionOperators.nearestNeighbor());
    }

    /**
     * INTERNAL:
     * Add XMLType as the default database type for org.w3c.dom.Documents.
     * Add TIMESTAMP, TIMESTAMP WITH TIME ZONE and TIMESTAMP WITH LOCAL TIME ZONE
     */
    @Override
    protected Hashtable buildFieldTypes() {
        Hashtable fieldTypes = super.buildFieldTypes();
        fieldTypes.put(org.w3c.dom.Document.class, new FieldTypeDefinition("sys.XMLType"));
        //Bug#3381652 10g database does not accept Time for DATE field
        fieldTypes.put(java.sql.Time.class, new FieldTypeDefinition("TIMESTAMP", false));
        fieldTypes.put(java.sql.Timestamp.class, new FieldTypeDefinition("TIMESTAMP", false));
        fieldTypes.put(ORACLE_SQL_TIMESTAMP, new FieldTypeDefinition("TIMESTAMP", false));
        fieldTypes.put(ORACLE_SQL_TIMESTAMPTZ, new FieldTypeDefinition("TIMESTAMP WITH TIME ZONE", false));
        fieldTypes.put(ORACLE_SQL_TIMESTAMPLTZ, new FieldTypeDefinition("TIMESTAMP WITH LOCAL TIME ZONE", false));

        fieldTypes.put(java.time.LocalDate.class, new FieldTypeDefinition("DATE"));
        fieldTypes.put(java.time.LocalDateTime.class, new FieldTypeDefinition("TIMESTAMP"));
        fieldTypes.put(java.time.LocalTime.class, new FieldTypeDefinition("TIMESTAMP"));
        fieldTypes.put(java.time.OffsetDateTime.class, new FieldTypeDefinition("TIMESTAMP")); //TIMESTAMP WITH TIME ZONE ???
        fieldTypes.put(java.time.OffsetTime.class, new FieldTypeDefinition("TIMESTAMP")); //TIMESTAMP WITH TIME ZONE ???
        return fieldTypes;
    }

    /**
     * Build the hint string used for first rows.
     *
     * Allows it to be overridden
     * @param max
     * @return
     */
    @Override
    protected String buildFirstRowsHint(int max){
        return HINT_START + '(' + max + ')'+ HINT_END;
    }

    /**
     * INTERNAL:
     * Add TIMESTAMP, TIMESTAMP WITH TIME ZONE and TIMESTAMP WITH LOCAL TIME ZONE
     */
    @Override
    protected Map<String, Class> buildClassTypes() {
        Map<String, Class> classTypeMapping = super.buildClassTypes();
        classTypeMapping.put("TIMESTAMP", ORACLE_SQL_TIMESTAMP);
        classTypeMapping.put("TIMESTAMP WITH TIME ZONE", ORACLE_SQL_TIMESTAMPTZ);
        classTypeMapping.put("TIMESTAMP WITH LOCAL TIME ZONE", ORACLE_SQL_TIMESTAMPLTZ);
        return classTypeMapping;
    }

    @Override
    public Object clone() {
        Oracle9Platform clone = (Oracle9Platform)super.clone();
        clone.clearConnectionData();
        return clone;
    }

    /**
     * INTERNAL:
     * Allow for conversion from the Oracle type to the Java type.
     */
    @Override
    public Object convertObject(Object sourceObject, Class javaClass) throws ConversionException, DatabaseException {
        if ((javaClass == null) || ((sourceObject != null) && (sourceObject.getClass() == javaClass))) {
            return sourceObject;
        }
        if (sourceObject == null) {
            return super.convertObject(sourceObject, javaClass);
        }

        Object valueToConvert = sourceObject;

        //Used in Type Conversion Mapping on write
        if ((javaClass == TIMESTAMPTypes.TIMESTAMP_CLASS) || (javaClass == TIMESTAMPTypes.TIMESTAMPLTZ_CLASS)) {
            return sourceObject;
        }

        if (javaClass == TIMESTAMPTypes.TIMESTAMPTZ_CLASS) {
            if (sourceObject instanceof java.util.Date) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(((java.util.Date)sourceObject).getTime());
                return cal;
            } else {
                return sourceObject;
            }
        }

        if (javaClass == XMLTYPE) {
            //Don't convert to XMLTypes. This will be done by the
            //XMLTypeBindCallCustomParameter to ensure the correct
            //Connection is used
            return sourceObject;
        }

        //Added to overcome an issue with the oracle 9.0.1.1.0 JDBC driver.
        if (sourceObject instanceof TIMESTAMP) {
            try {
                valueToConvert = ((TIMESTAMP)sourceObject).timestampValue();
            } catch (SQLException exception) {
                throw DatabaseException.sqlException(exception);
            }
        } else if (sourceObject instanceof TIMESTAMPTZWrapper) {
            //Bug#4364359 Used when database type is TIMESTAMPTZ.  Timestamp and session timezone are wrapped
            //in TIMESTAMPTZWrapper.  Separate Calendar from any other types.
            if (((javaClass == ClassConstants.CALENDAR) || (javaClass == ClassConstants.GREGORIAN_CALENDAR))) {
                try {
                    return TIMESTAMPHelper.buildCalendar((TIMESTAMPTZWrapper)sourceObject);
                } catch (SQLException exception) {
                    throw DatabaseException.sqlException(exception);
                }
            } else {
                //If not using native sql, Calendar will be converted to Timestamp just as
                //other date time types
                valueToConvert = ((TIMESTAMPTZWrapper)sourceObject).getTimestamp();
            }
        } else if (sourceObject instanceof TIMESTAMPLTZWrapper) {
            //Bug#4364359 Used when database type is TIMESTAMPLTZ.  Timestamp and session timezone id are wrapped
            //in TIMESTAMPLTZWrapper.  Separate Calendar from any other types.
            if (((javaClass == ClassConstants.CALENDAR) || (javaClass == ClassConstants.GREGORIAN_CALENDAR))) {
                try {
                    return TIMESTAMPHelper.buildCalendar((TIMESTAMPLTZWrapper)sourceObject);
                } catch (SQLException exception) {
                    throw DatabaseException.sqlException(exception);
                }
            } else {
                //If not using native sql, Calendar will be converted to Timestamp just as
                //other date time types
                valueToConvert = ((TIMESTAMPLTZWrapper)sourceObject).getTimestamp();
            }
        }

        return super.convertObject(valueToConvert, javaClass);
    }

    /**
     * INTERNAL:
     *    Appends an Oracle specific Timestamp, if usesNativeSQL is true otherwise use the ODBC format.
     *    Native Format: to_timestamp ('1997-11-06 10:35:45.656' , 'yyyy-mm-dd hh:mm:ss.ff')
     */
    @Override
    protected void appendTimestamp(java.sql.Timestamp timestamp, Writer writer) throws IOException {
        if (usesNativeSQL()) {
            writer.write("to_timestamp('");
            writer.write(Helper.printTimestamp(timestamp));
            writer.write("','yyyy-mm-dd HH24:MI:SS.FF')");
        } else {
            super.appendTimestamp(timestamp, writer);
        }
    }

    /**
     * INTERNAL:
     * Appends an Oracle specific Timestamp with timezone and daylight time
     * elements if usesNativeSQL is true, otherwise use the ODBC format.
     * Native Format:
     * (DST) to_timestamp_tz ('1997-11-06 10:35:45.345 America/Los_Angeles','yyyy-mm-dd hh:mm:ss.ff TZR TZD')
     * (non-DST) to_timestamp_tz ('1997-11-06 10:35:45.345 America/Los_Angeles','yyyy-mm-dd hh:mm:ss.ff TZR')
     */
    @Override
    protected void appendCalendar(Calendar calendar, Writer writer) throws IOException {
        if (usesNativeSQL()) {
            writer.write("to_timestamp_tz('");
            writer.write(TIMESTAMPHelper.printCalendar(calendar));
            // append TZD element if the calendar's timezone is in daylight time
            if (TIMESTAMPHelper.shouldAppendDaylightTime(calendar)) {
                writer.write("','yyyy-mm-dd HH24:MI:SS.FF TZR TZD')");
            } else {
                writer.write("','yyyy-mm-dd HH24:MI:SS.FF TZR')");
            }
        } else {
            super.appendCalendar(calendar, writer);
        }
    }

    /**
     * INTERNAL:
     */
    @Override
    public void initializeConnectionData(Connection connection) throws SQLException {
        if (this.isConnectionDataInitialized || (connection == null) || (connection.getMetaData() == null)) {
            return;
        }
        this.driverVersion = connection.getMetaData().getDriverVersion();
        // printCalendar for versions greater or equal 9 and less than 10.2.0.4
        this.shouldPrintCalendar = Helper.compareVersions("9", this.driverVersion) <= 0 && Helper.compareVersions(this.driverVersion, "10.2.0.4") < 0;
        if (Helper.compareVersions(this.driverVersion, "11.1.0.7") >= 0) {
            if( connection instanceof OracleConnection ) {
                final OracleConnection oraConn = (OracleConnection)connection;
                String timestampTzInGmtPropStr = oraConn.getProperties().getProperty("oracle.jdbc.timestampTzInGmt", "true");
                this.connectionUserName = oraConn.getUserName();
                this.isTimestampInGmt = timestampTzInGmtPropStr.equalsIgnoreCase("true");
            } else {
                this.connectionUserName = connection.getMetaData().getUserName();
                this.isTimestampInGmt = true;
            }
            if (Helper.compareVersions(this.driverVersion, "11.2.0.2") >= 0) {
                this.isLtzTimestampInGmt = true;
            }
        }
        this.isConnectionDataInitialized = true;
    }

    public void clearConnectionData() {
        this.driverVersion = null;
        this.isConnectionDataInitialized = false;
    }

    /**
     * INTERNAL:
     * Clears both implicit and explicit caches of OracleConnection
     */
    @Override
    public void clearOracleConnectionCache(Connection conn) {
        if(conn instanceof OracleConnection){
            OracleConnection oracleConnection = (OracleConnection)conn;
            try {
                if(oracleConnection.getImplicitCachingEnabled()) {
                    oracleConnection.purgeImplicitCache();
                }
            } catch(SQLException ex) {
                // ignore
            }
            try {
                if(oracleConnection.getExplicitCachingEnabled()) {
                    oracleConnection.purgeExplicitCache();
                }
            } catch(SQLException ex) {
                // ignore
            }
        }
    }

    /**
     *  INTERNAL:
     *  Note that index (not index+1) is used in statement.setObject(index, parameter)
     *    Binding starts with a 1 not 0, so make sure that index &gt; 0.
     *  Treat Calendar separately. Bind Calendar as TIMESTAMPTZ.
     */
    @Override
    public void setParameterValueInDatabaseCall(Object parameter, PreparedStatement statement, int index, AbstractSession session) throws SQLException {
        if (parameter instanceof Calendar) {
            Calendar calendar = (Calendar)parameter;
            Connection conn = getConnection(session, statement.getConnection());
            TIMESTAMPTZ tsTZ = TIMESTAMPHelper.buildTIMESTAMPTZ(calendar, conn, this.shouldPrintCalendar);
            statement.setObject(index, tsTZ);
        } else if (this.shouldTruncateDate && parameter instanceof java.sql.Date) {
            // hours, minutes, seconds all set to zero
            statement.setDate(index, Helper.truncateDateIgnoreMilliseconds((java.sql.Date)parameter));
        } else {
            super.setParameterValueInDatabaseCall(parameter, statement, index, session);
        }
    }

    /**
     * INTERNAL:
     * Answer the timestamp from the server. Convert TIMESTAMPTZ to Timestamp
     */
    @Override
    public java.sql.Timestamp getTimestampFromServer(AbstractSession session, String sessionName) {
        if (getTimestampQuery() != null) {
            getTimestampQuery().setSessionName(sessionName);
            Object ob = session.executeQuery(getTimestampQuery());
            return ((TIMESTAMPTZWrapper)ob).getTimestamp();
        }
        return super.getTimestampFromServer(session, sessionName);
    }

    /**
     * INTERNAL:
     * This method returns the query to select the SYSTIMESTAMP as TIMESTAMPTZ
     * from the server for Oracle9i.
     */
    @Override
    public ValueReadQuery getTimestampQuery() {
        if (timestampQuery == null) {
            timestampQuery = new ValueReadQuery();
            timestampQuery.setSQLString("SELECT SYSTIMESTAMP FROM DUAL");
            timestampQuery.setAllowNativeSQLQuery(true);
        }
        return timestampQuery;
    }

    /**
     * INTERNAL:
     * Return the current SYSTIMESTAMP as TIMESTAMPTZ from the server.
     */
    @Override
    public String serverTimestampString() {
        return "SYSTIMESTAMP";
    }

    protected Vector buildToTIMESTAMPVec() {
        Vector vec = new Vector();
        vec.addElement(java.util.Date.class);
        vec.addElement(Timestamp.class);
        vec.addElement(Calendar.class);
        vec.addElement(String.class);
        vec.addElement(Long.class);
        vec.addElement(Date.class);
        vec.addElement(Time.class);
        return vec;
    }

    protected Vector buildToNStringCharVec() {
        Vector vec = new Vector();
        vec.addElement(String.class);
        vec.addElement(Character.class);
        return vec;
    }

    protected Vector buildToNClobVec() {
        Vector vec = new Vector();
        vec.addElement(String.class);
        vec.addElement(Character[].class);
        vec.addElement(char[].class);
        return vec;
    }

    /**
     * PUBLIC:
     * Return the BLOB/CLOB value limits on thin driver. The default value is 0.
     * If usesLocatorForLOBWrite is true, locator will be used in case the
     * lob's size is larger than lobValueLimit.
     */
    @Override
    public int getLobValueLimits() {
        return lobValueLimits;
    }

    /**
    * PUBLIC:
    * Set the BLOB/CLOB value limits on thin driver. The default value is 0.
    * If usesLocatorForLOBWrite is true, locator will be used in case the
    * lob's size is larger than lobValueLimit.
    */
    @Override
    public void setLobValueLimits(int lobValueLimits) {
        this.lobValueLimits = lobValueLimits;
    }

    /**
     * INTERNAL:
     * Return if the type is a special oracle type.
     * bug 3325122 - just checking against the 4 classes is faster than isAssignableFrom MWN.
     */
    protected boolean isOracle9Specific(Class type) {
        return (type == NCHAR) || (type == NSTRING) || (type == NCLOB) || (type == XMLTYPE);
    }

    /**
     * INTERNAL:
     * Used in write LOB method only to identify a CLOB.
     */
    @Override
    protected boolean isClob(Class type) {
        return NCLOB.equals(type) || super.isClob(type);
    }

    /**
     * INTERNAL:
     * Used by SQLCall.translate(..)
     * The binding *must* be performed (NCHAR, NSTRING, NCLOB).
     * In these special cases the method returns a wrapper object
     * which knows whether it should be bound or appended and knows how to do that.
     */
    @Override
    public Object getCustomModifyValueForCall(Call call, Object value, DatabaseField field, boolean shouldBind) {
        Class type = field.getType();
        if ((type != null) && isOracle9Specific(type)) {
            if(value == null) {
                return null;
            }
            if (NCHAR.equals(type) || NSTRING.equals(type)) {
                return new NTypeBindCallCustomParameter(value);
            } else if (NCLOB.equals(type)) {
                value = convertToDatabaseType(value);
                if (shouldUseLocatorForLOBWrite()) {
                    if (lobValueExceedsLimit(value)) {
                        ((DatabaseCall)call).addContext(field, value);
                        value = new String(" ");
                    }
                }
                return new NTypeBindCallCustomParameter(value);
            } else if (XMLTYPE.equals(type)) {
                return getXMLTypeFactory().createXMLTypeBindCallCustomParameter(value);
            }
        }
        return super.getCustomModifyValueForCall(call, value, field, shouldBind);
    }

    protected Vector buildFromStringCharVec(Class javaClass) {
        Vector vec = getConversionManager().getDataTypesConvertedFrom(javaClass);
        vec.addElement(NCHAR);
        vec.addElement(NSTRING);
        if (javaClass == String.class) {
            vec.addElement(NCLOB);
        }
        return vec;
    }

    /**
     * INTERNAL:
     * Return the list of Classes that can be converted to from the passed in javaClass.
     * oracle.sql.TIMESTAMP and NCHAR types are added in some lists.
     * @param javaClass - the class that is converted from
     * @return - a vector of classes
     */
    @Override
    public Vector getDataTypesConvertedFrom(Class javaClass) {
        if (dataTypesConvertedFromAClass == null) {
            dataTypesConvertedFromAClass = new Hashtable(5);
        }
        Vector dataTypes = (Vector) dataTypesConvertedFromAClass.get(javaClass);
        if (dataTypes != null) {
            return dataTypes;
        }
        dataTypes = super.getDataTypesConvertedFrom(javaClass);
        if ((javaClass == String.class) || (javaClass == Character.class)) {
            dataTypes.addElement(NCHAR);
            dataTypes.addElement(NSTRING);
            if (javaClass == String.class) {
                dataTypes.addElement(NCLOB);
            }
        }
        if ((javaClass == char[].class) || (javaClass == Character[].class)) {
            dataTypes.addElement(NCLOB);
        }
        dataTypesConvertedFromAClass.put(javaClass, dataTypes);
        return dataTypes;
    }

    /**
     * INTERNAL:
     * Return the list of Classes that can be converted from to the passed in javaClass.
     * A list is added for oracle.sql.TIMESTAMP and NCHAR types.
     * @param javaClass - the class that is converted to
     * @return - a vector of classes
     */
    @Override
    public Vector getDataTypesConvertedTo(Class javaClass) {
        if (dataTypesConvertedToAClass == null) {
            dataTypesConvertedToAClass = new Hashtable(5);
        }
        Vector dataTypes = (Vector) dataTypesConvertedToAClass.get(javaClass);
        if (dataTypes != null) {
            return dataTypes;
        }
        if ((javaClass == NCHAR) || (javaClass == NSTRING)) {
            dataTypes = buildToNStringCharVec();
        } else if (javaClass == NCLOB) {
            dataTypes = buildToNClobVec();
        } else {
            dataTypes = super.getDataTypesConvertedTo(javaClass);
        }
        dataTypesConvertedToAClass.put(javaClass, dataTypes);
        return dataTypes;
    }


    /**
     * Return the JDBC type for the given database field to be passed to Statement.setNull
     * The Oracle driver does not like the OPAQUE type so VARCHAR must be used.
     */
    @Override
    public int getJDBCTypeForSetNull(DatabaseField field) {
        int type = getJDBCType(field);
        if (type == OracleTypes.OPAQUE || type == Types_SQLXML) {
            // VARCHAR seems to work, driver does not like OPAQUE.
            return java.sql.Types.VARCHAR;
        }
        return type;
    }

    /**
     * Return the JDBC type for the Java type.
     * The Oracle driver does not like the OPAQUE type so VARCHAR must be used.
     */
    @Override
    public int getJDBCType(Class javaType) {
        if (javaType == XMLTYPE) {
            //return OracleTypes.OPAQUE;
            // VARCHAR seems to work...
            return java.sql.Types.VARCHAR;
        }
        return super.getJDBCType(javaType);
    }

    /**
     * INTERNAL: This gets called on each batch statement execution
     * Needs to be implemented so that it returns the number of rows successfully modified
     * by this statement for optimistic locking purposes (if useNativeBatchWriting is enabled, and
     * the call uses optimistic locking).
     *
     * @param isStatementPrepared - flag is set to true if this statement is prepared
     * @return - number of rows modified/deleted by this statement
     */
    @Override
    public int executeBatch(Statement statement,  boolean isStatementPrepared) throws SQLException {
        if (usesNativeBatchWriting() && isStatementPrepared) {
            int rowCount = 0;
            try {
                rowCount = ((OraclePreparedStatement)statement).sendBatch();
            } finally {
                ((OraclePreparedStatement) statement).setExecuteBatch(1);
            }
            return rowCount;
        } else {
            return super.executeBatch(statement, isStatementPrepared);
        }
    }

    /**
     * INTERNAL: This gets called on each iteration to add parameters to the batch
     * Needs to be implemented so that it returns the number of rows successfully modified
     * by this statement for optimistic locking purposes (if useNativeBatchWriting is enabled, and
     * the call uses optimistic locking).  Is used with parameterized SQL
     *
     * @return - number of rows modified/deleted by this statement if it was executed (0 if it wasn't)
     */
    @Override
    public int addBatch(PreparedStatement statement) throws java.sql.SQLException {
        if (usesNativeBatchWriting()){
            return statement.executeUpdate();
        }else{
            return super.addBatch(statement);
        }
    }

    /**
     * INTERNAL: Allows setting the batch size on the statement
     * Is used with parameterized SQL, and should only be passed in prepared statements
     *
     * @return - statement to be used for batch writing
     */
    @Override
    public Statement prepareBatchStatement(Statement statement, int maxBatchWritingSize) throws java.sql.SQLException {
        if (usesNativeBatchWriting()){
            //add max statement setting
            ((OraclePreparedStatement) statement).setExecuteBatch(maxBatchWritingSize);
        }
        return statement;
    }

    /**
     * INTERNAL:
     * Lazy initialization of xmlTypeFactory allows to avoid loading xdb-dependent
     * class XMLTypeFactoryImpl unless xdb is used.
     * @return XMLTypeFactory
     */
    protected XMLTypeFactory getXMLTypeFactory() {
        if(xmlTypeFactory == null) {
            String className = "org.eclipse.persistence.internal.platform.database.oracle.xdb.XMLTypeFactoryImpl";
            try {
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    Class xmlTypeFactoryClass = AccessController.doPrivileged(new PrivilegedClassForName(className, true, this.getClass().getClassLoader()));
                    Constructor xmlTypeFactoryConstructor = AccessController.doPrivileged(new PrivilegedGetConstructorFor(xmlTypeFactoryClass, new Class[0], true));
                    xmlTypeFactory = (XMLTypeFactory)AccessController.doPrivileged(new PrivilegedInvokeConstructor(xmlTypeFactoryConstructor, new Object[0]));
                }else{
                    Class xmlTypeFactoryClass = PrivilegedAccessHelper.getClassForName(className, true, this.getClass().getClassLoader());
                    Constructor xmlTypeFactoryConstructor = PrivilegedAccessHelper.getConstructorFor(xmlTypeFactoryClass, new Class[0], true);
                    xmlTypeFactory = (XMLTypeFactory)PrivilegedAccessHelper.invokeConstructor(xmlTypeFactoryConstructor, new Object[0]);
                }
            } catch (Exception e) {
                throw QueryException.reflectiveCallOnTopLinkClassFailed(className, e);
            }
        }
        return xmlTypeFactory;
    }

    /**
     * INTERNAL:
     * Indicates whether the passed object is an instance of XDBDocument.
     * @return boolean
     */
    @Override
    public boolean isXDBDocument(Object obj) {
        return getXMLTypeFactory().isXDBDocument(obj);
    }

    /**
     * INTERNAL:
     * Indicates whether this Oracle platform can unwrap Oracle connection.
     */
    @Override
    public boolean canUnwrapOracleConnection() {
        return true;
    }

    /**
     * INTERNAL:
     * If can unwrap returns unwrapped Oracle connection, otherwise original connection.
     */
    @Override
    public Connection unwrapOracleConnection(Connection connection) {
        //Bug#4607977  Use getPhysicalConnection() instead of physicalConnectionWithin() because it's not suppported in 9.2 driver
        if(connection instanceof oracle.jdbc.internal.OracleConnection){
            return ((oracle.jdbc.internal.OracleConnection)connection).getPhysicalConnection();
        }else{
            return super.unwrapOracleConnection(connection);
        }
    }

    /**
     * PUBLIC:
     * Return is this is the Oracle 9 platform.
     */
    @Override
    public boolean isOracle9() {
        return true;
    }

    /**
     * INTERNAL:
     */
    @Override
    public ConnectionCustomizer createConnectionCustomizer(Accessor accessor, AbstractSession session) {
        Object proxyTypeValue = session.getProperty(PersistenceUnitProperties.ORACLE_PROXY_TYPE);
        if (proxyTypeValue == null || ((proxyTypeValue instanceof String) && ((String)proxyTypeValue).length() == 0)) {
            return null;
        } else {
            return new OracleJDBC_10_1_0_2ProxyConnectionCustomizer(accessor, session);
        }
    }

    /**
     * INTERNAL: Return the driver version.
     */
    public String getDriverVersion() {
        return driverVersion;
    }

    /**
     * INTERNAL: Return if timestamps are returned in GMT by the driver.
     */
    public boolean isTimestampInGmt() {
        return isTimestampInGmt;
    }

    /**
     * INTERNAL: Return if ltz timestamps are returned in GMT by the driver.
     */
    public boolean isLtzTimestampInGmt() {
        return isLtzTimestampInGmt;
    }

    /**
     * PUBLIC:
     * Indicates whether time component of java.sql.Date should be truncated (hours, minutes, seconds all set to zero)
     * before been passed as a parameter to PreparedStatement.
     * Starting with version 12.1 oracle jdbc Statement.setDate no longer zeroes sql.Date's entire time component (only milliseconds).
     * "true" indicates that the platform truncates days/hours/minutes before passing the date to Statement.setDate method.
     */
    public boolean shouldTruncateDate() {
        return shouldTruncateDate;
    }

    /**
     * PUBLIC:
     * Indicates whether time component of java.sql.Date should be truncated (hours, minutes, seconds all set to zero)
     * before been passed as a parameter to PreparedStatement.
     * Starting with version 12.1 oracle jdbc Statement.setDate no longer zeroes sql.Date's entire time component (only milliseconds).
     * Set this flag to true to make the platform to truncate days/hours/minutes before passing the date to Statement.setDate method.
     */
    public void setShouldTruncateDate(boolean shouldTruncateDate) {
        this.shouldTruncateDate = shouldTruncateDate;
    }

    /**
     * INTERNAL:
     * User name from JDBC connection is stored in {@link #initializeConnectionData(Connection)}.
     * @return Always returns {@code true}
     */
    @Override
    public boolean supportsConnectionUserName() {
        return true;
    }

    /**
     * INTERNAL:
     * Returns user name retrieved from JDBC connection. {@link #initializeConnectionData(Connection)} shall be called
     * before this method.
     * @return User name retrieved from JDBC connection.
     */
    @Override
    public String getConnectionUserName() {
        return this.connectionUserName;
    }

}
