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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.mappings.converters;

import java.util.*;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.descriptors.TypeMapping;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.mappings.foundation.AbstractDirectMapping;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * <b>Purpose</b>: Object type converter is used to match a fixed number of database data values
 * to Java object value. It can be used when the values on the database and in the Java differ.
 * To create an object type converter, simply specify the set of conversion value pairs.
 * A default value and one-way conversion are also supported for legacy data situations.
 *
 * @author James Sutherland
 * @since Toplink 10
 */
public class ObjectTypeConverter implements Converter {
    protected DatabaseMapping mapping;
    protected transient Map fieldToAttributeValues;
    protected Map attributeToFieldValues;
    protected transient Object defaultAttributeValue;
    protected transient Class fieldClassification;
    protected transient String fieldClassificationName;

    /**
     * PUBLIC:
     * Default constructor.
     */
    public ObjectTypeConverter() {
        this.attributeToFieldValues = new HashMap(10);
        this.fieldToAttributeValues = new HashMap(10);
    }

    /**
     * PUBLIC:
     * Default constructor.
     */
    public ObjectTypeConverter(DatabaseMapping mapping) {
        this();
        this.mapping = mapping;
    }

    /**
     * PUBLIC:
     * A type conversion value is a two-way mapping from the database to the object.
     * The database value will be substituted for the object value when read,
     * and the object value will be substituted for database value when written.
     * Note that each field/attribute value must have one and only one attribute/field value to maintain a two-way mapping.
     */
    public void addConversionValue(Object fieldValue, Object attributeValue) {
        if (fieldValue == null) {
            fieldValue = Helper.NULL_VALUE;
        }

        if (attributeValue == null) {
            attributeValue = Helper.NULL_VALUE;
        }

        getFieldToAttributeValues().put(fieldValue, attributeValue);
        getAttributeToFieldValues().put(attributeValue, fieldValue);
    }

    /**
     * PUBLIC:
     * An attribute only conversion value is a one-way mapping from the database to the object.
     * This can be used if multiple database values are desired to be mapped to the same object value.
     * Note that when written only the default value will be used for the attribute, not this value.
     */
    public void addToAttributeOnlyConversionValue(Object fieldValue, Object attributeValue) {
        if (fieldValue == null) {
            fieldValue = Helper.NULL_VALUE;
        }

        if (attributeValue == null) {
            attributeValue = Helper.NULL_VALUE;
        }

        getFieldToAttributeValues().put(fieldValue, attributeValue);
    }

    /**
     * INTERNAL:
     * Get the attribute to field mapping.
     */
    public Map getAttributeToFieldValues() {
        return attributeToFieldValues;
    }

    /**
     * INTERNAL:
     * Convert all the class-name-based settings in this converter to actual 
     * class-based settings. This method is used when converting a project 
     * that has been built with class names to a project with classes.
     * @param classLoader 
     */
    public void convertClassNamesToClasses(ClassLoader classLoader){
        // Does nothing right now but was implemented since EnumTypeConverter
        // is dependent on this method but we need to avoid JDK 1.5 
        // dependencies. AbstractDirectMapping will call this method.
    }
    
    /**
     * INTERNAL:
     * Returns the corresponding attribute value for the specified field value.
     */
    public Object convertDataValueToObjectValue(Object fieldValue, Session session) {
        Object attributeValue = null;

        if (fieldValue == null) {
            attributeValue = getFieldToAttributeValues().get(Helper.NULL_VALUE);
        } else {
            try {
                fieldValue = ((AbstractSession)session).getDatasourcePlatform().getConversionManager().convertObject(fieldValue, getFieldClassification());
            } catch (ConversionException e) {
                throw ConversionException.couldNotBeConverted(mapping, mapping.getDescriptor(), e);
            }

            attributeValue = getFieldToAttributeValues().get(fieldValue);
            if (attributeValue == null) {
                if (getDefaultAttributeValue() != null) {
                    attributeValue = getDefaultAttributeValue();
                } else {
                    // CR#3779
                    throw DescriptorException.noFieldValueConversionToAttributeValueProvided(fieldValue, getMapping().getField(), getMapping());
                }
            }
        }
        return attributeValue;
    }

    /**
     * PUBLIC:
     * The default value can be used if the database can possibly store additional values then those that
     * have been mapped.  Any value retreived from the database that is not mapped will be substitued for the default value.
     */
    public Object getDefaultAttributeValue() {
        return defaultAttributeValue;
    }

    /**
     * INTERNAL:
     * Return the mapping.
     */
    protected DatabaseMapping getMapping() {
        return mapping;
    }

    /**
     * INTERNAL:
     * Set the mapping.
     */
    protected void setMapping(DatabaseMapping mapping) {
        this.mapping = mapping;
    }

    /**
     * INTERNAL:
     * Get the type of the field value to allow conversion from the database.
     */
    public Class getFieldClassification() {
        return fieldClassification;
    }

    public String getFieldClassificationName() {
        if ((fieldClassificationName == null) && (fieldClassification != null)) {
            fieldClassificationName = fieldClassification.getName();
        }
        return fieldClassificationName;
    }

    /**
     * INTERNAL:
     * Return the classifiction for the field contained in the mapping.
     * This is used to convert the row value to a consistent java value.
     * By default this is null which means unknown.
     */
    public Class getFieldClassification(DatabaseField fieldToClassify) {
        return getFieldClassification();
    }

    /**
     * INTERNAL:
     * Return a collection of the field to attribute value associations.
     */
    public Vector getFieldToAttributeValueAssociations() {
        Vector associations = new Vector(getFieldToAttributeValues().size());
        Iterator fieldValueEnum = getFieldToAttributeValues().keySet().iterator();
        Iterator attributeValueEnum = getFieldToAttributeValues().values().iterator();
        while (fieldValueEnum.hasNext()) {
            Object fieldValue = fieldValueEnum.next();
            if (fieldValue == Helper.NULL_VALUE) {
                fieldValue = null;
            }
            Object attributeValue = attributeValueEnum.next();
            if (attributeValue == Helper.NULL_VALUE) {
                attributeValue = null;
            }
            associations.addElement(new TypeMapping(fieldValue, attributeValue));
        }

        return associations;
    }

    /**
     * INTERNAL:
     * Get the field to attribute mapping.
     */
    public Map getFieldToAttributeValues() {
        return fieldToAttributeValues;
    }

    /**
     *  INTERNAL:
     *  Convert to the data value.
     */
    public Object convertObjectValueToDataValue(Object attributeValue, Session session) {
        Object fieldValue;
        if (attributeValue == null) {
            fieldValue = getAttributeToFieldValues().get(Helper.NULL_VALUE);
        } else {
            fieldValue = getAttributeToFieldValues().get(attributeValue);
            if (fieldValue == null) {
                throw DescriptorException.noAttributeValueConversionToFieldValueProvided(attributeValue, getMapping());
            }
        }
        return fieldValue;
    }


    /**
     * PUBLIC:
     * This is a very specific protocol which maps fieldValues "T" and "F"
     * to true and false respectively.
     */
    public void mapBooleans() {
        addConversionValue("F", Boolean.valueOf(false));
        addConversionValue("T", Boolean.valueOf(true));
    }

    /**
     * PUBLIC:
     * This is a very specific protocol which maps fieldValues "F" and "M"
     * to "Female" and "Male" respectively.
     */
    public void mapGenders() {
        addConversionValue("F", "Female");
        addConversionValue("M", "Male");
    }

    /**
     * PUBLIC:
     * This is a very specific protocol which maps fieldValues "Y" and "N"
     * to "Yes" and "No" respectively.
     */
    public void mapResponses() {
        addConversionValue("Y", "Yes");
        addConversionValue("N", "No");
    }

    /**
     * INTERNAL:
     * Set the field classification through searching the fields map.
     */
    public void initializeFieldClassification(Session session) throws DescriptorException {
        if (getFieldToAttributeValues().isEmpty()) {
            return;
        }
        Class type = null;
        Iterator fieldValuesEnum = getFieldToAttributeValues().keySet().iterator();
        while (fieldValuesEnum.hasNext() && (type == null)) {
            Object value = fieldValuesEnum.next();
            if (value != Helper.NULL_VALUE) {
                type = value.getClass();
            }
        }

        setFieldClassification(type);
        // CR#... Mapping must also have the field classification.
        if (getMapping().isDirectToFieldMapping()) {
            AbstractDirectMapping directMapping = (AbstractDirectMapping)getMapping();

            // Allow user to specify field type to override computed value. (i.e. blob, nchar)
            if (directMapping.getFieldClassification() == null) {
                directMapping.setFieldClassification(type);
            }
        }
    }

    /**
     * INTERNAL:
     * Set the mapping.
     */
    public void initialize(DatabaseMapping mapping, Session session) {
        this.mapping = mapping;
        initializeFieldClassification(session);
    }

    /**
     * INTERNAL:
     * Set the attribute to field mapping.
     */
    public void setAttributeToFieldValues(Map attributeToFieldValues) {
        this.attributeToFieldValues = attributeToFieldValues;
    }

    /**
     * PUBLIC:
     * The default value can be used if the database can possibly store additional values then those that
     * have been mapped.  Any value retreived from the database that is not mapped will be substitued for the default value.
     */
    public void setDefaultAttributeValue(Object defaultAttributeValue) {
        this.defaultAttributeValue = defaultAttributeValue;
    }

    /**
     * INTERNAL:
     * Set the type of the field value to allow conversion from the database.
     */
    public void setFieldClassification(Class fieldClassification) {
        this.fieldClassification = fieldClassification;
    }

    public void setFieldClassificationName(String fieldClassificationName) {
        this.fieldClassificationName = fieldClassificationName;
    }

    /**
     * INTERNAL:
     * Set a collection of the field to attribute value associations.
     */
    public void setFieldToAttributeValueAssociations(Vector fieldToAttributeValueAssociations) {
        setFieldToAttributeValues(new HashMap(fieldToAttributeValueAssociations.size() + 1));
        setAttributeToFieldValues(new HashMap(fieldToAttributeValueAssociations.size() + 1));
        for (Enumeration associationsEnum = fieldToAttributeValueAssociations.elements();
                 associationsEnum.hasMoreElements();) {
            Association association = (Association)associationsEnum.nextElement();
            addConversionValue(association.getKey(), association.getValue());
        }
    }

    /**
     * INTERNAL:
     * Set the field to attribute mapping.
     */
    public void setFieldToAttributeValues(Map fieldToAttributeValues) {
        this.fieldToAttributeValues = fieldToAttributeValues;
    }

    /**
     * INTERNAL:
     * If the converter converts the value to a non-atomic value, i.e.
     * a value that can have its' parts changed without being replaced,
     * then it must return false, serialization can be non-atomic.
     */
    public boolean isMutable() {
        return false;
    }
}
