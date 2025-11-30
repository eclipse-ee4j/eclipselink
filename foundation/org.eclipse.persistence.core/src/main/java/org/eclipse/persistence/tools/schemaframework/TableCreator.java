/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2020 IBM Corporation. All rights reserved.
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
//     02/04/2013-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support
package org.eclipse.persistence.tools.schemaframework;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.databaseaccess.FieldTypeDefinition;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.eclipse.persistence.sequencing.Sequence;
import org.eclipse.persistence.sequencing.TableSequence;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Session;

/**
 * <b>Purpose</b>: This class is responsible for creating the tables defined in the project.
 * A specific subclass of this class is created for each project.  The specific table information
 * is defined in the subclass.
 *
 * @since TopLink 2.0
 * @author Peter Krogh
 */
public class TableCreator {
    /** Default identity generator sequence name.
     *  Copy of value from JPA: {@code MetadataProject.DEFAULT_IDENTITY_GENERATOR}. */
    public static final String DEFAULT_IDENTITY_GENERATOR = "SEQ_GEN_IDENTITY";

    /** Flag to disable table existence check before create. */
    public static boolean CHECK_EXISTENCE = true;

    protected List<TableDefinition> tableDefinitions;
    protected String name;
    protected boolean ignoreDatabaseException; //if true, DDL generation will continue even if exceptions occur

    public TableCreator() {
        this(new ArrayList<>());
    }

    public TableCreator(List<TableDefinition> tableDefinitions) {
        super();
        this.tableDefinitions = tableDefinitions;
    }

    /**
     * Add the table.
     */
    public void addTableDefinition(TableDefinition tableDefinition) {
        this.tableDefinitions.add(tableDefinition);
    }

    /**
     * Add a set of tables.
     */
    public void addTableDefinitions(Collection<TableDefinition> tableDefs) {
        this.tableDefinitions.addAll(tableDefs);
    }


    /**
     * Create constraints.
     */
    public void createConstraints(DatabaseSession session) {
        //CR2612669
        createConstraints(session, new SchemaManager(session));
    }

    /**
     * Create constraints.
     */
    public void createConstraints(DatabaseSession session, SchemaManager schemaManager) {
        createConstraints(session, schemaManager, true);
    }

    /**
     * Create constraints.
     */
    public void createConstraints(DatabaseSession session, SchemaManager schemaManager, boolean build) {
        createConstraints(getTableDefinitions(), session, schemaManager, build);
    }

    /**
     * Create constraints.
     */
    public void createConstraints(List<TableDefinition> tables, DatabaseSession session, SchemaManager schemaManager, boolean build) {
        buildConstraints(schemaManager, build);

        // Unique constraints should be generated before foreign key constraints,
        // because foreign key constraints can reference unique constraints
        for (TableDefinition table : tables) {
            try {
                schemaManager.createUniqueConstraints(table);
            } catch (DatabaseException ex) {
                if (!shouldIgnoreDatabaseException()) {
                    throw ex;
                }
            }
        }

        for (TableDefinition table : tables) {
            try {
                schemaManager.createForeignConstraints(table);
            } catch (DatabaseException ex) {
                if (!shouldIgnoreDatabaseException()) {
                    throw ex;
                }
            }
        }
    }

    /**
     * This creates the tables on the database.
     * If the table already exists this will fail.
     */
    public void createTables(org.eclipse.persistence.sessions.DatabaseSession session) {
        //CR2612669
        createTables(session, new SchemaManager(session));
    }

    /**
     * This creates the tables on the database.
     * If the table already exists this will fail.
     */
    public void createTables(DatabaseSession session, SchemaManager schemaManager) {
        createTables(session, schemaManager, true);
    }

    /**
     * This creates the tables on the database.
     * If the table already exists this will fail.
     */
    public void createTables(DatabaseSession session, SchemaManager schemaManager, boolean build) {
        createTables(session, schemaManager, build, true, true, true);
    }

    /**
     * This creates the tables on the database.
     * If the table already exists this will fail.
     * @param session Active database session.
     * @param schemaManager Database schema manipulation manager.
     * @param build Whether to build constraints.
     * @param check Whether to check for tables existence.
     * @param createSequenceTables Whether to create sequence tables.
     * @param createSequences Whether to create sequences.
     */
    public void createTables(final DatabaseSession session, final SchemaManager schemaManager, final boolean build,
            final boolean check, final boolean createSequenceTables, final boolean createSequences) {
        buildConstraints(schemaManager, build);

        final String sequenceTableName = getSequenceTableName(session);
        final List<TableDefinition> missingTables = new ArrayList<>();
        for (TableDefinition table : getTableDefinitions()) {
            // Must not create sequence table as done in createSequences.
            if (!table.getName().equals(sequenceTableName)) {
                boolean alreadyExists = false;
                // Check if the table already exists, to avoid logging create error.
                if (check && CHECK_EXISTENCE && schemaManager.shouldWriteToDatabase()) {
                    alreadyExists = schemaManager.checkTableExists(table);
                }
                if (!alreadyExists) {
                    missingTables.add(table);
                    try {
                        schemaManager.createObject(table);
                        session.getSessionLog().log(SessionLog.FINEST, SessionLog.DDL, "default_tables_created", table.getFullName());
                    } catch (DatabaseException ex) {
                        session.getSessionLog().log(SessionLog.FINEST, SessionLog.DDL, "default_tables_already_existed", table.getFullName());
                        if (!shouldIgnoreDatabaseException()) {
                            throw ex;
                        }
                    }
                }
            }
        }

        createConstraints(missingTables, session, schemaManager, false);

        schemaManager.createOrReplaceSequences(createSequenceTables, createSequences);
        session.getDatasourcePlatform().initIdentitySequences(session, DEFAULT_IDENTITY_GENERATOR);
    }

    /**
     * Drop the table constraints from the database.
     */
    public void dropConstraints(DatabaseSession session) {
        //CR2612669
        dropConstraints(session, new SchemaManager(session));
    }

    /**
     * Drop the table constraints from the database.
     */
    public void dropConstraints(DatabaseSession session, SchemaManager schemaManager) {
        dropConstraints(session, schemaManager, true);
    }

    /**
     * Drop the table constraints from the database.
     */
    public void dropConstraints(DatabaseSession session, SchemaManager schemaManager, boolean build) {
        buildConstraints(schemaManager, build);

        for (TableDefinition table : getTableDefinitions()) {
            try {
                schemaManager.dropConstraints(table);
            } catch (DatabaseException exception) {
                //ignore
            }
        }
    }

    /**
     * Drop the tables from the database.
     */
    public void dropTables(DatabaseSession session) {
        //CR2612669
        dropTables(session, new SchemaManager(session));
    }

    /**
     * Drop the tables from the database.
     */
    public void dropTables(DatabaseSession session, SchemaManager schemaManager) {
        dropTables(session, schemaManager, true);
    }

    /**
     * Drop the tables from the database.
     * @param session Active database session.
     * @param schemaManager Database schema manipulation manager.
     * @param build Whether to build constraints.
     */
    public void dropTables(final DatabaseSession session, final SchemaManager schemaManager, final boolean build) {
        buildConstraints(schemaManager, build);

        // CR 3870467, do not log stack, or log at all if not fine
        boolean shouldLogExceptionStackTrace = session.getSessionLog().shouldLogExceptionStackTrace();
        final int level = session.getSessionLog().getLevel();
        if (shouldLogExceptionStackTrace) {
            session.getSessionLog().setShouldLogExceptionStackTrace(false);
        }
        if (level > SessionLog.FINE) {
            session.getSessionLog().setLevel(SessionLog.SEVERE);
        }
        try {
            dropConstraints(session, schemaManager, false);

            final String sequenceTableName = getSequenceTableName(session);
            List<TableDefinition> tables = getTableDefinitions();
            int trys = 1;
            if (SchemaManager.FORCE_DROP) {
                trys = 5;
            }
            while ((trys > 0) && !tables.isEmpty()) {
                trys--;
                final List<TableDefinition> failed = new ArrayList<>();
                final Set<String> tableNames = new HashSet<>(tables.size());
                for (final TableDefinition table : tables) {
                    final String tableName = table.getName();
                    // Must not create sequence table as done in createSequences.
                    if (!tableName.equals(sequenceTableName)) {
                        try {
                            schemaManager.dropObject(table);
                            tableNames.add(tableName);
                        } catch (DatabaseException exception) {
                            failed.add(table);
                            if (!shouldIgnoreDatabaseException()) {
                                throw exception;
                            }
                        }
                    }
                }
                session.getDatasourcePlatform().removeIdentitySequences(session, DEFAULT_IDENTITY_GENERATOR, tableNames);
                tables = failed;
            }
        } finally {
            if (shouldLogExceptionStackTrace) {
                session.getSessionLog().setShouldLogExceptionStackTrace(true);
            }
            if (level > SessionLog.FINE) {
                session.getSessionLog().setLevel(level);
            }
        }
    }

    /**
     * Return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Return the tables.
     */
    public List<TableDefinition> getTableDefinitions() {
        return tableDefinitions;
    }

    /**
     * Recreate the tables on the database.
     * This will drop the tables if they exist and recreate them.
     */
    public void replaceTables(DatabaseSession session) {
        replaceTables(session, new SchemaManager(session));
    }

    /**
     * Recreate the tables on the database.
     * This will drop the tables if they exist and recreate them.
     */
    public void replaceTables(DatabaseSession session, SchemaManager schemaManager) {
        replaceTables(session, schemaManager, true, true);
    }

    /**
     * Recreate the tables on the database.
     * This will drop the tables if they exist and recreate them.
     */
    public void replaceTables(DatabaseSession session, SchemaManager schemaManager, boolean createSequenceTables) {
        replaceTables(session, schemaManager, createSequenceTables, false);
    }

    /**
     * Recreate the tables on the database.
     * This will drop the tables if they exist and recreate them.
     */
    public void replaceTables(DatabaseSession session, SchemaManager schemaManager, boolean createSequenceTables, boolean createSequences) {
        replaceTablesAndConstraints(schemaManager, session, createSequenceTables, createSequences);
    }

    /**
     * Truncate all tables in the database.
     *
     * @param session current database session
     * @param schemaManager database schema manager
     * @param generateFKConstraints attempt to create fk constraints when {@code true}
     */
    void truncateTables(DatabaseSession session, SchemaManager schemaManager, boolean generateFKConstraints) {
        TableCreator tableCreator = schemaManager.getDefaultTableCreator(generateFKConstraints);
        String sequenceTableName = tableCreator.getSequenceTableName(session);
        List<TableDefinition> tables = tableCreator.getTableDefinitions();
        dropConstraints(session, schemaManager, false);
        for (TableDefinition table : tables) {
            if (!table.getName().equals(sequenceTableName)) {
                try {
                    Writer stmtWriter = new StringWriter();
                    session.getPlatform().writeTruncateTable(stmtWriter, ((AbstractSession) session), table);
                    ((AbstractSession) session)
                            .priviledgedExecuteNonSelectingCall(
                                    new org.eclipse.persistence.queries.SQLCall(stmtWriter.toString()));
                } catch (DatabaseException ex) {
                    if (!shouldIgnoreDatabaseException()) {
                        throw ex;
                    }
                // stmtWriter is StringWriter so this is not expected to happen
                } catch (IOException ex) {
                    throw ValidationException.tablesTruncationFailed(ex);
                }
            }
        }
        createConstraints(tables, session, schemaManager, false);
    }

    protected void replaceTablesAndConstraints(SchemaManager schemaManager, DatabaseSession session, boolean createSequenceTables, boolean createSequences) {
        buildConstraints(schemaManager, true);
        boolean ignore = shouldIgnoreDatabaseException();
        setIgnoreDatabaseException(true);
        try {
            dropTables(session, schemaManager, false);
        } finally {
            setIgnoreDatabaseException(ignore);
        }
        createTables(session, schemaManager, false, false, createSequenceTables, createSequences);
    }

    protected void replaceTablesAndConstraints(SchemaManager schemaManager, DatabaseSession session) {
        replaceTables(session, schemaManager, false, false);
    }

    /**
     * Convert any field constraint to constraint objects.
     */
    protected void buildConstraints(SchemaManager schemaManager, boolean build) {
        if (build) {
            for (TableDefinition table : getTableDefinitions()) {
                schemaManager.buildFieldTypes(table);
            }
        }
    }

    /**
     * Set the name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set the tables.
     */
    public void setTableDefinitions(List<TableDefinition> tableDefinitions) {
        this.tableDefinitions = tableDefinitions;
    }

    /**
     * Return true if DatabaseException is to be ignored.
     */
    public boolean shouldIgnoreDatabaseException() {
        return ignoreDatabaseException;
    }

    /**
     * Set flag whether DatabaseException should be ignored.
     */
    public void setIgnoreDatabaseException(boolean ignoreDatabaseException) {
        this.ignoreDatabaseException = ignoreDatabaseException;
    }

    /**
     * This returns the Sequence Table's qualified name, without delimiting.
     * @return the qualified table name
     */
    protected String getSequenceTableName(Session session) {
        String sequenceTableName = null;
        if (session.getProject().usesSequencing()) {
            Sequence sequence = session.getLogin().getDefaultSequence();
            if (sequence instanceof TableSequence) {
                sequenceTableName = ((TableSequence)sequence).getQualifiedTableName();
            }
        }
        return sequenceTableName;
    }

    /**
     * Create or extend the tables on the database.
     * This will alter existing tables to add missing fields or create the table otherwise.
     * It will also create Sequences tables and objects.
     */
    public void extendTables(DatabaseSession session, SchemaManager schemaManager) {
        extendTablesAndConstraints(schemaManager, session);
        schemaManager.createOrReplaceSequences(true, true);
    }

    protected void extendTablesAndConstraints(SchemaManager schemaManager, DatabaseSession session) {
        buildConstraints(schemaManager, true);
        boolean ignore = shouldIgnoreDatabaseException();
        setIgnoreDatabaseException(true);
        try {
            extendTables(session, schemaManager, false);
        } finally {
            setIgnoreDatabaseException(ignore);
        }
    }

    /**
     * Validate tables in the database.
     * Found issues are passed as {@link List} of {@link TableValidationException} to provided consumer
     * when validation failed.
     *
     * @param session Active database session.
     * @param schemaManager Database schema manipulation manager.
     * @param onFailed optional {@link Consumer} to accept {@link List} of {@link TableValidationException}
     *                 containing validation failures. Consumer is called <b>only</b> when validation failed
     *                 and {@code onFailed} is not null.
     * @param full run full validation when {@code true} or simple when {@code false}
     * @return Value of {@code true} when validation passed or {@code false} otherwise.
     */
    public boolean validateTables(final DatabaseSession session,
                                  final SchemaManager schemaManager,
                                  Consumer<List<TableValidationException>> onFailed,
                                  boolean full) {
        List<TableDefinition> tableDefinitions = getTableDefinitions();
        List<TableValidationException> exceptions = new ArrayList<>(tableDefinitions.size());
        tableDefinitions.forEach(tableDefinition -> {
            String tableName = tableDefinition.getTable() == null
                    ? tableDefinition.getName()
                    : tableDefinition.getTable().getName();
            if (schemaManager.checkTableExists(tableDefinition)) {
                List<AbstractRecord> columnsInfo = readColumnInfo((AbstractSession) session, tableDefinition);
                if (columnsInfo != null && !columnsInfo.isEmpty()) {
                    final Map<DatabaseField, AbstractRecord> columns = parseColumnInfo((AbstractSession) session,
                                                                                       tableDefinition,
                                                                                       columnsInfo);
                    // Database table columns check
                    CheckDatabaseColumns check = new CheckDatabaseColumns(session, columns.size(), full);
                    processColumns(tableDefinition,
                                           columns,
                                           check::checkExisting,
                                           check::addMissing,
                                           check::surplusColumns);
                    // Missing columns
                    if (!check.getMissingColumns().isEmpty()) {
                        exceptions.add(
                                new TableValidationException.MissingColumns(tableName, List.copyOf(check.getMissingColumns())));
                    }
                    // Surplus columns
                    if (!check.getSurplusFields().isEmpty()) {
                        exceptions.add(
                                new TableValidationException.SurplusColumns(
                                        tableName,
                                        List.copyOf(check.getSurplusFields().stream().map(DatabaseField::getName).toList())));
                    }
                    if (!check.getExistingColumnsDiff().isEmpty()) {
                        exceptions.add(
                                new TableValidationException.DifferentColumns(tableName, List.copyOf(check.getExistingColumnsDiff())));
                    }
                }
            } else {
                exceptions.add(new TableValidationException.MissingTable(tableName));
            }
        });
        if (exceptions.isEmpty()) {
            return true;
        } else {
            // Pass validation failures to provided consumer
            if (onFailed != null) {
                onFailed.accept(exceptions);
            }
            return false;
        }
    }

    /**
     * This creates/extends the tables on the database.
     * @param session Active database session.
     * @param schemaManager Database schema manipulation manager.
     * @param build Whether to build constraints.
     */
    public void extendTables(final DatabaseSession session, final SchemaManager schemaManager, final boolean build) {
        buildConstraints(schemaManager, build);

        final String sequenceTableName = getSequenceTableName(session);
        for (final TableDefinition table : getTableDefinitions()) {
            // Must not create sequence table as done in createSequences.
            if (!table.getName().equals(sequenceTableName)) {
                final AbstractSession abstractSession = (AbstractSession) session;
                boolean alreadyExists = false;
                // Check if the table already exists, to avoid logging create error.
                if (CHECK_EXISTENCE && schemaManager.shouldWriteToDatabase()) {
                    alreadyExists = schemaManager.checkTableExists(table);
                }
                DatabaseException createTableException = null;
                if (!alreadyExists) {
                    //assume table does not exist
                    try {
                        schemaManager.createObject(table);
                        session.getSessionLog().log(SessionLog.FINEST, SessionLog.DDL, "default_tables_created", table.getFullName());
                    } catch (final DatabaseException exception) {
                        createTableException = exception;
                        alreadyExists = true;
                    }
                }
                if (alreadyExists) {
                    //Assume the table exists, so lookup the column info

                    //While SQL is case insensitive, getColumnInfo is and will not return the table info unless the name is passed in
                    //as it is stored internally.
                    String tableName = table.getTable()==null? table.getName(): table.getTable().getName();
                    final boolean usesDelimiting = (table.getTable()!=null && table.getTable().shouldUseDelimiters());
                    List<AbstractRecord> columnInfo = null;

                    columnInfo = abstractSession.getAccessor().getColumnInfo(tableName, null, abstractSession);

                    if (!usesDelimiting && (columnInfo == null || columnInfo.isEmpty()) ) {
                        tableName = tableName.toUpperCase();
                        columnInfo = abstractSession.getAccessor().getColumnInfo(tableName, null, abstractSession);
                        if (( columnInfo == null || columnInfo.isEmpty()) ){
                            tableName = tableName.toLowerCase();
                            columnInfo = abstractSession.getAccessor().getColumnInfo(tableName, null, abstractSession);
                        }
                    }
                    if (columnInfo != null && !columnInfo.isEmpty()) {
                        //Table exists, add individual fields as necessary

                        //hash the table's existing columns by name
                        final Map<DatabaseField, AbstractRecord> columns = new HashMap<>(columnInfo.size());
                        final DatabaseField columnNameLookupField = new DatabaseField("COLUMN_NAME");
                        final DatabaseField schemaLookupField = new DatabaseField("TABLE_SCHEM");
                        boolean schemaMatchFound = false;
                        // Determine the probably schema for the table, this is a heuristic, so should not cause issues if wrong.
                        String qualifier = table.getQualifier();
                        if ((qualifier == null) || (qualifier.length() == 0)) {
                            qualifier = session.getDatasourcePlatform().getTableQualifier();
                            if ((qualifier == null) || (qualifier.length() == 0)) {
                                qualifier = session.getLogin().getUserName();
                                // Oracle DB DS defined in WLS does not contain user name so it's stored in platform.
                                if ((qualifier == null) || (qualifier.length() == 0)) {
                                    final DatabasePlatform platform = session.getPlatform();
                                    if (platform.supportsConnectionUserName()) {
                                        qualifier = platform.getConnectionUserName();
                                    }
                                }
                            }
                        }
                        final boolean checkSchema = (qualifier != null) && (qualifier.length() > 0);
                        for (final AbstractRecord record : columnInfo) {
                            final String fieldName = (String)record.get(columnNameLookupField);
                            if (fieldName != null && fieldName.length() > 0) {
                                final DatabaseField column = new DatabaseField(fieldName);
                                if (session.getPlatform().shouldForceFieldNamesToUpperCase()) {
                                    column.useUpperCaseForComparisons(true);
                                }
                                final String schema = (String)record.get(schemaLookupField);
                                // Check the schema as well.  Ignore columns for other schema if a schema match is found.
                                if (schemaMatchFound) {
                                    if (qualifier.equalsIgnoreCase(schema)) {
                                        columns.put(column,  record);
                                    }
                                } else {
                                    if (checkSchema) {
                                        if (qualifier.equalsIgnoreCase(schema)) {
                                            schemaMatchFound = true;
                                            // Remove unmatched columns from other schemas.
                                            columns.clear();
                                        }
                                    }
                                    // If none of the schemas match what is expected, assume what is expected is wrong, and use all columns.
                                    columns.put(column,  record);
                                }
                            }
                        }

                        //Go through each field we need to have in the table to see if it already exists
                        for (final FieldDefinition fieldDef : table.getFields()){
                            DatabaseField dbField = fieldDef.getDatabaseField();
                            if ( dbField == null ) {
                                dbField = new DatabaseField(fieldDef.getName());
                            }
                            if (columns.get(dbField)== null) {
                                //field does not exist so add it to the table
                                try {
                                    table.addFieldOnDatabase(abstractSession, fieldDef);
                                } catch (final DatabaseException addFieldEx) {
                                    session.getSessionLog().log(SessionLog.FINEST,  SessionLog.DDL, "cannot_add_field_to_table", dbField.getName(), table.getFullName(), addFieldEx.getMessage());
                                    if (!shouldIgnoreDatabaseException()) {
                                        throw addFieldEx;
                                    }
                                }
                            }
                        }
                    } else if (createTableException != null) {
                        session.getSessionLog().log(SessionLog.FINEST, SessionLog.DDL, "cannot_create_table", table.getFullName(), createTableException.getMessage());
                        if (!shouldIgnoreDatabaseException()) {
                            throw createTableException;
                        }
                    }
                }
            }
        }
        createConstraints(session, schemaManager, false);

        schemaManager.createSequences();
        session.getDatasourcePlatform().initIdentitySequences(session, DEFAULT_IDENTITY_GENERATOR);

    }

    // Reads column information from the database.
    private List<AbstractRecord> readColumnInfo(AbstractSession session, TableDefinition table) {
        String tableName = table.getTable() == null ? table.getName() : table.getTable().getName();
        boolean notUsesDelimiting = table.getTable() == null || !table.getTable().shouldUseDelimiters();

        List<AbstractRecord> columnInfo = session.getAccessor().getColumnInfo(tableName, null, session);
        if (notUsesDelimiting && (columnInfo == null || columnInfo.isEmpty()) ) {
            tableName = tableName.toUpperCase();
            columnInfo = session.getAccessor().getColumnInfo(tableName, null, session);
            if (( columnInfo == null || columnInfo.isEmpty()) ){
                tableName = tableName.toLowerCase();
                columnInfo = session.getAccessor().getColumnInfo(tableName, null, session);
            }
        }
        return columnInfo;
    }


    // Parse column information read from the database.
    private static Map<DatabaseField, AbstractRecord> parseColumnInfo(AbstractSession session,
                                                                      TableDefinition table,
                                                                      List<AbstractRecord> columnInfo) {
        // Hash the table's existing columns by name
        final Map<DatabaseField, AbstractRecord> columns = new HashMap<>(columnInfo.size());
        final DatabaseField columnNameLookupField = new DatabaseField("COLUMN_NAME");
        final DatabaseField schemaLookupField = new DatabaseField("TABLE_SCHEM");
        boolean schemaMatchFound = false;
        // Determine the probable schema for the table, this is a heuristic, so should not cause issues if wrong.
        String qualifier = table.getQualifier();
        if ((qualifier == null) || (qualifier.isEmpty())) {
            qualifier = session.getDatasourcePlatform().getTableQualifier();
            if ((qualifier == null) || (qualifier.isEmpty())) {
                qualifier = session.getLogin().getUserName();
                // Oracle DB DS defined in WLS does not contain username, so it's stored in platform.
                if ((qualifier == null) || (qualifier.isEmpty())) {
                    final DatabasePlatform platform = session.getPlatform();
                    if (platform.supportsConnectionUserName()) {
                        qualifier = platform.getConnectionUserName();
                    }
                }
            }
        }
        final boolean checkSchema = (qualifier != null) && (!qualifier.isEmpty());
        for (final AbstractRecord record : columnInfo) {
            final String fieldName = (String)record.get(columnNameLookupField);
            if (fieldName != null && !fieldName.isEmpty()) {
                final DatabaseField column = new DatabaseField(fieldName);
                if (session.getPlatform().shouldForceFieldNamesToUpperCase()) {
                    column.useUpperCaseForComparisons(true);
                }
                final String schema = (String)record.get(schemaLookupField);
                // Check the schema as well.  Ignore columns for other schema if a schema match is found.
                if (schemaMatchFound) {
                    if (qualifier.equalsIgnoreCase(schema)) {
                        columns.put(column,  record);
                    }
                } else {
                    if (checkSchema) {
                        if (qualifier.equalsIgnoreCase(schema)) {
                            schemaMatchFound = true;
                            // Remove unmatched columns from other schemas.
                            columns.clear();
                        }
                    }
                    // If none of the schemas match what is expected, assume what is expected is wrong, and use all columns.
                    columns.put(column,  record);
                }
            }
        }
        return columns;
    }

    // Run provided action for each column missing in the database.
    private static void processMissingColumns(TableDefinition table,
                                              Map<DatabaseField, AbstractRecord> columns,
                                              CheckDatabaseColumns.MissingField missingAction) {
        processColumns(table, columns, null, missingAction, null);
    }

    // Run provided action for each column missing in the database.
    // Optionally provide set of database columns not present in the TableDefinition.
    private static void processColumns(TableDefinition table,
                                       Map<DatabaseField, AbstractRecord> columns,
                                       CheckDatabaseColumns.ExistingField existingAction,
                                       CheckDatabaseColumns.MissingField missingAction,
                                       CheckDatabaseColumns.SurplusFields surplusAction) {
        // Surplus database fields if consumer was provided
        boolean isSurplusAction = surplusAction != null;
        Set<DatabaseField> surplusSet = isSurplusAction ? new HashSet<>(columns.keySet()) : null;
        // Process all fields from TableDefinition
        for (final FieldDefinition fieldDef : table.getFields()) {
            DatabaseField dbField = fieldDef.getDatabaseField();
            if (dbField == null) {
                dbField = new DatabaseField(fieldDef.getName());
            }
            // Run action for missing column
            AbstractRecord dbColumn = columns.get(dbField);
            if (dbColumn == null && missingAction != null) {
                missingAction.accept(fieldDef, dbField);
                // Handle existing column
            } else {
                // Run action for existing column
                if (existingAction != null) {
                    existingAction.accept(fieldDef, dbField, dbColumn);
                }
                // Update surplus columns set
                if (isSurplusAction) {
                    surplusSet.remove(dbField);
                }
            }
        }
        // Supply set of database columns not present in the TableDefinition when requested
        if (isSurplusAction) {
            surplusAction.accept(surplusSet);
        }
    }

    // Tables validator
    private static final class CheckDatabaseColumns {

        // Current database session
        final DatabaseSession session;
        // Run full validation when {@code true} or simple when {@code false)
        final boolean full;
        // List of missing columns
        final List<String> missingColumns;
        // List of differences found in existing columns
        final List<TableValidationException.DifferentColumns.Difference> existingColumnsDiff;
        // List of surplus columns, this set is built in processColumns method
        Set<DatabaseField> surplusFields;

        private CheckDatabaseColumns(DatabaseSession session, int size, boolean full) {
            this.session = session;
            this.full = full;
            this.missingColumns = new ArrayList<>(size);
            this.existingColumnsDiff = new LinkedList<>();
            this.surplusFields = null;
        }

        // Database columns check callback for missing column (existing in TableDefinition but not in database)
        private void addMissing(FieldDefinition fieldDefinition, DatabaseField databaseField) {
            missingColumns.add(databaseField.getName());
        }

        // Database columns check callback for column existing in both TableDefinition and the database
        private void checkExisting(FieldDefinition fieldDefinition, DatabaseField databaseField, AbstractRecord dbRecord) {
            // Existing columns validation only in full mode
            if (full) {
                FieldTypeDefinition expectedDbType = DatabaseObjectDefinition.getFieldTypeDefinition(session.getPlatform(),
                        fieldDefinition.getType(),
                        fieldDefinition.getTypeName());
                String dbTypeName = (String) dbRecord.get("TYPE_NAME");
                if (dbTypeName != null) {
                    // Type mismatch. DB typeName may be an alias, e.g. INT/INTEGER!
                    if (!expectedDbType.isTypeName(dbTypeName, false)) {
                        existingColumnsDiff.add(
                                new TableValidationException.DifferentColumns.TypeDifference(databaseField.getName(),
                                        expectedDbType.getName(),
                                        dbTypeName));
                    }
                    // Nullable mismatch
                    Nullable dbNullable = dbColumnNullable(dbRecord);
                    // Check only for known definition
                    if (dbNullable != Nullable.UNKNOWN) {
                        // Based on identical check in FieldDefinition#appendDBString(Writer, AbstractSession, TableDefinition, String)
                        boolean modelIsNullable = fieldDefinition.shouldPrintFieldNullClause(expectedDbType);
                        switch (dbNullable) {
                            case NO:
                                if (modelIsNullable) {
                                    existingColumnsDiff.add(
                                            new TableValidationException.DifferentColumns.NullableDifference(databaseField.getName(),
                                                    true,
                                                    false));
                                }
                                break;
                            case YES:
                                if (!modelIsNullable) {
                                    existingColumnsDiff.add(
                                            new TableValidationException.DifferentColumns.NullableDifference(databaseField.getName(),
                                                    false,
                                                    true));
                                }
                                break;
                        }
                    }
                }
            }
        }

        // Database columns check callback for set of surplus fields (existing in database but not in TableDefinition)
        private void surplusColumns(Set<DatabaseField> databaseFields) {
            this.surplusFields = databaseFields;
        }

        private Set<DatabaseField> getSurplusFields() {
            return surplusFields;
        }

        private List<String> getMissingColumns() {
            return missingColumns;
        }

        private List<TableValidationException.DifferentColumns.Difference> getExistingColumnsDiff() {
            return existingColumnsDiff;
        }

        // Existing fields processing callback definition
        @FunctionalInterface
        private interface ExistingField {
            void accept(FieldDefinition fieldDefinition, DatabaseField databaseField, AbstractRecord dbRecord);
        }

        // Missing fields processing callback definition
        @FunctionalInterface
        private interface MissingField {
            void accept(FieldDefinition fieldDefinition, DatabaseField databaseField);
        }

        // Surplus fields set callback definition
        @FunctionalInterface
        private interface SurplusFields {
            void accept(Set<DatabaseField> surplusFields);
        }

        // Database column description may contain NULLABLE and/or IS_NULLABLE.
        // According to description in org.eclipse.persistence.internal.databaseaccess.DatabaseAccessor and DB manuals:
        // IS_NULLABLE: The column nullability.
        //              The value is YES if NULL values can be stored in the column, NO if not.
        // NULLABLE: The column nullability as INTEGER value as boolean.
        //           The value is 1 if NULL values can be stored in the column, 0 if not.
        // Use one of those values if present or return UNKNOWN when nothing was found.
        private Nullable dbColumnNullable(AbstractRecord dbRecord) {
            Nullable result = Nullable.parseIsNullable((String) dbRecord.get("IS_NULLABLE"));
            if (result == Nullable.UNKNOWN) {
                result = Nullable.parseNullable((Integer) dbRecord.get("NULLABLE"));
            }
            return result;
        }

        // Nullability definition of the database column
        private enum Nullable {
            UNKNOWN,
            YES,
            NO;

            // Parse IS_NULLABLE database column description
            private static Nullable parseIsNullable(String isNullable) {
                if (isNullable == null) {
                    return UNKNOWN;
                }
                return switch (isNullable.toUpperCase()) {
                    case "NO" -> NO;
                    case "YES" -> YES;
                    default -> UNKNOWN;
                };
            }

            // Parse NULLABLE database column description
            private static Nullable parseNullable(Integer nullable) {
                if (nullable == null) {
                    return UNKNOWN;
                }
                return switch (nullable) {
                    case 0 -> NO;
                    case 1 -> YES;
                    default -> UNKNOWN;
                };
            }
        }
    }
}
