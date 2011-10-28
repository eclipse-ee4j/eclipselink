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
package dbws.testing.objecttype;

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

//test imports
import dbws.testing.TestHelper;

/**
 * Tests Oracle Object types. 
 *
 */
public class ObjectTypeTestSuite extends TestHelper {
    
    @BeforeClass
    public static void setUp() throws WSDLException, SecurityException, NoSuchFieldException,
        IllegalArgumentException, IllegalAccessException {
        DBWS_BUILDER_XML_USERNAME =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<dbws-builder xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
              "<properties>" +
                  "<property name=\"projectName\">ObjectTypeTests</property>" +
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
                  "name=\"GetEmpTypeByIdTest\" " +
                  "catalogPattern=\"TOPLEVEL\" " +
                  "procedurePattern=\"GET_EMP_TYPE_BY_ID\" " +
                  "isAdvancedJDBC=\"true\" " +
                  "returnType=\"emp_typeType\" " +
              "/>" +
              "<procedure " +
                  "name=\"GetEmpTypeByIdTest2\" " +
                  "catalogPattern=\"TOPLEVEL\" " +
                  "procedurePattern=\"GET_EMP_TYPE_BY_ID_2\" " +
                  "isAdvancedJDBC=\"true\" " +
                  "returnType=\"emp_typeType\" " +
              "/>" +
            "</dbws-builder>";
          builder = new DBWSBuilder();
          TestHelper.setUp(".");
    }

    @Test
    public void getEmpTypeByIdTest() {
        Invocation invocation = new Invocation("GetEmpTypeByIdTest");
        invocation.setParameter("EID", 66);
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
        "<emp_typeType xmlns=\"urn:ObjectTypeTests\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
        "<id>66</id>" +
        "<name>BUBBLES</name>" +
        "<phone>" +
        "<home>(613) 234-4567</home>" +
        "<cell>(613) 858-3434</cell>" +
        "</phone>" +
        "</emp_typeType>";

    @Test
    public void getEmpTypeByIdTest2() {
        Invocation invocation = new Invocation("GetEmpTypeByIdTest2");
        invocation.setParameter("EID", 69);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        //marshaller.marshal(result, System.out);
        Document controlDoc = xmlParser.parse(new StringReader(RESULT2_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    static String RESULT2_XML = 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<emp_typeType xmlns=\"urn:ObjectTypeTests\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
        "<id>69</id>" +
        "<name>RICKY</name>" +
        "<phone>" +
        "<home>(613) 344-1232</home>" +
        "<cell>(613) 823-2323</cell>" +
        "</phone>" +
        "</emp_typeType>";
}