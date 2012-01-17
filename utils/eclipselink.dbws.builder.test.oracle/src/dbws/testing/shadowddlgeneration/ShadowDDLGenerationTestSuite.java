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
import java.io.StringReader;
import java.sql.SQLException;
import java.util.List;

//java eXtension imports
import javax.wsdl.WSDLException;

//JUnit4 imports
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static junit.framework.Assert.assertEquals;

//EclipseLink imports
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.tools.dbws.DBWSBuilder;
import org.eclipse.persistence.tools.dbws.DBWSBuilderHelper;
import org.eclipse.persistence.tools.dbws.DBWSBuilderModel;
import org.eclipse.persistence.tools.dbws.DBWSBuilderModelProject;
import org.eclipse.persistence.tools.dbws.JSR109WebServicePackager;
import org.eclipse.persistence.tools.dbws.XRPackager;
import org.eclipse.persistence.tools.dbws.oracle.OracleHelper;
import org.eclipse.persistence.tools.dbws.oracle.ShadowDDLGenerator;
import org.eclipse.persistence.tools.oracleddl.metadata.ArgumentType;
import org.eclipse.persistence.tools.oracleddl.metadata.CompositeDatabaseType;
import org.eclipse.persistence.tools.oracleddl.metadata.PLSQLRecordType;
import org.eclipse.persistence.tools.oracleddl.metadata.ProcedureType;
import static org.eclipse.persistence.tools.dbws.DBWSBuilder.NO_SESSIONS_FILENAME;
import static org.eclipse.persistence.tools.dbws.DBWSBuilder.SESSIONS_FILENAME_KEY;
import static org.eclipse.persistence.tools.dbws.DBWSPackager.ArchiveUse.noArchive;
import static org.eclipse.persistence.tools.dbws.Util.sqlMatch;

//test imports
import dbws.testing.DBWSTestSuite;
import dbws.testing.shadowddlgeneration.oldjpub.AttributeField;
import dbws.testing.shadowddlgeneration.oldjpub.MethodFilter;
import dbws.testing.shadowddlgeneration.oldjpub.PlsqlRecordType;
import dbws.testing.shadowddlgeneration.oldjpub.PlsqlTableType;
import dbws.testing.shadowddlgeneration.oldjpub.ProcedureMethod;
import dbws.testing.shadowddlgeneration.oldjpub.PublisherException;
import dbws.testing.shadowddlgeneration.oldjpub.SqlReflector;
import dbws.testing.shadowddlgeneration.oldjpub.SqlTypeWithMethods;
import dbws.testing.shadowddlgeneration.oldjpub.TypeClass;
import static dbws.testing.shadowddlgeneration.oldjpub.Util.IS_PACKAGE;

public class ShadowDDLGenerationTestSuite extends DBWSTestSuite {

    static final String SHADOWDDLTEST_PACKAGE = "SHADOWDDLTEST";
    static final String SHADOWDDLTEST_PROCEDURE = "P1";
    static final String SHADOWDDLTEST_RECORD_TYPE = "NUM_RECORD";
    static final String SHADOWDDLTEST_TABLE_TYPE = "NUM_TBL";
    static final String CREATE_SHADOWDDLTEST_PACKAGE =
        "CREATE OR REPLACE PACKAGE " + SHADOWDDLTEST_PACKAGE + " AS" +
            "\nTYPE " + SHADOWDDLTEST_TABLE_TYPE + " IS TABLE OF NUMBER INDEX BY BINARY_INTEGER;" +
            "\nTYPE " + SHADOWDDLTEST_RECORD_TYPE + " IS RECORD (" +
                "\nN1 VARCHAR2(10)," +
                "\nN2 " + SHADOWDDLTEST_TABLE_TYPE +
            "\n);" +
            "\nPROCEDURE " + SHADOWDDLTEST_PROCEDURE + "(NR IN " + SHADOWDDLTEST_RECORD_TYPE + ");" +
        "\nEND " + SHADOWDDLTEST_PACKAGE + ";";
    static final String CREATE_SHADOWDDLTEST_PACKAGE_BODY =
        "CREATE OR REPLACE PACKAGE BODY " + SHADOWDDLTEST_PACKAGE + " AS" +
            "\nPROCEDURE " + SHADOWDDLTEST_PROCEDURE + "(NR IN NUM_RECORD) AS" +
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
    static SqlTypeWithMethods methodType = null;

    @BeforeClass
    public static void setUp() throws WSDLException, SQLException, PublisherException {
        if (conn == null) {
            try {
                conn = buildConnection();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
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
        DBWS_BUILDER_XML_USERNAME =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<dbws-builder xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
              "<properties>" +
                  "<property name=\"projectName\">shadowDDL</property>" +
                  "<property name=\"logLevel\">off</property>" +
                  "<property name=\"username\">";
          DBWS_BUILDER_XML_PASSWORD =
                  "</property><property name=\"password\">";
          DBWS_BUILDER_XML_URL =
                  "</property><property name=\"url\">";
          DBWS_BUILDER_XML_DRIVER =
                  "</property><property name=\"driver\">";
          DBWS_BUILDER_XML_PLATFORM =
                  "</property><property name=\"platformClassname\">";
          DBWS_BUILDER_XML_MAIN =
                  "</property>" +
              "</properties>" +
              "<plsql-procedure " +
                  "name=\"p1Test\" " +
                  "catalogPattern=\"" + SHADOWDDLTEST_PACKAGE  + "\" " +
                  "procedurePattern=\"" + SHADOWDDLTEST_PROCEDURE + "\" " +
              "/>" +
            "</dbws-builder>";
          builder = new DBWSBuilder();
          username = System.getProperty(DATABASE_USERNAME_KEY, DEFAULT_DATABASE_USERNAME);
          password = System.getProperty(DATABASE_PASSWORD_KEY, DEFAULT_DATABASE_PASSWORD);
          url = System.getProperty(DATABASE_URL_KEY, DEFAULT_DATABASE_URL);
          String builderString = DBWS_BUILDER_XML_USERNAME + username + DBWS_BUILDER_XML_PASSWORD +
              password + DBWS_BUILDER_XML_URL + url + DBWS_BUILDER_XML_DRIVER + DATABASE_DRIVER +
              DBWS_BUILDER_XML_PLATFORM + DATABASE_PLATFORM + DBWS_BUILDER_XML_MAIN;
          XMLContext context = new XMLContext(new DBWSBuilderModelProject());
          XMLUnmarshaller unmarshaller = context.createUnmarshaller();
          DBWSBuilderModel builderModel = (DBWSBuilderModel)unmarshaller.unmarshal(
              new StringReader(builderString));
          builder.quiet = true;
          builder.setPlatformClassname(DATABASE_PLATFORM);
          builder.properties = builderModel.properties;
          builder.operations = builderModel.operations;
          XRPackager xrPackager = new JSR109WebServicePackager(null, "WebServiceTestPackager", noArchive) {
              @Override
              public void start() {
                  // do nothing - don't have to verify existence of 'stageDir' when
                  // all the streams are in-memory
              }
          };
          xrPackager.setDBWSBuilder(builder);
          builder.setPackager(xrPackager);
          builder.getProperties().put(SESSIONS_FILENAME_KEY, NO_SESSIONS_FILENAME);
          DBWSBuilderHelper helper = builder.getBuilderHelper();
          helper.buildDbArtifacts();

          SqlReflector sqlReflector = new SqlReflector(conn, username);
          methodType = (SqlTypeWithMethods)sqlReflector.addSqlUserType(
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
    }

    @AfterClass
    public static void tearDown() {
        if (ddlDrop) {
            runDdl(conn, DROP_SHADOWDDLTEST_PACKAGE, ddlDebug);
        }
    }

    @Test
    public void testOldJPubDDL() throws SQLException, PublisherException {
        List<ProcedureMethod> methods = methodType.getDeclaredMethods();
        String numRecordCreateDDL = null;
        String numRecordDropDDL = null;
        String numTblCreateDDL = null;
        String numTblDropDDL = null;
        for (TypeClass typeClass : methods.get(0).getParamTypes()) {
            if (typeClass instanceof PlsqlRecordType) {
                PlsqlRecordType recordType = (PlsqlRecordType)typeClass;
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
        assertEquals("generated create shadow DDL for NUM_RECORD does not match expected shadow DDL",
            numRecordCreateDDL, EXPECTED_NUMRECORD_CREATE_SHADOWDDL);
        assertEquals("generated drop shadow DDL for NUM_RECORD does not match expected shadow DDL",
            numRecordDropDDL, EXPECTED_NUMRECORD_DROP_SHADOWDDL);
        assertEquals("generated create shadow DDL for NUM_TBL does not match expected shadow DDL",
            numTblCreateDDL, EXPECTED_NUMTBL_CREATE_SHADOWDDL);
        assertEquals("generated drop shadow DDL for NUM_TBL does not match expected shadow DDL",
            numTblDropDDL, EXPECTED_NUMTBL_DROP_SHADOWDDL);
    }

    static final String EXPECTED_NUMRECORD_CREATE_SHADOWDDL =
        "CREATE OR REPLACE TYPE SHADOWDDLTEST_NUM_RECORD AS OBJECT (" +
        "      N1 VARCHAR2(10)," +
        "      N2 SHADOWDDLTEST_NUM_TBL" +
        ");";
    static final String EXPECTED_NUMRECORD_DROP_SHADOWDDL =
        "DROP TYPE SHADOWDDLTEST_NUM_RECORD FORCE;";
    static final String EXPECTED_NUMTBL_CREATE_SHADOWDDL =
        "CREATE OR REPLACE TYPE SHADOWDDLTEST_NUM_TBL AS TABLE OF NUMBER;";
    static final String EXPECTED_NUMTBL_DROP_SHADOWDDL =
        "DROP TYPE SHADOWDDLTEST_NUM_TBL FORCE;";

    @Test
    public void testShadowDDLGenerator() {
        OracleHelper helper = (OracleHelper)builder.getBuilderHelper();
        List<ProcedureType> dbStoredProcedures = helper.getDbStoredProcedures();
        ProcedureType p1 = dbStoredProcedures.get(0);
        String numRecordCreateDDL = null;
        String numRecordDropDDL = null;
        String numTblCreateDDL = null;
        String numTblDropDDL = null;
        for (ArgumentType arg : p1.getArguments()) {
            if (arg.isComposite()) {
                CompositeDatabaseType dataType = (CompositeDatabaseType)arg.getDataType();
                if (dataType instanceof PLSQLRecordType) {
                    PLSQLRecordType recType = (PLSQLRecordType)dataType;
                    ShadowDDLGenerator shadowDDLGenerator = new ShadowDDLGenerator(recType.getParentType());
                    numRecordCreateDDL = removeLineTerminators(
                        shadowDDLGenerator.getCreateDDLFor(SHADOWDDLTEST_RECORD_TYPE));
                    numRecordDropDDL = removeLineTerminators(
                        shadowDDLGenerator.getDropDDLFor(SHADOWDDLTEST_RECORD_TYPE));
                    numTblCreateDDL = removeLineTerminators(
                        shadowDDLGenerator.getCreateDDLFor(SHADOWDDLTEST_TABLE_TYPE));
                    numTblDropDDL = removeLineTerminators(
                        shadowDDLGenerator.getDropDDLFor(SHADOWDDLTEST_TABLE_TYPE));
                    break;
                }
            }
        }
        assertEquals("generated create shadow DDL for NUM_RECORD does not match expected shadow DDL",
            numRecordCreateDDL, EXPECTED_NUMRECORD_CREATE_SHADOWDDL);
        assertEquals("generated drop shadow DDL for NUM_RECORD does not match expected shadow DDL",
            numRecordDropDDL, EXPECTED_NUMRECORD_DROP_SHADOWDDL);
        assertEquals("generated create shadow DDL for NUM_TBL does not match expected shadow DDL",
            numTblCreateDDL, EXPECTED_NUMTBL_CREATE_SHADOWDDL);
        assertEquals("generated drop shadow DDL for NUM_TBL does not match expected shadow DDL",
            numTblDropDDL, EXPECTED_NUMTBL_DROP_SHADOWDDL);
    }
}