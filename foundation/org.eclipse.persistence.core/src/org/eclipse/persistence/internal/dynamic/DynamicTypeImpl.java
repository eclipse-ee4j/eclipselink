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
 *     dclarke - Dynamic Persistence INCUBATION - Enhancement 200045
 *     			 http://wiki.eclipse.org/EclipseLink/Development/Dynamic
 *     
 * This code is being developed under INCUBATION and is not currently included 
 * in the automated EclipseLink build. The API in this code may change, or 
 * may never be included in the product. Please provide feedback through mailing 
 * lists or the bug database.
 ******************************************************************************/
package org.eclipse.persistence.internal.dynamic;

//javase imports
import java.util.AbstractList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//EclipseLink imports
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.exceptions.DynamicException;
import org.eclipse.persistence.mappings.DatabaseMapping;

/**
 * An EntityType provides a metadata facade into the EclipseLink
 * object-relational metadata (descriptors & mappings) with specific knowledge
 * of the entity types being dynamic.
 * 
 * @author dclarke, mnorman
 * @since EclipseLink 1.2
 */
public class DynamicTypeImpl implements DynamicType {

    protected ClassDescriptor descriptor;

    protected List<String> propertyNames = new PropertyNameList();
    
    protected DynamicType parentType;

    /**
     * These properties require initialization when a new instance is created.
     * This includes properties that are primitives as well as relationships
     * requiring indirection ValueHolders or collections.
     */
    protected Set<DatabaseMapping> mappingsRequiringInitialization = new HashSet<DatabaseMapping>();

    /**
     * Creation of an EntityTypeImpl for an existing Descriptor with mappings.
     * 
     * @param descriptor
     */
    public DynamicTypeImpl(ClassDescriptor descriptor, DynamicType parentType) {
        this.descriptor = descriptor;
        this.parentType = parentType;
    }

    public ClassDescriptor getDescriptor() {
        return this.descriptor;
    }

    public DynamicType getParentType() {
        return this.parentType;
    }
    
    public List<DatabaseMapping> getMappings() {
        return getDescriptor().getMappings();
    }

    /**
     * @see DynamicType#getName()
     */
    public String getName() {
        return getDescriptor().getAlias();
    }

    public String getClassName() {
        return getDescriptor().getJavaClassName();
    }

    public int getNumberOfProperties() {
        return getMappings().size();
    }

    public Set<DatabaseMapping> getMappingsRequiringInitialization() {
        return this.mappingsRequiringInitialization;
    }


    public boolean isInitialized() {
        return getDescriptor().isFullyInitialized();
    }

    public boolean containsProperty(String propertyName) {
        return getPropertiesNames().contains(propertyName);
    }

    public Class<?> getJavaClass() {
        return getDescriptor().getJavaClass();
    }

    public DatabaseMapping getMapping(String propertyName) {
        DatabaseMapping mapping = getDescriptor().getMappingForAttributeName(propertyName);

        if (mapping == null) {
            throw DynamicException.invalidPropertyName(this, propertyName);
        }

        return mapping;
    }

    public DatabaseMapping getMapping(int propertyIndex) {
        if (propertyIndex < 0 || propertyIndex >= getMappings().size()) {
            throw DynamicException.invalidPropertyIndex(this, propertyIndex);
        }

        DatabaseMapping mapping = getMappings().get(propertyIndex);

        return mapping;
    }

    public List<String> getPropertiesNames() {
        return this.propertyNames;
    }

    public int getPropertyIndex(String propertyName) {
        return getMappings().indexOf(getMapping(propertyName));
    }

    public Class<?> getPropertyType(int propertyIndex) {
        return getMapping(propertyIndex).getAttributeClassification();
    }

    public Class<?> getPropertyType(String propertyName) {
        return getMapping(propertyName).getAttributeClassification();
    }

    public DynamicEntity newDynamicEntity() {
        return (DynamicEntity) getDescriptor().getInstantiationPolicy().buildNewInstance();
    }

    public String toString() {
        return "EntityType(" + getName() + ") - " + getDescriptor();
    }

    /**
     * Simple AbstractList to dynamically provide read-only access to the
     * property names leveraging the descriptor's mappings. This list will allow
     * users to access the properties cleanly through the meta-model approach of
     * asking a type for its properties
     */
    private class PropertyNameList extends AbstractList<String> {

        public String get(int index) {
            return DynamicTypeImpl.this.getMapping(index).getAttributeName();
        }

        public int size() {
            return DynamicTypeImpl.this.getNumberOfProperties();
        }

    }
}
