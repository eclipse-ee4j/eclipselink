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
package org.eclipse.persistence.tools.dbws.oracle;

//javase imports
import static java.sql.Types.ARRAY;
import static java.sql.Types.OTHER;
import static java.sql.Types.STRUCT;
import static java.util.logging.Level.FINEST;
import static org.eclipse.persistence.internal.helper.ClassConstants.Object_Class;
import static org.eclipse.persistence.internal.xr.Util.SXF_QNAME;
import static org.eclipse.persistence.internal.xr.XRDynamicClassLoader.COLLECTION_WRAPPER_SUFFIX;
import static org.eclipse.persistence.internal.xr.sxf.SimpleXMLFormat.DEFAULT_SIMPLE_XML_FORMAT_TAG;
import static org.eclipse.persistence.oxm.XMLConstants.ANY_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.DATE_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.SCHEMA_INSTANCE_PREFIX;
import static org.eclipse.persistence.oxm.XMLConstants.SCHEMA_INSTANCE_URL;
import static org.eclipse.persistence.oxm.XMLConstants.SCHEMA_PREFIX;
import static org.eclipse.persistence.oxm.XMLConstants.SCHEMA_URL;
import static org.eclipse.persistence.oxm.mappings.nullpolicy.XMLNullRepresentationType.XSI_NIL;
import static org.eclipse.persistence.tools.dbws.Util.SXF_QNAME_CURSOR;
import static org.eclipse.persistence.tools.dbws.Util.addSimpleXMLFormat;
import static org.eclipse.persistence.tools.dbws.Util.buildCustomQName;
import static org.eclipse.persistence.tools.dbws.Util.getXMLTypeFromJDBCType;
import static org.eclipse.persistence.tools.dbws.Util.qNameFromString;
import static org.eclipse.persistence.tools.dbws.Util.requiresSimpleXMLFormat;
import static org.eclipse.persistence.tools.oracleddl.metadata.ArgumentTypeDirection.IN;
import static org.eclipse.persistence.tools.oracleddl.metadata.ArgumentTypeDirection.INOUT;

import java.sql.Array;
import java.sql.Struct;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.namespace.QName;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.ClassConstants;
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
import org.eclipse.persistence.internal.xr.XRDynamicClassLoader;
import org.eclipse.persistence.internal.xr.sxf.SimpleXMLFormat;
import org.eclipse.persistence.internal.xr.sxf.SimpleXMLFormatProject;
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
import org.eclipse.persistence.oxm.schema.XMLSchemaReference;
import org.eclipse.persistence.oxm.schema.XMLSchemaURLReference;
import org.eclipse.persistence.platform.database.jdbc.JDBCTypes;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLCollection;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLStoredFunctionCall;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLStoredProcedureCall;
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
import org.eclipse.persistence.tools.oracleddl.metadata.DatabaseType;
import org.eclipse.persistence.tools.oracleddl.metadata.FieldType;
import org.eclipse.persistence.tools.oracleddl.metadata.FunctionType;
import org.eclipse.persistence.tools.oracleddl.metadata.ObjectType;
import org.eclipse.persistence.tools.oracleddl.metadata.PLSQLCollectionType;
import org.eclipse.persistence.tools.oracleddl.metadata.PLSQLPackageType;
import org.eclipse.persistence.tools.oracleddl.metadata.PLSQLRecordType;
import org.eclipse.persistence.tools.oracleddl.metadata.PLSQLType;
import org.eclipse.persistence.tools.oracleddl.metadata.ProcedureType;
import org.eclipse.persistence.tools.oracleddl.metadata.TableType;
import org.eclipse.persistence.tools.oracleddl.metadata.VArrayType;
import org.eclipse.persistence.tools.oracleddl.parser.ParseException;
import org.eclipse.persistence.tools.oracleddl.util.DatabaseTypeBuilder;

public class OracleHelper extends BaseDBWSBuilderHelper implements DBWSBuilderHelper {

    protected DatabaseTypeBuilder dtBuilder = new DatabaseTypeBuilder();
    protected boolean hasComplexProcedureArgs = false;

    public OracleHelper(DBWSBuilder dbwsBuilder) {
        super(dbwsBuilder);
    }

    /**
     * Indicates if this helper instance contains one or more
     * TableType instances in TableType List "dbTables".
     */
    public boolean hasTables() {
        if (dbTables.size() == 0) {
            return false;
        }
        return true;
    }

    public boolean hasComplexProcedureArgs() {
        return hasComplexProcedureArgs;
    }

    /**
     * Builds query operations for a given ProcedureOperationModel.
     */
    public void buildProcedureOperation(ProcedureOperationModel procedureOperationModel) {
        String name = procedureOperationModel.getName();
        List<ProcedureType> procs = new ArrayList<ProcedureType>();
        for (Map.Entry<ProcedureType, DbStoredProcedureNameAndModel> me : dbStoredProcedure2QueryName.entrySet()) {
            ProcedureType key = me.getKey();
            DbStoredProcedureNameAndModel value = me.getValue();
            if (value.name.equals(procedureOperationModel.getName())) {
                procs.add(key);
            }
        }
        // nested under a <table> operation
        if (procs.isEmpty()) {
            List<ProcedureType> additionalProcs = loadProcedures(procedureOperationModel);
            if (additionalProcs != null && !additionalProcs.isEmpty()) {
                procs.addAll(additionalProcs);
            }
            //TODO - add to dbStoredProcedure2QueryName map
        }

        // handle PL/SQL
        if (procedureOperationModel.isPLSQLProcedureOperation() && procedureOperationModel.hasComplexArguments()) {
            buildPLSQLProcedureOperation(procedureOperationModel, procs);
            return;
        }

        // handle non-PL/SQL
        for (ProcedureType storedProcedure : procs) {
            QueryOperation qo = new QueryOperation();
            qo.setName(getNameForQueryOperation(name, storedProcedure));

            String qualifiedProcName = getQualifiedProcedureName(procedureOperationModel, storedProcedure);
            dbwsBuilder.logMessage(FINEST, "Building QueryOperation for " + qualifiedProcName);

            QueryHandler qh;
            if (storedProcedure.isFunction()) {
                qh = new StoredFunctionQueryHandler();
            }
            else {
                qh = new StoredProcedureQueryHandler();
            }
            ((StoredProcedureQueryHandler)qh).setName(qualifiedProcName);

            // before assigning queryHandler, check for named query in OR project
            List<DatabaseQuery> queries = dbwsBuilder.getOrProject().getQueries();
            if (queries.size() > 0) {
                for (DatabaseQuery q : queries) {
                    if (q.getName().equals(qo.getName())) {
                        qh = new NamedQueryHandler();
                        ((NamedQueryHandler)qh).setName(qo.getName());
                    }
                }
            }

            qo.setQueryHandler(qh);
            String returnType = procedureOperationModel.getReturnType();
            boolean isCollection = procedureOperationModel.isCollection();
            boolean isSimpleXMLFormat = procedureOperationModel.isSimpleXMLFormat();

            Result result = null;
            if (storedProcedure.isFunction()) {
                result = buildResultForStoredFunction(storedProcedure, returnType);
            }
            else {
                // if user overrides returnType, assume they're right
                if (returnType != null) {
                    result = new Result();
                    result.setType(buildCustomQName(returnType, dbwsBuilder));
                }
                else {
                    if (isCollection) {
                        result = new CollectionResult();
                        if (isSimpleXMLFormat) {
                            result.setType(SXF_QNAME_CURSOR);
                        }
                    }
                    else {
                        result = new Result();
                        result.setType(SXF_QNAME);
                    }
                }
            }
            if (procedureOperationModel.getBinaryAttachment()) {
                Attachment attachment = new Attachment();
                attachment.setMimeType("application/octet-stream");
                result.setAttachment(attachment);
            }
            for (ArgumentType arg : storedProcedure.getArguments()) {
                String argName = arg.getArgumentName();
                if (argName != null) {
                    ProcedureArgument pa = null;
                    Parameter parm = null;
                    ArgumentTypeDirection direction = arg.getDirection();
                    QName xmlType = null;
                    // handle PL/SQL records and collections
                    if (arg.getDataType() instanceof PLSQLType) {
                        hasComplexProcedureArgs = true;
                        String typeString = storedProcedure.getCatalogName() + "_" + arg.getTypeName();
                        xmlType = buildCustomQName(typeString, dbwsBuilder);
                    } else if (arg.getDataType() instanceof VArrayType) {
                        // handle advanced JDBC types
                        hasComplexProcedureArgs = true;
                        String typeString = arg.getTypeName().toLowerCase().concat("Type");
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
                    if (direction == IN) {
                        parm = new Parameter();
                        parm.setName(argName);
                        parm.setType(xmlType);
                        pa = new ProcedureArgument();
                        pa.setName(argName);
                        pa.setParameterName(argName);
                        if (qh instanceof StoredProcedureQueryHandler) {
                            ((StoredProcedureQueryHandler)qh).getInArguments().add(pa);
                        }
                    }
                    else {
                        // the first OUT/INOUT arg determines singleResult vs. collectionResult
                        pa = new ProcedureOutputArgument();
                        ProcedureOutputArgument pao = (ProcedureOutputArgument)pa;
                        pao.setName(argName);
                        pao.setParameterName(argName);
                        boolean isCursor = arg.getTypeName().contains("CURSOR");
                        if (isCursor && returnType == null) { // if user overrides returnType, assume they're right
                            pao.setResultType(SXF_QNAME_CURSOR);
                            if (result == null) {
                                result = new CollectionResult();
                                result.setType(SXF_QNAME_CURSOR);
                            }
                        }
                        else {
                            // if user overrides returnType, assume they're right
                            // Hmm, multiple OUT's gonna be a problem - later!
                            if (returnType != null && !isSimpleXMLFormat) {
                                xmlType = qNameFromString("{" + dbwsBuilder.getTargetNamespace() + "}" +
                                    returnType, dbwsBuilder.getSchema());
                            }
                            if (isCursor) {
                                pao.setResultType(new QName("", "cursor of " + returnType));
                                Result newResult = new CollectionResult();
                                newResult.setType(result.getType());
                                result = newResult;
                            }
                            else {
                                pao.setResultType(xmlType);
                            }
                            if (result == null) {
                                if (isCollection) {
                                    result = new CollectionResult();
                                }
                                else {
                                    result = new Result();
                                }
                                result.setType(xmlType);
                            }
                        }
                        if (direction == INOUT) {
                            if (qh instanceof StoredProcedureQueryHandler) {
                                ((StoredProcedureQueryHandler)qh).getInOutArguments().add(pao);
                            }
                        }
                        else {
                            if (qh instanceof StoredProcedureQueryHandler) {
                                ((StoredProcedureQueryHandler)qh).getOutArguments().add(pao);
                            }
                        }
                        if (arg.getDataType() instanceof PLSQLType) {
                            pa.setComplexTypeName(storedProcedure.getCatalogName() + "_" + arg.getTypeName());
                        }
                    }
                    if (parm != null) {
                        qo.getParameters().add(parm);
                    }
                }
            }
            // the user may want simpleXMLFormat
            handleSimpleXMLFormat(isSimpleXMLFormat, result, procedureOperationModel);

            qo.setResult(result);
            dbwsBuilder.getXrServiceModel().getOperations().put(qo.getName(), qo);
        }
        finishProcedureOperation();
    }

    /**
     * Builds query operations for a given PLSQLProcedureOperationModel.
     */
    public void buildPLSQLProcedureOperation(ProcedureOperationModel procedureOperationModel, List<ProcedureType> procs) {
        String name = procedureOperationModel.getName();
        for (ProcedureType storedProcedure : procs) {
            QueryOperation qo = new QueryOperation();
            qo.setName(getNameForQueryOperation(name, storedProcedure));

            dbwsBuilder.logMessage(FINEST, "Building QueryOperation for " + getQualifiedProcedureName(procedureOperationModel, storedProcedure));

            String returnType = procedureOperationModel.getReturnType();
            boolean isCollection = procedureOperationModel.isCollection();
            boolean isSimpleXMLFormat = procedureOperationModel.isSimpleXMLFormat();

            Result result = null;
            if (storedProcedure.isFunction()) {
                result = buildResultForStoredFunction(storedProcedure, returnType);
            }
            if (procedureOperationModel.getBinaryAttachment()) {
                Attachment attachment = new Attachment();
                attachment.setMimeType("application/octet-stream");
                result.setAttachment(attachment);
            }
            for (ArgumentType arg : storedProcedure.getArguments()) {
                String argName = arg.getArgumentName();
                if (argName != null) {
                    ProcedureArgument pa = null;
                    ProcedureArgument paShadow = null; // for INOUT's
                    Parameter parm = null;
                    ArgumentTypeDirection direction = arg.getDirection();
                    QName xmlType = null;
                    // handle PL/SQL records and collections
                    if (arg.getDataType() instanceof PLSQLType) {
                        hasComplexProcedureArgs = true;
                        String typeString = storedProcedure.getCatalogName() + "_" + arg.getTypeName();
                        xmlType = buildCustomQName(typeString, dbwsBuilder);
                    } else if (arg.getDataType() instanceof VArrayType) {
                        // handle advanced JDBC types
                        hasComplexProcedureArgs = true;
                        String typeString = arg.getTypeName().toLowerCase().concat("Type");
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
                    if (direction == IN) {
                        parm = new Parameter();
                        parm.setName(argName);
                        parm.setType(xmlType);
                        pa = new ProcedureArgument();
                        pa.setName(argName);
                        pa.setParameterName(argName);
                    }
                    else {
                        // the first OUT/INOUT arg determines singleResult vs. collectionResult
                        pa = new ProcedureOutputArgument();
                        ProcedureOutputArgument pao = (ProcedureOutputArgument)pa;
                        pao.setName(argName);
                        pao.setParameterName(argName);
                        boolean isCursor = arg.getTypeName().contains("CURSOR");
                        if (isCursor && returnType == null) { // if user overrides returnType, assume they're right
                            pao.setResultType(SXF_QNAME_CURSOR);
                            if (result == null) {
                                result = new CollectionResult();
                                result.setType(SXF_QNAME_CURSOR);
                            }
                        }
                        else {
                            // if user overrides returnType, assume they're right
                            // Hmm, multiple OUT's gonna be a problem - later!
                            if (returnType != null && !isSimpleXMLFormat) {
                                xmlType = qNameFromString("{" + dbwsBuilder.getTargetNamespace() + "}" +
                                    returnType, dbwsBuilder.getSchema());
                            }
                            if (isCursor) {
                                pao.setResultType(new QName("", "cursor of " + returnType));
                                Result newResult = new CollectionResult();
                                newResult.setType(result.getType());
                                result = newResult;
                            }
                            else {
                                pao.setResultType(xmlType);
                            }
                            if (result == null) {
                                if (isCollection) {
                                    result = new CollectionResult();
                                }
                                else {
                                    result = new Result();
                                }
                                result.setType(xmlType);
                            }
                        }
                        if (direction == INOUT) {
                            parm = new Parameter();
                            parm.setName(argName);
                            // bug 303331 - set type in 'shadow' and 'regular' parameter
                            parm.setType(getXMLTypeFromJDBCType(Util.getJDBCTypeFromTypeName(arg.getTypeName())));
                            result.setType(parm.getType());
                            // use of INOUT precludes SimpleXMLFormat
                            isSimpleXMLFormat = false;
                            paShadow = new ProcedureArgument();
                            paShadow.setName(argName);
                            paShadow.setParameterName(argName);
                        }
                    }
                    if (arg.getDataType() instanceof PLSQLType) {
                        pa.setComplexTypeName(storedProcedure.getCatalogName() + "_" + arg.getTypeName());
                        if (paShadow != null) {
                            paShadow.setComplexTypeName(pa.getComplexTypeName());
                        }
                    }
                    if (parm != null) {
                        qo.getParameters().add(parm);
                    }
                }
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
    protected Result buildResultForStoredFunction(ProcedureType storedProcedure, String returnType) {
        Result result = null;
        FunctionType storedFunction = (FunctionType)storedProcedure;
        DatabaseType rarg = storedFunction.getReturnArgument();
        if (rarg.getTypeName().contains("CURSOR")) {
            result = new CollectionResult();
            result.setType(SXF_QNAME_CURSOR);
        }
        else {
            result = new Result();
            int rargJdbcType = Util.getJDBCTypeFromTypeName(rarg.getTypeName());
            switch (rargJdbcType) {
                case OTHER:
                    // if user overrides returnType, assume they're right
                    if (returnType == null) {
                        returnType = rarg.getTypeName();
                    }
                    String packageName = storedProcedure.getCatalogName();
                    String returnTypeName = (packageName != null && packageName.length() > 0) ?
                            packageName + "_" + returnType : returnType;
                    result.setType(buildCustomQName(returnTypeName, dbwsBuilder));
                    break;
                case STRUCT:
                case ARRAY:
                    // if user overrides returnType, assume they're right
                    if (returnType != null) {
                        result.setType(buildCustomQName(returnType, dbwsBuilder));
                    } else {
                        result.setType(ANY_QNAME);
                    }
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
     * Returns the name to be used for a QueryOperation based on a given
     * ProcedureType and ProcedureOperationModel name.
     *
     * The returned string will either be the provided 'modelName' if
     * non-null & non-empty string, or in the format:
     * 'overload_catalog_schema_procedureName'
     */
    protected String getNameForQueryOperation(String modelName, ProcedureType storedProcedure) {
        if (modelName != null && modelName.length() > 0) {
            return modelName;
        }
        StringBuilder sb = new StringBuilder();
        if (storedProcedure.getOverload() > 0) {
            sb.append(storedProcedure.getOverload());
            sb.append('_');
        }
        if (storedProcedure.getCatalogName() != null && storedProcedure.getCatalogName().length() > 0) {
            sb.append(storedProcedure.getCatalogName());
            sb.append('_');
        }
        if (storedProcedure.getSchema() != null && storedProcedure.getSchema().length() > 0) {
            sb.append(storedProcedure.getSchema());
            sb.append('_');
        }
        sb.append(storedProcedure.getProcedureName());
        return sb.toString();
    }

    /**
     * Returns the qualified stored procedure name based on a given ProcedureType
     * and ProcedureOperationModel.
     *
     * The returned string will be in the format: 'catalog.schema.procedureName'
     *
     */
    protected String getQualifiedProcedureName(ProcedureOperationModel procedureOperationModel, ProcedureType storedProcedure) {
        StringBuilder sb = new StringBuilder();
        if (storedProcedure.getCatalogName() != null && storedProcedure.getCatalogName().length() > 0) {
            sb.append(storedProcedure.getCatalogName());
            sb.append('.');
        }
        if (procedureOperationModel.getSchemaPattern() != null &&
            procedureOperationModel.getSchemaPattern().length() > 0 &&
            storedProcedure.getSchema() != null &&
            storedProcedure.getSchema().length() > 0) {
            sb.append(storedProcedure.getSchema());
            sb.append('.');
        }
        sb.append(storedProcedure.getProcedureName());
        return sb.toString();
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

    /**
     * Generates a List<TableType> based on a given originalCatalogPattern, schemaPattern,
     * and tableNamePattern.
     */
    protected List<TableType> loadTables(String originalCatalogPattern, String schemaPattern,
        String tableNamePattern) {
        try {
            return dtBuilder.buildTables(dbwsBuilder.getConnection(), schemaPattern,
                tableNamePattern);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Generates a List<ProcedureType> based on a given ProcedureOperationModel.
     */
    protected List<ProcedureType> loadProcedures(ProcedureOperationModel procedureModel) {
        List<ProcedureType> allProcsAndFuncs = new ArrayList<ProcedureType>();
        try {
            // handle PLSQL package stored procedures/functions
            if (procedureModel.isPLSQLProcedureOperation()) {
                List<PLSQLPackageType> foundPackages = dtBuilder.buildPackages(dbwsBuilder.getConnection(),
                    procedureModel.getSchemaPattern(), procedureModel.getCatalogPattern());
                if (foundPackages != null) {
                    for (PLSQLPackageType plsqlpkg : foundPackages) {
                        for (ProcedureType pType : plsqlpkg.getProcedures()) {
                            if (Util.sqlMatch(procedureModel.getProcedurePattern(), pType.getProcedureName())) {
                                allProcsAndFuncs.add(pType);
                            }
                        }
                    }
                }
            } else {
                // handle top-level stored procedures/functions
                List<ProcedureType> foundProcedures = dtBuilder.buildProcedures(dbwsBuilder.getConnection(),
                        procedureModel.getSchemaPattern(), procedureModel.getProcedurePattern());
                List<FunctionType> foundFunctions = dtBuilder.buildFunctions(dbwsBuilder.getConnection(),
                        procedureModel.getSchemaPattern(), procedureModel.getProcedurePattern());

                if (foundProcedures != null) {
                    allProcsAndFuncs.addAll(foundProcedures);
                }
                if (foundFunctions != null) {
                    allProcsAndFuncs.addAll(foundFunctions);
                }
            }
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return allProcsAndFuncs.isEmpty() ? null : allProcsAndFuncs;
    }

    /**
     * Create descriptors for complex arguments.  The newly created
     * descriptors will be added to the given OR/OX projects.
     */
    protected void addToOROXProjectsForComplexArgs(List<ArgumentType> arguments, Project orProject, Project oxProject, ProcedureOperationModel opModel) {
        for (ArgumentType arg : arguments) {
            DatabaseType dbType = arg.getDataType();
            String name;
            String targetTypeName;
            String alias;

            if (dbType instanceof PLSQLType) {
                name = opModel.getCatalogPattern() + "." + dbType.getTypeName();
                targetTypeName = opModel.getCatalogPattern() + "_" + dbType.getTypeName();
                alias = targetTypeName.toLowerCase();
                // handle PL/SQL record type
                if (dbType instanceof PLSQLRecordType) {
                    addToOXProjectForPLSQLRecordArg(dbType, oxProject, name, alias, targetTypeName, opModel.getCatalogPattern());
                    addToORProjectForPLSQLRecordArg(dbType, orProject, name, alias, targetTypeName, opModel.getCatalogPattern());
                }  // handle PL/SQL collection type
                else {
                    addToOXProjectForPLSQLTableArg(dbType, oxProject, name, alias, targetTypeName, opModel.getCatalogPattern());
                    addToORProjectForPLSQLTableArg(dbType, orProject, name, alias, targetTypeName, opModel.getCatalogPattern());
                }
            }
            else {
                name = dbType.getTypeName();
                targetTypeName = dbType.getTypeName();
                alias = targetTypeName.toLowerCase();
                // handle VArray type
                if (dbType instanceof VArrayType) {
                    addToOXProjectForVArrayArg(dbType, oxProject, name, alias, targetTypeName);
                    addToORProjectForVArrayArg(dbType, orProject, name, alias, targetTypeName);
                }  // handle Object type
                else if (dbType instanceof ObjectType) {
                    addToOXProjectForObjectTypeArg(dbType, oxProject, alias, targetTypeName);
                    addToORProjectForObjectTypeArg(dbType, orProject, alias);
                }
            }
        }
    }

    /**
     * Build descriptor and mappings for a PL/SQL record argument.  The newly
     * created descriptor will be added to the given OX project.
     */
    protected void addToOXProjectForPLSQLRecordArg(DatabaseType dbType, Project oxProject, String recordName, String recordAlias, String targetTypeName, String catalogPattern) {
        QName xmlType = buildCustomQName(targetTypeName, dbwsBuilder);
        String targetNamespace = xmlType.getNamespaceURI();

        XMLDescriptor xdesc = (XMLDescriptor) oxProject.getDescriptorForAlias(recordAlias);
        if (xdesc == null) {
            xdesc = new XMLDescriptor();
            xdesc.setAlias(recordAlias);
            xdesc.setJavaClassName(recordName.toLowerCase());
            xdesc.setDefaultRootElement(targetTypeName);
            xdesc.getQueryManager();
            XMLSchemaURLReference schemaReference = new XMLSchemaURLReference();
            schemaReference.setSchemaContext("/" + targetTypeName);
            schemaReference.setType(org.eclipse.persistence.platform.xml.XMLSchemaReference.COMPLEX_TYPE);
            xdesc.setSchemaReference(schemaReference);
            NamespaceResolver nr = new NamespaceResolver();
            nr.setDefaultNamespaceURI(targetNamespace);
            xdesc.setNamespaceResolver(nr);
            oxProject.addDescriptor(xdesc);
        }
        // handle fields
        PLSQLRecordType plsqlRecType = (PLSQLRecordType) dbType;
        for (FieldType fType : plsqlRecType.getFields()) {
            String fieldName = fType.getFieldName();
            String lFieldName = fieldName.toLowerCase();
            if (xdesc.getMappingForAttributeName(lFieldName) == null) {
                if (fType.isComposite()) {
                    // handle pl/sql record and pl/sql table fields
                    if (fType.getDataType() instanceof PLSQLRecordType) {
                        XMLCompositeObjectMapping fieldMapping = new XMLCompositeObjectMapping();
                        fieldMapping.setAttributeName(lFieldName);
                        fieldMapping.setXPath(lFieldName);
                        XMLField xField = (XMLField)fieldMapping.getField();
                        xField.setRequired(true);
                        fieldMapping.setReferenceClassName((catalogPattern + "." + fType.getDataType()).toLowerCase());
                        xdesc.addMapping(fieldMapping);
                    }
                    else if (fType.getDataType() instanceof PLSQLCollectionType) {
                        PLSQLCollectionType tableType = (PLSQLCollectionType) fType.getDataType();
                        if (tableType.getNestedType().isComposite()) {
                            XMLCompositeObjectMapping fieldMapping = new XMLCompositeObjectMapping();
                            fieldMapping.setAttributeName(lFieldName);
                            fieldMapping.setXPath(lFieldName);
                            XMLField xField = (XMLField)fieldMapping.getField();
                            xField.setRequired(true);
                            fieldMapping.setReferenceClassName((catalogPattern + "." + tableType.getTypeName()).toLowerCase() + COLLECTION_WRAPPER_SUFFIX);
                            xdesc.addMapping(fieldMapping);
                        } else {
                            XMLCompositeDirectCollectionMapping fieldMapping = new XMLCompositeDirectCollectionMapping();
                            XMLDescriptor refDesc = (XMLDescriptor) oxProject.getDescriptorForAlias((catalogPattern + "_" + tableType.getTypeName()).toLowerCase());
                            if (refDesc != null) {
                                XMLCompositeDirectCollectionMapping itemsMapping = (XMLCompositeDirectCollectionMapping)refDesc.getMappingForAttributeName(ITEMS_MAPPING_ATTRIBUTE_NAME);
                                Class<?> attributeElementClass = itemsMapping.getAttributeElementClass();
                                fieldMapping.setAttributeElementClass(attributeElementClass);
                            } else {
                                // TODO - can we default to String here?
                                fieldMapping.setAttributeElementClass(String.class);
                            }
                            fieldMapping.setAttributeName(lFieldName);
                            fieldMapping.setUsesSingleNode(true);
                            fieldMapping.setXPath(lFieldName + "/item/text()");
                            XMLField xField = (XMLField)fieldMapping.getField();
                            xField.setRequired(true);
                            fieldMapping.useCollectionClassName("java.util.ArrayList");
                            AbstractNullPolicy nullPolicy = fieldMapping.getNullPolicy();
                            nullPolicy.setNullRepresentedByEmptyNode(false);
                            nullPolicy.setMarshalNullRepresentation(XSI_NIL);
                            nullPolicy.setNullRepresentedByXsiNil(true);
                            fieldMapping.setNullPolicy(nullPolicy);
                            xdesc.getNamespaceResolver().put(SCHEMA_INSTANCE_PREFIX, SCHEMA_INSTANCE_URL);  // to support xsi:nil policy
                            xdesc.addMapping(fieldMapping);
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
            ordtDesc = new ObjectRelationalDataTypeDescriptor();
            ordtDesc.descriptorIsAggregate();
            ordtDesc.setAlias(recordAlias);
            ordtDesc.setJavaClassName(recordName.toLowerCase());
            ordtDesc.getQueryManager();
            ordtDesc.setStructureName(targetTypeName);
            orProject.addDescriptor(ordtDesc);
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
                    if (field.getName().equals(lFieldName)) {
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                ordtDesc.addFieldOrdering(lFieldName);
            }
            if (ordtDesc.getMappingForAttributeName(lFieldName) == null) {
                if (fType.isComposite()) {
                    if (fType.getDataType() instanceof PLSQLRecordType) {
                        StructureMapping structureMapping = new StructureMapping();
                        structureMapping.setAttributeName(lFieldName);
                        structureMapping.setFieldName(lFieldName);
                        structureMapping.setReferenceClassName(recordName.toLowerCase());
                        ordtDesc.addMapping(structureMapping);
                    }
                    else if (fType.getDataType() instanceof PLSQLCollectionType) {
                        PLSQLCollectionType tableType = (PLSQLCollectionType) fType.getDataType();
                        if (tableType.getNestedType().isComposite()) {
                            ObjectArrayMapping objectArrayMapping = new ObjectArrayMapping();
                            objectArrayMapping.setAttributeName(lFieldName);
                            objectArrayMapping.setFieldName(lFieldName);
                            objectArrayMapping.setStructureName(targetTypeName);
                            objectArrayMapping.setReferenceClassName((catalogPattern + "." + tableType.getTypeName()).toLowerCase() + COLLECTION_WRAPPER_SUFFIX);
                            ordtDesc.addMapping(objectArrayMapping);
                        } else {
                            ArrayMapping arrayMapping = new ArrayMapping();
                            arrayMapping.setAttributeName(lFieldName);
                            arrayMapping.setFieldName(lFieldName);
                            arrayMapping.setStructureName(targetTypeName);
                            ordtDesc.addMapping(arrayMapping);
                        }
                    }
                } else {
                    ordtDesc.addDirectMapping(lFieldName, lFieldName);
                }
            }
        }
    }

    /**
     * Build descriptor and mappings for a PL/SQL collection argument.  The newly
     * created descriptor will be added to the given OX project.
     */
    protected void addToOXProjectForPLSQLTableArg(DatabaseType dbType, Project oxProject, String tableName, String tableAlias, String targetTypeName, String catalogPattern) {
        QName xmlType = buildCustomQName(targetTypeName, dbwsBuilder);
        String targetNamespace = xmlType.getNamespaceURI();

        XMLDescriptor xdesc = (XMLDescriptor) oxProject.getDescriptorForAlias(tableAlias);
        if (xdesc == null) {
            xdesc = new XMLDescriptor();
            xdesc.setAlias(tableAlias);
            xdesc.setJavaClassName(tableName.toLowerCase() + COLLECTION_WRAPPER_SUFFIX);
            xdesc.getQueryManager();
            xdesc.setDefaultRootElement(targetTypeName);
            XMLSchemaURLReference schemaReference = new XMLSchemaURLReference("");
            schemaReference.setSchemaContext("/" + targetTypeName);
            schemaReference.setType(org.eclipse.persistence.platform.xml.XMLSchemaReference.COMPLEX_TYPE);
            xdesc.setSchemaReference(schemaReference);
            NamespaceResolver nr = new NamespaceResolver();
            nr.setDefaultNamespaceURI(targetNamespace);
            xdesc.setNamespaceResolver(nr);
            oxProject.addDescriptor(xdesc);
        }

        boolean itemsMappingFound = xdesc.getMappingForAttributeName(ITEMS_MAPPING_ATTRIBUTE_NAME) == null ? false : true;
        if (!itemsMappingFound) {
            DatabaseType nestedType = ((PLSQLCollectionType) dbType).getNestedType();
            if (nestedType instanceof PLSQLRecordType) {
                XMLCompositeCollectionMapping itemsMapping = new XMLCompositeCollectionMapping();
                itemsMapping.setAttributeName(ITEMS_MAPPING_ATTRIBUTE_NAME);
                itemsMapping.setXPath(ITEM_MAPPING_NAME);
                XMLField xField = (XMLField)itemsMapping.getField();
                xField.setRequired(true);
                itemsMapping.useCollectionClassName("java.util.ArrayList");
                itemsMapping.setReferenceClassName((catalogPattern + "." + ((PLSQLRecordType)nestedType).getTypeName()).toLowerCase());
                xdesc.addMapping(itemsMapping);
            } else {
                if (nestedType.isComposite()) {
                    XMLCompositeCollectionMapping itemsMapping = new XMLCompositeCollectionMapping();
                    itemsMapping.setAttributeName(ITEMS_MAPPING_ATTRIBUTE_NAME);
                    itemsMapping.setXPath(ITEM_MAPPING_NAME);
                    XMLField xField = (XMLField)itemsMapping.getField();
                    xField.setRequired(true);
                    itemsMapping.useCollectionClassName("java.util.ArrayList");
                    itemsMapping.setReferenceClassName(tableName.toLowerCase() + COLLECTION_WRAPPER_SUFFIX);
                    xdesc.addMapping(itemsMapping);
                }
                else {
                    XMLCompositeDirectCollectionMapping itemsMapping = new XMLCompositeDirectCollectionMapping();
                    itemsMapping.setAttributeElementClass(getAttributeClassForDatabaseType(nestedType));
                    itemsMapping.setAttributeName(ITEMS_MAPPING_ATTRIBUTE_NAME);
                    itemsMapping.setUsesSingleNode(true);
                    itemsMapping.setXPath(ITEM_MAPPING_NAME + "/text()");
                    XMLField xField = (XMLField)itemsMapping.getField();
                    xField.setRequired(true);
                    itemsMapping.useCollectionClassName("java.util.ArrayList");
                    AbstractNullPolicy nullPolicy = itemsMapping.getNullPolicy();
                    nullPolicy.setNullRepresentedByEmptyNode(false);
                    nullPolicy.setMarshalNullRepresentation(XSI_NIL);
                    nullPolicy.setNullRepresentedByXsiNil(true);
                    itemsMapping.setNullPolicy(nullPolicy);
                    xdesc.getNamespaceResolver().put(SCHEMA_INSTANCE_PREFIX, SCHEMA_INSTANCE_URL); // to support xsi:nil policy
                    xdesc.addMapping(itemsMapping);
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
            ordt = new ObjectRelationalDataTypeDescriptor();
            ordt.descriptorIsAggregate();
            ordt.setAlias(tableAlias);
            ordt.setJavaClassName(tableName.toLowerCase() + COLLECTION_WRAPPER_SUFFIX);
            ordt.getQueryManager();
            orProject.addDescriptor(ordt);
        }
        boolean itemsMappingFound = ordt.getMappingForAttributeName(ITEMS_MAPPING_ATTRIBUTE_NAME) == null ? false : true;
        if (!itemsMappingFound) {
            DatabaseType nestedType = ((PLSQLCollectionType) dbType).getNestedType();
            if (nestedType instanceof PLSQLRecordType) {
                ObjectArrayMapping itemsMapping = new ObjectArrayMapping();
                itemsMapping.setAttributeName(ITEMS_MAPPING_ATTRIBUTE_NAME);
                itemsMapping.setFieldName(ITEMS_MAPPING_FIELD_NAME);
                itemsMapping.setStructureName(targetTypeName);
                itemsMapping.setReferenceClassName((catalogPattern + "." + ((PLSQLRecordType)nestedType).getTypeName()).toLowerCase());
                ordt.addMapping(itemsMapping);
            } else {
                ArrayMapping itemsMapping = new ArrayMapping();
                itemsMapping.setAttributeName(ITEMS_MAPPING_ATTRIBUTE_NAME);
                itemsMapping.setFieldName(ITEMS_MAPPING_FIELD_NAME);
                itemsMapping.useCollectionClass(ArrayList.class);
                itemsMapping.setStructureName(targetTypeName);
                ordt.addMapping(itemsMapping);
            }
        }
    }

    /**
     * Build descriptor and mappings for a VArray argument.  The newly
     * created descriptor will be added to the given OX project.
     */
    protected void addToOXProjectForVArrayArg(DatabaseType dbType, Project oxProject, String arrayName, String arrayAlias, String targetTypeName) {
        QName xmlType = buildCustomQName(targetTypeName, dbwsBuilder);
        String targetNamespace = xmlType.getNamespaceURI();
        String userType = nct.generateSchemaAlias(arrayAlias);

        XMLDescriptor xdesc = (XMLDescriptor) oxProject.getDescriptorForAlias(arrayAlias);
        if (xdesc == null) {
            xdesc = new XMLDescriptor();
            xdesc.setAlias(arrayAlias);
            xdesc.setJavaClassName(arrayAlias + COLLECTION_WRAPPER_SUFFIX);
            xdesc.getQueryManager();
            XMLSchemaURLReference schemaReference = new XMLSchemaURLReference();
            schemaReference.setSchemaContext("/" + userType);
            schemaReference.setType(org.eclipse.persistence.platform.xml.XMLSchemaReference.COMPLEX_TYPE);
            xdesc.setSchemaReference(schemaReference);
            NamespaceResolver nr = new NamespaceResolver();
            nr.setDefaultNamespaceURI(targetNamespace);
            xdesc.setNamespaceResolver(nr);
            xdesc.setDefaultRootElement(userType);
            oxProject.addDescriptor(xdesc);
        }

        boolean itemsMappingFound = xdesc.getMappingForAttributeName(ITEMS_MAPPING_ATTRIBUTE_NAME) == null ? false : true;
        if (!itemsMappingFound) {
            DatabaseType nestedType = ((VArrayType) dbType).getEnclosedType();
            if (nestedType.isComposite()) {
                XMLCompositeCollectionMapping itemsMapping = new XMLCompositeCollectionMapping();
                itemsMapping.setAttributeName(ITEMS_MAPPING_ATTRIBUTE_NAME);
                itemsMapping.setXPath(ITEM_MAPPING_NAME);
                XMLField xField = (XMLField)itemsMapping.getField();
                xField.setRequired(true);
                itemsMapping.useCollectionClassName("java.util.ArrayList");
                itemsMapping.setReferenceClassName(arrayName.toLowerCase() + COLLECTION_WRAPPER_SUFFIX);
                xdesc.addMapping(itemsMapping);
            }
            else {
                XMLCompositeDirectCollectionMapping itemsMapping = new XMLCompositeDirectCollectionMapping();
                itemsMapping.setAttributeElementClass(getAttributeClassForDatabaseType(nestedType));
                itemsMapping.setAttributeName(ITEMS_MAPPING_ATTRIBUTE_NAME);
                itemsMapping.setUsesSingleNode(true);
                itemsMapping.setXPath(ITEM_MAPPING_NAME + "/text()");
                XMLField xField = (XMLField)itemsMapping.getField();
                xField.setRequired(true);
                itemsMapping.useCollectionClassName("java.util.ArrayList");
                AbstractNullPolicy nullPolicy = itemsMapping.getNullPolicy();
                nullPolicy.setNullRepresentedByEmptyNode(false);
                nullPolicy.setMarshalNullRepresentation(XSI_NIL);
                nullPolicy.setNullRepresentedByXsiNil(true);
                itemsMapping.setNullPolicy(nullPolicy);
                xdesc.getNamespaceResolver().put(SCHEMA_INSTANCE_PREFIX, SCHEMA_INSTANCE_URL); // to support xsi:nil policy
                xdesc.addMapping(itemsMapping);
            }
        }
    }

    /**
     * Build descriptor and mappings for a VArray argument.  The newly
     * created descriptor will be added to the given OX project.
     */
    protected void addToORProjectForVArrayArg(DatabaseType dbType, Project orProject, String arrayName, String arrayAlias, String targetTypeName) {
        ObjectRelationalDataTypeDescriptor ordt = (ObjectRelationalDataTypeDescriptor)orProject.getDescriptorForAlias(arrayAlias);
        if (ordt == null) {
            ordt = new ObjectRelationalDataTypeDescriptor();
            ordt.descriptorIsAggregate();
            ordt.setAlias(arrayAlias);
            // TODO - what about package name?
            ordt.setJavaClassName(arrayAlias + COLLECTION_WRAPPER_SUFFIX);
            ordt.getQueryManager();
            orProject.addDescriptor(ordt);
        }

        boolean itemsMappingFound = ordt.getMappingForAttributeName(ITEMS_MAPPING_ATTRIBUTE_NAME) == null ? false : true;
        if (!itemsMappingFound) {
            DatabaseType nestedType = ((VArrayType) dbType).getEnclosedType();
            if (nestedType.isComposite()) {
                ObjectArrayMapping itemsMapping = new ObjectArrayMapping();
                itemsMapping.setAttributeName(ITEMS_MAPPING_ATTRIBUTE_NAME);
                itemsMapping.setFieldName(ITEMS_MAPPING_FIELD_NAME);
                itemsMapping.setStructureName(targetTypeName);
                itemsMapping.setReferenceClassName((((PLSQLRecordType)nestedType).getTypeName()).toLowerCase());
                ordt.addMapping(itemsMapping);
            } else {
                ArrayMapping itemsMapping = new ArrayMapping();
                itemsMapping.setAttributeName(ITEMS_MAPPING_ATTRIBUTE_NAME);
                itemsMapping.setFieldName(ITEMS_MAPPING_FIELD_NAME);
                itemsMapping.useCollectionClass(ArrayList.class);
                itemsMapping.setStructureName(targetTypeName);
                ordt.addMapping(itemsMapping);
            }
        }
    }

    /**
     * Build descriptor and mappings for an Object type argument.  The
     * newly created descriptor will be added to the given OX project.
     */
    protected void addToOXProjectForObjectTypeArg(DatabaseType dbType, Project oxProject, String objectAlias, String targetTypeName) {
        QName xmlType = buildCustomQName(targetTypeName, dbwsBuilder);
        String targetNamespace = xmlType.getNamespaceURI();
        String userType = nct.generateSchemaAlias(objectAlias);

        XMLDescriptor xdesc = (XMLDescriptor) oxProject.getDescriptorForAlias(objectAlias);
        if (xdesc == null) {
            xdesc = buildNewXMLDescriptor(objectAlias, userType, targetNamespace);
            oxProject.addDescriptor(xdesc);
        }

        ObjectType oType = (ObjectType) dbType;
        for (FieldType field : oType.getFields()) {
            String fieldName = field.getFieldName();
            String lFieldName = fieldName.toLowerCase();
            if (xdesc.getMappingForAttributeName(lFieldName) == null) {
                if (field.isComposite()) {
                    // handle ObjectType field
                    if (field.getDataType() instanceof ObjectType) {
                        String alias = field.getDataType().getTypeName().toLowerCase();
                        XMLDescriptor xdesc2 = (XMLDescriptor) oxProject.getDescriptorForAlias(alias);
                        if (xdesc2 == null) {
                            String targetTypeName2 = field.getDataType().getTypeName();
                            QName xmlType2 = buildCustomQName(targetTypeName2, dbwsBuilder);
                            String targetNamespace2 = xmlType2.getNamespaceURI();
                            String userType2 = nct.generateSchemaAlias(alias);
                            xdesc2 = buildNewXMLDescriptor(alias, userType2, targetNamespace2);
                            oxProject.addDescriptor(xdesc2);
                            addToOXProjectForObjectTypeArg(field.getDataType(), oxProject, alias, targetTypeName2);
                        }
                        XMLCompositeObjectMapping compMapping = new XMLCompositeObjectMapping();
                        compMapping.setAttributeName(lFieldName);
                        compMapping.setReferenceClassName(xdesc2.getJavaClassName());
                        compMapping.setXPath(lFieldName);
                        XMLField xField = (XMLField)compMapping.getField();
                        xField.setRequired(true);
                        xdesc.addMapping(compMapping);
                    }
                    // TODO - handle VArray field (composite collection mapping)
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
    protected void addToORProjectForObjectTypeArg(DatabaseType dbType, Project orProject, String objectAlias) {
        ObjectRelationalDataTypeDescriptor ordt = (ObjectRelationalDataTypeDescriptor)orProject.getDescriptorForAlias(objectAlias);
        if (ordt == null) {
            ordt = buildNewObjectRelationalDataTypeDescriptor(objectAlias);
            orProject.addDescriptor(ordt);
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
                    if (field.getName().equals(lFieldName)) {
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                ordt.addFieldOrdering(lFieldName);
            }

            if (ordt.getMappingForAttributeName(lFieldName) == null) {
                if (fType.isComposite()) {
                    // handle ObjectType field
                    if (fType.getDataType() instanceof ObjectType) {
                        String targetTypeName2 = fType.getDataType().getTypeName();
                        String alias = targetTypeName2.toLowerCase();
                        ObjectRelationalDataTypeDescriptor ordt2 = (ObjectRelationalDataTypeDescriptor)orProject.getDescriptorForAlias(alias);
                        if (ordt2 == null) {
                            ordt2 = buildNewObjectRelationalDataTypeDescriptor(alias);
                            orProject.addDescriptor(ordt2);
                            addToORProjectForObjectTypeArg(fType.getDataType(), orProject, alias);
                        }
                        StructureMapping structMapping = new StructureMapping();
                        structMapping.setFieldName(lFieldName);
                        structMapping.setAttributeName(lFieldName);
                        structMapping.setReferenceClassName(ordt2.getJavaClassName());
                        ordt.addMapping(structMapping);
                    }
                    // TODO - handle VArray field (object array mapping)
                } else {
                    // direct mapping
                    DirectToFieldMapping dfm = new DirectToFieldMapping();
                    dfm.setFieldName(lFieldName);
                    dfm.setAttributeName(lFieldName);
                    ordt.addMapping(dfm);
                }
            }
        }
    }

    /**
     * Build a Query for the given ProcedureType instance and add
     * it to the given OR project's list of queries.
     */
    protected void buildQueryForProcedureType(ProcedureType procType, Project orProject, Project oxProject, ProcedureOperationModel opModel, String queryName) {
        if (opModel.isPLSQLProcedureOperation()) {
            buildQueryForPLSQLProcedureType(procType, orProject, oxProject, opModel, queryName);
        } else {
            buildQueryForAdvancedJDBCProcedureType(procType, orProject, oxProject, opModel, queryName);
        }
    }
    /**
     * Build a Query for the given ProcedureType instance and add
     * it to the given OR project's list of queries.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected void buildQueryForPLSQLProcedureType(ProcedureType procType, Project orProject, Project oxProject, ProcedureOperationModel opModel, String queryName) {
        PLSQLStoredProcedureCall call;
        if (procType.isFunction()) {
            org.eclipse.persistence.internal.helper.DatabaseType returnDatabaseType = opModel.getDbStoredFunctionReturnType();
            if (returnDatabaseType == null) {
                returnDatabaseType = buildDatabaseTypeFromMetadataType(((FunctionType)procType).getReturnArgument(), procType.getCatalogName());
            }
            call = new PLSQLStoredFunctionCall(returnDatabaseType);
        } else {
            call = new PLSQLStoredProcedureCall();
        }

        String cat = procType.getCatalogName();
        String catalogPrefix = (cat == null || cat.length() == 0) ? "" : cat + ".";
        call.setProcedureName(catalogPrefix + procType.getProcedureName());
        String returnType = opModel.getReturnType();
        boolean hasResponse = returnType != null;
        String typ = null;
        ClassDescriptor xdesc = null;
        if (hasResponse) {
            int idx = returnType.indexOf(":");
            if (idx == -1) {
                idx = returnType.indexOf("}");
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
        DatabaseQuery dq = null;
        if (hasResponse && opModel.isCollection()) {
            dq = new DataReadQuery();
        } else {
            dq = new ValueReadQuery();
        }
        dq.bindAllParameters();
        dq.setName(queryName);
        dq.setCall(call);

        for (ArgumentType arg : procType.getArguments()) {
            DatabaseType argType = arg.getDataType();
            org.eclipse.persistence.internal.helper.DatabaseType databaseType = buildDatabaseTypeFromMetadataType(argType, cat);

            ArgumentTypeDirection direction = arg.getDirection();
            if (direction == ArgumentTypeDirection.OUT) {
                call.addNamedOutputArgument(arg.getArgumentName(), databaseType);
            } else if (direction == IN) {
                call.addNamedArgument(arg.getArgumentName(), databaseType);
            } else {
                call.addNamedInOutputArgument(arg.getArgumentName(), databaseType);
            }
            if (direction == IN || direction == INOUT) {
                if (xdesc != null) {
                    dq.addArgumentByTypeName(arg.getArgumentName(), xdesc.getJavaClassName());
                } else {
                    if (databaseType instanceof PLSQLCollection) {
                        dq.addArgument(arg.getArgumentName(), Array.class);
                    } else if (databaseType instanceof PLSQLrecord) {
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
     * Build a Query for the given ProcedureType instance and add
     * it to the given OR project's list of queries.
     */
    @SuppressWarnings("rawtypes")
    protected void buildQueryForAdvancedJDBCProcedureType(ProcedureType procType, Project orProject, Project oxProject, ProcedureOperationModel opModel, String queryName) {
        StoredProcedureCall call;
        if (procType.isFunction()) {
            ArgumentType returnArg = ((FunctionType)procType).getReturnArgument();
            String javaTypeName = returnArg.getTypeName();
            ClassDescriptor desc = oxProject.getDescriptorForAlias(javaTypeName.toLowerCase());
            if (desc != null) {
                javaTypeName = desc.getJavaClassName();
            }

            if (returnArg.isComposite()) {
                DatabaseType dataType = returnArg.getDataType();
                if (dataType instanceof VArrayType) {
                    call = new StoredFunctionCall(Types.ARRAY, returnArg.getTypeName(), javaTypeName, buildFieldForNestedType(dataType));
                } else {
                    // assumes ObjectType
                    call = new StoredFunctionCall(Types.STRUCT, returnArg.getTypeName(), javaTypeName);
                }
            } else {
                call = new StoredFunctionCall();
                ((StoredFunctionCall) call).setResult(null, ClassConstants.OBJECT);
            }
        } else {
            call = new StoredProcedureCall();
        }
        String cat = procType.getCatalogName();
        String catalogPrefix = (cat == null || cat.length() == 0) ? "" : cat + ".";
        call.setProcedureName(catalogPrefix + procType.getProcedureName());

        String returnType = opModel.getReturnType();
        boolean hasResponse = returnType != null;
        DatabaseQuery dq = null;
        if (hasResponse && opModel.isCollection()) {
            dq = new DataReadQuery();
        } else {
            dq = new ValueReadQuery();
        }
        dq.bindAllParameters();
        dq.setName(queryName);
        dq.setCall(call);

        for (ArgumentType arg : procType.getArguments()) {
            DatabaseType argType = arg.getDataType();
            ArgumentTypeDirection direction = arg.getDirection();
            String javaTypeName = argType.getTypeName();
            ClassDescriptor desc = oxProject.getDescriptorForAlias(javaTypeName.toLowerCase());
            if (desc != null) {
                javaTypeName = desc.getJavaClassName();
            }

            if (direction == ArgumentTypeDirection.IN) {
                dq.addArgument(arg.getArgumentName());
                if (argType instanceof VArrayType) {
                    call.addNamedArgument(arg.getArgumentName(), arg.getArgumentName(), Types.ARRAY, argType.getTypeName(), javaTypeName);
                } else if (argType instanceof ObjectType) {
                    call.addNamedArgument(arg.getArgumentName(), arg.getArgumentName(), Types.STRUCT, argType.getTypeName(), javaTypeName);
                }
                else {
                    call.addNamedArgument(arg.getArgumentName(), arg.getArgumentName(), Util.getJDBCTypeFromTypeName(argType.getTypeName()));
                }
            }
            else if (direction == ArgumentTypeDirection.OUT) {
                if (argType.isComposite()) {
                    if (argType instanceof VArrayType) {
                        // the following call will try and load the collection wrapper class via XRDynamicClassLoader
                        try {
                            call.addNamedOutputArgument(arg.getArgumentName(), arg.getArgumentName(), Types.ARRAY, argType.getTypeName(), new XRDynamicClassLoader(this.getClass().getClassLoader()).loadClass(javaTypeName), buildFieldForNestedType(argType));
                        } catch (ClassNotFoundException e) {
                            // TODO - will we ever get here?
                            // at this point we can try updating the field's type name manually
                            call.addNamedOutputArgument(arg.getArgumentName(), arg.getArgumentName(), Types.ARRAY, argType.getTypeName(), (Class)null, buildFieldForNestedType(argType));
                            for (Object param : call.getParameters()) {
                                DatabaseField dbf = (DatabaseField) param;
                                if (dbf.getName().equals(arg.getArgumentName())) {
                                    dbf.setTypeName(javaTypeName);
                                }
                            }
                        }
                    } else {
                        // assumes ObjectType
                        try {
                            // the following call will try and load the object type class via XRDynamicClassLoader
                            call.addNamedOutputArgument(arg.getArgumentName(), arg.getArgumentName(), Types.STRUCT, argType.getTypeName(), new XRDynamicClassLoader(this.getClass().getClassLoader()).loadClass(javaTypeName));
                        } catch (ClassNotFoundException e) {
                            // TODO - will we ever get here?
                            // at this point we can try updating the field's type name manually
                            call.addNamedOutputArgument(arg.getArgumentName(), arg.getArgumentName(), Types.STRUCT, argType.getTypeName(), (Class)null);
                            for (Object param : call.getParameters()) {
                                DatabaseField dbf = (DatabaseField) param;
                                if (dbf.getName().equals(arg.getArgumentName())) {
                                    dbf.setTypeName(javaTypeName);
                                }
                            }
                        }
                    }
                } else {
                    call.addNamedOutputArgument(arg.getArgumentName(), arg.getArgumentName(), Util.getJDBCTypeFromTypeName(argType.getTypeName()));
                }
            }
            else if (direction == ArgumentTypeDirection.INOUT) {
                dq.addArgument(arg.getArgumentName());
                call.addNamedInOutputArgument(arg.getArgumentName());
            }
        }

        orProject.getQueries().add(dq);
    }

    /**
     * Build an OR database field for a given type's nested type.
     * This method assumes that owningType is an VArrayType.
     */
    protected ObjectRelationalDatabaseField buildFieldForNestedType(DatabaseType owningType) {
        ObjectRelationalDatabaseField nestedField = new ObjectRelationalDatabaseField("");
        DatabaseType nestedType = ((VArrayType) owningType).getEnclosedType();
        nestedField.setSqlTypeName(Util.getJDBCTypeNameFromType(Types.ARRAY));
        nestedField.setSqlType(Types.ARRAY);
        nestedField.setTypeName(nestedType.getTypeName());
        return nestedField;
    }

    /**
     * Create an XMLDirectMapping for a given FieldType instance, and add the
     * newly created mapping to the given XMLDescriptor.
     */
    protected void addDirectMappingForFieldType(XMLDescriptor xdesc, String attributeName, FieldType fType) {
        XMLDirectMapping fieldMapping = new XMLDirectMapping();
        fieldMapping.setAttributeName(attributeName);
        XMLField xField = new XMLField(attributeName + "/text()");
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
     * Build an XMLDescriptor based on a given descriptor alias,
     * schema alias, and target namespace.
     */
    protected XMLDescriptor buildNewXMLDescriptor(String objectAlias, String userType, String targetNamespace) {
        XMLDescriptor xdesc = new XMLDescriptor();
        xdesc.setAlias(objectAlias);
        xdesc.setJavaClassName(objectAlias);
        xdesc.getQueryManager();
        XMLSchemaURLReference schemaReference = new XMLSchemaURLReference();
        schemaReference.setSchemaContext("/" + userType);
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
     * descriptor alias.
     */
    protected ObjectRelationalDataTypeDescriptor buildNewObjectRelationalDataTypeDescriptor(String alias) {
        ObjectRelationalDataTypeDescriptor ordt = new ObjectRelationalDataTypeDescriptor();
        ordt.setStructureName(alias.toUpperCase());
        ordt.descriptorIsAggregate();
        ordt.setAlias(alias);
        ordt.setJavaClassName(alias);
        ordt.getQueryManager();
        return ordt;
    }
}