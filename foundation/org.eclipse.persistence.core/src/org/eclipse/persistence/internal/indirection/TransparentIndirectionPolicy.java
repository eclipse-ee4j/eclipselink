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
package org.eclipse.persistence.internal.indirection;

import java.util.*;
import org.eclipse.persistence.descriptors.changetracking.*;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.indirection.*;
import org.eclipse.persistence.sessions.remote.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.descriptors.*;
import org.eclipse.persistence.internal.queries.*;
import org.eclipse.persistence.internal.sessions.remote.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.MergeManager;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;

/**
 * <h2>Purpose</h2>:
 * TransparentIndirectionPolicy implements the behavior necessary for a
 * a CollectionMapping to use
 * IndirectContainers to delay the reading of objects from the database
 * until they are actually needed.
 *
 * @see CollectionMapping
 * @see IndirectContainer
 * @author Big Country
 * @since TOPLink/Java 2.5
 */
public class TransparentIndirectionPolicy extends IndirectionPolicy {
    //3732
    protected static Integer defaultContainerSize;

    /** PERF: Cache the mappings container policy. */
    protected ContainerPolicy containerPolicy;
    
    /**
     * IndirectList and IndirectSet can be configured not to instantiate the list from the
     * database when you add and remove from them.  IndirectList defaults to this behavior. When
     * Set to true, the collection associated with this TransparentIndirection will be setup so as
     * not to instantiate for adds and removes.  The weakness of this setting for an IndirectSet is
     * that when the set is not instantiated, if a duplicate element is added, it will not be
     * detected until commit time.
     */
    protected Boolean useLazyInstantiation;

    /**
     * INTERNAL:
     * Construct a new indirection policy.
     */
    public TransparentIndirectionPolicy() {
        super();
    }

    /**
     * INTERNAL:
     *    Return a backup clone of the attribute.
     */
    public Object backupCloneAttribute(Object attributeValue, Object clone, Object backup, UnitOfWorkImpl unitOfWork) {
        // delay instantiation until absolutely necessary
        if ((!(attributeValue instanceof IndirectContainer)) || objectIsInstantiated(attributeValue)) {
            return super.backupCloneAttribute(attributeValue, clone, backup, unitOfWork);
        } else {
            return buildBackupClone((IndirectContainer)attributeValue);
        }
    }

    /**
     * INTERNAL:
     * Return a backup clone of a clone container that has not been
     * read from the database yet.
     * This is used by the indirection policy to hook together a UOW
     * clone with its backup clone - only when the clone (the working
     * copy returned to the user) instantiates its contents from the
     * database will these contents be copied to the backup clone.
     */
    protected Object buildBackupClone(IndirectContainer container) {
        UnitOfWorkValueHolder containerValueHolder = (UnitOfWorkValueHolder)container.getValueHolder();
        // CR#2852176 Use a BackupValueHolder to handle replacing of the original.
        BackupValueHolder backupValueHolder = new BackupValueHolder(containerValueHolder);
        containerValueHolder.setBackupValueHolder(backupValueHolder);
        return this.buildIndirectContainer(backupValueHolder);
    }

    /**
     * Construct and return an instance of the specified
     * indirect container class.
     */
    protected IndirectContainer buildIndirectContainer() {
        IndirectContainer container = null;
        //3732
        if (defaultContainerSize != null) {
            container = (IndirectContainer)getContainerPolicy().containerInstance(getDefaultContainerSize());
        } else {
            container = (IndirectContainer)getContainerPolicy().containerInstance();
        }
        if (container instanceof IndirectCollection){
            if (this.useLazyInstantiation != null){
                ((IndirectCollection)container).setUseLazyInstantiation(this.useLazyInstantiation.booleanValue());
            }
        }
        return container;
    }

    /**
     * Return a new IndirectContainer with the specified value holder.
     */
    protected Object buildIndirectContainer(ValueHolderInterface valueHolder) {
        IndirectContainer result = buildIndirectContainer();
        result.setValueHolder(valueHolder);
        return result;
    }

    /**
     * INTERNAL: This method can be used when an Indirection Object is required
     * to be built from a provided ValueHolderInterface object. This may be used
     * for custom value holder types. Certain policies like the
     * TransparentIndirectionPolicy may wrap the valueholder in another object.
     */
    
    public Object buildIndirectObject(ValueHolderInterface valueHolder){
        return buildIndirectContainer(valueHolder);
    }
    /**
     * Return a clone of the attribute.
     * @param buildDirectlyFromRow indicates that we are building the clone directly
     * from a row as opposed to building the original from the row, putting it in
     * the shared cache, and then cloning the original.
     */
    public Object cloneAttribute(Object attributeValue, Object original, CacheKey cacheKey, Object clone, Integer refreshCascade, AbstractSession cloningSession, boolean buildDirectlyFromRow) {
        ValueHolderInterface valueHolder = null;
        Object container = null;
        IndirectList indirectList = null;
        IndirectContainer indirectContainer = null;
        if (attributeValue instanceof IndirectContainer) {
            indirectContainer = (IndirectContainer)attributeValue;
            valueHolder = indirectContainer.getValueHolder();
            if (indirectContainer instanceof IndirectList) {
                indirectList = (IndirectList)indirectContainer;
            }
        }
        if (!buildDirectlyFromRow && cloningSession.isUnitOfWork() && ((UnitOfWorkImpl)cloningSession).isOriginalNewObject(original)) {
            // CR#3156435 Throw a meaningful exception if a serialized/dead value holder is detected.
            // This can occur if an existing serialized object is attempt to be registered as new.
            if ((valueHolder instanceof DatabaseValueHolder)
                    && (! ((DatabaseValueHolder) valueHolder).isInstantiated())
                    && (((DatabaseValueHolder) valueHolder).getSession() == null)
                    && (! ((DatabaseValueHolder) valueHolder).isSerializedRemoteUnitOfWorkValueHolder())) {
                throw DescriptorException.attemptToRegisterDeadIndirection(original, this.mapping);
            }
            if (this.mapping.getRelationshipPartner() == null) {
                container = this.mapping.buildCloneForPartObject(attributeValue, original, cacheKey, clone, cloningSession, refreshCascade, false, false);
            } else {
                if (indirectContainer == null) {
                    valueHolder = new ValueHolder(attributeValue);
                }
                AbstractRecord row = null;
                if (valueHolder instanceof DatabaseValueHolder) {
                    row = ((DatabaseValueHolder)valueHolder).getRow();
                }

                //If a new object is being cloned then we must build a new UOWValueHolder
                //  this is so that new clones can also have their relationships managed
                // here the code instantiates the valueholder in a privledged manner because a
                // UOWValueHolder will assume the objects in the collection are existing if the valueholder
                //  Goes through it's own instantiation process.
                DatabaseValueHolder newValueHolder = this.mapping.createCloneValueHolder(valueHolder, original, clone, row, cloningSession, buildDirectlyFromRow);
                container = buildIndirectContainer(newValueHolder);
                Object cloneCollection = this.mapping.buildCloneForPartObject(attributeValue, original, cacheKey, clone, cloningSession, refreshCascade, false, false);
                newValueHolder.privilegedSetValue(cloneCollection);
                newValueHolder.setInstantiated();
            }
        } else {
            if (indirectContainer == null) {
                valueHolder = new ValueHolder(attributeValue);
            }
            AbstractRecord row = null;
            if (valueHolder instanceof DatabaseValueHolder) {
                row = ((DatabaseValueHolder)valueHolder).getRow();
            }
            DatabaseValueHolder uowValueHolder = this.mapping.createCloneValueHolder(valueHolder, original, clone, row, cloningSession, buildDirectlyFromRow);
            if ((indirectContainer == null) || !buildDirectlyFromRow) {
                container = buildIndirectContainer(uowValueHolder);
            } else {
                // PERF: If building from rows inside uow, there is no original,
                // so just use the already built indirect collection.
                indirectContainer.setValueHolder(uowValueHolder);
                container = indirectContainer;              
            }
        }
        if (cloningSession.isUnitOfWork()){
            // Set the change listener.
            if ((this.mapping.getDescriptor().getObjectChangePolicy().isObjectChangeTrackingPolicy())
                    && (((ChangeTracker)clone)._persistence_getPropertyChangeListener() != null)
                    && (container instanceof CollectionChangeTracker) ) {
                ((CollectionChangeTracker)container).setTrackedAttributeName(this.mapping.getAttributeName());
                ((CollectionChangeTracker)container)._persistence_setPropertyChangeListener(((ChangeTracker)clone)._persistence_getPropertyChangeListener());
            }
            if (indirectList != null) {
                ((IndirectList)container).setIsListOrderBrokenInDb(indirectList.isListOrderBrokenInDb());
            }
        }
        return container;
    }

    /**
     * INTERNAL:
     *    Return whether the container policy is valid for the indirection policy.
     * In this case, the container policy MUST be configured
     * for an IndirectContainer.
     */
    protected boolean containerPolicyIsValid() {
        if (Helper.classImplementsInterface(this.getContainerClass(), ClassConstants.IndirectContainer_Class)) {
            return true;
        }
        return false;
    }

    /**
     * INTERNAL:
     *    Return the primary key for the reference object (i.e. the object
     * object referenced by domainObject and specified by mapping).
     * This key will be used by a RemoteValueHolder.
     * OneToOneMappings should not be using transparent direction.
     */
    @Override
    public Object extractPrimaryKeyForReferenceObject(Object referenceObject, AbstractSession session) {
        throw DescriptorException.invalidUseOfTransparentIndirection(this.getMapping());
    }

    /**
     * INTERNAL:
     *    Return the reference row for the reference object.
     * This allows the new row to be built without instantiating
     * the reference object.
     * Return null if the object has already been instantiated.
     */
    public AbstractRecord extractReferenceRow(Object referenceObject) {
        if (this.objectIsInstantiated(referenceObject)) {
            return null;
        } else {
            return ((DatabaseValueHolder)((IndirectContainer)referenceObject).getValueHolder()).getRow();
        }
    }

    /**
     * INTERNAL:
     * An object has been serialized from the server to the client.
     * Replace the transient attributes of the remote value holders
     * with client-side objects.
     */
    public void fixObjectReferences(Object object, Map objectDescriptors, Map processedObjects, ObjectLevelReadQuery query, DistributedSession session) {
        Object container = getMapping().getAttributeValueFromObject(object);
        if (container instanceof IndirectContainer && ((((IndirectContainer) container).getValueHolder() instanceof RemoteValueHolder)) ) {
            RemoteValueHolder valueHolder = (RemoteValueHolder)((IndirectContainer)container).getValueHolder();
            valueHolder.setSession(session);
            valueHolder.setMapping(getMapping());
            if ((!query.shouldMaintainCache()) && ((!query.shouldCascadeParts()) || (query.shouldCascadePrivateParts() && (!this.mapping.isPrivateOwned())))) {
                valueHolder.setQuery(null);
            } else {
                valueHolder.setQuery(query);
            }

            // set to uninstantiated since no objects are serialized past remote value holders
            valueHolder.setUninstantiated();
        } else {
            this.mapping.fixRealObjectReferences(object, objectDescriptors, processedObjects, query, session);
        }
    }

    /**
     * INTERNAL:
     *    Return the container class for the mapping.
     */
    protected Class getContainerClass() {
        return this.getContainerPolicy().getContainerClass();
    }

    /**
     * INTERNAL:
     *    Return the container policy for the mapping.
     */
    protected ContainerPolicy getContainerPolicy() {
        if (this.containerPolicy == null) {
            this.containerPolicy = getCollectionMapping().getContainerPolicy();
        }
        return this.containerPolicy;
    }

    /**
     * INTERNAL:
     * Return the the size to of container to create.  Default to using default constructor.
     */
    protected static int getDefaultContainerSize() {
        //3732
        return defaultContainerSize.intValue();
    }

    /**
     * INTERNAL:
     *    Return the original indirection object for a unit of work indirection object.
     */
    @Override
    public Object getOriginalIndirectionObject(Object unitOfWorkIndirectionObject, AbstractSession session) {
        IndirectContainer container = (IndirectContainer)unitOfWorkIndirectionObject;
        if (container.getValueHolder() instanceof UnitOfWorkValueHolder) {
            return buildIndirectContainer((ValueHolderInterface) getOriginalValueHolder(unitOfWorkIndirectionObject, session));
        } else {
            return container;
        }
    }

    /**
     * INTERNAL:
     *    Return the original indirection object for a unit of work indirection object.
     */
    @Override
    public Object getOriginalIndirectionObjectForMerge(Object unitOfWorkIndirectionObject, AbstractSession session) {
        IndirectContainer container = (IndirectContainer) getOriginalIndirectionObject(unitOfWorkIndirectionObject, session);
        DatabaseValueHolder holder = (DatabaseValueHolder)container.getValueHolder();
        if (holder != null && holder.getSession()!= null){
            holder.setSession(session);
        }
        return container;
    }

    /**
     * INTERNAL: Return the original valueHolder object. Access to the
     * underlying valueholder may be required when serializing the valueholder
     * or converting the valueHolder to another type.
     */
    @Override
    public Object getOriginalValueHolder(Object unitOfWorkIndirectionObject, AbstractSession session) {
        if (! (unitOfWorkIndirectionObject instanceof IndirectContainer)){
            return new ValueHolder();
        }
        IndirectContainer container = (IndirectContainer)unitOfWorkIndirectionObject;
        if (container.getValueHolder() instanceof WrappingValueHolder) {
            ValueHolderInterface valueHolder = ((WrappingValueHolder)container.getValueHolder()).getWrappedValueHolder();
            if ((valueHolder == null) && session.isRemoteUnitOfWork()) {
                RemoteSessionController controller = ((RemoteUnitOfWork)session).getParentSessionController();
                valueHolder = controller.getRemoteValueHolders().get(((UnitOfWorkValueHolder)container.getValueHolder()).getWrappedValueHolderRemoteID());
            }
            if (!session.isProtectedSession()){
                while (valueHolder instanceof WrappingValueHolder && ((WrappingValueHolder)valueHolder).getWrappedValueHolder() != null){
                    valueHolder = ((WrappingValueHolder)valueHolder).getWrappedValueHolder();
                }
            }
            return valueHolder;
        } else {
            return container.getValueHolder();
        }
    }

    /**
     * INTERNAL:
     * Return the "real" attribute value, as opposed to any wrapper.
     * This will trigger the wrapper to instantiate the value.
     */
    public Object getRealAttributeValueFromObject(Object object, Object attribute) {
        // PERF: do not instantiate - this.getContainerPolicy().sizeFor(object);// forgive me for this hack: but we have to do something to trigger the database read
        return attribute;
    }
    
    /**
     * INTERNAL:
     * Trigger the instantiation of the value.
     */
    public void instantiateObject(Object object, Object attribute) {
        getContainerPolicy().sizeFor(attribute);
    }

    /**
     * INTERNAL:
     * The method validateAttributeOfInstantiatedObject(Object attributeValue) fixes the value of the attributeValue 
     * in cases where it is null and indirection requires that it contain some specific data structure.  Return whether this will happen.
     * This method is used to help determine if indirection has been triggered
     * @param attributeValue
     * @return
     * @see validateAttributeOfInstantiatedObject(Object attributeValue)
     */
    @Override
    public boolean isAttributeValueFullyBuilt(Object attributeValue){
        return attributeValue != null;
    }

    public Boolean getUseLazyInstantiation() {
        return useLazyInstantiation;
    }
    
    /**
     * INTERNAL:
     * Extract and return the appropriate value from the
     * specified remote value holder.
     */
    public Object getValueFromRemoteValueHolder(RemoteValueHolder remoteValueHolder) {
        Object result = remoteValueHolder.getServerIndirectionObject();
        this.getContainerPolicy().sizeFor(result);// forgive me for this hack: but we have to do something to trigger the database read
        return result;
    }

    /**
     * INTERNAL:
     *    Set the value of the appropriate attribute of target to attributeValue.
     * If the Target has yet to be instantiated then we need to instantiate the target to ensure that
     * the backup clone is instantiated for comparison.
     */
    public void setRealAttributeValueInObject(Object target, Object attributeValue) {

        /*
           Bug 3573808 - do NOT trigger the valueholder; SPECj benchmark
           deadlocks in this method.
           Re-ran the original testcase IndirectContainerTestDatabase
            testMergeCloneWithSerializedTransparentIndirection
           and it passes without triggering the valueholder. MWN

        //cr 3788
        // Trigger the valueholder when setting the value in an object
        Object object = this.getMapping().getAttributeValueFromObject(target);
        if (object instanceof IndirectContainer){
            ((IndirectContainer)object).getValueHolder().getValue();
        }
        */
        super.setRealAttributeValueInObject(target, attributeValue);
    }
    
    /**
     * INTERNAL:
     * set the source object into QueryBasedValueHolder.
     */
    public void setSourceObject(Object sourceObject, Object attributeValue) {        
        if( attributeValue instanceof IndirectContainer) {
            ValueHolderInterface valueHolder = ((IndirectContainer)attributeValue).getValueHolder();
            if (valueHolder instanceof QueryBasedValueHolder) {
                ((QueryBasedValueHolder)valueHolder).setSourceObject(sourceObject);
            }
        }
    }
    
    /**
     * ADVANCED:
     * IndirectList and IndirectSet can be configured not to instantiate the list from the
     * database when you add and remove from them.  IndirectList defaults to this behavior. When
     * Set to true, the collection associated with this TransparentIndirection will be setup so as
     * not to instantiate for adds and removes.  The weakness of this setting for an IndirectSet is
     * that when the set is not instantiated, if a duplicate element is added, it will not be
     * detected until commit time.
     */
    @Override
    public void setUseLazyInstantiation(Boolean useLazyInstantiation) {
        this.useLazyInstantiation = useLazyInstantiation;
    }
    
    /**
     * ADVANCED:
     * IndirectList and IndirectSet can be configured not to instantiate the list from the
     * database when you add and remove from them.  IndirectList defaults to this behavior. When
     * Set to true, the collection associated with this TransparentIndirection will be setup so as
     * not to instantiate for adds and removes.  The weakness of this setting for an IndirectSet is
     * that when the set is not instantiated, if a duplicate element is added, it will not be
     * detected until commit time.
     */
    @Override
    public Boolean shouldUseLazyInstantiation() {
        return useLazyInstantiation;
    }
    
    /**
     * INTERNAL:
     * Iterate over the specified attribute value.
     */
    public void iterateOnAttributeValue(DescriptorIterator iterator, Object attributeValue) {
        if (attributeValue instanceof IndirectContainer) {
            iterator.iterateIndirectContainerForMapping((IndirectContainer)attributeValue, this.getMapping());
        } else {// it must be a "real" collection
            super.iterateOnAttributeValue(iterator, attributeValue);
        }
    }

    /**
     * INTERNAL
     * Replace the client value holder with the server value holder,
     * after copying some of the settings from the client value holder.
     */
    public void mergeRemoteValueHolder(Object clientSideDomainObject, Object serverSideDomainObject, MergeManager mergeManager) {
        // This will always be a transparent with a remote.
        IndirectContainer serverContainer = (IndirectContainer)getMapping().getAttributeValueFromObject(serverSideDomainObject);
        RemoteValueHolder serverValueHolder = (RemoteValueHolder)serverContainer.getValueHolder();
        mergeClientIntoServerValueHolder(serverValueHolder, mergeManager);

        getMapping().setAttributeValueInObject(clientSideDomainObject, serverContainer);
    }

    /**
     * INTERNAL:
     *    Return the null value of the appropriate attribute. That is, the
     * field from the database is NULL, return what should be
     * placed in the object's attribute as a result.
     * OneToOneMappings should not be using transparent direction.
     */
    public Object nullValueFromRow() {
        throw DescriptorException.invalidUseOfTransparentIndirection(this.getMapping());
    }

    /**
     * INTERNAL:
     * Return whether the specified object is instantiated.
     */
    public boolean objectIsInstantiated(Object object) {
        if (object instanceof IndirectContainer) {
            return ((IndirectContainer)object).isInstantiated();
        } else {
            return true;// it must be a "real" collection
        }
    }
    
    /**
     * INTERNAL:
     * Return whether the specified object can be instantiated without database access.
     */
    public boolean objectIsEasilyInstantiated(Object object) {
        if (object instanceof IndirectContainer) {
            ValueHolderInterface valueHolder = ((IndirectContainer)object).getValueHolder();
            if (valueHolder instanceof DatabaseValueHolder) {
                return ((DatabaseValueHolder)valueHolder).isEasilyInstantiated();
            }
        }
        return true;
    }
        
    /**
     * INTERNAL:
     * Return whether the specified object is instantiated, or if it has changes.
     */
    public boolean objectIsInstantiatedOrChanged(Object object) {
        return objectIsInstantiated(object) || ((object instanceof IndirectCollection) && ((IndirectCollection)object).hasDeferredChanges());
    }

    /**
     * ADVANCED:
     * Set the size to of container to create.  Default to using default constructor.
     */
    public static void setDefaultContainerSize(int defaultSize) {
        //3732
        defaultContainerSize = Integer.valueOf(defaultSize);
    }

    /**
     * INTERNAL:
     * Return whether the type is appropriate for the indirection policy.
     * In this case, the attribute type MUST be
     * compatible with the one specified by the ContainerPolicy
     * (i.e. either the container policy class is a subclass of the
     * declared type [jdk1.1] or the container policy class implements
     * the declared interface [jdk1.2]).
     */
    protected boolean typeIsValid(Class declaredType) {
        if (Helper.classIsSubclass(this.getContainerClass(), declaredType)) {
            return true;
        }
        if (Helper.classImplementsInterface(this.getContainerClass(), declaredType)) {
            return true;
        }
        return false;
    }

    /**
     * INTERNAL:
     * Return whether the indirection policy uses transparent indirection.
     */
    public boolean usesTransparentIndirection(){
        return true;
    }

    /**
     * INTERNAL:
     * Verify that the value of the attribute within an instantiated object
     * is of the appropriate type for the indirection policy.
     * In this case, the attribute must be non-null and it must be at least a
     * subclass or implementor of the container type.
     * If the value is null return a new indirection object to be used for the attribute.
     */
    public Object validateAttributeOfInstantiatedObject(Object attributeValue) {
        // PERF: If the value is null, create a new value holder instance for the attribute value,
        // this allows for indirection attributes to not be instantiated in the constructor as they
        // are typically replaced when reading or cloning so is very inefficient to initialize.
        if (attributeValue == null) {
            return buildIndirectContainer();
        }
        return attributeValue;
    }

    /**
     * INTERNAL:
     * Verify that the container policy is compatible with the
     * indirection policy. If it is incorrect, add an exception to the
     * integrity checker.
     */
    public void validateContainerPolicy(IntegrityChecker checker) throws DescriptorException {
        super.validateContainerPolicy(checker);
        if (!this.containerPolicyIsValid()) {
            checker.handleError(DescriptorException.invalidContainerPolicyWithTransparentIndirection(this.getMapping(), this.getContainerPolicy()));
        }

        // Bug 2618982
        if (getContainerPolicy().isMapPolicy() && ((((ForeignReferenceMapping)getMapping()).getRelationshipPartnerAttributeName() != null) || (getMapping().getRelationshipPartner() != null))) {
            checker.handleError(DescriptorException.unsupportedTypeForBidirectionalRelationshipMaintenance(this.getMapping(), this.getContainerPolicy()));
        }
    }

    /**
     * INTERNAL:
     * Verify that attributeType is correct for the
     * indirection policy. If it is incorrect, add an exception to the
     * integrity checker.
     * In this case, the attribute type MUST be
     * compatible with the one specified by the ContainerPolicy.
     */
    public void validateDeclaredAttributeType(Class attributeType, IntegrityChecker checker) throws DescriptorException {
        super.validateDeclaredAttributeType(attributeType, checker);
        if (!this.typeIsValid(attributeType)) {
            checker.handleError(DescriptorException.attributeAndMappingWithTransparentIndirectionMismatch(this.getMapping(), attributeType, this.validTypeName()));
        }
    }

    /**
     * INTERNAL:
     * Verify that getter returnType is correct for the
     * indirection policy. If it is incorrect, add an exception
     * to the integrity checker.
     * In this case, the attribute type MUST be
     * compatible with the one specified by the ContainerPolicy.
     */
    public void validateGetMethodReturnType(Class returnType, IntegrityChecker checker) throws DescriptorException {
        super.validateGetMethodReturnType(returnType, checker);
        if (!this.typeIsValid(returnType)) {
            checker.handleError(DescriptorException.returnAndMappingWithTransparentIndirectionMismatch(this.getMapping(), returnType, this.validTypeName()));
        }
    }

    /**
     * INTERNAL:
     * Verify that setter parameterType is correct for the
     * indirection policy. If it is incorrect, add an exception
     * to the integrity checker.
     * In this case, the attribute type MUST be
     * compatible with the one specified by the ContainerPolicy.
     */
    public void validateSetMethodParameterType(Class parameterType, IntegrityChecker checker) throws DescriptorException {
        super.validateSetMethodParameterType(parameterType, checker);
        if (!this.typeIsValid(parameterType)) {
            checker.handleError(DescriptorException.parameterAndMappingWithTransparentIndirectionMismatch(this.getMapping(), parameterType, this.validTypeName()));
        }
    }

    /**
     * INTERNAL:
     *    Return the type that is appropriate for the indirection policy.
     */
    protected String validTypeName() {
        return Helper.getShortClassName(this.getContainerClass());
    }

    /**
     * INTERNAL:
     * Return the value to be stored in the object's attribute.
     *    This value is determined by the batchQuery.
     * In this case, wrap the query in an IndirectContainer for later invocation.
     */
    public Object valueFromBatchQuery(ReadQuery batchQuery, AbstractRecord row, ObjectLevelReadQuery originalQuery, CacheKey parentCacheKey) {
        return this.buildIndirectContainer(new BatchValueHolder(batchQuery, row, getForeignReferenceMapping(), originalQuery, parentCacheKey));
    }

    /**
     * INTERNAL:
     * Return the value to be stored in the object's attribute.
     * This value is determined by invoking the appropriate
     * method on the object and passing it the row and session.
     * TransformationMappings should not be using transparent direction.
     */
    public Object valueFromMethod(Object object, AbstractRecord row, AbstractSession session) {
        throw DescriptorException.invalidUseOfTransparentIndirection(this.getMapping());
    }

    /**
     * INTERNAL:
     * Return the value to be stored in the object's attribute.
     * This value is determined by the query.
     * In this case, wrap the query in an IndirectContainer for later invocation.
     */
    public Object valueFromQuery(ReadQuery query, AbstractRecord row, AbstractSession session) {
        return this.buildIndirectContainer(new QueryBasedValueHolder(query, row, session));
    }
    
    /**
     * INTERNAL:
     * A combination of valueFromQuery and valueFromRow(object).
     * Sometimes the attribute is known (joining) but we still need to hang on
     * to the query (pessimistic locking).
     */
    public Object valueFromQuery(ReadQuery query, AbstractRecord row, Object object, AbstractSession session) {
        return this.buildIndirectContainer(new QueryBasedValueHolder(query, object, row, session));
    }

    /**
     * INTERNAL:
     * Return the value to be stored in the object's attribute.
     * This allows wrapping of the real value, none is required for transparent.
     */
    public Object valueFromRow(Object object) {
        return object;
    }
}
