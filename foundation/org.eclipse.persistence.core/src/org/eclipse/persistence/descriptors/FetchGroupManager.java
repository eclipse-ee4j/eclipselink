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
 *     05/19/2010-2.1 ailitchev - Bug 244124 - Add Nested FetchGroup 
 ******************************************************************************/  
package org.eclipse.persistence.descriptors;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.persistence.descriptors.changetracking.ObjectChangePolicy;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.queries.EntityFetchGroup;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.queries.FetchGroup;
import org.eclipse.persistence.queries.FetchGroupTracker;

/**
 * <p><b>Purpose</b>: The fetch group manager controls the named fetch groups defined at
 * the descriptor level. EclipseLink supports multiple, overlapped fetch groups, optionally with
 * one of them as the default fetch group.
 * <p>
 * The domain object must implement org.eclipse.persistence.queries.FetchGroupTracker interface,
 * in order to make use of the fetch group performance enhancement feature.
 * <p>
 * Please refer to FetchGroup class for the pros and cons of fetch group usage.
 *
 * @see org.eclipse.persistence.queries.FetchGroup
 * @see org.eclipse.persistence.queries.FetchGroupTracker
 *
 * @author King Wang, dclarke
 * @since TopLink 10.1.3.
 */
public class FetchGroupManager implements Cloneable {
    //The group map is keyed by the group name, valued by the fetch group object. 
    private Map<String, FetchGroup> fetchGroups = null;

    // EntityFetchGroups mapped by their AttributeNames Sets.
    private Map<Set<String>, EntityFetchGroup> entityFetchGroups = new ConcurrentHashMap();
    
    //default fetch group
    private FetchGroup defaultFetchGroup;
    private EntityFetchGroup defaultEntityFetchGroup;

    // full fetch group - contains all attributes, none of them nested.
    private FetchGroup fullFetchGroup;
    
    // minimal fetch group - contains primary key attribute(s) and version
    private FetchGroup minimalFetchGroup;
    
    // identity fetch group - contains primary key attribute(s) only
    private EntityFetchGroup idEntityFetchGroup;
    
    //ref to the descriptor
    private ClassDescriptor descriptor;
    
    // indicates whether defaultFetchGroup should be copied from the parent if not set.
    private boolean shouldUseInheritedDefaultFetchGroup = true;
    
    /**
     * Constructor
     */
    public FetchGroupManager() {
    }

    /**
     * Add a named fetch group to the descriptor
     */
    public void addFetchGroup(FetchGroup group) {
        //create a new fetch group and put it in the group map.
        getFetchGroups().put(group.getName(), group);
    }

    /**
     * Return the fetch group map: keyed by the group name, valued by the fetch group object.
     */
    public Map<String, FetchGroup> getFetchGroups() {
        if (this.fetchGroups == null) {
            //lazy initialized
            this.fetchGroups = new HashMap<String, FetchGroup>(2);
        }

        return this.fetchGroups;
    }

    /**
     * Return the descriptor-level default fetch group.
     * All read object and read all queries would use the default fetch group if no fetch group
     * is explicitly defined for the query, unless setShouldUseDefaultFetchGroup(false); is also
     * called on the query.
     *
     * Default fetch group should be used carefully. It would be beneficial if most of the system queries
     * are for the subset of the object, so un-needed attributes data would not have to be read, and the
     * users do not have to setup every query for the given fetch group, as default one is always used.
     * However, if queries on object are mostly use case specific and not systematic, using default fetch group
     * could cause undesirable extra round-trip and performance degradation.
     *
     * @see org.eclipse.persistence.queries.ObjectLevelReadQuery#setShouldUseDefaultFetchGroup(boolean)
     */
    public FetchGroup getDefaultFetchGroup() {
        return this.defaultFetchGroup;
    }

    public EntityFetchGroup getDefaultEntityFetchGroup() {
        return this.defaultEntityFetchGroup;
    }

    public FetchGroup createMinimalFetchGroup() {
        return this.minimalFetchGroup.clone();
    }

    public boolean isMinimalFetchGroup(FetchGroup fetchGroup) {
        return this.minimalFetchGroup.equals(fetchGroup);
    }

    public EntityFetchGroup getIdEntityFetchGroup() {
        return this.idEntityFetchGroup;
    }

    public void addMinimalFetchGroup(FetchGroup fetchGroup) {
        Iterator<String> it = this.minimalFetchGroup.getAttributeNames().iterator();
        while(it.hasNext()) {
            String name = it.next();
            if(!fetchGroup.containsAttribute(name)) {
                fetchGroup.addAttribute(name);
            }
        }
    }
    
    public FetchGroup createDefaultFetchGroup() {
        return this.defaultFetchGroup.clone();
    }

    public FetchGroup createFullFetchGroup() {
        return this.fullFetchGroup.clone();
    }

    public boolean isFullFetchGroup(FetchGroup fetchGroup) {
        return this.fullFetchGroup.equals(fetchGroup);
    }

    public EntityFetchGroup getEntityFetchGroup(Set<String> attributeNames) {
        EntityFetchGroup entityFetchGroup = this.entityFetchGroups.get(attributeNames);
        if(entityFetchGroup == null) {
            entityFetchGroup = new EntityFetchGroup(attributeNames);
            // EntityFetchGroup that contains all attributes is equivalent to no fetch group
            if(entityFetchGroup.equals(this.fullFetchGroup)) {
                return null;
            }
            this.entityFetchGroups.put(attributeNames, entityFetchGroup);
        }
        
        return entityFetchGroup;
    }
    
    public EntityFetchGroup getEntityFetchGroup(FetchGroup fetchGroup) {
        EntityFetchGroup entityFetchGroup = this.entityFetchGroups.get(fetchGroup.getAttributeNames());
        if(entityFetchGroup == null) {
            entityFetchGroup = new EntityFetchGroup(fetchGroup);
            // EntityFetchGroup that contains all attributes is equivalent to no fetch group
            if(entityFetchGroup.equals(this.fullFetchGroup)) {
                return null;
            }
            this.entityFetchGroups.put(entityFetchGroup.getAttributeNames(), entityFetchGroup);
        }
        
        return entityFetchGroup;
    }
    
    /**
     * Return a pre-defined named fetch group.
     * 
     * Lookup the FetchGroup to use given a name taking into
     * consideration descriptor inheritance to ensure parent descriptors are
     * searched for named FetchGroups. 
     */
    public FetchGroup getFetchGroup(String groupName) {
        FetchGroup fg =  this.fetchGroups.get(groupName);
        if (fg == null && getDescriptor().isChildDescriptor()) {
            ClassDescriptor current = this.descriptor;

            while (fg == null && current.isChildDescriptor()) {
                ClassDescriptor parent = current.getInheritancePolicy().getParentDescriptor();
                if (parent.hasFetchGroupManager()) {
                    fg = parent.getFetchGroupManager().getFetchGroup(groupName);
                }
                current = parent;
            }
        }
        return fg;
    }

    /**
     * Lookup the FetchGroup to use given a name and a flag taking into
     * consideration descriptor inheritance to ensure parent descriptors are
     * searched for named and default FetchGroup. This is used to determine the
     * FetchGroup to use in a query's prepare.
     */
    public FetchGroup getFetchGroup(String groupName, boolean useDefault) {
        FetchGroup fg = null;

        if (groupName != null) {
            fg = getFetchGroup(groupName);
        }

        // Process default using hierarchy
        if (fg == null && useDefault) {
            fg = getDefaultFetchGroup();
        }

        return fg;
    }

    /**
     * Set the descriptor-level default fetch group.
     * All read object and read all queries would use the default fetch group if no fetch group is
     * explicitly defined for the query, unless setShouldUseDefaultFetchGroup(false);
     * is also called on the query.
     *
     * Default fetch group should be used carefully. It would be beneficial if most of the system queries
     * are for the subset of the object, so un-needed attributes data would not have to be read, and the
     * users do not have to setup every query for the given fetch group, as default one is always used.
     * However, if queries on object are mostly use case specific and not systematic, using default fetch group
     * could cause undesirable extra round-trip and performance degradation.
     *
     * @see org.eclipse.persistence.queries.ObjectLevelReadQuery#setShouldUseDefaultFetchGroup(boolean)
     */
    public void setDefaultFetchGroup(FetchGroup newDefaultFetchGroup) {
        if(this.defaultFetchGroup != newDefaultFetchGroup) {
            if(this.entityFetchGroups != null) {
                if(newDefaultFetchGroup != null) {
                    // minimalFecthGroup is set during initialization
                    if(this.minimalFetchGroup != null) {
                        addMinimalFetchGroup(newDefaultFetchGroup);
                        this.defaultEntityFetchGroup = this.getEntityFetchGroup(newDefaultFetchGroup);
                    }
                } else {
                    this.defaultEntityFetchGroup = null;
                }
            }
            this.defaultFetchGroup = newDefaultFetchGroup;
        }
    }

    /**
     * INTERNAL:
     * Return true if the object is partially fetched and cached.
     * It applies to the query with fetch group.
     */
    public boolean isPartialObject(Object domainObject) {
        if (domainObject != null) {
            FetchGroup fetchGroupInCache = ((FetchGroupTracker)domainObject)._persistence_getFetchGroup();

            //if the fetch group reference is not null, it means the object is partial.
            return (fetchGroupInCache != null);
        }
        return false;
    }

    /**
     * INTERNAL:
     * Return if the cached object data is sufficiently valid against a fetch group
     */
    public boolean isObjectValidForFetchGroup(Object object, FetchGroup fetchGroup) {
        FetchGroup groupInObject = ((FetchGroupTracker)object)._persistence_getFetchGroup();
        return (groupInObject == null) || groupInObject.isSupersetOf(fetchGroup);
    }

    /**
     * INTERNAL:
     * Return true if the cached object data should be written in clone.
     * It is used in Fetch Group case when filling in the clone from the cached object.
     */
    public boolean shouldWriteInto(Object cachedObject, Object clone) {
        if (isPartialObject(clone)) {
            FetchGroup fetchGroupInSrc = ((FetchGroupTracker)cachedObject)._persistence_getFetchGroup();
            FetchGroup fetchGroupInTarg = ((FetchGroupTracker)clone)._persistence_getFetchGroup();

            //if the target fetch group is not null (i.e. fully fetched object) or if partially fetched, it's not a superset of that of the source, 
            //or if refresh is required, should always write (either refresh or revert) data from the cache to the clones.
            return fetchGroupInTarg != null || fetchGroupInTarg.isSupersetOf(fetchGroupInSrc) || ((FetchGroupTracker) cachedObject)._persistence_shouldRefreshFetchGroup();
        }
        return false;
    }

    /**
     * INTERNAL:
     * Write data of the partially fetched object into the working and backup clones
     */
    public void writePartialIntoClones(Object partialObject, Object workingClone, UnitOfWorkImpl uow) {
        FetchGroup fetchGroupInClone = ((FetchGroupTracker)workingClone)._persistence_getFetchGroup();
        FetchGroup fetchGroupInObject = ((FetchGroupTracker)partialObject)._persistence_getFetchGroup();
        Object backupClone = uow.getBackupClone(workingClone, descriptor);

        // Update fetch group in clone as the union of two,
        // do this first to avoid fetching during method access.
        FetchGroup union = unionFetchGroups(fetchGroupInObject, fetchGroupInClone);
        // Finally, update clone's fetch group reference.
        setObjectFetchGroup(workingClone, union, uow);
        if (workingClone != backupClone) {
            setObjectFetchGroup(backupClone, union, uow);
        }
        ObjectChangePolicy policy = descriptor.getObjectChangePolicy();
        // Turn it 'off' to prevent unwanted events.
        policy.dissableEventProcessing(workingClone);
        try {
            //if refresh is set, force to fill in fetch group data
            if (((FetchGroupTracker)partialObject)._persistence_shouldRefreshFetchGroup()) {
                //refresh and fill in the fetch group data
                refreshFetchGroupIntoClones(partialObject, workingClone, backupClone, fetchGroupInObject, fetchGroupInClone, uow);
            } else {//no refresh is enforced
                //revert the unfetched attributes of the clones.
                revertDataIntoUnfetchedAttributesOfClones(partialObject, workingClone, backupClone, fetchGroupInObject, fetchGroupInClone, uow);
            }
        } finally {
            policy.enableEventProcessing(workingClone);
        }
    }

    /**
     * INTERNAL:
     * Refresh the fetch group data into the working and backup clones.
     * This is called if refresh is enforced
     */
    // TODO-244124-dclarke: Needs to be updated to reflect new FetchGroup behaviour
    private void refreshFetchGroupIntoClones(Object cachedObject, Object workingClone, Object backupClone, FetchGroup fetchGroupInObject, FetchGroup fetchGroupInClone, UnitOfWorkImpl uow) {
        Vector mappings = descriptor.getMappings();
        boolean isObjectPartial = (fetchGroupInObject != null);
        Set fetchedAttributes = isObjectPartial ? fetchGroupInObject.getAttributeNames() : null;
        int size = mappings.size();
        for (int index = 0; index < size; index++) {
            DatabaseMapping mapping = (DatabaseMapping)mappings.get(index);
            if ((!isObjectPartial) || ((fetchedAttributes != null) && fetchedAttributes.contains(mapping.getAttributeName()))) {
                // Only refresh the fetched attributes into clones.
                mapping.buildClone(cachedObject, workingClone, uow);
                if (workingClone != backupClone) {
                    mapping.buildClone(workingClone, backupClone, uow);
                }
            }
        }
    }

    /**
     * Revert the clones' unfetched attributes, and leave fetched ones intact.
     */
    private void revertDataIntoUnfetchedAttributesOfClones(Object cachedObject, Object workingClone, Object backupClone, FetchGroup fetchGroupInObject, FetchGroup fetchGroupInClone, UnitOfWorkImpl uow) {
        // Fetched attributes set in working clone.
        Set fetchedAttributesClone = fetchGroupInClone.getAttributeNames();
        // Fetched attributes set in cached object.
        Set fetchedAttributesCached = null;
        if (fetchGroupInObject != null) {
            fetchedAttributesCached = fetchGroupInObject.getAttributeNames();
        }

        for (DatabaseMapping mapping : descriptor.getMappings()) {
            String attributeName = mapping.getAttributeName();
            // Only revert the attribute which is fetched by the cached object, but not fetched by the clone.
            if ((fetchedAttributesCached == null || fetchedAttributesCached.contains(attributeName)) && !fetchedAttributesClone.contains(attributeName)) {
                mapping.buildClone(cachedObject, workingClone, uow);
                if (workingClone != backupClone) {
                    mapping.buildClone(workingClone, backupClone, uow);
                }
            }
        }
    }

    /**
     * INTERNAL:
     * Copy fetch group reference from the source object to the target
     */
    public void copyFetchGroupInto(Object source, Object target, AbstractSession session) {
        if (isPartialObject(source)) {
            setObjectFetchGroup(target, ((FetchGroupTracker)source)._persistence_getFetchGroup(), session);
        }
    }

    /**
     * INTERNAL:
     * Union the fetch group of the domain object with the new fetch group.
     */
    public void unionFetchGroupIntoObject(Object source, FetchGroup newFetchGroup, AbstractSession session) {
        setObjectFetchGroup(source, unionFetchGroups(((FetchGroupTracker)source)._persistence_getFetchGroup(), newFetchGroup), session);
    }

    /**
     * INTERNAL:
     * Union two fetch groups.
     */
    public FetchGroup unionFetchGroups(FetchGroup first, FetchGroup second) {
        if ((first == null) || (second == null)) {
            return null;
        }

        //return the superset if applied
        if (first == second || first.isSupersetOf(second)) {
            return first;
        } else if (second.isSupersetOf(first)) {
            return second;
        }

        Set<String> unionAttributeNames = new HashSet();
        unionAttributeNames.addAll(first.getAttributeNames());
        unionAttributeNames.addAll(second.getAttributeNames());
        return getEntityFetchGroup(unionAttributeNames);
    }

    /**
     * INTERNAL:
     * Reset object attributes to the default values.
     */
    public void reset(Object source) {
        ((FetchGroupTracker)source)._persistence_resetFetchGroup();
    }

    /**
     * INTERNAL:
     */
    public FetchGroup getObjectFetchGroup(Object domainObject) {
        if (domainObject != null) {
            return ((FetchGroupTracker)domainObject)._persistence_getFetchGroup();
        }
        return null;
    }
    
    /**
     * INTERNAL:
     */
    public void setObjectFetchGroup(Object source, FetchGroup fetchGroup, AbstractSession session) {
        FetchGroupTracker tracker = (FetchGroupTracker)source;
        if(fetchGroup == null) {
            tracker._persistence_setFetchGroup(null);
            tracker._persistence_setSession(null);
        } else {
            if(fetchGroup.isEntityFetchGroup()) {
                // it's EntityFetchGroup - just set it
                tracker._persistence_setFetchGroup(fetchGroup);
                tracker._persistence_setSession(session);
            } else {
                Set<String> attributeNames = fetchGroup.getAttributeNames();
                EntityFetchGroup entityFetchGroup = this.getEntityFetchGroup(fetchGroup);
                if(entityFetchGroup != null) {
                    tracker._persistence_setFetchGroup(entityFetchGroup);
                    tracker._persistence_setSession(session);
                } else {
                    tracker._persistence_setFetchGroup(null);
                    tracker._persistence_setSession(null);
                }
            }
        }        
    }

    /**
     * INTERNAL:
     * Set if the tracked object is fetched from executing a query with or without refresh.
     */
    public void setRefreshOnFetchGroupToObject(Object source, boolean shouldRefreshOnFetchgroup) {
        ((FetchGroupTracker)source)._persistence_setShouldRefreshFetchGroup(shouldRefreshOnFetchgroup);
    }

    /**
     * Return true if the attribute of the object has already been fetched
     */
    public boolean isAttributeFetched(Object entity, String attributeName) {
        FetchGroup fetchGroup = ((FetchGroupTracker) entity)._persistence_getFetchGroup();
        if (fetchGroup == null) {
            return true;
        }        
        return fetchGroup.containsAttribute(attributeName);
    }

    /**
     * PUBLIC:
     * Return the referenced descriptor.
     */
    public ClassDescriptor getDescriptor() {
        return descriptor;
    }

    /**
     * Set the referenced descriptor.
     */
    public void setDescriptor(ClassDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    /**
     * Return true if a fetch group exists for the given group name.
     */
    public boolean hasFetchGroup(String groupName) {
        return getFetchGroups().containsKey(groupName);
    }
    
    /**
     * INTERNAL: Initialize the fetch groups. XXX-dclarke: added support for
     * reinit the query manager's queries if they exist
     */
    public void initialize(AbstractSession session) throws DescriptorException {
        if (!(Helper.classImplementsInterface(getDescriptor().getJavaClass(), ClassConstants.FetchGroupTracker_class))) {
            //to use fetch group, the domain class must implement FetchGroupTracker interface
            session.getIntegrityChecker().handleError(DescriptorException.needToImplementFetchGroupTracker(getDescriptor().getJavaClass(), getDescriptor()));
        }
        this.minimalFetchGroup = new FetchGroup();
        this.fullFetchGroup = new FetchGroup();
        for (DatabaseMapping mapping : getDescriptor().getMappings()) {
            String name = mapping.getAttributeName();
            if(mapping.isPrimaryKeyMapping()) {
                this.minimalFetchGroup.addAttribute(name);
            }
            this.fullFetchGroup.addAttribute(name);
        }
        // currently minimalFetchGroup contains only PrimaryKey - that's what idEntityFetchGroup will consist of. 
        this.idEntityFetchGroup = getEntityFetchGroup(this.minimalFetchGroup);
        if(this.descriptor.usesOptimisticLocking()) {
            DatabaseField lockField = this.descriptor.getOptimisticLockingPolicy().getWriteLockField();
            if (lockField != null) {
                DatabaseMapping lockMapping = getDescriptor().getObjectBuilder().getMappingForField(lockField);
                if (lockMapping != null) {
                    String attributeName = lockMapping.getAttributeName();
                    minimalFetchGroup.addAttribute(attributeName);
                }
            }
        }
        // Now minimalFetchGroup contains PrimaryKey plus locking field - getEntityFetchGroup call ensures
        // that corresponding EntityFetchGroup is cached in entityFetchGroups map.
        // Note that the new EntityFetchGroup is not created if there is no locking field.
        getEntityFetchGroup(this.minimalFetchGroup);
        
        // Create and cache EntityFetchGroups for named fetch groups.
        if(this.fetchGroups != null) {
            Iterator<FetchGroup> it = this.fetchGroups.values().iterator();
            while(it.hasNext()) {
                getEntityFetchGroup(it.next());
            }
        }
    }
    
    /**
     * INTERNAL:
     * postInitialize called for inheritance children first.
     * That allows to copy defaultFetchGroup from the parent only in case
     * it has been set by user (not automatically generated).
     */
    public void postInitialize(AbstractSession session) throws DescriptorException {
        if (!(Helper.classImplementsInterface(getDescriptor().getJavaClass(), ClassConstants.FetchGroupTracker_class))) {
            // initialize already threw exception here
            return;
        }
        if(this.defaultFetchGroup == null) {
            // Look up default fetch group set by user on parent descriptors
            if(this.descriptor.isChildDescriptor() && this.shouldUseInheritedDefaultFetchGroup) {
                ClassDescriptor current = this.descriptor;
                while(current.isChildDescriptor()) {
                    ClassDescriptor parent = current.getInheritancePolicy().getParentDescriptor();
                    if (parent.hasFetchGroupManager()) {
                        this.defaultFetchGroup = parent.getFetchGroupManager().getDefaultFetchGroup();
                        if(this.defaultFetchGroup != null) {
                            return;
                        }
                    }
                    current = parent;
                }
            }
            
            FetchGroup defaultCandidate = new FetchGroup();
            boolean hasLazy = false;
            for (DatabaseMapping mapping : getDescriptor().getMappings()) {
                String name = mapping.getAttributeName();
                if (mapping.isForeignReferenceMapping() || (!mapping.isLazy())) {
                    defaultCandidate.addAttribute(mapping.getAttributeName());
                } else {
                    hasLazy = true;
                }
            }
            if(hasLazy) {
                this.defaultFetchGroup = defaultCandidate;
            }
        }
        if(this.defaultFetchGroup != null) {
            addMinimalFetchGroup(this.defaultFetchGroup);
            this.defaultEntityFetchGroup = getEntityFetchGroup(this.defaultFetchGroup);
        }
    }
    
    /**
     * INTERNAL:
     * Clone the fetch group manager.
     */
    public Object clone() {
        try {
            return super.clone();
        } catch (Exception exception) {
            throw new InternalError(exception.toString());
        }
    }
    
    public void setShouldUseInheritedDefaultFetchGroup(boolean shouldUseInheritedDefaultFetchGroup) {
        this.shouldUseInheritedDefaultFetchGroup = shouldUseInheritedDefaultFetchGroup;
    }
    
    public boolean shouldUseInheritedDefaultFetchGroup() {
        return this.shouldUseInheritedDefaultFetchGroup;
    }
}
