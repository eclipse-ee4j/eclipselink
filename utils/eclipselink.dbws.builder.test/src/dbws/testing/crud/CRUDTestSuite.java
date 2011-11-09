/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Mike Norman - May 2008, created DBWS test package
 ******************************************************************************/
package dbws.testing.crud;

//javase imports
import java.io.Reader;
import java.io.StringReader;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

//java eXtension imports
import javax.wsdl.WSDLException;

//JUnit4 imports
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

//EclipseLink imports
import org.eclipse.persistence.internal.xr.XRDynamicEntity;
import org.eclipse.persistence.internal.xr.Invocation;
import org.eclipse.persistence.internal.xr.Operation;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;

//testing imports
import dbws.testing.DBWSTestSuite;

public class CRUDTestSuite extends DBWSTestSuite {

    static final String CREATE_CRUD_TABLE =
        "CREATE TABLE IF NOT EXISTS crud_table (" +
            "\nID NUMERIC NOT NULL," +
            "\nNAME VARCHAR(80)," +
            "\nPRIMARY KEY (ID)" +
        "\n)";
    static final String[] POPULATE_CRUD_TABLE = new String[] {
        "insert into crud_table values (1, 'crud1')",
        "insert into crud_table values (2, 'crud2')",
        "insert into crud_table values (3, 'other')"
    };
    static final String DROP_CRUD_TABLE =
        "DROP TABLE crud_table";

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
                createDbArtifact(conn, CREATE_CRUD_TABLE);
            }
            catch (SQLException e) {
                //ignore
            }
            try {
                Statement stmt = conn.createStatement();
                for (int i = 0; i < POPULATE_CRUD_TABLE.length; i++) {
                    stmt.addBatch(POPULATE_CRUD_TABLE[i]);
                }
                stmt.executeBatch();
            }
            catch (SQLException e) {
                //ignore
            }
        }
        DBWS_BUILDER_XML_USERNAME =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<dbws-builder xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
          "<properties>" +
              "<property name=\"projectName\">crud</property>" +
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
          "<table " +
            "schemaPattern=\"%\" " +
            "tableNamePattern=\"crud_table\" " +
            ">" +
            "<sql " +
              "name=\"findByName\" " +
              "returnType=\"crud_tableType\" " +
              "isCollection=\"true\" " +
              ">" +
              "<text><![CDATA[select * from crud_table where name like 'crud%']]></text>" +
            "</sql>" +
          "</table>" +
        "</dbws-builder>";
        builder = null;
        DBWSTestSuite.setUp(".");
    }

    @AfterClass
    public static void tearDown() {
        String ddlDrop = System.getProperty(DATABASE_DDL_DROP_KEY, DEFAULT_DATABASE_DDL_DROP);
        if ("true".equalsIgnoreCase(ddlDrop)) {
            dropDbArtifact(conn, DROP_CRUD_TABLE);
        }
    }

    // hokey naming convention for test methods to assure order-of-operations
    // w.r.t. insert/update/delete

    @Test
    public void test1_readOne() {
        Invocation invocation = new Invocation("findByPrimaryKey_crud_tableType");
        invocation.setParameter("id", 1);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        Document doc = xmlPlatform.createDocument();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(CRUD1_CONTROL_DOC));
        assertTrue("control document not same as instance document",
            comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String CRUD1_CONTROL_DOC =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<crud_tableType xmlns=\"urn:crud\">" +
          "<id>1</id>" +
          "<name>crud1</name>" +
        "</crud_tableType>";

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    public void test2_readAll() {
        Invocation invocation = new Invocation("findAll_crud_tableType");
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
        Document controlDoc = xmlParser.parse(new StringReader(FIND_ALL_CONTROL_DOC));
        assertTrue("control document not same as instance document",
            comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String FIND_ALL_CONTROL_DOC =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?> " +
        "<all>" +
           "<crud_tableType xmlns=\"urn:crud\">" +
            "<id>1</id>" +
            "<name>crud1</name>" +
          "</crud_tableType>" +
          "<crud_tableType xmlns=\"urn:crud\">" +
            "<id>2</id>" +
            "<name>crud2</name>" +
          "</crud_tableType>" +
          "<crud_tableType xmlns=\"urn:crud\">" +
            "<id>3</id>" +
            "<name>other</name>" +
          "</crud_tableType>" +
        "</all>";

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    public void test3_findByName() {
        Invocation invocation = new Invocation("findByName");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        Document doc = xmlPlatform.createDocument();
        Element ec = doc.createElement("some");
        doc.appendChild(ec);
        for (Object r : (Vector)result) {
            marshaller.marshal(r, ec);
        }
        Document controlDoc = xmlParser.parse(new StringReader(FIND_BY_NAME_CONTROL_DOC));
        assertTrue("control document not same as instance document",
            comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String FIND_BY_NAME_CONTROL_DOC =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?> " +
        "<some>" +
          "<crud_tableType xmlns=\"urn:crud\">" +
            "<id>1</id>" +
            "<name>crud1</name>" +
          "</crud_tableType>" +
          "<crud_tableType xmlns=\"urn:crud\">" +
            "<id>2</id>" +
            "<name>crud2</name>" +
          "</crud_tableType>" +
        "</some>";

    @Test
    public void test4_update() {
        XMLUnmarshaller unMarshaller = xrService.getXMLContext().createUnmarshaller();
        Reader reader = new StringReader(CRUD1_CONTROL_DOC);
        InputSource inputSource = new InputSource(reader);
        XRDynamicEntity firstEmp = (XRDynamicEntity)unMarshaller.unmarshal(inputSource);
        firstEmp.set("name", "some other name");
        Invocation invocation = new Invocation("update_crud_tableType");
        invocation.setParameter("theInstance", firstEmp);
        Operation op = xrService.getOperation(invocation.getName());
        op.invoke(xrService, invocation);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void test5_delete() {
        Invocation invocation = new Invocation("findAll_crud_tableType");
        Operation op = xrService.getOperation(invocation.getName());
        Vector<XRDynamicEntity> result = (Vector<XRDynamicEntity>)op.invoke(xrService, invocation);
        XRDynamicEntity firstEmp = result.firstElement();
        Invocation invocation2 = new Invocation("delete_crud_tableType");
        invocation2.setParameter("id", firstEmp.get("id"));
        Operation op2 = xrService.getOperation(invocation2.getName());
        op2.invoke(xrService, invocation2);
        Vector<XRDynamicEntity> result2 = (Vector<XRDynamicEntity>)op.invoke(xrService, invocation);
        assertTrue("Wrong number of employees", result2.size() == 2);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void test6_create() {
        XMLUnmarshaller unMarshaller = xrService.getXMLContext().createUnmarshaller();
        Reader reader = new StringReader(CRUD1_CONTROL_DOC);
        InputSource inputSource = new InputSource(reader);
        XRDynamicEntity anotherEmployee = (XRDynamicEntity)unMarshaller.unmarshal(inputSource);
        Invocation invocation = new Invocation("create_crud_tableType");
        invocation.setParameter("theInstance", anotherEmployee);
        Operation op = xrService.getOperation(invocation.getName());
        op.invoke(xrService, invocation);
        Invocation invocation2 = new Invocation("findAll_crud_tableType");
        Operation op2 = xrService.getOperation(invocation2.getName());
        Vector<XRDynamicEntity> result2 = (Vector<XRDynamicEntity>)op2.invoke(xrService, invocation2);
        assertTrue("Wrong number of employees", result2.size() == 3);
    }
}
