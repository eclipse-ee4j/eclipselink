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
package dbws.testing.objecttabletype;


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
 * Tests Oracle Object Table types.
 *
 */
public class ObjectTableTypeTestSuite extends DBWSTestSuite {

    static final String CREATE_PERSONTYPE =
        "CREATE OR REPLACE TYPE PERSONTYPE AS OBJECT (" +
            "\nNAME VARCHAR2(20)," +
            "\nAGE NUMBER," +
            "\nGENDER VARCHAR2(1)," +
            "\nINCARCERATED DATE" +
        "\n)";
    static final String CREATE_PERSONTYPE_TABLE =
        "CREATE OR REPLACE TYPE PERSONTYPE_TABLE AS TABLE OF PERSONTYPE";
    static final String CREATE_GET_PERSONTYPE_PROC =
        "CREATE OR REPLACE PROCEDURE GET_PERSONTYPE_TABLE(PTABLE OUT PERSONTYPE_TABLE) AS" +
        "\nBEGIN" +
            "\nPTABLE := PERSONTYPE_TABLE();" +
            "\nPTABLE.EXTEND;" +
            "\nPTABLE(PTABLE.COUNT) := PERSONTYPE('BUBBLES', 32, 'M', '1990-11-19');" +
            "\nPTABLE.EXTEND;" +
            "\nPTABLE(PTABLE.COUNT) := PERSONTYPE('RICKY', 33, 'M', '1985-10-01');" +
            "\nPTABLE.EXTEND;" +
            "\nPTABLE(PTABLE.COUNT) := PERSONTYPE('JULIAN', 35, 'M', '1988-02-07');" +
            "\nPTABLE.EXTEND;" +
            "\nPTABLE(PTABLE.COUNT) := PERSONTYPE('SARAH', 25, 'F', '2002-05-12');" +
            "\nPTABLE.EXTEND;" +
            "\nPTABLE(PTABLE.COUNT) := PERSONTYPE('J-ROC', 27, 'M', '1998-12-17');" +
        "\nEND GET_PERSONTYPE_TABLE;";
    static final String CREATE_GET_PERSONTYPE2_FUNC =
        "CREATE OR REPLACE FUNCTION GET_PERSONTYPE_TABLE2 RETURN PERSONTYPE_TABLE AS" +
        "\nL_DATA PERSONTYPE_TABLE;" +
        "\nBEGIN" +
            "\nGET_PERSONTYPE_TABLE(L_DATA);" +
            "\nRETURN L_DATA;" +
        "\nEND GET_PERSONTYPE_TABLE2;";
    static final String CREATE_ADD_PERSONTYPE_TO_TABLE_PROC =
        "CREATE OR REPLACE PROCEDURE ADD_PERSONTYPE_TO_TABLE(PTYPETOADD IN PERSONTYPE, OLDTABLE IN PERSONTYPE_TABLE, NEWTABLE OUT PERSONTYPE_TABLE) AS" +
        "\nBEGIN" +
          "\nNEWTABLE := OLDTABLE;" +
          "\nNEWTABLE.EXTEND;" +
          "\nNEWTABLE(NEWTABLE.COUNT) := PTYPETOADD;" +
        "\nEND ADD_PERSONTYPE_TO_TABLE;";
    static final String CREATE_ADD_PERSONTYPE_TO_TABLE2_FUNC =
        "CREATE OR REPLACE FUNCTION ADD_PERSONTYPE_TO_TABLE2(PTYPETOADD IN PERSONTYPE, OLDTABLE IN PERSONTYPE_TABLE) RETURN PERSONTYPE_TABLE AS" +
        "\nNEWTABLE PERSONTYPE_TABLE;" +
        "\nBEGIN" +
            "\nADD_PERSONTYPE_TO_TABLE(PTYPETOADD, OLDTABLE, NEWTABLE);" +
            "\nRETURN NEWTABLE;" +
        "\nEND ADD_PERSONTYPE_TO_TABLE2;";

    static final String DROP_GET_PERSONTYPE_TABLE =
        "DROP PROCEDURE GET_PERSONTYPE_TABLE";
    static final String DROP_GET_PERSONTYPE2_FUNC =
        "DROP FUNCTION GET_PERSONTYPE_TABLE2";
    static final String DROP_ADD_PERSONTYPE_TO_TABLE_PROC =
        "DROP PROCEDURE ADD_PERSONTYPE_TO_TABLE";
    static final String DROP_ADD_PERSONTYPE_TO_TABLE2_FUNC =
        "DROP FUNCTION ADD_PERSONTYPE_TO_TABLE2";
    static final String DROP_PERSONTYPE_TABLE =
        "DROP TYPE PERSONTYPE_TABLE";
    static final String DROP_PERSONTYPE =
        "DROP TYPE PERSONTYPE";

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
                createDbArtifact(conn, CREATE_PERSONTYPE);
                createDbArtifact(conn, CREATE_PERSONTYPE_TABLE);
                createDbArtifact(conn, CREATE_GET_PERSONTYPE_PROC);
                createDbArtifact(conn, CREATE_GET_PERSONTYPE2_FUNC);
                createDbArtifact(conn, CREATE_ADD_PERSONTYPE_TO_TABLE_PROC);
                createDbArtifact(conn, CREATE_ADD_PERSONTYPE_TO_TABLE2_FUNC);
            }
            catch (SQLException e) {
                //e.printStackTrace();
            }
        }
        DBWS_BUILDER_XML_USERNAME =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<dbws-builder xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
              "<properties>" +
                  "<property name=\"projectName\">ObjectTableTypeTests</property>" +
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
                  "name=\"GetPersonTypeTable\" " +
                  "catalogPattern=\"TOPLEVEL\" " +
                  "procedurePattern=\"GET_PERSONTYPE_TABLE\" " +
                  "isAdvancedJDBC=\"true\" " +
                  "returnType=\"persontype_tableType\" " +
              "/>" +
              "<procedure " +
                  "name=\"GetPersonTypeTable2\" " +
                  "catalogPattern=\"TOPLEVEL\" " +
                  "procedurePattern=\"GET_PERSONTYPE_TABLE2\" " +
                  "isAdvancedJDBC=\"true\" " +
                  "returnType=\"persontype_tableType\" " +
              "/>" +
              "<procedure " +
                  "name=\"AddPersonTypeToTable\" " +
                  "catalogPattern=\"TOPLEVEL\" " +
                  "procedurePattern=\"ADD_PERSONTYPE_TO_TABLE\" " +
                  "isAdvancedJDBC=\"true\" " +
                  "returnType=\"persontype_tableType\" " +
              "/>" +
              "<procedure " +
                  "name=\"AddPersonTypeToTable2\" " +
                  "catalogPattern=\"TOPLEVEL\" " +
                  "procedurePattern=\"ADD_PERSONTYPE_TO_TABLE2\" " +
                  "isAdvancedJDBC=\"true\" " +
                  "returnType=\"persontype_tableType\" " +
              "/>" +
            "</dbws-builder>";
          builder = null;
          DBWSTestSuite.setUp(".");
    }

    @AfterClass
    public static void tearDown() {
        String ddlDrop = System.getProperty(DATABASE_DDL_DROP_KEY, DEFAULT_DATABASE_DDL_DROP);
        if ("true".equalsIgnoreCase(ddlDrop)) {
            dropDbArtifact(conn, DROP_GET_PERSONTYPE_TABLE);
            dropDbArtifact(conn, DROP_GET_PERSONTYPE2_FUNC);
            dropDbArtifact(conn, DROP_ADD_PERSONTYPE_TO_TABLE_PROC);
            dropDbArtifact(conn, DROP_ADD_PERSONTYPE_TO_TABLE2_FUNC);
            dropDbArtifact(conn, DROP_PERSONTYPE_TABLE);
            dropDbArtifact(conn, DROP_PERSONTYPE);
        }
    }

    @Test
    public void getPersonTypeTable() {
        Invocation invocation = new Invocation("GetPersonTypeTable");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        //marshaller.marshal(result, System.out);
        Document controlDoc = xmlParser.parse(new StringReader(RESULT_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    @Test
    public void getPersonTypeTable2() {
        Invocation invocation = new Invocation("GetPersonTypeTable2");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        //marshaller.marshal(result, System.out);
        Document controlDoc = xmlParser.parse(new StringReader(RESULT_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    static String RESULT_XML =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<persontype_tableType xmlns=\"urn:ObjectTableTypeTests\">" +
        "<item>" +
        "<name>BUBBLES</name>" +
        "<age>32</age>" +
        "<gender>M</gender>" +
        "<incarcerated>1990-11-19</incarcerated>" +
        "</item>" +
        "<item>" +
        "<name>RICKY</name>" +
        "<age>33</age>" +
        "<gender>M</gender>" +
        "<incarcerated>1985-10-01</incarcerated>" +
        "</item>" +
        "<item>" +
        "<name>JULIAN</name>" +
        "<age>35</age>" +
        "<gender>M</gender>" +
        "<incarcerated>1988-02-07</incarcerated>" +
        "</item>" +
        "<item>" +
        "<name>SARAH</name>" +
        "<age>25</age>" +
        "<gender>F</gender>" +
        "<incarcerated>2002-05-12</incarcerated>" +
        "</item>" +
        "<item>" +
        "<name>J-ROC</name>" +
        "<age>27</age>" +
        "<gender>M</gender>" +
        "<incarcerated>1998-12-17</incarcerated>" +
        "</item>" +
        "</persontype_tableType>";

    @Test
    public void addPersonTypeToTable() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object personType = unmarshaller.unmarshal(new StringReader(PTYPE_INPUT_XML));
        Object tableType = unmarshaller.unmarshal(new StringReader(PTABLE_INPUT_XML));

        Invocation invocation = new Invocation("AddPersonTypeToTable");
        invocation.setParameter("PTYPETOADD", personType);
        invocation.setParameter("OLDTABLE", tableType);

        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        //marshaller.marshal(result, System.out);
        Document controlDoc = xmlParser.parse(new StringReader(NEW_PTABLE_OUTPUT_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    @Test
    public void addPersonTypeToTable2() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object personType = unmarshaller.unmarshal(new StringReader(PTYPE_INPUT_XML));
        Object tableType = unmarshaller.unmarshal(new StringReader(PTABLE_INPUT_XML));

        Invocation invocation = new Invocation("AddPersonTypeToTable2");
        invocation.setParameter("PTYPETOADD", personType);
        invocation.setParameter("OLDTABLE", tableType);

        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        //marshaller.marshal(result, System.out);
        Document controlDoc = xmlParser.parse(new StringReader(NEW_PTABLE_OUTPUT_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    static String PTYPE_INPUT_XML =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<persontypeType xmlns=\"urn:ObjectTableTypeTests\">" +
        "<name>COREY</name>" +
        "<age>20</age>" +
        "<gender>M</gender>" +
        "<incarcerated>1997-12-09</incarcerated>" +
        "</persontypeType>";

    static String PTABLE_INPUT_XML =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<persontype_tableType xmlns=\"urn:ObjectTableTypeTests\">" +
            "<item>" +
            "<name>BUBBLES</name>" +
            "<age>32</age>" +
            "<gender>M</gender>" +
            "<incarcerated>1990-11-19</incarcerated>" +
            "</item>" +
            "<item>" +
            "<name>RICKY</name>" +
            "<age>33</age>" +
            "<gender>M</gender>" +
            "<incarcerated>1985-10-01</incarcerated>" +
            "</item>" +
            "<item>" +
            "<name>JULIAN</name>" +
            "<age>35</age>" +
            "<gender>M</gender>" +
            "<incarcerated>1988-02-07</incarcerated>" +
            "</item>" +
            "<item>" +
            "<name>SARAH</name>" +
            "<age>25</age>" +
            "<gender>F</gender>" +
            "<incarcerated>2002-05-12</incarcerated>" +
            "</item>" +
            "<item>" +
            "<name>J-ROC</name>" +
            "<age>27</age>" +
            "<gender>M</gender>" +
            "<incarcerated>1998-12-17</incarcerated>" +
            "</item>" +
            "</persontype_tableType>";

    static String NEW_PTABLE_OUTPUT_XML =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<persontype_tableType xmlns=\"urn:ObjectTableTypeTests\">" +
            "<item>" +
            "<name>BUBBLES</name>" +
            "<age>32</age>" +
            "<gender>M</gender>" +
            "<incarcerated>1990-11-19</incarcerated>" +
            "</item>" +
            "<item>" +
            "<name>RICKY</name>" +
            "<age>33</age>" +
            "<gender>M</gender>" +
            "<incarcerated>1985-10-01</incarcerated>" +
            "</item>" +
            "<item>" +
            "<name>JULIAN</name>" +
            "<age>35</age>" +
            "<gender>M</gender>" +
            "<incarcerated>1988-02-07</incarcerated>" +
            "</item>" +
            "<item>" +
            "<name>SARAH</name>" +
            "<age>25</age>" +
            "<gender>F</gender>" +
            "<incarcerated>2002-05-12</incarcerated>" +
            "</item>" +
            "<item>" +
            "<name>J-ROC</name>" +
            "<age>27</age>" +
            "<gender>M</gender>" +
            "<incarcerated>1998-12-17</incarcerated>" +
            "</item>" +
            "<item>" +
            "<name>COREY</name>" +
            "<age>20</age>" +
            "<gender>M</gender>" +
            "<incarcerated>1997-12-09</incarcerated>" +
            "</item>" +
            "</persontype_tableType>";
}