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
 *     David McCann - June 05, 2012 - 2.4.1 - Initial implementation
 ******************************************************************************/
package dbws.testing.prependedschema;

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
 * Tests parser handling of types, etc. that have the schema name (aka user name)
 * prepended to the name, i.e. 'SCOTT.MY_TYPE'. 
 *
 */
public class PrependedSchemaTestSuite extends DBWSTestSuite {
    static String username;
    static {
        username = System.getProperty(DATABASE_USERNAME_KEY, DEFAULT_DATABASE_USERNAME);
    }

    static final String CREATE_TESMAN_TABLE2 =
        "CREATE TABLE TESMAN_TABLE2(SRNO NUMBER, DETAIL "+username+".TESMAN_TYPE2)" +
          "\nLOGGING" +
          "\nTABLESPACE USERS" +
          "\nPCTFREE 10" +
          "\nINITRANS 1" +
          "\nSTORAGE" +
          "\n(" +
            "\nINITIAL 65536" +
            "\nMINEXTENTS 1" +
            "\nMAXEXTENTS UNLIMITED" +
            "\nBUFFER_POOL DEFAULT" +
          "\n)";
    static final String CREATE_TESMAN_TYPE1 =
        "CREATE TYPE TESMAN_TYPE1 AS OBJECT" +
          "\n(" +
            "\nACCT NUMBER, " +
            "\nSTATE VARCHAR2(30)," + 
            "\nDIVISION VARCHAR2(30)," +
            "\nCOUNTRY VARCHAR2(30)" +
          "\n);";
    static final String CREATE_TESMAN_TYPE2 =
        "CREATE TYPE TESMAN_TYPE2 AS OBJECT" +
          "\n(" +
            "\nPNR NUMBER, " +
            "\nCOMPANY VARCHAR2(30)," +
            "\nSE VARCHAR2(30)," +
            "\nSCRIP VARCHAR2(30)," +
            "\nTT "+username+".TESMAN_TYPE1" +
          "\n);";
    
    static final String[] POPULATE_TESMAN_TABLE2 = new String[] {
        "INSERT INTO TESMAN_TABLE2 (SRNO, DETAIL) VALUES (1, " +
            "TESMAN_TYPE2(11, 'SOMECOMPANY', 'SEVAL', 'SCRIPVAL'," +
            "TESMAN_TYPE1(45, 'SOMESTATE', 'SOMEDIV', 'CANADA')))",
        "INSERT INTO TESMAN_TABLE2 (SRNO, DETAIL) VALUES (666, " +
            "TESMAN_TYPE2(12, 'SOMECO', 'SECKS', 'SCRIPPER'," +
            "TESMAN_TYPE1(46, 'COMASTATE', 'MYDIV', 'CANADA')))"};

    static final String CREATE_TESMAN_PACK = 
        "create or replace PACKAGE TESMANPACK AS" +
          "\nFUNCTION TESMANFUNC17( param1 in INTEGER) return "+username+".TESMAN_TABLE2%ROWTYPE;" +
        "\nEND TESMANPACK;";
    static final String CREATE_TESMAN_BODY =
        "create or replace PACKAGE BODY TESMANPACK AS" +
          "\nFUNCTION TESMANFUNC17(param1 in INTEGER) return "+username+".TESMAN_TABLE2%ROWTYPE AS" +
            "\nl_data1 "+username+".TESMAN_TABLE2%ROWTYPE;" +
            "\nCURSOR c_emp(paramtemp in INTEGER) IS" +
              "\nselect * from TESMAN_TABLE2 te WHERE te.srno=paramtemp;" +
          "\nbegin" + 
            "\nopen c_emp(param1);" +
            "\nLOOP" +
              "\nFETCH c_emp into l_data1;" +
              "\nexit when c_emp%NOTFOUND;" +
            "\nEND LOOP;" +
            "\nRETURN l_data1;" +
          "\nEND TESMANFUNC17;" +
        "\nEND TESMANPACK;";
    
    static final String DROP_TESMAN_BODY =
        "DROP PACKAGE BODY TESMANPACK";
    static final String DROP_TESMAN_PACK =
        "DROP PACKAGE TESMANPACK";
    static final String DROP_TESMAN_TABLE2 =
        "DROP TABLE TESMAN_TABLE2";
    static final String DROP_TESMAN_TYPE2 =
        "DROP TYPE TESMAN_TYPE2";
    static final String DROP_TESMAN_TYPE1 =
        "DROP TYPE TESMAN_TYPE1";

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
            runDdl(conn, CREATE_TESMAN_TYPE1, ddlDebug);
            runDdl(conn, CREATE_TESMAN_TYPE2, ddlDebug);
            runDdl(conn, CREATE_TESMAN_TABLE2, ddlDebug);
            try {
                Statement stmt = conn.createStatement();
                for (int i = 0; i < POPULATE_TESMAN_TABLE2.length; i++) {
                    stmt.addBatch(POPULATE_TESMAN_TABLE2[i]);
                }
                stmt.executeBatch();
            }
            catch (SQLException e) {e.printStackTrace();}
            runDdl(conn, CREATE_TESMAN_PACK, ddlDebug);
            runDdl(conn, CREATE_TESMAN_BODY, ddlDebug);
        }
        DBWS_BUILDER_XML_USERNAME =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<dbws-builder xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
              "<properties>" +
                  "<property name=\"projectName\">PrependedSchema</property>" +
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
                  "name=\"TestFunc17\" " +
                  "catalogPattern=\"TESMANPACK\" " +
                  "procedurePattern=\"TESMANFUNC17\" " +
              "/>" +
            "</dbws-builder>";
          builder = null;
          DBWSTestSuite.setUp(".");

          // execute shadow type ddl to generate JDBC equivalents of PL/SQL types 
          for (String ddl : builder.getTypeDDL()) {
              //System.out.println("create: " + ddl);
              runDdl(conn, ddl, ddlDebug);
          }
    }

    @AfterClass
    public static void tearDown() {
        // drop shadow type ddl 
        for (String ddl : builder.getTypeDropDDL()) {
            // may need to strip off trailing ';'
            try {
                int lastIdx = ddl.lastIndexOf(";");
                if (lastIdx == (ddl.length() - 1)) {
                    ddl = ddl.substring(0, ddl.length() - 1);
                }
            } catch (Exception xxx) {}
            //System.out.println("drop: " + ddl);
            runDdl(conn, ddl, ddlDebug);
        }
        if (ddlDrop) {
            runDdl(conn, DROP_TESMAN_BODY, ddlDebug);
            runDdl(conn, DROP_TESMAN_PACK, ddlDebug);
        	runDdl(conn, DROP_TESMAN_TABLE2, ddlDebug);
            runDdl(conn, DROP_TESMAN_TYPE2, ddlDebug);
            runDdl(conn, DROP_TESMAN_TYPE1, ddlDebug);
        }
    }
    
    @Test
    public void test() {
        Invocation invocation = new Invocation("TestFunc17");
        invocation.setParameter("param1", 666);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(TESMAN_TABLE2_ROWTYPE));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    public static final String TESMAN_TABLE2_ROWTYPE =
        STANDALONE_XML_HEADER +
        "<TESMAN_TABLE2_ROWTYPE xmlns=\"urn:PrependedSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
           "<srno>666</srno>" +
           "<detail>" +
              "<pnr>12</pnr>" +
              "<company>SOMECO</company>" +
              "<se>SECKS</se>" +
              "<scrip>SCRIPPER</scrip>" +
              "<tt>" +
                 "<acct>46</acct>" +
                 "<state>COMASTATE</state>" +
                 "<division>MYDIV</division>" +
                 "<country>CANADA</country>" +
              "</tt>" +
           "</detail>" +
        "</TESMAN_TABLE2_ROWTYPE>";
}
