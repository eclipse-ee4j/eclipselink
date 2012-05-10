/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package dbws.testing.mtom;

//javase imports
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import org.xml.sax.InputSource;

//java eXtension imports
import javax.activation.DataHandler;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.wsdl.WSDLException;
import javax.xml.namespace.QName;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Endpoint;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.soap.MTOMFeature;
import javax.xml.ws.soap.SOAPBinding;
import static javax.xml.ws.Service.Mode.MESSAGE;
import static javax.xml.ws.soap.SOAPBinding.SOAP11HTTP_BINDING;

//JUnit4 imports
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

//EclipseLink imports
import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.internal.dbws.ProviderHelper;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.oxm.ByteArrayDataSource;
import org.eclipse.persistence.internal.xr.ProjectHelper;
import org.eclipse.persistence.internal.xr.XRDynamicClassLoader;
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
import org.eclipse.persistence.tools.dbws.JSR109WebServicePackager;
import org.eclipse.persistence.tools.dbws.OperationModel;
import org.eclipse.persistence.tools.dbws.TableOperationModel;
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
import static dbws.testing.DBWSTestSuite.MTOM;
import static dbws.testing.DBWSTestSuite.MTOM_NAMESPACE;
import static dbws.testing.DBWSTestSuite.MTOM_PORT;
import static dbws.testing.DBWSTestSuite.MTOM_SERVICE;
import static dbws.testing.DBWSTestSuite.MTOM_SERVICE_NAMESPACE;
import static dbws.testing.DBWSTestSuite.MTOM_TEST;
import static dbws.testing.DBWSTestSuite.buildConnection;
import static dbws.testing.DBWSTestSuite.runDdl;

@WebServiceProvider(
    targetNamespace = MTOM_SERVICE_NAMESPACE,
    serviceName = MTOM_SERVICE,
    portName = MTOM_PORT
)
@ServiceMode(MESSAGE)
public class MTOMTestSuite extends ProviderHelper implements Provider<SOAPMessage> {

    static final String CREATE_MTOM_TABLE =
        "CREATE TABLE IF NOT EXISTS mtom (" +
            "\nID DECIMAL(7,0) NOT NULL," +
            "\nDESCRIPT VARCHAR(80)," +
            "\nSTUFF MEDIUMBLOB," +
            "\nPRIMARY KEY (ID)" +
        "\n)";
    static final String DROP_MTOM_TABLE =
        "DROP TABLE mtom";

    //lorem ipsum is the name given to commonly used placeholder filler text
    //this copy is exactly 5000 bytes long
    static final String LOREM_IPSUM =
        "Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque " +
        "laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi " +
        "architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas " +
        "sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione " +
        "voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit " +
        "amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut " +
        "labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis " +
        "nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea " +
        "commodi consequatur? Quis autem vel eum iure reprehenderit qui in ea voluptate velit " +
        "esse quam nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas " +
        "nulla pariatur? At vero eos et accusamus et iusto odio dignissimos ducimus qui " +
        "blanditiis praesentium voluptatum deleniti atque corrupti quos dolores et quas " +
        "molestias excepturi sint occaecati cupiditate non provident, similique sunt in culpa " +
        "qui officia deserunt mollitia animi, id est laborum et dolorum fuga. Et harum quidem " +
        "rerum facilis est et expedita distinctio. Nam libero tempore, cum soluta nobis est " +
        "eligendi optio cumque nihil impedit quo minus id quod maxime placeat facere possimus, " +
        "omnis voluptas assumenda est, omnis dolor repellendus. Temporibus autem quibusdam et " +
        "aut officiis debitis aut rerum necessitatibus saepe eveniet ut et voluptates " +
        "repudiandae sint et molestiae non recusandae. Itaque earum rerum hic tenetur a " +
        "sapiente delectus, ut aut reiciendis voluptatibus maiores alias consequatur aut " +
        "perferendis doloribus asperiores repellat. Sed ut perspiciatis unde omnis iste natus " +
        "error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa " +
        "quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. " +
        "Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia " +
        "consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro " +
        "quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed " +
        "quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat " +
        "voluptatem. Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis " +
        "suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum " +
        "iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur, vel " +
        "illum qui dolorem eum fugiat quo voluptas nulla pariatur? At vero eos et accusamus et " +
        "iusto odio dignissimos ducimus qui blanditiis praesentium voluptatum deleniti atque " +
        "corrupti quos dolores et quas molestias excepturi sint occaecati cupiditate non " +
        "provident, similique sunt in culpa qui officia deserunt mollitia animi, id est laborum " +
        "et dolorum fuga. Et harum quidem rerum facilis est et expedita distinctio. Nam libero " +
        "tempore, cum soluta nobis est eligendi optio cumque nihil impedit quo minus id quod " +
        "maxime placeat facere possimus, omnis voluptas assumenda est, omnis dolor repellendus. " +
        "Temporibus autem quibusdam et aut officiis debitis aut rerum necessitatibus saepe " +
        "eveniet ut et voluptates repudiandae sint et molestiae non recusandae. Itaque earum " +
        "rerum hic tenetur a sapiente delectus, ut aut reiciendis voluptatibus maiores alias " +
        "consequatur aut perferendis doloribus asperiores repellat. Sed ut perspiciatis unde " +
        "omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem " +
        "aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae " +
        "dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit " +
        "aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi " +
        "nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, " +
        "adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore " +
        "magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum " +
        "exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi " +
        "consequatur? Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam " +
        "nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla " +
        "pariatur? At vero eos et accusamus et iusto odio dignissimos ducimus qui blanditiis " +
        "praesentium voluptatum deleniti atque corrupti quos dolores et quas molestias excepturi " +
        "sint occaecati cupiditate non provident, similique sunt in culpa qui officia deserunt " +
        "mollitia animi, id est laborum et dolorum fuga. Et harum quidem rerum facilis est et " +
        "expedita distinctio. Nam libero tempore, cum soluta nobis est eligendi optio cumque " +
        "nihil impedit quo minus id quod maxime placeat facere possimus, omnis voluptas " +
        "assumenda est, omnis dolor repellendus. Temporibus autem quibusdam et aut officiis " +
        "debitis aut rerum necessitatibus saepe eveniet ut et voluptates repudiandae sint et " +
        "molestiae non recusandae. Itaque earum rerum hic.";

    public static final String ENDPOINT_ADDRESS = "http://localhost:9999/" + MTOM;
    static final int NUM_CREATE = 3;
    static final String SOAP_CREATE_REQUEST_ID =
        "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
           "<SOAP-ENV:Body>" +
              "<srvc:create_mtomType xmlns:srvc=\"" + MTOM_SERVICE_NAMESPACE + "\" xmlns=\"" + MTOM_NAMESPACE + "\">" +
                 "<srvc:theInstance>" +
                    "<mtomType>" +
                       "<id>";
    static final String SOAP_CREATE_REQUEST_END =
                       "</id>" +
                       "<descript>this is a test</descript>" +
                       "<stuff>" +
                         "<xop:Include href=\"cid:LOREM\" xmlns:xop=\"http://www.w3.org/2004/08/xop/include\"/>" +
                       "</stuff>" +
                    "</mtomType>" +
                 "</srvc:theInstance>" +
              "</srvc:create_mtomType>" +
           "</SOAP-ENV:Body>" +
        "</SOAP-ENV:Envelope>";
    static final String SOAP_FIND_BY_PK_REQUEST =
        "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
           "<SOAP-ENV:Body>" +
              "<srvc:findByPrimaryKey_mtomType xmlns:srvc=\"" + MTOM_SERVICE_NAMESPACE + "\" xmlns=\"" + MTOM_NAMESPACE + "\">" +
                   "<id>1</id>" +
              "</srvc:findByPrimaryKey_mtomType>" +
           "</SOAP-ENV:Body>" +
        "</SOAP-ENV:Envelope>";
    static final String SOAP_FIND_ALL_REQUEST =
        "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
           "<SOAP-ENV:Body>" +
              "<srvc:findAll_mtomType xmlns:srvc=\"" + MTOM_SERVICE_NAMESPACE + "\" xmlns=\"" + MTOM_NAMESPACE + "\"/>" +
           "</SOAP-ENV:Body>" +
        "</SOAP-ENV:Envelope>";
    static final String SOAP_REMOVE_ID =
        "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
           "<SOAP-ENV:Body>" +
              "<srvc:delete_mtomType xmlns:srvc=\"" + MTOM_SERVICE_NAMESPACE + "\" xmlns=\"" + MTOM_NAMESPACE + "\">" +
                   "<id>";
    static final String SOAP_REMOVE_END =
                   "</id>" +
              "</srvc:delete_mtomType>" +
           "</SOAP-ENV:Body>" +
        "</SOAP-ENV:Envelope>";

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
    static Dispatch<SOAPMessage> dispatch = null;
    static DBWSBuilder builder = new DBWSBuilder();

    @Resource
    protected WebServiceContext wsContext;

    static boolean ddlCreate = false;
    static boolean ddlDrop = false;
    static boolean ddlDebug = false;

    @BeforeClass
    public static void setUp() throws WSDLException {
        try {
            conn = buildConnection();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        String ddlCreateProp = System.getProperty(DATABASE_DDL_CREATE_KEY, DEFAULT_DATABASE_DDL_CREATE);
        if ("true".equalsIgnoreCase(ddlCreateProp)) {
            ddlCreate = true;
        }
        String ddlDropProp = System.getProperty(DATABASE_DDL_DROP_KEY, DEFAULT_DATABASE_DDL_DROP);
        if ("true".equalsIgnoreCase(ddlDropProp)) {
            ddlDrop = true;
        }
        String ddlDebugProp = System.getProperty(DATABASE_DDL_DEBUG_KEY, DEFAULT_DATABASE_DDL_DEBUG);
        if ("true".equalsIgnoreCase(ddlDebugProp)) {
            ddlDebug = true;
        }
        if (ddlCreate) {
            runDdl(conn, CREATE_MTOM_TABLE, ddlDebug);
        }
        String username = System.getProperty(DATABASE_USERNAME_KEY, DEFAULT_DATABASE_USERNAME);
        String password = System.getProperty(DATABASE_PASSWORD_KEY, DEFAULT_DATABASE_PASSWORD);
        String url = System.getProperty(DATABASE_URL_KEY, DEFAULT_DATABASE_URL);
        builder.setProjectName(MTOM_TEST);
        builder.setTargetNamespace(MTOM_NAMESPACE);
        TableOperationModel mtomOp = new TableOperationModel();
        mtomOp.additionalOperations = new ArrayList<OperationModel>();
        mtomOp.setName(MTOM_TEST);
        mtomOp.setTablePattern(MTOM);
        mtomOp.setBinaryAttachment(true);
        mtomOp.setAttachmentType("MTOM");
        builder.getOperations().add(mtomOp);
        builder.quiet = true;
        builder.setLogLevel(SessionLog.FINE_LABEL);
        builder.setDriver(DATABASE_DRIVER);
        builder.setPlatformClassname(DATABASE_PLATFORM);
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
        MTOMTestSuite mtomTestSuite = new MTOMTestSuite();
        mtomTestSuite.mtomEnabled = true;
        endpoint = Endpoint.create(mtomTestSuite);
        endpoint.publish(ENDPOINT_ADDRESS);
        QName serviceQName = new QName(MTOM_SERVICE_NAMESPACE, MTOM_SERVICE);
        portQName = new QName(MTOM_SERVICE_NAMESPACE, MTOM_PORT);
        testService = Service.create(serviceQName);
        testService.addPort(portQName, SOAP11HTTP_BINDING, ENDPOINT_ADDRESS);
        dispatch = testService.createDispatch(portQName, SOAPMessage.class,
            MESSAGE, new WebServiceFeature[]{new MTOMFeature(100)});
    }

    @AfterClass
    public static void teardown() {
        if (endpoint != null) {
            endpoint.stop();
        }
        if (ddlDrop) {
            runDdl(conn, DROP_MTOM_TABLE, ddlDebug);
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
        super.init(new XRDynamicClassLoader(
            Thread.currentThread().getContextClassLoader()), null, true);
    }

    @Override
    public SOAPMessage invoke(SOAPMessage request) {
        if (wsContext != null) {
            setMessageContext(wsContext.getMessageContext());
        }
        return super.invoke(request);
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
        ((DatabaseLogin)login).setUsesStreamsForBinding(true);
        orProject.setDatasourceLogin(login);
        ProjectHelper.fixOROXAccessors(orProject, oxProject);
        DatabaseSession databaseSession = orProject.createDatabaseSession();
        databaseSession.dontLogMessages();
        xrService.setORSession(databaseSession);
        xrService.setXMLContext(new XMLContext(oxProject));
        xrService.setOXSession(xrService.getXMLContext().getSession(0));
    }

    @Test
    public void mtomTest_create() {
        try {
            MessageFactory factory = ((SOAPBinding)dispatch.getBinding()).getMessageFactory();
            DataHandler dataHandler = new DataHandler(new ByteArrayDataSource(LOREM_IPSUM.getBytes(),
                "application/octet-stream"));
            for (int i = 1; i <= NUM_CREATE; i ++) {
                SOAPMessage request = factory.createMessage();
                SOAPPart part = request.getSOAPPart();
                DOMSource domSource = new DOMSource(getDocumentBuilder().parse(
                    new InputSource(new StringReader(SOAP_CREATE_REQUEST_ID + i +
                        SOAP_CREATE_REQUEST_END))));
                part.setContent(domSource);
                AttachmentPart attachmentPart1 = request.createAttachmentPart(dataHandler);
                attachmentPart1.setContentId("LOREM");
                attachmentPart1.setContentType("application/octet-stream");
                request.addAttachmentPart(attachmentPart1);
                request.saveChanges();
                dispatch.invoke(request);
            }
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void mtomTest_findByPK() {
        try {
            MessageFactory factory = ((SOAPBinding)dispatch.getBinding()).getMessageFactory();
            SOAPMessage request = factory.createMessage();
            SOAPPart part = request.getSOAPPart();
            DOMSource domSource = new DOMSource(getDocumentBuilder().parse(
                new InputSource(new StringReader(SOAP_FIND_BY_PK_REQUEST))));
            part.setContent(domSource);
            SOAPMessage response = null;
            response = dispatch.invoke(request);
            AttachmentPart aPart = (AttachmentPart)response.getAttachments().next();
            DataHandler dh = aPart.getDataHandler();
            InputStream inputStream = dh.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte buf[] = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                baos.write(buf, 0, len);
            }
            baos.close();
            inputStream.close();
            String responseString = baos.toString();
            assertTrue("incorrect number of bytes returned from database",
                responseString.length() == 5000);
            assertEquals("incorrect content from database", LOREM_IPSUM, responseString);
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void mtomTest_findAll() {
        try {
            MessageFactory factory = ((SOAPBinding)dispatch.getBinding()).getMessageFactory();
            SOAPMessage request = factory.createMessage();
            SOAPPart part = request.getSOAPPart();
            DOMSource domSource = new DOMSource(getDocumentBuilder().parse(
                new InputSource(new StringReader(SOAP_FIND_ALL_REQUEST))));
            part.setContent(domSource);
            SOAPMessage response = null;
            response = dispatch.invoke(request);
            assertTrue("incorrect number of attachments",
                response.countAttachments() == 3);
            for (Iterator<AttachmentPart> attachmentsIterator =
                response.getAttachments(); attachmentsIterator.hasNext();) {
                AttachmentPart aPart = attachmentsIterator.next();
                DataHandler dh = aPart.getDataHandler();
                InputStream inputStream = dh.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte buf[] = new byte[4096];
                int len;
                while ((len = inputStream.read(buf)) > 0) {
                    baos.write(buf, 0, len);
                }
                baos.close();
                inputStream.close();
                String responseString = baos.toString();
                assertTrue("incorrect number of bytes returned from database",
                    responseString.length() == 5000);
                assertEquals("incorrect content from database", LOREM_IPSUM, responseString);
            }
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void mtomTest_removeAll() {
        try {
            MessageFactory factory = ((SOAPBinding)dispatch.getBinding()).getMessageFactory();
            for (int i = 1; i <= NUM_CREATE; i ++) {
                SOAPMessage request = factory.createMessage();
                SOAPPart part = request.getSOAPPart();
                DOMSource domSource = new DOMSource(getDocumentBuilder().parse(
                    new InputSource(new StringReader(SOAP_REMOVE_ID + i +
                        SOAP_REMOVE_END))));
                part.setContent(domSource);
                dispatch.invoke(request);
            }
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }
}