/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     David McCann - 2.3 - Initial implementation
 ******************************************************************************/
package dbws.testing.sqlascollection;

//javase imports
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import org.w3c.dom.Document;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

//java eXtension imports
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.ServletContext;
import javax.wsdl.WSDLException;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Endpoint;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceProvider;
import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;
import static javax.xml.ws.Service.Mode.MESSAGE;
import static javax.xml.ws.soap.SOAPBinding.SOAP11HTTP_BINDING;

//JUnit4 imports
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

//EclipseLink imports
import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.internal.dbws.ProviderHelper;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.xr.ProjectHelper;
import org.eclipse.persistence.internal.xr.XRDynamicClassLoader;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLLogin;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.platform.xml.XMLComparer;
import org.eclipse.persistence.platform.xml.XMLParser;
import org.eclipse.persistence.platform.xml.XMLPlatform;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.DatasourceLogin;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.factories.XMLProjectReader;
import org.eclipse.persistence.tools.dbws.DBWSBuilder;
import org.eclipse.persistence.tools.dbws.DBWSBuilderModel;
import org.eclipse.persistence.tools.dbws.DBWSBuilderModelProject;
import org.eclipse.persistence.tools.dbws.JSR109WebServicePackager;
import static org.eclipse.persistence.tools.dbws.DBWSBuilder.NO_SESSIONS_FILENAME;
import static org.eclipse.persistence.tools.dbws.DBWSBuilder.SESSIONS_FILENAME_KEY;
import static org.eclipse.persistence.tools.dbws.DBWSPackager.ArchiveUse.noArchive;
import static org.eclipse.persistence.tools.dbws.XRPackager.__nullStream;

//testing imports
import static dbws.testing.DBWSTestSuite.DATABASE_DDL_CREATE_KEY;
import static dbws.testing.DBWSTestSuite.DATABASE_DDL_DEBUG_KEY;
import static dbws.testing.DBWSTestSuite.DATABASE_DDL_DROP_KEY;
import static dbws.testing.DBWSTestSuite.DATABASE_DRIVER;
import static dbws.testing.DBWSTestSuite.DATABASE_PLATFORM;
import static dbws.testing.DBWSTestSuite.DATABASE_USERNAME_KEY;
import static dbws.testing.DBWSTestSuite.DATABASE_PASSWORD_KEY;
import static dbws.testing.DBWSTestSuite.DATABASE_URL_KEY;
import static dbws.testing.DBWSTestSuite.DEFAULT_DATABASE_DDL_CREATE;
import static dbws.testing.DBWSTestSuite.DEFAULT_DATABASE_DDL_DEBUG;
import static dbws.testing.DBWSTestSuite.DEFAULT_DATABASE_DDL_DROP;
import static dbws.testing.DBWSTestSuite.DEFAULT_DATABASE_PASSWORD;
import static dbws.testing.DBWSTestSuite.DEFAULT_DATABASE_URL;
import static dbws.testing.DBWSTestSuite.DEFAULT_DATABASE_USERNAME;
import static dbws.testing.DBWSTestSuite.SQLCOLLECTION;
import static dbws.testing.DBWSTestSuite.SQLCOLLECTION_NAMESPACE;
import static dbws.testing.DBWSTestSuite.SQLCOLLECTION_SERVICE_NAMESPACE;
import static dbws.testing.DBWSTestSuite.SQLCOLLECTION_SERVICE;
import static dbws.testing.DBWSTestSuite.SQLCOLLECTION_SERVICE_PORT;
import static dbws.testing.DBWSTestSuite.buildConnection;
import static dbws.testing.DBWSTestSuite.createDbArtifact;
import static dbws.testing.DBWSTestSuite.documentToString;
import static dbws.testing.DBWSTestSuite.dropDbArtifact;

@WebServiceProvider(
    targetNamespace = SQLCOLLECTION_SERVICE_NAMESPACE,
    serviceName = SQLCOLLECTION_SERVICE,
    portName = SQLCOLLECTION_SERVICE_PORT
)
@ServiceMode(MESSAGE)
public class SQLAsCollectionTestSuite extends ProviderHelper implements Provider<SOAPMessage> {

    static final String CREATE_SQLCOLLECTION_TABLE =
        "CREATE TABLE IF NOT EXISTS sqlascollection (" +
            "\nID NUMERIC NOT NULL," +
            "\nNAME varchar(25)," +
            "\nSINCE date," +
            "\nPRIMARY KEY (ID)" +
        "\n)";
    static String[] POPULATE_SQLCOLLECTION_TABLE = new String[] {
        "INSERT INTO sqlascollection (ID, NAME, SINCE) VALUES (1, 'mike', '2001-12-25')",
        "INSERT INTO sqlascollection (ID, NAME, SINCE) VALUES (2, null,'2001-12-25')",
        "INSERT INTO sqlascollection (ID, NAME, SINCE) VALUES (3, 'rick',NULL)"
    };
    static final String DROP_SQLCOLLECTION_TABLE =
        "DROP TABLE sqlascollection";

    static boolean ddlDebug = false;

	static final String ENDPOINT_ADDRESS = "http://localhost:9999/" + SQLCOLLECTION + "Test";

    // JUnit test fixtures
    static Connection conn = null;
    static ByteArrayOutputStream DBWS_SERVICE_STREAM = new ByteArrayOutputStream();
    static ByteArrayOutputStream DBWS_SCHEMA_STREAM = new ByteArrayOutputStream();
    static ByteArrayOutputStream DBWS_OR_STREAM = new ByteArrayOutputStream();
    static ByteArrayOutputStream DBWS_OX_STREAM = new ByteArrayOutputStream();
    static ByteArrayOutputStream DBWS_WSDL_STREAM = new ByteArrayOutputStream();
    static XMLComparer comparer = new XMLComparer();
    static XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
    static XMLParser xmlParser = xmlPlatform.newXMLParser();
    static Endpoint endpoint = null;
    static QName portQName = null;
    static Service testService = null;
    static DBWSBuilder builder = new DBWSBuilder();

    @BeforeClass
    public static void setUp() throws WSDLException {
        try {
            conn = buildConnection();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        String ddlCreate = System.getProperty(DATABASE_DDL_CREATE_KEY, DEFAULT_DATABASE_DDL_CREATE);
        String ddlDebugProp = System.getProperty(DATABASE_DDL_DEBUG_KEY, DEFAULT_DATABASE_DDL_DEBUG);
        if ("true".equalsIgnoreCase(ddlDebugProp)) {
            ddlDebug = true;
        }
        if ("true".equalsIgnoreCase(ddlCreate)) {
            try {
                createDbArtifact(conn, CREATE_SQLCOLLECTION_TABLE);
            }
            catch (SQLException e) {
                if (ddlDebug) {
                    e.printStackTrace();
                }
            }
            try {
                Statement stmt = conn.createStatement();
                for (int i = 0; i < POPULATE_SQLCOLLECTION_TABLE.length; i++) {
                    stmt.addBatch(POPULATE_SQLCOLLECTION_TABLE[i]);
                }
                stmt.executeBatch();
            }
            catch (SQLException e) {
                if (ddlDebug) {
                    e.printStackTrace();
                }
            }
        }
        String username = System.getProperty(DATABASE_USERNAME_KEY, DEFAULT_DATABASE_USERNAME);
        String password = System.getProperty(DATABASE_PASSWORD_KEY, DEFAULT_DATABASE_PASSWORD);
        String url = System.getProperty(DATABASE_URL_KEY, DEFAULT_DATABASE_URL);
        String builderString = DBWS_BUILDER_XML_USERNAME + username + DBWS_BUILDER_XML_PASSWORD +
            password + DBWS_BUILDER_XML_URL + url + DBWS_BUILDER_XML_DRIVER + DATABASE_DRIVER +
            DBWS_BUILDER_XML_PLATFORM + DATABASE_PLATFORM + DBWS_BUILDER_XML_MAIN;
        XMLContext context = new XMLContext(new DBWSBuilderModelProject());
        XMLUnmarshaller unmarshaller = context.createUnmarshaller();
        DBWSBuilderModel builderModel =
            (DBWSBuilderModel)unmarshaller.unmarshal(new StringReader(builderString));
        builder.quiet = true;
        builder.properties = builderModel.properties;
        builder.getProperties().put(SESSIONS_FILENAME_KEY, NO_SESSIONS_FILENAME);
        builder.getTargetNamespace();
        builder.operations = builderModel.operations;
        builder.setPackager(new JSR109WebServicePackager(null, "WebServiceTestPackager", noArchive) {
            @Override
            public void start() {
            }
        });
        builder.build(DBWS_SCHEMA_STREAM, __nullStream, DBWS_SERVICE_STREAM, DBWS_OR_STREAM,
            DBWS_OX_STREAM, __nullStream, __nullStream, DBWS_WSDL_STREAM, __nullStream,
            __nullStream, __nullStream, __nullStream, null);
        endpoint = Endpoint.create(new SQLAsCollectionTestSuite());
        endpoint.publish(ENDPOINT_ADDRESS);
        QName serviceQName = new QName(SQLCOLLECTION_SERVICE_NAMESPACE, SQLCOLLECTION_SERVICE);
        portQName = new QName(SQLCOLLECTION_SERVICE_NAMESPACE, SQLCOLLECTION_SERVICE_PORT);
        testService = Service.create(serviceQName);
        testService.addPort(portQName, SOAP11HTTP_BINDING, ENDPOINT_ADDRESS);
    }

    @AfterClass
    public static void teardown() {
        if (endpoint != null) {
            endpoint.stop();
        }
        String ddlDrop = System.getProperty(DATABASE_DDL_DROP_KEY, DEFAULT_DATABASE_DDL_DROP);
        if ("true".equalsIgnoreCase(ddlDrop)) {
            try {
                dropDbArtifact(conn, DROP_SQLCOLLECTION_TABLE);
            }
            catch (SQLException e) {
                if (ddlDebug) {
                    e.printStackTrace();
                }
            }
        }
    }
    @PreDestroy
    public void destroy() {
        super.destroy();
    }

    @Override
    protected InputStream initXRServiceStream(ClassLoader parentClassLoader, ServletContext sc) {
        return new ByteArrayInputStream(DBWS_SERVICE_STREAM.toByteArray());
    }

    @Override
    protected InputStream initXRSchemaStream(ClassLoader parentClassLoader, ServletContext sc) {
        return new ByteArrayInputStream(DBWS_SCHEMA_STREAM.toByteArray());
    }

    @Override
    protected InputStream initWSDLInputStream(ClassLoader parentClassLoader, ServletContext sc) {
        return new ByteArrayInputStream(DBWS_WSDL_STREAM.toByteArray());
    }

    @PostConstruct
    public void init() {
        super.init(new XRDynamicClassLoader(Thread.currentThread().getContextClassLoader()),
            null, false);
    }

     @Override
     public void logoutSessions() {
         if (xrService.getORSession() != null) {
             ((DatabaseSession)xrService.getORSession()).logout();
         }
         if (xrService.getOXSession() != null) {
             ((DatabaseSession)xrService.getOXSession()).logout();
         }
     }

     @Override
     public void buildSessions() {
         Project oxProject = XMLProjectReader.read(new StringReader(DBWS_OX_STREAM.toString()),
             parentClassLoader);
         ((XMLLogin)oxProject.getDatasourceLogin()).setEqualNamespaceResolvers(false);
         Project orProject = XMLProjectReader.read(new StringReader(DBWS_OR_STREAM.toString()),
             parentClassLoader);
         DatasourceLogin login = orProject.getLogin();
         login.setUserName(builder.getUsername());
         login.setPassword(builder.getPassword());
         ((DatabaseLogin)login).setConnectionString(builder.getUrl());
         ((DatabaseLogin)login).setDriverClassName(DATABASE_DRIVER);
         Platform platform = builder.getDatabasePlatform();
         ConversionManager cm = platform.getConversionManager();
         cm.setLoader(parentClassLoader);
         login.setDatasourcePlatform(platform);
         ((DatabaseLogin)login).bindAllParameters();
         orProject.setDatasourceLogin(login);
         ProjectHelper.fixOROXAccessors(orProject, oxProject);
         DatabaseSession databaseSession = orProject.createDatabaseSession();
         databaseSession.dontLogMessages();
         xrService.setORSession(databaseSession);
         xrService.setXMLContext(new XMLContext(oxProject));
         xrService.setOXSession(xrService.getXMLContext().getSession(0));
     }

    static final String GETDATA_REQUEST =
        "<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
          "<env:Body>" +
            "<srvc:"+SQLCOLLECTION_SERVICE+" xmlns:srvc=\"" + SQLCOLLECTION_SERVICE_NAMESPACE + "\" xmlns=\"" + SQLCOLLECTION_NAMESPACE + "\"/>" +
          "</env:Body>" +
        "</env:Envelope>";

    @Test
    public void getData() throws SOAPException, ParserConfigurationException, SAXException, IOException, TransformerException {
        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage request = factory.createMessage();
        SOAPPart part = request.getSOAPPart();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        DOMSource domSource = new DOMSource(db.parse(new InputSource(new StringReader(GETDATA_REQUEST))));
        part.setContent(domSource);
        Dispatch<SOAPMessage> dispatch = testService.createDispatch(portQName, SOAPMessage.class, Service.Mode.MESSAGE);
        BindingProvider bp = dispatch;
        Map<String, Object> rc = bp.getRequestContext();
        rc.put(ENDPOINT_ADDRESS_PROPERTY, ENDPOINT_ADDRESS);
        SOAPMessage response = null;
        try {
            response = dispatch.invoke(request);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail("An unexpected exception occurred: " + e.getMessage());
        }
        if (response != null) {
            Source src = response.getSOAPPart().getContent();
            DOMResult result = new DOMResult();
            getTransformer().transform(src, result);
            Document resultDoc = (Document)result.getNode();
            String resultString = documentToString(resultDoc);
            Document controlDoc = xmlParser.parse(new StringReader(GETDATA_RESPONSE));
            String controlString = documentToString(controlDoc);
            assertTrue("Control document not same as instance document.\n Expected:\n" + controlString + "\nActual:\n" + resultString, controlString.equals(resultString));
        }
    }

    static final String DBWS_BUILDER_XML_USERNAME =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<dbws-builder xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
          "<properties>" +
              "<property name=\"projectName\">" + SQLCOLLECTION + "</property>" +
              "<property name=\"logLevel\">off</property>" +
              "<property name=\"username\">";
    static final String DBWS_BUILDER_XML_PASSWORD =
              "</property><property name=\"password\">";
    static final String DBWS_BUILDER_XML_URL =
              "</property><property name=\"url\">";
    static final String DBWS_BUILDER_XML_DRIVER =
              "</property><property name=\"driver\">";
    static final String DBWS_BUILDER_XML_PLATFORM =
              "</property><property name=\"platformClassname\">";
    static final String DBWS_BUILDER_XML_MAIN =
              "</property>" +
          "</properties>" +
          "<sql " +
              "name=\""+SQLCOLLECTION_SERVICE+"\" " +
              "returnType=\"sRecord\" " +
              "isCollection=\"true\">" +
              "<statement><![CDATA[select * from sqlascollection]]></statement>" +
              "<build-statement><![CDATA[select * from sqlascollection where 0=1]]></build-statement>" +
          "</sql>" +
        "</dbws-builder>";
    static final String GETDATA_RESPONSE =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
        "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
            "<SOAP-ENV:Header/>\n" +
            "<SOAP-ENV:Body>\n" +
                "<srvc:"+SQLCOLLECTION_SERVICE+"Response xmlns=\"" + SQLCOLLECTION_NAMESPACE + "\" xmlns:srvc=\"" + SQLCOLLECTION_SERVICE_NAMESPACE + "\">\n" +
                    "<srvc:result>\n" +
                        "<sRecord>\n" +
                            "<id>1</id>\n" +
                            "<name>mike</name>\n" +
                            "<since>2001-12-25</since>\n" +
                        "</sRecord>\n" +
                        "<sRecord>\n" +
                            "<id>2</id>\n" +
                            "<name xsi:nil=\"true\"/>\n" +
                            "<since>2001-12-25</since>\n" +
                        "</sRecord>\n" +
                        "<sRecord>\n" +
                            "<id>3</id>\n" +
                            "<name>rick</name>\n" +
                            "<since xsi:nil=\"true\"/>\n" +
                        "</sRecord>\n" +
                    "</srvc:result>\n" +
                "</srvc:"+SQLCOLLECTION_SERVICE+"Response>\n" +
            "</SOAP-ENV:Body>\n" +
        "</SOAP-ENV:Envelope>";
}