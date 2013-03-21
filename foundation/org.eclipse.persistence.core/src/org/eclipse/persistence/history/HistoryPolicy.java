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
package org.eclipse.persistence.history;

import java.io.Serializable;
import java.util.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.internal.databaseaccess.*;
import org.eclipse.persistence.internal.expressions.*;
import org.eclipse.persistence.internal.history.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.queries.*;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.mappings.DatabaseMapping.WriteType;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.queries.*;

/**
 * <b>Purpose:</b>Expresses how historical data is saved on the data store.
 * <p>This information is used to both maintain a history of all objects
 * modified through TopLink and to enable point in time querying.
 * <p>If Oracle 9R2 or later Flashback is used this policy is not required, as
 * the preservation of history is automatic.
 * <p>Descriptors, ManyToManyMappings, DirectCollectionMappings,
 * and DirectMapMappings only can have a history policy, as only they have associated
 * database tables.
 * @author Stephen McRitchie
 * @since 10
 */
public class HistoryPolicy implements Cloneable, Serializable {
    protected ClassDescriptor descriptor;
    protected DatabaseMapping mapping;
    protected List<DatabaseTable> historicalTables;
    protected List<DatabaseField> startFields;
    protected List<DatabaseField> endFields;
    protected boolean shouldHandleWrites = true;
    protected boolean usesLocalTime = true;

    public HistoryPolicy() {
    }

    /**
     * INTERNAL:
     * Add any temporal querying conditions to this object expression.
     */
    public Expression additionalHistoryExpression(Expression context, Expression base) {
        return additionalHistoryExpression(context, base, null);
    }

    /**
     * INTERNAL:
     * Add any temporal querying conditions to this object expression.
     * @parameter Integer tableIndex not null indicates that only expression for a single table should be returned.
     */
    public Expression additionalHistoryExpression(Expression context, Expression base, Integer tableIndex) {
        //
        AsOfClause clause = base.getAsOfClause();
        Object value = clause.getValue();
        Expression join = null;
        Expression subJoin = null;
        Expression start = null;
        Expression end = null;
        if (value == null) {
            return null;
            // for now nothing as assume mirroring historical tables.
        } else {
            if (value instanceof Expression) {
                // Sort of an implementation of native sql.
                // Print AS OF TIMESTAMP (SYSDATE - 1000*60*10) not AS OF ('SYSDATE - 1000*60*10').
                if ((value instanceof ConstantExpression) && (((ConstantExpression)value).getValue() instanceof String)) {
                    value = (((ConstantExpression)value).getValue());
                }
            } else {
                ConversionManager converter = ConversionManager.getDefaultManager();
                value = converter.convertObject(value, ClassConstants.TIMESTAMP);
            }

            if (getMapping() != null) {
                if (tableIndex != null && tableIndex.intValue() > 0) {
                    return null;
                }
                TableExpression tableExp = null;
                DatabaseTable historicalTable = getHistoricalTables().get(0);
                tableExp = (TableExpression)((ObjectExpression)base).existingDerivedTable(historicalTable);

                start = tableExp.getField(getStart());
                end = tableExp.getField(getEnd());

                join = start.lessThanEqual(value).and(end.isNull().or(end.greaterThan(value)));

                // We also need to do step two here in advance.	
                tableExp.setTable(historicalTable);

                return join;
            }
            int iFirst, iLast;
            if (tableIndex == null) {
                // loop through all history tables
                iFirst = 0;
                iLast = getHistoricalTables().size() - 1;
            } else {
                // only return expression for the specified table
                iFirst = tableIndex.intValue();
                iLast = iFirst;
            }
            for (int i = iFirst; i <= iLast ; i++) {
                start = base.getField(getStart(i));
                end = base.getField(getEnd(i));

                subJoin = start.lessThanEqual(value).and(end.isNull().or(end.greaterThan(value)));
                join = ((join == null) ? subJoin : join.and(subJoin));
            }
            return join;
        }
    }

    /**
     * PUBLIC:
     * Performs a sufficiently deep clone.
     * Use to quickly setup standard policies on multiple descriptors.
     */
    public Object clone() {
        HistoryPolicy clone = null;
        try {
            clone = (HistoryPolicy)super.clone();
        } catch (CloneNotSupportedException ignore) {
        }
        if (startFields != null) {
            clone.setStartFields(new ArrayList(startFields.size()));
            for (DatabaseField field : startFields) {
                clone.getStartFields().add(field.clone());
            }
        }
        if (endFields != null) {
            clone.setEndFields(new ArrayList(endFields.size()));
            for (DatabaseField field : endFields) {
                clone.getEndFields().add(field.clone());
            }
        }
        if (historicalTables != null) {
            clone.setHistoricalTables(new ArrayList(historicalTables));
        }
        return clone;
    }

    /**
     * PUBLIC:
     * Whenever a historical record is logically deleted (updated) or inserted,
     * the end and start fields respectively will be set to this value.
     */
    public Object getCurrentTime(AbstractSession session) {
        if (shouldUseLocalTime()) {
            return new java.sql.Timestamp(System.currentTimeMillis());
        }
        if (shouldUseDatabaseTime()) {
            AbstractSession readSession = session.getSessionForClass(getDescriptor().getJavaClass());
            while (readSession.isUnitOfWork()) {
                readSession = ((UnitOfWorkImpl)readSession).getParent().getSessionForClass(getDescriptor().getJavaClass());
            }
            return readSession.getDatasourceLogin().getDatasourcePlatform().getTimestampFromServer(session, readSession.getName());
        }
        return null;
    }

    /**
     * INTERNAL:
     * Return a minimal time increment supported by the platform.
     */
    public long getMinimumTimeIncrement(AbstractSession session) {
        AbstractSession readSession = session.getSessionForClass(getDescriptor().getJavaClass());
        while (readSession.isUnitOfWork()) {
            readSession = ((UnitOfWorkImpl)readSession).getParent().getSessionForClass(getDescriptor().getJavaClass());
        }
        return readSession.getPlatform().minimumTimeIncrement();
    }

    /**
     * PUBLIC:
     * Return the descriptor of the policy.
     */
    public ClassDescriptor getDescriptor() {
        return descriptor;
    }

    /**
     * INTERNAL:
     */
    public final List<DatabaseTable> getHistoricalTables() {
        if (historicalTables == null) {
            historicalTables = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(1);
        }
        return historicalTables;
    }

    /**
     * PUBLIC:
     */
    public List<String> getHistoryTableNames() {
        List<String> names = new ArrayList(getHistoricalTables().size());
        for (DatabaseTable table : getHistoricalTables()) {
            names.add(table.getQualifiedName());
        }
        return names;
    }

    /**
     * PUBLIC:
     */
    public DatabaseMapping getMapping() {
        return mapping;
    }

    /**
     * INTERNAL:
     */
    protected DatabaseField getStart() {
        if (startFields != null) {
            return startFields.get(0);
        } else {
            return null;
        }
    }

    /**
     * INTERNAL:
     */
    protected DatabaseField getStart(int i) {
        return startFields.get(i);
    }

    /**
     * PUBLIC:
     * Answers the name of the start field.  Assumes that multiple tables
     * for a descriptor have the same field names.
     */
    public String getStartFieldName() {
        if (getStart() != null) {
            return getStart().getName();
        } else {
            return null;
        }
    }

    /**
     * INTERNAL:
     */
    public List<DatabaseField> getStartFields() {
        return startFields;
    }

    /**
     * INTERNAL:
     */
    protected DatabaseField getEnd() {
        if (endFields != null) {
            return endFields.get(0);
        } else {
            return null;
        }
    }

    /**
     * INTERNAL:
     */
    protected DatabaseField getEnd(int i) {
        return endFields.get(i);
    }

    /**
     * PUBLIC:
     */
    public String getEndFieldName() {
        if (getEnd() != null) {
            return getEnd().getName();
        } else {
            return null;
        }
    }

    /**
     * INTERNAL:
     */
    public List<DatabaseField> getEndFields() {
        return endFields;
    }

    /**
     * PUBLIC:
     */
    public void setDescriptor(ClassDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    /**
     * INTERNAL:
     * Initialize a HistoryPolicy.
     */
    public void initialize(AbstractSession session) {
        if (getMapping() != null) {
            setDescriptor(getMapping().getDescriptor());
            if (getMapping().isDirectCollectionMapping()) {
                DatabaseTable refTable = ((DirectCollectionMapping)getMapping()).getReferenceTable();
                DatabaseTable histTable = getHistoricalTables().get(0);
                histTable.setName(refTable.getName());
                histTable.setTableQualifier(refTable.getTableQualifier());
                getStart().setTable(histTable);
                getEnd().setTable(histTable);
            } else if (getMapping().isManyToManyMapping()) {
                DatabaseTable relationTable = ((ManyToManyMapping)getMapping()).getRelationTable();
                DatabaseTable histTable = getHistoricalTables().get(0);
                histTable.setName(relationTable.getName());
                histTable.setTableQualifier(relationTable.getTableQualifier());
                getStart().setTable(histTable);
                getEnd().setTable(histTable);
            }
            verifyTableQualifiers(session.getPlatform());
            return;
        }

        // Some historicalTables will be inherited from a parent policy.
        int offset = getDescriptor().getTables().size() - getHistoricalTables().size();

        // In this configuration descriptor tables, history tables, and start/end fields
        // are all in the same order.
        if (!getHistoricalTables().isEmpty() && getHistoricalTables().get(0).getName().equals("")) {
            for (int i = 0; i < getHistoricalTables().size(); i++) {
                DatabaseTable table = getHistoricalTables().get(i);
                if (table.getName().equals("")) {
                    DatabaseTable mirrored = getDescriptor().getTables().get(i + offset);
                    table.setName(mirrored.getName());
                    table.setTableQualifier(mirrored.getTableQualifier());
                }
                if (getStartFields().size() < (i + 1)) {
                    DatabaseField startField = getStart(0).clone();
                    startField.setTable(table);
                    getStartFields().add(startField);
                } else {
                    DatabaseField startField = getStart(i);
                    startField.setTable(table);
                }
                if (getEndFields().size() < (i + 1)) {
                    DatabaseField endField = getEnd(0).clone();
                    endField.setTable(table);
                    getEndFields().add(endField);
                } else {
                    DatabaseField endField = getEnd(i);
                    endField.setTable(table);
                }
            }
        } else {
            // The user did not specify history tables/fields in order, so
            // initialize will take a little longer.
            List<DatabaseTable> unsortedTables = getHistoricalTables();
            List<DatabaseTable> sortedTables = new ArrayList(unsortedTables.size());
            List<DatabaseField> sortedStartFields = new ArrayList(unsortedTables.size());
            List<DatabaseField> sortedEndFields = new ArrayList(unsortedTables.size());
            boolean universalStartField = ((getStartFields().size() == 1) && (!(getStartFields().get(0)).hasTableName()));
            boolean universalEndField = ((getEndFields().size() == 1) && (!(getEndFields().get(0)).hasTableName()));
            DatabaseTable descriptorTable = null;
            DatabaseTable historicalTable = null;
            DatabaseField historyField = null;

            List<DatabaseTable> descriptorTables = getDescriptor().getTables();
            for (int i = offset; i < descriptorTables.size(); i++) {
                descriptorTable = descriptorTables.get(i);

                int index = unsortedTables.indexOf(descriptorTable);
                if (index == -1) {
                    // this is a configuration error!
                }
                historicalTable = unsortedTables.get(index);
                historicalTable.setTableQualifier(descriptorTable.getTableQualifier());
                sortedTables.add(historicalTable);

                if (universalStartField) {
                    historyField = getStart(0).clone();
                    historyField.setTable(historicalTable);
                    sortedStartFields.add(historyField);
                } else {
                    for (DatabaseField field : getStartFields()) {
                        if (field.getTable().equals(historicalTable)) {
                            sortedStartFields.add(field);
                            break;
                        }
                    }
                }
                if (universalEndField) {
                    historyField = getEnd(0).clone();
                    historyField.setTable(historicalTable);
                    sortedEndFields.add(historyField);
                } else {
                    for (DatabaseField field : getEndFields()) {
                        if (field.getTable().equals(historicalTable)) {
                            sortedEndFields.add(field);
                            break;
                        }
                    }
                }
            }
            setHistoricalTables(sortedTables);
            setStartFields(sortedStartFields);
            setEndFields(sortedEndFields);
        }
        verifyTableQualifiers(session.getPlatform());

        // A user need not set a policy on every level of an inheritance, but
        // historic tables can be inherited.
        if (getDescriptor().hasInheritance()) {
            ClassDescriptor parentDescriptor = getDescriptor().getInheritancePolicy().getParentDescriptor();
            while ((parentDescriptor != null) && (parentDescriptor.getHistoryPolicy() == null)) {
                parentDescriptor = parentDescriptor.getInheritancePolicy().getParentDescriptor();
            }
            if (parentDescriptor != null) {
                // Unique is required because the builder can add the same table many times.
                // This is done after init properties to make sure the default table is the first local one.
                setHistoricalTables(Helper.concatenateUniqueLists(parentDescriptor.getHistoryPolicy().getHistoricalTables(), getHistoricalTables()));
                setStartFields(Helper.concatenateUniqueLists(parentDescriptor.getHistoryPolicy().getStartFields(), getStartFields()));
                setEndFields(Helper.concatenateUniqueLists(parentDescriptor.getHistoryPolicy().getEndFields(), getEndFields()));
            }
        }
    }

    /**
     * PUBLIC:
     * Use to specify the names of the mirroring historical tables.
     * <p>
     * Assumes that the order in which tables are added with descriptor.addTableName()
     * matches the order in which mirroring historical tables are added with
     * descriptor.addHistoryTableName().
     */
    public void addHistoryTableName(String name) {
        HistoricalDatabaseTable table = new HistoricalDatabaseTable("");
        table.setHistoricalName(name);
        getHistoricalTables().add(table);
    }

    /**
     * PUBLIC:
     * Use to specify the names of the mirroring historical tables.
     * <p>
     * Explicitly states that <code>sourceTableName</code> is mirrored by history table
     * <code>historyTableName</code>.
     * The order in which tables are added with descriptor.addTableName()
     * should still match the order in which mirroring historical tables are
     * added with descriptor.addMirroringHistoryTableName().
     */
    public void addHistoryTableName(String sourceTableName, String historyTableName) {
        if ((sourceTableName == null) || sourceTableName.equals("")) {
            addHistoryTableName(historyTableName);
        }
        HistoricalDatabaseTable table = new HistoricalDatabaseTable(sourceTableName);
        table.setHistoricalName(historyTableName);
        // Note that the equality check is only on sourceTableName, not historyTableName.
        int index = getHistoricalTables().indexOf(table);
        if (index == -1) {
            getHistoricalTables().add(table);
        } else {
            getHistoricalTables().set(index, table);
        }
    }

    /**
     * INTERNAL:
     */
    public void setHistoricalTables(List<DatabaseTable> historicalTables) {
        this.historicalTables = historicalTables;
    }

    /**
     * INTERNAL:
     */
    public void setMapping(DatabaseMapping mapping) {
        this.mapping = mapping;
    }

    /**
     * INTERNAL:
     */
    protected void setStartFields(List<DatabaseField> startFields) {
        this.startFields = startFields;
    }

    /**
     * PUBLIC:
     * Sets the name of the start field.
     * <p>
     * By default all tables belonging to a descriptor have the same primary
     * key field names, and so the same start field names also.
     * <p>
     * However, if <code>startFieldName</code> is qualified, i.e. of the form
     * "EMPLOYEE_HIST.EMP_START", then this call will only set the start field
     * name for a single historical table.
     */
    public void addStartFieldName(String startFieldName) {
        DatabaseField startField = new DatabaseField(startFieldName);
        startField.setType(ClassConstants.TIMESTAMP);

        if (startFields == null) {
            startFields = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance();
            startFields.add(startField);
            return;
        }

        for (DatabaseField existing : startFields) {
            if (startField.getTableName().equals(existing.getTableName())) {
                existing.setName(startField.getName());
                return;
            }
        }
        startFields.add(startField);
    }

    /**
     * ADVANCED:
     * Sets the type of all start fields.  Not required to be set as the default
     * of Timestamp is assumed.
     */
    public void setStartFieldType(Class type) {
        for (DatabaseField existing : startFields) {
            existing.setType(type);
        }
    }

    /**
     * INTERNAL:
     */
    protected void setEndFields(List<DatabaseField> endFields) {
        this.endFields = endFields;
    }

    /**
     * PUBLIC:
     * @see #addStartFieldName
     */
    public void addEndFieldName(String endFieldName) {
        DatabaseField endField = new DatabaseField(endFieldName);
        endField.setType(ClassConstants.TIMESTAMP);

        if (endFields == null) {
            endFields = new ArrayList();
            endFields.add(endField);
            return;
        }

        for (DatabaseField existing : endFields) {
            if (endField.getTableName().equals(existing.getTableName())) {
                existing.setName(endField.getName());
                return;
            }
        }
        endFields.add(endField);
    }

    /**
     * ADVANCED:
     * @see #setStartFieldType
     */
    public void setEndFieldType(String fieldName, Class type) {
        for (DatabaseField existing : endFields) {
            existing.setType(type);
        }
    }

    /**
     * Sets if TopLink is responsible for writing history.
     * <p>
     * If history is maintained via low level database triggers or application
     * logic a policy is still needed for point in time querying.
     * <p>
     * If Oracle flashback is used no HistoryPolicy is needed.
     * <p>
     * Setting this to false lets you use History for many other applications.
     * For instance a table that tracks available flights or hotel deals may
     * benefit from a HistoryPolicy just to simplify temporal querying.
     * <p>If all hotel discounts have a start and end date, you could query on
     * all discounts available at a certain date.
     */
    public void setShouldHandleWrites(boolean value) {
        this.shouldHandleWrites = value;
    }

    /**
     * Answers if TopLink is responsible for writing history.
     * <p>
     * If history is maintained via low level database triggers or application
     * logic a policy is still usefull for point in time querying.
     * <p>
     * If Oracle flashback is used no HistoryPolicy is needed.
     * @return true by default
     * @see #setShouldHandleWrites
     */
    public boolean shouldHandleWrites() {
        return shouldHandleWrites;
    }

    /**
     * Sets if the Timestamp used in maintainaing history should be the
     * current time according to the database.
     * @param value if false uses localTime (default) instead
     */
    public void setShouldUseDatabaseTime(boolean value) {
        usesLocalTime = !value;
    }

    /**
     * Answers if the Timestamp used in maintaining history should be
     * System.currentTimeMillis();
     * @see #shouldUseDatabaseTime
     * @see #useLocalTime
     * @return true by default
     */
    public boolean shouldUseLocalTime() {
        return usesLocalTime;
    }

    /**
     * Answers if the Timestamp used in maintaining history should be the
     * current time according to the database.
     * @see #shouldUseLocalTime
     * @see #useDatabaseTime
     * @return false by default
     */
    public boolean shouldUseDatabaseTime() {
        return !usesLocalTime;
    }

    /**
     * Answers if the Timestamp used in maintaining history should be
     * System.currentTimeMillis();
     * @see #useDatabaseTime
     * @see #shouldUseLocalTime
     */
    public void useLocalTime() {
        usesLocalTime = true;
    }

    /**
     * Answers if the Timestamp used in maintaining history should be the
     * current time according to the database.
     * @see #useLocalTime
     * @see #shouldUseDatabaseTime
     */
    public void useDatabaseTime() {
        usesLocalTime = false;
    }

    /**
     * INTERNAL: Check that the qualifiers on the historical tables are
     * properly set.
     * <p>A similar method exists on ClassDescriptor.
     */
    protected void verifyTableQualifiers(DatasourcePlatform platform) {
        String tableQualifier = platform.getTableQualifier();

        if (tableQualifier.length() == 0) {
            return;
        }

        for (DatabaseTable table : getHistoricalTables()) {
            // Build a scratch table to see if history table name has a qualifier.
            DatabaseTable scratchTable = new DatabaseTable(table.getQualifiedName());
            if (scratchTable.getTableQualifier().length() == 0) {
                scratchTable.setTableQualifier(tableQualifier);
                ((HistoricalDatabaseTable)table).setHistoricalName(scratchTable.getQualifiedNameDelimited(platform));
            }
        }
    }

    /**
     * INTERNAL:
     * Checks for the case where an object has multiple tables but only some
     * are part of a minimal update.
     */
    protected boolean checkWastedVersioning(AbstractRecord modifyRow, DatabaseTable table) {
        for (Enumeration fieldsEnum = modifyRow.keys(); fieldsEnum.hasMoreElements();) {
            DatabaseField field = (DatabaseField)fieldsEnum.nextElement();
            if (field.getTable().equals(table) || (!field.hasTableName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * INTERNAL:
     */
    public void postDelete(ModifyQuery deleteQuery) {
        logicalDelete(deleteQuery, false);
    }
    
    /**
     * INTERNAL:
     */
    // Bug 319276 - pass whether shallow insert/update
    public void postUpdate(ObjectLevelModifyQuery writeQuery) {
        postUpdate(writeQuery, false);
    }

    /**
     * INTERNAL:
     */
    public void postUpdate(ObjectLevelModifyQuery writeQuery, boolean isShallow) {
        logicalDelete(writeQuery, true, isShallow);
        logicalInsert(writeQuery, true);
    }

    /**
     * INTERNAL:
     */
    public void postInsert(ObjectLevelModifyQuery writeQuery) {
        logicalInsert(writeQuery, false);
    }

    /**
     * INTERNAL:
     * Perform a logical insert into the historical schema, creating a new version
     * of an object.
     * <p>Called by postInsert() and also postUpdate() (which first does a logicalDelete
     * of the previous version).
     */
    public void logicalInsert(ObjectLevelModifyQuery writeQuery, boolean isUpdate) {
        ClassDescriptor descriptor = getDescriptor();
        AbstractRecord modifyRow = null;
        AbstractRecord originalModifyRow = writeQuery.getModifyRow();
        Object currentTime = null;
        if (isUpdate) {
            modifyRow = descriptor.getObjectBuilder().buildRow(writeQuery.getObject(), writeQuery.getSession(), WriteType.UPDATE); // Bug 319276
            // If anyone added items to the modify row, then they should also be added here.
            modifyRow.putAll(originalModifyRow);
        } else {
            modifyRow = originalModifyRow;
            // If update would have already discovered timestamp to use.
            currentTime = getCurrentTime(writeQuery.getSession());
        }
        StatementQueryMechanism insertMechanism = new StatementQueryMechanism(writeQuery);

        for (int i = 0; i < getHistoricalTables().size(); i++) {
            DatabaseTable table = getHistoricalTables().get(i);
            if (isUpdate && !checkWastedVersioning(originalModifyRow, table)) {
                continue;
            }
            if (!isUpdate) {
                modifyRow.add(getStart(i), currentTime);
            }
            SQLInsertStatement insertStatement = new SQLInsertStatement();
            insertStatement.setTable(table);
            insertMechanism.getSQLStatements().add(insertStatement);
        }
        if (insertMechanism.hasMultipleStatements()) {
            writeQuery.setTranslationRow(modifyRow);
            writeQuery.setModifyRow(modifyRow);
            insertMechanism.insertObject();
        }
    }

    /**
     * INTERNAL:
     * Performs a logical insert into the historical schema.  Direct
     * collections and many to many mappings are maintained through the session
     * events.
     */
    public void mappingLogicalInsert(DataModifyQuery originalQuery, AbstractRecord arguments, AbstractSession session) {
        DataModifyQuery historyQuery = new DataModifyQuery();
        SQLInsertStatement historyStatement = new SQLInsertStatement();
        DatabaseTable histTable = getHistoricalTables().get(0);

        historyStatement.setTable(histTable);
        AbstractRecord modifyRow = originalQuery.getModifyRow().clone();
        AbstractRecord translationRow = arguments.clone();

        // Start could be the version field in timestamp locking.
        if (!modifyRow.containsKey(getStart())) {
            Object time = getCurrentTime(session);
            modifyRow.add(getStart(), time);
            translationRow.add(getStart(), time);
        }
        historyQuery.setSQLStatement(historyStatement);
        historyQuery.setModifyRow(modifyRow);
        historyStatement.setModifyRow(modifyRow);
        session.executeQuery(historyQuery, translationRow);
    }
    
    /**
     * INTERNAL:
     * Performs a logical delete (update) on the historical schema.
     */
    // Bug 319276 - pass whether shallow insert/update
    public void logicalDelete(ModifyQuery writeQuery, boolean isUpdate) {
        logicalDelete(writeQuery, isUpdate, false);
    }

    /**
     * INTERNAL:
     * Performs a logical delete (update) on the historical schema.
     */
    public void logicalDelete(ModifyQuery writeQuery, boolean isUpdate, boolean isShallow) {
        ClassDescriptor descriptor = writeQuery.getDescriptor();
        AbstractRecord originalModifyRow = writeQuery.getModifyRow();
        AbstractRecord modifyRow = new DatabaseRecord();
        StatementQueryMechanism updateMechanism = new StatementQueryMechanism(writeQuery);
        Object currentTime = getCurrentTime(writeQuery.getSession());

        for (int i = 0; i < getHistoricalTables().size(); i++) {
            DatabaseTable table = getHistoricalTables().get(i);

            if (isUpdate && !checkWastedVersioning(originalModifyRow, table)) {
                continue;
            }
            SQLUpdateStatement updateStatement = new SQLUpdateStatement();
            updateStatement.setTable(table);
            Expression whereClause = null;
            if (writeQuery instanceof DeleteAllQuery) {
                if (writeQuery.getSelectionCriteria() != null) {
                    whereClause = (Expression)writeQuery.getSelectionCriteria().clone();
                }
            } else {
                whereClause = descriptor.getObjectBuilder().buildPrimaryKeyExpression(table);
            }
            ExpressionBuilder builder = ((whereClause == null) ? new ExpressionBuilder() : whereClause.getBuilder());
            whereClause = builder.getField(getEnd(i)).isNull().and(whereClause);
            updateStatement.setWhereClause(whereClause);

            modifyRow.add(getEnd(i), currentTime); 

            // save a little time here and add the same timestamp value for
            // the start field in the logicalInsert.
            if (isUpdate) {
                if (isShallow) {
                    // Bug 319276 - increment the timestamp by 1 to avoid unique constraint violation potential 
                    java.sql.Timestamp  incrementedTime = (java.sql.Timestamp) currentTime;
                    incrementedTime.setTime(incrementedTime.getTime() + getMinimumTimeIncrement(writeQuery.getSession()));
                    originalModifyRow.add(getStart(i), incrementedTime); 
                } else {
                    originalModifyRow.add(getStart(i), currentTime);
                }
            }
            updateMechanism.getSQLStatements().add(updateStatement);
        }
        if (updateMechanism.hasMultipleStatements()) {
            writeQuery.setModifyRow(modifyRow);
            updateMechanism.updateObject();
            writeQuery.setModifyRow(originalModifyRow);
        }
    }

    /**
     * INTERNAL:
     * Performs a logical delete (update) on the historical schema.  Direct
     * collections and many to many mappings are maintained through the session
     * events.
     */
    public void mappingLogicalDelete(ModifyQuery originalQuery, AbstractRecord arguments, AbstractSession session) {
        SQLDeleteStatement originalStatement = (SQLDeleteStatement)originalQuery.getSQLStatement();

        DataModifyQuery historyQuery = new DataModifyQuery();
        SQLUpdateStatement historyStatement = new SQLUpdateStatement();
        DatabaseTable histTable = getHistoricalTables().get(0);

        historyStatement.setTable(histTable);
        Expression whereClause = (Expression)originalStatement.getWhereClause().clone();
        DatabaseField endField = getEnd();
        whereClause = whereClause.getBuilder().getField(endField).isNull().and(whereClause);
        historyStatement.setWhereClause(whereClause);
        AbstractRecord modifyRow = new DatabaseRecord();
        AbstractRecord translationRow = arguments.clone();
        Object time = getCurrentTime(session);
        modifyRow.add(getEnd(), time);
        translationRow.add(getEnd(), time);
        historyStatement.setModifyRow(modifyRow);
        historyQuery.setSQLStatement(historyStatement);
        historyQuery.setModifyRow(modifyRow);
        session.executeQuery(historyQuery, translationRow);
    }
}
