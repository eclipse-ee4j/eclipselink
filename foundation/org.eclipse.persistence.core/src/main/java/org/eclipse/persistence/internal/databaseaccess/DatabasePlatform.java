/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 * Copyright (c) 1998, 2026 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019, 2024 IBM Corporation. All rights reserved.
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
//     Markus KARG - Added methods allowing to support stored procedure creation on SQLAnywherePlatform.
//     tware - added implementation of computeMaxRowsForSQL
//     Dies Koper (Fujitsu) - bug fix for printFieldUnique()
//     Dies Koper (Fujitsu) - added methods to create/drop indices
//     Vikram Bhatia - added method for releasing temporary LOBs after conversion
//     09/09/2011-2.3.1 Guy Pelletier
//       - 356197: Add new VPD type to MultitenantType
//     02/04/2013-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support
//     04/30/2014-2.6 Lukas Jungmann
//       - 380101: Invalid MySQL SQL syntax in query with LIMIT and FOR UPDATE
//     02/19/2015 - Rick Curtis
//       - 458877 : Add national character support
//     02/23/2015-2.6 Dalia Abo Sheasha
//       - 460607: Change DatabasePlatform StoredProcedureTerminationToken to be configurable
//     11/12/2018 - Will Dazey
//       - 540929 : 'jdbc.sql-cast' property does not copy
//     12/06/2018 - Will Dazey
//       - 542491: Add new 'eclipselink.jdbc.force-bind-parameters' property to force enable binding
//     13/01/2022-4.0.0 Tomas Kraus
//       - 1391: JSON support in JPA
//     12/05/2023: Tomas Kraus
//       - New Jakarta Persistence 3.2 Features
package org.eclipse.persistence.internal.databaseaccess;

// javase imports

import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Struct;
import java.sql.Types;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;
import javax.xml.transform.dom.DOMResult;

import org.w3c.dom.Document;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.core.helper.CoreClassConstants;
import org.eclipse.persistence.internal.databaseaccess.DatasourceCall.ParameterType;
import org.eclipse.persistence.internal.expressions.ExpressionSQLPrinter;
import org.eclipse.persistence.internal.expressions.ParameterExpression;
import org.eclipse.persistence.internal.expressions.SQLSelectStatement;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.sequencing.Sequencing;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.mappings.structures.ObjectRelationalDatabaseField;
import org.eclipse.persistence.platform.database.AccessPlatform;
import org.eclipse.persistence.platform.database.DB2Platform;
import org.eclipse.persistence.platform.database.DBasePlatform;
import org.eclipse.persistence.platform.database.OraclePlatform;
import org.eclipse.persistence.platform.database.PostgreSQLPlatform;
import org.eclipse.persistence.platform.database.SybasePlatform;
import org.eclipse.persistence.platform.database.SymfowarePlatform;
import org.eclipse.persistence.platform.database.converters.StructConverter;
import org.eclipse.persistence.platform.database.partitioning.DataPartitioningCallback;
import org.eclipse.persistence.queries.Call;
import org.eclipse.persistence.queries.DataReadQuery;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ObjectBuildingQuery;
import org.eclipse.persistence.queries.ReadQuery;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.queries.SQLCall;
import org.eclipse.persistence.queries.StoredProcedureCall;
import org.eclipse.persistence.sequencing.Sequence;
import org.eclipse.persistence.sequencing.TableSequence;
import org.eclipse.persistence.sessions.SessionProfiler;
import org.eclipse.persistence.tools.schemaframework.DDLPlatform;
import org.eclipse.persistence.tools.schemaframework.FieldDefinition;
import org.eclipse.persistence.tools.schemaframework.SequenceDefinition;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

/**
 * DatabasePlatform is private to EclipseLink. It encapsulates behavior specific to a database platform
 * (eg. Oracle, Sybase, DBase), and provides protocol for EclipseLink to access this behavior. The behavior categories
 * which require platform specific handling are SQL generation and sequence behavior. While database platform
 * currently provides sequence number retrieval behavior, this will move to a sequence manager (when it is
 * implemented).
 *
 * @since TOPLink/Java 1.0
 */
public class DatabasePlatform extends DatasourcePlatform implements DDLPlatform {

    /** Indicates that native SQL should be used for literal values instead of ODBC escape format
     Only used with Oracle, Sybase and DB2 */
    protected boolean usesNativeSQL;

    /** Indicates that binding will be used for BLOB data. NOTE: does not work well with ODBC. */
    protected boolean usesByteArrayBinding;

    /** Batch all write statements */
    protected boolean usesBatchWriting;

    /** Bind all arguments to any SQL statement. */
    protected Boolean shouldBindAllParameters;

    /** Bind all arguments to any SQL statement. */
    protected boolean shouldForceBindAllParameters;

    /** Bind some arguments to any SQL statement. */
    protected boolean shouldBindPartialParameters;

    /** Cache all prepared statements, this requires full parameter binding as well. */
    protected boolean shouldCacheAllStatements;

    /** The statement cache size for prepare parameterized statements. */
    protected int statementCacheSize;

    /** Can be used if the app expects upper case but the database is not return consistent case, i.e. different databases. */
    protected boolean shouldForceFieldNamesToUpperCase;

    /** Indicates (if true) to remove blanks characters from the right of CHAR strings. */
    protected boolean shouldTrimStrings;

    /** Indicates that streams will be used to store BLOB data. NOTE: does not work with ODBC */
    protected boolean usesStreamsForBinding;

    /** Indicates the size above which strings will be bound NOTE: does not work with ODBC */
    protected int stringBindingSize;

    /** Indicates that strings will above the stringBindingSize will be bound NOTE: does not work with ODBC */
    protected boolean usesStringBinding;

    /** Allow for the batch size to be set as many database have strict limits. **/
    protected int maxBatchWritingSize;

    /** used for casting of input parameters in certain DBs **/
    protected int castSizeForVarcharParameter;

    /** Allow for our batch writing support to be used in JDK 1.2. **/
    protected boolean usesJDBCBatchWriting;

    /** bug 4241441: Allow custom batch writing to enable batching with optimistic locking. **/
    protected boolean usesNativeBatchWriting;

    /** Allow for a custom batch writing mechanism. **/
    protected BatchWritingMechanism batchWritingMechanism;

    /** Allow configuration option to use Where clause outer joining or From clause joining. **/
    protected Boolean printOuterJoinInWhereClause;

    /** Allow configuration option to use Where clause joining or From clause joining. **/
    protected Boolean printInnerJoinInWhereClause;

    /** Allow for the code that is used for preparing cursored outs for a storedprocedure to be settable. **/
    protected int cursorCode;

    /** The transaction isolation level to be set on the connection (optional). */
    protected int transactionIsolation;

    /** Some JDBC drivers do not support AutoCommit in the way EclipseLink expects.  (e.g. Attunity Connect, JConnect) */
    protected boolean supportsAutoCommit;

    /**
     * Allow for driver level data conversion optimization to be disabled,
     * required because some drivers can loose precision.
     */
    protected boolean shouldOptimizeDataConversion;

    /** Allow for case in field names to be ignored as some databases are not case sensitive and when using custom this can be an issue. */
    public static boolean shouldIgnoreCaseOnFieldComparisons = false;


    /** Bug#3214927 The default is 32000 for DynamicSQLBatchWritingMechanism.
     * It would become 100 when switched to ParameterizedSQLBatchWritingMechanism.
     */
    public static final int DEFAULT_MAX_BATCH_WRITING_SIZE = 32000;
    public static final int DEFAULT_PARAMETERIZED_MAX_BATCH_WRITING_SIZE = 100;

    /** Timeout used is isValid() check for dead connections. */
    public static final int IS_VALID_TIMEOUT = 0;

    /** This attribute will store the SQL query that will be used to 'ping' the database
     * connection in order to check the health of a connection.
     */
    protected String pingSQL;

    /** The following two maps, provide two ways of looking up StructConverters.
     * They can be looked up by java Class or by Struct type
     */
    protected Map<String, StructConverter> structConverters = null;
    protected Map<Class<?>, StructConverter> typeConverters = null;

    /**
     * Some platforms allow a query's maxRows and FirstResult settings to be
     * specified in SQL.  This setting allows it to be enabled/disabled
     */
    protected boolean useRownumFiltering = true;

    /**
     * Allow platform specific cast to be enabled.
     */
    protected boolean isCastRequired = false;

    /**
     * Allow user to require literals to be bound.
     */
    protected boolean shouldBindLiterals = true;


    /**
     * Used to integrate with data partitioning in an external DataSource such as UCP.
     */
    protected DataPartitioningCallback partitioningCallback;

    protected Boolean useJDBCStoredProcedureSyntax;
    protected String driverName;

    // DatabaseJsonPlatform has lazy initialization
    /** JSON support for ResultSet data retrieval. */
    private transient volatile DatabaseJsonPlatform jsonPlatform;

    /** This attribute will store the results from the batch execution */
    private int[] executeBatchRowCounts;
    /**
     * Creates an instance of default database platform.
     */
    public DatabasePlatform() {
        this.tableQualifier = "";
        this.usesNativeSQL = false;
        this.usesByteArrayBinding = true;
        this.usesStringBinding = false;
        this.stringBindingSize = 255;
        this.shouldTrimStrings = true;
        this.shouldBindAllParameters = null;
        this.shouldForceBindAllParameters = false;
        this.shouldBindPartialParameters = false;
        this.shouldCacheAllStatements = false;
        this.shouldOptimizeDataConversion = true;
        this.statementCacheSize = 50;
        this.shouldForceFieldNamesToUpperCase = false;
        this.maxBatchWritingSize = 0;
        this.usesJDBCBatchWriting = true;
        this.transactionIsolation = -1;
        this.cursorCode = -10;
        this.supportsAutoCommit = true;
        this.usesNativeBatchWriting = false;
        this.castSizeForVarcharParameter = 32672;
        this.startDelimiter = "\"";
        this.endDelimiter = "\"";
        this.useJDBCStoredProcedureSyntax = null;
        this.storedProcedureTerminationToken = ";";
        this.jsonPlatform = null;
        this.executeBatchRowCounts = new int[0];
    }

    /**
     * Initialize operators to avoid concurrency issues.
     */
    @Override
    public void initialize() {
        getPlatformOperators();
    }

    /**
     * Check if has callback.
     * Used to integrate with data partitioning in an external DataSource such as UCP.
     */
    public boolean hasPartitioningCallback() {
        return this.partitioningCallback != null;
    }

    /**
     * Return callback.
     * Used to integrate with data partitioning in an external DataSource such as UCP.
     */
    public DataPartitioningCallback getPartitioningCallback() {
        return partitioningCallback;
    }

    /**
     * Set callback.
     * Used to integrate with data partitioning in an external DataSource such as UCP.
     */
    public void setPartitioningCallback(DataPartitioningCallback partitioningCallback) {
        this.partitioningCallback = partitioningCallback;
    }

    /**
     * Return if casting is enabled for platforms that support it.
     * Allow platform specific cast to be disabled.
     */
    public boolean isCastRequired() {
        return isCastRequired;
    }

    /**
     * Set if casting is enabled for platforms that support it.
     * Allow platform specific cast to be disabled.
     */
    public void setIsCastRequired(boolean isCastRequired) {
        this.isCastRequired = isCastRequired;
    }

    /**
     * INTERNAL:
     * Get the map of StructConverters that will be used to preprocess
     * STRUCT data as it is read
     */
    public Map<String, StructConverter> getStructConverters() {
        return this.structConverters;
    }

    /**
     * INTERNAL:
     * Get the map of TypeConverters
     * This map indexes StructConverters by the Java Class they are meant to
     * convert
     */
    public Map<Class<?>, StructConverter> getTypeConverters() {
        if (typeConverters == null){
            typeConverters = new HashMap<>();
        }
        return this.typeConverters;
    }

    /**
     * Add a StructConverter to this DatabasePlatform
     * This StructConverter will be invoked for all writes to the database for the class returned
     * by its getJavaType() method and for all reads from the database for the Structs described
     * by its getStructName() method
     */
    public void addStructConverter(StructConverter converter) {
        if (structConverters == null){
            structConverters = new HashMap<>();
        }
        if (typeConverters == null){
            typeConverters = new HashMap<>();
        }
        structConverters.put(converter.getStructName(), converter);
        typeConverters.put(converter.getJavaType(), converter);
    }

    /**
     * INTERNAL: This gets called on each iteration to add parameters to the batch
     * Needs to be implemented so that it returns the number of rows successfully modified
     * by this statement for optimistic locking purposes (if useNativeBatchWriting is enabled, and
     * the call uses optimistic locking).  Is used with parameterized SQL
     *
     * @return - number of rows modified/deleted by this statement if it was executed (0 if it wasn't)
     */
    public int addBatch(PreparedStatement statement) throws java.sql.SQLException {
        statement.addBatch();
        return 0;
    }

    /**
     * Used by JDBC drivers that do not support autocommit so simulate an autocommit.
     */
    public void autoCommit(DatabaseAccessor accessor) throws SQLException {
        if (!supportsAutoCommit()) {
            accessor.getConnection().commit();
        }
    }

    /**
     * Used for jdbc drivers which do not support autocommit to explicitly begin a transaction
     * This method is a no-op for databases which implement autocommit as expected.
     */
    public void beginTransaction(DatabaseAccessor accessor) throws SQLException {
        if (!supportsAutoCommit()) {
            try (Statement statement = accessor.getConnection().createStatement()) {
                statement.executeUpdate("BEGIN TRANSACTION");
            }
        }
    }

    /**
     * INTERNAL:
     * Return the selection criteria used to IN batch fetching.
     */
    public Expression buildBatchCriteria(ExpressionBuilder builder,Expression field) {

        return field.in(
                builder.getParameter(ForeignReferenceMapping.QUERY_BATCH_PARAMETER));
    }

    /**
     * INTERNAL:
     * Return the selection criteria used to IN batch fetching.
     */
    public Expression buildBatchCriteriaForComplexId(ExpressionBuilder builder,List<Expression> fields) {
        return builder.value(fields).in(
                    builder.getParameter(ForeignReferenceMapping.QUERY_BATCH_PARAMETER));
    }

    /**
     * INTERNAL
     * Returns null unless the platform supports call with returning
     */
    public DatabaseCall buildCallWithReturning(SQLCall sqlCall, List<DatabaseField> returnFields) {
        throw ValidationException.platformDoesNotSupportCallWithReturning(getClass().getSimpleName());
    }

    /**
     * Returns true iff:
     * <ul>
     * <li>tThe current driver supports calling get/setNString</li>
     * <li> Strings are globally mapped to a national character varying type (useNationalCharacterVarying()).</li>
     * </ul>
     */
    public boolean shouldUseGetSetNString() {
        return getDriverSupportsNVarChar() && getUseNationalCharacterVaryingTypeForString();
    }

    /**
     * True if the current jdbc driver supports get/setNString methods
     */
    protected boolean driverSupportsNationalCharacterVarying = false;
    /**
     * If true, the platform should map String columns to a type that supports
     * national characters.
     */
    protected boolean useNationalCharacterVarying = false;

    public boolean getDriverSupportsNVarChar() {
        return driverSupportsNationalCharacterVarying;
    }

    public void setDriverSupportsNVarChar(boolean b) {
        driverSupportsNationalCharacterVarying = b;
    }

    public boolean getUseNationalCharacterVaryingTypeForString() {
        return useNationalCharacterVarying;
    }

    public void setUseNationalCharacterVaryingTypeForString(boolean b) {
        useNationalCharacterVarying = b;
    }

    /**
     * Return the proc syntax for this platform.
     */
    public String buildProcedureCallString(StoredProcedureCall call, AbstractSession session, AbstractRecord row) {
        StringWriter writer = new StringWriter();
        writer.write(call.getCallHeader(this));
        writer.write(call.getProcedureName());
        if (requiresProcedureCallBrackets()) {
            writer.write("(");
        } else {
            writer.write(" ");
        }

        int indexFirst = call.getFirstParameterIndexForCallString();
        int size = call.getParameters().size();
        for (int index = indexFirst; index < size; index++) {
            String name = call.getProcedureArgumentNames().get(index);
            Object parameter = call.getParameters().get(index);
            ParameterType parameterType = call.getParameterTypes().get(index);
            // If the argument is optional and null, ignore it.
            if (!call.hasOptionalArguments() || !call.getOptionalArguments().contains(parameter) || (row.get(parameter) != null)) {

                writer.write(getProcedureArgument(name, parameter, parameterType, call, session));

                if (DatasourceCall.isOutputParameterType(parameterType)) {
                    if (requiresProcedureCallOuputToken()) {
                        writer.write(" ");
                        writer.write(getOutputProcedureToken());
                    }
                }
                if ((index + 1) < call.getParameters().size()) {
                    writer.write(", ");
                }
            }
        }

        if (requiresProcedureCallBrackets()) {
            writer.write(")");
        }
        writer.write(getProcedureCallTail());

        return writer.toString();
    }

    /**
     * INTERNAL
     * Indicates whether the platform can build call with returning.
     * In case this method returns true, buildCallWithReturning method
     * may be called.
     */
    public boolean canBuildCallWithReturning() {
        return false;
    }

    /**
     * INTERNAL:
     * Supports Batch Writing with Optimistic Locking.
     */
    public boolean canBatchWriteWithOptimisticLocking(DatabaseCall call) {
        if (this.batchWritingMechanism != null) {
            // Assume a custom batch mechanism can return a valid row count.
            return true;
        }
        // The JDBC spec supports this, so assume it is implemented correctly by default.
        return true;
    }

    /**
     * INTERNAL:
     * Use the JDBC maxResults and firstResultIndex setting to compute a value to use when
     * limiting the results of a query in SQL.  These limits tend to be used in two ways.
     * <p>
     * 1. MaxRows is the index of the last row to be returned (like JDBC maxResults)
     * 2. MaxRows is the number of rows to be returned
     * <p>
     * By default, we assume case 1 and simply return the value of maxResults.  Subclasses
     * may provide an override
     *
     * @see org.eclipse.persistence.platform.database.MySQLPlatform
     */
    public int computeMaxRowsForSQL(int firstResultIndex, int maxResults){
        return maxResults;
    }

    /**
     *  Used for jdbc drivers which do not support autocommit to explicitly commit a transaction
     *  This method is a no-op for databases which implement autocommit as expected.
     */
    public void commitTransaction(DatabaseAccessor accessor) throws SQLException {
        if (!supportsAutoCommit()) {
            accessor.getConnection().commit();
        }
    }

    /**
     * Any platform that supports VPD should implement this method.
     */
    public DatabaseQuery getVPDClearIdentifierQuery(String vpdIdentifier) {
        return null;
    }

    /**
     * Any platform that supports VPD should implement this method.
     */
    public DatabaseQuery getVPDSetIdentifierQuery(String vpdIdentifier) {
        return null;
    }

    /**
     * INTERNAL
     * We support more primitive than JDBC does so we must do conversion before printing or binding.
     */
    public Object convertToDatabaseType(Object value) {
        if (value == null) {
            return null;
        }
        if (value.getClass() == CoreClassConstants.UTILDATE) {
            return Helper.timestampFromDate((java.util.Date)value);
        } else if (value instanceof Character) {
            return ((Character)value).toString();
        } else if (value instanceof Calendar) {
            return Helper.timestampFromDate(((Calendar)value).getTime());
        } else if (value instanceof BigInteger) {
            return new BigDecimal((BigInteger)value);
        } else if (value instanceof char[]) {
            return new String((char[])value);
        } else if (value instanceof Character[]) {
            return convertObject(value, CoreClassConstants.STRING);
        } else if (value instanceof Byte[]) {
            return convertObject(value, CoreClassConstants.APBYTE);
        }
        return value;
    }

    /**
     * Copy the state into the new platform.
     */
    @Override
    public void copyInto(Platform platform) {
        super.copyInto(platform);
        if (!(platform instanceof DatabasePlatform databasePlatform)) {
            return;
        }
        databasePlatform.setShouldTrimStrings(shouldTrimStrings());
        databasePlatform.setUsesNativeSQL(usesNativeSQL());
        databasePlatform.setUsesByteArrayBinding(usesByteArrayBinding());
        databasePlatform.setUsesStringBinding(usesStringBinding());
        databasePlatform.shouldBindAllParameters = this.shouldBindAllParameters;
        databasePlatform.shouldForceBindAllParameters = this.shouldForceBindAllParameters;
        databasePlatform.shouldBindPartialParameters = this.shouldBindPartialParameters;
        databasePlatform.setShouldCacheAllStatements(shouldCacheAllStatements());
        databasePlatform.setStatementCacheSize(getStatementCacheSize());
        databasePlatform.setTransactionIsolation(getTransactionIsolation());
        databasePlatform.setBatchWritingMechanism(getBatchWritingMechanism());
        databasePlatform.setMaxBatchWritingSize(getMaxBatchWritingSize());
        databasePlatform.setShouldForceFieldNamesToUpperCase(shouldForceFieldNamesToUpperCase());
        databasePlatform.setShouldOptimizeDataConversion(shouldOptimizeDataConversion());
        databasePlatform.setStringBindingSize(getStringBindingSize());
        databasePlatform.setUsesBatchWriting(usesBatchWriting());
        databasePlatform.setUsesJDBCBatchWriting(usesJDBCBatchWriting());
        databasePlatform.setUsesNativeBatchWriting(usesNativeBatchWriting());
        databasePlatform.setUsesStreamsForBinding(usesStreamsForBinding());
        databasePlatform.shouldCreateIndicesOnForeignKeys = this.shouldCreateIndicesOnForeignKeys;
        databasePlatform.printOuterJoinInWhereClause = this.printOuterJoinInWhereClause;
        databasePlatform.printInnerJoinInWhereClause = this.printInnerJoinInWhereClause;
        //use the variable directly to avoid custom platform strings - only want to copy user set values.
        //specifically used for login platform detection
        databasePlatform.setTableCreationSuffix(this.tableCreationSuffix);
        databasePlatform.setIsCastRequired(isCastRequired());
    }

    /**
     * Return if the platform does not maintain the row count on batch executes
     * and requires an output parameter to maintain the row count.
     */
    public boolean isRowCountOutputParameterRequired() {
        return false;
    }

    /**
     * Used for batch writing for row count return.
     */
    public String getBatchRowCountDeclareString() {
        return "";
    }

    /**
     * Used for batch writing for row count return.
     */
    public String getBatchRowCountAssignString() {
        return "";
    }

    /**
     * Used for batch writing for row count return.
     */
    public String getBatchRowCountReturnString() {
        return "";
    }

    /**
     * INTERNAL:
     * This method is used to unwrap the oracle connection wrapped by
     * the application server.  EclipseLink needs this unwrapped connection for certain
     * Oracle Specific support. (ie TIMESTAMPTZ)
     * This is added as a workaround for bug 4565190
     */
    public Connection getConnection(AbstractSession session, Connection connection) {
        return connection;
    }

    /**
     * This method determines if any special processing needs to occur prior to writing a field.
     * <p>
     * It does things such as determining if a field must be bound and flagging the parameter as one
     * that must be bound.
     */
    @Override
    public Object getCustomModifyValueForCall(Call call, Object value, DatabaseField field, boolean shouldBind) {

        if (typeConverters != null){
            StructConverter converter = typeConverters.get(field.getType());

            if (converter != null) {
                Object bindValue = value;
                if (bindValue == null) {
                    bindValue = new ObjectRelationalDatabaseField(field);
                    ((ObjectRelationalDatabaseField)bindValue).setSqlType(java.sql.Types.STRUCT);
                    ((ObjectRelationalDatabaseField)bindValue).setSqlTypeName(converter.getStructName());
                }
                return new BindCallCustomParameter(bindValue);
            }
        }
        return super.getCustomModifyValueForCall(call, value, field, shouldBind);
    }

    /**
     * Used for stored function calls.
     */
    public String getAssignmentString() {
        return "= ";
    }

    /**
     * ADVANCED:
     * Get the maximum length allowed by the database for a Varchar Parameter
     * This is used by subclasses when writing SQL for parameters
     * @see DB2Platform
     */
    public int getCastSizeForVarcharParameter(){
        return castSizeForVarcharParameter;
    }

    /**
     * ADVANCED:
     * Return the code for preparing cursored output
     * parameters in a stored procedure
     */
    public int getCursorCode() {
        return cursorCode;
    }

    /**
     * Returns the table name used by TableSequence by default.
     */
    public String getDefaultSequenceTableName() {
        return "SEQUENCE";
    }

    /**
     * Used for stored function calls.
     */
    public String getFunctionCallHeader() {
        return getProcedureCallHeader() + "? " + getAssignmentString();
    }

    /**
     * Returns the JDBC outer join operator for SELECT statements.
     */
    public String getJDBCOuterJoinString() {
        return "{oj ";
    }

    /**
     * Return the JDBC type for the given database field to be passed to Statement.setNull
     */
    public int getJDBCTypeForSetNull(DatabaseField field) {
        return getJDBCType(field);
    }

    /**
     * Return the JDBC type for the given database field.
     */
    public int getJDBCType(DatabaseField field) {
        if (field != null) {
            // If the field has a specified JDBC type, use it,
            // otherwise compute the type from the Java class type.
            if (field.getSqlType() != DatabaseField.NULL_SQL_TYPE) {
                return field.getSqlType();
            } else {
                return getJDBCType(ConversionManager.getObjectClass(field.getType()));
            }
        } else {
            return getJDBCType((Class<?>)null);
        }
    }

    /**
     * Return the JDBC type for the Java type.
     */
    public int getJDBCType(Class<?> javaType) {
        if (javaType == null) {
            return Types.VARCHAR;// Best guess, sometimes we cannot determine type from mapping, this may fail on some drivers, other dont care what type it is.
        } else if (javaType == CoreClassConstants.STRING) {
            return Types.VARCHAR;
        } else if (javaType == CoreClassConstants.BIGDECIMAL) {
            return Types.DECIMAL;
        } else if (javaType == CoreClassConstants.BIGINTEGER) {
            return Types.BIGINT;
        } else if (javaType == CoreClassConstants.BOOLEAN) {
            return Types.BIT;
        } else if (javaType == CoreClassConstants.BYTE) {
            return Types.TINYINT;
        } else if (javaType == CoreClassConstants.CHAR) {
            return Types.CHAR;
        } else if (javaType == CoreClassConstants.DOUBLE) {
            return Types.DOUBLE;
        } else if (javaType == CoreClassConstants.FLOAT) {
            return Types.FLOAT;
        } else if (javaType == CoreClassConstants.INTEGER) {
            return Types.INTEGER;
        } else if (javaType == CoreClassConstants.LONG) {
            return Types.BIGINT;
        } else if (javaType == CoreClassConstants.NUMBER) {
            return Types.DECIMAL;
        } else if (javaType == CoreClassConstants.SHORT ) {
            return Types.SMALLINT;
        } else if (javaType == CoreClassConstants.CALENDAR ) {
            return Types.TIMESTAMP;
        } else if (javaType == CoreClassConstants.UTILDATE ) {//bug 5237080, return TIMESTAMP for java.util.Date as well
            return Types.TIMESTAMP;
        } else if (javaType == ClassConstants.TIME_INSTANT ) {
            return Types.TIMESTAMP;
        } else if (javaType == ClassConstants.TIME ||
            javaType == ClassConstants.TIME_LTIME) { //bug 546312
            return Types.TIME;
        } else if (javaType == ClassConstants.SQLDATE ||
            javaType == ClassConstants.TIME_LDATE) { //bug 546312
            return Types.DATE;
        } else if (javaType == ClassConstants.TIMESTAMP ||
            javaType == ClassConstants.TIME_LDATETIME) { //bug 546312
            return Types.TIMESTAMP;
        } else if(javaType == ClassConstants.TIME_OTIME) { //bug 546312
            return Types.TIME_WITH_TIMEZONE;
        } else if(javaType == ClassConstants.TIME_ODATETIME) { //bug 546312
            return Types.TIMESTAMP_WITH_TIMEZONE;
        } else if (javaType == ClassConstants.TIME_YEAR ) {
            return Types.INTEGER;
        }else if (javaType == CoreClassConstants.ABYTE) {
            return Types.LONGVARBINARY;
        } else if (javaType == CoreClassConstants.APBYTE) {
            return Types.LONGVARBINARY;
        } else if (javaType == ClassConstants.BLOB) {
            return Types.BLOB;
        } else if (javaType == ClassConstants.ACHAR) {
            return Types.LONGVARCHAR;
        } else if (javaType == CoreClassConstants.APCHAR) {
            return Types.LONGVARCHAR;
        } else if (javaType == ClassConstants.CLOB) {
            return Types.CLOB;
        } else {
            return Types.VARCHAR;// Best guess, sometimes we cannot determine type from mapping, this may fail on some drivers, other dont care what type it is.
        }
    }

    /**
     * INTERNAL:
     * Returns the type name corresponding to the jdbc type
     */
    public String getJdbcTypeName(int jdbcType) {
        return null;
    }

    /**
     * INTERNAL:
     * Returns the minimum time increment supported by the platform.
     */
    public long minimumTimeIncrement() {
        return 1;
    }

    /**
     * Allow for the max batch writing size to be set.
     * This allows for the batch size to be limited as most database have strict limits.
     * The size is in characters, the default is 32000 but the real value depends on the database configuration.
     */
    public int getMaxBatchWritingSize() {
        return maxBatchWritingSize;
    }

    /**
     * INTERNAL:
     * Get the object from the JDBC Result set.  Added to allow other platforms to
     * override.
     * @see "org.eclipse.persistence.platform.database.oracle.Oracle9Plaform"
     */
    public Object getObjectFromResultSet(ResultSet resultSet, int columnNumber, int type, AbstractSession session) throws java.sql.SQLException {
        Object objectFromResultSet = resultSet.getObject(columnNumber);
        if (objectFromResultSet != null){
            if(structConverters != null && type == Types.STRUCT){
                String structType = ((Struct)objectFromResultSet).getSQLTypeName();
                if (getStructConverters().containsKey(structType)) {
                    return getStructConverters().get(structType).convertToObject((Struct)objectFromResultSet);
                }
            } else if(type == Types.SQLXML) {
                String str = ((SQLXML) objectFromResultSet).getString();
                ((SQLXML) objectFromResultSet).free();
                return str;
            }
        }
        return objectFromResultSet;
    }

    /**
     * Used for determining if an SQL exception was communication based. This SQL should be
     * as efficient as possible and ensure a round trip to the database.
     */
    public String getPingSQL(){
        return pingSQL;
    }

    /**
     * Obtain the platform specific argument string
     */
    public String getProcedureArgument(String name, Object parameter, ParameterType parameterType, StoredProcedureCall call, AbstractSession session) {
        if (name != null && shouldPrintStoredProcedureArgumentNameInCall()) {
            return getProcedureArgumentString() + name + " = " + "?";
        }
        return "?";
    }

    /**
     * Used for sp calls.
     */
    public String getProcedureCallHeader() {
        return "EXECUTE PROCEDURE ";
    }

    /**
     * Used for sp calls.
     */
    public String getProcedureCallTail() {
        return "";
    }

    public String getQualifiedSequenceTableName() {
        if (getDefaultSequence().isTable()) {
            return getQualifiedName(((TableSequence)getDefaultSequence()).getTableName());
        } else {
            throw ValidationException.wrongSequenceType(getDefaultSequence().getClass().getSimpleName(), "getTableName");
        }
    }

    public String getQualifiedName(String name) {
        if (getTableQualifier().isEmpty()) {
            return name;
        } else {
            return getTableQualifier() + "." + name;
        }
    }

    /**
     * This syntax does no wait on the lock.
     * (i.e. In Oracle adding NOWAIT to the end will accomplish this)
     */
    public String getNoWaitString() {
        return " NOWAIT";
    }

    /**
     * This syntax does no wait on the lock.
     * (i.e. In Oracle adding FOR UPDATE NOWAIT to the end will accomplish this)
     */
    public String getSelectForUpdateNoWaitString() {
        return getSelectForUpdateString() + getNoWaitString();
    }

    /**
     * For fine-grained pessimistic locking the column names can be
     * specified individually.
     */
    public String getSelectForUpdateOfString() {
        return " FOR UPDATE OF ";
    }

    /**
     * Most database support a syntax. although don't actually lock the row.
     * Some require the OF some don't like it.
     */
    public String getSelectForUpdateString() {
        return " FOR UPDATE";
    }

    /**
     * Platforms that support the WAIT option should override this method.
     * By default the wait timeout is ignored.
     *
     *  @see DatabasePlatform#supportsWaitForUpdate()
     */
    public String getSelectForUpdateWaitString(Integer waitTimeout) {
        return getSelectForUpdateString();
    }

    public String getSequenceCounterFieldName() {
        if (getDefaultSequence().isTable()) {
            return ((TableSequence)getDefaultSequence()).getCounterFieldName();
        } else {
            throw ValidationException.wrongSequenceType(getDefaultSequence().getClass().getSimpleName(), "getCounterFieldName");
        }
    }

    public String getSequenceNameFieldName() {
        if (getDefaultSequence().isTable()) {
            return ((TableSequence)getDefaultSequence()).getNameFieldName();
        } else {
            throw ValidationException.wrongSequenceType(getDefaultSequence().getClass().getSimpleName(), "getNameFieldName");
        }
    }

    public String getSequenceTableName() {
        if (getDefaultSequence().isTable()) {
            String tableName = ((TableSequence)getDefaultSequence()).getTableName();
            if(tableName.isEmpty()) {
                tableName = this.getDefaultSequenceTableName();
            }
            return tableName;
        } else {
            throw ValidationException.wrongSequenceType(getDefaultSequence().getClass().getSimpleName(), "getTableName");
        }
    }

    /**
     * The statement cache size for prepare parameterized statements.
     */
    public int getStatementCacheSize() {
        return statementCacheSize;
    }

    public String getStoredProcedureParameterPrefix() {
        return "";
    }

    public int getStringBindingSize() {
        return stringBindingSize;
    }

    /**
     * Returns the transaction isolation setting for a connection.
     * Return -1 if it has not been set.
     */
    public int getTransactionIsolation() {
        return transactionIsolation;
    }

    /**
     * Some database require outer joins to be given in the where clause, others require it in the from clause.
     * Informix requires it in the from clause with no ON expression.
     */
    public boolean isInformixOuterJoin() {
        return false;
    }

    /**
     * Returns true if this platform complies with the expected behavior from
     * a jdbc execute call. Most platforms do, some have issues:
     *
     * @see PostgreSQLPlatform
     */
    public boolean isJDBCExecuteCompliant() {
        return true;
    }

    /**
     * Return true is the given exception occurred as a result of a lock
     * time out exception (WAIT clause). If sub-platform supports this clause,
     * this method should be necessary checks should be made.
     * <p>
     * By default though, this method return false.
     *
     * @see OraclePlatform
     */
    public boolean isLockTimeoutException(DatabaseException e) {
        return false;
    }

    /**
     * INTERNAL:
     * Indicates whether SELECT DISTINCT ... FOR UPDATE is allowed by the platform (Oracle doesn't allow this).
     */
    public boolean isForUpdateCompatibleWithDistinct() {
        return true;
    }

    /**
     * INTERNAL:
     * Indicates whether SELECT DISTINCT lob FROM ... (where lob is BLOB or CLOB) is allowed by the platform (Oracle doesn't allow this).
     */
    public boolean isLobCompatibleWithDistinct() {
        return true;
    }

    /**
     * Returns the attribute containing the results from the batch execution
     */
    public int[] getExecuteBatchRowCounts() {
        return executeBatchRowCounts;
    }

    /**
     * Sets the attribute containing the results from the batch execution
     */
    public void setExecuteBatchRowCounts(int[] rowCounts) {
        executeBatchRowCounts = rowCounts;
    }

    /**
     * Builds a table of maximum numeric values keyed on java class. This is used for type testing but
     * might also be useful to end users attempting to sanitize values.
     * <p><b>NOTE</b>: BigInteger &amp; BigDecimal maximums are dependent upon their precision &amp; Scale
     */
    public Map<Class<? extends Number>, ? super Number> maximumNumericValues() {
        Map<Class<? extends Number>, ? super Number> values = new HashMap<>();

        values.put(Integer.class, Integer.MAX_VALUE);
        values.put(Long.class, Long.MAX_VALUE);
        values.put(Double.class, Double.MAX_VALUE);
        values.put(Short.class, Short.MAX_VALUE);
        values.put(Byte.class, Byte.MAX_VALUE);
        values.put(Float.class, Float.MAX_VALUE);
        values.put(java.math.BigInteger.class, new java.math.BigInteger("999999999999999999999999999999999999999"));
        values.put(java.math.BigDecimal.class, new java.math.BigDecimal("99999999999999999999.9999999999999999999"));
        return values;
    }

    /**
     * Builds a table of minimum numeric values keyed on java class. This is used for type testing but
     * might also be useful to end users attempting to sanitize values.
     * <p><b>NOTE</b>: BigInteger &amp; BigDecimal minimums are dependent upon their precision &amp; Scale
     */
    public Map<Class<? extends Number>, ? super Number> minimumNumericValues() {
        Map<Class<? extends Number>, ? super Number> values = new HashMap<>();

        values.put(Integer.class, Integer.MIN_VALUE);
        values.put(Long.class, Long.MIN_VALUE);
        values.put(Double.class, Double.MIN_VALUE);
        values.put(Short.class, Short.MIN_VALUE);
        values.put(Byte.class, Byte.MIN_VALUE);
        values.put(Float.class, Float.MIN_VALUE);
        values.put(java.math.BigInteger.class, new java.math.BigInteger("-99999999999999999999999999999999999999"));
        values.put(java.math.BigDecimal.class, new java.math.BigDecimal("-9999999999999999999.9999999999999999999"));
        return values;
    }

    /**
     * Internal: Allows setting the batch size on the statement
     *  Is used with parameterized SQL, and should only be passed in prepared statements
     *
     * @return statement to be used for batch writing
     */
    public Statement prepareBatchStatement(Statement statement, int maxBatchWritingSize) throws java.sql.SQLException {
        return statement;
    }

    /**
     * Print the int array on the writer. Added to handle int[] passed as parameters to named queries
     * Returns the number of  objects using binding.
     */
    public int printValuelist(int[] theObjects, DatabaseCall call, Writer writer) throws IOException {
        int nBoundParameters = 0;
        writer.write("(");
        for (int i = 0; i < theObjects.length; i++) {
            nBoundParameters = nBoundParameters + appendParameterInternal(call, writer, theObjects[i]);
            if (i < (theObjects.length - 1)) {
                writer.write(", ");
            }
        }
        writer.write(")");
        return nBoundParameters;
    }

    public int printValuelist(Collection<?> theObjects, DatabaseCall call, Writer writer) throws IOException {
        int nBoundParameters = 0;
        writer.write("(");
        Iterator<?> iterator = theObjects.iterator();
        while (iterator.hasNext()) {
            nBoundParameters = nBoundParameters + appendParameterInternal(call, writer, iterator.next());
            if (iterator.hasNext()) {
                writer.write(", ");
            }
        }
        writer.write(")");
        return nBoundParameters;
    }

    /**
     * This method is used to register output parameter on CallableStatements for Stored Procedures
     * as each database seems to have a different method.
     *
     * @see java.sql.CallableStatement#registerOutParameter(int parameterIndex, int sqlType)
     */
    public void registerOutputParameter(CallableStatement statement, int parameterIndex, int sqlType) throws SQLException {
        statement.registerOutParameter(parameterIndex, sqlType);
    }

    /**
     * This method is used to register output parameter on CallableStatements for Stored Procedures
     * as each database seems to have a different method.
     *
     * @see java.sql.CallableStatement#registerOutParameter(int parameterIndex, int sqlType, String typeName)
     */
    public void registerOutputParameter(CallableStatement statement, int parameterIndex, int sqlType, String typeName) throws SQLException {
        statement.registerOutParameter(parameterIndex, sqlType, typeName);
    }

    /**
     * This method is used to register output parameter on CallableStatements for Stored Procedures
     * as each database seems to have a different method.
     *
     * @see java.sql.CallableStatement#registerOutParameter(String parameterName, int sqlType)
     */
    public void registerOutputParameter(CallableStatement statement, String parameterName, int sqlType) throws SQLException {
        statement.registerOutParameter(parameterName, sqlType);
    }

    /**
     * This method is used to register output parameter on CallableStatements for Stored Procedures
     * as each database seems to have a different method.
     *
     * @see java.sql.CallableStatement#registerOutParameter(String parameterName, int sqlType, String typeName)
     */
    public void registerOutputParameter(CallableStatement statement, String parameterName, int sqlType, String typeName) throws SQLException {
        statement.registerOutParameter(parameterName, sqlType, typeName);
    }

    /**
     * USed for sp calls.
     */
    public boolean requiresProcedureCallBrackets() {
        return true;
    }

    /**
     * Used for sp calls.  Sybase must print output after output params.
     */
    public boolean requiresProcedureCallOuputToken() {
        return false;
    }

    /**
     * INTERNAL:
     * Indicates whether the version of CallableStatement.registerOutputParameter method
     * that takes type name should be used.
     */
    public boolean requiresTypeNameToRegisterOutputParameter() {
        return false;
    }

    /**
     * INTERNAL:
     * Used by Exists queries because they just need to select a single row.
     * In most databases, we will select one of the primary key fields.
     * <p>
     * On databases where, for some reason we cannot select one of the key fields
     * this method can be overridden
     * @see SymfowarePlatform
     */
    public void retrieveFirstPrimaryKeyOrOne(ReportQuery subselect){
        subselect.setShouldRetrieveFirstPrimaryKey(true);
    }
    /**
     *  Used for jdbc drivers which do not support autocommit to explicitly rollback a transaction
     *  This method is a no-op for databases which implement autocommit as expected.
     */
    public void rollbackTransaction(DatabaseAccessor accessor) throws SQLException {
        if (!supportsAutoCommit()) {
            accessor.getConnection().rollback();
        }
    }

    /**
     * ADVANCED:
     * Set the maximum length allowed by the database for a Varchar Parameter
     * This is used by subclasses when writing SQL for parameters
     * @see DB2Platform
     */
    public void setCastSizeForVarcharParameter(int maxLength){
        castSizeForVarcharParameter = maxLength;
    }

    /**
     * ADVANCED:
     * Set the code for preparing cursored output
     * parameters in a stored procedure
     */
    public void setCursorCode(int cursorCode) {
        this.cursorCode = cursorCode;
    }

    /**
     * During auto-detect, the driver name is set on the platform.
     */
    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    /**
     * Allow for the max batch writing size to be set.
     * This allows for the batch size to be limited as most database have strict limits.
     * The size is in characters, the default is 32000 but the real value depends on the database configuration.
     */
    public void setMaxBatchWritingSize(int maxBatchWritingSize) {
        this.maxBatchWritingSize = maxBatchWritingSize;
    }

    public void setSequenceCounterFieldName(String name) {
        if (getDefaultSequence().isTable()) {
            ((TableSequence)getDefaultSequence()).setCounterFieldName(name);
        } else {
            if (!name.equals((new TableSequence()).getCounterFieldName())) {
                throw ValidationException.wrongSequenceType(getDefaultSequence().getClass().getSimpleName(), "setCounterFieldName");
            }
        }
    }

    public void setSequenceNameFieldName(String name) {
        if (getDefaultSequence().isTable()) {
            ((TableSequence)getDefaultSequence()).setNameFieldName(name);
        } else {
            if (!name.equals((new TableSequence()).getNameFieldName())) {
                throw ValidationException.wrongSequenceType(getDefaultSequence().getClass().getSimpleName(), "setNameFieldName");
            }
        }
    }

    public void setSequenceTableName(String name) {
        if (getDefaultSequence().isTable()) {
            ((TableSequence)getDefaultSequence()).setTableName(name);
        } else {
            if (!name.equals((new TableSequence()).getTableName())) {
                throw ValidationException.wrongSequenceType(getDefaultSequence().getClass().getSimpleName(), "setTableName");
            }
        }
    }

    /**
     * Bind all arguments to any SQL statement.
     */
    public void setShouldBindAllParameters(boolean shouldBindAllParameters) {
        this.shouldBindAllParameters = shouldBindAllParameters;
    }

    /**
     * Cache all prepared statements, this requires full parameter binding as well.
     */
    public void setShouldCacheAllStatements(boolean shouldCacheAllStatements) {
        this.shouldCacheAllStatements = shouldCacheAllStatements;
    }

    /**
     * Used to enable parameter binding and override the platform default
     */
    public void setShouldForceBindAllParameters(boolean shouldForceBindAllParameters) {
        this.shouldForceBindAllParameters = shouldForceBindAllParameters;
    }

    /**
     * Used to enable parameter binding and override the platform default
     */
    public void setShouldBindPartialParameters(boolean shouldBindPartialParameters) {
        this.shouldBindPartialParameters = shouldBindPartialParameters;
    }

    /**
     * Can be used if the app expects upper case but the database is not return consistent case, i.e. different databases.
     */
    public void setShouldForceFieldNamesToUpperCase(boolean shouldForceFieldNamesToUpperCase) {
        this.shouldForceFieldNamesToUpperCase = shouldForceFieldNamesToUpperCase;
    }

    /**
     * Allow for case in field names to be ignored as some databases are not case sensitive and when using custom this can be an issue.
     */
    public static void setShouldIgnoreCaseOnFieldComparisons(boolean newShouldIgnoreCaseOnFieldComparisons) {
        shouldIgnoreCaseOnFieldComparisons = newShouldIgnoreCaseOnFieldComparisons;
    }

    /**
     * Set if our driver level data conversion optimization is enabled.
     * This can be disabled as some drivers perform data conversion themselves incorrectly.
     */
    public void setShouldOptimizeDataConversion(boolean value) {
        this.shouldOptimizeDataConversion = value;
    }

    public void setShouldTrimStrings(boolean aBoolean) {
        shouldTrimStrings = aBoolean;
    }

    /**
     * The statement cache size for prepare parameterized statements.
     */
    public void setStatementCacheSize(int statementCacheSize) {
        this.statementCacheSize = statementCacheSize;
    }

    public void setStringBindingSize(int aSize) {
        stringBindingSize = aSize;
    }

    /**
     * supportsAutoCommit can be set to false for JDBC drivers which do not support autocommit.
     */
    public void setSupportsAutoCommit(boolean supportsAutoCommit) {
        this.supportsAutoCommit = supportsAutoCommit;
    }

    /**
     * Get the String used on all table creation statements generated from the DefaultTableGenerator
     * with a session using this project (DDL generation).  This value will be appended to CreationSuffix strings
     * stored on the DatabaseTable or TableDefinition.
     * ie setTableCreationSuffix("engine=InnoDB");
     */
    public void setTableCreationSuffix(String tableCreationSuffix){
        this.tableCreationSuffix = tableCreationSuffix;
    }

    /**
     * Set the transaction isolation setting for a connection.
     */
    public void setTransactionIsolation(int isolationLevel) {
        transactionIsolation = isolationLevel;
    }

    /**
     * Return true if JDBC syntax should be used for stored procedure calls.
     */
    public void setUseJDBCStoredProcedureSyntax(Boolean useJDBCStoredProcedureSyntax) {
        this.useJDBCStoredProcedureSyntax = useJDBCStoredProcedureSyntax;
    }

    public void setUsesBatchWriting(boolean usesBatchWriting) {
        this.usesBatchWriting = usesBatchWriting;
    }

    public void setUsesByteArrayBinding(boolean usesByteArrayBinding) {
        this.usesByteArrayBinding = usesByteArrayBinding;
    }

    /**
     * Some JDBC 2 drivers to not support batching, so this lets are own batching be used.
     */
    public void setUsesJDBCBatchWriting(boolean usesJDBCBatchWriting) {
        this.usesJDBCBatchWriting = usesJDBCBatchWriting;
    }

    /**
     * Advanced:
     * This is used to enable native batch writing on drivers that support it.  Enabling
     * Native batchwriting will result in the batch writing mechanisms to be used on objects
     * that have optimistic locking, and so execution of statements on these objects will be
     * delayed until the batch statement is executed.  Only use this method with platforms that
     * have overridden the prepareBatchStatement, addBatch and executeBatch as required
     * <p>
     * Current support is limited to the Oracle9Platform class.
     *
     * @param usesNativeBatchWriting - flag to turn on/off native batch writing
     */
    public void setUsesNativeBatchWriting(boolean usesNativeBatchWriting){
        this.usesNativeBatchWriting = usesNativeBatchWriting;
    }

    public void setUsesNativeSQL(boolean usesNativeSQL) {
        this.usesNativeSQL = usesNativeSQL;
    }

    /**
     * Return the custom batch writing mechanism.
     */
    public BatchWritingMechanism getBatchWritingMechanism() {
        return batchWritingMechanism;
    }

    /**
     * Set the custom batch writing mechanism.
     */
    public void setBatchWritingMechanism(BatchWritingMechanism batchWritingMechanism) {
        this.batchWritingMechanism = batchWritingMechanism;
    }

    /**
     * Set if SQL-Level pagination should be used for FirstResult and MaxRows settings.
     * Default is true.
     * <p>
     * Note: This setting is used to disable SQL-level pagination on platforms for which it is
     * implemented.  On platforms where we use JDBC for pagination, it will be ignored
     */
    public void setShouldUseRownumFiltering(boolean useRownumFiltering) {
        this.useRownumFiltering = useRownumFiltering;
    }

    public void setUsesStreamsForBinding(boolean usesStreamsForBinding) {
        this.usesStreamsForBinding = usesStreamsForBinding;
    }

    /**
     * Changes the way that OuterJoins are done on the database.  With a value of
     * true, outerjoins are performed in the where clause using the outer join token
     * for that database.
     * <p>
     *  With the value of false, outerjoins are performed in the from clause.
     */
    public void setPrintOuterJoinInWhereClause(boolean printOuterJoinInWhereClause) {
        this.printOuterJoinInWhereClause = printOuterJoinInWhereClause;
    }

    /**
     * Changes the way that inner joins are printed in generated SQL for the database.
     * With a value of true, inner joins are printed in the WHERE clause,
     * if false, inner joins are printed in the FROM clause.
     */
    public void setPrintInnerJoinInWhereClause(boolean printInnerJoinInWhereClause) {
        this.printInnerJoinInWhereClause = printInnerJoinInWhereClause;
    }

    public void setUsesStringBinding(boolean aBool) {
        usesStringBinding = aBool;
    }

    /**
     * Bind all arguments to any SQL statement.
     */
    public boolean shouldBindAllParameters() {
        // Non-null value implies it has been overridden
        if(this.shouldBindAllParameters != null) {
            return this.shouldBindAllParameters;
        }
        // Default value
        return true;
    }

    /**
     * Used to determine if the platform should perform partial parameter binding or not
     * <p>
     * Off by default. Only platforms with the support added should enable this configuration.
     */
    public boolean shouldBindPartialParameters() {
        return false;
    }

    /**
     * Cache all prepared statements, this requires full parameter binding as well.
     */
    public boolean shouldCacheAllStatements() {
        return shouldCacheAllStatements;
    }

    /**
     * Used for table creation. Most databases do not create an index automatically for
     * foreign key columns.  Normally it is recommended to index foreign key columns.
     * This allows for foreign key indexes to be configured, by default foreign keys are not indexed.
     */
    public void setShouldCreateIndicesOnForeignKeys(boolean shouldCreateIndicesOnForeignKeys) {
        this.shouldCreateIndicesOnForeignKeys = shouldCreateIndicesOnForeignKeys;
    }

    /**
     * Used to enable parameter binding and override platform default
     */
    public boolean shouldForceBindAllParameters() {
        return this.shouldForceBindAllParameters;
    }

    /**
     * Can be used if the app expects upper case but the database is not return consistent case, i.e. different databases.
     */
    public boolean shouldForceFieldNamesToUpperCase() {
        return shouldForceFieldNamesToUpperCase;
    }

    /**
     * Allow for case in field names to be ignored as some databases are not case sensitive and when using custom this can be an issue.
     */
    public static boolean shouldIgnoreCaseOnFieldComparisons() {
        return shouldIgnoreCaseOnFieldComparisons;
    }

    /**
     * Allow for the platform to ignore exceptions.
     * This is required for DB2 which throws no-data modified as an exception.
     */
    public boolean shouldIgnoreException(SQLException exception) {
        // By default nothing is ignored.
        return false;
    }

    /**
     * Return if our driver level data conversion optimization is enabled.
     * This can be disabled as some drivers perform data conversion themselves incorrectly.
     */
    public boolean shouldOptimizeDataConversion() {
        return shouldOptimizeDataConversion;
    }

    /**
     * This is required in the construction of the stored procedures with
     * output parameters
     */
    public boolean shouldPrintInOutputTokenBeforeType() {
        return true;
    }

    /**
     * Some database require outer joins to be given in the where clause, others require it in the from clause.
     */
    public boolean shouldPrintOuterJoinInWhereClause() {
        if(this.printOuterJoinInWhereClause == null) {
            this.printOuterJoinInWhereClause = Boolean.FALSE;
        }
        return this.printOuterJoinInWhereClause;
    }

    /**
     * This allows which clause inner joins are printed into in SQL generation.
     * By default most platforms put inner joins in the WHERE clause.
     * If set to false, inner joins will be printed in the FROM clause.
     */
    public boolean shouldPrintInnerJoinInWhereClause(ReadQuery query) {
        Boolean printInnerJoinInWhereClauseQueryHint = ((query != null) && (query instanceof ObjectBuildingQuery)) ? ((ObjectBuildingQuery)query).printInnerJoinInWhereClause() : null;
        if (printInnerJoinInWhereClauseQueryHint != null) {
            return printInnerJoinInWhereClauseQueryHint;
        } else {
            if (this.printInnerJoinInWhereClause == null) {
                return true;
            }
            return this.printInnerJoinInWhereClause;
        }
    }

    /**
     * INTERNAL:
     * Should the variable name of a stored procedure call be printed as part of the procedure call
     * e.g. EXECUTE PROCEDURE MyStoredProc(myvariable = ?)
     */
    public boolean shouldPrintStoredProcedureArgumentNameInCall(){
        return true;
    }

    public boolean shouldPrintForUpdateClause() {
        return true;
    }

    public boolean shouldTrimStrings() {
        return shouldTrimStrings;
    }

    @Override
    public boolean shouldUseCustomModifyForCall(DatabaseField field) {
        return (field.getSqlType() == Types.STRUCT &&
            (typeConverters != null && typeConverters.containsKey(field.getType()))) ||
            super.shouldUseCustomModifyForCall(field);
    }

    /**
     * JDBC defines and outer join syntax, many drivers do not support this. So we normally avoid it.
     */
    public boolean shouldUseJDBCOuterJoinSyntax() {
        return true;
    }

    /**
     * Return if Oracle ROWNUM pagination should be used for FirstResult and MaxRows settings.
     * Default is true.
     * <p>
     * Note: This setting is used to disable SQL-level pagination on platforms for which it is
     * implemented.  On platforms where we use JDBC for pagination, it will be ignored
     */
    public boolean shouldUseRownumFiltering() {
        return this.useRownumFiltering;
    }

    /**
     * Indicates whether the ANSI syntax for inner joins (e.g. SELECT FROM t1
     * JOIN t2 ON t1.pk = t2.fk) is supported by this platform.
     */
    public boolean supportsANSIInnerJoinSyntax() {
        return true;
    }

    /**
     * supportsAutoCommit must sometimes be set to false for JDBC drivers which do not
     * support autocommit.  Used to determine how to handle transactions properly.
     */
    public boolean supportsAutoCommit() {
        return supportsAutoCommit;
    }

    /**
     * Some db allow VARCHAR db field to be used in arithmetic operations automatically converting them to numeric:
     * UPDATE OL_PHONE SET PHONE_ORDER_VARCHAR = (PHONE_ORDER_VARCHAR + 1) WHERE ...
     * SELECT ... WHERE  ... t0.MANAGED_ORDER_VARCHAR BETWEEN 1 AND 4 ...
     */
    public boolean supportsAutoConversionToNumericForArithmeticOperations() {
        return false;
    }

    /**
     * By default, platforms do not support VPD. Those that do need to override
     * this method.
     */
    public boolean supportsVPD() {
        return false;
    }

    /**
     *  INTERNAL:
     *  Indicates whether the platform supports timeouts on For Update
     *
     *  @see DatabasePlatform#getSelectForUpdateWaitString(Integer waitTimeout)
     */
    public boolean supportsWaitForUpdate() {
        return false;
    }

    /**
     * Used to determine if the platform supports untyped parameters, as ordinal variables, within the Order By clause
     * <p>
     * On by default. Only platforms without support added should disable this configuration.
     */
    public boolean supportsOrderByParameters() {
        return true;
    }

    /**
     * Internal: This gets called on each batch statement execution
     * Needs to be implemented so that it returns the number of rows successfully modified
     * by this statement for optimistic locking purposes.
     *
     * @param isStatementPrepared - flag is set to true if this statement is prepared
     * @return - number of rows modified/deleted by this statement
     */
    public int executeBatch(Statement statement, boolean isStatementPrepared) throws java.sql.SQLException {
       int[] rowCounts = statement.executeBatch();
       setExecuteBatchRowCounts(rowCounts);
       int rowCount = 0;
       // Otherwise check if the row counts were returned.
       for (int count : rowCounts) {
           if (count > 0) {
               // The row count will be matched with the statement count.
               rowCount ++;
           } else {
               // The row counts were not known, check for a total row count.
               // If the total count is not known, then the update should fail,
               // and the platform must override canBatchWriteWithOptimisticLocking() to return false.
               return statement.getUpdateCount();
           }
       }
       return rowCount;
    }

    /**
     * because each platform has different requirements for accessing stored procedures and
     * the way that we can combine resultsets and output params, the stored procedure call
     * is being executed on the platform.
     */
    public Object executeStoredProcedure(DatabaseCall dbCall, PreparedStatement statement, DatabaseAccessor accessor, AbstractSession session) throws SQLException {
        Object result = null;
        ResultSet resultSet = null;
        if (!dbCall.getReturnsResultSet()) {// no result set is expected
            if (dbCall.isCursorOutputProcedure()) {
                result = accessor.executeNoSelect(dbCall, statement, session);
                int index = dbCall.getCursorOutIndex();
                resultSet = (ResultSet)dbCall.getOutputParameterValue((CallableStatement)statement, index - 1, session);
            } else {
                accessor.executeDirectNoSelect(statement, dbCall, session);

                // Meaning we have at least one out parameter (or out cursors).
                if (dbCall.shouldBuildOutputRow() || dbCall.hasOutputCursors()) {
                    result = accessor.buildOutputRow((CallableStatement)statement, dbCall, session);

                    // ReadAllQuery may be returning just output params, or they
                    // may be executing a DataReadQuery, which also assumes a vector
                    if (dbCall.areManyRowsReturned()) {
                        Vector tempResult = new Vector();
                        tempResult.add(result);
                        result = tempResult;
                    }
                } else {
                    // No out params whatsover, return an empty list.
                    result = new Vector();
                }
            }
        } else {
            // so specifically in Sybase JConnect 5.5 we must create the result vector before accessing the
            // output params in the case where the user is returning both.  this is a driver limitation
            resultSet = accessor.executeSelect(dbCall, statement, session);
        }

        if (resultSet != null) {
            dbCall.matchFieldOrder(resultSet, accessor, session);

            if (dbCall.isCursorReturned()) {
                dbCall.setStatement(statement);
                dbCall.setResult(resultSet);
                return dbCall;
            }

            result = accessor.processResultSet(resultSet, dbCall, statement, session);
        }

        // If the output is not allowed with the result set, we must hold off till the result set has
        // been processed before accessing the out parameters.
        if (dbCall.shouldBuildOutputRow() && ! isOutputAllowWithResultSet()) {
            AbstractRecord outputRow = accessor.buildOutputRow((CallableStatement)statement, dbCall, session);
            dbCall.getQuery().setProperty("output", outputRow);
            session.getEventManager().outputParametersDetected(outputRow, dbCall);
        }

        return result;
    }

    /**
     * Used for determining if an SQL exception was communication based. This SQL should be
     * as efficient as possible and ensure a round trip to the database.
     */
    public void setPingSQL(String pingSQL) {
        this.pingSQL = pingSQL;
    }

    // Following methods add parameters into PreparedStatement. They are being called when
    // eclipselink.jdbc.bind-parameters PU property is set to true.

    /**
     * INTERNAL
     * Set the parameter in the JDBC statement at the given index.
     * This support a wide range of different parameter types,
     * and is heavily optimized for common types.
     */
    public void setParameterValueInDatabaseCall(Object parameter,
                PreparedStatement statement, int index, AbstractSession session)
                throws SQLException {
        // Process common types first.
        if (parameter instanceof String) {
            // Check for stream binding of large strings.
            if (usesStringBinding() && (((String)parameter).length() > getStringBindingSize())) {
                CharArrayReader reader = new CharArrayReader(((String)parameter).toCharArray());
                statement.setCharacterStream(index, reader, ((String)parameter).length());
            } else {
                if (shouldUseGetSetNString()) {
                    statement.setNString(index, (String) parameter);
                } else {
                    statement.setString(index, (String) parameter);
                }
            }
        } else if (parameter instanceof Number number) {
            if (number instanceof Integer) {
                statement.setInt(index, number.intValue());
            } else if (number instanceof Long) {
                statement.setLong(index, number.longValue());
            }  else if (number instanceof BigDecimal) {
                statement.setBigDecimal(index, (BigDecimal) number);
            } else if (number instanceof Double) {
                statement.setDouble(index, number.doubleValue());
            } else if (number instanceof Float) {
                statement.setFloat(index, number.floatValue());
            } else if (number instanceof Short) {
                statement.setShort(index, number.shortValue());
            } else if (number instanceof Byte) {
                statement.setByte(index, number.byteValue());
            } else if (number instanceof BigInteger) {
                // Convert to BigDecimal.
                statement.setBigDecimal(index, new BigDecimal((BigInteger) number));
            } else {
                statement.setObject(index, parameter);
            }
        }  else if (parameter instanceof java.sql.Date){
            statement.setDate(index,(java.sql.Date)parameter);
        }  else if (parameter instanceof java.time.LocalDate){
            statement.setDate(index, java.sql.Date.valueOf((java.time.LocalDate) parameter));
        } else if (parameter instanceof java.sql.Timestamp){
            statement.setTimestamp(index,(java.sql.Timestamp)parameter);
        } else if (parameter instanceof java.time.Instant){
            statement.setTimestamp(index, java.sql.Timestamp.from((java.time.Instant)parameter));
        } else if (parameter instanceof java.time.LocalDateTime){
            statement.setTimestamp(index, java.sql.Timestamp.valueOf((java.time.LocalDateTime) parameter));
        } else if (parameter instanceof java.time.OffsetDateTime) {
            statement.setTimestamp(index, java.sql.Timestamp.from(((java.time.OffsetDateTime) parameter).toInstant()));
        } else if (parameter instanceof java.time.Year) {
            statement.setInt(index, ((java.time.Year)parameter).getValue());
        } else if (parameter instanceof java.sql.Time){
            statement.setTime(index,(java.sql.Time)parameter);
        } else if (parameter instanceof java.time.LocalTime lt){
            java.sql.Timestamp ts = java.sql.Timestamp.valueOf(java.time.LocalDateTime.of(java.time.LocalDate.ofEpochDay(0), lt));
            // This may cause cast exceptions, statement.setTime(index, ...) should be here, but some platforms rely on full TIMESTAMP types
            // overriden to statement.setTime(index, ...) in: SQLServerPlatform
            statement.setTimestamp(index, ts);
        } else if (parameter instanceof java.time.OffsetTime ot) {
            java.sql.Timestamp ts = java.sql.Timestamp.valueOf(java.time.LocalDateTime.of(java.time.LocalDate.ofEpochDay(0), ot.toLocalTime()));
            statement.setTimestamp(index, ts);
        } else if (parameter instanceof Boolean) {
            statement.setBoolean(index, (Boolean) parameter);
        } else if (parameter == null) {
            // Normally null is passed as a DatabaseField so the type is included, but in some case may be passed directly.
            statement.setNull(index, getJDBCType((Class<?>)null));
        } else if (parameter instanceof DatabaseField) {
            setNullFromDatabaseField((DatabaseField)parameter, statement, index);
        } else if (parameter instanceof byte[]) {
            if (usesStreamsForBinding()) {
                ByteArrayInputStream inputStream = new ByteArrayInputStream((byte[])parameter);
                statement.setBinaryStream(index, inputStream, ((byte[])parameter).length);
            } else {
                statement.setBytes(index, (byte[])parameter);
            }
        }
        // Next process types that need conversion.
        else if (parameter instanceof Calendar) {
            statement.setTimestamp(index, Helper.timestampFromDate(((Calendar)parameter).getTime()));
        } else if (parameter.getClass() == CoreClassConstants.UTILDATE) {
            statement.setTimestamp(index, Helper.timestampFromDate((java.util.Date) parameter));
        } else if (parameter instanceof Character) {
            statement.setString(index, ((Character)parameter).toString());
        } else if (parameter instanceof char[]) {
            statement.setString(index, new String((char[])parameter));
        } else if (parameter instanceof Character[]) {
            statement.setString(index, convertObject(parameter, CoreClassConstants.STRING));
        } else if (parameter instanceof Byte[]) {
            statement.setBytes(index, (byte[])convertObject(parameter, CoreClassConstants.APBYTE));
        } else if (parameter instanceof SQLXML) {
            statement.setSQLXML(index, (SQLXML) parameter);
        } else if (parameter instanceof Document) {
            SQLXML sqlxml = statement.getConnection().createSQLXML();
            sqlxml.setResult(DOMResult.class).setNode((Document)parameter);
            statement.setSQLXML(index, sqlxml);
        } else if (parameter instanceof BindCallCustomParameter) {
            ((BindCallCustomParameter)(parameter)).set(this, statement, index, session);
        } else if (typeConverters != null && typeConverters.containsKey(parameter.getClass())){
            StructConverter converter = typeConverters.get(parameter.getClass());
            parameter = converter.convertToStruct(parameter, getConnection(session, statement.getConnection()));
            statement.setObject(index, parameter);
        } else if (parameter instanceof UUID uuid) {
            statement.setBytes(index, (byte[]) convertObject(uuid, CoreClassConstants.APBYTE));
        } else {
            statement.setObject(index, parameter);
        }
    }

    /**
     * INTERNAL
     * Set the parameter in the JDBC statement with the given name.
     * This support a wide range of different parameter types,
     * and is heavily optimized for common types.
     */
    public void setParameterValueInDatabaseCall(Object parameter,
                CallableStatement statement, String name, AbstractSession session)
                throws SQLException {
        // Process common types first.
        if (parameter instanceof String) {
            // Check for stream binding of large strings.
            if (usesStringBinding() && (((String)parameter).length() > getStringBindingSize())) {
                CharArrayReader reader = new CharArrayReader(((String)parameter).toCharArray());
                statement.setCharacterStream(name, reader, ((String)parameter).length());
            } else {
                if (shouldUseGetSetNString()) {
                    statement.setNString(name, (String) parameter);
                } else {
                    statement.setString(name, (String) parameter);
                }
            }
        } else if (parameter instanceof Number number) {
            if (number instanceof Integer) {
                statement.setInt(name, number.intValue());
            } else if (number instanceof Long) {
                statement.setLong(name, number.longValue());
            }  else if (number instanceof BigDecimal) {
                statement.setBigDecimal(name, (BigDecimal) number);
            } else if (number instanceof Double) {
                statement.setDouble(name, number.doubleValue());
            } else if (number instanceof Float) {
                statement.setFloat(name, number.floatValue());
            } else if (number instanceof Short) {
                statement.setShort(name, number.shortValue());
            } else if (number instanceof Byte) {
                statement.setByte(name, number.byteValue());
            } else if (number instanceof BigInteger) {
                // Convert to BigDecimal.
                statement.setBigDecimal(name, new BigDecimal((BigInteger) number));
            } else {
                statement.setObject(name, parameter);
            }
        }  else if (parameter instanceof java.sql.Date){
            statement.setDate(name,(java.sql.Date)parameter);
        }  else if (parameter instanceof java.time.LocalDate){
            statement.setDate(name, java.sql.Date.valueOf((java.time.LocalDate) parameter));
        } else if (parameter instanceof java.time.Instant){
            statement.setTimestamp(name, java.sql.Timestamp.from((java.time.Instant)parameter));
        } else if (parameter instanceof java.sql.Timestamp){
            statement.setTimestamp(name,(java.sql.Timestamp)parameter);
        } else if (parameter instanceof java.time.LocalDateTime){
            statement.setTimestamp(name, java.sql.Timestamp.valueOf((java.time.LocalDateTime) parameter));
        } else if (parameter instanceof java.time.OffsetDateTime) {
            statement.setTimestamp(name, java.sql.Timestamp.from(((java.time.OffsetDateTime) parameter).toInstant()));
        } else if (parameter instanceof java.sql.Time){
            statement.setTime(name,(java.sql.Time)parameter);
        } else if (parameter instanceof java.time.LocalTime lt){
            java.sql.Timestamp ts = java.sql.Timestamp.valueOf(java.time.LocalDateTime.of(java.time.LocalDate.ofEpochDay(0), lt));
            statement.setTimestamp(name, ts);
        } else if (parameter instanceof java.time.OffsetTime ot) {
            java.sql.Timestamp ts = java.sql.Timestamp.valueOf(java.time.LocalDateTime.of(java.time.LocalDate.ofEpochDay(0), ot.toLocalTime()));
            statement.setTimestamp(name, ts);
        } else if (parameter instanceof java.time.Year) {
            statement.setInt(name, ((java.time.Year)parameter).getValue());
        } else if (parameter instanceof Boolean) {
            statement.setBoolean(name, (Boolean) parameter);
        } else if (parameter == null) {
            // Normally null is passed as a DatabaseField so the type is included, but in some case may be passed directly.
            statement.setNull(name, getJDBCType((Class<?>)null));
        } else if (parameter instanceof DatabaseField) {
            setNullFromDatabaseField((DatabaseField)parameter, statement, name);
        } else if (parameter instanceof byte[]) {
            if (usesStreamsForBinding()) {
                ByteArrayInputStream inputStream = new ByteArrayInputStream((byte[])parameter);
                statement.setBinaryStream(name, inputStream, ((byte[])parameter).length);
            } else {
                statement.setBytes(name, (byte[])parameter);
            }
        }
        // Next process types that need conversion.
        else if (parameter instanceof Calendar) {
            statement.setTimestamp(name, Helper.timestampFromDate(((Calendar)parameter).getTime()));
        } else if (parameter.getClass() == CoreClassConstants.UTILDATE) {
            statement.setTimestamp(name, Helper.timestampFromDate((java.util.Date) parameter));
        } else if (parameter instanceof Character) {
            statement.setString(name, ((Character)parameter).toString());
        } else if (parameter instanceof char[]) {
            statement.setString(name, new String((char[])parameter));
        } else if (parameter instanceof Character[]) {
            statement.setString(name, convertObject(parameter, CoreClassConstants.STRING));
        } else if (parameter instanceof Byte[]) {
            statement.setBytes(name, (byte[])convertObject(parameter, CoreClassConstants.APBYTE));
        } else if (parameter instanceof SQLXML) {
            statement.setSQLXML(name, (SQLXML) parameter);
        } else if (parameter instanceof Document) {
            SQLXML sqlxml = statement.getConnection().createSQLXML();
            sqlxml.setResult(DOMResult.class).setNode((Document)parameter);
            statement.setSQLXML(name, sqlxml);
        } else if (parameter instanceof BindCallCustomParameter) {
            ((BindCallCustomParameter)(parameter)).set(this, statement, name, session);
        } else if (typeConverters != null && typeConverters.containsKey(parameter.getClass())){
            StructConverter converter = typeConverters.get(parameter.getClass());
            parameter = converter.convertToStruct(parameter, getConnection(session, statement.getConnection()));
            statement.setObject(name, parameter);
        } else if (parameter instanceof UUID uuid) {
            statement.setBytes(name, (byte[]) convertObject(uuid, CoreClassConstants.APBYTE));
        } else {
            statement.setObject(name, parameter);
        }
    }

    protected void setNullFromDatabaseField(DatabaseField databaseField, PreparedStatement statement, int index) throws SQLException {
        // Substituted null value for the corresponding DatabaseField.
        // Cannot bind null through set object, so we must compute the type, this is not good.
        // Fix for bug 2730536: for ARRAY/REF/STRUCT types must pass in the
        // user defined type to setNull as well.
        if (databaseField.isObjectRelationalDatabaseField()) {
            ObjectRelationalDatabaseField field = (ObjectRelationalDatabaseField)databaseField;
            statement.setNull(index, field.getSqlType(), field.getSqlTypeName());
        } else {
            int jdbcType = getJDBCTypeForSetNull(databaseField);
            statement.setNull(index, jdbcType);
        }
    }

    protected void setNullFromDatabaseField(DatabaseField databaseField, CallableStatement statement, String name) throws SQLException {
        // Substituted null value for the corresponding DatabaseField.
        // Cannot bind null through set object, so we must compute the type, this is not good.
        // Fix for bug 2730536: for ARRAY/REF/STRUCT types must pass in the
        // user defined type to setNull as well.
        if (databaseField.isObjectRelationalDatabaseField()) {
            ObjectRelationalDatabaseField field = (ObjectRelationalDatabaseField)databaseField;
            statement.setNull(name, field.getSqlType(), field.getSqlTypeName());
        } else {
            int jdbcType = getJDBCTypeForSetNull(databaseField);
            statement.setNull(name, jdbcType);
        }
    }

    /**
     * INTERNAL
     * Get the parameter from the JDBC statement with the given index.
     * @param index - 1-based index in the argument list
     */
    public Object getParameterValueFromDatabaseCall(CallableStatement statement, int index, AbstractSession session) throws SQLException {
        return statement.getObject(index);
    }

    /**
     * INTERNAL
     * Get the parameter from the JDBC statement with the given name.
     */
    public Object getParameterValueFromDatabaseCall(CallableStatement statement, String name, AbstractSession session) throws SQLException {
        return statement.getObject(name);
    }

    // Following method adds parameters values directly into Statement. This method is being called when
    // eclipselink.jdbc.bind-parameters PU property is set to false.

    /**
     * Returns the number of parameters that used binding.
     * Should only be called in case binding is not used.
     */
    public int appendParameterInternal(Call call, Writer writer, Object parameter) {
        int nBoundParameters = 0;
        DatabaseCall databaseCall = (DatabaseCall)call;
        try {
            // PERF: Print Calendars directly avoiding timestamp conversion,
            // Must be before conversion as you cannot bind calendars.
            if (parameter instanceof Calendar) {
                appendCalendar((Calendar)parameter, writer);
                return nBoundParameters;
            }
            Object dbValue = convertToDatabaseType(parameter);

            if (dbValue instanceof String) {// String and number first as they are most common.
                if (usesStringBinding() && (((String)dbValue).length() >= getStringBindingSize())) {
                    databaseCall.bindParameter(writer, dbValue);
                    nBoundParameters = 1;
                } else {
                    appendString((String)dbValue, writer);
                }
            } else if (dbValue instanceof Number) {
                appendNumber((Number)dbValue, writer);
            } else if (dbValue instanceof java.sql.Time) {
                appendTime((java.sql.Time)dbValue, writer);
            } else if (dbValue instanceof java.sql.Timestamp) {
                appendTimestamp((java.sql.Timestamp)dbValue, writer);
            } else if (dbValue instanceof java.time.Instant){
                appendTimestamp(java.sql.Timestamp.from((java.time.Instant) dbValue), writer);
            } else if (dbValue instanceof java.time.LocalDate){
                appendDate(java.sql.Date.valueOf((java.time.LocalDate) dbValue), writer);
            } else if (dbValue instanceof java.time.LocalDateTime){
                appendTimestamp(java.sql.Timestamp.valueOf((java.time.LocalDateTime) dbValue), writer);
            } else if (dbValue instanceof java.time.OffsetDateTime) {
                appendTimestamp(java.sql.Timestamp.from(((java.time.OffsetDateTime) dbValue).toInstant()), writer);
            } else if (dbValue instanceof java.time.LocalTime lt){
                java.sql.Timestamp ts = java.sql.Timestamp.valueOf(java.time.LocalDateTime.of(java.time.LocalDate.ofEpochDay(0), lt));
                appendTimestamp(ts, writer);
            } else if (dbValue instanceof java.time.OffsetTime ot) {
                java.sql.Timestamp ts = java.sql.Timestamp.valueOf(java.time.LocalDateTime.of(java.time.LocalDate.ofEpochDay(0), ot.toLocalTime()));
                appendTimestamp(ts, writer);
            } else if (dbValue instanceof java.time.Year){
                appendNumber(((java.time.Year) dbValue).getValue(), writer);
            } else if (dbValue instanceof java.sql.Date) {
                appendDate((java.sql.Date)dbValue, writer);
            } else if (dbValue == null) {
                writer.write("NULL");
            } else if (dbValue instanceof Boolean) {
                appendBoolean((Boolean)dbValue, writer);
            } else if (dbValue instanceof byte[]) {
                if (usesByteArrayBinding()) {
                    databaseCall.bindParameter(writer, dbValue);
                    nBoundParameters = 1;
                } else {
                    appendByteArray((byte[])dbValue, writer);
                }
            } else if (dbValue instanceof Collection) {
                nBoundParameters = printValuelist((Collection<?>)dbValue, databaseCall, writer);
            } else if (typeConverters != null && typeConverters.containsKey(dbValue.getClass())){
                dbValue = new BindCallCustomParameter(dbValue);
                // custom binding is required, object to be bound is wrapped (example NCHAR, NVARCHAR2, NCLOB on Oracle9)
                databaseCall.bindParameter(writer, dbValue);
            } else if ((parameter instanceof Struct) || (parameter instanceof Array) || (parameter instanceof Ref)) {
                databaseCall.bindParameter(writer, parameter);
                nBoundParameters = 1;
            } else if (dbValue.getClass() == int[].class) {
                nBoundParameters = printValuelist((int[])dbValue, databaseCall, writer);
            } else if (dbValue instanceof AppendCallCustomParameter) {
                // custom append is required (example BLOB, CLOB on Oracle8)
                ((AppendCallCustomParameter)dbValue).append(writer);
                nBoundParameters = 1;
            } else if (dbValue instanceof BindCallCustomParameter) {
                // custom binding is required, object to be bound is wrapped (example NCHAR, NVARCHAR2, NCLOB on Oracle9)
                databaseCall.bindParameter(writer, dbValue);
                nBoundParameters = 1;
            } else if (parameter instanceof UUID uuid) {
                nBoundParameters = appendParameterInternal(databaseCall, writer, convertObject(uuid, CoreClassConstants.APBYTE));
            } else {
                // Assume database driver primitive that knows how to print itself, this is required for drivers
                // such as Oracle JDBC, Informix JDBC and others, as well as client specific classes.
                writer.write(dbValue.toString());
            }
        } catch (IOException exception) {
            throw ValidationException.fileError(exception);
        }

        return nBoundParameters;
    }

    /**
     * Appends a Boolean value as a number
     */
    protected void appendBoolean(Boolean bool, Writer writer) throws IOException {
        if (bool) {
            writer.write("1");
        } else {
            writer.write("0");
        }
    }

    /**
     * Append the ByteArray in ODBC literal format ({b hexString}).
     * This limits the amount of Binary data by the length of the SQL. Binding should increase this limit.
     */
    protected void appendByteArray(byte[] bytes, Writer writer) throws IOException {
        writer.write("{b '");
        writer.write(HexFormat.of().formatHex(bytes));
        writer.write("'}");
    }

    /**
     * Answer a platform correct string representation of a Date, suitable for SQL generation.
     * The date is printed in the ODBC platform independent format {d 'yyyy-mm-dd'}.
     */
    protected void appendDate(java.sql.Date date, Writer writer) throws IOException {
        writer.write("{d '");
        writer.write(Helper.printDate(date));
        writer.write("'}");
    }

    /**
     * Write number to SQL string. This is provided so that database which do not support
     * Exponential format can customize their printing.
     */
    protected void appendNumber(Number number, Writer writer) throws IOException {
        writer.write(number.toString());
    }

    /**
     * INTERNAL:
     * In case shouldBindLiterals is true, instead of null value a DatabaseField
     * value may be passed (so that it's type could be used for binding null).
     *
     * @param canBind - allows higher up the stack (where more context exists) to tell if this literal can be bound
     */
    public void appendLiteralToCall(Call call, Writer writer, Object literal, Boolean canBind) {
        if(!Boolean.FALSE.equals(canBind) && shouldBindLiterals()) {
            appendLiteralToCallWithBinding(call, writer, literal);
        } else {
            int nParametersToAdd = appendParameterInternal(call, writer, literal);
            for (int i = 0; i < nParametersToAdd; i++) {
                ((DatabaseCall)call).getParameterTypes().add(ParameterType.LITERAL);
            }
        }
    }

    /**
     * INTERNAL:
     * Override this method in case the platform needs to do something special for binding literals.
     * Note that instead of null value a DatabaseField
     * value may be passed (so that it's type could be used for binding null).
     */
    protected void appendLiteralToCallWithBinding(Call call, Writer writer, Object literal) {
        ((DatabaseCall)call).appendLiteral(writer, literal);
    }

    /**
     * Write a database-friendly representation of the given parameter to the writer.
     * Determine the class of the object to be written, and invoke the appropriate print method
     * for that object. The default is "toString".
     * The platform may decide to bind some types, such as byte arrays and large strings.
     * Should only be called in case binding is not used.
     */
    @Override
    public void appendParameter(Call call, Writer writer, Object parameter) {
        appendParameterInternal(call, writer, parameter);
    }

    /**
     * Write the string.  Quotes must be double quoted.
     */
    protected void appendString(String string, Writer writer) throws IOException {
        writer.write('\'');
        for (int position = 0; position < string.length(); position++) {
            if (string.charAt(position) == '\'') {
                writer.write("''");
            } else {
                writer.write(string.charAt(position));
            }
        }
        writer.write('\'');
    }

    /**
     * Answer a platform correct string representation of a Time, suitable for SQL generation.
     * The time is printed in the ODBC platform independent format {t'hh:mm:ss'}.
     */
    protected void appendTime(java.sql.Time time, Writer writer) throws IOException {
        writer.write("{t '");
        writer.write(Helper.printTime(time));
        writer.write("'}");
    }

    /**
     * Answer a platform correct string representation of a Timestamp, suitable for SQL generation.
     * The timestamp is printed in the ODBC platform independent timestamp format {ts'YYYY-MM-DD HH:MM:SS.NNNNNNNNN'}.
     */
    protected void appendTimestamp(java.sql.Timestamp timestamp, Writer writer) throws IOException {
        writer.write("{ts '");
        writer.write(Helper.printTimestamp(timestamp));
        writer.write("'}");
    }

    /**
     * Answer a platform correct string representation of a Calendar as a Timestamp, suitable for SQL generation.
     * The calendar is printed in the ODBC platform independent timestamp format {ts'YYYY-MM-DD HH:MM:SS.NNNNNNNNN'}.
     */
    protected void appendCalendar(Calendar calendar, Writer writer) throws IOException {
        writer.write("{ts '");
        writer.write(Helper.printCalendar(calendar));
        writer.write("'}");
    }

    public boolean usesBatchWriting() {
        return usesBatchWriting;
    }

    public boolean usesByteArrayBinding() {
        return usesByteArrayBinding;
    }

    public boolean usesSequenceTable() {
        return getDefaultSequence().isTable();
    }

    /**
     * Some JDBC 2 drivers to not support batching, so this lets are own batching be used.
     */
    public boolean usesJDBCBatchWriting() {
        return usesJDBCBatchWriting;
    }

    public boolean usesNativeBatchWriting(){
        return usesNativeBatchWriting;
    }

    public boolean usesNativeSQL() {
        return usesNativeSQL;
    }

    public boolean usesStreamsForBinding() {
        return usesStreamsForBinding;
    }

    public boolean usesStringBinding() {
        return usesStringBinding;
    }

    /**
     * INTERNAL:
     * Write LOB value - only on Oracle8 and up
     */
    public void writeLOB(DatabaseField field, Object value, ResultSet resultSet, AbstractSession session) throws SQLException {
        // used by Oracle8Platform
    }

    /**
     * INTERNAL:
     * Indicates whether the platform supports the count distinct function with multiple fields.
     */
    public boolean supportsCountDistinctWithMultipleFields() {
        return false;
    }

    /**
     * INTERNAL:
     * Create platform-default Sequence
     */
    @Override
    protected Sequence createPlatformDefaultSequence() {
        return new TableSequence();
    }

    /**
     * INTERNAL:
     * Indicates whether the platform supports temporary tables.
     * Temporary tables may be used by UpdateAllQueries:
     * though attempt is always made to perform UpdateAll without using temporary
     * storage there are some scenarios that can't be fulfilled without it.
     * Don't override this method.
     * If the platform support temporary tables then override
     * either supportsLocalTempTables() or supportsGlobalTempTables()
     * method.
     */
     public final boolean supportsTempTables() {
         return supportsLocalTempTables() || supportsGlobalTempTables();
     }

    /**
     * INTERNAL:
     * Indicates whether the platform supports local temporary tables.
     * "Local" means that several threads may create
     * temporary tables with the same name.
     * Local temporary table is created in the beginning of UpdateAllQuery
     * execution and dropped in the end of it.
     * Override this method if the platform supports local temporary tables.
     */
     public boolean supportsLocalTempTables() {
         return false;
     }

    /**
     * INTERNAL:
     * Indicates whether the platform supports global temporary tables.
     * "Global" means that an attempt to create temporary table with the same
     * name for the second time results in exception.
     * EclipseLink attempts to create global temporary table in the beginning of UpdateAllQuery,
     * execution and assumes that it already exists in case SQLException results.
     * In the end of UpdateAllQuery execution all rows are removed from the temporary table -
     * it is necessary in case the same temporary table will be used by another UpdateAllQuery
     * in the same transaction.
     * Override this method if the platform supports global temporary tables.
     * Note that this method is ignored in case supportsLocalTempTables() returns true.
     */
     public boolean supportsGlobalTempTables() {
         return false;
     }

    /**
     * INTERNAL:
     * Override this method if the platform supports temporary tables.
     * This should contain the beginning of sql string for
     * creating temporary table - the sql statement name, for instance:
     * "CREATE GLOBAL TEMPORARY TABLE ".
     * Don't forget to end it with a space.
     */
     protected String getCreateTempTableSqlPrefix() {
         throw ValidationException.platformDoesNotOverrideGetCreateTempTableSqlPrefix(getClass().getSimpleName());
     }

    /**
     * INTERNAL:
     * May override this method if the platform support temporary tables.
     * @param table is original table for which temp table is created.
     * @return temporary table
     */
     public DatabaseTable getTempTableForTable(DatabaseTable table) {
         return new DatabaseTable("TL_" + table.getName(), table.getTableQualifier(), table.shouldUseDelimiters(), getStartDelimiter(), getEndDelimiter());
     }

    /**
     * INTERNAL:
     * May override this method if the platform support temporary tables.
     * This should contain the ending of sql string for
     * creating temporary table, for instance:
     * " ON COMMIT DELETE ROWS"
     * Don't forget to begin it with a space.
     */
    protected String getCreateTempTableSqlSuffix() {
        return "";
    }

    /**
     * INTERNAL:
     * May override this method if the platform supports temporary tables.
     * With this method not overridden the sql string for temporary table creation
     * will include a list of database fields extracted from descriptor:
     * getCreateTempTableSqlPrefix() + getTempTableForTable(table).getQualifiedName() +
     * (list of database fields) + getCreateTempTableSqlSuffix().
     * If this method is overridden its output will be used instead of fields' list:
     * getCreateTempTableSqlPrefix() + getTempTableForTable(table).getQualifiedName() +
     * getCreateTempTableSqlBodyForTable(table) + getCreateTempTableSqlSuffix().
     * Don't forget to begin it with a space.
     * Example: " LIKE " + table.getQualifiedName();
     * @param table is original table for which temp table is created.
     * @return String
     */
    protected String getCreateTempTableSqlBodyForTable(DatabaseTable table) {
        return null;
    }

    /**
     * INTERNAL:
     * Indicates whether temporary table can specify primary keys (some platforms don't allow that).
     * Used by writeCreateTempTableSql method.
     */
    protected boolean shouldTempTableSpecifyPrimaryKeys() {
        return true;
    }

    /**
     * INTERNAL:
     * Don't override this method.
     * Write an sql string for creation of the temporary table.
     * Note that in case of local temp table support it's possible to limit
     * the fields in the temp table to those needed for the operation it supports (usedFields) -
     * the temp table will be dropped in the end of query execution.
     * Alternatively, in global temp table case the table with a given name is created just once
     * and will be potentially used by various operations with various sets of used fields,
     * therefore global temp table should contain all mapped fields (allFields).
     * Precondition: supportsTempTables() == true.
     * Precondition: pkFields contained in usedFields contained in allFields
     * @param writer for writing the sql
     * @param table is original table for which temp table is created.
     * @param pkFields primary key fields for the original table.
     * @param usedFields fields that will be used by operation for which temp table is created.
     * @param allFields all mapped fields for the original table.
     */
     public final void writeCreateTempTableSql(Writer writer, DatabaseTable table, AbstractSession session,
                                         Collection<DatabaseField> pkFields,
                                         Collection<DatabaseField> usedFields,
                                         Collection<DatabaseField> allFields) throws IOException
    {
        String body = getCreateTempTableSqlBodyForTable(table);
        if(body == null) {
            TableDefinition tableDef = new TableDefinition();
            Collection<DatabaseField> fields;
            if(supportsLocalTempTables()) {
                fields = usedFields;
            } else {
                // supportsGlobalTempTables() == true
                fields = allFields;
            }
            Iterator<DatabaseField> itFields = fields.iterator();
            while (itFields.hasNext()) {
                DatabaseField field = itFields.next();
                FieldDefinition fieldDef;
                //gfbug3307, should use columnDefinition if it was defined.
                if ((field.getColumnDefinition()!= null) && (field.getColumnDefinition().isEmpty())) {
                    Class<?> type = ConversionManager.getObjectClass(field.getType());
                    // Default type to VARCHAR, if unknown.
                    if (type == null) {
                        type = ConversionManager.getObjectClass(CoreClassConstants.STRING);
                    }
                   fieldDef = new FieldDefinition(field.getNameDelimited(this), type);
                } else {
                   fieldDef = new FieldDefinition(field.getNameDelimited(this), field.getColumnDefinition());
                }
                if (pkFields.contains(field) && shouldTempTableSpecifyPrimaryKeys()) {
                    fieldDef.setIsPrimaryKey(true);
                }
                tableDef.addField(fieldDef);
            }
            tableDef.setCreationPrefix(getCreateTempTableSqlPrefix());
            tableDef.setName(getTempTableForTable(table).getQualifiedNameDelimited(this));
            tableDef.setCreationSuffix(getCreateTempTableSqlSuffix());
            tableDef.buildCreationWriter(session, writer);
        } else {
            writer.write(getCreateTempTableSqlPrefix());
            writer.write(getTempTableForTable(table).getQualifiedNameDelimited(this));
            writer.write(body);
            writer.write(getCreateTempTableSqlSuffix());
        }
    }

    /**
     * INTERNAL:
     * May need to override this method if the platform supports temporary tables
     * and the generated sql doesn't work.
     * Write an sql string for insertion into the temporary table.
     * Precondition: supportsTempTables() == true.
     * @param writer for writing the sql
     * @param table is original table for which temp table is created.
     * @param usedFields fields that will be used by operation for which temp table is created.
     */
     public void writeInsertIntoTableSql(Writer writer, DatabaseTable table, Collection<DatabaseField> usedFields) throws IOException {
        writer.write("INSERT INTO ");
        writer.write(getTempTableForTable(table).getQualifiedNameDelimited(this));

        writer.write(" (");
        writeFieldsList(writer, usedFields, this);
        writer.write(") ");
    }

    /**
     * INTERNAL:
     * Override this if the platform cannot handle NULL in select clause.
     */
    public boolean isNullAllowedInSelectClause() {
        return true;
    }

    /**
     * INTERNAL:
     * Return true if output parameters can be built with result sets.
     */
    public boolean isOutputAllowWithResultSet() {
        return true;
    }

    /**
     * INTERNAL:
     * May need to override this method if the platform supports temporary tables
     * and the generated sql doesn't work.
     * Write an sql string for updating the original table from the temporary table.
     * Precondition: supportsTempTables() == true.
     * Precondition: pkFields and assignFields don't intersect.
     * @param writer for writing the sql
     * @param table is original table for which temp table is created.
     * @param pkFields primary key fields for the original table.
     * @param assignedFields fields to be assigned a new value.
     */
     public void writeUpdateOriginalFromTempTableSql(Writer writer, DatabaseTable table,
                                                     Collection<DatabaseField> pkFields,
                                                     Collection<DatabaseField> assignedFields) throws IOException
    {
        writer.write("UPDATE ");
        String tableName = table.getQualifiedNameDelimited(this);
        writer.write(tableName);
        writer.write(" SET (");
        writeFieldsList(writer, assignedFields, this);
        writer.write(") = (SELECT ");
        writeFieldsList(writer, assignedFields, this);
        writer.write(" FROM ");
        String tempTableName = getTempTableForTable(table).getQualifiedNameDelimited(this);
        writer.write(tempTableName);
        writeAutoJoinWhereClause(writer, null, tableName, pkFields, this);
        writer.write(") WHERE EXISTS(SELECT ");
        writer.write(pkFields.iterator().next().getNameDelimited(this));
        writer.write(" FROM ");
        writer.write(tempTableName);
        writeAutoJoinWhereClause(writer, null, tableName, pkFields, this);
        writer.write(")");
    }

    /**
     * INTERNAL:
     * Write an sql string for deletion from target table using temporary table.
     * At this point temporary table should contains pks for the rows that should be
     * deleted from target table.
     * Temporary tables are not required for DeleteAllQuery, however will be used if
     * shouldAlwaysUseTempStorageForModifyAll()==true
     * May need to override this method in case it generates sql that doesn't work on the platform.
     * Precondition: supportsTempTables() == true.
     * @param writer for writing the sql
     * @param table is original table for which temp table is created.
     * @param targetTable is a table from which to delete.
     * @param pkFields primary key fields for the original table.
     * @param targetPkFields primary key fields for the target table.
     */
     public void writeDeleteFromTargetTableUsingTempTableSql(Writer writer, DatabaseTable table, DatabaseTable targetTable,
                                                             Collection<DatabaseField> pkFields,
                                                             Collection<DatabaseField> targetPkFields, DatasourcePlatform platform) throws IOException
    {
        writer.write("DELETE FROM ");
        String targetTableName = targetTable.getQualifiedNameDelimited(this);
        writer.write(targetTableName);
        writer.write(" WHERE EXISTS(SELECT ");
        writer.write(pkFields.iterator().next().getNameDelimited(platform));
        writer.write(" FROM ");
        String tempTableName = getTempTableForTable(table).getQualifiedNameDelimited(this);
        writer.write(tempTableName);
        writeJoinWhereClause(writer, null, targetTableName, pkFields, targetPkFields, this);
        writer.write(")");
    }

     public boolean wasFailureCommunicationBased(SQLException exception, Connection connection, AbstractSession sessionForProfile){
         if (connection == null) {
             //Without a connection we are  unable to determine what caused the error so return false.
             //The only case where connection will be null should be External Connection Pooling so
             //returning false is ok as there is no connection management requirement
             return false;
         } else if (this.pingSQL == null) {
             // By default use the JDBC isValid API unless a ping SQL has been set.
             // The ping SQL is set by most platforms, but user could set to null to used optimized JDBC check if desired.
             try {
                 return !connection.isValid(IS_VALID_TIMEOUT);
             } catch (Throwable failed) {
                 // Catch throwable as old JDBC drivers may not support isValid.
                 return false;
             }
         }
         PreparedStatement statement = null;
         try{
             sessionForProfile.startOperationProfile(SessionProfiler.ConnectionPing);
             if (sessionForProfile.shouldLog(SessionLog.FINE, SessionLog.SQL)) {// Avoid printing if no logging required.
                 sessionForProfile.log(SessionLog.FINE, SessionLog.SQL, getPingSQL(), null, null, false);
             }
             statement = connection.prepareStatement(getPingSQL());
             ResultSet result = statement.executeQuery();
             result.close();
             statement.close();
         }catch (SQLException ex){
             try{
                 //try to close statement again in case the query or result.close() caused an exception.
                 if (statement != null) statement.close();
             }catch (SQLException exception2){
                 //ignore;
             }
             return true;
         }finally{
             sessionForProfile.endOperationProfile(SessionProfiler.ConnectionPing);
         }
         return false;
     }

    /**
     * INTERNAL:
     * Don't override this method.
     * Write an sql string for clean up of the temporary table.
     * Drop a local temp table or delete all from a global temp table (so that it's
     * ready to be used again in the same transaction).
     * Precondition: supportsTempTables() == true.
     * @param writer for writing the sql
     * @param table is original table for which temp table is created.
     */
     public final void writeCleanUpTempTableSql(Writer writer, DatabaseTable table) throws IOException {
        if(supportsLocalTempTables()) {
            writer.write("DROP TABLE ");
        } else {
            // supportsGlobalTempTables() == true
            writer.write("DELETE FROM ");
        }
        writer.write(getTempTableForTable(table).getQualifiedNameDelimited(this));
    }

    /**
     * INTERNAL:
     * That method affects UpdateAllQuery and DeleteAllQuery execution.
     * In case it returns false modify all queries would attempt to proceed
     * without using temporary storage if it is possible.
     * In case it returns true modify all queries would use temporary storage unless
     * each modify statement doesn't reference any other tables.
     * May need to override this method if the platform can't handle the sql
     * generated for modify all queries without using temporary storage.
     */
    public boolean shouldAlwaysUseTempStorageForModifyAll() {
        return false;
    }

   /**
    * INTERNAL:
    * May need to override this method if the sql generated for UpdateAllQuery
    * using temp tables fails in case parameter binding is used.
    */
    public boolean dontBindUpdateAllQueryUsingTempTables() {
        return false;
    }

    /**
     * INTERNAL:
     * helper method, don't override.
     */
    protected static void writeFieldsList(Writer writer, Collection<DatabaseField> fields, DatasourcePlatform platform) throws IOException {
        boolean isFirst = true;
        Iterator<DatabaseField> itFields = fields.iterator();
        while(itFields.hasNext()) {
            if(isFirst) {
                isFirst = false;
            } else {
                writer.write(", ");
            }
            DatabaseField field = itFields.next();
            writer.write(field.getNameDelimited(platform));
        }
    }

    /**
     * INTERNAL:
     * helper method, don't override.
     */
    protected static void writeAutoAssignmentSetClause(Writer writer, String tableName1, String tableName2, Collection<DatabaseField> fields, DatasourcePlatform platform) throws IOException {
        writer.write(" SET ");
        writeFieldsAutoClause(writer, tableName1, tableName2, fields, ", ", platform);
    }

    /**
     * INTERNAL:
     * helper method, don't override.
     */
    protected static void writeAutoJoinWhereClause(Writer writer, String tableName1, String tableName2, Collection<DatabaseField> pkFields, DatasourcePlatform platform) throws IOException {
        writer.write(" WHERE ");
        writeFieldsAutoClause(writer, tableName1, tableName2, pkFields, " AND ", platform);
    }

    /**
     * INTERNAL:
     * helper method, don't override.
     */
    protected static void writeFieldsAutoClause(Writer writer, String tableName1, String tableName2, Collection<DatabaseField> fields, String separator, DatasourcePlatform platform) throws IOException {
        writeFields(writer, tableName1, tableName2, fields, fields, separator, platform);
    }
    /**
     * INTERNAL:
     * helper method, don't override.
     */
    protected static void writeJoinWhereClause(Writer writer, String tableName1, String tableName2, Collection<DatabaseField> pkFields1, Collection<DatabaseField> pkFields2, DatasourcePlatform platform) throws IOException {
        writer.write(" WHERE ");
        writeFields(writer, tableName1, tableName2, pkFields1, pkFields2, " AND ", platform);
    }

    /**
     * INTERNAL:
     * helper method, don't override.
     */
    protected static void writeFields(Writer writer, String tableName1, String tableName2, Collection<DatabaseField> fields1, Collection<DatabaseField> fields2, String separator, DatasourcePlatform platform) throws IOException {
        boolean isFirst = true;
        Iterator<DatabaseField> itFields1 = fields1.iterator();
        Iterator<DatabaseField> itFields2 = fields2.iterator();
        while(itFields1.hasNext()) {
            if(isFirst) {
                isFirst = false;
            } else {
                writer.write(separator);
            }
            if(tableName1 != null) {
                writer.write(tableName1);
                writer.write(".");
            }
            String fieldName1 = itFields1.next().getNameDelimited(platform);
            writer.write(fieldName1);
            writer.write(" = ");
            if(tableName2 != null) {
                writer.write(tableName2);
                writer.write(".");
            }
            String fieldName2 = itFields2.next().getNameDelimited(platform);
            writer.write(fieldName2);
        }
    }

    public void writeParameterMarker(Writer writer, ParameterExpression expression, AbstractRecord record, DatabaseCall call) throws IOException {
        writer.write("?");
    }

    /**
     * INTERNAL:
     * This method builds an Array using the unwrapped connection within the session
     * @return Array
     */
    public Array createArray(String elementDataTypeName, Object[] elements, AbstractSession session, Connection connection) throws SQLException {
        //Bug#5200836 need unwrap the connection prior to using.
        java.sql.Connection unwrappedConnection = getConnection(session, connection);
        return createArray(elementDataTypeName,elements,unwrappedConnection);
    }

    /**
     * INTERNAL:
     * This method builds a Struct using the unwrapped connection within the session
     * @return Struct
     */
    public Struct createStruct(String structTypeName, Object[] attributes, AbstractSession session, Connection connection) throws SQLException {
        java.sql.Connection unwrappedConnection = getConnection(session, connection);
        return createStruct(structTypeName,attributes,unwrappedConnection);
    }

    /**
     * INTERNAL:
     * This method builds a Struct using the unwrapped connection within the session
     * @return Struct
     */
    public Struct createStruct(String structTypeName, Object[] attributes, AbstractRecord row, List<DatabaseField> orderedFields, AbstractSession session, Connection connection) throws SQLException {
        java.sql.Connection unwrappedConnection = getConnection(session, connection);
        return createStruct(structTypeName,attributes,unwrappedConnection);
    }

    /**
     * INTERNAL:
     * Platforms that support java.sql.Array may override this method.
     * @return Array
     */
    public Array createArray(String elementDataTypeName, Object[] elements, Connection connection) throws SQLException {
        return connection.createArrayOf(elementDataTypeName, elements);
    }

    /**
     * INTERNAL:
     * Platforms that support java.sql.Struct may override this method.
     * @return Struct
     */
    public Struct createStruct(String structTypeName, Object[] attributes, Connection connection) throws SQLException {
        return connection.createStruct(structTypeName, attributes);
    }

    /**
     * INTERNAL:
     * Indicates whether the passed object is an instance of XDBDocument.
     * To avoid dependency on oracle.xdb the method returns false.
     * Overridden in Oracle9Platform
     * @return String
     */
    public boolean isXDBDocument(Object obj) {
        return false;
    }

    /**
     * Allows platform to choose whether to bind literals in DatabaseCalls or not.
     */
    public boolean shouldBindLiterals() {
        return this.shouldBindLiterals;
    }

    /**
     * Allows user to choose whether to bind literals in DatabaseCalls or not.
     */
    public void setShouldBindLiterals(boolean shouldBindLiterals) {
        this.shouldBindLiterals = shouldBindLiterals;
    }

    /**
     * INTERNAL:
     * Some databases have issues with using parameters on certain functions and relations.
     * This allows statements to disable binding only in these cases.
     * <p>
     * Alternatively, DatabasePlatforms can override specific ExpressionOperators and add them
     * to the platform specific operators. See {@link DatasourcePlatform#initializePlatformOperators()}
     */
    public boolean isDynamicSQLRequiredForFunctions() {
        return false;
    }

    public boolean allowBindingForSelectClause() {
        return true;
    }

    /**
     * INTERNAL:
     * Platforms that support java.sql.Ref may override this method.
     * @return Object
     */
    public Object getRefValue(Ref ref,Connection connection) throws SQLException {
        return ref.getObject();
    }
    /**
     * INTERNAL:
     * This method builds a REF using the unwrapped connection within the session
     * @return Object
     */
    public Object getRefValue(Ref ref,AbstractSession executionSession,Connection connection) throws SQLException {
        //Bug#6068155, ensure connection is lived when processing the REF type value.
        java.sql.Connection unwrappedConnection = getConnection(executionSession,connection);
        return getRefValue(ref,unwrappedConnection);
    }


    /**
     * INTERNAL:
     * Prints return keyword for StoredFunctionDefinition:
     *    CREATE FUNCTION StoredFunction_In (P_IN BIGINT)
     *      RETURN  BIGINT
     * The method was introduced because MySQL requires "RETURNS" instead:
     *    CREATE FUNCTION StoredFunction_In (P_IN BIGINT)
     *      RETURNS  BIGINT
     */
    public void printStoredFunctionReturnKeyWord(Writer writer) throws IOException {
        writer.write("\n\t RETURN ");
    }

    /**
     * INTERNAL:
     * Print the SQL representation of the statement on a stream, storing the fields
     * in the DatabaseCall.
     */
    public void printSQLSelectStatement(DatabaseCall call, ExpressionSQLPrinter printer, SQLSelectStatement statement){
        call.setFields(statement.printSQL(printer));
    }

    /**
     * INTERNAL:
     * Indicates whether locking clause should be printed after where clause by SQLSelectStatement.
     * Example:
     *   on Oracle platform (method returns true):
     *     SELECT ADDRESS_ID, ... FROM ADDRESS WHERE (ADDRESS_ID = ?) FOR UPDATE
     *   on SQLServer platform (method returns false):
     *     SELECT ADDRESS_ID, ... FROM ADDRESS WITH (UPDLOCK) WHERE (ADDRESS_ID = ?)
     */
    public boolean shouldPrintLockingClauseAfterWhereClause() {
        return true;
    }

    /**
     * INTERNAL:
     * Indicates whether locking clause could be selectively applied only to some tables in a ReadQuery.
     * Example: the following locks the rows in SALARY table, doesn't lock the rows in EMPLOYEE table:
     *   on Oracle platform (method returns true):
     *     SELECT t0.EMP_ID..., t1.SALARY FROM EMPLOYEE t0, SALARY t1 WHERE ... FOR UPDATE t1.SALARY
     *   on SQLServer platform (method returns true):
     *     SELECT t0.EMP_ID..., t1.SALARY FROM EMPLOYEE t0, SALARY t1 WITH (UPDLOCK) WHERE ...
     */
    public boolean supportsIndividualTableLocking() {
        return true;
    }

    /**
     * INTERNAL:
     * Indicates whether locking clause could be applied to the query that has more than one table
     */
    public boolean supportsLockingQueriesWithMultipleTables() {
        return true;
    }

    /**
     * INTERNAL:
     * Indicates whether locking OF clause should print alias for field.
     * Example:
     *   on Oracle platform (method returns false):
     *     SELECT ADDRESS_ID, ... FROM ADDRESS T1 WHERE (T1.ADDRESS_ID = ?) FOR UPDATE OF T1.ADDRESS_ID
     *   on Postgres platform (method returns true):
     *     SELECT ADDRESS_ID, ... FROM ADDRESS T1 WHERE (T1.ADDRESS_ID = ?) FOR UPDATE OF T1
     */
    public boolean shouldPrintAliasForUpdate() {
        return false;
    }

    /**
     * INTERNAL:
     * Return if nesting outer joins is supported, i.e. each join must be followed by the ON clause.
     */
    public boolean supportsNestingOuterJoins() {
        return true;
    }

    /**
     * INTERNAL:
     * Return if brackets can be used in the ON clause for outer joins.
     */
    public boolean supportsOuterJoinsWithBrackets() {
        return true;
    }

    /**
     * INTERNAL:
     * Used by some platforms during reading of ResultSet to free temporary objects.
     */
    public void freeTemporaryObject(Object value) throws SQLException {
    }

    /**
     * INTERNAL:
     * Allow initialization from the connection.
     */
    public void initializeConnectionData(Connection connection) throws SQLException {
    }

    /**
     * INTERNAL:
     * Override this method if the platform supports storing JDBC connection user name during
     * {@link #initializeConnectionData(Connection)}.
     * @return Always returns {@code false}
     */
    public boolean supportsConnectionUserName() {
        return false;
    }

    /**
     * INTERNAL:
     * Returns user name retrieved from JDBC connection.
     * @throws UnsupportedOperationException on every single call until overridden.
     */
    public String getConnectionUserName() {
        throw new UnsupportedOperationException("Connection user name is not supported.");
    }

    // Eager initialization in constructor causes CORBA Extension tests to fail.
    /**
     * Get JSON support extension instance.
     * This instance is initialized lazily with 1st JSON support request.
     *
     * @return JSON support extension instance
     */
    public DatabaseJsonPlatform getJsonPlatform() {
        if (jsonPlatform != null) {
            return jsonPlatform;
        }
        synchronized (this) {
            if (jsonPlatform == null) {
                jsonPlatform = JsonPlatformManager.getInstance().createPlatform(this.getClass());
            }
            return jsonPlatform;
        }
    }

    /*
                                 ____  ____  __
                                |    \|    \|  |
                                |  |  |  |  |  |__
                                |____/|____/|_____|
     */
    // DDL related fields
    /** Holds a map of values used to map JAVA types to database types for table creation */
    private transient Map<Class<?>, FieldDefinition.DatabaseType> databaseTypes;

    /** Stores mapping of class types to database types for schema creation. */
    private transient Map<String, Class<?>> javaTypes;

    /**
     * String used on all table creation statements generated from the DefaultTableGenerator
     * with a session using this project.  This value will be appended to CreationSuffix strings
     * stored within the DatabaseTable creationSuffix.
     */
    private String tableCreationSuffix;

    /** Allows auto-indexing for foreign keys to be set. */
    private boolean shouldCreateIndicesOnForeignKeys;

    /**
     * The delimiter between stored procedures in multiple stored procedure
     * calls.
     */
    private String storedProcedureTerminationToken;

    // DDL related methods
    /**
     * INTERNAL:
     * Don't override this method.
     *
     * @param fullTableName
     *            qualified name of the table the index is to be created on
     * @param indexName
     *            name of the index
     * @param columnNames
     *            one or more columns the index is created for
     */
    public final String buildCreateIndex(String fullTableName, String indexName, String... columnNames) {
        return buildCreateIndex(fullTableName, indexName, "", false, columnNames);
    }

    /**
     * INTERNAL:
     * Don't override this method.
     *
     * @param fullTableName
     *            qualified name of the table the index is to be removed from
     * @param indexName
     *            name of the index
     */
    public final String buildDropIndex(String fullTableName, String indexName) {
        return buildDropIndex(fullTableName, indexName, "");
    }

    /**
     * Return the mapping of class types to database types for the schema framework.
     */
    protected Map<String, Class<?>> buildJavaTypes() {
        if (javaTypes == null) {
            javaTypes = CLASS_TYPES;
        }
        return javaTypes;
    }

    /**
     * Return the mapping of class types to database types for the schema framework.
     */
    protected Map<Class<?>, FieldDefinition.DatabaseType> buildDatabaseTypes() {
        Map<Class<?>, FieldDefinition.DatabaseType> fieldTypeMapping = new HashMap<>(DB_TYPES);
        getJsonPlatform().updateFieldTypes(fieldTypeMapping);
        return fieldTypeMapping;
    }

    /**
     * INTERNAL:
     * Executes and evaluates query to check whether given sequence exists.
     * @param session current database session
     * @param sequence database sequence meta-data
     * @param suppressLogging whether to suppress logging during query execution
     * @return value of {@code true} if given sequence exists or {@code false} otherwise
     */
    @SuppressWarnings({"removal"})
    public boolean checkSequenceExists(final AbstractSession session,
                                       final SequenceDefinition sequence, final boolean suppressLogging) {
        //TODO: delete sequence.checkIfExist method
        // and follow the pattern from checkTableExists
        try {
            session.setLoggingOff(suppressLogging);
            return sequence.checkIfExist(session);
        } catch (Exception notFound) {
            return false;
        }
    }

    /**
     * INTERNAL:
     * Executes and evaluates query to check whether given table exists.
     * Returned value is always {@code true}, because an exception is thrown
     * when given table does not exist.
     * @param session current database session
     * @param table database table meta-data
     * @param suppressLogging whether to suppress logging during query execution
     * @return value of {@code true} if given table exists or {@code false} otherwise
     */
    public boolean checkTableExists(final AbstractSession session,
                                    final TableDefinition table, final boolean suppressLogging) {
        try {
            session.setLoggingOff(suppressLogging);
            session.executeQuery(getTableExistsQuery(table));
            return true;
        } catch (Exception notFound) {
            return false;
        }
    }

    @Override
    public FieldDefinition.DatabaseType getDatabaseType(String typeName) {
        final Class<?> typeFromName = getJavaTypes().get(typeName);
        if (typeFromName == null) { // if unknown type name, use as it is
            return new FieldDefinition.DatabaseType(typeName);
        }
        FieldDefinition.DatabaseType fieldType = getDatabaseType(typeFromName);
        if (fieldType == null) {
            throw ValidationException.javaTypeIsNotAValidDatabaseType(typeFromName);
        }
        return fieldType;
    }

    @Override
    public Map<Class<?>, FieldDefinition.DatabaseType> getDatabaseTypes() {
        if (this.databaseTypes == null) {
            this.databaseTypes = buildDatabaseTypes();
        }
        return this.databaseTypes;
    }

    /**
     * Return the class type to database type mapping for the schema framework.
     */
    @Override
    public Map<String, Class<?>> getJavaTypes() {
        if (javaTypes == null) {
            javaTypes = buildJavaTypes();
        }
        return javaTypes;
    }

    /**
     * Returns the delimiter between stored procedures in multiple stored
     * procedure calls.
     */
    @Override
    public String getStoredProcedureTerminationToken() {
        return storedProcedureTerminationToken;
    }

    /**
     * Get the String used on all table creation statements generated from the DefaultTableGenerator
     * with a session using this project (DDL generation).  This value will be appended to CreationSuffix strings
     * stored on the DatabaseTable or TableDefinition.
     */
    @Override
    @Deprecated(forRemoval = true, since = "4.0.9")
    public String getTableCreationSuffix(){
        return this.tableCreationSuffix;
    }

    /**
     * Any platform that supports VPD should implement this method. Used for DDL
     * generation.
     */
    public String getVPDCreationFunctionString(String tableName, String tenantFieldName) {
        return null;
    }

    /**
     * Any platform that supports VPD should implement this method. Used for DDL
     * generation.
     */
    public String getVPDCreationPolicyString(String tableName, AbstractSession session) {
        return null;
    }

    /**
     * Any platform that supports VPD should implement this method. Used for DDL
     * generation.
     */
    public String getVPDDeletionString(String tableName, AbstractSession session) {
        return null;
    }

    // Value of shouldCheckResultTableExistsQuery must be false.
    /**
     * INTERNAL:
     * Returns query to check whether given table exists.
     * Query execution throws an exception when no such table exists.
     * @param table database table meta-data
     * @return query to check whether given table exists
     */
    protected DataReadQuery getTableExistsQuery(final TableDefinition table) {
        final String sql = "SELECT 1 FROM " + table.getFullName();
        final DataReadQuery query = new DataReadQuery(sql);
        query.setMaxRows(1);
        return query;
    }

    public void setDatabaseTypes(Map<Class<?>, FieldDefinition.DatabaseType> theFieldTypes) {
        databaseTypes = theFieldTypes;
    }

    public void setJavaTypes(Map<String, Class<?>> classTypes) {
        this.javaTypes = classTypes;
    }

    public void setStoredProcedureTerminationToken(String storedProcedureTerminationToken) {
        this.storedProcedureTerminationToken = storedProcedureTerminationToken;
    }

    @Override
    public boolean shouldCreateIndicesOnForeignKeys() {
        return shouldCreateIndicesOnForeignKeys;
    }

    @Deprecated(forRemoval = true, since = "4.0.9")
    public boolean shouldPrintFieldIdentityClause(AbstractSession session, String qualifiedFieldName) {
        if (!supportsIdentity()) {
            return false;
        }
        if ((session.getSequencing() == null) || (session.getSequencing().whenShouldAcquireValueForAll() == Sequencing.BEFORE_INSERT)) {
            return false;
        }

        boolean shouldAcquireSequenceValueAfterInsert = false;
        DatabaseField field = new DatabaseField(qualifiedFieldName, getStartDelimiter(), getEndDelimiter());
        Iterator<ClassDescriptor> descriptors = session.getDescriptors().values().iterator();
        while (descriptors.hasNext()) {
            ClassDescriptor descriptor = descriptors.next();
            if (!descriptor.usesSequenceNumbers()) {
                continue;
            }
            if (descriptor.getSequenceNumberField().equals(field)) {
                String seqName = descriptor.getSequenceNumberName();
                Sequence sequence = getSequence(seqName);
                shouldAcquireSequenceValueAfterInsert = sequence.shouldAcquireValueAfterInsert();
                break;
            }
        }
        return shouldAcquireSequenceValueAfterInsert;
    }

    /**
     * INTERNAL:
     * May need to override this method if the platform supports ALTER TABLE ADD &lt;column&gt;
     * and the generated sql doesn't work.
     * Write the string that follows ALTER TABLE to create a sql statement for
     * the platform in order to append a new column to an existing table.
     */
    public void writeAddColumnClause(Writer writer, AbstractSession session, TableDefinition table, FieldDefinition field) throws IOException {
        writer.write("ADD ");
        field.appendDBString(writer, session, table);
    }

    /**
     * INTERNAL:
     * May need to override this method if the platform supports ALTER TABLE DROP COLUMN {@code column}
     * and the generated sql doesn't work.
     * Write the string that follows ALTER TABLE to create a sql statement for
     * the platform in order to drop existing column from an existing table.
     */
    public void writeDropColumnClause(Writer writer, AbstractSession session, TableDefinition table, String fieldName) throws IOException {
        writer.write("DROP COLUMN ");
        writer.write(fieldName);
    }

    /**
     * INTERNAL:
     * May need to override this method if the platform supports TRUNCATE TABLE {@code column}
     * and the generated sql doesn't work.
     * Write the string that creates TRUNCATE TABLE sql statement for the platform in order
     * to truncate an existing table.
     */
    public void writeTruncateTable(Writer writer, AbstractSession session, TableDefinition table) throws IOException {
        String tableName = table.getTable() == null
                ? table.getName()
                : table.getTable().getName();
        writer.write("TRUNCATE TABLE ");
        writer.write(tableName);
    }

}
