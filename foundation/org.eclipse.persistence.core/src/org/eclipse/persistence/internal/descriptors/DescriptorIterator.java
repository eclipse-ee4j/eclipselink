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

import java.util.*;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.indirection.*;
import org.eclipse.persistence.internal.queries.AttributeItem;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.queries.AttributeGroup;
import org.eclipse.persistence.queries.FetchGroup;

/**
 * This class provides a generic way of using the descriptor information
 * to traverse an object graph.
 * Define a subclass, or an inner class, that implements at least
 * #iterate(Object) to implement a new traversal
 * feature without having to change the mapping classes or the object builder.
 * It provides functionality such as a cascading depth, a stack of visited object,
 * and a collection of the visited objects.
 *
 * NOTE:
 * If this works nicely the merge manager, remote traversals, and maybe
 * even aspects of the commit manager could be converted to use this class.
 */
public abstract class DescriptorIterator {
    public static final int NoCascading = 1;
    public static final int CascadePrivateParts = 2;
    public static final int CascadeAllParts = 3;
    protected Map visitedObjects;
    protected Stack visitedStack;
    protected AbstractSession session;
    protected DatabaseMapping currentMapping;
    protected ClassDescriptor currentDescriptor;
    protected AttributeItem currentItem;
    protected AttributeGroup currentGroup;
    protected boolean usesGroup;
    /* Ignored if usesGroup is false.
     * If set to true allows visiting the same object several times - 
     * as long as it hasn't been visited with the currentGroup.
     */
    protected boolean shouldTrackCurrentGroup;
    protected Object result;// this is a work area, typically used as a Collecting Parm
    protected boolean shouldIterateOverIndirectionObjects;
    protected boolean shouldIterateOverUninstantiatedIndirectionObjects;
    protected boolean shouldIterateOverWrappedObjects;
    protected boolean shouldIterateOnIndirectionObjects;
    protected boolean shouldIterateOnAggregates;
    protected boolean shouldIterateOnPrimitives;
    // false by default; true means if object has FetchGroup then don't iterate outside it.
    protected boolean shouldIterateOnFetchGroupAttributesOnly;
    protected boolean shouldBreak;
    protected int cascadeDepth;// see static constants below
    protected CascadeCondition cascadeCondition;

    /**
     * Construct a typical iterator:
     *    iterate over all the objects
     *    process the objects contained by "value holders"...
     *    ...but only if they have already been instantiated...
     *    ...and don't process the "value holders" themselves
     *    process "wrapped" objects
     *    skip aggregate objects
     *    skip primitives (Strings, Dates, Integers, etc.)
     */
    public DescriptorIterator() {
        // 2612538 - the default size of Map (32) is appropriate
        this.visitedObjects = new IdentityHashMap();
        this.visitedStack = new Stack();
        this.cascadeDepth = CascadeAllParts;
        this.shouldIterateOverIndirectionObjects = true;// process the objects contained by ValueHolders...
        this.shouldIterateOverUninstantiatedIndirectionObjects = false;// ...but only if they have already been instantiated...
        this.shouldIterateOnIndirectionObjects = false;// ...and don't process the ValueHolders themselves
        this.shouldIterateOverWrappedObjects = true;// process "wrapped" objects
        this.shouldIterateOnAggregates = false;
        this.shouldIterateOnPrimitives = false;
        this.shouldIterateOnFetchGroupAttributesOnly = false;
        this.shouldBreak = false;
        this.cascadeCondition = new CascadeCondition();
    }

    public int getCascadeDepth() {
        return cascadeDepth;
    }

    public ClassDescriptor getCurrentDescriptor() {
        return currentDescriptor;
    }

    public DatabaseMapping getCurrentMapping() {
        return currentMapping;
    }

    public AttributeItem getCurrentItem() {
        return this.currentItem;
    }

    public AttributeGroup getCurrentGroup() {
        return this.currentGroup;
    }

    /**
     * Fetch and return the descriptor for the specified object.
     */
    protected ClassDescriptor getDescriptorFor(Object object) {
        ClassDescriptor result = getSession().getDescriptor(object);
        if (result == null) {
            throw DescriptorException.missingDescriptor(object.getClass().getName());
        }
        return result;
    }

    public Object getResult() {
        return result;
    }

    public AbstractSession getSession() {
        return session;
    }

    /**
     * Return the second-to-last object visited.
     */
    public Object getVisitedGrandparent() {
        Object parent = getVisitedStack().pop();
        Object result = getVisitedStack().peek();
        getVisitedStack().push(parent);
        return result;
    }

    public Map getVisitedObjects() {
        return visitedObjects;
    }

    /**
     * Return the last object visited.
     */
    public Object getVisitedParent() {
        return getVisitedStack().peek();
    }

    public Stack getVisitedStack() {
        return visitedStack;
    }

    /**
     * Iterate an aggregate object
     * (i.e. an object that is the target of an AggregateMapping).
     * Override this method if appropriate.
     */
    protected void internalIterateAggregateObject(Object aggregateObject) {
        iterate(aggregateObject);
    }

    /**
     * Iterate an indirect container (IndirectList or IndirectMap).
     * Override this method if appropriate.
     */
    protected void internalIterateIndirectContainer(IndirectContainer container) {
        iterate(container);
    }

    /**
     * Iterate a primitive object (String, Date, Integer, etc.).
     * Override this method if appropriate.
     */
    protected void internalIteratePrimitive(Object primitiveValue) {
        iterate(primitiveValue);
    }

    /**
     * Iterate a (a non-Aggregate) reference object.
     * Override this method if appropriate.
     */
    protected void internalIterateReferenceObject(Object referenceObject) {
        iterate(referenceObject);
    }

    /**
     * Iterate a value holder.
     * Override this method if appropriate.
     */
    protected void internalIterateValueHolder(ValueHolderInterface valueHolder) {
        iterate(valueHolder);
    }

    /**
     * To define a new iterator create a subclass and define at least this method.
     * Given an object or set of the objects, this method will be called on those
     * objects and any object connected to them by using the descriptors to
     * traverse the object graph.
     * Override the assorted #internalIterate*() methods if appropriate.
     */
    protected abstract void iterate(Object object);

    /**
     * Iterate on the mapping's reference object and
     * recursively iterate on the reference object's
     * reference objects.
     * This is used for aggregate and aggregate collection mappings, which are not iterated on by default.
     */
    public void iterateForAggregateMapping(Object aggregateObject, DatabaseMapping mapping, ClassDescriptor descriptor) {
        if (aggregateObject == null) {
            return;
        }
        setCurrentMapping(mapping);
        // aggregate descriptors are passed in because they could be part of an inheritance tree
        setCurrentDescriptor(descriptor);
        
        AttributeGroup currentGroupOriginal = null;
        AttributeItem currentItemOriginal = null;
        if(this.usesGroup) {
            currentGroupOriginal = this.currentGroup;
            currentItemOriginal = this.currentItem;
            this.currentGroup = this.currentItem.getGroup();
        }

        if (shouldIterateOnAggregates()) {// false by default
            internalIterateAggregateObject(aggregateObject);
            if (shouldBreak()) {
                setShouldBreak(false);
                if(this.usesGroup) {
                    this.currentGroup = currentGroupOriginal;
                    this.currentItem = currentItemOriginal;
                }
                return;
            }
        }

        iterateReferenceObjects(aggregateObject);
        if(this.usesGroup) {
            this.currentGroup = currentGroupOriginal;
            this.currentItem = currentItemOriginal;
        }
    }

    /**
     * Iterate on the indirection object for its mapping.
     */
    public void iterateIndirectContainerForMapping(IndirectContainer container, DatabaseMapping mapping) {
        setCurrentMapping(mapping);
        setCurrentDescriptor(null);

        if (shouldIterateOnIndirectionObjects()) {// false by default
            internalIterateIndirectContainer(container);
        }

        if (shouldIterateOverUninstantiatedIndirectionObjects() || (shouldIterateOverIndirectionObjects() && container.isInstantiated())) {
            // force instantiation only if specified
            mapping.iterateOnRealAttributeValue(this, container);
        } else if (shouldIterateOverIndirectionObjects()) {
            // PERF: Allow the indirect container to iterate any cached elements.
            if (container instanceof IndirectCollection)  {
                mapping.iterateOnRealAttributeValue(this, ((IndirectCollection)container).getAddedElements());
            }
        }
    }

    /**
     * Iterate on the primitive value for its mapping.
     */
    public void iteratePrimitiveForMapping(Object primitiveValue, DatabaseMapping mapping) {
        if (primitiveValue == null) {
            return;
        }
        setCurrentMapping(mapping);
        setCurrentDescriptor(null);

        if (shouldIterateOnPrimitives()) {// false by default
            AttributeGroup currentGroupOriginal = null;
            AttributeItem currentItemOriginal = null;
            if(this.usesGroup) {
                currentGroupOriginal = this.currentGroup;
                currentItemOriginal = this.currentItem;
                this.currentGroup = this.currentItem.getGroup();
            }

            internalIteratePrimitive(primitiveValue);
            
            if(this.usesGroup) {
                this.currentGroup = currentGroupOriginal;
                this.currentItem = currentItemOriginal;
            }
        }
    }

    /**
     * Iterate on the mapping's reference object and
     * recursively iterate on the reference object's
     * reference objects.
     */
    public void iterateReferenceObjectForMapping(Object referenceObject, DatabaseMapping mapping) {
        if (this.cascadeCondition.shouldNotCascade(mapping)) {
            return;
        }

        // When using wrapper policy in EJB the iteration can stop in certain cases,
        // this is because EJB forces beans to be registered anyway and clone identity can be violated
        // and the violated clones references to session objects should not be traversed.
        ClassDescriptor rd = mapping.getReferenceDescriptor();
        if ((!shouldIterateOverWrappedObjects()) && (rd != null) && (rd.hasWrapperPolicy())) {
            return;
        }
        if (referenceObject == null) {
            return;
        }

        if(this.usesGroup && this.shouldTrackCurrentGroup) {
            Set visited = (Set)getVisitedObjects().get(referenceObject);
            if(visited == null) {
                visited = new HashSet(1);
                visited.add(this.currentItem.getGroup());
                getVisitedObjects().put(referenceObject, visited);
            } else {
                if(visited.contains(this.currentItem.getGroup())) {
                    // source object has been already visited with an equal group
                    return;
                } else {
                    visited.add(this.currentItem.getGroup());
                }
            }
        } else {
            // Check if already processed.
            if (getVisitedObjects().containsKey(referenceObject)) {
                return;
            }
    
            getVisitedObjects().put(referenceObject, referenceObject);
        }
        setCurrentMapping(mapping);
        setCurrentDescriptor(getDescriptorFor(referenceObject));

        AttributeGroup currentGroupOriginal = null;
        AttributeItem currentItemOriginal = null;
        if(this.usesGroup) {
            currentGroupOriginal = this.currentGroup;
            currentItemOriginal = this.currentItem;
            this.currentGroup = this.currentItem.getGroup();
        }

        internalIterateReferenceObject(referenceObject);
        if (shouldBreak()) {
            setShouldBreak(false);
            if(this.usesGroup) {
                this.currentGroup = currentGroupOriginal;
                this.currentItem = currentItemOriginal;
            }
            return;
        }

        iterateReferenceObjects(referenceObject);
        if(this.usesGroup) {
            this.currentGroup = currentGroupOriginal;
            this.currentItem = currentItemOriginal;
        }
    }

    /**
     * Iterate over the sourceObject's reference objects,
     * updating the visited stack appropriately.
     */
    protected void iterateReferenceObjects(Object sourceObject) {
        if(this.usesGroup) {
            // object is outside of the group - don't iterate over its references
            if(this.currentGroup == null || !this.currentGroup.hasItems()) {
                return;
            }
        }            
        
        getVisitedStack().push(sourceObject);
        internalIterateReferenceObjects(sourceObject);
        getVisitedStack().pop();
    }

    protected void internalIterateReferenceObjects(Object sourceObject) {
        List<DatabaseMapping> mappings;
        // Only iterate on relationships if required.
        if (shouldIterateOnPrimitives()) {
            mappings = getCurrentDescriptor().getObjectBuilder().getDescriptor().getMappings();
        } else {
            ObjectBuilder builder = getCurrentDescriptor().getObjectBuilder().getDescriptor().getObjectBuilder();
            // PERF: Only process relationships.
            if (builder.isSimple()) {
                return;
            }
            mappings = builder.getRelationshipMappings();
        }
        
        if (shouldIterateOnFetchGroupAttributesOnly()) {
            if(getCurrentDescriptor().hasFetchGroupManager()) {
                FetchGroup fetchGroup = getCurrentDescriptor().getFetchGroupManager().getObjectFetchGroup(sourceObject);
                if (fetchGroup != null) {
                    List<DatabaseMapping> fetchGroupMappings = new ArrayList();
                    for (DatabaseMapping mapping : mappings) {
                        if (fetchGroup.containsAttributeInternal(mapping.getAttributeName())) {
                            fetchGroupMappings.add(mapping);
                        }
                    }
                    mappings = fetchGroupMappings;
                }
            }
        }
        
        if (this.usesGroup) {
            AttributeGroup currentGroupOriginal = this.currentGroup;
            AttributeItem currentItemOriginal = this.currentItem;
            for (DatabaseMapping mapping : mappings) {
                this.currentItem = this.currentGroup.getAllItems().get(mapping.getAttributeName());
                // iterate only over the mappings found in the group
                if (currentItem != null) {
                    mapping.iterate(this);
                    this.currentGroup = currentGroupOriginal;
                }
            }
            this.currentItem = currentItemOriginal;
        } else {
            for (DatabaseMapping mapping : mappings) {
                mapping.iterate(this);
            }
        }
    }

    /**
     * Iterate on the value holder for its mapping.
     */
    public void iterateValueHolderForMapping(ValueHolderInterface valueHolder, DatabaseMapping mapping) {
        setCurrentMapping(mapping);
        setCurrentDescriptor(null);

        if (shouldIterateOnIndirectionObjects()) {// false by default
            internalIterateValueHolder(valueHolder);
        }

        if (shouldIterateOverUninstantiatedIndirectionObjects() || (shouldIterateOverIndirectionObjects() && valueHolder.isInstantiated())) {
            // force instantiation only if specified
            mapping.iterateOnRealAttributeValue(this, valueHolder.getValue());
        }
    }

    public void setCascadeDepth(int cascadeDepth) {
        this.cascadeDepth = cascadeDepth;
    }
    
    public void setCascadeCondition(CascadeCondition cascadeCondition){
        this.cascadeCondition = cascadeCondition;
    }

    public void setCurrentDescriptor(ClassDescriptor currentDescriptor) {
        this.currentDescriptor = currentDescriptor;
    }

    public void setCurrentMapping(DatabaseMapping currentMapping) {
        this.currentMapping = currentMapping;
    }

    public void setCurrentItem(AttributeItem item) {
        this.currentItem = item;
    }

    public void setCurrentGroup(AttributeGroup group) {
        this.currentGroup = group;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public void setSession(AbstractSession session) {
        this.session = session;
    }

    public void setShouldBreak(boolean shouldBreak) {
        this.shouldBreak = shouldBreak;
    }

    /**
     * Set whether the aggregate reference objects themselves
     * should be processed. (The objects referenced by the aggregate
     * objects will be processed either way.)
     */
    public void setShouldIterateOnAggregates(boolean shouldIterateOnAggregates) {
        this.shouldIterateOnAggregates = shouldIterateOnAggregates;
    }

    /**
     * Set whether the attributes outside fetch group should be processed. 
     */
    public void setShouldIterateOnFetchGroupAttributesOnly(boolean shouldIterateOnFetchGroupAttributesOnly) {
        this.shouldIterateOnFetchGroupAttributesOnly = shouldIterateOnFetchGroupAttributesOnly;
    }
    
    /**
     * Set whether the indirection objects themselves (e.g. the ValueHolders)
     * should be processed.
     */
    public void setShouldIterateOnIndirectionObjects(boolean shouldIterateOnIndirectionObjects) {
        this.shouldIterateOnIndirectionObjects = shouldIterateOnIndirectionObjects;
    }

    /**
     * Set whether to process primitive reference objects
     * (e.g. Strings, Dates, ints).
     */
    public void setShouldIterateOnPrimitives(boolean shouldIterateOnPrimitives) {
        this.shouldIterateOnPrimitives = shouldIterateOnPrimitives;
    }

    /**
     * Set whether to process the objects contained by indirection objects
     * (e.g. a ValueHolder's value) - but *without* instantiating them.
     * @see #setShouldIterateOverUninstantiatedIndirectionObjects()
     */
    public void setShouldIterateOverIndirectionObjects(boolean shouldIterateOverIndirectionObjects) {
        this.shouldIterateOverIndirectionObjects = shouldIterateOverIndirectionObjects;
    }

    /**
     * Set whether to *instantiate* and process the objects
     * contained by indirection objects (e.g. a ValueHolder's value).
     */
    public void setShouldIterateOverUninstantiatedIndirectionObjects(boolean shouldIterateOverUninstantiatedIndirectionObjects) {
        this.shouldIterateOverUninstantiatedIndirectionObjects = shouldIterateOverUninstantiatedIndirectionObjects;
    }

    public void setShouldIterateOverWrappedObjects(boolean shouldIterateOverWrappedObjects) {
        this.shouldIterateOverWrappedObjects = shouldIterateOverWrappedObjects;
    }

    public void setShouldTrackCurrentGroup(boolean shouldTrackCurrentGroup) {
        this.shouldTrackCurrentGroup = shouldTrackCurrentGroup;
    }

    public void setVisitedObjects(Map visitedObjects) {
        this.visitedObjects = visitedObjects;
    }

    protected void setVisitedStack(Stack visitedStack) {
        this.visitedStack = visitedStack;
    }

    public boolean shouldBreak() {
        return shouldBreak;
    }

    public boolean shouldCascadeAllParts() {
        return getCascadeDepth() == CascadeAllParts;
    }

    public boolean shouldCascadeNoParts() {
        return (getCascadeDepth() == NoCascading);
    }

    public boolean shouldCascadePrivateParts() {
        return (getCascadeDepth() == CascadeAllParts) || (getCascadeDepth() == CascadePrivateParts);
    }

    /**
     * Return whether the aggregate reference objects themselves
     * should be processed. (The objects referenced by the aggregate
     * objects will be processed either way.)
     */
    public boolean shouldIterateOnAggregates() {
        return shouldIterateOnAggregates;
    }

    /**
     * If true then if object has a FetchGroup then iterations
     * not performed on mappings that are outside of the FetchGroup.
     */
    public boolean shouldIterateOnFetchGroupAttributesOnly() {
        return this.shouldIterateOnFetchGroupAttributesOnly;
    }
    
    /**
     * Return whether the indirection objects themselves (e.g. the ValueHolders)
     * should be processed.
     */
    public boolean shouldIterateOnIndirectionObjects() {
        return shouldIterateOnIndirectionObjects;
    }

    /**
     * Return whether to process primitive reference objects
     * (e.g. Strings, Dates, ints).
     */
    public boolean shouldIterateOnPrimitives() {
        return shouldIterateOnPrimitives;
    }

    /**
     * Return whether to process the objects contained by indirection objects
     * (e.g. a ValueHolder's value) - but *without* instantiating them.
     * @see #shouldIterateOverUninstantiatedIndirectionObjects()
     */
    public boolean shouldIterateOverIndirectionObjects() {
        return shouldIterateOverIndirectionObjects;
    }

    /**
     * Return whether to *instantiate* and process the objects
     * contained by indirection objects (e.g. a ValueHolder's value).
     */
    public boolean shouldIterateOverUninstantiatedIndirectionObjects() {
        return shouldIterateOverUninstantiatedIndirectionObjects;
    }

    public boolean shouldIterateOverWrappedObjects() {
        return shouldIterateOverWrappedObjects;
    }

    public boolean shouldTrackCurrentGroup() {
        return this.shouldTrackCurrentGroup;
    }

    public boolean usesGroup() {
        return this.usesGroup;
    }
    
    /**
     * This is the root method called to start the iteration.
     */
    public void startIterationOn(Object sourceObject) {
        startIterationOn(sourceObject, null);
    }
    
    public void startIterationOn(Object sourceObject, AttributeGroup group) {
        this.usesGroup = group != null;
        if(this.usesGroup && this.shouldTrackCurrentGroup) {
            Set visited = (Set)getVisitedObjects().get(sourceObject);
            if(visited == null) {
                visited = new HashSet(1);
                visited.add(group);
                getVisitedObjects().put(sourceObject, visited);
            } else {
                if(visited.contains(group)) {
                    // source object has been already visited with an equal group
                    return;
                } else {
                    visited.add(group);
                }
            }
        } else {
            if (getVisitedObjects().containsKey(sourceObject)) {
                return;
            }
            getVisitedObjects().put(sourceObject, sourceObject);
        }
        setCurrentMapping(null);
        setCurrentDescriptor(getSession().getDescriptor(sourceObject));
        setCurrentItem(null);
        setCurrentGroup(group);

        iterate(sourceObject);

        // start the recursion
        if ((getCurrentDescriptor() != null) && (!shouldCascadeNoParts())  && !this.shouldBreak()) {
            iterateReferenceObjects(sourceObject);
        }
    }
    
    public class CascadeCondition{
        public boolean shouldNotCascade(DatabaseMapping mapping){
            return !(shouldCascadeAllParts() || (shouldCascadePrivateParts() && mapping.isPrivateOwned()));
        }
    }
}
