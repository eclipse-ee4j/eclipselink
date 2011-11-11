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
package dbws.testing.simpletablewithnestedsql;

//javase imports
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

//java eXtension imports
import javax.wsdl.WSDLException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

//JUnit4 imports
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

//EclipseLink imports
import org.eclipse.persistence.internal.oxm.schema.SchemaModelProject;
import org.eclipse.persistence.internal.oxm.schema.model.ComplexType;
import org.eclipse.persistence.internal.oxm.schema.model.Element;
import org.eclipse.persistence.internal.oxm.schema.model.Schema;
import org.eclipse.persistence.internal.xr.Invocation;
import org.eclipse.persistence.internal.xr.Operation;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.tools.dbws.DBWSBuilder;

import static org.eclipse.persistence.internal.dbws.ProviderHelper.MATCH_SCHEMA;

//testing imports
import dbws.testing.DBWSTestSuite;

public class SimpleTableWithNestedSQLTestSuite extends DBWSTestSuite {

    static final String CREATE_SIMPLE_TABLE =
        "CREATE TABLE SIMPLETABLE2 (" +
            "\nID NUMBER NOT NULL," +
            "\nNAME VARCHAR(25)," +
            "\nSINCE DATE," +
            "\nPRIMARY KEY (ID)" +
        "\n)";
    static final String[] POPULATE_SIMPLE_TABLE = new String[] {
        "INSERT INTO SIMPLETABLE2 (ID, NAME, SINCE) VALUES (1, 'mike', '2001-12-25')",
        "INSERT INTO SIMPLETABLE2 (ID, NAME, SINCE) VALUES (2, 'blaise','2002-02-12')",
        "INSERT INTO SIMPLETABLE2 (ID, NAME, SINCE) VALUES (3, 'rick','2001-10-30')",
        "INSERT INTO SIMPLETABLE2 (ID, NAME, SINCE) VALUES (4, 'mikey', '2010-01-01')"
    };
    static final String DROP_SIMPLE_TABLE =
        "DROP TABLE SIMPLETABLE2";
    static final String FINDBYNAME_RESPONSETYPE = "findByNameResponseType";
    static final String TABLE_ALIAS ="ns1:simpletable2Type";

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
                createDbArtifact(conn, CREATE_SIMPLE_TABLE);
            }
            catch (SQLException e) {
                //e.printStackTrace();
            }
            try {
                Statement stmt = conn.createStatement();
                for (int i = 0; i < POPULATE_SIMPLE_TABLE.length; i++) {
                    stmt.addBatch(POPULATE_SIMPLE_TABLE[i]);
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
                "<property name=\"projectName\">simpletable2</property>" +
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
              "tableNamePattern=\"SIMPLETABLE2\" " +
              ">" +
              "<sql " +
                "name=\"findByName\" " +
                "isCollection=\"true\" " +
                "returnType=\"simpletable2Type\" " +
                ">" +
                "<text><![CDATA[select * from SIMPLETABLE2 where NAME like ?]]></text>" +
                "<binding name=\"NAME\" type=\"xsd:string\"/>" +
              "</sql>" +
            "</table>" +
          "</dbws-builder>";
        builder = new DBWSBuilder();
        DBWSTestSuite.setUp(".");
    }

    @AfterClass
    public static void tearDown() {
        String ddlDrop = System.getProperty(DATABASE_DDL_DROP_KEY, DEFAULT_DATABASE_DDL_DROP);
        if ("true".equalsIgnoreCase(ddlDrop)) {
            dropDbArtifact(conn, DROP_SIMPLE_TABLE);
        }
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void findByNameTest() {
        Invocation invocation = new Invocation("findByName");
        invocation.setParameter("NAME", "m%");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        Document doc = xmlPlatform.createDocument();
        org.w3c.dom.Element ec = doc.createElement("collection");
        doc.appendChild(ec);
        for (Object r : (Vector)result) {
            marshaller.marshal(r, ec);
        }
        Document controlDoc = xmlParser.parse(new StringReader(FIND_BY_NAME_CONTROL_DOC));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    static String FIND_BY_NAME_CONTROL_DOC =
        REGULAR_XML_HEADER +
        "<collection>" +
            "<simpletable2Type xmlns=\"urn:simpletable2\">" +
                "<id>1</id>" +
                "<name>mike</name>" +
                "<since>2001-12-25</since>" +
            "</simpletable2Type>" +
            "<simpletable2Type xmlns=\"urn:simpletable2\">" +
                "<id>4</id>" +
                "<name>mikey</name>" +
                "<since>2010-01-01</since>" +
            "</simpletable2Type>" +
        "</collection>";

    @Test
    public void validateElementRefType() throws TransformerFactoryConfigurationError, TransformerException {
        StringWriter sw = new StringWriter();
        StreamSource wsdlStreamSource = new StreamSource(new StringReader(DBWS_WSDL_STREAM.toString()));
        Transformer t = TransformerFactory.newInstance().newTransformer(new StreamSource(
            new StringReader(MATCH_SCHEMA)));
        StreamResult streamResult = new StreamResult(sw);
        t.transform(wsdlStreamSource, streamResult);
        sw.toString();
        SchemaModelProject schemaProject = new SchemaModelProject();
        XMLContext xmlContext2 = new XMLContext(schemaProject);
        XMLUnmarshaller unmarshaller = xmlContext2.createUnmarshaller();
        Schema schema = (Schema)unmarshaller.unmarshal(new StringReader(sw.toString()));
        ComplexType findByNameResponseType =
            (ComplexType) schema.getTopLevelComplexTypes().get(FINDBYNAME_RESPONSETYPE);
        Element result = (Element)findByNameResponseType.getSequence().getElements().get(0);
        Element unnamed = (Element)result.getComplexType().getSequence().getElements().get(0);
        assertTrue("wrong refType for " + FINDBYNAME_RESPONSETYPE, TABLE_ALIAS.equals(unnamed.getRef()));
    }
}