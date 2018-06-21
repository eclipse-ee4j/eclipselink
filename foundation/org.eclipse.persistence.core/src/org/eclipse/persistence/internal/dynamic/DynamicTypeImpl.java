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
//     dclarke, mnorman - Dynamic Persistence
//       http://wiki.eclipse.org/EclipseLink/Development/Dynamic
//       (https://bugs.eclipse.org/bugs/show_bug.cgi?id=200045)
//
package org.eclipse.persistence.internal.dynamic;

//javase imports
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

//EclipseLink imports
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.exceptions.DynamicException;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;

/**
 * An EntityType provides a metadata facade into the EclipseLink
 * object-relational metadata (descriptors & mappings) with specific knowledge
 * of the entity types being dynamic.
 *
 * @author dclarke, mnorman
 * @since EclipseLink 1.2
 */
public class DynamicTypeImpl implements DynamicType, Cloneable {

    protected ClassDescriptor descriptor;
    protected DynamicType parentType;
    protected DynamicPropertiesManager dpm;

    /**
     * These properties require initialization when a new instance is created.
     * This includes properties that are primitives as well as relationships
     * requiring indirection ValueHolders or collections.
     */
    protected Set<DatabaseMapping> mappingsRequiringInitialization = new HashSet<DatabaseMapping>();

    protected DynamicTypeImpl() {
        super();
    }

    /**
     * Creation of an EntityTypeImpl for an existing Descriptor with mappings.
     *
     * @param descriptor
     */
    public DynamicTypeImpl(ClassDescriptor descriptor, DynamicType parentType) {
        this();
        this.descriptor = descriptor;
        this.parentType = parentType;
    }

    @Override
    public Object clone() {
        // clone yerself
        try {
            return super.clone();
        }
        catch (Exception exception) {
            throw new AssertionError(exception);
        }
    }

    @Override
    public ClassDescriptor getDescriptor() {
        return this.descriptor;
    }
    public void setDescriptor(ClassDescriptor descriptor) {
        this.descriptor = descriptor;
    }


    public DynamicPropertiesManager getDynamicPropertiesManager() {
        return dpm;
    }

    public void setDynamicPropertiesManager(DynamicPropertiesManager dpm) {
        this.dpm = dpm;
    }

    @Override
    public DynamicType getParentType() {
        return this.parentType;
    }

    public List<DatabaseMapping> getMappings() {
        return getDescriptor().getMappings();
    }

    /**
     * @see DynamicType#getName()
     */
    @Override
    public String getName() {
        return getDescriptor().getAlias();
    }

    @Override
    public String getClassName() {
        return getDescriptor().getJavaClassName();
    }

    @Override
    public int getNumberOfProperties() {
        return dpm.getPropertyNames().size();
    }

    public Set<DatabaseMapping> getMappingsRequiringInitialization() {
        return mappingsRequiringInitialization;
    }


    public boolean isInitialized() {
        return getDescriptor().isFullyInitialized();
    }

    @Override
    public boolean containsProperty(String propertyName) {
        return dpm.contains(propertyName);
    }

    @Override
    public Class<? extends DynamicEntity> getJavaClass() {
        return getDescriptor().getJavaClass();
    }

    public DatabaseMapping getMapping(String propertyName) {
        DatabaseMapping mapping = getDescriptor().getMappingForAttributeName(propertyName);
        if (mapping == null) {
            throw DynamicException.invalidPropertyName(this, propertyName);
        }
        return mapping;
    }

    @Override
    public List<String> getPropertiesNames() {
        return dpm.getPropertyNames();
    }

    @Override
    public Class<?> getPropertyType(String propertyName) {
        return getMapping(propertyName).getAttributeClassification();
    }

    @Override
    public DynamicEntity newDynamicEntity() {
        DynamicEntity newDynamicEntity = (DynamicEntity)getDescriptor().getInstantiationPolicy().
            buildNewInstance();
        return newDynamicEntity;
    }

    /**
     * Ensure the value being set is supported by the mapping. If the mapping is
     * direct/basic and the mapping's type is primitive ensure the non-primitive
     * type is allowed.
     */
    public void checkSet(String propertyName, Object value) throws DynamicException {
        DatabaseMapping mapping = getMapping(propertyName);
        if (value == null) {
            if (mapping.isCollectionMapping() ||
                (mapping.getAttributeClassification() != null &&
                 mapping.getAttributeClassification().isPrimitive())) {
                throw DynamicException.invalidSetPropertyType(mapping, null);
            }
            return;
        }
        Class<?> expectedType = mapping.getAttributeClassification();
        if (mapping.isForeignReferenceMapping()) {
            if (mapping.isCollectionMapping()) {
                if (((CollectionMapping) mapping).getContainerPolicy().isMapPolicy()) {
                    expectedType = Map.class;
                } else {
                    expectedType = Collection.class;
                }
            } else {
                expectedType = ((ForeignReferenceMapping)mapping).getReferenceClass();
            }
        }
        if (expectedType != null && expectedType.isPrimitive() && !value.getClass().isPrimitive()) {
            expectedType = Helper.getObjectClass(expectedType);
        }
        if (expectedType != null && !expectedType.isAssignableFrom(value.getClass())) {
            throw DynamicException.invalidSetPropertyType(mapping, value);
        }
    }

    @Override
    public int getPropertyIndex(String propertyName) {
        return dpm.getPropertyNames().indexOf(propertyName);
    }

    @Override
    public Class<?> getPropertyType(int propertyIndex) {
        return getDescriptor().getMappings().get(propertyIndex).getAttributeClassification();
    }

    @Override
    public String toString() {
        return "DynamicEntityType(" + getName() + ") - " + getDescriptor();
    }
}
