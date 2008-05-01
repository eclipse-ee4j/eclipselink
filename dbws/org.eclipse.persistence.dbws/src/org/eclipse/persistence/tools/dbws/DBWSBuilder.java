/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/

package org.eclipse.persistence.tools.dbws;

// javase imports
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.security.AccessController;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.sql.Types.ARRAY;
import static java.sql.Types.DATALINK;
import static java.sql.Types.JAVA_OBJECT;
import static java.sql.Types.OTHER;
import static java.sql.Types.STRUCT;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.FINEST;
import static java.util.logging.Level.SEVERE;

// Java extension imports
import javax.wsdl.WSDLException;
import javax.xml.namespace.QName;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

// TopLink imports
import org.eclipse.persistence.Version;
import org.eclipse.persistence.dbws.DBWSModel;
import org.eclipse.persistence.dbws.DBWSModelProject;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.libraries.asm.ClassWriter;
import org.eclipse.persistence.internal.libraries.asm.CodeVisitor;
import org.eclipse.persistence.internal.libraries.asm.attrs.Annotation;
import org.eclipse.persistence.internal.libraries.asm.attrs.RuntimeVisibleAnnotations;
import org.eclipse.persistence.internal.libraries.asm.attrs.SignatureAttribute;
import org.eclipse.persistence.internal.oxm.schema.SchemaModelProject;
import org.eclipse.persistence.internal.oxm.schema.model.ComplexType;
import org.eclipse.persistence.internal.oxm.schema.model.Element;
import org.eclipse.persistence.internal.oxm.schema.model.Schema;
import org.eclipse.persistence.internal.oxm.schema.model.Sequence;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.internal.sessions.factories.XMLSessionConfigWriter;
import org.eclipse.persistence.internal.sessions.factories.model.SessionConfigs;
import org.eclipse.persistence.internal.sessions.factories.model.log.DefaultSessionLogConfig;
import org.eclipse.persistence.internal.sessions.factories.model.login.DatabaseLoginConfig;
import org.eclipse.persistence.internal.sessions.factories.model.platform.ServerPlatformConfig;
import org.eclipse.persistence.internal.sessions.factories.model.project.ProjectClassConfig;
import org.eclipse.persistence.internal.sessions.factories.model.project.ProjectConfig;
import org.eclipse.persistence.internal.sessions.factories.model.project.ProjectXMLConfig;
import org.eclipse.persistence.internal.sessions.factories.model.session.DatabaseSessionConfig;
import org.eclipse.persistence.internal.sessions.factories.model.session.ServerSessionConfig;
import org.eclipse.persistence.internal.xr.CollectionResult;
import org.eclipse.persistence.internal.xr.DeleteOperation;
import org.eclipse.persistence.internal.xr.InsertOperation;
import org.eclipse.persistence.internal.xr.NamedQueryHandler;
import org.eclipse.persistence.internal.xr.Operation;
import org.eclipse.persistence.internal.xr.Parameter;
import org.eclipse.persistence.internal.xr.QueryOperation;
import org.eclipse.persistence.internal.xr.Result;
import org.eclipse.persistence.internal.xr.UpdateOperation;
import org.eclipse.persistence.internal.xr.XRServiceModel;
import org.eclipse.persistence.internal.xr.sxf.SimpleXMLFormatProject;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.converters.SerializedObjectConverter;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.XMLLogin;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.oxm.mappings.XMLBinaryDataMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.platform.SAXPlatform;
import org.eclipse.persistence.platform.database.oracle.OraclePlatform;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.factories.XMLProjectWriter;
import org.eclipse.persistence.tools.dbws.jdbc.DbColumn;
import org.eclipse.persistence.tools.dbws.jdbc.DbStoredArgument;
import org.eclipse.persistence.tools.dbws.jdbc.DbStoredProcedure;
import org.eclipse.persistence.tools.dbws.jdbc.DbTable;
import org.eclipse.persistence.tools.dbws.jdbc.JDBCHelper;
import static org.eclipse.persistence.internal.helper.ClassConstants.ABYTE;
import static org.eclipse.persistence.internal.helper.ClassConstants.APBYTE;
import static org.eclipse.persistence.internal.libraries.asm.Constants.ACC_BRIDGE;
import static org.eclipse.persistence.internal.libraries.asm.Constants.ACC_ENUM;
import static org.eclipse.persistence.internal.libraries.asm.Constants.ACC_FINAL;
import static org.eclipse.persistence.internal.libraries.asm.Constants.ACC_PUBLIC;
import static org.eclipse.persistence.internal.libraries.asm.Constants.ACC_STATIC;
import static org.eclipse.persistence.internal.libraries.asm.Constants.ACC_SUPER;
import static org.eclipse.persistence.internal.libraries.asm.Constants.ACC_SYNTHETIC;
import static org.eclipse.persistence.internal.libraries.asm.Constants.ALOAD;
import static org.eclipse.persistence.internal.libraries.asm.Constants.ARETURN;
import static org.eclipse.persistence.internal.libraries.asm.Constants.CHECKCAST;
import static org.eclipse.persistence.internal.libraries.asm.Constants.INVOKESPECIAL;
import static org.eclipse.persistence.internal.libraries.asm.Constants.INVOKEVIRTUAL;
import static org.eclipse.persistence.internal.libraries.asm.Constants.RETURN;
import static org.eclipse.persistence.internal.libraries.asm.Constants.V1_5;
import static org.eclipse.persistence.internal.xr.Util.DBWS_OR_LABEL;
import static org.eclipse.persistence.internal.xr.Util.DBWS_OR_SESSION_NAME_SUFFIX;
import static org.eclipse.persistence.internal.xr.Util.DBWS_OR_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_OX_LABEL;
import static org.eclipse.persistence.internal.xr.Util.DBWS_OX_SESSION_NAME_SUFFIX;
import static org.eclipse.persistence.internal.xr.Util.DBWS_OX_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_SCHEMA_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_SERVICE_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_SESSIONS_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_WSDL;
import static org.eclipse.persistence.internal.xr.Util.DEFAULT_ATTACHMENT_MIMETYPE;
import static org.eclipse.persistence.internal.xr.Util.META_INF_PATHS;
import static org.eclipse.persistence.internal.xr.Util.getClassFromJDBCType;
import static org.eclipse.persistence.internal.xr.Util.TARGET_NAMESPACE_PREFIX;
import static org.eclipse.persistence.internal.xr.Util.WEB_INF_PATHS;
import static org.eclipse.persistence.oxm.XMLConstants.BASE_64_BINARY_QNAME;
import static org.eclipse.persistence.tools.dbws.Util.CREATE_OPERATION_NAME;
import static org.eclipse.persistence.tools.dbws.Util.DEFAULT_WSDL_LOCATION_URI;
import static org.eclipse.persistence.tools.dbws.Util.DBWS_PROVIDER_CLASS_FILE;
import static org.eclipse.persistence.tools.dbws.Util.DBWS_PROVIDER_NAME;
import static org.eclipse.persistence.tools.dbws.Util.DBWS_PROVIDER_PACKAGE;
import static org.eclipse.persistence.tools.dbws.Util.DBWS_PROVIDER_SOURCE_FILE;
import static org.eclipse.persistence.tools.dbws.Util.FINDALL_QUERYNAME;
import static org.eclipse.persistence.tools.dbws.Util.ORACLE_WEBSERVICES_FILENAME;
import static org.eclipse.persistence.tools.dbws.Util.PK_QUERYNAME;
import static org.eclipse.persistence.tools.dbws.Util.REMOVE_OPERATION_NAME;
import static org.eclipse.persistence.tools.dbws.Util.SWAREF_FILENAME;
import static org.eclipse.persistence.tools.dbws.Util.THE_INSTANCE_NAME;
import static org.eclipse.persistence.tools.dbws.Util.UPDATE_OPERATION_NAME;
import static org.eclipse.persistence.tools.dbws.Util.WEB_XML_FILENAME;
import static org.eclipse.persistence.tools.dbws.Util.WEBSERVICES_FILENAME;
import static org.eclipse.persistence.tools.dbws.Util.WSI_SWAREF_XSD_FILE;
import static org.eclipse.persistence.tools.dbws.Util.DEFAULT_PLATFORM_CLASSNAME;
import static org.eclipse.persistence.tools.dbws.Util.getXMLTypeFromJDBCType;
import static org.eclipse.persistence.tools.dbws.Util.InOut.OUT;
import static org.eclipse.persistence.tools.dbws.Util.WSDL_DIR;
import static org.eclipse.persistence.tools.dbws.Util.WSI_SWAREF;
import static org.eclipse.persistence.tools.dbws.Util.WSI_SWAREF_PREFIX;
import static org.eclipse.persistence.tools.dbws.Util.WSI_SWAREF_URI;

public class DBWSBuilder extends DBWSBuilderModel {

    public static final String BUILDER_PACKAGER_KEY = "dbws.builder.packager";
    public static final String BUILDER_FILE_PATH = "-dbws-builder";
    public static final String STAGE_DIR = "-stagedir";
    public static final String JAVASE_MODE_KEY = "javaseMode";
    public static final String DRIVER_KEY = "driver";
    public static final String USERNAME_KEY= "username";
    public static final String PASSWORD_KEY = "password";
    public static final String URL_KEY = "url";
    public static final String PROJNAME_KEY = "projectName";
    public static final String CONTEXT_ROOT_KEY = "contextRoot";
    public static final String DATASOURCE_KEY = "dataSource";
    public static final String SESSIONS_FILENAME_KEY = "sessionsFileName";
    public static final String PLATFORM_CLASSNAME_KEY = "platformClassname";
    public static final String ORSESSION_CUSTOMIZER_KEY = "orSessionCustomizerClassName";
    public static final String OXSESSION_CUSTOMIZER_KEY = "oxSessionCustomizerClassName";
    public static final String WSDL_URI_KEY = "wsdlLocationURI";
    public static final String LOG_LEVEL_KEY = "logLevel";
    public static final String TARGET_NAMESPACE_KEY = "targetNamespace";
    public static final String WEB_XML_PREAMBLE = 
    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
    "<web-app\n" +
    "  xmlns=\"http://java.sun.com/xml/ns/javaee\"\n" +
    "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
    "  xsi:schemaLocation=\"http://java.sun.com/xml/ns/javaee" +
    "  http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd\"\n" +
    "  version=\"2.5\"\n" +
    "  >\n" +
    "  <servlet>\n" +
    "    <servlet-name>DBWSProvider</servlet-name>\n" +
    "    <display-name>DBWSProvider</display-name>\n" +
    "    <description>JAX-WS endpoint Provider</description>\n" +
    "    <servlet-class>_dbws.DBWSProvider</servlet-class>\n" +
    "    <load-on-startup>1</load-on-startup>\n" +
    "  </servlet>\n" +
    "  <servlet-mapping>\n" +
    "    <servlet-name>DBWSProvider</servlet-name>\n" +
    "    <url-pattern>";
    // contextRoot ^^ here
    public static final String WEB_XML_POSTSCRIPT = 
            "</url-pattern>\n" +
        "  </servlet-mapping>\n" +
        "</web-app>\n";
    public static final String WSI_SWAREF_XSD =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n" +
        "<xsd:schema targetNamespace=\"" + Util.WSI_SWAREF_URI + "\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"> \n" +
        "  <xsd:simpleType name=\"" + Util.WSI_SWAREF + "\"> \n" +
        "    <xsd:restriction base=\"xsd:anyURI\"/> \n" +
        "  </xsd:simpleType> \n" +
        "</xsd:schema>";

    public static final String WEBSERVICES_PREAMBLE =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n" +
        "<webservices \n" +
          "  xmlns=\"http://java.sun.com/xml/ns/javaee\" \n" +
          "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n" +
          "  xsi:schemaLocation=\"http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/javaee_web_services_1_2.xsd\" \n" +
          "  version=\"1.2\" \n" +
          "  > \n" +
          "  <webservice-description> \n" +
            "  <webservice-description-name>";
                           // serviceName ^^ here
    public static final String WEBSERVICES_PORT_COMPONENT_NAME =
                                             "</webservice-description-name> \n" +
            "    <port-component> \n" + 
            "      <port-component-name>"; 
 // dotted-format serviceName.portName ^^ here
    public static final String WEBSERVICES_WSDL_SERVICE = 
                                             "</port-component-name> \n" +
            "      <wsdl-service xmlns:ns0=\"";
                          // service URI ^^ here

    public static final String WEBSERVICES_WSDL_PORT =
                                             "</wsdl-service> \n" +
            "      <wsdl-port xmlns:ns1=\"";
        

    public static final String WEBSERVICES_SUFFIX = 
                                             "</wsdl-port> \n" +
            "      <service-impl-bean> \n" + 
            "        <servlet-link>DBWSProvider</servlet-link> \n" + 
            "      </service-impl-bean> \n" + 
            "    </port-component> \n" + 
            "  </webservice-description> \n" + 
            "</webservices>"; 
    
    public static final String ORACLE_WEBSERVICES_PREAMBLE = 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n" +
        "<oracle-webservices \n" +
        "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n" + 
        "  xsi:noNamespaceSchemaLocation=\"http://xmlns.oracle.com/oracleas/schema/oracle-webservices-11_1.xsd\" \n" + 
        "  > \n" + 
        "  <webservice-description name=\"";
                           // serviceName ^^ here
    public static final String ORACLE_WEBSERVICES_PORT_COMPONENT_NAME =
                                             "\"> \n" + 
        "    <port-component name=\"";
              // dotted-format serviceName.portName ^^ here
    public static final String ORACLE_WEBSERVICES_SUFFIX =
                                        "\"> \n" + 
        "      <policy-references>\n" +
        "        <!-- add policies here -->\n" +
        "      </policy-references>\n" +
        "    </port-component>\n" +
        "  </webservice-description>\n" +
        "</oracle-webservices>";
    public static final String DBWS_PROVIDER_SOURCE_PREAMBLE =
        "package _dbws;\n" +
        "\n" +
        "import javax.annotation.PostConstruct;\n" +
        "import javax.annotation.PreDestroy;\n" +
        "import javax.xml.soap.SOAPMessage;\n" +
        "import javax.xml.ws.Provider;\n" +
        "import javax.xml.ws.ServiceMode;\n" +
        "import javax.xml.ws.WebServiceProvider;\n" +
        "import static javax.xml.ws.Service.Mode.MESSAGE;\n" +
        "import oracle.toplink.internal.dbws.ProviderHelper;\n" +
        "\n" +
        "@WebServiceProvider(\n" +
        "    wsdlLocation = \"" + WEB_INF_PATHS[1] + WSDL_DIR + "/" + DBWS_WSDL + "\",\n" +
        "    serviceName = \"";
    public static final String DBWS_PROVIDER_SOURCE_PORT_NAME =
        "\",\n    portName = \"";
    public static final String DBWS_PROVIDER_SOURCE_TARGET_NAMESPACE =
        "\",\n    targetNamespace = \"";
    public static final String DBWS_PROVIDER_SOURCE_SUFFIX =
        "\"\n)\n@ServiceMode(MESSAGE)\n" +
        "public class DBWSProvider extends ProviderHelper implements Provider<SOAPMessage> {\n" +
        "    public  DBWSProvider() {\n" +
        "        super();\n" +
        "    }\n" +
        "    @Override\n" +
        "    @PostConstruct\n" +
        "    public void init() {\n" + 
        "        super.init();\n" +
        "    }\n" +
        "    @Override\n" +
        "    public SOAPMessage invoke(SOAPMessage request) {\n" + 
        "        return super.invoke(request);\n" +
        "    }\n" +
        "    @Override\n" +
        "    @PreDestroy\n" +
        "    public void destroy() {\n" + 
        "        super.destroy();\n" +
        "    }\n" +
        "};";
    public static final String ASMIFIED_DBWS_PROVIDER_HELPER =
        "oracle/toplink/internal/dbws/ProviderHelper";
    public static final String ASMIFIED_JAX_WS_PROVIDER =
        "javax/xml/ws/Provider";
    public static final String ASMIFIED_JAX_WS_WEB_SERVICE_PROVIDER = 
        "javax/xml/ws/WebServiceProvider";
    public static final String ASMIFIED_JAX_WS_SERVICE_MODE =
        "javax/xml/ws/ServiceMode";
    public static final String ASMIFIED_JSR_250_POSTCONSTRUCT =
        "javax/annotation/PostConstruct";
    public static final String ASMIFIED_JSR_250_PREDESTROY = 
        "javax/annotation/PreDestroy";
    public static final String ASMIFIED_JAX_WS_SERVICE =
        "javax/xml/ws/Service";
    public static final String ASMIFIED_SOAP_MESSAGE = 
        "javax/xml/soap/SOAPMessage";
    
    protected DBWSBuilderPackager packager;
    protected Logger logger;
    public boolean quiet = false;
    protected String destDir;
    protected DatabasePlatform databasePlatform;
    protected Project orProject;
    protected Project oxProject;
    protected WSDLGenerator wsdlGenerator = null;
    protected Schema schema = new Schema();
    protected NamespaceResolver ns = schema.getNamespaceResolver();
    protected XRServiceModel xrServiceModel = new DBWSModel();
    protected List<DbTable> dbTables = new ArrayList<DbTable>();   
    protected List<DbStoredProcedure> dbStoredProcedures = new ArrayList<DbStoredProcedure>();
    
    public DBWSBuilder() {
        super();
        ns.put("xsi", W3C_XML_SCHEMA_INSTANCE_NS_URI);
        ns.put("xsd", W3C_XML_SCHEMA_NS_URI);
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws WSDLException {
        DBWSBuilder builder = new DBWSBuilder();
        String packagerClassname = System.getProperty(BUILDER_PACKAGER_KEY);
        if (packagerClassname != null) {
            DBWSBuilderPackager dbwsBuilderPackager = null;
            try {
                Class<DBWSBuilderPackager> packagerClass = null;
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                   packagerClass = (Class<DBWSBuilderPackager>)AccessController.doPrivileged(
                        new PrivilegedClassForName(packagerClassname));
                }
                else {
                    packagerClass = PrivilegedAccessHelper.getClassForName(packagerClassname);
                }
                dbwsBuilderPackager = packagerClass.newInstance();
            }
            catch (Exception e) { /* ignore */ }
            if (dbwsBuilderPackager != null) {
                builder.setPackager(dbwsBuilderPackager);
            }
        }
        builder.start(args);
    }

    public void start(String[] args) throws WSDLException {
        
        if (args.length == 4 && BUILDER_FILE_PATH.equalsIgnoreCase(args[0]) &&
            STAGE_DIR.equalsIgnoreCase(args[2])) {
            String toolsFile = args[1];
            String stageDirname = args[3];
            File dbwsToolsFile = new File(toolsFile);
            if (dbwsToolsFile.exists() && dbwsToolsFile.isFile()) {
                File stageDir = new File(stageDirname);
                if (stageDir.exists() && stageDir.isDirectory()) {
                    if (packager == null) {
                        setPackager(new DBWSBuilderExpandedWARPackager()); 
                    }
                    packager.setStageDir(stageDir);
                    packager.setMode(javaseMode());
                    XMLContext context = new XMLContext(new DBWSBuilderModelProject());
                    XMLUnmarshaller unmarshaller = context.createUnmarshaller();
                    DBWSBuilderModel model = (DBWSBuilderModel)unmarshaller.unmarshal(dbwsToolsFile);
                    properties = model.properties;
                    operations = model.operations;
                    if (operations.size() == 0) {
                        logMessage(SEVERE, "No operations specified");
                        return;
                    }
                    packager.setHasAttachments(hasAttachments());
                    OutputStream dbwsSchemaStream = null;
                    try {
                        dbwsSchemaStream = packager.getSchemaStream();
                    }
                    catch (FileNotFoundException fnfe) {
                        logMessage(SEVERE, "DBWSBuilder unable to create " + DBWS_SCHEMA_XML, fnfe);
                        return;
                    }
                    OutputStream dbwsSessionsStream = null;
                    try {
                        dbwsSessionsStream = packager.getSessionsStream(getSessionsFileName());
                    }
                    catch (FileNotFoundException fnfe) {
                        logMessage(SEVERE, "DBWSBuilder unable to create " + DBWS_SESSIONS_XML, fnfe);
                        return;
                    };
                    OutputStream dbwsServiceStream = null;
                    try {
                        dbwsServiceStream = packager.getServiceStream();
                    }
                    catch (FileNotFoundException fnfe) {
                        logMessage(SEVERE, "DBWSBuilder unable to create " + DBWS_SERVICE_XML, fnfe);
                        return;
                    };
                    OutputStream dbwsOrStream = null;
                    try {
                        dbwsOrStream = packager.getOrStream();
                    }
                    catch (FileNotFoundException fnfe) {
                        logMessage(SEVERE, "DBWSBuilder unable to create " + DBWS_OR_XML, fnfe);
                        return;
                    };
                    OutputStream dbwsOxStream = null;
                    try {
                        dbwsOxStream = packager.getOxStream();
                    }
                    catch (FileNotFoundException fnfe) {
                        logMessage(SEVERE, "DBWSBuilder unable to create " + DBWS_OX_XML, fnfe);
                        return;
                    };
                    OutputStream wsdlStream = null;
                    try {
                        wsdlStream = packager.getWSDLStream();
                    }
                    catch (FileNotFoundException fnfe) {
                        logMessage(SEVERE, "DBWSBuilder unable to create " + DBWS_WSDL, fnfe);
                        return;
                    };
                    OutputStream swarefStream = null;
                    try {
                        swarefStream = packager.getSWARefStream();
                    }
                    catch (FileNotFoundException fnfe) {
                        logMessage(SEVERE, "DBWSBuilder unable to create " + SWAREF_FILENAME, fnfe);
                        return;
                    };
                    OutputStream webXmlStream = null;
                    try {
                        webXmlStream = packager.getWebXmlStream();
                    }
                    catch (FileNotFoundException fnfe) {
                        logMessage(SEVERE, "DBWSBuilder unable to create " + WEB_XML_FILENAME, fnfe);
                        return;
                    };
                    OutputStream webservicesXmlStream = null;
                    try {
                        webservicesXmlStream = packager.getWebservicesXmlStream();
                    }
                    catch (FileNotFoundException fnfe) {
                        logMessage(SEVERE,
                            "DBWSBuilder unable to create " + WEBSERVICES_FILENAME, fnfe);
                        return;
                    };
                    OutputStream oracleWebservicesXmlStream = null;
                    try {
                        oracleWebservicesXmlStream = packager.getOracleWebservicesXmlStream();
                    }
                    catch (FileNotFoundException fnfe) {
                        logMessage(SEVERE,
                            "DBWSBuilder unable to create " + ORACLE_WEBSERVICES_FILENAME, fnfe);
                        return;
                    };
                    OutputStream codeGenProviderStream = null;
                    try {
                        codeGenProviderStream = packager.getCodeGenProviderStream();
                    }
                    catch (FileNotFoundException fnfe) {
                        logMessage(SEVERE,
                            "DBWSBuilder unable to create " + DBWS_PROVIDER_CLASS_FILE, fnfe);
                        return;
                    };
                    OutputStream sourceProviderStream = null;
                    try {
                        sourceProviderStream = packager.getSourceProviderStream();
                    }
                    catch (FileNotFoundException fnfe) {
                        logMessage(SEVERE,
                            "DBWSBuilder unable to create " + DBWS_PROVIDER_SOURCE_FILE, fnfe);
                        return;
                    };
                    build(dbwsSchemaStream, dbwsSessionsStream, dbwsServiceStream, dbwsOrStream,
                        dbwsOxStream, swarefStream, webXmlStream, webservicesXmlStream,
                        oracleWebservicesXmlStream, wsdlStream, codeGenProviderStream, 
                        sourceProviderStream, logger);
                }
                else {
                    logMessage(SEVERE, "DBWSBuilder unable to locate stage directory " + stageDirname);
                    return;
                }
            }
            else {
                logMessage(SEVERE, "DBWSBuilder unable to locate dbws-builder.xml file " + toolsFile);
                return;
            }
        }
        else {
            logMessage(SEVERE,
                "DBWSBuilder requires " + BUILDER_FILE_PATH + " {path_to_dbws-builder.xml_file} " +
                    STAGE_DIR + " {path_to_stage_directory}.");
            return;
        }
    }
    
    public void build(OutputStream dbwsSchemaStream, OutputStream dbwsSessionsStream,
        OutputStream dbwsServiceStream, OutputStream dbwsOrStream, OutputStream dbwsOxStream,
        OutputStream swarefStream, OutputStream webXmlStream, OutputStream webservicesXmlStream,
        OutputStream oracleWebservicesXmlStream, OutputStream wsdlStream,
        OutputStream codeGenProviderStream, OutputStream sourceProviderStream, Logger logger)
        throws WSDLException {

        this.logger = logger; // in case some other tool wishes to use a java.util.logger
        // misc setup 
        xrServiceModel.setName(getProjectName());
        String sessionsFileName = getSessionsFileName();
        if (sessionsFileName != null && sessionsFileName.length() > 0) {
            xrServiceModel.setSessionsFile(sessionsFileName);
        }
        buildDbArtifacts();
        buildOROXProjects(); // don't write out projects yet; buildDBWSModel may add additional mappings
        // don't write out schema yet; buildDBWSModel/buildWSDL may add additional schema elements
        buildSchema();
        buildSessionsXML(dbwsSessionsStream);
        buildDBWSModel(dbwsServiceStream);
        writeAttachmentSchema(swarefStream);
        writeWebXML(webXmlStream);
        buildWSDL(wsdlStream);
        writeWebservicesXML(webservicesXmlStream);
        writeOracleWebservicesXML(oracleWebservicesXmlStream);
        writeDBWSProviderClass(codeGenProviderStream);
        writeDBWSProviderSource(sourceProviderStream);
        writeSchema(dbwsSchemaStream); // now write out schema
        writeOROXProjects(dbwsOrStream, dbwsOxStream);
    }

    protected void buildDbArtifacts() {
        
        // do Table operations first
        boolean isOracle = getDatabasePlatform() instanceof OraclePlatform;
        for (OperationModel operation : operations) {
            if (operation.isTableOperation()) {
                TableOperationModel tableModel = (TableOperationModel)operation;
                String catalogPattern =
                    isOracle ? null : tableModel.getCatalogPattern();
                String schemaPattern = tableModel.getSchemaPattern();
                String tableNamePattern = tableModel.getTablePattern();
                List<DbTable> tables = checkTables(loadTables(catalogPattern,
                    schemaPattern, tableNamePattern));
                if (tables.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("No matching tables for pattern ");
                    if (catalogPattern != null) {
                        sb.append(catalogPattern);
                        sb.append(".");
                    }
                    if (schemaPattern != null) {
                        sb.append(schemaPattern);
                        sb.append(".");
                    }
                    sb.append(tableNamePattern);
                    logMessage(FINEST, sb.toString());
                }
                else {
                    dbTables.addAll(tables);
                }
            }
        }

        // next do StoredProcedure operations
        for (OperationModel operation : operations) {
            if (operation.isProcedureOperation()) {
                ProcedureOperationModel procedureModel = (ProcedureOperationModel)operation;
                String catalogPattern = procedureModel.getCatalogPattern();
                String schemaPattern = procedureModel.getSchemaPattern();
                String procedurePattern = procedureModel.getProcedurePattern();
                List<DbStoredProcedure> procs = loadProcedures(catalogPattern,
                    schemaPattern, procedurePattern, procedureModel.overload);
                if (procs.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("No matching procedures for pattern ");
                    if (catalogPattern != null) {
                        sb.append(catalogPattern);
                        sb.append(".");
                    }
                    if (schemaPattern != null) {
                        sb.append(schemaPattern);
                        sb.append(".");
                    }
                    sb.append(procedurePattern);
                    logMessage(FINEST, sb.toString());
                }
                else {
                    dbStoredProcedures.addAll(procs);
                }
            }
        }
    }

    protected List<DbTable> loadTables(String catalogPattern, String schemaPattern,
        String tableNamePattern) {
        return checkTables(JDBCHelper.buildDbTable(getConnection(),
            databasePlatform, catalogPattern, schemaPattern, tableNamePattern));
    }

    public List<DbTable> checkTables(List<DbTable> dbTables) {
        List<DbTable> supportedTables = new ArrayList<DbTable>(dbTables.size());
        for (DbTable dbTable : dbTables) {
            boolean unSupportedColumnType = false;
            for (DbColumn dbColumn : dbTable.getColumns()) {
                switch (dbColumn.getJDBCType()) {
                    case Types.ARRAY :
                    case Types.STRUCT :
                    case Types.OTHER :
                    case Types.DATALINK :
                    case Types.JAVA_OBJECT :
                        unSupportedColumnType = true;
                        break;
                }
            }
            if (!unSupportedColumnType) {
                supportedTables.add(dbTable);
            }
        }
        return supportedTables;
    }

    public void addDbTable(DbTable dbTable) {
        dbTables.add(dbTable);
    }

    protected List<DbStoredProcedure> loadProcedures (String catalogPattern, String schemaPattern,
        String procedurePattern, int overload) {
        return checkStoredProcedures(
            JDBCHelper.buildStoredProcedure(getConnection(), databasePlatform, catalogPattern,
            schemaPattern, procedurePattern), overload);
    }

    public List<DbStoredProcedure> checkStoredProcedures(List<DbStoredProcedure> procedures,
        int oracleOverload) {

        List<DbStoredProcedure> supportedProcedures =
            new ArrayList<DbStoredProcedure>(procedures.size());

        List<DbStoredProcedure> copyOfProcedures =
            new ArrayList<DbStoredProcedure>(procedures.size());

        List<DbStoredProcedure> overloadedProcedure =
            new ArrayList<DbStoredProcedure>(1);
        if (oracleOverload == 0) {
            copyOfProcedures.addAll(procedures);
        }
        else {
            // For Oracle, storedProcedures can be overloaded - we are looking for
            // a specific version
            for (DbStoredProcedure storedProcedure : procedures) {
                if (storedProcedure.getOverload() == oracleOverload) {
                    overloadedProcedure.add(storedProcedure);
                    break;
                }
            }
            copyOfProcedures.addAll(overloadedProcedure);
        }
        for (DbStoredProcedure storedProcedure : copyOfProcedures) {
            boolean unSupportedArgType = false;
            for (DbStoredArgument arg : storedProcedure.getArguments()) {
                int jdbcType = arg.getJdbcType();
                if (jdbcType == OTHER) {
                    // For Oracle, the only way to get anything 'out' of a Stored Procedure
                    // is via a CURSOR - so this type of 'OTHER' is allowed
                    if (arg.getJdbcTypeName().contains("CURSOR") && arg.getInOut() == OUT) {
                        continue;
                    }
                    else {
                        unSupportedArgType = true;
                        break;
                    }
                }
                else if (jdbcType == ARRAY ||
                         jdbcType == STRUCT ||
                         jdbcType == DATALINK ||
                         jdbcType == JAVA_OBJECT) {
                        unSupportedArgType = true;
                        break;
                }
            }
            if (!unSupportedArgType) {
                supportedProcedures.add(storedProcedure);
            }
        }
        return supportedProcedures;
    }


    public void addDbStoredProcedure(DbStoredProcedure dbStoredProcedure) {
        dbStoredProcedures.add(dbStoredProcedure);
    }

    public void addSqlOperation(SQLOperationModel sqlOperation) {
        operations.add(sqlOperation);
    }

    protected void buildOROXProjects() {
        String projectName = getProjectName();
        if (dbTables.isEmpty()) {
            logMessage(FINEST, "No tables specified");
            orProject = new Project();
            orProject.setName(projectName + "-" + DBWS_OR_LABEL);
            oxProject = new SimpleXMLFormatProject();
            oxProject.setName(projectName + "-" + DBWS_OX_LABEL);
        }
        else {
            logMessage(FINE, "Building TopLink OR Project " + DBWS_OR_XML);
            orProject = new Project();
            orProject.setName(projectName + "-" + DBWS_OR_LABEL);
            logMessage(FINE, "Building TopLink OX Project " + DBWS_OX_XML);
            oxProject = new Project();
            oxProject.setName(projectName + "-" + DBWS_OX_LABEL);
            for (DbTable dbTable : dbTables) {
                RelationalDescriptor desc = new RelationalDescriptor();
                orProject.addDescriptor(desc);
                String tableName = dbTable.getName();
                String tablename_lc = dbTable.getName().toLowerCase();
                desc.addTableName(tableName);
                desc.setAlias(tablename_lc);
                String generatedJavaClassName = getGeneratedJavaClassName(tableName);
                desc.setJavaClassName(generatedJavaClassName);
                desc.useWeakIdentityMap();
                XMLDescriptor xdesc = new XMLDescriptor();
                oxProject.addDescriptor(xdesc);
                xdesc.setJavaClassName(generatedJavaClassName);
                xdesc.setAlias(tablename_lc);
                NamespaceResolver nr = new NamespaceResolver();
                nr.put("xsi", W3C_XML_SCHEMA_INSTANCE_NS_URI);
                nr.put("xsd", W3C_XML_SCHEMA_NS_URI);
                nr.put(TARGET_NAMESPACE_PREFIX, getTargetNamespace());
                xdesc.setNamespaceResolver(nr);
                xdesc.setDefaultRootElement(TARGET_NAMESPACE_PREFIX + ":" + tablename_lc);
                for (DbColumn dbColumn : dbTable.getColumns()) {
                    String columnName = dbColumn.getName();
                    int jdbcType = dbColumn.getJDBCType();
                    String dmdTypeName = dbColumn.getJDBCTypeName();
                    logMessage(FINE, "Building mappings for " + tableName + "." + columnName);
                    XMLDirectMapping xdm = null;
                    QName qName = getXMLTypeFromJDBCType(jdbcType);
                    // figure out if binary attachments are required
                    boolean isSwaRef = false;
                    if (qName == BASE_64_BINARY_QNAME) {
                        for (OperationModel om : operations) {
                            if (om.isTableOperation()) {
                                TableOperationModel tom = (TableOperationModel)om;
                                if (tom.additionalOperations.size() > 0) {
                                    for (OperationModel om2 : tom.additionalOperations) {
                                        if (om2.isProcedureOperation()) {
                                            ProcedureOperationModel pom = (ProcedureOperationModel)om2;
                                            if (pom.getBinaryAttachment()) {
                                                // only need one operation to require attachments
                                                isSwaRef = true;
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (isSwaRef) {
                            xdm = new XMLBinaryDataMapping();
                            XMLBinaryDataMapping xbdm = (XMLBinaryDataMapping)xdm;
                            xbdm.setSwaRef(isSwaRef);
                            xbdm.setMimeType(DEFAULT_ATTACHMENT_MIMETYPE);
                        }
                        else {
                            xdm = new XMLDirectMapping();
                            SerializedObjectConverter converter = new SerializedObjectConverter(xdm);
                            xdm.setConverter(converter);
                        }
                    }
                    else {
                        xdm = new XMLDirectMapping();
                    }
                    DirectToFieldMapping dtfm = new DirectToFieldMapping();
                    Class<?> attributeClass = getClassFromJDBCType(dmdTypeName, databasePlatform);
                    if (qName == BASE_64_BINARY_QNAME && attributeClass == ABYTE) {
                        // switch to primitive byte[] from Byte[]
                        attributeClass = APBYTE;
                    }
                    dtfm.setAttributeClassificationName(attributeClass.getName());
                    String fieldName = columnName.toLowerCase();
                    dtfm.setAttributeName(fieldName);
                    DatabaseField databaseField = new DatabaseField(columnName, tableName);
                    databaseField.setSqlType(jdbcType);
                    dtfm.setField(databaseField);
                    desc.addMapping(dtfm);
                    if (dbColumn.isPK()) {
                        desc.addPrimaryKeyField(databaseField);
                    }
                    xdm.setAttributeName(fieldName);
                    String xPath = TARGET_NAMESPACE_PREFIX + ":" + fieldName;
                    if (!isSwaRef) {
                        xPath += "/text()";
                    }
                    xdm.setXPath(xPath);
                    XMLField xmlField = (XMLField)xdm.getField();
                    xmlField.setSchemaType(qName);
                    if (!isSwaRef && qName == BASE_64_BINARY_QNAME) {
                        xmlField.setIsTypedTextField(true);
                        xmlField.addConversion(BASE_64_BINARY_QNAME, APBYTE);
                    }
                    xdesc.addMapping(xdm);
              }
              ReadObjectQuery roq = new ReadObjectQuery();
              roq.setReferenceClassName(generatedJavaClassName);
              Expression expression = null;
              Expression builder = new ExpressionBuilder();
              Expression subExp1;
              Expression subExp2;
              Expression subExpression;
              List<DatabaseField> primaryKeyFields = desc.getPrimaryKeyFields();
              for (int index = 0; index < primaryKeyFields.size(); index++) {
                  DatabaseField primaryKeyField = primaryKeyFields.get(index);
                  subExp1 = builder.getField(primaryKeyField);
                  subExp2 = builder.getParameter(primaryKeyField.getName().toLowerCase());
                  subExpression = subExp1.equal(subExp2);
                  if (expression == null) {
                      expression = subExpression;
                  }
                  else {
                      expression = expression.and(subExpression);
                  }
                  roq.addArgument(primaryKeyField.getName().toLowerCase());
              }
              roq.setSelectionCriteria(expression);
              desc.getQueryManager().addQuery(PK_QUERYNAME, roq);
              ReadAllQuery raq = new ReadAllQuery();
              roq.setReferenceClassName(generatedJavaClassName);
              desc.getQueryManager().addQuery(FINDALL_QUERYNAME, raq);
          }
          DatabaseLogin databaseLogin = new DatabaseLogin();
          databaseLogin.removeProperty("user");
          databaseLogin.removeProperty("password");
          databaseLogin.setDriverClassName(null);
          databaseLogin.setConnectionString(null);
          orProject.setLogin(databaseLogin);
          XMLLogin xmlLogin = new XMLLogin();
          xmlLogin.setDatasourcePlatform(new SAXPlatform());
          xmlLogin.getProperties().remove("user");
          xmlLogin.getProperties().remove("password");
          oxProject.setLogin(xmlLogin);
        }
    }

    protected void writeOROXProjects(OutputStream dbwsOrStream, OutputStream dbwsOxStream) {
        if (dbTables.size() > 0 && dbwsOrStream != null) {
            XMLProjectWriter.write(orProject, new OutputStreamWriter(dbwsOrStream));
        }
        if (!(oxProject instanceof SimpleXMLFormatProject) && dbwsOxStream != null) {
            XMLProjectWriter.write(oxProject, new OutputStreamWriter(dbwsOxStream));
        }
        packager.closeOrStream(dbwsOrStream);
        packager.closeOxStream(dbwsOxStream);
    }

    @SuppressWarnings("unchecked")
    protected void writeDBWSProviderClass(OutputStream codeGenProviderStream) {
        if (codeGenProviderStream != null) {
            logMessage(FINEST, "writing " + DBWS_PROVIDER_CLASS_FILE);
         
            ClassWriter cw = new ClassWriter(true);
            CodeVisitor cv;
    
            cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, DBWS_PROVIDER_PACKAGE + "/" + DBWS_PROVIDER_NAME, 
                ASMIFIED_DBWS_PROVIDER_HELPER, new String[]{ASMIFIED_JAX_WS_PROVIDER}, null);
            cw.visitInnerClass(ASMIFIED_JAX_WS_SERVICE + "$Mode", ASMIFIED_JAX_WS_SERVICE,
                "Mode", ACC_PUBLIC + ACC_FINAL + ACC_STATIC + ACC_ENUM);
    
            cv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            cv.visitVarInsn(ALOAD, 0);
            cv.visitMethodInsn(INVOKESPECIAL, ASMIFIED_DBWS_PROVIDER_HELPER, "<init>", "()V");
            cv.visitInsn(RETURN);
            cv.visitMaxs(0, 0);
           
            // METHOD ATTRIBUTES
            RuntimeVisibleAnnotations methodAttrs0 = new RuntimeVisibleAnnotations();
            Annotation methodAttrs1ann0 = new Annotation("L" + ASMIFIED_JSR_250_POSTCONSTRUCT + ";");
            methodAttrs0.annotations.add(methodAttrs1ann0);
           
            cv = cw.visitMethod(ACC_PUBLIC, "init", "()V", null, methodAttrs0);
            cv.visitVarInsn(ALOAD, 0);
            cv.visitMethodInsn(INVOKESPECIAL, ASMIFIED_DBWS_PROVIDER_HELPER,
                "init", "()V");
            cv.visitInsn(RETURN);
            cv.visitMaxs(0, 0);
           
            cv = cw.visitMethod(ACC_PUBLIC, "invoke",
                "(L" + ASMIFIED_SOAP_MESSAGE + ";)L" + ASMIFIED_SOAP_MESSAGE + ";", null, null);
            cv.visitVarInsn(ALOAD, 0);
            cv.visitVarInsn(ALOAD, 1);
            cv.visitMethodInsn(INVOKESPECIAL, ASMIFIED_DBWS_PROVIDER_HELPER,
                "invoke", "(L" + ASMIFIED_SOAP_MESSAGE + ";)L" + ASMIFIED_SOAP_MESSAGE + ";");
            cv.visitInsn(ARETURN);
            cv.visitMaxs(0, 0);
            
            // METHOD ATTRIBUTES
            RuntimeVisibleAnnotations methodAttrs1 = new RuntimeVisibleAnnotations();
            Annotation methodAttrs1ann1 = new Annotation("L" + ASMIFIED_JSR_250_PREDESTROY + ";");
            methodAttrs1.annotations.add(methodAttrs1ann1);
            
            cv = cw.visitMethod(ACC_PUBLIC, "destroy", "()V", null, methodAttrs1);
            cv.visitVarInsn(ALOAD, 0);
            cv.visitMethodInsn(INVOKESPECIAL, ASMIFIED_DBWS_PROVIDER_HELPER,
                "destroy", "()V");
            cv.visitInsn(RETURN);
            cv.visitMaxs(0, 0);
            
            // synthetic
            cv = cw.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "invoke", 
                "(Ljava/lang/Object;)Ljava/lang/Object;", null, null);
            cv.visitVarInsn(ALOAD, 0);
            cv.visitVarInsn(ALOAD, 1);
            cv.visitTypeInsn(CHECKCAST, ASMIFIED_SOAP_MESSAGE); 
            cv.visitMethodInsn(INVOKEVIRTUAL, DBWS_PROVIDER_PACKAGE + "/" + DBWS_PROVIDER_NAME,
                "invoke", "(L" + ASMIFIED_SOAP_MESSAGE + ";)L" + ASMIFIED_SOAP_MESSAGE + ";");
            cv.visitInsn(ARETURN);
            cv.visitMaxs(0, 0);
            
            // CLASS ATRIBUTE
            SignatureAttribute signatureAttr = new SignatureAttribute(
                "L" + ASMIFIED_DBWS_PROVIDER_HELPER + ";L" + ASMIFIED_JAX_WS_PROVIDER + 
                "<L" + ASMIFIED_SOAP_MESSAGE + ";>;");
            cw.visitAttribute(signatureAttr);
            
            // CLASS ATRIBUTE
            RuntimeVisibleAnnotations classAttr = new RuntimeVisibleAnnotations();
            Annotation attrann0 = new Annotation("L" + ASMIFIED_JAX_WS_WEB_SERVICE_PROVIDER + ";");
            attrann0.add("wsdlLocation", WEB_INF_PATHS[1] + WSDL_DIR + "/" + DBWS_WSDL);
            attrann0.add("serviceName", wsdlGenerator.serviceName);
            attrann0.add("portName", wsdlGenerator.serviceName);
            attrann0.add("targetNamespace", getTargetNamespace());
            classAttr.annotations.add(attrann0);
            Annotation attrann1 = new Annotation("L" + ASMIFIED_JAX_WS_SERVICE_MODE + ";");
            attrann1.add("value", new Annotation.EnumConstValue(
                "L" + ASMIFIED_JAX_WS_SERVICE + "$Mode;", "MESSAGE"));
            classAttr.annotations.add(attrann1);
            cw.visitAttribute(classAttr);
            cv.visitMaxs(0, 0);
    
            cw.visitEnd();
            
            byte[] bytes = cw.toByteArray();
            try {
                codeGenProviderStream.write(bytes, 0, bytes.length);
            }
            catch (IOException e) {/* ignore */}
            packager.closeCodeGenProviderStream(codeGenProviderStream);
        }
    }

    protected void writeDBWSProviderSource(OutputStream sourceProviderStream) {
        if (sourceProviderStream != null) {
            logMessage(FINEST, "writing " + DBWS_PROVIDER_SOURCE_FILE);
            StringBuilder sb = new StringBuilder(DBWS_PROVIDER_SOURCE_PREAMBLE);
            sb.append(wsdlGenerator.serviceName);
            sb.append(DBWS_PROVIDER_SOURCE_PORT_NAME);
            sb.append(wsdlGenerator.serviceName);
            sb.append(DBWS_PROVIDER_SOURCE_TARGET_NAMESPACE);
            sb.append(getTargetNamespace());
            sb.append(DBWS_PROVIDER_SOURCE_SUFFIX);
            OutputStreamWriter osw = 
                new OutputStreamWriter(new BufferedOutputStream(sourceProviderStream));
            try {
                osw.write(sb.toString());
                osw.flush();
            }
            catch (IOException e) {/* ignore */}
            packager.closeSourceProviderStream(sourceProviderStream);
        }
    }

    @SuppressWarnings("unchecked")
    protected void buildSchema() {
        schema.setName(getProjectName());
        schema.setTargetNamespace(getTargetNamespace());
        ns.put(TARGET_NAMESPACE_PREFIX, getTargetNamespace());
        schema.setDefaultNamespace(getTargetNamespace());
        schema.setElementFormDefault(true);
        for (DbTable dbTable : dbTables) {
            ClassDescriptor desc = null;
            Set<Map.Entry<Class, ClassDescriptor>> orDescriptorSet =
                orProject.getDescriptors().entrySet();
            for (Map.Entry<Class, ClassDescriptor> me : orDescriptorSet) {
                desc = me.getValue();
                if (desc.getAlias().equalsIgnoreCase(dbTable.getName())) {
                    break;
                }
            }
            String tablename_lc = desc.getAlias();
            ComplexType complexType = new ComplexType();
            complexType.setName(tablename_lc + "Type");
            Sequence sequence = new Sequence();
            Iterator j = desc.getMappings().iterator();
            while (j.hasNext()) {
                DirectToFieldMapping mapping = (DirectToFieldMapping)j.next();
                Element element = new Element();
                DatabaseField field = mapping.getField();
                element.setName(field.getName().toLowerCase());
                if (desc.getPrimaryKeyFields().contains(field)) {
                    element.setMinOccurs("1");
                }
                else {
                    element.setMinOccurs("0");
                }
                for (DbColumn dbColumn : dbTable.getColumns()) {
                    if (dbColumn.getName().equals(field.getName())) {
                        StringBuilder sb = new StringBuilder();
                        int jdbcType = dbColumn.getJDBCType();
                        QName qName = getXMLTypeFromJDBCType(jdbcType);
                        if (qName == BASE_64_BINARY_QNAME) {
                            Set<Map.Entry<Class, ClassDescriptor>> oxDescriptorSet =
                                oxProject.getDescriptors().entrySet();
                            for (Map.Entry<Class, ClassDescriptor> me : oxDescriptorSet) {
                                ClassDescriptor oxDesc = me.getValue();
                                if (oxDesc.getAlias().equals(tablename_lc)) {
                                    DatabaseMapping dm =
                                        oxDesc.getMappingForAttributeName(mapping.getAttributeName());
                                    if (dm instanceof XMLBinaryDataMapping) {
                                        XMLBinaryDataMapping xbdm = (XMLBinaryDataMapping)dm;
                                        if (xbdm.isSwaRef()) {
                                            sb.append(WSI_SWAREF_PREFIX + ":" + WSI_SWAREF);
                                            schema.getNamespaceResolver().put(WSI_SWAREF_PREFIX,
                                                WSI_SWAREF_URI);
                                        }
                                        else {
                                            sb.append(BASE_64_BINARY_QNAME.toString());
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                        else {
                            if (qName.getNamespaceURI().equalsIgnoreCase(W3C_XML_SCHEMA_NS_URI)) {
                                sb.append("xsd:");
                            }
                            sb.append(qName.getLocalPart());
                        }
                        element.setType(sb.toString());
                        sequence.addElement(element);
                        break;
                    }
                }
            }
            complexType.setSequence(sequence);
            schema.addTopLevelComplexTypes(complexType);
            Element wrapperElement = new Element();
            wrapperElement.setName(tablename_lc);
            wrapperElement.setType(tablename_lc + "Type");
            schema.addTopLevelElement(wrapperElement);
        }
    }

    protected void writeSchema(OutputStream dbwsSchemaStream) {
        if (dbwsSchemaStream != null) {
            SchemaModelProject schemaProject = new SchemaModelProject();
            boolean hasSwaRef = schema.getNamespaceResolver().resolveNamespacePrefix(WSI_SWAREF_PREFIX) != null;
            if (hasSwaRef) {
                XMLDescriptor descriptor = (XMLDescriptor)schemaProject.getClassDescriptor(Schema.class);
                descriptor.getNamespaceResolver().put(WSI_SWAREF_PREFIX, WSI_SWAREF_URI);
            }
            XMLContext context = new XMLContext(schemaProject);
            XMLMarshaller marshaller = context.createMarshaller();
            marshaller.marshal(schema, dbwsSchemaStream);
            packager.closeSchemaStream(dbwsSchemaStream);
        }
    }

    protected void buildSessionsXML(OutputStream dbwsSessionsStream) {
        if (dbwsSessionsStream != null) {
            logMessage(FINEST, "Building " + getSessionsFileName());
        SessionConfigs ts = new SessionConfigs();
        ts.setVersion(Version.getVersion());
        DatabaseSessionConfig orSessionConfig = null;
        String dataSource = getDataSource();
        if (dataSource != null) {
            orSessionConfig = new ServerSessionConfig();
        }
        else {
            orSessionConfig = new DatabaseSessionConfig();
        }
        String projectName = getProjectName();
        orSessionConfig.setName(projectName + "-" + DBWS_OR_SESSION_NAME_SUFFIX);

        ProjectConfig orProjectConfig = buildORProjectConfig();
        orSessionConfig.setPrimaryProject(orProjectConfig);
            String orSessionCustomizerClassName = getOrSessionCustomizerClassName();
            if (orSessionCustomizerClassName != null && !"".equals(orSessionCustomizerClassName)) {
                orSessionConfig.setSessionCustomizerClass(orSessionCustomizerClassName);
        }

        DatabaseLoginConfig dlc = new DatabaseLoginConfig();
        dlc.setBindAllParameters(true);
        dlc.setJdbcBatchWriting(true);
        if (dataSource != null) {
            ServerPlatformConfig spc = null; // new Oc4j_11_1_1_PlatformConfig();
            spc.setEnableJTA(true);
            spc.setEnableRuntimeServices(true);
            orSessionConfig.setServerPlatformConfig(spc);
            dlc.setExternalConnectionPooling(true);
            dlc.setExternalTransactionController(true);
            dlc.setDatasource(dataSource);
        }
        else {
            dlc.setConnectionURL(getUrl());
            dlc.setDriverClass(getDriver());
            dlc.setUsername(getUsername());
            dlc.setEncryptedPassword(getPassword());
        }
        dlc.setPlatformClass(getPlatformClassname());
        orSessionConfig.setLoginConfig(dlc);
        DefaultSessionLogConfig orLogConfig = new DefaultSessionLogConfig();
        orLogConfig.setLogLevel(getLogLevel());
        orSessionConfig.setLogConfig(orLogConfig);
        ts.addSessionConfig(orSessionConfig);

        DatabaseSessionConfig oxSessionConfig = new DatabaseSessionConfig();
        oxSessionConfig.setName(projectName + "-" + DBWS_OX_SESSION_NAME_SUFFIX);
        ProjectConfig oxProjectConfig = buildOXProjectConfig();
        oxSessionConfig.setPrimaryProject(oxProjectConfig);
        DefaultSessionLogConfig oxLogConfig = new DefaultSessionLogConfig();
        oxLogConfig.setLogLevel("off");
        oxSessionConfig.setLogConfig(oxLogConfig);
            String oxSessionCustomizerClassName = getOxSessionCustomizerClassName();
            if (oxSessionCustomizerClassName != null && !"".equals(oxSessionCustomizerClassName)) {
                oxSessionConfig.setSessionCustomizerClass(oxSessionCustomizerClassName);
            }
            ts.addSessionConfig(oxSessionConfig);
            XMLSessionConfigWriter.write(ts, new OutputStreamWriter(dbwsSessionsStream));
            packager.closeSessionsStream(dbwsSessionsStream);
        }
    }

    @SuppressWarnings("unchecked")
    protected void buildDBWSModel(OutputStream dbwsServiceStream) {
        
        if (dbwsServiceStream != null) {
            for (Iterator i = orProject.getOrderedDescriptors().iterator(); i.hasNext();) {
                ClassDescriptor desc = (ClassDescriptor)i.next();
                String tablename_lc = desc.getAlias();
                QueryOperation findByPKQueryOperation = new QueryOperation();
                findByPKQueryOperation.setName(PK_QUERYNAME + "_" + tablename_lc);
                NamedQueryHandler nqh1 = new NamedQueryHandler();
                nqh1.setName(PK_QUERYNAME);
                nqh1.setDescriptor(tablename_lc);
                Result result = new Result();
                QName theInstanceType = new QName(getTargetNamespace(), tablename_lc + "Type",
                    TARGET_NAMESPACE_PREFIX);
                result.setType(theInstanceType);
            findByPKQueryOperation.setResult(result);
            findByPKQueryOperation.setQueryHandler(nqh1);
            for (Iterator j = desc.getPrimaryKeyFields().iterator(); j.hasNext();) {
                DatabaseField field = (DatabaseField)j.next();
                Parameter p = new Parameter();
                p.setName(field.getName().toLowerCase());
                p.setType(getXMLTypeFromJDBCType(field.getSqlType()));
                findByPKQueryOperation.getParameters().add(p);
            }
            xrServiceModel.getOperations().put(findByPKQueryOperation.getName(), findByPKQueryOperation);
            QueryOperation findAllOperation = new QueryOperation();
            findAllOperation.setName(FINDALL_QUERYNAME + "_" + tablename_lc);
            NamedQueryHandler nqh2 = new NamedQueryHandler();
            nqh2.setName(FINDALL_QUERYNAME);
            nqh2.setDescriptor(tablename_lc);
            Result result2 = new CollectionResult();
            result2.setType(theInstanceType);
            findAllOperation.setResult(result2);
            findAllOperation.setQueryHandler(nqh2);
            xrServiceModel.getOperations().put(findAllOperation.getName(), findAllOperation);
            InsertOperation insertOperation = new InsertOperation();
            insertOperation.setName(CREATE_OPERATION_NAME + "_" + tablename_lc);
            Parameter theInstance = new Parameter();
            theInstance.setName(THE_INSTANCE_NAME);
            theInstance.setType(theInstanceType);
            insertOperation.getParameters().add(theInstance);
            xrServiceModel.getOperations().put(insertOperation.getName(), insertOperation);
            UpdateOperation updateOperation = new UpdateOperation();
            updateOperation.setName(UPDATE_OPERATION_NAME + "_" + tablename_lc);
            updateOperation.getParameters().add(theInstance);
            xrServiceModel.getOperations().put(updateOperation.getName(), updateOperation);
            DeleteOperation deleteOperation = new DeleteOperation();
            deleteOperation.setName(REMOVE_OPERATION_NAME + "_" + tablename_lc);
            deleteOperation.getParameters().add(theInstance);
            xrServiceModel.getOperations().put(deleteOperation.getName(), deleteOperation);
        }
        // check for additional operations
        for (OperationModel operation : operations) {
            if (operation.isTableOperation()) {
                TableOperationModel tableModel = (TableOperationModel)operation;
                if (tableModel.additionalOperations.size() > 0) {
                    for (OperationModel additionalOperation : tableModel.additionalOperations) {
                        additionalOperation.buildOperation(this);
                    }
                }
            }
            else { // handle non-nested <sql> and <procedure> operations
                operation.buildOperation(this);
            }
        }
            DBWSModelProject modelProject = new DBWSModelProject();
            modelProject.ns.put(TARGET_NAMESPACE_PREFIX, getTargetNamespace());
            XMLContext context = new XMLContext(modelProject);
            XMLMarshaller marshaller = context.createMarshaller();
            marshaller.marshal(xrServiceModel, dbwsServiceStream);
            packager.closeServiceStream(dbwsServiceStream);
        }
    }

    protected void writeAttachmentSchema(OutputStream swarefStream) {
        if (swarefStream != null) {
            logMessage(FINEST, "writing " + WSI_SWAREF_XSD_FILE);
            OutputStreamWriter osw = new OutputStreamWriter(new BufferedOutputStream(swarefStream));
            try {
                osw.write(DBWSBuilder.WSI_SWAREF_XSD);
                osw.flush();
            }
            catch (IOException e) {/* ignore */}
            packager.closeSWARefStream(swarefStream);
        }
    }

    protected void writeWebXML(OutputStream webXmlStream) {
        if (webXmlStream != null) {
            logMessage(FINEST, "writing web.xml");
            StringBuilder sb = new StringBuilder(DBWSBuilder.WEB_XML_PREAMBLE);
            sb.append(getContextRoot());
            sb.append(WEB_XML_POSTSCRIPT);
            OutputStreamWriter osw = new OutputStreamWriter(new BufferedOutputStream(webXmlStream));
            try {
                osw.write(sb.toString());
                osw.flush();
            }
            catch (IOException e) {/* ignore */}
            packager.closeWebXmlStream(webXmlStream);
        }
    }
    
    protected void writeWebservicesXML(OutputStream webservicesXmlStream) {
        if (webservicesXmlStream != null) {
            logMessage(FINEST, "writing " + WEBSERVICES_FILENAME);
            StringBuilder sb = new StringBuilder(WEBSERVICES_PREAMBLE);
            sb.append(wsdlGenerator.serviceName);
            sb.append(WEBSERVICES_PORT_COMPONENT_NAME);
            sb.append(wsdlGenerator.serviceName + "." + wsdlGenerator.serviceName);
            sb.append(WEBSERVICES_WSDL_SERVICE);
            sb.append(wsdlGenerator.serviceNameSpace);
            sb.append("\">ns0:");
            sb.append(wsdlGenerator.serviceName);
            sb.append(WEBSERVICES_WSDL_PORT);
            sb.append(wsdlGenerator.serviceNameSpace);
            sb.append("\">ns1:");
            sb.append(wsdlGenerator.serviceName);
            sb.append(WEBSERVICES_SUFFIX);
            OutputStreamWriter osw = new OutputStreamWriter(new BufferedOutputStream(webservicesXmlStream));
            try {
                osw.write(sb.toString());
                osw.flush();
            }
            catch (IOException e) {/* ignore */}
            packager.closeOracleWebservicesXmlStream(webservicesXmlStream);
        }
    }

    protected void writeOracleWebservicesXML(OutputStream oracleWebservicesXmlStream) {
        if (oracleWebservicesXmlStream != null) {
            logMessage(FINEST, "writing " + ORACLE_WEBSERVICES_FILENAME);
            StringBuilder sb = new StringBuilder(ORACLE_WEBSERVICES_PREAMBLE);
            sb.append(wsdlGenerator.serviceName);
            sb.append(ORACLE_WEBSERVICES_PORT_COMPONENT_NAME);
            sb.append(wsdlGenerator.serviceName + "." + wsdlGenerator.serviceName);
            sb.append(ORACLE_WEBSERVICES_SUFFIX);
            OutputStreamWriter osw = new OutputStreamWriter(new BufferedOutputStream(oracleWebservicesXmlStream));
            try {
                osw.write(sb.toString());
                osw.flush();
            }
            catch (IOException e) {/* ignore */}
            packager.closeOracleWebservicesXmlStream(oracleWebservicesXmlStream);
        }
    }

    protected void buildWSDL(OutputStream wsdlStream) throws WSDLException {
        if (wsdlStream != null) {
            logMessage(FINEST, "building " + DBWS_WSDL);
            wsdlGenerator = new WSDLGenerator(xrServiceModel, getWsdlLocationURI(), 
              packager.hasAttachments(), getTargetNamespace(), wsdlStream);
            wsdlGenerator.generateWSDL();
            packager.closeWSDLStream(wsdlStream);
        }
    }
    
    @SuppressWarnings("unchecked")
    protected Connection getConnection() {
        Connection conn = null;
        String driverClassName = getDriver();
        try {
            @SuppressWarnings("unused")
            Class driverClass = null;
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                driverClass = (Class)AccessController.doPrivileged(
                    new PrivilegedClassForName(driverClassName));
            }
            else {
                driverClass = PrivilegedAccessHelper.getClassForName(driverClassName);
            }
            conn = DriverManager.getConnection(getUrl(), getUsername(), getPassword());
        }
        catch (Exception e) {
            logMessage(SEVERE, "cannot load JDBC driver " + driverClassName);
        }
        return conn;
    }

    protected ProjectConfig buildORProjectConfig() {
        ProjectConfig orProjectConfig = null;
        if (dbTables.size() > 0) {
            orProjectConfig = new ProjectXMLConfig();
            orProjectConfig.setProjectString(META_INF_PATHS[0] + DBWS_OR_XML); // META_INF_PATHS[0] is the upper-case version
        }
        else {
            orProjectConfig = new ProjectClassConfig();
            orProjectConfig.setProjectString(Project.class.getName());
        }
        return orProjectConfig;
    }

    protected ProjectConfig buildOXProjectConfig() {
        ProjectConfig oxProjectConfig = null;
        if (dbTables.size() > 0) {
            oxProjectConfig = new ProjectXMLConfig();
            oxProjectConfig.setProjectString(META_INF_PATHS[0] + DBWS_OX_XML);
        }
        else {
            oxProjectConfig = new ProjectClassConfig();
            oxProjectConfig.setProjectString(SimpleXMLFormatProject.class.getName());
        }
        return oxProjectConfig;
    }

    protected String getGeneratedJavaClassName(String tableName) {
        String first = tableName.substring(0, 1).toUpperCase();
        String rest = tableName.toLowerCase().substring(1);
        return getProjectName().toLowerCase() + "." + first + rest;
    }

    protected boolean hasAttachments() {
        for (Operation op : xrServiceModel.getOperationsList()) {
            if (op instanceof QueryOperation) {
                if (((QueryOperation)op).isAttachment()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public DBWSBuilderPackager getPackager() {
        return packager;
    }
    public void setPackager(DBWSBuilderPackager packager) {
        this.packager = packager;
    }

    public boolean javaseMode() {
        String s = properties.get(JAVASE_MODE_KEY);
        if (s == null || !s.equalsIgnoreCase("true")) {
            return false;
        }
        return true;
    }

    public String getDriver() {
        return properties.get(DRIVER_KEY);
    }
    public void setDriver(String driver) {
        properties.put(DRIVER_KEY, driver);
    }

    public String getUsername() {
        return properties.get(USERNAME_KEY);
    }
    public void setUsername(String username) {
        properties.put(USERNAME_KEY, username);
    }

    public String getPassword() {
        return properties.get(PASSWORD_KEY);
    }

    public void setPassword(String password) {
        properties.put(PASSWORD_KEY, password);
    }

    public String getUrl() {
        return properties.get(URL_KEY);
    }

    public void setUrl(String url) {
        properties.put(URL_KEY, url);
    }

    public String getProjectName() {
        return properties.get(PROJNAME_KEY);
    }

    public void setProjectName(String projectName) {
        properties.put(PROJNAME_KEY, projectName);
    }

    public String getContextRoot() {
        String contextRoot = properties.get(CONTEXT_ROOT_KEY);
        if (contextRoot == null) {
            contextRoot = "/" + getProjectName();
            setContextRoot(contextRoot);
        }
        return contextRoot;
    }

    public void setContextRoot(String contextRoot) {
        properties.put(CONTEXT_ROOT_KEY, contextRoot);
    }

    public String getDataSource() {
        return properties.get(DATASOURCE_KEY);
    }

    public void setDataSource(String dataSource) {
        properties.put(DATASOURCE_KEY, dataSource);
    }

    public String getSessionsFileName() {
        String sessionsFileName = properties.get(SESSIONS_FILENAME_KEY);
        if (sessionsFileName == null || sessionsFileName.length() == 0) {
            sessionsFileName = DBWS_SESSIONS_XML;
            setSessionsFileName(sessionsFileName);
        }
        return sessionsFileName;
    }

    public void setSessionsFileName(String sessionsFileName) {
        properties.put(SESSIONS_FILENAME_KEY, sessionsFileName);
    }

    public String getPlatformClassname() {
        String platformClassname = properties.get(PLATFORM_CLASSNAME_KEY);
        if (platformClassname == null || platformClassname.length() == 0) {
            platformClassname = DEFAULT_PLATFORM_CLASSNAME;
            setPlatformClassname(platformClassname);
        }
        return platformClassname;

    }
    public void setPlatformClassname(String platformClassname) {
        properties.put(PLATFORM_CLASSNAME_KEY, platformClassname);

    }
    @SuppressWarnings("unchecked")
    public DatabasePlatform getDatabasePlatform() {
        String platformClassname = getPlatformClassname();
        try {
            Class platformClass = null;
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                platformClass = (Class)AccessController.doPrivileged(
                    new PrivilegedClassForName(platformClassname));
            }
            else {
                platformClass =
                    PrivilegedAccessHelper.getClassForName(platformClassname);
            }
            databasePlatform = (DatabasePlatform)Helper.getInstanceFromClass(platformClass);
        }
        catch (Exception e) {
            databasePlatform = new OraclePlatform();
        }
        return databasePlatform;
    }
    public void setDatabasePlatform(DatabasePlatform databasePlatform) {
        this.databasePlatform = databasePlatform;
    }

    public Project getOrProject() {
        return orProject;
    }

    public Project getOxProject() {
        return oxProject;
    }

    public Schema getSchema() {
        return schema;
    }

    public XRServiceModel getXrServiceModel() {
        return xrServiceModel;
    }

    public String getOrSessionCustomizerClassName() {
        return properties.get(ORSESSION_CUSTOMIZER_KEY);
    }

    public void setOrSessionCustomizerClassName(String sessionCustomizerClassName) {
        properties.put(ORSESSION_CUSTOMIZER_KEY, sessionCustomizerClassName);
    }

    public String getOxSessionCustomizerClassName() {
        return properties.get(OXSESSION_CUSTOMIZER_KEY);
    }

    public void setOXSessionCustomizerClassName(String sessionCustomizerClassName) {
        properties.put(OXSESSION_CUSTOMIZER_KEY, sessionCustomizerClassName);
    }

    public String getWsdlLocationURI() {
        String wsdlLocationURI = properties.get(WSDL_URI_KEY);
        if (wsdlLocationURI == null || wsdlLocationURI.length() == 0) {
            wsdlLocationURI = DEFAULT_WSDL_LOCATION_URI;
        }
        return wsdlLocationURI;
    }

    public void setWsdlLocationURI(String wsdlLocationURI) {
        properties.put(WSDL_URI_KEY, wsdlLocationURI);
    }

    public String getLogLevel() {
        return properties.get(LOG_LEVEL_KEY);
    }

    public void setLogLevel(String logLevel) {
        properties.put(LOG_LEVEL_KEY, logLevel);
    }

    public String getTargetNamespace() {
        String targetNamespace = properties.get(TARGET_NAMESPACE_KEY);
        if (targetNamespace == null) {
            targetNamespace = "urn:" + getProjectName();
            setTargetNamespace(targetNamespace);
        }
        return targetNamespace;
    }

    public void setTargetNamespace(String targetNamespace) {
        properties.put(TARGET_NAMESPACE_KEY, targetNamespace);
    }

    protected void logMessage(Level level, String message) {
        if (logger != null) {
            logger.log(level, message);
        }
        else if (!quiet) {
            System.out.println(message);
        }
    }

    protected void logMessage(Level severe, String message, Exception e) {
        if (logger != null) {
            logger.log(severe, message, e);
        }
        else {
            PrintWriter pw = new PrintWriter(System.out);
            e.printStackTrace(pw);
            System.out.println(message);
        }
    }

}
