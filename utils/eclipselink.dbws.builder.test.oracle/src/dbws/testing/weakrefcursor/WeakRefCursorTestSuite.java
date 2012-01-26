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
 *     David McCann - September 06, 2011 - 2.4 - Initial implementation
 ******************************************************************************/
package dbws.testing.weakrefcursor;

//javase imports
import java.io.StringReader;
import org.w3c.dom.Document;

//java eXtension imports
import javax.wsdl.WSDLException;

//JUnit4 imports
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

//EclipseLink imports
import org.eclipse.persistence.internal.xr.Invocation;
import org.eclipse.persistence.internal.xr.Operation;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.tools.dbws.DBWSBuilder;

//test imports
import dbws.testing.DBWSTestSuite;

/**
 * Tests PL/SQL procedures with simple arguments.
 *
 */
public class WeakRefCursorTestSuite extends DBWSTestSuite {

    static final String WEAKLY_TYPED_REF_CURSOR = "WEAKLY_TYPED_REF_CURSOR";
    static final String WEAKLY_TYPED_REF_CURSOR_TEST_PACKAGE = WEAKLY_TYPED_REF_CURSOR + "_TEST";
    static final String CREATE_WEAKLY_TYPED_REF_CURSOR_TEST_PACKAGE =
        "CREATE OR REPLACE PACKAGE " + WEAKLY_TYPED_REF_CURSOR_TEST_PACKAGE + " AS" +
            "\nTYPE " + WEAKLY_TYPED_REF_CURSOR + " IS REF CURSOR;" +
            "\nPROCEDURE GET_EMPS(P_DEPTNO IN EMP.DEPTNO%TYPE, P_RECORDSET OUT " + WEAKLY_TYPED_REF_CURSOR + ");" +
        "\nEND " + WEAKLY_TYPED_REF_CURSOR_TEST_PACKAGE + ";";
    static final String CREATE_WEAKLY_TYPED_REF_CURSOR_TEST_PACKAGE_BODY =
        "CREATE OR REPLACE PACKAGE BODY " + WEAKLY_TYPED_REF_CURSOR_TEST_PACKAGE + " AS" +
            "\nPROCEDURE GET_EMPS(P_DEPTNO IN EMP.DEPTNO%TYPE, P_RECORDSET OUT " + WEAKLY_TYPED_REF_CURSOR + ") AS" +
            "\nBEGIN" +
            "\n    OPEN P_RECORDSET FOR" +
            "\n        SELECT ENAME, EMPNO, DEPTNO FROM EMP WHERE DEPTNO = P_DEPTNO ORDER BY ENAME;" +
            "\nEND GET_EMPS;" +
        "\nEND " + WEAKLY_TYPED_REF_CURSOR_TEST_PACKAGE + ";";

    static final String DROP_WEAKLY_TYPED_REF_CURSOR_TEST_PACKAGE =
        "DROP PACKAGE " + WEAKLY_TYPED_REF_CURSOR_TEST_PACKAGE;

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
            runDdl(conn, CREATE_WEAKLY_TYPED_REF_CURSOR_TEST_PACKAGE, ddlDebug);
            runDdl(conn, CREATE_WEAKLY_TYPED_REF_CURSOR_TEST_PACKAGE_BODY, ddlDebug);
        }
        username = System.getProperty(DATABASE_USERNAME_KEY, DEFAULT_DATABASE_USERNAME);
        DBWS_BUILDER_XML_USERNAME =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<dbws-builder xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
              "<properties>" +
                  "<property name=\"projectName\">weakRefCursor</property>" +
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
                  "name=\"weakRefCursorTest\" " +
                  "catalogPattern=\"" + WEAKLY_TYPED_REF_CURSOR_TEST_PACKAGE + "\" " +
                  "procedurePattern=\"GET_EMPS\" " +
                  "isCollection=\"true\" " +
                  "isSimpleXMLFormat=\"true\" " +
              "/>" +
            "</dbws-builder>";
          builder = new DBWSBuilder();
          DBWSTestSuite.setUp(".");
    }

    @AfterClass
    public static void tearDown() {
        if (ddlDrop) {
            runDdl(conn, DROP_WEAKLY_TYPED_REF_CURSOR_TEST_PACKAGE, ddlDebug);
        }
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void weakRefCursorTest() {
        Invocation invocation = new Invocation("weakRefCursorTest");
        invocation.setParameter("P_DEPTNO", Integer.valueOf(10));
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        Document doc = xmlPlatform.createDocument();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(EMPLOYEES_IN_DEPT10_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String EMPLOYEES_IN_DEPT10_XML =
        REGULAR_XML_HEADER +
        "<simple-xml-format>" +
            "<simple-xml>" +
                "<ENAME>CLARK</ENAME>" +
                "<EMPNO>7782</EMPNO>" +
                "<DEPTNO>10</DEPTNO>" +
            "</simple-xml>" +
            "<simple-xml>" +
                "<ENAME>KING</ENAME>" +
                "<EMPNO>7839</EMPNO>" +
                "<DEPTNO>10</DEPTNO>" +
            "</simple-xml>" +
            "<simple-xml>" +
                "<ENAME>MILLER</ENAME>" +
                "<EMPNO>7934</EMPNO>" +
                "<DEPTNO>10</DEPTNO>" +
            "</simple-xml>" +
        "</simple-xml-format>";

}