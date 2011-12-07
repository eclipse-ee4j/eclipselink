/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping files
 *     03/27/2009-2.0 Guy Pelletier 
 *       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
 *     04/27/2010-2.1 Guy Pelletier 
 *       - 309856: MappedSuperclasses from XML are not being initialized properly
 *     03/24/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 1)
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.converters;

import org.eclipse.persistence.exceptions.ValidationException;

import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.MappingAccessor;

import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;

import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.TypeConversionConverter;

/**
 * INTERNAL:
 * Object to hold onto a type converter metadata. 
 * 
 * Key notes:
 * - any metadata mapped from XML to this class must be compared in the
 *   equals method.
 * - when loading from annotations, the constructor accepts the metadata
 *   accessor this metadata was loaded from. Used it to look up any 
 *   'companion' annotation needed for processing.
 * - methods should be preserved in alphabetical order.
 * 
 * @author Guy Pelletier
 * @since TopLink 11g
 */
public class TypeConverterMetadata extends AbstractConverterMetadata {
    private MetadataClass m_dataType;
    private MetadataClass m_objectType;
    private String m_dataTypeName;
    private String m_objectTypeName;
    
    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public TypeConverterMetadata() {
        super("<type-converter>");
    }

    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public TypeConverterMetadata(MetadataAnnotation typeConverter, MetadataAccessor accessor) {
        super(typeConverter, accessor);
        
        m_dataType = getMetadataClass((String)typeConverter.getAttributeString("dataType")); 
        m_objectType = getMetadataClass((String)typeConverter.getAttributeString("objectType")); 
    }
    
    /**
     * INTERNAL:
     * Used for XML loading.
     */
    protected TypeConverterMetadata(String xmlElement) {
        super(xmlElement);
    }
    
    /**
     * INTERNAL:
     * For merging and overriding to work properly, all ORMetadata must be able 
     * to compare themselves for metadata equality.
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (super.equals(objectToCompare) && objectToCompare instanceof TypeConverterMetadata) {
            TypeConverterMetadata typeConverter = (TypeConverterMetadata) objectToCompare;
            
            if (! valuesMatch(getName(), typeConverter.getName())) {
                return false;
            }
            
            if (! valuesMatch(m_dataTypeName, typeConverter.getDataTypeName())) {
                return false;
            }
            
            return valuesMatch(m_objectTypeName, typeConverter.getObjectTypeName());
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     */
    public MetadataClass getDataType() {
        return m_dataType;
    }
    
    /**
     * INTERNAL:
     */
    public MetadataClass getDataType(MappingAccessor accessor, MetadataClass referenceClass) {
        if (m_dataType.isVoid()) {
            if (referenceClass == null) {
                throw ValidationException.noConverterDataTypeSpecified(accessor.getJavaClass(), accessor.getAttributeName(), getName());
            } else {
                // Log the defaulting data type.
                accessor.getLogger().logConfigMessage(MetadataLogger.CONVERTER_DATA_TYPE, accessor, getName(), referenceClass);
            }
            
            return referenceClass;
        } else {
            return m_dataType;
        }
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getDataTypeName() {
        return m_dataTypeName;
    }
    
    /**
     * INTERNAL:
     */
    public MetadataClass getObjectType(MappingAccessor accessor, MetadataClass referenceClass) {
        if (m_objectType.isVoid()) {
            if (referenceClass == null) {
                throw ValidationException.noConverterObjectTypeSpecified(accessor.getJavaClass(), accessor.getAttributeName(), getName());
            } else {
                // Log the defaulting object type name.
                accessor.getLogger().logConfigMessage(MetadataLogger.CONVERTER_OBJECT_TYPE, accessor, getName(), referenceClass);
            }
            
            return referenceClass;
        } else {
            return m_objectType;
        }
    }
    
    /**
     * INTERNAL:
     */
    public MetadataClass getObjectType() {
        return m_objectType;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getObjectTypeName() {
        return m_objectTypeName;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);
        
        m_dataType = initXMLClassName(m_dataTypeName);
        m_objectType = initXMLClassName(m_objectTypeName);
    }
    
    /**
     * INTERNAL:
     */
    public void process(DatabaseMapping mapping, MappingAccessor accessor, MetadataClass referenceClass, boolean isForMapKey) {
        TypeConversionConverter converter = new TypeConversionConverter(mapping);
        
        // Process the data type and set the data class name.
        converter.setDataClassName(getDataType(accessor, referenceClass).getName());
        
        // Process the object type and set the object class name.
        converter.setObjectClassName(getObjectType(accessor, referenceClass).getName());
        
        // Set the converter on the mapping.
        setConverter(mapping, converter, isForMapKey);
        
        // Set the field classification.
        setFieldClassification(mapping, getJavaClass(m_dataType), isForMapKey);
    }
    
    /**
     * INTERNAL:
     */
    public void setDataType(MetadataClass dataType) {
        m_dataType = dataType;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setDataTypeName(String dataTypeName) {
        m_dataTypeName = dataTypeName;
    }
    
    /**
     * INTERNAL:
     */
    public void setObjectType(MetadataClass objectType) {
        m_objectType = objectType;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setObjectTypeName(String objectTypeName) {
        m_objectTypeName = objectTypeName;
    }
}
