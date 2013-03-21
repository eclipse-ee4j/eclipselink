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
package org.eclipse.persistence.internal.descriptors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Iterator;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.InheritancePolicy;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.expressions.SQLSelectStatement;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.queries.DataReadQuery;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.ObjectChangeSet;
import org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;

/**
 * INTERNAL:
 */
public class CascadeLockingPolicy {
    protected Class m_parentClass;
    protected ReadObjectQuery m_query;
    protected ClassDescriptor m_descriptor;
    protected ClassDescriptor m_parentDescriptor;
    protected Map<DatabaseField, DatabaseField> m_queryKeyFields;
    protected Map<DatabaseField, DatabaseField> m_mappedQueryKeyFields;
    protected Map<DatabaseField, DatabaseField> m_unmappedQueryKeyFields;
    protected DatabaseMapping m_parentMapping;
    protected boolean m_lookForParentMapping;
    protected boolean m_shouldHandleUnmappedFields;
    protected boolean m_hasCheckedForUnmappedFields;
    protected DataReadQuery m_unmappedFieldsQuery;
    
    /**
     * INTERNAL:
     */
    public CascadeLockingPolicy(ClassDescriptor parentDescriptor, ClassDescriptor descriptor) {
        m_descriptor = descriptor;
        m_parentDescriptor = parentDescriptor;
        m_parentClass = m_parentDescriptor.getJavaClass();
    }
    
    /**
     * INTERNAL:
     */
    protected ReadObjectQuery getQuery() {
        if (m_query == null) {
            m_query = new ReadObjectQuery(m_parentClass);
            
            Expression selectionCriteria = null;
            Iterator keys = m_queryKeyFields.keySet().iterator();
            ExpressionBuilder builder = new ExpressionBuilder();
            
            while (keys.hasNext()) {
                String keyField = ((DatabaseField) keys.next()).getQualifiedName();
                
                if (selectionCriteria == null) {
                    selectionCriteria = builder.getField(keyField).equal(builder.getParameter(keyField));
                } else {
                    selectionCriteria.and(builder.getField(keyField).equal(builder.getParameter(keyField)));
                }
                
                m_query.addArgument(keyField);
            }
            
            m_query.setSelectionCriteria(selectionCriteria);
            m_query.setShouldUseWrapperPolicy(false);
        }
        
        return m_query;
    }
    
    /**
     * INTERNAL:
     */
     protected DatabaseMapping getParentMapping() {
        // If the query is null, then we have not been initialized. Try to
        // look up a parent mapping first if we have lookup fields. For a 
        // 1-M we can not perform the getMappingForField until the fields 
        // have been initialized.
        // If the parent mapping is not found, a query will be initialized
        // and the following lookup will no longer hit.
        if (m_parentMapping == null && m_lookForParentMapping && m_query == null) {
            Iterator<DatabaseField> itFields = m_queryKeyFields.values().iterator();
            while(itFields.hasNext()) {
                DatabaseMapping mapping = m_descriptor.getObjectBuilder().getMappingForField(itFields.next());
                
                if(mapping == null) {
                    // at least one field is not mapped therefore no parent mapping exists.
                    m_parentMapping = null;
                    break;
                } else if(mapping.isObjectReferenceMapping()) {
                    if(m_parentMapping == null) {
                        m_parentMapping = mapping;
                    } else {
                        if(m_parentMapping != mapping) {
                            // there's more than one mapping therefore no parent mapping exists. 
                            m_parentMapping = null;
                            break;
                        }
                    }
                }
            }
        }
        
        return m_parentMapping;
     }
     
     /**
      * Get the descriptor that really represents this object
      * In the case of inheritance, the object may represent a subclass of class the descriptor
      * represents. 
      * 
      * If there is no InheritancePolicy, we return our parentDescriptor
      * If there is inheritance we will search for a descriptor that represents parentObj and
      * return that descriptor
      * @param parentObj
      * @return
      */
     protected ClassDescriptor getParentDescriptorFromInheritancePolicy(Object parentObj){
         ClassDescriptor realParentDescriptor = m_parentDescriptor;
         if (realParentDescriptor.hasInheritance()){
             InheritancePolicy inheritancePolicy = realParentDescriptor.getInheritancePolicy();
             ClassDescriptor childDescriptor = inheritancePolicy.getDescriptor(parentObj.getClass());
             if (childDescriptor != null){
                 realParentDescriptor = childDescriptor;
             }
         }
         return realParentDescriptor;
     }

    /**
     * INTERNAL:
     */
     protected AbstractRecord getMappedTranslationRow(Object changedObj, UnitOfWorkImpl uow) {
         AbstractRecord translationRow = new DatabaseRecord();
         Iterator<Map.Entry<DatabaseField, DatabaseField>> it = m_mappedQueryKeyFields.entrySet().iterator();
         while(it.hasNext()) {
             Map.Entry<DatabaseField, DatabaseField> entry = it.next();
             Object value = m_descriptor.getObjectBuilder().extractValueFromObjectForField(changedObj, entry.getValue(), uow);
             translationRow.add(entry.getKey(), value);
         }
         return translationRow;
     }
    
     /**
      * INTERNAL:
      */
      protected AbstractRecord getUnmappedTranslationRow(Object changedObj, UnitOfWorkImpl uow) {
         AbstractRecord unmappedFieldsQueryTranslationRow = new DatabaseRecord();
         Iterator<DatabaseField> itPrimaryKey = m_descriptor.getPrimaryKeyFields().iterator();
         while (itPrimaryKey.hasNext()) {
             DatabaseField primaryKey = itPrimaryKey.next();
             Object value = m_descriptor.getObjectBuilder().extractValueFromObjectForField(changedObj, primaryKey, uow);
             unmappedFieldsQueryTranslationRow.add(primaryKey, value);
         }
         List result = (List)uow.executeQuery(m_unmappedFieldsQuery, unmappedFieldsQueryTranslationRow);
         if(result == null || result.isEmpty()) {
             // the object is not in the db
             return null;
         }
         
         AbstractRecord unmappedValues = (AbstractRecord)result.get(0);

         AbstractRecord translationRow = new DatabaseRecord();
         Iterator<Map.Entry<DatabaseField, DatabaseField>> it = m_unmappedQueryKeyFields.entrySet().iterator();
         while(it.hasNext()) {
             Map.Entry<DatabaseField, DatabaseField> entry = it.next();
             Object value = unmappedValues.get(entry.getValue());
             translationRow.add(entry.getKey(), value);
         }
         return translationRow;
      }
     
     /**
      * INTERNAL:
      * Identify mapped and not mapped fields (should be done once).
      * The result - either two non-empty Maps m_unmappedQueryKeyFields and m_mappedQueryKeyFields,
      * or m_unmappedQueryKeyFields == null and m_mappedQueryKeyFields == m_queryKeyFields.
      */
     public void initUnmappedFields(UnitOfWorkImpl uow) {
         if(!m_hasCheckedForUnmappedFields) {
             m_mappedQueryKeyFields = new HashMap<DatabaseField, DatabaseField>();
             m_unmappedQueryKeyFields = new HashMap<DatabaseField, DatabaseField>();
             Iterator<Map.Entry<DatabaseField, DatabaseField>> it = m_queryKeyFields.entrySet().iterator();
             while(it.hasNext()) {
                 Map.Entry<DatabaseField, DatabaseField> entry = it.next();
                 if(m_descriptor.getObjectBuilder().getMappingForField(entry.getValue()) == null) {
                     m_unmappedQueryKeyFields.put(entry.getKey(), entry.getValue());
                 } else {
                     m_mappedQueryKeyFields.put(entry.getKey(), entry.getValue());
                 }
             }
             if(m_unmappedQueryKeyFields.isEmpty()) {
                 m_unmappedQueryKeyFields = null;
                 m_mappedQueryKeyFields = m_queryKeyFields;
             }
             initUnmappedFieldsQuery(uow);
             m_hasCheckedForUnmappedFields = true;
         }
     }

     /**
      * INTERNAL:
      * This method called in case there are m_unmappedQueryKeyFields.
      * It creates a query that would fetch the values for this fields from the db.
      */
     public void initUnmappedFieldsQuery(UnitOfWorkImpl uow) {
         if(m_unmappedFieldsQuery == null) {
             m_unmappedFieldsQuery = new DataReadQuery();

             Expression whereClause = null;
             Expression builder = new ExpressionBuilder();
             Iterator<DatabaseField> itPrimaryKey = m_descriptor.getPrimaryKeyFields().iterator();
             while (itPrimaryKey.hasNext()) {
                 DatabaseField primaryKey = itPrimaryKey.next();
                 Expression expression = builder.getField(primaryKey).equal(builder.getParameter(primaryKey));
                 whereClause = expression.and(whereClause);
                 m_unmappedFieldsQuery.addArgument(primaryKey.getQualifiedName());
             }

             SQLSelectStatement statement = new SQLSelectStatement();
             Iterator<DatabaseField> itUnmappedFields = m_unmappedQueryKeyFields.values().iterator();
             while (itUnmappedFields.hasNext()) {
                 DatabaseField field = itUnmappedFields.next();
                 statement.addField(field);
             }
             
             statement.setWhereClause(whereClause);
             statement.normalize(uow.getParent(), m_descriptor);
             m_unmappedFieldsQuery.setSQLStatement(statement);
             m_unmappedFieldsQuery.setSessionName(m_descriptor.getSessionName());
         }
     }
     
     /**
     * INTERNAL:
     */
    public void lockNotifyParent(Object obj, UnitOfWorkChangeSet changeSet, UnitOfWorkImpl uow) {
        Object parentObj = null;
        
        // Check for a parent object via the parent (back pointer) mapping first.
        DatabaseMapping parentMapping = getParentMapping();
        if (parentMapping != null && parentMapping.isObjectReferenceMapping()) {
            parentObj = parentMapping.getRealAttributeValueFromObject(obj, uow);
        }

        // If the parent object is still null at this point, try a query.
        // check out why no query keys.
        if (parentObj == null) {
            AbstractRecord translationRow; 
            if(m_shouldHandleUnmappedFields) {
                // should look for unmapped fields.
                initUnmappedFields(uow);
                if(m_unmappedQueryKeyFields != null) {
                    // there are some unmapped fields - fetch the values for the from the db.
                    AbstractRecord unmappedTranslationRow = getUnmappedTranslationRow(obj, uow);
                    if(unmappedTranslationRow == null) {
                        // the object is not yet in the db
                        return;
                    } else {
                        // merge mapped and unmapped values into the single translation row.
                        translationRow = getMappedTranslationRow(obj, uow); 
                        translationRow.putAll(unmappedTranslationRow);
                    }
                } else {
                    // no unmapped fields
                    translationRow = getMappedTranslationRow(obj, uow); 
                }
            } else {
                // no unmapped fields
                translationRow = getMappedTranslationRow(obj, uow); 
            }
            // the query is set to return an unwrapped object.
            parentObj = uow.executeQuery(getQuery(), translationRow);

        } else {
            // make sure the parent object is unwrapped.
            if (m_parentDescriptor.hasWrapperPolicy()) {
                m_parentDescriptor.getWrapperPolicy().unwrapObject(parentObj, uow);   
            }
        }
        ClassDescriptor realParentDescriptor = m_parentDescriptor;
        if (parentObj != null){
            realParentDescriptor = getParentDescriptorFromInheritancePolicy(parentObj);
        }

        // If we have a parent object, force update the version field if one 
        // exists, and keep firing the notification up the chain.
        // Otherwise, do nothing.
        if (parentObj != null) {
            // Need to check if we are a non cascade locking node within a 
            // cascade locking policy chain.
            if (realParentDescriptor.usesOptimisticLocking() && realParentDescriptor.getOptimisticLockingPolicy().isCascaded()) {
                ObjectChangeSet ocs = realParentDescriptor.getObjectBuilder().createObjectChangeSet(parentObj, changeSet, uow);

                if (!ocs.hasForcedChangesFromCascadeLocking()) {
                    ocs.setHasForcedChangesFromCascadeLocking(true);
                    changeSet.addObjectChangeSet(ocs, uow, true);
                }
            }
        
            // Keep sending the notification up the chain ...
            if (realParentDescriptor.hasCascadeLockingPolicies()) {
                for (CascadeLockingPolicy policy : realParentDescriptor.getCascadeLockingPolicies()) {
                    policy.lockNotifyParent(parentObj, changeSet, uow);
                }
            }
        }
    }
    
    /**
     * INTERNAL:
     */
    public void setQueryKeyFields(Map<DatabaseField, DatabaseField> queryKeyFields) {
        setQueryKeyFields(queryKeyFields, true);
    }
    
    /**
     * INTERNAL:
     */
    public void setQueryKeyFields(Map<DatabaseField, DatabaseField> queryKeyFields, boolean lookForParentMapping) {
        m_queryKeyFields = queryKeyFields;
        m_mappedQueryKeyFields = m_queryKeyFields; 
        this.m_lookForParentMapping = lookForParentMapping;
    }
    
    /**
     * INTERNAL:
     * Indicates whether to expect unmapped fields.
     * That should be set to true for UnidirectionalOneToManyMapping.
     */
    public void setShouldHandleUnmappedFields(boolean shouldHandleUnmappedFields) {
        m_shouldHandleUnmappedFields = shouldHandleUnmappedFields;
    }

    /**
     * INTERNAL:
     */
    public boolean shouldHandleUnmappedFields() {
        return m_shouldHandleUnmappedFields;
    }
}
