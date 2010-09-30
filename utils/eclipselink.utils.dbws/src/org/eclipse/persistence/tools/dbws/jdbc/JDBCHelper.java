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

package org.eclipse.persistence.tools.dbws.jdbc;

// Javase imports
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import static java.sql.DatabaseMetaData.columnNullable;
import static java.sql.DatabaseMetaData.procedureReturnsResult;
import static java.sql.DatabaseMetaData.tableIndexStatistic;
import static java.sql.DatabaseMetaData.procedureNullable;
import static java.sql.DatabaseMetaData.procedureColumnInOut;
import static java.sql.DatabaseMetaData.procedureColumnOut;
import static java.sql.DatabaseMetaData.procedureColumnReturn;

// Java extension imports

// EclipseLink imports
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.platform.database.DerbyPlatform;
import org.eclipse.persistence.platform.database.MySQLPlatform;
import org.eclipse.persistence.platform.database.PostgreSQLPlatform;
import org.eclipse.persistence.tools.dbws.ProcedureOperationModel;
import static org.eclipse.persistence.tools.dbws.Util.InOut.INOUT;
import static org.eclipse.persistence.tools.dbws.Util.InOut.OUT;
import static org.eclipse.persistence.tools.dbws.Util.InOut.RETURN;
import static org.eclipse.persistence.tools.dbws.Util.escapePunctuation;

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
public class JDBCHelper {

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

    public static List<DbTable> buildDbTable(Connection connection, DatabasePlatform platform,
        String originalCatalogPattern, String originalSchemaPattern, String originalTablePattern) {

        List<DbTable> dbTables = null;
        boolean isOracle = platform.getClass().getName().contains("Oracle") ? true : false;
        String schemaPattern = escapePunctuation(originalSchemaPattern, isOracle);
        String tablePattern = escapePunctuation(originalTablePattern, isOracle);
        DatabaseMetaData databaseMetaData = getDatabaseMetaData(connection);
        boolean supportsCatalogsInTableDefinitions = true;
        try {
            supportsCatalogsInTableDefinitions =
                databaseMetaData.supportsCatalogsInTableDefinitions();
        }
        catch (SQLException sqlException) { /* ignore*/ }
        String catalogPattern = escapePunctuation(
            supportsCatalogsInTableDefinitions ? originalCatalogPattern : "", isOracle);
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
            dbTables = new ArrayList<DbTable>();
            try {
                while (tablesInfo.next()) {
                    String actualTableCatalog = null;
                    try {
                        actualTableCatalog = tablesInfo.getString(TABLESINFO_CATALOG);
                    }
                    catch (SQLException sqlException) {
                        // we can live without catalog info
                    }
                    if (actualTableCatalog != null && actualTableCatalog.length() == 0) {
                        if (isOracle) { // Oracle requires NULL, not "" empty string
                            actualTableCatalog = null;
                        }
                    }
                    String actualTableSchema = null;
                    try {
                        actualTableSchema = tablesInfo.getString(TABLESINFO_SCHEMA);
                    } catch (SQLException sqlException) {
                        // we can live without schema info
                    }
                    if (actualTableSchema != null && actualTableSchema.length() == 0) {
                        if (isOracle) { // Oracle requires NULL, not "" empty string
                            actualTableSchema = null;
                        }
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
                    dbTable.setName(actualTableName);
                    if (tableType != null) {
                        dbTable.setType(tableType);
                    }
                    dbTables.add(dbTable);
                    ResultSet columnInfo = databaseMetaData.getColumns(actualTableCatalog,
                        actualTableSchema, actualTableName, "%");
                    while (columnInfo.next()) {
                        DbColumn dbColumn = new DbColumn();
                        dbColumn.setName(columnInfo.getString(COLUMNSINFO_COLUMN_NAME));
                        dbColumn.setOrdinalPosition(columnInfo.getInt(COLUMNSINFO_ORDINAL_POSITION));
                        try {
                            dbColumn.setPrecision(columnInfo.getInt(COLUMNSINFO_COLUMN_SIZE));
                        }
                        catch (NumberFormatException nfe) {
                            // set precision to be -1 to indicate 'too big'
                            dbColumn.setPrecision(-1);
                        }
                        dbColumn.setScale(columnInfo.getInt(COLUMNSINFO_DECIMAL_DIGITS));
                        dbColumn.setNullable(
                          columnInfo.getInt(COLUMNSINFO_NULLABLE) == columnNullable ? true : false);
                        dbColumn.setJDBCType(columnInfo.getInt(COLUMNSINFO_DATA_TYPE));
                        dbColumn.setJDBCTypeName(columnInfo.getString(COLUMNSINFO_TYPE_NAME));
                        dbTable.getColumns().add(dbColumn.getOrdinalPosition() - 1, dbColumn);
                    }
                    columnInfo.close();
                    ResultSet pksInfo = databaseMetaData.getPrimaryKeys(actualTableCatalog,
                        actualTableSchema, actualTableName);
                    if (pksInfo != null) {
                        while (pksInfo.next()) {
                            short keySeq = pksInfo.getShort(PKSINFO_KEY_SEQ);
                            String pkConstraintName = pksInfo.getString(PKSINFO_PK_NAME);
                            DbColumn dbColumn = dbTable.getColumns().get(keySeq - 1);
                            dbColumn.setPK(true);
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
                                        DbColumn dbColumn = dbTable.getColumns().get(pos - 1);
                                        if (!dbColumn.isPK()) {
                                            dbColumn.setUnique(true);
                                        }
                                    }
                                }
                            }
                        }
                        indexInfo.close();
                    } catch (SQLException sqlException) {
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

    public static List<DbStoredProcedure> buildStoredProcedure(Connection connection,
        DatabasePlatform platform, ProcedureOperationModel procedureModel ) {

        List<DbStoredProcedure> dbStoredProcedures = null;
        boolean catalogMatchDontCare = false;
        if (platform instanceof MySQLPlatform || platform instanceof DerbyPlatform ||
        	platform instanceof PostgreSQLPlatform ) {
        	// TODO - get info on other platforms that also require catalogMatchDontCare = true
        	catalogMatchDontCare = true;
        }
        // Oracle is 'special' - the catalogMatchDontCare logic only applies if the catalogPattern
        // is NULL vs. the empty "" string
        boolean isOracle = platform.getClass().getName().contains("Oracle") ? true : false;
        String originalCatalogPattern = procedureModel.getCatalogPattern();
        String originalSchemaPattern = procedureModel.getSchemaPattern();
        String originalProcedurePattern = procedureModel.getProcedurePattern();
        String catalogPattern = escapePunctuation(originalCatalogPattern, isOracle);
        String schemaPattern = escapePunctuation(originalSchemaPattern, isOracle);
        String procedurePattern = escapePunctuation(originalProcedurePattern, isOracle);
        // Make sure procedure(s) is/are available
        ResultSet procsInfo = null;
        try {
            DatabaseMetaData databaseMetaData = getDatabaseMetaData(connection);
            procsInfo = databaseMetaData.getProcedures(catalogPattern, schemaPattern, procedurePattern);
            // did we get a hit?
            if (procsInfo != null) {
                List<DbStoredProcedure> tmpProcs = new ArrayList<DbStoredProcedure>();
                while (procsInfo.next()) {
                    String actualCatalogName = procsInfo.getString(PROCS_INFO_CATALOG);
                    String actualSchemaName = procsInfo.getString(PROCS_INFO_SCHEMA);
                    String actualProcedureName = procsInfo.getString(PROCS_INFO_NAME);
                    short procedureType = procsInfo.getShort(PROCS_INFO_TYPE);
                    DbStoredProcedure dbStoredProcedure;
                    if (procedureType == procedureReturnsResult) {
                        dbStoredProcedure = new DbStoredFunction(actualProcedureName);
                    }
                    else {
                        dbStoredProcedure = new DbStoredProcedure(actualProcedureName);
                    }
                    if (actualCatalogName != null && actualCatalogName.length() > 0) {
                        dbStoredProcedure.setCatalog(actualCatalogName);
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
                    dbStoredProcedures = new ArrayList<DbStoredProcedure>(numProcs);
                    List<OverloadHolder> overloadHolderList = new ArrayList<OverloadHolder>();
                    ResultSet procedureColumnsInfo = null;
                    procedureColumnsInfo = databaseMetaData.getProcedureColumns(catalogPattern,
                        schemaPattern, procedurePattern, "%");
                    while (procedureColumnsInfo.next()) {
                        String actualCatalogName = procedureColumnsInfo.getString(PROC_COLS_INFO_CATALOG);
                        String actualSchemaName = procedureColumnsInfo.getString(PROC_COLS_INFO_SCHEMA);
                        String actualProcedureName = procedureColumnsInfo.getString(PROC_COLS_INFO_NAME);
                        String argName = procedureColumnsInfo.getString(PROC_COLS_INFO_COLNAME);
                        DbStoredArgument dbStoredArgument = new DbStoredArgument(argName);
                        short inOut = procedureColumnsInfo.getShort(PROC_COLS_INFO_TYPE);
                        if (inOut == procedureColumnInOut) {
                            dbStoredArgument.setInOut(INOUT);
                        }
                        else if (inOut == procedureColumnOut) {
                            dbStoredArgument.setInOut(OUT);
                        }
                        else if (inOut == procedureColumnReturn) {
                            dbStoredArgument.setInOut(RETURN);
                        }
                        dbStoredArgument.setJdbcType(procedureColumnsInfo.getInt(
                            PROC_COLS_INFO_DATA_TYPE));
                        dbStoredArgument.setJdbcTypeName(procedureColumnsInfo.getString(
                            PROC_COLS_INFO_TYPE_NAME));
                        dbStoredArgument.setPrecision(procedureColumnsInfo.getInt(
                            PROC_COLS_INFO_PRECISION));
                        dbStoredArgument.setScale(procedureColumnsInfo.getInt(
                            PROC_COLS_INFO_SCALE));
                        dbStoredArgument.setRadix(procedureColumnsInfo.getShort(
                            PROC_COLS_INFO_RADIX));
                        dbStoredArgument.setNullable(procedureColumnsInfo.getShort(
                            PROC_COLS_INFO_NULLABLE) == procedureNullable ? true
                            : false);
                        if (isOracle) {
                            dbStoredArgument.setSeq(procedureColumnsInfo.getShort(
                                PROC_COLS_INFO_ORA_SEQUENCE));
                        }
                        short overload = isOracle ?
                            procedureColumnsInfo.getShort(PROC_COLS_INFO_ORA_OVERLOAD) : 0;
                        // find matching DbStoredProcedure
                        if (overload == 0) {
                            // this dbStoredArgument belongs to a 'regular' procedure
                            DbStoredProcedure matchingProc = null;
                            for (int i = 0; i < tmpProcs.size();) {
                            	DbStoredProcedure tmpProc = tmpProcs.get(i);
                                if (tmpProc.matches(actualCatalogName, actualSchemaName,
                                    actualProcedureName, isOracle, catalogMatchDontCare)) {
                                	matchingProc = tmpProc;
                                    dbStoredProcedures.add(matchingProc);
                                    break;
                                }
                                i++;
                            }
                            if (matchingProc == null) {
                                // look in dbStoredProcedures - matching proc already moved over ?
                                for (DbStoredProcedure dbStoredProcedure: dbStoredProcedures) {
                                    if (dbStoredProcedure.matches(actualCatalogName, actualSchemaName,
                                        actualProcedureName, isOracle, catalogMatchDontCare)) {
                                        matchingProc = dbStoredProcedure;
                                        break;
                                    }
                                }
                            }
                            if (matchingProc != null) {
                                if (matchingProc.isFunction() &&
                                   (isOracle ? (dbStoredArgument.getName() == null) :
                                    (dbStoredArgument.getName().equalsIgnoreCase(""))) &&
                                    dbStoredArgument.getSeq() == (isOracle ? 1 : 0)) {
                                    ((DbStoredFunction)matchingProc).setReturnArg(dbStoredArgument);
                                }
                                else {
                                    matchingProc.getArguments().add(dbStoredArgument);
                                }
                                tmpProcs.remove(matchingProc);
                            }
                            // else some argument that doesn't have a matching proc? ignore for now
                        }
                        else {
                            // this dbStoredArgument belongs to an overloaded procedure
                            OverloadHolder overloadHolder = null;
                            for (Iterator<OverloadHolder> i = overloadHolderList.iterator(); i.hasNext();) {
                                OverloadHolder ovrldhldr = i.next();
                                if (ovrldhldr.overload == overload &&
                                    ovrldhldr.packageName.equals(actualCatalogName) &&
                                    ovrldhldr.procedureSchema.equals(actualSchemaName) &&
                                    ovrldhldr.procedureName.equals(actualProcedureName)) {
                                    overloadHolder = ovrldhldr;
                                    break;
                                }
                            }
                            if (overloadHolder == null) {
                                // need to create a new one
                                overloadHolder = new OverloadHolder(actualCatalogName, actualSchemaName,
                                    actualProcedureName, overload);
                                overloadHolderList.add(overloadHolder);
                            }
                            overloadHolder.getArgs().add(dbStoredArgument);
                        }
                    }
                    procedureColumnsInfo.close();
                    for (Iterator<OverloadHolder> i = overloadHolderList.iterator(); i.hasNext();) {
                        OverloadHolder ovrldhldr = i.next();
                        Collections.sort(ovrldhldr.getArgs(), new Comparator<DbStoredArgument>() {
                            public int compare(DbStoredArgument o1, DbStoredArgument o2) {
                                return o1.getSeq() - o2.getSeq();
                            }
                        });
                        DbStoredProcedure matchingProc = null;
                        // find a matching proc from dbStoredProcedures
                        for (int j = 0; j < tmpProcs.size();) {
                            DbStoredProcedure dbStoredProcedure = tmpProcs.get(j);
                            if (dbStoredProcedure.matches(ovrldhldr.packageName,
                                ovrldhldr.procedureSchema, ovrldhldr.procedureName, true, false)) {
                                // check for function/procedures with same names
                                DbStoredArgument firstArg = ovrldhldr.getArgs().get(0);
                                if (firstArg.getName() == null && firstArg.getSeq() == 1) {
                                    // need a DbStoredFunction
                                    if (dbStoredProcedure.isFunction()) {
                                        matchingProc = tmpProcs.remove(j);
                                        break;
                                    }
                                }
                                else {
                                    matchingProc = tmpProcs.remove(j);
                                    break;
                                }
                            }
                            j++;
                        }
                        if (matchingProc != null) {
                            if (matchingProc.isFunction()) {
                                DbStoredArgument returnArg = ovrldhldr.getArgs().remove(0);
                                ((DbStoredFunction)matchingProc).setReturnArg(returnArg);
                            }
                            matchingProc.setOverload(ovrldhldr.overload);
                            matchingProc.getArguments().addAll(ovrldhldr.getArgs());
                            dbStoredProcedures.add(matchingProc);
                        }
                    }
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
            Collections.sort(dbStoredProcedures, new Comparator<DbStoredProcedure>() {
                public int compare(DbStoredProcedure o1, DbStoredProcedure o2) {
                    String name1 = o1.getName();
                    String name2 = o2.getName();
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

    public static DatabaseMetaData getDatabaseMetaData(Connection connection) {

        DatabaseMetaData databaseMetaData = null;
        try {
            databaseMetaData = connection.getMetaData();
        } catch (SQLException sqlException) {
            // without metadata, there is nothing to do here
            throw new IllegalStateException("failure retrieving JDBC metadata", sqlException);
        }
        return databaseMetaData;
    }

    public static class OverloadHolder {
        String packageName;
        String procedureSchema;
        String procedureName;
        short overload;
        List<DbStoredArgument> overloadedArgs;
        public OverloadHolder(String packageName, String procedureSchema,
            String procedureName, short overload) {
            this.packageName = packageName;
            this.procedureSchema = procedureSchema;
            this.procedureName = procedureName;
            this.overload = overload;
            overloadedArgs= new ArrayList<DbStoredArgument>();
        }
        public List<DbStoredArgument> getArgs() {
            return overloadedArgs;
        }
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(overload);
            sb.append(' ');
            sb.append(packageName);
            sb.append('-');
            sb.append(procedureSchema);
            sb.append('-');
            sb.append(procedureName);
            sb.append(' ');
            sb.append(overloadedArgs);
            return sb.toString();
        }
     }
    
    public static List<DbColumn> buildDbColumns(Connection connection, String secondarySql) {
        List<DbColumn> columns = null;
        ResultSet resultSet = null;
        try {
        	Statement statement = connection.createStatement();
        	resultSet = statement.executeQuery(secondarySql);
        }
        catch (SQLException sqlException) {
        	throw new IllegalStateException("failure executing secondary SQL: " +
        		secondarySql, sqlException);
        }
        if (resultSet != null) {
			ResultSetMetaData rsMetaData = null;
        	try {
        		rsMetaData = resultSet.getMetaData();
			}
        	catch (SQLException sqlException) {
            	throw new IllegalStateException("failure retrieving resultSet metadata", sqlException);
			}
        	if (rsMetaData != null) {
        		int columnCount = 0;
        		try {
        			columnCount = rsMetaData.getColumnCount();
				}
        		catch (SQLException sqlException) {
                	throw new IllegalStateException("failure retrieving columnCount", sqlException);
				}
        		if (columnCount > 0) {
        			columns = new ArrayList<DbColumn>(columnCount);
        			try {
						for (int i = 1; i <= columnCount; i++) {
							DbColumn dbColumn = new DbColumn();
							dbColumn.setOrdinalPosition(i);
							dbColumn.setName(rsMetaData.getColumnLabel(i));
							dbColumn.setJDBCType(rsMetaData.getColumnType(i));
							dbColumn.setJDBCTypeName(rsMetaData.getColumnTypeName(i));
							dbColumn.setPrecision(rsMetaData.getPrecision(i));
							dbColumn.setScale(rsMetaData.getScale(i));
							dbColumn.setNullable(
								rsMetaData.isNullable(i)==ResultSetMetaData.columnNullable);
							columns.add(dbColumn);
						}
					}
        			catch (SQLException sqlException) {
                    	throw new IllegalStateException("failure retrieving column information",
                    		sqlException);
    				}
        		}
        	}
        }
        return columns;
    }
}