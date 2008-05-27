/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Mike Norman - May 2008, created DBWS tools package
 ******************************************************************************/

package dbws.testing.crud;

// Javase imports
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

// Java extension classes
import javax.wsdl.WSDLException;

// JUnit imports
import org.junit.BeforeClass;
import org.junit.Test;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

// EclipseLink imports
import org.eclipse.persistence.internal.xr.BaseEntity;
import org.eclipse.persistence.internal.xr.Invocation;
import org.eclipse.persistence.internal.xr.Operation;
import org.eclipse.persistence.internal.xr.XRServiceAdapter;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;

// domain imports
import dbws.testing.TestDBWSFactory;
import static dbws.testing.TestDBWSFactory.buildJar;
import static dbws.testing.TestDBWSFactory.comparer;
import static dbws.testing.TestDBWSFactory.DATABASE_DRIVER_KEY;
import static dbws.testing.TestDBWSFactory.DATABASE_PASSWORD_KEY;
import static dbws.testing.TestDBWSFactory.DATABASE_PLATFORM_KEY;
import static dbws.testing.TestDBWSFactory.DATABASE_URL_KEY;
import static dbws.testing.TestDBWSFactory.DATABASE_USERNAME_KEY;
import static dbws.testing.TestDBWSFactory.DEFAULT_DATABASE_DRIVER;
import static dbws.testing.TestDBWSFactory.DEFAULT_DATABASE_PASSWORD;
import static dbws.testing.TestDBWSFactory.DEFAULT_DATABASE_PLATFORM;
import static dbws.testing.TestDBWSFactory.DEFAULT_DATABASE_URL;
import static dbws.testing.TestDBWSFactory.DEFAULT_DATABASE_USERNAME;
import static dbws.testing.TestDBWSFactory.xmlParser;
import static dbws.testing.TestDBWSFactory.xmlPlatform;

public class CRUDTestSuite {

    public static final String DBWS_BUILDER_XML_USERNAME =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<dbws-builder xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
          "<properties>" +
              "<property name=\"projectName\">crud</property>" +
              "<property name=\"logLevel\">off</property>" +
              "<property name=\"username\">";
      public static final String DBWS_BUILDER_XML_PASSWORD =
              "</property><property name=\"password\">";
      public static final String DBWS_BUILDER_XML_URL =
              "</property><property name=\"url\">";
      public static final String DBWS_BUILDER_XML_DRIVER =
              "</property><property name=\"driver\">";
      public static final String DBWS_BUILDER_XML_PLATFORM =
              "</property><property name=\"platformClassname\">";
      public static final String DBWS_BUILDER_XML_MAIN =
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

    public static void main(String[] args) throws IOException, WSDLException {
        String username = System.getProperty(DATABASE_USERNAME_KEY, DEFAULT_DATABASE_USERNAME);
        String password = System.getProperty(DATABASE_PASSWORD_KEY, DEFAULT_DATABASE_PASSWORD);
        String url = System.getProperty(DATABASE_URL_KEY, DEFAULT_DATABASE_URL);
        String driver = System.getProperty(DATABASE_DRIVER_KEY, DEFAULT_DATABASE_DRIVER);
        String platform = System.getProperty(DATABASE_PLATFORM_KEY, DEFAULT_DATABASE_PLATFORM);

        String builderString = DBWS_BUILDER_XML_USERNAME + username + DBWS_BUILDER_XML_PASSWORD +
            password + DBWS_BUILDER_XML_URL + url + DBWS_BUILDER_XML_DRIVER + driver +
            DBWS_BUILDER_XML_PLATFORM + platform + DBWS_BUILDER_XML_MAIN;

        buildJar(builderString, "CRUD");
    }

	// test fixture(s)
    static XRServiceAdapter xrService = null;
    @BeforeClass
    public static void setUpDBWSService() {
        TestDBWSFactory serviceFactory = new TestDBWSFactory();
        xrService = serviceFactory.buildService();
    }

    // hokey naming convention for test methods to assure order-of-operations
    // w.r.t. insert/update/delete

    @Test
    public void test1_readOne() {
        Invocation invocation = new Invocation("findByPrimaryKey_crud_table");
        invocation.setParameter("id", 1);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        Document doc = xmlPlatform.createDocument();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(CRUD1_CONTROL_DOC));
        assertTrue("control document not same as instance document",
            comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String CRUD1_CONTROL_DOC =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<ns1:crud_table xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:ns1=\"urn:crud\"" +
                                       " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<ns1:id>1</ns1:id>" +
          "<ns1:name>crud1</ns1:name>" +
        "</ns1:crud_table>";

    @SuppressWarnings("unchecked")
    @Test
    public void test2_readAll() {
        Invocation invocation = new Invocation("findAll_crud_table");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        Document doc = xmlPlatform.createDocument();
        Element ec = doc.createElement("all");
        doc.appendChild(ec);
        for (Object r : (Vector)result) {
            marshaller.marshal(r, ec);
        }
        Document controlDoc = xmlParser.parse(new StringReader(FIND_ALL_CONTROL_DOC));
        assertTrue("control document not same as instance document",
            comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String FIND_ALL_CONTROL_DOC =
    	"<?xml version=\"1.0\" encoding=\"UTF-8\"?> " +
    	"<all>" +
    	  "<ns1:crud_table xmlns:ns1=\"urn:crud\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
    	    "<ns1:id>1</ns1:id>" +
    	    "<ns1:name>crud1</ns1:name>" +
    	  "</ns1:crud_table>" +
    	  "<ns1:crud_table xmlns:ns1=\"urn:crud\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
    	    "<ns1:id>2</ns1:id>" +
    	    "<ns1:name>crud2</ns1:name>" +
    	  "</ns1:crud_table>" +
    	  "<ns1:crud_table xmlns:ns1=\"urn:crud\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
    	    "<ns1:id>3</ns1:id>" +
    	    "<ns1:name>other</ns1:name>" +
    	  "</ns1:crud_table>" +
    	"</all>";

    @SuppressWarnings("unchecked")
    @Test
    public void test3_findByName() {
        Invocation invocation = new Invocation("findByName");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        Document doc = xmlPlatform.createDocument();
        Element ec = doc.createElement("some");
        doc.appendChild(ec);
        for (Object r : (Vector)result) {
            marshaller.marshal(r, ec);
        }
        Document controlDoc = xmlParser.parse(new StringReader(FIND_BY_NAME_CONTROL_DOC));
        assertTrue("control document not same as instance document",
            comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String FIND_BY_NAME_CONTROL_DOC =
    	"<?xml version=\"1.0\" encoding=\"UTF-8\"?> " +
    	"<some>" +
    	  "<ns1:crud_table xmlns:ns1=\"urn:crud\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
    	    "<ns1:id>1</ns1:id>" +
    	    "<ns1:name>crud1</ns1:name>" +
    	  "</ns1:crud_table>" +
    	  "<ns1:crud_table xmlns:ns1=\"urn:crud\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
    	    "<ns1:id>2</ns1:id>" +
    	    "<ns1:name>crud2</ns1:name>" +
    	  "</ns1:crud_table>" +
    	"</some>";

    @Test
    public void test4_update() {
        XMLUnmarshaller unMarshaller = xrService.getXMLContext().createUnmarshaller();
        Reader reader = new StringReader(CRUD1_CONTROL_DOC);
        InputSource inputSource = new InputSource(reader);
        BaseEntity firstEmp = (BaseEntity)unMarshaller.unmarshal(inputSource);
        firstEmp.set(1, "some other name");
        Invocation invocation = new Invocation("update_crud_table");
        invocation.setParameter("theInstance", firstEmp);
        Operation op = xrService.getOperation(invocation.getName());
        op.invoke(xrService, invocation);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void test5_delete() {
        Invocation invocation = new Invocation("findAll_crud_table");
        Operation op = xrService.getOperation(invocation.getName());
        Vector<BaseEntity> result = (Vector<BaseEntity>)op.invoke(xrService, invocation);
        BaseEntity firstEmp = result.firstElement();
        Invocation invocation2 = new Invocation("delete_crud_table");
        invocation2.setParameter("theInstance", firstEmp);
        Operation op2 = xrService.getOperation(invocation2.getName());
        op2.invoke(xrService, invocation2);
        Vector<BaseEntity> result2 = (Vector<BaseEntity>)op.invoke(xrService, invocation);
        assertTrue("Wrong number of employees", result2.size() == 2);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void test6_create() {
        XMLUnmarshaller unMarshaller = xrService.getXMLContext().createUnmarshaller();
        Reader reader = new StringReader(CRUD1_CONTROL_DOC);
        InputSource inputSource = new InputSource(reader);
        BaseEntity anotherEmployee = (BaseEntity)unMarshaller.unmarshal(inputSource);
        Invocation invocation = new Invocation("create_crud_table");
        invocation.setParameter("theInstance", anotherEmployee);
        Operation op = xrService.getOperation(invocation.getName());
        op.invoke(xrService, invocation);
        Invocation invocation2 = new Invocation("findAll_crud_table");
        Operation op2 = xrService.getOperation(invocation2.getName());
        Vector<BaseEntity> result2 = (Vector<BaseEntity>)op2.invoke(xrService, invocation2);
        assertTrue("Wrong number of employees", result2.size() == 3);
    }
}