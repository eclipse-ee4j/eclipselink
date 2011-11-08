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
 *     David McCann - September 08, 2011 - 2.4 - Initial implementation
 ******************************************************************************/
package dbws.testing.plsqlrecord;

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

//test imports
import dbws.testing.DBWSTestSuite;

/**
 * Tests PL/SQL procedures with simple arguments.
 *
 */
public class PLSQLRecordTestSuite extends DBWSTestSuite {

    static final String CREATE_PACKAGE1_MTAB1_TYPE =
        "CREATE OR REPLACE TYPE PACKAGE1_MTAB1 AS TABLE OF NUMBER";
    static final String CREATE_PACKAGE1_NRECORD_TYPE =
        "CREATE OR REPLACE TYPE PACKAGE1_NRECORD AS OBJECT (" +
            "\nN1 VARCHAR2(10)," +
            "\nN2 DECIMAL(7,2)" +
        "\n)";
    static final String CREATE_PACKAGE1_MRECORD_TYPE =
        "CREATE OR REPLACE TYPE PACKAGE1_MRECORD AS OBJECT (" +
            "\nM1 PACKAGE1_MTAB1" +
        "\n)";
    static final String CREATE_PACKAGE1_PACKAGE =
        "CREATE OR REPLACE PACKAGE PACKAGE1 AS" +
            "\nTYPE MTAB1 IS TABLE OF NUMBER INDEX BY BINARY_INTEGER;" +
            "\nTYPE NRECORD IS RECORD (" +
                "\nN1 VARCHAR2(10)," +
                "\nN2 DECIMAL(7,2)" +
            "\n);" +
            "\nTYPE MRECORD IS RECORD (" +
                "\nM1 MTAB1" +
            "\n);" +
            "\nPROCEDURE GETNEWREC(NEWREC OUT NRECORD);" +
            "\nPROCEDURE COPYREC(ORIGINALREC IN NRECORD, NEWREC OUT NRECORD, SUFFIX IN VARCHAR2);" +
            "\nPROCEDURE GETRECWITHTABLE(ORIGINALTAB IN MTAB1, NEWREC OUT MRECORD);" +
            "\nFUNCTION COPYREC2(ORIGINALREC IN NRECORD, SUFFIX IN VARCHAR2) RETURN NRECORD;" +
            "\nFUNCTION GETRECWITHTABLE2(ORIGINALTAB IN MTAB1) RETURN MRECORD;" +
        "\nEND PACKAGE1;";
    static final String CREATE_PACKAGE1_BODY =
        "CREATE OR REPLACE PACKAGE BODY PACKAGE1 AS" +
            "\nPROCEDURE GETNEWREC(NEWREC OUT NRECORD) AS" +
            "\nBEGIN" +
                "\nNEWREC.N1 := 'new record';" +
                "\nNEWREC.N2 := 100.11;" +
            "\nEND GETNEWREC;" +
            "\nPROCEDURE COPYREC(ORIGINALREC IN NRECORD, NEWREC OUT NRECORD, SUFFIX IN VARCHAR2) AS" +
            "\nBEGIN" +
                "\nNEWREC.N1 := CONCAT(ORIGINALREC.N1, SUFFIX);" +
                "\nNEWREC.N2 := ORIGINALREC.N2 + 0.1;" +
            "\nEND COPYREC;" +
            "\nPROCEDURE GETRECWITHTABLE(ORIGINALTAB IN MTAB1, NEWREC OUT MRECORD) AS" +
            "\nBEGIN" +
                "\nNEWREC.M1 := ORIGINALTAB;" +
            "\nEND GETRECWITHTABLE;" +
            "\nFUNCTION COPYREC2(ORIGINALREC IN NRECORD, SUFFIX IN VARCHAR2) RETURN NRECORD IS" +
            "\nnewrec NRECORD;" +
            "\nBEGIN" +
                "\nnewrec.N1 := CONCAT(ORIGINALREC.N1, SUFFIX);" +
                "\nnewrec.N2 := ORIGINALREC.N2 + 0.1;" +
                "\nRETURN newrec;" +
            "\nEND COPYREC2;" +
            "\nFUNCTION GETRECWITHTABLE2(ORIGINALTAB IN MTAB1) RETURN MRECORD IS" +
            "\nNEWREC MRECORD;" +
            "\nBEGIN" +
                "\nNEWREC.M1 := ORIGINALTAB;" +
                "\nRETURN NEWREC;" +
            "\nEND GETRECWITHTABLE2;" +
        "\nEND PACKAGE1;";
    static final String DROP_PACKAGE1_MTAB1_TYPE =
        "DROP TYPE PACKAGE1_MTAB1";
    static final String DROP_PACKAGE1_NRECORD_TYPE =
        "DROP TYPE PACKAGE1_NRECORD";
    static final String DROP_PACKAGE1_PACKAGE =
        "DROP PACKAGE PACKAGE1";
    static final String DROP_PACKAGE1_MRECORD_TYPE =
        "DROP TYPE PACKAGE1_MRECORD";

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
                createDbArtifact(conn, CREATE_PACKAGE1_MTAB1_TYPE);
                createDbArtifact(conn, CREATE_PACKAGE1_NRECORD_TYPE);
                createDbArtifact(conn, CREATE_PACKAGE1_MRECORD_TYPE);
                createDbArtifact(conn, CREATE_PACKAGE1_PACKAGE);
                createDbArtifact(conn, CREATE_PACKAGE1_BODY);
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
        DBWS_BUILDER_XML_USERNAME =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<dbws-builder xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
              "<properties>" +
                  "<property name=\"projectName\">PLSQLRecord</property>" +
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
                  "name=\"GetNewRecordTest\" " +
                  "catalogPattern=\"PACKAGE1\" " +
                  "procedurePattern=\"GETNEWREC\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"CopyRecordTest\" " +
                  "catalogPattern=\"PACKAGE1\" " +
                  "procedurePattern=\"COPYREC\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"CopyRecordTest2\" " +
                  "catalogPattern=\"PACKAGE1\" " +
                  "procedurePattern=\"COPYREC2\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"GetRecordWithTableTest\" " +
                  "catalogPattern=\"PACKAGE1\" " +
                  "procedurePattern=\"GETRECWITHTABLE\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"GetRecordWithTableTest2\" " +
                  "catalogPattern=\"PACKAGE1\" " +
                  "procedurePattern=\"GETRECWITHTABLE2\" " +
              "/>" +
            "</dbws-builder>";
          builder = null;
          DBWSTestSuite.setUp(".");
    }

    @AfterClass
    public static void tearDown() {
        if ("true".equalsIgnoreCase(ddl)) {
            dropDbArtifact(conn, DROP_PACKAGE1_PACKAGE);
            dropDbArtifact(conn, DROP_PACKAGE1_MRECORD_TYPE);
            dropDbArtifact(conn, DROP_PACKAGE1_NRECORD_TYPE);
            dropDbArtifact(conn, DROP_PACKAGE1_MTAB1_TYPE);
        }
    }

    @Test
    public void getNewRecordTest() {
        Invocation invocation = new Invocation("GetNewRecordTest");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(RECORD_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String RECORD_XML =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
        "<PACKAGE1_NRECORD xmlns=\"urn:PLSQLRecord\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<n1>new record</n1>" +
          "<n2>100.11</n2>" +
        "</PACKAGE1_NRECORD>";

    /**
     * StoredProcedure test.
     * Copies n1 to new record.n1 appending '.copy'.
     * Copies n2 to new record.n2 adding 0.1 to the amount.
     */
    @Test
    public void copyRecordTest() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object inputRec = unmarshaller.unmarshal(new StringReader(INPUTRECORD_XML));
        Invocation invocation = new Invocation("CopyRecordTest");
        invocation.setParameter("ORIGINALREC", inputRec);
        invocation.setParameter("SUFFIX", ".copy");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(OUTPUTRECORD_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String INPUTRECORD_XML =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
        "<PACKAGE1_NRECORD xmlns=\"urn:PLSQLRecord\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<n1>data</n1>" +
          "<n2>100.00</n2>" +
        "</PACKAGE1_NRECORD>";
    public static final String OUTPUTRECORD_XML =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
        "<PACKAGE1_NRECORD xmlns=\"urn:PLSQLRecord\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<n1>data.copy</n1>" +
          "<n2>100.1</n2>" +
        "</PACKAGE1_NRECORD>";

    /**
     * StoredFunction test.
     * Copies n1 to new record.n1 appending '.copy'.
     * Copies n2 to new record.n2 adding 0.1 to the amount.
     */
    @Test
    public void copyRecordTest2() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object inputRec = unmarshaller.unmarshal(new StringReader(INPUTRECORD_XML));
        Invocation invocation = new Invocation("CopyRecordTest2");
        invocation.setParameter("ORIGINALREC", inputRec);
        invocation.setParameter("SUFFIX", ".copy");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(OUTPUTRECORD_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    /**
     * StoredProcedure test.
     */
    @Test
    public void getRecordWithTableTest() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object inputTable = unmarshaller.unmarshal(new StringReader(TABLE_XML));
        Invocation invocation = new Invocation("GetRecordWithTableTest");
        invocation.setParameter("ORIGINALTAB", inputTable);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(OUTPUTRECORDWITHTABLE_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String TABLE_XML =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
        "<PACKAGE1_MTAB1 xmlns=\"urn:PLSQLRecord\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<item>666</item>" +
        "</PACKAGE1_MTAB1>";
    public static final String OUTPUTRECORDWITHTABLE_XML =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
        "<PACKAGE1_MRECORD xmlns=\"urn:PLSQLRecord\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<m1>" +
              "<item>666</item>" +
          "</m1>" +
        "</PACKAGE1_MRECORD>";

    /**
     * StoredFunction test.
     */
    @Test
    public void getRecordWithTableTest2() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object inputTable = unmarshaller.unmarshal(new StringReader(TABLE_XML));
        Invocation invocation = new Invocation("GetRecordWithTableTest2");
        invocation.setParameter("ORIGINALTAB", inputTable);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(OUTPUTRECORDWITHTABLE_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
}