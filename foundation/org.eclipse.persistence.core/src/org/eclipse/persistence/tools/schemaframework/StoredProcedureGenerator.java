/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.tools.schemaframework;

import java.io.*;
import java.util.*;
import java.sql.Time;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.sequencing.TableSequence;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.internal.queries.*;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.expressions.ParameterExpression;

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
    private Hashtable storedProcedures;
    /** This hashtable is used to store the storedProcedure referenced by the mapping name. */
    private Hashtable mappingStoredProcedures;
    private Hashtable intToTypeConverterHash;
    private Writer writer;
    private String prefix;
    private static final String DEFAULT_PREFIX = "";
    private Hashtable sequenceProcedures;
    private static final int MAX_NAME_SIZE = 30;

    public StoredProcedureGenerator(SchemaManager schemaMngr) {
        super();
        this.schemaManager = schemaMngr;
        this.sequenceProcedures = new Hashtable();
        this.storedProcedures = new Hashtable();
        this.mappingStoredProcedures = new Hashtable();
        this.buildIntToTypeConverterHash();
        this.prefix = DEFAULT_PREFIX;
        this.verify();
    }

    /**
     * INTERNAL: Build all conversions based on JDBC return values.
     */
    protected void buildIntToTypeConverterHash() {
        this.intToTypeConverterHash = new Hashtable();
        this.intToTypeConverterHash.put(Integer.valueOf(8), Double.class);
        this.intToTypeConverterHash.put(Integer.valueOf(-7), Boolean.class);
        this.intToTypeConverterHash.put(Integer.valueOf(-3), Byte[].class);
        this.intToTypeConverterHash.put(Integer.valueOf(-6), Short.class);
        this.intToTypeConverterHash.put(Integer.valueOf(5), Short.class);
        this.intToTypeConverterHash.put(Integer.valueOf(4), Integer.class);
        this.intToTypeConverterHash.put(Integer.valueOf(2), java.math.BigDecimal.class);
        this.intToTypeConverterHash.put(Integer.valueOf(6), Float.class);
        this.intToTypeConverterHash.put(Integer.valueOf(1), Character.class);
        this.intToTypeConverterHash.put(Integer.valueOf(12), String.class);
        this.intToTypeConverterHash.put(Integer.valueOf(91), java.sql.Date.class);
        this.intToTypeConverterHash.put(Integer.valueOf(93), java.sql.Timestamp.class);
        this.intToTypeConverterHash.put(Integer.valueOf(3), java.math.BigDecimal.class);

        this.intToTypeConverterHash.put(Integer.valueOf(-5), java.math.BigDecimal.class);
        this.intToTypeConverterHash.put(Integer.valueOf(7), Float.class);
        this.intToTypeConverterHash.put(Integer.valueOf(-1), String.class);
        this.intToTypeConverterHash.put(Integer.valueOf(92), Time.class);
        this.intToTypeConverterHash.put(Integer.valueOf(-2), Byte[].class);
        this.intToTypeConverterHash.put(Integer.valueOf(-4), Byte[].class);
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
        int tokenIndex = stringToModify.indexOf("?");

        while (tokenIndex != -1) {
            stringWriter.write(stringToModify.substring(startIndex, tokenIndex));
            startIndex = tokenIndex + 1;
            Object parameter = call.getParameters().get(nextParamIndex);
            if (parameter instanceof DatabaseField) {
                stringWriter.write(replacementToken);
                stringWriter.write(((DatabaseField)parameter).getName());
            } else if (parameter instanceof ParameterExpression) {
                stringWriter.write(replacementToken);
                stringWriter.write(((ParameterExpression)parameter).getField().getName());
            } else {
                getSession().getPlatform().appendParameter(call, stringWriter, parameter);
            }

            tokenIndex = stringToModify.indexOf("?", startIndex);
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
        Vector storedProcedureVector;
        Hashtable mappingHashtable;
        StoredProcedureDefinition definition;
        Vector storedProcedureDefinitionArguments;
        Enumeration argumentEnum;
        FieldDefinition fieldDefinition;
        try {
            outputWriter.write("package ");
            outputWriter.write(packageName);
            outputWriter.write(";\n\nimport java.util.*;\nimport java.lang.reflect.*;");
            outputWriter.write("\nimport org.eclipse.persistence.queries.*;\nimport org.eclipse.persistence.sessions.*;\nimport org.eclipse.persistence.mappings.*;\n\n/**\n * ");
            outputWriter.write("This is a EclipseLink generated class to add stored procedure admendments to a project.  \n * Any changes to this code will be lost when the class is regenerated \n */\npublic class ");
            outputWriter.write(className);
            outputWriter.write("{\n");
            Enumeration descriptorEnum = this.storedProcedures.keys();
            while (descriptorEnum.hasMoreElements()) {
                descriptor = (ClassDescriptor)descriptorEnum.nextElement();
                if (descriptor.isDescriptorForInterface() || descriptor.isAggregateDescriptor()) {
                    continue;
                }
                outputWriter.write(methodComment);
                outputWriter.write("\npublic static void amend");
                outputWriter.write(Helper.getShortClassName(descriptor.getJavaClass()));
                outputWriter.write("ClassDescriptor(ClassDescriptor descriptor){\n\t");
                storedProcedureVector = (Vector)this.storedProcedures.get(descriptor);
                mappingHashtable = (Hashtable)this.mappingStoredProcedures.get(descriptor);
                definition = (StoredProcedureDefinition)storedProcedureVector.elementAt(0);
                outputWriter.write("\n\t//INSERT QUERY\n");
                outputWriter.write("\tInsertObjectQuery insertQuery = new InsertObjectQuery();\n\tStoredProcedureCall call = new StoredProcedureCall();\n");
                outputWriter.write("\tcall.setProcedureName(\"");
                outputWriter.write(definition.getName());
                outputWriter.write("\");\n\t");
                storedProcedureDefinitionArguments = definition.getArguments();
                argumentEnum = storedProcedureDefinitionArguments.elements();

                while (argumentEnum.hasMoreElements()) {
                    fieldDefinition = (FieldDefinition)argumentEnum.nextElement();
                    outputWriter.write("call.addNamedArgument(\"");
                    outputWriter.write(fieldDefinition.getName());
                    outputWriter.write("\", \"");
                    outputWriter.write(this.getFieldName(fieldDefinition.getName()));
                    outputWriter.write("\");\n\t");
                }
                outputWriter.write("insertQuery.setCall(call);\n\tdescriptor.getQueryManager().setInsertQuery(insertQuery);\n\t");
                definition = (StoredProcedureDefinition)storedProcedureVector.elementAt(1);
                if (definition != null) {
                    outputWriter.write("\n\t//UPDATE QUERY\n");
                    outputWriter.write("\tUpdateObjectQuery updateQuery = new UpdateObjectQuery();\n\tcall = new StoredProcedureCall();\n");
                    outputWriter.write("\tcall.setProcedureName(\"");
                    outputWriter.write(definition.getName());
                    outputWriter.write("\");\n\t");
                    storedProcedureDefinitionArguments = definition.getArguments();
                    argumentEnum = storedProcedureDefinitionArguments.elements();
                    while (argumentEnum.hasMoreElements()) {
                        fieldDefinition = (FieldDefinition)argumentEnum.nextElement();
                        outputWriter.write("call.addNamedArgument(\"");
                        outputWriter.write(fieldDefinition.getName());
                        outputWriter.write("\", \"");
                        outputWriter.write(this.getFieldName(fieldDefinition.getName()));
                        outputWriter.write("\");\n\t");
                    }
                    outputWriter.write("updateQuery.setCall(call);\n\tdescriptor.getQueryManager().setUpdateQuery(updateQuery);\n");
                }
                definition = (StoredProcedureDefinition)storedProcedureVector.elementAt(2);
                outputWriter.write("\n\t//DELETE QUERY\n");
                outputWriter.write("\tDeleteObjectQuery deleteQuery = new DeleteObjectQuery();\n\tcall = new StoredProcedureCall();\n");
                outputWriter.write("\tcall.setProcedureName(\"");
                outputWriter.write(definition.getName());
                outputWriter.write("\");\n\t");
                storedProcedureDefinitionArguments = definition.getArguments();
                argumentEnum = storedProcedureDefinitionArguments.elements();
                while (argumentEnum.hasMoreElements()) {
                    fieldDefinition = (FieldDefinition)argumentEnum.nextElement();
                    outputWriter.write("call.addNamedArgument(\"");
                    outputWriter.write(fieldDefinition.getName());
                    outputWriter.write("\", \"");
                    outputWriter.write(this.getFieldName(fieldDefinition.getName()));
                    outputWriter.write("\");\n\t");
                }
                outputWriter.write("deleteQuery.setCall(call);\n\tdescriptor.getQueryManager().setDeleteQuery(deleteQuery);\n");
                if (storedProcedureVector.size() > 3) {
                    definition = (StoredProcedureDefinition)storedProcedureVector.elementAt(3);
                    outputWriter.write("\n\t//READ OBJECT QUERY\n");
                    outputWriter.write("\tReadObjectQuery readQuery = new ReadObjectQuery();\n\tcall = new StoredProcedureCall();\n");
                    outputWriter.write("\tcall.setProcedureName(\"");
                    outputWriter.write(definition.getName());
                    outputWriter.write("\");\n\t");
                    storedProcedureDefinitionArguments = definition.getArguments();
                    argumentEnum = storedProcedureDefinitionArguments.elements();
                    while (argumentEnum.hasMoreElements()) {
                        fieldDefinition = (FieldDefinition)argumentEnum.nextElement();
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
                    definition = (StoredProcedureDefinition)storedProcedureVector.elementAt(4);
                    outputWriter.write("\n\t//READ ALL QUERY\n");
                    outputWriter.write("\tReadAllQuery readAllQuery = new ReadAllQuery();\n\tcall = new StoredProcedureCall();\n");
                    outputWriter.write("\tcall.setProcedureName(\"");
                    outputWriter.write(definition.getName());
                    outputWriter.write("\");\n\t");
                    storedProcedureDefinitionArguments = definition.getArguments();
                    argumentEnum = storedProcedureDefinitionArguments.elements();
                    while (argumentEnum.hasMoreElements()) {
                        fieldDefinition = (FieldDefinition)argumentEnum.nextElement();
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
                    for (Enumeration e = mappingHashtable.keys(); e.hasMoreElements();) {
                        String mappingName = (String)e.nextElement();
                        definition = (StoredProcedureDefinition)((Hashtable)mappingHashtable.get(mappingName)).get("1MREAD");
                        if (definition != null) {
                            outputWriter.write("\n\t//MAPPING READALL QUERY FOR " + mappingName + "\n");
                            outputWriter.write("\tmappingQuery= new ReadAllQuery();\n\tcall = new StoredProcedureCall();\n");
                            outputWriter.write("\tcall.setProcedureName(\"");
                            outputWriter.write(definition.getName());
                            outputWriter.write("\");\n\t");
                            storedProcedureDefinitionArguments = definition.getArguments();
                            argumentEnum = storedProcedureDefinitionArguments.elements();
                            while (argumentEnum.hasMoreElements()) {
                                fieldDefinition = (FieldDefinition)argumentEnum.nextElement();
                                outputWriter.write("call.addNamedArgument(\"");
                                outputWriter.write(fieldDefinition.getName());
                                outputWriter.write("\", \"");
                                outputWriter.write(this.getFieldName(fieldDefinition.getName()));
                                outputWriter.write("\");\n\t");
                            }
                            outputWriter.write("mappingQuery.setCall(call);\n\t((OneToManyMapping)descriptor.getMappingForAttributeName(\"" + mappingName + "\")).setCustomSelectionQuery(mappingQuery);\n");
                        }

                        //DeleteAll Query
                        definition = (StoredProcedureDefinition)((Hashtable)mappingHashtable.get(mappingName)).get("1MDALL");
                        if (definition != null) {
                            outputWriter.write("\n\t//MAPPING DELETEALL QUERY FOR " + mappingName + "\n");
                            outputWriter.write("\tdeleteMappingQuery= new DeleteAllQuery();\n\tcall = new StoredProcedureCall();\n");
                            outputWriter.write("\tcall.setProcedureName(\"");
                            outputWriter.write(definition.getName());
                            outputWriter.write("\");\n\t");
                            storedProcedureDefinitionArguments = definition.getArguments();
                            argumentEnum = storedProcedureDefinitionArguments.elements();
                            while (argumentEnum.hasMoreElements()) {
                                fieldDefinition = (FieldDefinition)argumentEnum.nextElement();
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
            definition = (StoredProcedureDefinition)sequenceProcedures.get("SELECT");
            if (definition != null) {
                outputWriter.write("\n\tValueReadQuery seqSelectQuery = new ValueReadQuery();\n\tcall = new StoredProcedureCall();\n");
                outputWriter.write("\tcall.setProcedureName(\"");
                outputWriter.write(definition.getName());
                outputWriter.write("\");\n\t");
                storedProcedureDefinitionArguments = definition.getArguments();
                argumentEnum = storedProcedureDefinitionArguments.elements();
                while (argumentEnum.hasMoreElements()) {
                    fieldDefinition = (FieldDefinition)argumentEnum.nextElement();
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
            outputWriter.write("\n\t\t\tMethod method = " + className + ".class.getMethod(\"amend\"+org.eclipse.persistence.internal.helper.Helper.getShortClassName(descriptor.getJavaClass())+\"ClassDescriptor\", new Class[] {ClassDescriptor.class});");
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
    protected Hashtable generateMappingStoredProcedures(ClassDescriptor descriptor) {
        Vector mappings = descriptor.getMappings();
        Hashtable mappingSP = new Hashtable();
        Hashtable mappingTable;
        for (Enumeration enumtr = mappings.elements(); enumtr.hasMoreElements();) {
            mappingTable = new Hashtable();
            DatabaseMapping mapping = (DatabaseMapping)enumtr.nextElement();
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
    protected StoredProcedureDefinition generateObjectStoredProcedure(DatabaseQuery query, List fields, String namePrefix) {
        String className = Helper.getShortClassName(query.getDescriptor().getJavaClass());

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
        return generateOneToManyMappingProcedures(mapping, deleteAllQuery, mapping.getTargetForeignKeyToSourceKeys(), "D_1M_");
    }

    /**
     * INTERNAL: Generates all the stored procedures for this mapping
     */
    protected StoredProcedureDefinition generateOneToManyMappingProcedures(OneToManyMapping mapping, DatabaseQuery query, Map fields, String namePrefix) {
        String sourceClassName = Helper.getShortClassName(mapping.getDescriptor().getJavaClass());
        return generateStoredProcedure(query, new ArrayList(fields.values()), getPrefix() + namePrefix + sourceClassName + "_" + mapping.getAttributeName());
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
        return generateOneToManyMappingProcedures(mapping, readAllQuery, mapping.getTargetForeignKeyToSourceKeys(), "R_1M_");
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
    protected void generateSequenceStoredProcedures(org.eclipse.persistence.sessions.Project project) {
        DatabaseLogin login = (DatabaseLogin)project.getDatasourceLogin();
        if (login.shouldUseNativeSequencing()) {
            // There is nothing required for native SQL.
            return;
        }

        if (project.usesSequencing()) {
            if (!getSession().getPlatform().isOracle()) {
                // CR#3934352, updated to support new sequencing and use a single procedure.
                StoredProcedureDefinition definition = new StoredProcedureDefinition();
                definition.setName(Helper.truncate(project.getName() + "SEQ_SEL", MAX_NAME_SIZE));
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
    protected StoredProcedureDefinition generateStoredProcedure(DatabaseQuery query, List fields, String name) {
        return generateStoredProcedure(query, fields, new DatabaseRecord(), name);
    }

    /**
     * INTERNAL: Generates the stored procedure for this query using the row
     * passed in for the check prepare.
     */
    protected StoredProcedureDefinition generateStoredProcedure(DatabaseQuery query, List fields, AbstractRecord rowForPrepare, String name) {
        StoredProcedureDefinition definition = new StoredProcedureDefinition();
        Vector callVector;
        Vector statementVector = new Vector();

        query.checkPrepare(getSession(), rowForPrepare, true);
        callVector = ((CallQueryMechanism)query.getQueryMechanism()).getCalls();
        if (callVector.isEmpty()) {
            if (((CallQueryMechanism)query.getQueryMechanism()).getCall() == null) {
                // do nothing
            } else {
                callVector.addElement(((CallQueryMechanism)query.getQueryMechanism()).getCall());
            }
        }
        Enumeration enumtr = callVector.elements();
        while (enumtr.hasMoreElements()) {
            SQLCall call = (SQLCall)enumtr.nextElement();
            statementVector.addElement(this.buildProcedureString(call));
        }
        definition.setStatements(statementVector);
        DatabaseField databaseField;
        AbstractRecord dataRow;
        Hashtable fieldNames = new Hashtable();
        List primaryKeyFields = fields;
        for (int index = 0; index < primaryKeyFields.size(); index++) {
            databaseField = (DatabaseField)primaryKeyFields.get(index);
            fieldNames.put(databaseField.getName(), this.schemaManager.getColumnInfo(null, null, databaseField.getTableName(), databaseField.getName()).firstElement());
        }

        definition.setName(Helper.truncate(name, MAX_NAME_SIZE));
        Enumeration fieldsEnum = fieldNames.keys();
        String prefixArgToken;
        if (getSession().getPlatform().isOracle()) {
            prefixArgToken = getSession().getPlatform().getStoredProcedureParameterPrefix();
        } else {
            prefixArgToken = "";
        }
        while (fieldsEnum.hasMoreElements()) {
            Number dataType;
            dataRow = (AbstractRecord)fieldNames.get(fieldsEnum.nextElement());
            dataType = (Number)dataRow.get("DATA_TYPE");
            Class type = this.getFieldType(dataType);
            String typeName = (String)dataRow.get("TYPE_NAME");
            if ((type != null) || (typeName == null) || (typeName.length() == 0)) {
                definition.addArgument(prefixArgToken + (String)dataRow.get("COLUMN_NAME"), type, ((Number)dataRow.get("COLUMN_SIZE")).intValue());
            } else {
                definition.addArgument(prefixArgToken + (String)dataRow.get("COLUMN_NAME"), typeName);
            }
        }

        return definition;

    }

    /**
     * PUBLIC:
     * generates all the stored procedures using the schema manager.  The schema manager
     * may be set to write directly to the database on the a file.  See
     * outputDDLToWriter(Writer) and outputDDLToDatabase() on SchemaManager
     */
    public void generateStoredProcedures() {
        // Must turn binding off to ensure literals are printed correctly.
        boolean wasBinding = getSession().getLogin().shouldBindAllParameters();
        getSession().getLogin().setShouldBindAllParameters(false);
        Map descriptors = getSession().getProject().getDescriptors();
        Iterator iterator = descriptors.keySet().iterator();
        ClassDescriptor desc;
        StoredProcedureDefinition definition;
        Vector definitionVector;
        this.generateSequenceStoredProcedures(getSession().getProject());
        while (iterator.hasNext()) {
            desc = (ClassDescriptor)descriptors.get(iterator.next());
            if (desc.isDescriptorForInterface() || desc.isDescriptorTypeAggregate()) {
                continue;
            }
            definition = this.generateInsertStoredProcedure(desc);
            definitionVector = new Vector();
            definitionVector.addElement(definition);
            this.writeDefinition(definition);
            definition = this.generateUpdateStoredProcedure(desc);
            definitionVector.addElement(definition);
            this.writeDefinition(definition);
            definition = this.generateDeleteStoredProcedure(desc);
            definitionVector.addElement(definition);
            this.writeDefinition(definition);
            if (!getSession().getPlatform().isOracle()) {
                definition = this.generateReadStoredProcedure(desc);
                definitionVector.addElement(definition);
                this.writeDefinition(definition);
                definition = this.generateReadAllStoredProcedure(desc);
                definitionVector.addElement(definition);
                this.writeDefinition(definition);
            }
            Hashtable mappingDefinitions = this.generateMappingStoredProcedures(desc);
            for (Enumeration enum2 = mappingDefinitions.elements(); enum2.hasMoreElements();) {
                Hashtable table = (Hashtable)enum2.nextElement();
                definition = (StoredProcedureDefinition)table.get("1MREAD");
                if (definition != null) {
                    this.writeDefinition(definition);
                }
                definition = (StoredProcedureDefinition)table.get("1MDALL");
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
     * PUBLIC:
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
    protected Class getFieldType(Object jdbcDataType) {
        Integer key = Integer.valueOf(((Number)jdbcDataType).intValue());
        return (Class)intToTypeConverterHash.get(key);
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
    protected void verify() throws org.eclipse.persistence.exceptions.ValidationException {
        if (getSession().getProject().usesOptimisticLocking()) {
            throw org.eclipse.persistence.exceptions.ValidationException.optimisticLockingNotSupportedWithStoredProcedureGeneration();
        }
    }

    public void writeDefinition(StoredProcedureDefinition definition) {
        this.schemaManager.replaceObject(definition);
    }
}
