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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.converters;

import java.lang.reflect.AnnotatedElement;

import org.eclipse.persistence.exceptions.ValidationException;

import org.eclipse.persistence.internal.jpa.metadata.MetadataHelper;
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
    private String m_dataTypeName;
    private String m_objectTypeName;
    
    /**
     * INTERNAL:
     */
    public TypeConverterMetadata() {
    	setLoadedFromXML();
    }
    
    /**
     * INTERNAL:
     */
    public TypeConverterMetadata(Object typeConverter, AnnotatedElement annotatedElement) {
    	setLoadedFromAnnotation();
    	setLocation(annotatedElement.toString());
    	
    	setName((String)org.eclipse.persistence.internal.jpa.metadata.converters.MetadataHelper.invokeMethod("name", typeConverter));
    	
    	m_dataType = (Class)org.eclipse.persistence.internal.jpa.metadata.converters.MetadataHelper.invokeMethod("dataType", typeConverter); 
    	m_objectType = (Class)org.eclipse.persistence.internal.jpa.metadata.converters.MetadataHelper.invokeMethod("objectType", typeConverter); 
    }
    
    /**
     * INTERNAL:
     */
    public boolean equals(Object objectToCompare) {
    	if (objectToCompare instanceof TypeConverterMetadata) {
    		TypeConverterMetadata typeConverter = (TypeConverterMetadata) objectToCompare;
    		
    		if (! MetadataHelper.valuesMatch(getName(), typeConverter.getName())) {
    			return false;
    		}
    		
    		if (! MetadataHelper.valuesMatch(m_dataType, typeConverter.getDataType())) {
    			return false;
    		}
    		
    		return MetadataHelper.valuesMatch(m_objectType, typeConverter.getObjectType());
    	}
    	
    	return false;
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
        if (m_dataType == void.class) {
            Class dataType = accessor.getReferenceClass();
            
            if (dataType == null) {
            	throw ValidationException.noConverterDataTypeSpecified(accessor.getJavaClass(), accessor.getAttributeName(), getName());
            } else {
                // Log the defaulting data type.
                accessor.getLogger().logConfigMessage(MetadataLogger.CONVERTER_DATA_TYPE, accessor, getName(), dataType);
            }
            
            return dataType;
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
    public Class getObjectType(DirectAccessor accessor) {
        if (m_objectType == void.class) {
            Class objectType = accessor.getReferenceClass();
            
            if (objectType == null) {
            	throw ValidationException.noConverterObjectTypeSpecified(accessor.getJavaClass(), accessor.getAttributeName(), getName());
            } else {
                // Log the defaulting object type name.
                accessor.getLogger().logConfigMessage(MetadataLogger.CONVERTER_OBJECT_TYPE, accessor, getName(), objectType);
            }
            
            return objectType;
        } else {
        	return m_objectType;
        }
    }
    
    /**
     * INTERNAL:
     */
    public Class getObjectType() {
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
     * INTERNAL: (Overridden in ObjectTypeConverterMetadata)
     */
    public void process(DatabaseMapping mapping, DirectAccessor accessor) {
        TypeConversionConverter converter = new TypeConversionConverter(mapping);
        
        // Process the data type and set the data class name.
        converter.setDataClassName(getDataType(accessor).getName());
        
        // Process the object type and set the object class name.
        converter.setObjectClassName(getObjectType(accessor).getName());
        
        // Set the converter on the mapping.
        accessor.setConverter(mapping, converter);
        
        // Set the field classification.
        accessor.setFieldClassification(mapping, m_dataType);
    }
    
    /**
     * INTERNAL:
     */
    public void setDataType(Class dataType) {
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
    public void setObjectType(Class objectType) {
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
