/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Mike Norman - May 2008, created DBWS test package
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
            runDdl(conn, CREATE_CRUD_TABLE, ddlDebug);
            try {
                Statement stmt = conn.createStatement();
                for (int i = 0; i < POPULATE_CRUD_TABLE.length; i++) {
                    stmt.addBatch(POPULATE_CRUD_TABLE[i]);
                }
                stmt.executeBatch();
            }
            catch (SQLException e) {
                if (ddlDebug) {
                    e.printStackTrace();
                }
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
        if (ddlDrop) {
            runDdl(conn, DROP_CRUD_TABLE, ddlDebug);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testCRUDLifecycle() {
        // test findByPK
        Invocation invocation = new Invocation("findByPrimaryKey_Crud_tableType");
        invocation.setParameter("id", 1);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        Document doc = xmlPlatform.createDocument();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(CRUD1_CONTROL_DOC));
        assertTrue("findByPK failed:  control document not same as instance document",
            comparer.isNodeEqual(controlDoc, doc));
        // test findAll
        invocation = new Invocation("findAll_Crud_tableType");
        op = xrService.getOperation(invocation.getName());
        result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        marshaller = xrService.getXMLContext().createMarshaller();
        doc = xmlPlatform.createDocument();
        Element ec = doc.createElement("all");
        doc.appendChild(ec);
        for (Object r : (Vector)result) {
            marshaller.marshal(r, ec);
        }
        controlDoc = xmlParser.parse(new StringReader(FIND_ALL_CONTROL_DOC));
        assertTrue("findAll failed:  control document not same as instance document",
            comparer.isNodeEqual(controlDoc, doc));
        // test findByName
        invocation = new Invocation("findByName");
        op = xrService.getOperation(invocation.getName());
        result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        marshaller = xrService.getXMLContext().createMarshaller();
        doc = xmlPlatform.createDocument();
        ec = doc.createElement("some");
        doc.appendChild(ec);
        for (Object r : (Vector)result) {
            marshaller.marshal(r, ec);
        }
        controlDoc = xmlParser.parse(new StringReader(FIND_BY_NAME_CONTROL_DOC));
        assertTrue("findByName failed:  control document not same as instance document",
            comparer.isNodeEqual(controlDoc, doc));
        // test update
        XMLUnmarshaller unMarshaller = xrService.getXMLContext().createUnmarshaller();
        Reader reader = new StringReader(CRUD1_CONTROL_DOC);
        InputSource inputSource = new InputSource(reader);
        XRDynamicEntity firstEmp = (XRDynamicEntity)unMarshaller.unmarshal(inputSource);
        firstEmp.set("name", "some other name");
        invocation = new Invocation("update_Crud_tableType");
        invocation.setParameter("theInstance", firstEmp);
        op = xrService.getOperation(invocation.getName());
        op.invoke(xrService, invocation);
        // test delete
        invocation = new Invocation("findAll_Crud_tableType");
        op = xrService.getOperation(invocation.getName());
        Vector<XRDynamicEntity> result1 = (Vector<XRDynamicEntity>)op.invoke(xrService, invocation);
        firstEmp = result1.firstElement();
        Invocation invocation2 = new Invocation("delete_Crud_tableType");
        invocation2.setParameter("id", firstEmp.get("id"));
        Operation op2 = xrService.getOperation(invocation2.getName());
        op2.invoke(xrService, invocation2);
        Vector<XRDynamicEntity> result2 = (Vector<XRDynamicEntity>)op.invoke(xrService, invocation);
        assertTrue("Delete failed:  wrong number of employees", result2.size() == 2);
        // test create
        unMarshaller = xrService.getXMLContext().createUnmarshaller();
        reader = new StringReader(CRUD1_CONTROL_DOC);
        inputSource = new InputSource(reader);
        XRDynamicEntity anotherEmployee = (XRDynamicEntity)unMarshaller.unmarshal(inputSource);
        invocation = new Invocation("create_Crud_tableType");
        invocation.setParameter("theInstance", anotherEmployee);
        op = xrService.getOperation(invocation.getName());
        op.invoke(xrService, invocation);
        invocation2 = new Invocation("findAll_Crud_tableType");
        op2 = xrService.getOperation(invocation2.getName());
        Vector<XRDynamicEntity> result3 = (Vector<XRDynamicEntity>)op2.invoke(xrService, invocation2);
        assertTrue("Create failed:  wrong number of employees", result3.size() == 3);
    }

    public static final String CRUD1_CONTROL_DOC =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<crud_tableType xmlns=\"urn:crud\">" +
          "<id>1</id>" +
          "<name>crud1</name>" +
        "</crud_tableType>";
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
}
