/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Mike Norman - May 01 2008, creating DBWS tools package
 ******************************************************************************/

package org.eclipse.persistence.tools.dbws;

//javase imports
import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.AccessController;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.logging.Level.SEVERE;

//java eXtension imports
import javax.wsdl.WSDLException;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

//EclipseLink imports
import org.eclipse.persistence.dbws.DBWSModel;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.oxm.schema.model.Schema;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.internal.sessions.factories.model.project.ProjectClassConfig;
import org.eclipse.persistence.internal.sessions.factories.model.project.ProjectConfig;
import org.eclipse.persistence.internal.sessions.factories.model.project.ProjectXMLConfig;
import org.eclipse.persistence.internal.xr.Operation;
import org.eclipse.persistence.internal.xr.QueryOperation;
import org.eclipse.persistence.internal.xr.XRServiceModel;
import org.eclipse.persistence.internal.xr.sxf.SimpleXMLFormatProject;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.platform.database.MySQLPlatform;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.tools.dbws.DBWSPackager.ArchiveUse;
import org.eclipse.persistence.tools.dbws.jdbc.JDBCHelper;
import org.eclipse.persistence.tools.dbws.oracle.OracleHelper;
import static org.eclipse.persistence.internal.xr.Util.DBWS_OR_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_OX_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_SCHEMA_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_SERVICE_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_SESSIONS_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_WSDL;
import static org.eclipse.persistence.tools.dbws.DBWSPackager.ArchiveUse.archive;
import static org.eclipse.persistence.tools.dbws.DBWSPackager.ArchiveUse.ignore;
import static org.eclipse.persistence.tools.dbws.DBWSPackager.ArchiveUse.noArchive;
import static org.eclipse.persistence.tools.dbws.Util.DBWS_PROVIDER_CLASS_FILE;
import static org.eclipse.persistence.tools.dbws.Util.DBWS_PROVIDER_SOURCE_FILE;
import static org.eclipse.persistence.tools.dbws.Util.DEFAULT_PLATFORM_CLASSNAME;
import static org.eclipse.persistence.tools.dbws.Util.DEFAULT_WSDL_LOCATION_URI;
import static org.eclipse.persistence.tools.dbws.Util.PROVIDER_LISTENER_CLASS_FILE;
import static org.eclipse.persistence.tools.dbws.Util.PROVIDER_LISTENER_SOURCE_FILE;
import static org.eclipse.persistence.tools.dbws.Util.SWAREF_FILENAME;
import static org.eclipse.persistence.tools.dbws.Util.WEB_XML_FILENAME;
import static org.eclipse.persistence.tools.dbws.XRPackager.__nullStream;

public class DBWSBuilder extends DBWSBuilderModel {
    public static final String BUILDER_FILE_PATH = "-builderFile";
    public static final String BUILDER_PACKAGING = "-packageAs";
    public static final String STAGE_DIR = "-stageDir";
    public static final String DRIVER_KEY = "driver";
    public static final String USERNAME_KEY= "username";
    public static final String PASSWORD_KEY = "password";
    public static final String URL_KEY = "url";
    public static final String PROJNAME_KEY = "projectName";
    public static final String CONTEXT_ROOT_KEY = "contextRoot";
    public static final String DATASOURCE_KEY = "dataSource";
    public static final String SESSIONS_FILENAME_KEY = "sessionsFileName";
    public static final String NO_SESSIONS_FILENAME = "no-sessions-fileName";
    public static final String PLATFORM_CLASSNAME_KEY = "platformClassname";
    public static final String ORSESSION_CUSTOMIZER_KEY = "orSessionCustomizerClassName";
    public static final String OXSESSION_CUSTOMIZER_KEY = "oxSessionCustomizerClassName";
    public static final String WSDL_URI_KEY = "wsdlLocationURI";
    public static final String LOG_LEVEL_KEY = "logLevel";
    public static final String TARGET_NAMESPACE_KEY = "targetNamespace";
    public static final String USE_SOAP12_KEY = "useSOAP12";

    public static Map<String,DBWSPackager> PACKAGERS = new HashMap<String,DBWSPackager>();
    static {
        ServiceLoader<DBWSPackager> packagers = ServiceLoader.load(DBWSPackager.class);
        for (DBWSPackager packager : packagers) {
            PACKAGERS.put(packager.getPackagerLabel(), packager);
        }
    }
    protected DBWSPackager packager;
    protected Logger logger;
    public boolean quiet = false;
    protected String destDir;
    protected DatabasePlatform databasePlatform;
    protected Connection conn;
    protected Project orProject;
    protected Project oxProject;
    protected WSDLGenerator wsdlGenerator = null;
    protected Schema schema = new Schema();
    protected NamespaceResolver ns = schema.getNamespaceResolver();
    protected XRServiceModel xrServiceModel = new DBWSModel();
    protected DBWSBuilderHelper builderHelper = null;
    protected NamingConventionTransformer topTransformer;
    protected Set<String> typeDDL = new HashSet<String>();
    protected Set<String> typeDropDDL = new HashSet<String>();
    public List<String> requireCRUDOperations = new ArrayList<String>();

    public DBWSBuilder() {
        super();
        ns.put("xsi", W3C_XML_SCHEMA_INSTANCE_NS_URI);
        ns.put("xsd", W3C_XML_SCHEMA_NS_URI);
    }

    public static void main(String[] args) throws WSDLException {
        DBWSBuilder builder = new DBWSBuilder();
        builder.start(args);
    }

    public void start(String[] args) throws WSDLException {

        if (args.length > 5 && BUILDER_FILE_PATH.equals(args[0]) &&
            STAGE_DIR.equals(args[2]) &&
            args[4].startsWith(BUILDER_PACKAGING)) {
            String builderFilename = args[1];
            String stageDirname = args[3];
            String packagerTag = args[5];
            String archiverTag = null;
            ArchiveUse archiveUse = ignore;
            int cIdx = args[4].indexOf(':');
            if (cIdx == 10) {
                archiverTag = args[4].substring(cIdx+1);
                if (archive.name().equals(archiverTag)) {
                    archiveUse = archive;
                }
                else if (noArchive.name().equals(archiverTag)) {
                    archiveUse = noArchive;
                }
            }
            String[] additionalArgs = null;
            if (args.length > 6) {
                additionalArgs = new String[args.length - 6];
                System.arraycopy(args, 6, additionalArgs, 0, args.length - 6);
            }
            File builderFile = new File(builderFilename);
            if (builderFile.exists() && builderFile.isFile()) {
                File stageDir = new File(stageDirname);
                if (stageDir.exists() && stageDir.isDirectory()) {
                    XMLContext context = new XMLContext(new DBWSBuilderModelProject());
                    XMLUnmarshaller unmarshaller = context.createUnmarshaller();
                    DBWSBuilderModel model = (DBWSBuilderModel)unmarshaller.unmarshal(builderFile);
                    properties = model.properties;
                    operations = model.operations;
                    if (operations.size() == 0) {
                        logMessage(SEVERE, "No operations specified");
                        return;
                    }
                    packager = PACKAGERS.get(packagerTag);
                    if (packager != null) {
                        packager.setDBWSBuilder(this);
                        packager.setArchiveUse(archiveUse);
                        packager.setAdditionalArgs(additionalArgs);
                        packager.setStageDir(stageDir);
                        packager.setSessionsFileName(getSessionsFileName());
                        start();
                        return;
                    }
                }
                else {
                    logMessage(SEVERE, "DBWSBuilder unable to locate stage directory " +
                        stageDirname);
                    return;
                }
            }
            else {
                logMessage(SEVERE, "DBWSBuilder unable to locate dbws-builder.xml file " +
                    builderFilename);
                return;
            }
        }
/*
prompt> java -cp eclipselink.jar:eclipselink-dbwsutils.jar:your_favourite_jdbc_driver.jar org.eclipse.persistence.tools.dbws.DBWSBuilder -builderFile {path_to_dbws_builder.xml_file} -stageDir {path_to_staging_directory} -packageAs {how_to_package_output} [additionalArgs]
 */
        StringBuilder sb = new StringBuilder(30);
        sb.append("DBWSBuilder usage ([] indicates optional argument):\nprompt> java -cp eclipselink.jar:eclipselink-dbwsutils.jar:your_favourite_jdbc_driver.jar \\\n\t");
        sb.append(this.getClass().getName());
        sb.append(" ");
        sb.append(BUILDER_FILE_PATH);
        sb.append(" {path_to_dbwsbuilder.xml} \\\n\t");
        sb.append(STAGE_DIR);
        sb.append(" ");
        sb.append(" {path_to_stageDir}");
        sb.append(" ");
        sb.append(BUILDER_PACKAGING);
        sb.append("[:archive_flag - archive, noArchive, ignore] {packager} [additional arguments]\nAvailable packagers:\n\t");
        for (Iterator<Map.Entry<String, DBWSPackager>> i = PACKAGERS.entrySet().iterator(); i.hasNext();) {
            Map.Entry<String, DBWSPackager> me = i.next();
            sb.append(me.getValue().getUsage());
            if (i.hasNext()) {
                sb.append("\n\t");
            }
        }
        logMessage(SEVERE, sb.toString());
        return;
    }

    public void start() throws WSDLException {
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
        OutputStream classProviderStream = null;
        try {
            classProviderStream = packager.getProviderClassStream();
        }
        catch (FileNotFoundException fnfe) {
            logMessage(SEVERE,
                "DBWSBuilder unable to create " + DBWS_PROVIDER_CLASS_FILE, fnfe);
            return;
        };
        OutputStream sourceProviderStream = null;
        try {
            sourceProviderStream = packager.getProviderSourceStream();
        }
        catch (FileNotFoundException fnfe) {
            logMessage(SEVERE,
                "DBWSBuilder unable to create " + DBWS_PROVIDER_SOURCE_FILE, fnfe);
            return;
        };
        OutputStream classProviderListenerStream = null;
        try {
            classProviderListenerStream = packager.getProviderListenerClassStream();
        }
        catch (FileNotFoundException fnfe) {
            logMessage(SEVERE,
                "DBWSBuilder unable to create " + PROVIDER_LISTENER_CLASS_FILE, fnfe);
            return;
        };
        OutputStream sourceProviderListenerStream = null;
        try {
            sourceProviderListenerStream = packager.getProviderListenerSourceStream();
        }
        catch (FileNotFoundException fnfe) {
            logMessage(SEVERE,
                "DBWSBuilder unable to create " + PROVIDER_LISTENER_SOURCE_FILE, fnfe);
            return;
        };
        build(dbwsSchemaStream, dbwsSessionsStream, dbwsServiceStream, dbwsOrStream,
            dbwsOxStream, swarefStream, webXmlStream, wsdlStream, classProviderStream,
            sourceProviderStream, classProviderListenerStream, sourceProviderListenerStream,
            logger);
    }

    public void build(OutputStream dbwsSchemaStream, OutputStream dbwsSessionsStream,
        OutputStream dbwsServiceStream, OutputStream dbwsOrStream, OutputStream dbwsOxStream,
        OutputStream swarefStream, OutputStream webXmlStream, OutputStream wsdlStream,
        OutputStream classProviderStream, OutputStream sourceProviderStream,
        OutputStream classProviderListenerStream, OutputStream sourceProviderListenerStream,
        Logger logger)
        throws WSDLException {

        this.logger = logger; // in case some other tool wishes to use a java.util.logger
        // misc setup
        xrServiceModel.setName(getProjectName());
        String sessionsFileName = getSessionsFileName();
        if (sessionsFileName != null && sessionsFileName.length() > 0) {
            xrServiceModel.setSessionsFile(sessionsFileName);
        }
        //has someone manually set a custom NamingConventionTransformer?
        if (topTransformer == null) {
            // setup the NamingConventionTransformers
            ServiceLoader<NamingConventionTransformer> transformers = ServiceLoader.load(NamingConventionTransformer.class);
            Iterator<NamingConventionTransformer> transformerIter = transformers.iterator();
            topTransformer = transformerIter.next();
            LinkedList<NamingConventionTransformer> transformerList = new LinkedList<NamingConventionTransformer>();
            // check for user-provided transformer in front of default transformers
            if (!((DefaultNamingConventionTransformer)topTransformer).isDefaultTransformer()) {
                // ditch all default transformers, except for the last one - SQLX2003Transformer
                for (; transformerIter.hasNext(); ) {
                    NamingConventionTransformer nextTransformer = transformerIter.next();
                    if (!((DefaultNamingConventionTransformer)nextTransformer).isDefaultTransformer()) {
                        transformerList.addLast(nextTransformer);
                    }
                    else if (nextTransformer instanceof SQLX2003Transformer) {
                        transformerList.addLast(nextTransformer);
                    }
                }
            }
            else {
                // assume usual configuration: ToLowerTransformer -> TypeSuffixTransformer -> SQLX2003Transformer
                for (; transformerIter.hasNext(); ) {
                    transformerList.addLast(transformerIter.next());
                }
            }
            // hook up the chain-of-responsibility
            NamingConventionTransformer nextTransformer = topTransformer;
            for (Iterator<NamingConventionTransformer> i = transformerList.iterator(); i.hasNext();) {
                NamingConventionTransformer nct = i.next();
                ((DefaultNamingConventionTransformer)nextTransformer).setNextTransformer(nct);
                nextTransformer = nct;
            }
        }
        packager.start();
        DBWSBuilderHelper helper = getBuilderHelper();
        helper.buildDbArtifacts();
        helper.buildOROXProjects(topTransformer);  // don't write out projects yet; buildDBWSModel may add additional mappings
        // don't write out schema yet; buildDBWSModel/buildWSDL may add additional schema elements
        helper.buildSchema(topTransformer);
        helper.buildSessionsXML(dbwsSessionsStream);
        packager.setHasAttachments(hasAttachments());
        helper.buildDBWSModel(topTransformer, dbwsServiceStream);
        helper.writeAttachmentSchema(swarefStream);
        helper.buildWSDL(wsdlStream, topTransformer);
        helper.writeWebXML(webXmlStream);
        helper.generateDBWSProvider(sourceProviderStream, classProviderStream, sourceProviderListenerStream,
            classProviderListenerStream);
        helper.writeSchema(dbwsSchemaStream); // now write out schema
        helper.writeOROXProjects(dbwsOrStream, dbwsOxStream);
        packager.end();
    }

    public OutputStream getShadowDDLStream() {
        return __nullStream;
    }

    public void addSqlOperation(SQLOperationModel sqlOperation) {
        operations.add(sqlOperation);
    }

    protected ProjectConfig buildORProjectConfig() {
        ProjectConfig orProjectConfig = null;
        boolean useProjectXML = false;
        if (builderHelper.hasTables() || hasBuildSqlOperations()) {
            useProjectXML = true;
        }
        else if (builderHelper.hasComplexProcedureArgs()) {
            useProjectXML = true;
        }
        if (!useProjectXML) {
            // check for any named queries - SimpleXMLFormatProject's sometimes need them
            if (orProject.getQueries().size() > 0) {
                useProjectXML = true;
            }
            // check for ObjectRelationalDataTypeDescriptor's - Advanced JDBC object/varray types
            else if (orProject.getDescriptors().size() > 0) {
                Collection<ClassDescriptor> descriptors = orProject.getDescriptors().values();
                for (ClassDescriptor desc : descriptors) {
                    if (desc.isObjectRelationalDataTypeDescriptor()) {
                        useProjectXML = true;
                        break;
                    }
                }
            }
        }
        if (useProjectXML) {
            orProjectConfig = new ProjectXMLConfig();
            String pathPrefix = packager.getOrProjectPathPrefix();
            orProjectConfig.setProjectString(
                pathPrefix == null ? DBWS_OR_XML : pathPrefix + DBWS_OR_XML);
        }
        else {
            orProjectConfig = new ProjectClassConfig();
            orProjectConfig.setProjectString(Project.class.getName());
        }
        return orProjectConfig;
    }

    protected ProjectConfig buildOXProjectConfig() {
        ProjectConfig oxProjectConfig = null;
        boolean useProjectXML = false;
        if (builderHelper.hasTables() || hasBuildSqlOperations()) {
            useProjectXML = true;
        }
        else if (builderHelper.hasComplexProcedureArgs()) {
            useProjectXML = true;
        }
        if (!useProjectXML) {
            // check for any named queries - SimpleXMLFormatProject's sometimes need them
            if (orProject.getQueries().size() > 0) {
                useProjectXML = true;
            }
            // check for ObjectRelationalDataTypeDescriptor's - Advanced JDBC object/varray types
            else if (orProject.getDescriptors().size() > 0) {
                Collection<ClassDescriptor> descriptors = orProject.getDescriptors().values();
                for (ClassDescriptor desc : descriptors) {
                    if (desc.isObjectRelationalDataTypeDescriptor()) {
                        useProjectXML = true;
                        break;
                    }
                }
            }
        }
        if (useProjectXML) {
            oxProjectConfig = new ProjectXMLConfig();
            String pathPrefix = packager.getOxProjectPathPrefix();
            oxProjectConfig.setProjectString(
                pathPrefix == null ? DBWS_OX_XML : pathPrefix + DBWS_OX_XML);
        }
        else {
            oxProjectConfig = new ProjectClassConfig();
            oxProjectConfig.setProjectString(SimpleXMLFormatProject.class.getName());
        }
        return oxProjectConfig;
    }

    protected boolean hasAttachments() {
        for (Operation op : xrServiceModel.getOperationsList()) {
            if (op instanceof QueryOperation) {
                if (((QueryOperation)op).isAttachment()) {
                    return true;
                }
            }
        }
        //check if any operation is marked with binaryAttachment="true"
        for (OperationModel op : operations) {
            if (op.getBinaryAttachment()) {
                return true;
            }
            // check nested operations
            if (op.isTableOperation()) {
            	TableOperationModel top = (TableOperationModel)op;
            	if (top.additionalOperations != null) {
                	for (OperationModel addOp : top.additionalOperations) {
                		if (addOp.binaryAttachment) {
                			return true;
                		}
                	}
            	}
            }
        }
        return false;
    }

    public DBWSPackager getPackager() {
        return packager;
    }
    public void setPackager(DBWSPackager packager) {
        this.packager = packager;
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

    @SuppressWarnings({"unchecked"/*, "rawtypes"*/})
    public Connection getConnection() {
        if (conn == null ) {
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
                Properties props = new Properties();
                props.put("user", getUsername());
                props.put("password", getPassword());
                if (getPlatformClassname().contains("MySQL")) {
                    props.put("useInformationSchema", "true");
                }
                conn = DriverManager.getConnection(getUrl(), props);
            }
            catch (Exception e) {
                logMessage(SEVERE, "JDBC driver error: " + driverClassName, e);
            }
        }
        return conn;
    }
    public void setConnection(Connection conn) {
        this.conn = conn;
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
        if (NO_SESSIONS_FILENAME.equals(sessionsFileName)) {
            return null;
        }
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
    @SuppressWarnings({"unchecked"/*, "rawtypes"*/})
    public DatabasePlatform getDatabasePlatform() {
        if (databasePlatform == null) {
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
                databasePlatform = new MySQLPlatform();
            }
        }
        return databasePlatform;
    }
    public void setDatabasePlatform(DatabasePlatform databasePlatform) {
        this.databasePlatform = databasePlatform;
    }

    public Project getOrProject() {
        return orProject;
    }
    public void setOrProject(Project orProject) {
        this.orProject = orProject;
    }

    public Project getOxProject() {
        return oxProject;
    }
    public void setOxProject(Project oxProject) {
        this.oxProject = oxProject;
    }

    public Schema getSchema() {
        return schema;
    }
    public void setSchema(Schema schema) {
        this.schema = schema;
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

    public WSDLGenerator getWSDLGenerator() {
        return wsdlGenerator;
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

    public Set<String> getTypeDDL() {
        return typeDDL;
    }

    public Set<String> getTypeDropDDL() {
        return typeDropDDL;
    }

    public DBWSBuilderHelper getBuilderHelper() {
        if (builderHelper == null) {
            boolean isOracle = getDatabasePlatform().getClass().getName().contains("Oracle")
                ? true : false;
            if (isOracle) {
                builderHelper = new OracleHelper(this);
            }
            else {
                builderHelper = new JDBCHelper(this);
            }
        }
        return builderHelper;
    }
    public void setBuilderHelper(DBWSBuilderHelper builderHelper) {
        this.builderHelper = builderHelper;
    }

    public void useSOAP12() {
        properties.put(USE_SOAP12_KEY, "true");

    }
    public boolean usesSOAP12() {
        boolean useSOAP12 = false;
        String s = properties.get(USE_SOAP12_KEY);
        if (s != null) {
            useSOAP12 = s.toLowerCase().equals("true");
        }
        return useSOAP12;
    }

    public boolean mtomEnabled() {
        boolean mtomEnabled = false;

        for (OperationModel opModel : getOperations()) {
            String attachmentType = opModel.getAttachmentType();
            if ("MTOM".equalsIgnoreCase(attachmentType) || "SWAREF".equalsIgnoreCase(attachmentType)) {
                mtomEnabled = true;
                break;
            }
        }
        return mtomEnabled;
    }

    public void setTargetNamespace(String targetNamespace) {
        properties.put(TARGET_NAMESPACE_KEY, targetNamespace);
    }

    public void logMessage(Level level, String message) {
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

    public NamingConventionTransformer getTopNamingConventionTransformer() {
        return topTransformer;
    }
    public void setTopNamingConventionTransformer(NamingConventionTransformer topTransformer) {
        this.topTransformer = topTransformer;
    }

    public boolean hasBuildSqlOperations() {
        boolean flag = false;
        for (OperationModel om : operations) {
            if (om.isSQLOperation()) {
                SQLOperationModel sqlOm = (SQLOperationModel)om;
                String buildSql = sqlOm.getBuildSql();
                if (buildSql != null && buildSql.length() > 0) {
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }
}