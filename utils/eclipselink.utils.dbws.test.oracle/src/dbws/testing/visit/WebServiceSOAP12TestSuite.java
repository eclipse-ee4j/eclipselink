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
import java.io.InputStream;
import java.io.StringReader;

//java eXtension imports
import javax.annotation.PreDestroy;
import javax.servlet.ServletContext;
import javax.wsdl.WSDLException;
import javax.xml.namespace.QName;
import javax.xml.ws.Endpoint;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceProvider;
import static javax.xml.ws.soap.SOAPBinding.SOAP12HTTP_BINDING;

//JUnit4 imports
import org.junit.AfterClass;
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
import static org.eclipse.persistence.tools.dbws.DBWSBuilder.NO_SESSIONS_FILENAME;
import static org.eclipse.persistence.tools.dbws.DBWSBuilder.SESSIONS_FILENAME_KEY;
import static org.eclipse.persistence.tools.dbws.DBWSPackager.ArchiveUse.noArchive;
import static org.eclipse.persistence.tools.dbws.XRPackager.__nullStream;

public class WebServiceSOAP12TestSuite extends ProviderHelper {

    public static final String DATABASE_USERNAME_KEY = "db.user";
    public static final String DATABASE_PASSWORD_KEY = "db.pwd";
    public static final String DEFAULT_DATABASE_DRIVER = "oracle.jdbc.OracleDriver";
    public static final String DEFAULT_DATABASE_URL = "jdbc:oracle:thin:@localhost:1521:ORCL";
    public static final String DATABASE_URL_KEY = "db.url";
    public static final String DATABASE_DRIVER_KEY = "db.driver";
    public static final String DEFAULT_DATABASE_USERNAME = "scott";
    public static final String DEFAULT_DATABASE_PASSWORD = "tiger";
    public static final String DEFAULT_DATABASE_PLATFORM =
        "org.eclipse.persistence.platform.database.oracle.Oracle11Platform";
    
    // JUnit test fixtures
    public static XMLComparer comparer = new XMLComparer();
    public static XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
    public static XMLParser xmlParser = xmlPlatform.newXMLParser();
    public static Endpoint endpoint = null;
    public static QName portQName = null;
    public static Service testService = null;
    public static DBWSBuilder builder = new DBWSBuilder();
    public static ByteArrayOutputStream DBWS_SERVICE_STREAM = new ByteArrayOutputStream();
    public static ByteArrayOutputStream DBWS_SCHEMA_STREAM = new ByteArrayOutputStream();
    public static ByteArrayOutputStream DBWS_OR_STREAM = new ByteArrayOutputStream();
    public static ByteArrayOutputStream DBWS_OX_STREAM = new ByteArrayOutputStream();
    public static ByteArrayOutputStream DBWS_WSDL_STREAM = new ByteArrayOutputStream();
    
    public static void serviceSetup(String endPointAddress, WebServiceSOAP12TestSuite endPoint)
        throws WSDLException {
        builder.quiet = true;
        builder.useSOAP12();
        builder.setLogLevel(SessionLog.FINE_LABEL);
        builder.setDriver(DEFAULT_DATABASE_DRIVER);
        builder.setPlatformClassname(DEFAULT_DATABASE_PLATFORM);
        builder.getProperties().put(SESSIONS_FILENAME_KEY, NO_SESSIONS_FILENAME);
        String username = System.getProperty(WebServiceTestSuite.DATABASE_USERNAME_KEY);
        if (username == null) {
            fail("error retrieving database username");
        }
        builder.setUsername(username);
        String password = System.getProperty(WebServiceTestSuite.DATABASE_PASSWORD_KEY);
        if (password == null) {
            fail("error retrieving database password");
        }
        builder.setPassword(password);
        String url = System.getProperty(WebServiceTestSuite.DATABASE_URL_KEY);
        if (url == null) {
            fail("error retrieving database url");
        }
        builder.setUrl(url);
        builder.setPackager(new JSR109WebServicePackager(null, "WebServiceTestPackager", noArchive) {
            @Override
            public void start() {
            }
        });
        builder.build(DBWS_SCHEMA_STREAM, __nullStream, DBWS_SERVICE_STREAM, DBWS_OR_STREAM,
            DBWS_OX_STREAM, __nullStream, __nullStream, DBWS_WSDL_STREAM, __nullStream, __nullStream,
            __nullStream, __nullStream, null);
        endpoint = Endpoint.create(SOAP12HTTP_BINDING, endPoint);
        endpoint.publish(endPointAddress);
        WebServiceProvider testProvider = endPoint.getClass().getAnnotation(WebServiceProvider.class);
        String serviceNamespace = testProvider.targetNamespace();
        String serviceName = testProvider.serviceName();
        String portName = testProvider.portName();
        QName serviceQName = new QName(serviceNamespace, serviceName);
        portQName = new QName(serviceNamespace, portName);
        testService = Service.create(serviceQName);
        testService.addPort(portQName, SOAP12HTTP_BINDING, endPointAddress);
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
    protected InputStream initXRServicestream(ClassLoader parentClassLoader, ServletContext sc) {
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
    
    protected void init() {
        super.init(new XRDynamicClassLoader(
            Thread.currentThread().getContextClassLoader()), null, false);
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

}