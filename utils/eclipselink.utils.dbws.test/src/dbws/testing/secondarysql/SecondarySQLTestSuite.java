/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Mike Norman - Sept 2010: added new capabilities based on https://bugs.eclipse.org/bugs/show_bug.cgi?id=322949
 ******************************************************************************/
package dbws.testing.secondarysql;

//javase imports
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

//java eXtension imports
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.ServletContext;
import javax.wsdl.WSDLException;
import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

//EclipseLink imports
import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.internal.dbws.ProviderHelper;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.xr.ProjectHelper;
import org.eclipse.persistence.internal.xr.XRDynamicClassLoader;
import org.eclipse.persistence.logging.SessionLog;
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
import org.eclipse.persistence.tools.dbws.SQLOperationModel;
import static org.eclipse.persistence.tools.dbws.DBWSBuilder.NO_SESSIONS_FILENAME;
import static org.eclipse.persistence.tools.dbws.DBWSBuilder.SESSIONS_FILENAME_KEY;
import static org.eclipse.persistence.tools.dbws.DBWSPackager.ArchiveUse.noArchive;
import static org.eclipse.persistence.tools.dbws.XRPackager.__nullStream;

//domain-specific (test) imports
import static dbws.testing.DBWSTestSuite.DATABASE_DRIVER_KEY;
import static dbws.testing.DBWSTestSuite.DATABASE_PASSWORD_KEY;
import static dbws.testing.DBWSTestSuite.DATABASE_PLATFORM_KEY;
import static dbws.testing.DBWSTestSuite.DATABASE_URL_KEY;
import static dbws.testing.DBWSTestSuite.DATABASE_USERNAME_KEY;
import static dbws.testing.DBWSTestSuite.DEFAULT_DATABASE_DRIVER;
import static dbws.testing.DBWSTestSuite.DEFAULT_DATABASE_PASSWORD;
import static dbws.testing.DBWSTestSuite.DEFAULT_DATABASE_PLATFORM;
import static dbws.testing.DBWSTestSuite.DEFAULT_DATABASE_URL;
import static dbws.testing.DBWSTestSuite.DEFAULT_DATABASE_USERNAME;
import static dbws.testing.DBWSTestSuite.NONSENCE_WHERE_SQL;
import static dbws.testing.DBWSTestSuite.SECONDARY;
import static dbws.testing.DBWSTestSuite.SECONDARY_ALL_SCHEMA_TYPE;
import static dbws.testing.DBWSTestSuite.SECONDARY_ALL_SQL;
import static dbws.testing.DBWSTestSuite.SECONDARY_COUNT_SCHEMA_TYPE;
import static dbws.testing.DBWSTestSuite.SECONDARY_COUNT_SQL;
import static dbws.testing.DBWSTestSuite.SECONDARY_NAMESPACE;
import static dbws.testing.DBWSTestSuite.SECONDARY_PORT;
import static dbws.testing.DBWSTestSuite.SECONDARY_SERVICE;
import static dbws.testing.DBWSTestSuite.SECONDARY_SERVICE_NAMESPACE;
import static dbws.testing.DBWSTestSuite.SECONDARY_TEST;

@WebServiceProvider(
    targetNamespace = SECONDARY_SERVICE_NAMESPACE,
    serviceName = SECONDARY_SERVICE,
    portName = SECONDARY_PORT
)
@ServiceMode(MESSAGE)

public class SecondarySQLTestSuite extends ProviderHelper implements Provider<SOAPMessage> {

    static final String ENDPOINT_ADDRESS = "http://localhost:9999/" + SECONDARY_TEST;
    static final String DBWS_BUILDER_XML_USERNAME =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<dbws-builder xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
          "<properties>" +
              "<property name=\"projectName\">" + SECONDARY + "</property>" +
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
              "name=\"allSecondary\" " +
              "isCollection=\"true\" " +
              "returnType=\"" + SECONDARY_ALL_SCHEMA_TYPE + "\"> " +
              "<text><![CDATA[" + SECONDARY_ALL_SQL + "]]></text>" +
              "<secondary-text><![CDATA[" + SECONDARY_ALL_SQL + 
              	NONSENCE_WHERE_SQL + "]]></secondary-text>" +
          "</sql>" +
          "<sql " +
              "name=\"countSecondary\" " +
              "isCollection=\"false\" " +
              "returnType=\"" + SECONDARY_COUNT_SCHEMA_TYPE +"\"> " +
              "<text><![CDATA[" + SECONDARY_COUNT_SQL + "]]></text>" +
              "<secondary-text><![CDATA[" + SECONDARY_COUNT_SQL + 
                NONSENCE_WHERE_SQL + "]]></secondary-text>" +
          "</sql>" +
        "</dbws-builder>";

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
        String driver = System.getProperty(DATABASE_DRIVER_KEY, DEFAULT_DATABASE_DRIVER);
        String platform = System.getProperty(DATABASE_PLATFORM_KEY, DEFAULT_DATABASE_PLATFORM);
       
        String builderString = DBWS_BUILDER_XML_USERNAME + username + DBWS_BUILDER_XML_PASSWORD +
        password + DBWS_BUILDER_XML_URL + url + DBWS_BUILDER_XML_DRIVER + driver +
        DBWS_BUILDER_XML_PLATFORM + platform + DBWS_BUILDER_XML_MAIN;
        XMLContext context = new XMLContext(new DBWSBuilderModelProject());
        XMLUnmarshaller unmarshaller = context.createUnmarshaller();
        DBWSBuilderModel builderModel =
            (DBWSBuilderModel)unmarshaller.unmarshal(new StringReader(builderString));

        builder.quiet = true;
        builder.properties = builderModel.properties;
        builder.getProperties().put(SESSIONS_FILENAME_KEY, NO_SESSIONS_FILENAME);
        builder.getTargetNamespace();
        builder.operations = builderModel.operations;
        builder.setLogLevel(SessionLog.FINE_LABEL);
        builder.setPackager(new JSR109WebServicePackager(null, "WebServiceTestPackager", noArchive) {
            @Override
            public void start() {
            }
        });
        builder.build(DBWS_SCHEMA_STREAM, __nullStream, DBWS_SERVICE_STREAM, DBWS_OR_STREAM,
            DBWS_OX_STREAM, __nullStream, __nullStream, DBWS_WSDL_STREAM, __nullStream,
            __nullStream, __nullStream, __nullStream, null);
        endpoint = Endpoint.create(new SecondarySQLTestSuite());
        endpoint.publish(ENDPOINT_ADDRESS);
        QName serviceQName = new QName(SECONDARY_SERVICE_NAMESPACE, SECONDARY_SERVICE);
        portQName = new QName(SECONDARY_SERVICE_NAMESPACE, SECONDARY_PORT);
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

     //hokey test naming convention to hack order-of-tests
     
     @Test
     public void checkSQLOperationModel() {
     	SQLOperationModel sqlModel = (SQLOperationModel)builder.operations.get(0);
     	assertEquals(SECONDARY_ALL_SQL + NONSENCE_WHERE_SQL , sqlModel.getSecondarySqlText());
     	assertFalse(sqlModel.isSimpleXMLFormat());
     	assertEquals(SECONDARY_ALL_SCHEMA_TYPE, sqlModel.getReturnType());
     }

     @Test
     public void checkSchema() {
         Document doc = xmlParser.parse(new StringReader(DBWS_SCHEMA_STREAM.toString()));
         Document controlDoc = xmlParser.parse(new StringReader(SCHEMA_CONTROL_DOC));
         assertTrue("control document not same as instance document", comparer.isNodeEqual(
                 controlDoc, doc));
     }

     public static final String SCHEMA_CONTROL_DOC =
         "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
         "<xsd:schema xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" targetNamespace=\"urn:secondarySQL\" xmlns=\"urn:secondarySQL\" elementFormDefault=\"qualified\">\n" +
  	    "   <xsd:complexType name=\"secondaryType\">\n" +
 	    "      <xsd:sequence>\n" +
 	    "         <xsd:element name=\"empno\" type=\"xsd:decimal\" minOccurs=\"0\" nillable=\"true\"/>\n" +
 	    "         <xsd:element name=\"ename\" type=\"xsd:string\" minOccurs=\"0\" nillable=\"true\"/>\n" +
 	    "         <xsd:element name=\"job\" type=\"xsd:string\" minOccurs=\"0\" nillable=\"true\"/>\n" +
 	    "         <xsd:element name=\"mgr\" type=\"xsd:decimal\" minOccurs=\"0\" nillable=\"true\"/>\n" +
 	    "         <xsd:element name=\"hiredate\" type=\"xsd:date\" minOccurs=\"0\" nillable=\"true\"/>\n" +
 	    "         <xsd:element name=\"sal\" type=\"xsd:decimal\" minOccurs=\"0\" nillable=\"true\"/>\n" +
 	    "         <xsd:element name=\"comm\" type=\"xsd:decimal\" minOccurs=\"0\" nillable=\"true\"/>\n" +
 	    "         <xsd:element name=\"deptno\" type=\"xsd:decimal\" minOccurs=\"0\" nillable=\"true\"/>\n" +
 	    "      </xsd:sequence>\n" +
 	    "   </xsd:complexType>\n" +
 	    "   <xsd:complexType name=\"secondaryAggregate\">\n" +
 	    "      <xsd:sequence>\n" +
 	    "         <xsd:element name=\"count\" type=\"xsd:integer\" minOccurs=\"0\" nillable=\"true\"/>\n" +
 	    "         <xsd:element name=\"max-salary\" type=\"xsd:decimal\" minOccurs=\"0\" nillable=\"true\"/>\n" +
 	    "      </xsd:sequence>\n" +
 	    "   </xsd:complexType>\n" +
 	    "   <xsd:element name=\"secondaryType\" type=\"secondaryType\"/>\n" +
 	    "   <xsd:element name=\"secondaryAggregate\" type=\"secondaryAggregate\"/>\n" +
 	    "</xsd:schema>";

     static final String ALL_CUSTOM_CONTROL_DOC =
         "<?xml version=\"1.0\" encoding=\"UTF-8\"?> " +
         "<all-custom>" +    
         "</all-custom>";

     static final String COUNT_REQUEST_MSG = 
         "<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
             "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
           "<env:Header/>" +
           "<env:Body>" +
             "<countSecondary xmlns=\"" + SECONDARY_SERVICE_NAMESPACE + "\"/>" +
           "</env:Body>" +
         "</env:Envelope>";
     
     @Test
     public void countSecondary() throws SOAPException, SAXException, IOException, TransformerException {
         MessageFactory factory = MessageFactory.newInstance();
         SOAPMessage request = factory.createMessage();
         SOAPPart part = request.getSOAPPart();
         DOMSource domSource = new DOMSource(getDocumentBuilder().parse(
             new InputSource(new StringReader(COUNT_REQUEST_MSG))));
         part.setContent(domSource);
         Dispatch<SOAPMessage> dispatch = testService.createDispatch(portQName, SOAPMessage.class,
             Service.Mode.MESSAGE);
         SOAPMessage response = null;
         try {
             response = dispatch.invoke(request);
         }
         catch (SOAPFaultException sfe) {
             sfe.printStackTrace();
         }
         if (response != null) {
             Source src = response.getSOAPPart().getContent();
             TransformerFactory tf = TransformerFactory.newInstance();
             Transformer transformer = tf.newTransformer();
             DOMResult result = new DOMResult();
             transformer.transform(src, result);
             Document resultDoc = (Document)result.getNode();
             Document controlDoc = xmlParser.parse(new StringReader(COUNT_RESPONSE_MSG));
             assertTrue("control document not same as instance document",
                 comparer.isNodeEqual(controlDoc, resultDoc));
         }
     }
     static final String COUNT_RESPONSE_MSG =
	     "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
	     "<SOAP-ENV:Header/>" +
	     "<SOAP-ENV:Body>" +
	       "<srvc:countSecondaryResponse xmlns=\"" + SECONDARY_NAMESPACE +
	       		"\" xmlns:srvc=\"" + SECONDARY_SERVICE_NAMESPACE + "\">" +          
	         "<srvc:result>" +
	           "<secondaryAggregate xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
	             "<count>14</count>" +
	             "<max-salary>5000.00</max-salary>" +
	           "</secondaryAggregate>" +
	         "</srvc:result>" +
	       "</srvc:countSecondaryResponse>" +
	     "</SOAP-ENV:Body>" +
	     "</SOAP-ENV:Envelope>";

     static final String ALL_REQUEST_MSG = 
         "<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
             "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
           "<env:Header/>" +
           "<env:Body>" +
             "<allSecondary xmlns=\"" + SECONDARY_SERVICE_NAMESPACE + "\"/>" +
           "</env:Body>" +
         "</env:Envelope>";
     @Test
     public void allSecondary() throws SOAPException, SAXException, IOException, TransformerException {
         MessageFactory factory = MessageFactory.newInstance();
         SOAPMessage request = factory.createMessage();
         SOAPPart part = request.getSOAPPart();
         DOMSource domSource = new DOMSource(getDocumentBuilder().parse(
             new InputSource(new StringReader(ALL_REQUEST_MSG))));
         part.setContent(domSource);
         Dispatch<SOAPMessage> dispatch = testService.createDispatch(portQName, SOAPMessage.class,
             Service.Mode.MESSAGE);
         SOAPMessage response = null;
         try {
             response = dispatch.invoke(request);
         }
         catch (SOAPFaultException sfe) {
             sfe.printStackTrace();
         }
         if (response != null) {
             Source src = response.getSOAPPart().getContent();
             TransformerFactory tf = TransformerFactory.newInstance();
             Transformer transformer = tf.newTransformer();
             DOMResult result = new DOMResult();
             transformer.transform(src, result);
             Document resultDoc = (Document)result.getNode();
             Document controlDoc = xmlParser.parse(new StringReader(ALL_RESPONSE_MSG));
             assertTrue("control document not same as instance document",
                 comparer.isNodeEqual(controlDoc, resultDoc));
         }
     }
     static final String ALL_RESPONSE_MSG =
	   "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
	     "<SOAP-ENV:Header/>" +
	     "<SOAP-ENV:Body>" +
	       "<srvc:allSecondaryResponse xmlns=\"" + SECONDARY_NAMESPACE +
	           "\" xmlns:srvc=\"" + SECONDARY_SERVICE_NAMESPACE + "\">" +            
		     "<srvc:result>" +
	 	       "<secondaryType xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
	 		     "<empno>7369</empno>" +
	 		     "<ename>SMITH</ename>" +
	 		     "<job>CLERK</job>" +
	 		     "<mgr>7902</mgr>" +
	 		     "<hiredate>1980-12-17</hiredate>" +
	 		     "<sal>800.00</sal>" +
	 		     "<comm xsi:nil=\"true\"/>" +
	 		     "<deptno>20</deptno>" +
	 	       "</secondaryType>" +
	 	       "<secondaryType xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
	 		     "<empno>7499</empno>" +
	 		     "<ename>ALLEN</ename>" +
	 		     "<job>SALESMAN</job>" +
	 		     "<mgr>7698</mgr>" +
	 		     "<hiredate>1981-02-20</hiredate>" +
	 		     "<sal>1600.00</sal>" +
	 		     "<comm>300.00</comm>" +
	 		     "<deptno>30</deptno>" +
	 	      "</secondaryType>" +
	 	      "<secondaryType xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
	 		     "<empno>7521</empno>" +
	 		     "<ename>WARD</ename>" +
	 		     "<job>SALESMAN</job>" +
	 		     "<mgr>7698</mgr>" +
	 		     "<hiredate>1981-02-22</hiredate>" +
	 		     "<sal>1250.00</sal>" +
	 		     "<comm>500.00</comm>" +
	 		     "<deptno>30</deptno>" +
	 	      "</secondaryType>" +
	 	      "<secondaryType xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
	 		     "<empno>7566</empno>" +
	 		     "<ename>JONES</ename>" +
	 		     "<job>MANAGER</job>" +
	 		     "<mgr>7839</mgr>" +
	 		     "<hiredate>1981-04-02</hiredate>" +
	 		     "<sal>2975.00</sal>" +
	 		     "<comm xsi:nil=\"true\"/>" +
	 		     "<deptno>20</deptno>" +
	 	      "</secondaryType>" +
	 	      "<secondaryType xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
	 		     "<empno>7654</empno>" +
	 		     "<ename>MARTIN</ename>" +
	 		     "<job>SALESMAN</job>" +
	 		     "<mgr>7698</mgr>" +
	 		     "<hiredate>1981-09-28</hiredate>" +
	 		     "<sal>1250.00</sal>" +
	 		     "<comm>1400.00</comm>" +
	 		     "<deptno>30</deptno>" +
	 	      "</secondaryType>" +
	 	      "<secondaryType xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
	 		     "<empno>7698</empno>" +
	 		     "<ename>BLAKE</ename>" +
	 		     "<job>MANAGER</job>" +
	 		     "<mgr>7839</mgr>" +
	 		     "<hiredate>1981-05-01</hiredate>" +
	 		     "<sal>2850.00</sal>" +
	 		     "<comm xsi:nil=\"true\"/>" +
	 		     "<deptno>30</deptno>" +
	 	      "</secondaryType>" +
	 	      "<secondaryType xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
	 		     "<empno>7782</empno>" +
	 		     "<ename>CLARK</ename>" +
	 		     "<job>MANAGER</job>" +
	 		     "<mgr>7839</mgr>" +
	 		     "<hiredate>1981-06-09</hiredate>" +
	 		     "<sal>2450.00</sal>" +
	 		     "<comm xsi:nil=\"true\"/>" +
	 		     "<deptno>10</deptno>" +
	 	      "</secondaryType>" +
	 	      "<secondaryType xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
	 		     "<empno>7788</empno>" +
	 		     "<ename>SCOTT</ename>" +
	 		     "<job>ANALYST</job>" +
	 		     "<mgr>7566</mgr>" +
	 		     "<hiredate>1981-06-09</hiredate>" +
	 		     "<sal>3000.00</sal>" +
	 		     "<comm xsi:nil=\"true\"/>" +
	 		     "<deptno>20</deptno>" +
	 	      "</secondaryType>" +
	 	      "<secondaryType xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
	 		     "<empno>7839</empno>" +
	 		     "<ename>KING</ename>" +
	 		     "<job>PRESIDENT</job>" +
	 		     "<mgr xsi:nil=\"true\"/>" +
	 		     "<hiredate>1981-11-17</hiredate>" +
	 		     "<sal>5000.00</sal>" +
	 		     "<comm xsi:nil=\"true\"/>" +
	 		     "<deptno>10</deptno>" +
	 	      "</secondaryType>" +
	 	      "<secondaryType xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
	 		     "<empno>7844</empno>" +
	 		     "<ename>TURNER</ename>" +
	 		     "<job>SALESMAN</job>" +
	 		     "<mgr>7698</mgr>" +
	 		     "<hiredate>1981-09-08</hiredate>" +
	 		     "<sal>1500.00</sal>" +
	 		     "<comm>0.00</comm>" +
	 		     "<deptno>30</deptno>" +
	 	      "</secondaryType>" +
	 	      "<secondaryType xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
	 		     "<empno>7876</empno>" +
	 		     "<ename>ADAMS</ename>" +
	 		     "<job>CLERK</job>" +
	 		     "<mgr>7788</mgr>" +
	 		     "<hiredate>1987-05-23</hiredate>" +
	 		     "<sal>1100.00</sal>" +
	 		     "<comm xsi:nil=\"true\"/>" +
	 		     "<deptno>20</deptno>" +
	 	      "</secondaryType>" +
	 	      "<secondaryType xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
	 		     "<empno>7900</empno>" +
	 		     "<ename>JAMES</ename>" +
	 		     "<job>CLERK</job>" +
	 		     "<mgr>7698</mgr>" +
	 		     "<hiredate>1981-12-03</hiredate>" +
	 		     "<sal>950.00</sal>" +
	 		     "<comm xsi:nil=\"true\"/>" +
	 		     "<deptno>30</deptno>" +
	 	      "</secondaryType>" +
	 	      "<secondaryType xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
	 		     "<empno>7902</empno>" +
	 		     "<ename>FORD</ename>" +
	 		     "<job>ANALYST</job>" +
	 		     "<mgr>7566</mgr>" +
	 		     "<hiredate>1981-12-03</hiredate>" +
	 		     "<sal>3000.00</sal>" +
	 		     "<comm xsi:nil=\"true\"/>" +
	 		     "<deptno>20</deptno>" +
	 	      "</secondaryType>" +
	 	      "<secondaryType xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
	 		     "<empno>7934</empno>" +
	 		     "<ename>MILLER</ename>" +
	 		     "<job>CLERK</job>" +
	 		     "<mgr>7782</mgr>" +
	 		     "<hiredate>1982-01-23</hiredate>" +
	 		     "<sal>1300.00</sal>" +
	 		     "<comm xsi:nil=\"true\"/>" +
	 		     "<deptno>10</deptno>" +
	 	      "</secondaryType>" + 
		     "</srvc:result>" +
		   "</srvc:allSecondaryResponse>" +
         "</SOAP-ENV:Body>" +
       "</SOAP-ENV:Envelope>";

     @Test
     public void testForDuplicateColumns() {
         String username = System.getProperty(DATABASE_USERNAME_KEY, DEFAULT_DATABASE_USERNAME);
         String password = System.getProperty(DATABASE_PASSWORD_KEY, DEFAULT_DATABASE_PASSWORD);
         String url = System.getProperty(DATABASE_URL_KEY, DEFAULT_DATABASE_URL);
         String driver = System.getProperty(DATABASE_DRIVER_KEY, DEFAULT_DATABASE_DRIVER);
         String platform = System.getProperty(DATABASE_PLATFORM_KEY, DEFAULT_DATABASE_PLATFORM);
         String builderString = DBWS_BUILDER_XML_USERNAME + username + DBWS_BUILDER_XML_PASSWORD +
         	password + DBWS_BUILDER_XML_URL + url + DBWS_BUILDER_XML_DRIVER + driver +
         	  DBWS_BUILDER_XML_PLATFORM + platform + 
         	    "</property>" +
              "</properties>" +
            "<sql " +
              "name=\"badColumns\" " +
              "returnType=\"dontCare\"> " +
              "<text><![CDATA[dontCare]]></text>" +
              "<secondary-text><![CDATA[SELECT ENAME, ENAME FROM SECONDARY]]></secondary-text>" +
            "</sql>" +
         "</dbws-builder>";
         XMLContext context = new XMLContext(new DBWSBuilderModelProject());
         XMLUnmarshaller unmarshaller = context.createUnmarshaller();
         DBWSBuilderModel builderModel =
             (DBWSBuilderModel)unmarshaller.unmarshal(new StringReader(builderString));
         builder = new DBWSBuilder();
         builder.quiet = true;
         builder.properties = builderModel.properties;
         builder.getProperties().put(SESSIONS_FILENAME_KEY, NO_SESSIONS_FILENAME);
         builder.getTargetNamespace();
         builder.operations = builderModel.operations;
         builder.setLogLevel(SessionLog.FINE_LABEL);
         builder.setPackager(new JSR109WebServicePackager(null, "WebServiceTestPackager", noArchive) {
             @Override
             public void start() {
             }
         });
         try {
			builder.build(DBWS_SCHEMA_STREAM, __nullStream, DBWS_SERVICE_STREAM, DBWS_OR_STREAM,
			     DBWS_OX_STREAM, __nullStream, __nullStream, DBWS_WSDL_STREAM, __nullStream,
			     __nullStream, __nullStream, __nullStream, null);
		}
        catch (Exception e) {
        	assertEquals("Duplicate ResultSet columns not supported", e.getMessage());
		}
     }
}