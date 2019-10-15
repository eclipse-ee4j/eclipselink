/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
//     02/02/2009-2.0 Chris delahunt
//       - 241765: JPA 2.0 Derived identities
//     04/24/2009-2.0 Guy Pelletier
//       - 270011: JPA 2.0 MappedById support
//     10/21/2009-2.0 Guy Pelletier
//       - 290567: mappedbyid support incomplete
//     12/17/2010-2.2 Guy Pelletier
//       - 330755: Nested embeddables can't be used as embedded ids
//     11/10/2011-2.4 Guy Pelletier
//       - 357474: Address primaryKey option from tenant discriminator column
//     14/05/2012-2.4 Guy Pelletier
//       - 376603: Provide for table per tenant support for multitenant applications
//     08/20/2012-2.4 Guy Pelletier
//       - 381079: EclipseLink dynamic entity does not support embedded-id
package org.eclipse.persistence.internal.jpa;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.persistence.annotations.CacheKeyType;
import org.eclipse.persistence.descriptors.CMPPolicy;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.identitymaps.CacheId;
import org.eclipse.persistence.internal.indirection.WeavedObjectBasicIndirectionPolicy;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.internal.security.PrivilegedGetField;
import org.eclipse.persistence.internal.security.PrivilegedGetMethod;
import org.eclipse.persistence.internal.security.PrivilegedGetValueFromField;
import org.eclipse.persistence.internal.security.PrivilegedMethodInvoker;
import org.eclipse.persistence.internal.security.PrivilegedSetValueInField;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ObjectReferenceMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.mappings.foundation.AbstractColumnMapping;

/**
 * Defines primary key extraction code for use in JPA. A descriptor should have a CMP3Policy
 * attached to handle basic Id as well as IdClass/EmbeddedId usage.
 *
 * @since TopLink 10.1.3
 */

public class CMP3Policy extends CMPPolicy {

    /** Stores the fields for this classes compound primary key class if required. */
    protected transient KeyElementAccessor[] keyClassFields;

    /** Used to look up the KeyElementAccessor for a specific DatabaseField, used for
      resolving DerivedIds */
    protected transient HashMap<DatabaseField,KeyElementAccessor> fieldToAccessorMap;

    // Store the primary key class name
    protected String pkClassName;

    // Stores the class version of the PKClass
    protected Class pkClass = null;

    public CMP3Policy() {
        super();
    }

    /**
     * INTERNAL:
     * Add the read only mappings for the given field to the allMappings list.
     * @param aDescriptor
     * @param field
     * @param allMappings
     */
    protected void addWritableMapping(ClassDescriptor aDescriptor, DatabaseField field, List allMappings) {
        DatabaseMapping writableMapping = aDescriptor.getObjectBuilder().getMappingForField(field);

        if (writableMapping != null) {
            // Since it may be another aggregate mapping, add it to
            // the allMappings list so we can drill down on it as
            // well.
            allMappings.add(writableMapping);
        }
    }

    /**
     * INTERNAL:
     * Add the writable mapping for the given field to the allMappings list.
     * @param aDescriptor
     * @param field
     * @param allMappings
     */
    protected void addReadOnlyMappings(ClassDescriptor aDescriptor, DatabaseField field, List allMappings) {
        List readOnlyMappings = aDescriptor.getObjectBuilder().getReadOnlyMappingsForField(field);

        if (readOnlyMappings != null) {
            allMappings.addAll(readOnlyMappings);
        }
    }

    /**
     * INTERNAL:
     * Clone the CMP3Policy
     */
    @Override
    public CMP3Policy clone() {
        CMP3Policy policy = new CMP3Policy();
        policy.setPrimaryKeyClassName(getPKClassName());
        policy.setPKClass(getPKClass());
        return policy;
    }

    /**
     * INTERNAL:
     * Convert all the class-name-based settings in this object to actual class-based
     * settings. This method is used when converting a project that has been built
     * with class names to a project with classes.
     * @param classLoader
     */
    @Override
    public void convertClassNamesToClasses(ClassLoader classLoader){
        if(getPKClassName() != null){
            try{
                Class aPKClass = null;
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    try {
                        aPKClass = AccessController.doPrivileged(new PrivilegedClassForName(getPKClassName(), true, classLoader));
                    } catch (PrivilegedActionException exception) {
                        throw new IllegalArgumentException(ExceptionLocalization.buildMessage("pk_class_not_found", new Object[] {this.pkClassName}), exception.getException());

                    }
                } else {
                    aPKClass = org.eclipse.persistence.internal.security.PrivilegedAccessHelper.getClassForName(getPKClassName(), true, classLoader);
                }
                setPKClass(aPKClass);
            } catch (ClassNotFoundException exc){
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("pk_class_not_found", new Object[] {this.pkClassName}), exc);
            }
        }
    }

    /**
     * INTERNAL:
     * Return if this policy is for CMP3.
     */
    @Override
    public boolean isCMP3Policy() {
        return true;
    }

    /**
     * INTERNAL:
     */
    public void setPrimaryKeyClassName(String pkClassName) {
        this.pkClassName = pkClassName;
    }

    /**
     * INTERNAL:
     * Return the java Class representing the primary key class name
     */
    @Override
    public Class getPKClass() {
        return this.pkClass;
    }

    /**
     * ADVANCED:
     */
    public void setPKClass(Class pkClass) {
        this.pkClass = pkClass;
    }

    /**
     * INTERNAL:
     */
    public String getPKClassName() {
        return pkClassName;
    }

    /**
     * INTERNAL:
     */
    @Override
    public Object getPKClassInstance() {
        try {
            return getPKClass().newInstance();
        } catch (IllegalAccessException ex) {
            throw DescriptorException.exceptionAccessingPrimaryKeyInstance(this.getDescriptor(), ex);
        } catch (InstantiationException ex){
            throw DescriptorException.exceptionAccessingPrimaryKeyInstance(this.getDescriptor(), ex);
        }
    }

    /**
     * INTERNAL:
     * Use the key to create a EclipseLink primary key.
     * If the key is simple (direct mapped) then just add it to a vector,
     * otherwise must go through the inefficient process of copying the key into the bean
     * and extracting the key from the bean.
     */
    @Override
    public Object createPrimaryKeyFromId(Object key, AbstractSession session) {
        // If the descriptor primary key is mapped through direct-to-field mappings,
        // then no elaborate conversion is required.
        // If key is compound, add each value to the vector.
        KeyElementAccessor[] pkElementArray = this.getKeyClassFields();
        Object[] primaryKey = null;
        if (getDescriptor().getCacheKeyType() != CacheKeyType.ID_VALUE) {
            primaryKey = new Object[pkElementArray.length];
        }
        for (int index = 0; index < pkElementArray.length; index++) {
            DatabaseMapping mapping = pkElementArray[index].getMapping();
            Object fieldValue = null;
            if (mapping.isAbstractColumnMapping()) {
                if (pkElementArray[index].isNestedAccessor()) {
                    // We have nested aggregate(s) in the embedded id pkclass.
                    DatabaseField keyField = pkElementArray[index].getDatabaseField();
                    Object keyToUse = key;
                    DatabaseMapping keyMapping = getDescriptor().getObjectBuilder().getMappingForField(keyField);

                    if (keyMapping.isAggregateMapping()) {
                        keyMapping = keyMapping.getReferenceDescriptor().getObjectBuilder().getMappingForField(keyField);

                        // Keep driving down the nested aggregates ...
                        while (keyMapping.isAggregateMapping()) {
                            keyToUse = keyMapping.getRealAttributeValueFromObject(keyToUse, session);
                            keyMapping = keyMapping.getReferenceDescriptor().getObjectBuilder().getMappingForField(keyField);
                        }

                        fieldValue = ((AbstractColumnMapping)mapping).getFieldValue(pkElementArray[index].getValue(keyToUse, session), session);
                    } else {
                        // This should never hit but just in case ... better to get a proper exception rather than a NPE etc.
                        fieldValue = ((AbstractColumnMapping)mapping).getFieldValue(pkElementArray[index].getValue(keyToUse, session), session);
                    }
                } else {
                    fieldValue = ((AbstractColumnMapping)mapping).getFieldValue(pkElementArray[index].getValue(key, session), session);
                }
            } else {
                fieldValue = pkElementArray[index].getValue(key, session);
                if ( (fieldValue !=null) && (pkClass != null) && (mapping.isOneToOneMapping()) ){
                    OneToOneMapping refmapping = (OneToOneMapping)mapping;
                    DatabaseField targetKey = refmapping.getSourceToTargetKeyFields().get(pkElementArray[index].getDatabaseField());
                    CMPPolicy refPolicy = refmapping.getReferenceDescriptor().getCMPPolicy();
                    if (refPolicy.isCMP3Policy()){
                        Class aPKClass = refPolicy.getPKClass();
                        if ((aPKClass != null) && (aPKClass != fieldValue.getClass()) && (!aPKClass.isAssignableFrom(fieldValue.getClass()))) {
                            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("invalid_pk_class", new Object[] { aPKClass, fieldValue.getClass() }));
                        }
                        fieldValue = ((CMP3Policy)refPolicy).getPkValueFromKeyForField(fieldValue, targetKey, session);
                    }
                }
            }
            if (getDescriptor().getCacheKeyType() == CacheKeyType.ID_VALUE) {
                return fieldValue;
            }
            primaryKey[index] = fieldValue;
        }
        return new CacheId(primaryKey);
    }

    /**
     * INTERNAL:
     * @param cls
     * @param fieldName
     * @return the field from the class with name equal to fieldName.
     * @throws NoSuchFieldException
     */
    protected Field getField(Class cls, String fieldName) throws NoSuchFieldException {
        Field keyField = null;
        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
            try {
                keyField = AccessController.doPrivileged(new PrivilegedGetField(cls, fieldName, true));
            } catch (PrivilegedActionException exception) {
                throw (NoSuchFieldException) exception.getException();
            }
        } else {
            keyField = PrivilegedAccessHelper.getField(cls, fieldName, true);
        }

        return keyField;
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
    @Override
    public Object createBeanUsingKey(Object key, AbstractSession session) {
        try {
            Object bean = this.getDescriptor().getInstantiationPolicy().buildNewInstance();
            KeyElementAccessor[] keyElements = this.getKeyClassFields();
            for (int index = 0; index < keyElements.length; ++index) {
                Object toWriteInto = bean;
                Object keyFieldValue = keyElements[index].getValue(key, session);
                DatabaseField field = keyElements[index].getDatabaseField();
                DatabaseMapping mapping = this.getDescriptor().getObjectBuilder().getMappingForAttributeName(keyElements[index].getAttributeName());
                if (mapping == null) {// must be aggregate
                    mapping = this.getDescriptor().getObjectBuilder().getMappingForField(field);
                }
                while (mapping.isAggregateObjectMapping()) {
                    Object aggregate = mapping.getRealAttributeValueFromObject(toWriteInto, session);
                    if (aggregate == null) {
                        aggregate = mapping.getReferenceDescriptor().getJavaClass().newInstance();
                        mapping.setRealAttributeValueInObject(toWriteInto, aggregate);
                    }
                    mapping = mapping.getReferenceDescriptor().getObjectBuilder().getMappingForAttributeName(keyElements[index].getAttributeName());
                    if (mapping == null) {// must be aggregate
                        mapping = this.getDescriptor().getObjectBuilder().getMappingForField(field);
                    }

                    //change the object to write into to the aggregate for the next stage of the
                    // loop or for when we exit the loop.
                    toWriteInto = aggregate;
                }
                mapping.setRealAttributeValueInObject(toWriteInto, keyFieldValue);
            }
            return bean;
        } catch (Exception e) {
            throw DescriptorException.errorUsingPrimaryKey(key, this.getDescriptor(), e);
        }
    }

    /**
     * INTERNAL:
     * Cache the bean's primary key fields so speed up creating of primary key
     * objects and initialization of beans.
     *
     * Note, we have to re-look up the fields for the bean class since
     * these fields may have been loaded with the wrong loader (thank you Kirk).
     * If the key is compound, we also have to look up the fields for the key.
     */
    protected KeyElementAccessor[] initializePrimaryKeyFields(Class keyClass, AbstractSession session) {
        KeyElementAccessor[] pkAttributes = null;
        ClassDescriptor aDescriptor = this.getDescriptor();

        fieldToAccessorMap = new HashMap<DatabaseField,KeyElementAccessor>();
        int numberOfIDFields = aDescriptor.getPrimaryKeyFields().size();
        pkAttributes = new KeyElementAccessor[numberOfIDFields];
        Iterator attributesIter = aDescriptor.getPrimaryKeyFields().iterator();

        // Used fields in case it is an embedded class
        for (int i = 0; attributesIter.hasNext(); i++) {
            DatabaseField field = (DatabaseField)attributesIter.next();

            // We need to check all mappings for this field, not just the writable one and instead of
            // having multiple sections of duplicate code we'll just add the writable mapping directly
            // to the list.
            List allMappings = new ArrayList(1);
            addReadOnlyMappings(aDescriptor, field, allMappings);
            addWritableMapping(aDescriptor, field, allMappings);

            // This exception will be used to determine if the element (field or method) from
            // the mapping was found on the key class was found or not and throw an exception otherwise.
            Exception noSuchElementException = null;

            // Set the current key class ...
            Class currentKeyClass = keyClass;

            // We always start by looking at the writable mappings first. Our preference is to use the
            // writable mappings unless a derived id mapping is specified in which case we'll want to use
            // that mapping instead when we find it.
            for (int index = (allMappings.size() - 1); index >= 0; --index) {
                DatabaseMapping mapping = (DatabaseMapping) allMappings.get(index);

                // So here is the ugly check to see if we want to further look at this mapping as part of
                // the id or not.
                if (aDescriptor.hasDerivedId() && ! mapping.derivesId()) {
                    // If the mapping is not a derived id, then we need to keep looking for the mapping that
                    // is marked as the derived id mapping. However, in a mapped by id case, we may have
                    // 'extra' non-derived id fields within the embeddable class (and the writable mapping
                    // that we care about at this point will be defined on the embeddable descriptor). Therefore,
                    // we can't bail at this point and must drill down further into the embeddable to make sure
                    // we initialize this portion of the composite id.
                    if (mapping.isAggregateMapping() && allMappings.size() > 1) {
                        // Bail ... more mappings to check.
                        continue;
                    }
                } else if (mapping.isForeignReferenceMapping() && !mapping.isOneToOneMapping()) {
                    // JPA 2.0 allows DerrivedIds, so Id fields are either OneToOne or DirectToField mappings
                    continue;
                }

                if (mapping.isAggregateMapping()) {
                    // Either this aggregate is the key class, or we need to drill down further. Add the read
                    // only and writable mappings from the aggregate.
                    addReadOnlyMappings(mapping.getReferenceDescriptor(), field, allMappings);
                    addWritableMapping(mapping.getReferenceDescriptor(), field, allMappings);

                    // Since we added the mappings from this aggregate mapping, we should remove this aggregate
                    // mapping from the allMappings list. Otherwise, if the mapping for the primary key field is
                    // not found in the aggregate (or nested aggregate) then we will hit an infinite loop when
                    // searching the aggregate and its mappings. Note: This is cautionary, since in reality, this
                    // 'should' never happen, but if it does we certainly would rather throw an exception instead
                    // of causing an infinite loop.
                    allMappings.remove(mapping);

                    // Update the index to parse the next mapping correctly.
                    index = allMappings.size();

                    // Update the key class now ...
                    currentKeyClass = mapping.getReferenceDescriptor().getJavaClass();
                } else {
                    String fieldName = (mapping.hasMapsIdValue()) ? mapping.getMapsIdValue() : mapping.getAttributeName();

                    if (currentKeyClass == null || mapping.isMultitenantPrimaryKeyMapping()) {
                        // Without a currentKeyClass, the primary key is a non compound key but
                        // we may need to add any multitenant primary key mappings that are
                        // defined and we need an accessor for them. The same case will hold
                        // true when we do have a currentKeyClass. Multitenant primary keys
                        // must still be added.
                        pkAttributes[i] = new KeyIsElementAccessor(fieldName, field, mapping);
                        if (mapping.isAbstractDirectMapping()) {
                            setPKClass(ConversionManager.getObjectClass(mapping.getAttributeClassification()));
                        } else if (mapping.isOneToOneMapping()) {
                            ClassDescriptor refDescriptor = mapping.getReferenceDescriptor();
                            // ensure the referenced descriptor was initialized
                            if (!session.isRemoteSession()) {
                                refDescriptor.initialize(session);
                            }
                            CMPPolicy refPolicy = refDescriptor.getCMPPolicy();
                            setPKClass(refPolicy.getPKClass());
                        }
                        fieldToAccessorMap.put(field, pkAttributes[i]);
                        noSuchElementException = null;
                    } else {
                        if (mapping.isOneToOneMapping()){
                            ClassDescriptor refDescriptor = mapping.getReferenceDescriptor();
                            // ensure the referenced descriptor was initialized
                            if (!session.isRemoteSession()) {
                                refDescriptor.initialize(session);
                            }
                            CMPPolicy refPolicy = refDescriptor.getCMPPolicy();
                            if ((refPolicy!=null) && refPolicy.isCMP3Policy() && (refPolicy.getPKClass() == currentKeyClass)){
                                //Since the ref pk class is our pk class, get the accessor we need to pull the value out of the PK class for our field
                                OneToOneMapping refmapping = (OneToOneMapping)mapping;
                                DatabaseField targetKey = refmapping.getSourceToTargetKeyFields().get(field);
                                pkAttributes[i] = ((CMP3Policy)refPolicy).fieldToAccessorMap.get(targetKey);
                                //associate their accessor to our field so we can look it up when we need it
                                this.fieldToAccessorMap.put(field, pkAttributes[i]);
                                noSuchElementException = null;
                                break;
                            }
                        }

                        try {
                            pkAttributes[i] = new FieldAccessor(this, getField(currentKeyClass, fieldName), fieldName, field, mapping, currentKeyClass != keyClass);
                            fieldToAccessorMap.put(field, pkAttributes[i]);
                            noSuchElementException = null;
                        } catch (NoSuchFieldException ex) {
                            String getMethodName = null;
                            String setMethodName = null;
                            if(mapping.isObjectReferenceMapping() && ((ObjectReferenceMapping)mapping).getIndirectionPolicy().isWeavedObjectBasicIndirectionPolicy()) {
                                WeavedObjectBasicIndirectionPolicy weavedIndirectionPolicy = (WeavedObjectBasicIndirectionPolicy)((ObjectReferenceMapping)mapping).getIndirectionPolicy();
                                if (weavedIndirectionPolicy.hasUsedMethodAccess()) {
                                    getMethodName = weavedIndirectionPolicy.getGetMethodName();
                                    setMethodName = weavedIndirectionPolicy.getSetMethodName();
                                }
                            } else {
                                getMethodName = mapping.getGetMethodName();
                                setMethodName = mapping.getSetMethodName();
                            }
                            if (getMethodName != null) {
                                // Must be a property.
                                try {
                                    Method getMethod = null;
                                    if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                                        try {
                                            getMethod = AccessController.doPrivileged(new PrivilegedGetMethod(currentKeyClass, getMethodName, new Class[] {}, true));
                                        } catch (PrivilegedActionException exception) {
                                            throw (NoSuchMethodException)exception.getException();
                                        }
                                    } else {
                                        getMethod = PrivilegedAccessHelper.getMethod(currentKeyClass, getMethodName, new Class[] {}, true);
                                    }
                                    Method setMethod = null;
                                    if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                                        try {
                                            setMethod = AccessController.doPrivileged(new PrivilegedGetMethod(currentKeyClass, setMethodName, new Class[] {getMethod.getReturnType()}, true));
                                        } catch (PrivilegedActionException exception) {
                                            throw (NoSuchMethodException)exception.getException();
                                        }
                                    } else {
                                        setMethod = PrivilegedAccessHelper.getMethod(currentKeyClass, setMethodName, new Class[] {getMethod.getReturnType()}, true);
                                    }
                                    pkAttributes[i] = new PropertyAccessor(this, getMethod, setMethod, fieldName, field, mapping, currentKeyClass != keyClass);
                                    this.fieldToAccessorMap.put(field, pkAttributes[i]);
                                    noSuchElementException = null;
                                } catch (NoSuchMethodException exs) {
                                    // not a field not a method, but a pk class is defined.  Check for other mappings
                                    noSuchElementException = exs;
                                }
                            } else {
                                noSuchElementException = ex;
                            }

                            // If we can't load the field or methods and the attribute accessor is a values
                            // accessor then we're dealing with a dynamic entity.
                            if (noSuchElementException != null && mapping.getAttributeAccessor().isValuesAccessor()) {
                                pkAttributes[i] = new ValuesFieldAccessor(fieldName, field, mapping, currentKeyClass != keyClass);
                                noSuchElementException = null;
                            }
                        }
                    }

                    if (mapping.derivesId() || noSuchElementException == null) {
                        // Break out of the loop as we do not need to look for
                        // any more mappings
                        break;
                    }
                }
            } // end for loop

            if (noSuchElementException != null) {
                throw DescriptorException.errorUsingPrimaryKey(keyClass, getDescriptor(), noSuchElementException);
            }
        } // end first for loop

        return pkAttributes;
    }

    /**
     * INTERNAL:
     * @return Returns the keyClassFields.
     */
    @Override
    protected KeyElementAccessor[] getKeyClassFields() {
        return this.keyClassFields;
    }

    // Made static for performance reasons.
    private static abstract class CommonAccessor implements KeyElementAccessor {
        private final String attributeName;
        private final DatabaseField databaseField;
        protected final DatabaseMapping mapping;
        private final boolean isNestedAccessor;

        public CommonAccessor(String attributeName, DatabaseField field, DatabaseMapping mapping, boolean isNestedAccessor) {
            this.attributeName = attributeName;
            this.databaseField = field;
            this.mapping = mapping;
            this.isNestedAccessor = isNestedAccessor;
        }

        @Override
        public String getAttributeName() {
            return this.attributeName;
        }

        @Override
        public DatabaseField getDatabaseField() {
            return this.databaseField;
        }

        @Override
        public DatabaseMapping getMapping() {
            return this.mapping;
        }

        @Override
        public boolean isNestedAccessor() {
            return isNestedAccessor;
        }

    }

    // Made static final for performance reasons.
    /**
     * INTERNAL:
     * This class is used when the key class element is a property
     */
    private static final class PropertyAccessor extends CommonAccessor {
        private final Method getMethod;
        private final Method setMethod;
        private final CMPPolicy policy;

        public PropertyAccessor(CMPPolicy policy, Method getMethod, Method setMethod, String attributeName, DatabaseField databaseField, DatabaseMapping mapping, boolean isNestedAccessor) {
            super(attributeName, databaseField, mapping, isNestedAccessor);
            this.getMethod = getMethod;
            this.setMethod = setMethod;
            this.policy = policy;
        }

        @Override
        public Object getValue(Object object, AbstractSession session) {
            try {
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    try {
                        return AccessController.doPrivileged(new PrivilegedMethodInvoker(this.getMethod, object, new Object[] {  }));
                    } catch (PrivilegedActionException exception) {
                        Exception throwableException = exception.getException();
                        if (throwableException instanceof IllegalAccessException) {
                            throw (IllegalAccessException)throwableException;
                        } else {
                            throw (java.lang.reflect.InvocationTargetException)throwableException;
                        }
                    }
                } else {
                    return PrivilegedAccessHelper.invokeMethod(this.getMethod, object, new Object[] {  });
                }
            } catch (Exception ex) {
                throw DescriptorException.errorUsingPrimaryKey(object, policy.getDescriptor(), ex);
            }
        }

        @Override
        public void setValue(Object object, Object value) {
            try {
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    try {
                        AccessController.doPrivileged(new PrivilegedMethodInvoker(this.setMethod, object, new Object[] {value}));
                    } catch (PrivilegedActionException exception) {
                        Exception throwableException = exception.getException();
                        if (throwableException instanceof IllegalAccessException) {
                            throw (IllegalAccessException)throwableException;
                        } else {
                            throw (java.lang.reflect.InvocationTargetException)throwableException;
                        }
                    }
                } else {
                    PrivilegedAccessHelper.invokeMethod(this.setMethod, object, new Object[] {value});
                }
            } catch (Exception ex) {
                throw DescriptorException.errorUsingPrimaryKey(object, policy.getDescriptor(), ex);
            }
        }
    }

    // Made static final for performance reasons.
    /**
     * INTERNAL:
     * This class will be used when the element of the keyclass is a field
     */
    private static final class FieldAccessor extends CommonAccessor {
        private final Field field;
        private final CMPPolicy policy;

        public FieldAccessor(CMPPolicy policy, Field field, String attributeName, DatabaseField databaseField, DatabaseMapping mapping, boolean isNestedAccessor) {
            super(attributeName, databaseField, mapping, isNestedAccessor);
            this.field = field;
            this.policy = policy;
        }

         @Override
         public Object getValue(Object object, AbstractSession session) {
            try {
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    try {
                        return AccessController.doPrivileged(new PrivilegedGetValueFromField(field, object));
                    } catch (PrivilegedActionException exception) {
                        throw DescriptorException.errorUsingPrimaryKey(object, policy.getDescriptor(), exception.getException());                    }
                } else {
                    return org.eclipse.persistence.internal.security.PrivilegedAccessHelper.getValueFromField(field, object);
                }
            } catch (Exception ex) {
                throw DescriptorException.errorUsingPrimaryKey(object, policy.getDescriptor(), ex);
            }
        }

        @Override
        public void setValue(Object object, Object value) {
            try {
                Field pkField = null;
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    try {
                        pkField = AccessController.doPrivileged(new PrivilegedGetField(object.getClass(), field.getName(), true));
                        AccessController.doPrivileged(new PrivilegedSetValueInField(pkField, object, value));
                    } catch (PrivilegedActionException exception) {
                        throw DescriptorException.errorUsingPrimaryKey(object, policy.getDescriptor(), exception.getException());
                    }
                } else {
                    pkField = PrivilegedAccessHelper.getField(object.getClass(), field.getName(), true);
                    PrivilegedAccessHelper.setValueInField(pkField, object, value);
                }
            } catch (Exception ex) {
                throw DescriptorException.errorUsingPrimaryKey(object, policy.getDescriptor(), ex);
            }
        }
    }

    // Made static final for performance reasons.
    /**
     * INTERNAL:
     * This class will be used when the element of the keyclass is a virtual field.
     */
    private static final class ValuesFieldAccessor extends CommonAccessor {

        public ValuesFieldAccessor(String attributeName, DatabaseField databaseField, DatabaseMapping mapping, boolean isNestedAccessor) {
            super(attributeName, databaseField, mapping, isNestedAccessor);
        }

        @Override
        public Object getValue(Object object, AbstractSession session) {
        return mapping.getRealAttributeValueFromObject(object, session);
        }

        @Override
        public void setValue(Object object, Object value) {
        mapping.setRealAttributeValueInObject(object, value);
        }
    }

    /**
     * INTERNAL:
     * Pull the value for the field from the key.
     *
     * @param key Object the primary key to use to get the value for the field
     * @param field DatabaseField the field to find a value for
     * @return Object
     */
    public Object getPkValueFromKeyForField(Object key, DatabaseField field, AbstractSession session){
        Object fieldValue = null;
        KeyElementAccessor accessor = this.fieldToAccessorMap.get(field);
        DatabaseMapping mapping = accessor.getMapping();
        if (mapping.isAbstractColumnMapping()) {
            fieldValue = ((AbstractColumnMapping)mapping).getFieldValue(accessor.getValue(key, session), session);
        } else {
            fieldValue = accessor.getValue(key, session);
            if (mapping.isOneToOneMapping()){
                OneToOneMapping refmapping = (OneToOneMapping)mapping;
                DatabaseField targetKey = refmapping.getSourceToTargetKeyFields().get(accessor.getDatabaseField());
                CMPPolicy refPolicy = refmapping.getReferenceDescriptor().getCMPPolicy();
                if (refPolicy.isCMP3Policy()){
                    Class pkClass = refPolicy.getPKClass();
                    if ((pkClass != null) && (pkClass != fieldValue.getClass()) && (!pkClass.isAssignableFrom(fieldValue.getClass()))) {
                        throw new IllegalArgumentException(ExceptionLocalization.buildMessage("invalid_pk_class", new Object[] { refPolicy.getPKClass(), fieldValue.getClass() }));
                    }
                    fieldValue = ((CMP3Policy)refPolicy).getPkValueFromKeyForField(fieldValue, targetKey, session);
                }
            }
        }
        return fieldValue;
    }

    /**
     * INTERNAL:
     * Initialize the CMPPolicy settings.
     */
    @Override
    public void initialize(ClassDescriptor descriptor, AbstractSession session) throws DescriptorException {
        super.initialize(descriptor, session);

        this.keyClassFields = initializePrimaryKeyFields(this.pkClass, session);
    }

    /**
     * INTERNAL:
     * Initialize the CMPPolicy settings for remote sessions.
     */
    @Override
    public void remoteInitialize(ClassDescriptor descriptor, AbstractSession session) throws DescriptorException {
        super.remoteInitialize(descriptor, session);

        this.keyClassFields = initializePrimaryKeyFields(null, session);
    }
}
