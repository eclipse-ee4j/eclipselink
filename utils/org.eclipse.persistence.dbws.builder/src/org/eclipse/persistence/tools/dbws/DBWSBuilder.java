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
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.AccessController;
import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Struct;
import java.sql.Types;
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
import static java.sql.Types.ARRAY;
import static java.sql.Types.DATALINK;
import static java.sql.Types.JAVA_OBJECT;
import static java.sql.Types.OTHER;
import static java.sql.Types.STRUCT;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.FINEST;
import static java.util.logging.Level.SEVERE;

//Java extension imports
import javax.wsdl.WSDLException;
import javax.xml.namespace.QName;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

//EclipseLink imports
import org.eclipse.persistence.dbws.DBWSModel;
import org.eclipse.persistence.dbws.DBWSModelProject;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseType;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.oxm.schema.SchemaModelGenerator;
import org.eclipse.persistence.internal.oxm.schema.SchemaModelGeneratorProperties;
import org.eclipse.persistence.internal.oxm.schema.SchemaModelProject;
import org.eclipse.persistence.internal.oxm.schema.model.ComplexType;
import org.eclipse.persistence.internal.oxm.schema.model.Schema;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.internal.sessions.factories.MissingDescriptorListener;
import org.eclipse.persistence.internal.sessions.factories.ObjectPersistenceWorkbenchXMLProject;
import org.eclipse.persistence.internal.sessions.factories.XMLSessionConfigWriter;
import org.eclipse.persistence.internal.sessions.factories.model.SessionConfigs;
import org.eclipse.persistence.internal.sessions.factories.model.project.ProjectClassConfig;
import org.eclipse.persistence.internal.sessions.factories.model.project.ProjectConfig;
import org.eclipse.persistence.internal.sessions.factories.model.project.ProjectXMLConfig;
import org.eclipse.persistence.internal.xr.ProjectHelper;
import org.eclipse.persistence.internal.xr.XRDynamicClassLoader;
import org.eclipse.persistence.internal.xr.CollectionResult;
import org.eclipse.persistence.internal.xr.DeleteOperation;
import org.eclipse.persistence.internal.xr.InsertOperation;
import org.eclipse.persistence.internal.xr.NamedQueryHandler;
import org.eclipse.persistence.internal.xr.Operation;
import org.eclipse.persistence.internal.xr.Parameter;
import org.eclipse.persistence.internal.xr.QueryOperation;
import org.eclipse.persistence.internal.xr.Result;
import org.eclipse.persistence.internal.xr.UpdateOperation;
import org.eclipse.persistence.internal.xr.Util;
import org.eclipse.persistence.internal.xr.XRServiceModel;
import org.eclipse.persistence.internal.xr.sxf.SimpleXMLFormatProject;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.converters.SerializedObjectConverter;
import org.eclipse.persistence.mappings.structures.ObjectRelationalDataTypeDescriptor;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.XMLLogin;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.oxm.mappings.XMLBinaryDataMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.mappings.nullpolicy.AbstractNullPolicy;
import org.eclipse.persistence.oxm.platform.DOMPlatform;
import org.eclipse.persistence.oxm.schema.XMLSchemaReference;
import org.eclipse.persistence.oxm.schema.XMLSchemaURLReference;
import org.eclipse.persistence.platform.database.MySQLPlatform;
import org.eclipse.persistence.platform.database.jdbc.JDBCTypes;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLCollection;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLStoredFunctionCall;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLStoredProcedureCall;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLrecord;
import org.eclipse.persistence.platform.database.oracle.publisher.visit.PublisherListenerChainAdapter;
import org.eclipse.persistence.platform.database.oracle.publisher.visit.PublisherWalker;
import org.eclipse.persistence.queries.DataReadQuery;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.queries.ValueReadQuery;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.factories.XMLProjectReader;
import org.eclipse.persistence.sessions.factories.XMLProjectWriter;
import org.eclipse.persistence.tools.dbws.DBWSPackager.ArchiveUse;
import org.eclipse.persistence.tools.dbws.NamingConventionTransformer.ElementStyle;
import org.eclipse.persistence.tools.dbws.Util.InOut;
import org.eclipse.persistence.tools.dbws.jdbc.DbColumn;
import org.eclipse.persistence.tools.dbws.jdbc.DbStoredArgument;
import org.eclipse.persistence.tools.dbws.jdbc.DbStoredProcedure;
import org.eclipse.persistence.tools.dbws.jdbc.DbTable;
import org.eclipse.persistence.tools.dbws.jdbc.JDBCHelper;
import org.eclipse.persistence.tools.dbws.oracle.AdvancedJDBCORDescriptorBuilder;
import org.eclipse.persistence.tools.dbws.oracle.AdvancedJDBCOXDescriptorBuilder;
import org.eclipse.persistence.tools.dbws.oracle.AdvancedJDBCQueryBuilder;
import org.eclipse.persistence.tools.dbws.oracle.OracleHelper;
import org.eclipse.persistence.tools.dbws.oracle.PLSQLHelperObjectsBuilder;
import org.eclipse.persistence.tools.dbws.oracle.PLSQLORDescriptorBuilder;
import org.eclipse.persistence.tools.dbws.oracle.PLSQLOXDescriptorBuilder;
import org.eclipse.persistence.tools.dbws.oracle.PLSQLStoredArgument;
import static org.eclipse.persistence.internal.helper.ClassConstants.APBYTE;
import static org.eclipse.persistence.internal.oxm.schema.SchemaModelGeneratorProperties.ELEMENT_FORM_QUALIFIED_KEY;
import static org.eclipse.persistence.internal.xr.sxf.SimpleXMLFormat.DEFAULT_SIMPLE_XML_FORMAT_TAG;
import static org.eclipse.persistence.internal.xr.Util.DBWS_OR_LABEL;
import static org.eclipse.persistence.internal.xr.Util.DBWS_OR_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_OX_LABEL;
import static org.eclipse.persistence.internal.xr.Util.DBWS_OX_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_SCHEMA_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_SERVICE_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_SESSIONS_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_WSDL;
import static org.eclipse.persistence.internal.xr.Util.DEFAULT_ATTACHMENT_MIMETYPE;
import static org.eclipse.persistence.internal.xr.Util.PK_QUERYNAME;
import static org.eclipse.persistence.internal.xr.Util.TARGET_NAMESPACE_PREFIX;
import static org.eclipse.persistence.internal.xr.Util.getClassFromJDBCType;
import static org.eclipse.persistence.oxm.XMLConstants.BASE_64_BINARY_QNAME;
import static org.eclipse.persistence.oxm.mappings.nullpolicy.XMLNullRepresentationType.XSI_NIL;
import static org.eclipse.persistence.platform.database.oracle.publisher.Util.TOPLEVEL;
import static org.eclipse.persistence.tools.dbws.DBWSPackager.ArchiveUse.archive;
import static org.eclipse.persistence.tools.dbws.DBWSPackager.ArchiveUse.noArchive;
import static org.eclipse.persistence.tools.dbws.DBWSPackager.ArchiveUse.ignore;
import static org.eclipse.persistence.tools.dbws.NamingConventionTransformer.ElementStyle.ATTRIBUTE;
import static org.eclipse.persistence.tools.dbws.NamingConventionTransformer.ElementStyle.ELEMENT;
import static org.eclipse.persistence.tools.dbws.NamingConventionTransformer.ElementStyle.NONE;
import static org.eclipse.persistence.tools.dbws.Util.CREATE_OPERATION_NAME;
import static org.eclipse.persistence.tools.dbws.Util.DBWS_PROVIDER_CLASS_FILE;
import static org.eclipse.persistence.tools.dbws.Util.DBWS_PROVIDER_SOURCE_FILE;
import static org.eclipse.persistence.tools.dbws.Util.DEFAULT_PLATFORM_CLASSNAME;
import static org.eclipse.persistence.tools.dbws.Util.DEFAULT_WSDL_LOCATION_URI;
import static org.eclipse.persistence.tools.dbws.Util.FINDALL_QUERYNAME;
import static org.eclipse.persistence.tools.dbws.Util.PROVIDER_LISTENER_CLASS_FILE;
import static org.eclipse.persistence.tools.dbws.Util.PROVIDER_LISTENER_SOURCE_FILE;
import static org.eclipse.persistence.tools.dbws.Util.REMOVE_OPERATION_NAME;
import static org.eclipse.persistence.tools.dbws.Util.SWAREF_FILENAME;
import static org.eclipse.persistence.tools.dbws.Util.THE_INSTANCE_NAME;
import static org.eclipse.persistence.tools.dbws.Util.UPDATE_OPERATION_NAME;
import static org.eclipse.persistence.tools.dbws.Util.WEB_XML_FILENAME;
import static org.eclipse.persistence.tools.dbws.Util.WSI_SWAREF;
import static org.eclipse.persistence.tools.dbws.Util.WSI_SWAREF_PREFIX;
import static org.eclipse.persistence.tools.dbws.Util.WSI_SWAREF_URI;
import static org.eclipse.persistence.tools.dbws.Util.WSI_SWAREF_XSD_FILE;
import static org.eclipse.persistence.tools.dbws.Util.addSimpleXMLFormat;
import static org.eclipse.persistence.tools.dbws.Util.getXMLTypeFromJDBCType;
import static org.eclipse.persistence.tools.dbws.Util.InOut.IN;
import static org.eclipse.persistence.tools.dbws.Util.InOut.INOUT;
import static org.eclipse.persistence.tools.dbws.Util.InOut.OUT;
import static org.eclipse.persistence.tools.dbws.Util.escapePunctuation;
import static org.eclipse.persistence.tools.dbws.Util.sqlMatch;
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
    public static final String WSI_SWAREF_XSD =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n" +
        "<xsd:schema targetNamespace=\"" + WSI_SWAREF_URI + "\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"> \n" +
        "  <xsd:simpleType name=\"" + WSI_SWAREF + "\"> \n" +
        "    <xsd:restriction base=\"xsd:anyURI\"/> \n" +
        "  </xsd:simpleType> \n" +
        "</xsd:schema>";

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
    protected List<DbTable> dbTables = new ArrayList<DbTable>();
    protected List<DbStoredProcedure> dbStoredProcedures = new ArrayList<DbStoredProcedure>();
    protected Map<DbStoredProcedure, DbStoredProcedureNameAndModel> dbStoredProcedure2QueryName =
        new HashMap<DbStoredProcedure, DbStoredProcedureNameAndModel>();
    protected NamingConventionTransformer topTransformer;
    protected Set<String> typeDDL = new HashSet<String>();
    protected Set<String> typeDropDDL = new HashSet<String>();

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
            ServiceLoader<NamingConventionTransformer> transformers =
                ServiceLoader.load(NamingConventionTransformer.class);
            Iterator<NamingConventionTransformer> transformerIter = transformers.iterator();
            topTransformer = transformerIter.next();
            LinkedList<NamingConventionTransformer> transformerList =
                new LinkedList<NamingConventionTransformer>();
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
        buildDbArtifacts();
        buildOROXProjects(topTransformer); // don't write out projects yet; buildDBWSModel may add additional mappings
        // don't write out schema yet; buildDBWSModel/buildWSDL may add additional schema elements
        buildSchema(topTransformer);
        buildSessionsXML(dbwsSessionsStream);
        buildDBWSModel(topTransformer, dbwsServiceStream);
        packager.setHasAttachments(hasAttachments());
        writeAttachmentSchema(swarefStream);
        buildWSDL(wsdlStream, topTransformer);
        writeWebXML(webXmlStream);
        generateDBWSProvider(sourceProviderStream, classProviderStream, sourceProviderListenerStream,
            classProviderListenerStream);
        writeSchema(dbwsSchemaStream); // now write out schema
        writeOROXProjects(dbwsOrStream, dbwsOxStream);
        packager.end();
    }

    public OutputStream getShadowDDLStream() {
        return __nullStream;
    }

    public void buildDbArtifacts() {
        // do Table operations first
        boolean isOracle =
            getDatabasePlatform().getClass().getName().contains("Oracle") ? true : false;
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
                List<DbStoredProcedure> procs = loadProcedures(procedureModel, isOracle);
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
        buildDbStoredProcedure2QueryNameMap(dbStoredProcedure2QueryName, dbStoredProcedures,
            operations, isOracle);
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

    protected List<DbStoredProcedure> loadProcedures(ProcedureOperationModel procedureModel,
        boolean isOracle) {
        //bug 296114 - Problem using JDBC metadata for Oracle Stored Function in package with overloading
        //             use OracleHelper for all StoredProcedure operations, not just PL/SQL or
        //             advancedJDBC
        if (isOracle) {
            return OracleHelper.buildStoredProcedure(getConnection(), getUsername(), databasePlatform,
                procedureModel);
        }
        else {
            // use JDBC helper
            return checkStoredProcedures(JDBCHelper.buildStoredProcedure(getConnection(),
                databasePlatform, procedureModel), 0);
        }
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

    protected void buildOROXProjects(NamingConventionTransformer nct) {
        String projectName = getProjectName();
        orProject = new Project();
        orProject.setName(projectName + "-" + DBWS_OR_LABEL);
        if (dbTables.isEmpty() && !hasBuildSqlOperations()) {
            logMessage(FINEST, "No tables specified");
            oxProject = new SimpleXMLFormatProject();
        }
        else {
            oxProject = new Project();
        }
        oxProject.setName(projectName + "-" + DBWS_OX_LABEL);
        for (DbTable dbTable : dbTables) {
            String tableName = dbTable.getName();
            RelationalDescriptor desc = buildORDescriptor(tableName, nct);
            orProject.addDescriptor(desc);
            XMLDescriptor xdesc = buildOXDescriptor(tableName, nct);
            oxProject.addDescriptor(xdesc);
            for (DbColumn dbColumn : dbTable.getColumns()) {
                String columnName = dbColumn.getName();
                ElementStyle style = nct.styleForElement(columnName);
                if (style == NONE) {
                    continue;
                }
                logMessage(FINE, "Building mappings for " + tableName + "." + columnName);
                DirectToFieldMapping orFieldMapping = buildORFieldMappingFromColumn(dbColumn, desc, nct);
                desc.addMapping(orFieldMapping);
                XMLDirectMapping oxFieldMapping = buildOXFieldMappingFromColumn(dbColumn, xdesc, nct);
                xdesc.addMapping(oxFieldMapping);
                // check for switch from Byte[] to byte[]
                if (oxFieldMapping.getAttributeClassificationName() == APBYTE.getName()) {
                    orFieldMapping.setAttributeClassificationName(APBYTE.getName());
                }
          }
          ReadObjectQuery roq = new ReadObjectQuery();
          String generatedJavaClassName = getGeneratedJavaClassName(tableName);
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
          raq.setReferenceClassName(generatedJavaClassName);
          desc.getQueryManager().addQuery(FINDALL_QUERYNAME, raq);
        }
        for (OperationModel opModel : operations) {
            if (opModel.isProcedureOperation()) {
                ProcedureOperationModel procOpModel = (ProcedureOperationModel)opModel;
                if (procOpModel.getJPubType() != null) {
                    if (procOpModel.isPLSQLProcedureOperation()) {
                        buildOROXProjectsForAdvancedPLSQLProcedure(procOpModel);
                    }
                    else {
                        buildOROXProjectsForAdvancedProcedure(procOpModel, nct);
                    }
                }
            }
            if (opModel.isSQLOperation() && ((SQLOperationModel)opModel).hasBuildSql()) {
                buildOROXProjectsForSecondarySql((SQLOperationModel)opModel, nct);
            }
        }
        DatabaseLogin databaseLogin = new DatabaseLogin();
        databaseLogin.removeProperty("user");
        databaseLogin.removeProperty("password");
        databaseLogin.setDriverClassName(null);
        databaseLogin.setConnectionString(null);
        orProject.setLogin(databaseLogin);
        XMLLogin xmlLogin = new XMLLogin();
        xmlLogin.setDatasourcePlatform(new DOMPlatform());
        xmlLogin.getProperties().remove("user");
        xmlLogin.getProperties().remove("password");
        oxProject.setLogin(xmlLogin);
    }

    protected void buildOROXProjectsForSecondarySql(SQLOperationModel sqlOm,
        NamingConventionTransformer nct) {
        List<DbColumn> columns = JDBCHelper.buildDbColumns(getConnection(),
            sqlOm.getBuildSql());
        String tableName = sqlOm.getReturnType();
        // need custom NamingConventionTransformer so that returnType/tableName is
        // used verbatim
        NamingConventionTransformer extraNct = new DefaultNamingConventionTransformer() {
            @Override
            protected boolean isDefaultTransformer() {
                return false;
            }
            @Override
            public String generateSchemaAlias(String tableName) {
                return tableName;
            }
        };
        ((DefaultNamingConventionTransformer)extraNct).setNextTransformer(nct);
        RelationalDescriptor desc = buildORDescriptor(tableName, extraNct);
        desc.descriptorIsAggregate();
        orProject.addDescriptor(desc);
        XMLDescriptor xdesc = buildOXDescriptor(tableName, extraNct);
        oxProject.addDescriptor(xdesc);
        List<String> columnsAlreadyProcessed = new ArrayList<String>();
        for (DbColumn dbColumn : columns) {
            String columnName = dbColumn.getName();
            if (!columnsAlreadyProcessed.contains(columnName)) {
                columnsAlreadyProcessed.add(columnName);
                ElementStyle style = nct.styleForElement(columnName);
                if (style == NONE) {
                    continue;
                }
                logMessage(FINE, "Building mappings for " + columnName);
                DirectToFieldMapping orFieldMapping = buildORFieldMappingFromColumn(dbColumn, desc, nct);
                desc.addMapping(orFieldMapping);
                XMLDirectMapping oxFieldMapping = buildOXFieldMappingFromColumn(dbColumn, xdesc, nct);
                xdesc.addMapping(oxFieldMapping);
            }
            else {
                logMessage(SEVERE, "Duplicate ResultSet columns not supported '" + columnName + "'");
                throw new RuntimeException("Duplicate ResultSet columns not supported");
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected void buildOROXProjectsForAdvancedPLSQLProcedure(ProcedureOperationModel procOpModel) {
        /*
         * Walk-thru sqlType, building PLSQLCollection/PLSQLrecord helper objects,
         * ObjectRelationalDataTypeDescriptors and mappings, XMLDescriptors and mappings
         */
        PLSQLHelperObjectsBuilder helperObjectsBuilder = new PLSQLHelperObjectsBuilder(this, procOpModel);
        PLSQLORDescriptorBuilder orDescriptorBuilder = new PLSQLORDescriptorBuilder();
        PLSQLOXDescriptorBuilder oxDescriptorBuilder = new PLSQLOXDescriptorBuilder(getTargetNamespace());
        PublisherListenerChainAdapter chain = new PublisherListenerChainAdapter();
        chain.addListener(helperObjectsBuilder);
        chain.addListener(orDescriptorBuilder);
        chain.addListener(oxDescriptorBuilder);
        PublisherWalker walker = new PublisherWalker(chain);
        procOpModel.getJPubType().accept(walker);
        if (orDescriptorBuilder.getDescriptors() != null) {
            for (ObjectRelationalDataTypeDescriptor ordtDescriptor : orDescriptorBuilder.getDescriptors()) {
                orProject.addDescriptor(ordtDescriptor);
            }
        }
        if (oxDescriptorBuilder.getDescriptors() != null) {
            for (XMLDescriptor xdesc : oxDescriptorBuilder.getDescriptors()) {
                oxProject.addDescriptor(xdesc);
            }
        }
        if (procOpModel.hasDbStoredProcedures()) {
            for (int i=0; i< procOpModel.getDbStoredProcedures().size(); i++) {
                DbStoredProcedure storedProcedure = procOpModel.getDbStoredProcedures().get(i);
                boolean isPLSQLStoredProc = false;
                for (DbStoredArgument arg : storedProcedure.getArguments()) {
                    if (arg.isPLSQLArgument() || storedProcedure.getOverload() > 0) {
                        isPLSQLStoredProc = true;
                        break;
                    }
                }
                
                PLSQLStoredProcedureCall call;
                if (storedProcedure.isFunction()) {
                    if (procOpModel.getDbStoredFunctionReturnType() != null) {
                        call = new PLSQLStoredFunctionCall(procOpModel.getDbStoredFunctionReturnType());
                    } else {
                        call = new PLSQLStoredFunctionCall();
                    }
                } else {
                    call = new PLSQLStoredProcedureCall();
                }
                
                if (isPLSQLStoredProc) {
                    String catalogPrefix = null;
                    String cat = storedProcedure.getCatalog();
                    if (cat == null | cat.length() == 0) {
                        catalogPrefix = "";
                    }
                    else {
                        catalogPrefix = cat + ".";
                    }
                    call.setProcedureName(catalogPrefix + storedProcedure.getName());
                    DatabaseQuery dq = null;
                    DbStoredProcedureNameAndModel nameAndModel =
                        dbStoredProcedure2QueryName.get(storedProcedure);
                    String returnType = nameAndModel.procOpModel.getReturnType();
                    boolean hasResponse = returnType != null;
                    String typ = null;
                    ClassDescriptor xdesc = null;
                    if (hasResponse) {
                        int idx = 0;
                        int colonIdx = returnType.indexOf(":");
                        if (colonIdx == -1) {
                            idx = returnType.indexOf("}");
                        }
                        else {
                            idx = colonIdx;
                        }
                        if (idx > 0) {
                            typ = returnType.substring(idx+1);
                            for (XMLDescriptor xd : (List<XMLDescriptor>)(List)oxProject.getOrderedDescriptors()) {
                                if (xd.getSchemaReference() != null) {
                                    String context = xd.getSchemaReference().getSchemaContext();
                                    if (context.substring(1).equals(typ)) {
                                        xdesc = xd;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    if (hasResponse) {
                        if (nameAndModel.procOpModel.isCollection) {
                            dq = new DataReadQuery();
                        }
                        else {
                            dq = new ValueReadQuery();
                        }
                    }
                    else {
                        dq = new ValueReadQuery();
                    }
                    dq.bindAllParameters();
                    dq.setName(nameAndModel.name);
                    dq.setCall(call);

                    DatabaseType[] typesForMethod = procOpModel.getArgumentTypes().get(i);
                    for (int j=0, len=typesForMethod.length; j<len; j++) {
                        DbStoredArgument arg = storedProcedure.getArguments().get(j);
                        DatabaseType databaseType = typesForMethod[j];
                        InOut direction = arg.getInOut();
                        if (direction == OUT) {
                            call.addNamedOutputArgument(arg.getName(), databaseType);
                        }
                        else if (direction == IN) {
                            call.addNamedArgument(arg.getName(), databaseType);
                        }
                        else {
                            call.addNamedInOutputArgument(arg.getName(), databaseType);
                        }
                        if (direction == IN | direction == INOUT) {
                            if (xdesc != null) {
                                dq.addArgumentByTypeName(arg.getName(), xdesc.getJavaClassName());
                            }
                            else {
                                if (databaseType instanceof PLSQLCollection) {
                                    dq.addArgument(arg.getName(), Array.class);
                                }
                                else if (databaseType instanceof PLSQLrecord) {
                                    dq.addArgument(arg.getName(), Struct.class);
                                }
                                else {
                                    dq.addArgument(arg.getName(),
                                        JDBCTypes.getClassForCode(databaseType.getConversionCode()));
                                }
                            }
                        }
                    }
                    orProject.getQueries().add(dq);
                }
            }
        }
    }

    protected void buildOROXProjectsForAdvancedProcedure(ProcedureOperationModel procOpModel,
        NamingConventionTransformer nct) {
        // bug 271679 Advanced JDBC types (table, object, array)

        /*
         * Walk-thru sqlType, building ObjectRelationalDataTypeDescriptors (and mappings),
         * XMLDescriptors (and mappings) and DatabaseQuery's
         */
        AdvancedJDBCQueryBuilder queryBuilder =
            new AdvancedJDBCQueryBuilder(dbStoredProcedures, dbStoredProcedure2QueryName);
        AdvancedJDBCORDescriptorBuilder orDescriptorBuilder = new AdvancedJDBCORDescriptorBuilder();
        AdvancedJDBCOXDescriptorBuilder oxDescriptorBuilder =
            new AdvancedJDBCOXDescriptorBuilder(getTargetNamespace(), nct);
        PublisherListenerChainAdapter chain = new PublisherListenerChainAdapter();
        chain.addListener(queryBuilder);
        chain.addListener(orDescriptorBuilder);
        chain.addListener(oxDescriptorBuilder);
        PublisherWalker walker = new PublisherWalker(chain);
        procOpModel.getJPubType().accept(walker);
        if (orDescriptorBuilder.getDescriptors() != null) {
            for (ObjectRelationalDataTypeDescriptor ordtDescriptor : orDescriptorBuilder.getDescriptors()) {
                orProject.addDescriptor(ordtDescriptor);
            }
        }
        if (oxDescriptorBuilder.getDescriptors() != null) {
            for (XMLDescriptor xdesc : oxDescriptorBuilder.getDescriptors()) {
                oxProject.addDescriptor(xdesc);
            }
        }
        List<DatabaseQuery> newQueries = queryBuilder.getQueries();
        if (newQueries != null) {
            orProject.getQueries().addAll(newQueries);
        }
    }

    protected void writeOROXProjects(OutputStream dbwsOrStream, OutputStream dbwsOxStream) {
        boolean writeORProject = false;
        if (dbTables.size() > 0 || hasBuildSqlOperations()) {
            writeORProject = true;
        }
        else if (dbStoredProcedures.size() > 0) {
            for (DbStoredProcedure storedProcedure : dbStoredProcedures) {
                for (DbStoredArgument storedArgument : storedProcedure.getArguments()) {
                    if (storedArgument instanceof PLSQLStoredArgument) {
                        writeORProject = true;
                        break;
                    }
                }
                if (writeORProject) {
                    break;
                }
            }
            if (!writeORProject) {
                // check for any named queries - SimpleXMLFormatProject's sometimes need them
                if (orProject.getQueries().size() > 0) {
                    writeORProject = true;
                }
                // check for ObjectRelationalDataTypeDescriptor's - Advanced JDBC object/varray types
                else if (orProject.getDescriptors().size() > 0) {
                    Collection<ClassDescriptor> descriptors = orProject.getDescriptors().values();
                    for (ClassDescriptor desc : descriptors) {
                        if (desc.isObjectRelationalDataTypeDescriptor()) {
                            writeORProject = true;
                            break;
                        }
                    }
                }
            }
        }
        if (writeORProject && !isNullStream(dbwsOrStream)) {
            XMLContext context = new XMLContext(new ObjectPersistenceWorkbenchXMLProject());
            context.getSession(orProject).getEventManager().addListener(new MissingDescriptorListener());
            XMLMarshaller marshaller = context.createMarshaller();
            marshaller.marshal(orProject, new OutputStreamWriter(dbwsOrStream));
        }
        if (!isNullStream(dbwsOxStream)) {
            boolean writeOXProject = false;
            if (dbTables.size() > 0 || hasBuildSqlOperations()) {
                writeOXProject = true;
            }
            else if (dbStoredProcedures.size() > 0) {
                for (DbStoredProcedure storedProcedure : dbStoredProcedures) {
                    for (DbStoredArgument storedArgument : storedProcedure.getArguments()) {
                        if (storedArgument instanceof PLSQLStoredArgument) {
                            writeOXProject = true;
                            break;
                        }
                    }
                    if (writeOXProject) {
                        break;
                    }
                }
                if (!writeOXProject) {
                    // check for any named queries - SimpleXMLFormatProject's sometimes need them
                    if (orProject.getQueries().size() > 0) {
                        writeOXProject = true;
                    }
                    // check for ObjectRelationalDataTypeDescriptor's - Advanced JDBC object/varray types
                    else if (orProject.getDescriptors().size() > 0) {
                        Collection<ClassDescriptor> descriptors = orProject.getDescriptors().values();
                        for (ClassDescriptor desc : descriptors) {
                            if (desc.isObjectRelationalDataTypeDescriptor()) {
                                writeOXProject = true;
                                break;
                            }
                        }
                    }
                }
            }
            if (writeOXProject) {
                XMLContext context = new XMLContext(new ObjectPersistenceWorkbenchXMLProject());
                context.getSession(oxProject).getEventManager().addListener(new MissingDescriptorListener());
                XMLMarshaller marshaller = context.createMarshaller();
                marshaller.marshal(oxProject, new OutputStreamWriter(dbwsOxStream));
            }
        }
        packager.closeOrStream(dbwsOrStream);
        packager.closeOxStream(dbwsOxStream);
    }

    protected void generateDBWSProvider(OutputStream sourceProviderStream,
        OutputStream classProviderStream, OutputStream sourceProviderListenerStream,
        OutputStream classProviderListenerStream) {
        if (!isNullStream(sourceProviderStream)) {
            logMessage(FINEST, "generating " + DBWS_PROVIDER_SOURCE_FILE);
        }
        if (!isNullStream(classProviderStream)) {
            logMessage(FINEST, "generating " + DBWS_PROVIDER_CLASS_FILE);
        }
        packager.writeProvider(sourceProviderStream, classProviderStream,
            sourceProviderListenerStream, classProviderListenerStream, this);
        packager.closeProviderSourceStream(sourceProviderStream);
        packager.closeProviderClassStream(classProviderStream);
    }

    @SuppressWarnings("unchecked")
    protected void buildSchema(NamingConventionTransformer nct) {

        List<XMLDescriptor> descriptorsToProcess = new ArrayList<XMLDescriptor>();
        for (XMLDescriptor desc : (List<XMLDescriptor>)(List)oxProject.getOrderedDescriptors()) {
            String alias = desc.getAlias();
            if (!DEFAULT_SIMPLE_XML_FORMAT_TAG.equals(alias)) {
                descriptorsToProcess.add(desc);
            }
        }
        if (descriptorsToProcess.size() > 0) {
            // need a deep-copy clone of oxProject; simplest way is to marshall/unmarshall to a stream
            StringWriter sw = new StringWriter();
            XMLProjectWriter.write(oxProject, sw);
            XRDynamicClassLoader specialLoader =
                new XRDynamicClassLoader(this.getClass().getClassLoader());
            Project oxProjectClone = XMLProjectReader.read(new StringReader(sw.toString()), specialLoader);
            ProjectHelper.fixOROXAccessors(oxProjectClone, null);
            XMLLogin xmlLogin = new XMLLogin();
            DOMPlatform domPlatform = new DOMPlatform();
            domPlatform.getConversionManager().setLoader(specialLoader);
            xmlLogin.setPlatform(domPlatform);
            oxProjectClone.setLogin(xmlLogin);
            oxProjectClone.createDatabaseSession(); // initialize reference descriptors
            SchemaModelGenerator schemaGenerator = new SchemaModelGenerator(true);
            SchemaModelGeneratorProperties sgProperties = new SchemaModelGeneratorProperties();
            // set element form default to qualified for target namespace
            sgProperties.addProperty(getTargetNamespace(), ELEMENT_FORM_QUALIFIED_KEY, true);
            Map<String, Schema> schemaMap = schemaGenerator.generateSchemas(descriptorsToProcess, sgProperties);
            Schema s = schemaMap.get(getTargetNamespace());
            // check existing schema for any top-level ComplexTypes
            if (schema != null && s != null) {
                Map<String, ComplexType> topLevelComplexTypes = schema.getTopLevelComplexTypes();
                for (Map.Entry<String, ComplexType> me : topLevelComplexTypes.entrySet()) {
                    s.addTopLevelComplexTypes(me.getValue());
                }
                // add any additional namespaces
                NamespaceResolver snr = s.getNamespaceResolver();
                NamespaceResolver nr = schema.getNamespaceResolver();
                // we only want to add prefix/uri pairs for prefixes that don't already exist
                for (String prefix : nr.getPrefixesToNamespaces().keySet()) {
                	if (snr.resolveNamespacePrefix(prefix) == null) {
                		snr.put(prefix, nr.resolveNamespacePrefix(prefix));
                	}
                }
                schema = s; // switch
                schema.setNamespaceResolver(snr);
            }
        }
        else {
            addSimpleXMLFormat(schema);
            schema.setTargetNamespace(getTargetNamespace());
        }
    }

    protected void writeSchema(OutputStream dbwsSchemaStream) {
        if (!isNullStream(dbwsSchemaStream)) {
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
        if (!isNullStream(dbwsSessionsStream)) {
            logMessage(FINEST, "Building " + getSessionsFileName());
            SessionConfigs ts = packager.buildSessionsXML(dbwsSessionsStream, this);
            XMLSessionConfigWriter.write(ts, new OutputStreamWriter(dbwsSessionsStream));
            packager.closeSessionsStream(dbwsSessionsStream);
        }
    }

    @SuppressWarnings({"unchecked"/*, "rawtypes"*/})
    protected void buildDBWSModel(NamingConventionTransformer nct, OutputStream dbwsServiceStream) {

        if (!isNullStream(dbwsServiceStream)) {
            for (Iterator i = orProject.getOrderedDescriptors().iterator(); i.hasNext();) {
                ClassDescriptor desc = (ClassDescriptor)i.next();
                String tablenameAlias = desc.getAlias();
                boolean buildCRUDoperations = false;
                for (DbTable dbTable : dbTables) {
                    if (nct.generateSchemaAlias(dbTable.getName()).equals(tablenameAlias)) {
                        buildCRUDoperations = true;
                        break;
                    }
                }
                if (buildCRUDoperations) {
                    QueryOperation findByPKQueryOperation = new QueryOperation();
                    findByPKQueryOperation.setName(Util.PK_QUERYNAME + "_" + tablenameAlias);
                    findByPKQueryOperation.setUserDefined(false);
                    NamedQueryHandler nqh1 = new NamedQueryHandler();
                    nqh1.setName(Util.PK_QUERYNAME);
                    nqh1.setDescriptor(tablenameAlias);
                    Result result = new Result();
                    QName theInstanceType = new QName(getTargetNamespace(), tablenameAlias,
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
                    findAllOperation.setName(FINDALL_QUERYNAME + "_" + tablenameAlias);
                    findAllOperation.setUserDefined(false);
                    NamedQueryHandler nqh2 = new NamedQueryHandler();
                    nqh2.setName(FINDALL_QUERYNAME);
                    nqh2.setDescriptor(tablenameAlias);
                    Result result2 = new CollectionResult();
                    result2.setType(theInstanceType);
                    findAllOperation.setResult(result2);
                    findAllOperation.setQueryHandler(nqh2);
                    xrServiceModel.getOperations().put(findAllOperation.getName(), findAllOperation);
                    InsertOperation insertOperation = new InsertOperation();
                    insertOperation.setName(CREATE_OPERATION_NAME + "_" + tablenameAlias);
                    Parameter theInstance = new Parameter();
                    theInstance.setName(THE_INSTANCE_NAME);
                    theInstance.setType(theInstanceType);
                    insertOperation.getParameters().add(theInstance);
                    xrServiceModel.getOperations().put(insertOperation.getName(), insertOperation);
                    UpdateOperation updateOperation = new UpdateOperation();
                    updateOperation.setName(UPDATE_OPERATION_NAME + "_" + tablenameAlias);
                    updateOperation.getParameters().add(theInstance);
                    xrServiceModel.getOperations().put(updateOperation.getName(), updateOperation);
                    DeleteOperation deleteOperation = new DeleteOperation();
                    deleteOperation.setName(REMOVE_OPERATION_NAME + "_" + tablenameAlias);
                    deleteOperation.setDescriptorName(tablenameAlias);
                    for (Iterator j = desc.getPrimaryKeyFields().iterator(); j.hasNext();) {
                        DatabaseField field = (DatabaseField)j.next();
                        Parameter p = new Parameter();
                        p.setName(field.getName().toLowerCase());
                        p.setType(getXMLTypeFromJDBCType(field.getSqlType()));
                        deleteOperation.getParameters().add(p);
                    }
                    xrServiceModel.getOperations().put(deleteOperation.getName(), deleteOperation);
                }
            }
            // check for additional operations
            for (OperationModel operation : operations) {
                if (operation.isTableOperation()) {
                    TableOperationModel tableModel = (TableOperationModel)operation;
                    if (tableModel.additionalOperations != null &&
                        tableModel.additionalOperations.size() > 0) {
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
        if (!isNullStream(swarefStream)) {
            logMessage(FINEST, "writing " + WSI_SWAREF_XSD_FILE);
            OutputStreamWriter osw = new OutputStreamWriter(new BufferedOutputStream(swarefStream));
            try {
                osw.write(WSI_SWAREF_XSD);
                osw.flush();
            }
            catch (IOException e) {/* ignore */}
            packager.closeSWARefStream(swarefStream);
        }
    }

    protected void writeWebXML(OutputStream webXmlStream) {
        if (!isNullStream(webXmlStream)) {
            logMessage(FINEST, "writing web.xml");
            packager.writeWebXml(webXmlStream, this);
            packager.closeWebXmlStream(webXmlStream);
        }
    }

    public void buildWSDL(OutputStream wsdlStream, NamingConventionTransformer nct) throws WSDLException {
        if (!isNullStream(wsdlStream)) {
            logMessage(FINEST, "building " + DBWS_WSDL);
            wsdlGenerator = new WSDLGenerator(xrServiceModel, nct, getWsdlLocationURI(),
                packager.hasAttachments(), getTargetNamespace(), wsdlStream);
            wsdlGenerator.generateWSDL(usesSOAP12());
            packager.closeWSDLStream(wsdlStream);
        }
    }

    protected ProjectConfig buildORProjectConfig() {
        ProjectConfig orProjectConfig = null;
        boolean useProjectXML = false;
        if (dbTables.size() > 0 || hasBuildSqlOperations()) {
            useProjectXML = true;
        }
        else if (dbStoredProcedures.size() > 0) {
            for (DbStoredProcedure storedProcedure : dbStoredProcedures) {
                for (DbStoredArgument storedArgument : storedProcedure.getArguments()) {
                    if (storedArgument instanceof PLSQLStoredArgument) {
                        useProjectXML = true;
                        break;
                    }
                }
                if (useProjectXML) {
                    break;
                }
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
        if (dbTables.size() > 0 || hasBuildSqlOperations()) {
            useProjectXML = true;
        }
        else if (dbStoredProcedures.size() > 0) {
            for (DbStoredProcedure storedProcedure : dbStoredProcedures) {
                for (DbStoredArgument storedArgument : storedProcedure.getArguments()) {
                    if (storedArgument instanceof PLSQLStoredArgument) {
                        useProjectXML = true;
                        break;
                    }
                }
                if (useProjectXML) {
                    break;
                }
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

    protected boolean isNullStream(OutputStream outputStream) {
        if (outputStream == null | outputStream == __nullStream) {
            return true;
        }
        return false;
    }

    public static void buildDbStoredProcedure2QueryNameMap(
        Map<DbStoredProcedure, DbStoredProcedureNameAndModel> dbStoredProcedure2QueryName,
        List<DbStoredProcedure> dbStoredProcedures,
        ArrayList<OperationModel> operations, boolean isOracle) {
        for (OperationModel opModel : operations) {
            if (opModel.isProcedureOperation()) {
                ProcedureOperationModel procOpModel = (ProcedureOperationModel)opModel;
                // scan all the dbStoredProcedures for matches
                List<DbStoredProcedure> matches = new ArrayList<DbStoredProcedure>();
                String modelCatalogPattern =
                    escapePunctuation(procOpModel.getCatalogPattern(), isOracle);
                if (TOPLEVEL.equalsIgnoreCase(modelCatalogPattern)) {
                    modelCatalogPattern = null;
                }
                String modelSchemaPattern =
                    escapePunctuation(procOpModel.getSchemaPattern(), isOracle);
                String modelProcedureNamePattern =
                    escapePunctuation(procOpModel.getProcedurePattern(), isOracle);
                for (DbStoredProcedure storedProc : dbStoredProcedures) {
                    boolean procedureNameMatch =
                        sqlMatch(modelProcedureNamePattern, storedProc.getName());
                    if (storedProc.getCatalog() == null || modelCatalogPattern == null) {
                        if (storedProc.getSchema() == null) {
                            // solely determined by procedureName
                            if (procedureNameMatch) {
                                matches.add(storedProc);
                            }
                        }
                        // combination of schema & procedureName
                        else if (sqlMatch(modelSchemaPattern, storedProc.getSchema()) && procedureNameMatch) {
                            matches.add(storedProc);
                        }
                    }
                    else {
                        boolean catalogMatch =
                            sqlMatch(modelCatalogPattern, storedProc.getCatalog());
                        if (storedProc.getSchema() == null) {
                            // determined by catalog * procedureName
                            if (catalogMatch && procedureNameMatch) {
                                matches.add(storedProc);
                            }
                        }
                        // combination of catalog, schema & procedureName
                        else if (sqlMatch(modelSchemaPattern, storedProc.getSchema()) && catalogMatch && procedureNameMatch) {
                            matches.add(storedProc);
                        }
                    }
                }
                if (matches.size() == 1) {
                    DbStoredProcedureNameAndModel nameAndModel =
                        new DbStoredProcedureNameAndModel(procOpModel.getName(), procOpModel);
                    dbStoredProcedure2QueryName.put(matches.get(0), nameAndModel);
                }
                else {
                    for (int i = 0, len = matches.size(); i < len;) {
                        DbStoredProcedureNameAndModel nameAndModel =
                            new DbStoredProcedureNameAndModel(procOpModel.getName()+(i+1), procOpModel);
                        dbStoredProcedure2QueryName.put(matches.get(i), nameAndModel);
                        i++;
                    }
                }
            }
        }
    }
    public static class DbStoredProcedureNameAndModel {
        public String name;
        public ProcedureOperationModel procOpModel;
        DbStoredProcedureNameAndModel(String name, ProcedureOperationModel procOpModel) {
            this.name = name;
            this.procOpModel = procOpModel;
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

    public RelationalDescriptor buildORDescriptor(String tableName, NamingConventionTransformer nct) {
        RelationalDescriptor desc = new RelationalDescriptor();
        String tablenameAlias = nct.generateSchemaAlias(tableName);
        desc.addTableName(tableName);
        desc.setAlias(tablenameAlias);
        String generatedJavaClassName = getGeneratedJavaClassName(tableName);
        desc.setJavaClassName(generatedJavaClassName);
        desc.useWeakIdentityMap();
        return desc;
    }

    public XMLDescriptor buildOXDescriptor(String tableName, NamingConventionTransformer nct) {
        XMLDescriptor xdesc = new XMLDescriptor();
        String generatedJavaClassName = getGeneratedJavaClassName(tableName);
        xdesc.setJavaClassName(generatedJavaClassName);
        String tablenameAlias = nct.generateSchemaAlias(tableName);
        xdesc.setAlias(tablenameAlias);
        NamespaceResolver nr = new NamespaceResolver();
        nr.setDefaultNamespaceURI(getTargetNamespace());
        xdesc.setNamespaceResolver(nr);
        xdesc.setDefaultRootElement(tablenameAlias);
        XMLSchemaURLReference schemaReference = new XMLSchemaURLReference("");
        schemaReference.setSchemaContext("/" + tablenameAlias);
        schemaReference.setType(XMLSchemaReference.COMPLEX_TYPE);
        xdesc.setSchemaReference(schemaReference);
        return xdesc;
    }

    public DirectToFieldMapping buildORFieldMappingFromColumn(DbColumn dbColumn,
        RelationalDescriptor desc, NamingConventionTransformer nct) {
        DirectToFieldMapping dtfm = new DirectToFieldMapping();
        String columnName = dbColumn.getName();
        int jdbcType = dbColumn.getJDBCType();
        String dmdTypeName = dbColumn.getJDBCTypeName();
        Class<?> attributeClass;
        if ("CHAR".equalsIgnoreCase(dmdTypeName) && dbColumn.getPrecision() > 1) {
            attributeClass = String.class;
        }
        else {
            attributeClass= getClassFromJDBCType(dmdTypeName.toUpperCase(), databasePlatform);
        }
        dtfm.setAttributeClassificationName(attributeClass.getName());
        String fieldName = nct.generateElementAlias(columnName);
        dtfm.setAttributeName(fieldName);
        DatabaseField databaseField = new DatabaseField(columnName, desc.getTableName());
        databaseField.setSqlType(jdbcType);
        dtfm.setField(databaseField);
        if (nct.getOptimisticLockingField() != null &&
            nct.getOptimisticLockingField().equalsIgnoreCase(columnName)) {
            desc.useVersionLocking(columnName, false);
        }
        if (dbColumn.isPK()) {
            desc.addPrimaryKeyField(databaseField);
        }
        return dtfm;
    }

    public XMLDirectMapping buildOXFieldMappingFromColumn(DbColumn dbColumn, XMLDescriptor xdesc,
        NamingConventionTransformer nct) {
        XMLDirectMapping xdm = null;
        String columnName = dbColumn.getName();
        int jdbcType = dbColumn.getJDBCType();
        String dmdTypeName = dbColumn.getJDBCTypeName();
        QName qName = getXMLTypeFromJDBCType(jdbcType);
        Class<?> attributeClass;
        if ("CHAR".equalsIgnoreCase(dmdTypeName) && dbColumn.getPrecision() > 1) {
            attributeClass = String.class;
        }
        else {
            attributeClass= getClassFromJDBCType(dmdTypeName.toUpperCase(), databasePlatform);
        }
        // figure out if binary attachments are required
        boolean binaryAttach = false;
        String attachmentType = null;
        if (BASE_64_BINARY_QNAME.equals(qName)) {
            // use primitive byte[] array, not object Byte[] array
            attributeClass = APBYTE;
            for (OperationModel om : operations) {
                if (om.isTableOperation()) {
                    TableOperationModel tom = (TableOperationModel)om;
                    if (tom.getBinaryAttachment()) {
                        binaryAttach = true;
                        if ("MTOM".equalsIgnoreCase(tom.getAttachmentType())) {
                            attachmentType = "MTOM";
                        }
                        else {
                            attachmentType = "SWAREF";
                        }
                        // only need one operation to require attachments
                        break;
                    }
                    if (tom.additionalOperations.size() > 0) {
                        for (OperationModel om2 : tom.additionalOperations) {
                            if (om2.isProcedureOperation()) {
                                ProcedureOperationModel pom = (ProcedureOperationModel)om2;
                                if (pom.getBinaryAttachment()) {
                                    binaryAttach = true;
                                    if ("MTOM".equalsIgnoreCase(tom.getAttachmentType())) {
                                        attachmentType = "MTOM";
                                    }
                                    else {
                                        attachmentType = "SWAREF";
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            if (binaryAttach) {
                xdm = new XMLBinaryDataMapping();
                XMLBinaryDataMapping xbdm = (XMLBinaryDataMapping)xdm;
                if (attachmentType.equals("SWAREF")) {
                    xbdm.setSwaRef(true);
                }
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
        String fieldName = nct.generateElementAlias(columnName);
        xdm.setAttributeName(fieldName);
        xdm.setAttributeClassificationName(attributeClass.getName());
        String xPath = "";
        ElementStyle style = nct.styleForElement(columnName);
        if (style == ATTRIBUTE) {
            xPath += "@" + fieldName;
        }
        else if (style == ELEMENT){
            xPath += fieldName;
            if (!dbColumn.isPK()) {
                AbstractNullPolicy nullPolicy = xdm.getNullPolicy();
                nullPolicy.setNullRepresentedByEmptyNode(false);
                nullPolicy.setMarshalNullRepresentation(XSI_NIL);
                nullPolicy.setNullRepresentedByXsiNil(true);
                xdm.setNullPolicy(nullPolicy);
            }
        }
        if (attributeClass != APBYTE) {
            xPath += "/text()";
        }
        xdm.setXPath(xPath);
        XMLField xmlField = (XMLField)xdm.getField();
        xmlField.setSchemaType(qName);
        if (dbColumn.isPK()) {
            xmlField.setRequired(true);
        }
        return xdm;
    }
}