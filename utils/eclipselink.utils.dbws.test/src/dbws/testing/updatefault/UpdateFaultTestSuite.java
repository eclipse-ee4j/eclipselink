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
package dbws.testing.updatefault;

//javase imports
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

//java eXtension imports
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.wsdl.WSDLException;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Endpoint;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceException;
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
import org.eclipse.persistence.dbws.DBWSModelProject;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.internal.dbws.DBWSAdapter;
import org.eclipse.persistence.internal.dbws.ProviderHelper;
import org.eclipse.persistence.internal.dbws.SOAPResponseWriter;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.oxm.schema.SchemaModelProject;
import org.eclipse.persistence.internal.oxm.schema.model.Schema;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.internal.xr.XRDynamicClassLoader;
import org.eclipse.persistence.internal.xr.Invocation;
import org.eclipse.persistence.internal.xr.Operation;
import org.eclipse.persistence.internal.xr.Parameter;
import org.eclipse.persistence.internal.xr.ProjectHelper;
import org.eclipse.persistence.internal.xr.XRServiceModel;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.mappings.AttributeAccessor;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLLogin;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.oxm.mappings.XMLAnyCollectionMapping;
import org.eclipse.persistence.platform.xml.XMLComparer;
import org.eclipse.persistence.platform.xml.XMLParser;
import org.eclipse.persistence.platform.xml.XMLPlatform;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.platform.xml.XMLSchemaReference;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.DatasourceLogin;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.factories.XMLProjectReader;
import org.eclipse.persistence.tools.dbws.DBWSBuilder;
import org.eclipse.persistence.tools.dbws.OperationModel;
import org.eclipse.persistence.tools.dbws.TableOperationModel;
import org.eclipse.persistence.tools.dbws.JSR109WebServicePackager;
import static org.eclipse.persistence.internal.xr.Util.SERVICE_NAMESPACE_PREFIX;
import static org.eclipse.persistence.oxm.mappings.UnmarshalKeepAsElementPolicy.KEEP_UNKNOWN_AS_ELEMENT;
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
import static dbws.testing.DBWSTestSuite.SFAULT;
import static dbws.testing.DBWSTestSuite.SFAULT_NAMESPACE;
import static dbws.testing.DBWSTestSuite.SFAULT_PORT;
import static dbws.testing.DBWSTestSuite.SFAULT_SERVICE;
import static dbws.testing.DBWSTestSuite.SFAULT_SERVICE_NAMESPACE;
import static dbws.testing.DBWSTestSuite.SFAULT_TEST;

@WebServiceProvider(
    targetNamespace = SFAULT_SERVICE_NAMESPACE,
    serviceName = SFAULT_SERVICE,
    portName = SFAULT_PORT
)
@ServiceMode(MESSAGE)
public class UpdateFaultTestSuite extends ProviderHelper implements Provider<SOAPMessage> {

    public static final String ENDPOINT_ADDRESS = "http://localhost:9999/" + SFAULT;
    static final String SOAP_UPDATE_REQUEST = 
        "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
           "<SOAP-ENV:Body>" + 
              "<srvc:update_sfault_tableType xmlns:srvc=\"" + SFAULT_SERVICE_NAMESPACE + "\" xmlns=\"" + SFAULT_NAMESPACE + "\">" +
                 "<srvc:theInstance>" +
                    "<sfault_tableType>" +
                       "<id>1</id>" +
                       // name field is too long for database column
                       "<name>abcdefghij</name>" +
                    "</sfault_tableType>" +
                 "</srvc:theInstance>" +
              "</srvc:update_sfault_tableType>" + 
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
        builder.setProjectName(SFAULT_TEST);
        builder.setTargetNamespace(SFAULT_NAMESPACE);
        TableOperationModel sFaultOp = new TableOperationModel();
        sFaultOp.additionalOperations = new ArrayList<OperationModel>();
        sFaultOp.setName(SFAULT_TEST);
        sFaultOp.setTablePattern(SFAULT);
        builder.getOperations().add(sFaultOp);
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
        endpoint = Endpoint.create(new UpdateFaultTestSuite());
        endpoint.publish(ENDPOINT_ADDRESS);
        QName serviceQName = new QName(SFAULT_SERVICE_NAMESPACE, SFAULT_SERVICE);
        portQName = new QName(SFAULT_SERVICE_NAMESPACE, SFAULT_PORT);
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

    @PostConstruct
    @SuppressWarnings("serial")
    public void init() {
        parentClassLoader = new XRDynamicClassLoader(Thread.currentThread().getContextClassLoader());
        InputStream xrServiceStream = new ByteArrayInputStream(DBWS_SERVICE_STREAM.toByteArray());
        DBWSModelProject xrServiceModelProject = new DBWSModelProject();
        XMLContext xmlContext = new XMLContext(xrServiceModelProject);
        XMLUnmarshaller unmarshaller = xmlContext.createUnmarshaller();
        XRServiceModel dbwsModel = null;
        try {
            dbwsModel = (XRServiceModel)unmarshaller.unmarshal(xrServiceStream);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        xrSchemaStream = new ByteArrayInputStream(DBWS_SCHEMA_STREAM.toByteArray());
        buildService(dbwsModel);
    
        // the xrService built by 'buildService' above is overridden to produce an
        // instance of DBWSAdapter (a sub-class of XRService)
        DBWSAdapter dbwsAdapter = (DBWSAdapter)xrService;
    
        // get inline schema from WSDL - has additional types for the operations
        StringWriter sw = new StringWriter();
        InputStream wsdlInputStream = new ByteArrayInputStream(DBWS_WSDL_STREAM.toByteArray());
        try {
            StreamSource wsdlStreamSource = new StreamSource(wsdlInputStream);
            Transformer t = TransformerFactory.newInstance().newTransformer(new StreamSource(
                new StringReader(MATCH_SCHEMA)));
            StreamResult streamResult = new StreamResult(sw);
            t.transform(wsdlStreamSource, streamResult);
            sw.toString();
            wsdlInputStream.close();
            SchemaModelProject schemaProject = new SchemaModelProject();
            XMLContext xmlContext2 = new XMLContext(schemaProject);
            unmarshaller = xmlContext2.createUnmarshaller();
            Schema extendedSchema = (Schema)unmarshaller.unmarshal(new StringReader(sw.toString()));
            dbwsAdapter.setExtendedSchema(extendedSchema);
        }
        catch (Exception e) {
            // that's Ok, WSDL may not contain inline schema
        }
    
        String tns = dbwsAdapter.getExtendedSchema().getTargetNamespace();
        Project oxProject = dbwsAdapter.getOXSession().getProject();
        XMLDescriptor invocationDescriptor = new XMLDescriptor();
        invocationDescriptor.setJavaClass(Invocation.class);
        NamespaceResolver nr = new NamespaceResolver();
        invocationDescriptor.setNamespaceResolver(nr);
        nr.setDefaultNamespaceURI(tns);
        nr.put(SERVICE_NAMESPACE_PREFIX, tns);
        XMLAnyCollectionMapping parametersMapping = new XMLAnyCollectionMapping();
        parametersMapping.setAttributeName("parameters");
        parametersMapping.setAttributeAccessor(new AttributeAccessor() {
            Project oxProject;
            DBWSAdapter dbwsAdapter;
            @Override
            public Object getAttributeValueFromObject(Object object) {
              return ((Invocation)object).getParameters();
            }
            @SuppressWarnings({"unchecked", "rawtypes"})
            @Override
            public void setAttributeValueInObject(Object object, Object value) {
                Invocation invocation = (Invocation)object;
                Vector values = (Vector)value;
                for (Iterator i = values.iterator(); i.hasNext();) {
                  /* scan through values:
                   *  if XML conforms to something mapped, it an object; else it is a DOM Element
                   *  (probably a scalar). Walk through operations for the types, converting
                   *   as required. The 'key' is the local name of the element - for mapped objects,
                   *   have to get the element name from the schema context for the object
                   */
                  Object o = i.next();
                  if (o instanceof Element) {
                    Element e = (Element)o;
                    String key = e.getLocalName();
                    if ("theInstance".equals(key)) {
                        NodeList nl = e.getChildNodes();
                        for (int j = 0; j < nl.getLength(); j++) {
                            Node n = nl.item(j);
                            if (n.getNodeType() == Node.ELEMENT_NODE) {
                                try {
                                    Object theInstance = 
                                        dbwsAdapter.getXMLContext().createUnmarshaller().unmarshal(n);
                                    if (theInstance instanceof XMLRoot) {
                                        theInstance = ((XMLRoot)theInstance).getObject();
                                    }
                                    invocation.setParameter(key, theInstance);
                                    break;
                                }
                                catch (XMLMarshalException xmlMarshallException) {
                                   throw new WebServiceException(xmlMarshallException);
                                }
                            }
                        }
                    }
                    else {
                        ClassDescriptor desc = null;
                        for (XMLDescriptor xdesc : (Vector<XMLDescriptor>)oxProject.getOrderedDescriptors()) {
                            XMLSchemaReference schemaReference = xdesc.getSchemaReference();
                            if (schemaReference != null && 
                                schemaReference.getSchemaContext().equalsIgnoreCase(key)) {
                                desc = xdesc;
                                break;
                            }
                        }
                        if (desc != null) {
                            try {
                                Object theObject = 
                                    dbwsAdapter.getXMLContext().createUnmarshaller().unmarshal(e,
                                        desc.getJavaClass());
                                if (theObject instanceof XMLRoot) {
                                    theObject = ((XMLRoot)theObject).getObject();
                                }
                                invocation.setParameter(key, theObject);
                            }
                            catch (XMLMarshalException xmlMarshallException) {
                               throw new WebServiceException(xmlMarshallException);
                            }
                        }
                        else {
                            String serviceName = e.getParentNode().getLocalName();
                            boolean found = false;
                            for (Operation op : dbwsAdapter.getOperationsList()) {
                                if (op.getName().equals(serviceName)) {
                                    for (Parameter p : op.getParameters()) {
                                        if (p.getName().equals(key)) {
                                            desc = dbwsAdapter.getDescriptorsByQName().get(p.getType());
                                            if (desc != null) {
                                                found = true;
                                            }
                                            break;
                                        }
                                    }
                                }
                                if (found) {
                                    break;
                                }
                            }
                            if (found) {
                                Object theObject = 
                                    dbwsAdapter.getXMLContext().createUnmarshaller().unmarshal(e,
                                        desc.getJavaClass());
                                if (theObject instanceof XMLRoot) {
                                    theObject = ((XMLRoot)theObject).getObject();
                                }
                                invocation.setParameter(key, theObject);
                            }
                            else {
                                String val = e.getTextContent();
                                invocation.setParameter(key, val);
                            }
                        }
                    }
                  }
                  else {
                    XMLDescriptor descriptor = (XMLDescriptor)oxProject.getDescriptor(o.getClass());
                    String key = descriptor.getDefaultRootElement();
                    int idx = key.indexOf(':');
                    if (idx != -1) {
                      key = key.substring(idx+1);
                    }
                    invocation.setParameter(key, o);
                  }
                }
            }
            public AttributeAccessor setProjectAndAdapter(Project oxProject, DBWSAdapter dbwsAdapter) {
              this.oxProject = oxProject;
              this.dbwsAdapter = dbwsAdapter;
              return this;
            }
        }.setProjectAndAdapter(oxProject, dbwsAdapter));
        parametersMapping.setKeepAsElementPolicy(KEEP_UNKNOWN_AS_ELEMENT);
        invocationDescriptor.addMapping(parametersMapping);
        oxProject.addDescriptor(invocationDescriptor);
        ((DatabaseSessionImpl)dbwsAdapter.getOXSession()).initializeDescriptorIfSessionAlive(invocationDescriptor);
        dbwsAdapter.getXMLContext().storeXMLDescriptorByQName(invocationDescriptor);
    
        // create SOAP message response handler
        responseWriter = new SOAPResponseWriter(dbwsAdapter);
        responseWriter.initialize();
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
    public void soapFault() throws SOAPException, IOException, SAXException,
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
                sfe.getMessage().contains("Data too long for column 'NAME'"));
        }
        // just for debugging, keep 'response' variable alive after try-catch
        if (response != null) {
            response.hashCode(); 
        }
    }
}