/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
 *     02/25/2009-2.0 Guy Pelletier 
 *       - 265359: JPA 2.0 Element Collections - Metadata processing portions
 *     03/27/2009-2.0 Guy Pelletier 
 *       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
 *     04/27/2010-2.1 Guy Pelletier 
 *       - 309856: MappedSuperclasses from XML are not being initialized properly
 *     03/24/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 1)
 *     25/05/2012-2.4 Guy Pelletier  
 *       - 354678: Temp classloader is still being used during metadata processing
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.converters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.eclipse.persistence.exceptions.ValidationException;

import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.MappingAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.EnumTypeConverter;

/**
 * INTERNAL:
 * Object to hold onto an object type converter metadata.
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
public class ObjectTypeConverterMetadata extends TypeConverterMetadata {
    private List<ConversionValueMetadata> m_conversionValues = new ArrayList<ConversionValueMetadata>();
    private String m_defaultObjectValue;
    
    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public ObjectTypeConverterMetadata() {
        super("<object-type-converter>");
    }
    
    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public ObjectTypeConverterMetadata(MetadataAnnotation objectTypeConverter, MetadataAccessor accessor) {
        super(objectTypeConverter, accessor);
        
        for (Object conversionValue: (Object[]) objectTypeConverter.getAttributeArray("conversionValues")) {
            m_conversionValues.add(new ConversionValueMetadata((MetadataAnnotation)conversionValue, accessor));
        }
        
        m_defaultObjectValue = (String) objectTypeConverter.getAttribute("defaultObjectValue"); 
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (super.equals(objectToCompare) && objectToCompare instanceof ObjectTypeConverterMetadata) {
            ObjectTypeConverterMetadata objectTypeConverter = (ObjectTypeConverterMetadata) objectToCompare;
            
            if (! valuesMatch(m_conversionValues, objectTypeConverter.getConversionValues())) {
                return false;
            }
            
            return valuesMatch(m_defaultObjectValue, objectTypeConverter.getDefaultObjectValue());
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<ConversionValueMetadata> getConversionValues() {
        return m_conversionValues;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getDefaultObjectValue() {
        return m_defaultObjectValue;
    }
    
    /**
     * INTERNAL:
     */
    public boolean hasConversionValues() {
        return ! m_conversionValues.isEmpty();
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public void process(DatabaseMapping mapping, MappingAccessor accessor, MetadataClass referenceClass, boolean isForMapKey) {
        org.eclipse.persistence.mappings.converters.ObjectTypeConverter converter;
        MetadataClass dataType = getDataType(accessor, referenceClass);
        MetadataClass objectType = getObjectType(accessor, referenceClass);
        
        if (objectType.isEnum()) {
            // Create an EnumTypeConverter. 
            converter = new EnumTypeConverter(mapping, objectType.getName());
            
            // The object values should be the names of the enum members so 
            // force the objectType to String to ensure the initObject calls 
            // below will work.
            objectType = getMetadataFactory().getMetadataClass(String.class.getName());
        } else {
            // Create an ObjectTypeConverter.
            converter = new org.eclipse.persistence.mappings.converters.ObjectTypeConverter(mapping);
        }
        
        // Set the converter name (for better exception handling)
        converter.setConverterName(getName());
        
        // Set the type names on the converter.
        converter.setDataTypeName(getJavaClassName(dataType));
        converter.setObjectTypeName(getJavaClassName(objectType));
        
        // Process the conversion values.
        // Hold two-way mappings from the database to the object.
        Map<String, String> dataToObjectValues = new HashMap<String, String>();
        // If a member from m_dataToObjectValues is not in m_objectToDataValues
        // then it will be assumed that it is a one-way mapping.
        Map<String, String> objectToDataValues = new HashMap<String, String>();
   
        if (hasConversionValues()) {
            for (ConversionValueMetadata conversionValue: getConversionValues()) {
                String dataValue = conversionValue.getDataValue();
                String objectValue = conversionValue.getObjectValue();
            
                if (dataToObjectValues.containsKey(dataValue)) {
                    throw ValidationException.multipleObjectValuesForDataValue(accessor.getJavaClass(), getName(), dataValue);
                } else {
                    dataToObjectValues.put(dataValue, objectValue);
                
                    // Only add it if it is a two-way mapping.
                    if (! objectToDataValues.containsKey(objectValue)) {
                        objectToDataValues.put(objectValue, dataValue);
                    }
                }
            }
        }
        
        // Process the data to object mappings. The object and data values
        // should be primitive wrapper types so we can initialize the 
        // conversion values now.
        for (String dataValue : dataToObjectValues.keySet()) {
            String objectValue = dataToObjectValues.get(dataValue);
            
            if (objectToDataValues.containsKey(objectValue)) {
                // It's a two-way mapping ...
                converter.addConversionValueStrings(dataValue, objectValue);
            } else {
                // It's a one-way mapping ...
                converter.addToAttributeOnlyConversionValueStrings(dataValue, objectValue);
            }
        }
        
        // Process the defaultObjectValue if one is specified.
        if (m_defaultObjectValue != null && ! m_defaultObjectValue.equals("")) {
            converter.setDefaultAttributeValueString(m_defaultObjectValue);
        }
        
        // Set the converter on the mapping.
        setConverter(mapping, converter, isForMapKey);
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setConversionValues(List<ConversionValueMetadata> conversionValues) {
        m_conversionValues = conversionValues;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setDefaultObjectValue(String defaultObjectValue) {
        m_defaultObjectValue = defaultObjectValue;
    }
}
