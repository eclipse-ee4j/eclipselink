/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     02/08/2012-2.4 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
//     06/20/2012-2.5 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
//     07/13/2012-2.5 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
//     08/24/2012-2.5 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
//     09/27/2012-2.5 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
package org.eclipse.persistence.queries;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.sessions.DatabaseRecord;

/**
 * <p>
 * <b>Purpose</b>: Concrete class to perform read using raw SQL and the SQLResultSetMapping.
 *
 * <p>
 * <b>Responsibilities</b>: Execute a selecting raw SQL string. Returns a List of results. Each item in the list will be
 * another list consisting of the expected populated return types in the order they were specified in the
 * SQLResultSetMapping
 *
 * @see SQLResultSetMapping
 * @author Gordon Yorke
 * @since TopLink Java Essentials
 */
public class ResultSetMappingQuery extends ObjectBuildingQuery {

    private static final long serialVersionUID = 1L;

    protected boolean isExecuteCall;
    protected boolean returnNameValuePairs;

    // Unused: nothing in this class reads it and nothing subclasses this class. Parameterized
    // rather than removed, because it is protected and so visible to any extender.
    protected Vector<DatabaseRecord> resultRows;

    protected List<String> resultSetMappingNames = new ArrayList<>();
    protected List<SQLResultSetMapping> resultSetMappings = new ArrayList<>();

    /**
     * The type each row is read into, when the caller named a result class rather than a result set
     * mapping. It stands in for a mapping, and the mapping it stands for cannot be worked out until
     * the query has run - see {@link #buildResultSetMappingForResultClass}.
     */
    protected Class<?> resultClass;

    /**
     * PUBLIC: Initialize the state of the query.
     */
    public ResultSetMappingQuery() {
        super();
    }

    /**
     * PUBLIC: Initialize the query to use the specified call.
     */
    public ResultSetMappingQuery(Call call) {
        this();
        setCall(call);
    }

    /**
     * PUBLIC: Initialize the query to use the specified call and SQLResultSetMapping
     */
    public ResultSetMappingQuery(Call call, String sqlResultSetMappingName) {
        this();
        setCall(call);
        this.resultSetMappingNames.add(sqlResultSetMappingName);
    }

    /**
     * PUBLIC: This will be the SQLResultSetMapping that is used by this query to process the database results
     */
    public void addSQLResultSetMapping(SQLResultSetMapping resultSetMapping) {
        this.resultSetMappings.add(resultSetMapping);
        this.resultSetMappingNames.add(resultSetMapping.getName());
    }

    /**
     * PUBLIC: Add a SQLResultSetMapping that is used by this query to process the database results.
     */
    public void addSQLResultSetMappingName(String name) {
        if (name == null) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("null_sqlresultsetmapping_in_query"));
        }

        resultSetMappingNames.add(name);
    }

    /**
     * INTERNAL:
     * <P>
     * This method is called by the object builder when building an original. It will cause the original to be cached in the
     * query results if the query is set to do so.
     */
    @Override
    public void cacheResult(Object unwrappedOriginal) {
        Object cachableObject = unwrappedOriginal;
        if (shouldUseWrapperPolicy()) {
            cachableObject = getSession().wrapObject(unwrappedOriginal);
        }

        setTemporaryCachedQueryResults(cachableObject);
    }

    /**
     * INTERNAL: Convert all the class-name-based settings in this ResultSetMapping to actual class-based settings. This
     * method is used when converting a project that has been built with class names to a project with classes.
     */
    @Override
    public void convertClassNamesToClasses(ClassLoader classLoader) {
        for (SQLResultSetMapping mapping : this.resultSetMappings) {
            mapping.convertClassNamesToClasses(classLoader);
        }
    }

    /**
     * Indicates whether or not to return populated DatabaseRecord(s) as opposed to raw data when an SQLResultSetMapping is
     * not set.
     */
    public boolean shouldReturnNameValuePairs() {
        return returnNameValuePairs;
    }

    /**
     * Set the flag that indicates whether or not to return populated DatabaseRecord(s) as opposed to raw data when an
     * SQLResultSetMapping is not set.
     */
    public void setShouldReturnNameValuePairs(boolean returnNameValuePairs) {
        this.returnNameValuePairs = returnNameValuePairs;
    }

    /**
     * PUBLIC: This will be the SQLResultSetMapping that is used by this query to process the database results
     */
    public void setSQLResultSetMapping(SQLResultSetMapping resultSetMapping) {
        addSQLResultSetMapping(resultSetMapping);
    }

    /**
     * PUBLIC: This will be the SQLResultSetMappings that are used by this query to process the database results
     */
    public void setSQLResultSetMappings(List<SQLResultSetMapping> resultSetMappings) {
        this.resultSetMappings = resultSetMappings;
    }

    /**
     * PUBLIC: This will be the SQLResultSetMapping that is used by this query to process the database results
     */
    public void setSQLResultSetMappingName(String name) {
        addSQLResultSetMappingName(name);
    }

    /**
     * PUBLIC: This will be the SQLResult
     */
    public void setSQLResultSetMappingNames(List<String> names) {
        if (names.isEmpty()) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("null_sqlresultsetmapping_in_query"));
        }

        this.resultSetMappingNames = names;
    }

    /**
     * INTERNAL: This method is used to build the results. Interpreting the SQLResultSetMapping(s).
     */
    public List<Object> buildObjectsFromRecords(List<?> databaseRecords) {
        if (getSQLResultSetMappings().size() <= 1) {
            return buildObjectsFromRecords(databaseRecords, getSQLResultSetMapping());
        }

        int numberOfRecords = databaseRecords.size();
        List<Object> results = new ArrayList<>(numberOfRecords);

        for (int recordIndex = 0; recordIndex < numberOfRecords; recordIndex++) {
            Object records = databaseRecords.get(recordIndex);

            if (records instanceof Map<?, ?> recordsMap) {
                // We have a map keyed on named ref_cursors

                for (Object list : recordsMap.values()) {
                    results.add(buildObjectsFromRecords((List<?>) list, getSQLResultSetMappings().get(recordIndex)));
                    recordIndex++;
                }
            } else {
                // Regular list of records, iterate through them.
                results.add(buildObjectsFromRecords((List<?>) records, getSQLResultSetMappings().get(recordIndex)));
            }
        }

        return results;
    }

    /**
     * INTERNAL: This method is used to build the results with the SQLResultSetMapping at the given index.
     */
    public List<Object> buildObjectsFromRecords(List<?> databaseRecords, int index) {
        if (getSQLResultSetMappings().isEmpty()) {
            return buildObjectsFromRecords(databaseRecords, null);
        }

        return buildObjectsFromRecords(databaseRecords, getSQLResultSetMappings().get(index));
    }

    /**
     * INTERNAL: This method is used to build the results. Interpreting the SQLResultSetMapping.
     */
    protected List<Object> buildObjectsFromRecords(List<?> databaseRecords, SQLResultSetMapping mapping) {
        int numberOfRecords = databaseRecords.size();
        List<Object> results = new ArrayList<>(numberOfRecords);

        if ((mapping == null) && (this.resultClass != null) && !databaseRecords.isEmpty()) {
            SQLResultSetMapping resultClassMapping =
                    buildResultSetMappingForResultClass((DatabaseRecord) databaseRecords.get(0));

            if (resultClassMapping != null) {
                return buildObjectsFromRecords(databaseRecords, resultClassMapping);
            }
        }

        if (mapping == null) {
            if (shouldReturnNameValuePairs()) {
                // Returned as it stands, so callers keep getting the same list of DatabaseRecords
                // they always did. Safe because this method only ever reads from the argument.
                @SuppressWarnings("unchecked")
                List<Object> nameValuePairs = (List<Object>) databaseRecords;
                return nameValuePairs;
            }
            for (Iterator<?> iterator = databaseRecords.iterator(); iterator.hasNext();) {
                DatabaseRecord record = (DatabaseRecord) iterator.next();
                results.add(record.values().toArray());
            }
        } else {
            for (Iterator<?> iterator = databaseRecords.iterator(); iterator.hasNext();) {
                if (mapping.getResults().size() > 1) {
                    Object[] resultElement = new Object[mapping.getResults().size()];
                    DatabaseRecord record = (DatabaseRecord) iterator.next();
                    for (int i = 0; i < mapping.getResults().size(); i++) {
                        resultElement[i] = mapping.getResults().get(i).getValueFromRecord(record, this);
                    }
                    results.add(resultElement);
                } else if (mapping.getResults().size() == 1) {
                    DatabaseRecord record = (DatabaseRecord) iterator.next();
                    results.add(mapping.getResults().get(0).getValueFromRecord(record, this));
                } else {
                    return results;
                }
            }
        }

        return results;
    }

    /**
     * INTERNAL: Executes the prepared query on the datastore.
     */
    @Override
    public Object executeDatabaseQuery() throws DatabaseException {
        if (getSession().isUnitOfWork()) {
            UnitOfWorkImpl unitOfWork = (UnitOfWorkImpl) getSession();

            // Note if a nested unit of work this will recursively start a
            // transaction early on the parent also.
            if (isLockQuery()) {
                if ((!unitOfWork.getCommitManager().isActive()) && (!unitOfWork.wasTransactionBegunPrematurely())) {
                    unitOfWork.beginTransaction();
                    unitOfWork.setWasTransactionBegunPrematurely(true);
                }
            }
            if (unitOfWork.isNestedUnitOfWork()) {
                // execute in parent UOW then register normally here.
                UnitOfWorkImpl nestedUnitOfWork = (UnitOfWorkImpl) getSession();
                setSession(nestedUnitOfWork.getParent());
                Object result = executeDatabaseQuery();
                setSession(nestedUnitOfWork);
                Object clone = registerIndividualResult(result, null, unitOfWork, null, null);

                if (shouldUseWrapperPolicy()) {
                    clone = getDescriptor().getObjectBuilder().wrapObject(clone, unitOfWork);
                }
                return clone;
            }
        }
        session.validateQuery(this);// this will update the query with any settings

        if (getQueryId() == 0) {
            setQueryId(getSession().getNextQueryId());
        }

        if (getCall().isExecuteUpdate()) {
            DatabaseCall call = ((StoredProcedureCall) getQueryMechanism().execute());
            setExecutionTime(System.currentTimeMillis());
            return call;
        }

        Vector<?> rows = getQueryMechanism().executeSelect();
        setExecutionTime(System.currentTimeMillis());

        // If using 1-m joins, must set all rows.
        return buildObjectsFromRecords(rows);
    }

    /**
     * INTERNAL:
     * Build the result set mapping implied by this query's {@linkplain #getResultClass result
     * class}, using the columns of a row that the query actually returned.
     * <p>
     * A result class is a shorthand for a mapping, but which mapping it stands for depends on the
     * result set: a class is read as a constructor result whose arguments are all of the columns, so
     * how many columns there are, and what they are called, decides which constructor is meant.
     * Neither is known until the query has run, which is why this is built here rather than when the
     * query is created.
     *
     * @param row a row this query returned, whose fields describe the shape of the result set
     * @return the mapping to read every row with, or {@code null} to read rows as {@code Object[]}
     */
    protected SQLResultSetMapping buildResultSetMappingForResultClass(DatabaseRecord row) {
        // Object[] is a request for the row as it stands, which is what a query with no mapping at
        // all already returns, so there is nothing to build
        if (this.resultClass == ClassConstants.AOBJECT) {
            return null;
        }

        SQLResultSetMapping mapping = new SQLResultSetMapping(this.resultClass.getName());

        if (isScalarResultClass(this.resultClass)) {
            // A basic type reads a single column, and the column carries the type so that the value
            // is converted to what the caller asked for rather than handed over as the driver
            // returned it
            DatabaseField column = row.getFields().get(0).clone();
            column.setType(this.resultClass);
            mapping.addResult(new ColumnResult(column));
        }
        else {
            // Anything else is a constructor result over all of the columns. The columns are typed
            // from the constructor's own parameters, so that a driver returning a Long where the
            // constructor takes an Integer is converted rather than failing to match
            Constructor<?> constructor = findConstructor(row.getFields().size());
            ConstructorResult constructorResult = new ConstructorResult(this.resultClass);
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            List<DatabaseField> fields = row.getFields();

            for (int index = 0; index < fields.size(); index++) {
                DatabaseField column = fields.get(index).clone();
                column.setType(parameterTypes[index]);
                constructorResult.addColumnResult(new ColumnResult(column));
            }

            mapping.addResult(constructorResult);
        }

        return mapping;
    }

    /**
     * INTERNAL:
     * Return the result class' constructor taking the given number of arguments.
     *
     * @param argumentCount the number of columns in the result set
     * @throws QueryException if the result class has no constructor of that arity, or has more than
     *         one, in which case which of them was meant is anybody's guess
     */
    private Constructor<?> findConstructor(int argumentCount) {
        Constructor<?> found = null;

        for (Constructor<?> candidate : this.resultClass.getDeclaredConstructors()) {
            if (candidate.getParameterCount() == argumentCount) {
                if (found != null) {
                    throw QueryException.exceptionWhileInitializingConstructor(
                            new NoSuchMethodException(
                                    "More than one constructor takes the " + argumentCount
                                            + " columns of the result set, so which of them was meant"
                                            + " cannot be determined. Use a constructor result to say"
                                            + " which columns map to which arguments."),
                            this, this.resultClass);
                }
                found = candidate;
            }
        }

        if (found == null) {
            throw QueryException.exceptionWhileInitializingConstructor(
                    new NoSuchMethodException(
                            "No constructor takes the " + argumentCount + " columns of the result set."
                                    + " A result class is read as a constructor result over all of the"
                                    + " columns, so it needs a constructor with one argument per column."),
                    this, this.resultClass);
        }

        found.setAccessible(true);
        return found;
    }

    /**
     * INTERNAL:
     * Return whether the given result class is a basic type, and so reads a single column of the
     * result set rather than being constructed from all of them.
     * <p>
     * The distinction cannot be left to whether the class happens to have a constructor of the right
     * arity: {@code String} has a single-argument constructor, so a single-column result set read
     * into a {@code String} would otherwise be constructed rather than converted, and a type such as
     * {@code Long} would be constructed from whatever single-argument constructor it happens to
     * declare. The types listed here are those the specification treats as basic.
     */
    private static boolean isScalarResultClass(Class<?> resultClass) {
        return resultClass.isPrimitive()
                || resultClass.isEnum()
                // byte[], Byte[], char[] and Character[] are basic types in their own right
                || resultClass.isArray()
                || resultClass == ClassConstants.STRING
                || resultClass == ClassConstants.CHAR
                || resultClass == ClassConstants.BOOLEAN
                || resultClass == ClassConstants.UUID
                || Number.class.isAssignableFrom(resultClass)
                // covers java.sql.Date, java.sql.Time and java.sql.Timestamp as well
                || java.util.Date.class.isAssignableFrom(resultClass)
                || java.util.Calendar.class.isAssignableFrom(resultClass)
                // covers the java.time types: Instant, LocalDate, LocalDateTime, LocalTime,
                // OffsetDateTime, OffsetTime, Year, YearMonth and ZonedDateTime
                || java.time.temporal.Temporal.class.isAssignableFrom(resultClass);
    }

    /**
     * PUBLIC:
     * Return the type each row of this query is read into, when a result class was named rather than
     * a result set mapping.
     */
    public Class<?> getResultClass() {
        return this.resultClass;
    }

    /**
     * PUBLIC:
     * Set the type each row of this query is read into. Used instead of a result set mapping; see
     * {@link #buildResultSetMappingForResultClass}.
     */
    public void setResultClass(Class<?> resultClass) {
        this.resultClass = resultClass;
    }

    /**
     * PUBLIC: Return true if there are results set mappings associated with this query.
     */
    public boolean hasResultSetMappings() {
        return !getSQLResultSetMappings().isEmpty();
    }

    /**
     * PUBLIC: Return true if this is a result set mapping query.
     */
    @Override
    public boolean isResultSetMappingQuery() {
        return true;
    }

    /**
     * INTERNAL: Prepare the receiver for execution in a session.
     */
    @Override
    protected void prepare() {
        if ((!shouldMaintainCache()) && shouldRefreshIdentityMapResult()) {
            throw QueryException.refreshNotPossibleWithoutCache(this);
        }

        getQueryMechanism().prepare();

        if (isExecuteCall) {
            getQueryMechanism().prepareExecute();
        } else {
            getQueryMechanism().prepareExecuteSelect();
        }
    }

    /**
     * PUBLIC: This will be the SQLResultSetMapping that is used by this query to process the database results
     */
    public SQLResultSetMapping getSQLResultSetMapping() {
        if (resultSetMappings.isEmpty()) {
            if (resultSetMappingNames.isEmpty()) {
                return null;
            } else {
                return getSession().getProject().getSQLResultSetMapping(resultSetMappingNames.get(0));
            }
        }

        return resultSetMappings.get(0);
    }

    /**
     * PUBLIC: This will be the SQLResultSetMapping that is used by this query to process the database results
     */
    public List<SQLResultSetMapping> getSQLResultSetMappings() {
        if (this.resultSetMappings.isEmpty()) {
            ArrayList<SQLResultSetMapping> list = new ArrayList<>();
            for (String resultSetMappingName : this.resultSetMappingNames) {
                list.add(getSession().getProject().getSQLResultSetMapping(resultSetMappingName));
            }

            return list;
        } else {
            return resultSetMappings;
        }
    }

    /**
     * PUBLIC: Return the result set mapping name.
     */
    public String getSQLResultSetMappingName() {
        return this.resultSetMappingNames.get(0);
    }

    /**
     * PUBLIC: Return the result set mapping name.
     */
    public List<String> getSQLResultSetMappingNames() {
        return this.resultSetMappingNames;
    }

    /**
     * PUBLIC: Set to true if you the actual jdbc result set returned from query execution. This will unprepare the query in
     * case it was executed previously for a getResultList() call instead (or vice versa)
     */
    public void setIsExecuteCall(boolean isExecuteCall) {
        this.isExecuteCall = isExecuteCall;

        // Force the query to prepare.
        setIsPrepared(false);
    }
}
