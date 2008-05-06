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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/

package dbws.testing.crud;

// Javase imports
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import org.w3c.dom.Document;

// Java extension classes
import javax.wsdl.WSDLException;

// JUnit imports
import org.junit.BeforeClass;
import org.junit.Test;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

// EclipseLink imports
import org.eclipse.persistence.internal.xr.Invocation;
import org.eclipse.persistence.internal.xr.Operation;
import org.eclipse.persistence.internal.xr.XRServiceAdapter;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.platform.database.MySQL4Platform;
import org.eclipse.persistence.platform.xml.XMLComparer;
import org.eclipse.persistence.platform.xml.XMLParser;
import org.eclipse.persistence.platform.xml.XMLPlatform;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.tools.dbws.DBWSBuilder;
import org.eclipse.persistence.tools.dbws.DBWSBuilderModel;
import org.eclipse.persistence.tools.dbws.DBWSBuilderModelProject;
import org.eclipse.persistence.tools.dbws.SimpleJarPackager;

// domain imports
import dbws.testing.TestDBWSFactory;

public class CRUDTestSuite {

    public final static String DATABASE_USERNAME_KEY = "db.user";
    public final static String DATABASE_PASSWORD_KEY = "db.pwd";
    public final static String DATABASE_URL_KEY = "db.url";
    public final static String DATABASE_DRIVER_KEY = "db.driver";
    public final static String DATABASE_PLATFORM_KEY = "db.platform";
    public final static String DEFAULT_DATABASE_USERNAME = "MNORMAN";
    public final static String DEFAULT_DATABASE_PASSWORD = "password";
    public final static String DEFAULT_DATABASE_URL = "jdbc:mysql://tlsvrdb4.ca.oracle.com/" +
        DEFAULT_DATABASE_USERNAME;
    public final static String DEFAULT_DATABASE_DRIVER = "com.mysql.jdbc.Driver";
    public final static String DEFAULT_DATABASE_PLATFORM =
        MySQL4Platform.class.getName();

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
          "<table schemaPattern=\"%\" tableNamePattern=\"crud_table\"/>" +
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

        XMLContext context = new XMLContext(new DBWSBuilderModelProject());
        XMLUnmarshaller unmarshaller = context.createUnmarshaller();
        DBWSBuilderModel builderModel =
        	(DBWSBuilderModel)unmarshaller.unmarshal(new StringReader(builderString));
    	DBWSBuilder dbwsBuilder = new DBWSBuilder();
    	dbwsBuilder.quiet = true;
    	dbwsBuilder.properties = builderModel.properties;
    	dbwsBuilder.operations = builderModel.operations;
    	SimpleJarPackager simpleDBWSJarPackager = new SimpleJarPackager("dbwsCRUD.jar");
    	simpleDBWSJarPackager.setStageDir(new File("."));
    	dbwsBuilder.setPackager(simpleDBWSJarPackager);
    	dbwsBuilder.start();
    }

	// test fixture(s)
    static XMLComparer comparer = new XMLComparer();
    static XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
    static XMLParser xmlParser = xmlPlatform.newXMLParser();
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
        assertTrue("control document not same as XRService instance document",
            comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String CRUD1_CONTROL_DOC =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<ns1:crud_table xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:ns1=\"urn:crud\"" +
                                       " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<ns1:id>1</ns1:id>" +
          "<ns1:name>crud1</ns1:name>" +
        "</ns1:crud_table>";
}