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
 *     08/15/2008-1.0.1 Chris Delahunt 
 *       - 237545: List attribute types on OneToMany using @OrderBy does not work with attribute change tracking
 ******************************************************************************/  
package org.eclipse.persistence.internal.queries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.annotations.CacheKeyType;
import org.eclipse.persistence.internal.identitymaps.CacheId;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.ChangeRecord;
import org.eclipse.persistence.internal.sessions.ObjectChangeSet;
import org.eclipse.persistence.internal.sessions.CollectionChangeRecord;
import org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.sessions.DatabaseRecord;

/**
 * <p><b>Purpose</b>: A ListContainerPolicy is ContainerPolicy whose container class
 * implements the List interface.  This signifies that the collection has order
 * <p>
 * <p><b>Responsibilities</b>:
 * Provide the functionality to operate on an instance of a List.
 *
 * @see ContainerPolicy
 * @see CollectionContainerPolicy
 */
public class ListContainerPolicy extends CollectionContainerPolicy {
    /**
     * INTERNAL:
     * Construct a new policy.
     */
    public ListContainerPolicy() {
        super();
    }

    /**
     * INTERNAL:
     * Construct a new policy for the specified class.
     */
    public ListContainerPolicy(Class containerClass) {
        super(containerClass);
    }
    
    /**
     * INTERNAL:
     * Construct a new policy for the specified class name.
     */
    public ListContainerPolicy(String containerClassName) {
        super(containerClassName);
    }
    
    /**
     * INTERNAL:
     * Returns the element at the specified position in this list.
     * The session may be required to unwrap for the wrapper policy.
     */
    public Object get(int index, Object container, AbstractSession session){
        if ( (index <0) || (index>= sizeFor(container)) ) {
            return null;
        }
        Object object  = ((List)container).get(index);
        if (hasElementDescriptor() && getElementDescriptor().hasWrapperPolicy()) {
            object = getElementDescriptor().getObjectBuilder().unwrapObject(object, session);
        }
        return object;
    }

    /**
     * INTERNAL:
     * Validate the container type.
     */
    @Override
    public boolean isValidContainer(Object container) {
        // PERF: Use instanceof which is inlined, not isAssignable which is very inefficent.
        return container instanceof List;
    }

    /**
     * INTERNAL:
     * Returns true if the collection has order
     *
     * @see ContainerPolicy#iteratorFor(java.lang.Object)
     */
    @Override
    public boolean hasOrder() {
        return true;
    }

    @Override
    public boolean isListPolicy() {
        return true;
    }
    
    /**
     * INTERNAL:
     * Returns the index in this list of the first occurrence of the specified element, 
     * or -1 if this list does not contain this element
     * The session may be required to unwrap for the wrapper policy.
     */
    public int indexOf(Object element, Object container, AbstractSession session) {
       if (hasElementDescriptor() && getElementDescriptor().hasWrapperPolicy()) {
           // The wrapper for the object must be removed.
           int count = -1;
           Object iterator = iteratorFor(container);
           while (hasNext(iterator)) {
               count++;
               Object next = next(iterator);
               if (getElementDescriptor().getObjectBuilder().unwrapObject(next, session).equals(element)) {
                   return count;
               }
           }
           return -1;
       } else {
           return ((List)container).indexOf(element);
       }
    }

    /**
     * This method is used to bridge the behavior between Attribute Change Tracking and
     * deferred change tracking with respect to adding the same instance multiple times.
     * Each ContainerPolicy type will implement specific behavior for the collection 
     * type it is wrapping.  These methods are only valid for collections containing object references
     */
    @Override
    public void recordAddToCollectionInChangeRecord(ObjectChangeSet changeSetToAdd, CollectionChangeRecord collectionChangeRecord){
        if (collectionChangeRecord.getRemoveObjectList().containsKey(changeSetToAdd)) {
            collectionChangeRecord.getRemoveObjectList().remove(changeSetToAdd);
        } else {
            if (collectionChangeRecord.getAddObjectList().containsKey(changeSetToAdd)){
                collectionChangeRecord.getAddOverFlow().add(changeSetToAdd);
            }else{
                collectionChangeRecord.getAddObjectList().put(changeSetToAdd, changeSetToAdd);
            }
        }
    }
    
    /**
     * This method is used to bridge the behavior between Attribute Change Tracking and
     * deferred change tracking with respect to adding the same instance multiple times.
     * Each ContainerPolicy type will implement specific behavior for the collection 
     * type it is wrapping.  These methods are only valid for collections containing object references
     */
    @Override
    public void recordRemoveFromCollectionInChangeRecord(ObjectChangeSet changeSetToRemove, CollectionChangeRecord collectionChangeRecord){
        if(collectionChangeRecord.getAddObjectList().containsKey(changeSetToRemove)) {
            if (collectionChangeRecord.getAddOverFlow().contains(changeSetToRemove)){
                collectionChangeRecord.getAddOverFlow().remove(changeSetToRemove);
            }else {
                collectionChangeRecord.getAddObjectList().remove(changeSetToRemove);
            }
        } else {
            collectionChangeRecord.getRemoveObjectList().put(changeSetToRemove, changeSetToRemove);
        }
    }

    /**
     * INTERNAL:
     * Update a ChangeRecord to replace the ChangeSet for the old entity with the changeSet for the new Entity.  This is
     * used when an Entity is merged into itself and the Entity reference new or detached entities.
     */
    @Override
    public void updateChangeRecordForSelfMerge(ChangeRecord changeRecord, Object source, Object target, ForeignReferenceMapping mapping, UnitOfWorkChangeSet parentUOWChangeSet, UnitOfWorkImpl unitOfWork){
        Map<ObjectChangeSet, ObjectChangeSet> list = ((CollectionChangeRecord)changeRecord).getAddObjectList();
        
        ObjectChangeSet sourceSet = parentUOWChangeSet.getCloneToObjectChangeSet().get(source);
        if (list.containsKey(sourceSet)){
            ObjectChangeSet targetSet = ((UnitOfWorkChangeSet)unitOfWork.getUnitOfWorkChangeSet()).findOrCreateLocalObjectChangeSet(target, mapping.getReferenceDescriptor(), unitOfWork.isCloneNewObject(target));
            parentUOWChangeSet.addObjectChangeSetForIdentity(targetSet, target);
            list.remove(sourceSet);
            list.put(targetSet, targetSet);
            return;
        }
        List<ObjectChangeSet> overFlow = ((CollectionChangeRecord)changeRecord).getAddOverFlow();
        int index = 0;
        for (ObjectChangeSet changeSet: overFlow){
            if (changeSet == sourceSet){
                overFlow.set(index,((UnitOfWorkChangeSet)unitOfWork.getUnitOfWorkChangeSet()).findOrCreateLocalObjectChangeSet(target, mapping.getReferenceDescriptor(), unitOfWork.isCloneNewObject(target)));
                return;
            }
            ++index;
        }
    }
    

    /**
     * INTERNAL:
     * This method is used to load a relationship from a list of PKs. This list
     * may be available if the relationship has been cached.
     */
    public Object valueFromPKList(Object[] pks, AbstractRecord foreignKeys, ForeignReferenceMapping mapping, AbstractSession session){
        
        Object result = containerInstance(pks.length);
        Map<Object, Object> fromCache = session.getIdentityMapAccessor().getAllFromIdentityMapWithEntityPK(pks, elementDescriptor);

        
        DatabaseRecord translationRow = new DatabaseRecord();
        List foreignKeyValues = new ArrayList(pks.length - fromCache.size());
        for (int index = 0; index < pks.length; ++index){
            //it is a map so the keys are in the list but we do not need them in this case
            Object pk = pks[index];
            if (!fromCache.containsKey(pk)){
                if (this.elementDescriptor.getCachePolicy().getCacheKeyType() == CacheKeyType.CACHE_ID){
                    foreignKeyValues.add(Arrays.asList(((CacheId)pk).getPrimaryKey()));
                }else{
                    foreignKeyValues.add(pk);
                }
            }
        }
        if (!foreignKeyValues.isEmpty()){
            translationRow.put(ForeignReferenceMapping.QUERY_BATCH_PARAMETER, foreignKeyValues);
    
            ReadAllQuery query = new ReadAllQuery();
            query.setReferenceClass(this.elementDescriptor.getJavaClass());
            query.setIsExecutionClone(true);
            query.setTranslationRow(translationRow);
            query.setSession(session);
            query.setSelectionCriteria(elementDescriptor.buildBatchCriteriaByPK(query.getExpressionBuilder(), query));
            Collection<Object> temp = (Collection<Object>) session.executeQuery(query);
            if (temp.size() < foreignKeyValues.size()){
                //Not enough results have been found, this must be a stale collection with a removed
                //element.  Execute a reload based on FK.
                return session.executeQuery(mapping.getSelectionQuery(), foreignKeys);
            }
            for (Object element: temp){
                Object pk = elementDescriptor.getObjectBuilder().extractPrimaryKeyFromObject(element, session);
                fromCache.put(pk, element);
            }
        }
        for(Object key : pks){
            addInto(fromCache.get(key), result, session);
        }
        return result;
    }
}
