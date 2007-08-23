/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
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
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.MergeManager;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.internal.sessions.AbstractSession;

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
        //3732
        if (defaultContainerSize != null) {
            return (IndirectContainer)getContainerPolicy().containerInstance(getDefaultContainerSize());
        } else {
            return (IndirectContainer)getContainerPolicy().containerInstance();
        }
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
     * Return a clone of the attribute.
     * @param buildDirectlyFromRow indicates that we are building the clone directly
     * from a row as opposed to building the original from the row, putting it in
     * the shared cache, and then cloning the original.
     */
    public Object cloneAttribute(Object attributeValue, Object original, Object clone, UnitOfWorkImpl unitOfWork, boolean buildDirectlyFromRow) {
        ValueHolderInterface valueHolder = null;
        Object container = null;
        if (attributeValue instanceof IndirectContainer) {
            valueHolder = ((IndirectContainer)attributeValue).getValueHolder();
        }
        if (!buildDirectlyFromRow && unitOfWork.isOriginalNewObject(original)) {
            // CR#3156435 Throw a meaningful exception if a serialized/dead value holder is detected.
            // This can occur if an existing serialized object is attempt to be registered as new.
            if ((valueHolder instanceof DatabaseValueHolder)
                    && (! ((DatabaseValueHolder) valueHolder).isInstantiated())
                    && (((DatabaseValueHolder) valueHolder).getSession() == null)
                    && (! ((DatabaseValueHolder) valueHolder).isSerializedRemoteUnitOfWorkValueHolder())) {
                throw DescriptorException.attemptToRegisterDeadIndirection(original, getMapping());
            }
            if (getMapping().getRelationshipPartner() == null) {
                container = getMapping().buildCloneForPartObject(attributeValue, original, clone, unitOfWork, false);
            } else {
                if (!(attributeValue instanceof IndirectContainer)) {
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
                UnitOfWorkValueHolder newValueHolder = this.getMapping().createUnitOfWorkValueHolder(valueHolder, original, clone, row, unitOfWork, buildDirectlyFromRow);
                container = buildIndirectContainer(newValueHolder);
                Object cloneCollection = getMapping().buildCloneForPartObject(attributeValue, original, clone, unitOfWork, false);
                newValueHolder.privilegedSetValue(cloneCollection);
                newValueHolder.setInstantiated();
            }
        } else {
            if (!(attributeValue instanceof IndirectContainer)) {
                valueHolder = new ValueHolder(attributeValue);
            }
            AbstractRecord row = null;
            if (valueHolder instanceof DatabaseValueHolder) {
                row = ((DatabaseValueHolder)valueHolder).getRow();
            }
            container = buildIndirectContainer(getMapping().createUnitOfWorkValueHolder(valueHolder, original, clone, row, unitOfWork, buildDirectlyFromRow));
        }
        if ( (getMapping().getDescriptor().getObjectChangePolicy().isObjectChangeTrackingPolicy())
          && (((ChangeTracker)clone)._persistence_getPropertyChangeListener() != null)
          && (container instanceof CollectionChangeTracker) ) {
            ((CollectionChangeTracker)container).setTopLinkAttributeName(getMapping().getAttributeName());
            ((CollectionChangeTracker)container)._persistence_setPropertyChangeListener(((ChangeTracker)clone)._persistence_getPropertyChangeListener());
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
    public Vector extractPrimaryKeyForReferenceObject(Object referenceObject, AbstractSession session) {
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
    public void fixObjectReferences(Object object, IdentityHashtable objectDescriptors, IdentityHashtable processedObjects, ObjectLevelReadQuery query, RemoteSession session) {
        Object container = getMapping().getAttributeValueFromObject(object);
        //There may have been a concurrent modifucation of the collection on the server side
        // and the valueholders would not have been updated so treat it like no indirection
        if (container instanceof IndirectContainer || (!(((IndirectContainer) container).getValueHolder() instanceof RemoteValueHolder)) ) {
            RemoteValueHolder valueHolder = (RemoteValueHolder)((IndirectContainer)container).getValueHolder();
            valueHolder.setSession(session);
            valueHolder.setMapping(getMapping());
            if ((!query.shouldMaintainCache()) && ((!query.shouldCascadeParts()) || (query.shouldCascadePrivateParts() && (!getMapping().isPrivateOwned())))) {
                valueHolder.setQuery(null);
            } else {
                valueHolder.setQuery(query);
            }

            // set to uninstantiated since no objects are serialized past remote value holders
            valueHolder.setUninstantiated();
        } else {
            getMapping().fixRealObjectReferences(object, objectDescriptors, processedObjects, query, session);
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
    public Object getOriginalIndirectionObject(Object unitOfWorkIndirectionObject, AbstractSession session) {
        IndirectContainer container = (IndirectContainer)unitOfWorkIndirectionObject;
        if (container.getValueHolder() instanceof UnitOfWorkValueHolder) {
            ValueHolderInterface valueHolder = ((UnitOfWorkValueHolder)container.getValueHolder()).getWrappedValueHolder();
            if ((valueHolder == null) && session.isRemoteUnitOfWork()) {
                RemoteSessionController controller = ((RemoteUnitOfWork)session).getParentSessionController();
                valueHolder = (ValueHolderInterface)controller.getRemoteValueHolders().get(((UnitOfWorkValueHolder)container.getValueHolder()).getWrappedValueHolderRemoteID());
            }
            return buildIndirectContainer(valueHolder);
        } else {
            return container;
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
        defaultContainerSize = new Integer(defaultSize);
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
        // are typically replaced when reading or cloning so is very inefficent to initialize.
        if (attributeValue == null) {
            return buildIndirectContainer();
        }
        if (!(this.getContainerPolicy().isValidContainer(attributeValue))) {
            throw DescriptorException.indirectContainerInstantiationMismatch(attributeValue, this.getMapping());
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
            checker.handleError(DescriptorException.attributeAndMappingWithTransparentIndirectionMismatch(this.getMapping(), this.validTypeName()));
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
            checker.handleError(DescriptorException.returnAndMappingWithTransparentIndirectionMismatch(this.getMapping(), this.validTypeName()));
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
            checker.handleError(DescriptorException.parameterAndMappingWithTransparentIndirectionMismatch(this.getMapping(), this.validTypeName()));
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
    public Object valueFromBatchQuery(ReadQuery batchQuery, AbstractRecord row, ObjectLevelReadQuery originalQuery) {
        return this.buildIndirectContainer(new BatchValueHolder(batchQuery, row, getForeignReferenceMapping(), originalQuery));
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
        return valueFromQuery(query, row, session);
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