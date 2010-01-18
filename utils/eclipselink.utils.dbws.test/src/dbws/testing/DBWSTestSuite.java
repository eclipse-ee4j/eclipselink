/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
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

package dbws.testing;

//javase imports
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import org.w3c.dom.Document;

//java eXtension imports
import javax.wsdl.WSDLException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

//JUnit4 imports

//EclipseLink imports
import org.eclipse.persistence.dbws.DBWSModel;
import org.eclipse.persistence.dbws.DBWSModelProject;
import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.xr.XRDynamicClassLoader;
import org.eclipse.persistence.internal.xr.ProjectHelper;
import org.eclipse.persistence.internal.xr.XRServiceAdapter;
import org.eclipse.persistence.internal.xr.XRServiceFactory;
import org.eclipse.persistence.internal.xr.XRServiceModel;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.platform.xml.XMLComparer;
import org.eclipse.persistence.platform.xml.XMLParser;
import org.eclipse.persistence.platform.xml.XMLPlatform;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
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

// domain-specific (testing) imports

public class DBWSTestSuite {

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
        "org.eclipse.persistence.platform.database.MySQLPlatform";

    public static final String SFAULT = "sfault_table";
    public static final String SFAULT_TEST = SFAULT + "Test";
    public static final String SFAULT_SERVICE = SFAULT + "Service";
    public static final String SFAULT_NAMESPACE = "urn:" + SFAULT;
    public static final String SFAULT_SERVICE_NAMESPACE = "urn:" + SFAULT_SERVICE;
    public static final String SFAULT_PORT = SFAULT_SERVICE + "Port";

    public static final String ROOTCAUSE = "rootcause_table";
    public static final String ROOTCAUSE_TEST = ROOTCAUSE + "Test";
    public static final String ROOTCAUSE_SERVICE = ROOTCAUSE + "Service";
    public static final String ROOTCAUSE_NAMESPACE = "urn:" + ROOTCAUSE;
    public static final String ROOTCAUSE_SERVICE_NAMESPACE = "urn:" + ROOTCAUSE_SERVICE;
    public static final String ROOTCAUSE_PORT = ROOTCAUSE_SERVICE + "Port";
    
    public static final String OPTLOCK = "optlock";
    public static final String OPTLOCK_TEST = OPTLOCK + "Test";
    public static final String OPTLOCK_SERVICE = OPTLOCK + "Service";
    public static final String OPTLOCK_NAMESPACE = "urn:" + OPTLOCK;
    public static final String OPTLOCK_SERVICE_NAMESPACE = "urn:" + OPTLOCK_SERVICE;
    public static final String OPTLOCK_PORT = OPTLOCK_SERVICE + "Port";

    public static final String MTOM = "mtom";
    public static final String MTOM_TEST = MTOM + "Test";
    public static final String MTOM_SERVICE = MTOM + "Service";
    public static final String MTOM_NAMESPACE = "urn:" + MTOM;
    public static final String MTOM_SERVICE_NAMESPACE = "urn:" + MTOM_SERVICE;
    public static final String MTOM_PORT = MTOM_SERVICE + "Port";

    // JUnit test fixtures

    public static String DBWS_BUILDER_XML_USERNAME;
    public static String DBWS_BUILDER_XML_PASSWORD;
    public static String DBWS_BUILDER_XML_URL;
    public static String DBWS_BUILDER_XML_DRIVER;
    public static String DBWS_BUILDER_XML_PLATFORM;
    public static String DBWS_BUILDER_XML_MAIN;
    public static XMLComparer comparer = new XMLComparer();
    public static XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
    public static XMLParser xmlParser = xmlPlatform.newXMLParser();
    public static DBWSBuilder builder = new DBWSBuilder();
    public static XRServiceAdapter xrService = null;
    public static ByteArrayOutputStream DBWS_SERVICE_STREAM = new ByteArrayOutputStream();
    public static ByteArrayOutputStream DBWS_SCHEMA_STREAM = new ByteArrayOutputStream();
    public static ByteArrayOutputStream DBWS_OR_STREAM = new ByteArrayOutputStream();
    public static ByteArrayOutputStream DBWS_OX_STREAM = new ByteArrayOutputStream();

    public static void setUp() throws WSDLException {
        final String username = System.getProperty(DATABASE_USERNAME_KEY, DEFAULT_DATABASE_USERNAME);
        final String password = System.getProperty(DATABASE_PASSWORD_KEY, DEFAULT_DATABASE_PASSWORD);
        final String url = System.getProperty(DATABASE_URL_KEY, DEFAULT_DATABASE_URL);
        final String driver = System.getProperty(DATABASE_DRIVER_KEY, DEFAULT_DATABASE_DRIVER);
        final String platform = System.getProperty(DATABASE_PLATFORM_KEY, DEFAULT_DATABASE_PLATFORM);
        String builderString = DBWS_BUILDER_XML_USERNAME + username + DBWS_BUILDER_XML_PASSWORD +
        password + DBWS_BUILDER_XML_URL + url + DBWS_BUILDER_XML_DRIVER + driver +
        DBWS_BUILDER_XML_PLATFORM + platform + DBWS_BUILDER_XML_MAIN;
        XMLContext context = new XMLContext(new DBWSBuilderModelProject());
        XMLUnmarshaller unmarshaller = context.createUnmarshaller();
        DBWSBuilderModel builderModel =
            (DBWSBuilderModel)unmarshaller.unmarshal(new StringReader(builderString));
        builder.quiet = true;
        builder.setPlatformClassname(platform);
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
            null);
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
                ((DatabaseLogin)login).setDriverClassName(driver);
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

    public static String documentToString(Document doc) {
        DOMSource domSource = new DOMSource(doc);
        StringWriter stringWriter = new StringWriter();
        StreamResult result = new StreamResult(stringWriter);
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty("indent", "yes");
            transformer.transform(domSource, result);
            return stringWriter.toString();
        } catch (Exception e) {
            // e.printStackTrace();
            return "<empty/>";
        }
    }
}
