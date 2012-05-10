/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     David McCann - September 23, 2011 - 2.4 - Initial implementation
 ******************************************************************************/
package dbws.testing.plsqlcollection;

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
import org.eclipse.persistence.oxm.XMLUnmarshaller;

//test imports
import dbws.testing.DBWSTestSuite;

/**
 * Tests PL/SQL collections with simple arguments.
 *
 */
public class PLSQLCollectionTestSuite extends DBWSTestSuite {

    static final String CREATE_PACKAGE2_PACKAGE =
        "CREATE OR REPLACE PACKAGE PACKAGE2 AS" +
            "\nTYPE TAB1 IS TABLE OF VARCHAR2(111) INDEX BY BINARY_INTEGER;" +
            "\nTYPE ORECORD IS RECORD (" +
                "\nO1 VARCHAR2(10)," +
                "\nO2 DECIMAL(7,2)" +
            "\n);" +
            "\nTYPE TAB2 IS TABLE OF ORECORD INDEX BY BINARY_INTEGER;" +
            "\nPROCEDURE COPYTABLE(OLDTAB IN TAB1, NEWTAB OUT TAB1);" +
            "\nPROCEDURE SETRECORD(INREC IN ORECORD, NEWTAB OUT TAB2);" +
            "\nFUNCTION COPYTABLE2(OLDTAB IN TAB1) RETURN TAB1;" +
            "\nFUNCTION SETRECORD2(INREC IN ORECORD) RETURN TAB2;" +
        "\nEND PACKAGE2;";
    static final String CREATE_PACKAGE2_BODY =
        "CREATE OR REPLACE PACKAGE BODY PACKAGE2 AS" +
            "\nPROCEDURE COPYTABLE(OLDTAB IN TAB1, NEWTAB OUT TAB1) AS" +
            "\nBEGIN" +
                "\nNEWTAB := OLDTAB;" +
            "\nEND COPYTABLE;" +
            "\nPROCEDURE SETRECORD(INREC IN ORECORD, NEWTAB OUT TAB2) AS" +
            "\nBEGIN" +
                "\nNEWTAB(0) := INREC;" +
            "\nEND SETRECORD;" +
            "\nFUNCTION COPYTABLE2(OLDTAB IN TAB1) RETURN TAB1 IS NEWTAB TAB1;" +
            "\nBEGIN" +
                "\nNEWTAB := OLDTAB;" +
                "\nRETURN NEWTAB;" +
            "\nEND COPYTABLE2;" +
            "\nFUNCTION SETRECORD2(INREC IN ORECORD) RETURN TAB2 IS NEWTAB TAB2;" +
            "\nBEGIN" +
                "\nNEWTAB(0) := INREC;" +
                "\nRETURN NEWTAB;" +
            "\nEND SETRECORD2;" +
        "\nEND PACKAGE2;";
    static final String CREATE_PACKAGE2_TAB1_TYPE =
        "CREATE OR REPLACE TYPE PACKAGE2_TAB1 AS TABLE OF VARCHAR2(111)";
    static final String CREATE_PACKAGE2_ORECORD_TYPE =
        "CREATE OR REPLACE TYPE PACKAGE2_ORECORD AS OBJECT (" +
            "\nO1 VARCHAR2(10)," +
            "\nO2 DECIMAL(7,2)" +
        "\n)";
    static final String CREATE_PACKAGE2_TAB2_TYPE =
        "CREATE OR REPLACE TYPE PACKAGE2_TAB2 AS TABLE OF PACKAGE2_ORECORD";
    static final String DROP_PACKAGE2_PACKAGE =
        "DROP PACKAGE PACKAGE2";
    static final String DROP_PACKAGE2_TAB1_TYPE =
        "DROP TYPE PACKAGE2_TAB1";
    static final String DROP_PACKAGE2_TAB2_TYPE =
        "DROP TYPE PACKAGE2_TAB2";
    static final String DROP_PACKAGE2_ORECORD_TYPE =
        "DROP TYPE PACKAGE2_ORECORD";

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
            runDdl(conn, CREATE_PACKAGE2_PACKAGE, ddlDebug);
            runDdl(conn, CREATE_PACKAGE2_BODY, ddlDebug);
            runDdl(conn, CREATE_PACKAGE2_TAB1_TYPE, ddlDebug);
            runDdl(conn, CREATE_PACKAGE2_ORECORD_TYPE, ddlDebug);
            runDdl(conn, CREATE_PACKAGE2_TAB2_TYPE, ddlDebug);
        }
        DBWS_BUILDER_XML_USERNAME =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<dbws-builder xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
              "<properties>" +
                  "<property name=\"projectName\">PLSQLCollection</property>" +
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
                  "name=\"CopyTableTest\" " +
                  "catalogPattern=\"PACKAGE2\" " +
                  "procedurePattern=\"COPYTABLE\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"CopyTableTest2\" " +
                  "catalogPattern=\"PACKAGE2\" " +
                  "procedurePattern=\"COPYTABLE2\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"SetRecordTest\" " +
                  "catalogPattern=\"PACKAGE2\" " +
                  "procedurePattern=\"SETRECORD\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"SetRecordTest2\" " +
                  "catalogPattern=\"PACKAGE2\" " +
                  "procedurePattern=\"SETRECORD2\" " +
              "/>" +
            "</dbws-builder>";
          builder = null;
          DBWSTestSuite.setUp(".");
    }

    @AfterClass
    public static void tearDown() {
        String ddlDrop = System.getProperty(DATABASE_DDL_DROP_KEY, DEFAULT_DATABASE_DDL_DROP);
        if ("true".equalsIgnoreCase(ddlDrop)) {
            runDdl(conn, DROP_PACKAGE2_PACKAGE, ddlDebug);
            runDdl(conn, DROP_PACKAGE2_TAB1_TYPE, ddlDebug);
            runDdl(conn, DROP_PACKAGE2_TAB2_TYPE, ddlDebug);
            runDdl(conn, DROP_PACKAGE2_ORECORD_TYPE, ddlDebug);
        }
    }

    @Test
    public void copyTableTest() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object inputTab1 = unmarshaller.unmarshal(new StringReader(TABLE_XML));
        Invocation invocation = new Invocation("CopyTableTest");
        invocation.setParameter("OLDTAB", inputTab1);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(TABLE_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String TABLE_XML =
        STANDALONE_XML_HEADER +
        "<PACKAGE2_TAB1 xmlns=\"urn:PLSQLCollection\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<item>BLAH</item>" +
        "</PACKAGE2_TAB1>";

    @Test
    public void copyTableTest2() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object inputTab1 = unmarshaller.unmarshal(new StringReader(TABLE_XML));
        Invocation invocation = new Invocation("CopyTableTest2");
        invocation.setParameter("OLDTAB", inputTab1);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(TABLE_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    /**
     * StoredProcedure test.
     */
    @Test
    public void setRecordTest() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object inputORec = unmarshaller.unmarshal(new StringReader(INPUTORECORD_XML));
        Invocation invocation = new Invocation("SetRecordTest");
        invocation.setParameter("INREC", inputORec);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(OUTPUTTABLE_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String INPUTORECORD_XML =
        STANDALONE_XML_HEADER +
        "<PACKAGE2_ORECORD xmlns=\"urn:PLSQLCollection\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<o1>somedata</o1>" +
          "<o2>66.6</o2>" +
        "</PACKAGE2_ORECORD>";
    public static final String OUTPUTTABLE_XML =
        STANDALONE_XML_HEADER +
        "<PACKAGE2_TAB2 xmlns=\"urn:PLSQLCollection\">" +
          "<item>" +
            "<o1>somedata</o1>" +
            "<o2>66.6</o2>" +
          "</item>" +
        "</PACKAGE2_TAB2>";

    /**
     * StoredFunction test.
     */
    @Test
    public void setRecordTest2() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object inputORec = unmarshaller.unmarshal(new StringReader(INPUTORECORD_XML));
        Invocation invocation = new Invocation("SetRecordTest2");
        invocation.setParameter("INREC", inputORec);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(OUTPUTTABLE_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

}