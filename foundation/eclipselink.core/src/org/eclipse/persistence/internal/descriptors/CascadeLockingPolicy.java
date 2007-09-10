/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.descriptors;

import java.util.Vector;
import java.util.Map;
import java.util.Iterator;
import java.util.Enumeration;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.queries.ReadObjectQuery;
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
    protected Map m_queryKeyFields;
    protected Vector m_mappingLookupFields;
    protected DatabaseMapping m_parentMapping;
    
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
        if (m_parentMapping == null && m_mappingLookupFields != null && m_query == null) {
            boolean firstMapping = true;
        
            for (Enumeration fields = m_mappingLookupFields.elements(); fields.hasMoreElements();) {
                DatabaseMapping mapping = m_descriptor.getObjectBuilder().getMappingForField((DatabaseField) fields.nextElement());
                
                if (mapping.isObjectReferenceMapping()) {
                    m_parentMapping = mapping;
                    break;
                }
            }
        }
        
        return m_parentMapping;
     }
     
    /**
     * INTERNAL:
     */
     protected AbstractRecord getTranslationRow(Object changedObj, UnitOfWorkImpl uow) {
    	 AbstractRecord translationRow = new DatabaseRecord();
        Iterator keys = m_queryKeyFields.keySet().iterator();
        
        while (keys.hasNext()) {
            DatabaseField keyField = (DatabaseField) keys.next();
            DatabaseField valueField = (DatabaseField) m_queryKeyFields.get(keyField);
            
            Object value = m_descriptor.getObjectBuilder().extractValueFromObjectForField(changedObj, valueField, uow);
            translationRow.add(keyField, value);
        }
        
        return translationRow;
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
            // the query is set to return an unwrapped object.
            parentObj = uow.executeQuery(getQuery(), getTranslationRow(obj, uow));
        } else {
            // make sure the parent object is unwrapped.
            if (m_parentDescriptor.hasWrapperPolicy()) {
                m_parentDescriptor.getWrapperPolicy().unwrapObject(parentObj, uow);   
            }
        }
    
        // If we have a parent object, force update the version field if one 
        // exists, and keep firing the notification up the chain.
        // Otherwise, do nothing.
        if (parentObj != null) {
            // Need to check if we are a non cascade locking node within a 
            // cascade locking policy chain.
            if (m_parentDescriptor.usesOptimisticLocking() && m_parentDescriptor.getOptimisticLockingPolicy().isCascaded()) {
                ObjectChangeSet ocs = m_parentDescriptor.getObjectBuilder().createObjectChangeSet(parentObj, changeSet, uow);
                
                if (!ocs.hasForcedChangesFromCascadeLocking()) {
                    ocs.setHasForcedChangesFromCascadeLocking(true);
                    changeSet.addObjectChangeSet(ocs, uow, true);
                }
            }
        
            // Keep sending the notification up the chain ...
            if (m_parentDescriptor.hasCascadeLockingPolicies()) {
                for (Enumeration policies = m_parentDescriptor.getCascadeLockingPolicies().elements(); policies.hasMoreElements();) {
                    CascadeLockingPolicy policy = (CascadeLockingPolicy) policies.nextElement();
                    policy.lockNotifyParent(parentObj, changeSet, uow);
                }
            }
        }
    }
    
    /**
     * INTERNAL:
     */
    public void setQueryKeyFields(Map queryKeyFields) {
        setQueryKeyFields(queryKeyFields, true);
    }
    
    /**
     * INTERNAL:
     */
    public void setQueryKeyFields(Map queryKeyFields, boolean lookForParentMapping) {
        m_queryKeyFields = queryKeyFields;
     
        if (lookForParentMapping) {
            // Extract the mapping lookup fields.
            m_mappingLookupFields = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance();
            for (Iterator keys = m_queryKeyFields.keySet().iterator(); keys.hasNext(); ) {
                m_mappingLookupFields.add((DatabaseField) m_queryKeyFields.get(keys.next()));
            }
        }
    }
}
