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
 *     David McCann - Aug.17, 2011 - 2.4 - Initial implementation
 ******************************************************************************/
package dbws.testing.secondarysql;

//javase imports
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
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
import static org.junit.Assert.fail;

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

//testing imports
import dbws.testing.AllTests;
import static dbws.testing.DBWSTestSuite.DATABASE_DRIVER;
import static dbws.testing.DBWSTestSuite.DATABASE_PLATFORM;
import static dbws.testing.DBWSTestSuite.DATABASE_PASSWORD_KEY;
import static dbws.testing.DBWSTestSuite.DATABASE_URL_KEY;
import static dbws.testing.DBWSTestSuite.DATABASE_USERNAME_KEY;
import static dbws.testing.DBWSTestSuite.DATABASE_DDL_CREATE_KEY;
import static dbws.testing.DBWSTestSuite.DATABASE_DDL_DEBUG_KEY;
import static dbws.testing.DBWSTestSuite.DATABASE_DDL_DROP_KEY;
import static dbws.testing.DBWSTestSuite.DEFAULT_DATABASE_PASSWORD;
import static dbws.testing.DBWSTestSuite.DEFAULT_DATABASE_URL;
import static dbws.testing.DBWSTestSuite.DEFAULT_DATABASE_USERNAME;
import static dbws.testing.DBWSTestSuite.DEFAULT_DATABASE_DDL_CREATE;
import static dbws.testing.DBWSTestSuite.DEFAULT_DATABASE_DDL_DEBUG;
import static dbws.testing.DBWSTestSuite.DEFAULT_DATABASE_DDL_DROP;
import static dbws.testing.DBWSTestSuite.REGULAR_XML_HEADER;
import static dbws.testing.DBWSTestSuite.buildConnection;
import static dbws.testing.DBWSTestSuite.runDdl;
import static dbws.testing.DBWSTestSuite.documentToString;
import static dbws.testing.secondarysql.SecondarySQLTestSuite.SECONDARY_PORT;
import static dbws.testing.secondarysql.SecondarySQLTestSuite.SECONDARY_SERVICE;
import static dbws.testing.secondarysql.SecondarySQLTestSuite.SECONDARY_SERVICE_NAMESPACE;

@WebServiceProvider(
    targetNamespace = SECONDARY_SERVICE_NAMESPACE,
    serviceName = SECONDARY_SERVICE,
    portName = SECONDARY_PORT
)
@ServiceMode(MESSAGE)
public class SecondarySQLTestSuite extends ProviderHelper implements Provider<SOAPMessage> {

    static final String CREATE_SECONDARY_TABLE =
        "CREATE TABLE SECONDARY (" +
            "\nEMPNO NUMERIC(4)," +
            "\nENAME VARCHAR(10)," +
            "\nJOB VARCHAR(9)," +
            "\nMGR NUMERIC(4)," +
            "\nHIREDATE DATE," +
            "\nSAL DECIMAL(7,2)," +
            "\nCOMM DECIMAL(7,2)," +
            "\nDEPTNO NUMERIC(2)," +
            "\nPRIMARY KEY (EMPNO)" +
        "\n)";
    static final String[] POPULATE_SECONDARY_TABLE = new String[] {
        "INSERT INTO SECONDARY VALUES (7369,'SMITH','CLERK',7902,TO_DATE('1980-12-17 00:00:00','YYYY-MM-DD HH24:MI:SS'),800.88,NULL,20)",
        "INSERT INTO SECONDARY VALUES (7499,'ALLEN','SALESMAN',7698,TO_DATE('1981-2-20 00:00:00','YYYY-MM-DD HH24:MI:SS'),1600,300,30)",
        "INSERT INTO SECONDARY VALUES (7521,'WARD','SALESMAN',7698,TO_DATE('1981-2-22 00:00:00','YYYY-MM-DD HH24:MI:SS'),1250,500,30)",
        "INSERT INTO SECONDARY VALUES (7566,'JONES','MANAGER',7839,TO_DATE('1981-4-2 00:00:00','YYYY-MM-DD HH24:MI:SS'),2975,NULL,20)",
        "INSERT INTO SECONDARY VALUES (7654,'MARTIN','SALESMAN',7698,TO_DATE('1981-9-28 00:00:00','YYYY-MM-DD HH24:MI:SS'),1250,1400,30)",
        "INSERT INTO SECONDARY VALUES (7698,'BLAKE','MANAGER',7839,TO_DATE('1981-5-1 00:00:00','YYYY-MM-DD HH24:MI:SS'),2850,NULL,30)",
        "INSERT INTO SECONDARY VALUES (7782,'CLARK','MANAGER',7839,TO_DATE('1981-6-9 00:00:00','YYYY-MM-DD HH24:MI:SS'),2450,NULL,10)",
        "INSERT INTO SECONDARY VALUES (7788,'SCOTT','ANALYST',7566,TO_DATE('1981-06-09 00:00:00','YYYY-MM-DD HH24:MI:SS'),3000,NULL,20)",
        "INSERT INTO SECONDARY VALUES (7839,'KING','PRESIDENT',NULL,TO_DATE('1981-11-17 00:00:00','YYYY-MM-DD HH24:MI:SS'),5000.99,NULL,10)",
        "INSERT INTO SECONDARY VALUES (7844,'TURNER','SALESMAN',7698,TO_DATE('1981-9-8 00:00:00','YYYY-MM-DD HH24:MI:SS'),1500,0,30)",
        "INSERT INTO SECONDARY VALUES (7876,'ADAMS','CLERK',7788,TO_DATE('1987-05-23 00:00:00','YYYY-MM-DD HH24:MI:SS'),1100,NULL,20)",
        "INSERT INTO SECONDARY VALUES (7900,'JAMES','CLERK',7698,TO_DATE('1981-12-03 00:00:00','YYYY-MM-DD HH24:MI:SS'),950,NULL,30)",
        "INSERT INTO SECONDARY VALUES (7902,'FORD','ANALYST',7566,TO_DATE('1981-12-03 00:00:00','YYYY-MM-DD HH24:MI:SS'),3000,NULL,20)",
        "INSERT INTO SECONDARY VALUES (7934,'MILLER','CLERK',7782,TO_DATE('1982-01-23 00:00:00','YYYY-MM-DD HH24:MI:SS'),1300,NULL,10)"
        };
    static final String DROP_SECONDARY_TABLE =
        "DROP TABLE SECONDARY";

    static final String NONSENCE_WHERE_SQL = " WHERE 0=1";
    static final String SECONDARY = "secondarySQL";
    static final String SECONDARY_TEST = SECONDARY + "Test";
    static final String SECONDARY_SERVICE = SECONDARY + "Service";
    static final String SECONDARY_NAMESPACE = "urn:" + SECONDARY;
    static final String SECONDARY_SERVICE_NAMESPACE = "urn:" + SECONDARY_SERVICE;
    static final String SECONDARY_PORT = SECONDARY_SERVICE + "Port";
    static final String SECONDARY_COUNT_SQL =
        "select count(*) as \"COUNT\", CAST(max(SAL) as NUMBER(7,2)) as \"MAX-Salary\" from SECONDARY";
    static final String SECONDARY_COUNT_SCHEMA_TYPE = "secondaryAggregate";
    static final String SECONDARY_ALL_SQL =
        "select * from SECONDARY";
    static final String SECONDARY_ALL_SCHEMA_TYPE = "secondaryType";
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
              "<statement><![CDATA[" + SECONDARY_ALL_SQL + "]]></statement>" +
              "<build-statement><![CDATA[" + SECONDARY_ALL_SQL +
                  NONSENCE_WHERE_SQL + "]]></build-statement>" +
          "</sql>" +
          "<sql " +
              "name=\"countSecondary\" " +
              "isCollection=\"false\" " +
              "returnType=\"" + SECONDARY_COUNT_SCHEMA_TYPE +"\"> " +
              "<statement><![CDATA[" + SECONDARY_COUNT_SQL + "]]></statement>" +
              "<build-statement><![CDATA[" + SECONDARY_COUNT_SQL +
                NONSENCE_WHERE_SQL + "]]></build-statement>" +
          "</sql>" +
        "</dbws-builder>";

    // JUnit test fixtures
    static Connection conn = AllTests.conn;
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

    static boolean ddlCreate = false;
    static boolean ddlDrop = false;
    static boolean ddlDebug = false;

    @BeforeClass
    public static void setUp() throws WSDLException {
        if (conn == null) {
            try {
                conn = buildConnection();
            }
            catch (Exception e) {
                //e.printStackTrace(); ignore
            }
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
            runDdl(conn, CREATE_SECONDARY_TABLE, ddlDebug);
            try {
                Statement stmt = conn.createStatement();
                for (int i = 0; i < POPULATE_SECONDARY_TABLE.length; i++) {
                    stmt.addBatch(POPULATE_SECONDARY_TABLE[i]);
                }
                stmt.executeBatch();
            }
            catch (SQLException e) {
              //e.printStackTrace();
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
        if (ddlDrop) {
            runDdl(conn, DROP_SECONDARY_TABLE, ddlDebug);
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

     @Test
     public void checkSQLOperationModel() {
         SQLOperationModel sqlModel = (SQLOperationModel)builder.operations.get(0);
         assertEquals(SECONDARY_ALL_SQL + NONSENCE_WHERE_SQL , sqlModel.getBuildSql());
         assertFalse(sqlModel.isSimpleXMLFormat());
         assertEquals(SECONDARY_ALL_SCHEMA_TYPE, sqlModel.getReturnType());
     }

     @Test
     public void checkSchema() {
         Document doc = xmlParser.parse(new StringReader(DBWS_SCHEMA_STREAM.toString()));
         Document controlDoc = xmlParser.parse(new StringReader(SCHEMA_CONTROL_DOC));
         assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
     }

     public static final String SCHEMA_CONTROL_DOC =
         REGULAR_XML_HEADER +
         "<xsd:schema xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" targetNamespace=\"urn:secondarySQL\" xmlns=\"urn:secondarySQL\" elementFormDefault=\"qualified\">\n" +
          "   <xsd:complexType name=\"secondaryType\">\n" +
         "      <xsd:sequence>\n" +
         "         <xsd:element name=\"empno\" type=\"xsd:decimal\" minOccurs=\"0\" nillable=\"true\"/>\n" +
         "         <xsd:element name=\"ename\" type=\"xsd:string\" minOccurs=\"0\" nillable=\"true\"/>\n" +
         "         <xsd:element name=\"job\" type=\"xsd:string\" minOccurs=\"0\" nillable=\"true\"/>\n" +
         "         <xsd:element name=\"mgr\" type=\"xsd:decimal\" minOccurs=\"0\" nillable=\"true\"/>\n" +
         "         <xsd:element name=\"hiredate\" type=\"xsd:dateTime\" minOccurs=\"0\" nillable=\"true\"/>\n" +
         "         <xsd:element name=\"sal\" type=\"xsd:decimal\" minOccurs=\"0\" nillable=\"true\"/>\n" +
         "         <xsd:element name=\"comm\" type=\"xsd:decimal\" minOccurs=\"0\" nillable=\"true\"/>\n" +
         "         <xsd:element name=\"deptno\" type=\"xsd:decimal\" minOccurs=\"0\" nillable=\"true\"/>\n" +
         "      </xsd:sequence>\n" +
         "   </xsd:complexType>\n" +
         "   <xsd:complexType name=\"secondaryAggregate\">\n" +
         "      <xsd:sequence>\n" +
         "         <xsd:element name=\"count\" type=\"xsd:decimal\" minOccurs=\"0\" nillable=\"true\"/>\n" +
         "         <xsd:element name=\"max-salary\" type=\"xsd:decimal\" minOccurs=\"0\" nillable=\"true\"/>\n" +
         "      </xsd:sequence>\n" +
         "   </xsd:complexType>\n" +
         "   <xsd:element name=\"secondaryType\" type=\"secondaryType\"/>\n" +
         "   <xsd:element name=\"secondaryAggregate\" type=\"secondaryAggregate\"/>\n" +
         "</xsd:schema>";

     static final String ALL_CUSTOM_CONTROL_DOC =
         REGULAR_XML_HEADER +
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
             fail("An unexpected exception occurred: " + sfe.getMessage());
         }

         if (response != null) {
             Source src = response.getSOAPPart().getContent();
             TransformerFactory tf = TransformerFactory.newInstance();
             Transformer transformer = tf.newTransformer();
             DOMResult result = new DOMResult();
             transformer.transform(src, result);
             Document resultDoc = (Document)result.getNode();
             Document controlDoc = xmlParser.parse(new StringReader(COUNT_RESPONSE_MSG));
             assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(resultDoc), comparer.isNodeEqual(controlDoc, resultDoc));
         } else {
             fail("Response is null");
         }
     }
     static final String COUNT_RESPONSE_MSG =
         "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
         "<SOAP-ENV:Header/>" +
         "<SOAP-ENV:Body>" +
           "<srvc:countSecondaryResponse xmlns=\"" + SECONDARY_NAMESPACE +
                   "\" xmlns:srvc=\"" + SECONDARY_SERVICE_NAMESPACE + "\">" +
             "<srvc:result>" +
               "<secondaryAggregate>" +
                 "<count>14</count>" +
                 "<max-salary>5000.99</max-salary>" +
               "</secondaryAggregate>" +
             "</srvc:result>" +
           "</srvc:countSecondaryResponse>" +
         "</SOAP-ENV:Body>" +
         "</SOAP-ENV:Envelope>";

     static final String ALL_REQUEST_MSG =
         "<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
             "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
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
             fail("An unexpected exception occurred: " + sfe.getMessage());
         }
         if (response != null) {
             Source src = response.getSOAPPart().getContent();
             TransformerFactory tf = TransformerFactory.newInstance();
             Transformer transformer = tf.newTransformer();
             DOMResult result = new DOMResult();
             transformer.transform(src, result);
             Document resultDoc = (Document)result.getNode();
             Document controlDoc = xmlParser.parse(new StringReader(ALL_RESPONSE_MSG));
             assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(resultDoc), comparer.isNodeEqual(controlDoc, resultDoc));
         } else {
             fail("Response is null");
         }
     }
     static final String ALL_RESPONSE_MSG =
       "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
         "<SOAP-ENV:Header/>" +
         "<SOAP-ENV:Body>" +
           "<srvc:allSecondaryResponse xmlns=\"" + SECONDARY_NAMESPACE +
               "\" xmlns:srvc=\"" + SECONDARY_SERVICE_NAMESPACE + "\">" +
             "<srvc:result>" +
                "<secondaryType>" +
                  "<empno>7369</empno>" +
                  "<ename>SMITH</ename>" +
                  "<job>CLERK</job>" +
                  "<mgr>7902</mgr>" +
                  "<hiredate>1980-12-17T00:00:00.0</hiredate>" +
                  "<sal>800.88</sal>" +
                  "<comm xsi:nil=\"true\"/>" +
                  "<deptno>20</deptno>" +
                "</secondaryType>" +
                "<secondaryType>" +
                  "<empno>7499</empno>" +
                  "<ename>ALLEN</ename>" +
                  "<job>SALESMAN</job>" +
                  "<mgr>7698</mgr>" +
                  "<hiredate>1981-02-20T00:00:00.0</hiredate>" +
                  "<sal>1600</sal>" +
                  "<comm>300</comm>" +
                  "<deptno>30</deptno>" +
               "</secondaryType>" +
               "<secondaryType>" +
                  "<empno>7521</empno>" +
                  "<ename>WARD</ename>" +
                  "<job>SALESMAN</job>" +
                  "<mgr>7698</mgr>" +
                  "<hiredate>1981-02-22T00:00:00.0</hiredate>" +
                  "<sal>1250</sal>" +
                  "<comm>500</comm>" +
                  "<deptno>30</deptno>" +
               "</secondaryType>" +
               "<secondaryType>" +
                  "<empno>7566</empno>" +
                  "<ename>JONES</ename>" +
                  "<job>MANAGER</job>" +
                  "<mgr>7839</mgr>" +
                  "<hiredate>1981-04-02T00:00:00.0</hiredate>" +
                  "<sal>2975</sal>" +
                  "<comm xsi:nil=\"true\"/>" +
                  "<deptno>20</deptno>" +
               "</secondaryType>" +
               "<secondaryType>" +
                  "<empno>7654</empno>" +
                  "<ename>MARTIN</ename>" +
                  "<job>SALESMAN</job>" +
                  "<mgr>7698</mgr>" +
                  "<hiredate>1981-09-28T00:00:00.0</hiredate>" +
                  "<sal>1250</sal>" +
                  "<comm>1400</comm>" +
                  "<deptno>30</deptno>" +
               "</secondaryType>" +
               "<secondaryType>" +
                  "<empno>7698</empno>" +
                  "<ename>BLAKE</ename>" +
                  "<job>MANAGER</job>" +
                  "<mgr>7839</mgr>" +
                  "<hiredate>1981-05-01T00:00:00.0</hiredate>" +
                  "<sal>2850</sal>" +
                  "<comm xsi:nil=\"true\"/>" +
                  "<deptno>30</deptno>" +
               "</secondaryType>" +
               "<secondaryType>" +
                  "<empno>7782</empno>" +
                  "<ename>CLARK</ename>" +
                  "<job>MANAGER</job>" +
                  "<mgr>7839</mgr>" +
                  "<hiredate>1981-06-09T00:00:00.0</hiredate>" +
                  "<sal>2450</sal>" +
                  "<comm xsi:nil=\"true\"/>" +
                  "<deptno>10</deptno>" +
               "</secondaryType>" +
               "<secondaryType>" +
                  "<empno>7788</empno>" +
                  "<ename>SCOTT</ename>" +
                  "<job>ANALYST</job>" +
                  "<mgr>7566</mgr>" +
                  "<hiredate>1981-06-09T00:00:00.0</hiredate>" +
                  "<sal>3000</sal>" +
                  "<comm xsi:nil=\"true\"/>" +
                  "<deptno>20</deptno>" +
               "</secondaryType>" +
               "<secondaryType>" +
                  "<empno>7839</empno>" +
                  "<ename>KING</ename>" +
                  "<job>PRESIDENT</job>" +
                  "<mgr xsi:nil=\"true\"/>" +
                  "<hiredate>1981-11-17T00:00:00.0</hiredate>" +
                  "<sal>5000.99</sal>" +
                  "<comm xsi:nil=\"true\"/>" +
                  "<deptno>10</deptno>" +
               "</secondaryType>" +
               "<secondaryType>" +
                  "<empno>7844</empno>" +
                  "<ename>TURNER</ename>" +
                  "<job>SALESMAN</job>" +
                  "<mgr>7698</mgr>" +
                  "<hiredate>1981-09-08T00:00:00.0</hiredate>" +
                  "<sal>1500</sal>" +
                  "<comm>0</comm>" +
                  "<deptno>30</deptno>" +
               "</secondaryType>" +
               "<secondaryType>" +
                  "<empno>7876</empno>" +
                  "<ename>ADAMS</ename>" +
                  "<job>CLERK</job>" +
                  "<mgr>7788</mgr>" +
                  "<hiredate>1987-05-23T00:00:00.0</hiredate>" +
                  "<sal>1100</sal>" +
                  "<comm xsi:nil=\"true\"/>" +
                  "<deptno>20</deptno>" +
               "</secondaryType>" +
               "<secondaryType>" +
                  "<empno>7900</empno>" +
                  "<ename>JAMES</ename>" +
                  "<job>CLERK</job>" +
                  "<mgr>7698</mgr>" +
                  "<hiredate>1981-12-03T00:00:00.0</hiredate>" +
                  "<sal>950</sal>" +
                  "<comm xsi:nil=\"true\"/>" +
                  "<deptno>30</deptno>" +
               "</secondaryType>" +
               "<secondaryType>" +
                  "<empno>7902</empno>" +
                  "<ename>FORD</ename>" +
                  "<job>ANALYST</job>" +
                  "<mgr>7566</mgr>" +
                  "<hiredate>1981-12-03T00:00:00.0</hiredate>" +
                  "<sal>3000</sal>" +
                  "<comm xsi:nil=\"true\"/>" +
                  "<deptno>20</deptno>" +
               "</secondaryType>" +
               "<secondaryType>" +
                  "<empno>7934</empno>" +
                  "<ename>MILLER</ename>" +
                  "<job>CLERK</job>" +
                  "<mgr>7782</mgr>" +
                  "<hiredate>1982-01-23T00:00:00.0</hiredate>" +
                  "<sal>1300</sal>" +
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
         String builderString = DBWS_BUILDER_XML_USERNAME + username + DBWS_BUILDER_XML_PASSWORD +
             password + DBWS_BUILDER_XML_URL + url + DBWS_BUILDER_XML_DRIVER + DATABASE_DRIVER +
               DBWS_BUILDER_XML_PLATFORM + DATABASE_PLATFORM +
                 "</property>" +
              "</properties>" +
            "<sql " +
              "name=\"badColumns\" " +
              "returnType=\"dontCare\"> " +
              "<statement><![CDATA[dontCare]]></statement>" +
              "<build-statement><![CDATA[select ename, ename from secondary where 0=1]]></build-statement>" +
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