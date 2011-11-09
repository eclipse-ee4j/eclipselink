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
 *     David McCann - October 3, 2011 - 2.4 - Initial implementation
 ******************************************************************************/
package dbws.testing.varray;

//javase imports
import java.io.StringReader;
import java.sql.SQLException;
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
import org.eclipse.persistence.tools.dbws.DBWSBuilder;

//test imports
import dbws.testing.DBWSTestSuite;

/**
 * Tests VARRAY types.
 *
 */
public class VArrayTestSuite extends DBWSTestSuite {

    static final String CREATE_VCARRAY_VARRAY =
        "CREATE OR REPLACE TYPE VCARRAY AS VARRAY(4) OF VARCHAR2(20)";
    static final String CREATE_GETVCARRAY_PROC =
        "CREATE OR REPLACE PROCEDURE GETVCARRAY(T IN VARCHAR, U OUT VCARRAY) AS" +
        "\nBEGIN" +
            "\nU := VCARRAY();" +
            "\nU.EXTEND;" +
            "\nU(1) := CONCAT('entry1-', T);" +
            "\nU.EXTEND;" +
            "\nU(2) := CONCAT('entry2-', T);" +
        "\nEND GETVCARRAY;";
    static final String CREATE_GETVCARRAY2_FUNC =
        "CREATE OR REPLACE FUNCTION GETVCARRAY2(T IN VARCHAR) RETURN VCARRAY AS" +
        "\nL_DATA VCARRAY := VCARRAY();" +
        "\nBEGIN" +
            "\nL_DATA.EXTEND;" +
            "\nL_DATA(1) := CONCAT('entry1-', T);" +
            "\nL_DATA.EXTEND;" +
            "\nL_DATA(2) := CONCAT('entry2-', T);" +
            "\nRETURN L_DATA;" +
        "\nEND GETVCARRAY2;";
    static final String CREATE_COPYVCARRAY_PROC =
        "CREATE OR REPLACE PROCEDURE COPYVCARRAY(V IN VCARRAY, U OUT VCARRAY) AS" +
        "\nBEGIN" +
            "\nU := V;" +
            "\nU.EXTEND;" +
            "\nU(3) := 'copy';" +
        "\nEND COPYVCARRAY;";
    static final String CREATE_COPYVCARRAY2_FUNC =
        "CREATE OR REPLACE FUNCTION COPYVCARRAY2(V IN VCARRAY) RETURN VCARRAY AS" +
        "\nL_DATA VCARRAY := V;" +
        "\nBEGIN" +
            "\nL_DATA.EXTEND;" +
            "\nL_DATA(3) := 'copy';" +
            "\nRETURN L_DATA;" +
        "\nEND COPYVCARRAY2;";
    static final String CREATE_GETVALUEFROMVCARRAY_PROC =
        "CREATE OR REPLACE PROCEDURE GETVALUEFROMVCARRAY(V IN VCARRAY, I IN INTEGER, U OUT VARCHAR2, O OUT VARCHAR2) AS" +
        "\nBEGIN" +
            "\nU := V(I);" +
            "\nO := CONCAT('copy of ', V(I));" +
        "\nEND GETVALUEFROMVCARRAY;";
    static final String CREATE_GETVALUEFROMVCARRAY2_FUNC =
        "CREATE OR REPLACE FUNCTION GETVALUEFROMVCARRAY2(V IN VCARRAY, I IN INTEGER) RETURN VARCHAR2 AS" +
        "\nBEGIN" +
            "\nRETURN V(I);" +
        "\nEND GETVALUEFROMVCARRAY2;";
    static final String DROP_VCARRAY_VARRAY =
        "DROP TYPE VCARRAY";
    static final String DROP_GETVCARRAY_PROC =
        "DROP PROCEDURE GETVCARRAY";
    static final String DROP_GETVCARRAY2_FUNC =
        "DROP FUNCTION GETVCARRAY2";
    static final String DROP_COPYVCARRAY_PROC =
        "DROP PROCEDURE COPYVCARRAY";
    static final String DROP_COPYVCARRAY2_FUNC =
        "DROP FUNCTION COPYVCARRAY2";
    static final String DROP_GETVALUEFROMVCARRAY_PROC =
        "DROP PROCEDURE GETVALUEFROMVCARRAY";
    static final String DROP_GETVALUEFROMVCARRAY2_FUNC =
        "DROP FUNCTION GETVALUEFROMVCARRAY2";

    // JUnit test fixtures
    static String ddl = "false";

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
        ddl = System.getProperty(DATABASE_DDL_KEY, DEFAULT_DATABASE_DDL);
        if ("true".equalsIgnoreCase(ddl)) {
            try {
                createDbArtifact(conn, CREATE_VCARRAY_VARRAY);
                createDbArtifact(conn, CREATE_GETVCARRAY_PROC);
                createDbArtifact(conn, CREATE_GETVCARRAY2_FUNC);
                createDbArtifact(conn, CREATE_COPYVCARRAY_PROC);
                createDbArtifact(conn, CREATE_COPYVCARRAY2_FUNC);
                createDbArtifact(conn, CREATE_GETVALUEFROMVCARRAY_PROC);
                createDbArtifact(conn, CREATE_GETVALUEFROMVCARRAY2_FUNC);
            }
            catch (SQLException e) {
                //e.printStackTrace();
            }
        }
        DBWS_BUILDER_XML_USERNAME =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<dbws-builder xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
              "<properties>" +
                  "<property name=\"projectName\">VArrayTests</property>" +
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
                  "name=\"GetVCArrayTest\" " +
                  "catalogPattern=\"TOPLEVEL\" " +
                  "procedurePattern=\"GETVCARRAY\" " +
                  "isAdvancedJDBC=\"true\" " +
                  "returnType=\"vcarrayType\" " +
              "/>" +
              "<procedure " +
                  "name=\"GetVCArrayTest2\" " +
                  "catalogPattern=\"TOPLEVEL\" " +
                  "procedurePattern=\"GETVCARRAY2\" " +
                  "isAdvancedJDBC=\"true\" " +
                  "returnType=\"vcarrayType\" " +
              "/>" +
              "<procedure " +
                  "name=\"CopyVCArrayTest\" " +
                  "catalogPattern=\"TOPLEVEL\" " +
                  "procedurePattern=\"COPYVCARRAY\" " +
                  "isAdvancedJDBC=\"true\" " +
                  "returnType=\"vcarrayType\" " +
              "/>" +
              "<procedure " +
                  "name=\"CopyVCArrayTest2\" " +
                  "catalogPattern=\"TOPLEVEL\" " +
                  "procedurePattern=\"COPYVCARRAY2\" " +
                  "isAdvancedJDBC=\"true\" " +
                  "returnType=\"vcarrayType\" " +
              "/>" +
              "<procedure " +
                  "name=\"GetValueFromVCArrayTest\" " +
                  "catalogPattern=\"TOPLEVEL\" " +
                  "procedurePattern=\"GETVALUEFROMVCARRAY\" " +
                  "isAdvancedJDBC=\"true\" " +
                  "isSimpleXMLFormat=\"true\" " +
                  "isCollection=\"true\" " +
                  "returnType=\"xsd:string\" " +
              "/>" +
              "<procedure " +
                  "name=\"GetValueFromVCArrayTest2\" " +
                  "catalogPattern=\"TOPLEVEL\" " +
                  "procedurePattern=\"GETVALUEFROMVCARRAY2\" " +
                  "isAdvancedJDBC=\"true\" " +
                  "isSimpleXMLFormat=\"true\" " +
               "/>" +
            "</dbws-builder>";
          builder = new DBWSBuilder();
          DBWSTestSuite.setUp(".");
    }

    @AfterClass
    public static void tearDown() {
        if ("true".equalsIgnoreCase(ddl)) {
            dropDbArtifact(conn, DROP_GETVCARRAY_PROC);
            dropDbArtifact(conn, DROP_GETVCARRAY2_FUNC);
            dropDbArtifact(conn, DROP_COPYVCARRAY_PROC);
            dropDbArtifact(conn, DROP_COPYVCARRAY2_FUNC);
            dropDbArtifact(conn, DROP_GETVALUEFROMVCARRAY_PROC);
            dropDbArtifact(conn, DROP_GETVALUEFROMVCARRAY2_FUNC);
            dropDbArtifact(conn, DROP_VCARRAY_VARRAY);
        }
    }

    @Test
    public void getVCArrayTest() {
        Invocation invocation = new Invocation("GetVCArrayTest");
        invocation.setParameter("T", "xxx");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(VARRAY_RESULT));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    @Test
    public void getVCArrayTest2() {
        Invocation invocation = new Invocation("GetVCArrayTest2");
        invocation.setParameter("T", "xxx");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(VARRAY_RESULT));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    static String VARRAY_RESULT =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<vcarrayType xmlns=\"urn:VArrayTests\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
        "<item>entry1-xxx</item>" +
        "<item>entry2-xxx</item>" +
        "</vcarrayType>";

    @Test
    public void copyVCArrayTest() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object input = unmarshaller.unmarshal(new StringReader(INPUT_XML));
        Invocation invocation = new Invocation("CopyVCArrayTest");
        invocation.setParameter("V", input);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(RESULT_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    @Test
    public void copyVCArrayTest2() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object input = unmarshaller.unmarshal(new StringReader(INPUT_XML));
        Invocation invocation = new Invocation("CopyVCArrayTest2");
        invocation.setParameter("V", input);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(RESULT_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    static String INPUT_XML =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<vcarrayType xmlns=\"urn:VArrayTests\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
        "<item>one</item>" +
        "<item>two</item>" +
        "</vcarrayType>";

    static String RESULT_XML =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<vcarrayType xmlns=\"urn:VArrayTests\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
        "<item>one</item>" +
        "<item>two</item>" +
        "<item>copy</item>" +
        "</vcarrayType>";

    @Test
    public void getValueFromVCArrayTest() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object input = unmarshaller.unmarshal(new StringReader(INPUT2_XML));
        Invocation invocation = new Invocation("GetValueFromVCArrayTest");
        invocation.setParameter("V", input);
        invocation.setParameter("I", 2);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(RESULT2_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    @Test
    public void getValueFromVCArrayTest2() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object input = unmarshaller.unmarshal(new StringReader(INPUT2_XML));
        Invocation invocation = new Invocation("GetValueFromVCArrayTest2");
        invocation.setParameter("V", input);
        invocation.setParameter("I", 3);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(RESULT3_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    static String INPUT2_XML =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<vcarrayType xmlns=\"urn:VArrayTests\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
        "<item>1-foo</item>" +
        "<item>2-bar</item>" +
        "<item>3-foobar</item>" +
        "<item>4-blah</item>" +
        "</vcarrayType>";

    static String RESULT2_XML =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<simple-xml-format>" +
        "<simple-xml>" +
        "<U>2-bar</U>" +
        "<O>copy of 2-bar</O>" +
        "</simple-xml>" +
        "</simple-xml-format>";

    static String RESULT3_XML =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<simple-xml-format>" +
        "<simple-xml>" +
        "<result>3-foobar</result>" +
        "</simple-xml>" +
        "</simple-xml-format>";
}