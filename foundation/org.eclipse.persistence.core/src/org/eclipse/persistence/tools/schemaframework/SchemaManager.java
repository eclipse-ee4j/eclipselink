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
 *     Dies Koper - add support for creating indices on tables
 ******************************************************************************/  
package org.eclipse.persistence.tools.schemaframework;

import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.databaseaccess.DatabaseAccessor;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.sequencing.Sequencing;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.sequencing.DefaultSequence;
import org.eclipse.persistence.sequencing.NativeSequence;
import org.eclipse.persistence.sequencing.Sequence;
import org.eclipse.persistence.sequencing.TableSequence;
import org.eclipse.persistence.sequencing.UnaryTableSequence;

/**
 * <p>
 * <b>Purpose</b>: Define all user level protocol for development time database manipulation.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Define protocol for schema creation.
 * <li> Define any useful testing specific protocol.
 * </ul>
 */
public class SchemaManager {
    protected DatabaseSessionImpl session;
    protected Writer createSchemaWriter;
    protected Writer dropSchemaWriter;
    protected boolean createSQLFiles = true; //if true, schema writer will add terminator string.
    protected TableCreator defaultTableCreator;

    /** Allow table creator to occur "fast" by just deleting all the rows. */
    public static boolean FAST_TABLE_CREATOR = false;
    
    public SchemaManager(DatabaseSessionImpl session) {
        this.session = session;
    }

    public SchemaManager(org.eclipse.persistence.sessions.DatabaseSession session) {
        this.session = ((DatabaseSessionImpl)session);
    }

    protected Writer getDropSchemaWriter() {
        if (null == dropSchemaWriter) {
            return createSchemaWriter;
        } else {
            return dropSchemaWriter;
        }
    }

    /**
     * PUBLIC: If the schema manager is writing to a writer, append this string
     * to that writer.
     */
    public void appendToDDLWriter(String stringToWrite) {
        // If this method is called, we know that it is the old case and
        // it would not matter which schemaWriter we use as both the 
        // create and drop schemaWriters are essentially the same. 
        // So just pick one.
        appendToDDLWriter(createSchemaWriter, stringToWrite);
    }

    public void appendToDDLWriter(Writer schemaWriter, String stringToWrite) {
        if (schemaWriter == null) {
            return; //do nothing.  Ignore append request
        }

        try {
            schemaWriter.write(stringToWrite);
            schemaWriter.flush();
        } catch (java.io.IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
    }

    /**
     * INTERNAL:
     * builds the field names based on the type read in from the builder
     */
    public void buildFieldTypes(TableDefinition tableDef) {
        tableDef.buildFieldTypes(getSession());
    }

    /**
     * PUBLIC:
     * Close the schema writer.
     */
    public void closeDDLWriter() {
        closeDDLWriter(createSchemaWriter);
        closeDDLWriter(dropSchemaWriter);
        createSchemaWriter = null;
        dropSchemaWriter = null;
    }

    public void closeDDLWriter(Writer schemaWriter) {
        if (schemaWriter == null) {
            return;
        }

        try {
            schemaWriter.flush();
            schemaWriter.close();
        } catch (java.io.IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
    }

    /**
     * Use the table definition to add the constraints to the database, this is normally done
     * in two steps to avoid dependencies.
     */
    public void createConstraints(TableDefinition tableDefinition) throws EclipseLinkException {
        boolean usesBatchWriting = false;

        if (getSession().getPlatform().usesBatchWriting()) {
            usesBatchWriting = true;
            getSession().getPlatform().setUsesBatchWriting(false);
        }

        try {
            if (shouldWriteToDatabase()) {
                tableDefinition.createConstraintsOnDatabase(getSession());
            } else {
            tableDefinition.setCreateSQLFiles(createSQLFiles);
            tableDefinition.createConstraints(getSession(), createSchemaWriter);
            }
        } finally {
            if (usesBatchWriting) {
                getSession().getPlatform().setUsesBatchWriting(true);
            }
        }
    }
    
    void createUniqueConstraints(TableDefinition tableDefinition) throws EclipseLinkException {
        if (shouldWriteToDatabase()) {
            tableDefinition.createUniqueConstraintsOnDatabase(getSession());
        } else {
            tableDefinition.setCreateSQLFiles(createSQLFiles);
            tableDefinition.createUniqueConstraints(getSession(), createSchemaWriter);
        }
    }
    
    void createForeignConstraints(TableDefinition tableDefinition) throws EclipseLinkException {
        if (shouldWriteToDatabase()) {
            tableDefinition.createForeignConstraintsOnDatabase(getSession());
        } else {
            tableDefinition.setCreateSQLFiles(createSQLFiles);
            tableDefinition.createForeignConstraints(getSession(), createSchemaWriter);
        }
    }

    /**
     * Use the definition object to create the schema entity on the database.
     * This is used for creating tables, views, procedures ... etc ...
     */
    public void createObject(DatabaseObjectDefinition databaseObjectDefinition) throws EclipseLinkException {
        boolean usesBatchWriting = false;

        if (getSession().getPlatform().usesBatchWriting()) {
            usesBatchWriting = true;
            getSession().getPlatform().setUsesBatchWriting(false);
        }

        try {
            if (shouldWriteToDatabase()) {
                databaseObjectDefinition.createOnDatabase(getSession());
            } else {
                databaseObjectDefinition.createObject(getSession(), createSchemaWriter);
                if (createSQLFiles){
                    this.appendToDDLWriter(createSchemaWriter, getSession().getPlatform().getStoredProcedureTerminationToken());
                }
                this.appendToDDLWriter(createSchemaWriter, "\n");
                databaseObjectDefinition.postCreateObject(getSession(), createSchemaWriter, createSQLFiles);
            }
        } finally {
            if (usesBatchWriting) {
                getSession().getPlatform().setUsesBatchWriting(true);
            }
        }
    }

    /**
     * Create all the receiver's sequences on the database for all of the loaded descriptors.
     */
    public void createSequences() throws EclipseLinkException {
        createOrReplaceSequences(true);
    }

    public void setCreateSQLFiles(boolean genFlag) {
        this.createSQLFiles = genFlag;
    }

    /**
     * Drop and recreate all the receiver's sequences on the database for all of the loaded descriptors.
     */
    public void replaceSequences() throws EclipseLinkException {
        createOrReplaceSequences(false);
    }

    /**
     * Common implementor for createSequence and replaceSequence
     * @param create - true to create the sequences, false to replace them (dropped then create)
     */
    protected void createOrReplaceSequences(boolean create) throws EclipseLinkException {              
        createOrReplaceSequences(create, create);
    }
    
    /**
     * Common implementor for createSequence and replaceSequence, distinguishes between sequence tables and sequence objects
     * @param createSequenceTables - true to create the sequences tables, false to replace them (dropped then create)
     * @param createSequenceObjects - true to create the sequences objects, false to replace them (dropped then create)
     */
    protected void createOrReplaceSequences(boolean createSequenceTables, boolean createSequenceObjects) throws EclipseLinkException {
        // PERF: Allow a special "fast" flag to be set on the session causes a delete from the table instead of a replace.
        boolean fast = FAST_TABLE_CREATOR;
        if (fast) {
            // Assume sequences already created.
            return;
        }
        Sequencing sequencing = getSession().getSequencing();

        if ((sequencing == null) || (sequencing.whenShouldAcquireValueForAll() == Sequencing.AFTER_INSERT)) {
            // Not required on Sybase native etc.
            return;
        }

        // Prepare table and sequence definitions
        // table name mapped to TableDefinition
        HashMap tableDefinitions = new HashMap();

        // sequence name to SequenceDefinition
        HashSet sequenceDefinitions = new HashSet();

        // remember the processed - to handle each sequence just once.
        HashSet processedSequenceNames = new HashSet();

        buildTableAndSequenceDefinitions(sequenceDefinitions, processedSequenceNames, tableDefinitions);
        processTableDefinitions(tableDefinitions, createSequenceTables);
        processSequenceDefinitions(sequenceDefinitions, createSequenceObjects);
    }    

    private void buildTableAndSequenceDefinitions(final HashSet sequenceDefinitions, 
            final HashSet processedSequenceNames, final HashMap tableDefinitions) {
        Iterator descriptors = getSession().getDescriptors().values().iterator();

        while (descriptors.hasNext()) {
            ClassDescriptor descriptor = (ClassDescriptor) descriptors.next();

            if (descriptor.usesSequenceNumbers()) {
                String seqName = descriptor.getSequenceNumberName();

                if (seqName == null) {
                    seqName = getSession().getDatasourcePlatform().getDefaultSequence().getName();
                }

                if (processedSequenceNames.contains(seqName)) {
                    continue;
                }

                processedSequenceNames.add(seqName);

                Sequence sequence = getSession().getDatasourcePlatform().getSequence(seqName);

                SequenceDefinition sequenceDefinition = buildSequenceDefinition(sequence);

                if (sequenceDefinition == null) {
                    continue;
                }

                sequenceDefinitions.add(sequenceDefinition);

                TableDefinition tableDefinition = sequenceDefinition.buildTableDefinition();

                if (tableDefinition != null) {
                    String tableName = tableDefinition.getName();
                    TableDefinition otherTableDefinition = (TableDefinition) tableDefinitions.get(tableName);

                    if (otherTableDefinition != null) {
                        // check for a conflict; if there is one - throw a ValidationException
                    } else {
                        tableDefinitions.put(tableName, tableDefinition);
                    }
                }
            }
        }
    }

    /**
     * Method creates database sequence tables.  If create is true, it will attempt to create the sequence tables and silently 
     * ignore exceptions.  If create is false, it will drop the tables ignoring any exceptions, then create it.  
     * @param tableDefinitions - HashMap of Sequence table definitions
     * @param create - true if tables should be created, false if they should be replaced (dropped then created)
     * @throws EclipseLinkException
     */
    private void processTableDefinitions(final HashMap tableDefinitions, final boolean create) throws EclipseLinkException {

        // create tables
        Iterator itTableDefinitions = tableDefinitions.values().iterator();
        
        // CR 3870467, do not log stack
        boolean shouldLogExceptionStackTrace = session.getSessionLog().shouldLogExceptionStackTrace();

        while (itTableDefinitions.hasNext()) {
            TableDefinition tableDefinition = (TableDefinition) itTableDefinitions.next();

            processDatabaseObjectDefinition(tableDefinition, create, shouldLogExceptionStackTrace);
        }
    }

    /**
     * Method creates database sequence objects.  If create is true, it will attempt to create the sequence and silently ignore
     * exceptions.  If create is false, it will drop the sequence ignoring any exceptions, then create it.  
     * @param sequenceDefinitions - HashSet of Sequence object definitions
     * @param create - true if sequenceDefinitions should be created, false if they should be replaced (dropped then created)
     * @throws EclipseLinkException
     */
    private void processSequenceDefinitions(final HashSet sequenceDefinitions, final boolean create) throws EclipseLinkException {
        
        // CR 3870467, do not log stack
        boolean shouldLogExceptionStackTrace = session.getSessionLog().shouldLogExceptionStackTrace();
        // create sequence objects
        Iterator itSequenceDefinitions = sequenceDefinitions.iterator();
        
        while (itSequenceDefinitions.hasNext()) {
            SequenceDefinition sequenceDefinition = (SequenceDefinition) itSequenceDefinitions.next();
            
            processDatabaseObjectDefinition(sequenceDefinition, create, shouldLogExceptionStackTrace);
        }
    }

    /**
     * Method creates database tables/objects.  If create is true, it will attempt to create the object and silently ignore
     * exceptions.  If create is false, it will drop the object ignoring any exceptions, then create it.  
     * @param definition -the object definition
     * @param create - true if the definition should be created, false if it should be replaced (dropped then created)
     * @throws EclipseLinkException
     */
    private void processDatabaseObjectDefinition(DatabaseObjectDefinition definition, final boolean create, final boolean shouldLogExceptionStackTrace) throws EclipseLinkException {
        if (shouldLogExceptionStackTrace) {
            session.getSessionLog().setShouldLogExceptionStackTrace(false);
        }

        if (create) {
            try {
                createObject(definition);
            } catch (DatabaseException exception) {
                // Ignore already created
            } finally {
                if (shouldLogExceptionStackTrace) {
                    session.getSessionLog().setShouldLogExceptionStackTrace(true);
                }
            }
        } else {
            try {
                dropObject(definition);
            } catch (DatabaseException exception) {
                // Ignore table not found for first creation
            } finally {
                if (shouldLogExceptionStackTrace) {
                    session.getSessionLog().setShouldLogExceptionStackTrace(true);
                }
            }

            createObject(definition);
        }
    }

    protected SequenceDefinition buildSequenceDefinition(Sequence sequence) {
        if (sequence.shouldAcquireValueAfterInsert()) {
            return null;
        }
        if (sequence instanceof TableSequence ||
            (sequence instanceof DefaultSequence && ((DefaultSequence)sequence).getDefaultSequence() instanceof TableSequence)) {
            return new TableSequenceDefinition(sequence);
        } else if (sequence instanceof UnaryTableSequence ||
                   (sequence instanceof DefaultSequence && ((DefaultSequence)sequence).getDefaultSequence() instanceof UnaryTableSequence)) {
            return new UnaryTableSequenceDefinition(sequence);
        } else if (sequence instanceof NativeSequence || 
                   (sequence instanceof DefaultSequence && ((DefaultSequence)sequence).getDefaultSequence() instanceof NativeSequence)) {
            return new SequenceObjectDefinition(sequence);
        } else {
            return null;
        }
    }

    /**
     * Use the table definition to drop the constraints from the table, this is normally done
     * in two steps to avoid dependencies.
     */
    public void dropConstraints(TableDefinition tableDefinition) throws EclipseLinkException {
        boolean usesBatchWriting = false;

        if (getSession().getPlatform().usesBatchWriting()) {
            usesBatchWriting = true;
            getSession().getPlatform().setUsesBatchWriting(false);
        }

        try {
            if (shouldWriteToDatabase()) {
                tableDefinition.dropConstraintsOnDatabase(getSession());
            } else {
                tableDefinition.setCreateSQLFiles(createSQLFiles);
                tableDefinition.dropConstraints(getSession(), getDropSchemaWriter());
            }
        } finally {
            if (usesBatchWriting) {
                getSession().getPlatform().setUsesBatchWriting(true);
            }
        }
    }

    /**
     * Use the definition object to drop the schema entity from the database.
     * This is used for dropping tables, views, procedures ... etc ...
     */
    public void dropObject(DatabaseObjectDefinition databaseObjectDefinition) throws EclipseLinkException {
        boolean usesBatchWriting = false;

        if (getSession().getPlatform().usesBatchWriting()) {
            usesBatchWriting = true;
            getSession().getPlatform().setUsesBatchWriting(false);
        }

        try {
            if (shouldWriteToDatabase()) {
                // drop actual object
                databaseObjectDefinition.dropFromDatabase(getSession());
            } else {
                Writer dropSchemaWriter = getDropSchemaWriter();
                // drop actual object
                databaseObjectDefinition.dropObject(getSession(), dropSchemaWriter, createSQLFiles);
                if (createSQLFiles){
                    this.appendToDDLWriter(dropSchemaWriter, getSession().getPlatform().getStoredProcedureTerminationToken());
                }
                this.appendToDDLWriter(dropSchemaWriter, "\n");
            }
        } finally {
            if (usesBatchWriting) {
                getSession().getPlatform().setUsesBatchWriting(true);
            }
        }
    }

    /**
     * Drop (delete) the table named tableName from the database.
     */
    public void dropTable(String tableName) throws EclipseLinkException {
        TableDefinition tableDefinition;

        tableDefinition = new TableDefinition();
        tableDefinition.setName(tableName);
        dropObject(tableDefinition);
    }

    /**
     * INTERNAL:
     * Close the schema writer when the schema manger is garbage collected
     */
    public void finalize() {
        try {
            this.closeDDLWriter();
        } catch (ValidationException exception) {
            // do nothing
        }
    }

    /**
     * PUBLIC:
     * Use this method to generate stored procedures based on the dynamic SQL generated
     * for your mappings and descriptors.  This should be used with caution as it maintenance
     * will be high.  Stored procedures may be generated either directly on the database
     * or to a file.
     */
    public void generateStoredProcedures() throws EclipseLinkException {
        new StoredProcedureGenerator(this).generateStoredProcedures();
    }

    /**
     * PUBLIC:
     * Use this method to generate stored procedures based on the dynamic SQL generated
     * for your mappings and descriptors.  This should be used with caution as it maintenance
     * will be high.  Stored procedures may be generated either directly on the database
     * or to a file.
     */
    public void generateStoredProcedures(Writer writer) throws EclipseLinkException {
        new StoredProcedureGenerator(this).generateStoredProcedures(writer);
    }

    /**
     * PUBLIC:
     * Use this method to generate stored procedures based on the dynamic SQL generated
     * for your mappings and descriptors.  This should be used with caution as it maintenance
     * will be high.  Stored procedures may be generated either directly on the database
     * or to a file.
     */
    public void generateStoredProceduresAndAmendmentClass(Writer writer, String fullyQualifiedClassName) throws EclipseLinkException {
        String className = fullyQualifiedClassName.substring(fullyQualifiedClassName.lastIndexOf('.') + 1);
        String packageName = fullyQualifiedClassName.substring(0, fullyQualifiedClassName.lastIndexOf('.'));
        StoredProcedureGenerator storedProcedureGenerator = new StoredProcedureGenerator(this);
        storedProcedureGenerator.generateStoredProcedures();

        storedProcedureGenerator.generateAmendmentClass(writer, packageName, className);
    }

    /**
     * PUBLIC:
     * Use this method to generate stored procedures based on the dynamic SQL generated
     * for your mappings and descriptors.  This should be used with caution as it maintenance
     * will be high.  Stored procedures may be generated either directly on the database
     * or to a file.
     */
    public void generateStoredProceduresAndAmendmentClass(String path, String fullyQualifiedClassName) throws EclipseLinkException {
    	java.io.FileWriter fileWriter = null;
    	try {
            StoredProcedureGenerator storedProcedureGenerator = new StoredProcedureGenerator(this);

            if (!(path.endsWith("\\") || path.endsWith("/"))) {
                path = path + "\\";
            }

            String className = fullyQualifiedClassName.substring(fullyQualifiedClassName.lastIndexOf('.') + 1);
            String packageName = fullyQualifiedClassName.substring(0, fullyQualifiedClassName.lastIndexOf('.'));
            String fileName = path + className + ".java";
            fileWriter = new java.io.FileWriter(fileName);
            storedProcedureGenerator.generateStoredProcedures();
            storedProcedureGenerator.generateAmendmentClass(fileWriter, packageName, className);
            fileWriter.close();
        } catch (java.io.IOException ioException) {
            throw ValidationException.fileError(ioException);
        } finally {
        	Helper.close(fileWriter);
        }
    }

    /**
     * Return the appropriate accessor.
     * Assume we are dealing with a JDBC accessor.
     */
    protected DatabaseAccessor getAccessor() {
        return (DatabaseAccessor) getSession().getAccessor();
    }

    /**
     * Get a description of table columns available in a catalog.
     *
     * <P>Each column description has the following columns:
     *  <OL>
     *    <LI><B>TABLE_CAT</B> String => table catalog (may be null)
     *    <LI><B>TABLE_SCHEM</B> String => table schema (may be null)
     *    <LI><B>TABLE_NAME</B> String => table name
     *    <LI><B>COLUMN_NAME</B> String => column name
     *    <LI><B>DATA_TYPE</B> short => SQL type from java.sql.Types
     *    <LI><B>TYPE_NAME</B> String => Data source dependent type name
     *    <LI><B>COLUMN_SIZE</B> int => column size.  For char or date
     *        types this is the maximum number of characters, for numeric or
     *        decimal types this is precision.
     *    <LI><B>BUFFER_LENGTH</B> is not used.
     *    <LI><B>DECIMAL_DIGITS</B> int => the number of fractional digits
     *    <LI><B>NUM_PREC_RADIX</B> int => Radix (typically either 10 or 2)
     *    <LI><B>NULLABLE</B> int => is NULL allowed?
     *      <UL>
     *      <LI> columnNoNulls - might not allow NULL values
     *      <LI> columnNullable - definitely allows NULL values
     *      <LI> columnNullableUnknown - nullability unknown
     *      </UL>
     *    <LI><B>REMARKS</B> String => comment describing column (may be null)
     *     <LI><B>COLUMN_DEF</B> String => default value (may be null)
     *    <LI><B>SQL_DATA_TYPE</B> int => unused
     *    <LI><B>SQL_DATETIME_SUB</B> int => unused
     *    <LI><B>CHAR_OCTET_LENGTH</B> int => for char types the
     *       maximum number of bytes in the column
     *    <LI><B>ORDINAL_POSITION</B> int    => index of column in table
     *      (starting at 1)
     *    <LI><B>IS_NULLABLE</B> String => "NO" means column definitely
     *      does not allow NULL values; "YES" means the column might
     *      allow NULL values.  An empty string means nobody knows.
     *  </OL>
     *
     * @param tableName a table name pattern
     * @return a Vector of Records.
     */
    public Vector getAllColumnNames(String tableName) throws DatabaseException {
        return getAccessor().getColumnInfo(null, null, tableName, null, getSession());
    }

    /**
     * Get a description of table columns available in a catalog.
     *
     * <P>Each column description has the following columns:
     *  <OL>
     *    <LI><B>TABLE_CAT</B> String => table catalog (may be null)
     *    <LI><B>TABLE_SCHEM</B> String => table schema (may be null)
     *    <LI><B>TABLE_NAME</B> String => table name
     *    <LI><B>COLUMN_NAME</B> String => column name
     *    <LI><B>DATA_TYPE</B> short => SQL type from java.sql.Types
     *    <LI><B>TYPE_NAME</B> String => Data source dependent type name
     *    <LI><B>COLUMN_SIZE</B> int => column size.  For char or date
     *        types this is the maximum number of characters, for numeric or
     *        decimal types this is precision.
     *    <LI><B>BUFFER_LENGTH</B> is not used.
     *    <LI><B>DECIMAL_DIGITS</B> int => the number of fractional digits
     *    <LI><B>NUM_PREC_RADIX</B> int => Radix (typically either 10 or 2)
     *    <LI><B>NULLABLE</B> int => is NULL allowed?
     *      <UL>
     *      <LI> columnNoNulls - might not allow NULL values
     *      <LI> columnNullable - definitely allows NULL values
     *      <LI> columnNullableUnknown - nullability unknown
     *      </UL>
     *    <LI><B>REMARKS</B> String => comment describing column (may be null)
     *     <LI><B>COLUMN_DEF</B> String => default value (may be null)
     *    <LI><B>SQL_DATA_TYPE</B> int => unused
     *    <LI><B>SQL_DATETIME_SUB</B> int => unused
     *    <LI><B>CHAR_OCTET_LENGTH</B> int => for char types the
     *       maximum number of bytes in the column
     *    <LI><B>ORDINAL_POSITION</B> int    => index of column in table
     *      (starting at 1)
     *    <LI><B>IS_NULLABLE</B> String => "NO" means column definitely
     *      does not allow NULL values; "YES" means the column might
     *      allow NULL values.  An empty string means nobody knows.
     *  </OL>
     *
     * @param creatorName a schema name pattern; "" retrieves those
     * without a schema
     * @param tableName a table name pattern
     * @return a Vector of Records.
     */
    public Vector getAllColumnNames(String creatorName, String tableName) throws DatabaseException {
        return getAccessor().getColumnInfo(null, creatorName, tableName, null, getSession());
    }

    /**
     * Get a description of tables available in a catalog.
     *
     * <P>Each table description has the following columns:
     *  <OL>
     *    <LI><B>TABLE_CAT</B> String => table catalog (may be null)
     *    <LI><B>TABLE_SCHEM</B> String => table schema (may be null)
     *    <LI><B>TABLE_NAME</B> String => table name
     *    <LI><B>TABLE_TYPE</B> String => table type.  Typical types are "TABLE",
     *            "VIEW",    "SYSTEM TABLE", "GLOBAL TEMPORARY",
     *            "LOCAL TEMPORARY", "ALIAS", "SYNONYM".
     *    <LI><B>REMARKS</B> String => explanatory comment on the table
     *  </OL>
     *
     * <P><B>Note:</B> Some databases may not return information for
     * all tables.
     *
     * @return a Vector of Records.
     */
    public Vector getAllTableNames() throws DatabaseException {
        return getAccessor().getTableInfo(null, null, null, null, getSession());
    }

    /**
     * Get a description of table columns available in a catalog.
     *
     * <P>Each column description has the following columns:
     *  <OL>
     *    <LI><B>TABLE_CAT</B> String => table catalog (may be null)
     *    <LI><B>TABLE_SCHEM</B> String => table schema (may be null)
     *    <LI><B>TABLE_NAME</B> String => table name
     *    <LI><B>COLUMN_NAME</B> String => column name
     *    <LI><B>DATA_TYPE</B> short => SQL type from java.sql.Types
     *    <LI><B>TYPE_NAME</B> String => Data source dependent type name
     *    <LI><B>COLUMN_SIZE</B> int => column size.  For char or date
     *        types this is the maximum number of characters, for numeric or
     *        decimal types this is precision.
     *    <LI><B>BUFFER_LENGTH</B> is not used.
     *    <LI><B>DECIMAL_DIGITS</B> int => the number of fractional digits
     *    <LI><B>NUM_PREC_RADIX</B> int => Radix (typically either 10 or 2)
     *    <LI><B>NULLABLE</B> int => is NULL allowed?
     *      <UL>
     *      <LI> columnNoNulls - might not allow NULL values
     *      <LI> columnNullable - definitely allows NULL values
     *      <LI> columnNullableUnknown - nullability unknown
     *      </UL>
     *    <LI><B>REMARKS</B> String => comment describing column (may be null)
     *     <LI><B>COLUMN_DEF</B> String => default value (may be null)
     *    <LI><B>SQL_DATA_TYPE</B> int => unused
     *    <LI><B>SQL_DATETIME_SUB</B> int => unused
     *    <LI><B>CHAR_OCTET_LENGTH</B> int => for char types the
     *       maximum number of bytes in the column
     *    <LI><B>ORDINAL_POSITION</B> int    => index of column in table
     *      (starting at 1)
     *    <LI><B>IS_NULLABLE</B> String => "NO" means column definitely
     *      does not allow NULL values; "YES" means the column might
     *      allow NULL values.  An empty string means nobody knows.
     *  </OL>
     *
     * @param creatorName a schema name pattern; "" retrieves those
     * without a schema
     * @return a Vector of Records.
     */
    public Vector getAllTableNames(String creatorName) throws DatabaseException {
        return getAccessor().getTableInfo(null, creatorName, null, null, getSession());
    }

    /**
     * Get a description of table columns available in a catalog.
     *
     * <P>Only column descriptions matching the catalog, schema, table
     * and column name criteria are returned.  They are ordered by
     * TABLE_SCHEM, TABLE_NAME and ORDINAL_POSITION.
     *
     * <P>Each column description has the following columns:
     *  <OL>
     *    <LI><B>TABLE_CAT</B> String => table catalog (may be null)
     *    <LI><B>TABLE_SCHEM</B> String => table schema (may be null)
     *    <LI><B>TABLE_NAME</B> String => table name
     *    <LI><B>COLUMN_NAME</B> String => column name
     *    <LI><B>DATA_TYPE</B> short => SQL type from java.sql.Types
     *    <LI><B>TYPE_NAME</B> String => Data source dependent type name
     *    <LI><B>COLUMN_SIZE</B> int => column size.  For char or date
     *        types this is the maximum number of characters, for numeric or
     *        decimal types this is precision.
     *    <LI><B>BUFFER_LENGTH</B> is not used.
     *    <LI><B>DECIMAL_DIGITS</B> int => the number of fractional digits
     *    <LI><B>NUM_PREC_RADIX</B> int => Radix (typically either 10 or 2)
     *    <LI><B>NULLABLE</B> int => is NULL allowed?
     *      <UL>
     *      <LI> columnNoNulls - might not allow NULL values
     *      <LI> columnNullable - definitely allows NULL values
     *      <LI> columnNullableUnknown - nullability unknown
     *      </UL>
     *    <LI><B>REMARKS</B> String => comment describing column (may be null)
     *     <LI><B>COLUMN_DEF</B> String => default value (may be null)
     *    <LI><B>SQL_DATA_TYPE</B> int => unused
     *    <LI><B>SQL_DATETIME_SUB</B> int => unused
     *    <LI><B>CHAR_OCTET_LENGTH</B> int => for char types the
     *       maximum number of bytes in the column
     *    <LI><B>ORDINAL_POSITION</B> int    => index of column in table
     *      (starting at 1)
     *    <LI><B>IS_NULLABLE</B> String => "NO" means column definitely
     *      does not allow NULL values; "YES" means the column might
     *      allow NULL values.  An empty string means nobody knows.
     *  </OL>
     *
     * @param catalog a catalog name; "" retrieves those without a
     * catalog; null means drop catalog name from the selection criteria
     * @param schema a schema name pattern; "" retrieves those
     * without a schema
     * @param tableName a table name pattern
     * @param columnName a column name pattern
     * @return a Vector of Records.
     */
    public Vector getColumnInfo(String catalog, String schema, String tableName, String columnName) throws DatabaseException {
        return getAccessor().getColumnInfo(catalog, schema, tableName, columnName, getSession());
    }

    public AbstractSession getSession() {
        return session;
    }

    /**
     * Get a description of tables available in a catalog.
     *
     * <P>Only table descriptions matching the catalog, schema, table
     * name and type criteria are returned.  They are ordered by
     * TABLE_TYPE, TABLE_SCHEM and TABLE_NAME.
     *
     * <P>Each table description has the following columns:
     *  <OL>
     *    <LI><B>TABLE_CAT</B> String => table catalog (may be null)
     *    <LI><B>TABLE_SCHEM</B> String => table schema (may be null)
     *    <LI><B>TABLE_NAME</B> String => table name
     *    <LI><B>TABLE_TYPE</B> String => table type.  Typical types are "TABLE",
     *            "VIEW",    "SYSTEM TABLE", "GLOBAL TEMPORARY",
     *            "LOCAL TEMPORARY", "ALIAS", "SYNONYM".
     *    <LI><B>REMARKS</B> String => explanatory comment on the table
     *  </OL>
     *
     * <P><B>Note:</B> Some databases may not return information for
     * all tables.
     *
     * @param catalog a catalog name; "" retrieves those without a
     * catalog; null means drop catalog name from the selection criteria
     * @param schema a schema name pattern; "" retrieves those
     * without a schema
     * @param tableName a table name pattern
     * @param types a list of table types to include; null returns all types
     * @return a Vector of Records.
     */
    public Vector getTableInfo(String catalog, String schema, String tableName, String[] types) throws DatabaseException {
        return getAccessor().getTableInfo(catalog, schema, tableName, types, getSession());
    }

    /**
     * PUBLIC:
     * Output all DDL statements directly to the database.
     */
    public void outputDDLToDatabase() {
        this.createSchemaWriter = null;
        this.dropSchemaWriter = null;
    }

    /**
     * PUBLIC:
     * Output all DDL statements to a file writer specified by the name in the parameter.
     */
    public void outputDDLToFile(String fileName) {
        try {
            this.createSchemaWriter = new java.io.FileWriter(fileName);
        } catch (java.io.IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
    }

    public void outputCreateDDLToFile(String fileName) {
        try {
            this.createSchemaWriter = new java.io.FileWriter(fileName);
        } catch (java.io.IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
    }

    public void outputDropDDLToFile(String fileName) {
        try {
            this.dropSchemaWriter = new java.io.FileWriter(fileName);
        } catch (java.io.IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
    }

    /**
     * PUBLIC:
     * Output all DDL statements to a writer specified in the parameter.
     */
    public void outputDDLToWriter(Writer schemaWriter) {
        this.createSchemaWriter = schemaWriter;
    }

    public void outputCreateDDLToWriter(Writer createWriter) {
        this.createSchemaWriter = createWriter;
    }

    public void outputDropDDLToWriter(Writer dropWriter) {
        this.dropSchemaWriter = dropWriter;
    }

    /**
     * Use the definition object to drop and recreate the schema entity on the database.
     * This is used for dropping tables, views, procedures ... etc ...
     * This handles and ignore any database error while dropping in case the object did not previously exist.
     */
    public void replaceObject(DatabaseObjectDefinition databaseDefinition) throws EclipseLinkException {                
        // PERF: Allow a special "fast" flag to be set on the session causes a delete from the table instead of a replace.
        boolean fast = FAST_TABLE_CREATOR;
        if (fast && (databaseDefinition instanceof TableDefinition)) {
            session.executeNonSelectingSQL("DELETE FROM " + databaseDefinition.getName()); 
        } else {
            // CR 3870467, do not log stack
            boolean shouldLogExceptionStackTrace = getSession().getSessionLog().shouldLogExceptionStackTrace();
    
            if (shouldLogExceptionStackTrace) {
                getSession().getSessionLog().setShouldLogExceptionStackTrace(false);
            }
            try {
                dropObject(databaseDefinition);
            } catch (DatabaseException exception) {
                // Ignore error
            } finally {
                if (shouldLogExceptionStackTrace) {
                    getSession().getSessionLog().setShouldLogExceptionStackTrace(true);
                }
            }

            createObject(databaseDefinition);
        }
    }

    /**
     * Construct the default TableCreator.
     * If the default TableCreator is already created, just returns it. 
     */
    protected TableCreator getDefaultTableCreator(boolean generateFKConstraints) {
        if(defaultTableCreator == null) {
            defaultTableCreator = new DefaultTableGenerator(session.getProject(),generateFKConstraints).generateDefaultTableCreator();
            defaultTableCreator.setIgnoreDatabaseException(true);
        }
        return defaultTableCreator;
    }
    
    /**
    * Create the default table schema for the TopLink project this session associated with.
    */
    public void createDefaultTables(boolean generateFKConstraints) {
        //Create each table w/o throwing exception and/or exit if some of them are already existed in the db. 
        //If a table is already existed, skip the creation.

        boolean shouldLogExceptionStackTrace = getSession().getSessionLog().shouldLogExceptionStackTrace();
        getSession().getSessionLog().setShouldLogExceptionStackTrace(false);

        try {
            TableCreator tableCreator = getDefaultTableCreator(generateFKConstraints);
            tableCreator.createTables(session, this);
        } catch (DatabaseException ex) {
            // Ignore error
        } finally {
            getSession().getSessionLog().setShouldLogExceptionStackTrace(shouldLogExceptionStackTrace);
        }
    }

    /**
     * Drop and recreate the default table schema for the TopLink project this session associated with.
     */
    public void replaceDefaultTables() throws EclipseLinkException {
        boolean shouldLogExceptionStackTrace = getSession().getSessionLog().shouldLogExceptionStackTrace();
        getSession().getSessionLog().setShouldLogExceptionStackTrace(false);

        try {
            TableCreator tableCreator = getDefaultTableCreator(true);
            tableCreator.replaceTables(session, this);
        } catch (DatabaseException exception) {
            // Ignore error
        } finally {
            getSession().getSessionLog().setShouldLogExceptionStackTrace(shouldLogExceptionStackTrace);
        }
    }
    
    /**
     * Drop and recreate the default table schema for the TopLink project this session associated with.
     */
    public void replaceDefaultTables(boolean keepSequenceTables, boolean generateFKConstraints) throws EclipseLinkException {
        boolean shouldLogExceptionStackTrace = getSession().getSessionLog().shouldLogExceptionStackTrace();
        getSession().getSessionLog().setShouldLogExceptionStackTrace(false);

        try {
            TableCreator tableCreator = getDefaultTableCreator(generateFKConstraints);
            tableCreator.replaceTables(session, this, keepSequenceTables);
        } catch (DatabaseException exception) {
            // Ignore error
        } finally {
            getSession().getSessionLog().setShouldLogExceptionStackTrace(shouldLogExceptionStackTrace);
        }
    }

    public void setSession(DatabaseSessionImpl session) {
        this.session = session;
    }

    /**
     * PUBLIC:
     * Return true if this SchemaManager should write to the database directly
     */
    public boolean shouldWriteToDatabase() {
        return ((this.createSchemaWriter == null) && (this.dropSchemaWriter == null));
    }

    /**
     * Use the definition to alter sequence.
     */
    public void alterSequence(SequenceDefinition sequenceDefinition) throws EclipseLinkException {
        if (!sequenceDefinition.isAlterSupported(getSession())) {
            return;
        }

        boolean usesBatchWriting = false;

        if (getSession().getPlatform().usesBatchWriting()) {
            usesBatchWriting = true;
            getSession().getPlatform().setUsesBatchWriting(false);
        }

        try {
            if (shouldWriteToDatabase()) {
                sequenceDefinition.alterOnDatabase(getSession());
            } else {
                sequenceDefinition.alter(getSession(), createSchemaWriter);
            }
        } finally {
            if (usesBatchWriting) {
                getSession().getPlatform().setUsesBatchWriting(true);
            }
        }
    }

}
