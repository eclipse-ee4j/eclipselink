/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     David McCann - Aug.17, 2011 - 2.4 - Initial implementation
package dbws.testing.secondarysql;

//javase imports
import static org.eclipse.persistence.tools.dbws.DBWSBuilder.NO_SESSIONS_FILENAME;
import static org.eclipse.persistence.tools.dbws.DBWSBuilder.SESSIONS_FILENAME_KEY;
import static org.eclipse.persistence.tools.dbws.DBWSPackager.ArchiveUse.noArchive;
import static org.eclipse.persistence.tools.dbws.XRPackager.__nullStream;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.Iterator;

import javax.wsdl.WSDLException;

import org.eclipse.persistence.internal.xr.Invocation;
import org.eclipse.persistence.internal.xr.Operation;
import org.eclipse.persistence.internal.xr.XRDynamicEntity_CollectionWrapper;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.tools.dbws.DBWSBuilder;
import org.eclipse.persistence.tools.dbws.DBWSBuilderModel;
import org.eclipse.persistence.tools.dbws.DBWSBuilderModelProject;
import org.eclipse.persistence.tools.dbws.JSR109WebServicePackager;
import org.eclipse.persistence.tools.dbws.SQLOperationModel;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import dbws.testing.DBWSTestSuite;

public class SecondarySQLTestSuite extends DBWSTestSuite {

    static final String CREATE_SECONDARY_TABLE =
        "CREATE TABLE DBWS_SECONDARY (" +
            "\nEMPNO NUMERIC(4)," +
            "\nENAME VARCHAR(10)," +
            "\nJOB VARCHAR(9)," +
            "\nMGR NUMERIC(4)," +
            "\nHIREDATE DATE," +
            "\nSAL DECIMAL(7,2)," +
            "\nCOMM DECIMAL(7,2)," +
            "\nDEPTNO NUMERIC(2)," +
            "\nPRIMARY KEY (EMPNO)" +
        "\n)";
    static final String[] POPULATE_SECONDARY_TABLE = new String[] {
        "INSERT INTO DBWS_SECONDARY VALUES (7369,'SMITH','CLERK',7902,TO_DATE('1980-12-17 00:00:00','YYYY-MM-DD HH24:MI:SS'),800.88,NULL,20)",
        "INSERT INTO DBWS_SECONDARY VALUES (7499,'ALLEN','SALESMAN',7698,TO_DATE('1981-2-20 00:00:00','YYYY-MM-DD HH24:MI:SS'),1600,300,30)",
        "INSERT INTO DBWS_SECONDARY VALUES (7521,'WARD','SALESMAN',7698,TO_DATE('1981-2-22 00:00:00','YYYY-MM-DD HH24:MI:SS'),1250,500,30)",
        "INSERT INTO DBWS_SECONDARY VALUES (7566,'JONES','MANAGER',7839,TO_DATE('1981-4-2 00:00:00','YYYY-MM-DD HH24:MI:SS'),2975,NULL,20)",
        "INSERT INTO DBWS_SECONDARY VALUES (7654,'MARTIN','SALESMAN',7698,TO_DATE('1981-9-28 00:00:00','YYYY-MM-DD HH24:MI:SS'),1250,1400,30)",
        "INSERT INTO DBWS_SECONDARY VALUES (7698,'BLAKE','MANAGER',7839,TO_DATE('1981-5-1 00:00:00','YYYY-MM-DD HH24:MI:SS'),2850,NULL,30)",
        "INSERT INTO DBWS_SECONDARY VALUES (7782,'CLARK','MANAGER',7839,TO_DATE('1981-6-9 00:00:00','YYYY-MM-DD HH24:MI:SS'),2450,NULL,10)",
        "INSERT INTO DBWS_SECONDARY VALUES (7788,'SCOTT','ANALYST',7566,TO_DATE('1981-06-09 00:00:00','YYYY-MM-DD HH24:MI:SS'),3000,NULL,20)",
        "INSERT INTO DBWS_SECONDARY VALUES (7839,'KING','PRESIDENT',NULL,TO_DATE('1981-11-17 00:00:00','YYYY-MM-DD HH24:MI:SS'),5000.99,NULL,10)",
        "INSERT INTO DBWS_SECONDARY VALUES (7844,'TURNER','SALESMAN',7698,TO_DATE('1981-9-8 00:00:00','YYYY-MM-DD HH24:MI:SS'),1500,0,30)",
        "INSERT INTO DBWS_SECONDARY VALUES (7876,'ADAMS','CLERK',7788,TO_DATE('1987-05-23 00:00:00','YYYY-MM-DD HH24:MI:SS'),1100,NULL,20)",
        "INSERT INTO DBWS_SECONDARY VALUES (7900,'JAMES','CLERK',7698,TO_DATE('1981-12-03 00:00:00','YYYY-MM-DD HH24:MI:SS'),950,NULL,30)",
        "INSERT INTO DBWS_SECONDARY VALUES (7902,'FORD','ANALYST',7566,TO_DATE('1981-12-03 00:00:00','YYYY-MM-DD HH24:MI:SS'),3000,NULL,20)",
        "INSERT INTO DBWS_SECONDARY VALUES (7934,'MILLER','CLERK',7782,TO_DATE('1982-01-23 00:00:00','YYYY-MM-DD HH24:MI:SS'),1300,NULL,10)"
        };
    static final String DROP_SECONDARY_TABLE =
        "DROP TABLE DBWS_SECONDARY";
    static final String GET_SECONDARY_BY_NAME = "DBWS_SECONDARY_GETBYNAME";
    static final String CREATE_GET_SECONDARY_BY_NAME_PROC =
        "CREATE OR REPLACE PROCEDURE " + GET_SECONDARY_BY_NAME+ "(N IN VARCHAR2, RESULTS OUT SYS_REFCURSOR) AS" +
        "\nBEGIN" +
            "\nOPEN RESULTS FOR SELECT EMPNO, ENAME, HIREDATE FROM DBWS_SECONDARY WHERE ENAME LIKE N;"+
        "\nEND " + GET_SECONDARY_BY_NAME + ";";
    static final String DROP_GET_SECONDARY_BY_NAME_PROC =
        "DROP PROCEDURE " + GET_SECONDARY_BY_NAME;

    static final String NONSENCE_WHERE_SQL = " WHERE 0=1";
    static final String SECONDARY = "secondarySQL";
    static final String SECONDARY_COUNT_SQL =
        "select count(*) as \"COUNT\", CAST(max(SAL) as NUMBER(7,2)) as \"MAX-Salary\" from DBWS_SECONDARY";
    static final String SECONDARY_COUNT_SCHEMA_TYPE = "secondaryAggregate";
    static final String SECONDARY_ALL_SQL =
        "select * from DBWS_SECONDARY";
    static final String SECONDARY_ALL_SCHEMA_TYPE = "dbws_secondaryType";
    static final String GETBYNAME_SCHEMA_TYPE = "empType";
    static final String GETBYNAME_SCHEMA_SQL =
        "SELECT CAST(NULL AS NUMERIC(4)) AS EMPNO, " +
            "CAST(NULL AS  VARCHAR(10)) AS  ENAME, " +
            "CAST(NULL AS DATE) AS HIREDATE FROM DUAL";

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
                //e.printStackTrace(); ignore
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
            runDdl(conn, CREATE_SECONDARY_TABLE, ddlDebug);
            runDdl(conn, CREATE_GET_SECONDARY_BY_NAME_PROC, ddlDebug);
            try {
                Statement stmt = conn.createStatement();
                for (int i = 0; i < POPULATE_SECONDARY_TABLE.length; i++) {
                    stmt.addBatch(POPULATE_SECONDARY_TABLE[i]);
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
              "<property name=\"projectName\">" + SECONDARY + "</property>" +
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
          "<sql " +
              "name=\"allSecondary\" " +
              "isCollection=\"true\" " +
              "returnType=\"" + SECONDARY_ALL_SCHEMA_TYPE +"\"> " +
              ">" +
              "<statement><![CDATA[" + SECONDARY_ALL_SQL + "]]></statement>" +
              "<build-statement><![CDATA[" + SECONDARY_ALL_SQL + NONSENCE_WHERE_SQL + "]]></build-statement>" +
          "</sql>" +
          "<sql " +
              "name=\"countSecondary\" " +
              "isCollection=\"false\" " +
              "returnType=\"" + SECONDARY_COUNT_SCHEMA_TYPE +"\"> " +
              "<statement><![CDATA[" + SECONDARY_COUNT_SQL + "]]></statement>" +
              "<build-statement><![CDATA[" + SECONDARY_COUNT_SQL + NONSENCE_WHERE_SQL + "]]></build-statement>" +
          "</sql>" +
          "<procedure " +
              "name=\"getByName\" " +
              "catalogPattern=\"TOPLEVEL\" " +
              "procedurePattern=\"" + GET_SECONDARY_BY_NAME + "\" " +
              "isCollection=\"true\" " +
              "returnType=\"" + GETBYNAME_SCHEMA_TYPE + "\"> " +
              "<build-statement><![CDATA[" + GETBYNAME_SCHEMA_SQL + "]]></build-statement>" +
          "</procedure>" +
        "</dbws-builder>";

        builder = new DBWSBuilder();
        DBWSTestSuite.setUp(".");
    }

    @AfterClass
    public static void teardown() {
        if (ddlDrop) {
            runDdl(conn, DROP_GET_SECONDARY_BY_NAME_PROC, ddlDebug);
            runDdl(conn, DROP_SECONDARY_TABLE, ddlDebug);
        }
    }

    @Test
     public void checkSQLOperationModel() {
         SQLOperationModel sqlModel = (SQLOperationModel)builder.operations.get(0);
         assertEquals(SECONDARY_ALL_SQL + NONSENCE_WHERE_SQL , sqlModel.getBuildSql());
         assertFalse(sqlModel.isSimpleXMLFormat());
         assertEquals(SECONDARY_ALL_SCHEMA_TYPE, sqlModel.getReturnType());
     }

     @Test
     public void checkSchema() {
         Document doc = xmlParser.parse(new StringReader(DBWS_SCHEMA_STREAM.toString()));
         Document controlDoc = xmlParser.parse(new StringReader(SCHEMA_CONTROL_DOC));
         assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
     }

     public static final String SCHEMA_CONTROL_DOC =
         REGULAR_XML_HEADER +
         "<xsd:schema xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" targetNamespace=\"urn:secondarySQL\" xmlns=\"urn:secondarySQL\" elementFormDefault=\"qualified\">\n" +
         "   <xsd:complexType name=\"empType\">\n" +
         "      <xsd:sequence>\n" +
         "         <xsd:element name=\"empno\" type=\"xsd:decimal\" minOccurs=\"0\" nillable=\"true\"/>\n" +
         "         <xsd:element name=\"ename\" type=\"xsd:string\" minOccurs=\"0\" nillable=\"true\"/>\n" +
         "         <xsd:element name=\"hiredate\" type=\"xsd:dateTime\" minOccurs=\"0\" nillable=\"true\"/>\n" +
         "      </xsd:sequence>\n" +
         "   </xsd:complexType>\n" +
         "   <xsd:complexType name=\"secondaryAggregate\">\n" +
         "      <xsd:sequence>\n" +
         "         <xsd:element name=\"count\" type=\"xsd:decimal\" minOccurs=\"0\" nillable=\"true\"/>\n" +
         "         <xsd:element name=\"max-salary\" type=\"xsd:decimal\" minOccurs=\"0\" nillable=\"true\"/>\n" +
         "      </xsd:sequence>\n" +
         "   </xsd:complexType>\n" +
         "   <xsd:complexType name=\"dbws_secondaryType\">\n" +
         "      <xsd:sequence>\n" +
         "         <xsd:element name=\"empno\" type=\"xsd:decimal\" minOccurs=\"0\" nillable=\"true\"/>\n" +
         "         <xsd:element name=\"ename\" type=\"xsd:string\" minOccurs=\"0\" nillable=\"true\"/>\n" +
         "         <xsd:element name=\"job\" type=\"xsd:string\" minOccurs=\"0\" nillable=\"true\"/>\n" +
         "         <xsd:element name=\"mgr\" type=\"xsd:decimal\" minOccurs=\"0\" nillable=\"true\"/>\n" +
         "         <xsd:element name=\"hiredate\" type=\"xsd:dateTime\" minOccurs=\"0\" nillable=\"true\"/>\n" +
         "         <xsd:element name=\"sal\" type=\"xsd:decimal\" minOccurs=\"0\" nillable=\"true\"/>\n" +
         "         <xsd:element name=\"comm\" type=\"xsd:decimal\" minOccurs=\"0\" nillable=\"true\"/>\n" +
         "         <xsd:element name=\"deptno\" type=\"xsd:decimal\" minOccurs=\"0\" nillable=\"true\"/>\n" +
         "      </xsd:sequence>\n" +
         "   </xsd:complexType>\n" +
         "   <xsd:element name=\"empType\" type=\"empType\"/>\n" +
         "   <xsd:element name=\"secondaryAggregate\" type=\"secondaryAggregate\"/>\n" +
         "   <xsd:element name=\"dbws_secondaryType\" type=\"dbws_secondaryType\"/>\n" +
         "</xsd:schema>";

     @Test
     public void countSecondary() throws ParseException {
         Invocation invocation = new Invocation("countSecondary");
         Operation op = xrService.getOperation(invocation.getName());
         Object result = op.invoke(xrService, invocation);
         assertNotNull("result is null", result);
         Document doc = xmlPlatform.createDocument();
         XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
         marshaller.marshal(result, doc);
         Document controlDoc = xmlParser.parse(new StringReader(SECONDARY_AGGREGATE_XML));
         assertTrue("control document not same as instance document", comparer.isNodeEqual(controlDoc, doc));
     }
     static final String SECONDARY_AGGREGATE_XML =
         "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
         "<secondaryAggregate xmlns=\"urn:secondarySQL\">" +
             "<count>14</count>" +
             "<max-salary>5000.99</max-salary>" +
         "</secondaryAggregate>";

     @SuppressWarnings("rawtypes")
     @Test
     public void allSecondary() throws ParseException {
         Invocation invocation = new Invocation("allSecondary");
         Operation op = xrService.getOperation(invocation.getName());
         Object result = op.invoke(xrService, invocation);
         assertNotNull("result is null", result);

         XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
         Document doc = xmlPlatform.createDocument();
         Element ec = doc.createElement("collection");
         doc.appendChild(ec);
         XRDynamicEntity_CollectionWrapper xrDynEntityCol = (XRDynamicEntity_CollectionWrapper) result;
         for (Iterator xrIt = xrDynEntityCol.iterator(); xrIt.hasNext(); ) {
             marshaller.marshal(xrIt.next(), ec);
         }
         Document controlDoc = xmlParser.parse(new StringReader(ALL_RESPONSE_MSG));
         assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
     }
     static final String ALL_RESPONSE_MSG =
         "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
         "<collection>" +
            "<dbws_secondaryType xmlns=\"urn:secondarySQL\">" +
              "<empno>7369</empno>" +
              "<ename>SMITH</ename>" +
              "<job>CLERK</job>" +
              "<mgr>7902</mgr>" +
              "<hiredate>1980-12-17T00:00:00-05:00</hiredate>" +
              "<sal>800.88</sal>" +
              "<comm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
              "<deptno>20</deptno>" +
            "</dbws_secondaryType>" +
            "<dbws_secondaryType xmlns=\"urn:secondarySQL\">" +
              "<empno>7499</empno>" +
              "<ename>ALLEN</ename>" +
              "<job>SALESMAN</job>" +
              "<mgr>7698</mgr>" +
              "<hiredate>1981-02-20T00:00:00-05:00</hiredate>" +
              "<sal>1600</sal>" +
              "<comm>300</comm>" +
              "<deptno>30</deptno>" +
           "</dbws_secondaryType>" +
           "<dbws_secondaryType xmlns=\"urn:secondarySQL\">" +
              "<empno>7521</empno>" +
              "<ename>WARD</ename>" +
              "<job>SALESMAN</job>" +
              "<mgr>7698</mgr>" +
              "<hiredate>1981-02-22T00:00:00-05:00</hiredate>" +
              "<sal>1250</sal>" +
              "<comm>500</comm>" +
              "<deptno>30</deptno>" +
           "</dbws_secondaryType>" +
           "<dbws_secondaryType xmlns=\"urn:secondarySQL\">" +
              "<empno>7566</empno>" +
              "<ename>JONES</ename>" +
              "<job>MANAGER</job>" +
              "<mgr>7839</mgr>" +
              "<hiredate>1981-04-02T00:00:00-05:00</hiredate>" +
              "<sal>2975</sal>" +
              "<comm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
              "<deptno>20</deptno>" +
           "</dbws_secondaryType>" +
           "<dbws_secondaryType xmlns=\"urn:secondarySQL\">" +
              "<empno>7654</empno>" +
              "<ename>MARTIN</ename>" +
              "<job>SALESMAN</job>" +
              "<mgr>7698</mgr>" +
              "<hiredate>1981-09-28T00:00:00-04:00</hiredate>" +
              "<sal>1250</sal>" +
              "<comm>1400</comm>" +
              "<deptno>30</deptno>" +
           "</dbws_secondaryType>" +
           "<dbws_secondaryType xmlns=\"urn:secondarySQL\">" +
              "<empno>7698</empno>" +
              "<ename>BLAKE</ename>" +
              "<job>MANAGER</job>" +
              "<mgr>7839</mgr>" +
              "<hiredate>1981-05-01T00:00:00-04:00</hiredate>" +
              "<sal>2850</sal>" +
              "<comm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
              "<deptno>30</deptno>" +
           "</dbws_secondaryType>" +
           "<dbws_secondaryType xmlns=\"urn:secondarySQL\">" +
              "<empno>7782</empno>" +
              "<ename>CLARK</ename>" +
              "<job>MANAGER</job>" +
              "<mgr>7839</mgr>" +
              "<hiredate>1981-06-09T00:00:00-04:00</hiredate>" +
              "<sal>2450</sal>" +
              "<comm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
              "<deptno>10</deptno>" +
           "</dbws_secondaryType>" +
           "<dbws_secondaryType xmlns=\"urn:secondarySQL\">" +
              "<empno>7788</empno>" +
              "<ename>SCOTT</ename>" +
              "<job>ANALYST</job>" +
              "<mgr>7566</mgr>" +
              "<hiredate>1981-06-09T00:00:00-04:00</hiredate>" +
              "<sal>3000</sal>" +
              "<comm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
              "<deptno>20</deptno>" +
           "</dbws_secondaryType>" +
           "<dbws_secondaryType xmlns=\"urn:secondarySQL\">" +
              "<empno>7839</empno>" +
              "<ename>KING</ename>" +
              "<job>PRESIDENT</job>" +
              "<mgr xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
              "<hiredate>1981-11-17T00:00:00-05:00</hiredate>" +
              "<sal>5000.99</sal>" +
              "<comm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
              "<deptno>10</deptno>" +
           "</dbws_secondaryType>" +
           "<dbws_secondaryType xmlns=\"urn:secondarySQL\">" +
              "<empno>7844</empno>" +
              "<ename>TURNER</ename>" +
              "<job>SALESMAN</job>" +
              "<mgr>7698</mgr>" +
              "<hiredate>1981-09-08T00:00:00-04:00</hiredate>" +
              "<sal>1500</sal>" +
              "<comm>0</comm>" +
              "<deptno>30</deptno>" +
           "</dbws_secondaryType>" +
           "<dbws_secondaryType xmlns=\"urn:secondarySQL\">" +
              "<empno>7876</empno>" +
              "<ename>ADAMS</ename>" +
              "<job>CLERK</job>" +
              "<mgr>7788</mgr>" +
              "<hiredate>1987-05-23T00:00:00-04:00</hiredate>" +
              "<sal>1100</sal>" +
              "<comm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
              "<deptno>20</deptno>" +
           "</dbws_secondaryType>" +
           "<dbws_secondaryType xmlns=\"urn:secondarySQL\">" +
              "<empno>7900</empno>" +
              "<ename>JAMES</ename>" +
              "<job>CLERK</job>" +
              "<mgr>7698</mgr>" +
              "<hiredate>1981-12-03T00:00:00-05:00</hiredate>" +
              "<sal>950</sal>" +
              "<comm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
              "<deptno>30</deptno>" +
           "</dbws_secondaryType>" +
           "<dbws_secondaryType xmlns=\"urn:secondarySQL\">" +
              "<empno>7902</empno>" +
              "<ename>FORD</ename>" +
              "<job>ANALYST</job>" +
              "<mgr>7566</mgr>" +
              "<hiredate>1981-12-03T00:00:00-05:00</hiredate>" +
              "<sal>3000</sal>" +
              "<comm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
              "<deptno>20</deptno>" +
           "</dbws_secondaryType>" +
           "<dbws_secondaryType xmlns=\"urn:secondarySQL\">" +
              "<empno>7934</empno>" +
              "<ename>MILLER</ename>" +
              "<job>CLERK</job>" +
              "<mgr>7782</mgr>" +
              "<hiredate>1982-01-23T00:00:00-05:00</hiredate>" +
              "<sal>1300</sal>" +
              "<comm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
              "<deptno>10</deptno>" +
           "</dbws_secondaryType>" +
       "</collection>";

     @Test
     public void testForDuplicateColumns() {
         String username = System.getProperty(DATABASE_USERNAME_KEY, DEFAULT_DATABASE_USERNAME);
         String password = System.getProperty(DATABASE_PASSWORD_KEY, DEFAULT_DATABASE_PASSWORD);
         String url = System.getProperty(DATABASE_URL_KEY, DEFAULT_DATABASE_URL);
         String builderString = DBWS_BUILDER_XML_USERNAME + username + DBWS_BUILDER_XML_PASSWORD +
             password + DBWS_BUILDER_XML_URL + url + DBWS_BUILDER_XML_DRIVER + DATABASE_DRIVER +
               DBWS_BUILDER_XML_PLATFORM + DATABASE_PLATFORM +
                 "</property>" +
              "</properties>" +
            "<sql " +
              "name=\"badColumns\" " +
              "returnType=\"dontCare\"> " +
              "<statement><![CDATA[dontCare]]></statement>" +
              "<build-statement><![CDATA[select ename, ename from dbws_secondary where 0=1]]></build-statement>" +
            "</sql>" +
         "</dbws-builder>";
         XMLContext context = new XMLContext(new DBWSBuilderModelProject());
         XMLUnmarshaller unmarshaller = context.createUnmarshaller();
         DBWSBuilderModel builderModel =
             (DBWSBuilderModel)unmarshaller.unmarshal(new StringReader(builderString));
         DBWSBuilder builder2 = new DBWSBuilder();
         builder2.quiet = true;
         builder2.properties = builderModel.properties;
         builder2.getProperties().put(SESSIONS_FILENAME_KEY, NO_SESSIONS_FILENAME);
         builder2.getTargetNamespace();
         builder2.operations = builderModel.operations;
         builder2.setLogLevel(SessionLog.FINE_LABEL);
         builder2.setPackager(new JSR109WebServicePackager(null, "WebServiceTestPackager", noArchive) {
             @Override
             public void start() {
             }
         });
         try {
            builder2.build(DBWS_SCHEMA_STREAM, __nullStream, DBWS_SERVICE_STREAM, DBWS_OR_STREAM,
                 DBWS_OX_STREAM, __nullStream, __nullStream, DBWS_WSDL_STREAM, __nullStream,
                 __nullStream, __nullStream, __nullStream, null);
         }
         catch (Exception e) {
             assertEquals("Duplicate ResultSet columns not supported", e.getMessage());
         }
     }

     static final String GETBYNAME_RESPONSE_MSG =
         "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
         "<collection>" +
             "<empType xmlns=\"urn:secondarySQL\">" +
                 "<empno>7566</empno>" +
                 "<ename>JONES</ename>" +
                 "<hiredate>1981-04-02T00:00:00-05:00</hiredate>" +
             "</empType>" +
             "<empType xmlns=\"urn:secondarySQL\">" +
                 "<empno>7900</empno>" +
                 "<ename>JAMES</ename>" +
                 "<hiredate>1981-12-03T00:00:00-05:00</hiredate>" +
             "</empType>" +
         "</collection>";

     @SuppressWarnings("rawtypes")
     @Test
     public void getByNameTest() throws ParseException {
         Invocation invocation = new Invocation("getByName");
         invocation.setParameter("N", "J%");
         Operation op = xrService.getOperation(invocation.getName());
         Object result = op.invoke(xrService, invocation);
         assertNotNull("result is null", result);

         XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
         Document doc = xmlPlatform.createDocument();
         Element ec = doc.createElement("collection");
         doc.appendChild(ec);
         XRDynamicEntity_CollectionWrapper xrDynEntityCol = (XRDynamicEntity_CollectionWrapper) result;
         for (Iterator xrIt = xrDynEntityCol.iterator(); xrIt.hasNext(); ) {
             marshaller.marshal(xrIt.next(), ec);
         }
         Document controlDoc = xmlParser.parse(new StringReader(GETBYNAME_RESPONSE_MSG));
         assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
     }
}
