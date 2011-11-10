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
 *     David McCann - October 24, 2011 - 2.4 - Initial implementation
 ******************************************************************************/
package dbws.testing.objecttype;

//javase imports
import java.io.StringReader;
import java.sql.SQLException;
import java.sql.Statement;
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
import org.eclipse.persistence.oxm.XMLUnmarshaller;

//test imports
import dbws.testing.DBWSTestSuite;

/**
 * Tests Oracle Object types.
 *
 */
public class ObjectTypeTestSuite extends DBWSTestSuite {

    static final String CREATE_PHONE_TYPE =
        "CREATE OR REPLACE TYPE PHONE_TYPE AS OBJECT (" +
            "\nHOME VARCHAR2(20)," +
            "\nCELL VARCHAR2(20)" +
        "\n)";
    static final String CREATE_EMP_TYPE =
        "CREATE OR REPLACE TYPE EMP_TYPE AS OBJECT (" +
            "\nID NUMBER," +
            "\nNAME VARCHAR2(20)," +
            "\nPHONE PHONE_TYPE" +
        "\n)";
    static final String CREATE_EMP_TYPE_TABLE =
        "CREATE TABLE EMP_TYPE_TABLE OF EMP_TYPE";
    static final String[] POPULATE_EMP_TYPE_TABLE = new String[] {
        "INSERT INTO EMP_TYPE_TABLE VALUES (" +
            "EMP_TYPE(66, 'BUBBLES', PHONE_TYPE('(613) 234-4567', '(613) 858-3434')))",
        "INSERT INTO EMP_TYPE_TABLE VALUES (" +
            "EMP_TYPE(69, 'RICKY', PHONE_TYPE('(613) 344-1232', '(613) 823-2323')))",
        "INSERT INTO EMP_TYPE_TABLE VALUES (" +
            "EMP_TYPE(99, 'JULIAN', PHONE_TYPE('(613) 424-0987', '(613) 555-8888')))"
        };
    static final String CREATE_GET_EMP_TYPE_BY_ID_PROC =
        "CREATE OR REPLACE PROCEDURE GET_EMP_TYPE_BY_ID(EID IN NUMBER, ETYPE OUT EMP_TYPE) AS" +
        "\nBEGIN" +
            "\nSELECT VALUE(E) INTO ETYPE FROM EMP_TYPE_TABLE E WHERE E.ID = EID;" +
        "\nEND GET_EMP_TYPE_BY_ID;";
    static final String CREATE_GET_EMP_TYPE_BY_ID_2_FUNC =
        "CREATE OR REPLACE FUNCTION GET_EMP_TYPE_BY_ID_2(EID IN NUMBER) RETURN EMP_TYPE AS" +
        "\nETYPE EMP_TYPE;" +
        "\nBEGIN" +
            "\nSELECT VALUE(E) INTO ETYPE FROM EMP_TYPE_TABLE E WHERE E.ID = EID;" +
            "\nRETURN ETYPE;" +
        "\nEND GET_EMP_TYPE_BY_ID_2;";
    static final String CREATE_ADD_EMP_TYPE_PROC =
        "CREATE OR REPLACE PROCEDURE ADD_EMP_TYPE(ETYPE IN EMP_TYPE, RESULT OUT EMP_TYPE) AS" +
        "\nBEGIN" +
            "\nDELETE FROM EMP_TYPE_TABLE WHERE ID = ETYPE.ID;" +
            "\nINSERT INTO EMP_TYPE_TABLE VALUES (ETYPE);" +
            "\nSELECT VALUE(E) INTO RESULT FROM EMP_TYPE_TABLE E WHERE E.ID = ETYPE.ID;" +
            "\nDELETE FROM EMP_TYPE_TABLE WHERE ID = ETYPE.ID;" +
        "\nEND ADD_EMP_TYPE;";
    static final String CREATE_ADD_EMP_TYPE2_FUNC =
        "CREATE OR REPLACE FUNCTION ADD_EMP_TYPE2(ETYPE IN EMP_TYPE) RETURN EMP_TYPE AS" +
        "\nRESULT EMP_TYPE;" +
        "\nBEGIN" +
            "\nADD_EMP_TYPE(ETYPE, RESULT);" +
            "\nRETURN RESULT;" +
        "\nEND ADD_EMP_TYPE2;";
    static final String DROP_PHONE_TYPE =
        "DROP TYPE PHONE_TYPE";
    static final String DROP_EMP_TYPE =
        "DROP TYPE EMP_TYPE";
    static final String DROP_EMP_TYPE_TABLE =
        "DROP TABLE EMP_TYPE_TABLE";
    static final String DROP_GET_EMP_TYPE_BY_ID_PROC =
        "DROP PROCEDURE GET_EMP_TYPE_BY_ID";
    static final String DROP_GET_EMP_TYPE_BY_ID_2_FUNC =
        "DROP FUNCTION GET_EMP_TYPE_BY_ID_2";
    static final String DROP_ADD_EMP_TYPE_PROC =
        "DROP PROCEDURE ADD_EMP_TYPE";
    static final String DROP_ADD_EMP_TYPE2_FUNC =
        "DROP FUNCTION ADD_EMP_TYPE2_FUNC";

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
        String ddlCreate = System.getProperty(DATABASE_DDL_CREATE_KEY, DEFAULT_DATABASE_DDL_CREATE);
        if ("true".equalsIgnoreCase(ddlCreate)) {
            try {
                createDbArtifact(conn, CREATE_PHONE_TYPE);
                createDbArtifact(conn, CREATE_EMP_TYPE);
                createDbArtifact(conn, CREATE_EMP_TYPE_TABLE);
                createDbArtifact(conn, CREATE_GET_EMP_TYPE_BY_ID_PROC);
                createDbArtifact(conn, CREATE_GET_EMP_TYPE_BY_ID_2_FUNC);
                createDbArtifact(conn, CREATE_ADD_EMP_TYPE_PROC);
                createDbArtifact(conn, CREATE_ADD_EMP_TYPE2_FUNC);
            }
            catch (SQLException e) {
                //e.printStackTrace();
            }
            try {
                Statement stmt = conn.createStatement();
                for (int i = 0; i < POPULATE_EMP_TYPE_TABLE.length; i++) {
                    stmt.addBatch(POPULATE_EMP_TYPE_TABLE[i]);
                }
                stmt.executeBatch();
            }
            catch (SQLException e) {
                //e.printStackTrace();
            }
        }
        DBWS_BUILDER_XML_USERNAME =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<dbws-builder xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
              "<properties>" +
                  "<property name=\"projectName\">ObjectTypeTests</property>" +
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
              "<procedure " +
                  "name=\"GetEmpTypeByIdTest\" " +
                  "catalogPattern=\"TOPLEVEL\" " +
                  "procedurePattern=\"GET_EMP_TYPE_BY_ID\" " +
                  "isAdvancedJDBC=\"true\" " +
                  "returnType=\"emp_typeType\" " +
              "/>" +
              "<procedure " +
                  "name=\"GetEmpTypeByIdTest2\" " +
                  "catalogPattern=\"TOPLEVEL\" " +
                  "procedurePattern=\"GET_EMP_TYPE_BY_ID_2\" " +
                  "isAdvancedJDBC=\"true\" " +
                  "returnType=\"emp_typeType\" " +
              "/>" +
              "<procedure " +
                  "name=\"AddEmpTypeTest\" " +
                  "catalogPattern=\"TOPLEVEL\" " +
                  "procedurePattern=\"ADD_EMP_TYPE\" " +
                  "isAdvancedJDBC=\"true\" " +
                  "returnType=\"emp_typeType\" " +
              "/>" +
              "<procedure " +
                  "name=\"AddEmpTypeTest2\" " +
                  "catalogPattern=\"TOPLEVEL\" " +
                  "procedurePattern=\"ADD_EMP_TYPE2\" " +
                  "isAdvancedJDBC=\"true\" " +
                  "returnType=\"emp_typeType\" " +
              "/>" +
            "</dbws-builder>";
          builder = null;
          DBWSTestSuite.setUp(".");
    }

    @AfterClass
    public static void tearDown() {
        String ddlDrop = System.getProperty(DATABASE_DDL_DROP_KEY, DEFAULT_DATABASE_DDL_DROP);
        if ("true".equalsIgnoreCase(ddlDrop)) {
            dropDbArtifact(conn, DROP_ADD_EMP_TYPE2_FUNC);
            dropDbArtifact(conn, DROP_ADD_EMP_TYPE_PROC);
            dropDbArtifact(conn, DROP_GET_EMP_TYPE_BY_ID_2_FUNC);
            dropDbArtifact(conn, DROP_GET_EMP_TYPE_BY_ID_PROC);
            dropDbArtifact(conn, DROP_EMP_TYPE_TABLE);
            dropDbArtifact(conn, DROP_EMP_TYPE);
            dropDbArtifact(conn, DROP_PHONE_TYPE);
        }
    }

    @Test
    public void getEmpTypeByIdTest() {
        Invocation invocation = new Invocation("GetEmpTypeByIdTest");
        invocation.setParameter("EID", 66);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(RESULT_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    static String RESULT_XML =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<emp_typeType xmlns=\"urn:ObjectTypeTests\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
        "<id>66</id>" +
        "<name>BUBBLES</name>" +
        "<phone>" +
        "<home>(613) 234-4567</home>" +
        "<cell>(613) 858-3434</cell>" +
        "</phone>" +
        "</emp_typeType>";

    @Test
    public void getEmpTypeByIdTest2() {
        Invocation invocation = new Invocation("GetEmpTypeByIdTest2");
        invocation.setParameter("EID", 69);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(RESULT2_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    static String RESULT2_XML =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<emp_typeType xmlns=\"urn:ObjectTypeTests\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
        "<id>69</id>" +
        "<name>RICKY</name>" +
        "<phone>" +
        "<home>(613) 344-1232</home>" +
        "<cell>(613) 823-2323</cell>" +
        "</phone>" +
        "</emp_typeType>";

    @Test
    public void addEmpTypeTest() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object eType = unmarshaller.unmarshal(new StringReader(ETYPE_XML));
        Invocation invocation = new Invocation("AddEmpTypeTest");
        invocation.setParameter("ETYPE", eType);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(ETYPE_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    @Test
    public void addEmpTypeTest2() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object eType = unmarshaller.unmarshal(new StringReader(ETYPE_XML));
        Invocation invocation = new Invocation("AddEmpTypeTest2");
        invocation.setParameter("ETYPE", eType);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(ETYPE_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    static String ETYPE_XML =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<emp_typeType xmlns=\"urn:ObjectTypeTests\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
        "<id>9</id>" +
        "<name>LAHEY</name>" +
        "<phone>" +
        "<home>(902) 987-0011</home>" +
        "<cell>(902) 789-1100</cell>" +
        "</phone>" +
        "</emp_typeType>";
}