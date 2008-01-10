/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.descriptors;

import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.queries.UpdateObjectQuery;
import org.eclipse.persistence.internal.descriptors.CMPLifeCycleListener;
import org.eclipse.persistence.internal.descriptors.ObjectBuilder;
import org.eclipse.persistence.internal.helper.DatabaseField;
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
    // SPECJ: Temporary global optimization flag, use to disable uow merge.
    public static boolean OPTIMIZE_PESSIMISTIC_CMP = false;
    
    protected Boolean forceUpdate;
    protected Boolean updateAllFields;

    /** Allow the bean to always be locked as it enters a new transaction. */
    protected PessimisticLockingPolicy pessimisticLockingPolicy;

    /** Allows for the CMP life-cycle events to be intercepted from core by the CMP integration. */
    protected CMPLifeCycleListener lifeCycleListener;

    /** Class originally mapped, before anything was generated. */
    protected Class mappedClass;
    protected ClassDescriptor descriptor;

    /** The object deferral level.  This controlls when objects changes will be sent to the Database. */
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
     * INTERNAL:
     * Notify that the insert operation has occurred, allow a sequence primary key to be reset.
     */
    public void postInsert(Object bean, AbstractSession session) {
        if (getLifeCycleListener() != null) {
            getLifeCycleListener().postInsert(bean, session);
        }
    }

    /**
     * INTERNAL:
     * Allow the ejbLoad life-cycle callback to be called.
     */
    public void invokeEJBLoad(Object bean, AbstractSession session) {
        if (getLifeCycleListener() != null) {
            getLifeCycleListener().invokeEJBLoad(bean, session);
        }
    }

    /**
     * INTERNAL:
     * Allow the ejbStore life-cycle callback to be called.
     */
    public void invokeEJBStore(Object bean, AbstractSession session) {
        if (getLifeCycleListener() != null) {
            getLifeCycleListener().invokeEJBStore(bean, session);
        }
    }

    /**
     * INTERNAL:
     * Return the CMP life-cycle listener, used to intercept events from core by CMP integration.
     */
    public CMPLifeCycleListener getLifeCycleListener() {
        return this.lifeCycleListener;
    }

    /**
     * INTERNAL:
     * Set the CMP life-cycle listener, used to intercept events from core by CMP integration.
     * This should be set by the CMP integration during deployment.
     */
    public void setLifeCycleListener(CMPLifeCycleListener lifeCycleListener) {
        this.lifeCycleListener = lifeCycleListener;
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
    public Object createPrimaryKeyInstance(Object key, AbstractSession session) {
        Object keyInstance = getPKClassInstance();
        ObjectBuilder builder = getDescriptor().getObjectBuilder();
        KeyElementAccessor[] pkElementArray = this.getKeyClassFields(getPKClass());
                
        for (int index = 0; index < pkElementArray.length; index++) {
            KeyElementAccessor accessor = pkElementArray[index];
            DatabaseMapping mapping = builder.getMappingForAttributeName(accessor.getAttributeName());
            // With session validation, the mapping shouldn't be null at this 
            // point, don't bother checking.
            
            while (mapping.isAggregateObjectMapping()) {
                mapping = mapping.getReferenceDescriptor().getObjectBuilder().getMappingForAttributeName(pkElementArray[index].getAttributeName());
            
                if (mapping == null) { // must be aggregate
                    mapping = builder.getMappingForField(accessor.getDatabaseField());
                }
            }
            
            Object fieldValue = mapping.getRealAttributeValueFromObject(key, session);
            accessor.setValue(keyInstance, fieldValue);
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
     * This is the interface used to encapsilate the the type of key class element
     */
    protected interface KeyElementAccessor {
        public String getAttributeName();
        public DatabaseField getDatabaseField();
        public Object getValue(Object object);
        public void setValue(Object object, Object value);
    }
    
    /**
     * INTERNAL:
     * This class will be used when the keyClass is a primitive
     */
    protected class KeyIsElementAccessor implements KeyElementAccessor {
        protected String attributeName;
        protected DatabaseField databaseField;

        public KeyIsElementAccessor(String attributeName, DatabaseField databaseField) {
            this.attributeName = attributeName;
            this.databaseField = databaseField;
        }

        public String getAttributeName() {
            return attributeName;
        }

        public DatabaseField getDatabaseField() {
            return this.databaseField;
        }
        
        public Object getValue(Object object) {
            return object;
        }
        
        public void setValue(Object object, Object value) {
            // WIP - do nothing for now??? 
        }
    }
}