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
package org.eclipse.persistence.descriptors;

import java.util.*;

import org.eclipse.persistence.descriptors.changetracking.ObjectChangePolicy;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.queries.FetchGroup;
import org.eclipse.persistence.queries.FetchGroupTracker;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;

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
 * @author King Wang
 * @since TopLink 10.1.3.
 */
public class FetchGroupManager implements Cloneable {
    //The group map is keyed by the group name, valued by the fetch group object. 
    private Map fetchGroups = null;

    //default fetch group
    private FetchGroup defaultFetchGroup;

    //ref to the descriptor
    private ClassDescriptor descriptor;
    
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
    public Map getFetchGroups() {
        if (fetchGroups == null) {
            //lazy initialized
            fetchGroups = new HashMap(2);
        }

        return fetchGroups;
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
        return defaultFetchGroup;
    }

    /**
     * Return a pre-defined named fetch group.
     */
    public FetchGroup getFetchGroup(String groupName) {
        return (FetchGroup)getFetchGroups().get(groupName);
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
        defaultFetchGroup = newDefaultFetchGroup;
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
            return (!((fetchGroupInTarg == null) || fetchGroupInTarg.isSupersetOf(fetchGroupInSrc)) || ((FetchGroupTracker)cachedObject)._persistence_shouldRefreshFetchGroup());
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
     * Refresh the fetch group data into the working and backup clones.
     * This is called if refresh is enforced
     */
    private void refreshFetchGroupIntoClones(Object cachedObject, Object workingClone, Object backupClone, FetchGroup fetchGroupInObject, FetchGroup fetchGroupInClone, UnitOfWorkImpl uow) {
        Vector mappings = descriptor.getMappings();
        boolean isObjectPartial = (fetchGroupInObject != null);
        Set fetchedAttributes = isObjectPartial ? fetchGroupInObject.getAttributes() : null;
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
        Vector mappings = descriptor.getMappings();
        // Fetched attributes set in working clone.
        Set fetchedAttributesClone = fetchGroupInClone.getAttributes();
        // Fetched attributes set in cached object.
        Set fetchedAttributesCached = null;
        if (fetchGroupInObject != null) {
            fetchedAttributesCached = fetchGroupInObject.getAttributes();
        }
        int size = mappings.size();
        for (int index = 0; index < size; index++) {
            DatabaseMapping mapping = (DatabaseMapping)mappings.get(index);
            String attributeName = mapping.getAttributeName();
            // Only revert the attribute which is fetched by the cached object, but not fetched by the clone.
            if (((fetchedAttributesCached == null) || fetchedAttributesCached.contains(attributeName)) && (!fetchedAttributesClone.contains(attributeName))) {
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
        if (first.isSupersetOf(second)) {
            return first;
        } else if (second.isSupersetOf(first)) {
            return second;
        }

        //otherwise, union two fetch groups
        StringBuffer unionGroupName = new StringBuffer(first.getName());
        unionGroupName.append("_");
        unionGroupName.append(second.getName());
        FetchGroup unionFetchGroup = new FetchGroup(unionGroupName.toString());
        unionFetchGroup.addAttributes(first.getAttributes());
        unionFetchGroup.addAttributes(second.getAttributes());
        return unionFetchGroup;
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
     * Reset object attributes to the default their values.
     */
    public void setObjectFetchGroup(Object source, FetchGroup fetchGroup, AbstractSession session) {
        FetchGroupTracker tracker = (FetchGroupTracker)source;
        tracker._persistence_setFetchGroup(fetchGroup);
        tracker._persistence_setSession(session);
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
    public boolean isAttributeFetched(Object object, String attributeName) {
        FetchGroup fetchgroup = ((FetchGroupTracker)object)._persistence_getFetchGroup();
        return (fetchgroup == null) || (fetchgroup.containsAttribute(attributeName));
    }

    /**
     * PUBLIC:
     * Return the referenced descriptor.
     */
    public ClassDescriptor getDescriptor() {
        return descriptor;
    }

    /**
     * INTERNAL:
     * Return the referenced descriptor.
     */
    public ClassDescriptor getClassDescriptor() {
		return getDescriptor();
    }

    /**
     * Set the referenced descriptor.
     */
    public void setDescriptor(ClassDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    /**
     * INTERNAL:
     * Prepare the query with the fetch group to add group attributes to the query
     * for partial reading.
     */
    public void prepareQueryWithFetchGroup(ObjectLevelReadQuery query) {
        // Initialize query's fetch group.
        query.initializeFetchGroup();
        if (!query.hasFetchGroup()) {
            // Simply return if fetch group is not defined.
            return;
        } else {
            if (query.isReportQuery()) {
                //fetch group does not work with report query
                throw QueryException.fetchGroupNotSupportOnReportQuery();
            }
            if (query.hasPartialAttributeExpressions()) {
                //fetch group does not work with partial attribute reading
                throw QueryException.fetchGroupNotSupportOnPartialAttributeReading();
            }
        }
        // Must ensure all primary key mapping attribute are in the fetch group.
        for (Iterator iterator = getDescriptor().getObjectBuilder().getPrimaryKeyMappings().iterator(); iterator.hasNext();) {
            DatabaseMapping mapping = (DatabaseMapping)iterator.next();
            query.getFetchGroup().addAttribute(mapping.getAttributeName());
        }
        // Ensure locking mapping is in fetch group.
        if (query.shouldMaintainCache() && getDescriptor().usesOptimisticLocking()) {
            DatabaseField lockField = getDescriptor().getOptimisticLockingPolicy().getWriteLockField();
            if (lockField != null) {
                DatabaseMapping lockMapping = getDescriptor().getObjectBuilder().getMappingForField(lockField);
                if (lockMapping != null) {
                    query.getFetchGroup().addAttribute(lockMapping.getAttributeName());
                }
            }
        }
    }
    
    /**
     * Return true if a fetch group exists for the given group name.
     */
    public boolean hasFetchGroup(String groupName) {
        return getFetchGroups().containsKey(groupName);
    }
    
    /**
     * INTERNAL:
     * Initialize the fetch groups.
     */
    public void initialize(AbstractSession session) throws DescriptorException {
        if (!(Helper.classImplementsInterface(getDescriptor().getJavaClass(), ClassConstants.FetchGroupTracker_class))) {
            //to use fetch group, the domain class must implement FetchGroupTracker interface
            session.getIntegrityChecker().handleError(DescriptorException.needToImplementFetchGroupTracker(getDescriptor().getJavaClass(), getDescriptor()));
        }
        if (getDefaultFetchGroup() == null) {
            // Check for lazy mappings to build default fetch group.
            FetchGroup fetchGroup = new FetchGroup();
            boolean hasLazy = false;
            for (DatabaseMapping mapping : getDescriptor().getMappings()) {
                if (mapping.isForeignReferenceMapping() || (!mapping.isLazy())) {
                    fetchGroup.addAttribute(mapping.getAttributeName());
                } else {
                    hasLazy = true;
                }
            }
            if (hasLazy) {
                setDefaultFetchGroup(fetchGroup);
            }
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
}
