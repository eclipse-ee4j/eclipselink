/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
import java.security.AccessController;
import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.Vector;
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
import org.eclipse.persistence.internal.oxm.schema.SchemaModelProject;
import org.eclipse.persistence.internal.oxm.schema.model.Attribute;
import org.eclipse.persistence.internal.oxm.schema.model.ComplexType;
import org.eclipse.persistence.internal.oxm.schema.model.Element;
import org.eclipse.persistence.internal.oxm.schema.model.Schema;
import org.eclipse.persistence.internal.oxm.schema.model.Sequence;
import org.eclipse.persistence.internal.oxm.schema.model.SimpleComponent;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.internal.sessions.factories.MissingDescriptorListener;
import org.eclipse.persistence.internal.sessions.factories.ObjectPersistenceWorkbenchXMLProject;
import org.eclipse.persistence.internal.sessions.factories.XMLSessionConfigWriter;
import org.eclipse.persistence.internal.sessions.factories.model.SessionConfigs;
import org.eclipse.persistence.internal.sessions.factories.model.project.ProjectClassConfig;
import org.eclipse.persistence.internal.sessions.factories.model.project.ProjectConfig;
import org.eclipse.persistence.internal.sessions.factories.model.project.ProjectXMLConfig;
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
import org.eclipse.persistence.mappings.structures.ArrayMapping;
import org.eclipse.persistence.mappings.structures.ObjectRelationalDataTypeDescriptor;
import org.eclipse.persistence.mappings.structures.ObjectRelationalDatabaseField;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.XMLLogin;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.oxm.mappings.XMLBinaryDataMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.platform.SAXPlatform;
import org.eclipse.persistence.platform.database.MySQLPlatform;
import org.eclipse.persistence.platform.database.jdbc.JDBCTypes;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLCollection;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLStoredProcedureCall;
import org.eclipse.persistence.queries.DataModifyQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.tools.dbws.DBWSPackager.ArchiveUse;
import org.eclipse.persistence.tools.dbws.NamingConventionTransformer.ElementStyle;
import org.eclipse.persistence.tools.dbws.jdbc.DbColumn;
import org.eclipse.persistence.tools.dbws.jdbc.DbStoredArgument;
import org.eclipse.persistence.tools.dbws.jdbc.DbStoredProcedure;
import org.eclipse.persistence.tools.dbws.jdbc.DbTable;
import org.eclipse.persistence.tools.dbws.jdbc.JDBCHelper;
import org.eclipse.persistence.tools.dbws.oracle.OracleHelper;
import org.eclipse.persistence.tools.dbws.oracle.PLSQLStoredArgument;
import static org.eclipse.persistence.internal.helper.ClassConstants.ABYTE;
import static org.eclipse.persistence.internal.helper.ClassConstants.APBYTE;
import static org.eclipse.persistence.internal.xr.Util.DBWS_OR_LABEL;
import static org.eclipse.persistence.internal.xr.Util.DBWS_OR_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_OX_LABEL;
import static org.eclipse.persistence.internal.xr.Util.DBWS_OX_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_SCHEMA_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_SERVICE_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_SESSIONS_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_WSDL;
import static org.eclipse.persistence.internal.xr.Util.DEFAULT_ATTACHMENT_MIMETYPE;
import static org.eclipse.persistence.internal.xr.Util.TARGET_NAMESPACE_PREFIX;
import static org.eclipse.persistence.internal.xr.Util.getClassFromJDBCType;
import static org.eclipse.persistence.oxm.XMLConstants.BASE_64_BINARY_QNAME;
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
import static org.eclipse.persistence.tools.dbws.Util.PK_QUERYNAME;
import static org.eclipse.persistence.tools.dbws.Util.REMOVE_OPERATION_NAME;
import static org.eclipse.persistence.tools.dbws.Util.SWAREF_FILENAME;
import static org.eclipse.persistence.tools.dbws.Util.THE_INSTANCE_NAME;
import static org.eclipse.persistence.tools.dbws.Util.UPDATE_OPERATION_NAME;
import static org.eclipse.persistence.tools.dbws.Util.WEB_XML_FILENAME;
import static org.eclipse.persistence.tools.dbws.Util.WSI_SWAREF;
import static org.eclipse.persistence.tools.dbws.Util.WSI_SWAREF_PREFIX;
import static org.eclipse.persistence.tools.dbws.Util.WSI_SWAREF_URI;
import static org.eclipse.persistence.tools.dbws.Util.WSI_SWAREF_XSD_FILE;
import static org.eclipse.persistence.tools.dbws.Util.getXMLTypeFromJDBCType;
import static org.eclipse.persistence.tools.dbws.Util.InOut.OUT;
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
    public static final String PLATFORM_CLASSNAME_KEY = "platformClassname";
    public static final String ORSESSION_CUSTOMIZER_KEY = "orSessionCustomizerClassName";
    public static final String OXSESSION_CUSTOMIZER_KEY = "oxSessionCustomizerClassName";
    public static final String WSDL_URI_KEY = "wsdlLocationURI";
    public static final String LOG_LEVEL_KEY = "logLevel";
    public static final String TARGET_NAMESPACE_KEY = "targetNamespace";
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
        OutputStream codeGenProviderStream = null;
        try {
            codeGenProviderStream = packager.getProviderClassStream();
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
        build(dbwsSchemaStream, dbwsSessionsStream, dbwsServiceStream, dbwsOrStream,
            dbwsOxStream, swarefStream, webXmlStream, wsdlStream, codeGenProviderStream,
            sourceProviderStream, logger);
    }

    public void build(OutputStream dbwsSchemaStream, OutputStream dbwsSessionsStream,
        OutputStream dbwsServiceStream, OutputStream dbwsOrStream, OutputStream dbwsOxStream,
        OutputStream swarefStream, OutputStream webXmlStream, OutputStream wsdlStream,
        OutputStream codeGenProviderStream, OutputStream sourceProviderStream, Logger logger)
        throws WSDLException {

        this.logger = logger; // in case some other tool wishes to use a java.util.logger
        // misc setup
        xrServiceModel.setName(getProjectName());
        String sessionsFileName = getSessionsFileName();
        if (sessionsFileName != null && sessionsFileName.length() > 0) {
            xrServiceModel.setSessionsFile(sessionsFileName);
        }
        // setup the NamingConventionTransformers
        ServiceLoader<NamingConventionTransformer> transformers =
            ServiceLoader.load(NamingConventionTransformer.class);
        Iterator<NamingConventionTransformer> transformerIter = transformers.iterator();
        NamingConventionTransformer topTransformer = transformerIter.next();
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
        packager.start();
        buildDbArtifacts();
        buildOROXProjects(topTransformer); // don't write out projects yet; buildDBWSModel may add additional mappings
        // don't write out schema yet; buildDBWSModel/buildWSDL may add additional schema elements
        buildSchema(topTransformer);
        buildSessionsXML(dbwsSessionsStream);
        buildDBWSModel(topTransformer, dbwsServiceStream);
        writeAttachmentSchema(swarefStream);
        buildWSDL(wsdlStream, topTransformer);
        writeWebXML(webXmlStream);
        writeDBWSProviderClass(codeGenProviderStream);
        writeDBWSProviderSource(sourceProviderStream);
        writeSchema(dbwsSchemaStream); // now write out schema
        writeOROXProjects(dbwsOrStream, dbwsOxStream);
        packager.end();
    }

    protected void buildDbArtifacts() {
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
                List<DbStoredProcedure> procs = loadProcedures(catalogPattern,
                    schemaPattern, procedurePattern, procedureModel.overload, isOracle);
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
                    case Types.ARRAY :   // TODO - once JPub stuff is in EclipseLink, take 
                    case Types.STRUCT :  // ARRAY and STRUCT out of this list
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

    protected List<DbStoredProcedure> loadProcedures(String catalogPattern, String schemaPattern,
        String procedurePattern, int overload, boolean isOracle) {
        if (isOracle && catalogPattern != null) { // procedure is in a package, use OracleHelper
            return OracleHelper.buildStoredProcedure(getConnection(), getUsername(), databasePlatform,
                catalogPattern, schemaPattern, procedurePattern);
        }
        else {
            // use JDBC helper
            return checkStoredProcedures(
                JDBCHelper.buildStoredProcedure(getConnection(), databasePlatform, catalogPattern,
                schemaPattern, procedurePattern), overload);
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
                else if (jdbcType == ARRAY ||     // TODO - once JPub stuff is in EclipseLink, take 
                         jdbcType == STRUCT ||    // ARRAY and STRUCT out of this list 
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
        if (dbTables.isEmpty()) {
            logMessage(FINEST, "No tables specified");
            oxProject = new SimpleXMLFormatProject();
        }
        else {
            oxProject = new Project();
        }
        oxProject.setName(projectName + "-" + DBWS_OX_LABEL);
        for (DbTable dbTable : dbTables) {
            RelationalDescriptor desc = new RelationalDescriptor();
            orProject.addDescriptor(desc);
            String tableName = dbTable.getName();
            String tablenameAlias = nct.generateSchemaAlias(tableName);
            desc.addTableName(tableName);
            desc.setAlias(tablenameAlias);
            String generatedJavaClassName = getGeneratedJavaClassName(tableName);
            desc.setJavaClassName(generatedJavaClassName);
            desc.useWeakIdentityMap();
            XMLDescriptor xdesc = new XMLDescriptor();
            oxProject.addDescriptor(xdesc);
            xdesc.setJavaClassName(generatedJavaClassName);
            xdesc.setAlias(tablenameAlias);
            NamespaceResolver nr = new NamespaceResolver();
            nr.put("xsi", W3C_XML_SCHEMA_INSTANCE_NS_URI);
            nr.put("xsd", W3C_XML_SCHEMA_NS_URI);
            nr.put(TARGET_NAMESPACE_PREFIX, getTargetNamespace());
            xdesc.setNamespaceResolver(nr);
            xdesc.setDefaultRootElement(TARGET_NAMESPACE_PREFIX + ":" + tablenameAlias);
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
                Class<?> attributeClass = getClassFromJDBCType(dmdTypeName.toUpperCase(),
                	databasePlatform);
                if (qName == BASE_64_BINARY_QNAME && attributeClass == ABYTE) {
                    // switch to primitive byte[] from Byte[]
                    attributeClass = APBYTE;
                }
                dtfm.setAttributeClassificationName(attributeClass.getName());
                String fieldName = nct.generateElementAlias(columnName);
                dtfm.setAttributeName(fieldName);
                DatabaseField databaseField = new DatabaseField(columnName, tableName);
                databaseField.setSqlType(jdbcType);
                dtfm.setField(databaseField);
                if (dbColumn.isPK()) {
                    desc.addPrimaryKeyField(databaseField);
                }
                xdm.setAttributeName(fieldName);
                String xPath = TARGET_NAMESPACE_PREFIX + ":";
                ElementStyle style = nct.styleForElement(columnName);
                if (style == NONE) {
                    continue;
                }
                else if (style == ATTRIBUTE) {
                    xPath += "@" + fieldName;
                }
                else if (style == ELEMENT){
                    xPath += fieldName;
                }
                desc.addMapping(dtfm);
                xdesc.addMapping(xdm);
                if (!isSwaRef && style == ELEMENT) {
                    xPath += "/text()";
                }
                xdm.setXPath(xPath);
                XMLField xmlField = (XMLField)xdm.getField();
                xmlField.setSchemaType(qName);
                if (!isSwaRef && qName == BASE_64_BINARY_QNAME) {
                    xmlField.setIsTypedTextField(true);
                    xmlField.addConversion(BASE_64_BINARY_QNAME, APBYTE);
                }
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
        for (DbStoredProcedure storedProcedure : dbStoredProcedures) {
            boolean hasPLSQLStoredArgument = false;
            List<DbStoredArgument> arguments = storedProcedure.getArguments();
            int i = 0, l = arguments.size();
            for (; i < l; i++) {
                if (arguments.get(i) instanceof PLSQLStoredArgument) {
                    hasPLSQLStoredArgument = true;
                    break;
                }
            }
            // TODO - fix all of this!
            if (hasPLSQLStoredArgument) {
                PLSQLStoredArgument plsqlStoredArgument = (PLSQLStoredArgument)arguments.get(i);
                ObjectRelationalDataTypeDescriptor ordtDescriptor = 
                    new ObjectRelationalDataTypeDescriptor();
                String alias = plsqlStoredArgument.getName();
                ordtDescriptor.setAlias(alias);
                String javaClassName = plsqlStoredArgument.getPlSqlTypeName().toLowerCase() + "." + 
                    plsqlStoredArgument.getName();
                String arrayName = plsqlStoredArgument.getJdbcTypeName();
                ordtDescriptor.setJavaClassName(javaClassName);
                ordtDescriptor.setTableName(arrayName);
                ordtDescriptor.setPrimaryKeyFieldName("ITEMS");
                ArrayMapping itemsMapping = new ArrayMapping();
                itemsMapping.setAttributeName("items");
                ObjectRelationalDatabaseField field = new ObjectRelationalDatabaseField("ITEMS");
                itemsMapping.setField(field);
                itemsMapping.setStructureName(arrayName);
                ordtDescriptor.addMapping(itemsMapping);
                XMLDescriptor xdesc = new XMLDescriptor();
                xdesc.setAlias(alias);
                xdesc.setJavaClassName(javaClassName);
                xdesc.setDefaultRootElement(TARGET_NAMESPACE_PREFIX + ":" +
                    plsqlStoredArgument.getJdbcTypeName());
                NamespaceResolver nr = new NamespaceResolver();
                nr.put(TARGET_NAMESPACE_PREFIX, getTargetNamespace());
                xdesc.setNamespaceResolver(nr);
                XMLCompositeDirectCollectionMapping listOfStringsMapping = 
                    new XMLCompositeDirectCollectionMapping();
                listOfStringsMapping.useCollectionClass(Vector.class);
                listOfStringsMapping.setAttributeName("items");
                listOfStringsMapping.setXPath(TARGET_NAMESPACE_PREFIX + ":item/text()");
                ((XMLField)listOfStringsMapping.getField()).setUsesSingleNode(false);
                xdesc.addMapping(listOfStringsMapping);
                oxProject.addDescriptor(xdesc);
                PLSQLStoredProcedureCall call = new PLSQLStoredProcedureCall();
                call.setProcedureName(storedProcedure.getCatalog() + "." + storedProcedure.getName());
                String queryOperationName = "";
                for (OperationModel opModel : operations) {
                    if (opModel.isProcedureOperation()) {
                        ProcedureOperationModel procOpModel = (ProcedureOperationModel)opModel;
                        if (procOpModel.getCatalogPattern().equalsIgnoreCase(storedProcedure.getCatalog())
                            && procOpModel.getProcedurePattern().equalsIgnoreCase(storedProcedure.getName())) {
                            queryOperationName = procOpModel.getName();
                            break;
                        }
                    }
                }
                DataModifyQuery mdq = new DataModifyQuery();
                mdq.setName(queryOperationName);
                mdq.setCall(call);
                for (DbStoredArgument argument : arguments) {
                    if (argument instanceof PLSQLStoredArgument) {
                        PLSQLStoredArgument foo = (PLSQLStoredArgument)argument;
                        PLSQLCollection coll = new PLSQLCollection();
                        coll.setTypeName(foo.getPlSqlTypeName());
                        coll.setCompatibleType(foo.getJdbcTypeName());
                        coll.setNestedType(JDBCTypes.VARCHAR_TYPE);
                        call.addNamedArgument(foo.getName(), coll);
                        mdq.addArgument(foo.getName(), Array.class);
                    }
                    else {
                        DatabaseType databaseType = 
                            JDBCTypes.getDatabaseTypeForCode(argument.getJdbcType());
                        call.addNamedArgument(argument.getName(), databaseType);
                        //mdq.addArgument(argument.getName(), databaseType.getTypeName());
                        mdq.addArgument(argument.getName(), String.class);
                    }
                }
                ordtDescriptor.getQueryManager().addQuery(queryOperationName, mdq);
                orProject.addDescriptor(ordtDescriptor);
            }
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

    protected void writeOROXProjects(OutputStream dbwsOrStream, OutputStream dbwsOxStream) {
        boolean writeORProject = false;
        if (dbTables.size() > 0) {
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
        }
        if (writeORProject && !isNullStream(dbwsOrStream)) {
            XMLContext context = new XMLContext(new ObjectPersistenceWorkbenchXMLProject());
            context.getSession(orProject).getEventManager().addListener(new MissingDescriptorListener());
            XMLMarshaller marshaller = context.createMarshaller();
            marshaller.marshal(orProject, new OutputStreamWriter(dbwsOrStream));
        }
        if (!isNullStream(dbwsOxStream)) {
            boolean writeOXProject = false;
            if (oxProject instanceof SimpleXMLFormatProject) {
                if (oxProject.getOrderedDescriptors().size() > 1) {
                    writeOXProject = true;
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

    protected void writeDBWSProviderClass(OutputStream codeGenProviderStream) {
        if (!isNullStream(codeGenProviderStream)) {
            logMessage(FINEST, "writing " + DBWS_PROVIDER_CLASS_FILE);
        	packager.writeProviderClass(codeGenProviderStream, this);
            packager.closeProviderClassStream(codeGenProviderStream);
        }
    }

    public void writeDBWSProviderSource(OutputStream sourceProviderStream) {
        if (!isNullStream(sourceProviderStream)) {
            logMessage(FINEST, "writing " + DBWS_PROVIDER_SOURCE_FILE);
            packager.writeProviderSource(sourceProviderStream, this);
            packager.closeProviderSourceStream(sourceProviderStream);
        }
    }

    @SuppressWarnings("unchecked")
    protected void buildSchema(NamingConventionTransformer nct) {
        schema.setName(getProjectName());
        schema.setTargetNamespace(getTargetNamespace());
        ns.put(TARGET_NAMESPACE_PREFIX, getTargetNamespace());
        schema.setDefaultNamespace(getTargetNamespace());
        schema.setElementFormDefault(true);
        for (DbTable dbTable : dbTables) {
            String dbTableName = dbTable.getName();
            String schemaAlias = nct.generateSchemaAlias(dbTableName);
            ClassDescriptor desc = null;
            Set<Map.Entry<Class, ClassDescriptor>> orDescriptorSet =
                orProject.getDescriptors().entrySet();
            for (Map.Entry<Class, ClassDescriptor> me : orDescriptorSet) {
                desc = me.getValue();
                if (desc.getAlias().equalsIgnoreCase(dbTableName)) {
                    break;
                }
            }
            if (desc != null) {
	            String tablenameAlias = desc.getAlias();
	            ComplexType complexType = new ComplexType();
	            complexType.setName(schemaAlias);
	            Sequence sequence = new Sequence();
	            Iterator j = desc.getMappings().iterator();
	            while (j.hasNext()) {
	                DirectToFieldMapping mapping = (DirectToFieldMapping)j.next();
                    DatabaseField field = mapping.getField();
	                SimpleComponent component = null;
                    StringBuilder sb = new StringBuilder();
                    String dbColumnName = null;
                    boolean optional = true;
                    for (DbColumn dbColumn : dbTable.getColumns()) {
                        dbColumnName = dbColumn.getName();
                        if (dbColumnName.equals(field.getName())) {
                            if (!dbColumn.isNullable() || dbColumn.isPK() || dbColumn.isUnique()) {
                                optional = false;
                            }
                            int jdbcType = dbColumn.getJDBCType();
                            QName qName = getXMLTypeFromJDBCType(jdbcType);
                            if (qName == BASE_64_BINARY_QNAME) {
                                Set<Map.Entry<Class, ClassDescriptor>> oxDescriptorSet =
                                    oxProject.getDescriptors().entrySet();
                                for (Map.Entry<Class, ClassDescriptor> me : oxDescriptorSet) {
                                    ClassDescriptor oxDesc = me.getValue();
                                    if (oxDesc.getAlias().equals(tablenameAlias)) {
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
                            break;
                        }
                    }
                    String elementNameAlias = nct.generateElementAlias(dbColumnName);
                    if (nct.styleForElement(dbColumnName) == ELEMENT) {
                        component = new Element();
                        sequence.addElement((Element)component);
                    }
                    else {
                        component = new Attribute();
                        complexType.getOrderedAttributes().add(component);
                    }
                    component.setName(elementNameAlias);
                    component.setType(sb.toString());
                    if (optional) {
                        if (component instanceof Element) {
                            Element element = (Element)component;
                            element.setMinOccurs("0");
                        }
                    }
                    else {
                        if (component instanceof Element) {
                            Element element = (Element)component;
                            element.setMinOccurs("1");
                        }
                        else {
                            Attribute attribute = (Attribute)component;
                            attribute.setUse(Attribute.REQUIRED);
                        }
                    }
	            }
	            complexType.setSequence(sequence);
	            schema.addTopLevelComplexTypes(complexType);
	            Element wrapperElement = new Element();
	            wrapperElement.setName(tablenameAlias);
	            wrapperElement.setType(complexType.getName());
	            schema.addTopLevelElement(wrapperElement);
            }
        }
        for (DbStoredProcedure storedProcedure : dbStoredProcedures) {
            for (DbStoredArgument storedArgument : storedProcedure.getArguments()) {
                if (storedArgument instanceof PLSQLStoredArgument) {
                    PLSQLStoredArgument plsqlStoredArgument = (PLSQLStoredArgument)storedArgument;
                    String schemaAlias = plsqlStoredArgument.getJdbcTypeName();
                    /*
                    <xsd:complexType name=${schemaAlias}>
                      <xsd:sequence>
                        <xsd:element name=${schemaAlias}>
                          <xsd:complexType>
                            <xsd:sequence>
                              <xs:element name="item" type="xsd:string" minOccurs=0 maxOccurs="unbounded"/>
                            </xsd:sequence>
                          </xsd:complexType>
                        </xsd:element>
                      </xsd:sequence>
                    </xsd:complexType>
                    TODO - need to figure out nested complex types
                     */
                    ComplexType complexType = new ComplexType();
                    complexType.setName(schemaAlias);
                    Sequence sequence = new Sequence();
                    complexType.setSequence(sequence);
                    Element wrappedElement = new Element();
                    wrappedElement.setName(schemaAlias);
                    sequence.addElement(wrappedElement);
                    ComplexType wrappedComplexType = new ComplexType();
                    wrappedElement.setComplexType(wrappedComplexType);
                    Sequence wrappedsequence = new Sequence();
                    wrappedComplexType.setSequence(wrappedsequence);
                    Element itemsElement = new Element();
                    itemsElement.setName("item");
                    itemsElement.setType("xsd:string");
                    itemsElement.setMinOccurs("0");
                    itemsElement.setMaxOccurs("unbounded");
                    wrappedsequence.addElement(itemsElement);
                    schema.addTopLevelComplexTypes(complexType);
                    Element element = new Element();
                    element.setName(schemaAlias);
                    element.setType(complexType.getName());
                    schema.addTopLevelElement(element);
                    break;
                }
            }
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

    @SuppressWarnings("unchecked")
    protected void buildDBWSModel(NamingConventionTransformer nct, OutputStream dbwsServiceStream) {

        if (!isNullStream(dbwsServiceStream)) {
            for (Iterator i = orProject.getOrderedDescriptors().iterator(); i.hasNext();) {
                ClassDescriptor desc = (ClassDescriptor)i.next();
                String tablenameAlias = desc.getAlias();
                boolean buildCRUDoperations = false;
                for (DbTable dbTable : dbTables) {
                    if (dbTable.getName().equals(tablenameAlias)) {
                        buildCRUDoperations = true;
                        break;
                    }
                }
                if (buildCRUDoperations) {
                    QueryOperation findByPKQueryOperation = new QueryOperation();
                    findByPKQueryOperation.setName(PK_QUERYNAME + "_" + tablenameAlias);
                    NamedQueryHandler nqh1 = new NamedQueryHandler();
                    nqh1.setName(PK_QUERYNAME);
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
                    deleteOperation.getParameters().add(theInstance);
                    xrServiceModel.getOperations().put(deleteOperation.getName(), deleteOperation);
                }
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
            wsdlGenerator.generateWSDL();
            packager.closeWSDLStream(wsdlStream);
        }
    }

    protected ProjectConfig buildORProjectConfig() {
        ProjectConfig orProjectConfig = null;
        boolean useProjectXML = false;
        if (dbTables.size() > 0) {
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
        if (dbTables.size() > 0) {
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

    @SuppressWarnings("unchecked")
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
                logMessage(SEVERE, "cannot load JDBC driver " + driverClassName, e);
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
            databasePlatform = new MySQLPlatform();
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
}
