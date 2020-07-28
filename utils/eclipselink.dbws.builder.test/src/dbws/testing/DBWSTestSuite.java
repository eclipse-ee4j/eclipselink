/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Mike Norman - May 2008, created DBWS test package
package dbws.testing;

//javase imports
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

//java eXtension imports
import javax.wsdl.WSDLException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

//EclipseLink imports
import org.eclipse.persistence.dbws.DBWSModel;
import org.eclipse.persistence.dbws.DBWSModelProject;
import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.jpa.deployment.PersistenceUnitProcessor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProcessor;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.internal.xr.ProjectHelper;
import org.eclipse.persistence.internal.xr.XRDynamicClassLoader;
import org.eclipse.persistence.internal.xr.XRServiceAdapter;
import org.eclipse.persistence.internal.xr.XRServiceFactory;
import org.eclipse.persistence.internal.xr.XRServiceModel;
import org.eclipse.persistence.internal.xr.XmlBindingsModel;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.platform.xml.XMLComparer;
import org.eclipse.persistence.platform.xml.XMLParser;
import org.eclipse.persistence.platform.xml.XMLPlatform;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.DatasourceLogin;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.tools.dbws.DBWSBuilder;
import org.eclipse.persistence.tools.dbws.DBWSBuilderModel;
import org.eclipse.persistence.tools.dbws.DBWSBuilderModelProject;
import org.eclipse.persistence.tools.dbws.JSR109WebServicePackager;
import org.eclipse.persistence.tools.dbws.XRPackager;

import static org.eclipse.persistence.tools.dbws.DBWSBuilder.NO_SESSIONS_FILENAME;
import static org.eclipse.persistence.tools.dbws.DBWSBuilder.SESSIONS_FILENAME_KEY;
import static org.eclipse.persistence.tools.dbws.DBWSPackager.ArchiveUse.noArchive;
import static org.eclipse.persistence.tools.dbws.Util.OR_PRJ_SUFFIX;
import static org.eclipse.persistence.tools.dbws.Util.OX_PRJ_SUFFIX;
import static org.eclipse.persistence.tools.dbws.XRPackager.__nullStream;

public class DBWSTestSuite {

    public static final String DATABASE_DRIVER = "com.mysql.jdbc.Driver";
    public static final String DATABASE_PLATFORM =
        "org.eclipse.persistence.platform.database.MySQLPlatform";

    public static final String DATABASE_USERNAME_KEY = "db.user";
    public static final String DEFAULT_DATABASE_USERNAME = "user";
    public static final String DATABASE_PASSWORD_KEY = "db.pwd";
    public static final String DEFAULT_DATABASE_PASSWORD = "password";
    public static final String DATABASE_URL_KEY = "db.url";
    public static final String DEFAULT_DATABASE_URL = "jdbc:mysql://localhost:3306/test";
    public static final String DATABASE_DDL_CREATE_KEY = "db.ddl.create";
    public static final String DEFAULT_DATABASE_DDL_CREATE = "false";
    public static final String DATABASE_DDL_DROP_KEY = "db.ddl.drop";
    public static final String DEFAULT_DATABASE_DDL_DROP = "false";
    public static final String DATABASE_DDL_DEBUG_KEY = "db.ddl.debug";
    public static final String DEFAULT_DATABASE_DDL_DEBUG = "false";

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

    public static final String NONSENCE_WHERE_SQL = " WHERE 0=1";
    public static final String SECONDARY = "secondarySQL";
    public static final String SECONDARY_TEST = SECONDARY + "Test";
    public static final String SECONDARY_SERVICE = SECONDARY + "Service";
    public static final String SECONDARY_NAMESPACE = "urn:" + SECONDARY;
    public static final String SECONDARY_SERVICE_NAMESPACE = "urn:" + SECONDARY_SERVICE;
    public static final String SECONDARY_PORT = SECONDARY_SERVICE + "Port";
    public static final String SECONDARY_COUNT_SQL =
        "select count(*) as \"COUNT\", max(SAL) as \"MAX-Salary\" from secondary";
    public static final String SECONDARY_COUNT_SCHEMA_TYPE = "secondaryAggregate";
    public static final String SECONDARY_ALL_SQL =
        "select * from secondary";
    public static final String SECONDARY_ALL_SCHEMA_TYPE = "secondaryType";

    public static final String SQLCOLLECTION = "sqlAsCollection";
    public static final String SQLCOLLECTION_NAMESPACE = "urn:" + SQLCOLLECTION;
    public static final String SQLCOLLECTION_SERVICE_NAMESPACE = SQLCOLLECTION_NAMESPACE + "Service";
    public static final String SQLCOLLECTION_SERVICE = SQLCOLLECTION + "Service";
    public static final String SQLCOLLECTION_SERVICE_PORT = SQLCOLLECTION_SERVICE + "Port";

    public static final String SOAP12 = "soap12";
    public static final String SOAP12_TEST = SOAP12 + "Test";
    public static final String SOAP12_SERVICE = SOAP12 + "Service";
    public static final String SOAP12_NAMESPACE = "urn:" + SOAP12;
    public static final String SOAP12_SERVICE_NAMESPACE = "urn:" + SOAP12_SERVICE;
    public static final String SOAP12_PORT = SOAP12_SERVICE + "Port";

    //shared JUnit fixtures
    public static Connection conn = AllTests.conn;
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
    public static DBWSBuilder builder = null;
    public static XRServiceAdapter xrService = null;
    public static ByteArrayOutputStream DBWS_SERVICE_STREAM = null;
    public static ByteArrayOutputStream DBWS_SCHEMA_STREAM = null;
    public static ByteArrayOutputStream DBWS_SESSION_STREAM = null;
    public static ByteArrayOutputStream DBWS_OR_STREAM = null;
    public static ByteArrayOutputStream DBWS_OX_STREAM = null;
    public static ByteArrayOutputStream DBWS_WSDL_STREAM = null;

    public static DBWSLogger dbwsLogger;

    /**
     * This method is to be used when sessions xml should not be generated.
     *
     * @throws WSDLException
     */
    public static void setUp() throws WSDLException {
        setUp(null, false);
    }

    /**
     * This method should be used when sessions xml is to be generated and written out
     * to DBWS_SESSION_STREAM.
     *
     * @param stageDir sessions xml will be generated and written out if non-null
     * @throws WSDLException
     */
    public static void setUp(String stageDir) throws WSDLException {
        setUp(stageDir, false);
    }

    /**
     * This method should be used when sessions xml is to be generated and written out
     * to DBWS_SESSION_STREAM.
     *
     * @param stageDir sessions xml will be generated and written out if non-null
     * @throws WSDLException
     */
    public static void setUp(String stageDir, boolean useLogger) throws WSDLException {
        comparer.setIgnoreOrder(true);
        if (builder == null) {
            builder = new DBWSBuilder();
        }
        DBWS_SERVICE_STREAM = new ByteArrayOutputStream();
        DBWS_SCHEMA_STREAM = new ByteArrayOutputStream();
        DBWS_SESSION_STREAM = new ByteArrayOutputStream();
        DBWS_OR_STREAM = new ByteArrayOutputStream();
        DBWS_OX_STREAM = new ByteArrayOutputStream();
        DBWS_WSDL_STREAM = new ByteArrayOutputStream();
        final String username = System.getProperty(DATABASE_USERNAME_KEY, DEFAULT_DATABASE_USERNAME);
        final String password = System.getProperty(DATABASE_PASSWORD_KEY, DEFAULT_DATABASE_PASSWORD);
        final String url = System.getProperty(DATABASE_URL_KEY, DEFAULT_DATABASE_URL);
        String builderString = DBWS_BUILDER_XML_USERNAME + username + DBWS_BUILDER_XML_PASSWORD +
            password + DBWS_BUILDER_XML_URL + url + DBWS_BUILDER_XML_DRIVER + DATABASE_DRIVER +
            DBWS_BUILDER_XML_PLATFORM + DATABASE_PLATFORM + DBWS_BUILDER_XML_MAIN;
        XMLContext context = new XMLContext(new DBWSBuilderModelProject());
        XMLUnmarshaller unmarshaller = context.createUnmarshaller();
        DBWSBuilderModel builderModel =
            (DBWSBuilderModel)unmarshaller.unmarshal(new StringReader(builderString));
        builder.quiet = true;
        builder.setPlatformClassname(DATABASE_PLATFORM);
        builder.properties = builderModel.properties;
        builder.operations = builderModel.operations;
        XRPackager xrPackager = new JSR109WebServicePackager(null, "WebServiceTestPackager", noArchive) {
            @Override
            public void start() {
                // do nothing - don't have to verify existence of 'stageDir' when
                // all the streams are in-memory
            }
        };
        xrPackager.setDBWSBuilder(builder);
        builder.setPackager(xrPackager);
        builder.setPackager(xrPackager);
        dbwsLogger = null;
        if (useLogger) {
            dbwsLogger = new DBWSLogger("DBWSTestLogger", null);
        }
        if (stageDir == null) {
            builder.getProperties().put(SESSIONS_FILENAME_KEY, NO_SESSIONS_FILENAME);
            builder.build(DBWS_SCHEMA_STREAM, __nullStream, DBWS_SERVICE_STREAM, DBWS_OR_STREAM,
                    DBWS_OX_STREAM, __nullStream, __nullStream, __nullStream, __nullStream, __nullStream,
                    __nullStream, __nullStream, dbwsLogger);
        } else {
            xrPackager.setStageDir(new File(stageDir));
            builder.build(DBWS_SCHEMA_STREAM, DBWS_SESSION_STREAM, DBWS_SERVICE_STREAM, DBWS_OR_STREAM,
                    DBWS_OX_STREAM, __nullStream, __nullStream, DBWS_WSDL_STREAM, __nullStream, __nullStream,
                    __nullStream, __nullStream, dbwsLogger);
        }
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
                DatasourceLogin login = new DatabaseLogin();
                login.setUserName(username);
                login.setPassword(password);
                ((DatabaseLogin) login).setConnectionString(url);
                ((DatabaseLogin) login).setDriverClassName(DATABASE_PLATFORM);
                Platform platform = builder.getDatabasePlatform();
                ConversionManager conversionManager = platform.getConversionManager();
                if (conversionManager != null) {
                    conversionManager.setLoader(xrdecl);
                }
                login.setDatasourcePlatform(platform);
                ((DatabaseLogin) login).bindAllParameters();

                Project orProject = null;
                if (DBWS_OR_STREAM.size() != 0) {
                    MetadataProcessor processor = new MetadataProcessor(new XRPersistenceUnitInfo(xrdecl),
                            new DatabaseSessionImpl(login), xrdecl, false, true, false, false, false, null, null);
                    processor.setMetadataSource(new JPAMetadataSource(xrdecl, new StringReader(DBWS_OR_STREAM.toString())));
                    PersistenceUnitProcessor.processORMetadata(processor, true, PersistenceUnitProcessor.Mode.ALL);
                    processor.addNamedQueries();
                    orProject = processor.getProject().getProject();
                } else {
                    orProject = new Project();
                }
                orProject.setName(builder.getProjectName().concat(OR_PRJ_SUFFIX));
                orProject.setDatasourceLogin(login);
                DatabaseSession databaseSession = orProject.createDatabaseSession();
                if ("off".equalsIgnoreCase(builder.getLogLevel())) {
                    databaseSession.dontLogMessages();
                } else {
                    databaseSession.setLogLevel(AbstractSessionLog.translateStringToLoggingLevel(builder.getLogLevel()));
                }
                xrService.setORSession(databaseSession);
                orProject.convertClassNamesToClasses(xrdecl);

                Project oxProject = null;
                if (DBWS_OX_STREAM.size() != 0) {
                    Map<String, OXMMetadataSource> metadataMap = new HashMap<String, OXMMetadataSource>();
                    StreamSource xml = new StreamSource(new StringReader(DBWS_OX_STREAM.toString()));
                    try {
                        JAXBContext jc = JAXBContext.newInstance(XmlBindingsModel.class);
                        Unmarshaller unmarshaller = jc.createUnmarshaller();

                        JAXBElement<XmlBindingsModel> jaxbElt = unmarshaller.unmarshal(xml, XmlBindingsModel.class);
                        XmlBindingsModel model = jaxbElt.getValue();
                        for (XmlBindings xmlBindings : model.getBindingsList()) {
                            metadataMap.put(xmlBindings.getPackageName(), new OXMMetadataSource(xmlBindings));
                        }
                    } catch (JAXBException jaxbex) {
                        jaxbex.printStackTrace();
                    }

                    Map<String, Map<String, OXMMetadataSource>> properties = new HashMap<String, Map<String, OXMMetadataSource>>();
                    properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, metadataMap);
                    try {
                        org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext jCtx =
                                org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContextFactory.createContextFromOXM(xrdecl, properties);
                        oxProject = jCtx.getXMLContext().getSession(0).getProject();
                    } catch (JAXBException e) {
                        e.printStackTrace();
                    }
                } else {
                    oxProject = new Project();
                }
                oxProject.setName(builder.getProjectName().concat(OX_PRJ_SUFFIX));

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
                prepareDescriptors(oxProject, orProject, xrdecl);
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

    /**
     * Helper method that removes empty text nodes from a Document.
     * This is typically called prior to comparing two documents
     * for equality.
     *
     */
    public static void removeEmptyTextNodes(Node node) {
        NodeList nodeList = node.getChildNodes();
        Node childNode;
        for (int x = nodeList.getLength() - 1; x >= 0; x--) {
            childNode = nodeList.item(x);
            if (childNode.getNodeType() == Node.TEXT_NODE) {
                if (childNode.getNodeValue().trim().equals("")) {
                    node.removeChild(childNode);
                }
            } else if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                removeEmptyTextNodes(childNode);
            }
        }
    }

    /**
     * Returns the given org.w3c.dom.Document as a String.
     */
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

    public static Connection buildConnection() throws ClassNotFoundException, SQLException {
        String username = System.getProperty(DATABASE_USERNAME_KEY, DEFAULT_DATABASE_USERNAME);
        String password = System.getProperty(DATABASE_PASSWORD_KEY, DEFAULT_DATABASE_PASSWORD);
        String url = System.getProperty(DATABASE_URL_KEY, DEFAULT_DATABASE_URL);
        Class.forName(DATABASE_DRIVER);
        return DriverManager.getConnection(url, username, password);
    }

    public static void runDdl(Connection conn, String ddl, boolean printStackTrace) {
        try {
            PreparedStatement pStmt = conn.prepareStatement(ddl);
            pStmt.execute();
        }
        catch (SQLException e) {
            if (printStackTrace) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Logger to test that a given message was logged correctly.
     *
     */
    public static class DBWSLogger extends Logger {
        List<String> messages;

        protected DBWSLogger(String name, String resourceBundleName) {
            super(name, resourceBundleName);
            messages = new ArrayList<String>();
        }

        public void log(Level level, String msg) {
            //System.out.println(level.getName() + ": " + msg);
            messages.add(level.getName() + ": " + msg);
        }

        public boolean hasMessages() {
            return messages != null && messages.size() > 0;
        }

        public boolean hasWarnings() {
            if (messages != null || messages.size() > 0) {
                for (String message : messages) {
                    if (message.startsWith("WARNING")) {
                        return true;
                    }
                }
            }
            return false;
        }

        public List<String> getWarnings() {
            List<String> warnings = null;
            if (messages != null || messages.size() > 0) {
                warnings = new ArrayList<String>();
                for (String message : messages) {
                    if (message.startsWith("WARNING")) {
                        warnings.add(message);
                    }
                }
            }
            return warnings;
        }

        public List<String> getMessages() {
            return messages;
        }
    }
}
