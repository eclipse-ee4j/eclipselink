/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping files  
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.metadata.accessors.mappings;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.persistence.annotations.JoinFetchType;
import org.eclipse.persistence.annotations.Properties;
import org.eclipse.persistence.annotations.Property;
import org.eclipse.persistence.exceptions.ValidationException;

import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.accessors.AccessMethodsMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.PropertyMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataMethod;
import org.eclipse.persistence.internal.queries.CollectionContainerPolicy;

import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;

/**
 * INTERNAL:
 * An abstract mapping accessor. Holds common metadata for all mappings.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 1.0
 */
public abstract class MappingAccessor extends MetadataAccessor {
    private AccessMethodsMetadata m_accessMethods;
    private Map<String, PropertyMetadata> m_properties = new HashMap<String, PropertyMetadata>();
    
    /**
     * INTERNAL:
     */
    protected MappingAccessor(String xmlElement) {
        super(xmlElement);
    }
    
    /**
     * INTERNAL:
     */
    protected MappingAccessor(Annotation annotation, MetadataAccessibleObject accessibleObject, ClassAccessor classAccessor) {
        super(annotation, accessibleObject, classAccessor.getDescriptor(), classAccessor.getProject());
    }
    
    /**
     * INTERNAL:
     */
    public AccessMethodsMetadata getAccessMethods(){
        return m_accessMethods;
    }
    
    /**
     * INTERNAL:
     * Returns the get method name of a method accessor. Note, this method
     * should not be called when processing field access.
     */
    protected String getGetMethodName() {
        if (m_accessMethods != null && m_accessMethods.getGetMethodName() != null) {
            return m_accessMethods.getGetMethodName();
        }
        
        return getAccessibleObjectName();
    }
    
    /**
     * INTERNAL:
     * Return the mapping join fetch type.
     */
    protected int getMappingJoinFetchType(Enum joinFetchType) {
        if (joinFetchType == null) {
            return ForeignReferenceMapping.NONE;
        } else if (joinFetchType.equals(JoinFetchType.INNER)) {
            return ForeignReferenceMapping.INNER_JOIN;
        }
        
        return ForeignReferenceMapping.OUTER_JOIN;
    }
    
    /**
     * INTERNAL:
     * Return the raw class for this accessor. 
     * Eg. For an accessor with a type of java.util.Collection<Employee>, this 
     * method will return java.util.Collection
     */
    public Class getRawClass() {
        return getAccessibleObject().getRawClass();   
    }
    
    /**
     * INTERNAL: 
     * Return the reference class for this accessor. By default the reference
     * class is the raw class. Some accessors may need to override this
     * method to drill down further. That is, try to extract a reference class
     * from generics.
     */
    public Class getReferenceClass() {
        return getAccessibleObject().getRawClass();
    }
    
    /**
     * INTERNAL:
     * Attempts to return a reference class from a generic specification. Note,
     * this method may return null.
     */
    public Class getReferenceClassFromGeneric() {
        return getAccessibleObject().getReferenceClassFromGeneric();
    }

    /**
     * INTERNAL:
     * Return the reference class name for this accessor.
     */
    public String getReferenceClassName() {
        return getReferenceClass().getName();
    }
    
    /**
     * INTERNAL:
     * Return the reference metadata descriptor for this accessor.
     */
    public MetadataDescriptor getReferenceDescriptor() {
        ClassAccessor accessor = getProject().getAccessor(getReferenceClassName());
        
        if (accessor == null) {
            throw ValidationException.classNotListedInPersistenceUnit(getReferenceClassName());
        }
        
        return accessor.getDescriptor();
    }
    
    /**
     * INTERNAL:
     * Returns the set method name of a method accessor. Note, this method
     * should not be called when processing field access.
     */
    protected String getSetMethodName() {
        if (m_accessMethods != null && m_accessMethods.getSetMethodName() != null) {
            return m_accessMethods.getSetMethodName();
        }

        return ((MetadataMethod) getAccessibleObject()).getSetMethodName();
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject) {
        super.initXMLObject(accessibleObject);
        
        // Initialize single objects.
        initXMLObject(m_accessMethods, accessibleObject);
    }
    
    /**
     * INTERNAL:
     * Adds properties to the mapping.
     */
    protected void processProperties(DatabaseMapping mapping) {
        // If we were loaded from XML use the properties loaded from there
        // only. Otherwise look for annotations.
        if (loadedFromXML()) {
            for (PropertyMetadata property : getProperties()) {
                processProperty(mapping, property);
            }
        } else {
            // Look for annotations.
            Annotation properties = getAnnotation(Properties.class);
            if (properties != null) {
                for (Annotation property : (Annotation[]) MetadataHelper.invokeMethod("value", properties)) {
                    processProperty(mapping, new PropertyMetadata(property, getAccessibleObject()));
                }
            }
            
            Annotation property = getAnnotation(Property.class);
            if (property != null) {
                processProperty(mapping, new PropertyMetadata(property, getAccessibleObject()));
            }    
        }
    }
    
    /**
     * INTERNAL:
     * Adds properties to the mapping. They can only come from one place,
     * therefore is we add the same one twice we know to throw an exception.
     */
    protected void processProperty(DatabaseMapping mapping, PropertyMetadata property) {
        if (property.shouldOverride(m_properties.get(property.getName()))) {
            m_properties.put(property.getName(), property);
            mapping.getProperties().put(property.getName(), property.getConvertedValue());
        }
    }
    
    /**
     * INTERNAL:
     */
    public void setAccessMethods(AccessMethodsMetadata accessMethods){
        m_accessMethods = accessMethods;
    }
    
    /**
     * INTERNAL:
     * Set the getter and setter access methods for this accessor.
     */
    protected void setAccessorMethods(DatabaseMapping mapping) {
        if (usesPropertyAccess(getDescriptor())) {
            mapping.setGetMethodName(getGetMethodName());
            mapping.setSetMethodName(getSetMethodName());
        }
    }
    
    /** 
     * INTERNAL:
     * Set the correct indirection policy on a collection mapping. Method
     * assume that the reference class has been set on the mapping before
     * calling this method.
     */
    public void setIndirectionPolicy(CollectionMapping mapping, String mapKey, boolean usesIndirection) {
        Class rawClass = getRawClass();
        
        if (usesIndirection) {            
            if (rawClass == Map.class) {
                mapping.useTransparentMap(mapKey);
            } else if (rawClass == List.class) {
                mapping.useTransparentList();
            } else if (rawClass == Collection.class) {
                mapping.useTransparentCollection();
                mapping.setContainerPolicy(new CollectionContainerPolicy(ClassConstants.IndirectList_Class));
            } else if (rawClass == Set.class) {
                mapping.useTransparentSet();
            } else {
                //bug221577: This should be supported when a transparent indirection class can be set through eclipseLink_orm.xml, or basic indirection is used
                this.getLogger().logWarningMessage(MetadataLogger.WARNING_INVALID_COLLECTION_USED_ON_LAZY_RELATION, this.getJavaClass(), this.getAnnotatedElement(), rawClass);
            }
        } else {
            mapping.dontUseIndirection();
            
            if (rawClass == Map.class) {
                mapping.useMapClass(java.util.Hashtable.class, mapKey);
            } else if (rawClass == Set.class) {
                mapping.useCollectionClass(java.util.HashSet.class);
            } else if ( (rawClass == List.class) || (rawClass == Collection.class)){
                mapping.useCollectionClass(java.util.Vector.class);
            } else {
                //bug221577: use the supplied collection class type if its not an interface
                if (mapKey == null || mapKey.equals("")){
                    mapping.useCollectionClass(rawClass);
                } else {
                    mapping.useMapClass(rawClass, mapKey);
                }
            }
        }
    }
    
    /**
     * INTERNAL:
     * Returns true if this class uses property access. In an inheritance 
     * hierarchy, the subclasses inherit their access type from the parent.
     * The metadata helper method caches the class access types for 
     * efficiency.
     * @see MetadataDescriptor usesPropertyAccess()
     */
    public boolean usesPropertyAccess(MetadataDescriptor descriptor) {
        if (m_accessMethods == null) {
            return descriptor.usesPropertyAccess();
        }
        
        return true;
    }
    
}
