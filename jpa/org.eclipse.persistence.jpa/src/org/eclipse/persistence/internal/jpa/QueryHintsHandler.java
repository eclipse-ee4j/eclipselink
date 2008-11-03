/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
package org.eclipse.persistence.internal.jpa;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.sql.Time;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.exceptions.QueryException;


import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.config.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.invalidation.DailyCacheInvalidationPolicy;
import org.eclipse.persistence.descriptors.invalidation.TimeToLiveCacheInvalidationPolicy;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.queries.ObjectBuildingQuery;
import org.eclipse.persistence.queries.QueryRedirector;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.queries.ReadQuery;
import org.eclipse.persistence.queries.ReportQuery;

/**
 * The class processes query hints.
 * 
 * EclipseLink query hints and their values defined in org.eclipse.persistence.config package.
 * 
 * To add a new query hint:
 *   Define a new hint in QueryHints;
 *   Add a class containing hint's values if required to config package (like CacheUsage);
 *      Alternatively values defined in HintValues may be used - Refresh and BindParameters hints do that.
 *   Add an inner class to this class extending Hint corresponding to the new hint (like CacheUsageHint);
 *      The first constructor parameter is hint name; the second is default value;
 *      In constructor 
 *          provide 2-dimensional value array in case the values should be translated (currently all Hint classes do that);
 *              in case translation is not required provide a single-dimension array (no such examples yet).
 *   In inner class Hint static initializer addHint an instance of the new hint class (like addHint(new CacheUsageHint())).
 * 
 * @see QueryHints
 * @see HintValues
 * @see CacheUsage
 * @see PessimisticLock
 */
public class QueryHintsHandler {
    
    /**
     * Verifies the hints.
     * 
     * If session != null then logs a FINEST message for each hint.
     * queryName parameter used only for identifying the query in messages,
     * if it's null then "null" will be used.
     * Throws IllegalArgumentException in case the hint value is illegal.
     */
    public static void verify(Map hints, String queryName, AbstractSession session) {
        if(hints == null) {
            return;
        }
        Iterator it = hints.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            String hintName = (String)entry.getKey();
            verify(hintName, entry.getValue(), queryName, session);
        }
    }
    
    /**
     * Verifies the hint.
     * 
     * If session != null then logs a FINEST message.
     * queryName parameter used only for identifying the query in messages,
     * if it's null then "null" will be used.
     * Throws IllegalArgumentException in case the hint value is illegal.
     */
    public static void verify(String hintName, Object hintValue, String queryName, AbstractSession session) {
        Hint.verify(hintName, shouldUseDefault(hintValue), hintValue, queryName, session);
    }
    
    /**
     * Applies the hints to the query.
     * Throws IllegalArgumentException in case the hint value is illegal.
     */
    public static DatabaseQuery apply(Map hints, DatabaseQuery query) {
        if (hints == null) {
            return query;
        }
        DatabaseQuery hintQuery = query;
        Iterator iterator = hints.entrySet().iterator();
        while(iterator.hasNext()) {
            Map.Entry entry = (Map.Entry)iterator.next();
            String hintName = (String)entry.getKey();
            hintQuery = apply(hintName, entry.getValue(), hintQuery);
        }
        return hintQuery;
    }
    
    /**
     * Applies the hint to the query.
     * Throws IllegalArgumentException in case the hint value is illegal.
     */
    public static DatabaseQuery apply(String hintName, Object hintValue, DatabaseQuery query) {
        return Hint.apply(hintName, shouldUseDefault(hintValue), hintValue, query);
    }
    
    /**
     * Common hint value processing into an boolean value. If the hint is
     * null, false is returned. Those methods that need to handle a null hint
     * to be something other than false should not call this method.
     */
    public static boolean parseBooleanHint(Object hint) {
        if (hint == null) {
            return false;
        } else {
            return Boolean.valueOf(hint.toString());
        }
    }
    
    /**
     * Common hint value processing into an integer value. If the hint is
     * null, -1 is returned.
     */
    public static int parseIntegerHint(Object hint, String hintName) {
        if (hint == null) {
            return -1;
        } else {
            try {
                return Integer.parseInt(hint.toString());
            } catch (NumberFormatException e) {
                throw QueryException.queryHintContainedInvalidIntegerValue(hintName, hint, e);
            }
        }
    }
    
    /**
     * Empty String hintValue indicates that the default hint value
     * should be used.
     */
    protected static boolean shouldUseDefault(Object hintValue) {
        return (hintValue != null) &&  (hintValue instanceof String) && (((String)hintValue).length() == 0);
    }
    
    /**
     * Define a generic Hint.
     * Hints should subclass this and override the applyToDatabaseQuery and set the valueArray.
     */
    protected static abstract class Hint {
        static HashMap mainMap = new HashMap();
        Object[] valueArray;
        HashMap valueMap;
        String name;
        String defaultValue;
        Object defaultValueToApply;
        boolean valueToApplyMayBeNull;
        
        static {
            addHint(new BindParametersHint());
            addHint(new CacheUsageHint());
            addHint(new QueryTypeHint());
            addHint(new PessimisticLockHint());
            addHint(new PessimisticLockTimeoutHint());
            addHint(new RefreshHint());
            addHint(new CascadePolicyHint());
            addHint(new BatchHint());
            addHint(new FetchHint());
            addHint(new ReturnSharedHint());
            addHint(new JDBCTimeoutHint());
            addHint(new JDBCFetchSizeHint());
            addHint(new JDBCMaxRowsHint());
            addHint(new ResultCollectionTypeHint());
            addHint(new RedirectorHint());
            addHint(new QueryCacheHint());
            addHint(new QueryCacheSizeHint());
            addHint(new QueryCacheExpiryHint());
            addHint(new QueryCacheExpiryTimeOfDayHint());
        }
        
        Hint(String name, String defaultValue) {
            this.name = name;
            this.defaultValue = defaultValue;
        }

        abstract DatabaseQuery applyToDatabaseQuery(Object valueToApply, DatabaseQuery query);
                
        static void verify(String hintName, boolean shouldUseDefault, Object hintValue, String queryName, AbstractSession session) {
            Hint hint = (Hint)mainMap.get(hintName);
            if(hint == null) {
                if(session != null) {
                    session.log(SessionLog.FINEST, SessionLog.QUERY, "unknown_query_hint", new Object[]{getPrintValue(queryName), hintName});
                }
                return;
            }
                                    
            hint.verify(hintValue, shouldUseDefault, queryName, session);
        }
        
        void verify(Object hintValue, boolean shouldUseDefault, String queryName, AbstractSession session) {
            if(shouldUseDefault) {
                hintValue = defaultValue;
            }
            if(session != null) {
                session.log(SessionLog.FINEST, SessionLog.QUERY, "query_hint", new Object[]{getPrintValue(queryName), name, getPrintValue(hintValue)});
            }
            if(!shouldUseDefault && valueMap != null && !valueMap.containsKey(getUpperCaseString(hintValue))) {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("ejb30-wrong-query-hint-value",new Object[]{getPrintValue(queryName), name, getPrintValue(hintValue)}));
            }
        }
        
        static DatabaseQuery apply(String hintName, boolean shouldUseDefault, Object hintValue, DatabaseQuery query) {
            Hint hint = (Hint)mainMap.get(hintName);
            if (hint == null) {
                // unknown hint name - silently ignored.
                return query;
            }
            
            return hint.apply(hintValue, shouldUseDefault, query);
        }
        
        DatabaseQuery apply(Object hintValue, boolean shouldUseDefault, DatabaseQuery query) {
            Object valueToApply = hintValue;
            if(shouldUseDefault) {
                valueToApply = defaultValueToApply;
            } else {
                if(valueMap != null) {
                    String key = getUpperCaseString(hintValue);
                    valueToApply = valueMap.get(key);
                    if(valueToApply == null) {
                        boolean wrongKey = true;
                        if(valueToApplyMayBeNull) {
                            wrongKey = !valueMap.containsKey(key);
                        }
                        if(wrongKey) {
                            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("ejb30-wrong-query-hint-value",new Object[]{getQueryId(query), name, getPrintValue(hintValue)}));
                        }
                    }
                }
            }
            return applyToDatabaseQuery(valueToApply, query);
        }

        static String getQueryId(DatabaseQuery query) {
            String queryId = query.getName();
            if(queryId == null) {
                queryId = query.getEJBQLString();
            }
            return getPrintValue(queryId);
        }
        
        static String getPrintValue(Object hintValue) {
            return hintValue != null ? hintValue.toString() : "null";
        }
    
        static String getUpperCaseString(Object hintValue) {
            return hintValue != null ? hintValue.toString().toUpperCase() : null;
        }

        void initialize() {
            if(valueArray != null) {
                valueMap = new HashMap(valueArray.length);
                if(valueArray instanceof Object[][]) {
                    Object[][] valueArray2 = (Object[][])valueArray;
                    for(int i=0; i<valueArray2.length; i++) {
                        valueMap.put(getUpperCaseString(valueArray2[i][0]), valueArray2[i][1]);
                        if(valueArray2[i][1] == null) {
                            valueToApplyMayBeNull = true;
                        }
                    }
                } else {
                    for(int i=0; i<valueArray.length; i++) {
                        valueMap.put(getUpperCaseString(valueArray[i]), valueArray[i]);
                        if(valueArray[i] == null) {
                            valueToApplyMayBeNull = true;
                        }
                    }
                }
                defaultValueToApply = valueMap.get(defaultValue.toUpperCase());
            }
        }
        
        static void addHint(Hint hint) {
            hint.initialize();
            mainMap.put(hint.name, hint);
        }
    }

    protected static class BindParametersHint extends Hint {
        BindParametersHint() {
            super(QueryHints.BIND_PARAMETERS, HintValues.PERSISTENCE_UNIT_DEFAULT);
            valueArray = new Object[][] { 
                {HintValues.PERSISTENCE_UNIT_DEFAULT, null},
                {HintValues.TRUE, Boolean.TRUE},
                {HintValues.FALSE, Boolean.FALSE}
            };
        }
    
        DatabaseQuery applyToDatabaseQuery(Object valueToApply, DatabaseQuery query) {
            if (valueToApply == null) {
                query.ignoreBindAllParameters();
            } else {
                query.setShouldBindAllParameters(((Boolean)valueToApply).booleanValue());
            }
            return query;
        }
    }

    /**
     * Configure the cache usage of the query.
     * As many of the usages require a ReadObjectQuery, the hint may also require to change the query type.
     */
    protected static class CacheUsageHint extends Hint {
        CacheUsageHint() {
            super(QueryHints.CACHE_USAGE, CacheUsage.DEFAULT);
            valueArray = new Object[][] {
                {CacheUsage.UseEntityDefault, ObjectLevelReadQuery.UseDescriptorSetting},
                {CacheUsage.DoNotCheckCache, ObjectLevelReadQuery.DoNotCheckCache},
                {CacheUsage.CheckCacheByExactPrimaryKey, ObjectLevelReadQuery.CheckCacheByExactPrimaryKey},
                {CacheUsage.CheckCacheByPrimaryKey, ObjectLevelReadQuery.CheckCacheByPrimaryKey},
                {CacheUsage.CheckCacheThenDatabase, ObjectLevelReadQuery.CheckCacheThenDatabase},
                {CacheUsage.CheckCacheOnly, ObjectLevelReadQuery.CheckCacheOnly},
                {CacheUsage.ConformResultsInUnitOfWork, ObjectLevelReadQuery.ConformResultsInUnitOfWork}
            };
        }
    
        DatabaseQuery applyToDatabaseQuery(Object valueToApply, DatabaseQuery query) {
            if (query.isObjectLevelReadQuery()) {
                int cacheUsage = ((Integer)valueToApply).intValue();
                ((ObjectLevelReadQuery)query).setCacheUsage(cacheUsage);
                if (cacheUsage == ObjectLevelReadQuery.CheckCacheByExactPrimaryKey
                        || cacheUsage == ObjectLevelReadQuery.CheckCacheByPrimaryKey
                        || cacheUsage == ObjectLevelReadQuery.CheckCacheThenDatabase) {
                    ReadObjectQuery newQuery = new ReadObjectQuery();
                    newQuery.copyFromQuery(query);
                    return newQuery;
                }
            }
            return query;
        }
    }

    protected static class CascadePolicyHint extends Hint {
        CascadePolicyHint() {
            super(QueryHints.REFRESH_CASCADE, CascadePolicy.DEFAULT);
            valueArray = new Object[][] {
                {CascadePolicy.NoCascading, DatabaseQuery.NoCascading},
                {CascadePolicy.CascadePrivateParts, DatabaseQuery.CascadePrivateParts},
                {CascadePolicy.CascadeAllParts, DatabaseQuery.CascadeAllParts},
                {CascadePolicy.CascadeByMapping, DatabaseQuery.CascadeByMapping}
            };
        }
    
        DatabaseQuery applyToDatabaseQuery(Object valueToApply, DatabaseQuery query) {
            // this time cascade policy make sense only for read query with refresh option
            // However cascade policy is generic property for DatabaseQuery, 
            // therefore can have a meaning for other types of query in the future. 
            if (query.isObjectLevelReadQuery()) {
                query.setCascadePolicy((Integer)valueToApply);
            }
            return query;
        }
    }

    /**
     * Configure the type of the query.
     */
    protected static class QueryTypeHint extends Hint {
        QueryTypeHint() {
            super(QueryHints.QUERY_TYPE, QueryType.DEFAULT);
            valueArray = new Object[][] {
                {QueryType.Auto, QueryType.Auto},
                {QueryType.ReadAll, QueryType.ReadAll},
                {QueryType.ReadObject, QueryType.ReadObject},
                {QueryType.Report, QueryType.Report}
            };
        }
    
        DatabaseQuery applyToDatabaseQuery(Object valueToApply, DatabaseQuery query) {
            if (query.isObjectLevelReadQuery()) {
                if (valueToApply == QueryType.ReadAll) {
                    ReadAllQuery newQuery = new ReadAllQuery();
                    newQuery.copyFromQuery(query);
                    return newQuery;
                } else if (valueToApply == QueryType.ReadObject) {
                    ReadObjectQuery newQuery = new ReadObjectQuery();
                    newQuery.copyFromQuery(query);
                    return newQuery;
                } else if (valueToApply == QueryType.Report) {
                    ReportQuery newQuery = new ReportQuery();
                    newQuery.copyFromQuery(query);
                    return newQuery;
                }
            }
            return query;
        }
    }
    
    protected static class PessimisticLockHint extends Hint {
        PessimisticLockHint() {
            super(QueryHints.PESSIMISTIC_LOCK, PessimisticLock.DEFAULT);
            valueArray = new Object[][] {
                {PessimisticLock.NoLock, ObjectLevelReadQuery.NO_LOCK},
                {PessimisticLock.Lock, ObjectLevelReadQuery.LOCK},
                {PessimisticLock.LockNoWait, ObjectLevelReadQuery.LOCK_NOWAIT}
            };
        }
    
        DatabaseQuery applyToDatabaseQuery(Object valueToApply, DatabaseQuery query) {
            if (query.isObjectBuildingQuery()) {
                ((ObjectBuildingQuery)query).setLockMode(((Short)valueToApply).shortValue());
            }
            return query;
        }
    }

    protected static class PessimisticLockTimeoutHint extends Hint {
        PessimisticLockTimeoutHint() {
            super(QueryHints.PESSIMISTIC_LOCK_TIMEOUT, "");
        }
    
        DatabaseQuery applyToDatabaseQuery(Object valueToApply, DatabaseQuery query) {
            if (query.isObjectLevelReadQuery()) {                    
                ((ObjectLevelReadQuery) query).setWaitTimeout(QueryHintsHandler.parseIntegerHint(valueToApply, QueryHints.PESSIMISTIC_LOCK_TIMEOUT));
            } else {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("ejb30-wrong-type-for-query-hint",new Object[]{getQueryId(query), name, getPrintValue(valueToApply)}));
            }
            
            return query;
        }
    }
    
    protected static class RefreshHint extends Hint {
        RefreshHint() {
            super(QueryHints.REFRESH, HintValues.FALSE);
            valueArray = new Object[][] { 
                {HintValues.FALSE, Boolean.FALSE},
                {HintValues.TRUE, Boolean.TRUE}
            };
        }
    
        DatabaseQuery applyToDatabaseQuery(Object valueToApply, DatabaseQuery query) {
            if (query.isObjectBuildingQuery()) {
                ((ObjectBuildingQuery)query).setShouldRefreshIdentityMapResult(((Boolean)valueToApply).booleanValue());
            }
            return query;
        }
    }

    /**
     * Define the query cache hint.
     * Only reset the query cache if unset (as other query cache properties may be set first).
     */
    protected static class QueryCacheHint extends Hint {
        QueryCacheHint() {
            super(QueryHints.QUERY_RESULTS_CACHE, HintValues.FALSE);
            valueArray = new Object[][] { 
                {HintValues.FALSE, Boolean.FALSE},
                {HintValues.TRUE, Boolean.TRUE}
            };
        }
    
        DatabaseQuery applyToDatabaseQuery(Object valueToApply, DatabaseQuery query) {
            if (query.isReadQuery()) {
                if (((Boolean)valueToApply).booleanValue()) {
                    if (((ReadQuery)query).getQueryResultsCachePolicy() == null) {
                        ((ReadQuery)query).cacheQueryResults();
                    }
                }
            } else {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("ejb30-wrong-type-for-query-hint",new Object[]{getQueryId(query), name, getPrintValue(valueToApply)}));
            }
            return query;
        }
    }

    /**
     * Define the query cache size hint.
     * Only reset the query cache if unset (as other query cache properties may be set first).
     */
    protected static class QueryCacheSizeHint extends Hint {
        QueryCacheSizeHint() {
            super(QueryHints.QUERY_RESULTS_CACHE_SIZE, "");
        }
    
        DatabaseQuery applyToDatabaseQuery(Object valueToApply, DatabaseQuery query) {
            if (query.isReadQuery()) {
                ReadQuery readQuery = (ReadQuery)query;
                if (readQuery.getQueryResultsCachePolicy() == null) {
                    readQuery.cacheQueryResults();
                }
                try {
                    readQuery.getQueryResultsCachePolicy().setMaximumCachedResults(Integer.parseInt((String)valueToApply));
                } catch (NumberFormatException exception) {
                    throw QueryException.queryHintContainedInvalidIntegerValue(QueryHints.QUERY_RESULTS_CACHE_SIZE, valueToApply, exception);
                }
            } else {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("ejb30-wrong-type-for-query-hint",new Object[]{getQueryId(query), name, getPrintValue(valueToApply)}));
            }
            return query;
        }
    }

    /**
     * Define the query cache expiry hint.
     * Only reset the query cache if unset (as other query cache properties may be set first).
     */
    protected static class QueryCacheExpiryHint extends Hint {
        QueryCacheExpiryHint() {
            super(QueryHints.QUERY_RESULTS_CACHE_EXPIRY, "");
        }
    
        DatabaseQuery applyToDatabaseQuery(Object valueToApply, DatabaseQuery query) {
            if (query.isReadQuery()) {
                ReadQuery readQuery = (ReadQuery)query;
                if (readQuery.getQueryResultsCachePolicy() == null) {
                    readQuery.cacheQueryResults();
                }
                try {
                    readQuery.getQueryResultsCachePolicy().setCacheInvalidationPolicy(
                            new TimeToLiveCacheInvalidationPolicy(Integer.parseInt((String)valueToApply)));
                } catch (NumberFormatException exception) {
                    throw QueryException.queryHintContainedInvalidIntegerValue(QueryHints.QUERY_RESULTS_CACHE_EXPIRY, valueToApply, exception);
                }
            } else {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("ejb30-wrong-type-for-query-hint",new Object[]{getQueryId(query), name, getPrintValue(valueToApply)}));
            }
            return query;
        }
    }

    /**
     * Define the query cache expiry time of day hint.
     * Only reset the query cache if unset (as other query cache properties may be set first).
     */
    protected static class QueryCacheExpiryTimeOfDayHint extends Hint {
        QueryCacheExpiryTimeOfDayHint() {
            super(QueryHints.QUERY_RESULTS_CACHE_EXPIRY_TIME_OF_DAY, "");
        }
    
        DatabaseQuery applyToDatabaseQuery(Object valueToApply, DatabaseQuery query) {
            if (query.isReadQuery()) {
                ReadQuery readQuery = (ReadQuery)query;
                if (readQuery.getQueryResultsCachePolicy() == null) {
                    readQuery.cacheQueryResults();
                }
                try {
                    Time time = Helper.timeFromString((String)valueToApply);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(time);
                    readQuery.getQueryResultsCachePolicy().setCacheInvalidationPolicy(
                            new DailyCacheInvalidationPolicy(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND), 0));
                } catch (ConversionException exception) {
                    throw QueryException.queryHintContainedInvalidIntegerValue(QueryHints.QUERY_RESULTS_CACHE_EXPIRY_TIME_OF_DAY, valueToApply, exception);
                }
            } else {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("ejb30-wrong-type-for-query-hint",new Object[]{getQueryId(query), name, getPrintValue(valueToApply)}));
            }
            return query;
        }
    }
    
    protected static class BatchHint extends Hint {
        BatchHint() {
            super(QueryHints.BATCH, "");
        }
    
        DatabaseQuery applyToDatabaseQuery(Object valueToApply, DatabaseQuery query) {
            if (query.isReadAllQuery() && !query.isReportQuery()) {
                ReadAllQuery raq = (ReadAllQuery)query;
                StringTokenizer tokenizer = new StringTokenizer((String)valueToApply, ".");
                if (tokenizer.countTokens() < 2){
                    throw QueryException.queryHintDidNotContainEnoughTokens(query, QueryHints.BATCH, valueToApply);
                }
                // ignore the first token since we are assuming read all query
                // e.g. In e.phoneNumbers we will assume "e" refers to the base of the query
                String previousToken = tokenizer.nextToken();
                ClassDescriptor descriptor = raq.getDescriptor();
                Expression expression = raq.getExpressionBuilder();
                while (tokenizer.hasMoreTokens()){
                    String token = tokenizer.nextToken();
                    ForeignReferenceMapping frMapping = null;
                    DatabaseMapping mapping = descriptor.getObjectBuilder().getMappingForAttributeName(token);
                    if (mapping == null){
                        throw QueryException.queryHintNavigatedNonExistantRelationship(query, QueryHints.BATCH, valueToApply, previousToken + "." + token);
                    } else if (!mapping.isForeignReferenceMapping()){
                        throw QueryException.queryHintNavigatedIllegalRelationship(query, QueryHints.BATCH, valueToApply, previousToken + "." + token);
                    } else {
                        frMapping = (ForeignReferenceMapping)mapping;
                    }
                    descriptor = frMapping.getReferenceDescriptor();
                    if (frMapping.isCollectionMapping()){
                        expression = expression.anyOf(token);
                    } else {
                        expression = expression.get(token);
                    }
                    previousToken = token;
                }
                raq.addBatchReadAttribute(expression);
            } else {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("ejb30-wrong-type-for-query-hint",new Object[]{getQueryId(query), name, getPrintValue(valueToApply)}));
            }
            return query;
        }
    }
    
    protected static class FetchHint extends Hint {
        FetchHint() {
            super(QueryHints.FETCH, "");
        }
    
        DatabaseQuery applyToDatabaseQuery(Object valueToApply, DatabaseQuery query) {
            if (query.isObjectLevelReadQuery() && !query.isReportQuery()) {
                ObjectLevelReadQuery olrq = (ObjectLevelReadQuery)query;
                StringTokenizer tokenizer = new StringTokenizer((String)valueToApply, ".");
                if (tokenizer.countTokens() < 2){
                    throw QueryException.queryHintDidNotContainEnoughTokens(query, QueryHints.BATCH, valueToApply);
                }
                // ignore the first token since we are assuming read all query
                // e.g. In e.phoneNumbers we will assume "e" refers to the base of the query
                String previousToken = tokenizer.nextToken();
                ClassDescriptor descriptor = olrq.getDescriptor();
                Expression expression = olrq.getExpressionBuilder();
                while (tokenizer.hasMoreTokens()){
                    String token = tokenizer.nextToken();
                    ForeignReferenceMapping frMapping = null;
                    DatabaseMapping mapping = descriptor.getObjectBuilder().getMappingForAttributeName(token);
                    if (mapping == null){
                        throw QueryException.queryHintNavigatedNonExistantRelationship(query, QueryHints.BATCH, valueToApply, previousToken + "." + token);
                    } else if (!mapping.isForeignReferenceMapping()){
                        throw QueryException.queryHintNavigatedIllegalRelationship(query, QueryHints.BATCH, valueToApply, previousToken + "." + token);
                    } else {
                        frMapping = (ForeignReferenceMapping)mapping;
                    }
                    descriptor = frMapping.getReferenceDescriptor();
                    if (frMapping.isCollectionMapping()){
                        expression = expression.anyOf(token);
                    } else {
                        expression = expression.get(token);
                    }
                    previousToken = token;
                }
                olrq.addJoinedAttribute(expression);
            } else {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("ejb30-wrong-type-for-query-hint",new Object[]{getQueryId(query), name, getPrintValue(valueToApply)}));
            }
            return query;
        }
    }

    protected static class ReturnSharedHint extends Hint {
        ReturnSharedHint() {
            super(QueryHints.READ_ONLY, HintValues.FALSE);
            valueArray = new Object[][] { 
                {HintValues.FALSE, Boolean.FALSE},
                {HintValues.TRUE, Boolean.TRUE}
            };
        }
    
        DatabaseQuery applyToDatabaseQuery(Object valueToApply, DatabaseQuery query) {
            if (query.isObjectLevelReadQuery()) {
                ((ObjectLevelReadQuery)query).setIsReadOnly(((Boolean)valueToApply).booleanValue());
            } else {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("ejb30-wrong-type-for-query-hint",new Object[]{getQueryId(query), name, getPrintValue(valueToApply)}));
            }
            return query;
        }
    }
    
    protected static class JDBCTimeoutHint extends Hint {
        JDBCTimeoutHint() {
            super(QueryHints.JDBC_TIMEOUT, "");
        }
    
        DatabaseQuery applyToDatabaseQuery(Object valueToApply, DatabaseQuery query) {
            query.setQueryTimeout(QueryHintsHandler.parseIntegerHint(valueToApply, QueryHints.JDBC_TIMEOUT));
            return query;
        }
    }
    

    
    protected static class JDBCFetchSizeHint extends Hint {
        JDBCFetchSizeHint() {
            super(QueryHints.JDBC_FETCH_SIZE, "");
        }
    
        DatabaseQuery applyToDatabaseQuery(Object valueToApply, DatabaseQuery query) {
            if (query.isReadQuery()) {
                ((ReadQuery) query).setFetchSize(QueryHintsHandler.parseIntegerHint(valueToApply, QueryHints.JDBC_FETCH_SIZE));
            } else {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("ejb30-wrong-type-for-query-hint",new Object[]{getQueryId(query), name, getPrintValue(valueToApply)}));
            }
            
            return query;
        }
    }
    
    protected static class JDBCMaxRowsHint extends Hint {
        JDBCMaxRowsHint() {
            super(QueryHints.JDBC_MAX_ROWS, "");
        }
    
        DatabaseQuery applyToDatabaseQuery(Object valueToApply, DatabaseQuery query) {
            if (query.isReadQuery()) {
                ((ReadQuery) query).setMaxRows(QueryHintsHandler.parseIntegerHint(valueToApply, QueryHints.JDBC_MAX_ROWS));
            } else {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("ejb30-wrong-type-for-query-hint",new Object[]{getQueryId(query), name, getPrintValue(valueToApply)}));
            }
            return query;
        }
    }
    
    protected static class ResultCollectionTypeHint extends Hint {
        ResultCollectionTypeHint() {
            super(QueryHints.RESULT_COLLECTION_TYPE, "");
        }
    
        DatabaseQuery applyToDatabaseQuery(Object valueToApply, DatabaseQuery query) {
            if (query.isReadAllQuery()) {
                Class collectionClass = null;
                if (valueToApply instanceof String) {
                    try {
                        // TODO: This is not using the correct classloader.
                        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
                            try {
                                collectionClass = (Class)AccessController.doPrivileged(new PrivilegedClassForName((String)valueToApply));
                            } catch (PrivilegedActionException exception) {
                                throw QueryException.classNotFoundWhileUsingQueryHint(query, valueToApply, exception.getException());
                            }
                        } else {
                            collectionClass = PrivilegedAccessHelper.getClassForName((String)valueToApply);
                        }
                    } catch (ClassNotFoundException exc){
                        throw QueryException.classNotFoundWhileUsingQueryHint(query, valueToApply, exc);
                    }
                } else {
                    collectionClass = (Class)valueToApply;
                }
                ((ReadAllQuery)query).useCollectionClass(collectionClass);
            } else {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("ejb30-wrong-type-for-query-hint",new Object[]{getQueryId(query), name, getPrintValue(valueToApply)}));
            }
            return query;
        }
    }
    
    protected static class RedirectorHint extends Hint {
        RedirectorHint() {
            super(QueryHints.QUERY_REDIRECTOR, "");
        }
    
        DatabaseQuery applyToDatabaseQuery(Object valueToApply, DatabaseQuery query) {
            try{
                query.setRedirector((QueryRedirector)valueToApply);
            }catch(ClassCastException ex){
                throw QueryException.unableToSetRedirectorOnQueryFromHint(query,QueryHints.QUERY_REDIRECTOR, valueToApply.getClass().getName(), ex);
            }
            return query;
        }
    }
}
