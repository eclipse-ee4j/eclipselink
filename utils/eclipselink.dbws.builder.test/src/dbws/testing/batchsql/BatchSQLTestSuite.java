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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;

import javax.wsdl.WSDLException;

import org.eclipse.persistence.internal.xr.Invocation;
import org.eclipse.persistence.internal.xr.Operation;
import org.eclipse.persistence.internal.xr.ValueObject;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;

import dbws.testing.DBWSTestSuite;

public class BatchSQLTestSuite extends DBWSTestSuite {
    @BeforeClass
    public static void setUp() throws WSDLException {
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
        DBWSTestSuite.setUp();
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
