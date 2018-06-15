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
//     David McCann - 2.5.0 - Sept.18, 2012 - Initial Implementation
package org.eclipse.persistence.tools.dbws;

import static org.eclipse.persistence.internal.databaseaccess.DatasourceCall.IN;
import static org.eclipse.persistence.internal.databaseaccess.DatasourceCall.INOUT;
import static org.eclipse.persistence.internal.databaseaccess.DatasourceCall.OUT;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.EL_ACCESS_VIRTUAL;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_PARAMETER_IN;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_PARAMETER_INOUT;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_PARAMETER_OUT;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_PARAMETER_REF_CURSOR;
import static org.eclipse.persistence.internal.xr.XRDynamicClassLoader.COLLECTION_WRAPPER_SUFFIX;
import static org.eclipse.persistence.platform.database.oracle.plsql.OraclePLSQLTypes.PLSQLBoolean;
import static org.eclipse.persistence.platform.database.oracle.plsql.OraclePLSQLTypes.XMLType;
import static org.eclipse.persistence.tools.dbws.Util.BOOLEAN_STR;
import static org.eclipse.persistence.tools.dbws.Util.DOT;
import static org.eclipse.persistence.tools.dbws.Util.PERCENT;
import static org.eclipse.persistence.tools.dbws.Util.ROWTYPE_STR;
import static org.eclipse.persistence.tools.dbws.Util.UNDERSCORE;
import static org.eclipse.persistence.tools.dbws.Util.VARCHAR2_STR;
import static org.eclipse.persistence.tools.dbws.Util.VARCHAR_STR;
import static org.eclipse.persistence.tools.dbws.Util.XMLTYPE_STR;
import static org.eclipse.persistence.tools.dbws.Util._TYPE_STR;
import static org.eclipse.persistence.tools.dbws.Util.getGeneratedAlias;
import static org.eclipse.persistence.tools.dbws.Util.getJDBCTypeFromTypeName;
import static org.eclipse.persistence.tools.dbws.Util.getOraclePLSQLTypeForName;
import static org.eclipse.persistence.tools.dbws.Util.isArgPLSQLScalar;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EmbeddableAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EntityAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.XMLAttributes;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.BasicAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.EmbeddedAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.IdAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.MappingAccessor;
import org.eclipse.persistence.internal.jpa.metadata.columns.ColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.ComplexTypeMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.NamedNativeQueryMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.NamedPLSQLStoredFunctionQueryMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.NamedPLSQLStoredProcedureQueryMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.NamedStoredFunctionQueryMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.NamedStoredProcedureQueryMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.OracleArrayTypeMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.OracleComplexTypeMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.OracleObjectTypeMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.PLSQLComplexTypeMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.PLSQLParameterMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.PLSQLRecordMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.PLSQLTableMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.QueryHintMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.StoredProcedureParameterMetadata;
import org.eclipse.persistence.internal.jpa.metadata.structures.ArrayAccessor;
import org.eclipse.persistence.internal.jpa.metadata.structures.StructMetadata;
import org.eclipse.persistence.internal.jpa.metadata.structures.StructureAccessor;
import org.eclipse.persistence.internal.jpa.metadata.tables.TableMetadata;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.mappings.AggregateMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.structures.ArrayMapping;
import org.eclipse.persistence.mappings.structures.ObjectArrayMapping;
import org.eclipse.persistence.mappings.structures.ObjectRelationalDataTypeDescriptor;
import org.eclipse.persistence.mappings.structures.ObjectRelationalDatabaseField;
import org.eclipse.persistence.mappings.structures.StructureMapping;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLStoredFunctionCall;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLStoredProcedureCall;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLargument;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.StoredFunctionCall;
import org.eclipse.persistence.queries.StoredProcedureCall;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.tools.oracleddl.metadata.CompositeDatabaseType;
import org.eclipse.persistence.tools.oracleddl.metadata.FieldType;
import org.eclipse.persistence.tools.oracleddl.metadata.ObjectTableType;
import org.eclipse.persistence.tools.oracleddl.metadata.ObjectType;
import org.eclipse.persistence.tools.oracleddl.metadata.PLSQLCollectionType;
import org.eclipse.persistence.tools.oracleddl.metadata.PLSQLRecordType;
import org.eclipse.persistence.tools.oracleddl.metadata.PLSQLType;
import org.eclipse.persistence.tools.oracleddl.metadata.VArrayType;

/**
 * This class is responsible for generating an XMLEntityMappings instance based
 * on a given OR Project's Queries and Descriptors.
 *
 */
public class XmlEntityMappingsGenerator {
    static final String IN_STR = "IN";
    static final String INOUT_STR = "IN_OUT";
    static final String OUT_STR = "OUT";
    static final String CURSOR_STR = "OUT_CURSOR";

    static final String ARRAYLIST_STR = "java.util.ArrayList";
    static final String STRING_STR = "java.lang.String";

    /**
     * Generate an XMLEntityMappings instance based on a given OR Project's Queries and Descriptors.
     *
     * @param orProject the ORM Project instance containing Queries and Descriptors to be used to generate an XMLEntityMappings
     * @param complexTypes list of composite database types used to generate metadata for advanced Oracle and PL/SQL types
     * @param crudOperations map of maps keyed on table name - the second map are operation name to SQL string entries
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static XMLEntityMappings generateXmlEntityMappings(Project orProject, List<CompositeDatabaseType> complexTypes, Map<String, Map<String, String>> crudOperations) {
        List<ClassDescriptor> descriptors = orProject.getOrderedDescriptors();
        List<DatabaseQuery> queries = orProject.getQueries();

        XMLEntityMappings xmlEntityMappings = new XMLEntityMappings();
        xmlEntityMappings.setEmbeddables(new ArrayList<EmbeddableAccessor>());
        xmlEntityMappings.setEntities(new ArrayList<EntityAccessor>());

        xmlEntityMappings.setPLSQLRecords(new ArrayList<PLSQLRecordMetadata>());
        xmlEntityMappings.setPLSQLTables(new ArrayList<PLSQLTableMetadata>());
        xmlEntityMappings.setOracleObjectTypes(new ArrayList<OracleObjectTypeMetadata>());
        xmlEntityMappings.setOracleArrayTypes(new ArrayList<OracleArrayTypeMetadata>());

        // process PL/SQL records and collections, and Oracle advanced JDBC types
        List<PLSQLRecordMetadata> plsqlRecords = null;
        List<PLSQLTableMetadata>  plsqlTables  = null;
        List<OracleObjectTypeMetadata> objectTypes = null;
        List<OracleArrayTypeMetadata> arrayTypes = null;

        // generate metadata for each of the composite types
        List<ComplexTypeMetadata> complexTypeMetadata = processCompositeTypes(complexTypes, orProject);
        for (ComplexTypeMetadata cTypeMetadata : complexTypeMetadata) {
            if (cTypeMetadata.isOracleComplexTypeMetadata()) {
                OracleComplexTypeMetadata octMetadata = (OracleComplexTypeMetadata) cTypeMetadata;
                if (octMetadata.isOracleArrayTypeMetadata()) {
                    if (arrayTypes == null) {
                        arrayTypes = new ArrayList<OracleArrayTypeMetadata>();
                    }
                    arrayTypes.add((OracleArrayTypeMetadata) octMetadata);
                } else {
                    // assumes OracleObjectTypeMetadata
                    if (objectTypes == null) {
                        objectTypes = new ArrayList<OracleObjectTypeMetadata>();
                    }
                    objectTypes.add((OracleObjectTypeMetadata) octMetadata);
                }
            } else {
                // assumes PL/SQL complex type metadata
                PLSQLComplexTypeMetadata plsqlctMetadata = (PLSQLComplexTypeMetadata) cTypeMetadata;
                if (plsqlctMetadata.isPLSQLRecordMetadata()) {
                    if (plsqlRecords == null) {
                        plsqlRecords = new ArrayList<PLSQLRecordMetadata>();
                    }
                    plsqlRecords.add((PLSQLRecordMetadata) plsqlctMetadata);
                } else {
                    // assumes PLSQLTableMetadata
                    if (plsqlTables == null) {
                        plsqlTables  = new ArrayList<PLSQLTableMetadata>();
                    }
                    plsqlTables.add((PLSQLTableMetadata) plsqlctMetadata);
                }
            }
        }
        // add generated metadata
        xmlEntityMappings.setPLSQLRecords(plsqlRecords);
        xmlEntityMappings.setPLSQLTables(plsqlTables);
        xmlEntityMappings.setOracleObjectTypes(objectTypes);
        xmlEntityMappings.setOracleArrayTypes(arrayTypes);

        // process stored procedures/functions
        List<NamedPLSQLStoredProcedureQueryMetadata> plsqlStoredProcs = null;
        List<NamedPLSQLStoredFunctionQueryMetadata> plsqlStoredFuncs = null;
        List<NamedStoredProcedureQueryMetadata> storedProcs = null;
        List<NamedStoredFunctionQueryMetadata> storedFuncs = null;
        List<NamedNativeQueryMetadata> namedNativeQueries = null;

        // process database queries set on the descriptor
        for (DatabaseQuery query : queries) {
            if (query.getCall().isStoredFunctionCall()) {
                if (query.getCall() instanceof PLSQLStoredFunctionCall) {
                    PLSQLStoredFunctionCall call = (PLSQLStoredFunctionCall)query.getCall();
                    NamedPLSQLStoredFunctionQueryMetadata metadata = new NamedPLSQLStoredFunctionQueryMetadata();

                    metadata.setName(query.getName());
                    metadata.setProcedureName(call.getProcedureName());

                    List<PLSQLParameterMetadata> params = new ArrayList<PLSQLParameterMetadata>();
                    if (plsqlStoredFuncs == null) {
                        plsqlStoredFuncs = new ArrayList<NamedPLSQLStoredFunctionQueryMetadata>();
                    }

                    PLSQLargument arg;
                    PLSQLParameterMetadata param;
                    List<PLSQLargument> types = call.getArguments();
                    for (int i=0; i < types.size(); i++) {
                        arg = types.get(i);
                        param = new PLSQLParameterMetadata();
                        param.setName(arg.name);
                        String dbType = arg.databaseType.getTypeName();

                        if (arg.databaseType == XMLType) {
                            dbType = XMLType.name();
                        } else if (arg.databaseType == PLSQLBoolean) {
                            dbType = PLSQLBoolean.name();
                        } else {
                            if (!(getJDBCTypeFromTypeName(dbType) == Types.OTHER)) {
                                dbType = dbType.concat(_TYPE_STR);
                            }
                        }
                        param.setDatabaseType(dbType);
                        if (i == 0) {
                            // first arg is the return arg
                            metadata.setReturnParameter(param);
                            if (arg.cursorOutput) {
                                param.setDirection(CURSOR_STR);
                            }
                        } else {
                            param.setDirection(getDirectionAsString(arg.direction));
                            params.add(param);
                        }
                    }
                    if (params.size() > 0) {
                        metadata.setParameters(params);
                    }
                    plsqlStoredFuncs.add(metadata);
                } else {
                    StoredFunctionCall call = (StoredFunctionCall)query.getCall();
                    NamedStoredFunctionQueryMetadata metadata = new NamedStoredFunctionQueryMetadata();

                    metadata.setName(query.getName());
                    metadata.setProcedureName(call.getProcedureName());

                    List<StoredProcedureParameterMetadata> params = new ArrayList<StoredProcedureParameterMetadata>();
                    if (storedFuncs == null) {
                        storedFuncs = new ArrayList<NamedStoredFunctionQueryMetadata>();
                    }

                    DatabaseField arg;
                    StoredProcedureParameterMetadata param;
                    List<DatabaseField> paramFields = call.getParameters();
                    List<Integer> types = call.getParameterTypes();
                    for (int i=0; i < paramFields.size(); i++) {
                        arg = paramFields.get(i);
                        param = new StoredProcedureParameterMetadata();
                        param.setTypeName(arg.getTypeName());

                        if (arg.getSqlType() != DatabaseField.NULL_SQL_TYPE) {
                            param.setJdbcType(arg.getSqlType());
                        }

                        if (arg.isObjectRelationalDatabaseField()) {
                            param.setJdbcTypeName(((ObjectRelationalDatabaseField)arg).getSqlTypeName());
                        }

                        if (i == 0) {
                            // first arg is the return arg
                            metadata.setReturnParameter(param);
                            // handle CURSOR types - want name/value pairs returned
                            if (types.get(i) == 8) {
                                addQueryHint(metadata);
                            }
                        } else {
                            param.setName(arg.getName());
                            param.setMode(getParameterModeAsString(types.get(i)));
                            params.add(param);
                        }
                    }
                    if (params.size() > 0) {
                        metadata.setParameters(params);
                    }
                    storedFuncs.add(metadata);
                }
            } else if (query.getCall().isStoredProcedureCall()) {
                if (query.getCall() instanceof PLSQLStoredProcedureCall) {
                    PLSQLStoredProcedureCall call = (PLSQLStoredProcedureCall)query.getCall();
                    if (plsqlStoredProcs == null) {
                        plsqlStoredProcs = new ArrayList<NamedPLSQLStoredProcedureQueryMetadata>();
                    }

                    NamedPLSQLStoredProcedureQueryMetadata metadata = new NamedPLSQLStoredProcedureQueryMetadata();
                    metadata.setName(query.getName());
                    metadata.setProcedureName(call.getProcedureName());

                    PLSQLParameterMetadata param;
                    List<PLSQLParameterMetadata> params = new ArrayList<PLSQLParameterMetadata>();

                    List<PLSQLargument> types = call.getArguments();
                    for (PLSQLargument arg : types) {
                        param = new PLSQLParameterMetadata();
                        param.setName(arg.name);
                        String dbType = processTypeName(arg.databaseType.getTypeName());

                        if (arg.cursorOutput) {
                            param.setDirection(CURSOR_STR);
                        } else {
                            param.setDirection(getDirectionAsString(arg.direction));
                        }

                        if (arg.databaseType == XMLType) {
                            param.setDatabaseType(XMLType.name());
                        } else if (arg.databaseType == PLSQLBoolean) {
                            param.setDatabaseType(PLSQLBoolean.name());
                        } else {
                            param.setDatabaseType(dbType);
                        }
                        params.add(param);
                    }
                    if (params.size() > 0) {
                        metadata.setParameters(params);
                    }
                    plsqlStoredProcs.add(metadata);
                } else {
                    StoredProcedureCall call = (StoredProcedureCall) query.getCall();
                    NamedStoredProcedureQueryMetadata metadata = new NamedStoredProcedureQueryMetadata();
                    metadata.setName(query.getName());
                    metadata.setProcedureName(call.getProcedureName());
                    metadata.setReturnsResultSet(false);

                    List<StoredProcedureParameterMetadata> params = new ArrayList<StoredProcedureParameterMetadata>();
                    DatabaseField arg;
                    StoredProcedureParameterMetadata param;
                    List paramFields = call.getParameters();
                    List<Integer> types = call.getParameterTypes();
                    for (int i = 0; i < paramFields.size(); i++) {
                        if (types.get(i) == DatabaseCall.INOUT) {
                            // for INOUT we get Object[IN, OUT]
                            arg = (DatabaseField) ((Object[]) paramFields.get(i))[1];
                        } else {
                            arg = (DatabaseField) paramFields.get(i);
                        }

                        param = new StoredProcedureParameterMetadata();
                        param.setName(arg.getName());
                        param.setTypeName(arg.getTypeName());
                        if (arg.getSqlType() != DatabaseField.NULL_SQL_TYPE) {
                            param.setJdbcType(arg.getSqlType());
                        }
                        if (arg.isObjectRelationalDatabaseField()) {
                            param.setJdbcTypeName(((ObjectRelationalDatabaseField) arg).getSqlTypeName());
                        }

                        param.setMode(getParameterModeAsString(types.get(i)));

                        // handle CURSOR types - want name/value pairs returned
                        if (types.get(i) == 8) {
                            addQueryHint(metadata);
                        }

                        params.add(param);
                    }
                    if (params.size() > 0) {
                        metadata.setParameters(params);
                    }
                    if (storedProcs == null) {
                        storedProcs = new ArrayList<NamedStoredProcedureQueryMetadata>();
                    }
                    storedProcs.add(metadata);
                }
            } else {
                // named native query
                NamedNativeQueryMetadata namedQuery = new NamedNativeQueryMetadata();
                namedQuery.setName(query.getName());
                namedQuery.setQuery(query.getSQLString());
                namedQuery.setResultClassName(query.getReferenceClassName());
                if (namedNativeQueries == null) {
                    namedNativeQueries = new ArrayList<NamedNativeQueryMetadata>();
                }
                namedNativeQueries.add(namedQuery);
            }
        }
        // add generated metadata
        if (plsqlStoredProcs != null) {
            xmlEntityMappings.setNamedPLSQLStoredProcedureQueries(plsqlStoredProcs);
        }
        if (plsqlStoredFuncs != null) {
            xmlEntityMappings.setNamedPLSQLStoredFunctionQueries(plsqlStoredFuncs);
        }
        if (storedProcs != null) {
            xmlEntityMappings.setNamedStoredProcedureQueries(storedProcs);
        }
        if (storedFuncs != null) {
            xmlEntityMappings.setNamedStoredFunctionQueries(storedFuncs);
        }
        if (namedNativeQueries != null) {
            xmlEntityMappings.setNamedNativeQueries(namedNativeQueries);
        }

        // generate a ClassAccessor for each Descriptor, keeping track of Embeddables
        List<String> embeddables = new ArrayList<String>();
        Map<String, ClassAccessor> accessors = new HashMap<String, ClassAccessor>();
        for (ClassDescriptor cdesc : descriptors) {
            boolean embeddable = false;
            ClassAccessor classAccessor;
            if (cdesc.isAggregateDescriptor()) {
                embeddable = true;
                classAccessor = new EmbeddableAccessor();
                embeddables.add(cdesc.getJavaClassName());
            } else {
                classAccessor = new EntityAccessor();
            }
            classAccessor.setClassName(cdesc.getJavaClassName());
            classAccessor.setAccess(EL_ACCESS_VIRTUAL);

            // may need add STRUCT metadata to the classAccessor to ensure correct field ordering
            if (cdesc.isObjectRelationalDataTypeDescriptor()) {
                ObjectRelationalDataTypeDescriptor odesc = (ObjectRelationalDataTypeDescriptor) cdesc;
                if (odesc.getOrderedFields().size() > 0) {
                    StructMetadata struct = new StructMetadata();
                    struct.setName(odesc.getStructureName());
                    struct.setFields(odesc.getOrderedFields());
                    classAccessor.setStruct(struct);
                }
            }

            if (!embeddable && cdesc.getTableName() != null) {
                TableMetadata table = new TableMetadata();
                table.setName(cdesc.getTableName());
                ((EntityAccessor)classAccessor).setTable(table);
            }

            if (!embeddable) {
                List<NamedNativeQueryMetadata> namedNatQueries = new ArrayList<NamedNativeQueryMetadata>();
                NamedNativeQueryMetadata namedQuery;
                DatabaseQuery dbQuery;
                // process findAll and findByPk queries
                for (Iterator<DatabaseQuery> queryIt = cdesc.getQueryManager().getAllQueries().iterator(); queryIt.hasNext();) {
                    dbQuery = queryIt.next();
                    namedQuery = new NamedNativeQueryMetadata();
                    namedQuery.setName(dbQuery.getName());
                    namedQuery.setQuery(dbQuery.getSQLString());
                    namedQuery.setResultClassName(dbQuery.getReferenceClassName());
                    namedNatQueries.add(namedQuery);
                }
                // now create/update/delete operations
                Map<String, String> crudOps = crudOperations.get(cdesc.getTableName());
                if (!crudOps.isEmpty()) {
                    for (String opName : crudOps.keySet()) {
                        String crudSql = crudOps.get(opName);
                        NamedNativeQueryMetadata crudQuery = new NamedNativeQueryMetadata();
                        crudQuery.setName(opName);
                        crudQuery.setQuery(crudSql);
                        namedNatQueries.add(crudQuery);
                    }
                }
                if (namedNatQueries.size() > 0) {
                    ((EntityAccessor)classAccessor).setNamedNativeQueries(namedNatQueries);
                }
            }

            classAccessor.setAttributes(new XMLAttributes());
            classAccessor.getAttributes().setIds(new ArrayList<IdAccessor>());
            classAccessor.getAttributes().setBasics(new ArrayList<BasicAccessor>());
            classAccessor.getAttributes().setArrays(new ArrayList<ArrayAccessor>());
            classAccessor.getAttributes().setStructures(new ArrayList<StructureAccessor>());
            classAccessor.getAttributes().setEmbeddeds(new ArrayList<EmbeddedAccessor>());

            if (embeddable) {
                xmlEntityMappings.getEmbeddables().add((EmbeddableAccessor) classAccessor);
            } else {
                xmlEntityMappings.getEntities().add((EntityAccessor) classAccessor);
            }
            accessors.put(cdesc.getJavaClassName(), classAccessor);
        }
        // now the we know what the embeddables are, we can process mappings
        for (ClassDescriptor cdesc : descriptors) {
            ClassAccessor classAccessor = accessors.get(cdesc.getJavaClassName());

            MappingAccessor mapAccessor;
            // generate a MappingAccessor for each mapping
            for (DatabaseMapping dbMapping : cdesc.getMappings()) {
                mapAccessor = generateMappingAccessor(dbMapping, embeddables);
                if (mapAccessor == null) {
                    continue;
                }
                if (mapAccessor.isId()) {
                    classAccessor.getAttributes().getIds().add((IdAccessor) mapAccessor);
                } else if (mapAccessor.isBasic()) {
                    classAccessor.getAttributes().getBasics().add((BasicAccessor) mapAccessor);
                } else if (mapAccessor instanceof ArrayAccessor) {
                    classAccessor.getAttributes().getArrays().add((ArrayAccessor) mapAccessor);
                } else if (mapAccessor instanceof StructureAccessor) {
                    classAccessor.getAttributes().getStructures().add((StructureAccessor) mapAccessor);
                } else { // assumes EmbeddedAccessor
                    classAccessor.getAttributes().getEmbeddeds().add((EmbeddedAccessor) mapAccessor);
                }
            }
        }
        return xmlEntityMappings;
    }

    /**
     * Process a given DatabaseMapping and return a MappingAccessor.
     *
     * Expected mappings are:
     * <ul>
     * <li>org.eclipse.persistence.mappings.DirectToFieldMapping
     * <li>org.eclipse.persistence.mappings.structures.ArrayMapping
     * <li>org.eclipse.persistence.mappings.structures.ObjectArrayMapping
     * <li>org.eclipse.persistence.mappings.structures.StructureMapping
     * </ul>
     */
    protected static MappingAccessor generateMappingAccessor(DatabaseMapping mapping, List<String> embeddables) {
        MappingAccessor mapAccessor = null;

        if (mapping.isDirectToFieldMapping()) {
            mapAccessor = processDirectMapping((DirectToFieldMapping) mapping);
        } else if (mapping instanceof ObjectArrayMapping) {
            mapAccessor = processObjectArrayMapping((ObjectArrayMapping) mapping);
        } else if (mapping instanceof ArrayMapping) {
            mapAccessor = processArrayMapping((ArrayMapping) mapping);
        } else if (mapping.isStructureMapping()) {
            mapAccessor = processStructureMapping((StructureMapping) mapping);
        } else if (embeddables.contains(((AggregateMapping) mapping).getReferenceClassName())) {
            mapAccessor = processEmbeddedMapping((AggregateMapping) mapping);
        }
        return mapAccessor;
    }

    /**
     * Generate a MappingAccessor for a given DirectToFieldMapping.
     *
     */
    protected static BasicAccessor processDirectMapping(DirectToFieldMapping mapping) {
        BasicAccessor directMapping;
        if (mapping.getDescriptor().getPrimaryKeyFields().contains(mapping.getField())) {
            directMapping = new IdAccessor();
        } else {
            directMapping = new BasicAccessor();
        }
        directMapping.setName(mapping.getAttributeName());
        directMapping.setAttributeType(mapping.getAttributeClassificationName());

        ColumnMetadata column = new ColumnMetadata();
        column.setName(mapping.getField().getName());
        directMapping.setColumn(column);

        return directMapping;
    }

    /**
     * Generate a MappingAccessor for a given ArrayMapping.
     *
     */
    protected static ArrayAccessor processArrayMapping(ArrayMapping mapping) {
        ArrayAccessor arrayAccessor = new ArrayAccessor();
        arrayAccessor.setName(mapping.getAttributeName());
        arrayAccessor.setDatabaseType(mapping.getStructureName());
        arrayAccessor.setAttributeType(ARRAYLIST_STR);
        arrayAccessor.setTargetClassName(mapping.getStructureName());

        ColumnMetadata column = new ColumnMetadata();
        column.setName(mapping.getField().getName());
        arrayAccessor.setColumn(column);

        return arrayAccessor;
    }

    /**
     * Generate a MappingAccessor for a given ObjectArrayMapping.
     *
     */
    protected static ArrayAccessor processObjectArrayMapping(ObjectArrayMapping mapping) {
        ArrayAccessor arrayAccessor = new ArrayAccessor();
        arrayAccessor.setName(mapping.getAttributeName());
        arrayAccessor.setDatabaseType(mapping.getStructureName());
        arrayAccessor.setTargetClassName(mapping.getReferenceClassName());
        arrayAccessor.setAttributeType(ARRAYLIST_STR);

        ColumnMetadata column = new ColumnMetadata();
        column.setName(mapping.getField().getName());
        arrayAccessor.setColumn(column);

        return arrayAccessor;
    }

    /**
     * Generate a MappingAccessor for a given StructureMapping.
     *
     */
    protected static StructureAccessor processStructureMapping(StructureMapping mapping) {
        StructureAccessor structAccessor = new StructureAccessor();
        structAccessor.setName(mapping.getAttributeName());
        structAccessor.setTargetClassName(mapping.getReferenceClassName());
        structAccessor.setAttributeType(mapping.getReferenceClassName());

        ColumnMetadata column = new ColumnMetadata();
        column.setName(mapping.getField().getName());
        structAccessor.setColumn(column);

        return structAccessor;
    }

    /**
     * Generate an EmbeddedAccessor for a given AggregateMapping.
     *
     */
    protected static EmbeddedAccessor processEmbeddedMapping(AggregateMapping mapping) {
        EmbeddedAccessor embeddedAccessor = new EmbeddedAccessor();
        embeddedAccessor.setName(mapping.getAttributeName());
        embeddedAccessor.setAttributeType(mapping.getReferenceClassName());

        return embeddedAccessor;
    }

    /**
     * Return a parameter direction as a String based on a given in value.
     *
     * Expected 'direction' value is one of:
     * <ul>
     * <li>org.eclipse.persistence.internal.databaseaccess.DatasourceCall.IN
     * <li>org.eclipse.persistence.internal.databaseaccess.DatasourceCall.INOUT
     * <li>org.eclipse.persistence.internal.databaseaccess.DatasourceCall.OUT
     * <li>org.eclipse.persistence.internal.databaseaccess.DatasourceCall.OUT_CURSOR
     * </ul>
     *
     */
    public static String getDirectionAsString(int direction) {
        if (direction == IN) {
            return IN_STR;
        }
        if (direction == OUT) {
            return OUT_STR;
        }
        if (direction == INOUT) {
            return INOUT_STR;
        }
        return CURSOR_STR;
    }

    /**
     * Return a parameter mode as a String based on a given in value.
     *
     * Expected 'direction' value is one of:
     * <ul>
     * <li>org.eclipse.persistence.internal.databaseaccess.DatasourceCall.IN
     * <li>org.eclipse.persistence.internal.databaseaccess.DatasourceCall.INOUT
     * <li>org.eclipse.persistence.internal.databaseaccess.DatasourceCall.OUT
     * <li>org.eclipse.persistence.internal.databaseaccess.DatasourceCall.OUT_CURSOR
     * </ul>
     *
     * Will return one of:
     * <ul>
     * <li>org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_PARAMETER_IN
     * <li>org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_PARAMETER_INOUT
     * <li>org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_PARAMETER_OUT
     * <li>org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_PARAMETER_REF_CURSOR
     * </ul>
     */
    public static String getParameterModeAsString(int direction) {
        if (direction == IN) {
            return JPA_PARAMETER_IN;
        }
        if (direction == OUT) {
            return JPA_PARAMETER_OUT;
        }
        if (direction == INOUT) {
            return JPA_PARAMETER_INOUT;
        }
        return JPA_PARAMETER_REF_CURSOR;
    }

    /**
     * Alter the given type name if required.
     */
    protected static String processTypeName(String typeName) {
        if (!(getJDBCTypeFromTypeName(typeName) == Types.OTHER)) {
            // OR Metadata doesn't handle VARCHAR2
            if (typeName.equals(VARCHAR2_STR)) {
                typeName = VARCHAR_STR;
            }
            // for BOOLEAN we want to wrap the type in a PLSQLrecord (in ORMetadata.getDatabaseTypeEnum) to
            // force the appropriate conversion method in PLSQLStoredProcedureCall (declare block, etc)
            if (!typeName.equals(BOOLEAN_STR) && !typeName.equals(XMLTYPE_STR)) {
                typeName = typeName.concat(_TYPE_STR);
            }
        } else {
            String oPLSQLTypeName = getOraclePLSQLTypeForName(typeName);
            if (oPLSQLTypeName != null) {
                typeName = oPLSQLTypeName;
            }
        }
        return typeName;
    }

    /**
     * Returns a list of ComplexTypeMetadata instances generated based on a list of
     * CompositeDatabaseTypes.  The given non-empty list should contain one or
     * more of the following types:
     * <ul>
     * <li>PLSQLCollectionType
     * <li>PLSQLRecordType
     * <li>ObjectTableType
     * <li>ObjectType
     * <li>VArrayType
     * </ul>
     */
    protected static List<ComplexTypeMetadata> processCompositeTypes(List<CompositeDatabaseType> complexTypes, Project orProject) {
        List<String> processedTypeNames = new ArrayList<String>();
        List<ComplexTypeMetadata> processedTypes = new ArrayList<ComplexTypeMetadata>();
        // process composite types
        for (CompositeDatabaseType cdbType : complexTypes) {
            ComplexTypeMetadata complexTypeMetadata = processDatabaseType(cdbType, orProject);
            if (complexTypeMetadata != null) {
                processedTypeNames.add(complexTypeMetadata.getName());
                processedTypes.add(complexTypeMetadata);
            }
        }
        // now handle processing composite enclosed types
        for (CompositeDatabaseType cdbType : complexTypes) {
            if (cdbType.getEnclosedType() != null && cdbType.getEnclosedType().isComposite()) {
                CompositeDatabaseType enclosedType = (CompositeDatabaseType) cdbType.getEnclosedType();
                ComplexTypeMetadata complexTypeMetadata = processDatabaseType(enclosedType, orProject, processedTypeNames);
                if (complexTypeMetadata != null) {
                    processedTypeNames.add(complexTypeMetadata.getName());
                    processedTypes.add(complexTypeMetadata);

                    if (enclosedType.isObjectType()) {
                        for (FieldType ft : ((ObjectType) enclosedType).getFields()) {
                            if (ft.isComposite()) {
                                complexTypeMetadata = processDatabaseType(ft, orProject, processedTypeNames);
                                if (complexTypeMetadata != null) {
                                    processedTypeNames.add(complexTypeMetadata.getName());
                                    processedTypes.add(complexTypeMetadata);
                                }
                            }
                        }
                    } else if (enclosedType.isVArrayType()) {
                        VArrayType vType = (VArrayType) enclosedType;
                        if (vType.getEnclosedType().isComposite()) {
                            complexTypeMetadata = processDatabaseType((CompositeDatabaseType) vType.getEnclosedType(), orProject, processedTypeNames);
                            if (complexTypeMetadata != null) {
                                processedTypeNames.add(complexTypeMetadata.getName());
                                processedTypes.add(complexTypeMetadata);
                            }
                        }
                    }
                }
            }

        }
        return processedTypes;
    }

    /**
     * Process the given CompositeDatabaseType and return a ComplexTypeMetadata
     * instance. The given type is expected to be one of:
     * <ul>
     * <li>PLSQLCollectionType
     * <li>PLSQLRecordType
     * <li>ObjectTableType
     * <li>ObjectType
     * <li>VArrayType
     * </ul>
     * If the given type is not one of these, null is returned.
     */
    protected static ComplexTypeMetadata processDatabaseType(CompositeDatabaseType cdbType, Project orProject) {
        return processDatabaseType(cdbType, orProject, new ArrayList<String>());
    }

    /**
     * Process the given CompositeDatabaseType and return a ComplexTypeMetadata
     * instance. The given type is expected to be one of:
     * <ul>
     * <li>PLSQLCollectionType
     * <li>PLSQLRecordType
     * <li>ObjectTableType
     * <li>ObjectType
     * <li>VArrayType
     * </ul>
     * If the given type is not one of these, null is returned.  Previously processed types
     * are tracked to avoid unnecessary work.
     */
    protected static ComplexTypeMetadata processDatabaseType(CompositeDatabaseType cdbType, Project orProject, List<String> processedTypeNames) {
        if (!processedTypeNames.contains(cdbType.getTypeName())) {
            if (cdbType.isPLSQLCollectionType()) {
                return processPLSQLCollectionType((PLSQLCollectionType) cdbType);
            }
            if (cdbType.isPLSQLRecordType()) {
                return processPLSQLRecordType((PLSQLRecordType) cdbType);
            }
            if (cdbType.isObjectTableType()) {
                return processObjectTableType((ObjectTableType) cdbType, orProject);
            }
            if (cdbType.isObjectType()) {
                return processObjectType((ObjectType) cdbType, orProject);
            }
            if (cdbType.isVArrayType()) {
                return processVArrayType((VArrayType) cdbType, orProject);
            }
        }
        return null;
    }

    /**
     * Process the given PLSQLCollectionType and return a PLSQLTableMetadata instance.
     *
     */
    protected static ComplexTypeMetadata processPLSQLCollectionType(PLSQLCollectionType plsqlCollectionType) {
        String typeName = plsqlCollectionType.getParentType().getPackageName() + DOT + plsqlCollectionType.getTypeName();
        String compatiableName = plsqlCollectionType.getParentType().getPackageName() + UNDERSCORE + plsqlCollectionType.getTypeName();

        PLSQLTableMetadata plsqlTable = new PLSQLTableMetadata();
        plsqlTable.setName(typeName);
        plsqlTable.setCompatibleType(compatiableName);
        // handle Nested Table (i.e. non-Varray)
        plsqlTable.setNestedTable(!plsqlCollectionType.isIndexed());
        String dbType = plsqlCollectionType.getEnclosedType().getTypeName();
        if (!(getJDBCTypeFromTypeName(dbType) == Types.OTHER)) {
            // need special handling for nested PL/SQL scalar types
            if (isArgPLSQLScalar(dbType)) {
                plsqlTable.setNestedType(getOraclePLSQLTypeForName(dbType));
            } else {
                // OR Metadata doesn't handle VARCHAR2
                if (dbType.equals(VARCHAR2_STR)) {
                    dbType = VARCHAR_STR;
                }
                plsqlTable.setNestedType(dbType + _TYPE_STR);
            }
        } else {
            // may need to prepend package name
            String pkg = null;
            if (plsqlCollectionType.getEnclosedType() != null && plsqlCollectionType.getEnclosedType().isPLSQLType()) {
                PLSQLType pType = (PLSQLType) plsqlCollectionType.getEnclosedType();
                pkg = pType.getParentType().getPackageName();
            }
            plsqlTable.setNestedType(pkg == null ? dbType : pkg + DOT + dbType);
        }

        plsqlTable.setJavaType(typeName.toLowerCase() + COLLECTION_WRAPPER_SUFFIX);
        return plsqlTable;
    }

    /**
     * Process the given PLSQLRecordType and return a PLSQLRecordMetadata instance.
     *
     */
    protected static ComplexTypeMetadata processPLSQLRecordType(PLSQLRecordType plsqlRecordType) {
        // for %ROWTYPE we create a 'place holder' PL/SQL Record - in this case there is no package name
        String packageName = plsqlRecordType.getParentType().getPackageName();
        String typeName = packageName != null ? plsqlRecordType.getParentType().getPackageName() + DOT + plsqlRecordType.getTypeName() : plsqlRecordType.getTypeName();
        String compatiableName = packageName != null ? plsqlRecordType.getParentType().getPackageName() + UNDERSCORE + plsqlRecordType.getTypeName() : plsqlRecordType.getTypeName();
        if (compatiableName.contains(PERCENT)) {
            compatiableName = compatiableName.replace(PERCENT, UNDERSCORE);
        }

        PLSQLRecordMetadata plsqlRecordMetadata = new PLSQLRecordMetadata();
        plsqlRecordMetadata.setName(typeName);
        plsqlRecordMetadata.setCompatibleType(compatiableName);
        if (typeName.endsWith(ROWTYPE_STR)) {
            plsqlRecordMetadata.setJavaType(compatiableName.toLowerCase());
        } else {
            plsqlRecordMetadata.setJavaType(typeName.toLowerCase());
        }

        List<PLSQLParameterMetadata> fields = new ArrayList<PLSQLParameterMetadata>();
        PLSQLParameterMetadata field;
        for (FieldType fld : plsqlRecordType.getFields()) {
            field = new PLSQLParameterMetadata();
            field.setName(fld.getFieldName());
            String pkg = null;
            if (fld.getEnclosedType() != null && fld.getEnclosedType().isPLSQLType()) {
                PLSQLType pType = (PLSQLType) fld.getEnclosedType();
                pkg = pType.getParentType().getPackageName();
            }
            String dbType = fld.getTypeName();
            if (!(getJDBCTypeFromTypeName(dbType) == Types.OTHER)) {
                // OR Metadata doesn't handle VARCHAR2
                if (dbType.equals(VARCHAR2_STR)) {
                    dbType = VARCHAR_STR;
                }
                if (!dbType.equals(BOOLEAN_STR)) {
                    dbType = dbType.concat(_TYPE_STR);
                }
            }
            dbType = pkg == null ? dbType : pkg + DOT + dbType;
            field.setDatabaseType(dbType);
            fields.add(field);
        }
        plsqlRecordMetadata.setFields(fields);
        return plsqlRecordMetadata;
    }

    /**
     * Process the given ObjectTableType and return an OracleArrayTypeMetadata instance.
     *
     */
    protected static ComplexTypeMetadata processObjectTableType(ObjectTableType oTableType, Project orProject) {
        ClassDescriptor cDesc = orProject.getDescriptorForAlias(getGeneratedAlias(oTableType.getTypeName()));

        OracleArrayTypeMetadata oatMetadata = new OracleArrayTypeMetadata();
        oatMetadata.setName(oTableType.getTypeName());
        oatMetadata.setJavaType(cDesc.getJavaClassName());

        oatMetadata.setNestedType(oTableType.getEnclosedType().getTypeName());

        return oatMetadata;
    }

    /**
     * Process the given ObjectType and return an OracleObjectTypeMetadata instance.
     *
     */
    protected static ComplexTypeMetadata processObjectType(ObjectType oType, Project orProject) {
        ClassDescriptor cDesc = orProject.getDescriptorForAlias(getGeneratedAlias(oType.getTypeName()));

        OracleObjectTypeMetadata ootMetadata = new OracleObjectTypeMetadata();
        ootMetadata.setName(oType.getTypeName());
        ootMetadata.setJavaType(cDesc.getJavaClassName());

        List<PLSQLParameterMetadata> fields = new ArrayList<PLSQLParameterMetadata>();
        for (FieldType ft : oType.getFields()) {
            PLSQLParameterMetadata fieldMetadata = new PLSQLParameterMetadata();
            fieldMetadata.setName(ft.getFieldName());
            fieldMetadata.setDatabaseType(processTypeName(ft.getTypeName()));
            fields.add(fieldMetadata);
        }
        ootMetadata.setFields(fields);
        return ootMetadata;
    }

    /**
     * Process the given VArrayType and return an OracleArrayTypeMetadata instance.
     *
     */
    protected static ComplexTypeMetadata processVArrayType(VArrayType vType, Project orProject) {
        ClassDescriptor cDesc = orProject.getDescriptorForAlias(getGeneratedAlias(vType.getTypeName()));

        OracleArrayTypeMetadata oatMetadata = new OracleArrayTypeMetadata();
        oatMetadata.setName(vType.getTypeName());
        oatMetadata.setJavaType(cDesc.getJavaClassName());
        oatMetadata.setNestedType(processTypeName(vType.getEnclosedType().getTypeName()));

        return oatMetadata;
    }

    /**
     * Adds a ReturnNameValuePairsHint to the given query metadata instance.
     */
    protected static void addQueryHint(NamedNativeQueryMetadata metadata) {
        List<QueryHintMetadata> hints = metadata.getHints();
        if (hints == null) {
            hints = new ArrayList<QueryHintMetadata>();
        }
        QueryHintMetadata hint = new QueryHintMetadata();
        hint.setName(QueryHints.RETURN_NAME_VALUE_PAIRS);
        hint.setValue(HintValues.TRUE);
        hints.add(hint);
        metadata.setHints(hints);
    }
}
