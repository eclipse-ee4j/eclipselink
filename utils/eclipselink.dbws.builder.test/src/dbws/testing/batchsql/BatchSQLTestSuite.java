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
 * David McCann - 2.4 - Initial implementation
 ******************************************************************************/
package dbws.testing.batchsql;

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
import org.eclipse.persistence.internal.xr.ValueObject;
import org.eclipse.persistence.oxm.XMLMarshaller;

//testing imports
import dbws.testing.DBWSTestSuite;

public class BatchSQLTestSuite extends DBWSTestSuite {

    static final String CREATE_BATCH1_TABLE =
        "CREATE TABLE IF NOT EXISTS batch1 (" +
            "\nEMPNO NUMERIC(4)," +
            "\nENAME VARCHAR(10)," +
            "\nJOB VARCHAR(9)," +
            "\nMGR NUMERIC(4)," +
            "\nHIREDATE DATE," +
            "\nSAL NUMERIC(7,2)," +
            "\nCOMM NUMERIC(7,2)," +
            "\nDEPTNO NUMERIC(2)," +
            "\nPRIMARY KEY (EMPNO)" +
        "\n)";
    static final String CREATE_BATCH2_TABLE =
        "CREATE TABLE IF NOT EXISTS batch2 (" +
            "\nJOB VARCHAR(9)," +
            "\nAVGSAL NUMERIC(7,2)," +
            "\nPRIMARY KEY (JOB)" +
        "\n)";
    static String[] POPULATE_BATCH1_TABLE = new String[] {
        "INSERT INTO batch1 VALUES (7369,'SMITH','CLERK',7902,'1980-12-17',800,NULL,20)",
        "INSERT INTO batch1 VALUES (7499,'ALLEN','SALESMAN',7698,'1981-2-20',1600,300,30)",
        "INSERT INTO batch1 VALUES (7521,'WARD','SALESMAN',7698,'1981-2-22',1250,500,30)",
        "INSERT INTO batch1 VALUES (7566,'JONES','MANAGER',7839,'1981-4-2',2975,NULL,20)",
        "INSERT INTO batch1 VALUES (7654,'MARTIN','SALESMAN',7698,'1981-9-28',1250,1400,30)",
        "INSERT INTO batch1 VALUES (7698,'BLAKE','MANAGER',7839,'1981-5-1',2850,NULL,30)",
        "INSERT INTO batch1 VALUES (7782,'CLARK','MANAGER',7839,'1981-6-9',2450,NULL,10)",
        "INSERT INTO batch1 VALUES (7788,'SCOTT','ANALYST',7566,'1981-06-09',3000,NULL,20)",
        "INSERT INTO batch1 VALUES (7839,'KING','PRESIDENT',NULL,'1981-11-17',5000,NULL,10)",
        "INSERT INTO batch1 VALUES (7844,'TURNER','SALESMAN',7698,'1981-9-8',1500,0,30)",
        "INSERT INTO batch1 VALUES (7876,'ADAMS','CLERK',7788,'1987-05-23',1100,NULL,20)",
        "INSERT INTO batch1 VALUES (7900,'JAMES','CLERK',7698,'1981-12-03',950,NULL,30)",
        "INSERT INTO batch1 VALUES (7902,'FORD','ANALYST',7566,'1981-12-03',3000,NULL,20)",
        "INSERT INTO batch1 VALUES (7934,'MILLER','CLERK',7782,'1982-01-23',1300,NULL,10)"
    };
    static final String[] POPULATE_BATCH2_TABLE = new String[] {
        "INSERT INTO batch2 VALUES ('CLERK',0)",
        "INSERT INTO batch2 VALUES ('SALESMAN',0)",
        "INSERT INTO batch2 VALUES ('MANAGER',0)",
        "INSERT INTO batch2 VALUES ('ANALYST',0)",
        "INSERT INTO batch2 VALUES ('PRESIDENT',0)"
    };
    static final String DROP_BATCH1_TABLE =
        "DROP TABLE BATCH1";
    static final String DROP_BATCH2_TABLE =
        "DROP TABLE BATCH2";

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
                createDbArtifact(conn, CREATE_BATCH1_TABLE);
                createDbArtifact(conn, CREATE_BATCH2_TABLE);
            }
            catch (SQLException e) {
                //ignore
            }
            try {
                Statement stmt = conn.createStatement();
                for (int i = 0; i < POPULATE_BATCH1_TABLE.length; i++) {
                    stmt.addBatch(POPULATE_BATCH1_TABLE[i]);
                }
                for (int i = 0; i < POPULATE_BATCH2_TABLE.length; i++) {
                    stmt.addBatch(POPULATE_BATCH2_TABLE[i]);
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
                "<property name=\"projectName\">batchSQL</property>" +
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
              "tableNamePattern=\"batch2\" " +
              ">" +
              "<sql " +
                "name=\"getAverageSalary\" " +
                "isCollection=\"false\" " +
                "simpleXMLFormatTag=\"avg-salary\" " +
                ">" +
                  "<text><![CDATA[SELECT AVGSAL as \"clerk-avg\" FROM batch2 WHERE JOB='CLERK']]></text>" +
                "</sql>" +
              "</table>" +
              "<batch-sql " +
                "name=\"avgSalary\" " +
                ">" +
                "<batch-statement><![CDATA[" +
                    "START TRANSACTION\n" +
                    "SELECT @A:=AVG(SAL) FROM batch1 WHERE JOB='CLERK'\n" +
                    "UPDATE batch2 SET AVGSAL=@A WHERE JOB='CLERK'\n" +
                    "COMMIT\n" +
                    "]]>" +
                "</batch-statement> " +
              "</batch-sql>" +
              "<batch-sql " +
              "name=\"invalidSQL\" " +
              ">" +
              "<batch-statement><![CDATA[" +
                  "START TRANSACTION\n" +
                  "SELECT @A:=666(SAL) FROM batch1 WHERE JOB='CLERK'\n" +
                  "UPDATE batch2 SET AVGSAL=@A WHERE JOB='CLERK'\n" +
                  "COMMIT\n" +
                  "]]>" +
              "</batch-statement> " +
            "</batch-sql>" +
          "</dbws-builder>";
        builder = null;
        DBWSTestSuite.setUp(".");
    }

    @AfterClass
    public static void tearDown() {
        String ddlDrop = System.getProperty(DATABASE_DDL_DROP_KEY, DEFAULT_DATABASE_DDL_DROP);
        if ("true".equalsIgnoreCase(ddlDrop)) {
            dropDbArtifact(conn, DROP_BATCH1_TABLE);
            dropDbArtifact(conn, DROP_BATCH2_TABLE);
        }
    }

    static String CONTROL_DOC =
    	"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<avg-salary xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"simple-xml-format\">" +
          "<simple-xml>" +
            "<clerk-avg>1037.50</clerk-avg>" +
          "</simple-xml>" +
        "</avg-salary>";

    /**
     * Tests executing batch SQL statements.
     *
     * Positive test.
     */
    @Test
    public void testAvgSalary() throws Exception {
        Invocation invocation = new Invocation("avgSalary");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        // we expect a ValueObject containing '0' to be returned
        assertNotNull("result is null", result);
        assertTrue("Expected [ValueObject] but was [" +result.getClass().getName()+ "]", result instanceof ValueObject);
        ValueObject vobj = (ValueObject) result;
        assertTrue("Expected [Integer] but was [" +vobj.getClass().getName()+ "]", vobj.value instanceof Integer);
        Integer value = (Integer) vobj.value;
        assertTrue("Expected [0] but was [" + value + "]", value == 0);

        // verify that the batch sql statements executed correctly
        invocation = new Invocation("getAverageSalary");
        op = xrService.getOperation(invocation.getName());
        result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        Document doc = xmlPlatform.createDocument();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(CONTROL_DOC));
        assertTrue("control document not same as instance document", comparer.isNodeEqual(controlDoc, doc));
    }

    /**
     * Tests executing batch SQL statements, on of which is not
     * valid.
     *
     * Negative test.
     */
    @Test
    public void testBadSQL() throws Exception {
        Invocation invocation = new Invocation("invalidSQL");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        // we expect a ValueObject containing '1' to be returned
        assertNotNull("result is null", result);
        assertTrue("Expected [ValueObject] but was [" +result.getClass().getName()+ "]", result instanceof ValueObject);
        ValueObject vobj = (ValueObject) result;
        assertTrue("Expected [Integer] but was [" +vobj.getClass().getName()+ "]", vobj.value instanceof Integer);
        Integer value = (Integer) vobj.value;
        assertTrue("Expected [1] but was [" + value + "]", value == 1);
    }
}
