/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Mike Norman - June 10 2011, created DDL parser package
//     David McCann - July 2011, visit tests
package dbws.testing.simplesp;

import static org.eclipse.persistence.internal.xr.QueryOperation.ORACLEOPAQUE_STR;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

//javase imports
import java.io.StringReader;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.Vector;

//java eXtension imports
import javax.wsdl.WSDLException;

//EclipseLink imports
import org.eclipse.persistence.internal.xr.Invocation;
import org.eclipse.persistence.internal.xr.Operation;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.tools.dbws.DBWSBuilder;
import org.eclipse.persistence.tools.dbws.ProcedureOperationModel;
import org.eclipse.persistence.tools.dbws.TableOperationModel;
//JUnit4 imports
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

//test imports
import dbws.testing.DBWSTestSuite;

public class SimpleSPTestSuite extends DBWSTestSuite {

    static final String CREATE_SIMPLESP_TABLE =
        "CREATE TABLE SIMPLESP (" +
            "\nEMPNO DECIMAL(4,0) NOT NULL," +
            "\nENAME VARCHAR(10)," +
            "\nJOB VARCHAR(9)," +
            "\nMGR DECIMAL(4,0)," +
            "\nHIREDATE DATE," +
            "\nSAL DECIMAL(7,2)," +
            "\nCOMM DECIMAL(7,2)," +
            "\nDEPTNO DECIMAL(2,0)," +
            "\nPRIMARY KEY (EMPNO)" +
        "\n)";
    static final String[] POPULATE_SIMPLESP_TABLE = new String[] {
        "INSERT INTO SIMPLESP VALUES (7369,'SMITH','CLERK',7902," +
            "TO_DATE('1980-12-17 00:00:00','YYYY-MM-DD HH24:MI:SS'),800.92,NULL,20)",
        "INSERT INTO SIMPLESP VALUES (7499,'ALLEN','SALESMAN',7698," +
            "TO_DATE('1981-2-20 00:00:00','YYYY-MM-DD HH24:MI:SS'),1600,300,30)",
        "INSERT INTO SIMPLESP VALUES (7521,'WARD','SALESMAN',7698," +
            "TO_DATE('1981-2-22 00:00:00','YYYY-MM-DD HH24:MI:SS'),1250,500,30)",
        "INSERT INTO SIMPLESP VALUES (7566,'JONES','MANAGER',7839," +
            "TO_DATE('1981-4-2 00:00:00','YYYY-MM-DD HH24:MI:SS'),2975,NULL,20)",
        "INSERT INTO SIMPLESP VALUES (7654,'MARTIN','SALESMAN',7698," +
            "TO_DATE('1981-9-28 00:00:00','YYYY-MM-DD HH24:MI:SS'),1250,1400,30)",
        "INSERT INTO SIMPLESP VALUES (7698,'BLAKE','MANAGER',7839," +
            "TO_DATE('1981-5-1 00:00:00','YYYY-MM-DD HH24:MI:SS'),2850,NULL,30)",
        "INSERT INTO SIMPLESP VALUES (7782,'CLARK','MANAGER',7839," +
            "TO_DATE('1981-6-9 00:00:00','YYYY-MM-DD HH24:MI:SS'),2450,NULL,10)",
        "INSERT INTO SIMPLESP VALUES (7788,'SCOTT','ANALYST',7566," +
            "TO_DATE('1981-06-09 00:00:00','YYYY-MM-DD HH24:MI:SS'),3000,NULL,20)",
        "INSERT INTO SIMPLESP VALUES (7839,'KING','PRESIDENT',NULL," +
            "TO_DATE('1981-11-17 00:00:00','YYYY-MM-DD HH24:MI:SS'),5000,NULL,10)",
        "INSERT INTO SIMPLESP VALUES (7844,'TURNER','SALESMAN',7698," +
            "TO_DATE('1981-9-8 00:00:00','YYYY-MM-DD HH24:MI:SS'),1500,0,30)",
        "INSERT INTO SIMPLESP VALUES (7876,'ADAMS','CLERK',7788," +
            "TO_DATE('1987-05-23 00:00:00','YYYY-MM-DD HH24:MI:SS'),1100,NULL,20)",
        "INSERT INTO SIMPLESP VALUES (7900,'JAMES','CLERK',7698," +
            "TO_DATE('1981-12-03 00:00:00','YYYY-MM-DD HH24:MI:SS'),950,NULL,30)",
        "INSERT INTO SIMPLESP VALUES (7902,'FORD','ANALYST',7566," +
            "TO_DATE('1981-12-03 00:00:00','YYYY-MM-DD HH24:MI:SS'),3000,NULL,20)",
        "INSERT INTO SIMPLESP VALUES (7934,'MILLER','CLERK',7782," +
            "TO_DATE('1982-01-23 00:00:00','YYYY-MM-DD HH24:MI:SS'),1300,NULL,10)"
    };
    static final String CREATE_VARCHARSP_PROC =
        "CREATE PROCEDURE VARCHARSP(X IN VARCHAR) is" +
        "\nBEGIN" +
            "\nnull;" +
        "\nEND VARCHARSP;";
    static final String CREATE_NOARGSP_PROC =
        "CREATE PROCEDURE NOARGSP is" +
        "\nBEGIN" +
            "\nnull;" +
        "\nEND NOARGSP;";
    static final String CREATE_INOUTARGSSP_PROC =
        "CREATE PROCEDURE INOUTARGSSP(T IN VARCHAR, U OUT VARCHAR, V OUT NUMERIC) is" +
        "\nBEGIN" +
            "\nU := CONCAT('barf-' , T);" +
            "\nV := 55;" +
        "\nEND INOUTARGSSP;";
    static final String CREATE_FINDBYJOB_PROC =
        "CREATE PROCEDURE FINDBYJOB (J IN VARCHAR, SIMPL OUT SYS_REFCURSOR) IS" +
        "\nBEGIN" +
        "   \nOPEN SIMPL FOR SELECT * FROM SIMPLESP WHERE JOB LIKE J;" +
        "\nEND FINDBYJOB;";
    static final String CREATE_GETALL_PROC =
        "CREATE PROCEDURE GETALL(SIMPL OUT SYS_REFCURSOR) IS" +
        "\nBEGIN" +
        "   \nOPEN SIMPL FOR SELECT * FROM SIMPLESP;" +
        "\nEND GETALL;";
    static final String CREATE_GETSALARYBYID_PROC =
        "CREATE OR REPLACE PROCEDURE GETSALARYBYID(S IN OUT NUMERIC) is" +
        "\nBEGIN" +
        "   \nSELECT SAL INTO S FROM SIMPLESP WHERE EMPNO = S;" +
        "\nEND GETSALARYBYID;";
    static final String CREATE_OUT_IN_INOUT_ARGSSP_PROC =
        "CREATE PROCEDURE OUTININOUTARGSSP(T OUT VARCHAR, U IN VARCHAR, V IN OUT NUMERIC) is" +
        "\nBEGIN" +
            "\nT := CONCAT('barfoo-' , U);" +
            "\nV := V + 1;" +
        "\nEND OUTININOUTARGSSP;";
    static final String CREATE_GET_XMLTYPE_PROC =
        "CREATE OR REPLACE PROCEDURE GET_XMLTYPE(W IN VARCHAR2, X OUT XMLTYPE) is" +
        "\nBEGIN" +
            "\nX := XMLTYPE(W);" +
        "\nEND GET_XMLTYPE;";
    static final String DROP_SIMPLESP_TABLE =
        "DROP TABLE SIMPLESP";
    static final String DROP_VARCHARSP_PROC =
        "DROP PROCEDURE VARCHARSP";
    static final String DROP_NOARGSP_PROC =
        "DROP PROCEDURE NOARGSP";
    static final String DROP_INOUTARGSSP_PROC =
        "DROP PROCEDURE INOUTARGSSP";
    static final String DROP_FINDBYJOB_PROC =
        "DROP PROCEDURE FINDBYJOB";
    static final String DROP_GETALL_PROC =
        "DROP PROCEDURE GETALL";
    static final String DROP_GETSALARYBYID_PROC =
        "DROP PROCEDURE GETSALARYBYID";
    static final String DROP_OUT_IN_INOUT_ARGSSP_PROC =
        "DROP PROCEDURE OUTININOUTARGSSP";
    static final String DROP_GET_XMLTYPE_PROC =
        "DROP PROCEDURE GET_XMLTYPE";

    static boolean ddlCreate = false;
    static boolean ddlDrop = false;
    static boolean ddlDebug = false;

    @BeforeClass
    public static void setUp() throws WSDLException {
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
            runDdl(conn, CREATE_SIMPLESP_TABLE, ddlDebug);
            runDdl(conn, CREATE_VARCHARSP_PROC, ddlDebug);
            runDdl(conn, CREATE_NOARGSP_PROC, ddlDebug);
            runDdl(conn, CREATE_INOUTARGSSP_PROC, ddlDebug);
            runDdl(conn, CREATE_FINDBYJOB_PROC, ddlDebug);
            runDdl(conn, CREATE_GETALL_PROC, ddlDebug);
            runDdl(conn, CREATE_GETSALARYBYID_PROC, ddlDebug);
            runDdl(conn, CREATE_OUT_IN_INOUT_ARGSSP_PROC, ddlDebug);
            runDdl(conn, CREATE_GET_XMLTYPE_PROC, ddlDebug);
            try {
                Statement stmt = conn.createStatement();
                for (int i = 0; i < POPULATE_SIMPLESP_TABLE.length; i++) {
                    stmt.addBatch(POPULATE_SIMPLESP_TABLE[i]);
                }
                stmt.executeBatch();
            }
            catch (SQLException e) {
                //e.printStackTrace();
            }
        }

        username = System.getProperty(DATABASE_USERNAME_KEY, DEFAULT_DATABASE_USERNAME);
        password = System.getProperty(DATABASE_PASSWORD_KEY, DEFAULT_DATABASE_PASSWORD);
        url = System.getProperty(DATABASE_URL_KEY, DEFAULT_DATABASE_URL);

        builder = new DBWSBuilder();

        builder.setProjectName("simpleSP");
        builder.setLogLevel("off");
        builder.setUsername(username);
        builder.setPassword(password);
        builder.setUrl(url);
        builder.setDriver(System.getProperty("db.driver", DATABASE_DRIVER));
        builder.setPlatformClassname(System.getProperty("db.platform", DATABASE_PLATFORM));

        ProcedureOperationModel procOpModel = new ProcedureOperationModel();
        procOpModel.setName("VarcharTest");
        procOpModel.setCatalogPattern("TOPLEVEL");
        procOpModel.setProcedurePattern("VarcharSP");
        procOpModel.setReturnType("xsd:int");
        builder.addOperation(procOpModel);

        procOpModel = new ProcedureOperationModel();
        procOpModel.setName("NoArgsTest");
        procOpModel.setCatalogPattern("TOPLEVEL");
        procOpModel.setProcedurePattern("NoArgSP");
        procOpModel.setReturnType("xsd:int");
        builder.addOperation(procOpModel);

        procOpModel = new ProcedureOperationModel();
        procOpModel.setName("InOutArgsTest");
        procOpModel.setCatalogPattern("TOPLEVEL");
        procOpModel.setProcedurePattern("InOutArgsSP");
        procOpModel.setIsSimpleXMLFormat(true);
        builder.addOperation(procOpModel);

        procOpModel = new ProcedureOperationModel();
        procOpModel.setName("OutInInOutArgsTest");
        procOpModel.setCatalogPattern("TOPLEVEL");
        procOpModel.setProcedurePattern("OUTININOUTARGSSP");
        procOpModel.setIsSimpleXMLFormat(true);
        procOpModel.setIsCollection(true);
        builder.addOperation(procOpModel);

        procOpModel = new ProcedureOperationModel();
        procOpModel.setName("FindByJobTest");
        procOpModel.setCatalogPattern("TOPLEVEL");
        procOpModel.setProcedurePattern("FindByJob");
        procOpModel.setIsCollection(true);
        procOpModel.setIsSimpleXMLFormat(true);
        procOpModel.setSimpleXMLFormatTag("simplesp-rows");
        procOpModel.setXmlTag("simplesp-row");
        builder.addOperation(procOpModel);
        procOpModel = new ProcedureOperationModel();
        procOpModel.setName("GetSalaryByIdTest");
        procOpModel.setCatalogPattern("TOPLEVEL");
        procOpModel.setProcedurePattern("GETSALARYBYID");
        builder.addOperation(procOpModel);

        TableOperationModel tableOpModel = new TableOperationModel();
        tableOpModel.setSchemaPattern("%");
        tableOpModel.setTablePattern("SIMPLESP");
        procOpModel = new ProcedureOperationModel();
        procOpModel.setName("GetAllTest");
        procOpModel.setCatalogPattern("TOPLEVEL");
        procOpModel.setProcedurePattern("GetAll");
        procOpModel.setIsCollection(true);
        procOpModel.setReturnType("simplespType");
        tableOpModel.addOperation(procOpModel);

        procOpModel = new ProcedureOperationModel();
        procOpModel.setName("getXMLTypeData");
        procOpModel.setCatalogPattern("TOPLEVEL");
        procOpModel.setProcedurePattern("GET_XMLTYPE");
        procOpModel.setIsSimpleXMLFormat(true);
        builder.addOperation(procOpModel);

        builder.addOperation(tableOpModel);
        setUp(".", false, true);
    }

    @AfterClass
    public static void tearDown() {
        if (ddlDrop) {
            runDdl(conn, DROP_VARCHARSP_PROC, ddlDebug);
            runDdl(conn, DROP_NOARGSP_PROC, ddlDebug);
            runDdl(conn, DROP_INOUTARGSSP_PROC, ddlDebug);
            runDdl(conn, DROP_FINDBYJOB_PROC, ddlDebug);
            runDdl(conn, DROP_GETALL_PROC, ddlDebug);
            runDdl(conn, DROP_GETSALARYBYID_PROC, ddlDebug);
            runDdl(conn, DROP_SIMPLESP_TABLE, ddlDebug);
            runDdl(conn, DROP_OUT_IN_INOUT_ARGSSP_PROC, ddlDebug);
            runDdl(conn, DROP_GET_XMLTYPE_PROC, ddlDebug);
        }
    }

    static final String VALUE_1_XML =
        REGULAR_XML_HEADER +
        "<value>1</value>";
   @Test
    public void varcharTest() {
        Invocation invocation = new Invocation("VarcharTest");
        invocation.setParameter("X", "this is a test");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(VALUE_1_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    @Test
    public void noargsTest() {
        Invocation invocation = new Invocation("NoArgsTest");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(VALUE_1_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    @Test
    public void inOutArgsTest() {
        Invocation invocation = new Invocation("InOutArgsTest");
        invocation.setParameter("T", "yuck");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(IN_OUT_ARGS_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String IN_OUT_ARGS_XML =
        REGULAR_XML_HEADER +
        "<simple-xml-format>" +
            "<simple-xml>" +
                "<U>barf-yuck</U>" +
                "<V>55</V>" +
            "</simple-xml>" +
        "</simple-xml-format>";

    @Test
    public void findByJobTest() {
        Invocation invocation = new Invocation("FindByJobTest");
        invocation.setParameter("J", "CL%");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(ALL_SIMPLESP_CLERK_ROWS_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String ALL_SIMPLESP_CLERK_ROWS_XML =
        REGULAR_XML_HEADER +
        "<simplesp-rows xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"simple-xml-format\">" +
          "<simplesp-row>" +
              "<EMPNO>7369</EMPNO>" +
              "<ENAME>SMITH</ENAME>" +
              "<JOB>CLERK</JOB>" +
              "<MGR>7902</MGR>" +
              "<HIREDATE>1980-12-17T00:00:00-05:00</HIREDATE>" +
              "<SAL>800.92</SAL>" +
              "<DEPTNO>20</DEPTNO>" +
          "</simplesp-row>" +
          "<simplesp-row>" +
              "<EMPNO>7876</EMPNO>" +
              "<ENAME>ADAMS</ENAME>" +
              "<JOB>CLERK</JOB>" +
              "<MGR>7788</MGR>" +
              "<HIREDATE>1987-05-23T00:00:00-04:00</HIREDATE>" +
              "<SAL>1100</SAL>" +
              "<DEPTNO>20</DEPTNO>" +
          "</simplesp-row>" +
          "<simplesp-row>" +
              "<EMPNO>7900</EMPNO>" +
              "<ENAME>JAMES</ENAME>" +
              "<JOB>CLERK</JOB>" +
              "<MGR>7698</MGR>" +
              "<HIREDATE>1981-12-03T00:00:00-05:00</HIREDATE>" +
              "<SAL>950</SAL>" +
              "<DEPTNO>30</DEPTNO>" +
          "</simplesp-row>" +
          "<simplesp-row>" +
              "<EMPNO>7934</EMPNO>" +
              "<ENAME>MILLER</ENAME>" +
              "<JOB>CLERK</JOB>" +
              "<MGR>7782</MGR>" +
              "<HIREDATE>1982-01-23T00:00:00-05:00</HIREDATE>" +
              "<SAL>1300</SAL>" +
              "<DEPTNO>10</DEPTNO>" +
          "</simplesp-row>" +
      "</simplesp-rows>";

    @SuppressWarnings("rawtypes")
    @Test
    public void getAllTest() {
        Invocation invocation = new Invocation("GetAllTest");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        Document doc = xmlPlatform.createDocument();
        Element ec = doc.createElement("all");
        doc.appendChild(ec);
        for (Object r : (Vector)result) {
            marshaller.marshal(r, ec);
        }
        Document controlDoc = xmlParser.parse(new StringReader(ALL_SIMPLESP_ROWS_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String ALL_SIMPLESP_ROWS_XML =
        REGULAR_XML_HEADER +
        "<all>" +
            "<simplespType xmlns=\"urn:simpleSP\">" +
                "<empno>7369</empno>" +
                "<ename>SMITH</ename>" +
                "<job>CLERK</job>" +
                "<mgr>7902</mgr>" +
                "<hiredate>1980-12-17</hiredate>" +
                "<sal>800.92</sal>" +
                "<comm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
                "<deptno>20</deptno>" +
            "</simplespType>" +
            "<simplespType xmlns=\"urn:simpleSP\">" +
                "<empno>7499</empno>" +
                "<ename>ALLEN</ename>" +
                "<job>SALESMAN</job>" +
                "<mgr>7698</mgr>" +
                "<hiredate>1981-02-20</hiredate>" +
                "<sal>1600</sal>" +
                "<comm>300</comm>" +
                "<deptno>30</deptno>" +
            "</simplespType>" +
            "<simplespType xmlns=\"urn:simpleSP\">" +
                "<empno>7521</empno>" +
                "<ename>WARD</ename>" +
                "<job>SALESMAN</job>" +
                "<mgr>7698</mgr>" +
                "<hiredate>1981-02-22</hiredate>" +
                "<sal>1250</sal>" +
                "<comm>500</comm>" +
                "<deptno>30</deptno>" +
            "</simplespType>" +
            "<simplespType xmlns=\"urn:simpleSP\">" +
                "<empno>7566</empno>" +
                "<ename>JONES</ename>" +
                "<job>MANAGER</job>" +
                "<mgr>7839</mgr>" +
                "<hiredate>1981-04-02</hiredate>" +
                "<sal>2975</sal>" +
                "<comm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
                "<deptno>20</deptno>" +
            "</simplespType>" +
            "<simplespType xmlns=\"urn:simpleSP\">" +
                "<empno>7654</empno>" +
                "<ename>MARTIN</ename>" +
                "<job>SALESMAN</job>" +
                "<mgr>7698</mgr>" +
                "<hiredate>1981-09-28</hiredate>" +
                "<sal>1250</sal>" +
                "<comm>1400</comm>" +
                "<deptno>30</deptno>" +
            "</simplespType>" +
            "<simplespType xmlns=\"urn:simpleSP\">" +
                "<empno>7698</empno>" +
                "<ename>BLAKE</ename>" +
                "<job>MANAGER</job>" +
                "<mgr>7839</mgr>" +
                "<hiredate>1981-05-01</hiredate>" +
                "<sal>2850</sal>" +
                "<comm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
                "<deptno>30</deptno>" +
            "</simplespType>" +
            "<simplespType xmlns=\"urn:simpleSP\">" +
                "<empno>7782</empno>" +
                "<ename>CLARK</ename>" +
                "<job>MANAGER</job>" +
                "<mgr>7839</mgr>" +
                "<hiredate>1981-06-09</hiredate>" +
                "<sal>2450</sal>" +
                "<comm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
                "<deptno>10</deptno>" +
            "</simplespType>" +
            "<simplespType xmlns=\"urn:simpleSP\">" +
                "<empno>7788</empno>" +
                "<ename>SCOTT</ename>" +
                "<job>ANALYST</job>" +
                "<mgr>7566</mgr>" +
                "<hiredate>1981-06-09</hiredate>" +
                "<sal>3000</sal>" +
                "<comm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
                "<deptno>20</deptno>" +
            "</simplespType>" +
            "<simplespType xmlns=\"urn:simpleSP\">" +
                "<empno>7839</empno>" +
                "<ename>KING</ename>" +
                "<job>PRESIDENT</job>" +
                "<mgr xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
                "<hiredate>1981-11-17</hiredate>" +
                "<sal>5000</sal>" +
                "<comm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
                "<deptno>10</deptno>" +
            "</simplespType>" +
            "<simplespType xmlns=\"urn:simpleSP\">" +
                "<empno>7844</empno>" +
                "<ename>TURNER</ename>" +
                "<job>SALESMAN</job>" +
                "<mgr>7698</mgr>" +
                "<hiredate>1981-09-08</hiredate>" +
                "<sal>1500</sal>" +
                "<comm>0</comm>" +
                "<deptno>30</deptno>" +
            "</simplespType>" +
            "<simplespType xmlns=\"urn:simpleSP\">" +
                "<empno>7876</empno>" +
                "<ename>ADAMS</ename>" +
                "<job>CLERK</job>" +
                "<mgr>7788</mgr>" +
                "<hiredate>1987-05-23</hiredate>" +
                "<sal>1100</sal>" +
                "<comm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
                "<deptno>20</deptno>" +
            "</simplespType>" +
            "<simplespType xmlns=\"urn:simpleSP\">" +
                "<empno>7900</empno>" +
                "<ename>JAMES</ename>" +
                "<job>CLERK</job>" +
                "<mgr>7698</mgr>" +
                "<hiredate>1981-12-03</hiredate>" +
                "<sal>950</sal>" +
                "<comm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
                "<deptno>30</deptno>" +
            "</simplespType>" +
            "<simplespType xmlns=\"urn:simpleSP\">" +
                "<empno>7902</empno>" +
                "<ename>FORD</ename>" +
                "<job>ANALYST</job>" +
                "<mgr>7566</mgr>" +
                "<hiredate>1981-12-03</hiredate>" +
                "<sal>3000</sal>" +
                "<comm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
                "<deptno>20</deptno>" +
            "</simplespType>" +
            "<simplespType xmlns=\"urn:simpleSP\">" +
                "<empno>7934</empno>" +
                "<ename>MILLER</ename>" +
                "<job>CLERK</job>" +
                "<mgr>7782</mgr>" +
                "<hiredate>1982-01-23</hiredate>" +
                "<sal>1300</sal>" +
                "<comm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
                "<deptno>10</deptno>" +
            "</simplespType>" +
        "</all>";

      @Test
      public void getSalaryByIdTest() {
          Invocation invocation = new Invocation("GetSalaryByIdTest");
          invocation.setParameter("S", 7876);
          Operation op = xrService.getOperation(invocation.getName());
          Object result = op.invoke(xrService, invocation);
          assertNotNull("result is null", result);
          Document doc = xmlPlatform.createDocument();
          XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
          marshaller.marshal(result, doc);
          Document controlDoc = xmlParser.parse(new StringReader(SALARY));
          assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
      }

      public static final String SALARY =
          "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
          "<value>1100</value>";

      @Test
      public void outInInOutTest() {
          Invocation invocation = new Invocation("OutInInOutArgsTest");
          invocation.setParameter("U", "this is a test");
          invocation.setParameter("V", 665);
          Operation op = xrService.getOperation(invocation.getName());
          Object result = op.invoke(xrService, invocation);
          assertNotNull("result is null", result);
          Document doc = xmlPlatform.createDocument();
          XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
          marshaller.marshal(result, doc);
          Document controlDoc = xmlParser.parse(new StringReader(MULTIPLE_OUT_XML));
          assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
      }

      public static final String MULTIPLE_OUT_XML =
          "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
          "<simple-xml-format>" +
          "<simple-xml>" +
          "<V>666</V>" +
          "<T>barfoo-this is a test</T>" +
          "</simple-xml>" +
          "</simple-xml-format>";

      @Test
      public void getXMLTypeData() throws ParseException {
          Invocation invocation = new Invocation("getXMLTypeData");
          invocation.setParameter("W", "<jb><data> jdev testing for 12.1.2 </data></jb>");
          Operation op = xrService.getOperation(invocation.getName());
          Object result = op.invoke(xrService, invocation);
          assertNotNull("result is null", result);
          Document doc = xmlPlatform.createDocument();
          XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
          marshaller.marshal(((XMLRoot)result).getObject(), doc);
          Document controlDoc = xmlParser.parse(new StringReader(XMLTYPE_XML));
          boolean areDocsEqual = comparer.isNodeEqual(controlDoc, doc);
          if (!areDocsEqual) {
              String testDocString = documentToString(doc);
              String msg = "Control document not same as instance document.";
              if (testDocString.contains(ORACLEOPAQUE_STR)) {
                  msg = msg + " Please make sure that Oracle's XDB and XMLParser jars are on the test classpath.";
              }
              fail(msg + "\nExpected:\n" + documentToString(controlDoc) + "\nActual:\n" + testDocString);
          }

      }
      public static final String XMLTYPE_XML =
          "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
          "<simple-xml-format>" +
          "<simple-xml>" +
          "<result>&lt;jb>&lt;data> jdev testing for 12.1.2 &lt;/data>&lt;/jb></result>" +
          "</simple-xml>" +
          "</simple-xml-format>";
}
