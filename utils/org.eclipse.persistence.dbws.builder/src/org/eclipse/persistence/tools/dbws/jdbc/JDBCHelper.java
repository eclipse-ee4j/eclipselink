/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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

package org.eclipse.persistence.tools.dbws.jdbc;

//javase imports
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import static java.sql.DatabaseMetaData.columnNullable;
import static java.sql.DatabaseMetaData.procedureReturnsResult;
import static java.sql.DatabaseMetaData.tableIndexStatistic;
import static java.sql.DatabaseMetaData.procedureColumnInOut;
import static java.sql.DatabaseMetaData.procedureColumnOut;
import static java.sql.DatabaseMetaData.procedureColumnReturn;
import static java.sql.Types.ARRAY;
import static java.sql.Types.OTHER;
import static java.sql.Types.STRUCT;
import static java.util.logging.Level.FINEST;

//java eXtension imports
import javax.xml.namespace.QName;

// EclipseLink imports
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.internal.xr.Attachment;
import org.eclipse.persistence.internal.xr.CollectionResult;
import org.eclipse.persistence.internal.xr.NamedQueryHandler;
import org.eclipse.persistence.internal.xr.Parameter;
import org.eclipse.persistence.internal.xr.ProcedureArgument;
import org.eclipse.persistence.internal.xr.ProcedureOutputArgument;
import org.eclipse.persistence.internal.xr.QueryHandler;
import org.eclipse.persistence.internal.xr.QueryOperation;
import org.eclipse.persistence.internal.xr.Result;
import org.eclipse.persistence.internal.xr.StoredFunctionQueryHandler;
import org.eclipse.persistence.internal.xr.StoredProcedureQueryHandler;
import org.eclipse.persistence.platform.database.DerbyPlatform;
import org.eclipse.persistence.platform.database.MySQLPlatform;
import org.eclipse.persistence.platform.database.PostgreSQLPlatform;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.tools.dbws.BaseDBWSBuilderHelper;
import org.eclipse.persistence.tools.dbws.DBWSBuilder;
import org.eclipse.persistence.tools.dbws.DBWSBuilderHelper;
import org.eclipse.persistence.tools.dbws.ProcedureOperationModel;
import org.eclipse.persistence.tools.dbws.Util;

import static org.eclipse.persistence.internal.xr.Util.SXF_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.ANY_QNAME;
import static org.eclipse.persistence.tools.dbws.Util.SXF_QNAME_CURSOR;
import static org.eclipse.persistence.tools.dbws.Util.buildCustomQName;
import static org.eclipse.persistence.tools.dbws.Util.escapePunctuation;
import static org.eclipse.persistence.tools.dbws.Util.getXMLTypeFromJDBCType;
import static org.eclipse.persistence.tools.dbws.Util.qNameFromString;

//DDL parser imports
import org.eclipse.persistence.tools.oracleddl.metadata.ArgumentType;
import org.eclipse.persistence.tools.oracleddl.metadata.ArgumentTypeDirection;
import org.eclipse.persistence.tools.oracleddl.metadata.CompositeDatabaseType;
import org.eclipse.persistence.tools.oracleddl.metadata.DatabaseType;
import org.eclipse.persistence.tools.oracleddl.metadata.FunctionType;
import org.eclipse.persistence.tools.oracleddl.metadata.ProcedureType;
import org.eclipse.persistence.tools.oracleddl.metadata.TableType;
import static org.eclipse.persistence.tools.oracleddl.metadata.ArgumentTypeDirection.IN;
import static org.eclipse.persistence.tools.oracleddl.metadata.ArgumentTypeDirection.INOUT;
import static org.eclipse.persistence.tools.oracleddl.metadata.ArgumentTypeDirection.OUT;
import static org.eclipse.persistence.tools.oracleddl.metadata.ArgumentTypeDirection.RETURN;

/*
 * Known Problems/Limitations: Oracle
 *
 * 1) Oracle driver returns wrong value of column size
 * Driver versions:
 * - 10.0.1.4
 * - 9.2.0.6
 * When tables are created with NLS_LENGTH_SEMANTICS='CHAR'
 * As per JavaDOC for the method ResultSet DatabaseMetaData.getColumns(....) :
 *            7. COLUMN_SIZE int => column size. For char or date types this is the maximum
 *               number of characters, for numeric or decimal types this is precision.
 * Suppose you create a table :
 *     create table test (col_char varchar2(8));
 * Now Retrieve the MetaData for the column "col_char" :
 *     ...
 *     ResultSet columnsInfo = databaseMetaData.getColumns(null, null, "test", "%");
 *     if (columnsInfo != null) {
 *        while (columnsInfo.next()) {
 *           dbColumn.setName(columnsInfo.getString(4)); // 4 COLUMN_NAME String => column name
 *           dbColumn.setPrecision(columnsInfo.getInt(7)); // 7 COLUMN_SIZE int => column size.
 *                                                         // For char or date types this is the
 *                                                         // maximum number of characters, for
 *                                                         // numeric or decimal types this is
 *                                                         // precision.
 *        }
 *     }
 * the return value is 24 which is number of bytes and not char_length
 * as per the JavaDoc for the column col_char the return value should be 8.
 * But it returns the value 24 which is the number of bytes and not the char_length.
 * solved by 4485954 - updated version of oracle.jdbc.driver.OracleDatabaseMetaData
 *                     in 10.2.0.2.0
 *
 * 3) DatabaseMetaData.getColumns:
 * The driver doesn't return INTEGER data type, instead it returns 3 ("NUMBER")
 * with column precision of 22 and scale 0.
 *
 * 4) databaseMetaData.getTables() does not work for SYNONYMS
 *
 * 5) databaseMetaData.getProcedureColumns will not return the 'null' column for Stored Procedures
 *    that have no arguments, while USER_PROCEDURES (since 9.x) will
 *
 * 6) When making a call java method to a pl/sql stored procedure, register an out parameter of
 * type java.sql.Types.VARCHAR (statement.registerOutParameter(i, Types.VARCHAR). The procedure
 * has an OUT parameter of type VARCHAR2 with a NOCOPY hint (parameter1 OUT NOCOPY  VARCHAR2).
 * The pl/sql procedure throws an exception as soon as the size of 'parameter1' exceeds ~2000
 * characters (~4000 bytes).
 *
 * workaround : use oracle.jdbc.OracleTypes.VARCHAR
 *
 * 7) databaseMetaData.getIndexInfo() does not work for VIEWS, a SQLException with the following
 * "ORA-01702: a view is not appropriate here: can't retrieve indexInfo" is thrown
 * (other platforms just ignore).
 *
 * For IBM AS/400 via JTOpen
 * 1) In the result set returned by DatabaseMetaData.getColumns(), the CHAR_OCTET_LENGTH and
 *    ORDINAL_POSITION columns are not supported in Toolbox versions prior to V5R1 (JTOpen 2.x).
 *    The following workarounds are available:
 *     For CHAR_OCTET_LENGTH, which is supposed to indicate the maximum number of bytes in the column,
 *     use the value in the COLUMN_SIZE. Multiply this value by 2 if the column holds DBCS characters.
 *     For ORDINAL_POSITION, which is supposed to indicate the index of the column in a table,
 *     run a query for all columns in the table ("SELECT * FROM table"), and then issue
 *     ResultSet.findColumn() on the result set to determine the column index from the column name.
 *     In the ResultSetMetaData object, the getTableName() and getSchema() name methods are not
 *     supported in Toolbox versions prior to V5R2 (JTOpen 3.x). In supported Toolbox versions,
 *     these methods work only if the "extended metadata" connection property has been set to True
 *     and only when the target server is at the V5R2 operating system or later. Because retrieving
 *     the additional information may incur a performance penalty, this property is set to False by
 *     default.
 *
 * ODBC-JDBC Bridge
 * 1) for MS-ACCESS getPrimaryKeys not supported
 *
 * Apache Derby (a.k.a. JavaDB)
 * 1) metadata for storedFunctions not available until JDK 6/JDBC 4
 *    (Oracle returns metadata for storedFunctions via databaseMetaData.getProcedures())
 *
 */
public class JDBCHelper extends BaseDBWSBuilderHelper implements DBWSBuilderHelper {

    // useful fields in databaseMetaData.getTables() ResultSet
    public static final int TABLESINFO_CATALOG = 1; // String ==> table catalog (may be null)
    public static final int TABLESINFO_SCHEMA = 2; // String => table schema (may be null)
    public static final int TABLESINFO_NAME = 3; // String => table name
    public static final int TABLESINFO_TYPE = 4; // String => table type. Typical types are:
                                                 // "TABLE", "VIEW", "SYSTEM TABLE",
                                                 // "GLOBAL TEMPORARY", "LOCAL TEMPORARY",
                                                 // "ALIAS", "SYNONYM".
    // useful fields in databaseMetaData.getColumns() ResultSet
    public static final int COLUMNSINFO_COLUMN_NAME = 4; // String => table name
    public static final int COLUMNSINFO_DATA_TYPE = 5; // int => SQL type from java.sql.Types
    public static final int COLUMNSINFO_TYPE_NAME = 6; // String => Data source dependent type name,
                                                       // for a UDT the type name is fully qualified
    public static final int COLUMNSINFO_COLUMN_SIZE = 7; // int => column size. For char or date
                                                         // types, this is the maximum number of
                                                         // characters, for numeric or decimal types
                                                         // this is precision.
    public static final int COLUMNSINFO_DECIMAL_DIGITS = 9; // int => the number of fractional digits
    public static final int COLUMNSINFO_NULLABLE = 11; // int => is NULL allowed?
                                                       //  columnNoNulls - might not allow
                                                       //  columnNullable - definitely allows
                                                       //  columnNullableUnknown - unknown
    public static final int COLUMNSINFO_ORDINAL_POSITION = 17; // int => index of column in table
                                                               // (1-based)
    // useful fields in databaseMetaData.getPrimaryKeys() ResultSet
    public static final int PKSINFO_KEY_SEQ = 5; // KEY_SEQ short => sequence number within primary
                                                 // key
    public static final int PKSINFO_PK_NAME = 6; // PK_NAME String => primary key name (may be null)
    // useful fields in databaseMetaData.getIndexInfo() ResultSet
    public static final int INDEXINFO_NON_UNIQUE = 4; // boolean => Can index values be non-unique.
                                                      // false when TYPE is tableIndexStatistic
    public static final int INDEXINFO_TYPE = 7; // short => index type:
                                                //  tableIndexStatistic - this identifies
                                                //    table statistics that are returned in
                                                //    conjuction with a table's index
                                                //    descriptions
                                                //  tableIndexClustered - this is a
                                                //    clustered index
                                                //  tableIndexHashed - this is a hashed
                                                //    index
                                                //  tableIndexOther - this is some other
                                                //    style of index
    public static final int INDEXINFO_ORDINAL_POSITION = 8; // short => column sequence number
                                                            // within index; zero when TYPE is
                                                            // tableIndexStatistic
    // useful fields in databaseMetaData.getProcedures() ResultSet
    public static final int PROCS_INFO_CATALOG = 1; // String => procedure catalog (may be null)
    public static final int PROCS_INFO_SCHEMA = 2; // String => procedure schema (may be null)
    public static final int PROCS_INFO_NAME   = 3; // String ==> procedure name
    public static final int PROCS_INFO_TYPE = 8; // short => kind of procedure:
                                                 //  procedureResultUnknown - May return a result
                                                 //  procedureNoResult - Does not return a result
                                                 //  procedureReturnsResult - Returns a result
    // useful fields in databaseMetaData.getProcedureColumns() ResultSet
    public static final int PROC_COLS_INFO_CATALOG = 1; // String ==> procedure catalog (may be null)
    public static final int PROC_COLS_INFO_SCHEMA = 2; // String ==> procedure schema (may be null)
    public static final int PROC_COLS_INFO_NAME = 3; // String ==> procedure name
    public static final int PROC_COLS_INFO_COLNAME = 4; // String => column/parameter name
    public static final int PROC_COLS_INFO_TYPE = 5; // Short => kind of column/parameter:
                                                     //  procedureColumnUnknown - nobody knows
                                                     //  procedureColumnIn - IN parameter
                                                     //  procedureColumnInOut - INOUT parameter
                                                     //  procedureColumnOut - OUT parameter
                                                     //  procedureColumnReturn - procedure return value
                                                     //  procedureColumnResult - result column in ResultSet
    public static final int PROC_COLS_INFO_DATA_TYPE = 6; // int => SQL type from java.sql.Types
    public static final int PROC_COLS_INFO_TYPE_NAME = 7; // String => SQL type name, for a UDT type
                                                          // the type name is fully qualified
    public static final int PROC_COLS_INFO_PRECISION = 8; // int => precision
    public static final int PROC_COLS_INFO_LENGTH = 9; // int => length in bytes of data
    public static final int PROC_COLS_INFO_SCALE = 10; // short => scale
    public static final int PROC_COLS_INFO_RADIX = 11; // short => radix
    public static final int PROC_COLS_INFO_NULLABLE = 12; // short => can it contain NULL.
                                                          //  procedureNoNulls - does not allow NULL values
                                                          //  procedureNullable - allows NULL values
                                                          //  procedureNullableUnknown - nullability unknown
    public static final int PROC_COLS_INFO_ORA_SEQUENCE = 14; // Oracle-specific
                                                              //  for package-qualified procedures,
                                                              //  Oracle allows overloading of the
                                                              //  procedure name. This field indicates
                                                              //  the position of the argument (as
                                                              //  argument names may be reused
    public static final int PROC_COLS_INFO_ORA_OVERLOAD = 15; // Oracle-specific
                                                              //  for package-qualified procedures,
                                                              //  Oracle allows overloading of the
                                                              //  procedure name. This field indicates
                                                              //  the 'overload level' - i.e. which
                                                              //  procedure we are dealing with
    //protected List<DbStoredProcedure> dbStoredProcedures = new ArrayList<DbStoredProcedure>();
    //protected Map<DbStoredProcedure, DbStoredProcedureNameAndModel> dbStoredProcedure2QueryName =
    //    new HashMap<DbStoredProcedure, DbStoredProcedureNameAndModel>();

    public JDBCHelper(DBWSBuilder dbwsBuilder) {
        super(dbwsBuilder);
    }

    public boolean hasTables() {
        if (dbTables.size() == 0) {
            return false;
        }
        return true;
    }

    public boolean hasComplexProcedureArgs() {
        return false;
    }

    public void buildProcedureOperation(ProcedureOperationModel procedureOperationModel) {
        String name = procedureOperationModel.getName();
        boolean isMySQL = dbwsBuilder.getDatabasePlatform().getClass().getName().contains("MySQL");
        for (ProcedureType storedProcedure : procedureOperationModel.getDbStoredProcedures()) {
            StringBuilder sb = new StringBuilder();
            if (name == null || name.length() == 0) {
                if (storedProcedure.getOverload() > 0) {
                    sb.append(storedProcedure.getOverload());
                    sb.append('_');
                }
                if (storedProcedure.getCatalogName() != null && storedProcedure.getCatalogName().length() > 0) {
                    sb.append(storedProcedure.getCatalogName());
                    sb.append('_');
                }
                if (storedProcedure.getSchema() != null && storedProcedure.getSchema().length() > 0) {
                    sb.append(storedProcedure.getSchema());
                    sb.append('_');
                }
                sb.append(storedProcedure.getProcedureName());
            }
            else {
                sb.append(name);
            }
            QueryOperation qo = new QueryOperation();
            qo.setName(sb.toString());
            QueryHandler qh;
            if (storedProcedure.isFunctionType()) {
                qh = new StoredFunctionQueryHandler();
            }
            else {
              qh = new StoredProcedureQueryHandler();
            }
            sb = new StringBuilder();
            if (!isMySQL) {
                if (storedProcedure.getCatalogName() != null && storedProcedure.getCatalogName().length() > 0) {
                    sb.append(storedProcedure.getCatalogName());
                    sb.append('.');
                }
            }
            if (storedProcedure.getSchema() != null && storedProcedure.getSchema().length() > 0) {
                sb.append(storedProcedure.getSchema());
                sb.append('.');
            }
            sb.append(storedProcedure.getProcedureName());
            ((StoredProcedureQueryHandler)qh).setName(sb.toString());
            dbwsBuilder.logMessage(FINEST, "Building QueryOperation for " + sb.toString());
            // before assigning queryHandler, check for named query in OR project
            List<DatabaseQuery> queries = dbwsBuilder.getOrProject().getQueries();
            if (queries.size() > 0) {
                for (DatabaseQuery q : queries) {
                    if (q.getName().equals(qo.getName())) {
                        qh = new NamedQueryHandler();
                        ((NamedQueryHandler)qh).setName(qo.getName());
                    }
                }
            }
            qo.setQueryHandler(qh);
            String returnType = procedureOperationModel.getReturnType();
            boolean isCollection = procedureOperationModel.isCollection();
            boolean isSimpleXMLFormat = procedureOperationModel.isSimpleXMLFormat();
            Result result = null;
            if (storedProcedure.isFunctionType()) {
                FunctionType storedFunction = (FunctionType) storedProcedure;
                DatabaseType rarg = storedFunction.getReturnArgument();
                if (rarg.getTypeName().contains("CURSOR")) {
                    result = new CollectionResult();
                    result.setType(SXF_QNAME_CURSOR);
                }
                else {
                    result = new Result();
                    int rargJdbcType = Util.getJDBCTypeFromTypeName(rarg.getTypeName());
                    switch (rargJdbcType) {
                        case STRUCT:
                        case ARRAY:
                        case OTHER:
                            if (returnType != null) {
                                result.setType(buildCustomQName(returnType, dbwsBuilder));
                            }
                            else {
                                result.setType(ANY_QNAME);
                            }
                            break;
                        default :
                            result.setType(getXMLTypeFromJDBCType(rargJdbcType));
                            break;
                    }
                }
            }
            else {
                // if user overrides returnType, assume they're right
                if (returnType != null) {
                    result = new Result();
                    result.setType(buildCustomQName(returnType, dbwsBuilder));
                }
                else {
                    if (isCollection) {
                        result = new CollectionResult();
                        if (isSimpleXMLFormat) {
                            result.setType(SXF_QNAME_CURSOR);
                        }
                    }
                    else {
                        result = new Result();
                        result.setType(SXF_QNAME);
                    }
                }
            }
            if (procedureOperationModel.getBinaryAttachment()) {
                Attachment attachment = new Attachment();
                attachment.setMimeType("application/octet-stream");
                result.setAttachment(attachment);
            }
            for (ArgumentType arg : storedProcedure.getArguments()) {
                String argName = arg.getArgumentName();
                if (argName != null) {
                    ProcedureArgument pa = null;
                    Parameter parm = null;
                    ArgumentTypeDirection direction = arg.getDirection();
                    QName xmlType = null;
                    switch (Util.getJDBCTypeFromTypeName(arg.getTypeName())) {
                        case STRUCT:
                        case ARRAY:
                        case OTHER:
                            String typeString = nct.generateSchemaAlias(arg.getTypeName());
                            xmlType = buildCustomQName(typeString, dbwsBuilder);
                            break;
                        default :
                            xmlType = getXMLTypeFromJDBCType(Util.getJDBCTypeFromTypeName(arg.getTypeName()));
                            break;
                    }
                    if (direction == IN) {
                        parm = new Parameter();
                        parm.setName(argName);
                        parm.setType(xmlType);
                        pa = new ProcedureArgument();
                        pa.setName(argName);
                        pa.setParameterName(argName);
                        if (qh instanceof StoredProcedureQueryHandler) {
                            ((StoredProcedureQueryHandler)qh).getInArguments().add(pa);
                        }
                    }
                    else {
                        // the first OUT/INOUT arg determines singleResult vs. collectionResult
                        pa = new ProcedureOutputArgument();
                        ProcedureOutputArgument pao = (ProcedureOutputArgument)pa;
                        pao.setName(argName);
                        pao.setParameterName(argName);
                        if (arg.getTypeName().contains("CURSOR") &&
                            returnType == null) { // if user overrides returnType, assume they're right
                            pao.setResultType(SXF_QNAME_CURSOR);
                            if (result == null) {
                                result = new CollectionResult();
                                result.setType(SXF_QNAME_CURSOR);
                            }
                        }
                        else {
                            // if user overrides returnType, assume they're right
                            // Hmm, multiple OUT's gonna be a problem - later!
                            if (returnType != null && !isSimpleXMLFormat) {
                                xmlType = qNameFromString("{" + dbwsBuilder.getTargetNamespace() + "}" +
                                    returnType, dbwsBuilder.getSchema());
                            }
                            pao.setResultType(xmlType);
                            if (result == null) {
                                if (isCollection) {
                                    result = new CollectionResult();
                                }
                                else {
                                    result = new Result();
                                }
                                result.setType(xmlType);
                            }
                        }
                        if (direction == INOUT) {
                            parm = new Parameter();
                            parm.setName(argName);
                            parm.setType(xmlType);
                            result.setType(xmlType);
                            // use of INOUT precludes SimpleXMLFormat
                            isSimpleXMLFormat = false;

                            if (qh instanceof StoredProcedureQueryHandler) {
                                ((StoredProcedureQueryHandler)qh).getInOutArguments().add(pao);
                            }
                        }
                        else {
                            if (qh instanceof StoredProcedureQueryHandler) {
                                ((StoredProcedureQueryHandler)qh).getOutArguments().add(pao);
                            }
                        }
                    }
                    if (parm != null) {
                        qo.getParameters().add(parm);
                    }
                }
            }

            handleSimpleXMLFormat(isSimpleXMLFormat, result, procedureOperationModel);
            qo.setResult(result);
            dbwsBuilder.getXrServiceModel().getOperations().put(qo.getName(), qo);
        }
        finishProcedureOperation();
    }

    protected List<TableType> loadTables(List<String> catalogPatterns, List<String> schemaPatterns,
        List<String> tableNamePatterns) {
        List<TableType> tables = new ArrayList<TableType>();
        for (int i = 0, len = catalogPatterns.size(); i < len; i++) {
            String catalogPattern = catalogPatterns.get(i);
            String schemaPattern = schemaPatterns.get(i);
            String tablePattern = tableNamePatterns.get(i);
            tables.addAll(loadTables(catalogPattern, schemaPattern, tablePattern));
        }
        return tables;
    }
    protected List<TableType> loadTables(String originalCatalogPattern, String originalSchemaPattern,
        String originalTablePattern) {
        List<TableType> dbTables = null;
        String schemaPattern = escapePunctuation(originalSchemaPattern);
        String tablePattern = escapePunctuation(originalTablePattern);
        DatabaseMetaData databaseMetaData = getDatabaseMetaData(dbwsBuilder.getConnection());
        boolean supportsCatalogsInTableDefinitions = true;
        try {
            supportsCatalogsInTableDefinitions =
                databaseMetaData.supportsCatalogsInTableDefinitions();
        }
        catch (SQLException sqlException) { /* ignore*/ }
        String catalogPattern = escapePunctuation(
            supportsCatalogsInTableDefinitions ? originalCatalogPattern : "");
        // Make sure table(s) is/are available
        ResultSet tablesInfo = null;
        try {
            tablesInfo = databaseMetaData.getTables(catalogPattern, schemaPattern, tablePattern, null);
        }
        catch (SQLException sqlException) {
            throw new IllegalStateException("failure retrieving JDBC table metadata", sqlException);
        }
        // did we get a hit?
        if (tablesInfo != null) {
            dbTables = new ArrayList<TableType>();
            try {
                while (tablesInfo.next()) {
                    String actualTableCatalog = null;
                    try {
                        actualTableCatalog = tablesInfo.getString(TABLESINFO_CATALOG);
                    }
                    catch (SQLException sqlException) {
                        // we can live without catalog info
                    }
                    String actualTableSchema = null;
                    try {
                        actualTableSchema = tablesInfo.getString(TABLESINFO_SCHEMA);
                    }
                    catch (SQLException sqlException) {
                        // we can live without schema info
                    }
                    String actualTableName = tablesInfo.getString(TABLESINFO_NAME);
                    String tableType = null;
                    try {
                        tableType = tablesInfo.getString(TABLESINFO_TYPE);
                    }
                    catch (SQLException sqlException) {
                        // we can live without table type - assume TABLE
                    }
                    DbTable dbTable = new DbTable();
                    dbTable.setCatalog(actualTableCatalog);
                    dbTable.setSchema(actualTableSchema);
                    dbTable.setTableName(actualTableName);
                    if (tableType != null) {
                        dbTable.setType(tableType);
                    }
                    dbTables.add(dbTable);
                    ResultSet columnInfo = databaseMetaData.getColumns(actualTableCatalog,
                        actualTableSchema, actualTableName, "%");
                    while (columnInfo.next()) {
                        String columnName = columnInfo.getString(COLUMNSINFO_COLUMN_NAME);
                        DbColumn dbColumn = new DbColumn(columnName);
                        int dbPrecision = -1;
                        try {
                            dbPrecision = columnInfo.getInt(COLUMNSINFO_COLUMN_SIZE);
                        }
                        catch (NumberFormatException nfe) {
                            // ignore
                        }
                        int dbScale = columnInfo.getInt(COLUMNSINFO_DECIMAL_DIGITS);
                        if (columnInfo.getInt(COLUMNSINFO_NULLABLE) == columnNullable) {
                            dbColumn.unSetNotNull();
                        }
                        else {
                            dbColumn.setNotNull();
                        }
                        dbColumn.setJDBCType(columnInfo.getInt(COLUMNSINFO_DATA_TYPE));
                        dbColumn.setJDBCTypeName(columnInfo.getString(COLUMNSINFO_TYPE_NAME));
                        dbColumn.setEnclosedType(buildTypeForJDBCType(dbColumn.getJDBCType(),
                            dbPrecision, dbScale));
                        dbTable.getColumns().add(dbColumn);
                    }
                    columnInfo.close();
                    ResultSet pksInfo = databaseMetaData.getPrimaryKeys(actualTableCatalog,
                        actualTableSchema, actualTableName);
                    if (pksInfo != null) {
                        while (pksInfo.next()) {
                            short keySeq = pksInfo.getShort(PKSINFO_KEY_SEQ);
                            String pkConstraintName = pksInfo.getString(PKSINFO_PK_NAME);
                            DbColumn dbColumn = (DbColumn)dbTable.getColumns().get(keySeq - 1);
                            dbColumn.setPk();
                            dbColumn.setPkConstraintName(pkConstraintName);
                        }
                    }
                    pksInfo.close();
                    try {
                        ResultSet indexInfo = databaseMetaData.getIndexInfo(actualTableCatalog,
                            actualTableSchema, actualTableName, true, false);
                        if (indexInfo != null) {
                            while (indexInfo.next()) {
                                boolean nonUnique = indexInfo.getBoolean(INDEXINFO_NON_UNIQUE);
                                if (!nonUnique) { // reverse logic!
                                    short type = indexInfo.getShort(INDEXINFO_TYPE);
                                    if (type != tableIndexStatistic) {
                                        short pos = indexInfo.getShort(INDEXINFO_ORDINAL_POSITION);
                                        DbColumn dbColumn = (DbColumn)dbTable.getColumns().get(pos - 1);
                                        if (!dbColumn.pk()) {
                                            dbColumn.setUnique(true);
                                        }
                                    }
                                }
                            }
                        }
                        indexInfo.close();
                    }
                    catch (SQLException sqlException) {
                        //TODO - do we still need this if we (JDBCHelper) isn't handling Oracle?
                        // sqlException.printStackTrace();
                        // ORA-01702: a view is not appropriate here: can't retrieve indexInfo
                        // for views on Oracle, it blows up (other platforms just ignore)
                    }
                }
                tablesInfo.close();
            }
            catch (SQLException sqlException) {
                throw new IllegalStateException("failure retrieving JDBC table metadata",
                    sqlException);
            }
        }
        return dbTables;
    }

    protected List<ProcedureType> loadProcedures(List<String> catalogPatterns,
        List<String> schemaPatterns, List<String> procedureNamePatterns) {
        List<ProcedureType> procedures = new ArrayList<ProcedureType>();
        for (int i = 0, len = catalogPatterns.size(); i < len; i++) {
            String catalogPattern = catalogPatterns.get(i);
            String schemaPattern = schemaPatterns.get(i);
            String tablePattern = procedureNamePatterns.get(i);
            List<ProcedureType> procs = loadProcedures(catalogPattern, schemaPattern, tablePattern);
            if (procs != null) {
                procedures.addAll(loadProcedures(catalogPattern, schemaPattern, tablePattern));
            }
        }
        return procedures;
    }
    protected List<ProcedureType> loadProcedures(String originalCatalogPattern,
        String originalSchemaPattern, String originalProcedurePattern) {
        List<ProcedureType> dbStoredProcedures = null;
        boolean catalogMatchDontCare = false;
        DatabasePlatform platform = dbwsBuilder.getDatabasePlatform();
        if (platform instanceof MySQLPlatform ||
            platform instanceof DerbyPlatform ||
            platform instanceof PostgreSQLPlatform ) {
            // TODO - get info on other platforms that also require catalogMatchDontCare = true
            catalogMatchDontCare = true;
        }
        String catalogPattern = escapePunctuation(originalCatalogPattern);
        String schemaPattern = escapePunctuation(originalSchemaPattern);
        String procedurePattern = escapePunctuation(originalProcedurePattern);
        // Make sure procedure(s) is/are available
        ResultSet procsInfo = null;
        try {
            DatabaseMetaData databaseMetaData = getDatabaseMetaData(dbwsBuilder.getConnection());
            procsInfo = databaseMetaData.getProcedures(catalogPattern, schemaPattern, procedurePattern);
            // did we get a hit?
            if (procsInfo != null) {
                List<ProcedureType> tmpProcs = new ArrayList<ProcedureType>();
                while (procsInfo.next()) {
                    String actualCatalogName = procsInfo.getString(PROCS_INFO_CATALOG);
                    String actualSchemaName = procsInfo.getString(PROCS_INFO_SCHEMA);
                    String actualProcedureName = procsInfo.getString(PROCS_INFO_NAME);
                    short procedureType = procsInfo.getShort(PROCS_INFO_TYPE);
                    ProcedureType dbStoredProcedure;
                    if (procedureType == procedureReturnsResult) {
                        dbStoredProcedure = new FunctionType(actualProcedureName);
                    }
                    else {
                        dbStoredProcedure = new ProcedureType(actualProcedureName);
                    }
                    if (actualCatalogName != null && actualCatalogName.length() > 0) {
                        dbStoredProcedure.setCatalogName(actualCatalogName);
                    }
                    if (actualSchemaName != null && actualSchemaName.length() > 0) {
                        dbStoredProcedure.setSchema(actualSchemaName);
                    }
                    tmpProcs.add(dbStoredProcedure);
                }
                procsInfo.close();
                /* new a temp bucket to hold DbStoredArgs until they can be sorted out with respect
                 * to which DbStoredProcedure owns which args; this has to be done because Oracle can
                 * return multiple hits across multiple packages for the same procedureName.
                 */
                int numProcs = tmpProcs.size();
                if (numProcs > 0) {
                    dbStoredProcedures = new ArrayList<ProcedureType>(numProcs);
                    ResultSet procedureColumnsInfo = null;
                    procedureColumnsInfo = databaseMetaData.getProcedureColumns(catalogPattern,
                        schemaPattern, procedurePattern, "%");
                    while (procedureColumnsInfo.next()) {
                        String actualCatalogName = procedureColumnsInfo.getString(PROC_COLS_INFO_CATALOG);
                        String actualSchemaName = procedureColumnsInfo.getString(PROC_COLS_INFO_SCHEMA);
                        String actualProcedureName = procedureColumnsInfo.getString(PROC_COLS_INFO_NAME);
                        String argName = procedureColumnsInfo.getString(PROC_COLS_INFO_COLNAME);
                        // some MySql drivers return empty string, some return null: set to emptyString regardless
                        if (argName == null) {
                            argName = "";
                        }
                        ArgumentType dbStoredArgument = new ArgumentType(argName);
                        short inOut = procedureColumnsInfo.getShort(PROC_COLS_INFO_TYPE);
                        if (inOut == procedureColumnInOut) {
                            dbStoredArgument.setDirection(INOUT);
                        }
                        else if (inOut == procedureColumnOut) {
                            dbStoredArgument.setDirection(OUT);
                        }
                        else if (inOut == procedureColumnReturn) {
                            dbStoredArgument.setDirection(RETURN);
                        } else {
                            // default to ArgumentTypeDirection.IN
                            dbStoredArgument.setDirection(IN);
                        }

                        int jdbcType = procedureColumnsInfo.getInt(PROC_COLS_INFO_DATA_TYPE);
                        int precision = procedureColumnsInfo.getInt(PROC_COLS_INFO_PRECISION);
                        int scale = procedureColumnsInfo.getInt(PROC_COLS_INFO_SCALE);

                        dbStoredArgument.setEnclosedType(buildTypeForJDBCType(jdbcType, precision, scale));

                        // find matching DbStoredProcedure
                        // this dbStoredArgument belongs to a 'regular' procedure
                        ProcedureType matchingProc = null;
                        for (int i = 0; i < tmpProcs.size();) {
                            ProcedureType tmpProc = tmpProcs.get(i);
                            if (matches(tmpProc, actualCatalogName, actualSchemaName,
                                actualProcedureName, false, catalogMatchDontCare)) {
                                matchingProc = tmpProc;
                                dbStoredProcedures.add(matchingProc);
                                break;
                            }
                            i++;
                        }
                        if (matchingProc == null) {
                            // look in dbStoredProcedures - matching proc already moved over ?
                            for (ProcedureType dbStoredProcedure: dbStoredProcedures) {
                                if (matches(dbStoredProcedure, actualCatalogName, actualSchemaName,
                                    actualProcedureName, false, catalogMatchDontCare)) {
                                    matchingProc = dbStoredProcedure;
                                    break;
                                }
                            }
                        }
                        if (matchingProc != null) {
                            if (matchingProc.isFunctionType() && dbStoredArgument.getArgumentName().equalsIgnoreCase("")) {
                                ((FunctionType)matchingProc).setReturnArgument(dbStoredArgument);
                            }
                            else {
                                matchingProc.getArguments().add(dbStoredArgument);
                            }
                            tmpProcs.remove(matchingProc);
                        }
                        // else some argument that doesn't have a matching proc? ignore for now
                    }
                    procedureColumnsInfo.close();
                    if (!tmpProcs.isEmpty()) {
                        // leftovers are the no-arg procedures
                        dbStoredProcedures.addAll(tmpProcs);
                    }
                }
            }
        }
        catch (SQLException sqlException) {
            throw new IllegalStateException("failure retrieving Stored Procedure metadata",
                sqlException);
        }
        if (dbStoredProcedures != null && !dbStoredProcedures.isEmpty()) {
            Collections.sort(dbStoredProcedures, new Comparator<ProcedureType>() {
                public int compare(ProcedureType o1, ProcedureType o2) {
                    String name1 = o1.getProcedureName();
                    String name2 = o2.getProcedureName();
                    if (!name1.equals(name2)) {
                        return name1.compareTo(name2);
                    }
                    else {
                        return o1.getOverload() - o2.getOverload();
                    }
                }
            });
        }
        return dbStoredProcedures;
    }

    static DatabaseMetaData getDatabaseMetaData(Connection connection) {
    	if (connection == null) {
            // without a connection we cannot retrieve the metadata
            throw new IllegalStateException("Connection is null - cannot retrieve JDBC metadata");
    	}
        DatabaseMetaData databaseMetaData = null;
        try {
            databaseMetaData = connection.getMetaData();
        } catch (SQLException sqlException) {
            // without metadata, there is nothing to do here
            throw new IllegalStateException("failure retrieving JDBC metadata", sqlException);
        }
        return databaseMetaData;
    }

    public static boolean matches(ProcedureType proc, String catalog, String schema, String name, boolean isOracle,
            boolean catalogMatchDontCare) {

        // return true if all 3 match, sorta

        boolean catalogMatch =
                proc.getCatalogName() == null ?
                // for Oracle, catalog matching is 'dont-care' only if null
                (isOracle ? true :
                // other platforms: null has to match null
               (catalog == null))
                : proc.getCatalogName().equals(catalog);
        // but catalogDontCare trumps!
        if (catalogMatchDontCare) {
            catalogMatch = true;
        }
        boolean schemaMatch =
            // either they are both null or they match
            proc.getSchema() == null ? (schema == null)
                : proc.getSchema().equals(schema);
        boolean nameMatch =
            // either they are both null or they match
            proc.getProcedureName() == null ? (name == null)
                : proc.getProcedureName().equals(name);
        return catalogMatch && schemaMatch && nameMatch;
    }

    @Override
    public void addToOROXProjectsForComplexTypes(List<CompositeDatabaseType> types, Project orProject, Project oxProject) {
        // TODO - handle complex types
    }
    @Override
    protected void buildQueryForProcedureType(ProcedureType procType, Project orProject, Project oxProject, ProcedureOperationModel opModel, boolean hasComplexArgs) {
        // TODO - handle queries
    }
}