/*******************************************************************************
 * Copyright (c) 1998-2012 Oracle and/or its affiliates. All rights reserved.
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
package dbws.testing.rowtype;

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

//test imports
import dbws.testing.DBWSTestSuite;

/**
 * Tests %ROWTYPE support. If, in a PL/SQL package, you want a record 
 * with fields (names & types, but not constraints) that mirror a
 * JDBC table, without defining the record in the package, %ROWTYPE
 * can be used, i.e. "PARAM1 OUT MYTABLE%ROWTYPE".  
 *
 */
public class RowTypeTestSuite extends DBWSTestSuite {

    static final String WSDL_REF = "ref=\"ns1:RTYPE_TABLE_ROWTYPE\"";
    static final String XSD = 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<xsd:schema xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" targetNamespace=\"urn:rowtype\" xmlns=\"urn:rowtype\" elementFormDefault=\"qualified\">" +
           "<xsd:complexType name=\"RTYPE_TABLE_ROWTYPE\">" +
              "<xsd:sequence>" + 
                 "<xsd:element name=\"id\" type=\"xsd:decimal\" nillable=\"true\"/>" +
                 "<xsd:element name=\"name\" type=\"xsd:string\" nillable=\"true\"/>" +
                 "<xsd:element name=\"since\" type=\"xsd:date\" nillable=\"true\"/>" +
              "</xsd:sequence>" +
           "</xsd:complexType>" +
           "<xsd:element name=\"RTYPE_TABLE_ROWTYPE\" type=\"RTYPE_TABLE_ROWTYPE\"/>" +
        "</xsd:schema>";
    static final String SERVICE = 
       "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
       "<dbws xmlns:ns1=\"urn:rowtype\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<name>rowtype</name>" +
          "<sessions-file>eclipselink-dbws-sessions.xml</sessions-file>" +
          "<query>" +
             "<name>rowtypeTest</name>" +
             "<parameter>" +
                "<name>PARAM1</name>" +
                "<type>xsd:int</type>" +
             "</parameter>" +
             "<result>" +
                "<type>ns1:RTYPE_TABLE_ROWTYPE</type>" +
             "</result>" +
              "<named-query>" +
                 "<name>rowtypeTest</name>" +
              "</named-query>" +
          "</query>" +
          "<query>" +
            "<name>rowtypeTest2</name>" +
             "<parameter>" +
                "<name>PARAM1</name>" +
                "<type>xsd:int</type>" +
             "</parameter>" +
            "<result>" +
               "<type>ns1:RTYPE_TABLE_ROWTYPE</type>" +
            "</result>" +
              "<named-query>" +
                 "<name>rowtypeTest2</name>" +
              "</named-query>" +
         "</query>" +
      "</dbws>";
    
    
    static final String ROWTYPE_TEST_TABLE = "RTYPE_TABLE";
    static final String CREATE_ROWTYPE_TEST_TABLE = "CREATE TABLE " + ROWTYPE_TEST_TABLE + " (" +
        "\nID NUMBER NOT NULL," +
        "\nNAME VARCHAR(25)," +
        "\nSINCE DATE," +
        "\nPRIMARY KEY (ID)" +
    "\n)";
    static final String[] POPULATE_ROWTYPE_TEST_TABLE = new String[] {
        "INSERT INTO " + ROWTYPE_TEST_TABLE + " (ID, NAME, SINCE) VALUES (1, 'Conky', " +
            "TO_DATE('2001-12-25 00:00:00','YYYY-MM-DD HH24:MI:SS'))",
        "INSERT INTO " + ROWTYPE_TEST_TABLE + " (ID, NAME, SINCE) VALUES (2, 'Ricky', " +
            "TO_DATE('2002-02-12 00:00:00','YYYY-MM-DD HH24:MI:SS'))",
        "INSERT INTO " + ROWTYPE_TEST_TABLE + " (ID, NAME, SINCE) VALUES (3, 'Bubbles'," +
            "TO_DATE('2001-10-30 00:00:00','YYYY-MM-DD HH24:MI:SS'))",
        "INSERT INTO " + ROWTYPE_TEST_TABLE + " (ID, NAME, SINCE) VALUES (4, 'Julian', " +
            "TO_DATE('2010-01-01 00:00:00','YYYY-MM-DD HH24:MI:SS'))"
    };
    static final String ROWTYPE_TEST_PACKAGE = "RTYPE_PKG";
    static final String CREATE_ROWTYPE_TEST_PACKAGE =
        "CREATE OR REPLACE PACKAGE " + ROWTYPE_TEST_PACKAGE + " AS" +
            "\nPROCEDURE testProc(PARAM1 IN INTEGER, PARAM2 OUT " + ROWTYPE_TEST_TABLE + "%ROWTYPE);" +
            "\nFUNCTION test(PARAM1 IN INTEGER) RETURN " + ROWTYPE_TEST_TABLE + "%ROWTYPE;" +
        "\nEND " + ROWTYPE_TEST_PACKAGE + ";";
    
    static final String CREATE_ROWTYPE_TEST_PACKAGE_BODY =
        "CREATE OR REPLACE PACKAGE BODY " + ROWTYPE_TEST_PACKAGE + " AS" +
            "\nPROCEDURE testProc(PARAM1 IN INTEGER, PARAM2 OUT " + ROWTYPE_TEST_TABLE + "%ROWTYPE) AS" +
            "\nBEGIN" +
              "\nPARAM2 := test(PARAM1);" +
            "\nEND testProc;" +
            "\nFUNCTION test(PARAM1 IN INTEGER) RETURN " + ROWTYPE_TEST_TABLE + "%ROWTYPE AS" +        
                "\nL_DATA1 " + ROWTYPE_TEST_TABLE + "%ROWTYPE;" +
                "\nCURSOR C_EMP(PARAMTEMP IN INTEGER) IS SELECT * FROM " + ROWTYPE_TEST_TABLE + 
                    " WHERE " + ROWTYPE_TEST_TABLE + ".ID=PARAMTEMP;" +
            "\nBEGIN" +
                "\nOPEN C_EMP(PARAM1);" +
                "\nLOOP" +
                "\nFETCH C_EMP INTO L_DATA1;" +
                "\nEXIT WHEN C_EMP%NOTFOUND;" +
                "\nEND LOOP;" +
                "\nRETURN L_DATA1;" +
            "\nEND test;" +
        "\nEND " + ROWTYPE_TEST_PACKAGE + ";";
    
    static final String DROP_ROWTYPE_TEST_TABLE = "DROP TABLE " + ROWTYPE_TEST_TABLE;
    static final String DROP_ROWTYPE_SHADOWTYPE = "DROP TYPE " + ROWTYPE_TEST_TABLE + "_ROWTYPE";
    static final String DROP_ROWTYPE_TEST_PACKAGE = "DROP PACKAGE " + ROWTYPE_TEST_PACKAGE;

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
            runDdl(conn, CREATE_ROWTYPE_TEST_TABLE, ddlDebug);
            try {
                Statement stmt = conn.createStatement();
                for (int i = 0; i < POPULATE_ROWTYPE_TEST_TABLE.length; i++) {
                    stmt.addBatch(POPULATE_ROWTYPE_TEST_TABLE[i]);
                }
                stmt.executeBatch();
            }
            catch (SQLException e) {
                //e.printStackTrace();
            }
            runDdl(conn, CREATE_ROWTYPE_TEST_PACKAGE, ddlDebug);
            runDdl(conn, CREATE_ROWTYPE_TEST_PACKAGE_BODY, ddlDebug);
        }
        DBWS_BUILDER_XML_USERNAME =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<dbws-builder xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
              "<properties>" +
                  "<property name=\"projectName\">rowtype</property>" +
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
	              "name=\"rowtypeTest2\" " +
	              "catalogPattern=\"" + ROWTYPE_TEST_PACKAGE + "\" " +
	              "procedurePattern=\"testProc\" " +
	           "/>" +
              "<plsql-procedure " +
                  "name=\"rowtypeTest\" " +
                  "catalogPattern=\"" + ROWTYPE_TEST_PACKAGE + "\" " +
                  "procedurePattern=\"test\" " +
               "/>" +
            "</dbws-builder>";
          builder = null;
          DBWSTestSuite.setUp(".");
          
          // execute shadow type ddl to generate JDBC equivalents of PL/SQL types 
          for (String ddl : builder.getTypeDDL()) {
        	  runDdl(conn, ddl, ddlDebug);
          }
    }

    @AfterClass
    public static void tearDown() {
        String ddlDrop = System.getProperty(DATABASE_DDL_DROP_KEY, DEFAULT_DATABASE_DDL_DROP);
        if ("true".equalsIgnoreCase(ddlDrop)) {
            runDdl(conn, DROP_ROWTYPE_TEST_PACKAGE, ddlDebug);
            runDdl(conn, DROP_ROWTYPE_TEST_TABLE, ddlDebug);
        }
        // drop shadow type ddl 
        for (String ddl : builder.getTypeDropDDL()) {
      	  	runDdl(conn, ddl, ddlDebug);
        }
    }

    @Test
    public void rowTypeTest() {
        Invocation invocation = new Invocation("rowtypeTest");
        Operation op = xrService.getOperation(invocation.getName());
        invocation.setParameter("PARAM1", Integer.valueOf(1));
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(RECORD_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    @Test
    public void rowTypeTest2() {
        Invocation invocation = new Invocation("rowtypeTest2");
        Operation op = xrService.getOperation(invocation.getName());
        invocation.setParameter("PARAM1", Integer.valueOf(1));
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(RECORD_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String RECORD_XML =
        STANDALONE_XML_HEADER +
        "<RTYPE_TABLE_ROWTYPE xmlns=\"urn:rowtype\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
            "<id>1</id>" +
            "<name>Conky</name>" +
            "<since>2001-12-25</since>" +
        "</RTYPE_TABLE_ROWTYPE>";

    /**
     * Test element/complex type names do not contain '%'
     */
    @Test
    public void compareXSD() {
        assertNotNull("Schema stream is null", DBWS_SCHEMA_STREAM);
        Document controlDoc = xmlParser.parse(new StringReader(XSD));
        removeEmptyTextNodes(controlDoc);
        Document testDoc = xmlParser.parse(new StringReader(DBWS_SCHEMA_STREAM.toString()));
        removeEmptyTextNodes(testDoc);
        assertTrue("Schema comparison failed.  Expected:\n" + documentToString(controlDoc) + "\nActual\n" + documentToString(testDoc), comparer.isNodeEqual(controlDoc, testDoc));
    }
    
    /**
     * Test type names do not contain '%'
     */
    @Test
    public void compareSessions() {
        assertNotNull("Service stream is null", DBWS_SERVICE_STREAM);
        Document controlDoc = xmlParser.parse(new StringReader(SERVICE));
        removeEmptyTextNodes(controlDoc);
        Document testDoc = xmlParser.parse(new StringReader(DBWS_SERVICE_STREAM.toString()));
        removeEmptyTextNodes(testDoc);
        assertTrue("Service file comparison failed.  Expected:\n" + documentToString(controlDoc) + "\nActual\n" + documentToString(testDoc), comparer.isNodeEqual(controlDoc, testDoc));
    }
    
    /**
     * Test element ref name does not contain '%'
     */
    @Test
    public void compareWSDLElementRef() {
        assertNotNull("WSDL stream is null", DBWS_WSDL_STREAM);
        assertTrue("Expected to find:\n" + WSDL_REF + "\nbut did not.  WSDL:\n" + DBWS_WSDL_STREAM.toString(), DBWS_WSDL_STREAM.toString().contains(WSDL_REF));
    }
}