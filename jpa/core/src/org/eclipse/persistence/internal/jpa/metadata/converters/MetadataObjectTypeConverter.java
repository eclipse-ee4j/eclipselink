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

import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.PrivilegedActionException;

import java.util.Map;
import java.util.HashMap;

import org.eclipse.persistence.annotations.ConversionValue;
import org.eclipse.persistence.annotations.ObjectTypeConverter;

import org.eclipse.persistence.internal.jpa.metadata.MetadataValidator;
import org.eclipse.persistence.internal.jpa.metadata.accessors.DirectAccessor;

import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedGetConstructorFor;
import org.eclipse.persistence.internal.security.PrivilegedInvokeConstructor;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.EnumTypeConverter;

/**
 * Object to hold onto an object type converter metadata.
 * 
 * @author Guy Pelletier
 * @since TopLink 11g
 */
public class MetadataObjectTypeConverter extends MetadataTypeConverter {
    private ObjectTypeConverter m_objectTypeConverter;
    
    // Hold two-way mappings from the database to the object.
    private Map<String, String> m_dataToObjectValues = new HashMap<String, String>();
    // If a member from m_dataToObjectValues is not in m_objectToDataValues
    // then it will be assumed that it is a ono-way mapping.
    private Map<String, String> m_objectToDataValues = new HashMap<String, String>();
    
    /**
     * INTERNAL:
     * Constructor used from XMLObjectTypeConverter.
     */
    protected MetadataObjectTypeConverter() {}
    
    /**
     * INTERNAL:
     */
    public MetadataObjectTypeConverter(ObjectTypeConverter objectTypeConverter, MetadataValidator validator, Class javaClass) {
        m_objectTypeConverter = objectTypeConverter;
        
        for (ConversionValue conversionValue: objectTypeConverter.conversionValues()) {
            String dataValue = conversionValue.dataValue();
            String objectValue = conversionValue.objectValue();
            
            if (m_dataToObjectValues.containsKey(dataValue)) {
                validator.throwMultipleObjectValuesForDataValue(javaClass, getName(), dataValue);
            } else {
                m_dataToObjectValues.put(dataValue, objectValue);
                
                // Only add it if it is a two-way mapping.
                if (! m_objectToDataValues.containsKey(objectValue)) {
                    m_objectToDataValues.put(objectValue, dataValue);
                }
            }
        }
    }
    
    /**
     * INTERNAL: (Override from MetadataTypeConverter)
     */
    public Class getDataType() {
        return m_objectTypeConverter.dataType();
    }
    
    /**
     * INTERNAL:
     */
    public String getDefaultObjectValue() {
        return m_objectTypeConverter.defaultObjectValue();
    }
    
    /**
     * INTERNAL: (Override from MetadataTypeConverter)
     */
    public String getName() {
        return m_objectTypeConverter.name();
    }
    
    /**
     * INTERNAL: (Override from MetadataTypeConverter)
     */
    public Class getObjectType() {
        return m_objectTypeConverter.objectType();
    }
    
    /**
     * INTERNAL:
     */    
    private Object initObject(Class type, String value, DirectAccessor accessor, boolean isData) {
        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
            try {
                Constructor constructor = (Constructor) AccessController.doPrivileged(new PrivilegedGetConstructorFor(type, new Class[] {String.class}, false));
                return AccessController.doPrivileged(new PrivilegedInvokeConstructor(constructor, new Object[] {value}));
            } catch (PrivilegedActionException exception) {
                throwInitObjectException(exception, type, value, accessor, isData);
            }
        } else {
            try {
                Constructor constructor = PrivilegedAccessHelper.getConstructorFor(type, new Class[] {String.class}, false);
                return PrivilegedAccessHelper.invokeConstructor(constructor, new Object[] {value});
            } catch (Exception exception) {
                throwInitObjectException(exception, type, value, accessor, isData);
            }
        }
        
        return null; // keep compiler happy, will never hit.
    }
    
    /**
     * INTERNAL:
     */
    public void process(DatabaseMapping mapping, DirectAccessor accessor) {
        org.eclipse.persistence.mappings.converters.ObjectTypeConverter converter;
        Class dataType = getDataType(accessor);
        Class objectType = getObjectType(accessor);
        
        if (objectType.isEnum()) {
            // Create an EnumTypeConverter. 
            converter = new EnumTypeConverter(mapping, objectType.getName());
            
            // The object values should be the names of the enum members so 
            // force the objectType to String to ensure the initObject calls 
            // below will work.
            objectType = String.class;
        } else {
            // Create an ObjectTypeConverter.
            converter = new org.eclipse.persistence.mappings.converters.ObjectTypeConverter(mapping);
        }
        
        // Process the data to object mappings. The object and data values
        // should be primitive wrapper types so we can initialize the 
        // conversion values now.
        for (String dataValue : m_dataToObjectValues.keySet()) {
            String objectValue = m_dataToObjectValues.get(dataValue);
            
            Object data = initObject(dataType, dataValue, accessor, true);
            Object object = initObject(objectType, objectValue, accessor, false);
            
            if (m_objectToDataValues.containsKey(objectValue)) {
                // It's a two-way mapping ...
                converter.addConversionValue(data, object);
            } else {
                // It's a one-way mapping ...
                converter.addToAttributeOnlyConversionValue(data, object);
            }
        }
        
        // Process the defaultObjectValue if one is specified.
        if (! getDefaultObjectValue().equals("")) {
            converter.setDefaultAttributeValue(initObject(objectType, getDefaultObjectValue(), accessor, false));
        }
        
        // Set the converter on the mapping.
        accessor.setConverter(mapping, converter);
    }
    
    /**
     * INTERNAL:
     */    
    private void throwInitObjectException(Exception exception, Class type, String value, DirectAccessor accessor, boolean isData) {
        if (isData) {
            accessor.getValidator().throwErrorInstantiatingConversionValueData(getName(), value, type, exception);
        } else {
            accessor.getValidator().throwErrorInstantiatingConversionValueObject(getName(), value, type, exception);
        }
    }
}
