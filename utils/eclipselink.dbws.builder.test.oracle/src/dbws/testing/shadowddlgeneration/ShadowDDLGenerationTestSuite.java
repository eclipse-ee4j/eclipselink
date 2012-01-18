/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     David McCann - Sept. 07, 2011 - 2.4 - Initial implementation
 ******************************************************************************/
package dbws.testing.shadowddlgeneration;

//javase imports
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

//java eXtension imports

//JUnit4 imports
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static junit.framework.Assert.assertEquals;

//EclipseLink imports
import org.eclipse.persistence.tools.dbws.oracle.ShadowDDLGenerator;
import org.eclipse.persistence.tools.oracleddl.metadata.PLSQLCollectionType;
import org.eclipse.persistence.tools.oracleddl.metadata.PLSQLPackageType;
import org.eclipse.persistence.tools.oracleddl.metadata.PLSQLRecordType;
import org.eclipse.persistence.tools.oracleddl.parser.ParseException;
import org.eclipse.persistence.tools.oracleddl.util.DatabaseTypeBuilder;

import static org.eclipse.persistence.tools.dbws.Util.sqlMatch;

//test imports
import dbws.testing.shadowddlgeneration.oldjpub.AttributeField;
import dbws.testing.shadowddlgeneration.oldjpub.MethodFilter;
import dbws.testing.shadowddlgeneration.oldjpub.PlsqlRecordType;
import dbws.testing.shadowddlgeneration.oldjpub.PlsqlTableType;
import dbws.testing.shadowddlgeneration.oldjpub.ProcedureMethod;
import dbws.testing.shadowddlgeneration.oldjpub.PublisherException;
import dbws.testing.shadowddlgeneration.oldjpub.SqlReflector;
import dbws.testing.shadowddlgeneration.oldjpub.SqlTypeWithMethods;
import dbws.testing.shadowddlgeneration.oldjpub.TypeClass;
import static dbws.testing.DBWSTestSuite.DATABASE_DDL_CREATE_KEY;
import static dbws.testing.DBWSTestSuite.DATABASE_DDL_DEBUG_KEY;
import static dbws.testing.DBWSTestSuite.DATABASE_DDL_DROP_KEY;
import static dbws.testing.DBWSTestSuite.DATABASE_USERNAME_KEY;
import static dbws.testing.DBWSTestSuite.DEFAULT_DATABASE_DDL_CREATE;
import static dbws.testing.DBWSTestSuite.DEFAULT_DATABASE_DDL_DEBUG;
import static dbws.testing.DBWSTestSuite.DEFAULT_DATABASE_DDL_DROP;
import static dbws.testing.DBWSTestSuite.DEFAULT_DATABASE_USERNAME;
import static dbws.testing.DBWSTestSuite.buildConnection;
import static dbws.testing.DBWSTestSuite.removeLineTerminators;
import static dbws.testing.DBWSTestSuite.runDdl;
import static dbws.testing.shadowddlgeneration.oldjpub.Util.IS_PACKAGE;

public class ShadowDDLGenerationTestSuite {

    static final String SHADOWDDLTEST_PACKAGE = "SHADOWDDLTEST";
    static final String SHADOWDDLTEST_PROCEDURE = "P1";
    static final String SHADOWDDLTEST_RECORD_TYPE = "NUM_RECORD";
    static final String SHADOWDDLTEST_TABLE_TYPE = "NUM_TBL";
    static final String SHADOWDDLTEST_TABLE_TYPE2 = "NUM_TBL2";
    static final String CREATE_SHADOWDDLTEST_PACKAGE =
        "CREATE OR REPLACE PACKAGE " + SHADOWDDLTEST_PACKAGE + " AS" +
            "\nTYPE " + SHADOWDDLTEST_TABLE_TYPE + " IS TABLE OF NUMBER INDEX BY BINARY_INTEGER;" +
            "\nTYPE " + SHADOWDDLTEST_RECORD_TYPE + " IS RECORD (" +
                "\nN1 VARCHAR2(10)," +
                "\nN2 DECIMAL(7,2)," +
                "\nN3 " + SHADOWDDLTEST_TABLE_TYPE +
            "\n);" +
            "\nTYPE " + SHADOWDDLTEST_TABLE_TYPE2 + " IS TABLE OF " + SHADOWDDLTEST_RECORD_TYPE + ";" +
            "\nPROCEDURE " + SHADOWDDLTEST_PROCEDURE + "(NT2 IN " + SHADOWDDLTEST_TABLE_TYPE2 + ");" +
        "\nEND " + SHADOWDDLTEST_PACKAGE + ";";
    static final String CREATE_SHADOWDDLTEST_PACKAGE_BODY =
        "CREATE OR REPLACE PACKAGE BODY " + SHADOWDDLTEST_PACKAGE + " AS" +
            "\nPROCEDURE " + SHADOWDDLTEST_PROCEDURE + "(NT2 IN " + SHADOWDDLTEST_TABLE_TYPE2 + ") AS" +
            "\nBEGIN" +
                "\nnull;" +
            "\nEND " + SHADOWDDLTEST_PROCEDURE + ";" +
        "\nEND " + SHADOWDDLTEST_PACKAGE + ";";
    static final String DROP_SHADOWDDLTEST_PACKAGE =
        "DROP PACKAGE " + SHADOWDDLTEST_PACKAGE + ";";

    //JUnit 'fixture's
    static boolean ddlCreate = false;
    static boolean ddlDrop = false;
    static boolean ddlDebug = false;
    static String username = null;
    static Connection conn = null;
    static PLSQLPackageType shadowDDLTestPackage = null;

    @BeforeClass
    public static void setUp() throws ParseException, ClassNotFoundException, SQLException {
        username = System.getProperty(DATABASE_USERNAME_KEY, DEFAULT_DATABASE_USERNAME);
        conn = buildConnection();
        String ddlCreateProp = System.getProperty(DATABASE_DDL_CREATE_KEY, DEFAULT_DATABASE_DDL_CREATE);
        if ("true".equalsIgnoreCase(ddlCreateProp)) {
            ddlCreate = true;
        }
        String ddlDropProp = System.getProperty(DATABASE_DDL_DROP_KEY, DEFAULT_DATABASE_DDL_DROP);
        if ("true".equalsIgnoreCase(ddlDropProp)) {
            ddlDrop = true;
        }
        String ddlDebugProp = System.getProperty(DATABASE_DDL_DEBUG_KEY, DEFAULT_DATABASE_DDL_DEBUG);
        if ("true".equalsIgnoreCase(ddlDebugProp)) {
            ddlDebug = true;
        }
        if (ddlCreate) {
            runDdl(conn, CREATE_SHADOWDDLTEST_PACKAGE, ddlDebug);
            runDdl(conn, CREATE_SHADOWDDLTEST_PACKAGE_BODY, ddlDebug);
        }
        shadowDDLTestPackage = new DatabaseTypeBuilder().buildPackages(conn, username,
            SHADOWDDLTEST_PACKAGE).get(0);
    }

    @AfterClass
    public static void tearDown() {
        if (ddlDrop) {
            runDdl(conn, DROP_SHADOWDDLTEST_PACKAGE, ddlDebug);
        }
    }

    @Test
    public void testOldJPubDDLGeneration() throws SQLException, PublisherException {
        SqlReflector sqlReflector = new SqlReflector(conn, username);
        SqlTypeWithMethods methodType = (SqlTypeWithMethods)sqlReflector.addSqlUserType(
            username.toUpperCase(), SHADOWDDLTEST_PACKAGE, IS_PACKAGE, true, 0, 0,
            new MethodFilter() {
                public boolean acceptMethod(ProcedureMethod method, boolean preApprove) {
                    String methodName = method.getName();
                    if (sqlMatch(SHADOWDDLTEST_PROCEDURE, methodName)) {
                        return true;
                    }
                    return false;
                }

            }
         );
        String numRecordCreateDDL = null;
        String numRecordDropDDL = null;
        String numTblCreateDDL = null;
        String numTblDropDDL = null;
        String numTbl2CreateDDL = null;
        String numTbl2DropDDL = null;
        List<ProcedureMethod> methods = methodType.getDeclaredMethods();
        for (TypeClass typeClass : methods.get(0).getParamTypes()) {
            if (typeClass.getName().contains(SHADOWDDLTEST_TABLE_TYPE2)) {
                PlsqlTableType numTbl2 = (PlsqlTableType)typeClass;
                numTbl2CreateDDL = removeLineTerminators(numTbl2.getSqlTypeDecl());
                numTbl2DropDDL = removeLineTerminators(numTbl2.getSqlTypeDrop());
                PlsqlRecordType recordType = (PlsqlRecordType)numTbl2.getComponentType();
                numRecordCreateDDL = removeLineTerminators(recordType.getSqlTypeDecl());
                numRecordDropDDL = removeLineTerminators(recordType.getSqlTypeDrop());
                for (AttributeField field : recordType.getFields(false)) {
                    if (field.getType() instanceof PlsqlTableType) {
                        PlsqlTableType tableType = (PlsqlTableType)field.getType();
                        numTblCreateDDL = removeLineTerminators(tableType.getSqlTypeDecl());
                        numTblDropDDL = removeLineTerminators(tableType.getSqlTypeDrop());
                        break;
                    }
                }
            }
        }
        assertEquals("generated create shadow DDL for " + SHADOWDDLTEST_RECORD_TYPE +
            " does not match expected shadow DDL", numRecordCreateDDL,
            EXPECTED_NUMRECORD_CREATE_SHADOWDDL);
        assertEquals("generated drop shadow DDL for " + SHADOWDDLTEST_RECORD_TYPE +
        	" does not match expected shadow DDL", numRecordDropDDL, EXPECTED_NUMRECORD_DROP_SHADOWDDL);
        assertEquals("generated create shadow DDL for " + SHADOWDDLTEST_TABLE_TYPE +
        	" does not match expected shadow DDL", numTblCreateDDL, EXPECTED_NUMTBL_CREATE_SHADOWDDL);
        assertEquals("generated drop shadow DDL for " + SHADOWDDLTEST_TABLE_TYPE +
        	" does not match expected shadow DDL", numTblDropDDL, EXPECTED_NUMTBL_DROP_SHADOWDDL);
        assertEquals("generated create shadow DDL for " + SHADOWDDLTEST_TABLE_TYPE2 +
        	" does not match expected shadow DDL", numTbl2CreateDDL, EXPECTED_NUMTBL2_CREATE_SHADOWDDL);
        assertEquals("generated drop shadow DDL for " + SHADOWDDLTEST_TABLE_TYPE2 +
            " does not match expected shadow DDL", numTbl2DropDDL, EXPECTED_NUMTBL2_DROP_SHADOWDDL);
    }

    static final String EXPECTED_NUMRECORD_CREATE_SHADOWDDL =
        "CREATE OR REPLACE TYPE " + SHADOWDDLTEST_PACKAGE + "_" + SHADOWDDLTEST_RECORD_TYPE + " AS OBJECT (" +
        "      N1 VARCHAR2(10)," +
        "      N2 NUMBER(7, 2)," +
        "      N3 " + SHADOWDDLTEST_PACKAGE + "_" + SHADOWDDLTEST_TABLE_TYPE +
        ");";
    static final String EXPECTED_NUMRECORD_DROP_SHADOWDDL =
        "DROP TYPE " + SHADOWDDLTEST_PACKAGE + "_" + SHADOWDDLTEST_RECORD_TYPE + " FORCE;";
    static final String EXPECTED_NUMTBL_CREATE_SHADOWDDL =
        "CREATE OR REPLACE TYPE " + SHADOWDDLTEST_PACKAGE + "_" + SHADOWDDLTEST_TABLE_TYPE +
            " AS TABLE OF NUMBER;";
    static final String EXPECTED_NUMTBL_DROP_SHADOWDDL =
        "DROP TYPE " + SHADOWDDLTEST_PACKAGE + "_" + SHADOWDDLTEST_TABLE_TYPE + " FORCE;";
    static final String EXPECTED_NUMTBL2_CREATE_SHADOWDDL =
        "CREATE OR REPLACE TYPE " + SHADOWDDLTEST_PACKAGE + "_" + SHADOWDDLTEST_TABLE_TYPE2 +
            " AS TABLE OF " + SHADOWDDLTEST_PACKAGE + "_" + SHADOWDDLTEST_RECORD_TYPE + ";";
    static final String EXPECTED_NUMTBL2_DROP_SHADOWDDL =
        "DROP TYPE " + SHADOWDDLTEST_PACKAGE + "_" + SHADOWDDLTEST_TABLE_TYPE2 + " FORCE;";

    @Test
    public void testNewShadowDDLGeneration() {
        ShadowDDLGenerator shadowDDLGenerator = new ShadowDDLGenerator(shadowDDLTestPackage);
        PLSQLCollectionType collectionType = (PLSQLCollectionType)shadowDDLTestPackage.getTypes().get(0);
        String numTblCreateDDL = removeLineTerminators(shadowDDLGenerator.getCreateDDLFor(collectionType));
        String numTblDropDDL = removeLineTerminators(shadowDDLGenerator.getDropDDLFor(collectionType));
        PLSQLRecordType recordType = (PLSQLRecordType)shadowDDLTestPackage.getTypes().get(1);
        String numRecordCreateDDL = removeLineTerminators(shadowDDLGenerator.getCreateDDLFor(recordType));
        String numRecordDropDDL = removeLineTerminators(shadowDDLGenerator.getDropDDLFor(recordType));
        PLSQLCollectionType collectionType2 = (PLSQLCollectionType)shadowDDLTestPackage.getTypes().get(2);
        String numTbl2CreateDDL = removeLineTerminators(shadowDDLGenerator.getCreateDDLFor(collectionType2));
        String numTbl2DropDDL = removeLineTerminators(shadowDDLGenerator.getDropDDLFor(collectionType2));
        assertEquals("generated create shadow DDL for " + SHADOWDDLTEST_RECORD_TYPE +
            " does not match expected shadow DDL", numRecordCreateDDL,
            EXPECTED_NUMRECORD_CREATE_SHADOWDDL);
        assertEquals("generated drop shadow DDL for " + SHADOWDDLTEST_RECORD_TYPE +
            " does not match expected shadow DDL", numRecordDropDDL, EXPECTED_NUMRECORD_DROP_SHADOWDDL);
        assertEquals("generated create shadow DDL for " + SHADOWDDLTEST_TABLE_TYPE +
            " does not match expected shadow DDL", numTblCreateDDL, EXPECTED_NUMTBL_CREATE_SHADOWDDL);
        assertEquals("generated drop shadow DDL for " + SHADOWDDLTEST_TABLE_TYPE +
            " does not match expected shadow DDL", numTblDropDDL, EXPECTED_NUMTBL_DROP_SHADOWDDL);
        assertEquals("generated create shadow DDL for " + SHADOWDDLTEST_TABLE_TYPE2 +
            " does not match expected shadow DDL", numTbl2CreateDDL, EXPECTED_NUMTBL2_CREATE_SHADOWDDL);
        assertEquals("generated drop shadow DDL for " + SHADOWDDLTEST_TABLE_TYPE2 +
            " does not match expected shadow DDL", numTbl2DropDDL, EXPECTED_NUMTBL2_DROP_SHADOWDDL);
    }

}