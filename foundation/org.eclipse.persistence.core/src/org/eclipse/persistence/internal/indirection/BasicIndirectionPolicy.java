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
package org.eclipse.persistence.internal.indirection;

import java.util.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sessions.remote.RemoteSession;
import org.eclipse.persistence.indirection.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.descriptors.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.sessions.remote.RemoteSessionController;
import org.eclipse.persistence.internal.sessions.remote.RemoteUnitOfWork;
import org.eclipse.persistence.internal.sessions.remote.RemoteValueHolder;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.MergeManager;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.internal.sessions.AbstractSession;

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
    public Object cloneAttribute(Object attributeValue, Object original, Object clone, UnitOfWorkImpl unitOfWork, boolean buildDirectlyFromRow) {
        ValueHolderInterface valueHolder = (ValueHolderInterface) attributeValue;
        ValueHolderInterface result;
        
        if (!buildDirectlyFromRow && unitOfWork.isOriginalNewObject(original)) {
            // CR#3156435 Throw a meaningful exception if a serialized/dead value holder is detected.
            // This can occur if an existing serialized object is attempt to be registered as new.
            if ((valueHolder instanceof DatabaseValueHolder)
                    && (! ((DatabaseValueHolder) valueHolder).isInstantiated())
                    && (((DatabaseValueHolder) valueHolder).getSession() == null)
                    && (! ((DatabaseValueHolder) valueHolder).isSerializedRemoteUnitOfWorkValueHolder())) {
                throw DescriptorException.attemptToRegisterDeadIndirection(original, getMapping());
            }
            if (this.getMapping().getRelationshipPartner() == null) {
                result = new ValueHolder();
                result.setValue(this.getMapping().buildCloneForPartObject(valueHolder.getValue(), original, clone, unitOfWork, false));
            } else {
                //if I have a relationship partner trigger the indirection so that the value will be inserted
                // because of this call the entire tree should be recursively cloned
                AbstractRecord row = null;
                if (valueHolder instanceof DatabaseValueHolder) {
                    row = ((DatabaseValueHolder)valueHolder).getRow();
                }
                result = this.getMapping().createUnitOfWorkValueHolder(valueHolder, original, clone, row, unitOfWork, buildDirectlyFromRow);

                Object newObject = this.getMapping().buildCloneForPartObject(valueHolder.getValue(), original, clone, unitOfWork, false);
                ((UnitOfWorkValueHolder)result).privilegedSetValue(newObject);
                ((UnitOfWorkValueHolder)result).setInstantiated();
            }
        } else {
            AbstractRecord row = null;
            if (valueHolder instanceof DatabaseValueHolder) {
                row = ((DatabaseValueHolder)valueHolder).getRow();
            }
            result = this.getMapping().createUnitOfWorkValueHolder(valueHolder, original, clone, row, unitOfWork, buildDirectlyFromRow);
        }
        return result;
    }

    /**
     * INTERNAL:
     *    Return the primary key for the reference object (i.e. the object
     * object referenced by domainObject and specified by mapping).
     * This key will be used by a RemoteValueHolder.
     */
    public Vector extractPrimaryKeyForReferenceObject(Object referenceObject, AbstractSession session) {
        if (this.objectIsInstantiated(referenceObject)) {
            return super.extractPrimaryKeyForReferenceObject(((ValueHolderInterface)referenceObject).getValue(), session);
        } else {
            return this.getOneToOneMapping().extractPrimaryKeysForReferenceObjectFromRow(this.extractReferenceRow(referenceObject));
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
    public void fixObjectReferences(Object object, Map objectDescriptors, Map processedObjects, ObjectLevelReadQuery query, RemoteSession session) {
        Object attributeValue = this.getMapping().getAttributeValueFromObject(object);
        //bug 4147755 if it is not a Remote Valueholder then treat as if there was no VH...
        if (attributeValue instanceof RemoteValueHolder){
            RemoteValueHolder rvh = (RemoteValueHolder)this.getMapping().getAttributeValueFromObject(object);
            rvh.setSession(session);
            rvh.setMapping(this.getMapping());
    
            if ((!query.shouldMaintainCache()) && ((!query.shouldCascadeParts()) || (query.shouldCascadePrivateParts() && (!getMapping().isPrivateOwned())))) {
                rvh.setQuery(null);
            } else {
                rvh.setQuery(query);
            }
    
            // set to uninstantiated since no objects are serialized past remote value holders
            rvh.setUninstantiated();
        }else{
            this.getMapping().fixRealObjectReferences(object, objectDescriptors, processedObjects, query, session);        
        }
    }

    /**
     * INTERNAL:
     * Return the original indirection object for a unit of work indirection object.
     * This is used when building a new object from the unit of work when the original fell out of the cache.
     */
    public Object getOriginalIndirectionObject(Object unitOfWorkIndirectionObject, AbstractSession session) {
        return this.getOriginalValueHolder(unitOfWorkIndirectionObject, session);
    }

    /**
     * INTERNAL:
     *    Return the original indirection object for a unit of work indirection object.
     */
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
    public Object getOriginalValueHolder(Object unitOfWorkIndirectionObject, AbstractSession session) {
        if (unitOfWorkIndirectionObject instanceof UnitOfWorkValueHolder) {
            ValueHolderInterface valueHolder = ((UnitOfWorkValueHolder) unitOfWorkIndirectionObject).getWrappedValueHolder();
            if ((valueHolder == null) && session.isRemoteUnitOfWork()) {
                // For remote session the original value holder is transient,
                // so the value must be found in the registry or created.
                RemoteSessionController controller = ((RemoteUnitOfWork) session).getParentSessionController();
                Object id = ((UnitOfWorkValueHolder) unitOfWorkIndirectionObject).getWrappedValueHolderRemoteID();
                if (id == null) {
                    // Must build a new value holder.
                    Object object = ((UnitOfWorkValueHolder) unitOfWorkIndirectionObject).getSourceObject();
                    AbstractRecord row = getMapping().getDescriptor().getObjectBuilder().buildRow(object, session);
                    ReadObjectQuery query = new ReadObjectQuery();
                    query.setSession(((RemoteUnitOfWork) session).getParent());
                    valueHolder = (ValueHolderInterface) getMapping().valueFromRow(row, null, query);
                } else {
                    valueHolder = (ValueHolderInterface) controller.getRemoteValueHolders().get(id);
                }
            }
            if ((valueHolder != null) && (valueHolder instanceof DatabaseValueHolder)) {
                ((DatabaseValueHolder) valueHolder).releaseWrappedValueHolder();
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
        getMapping().setAttributeValueInObject(target, new ValueHolder());
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
     *    Iterate over the specified attribute value,
     */
    public void iterateOnAttributeValue(DescriptorIterator iterator, Object attributeValue) {
        iterator.iterateValueHolderForMapping((ValueHolderInterface)attributeValue, this.getMapping());
    }

    /**
     * INTERNAL
     * Replace the client value holder with the server value holder,
     * after copying some of the settings from the client value holder.
     */
    public void mergeRemoteValueHolder(Object clientSideDomainObject, Object serverSideDomainObject, MergeManager mergeManager) {
        // This will always be a remote value holder coming from the server,
        RemoteValueHolder serverValueHolder = (RemoteValueHolder)getMapping().getAttributeValueFromObject(serverSideDomainObject);
        mergeClientIntoServerValueHolder(serverValueHolder, mergeManager);

        getMapping().setAttributeValueInObject(clientSideDomainObject, serverValueHolder);
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
     * Set the value of the appropriate attribute of target to attributeValue.
     * In this case, place the value inside the target's ValueHolder.
     */
    public void setRealAttributeValueInObject(Object target, Object attributeValue) {
        ValueHolderInterface holder = (ValueHolderInterface)this.getMapping().getAttributeValueFromObject(target);
        if (holder == null) {
            holder = new ValueHolder(attributeValue);
        } else {
            holder.setValue(attributeValue);
        }
        super.setRealAttributeValueInObject(target, holder);
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
            throw DescriptorException.valueHolderInstantiationMismatch(attributeValue, this.getMapping());
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
            checker.handleError(DescriptorException.attributeAndMappingWithIndirectionMismatch(this.getMapping()));
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
            checker.handleError(DescriptorException.returnAndMappingWithIndirectionMismatch(this.getMapping()));
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
            checker.handleError(DescriptorException.parameterAndMappingWithIndirectionMismatch(this.getMapping()));
        }
    }

    /**
     * INTERNAL:
     * Return the value to be stored in the object's attribute.
     *    This value is determined by the batchQuery.
     * In this case, wrap the query in a ValueHolder for later invocation.
     */
    public Object valueFromBatchQuery(ReadQuery batchQuery, AbstractRecord row, ObjectLevelReadQuery originalQuery) {
        return new BatchValueHolder(batchQuery, row, this.getForeignReferenceMapping(), originalQuery);
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
