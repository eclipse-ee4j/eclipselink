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
 *     Mike Norman - from Proof-of-concept, become production code
 ******************************************************************************/
package org.eclipse.persistence.tools.dbws;

//javase imports
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.FINEST;
import static java.util.logging.Level.SEVERE;

//java eXtension imports
import javax.wsdl.WSDLException;
import javax.xml.namespace.QName;

//EclipseLink imports
import org.eclipse.persistence.dbws.DBWSModelProject;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.oxm.schema.SchemaModelGenerator;
import org.eclipse.persistence.internal.oxm.schema.SchemaModelGeneratorProperties;
import org.eclipse.persistence.internal.oxm.schema.SchemaModelProject;
import org.eclipse.persistence.internal.oxm.schema.model.ComplexType;
import org.eclipse.persistence.internal.oxm.schema.model.Schema;
import org.eclipse.persistence.internal.sessions.factories.MissingDescriptorListener;
import org.eclipse.persistence.internal.sessions.factories.ObjectPersistenceWorkbenchXMLProject;
import org.eclipse.persistence.internal.sessions.factories.XMLSessionConfigProject_11_1_1;
import org.eclipse.persistence.internal.sessions.factories.model.SessionConfigs;
import org.eclipse.persistence.internal.xr.CollectionResult;
import org.eclipse.persistence.internal.xr.DeleteOperation;
import org.eclipse.persistence.internal.xr.InsertOperation;
import org.eclipse.persistence.internal.xr.NamedQueryHandler;
import org.eclipse.persistence.internal.xr.Parameter;
import org.eclipse.persistence.internal.xr.ProjectHelper;
import org.eclipse.persistence.internal.xr.QueryOperation;
import org.eclipse.persistence.internal.xr.Result;
import org.eclipse.persistence.internal.xr.UpdateOperation;
import org.eclipse.persistence.internal.xr.Util;
import org.eclipse.persistence.internal.xr.XRDynamicClassLoader;
import org.eclipse.persistence.internal.xr.sxf.SimpleXMLFormat;
import org.eclipse.persistence.internal.xr.sxf.SimpleXMLFormatProject;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.converters.SerializedObjectConverter;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.XMLLogin;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.mappings.XMLBinaryDataMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.mappings.nullpolicy.AbstractNullPolicy;
import org.eclipse.persistence.oxm.platform.DOMPlatform;
import org.eclipse.persistence.platform.database.jdbc.JDBCTypes;
import org.eclipse.persistence.platform.database.oracle.jdbc.OracleArrayType;
import org.eclipse.persistence.platform.database.oracle.jdbc.OracleObjectType;
import org.eclipse.persistence.platform.database.oracle.plsql.OraclePLSQLTypes;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLCollection;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLrecord;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.factories.XMLProjectReader;
import org.eclipse.persistence.sessions.factories.XMLProjectWriter;
import org.eclipse.persistence.tools.dbws.NamingConventionTransformer.ElementStyle;
import org.eclipse.persistence.tools.dbws.jdbc.DbColumn;
import static org.eclipse.persistence.internal.helper.ClassConstants.APBYTE;
import static org.eclipse.persistence.internal.helper.ClassConstants.BIGDECIMAL;
import static org.eclipse.persistence.internal.helper.ClassConstants.BOOLEAN;
import static org.eclipse.persistence.internal.helper.ClassConstants.INTEGER;
import static org.eclipse.persistence.internal.helper.ClassConstants.JavaSqlDate_Class;
import static org.eclipse.persistence.internal.helper.ClassConstants.STRING;
import static org.eclipse.persistence.internal.oxm.schema.SchemaModelGeneratorProperties.ELEMENT_FORM_QUALIFIED_KEY;
import static org.eclipse.persistence.internal.xr.Util.DBWS_OR_LABEL;
import static org.eclipse.persistence.internal.xr.Util.DBWS_OX_LABEL;
import static org.eclipse.persistence.internal.xr.Util.DBWS_WSDL;
import static org.eclipse.persistence.internal.xr.Util.DEFAULT_ATTACHMENT_MIMETYPE;
import static org.eclipse.persistence.internal.xr.Util.PK_QUERYNAME;
import static org.eclipse.persistence.internal.xr.Util.TARGET_NAMESPACE_PREFIX;
import static org.eclipse.persistence.internal.xr.Util.getClassFromJDBCType;
import static org.eclipse.persistence.internal.xr.XRDynamicClassLoader.COLLECTION_WRAPPER_SUFFIX;
import static org.eclipse.persistence.internal.xr.sxf.SimpleXMLFormat.DEFAULT_SIMPLE_XML_FORMAT_TAG;
import static org.eclipse.persistence.oxm.XMLConstants.BASE_64_BINARY_QNAME;
import static org.eclipse.persistence.oxm.mappings.nullpolicy.XMLNullRepresentationType.XSI_NIL;
import static org.eclipse.persistence.tools.dbws.NamingConventionTransformer.ElementStyle.ATTRIBUTE;
import static org.eclipse.persistence.tools.dbws.NamingConventionTransformer.ElementStyle.ELEMENT;
import static org.eclipse.persistence.tools.dbws.NamingConventionTransformer.ElementStyle.NONE;
import static org.eclipse.persistence.tools.dbws.Util.CREATE_OPERATION_NAME;
import static org.eclipse.persistence.tools.dbws.Util.DBWS_PROVIDER_CLASS_FILE;
import static org.eclipse.persistence.tools.dbws.Util.DBWS_PROVIDER_SOURCE_FILE;
import static org.eclipse.persistence.tools.dbws.Util.FINDALL_QUERYNAME;
import static org.eclipse.persistence.tools.dbws.Util.REMOVE_OPERATION_NAME;
import static org.eclipse.persistence.tools.dbws.Util.THE_INSTANCE_NAME;
import static org.eclipse.persistence.tools.dbws.Util.TOPLEVEL;
import static org.eclipse.persistence.tools.dbws.Util.UPDATE_OPERATION_NAME;
import static org.eclipse.persistence.tools.dbws.Util.WSI_SWAREF_PREFIX;
import static org.eclipse.persistence.tools.dbws.Util.WSI_SWAREF_URI;
import static org.eclipse.persistence.tools.dbws.Util.WSI_SWAREF_XSD_FILE;
import static org.eclipse.persistence.tools.dbws.Util.WSI_SWAREF_XSD;
import static org.eclipse.persistence.tools.dbws.Util.addSimpleXMLFormat;
import static org.eclipse.persistence.tools.dbws.Util.buildORDescriptor;
import static org.eclipse.persistence.tools.dbws.Util.buildOXDescriptor;
import static org.eclipse.persistence.tools.dbws.Util.escapePunctuation;
import static org.eclipse.persistence.tools.dbws.Util.getJDBCTypeFromTypeName;
import static org.eclipse.persistence.tools.dbws.Util.getJDBCTypeNameFromType;
import static org.eclipse.persistence.tools.dbws.Util.getXMLTypeFromJDBCType;
import static org.eclipse.persistence.tools.dbws.Util.getGeneratedJavaClassName;
import static org.eclipse.persistence.tools.dbws.Util.isNullStream;
import static org.eclipse.persistence.tools.dbws.Util.requiresSimpleXMLFormat;
import static org.eclipse.persistence.tools.dbws.Util.sqlMatch;

//DDL parser imports
import org.eclipse.persistence.tools.oracleddl.metadata.ArgumentType;
import org.eclipse.persistence.tools.oracleddl.metadata.BinaryType;
import org.eclipse.persistence.tools.oracleddl.metadata.BlobType;
import org.eclipse.persistence.tools.oracleddl.metadata.CharType;
import org.eclipse.persistence.tools.oracleddl.metadata.ClobType;
import org.eclipse.persistence.tools.oracleddl.metadata.DatabaseType;
import org.eclipse.persistence.tools.oracleddl.metadata.DecimalType;
import org.eclipse.persistence.tools.oracleddl.metadata.DoubleType;
import org.eclipse.persistence.tools.oracleddl.metadata.FieldType;
import org.eclipse.persistence.tools.oracleddl.metadata.FloatType;
import org.eclipse.persistence.tools.oracleddl.metadata.FunctionType;
import org.eclipse.persistence.tools.oracleddl.metadata.LongRawType;
import org.eclipse.persistence.tools.oracleddl.metadata.NCharType;
import org.eclipse.persistence.tools.oracleddl.metadata.NClobType;
import org.eclipse.persistence.tools.oracleddl.metadata.NumericType;
import org.eclipse.persistence.tools.oracleddl.metadata.ObjectType;
import org.eclipse.persistence.tools.oracleddl.metadata.PLSQLCollectionType;
import org.eclipse.persistence.tools.oracleddl.metadata.PLSQLRecordType;
import org.eclipse.persistence.tools.oracleddl.metadata.PLSQLType;
import org.eclipse.persistence.tools.oracleddl.metadata.PrecisionType;
import org.eclipse.persistence.tools.oracleddl.metadata.ProcedureType;
import org.eclipse.persistence.tools.oracleddl.metadata.RawType;
import org.eclipse.persistence.tools.oracleddl.metadata.RealType;
import org.eclipse.persistence.tools.oracleddl.metadata.ScalarDatabaseTypeEnum;
import org.eclipse.persistence.tools.oracleddl.metadata.SizedType;
import org.eclipse.persistence.tools.oracleddl.metadata.TableType;
import org.eclipse.persistence.tools.oracleddl.metadata.VArrayType;
import org.eclipse.persistence.tools.oracleddl.metadata.VarChar2Type;

public abstract class BaseDBWSBuilderHelper {

    public static final String ITEM_MAPPING_NAME = "item";
    public static final String ITEMS_MAPPING_ATTRIBUTE_NAME = "items";
    public static final String ITEMS_MAPPING_FIELD_NAME = "ITEMS";
    public static final String BOOLEAN_STR = "BOOLEAN";
    public static final String DATE_STR = "DATE";
    public static final String DECIMAL_STR = "DECIMAL";
    public static final String INTEGER_STR = "INTEGER";
    public static final String MTOM_STR = "MTOM";
    public static final String NUMBER_STR = "NUMBER";
    public static final String SWAREF_STR = "SWAREF";


    protected List<TableType> dbTables = new ArrayList<TableType>();
    protected List<ProcedureType> dbStoredProcedures = new ArrayList<ProcedureType>();
    protected Map<ProcedureType, DbStoredProcedureNameAndModel> dbStoredProcedure2QueryName =
            new HashMap<ProcedureType, DbStoredProcedureNameAndModel>();
    protected DBWSBuilder dbwsBuilder;
    protected XMLSessionConfigProject_11_1_1 sessionConfigProject =
        new XMLSessionConfigProject_11_1_1();
    protected NamingConventionTransformer nct;
    protected ObjectPersistenceWorkbenchXMLProject workbenchXMLProject =
        new ObjectPersistenceWorkbenchXMLProject();

    public BaseDBWSBuilderHelper(DBWSBuilder dbwsBuilder) {
        this.dbwsBuilder = dbwsBuilder;
    }

    //abstract methods - platform-specific behaviour in JDBCHelper, OracleHelper
    public abstract boolean hasTables();

    public abstract boolean hasComplexProcedureArgs();

    protected abstract List<TableType> loadTables(String originalCatalogPattern,
        String originalSchemaPattern, String originalTablePattern);

    protected abstract List<ProcedureType> loadProcedures(ProcedureOperationModel procedureModel);

    protected abstract void addToOROXProjectsForComplexArgs(List<ArgumentType> arguments, Project orProject, Project oxProject, ProcedureOperationModel opModel);
    protected abstract void buildQueryForProcedureType(ProcedureType procType, Project orProject, Project oxProject, ProcedureOperationModel opModel, String queryName);

    protected void addToOROXProjectsForSecondarySql(SQLOperationModel sqlOm,
        Project orProject, Project oxProject, NamingConventionTransformer nct) {
        List<DbColumn> columns = buildDbColumns(dbwsBuilder.getConnection(), sqlOm.getBuildSql());
        String tableName = sqlOm.getReturnType();
        NamingConventionTransformer customNct = setUpCustomTransformer(tableName, nct);
        RelationalDescriptor desc = buildORDescriptor(tableName, dbwsBuilder.getProjectName(),
            null, customNct);
        desc.descriptorIsAggregate();
        orProject.addDescriptor(desc);
        XMLDescriptor xdesc = buildOXDescriptor(tableName, dbwsBuilder.getProjectName(),
            dbwsBuilder.getTargetNamespace(), customNct);
        oxProject.addDescriptor(xdesc);
        List<String> columnsAlreadyProcessed = new ArrayList<String>();
        for (DbColumn dbColumn : columns) {
            String columnName = dbColumn.getFieldName();
            if (!columnsAlreadyProcessed.contains(columnName)) {
                columnsAlreadyProcessed.add(columnName);
                ElementStyle style = nct.styleForElement(columnName);
                if (style == NONE) {
                    continue;
                }
                dbwsBuilder.logMessage(FINE, "Building mappings for " + columnName);
                DirectToFieldMapping orFieldMapping = buildORFieldMappingFromColumn(dbColumn, desc,
                    dbwsBuilder.getDatabasePlatform(), nct);
                desc.addMapping(orFieldMapping);
                XMLDirectMapping oxFieldMapping = buildOXFieldMappingFromColumn(dbColumn,
                    dbwsBuilder.getDatabasePlatform(), nct);
                xdesc.addMapping(oxFieldMapping);
            }
            else {
                dbwsBuilder.logMessage(SEVERE, "Duplicate ResultSet columns not supported '" +
                    columnName + "'");
                throw new RuntimeException("Duplicate ResultSet columns not supported");
            }
        }
    }

    public void buildOROXProjects(NamingConventionTransformer nct) {
        this.nct = nct; // save for later
        String projectName = dbwsBuilder.getProjectName();
        Project orProject = new Project();
        orProject.setName(projectName + "-" + DBWS_OR_LABEL);
        Project oxProject = null;
        if (dbTables.isEmpty() && !dbwsBuilder.hasBuildSqlOperations()) {
            dbwsBuilder.logMessage(FINEST, "No tables specified");
            oxProject = new SimpleXMLFormatProject();
        }
        else {
            oxProject = new Project();
        }
        oxProject.setName(projectName + "-" + DBWS_OX_LABEL);
        for (TableType dbTable : dbTables) {
            String tableName = dbTable.getTableName();
            RelationalDescriptor desc = buildORDescriptor(tableName, dbwsBuilder.getProjectName(),
                dbwsBuilder.requireCRUDOperations, nct);
            orProject.addDescriptor(desc);
            XMLDescriptor xdesc = buildOXDescriptor(tableName, dbwsBuilder.getProjectName(),
                dbwsBuilder.getTargetNamespace(), nct);
            oxProject.addDescriptor(xdesc);
            for (FieldType dbColumn : dbTable.getColumns()) {
                String columnName = dbColumn.getFieldName();
                ElementStyle style = nct.styleForElement(columnName);
                if (style == NONE) {
                    continue;
                }
                dbwsBuilder.logMessage(FINE, "Building mappings for " + tableName + "." + columnName);
                DirectToFieldMapping orFieldMapping = buildORFieldMappingFromColumn(dbColumn, desc,
                    dbwsBuilder.getDatabasePlatform(), nct);
                desc.addMapping(orFieldMapping);
                XMLDirectMapping oxFieldMapping = buildOXFieldMappingFromColumn(dbColumn,
                    dbwsBuilder.getDatabasePlatform(), nct);
                xdesc.addMapping(oxFieldMapping);
                // check for switch from Byte[] to byte[]
                if (oxFieldMapping.getAttributeClassificationName() == APBYTE.getName()) {
                    orFieldMapping.setAttributeClassificationName(APBYTE.getName());
                }
          }
          setUpFindQueries(tableName, desc);
        }
        finishUpProjects(orProject, oxProject);
    }

    protected DirectToFieldMapping buildORFieldMappingFromColumn(FieldType dbColumn,
        RelationalDescriptor desc, DatabasePlatform databasePlatform,
        NamingConventionTransformer nct) {
        String columnName = dbColumn.getFieldName();
        DatabaseType dataType = dbColumn.getDataType();
        String typeName = getTypeNameForDatabaseType(dataType);
        int jdbcType = getJDBCTypeFromTypeName(typeName);
        String dmdTypeName = getJDBCTypeNameFromType(jdbcType);
        Class<?> attributeClass = null;
        if ("CHAR".equalsIgnoreCase(dmdTypeName) && dbColumn.getDataType() instanceof SizedType) {
            SizedType sizedType = (SizedType)dbColumn.getDataType();
            if (sizedType.getSize() == 1) {
                attributeClass = Character.class;
            }
            else {
                attributeClass = String.class;
            }
        }
        else {
            attributeClass = getClassFromJDBCType(dmdTypeName.toUpperCase(), databasePlatform);
        }
        //https://bugs.eclipse.org/bugs/show_bug.cgi?id=359130
        //problem with java.sql.Timestamp conversion and Oracle11 platform
        if (attributeClass.getName().contains("oracle.sql.TIMESTAMP")) {
            attributeClass = java.sql.Timestamp.class;
        }
        DirectToFieldMapping dtfm = setUpDirectToFieldMapping(desc, columnName, nct, attributeClass,
            jdbcType, dbColumn.pk());
        return dtfm;
    }

    protected XMLDirectMapping buildOXFieldMappingFromColumn(FieldType dbColumn,
        DatabasePlatform databasePlatform, NamingConventionTransformer nct) {
        String columnName = dbColumn.getFieldName();
        DatabaseType dataType = dbColumn.getDataType();
        String typeName = getTypeNameForDatabaseType(dataType);
        int jdbcType = getJDBCTypeFromTypeName(typeName);
        String dmdTypeName = getJDBCTypeNameFromType(jdbcType);
        QName qName = getXMLTypeFromJDBCType(jdbcType);
        Class<?> attributeClass;
        if ("CHAR".equalsIgnoreCase(dmdTypeName) && dbColumn.getDataType() instanceof SizedType) {
            SizedType sizedType = (SizedType)dbColumn.getDataType();
            if (sizedType.getSize() == 1) {
                attributeClass = Character.class;
            }
            else {
                attributeClass = String.class;
            }
        }
        else {
            attributeClass = getClassFromJDBCType(dmdTypeName.toUpperCase(), databasePlatform);
        }
        //https://bugs.eclipse.org/bugs/show_bug.cgi?id=359130
        //problem with conversion and Oracle11 platform
        if (attributeClass.getName().contains("oracle.sql.TIMESTAMP")) {
            attributeClass = java.sql.Timestamp.class;
        }
        XMLDirectMapping xdm = setUpXMLDirectMapping(columnName, qName, nct, attributeClass,
            jdbcType, dbColumn.pk());
        return xdm;
    }

    /**
     * Gather table and procedure information based on TableOperation
     * and ProcedureOperation.  End result will be List<TableType>
     * and List<ProcedureType>.
     */
    public void buildDbArtifacts() {
        // do Table operations first
        //it is possible a builder might have pre-built tables
        if (dbTables.size() == 0) {
            for (OperationModel operation : dbwsBuilder.operations) {
                if (operation.isTableOperation()) {
                    TableOperationModel tableModel = (TableOperationModel)operation;
                    String catalogPattern = tableModel.getCatalogPattern();
                    String schemaPattern = tableModel.getSchemaPattern();
                    String tableNamePattern = tableModel.getTablePattern();
                    List<TableType> tables = loadTables(catalogPattern, schemaPattern, tableNamePattern);
                    if (tables == null || tables.isEmpty()) {
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
                        dbwsBuilder.logMessage(FINEST, sb.toString());
                    }
                    else {
                        dbTables.addAll(tables);
                    }
                }
            }
        }
        // next do StoredProcedure operations
        //its possible a builder might have pre-built procedures
        if (dbStoredProcedures.size() == 0) {
            for (OperationModel operation : dbwsBuilder.operations) {
                if (operation.isProcedureOperation()) {
                    ProcedureOperationModel procedureModel = (ProcedureOperationModel)operation;
                    List<ProcedureType> procs = loadProcedures(procedureModel);
                    if (procs == null || procs.isEmpty()) {
                        String catalogPattern = procedureModel.getCatalogPattern();
                        String schemaPattern = procedureModel.getSchemaPattern();
                        String procedurePattern = procedureModel.getProcedurePattern();
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
                        dbwsBuilder.logMessage(FINEST, sb.toString());
                    }
                    else {
                        dbStoredProcedures.addAll(procs);
                    }
                }
            }
        }
        buildDbStoredProcedure2QueryNameMap(dbStoredProcedure2QueryName,
            dbStoredProcedures, dbwsBuilder.operations);
    }

    @SuppressWarnings("unchecked")
    public void buildSchema(NamingConventionTransformer nct) {
        Project oxProject = dbwsBuilder.getOxProject();
        Schema schema = null;
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
            sgProperties.addProperty(dbwsBuilder.getTargetNamespace(), ELEMENT_FORM_QUALIFIED_KEY, true);
            Map<String, Schema> schemaMap = schemaGenerator.generateSchemas(descriptorsToProcess, sgProperties);
            Schema s = schemaMap.get(dbwsBuilder.getTargetNamespace());
            // check existing schema for any top-level ComplexTypes
            if (dbwsBuilder.getSchema() != null && s != null) {
                Map<String, ComplexType> topLevelComplexTypes = dbwsBuilder.getSchema().getTopLevelComplexTypes();
                for (Map.Entry<String, ComplexType> me : topLevelComplexTypes.entrySet()) {
                    s.addTopLevelComplexTypes(me.getValue());
                }
                // add any additional namespaces
                NamespaceResolver snr = s.getNamespaceResolver();
                NamespaceResolver nr = dbwsBuilder.getSchema().getNamespaceResolver();
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
            schema = new Schema();
            addSimpleXMLFormat(schema);
            schema.setTargetNamespace(dbwsBuilder.getTargetNamespace());
        }
        dbwsBuilder.setSchema(schema);
    }

    public void buildSessionsXML(OutputStream dbwsSessionsStream) {
        if (!isNullStream(dbwsSessionsStream)) {
            dbwsBuilder.logMessage(FINEST, "Building " + dbwsBuilder.getSessionsFileName());
            SessionConfigs ts = dbwsBuilder.getPackager().buildSessionsXML(dbwsSessionsStream, dbwsBuilder);
            XMLContext context = new XMLContext(sessionConfigProject);
            XMLMarshaller marshaller = context.createMarshaller();
            marshaller.marshal(ts, new OutputStreamWriter(dbwsSessionsStream));
            dbwsBuilder.getPackager().closeSessionsStream(dbwsSessionsStream);
        }
    }

    @SuppressWarnings({"unchecked"/*, "rawtypes"*/})
    public void buildDBWSModel(NamingConventionTransformer nct, OutputStream dbwsServiceStream) {
        Project orProject = dbwsBuilder.getOrProject();
        Project oxProject = dbwsBuilder.getOxProject();
        if (!isNullStream(dbwsServiceStream)) {
            for (Iterator i = orProject.getOrderedDescriptors().iterator(); i.hasNext();) {
                ClassDescriptor desc = (ClassDescriptor)i.next();
                String tablenameAlias = desc.getAlias();
                if (dbwsBuilder.requireCRUDOperations.contains(tablenameAlias)) {
                    QueryOperation findByPKQueryOperation = new QueryOperation();
                    findByPKQueryOperation.setName(Util.PK_QUERYNAME + "_" + tablenameAlias);
                    findByPKQueryOperation.setUserDefined(false);
                    NamedQueryHandler nqh1 = new NamedQueryHandler();
                    nqh1.setName(Util.PK_QUERYNAME);
                    nqh1.setDescriptor(tablenameAlias);
                    Result result = new Result();
                    QName theInstanceType = new QName(dbwsBuilder.getTargetNamespace(), tablenameAlias,
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
                    dbwsBuilder.xrServiceModel.getOperations().put(findByPKQueryOperation.getName(),
                        findByPKQueryOperation);
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
                    dbwsBuilder.xrServiceModel.getOperations().put(findAllOperation.getName(),
                        findAllOperation);
                    InsertOperation insertOperation = new InsertOperation();
                    insertOperation.setName(CREATE_OPERATION_NAME + "_" + tablenameAlias);
                    Parameter theInstance = new Parameter();
                    theInstance.setName(THE_INSTANCE_NAME);
                    theInstance.setType(theInstanceType);
                    insertOperation.getParameters().add(theInstance);
                    dbwsBuilder.xrServiceModel.getOperations().put(insertOperation.getName(),
                        insertOperation);
                    UpdateOperation updateOperation = new UpdateOperation();
                    updateOperation.setName(UPDATE_OPERATION_NAME + "_" + tablenameAlias);
                    updateOperation.getParameters().add(theInstance);
                    dbwsBuilder.xrServiceModel.getOperations().put(updateOperation.getName(),
                        updateOperation);
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
                    dbwsBuilder.xrServiceModel.getOperations().put(deleteOperation.getName(),
                        deleteOperation);
                }
            }
            // check for additional operations
            for (OperationModel operation : dbwsBuilder.operations) {
                if (operation.isTableOperation()) {
                    TableOperationModel tableModel = (TableOperationModel)operation;
                    if (tableModel.additionalOperations != null &&
                        tableModel.additionalOperations.size() > 0) {
                        for (OperationModel additionalOperation : tableModel.additionalOperations) {
                            if (additionalOperation.isSQLOperation() &&
                                ((SQLOperationModel)additionalOperation).hasBuildSql()) {
                                addToOROXProjectsForSecondarySql(
                                    (SQLOperationModel)additionalOperation, orProject, oxProject, nct);
                            }
                            else {
                                additionalOperation.buildOperation(dbwsBuilder);
                            }
                        }
                    }
                }
                else { // handle non-nested <sql> and <procedure> operations
                    operation.buildOperation(dbwsBuilder);
                }
            }

            DBWSModelProject modelProject = new DBWSModelProject();
            modelProject.ns.put(TARGET_NAMESPACE_PREFIX, dbwsBuilder.getTargetNamespace());
            XMLContext context = new XMLContext(modelProject);
            XMLMarshaller marshaller = context.createMarshaller();
            marshaller.marshal(dbwsBuilder.xrServiceModel, dbwsServiceStream);
            dbwsBuilder.getPackager().closeServiceStream(dbwsServiceStream);
        }
    }

    public void writeAttachmentSchema(OutputStream swarefStream) {
        if (!isNullStream(swarefStream)) {
            dbwsBuilder.logMessage(FINEST, "writing " + WSI_SWAREF_XSD_FILE);
            OutputStreamWriter osw = new OutputStreamWriter(new BufferedOutputStream(swarefStream));
            try {
                osw.write(WSI_SWAREF_XSD);
                osw.flush();
            }
            catch (IOException e) {/* ignore */}
            dbwsBuilder.getPackager().closeSWARefStream(swarefStream);
        }
    }

    public void buildWSDL(OutputStream wsdlStream, NamingConventionTransformer nct) throws WSDLException {
        if (!isNullStream(wsdlStream)) {
            dbwsBuilder.logMessage(FINEST, "building " + DBWS_WSDL);
            dbwsBuilder.wsdlGenerator = new WSDLGenerator(dbwsBuilder.xrServiceModel, nct,
                dbwsBuilder.getWsdlLocationURI(),
                dbwsBuilder.getPackager().hasAttachments(), dbwsBuilder.getTargetNamespace(), wsdlStream);
            dbwsBuilder.wsdlGenerator.generateWSDL(dbwsBuilder.usesSOAP12());
            dbwsBuilder.getPackager().closeWSDLStream(wsdlStream);
        }
    }

    public void writeWebXML(OutputStream webXmlStream) {
        if (!isNullStream(webXmlStream)) {
            dbwsBuilder.logMessage(FINEST, "writing web.xml");
            dbwsBuilder.getPackager().writeWebXml(webXmlStream, dbwsBuilder);
            dbwsBuilder.getPackager().closeWebXmlStream(webXmlStream);
        }
    }

    public void generateDBWSProvider(OutputStream sourceProviderStream,
        OutputStream classProviderStream, OutputStream sourceProviderListenerStream,
        OutputStream classProviderListenerStream) {
        if (!isNullStream(sourceProviderStream)) {
            dbwsBuilder.logMessage(FINEST, "generating " + DBWS_PROVIDER_SOURCE_FILE);
        }
        if (!isNullStream(classProviderStream)) {
            dbwsBuilder.logMessage(FINEST, "generating " + DBWS_PROVIDER_CLASS_FILE);
        }
        dbwsBuilder.getPackager().writeProvider(sourceProviderStream, classProviderStream,
            sourceProviderListenerStream, classProviderListenerStream, dbwsBuilder);
        dbwsBuilder.getPackager().closeProviderSourceStream(sourceProviderStream);
        dbwsBuilder.getPackager().closeProviderClassStream(classProviderStream);
    }

    public void writeSchema(OutputStream dbwsSchemaStream) {
        if (!isNullStream(dbwsSchemaStream)) {
            SchemaModelProject schemaProject = new SchemaModelProject();
            boolean hasSwaRef = dbwsBuilder.getSchema().getNamespaceResolver().resolveNamespacePrefix(
                WSI_SWAREF_PREFIX) != null;
            if (hasSwaRef) {
                XMLDescriptor descriptor = (XMLDescriptor)schemaProject.getClassDescriptor(Schema.class);
                descriptor.getNamespaceResolver().put(WSI_SWAREF_PREFIX, WSI_SWAREF_URI);
            }
            XMLContext context = new XMLContext(schemaProject);
            XMLMarshaller marshaller = context.createMarshaller();
            marshaller.marshal(dbwsBuilder.getSchema(), dbwsSchemaStream);
            dbwsBuilder.getPackager().closeSchemaStream(dbwsSchemaStream);
        }
    }

    public void writeOROXProjects(OutputStream dbwsOrStream, OutputStream dbwsOxStream) {
        Project orProject = dbwsBuilder.getOrProject();
        Project oxProject = dbwsBuilder.getOxProject();
        boolean writeORProject = false;
        if (hasTables() || dbwsBuilder.hasBuildSqlOperations()) {
            writeORProject = true;
        }
        else if (hasComplexProcedureArgs()) {
            writeORProject = true;
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
        if (writeORProject && !isNullStream(dbwsOrStream)) {
            XMLContext context = new XMLContext(workbenchXMLProject);
            context.getSession(orProject).getEventManager().addListener(new MissingDescriptorListener());
            XMLMarshaller marshaller = context.createMarshaller();
            marshaller.marshal(orProject, new OutputStreamWriter(dbwsOrStream));
        }
        if (!isNullStream(dbwsOxStream)) {
            boolean writeOXProject = false;
            if (hasTables() || dbwsBuilder.hasBuildSqlOperations()) {
                writeOXProject = true;
            }
            else if (hasComplexProcedureArgs()) {
                writeOXProject = true;
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
            if (writeOXProject) {
                XMLContext context = new XMLContext(workbenchXMLProject);
                context.getSession(oxProject).getEventManager().addListener(new MissingDescriptorListener());
                XMLMarshaller marshaller = context.createMarshaller();
                marshaller.marshal(oxProject, new OutputStreamWriter(dbwsOxStream));
           }
        }
        dbwsBuilder.getPackager().closeOrStream(dbwsOrStream);
        dbwsBuilder.getPackager().closeOxStream(dbwsOxStream);
    }

    /**
     * Return a ResultSetMetaData instance for a given SQL statement.
     *
     */
    protected static ResultSetMetaData getResultSetMetadataForSecondarySQL(Connection connection, String secondarySql) {
        ResultSet resultSet = null;
        try {
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery(secondarySql);
        }
        catch (SQLException sqlException) {
            throw new IllegalStateException("failure executing secondary SQL: " + secondarySql, sqlException);
        }
        if (resultSet != null) {
            try {
                return resultSet.getMetaData();
            }
            catch (SQLException sqlException) {
                throw new IllegalStateException("failure retrieving resultSet metadata", sqlException);
            }
        }
        return null;
    }

    protected static class DbStoredProcedureNameAndModel {
        public String name;
        public ProcedureOperationModel procOpModel;
        public DbStoredProcedureNameAndModel(String name, ProcedureOperationModel procOpModel) {
            this.name = name;
            this.procOpModel = procOpModel;
        }
    }

    protected static DirectToFieldMapping setUpDirectToFieldMapping(
        RelationalDescriptor desc, String columnName, NamingConventionTransformer nct,
        Class<?> attributeClass, int jdbcType, boolean isPk) {
        DirectToFieldMapping dtfm = new DirectToFieldMapping();
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
        if (isPk) {
            desc.addPrimaryKeyField(databaseField);
        }
        return dtfm;
    }

    protected XMLDirectMapping setUpXMLDirectMapping(String columnName, QName qName,
        NamingConventionTransformer nct, Class<?> attributeClass, int jdbcType, boolean isPk) {
        XMLDirectMapping xdm = null;
        // figure out if binary attachments are required
        boolean binaryAttach = false;
        String attachmentType = null;
        if (BASE_64_BINARY_QNAME.equals(qName)) {
            // use primitive byte[] array, not object Byte[] array
            attributeClass = APBYTE;
            for (OperationModel om : dbwsBuilder.operations) {
                if (om.isTableOperation()) {
                    TableOperationModel tom = (TableOperationModel)om;
                    if (tom.getBinaryAttachment()) {
                        binaryAttach = true;
                        if (MTOM_STR.equalsIgnoreCase(tom.getAttachmentType())) {
                            attachmentType = MTOM_STR;
                        }
                        else {
                            attachmentType = SWAREF_STR;
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
                                    if (MTOM_STR.equalsIgnoreCase(tom.getAttachmentType())) {
                                        attachmentType = MTOM_STR;
                                    }
                                    else {
                                        attachmentType = SWAREF_STR;
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
                if (attachmentType.equals(SWAREF_STR)) {
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
            if (!isPk) {
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
        if (isPk) {
            xmlField.setRequired(true);
        }
        return xdm;
    }

    protected NamingConventionTransformer setUpCustomTransformer(String tableName, NamingConventionTransformer nct) {
        // need custom NamingConventionTransformer so that returnType/tableName is used verbatim
        NamingConventionTransformer customNct = new DefaultNamingConventionTransformer() {
            @Override
            protected boolean isDefaultTransformer() {
                return false;
            }
            @Override
            public String generateSchemaAlias(String tableName) {
                return tableName;
            }
        };
        ((DefaultNamingConventionTransformer)customNct).setNextTransformer(nct);
        return customNct;
    }

    protected void setUpFindQueries(String tableName, RelationalDescriptor desc) {
        ReadObjectQuery roq = new ReadObjectQuery();
        String generatedJavaClassName = getGeneratedJavaClassName(tableName, dbwsBuilder.getProjectName());
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

    /**
     * Complete project configuration.  Build descriptors for secondary SQL and
     * complex arguments.  Set the projects on the DBWSBuilder instance.
     */
    protected void finishUpProjects(Project orProject, Project oxProject) {
        // handle secondary SQL
        for (OperationModel opModel : dbwsBuilder.operations) {
            if (opModel.isSQLOperation() && ((SQLOperationModel)opModel).hasBuildSql()) {
                addToOROXProjectsForSecondarySql((SQLOperationModel)opModel, orProject, oxProject,
                    nct);
            }
        }
        // handle complex procedure arguments
        for (ProcedureType procType : dbStoredProcedures) {
            DbStoredProcedureNameAndModel nameAndModel = dbStoredProcedure2QueryName.get(procType);
            if (nameAndModel != null && (nameAndModel.procOpModel.isPLSQLProcedureOperation()
                    || nameAndModel.procOpModel.isAdvancedJDBCProcedureOperation())) {
                // build list of arguments to process (i.e. build descriptors for)
                List<ArgumentType> args = new ArrayList<ArgumentType>();
                // return argument
                if (procType.isFunction()) {
                    // assumes that a function MUST have a return type
                    args.add(((FunctionType) procType).getReturnArgument());
                }
                // IN/OUT/INOUT arguments
                for (ArgumentType argument : procType.getArguments()) {
                    args.add(argument);
                }
                boolean hasComplexArgs = org.eclipse.persistence.tools.dbws.Util.hasComplexArgs(args);
                // set 'complex' flag on model to indicate complex arg processing is required
                nameAndModel.procOpModel.setHasComplexArguments(hasComplexArgs);
                if (hasComplexArgs || org.eclipse.persistence.tools.dbws.Util.hasPLSQLScalarArgs(args)) {
                    // subclasses are responsible for processing complex arguments
                    addToOROXProjectsForComplexArgs(args, orProject, oxProject, nameAndModel.procOpModel);
                    // build a query for this ProcedureType as it has one or more complex arguments
                    buildQueryForProcedureType(procType, orProject, oxProject, nameAndModel.procOpModel, nameAndModel.name);
                }
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
        dbwsBuilder.setOrProject(orProject);
        dbwsBuilder.setOxProject(oxProject);
    }

    protected static List<DbColumn> buildDbColumns(Connection connection, String secondarySql) {
        List<DbColumn> columns = null;
        ResultSetMetaData rsMetaData = getResultSetMetadataForSecondarySQL(connection, secondarySql);
        if (rsMetaData != null) {
            int columnCount = 0;
            try {
                columnCount = rsMetaData.getColumnCount();
            }
            catch (SQLException sqlException) {
                throw new IllegalStateException("failure retrieving columnCount", sqlException);
            }
            if (columnCount > 0) {
                columns = new ArrayList<DbColumn>(columnCount);
                try {
                    for (int i = 1; i <= columnCount; i++) {
                        String dbColumnName = rsMetaData.getColumnLabel(i);
                        DbColumn dbColumn = new DbColumn(dbColumnName);
                        //dbColumn.setOrdinalPosition(i);
                        dbColumn.setJDBCType(rsMetaData.getColumnType(i));
                        dbColumn.setJDBCTypeName(rsMetaData.getColumnTypeName(i));
                        int dbPrecision = rsMetaData.getPrecision(i);
                        int dbScale = rsMetaData.getScale(i);
                        dbColumn.setDataType(buildTypeForJDBCType(dbColumn.getJDBCType(),
                            dbPrecision, dbScale));
                        if (rsMetaData.isNullable(i) == ResultSetMetaData.columnNullable) {
                            dbColumn.unSetNotNull();
                        }
                        else {
                            dbColumn.setNotNull();
                        }
                        columns.add(dbColumn);
                    }
                }
                catch (SQLException sqlException) {
                    throw new IllegalStateException("failure retrieving column information",
                        sqlException);
                }
            }
        }
        return columns;
    }

    static void buildDbStoredProcedure2QueryNameMap(
        Map<ProcedureType, DbStoredProcedureNameAndModel> dbStoredProcedure2QueryName,
        List<ProcedureType> dbStoredProcedures, ArrayList<OperationModel> operations) {
        for (OperationModel opModel : operations) {
            if (opModel.isProcedureOperation()) {
                ProcedureOperationModel procOpModel = (ProcedureOperationModel)opModel;
                // scan all the dbStoredProcedures for matches
                List<ProcedureType> matches = new ArrayList<ProcedureType>();
                String modelCatalogPattern = escapePunctuation(procOpModel.getCatalogPattern());
                if (TOPLEVEL.equalsIgnoreCase(modelCatalogPattern)) {
                    modelCatalogPattern = null;
                }
                String modelSchemaPattern =
                    escapePunctuation(procOpModel.getSchemaPattern());
                String modelProcedureNamePattern =
                    escapePunctuation(procOpModel.getProcedurePattern());
                for (ProcedureType storedProc : dbStoredProcedures) {
                    boolean procedureNameMatch =
                        sqlMatch(modelProcedureNamePattern, storedProc.getProcedureName());
                    if (storedProc.getCatalogName() == null || modelCatalogPattern == null) {
                        if (modelSchemaPattern == null || modelSchemaPattern.length() == 0) {
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
                            sqlMatch(modelCatalogPattern, storedProc.getCatalogName());
                        if (modelSchemaPattern == null || modelSchemaPattern.length() == 0) {
                            // determined by catalog * procedureName
                            if (catalogMatch && procedureNameMatch) {
                                matches.add(storedProc);
                            }
                        }
                        // combination of catalog, schema & procedureName
                        else if (sqlMatch(modelSchemaPattern, storedProc.getSchema()) &&
                            catalogMatch && procedureNameMatch) {
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

    /**
     * Return the type name to be used for a given database type.  The given
     * DatabaseType's typeName attribute is typically returned, however, in
     * some cases special handling may be required.  For example, in the 
     * case of a NumericType instance, "DECIMAL" should be used for the
     * type name.
     */
    protected static String getTypeNameForDatabaseType(DatabaseType dataType) {
        String typeName = dataType.getTypeName();
        if (dataType instanceof NumericType) {
            NumericType numericDataType = (NumericType)dataType;
            if (numericDataType.getScale() > 0) {
                typeName = DECIMAL_STR;
            }
        }
        return typeName;
    }

    /**
     * Return a DatabaseType instance for a given JDCBType.  If applicable, precision
     * and scale values will be applied.  The default type instance will be
     * VarChar2Type.
     *
     */
    protected static DatabaseType buildTypeForJDBCType(int jdbcType, int precision, int scale) {
        DatabaseType type = new VarChar2Type();
        switch (jdbcType) {
            case Types.BINARY:
                type = new BinaryType();
                break;
            case Types.BLOB:
                type = new BlobType();
                break;
            case Types.CHAR:
                type = new CharType();
                break;
            case Types.CLOB:
                type = new ClobType();
                break;
            case Types.DATE:
                type = ScalarDatabaseTypeEnum.DATE_TYPE;
                break;
            case Types.BIGINT:
                type = ScalarDatabaseTypeEnum.BIGINT_TYPE;
                break;
            case Types.DECIMAL:
            case Types.NUMERIC:
                type = new DecimalType(precision, scale);
                break;
            case Types.DOUBLE:
                type = new DoubleType(precision, scale);
                break;
            case Types.FLOAT:
                type = new FloatType(precision, scale);
                break;
            case Types.LONGVARBINARY:
                type = new LongRawType();
                break;
            case Types.NCHAR:
                type = new NCharType();
                break;
            case Types.NCLOB:
                type = new NClobType();
                break;
            case Types.REAL:
                type = new RealType(precision, scale);
                break;
            case Types.TIME:
                type = ScalarDatabaseTypeEnum.TIME_TYPE;
                break;
            case Types.TIMESTAMP:
                type = ScalarDatabaseTypeEnum.TIMESTAMP_TYPE;
                break;
            case Types.VARBINARY:
                type = new RawType();
                break;
        }
        return type;
    }

    /**
     * Build a org.eclipse.persistence.internal.helper.DatabaseType instance from an
     * org.eclipse.persistence.tools.oracleddl.metadata.DatabaseType instance.
     */
    protected org.eclipse.persistence.internal.helper.DatabaseType buildDatabaseTypeFromMetadataType(DatabaseType dType) {
        return buildDatabaseTypeFromMetadataType(dType, null);
    }
    /**
     * Build a org.eclipse.persistence.internal.helper.DatabaseType  instance  from an
     * org.eclipse.persistence.tools.oracleddl.metadata.DatabaseType instance.  In the
     * the case of PLSQL Packages, the catalog (package) name can be passed in as well.
     */
    protected org.eclipse.persistence.internal.helper.DatabaseType buildDatabaseTypeFromMetadataType(DatabaseType dType, String catalog) {
        if (dType instanceof ArgumentType) {
            dType = ((ArgumentType) dType).getDataType();
        }
        // composite types
        if (dType.isComposite()) {
            String typeName = dType.getTypeName();
            String compatibleType = dType.getTypeName();
            String javaTypeName = (dType.getTypeName()).toLowerCase();

            // handle PL/SQL types
            if (dType instanceof PLSQLType) {
                if (catalog != null) {
                    typeName = (catalog + ".").concat(typeName);
                    compatibleType = (catalog + "_").concat(compatibleType);
                    javaTypeName = (catalog.toLowerCase() + ".").concat(javaTypeName);
                }
                // handle PL/SQL record
                if (dType instanceof PLSQLRecordType) {
                    PLSQLrecord plsqlRec = new PLSQLrecord();
                    plsqlRec.setTypeName(typeName);
                    plsqlRec.setCompatibleType(compatibleType);
                    plsqlRec.setJavaTypeName(javaTypeName);
                    // process fields
                    for (FieldType fld : ((PLSQLRecordType)dType).getFields()) {
                        if (fld.getDataType() instanceof PrecisionType) {
                            PrecisionType precisionType = (PrecisionType) fld.getDataType();
                            plsqlRec.addField(fld.getFieldName(), buildDatabaseTypeFromMetadataType(precisionType),
                                    (int)precisionType.getPrecision(), (int)precisionType.getScale());
                        } else if (fld.getDataType() instanceof SizedType) {
                            SizedType sizedType = (SizedType) fld.getDataType();
                            plsqlRec.addField(fld.getFieldName(), buildDatabaseTypeFromMetadataType(sizedType), (int)sizedType.getSize());
                        } else {
                            plsqlRec.addField(fld.getFieldName(), buildDatabaseTypeFromMetadataType(fld.getDataType(), catalog));
                        }
                    }
                    return plsqlRec;
                }
                // assumes PL/SQL collection
                PLSQLCollection plsqlCollection = new PLSQLCollection();
                plsqlCollection.setTypeName(typeName);
                plsqlCollection.setCompatibleType(compatibleType);
                plsqlCollection.setJavaTypeName(javaTypeName + COLLECTION_WRAPPER_SUFFIX);
                plsqlCollection.setNestedType(buildDatabaseTypeFromMetadataType(((PLSQLCollectionType) dType).getNestedType(), catalog));
                return plsqlCollection;
            }
            // handle advanced Oracle types
            if (dType instanceof VArrayType) {
                OracleArrayType varray = new OracleArrayType();
                varray.setTypeName(typeName);
                varray.setCompatibleType(compatibleType);
                varray.setJavaTypeName(javaTypeName + COLLECTION_WRAPPER_SUFFIX);
                varray.setNestedType(buildDatabaseTypeFromMetadataType(((VArrayType) dType).getEnclosedType(), null));
                return varray;
            }
            if (dType instanceof ObjectType) {
                OracleObjectType objType = new OracleObjectType();
                objType.setTypeName(typeName);
                objType.setCompatibleType(compatibleType);
                objType.setJavaTypeName(javaTypeName);
                Map<String, org.eclipse.persistence.internal.helper.DatabaseType> fields = objType.getFields();
                ObjectType oType = (ObjectType) dType;
                for (FieldType field : oType.getFields()) {
                    fields.put(field.getFieldName(), buildDatabaseTypeFromMetadataType(field.getDataType()));
                }
                return objType;
            }
            // TODO - return what here?
            return null;
        } else if (dType instanceof ScalarDatabaseTypeEnum) {
        	// handle PL/SQL Boolean
        	if (dType == ScalarDatabaseTypeEnum.BOOLEAN_TYPE) {
        		return OraclePLSQLTypes.PLSQLBoolean;
        	}
        }
        // scalar types
        return JDBCTypes.getDatabaseTypeForCode(org.eclipse.persistence.tools.dbws.Util.getJDBCTypeFromTypeName(dType.getTypeName()));
    }

    /**
     * Get the attribute class for a given DatabaseType.
     */
    public static Class<?> getAttributeClassForDatabaseType(DatabaseType dbType) {
        if (!dbType.isComposite()) {
            String typeName = dbType.getTypeName();
            if (NUMBER_STR.equals(typeName) || "NUMERIC".equals(typeName)) {
                return BIGDECIMAL;
            }
            if (INTEGER_STR.equals(typeName)) {
                return INTEGER;
            }
            if (BOOLEAN_STR.equals(typeName)) {
                return BOOLEAN;
            }
            if (DATE_STR.equals(typeName)) {
                return JavaSqlDate_Class;
            }
            // TODO - more conversions
        }
        return STRING;
    }
    
    /**
     * Apply SimpleXMLFormat if 'isSimpleXMLFormat' is true.  The SimpleXMLFormat is
     * configured based on the given ProcedureOperationModel's simpleXMLFormatTag
     * and xmlTag (if set) and set on the given Result.  A descriptor is also added
     * to the OXProject if none exists.
     */
    protected void handleSimpleXMLFormat(boolean isSimpleXMLFormat, Result result, ProcedureOperationModel procedureOperationModel) {
        if (isSimpleXMLFormat) {
            SimpleXMLFormat sxf = new SimpleXMLFormat();
            String simpleXMLFormatTag = procedureOperationModel.getSimpleXMLFormatTag();
            if (simpleXMLFormatTag != null && simpleXMLFormatTag.length() > 0) {
                sxf.setSimpleXMLFormatTag(simpleXMLFormatTag);
            }
            String xmlTag = procedureOperationModel.getXmlTag();
            if (xmlTag != null && xmlTag.length() > 0) {
                sxf.setXMLTag(xmlTag);
            }

            result.setSimpleXMLFormat(sxf);
            // check to see if the O-X project needs descriptor for SimpleXMLFormat
            if (dbwsBuilder.getOxProject().getDescriptorForAlias(DEFAULT_SIMPLE_XML_FORMAT_TAG) == null) {
                SimpleXMLFormatProject sxfProject = new SimpleXMLFormatProject();
                dbwsBuilder.getOxProject().addDescriptor(sxfProject.buildXRRowSetModelDescriptor());
            }
        }
    }
    
    /**
     * Perform any additional actions required for operation creation
     * for both PL/SQL and non-PL/SQL operation models.
     */
    protected void finishProcedureOperation() {
        // check to see if the schema requires sxfType to be added
        if (requiresSimpleXMLFormat(dbwsBuilder.getXrServiceModel()) &&
            dbwsBuilder.getSchema().getTopLevelElements().get("simple-xml-format") == null) {
            addSimpleXMLFormat(dbwsBuilder.getSchema());
        }
    }
}