/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.metadata.structures;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EmbeddableAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.DirectAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.jpa.metadata.columns.ColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.mappings.structures.ArrayMapping;
import org.eclipse.persistence.mappings.structures.ObjectArrayMapping;
import org.eclipse.persistence.mappings.structures.ObjectRelationalDatabaseField;

import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_COLUMN;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_FETCH_EAGER;

/**
 * An array accessor.
 * Used to support ArrayMapping and ObjectArrayMapping.
 * 
 * @author James Sutherland
 * @since EclipseLink 2.3
 */
public class ArrayAccessor extends DirectAccessor {
    private String m_databaseType;
    private ColumnMetadata m_column;
    private MetadataClass m_referenceClass;
    private MetadataClass m_targetClass;
    private String m_targetClassName;
    
    /**
     * Used for OX mapping.
     */
    public ArrayAccessor() {
        super("<array>");
    }
    
    /**
     * Used for annotations.
     */
    public ArrayAccessor(MetadataAnnotation array, MetadataAccessibleObject accessibleObject, ClassAccessor classAccessor) {
        super(array, accessibleObject, classAccessor);
        
        m_targetClass = getMetadataClass((String) array.getAttribute("targetClass"));        
        m_databaseType = (String) array.getAttribute("databaseType");
                
        // Set the column if one if defined.
        if (isAnnotationPresent(JPA_COLUMN)) {
            m_column = new ColumnMetadata(getAnnotation(JPA_COLUMN), this);
        }
    }

    @Override
    public boolean equals(Object objectToCompare) {
        if (super.equals(objectToCompare) && objectToCompare instanceof ArrayAccessor) {
            ArrayAccessor accessor = (ArrayAccessor) objectToCompare;
            
            if (! valuesMatch(m_column, accessor.getColumn())) {
                return false;
            }
            if (! valuesMatch(m_databaseType, accessor.getDatabaseType())) {
                return false;
            }
            
            return (! valuesMatch(m_targetClassName, accessor.getTargetClassName()));
        }
        
        return false;
    }

    public ColumnMetadata getColumn() {
        return m_column;
    }

    @Override
    protected ColumnMetadata getColumn(String loggingCtx) {
        return m_column == null ? super.getColumn(loggingCtx) : m_column;
    }

    public EmbeddableAccessor getEmbeddableAccessor() {
        return getProject().getEmbeddableAccessor(getReferenceClass());
    }
    
    /**
     * If a targetClass is specified in metadata, it will be set as the 
     * reference class, otherwise we will look to extract one from generics.
     * <p>
     * MappedSuperclass descriptors return Void when their parameterized generic reference class is null
     */
    @Override
    public MetadataClass getReferenceClass() {
        if (m_referenceClass == null) {
            m_referenceClass = getTargetClass();
        
            if (m_referenceClass == null || m_referenceClass.isVoid()) {
                // This call will attempt to extract the reference class from generics.
                m_referenceClass = getReferenceClassFromGeneric();
        
                if (m_referenceClass == null) {
                    // 266912: We do not handle the resolution of parameterized 
                    // generic types when the accessor is a MappedSuperclasses.
                    // the validation exception is relaxed in this case and
                    // void metadata class is returned.
                    if (getClassAccessor().isMappedSuperclass()) {
                        return getMetadataClass(Void.class);
                    }
                    
                    // Throw an exception. An element collection accessor must 
                    // have a reference class either through generics or a 
                    // specified target class on the mapping metadata.
                    throw ValidationException.unableToDetermineTargetClass(getAttributeName(), getJavaClass());
                }
            }
        }
            
        return m_referenceClass;
    }
    
    /**
     * In an element collection case, when the collection is not an embeddable
     * collection, there is no notion of a reference descriptor, therefore we
     * return this accessors descriptor
     */
    @Override
    public MetadataDescriptor getReferenceDescriptor() {
        if (isDirectEmbeddableCollection()) {
            return getEmbeddableAccessor().getDescriptor();
        } else {
            return super.getReferenceDescriptor();
        }
    }
    
    /**
     * Return the target class for this accessor.
     */
    protected MetadataClass getTargetClass() {
        return m_targetClass;
    }

    protected String getTargetClassName() {
        return m_targetClassName;
    }

    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);        
        
        // Initialize single objects.
        initXMLObject(m_column, accessibleObject);
        
        // Initialize the any class names we read from XML.
        m_targetClass = initXMLClassName(m_targetClassName);
    }
    
    /**
     * Return true if this element collection contains embeddable objects.
     */
    @Override
    public boolean isDirectEmbeddableCollection() {
        return getEmbeddableAccessor() != null;
    }
    
    /**
     * Process the ArrayMapping or ObjectArrayMapping.
     */
    @Override
    public void process() {
        if (isDirectEmbeddableCollection()) {
            ObjectArrayMapping mapping = new ObjectArrayMapping();
            
            // Add the mapping to the descriptor.
            setMapping(mapping);
            
            // Set the reference class name.
            mapping.setReferenceClassName(getReferenceClassName());
    
            // Set the attribute name.
            mapping.setAttributeName(getAttributeName());
            
            // Will check for PROPERTY access
            setAccessorMethods(mapping);
            
            mapping.setStructureName(getDatabaseType());
            
            // Process the @Column or column element if there is one.
            // A number of methods depend on this field so it must be
            // initialized before any further processing can take place.
            mapping.setField(new ObjectRelationalDatabaseField(getDatabaseField(getDescriptor().getPrimaryTable(), MetadataLogger.COLUMN)));
        } else {
            ArrayMapping mapping = new ArrayMapping();
            
            // Add the mapping to the descriptor.
            setMapping(mapping);
    
            // Set the attribute name.
            mapping.setAttributeName(getAttributeName());
            
            // Will check for PROPERTY access
            setAccessorMethods(mapping);
            
            mapping.setStructureName(getDatabaseType());
            
            // Process the @Column or column element if there is one.
            // A number of methods depend on this field so it must be
            // initialized before any further processing can take place.
            mapping.setField(new ObjectRelationalDatabaseField(getDatabaseField(getDescriptor().getPrimaryTable(), MetadataLogger.COLUMN)));
        }
    }

    public void setColumn(ColumnMetadata column) {
        m_column = column;
    }

    public void setTargetClassName(String targetClassName) {
        m_targetClassName = targetClassName;
    }

    @Override
    public String getDefaultFetchType() {
        return JPA_FETCH_EAGER; 
    }

    public String getDatabaseType() {
        return m_databaseType;
    }

    public void setDatabaseType(String databaseType) {
        m_databaseType = databaseType;
    }
}
