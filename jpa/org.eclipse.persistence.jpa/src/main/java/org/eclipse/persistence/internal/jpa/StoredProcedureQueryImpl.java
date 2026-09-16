/*
 * Copyright (c) 2012, 2024 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019, 2025 IBM Corporation. All rights reserved.
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
//     Zoltan NAGY & tware - updated support for MaxRows
//     11/01/2010-2.2 Guy Pelletier
//       - 322916: getParameter on Query throws NPE
//     11/09/2010-2.1 Michael O'Brien
//       - 329089: PERF: EJBQueryImpl.setParamenterInternal() move indexOf check inside non-native block
//     02/08/2012-2.4 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
//     06/20/2012-2.5 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
//     07/13/2012-2.5 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
//     08/24/2012-2.5 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
//     09/13/2012-2.5 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
//     09/27/2012-2.5 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
//     11/05/2012-2.5 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
//     08/23/2023: Tomas Kraus
//       - New Jakarta Persistence 3.2 Features
package org.eclipse.persistence.internal.jpa;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.CacheRetrieveMode;
import jakarta.persistence.CacheStoreMode;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.LockModeType;
import jakarta.persistence.LockTimeoutException;
import jakarta.persistence.Parameter;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.QueryFlushMode;
import jakarta.persistence.QueryTimeoutException;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Timeout;
import jakarta.persistence.metamodel.Type;
import jakarta.persistence.sql.ResultSetMapping;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.databaseaccess.DatabaseAccessor;
import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.internal.databaseaccess.DatasourceCall.ParameterType;
import org.eclipse.persistence.internal.databaseaccess.OutputParameterForCallableStatement;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.GenericTypes;
import org.eclipse.persistence.internal.jpa.querydef.ParameterExpressionImpl;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.mappings.converters.ConverterClass;
import org.eclipse.persistence.queries.DataReadQuery;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ResultSetMappingQuery;
import org.eclipse.persistence.queries.SQLResultSetMapping;
import org.eclipse.persistence.queries.StoredFunctionCall;
import org.eclipse.persistence.queries.StoredProcedureCall;

import static org.eclipse.persistence.config.QueryHints.CACHE_RETRIEVE_MODE;
import static org.eclipse.persistence.config.QueryHints.CACHE_STORE_MODE;
import static org.eclipse.persistence.config.QueryHints.QUERY_TIMEOUT;
import static org.eclipse.persistence.internal.jpa.ResultSetMappingTranslator.toEclipseLinkMapping;
import static org.eclipse.persistence.internal.localization.ExceptionLocalization.buildMessage;

/**
 * Concrete Jakarta Persistence query class. The JPA query wraps a StoredProcesureQuery which
 * is executed.
 */
public class StoredProcedureQueryImpl extends QueryImpl implements StoredProcedureQuery {
    protected boolean hasMoreResults;

    // Call will be returned from an execute. From it you can get the result set.
    protected DatabaseCall executeCall;

    protected Statement executeStatement;

    protected int executeResultSetIndex = -1;

    // If the procedure returns output cursor(s), we'll use them to satisfy
    // getResultList and getSingleResult calls so keep track of our index.
    protected int outputCursorIndex = -1;

    protected boolean isOutputCursorResultSet;

    // Set by close(). Persistence 4.0 requires every method to throw
    // IllegalStateException once the query has been closed.
    protected boolean isClosed;

    // Options influencing execution, keyed by option type so that adding an option
    // overwrites any existing option of the same type, as addOption specifies.
    // Insertion-ordered purely to keep getOptions() predictable when debugging.
    /**
     * Field name given to a stored function's return value. Only ever seen internally - the value is
     * read by position, and {@code getInternalParameters()} skips the slot - so the name simply has to
     * be recognisable in logs and not look like a positional argument.
     */
    private static final String FUNCTION_RESULT_FIELD_NAME = "FUNCTION_RESULT";

    private final Map<Class<?>, Option> options = new LinkedHashMap<>();

    /**
     * Base constructor for StoredProcedureQueryImpl. Initializes basic variables.
     */
    protected StoredProcedureQueryImpl(EntityManagerImpl entityManager) {
        super(entityManager);
    }

    /**
     * Create an StoredProcedureQueryImpl with a DatabaseQuery.
     */
    public StoredProcedureQueryImpl(DatabaseQuery query, EntityManagerImpl entityManager) {
        super(query, entityManager);
        // Inherit applicable hints from EntityManager
        inheritEntityManagerHints();
    }

    /**
     * Create an StoredProcedureQueryImpl with a query name.
     */
    public StoredProcedureQueryImpl(String name, EntityManagerImpl entityManager) {
        super(entityManager);
        this.queryName = name;
        // Inherit applicable hints from EntityManager
        inheritEntityManagerHints();
    }

    // ### Query construction

    /**
     * Build a ResultSetMappingQuery from a sql result set mapping name and a
     * stored procedure call.
     * <p>
     * This is called from a named stored procedure that employs result set
     * mapping name(s) which should be available from the session.
     */
    public static DatabaseQuery buildResultSetMappingNameQuery(List<String> resultSetMappingNames, StoredProcedureCall call) {
        ResultSetMappingQuery query = new ResultSetMappingQuery();
        call.setReturnMultipleResultSetCollections(call.hasMultipleResultSets() && ! call.isMultipleCursorOutputProcedure());
        query.setCall(call);
        query.setIsUserDefined(true);
        query.setSQLResultSetMappingNames(resultSetMappingNames);
        return query;
    }

    /**
     * Build a ResultSetMappingQuery from a sql result set mapping name and a
     * stored procedure call.
     * <p>
     * This is called from a named stored procedure that employs result set
     * mapping name(s) which should be available from the session.
     */
    public static DatabaseQuery buildResultSetMappingNameQuery(List<String> resultSetMappingNames, StoredProcedureCall call, Map<String, Object> hints, ClassLoader classLoader, AbstractSession session) {
        // apply any query hints
        DatabaseQuery hintQuery = applyHints(hints, buildResultSetMappingNameQuery(resultSetMappingNames, call) , classLoader, session);

        // apply any query arguments
        applyArguments(call, hintQuery);

        return hintQuery;
    }

    /**
     * Build a ResultSetMappingQuery from the sql result set mappings given
     *  a stored procedure call.
     * <p>
     * This is called from a named stored procedure query that employs result
     * class name(s). The resultSetMappings are build from these class name(s)
     * and are not available from the session.
     */
    public static DatabaseQuery buildResultSetMappingQuery(List<SQLResultSetMapping> resultSetMappings, StoredProcedureCall call) {
        ResultSetMappingQuery query = new ResultSetMappingQuery();
        call.setReturnMultipleResultSetCollections(call.hasMultipleResultSets() && ! call.isMultipleCursorOutputProcedure());
        query.setCall(call);
        query.setIsUserDefined(true);
        query.setSQLResultSetMappings(resultSetMappings);

        return query;
    }

    /**
     * Build a ResultSetMappingQuery from the sql result set mappings given
     *  a stored procedure call.
     * <p>
     * This is called from a named stored procedure query that employs result
     * class name(s). The resultSetMappings are build from these class name(s)
     * and are not available from the session.
     */
    public static DatabaseQuery buildResultSetMappingQuery(List<SQLResultSetMapping> resultSetMappings, StoredProcedureCall call, Map<String, Object> hints, ClassLoader classLoader, AbstractSession session) {
        // apply any query hints
        DatabaseQuery hintQuery = applyHints(hints, buildResultSetMappingQuery(resultSetMappings, call), classLoader, session);

        // apply any query arguments
        applyArguments(call, hintQuery);

        return hintQuery;
    }

    /**
     * Build a ReadAllQuery from a class and stored procedure call.
     */
    public static DatabaseQuery buildStoredProcedureQuery(Class<?> resultClass, StoredProcedureCall call, Map<String, Object> hints, ClassLoader classLoader, AbstractSession session) {
        DatabaseQuery query = new ReadAllQuery(resultClass);
        query.setCall(call);
        query.setIsUserDefined(true);

        // apply any query hints
        query = applyHints(hints, query, classLoader, session);

        // apply any query arguments
        applyArguments(call, query);

        return query;
    }

    /**
     * Build a DataReadQuery with the stored procedure call given.
     */
    public static DatabaseQuery buildStoredProcedureQuery(StoredProcedureCall call, Map<String, Object> hints, ClassLoader classLoader, AbstractSession session) {
        DataReadQuery query = new DataReadQuery();
        query.setResultType(DataReadQuery.AUTO);

        query.setCall(call);
        query.setIsUserDefined(true);

        // apply any query hints
        DatabaseQuery hintQuery = applyHints(hints, query, classLoader, session);

        // apply any query arguments
        applyArguments(call, hintQuery);

        return hintQuery;
    }

    /**
     * Build a ResultSetMappingQuery from a sql result set mapping name and a
     * stored procedure call.
     */
    public static DatabaseQuery buildStoredProcedureQuery(String sqlResultSetMappingName, StoredProcedureCall call, Map<String, Object> hints, ClassLoader classLoader, AbstractSession session) {
        ResultSetMappingQuery query = new ResultSetMappingQuery();
        query.setSQLResultSetMappingName(sqlResultSetMappingName);
        query.setCall(call);
        query.setIsUserDefined(true);

        // apply any query hints
        DatabaseQuery hintQuery = applyHints(hints, query, classLoader, session);

        // apply any query arguments
        applyArguments(call, hintQuery);

        return hintQuery;
    }

    // ### Registration

    @Override
    @SuppressWarnings({"rawtypes"})
    public StoredProcedureQuery registerStoredProcedureParameter(int position, Class type, ParameterMode mode) {
        verifyNotClosed();
        entityManager.verifyOpenWithSetRollbackOnly();
        StoredProcedureCall call = (StoredProcedureCall) getDatabaseQuery().getCall();

        switch (mode) {
            case IN:
                call.addUnamedArgument(String.valueOf(position), type);
                break;
            case OUT:
                call.addUnamedOutputArgument(String.valueOf(position), type);
                break;
            case INOUT:
                call.addUnamedInOutputArgument(String.valueOf(position), String.valueOf(position), type);
                break;
            case REF_CURSOR:
                call.useUnnamedCursorOutputAsResultSet(position);
                break;
        }

        // Force a re-calculate of the parameters.
        parameters = null;

        return this;
    }

    /**
     * Register a named parameter. When using parameter names, all parameters
     * must be registered in the order in which they occur in the parameter list
     * of the stored procedure.
     *
     * @param parameterName name of the parameter as registered or
     *        specified in metadata
     * @param type type of the parameter
     * @param mode parameter mode
     * @return the same query instance
     */
    @Override
    @SuppressWarnings({"rawtypes"})
    public StoredProcedureQuery registerStoredProcedureParameter(String parameterName, Class type, ParameterMode mode) {
        verifyNotClosed();
        entityManager.verifyOpenWithSetRollbackOnly();
        StoredProcedureCall call = (StoredProcedureCall) getDatabaseQuery().getCall();

        switch (mode) {
            case IN:
                call.addNamedArgument(parameterName, parameterName, type);
                break;
            case OUT:
                call.addNamedOutputArgument(parameterName, parameterName, type);
                break;
            case INOUT:
                call.addNamedInOutputArgument(parameterName, parameterName, parameterName, type);
                break;
            case REF_CURSOR:
                call.useNamedCursorOutputAsResultSet(parameterName);
                break;
        }

        // Force a re-calculate of the parameters.
        parameters = null;

        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Parameter<T> registerParameter(int position, Class<T> type, ParameterMode mode) {
        verifyNotClosed();
        registerStoredProcedureParameter(position, type, mode);

        return (Parameter<T>) getParameter(position);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Parameter<T> registerParameter(String parameterName, Class<T> type, ParameterMode mode) {
        verifyNotClosed();
        registerStoredProcedureParameter(parameterName, type, mode);

        return (Parameter<T>) getParameter(parameterName);
    }

    @Override
    public <T> Parameter<T> registerConvertedParameter(int position, Class<? extends AttributeConverter<T, ?>> converter, ParameterMode mode) {
        verifyNotClosed();
        @SuppressWarnings("unchecked")
        var converterInstance = (AttributeConverter<Object, Object>) buildConverter(converter).getAttributeConverter();

        registerStoredProcedureParameter(position, getDomainType(converterInstance), mode);
        parameterConverters.put(String.valueOf(position), converterInstance);

        @SuppressWarnings("unchecked")
        var parameter = (Parameter<T>) getParameter(position);

        return parameter;
    }

    @Override
    public <T> Parameter<T> registerConvertedParameter(String parameterName, Class<? extends AttributeConverter<T, ?>> converter, ParameterMode mode) {
        verifyNotClosed();
        @SuppressWarnings("unchecked")
        var converterInstance = (AttributeConverter<Object, Object>) buildConverter(converter).getAttributeConverter();

        registerStoredProcedureParameter(parameterName, getDomainType(converterInstance), mode);
        parameterConverters.put(parameterName, converterInstance);

        @SuppressWarnings("unchecked")
        var parameter = (Parameter<T>) getParameter(parameterName);

        return parameter;
    }

    @Override
    public <T> Parameter<T> registerResultParameter(Class<T> resultType) {
        verifyNotClosed();
        entityManager.verifyOpenWithSetRollbackOnly();

        return new FunctionReturnImpl<>(asFunctionCall(getCall(), resultType));
    }

    // ### Configuration

    /**
     * Set a query property or hint. The hints elements may be used to specify
     * query properties and hints. Properties defined by this specification must
     * be observed by the provider. Vendor-specific hints that are not
     * recognized by a provider must be silently ignored. Portable applications
     * should not rely on the standard timeout hint. Depending on the database
     * in use, this hint may or may not be observed.
     *
     * @param hintName name of the property or hint
     * @param value value for the property or hint
     * @return the same query instance
     * @throws IllegalArgumentException if the second argument is not valid for
     * the implementation
     */
    @Override
    public StoredProcedureQuery setHint(String hintName, Object value) {
        verifyNotClosed();
        try {
            entityManager.verifyOpen();
            setHintInternal(hintName, value);
            return this;
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    /**
     * Set the flush mode type to be used for the query execution.
     * The flush mode type applies to the query regardless of the
     * flush mode type in use for the entity manager.
     * @param flushMode flush mode
     * @return the same query instance
     */
    @Override
    @Deprecated(since = "6.0")
    public StoredProcedureQueryImpl setFlushMode(FlushModeType flushMode) {
        verifyNotClosed();
        super.setFlushMode(flushMode);
        return this;
    }

    /**
     * Set the {@linkplain QueryFlushMode query flush mode} to be used for the
     * query execution. This flush mode overrides the flush mode of the entity
     * manager.
     *
     * @param queryFlushMode The new flush mode
     * @return the same query instance
     */
    @Override
    public StoredProcedureQueryImpl setQueryFlushMode(QueryFlushMode queryFlushMode) {
        verifyNotClosed();
        super.setQueryFlushMode(queryFlushMode);

        // getOptions() has to report the flush mode too: "The returned set includes options set via
        // addOption, along with options specified via setTimeout or setQueryFlushMode."
        recordOption(queryFlushMode);
        return this;
    }

    /**
     * Set the lock mode type to be used for the query execution.
     *
     * @throws IllegalStateException
     *             if not a Java Persistence query language SELECT query
     */
    @Override
    public StoredProcedureQueryImpl setLockMode(LockModeType lockMode) {
        verifyNotClosed();
        return (StoredProcedureQueryImpl) super.setLockMode(lockMode);
    }

    /**
     * Set the position of the first result to retrieve.
     *
     * @param startPosition
     *            position of the first result, numbered from 0
     * @return the same query instance
     */
    @Override
    public StoredProcedureQueryImpl setFirstResult(int startPosition) {
        verifyNotClosed();
        throw new IllegalStateException(ExceptionLocalization.buildMessage("operation_not_supported", new Object[]{"setFirstResult", "StoredProcedureQuery"}));
    }

    /**
     * Set the maximum number of results to retrieve.
     *
     * @return the same query instance
     */
    @Override
    public StoredProcedureQueryImpl setMaxResults(int maxResult) {
        verifyNotClosed();
        throw new IllegalStateException(ExceptionLocalization.buildMessage("operation_not_supported", new Object[]{"setMaxResults", "StoredProcedureQuery"}));
    }

    @Override
    public CacheRetrieveMode getCacheRetrieveMode() {
        verifyNotClosed();
        return FindOptionUtils.getCacheRetrieveMode(entityManager.getAbstractSession(), getDatabaseQuery().getProperties());
    }

    @Override
    public StoredProcedureQueryImpl setCacheRetrieveMode(CacheRetrieveMode cacheRetrieveMode) {
        verifyNotClosed();
        FindOptionUtils.setCacheRetrieveMode(getDatabaseQuery().getProperties(), cacheRetrieveMode);
        setHint(CACHE_RETRIEVE_MODE, cacheRetrieveMode);
        return this;
    }

    @Override
    public CacheStoreMode getCacheStoreMode() {
        verifyNotClosed();
        return FindOptionUtils.getCacheStoreMode(entityManager.getAbstractSession(), getDatabaseQuery().getProperties());
    }

    @Override
    public StoredProcedureQueryImpl setCacheStoreMode(CacheStoreMode cacheStoreMode) {
        verifyNotClosed();
        FindOptionUtils.setCacheStoreMode(getDatabaseQuery().getProperties(), cacheStoreMode);
        setHint(CACHE_STORE_MODE, cacheStoreMode);
        return this;
    }

    @Override
    public Integer getTimeout() {
        verifyNotClosed();
        return FindOptionUtils.getTimeout(entityManager.getAbstractSession(), getDatabaseQuery().getProperties());
    }

    @Override
    public StoredProcedureQueryImpl setTimeout(Integer timeout) {
        verifyNotClosed();
        FindOptionUtils.setTimeout(getDatabaseQuery().getProperties(), timeout);
        setHint(QUERY_TIMEOUT, timeout);
        return this;
    }

    @Override
    public StoredProcedureQuery setTimeout(Timeout timeout) {
        verifyNotClosed();
        setTimeout(timeout.milliseconds());
        recordOption(timeout);
        return this;
    }

    @Override
    public StoredProcedureQuery addOption(Option option) {
        verifyNotClosed();

        // Options that have a dedicated setter must still take effect when they arrive this way,
        // otherwise addOption(Timeout) would register the option without applying the timeout.
        if (option instanceof Timeout timeout) {
            return setTimeout(timeout);
        }

        if (option instanceof QueryFlushMode queryFlushMode) {
            return setQueryFlushMode(queryFlushMode);
        }

        recordOption(option);
        return this;
    }

    @Override
    public Set<Option> getOptions() {
        verifyNotClosed();

        // "Mutation of the returned set does not affect the options of the stored procedure."
        return new HashSet<>(options.values());
    }

    /*
     * The overrides below add nothing but the closed check. Persistence 4.0 requires that "after
     * invocation of close(), every method of the StoredProcedureQuery throws IllegalStateException",
     * and these methods are inherited from QueryImpl, which is shared with plain queries and has no
     * close contract of its own. Overriding here keeps that contract where it belongs rather than
     * pushing a notion of closing into the shared base class.
     *
     * getResultStream() needs no override: it is a default method on Query that delegates to
     * getResultList(), which does check.
     */

    @Override
    public FlushModeType getFlushMode() {
        verifyNotClosed();
        return super.getFlushMode();
    }

    @Override
    public QueryFlushMode getQueryFlushMode() {
        verifyNotClosed();
        return super.getQueryFlushMode();
    }

    @Override
    public LockModeType getLockMode() {
        verifyNotClosed();
        return super.getLockMode();
    }

    @Override
    public int getFirstResult() {
        verifyNotClosed();
        return super.getFirstResult();
    }

    @Override
    public int getMaxResults() {
        verifyNotClosed();
        return super.getMaxResults();
    }

    @Override
    public Map<String, Object> getHints() {
        verifyNotClosed();
        return super.getHints();
    }

    @Override
    public <T> T unwrap(Class<T> cls) {
        verifyNotClosed();
        return super.unwrap(cls);
    }

    // ### Parameter binding

    /**
     * Bind an argument to a positional parameter.
     *
     * @return the same query instance
     * @throws IllegalArgumentException if position does not correspond to a
     * positional parameter of the query or if the argument is of incorrect type
     */
    @Override
    public StoredProcedureQuery setParameter(int position, Object value) {
        verifyNotClosed();
        try {
            entityManager.verifyOpen();
            setParameterInternal(position, value);
            return this;
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    @Override
    public StoredProcedureQuery setParameter(String name, Object value) {
        verifyNotClosed();
        try {
            entityManager.verifyOpen();
            setParameterInternal(name, value, false);
            return this;
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    @Override
    public <P> StoredProcedureQuery setParameter(int position, P value, Class<P> type) {
        verifyNotClosed();
        try {
            entityManager.verifyOpen();
            setParameterInternal(String.valueOf(position), value, type, true);
            return this;
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    @Override
    public <P> StoredProcedureQuery setParameter(String name, P value, Class<P> type) {
        verifyNotClosed();
        try {
            entityManager.verifyOpen();
            setParameterInternal(name, value, type, false);
            return this;
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    @Override
    public <P> StoredProcedureQuery setParameter(int position, P value, Type<P> type) {
        verifyNotClosed();
        try {
            entityManager.verifyOpen();
            setParameterInternal(String.valueOf(position), value, type.getJavaType(), true);
            return this;
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    @Override
    public <P> StoredProcedureQuery setParameter(String name, P value, Type<P> type) {
        verifyNotClosed();
        try {
            entityManager.verifyOpen();
            setParameterInternal(name, value, type.getJavaType(), false);
            return this;
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    /**
     * Bind the value of a Parameter object.
     *
     * @return the same query instance
     * @throws IllegalArgumentException if the parameter does not correspond to
     * a parameter of the query
     */
    @Override
    public <T> StoredProcedureQuery setParameter(Parameter<T> param, T value) {
        verifyNotClosed();
        verifyNotNull(param);

        //bug 402686: type validation
        String parameterNameOrPosition = getParameterId(param);
        Parameter<?> parameter = getInternalParameters().get(parameterNameOrPosition);
        if (parameter == null ) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("NO_PARAMETER_WITH_NAME", param.toString(), databaseQuery));
        }

        if (!parameter.getParameterType().equals(param.getParameterType())) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("INCORRECT_PARAMETER_TYPE", parameterNameOrPosition, param.getParameterType()));
        }

        return setParameter(parameterNameOrPosition, value);
    }

    /**
     * Bind an instance of java.util.Calendar to a positional parameter.
     *
     * @return the same query instance
     * @throws IllegalArgumentException if position does not correspond to a
     * positional parameter of the query or if the value argument is of
     * incorrect type
     */
    @Override
    public StoredProcedureQuery setParameter(int position, Calendar value, TemporalType temporalType) {
        verifyNotClosed();
        entityManager.verifyOpenWithSetRollbackOnly();
        return setParameter(position, convertTemporalType(value, temporalType));
    }

    /**
     * Bind an instance of java.util.Date to a positional parameter.
     *
     * @return the same query instance
     * @throws IllegalArgumentException if position does not correspond to a
     * positional parameter of the query or if the value argument is of
     * incorrect type
     */
    @Override
    public StoredProcedureQuery setParameter(int position, Date value, TemporalType temporalType) {
        verifyNotClosed();
        entityManager.verifyOpenWithSetRollbackOnly();
        return setParameter(position, convertTemporalType(value, temporalType));
    }

    @Override
    public StoredProcedureQuery setParameter(String name, Calendar value, TemporalType temporalType) {
        verifyNotClosed();
        entityManager.verifyOpenWithSetRollbackOnly();
        return setParameter(name, convertTemporalType(value, temporalType));
    }

    @Override
    public StoredProcedureQuery setParameter(String name, Date value, TemporalType temporalType) {
        verifyNotClosed();
        entityManager.verifyOpenWithSetRollbackOnly();
        return setParameter(name, convertTemporalType(value, temporalType));
    }

    /**
     * Bind an instance of java.util.Calendar to a Parameter object.
     *
     * @return the same query instance
     * @throws IllegalArgumentException if the parameter does not correspond to
     * a parameter of the query
     */
    @Override
    public StoredProcedureQuery setParameter(Parameter<Calendar> param, Calendar value, TemporalType temporalType) {
        verifyNotClosed();
        verifyNotNull(param);

        //bug 402686: type validation
        String parameterNameOrPosition = getParameterId(param);
        verySameType(param, getInternalParameters().get(parameterNameOrPosition), parameterNameOrPosition);

        return setParameter(parameterNameOrPosition, value, temporalType);
    }

    /**
     * Bind an instance of java.util.Date to a Parameter object.
     *
     * @return the same query instance
     * @throws IllegalArgumentException if the parameter does not correspond to
     * a parameter of the query
     */
    @Override
    public StoredProcedureQuery setParameter(Parameter<Date> param, Date value, TemporalType temporalType) {
        verifyNotClosed();
        verifyNotNull(param);

        //bug 402686: type validation
        String parameterNameOrPosition = getParameterId(param);
        verySameType(param, getInternalParameters().get(parameterNameOrPosition), parameterNameOrPosition);

        return setParameter(parameterNameOrPosition, value, temporalType);
    }

    @Override
    public <P> StoredProcedureQuery setConvertedParameter(String name, P value, Class<? extends AttributeConverter<P, ?>> converter) {
        verifyNotClosed();
        var converterInstance = converterInstance(converter);

        if (convertsEagerly(name, converterInstance)) {
            return
                setParameter(
                    name,
                    // Convert to relational type right here
                    converterInstance.convertToDatabaseColumn(value),
                    // Record in the parameter we are of the relational type
                    asObjectClass(getRelationalType(converterInstance)));
        }

        // Register the converter which does the same conversion as above, but at the time
        // we execute the procedure, i.e. the lazy conversion
        parameterConverters.put(name, converterInstance);

        return
            setParameter(
                name,
                // Set the original domain value
                value,
                // Record in the parameter we are of the domain type
                asObjectClass(getDomainType(converterInstance)));
    }

    @Override
    public <P> StoredProcedureQuery setConvertedParameter(int position, P value, Class<? extends AttributeConverter<P, ?>> converter) {
        verifyNotClosed();
        var converterInstance = converterInstance(converter);

        if (convertsEagerly(String.valueOf(position), converterInstance)) {
            return setParameter(position, converterInstance.convertToDatabaseColumn(value),
                    asObjectClass(getRelationalType(converterInstance)));
        }

        parameterConverters.put(String.valueOf(position), converterInstance);

        return setParameter(position, value, asObjectClass(getDomainType(converterInstance)));
    }

    @Override
    public StoredProcedureQuery setParameters(Object... arguments) {
        verifyNotClosed();
        for (int i=0; i<arguments.length; i++) {
            setParameter(i, arguments[i]);
        }

        return this;
    }

    // ### Parameter introspection

    /**
     * {@inheritDoc}
     * <p>
     * Overridden only to honour the Persistence 4.0 rule that a closed stored procedure query throws
     * {@code IllegalStateException}. {@code QueryImpl} is shared with plain queries, which have no
     * such contract, so the check belongs here.
     */
    @Override
    public Set<Parameter<?>> getParameters() {
        verifyNotClosed();
        return super.getParameters();
    }


    @Override
    public Parameter<?> getParameter(String name) {
        verifyNotClosed();
        return super.getParameter(name);
    }

    @Override
    public Parameter<?> getParameter(int position) {
        verifyNotClosed();
        return super.getParameter(position);
    }

    @Override
    public <T> Parameter<T> getParameter(String name, Class<T> type) {
        verifyNotClosed();
        return super.getParameter(name, type);
    }

    @Override
    public <T> Parameter<T> getParameter(int position, Class<T> type) {
        verifyNotClosed();
        return super.getParameter(position, type);
    }

    @Override
    public <T> T getParameterValue(Parameter<T> param) {
        verifyNotClosed();
        return super.getParameterValue(param);
    }

    @Override
    public Object getParameterValue(String name) {
        verifyNotClosed();
        return super.getParameterValue(name);
    }

    @Override
    public Object getParameterValue(int position) {
        verifyNotClosed();
        return super.getParameterValue(position);
    }

    @Override
    public boolean isBound(Parameter<?> param) {
        verifyNotClosed();
        return super.isBound(param);
    }

    // ### Execution

    /**
     * Returns true if the first result corresponds to a result set, and false
     * if it is an update count or if there are no results other than through
     * INOUT and OUT parameters, if any.
     * @return true if first result corresponds to result set
     * @throws QueryTimeoutException if the query execution exceeds the query
     * timeout value set and only the statement is rolled back
     * @throws PersistenceException if the query execution exceeds the query
     * timeout value set and the transaction is rolled back
     */
    @Override
    public boolean execute() {
        verifyNotClosed();
        try {
            entityManager.verifyOpen();

            if (!getDatabaseQueryInternal().isResultSetMappingQuery()) {
                throw new IllegalStateException(ExceptionLocalization.buildMessage("incorrect_spq_query_for_execute"));
            }

            getResultSetMappingQuery().setIsExecuteCall(true);
            executeCall = (DatabaseCall) executeReadQuery();
            executeStatement = executeCall.getStatement();

            // Add this query to the entity manager open queries list.
            // The query will be closed in the following cases:
            // Within a transaction:
            //  - on commit
            //  - on rollback
            // Outside of a transaction:
            //  - em close
            // Other safeguards, we will close the query if/when
            //  - we hit the end of the results.
            //  - this query is garbage collected (finalize method)
            //
            // Deferring closing the call avoids having to go through all the
            // results now (and building all the result objects) and things
            // remain on a as needed basis from the statement.
            entityManager.addOpenQuery(this);

            hasMoreResults = executeCall.getExecuteReturnValue();

            // If execute returned false but we have output cursors then return
            // true and build the results from the output cursors.
            if (!hasMoreResults && getCall().hasOutputCursors()) {
                hasMoreResults = true;
                outputCursorIndex = 0;
                isOutputCursorResultSet = true;
            }

            return hasMoreResults;
        } catch (LockTimeoutException exception) {
            throw exception;
        } catch (PersistenceException | IllegalStateException exception) {
            setRollbackOnly();
            throw exception;
        } catch (RuntimeException exception) {
            setRollbackOnly();
            throw new PersistenceException(exception);
        }
    }

    /**
     * Execute an update or delete statement (from a stored procedure query).
     * @return the number of entities updated or deleted
     */
    @Override
    public int executeUpdate() {
        verifyNotClosed();
        try {
            // Need to throw TransactionRequiredException if there is no active transaction
            entityManager.checkForTransaction(true);

            // Legacy: we could have a data read query or a read all query, so
            // clearly we shouldn't be executing an update on it. As of JPA 2.1
            // API we always create a result set mapping query to interact with
            // a stored procedure.
            // Also if the result set mapping query has result set mappings
            // defined, then it's clearly expecting result sets and we can be
            // preemptive in throwing an exception.
            if (! getDatabaseQueryInternal().isResultSetMappingQuery() || getResultSetMappingQuery().hasResultSetMappings()) {
                throw new IllegalStateException(ExceptionLocalization.buildMessage("incorrect_spq_query_for_execute_update"));
            }

            // If the return value is true indicating a result set then throw an exception.
            if (execute()) {
                throw new IllegalStateException(ExceptionLocalization.buildMessage("incorrect_spq_query_for_execute_update"));
            } else {
                return getUpdateCount();
            }
        } catch (LockTimeoutException exception) {
            throw exception;
        } catch (PersistenceException | IllegalStateException e) {
            setRollbackOnly();
            throw e;
        } catch (RuntimeException exception) {
            setRollbackOnly();
            throw new PersistenceException(exception);
        } finally {
            releaseConnections(); // Free the JDBC resources; the query stays usable.
        }
    }

    // ### Results

    @SuppressWarnings("unchecked")
    /**
     * Execute the query and return the query results as a List.
     * @return a list of the results
     */
    @Override
    public List<Object> getResultList() {
        verifyNotClosed();
        // bug51411440: need to throw IllegalStateException if query
        // executed on closed em
        this.entityManager.verifyOpenWithSetRollbackOnly();
        try {
            // If there is no execute statement, the user has not called
            // execute and is simply calling getResultList directly on the query.
            if (executeStatement == null) {

                // If it's not a result set mapping query (as of Jakarta Persistence 2.1 we
                // always create a result set mapping query to interact with a
                // stored procedure) then throw an exception.
                if (!getDatabaseQueryInternal().isResultSetMappingQuery()) {
                    throw new IllegalStateException(ExceptionLocalization.buildMessage("incorrect_spq_query_for_get_result_list"));
                }

                // If the return value is false indicating no result set then throw an exception.
                if (!execute()) {
                    throw new IllegalStateException(ExceptionLocalization.buildMessage("incorrect_spq_query_for_get_result_list"));
                }

                return getResultList();
            }

            if (!hasMoreResults()) {
                return null;
            }

            if (isOutputCursorResultSet) {
                // Return result set list for the current outputCursorIndex.
                List<Object> results = null;
                if (hasPositionalParameters()) {
                    results = (List<Object>) getOutputParameterValue(getCall().getOutputCursors().get(outputCursorIndex++).getIndex() + 1);
                } else {
                    results = (List<Object>) getOutputParameterValue(getCall().getOutputCursors().get(outputCursorIndex++).getName());
                }

                // Update the hasMoreResults flag.
                hasMoreResults = (outputCursorIndex < getCall().getOutputCursors().size());

                return results;
            }

            // Build the result records first.
            List<?> result = buildResultRecords(executeStatement.getResultSet());

            // Move the result pointer.
            moveResultPointer();

            return getResultSetMappingQuery().buildObjectsFromRecords(result, ++executeResultSetIndex);

        } catch (LockTimeoutException e) {
            throw e;
        } catch (PersistenceException | IllegalStateException e) {
            setRollbackOnly();
            throw e;
        } catch (Exception e) {
            setRollbackOnly();
            throw new PersistenceException(e);
        }
    }

    /**
     * Apply a programmatic result set mapping to this query, translating it into the form EclipseLink
     * consumes. The counterpart of {@link #applyResultClass}, and safe for the same reason: nothing
     * has executed at the point these methods are called.
     */
    private void applyResultSetMapping(ResultSetMapping<?> mapping) {
        DatabaseQuery query = getDatabaseQuery();

        if (!(query instanceof ResultSetMappingQuery resultSetMappingQuery)) {
            throw new IllegalStateException(
                    "A result set mapping can only be applied to a stored procedure query that returns a"
                            + " result set, but this query is a " + query.getClass().getSimpleName() + ".");
        }

        resultSetMappingQuery.setSQLResultSetMappings(List.of(toEclipseLinkMapping(mapping)));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> List<R> getResultList(Class<R> resultClass) {
        verifyNotClosed();
        applyResultClass(resultClass);
        return (List<R>) getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> List<R> getResultList(ResultSetMapping<R> mapping) {
        verifyNotClosed();
        applyResultSetMapping(mapping);
        return (List<R>) getResultList();
    }

    @Override
    public Object getSingleResult() {
        verifyNotClosed();
        return getSingleResult(true);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R getSingleResult(Class<R> resultClass) {
        verifyNotClosed();
        applyResultClass(resultClass);
        return (R) getSingleResult();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R getSingleResult(ResultSetMapping<R> mapping) {
        verifyNotClosed();
        applyResultSetMapping(mapping);
        return (R) getSingleResult();
    }

    @Override
    public Object getSingleResultOrNull() {
        verifyNotClosed();
        return getSingleResult(false);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R getSingleResultOrNull(Class<R> resultClass) {
        verifyNotClosed();
        applyResultClass(resultClass);
        return (R) getSingleResultOrNull();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R getSingleResultOrNull(ResultSetMapping<R> mapping) {
        verifyNotClosed();
        applyResultSetMapping(mapping);
        return (R) getSingleResultOrNull();
    }

    /**
     * Returns true if the next result corresponds to a result set, and false if
     * it is an update count or if there are no results other than through INOUT
     * and OUT parameters, if any.
     *
     * @return true if next result corresponds to result set
     * @throws QueryTimeoutException if the query execution exceeds the query
     * timeout value set and only the statement is rolled back
     * @throws PersistenceException if the query execution exceeds the query
     * timeout value set and the transaction is rolled back
     */
    @Override
    public boolean hasMoreResults() {
        verifyNotClosed();
        entityManager.verifyOpen();

        return hasMoreResults;
    }

    /**
     * Returns the update count or -1 if there is no pending result
     * or if the next result is not an update count.
     * @return update count or -1 if there is no pending result or
     * if the next result is not an update count
     * @throws QueryTimeoutException if the query execution exceeds
     * the query timeout value set and only the statement is
     * rolled back
     * @throws PersistenceException if the query execution exceeds
     * the query timeout value set and the transaction
     * is rolled back
     */
    @Override
    public int getUpdateCount() {
        verifyNotClosed();
        entityManager.verifyOpenWithSetRollbackOnly();

        if (executeStatement != null) {
            try {
                int updateCount = executeStatement.getUpdateCount();

                // Moving the result pointer when -1 is reached doesn't seem
                // to be an issue for the jbdc driver, however as a safeguard,
                // once -1 is reached don't bother trying to move the pointer
                // as there is no need to do so.
                if (updateCount > -1) {
                    moveResultPointer();
                }
                return updateCount;
            } catch (SQLException e) {
                throw getDetailedException(DatabaseException.sqlException(e, executeCall, executeCall.getQuery().getAccessor(), executeCall.getQuery().getSession(), false));
            }
        }

        return -1;
    }

    // ### Output parameters

    /**
     * Used to retrieve the values passed back from the procedure through INOUT
     * and OUT parameters. For portability, all results corresponding to result
     * sets and update counts must be retrieved before the values of output
     * parameters.
     * @param position parameter position
     * @return the result that is passed back through the parameter
     * @throws IllegalArgumentException if the position does not correspond to a
     * parameter of the query or is not an INOUT or OUT parameter
     */
    @Override
    public Object getOutputParameterValue(int position) {
        verifyNotClosed();
        entityManager.verifyOpen();

        if (isValidCallableStatement()) {
            try {
                Object obj = executeCall.getOutputParameterValue((CallableStatement) executeStatement, position - 1, entityManager.getAbstractSession());

                if (!(obj instanceof ResultSet resultSet)) {
                    return obj;
                }

                // If a result set is returned we have to build the objects.
                return getResultSetMappingQuery().buildObjectsFromRecords(buildResultRecords(resultSet), ++executeResultSetIndex);

            } catch (Exception exception) {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("jpa21_invalid_parameter_position", new Object[] { position, exception.getMessage() }), exception);
            }
        }

        return null;
    }

    /**
     * Used to retrieve the values passed back from the procedure through INOUT and OUT parameters. For portability, all
     * results corresponding to result sets and update counts must be retrieved before the values of output parameters.
     *
     * @param parameterName name of the parameter as registered or specified in metadata
     * @return the result that is passed back through the parameter
     * @throws IllegalArgumentException if the parameter name does not correspond to a parameter of the query or is not an
     * INOUT or OUT parameter
     */
    @Override
    public Object getOutputParameterValue(String parameterName) {
        verifyNotClosed();
        entityManager.verifyOpen();

        if (isValidCallableStatement()) {
            try {
                Object obj = executeCall.getOutputParameterValue((CallableStatement) executeStatement, parameterName, entityManager.getAbstractSession());

                if (!(obj instanceof ResultSet resultSet)) {
                    return obj;
                }

                // If a result set is returned we have to build the objects.
                return getResultSetMappingQuery().buildObjectsFromRecords(buildResultRecords(resultSet), ++executeResultSetIndex);

            } catch (Exception exception) {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("jpa21_invalid_parameter_name", new Object[] { parameterName, exception.getMessage() }), exception);
            }
        }

        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getOutputParameterValue(Parameter<T> parameter) {
        verifyNotClosed();
        verifyNotNull(parameter);

        Integer position = parameter.getPosition();
        return (T) (position != null
                ? getOutputParameterValue(position)
                : getOutputParameterValue(parameter.getName()));
    }

    // ### Teardown

    /**
     * {@inheritDoc}
     * <p>
     * Note the difference between this and {@link #releaseConnections()}. Persistence 4.0 makes
     * {@code close()} terminal: afterwards every method of the query throws
     * {@code IllegalStateException}. EclipseLink also releases the JDBC resources after each
     * execution, which is housekeeping rather than an end to the query's life - {@code executeUpdate()}
     * does it and the caller is still expected to call {@code getUpdateCount()} afterwards. Those
     * internal call sites use {@code releaseConnections()} so that they free the resources without
     * making the query unusable.
     */
    @Override
    public void close() {
        releaseConnections();
        isClosed = true;
    }

    /**
     * Release the JDBC resources held by the last execution, leaving the query usable.
     */
    private void releaseConnections() {
        if (executeCall != null) {
            DatabaseQuery query = executeCall.getQuery();
            AbstractSession session = query.getSession();

            // Release the accessors acquired for the query.
            for (Accessor accessor : query.getAccessors()) {
                session.releaseReadConnection(accessor);
            }

            try {
                if (executeStatement != null) {
                    DatabaseAccessor accessor = (DatabaseAccessor) query.getAccessor();
                    accessor.releaseStatement(executeStatement, query.getSQLString(), executeCall, session);
                }
            } catch (SQLException exception) {
                // Catch the exception and log a message.
                session.log(SessionLog.WARNING, SessionLog.CONNECTION, "exception_caught_closing_statement", exception);
            }
        }

        executeCall = null;
        executeStatement = null;
    }

    /**
     * Finalize method in case the query is not closed.
     */
    @Override
    @SuppressWarnings("removal")
    public void finalize() {
        releaseConnections();
    }


    // ### Protected methods

    /**
     * Return the stored procedure call associated with this query.
     */
    protected StoredProcedureCall getCall() {
        return (StoredProcedureCall) getDatabaseQueryInternal().getCall();
    }

    /**
     * Return the ResultSetMappingQuery for this stored procedure query.
     * NOTE: Methods assumes associated database query is a ResultSetMappingQuery.
     */
    protected ResultSetMappingQuery getResultSetMappingQuery() {
        if (executeCall != null) {
            return (ResultSetMappingQuery) executeCall.getQuery();
        }

        return (ResultSetMappingQuery) getDatabaseQuery();
    }

    /**
     * Return the internal map of parameters.
     */
    @Override
    protected Map<String, Parameter<?>> getInternalParameters() {
        if (parameters == null) {
            parameters = new HashMap<>();

            int index = 0;

            for (Object parameter : getCall().getParameters()) {
                // Skip a stored function's return value, which occupies the first slot. It is not one
                // of the parameters the caller registered - registerResultParameter hands back a
                // FunctionReturnImpl for it - and it could not be represented here anyway: its
                // argument name is null, which sends it down the positional branch below, where the
                // field name has to parse as a number.
                if (index == 0 && getCall().isStoredFunctionCall()) {
                    ++index;
                    continue;
                }

                ParameterType parameterType = getCall().getParameterTypes().get(index);
                String argumentName = getCall().getProcedureArgumentNames().get(index);

                DatabaseField field = null;

                if (parameterType == ParameterType.INOUT) {
                    field = (DatabaseField) ((Object[]) parameter)[0];
                } else if (parameterType == ParameterType.IN) {
                    field = (DatabaseField) parameter;
                } else if (parameterType == ParameterType.OUT || parameterType == ParameterType.OUT_CURSOR) {
                    if (parameter instanceof OutputParameterForCallableStatement) {
                        field = ((OutputParameterForCallableStatement) parameter).getOutputField();
                    } else {
                        field = (DatabaseField) parameter;
                    }
                }

                // If field is not null (one we care about) then add it, otherwise continue.
                if (field != null) {
                    // If the argument name is null then it is a positional parameter.
                    if (argumentName == null) {
                        parameters.put(field.getName(), new ParameterExpressionImpl<>(null, field.getType(), Integer.parseInt(field.getName())));
                    } else {
                        parameters.put(field.getName(), new ParameterExpressionImpl<>(null, field.getType(), field.getName()));
                    }
                }

                ++index;
            }
        }

        return parameters;
    }

    @Override
    protected void setParameterInternal(String name, Object value, Class<?> type, boolean isIndex) {
        validateParameter(name, value, isIndex);

        parameterValues.put(name, value);

        if (type != null) {
            parameterTypes.put(name, type);
        }
    }

    /**
     * Inherit applicable query hints from the EntityManager.
     * EntityManager-level settings to newly created queries.
     */
    protected void inheritEntityManagerHints() {
        if (entityManager == null || entityManager.properties == null) {
            return;
        }

        DatabaseQuery dbQuery = getDatabaseQuery();
        if (dbQuery == null) {
            return;
        }

        Map<String, Object> entityManagerProperties = entityManager.properties;

        // CACHE_RETRIEVE_MODE only applies to ObjectLevelReadQuery (SELECT queries)
        if (dbQuery.isObjectLevelReadQuery() && entityManagerProperties.containsKey(CACHE_RETRIEVE_MODE)) {
            setHint(CACHE_RETRIEVE_MODE, entityManagerProperties.get(CACHE_RETRIEVE_MODE));
        }

        // CACHE_STORE_MODE applies to all query types:
        // - For ObjectLevelReadQuery: controls whether results are stored in cache after reading
        // - For ModifyQuery: controls whether cache is invalidated after UPDATE/DELETE
        if (entityManagerProperties.containsKey(CACHE_STORE_MODE)) {
            setHint(CACHE_STORE_MODE, entityManagerProperties.get(CACHE_STORE_MODE));
        }
    }

    /**
     * Returns true if the execute statement for this query is 1) not null (i.e.
     * query has been executed and 2) is an instance of callable statement,
     * meaning there are out parameters associated with it.
     */
    protected boolean isValidCallableStatement() {
        if (executeStatement == null) {
            throw new IllegalStateException(ExceptionLocalization.buildMessage("jpa21_invalid_call_on_un_executed_query"));
        }

        if (! (executeStatement instanceof CallableStatement)) {
            throw new IllegalStateException(ExceptionLocalization.buildMessage("jpa21_invalid_call_with_no_output_parameters"));
        }

        return true;
    }

    /**
     * Build the given result set into a list objects. Assumes there is an
     * execute call available and therefore should not be called unless an
     * execute statement was issued by the user.
     */
    protected List<?> buildResultRecords(ResultSet resultSet) {
        try {
            AbstractSession session = (AbstractSession) getActiveSession();
            DatabaseAccessor accessor = (DatabaseAccessor) executeCall.getQuery().getAccessor();

            executeCall.setFields(null);
            executeCall.matchFieldOrder(resultSet, accessor, session);
            ResultSetMetaData metaData = resultSet.getMetaData();

            List<AbstractRecord> result =  new Vector<>();
            while (resultSet.next()) {
                result.add(accessor.fetchRow(executeCall.getFields(), executeCall.getFieldsArray(), resultSet, metaData, session));
            }

            // The result set must be closed in case the statement is cached and not closed.
            resultSet.close();

            return result;
        } catch (Exception e) {
            setRollbackOnly();
            throw new PersistenceException(e);
        }
    }


    // ### Private methods

    /**
     * Return the converter instance named by one of the converted-parameter methods.
     */
    @SuppressWarnings("unchecked")
    private <P> AttributeConverter<Object, Object> converterInstance(Class<? extends AttributeConverter<P, ?>> converter) {
        return (AttributeConverter<Object, Object>) buildConverter(converter).getAttributeConverter();
    }

    /**
     * Return whether the given converter must be applied to the argument now, rather than recorded
     * and applied when the query executes.
     * <p>
     * Both are needed, because a converted argument can be bound to a parameter that was declared
     * either way. {@link #registerConvertedParameter} declares the parameter with the converter's
     * domain type, so the domain value the caller binds validates as it stands and the conversion can
     * wait until {@code processParameters()} flattens the arguments - which it must, since the caller
     * may bind through any of the plain {@code setParameter} methods and never mention the converter
     * again. A parameter declared separately, by {@link #registerStoredProcedureParameter} or in
     * metadata, instead names the type the procedure itself takes, which is the converter's relational
     * type. There the domain value cannot survive validation against the declared type, so it has to
     * be converted before being bound.
     * <p>
     * The two branches must stay mutually exclusive: converting here and also recording the converter
     * would convert the value twice, the second time handing the converter a value of the wrong type.
     */
    private boolean convertsEagerly(String name, AttributeConverter<Object, Object> converterInstance) {
        Parameter<?> parameter = getInternalParameters().get(name);

        if (parameter == null || parameter.getParameterType() == null) {
            // Unknown or untyped parameter; leave it to the normal validation to report.
            return false;
        }

        Class<?> declaredType = parameter.getParameterType();

        return
            !declaredType.isAssignableFrom(getDomainType(converterInstance)) &&
             declaredType.isAssignableFrom(getRelationalType(converterInstance));
    }

    /**
     * Build and initialize a {@link ConverterClass} for a converter class named by one of the
     * {@code setConvertedParameter}/{@code registerConvertedParameter} methods.
     * <p>
     * {@code ConverterClass} is declared as {@code ConverterClass<T extends AttributeConverter<X, Y>, X, Y>},
     * so it wants both the domain type and the relational type. The API hands us only
     * {@code Class<? extends AttributeConverter<P, ?>>}: the domain type is {@code P}, but the
     * relational type is a wildcard and so cannot be inferred - {@code new ConverterClass<>(...)}
     * fails with "cannot infer type arguments". The relational type is only recoverable at runtime,
     * by reflecting over the converter's type arguments, which is what
     * {@link #getRelationalType(AttributeConverter)} does. {@code Object} therefore stands in for it
     * here, and the cast is safe because nothing in this class depends on that parameter statically.
     */
    @SuppressWarnings("unchecked")
    private <P> ConverterClass<AttributeConverter<P, Object>, P, Object> buildConverter(Class<? extends AttributeConverter<P, ?>> converter) {
        return new ConverterClass<AttributeConverter<P, Object>, P, Object>((AbstractSession) getActiveSession(),
                (Class<AttributeConverter<P, Object>>) (Class<?>) converter);
    }

    /**
     * Return the domain (database) type of the given attribute converter, that is, the first
     * type argument of {@link AttributeConverter}. This is the type the caller supplies, as opposed
     * to the domain type the JDBC layer binds.
     * <p>
     * The type argument is not necessarily a class. A converter declared as, say,
     * {@code AttributeConverter<Foo, List<String>>} resolves to a {@link java.lang.reflect.ParameterizedType},
     * and one that leaves its relational type generic resolves to a {@link java.lang.reflect.TypeVariable}.
     * Neither names a type the JDBC layer can bind, so both are rejected here rather than failing
     * later as a ClassCastException with nothing to point at.
     *
     * @throws IllegalArgumentException if the converter does not name a concrete database type
     */
    private static Class<?> getDomainType(AttributeConverter<?, ?> attributeConverter) {
        java.lang.reflect.Type domainType =
                GenericTypes.getTypeArgument(attributeConverter, AttributeConverter.class, 0);

        if (!(domainType instanceof Class<?> relationalClass)) {
            throw new IllegalArgumentException("""
                    Unable to determine the domain type of attribute converter [%s]. \
                    The first type argument of AttributeConverter resolved to [%s], which is not a class. \
                    A converter used with a query parameter must name a concrete domain type."""
                    .formatted(
                        attributeConverter.getClass().getName(),
                        domainType));
        }

        return relationalClass;
    }

    /**
     * Return the relational (database) type of the given attribute converter, that is, the second
     * type argument of {@link AttributeConverter}. This is the type the JDBC layer binds, as opposed
     * to the domain type the caller supplies.
     * <p>
     * The type argument is not necessarily a class. A converter declared as, say,
     * {@code AttributeConverter<Foo, List<String>>} resolves to a {@link java.lang.reflect.ParameterizedType},
     * and one that leaves its relational type generic resolves to a {@link java.lang.reflect.TypeVariable}.
     * Neither names a type the JDBC layer can bind, so both are rejected here rather than failing
     * later as a ClassCastException with nothing to point at.
     *
     * @throws IllegalArgumentException if the converter does not name a concrete database type
     */
    private static Class<?> getRelationalType(AttributeConverter<?, ?> attributeConverter) {
        java.lang.reflect.Type relationalType =
                GenericTypes.getTypeArgument(attributeConverter, AttributeConverter.class, 1);

        if (!(relationalType instanceof Class<?> relationalClass)) {
            throw new IllegalArgumentException("""
                    Unable to determine the database type of attribute converter [%s]. \
                    The second type argument of AttributeConverter resolved to [%s], which is not a class. \
                    A converter used with a query parameter must name a concrete database type."""
                    .formatted(
                        attributeConverter.getClass().getName(),
                        relationalType));
        }

        return relationalClass;
    }

    /**
     * Widen a {@code Class<?>} to {@code Class<Object>} so that it can be handed to the generic
     * {@code setParameter(..., P value, Class<P> type)} methods together with a value whose static
     * type is no more than {@code Object}.
     * <p>
     * Running a value through an attribute converter loses its static type: the result is an
     * {@code Object} and the relational type is read back off the converter reflectively as a
     * {@code Class<?>}. There is therefore no {@code P} satisfying both {@code P value} and
     * {@code Class<P> type}, and the call is rejected with "no suitable method found". Widening both
     * sides to {@code Object} only discards a relationship the signature cannot express - the
     * {@code Class} instance passed on is the real relational type, so parameter validation and JDBC
     * binding are unaffected.
     */
    @SuppressWarnings("unchecked")
    private static Class<Object> asObjectClass(Class<?> type) {
        return (Class<Object>) type;
    }

    /**
     *
     *
     * @param call
     * @param resultType
     * @return
     */
    private <T> StoredFunctionCall asFunctionCall(StoredProcedureCall call, Class<T> resultType) {
        StoredFunctionCall storedFunctionCall = asFunctionCall(call);
        storedFunctionCall.setResult(FUNCTION_RESULT_FIELD_NAME, resultType);

        return storedFunctionCall;

    }

    /**
     * Return this query's call as a {@link StoredFunctionCall}, converting it if it is not one already.
     * <p>
     * A function needs the call to be a {@code StoredFunctionCall} because that class reserves its
     * first parameter for the return value, which is what produces the leading placeholder in
     * <code>{? = call f(...)}</code>. Nothing in the specification lets a caller ask for a function up
     * front - {@code createStoredProcedureQuery} always builds a plain {@link StoredProcedureCall} -
     * so {@code registerResultParameter} is always the point at which this becomes known, and by then
     * the call object already exists. Since an object cannot change its class, it is rebuilt.
     * <p>
     * The arguments registered so far are appended <em>behind</em> the reserved return slot rather
     * than copied in place, which is what shifts them to JDBC positions 2, 3 and so on. Only the
     * declaration state is copied: the query cannot have executed yet, so there is no result or
     * statement state to carry over.
     */
    private StoredFunctionCall asFunctionCall(StoredProcedureCall call) {
        if (call instanceof StoredFunctionCall functionCall) {
            return functionCall;
        }

        StoredFunctionCall functionCall = new StoredFunctionCall();
        functionCall.setProcedureName(call.getProcedureName());
        functionCall.setHasMultipleResultSets(call.hasMultipleResultSets());
        functionCall.getParameters().addAll(call.getParameters());
        functionCall.getParameterTypes().addAll(call.getParameterTypes());
        functionCall.getProcedureArgumentNames().addAll(call.getProcedureArgumentNames());
        functionCall.getOptionalArguments().addAll(call.getOptionalArguments());

        getDatabaseQuery().setCall(functionCall);

        // Every argument has moved one place along, and the cached Parameter objects carry their
        // positions, so they have to be rebuilt.
        parameters = null;

        return functionCall;
    }

    /**
     * Record an option, replacing any option of the same type.
     * <p>
     * "The same type" is the option's own class, except for enum options, where it is the enum class
     * rather than the constant's class - a constant with a class body is an anonymous subclass, and
     * keying on that would let two constants of one enum coexist.
     */
    private void recordOption(Option option) {
        options.put(option instanceof Enum<?> constant ? constant.getDeclaringClass() : option.getClass(), option);
    }

    private void validateParameter(String name, Object value, boolean isIndex) {
        Parameter<?> parameter = getInternalParameters().get(name);
        StoredProcedureCall call = (StoredProcedureCall) getDatabaseQuery().getCall();

        validateParameterNonNull(name, isIndex, parameter, call);
        validateParameterType(name, value, isIndex, parameter, call);
    }

    private void validateParameterNonNull(String name, boolean isIndex, Parameter<?> parameter, StoredProcedureCall call) {
        if (parameter == null) {
            if (isIndex) {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("ejb30-wrong-argument-index", name, call.getProcedureName()));
            }

            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("ejb30-wrong-argument-name", name, call.getProcedureName()));
        }
    }

    private void validateParameterType(String name, Object value, boolean isIndex, Parameter<?> parameter, StoredProcedureCall call) {
        if (!isValidActualParameter(value, parameter.getParameterType())) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("ejb30-incorrect-parameter-type", name, value.getClass(), parameter.getParameterType(), call.getProcedureName()));
        }
    }

    private boolean hasPositionalParameters() {
        for (Parameter<?> parameter: getParameters()) {
            if (parameter.getName() != null) {
                return false;
            }
        }

        return true;
    }

    /**
     * Apply a result class to this query, so that the rows the procedure returns are mapped to it.
     * <p>
     * A result class given to {@code createStoredProcedureQuery} becomes a {@link SQLResultSetMapping}
     * on the underlying {@link ResultSetMappingQuery}. The 4.0 methods that take the class at
     * retrieval time rather than at creation time need to do the same thing, just later - which is
     * safe because nothing has executed yet at that point. The query is cloned first if it is shared,
     * since this mutates it.
     */
    private void applyResultClass(Class<?> resultClass) {
        DatabaseQuery query = getDatabaseQuery();

        if (!(query instanceof ResultSetMappingQuery resultSetMappingQuery)) {
            throw new IllegalStateException(
                    "A result class can only be applied to a stored procedure query that returns a result set, but this"
                            + " query is a " + query.getClass().getSimpleName() + ".");
        }

        resultSetMappingQuery.setSQLResultSetMappings(List.of(new SQLResultSetMapping(resultClass)));
    }

    private Object getSingleResult(boolean failOnEmpty) {
        // bug51411440: need to throw IllegalStateException if query
        // executed on closed em
        this.entityManager.verifyOpenWithSetRollbackOnly();
        try {
            // If there is no execute statement, the user has not called
            // execute and is simply calling getSingleResult directly on the query.
            if (executeStatement == null) {
                // If it's not a result set mapping query (as of JPA 2.1 we
                // always create a result set mapping query to interact with a
                // stored procedure) then throw an exception.
                if (! getDatabaseQueryInternal().isResultSetMappingQuery()) {
                    throw new IllegalStateException(ExceptionLocalization.buildMessage("incorrect_spq_query_for_get_single_result"));
                }

                // If the return value is true indicating a result set then
                // build and return the single result.
                if (execute()) {
                    return getSingleResult(failOnEmpty);
                } else {
                    throw new IllegalStateException(ExceptionLocalization.buildMessage("incorrect_spq_query_for_get_result_list"));
                }
            } else {
                if (hasMoreResults()) {
                    // Build the result records first.
                    List<?> results;

                    if (isOutputCursorResultSet) {
                        // Return result set list for the current outputCursorIndex.
                        if (hasPositionalParameters()) {
                            results = (List<?>) getOutputParameterValue(getCall().getOutputCursors().get(outputCursorIndex++).getIndex() + 1);
                        } else {
                            results = (List<?>) getOutputParameterValue(getCall().getOutputCursors().get(outputCursorIndex++).getName());
                        }

                        // Update the hasMoreResults flag.
                        hasMoreResults = (outputCursorIndex < getCall().getOutputCursors().size());
                    } else {
                        // Build the result records first.
                        List<?> result = buildResultRecords(executeStatement.getResultSet());

                        // Move the result pointer.
                        moveResultPointer();

                        results = getResultSetMappingQuery().buildObjectsFromRecords(result, ++executeResultSetIndex);
                    }

                    if (results.size() > 1) {
                        throwNonUniqueResultException(ExceptionLocalization.buildMessage("too_many_results_for_get_single_result"));
                    } else if (results.isEmpty()) {
                        if (failOnEmpty) {
                            throwNoResultException(ExceptionLocalization.buildMessage("no_entities_retrieved_for_get_single_result"));
                        } else {
                            return null;
                        }
                    }
                    // If hasMoreResults is true, we should throw an exception here.
                    if (results.size() > 1 || hasMoreResults) {
                        throwNonUniqueResultException(ExceptionLocalization.buildMessage("too_many_results_for_get_single_result"));
                    }

                    return results.get(0);
                } else {
                    return null;
                }
            }
        } catch (LockTimeoutException e) {
            throw e;
        } catch (PersistenceException | IllegalStateException e) {
            setRollbackOnly();
            throw e;
        } catch (Exception e) {
            setRollbackOnly();
            throw new PersistenceException(e);
        } finally {
            releaseConnections(); // Free the JDBC resources; the query stays usable.
        }
    }

    /**
     * INTERNAL:
     * Move the pointer up and update our has more results flag.
     * Once there are no result sets left, this will always return false.
     */
    private void moveResultPointer() {
        try {
            hasMoreResults = executeStatement.getMoreResults();
        } catch (SQLException e) {
            // swallow it.
            hasMoreResults = false;
        }
    }

    /**
     * Throw if this query has been closed.
     *
     * @throws IllegalStateException if {@link #close()} has been called
     */
    private void verifyNotClosed() {
        if (isClosed) {
            throw new IllegalStateException("The stored procedure query has been closed.");
        }
    }

    private void verifyNotNull(Object param) {
        if (param == null) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("NULL_PARAMETER_PASSED_TO_SET_PARAMETER"));
        }
    }

    private void verySameType(Parameter<?> param, Parameter<?> parameter, Object position) {
        if (parameter == null ) {
            throw new IllegalArgumentException(buildMessage("NO_PARAMETER_WITH_NAME", param.toString(), databaseQuery));
        }

        if (!parameter.getParameterType().equals(param.getParameterType())) {
            throw new IllegalArgumentException(buildMessage("INCORRECT_PARAMETER_TYPE", position, param.getParameterType()));
        }
    }
}
