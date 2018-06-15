/*
 * Copyright (c) 2010, 2018 Dies Koper (Fujitsu). Oracle and/or its affiliates, IBM Corporation.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Created Feb 19, 2010 - Dies Koper (Fujitsu)
//        bug 288715: Drop Table Restrictions: "table locked" errors when dropping
//                    tables in several Core and many JPA LRG tests on Symfoware.
//     Jul 19, 2014 - Tomas Kraus (Oracle)
//        bug 437578: Added few helper methods to simplify table builder methods.
//     Jan 06, 2015 - Dalia Abo Sheasha (IBM)
//        bug 454917: Moved a few helper methods from child class to be visible to all table creators.
package org.eclipse.persistence.testing.framework;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.tools.schemaframework.FieldDefinition;
import org.eclipse.persistence.tools.schemaframework.ForeignKeyConstraint;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;
import org.eclipse.persistence.tools.schemaframework.TableCreator;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

/**
 * Many JPA and a few Core tests use the same tables names, so at the start of
 * the tests tables are dropped and recreated. If the tables were created in a
 * previous test and the connection used to create it is still open, Symfoware
 * complains that the tables are locked when it tries to drop them.
 * <p/>
 *
 * This class sets a flag before the subsequent recreation of the tables to
 * delete the rows instead. The first time it is called it does not set the
 * flag, to allow the tables to be initially created.
 * <p/>
 *
 * To enable this functionality, the system property
 * "eclipselink.test.toggle-fast-table-creator" needs to be set to "true".
 * <p/>
 *
 * This class should be positioned between the test's table creator class and
 * TableCreator by making the's test table creator extend this class.<br/>
 *
 * @author Dies Koper, Tomas Kraus
 *
 */
public class TogglingFastTableCreator extends TableCreator {

    /** Character to separate table name and column name in full column identifier. */
    protected static final char TABLE_FIELD_SEPARATOR = '.';

    protected static Set fastTableCreators = new HashSet();
    protected static boolean useFastTableCreatorAfterInitialCreate = Boolean
            .getBoolean("eclipselink.test.toggle-fast-table-creator");

    /**
     * Delegates to super's constructor.
     */
    public TogglingFastTableCreator() {
        super();
    }

    /**
     * Delegates to super's constructor.
     */
    public TogglingFastTableCreator(Vector tableDefinitions) {
        super(tableDefinitions);
    }

    @Override
    public void replaceTables(DatabaseSession session) {
        // on Symfoware, to avoid table locking issues only the first invocation
        // of an instance of this class (drops & re-)creates the tables
        // if the system property is set.
        session.getSessionLog().log(SessionLog.FINEST, "TogglingFastTableCreator: useFastTableCreatorAfterInitialCreate: "
                + useFastTableCreatorAfterInitialCreate);

        boolean isFirstCreate = !isFastTableCreator();
        session.getSessionLog().log(SessionLog.FINEST, "TogglingFastTableCreator: " + getTableCreatorName()
                + " - isFirstCreate: " + isFirstCreate);
        session.getSessionLog().log(SessionLog.FINEST, "TogglingFastTableCreator: Current fastTableCreators: "
                + fastTableCreators);

        if (useFastTableCreatorAfterInitialCreate && !isFirstCreate) {
            session.getSessionLog().log(SessionLog.FINEST, "TogglingFastTableCreator: " + getTableCreatorName()
                    + " - toggling true");
            String sequenceTableName = getSequenceTableName(session);
            List<TableDefinition> tables = getTableDefinitions();
            for (TableDefinition table : tables) {
                if (!table.getName().equals(sequenceTableName)) {
                    SchemaManager schemaManager = new SchemaManager(session);
                    AbstractSession abstarctSession = schemaManager.getSession();
                    try {
                        abstarctSession.priviledgedExecuteNonSelectingCall(new org.eclipse.persistence.queries.SQLCall("DELETE FROM " + table.getFullName()));
                    } catch (DatabaseException ex) {
                        //Ignore database exception. eg. If there is no table to delete, it gives database exception.
                    }
                }
            }
        } else {
            super.replaceTables(session);
        }

        // next time just delete the rows instead.
        if (useFastTableCreatorAfterInitialCreate) {
            setFastTableCreator();
            session.getSessionLog().log(SessionLog.FINEST, "TogglingFastTableCreator: " + getTableCreatorName()
                    + " added to fastTableCreators");
        }
    }

    public boolean resetFastTableCreator() {
        AbstractSessionLog.getLog().log(SessionLog.FINEST, "TogglingFastTableCreator: removing table creator: "
                + getTableCreatorName());
        return fastTableCreators.remove(getTableCreatorName());
    }

    public boolean setFastTableCreator() {
        AbstractSessionLog.getLog().log(SessionLog.FINEST, "TogglingFastTableCreator: adding table creator: "
                + getTableCreatorName());
        return fastTableCreators.add(getTableCreatorName());
    }

    public boolean isFastTableCreator() {
        return fastTableCreators.contains(getTableCreatorName());
    }

    public String getTableCreatorName() {
        return this.getClass().getName();
    }

    /**
     * Helper method to concatenate table name and column name into full column name.
     * @param tableName Table name.
     * @param columnName Column name.
     * @return Full column name (prefixed with table name).
     */
    protected static String buildFullColumnName(
            final String tableName, final String columnName) {
        final StringBuilder sb = new StringBuilder(tableName.length() + columnName.length() + 1);
        sb.append(tableName).append(TABLE_FIELD_SEPARATOR).append(columnName);
        return sb.toString();

    }

    /**
     * Helper method to create {@link TableDefinition} instance with given table name.
     * @param name Table name.
     * @return {@link TableDefinition} instance with name set.
     */
    protected static TableDefinition createTable(final String name) {
        final TableDefinition table = new TableDefinition();
        table.setName(name);
        return table;
    }

    /**
     * Helper method to create {@link FieldDefinition} instance for unique
     * numeric primary key with given name and size.
     * @param name Column name.
     * @param size Column numeric type size.
     * @return Initialized {@link FieldDefinition} instance.
     */
    protected static FieldDefinition createNumericPk(
            final String name, final int size) {
        final FieldDefinition field = new FieldDefinition();
        field.setName(name);
        field.setTypeName("NUMERIC");
        field.setSize(size);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(true);
        field.setIsIdentity(true);
        return field;
    }

    /**
     * Helper method to create {@link FieldDefinition} instance for unique
     * numeric primary key with given name and default size of {@code 15}.
     * @param name Column name.
     * @return Initialized {@link FieldDefinition} instance.
     */
    protected static FieldDefinition createNumericPk(final String name) {
        return createNumericPk(name, 15);
    }

    /**
     * Helper method to create {@link FieldDefinition} instance for
     * numeric foreign key with given name, size and foreign key name.
     * @param name   Column name.
     * @param size   Column numeric type size.
     * @param fkName Foreign key name (e.g. {@code "MY_TABLE.ID"}.
     * @return Initialized {@link FieldDefinition} instance.
     */
    protected static FieldDefinition createNumericFk(
            final String name, final int size, final String fkName) {
        final FieldDefinition field = new FieldDefinition();
        field.setName(name);
        field.setTypeName("NUMERIC");
        field.setSize(size);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        field.setForeignKeyFieldName(fkName);
        return field;
    }

    /**
     * Helper method to create {@link FieldDefinition} instance for
     * numeric foreign key with given name, foreign key name and default size
     * of {@code 15}.
     * @param name Column name.
     * @return Initialized {@link FieldDefinition} instance.
     */
    protected static FieldDefinition createNumericFk(
            final String name, final String fkName) {
        return createNumericFk(name, 15, fkName);
    }

    /**
     * Helper method to create {@link FieldDefinition} instance for numeric column
     * with given name and size and without any additional constraints.
     * @param name Column name.
     * @param size Column numeric type size.
     * @param allowNull Allow {@code null} values for column.
     * @return Initialized {@link FieldDefinition} instance.
     */
    protected static FieldDefinition createNumericColumn(
            final String name, final int size, final boolean allowNull) {
        final FieldDefinition field = new FieldDefinition();
        field.setName(name);
        field.setTypeName("NUMERIC");
        field.setSize(size);
        field.setShouldAllowNull(allowNull);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        return field;
    }

    /**
     * Helper method to create {@link FieldDefinition} instance for numeric column
     * with given name, size of {@code 15}, with {@code null} value allowed and
     * without any additional constraints.
     * @param name Column name.
     * @param size Column numeric type size of {@code 15} with .
     * @return Initialized {@link FieldDefinition} instance.
     * @param allowNull Allow {@code null} values for column.
     */
    protected static FieldDefinition createNumericColumn(
            final String name) {
        return createNumericColumn(name, 15, true);
    }

    /**
     * Helper method to create {@link FieldDefinition} instance for <code>DTYPE</code>
     * column used for inheritance in model.
     * @return Initialized {@link FieldDefinition} instance.
     */
    protected static FieldDefinition createDTypeColumn() {
        final FieldDefinition field = new FieldDefinition();
        field.setName("DTYPE");
        field.setTypeName("VARCHAR2");
        field.setSize(15);
        field.setSubSize(0);
        field.setIsPrimaryKey(false);
        field.setIsIdentity(false);
        field.setUnique(false);
        field.setShouldAllowNull(true);
        return field;
    }

    /**
     * Helper method to create {@link FieldDefinition} instance for {@link String} column
     * with given name and size and without any additional constraints.
     * @param name Column name.
     * @param size Column numeric type size.
     * @param allowNull Allow {@code null} values for column.
     * @return Initialized {@link FieldDefinition} instance.
     */
    protected static FieldDefinition createStringColumn(
            final String name, final int size, final boolean allowNull) {
        final FieldDefinition field = new FieldDefinition();
        field.setName(name);
        field.setTypeName("VARCHAR");
        field.setSize(size);
        field.setShouldAllowNull(allowNull);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        return field;
    }

    /**
     * Helper method to create {@link FieldDefinition} instance for {@link String} column
     * with given name size of {@code 32}, with {@code null} value allowed and
     * without any additional constraints.
     * @param name Column name.
     * @return Initialized {@link FieldDefinition} instance.
     */
    protected static FieldDefinition createStringColumn(
            final String name) {
        return createStringColumn(name, 32, true);
    }

    /**
     * Helper method to create {@link FieldDefinition} instance
     * for {@link java.util.Date} column with given name and size and without
     * any additional constraints.
     * @param name Column name.
     * @param size Column date size.
     * @param allowNull Allow {@code null} values for column.
     * @return Initialized {@link FieldDefinition} instance.
     */
    protected static FieldDefinition createDateColumn(
            final String name, final int size, final boolean allowNull) {
        final FieldDefinition field = new FieldDefinition();
        field.setName(name);
        field.setTypeName("DATE");
        field.setSize(size);
        field.setShouldAllowNull(allowNull);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        return field;
    }

    /**
     * Helper method to create {@link FieldDefinition} instance
     * for {@link java.util.Date} column with given name size of {@code 23},
     * with {@code null} value allowed and without any additional constraints.
     * @param name Column name.
     * @return Initialized {@link FieldDefinition} instance.
     */
    protected static FieldDefinition createDateColumn(
            final String name) {
        return createDateColumn(name, 23, true);
    }

    protected void adjustForeignKeyFieldTypes(DatabaseSession session) {
        for (TableDefinition sourceTableDefinition : getTableDefinitions() ) {
            for (FieldDefinition sourceFieldDefinition : sourceTableDefinition.getFields()) {  // see TableDefinition, l.789
                if (sourceFieldDefinition.getForeignKeyFieldName() != null) {
                    // We need to build each foreign key constraint on the fly, because TableDefinition.buildFieldTypes() has not been called yet
                    ForeignKeyConstraint foreignKeyConstraint = buildForeignKeyConstraint(sourceFieldDefinition, session.getPlatform());
                    // Assume only one of each (as in Field Defintion)
                    String sourceFieldName = foreignKeyConstraint.getSourceFields().get(0);
                    String targetFieldName = foreignKeyConstraint.getTargetFields().get(0);

                    // Find the target table and the target field
                    TableDefinition targetTableDefinition = getTableDefinition(foreignKeyConstraint.getTargetTable());
                    // session.getSessionLog().log(SessionLog.FINEST, "AdvancedTableCreator: Found target table " +  foreignKeyConstraint.getTargetTable() + " for foreign key " + foreignKeyConstraint.getName());
                    FieldDefinition targetFieldDefinition = getFieldDefinition(targetTableDefinition, targetFieldName);
                    // session.getSessionLog().log(SessionLog.FINEST, "AdvancedTableCreator: Found target field " + targetFieldDefinition.getName() + " in table " + targetTableDefinition.getName());
                    // Only change source column if target is identity
                    String qualifiedName = targetTableDefinition.getFullName() + '.' + targetFieldDefinition.getName();
                    if (targetFieldDefinition.isIdentity() && session.getPlatform().shouldPrintFieldIdentityClause((AbstractSession)session, qualifiedName)) {
                        session.getSessionLog().log(SessionLog.FINEST, "AdvancedTableCreator.adjustForeignKeyFieldTypes(): Changing data type of source field " + sourceFieldDefinition.getName() + "to INTEGER in table " + sourceTableDefinition.getName());
                        sourceFieldDefinition.setTypeName("INTEGER");
                        sourceFieldDefinition.setSize(0);
                    }
                }
            }
        }
    }

    // Helper methods for adjustForeignKeyFieldTypes()

    private FieldDefinition getFieldDefinition(TableDefinition tableDefinition, String fieldName) {
        for (FieldDefinition targetField : tableDefinition.getFields()) { // see TableDefinition, l.351
            if (targetField.getName().equals(fieldName)) {
                return targetField;
            }
        }
        return null;
    }

    private TableDefinition getTableDefinition(String tableName) {
        for (TableDefinition targetTable : getTableDefinitions()) { // see TableCreator, l.87
            if (targetTable.getName().equals(tableName)) {
                return targetTable;
            }
        }
        return null;
    }

    // Mostly cloned from TableDefinition.buildForeignKeyConstraint()
    private ForeignKeyConstraint buildForeignKeyConstraint(FieldDefinition field, DatabasePlatform platform) {
        Vector sourceFields = new Vector();
        Vector targetFields = new Vector();
        ForeignKeyConstraint fkConstraint = new ForeignKeyConstraint();
        DatabaseField tempTargetField = new DatabaseField(field.getForeignKeyFieldName());
        DatabaseField tempSourceField = new DatabaseField(field.getName());

        sourceFields.add(tempSourceField.getName());
        targetFields.add(tempTargetField.getName());

        fkConstraint.setSourceFields(sourceFields);
        fkConstraint.setTargetFields(targetFields);
        fkConstraint.setTargetTable(tempTargetField.getTable().getQualifiedNameDelimited(platform));

        return fkConstraint;
    }
}
