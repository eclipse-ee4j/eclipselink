/*
 * Copyright (c) 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.tools.schemaframework;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

/**
 * This interface provides a contract for database platform-specific DDL generation behaviors.
 * Implementations of this interface can be used to customize or extend DDL printing logic
 * without modifying the core {@code org.eclipse.persistence.platform.database.DatabasePlatform} class.
 */
public interface DDLPlatform {

    FieldDefinition.DatabaseType TYPE_BLOB = new FieldDefinition.DatabaseType("BLOB");
    FieldDefinition.DatabaseType TYPE_CLOB = new FieldDefinition.DatabaseType("CLOB");
    FieldDefinition.DatabaseType TYPE_DATE = new FieldDefinition.DatabaseType("DATE");
    FieldDefinition.DatabaseType TYPE_TIME = new FieldDefinition.DatabaseType("TIME");
    FieldDefinition.DatabaseType TYPE_TIMESTAMP = new FieldDefinition.DatabaseType("TIMESTAMP");
    FieldDefinition.DatabaseType TYPE_CHAR = new FieldDefinition.DatabaseType("CHAR");
    FieldDefinition.DatabaseType TYPE_VARCHAR = new FieldDefinition.DatabaseType("VARCHAR");
    FieldDefinition.DatabaseType TYPE_NUMBER = new FieldDefinition.DatabaseType("NUMBER");
    FieldDefinition.DatabaseType TYPE_NUMERIC = new FieldDefinition.DatabaseType("NUMERIC");
    FieldDefinition.DatabaseType TYPE_BOOLEAN = TYPE_NUMBER.ofSize(1);
    FieldDefinition.DatabaseType TYPE_INTEGER = TYPE_NUMBER.ofSize(10);
    FieldDefinition.DatabaseType TYPE_LONG = TYPE_NUMBER.ofSize(19);
    FieldDefinition.DatabaseType TYPE_FLOAT = new FieldDefinition.DatabaseType("NUMBER", 12, 5, 19, 0, 19);
    FieldDefinition.DatabaseType TYPE_DOUBLE = new FieldDefinition.DatabaseType("NUMBER", 10, 5, 19, 0, 19);
    FieldDefinition.DatabaseType TYPE_SHORT = TYPE_NUMBER.ofSize(5);
    FieldDefinition.DatabaseType TYPE_BYTE = TYPE_NUMBER.ofSize(3);
    FieldDefinition.DatabaseType TYPE_BIG_DECIMAL = new FieldDefinition.DatabaseType("NUMBER", 19, 0, 19, 0, 19);
    FieldDefinition.DatabaseType TYPE_BIG_INTEGER = TYPE_NUMBER.ofSize(19);
    FieldDefinition.DatabaseType TYPE_BINARY = TYPE_NUMBER.ofSize(16);

    Map<Class<?>, FieldDefinition.DatabaseType> DB_TYPES = Map.ofEntries(
            Map.entry(Boolean.class, TYPE_BOOLEAN),
            Map.entry(Integer.class, TYPE_INTEGER),
            Map.entry(Long.class, TYPE_LONG),
            Map.entry(Float.class, TYPE_FLOAT),
            Map.entry(Double.class, TYPE_DOUBLE),
            Map.entry(Short.class, TYPE_SHORT),
            Map.entry(Byte.class, TYPE_BYTE),
            Map.entry(Number.class, TYPE_INTEGER),
            Map.entry(java.math.BigDecimal.class, TYPE_BIG_DECIMAL),
            Map.entry(java.math.BigInteger.class, TYPE_BIG_INTEGER),
            Map.entry(String.class, TYPE_VARCHAR),
            Map.entry(Character.class, TYPE_CHAR),
            Map.entry(Byte[].class, TYPE_BLOB),
            Map.entry(Character[].class, TYPE_CLOB),
            Map.entry(byte[].class, TYPE_BLOB),
            Map.entry(char[].class, TYPE_CLOB),
            Map.entry(java.sql.Blob.class, TYPE_BLOB),
            Map.entry(java.sql.Clob.class, TYPE_CLOB),
            Map.entry(java.sql.Date.class, TYPE_DATE),
            Map.entry(java.sql.Timestamp.class, TYPE_TIMESTAMP),
            Map.entry(java.sql.Time.class, TYPE_TIME),
            Map.entry(java.time.Instant.class, TYPE_TIMESTAMP),
            Map.entry(java.time.LocalDate.class, TYPE_DATE),
            Map.entry(java.time.LocalDateTime.class, TYPE_TIMESTAMP),
            Map.entry(java.time.LocalTime.class, TYPE_TIME),
            Map.entry(java.time.OffsetDateTime.class, TYPE_TIMESTAMP),
            Map.entry(java.time.OffsetTime.class, TYPE_TIME),
            Map.entry(java.time.Year.class, TYPE_INTEGER),
            Map.entry(java.util.Calendar.class, TYPE_TIMESTAMP),
            Map.entry(java.util.Date.class, TYPE_TIMESTAMP),
            Map.entry(java.util.UUID.class, TYPE_BINARY)
    );

    Map<String, Class<?>> CLASS_TYPES = Map.ofEntries(
            // Key the Map the other way for table creation.
            Map.entry("NUMBER", java.math.BigInteger.class),
            Map.entry("DECIMAL", java.math.BigDecimal.class),
            Map.entry("INTEGER", Integer.class),
            Map.entry("INT", Integer.class),
            Map.entry("NUMERIC", java.math.BigInteger.class),
            Map.entry("FLOAT(16)", Float.class),
            Map.entry("FLOAT(32)", Double.class),
            Map.entry("NUMBER(1) default 0", Boolean.class),
            Map.entry("SHORT", Short.class),
            Map.entry("BYTE", Byte.class),
            Map.entry("DOUBLE", Double.class),
            Map.entry("FLOAT", Float.class),
            Map.entry("SMALLINT", Short.class),

            Map.entry("BIT", Boolean.class),
            Map.entry("SMALLINT DEFAULT 0", Boolean.class),

            Map.entry("VARCHAR", String.class),
            Map.entry("CHAR", Character.class),
            Map.entry("LONGVARBINARY", Byte[].class),
            Map.entry("TEXT", Character[].class),
            Map.entry("LONGTEXT", Character[].class),
            Map.entry("BINARY", Byte[].class),
            Map.entry("MEMO", Character[].class),
            Map.entry("VARCHAR2", String.class),
            Map.entry("LONG RAW", Byte[].class),
            Map.entry("LONG", Character[].class),

            Map.entry("DATE", java.sql.Date.class),
            Map.entry("TIMESTAMP", java.sql.Timestamp.class),
            Map.entry("TIME", java.sql.Time.class),
            Map.entry("DATETIME", java.sql.Timestamp.class),

            Map.entry("BIGINT", java.math.BigInteger.class),
            Map.entry("DOUBLE PRECIS", Double.class),
            Map.entry("IMAGE", byte[].class),
            Map.entry("LONGVARCHAR", Character[].class),
            Map.entry("REAL", Float.class),
            Map.entry("TINYINT", Short.class),
        //    Map.entry("VARBINARY", Byte[].class),

            Map.entry("BLOB", byte[].class),
            Map.entry("CLOB", char[].class)
    );

    /**
     * Return the database type to class type mappings for the schema framework.
     */
    default Map<String, Class<?>> getJavaTypes() {
        return CLASS_TYPES;
    }

    /**
     * Return the class type to database type mappings for the schema framework.
     */
    default Map<Class<?>, FieldDefinition.DatabaseType> getDatabaseTypes() {
        return DB_TYPES;
    }


    /**
     * Returns the field type definition for the given Java type name.
     *
     * @param typeName the Java class representing the type
     * @return the field type definition
     */
    default FieldDefinition.DatabaseType getDatabaseType(String typeName) {
        Class<?> typeFromName = getJavaTypes().get(typeName);
        if (typeFromName == null) {
            // if unknown type name, use as it is
            return new FieldDefinition.DatabaseType(typeName);
        }
        return getDatabaseType(typeFromName);
    }

    /**
     * Returns the field type definition for the given Java type.
     *
     * @param type the Java class representing the type
     * @return the field type definition
     */
    default FieldDefinition.DatabaseType getDatabaseType(Class<?> type) {
        return getDatabaseTypes().get(type);
    }

    /**
     * INTERNAL:
     * Retrieve database platform specific field definition from database
     * specific platform handler for existing type or build a new one when type
     * is {@code null} and type for type name could not be found.
     * @param type     Field type (will be processed first when available).
     * @param name     Field type name (will be processed as backup option when
     *                 type class is not available).
     * @throws RuntimeException when provided type is not valid database type.
     */
    default FieldDefinition.DatabaseType getDatabaseType(Class<?> type, String name) {
        FieldDefinition.DatabaseType dbType = null;
        if (type != null) {
            dbType = getDatabaseType(type);
        } else if (name != null) {
            dbType = getDatabaseType(name);
        }
        return dbType;
    }

    /**
     * INTERNAL:
     * Override this method with the platform's CREATE INDEX statement.
     *
     * @param fullTableName qualified name of the table the index is to be created on
     * @param indexName     name of the index
     * @param qualifier     qualifier to construct qualified name of index if needed
     * @param isUnique      Indicates whether unique index is created
     * @param columnNames   one or more columns the index is created for
     */
    default String buildCreateIndex(String fullTableName, String indexName, String qualifier, boolean isUnique, String... columnNames) {
        StringBuilder queryString = new StringBuilder();
        if (isUnique) {
            queryString.append("CREATE UNIQUE INDEX ");
        } else {
            queryString.append("CREATE INDEX ");
        }
        if (!qualifier.isEmpty()) {
            queryString.append(qualifier).append(".");
        }
        queryString.append(indexName).append(" ON ").append(fullTableName).append(" (");
        queryString.append(columnNames[0]);
        for (int i = 1; i < columnNames.length; i++) {
            queryString.append(", ").append(columnNames[i]);
        }
        queryString.append(")");
        return queryString.toString();
    }

    /**
     * INTERNAL:
     * Override this method with the platform's DROP INDEX statement.
     *
     * @param fullTableName qualified name of the table the index is to be removed from
     * @param indexName     name of the index
     * @param qualifier     qualifier to construct qualified name of index if needed
     */
    default String buildDropIndex(String fullTableName, String indexName, String qualifier) {
        StringBuilder queryString = new StringBuilder();
        queryString.append("DROP INDEX ");
        if (!qualifier.isEmpty()) {
            queryString.append(qualifier).append(".");
        }
        queryString.append(indexName);
        if (requiresTableInIndexDropDDL()) {
            queryString.append(" ON ").append(fullTableName);
        }
        return queryString.toString();
    }

    /**
     * INTERNAL:
     * Returns sql used to alter sequence object's increment in the database.
     */
    default Writer buildSequenceObjectAlterIncrementWriter(Writer writer, String fullSeqName, int increment) throws IOException {
        writer.write("ALTER SEQUENCE ");
        writer.write(fullSeqName);
        writer.write(" INCREMENT BY " + increment);
        return writer;
    }

    /**
     * INTERNAL:
     * Returns sql used to create sequence object in the database.
     */
    default Writer buildSequenceObjectCreationWriter(Writer writer, String fullSeqName, int increment, int start) throws IOException {
        writer.write("CREATE SEQUENCE ");
        writer.write(fullSeqName);
        if (increment != 1) {
            writer.write(" INCREMENT BY " + increment);
        }
        writer.write(" START WITH " + start);
        return writer;
    }

    /**
     * INTERNAL:
     * Returns sql used to delete sequence object from the database.
     */
    default Writer buildSequenceObjectDeletionWriter(Writer writer, String fullSeqName) throws IOException {
        writer.write("DROP SEQUENCE ");
        writer.write(fullSeqName);
        return writer;
    }

    /**
     * Used for batch writing and sp defs.
     */
    default String getBatchBeginString() {
        return "";
    }

    /**
     * Used for batch writing and sp defs.
     */
    default String getBatchDelimiterString() {
        return "; ";
    }

    /**
     * Used for batch writing and sp defs.
     */
    default String getBatchEndString() {
        return "";
    }

    /**
     * Used for constraint deletion.
     */
    default String getConstraintDeletionString() {
        return " DROP CONSTRAINT ";
    }

    /**
     * Returns the platform-specific string for creating a view (e.g., "CREATE VIEW" or "CREATE OR REPLACE VIEW").
     *
     * @return the create view string
     */
    default String getCreateViewString() {
        return "CREATE VIEW ";
    }

    /**
     * Allows DROP TABLE to cascade dropping of any dependent constraints if the database supports this option.
     */
    default String getDropCascadeString() {
        return "";
    }

    /**
     * Used to allow platforms to define their own index prefixes
     */
    default String getIndexNamePrefix(boolean isUniqueSetOnField) {
        return "IX_";
    }

    /**
     * INTERNAL:
     * returns the maximum number of characters that can be used in a field
     * name on this platform.
     */
    default int getMaxFieldNameSize() {
        return 50;
    }

    /**
     * INTERNAL:
     * returns the maximum number of characters that can be used in an index
     * name on this platform.
     */
    default int getMaxIndexNameSize() {
        return getMaxFieldNameSize();
    }

    /**
     * Returns the maximum allowed size for unique key names on this platform.
     *
     * @return the maximum unique key name size
     */
    default int getMaxUniqueKeyNameSize() {
        return getMaxFieldNameSize();
    }

    /**
     * INTERNAL:
     * returns the maximum number of characters that can be used in a foreign key
     * name on this platform.
     */
    default int getMaxForeignKeyNameSize() {
        return getMaxFieldNameSize();
    }

    /**
     * Used for stored procedure defs.
     */
    default String getProcedureAsString() {
        return " AS";
    }

    /**
     * Used for stored procedure defs.
     */
    default String getProcedureBeginString() {
        return getBatchBeginString();
    }

    /**
     * Used for stored procedure defs.
     */
    default String getProcedureEndString() {
        return getBatchEndString();
    }

    /**
     * Some platforms have an option list
     * Only to be used for stored procedure creation.
     *
     * @see StoredProcedureDefinition
     */
    default String getProcedureOptionList() {
        return "";
    }

    default String getStoredProcedureTerminationToken() {
        return ";";
    }

    /**
     * Used for constraint deletion.
     */
    default String getUniqueConstraintDeletionString() {
        return getConstraintDeletionString();
    }

    /**
     * INTERNAL:
     * Override this method if the platform supports sequence objects
     * and it's possible to alter sequence object's increment in the database.
     */
    default boolean isAlterSequenceObjectSupported() {
        return false;
    }

    /**
     * Prints the platform-specific identity clause for a field (e.g., AUTO_INCREMENT).
     *
     * @param writer the writer to append to
     * @throws IOException if an I/O error occurs while writing
     */
    default void printFieldIdentityClause(Writer writer) throws IOException {
        //The default is to do nothing.
    }

    /**
     * Prints the platform-specific NOT NULL clause for a field.
     *
     * @param writer the writer to append to
     * @throws IOException if an I/O error occurs while writing
     */
    default void printFieldNotNullClause(Writer writer) throws IOException {
        writer.write(" NOT NULL");
    }

    /**
     * Prints the platform-specific NULL clause for a field.
     *
     * @param writer the writer to append to
     * @throws IOException if an I/O error occurs while writing
     */
    default void printFieldNullClause(Writer writer) throws IOException {
        // The default is to do nothing
    }

    /**
     * Prints the field type size clause to the writer, including type name and optional size.
     *
     * @param writer                         the writer to append to
     * @param field                          the field definition
     * @param databaseType                   the field type definition
     * @param shouldPrintFieldIdentityClause indicates if the identity clause should be printed
     * @throws IOException if an I/O error occurs while writing
     */
    default void printFieldTypeSize(Writer writer, FieldDefinition field, FieldDefinition.DatabaseType databaseType, boolean shouldPrintFieldIdentityClause) throws IOException {
        printFieldTypeSize(writer, field, databaseType);
    }

    default void printFieldTypeSize(Writer writer, FieldDefinition field, FieldDefinition.DatabaseType databaseType) throws IOException {
        writer.write(databaseType.name());
        if ((databaseType.allowSize()) && ((field.getSize() != 0) || (databaseType.requireSize()))) {
            writer.write("(");
            if (field.getSize() == 0) {
                writer.write(Integer.toString(databaseType.defaultSize()));
            } else {
                writer.write(Integer.toString(field.getSize()));
            }
            if (field.getSubSize() != 0) {
                writer.write(",");
                writer.write(Integer.toString(field.getSubSize()));
            } else if (databaseType.defaultSubSize() != 0) {
                writer.write(",");
                writer.write(Integer.toString(databaseType.defaultSubSize()));
            }
            writer.write(")");
        }
    }

    /**
     * Prints the platform-specific UNIQUE clause for a field.
     *
     * @param writer                         the writer to append to
     * @param shouldPrintFieldUniqueClause indicates if the identity clause should be printed
     * @throws IOException if an I/O error occurs while writing
     */
    default void printFieldUnique(Writer writer, boolean shouldPrintFieldUniqueClause) throws IOException {
        if (supportsUniqueKeyConstraints()) {
            writer.write(" UNIQUE");
        }
    }

    /**
     * Indicates whether the platform requires named primary key constraints.
     *
     * @return true if named primary key constraints are required; false otherwise
     */
    default boolean requiresNamedPrimaryKeyConstraints() {
        return false;
    }

    /**
     * Used for stored procedure creation: Some platforms need brackets around arguments declaration even if no arguments exist.
     * Those platform will override this and return true. All other platforms will omit the brackets in this case.
     */
    default boolean requiresProcedureBrackets() {
        return false;
    }

    /**
     * INTERNAL:
     * Return if this database requires the table name when dropping an index.
     */
    default boolean requiresTableInIndexDropDDL() {
        return false;
    }

    /**
     * Used for table creation. If a database platform does not support ALTER
     * TABLE syntax to add/drop unique constraints (like Symfoware), overriding
     * this method will allow the constraint to be specified in the CREATE TABLE
     * statement.
     * <p>
     * This only affects unique constraints specified using the UniqueConstraint
     * annotation or equivalent method. Columns for which the 'unique' attribute
     * is set to true will be declared 'UNIQUE' in the CREATE TABLE statement
     * regardless of the return value of this method.
     *
     * @return whether unique constraints should be declared as part of the
     *         CREATE TABLE statement instead of in separate ALTER TABLE
     *         ADD/DROP statements.
     */
    default boolean requiresUniqueConstraintCreationOnTableCreate() {
        return false;
    }

    /**
     * Used for table creation. Most databases create an index automatically
     * when a primary key is created. Symfoware does not.
     *
     * @return whether an index should be created explicitly for primary keys
     */
    default boolean shouldCreateIndicesForPrimaryKeys() {
        return false;
    }

    /**
     * Used for table creation. Most databases do not create an index automatically for
     * foreign key columns.  Normally it is recommended to index foreign key columns.
     * This allows for foreign key indexes to be configured, by default foreign keys are not indexed.
     *
     * @return whether an index should be created explicitly for foreign key constraints
     */
    default boolean shouldCreateIndicesOnForeignKeys() {
        return false;
    }

    /**
     * Used for table creation. Most databases create an index automatically for
     * columns with a unique constraint. Symfoware does not.
     *
     * @return whether an index should be created explicitly for unique
     *         constraints
     */
    default boolean shouldCreateIndicesOnUniqueKeys() {
        return false;
    }

    /**
     * Some Platforms want the constraint name after the constraint definition.
     */
    default boolean shouldPrintConstraintNameAfter() {
        return false;
    }

    /**
     * Used for stored procedure creation: Some platforms declare variables AFTER the procedure body's BEGIN string.
     * These need to override and return true. All others will print the variable declaration BEFORE the body's BEGIN string.
     */
    default boolean shouldPrintStoredProcedureVariablesAfterBeginString() {
        return false;
    }

    default boolean supportsDeleteOnCascade() {
        return supportsForeignKeyConstraints();
    }

    /**
     * Override this method if the platform supports setting fractional seconds
     * precision in a SQL time or timestamp data type.
     *
     * @return value of {@code true} when current database platform supports
     *         fractional seconds precision or {@code false} otherwise
     */
    default boolean supportsFractionalTime() {
        return false;
    }

    default boolean supportsForeignKeyConstraints() {
        return true;
    }

    default boolean supportsUniqueKeyConstraints() {
        return true;
    }

    /**
     * INTERNAL:
     * Return if this database support index creation.
     */
    default boolean supportsIndexes() {
        return true;
    }

    /**
     * Indicates whether the platform supports primary key constraints.
     *
     * @return true if primary key constraints are supported; false otherwise
     */
    default boolean supportsPrimaryKeyConstraint() {
        return true;
    }

    default boolean supportsStoredFunctions() {
        return false;
    }

    /**
     * Indicates whether the platform supports unique columns (e.g., UNIQUE keyword on column).
     *
     * @return true if unique columns are supported; false otherwise
     */
    default boolean supportsUniqueColumns() {
        return true;
    }

    /**
     * Writes the platform-specific table creation suffix to the provided writer.
     * This may include storage clauses, tablespace definitions, or other platform-specific DDL.
     *
     * @param writer         the writer to append the suffix to
     * @param creationSuffix any additional user-provided suffix
     * @throws IOException if an I/O error occurs while writing
     */
    default void writeTableCreationSuffix(Writer writer, String creationSuffix) throws IOException {
        if(creationSuffix!=null && !creationSuffix.isEmpty()) {
            writer.write(" " + creationSuffix);
        }
        String defaultTableCreationSuffix = getTableCreationSuffix();
        if (defaultTableCreationSuffix !=null && !defaultTableCreationSuffix.isEmpty()) {
            writer.write(" " + defaultTableCreationSuffix);
        }
    }

    /**
     * Get the String used on all table creation statements generated from the DefaultTableGenerator
     * with a session using this project (DDL generation).  This value will be appended to CreationSuffix strings
     * stored on the DatabaseTable or TableDefinition.
     */
    default String getTableCreationSuffix(){
        return "";
    }

    /**
     * Delimiter to use for fields and tables using spaces or other special values.
     * <p>
     * Some databases use different delimiters for the beginning and end of the value.
     * This delimiter indicates the end of the value.
     */
    default String getEndDelimiter() {
        return "";
    }

    /**
     * Delimiter to use for fields and tables using spaces or other special values.
     * <p>
     * Some databases use different delimiters for the beginning and end of the value.
     * This delimiter indicates the start of the value.
     */
    default String getStartDelimiter() {
        return "";
    }

    /**
     * Return the create schema SQL syntax. Subclasses should override as needed.
     */
    default String getCreateDatabaseSchemaString(String schema) {
        return "CREATE SCHEMA " + schema;
    }

    /**
     * Return the drop schema SQL syntax. Subclasses should override as needed.
     */
    default String getDropDatabaseSchemaString(String schema) {
        return "DROP SCHEMA " + schema;
    }

    /**
     * This is required in the construction of the stored procedures with
     * output parameters
     */
    default boolean shouldPrintOutputTokenBeforeType() {
        return true;
    }

    /**
     * This is required in the construction of the stored procedures with
     * output parameters
     */
    default boolean shouldPrintOutputTokenAtStart() {
        return false;
    }

    /**
     * This method is used to print the required output parameter token for the
     * specific platform.  Used when stored procedures are created.
     */
    default String getCreationOutputProcedureToken() {
        return getOutputProcedureToken();
    }

    /**
     * This method is used to print the output parameter token when stored
     * procedures are called
     */
    default String getOutputProcedureToken() {
        return "OUT";
    }

    /**
     * Used for sp defs.
     */
    default String getProcedureArgumentString() {
        return "";
    }

    /**
     * Used for stored procedure definitions.
     */
    default boolean allowsSizeInProcedureArguments() {
        return true;
    }

    /**
     * This method is used to print the output parameter token when stored
     * procedures are called
     */
    default String getInOutputProcedureToken() {
        return "IN OUT";
    }

    /**
     * This method is used to print the required output parameter token for the
     * specific platform.  Used when stored procedures are created.
     */
    default String getCreationInOutputProcedureToken() {
        return getInOutputProcedureToken();
    }

    /**
     * Used for stored procedure creation: Some platforms want to print prefix for INPUT arguments BEFORE NAME. If wanted, override and return true.
     */
    default boolean shouldPrintInputTokenAtStart() {
        return false;
    }

    /**
     * Used for stored procedure creation: Prefix for INPUT parameters.
     * Not required on most platforms.
     */
    default String getInputProcedureToken() {
        return "";
    }

}
