/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     
 ******************************************************************************/  
package org.eclipse.persistence.tools.schemaframework;

import java.util.*;
import java.io.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.databaseaccess.*;
import org.eclipse.persistence.queries.SQLCall;
import org.eclipse.persistence.exceptions.*;

/**
 * <p>
 * <b>Purpose</b>: Allow a generic way of creating tables on the different platforms.
 * <p>
 */
public class TableDefinition extends DatabaseObjectDefinition {
    protected Vector fields; //FieldDefinitions
    protected HashMap<String, ForeignKeyConstraint> foreignKeyMap; //key is the name of ForeignKeyConstraint
    protected Vector<UniqueKeyConstraint> uniqueKeys;
    protected String creationPrefix;
    protected String creationSuffix;
    private boolean createSQLFiles;

    public TableDefinition() {
        this.fields = new Vector();
        this.foreignKeyMap = new HashMap<String, ForeignKeyConstraint>();
        this.uniqueKeys = new Vector();
        this.creationPrefix = "CREATE TABLE ";
        this.creationSuffix = "";
    }

    /**
     * PUBLIC:
     * Add the field to the table, default sizes are used.
     * @param type is the Java class type corresponding to the database type.
     */
    public void addField(String fieldName, Class type) {
        this.addField(new FieldDefinition(fieldName, type));
    }

    /**
     * PUBLIC:
     * Add the field to the table.
     * @param type is the Java class type corresponding to the database type.
     */
    public void addField(String fieldName, Class type, int fieldSize) {
        this.addField(new FieldDefinition(fieldName, type, fieldSize));
    }

    /**
     * PUBLIC:
     * Add the field to the table.
     * @param type is the Java class type corresponding to the database type.
     */
    public void addField(String fieldName, Class type, int fieldSize, int fieldSubSize) {
        this.addField(new FieldDefinition(fieldName, type, fieldSize, fieldSubSize));
    }

    /**
     * PUBLIC:
     * Add the field to the type to a nested type.
     * @param typeName is the name of the nested type.
     */
    public void addField(String fieldName, String typeName) {
        this.addField(new FieldDefinition(fieldName, typeName));
    }

    /**
     * PUBLIC:
     * Add the field to the table.
     */
    public void addField(FieldDefinition field) {
        this.getFields().addElement(field);
    }

    /**
     * PUBLIC:
     * Add a foreign key constraint to the table.
     * If there is a same name foreign key constraint already, nothing will happen.
     */
    public void addForeignKeyConstraint(String name, String sourceField, String targetField, String targetTable) {
        ForeignKeyConstraint foreignKey = new ForeignKeyConstraint(name, sourceField, targetField, targetTable);
        addForeignKeyConstraint(foreignKey);
    }

    /**
     * PUBLIC:
     * Add a unique key constraint to the table.
     */
    public void addUniqueKeyConstraint(String name, String sourceField) {
        UniqueKeyConstraint uniqueKey = new UniqueKeyConstraint(name, sourceField);
        addUniqueKeyConstraint(uniqueKey);
    }
    
    /**
     * PUBLIC:
     * Add a unique key constraint to the table.
     */
    public void addUniqueKeyConstraint(String name, String[] sourceFields) {
        UniqueKeyConstraint uniqueKey = new UniqueKeyConstraint(name, sourceFields);
        addUniqueKeyConstraint(uniqueKey);
    }

    /**
     * PUBLIC:
     * Add a foreign key constraint to the table.
     * If there is a same name foreign key constraint already, nothing will happen.
     */
    public void addForeignKeyConstraint(ForeignKeyConstraint foreignKey) {
        if (!foreignKeyMap.containsKey(foreignKey.getName())) {
            foreignKeyMap.put(foreignKey.getName(), foreignKey);
        }
    }
    
    /**
     * PUBLIC:
     * Add a unique key constraint to the table.
     */
    public void addUniqueKeyConstraint(UniqueKeyConstraint uniqueKey) {
        getUniqueKeys().addElement(uniqueKey);
    }
    
    /**
     * PUBLIC:
     * Add the field to the table, default sizes are used.
     * Identity fields are used on Sybase for native sequencing,
     * The field must be of number type and cannot have a subsize.
     * @param type is the Java class type corresponding to the database type.
     */
    public void addIdentityField(String fieldName, Class type) {
        FieldDefinition fieldDef = new FieldDefinition(fieldName, type);
        fieldDef.setIsIdentity(true);
        fieldDef.setIsPrimaryKey(true);
        addField(fieldDef);
    }

    /**
     * PUBLIC:
     * Add the field to the table, default sizes are used.
     * Identity fields are used on Sybase for native sequencing,
     * The field must be of number type and cannot have a subsize.
     * @param type is the Java class type corresponding to the database type.
     */
    public void addIdentityField(String fieldName, Class type, int fieldSize) {
        FieldDefinition fieldDef = new FieldDefinition(fieldName, type, fieldSize);
        fieldDef.setIsIdentity(true);
        fieldDef.setIsPrimaryKey(true);
        addField(fieldDef);
    }

    /**
     * PUBLIC:
     * Add the field to the table, default sizes are used.
     * This field is set as part of the primary key.
     * @param type is the Java class type corresponding to the database type.
     */
    public void addPrimaryKeyField(String fieldName, Class type) {
        FieldDefinition fieldDef = new FieldDefinition(fieldName, type);
        fieldDef.setIsPrimaryKey(true);
        addField(fieldDef);
    }

    /**
     * PUBLIC:
     * Add the field to the table, default sizes are used.
     * This field is set as part of the primary key.
     * @param type is the Java class type corresponding to the database type.
     */
    public void addPrimaryKeyField(String fieldName, Class type, int fieldSize) {
        FieldDefinition fieldDef = new FieldDefinition(fieldName, type, fieldSize);
        fieldDef.setIsPrimaryKey(true);
        addField(fieldDef);
    }

    /**
     * INTERNAL:
     * Return the alter table statement to add the constraints.
     * This is done separately from the create because of dependencies.
     */
    public Writer buildConstraintCreationWriter(AbstractSession session, ForeignKeyConstraint foreignKey, Writer writer) throws ValidationException {
        try {
            writer.write("ALTER TABLE " + getFullName());
            writer.write(" ADD CONSTRAINT ");
            if (!session.getPlatform().shouldPrintConstraintNameAfter()) {
                writer.write(foreignKey.getName() + " ");
            }
            foreignKey.appendDBString(writer, session);
            if (session.getPlatform().shouldPrintConstraintNameAfter()) {
                writer.write(" CONSTRAINT " + foreignKey.getName());
            }
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
        return writer;
    }

    /**
     * INTERNAL:
     * Return the alter table statement to drop the constraints.
     * This is done separately to allow constraints to be dropped before the tables.
     */
    public Writer buildConstraintDeletionWriter(AbstractSession session, ForeignKeyConstraint foreignKey, Writer writer) throws ValidationException {
        try {
            writer.write("ALTER TABLE " + getFullName());
            writer.write(session.getPlatform().getConstraintDeletionString() + foreignKey.getName());
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
        return writer;
    }

    /**
     * INTERNAL:
     * Return the alter table statement to add the constraints.
     * This is done separately from the create because of dependencies.
     */
    public Writer buildUniqueConstraintCreationWriter(AbstractSession session, UniqueKeyConstraint uniqueKey, Writer writer) throws ValidationException {
        try {
            writer.write("ALTER TABLE " + getFullName());
            writer.write(" ADD CONSTRAINT ");
            if (!session.getPlatform().shouldPrintConstraintNameAfter()) {
                writer.write(uniqueKey.getName() + " ");
            }
            uniqueKey.appendDBString(writer, session);
            if (session.getPlatform().shouldPrintConstraintNameAfter()) {
                writer.write(" CONSTRAINT " + uniqueKey.getName());
            }
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
        return writer;
    }

    /**
     * INTERNAL:
     * Return the alter table statement to drop the constraints.
     * This is done separately to allow constraints to be dropped before the tables.
     */
    public Writer buildUniqueConstraintDeletionWriter(AbstractSession session, UniqueKeyConstraint uniqueKey, Writer writer) throws ValidationException {
        try {
            writer.write("ALTER TABLE " + getFullName());
            writer.write(session.getPlatform().getConstraintDeletionString() + uniqueKey.getName());
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
        return writer;
    }    

    /**
     * INTERNAL:
     * Return the beginning of the sql create statement - the part before the name.
     * Unless temp table is created should be "CREATE TABLE "
     */
    public String getCreationPrefix() {
        return creationPrefix;    
    }
    
    /**
     * INTERNAL:
     * Set the beginning of the sql create statement - the part before the name.
     * Use to create temp. table.
     */
    public void setCreationPrefix(String  creationPrefix) {
        this.creationPrefix = creationPrefix;
    }

    /**
     * INTERNAL:
     * Return the end of the sql create statement - the part after the field list.
     * Unless temp table is created should be empty.
     */
    public String getCreationSuffix() {
        return creationSuffix;
    }

    /**
     * INTERNAL:
     * Set the end of the sql create statement - the part after the field list.
     * Use to create temp table.
     */
    public void setCreationSuffix(String  creationSuffix) {
        this.creationSuffix = creationSuffix;
    }
    
    /**
     * INTERNAL:
     * Return the create table statement.
     */
    public Writer buildCreationWriter(AbstractSession session, Writer writer) throws ValidationException {
        try {
            writer.write(getCreationPrefix() + getFullName() + " (");
            for (Enumeration fieldsEnum = getFields().elements(); fieldsEnum.hasMoreElements();) {
                FieldDefinition field = (FieldDefinition)fieldsEnum.nextElement();
                field.appendDBString(writer, session, this);
                if (fieldsEnum.hasMoreElements()) {
                    writer.write(", ");
                }
            }
            Vector keyFields = getPrimaryKeyFieldNames();
            if ((!keyFields.isEmpty()) && session.getPlatform().supportsPrimaryKeyConstraint()) {
                writer.write(", ");
                if (session.getPlatform().requiresNamedPrimaryKeyConstraints()) {
                    writer.write("CONSTRAINT " + getFullName() + "_PK ");
                }
                writer.write("PRIMARY KEY (");
                for (Enumeration keyEnum = keyFields.elements(); keyEnum.hasMoreElements();) {
                    writer.write((String)keyEnum.nextElement());
                    if (keyEnum.hasMoreElements()) {
                        writer.write(", ");
                    }
                }
                writer.write(")");
            }
            writer.write(")");
            if(getCreationSuffix().length() > 0) {
                writer.write(getCreationSuffix());
            }
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
        return writer;
    }

    /**
     * INTERNAL:
     * Return the drop table statement.
     */
    public Writer buildDeletionWriter(AbstractSession session, Writer writer) throws ValidationException {
        try {
            writer.write("DROP TABLE " + getFullName());
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
        return writer;
    }

    /**
     * INTERNAL:
     * Build the foreign key constraints.
     */
    protected void buildFieldTypes(AbstractSession session) {        
        FieldDefinition field = null;

        // The ForeignKeyConstraint object is the newer way of doing things.
        // We support FieldDefinition.getForeignKeyFieldName() due to backwards compatibility
        // by converting it. To allow mixing both ways, we just add converted one to foreignKeys list.
        for (Enumeration enumtr = getFields().elements(); enumtr.hasMoreElements();) {
            field = (FieldDefinition)enumtr.nextElement();
            if (field.getForeignKeyFieldName() != null) {
                addForeignKeyConstraint(buildForeignKeyConstraint(field, session.getPlatform()));
            }
        }
    }

    /**
     * Build a foreign key constraint using FieldDefinition.getForeignKeyFieldName().
     */
    protected ForeignKeyConstraint buildForeignKeyConstraint(FieldDefinition field, DatabasePlatform platform) {
        Vector sourceFields = new Vector();
        Vector targetFields = new Vector();
        ForeignKeyConstraint fkConstraint = new ForeignKeyConstraint();
        DatabaseField tempTargetField = new DatabaseField(field.getForeignKeyFieldName());
        DatabaseField tempSourceField = new DatabaseField(field.getName());

        sourceFields.addElement(tempSourceField.getName());
        targetFields.addElement(tempTargetField.getName());

        fkConstraint.setSourceFields(sourceFields);
        fkConstraint.setTargetFields(targetFields);
        fkConstraint.setTargetTable(tempTargetField.getTable().getQualifiedNameDelimited());
        String tempName = buildForeignKeyConstraintName(this.getName(), tempSourceField.getName(), platform.getMaxForeignKeyNameSize());

        fkConstraint.setName(tempName);
        return fkConstraint;
    }

    /**
     * Build a foreign key constraint.
     */
    protected ForeignKeyConstraint buildForeignKeyConstraint(Vector fkFieldNames, Vector pkFieldNames, TableDefinition targetTable, DatabasePlatform platform) {
        assert fkFieldNames.size() > 0 && fkFieldNames.size() == pkFieldNames.size();
        
        ForeignKeyConstraint fkConstraint = new ForeignKeyConstraint();
        for(int i=0; i<fkFieldNames.size(); i++) {
            fkConstraint.getSourceFields().add(fkFieldNames.get(i));
            fkConstraint.getTargetFields().add(pkFieldNames.get(i));
        }

        fkConstraint.setTargetTable(targetTable.getFullName());
        String fkFieldName = (String)fkFieldNames.get(0);
        String name = buildForeignKeyConstraintName(this.getName(), fkFieldName, platform.getMaxForeignKeyNameSize());

        fkConstraint.setName(name);
        return fkConstraint;
    }

    /**
     * Return foreign key constraint name built from the table and field name with the specified maximum length. To
     * make the name short enough we
     * 1. Drop the "FK_" prefix.
     * 2. Drop the underscore characters if any.
     * 3. Drop the vowels from the table and field name.
     * 4. Truncate the table name to zero length if necessary.
     */
    protected String buildForeignKeyConstraintName(String tableName, String fieldName, int maximumNameLength) {
        String adjustedTableName = tableName;
        if(adjustedTableName.indexOf(' ') != -1 || adjustedTableName.indexOf('\"') != -1 || adjustedTableName.indexOf('`') != -1) {
            //if table name has spaces and/or is quoted, remove this from the constraint name.
            StringBuffer buff = new StringBuffer();
            for(int i = 0; i < tableName.length(); i++) {
                char c = tableName.charAt(i);
                if(c != ' ' && c != '\"' && c != '`') {
                    buff.append(c);
                }
            }
            adjustedTableName = buff.toString();
        }
        StringBuffer buff = new StringBuffer();
        for(int i = 0; i < fieldName.length(); i++) {
            char c = fieldName.charAt(i);
            if(c != ' ' && c != '\"' && c != '`') {
                buff.append(c);
            }
        }
        String adjustedFieldName = buff.toString();
        String foreignKeyName = "FK_" + adjustedTableName + "_" + adjustedFieldName;
        if (foreignKeyName.length() > maximumNameLength) {
            // First Remove the "FK_" prefix.
            foreignKeyName = adjustedTableName + "_" + adjustedFieldName;
            if (foreignKeyName.length() > maximumNameLength) {
                // Still too long: remove the underscore characters
                foreignKeyName = Helper.removeAllButAlphaNumericToFit(adjustedTableName + adjustedFieldName, maximumNameLength);
                if (foreignKeyName.length() > maximumNameLength) {
                    // Still too long: remove vowels from the table name and field name.
                    String onlyAlphaNumericTableName = Helper.removeAllButAlphaNumericToFit(adjustedTableName, 0);
                    String onlyAlphaNumericFieldName = Helper.removeAllButAlphaNumericToFit(adjustedFieldName, 0);
                    foreignKeyName = Helper.shortenStringsByRemovingVowelsToFit(onlyAlphaNumericTableName, onlyAlphaNumericFieldName, maximumNameLength);
                    if (foreignKeyName.length() > maximumNameLength) {
                        // Still too long: remove vowels from the table name and field name and truncate the table name.
                        String shortenedFieldName = Helper.removeVowels(onlyAlphaNumericFieldName);
                        String shortenedTableName = Helper.removeVowels(onlyAlphaNumericTableName);
                        if (shortenedFieldName.length() >= maximumNameLength) {
                            foreignKeyName = Helper.truncate(shortenedFieldName, maximumNameLength);
                        } else {
                            foreignKeyName = Helper.truncate(shortenedTableName, maximumNameLength - shortenedFieldName.length()) + shortenedFieldName;
                        }
                    }
                }
            }
        }
        return foreignKeyName;
    }

    protected UniqueKeyConstraint buildUniqueKeyConstraint(List<String> fieldNames, int serialNumber, DatabasePlatform platform) {
        assert fieldNames.size() > 0;
        
        UniqueKeyConstraint unqConstraint = new UniqueKeyConstraint();
        for(String fieldName : fieldNames) {
            unqConstraint.addSourceField(fieldName);
        }
        String name = buildUniqueKeyConstraintName(this.getName(), serialNumber, platform.getMaxUniqueKeyNameSize());
        unqConstraint.setName(name);
        return unqConstraint;
    }

    /**
     * Return unique key constraint name built from the table name and sequence 
     * number with the specified maximum length. To make the name short enough we
     * 1. Drop the "UNQ_" prefix.
     * 2. Drop the underscore characters if any.
     * 3. Drop the vowels from the table name.
     * 4. Truncate the table name to zero length if necessary.
     */
    protected String buildUniqueKeyConstraintName(String tableName, int serialNumber, int maximumNameLength) {
        String uniqueKeyName = "UNQ_" + tableName + "_" + serialNumber;
        if (uniqueKeyName.length() > maximumNameLength) {
            // First Remove the "UNQ_" prefix.
            uniqueKeyName = tableName + serialNumber;
            if (uniqueKeyName.length() > maximumNameLength) {
                // Still too long: remove the underscore characters
                uniqueKeyName = Helper.removeAllButAlphaNumericToFit(tableName + serialNumber, maximumNameLength);
                if (uniqueKeyName.length() > maximumNameLength) {
                    // Still too long: remove vowels from the table name
                    String onlyAlphaNumericTableName = Helper.removeAllButAlphaNumericToFit(tableName, 0);
                    String serialName = String.valueOf(serialNumber);
                    uniqueKeyName = Helper.shortenStringsByRemovingVowelsToFit(onlyAlphaNumericTableName, serialName, maximumNameLength);
                    if (uniqueKeyName.length() > maximumNameLength) {
                        // Still too long: remove vowels from the table name and truncate the table name.
                        String shortenedTableName = Helper.removeVowels(onlyAlphaNumericTableName);
                        uniqueKeyName = Helper.truncate(shortenedTableName, maximumNameLength - serialName.length()) + serialName;
                    }
                }
            }
        }
        return uniqueKeyName;
    }

    /**
     * PUBLIC:
     * Performs a deep copy of this table definition.
     */
    public Object clone() {
        TableDefinition clone = (TableDefinition)super.clone();
        if (fields != null) {
            clone.setFields(new Vector(fields.size()));
            for (Enumeration enumtr = getFields().elements(); enumtr.hasMoreElements();) {
                FieldDefinition fieldDef = (FieldDefinition)enumtr.nextElement();
                clone.addField((FieldDefinition)fieldDef.clone());
            }
        }
        if (foreignKeyMap != null) {
            clone.setForeignKeyMap((HashMap) foreignKeyMap.clone());
        }
        if (uniqueKeys != null) {
            clone.setUniqueKeys((Vector)uniqueKeys.clone());
        }        
        return clone;
    }

    /**
     * INTERNAL:
     * Execute the SQL alter table constraint creation string.
     */
    public void createConstraints(AbstractSession session, Writer schemaWriter) throws EclipseLinkException {       
        createUniqueConstraints(session, schemaWriter);
        createForeignConstraints(session, schemaWriter);
    }

    void createUniqueConstraints(final AbstractSession session, final Writer schemaWriter) throws ValidationException {
        if (schemaWriter == null) {
            createUniqueConstraintsOnDatabase(session);
            return;
        }
        
        for (Enumeration uniqueKeysEnum = getUniqueKeys().elements();
                 uniqueKeysEnum.hasMoreElements();) {              
            UniqueKeyConstraint uniqueKey = (UniqueKeyConstraint)uniqueKeysEnum.nextElement();
            buildUniqueConstraintCreationWriter(session, uniqueKey, schemaWriter).toString();
            try {
                if (createSQLFiles) {
                    schemaWriter.write(session.getPlatform().getStoredProcedureTerminationToken());
                }
                schemaWriter.write("\n");
            } catch (IOException exception) {
                throw ValidationException.fileError(exception);
            }
        }            
    }

    void createForeignConstraints(final AbstractSession session, final Writer schemaWriter) throws ValidationException {
        if (schemaWriter == null) {
            createForeignConstraintsOnDatabase(session);
            return;
        }
        
        for (ForeignKeyConstraint foreignKey : getForeignKeyMap().values()) {
            buildConstraintCreationWriter(session, foreignKey, schemaWriter).toString();
            try {
                if (createSQLFiles) {
                    schemaWriter.write(session.getPlatform().getStoredProcedureTerminationToken());
                }
                schemaWriter.write("\n");
            } catch (IOException exception) {
                throw ValidationException.fileError(exception);
            }
        }
    }

    /**
     * INTERNAL:
     * Execute the SQL alter table constraint creation string.
     */
    public void createConstraintsOnDatabase(AbstractSession session) throws EclipseLinkException {
        createUniqueConstraintsOnDatabase(session);       
        createForeignConstraintsOnDatabase(session);
    }

    void createUniqueConstraintsOnDatabase(final AbstractSession session) throws ValidationException, DatabaseException {       
        if ((!session.getPlatform().supportsUniqueKeyConstraints()) || getUniqueKeys().isEmpty()) {
            return;
        }

        for (Enumeration uniqueKeysEnum = getUniqueKeys().elements();
                 uniqueKeysEnum.hasMoreElements();) {            
            UniqueKeyConstraint uniqueKey = (UniqueKeyConstraint)uniqueKeysEnum.nextElement();
            session.executeNonSelectingCall(new org.eclipse.persistence.queries.SQLCall(buildUniqueConstraintCreationWriter(session, uniqueKey, new StringWriter()).toString()));
        } 
    }

    void createForeignConstraintsOnDatabase(final AbstractSession session) throws ValidationException, DatabaseException {        
        if ((!session.getPlatform().supportsForeignKeyConstraints()) || getForeignKeyMap().isEmpty()) {
            return;
        }

        for (ForeignKeyConstraint foreignKey : getForeignKeyMap().values()) {
            session.executeNonSelectingCall(new SQLCall(buildConstraintCreationWriter(session, foreignKey, new StringWriter()).toString()));
        }
    }

    /**
     * INTERNAL:
     * Return the delete SQL string.
     */
    public String deletionStringFor(DatabaseAccessor accessor) {
        return "DROP TABLE " + this.getName();
    }

    /**
     * INTERNAL:
     * Execute the SQL alter table constraint creation string.
     */
    public void dropConstraints(AbstractSession session, Writer schemaWriter) throws EclipseLinkException {
        if (schemaWriter == null) {
            this.dropConstraintsOnDatabase(session);
        } else {
            for (ForeignKeyConstraint foreignKey : getForeignKeyMap().values()) {
                buildConstraintDeletionWriter(session, foreignKey, schemaWriter).toString();
                try {
                    if (createSQLFiles) {
                        schemaWriter.write(session.getPlatform().getStoredProcedureTerminationToken());
                    }
                    schemaWriter.write("\n");
                } catch (IOException exception) {
                    throw ValidationException.fileError(exception);
                }
            }
                     
            for (Enumeration uniqueKeysEnum = getUniqueKeys().elements();
                     uniqueKeysEnum.hasMoreElements();) {        
                UniqueKeyConstraint uniqueKey = (UniqueKeyConstraint)uniqueKeysEnum.nextElement();
                buildUniqueConstraintDeletionWriter(session, uniqueKey, schemaWriter).toString();
                try {
                    if (createSQLFiles) {                    
                        schemaWriter.write(session.getPlatform().getStoredProcedureTerminationToken());
                    }
                    schemaWriter.write("\n");
                } catch (IOException exception) {
                    throw ValidationException.fileError(exception);
                }
            }
        }
    }

    /**
     * INTERNAL:
     * Execute the SQL alter table constraint creation string. Exceptions are caught and masked so that all
     * the foreign keys are dropped (even if they don't exist).
     */
    public void dropConstraintsOnDatabase(AbstractSession session) throws EclipseLinkException {
        dropForeignConstraintsOnDatabase(session);
        dropUniqueConstraintsOnDatabase(session);        
    }

    private void dropUniqueConstraintsOnDatabase(final AbstractSession session) throws ValidationException {        
        if ((!session.getPlatform().supportsUniqueKeyConstraints()) || getUniqueKeys().isEmpty()) {
            return;
        }
        
        for (Enumeration uniqueKeysEnum = getUniqueKeys().elements();
                 uniqueKeysEnum.hasMoreElements();) {
            UniqueKeyConstraint uniqueKey = (UniqueKeyConstraint)uniqueKeysEnum.nextElement();
            try {
                session.executeNonSelectingCall(new org.eclipse.persistence.queries.SQLCall(buildUniqueConstraintDeletionWriter(session, uniqueKey, new StringWriter()).toString()));
            } catch (DatabaseException ex) {/* ignore */
            }
        }        
    }

    private void dropForeignConstraintsOnDatabase(final AbstractSession session) throws ValidationException {        
        if ((!session.getPlatform().supportsForeignKeyConstraints()) || getForeignKeyMap().isEmpty()) {
            return;
        }

        for (ForeignKeyConstraint foreignKey : getForeignKeyMap().values()) {
            try {
                session.executeNonSelectingCall(new SQLCall(buildConstraintDeletionWriter(session, foreignKey, new StringWriter()).toString()));
            } catch (DatabaseException ex) {/* ignore */
            }
        }
    }


    /**
     * INTERNAL:
     */
    HashMap<String, ForeignKeyConstraint> getForeignKeyMap() {
        return foreignKeyMap;
    }

    /**
     * INTERNAL:
     */
    void setForeignKeyMap(HashMap<String, ForeignKeyConstraint> foreignKeyMap) {
        this.foreignKeyMap = foreignKeyMap;
    }

    /**
     * PUBLIC:
     */
    public Vector getFields() {
        return fields;
    }

    /**
     * PUBLIC:
     * Returns the ForeignKeyConstraint list.
     */
    public Vector<ForeignKeyConstraint> getForeignKeys() {
        return new Vector<ForeignKeyConstraint>(foreignKeyMap.values());
    }

    /**
     * PUBLIC:
     */
    public Vector<UniqueKeyConstraint> getUniqueKeys() {
        return uniqueKeys;
    }
    
    /**
     * PUBLIC:
     */
    public Vector getPrimaryKeyFieldNames() {
        Vector keyNames = new Vector();

        for (Enumeration fieldEnum = getFields().elements(); fieldEnum.hasMoreElements();) {
            FieldDefinition field = (FieldDefinition)fieldEnum.nextElement();
            if (field.isPrimaryKey()) {
                keyNames.addElement(field.getName());
            }
        }
        return keyNames;
    }

    /**
     * PUBLIC:
     */
    public void setFields(Vector fields) {
        this.fields = fields;
    }

    /**
     * PUBLIC:
     * Set the ForeignKeyConstraint list.
     * If the list contains the same name foreign key constraints, only the first one of that name will be added.
     */
    public void setForeignKeys(Vector<ForeignKeyConstraint> foreignKeys) {
        foreignKeyMap.clear();
        if (foreignKeys != null) {
            for(ForeignKeyConstraint foreignKey : foreignKeys) {
                foreignKeyMap.put(foreignKey .getName(), foreignKey);
            }
        }
    }
    
    /**
     * PUBLIC:
     */
    public void setUniqueKeys(Vector<UniqueKeyConstraint> uniqueKeys) {
        this.uniqueKeys = uniqueKeys;
    }
    
    /**
     * PUBLIC:
     */
    public void setCreateSQLFiles(boolean genFlag) {
        this.createSQLFiles = genFlag;
    }    
}
