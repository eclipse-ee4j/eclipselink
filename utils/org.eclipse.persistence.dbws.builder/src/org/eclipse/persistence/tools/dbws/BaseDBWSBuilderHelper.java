/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Mike Norman - from Proof-of-concept, become production code
package org.eclipse.persistence.tools.dbws;

//javase imports
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.FINEST;
import static java.util.logging.Level.SEVERE;
import static org.eclipse.persistence.internal.helper.ClassConstants.APBYTE;
import static org.eclipse.persistence.internal.oxm.Constants.BASE_64_BINARY_QNAME;
import static org.eclipse.persistence.internal.oxm.Constants.EMPTY_STRING;
import static org.eclipse.persistence.internal.oxm.Constants.XML_MIME_URL;
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
import static org.eclipse.persistence.oxm.mappings.nullpolicy.XMLNullRepresentationType.XSI_NIL;
import static org.eclipse.persistence.tools.dbws.NamingConventionTransformer.ElementStyle.ATTRIBUTE;
import static org.eclipse.persistence.tools.dbws.NamingConventionTransformer.ElementStyle.ELEMENT;
import static org.eclipse.persistence.tools.dbws.NamingConventionTransformer.ElementStyle.NONE;
import static org.eclipse.persistence.tools.dbws.Util.AT_SIGN;
import static org.eclipse.persistence.tools.dbws.Util.CHAR_STR;
import static org.eclipse.persistence.tools.dbws.Util.CLOSE_BRACKET;
import static org.eclipse.persistence.tools.dbws.Util.CLOSE_SQUARE_BRACKET;
import static org.eclipse.persistence.tools.dbws.Util.COMMA;
import static org.eclipse.persistence.tools.dbws.Util.CREATE_OPERATION_NAME;
import static org.eclipse.persistence.tools.dbws.Util.DBWS_PROVIDER_CLASS_FILE;
import static org.eclipse.persistence.tools.dbws.Util.DBWS_PROVIDER_SOURCE_FILE;
import static org.eclipse.persistence.tools.dbws.Util.DECIMAL_STR;
import static org.eclipse.persistence.tools.dbws.Util.DOT;
import static org.eclipse.persistence.tools.dbws.Util.FINDALL_QUERYNAME;
import static org.eclipse.persistence.tools.dbws.Util.OPEN_BRACKET;
import static org.eclipse.persistence.tools.dbws.Util.OPEN_SQUARE_BRACKET;
import static org.eclipse.persistence.tools.dbws.Util.PERCENT;
import static org.eclipse.persistence.tools.dbws.Util.REMOVE_OPERATION_NAME;
import static org.eclipse.persistence.tools.dbws.Util.ROWTYPE_STR;
import static org.eclipse.persistence.tools.dbws.Util.SINGLE_SPACE;
import static org.eclipse.persistence.tools.dbws.Util.SLASH_TEXT;
import static org.eclipse.persistence.tools.dbws.Util.THE_INSTANCE_NAME;
import static org.eclipse.persistence.tools.dbws.Util.TYPE_STR;
import static org.eclipse.persistence.tools.dbws.Util.UNDERSCORE;
import static org.eclipse.persistence.tools.dbws.Util.UPDATE_OPERATION_NAME;
import static org.eclipse.persistence.tools.dbws.Util.WSI_SWAREF_PREFIX;
import static org.eclipse.persistence.tools.dbws.Util.WSI_SWAREF_URI;
import static org.eclipse.persistence.tools.dbws.Util.WSI_SWAREF_XSD;
import static org.eclipse.persistence.tools.dbws.Util.WSI_SWAREF_XSD_FILE;
import static org.eclipse.persistence.tools.dbws.Util.XML_MIME_PREFIX;
import static org.eclipse.persistence.tools.dbws.Util.addSimpleXMLFormat;
import static org.eclipse.persistence.tools.dbws.Util.buildORDescriptor;
import static org.eclipse.persistence.tools.dbws.Util.buildOXDescriptor;
import static org.eclipse.persistence.tools.dbws.Util.buildTypeForJDBCType;
import static org.eclipse.persistence.tools.dbws.Util.getGeneratedJavaClassName;
import static org.eclipse.persistence.tools.dbws.Util.getGeneratedWrapperClassName;
import static org.eclipse.persistence.tools.dbws.Util.getJDBCTypeFromTypeName;
import static org.eclipse.persistence.tools.dbws.Util.getJDBCTypeNameFromType;
import static org.eclipse.persistence.tools.dbws.Util.getXMLTypeFromJDBCType;
import static org.eclipse.persistence.tools.dbws.Util.hasPLSQLArgs;
import static org.eclipse.persistence.tools.dbws.Util.isNullStream;
import static org.eclipse.persistence.tools.dbws.Util.requiresSimpleXMLFormat;
import static org.eclipse.persistence.tools.dbws.Util.sqlMatch;

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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

//java eXtension imports
import javax.wsdl.WSDLException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

//EclipseLink imports
import org.eclipse.persistence.dbws.DBWSModelProject;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.exceptions.DBWSException;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.internal.descriptors.DescriptorHelper;
import org.eclipse.persistence.internal.helper.ComplexDatabaseType;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappingsWriter;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.internal.oxm.mappings.Descriptor;
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
import org.eclipse.persistence.internal.xr.XmlBindingsModel;
import org.eclipse.persistence.internal.xr.sxf.SimpleXMLFormat;
import org.eclipse.persistence.internal.xr.sxf.SimpleXMLFormatProject;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
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
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLCursor;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLrecord;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.factories.XMLProjectReader;
import org.eclipse.persistence.sessions.factories.XMLProjectWriter;
import org.eclipse.persistence.tools.dbws.NamingConventionTransformer.ElementStyle;
import org.eclipse.persistence.tools.dbws.jdbc.DbColumn;
import org.eclipse.persistence.tools.dbws.jdbc.DbTable;
//DDL parser imports
import org.eclipse.persistence.tools.oracleddl.metadata.ArgumentType;
import org.eclipse.persistence.tools.oracleddl.metadata.CompositeDatabaseType;
import org.eclipse.persistence.tools.oracleddl.metadata.DatabaseType;
import org.eclipse.persistence.tools.oracleddl.metadata.FieldType;
import org.eclipse.persistence.tools.oracleddl.metadata.FunctionType;
import org.eclipse.persistence.tools.oracleddl.metadata.NumericType;
import org.eclipse.persistence.tools.oracleddl.metadata.ObjectTableType;
import org.eclipse.persistence.tools.oracleddl.metadata.ObjectType;
import org.eclipse.persistence.tools.oracleddl.metadata.PLSQLCollectionType;
import org.eclipse.persistence.tools.oracleddl.metadata.PLSQLCursorType;
import org.eclipse.persistence.tools.oracleddl.metadata.PLSQLPackageType;
import org.eclipse.persistence.tools.oracleddl.metadata.PLSQLRecordType;
import org.eclipse.persistence.tools.oracleddl.metadata.PLSQLType;
import org.eclipse.persistence.tools.oracleddl.metadata.PrecisionType;
import org.eclipse.persistence.tools.oracleddl.metadata.ProcedureType;
import org.eclipse.persistence.tools.oracleddl.metadata.ROWTYPEType;
import org.eclipse.persistence.tools.oracleddl.metadata.ScalarDatabaseType;
import org.eclipse.persistence.tools.oracleddl.metadata.SizedType;
import org.eclipse.persistence.tools.oracleddl.metadata.TYPEType;
import org.eclipse.persistence.tools.oracleddl.metadata.TableType;
import org.eclipse.persistence.tools.oracleddl.metadata.VArrayType;
import org.eclipse.persistence.tools.oracleddl.metadata.visit.EnclosedTypeVisitor;

public abstract class BaseDBWSBuilderHelper {

    public static final String ITEM_MAPPING_NAME = "item";
    public static final String ITEMS_MAPPING_ATTRIBUTE_NAME = "items";
    public static final String ITEMS_MAPPING_FIELD_NAME = "ITEMS";
    public static final String MTOM_STR = "MTOM";
    public static final String SWAREF_STR = "SWAREF";
    public static final String NO_TABLE_MSG = "No tables were found matching the following: ";
    public static final String NO_PROC_MSG = "No procedures were found matching the following: ";
    public static final String OXM_MARSHAL_EX_MSG = "An exception occurred while attempting to marshal the OXM bindings file";
    public static final String ORM_MARSHAL_EX_MSG = "An exception occurred while attempting to marshal the ORM mappings file";

    public static final String SELECT_FROM_STR = "SELECT * FROM ";
    public static final String WHERE_STR = " WHERE ";
    public static final String AND_STR = " AND ";
    public static final String SET_STR = " SET ";
    public static final String VALUES_STR = " VALUES ";
    public static final String UPDATE_STR = "UPDATE ";
    public static final String INSERT_STR = "INSERT INTO ";
    public static final String DELETE_STR = "DELETE FROM ";
    public static final String COMMA_SPACE_STR = COMMA + SINGLE_SPACE;
    public static final String EQUALS_BINDING1_STR = " = ?1";
    public static final String EQUALS_BINDING_STR = " = ?";
    public static final String TIMESTAMP_CLASS = "oracle.sql.TIMESTAMP";
    public static final String USER_STR = "user";
    public static final String PASSWORD_STR = "password";
    public static final String SIMPLEXML_STR = "simple-xml-format";

    protected List<TableType> dbTables = new ArrayList<TableType>();
    protected List<ProcedureType> dbStoredProcedures = new ArrayList<ProcedureType>();
    protected DBWSBuilder dbwsBuilder;
    protected XMLSessionConfigProject_11_1_1 sessionConfigProject = new XMLSessionConfigProject_11_1_1();
    protected NamingConventionTransformer nct;
    protected ObjectPersistenceWorkbenchXMLProject workbenchXMLProject = new ObjectPersistenceWorkbenchXMLProject();

    protected Map<String, ClassDescriptor> createdORDescriptors = new HashMap<String, ClassDescriptor>();
    protected List<String> referencedORDescriptors = new ArrayList<String>();
    protected List<CompositeDatabaseType> complextypes = new ArrayList<CompositeDatabaseType>();

    protected Map<String, Map<String, String>> crudOps = new HashMap<String, Map<String, String>>();

    public BaseDBWSBuilderHelper(DBWSBuilder dbwsBuilder) {
        this.dbwsBuilder = dbwsBuilder;
    }

    public List<TableType> getDbTables() {
        return dbTables;
    }

    public List<ProcedureType> getDbStoredProcedures() {
        return dbStoredProcedures;
    }

    //abstract methods - platform-specific behavior in JDBCHelper, OracleHelper
    public abstract boolean hasTables();

    protected abstract List<TableType> loadTables(List<String> catalogPatterns, List<String> schemaPatterns, List<String> tableNamePatterns);

    protected abstract List<ProcedureType> loadProcedures(List<String> catalogPatterns, List<String> schemaPatterns, List<String> procedureNamePatterns);

    protected abstract void addToOROXProjectsForComplexTypes(List<CompositeDatabaseType> types, Project orProject, Project oxProject);

    protected abstract void buildQueryForProcedureType(ProcedureType procType, Project orProject, Project oxProject, ProcedureOperationModel opModel, boolean hasComplexArgs);

    protected void addToOROXProjectsForBuildSql(ModelWithBuildSql modelWithBuildSql, Project orProject, Project oxProject, NamingConventionTransformer nct) {
        List<DbColumn> columns = buildDbColumns(dbwsBuilder.getConnection(), modelWithBuildSql.getBuildSql());
        String schemaAlias = modelWithBuildSql.getReturnType();
        String tableName = schemaAlias;
        NamingConventionTransformer customNct = setUpCustomTransformer(tableName, nct);
        RelationalDescriptor desc = buildORDescriptor(tableName, dbwsBuilder.getProjectName(), null, customNct);
        createdORDescriptors.put(desc.getJavaClassName(), desc);

        desc.descriptorIsAggregate();
        orProject.addDescriptor(desc);
        XMLDescriptor xdesc = buildOXDescriptor(desc.getAlias(), schemaAlias, desc.getJavaClassName(), dbwsBuilder.getTargetNamespace());
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
                DirectToFieldMapping orFieldMapping = buildORFieldMappingFromColumn(dbColumn, desc, dbwsBuilder.getDatabasePlatform(), nct);
                desc.addMapping(orFieldMapping);
                XMLDirectMapping oxFieldMapping = buildOXFieldMappingFromColumn(dbColumn, dbwsBuilder.getDatabasePlatform(), nct);
                if (oxFieldMapping instanceof XMLBinaryDataMapping) {
                    xdesc.getNamespaceResolver().put(XML_MIME_PREFIX, XML_MIME_URL);
                }
                xdesc.addMapping(oxFieldMapping);
            } else {
                dbwsBuilder.logMessage(SEVERE, "Duplicate ResultSet columns not supported '" + columnName + "'");
                throw new RuntimeException("Duplicate ResultSet columns not supported");
            }
        }
    }

    /**
     * Uses a custom visitor to traverse each procedure/function argument and build
     * a list of required Types.  Only on instance of a given type will exist in
     * the list.
     *
     */
    public List<CompositeDatabaseType> buildTypesList(List<OperationModel> operations) {
        EnclosedTypeVisitor etVisitor = new EnclosedTypeVisitor();
        for (OperationModel opModel : operations) {
            if (opModel.isProcedureOperation()) {
                ProcedureOperationModel procedureOperation = (ProcedureOperationModel)opModel;
                if (procedureOperation.isPLSQLProcedureOperation() || procedureOperation.isAdvancedJDBCProcedureOperation()) {
                    for (ProcedureType procType : procedureOperation.getDbStoredProcedures()) {
                        // build list of arguments to process (i.e. build descriptors for)
                        List<ArgumentType> args = new ArrayList<ArgumentType>();
                        // return argument
                        if (procType.isFunctionType()) {
                            // assumes that a function MUST have a return type
                            args.add(((FunctionType)procType).getReturnArgument());
                        }
                        args.addAll(procType.getArguments());
                        // now visit each argument
                        for (ArgumentType arg : args) {
                            // handle ROWTYPETypes
                            if (arg.getEnclosedType().isROWTYPEType()) {
                                ROWTYPEType rType = (ROWTYPEType) arg.getEnclosedType();
                                TableType tableType = (TableType) rType.getEnclosedType();
                                PLSQLRecordType plsqlRec = new PLSQLRecordType(rType.getTypeName());
                                plsqlRec.setParentType(new PLSQLPackageType());
                                for (FieldType col : tableType.getColumns()) {
                                    FieldType ft = new FieldType(col.getFieldName());
                                    ft.setEnclosedType(col.getEnclosedType());
                                    plsqlRec.addField(ft);
                                }
                                arg.setEnclosedType(plsqlRec);
                            }
                            // now visit each, adding types (only one instance of each) to the list
                            if (arg.isComposite()) {
                                etVisitor.visit((CompositeDatabaseType) arg);
                            }
                        }
                    }
                }
            }
        }

        // gather Complex types to hand into XMLEntityMappingsGenerator
        List<CompositeDatabaseType> types = etVisitor.getCompositeDatabaseTypes();
        for (CompositeDatabaseType type : types) {
            complextypes.add(type);
        }
        return etVisitor.getCompositeDatabaseTypes();
    }

    /**
     * Builds OR/OX projects, descriptors {@literal &} mappings.  This method can be
     * used when no complex types exist, and only table(s) and secondary
     * SQL will be used when building.
     */
    public void buildOROXProjects(NamingConventionTransformer nct) {
        buildOROXProjects(nct, new ArrayList<CompositeDatabaseType>());
    }

    /**
     * Builds OR/OX projects, descriptors {@literal &} mappings, based on a given list
     * of types, as well as table(s) and secondary SQL.
     */
    public void buildOROXProjects(NamingConventionTransformer nct, List<CompositeDatabaseType> types) {
        this.nct = nct; // save for later
        String projectName = dbwsBuilder.getProjectName();
        Project orProject = new Project();
        orProject.setName(projectName + UNDERSCORE + DBWS_OR_LABEL);
        Project oxProject = null;
        if (dbTables.isEmpty() && !dbwsBuilder.hasBuildSqlOperations()) {
            dbwsBuilder.logMessage(FINEST, "No tables specified");
            oxProject = new SimpleXMLFormatProject();
        } else {
            oxProject = new Project();
        }
        oxProject.setName(projectName + UNDERSCORE + DBWS_OX_LABEL);
        for (TableType dbTable : dbTables) {
            String tableName = dbTable.getTableName();
            RelationalDescriptor desc = buildORDescriptor(tableName, dbwsBuilder.getProjectName(), dbwsBuilder.requireCRUDOperations, nct);
            orProject.addDescriptor(desc);
            XMLDescriptor xdesc = buildOXDescriptor(tableName, dbwsBuilder.getProjectName(), dbwsBuilder.getTargetNamespace(), nct);
            oxProject.addDescriptor(xdesc);
            for (FieldType dbColumn : dbTable.getColumns()) {
                String columnName = dbColumn.getFieldName();
                ElementStyle style = nct.styleForElement(columnName);
                if (style == NONE) {
                    continue;
                }
                dbwsBuilder.logMessage(FINE, "Building mappings for " + tableName + DOT + columnName);
                DirectToFieldMapping orFieldMapping = buildORFieldMappingFromColumn(dbColumn, desc, dbwsBuilder.getDatabasePlatform(), nct);
                desc.addMapping(orFieldMapping);
                XMLDirectMapping oxFieldMapping = buildOXFieldMappingFromColumn(dbColumn, dbwsBuilder.getDatabasePlatform(), nct);
                if (oxFieldMapping instanceof XMLBinaryDataMapping) {
                    xdesc.getNamespaceResolver().put(XML_MIME_PREFIX, XML_MIME_URL);
                }
                xdesc.addMapping(oxFieldMapping);
                // check for switch from Byte[] to byte[]
                if (oxFieldMapping.getAttributeClassificationName() == APBYTE.getName()) {
                    orFieldMapping.setAttributeClassificationName(APBYTE.getName());
                }
          }
          setUpFindQueries(nct, tableName, desc);
        }
        finishUpProjects(orProject, oxProject, types);
    }

    /**
     * Complete project configuration.  Build descriptors for secondary SQL
     * and complex arguments. Set the projects on the DBWSBuilder instance.
     */
    protected void finishUpProjects(Project orProject, Project oxProject, List<CompositeDatabaseType> typeList) {
        // handle build SQL
        for (OperationModel opModel : dbwsBuilder.operations) {
            if (opModel.hasBuildSql()) {
                addToOROXProjectsForBuildSql((ModelWithBuildSql)opModel, orProject, oxProject, nct);
            }
        }
        // create OR/OX projects, descriptors/mappings for the types
        addToOROXProjectsForComplexTypes(typeList, orProject, oxProject);

        // build queries for procedures with complex arguments
        for (OperationModel opModel : dbwsBuilder.operations) {
            if (opModel.isProcedureOperation()) {
                ProcedureOperationModel procedureOperation = (ProcedureOperationModel)opModel;
                    for (ProcedureType procType : procedureOperation.getDbStoredProcedures()) {
                        buildQueryForProcedureType(procType, orProject, oxProject, procedureOperation,
                                hasPLSQLArgs(getArgumentListForProcedureType(procType)));
                    }
            }
        }

        // set aggregate OR descriptors where appropriate
        for (String refClassName : referencedORDescriptors) {
            ClassDescriptor cdesc = createdORDescriptors.get(refClassName);
            // should not be null, but check to avoid an exception
            if (cdesc != null) {
                cdesc.descriptorIsAggregate();
            }
        }

        DatabaseLogin databaseLogin = new DatabaseLogin();
        databaseLogin.removeProperty(USER_STR);
        databaseLogin.removeProperty(PASSWORD_STR);
        databaseLogin.setDriverClassName(null);
        databaseLogin.setConnectionString(null);
        orProject.setLogin(databaseLogin);
        XMLLogin xmlLogin = new XMLLogin();
        xmlLogin.setDatasourcePlatform(new DOMPlatform());
        xmlLogin.getProperties().remove(USER_STR);
        xmlLogin.getProperties().remove(PASSWORD_STR);
        oxProject.setLogin(xmlLogin);
        dbwsBuilder.setOrProject(orProject);
        dbwsBuilder.setOxProject(oxProject);
    }

    protected DirectToFieldMapping buildORFieldMappingFromColumn(FieldType dbColumn,
        RelationalDescriptor desc, DatabasePlatform databasePlatform,
        NamingConventionTransformer nct) {
        String columnName = dbColumn.getFieldName();
        DatabaseType dataType = dbColumn.getEnclosedType();
        String typeName = getTypeNameForDatabaseType(dataType);
        int jdbcType = getJDBCTypeFromTypeName(typeName);
        String dmdTypeName = getJDBCTypeNameFromType(jdbcType);
        Class<?> attributeClass = null;
        if (CHAR_STR.equalsIgnoreCase(dmdTypeName) && dbColumn.getEnclosedType().isSizedType()) {
            SizedType sizedType = (SizedType)dbColumn.getEnclosedType();
            if (sizedType.getSize() == 1) {
                attributeClass = Character.class;
            } else {
                attributeClass = String.class;
            }
        } else {
            attributeClass = getClassFromJDBCType(dmdTypeName.toUpperCase(), databasePlatform);
        }
        //https://bugs.eclipse.org/bugs/show_bug.cgi?id=359130
        //problem with java.sql.Timestamp conversion and Oracle11 platform
        if (attributeClass.getName().contains(TIMESTAMP_CLASS)) {
            attributeClass = java.sql.Timestamp.class;
        }
        DirectToFieldMapping dtfm = setUpDirectToFieldMapping(desc, columnName, nct, attributeClass, jdbcType, dbColumn.pk());
        return dtfm;
    }

    protected XMLDirectMapping buildOXFieldMappingFromColumn(FieldType dbColumn, DatabasePlatform databasePlatform, NamingConventionTransformer nct) {
        String columnName = dbColumn.getFieldName();
        DatabaseType dataType = dbColumn.getEnclosedType();
        String typeName = getTypeNameForDatabaseType(dataType);
        int jdbcType = getJDBCTypeFromTypeName(typeName);
        String dmdTypeName = getJDBCTypeNameFromType(jdbcType);
        QName qName = getXMLTypeFromJDBCType(jdbcType);
        Class<?> attributeClass;
        if (CHAR_STR.equalsIgnoreCase(dmdTypeName) && dbColumn.getEnclosedType().isSizedType()) {
            SizedType sizedType = (SizedType)dbColumn.getEnclosedType();
            if (sizedType.getSize() == 1) {
                attributeClass = Character.class;
            } else {
                attributeClass = String.class;
            }
        } else {
            attributeClass = getClassFromJDBCType(dmdTypeName.toUpperCase(), databasePlatform);
        }
        //https://bugs.eclipse.org/bugs/show_bug.cgi?id=359130
        //problem with conversion and Oracle11 platform
        if (attributeClass.getName().contains(TIMESTAMP_CLASS)) {
            attributeClass = java.sql.Timestamp.class;
        } else if (attributeClass.getName().contains(java.lang.Character[].class.getName())) {
            attributeClass = java.lang.String.class;
        }
        XMLDirectMapping xdm = setUpXMLDirectMapping(columnName, qName, nct, attributeClass, jdbcType, dbColumn.pk());
        return xdm;
    }

    /**
     * Gather table and procedure information based on TableOperation
     * and ProcedureOperation.  End result will be List&lt;TableType&gt;
     * and List&lt;ProcedureType&gt;.
     */
    public void buildDbArtifacts() {
        List<TableOperationModel> tableOperations = new ArrayList<TableOperationModel>();
        List<ProcedureOperationModel> procedureOperations = new ArrayList<ProcedureOperationModel>();
        //its possible a builder might have pre-built tables
        if (dbTables.size() == 0) {
            //do Table operations first
            for (OperationModel operation : dbwsBuilder.operations) {
                if (operation.isTableOperation()) {
                    TableOperationModel tom = (TableOperationModel)operation;
                    tableOperations.add(tom);
                    if (tom.additionalOperations != null && tom.additionalOperations.size() > 0) {
                        for (OperationModel nestedOperation : tom.additionalOperations) {
                            if (nestedOperation.isProcedureOperation()) {
                                procedureOperations.add((ProcedureOperationModel)nestedOperation);
                            }
                        }
                    }
                }
            }
            if (tableOperations.size() > 0) {
                List<String> catalogPatterns = new ArrayList<String>();
                List<String> schemaPatterns = new ArrayList<String>();
                List<String> tableNamePatterns = new ArrayList<String>();
                for (TableOperationModel tableOperation : tableOperations) {
                    catalogPatterns.add(tableOperation.getCatalogPattern());
                    schemaPatterns.add(tableOperation.getSchemaPattern());
                    tableNamePatterns.add(tableOperation.getTablePattern());
                }
                List<TableType> tables = loadTables(catalogPatterns, schemaPatterns, tableNamePatterns);
                // if we didn't find any tables log a WARNING
                if (tables == null || tables.isEmpty()) {
                    logNotFoundWarnings(NO_TABLE_MSG, schemaPatterns, catalogPatterns, tableNamePatterns);
                } else {
                    //now assign tables to operations
                    for (TableType tableType : tables) {
                        for (TableOperationModel tableOperation : tableOperations) {
                            //figure out catalog(optional)/schema/tableName matching
                            boolean tableNameMatch = sqlMatch(tableOperation.getTablePattern(), tableType.getTableName());
                            boolean schemaNameMatch = sqlMatch(tableOperation.getSchemaPattern(), tableType.getSchema());
                            if (tableNameMatch && schemaNameMatch) {
                                String originalCatalogPattern = tableOperation.getCatalogPattern();
                                if (tableType.isDbTableType() && originalCatalogPattern != null) {
                                    boolean catalogNameMatch = sqlMatch(originalCatalogPattern, ((DbTable) tableType).getCatalog());
                                    if (catalogNameMatch) {
                                        tableOperation.getDbTables().add(tableType);
                                    }
                                } else {
                                    tableOperation.getDbTables().add(tableType);
                                }
                            }
                        }
                    }
                    dbTables.addAll(tables);
                }
            }
        }

        // next do StoredProcedure operations
        // it's possible a builder might have pre-built procedures
        if (dbStoredProcedures.size() == 0) {
            for (OperationModel operation : dbwsBuilder.operations) {
                if (operation.isProcedureOperation()) {
                    procedureOperations.add((ProcedureOperationModel)operation);
                }
            }
            if (procedureOperations.size() > 0) {
                List<String> catalogPatterns = new ArrayList<String>();
                List<String> schemaPatterns = new ArrayList<String>();
                List<String> procedureNamePatterns = new ArrayList<String>();
                for (ProcedureOperationModel procedureOperation : procedureOperations) {
                    catalogPatterns.add(procedureOperation.getCatalogPattern());
                    schemaPatterns.add(procedureOperation.getSchemaPattern());
                    procedureNamePatterns.add(procedureOperation.getProcedurePattern());
                }
                List<ProcedureType> procedures = loadProcedures(catalogPatterns, schemaPatterns, procedureNamePatterns);
                // if we didn't find any procs/funcs log a WARNING
                if (procedures == null || procedures.isEmpty()) {
                    logNotFoundWarnings(NO_PROC_MSG, schemaPatterns, catalogPatterns, procedureNamePatterns);
                } else {
                    //now assign procedures to operations
                    for (ProcedureType procedureType : procedures) {
                        for (ProcedureOperationModel procedureOperation : procedureOperations) {
                            boolean procedureNameMatch = sqlMatch(procedureOperation.getProcedurePattern(),
                                procedureType.getProcedureName());
                            boolean schemaNameMatch = true;
                            boolean catalogNameMatch = true;
                            if (procedureNameMatch) {
                                String originalSchemaPattern = procedureOperation.getSchemaPattern();
                                if (originalSchemaPattern != null) {
                                    schemaNameMatch = sqlMatch(originalSchemaPattern, procedureType.getSchema());
                                }
                                String originalCatalogPattern = procedureOperation.getCatalogPattern();
                                if (originalCatalogPattern != null) {
                                    catalogNameMatch = sqlMatch(originalCatalogPattern, procedureType.getCatalogName());
                                }
                            }
                            if (procedureNameMatch && schemaNameMatch && catalogNameMatch) {
                                procedureOperation.getDbStoredProcedures().add(procedureType);
                            }
                        }
                    }
                    dbStoredProcedures.addAll(procedures);
                }
            }
        }
    }

    /**
     * Build the schema
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void buildSchema(NamingConventionTransformer nct) {
        Project oxProject = dbwsBuilder.getOxProject();
        Schema schema = null;
        List<Descriptor> descriptorsToProcess = new ArrayList<Descriptor>();
        for (Descriptor desc : (List<Descriptor>)(List)oxProject.getOrderedDescriptors()) {
            String alias = desc.getAlias();
            if (!DEFAULT_SIMPLE_XML_FORMAT_TAG.equals(alias)) {
                descriptorsToProcess.add(desc);
            }
        }
        if (descriptorsToProcess.size() > 0) {
            // need a deep-copy clone of oxProject; simplest way is to marshall/unmarshall to a stream
            StringWriter sw = new StringWriter();
            XMLProjectWriter.write(oxProject, sw);
            XRDynamicClassLoader specialLoader = new XRDynamicClassLoader(this.getClass().getClassLoader());
            Project oxProjectClone = XMLProjectReader.read(new StringReader(sw.toString()), specialLoader);
            ProjectHelper.fixOROXAccessors(oxProjectClone, null);
            XMLLogin xmlLogin = new XMLLogin();
            DOMPlatform domPlatform = new DOMPlatform();
            domPlatform.getConversionManager().setLoader(specialLoader);
            xmlLogin.setPlatform(domPlatform);
            oxProjectClone.setLogin(xmlLogin);
            oxProjectClone.createDatabaseSession(); // initialize reference descriptors
            SchemaModelGenerator schemaGenerator = new SchemaModelGenerator(XMLConversionManager.getDefaultXMLManager(), true);
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
        } else {
            schema = new Schema();
            addSimpleXMLFormat(schema);
            schema.setTargetNamespace(dbwsBuilder.getTargetNamespace());
        }
        dbwsBuilder.setSchema(schema);
    }

    /**
     * Build the sessions xml file.
     */
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

    /**
     * Build the DBWS model, a.k.a. service file.
     */
    @SuppressWarnings({"rawtypes"})
    public void buildDBWSModel(NamingConventionTransformer nct, OutputStream dbwsServiceStream) {
        Project orProject = dbwsBuilder.getOrProject();
        if (!isNullStream(dbwsServiceStream)) {
            for (Iterator i = orProject.getOrderedDescriptors().iterator(); i.hasNext();) {
                ClassDescriptor desc = (ClassDescriptor)i.next();
                String tablenameAlias = desc.getAlias();
                if (dbwsBuilder.requireCRUDOperations.contains(tablenameAlias)) {
                    String aliasType = tablenameAlias + TYPE_STR;
                    String tableName = desc.getTableName();
                    String schemaAlias = nct.generateSchemaAlias(tableName);
                    Map<String, String> ops;
                    if (!crudOps.containsKey(tableName)) {
                        ops = new HashMap<String, String>();
                        crudOps.put(tableName, ops);
                    }
                    ops = crudOps.get(tableName);

                    String pks = null;
                    int pkCount = 0;
                    for (Iterator j = desc.getPrimaryKeyFields().iterator(); j.hasNext();) {
                        DatabaseField field = (DatabaseField)j.next();
                        if (pkCount++ == 0) {
                            pks = OPEN_BRACKET + field.getName() + EQUALS_BINDING1_STR;
                        } else {
                            pks = pks.concat(AND_STR + field.getName() + EQUALS_BINDING_STR + pkCount++);
                        }
                    }
                    if (pks != null) {
                       pks = pks.concat(CLOSE_BRACKET);
                    }

                    // findByPk
                    String crudOpName = Util.PK_QUERYNAME + UNDERSCORE + aliasType;
                    String findByPKName = crudOpName;
                    QueryOperation findByPKQueryOperation = new QueryOperation();
                    findByPKQueryOperation.setName(crudOpName);
                    findByPKQueryOperation.setUserDefined(false);
                    NamedQueryHandler nqh1 = new NamedQueryHandler();
                    nqh1.setName(crudOpName);
                    nqh1.setDescriptor(tablenameAlias);
                    Result result = new Result();
                    QName theInstanceType = new QName(dbwsBuilder.getTargetNamespace(), schemaAlias, TARGET_NAMESPACE_PREFIX);
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
                    dbwsBuilder.xrServiceModel.getOperations().put(findByPKQueryOperation.getName(), findByPKQueryOperation);

                    // find all
                    crudOpName = FINDALL_QUERYNAME + UNDERSCORE + aliasType;
                    QueryOperation findAllOperation = new QueryOperation();
                    findAllOperation.setName(crudOpName);
                    findAllOperation.setUserDefined(false);
                    NamedQueryHandler nqh2 = new NamedQueryHandler();
                    nqh2.setName(crudOpName);
                    nqh2.setDescriptor(tablenameAlias);
                    Result result2 = new CollectionResult();
                    result2.setType(theInstanceType);
                    findAllOperation.setResult(result2);
                    findAllOperation.setQueryHandler(nqh2);
                    dbwsBuilder.xrServiceModel.getOperations().put(findAllOperation.getName(), findAllOperation);

                    // create
                    crudOpName = CREATE_OPERATION_NAME + UNDERSCORE + aliasType;
                    InsertOperation insertOperation = new InsertOperation();
                    insertOperation.setName(crudOpName);
                    Parameter theInstance = new Parameter();
                    theInstance.setName(THE_INSTANCE_NAME);
                    theInstance.setType(theInstanceType);
                    insertOperation.getParameters().add(theInstance);
                    dbwsBuilder.xrServiceModel.getOperations().put(insertOperation.getName(), insertOperation);

                    StringBuilder sqlStmt = new StringBuilder(128);
                    sqlStmt.append(INSERT_STR).append(tableName).append(SINGLE_SPACE).append(OPEN_BRACKET);
                    DescriptorHelper.buildColsFromMappings(sqlStmt, desc.getMappings(), COMMA_SPACE_STR);
                    sqlStmt.append(CLOSE_BRACKET).append(VALUES_STR).append(OPEN_BRACKET);
                    DescriptorHelper.buildValuesAsQMarksFromMappings(sqlStmt, desc.getMappings(), COMMA_SPACE_STR);
                    sqlStmt.append(CLOSE_BRACKET);
                    ops.put(crudOpName, sqlStmt.toString());

                    // update
                    crudOpName = UPDATE_OPERATION_NAME + UNDERSCORE + aliasType;
                    UpdateOperation updateOperation = new UpdateOperation();
                    updateOperation.setName(crudOpName);
                    updateOperation.getParameters().add(theInstance);
                    dbwsBuilder.xrServiceModel.getOperations().put(updateOperation.getName(), updateOperation);

                    sqlStmt = new StringBuilder(128);
                    sqlStmt.append(UPDATE_STR).append(tableName).append(SET_STR);
                    DescriptorHelper.buildColsAndValuesBindingsFromMappings(sqlStmt, desc.getMappings(),
                            desc.getPrimaryKeyFields(), pkCount, EQUALS_BINDING_STR, COMMA_SPACE_STR);
                    sqlStmt.append(WHERE_STR).append(pks);
                    ops.put(crudOpName, sqlStmt.toString());

                    // delete
                    crudOpName = REMOVE_OPERATION_NAME + UNDERSCORE + aliasType;
                    DeleteOperation deleteOperation = new DeleteOperation();
                    deleteOperation.setName(crudOpName);
                    deleteOperation.setDescriptorName(tablenameAlias);
                    deleteOperation.setFindByPKQuery(findByPKName);
                    for (Iterator j = desc.getPrimaryKeyFields().iterator(); j.hasNext();) {
                        DatabaseField field = (DatabaseField)j.next();
                        Parameter p = new Parameter();
                        p.setName(field.getName().toLowerCase());
                        p.setType(getXMLTypeFromJDBCType(field.getSqlType()));
                        deleteOperation.getParameters().add(p);
                    }
                    dbwsBuilder.xrServiceModel.getOperations().put(deleteOperation.getName(), deleteOperation);

                    sqlStmt = new StringBuilder(128);
                    sqlStmt.append(DELETE_STR).append(tableName).append(WHERE_STR).append(pks);
                    ops.put(crudOpName, sqlStmt.toString());
                }
            }
            // check for additional operations
            for (OperationModel operation : dbwsBuilder.operations) {
                if (operation.isTableOperation()) {
                    TableOperationModel tableModel = (TableOperationModel)operation;
                    if (tableModel.additionalOperations != null && tableModel.additionalOperations.size() > 0) {
                        for (OperationModel additionalOperation : tableModel.additionalOperations) {
                            if (additionalOperation.hasBuildSql()) {
                                addToOROXProjectsForBuildSql((ModelWithBuildSql) additionalOperation, orProject, dbwsBuilder.getOxProject(), nct);
                            } else {
                                additionalOperation.buildOperation(dbwsBuilder);
                            }
                        }
                    }
                } else { // handle non-nested <sql> and <procedure> operations
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
            } catch (IOException e) {/* ignore */
            }
            dbwsBuilder.getPackager().closeSWARefStream(swarefStream);
        }
    }

    public void buildWSDL(OutputStream wsdlStream, NamingConventionTransformer nct) throws WSDLException {
        if (!isNullStream(wsdlStream)) {
            dbwsBuilder.logMessage(FINEST, "building " + DBWS_WSDL);
            dbwsBuilder.wsdlGenerator = new WSDLGenerator(dbwsBuilder.xrServiceModel, nct, dbwsBuilder.getWsdlLocationURI(), dbwsBuilder.getPackager().hasAttachments(), dbwsBuilder.getTargetNamespace(), wsdlStream);
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

    /**
     * Write the (optional) deployment descriptor to the given OutputStream.
     */
    public void writeDeploymentDescriptor(OutputStream deploymentDescriptorStream) {
        if (!isNullStream(deploymentDescriptorStream)) {
            dbwsBuilder.logMessage(FINEST, "writing " + dbwsBuilder.getPackager().getDeploymentDescriptorFileName());
            dbwsBuilder.getPackager().writeDeploymentDescriptor(deploymentDescriptorStream);
            dbwsBuilder.getPackager().closeDeploymentDescriptorStream(deploymentDescriptorStream);
        }
    }

    public void generateDBWSProvider(OutputStream sourceProviderStream, OutputStream classProviderStream, OutputStream sourceProviderListenerStream, OutputStream classProviderListenerStream) {
        if (isNullStream(sourceProviderStream) && isNullStream(classProviderStream) && isNullStream(sourceProviderListenerStream) && isNullStream(classProviderListenerStream)) {
            //no work to do
            return;
        }

        if (!isNullStream(sourceProviderStream)) {
            dbwsBuilder.logMessage(FINEST, "generating " + DBWS_PROVIDER_SOURCE_FILE);
        }
        if (!isNullStream(classProviderStream)) {
            dbwsBuilder.logMessage(FINEST, "generating " + DBWS_PROVIDER_CLASS_FILE);
        }
        dbwsBuilder.getPackager().writeProvider(sourceProviderStream, classProviderStream, sourceProviderListenerStream, classProviderListenerStream, dbwsBuilder);
        dbwsBuilder.getPackager().closeProviderSourceStream(sourceProviderStream);
        dbwsBuilder.getPackager().closeProviderClassStream(classProviderStream);
    }

    public void writeSchema(OutputStream dbwsSchemaStream) {
        if (!isNullStream(dbwsSchemaStream)) {
            SchemaModelProject schemaProject = new SchemaModelProject();
            if (dbwsBuilder.getSchema().getNamespaceResolver().resolveNamespacePrefix(WSI_SWAREF_PREFIX) != null) {
                XMLDescriptor descriptor = (XMLDescriptor)schemaProject.getClassDescriptor(Schema.class);
                descriptor.getNamespaceResolver().put(WSI_SWAREF_PREFIX, WSI_SWAREF_URI);
            }
            if (dbwsBuilder.getSchema().getNamespaceResolver().resolveNamespacePrefix(XML_MIME_PREFIX) != null) {
                XMLDescriptor descriptor = (XMLDescriptor)schemaProject.getClassDescriptor(Schema.class);
                descriptor.getNamespaceResolver().put(XML_MIME_PREFIX, XML_MIME_URL);
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
        if (!writeORProject) {
            // check for any named queries - SimpleXMLFormatProject sometimes need them
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
        if ((writeORProject || !dbwsBuilder.xrServiceModel.getOperations().isEmpty()) && !isNullStream(dbwsOrStream)) {
            XMLContext context = new XMLContext(workbenchXMLProject);
            context.getSession(orProject).getEventManager().addListener(new MissingDescriptorListener());

            XMLEntityMappings mappings = XmlEntityMappingsGenerator.generateXmlEntityMappings(orProject, complextypes, crudOps);
            if (mappings != null) {
                XMLEntityMappingsWriter writer = new XMLEntityMappingsWriter();
                writer.write(mappings, dbwsOrStream);
            }
        }
        if (!isNullStream(dbwsOxStream)) {
            boolean writeOXProject = false;
            if (hasTables() || dbwsBuilder.hasBuildSqlOperations()) {
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
                List<XmlBindings> xmlBindingsList = XmlBindingsGenerator.generateXmlBindings(oxProject.getOrderedDescriptors());
                if (xmlBindingsList.size() > 0) {
                    XmlBindingsModel model = new XmlBindingsModel();
                    model.setBindingsList(xmlBindingsList);
                    try {
                        JAXBContext jc = JAXBContext.newInstance(XmlBindingsModel.class);
                        Marshaller marshaller = jc.createMarshaller();
                        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                        marshaller.marshal(model, dbwsOxStream);
                    } catch (JAXBException jaxbEx) {
                        throw new DBWSException(OXM_MARSHAL_EX_MSG, jaxbEx);
                    }
                }
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
        } catch (SQLException sqlException) {
            throw new IllegalStateException("failure executing secondary SQL: " + secondarySql, sqlException);
        }
        try {
            return resultSet.getMetaData();
        } catch (SQLException sqlException) {
            throw new IllegalStateException("failure retrieving resultSet metadata", sqlException);
        }
    }

    protected static DirectToFieldMapping setUpDirectToFieldMapping(RelationalDescriptor desc, String columnName, NamingConventionTransformer nct, Class<?> attributeClass, int jdbcType, boolean isPk) {
        DirectToFieldMapping dtfm = new DirectToFieldMapping();
        dtfm.setAttributeClassificationName(attributeClass.getName());
        String fieldName = nct.generateElementAlias(columnName);
        dtfm.setAttributeName(fieldName);
        DatabaseField databaseField = new DatabaseField(columnName, desc.getTableName());
        databaseField.setSqlType(jdbcType);
        dtfm.setField(databaseField);
        if (nct.getOptimisticLockingField() != null && nct.getOptimisticLockingField().equalsIgnoreCase(columnName)) {
            desc.useVersionLocking(columnName, false);
        }
        if (isPk) {
            desc.addPrimaryKeyField(databaseField);
        }
        return dtfm;
    }

    protected XMLDirectMapping setUpXMLDirectMapping(String columnName, QName qName, NamingConventionTransformer nct, Class<?> attributeClass, int jdbcType, boolean isPk) {
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
                        } else {
                            attachmentType = SWAREF_STR;
                        }
                        // only need one operation to require attachments
                        break;
                    }
                    if (tom.additionalOperations != null && tom.additionalOperations.size() > 0) {
                        for (OperationModel om2 : tom.additionalOperations) {
                            if (om2.isProcedureOperation()) {
                                ProcedureOperationModel pom = (ProcedureOperationModel)om2;
                                if (pom.getBinaryAttachment()) {
                                    binaryAttach = true;
                                    if (MTOM_STR.equalsIgnoreCase(tom.getAttachmentType())) {
                                        attachmentType = MTOM_STR;
                                    } else {
                                        attachmentType = SWAREF_STR;
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            XMLBinaryDataMapping xbdm = new XMLBinaryDataMapping();
            if (binaryAttach) {
                if (attachmentType.equals(SWAREF_STR)) {
                    xbdm.setSwaRef(true);
                    qName = XMLConstants.SWA_REF_QNAME;
                }
            } else {
                xbdm.setShouldInlineBinaryData(true);
            }
            xbdm.setMimeType(DEFAULT_ATTACHMENT_MIMETYPE);
            xdm = xbdm;
        } else {
            xdm = new XMLDirectMapping();
        }
        String fieldName = nct.generateElementAlias(columnName);
        xdm.setAttributeName(fieldName);
        xdm.setAttributeClassificationName(attributeClass.getName());
        String xPath = EMPTY_STRING;
        ElementStyle style = nct.styleForElement(columnName);
        if (style == ATTRIBUTE) {
            xPath += AT_SIGN + fieldName;
        } else if (style == ELEMENT) {
            xPath += fieldName;
        }
        if (style == ELEMENT && attributeClass != APBYTE) {
            xPath += SLASH_TEXT;
        }
        xdm.setXPath(xPath);
        XMLField xmlField = (XMLField)xdm.getField();
        xmlField.setSchemaType(qName);
        if (isPk) {
            xmlField.setRequired(true);
        } else {
            AbstractNullPolicy nullPolicy = xdm.getNullPolicy();
            nullPolicy.setNullRepresentedByEmptyNode(false);
            nullPolicy.setMarshalNullRepresentation(XSI_NIL);
            nullPolicy.setNullRepresentedByXsiNil(true);
            xdm.setNullPolicy(nullPolicy);
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

    /**
     * Generates 'findByPrimaryKey' and 'findAll' queries for a given table
     * and descriptor. The queries are set on the given descriptor.
     */
    protected void setUpFindQueries(NamingConventionTransformer nct, String tableName, RelationalDescriptor desc) {
        ReadObjectQuery roq = new ReadObjectQuery();
        String generatedJavaClassName = getGeneratedJavaClassName(tableName, dbwsBuilder.getProjectName());
        roq.setReferenceClassName(generatedJavaClassName);
        Expression expression = null;
        Expression builder = new ExpressionBuilder();
        Expression subExp1;
        Expression subExp2;
        Expression subExpression;
        String pks = null;
        List<DatabaseField> primaryKeyFields = desc.getPrimaryKeyFields();
        for (int index = 0; index < primaryKeyFields.size(); index++) {
            DatabaseField primaryKeyField = primaryKeyFields.get(index);
            subExp1 = builder.getField(primaryKeyField);
            subExp2 = builder.getParameter(primaryKeyField.getName().toLowerCase());
            subExpression = subExp1.equal(subExp2);
            if (expression == null) {
                expression = subExpression;
            } else {
                expression = expression.and(subExpression);
            }
            roq.addArgument(primaryKeyField.getName().toLowerCase());
            if (index == 0) {
                pks = OPEN_BRACKET + primaryKeyField.getName() + EQUALS_BINDING1_STR;
            } else {
                pks = pks.concat(AND_STR + primaryKeyField.getName() + EQUALS_BINDING_STR + index);
            }
        }
        if (pks != null) {
           pks = pks.concat(CLOSE_BRACKET);
        }

        roq.setSelectionCriteria(expression);

        desc.getQueryManager().addQuery(PK_QUERYNAME + UNDERSCORE + desc.getAlias() + TYPE_STR, roq);
        ReadAllQuery raq = new ReadAllQuery();
        raq.setReferenceClassName(generatedJavaClassName);
        desc.getQueryManager().addQuery(FINDALL_QUERYNAME + UNDERSCORE + desc.getAlias() + TYPE_STR, raq);

        // find by pk
        String findByPk = SELECT_FROM_STR + tableName + WHERE_STR + pks;
        roq.setSQLString(findByPk);

        // find all
        String findAll = SELECT_FROM_STR + tableName;
        raq.setSQLString(findAll);
    }

    protected static List<DbColumn> buildDbColumns(Connection connection, String secondarySql) {
        List<DbColumn> columns = null;
        ResultSetMetaData rsMetaData = getResultSetMetadataForSecondarySQL(connection, secondarySql);
        if (rsMetaData != null) {
            int columnCount = 0;
            try {
                columnCount = rsMetaData.getColumnCount();
            } catch (SQLException sqlException) {
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
                        dbColumn.setEnclosedType(buildTypeForJDBCType(dbColumn.getJDBCType(), dbPrecision, dbScale));
                        if (rsMetaData.isNullable(i) == ResultSetMetaData.columnNullable) {
                            dbColumn.unSetNotNull();
                        } else {
                            dbColumn.setNotNull();
                        }
                        columns.add(dbColumn);
                    }
                } catch (SQLException sqlException) {
                    throw new IllegalStateException("failure retrieving column information", sqlException);
                }
            }
        }
        return columns;
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
        if (dataType.isNumericType()) {
            NumericType numericDataType = (NumericType)dataType;
            if (numericDataType.getScale() > 0) {
                typeName = DECIMAL_STR;
            }
        }
        return typeName;
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
    @SuppressWarnings("rawtypes")
    protected org.eclipse.persistence.internal.helper.DatabaseType buildDatabaseTypeFromMetadataType(DatabaseType dType, String catalog) {
        // argument could be from a different package
        if (dType.isPLSQLType()) {
            PLSQLType pType = (PLSQLType) dType;
            catalog = pType.getParentType().getPackageName();
        }

        // handle cursors
        if (dType.isPLSQLCursorType()) {
            if (dType.isArgumentType()) {
                dType = ((ArgumentType)dType).getEnclosedType();
            }
            PLSQLCursorType pType = (PLSQLCursorType)dType;
            return new PLSQLCursor(pType.getParentType().getPackageName() + DOT + pType.getCursorName());
        }

        if (dType.isArgumentType()) {
            dType = ((ArgumentType)dType).getEnclosedType();
        } else if (dType.isTYPEType()) {
            dType = ((TYPEType)dType).getEnclosedType();
        }

        // composite types
        if (dType.isComposite()) {
            String typeName = dType.getTypeName();
            // for %ROWTYPE, the compatible JDBC type name cannot contain '%'
            String compatibleType = typeName.contains(PERCENT) ? typeName.replace(PERCENT, UNDERSCORE) : typeName;
            String javaTypeName = compatibleType.toLowerCase();

            // handle PL/SQL types
            if (dType.isPLSQLType()) {
                // for %ROWTYPE we don't want the catalog name prepended even if non-null
                if (catalog != null && !typeName.contains(ROWTYPE_STR)) {
                    typeName = (catalog + DOT).concat(typeName);
                    compatibleType = (catalog + UNDERSCORE).concat(compatibleType);
                    javaTypeName = (catalog.toLowerCase() + DOT).concat(javaTypeName);
                }
                // handle PL/SQL record
                if (dType.isPLSQLRecordType()) {
                    PLSQLrecord plsqlRec = new PLSQLrecord();
                    plsqlRec.setTypeName(typeName);
                    plsqlRec.setCompatibleType(compatibleType);
                    plsqlRec.setJavaTypeName(javaTypeName);
                    // process fields
                    for (FieldType fld : ((PLSQLRecordType)dType).getFields()) {
                        if (fld.getEnclosedType().isPrecisionType()) {
                            PrecisionType precisionType = (PrecisionType)fld.getEnclosedType();
                            plsqlRec.addField(fld.getFieldName(), buildDatabaseTypeFromMetadataType(precisionType), (int) precisionType.getPrecision(), (int) precisionType.getScale());
                        } else if (fld.getEnclosedType().isSizedType()) {
                            SizedType sizedType = (SizedType) fld.getEnclosedType();
                            plsqlRec.addField(fld.getFieldName(), buildDatabaseTypeFromMetadataType(sizedType), (int)sizedType.getSize());
                        } else {
                            plsqlRec.addField(fld.getFieldName(), buildDatabaseTypeFromMetadataType(fld.getEnclosedType(), catalog));
                        }
                    }
                    return plsqlRec;
                }
                // assumes PL/SQL collection
                PLSQLCollection plsqlCollection = new PLSQLCollection();
                plsqlCollection.setTypeName(typeName);
                plsqlCollection.setCompatibleType(compatibleType);
                plsqlCollection.setJavaTypeName(javaTypeName + COLLECTION_WRAPPER_SUFFIX);
                plsqlCollection.setNestedType(buildDatabaseTypeFromMetadataType(((PLSQLCollectionType) dType).getEnclosedType(), catalog));
                return plsqlCollection;
            }
            // handle advanced Oracle types
            if (dType.isVArrayType()) {
                OracleArrayType varray = new OracleArrayType();
                varray.setTypeName(typeName);
                varray.setCompatibleType(compatibleType);
                varray.setJavaTypeName(getGeneratedWrapperClassName(javaTypeName, dbwsBuilder.getProjectName()));
                varray.setNestedType(buildDatabaseTypeFromMetadataType(((VArrayType) dType).getEnclosedType(), null));
                return varray;
            }
            if (dType.isObjectType()) {
                OracleObjectType objType = new OracleObjectType();
                objType.setTypeName(typeName);
                objType.setCompatibleType(compatibleType);
                objType.setJavaTypeName(getGeneratedJavaClassName(javaTypeName, dbwsBuilder.getProjectName()));
                objType.setJavaType(getWrapperClass(objType.getJavaTypeName()));
                Map<String, org.eclipse.persistence.internal.helper.DatabaseType> fields = objType.getFields();
                ObjectType oType = (ObjectType) dType;
                for (FieldType field : oType.getFields()) {
                    fields.put(field.getFieldName(), buildDatabaseTypeFromMetadataType(field.getEnclosedType()));
                }
                return objType;
            }
            if (dType.isObjectTableType()) {
                OracleArrayType tableType = new OracleArrayType();
                tableType.setTypeName(typeName);
                tableType.setCompatibleType(compatibleType);
                tableType.setJavaTypeName(getGeneratedWrapperClassName(javaTypeName, dbwsBuilder.getProjectName()));
                org.eclipse.persistence.internal.helper.DatabaseType nestedType = buildDatabaseTypeFromMetadataType(((ObjectTableType) dType).getEnclosedType(), null);
                // need to set the Java Type on the nested type
                Class wrapper = getWrapperClass(nestedType);
                if (wrapper != null) {
                    ((ComplexDatabaseType) nestedType).setJavaType(wrapper);
                }
                tableType.setNestedType(nestedType);
                return tableType;
            }
            return null;
        } else if (dType.isScalar()) {
            org.eclipse.persistence.internal.helper.DatabaseType theType = OraclePLSQLTypes.getDatabaseTypeForCode(((ScalarDatabaseType)dType).getTypeName());
            if (theType != null) {
                return theType;
            }
        }
        // scalar types
        return JDBCTypes.getDatabaseTypeForCode(org.eclipse.persistence.tools.dbws.Util.getJDBCTypeFromTypeName(dType.getTypeName()));
    }

    /**
     * Apply SimpleXMLFormat if 'isSimpleXMLFormat' is true.  The SimpleXMLFormat is
     * configured based on the given ProcedureOperationModel's simpleXMLFormatTag
     * and xmlTag (if set) and set on the given Result.  A descriptor is also added
     * to the OXProject if none exists.
     */
    protected void handleSimpleXMLFormat(boolean isSimpleXMLFormat, Result result, ProcedureOperationModel procedureOperationModel) {
        if (isSimpleXMLFormat || result.getType() == Util.SXF_QNAME) {
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
        if (requiresSimpleXMLFormat(dbwsBuilder.getXrServiceModel()) && dbwsBuilder.getSchema().getTopLevelElements().get(SIMPLEXML_STR) == null) {
            addSimpleXMLFormat(dbwsBuilder.getSchema());
        }
    }

    /**
     * Return a list of ArgumentTypes for a given Procedure Type.  This will include
     * a return argument if pType is a FunctionType.
     */
    protected List<ArgumentType> getArgumentListForProcedureType(ProcedureType pType) {
        List<ArgumentType> args = new ArrayList<ArgumentType>();
        // return argument
        if (pType.isFunctionType()) {
            // assumes that a function MUST have a return type
            args.add(((FunctionType)pType).getReturnArgument());
        }
        args.addAll(pType.getArguments());
        return args;
    }

    /**
     * Return the wrapper class for a given DatabaseType.  The class will be loaded by the
     * XRDynamicClassloader, based on the DatabaseType's javaTypeName.  If the class
     * cannot be loaded, or the given DatabaseType is not a ComplexDatabaseType, null
     * will be returned.
     *
     */
    @SuppressWarnings("rawtypes")
    protected Class getWrapperClass(org.eclipse.persistence.internal.helper.DatabaseType databaseType) {
        if (databaseType instanceof ComplexDatabaseType) {
            return getWrapperClass(((ComplexDatabaseType) databaseType).getJavaTypeName());
        }
        return null;
    }

    /**
     * Return the wrapper class for a wrapper class name.  The wrapper class name would
     * typically be an argument type name, descriptor java class name, or a
     * DatabaseType's javaTypeName.
     *
     * The class will be loaded by the XRDynamicClassloader;  if the class cannot be
     * loaded, null will be returned.
     *
     */
    @SuppressWarnings("rawtypes")
    protected Class getWrapperClass(String wrapperClassName) {
        Class wrapperClass = null;
        try {
            // the following call will try and load the collection wrapper class via XRDynamicClassLoader
            wrapperClass = new XRDynamicClassLoader(this.getClass().getClassLoader()).loadClass(wrapperClassName);
        } catch (ClassNotFoundException e) {
            // should never get here, so ignore
        }
        return wrapperClass;
    }

    /**
     * Log a WARNING with the DBWSBuilder when a target (table, package, stored procedure/function, etc)
     * cannot be found using the information given by the user.
     *
     */
    protected void logNotFoundWarnings(String message, List<String> schemaPatterns, List<String> catalogPatterns, List<String> targetPatterns) {
        StringBuffer sb = new StringBuffer();
        sb.append(message);
        for (int i=0; i < targetPatterns.size(); i++) {
            sb.append(SINGLE_SPACE);
            sb.append(OPEN_SQUARE_BRACKET);
            boolean prependDot = false;
            String schemaName = schemaPatterns.get(i);
            if (schemaName != null && schemaName.length() > 0) {
                sb.append(schemaName);
                prependDot = true;
            }
            String pkgName = catalogPatterns.get(i);
            if (pkgName != null && pkgName.length() > 0) {
                if (prependDot) {
                    sb.append(DOT);
                }
                prependDot = true;
                sb.append(pkgName);
            }
            String tgtName = targetPatterns.get(i);
            if (tgtName != null && tgtName.length() > 0) {
                if (prependDot) {
                    sb.append(DOT);
                }
                sb.append(tgtName);
            }
            sb.append(CLOSE_SQUARE_BRACKET);
        }
        dbwsBuilder.logMessage(Level.WARNING, sb.toString());
    }

    /**
     * Log a WARNING with the DBWSBuilder when a package cannot be found
     * using the information given by the user.
     *
     */
    protected void logPackageNotFoundWarnings(String message, List<String> schemaPatterns, List<String> catalogPatterns) {
        StringBuffer sb = new StringBuffer();
        sb.append(message);
        for (int i=0; i < catalogPatterns.size(); i++) {
            sb.append(SINGLE_SPACE);
            sb.append(OPEN_SQUARE_BRACKET);
            boolean prependDot = false;
            String schemaName = schemaPatterns.get(i);
            if (schemaName != null && schemaName.length() > 0) {
                sb.append(schemaName);
                prependDot = true;
            }
            String pkgName = catalogPatterns.get(i);
            if (pkgName != null && pkgName.length() > 0) {
                if (prependDot) {
                    sb.append(DOT);
                }
                prependDot = true;
                sb.append(pkgName);
            }
            sb.append(CLOSE_SQUARE_BRACKET);
        }
        dbwsBuilder.logMessage(Level.WARNING, sb.toString());
    }
}
