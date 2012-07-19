/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.tools.dbws.oracle;

//javase imports
import java.sql.Array;
import java.sql.Struct;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import static java.sql.Types.ARRAY;
import static java.sql.Types.OTHER;
import static java.sql.Types.STRUCT;
import static java.util.logging.Level.FINEST;

//java eXtension imports
import javax.xml.namespace.QName;

//EclipseLink imports
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.ComplexDatabaseType;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.internal.xr.Attachment;
import org.eclipse.persistence.internal.xr.CollectionResult;
import org.eclipse.persistence.internal.xr.NamedQueryHandler;
import org.eclipse.persistence.internal.xr.Parameter;
import org.eclipse.persistence.internal.xr.ProcedureArgument;
import org.eclipse.persistence.internal.xr.ProcedureOutputArgument;
import org.eclipse.persistence.internal.xr.QueryHandler;
import org.eclipse.persistence.internal.xr.QueryOperation;
import org.eclipse.persistence.internal.xr.Result;
import org.eclipse.persistence.internal.xr.StoredFunctionQueryHandler;
import org.eclipse.persistence.internal.xr.StoredProcedureQueryHandler;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.structures.ArrayMapping;
import org.eclipse.persistence.mappings.structures.ObjectArrayMapping;
import org.eclipse.persistence.mappings.structures.ObjectRelationalDataTypeDescriptor;
import org.eclipse.persistence.mappings.structures.ObjectRelationalDatabaseField;
import org.eclipse.persistence.mappings.structures.StructureMapping;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.mappings.nullpolicy.AbstractNullPolicy;
import org.eclipse.persistence.oxm.schema.XMLSchemaURLReference;
import org.eclipse.persistence.platform.database.jdbc.JDBCTypes;
import org.eclipse.persistence.platform.database.oracle.jdbc.OracleObjectType;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLCollection;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLStoredFunctionCall;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLStoredProcedureCall;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLargument;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLrecord;
import org.eclipse.persistence.queries.DataReadQuery;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.StoredFunctionCall;
import org.eclipse.persistence.queries.StoredProcedureCall;
import org.eclipse.persistence.queries.ValueReadQuery;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.tools.dbws.BaseDBWSBuilderHelper;
import org.eclipse.persistence.tools.dbws.DBWSBuilder;
import org.eclipse.persistence.tools.dbws.DBWSBuilderHelper;
import org.eclipse.persistence.tools.dbws.ProcedureOperationModel;
import org.eclipse.persistence.tools.dbws.Util;
import org.eclipse.persistence.tools.oracleddl.metadata.ArgumentType;
import org.eclipse.persistence.tools.oracleddl.metadata.ArgumentTypeDirection;
import org.eclipse.persistence.tools.oracleddl.metadata.CompositeDatabaseType;
import org.eclipse.persistence.tools.oracleddl.metadata.DatabaseType;
import org.eclipse.persistence.tools.oracleddl.metadata.FieldType;
import org.eclipse.persistence.tools.oracleddl.metadata.FunctionType;
import org.eclipse.persistence.tools.oracleddl.metadata.ObjectTableType;
import org.eclipse.persistence.tools.oracleddl.metadata.ObjectType;
import org.eclipse.persistence.tools.oracleddl.metadata.PLSQLCollectionType;
import org.eclipse.persistence.tools.oracleddl.metadata.PLSQLCursorType;
import org.eclipse.persistence.tools.oracleddl.metadata.PLSQLPackageType;
import org.eclipse.persistence.tools.oracleddl.metadata.PLSQLRecordType;
import org.eclipse.persistence.tools.oracleddl.metadata.PLSQLType;
import org.eclipse.persistence.tools.oracleddl.metadata.ProcedureType;
import org.eclipse.persistence.tools.oracleddl.metadata.ROWTYPEType;
import org.eclipse.persistence.tools.oracleddl.metadata.TableType;
import org.eclipse.persistence.tools.oracleddl.metadata.VArrayType;
import org.eclipse.persistence.tools.oracleddl.metadata.visit.BaseDatabaseTypeVisitor;
import org.eclipse.persistence.tools.oracleddl.metadata.visit.DatabaseTypeVisitor;
import org.eclipse.persistence.tools.oracleddl.parser.ParseException;
import org.eclipse.persistence.tools.oracleddl.util.DatabaseTypeBuilder;
import static org.eclipse.persistence.internal.helper.ClassConstants.Object_Class;
import static org.eclipse.persistence.internal.xr.Util.SXF_QNAME;
import static org.eclipse.persistence.internal.xr.XRDynamicClassLoader.COLLECTION_WRAPPER_SUFFIX;
import static org.eclipse.persistence.oxm.XMLConstants.COLON;
import static org.eclipse.persistence.oxm.XMLConstants.DATE_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.DOT;
import static org.eclipse.persistence.oxm.XMLConstants.EMPTY_STRING;
import static org.eclipse.persistence.oxm.XMLConstants.INT;
import static org.eclipse.persistence.oxm.XMLConstants.SCHEMA_INSTANCE_PREFIX;
import static org.eclipse.persistence.oxm.XMLConstants.SCHEMA_INSTANCE_URL;
import static org.eclipse.persistence.oxm.XMLConstants.SCHEMA_PREFIX;
import static org.eclipse.persistence.oxm.XMLConstants.SCHEMA_URL;
import static org.eclipse.persistence.oxm.XMLConstants.TEXT;
import static org.eclipse.persistence.oxm.mappings.nullpolicy.XMLNullRepresentationType.XSI_NIL;
import static org.eclipse.persistence.tools.dbws.Util.SXF_QNAME_CURSOR;
import static org.eclipse.persistence.tools.dbws.Util.buildCustomQName;
import static org.eclipse.persistence.tools.dbws.Util.getGeneratedJavaClassName;
import static org.eclipse.persistence.tools.dbws.Util.getXMLTypeFromJDBCType;
import static org.eclipse.persistence.tools.dbws.Util.hasPLSQLCursorArg;
import static org.eclipse.persistence.tools.dbws.Util.qNameFromString;
import static org.eclipse.persistence.tools.dbws.Util.sqlMatch;
import static org.eclipse.persistence.tools.dbws.Util.APP_OCTET_STREAM;
import static org.eclipse.persistence.tools.dbws.Util.BUILDING_QUERYOP_FOR;
import static org.eclipse.persistence.tools.dbws.Util.CLOSE_PAREN;
import static org.eclipse.persistence.tools.dbws.Util.CURSOR_STR;
import static org.eclipse.persistence.tools.dbws.Util.CURSOR_OF_STR;
import static org.eclipse.persistence.tools.dbws.Util.OPEN_PAREN;
import static org.eclipse.persistence.tools.dbws.Util.PERCENT;
import static org.eclipse.persistence.tools.dbws.Util.UNDERSCORE;
import static org.eclipse.persistence.tools.dbws.Util.SLASH;
import static org.eclipse.persistence.tools.dbws.Util.TOPLEVEL;
import static org.eclipse.persistence.tools.dbws.Util.TYPE_STR;
import static org.eclipse.persistence.tools.oracleddl.metadata.ArgumentTypeDirection.IN;
import static org.eclipse.persistence.tools.oracleddl.metadata.ArgumentTypeDirection.INOUT;
import static org.eclipse.persistence.tools.oracleddl.metadata.ArgumentTypeDirection.OUT;

public class OracleHelper extends BaseDBWSBuilderHelper implements DBWSBuilderHelper {

    protected DatabaseTypeBuilder dtBuilder = new DatabaseTypeBuilder();
    protected boolean hasComplexProcedureArgs = false;
    public static final String NO_PKG_MSG = "No packages were found matching the following: ";

    public OracleHelper(DBWSBuilder dbwsBuilder) {
        super(dbwsBuilder);
    }

    /**
     * Indicates if this helper instance contains one or more
     * TableType instances in TableType List "dbTables".
     */
    public boolean hasTables() {
        return dbTables.size() == 0 ? false : true;
    }

    public boolean hasComplexProcedureArgs() {
        return hasComplexProcedureArgs;
    }

    @Override
    public void buildDbArtifacts() {
        super.buildDbArtifacts();
        //list of all directly-referenced packages
        Set<PLSQLPackageType> directPackages = new HashSet<PLSQLPackageType>();
        for (ProcedureType procedureType : dbStoredProcedures) {
            for (ArgumentType argumentType : procedureType.getArguments()) {
                DatabaseType argumentDataType = argumentType.getEnclosedType();
                if (argumentDataType.isPLSQLType()) {
                    PLSQLType plsqlType = (PLSQLType)argumentDataType;
                    directPackages.add(plsqlType.getParentType());
                }
            }
        }
        //any indirectly-referenced packages?
        final Set<PLSQLPackageType> indirectPackages = new HashSet<PLSQLPackageType>();
        DatabaseTypeVisitor indirectVisitor = new BaseDatabaseTypeVisitor() {
            @Override
            public void beginVisit(PLSQLPackageType databaseType) {
                indirectPackages.add(databaseType);
            }
        };
        for (PLSQLPackageType pckage : directPackages) {
            pckage.accept(indirectVisitor);
        }
        Set<PLSQLPackageType> packages = new HashSet<PLSQLPackageType>();
        packages.addAll(directPackages);
        packages.addAll(indirectPackages);
        for (PLSQLPackageType pckage : packages) {
            ShadowDDLGenerator ddlGenerator = new ShadowDDLGenerator(pckage);
            dbwsBuilder.getTypeDDL().addAll(ddlGenerator.getAllCreateDDLs());
            dbwsBuilder.getTypeDropDDL().addAll(ddlGenerator.getAllDropDDLs());
        }
    }

    /**
     * Builds query operations for a given ProcedureOperationModel.
     */
    public void buildProcedureOperation(ProcedureOperationModel procedureOperationModel) {
        for (ProcedureType storedProcedure : procedureOperationModel.getDbStoredProcedures()) {
            boolean hasComplexArgs = Util.hasComplexArgs(storedProcedure);
            QueryOperation qo = new QueryOperation();
            qo.setName(getNameForQueryOperation(procedureOperationModel, storedProcedure));

            String qualifiedProcName = getQualifiedProcedureName(procedureOperationModel, storedProcedure);
            dbwsBuilder.logMessage(FINEST, BUILDING_QUERYOP_FOR + qualifiedProcName);

            QueryHandler qh = null;
            if (!hasComplexArgs) {
                if (storedProcedure.isFunctionType()) {
                    qh = new StoredFunctionQueryHandler();
                } else {
                    qh = new StoredProcedureQueryHandler();
                }
                ((StoredProcedureQueryHandler) qh).setName(qualifiedProcName);

                // before assigning queryHandler, check for named query in OR
                // project
                List<DatabaseQuery> queries = dbwsBuilder.getOrProject().getQueries();
                if (queries.size() > 0) {
                    for (DatabaseQuery q : queries) {
                        if (q.getName().equals(qo.getName())) {
                            qh = new NamedQueryHandler();
                            ((NamedQueryHandler) qh).setName(qo.getName());
                        }
                    }
                }

                qo.setQueryHandler(qh);
            }

            String returnType = procedureOperationModel.getReturnType();
            boolean isCollection = procedureOperationModel.isCollection();
            boolean isSimpleXMLFormat = procedureOperationModel.isSimpleXMLFormat();

            Result result = null;
            if (storedProcedure.isFunctionType()) {
                ArgumentType returnArg = ((FunctionType)storedProcedure).getReturnArgument();
                result = buildResultForStoredFunction(returnArg, returnType);
                // for strongly typed ref cursors we will customize the simple-xml-format 
                // tags to better represent the PL/SQL record/table/column type
                if (returnArg.getEnclosedType().isPLSQLCursorType()) {
                    customizeSimpleXMLTagNames((PLSQLCursorType) returnArg.getEnclosedType(), procedureOperationModel);
                }
            } else if (hasComplexArgs) {
                if (Util.noOutArguments(storedProcedure)) {
                    result = new Result();
                    result.setType(new QName(SCHEMA_URL, INT, SCHEMA_PREFIX)); // rowcount
                }
            } else { // !hasComplexArgs
                // if user overrides returnType, assume they're right
                if (returnType != null) {
                    result = new Result();
                    result.setType(buildCustomQName(returnType, dbwsBuilder));
                } else {
                    if (isCollection) {
                        result = new CollectionResult();
                        if (isSimpleXMLFormat) {
                            result.setType(SXF_QNAME_CURSOR);
                        }
                    } else {
                        result = new Result();
                        result.setType(SXF_QNAME);
                    }
                }
            }
            for (ArgumentType arg : storedProcedure.getArguments()) {
                String argName = arg.getArgumentName();
                if (argName != null) {
                    QName xmlType = null;
                    ProcedureArgument pa = null;
                    ProcedureArgument paShadow = null; // for INOUT's
                    Parameter parm = null;
                    ArgumentTypeDirection direction = arg.getDirection();
                    if (!hasComplexArgs) {
                        if (arg.getEnclosedType().isPLSQLCursorType()) {
                            PLSQLCursorType cursorType = (PLSQLCursorType)arg.getEnclosedType();
                            if (cursorType.isWeaklyTyped()) {
                                xmlType = buildCustomQName("SYS_REFCURSOR", dbwsBuilder);
                            }
                        } else {
                            xmlType = getXMLTypeFromJDBCType(Util.getJDBCTypeFromTypeName(arg.getTypeName()));
                        }
                    } else {
                        // handle PL/SQL records and collections
                        if (arg.getEnclosedType().isPLSQLType()) {
                            hasComplexProcedureArgs = true;
                            String packageName = ((PLSQLType) arg.getEnclosedType()).getParentType().getPackageName();
                            String typeString = (packageName != null && packageName.length() > 0) ?
                                    packageName + UNDERSCORE + arg.getTypeName() : arg.getTypeName();
                            xmlType = buildCustomQName(typeString, dbwsBuilder);
                        } else if (arg.getEnclosedType().isVArrayType() ||
                            arg.getEnclosedType().isObjectType() ||
                            arg.getEnclosedType().isObjectTableType()) {
                            // handle advanced JDBC types
                            hasComplexProcedureArgs = true;
                            String typeString = arg.getTypeName().toLowerCase().concat(TYPE_STR);
                            xmlType = buildCustomQName(typeString, dbwsBuilder);
                        } else {
                            switch (Util.getJDBCTypeFromTypeName(arg.getTypeName())) {
                                case STRUCT:
                                case ARRAY:
                                    String typeString = nct.generateSchemaAlias(arg.getTypeName());
                                    xmlType = buildCustomQName(typeString, dbwsBuilder);
                                    break;
                                default :
                                    xmlType = getXMLTypeFromJDBCType(Util.getJDBCTypeFromTypeName(arg.getTypeName()));
                                    break;
                            }
                        }
                    }
                    if (direction == null || direction == IN) {
                        parm = new Parameter();
                        parm.setName(argName);
                        parm.setType(xmlType);

                        // handle optional arg
                        parm.setOptional(arg.optional());

                        pa = new ProcedureArgument();
                        pa.setName(argName);
                        pa.setParameterName(argName);
                        if (!hasComplexArgs && qh instanceof StoredProcedureQueryHandler) {
                            ((StoredProcedureQueryHandler)qh).getInArguments().add(pa);
                        }
                    } else {
                        // the first OUT/INOUT arg determines singleResult vs. collectionResult
                        pa = new ProcedureOutputArgument();
                        ProcedureOutputArgument pao = (ProcedureOutputArgument)pa;
                        pao.setName(argName);
                        pao.setParameterName(argName);
                        boolean isCursor = arg.isPLSQLCursorType() || arg.getTypeName().contains(CURSOR_STR);
                        
                        // for strongly typed ref cursors we will customize the simple-xml-format 
                        // tags to better represent the PL/SQL record/table/column type
                        if (arg.isPLSQLCursorType()) {
                            customizeSimpleXMLTagNames((PLSQLCursorType) arg.getEnclosedType(), procedureOperationModel);
                        }
                        if (isCursor && returnType == null) { // if user overrides returnType, assume they're right
                            pao.setResultType(SXF_QNAME_CURSOR);
                            if (result == null) {
                                result = new CollectionResult();
                                result.setType(SXF_QNAME_CURSOR);
                            }
                        } else {
                            // if user overrides returnType, assume they're right
                            // Hmm, multiple OUT's gonna be a problem - later!
                            if (returnType != null && !isSimpleXMLFormat) {
                                xmlType = qNameFromString(OPEN_PAREN + dbwsBuilder.getTargetNamespace() + CLOSE_PAREN +
                                    returnType, dbwsBuilder.getSchema());
                            }
                            if (isCursor) {
                                pao.setResultType(new QName(EMPTY_STRING, CURSOR_OF_STR + returnType));
                                Result newResult = new CollectionResult();
                                newResult.setType(result.getType());
                                result = newResult;
                            } else {
                                pao.setResultType(xmlType);
                            }
                            if (result == null) {
                                if (isCollection) {
                                    result = new CollectionResult();
                                } else {
                                    result = new Result();
                                }
                                result.setType(xmlType);
                            }
                        }
                        if (direction == INOUT) {
                            parm = new Parameter();
                            parm.setName(argName);
                            parm.setType(xmlType);
                            result.setType(xmlType);
                            // use of INOUT precludes SimpleXMLFormat
                            isSimpleXMLFormat = false;
                            if (!hasComplexArgs) {
                                if (qh instanceof StoredProcedureQueryHandler) {
                                    ((StoredProcedureQueryHandler)qh).getInOutArguments().add(pao);
                                }
                            } else {   // complex
                                paShadow = new ProcedureArgument();
                                paShadow.setName(argName);
                                paShadow.setParameterName(argName);
                            }
                        } else if (!hasComplexArgs) {
                            if (qh instanceof StoredProcedureQueryHandler) {
                                ((StoredProcedureQueryHandler)qh).getOutArguments().add(pao);
                            }
                        }
                    }
                    if (hasComplexArgs && arg.getEnclosedType().isPLSQLType()) {
                        pa.setComplexTypeName(storedProcedure.getCatalogName() + UNDERSCORE + arg.getTypeName());
                        if (paShadow != null) {
                            paShadow.setComplexTypeName(pa.getComplexTypeName());
                        }
                    }
                    if (parm != null) {
                        qo.getParameters().add(parm);
                    }
                }
            }
            if (procedureOperationModel.getBinaryAttachment()) {
                Attachment attachment = new Attachment();
                attachment.setMimeType(APP_OCTET_STREAM);
                result.setAttachment(attachment);
            }
            // the user may want simpleXMLFormat
            handleSimpleXMLFormat(isSimpleXMLFormat, result, procedureOperationModel);
            qo.setResult(result);
            dbwsBuilder.getXrServiceModel().getOperations().put(qo.getName(), qo);
        }
        finishProcedureOperation();
    }

    /**
     * Build and return a Result instance based on a given ProcedureType
     * and return type name.
     */
    protected Result buildResultForStoredFunction(ArgumentType returnArgument, String returnType) {
        Result result = null;
        DatabaseType rargDataType = returnArgument.getEnclosedType();

        // handle ref cursor
        if (rargDataType.isPLSQLCursorType() || returnArgument.getTypeName().contains(CURSOR_STR)) {
            result = new CollectionResult();
            result.setType(SXF_QNAME_CURSOR);
        } else {
            result = new Result();
            int rargJdbcType = OTHER;
            if (rargDataType.isComposite()) {
                if (rargDataType.isObjectType()) {
                    rargJdbcType = STRUCT;
                } else if (rargDataType.isVArrayType() || rargDataType.isObjectTableType()) {
                    rargJdbcType = ARRAY;
                }
            } else {
                rargJdbcType = Util.getJDBCTypeFromTypeName(returnArgument.getTypeName());
            }
            switch (rargJdbcType) {
                case OTHER:
                    // if user overrides returnType, assume they're right
                    if (returnType == null || returnType.length() == 0) {
                        returnType = rargDataType.getTypeName();
                    }
                    // packages only apply to PL/SQL types
                    String packageName = null;
                    if (rargDataType.isPLSQLType()) {
                        packageName = ((PLSQLType) rargDataType).getParentType().getPackageName();
                    }
                    String returnTypeName = (packageName != null && packageName.length() > 0) ?
                            packageName + UNDERSCORE + returnType : returnType;
                    result.setType(buildCustomQName(returnTypeName, dbwsBuilder));
                    break;
                case STRUCT:
                case ARRAY:
                    // if user overrides returnType, assume they're right
                    if (returnType == null || returnType.length() == 0) {
                        returnType = rargDataType.getTypeName().toLowerCase().concat(TYPE_STR);
                    }
                    result.setType(buildCustomQName(returnType, dbwsBuilder));
                    break;
                default :
                    // scalar types
                    result.setType(getXMLTypeFromJDBCType(rargJdbcType));
                    break;
            }
        }
        return result;
    }
    

    /**
     * Returns the name to be used for a QueryOperation (or Query) based on a
     * given ProcedureType and ProcedureOperationModel.
     *
     * The returned string will be:
     *
     * 1) If the given ProcedureOperationModel 'name' is a non-null & non-empty
     * string, the returned string will be one of the following:
     *   a) 'modelName' if no pattern matching or overloading
     *   b) 'modelName_procedureName' if pattern matching and no overloading
     *   c) 'modelName_overload' if overloading and no pattern matching
     *   d) 'modelName_procedureName_overload' if pattern matching & overloading
     *
     * OR
     *
     * 2) If the given ProcedureOperationModel 'name' is a null or empty string, the
     * returned string will be in the format: 'overload_catalog_schema_procedureName'
     */
    protected String getNameForQueryOperation(ProcedureOperationModel opModel, ProcedureType storedProcedure) {
        StringBuilder sb = new StringBuilder();
        String modelName = opModel.getName();

        if (modelName != null && modelName.length() > 0) {
            sb.append(modelName);
            // handle pattern matching
            if (opModel.getProcedurePattern().contains(Util.PERCENT)) {
                sb.append(UNDERSCORE);
                sb.append(storedProcedure.getProcedureName());
            }
            // handle overload
            if (storedProcedure.getOverload() != 0) {
                sb.append(UNDERSCORE);
                sb.append(storedProcedure.getOverload());
            }
        } else {
            if (storedProcedure.getOverload() > 0) {
                sb.append(storedProcedure.getOverload());
                sb.append(UNDERSCORE);
            }
            if (storedProcedure.getCatalogName() != null && storedProcedure.getCatalogName().length() > 0) {
                sb.append(storedProcedure.getCatalogName());
                sb.append(UNDERSCORE);
            }
            if (storedProcedure.getSchema() != null && storedProcedure.getSchema().length() > 0) {
                sb.append(storedProcedure.getSchema());
                sb.append(UNDERSCORE);
            }
            sb.append(storedProcedure.getProcedureName());
        }
        return sb.toString();
    }

    /**
     * Returns the qualified stored procedure name based on a given ProcedureType
     * and ProcedureOperationModel.
     *
     * The returned string will be in the format: 'schema.catalog.procedureName'
     *
     */
    protected String getQualifiedProcedureName(ProcedureOperationModel procedureOperationModel, ProcedureType storedProcedure) {
        StringBuilder sb = new StringBuilder();
        if (procedureOperationModel.getSchemaPattern() != null &&
            procedureOperationModel.getSchemaPattern().length() > 0 &&
            storedProcedure.getSchema() != null &&
            storedProcedure.getSchema().length() > 0) {
            sb.append(storedProcedure.getSchema());
            sb.append(DOT);
        }
        if (storedProcedure.getCatalogName() != null && storedProcedure.getCatalogName().length() > 0) {
            sb.append(storedProcedure.getCatalogName());
            sb.append(DOT);
        }
        sb.append(storedProcedure.getProcedureName());
        return sb.toString();
    }

    /**
     * Generates a List<TableType> based on a given set of patterns.
     */
    protected List<TableType> loadTables(List<String> catalogPatterns, List<String> schemaPatterns,
        List<String> tableNamePatterns) {
        try {
            return dtBuilder.buildTables(dbwsBuilder.getConnection(), schemaPatterns,
                tableNamePatterns);
        }
        catch (ParseException e) {
            //TODO - figure out what to do with a ParseException
        }
        return null;
    }

    /**
     * Generates a List<ProcedureType> based on a given set of patterns.
     */
    protected List<ProcedureType> loadProcedures(List<String> catalogPatterns, List<String> schemaPatterns,
        List<String> procedureNamePatterns) {
        List<ProcedureType> allProcsAndFuncs = new ArrayList<ProcedureType>();
        List<String> topLevelSchemaPatterns = new ArrayList<String>();
        List<String> topLevelProcedureNamePatterns = new ArrayList<String>();
        Map<String, Set<String>> packagePatterns = new HashMap<String,Set<String>>();
        for (int i = 0, len = catalogPatterns.size(); i < len; i++) {
            String catalogPattern = catalogPatterns.get(i);
            String schemaPattern = schemaPatterns.get(i);
            if (schemaPattern == null) {
                schemaPattern = dbwsBuilder.getUsername().toUpperCase();
            }
            if (catalogPattern == null || catalogPattern.length() == 0 ||
                TOPLEVEL.equals(catalogPattern)) {
                topLevelSchemaPatterns.add(schemaPattern);
                topLevelProcedureNamePatterns.add(procedureNamePatterns.get(i));
            }
            else {
                Set<String> packageNames = packagePatterns.get(schemaPattern);
                if (packageNames == null) {
                    packageNames = new HashSet<String>();
                    packagePatterns.put(schemaPattern, packageNames);
                }
                packageNames.add(catalogPattern);
            }
        }
        if (topLevelProcedureNamePatterns.size() > 0) {
            try {
                List<ProcedureType> topLevelProcedures = dtBuilder.buildProcedures(dbwsBuilder.getConnection(),
                    topLevelSchemaPatterns, topLevelProcedureNamePatterns);
                if (topLevelProcedures != null && topLevelProcedures.size() > 0) {
                    allProcsAndFuncs.addAll(topLevelProcedures);
                }
            }
            catch (ParseException e) {
                //e.printStackTrace();
                //TODO - not sure what to do with ParseException
            }
            try {
                List<FunctionType> topLevelFunctions = dtBuilder.buildFunctions(dbwsBuilder.getConnection(),
                    topLevelSchemaPatterns, topLevelProcedureNamePatterns);
                if (topLevelFunctions != null && topLevelFunctions.size() > 0) {
                    allProcsAndFuncs.addAll(topLevelFunctions);
                }
            }
            catch (ParseException e) {
                //e.printStackTrace();
                //TODO - not sure what to do with ParseException
            }
        }
        if (packagePatterns.size() > 0) {
            try {
                //unravel map
                List<String> schemaPats = new ArrayList<String>();
                List<String> packagePats = new ArrayList<String>();
                for (String schema : packagePatterns.keySet()) {
                    Set<String> packageNames = packagePatterns.get(schema);
                    for (String packageName : packageNames) {
                        schemaPats.add(schema);
                        packagePats.add(packageName);
                    }
                }
                List<PLSQLPackageType> packages = dtBuilder.buildPackages(dbwsBuilder.getConnection(),
                    schemaPats, packagePats);
                if (packages == null || packages.isEmpty()) {
                	logPackageNotFoundWarnings(NO_PKG_MSG, schemaPats, packagePats);
                } else {
	                for (PLSQLPackageType pakage : packages) {
	                    //check DDL generation
	                    ShadowDDLGenerator ddlGenerator = new ShadowDDLGenerator(pakage);
	                    dbwsBuilder.getTypeDDL().addAll(ddlGenerator.getAllCreateDDLs());
	                    dbwsBuilder.getTypeDropDDL().addAll(ddlGenerator.getAllDropDDLs());
	                    //check for overloading
	                    Map<String, List<ProcedureType>> overloadMap = new HashMap<String, List<ProcedureType>>();
	                    List<ProcedureType> procedures = pakage.getProcedures();
	                    for (ProcedureType procedure : procedures) {
	                        String procedureName = procedure.getProcedureName();
	                        List<ProcedureType> multipleProcedures = overloadMap.get(procedureName);
	                        if (multipleProcedures == null) {
	                            multipleProcedures = new ArrayList<ProcedureType>();
	                            overloadMap.put(procedureName, multipleProcedures);
	                        }
	                        multipleProcedures.add(procedure);
	                    }
	                    for (List<ProcedureType> procs : overloadMap.values()) {
	                        if (procs.size() >1) {
	                            for (int i = 0, len = procs.size(); i < len; i++) {
	                                procs.get(i).setOverload(i);
	                            }
	                        }
	                    }
	                    //check against procedureNamePatterns
	                    String tmp = "";
	                    for (int i = 0, len = procedureNamePatterns.size(); i < len; i++) {
	                        tmp += procedureNamePatterns.get(i);
	                        if (i < len -1) {
	                            tmp += "|";
	                        }
	                    }
	                    for (ProcedureType procedure : procedures) {
	                        if (sqlMatch(tmp, procedure.getProcedureName())) {
	                            allProcsAndFuncs.add(procedure);
	                        }
	                    }
	                }
                }
            }
            catch (ParseException e) {
                //e.printStackTrace();
                //TODO - not sure what to do with ParseException
            }
        }
        return allProcsAndFuncs.isEmpty() ? null : allProcsAndFuncs;
    }

    public void addToOROXProjectsForComplexTypes(List<CompositeDatabaseType> types, Project orProject, Project oxProject) {
        for (DatabaseType dbType : types) {
            String name;
            String alias;
            if (dbType.isPLSQLType()) {
                String catalogPattern = ((PLSQLType) dbType).getParentType().getPackageName();
                String targetTypeName;
                // for types enclosed in a ROWTYPEType, package doesn't apply
                if (catalogPattern == null) {
                    name = dbType.getTypeName();
                    targetTypeName = dbType.getTypeName();
                } else {
                    name = catalogPattern + DOT + dbType.getTypeName();
                    targetTypeName = catalogPattern + UNDERSCORE + dbType.getTypeName();
                }
                alias = targetTypeName.toLowerCase();
                
                // remove '%' from target and name
                name = name.replace(PERCENT, UNDERSCORE);
                targetTypeName = targetTypeName.replace(PERCENT, UNDERSCORE);
                
                // handle PL/SQL record type
                if (dbType.isPLSQLRecordType()) {
                    addToOXProjectForPLSQLRecordArg(dbType, oxProject, name, alias, targetTypeName, catalogPattern);
                    addToORProjectForPLSQLRecordArg(dbType, orProject, name, alias, targetTypeName, catalogPattern);
                }  // handle PL/SQL collection type
                else {
                    addToOXProjectForPLSQLTableArg(dbType, oxProject, name, alias, targetTypeName, catalogPattern);
                    addToORProjectForPLSQLTableArg(dbType, orProject, name, alias, targetTypeName, catalogPattern);
                }
            }
            else {
                // Advanced JDBC types need the (Java) package name prepended to the type name
                if (Util.isTypeComplex(dbType)) {
                    name = getGeneratedJavaClassName(dbType.getTypeName().toLowerCase(), dbwsBuilder.getProjectName());
                } else {
                    name = dbType.getTypeName();
                }
                alias = dbType.getTypeName().toLowerCase();
                // handle VArray type
                if (dbType.isVArrayType()) {
                    addToOXProjectForVArrayArg(dbType, oxProject, name, alias);
                    addToORProjectForVArrayArg(dbType, orProject, name, alias);
                }  // handle ObjectType type
                else if (dbType.isObjectType()) {
                    addToOXProjectForObjectTypeArg(dbType, oxProject, name, alias);
                    addToORProjectForObjectTypeArg(dbType, orProject, name, alias);
                } // handle ObjectTable type
                else if (dbType.isObjectTableType()) {
                    addToOXProjectForObjectTableTypeArg(dbType, oxProject, name, alias);
                    addToORProjectForObjectTableTypeArg(dbType, orProject, name, alias);
                }
            }
        }
    }
    
    /**
     * Build descriptor and mappings for a PL/SQL record argument.  The newly
     * created descriptor will be added to the given OX project.
     */
    protected void addToOXProjectForPLSQLRecordArg(DatabaseType dbType, Project oxProject, String recordName, String recordAlias, String targetTypeName, String catalogPattern) {
        XMLDescriptor xdesc = (XMLDescriptor) oxProject.getDescriptorForAlias(recordAlias);
        if (xdesc == null) {
            xdesc = buildAndAddNewXMLDescriptor(oxProject, recordAlias, recordName.toLowerCase(), targetTypeName, buildCustomQName(targetTypeName, dbwsBuilder).getNamespaceURI());
        }
        // handle fields
        PLSQLRecordType plsqlRecType = (PLSQLRecordType) dbType;
        for (FieldType fType : plsqlRecType.getFields()) {
            String lFieldName = fType.getFieldName().toLowerCase();
            if (xdesc.getMappingForAttributeName(lFieldName) == null) {
                if (fType.isComposite()) {
                    // handle pl/sql record and pl/sql table fields
                    if (fType.getEnclosedType().isPLSQLRecordType()) {
                        buildAndAddXMLCompositeObjectMapping(xdesc, lFieldName, (catalogPattern + DOT + fType.getEnclosedType()).toLowerCase());
                    } else if (fType.getEnclosedType().isPLSQLCollectionType()) {
                        PLSQLCollectionType tableType = (PLSQLCollectionType) fType.getEnclosedType();
                        if (tableType.getEnclosedType().isComposite()) {
                            buildAndAddXMLCompositeObjectMapping(xdesc, lFieldName, (catalogPattern + DOT + tableType.getTypeName()).toLowerCase() + COLLECTION_WRAPPER_SUFFIX);
                        } else {
                            Class<?> attributeElementClass = String.class;
                            XMLDescriptor refDesc = (XMLDescriptor) oxProject.getDescriptorForAlias((catalogPattern + UNDERSCORE + tableType.getTypeName()).toLowerCase());
                            if (refDesc != null) {
                                attributeElementClass = ((XMLCompositeDirectCollectionMapping)refDesc.getMappingForAttributeName(ITEMS_MAPPING_ATTRIBUTE_NAME)).getAttributeElementClass();
                            }
                            buildAndAddXMLCompositeDirectCollectionMapping(xdesc, lFieldName, lFieldName + SLASH + ITEM_MAPPING_NAME + SLASH + TEXT, attributeElementClass);
                        }
                    } else if (fType.getEnclosedType().isObjectType()) {
                        buildAndAddXMLCompositeObjectMapping(xdesc, lFieldName, getGeneratedJavaClassName(fType.getEnclosedType().getTypeName(), dbwsBuilder.getProjectName()));
                    } else if (fType.getEnclosedType().isVArrayType()) {
                        if (((VArrayType)fType.getEnclosedType()).getEnclosedType().isComposite()) {
                            String nestedTypeAlias = ((VArrayType) fType.getEnclosedType()).getEnclosedType().getTypeName().toLowerCase();
                            String nestedTypeName = getGeneratedJavaClassName(nestedTypeAlias, dbwsBuilder.getProjectName());
                            buildAndAddXMLCompositeCollectionMapping(xdesc, lFieldName, lFieldName + SLASH + ITEM_MAPPING_NAME, nestedTypeName);
                        } else {
                            buildAndAddXMLCompositeDirectCollectionMapping(xdesc, lFieldName, lFieldName + SLASH + ITEM_MAPPING_NAME + SLASH + TEXT, getAttributeClassForDatabaseType(fType.getEnclosedType()));
                        }
                    } else if (fType.getEnclosedType().isObjectTableType()) {
                    	ObjectTableType nestedType = (ObjectTableType) fType.getEnclosedType();
                    	if (nestedType.getEnclosedType().isComposite()) {
	                        String nestedTypeAlias = nestedType.getEnclosedType().getTypeName().toLowerCase();
	                        String nestedTypeName = getGeneratedJavaClassName(nestedTypeAlias, dbwsBuilder.getProjectName());
	                        // ObjectType is composite
	                        buildAndAddXMLCompositeCollectionMapping(xdesc, lFieldName, lFieldName + SLASH + ITEM_MAPPING_NAME, nestedTypeName);
                    	} else {
                            buildAndAddXMLCompositeDirectCollectionMapping(xdesc, lFieldName, lFieldName + SLASH + TEXT, getAttributeClassForDatabaseType(nestedType));
                    	}
                    }
                } else {
                    // direct mapping
                    addDirectMappingForFieldType(xdesc, lFieldName, fType);
                }
            }
        }
    }

    /**
     * Build descriptor and mappings for a PL/SQL record argument.  The newly
     * created descriptor will be added to the given OR project.
     */
    @SuppressWarnings("rawtypes")
    protected void addToORProjectForPLSQLRecordArg(DatabaseType dbType, Project orProject, String recordName, String recordAlias, String targetTypeName, String catalogPattern) {
        ObjectRelationalDataTypeDescriptor ordtDesc = (ObjectRelationalDataTypeDescriptor) orProject.getDescriptorForAlias(recordAlias);
        if (ordtDesc == null) {
            ordtDesc = buildAndAddNewObjectRelationalDataTypeDescriptor(orProject, recordAlias, recordName.toLowerCase());
        }
        // handle fields
        PLSQLRecordType plsqlRecType = (PLSQLRecordType) dbType;
        for (FieldType fType : plsqlRecType.getFields()) {
            String fieldName = fType.getFieldName();
            String lFieldName = fieldName.toLowerCase();
            // handle field ordering
            boolean found = false;
            Vector orderedFields = ordtDesc.getOrderedFields();
            for (Iterator i = orderedFields.iterator(); i.hasNext();) {
                Object o = i.next();
                if (o instanceof DatabaseField) {
                    DatabaseField field = (DatabaseField)o;
                    if (field.getName().equalsIgnoreCase(fieldName)) {
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                ordtDesc.addFieldOrdering(fieldName);
            }
            if (ordtDesc.getMappingForAttributeName(lFieldName) == null) {
                if (fType.isComposite()) {
                    if (fType.getEnclosedType().isPLSQLRecordType()) {
                        buildAndAddStructureMapping(ordtDesc, lFieldName, fieldName, recordName.toLowerCase());
                    } else if (fType.getEnclosedType().isPLSQLCollectionType()) {
                        PLSQLCollectionType tableType = (PLSQLCollectionType) fType.getEnclosedType();
                        if (tableType.getEnclosedType().isComposite()) {
                            buildAndAddObjectArrayMapping(ordtDesc, lFieldName, fieldName, (catalogPattern + "." + tableType.getTypeName()).toLowerCase() + COLLECTION_WRAPPER_SUFFIX, getStructureNameForField(fType, catalogPattern));
                        } else {
                            buildAndAddArrayMapping(ordtDesc, lFieldName, fieldName, getStructureNameForField(fType, catalogPattern));
                        }
                    } else if (fType.getEnclosedType().isObjectType()) {
                        buildAndAddStructureMapping(ordtDesc, lFieldName, fieldName, getGeneratedJavaClassName(fType.getEnclosedType().getTypeName(), dbwsBuilder.getProjectName()));
                    } else if (fType.getEnclosedType().isVArrayType()) {
                        if (((VArrayType)fType.getEnclosedType()).getEnclosedType().isComposite()) {
                            buildAndAddObjectArrayMapping(ordtDesc, lFieldName, fieldName, getGeneratedJavaClassName(((VArrayType)fType.getEnclosedType()).getEnclosedType().getTypeName(), dbwsBuilder.getProjectName()), getStructureNameForField(fType, null));
                        } else {
                            buildAndAddArrayMapping(ordtDesc, lFieldName, fieldName, getStructureNameForField(fType, null));
                        }
                    } else if (fType.getEnclosedType().isObjectTableType()) {
                    	ObjectTableType nestedType = (ObjectTableType) fType.getEnclosedType();
                    	if (nestedType.getEnclosedType().isComposite()) {
                    		ObjectType oType = (ObjectType) nestedType.getEnclosedType();
	                        String oTypeAlias = oType.getTypeName().toLowerCase();
	                        String oTypeName = getGeneratedJavaClassName(oTypeAlias, dbwsBuilder.getProjectName());
	                        // ObjectType is composite
	                        buildAndAddObjectArrayMapping(ordtDesc, lFieldName, fieldName, oTypeName, oTypeAlias.toUpperCase());
                    	} else {
                            buildAndAddArrayMapping(ordtDesc, lFieldName, fieldName, nestedType.getTypeName().toUpperCase());
                    	}
                    }
                } else {
                    // direct mapping
                    ordtDesc.addDirectMapping(lFieldName, fieldName);
                }
            }
        }
    }

    /**
     * Build descriptor and mappings for a PL/SQL collection argument.  The newly
     * created descriptor will be added to the given OX project.
     */
    protected void addToOXProjectForPLSQLTableArg(DatabaseType dbType, Project oxProject, String tableName, String tableAlias, String targetTypeName, String catalogPattern) {
        XMLDescriptor xdesc = (XMLDescriptor) oxProject.getDescriptorForAlias(tableAlias);
        if (xdesc == null) {
            xdesc = buildAndAddNewXMLDescriptor(oxProject, tableAlias, tableName.toLowerCase() + COLLECTION_WRAPPER_SUFFIX, targetTypeName, buildCustomQName(targetTypeName, dbwsBuilder).getNamespaceURI());
        }

        boolean itemsMappingFound = xdesc.getMappingForAttributeName(ITEMS_MAPPING_ATTRIBUTE_NAME) == null ? false : true;
        if (!itemsMappingFound) {
            DatabaseType nestedType = ((PLSQLCollectionType)dbType).getEnclosedType();
            if (nestedType.isPLSQLRecordType()) {
                String referenceClassName = (catalogPattern + DOT + ((PLSQLRecordType)nestedType).getTypeName()).toLowerCase();
                buildAndAddXMLCompositeCollectionMapping(xdesc, referenceClassName);
                if (oxProject.getDescriptorForAlias(referenceClassName) == null) {
                    String refTypeName = catalogPattern + UNDERSCORE + ((PLSQLRecordType)nestedType).getTypeName();
                    addToOXProjectForPLSQLRecordArg(nestedType, oxProject, referenceClassName, refTypeName.toLowerCase(), refTypeName, catalogPattern);
                }
            } else if (nestedType.isObjectType()) {
                buildAndAddXMLCompositeCollectionMapping(xdesc, getGeneratedJavaClassName(nestedType.getTypeName(), dbwsBuilder.getProjectName()));
            } else {
                if (nestedType.isComposite()) {
                    buildAndAddXMLCompositeCollectionMapping(xdesc, tableName.toLowerCase() + COLLECTION_WRAPPER_SUFFIX);
                }
                else {
                	buildAndAddXMLCompositeDirectCollectionMapping(xdesc, ITEMS_MAPPING_ATTRIBUTE_NAME, ITEM_MAPPING_NAME + SLASH + TEXT, getAttributeClassForDatabaseType(nestedType));
                }
            }
        }
    }

    /**
     * Build descriptor and mappings for a PL/SQL collection argument.  The newly
     * created descriptor will be added to the given OR project.
     */
    protected void addToORProjectForPLSQLTableArg(DatabaseType dbType, Project orProject, String tableName, String tableAlias, String targetTypeName, String catalogPattern) {
        ObjectRelationalDataTypeDescriptor ordt = (ObjectRelationalDataTypeDescriptor) orProject.getDescriptorForAlias(tableAlias);
        if (ordt == null) {
            ordt = buildAndAddNewObjectRelationalDataTypeDescriptor(orProject, tableAlias, tableName.toLowerCase() + COLLECTION_WRAPPER_SUFFIX);
        }
        boolean itemsMappingFound = ordt.getMappingForAttributeName(ITEMS_MAPPING_ATTRIBUTE_NAME) == null ? false : true;
        if (!itemsMappingFound) {
            DatabaseType nestedType = ((PLSQLCollectionType) dbType).getEnclosedType();
            if (nestedType.isPLSQLRecordType()) {
                String referenceClassName = (catalogPattern + DOT + ((PLSQLRecordType)nestedType).getTypeName()).toLowerCase();
                buildAndAddObjectArrayMapping(ordt, ITEMS_MAPPING_ATTRIBUTE_NAME, ITEMS_MAPPING_FIELD_NAME, referenceClassName, targetTypeName);
                if (orProject.getDescriptorForAlias(referenceClassName) == null) {
                    String refTypeName = catalogPattern + UNDERSCORE + ((PLSQLRecordType)nestedType).getTypeName();
                    addToORProjectForPLSQLRecordArg(nestedType, orProject, referenceClassName, refTypeName.toLowerCase(), refTypeName, catalogPattern);
                }
            } else if (nestedType.isObjectType()) {
                buildAndAddObjectArrayMapping(ordt, ITEMS_MAPPING_ATTRIBUTE_NAME, ITEMS_MAPPING_FIELD_NAME, getGeneratedJavaClassName(nestedType.getTypeName(), dbwsBuilder.getProjectName()), targetTypeName);
            } else {
                buildAndAddArrayMapping(ordt, ITEMS_MAPPING_ATTRIBUTE_NAME, ITEMS_MAPPING_FIELD_NAME, targetTypeName);
            }
        }
    }

    /**
     * Build descriptor and mappings for a VArray argument.  The newly
     * created descriptor will be added to the given OX project.
     */
    protected void addToOXProjectForVArrayArg(DatabaseType dbType, Project oxProject, String arrayName, String arrayAlias) {
        DatabaseType nestedDbType = ((VArrayType)dbType).getEnclosedType();
        String referenceTypeAlias = nestedDbType.getTypeName().toLowerCase();
    	String referenceTypeName = getGeneratedJavaClassName(referenceTypeAlias, dbwsBuilder.getProjectName());
        XMLDescriptor xdesc = (XMLDescriptor)oxProject.getDescriptorForAlias(arrayAlias);
        if (xdesc == null) {
            xdesc = buildAndAddNewXMLDescriptor(oxProject, arrayAlias, arrayName + COLLECTION_WRAPPER_SUFFIX, nct.generateSchemaAlias(arrayAlias), buildCustomQName(arrayName, dbwsBuilder).getNamespaceURI());
            // before we add this descriptor, check if the nested type's descriptor
            // should be built and added first
            XMLDescriptor refXdesc = (XMLDescriptor)oxProject.getDescriptorForAlias(referenceTypeAlias);
            if (refXdesc == null) {
                if (nestedDbType.isObjectType()) {
                    addToOXProjectForObjectTypeArg(nestedDbType, oxProject, referenceTypeName, referenceTypeAlias);
                }
            }
        }
        boolean itemsMappingFound = xdesc.getMappingForAttributeName(ITEMS_MAPPING_ATTRIBUTE_NAME) == null ? false : true;
        if (!itemsMappingFound) {
            if (nestedDbType.isComposite()) {
                buildAndAddXMLCompositeCollectionMapping(xdesc, referenceTypeName);
            }
            else {
            	buildAndAddXMLCompositeDirectCollectionMapping(xdesc, ITEMS_MAPPING_ATTRIBUTE_NAME, ITEM_MAPPING_NAME + SLASH + TEXT, getAttributeClassForDatabaseType(nestedDbType));
            }
        }
    }

    /**
     * Build descriptor and mappings for a VArray argument.  The newly
     * created descriptor will be added to the given OX project.
     */
    protected void addToORProjectForVArrayArg(DatabaseType dbType, Project orProject, String arrayName, String arrayAlias) {
        DatabaseType nestedDbType = ((VArrayType)dbType).getEnclosedType();
        String referenceTypeAlias = nestedDbType.getTypeName().toLowerCase();
        String referenceTypeName = getGeneratedJavaClassName(referenceTypeAlias, dbwsBuilder.getProjectName());
        ObjectRelationalDataTypeDescriptor ordt = (ObjectRelationalDataTypeDescriptor)orProject.getDescriptorForAlias(arrayAlias);
        if (ordt == null) {
            ordt = buildAndAddNewObjectRelationalDataTypeDescriptor(orProject, arrayAlias, arrayName + COLLECTION_WRAPPER_SUFFIX);
            // before we add this descriptor, check if the nested type's descriptor
            // should be built and added first
            ClassDescriptor refdesc = orProject.getDescriptorForAlias(referenceTypeAlias);
            if (refdesc == null) {
                if (nestedDbType.isObjectType()) {
                    addToORProjectForObjectTypeArg(nestedDbType, orProject, referenceTypeName, referenceTypeAlias);
                }
            }
        }
        boolean itemsMappingFound = ordt.getMappingForAttributeName(ITEMS_MAPPING_ATTRIBUTE_NAME) == null ? false : true;
        if (!itemsMappingFound) {
            if (nestedDbType.isComposite()) {
            	buildAndAddObjectArrayMapping(ordt, ITEMS_MAPPING_ATTRIBUTE_NAME, ITEMS_MAPPING_FIELD_NAME, referenceTypeName, arrayName);
            }
            else {
            	buildAndAddArrayMapping(ordt, ITEMS_MAPPING_ATTRIBUTE_NAME, ITEMS_MAPPING_FIELD_NAME, arrayAlias.toUpperCase());
            }
        }
    }

    /**
     * Build descriptor and mappings for an Object type argument.  The
     * newly created descriptor will be added to the given OX project.
     */
    protected void addToOXProjectForObjectTypeArg(DatabaseType dbType, Project oxProject, String objectName, String objectAlias) {
        XMLDescriptor xdesc = (XMLDescriptor) oxProject.getDescriptorForAlias(objectAlias);
        if (xdesc == null) {
            xdesc = buildAndAddNewXMLDescriptor(oxProject, objectAlias, objectName, nct.generateSchemaAlias(objectAlias), buildCustomQName(objectName, dbwsBuilder).getNamespaceURI());
        }
        ObjectType oType = (ObjectType) dbType;
        for (FieldType field : oType.getFields()) {
            String lFieldName = field.getFieldName().toLowerCase();
            if (xdesc.getMappingForAttributeName(lFieldName) == null) {
                if (field.isComposite()) {
                    String targetTypeName2 = field.getEnclosedType().getTypeName();
                    String alias = targetTypeName2.toLowerCase();
                    XMLDescriptor xdesc2 = (XMLDescriptor) oxProject.getDescriptorForAlias(alias);
                    boolean buildDescriptor = xdesc2 == null;
                    if (buildDescriptor) {
                        xdesc2 = buildAndAddNewXMLDescriptor(oxProject, alias, nct.generateSchemaAlias(alias), buildCustomQName(targetTypeName2, dbwsBuilder).getNamespaceURI());
                    }
                    // handle ObjectType field
                    if (field.getEnclosedType().isObjectType()) {
                        if (buildDescriptor) {
                            // need to update the java class name on the descriptor to include package (project) name
                            xdesc2.setJavaClassName(getGeneratedJavaClassName(alias, dbwsBuilder.getProjectName()));
                            addToOXProjectForObjectTypeArg(field.getEnclosedType(), oxProject, xdesc2.getJavaClassName(), alias);
                        }
                        buildAndAddXMLCompositeObjectMapping(xdesc, lFieldName, xdesc2.getJavaClassName());
                    } else if (field.getEnclosedType().isVArrayType()) {
                        // handle VArray field
                        if (buildDescriptor) {
                            // need to update the java class name on the descriptor to include package (project) name
                            xdesc2.setJavaClassName(getGeneratedJavaClassName(alias, dbwsBuilder.getProjectName()));
                            addToOXProjectForVArrayArg(field.getEnclosedType(), oxProject, xdesc2.getJavaClassName(), alias);
                        }
                        buildAndAddXMLCompositeDirectCollectionMapping(xdesc, lFieldName, lFieldName + SLASH + TEXT, getAttributeClassForDatabaseType(field.getEnclosedType()));
                    } else if (field.getEnclosedType().isObjectTableType()) {
                        // handle ObjectTableType field
                        if (buildDescriptor) {
                            // need to update the java class name on the descriptor to include package (project) name
                            xdesc2.setJavaClassName(getGeneratedJavaClassName(alias, dbwsBuilder.getProjectName()));
                            // make sure the descriptor is built for the enclosed ObjectType
                            addToOXProjectForObjectTableTypeArg(field.getEnclosedType(), oxProject, targetTypeName2, alias);
                        }
                        ObjectTableType tableType = (ObjectTableType) field.getEnclosedType();
                        if (tableType.getEnclosedType().isComposite()) {
	                        String nestedTypeAlias = ((ObjectTableType) field.getEnclosedType()).getEnclosedType().getTypeName().toLowerCase();
	                        String nestedTypeName = getGeneratedJavaClassName(nestedTypeAlias, dbwsBuilder.getProjectName());
	                        buildAndAddXMLCompositeCollectionMapping(xdesc, lFieldName, lFieldName + SLASH + ITEM_MAPPING_NAME, nestedTypeName);
                        } else {
                            buildAndAddXMLCompositeDirectCollectionMapping(xdesc, lFieldName, lFieldName + SLASH + TEXT, getAttributeClassForDatabaseType(tableType));
                        }
                    }
                } else {
                    // direct mapping
                    addDirectMappingForFieldType(xdesc, lFieldName, field);
                }
            }
        }
    }

    /**
     * Build descriptor and mappings for an Object type argument.  The
     * newly created descriptor will be added to the given OX project.
     */
    @SuppressWarnings("rawtypes")
    protected void addToORProjectForObjectTypeArg(DatabaseType dbType, Project orProject, String objectName, String objectAlias) {
        ObjectRelationalDataTypeDescriptor ordt = (ObjectRelationalDataTypeDescriptor)orProject.getDescriptorForAlias(objectAlias);
        if (ordt == null) {
            ordt = buildAndAddNewObjectRelationalDataTypeDescriptor(orProject, objectAlias, objectName);
        }
        ObjectType oType = (ObjectType) dbType;
        for (FieldType fType : oType.getFields()) {
            String fieldName = fType.getFieldName();
            String lFieldName = fieldName.toLowerCase();

            // handle field ordering
            boolean found = false;
            Vector orderedFields = ordt.getOrderedFields();
            for (Iterator i = orderedFields.iterator(); i.hasNext();) {
                Object o = i.next();
                if (o instanceof DatabaseField) {
                    DatabaseField field = (DatabaseField)o;
                    if (field.getName().equalsIgnoreCase(fieldName)) {
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                ordt.addFieldOrdering(fieldName);
            }
            if (ordt.getMappingForAttributeName(lFieldName) == null) {
                if (fType.isComposite()) {
                    String targetTypeName2 = fType.getEnclosedType().getTypeName();
                    String alias = targetTypeName2.toLowerCase();
                    ObjectRelationalDataTypeDescriptor ordt2 = (ObjectRelationalDataTypeDescriptor)orProject.getDescriptorForAlias(alias);
                    boolean buildDescriptor = ordt2 == null;
                    if (buildDescriptor) {
                        ordt2 = buildAndAddNewObjectRelationalDataTypeDescriptor(orProject, alias);
                    }
                    // handle ObjectType field
                    if (fType.getEnclosedType().isObjectType()) {
                        if (buildDescriptor) {
                            // need to update the java class name on the descriptor to include package (project) name
                            ordt2.setJavaClassName(getGeneratedJavaClassName(alias, dbwsBuilder.getProjectName()));
                            addToORProjectForObjectTypeArg(fType.getEnclosedType(), orProject, ordt2.getJavaClassName(), alias);
                        }
                        buildAndAddStructureMapping(ordt, lFieldName, fieldName, ordt2.getJavaClassName());
                    } else if (fType.getEnclosedType().isVArrayType()) {
                        // handle VArray field
                        if (buildDescriptor) {
                            // need to update the java class name on the descriptor to include package (project) name
                            ordt2.setJavaClassName(getGeneratedJavaClassName(alias, dbwsBuilder.getProjectName()));
                            addToORProjectForVArrayArg(fType.getEnclosedType(), orProject, ordt2.getJavaClassName(), alias);
                        }
                        buildAndAddArrayMapping(ordt, lFieldName, fieldName, getStructureNameForField(fType, null));
                    } else if (fType.getEnclosedType().isObjectTableType()) {
                        if (buildDescriptor) {
                            // need to update the java class name on the descriptor to include package (project) name
                            ordt2.setJavaClassName(getGeneratedJavaClassName(alias, dbwsBuilder.getProjectName()));
                            // make sure the descriptor is built for the enclosed ObjectType
                            addToORProjectForObjectTableTypeArg(fType.getEnclosedType(), orProject, targetTypeName2, alias);
                        }
                        if (((ObjectTableType) fType.getEnclosedType()).getEnclosedType().isComposite()) {
	                        ObjectType nestedType = (ObjectType)((ObjectTableType) fType.getEnclosedType()).getEnclosedType();
	                        String nestedTypeAlias = nestedType.getTypeName().toLowerCase();
	                        String nestedTypeName = getGeneratedJavaClassName(nestedTypeAlias, dbwsBuilder.getProjectName());
	                        buildAndAddObjectArrayMapping(ordt, lFieldName, fieldName, nestedTypeName, nestedTypeAlias.toUpperCase());
                        } else {
                            buildAndAddArrayMapping(ordt, lFieldName, fieldName, alias.toUpperCase());
                        }
                    }
                } else {
                    // direct mapping
                    DirectToFieldMapping dfm = new DirectToFieldMapping();
                    dfm.setFieldName(fieldName);
                    dfm.setAttributeName(lFieldName);
                    ordt.addMapping(dfm);
                }
            }
        }
    }

    /**
     * Build descriptor and mappings for an OracleTableType argument.  The
     * newly created descriptor will be added to the given OX project.
     */
    protected void addToOXProjectForObjectTableTypeArg(DatabaseType dbType, Project oxProject, String objectTableName, String objectTableAlias) {
        XMLDescriptor xdesc = (XMLDescriptor) oxProject.getDescriptorForAlias(objectTableAlias);
        if (xdesc == null) {
            xdesc = buildAndAddNewXMLDescriptor(oxProject, objectTableAlias, objectTableName + COLLECTION_WRAPPER_SUFFIX, nct.generateSchemaAlias(objectTableAlias), buildCustomQName(objectTableName, dbwsBuilder).getNamespaceURI());
        }
        boolean itemsMappingFound = xdesc.getMappingForAttributeName(ITEMS_MAPPING_ATTRIBUTE_NAME) == null ? false : true;
        if (!itemsMappingFound) {
            DatabaseType nType = ((ObjectTableType) dbType).getEnclosedType();
            if (nType.isObjectType()) {
                ObjectType oType = (ObjectType)nType;
                String nestedTypeAlias = oType.getTypeName().toLowerCase();
                String nestedTypeName = getGeneratedJavaClassName(nestedTypeAlias, dbwsBuilder.getProjectName());
                addToOXProjectForObjectTypeArg(oType, oxProject, nestedTypeName, nestedTypeAlias);
                // ObjectType is composite
                buildAndAddXMLCompositeCollectionMapping(xdesc, nestedTypeName);
            } else {
                buildAndAddXMLCompositeDirectCollectionMapping(xdesc, ITEMS_MAPPING_ATTRIBUTE_NAME, ITEM_MAPPING_NAME + SLASH + TEXT, getAttributeClassForDatabaseType(nType));
            }
        }
    }

    /**
     * Build descriptor and mappings for an OracleTableType argument.  The
     * newly created descriptor will be added to the given OR project.
     */
    protected void addToORProjectForObjectTableTypeArg(DatabaseType dbType, Project orProject, String objectTableName, String objectTableAlias) {
        ObjectRelationalDataTypeDescriptor ordt = (ObjectRelationalDataTypeDescriptor)orProject.getDescriptorForAlias(objectTableAlias);
        if (ordt == null) {
            ordt = buildAndAddNewObjectRelationalDataTypeDescriptor(orProject, objectTableAlias, objectTableName + COLLECTION_WRAPPER_SUFFIX);
        }
        boolean itemsMappingFound = ordt.getMappingForAttributeName(ITEMS_MAPPING_ATTRIBUTE_NAME) == null ? false : true;
        if (!itemsMappingFound) {
            DatabaseType nestedType = ((ObjectTableType) dbType).getEnclosedType();
            if (nestedType.isObjectType()) {
                ObjectType oType = (ObjectType) nestedType;
                String nestedTypeAlias = oType.getTypeName().toLowerCase();
                String nestedTypeName = getGeneratedJavaClassName(nestedTypeAlias, dbwsBuilder.getProjectName());
                addToORProjectForObjectTypeArg(oType, orProject, nestedTypeName, nestedTypeAlias);
                // ObjectType is composite
                buildAndAddObjectArrayMapping(ordt, ITEMS_MAPPING_ATTRIBUTE_NAME, ITEMS_MAPPING_FIELD_NAME, nestedTypeName, nestedTypeAlias.toUpperCase());
            } else {
                buildAndAddArrayMapping(ordt, ITEMS_MAPPING_ATTRIBUTE_NAME, ITEMS_MAPPING_FIELD_NAME, objectTableAlias.toUpperCase());
            }
        }
    }

    /**
     * Build a Query for the given ProcedureType instance and add
     * it to the given OR project's list of queries.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	protected void buildQueryForProcedureType(ProcedureType procType, Project orProject, Project oxProject, ProcedureOperationModel opModel, boolean hasPLSQLArgs) {
    	// if there are one or more PL/SQL args, then we need a PLSQLStoredProcedureCall
        StoredProcedureCall call;
    	ArgumentType returnArg = procType.isFunctionType() ? ((FunctionType)procType).getReturnArgument() : null;

    	// check for PL/SQL cursor arg
        boolean hasCursor = hasPLSQLCursorArg(getArgumentListForProcedureType(procType));
        hasPLSQLArgs = hasPLSQLArgs || hasCursor;
    	if (hasPLSQLArgs) {
            if (procType.isFunctionType()) {
                org.eclipse.persistence.internal.helper.DatabaseType dType = buildDatabaseTypeFromMetadataType(returnArg, procType.getCatalogName());
                if (hasCursor) {
                    call = new PLSQLStoredFunctionCall();
                    // constructor by default adds a RETURN argument, so remove it
                    ((PLSQLStoredFunctionCall)call).getArguments().remove(0);
                    ((PLSQLStoredFunctionCall)call).useNamedCursorOutputAsResultSet(CURSOR_STR, dType);
                } else {
                	Class wrapperClass = getWrapperClass(dType);
                	if (wrapperClass != null) {
                		((ComplexDatabaseType) dType).setJavaType(wrapperClass);
                	}
                    call = new PLSQLStoredFunctionCall(dType);
                	// check for non-associative collection
                	if (returnArg.getEnclosedType().isPLSQLCollectionType() && !((PLSQLCollectionType)returnArg.getEnclosedType()).isIndexed()) {
                		PLSQLargument plsqlArg = ((PLSQLStoredFunctionCall)call).getArguments().get(0);
                		plsqlArg.setIsNonAssociativeCollection(true);
                	}
                }
            } else {
                call = new PLSQLStoredProcedureCall();
            }
        } else {
            if (procType.isFunctionType()) {
                String javaTypeName = returnArg.getTypeName();
                ClassDescriptor desc = oxProject.getDescriptorForAlias(javaTypeName.toLowerCase());
                if (desc != null) {
                    javaTypeName = desc.getJavaClassName();
                }

                if (returnArg.isComposite()) {
                    DatabaseType dataType = returnArg.getEnclosedType();
                    if (dataType.isVArrayType() || dataType.isObjectTableType()) {
                        call = new StoredFunctionCall(Types.ARRAY, returnArg.getTypeName(), javaTypeName,
                            buildFieldForNestedType(dataType));
                    }
                    else {
                        // assumes ObjectType
                        call = new StoredFunctionCall(Types.STRUCT, returnArg.getTypeName(), javaTypeName);
                    }
                } else {
                    call = new StoredFunctionCall();
                    if (returnArg.getEnclosedType().isBlobType()) {
                        // handle BLOBs
                        ((StoredFunctionCall) call).setResult(null, ClassConstants.BLOB);
                    } else {
                        // default to OBJECT
                        ((StoredFunctionCall) call).setResult(null, ClassConstants.OBJECT);
                    }
                }
            } else {
                call = new StoredProcedureCall();
            }
        }

        String cat = procType.getCatalogName();
        String catalogPrefix = (cat == null || cat.length() == 0) ? EMPTY_STRING : cat + DOT;
        call.setProcedureName(catalogPrefix + procType.getProcedureName());
        String returnType = opModel.getReturnType();
        boolean hasResponse = returnType != null;

        DatabaseQuery dq = null;
        if (hasCursor || (hasResponse && opModel.isCollection())) {
            dq = new DataReadQuery();
        } else {
            dq = new ValueReadQuery();
        }
        dq.bindAllParameters();
        dq.setName(getNameForQueryOperation(opModel, procType));
        dq.setCall(call);

        for (ArgumentType arg : procType.getArguments()) {
            // handle optional arg
            if (arg.optional()) {
                call.addOptionalArgument(arg.getArgumentName());
            }

            DatabaseType argType = arg.getEnclosedType();
            ArgumentTypeDirection direction = arg.getDirection();

            // for PL/SQL
            org.eclipse.persistence.internal.helper.DatabaseType databaseType = null;
            // for Advanced JDBC
            String javaTypeName = null;

            if (hasPLSQLArgs) {
                databaseType = buildDatabaseTypeFromMetadataType(argType, cat);
            } else {
                javaTypeName = argType.getTypeName();
                ClassDescriptor desc = oxProject.getDescriptorForAlias(javaTypeName.toLowerCase());
                if (desc != null) {
                    // anything there's a descriptor for will include "packagename." in the class name
                    javaTypeName = desc.getJavaClassName();
                }
            }

            if (direction == IN) {
            	if (hasPLSQLArgs) {
                    Class wrapperClass = getWrapperClass(databaseType);
                    if (wrapperClass != null) {
                        ((ComplexDatabaseType) databaseType).setJavaType(wrapperClass);
                    }
                    ((PLSQLStoredProcedureCall)call).addNamedArgument(arg.getArgumentName(), databaseType);
                    // check for non-associative collection
                    if (argType.isPLSQLCollectionType() && !((PLSQLCollectionType)argType).isIndexed()) {
                        PLSQLargument plsqlArg = ((PLSQLStoredProcedureCall)call).getArguments().get(((PLSQLStoredProcedureCall)call).getArguments().size()-1);
                        plsqlArg.setIsNonAssociativeCollection(true);
                    }
                } else {
                    if (argType.isVArrayType()) {
                        dq.addArgument(arg.getArgumentName());
                        call.addNamedArgument(arg.getArgumentName(), arg.getArgumentName(),
                        Types.ARRAY, argType.getTypeName(), javaTypeName);
                    } else if (argType.isObjectType()) {
                        dq.addArgument(arg.getArgumentName());
                        call.addNamedArgument(arg.getArgumentName(), arg.getArgumentName(),
                        Types.STRUCT, argType.getTypeName(), javaTypeName);
                    } else if (argType.isObjectTableType()) {
                        dq.addArgument(arg.getArgumentName(), java.sql.Array.class);
                        call.addNamedArgument(arg.getArgumentName(), arg.getArgumentName(),
                        Types.ARRAY, argType.getTypeName(), buildFieldForNestedType(argType));
                    } else {
                        dq.addArgument(arg.getArgumentName());
                        call.addNamedArgument(arg.getArgumentName(), arg.getArgumentName(),
                        Util.getJDBCTypeFromTypeName(argType.getTypeName()));
                    }
                }
            } else if (direction == OUT) {
            	if (hasPLSQLArgs) {
            	    if(arg.isPLSQLCursorType()) {
            	        ((PLSQLStoredProcedureCall)call).useNamedCursorOutputAsResultSet(arg.getArgumentName(), databaseType);
            	    } else {
                        Class wrapperClass = getWrapperClass(databaseType);
                        if (wrapperClass != null) {
                            ((ComplexDatabaseType) databaseType).setJavaType(wrapperClass);
                        }
                        ((PLSQLStoredProcedureCall)call).addNamedOutputArgument(arg.getArgumentName(), databaseType);
            	    }
                } else {
                    if (argType.isComposite()) {
                        Class wrapperClass = getWrapperClass(javaTypeName);

                        if (argType.isVArrayType() || argType.isObjectTableType()) {
                            call.addNamedOutputArgument(arg.getArgumentName(), arg.getArgumentName(),
                                Types.ARRAY, argType.getTypeName(), wrapperClass, buildFieldForNestedType(argType));
                        } else {
                            // assumes ObjectType
                            call.addNamedOutputArgument(arg.getArgumentName(), arg.getArgumentName(),
                                Types.STRUCT, argType.getTypeName(), wrapperClass);
                        }
                    } else {
                        call.addNamedOutputArgument(arg.getArgumentName(), arg.getArgumentName(),
                        Util.getJDBCTypeFromTypeName(argType.getTypeName()));
                    }
            	}
            } else {  // INOUT
            	if (hasPLSQLArgs) {
            		((PLSQLStoredProcedureCall)call).addNamedInOutputArgument(arg.getArgumentName(), databaseType);
	            	// check for non-associative collection
	            	if (argType.isPLSQLCollectionType() && !((PLSQLCollectionType)argType).isIndexed()) {
	            		PLSQLargument plsqlArg = ((PLSQLStoredProcedureCall)call).getArguments().get(((PLSQLStoredProcedureCall)call).getArguments().size()-1);
	            		plsqlArg.setIsNonAssociativeCollection(true);
                    }
                } else {
                    dq.addArgument(arg.getArgumentName());
                    call.addNamedInOutputArgument(arg.getArgumentName());
                }
            }
            if (hasPLSQLArgs && (direction == IN || direction == INOUT)) {
                ClassDescriptor xdesc = null;
                if (hasResponse) {
                    int idx = returnType.indexOf(COLON);
                    if (idx == -1) {
                        idx = returnType.indexOf(CLOSE_PAREN);
                    }
                    if (idx > 0) {
                        String typ = returnType.substring(idx+1);
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

                if (xdesc != null) {
                    dq.addArgumentByTypeName(arg.getArgumentName(), xdesc.getJavaClassName());
                } else {
                    if (databaseType instanceof PLSQLCollection || databaseType instanceof VArrayType) {
                        dq.addArgument(arg.getArgumentName(), Array.class);
                    } else if (databaseType instanceof PLSQLrecord || databaseType instanceof OracleObjectType) {
                        dq.addArgument(arg.getArgumentName(), Struct.class);
                    } else {
                        dq.addArgument(arg.getArgumentName(),
                            JDBCTypes.getClassForCode(databaseType.getConversionCode()));
                    }
                }
            }
        }
        orProject.getQueries().add(dq);
    }

    /**
     * Build an OR database field for a given type's nested type.
     */
    protected ObjectRelationalDatabaseField buildFieldForNestedType(DatabaseType owningType) {
        ObjectRelationalDatabaseField nestedField = new ObjectRelationalDatabaseField("");
        DatabaseType nestedType;
        if (owningType.isVArrayType()) {
            nestedType = ((VArrayType)owningType).getEnclosedType();
            if (nestedType.isComposite()) {
                nestedField.setSqlTypeName(nestedType.getTypeName());
                nestedField.setSqlType(Types.STRUCT);
            }
            else {
                nestedField.setSqlTypeName(Util.getJDBCTypeNameFromType(Types.ARRAY));
                nestedField.setSqlType(Types.ARRAY);
            }
        }
        else {
            nestedType = ((ObjectTableType)owningType).getEnclosedType();
            nestedField.setSqlTypeName(nestedType.getTypeName());
            nestedField.setSqlType(Types.STRUCT);
        }
        nestedField.setTypeName(getGeneratedJavaClassName(nestedType.getTypeName().toLowerCase(), dbwsBuilder.getProjectName()));
        return nestedField;
    }

    /**
     * Create an XMLDirectMapping for a given FieldType instance, and add the
     * newly created mapping to the given XMLDescriptor.
     */
    protected void addDirectMappingForFieldType(XMLDescriptor xdesc, String attributeName, FieldType fType) {
        XMLDirectMapping fieldMapping = new XMLDirectMapping();
        fieldMapping.setAttributeName(attributeName);
        XMLField xField = new XMLField(attributeName + SLASH + TEXT);
        xField.setRequired(true);
        QName qnameFromDatabaseType = getXMLTypeFromJDBCType(org.eclipse.persistence.tools.dbws.Util.getJDBCTypeFromTypeName(fType.getTypeName()));
        xField.setSchemaType(qnameFromDatabaseType);
        // special case to avoid Calendar problems
        if (qnameFromDatabaseType == DATE_QNAME) {
            fieldMapping.setAttributeClassification(java.sql.Date.class);
            xField.addXMLConversion(DATE_QNAME, java.sql.Date.class);
            xField.addJavaConversion(java.sql.Date.class, DATE_QNAME);
            xdesc.getNamespaceResolver().put(SCHEMA_PREFIX, SCHEMA_URL);
        } else {
            Class<?> attributeClass = (Class<?>)XMLConversionManager.getDefaultXMLTypes().get(qnameFromDatabaseType);
            if (attributeClass == null) {
                attributeClass =  Object_Class;
            }
            fieldMapping.setAttributeClassification(attributeClass);
        }
        fieldMapping.setField(xField);
        AbstractNullPolicy nullPolicy = fieldMapping.getNullPolicy();
        nullPolicy.setNullRepresentedByEmptyNode(false);
        nullPolicy.setMarshalNullRepresentation(XSI_NIL);
        nullPolicy.setNullRepresentedByXsiNil(true);
        fieldMapping.setNullPolicy(nullPolicy);
        xdesc.getNamespaceResolver().put(SCHEMA_INSTANCE_PREFIX, SCHEMA_INSTANCE_URL); // to support xsi:nil policy
        xdesc.addMapping(fieldMapping);
    }

    /**
     * Build an XMLCompositeObjectMapping based on given attribute and reference
     * class names, and add the newly created mapping to the given descriptor.
     */
    protected void buildAndAddXMLCompositeObjectMapping(XMLDescriptor xdesc, String attributeName, String referenceClassName) {
        xdesc.addMapping(buildXMLCompositeObjectMapping(attributeName, referenceClassName));
    }
    /**
     * Build an XMLCompositeObjectMapping based on given attribute and reference
     * class names, and add the newly created mapping to the given descriptor.
     */
    protected void buildAndAddXMLCompositeObjectMapping(XMLDescriptor xdesc, String attributeName, String xpath, String referenceClassName) {
        xdesc.addMapping(buildXMLCompositeObjectMapping(attributeName, referenceClassName));
    }
    /**
     * Build an XMLCompositeObjectMapping based on given attribute
     * and reference class names.
     */
    protected XMLCompositeObjectMapping buildXMLCompositeObjectMapping(String attributeName, String referenceClassName) {
        return buildXMLCompositeObjectMapping(attributeName, attributeName, referenceClassName);
    }
    /**
     * Build an XMLCompositeObjectMapping based on given attribute
     * and reference class names.
     */
    protected XMLCompositeObjectMapping buildXMLCompositeObjectMapping(String attributeName, String xpath, String referenceClassName) {
        XMLCompositeObjectMapping mapping = new XMLCompositeObjectMapping();
        mapping.setAttributeName(attributeName);
        mapping.setXPath(xpath);
        XMLField xField = (XMLField)mapping.getField();
        xField.setRequired(true);
        mapping.setReferenceClassName(referenceClassName);
        return mapping;
    }

    /**
     * Build an XMLCompositeCollectionMapping based on a given attribute name,  xpath,
     * and reference class, and add the newly created mapping to the given descriptor.
     */
    protected void buildAndAddXMLCompositeCollectionMapping(XMLDescriptor xdesc, String attributeName, String xPath, String referenceClassName) {
        xdesc.addMapping(buildXMLCompositeCollectionMapping(attributeName, xPath, referenceClassName));
    }
    /**
     * Build an XMLCompositeCollectionMapping based on a given reference class
     * name, and add the newly created mapping to the given descriptor.
     */
    protected void buildAndAddXMLCompositeCollectionMapping(XMLDescriptor xdesc, String referenceClassName) {
        xdesc.addMapping(buildXMLCompositeCollectionMapping(referenceClassName));
    }
    /**
     * Build an XMLCompositeCollectionMapping based on a given reference class name.
     * The attribute name will be set to 'items', and the xpath set to 'item'.
     */
    protected XMLCompositeCollectionMapping buildXMLCompositeCollectionMapping(String referenceClassName) {
        return buildXMLCompositeCollectionMapping(ITEMS_MAPPING_ATTRIBUTE_NAME, ITEM_MAPPING_NAME, referenceClassName);
    }
    /**
     * Build an XMLCompositeCollectionMapping based on a given attribute name, xpath,
     * and reference class.
     */
    protected XMLCompositeCollectionMapping buildXMLCompositeCollectionMapping(String attributeName, String xPath, String referenceClassName) {
        XMLCompositeCollectionMapping itemsMapping = new XMLCompositeCollectionMapping();
        itemsMapping.setAttributeName(attributeName);
        itemsMapping.setXPath(xPath);
        ((XMLField)itemsMapping.getField()).setRequired(true);
        itemsMapping.useCollectionClass(ArrayList.class);
        itemsMapping.setReferenceClassName(referenceClassName);
        return itemsMapping;
    }

    /**
     * Build an XMLCompositeDirectCollectionMapping based on a given attribute name, xpath,
     * and attribute element class.  The newly created mapping will be added to the given
     * XML descriptor.
     */
    protected XMLCompositeDirectCollectionMapping buildAndAddXMLCompositeDirectCollectionMapping(XMLDescriptor xdesc, String attributeName, String xPath, Class<?> attributeElementClass) {
        XMLCompositeDirectCollectionMapping itemsMapping = buildXMLCompositeDirectCollectionMapping(attributeName, xPath, attributeElementClass);
        xdesc.getNamespaceResolver().put(SCHEMA_INSTANCE_PREFIX, SCHEMA_INSTANCE_URL); // to support xsi:nil policy
        xdesc.addMapping(itemsMapping);
        return itemsMapping;
    }
    /**
     * Build an XMLCompositeDirectCollectionMapping based on a given attribute name, xpath,
     * and attribute element class.
     */
    protected XMLCompositeDirectCollectionMapping buildXMLCompositeDirectCollectionMapping(String attributeName, String xPath, Class<?> attributeElementClass) {
        XMLCompositeDirectCollectionMapping itemsMapping = new XMLCompositeDirectCollectionMapping();
        itemsMapping.setAttributeElementClass(attributeElementClass);
        itemsMapping.setAttributeName(attributeName);
        itemsMapping.setUsesSingleNode(true);
        itemsMapping.setXPath(xPath);
        ((XMLField)itemsMapping.getField()).setRequired(true);
        itemsMapping.useCollectionClass(ArrayList.class);
        AbstractNullPolicy nullPolicy = itemsMapping.getNullPolicy();
        nullPolicy.setNullRepresentedByEmptyNode(false);
        nullPolicy.setMarshalNullRepresentation(XSI_NIL);
        nullPolicy.setNullRepresentedByXsiNil(true);
        itemsMapping.setNullPolicy(nullPolicy);
        return itemsMapping;
    }

    /**
     * Builds a StructureMapping based on a given attributeName, fieldName and reference
     * class name, and adds the newly created mapping to the given OR descriptor.
     */
    protected StructureMapping buildAndAddStructureMapping(ObjectRelationalDataTypeDescriptor orDesc, String attributeName, String fieldName, String referenceClassName) {
        StructureMapping structureMapping = buildStructureMapping(attributeName, fieldName, referenceClassName);
        orDesc.addMapping(structureMapping);
        return structureMapping;
    }
    /**
     * Builds a StructureMapping based on a given attributeName, fieldName
     * and reference class name.
     */
    protected StructureMapping buildStructureMapping(String attributeName, String fieldName, String referenceClassName) {
        StructureMapping structureMapping = new StructureMapping();
        structureMapping.setAttributeName(attributeName);
        structureMapping.setFieldName(fieldName);
        structureMapping.setReferenceClassName(referenceClassName);
        return structureMapping;
    }

    /**
     * Builds an ObjectArrayMapping based on a given attribute name, field name,
     * reference class name, field type and package name, and adds the newly
     * created mapping to the given OR descriptor.
     */
    protected ObjectArrayMapping buildAndAddObjectArrayMapping(ObjectRelationalDataTypeDescriptor orDesc, String attributeName, String fieldName, String referenceClassName, String structureName) {
        ObjectArrayMapping objectArrayMapping = buildObjectArrayMapping(attributeName, fieldName, referenceClassName, structureName);
        orDesc.addMapping(objectArrayMapping);
        return objectArrayMapping;
    }
    /**
     * Builds an ObjectArrayMapping based on a given attribute name, field name,
     * reference class name and structureName.
     */
    protected ObjectArrayMapping buildObjectArrayMapping(String attributeName, String fieldName, String referenceClassName, String structureName) {
        ObjectArrayMapping objectArrayMapping = new ObjectArrayMapping();
        objectArrayMapping.setAttributeName(attributeName);
        objectArrayMapping.setFieldName(fieldName);
        objectArrayMapping.setStructureName(structureName);
        objectArrayMapping.useCollectionClass(ArrayList.class);
        objectArrayMapping.setReferenceClassName(referenceClassName);
        return objectArrayMapping;
    }

    /**
     * Build an ArrayMapping based on a given attribute name, field name and structure
     * name.  The newly created mapping will be added to the given OR descriptor.
     */
    protected ArrayMapping buildAndAddArrayMapping(ObjectRelationalDataTypeDescriptor orDesc, String attributeName, String fieldName, String structureName) {
        ArrayMapping arrayMapping = buildArrayMapping(attributeName, fieldName, structureName);
        orDesc.addMapping(arrayMapping);
        return arrayMapping;
    }
    /**
     * Build an ArrayMapping based on a given attribute name, field name and structure
     * name.
     */
    protected ArrayMapping buildArrayMapping(String attributeName, String fieldName, String structureName) {
        ArrayMapping arrayMapping = new ArrayMapping();
        arrayMapping.setAttributeName(attributeName);
        arrayMapping.setFieldName(fieldName);
        arrayMapping.setStructureName(structureName);
        arrayMapping.useCollectionClass(ArrayList.class);
        return arrayMapping;
    }

    /**
     * Build an XMLDescriptor based on a given descriptor alias, schema alias and target
     * namespace, and add the newly created descriptor to the given OX Project.
     */
    protected XMLDescriptor buildAndAddNewXMLDescriptor(Project oxProject, String objectAlias, String userType, String targetNamespace) {
        return buildAndAddNewXMLDescriptor(oxProject, objectAlias, objectAlias, userType, targetNamespace);
    }
    /**
     * Build an XMLDescriptor based on a given descriptor alias, schema alias, java class name
     * and target namespace, and add the newly created descriptor to the given OX Project.
     */
    protected XMLDescriptor buildAndAddNewXMLDescriptor(Project oxProject, String objectAlias, String javaClassName, String userType, String targetNamespace) {
        XMLDescriptor xdesc = buildNewXMLDescriptor(objectAlias, javaClassName, userType, targetNamespace);
        oxProject.addDescriptor(xdesc);
        return xdesc;
    }
    /**
     * Build an XMLDescriptor based on a given descriptor alias,
     * schema alias, and target namespace.
     */
    protected XMLDescriptor buildNewXMLDescriptor(String objectAlias, String userType, String targetNamespace) {
        return buildNewXMLDescriptor(objectAlias, objectAlias, userType, targetNamespace);
    }
    /**
     * Build an XMLDescriptor based on a given descriptor alias,
     * java class name schema alias, and target namespace.
     */
    protected XMLDescriptor buildNewXMLDescriptor(String objectAlias, String javaClassName, String userType, String targetNamespace) {
        XMLDescriptor xdesc = new XMLDescriptor();
        xdesc.setAlias(objectAlias);
        xdesc.setJavaClassName(javaClassName);
        xdesc.getQueryManager();
        XMLSchemaURLReference schemaReference = new XMLSchemaURLReference();
        schemaReference.setSchemaContext(SLASH + userType);
        schemaReference.setType(org.eclipse.persistence.platform.xml.XMLSchemaReference.COMPLEX_TYPE);
        xdesc.setSchemaReference(schemaReference);
        NamespaceResolver nr = new NamespaceResolver();
        nr.setDefaultNamespaceURI(targetNamespace);
        xdesc.setNamespaceResolver(nr);
        xdesc.setDefaultRootElement(userType);
        return xdesc;
    }

    /**
     * Build an ObjectRelationalDataTypeDescriptor based on a given
     * descriptor alias and add it to the given OR Project.
     */
    protected ObjectRelationalDataTypeDescriptor buildAndAddNewObjectRelationalDataTypeDescriptor(Project orProject, String alias) {
        return buildAndAddNewObjectRelationalDataTypeDescriptor(orProject, alias, alias);
    }
    /**
     * Build an ObjectRelationalDataTypeDescriptor based on a given descriptor
     * alias and java class name, and add it to the given OR Project.
     */
    protected ObjectRelationalDataTypeDescriptor buildAndAddNewObjectRelationalDataTypeDescriptor(Project orProject, String alias, String javaClassName) {
        ObjectRelationalDataTypeDescriptor ordesc = buildNewObjectRelationalDataTypeDescriptor(alias, javaClassName);
        orProject.addDescriptor(ordesc);
        return ordesc;
    }
    /**
     * Build an ObjectRelationalDataTypeDescriptor based on a given
     * descriptor alias.
     */
    protected ObjectRelationalDataTypeDescriptor buildNewObjectRelationalDataTypeDescriptor(String alias) {
        return buildNewObjectRelationalDataTypeDescriptor(alias, alias);
    }
    /**
     * Build an ObjectRelationalDataTypeDescriptor based on a given
     * descriptor alias and java class name.
     */
    protected ObjectRelationalDataTypeDescriptor buildNewObjectRelationalDataTypeDescriptor(String alias, String javaClassName) {
        ObjectRelationalDataTypeDescriptor ordt = new ObjectRelationalDataTypeDescriptor();
        ordt.setStructureName(alias.toUpperCase());
        ordt.descriptorIsAggregate();
        ordt.setAlias(alias);
        ordt.setJavaClassName(javaClassName);
        ordt.getQueryManager();
        return ordt;
    }

    /**
     * Return the structure name to be set on a mapping based on a given
     * FieldType and packageName.
     */
    protected String getStructureNameForField(FieldType fType, String packageName) {
        DatabaseType type = fType.getEnclosedType();
        String structureName = type.getTypeName();
        if (packageName != null && packageName.length() > 0 && !packageName.equals(TOPLEVEL)) {
            structureName = packageName + UNDERSCORE + structureName;
        }
        return structureName;
    }
    
    /**
     * Customizes the simple-xml-format tags names to better represent the 
     * PL/SQL record/table/column type. This is possible only with 
     * strongly-typed ref cursors, since for weakly-typed ones we 
     * don't know anything about the cursor's output type.
     */
    protected void customizeSimpleXMLTagNames(PLSQLCursorType plsqlCursor, ProcedureOperationModel procedureOperationModel) {
        if (!plsqlCursor.isWeaklyTyped()) {
            // do not override user tag customization
            if (procedureOperationModel.getSimpleXMLFormatTag() == null) {
                procedureOperationModel.setSimpleXMLFormatTag(plsqlCursor.getCursorName());
            }
            
            // Enclosed type could be one of:
            // - PLSQLRecordType
            // - ROWTYPEType
            // - TYPEType
            if (procedureOperationModel.getXmlTag() == null) {
                if (plsqlCursor.getEnclosedType().isPLSQLRecordType()) {
                    PLSQLRecordType recType = (PLSQLRecordType) plsqlCursor.getEnclosedType();
                    procedureOperationModel.setXmlTag(recType.getTypeName());
                } else if (plsqlCursor.getEnclosedType().isROWTYPEType()) {
                    // assumes ROWTYPEType has an enclosed TableType
                    ROWTYPEType rowType = (ROWTYPEType) plsqlCursor.getEnclosedType();
                    TableType tableType = (TableType) rowType.getEnclosedType();
                    procedureOperationModel.setXmlTag(tableType.getTableName());
                } else {
                    // TODO:  handle TYPEType
                }
            }
        }
    }
}