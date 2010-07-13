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
 *     Mike Norman - May 2008, created DBWS Oracle test package
 ******************************************************************************/
package dbws.testing.visit;

//javase imports
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.w3c.dom.Document;

//java eXtension imports
import javax.wsdl.WSDLException;

//JUnit4 imports
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

//EclipseLink imports
import org.eclipse.persistence.dbws.DBWSModel;
import org.eclipse.persistence.dbws.DBWSModelProject;
import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.xr.XRDynamicClassLoader;
import org.eclipse.persistence.internal.xr.Invocation;
import org.eclipse.persistence.internal.xr.Operation;
import org.eclipse.persistence.internal.xr.ProjectHelper;
import org.eclipse.persistence.internal.xr.XRServiceAdapter;
import org.eclipse.persistence.internal.xr.XRServiceFactory;
import org.eclipse.persistence.internal.xr.XRServiceModel;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatasourceLogin;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.factories.XMLProjectReader;
import org.eclipse.persistence.tools.dbws.DBWSBuilder;
import org.eclipse.persistence.tools.dbws.DBWSBuilderModel;
import org.eclipse.persistence.tools.dbws.DBWSBuilderModelProject;
import org.eclipse.persistence.tools.dbws.XRPackager;
import static org.eclipse.persistence.tools.dbws.DBWSBuilder.NO_SESSIONS_FILENAME;
import static org.eclipse.persistence.tools.dbws.DBWSBuilder.SESSIONS_FILENAME_KEY;
import static org.eclipse.persistence.tools.dbws.XRPackager.__nullStream;

//domain-specific (a.k.a. testing) imports
import static dbws.testing.visit.WebServiceTestSuite.DEFAULT_DATABASE_DRIVER;

public class TypesTestSuite extends BuilderTestSuite {

  public static String DBWS_BUILDER_XML_USERNAME =
    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
    "<dbws-builder xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
      "<properties>" +
          "<property name=\"projectName\">testTypes</property>" +
          "<property name=\"logLevel\">off</property>" +
          "<property name=\"username\">";
  public static String DBWS_BUILDER_XML_PASSWORD =
          "</property><property name=\"password\">";
  public static String DBWS_BUILDER_XML_URL =
          "</property><property name=\"url\">";
  public static String DBWS_BUILDER_XML_MAIN = "</property>" +
          "<property name=\"driver\">oracle.jdbc.OracleDriver</property>" +
          "<property name=\"platformClassname\">org.eclipse.persistence.platform.database.oracle.Oracle11Platform</property>" +
      "</properties>" +
      "<procedure " +
         "name=\"echoInteger\" " +
         "catalogPattern=\"TEST_TYPES\" " +
         "procedurePattern=\"ECHO_INTEGER\" " +
         "isSimpleXMLFormat=\"true\" " +
      "/>" + 
      "<procedure " +
         "name=\"echoSmallint\" " +
         "catalogPattern=\"TEST_TYPES\" " +
         "procedurePattern=\"ECHO_SMALLINT\" " +
         "isSimpleXMLFormat=\"true\" " +
      "/>" +           
      "<procedure " +
         "name=\"echoNumeric\" " +
         "catalogPattern=\"TEST_TYPES\" " +
         "procedurePattern=\"ECHO_NUMERIC\" " +
         "isSimpleXMLFormat=\"true\" " +
      "/>" +      
      "<procedure " +
         "name=\"echoDec\" " +
         "catalogPattern=\"TEST_TYPES\" " +
         "procedurePattern=\"ECHO_DEC\" " +
         "isSimpleXMLFormat=\"true\" " +
      "/>" +      
      "<procedure " +
         "name=\"echoDecimal\" " +
         "catalogPattern=\"TEST_TYPES\" " +
         "procedurePattern=\"ECHO_DECIMAL\" " +
         "isSimpleXMLFormat=\"true\" " +
      "/>" +      
      "<procedure " +
         "name=\"echoNumber\" " +
         "catalogPattern=\"TEST_TYPES\" " +
         "procedurePattern=\"ECHO_NUMBER\" " +
         "isSimpleXMLFormat=\"true\" " +
      "/>" +
      "<procedure " +
         "name=\"echoVarchar\" " +
         "catalogPattern=\"TEST_TYPES\" " +
         "procedurePattern=\"ECHO_VARCHAR\" " +
         "isSimpleXMLFormat=\"true\" " +
      "/>" +
      "<procedure " +
         "name=\"echoVarchar2\" " +
         "catalogPattern=\"TEST_TYPES\" " +
         "procedurePattern=\"ECHO_VARCHAR2\" " +
         "isSimpleXMLFormat=\"true\" " +
      "/>" +
      "<procedure " +
         "name=\"echoChar\" " +
         "catalogPattern=\"TEST_TYPES\" " +
         "procedurePattern=\"ECHO_CHAR\" " +
         "isSimpleXMLFormat=\"true\" " +
      "/>" +
      "<procedure " +
         "name=\"echoReal\" " +
         "catalogPattern=\"TEST_TYPES\" " +
         "procedurePattern=\"ECHO_REAL\" " +
         "isSimpleXMLFormat=\"true\" " +
      "/>" +
      "<procedure " +
         "name=\"echoFloat\" " +
         "catalogPattern=\"TEST_TYPES\" " +
         "procedurePattern=\"ECHO_FLOAT\" " +
         "isSimpleXMLFormat=\"true\" " +
      "/>" +
      "<procedure " +
         "name=\"echoDouble\" " +
         "catalogPattern=\"TEST_TYPES\" " +
         "procedurePattern=\"ECHO_DOUBLE\" " +
         "isSimpleXMLFormat=\"true\" " +
      "/>" +
      "<procedure " +
         "name=\"echoDate\" " +
         "catalogPattern=\"TEST_TYPES\" " +
         "procedurePattern=\"ECHO_DATE\" " +
         "isSimpleXMLFormat=\"true\" " +
      "/>" +
      "<procedure " +
         "name=\"echoTimestamp\" " +
         "catalogPattern=\"TEST_TYPES\" " +
         "procedurePattern=\"ECHO_TIMESTAMP\" " +
         "isSimpleXMLFormat=\"true\" " +
      "/>" +
      "<procedure " +
         "name=\"echoClob\" " +
         "catalogPattern=\"TEST_TYPES\" " +
         "procedurePattern=\"ECHO_CLOB\" " +
         "isSimpleXMLFormat=\"true\" " +
      "/>" +
      "<procedure " +
         "name=\"echoBlob\" " +
         "catalogPattern=\"TEST_TYPES\" " +
         "procedurePattern=\"ECHO_BLOB\" " +
         "isSimpleXMLFormat=\"true\" " +
      "/>" +
      "<procedure " +
         "name=\"echoLong\" " +
         "catalogPattern=\"TEST_TYPES\" " +
         "procedurePattern=\"ECHO_LONG\" " +
         "isSimpleXMLFormat=\"true\" " +
      "/>" +
      "<procedure " +
         "name=\"echoLongRaw\" " +
         "catalogPattern=\"TEST_TYPES\" " +
         "procedurePattern=\"ECHO_LONG_RAW\" " +
         "isSimpleXMLFormat=\"true\" " +
      "/>" +
      "<procedure " +
         "name=\"echoRaw\" " +
         "catalogPattern=\"TEST_TYPES\" " +
         "procedurePattern=\"ECHO_RAW\" " +
         "isSimpleXMLFormat=\"true\" " +
      "/>" +
    "</dbws-builder>";
  
  // JUnit test fixtures
  public static DBWSBuilder builder = new DBWSBuilder();
  public static XRServiceAdapter xrService = null;
  public static ByteArrayOutputStream DBWS_SERVICE_STREAM = new ByteArrayOutputStream();
  public static ByteArrayOutputStream DBWS_SCHEMA_STREAM = new ByteArrayOutputStream();
  public static ByteArrayOutputStream DBWS_OR_STREAM = new ByteArrayOutputStream();
  public static ByteArrayOutputStream DBWS_OX_STREAM = new ByteArrayOutputStream();

  @BeforeClass
  public static void setup() throws SecurityException, IllegalArgumentException, 
      ClassNotFoundException, SQLException, NoSuchFieldException, IllegalAccessException,
      WSDLException {
      BuilderTestSuite.setUp();
      String builderString = DBWS_BUILDER_XML_USERNAME + username + DBWS_BUILDER_XML_PASSWORD +
      password + DBWS_BUILDER_XML_URL + url + DBWS_BUILDER_XML_MAIN;
      XMLContext context = new XMLContext(new DBWSBuilderModelProject());
      XMLUnmarshaller unmarshaller = context.createUnmarshaller();
      DBWSBuilderModel builderModel =
          (DBWSBuilderModel)unmarshaller.unmarshal(new StringReader(builderString));
      builder.quiet = true;
      builder.setPlatformClassname(ora11Platform.getClass().getName());
      builder.properties = builderModel.properties;
      builder.getProperties().put(SESSIONS_FILENAME_KEY, NO_SESSIONS_FILENAME);
      builder.operations = builderModel.operations;
      XRPackager xrPackager = new XRPackager() {
          @Override
          public void start() {// do nothing
          }
      };
      xrPackager.setSessionsFileName(builder.getSessionsFileName());
      xrPackager.setDBWSBuilder(builder);
      builder.setPackager(xrPackager);
      builder.build(DBWS_SCHEMA_STREAM, __nullStream, DBWS_SERVICE_STREAM, DBWS_OR_STREAM,
          DBWS_OX_STREAM, __nullStream, __nullStream, __nullStream, __nullStream, __nullStream,
          __nullStream, __nullStream, null);
      XRServiceFactory factory = new XRServiceFactory() {
          @Override
          public XRServiceAdapter buildService(XRServiceModel xrServiceModel) {
              parentClassLoader = this.getClass().getClassLoader();
              xrSchemaStream = new ByteArrayInputStream(DBWS_SCHEMA_STREAM.toByteArray());
              return super.buildService(xrServiceModel);
          }
          @Override
          public void buildSessions() {
              XRDynamicClassLoader xrdecl = new XRDynamicClassLoader(parentClassLoader);
              Project orProject = null;
              if (DBWS_OR_STREAM.size() != 0) {
                  orProject = XMLProjectReader.read(new StringReader(DBWS_OR_STREAM.toString()),
                      xrdecl);
              }
              else {
                  orProject = new Project();
                  orProject.setName(builder.getProjectName() + "-dbws-or"); 
              }
              Project oxProject = null;
              if (DBWS_OX_STREAM.size() != 0) {
                  oxProject = XMLProjectReader.read(new StringReader(DBWS_OX_STREAM.toString()),
                      xrdecl);
              }
              else {
                  oxProject = new Project();
                  oxProject.setName(builder.getProjectName() + "-dbws-ox");
              }
              DatasourceLogin login = new DatabaseLogin();
              login.setUserName(username);
              login.setPassword(password);
              ((DatabaseLogin)login).setConnectionString(url);
              ((DatabaseLogin)login).setDriverClassName(DEFAULT_DATABASE_DRIVER);
              Platform platform = builder.getDatabasePlatform();;
              ConversionManager conversionManager = platform.getConversionManager();
              if (conversionManager != null) {
                  conversionManager.setLoader(xrdecl);
              }
              login.setDatasourcePlatform(platform);
              ((DatabaseLogin)login).bindAllParameters();
              orProject.setDatasourceLogin(login);
              login = (DatasourceLogin)oxProject.getDatasourceLogin();
              if (login != null) {
                  platform = login.getDatasourcePlatform();
                  if (platform != null) {
                      conversionManager = platform.getConversionManager();
                      if (conversionManager != null) {
                          conversionManager.setLoader(xrdecl);
                      }
                  }
              }
              ProjectHelper.fixOROXAccessors(orProject, oxProject);
              xrService.setORSession(orProject.createDatabaseSession());
              xrService.getORSession().dontLogMessages();
              xrService.setXMLContext(new XMLContext(oxProject));
              xrService.setOXSession(xrService.getXMLContext().getSession(0));
          }
      };
      context = new XMLContext(new DBWSModelProject());
      unmarshaller = context.createUnmarshaller();
      DBWSModel model = (DBWSModel)unmarshaller.unmarshal(
          new StringReader(DBWS_SERVICE_STREAM.toString()));
      xrService = factory.buildService(model);
  }

  @Test
  public void echoSmallint() {
      Invocation invocation = new Invocation("echoSmallint");
      invocation.setParameter("PSMALLINT", Integer.valueOf(7));
      Operation op = xrService.getOperation(invocation.getName());
      Object result = op.invoke(xrService, invocation);
      assertNotNull("result is null", result);
      Document doc = xmlPlatform.createDocument();
      XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
      marshaller.marshal(result, doc);
      Document controlDoc = xmlParser.parse(new StringReader(ECHO_SMALLINT_RESULT));
      assertTrue("control document not same as instance document", comparer.isNodeEqual(
          controlDoc, doc));
  }
  public static final String ECHO_SMALLINT_RESULT =
      "<?xml version = '1.0' encoding = 'UTF-8'?>" +
      "<simple-xml-format>" +
         "<simple-xml>" +
            "<result>7</result>" +
         "</simple-xml>" +
      "</simple-xml-format>";


  @Test
  public void echoInteger() {
      Invocation invocation = new Invocation("echoInteger");
      invocation.setParameter("PINTEGER", Integer.valueOf(128));
      Operation op = xrService.getOperation(invocation.getName());
      Object result = op.invoke(xrService, invocation);
      assertNotNull("result is null", result);
      Document doc = xmlPlatform.createDocument();
      XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
      marshaller.marshal(result, doc);
      Document controlDoc = xmlParser.parse(new StringReader(ECHO_INTEGER_RESULT));
      assertTrue("control document not same as instance document", comparer.isNodeEqual(
          controlDoc, doc));
  }
  public static final String ECHO_INTEGER_RESULT =
      "<?xml version = '1.0' encoding = 'UTF-8'?>" +
      "<simple-xml-format>" +
         "<simple-xml>" +
            "<result>128</result>" +
         "</simple-xml>" +
      "</simple-xml-format>";

  @Test
  public void echoNumeric() {
      Invocation invocation = new Invocation("echoNumeric");
      invocation.setParameter("PNUMERIC", new BigDecimal("123.45"));
      Operation op = xrService.getOperation(invocation.getName());
      Object result = op.invoke(xrService, invocation);
      assertNotNull("result is null", result);
      Document doc = xmlPlatform.createDocument();
      XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
      marshaller.marshal(result, doc);
      Document controlDoc = xmlParser.parse(new StringReader(ECHO_NUMERIC_RESULT));
      assertTrue("control document not same as instance document", comparer.isNodeEqual(
          controlDoc, doc));
  }
  public static final String ECHO_NUMERIC_RESULT =
      "<?xml version = '1.0' encoding = 'UTF-8'?>" +
      "<simple-xml-format>" +
         "<simple-xml>" +
            "<result>123.45</result>" +
         "</simple-xml>" +
      "</simple-xml-format>";

  @Test
  public void echoDec() {
      Invocation invocation = new Invocation("echoDec");
      invocation.setParameter("PDEC", new BigDecimal("543.21"));
      Operation op = xrService.getOperation(invocation.getName());
      Object result = op.invoke(xrService, invocation);
      assertNotNull("result is null", result);
      Document doc = xmlPlatform.createDocument();
      XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
      marshaller.marshal(result, doc);
      Document controlDoc = xmlParser.parse(new StringReader(ECHO_DEC_RESULT));
      assertTrue("control document not same as instance document", comparer.isNodeEqual(
          controlDoc, doc));
  }
  public static final String ECHO_DEC_RESULT =
      "<?xml version = '1.0' encoding = 'UTF-8'?>" +
      "<simple-xml-format>" +
         "<simple-xml>" +
            "<result>543.21</result>" +
         "</simple-xml>" +
      "</simple-xml-format>";

  @Test
  public void echoDecimal() {
      Invocation invocation = new Invocation("echoDecimal");
      invocation.setParameter("PDECIMAL", new BigDecimal("23.9"));
      Operation op = xrService.getOperation(invocation.getName());
      Object result = op.invoke(xrService, invocation);
      assertNotNull("result is null", result);
      Document doc = xmlPlatform.createDocument();
      XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
      marshaller.marshal(result, doc);
      Document controlDoc = xmlParser.parse(new StringReader(ECHO_DECIMAL_RESULT));
      assertTrue("control document not same as instance document", comparer.isNodeEqual(
          controlDoc, doc));
  }
  public static final String ECHO_DECIMAL_RESULT =
      "<?xml version = '1.0' encoding = 'UTF-8'?>" +
      "<simple-xml-format>" +
         "<simple-xml>" +
            "<result>23.9</result>" +
         "</simple-xml>" +
      "</simple-xml-format>";

  @Test
  public void echoNumber() {
      Invocation invocation = new Invocation("echoNumber");
      invocation.setParameter("PNUMBER", Integer.valueOf(17));
      Operation op = xrService.getOperation(invocation.getName());
      Object result = op.invoke(xrService, invocation);
      assertNotNull("result is null", result);
      Document doc = xmlPlatform.createDocument();
      XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
      marshaller.marshal(result, doc);
      Document controlDoc = xmlParser.parse(new StringReader(ECHO_NUMBER_RESULT));
      assertTrue("control document not same as instance document", comparer.isNodeEqual(
          controlDoc, doc));
  }
  public static final String ECHO_NUMBER_RESULT =
      "<?xml version = '1.0' encoding = 'UTF-8'?>" +
      "<simple-xml-format>" +
         "<simple-xml>" +
            "<result>17.0</result>" +
         "</simple-xml>" +
      "</simple-xml-format>";

  @Test
  public void echoVarchar() {
      Invocation invocation = new Invocation("echoVarchar");
      invocation.setParameter("PVARCHAR", "this is a varchar test");
      Operation op = xrService.getOperation(invocation.getName());
      Object result = op.invoke(xrService, invocation);
      assertNotNull("result is null", result);
      Document doc = xmlPlatform.createDocument();
      XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
      marshaller.marshal(result, doc);
      Document controlDoc = xmlParser.parse(new StringReader(ECHO_VARCHAR_RESULT));
      assertTrue("control document not same as instance document", comparer.isNodeEqual(
          controlDoc, doc));
  }
  public static final String ECHO_VARCHAR_RESULT =
      "<?xml version = '1.0' encoding = 'UTF-8'?>" +
      "<simple-xml-format>" +
         "<simple-xml>" +
            "<result>this is a varchar test</result>" +
         "</simple-xml>" +
      "</simple-xml-format>";

  @Test
  public void echoVarchar2() {
      Invocation invocation = new Invocation("echoVarchar2");
      invocation.setParameter("PINPUTVARCHAR", "this is a varchar2 test");
      Operation op = xrService.getOperation(invocation.getName());
      Object result = op.invoke(xrService, invocation);
      assertNotNull("result is null", result);
      Document doc = xmlPlatform.createDocument();
      XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
      marshaller.marshal(result, doc);
      Document controlDoc = xmlParser.parse(new StringReader(ECHO_VARCHAR2_RESULT));
      assertTrue("control document not same as instance document", comparer.isNodeEqual(
          controlDoc, doc));
  }
  public static final String ECHO_VARCHAR2_RESULT =
      "<?xml version = '1.0' encoding = 'UTF-8'?>" +
      "<simple-xml-format>" +
         "<simple-xml>" +
            "<result>this is a varchar2 test</result>" +
         "</simple-xml>" +
      "</simple-xml-format>";         
  
  @Test
  public void echoChar() {
      Invocation invocation = new Invocation("echoChar");
      invocation.setParameter("PINPUTCHAR", "Q");
      Operation op = xrService.getOperation(invocation.getName());
      // something goes wrong with invoke
      Object result = op.invoke(xrService, invocation);
      assertNotNull("result is null", result);
      Document doc = xmlPlatform.createDocument();
      XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
      marshaller.marshal(result, doc);
      Document controlDoc = xmlParser.parse(new StringReader(ECHO_CHAR_RESULT));
      assertTrue("control document not same as instance document", comparer.isNodeEqual(
          controlDoc, doc));
  }
  public static final String ECHO_CHAR_RESULT =
      "<?xml version = '1.0' encoding = 'UTF-8'?>" +
      "<simple-xml-format>" +
         "<simple-xml>" +
            "<result>Q</result>" +
         "</simple-xml>" +
      "</simple-xml-format>";

  @Test
  public void echoReal() {
      Invocation invocation = new Invocation("echoReal");
      invocation.setParameter("PREAL", new Float("3.14159"));
      Operation op = xrService.getOperation(invocation.getName());
      Object result = op.invoke(xrService, invocation);
      assertNotNull("result is null", result);
      Document doc = xmlPlatform.createDocument();
      XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
      marshaller.marshal(result, doc);
      Document controlDoc = xmlParser.parse(new StringReader(ECHO_REAL_RESULT));
      assertTrue("control document not same as instance document", comparer.isNodeEqual(
          controlDoc, doc));
  }
  public static final String ECHO_REAL_RESULT =
      "<?xml version = '1.0' encoding = 'UTF-8'?>" +
      "<simple-xml-format>" +
         "<simple-xml>" +
            "<result>3.14159</result>" +
         "</simple-xml>" +
      "</simple-xml-format>";
  
  @Test
  public void echoFloat() {
      Invocation invocation = new Invocation("echoFloat");
      invocation.setParameter("PINPUTFLOAT", new Float("31415.926"));
      Operation op = xrService.getOperation(invocation.getName());
      Object result = op.invoke(xrService, invocation);
      assertNotNull("result is null", result);
      Document doc = xmlPlatform.createDocument();
      XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
      marshaller.marshal(result, doc);
      Document controlDoc = xmlParser.parse(new StringReader(ECHO_FLOAT_RESULT));
      assertTrue("control document not same as instance document", comparer.isNodeEqual(
          controlDoc, doc));
  }
  public static final String ECHO_FLOAT_RESULT =
      "<?xml version = '1.0' encoding = 'UTF-8'?>" +
      "<simple-xml-format>" +
         "<simple-xml>" +
            "<result>31415.926</result>" +
         "</simple-xml>" +
      "</simple-xml-format>";
  
  @Test
  public void echoDouble() {
      Invocation invocation = new Invocation("echoDouble");
      invocation.setParameter("PDOUBLE", new Double("314.15926"));
      Operation op = xrService.getOperation(invocation.getName());
      Object result = op.invoke(xrService, invocation);
      assertNotNull("result is null", result);
      Document doc = xmlPlatform.createDocument();
      XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
      marshaller.marshal(result, doc);
      Document controlDoc = xmlParser.parse(new StringReader(ECHO_DOUBLE_RESULT));
      assertTrue("control document not same as instance document", comparer.isNodeEqual(
          controlDoc, doc));
  }
  public static final String ECHO_DOUBLE_RESULT =
      "<?xml version = '1.0' encoding = 'UTF-8'?>" +
      "<simple-xml-format>" +
         "<simple-xml>" +
            "<result>314.15926</result>" +
         "</simple-xml>" +
      "</simple-xml-format>";
  
  @Test
  public void echoDate() throws ParseException {
      Invocation invocation = new Invocation("echoDate");
      SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
      invocation.setParameter("PINPUTDATE", format.parse("20091203"));
      Operation op = xrService.getOperation(invocation.getName());
      Object result = op.invoke(xrService, invocation);
      assertNotNull("result is null", result);
      Document doc = xmlPlatform.createDocument();
      XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
      marshaller.marshal(result, doc);
      Document controlDoc = xmlParser.parse(new StringReader(ECHO_DATE_RESULT));
      assertTrue("control document not same as instance document", comparer.isNodeEqual(
          controlDoc, doc));
  }
  public static final String ECHO_DATE_RESULT =
      "<?xml version = '1.0' encoding = 'UTF-8'?>" +
      "<simple-xml-format>" +
         "<simple-xml>" +
            "<result>2009-12-03T00:00:00.0</result>" +
         "</simple-xml>" +
      "</simple-xml-format>"; 
  
  @Test
  public void echoTimestamp() throws ParseException {
      Invocation invocation = new Invocation("echoTimestamp");
      SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd:hhmmss.SSS");
      invocation.setParameter("PINPUTTS", format.parse("20091204:091923.123"));
      Operation op = xrService.getOperation(invocation.getName());
      Object result = op.invoke(xrService, invocation);
      assertNotNull("result is null", result);
      Document doc = xmlPlatform.createDocument();
      XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
      marshaller.marshal(result, doc);
      Document controlDoc = xmlParser.parse(new StringReader(ECHO_TS_RESULT));
      assertTrue("control document not same as instance document", comparer.isNodeEqual(
          controlDoc, doc));
  }
  public static final String ECHO_TS_RESULT =
      "<?xml version = '1.0' encoding = 'UTF-8'?>" +
      "<simple-xml-format>" +
         "<simple-xml>" +
            "<result>2009-12-04T09:19:23.123</result>" +
         "</simple-xml>" +
      "</simple-xml-format>";  

  @Test
  public void echoClob() throws ParseException, SQLException {
      Invocation invocation = new Invocation("echoClob");
      invocation.setParameter("PINPUTCLOB", "This is a Clob test");
      Operation op = xrService.getOperation(invocation.getName());
      Object result = op.invoke(xrService, invocation);
      assertNotNull("result is null", result);
      Document doc = xmlPlatform.createDocument();
      XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
      marshaller.marshal(result, doc);
      Document controlDoc = xmlParser.parse(new StringReader(ECHO_CLOB_RESULT));
      assertTrue("control document not same as instance document", comparer.isNodeEqual(
          controlDoc, doc));
  }
  public static final String ECHO_CLOB_RESULT =
      "<?xml version = '1.0' encoding = 'UTF-8'?>" +
      "<simple-xml-format>" +
         "<simple-xml>" +
            "<result>This is a Clob test</result>" +
         "</simple-xml>" +
      "</simple-xml-format>";

  @Test
  public void echoBlob() throws ParseException {
      Invocation invocation = new Invocation("echoBlob");
      byte[] testBytes = "This is a test".getBytes();
      invocation.setParameter("PINPUTBLOB", testBytes);
      Operation op = xrService.getOperation(invocation.getName());
      Object result = op.invoke(xrService, invocation);
      assertNotNull("result is null", result);
      Document doc = xmlPlatform.createDocument();
      XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
      marshaller.marshal(((XMLRoot)result).getObject(), doc);
      Document controlDoc = xmlParser.parse(new StringReader(ECHO_BLOB_RESULT));
      assertTrue("control document not same as instance document", comparer.isNodeEqual(
          controlDoc, doc));
  }
  public static final String ECHO_BLOB_RESULT =
      "<?xml version = '1.0' encoding = 'UTF-8'?>" +
      "<simple-xml-format>" +
         "<simple-xml>" +
            "<result xsi:type=\"xsd:base64Binary\" " +
               "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
               "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
               "5647687063794270637942684948526C6333513D" +
            "</result>" +
         "</simple-xml>" +
      "</simple-xml-format>";
  
  @Test
  public void echoLong() throws ParseException {
      Invocation invocation = new Invocation("echoLong");
      byte[] testBytes = "This is another test".getBytes();
      invocation.setParameter("PLONG", testBytes);
      Operation op = xrService.getOperation(invocation.getName());
      Object result = op.invoke(xrService, invocation);
      assertNotNull("result is null", result);
      Document doc = xmlPlatform.createDocument();
      XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
      marshaller.marshal(((XMLRoot)result).getObject(), doc);
      Document controlDoc = xmlParser.parse(new StringReader(ECHO_LONG_RESULT));
      assertTrue("control document not same as instance document", comparer.isNodeEqual(
          controlDoc, doc));
  }
  public static final String ECHO_LONG_RESULT =
      "<?xml version = '1.0' encoding = 'UTF-8'?>" +
      "<simple-xml-format>" +
         "<simple-xml>" +
            "<result xsi:type=\"xsd:base64Binary\" " +
               "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
               "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
               "564768706379427063794268626D3930614756794948526C6333513D" +
            "</result>" +
         "</simple-xml>" +
      "</simple-xml-format>"; 
  
  @Test
  public void echoLongRaw() throws ParseException {
      Invocation invocation = new Invocation("echoLongRaw");
      byte[] testBytes = ("This is yet another test (long stringggggggggggggggggggggggggggggggggggggg" +
      		"ggggggggggggggggggggggggggggggggggggggg").getBytes();
      invocation.setParameter("PLONGRAW", testBytes);
      Operation op = xrService.getOperation(invocation.getName());
      Object result = op.invoke(xrService, invocation);
      assertNotNull("result is null", result);
      Document doc = xmlPlatform.createDocument();
      XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
      marshaller.marshal(((XMLRoot)result).getObject(), doc);
      Document controlDoc = xmlParser.parse(new StringReader(ECHO_LONGRAW_RESULT));
      assertTrue("control document not same as instance document", comparer.isNodeEqual(
          controlDoc, doc));
  }
  public static final String ECHO_LONGRAW_RESULT =
      "<?xml version = '1.0' encoding = 'UTF-8'?>" +
      "<simple-xml-format>" +
         "<simple-xml>" +
            "<result xsi:type=\"xsd:base64Binary\" " +
               "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
               "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
               "5647687063794270637942355A585167595735766447686C636942305A584E30494368736232356E49484E30636D6C755A32646E5A32646E5A32646E5A32646E5A32646E5A32646E5A32646E5A32646E5A32646E5A32646E5A32646E5A32646E5A32646E5A32646E5A32646E5A32646E5A32646E5A32646E5A32646E5A32646E5A32646E5A32646E5A32646E5A32646E5A32646E5A32633D" +
            "</result>" +
         "</simple-xml>" +
      "</simple-xml-format>";
  
  @Test
  public void echoRaw() throws ParseException {
      Invocation invocation = new Invocation("echoRaw");
      byte[] testBytes = "This is yet another test!".getBytes();
      invocation.setParameter("PRAW", testBytes);
      Operation op = xrService.getOperation(invocation.getName());
      Object result = op.invoke(xrService, invocation);
      assertNotNull("result is null", result);
      Document doc = xmlPlatform.createDocument();
      XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
      marshaller.marshal(((XMLRoot)result).getObject(), doc);
      Document controlDoc = xmlParser.parse(new StringReader(ECHO_RAW_RESULT));
      assertTrue("control document not same as instance document", comparer.isNodeEqual(
          controlDoc, doc));
  }
  public static final String ECHO_RAW_RESULT =
      "<?xml version = '1.0' encoding = 'UTF-8'?>" +
      "<simple-xml-format>" +
         "<simple-xml>" +
            "<result xsi:type=\"xsd:base64Binary\" " +
               "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
               "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
               "5647687063794270637942355A585167595735766447686C636942305A584E3049513D3D" +
            "</result>" +
         "</simple-xml>" +
      "</simple-xml-format>";  
}