/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.queries.UpdateAllQuery;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.queries.ReportQueryResult;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.expressions.DataExpression;
 
public class UpdateAllQueryTestHelper {
            
    public static String execute(Session mainSession, Class referenceClass, HashMap updateClauses, Expression selectionExpression) {
        return execute(mainSession, referenceClass, updateClauses, selectionExpression, true);
    }

    public static String execute(Session mainSession, Class referenceClass, HashMap updateClauses, Expression selectionExpression, boolean handleChildren) {
        return execute(mainSession, createUpdateAllQuery(referenceClass, updateClauses, selectionExpression), true);
    }

    public static String execute(Session mainSession, UpdateAllQuery uq) {
        return execute(mainSession, uq,  true);
    }
    
    // Compares results of updating multiple objects yusing traditionalTopLink update one-by-one approach
    // with updating using UpdateAllQuery. If results differ retuns a non-null error message.
    // To use this method:
    // 1. Populate db with the objects on which update should be performed;
    // 2. In case handleChildren == true the similar query will also be tested with all subclasses:
    //      example: for uq.referenceClass()==Project, the test will also run for SmallProject and LargeProject.
    // 3. After the test is completed it leaves the db in the original state (uses rollbacks).
    public static String execute(Session mainSession, UpdateAllQuery uq,  boolean handleChildren) {
        // Find the inheritance root class - the test will compare all instances of this class
        // after traditional TopLink update (one-by-one) with all instances of this class
        // after UpdateAllQuery. The test succeeds if the two collections are equal.
        Class rootClass = uq.getReferenceClass();
        ClassDescriptor descriptor = mainSession.getClassDescriptor(uq.getReferenceClass());
        if(descriptor.hasInheritance()) {
            ClassDescriptor parentDescriptor = descriptor;
            while(!parentDescriptor.getInheritancePolicy().isRootParentDescriptor()) {
                parentDescriptor = parentDescriptor.getInheritancePolicy().getParentDescriptor();
            }
            rootClass = parentDescriptor.getJavaClass();
        }
                
        String errorMsg = execute(mainSession, uq, handleChildren, rootClass);

        if(errorMsg.length() == 0) {
            return null;
        } else {
            return errorMsg;
        }
    }
    
    protected static String execute(Session mainSession, UpdateAllQuery uq, boolean handleChildren,
                                  Class rootClass) {
        String errorMsg = "";
        ClassDescriptor descriptor = mainSession.getDescriptor(uq.getReferenceClass());
        
        clearCache(mainSession);
        
        // original objects
        Vector objects = mainSession.readAllObjects(rootClass);
        
        // first update using the original TopLink approach - one by one.
        // That will be done using report query - it will use the same selection criteria
        // as UpdateAllQuery and each attribute will correspond to an update item.
        ReportQuery rq = new ReportQuery(uq.getReferenceClass(), uq.getExpressionBuilder());    
        rq.setSelectionCriteria(uq.getSelectionCriteria());
        rq.setShouldRetrievePrimaryKeys(true);
        // some db platforms don't allow nulls in select clause - so add the fields with null values to the query result.
        Vector fieldsWithNullValues = new Vector();
        Iterator itEntrySets = uq.getUpdateClauses().entrySet().iterator();
        while(itEntrySets.hasNext()) {
            Map.Entry entry = (Map.Entry)itEntrySets.next();
            Expression valueExpression;
            String keyString = getQualifiedFieldNameFromKey(entry.getKey(), rq.getReferenceClass(), descriptor, mainSession);
            Object value = entry.getValue();
            DatabaseMapping mapping = descriptor.getObjectBuilder().getMappingForField(new DatabaseField(keyString));
            if(mapping != null && mapping.isOneToOneMapping() && value != null) {
                // Note that this only works in case the reference PK is not compound
                if(((OneToOneMapping)mapping).getSourceToTargetKeyFields().size() > 1) {
                    errorMsg = "Attribute "+ mapping.getAttributeName() + " mapped with 1to1 mapping that has more than one targetKeyField. UpdateAllQueryTestHelper currently doesn't support that.";
                }
                DatabaseField targetField = ((OneToOneMapping)mapping).getSourceToTargetKeyFields().get(new DatabaseField(keyString));
                if(value instanceof Expression) {
                    valueExpression = ((Expression)(((Expression)value).clone())).getField(targetField);
                } else {
                    ClassDescriptor targetDescriptor = ((OneToOneMapping)mapping).getReferenceDescriptor();
                    Object fieldValue = targetDescriptor.getObjectBuilder().extractValueFromObjectForField(value, targetField, (org.eclipse.persistence.internal.sessions.AbstractSession)mainSession);
                    valueExpression = rq.getExpressionBuilder().value(fieldValue);
                }
            } else {
                if(value instanceof Expression) {
                    valueExpression = (Expression)value;
                } else {
                    valueExpression = rq.getExpressionBuilder().value(value);
                }
            }
            if(value == null) {
                fieldsWithNullValues.add(keyString);
            } else {
                rq.addAttribute(keyString, valueExpression);
            }
        }
        
        UnitOfWork uow = mainSession.acquireUnitOfWork();
        // mainSession could be a ServerSession
        Session session = uow.getParent();
        
        // report query results contain the values to be assigned for each object to be updated.
        Vector result = (Vector)session.executeQuery(rq);
        Vector objectsAfterOneByOneUpdate = new Vector(objects.size());
        ((org.eclipse.persistence.internal.sessions.AbstractSession)session).beginTransaction();
        try {
            for (int i = 0; i < result.size(); i++) {
                // read through uow the object(clone) to be updated
                ReportQueryResult reportResult = (ReportQueryResult)result.elementAt(i);
                // hammer into the object the updated values
                Object obj = reportResult.readObject(rq.getReferenceClass(), uow);
                DatabaseRecord row = new DatabaseRecord();
                for (int j = 0; j < reportResult.getNames().size(); j++) {
                    String name = reportResult.getNames().get(j);
                    DatabaseField field = new DatabaseField(name);
                    Object value = reportResult.getResults().get(j);
                    row.add(field, value);            
                }
                // some db platforms don't allow nulls in select clause - so add the fields with null values to the query result
                for (int j = 0; j < fieldsWithNullValues.size(); j++) {
                    String name = (String)fieldsWithNullValues.elementAt(j);
                    DatabaseField field = new DatabaseField(name);
                    row.add(field, null);
                }
                rq.getDescriptor().getObjectBuilder().assignReturnRow(obj, (AbstractSession)uow, row, null);
            }
            // uow committed - objects updated.
            uow.commit();
    
            // Because the transaction will be rolled back (to return to the original state to execute UpdateAllQuery)
            // objects are copied into another vector - later it will be compared with UpdateAllQuery result.
            for(int i=0; i < objects.size(); i++) {
                Object original = objects.elementAt(i);
                Object copy = buildCopy(descriptor, original, uow);
                objectsAfterOneByOneUpdate.add(copy);
            }
        } finally {
            // transaction rolled back - objects back to the original state in the db.
            ((org.eclipse.persistence.internal.sessions.AbstractSession)session).rollbackTransaction();
        }
        clearCache(mainSession);

        // now use UpdateAllQuery
        uow = mainSession.acquireUnitOfWork();
        // mainSession could be a ServerSession
        session = uow.getParent();        
        Vector objectsAfterUpdateAll = new Vector(objects.size());
        ((org.eclipse.persistence.internal.sessions.AbstractSession)session).beginTransaction();
        try {
            uow.executeQuery(uq);
            // uow committed - objects updated.
            uow.commit();
    
            // Because the transaction will be rolled back (to return to the original state)
            // objects are copied into another vector - it will be compared with update one-by-one result.
            for(int i=0; i < objects.size(); i++) {
                Object original = objects.elementAt(i);
                Object copy = buildCopy(descriptor, original, uow);
                objectsAfterUpdateAll.add(copy);
            }
        } finally {
            // transaction rolled back - objects back to the original state in the db.
            ((org.eclipse.persistence.internal.sessions.AbstractSession)session).rollbackTransaction();
        }
        clearCache(mainSession);
        
        // verify
        String classErrorMsg = "";
        for(int i=0; i < objects.size(); i++) {
            Object obj = objects.elementAt(i);
            Object obj1 = objectsAfterOneByOneUpdate.elementAt(i);
            Object obj2 = objectsAfterUpdateAll.elementAt(i);
            boolean equal = rq.getDescriptor().getObjectBuilder().compareObjects(obj, obj2, (org.eclipse.persistence.internal.sessions.AbstractSession)session);
            if(!equal) {
                classErrorMsg = classErrorMsg + "Difference: original = " + obj.toString() + "; afterOneByOneUpdate = " + obj1.toString() +"; afterUpdateAll = " + obj2.toString() + ";";
            }
        }
        if(classErrorMsg.length() > 0) {
            errorMsg = errorMsg + classErrorMsg;
        }

        if(handleChildren) {
            if(descriptor.hasInheritance() && descriptor.getInheritancePolicy().hasChildren()) {
                Iterator it = descriptor.getInheritancePolicy().getChildDescriptors().iterator();
                while(it.hasNext()) {
                    ClassDescriptor childDescriptor = (ClassDescriptor)it.next();
                    Class childReferenceClass = childDescriptor.getJavaClass();
                    UpdateAllQuery childUq = (UpdateAllQuery)uq.clone();
                    childUq.setReferenceClass(childReferenceClass);
                    childUq.setIsPrepared(false);
                    errorMsg += execute(mainSession, childUq, handleChildren, rootClass);
                }
            }
        }
        return errorMsg;
    }
    
    protected static void clearCache(Session mainSession) {
        mainSession.getIdentityMapAccessor().initializeAllIdentityMaps();
    }
    
    public static UpdateAllQuery createUpdateAllQuery(Class referenceClass, HashMap updateClauses, Expression selectionExpression) {
        // Construct UpdateAllQuery
        UpdateAllQuery uq = new UpdateAllQuery(referenceClass, selectionExpression);
        Iterator itEntrySets = updateClauses.entrySet().iterator();
        while(itEntrySets.hasNext()) {
            Map.Entry entry = (Map.Entry)itEntrySets.next();
            uq.addUpdate((String)entry.getKey(), entry.getValue());
        }
        return uq;
    }
    
    static protected Object buildCopy(ClassDescriptor descriptor, Object original, UnitOfWork uow) {
        Object copy = descriptor.getCopyPolicy().buildClone(original, uow);
        descriptor.getObjectBuilder().copyInto(original, copy, true);
        return copy;
    }

    static protected String getQualifiedFieldNameFromKey(Object key, Class referenceClass, ClassDescriptor descriptor, Session session) {
        DatabaseField field = null;
        if(key instanceof String) {
            // attribute name
            String name = (String)key;
            DatabaseMapping mapping = descriptor.getObjectBuilder().getMappingForAttributeName(name);
            if(mapping != null) {
                field = mapping.getFields().firstElement();
            }
        } else if(key instanceof DataExpression) {
            DataExpression fieldExpression = (DataExpression)key;
            field = descriptor.getObjectBuilder().getFieldForQueryKeyName(fieldExpression.getName());
            if(field == null) {
                DataExpression fieldExpressionClone = (DataExpression)fieldExpression.clone();
                fieldExpressionClone.getBuilder().setQueryClass(referenceClass);
                fieldExpressionClone.getBuilder().setSession((org.eclipse.persistence.internal.sessions.AbstractSession)session);
                field = fieldExpressionClone.getField();
            }
        }
        
        if(field != null) {
            return field.getQualifiedName();
        }
        
        // should never happen
        return null;
    }
}
