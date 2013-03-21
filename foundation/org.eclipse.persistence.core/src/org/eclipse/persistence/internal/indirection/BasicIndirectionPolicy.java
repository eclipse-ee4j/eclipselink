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

import java.rmi.server.ObjID;
import java.util.*;

import org.eclipse.persistence.mappings.DatabaseMapping.WriteType;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sessions.remote.DistributedSession;
import org.eclipse.persistence.indirection.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.descriptors.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.sessions.remote.RemoteSessionController;
import org.eclipse.persistence.internal.sessions.remote.RemoteUnitOfWork;
import org.eclipse.persistence.internal.sessions.remote.RemoteValueHolder;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.MergeManager;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;

/**
 * <h2>Purpose</h2>:
 * BasicIndirectionPolicy implements the behavior necessary for a
 * a ForeignReferenceMapping (or TransformationMapping) to
 * use ValueHolders to delay the reading of objects from the database
 * until they are actually needed.
 *
 * @see ForeignReferenceMapping
 * @author Mike Norman
 * @since TOPLink/Java 2.5
 */
public class BasicIndirectionPolicy extends IndirectionPolicy {

    /**
     * INTERNAL:
     * Construct a new indirection policy.
     */
    public BasicIndirectionPolicy() {
        super();
    }

    /**
     * INTERNAL:
     *    Return a backup clone of the attribute.
     */
    public Object backupCloneAttribute(Object attributeValue, Object clone, Object backup, UnitOfWorkImpl unitOfWork) {
        //no need to check if the attribute is a valueholder because closeAttribute
        // should always be called first
        ValueHolderInterface valueHolder = (ValueHolderInterface)attributeValue;// cast the value
        ValueHolder result = new ValueHolder();

        // delay instantiation until absolutely necessary
        if ((!(valueHolder instanceof UnitOfWorkValueHolder)) || valueHolder.isInstantiated()) {
            result.setValue(super.backupCloneAttribute(valueHolder.getValue(), clone, backup, unitOfWork));
        } else {
            ((UnitOfWorkValueHolder)valueHolder).setBackupValueHolder(result);
        }

        return result;
    }

    /**
     * INTERNAL: This method can be used when an Indirection Object is required
     * to be built from a provided ValueHolderInterface object. This may be used
     * for custom value holder types. Certain policies like the
     * TransparentIndirectionPolicy may wrap the valueholder in another object.
     */
    
    public Object buildIndirectObject(ValueHolderInterface valueHolder){
        return valueHolder;
    }

    /**
     * INTERNAL:
     *    Return a clone of the attribute.
     *  @param buildDirectlyFromRow indicates that we are building the clone
     *  directly from a row as opposed to building the original from the
     *  row, putting it in the shared cache, and then cloning the original.
     */
    public Object cloneAttribute(Object attributeValue, Object original, CacheKey cacheKey, Object clone, Integer refreshCascade, AbstractSession cloningSession, boolean buildDirectlyFromRow) {
        ValueHolderInterface valueHolder = (ValueHolderInterface) attributeValue;
        ValueHolderInterface result;
        
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
                result = new ValueHolder();
                result.setValue(this.mapping.buildCloneForPartObject(valueHolder.getValue(), original, null, clone, cloningSession, refreshCascade, false, false));
            } else {
                //if I have a relationship partner trigger the indirection so that the value will be inserted
                // because of this call the entire tree should be recursively cloned
                AbstractRecord row = null;
                if (valueHolder instanceof DatabaseValueHolder) {
                    row = ((DatabaseValueHolder)valueHolder).getRow();
                }
                result = this.mapping.createCloneValueHolder(valueHolder, original, clone, row, cloningSession, buildDirectlyFromRow);

                Object newObject = this.mapping.buildCloneForPartObject(valueHolder.getValue(), original, cacheKey, clone, cloningSession, refreshCascade, false, false);
                ((UnitOfWorkValueHolder)result).privilegedSetValue(newObject);
                ((UnitOfWorkValueHolder)result).setInstantiated();
            }
        } else {
            AbstractRecord row = null;
            if (valueHolder instanceof DatabaseValueHolder) {
                row = ((DatabaseValueHolder)valueHolder).getRow();
            }
            result = this.mapping.createCloneValueHolder(valueHolder, original, clone, row, cloningSession, buildDirectlyFromRow);
        }
        return result;
    }

    /**
     * INTERNAL:
     * Return the primary key for the reference object (i.e. the object
     * object referenced by domainObject and specified by mapping).
     * This key will be used by a RemoteValueHolder.
     */
    @Override
    public Object extractPrimaryKeyForReferenceObject(Object referenceObject, AbstractSession session) {
        if (objectIsEasilyInstantiated(referenceObject)) {
            return super.extractPrimaryKeyForReferenceObject(((ValueHolderInterface)referenceObject).getValue(), session);
        } else {
            return getOneToOneMapping().extractPrimaryKeysForReferenceObjectFromRow(extractReferenceRow(referenceObject));
        }
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
            return ((DatabaseValueHolder)referenceObject).getRow();
        }
    }

    /**
     * INTERNAL:
     * An object has been serialized from the server to the client.
     * Replace the transient attributes of the remote value holders
     * with client-side objects.
     */
    public void fixObjectReferences(Object object, Map objectDescriptors, Map processedObjects, ObjectLevelReadQuery query, DistributedSession session) {
        Object attributeValue = this.mapping.getAttributeValueFromObject(object);
        //bug 4147755 if it is not a Remote Valueholder then treat as if there was no VH...
        if (attributeValue instanceof RemoteValueHolder){
            RemoteValueHolder rvh = (RemoteValueHolder)this.mapping.getAttributeValueFromObject(object);
            rvh.setSession(session);
            rvh.setMapping(this.mapping);
    
            if ((!query.shouldMaintainCache()) && ((!query.shouldCascadeParts()) || (query.shouldCascadePrivateParts() && (!this.mapping.isPrivateOwned())))) {
                rvh.setQuery(null);
            } else {
                rvh.setQuery(query);
            }
    
            // set to uninstantiated since no objects are serialized past remote value holders
            rvh.setUninstantiated();
        }else{
            this.mapping.fixRealObjectReferences(object, objectDescriptors, processedObjects, query, session);        
        }
    }

    /**
     * INTERNAL:
     * Return the original indirection object for a unit of work indirection object.
     * This is used when building a new object from the unit of work when the original fell out of the cache.
     */
    @Override
    public Object getOriginalIndirectionObject(Object unitOfWorkIndirectionObject, AbstractSession session) {
        return this.getOriginalValueHolder(unitOfWorkIndirectionObject, session);
    }

    /**
     * INTERNAL:
     *    Return the original indirection object for a unit of work indirection object.
     */
    @Override
    public Object getOriginalIndirectionObjectForMerge(Object unitOfWorkIndirectionObject, AbstractSession session) {
        DatabaseValueHolder holder = (DatabaseValueHolder)getOriginalIndirectionObject(unitOfWorkIndirectionObject, session);
        if (holder != null && holder.getSession()!= null){
            holder.setSession(session);
        }
        return holder;
    }

    /**
     * INTERNAL: Return the original valueHolder object. Access to the
     * underlying valueholder may be required when serializing the valueholder
     * or converting the valueHolder to another type.
     */
    @Override
    public Object getOriginalValueHolder(Object unitOfWorkIndirectionObject, AbstractSession session) {
        if ((unitOfWorkIndirectionObject instanceof UnitOfWorkValueHolder)
                && (((UnitOfWorkValueHolder)unitOfWorkIndirectionObject).getRemoteUnitOfWork() != null)) {
            ValueHolderInterface valueHolder = ((UnitOfWorkValueHolder) unitOfWorkIndirectionObject).getWrappedValueHolder();
            if (valueHolder == null) {
                // For remote session the original value holder is transient,
                // so the value must be found in the registry or created.
                RemoteUnitOfWork remoteUnitOfWork = (RemoteUnitOfWork)((UnitOfWorkValueHolder)unitOfWorkIndirectionObject).getRemoteUnitOfWork();
                RemoteSessionController controller = remoteUnitOfWork.getParentSessionController();
                ObjID id = ((UnitOfWorkValueHolder) unitOfWorkIndirectionObject).getWrappedValueHolderRemoteID();
                if (id != null) {
                    // This value holder may be on the server, or the client,
                    // on the server, the controller should exists, so can lock up in it,
                    // on the client, the id should be enough to create a new remote value holder.
                    if (controller != null) {
                        valueHolder = controller.getRemoteValueHolders().get(id);
                    } else if (session.isRemoteSession()) {
                        valueHolder = new RemoteValueHolder(id);
                        ((RemoteValueHolder)valueHolder).setSession(session);
                    }
                }
                if (valueHolder == null) {
                    // Must build a new value holder.
                    Object object = ((UnitOfWorkValueHolder) unitOfWorkIndirectionObject).getSourceObject();
                    AbstractRecord row = this.mapping.getDescriptor().getObjectBuilder().buildRow(object, session, WriteType.UNDEFINED);
                    ReadObjectQuery query = new ReadObjectQuery();
                    query.setSession(session);
                    valueHolder = (ValueHolderInterface) this.mapping.valueFromRow(row, null, query, true);
                }
                return valueHolder;
            }
        }
        if (unitOfWorkIndirectionObject instanceof WrappingValueHolder) {
            ValueHolderInterface valueHolder =  ((WrappingValueHolder)unitOfWorkIndirectionObject).getWrappedValueHolder();
            if (!session.isProtectedSession()){
                while (valueHolder instanceof WrappingValueHolder && ((WrappingValueHolder)valueHolder).getWrappedValueHolder() != null){
                    valueHolder = ((WrappingValueHolder)valueHolder).getWrappedValueHolder();
                }
            }
            if ((valueHolder != null) && (valueHolder instanceof DatabaseValueHolder)) {
                ((DatabaseValueHolder) valueHolder).releaseWrappedValueHolder(session);
            }
            return valueHolder;
        } else {
            return unitOfWorkIndirectionObject;
        }
    }
    
    /**
     * Reset the wrapper used to store the value.
     */
    public void reset(Object target) {
        this.mapping.setAttributeValueInObject(target, new ValueHolder());
    }
    
    /**
     * INTERNAL:
     * Return the "real" attribute value, as opposed to any wrapper.
     * This will trigger the wrapper to instantiate the value.
     */
    public Object getRealAttributeValueFromObject(Object object, Object attribute) {
        if (attribute instanceof ValueHolderInterface) {
            return ((ValueHolderInterface)attribute).getValue();
        } else {
            return attribute;
        }
    }

    /**
     * INTERNAL:
     * Extract and return the appropriate value from the
     * specified remote value holder.
     */
    public Object getValueFromRemoteValueHolder(RemoteValueHolder remoteValueHolder) {
        return remoteValueHolder.getValue();
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
    
    /**
     * INTERNAL:
     *    Iterate over the specified attribute value,
     */
    public void iterateOnAttributeValue(DescriptorIterator iterator, Object attributeValue) {
        iterator.iterateValueHolderForMapping((ValueHolderInterface)attributeValue, this.mapping);
    }

    /**
     * INTERNAL
     * Replace the client value holder with the server value holder,
     * after copying some of the settings from the client value holder.
     */
    public void mergeRemoteValueHolder(Object clientSideDomainObject, Object serverSideDomainObject, MergeManager mergeManager) {
        // This will always be a remote value holder coming from the server,
        RemoteValueHolder serverValueHolder = (RemoteValueHolder)this.mapping.getAttributeValueFromObject(serverSideDomainObject);
        mergeClientIntoServerValueHolder(serverValueHolder, mergeManager);

        this.mapping.setAttributeValueInObject(clientSideDomainObject, serverValueHolder);
    }

    /**
     * INTERNAL:
     *    Return the null value of the appropriate attribute. That is, the
     * field from the database is NULL, return what should be
     * placed in the object's attribute as a result.
     * In this case, return an empty ValueHolder.
     */
    public Object nullValueFromRow() {
        return new ValueHolder();
    }

    /**
     * INTERNAL:
     * Return whether the specified object is instantiated.
     */
    public boolean objectIsInstantiated(Object object) {
        return ((ValueHolderInterface)object).isInstantiated();
    }
    
    /**
     * INTERNAL:
     * Return whether the specified object can be instantiated without database access.
     */
    public boolean objectIsEasilyInstantiated(Object object) {
        if (object instanceof DatabaseValueHolder) {
            return ((DatabaseValueHolder)object).isEasilyInstantiated();
        } else {
            return true;
        }
    }

    /**
     * INTERNAL:
     * Set the value of the appropriate attribute of target to attributeValue.
     * In this case, place the value inside the target's ValueHolder.
     */
    public void setRealAttributeValueInObject(Object target, Object attributeValue) {
        ValueHolderInterface holder = (ValueHolderInterface)this.mapping.getAttributeValueFromObject(target);
        if (holder == null) {
            holder = new ValueHolder(attributeValue);
        } else {
            holder.setValue(attributeValue);
        }
        super.setRealAttributeValueInObject(target, holder);
    }

    /**
     * INTERNAL:
     * set the source object into QueryBasedValueHolder.
     * Used only by transparent indirection.
     */
    public void setSourceObject(Object sourceObject, Object attributeValue) {        
        if (attributeValue instanceof QueryBasedValueHolder) {
            ((QueryBasedValueHolder)attributeValue).setSourceObject(sourceObject);
        }
    }
    
    /**
     * INTERNAL:
     *    Return whether the type is appropriate for the indirection policy.
     * In this case, the attribute type MUST be ValueHolderInterface.
     */
    protected boolean typeIsValid(Class attributeType) {
        return attributeType == ClassConstants.ValueHolderInterface_Class ||
            attributeType == ClassConstants.WeavedAttributeValueHolderInterface_Class;
    }

    /**
     * INTERNAL:
     * Verify that the value of the attribute within an instantiated object
     * is of the appropriate type for the indirection policy.
     * In this case, the attribute must be non-null and it must be a
     * ValueHolderInterface.
     * If the value is null return a new indirection object to be used for the attribute.
     */
    public Object validateAttributeOfInstantiatedObject(Object attributeValue) {
        // PERF: If the value is null, create a new value holder instance for the attribute value,
        // this allows for indirection attributes to not be instantiated in the constructor as they
        // are typically replaced when reading or cloning so is very inefficient to initialize.
        if (attributeValue == null) {
            return new ValueHolder();
        }
        if (!(attributeValue instanceof ValueHolderInterface)) {
            throw DescriptorException.valueHolderInstantiationMismatch(attributeValue, this.mapping);
        }
        return attributeValue;
    }
    
    /**
     * INTERNAL:
     *    Verify that attributeType is correct for the
     * indirection policy. If it is incorrect, add an exception to the
     * integrity checker.
     * In this case, the attribute type MUST be ValueHolderInterface.
     */
    public void validateDeclaredAttributeType(Class attributeType, IntegrityChecker checker) throws DescriptorException {
        super.validateDeclaredAttributeType(attributeType, checker);
        if (!this.typeIsValid(attributeType)) {
            checker.handleError(DescriptorException.attributeAndMappingWithIndirectionMismatch(this.mapping));
        }
    }

    /**
     * INTERNAL:
     *    Verify that getter returnType is correct for the
     * indirection policy. If it is incorrect, add an exception
     * to the integrity checker.
     * In this case, the return type MUST be ValueHolderInterface.
     */
    public void validateGetMethodReturnType(Class returnType, IntegrityChecker checker) throws DescriptorException {
        super.validateGetMethodReturnType(returnType, checker);
        if (!this.typeIsValid(returnType)) {
            checker.handleError(DescriptorException.returnAndMappingWithIndirectionMismatch(this.mapping));
        }
    }

    /**
     * INTERNAL:
     *    Verify that setter parameterType is correct for the
     * indirection policy. If it is incorrect, add an exception
     * to the integrity checker.
     * In this case, the parameter type MUST be ValueHolderInterface.
     */
    public void validateSetMethodParameterType(Class parameterType, IntegrityChecker checker) throws DescriptorException {
        super.validateSetMethodParameterType(parameterType, checker);
        if (!this.typeIsValid(parameterType)) {
            checker.handleError(DescriptorException.parameterAndMappingWithIndirectionMismatch(this.mapping));
        }
    }

    /**
     * INTERNAL:
     * Return the value to be stored in the object's attribute.
     *    This value is determined by the batchQuery.
     * In this case, wrap the query in a ValueHolder for later invocation.
     */
    public Object valueFromBatchQuery(ReadQuery batchQuery, AbstractRecord row, ObjectLevelReadQuery originalQuery, CacheKey parentCacheKey) {
        return new BatchValueHolder(batchQuery, row, this.getForeignReferenceMapping(), originalQuery, parentCacheKey);
    }

    /**
     * INTERNAL:
     * Return the value to be stored in the object's attribute.
     * This value is determined by invoking the appropriate
     * method on the object and passing it the row and session.
     * In this case, wrap the row in a ValueHolder for later use.
     */
    public Object valueFromMethod(Object object, AbstractRecord row, AbstractSession session) {
        return new TransformerBasedValueHolder(this.getTransformationMapping().getAttributeTransformer(), object, row, session);
    }

    /**
     * INTERNAL:
     * Return the value to be stored in the object's attribute.
     *    This value is determined by the query.
     * In this case, wrap the query in a ValueHolder for later invocation.
     */
    public Object valueFromQuery(ReadQuery query, AbstractRecord row, Object sourceObject, AbstractSession session) {
        return new QueryBasedValueHolder(query, sourceObject, row, session);
    }
    
    /**
     * INTERNAL:
     * Return the value to be stored in the object's attribute.
     *    This value is determined by the query.
     * In this case, wrap the query in a ValueHolder for later invocation.
     */
    public Object valueFromQuery(ReadQuery query, AbstractRecord row, AbstractSession session) {
        return new QueryBasedValueHolder(query, row, session);
    }

    /**
     * INTERNAL:
     * Return the value to be stored in the object's attribute.
     *    This value is determined by the row.
     * In this case, simply wrap the object in a ValueHolder.
     */
    public Object valueFromRow(Object object) {
        return new ValueHolder(object);
    }
}
