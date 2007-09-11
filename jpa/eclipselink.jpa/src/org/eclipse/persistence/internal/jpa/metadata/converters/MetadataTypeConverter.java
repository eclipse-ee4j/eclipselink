/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.converters;

import org.eclipse.persistence.annotations.TypeConverter;

import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.accessors.DirectAccessor;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.TypeConversionConverter;

/**
 * Object to hold onto a type converter metadata. 
 * 
 * @author Guy Pelletier
 * @since TopLink 11g
 */
public class MetadataTypeConverter extends MetadataAbstractTypeConverter {
    private TypeConverter m_typeConverter;
    
    /**
     * INTERNAL:
     */
    protected MetadataTypeConverter() {}
    
    /**
     * INTERNAL:
     */
    public MetadataTypeConverter(TypeConverter typeConverter) {
        m_typeConverter = typeConverter;
    }
    
    /**
     * INTERNAL: (Overridden in MetadataObjectTypeConverter)
     */
    protected Class getDataType() {
        return m_typeConverter.dataType();
    }
    
    /**
     * INTERNAL:
     */
    protected Class getDataType(DirectAccessor accessor) {
        Class dataType = getDataType();
        
        if (dataType == void.class) {
            dataType = accessor.getReferenceClass();
            
            if (dataType == null) {
                // Throw an exception.
                accessor.getValidator().throwNoConverterDataTypeSpecified(accessor.getJavaClass(), accessor.getAttributeName(), getName());
            } else {
                // Log the defaulting data type.
                accessor.getLogger().logConfigMessage(MetadataLogger.CONVERTER_DATA_TYPE, accessor, getName(), dataType);
            }
        }
        
        return dataType;
    }
    
    /**
     * INTERNAL:
     */
    protected String getDataTypeName(DirectAccessor accessor) {
        return getDataType(accessor).getName();
    }
    
    /**
     * INTERNAL: (Overridden in MetadataObjectTypeConverter)
     */
    public String getName() {
        return m_typeConverter.name();
    }
    
    /**
     * INTERNAL: (Overridden in MetadataObjectTypeConverter)
     */
    protected Class getObjectType() {
        return m_typeConverter.objectType();
    }
    
    /**
     * INTERNAL:
     */
    protected Class getObjectType(DirectAccessor accessor) {
        Class objectType = getObjectType();
        
        if (objectType == void.class) {
            objectType = accessor.getReferenceClass();
            
            if (objectType == null) {
                // Throw an exception.
                accessor.getValidator().throwNoConverterObjectTypeSpecified(accessor.getJavaClass(), accessor.getAttributeName(), getName());
            } else {
                // Log the defaulting object type name.
                accessor.getLogger().logConfigMessage(MetadataLogger.CONVERTER_OBJECT_TYPE, accessor, getName(), objectType);
            }
        }
        
        return objectType;
    }
    
    /**
     * INTERNAL:
     */
    protected String getObjectTypeName(DirectAccessor accessor) {
        return getObjectType(accessor).getName();
    }
    
    /**
     * INTERNAL: (Overridden in MetadataObjectTypeConverter)
     */
    public void process(DatabaseMapping mapping, DirectAccessor accessor) {
        TypeConversionConverter converter = new TypeConversionConverter(mapping);
        
        // Process the data type and set the data class name.
        converter.setDataClassName(getDataTypeName(accessor));
        
        // Process the object type and set the object class name.
        converter.setObjectClassName(getObjectTypeName(accessor));
        
        // Set the converter on the mapping.
        accessor.setConverter(mapping, converter);
        
        // Set the field classification.
        accessor.setFieldClassification(mapping, getDataType());
    }
}
