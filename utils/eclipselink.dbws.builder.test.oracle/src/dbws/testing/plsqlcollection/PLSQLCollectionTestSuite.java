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
 *     David McCann - September 23, 2011 - 2.4 - Initial implementation
 ******************************************************************************/
package dbws.testing.plsqlcollection;

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
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.tools.dbws.DBWSBuilder;

//test imports
import dbws.testing.TestHelper;

/**
 * Tests PL/SQL collections with simple arguments. 
 *
 */
public class PLSQLCollectionTestSuite extends TestHelper {
    
    @BeforeClass
    public static void setUp() throws WSDLException, SecurityException, NoSuchFieldException,
        IllegalArgumentException, IllegalAccessException {
        DBWS_BUILDER_XML_USERNAME =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<dbws-builder xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
              "<properties>" +
                  "<property name=\"projectName\">PLSQLCollection</property>" +
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
                  "name=\"CopyTableTest\" " +
                  "catalogPattern=\"PACKAGE2\" " +
                  "procedurePattern=\"COPYTABLE\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"CopyTableTest2\" " +
                  "catalogPattern=\"PACKAGE2\" " +
                  "procedurePattern=\"COPYTABLE2\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"SetRecordTest\" " +
                  "catalogPattern=\"PACKAGE2\" " +
                  "procedurePattern=\"SETRECORD\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"SetRecordTest2\" " +
                  "catalogPattern=\"PACKAGE2\" " +
                  "procedurePattern=\"SETRECORD2\" " +
              "/>" +
            "</dbws-builder>";
          builder = new DBWSBuilder();
          TestHelper.setUp(".");
    }

    @Test
    public void copyTableTest() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object inputTab1 = unmarshaller.unmarshal(new StringReader(TABLE_XML));
        Invocation invocation = new Invocation("CopyTableTest");
        invocation.setParameter("OLDTAB", inputTab1);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(TABLE_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String TABLE_XML =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
        "<PACKAGE2_TAB1 xmlns=\"urn:PLSQLCollection\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<item>BLAH</item>" +
        "</PACKAGE2_TAB1>";

    @Test
    public void copyTableTest2() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object inputTab1 = unmarshaller.unmarshal(new StringReader(TABLE_XML));
        Invocation invocation = new Invocation("CopyTableTest2");
        invocation.setParameter("OLDTAB", inputTab1);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(TABLE_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    
    /**
     * StoredProcedure test.
     */
    @Test
    public void setRecordTest() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object inputORec = unmarshaller.unmarshal(new StringReader(INPUTORECORD_XML));
        Invocation invocation = new Invocation("SetRecordTest");
        invocation.setParameter("INREC", inputORec);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(OUTPUTTABLE_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String INPUTORECORD_XML =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
        "<PACKAGE2_ORECORD xmlns=\"urn:PLSQLCollection\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<o1>somedata</o1>" +
          "<o2>66.6</o2>" +
        "</PACKAGE2_ORECORD>";
    public static final String OUTPUTTABLE_XML =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
        "<PACKAGE2_TAB2 xmlns=\"urn:PLSQLCollection\">" +
          "<item>" +
            "<o1>somedata</o1>" +
            "<o2>66.6</o2>" +
          "</item>" +
        "</PACKAGE2_TAB2>";
    
    /**
     * StoredFunction test.
     */
    @Test
    public void setRecordTest2() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object inputORec = unmarshaller.unmarshal(new StringReader(INPUTORECORD_XML));
        Invocation invocation = new Invocation("SetRecordTest2");
        invocation.setParameter("INREC", inputORec);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(OUTPUTTABLE_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

}