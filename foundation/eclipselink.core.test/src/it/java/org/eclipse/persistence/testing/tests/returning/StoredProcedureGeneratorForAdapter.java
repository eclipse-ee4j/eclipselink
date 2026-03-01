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
package org.eclipse.persistence.testing.tests.returning;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.InsertObjectQuery;
import org.eclipse.persistence.queries.StoredProcedureCall;
import org.eclipse.persistence.queries.UpdateObjectQuery;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.tools.schemaframework.FieldDefinition;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;
import org.eclipse.persistence.tools.schemaframework.StoredProcedureDefinition;
import org.eclipse.persistence.tools.schemaframework.StoredProcedureGenerator;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * This stored procedure generator is meant to generate INSERT and UPDATE
 * procedures that return values, but currently only generates
 * procedures that just perform the INSERT/UPDATE, so is not currently used.
 * It will hopefully be finished at some point...
 */
public class StoredProcedureGeneratorForAdapter extends StoredProcedureGenerator {

    public StoredProcedureGeneratorForAdapter(SchemaManager schemaMngr) {
        super(schemaMngr);
        insertStoredProcedures = new Hashtable<>();
        updateStoredProcedures = new Hashtable<>();
        substituteName = new Hashtable<>();
    }

    protected Map<ClassDescriptor, StoredProcedureDefinition> insertStoredProcedures;
    protected Map<ClassDescriptor, StoredProcedureDefinition> updateStoredProcedures;
    protected Map<StoredProcedureDefinition, Map<String, String>> substituteName;
    protected boolean useTableNames;

    public boolean usesTableNames() {
        return useTableNames;
    }

    public void setUseTableNames(boolean useTableNames) {
        this.useTableNames = useTableNames;
    }

    /**
     * PUBLIC:
     * Inspired by StoredProcedureGenerator.generateStoredProcedures though
     * uses another attribute (insertStoredProcedures) and doesn't write definitions
     */
    public void generateInsertStoredProceduresDefinitionsForProject(Project project) {
        verifyProject(project);
        Map<Class<?>, ClassDescriptor> descrpts = project.getDescriptors();
        Iterator<Class<?>> iterator = descrpts.keySet().iterator();
        ClassDescriptor desc;
        while (iterator.hasNext()) {
            desc = descrpts.get(iterator.next());
            if (desc.isDescriptorForInterface() || desc.isAggregateDescriptor()) {
                continue;
            }
            if (!desc.getQueryManager().hasInsertQuery()) {
                InsertObjectQuery insertQuery = new InsertObjectQuery();
                insertQuery.setModifyRow(desc.getObjectBuilder().buildTemplateInsertRow(getSession()));
                desc.getQueryManager().setInsertQuery(insertQuery);
            }
            StoredProcedureDefinition definition = generateStoredProcedureDefinition(desc, desc.getQueryManager().getInsertQuery(), "INS_");
            insertStoredProcedures.put(desc, definition);
        }
    }

    public void generateUpdateStoredProceduresDefinitionsForProject(Project project) {
        verifyProject(project);
        Map<Class<?>, ClassDescriptor> descrpts = project.getDescriptors();
        Iterator<Class<?>> iterator = descrpts.keySet().iterator();
        ClassDescriptor desc;
        while (iterator.hasNext()) {
            desc = descrpts.get(iterator.next());
            if (desc.isDescriptorForInterface() || desc.isAggregateDescriptor()) {
                continue;
            }
            if (!desc.getQueryManager().hasUpdateQuery()) {
                UpdateObjectQuery updateQuery = new UpdateObjectQuery();
                updateQuery.setModifyRow(desc.getObjectBuilder().buildTemplateUpdateRow(getSession()));
                if (!updateQuery.getModifyRow().isEmpty()) {
                    desc.getQueryManager().setUpdateQuery(updateQuery);
                }
            }
            if (desc.getQueryManager().getUpdateQuery() != null) {
                StoredProcedureDefinition definition = generateStoredProcedureDefinition(desc, desc.getQueryManager().getUpdateQuery(), "UPD_");
                updateStoredProcedures.put(desc, definition);
            }
        }
    }

    protected StoredProcedureDefinition generateStoredProcedureDefinition(ClassDescriptor desc, DatabaseQuery query, String namePrefix) {
        List<DatabaseField> fields = desc.getFields();
        Map<String, String> namesNewToNames = null;
        if (shouldCapitalizeNames()) {
            namesNewToNames = new Hashtable<>();
            fields = capitalize(fields, namesNewToNames);
        }
        StoredProcedureDefinition definition = generateObjectStoredProcedure(query, fields, namePrefix);
        if (namesNewToNames != null && !namesNewToNames.isEmpty()) {
            substituteName.put(definition, namesNewToNames);
        }
        return definition;
    }

    public void generateStoredProceduresDefinitionsForProject(Project project) {
        generateInsertStoredProceduresDefinitionsForProject(project);
        generateUpdateStoredProceduresDefinitionsForProject(project);
    }

    /**
     * PUBLIC:
     */
    public void writeInsertStoredProcedures() {
        for (StoredProcedureDefinition definition : insertStoredProcedures.values()) {
            writeDefinition(definition);
        }
    }

    public void writeUpdateStoredProcedures() {
        for (StoredProcedureDefinition definition : updateStoredProcedures.values()) {
            writeDefinition(definition);
        }
    }

    public void writeStoredProcedures() {
        writeInsertStoredProcedures();
        writeUpdateStoredProcedures();
    }

    /**
     * INTERNAL:
     * The base class doesn't allow optimistic locking in getSession().getProject() -
     * override this restriction.
     */
    @Override
    protected void verify() throws org.eclipse.persistence.exceptions.ValidationException {
    }

    /**
     * INTERNAL:
     * The base class doesn't allow optimistic locking in the project.
     */
    protected void verifyProject(Project project) {
        if (project.usesOptimisticLocking()) {
            throw org.eclipse.persistence.exceptions.ValidationException.optimisticLockingNotSupportedWithStoredProcedureGeneration();
        }
    }

    /**
     * PUBLIC:
     * Amends descriptors with stored procedures
     */
    public void amendDescriptorsInsertQuery() {
        for (Map.Entry<ClassDescriptor, StoredProcedureDefinition> entry : insertStoredProcedures.entrySet()) {
            InsertObjectQuery insertQuery = new InsertObjectQuery();
            defineQuery(insertQuery, entry.getValue());
            entry.getKey().getQueryManager().setInsertQuery(insertQuery);
        }
    }

    public void amendDescriptorsUpdateQuery() {
        for (Map.Entry<ClassDescriptor, StoredProcedureDefinition> entry : updateStoredProcedures.entrySet()) {
            UpdateObjectQuery updateQuery = new UpdateObjectQuery();
            defineQuery(updateQuery, entry.getValue());
            entry.getKey().getQueryManager().setUpdateQuery(updateQuery);
        }
    }

    public void defineQuery(DatabaseQuery query, StoredProcedureDefinition definition) {
        Hashtable namesNewToNames = (Hashtable)substituteName.get(definition);
        query.setShouldBindAllParameters(true);
        StoredProcedureCall call = new StoredProcedureCall();
        call.setProcedureName(definition.getName());
        for (int i = 0; i < definition.getArguments().size(); i++) {
            FieldDefinition fieldDefinition = definition.getArguments().get(i);
            String procedureParameterName = fieldDefinition.getName();
            String fieldName = getFieldName(fieldDefinition.getName());
            String argumentFieldName = fieldName;
            if (namesNewToNames != null) {
                String fieldNameOriginal = (String)namesNewToNames.get(fieldName);
                if (fieldNameOriginal != null) {
                    argumentFieldName = fieldNameOriginal;
                }
            }
            call.addNamedArgument(procedureParameterName, argumentFieldName);
        }
        query.setCall(call);
    }

    public void amendDescriptors() {
        amendDescriptorsInsertQuery();
        amendDescriptorsUpdateQuery();
    }

    // Need for capitalization is caused by bug3172139:
    // ORACLE CONNECTION METADATA.GETCOLUMNS FAILS IF PASSED LOWER-CASE NAMES
    // In case the bug is fixed, there would be no need for returning newFields.
    // However namesCapitalizedToNames still will be needed, because the DatabaseField's
    // name to be used as a parameter for StoredProcedureCall is extracted from
    // storedProcedureDefinition - and there it is always the same as in database.
    @SuppressWarnings({"unchecked"})
    protected List<DatabaseField> capitalize(List<DatabaseField> fields, Map<String, String> namesCapitalizedToNames) {
        // Can't change names of descriptor's fields, create a new Vector.
        List<DatabaseField> newFields = null;
        for (int i = 0; i < fields.size(); i++) {
            DatabaseField field = fields.get(i);
            String fieldNameUpper = field.getName().toUpperCase();
            String tableNameUpper = field.getTableName().toUpperCase();
            if (!fieldNameUpper.equals(field.getName()) || !tableNameUpper.equals(field.getTableName())) {
                DatabaseField newField = new DatabaseField(fieldNameUpper, tableNameUpper);
                newField.setType(field.getType());
                if (newFields == null) {
                    newFields = (List<DatabaseField>) ((Vector<DatabaseField>)fields).clone();
                }
                newFields.set(i, newField);
                namesCapitalizedToNames.put(fieldNameUpper, field.getName());
            }
        }
        if (newFields == null) {
            return fields;
        } else {
            return newFields;
        }
    }

    protected boolean shouldCapitalizeNames() {
        return getSession().getPlatform().isOracle();
    }

    protected String shortClassName(String originalClassName, int numberOfPackagesToIncludeIntoShortName) {
        int nLastIndex = originalClassName.length() - 1;
        for (int i = 0; i <= numberOfPackagesToIncludeIntoShortName && nLastIndex > 0; i++) {
            nLastIndex = originalClassName.lastIndexOf('.', nLastIndex - 1);
        }
        if (nLastIndex < 0 || nLastIndex == originalClassName.length() - 1) {
            return originalClassName;
        } else {
            return originalClassName.substring(nLastIndex + 1);
        }
    }

    @Override
    protected StoredProcedureDefinition generateObjectStoredProcedure(DatabaseQuery query, List<DatabaseField> fields, String namePrefix) {
        String namePrefixToUse = namePrefix;
        String className = query.getDescriptor().getJavaClass().getSimpleName();
        if (useTableNames) {
            String tableName = query.getDescriptor().getTableName();
            if (!compareNames(className, tableName)) {
                namePrefixToUse = namePrefix + tableName + "_";
            }
        }
        return generateStoredProcedure(query, fields, getPrefix() + namePrefixToUse + className);
    }

    protected boolean compareNames(String name1, String name2) {
        if (shouldCapitalizeNames()) {
            return name1.equalsIgnoreCase(name2);
        } else {
            return name1.equals(name2);
        }
    }
}
