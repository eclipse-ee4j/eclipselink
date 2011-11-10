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
 *     David McCann - Novebmer 9, 2011 - 2.4 - Initial implementation
 ******************************************************************************/
package dbws.testing.toplevelsimpleplsqlsp;

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
import org.eclipse.persistence.tools.dbws.DBWSBuilder;

//test imports
import dbws.testing.DBWSTestSuite;

/**
 * Tests top-level procedures with PL/SQL arguments.
 *
 */
public class TopLevelSimplePLSQLSPTestSuite extends DBWSTestSuite {

    static final String CREATE_BOOL_IN_PROC =
        "CREATE OR REPLACE PROCEDURE BOOL_IN_TEST(X IN BOOLEAN) AS" +
        "\nBEGIN" +
            "\nNULL;" +
        "\nEND BOOL_IN_TEST;";

    static final String DROP_BOOL_IN_PROC =
        "DROP PROCEDURE BOOL_IN_TEST";

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
                createDbArtifact(conn, CREATE_BOOL_IN_PROC);
            }
            catch (SQLException e) {
                //e.printStackTrace();
            }
        }
        DBWS_BUILDER_XML_USERNAME =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<dbws-builder xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
              "<properties>" +
                  "<property name=\"projectName\">TopLevelSimplePLSQLSP</property>" +
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
                  "name=\"testBoolean\" " +
                  "catalogPattern=\"TOPLEVEL\" " +
                  "procedurePattern=\"BOOL_IN_TEST\" " +
                  "isSimpleXMLFormat=\"true\" " +
                  "returnType=\"xsd:int\" " +
               "/>" +
            "</dbws-builder>";
          builder = new DBWSBuilder();
          DBWSTestSuite.setUp(".");
    }

    @AfterClass
    public static void tearDown() {
        String ddlDrop = System.getProperty(DATABASE_DDL_DROP_KEY, DEFAULT_DATABASE_DDL_DROP);
        if ("true".equalsIgnoreCase(ddlDrop)) {
            dropDbArtifact(conn, DROP_BOOL_IN_PROC);
        }
    }
    
    @Test
    public void testBoolean() {
        Invocation invocation = new Invocation("testBoolean");
        invocation.setParameter("X", Integer.valueOf(1));
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(TEST_BOOLEAN_RESULT));
        assertTrue("Control document not same as instance document. Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String TEST_BOOLEAN_RESULT =
      "<?xml version = '1.0' encoding = 'UTF-8'?>" +
      "<simple-xml-format>" +
         "<simple-xml>" +
            "<result>1</result>" +
         "</simple-xml>" +
      "</simple-xml-format>";
}