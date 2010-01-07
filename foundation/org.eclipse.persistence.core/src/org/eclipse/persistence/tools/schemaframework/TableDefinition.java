/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     Dies Koper - avoid generating constraints on platforms that do not support constraint generation
 *     Dies Koper - add support for creating indices on tables
 *******************************************************************************/  
package org.eclipse.persistence.tools.schemaframework;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.databaseaccess.DatabaseAccessor;
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.SQLCall;

/**
 * <p>
 * <b>Purpose</b>: Allow a generic way of creating tables on the different platforms.
 * <p>
 */
public class TableDefinition extends DatabaseObjectDefinition {
    protected Vector<FieldDefinition> fields; //FieldDefinitions
    protected HashMap<String, ForeignKeyConstraint> foreignKeyMap; //key is the name of ForeignKeyConstraint
    protected Vector<UniqueKeyConstraint> uniqueKeys;
    protected String creationPrefix;
    protected String creationSuffix;
    private boolean createSQLFiles;

    public TableDefinition() {
        this.fields = new Vector<FieldDefinition>();
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
     * Return the index creation statement.
     */
    public Writer buildIndexCreationWriter(AbstractSession session, String key,
            List<String> columnNames, Writer writer) throws ValidationException {
        try {
            String indexName = buildIndexName(getName(), key, session
                    .getPlatform().getMaxIndexNameSize(), session.getPlatform());
            writer.write(session.getPlatform().buildCreateIndex(getFullName(),
                    indexName, columnNames.toArray(new String[0])));

        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
        return writer;
    }

    /**
     * INTERNAL:
     * Return the index drop statement.
     */
    public Writer buildIndexDeletionWriter(AbstractSession session, String key, Writer writer) throws ValidationException {
        try {
            String indexName = buildIndexName(getName(), key,
                    session.getPlatform().getMaxIndexNameSize(), session.getPlatform());
            writer.write(session.getPlatform().buildDropIndex(getFullName(),
                    indexName));
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
            if (session.getPlatform().requiresUniqueConstraintCreationOnTableCreate()) {
                for (UniqueKeyConstraint constraint : getUniqueKeys()) {
                    writer.write(", ");
                    constraint.appendDBString(writer, session);
                }
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
        fkConstraint.setTargetTable(tempTargetField.getTable().getQualifiedNameDelimited(platform));
        String tempName = buildForeignKeyConstraintName(this.getName(), tempSourceField.getName(), platform.getMaxForeignKeyNameSize(), platform);

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
        String name = buildForeignKeyConstraintName(this.getName(), fkFieldName, platform.getMaxForeignKeyNameSize(), platform);

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
    protected String buildForeignKeyConstraintName(String tableName, String fieldName, int maximumNameLength, DatabasePlatform platform) {
        String startDelimiter = "";
        String endDelimiter = "";
        boolean useDelimiters = !platform.getStartDelimiter().equals("") && (tableName.startsWith(platform.getStartDelimiter()) || fieldName.startsWith(platform.getStartDelimiter()));
        // we will only delimit our generated constraints if either of the names that composes them is already delimited
        if (useDelimiters){
            startDelimiter = platform.getStartDelimiter();
            endDelimiter = platform.getEndDelimiter();
        }
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
        String foreignKeyName = startDelimiter + "FK_" + adjustedTableName + "_" + adjustedFieldName + endDelimiter;
        if (foreignKeyName.length() > maximumNameLength) {
            // First Remove the "FK_" prefix.
            foreignKeyName = startDelimiter + adjustedTableName + "_" + adjustedFieldName + endDelimiter;
            if (foreignKeyName.length() > maximumNameLength) {
                // Still too long: remove the underscore characters
                foreignKeyName = startDelimiter + Helper.removeAllButAlphaNumericToFit(adjustedTableName + adjustedFieldName, maximumNameLength) + endDelimiter;
                if (foreignKeyName.length() > maximumNameLength) {
                    // Still too long: remove vowels from the table name and field name.
                    String onlyAlphaNumericTableName = Helper.removeAllButAlphaNumericToFit(adjustedTableName, 0);
                    String onlyAlphaNumericFieldName = Helper.removeAllButAlphaNumericToFit(adjustedFieldName, 0);
                    foreignKeyName = startDelimiter + Helper.shortenStringsByRemovingVowelsToFit(onlyAlphaNumericTableName, onlyAlphaNumericFieldName, maximumNameLength) + endDelimiter;
                    if (foreignKeyName.length() > maximumNameLength) {
                        // Still too long: remove vowels from the table name and field name and truncate the table name.
                        String shortenedFieldName = Helper.removeVowels(onlyAlphaNumericFieldName);
                        String shortenedTableName = Helper.removeVowels(onlyAlphaNumericTableName);
                        int delimiterLength = startDelimiter.length() + endDelimiter.length();
                        if (shortenedFieldName.length() + delimiterLength >= maximumNameLength) {
                            foreignKeyName = startDelimiter + Helper.truncate(shortenedFieldName, maximumNameLength - delimiterLength) + endDelimiter;
                        } else {
                            foreignKeyName = startDelimiter + Helper.truncate(shortenedTableName, maximumNameLength - shortenedFieldName.length() - delimiterLength) + shortenedFieldName + endDelimiter;
                        }
                    }
                }
            }
        }
        return foreignKeyName;
    }

    protected UniqueKeyConstraint buildUniqueKeyConstraint(String name, List<String> fieldNames, int serialNumber, DatabasePlatform platform) {
        assert fieldNames.size() > 0;
        
        UniqueKeyConstraint unqConstraint = new UniqueKeyConstraint();
        
        for (String fieldName : fieldNames) {
            unqConstraint.addSourceField(fieldName);
        }
        
        // If the name was not provided, default one, otherwise use the name provided.
        if (name == null || name.equals("")) {
            unqConstraint.setName(buildUniqueKeyConstraintName(getName(), serialNumber, platform.getMaxUniqueKeyNameSize()));
        } else {
            // Hack if off if it exceeds the max size.
            if (name.length() > platform.getMaxUniqueKeyNameSize()) {
                unqConstraint.setName(name.substring(0, platform.getMaxUniqueKeyNameSize() - 1));
            } else {
                unqConstraint.setName(name);
            }
        }
        
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
     * Return key constraint name built from the table and key name with the
     * specified maximum length. To make the name short enough we:
     * 
     * <pre>
     * 1. Drop the &quot;IX_&quot; prefix.
     * 2. Drop the underscore characters if any.
     * 3. Drop the vowels from the table and key name.
     * 4. Truncate the table name to zero length if necessary.
     * </pre>
     */
    protected String buildIndexName(String tableName, String key, int maximumNameLength, DatabasePlatform platform) {
        String startDelimiter = "";
        String endDelimiter = "";
        boolean useDelimiters = !platform.getStartDelimiter().equals("") && (tableName.startsWith(platform.getStartDelimiter()) || key.startsWith(platform.getStartDelimiter()));
        // we will only delimit our generated indices if either of the names that composes them is already delimited
        if (useDelimiters){
            startDelimiter = platform.getStartDelimiter();
            endDelimiter = platform.getEndDelimiter();
        }
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
        for(int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            if(c != ' ' && c != '\"' && c != '`') {
                buff.append(c);
            }
        }
        String adjustedFieldName = buff.toString();
        String indexName = startDelimiter + "IX_" + adjustedTableName + "_" + adjustedFieldName + endDelimiter;
        if (indexName.length() > maximumNameLength) {
            // First Remove the "IX_" prefix.
            indexName = startDelimiter + adjustedTableName + "_" + adjustedFieldName + endDelimiter;
            if (indexName.length() > maximumNameLength) {
                // Still too long: remove the underscore characters
                indexName = startDelimiter + Helper.removeAllButAlphaNumericToFit(adjustedTableName + adjustedFieldName, maximumNameLength) + endDelimiter;
                if (indexName.length() > maximumNameLength) {
                    // Still too long: remove vowels from the table name and field name.
                    String onlyAlphaNumericTableName = Helper.removeAllButAlphaNumericToFit(adjustedTableName, 0);
                    String onlyAlphaNumericFieldName = Helper.removeAllButAlphaNumericToFit(adjustedFieldName, 0);
                    indexName = startDelimiter + Helper.shortenStringsByRemovingVowelsToFit(onlyAlphaNumericTableName, onlyAlphaNumericFieldName, maximumNameLength) + endDelimiter;
                    if (indexName.length() > maximumNameLength) {
                        // Still too long: remove vowels from the table name and field name and truncate the table name.
                        String shortenedFieldName = Helper.removeVowels(onlyAlphaNumericFieldName);
                        String shortenedTableName = Helper.removeVowels(onlyAlphaNumericTableName);
                        int delimiterLength = startDelimiter.length() + endDelimiter.length();
                        if (shortenedFieldName.length() + delimiterLength >= maximumNameLength) {
                            indexName = startDelimiter + Helper.truncate(shortenedFieldName, maximumNameLength - delimiterLength) + endDelimiter;
                        } else {
                            indexName = startDelimiter + Helper.truncate(shortenedTableName, maximumNameLength - shortenedFieldName.length() - delimiterLength) + shortenedFieldName + endDelimiter;
                        }
                    }
                }
            }
        }
        return indexName;
    }

    /**
     * PUBLIC:
     * Performs a deep copy of this table definition.
     */
    public Object clone() {
        TableDefinition clone = (TableDefinition)super.clone();
        if (fields != null) {
            clone.setFields(new Vector<FieldDefinition>(fields.size()));
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

        if ((!session.getPlatform().supportsUniqueKeyConstraints())
                || getUniqueKeys().isEmpty()
                || session.getPlatform().requiresUniqueConstraintCreationOnTableCreate()) {
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

        if (session.getPlatform().supportsForeignKeyConstraints()) {
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
        if ((!session.getPlatform().supportsUniqueKeyConstraints())
                || getUniqueKeys().isEmpty()
                || session.getPlatform().requiresUniqueConstraintCreationOnTableCreate()) {
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
     * INTERNAL:<br/>
     * Write the SQL create index string to create index on primary key if
     * passed a writer, else delegate to a method that executes the string on
     * the database.
     * 
     * @see #createIndexOnPrimaryKeyOnDatabase(AbstractSession)
     * @throws ValidationException
     *             wraps any IOException from the writer
     */
    public void createIndexOnPrimaryKey(AbstractSession session,
            Writer schemaWriter) {
        if (schemaWriter == null) {
            createIndexOnPrimaryKeyOnDatabase(session);
            return;
        }
        if (session.getPlatform().shouldCreateIndicesForPrimaryKeys()) {

            List<String> primKeyList = getPrimaryKeyFieldNames();
            if (!primKeyList.isEmpty()) {

                buildIndexCreationWriter(session, primKeyList.get(0),
                        primKeyList, schemaWriter);
                try {
                    if (createSQLFiles) {
                        schemaWriter.write(session.getPlatform()
                                .getStoredProcedureTerminationToken());
                    }
                    schemaWriter.write("\n");
                } catch (IOException exception) {
                    throw ValidationException.fileError(exception);
                }
            }
        }
    }

    /**
     * INTERNAL:<br/>
     * Write the SQL create index string if passed a writer, else delegate to a
     * method that executes the string on the database.
     * 
     * @see #createIndicesOnUniqueKeysOnDatabase(AbstractSession)
     * @throws ValidationException
     *             wraps any IOException from the writer
     */
    public void createIndicesOnUniqueKeys(AbstractSession session,
            Writer schemaWriter) throws EclipseLinkException {
        if (schemaWriter == null) {
            createIndicesOnUniqueKeysOnDatabase(session);
            return;
        }

        try {
            // indices for columns in unique key constraint declarations
            for (UniqueKeyConstraint uniqueKey : getUniqueKeys()) {
                buildIndexCreationWriter(session, uniqueKey.getName(),
                        uniqueKey.getSourceFields(), schemaWriter);
                if (createSQLFiles) {
                    schemaWriter.write(session.getPlatform()
                            .getStoredProcedureTerminationToken());
                }
                schemaWriter.write("\n");
            }

            // indices for columns with unique=true declarations
            for (FieldDefinition field : getFields()) {
                if (field.isUnique()) {
                    List<String> columnAsList = new ArrayList<String>();
                    columnAsList.add(field.getName());
                    buildIndexCreationWriter(session, field.getName(),
                            columnAsList, new StringWriter());
                    if (createSQLFiles) {
                        schemaWriter.write(session.getPlatform()
                                .getStoredProcedureTerminationToken());
                    }
                    schemaWriter.write("\n");
                }
            }
        } catch (IOException exception) {
            throw ValidationException.fileError(exception);
        }
    }

    /**
     * INTERNAL:<br/>
     * Execute the SQL create index statement to create index on the primary
     * key.
     */
    public void createIndexOnPrimaryKeyOnDatabase(AbstractSession session) {
        if (session.getPlatform().shouldCreateIndicesForPrimaryKeys()) {

            List<String> primKeyList = getPrimaryKeyFieldNames();
            if (!primKeyList.isEmpty()) {
                session.executeNonSelectingCall(new org.eclipse.persistence.queries.SQLCall(
                        buildIndexCreationWriter(session, primKeyList.get(0),
                                primKeyList, new StringWriter())
                                        .toString()));
            }
        }
    }

    /**
     * INTERNAL:<br/>
     * Execute the SQL create index statement to create index on the unique
     * keys.
     */
    public void createIndicesOnUniqueKeysOnDatabase(AbstractSession session) {
        if (!session.getPlatform().shouldCreateIndicesOnUniqueKeys()) {
            return;
        }

        // indices for columns in unique key constraint declarations
        for (UniqueKeyConstraint uniqueKey : getUniqueKeys()) {
            session
                    .executeNonSelectingCall(new org.eclipse.persistence.queries.SQLCall(
                            buildIndexCreationWriter(session,
                                    uniqueKey.getName(),
                                    uniqueKey.getSourceFields(),
                                    new StringWriter()).toString()));
        }

        // indices for columns with unique=true (or equivalent)
        for (FieldDefinition field : getFields()) {
            if (field.isUnique()) {
                List<String> columnAsList = new ArrayList<String>();
                columnAsList.add(field.getName());
                session
                        .executeNonSelectingCall(new org.eclipse.persistence.queries.SQLCall(
                                buildIndexCreationWriter(session,
                                        field.getName(), columnAsList,
                                        new StringWriter()).toString()));
            }
        }
    }

    /**
     * INTERNAL:
     * Execute the DDL to create this table.
     */
    public void createOnDatabase(AbstractSession session) throws EclipseLinkException {
        super.createOnDatabase(session);
        createIndexOnPrimaryKeyOnDatabase(session);
        createIndicesOnUniqueKeysOnDatabase(session);
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
            if (session.getPlatform().supportsForeignKeyConstraints()){
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
            }
            if (session.getPlatform().supportsUniqueKeyConstraints()
                    && (!session.getPlatform().requiresUniqueConstraintCreationOnTableCreate())) {
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
        if ((!session.getPlatform().supportsUniqueKeyConstraints())
                || getUniqueKeys().isEmpty()
                || session.getPlatform().requiresUniqueConstraintCreationOnTableCreate()) {
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
     * Execute the DDL to drop the table.
     */
    public void dropFromDatabase(AbstractSession session) throws EclipseLinkException {
        // first drop indices on table's primary and unique keys (if required)
        dropIndicesOnUniqueKeysOnDatabase(session);
        dropIndexOnPrimaryKeyOnDatabase(session);
        super.dropFromDatabase(session);
    }
    
    /**
     * INTERNAL:<br/>
     * Write the SQL drop index string to drop index on PK if passed a writer,
     * else delegate to a method that executes the string on the database.
     * 
     * @see #dropIndexOnPrimaryKeyOnDatabase(AbstractSession)
     * @throws ValidationException
     *             wraps any IOException from the writer
     */
    public void dropIndexOnPrimaryKey(AbstractSession session,
            Writer schemaWriter) throws EclipseLinkException {
        if (schemaWriter == null) {
            this.dropIndexOnPrimaryKeyOnDatabase(session);
        } else {
            // the following drops indices on primary keys
            if (session.getPlatform().shouldCreateIndicesForPrimaryKeys()
                    && !getPrimaryKeyFieldNames().isEmpty()) {
                try {
                    buildIndexDeletionWriter(session, getPrimaryKeyFieldNames()
                            .firstElement(), schemaWriter);
                    if (createSQLFiles) {
                        schemaWriter.write(session.getPlatform()
                                .getStoredProcedureTerminationToken());
                    }
                    schemaWriter.write("\n");
                } catch (IOException exception) {
                    throw ValidationException.fileError(exception);
                }
            }
        } // end of if
    }

    /**
     * INTERNAL:<br/>
     * Execute the SQL drop index string to drop indices.<br/>
     * Exceptions are caught and masked so that all the indices are dropped
     * (even if they don't exist).
     */
    public void dropIndicesOnUniqueKeys(AbstractSession session,
            Writer schemaWriter) throws EclipseLinkException {
        if (schemaWriter == null) {
            this.dropIndicesOnUniqueKeysOnDatabase(session);
        } else {
            if (session.getPlatform().shouldCreateIndicesOnUniqueKeys()) {
                try {

                    // drop indices on unique keys declared using annotation
                    // "@UniqueConstraint" (or equivalent)
                    for (UniqueKeyConstraint key : getUniqueKeys()) {
                        buildIndexDeletionWriter(session, key.getName(),
                                schemaWriter);
                        if (createSQLFiles) {
                            schemaWriter.write(session.getPlatform()
                                    .getStoredProcedureTerminationToken());
                        }
                        schemaWriter.write("\n");
                    }

                    // drop indices for unique keys declared using attribute
                    // "unique=false" (or equivalent)
                    for (FieldDefinition field : getFields()) {
                        if (field.isUnique()) {
                            buildIndexDeletionWriter(session, field.getName(),
                                    schemaWriter);
                            if (createSQLFiles) {
                                schemaWriter.write(session.getPlatform()
                                        .getStoredProcedureTerminationToken());
                            }
                            schemaWriter.write("\n");
                        }
                    }
                } catch (IOException exception) {
                    throw ValidationException.fileError(exception);
                }
            } // end of if
        } // end of if-else
    }

    /**
     * INTERNAL:<br/>
     * Execute the SQL drop index string to drop the index on the PK.<br/>
     * Exceptions are caught and masked (even if index doesn't exist).
     */
    public void dropIndexOnPrimaryKeyOnDatabase(AbstractSession session)
            throws EclipseLinkException {
        if ((session.getPlatform().shouldCreateIndicesForPrimaryKeys())
                && !getPrimaryKeyFieldNames().isEmpty()) {
            try {
                session
                        .executeNonSelectingCall(new org.eclipse.persistence.queries.SQLCall(
                                buildIndexDeletionWriter(
                                        session,
                                        getPrimaryKeyFieldNames()
                                                .firstElement(),
                                        new StringWriter()).toString()));
            } catch (DatabaseException ex) {/* ignore */
            }
        }
    }

    /**
     * INTERNAL:<br/>
     * Execute the SQL drop index string on the database.<br/>
     * Exceptions are caught and masked so that all the indices are dropped
     * (even if they don't exist).
     */
    public void dropIndicesOnUniqueKeysOnDatabase(AbstractSession session)
            throws EclipseLinkException {
        // the following drops indices for unique keys declared using annotation
        // "@UniqueConstraint" (or equivalent)
        if (session.getPlatform().shouldCreateIndicesOnUniqueKeys()) {
            for (UniqueKeyConstraint key : getUniqueKeys()) {
                try {
                    session
                            .executeNonSelectingCall(new org.eclipse.persistence.queries.SQLCall(
                                    buildIndexDeletionWriter(session,
                                            key.getName(), new StringWriter())
                                            .toString()));
                } catch (DatabaseException ex) {/* ignore */
                }
            }

            // the following drops indices for unique keys declared using
            // attribute "unique=false" (or equivalent)
            for (FieldDefinition field : getFields()) {
                if (field.isUnique()) {
                    try {
                        session
                                .executeNonSelectingCall(new org.eclipse.persistence.queries.SQLCall(
                                        buildIndexDeletionWriter(session,
                                                field.getName(),
                                                new StringWriter()).toString()));
                    } catch (DatabaseException ex) {/* ignore */
                    }
                }
            } // end of for
        } // end of if
    }

    /**
     * INTERNAL:
     * Execute the DDL to drop the table.  Either directly from the database
     * of write out the statement to a file.
     */
    public void dropObject(AbstractSession session, Writer schemaWriter, boolean createSQLFiles) throws EclipseLinkException {
        // first drop indices on table's primary and unique keys (if required)
        setCreateSQLFiles(createSQLFiles);
        dropIndicesOnUniqueKeys(session, schemaWriter);
        dropIndexOnPrimaryKey(session, schemaWriter);
        super.dropObject(session, schemaWriter, createSQLFiles);
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
    public Vector<FieldDefinition> getFields() {
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
    public Vector<String> getPrimaryKeyFieldNames() {
        Vector<String> keyNames = new Vector<String>();

        for (Enumeration fieldEnum = getFields().elements(); fieldEnum.hasMoreElements();) {
            FieldDefinition field = (FieldDefinition)fieldEnum.nextElement();
            if (field.isPrimaryKey()) {
                keyNames.addElement(field.getName());
            }
        }
        return keyNames;
    }

    
    /**
     * Execute any statements required after the creation of the object
     * @param session
     * @param createSchemaWriter
     */
    public void postCreateObject(AbstractSession session, Writer createSchemaWriter, boolean createSQLFiles){
        // create indices on table's primary and unique keys (if required)
        setCreateSQLFiles(createSQLFiles);
        createIndexOnPrimaryKey(session, createSchemaWriter);
        createIndicesOnUniqueKeys(session, createSchemaWriter);
    }
    
    /**
     * PUBLIC:
     */
    public void setFields(Vector<FieldDefinition> fields) {
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
