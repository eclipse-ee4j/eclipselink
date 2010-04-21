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
 ******************************************************************************/  
package org.eclipse.persistence.testing.framework;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.indirection.ValueHolder;
import org.eclipse.persistence.internal.descriptors.ObjectBuilder;
import org.eclipse.persistence.internal.expressions.QueryKeyExpression;
import org.eclipse.persistence.internal.helper.Helper;
import java.util.*;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.InMemoryQueryIndirectionPolicy;
import org.eclipse.persistence.mappings.DatabaseMapping;
 
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
        
        session.logMessage("JoinedAttributeTestHelper: getting control result:");
        Object controlResult = getControlResultsFromControlQuery(controlQuery, queryWithJoins, session);
        String errorMsg = "";
        session.logMessage("JoinedAttributeTestHelper: executing queryWithJoins:");
        if (controlResult instanceof Collection) {
            Collection result = (Collection)session.executeQuery(queryWithJoins);
            errorMsg = compareCollections((Collection)controlResult, result, controlQuery.getDescriptor(), session);
        } else {
            Object result = session.executeQuery(queryWithJoins);
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
    public static Object getControlResultsFromControlQuery (ObjectLevelReadQuery controlQuery, ObjectLevelReadQuery queryWithJoins, AbstractSession session){
        int valueHolderPolicy = InMemoryQueryIndirectionPolicy.SHOULD_TRIGGER_INDIRECTION;

        // Need to execute the control query twice, once to determine objects excluded from outjoins,
        // and once to instantiate indirection on only those objects not excluded (otherwise may instantiate indirection differently than join query).
        Object controlResult = session.executeQuery(controlQuery);
        boolean isCollection = false;
        Collection collectionResult = null;
        if (controlResult instanceof Collection) {
            collectionResult = (Collection)controlResult;
            isCollection = true;
        } else {
            collectionResult = new Vector(1);
            collectionResult.add(controlResult);
        }
        Set excluded = new HashSet();
        // Iterate over the result and add removed results to the excluded set.
        for (Iterator iterator = collectionResult.iterator(); iterator.hasNext(); ) {
            Object object = iterator.next();
            boolean remove = false;
            for (Iterator joinsIterator = queryWithJoins.getJoinedAttributeManager().getJoinedAttributeExpressions().iterator(); joinsIterator.hasNext(); ) {
                Expression joinExpression = (Expression)joinsIterator.next();
                joinExpression.getBuilder().setSession(session);
                joinExpression.getBuilder().setQueryClass(queryWithJoins.getReferenceClass());
                // Instantiate value holders that should be instantiated.
                Object value = joinExpression.valueFromObject(object, session, null, valueHolderPolicy, false);
                if (joinExpression.isQueryKeyExpression()) {
                    QueryKeyExpression queryKeyExpression = ((QueryKeyExpression)joinExpression);
                    // Iin case of NOT an outer join, remove objects with null / empty values.
                    if (!queryKeyExpression.shouldUseOuterJoin()) {
                        if (value == null) {
                            remove = true;
                            break;
                        } else if (value instanceof Collection) {
                            Collection collection = (Collection)value;
                            if (collection.isEmpty()) {
                            remove = true;
                                break;
                            } else if (!queryKeyExpression.shouldQueryToManyRelationship()) {
                                Iterator collectionIterator = collection.iterator();
                                while (collectionIterator.hasNext()) {
                                    if (collectionIterator.next() == null) {
                                        remove = true;
                                        break;
                                    }
                                }
                                if (remove == true) {
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            if (remove) {
                excluded.add(new CacheKey(session.keyFromObject(object)));
            }
        }
        session.getIdentityMapAccessor().initializeAllIdentityMaps();
        
        // Now execute and only instantiate indirection in non-excluded objects.
        controlResult = session.executeQuery(controlQuery);
        isCollection = false;
        collectionResult = null;
        if (controlResult instanceof Collection) {
            collectionResult = (Collection)controlResult;
            isCollection = true;
        } else {
            collectionResult = new Vector(1);
            collectionResult.add(controlResult);
        }
        // Iterate over the result and instantiate all joined indirection.
        for (Iterator iterator = collectionResult.iterator(); iterator.hasNext(); ) {
            Object object = iterator.next();
            if (excluded.contains(new CacheKey(session.keyFromObject(object)))) {
                iterator.remove();
            } else {
                for (Iterator joinsIterator = queryWithJoins.getJoinedAttributeManager().getJoinedAttributeExpressions().iterator(); joinsIterator.hasNext(); ) {
                    Expression joinExpression = (Expression)joinsIterator.next();
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
        String errorMsg = "";
        if(col1 != null) {
            if(processed.containsKey(col1)) {
                return "";
            }
            processed.put(col1, col1);
            if(col2==null) {
                errorMsg = ": " + col1.toString() + "!= null ;  ";
                return errorMsg;
            }
        }
        if(col2 != null) {
            if(processed.containsKey(col2)) {
                return "";
            }
            processed.put(col2, col2);
            if(col1 == null) {
                errorMsg = ": null !=" + col2.toString() + ";  ";
                return errorMsg;
            }
        }
        if(col1.size() != col2.size()) {
            errorMsg = ": size1==" + Integer.toString(col1.size()) + "!= size2==" + Integer.toString(col2.size()) + ";  ";
            return errorMsg;
        }

        if(desc != null) {
            // objects keyed by pks
            HashMap map1 = new HashMap(col1.size());
            HashMap map2 = new HashMap(col2.size());
            
            boolean simplePk = desc.getPrimaryKeyFields().size()==1;
            ObjectBuilder builder = desc.getObjectBuilder();
            Iterator it1 = col1.iterator();
            Iterator it2 = col2.iterator();
            while(it1.hasNext()) {
                Object obj1 = it1.next();
                Object obj2 = it2.next();
                Object pk1 = builder.extractPrimaryKeyFromObject(obj1, session);
                Object pk2 = builder.extractPrimaryKeyFromObject(obj2, session);
                if(simplePk) {
                    pk1 = ((Vector)pk1).firstElement();
                    pk2 = ((Vector)pk2).firstElement();
                }
                map1.put(pk1, obj1);
                map2.put(pk2, obj2);
            }
    
            Iterator itEntries1 = map1.entrySet().iterator();
            while(itEntries1.hasNext()) {
                Map.Entry entry = (Map.Entry)itEntries1.next();
                Object pk = entry.getKey();
                Object obj1 = entry.getValue();
                Object obj2 = map2.get(pk);
                String objErrorMsg = compareObjects(obj1, obj2, session, processed);
                if(objErrorMsg.length() > 0) {
                    errorMsg += "PK = " + pk.toString() + ": " + Helper.getShortClassName(obj1.getClass()) + objErrorMsg + "  ";
                }
                
            }
        } else {
            // there's no target descriptor - compare collections directly
            if(!col1.equals(col2)) {
                errorMsg += "Collections " + col1.toString() + " and " + col2.toString() + " are not equal; ";
            }
        }

        return errorMsg;
    }
    
    protected static String compareMaps(Map map1, Map map2, AbstractSession session, Map processed) {
        if(map1==null && map2==null) {
            return "";
        }
        String errorMsg = "";
        if(map1 != null) {
            if(processed.containsKey(map1)) {
                return "";
            }
            processed.put(map1, map1);
            if(map2==null) {
                errorMsg = ": " + map1.toString() + "!= null ;  ";
                return errorMsg;
            }
        }
        if(map2 != null) {
            if(processed.containsKey(map2)) {
                return "";
            }
            processed.put(map2, map2);
            if(map1 == null) {
                errorMsg = ": null !=" + map2.toString() + ";  ";
                return errorMsg;
            }
        }
        if(map1.size() != map2.size()) {
            errorMsg = ": size1==" + Integer.toString(map1.size()) + "!= size2==" + Integer.toString(map2.size()) + ";  ";
            return errorMsg;
        }

        Iterator itEntries1 = map1.entrySet().iterator();
        while(itEntries1.hasNext()) {
            Map.Entry entry = (Map.Entry)itEntries1.next();
            Object key = entry.getKey();
            Object obj1 = entry.getValue();
            Object obj2 = map2.get(key);
            String objErrorMsg = compareObjects(obj1, obj2, session, processed);
            if(objErrorMsg.length() > 0) {
                errorMsg += "Key = " + key.toString() + ": " + Helper.getShortClassName(obj1.getClass()) + objErrorMsg + "  ";
            }
            
        }

        return errorMsg;
    }
    
    protected static String compareObjects(Object obj1, Object obj2, AbstractSession session, Map processed) {
        if(obj1==null && obj2==null) {
            return "";
        }
        String errorMsg = "";
        if(obj1 != null) {
            if(processed.containsKey(obj1)) {
                return "";
            }
            processed.put(obj1, obj1);
            if(obj2==null) {
                errorMsg = ": " + obj1.toString() + "!= null;  ";
                return errorMsg;
            }
        }
        if(obj2 != null) {
            if(processed.containsKey(obj2)) {
                return "";
            }
            processed.put(obj2, obj2);
            if(obj1 == null) {
                errorMsg = ": null !=" + obj2.toString() + ";  ";
                return errorMsg;
            }
        }

        if(obj1.getClass() != obj2.getClass()) {
            errorMsg = ": " + obj1.getClass().getName() + "!=" + obj2.getClass().getName() + "; ";
            return errorMsg;
        }
        
        ClassDescriptor desc = session.getDescriptor(obj1);
        if(desc == null && !obj1.equals(obj2)) {
            errorMsg = ": " + obj1.toString() + "!=" +obj2.toString() + ";  ";
            return errorMsg;
        }
        
        Vector mappings = desc.getMappings();
        for (int index = 0; index < mappings.size(); index++) {
            DatabaseMapping mapping = (DatabaseMapping)mappings.get(index);
            String mappingErrorMsg = compareAttributes(obj1, obj2, mapping, session, processed);
            errorMsg += mappingErrorMsg;
        }

        return errorMsg;
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
                if(frm.isOneToOneMapping() && value1 instanceof ValueHolder && ((ValueHolder)(value1)).getValue() == null) {
                    // In OneToOne case if the foreign key of the read object is null then ValueHolder (which is always instantiated) with value null is created 
                } else {
                    errorMsg = ":  indirection instantiated != indirection NOT instantiated; ";
                }
            } else if(!isInstantiated1 && isInstantiated2) {
                if(frm.isOneToOneMapping() && value2 instanceof ValueHolder && ((ValueHolder)(value2)).getValue() == null) {
                    // In OneToOne case if the foreign key of the read object is null then ValueHolder (which is always instantiated) with value null is created 
                } else {
                    errorMsg = ": indirection NOT instantiated != indirection instantiated; ";
                }
            } else {
                value1 = frm.getRealAttributeValueFromObject(obj1, session);
                value2 = frm.getRealAttributeValueFromObject(obj2, session);
                if(frm.isCollectionMapping()) {
                    Class containerClass = ((CollectionMapping)frm).getContainerPolicy().getContainerClass();
                    if(Collection.class.isAssignableFrom(containerClass)) {
                        errorMsg += compareCollections((Collection)value1, (Collection)value2, frm.getReferenceDescriptor(), session, processed);
                    } else if(Map.class.isAssignableFrom(containerClass)) {
                        errorMsg += compareMaps((Map)value1, (Map)value2, session, processed);
                    } else {
                        errorMsg += mapping.toString() + " container class implements neither Collection nor Map - can't processl; "; 
                    }
                } else {
                    errorMsg += compareObjects(value1, value2, session, processed);
                }
            }
        } else if (!mapping.compareObjects(obj1, obj2, session)) {
            Object value1 = mapping.getAttributeValueFromObject(obj1);
            if(value1 == null) {
                value1 = new String("null");
            }
            Object value2 = mapping.getAttributeValueFromObject(obj2);
            if(value2 == null) {
                value2 = new String("null");
            }
            errorMsg = ": " + value1.toString() + "!=" + value2.toString() + "; ";
        }
        if(errorMsg.length() > 0) {
            errorMsg = "." + mapping.getAttributeName() + errorMsg;
        }
        return errorMsg;
    }
}
