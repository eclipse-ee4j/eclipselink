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
import org.eclipse.persistence.exceptions.ValidationException;

import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.accessors.DirectAccessor;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.TypeConversionConverter;

/**
 * INTERNAL:
 * Object to hold onto a type converter metadata. 
 * 
 * @author Guy Pelletier
 * @since TopLink 11g
 */
public class TypeConverterMetadata extends AbstractConverterMetadata {
    private Class m_dataType;
    private Class m_objectType;
    
    /**
     * INTERNAL:
     */
    public TypeConverterMetadata() {}
    
    /**
     * INTERNAL:
     */
    public TypeConverterMetadata(TypeConverter typeConverter) {
    	m_dataType = typeConverter.dataType();
    	m_objectType = typeConverter.objectType();
        
        setName(typeConverter.name());
    }
    
    /**
     * INTERNAL:
     */
    public Class getDataType() {
    	return m_dataType;
    }
    
    /**
     * INTERNAL:
     */
    public Class getDataType(DirectAccessor accessor) {
        Class dataType = getDataType();
        
        if (dataType == void.class) {
            dataType = accessor.getReferenceClass();
            
            if (dataType == null) {
            	throw ValidationException.noConverterDataTypeSpecified(accessor.getJavaClass(), accessor.getAttributeName(), getName());
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
    public String getDataTypeName(DirectAccessor accessor) {
        return getDataType(accessor).getName();
    }
    
    /**
     * INTERNAL:
     */
    public Class getObjectType() {
        return m_objectType;
    }
    
    /**
     * INTERNAL:
     */
    public Class getObjectType(DirectAccessor accessor) {
        Class objectType = getObjectType();
        
        if (objectType == void.class) {
            objectType = accessor.getReferenceClass();
            
            if (objectType == null) {
            	throw ValidationException.noConverterObjectTypeSpecified(accessor.getJavaClass(), accessor.getAttributeName(), getName());
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
    public String getObjectTypeName(DirectAccessor accessor) {
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
    
    /**
     * INTERNAL:
     */
    public void setDataType(Class dataType) {
    	m_dataType = dataType;
    }
    
    /**
     * INTERNAL:
     */
    public void setObjectType(Class objectType) {
        m_objectType = objectType;
    }
}
