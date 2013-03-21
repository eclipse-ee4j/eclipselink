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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sessions.remote.*;
import org.eclipse.persistence.descriptors.changetracking.ChangeTracker;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.internal.queries.InterfaceContainerPolicy;
import org.eclipse.persistence.internal.sessions.remote.*;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.identitymaps.CacheKey;

/**
 * <h2>Purpose</h2>:
 * NoIndirectionPolicy implements the behavior necessary for a
 * a ForeignReferenceMapping (or TransformationMapping) to
 * directly use domain objects, as opposed to ValueHolders.
 *
 * @see ForeignReferenceMapping
 * @author Mike Norman
 * @since TOPLink/Java 2.5
 */
public class NoIndirectionPolicy extends IndirectionPolicy {

    /**
     * INTERNAL:
     * Construct a new indirection policy.
     */
    public NoIndirectionPolicy() {
        super();
    }

    /**
     * INTERNAL: This method can be used when an Indirection Object is required
     * to be built from a provided ValueHolderInterface object. This may be used
     * for custom value holder types. Certain policies like the
     * TransparentIndirectionPolicy may wrap the valueholder in another object.
     */
    
    public Object buildIndirectObject(ValueHolderInterface valueHolder){
        return valueHolder.getValue();
    }

    /**
     * INTERNAL:
     * Return a clone of the attribute.
     * @param buildDirectlyFromRow indicates that we are building the clone directly
     * from a row as opposed to building the original from the row, putting it in
     * the shared cache, and then cloning the original.
     */
    public Object cloneAttribute(Object attributeValue, Object original, CacheKey cacheKey, Object clone, Integer refreshCascade, AbstractSession cloningSession, boolean buildDirectlyFromRow) {
        // Since valueFromRow was called with the UnitOfWork, attributeValue
        // is already a registered result.
        if (buildDirectlyFromRow) {
            return attributeValue;
        }
        if (!cloningSession.isUnitOfWork()){
            return mapping.buildContainerClone(attributeValue, cloningSession);
        }
        boolean isExisting = !cloningSession.isUnitOfWork() || (((UnitOfWorkImpl) cloningSession).isObjectRegistered(clone) && (!(((UnitOfWorkImpl)cloningSession).isOriginalNewObject(original))));
        return this.getMapping().buildCloneForPartObject(attributeValue, original, cacheKey, clone, cloningSession, refreshCascade, isExisting, isExisting);// only assume from shared cache if it is existing
    }

    /**
     * INTERNAL:
     * Return whether the collection type is appropriate for the indirection policy.
     * In this case, the type MUST be a Vector (or, in the case of jdk1.2,
     * Collection or Map).
     */
    protected boolean collectionTypeIsValid(Class collectionType) {
        return getCollectionMapping().getContainerPolicy().isValidContainerType(collectionType);
    }

    /**
     * INTERNAL:
     * Return the reference row for the reference object.
     * This allows the new row to be built without instantiating
     * the reference object.
     * Return null if the object has already been instantiated.
     */
    public AbstractRecord extractReferenceRow(Object referenceObject) {
        return null;
    }

    /**
     * INTERNAL:
     * An object has been serialized from the server to the client.
     * Replace the transient attributes of the remote value holders
     * with client-side objects.
     */
    public void fixObjectReferences(Object object, Map objectDescriptors, Map processedObjects, ObjectLevelReadQuery query, DistributedSession session) {
        this.mapping.fixRealObjectReferences(object, objectDescriptors, processedObjects, query, session);
    }

    /**
     * INTERNAL:
     * Return the original indirection object for a unit of work indirection object.
     */
    @Override
    public Object getOriginalIndirectionObject(Object unitOfWorkIndirectionObject, AbstractSession session) {
        // This code appears broken, but actually is unreachable because
        // only called when indirection is true.
        return unitOfWorkIndirectionObject;
    }

    /**
     * INTERNAL: Return the original valueHolder object. Access to the
     * underlying valueholder may be required when serializing the valueholder
     * or converting the valueHolder to another type.
     */
    @Override
    public Object getOriginalValueHolder(Object unitOfWorkIndirectionObject, AbstractSession session){
        return unitOfWorkIndirectionObject;
    }

    /**
     * INTERNAL:
     * Return the "real" attribute value, as opposed to any wrapper.
     * This will trigger the wrapper to instantiate the value.
     */
    public Object getRealAttributeValueFromObject(Object object, Object attribute) {
        return attribute;
    }

    /**
     * INTERNAL:
     * Extract and return the appropriate value from the
     * specified remote value holder.
     */
    public Object getValueFromRemoteValueHolder(RemoteValueHolder remoteValueHolder) {
        throw DescriptorException.invalidIndirectionPolicyOperation(this, "getValueFromRemoteValueHolder");
    }

    /**
     * INTERNAL
     * Replace the client value holder with the server value holder,
     * after copying some of the settings from the client value holder.
     */
    public void mergeRemoteValueHolder(Object clientSideDomainObject, Object serverSideDomainObject, org.eclipse.persistence.internal.sessions.MergeManager mergeManager) {
        throw DescriptorException.invalidIndirectionPolicyOperation(this, "mergeRemoteValueHolder");
    }

    /**
     * INTERNAL:
     * Return the null value of the appropriate attribute. That is, the
     * field from the database is NULL, return what should be
     * placed in the object's attribute as a result.
     */
    public Object nullValueFromRow() {
        return null;
    }

    /**
     * INTERNAL:
     * Return whether the specified object is instantiated.
     */
    public boolean objectIsInstantiated(Object object) {
        return true;
    }
    
    /**
     * INTERNAL:
     * Return whether the specified object can be instantiated without database access.
     */
    public boolean objectIsEasilyInstantiated(Object object) {
        return true;
    }

    /**
     * INTERNAL:
     * Return whether the type is appropriate for the indirection policy.
     * In this case, the attribute type CANNOT be ValueHolderInterface.
     */
    protected boolean typeIsValid(Class attributeType) {
        return attributeType != ClassConstants.ValueHolderInterface_Class;
    }

    /**
     * INTERNAL:
     * Return whether the indirection policy actually uses indirection.
     * Here, we must reply false.
     */
    public boolean usesIndirection() {
        return false;
    }

    /**
     * INTERNAL:
     * Verify that attributeType is correct for the
     * indirection policy. If it is incorrect, add an exception to the
     * integrity checker.
     * In this case, the attribute type CANNOT be ValueHolderInterface.
     */
    public void validateDeclaredAttributeType(Class attributeType, IntegrityChecker checker) throws DescriptorException {
        super.validateDeclaredAttributeType(attributeType, checker);
        if (!this.typeIsValid(attributeType)) {
            checker.handleError(DescriptorException.attributeAndMappingWithoutIndirectionMismatch(this.mapping));
        }
    }

    /**
     * INTERNAL:
     * Verify that attributeType is an appropriate collection type for the
     * indirection policy. If it is incorrect, add an exception to the integrity checker.
     * In this case, the type MUST be a Vector (or, in the case of jdk1.2,
     * Collection or Map).
     */
    public void validateDeclaredAttributeTypeForCollection(Class attributeType, IntegrityChecker checker) throws DescriptorException {
        super.validateDeclaredAttributeTypeForCollection(attributeType, checker);
        if (!this.collectionTypeIsValid(attributeType)) {
            InterfaceContainerPolicy policy = (InterfaceContainerPolicy)getCollectionMapping().getContainerPolicy();
            checker.handleError(DescriptorException.attributeTypeNotValid(this.getCollectionMapping(), policy.getInterfaceType()));
        }
    }

    /**
     * INTERNAL:
     *    Verify that getter returnType is correct for the
     * indirection policy. If it is incorrect, add an exception
     * to the integrity checker.
     * In this case, the return type CANNOT be ValueHolderInterface.
     */
    public void validateGetMethodReturnType(Class returnType, IntegrityChecker checker) throws DescriptorException {
        super.validateGetMethodReturnType(returnType, checker);
        if (!this.typeIsValid(returnType)) {
            checker.handleError(DescriptorException.returnAndMappingWithoutIndirectionMismatch(this.mapping));
        }
    }

    /**
     * INTERNAL:
     * Verify that getter returnType is an appropriate collection type for the
     * indirection policy. If it is incorrect, add an exception to the integrity checker.
     * In this case, the type MUST be a Vector (or, in the case of jdk1.2,
     * Collection or Map).
     */
    public void validateGetMethodReturnTypeForCollection(Class returnType, IntegrityChecker checker) throws DescriptorException {
        super.validateGetMethodReturnTypeForCollection(returnType, checker);
        if (!this.collectionTypeIsValid(returnType)) {
            checker.handleError(DescriptorException.getMethodReturnTypeNotValid(getCollectionMapping()));
        }
    }

    /**
     * INTERNAL:
     * Verify that setter parameterType is correct for the
     * indirection policy. If it is incorrect, add an exception
     * to the integrity checker.
     * In this case, the parameter type CANNOT be ValueHolderInterface.
     */
    public void validateSetMethodParameterType(Class parameterType, IntegrityChecker checker) throws DescriptorException {
        super.validateSetMethodParameterType(parameterType, checker);
        if (!this.typeIsValid(parameterType)) {
            checker.handleError(DescriptorException.parameterAndMappingWithoutIndirectionMismatch(this.mapping));
        }
    }

    /**
     * INTERNAL:
     * Verify that setter parameterType is an appropriate collection type for the
     * indirection policy. If it is incorrect, add an exception to the integrity checker.
     * In this case, the type MUST be a Vector (or, in the case of jdk1.2,
     * Collection or Map).
     */
    public void validateSetMethodParameterTypeForCollection(Class parameterType, IntegrityChecker checker) throws DescriptorException {
        super.validateSetMethodParameterTypeForCollection(parameterType, checker);
        if (!this.collectionTypeIsValid(parameterType)) {
            checker.handleError(DescriptorException.setMethodParameterTypeNotValid(getCollectionMapping()));
        }
    }

    /**
     * INTERNAL:
     * Return the value to be stored in the object's attribute.
     * This value is determined by the batchQuery.
     * In this case, extract the result from the query.
     */
    public Object valueFromBatchQuery(ReadQuery batchQuery, AbstractRecord row, ObjectLevelReadQuery originalQuery, CacheKey parentCacheKey) {
        return getForeignReferenceMapping().extractResultFromBatchQuery(batchQuery, parentCacheKey, row, originalQuery.getSession(), originalQuery);
    }

    /**
     * INTERNAL:
     * Return the value to be stored in the object's attribute.
     * This value is determined by invoking the mapping's AttributeTransformer
     */
    public Object valueFromMethod(Object object, AbstractRecord row, AbstractSession session) {
        return getTransformationMapping().invokeAttributeTransformer(row, object, session);
    }

    /**
     * INTERNAL:
     * Return the value to be stored in the object's attribute.
     *    This value is determined by the query.
     * In this case, wrap the query in a ValueHolder for later invocation.
     */
    public Object valueFromQuery(ReadQuery query, AbstractRecord row, Object sourceObject, AbstractSession session) {
        return valueFromQuery(query, row, session);
    }
    
    /**
     * INTERNAL:
     * Return the value to be stored in the object's attribute.
     * This value is determined by the query.
     * In this case, simply execute the query and return its results.
     */
    public Object valueFromQuery(ReadQuery query, AbstractRecord row, AbstractSession session) {
        return session.executeQuery(query, row);
    }

    /**
     * INTERNAL:
     * Return the value to be stored in the object's attribute.
     * This value is determined by the row.
     * In this case, simply return the object.
     */
    public Object valueFromRow(Object object) {
        return object;
    }
    
    /**
     * Set the value of the appropriate attribute of target to attributeValue.
     * In this case, place the value inside the target's ValueHolder.
     * if trackChanges is true, set the value in the object as if the user was setting it.  Allow change tracking to pick up the change.
     */
    @Override
    public void setRealAttributeValueInObject(Object target, Object attributeValue, boolean trackChanges) {
        Object oldValue = null;
        if (trackChanges && (target instanceof ChangeTracker)) {
            oldValue = getRealAttributeValueFromObject(target, mapping.getAttributeValueFromObject(target));
        }
        setRealAttributeValueInObject(target, attributeValue);
        if (trackChanges && (target instanceof ChangeTracker)) {
            PropertyChangeListener listener = ((ChangeTracker) target)._persistence_getPropertyChangeListener();
            if (listener != null && attributeValue != oldValue)
            {
                listener.propertyChange(new PropertyChangeEvent(target, mapping.getAttributeName(), oldValue, attributeValue));
            }
        }
    }
}
