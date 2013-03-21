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

import java.io.*;
import java.util.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.internal.descriptors.*;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.sessions.remote.*;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.MergeManager;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sessions.remote.*;

/**
 * <h2>Purpose</h2>:
 * An IndirectionPolicy acts as a 'rules' holder that determines
 * the behavior of a ForeignReferenceMapping (or TransformationMapping)
 * with respect to indirection, or lack thereof.
 * <p>
 * <h3>Description</h3>:
 * IndirectionPolicy is an abstract class that defines the protocol to be implemented by
 * subclasses so that the assorted DatabaseMappings can use an assortment of
 * indirection policies:<ul>
 * <li> no indirection policy (read everything from database)
 * <li> basic indirection policy (use ValueHolders)
 * <li> transparent indirection policy (collections only)
 * <li> proxy indirection policy (transparent 1:1 indirection using JDK 1.3's <CODE>Proxy</CODE>)
 * </ul>
 *
 * <p>
 * <h3>Responsibilities</h3>:
 *     <ul>
 *     <li>instantiate the various IndirectionPolicies
 *     </ul>
 * <p>
 *
 * @see ForeignReferenceMapping
 * @author Mike Norman
 * @since TOPLink/Java 2.5
 */
public abstract class IndirectionPolicy implements Cloneable, Serializable {
    protected DatabaseMapping mapping;

    /**
     * INTERNAL:
     * Construct a new indirection policy.
     */
    public IndirectionPolicy() {
        super();
    }

    /**
     * INTERNAL:
     *    Return a backup clone of the attribute.
     */
    public Object backupCloneAttribute(Object attributeValue, Object clone, Object backup, UnitOfWorkImpl unitOfWork) {
        return this.mapping.buildBackupCloneForPartObject(attributeValue, clone, backup, unitOfWork);
    }

    /**
     * INTERNAL
     * Return true if the refresh should refresh on this mapping or not.
     */
    protected ReadObjectQuery buildCascadeQuery(MergeManager mergeManager) {
        ReadObjectQuery query = new ReadObjectQuery();
        if (this.mapping.getReferenceDescriptor() != null) {
            query.setReferenceClass(this.mapping.getReferenceDescriptor().getJavaClass());
        }
        if (mergeManager.shouldCascadeAllParts()) {
            query.cascadeAllParts();
            query.refreshIdentityMapResult();
        }
        if (mergeManager.shouldCascadePrivateParts() && getForeignReferenceMapping().isPrivateOwned()) {
            query.cascadePrivateParts();
            query.refreshIdentityMapResult();
        }

        return query;
    }
    
    /**
     * INTERNAL: This method can be used when an Indirection Object is required
     * to be built from a provided ValueHolderInterface object. This may be used
     * for custom value holder types. Certain policies like the
     * TransparentIndirectionPolicy may wrap the valueholder in another object.
     */
    
    public abstract Object buildIndirectObject(ValueHolderInterface valueHolder);

    /**
     * INTERNAL:
     * Clones itself.
     */
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    /**
     * INTERNAL:
     * Return a clone of the attribute.
     *  @param builtDirectlyFromRow indicates that we are building the clone
     *  directly from a row as opposed to building the original from the
     *  row, putting it in the shared cache, and then cloning the original.
     */
    public abstract Object cloneAttribute(Object attributeValue, Object original, CacheKey cacheKey, Object clone, Integer refreshCascade, AbstractSession cloningSession, boolean buildDirectlyFromRow);

    /**
     * INTERNAL:
     * Return the primary key for the reference object (i.e. the object
     * object referenced by domainObject and specified by mapping).
     * This key will be used by a RemoteValueHolder.
     */
    public Object extractPrimaryKeyForReferenceObject(Object referenceObject, AbstractSession session) {
        return getOneToOneMapping().extractPrimaryKeysFromRealReferenceObject(referenceObject, session);
    }

    /**
     * INTERNAL:
     * Return the reference row for the reference object.
     * This allows the new row to be built without instantiating
     * the reference object.
     * Return null if the object has already been instantiated.
     */
    public abstract AbstractRecord extractReferenceRow(Object referenceObject);

    /**
     * INTERNAL:
     * An object has been serialized from the server to the client.
     * Replace the transient attributes of the remote value holders
     * with client-side objects.
     */
    public abstract void fixObjectReferences(Object object, Map objectDescriptors, Map processedObjects, ObjectLevelReadQuery query, DistributedSession session);

    /**
     * INTERNAL:
     * Reduce casting clutter....
     */
    protected CollectionMapping getCollectionMapping() {
        return (CollectionMapping)this.mapping;
    }

    /**
     * INTERNAL:
     * Reduce casting clutter....
     */
    protected ForeignReferenceMapping getForeignReferenceMapping() {
        return (ForeignReferenceMapping)this.mapping;
    }

    /**
     * INTERNAL:
     * Return the database mapping that uses the indirection policy.
     */
    public DatabaseMapping getMapping() {
        return mapping;
    }

    /**
     * INTERNAL:
     * Reduce casting clutter....
     */
    protected ObjectReferenceMapping getOneToOneMapping() {
        return (ObjectReferenceMapping)this.mapping;
    }

    /**
     * INTERNAL:
     * Return the original indirection object for a unit of work indirection object.
     */
    public abstract Object getOriginalIndirectionObject(Object unitOfWorkIndirectionObject, AbstractSession session);

    /**
     * INTERNAL:
     *    Return the original indirection object for a unit of work indirection object.
     */
    public Object getOriginalIndirectionObjectForMerge(Object unitOfWorkIndirectionObject, AbstractSession session){
        return getOriginalIndirectionObject(unitOfWorkIndirectionObject, session);
    }

    /**
     * INTERNAL: Return the original valueHolder object. Access to the
     * underlying valueholder may be required when serializing the valueholder
     * or converting the valueHolder to another type.
     */
    public abstract Object getOriginalValueHolder(Object unitOfWorkIndirectionObject, AbstractSession session);
    
    /**
     * INTERNAL:
     * Return the "real" attribute value, as opposed to any wrapper.
     * This will trigger the wrapper to instantiate the value.
     */
    public abstract Object getRealAttributeValueFromObject(Object object, Object attribute);

    /**
     * INTERNAL:
     * Trigger the instantiation of the value.
     */
    public void instantiateObject(Object object, Object attribute) {
        getRealAttributeValueFromObject(object, attribute);
    }
    
    /**
     * INTERNAL:
     * Reduce casting clutter....
     */
    protected AbstractTransformationMapping getTransformationMapping() {
        return (AbstractTransformationMapping)this.mapping;
    }

    /**
     * INTERNAL:
     * Extract and return the appropriate value from the
     * specified remote value holder.
     */
    public abstract Object getValueFromRemoteValueHolder(RemoteValueHolder remoteValueHolder);
    
    /**
     * INTERNAL:
     * The method validateAttributeOfInstantiatedObject(Object attributeValue) fixes the value of the attributeValue 
     * in cases where it is null and indirection requires that it contain some specific data structure.  Return whether this will happen.
     * This method is used to help determine if indirection has been triggered
     * @param attributeValue
     * @return
     * @see validateAttributeOfInstantiatedObject(Object attributeValue)
     */
    public boolean isAttributeValueFullyBuilt(Object attributeValue){
        return true;
    }
    
    /**
     * INTERNAL:
     * Initialize the indirection policy (Do nothing by default)
     */
    public void initialize() {
    }

    /**
     * INTERNAL:
     */
    public boolean isWeavedObjectBasicIndirectionPolicy() {
        return false;
    }
    
    /**
     * INTERNAL:
     * Iterate over the specified attribute value,
     * heeding the settings in the iterator.
     */
    public void iterateOnAttributeValue(DescriptorIterator iterator, Object attributeValue) {
        if (attributeValue != null) {
            this.mapping.iterateOnRealAttributeValue(iterator, attributeValue);
        }
    }

    /**
     * INTERNAL
     * Replace the client value holder with the server value holder,
     * after copying some of the settings from the client value holder.
     */
    protected void mergeClientIntoServerValueHolder(RemoteValueHolder serverValueHolder, MergeManager mergeManager) {
        serverValueHolder.setMapping(this.mapping);
        serverValueHolder.setSession(mergeManager.getSession());

        if (this.mapping.isForeignReferenceMapping()) {
            ObjectLevelReadQuery query = buildCascadeQuery(mergeManager);
            serverValueHolder.setQuery(query);
        }
    }

    /**
     * INTERNAL
     * Replace the client value holder with the server value holder,
     * after copying some of the settings from the client value holder.
     */
    public abstract void mergeRemoteValueHolder(Object clientSideDomainObject, Object serverSideDomainObject, MergeManager mergeManager);

    /**
     * INTERNAL:
     * Return the null value of the appropriate attribute. That is, the
     * field from the database is NULL, return what should be
     * placed in the object's attribute as a result.
     */
    public abstract Object nullValueFromRow();

    /**
     * INTERNAL:
     * Return whether the specified object is instantiated.
     */
    public abstract boolean objectIsInstantiated(Object object);
    
    /**
     * INTERNAL:
     * Return whether the specified object can be instantiated without database access.
     */
    public abstract boolean objectIsEasilyInstantiated(Object object);
    
    /**
     * INTERNAL:
     * Return whether the specified object is instantiated, or if it has changes.
     */
    public boolean objectIsInstantiatedOrChanged(Object object) {
        return objectIsInstantiated(object);
    }

    /**
     * INTERNAL:
     * set the database mapping that uses the indirection policy.
     */
    public void setMapping(DatabaseMapping mapping) {
        this.mapping = mapping;
    }

    /**
     * INTERNAL:
     * Set the value of the appropriate attribute of target to attributeValue.
     * In this case, simply place the value inside the target.
     */
    public void setRealAttributeValueInObject(Object target, Object attributeValue) {
        this.mapping.setAttributeValueInObject(target, attributeValue);
    }
    
    /**
     * INTERNAL:
     * Same functionality as setRealAttributeValueInObject(Object target, Object attributeValue) but allows
     * overridden behavior for IndirectionPolicies that track changes
     * @param target
     * @param attributeValue
     * @param allowChangeTracking
     */
    public void setRealAttributeValueInObject(Object target, Object attributeValue, boolean allowChangeTracking) {
        setRealAttributeValueInObject(target, attributeValue);
    }
    
    /**
     * INTERNAL:
     * set the source object into QueryBasedValueHolder.
     * Used only by transparent indirection.
     */
    public void setSourceObject(Object sourceObject, Object attributeValue) {        
    }
    
    /**
     * ADVANCED:
     * This method will only change the behavior of TransparentIndirectionPolicy.
     * 
     * IndirectList and IndirectSet can be configured not to instantiate the list from the
     * database when you add and remove from them.  IndirectList defaults to this behavior. When
     * Set to true, the collection associated with this TransparentIndirection will be setup so as
     * not to instantiate for adds and removes.  The weakness of this setting for an IndirectSet is
     * that when the set is not instantiated, if a duplicate element is added, it will not be
     * detected until commit time.
     * 
     */
    public void setUseLazyInstantiation(Boolean useLazyInstantiation) {
    }
    
    /**
     * ADVANCED:
     * Returns false unless this is a transparent indirection policy
     * 
     * IndirectList and IndirectSet can be configured not to instantiate the list from the
     * database when you add and remove from them.  IndirectList defaults to this behavior. When
     * Set to true, the collection associated with this TransparentIndirection will be setup so as
     * not to instantiate for adds and removes.  The weakness of this setting for an IndirectSet is
     * that when the set is not instantiated, if a duplicate element is added, it will not be
     * detected until commit time.
     */
    public Boolean shouldUseLazyInstantiation() {
        return false;
    }
    /**
     * Reset the wrapper used to store the value.
     * This is only required if a wrapper is used.
     */
    public void reset(Object target) {
        // Nothing by default.
    }

    /**
     * INTERNAL:
     * Return whether the indirection policy actually uses indirection.
     * The default is true.
     */
    public boolean usesIndirection() {
        return true;
    }
    
    /**
     * INTERNAL:
     * Return whether the indirection policy uses transparent indirection.
     * The default is false.
     */
    public boolean usesTransparentIndirection(){
        return false;
    }

    /**
     * INTERNAL:
     * Verify that the value of the attribute within an instantiated object
     * is of the appropriate type for the indirection policy.
     * If it is incorrect, throw an exception.
     * If the value is null return a new indirection object to be used for the attribute.
     */
    public Object validateAttributeOfInstantiatedObject(Object attributeValue) throws DescriptorException {
        return attributeValue;
    }

    /**
     * INTERNAL:
     * Verify that the container policy is compatible with the
     * indirection policy. If it is incorrect, add an exception to the
     * integrity checker.
     */
    public void validateContainerPolicy(IntegrityChecker checker) throws DescriptorException {
        // by default, do nothing
    }

    /**
     * INTERNAL:
     * Verify that attributeType is correct for the
     * indirection policy. If it is incorrect, add an exception to the
     * integrity checker.
     */
    public void validateDeclaredAttributeType(Class attributeType, IntegrityChecker checker) throws DescriptorException {
        // by default, do nothing
    }

    /**
     * INTERNAL:
     * Verify that attributeType is an appropriate collection type for the
     * indirection policy. If it is incorrect, add an exception to the integrity checker.
     */
    public void validateDeclaredAttributeTypeForCollection(Class attributeType, IntegrityChecker checker) throws DescriptorException {
        // by default, do nothing
    }

    /**
     * INTERNAL:
     * Verify that getter returnType is correct for the
     * indirection policy. If it is incorrect, add an exception
     * to the integrity checker.
     */
    public void validateGetMethodReturnType(Class returnType, IntegrityChecker checker) throws DescriptorException {
        // by default, do nothing
    }

    /**
     * INTERNAL:
     * Verify that getter returnType is an appropriate collection type for the
     * indirection policy. If it is incorrect, add an exception to the integrity checker.
     */
    public void validateGetMethodReturnTypeForCollection(Class returnType, IntegrityChecker checker) throws DescriptorException {
        // by default, do nothing
    }

    /**
     * INTERNAL:
     * Verify that setter parameterType is correct for the
     * indirection policy. If it is incorrect, add an exception
     * to the integrity checker.
     */
    public void validateSetMethodParameterType(Class parameterType, IntegrityChecker checker) throws DescriptorException {
        // by default, do nothing
    }

    /**
     * INTERNAL:
     * Verify that setter parameterType is an appropriate collection type for the
     * indirection policy. If it is incorrect, add an exception to the integrity checker.
     */
    public void validateSetMethodParameterTypeForCollection(Class parameterType, IntegrityChecker checker) throws DescriptorException {
        // by default, do nothing
    }

    /**
     * INTERNAL:
     * Return the value to be stored in the object's attribute.
     * This value is determined by the batchQuery.
     */
    public abstract Object valueFromBatchQuery(ReadQuery batchQuery, AbstractRecord row, ObjectLevelReadQuery originalQuery, CacheKey parentCacheKey);

    /**
     * INTERNAL:
     * Return the value to be stored in the object's attribute.
     * This value is determined by invoking the appropriate
     * method on the object and passing it the row and session.
     */
    public abstract Object valueFromMethod(Object object, AbstractRecord row, AbstractSession session);
    
    /**
     * INTERNAL:
     * Return the value to be stored in the object's attribute.
     * This value is determined by the query.
     */
    public abstract Object valueFromQuery(ReadQuery query, AbstractRecord row, Object sourceObject, AbstractSession session);

    /**
     * INTERNAL:
     * Return the value to be stored in the object's attribute.
     * This value is determined by the query.
     */
    public abstract Object valueFromQuery(ReadQuery query, AbstractRecord row, AbstractSession session);

    /**
     * INTERNAL:
     * Return the value to be stored in the object's attribute.
     * This value is determined by the row.
     */
    public abstract Object valueFromRow(Object object);
}
