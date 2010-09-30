/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package dbws.testing.rootcause;

//javase imports
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

//java eXtension imports
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.ServletContext;
import javax.wsdl.WSDLException;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Endpoint;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.soap.SOAPFaultException;
import static javax.xml.ws.Service.Mode.MESSAGE;
import static javax.xml.ws.soap.SOAPBinding.SOAP11HTTP_BINDING;

//JUnit4 imports
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

//EclipseLink imports
import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.internal.dbws.ProviderHelper;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.xr.XRDynamicClassLoader;
import org.eclipse.persistence.internal.xr.ProjectHelper;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLLogin;
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
import org.eclipse.persistence.tools.dbws.OperationModel;
import org.eclipse.persistence.tools.dbws.TableOperationModel;
import org.eclipse.persistence.tools.dbws.JSR109WebServicePackager;
import static org.eclipse.persistence.tools.dbws.DBWSBuilder.NO_SESSIONS_FILENAME;
import static org.eclipse.persistence.tools.dbws.DBWSBuilder.SESSIONS_FILENAME_KEY;
import static org.eclipse.persistence.tools.dbws.DBWSPackager.ArchiveUse.noArchive;
import static org.eclipse.persistence.tools.dbws.XRPackager.__nullStream;

//domain-specific (test) imports
import static dbws.testing.DBWSTestSuite.DATABASE_PASSWORD_KEY;
import static dbws.testing.DBWSTestSuite.DATABASE_URL_KEY;
import static dbws.testing.DBWSTestSuite.DATABASE_USERNAME_KEY;
import static dbws.testing.DBWSTestSuite.DEFAULT_DATABASE_DRIVER;
import static dbws.testing.DBWSTestSuite.DEFAULT_DATABASE_PASSWORD;
import static dbws.testing.DBWSTestSuite.DEFAULT_DATABASE_PLATFORM;
import static dbws.testing.DBWSTestSuite.DEFAULT_DATABASE_URL;
import static dbws.testing.DBWSTestSuite.DEFAULT_DATABASE_USERNAME;
import static dbws.testing.DBWSTestSuite.ROOTCAUSE;
import static dbws.testing.DBWSTestSuite.ROOTCAUSE_SERVICE_NAMESPACE;
import static dbws.testing.DBWSTestSuite.ROOTCAUSE_NAMESPACE;
import static dbws.testing.DBWSTestSuite.ROOTCAUSE_PORT;
import static dbws.testing.DBWSTestSuite.ROOTCAUSE_SERVICE;
import static dbws.testing.DBWSTestSuite.ROOTCAUSE_TEST;

@WebServiceProvider(
  targetNamespace = ROOTCAUSE_SERVICE_NAMESPACE,
  serviceName = ROOTCAUSE_SERVICE,
  portName = ROOTCAUSE_PORT
)
@ServiceMode(MESSAGE)
public class RootCauseTestSuite extends ProviderHelper implements Provider<SOAPMessage> {

    public static final String ENDPOINT_ADDRESS = "http://localhost:9999/" + ROOTCAUSE;
    static final String SOAP_UPDATE_REQUEST =
        "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
           "<SOAP-ENV:Body>" +
              "<srvc:update_rootcause_tableType xmlns:srvc=\"" + ROOTCAUSE_SERVICE_NAMESPACE + "\" xmlns=\"" + ROOTCAUSE_NAMESPACE + "\">" +
                 "<srvc:theInstance>" +
                    // rootcause_tableType built wrong
                    "<barf>" +
                       "<id>1</id>" +
                       "<name />" +
                    "</barf>" +
                 "</srvc:theInstance>" +
              "</srvc:update_rootcause_tableType>" +
           "</SOAP-ENV:Body>" +
        "</SOAP-ENV:Envelope>";

    // JUnit test fixtures
    public static ByteArrayOutputStream DBWS_SERVICE_STREAM = new ByteArrayOutputStream();
    public static ByteArrayOutputStream DBWS_SCHEMA_STREAM = new ByteArrayOutputStream();
    public static ByteArrayOutputStream DBWS_OR_STREAM = new ByteArrayOutputStream();
    public static ByteArrayOutputStream DBWS_OX_STREAM = new ByteArrayOutputStream();
    public static ByteArrayOutputStream DBWS_WSDL_STREAM = new ByteArrayOutputStream();
    public static XMLComparer comparer = new XMLComparer();
    public static XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
    public static XMLParser xmlParser = xmlPlatform.newXMLParser();
    public static Endpoint endpoint = null;
    public static QName portQName = null;
    public static Service testService = null;
    public static DBWSBuilder builder = new DBWSBuilder();

    @BeforeClass
    public static void setUp() throws WSDLException {
        String username = System.getProperty(DATABASE_USERNAME_KEY, DEFAULT_DATABASE_USERNAME);
        String password = System.getProperty(DATABASE_PASSWORD_KEY, DEFAULT_DATABASE_PASSWORD);
        String url = System.getProperty(DATABASE_URL_KEY, DEFAULT_DATABASE_URL);
        builder.setProjectName(ROOTCAUSE_TEST);
        builder.setTargetNamespace(ROOTCAUSE_NAMESPACE);
        TableOperationModel rootCauseOp = new TableOperationModel();
        rootCauseOp.additionalOperations = new ArrayList<OperationModel>();
        rootCauseOp.setName(ROOTCAUSE_TEST);
        rootCauseOp.setTablePattern(ROOTCAUSE);
        builder.getOperations().add(rootCauseOp);
        builder.quiet = true;
        builder.setLogLevel(SessionLog.FINE_LABEL);
        builder.setDriver(DEFAULT_DATABASE_DRIVER);
        builder.setPlatformClassname(DEFAULT_DATABASE_PLATFORM);
        builder.getProperties().put(SESSIONS_FILENAME_KEY, NO_SESSIONS_FILENAME);
        builder.setUsername(username);
        builder.setPassword(password);
        builder.setUrl(url);
        builder.setPackager(new JSR109WebServicePackager(null, "WebServiceTestPackager", noArchive) {
            @Override
            public void start() {
            }
        });
        builder.build(DBWS_SCHEMA_STREAM, __nullStream, DBWS_SERVICE_STREAM, DBWS_OR_STREAM,
            DBWS_OX_STREAM, __nullStream, __nullStream, DBWS_WSDL_STREAM, __nullStream,
            __nullStream, __nullStream, __nullStream, null);
        endpoint = Endpoint.create(new RootCauseTestSuite());
        endpoint.publish(ENDPOINT_ADDRESS);
        QName serviceQName = new QName(ROOTCAUSE_SERVICE_NAMESPACE, ROOTCAUSE_SERVICE);
        portQName = new QName(ROOTCAUSE_SERVICE_NAMESPACE, ROOTCAUSE_PORT);
        testService = Service.create(serviceQName);
        testService.addPort(portQName, SOAP11HTTP_BINDING, ENDPOINT_ADDRESS);
    }

    @AfterClass
    public static void teardown() {
        if (endpoint != null) {
            endpoint.stop();
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
        ((DatabaseLogin)login).setDriverClassName(DEFAULT_DATABASE_DRIVER);
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

    @Test
    public void testRootCause() throws SOAPException, IOException, SAXException,
        ParserConfigurationException, TransformerException {
        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage request = factory.createMessage();
        SOAPPart part = request.getSOAPPart();
        DOMSource domSource = new DOMSource(getDocumentBuilder().parse(
            new InputSource(new StringReader(SOAP_UPDATE_REQUEST))));
        part.setContent(domSource);
        Dispatch<SOAPMessage> dispatch = testService.createDispatch(portQName, SOAPMessage.class,
            Service.Mode.MESSAGE);
        SOAPMessage response = null;
        try {
            response = dispatch.invoke(request);
        }
        catch (SOAPFaultException sfe) {
            assertTrue("incorrect SOAPFaultException",
                sfe.getMessage().contains("EclipseLink-25008"));
        }
        // just for debugging, keep 'response' variable alive after try-catch
        if (response != null) {
            response.hashCode();
        }
    }
}