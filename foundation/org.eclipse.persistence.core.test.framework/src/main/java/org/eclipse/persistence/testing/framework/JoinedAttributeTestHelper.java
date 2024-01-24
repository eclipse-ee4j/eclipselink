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
package org.eclipse.persistence.testing.framework;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.indirection.ValueHolder;
import org.eclipse.persistence.internal.descriptors.ObjectBuilder;
import org.eclipse.persistence.internal.expressions.QueryKeyExpression;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.queries.InMemoryQueryIndirectionPolicy;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class JoinedAttributeTestHelper {

    /**
     * Pass to this method controlQuery and query with joined attributes to be tested.
     * The method executes both controlQuery and query with joined attributes and compares results.
     * The errorMessage is returned - empty return means the result are equal.
     * Note that comparison:
     *   takes into account all objects returned by queries;
     *   doesn't instantiate indirection;
     *   error is reported in case on query has indirection instantiated and the other doesn't.
     *   joinedAttributes expressions set on the second query used to read the relevant values into
     *   the result of controlQuery.
     *
     * A simple way to create control query the will be in sync with queryWithJoins to be tested:
     *   instantiate queryWithJoins - the one to be tested,
     *   set it's referenceClass, selectionCriteria,..., everything but joinedAttributes;
     *   clone queryWithJoins - this clone is controlQuery;
     *   now add joined attributes to queryWithJoins;
     *   pass controlQuery and queryWithJoins to this method.
     */
    public static String executeQueriesAndCompareResults(ObjectLevelReadQuery controlQuery, ObjectLevelReadQuery queryWithJoins, AbstractSession session) {
        session.logMessage("JoinedAttributeTestHelper: executing queryWithJoins:");
        session.getIdentityMapAccessor().initializeAllIdentityMaps();
        Object result = session.executeQuery(queryWithJoins);
        session.logMessage("JoinedAttributeTestHelper: getting control result:");
        Object controlResult = getControlResultsFromControlQuery(controlQuery, queryWithJoins, session);
        String errorMsg = "";
        if (controlResult instanceof Collection) {
            errorMsg = compareCollections((Collection<?>)controlResult, (Collection<?>)result, controlQuery.getDescriptor(), session);
        } else {
            errorMsg = compareObjects(controlResult, result, session);
        }
        return errorMsg;
    }

    /**
     * Pass to this method controlQuery and query with joined attributes to be tested.
     * The method executes controlQuery and returns the results after triggering the relations
     * that need to be joined (using the queryWithJoins).
     *
     * @see executeQueriesAndCompareResults
     */
    @SuppressWarnings({"unchecked"})
    public static Object getControlResultsFromControlQuery (ObjectLevelReadQuery controlQuery, ObjectLevelReadQuery queryWithJoins, AbstractSession session){
        int valueHolderPolicy = InMemoryQueryIndirectionPolicy.SHOULD_TRIGGER_INDIRECTION;
        session.getIdentityMapAccessor().initializeAllIdentityMaps();
        // Need to execute the control query twice, once to determine objects excluded from outjoins,
        // and once to instantiate indirection on only those objects not excluded (otherwise may instantiate indirection differently than join query).
        Object controlResult = session.executeQuery(controlQuery);
        boolean isCollection = false;
        Collection<Object> collectionResult = null;
        if (controlResult instanceof Collection) {
            collectionResult = (Collection<Object>)controlResult;
            isCollection = true;
        } else {
            collectionResult = new Vector<>(1);
            collectionResult.add(controlResult);
        }
        Set<CacheKey> excluded = new HashSet<>();
        // Iterate over the result and add removed results to the excluded set.
        for (Object object : collectionResult) {
            boolean remove = false;
            for (Expression joinExpression : queryWithJoins.getJoinedAttributeManager().getJoinedAttributeExpressions()) {
                joinExpression.getBuilder().setSession(session);
                joinExpression.getBuilder().setQueryClass(queryWithJoins.getReferenceClass());
                // Instantiate value holders that should be instantiated.
                Object value = joinExpression.valueFromObject(object, session, null, valueHolderPolicy, false);
                if (joinExpression.isQueryKeyExpression()) {
                    QueryKeyExpression queryKeyExpression = ((QueryKeyExpression) joinExpression);
                    // Iin case of NOT an outer join, remove objects with null / empty values.
                    if (!queryKeyExpression.shouldUseOuterJoin()) {
                        if (value == null) {
                            remove = true;
                            break;
                        } else if (value instanceof Collection) {
                            Collection<?> collection = (Collection<?>) value;
                            if (collection.isEmpty()) {
                                remove = true;
                                break;
                            } else if (!queryKeyExpression.shouldQueryToManyRelationship()) {
                                for (Object o : collection) {
                                    if (o == null) {
                                        remove = true;
                                        break;
                                    }
                                }
                                if (remove) {
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            if (remove) {
                excluded.add(new CacheKey(session.getId(object)));
            }
        }
        session.getIdentityMapAccessor().initializeAllIdentityMaps();

        // Now execute and only instantiate indirection in non-excluded objects.
        controlResult = session.executeQuery(controlQuery);
        isCollection = false;
        collectionResult = null;
        if (controlResult instanceof Collection) {
            collectionResult = (Collection<Object>)controlResult;
            isCollection = true;
        } else {
            collectionResult = new Vector<>(1);
            collectionResult.add(controlResult);
        }
        // Iterate over the result and instantiate all joined indirection.
        for (Iterator<Object> iterator = collectionResult.iterator(); iterator.hasNext(); ) {
            Object object = iterator.next();
            if (excluded.contains(new CacheKey(session.getId(object)))) {
                iterator.remove();
            } else {
                for (Expression joinExpression : queryWithJoins.getJoinedAttributeManager().getJoinedAttributeExpressions()) {
                    // Instantiate value holders that should be instantiated.
                    joinExpression.valueFromObject(object, session, null, valueHolderPolicy, false);
                }
            }
        }
        session.getIdentityMapAccessor().initializeAllIdentityMaps();

        if (isCollection){
            return collectionResult;
        } else {
            return controlResult;
        }
    }

    // The errorMessage is returned - empty return means the collections are equal.
    // Note that comparison:
    //   takes into account all objects in collections: pks are extracted, objects with the same pks are compared.
    public static String compareCollections(Collection col1, Collection col2, ClassDescriptor desc, AbstractSession session) {
        Map processed = new IdentityHashMap();
        return compareCollections(col1, col2, desc, session, processed);
    }

    // The errorMessage is returned - empty return means the objects are equal.
    // Note that comparison:
    //   takes into account all objects referenced by the objects compared;
    //   doesn't instantiate indirection;
    //   error is reported in case on query has indirection instantiated and the other doesn't.
    public static String compareObjects(Object obj1, Object obj2, AbstractSession session) {
        Map processed = new IdentityHashMap();
        return compareObjects(obj1, obj2, session, processed);
    }

    protected static String compareCollections(Collection col1, Collection col2, ClassDescriptor desc, AbstractSession session, Map processed) {
        if(col1==null && col2==null) {
            return "";
        }
        StringBuilder errorMsg = new StringBuilder();
        if(col1 != null) {
            if(processed.containsKey(col1)) {
                return "";
            }
            processed.put(col1, col1);
            if(col2==null) {
                errorMsg = new StringBuilder(": " + col1 + "!= null ;  ");
                return errorMsg.toString();
            }
        }
        if(col2 != null) {
            if(processed.containsKey(col2)) {
                return "";
            }
            processed.put(col2, col2);
            if(col1 == null) {
                errorMsg = new StringBuilder(": null !=" + col2 + ";  ");
                return errorMsg.toString();
            }
        }
        if(col1.size() != col2.size()) {
            errorMsg = new StringBuilder(": size1==" + col1.size() + "!= size2==" + col2.size() + ";  ");
            return errorMsg.toString();
        }

        if(desc != null) {
            // objects keyed by pks
            HashMap map1 = new HashMap(col1.size());
            HashMap map2 = new HashMap(col2.size());

            ObjectBuilder builder = desc.getObjectBuilder();
            Iterator it1 = col1.iterator();
            Iterator it2 = col2.iterator();
            while(it1.hasNext()) {
                Object obj1 = it1.next();
                Object obj2 = it2.next();
                Object pk1 = builder.extractPrimaryKeyFromObject(obj1, session);
                Object pk2 = builder.extractPrimaryKeyFromObject(obj2, session);
                map1.put(pk1, obj1);
                map2.put(pk2, obj2);
            }

            for (Object o : map1.entrySet()) {
                Map.Entry entry = (Map.Entry) o;
                Object pk = entry.getKey();
                Object obj1 = entry.getValue();
                Object obj2 = map2.get(pk);
                String objErrorMsg = compareObjects(obj1, obj2, session, processed);
                if (!objErrorMsg.isEmpty()) {
                    errorMsg.append("PK = ").append(pk.toString()).append(": ").append(obj1.getClass().getSimpleName()).append(objErrorMsg).append("  ");
                }

            }
        } else {
            // there's no target descriptor - compare collections directly
            if(!col1.equals(col2)) {
                errorMsg.append("Collections ").append(col1).append(" and ").append(col2).append(" are not equal; ");
            }
        }

        return errorMsg.toString();
    }

    protected static String compareMaps(Map map1, Map map2, AbstractSession session, Map processed) {
        if(map1==null && map2==null) {
            return "";
        }
        StringBuilder errorMsg = new StringBuilder();
        if(map1 != null) {
            if(processed.containsKey(map1)) {
                return "";
            }
            processed.put(map1, map1);
            if(map2==null) {
                errorMsg = new StringBuilder(": " + map1 + "!= null ;  ");
                return errorMsg.toString();
            }
        }
        if(map2 != null) {
            if(processed.containsKey(map2)) {
                return "";
            }
            processed.put(map2, map2);
            if(map1 == null) {
                errorMsg = new StringBuilder(": null !=" + map2 + ";  ");
                return errorMsg.toString();
            }
        }
        if(map1.size() != map2.size()) {
            errorMsg = new StringBuilder(": size1==" + map1.size() + "!= size2==" + map2.size() + ";  ");
            return errorMsg.toString();
        }

        for (Object o : map1.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            Object key = entry.getKey();
            Object obj1 = entry.getValue();
            Object obj2 = map2.get(key);
            String objErrorMsg = compareObjects(obj1, obj2, session, processed);
            if (!objErrorMsg.isEmpty()) {
                errorMsg.append("Key = ").append(key.toString()).append(": ").append(obj1.getClass().getSimpleName()).append(objErrorMsg).append("  ");
            }

        }

        return errorMsg.toString();
    }

    protected static String compareObjects(Object obj1, Object obj2, AbstractSession session, Map processed) {
        if(obj1==null && obj2==null) {
            return "";
        }
        StringBuilder errorMsg = new StringBuilder();
        if(obj1 != null) {
            if(processed.containsKey(obj1)) {
                return "";
            }
            processed.put(obj1, obj1);
            if(obj2==null) {
                errorMsg = new StringBuilder(": " + obj1 + "!= null;  ");
                return errorMsg.toString();
            }
        }
        if(obj2 != null) {
            if(processed.containsKey(obj2)) {
                return "";
            }
            processed.put(obj2, obj2);
            if(obj1 == null) {
                errorMsg = new StringBuilder(": null !=" + obj2 + ";  ");
                return errorMsg.toString();
            }
        }

        if(obj1.getClass() != obj2.getClass()) {
            errorMsg = new StringBuilder(": " + obj1.getClass().getName() + "!=" + obj2.getClass().getName() + "; ");
            return errorMsg.toString();
        }

        ClassDescriptor desc = session.getDescriptor(obj1);
        if(desc == null ) {
            if (!obj1.equals(obj2)) {
                errorMsg = new StringBuilder(": " + obj1 + "!=" + obj2 + ";  ");
            }
            return errorMsg.toString();
        }

        List<DatabaseMapping> mappings = desc.getMappings();
        for (DatabaseMapping mapping : mappings) {
            String mappingErrorMsg = compareAttributes(obj1, obj2, mapping, session, processed);
            errorMsg.append(mappingErrorMsg);
        }

        return errorMsg.toString();
    }

    protected static String compareAttributes(Object obj1, Object obj2, DatabaseMapping mapping, AbstractSession session, Map processed) {
        String errorMsg = "";
        if(mapping.isForeignReferenceMapping()) {
            ForeignReferenceMapping frm = (ForeignReferenceMapping)mapping;
            Object value1 = frm.getAttributeValueFromObject(obj1);
            Object value2 = frm.getAttributeValueFromObject(obj2);
            boolean isInstantiated1 = frm.getIndirectionPolicy().objectIsInstantiated(value1);
            boolean isInstantiated2 = frm.getIndirectionPolicy().objectIsInstantiated(value2);
            if(!isInstantiated1 && !isInstantiated2) {
                return "";
            } else if(isInstantiated1 && !isInstantiated2) {
                if(frm.isOneToOneMapping() && value1 instanceof ValueHolder && ((ValueHolder<?>)(value1)).getValue() == null) {
                    // In OneToOne case if the foreign key of the read object is null then ValueHolder (which is always instantiated) with value null is created
                } else {
                    errorMsg = ":  indirection instantiated != indirection NOT instantiated; ";
                }
            } else if(!isInstantiated1 && isInstantiated2) {
                if(frm.isOneToOneMapping() && value2 instanceof ValueHolder && ((ValueHolder<?>)(value2)).getValue() == null) {
                    // In OneToOne case if the foreign key of the read object is null then ValueHolder (which is always instantiated) with value null is created
                } else {
                    errorMsg = ": indirection NOT instantiated != indirection instantiated; ";
                }
            } else {
                value1 = frm.getRealAttributeValueFromObject(obj1, session);
                value2 = frm.getRealAttributeValueFromObject(obj2, session);
                if(frm.isCollectionMapping()) {
                    Class<?> containerClass = frm.getContainerPolicy().getContainerClass();
                    if(Collection.class.isAssignableFrom(containerClass)) {
                        errorMsg += compareCollections((Collection)value1, (Collection)value2, frm.getReferenceDescriptor(), session, processed);
                    } else if(Map.class.isAssignableFrom(containerClass)) {
                        errorMsg += compareMaps((Map)value1, (Map)value2, session, processed);
                    } else {
                        errorMsg += mapping + " container class implements neither Collection nor Map - can't processl; ";
                    }
                } else {
                    errorMsg += compareObjects(value1, value2, session, processed);
                }
            }
        } else if (!mapping.compareObjects(obj1, obj2, session)) {
            Object value1 = mapping.getAttributeValueFromObject(obj1);
            if(value1 == null) {
                value1 = "null";
            }
            Object value2 = mapping.getAttributeValueFromObject(obj2);
            if(value2 == null) {
                value2 = "null";
            }
            errorMsg = ": " + value1 + "!=" + value2 + "; ";
        }
        if(!errorMsg.isEmpty()) {
            errorMsg = "." + mapping.getAttributeName() + errorMsg;
        }
        return errorMsg;
    }
}
