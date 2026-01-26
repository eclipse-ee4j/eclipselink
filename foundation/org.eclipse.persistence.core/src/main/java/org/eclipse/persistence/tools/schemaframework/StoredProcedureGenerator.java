/*
 * Copyright (c) 1998, 2026 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.schemaframework;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.databaseaccess.DatasourceCall;
import org.eclipse.persistence.internal.expressions.ParameterExpression;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.queries.CallQueryMechanism;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.DeleteAllQuery;
import org.eclipse.persistence.queries.DeleteObjectQuery;
import org.eclipse.persistence.queries.InsertObjectQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.queries.SQLCall;
import org.eclipse.persistence.queries.UpdateObjectQuery;
import org.eclipse.persistence.sequencing.TableSequence;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.sessions.Project;

/**
 * <b>Purpose</b>: To generate StoredProcedures from EclipseLink Projects <p>
 * <b>Description</b>: This Class was designed to read in a project and produce StoredProcedures.
 *  It then modifies the descriptors files of the project to use these StoredProcedures.
 * NOTE: reads are not supported in Oracle.
 *    <p>
 * <b>Responsibilities</b>:<ul>
 * <li>
 * </ul>
 * @since TopLink 2.1
 * @author Gordon Yorke
 */
public class StoredProcedureGenerator {
    public SchemaManager schemaManager;
    /** This hashtable is used to store the storedProcedure referenced by the class name. */
    private Map<ClassDescriptor, List<StoredProcedureDefinition>> storedProcedures;
    /** This hashtable is used to store the storedProcedure referenced by the mapping name. */
    private Map<ClassDescriptor, Map<String, Map<String, StoredProcedureDefinition>>> mappingStoredProcedures;
    private Map<Integer, Class<?>> intToTypeConverterHash;
    private Writer writer;
    private String prefix;
    private static final String DEFAULT_PREFIX = "";
    private Map<String, StoredProcedureDefinition> sequenceProcedures;
    private static final int MAX_NAME_SIZE = 30;

    public StoredProcedureGenerator(SchemaManager schemaMngr) {
        super();
        this.schemaManager = schemaMngr;
        this.sequenceProcedures = new Hashtable<>();
        this.storedProcedures = new Hashtable<>();
        this.mappingStoredProcedures = new Hashtable<>();
        this.buildIntToTypeConverterHash();
        this.prefix = DEFAULT_PREFIX;
        this.verify();
    }

    /**
     * INTERNAL: Build all conversions based on JDBC return values.
     */
    protected void buildIntToTypeConverterHash() {
        this.intToTypeConverterHash = new HashMap<>();
        this.intToTypeConverterHash.put(8, Double.class);
        this.intToTypeConverterHash.put(-7, Boolean.class);
        this.intToTypeConverterHash.put(-3, Byte[].class);
        this.intToTypeConverterHash.put(-6, Short.class);
        this.intToTypeConverterHash.put(5, Short.class);
        this.intToTypeConverterHash.put(4, Integer.class);
        this.intToTypeConverterHash.put(2, java.math.BigDecimal.class);
        this.intToTypeConverterHash.put(6, Float.class);
        this.intToTypeConverterHash.put(1, Character.class);
        this.intToTypeConverterHash.put(12, String.class);
        this.intToTypeConverterHash.put(91, java.sql.Date.class);
        this.intToTypeConverterHash.put(93, java.sql.Timestamp.class);
        this.intToTypeConverterHash.put(3, java.math.BigDecimal.class);

        this.intToTypeConverterHash.put(-5, java.math.BigDecimal.class);
        this.intToTypeConverterHash.put(7, Float.class);
        this.intToTypeConverterHash.put(-1, String.class);
        this.intToTypeConverterHash.put(92, Time.class);
        this.intToTypeConverterHash.put(-2, Byte[].class);
        this.intToTypeConverterHash.put(-4, Byte[].class);
        //this.intToTypeConverterHash.put(Integer.valueOf(0),null.class);
    }

    /**
     * INTERNAL: Given a call, this method produces the stored procedure string
     * based on the SQL string inside the call.
     */
    protected String buildProcedureString(SQLCall call) {
        String stringToModify = call.getSQLString();
        String replacementToken = getSession().getPlatform().getStoredProcedureParameterPrefix();
        StringWriter stringWriter = new StringWriter();
        int startIndex = 0;
        int nextParamIndex = 0;
        int tokenIndex = stringToModify.indexOf('?');

        while (tokenIndex != -1) {
            stringWriter.write(stringToModify.substring(startIndex, tokenIndex));
            startIndex = tokenIndex + 1;
            Object parameter = call.getParameters().get(nextParamIndex);
            if (parameter instanceof DatabaseField field) {
                stringWriter.write(replacementToken);
                stringWriter.write(field.getName());
            } else if (parameter instanceof ParameterExpression expression) {
                stringWriter.write(replacementToken);
                stringWriter.write(expression.getField().getName());
            } else {
                getSession().getPlatform().appendParameter(call, stringWriter, parameter);
            }

            tokenIndex = stringToModify.indexOf('?', startIndex);
            nextParamIndex++;
        }
        stringWriter.write(stringToModify.substring(startIndex));

        return stringWriter.toString();
    }

    /**
     * PUBLIC: Generate an amendment class that will set up the descriptors to use
     * these stored procedures.
     */
    public void generateAmendmentClass(Writer outputWriter, String packageName, String className) throws ValidationException {
        String methodComment = "/**\n * EclipseLink generated method. \n * <b>WARNING</b>: This code was generated by an automated tool.\n * Any changes will be lost when the code is re-generated\n */";
        ClassDescriptor descriptor;
        List<StoredProcedureDefinition> storedProcedureVector;
        Map<String, Map<String, StoredProcedureDefinition>> mappingHashtable;
        StoredProcedureDefinition definition;
        List<FieldDefinition> storedProcedureDefinitionArguments;
        Iterator<FieldDefinition> argumentEnum;
        FieldDefinition fieldDefinition;
        try {
            outputWriter.write("package ");
            outputWriter.write(packageName);
            outputWriter.write(";\n\nimport java.util.*;\nimport java.lang.reflect.*;");
            outputWriter.write("\nimport org.eclipse.persistence.queries.*;\nimport org.eclipse.persistence.sessions.*;\nimport org.eclipse.persistence.mappings.*;\n\n/**\n * ");
            outputWriter.write("This is a EclipseLink generated class to add stored procedure amendments to a project.  \n * Any changes to this code will be lost when the class is regenerated \n */\npublic class ");
            outputWriter.write(className);
            outputWriter.write("{\n");
            Iterator<ClassDescriptor> iterator1 = this.storedProcedures.keySet().iterator();
            while (iterator1.hasNext()) {
                descriptor = iterator1.next();
                if (descriptor.isDescriptorForInterface() || descriptor.isAggregateDescriptor()) {
                    continue;
                }
                outputWriter.write(methodComment);
                outputWriter.write("\npublic static void amend");
                outputWriter.write(descriptor.getJavaClass().getSimpleName());
                outputWriter.write("ClassDescriptor(ClassDescriptor descriptor){\n\t");
                storedProcedureVector = this.storedProcedures.get(descriptor);
                mappingHashtable = this.mappingStoredProcedures.get(descriptor);
                definition = storedProcedureVector.get(0);
                outputWriter.write("\n\t//INSERT QUERY\n");
                outputWriter.write("\tInsertObjectQuery insertQuery = new InsertObjectQuery();\n\tStoredProcedureCall call = new StoredProcedureCall();\n");
                outputWriter.write("\tcall.setProcedureName(\"");
                outputWriter.write(definition.getName());
                outputWriter.write("\");\n\t");
                storedProcedureDefinitionArguments = definition.getArguments();
                argumentEnum = storedProcedureDefinitionArguments.iterator();

                while (argumentEnum.hasNext()) {
                    fieldDefinition = argumentEnum.next();
                    outputWriter.write("call.addNamedArgument(\"");
                    outputWriter.write(fieldDefinition.getName());
                    outputWriter.write("\", \"");
                    outputWriter.write(this.getFieldName(fieldDefinition.getName()));
                    outputWriter.write("\");\n\t");
                }
                outputWriter.write("insertQuery.setCall(call);\n\tdescriptor.getQueryManager().setInsertQuery(insertQuery);\n\t");
                definition = storedProcedureVector.get(1);
                if (definition != null) {
                    outputWriter.write("\n\t//UPDATE QUERY\n");
                    outputWriter.write("\tUpdateObjectQuery updateQuery = new UpdateObjectQuery();\n\tcall = new StoredProcedureCall();\n");
                    outputWriter.write("\tcall.setProcedureName(\"");
                    outputWriter.write(definition.getName());
                    outputWriter.write("\");\n\t");
                    storedProcedureDefinitionArguments = definition.getArguments();
                    argumentEnum = storedProcedureDefinitionArguments.iterator();
                    while (argumentEnum.hasNext()) {
                        fieldDefinition = argumentEnum.next();
                        outputWriter.write("call.addNamedArgument(\"");
                        outputWriter.write(fieldDefinition.getName());
                        outputWriter.write("\", \"");
                        outputWriter.write(this.getFieldName(fieldDefinition.getName()));
                        outputWriter.write("\");\n\t");
                    }
                    outputWriter.write("updateQuery.setCall(call);\n\tdescriptor.getQueryManager().setUpdateQuery(updateQuery);\n");
                }
                definition = storedProcedureVector.get(2);
                outputWriter.write("\n\t//DELETE QUERY\n");
                outputWriter.write("\tDeleteObjectQuery deleteQuery = new DeleteObjectQuery();\n\tcall = new StoredProcedureCall();\n");
                outputWriter.write("\tcall.setProcedureName(\"");
                outputWriter.write(definition.getName());
                outputWriter.write("\");\n\t");
                storedProcedureDefinitionArguments = definition.getArguments();
                argumentEnum = storedProcedureDefinitionArguments.iterator();
                while (argumentEnum.hasNext()) {
                    fieldDefinition = argumentEnum.next();
                    outputWriter.write("call.addNamedArgument(\"");
                    outputWriter.write(fieldDefinition.getName());
                    outputWriter.write("\", \"");
                    outputWriter.write(this.getFieldName(fieldDefinition.getName()));
                    outputWriter.write("\");\n\t");
                }
                outputWriter.write("deleteQuery.setCall(call);\n\tdescriptor.getQueryManager().setDeleteQuery(deleteQuery);\n");
                if (storedProcedureVector.size() > 3) {
                    definition = storedProcedureVector.get(3);
                    outputWriter.write("\n\t//READ OBJECT QUERY\n");
                    outputWriter.write("\tReadObjectQuery readQuery = new ReadObjectQuery();\n\tcall = new StoredProcedureCall();\n");
                    outputWriter.write("\tcall.setProcedureName(\"");
                    outputWriter.write(definition.getName());
                    outputWriter.write("\");\n\t");
                    storedProcedureDefinitionArguments = definition.getArguments();
                    argumentEnum = storedProcedureDefinitionArguments.iterator();
                    while (argumentEnum.hasNext()) {
                        fieldDefinition = argumentEnum.next();
                        outputWriter.write("call.addNamedArgument(\"");
                        outputWriter.write(fieldDefinition.getName());
                        outputWriter.write("\", \"");
                        outputWriter.write(this.getFieldName(fieldDefinition.getName()));
                        outputWriter.write("\");\n\t");
                    }
                    outputWriter.write("readQuery.setCall(call);\n\tdescriptor.getQueryManager().setReadObjectQuery(readQuery);\n");
                }

                //generate read all stored procs.
                if (storedProcedureVector.size() > 4) {
                    definition = storedProcedureVector.get(4);
                    outputWriter.write("\n\t//READ ALL QUERY\n");
                    outputWriter.write("\tReadAllQuery readAllQuery = new ReadAllQuery();\n\tcall = new StoredProcedureCall();\n");
                    outputWriter.write("\tcall.setProcedureName(\"");
                    outputWriter.write(definition.getName());
                    outputWriter.write("\");\n\t");
                    storedProcedureDefinitionArguments = definition.getArguments();
                    argumentEnum = storedProcedureDefinitionArguments.iterator();
                    while (argumentEnum.hasNext()) {
                        fieldDefinition = argumentEnum.next();
                        outputWriter.write("call.addNamedArgument(\"");
                        outputWriter.write(fieldDefinition.getName());
                        outputWriter.write("\", \"");
                        outputWriter.write(this.getFieldName(fieldDefinition.getName()));
                        outputWriter.write("\");\n\t");
                    }
                    outputWriter.write("readAllQuery.setCall(call);\n\tdescriptor.getQueryManager().setReadAllQuery(readAllQuery);\n");
                }

                //generate 1:m mapping procedures.
                if (mappingHashtable != null) {
                    outputWriter.write("\n\t//MAPPING QUERIES\n");
                    //read all
                    outputWriter.write("\tReadAllQuery mappingQuery; \n");
                    outputWriter.write("\tDeleteAllQuery deleteMappingQuery; \n");
                    for (Iterator<String> iterator = mappingHashtable.keySet().iterator(); iterator.hasNext();) {
                        String mappingName = iterator.next();
                        definition = mappingHashtable.get(mappingName).get("1MREAD");
                        if (definition != null) {
                            outputWriter.write("\n\t//MAPPING READALL QUERY FOR " + mappingName + "\n");
                            outputWriter.write("\tmappingQuery= new ReadAllQuery();\n\tcall = new StoredProcedureCall();\n");
                            outputWriter.write("\tcall.setProcedureName(\"");
                            outputWriter.write(definition.getName());
                            outputWriter.write("\");\n\t");
                            storedProcedureDefinitionArguments = definition.getArguments();
                            argumentEnum = storedProcedureDefinitionArguments.iterator();
                            while (argumentEnum.hasNext()) {
                                fieldDefinition = argumentEnum.next();
                                outputWriter.write("call.addNamedArgument(\"");
                                outputWriter.write(fieldDefinition.getName());
                                outputWriter.write("\", \"");
                                outputWriter.write(this.getFieldName(fieldDefinition.getName()));
                                outputWriter.write("\");\n\t");
                            }
                            outputWriter.write("mappingQuery.setCall(call);\n\t((OneToManyMapping)descriptor.getMappingForAttributeName(\"" + mappingName + "\")).setCustomSelectionQuery(mappingQuery);\n");
                        }

                        //DeleteAll Query
                        definition = mappingHashtable.get(mappingName).get("1MDALL");
                        if (definition != null) {
                            outputWriter.write("\n\t//MAPPING DELETEALL QUERY FOR " + mappingName + "\n");
                            outputWriter.write("\tdeleteMappingQuery= new DeleteAllQuery();\n\tcall = new StoredProcedureCall();\n");
                            outputWriter.write("\tcall.setProcedureName(\"");
                            outputWriter.write(definition.getName());
                            outputWriter.write("\");\n\t");
                            storedProcedureDefinitionArguments = definition.getArguments();
                            argumentEnum = storedProcedureDefinitionArguments.iterator();
                            while (argumentEnum.hasNext()) {
                                fieldDefinition = argumentEnum.next();
                                outputWriter.write("call.addNamedArgument(\"");
                                outputWriter.write(fieldDefinition.getName());
                                outputWriter.write("\", \"");
                                outputWriter.write(this.getFieldName(fieldDefinition.getName()));
                                outputWriter.write("\");\n\t");
                            }
                            outputWriter.write("deleteMappingQuery.setCall(call);\n\t((OneToManyMapping)descriptor.getMappingForAttributeName(\"" + mappingName + "\")).setCustomDeleteAllQuery(deleteMappingQuery);\n");
                        }
                    }
                }
                outputWriter.write("}\n");
            }
            definition = sequenceProcedures.get("SELECT");
            if (definition != null) {
                outputWriter.write("\n\tValueReadQuery seqSelectQuery = new ValueReadQuery();\n\tcall = new StoredProcedureCall();\n");
                outputWriter.write("\tcall.setProcedureName(\"");
                outputWriter.write(definition.getName());
                outputWriter.write("\");\n\t");
                storedProcedureDefinitionArguments = definition.getArguments();
                argumentEnum = storedProcedureDefinitionArguments.iterator();
                while (argumentEnum.hasNext()) {
                    fieldDefinition = argumentEnum.next();
                    outputWriter.write("call.addNamedArgument(\"");
                    outputWriter.write(fieldDefinition.getName());
                    outputWriter.write("\", \"");
                    outputWriter.write(this.getFieldName(fieldDefinition.getName()));
                    outputWriter.write("\");\n\t");
                    outputWriter.write("seqSelectQuery.addArgument(\"" + this.getFieldName(fieldDefinition.getName()));
                    outputWriter.write("\");\n\t");
                }
                outputWriter.write("seqSelectQuery.setCall(call);\n\tproject.getLogin().setSelectSequenceNumberQuery(seqSelectQuery);\n");
            }
            outputWriter.write("}\n");

            outputWriter.write(methodComment);
            outputWriter.write("\npublic static void amendDescriptors(org.eclipse.persistence.sessions.Project project) throws Exception{");
            outputWriter.write("\n\tamendSequences(project);");
            outputWriter.write("\n\tfor(Iterator enumtr = project.getDescriptors().values().iterator(); enumtr.hasNext();) {");
            outputWriter.write("\n\t\tDescriptor descriptor = (ClassDescriptor)enumtr.next();");
            outputWriter.write("\n\t\tif(!(descriptor.isAggregateDescriptor() || descriptor.isDescriptorForInterface())) {");
            outputWriter.write("\n\t\t\tMethod method = " + className + ".class.getMethod(\"amend\"+descriptor.getJavaClass().getSimpleName()+\"ClassDescriptor\", new Class[] {ClassDescriptor.class});");
            outputWriter.write("\n\t\t\tmethod.invoke(null, new Object[] {descriptor});");
            outputWriter.write("\n\t\t}");
            outputWriter.write("\n\t}");
            outputWriter.write("\n}");
            outputWriter.write("\n}\n");
            outputWriter.flush();
        } catch (IOException exception) {
            throw ValidationException.fileError(exception);
        }
    }

    /**
     * INTERNAL: Generates the delete stored procedure for this descriptor
     */
    protected StoredProcedureDefinition generateDeleteStoredProcedure(ClassDescriptor descriptor) {
        DeleteObjectQuery deleteQuery = new DeleteObjectQuery();
        deleteQuery.setDescriptor(descriptor);
        deleteQuery.setModifyRow(new DatabaseRecord());
        return this.generateObjectStoredProcedure(deleteQuery, descriptor.getPrimaryKeyFields(), "DEL_");

    }

    /**
     * INTERNAL: Generates the insert stored procedure for this descriptor
     */
    protected StoredProcedureDefinition generateInsertStoredProcedure(ClassDescriptor descriptor) {
        InsertObjectQuery insertQuery = new InsertObjectQuery();
        insertQuery.setDescriptor(descriptor);
        insertQuery.setModifyRow(descriptor.getObjectBuilder().buildTemplateInsertRow(getSession()));
        return this.generateObjectStoredProcedure(insertQuery, descriptor.getFields(), "INS_");
    }

    /**
     * INTERNAL: Generates the mapping stored procedures for this descriptor.
     * currently only 1:1 and 1:M are supported
     */
    protected Map<String, Map<String, StoredProcedureDefinition>> generateMappingStoredProcedures(ClassDescriptor descriptor) {
        List<DatabaseMapping> mappings = descriptor.getMappings();
        Map<String, Map<String, StoredProcedureDefinition>> mappingSP = new Hashtable<>();
        Map<String, StoredProcedureDefinition> mappingTable;
        for (DatabaseMapping mapping: mappings) {
            mappingTable = new Hashtable<>();
            if (mapping.isOneToManyMapping()) {
                if (!getSession().getPlatform().isOracle()) {
                    //reads not supported in oracle
                    mappingTable.put("1MREAD", generateOneToManyMappingReadProcedure((OneToManyMapping)mapping));
                }
                if (mapping.isPrivateOwned()) {
                    //generate delete all for 1:M query
                    mappingTable.put("1MDALL", generateOneToManyMappingDeleteAllProcedure((OneToManyMapping)mapping));
                }
                mappingSP.put(mapping.getAttributeName(), mappingTable);
            }
        }
        return mappingSP;

    }

    /**
     * INTERNAL: Generates the object level stored procedure based on the passed in query
     */
    protected StoredProcedureDefinition generateObjectStoredProcedure(DatabaseQuery query, List<DatabaseField> fields, String namePrefix) {
        String className = query.getDescriptor().getJavaClass().getSimpleName();

        return generateStoredProcedure(query, fields, getPrefix() + namePrefix + className);
    }

    /**
     * INTERNAL: Generates the delete all stored procedure for this mapping
     */
    protected StoredProcedureDefinition generateOneToManyMappingDeleteAllProcedure(OneToManyMapping mapping) {
        ClassDescriptor targetDescriptor = mapping.getReferenceDescriptor();
        DeleteAllQuery deleteAllQuery = new DeleteAllQuery();
        deleteAllQuery.setDescriptor(targetDescriptor);
        deleteAllQuery.setReferenceClass(targetDescriptor.getJavaClass());
        deleteAllQuery.setSelectionCriteria(mapping.getSelectionCriteria());
        return generateOneToManyMappingProcedures(mapping, deleteAllQuery, mapping.getTargetForeignKeysToSourceKeys(), "D_1M_");
    }

    /**
     * INTERNAL: Generates all the stored procedures for this mapping
     */
    protected StoredProcedureDefinition generateOneToManyMappingProcedures(OneToManyMapping mapping, DatabaseQuery query, Map<DatabaseField, DatabaseField> fields, String namePrefix) {
        String sourceClassName = mapping.getDescriptor().getJavaClass().getSimpleName();
        return generateStoredProcedure(query, new ArrayList<>(fields.values()), getPrefix() + namePrefix + sourceClassName + "_" + mapping.getAttributeName());
    }

    /**
     * INTERNAL: Generates the read all stored procedure for this mapping
     */
    protected StoredProcedureDefinition generateOneToManyMappingReadProcedure(OneToManyMapping mapping) {
        ClassDescriptor targetDescriptor = mapping.getReferenceDescriptor();

        ReadAllQuery readAllQuery = new ReadAllQuery();
        readAllQuery.setDescriptor(targetDescriptor);
        readAllQuery.setReferenceClass(targetDescriptor.getJavaClass());
        readAllQuery.setSelectionCriteria(mapping.getSelectionCriteria());
        return generateOneToManyMappingProcedures(mapping, readAllQuery, mapping.getTargetForeignKeysToSourceKeys(), "R_1M_");
    }

    /**
     * INTERNAL: Generates the read all stored procedure for this descriptor
     */
    protected StoredProcedureDefinition generateReadAllStoredProcedure(ClassDescriptor descriptor) {
        ReadAllQuery readAllQuery = new ReadAllQuery();
        readAllQuery.setDescriptor(descriptor);
        readAllQuery.setReferenceClass(descriptor.getJavaClass());
        return generateObjectStoredProcedure(readAllQuery, descriptor.getPrimaryKeyFields(), "RALL_");

    }

    /**
     * INTERNAL: Generates the read stored procedure for this descriptor
     */
    protected StoredProcedureDefinition generateReadStoredProcedure(ClassDescriptor descriptor) {
        ReadObjectQuery readQuery = new ReadObjectQuery();
        readQuery.setDescriptor(descriptor);
        readQuery.setReferenceClass(descriptor.getJavaClass());
        return generateObjectStoredProcedure(readQuery, descriptor.getPrimaryKeyFields(), "READ_");

    }

    /**
     * INTERNAL: Generates the select and update stored procedures for this project.
     * no procedures are generated for native sequencing.  Note: reads are not supported in Oracle.
     */
    protected void generateSequenceStoredProcedures(Project project) {
        DatabaseLogin login = (DatabaseLogin)project.getDatasourceLogin();
        if (login.shouldUseNativeSequencing()) {
            // There is nothing required for native SQL.
            return;
        }

        if (project.usesSequencing()) {
            if (!getSession().getPlatform().isOracle()) {
                // CR#3934352, updated to support new sequencing and use a single procedure.
                StoredProcedureDefinition definition = new StoredProcedureDefinition();
                definition.setName(FrameworkHelper.truncate(project.getName() + "SEQ_SEL", MAX_NAME_SIZE));
                definition.addArgument("SEQ_NAME", String.class, 100);
                definition.addArgument("PREALLOC_SIZE", java.math.BigDecimal.class, 10);
                definition.addStatement("UPDATE " + ((TableSequence)login.getDefaultSequence()).getTableName() + " SET "
                    + ((TableSequence)login.getDefaultSequence()).getCounterFieldName() + " = "
                    + ((TableSequence)login.getDefaultSequence()).getCounterFieldName() + " + "
                    + getSession().getPlatform().getStoredProcedureParameterPrefix() + "PREALLOC_SIZE WHERE "
                    + ((TableSequence)login.getDefaultSequence()).getNameFieldName() + " = "
                    + getSession().getPlatform().getStoredProcedureParameterPrefix() + "SEQ_NAME");
                definition.addStatement("SELECT " + ((TableSequence)login.getDefaultSequence()).getCounterFieldName() + " FROM "
                    + ((TableSequence)login.getDefaultSequence()).getTableName() + " WHERE "
                    + ((TableSequence)login.getDefaultSequence()).getNameFieldName() + " = "
                    + getSession().getPlatform().getStoredProcedureParameterPrefix() + "SEQ_NAME");
                sequenceProcedures.put("SELECT", definition);
                writeDefinition(definition);
            }
        }
    }

    /**
     * INTERNAL: Generates the stored procedure for this query.  A new row
     * will be used for the check prepare.
     */
    protected StoredProcedureDefinition generateStoredProcedure(DatabaseQuery query, List<DatabaseField> fields, String name) {
        return generateStoredProcedure(query, fields, new DatabaseRecord(), name);
    }

    /**
     * INTERNAL: Generates the stored procedure for this query using the row
     * passed in for the check prepare.
     */
    protected StoredProcedureDefinition generateStoredProcedure(DatabaseQuery query, List<DatabaseField> fields, AbstractRecord rowForPrepare, String name) {
        StoredProcedureDefinition definition = new StoredProcedureDefinition();
        List<DatasourceCall> callVector;
        List<String> statementVector = new ArrayList<>();

        query.checkPrepare(getSession(), rowForPrepare, true);
        callVector = ((CallQueryMechanism)query.getQueryMechanism()).getCalls();
        if (callVector.isEmpty()) {
            if (((CallQueryMechanism)query.getQueryMechanism()).getCall() == null) {
                // do nothing
            } else {
                callVector.add(((CallQueryMechanism)query.getQueryMechanism()).getCall());
            }
        }
        Iterator<DatasourceCall> iterator1 = callVector.iterator();
        while (iterator1.hasNext()) {
            SQLCall call = (SQLCall) iterator1.next();
            statementVector.add(this.buildProcedureString(call));
        }
        definition.setStatements(statementVector);
        DatabaseField databaseField;
        AbstractRecord dataRow;
        Map<String, AbstractRecord> fieldNames = new Hashtable<>();
        List<DatabaseField> primaryKeyFields = fields;
        for (int index = 0; index < primaryKeyFields.size(); index++) {
            databaseField = primaryKeyFields.get(index);
            fieldNames.put(databaseField.getName(), this.schemaManager.getColumnInfo(databaseField.getTableName(), databaseField.getName()).get(0));
        }

        definition.setName(FrameworkHelper.truncate(name, MAX_NAME_SIZE));
        Iterator<String> iterator = fieldNames.keySet().iterator();
        String prefixArgToken;
        if (getSession().getPlatform().isOracle()) {
            prefixArgToken = getSession().getPlatform().getStoredProcedureParameterPrefix();
        } else {
            prefixArgToken = "";
        }
        while (iterator.hasNext()) {
            Number dataType;
            dataRow = fieldNames.get(iterator.next());
            dataType = (Number)dataRow.get("DATA_TYPE");
            Class<?> type = this.getFieldType(dataType);
            String typeName = (String)dataRow.get("TYPE_NAME");
            if ((type != null) || (typeName == null) || (typeName.isEmpty())) {
                definition.addArgument(prefixArgToken + dataRow.get("COLUMN_NAME"), type, ((Number)dataRow.get("COLUMN_SIZE")).intValue());
            } else {
                definition.addArgument(prefixArgToken + dataRow.get("COLUMN_NAME"), typeName);
            }
        }

        return definition;

    }

    /**
     * generates all the stored procedures using the schema manager.  The schema manager
     * may be set to write directly to the database or to the file.  See
     * outputDDLToWriter(Writer) and outputDDLToDatabase() on SchemaManager
     */
    public void generateStoredProcedures() {
        // Must turn binding off to ensure literals are printed correctly.
        boolean wasBinding = getSession().getLogin().shouldBindAllParameters();
        getSession().getLogin().setShouldBindAllParameters(false);
        Map<Class<?>, ClassDescriptor> descriptors = getSession().getProject().getDescriptors();
        Iterator<Class<?>> iterator = descriptors.keySet().iterator();
        ClassDescriptor desc;
        StoredProcedureDefinition definition;
        List<StoredProcedureDefinition> definitionVector;
        this.generateSequenceStoredProcedures(getSession().getProject());
        while (iterator.hasNext()) {
            desc = descriptors.get(iterator.next());
            if (desc.isDescriptorForInterface() || desc.isDescriptorTypeAggregate()) {
                continue;
            }
            definition = this.generateInsertStoredProcedure(desc);
            definitionVector = new ArrayList<>();
            definitionVector.add(definition);
            this.writeDefinition(definition);
            definition = this.generateUpdateStoredProcedure(desc);
            definitionVector.add(definition);
            this.writeDefinition(definition);
            definition = this.generateDeleteStoredProcedure(desc);
            definitionVector.add(definition);
            this.writeDefinition(definition);
            if (!getSession().getPlatform().isOracle()) {
                definition = this.generateReadStoredProcedure(desc);
                definitionVector.add(definition);
                this.writeDefinition(definition);
                definition = this.generateReadAllStoredProcedure(desc);
                definitionVector.add(definition);
                this.writeDefinition(definition);
            }
            Map<String, Map<String, StoredProcedureDefinition>> mappingDefinitions = this.generateMappingStoredProcedures(desc);
            for (Iterator<Map<String, StoredProcedureDefinition>> iterator1 = mappingDefinitions.values().iterator(); iterator1.hasNext();) {
                Map<String, StoredProcedureDefinition> table = iterator1.next();
                definition = table.get("1MREAD");
                if (definition != null) {
                    this.writeDefinition(definition);
                }
                definition = table.get("1MDALL");
                if (definition != null) {
                    this.writeDefinition(definition);
                }
            }
            this.storedProcedures.put(desc, definitionVector);
            if (!mappingDefinitions.isEmpty()) {
                this.mappingStoredProcedures.put(desc, mappingDefinitions);
            }
        }
        getSession().getLogin().setShouldBindAllParameters(wasBinding);
    }

    /**
     * generates all the stored procedures to the writer using
     * the schema manager outputDDLToWriter(Writer).
     */
    public void generateStoredProcedures(Writer writerOrNull) {
        this.writer = writerOrNull;

        this.schemaManager.outputDDLToWriter(getWriter());
        this.generateStoredProcedures();
        try {
            getWriter().flush();
        } catch (IOException exception) {
            System.out.println(exception);
            exception.printStackTrace();
        }
    }

    /**
     * INTERNAL: Generates the update stored procedure for this descriptor
     */
    protected StoredProcedureDefinition generateUpdateStoredProcedure(ClassDescriptor descriptor) {
        UpdateObjectQuery updateQuery = new UpdateObjectQuery();
        updateQuery.setDescriptor(descriptor);
        updateQuery.setModifyRow(descriptor.getObjectBuilder().buildTemplateUpdateRow(getSession()));

        return this.generateObjectStoredProcedure(updateQuery, descriptor.getFields(), "UPD_");

    }

    /**
     * INTERNAL:
     * return the original field name based on the argument name.
     */
    protected String getFieldName(String argumentName) {
        if (getSession().getPlatform().isOracle()) {
            return argumentName.substring(getSession().getPlatform().getStoredProcedureParameterPrefix().length());
        } else {
            return argumentName;
        }
    }

    /**
     * INTERNAL:
     * return the class corresponding to the passed in JDBC type.
     */
    protected Class<?> getFieldType(Object jdbcDataType) {
        Integer key = ((Number) jdbcDataType).intValue();
        return intToTypeConverterHash.get(key);
    }

    public String getPrefix() {
        return prefix;
    }

    public AbstractSession getSession() {
        return schemaManager.getSession();
    }

    public Writer getWriter() {
        return writer;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * INTERNAL:
     * Verify that this project and descriptors do not have optimistic locking.
     */
    protected void verify() throws ValidationException {
        if (getSession().getProject().usesOptimisticLocking()) {
            throw ValidationException.optimisticLockingNotSupportedWithStoredProcedureGeneration();
        }
    }

    public void writeDefinition(StoredProcedureDefinition definition) {
        this.schemaManager.replaceObject(definition);
    }
}
