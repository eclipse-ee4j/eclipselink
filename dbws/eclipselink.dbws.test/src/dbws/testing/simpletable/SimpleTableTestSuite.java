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
 *     Mike Norman - May 2008, created DBWS test package
 ******************************************************************************/

package dbws.testing.simpletable;

// Javase imports
import java.io.IOException;
import java.io.StringReader;
import org.w3c.dom.Document;

// Java extension imports
import javax.wsdl.WSDLException;

// JUnit imports
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

// EclipseLink imports
import org.eclipse.persistence.internal.xr.Invocation;
import org.eclipse.persistence.internal.xr.Operation;
import org.eclipse.persistence.internal.xr.XRServiceAdapter;
import org.eclipse.persistence.oxm.XMLMarshaller;

import dbws.testing.TestDBWSFactory;

// domain-specific imports
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

public class SimpleTableTestSuite {

    public static final String DBWS_BUILDER_XML_USERNAME =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<dbws-builder xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
          "<properties>" +
              "<property name=\"projectName\">simpletable</property>" +
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
            "tableNamePattern=\"simpletable\" " +
          "/>" +
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

        buildJar(builderString, "SimpleTable");
    }

	  // test fixture(s)
    static XRServiceAdapter xrService = null;
    @BeforeClass
    public static void setUpDBWSService() {
        TestDBWSFactory serviceFactory = new TestDBWSFactory();
        xrService = serviceFactory.buildService();
    }

    @Test
    public void findByPrimaryKeyTest() {
        Invocation invocation = new Invocation("findByPrimaryKey_simpletable");
        invocation.setParameter("id",1);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(ONE_PERSON_XML));
        assertTrue("control document not same as instance document",
            comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String ONE_PERSON_XML =
    "<?xml version = '1.0' encoding = 'UTF-8'?>" +
    "<ns1:simpletable xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:ns1=\"urn:simpletable\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
       "<ns1:id>1</ns1:id>" +
       "<ns1:name>mike</ns1:name>" +
       "<ns1:since>2001-12-25</ns1:since>" +
    "</ns1:simpletable>";
}
