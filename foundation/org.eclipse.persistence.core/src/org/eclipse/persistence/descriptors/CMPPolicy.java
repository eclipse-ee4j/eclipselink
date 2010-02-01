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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ObjectReferenceMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.queries.UpdateObjectQuery;
import org.eclipse.persistence.internal.descriptors.ObjectBuilder;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.identitymaps.CacheId;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * <p>
 * <b>Description</b>: Place holder for CMP specific information.  This class can be set on the ClassDescriptor.
 *
 * @see org.eclipse.persistence.descriptors.PessimisticLockingPolicy
 *
 * @since TopLink 10.1.3
 */
public class CMPPolicy implements java.io.Serializable {
    
    protected Boolean forceUpdate;
    protected Boolean updateAllFields;

    /** Allow the bean to always be locked as it enters a new transaction. */
    protected PessimisticLockingPolicy pessimisticLockingPolicy;


    /** Class originally mapped, before anything was generated. */
    protected Class mappedClass;
    protected ClassDescriptor descriptor;

    /** The object deferral level.  This controls when objects changes will be sent to the Database. */
    protected int modificationDeferralLevel = ALL_MODIFICATIONS;

    /** defer no changes */
    public static final int NONE = 0;

    /** defer updates */
    public static final int UPDATE_MODIFICATIONS = 1;

    /** defer all modifications, inserts and deletes included (default) */
    public static final int ALL_MODIFICATIONS = 2;

    /** This setting will allow customers to control when Toplink will issue the insert SQL for CMP beans. */
    protected int nonDeferredCreateTime = UNDEFINED;

    /** undefined if it is non-deferred issue sql at create */
    public static final int UNDEFINED = 0;

    /** issue SQL after ejbCreate but before ejbPostCreate */
    public static final int AFTER_EJBCREATE = 1;

    /** issue SQL after ejbPostCreate */
    public static final int AFTER_EJBPOSTCREATE = 2;

    public CMPPolicy() {
        this.forceUpdate = null;
        this.updateAllFields = null;
    }

    /**
     * ADVANCED:
     * This setting is only available for CMP beans that are not being deferred.
     * Using it will allow TopLink to  determine if the INSERT SQL should be sent to
     * the database before or after the postCreate call.
     */
    public int getNonDeferredCreateTime() {
        return this.nonDeferredCreateTime;
    }

    /**
     * PUBLIC:
     * Return the policy for bean pessimistic locking
     * @see #org.eclipse.persistence.descriptors.PessimisticLockingPolicy
     */
    public PessimisticLockingPolicy getPessimisticLockingPolicy() {
        return pessimisticLockingPolicy;
    }

    /**
     * ADVANCED:
     * This can be set to control when changes to objects are submitted to the database
     * This is only applicable to TopLink's CMP implementation and not available within
     * the core.
     */
    public void setDeferModificationsUntilCommit(int deferralLevel) {
        this.modificationDeferralLevel = deferralLevel;
    }

    /**
     * PUBLIC:
     * Define the mapped class. This is the class which was originally mapped in the MW
     *
     * @param Class newMappedClass
     */
    public void setMappedClass(Class newMappedClass) {
        mappedClass = newMappedClass;
    }

    /**
     * PUBLIC:
     * Answer the mapped class. This is the class which was originally mapped in the MW
     *
     */
    public Class getMappedClass() {
        return mappedClass;
    }

    /**
     * ADVANCED:
     * This setting is only available for CMP beans that are not being deferred.
     * Using it will allow TopLink to  determine if the INSERT SQL should be sent to
     * the database before or after the postCreate call.
     */
    public void setNonDeferredCreateTime(int createTime) {
        this.nonDeferredCreateTime = createTime;
    }

    /**
     * PUBLIC:
     * Configure bean pessimistic locking
     *
     * @param PessimisticLockingPolicy policy
     * @see #org.eclipse.persistence.descriptors.PessimisticLockingPolicy
     */
    public void setPessimisticLockingPolicy(PessimisticLockingPolicy policy) {
        pessimisticLockingPolicy = policy;
    }

    /**
     * PUBLIC:
     * Return true if bean pessimistic locking is configured
     */
    public boolean hasPessimisticLockingPolicy() {
        return pessimisticLockingPolicy != null;
    }

    /**
     * ADVANCED:
     * This can be used to control when changes to objects are submitted to the database
     * This is only applicable to TopLink's CMP implementation and not available within
     * the core.
     */
    public int getDeferModificationsUntilCommit() {
        return this.modificationDeferralLevel;
    }

    /**
     * ADVANCED:
     * Return true if descriptor is set to always update all registered objects of this type
     */
    public boolean getForceUpdate() {
        // default to false
        return (Boolean.TRUE.equals(this.forceUpdate));
    }

    /**
     * ADVANCED:
     * Configure whether TopLink should always update all registered objects of
     * this type.  NOTE: if set to true, then updateAllFields must also be set
     * to true
     *
     * @param boolean shouldForceUpdate
     */
    public void setForceUpdate(boolean shouldForceUpdate) {
        this.forceUpdate = Boolean.valueOf(shouldForceUpdate);
    }

    /**
     * ADVANCED:
     * Return true if descriptor is set to update all fields for an object of this
     * type when an update occurs.
     */
    public boolean getUpdateAllFields() {
        // default to false
        return Boolean.TRUE.equals(this.updateAllFields);
    }

    /**
     * ADVANCED:
     * Configure whether TopLink should update all fields for an object of this
     * type when an update occurs.
     *
     * @param boolean shouldUpdatAllFields
     */
    public void setUpdateAllFields(boolean shouldUpdatAllFields) {
        this.updateAllFields = Boolean.valueOf(shouldUpdatAllFields);
    }

    /**
     * INTERNAL:
     * return internal tri-state value so we can decide whether to inherit or not at init time.
     */
    public Boolean internalGetForceUpdate() {
        return this.forceUpdate;
    }

    /**
     * INTERNAL:
     * return internal tri-state value so we can decide whether to inherit or not at init time.
     */
    public Boolean internalGetUpdateAllFields() {
        return this.updateAllFields;
    }

    /**
     * INTERNAL:
     * internal method to set the tri-state value. This is done in InheritancePolicy at init time.
     */
    public void internalSetForceUpdate(Boolean newForceUpdateValue) {
        this.forceUpdate = newForceUpdateValue;
    }

    /**
     * INTERNAL:
     * internal method to set the tri-state value. This is done in InheritancePolicy at init time.
     */
    public void internalSetUpdateAllFields(Boolean newUpdateAllFieldsValue) {
        this.updateAllFields = newUpdateAllFieldsValue;
    }

    /**
     * INTERNAL:
     * Initialize the CMPPolicy settings.
     */
    public void initialize(ClassDescriptor descriptor, AbstractSession session) throws DescriptorException {
        // updateAllFields is true so set custom query in DescriptorQueryManager
        // to force full SQL.  Don't overwrite a user defined query
        if (this.getUpdateAllFields() && !descriptor.getQueryManager().hasUpdateQuery()) {
            descriptor.getQueryManager().setUpdateQuery(new UpdateObjectQuery());
        }

        // make sure updateAllFields is set if forceUpdate is true
        if (this.getForceUpdate() && !this.getUpdateAllFields()) {
            throw DescriptorException.updateAllFieldsNotSet(descriptor);
        }
    }

    /**
     * INTERNAL:
     * @return Returns the owningDescriptor.
     */
    public ClassDescriptor getDescriptor() {
        return descriptor;
    }

    /**
     * INTERNAL:
     * @param owningDescriptor The owningDescriptor to set.
     */
    public void setDescriptor(ClassDescriptor owningDescriptor) {
        this.descriptor = owningDescriptor;
    }
    
    /**
     * INTERNAL:
     * Return if this policy is for CMP3.
     */
    public boolean isCMP3Policy() {
        return false;
    }
    /**
     * INTERNAL:
     * Convert all the class-name-based settings in this object to actual class-based
     * settings. This method is used when converting a project that has been built
     * with class names to a project with classes.
     * @param classLoader 
     */
    public void convertClassNamesToClasses(ClassLoader classLoader){
    }

    /**
     * INTERNAL:
     * Create an instance of the composite primary key class for the key object.
     */
    public Object createPrimaryKeyInstanceFromId(Object key, AbstractSession session) {
        List primaryKeyValues;
        if (key instanceof CacheId) {
            primaryKeyValues = new ArrayList(Arrays.asList(((CacheId)key).getPrimaryKey()));
        } else {
            primaryKeyValues = new ArrayList(1);
            primaryKeyValues.add(key);
        }
        // Need to pass in a list, as the method removes from the list as it goes to handle nested ids.
        return createPrimaryKeyInstanceFromPrimaryKeyValues(primaryKeyValues, session);    
    }
    
    /**
     * INTERNAL:
     * Create an instance of the composite primary key class for the key object.
     */
    public Object createPrimaryKeyInstanceFromPrimaryKeyValues(List key, AbstractSession session) {
        Object keyInstance = null;
        KeyElementAccessor[] pkElementArray = getKeyClassFields(getPKClass());
        if ((pkElementArray.length == 1) && (pkElementArray[0] instanceof KeyIsElementAccessor)) {
            DatabaseMapping mapping = getDescriptor().getObjectBuilder().getMappingForAttributeName(pkElementArray[0].getAttributeName());
            if (mapping.isDirectToFieldMapping()) {
                Converter converter = ((DirectToFieldMapping) mapping).getConverter();
                if (converter != null){
                    return converter.convertDataValueToObjectValue(key.get(0), session);
                }
                keyInstance = key.get(0);
            } else if (mapping.isObjectReferenceMapping()) { // what if mapping comes from derived ID.  need to get the derived mapping.
                //get reference descriptor and extract pk from target cmp policy
                keyInstance = mapping.getReferenceDescriptor().getCMPPolicy().createPrimaryKeyInstanceFromId(key, session);
            }
            key.remove(0); // remove processed key incase keys are complex and derrived
        } else {
            keyInstance = getPKClassInstance();
            //get clone of Key so we can remove values.
            for (int index = 0; index < pkElementArray.length; index++) {
                KeyElementAccessor accessor = pkElementArray[index];
                DatabaseMapping mapping = getDescriptor().getObjectBuilder().getMappingForAttributeName(accessor.getAttributeName());
                Object fieldValue = null;
                if (mapping.isDirectToFieldMapping()) {
                    fieldValue = key.get(0);
                    Converter converter = ((DirectToFieldMapping) mapping).getConverter();
                    if (converter != null){
                        fieldValue = converter.convertDataValueToObjectValue(fieldValue, session);
                    }
                    key.remove(0);
                } else if (mapping.isObjectReferenceMapping()) { // what if mapping comes from derived ID.  need to get the derived mapping.
                    //get reference descriptor and extract pk from target cmp policy
                    fieldValue = mapping.getReferenceDescriptor().getCMPPolicy().createPrimaryKeyInstanceFromPrimaryKeyValues(key, session);
                }
                accessor.setValue(keyInstance, fieldValue);
            }
        }

        return keyInstance;
    }    

    /**
     * INTERNAL:
     * Create an instance of the Id class or value from the object.
     */
    public Object createPrimaryKeyInstance(Object object, AbstractSession session) {
        KeyElementAccessor[] pkElementArray = this.getKeyClassFields(getPKClass());
        ObjectBuilder builder = getDescriptor().getObjectBuilder();
        if (pkElementArray.length == 1 && pkElementArray[0] instanceof KeyIsElementAccessor){
            DatabaseMapping mapping = builder.getMappingForAttributeName(pkElementArray[0].getAttributeName());
            Object fieldValue = mapping.getRealAttributeValueFromObject(object, session);
            if (mapping.isObjectReferenceMapping()){
                fieldValue = mapping.getReferenceDescriptor().getCMPPolicy().createPrimaryKeyInstance(fieldValue, session);
            }
            return  fieldValue;
        }
        
        Object keyInstance = getPKClassInstance();
        Set<ObjectReferenceMapping> usedObjectReferenceMappings = new HashSet<ObjectReferenceMapping>();
        for (int index = 0; index < pkElementArray.length; index++) {
            Object keyObj = object;
            KeyElementAccessor accessor = pkElementArray[index];
            DatabaseField field = accessor.getDatabaseField();
            DatabaseMapping mapping = builder.getMappingForField(field);
            // With session validation, the mapping shouldn't be null at this 
            // point, don't bother checking.
            if (!mapping.isObjectReferenceMapping() || !usedObjectReferenceMappings.contains(mapping)){
                while (mapping.isAggregateObjectMapping()) {
                    keyObj = mapping.getRealAttributeValueFromObject(keyObj, session);
                    mapping = mapping.getReferenceDescriptor().getObjectBuilder().getMappingForField(field);
                }
                Object fieldValue = mapping.getRealAttributeValueFromObject(keyObj, session);
                if (mapping.isObjectReferenceMapping()){
                    fieldValue = mapping.getReferenceDescriptor().getCMPPolicy().createPrimaryKeyInstance(fieldValue, session);
                    usedObjectReferenceMappings.add((ObjectReferenceMapping)mapping);
                }
                accessor.setValue(keyInstance, fieldValue);
            }
        }
        
        return keyInstance;
    }
    
    /**
     * INTERNAL:
     */
    public Object getPKClassInstance() {
    	// TODO fix this exception so that it is more descriptive
    	// This method only works in CMP3Policy but was added here for separation
    	// of components
    	throw new RuntimeException("Should not get here.");
    }
    
    /**
     * INTERNAL:
     */
    public Class getPKClass() {
    	// TODO fix this exception so that it is more descriptive
    	// This method only works in CMP3Policy but was added here for separation
    	// of components
    	throw new RuntimeException("Should not get here.");
    }

    /**
     * INTERNAL:
     * Use the key to create a EclipseLink primary key.
     * If the key is simple (direct mapped) then just add it to a vector,
     * otherwise must go through the inefficient process of copying the key into the bean
     * and extracting the key from the bean.
     */
    public Object createPrimaryKeyFromId(Object key, AbstractSession session) {
        // TODO fix this exception so that it is more descriptive
        // This method only works in CMP3Policy but was added here for separation
        // of components
        throw new RuntimeException("Should not get here.");
    }

    /**
     * INTERNAL:
     * Use the key to create a bean and initialize its primary key fields.
     * Note: If is a compound PK then a primary key object is being used.
     * This method should only be used for 'templates' when executing
     * queries.  The bean built will not be given an EntityContext and should
     * not be used as an actual entity bean.
     *
     * @param key Object the primary key to use for initializing the bean's
     *            corresponding pk fields
     * @return Object
     */
    public Object createBeanUsingKey(Object key, AbstractSession session) {
        // TODO fix this exception so that it is more descriptive
        // This method only works in CMP3Policy but was added here for separation
        // of components
        throw new RuntimeException("Should not get here.");
    }
    
    /**
     * INTERNAL:
     * @return Returns the keyClassFields.
     */
    protected KeyElementAccessor[] getKeyClassFields(Class clazz) {
    	// TODO fix this exception so that it is more descriptive
    	// This method only works in CMP3Policy but was added here for separation
    	// of components
    	throw new RuntimeException("Should not get here.");
    }
    
    /**
     * INTERNAL:
     * This is the interface used to encapsulate the the type of key class element
     */
    protected interface KeyElementAccessor {
        public String getAttributeName();
        public DatabaseField getDatabaseField();
        public DatabaseMapping getMapping();
        public Object getValue(Object object, AbstractSession session);
        public void setValue(Object object, Object value);
    }
    
    /**
     * INTERNAL:
     * This class will be used when the keyClass is a primitive
     */
    protected class KeyIsElementAccessor implements KeyElementAccessor, Serializable {
        protected String attributeName;
        protected DatabaseField databaseField;
        protected DatabaseMapping mapping;

        public KeyIsElementAccessor(String attributeName, DatabaseField databaseField, DatabaseMapping mapping) {
            this.attributeName = attributeName;
            this.databaseField = databaseField;
            this.mapping = mapping;
        }

        public String getAttributeName() {
            return attributeName;
        }

        public DatabaseField getDatabaseField() {
            return this.databaseField;
        }
        
        public DatabaseMapping getMapping(){
            return this.mapping;
        }
        
        public Object getValue(Object object, AbstractSession session) {
            return object;
        }
        
        public void setValue(Object object, Object value) {
            // WIP - do nothing for now??? 
        }
    }
}
