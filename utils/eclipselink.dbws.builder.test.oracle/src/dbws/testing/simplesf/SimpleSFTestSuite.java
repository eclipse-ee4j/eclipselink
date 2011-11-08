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
 *     David McCann - August 31, 2011 - 2.4 - Initial implementation
 ******************************************************************************/
package dbws.testing.simplesf;

//javase imports
import java.io.StringReader;
import org.w3c.dom.Document;

//java eXtension imports
import javax.wsdl.WSDLException;

//JUnit4 imports
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

//EclipseLink imports
import org.eclipse.persistence.internal.xr.Invocation;
import org.eclipse.persistence.internal.xr.Operation;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.tools.dbws.DBWSBuilder;
import org.eclipse.persistence.tools.dbws.oracle.OracleHelper;

//test imports
import dbws.testing.DBWSTestSuite;

public class SimpleSFTestSuite extends DBWSTestSuite {
    
    @BeforeClass
    public static void setUp() throws WSDLException, SecurityException, NoSuchFieldException,
        IllegalArgumentException, IllegalAccessException {
        DBWS_BUILDER_XML_USERNAME =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<dbws-builder xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
              "<properties>" +
                  "<property name=\"projectName\">simpleSF</property>" +
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
                 "name=\"FindMaxSalTest\" " +
                 "procedurePattern=\"FINDMAXSAL\" " +
                 "isCollection=\"false\" " +
                 "isSimpleXMLFormat=\"true\" " +
              "/>" +
              "<procedure " +
                  "name=\"FindMaxSalForDeptTest\" " +
                  "procedurePattern=\"FINDMAXSALFORDEPT\" " +
                  "isCollection=\"false\" " +
                  "isSimpleXMLFormat=\"true\" " +
                  "simpleXMLFormatTag=\"max-sal-for-dept\" " +
              "/>" +
            "</dbws-builder>";
          builder = new DBWSBuilder();
          OracleHelper builderHelper = new OracleHelper(builder);
          builder.setBuilderHelper(builderHelper);
          DBWSTestSuite.setUp(".");
    }

    @Test
    public void findMaxSalTest() {
        Invocation invocation = new Invocation("FindMaxSalTest");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(FIND_MAX_SAL_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String FIND_MAX_SAL_XML =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<simple-xml-format>" +
          "<simple-xml>" +
            "<result>5000</result>" +
          "</simple-xml>" +
        "</simple-xml-format>";

    @Test
    public void findMaxSalForDeptTest() {
        Invocation invocation = new Invocation("FindMaxSalForDeptTest");
        invocation.setParameter("DEPT", 30);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(FIND_MAX_SAL_FOR_DEPT_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String FIND_MAX_SAL_FOR_DEPT_XML =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<max-sal-for-dept xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"any\">" +
          "<simple-xml>" +
            "<result>2850</result>" +
          "</simple-xml>" +
        "</max-sal-for-dept>";
}