/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *      *     30/05/2012-2.4 Guy Pelletier    
 *       - 354678: Temp classloader is still being used during metadata processing
 ******************************************************************************/  
package org.eclipse.persistence.mappings.converters;

import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.*;

import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.descriptors.TypeMapping;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.mappings.foundation.AbstractDirectMapping;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.internal.security.PrivilegedGetConstructorFor;
import org.eclipse.persistence.internal.security.PrivilegedInvokeConstructor;
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
    // String type names and values set from JPA processing.
    protected String converterName;
    protected Class dataType;
    protected String dataTypeName;
    protected Class objectType;
    protected String objectTypeName;
    protected Map<String, String> conversionValueStrings;
    protected Map<String, String> addToAttributeOnlyConversionValueStrings;
    
    protected DatabaseMapping mapping;
    protected transient Map fieldToAttributeValues;
    protected Map attributeToFieldValues;
    protected transient Object defaultAttributeValue;
    protected String defaultAttributeValueString;
    protected transient Class fieldClassification;
    protected transient String fieldClassificationName;
    
    /**
     * PUBLIC:
     * Default constructor.
     */
    public ObjectTypeConverter() {
        this.attributeToFieldValues = new HashMap(10);
        this.fieldToAttributeValues = new HashMap(10);
        this.conversionValueStrings = new HashMap<String, String>(10);
        this.addToAttributeOnlyConversionValueStrings = new HashMap<String, String>(10);
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
     * INTERNAL:
     * Set from JPA processing where we deal with strings only to avoid
     * class loader conflicts.
     */
    public void addConversionValueStrings(String dataValue, String objectValue) {
        this.conversionValueStrings.put(dataValue, objectValue);
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
     * Set from JPA processing where we deal with strings only to avoid
     * class loader conflicts.
     */
    public void addToAttributeOnlyConversionValueStrings(String dataValue, String objectValue) {
        this.addToAttributeOnlyConversionValueStrings.put(dataValue, objectValue);
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
        if (dataTypeName != null) {
            dataType = loadClass(dataTypeName, classLoader);
        }
        
        if (objectTypeName != null) {
            objectType = loadClass(objectTypeName, classLoader);
        }
        
        if (objectType != null && dataType != null) {
            // Process the data to object mappings. The object and data values
            // should be primitive wrapper types so we can initialize the 
            // conversion values now.
            for (String dataValue : conversionValueStrings.keySet()) {
                String objectValue = conversionValueStrings.get(dataValue);
                
                addConversionValue(initObject(dataType, dataValue, true), initObject(objectType, objectValue, false));
            }

            for (String dataValue : addToAttributeOnlyConversionValueStrings.keySet()) {
                String objectValue = addToAttributeOnlyConversionValueStrings.get(dataValue);
                
                addToAttributeOnlyConversionValue(initObject(dataType, dataValue, true), initObject(objectType, objectValue, false));
            }
            
            if (defaultAttributeValueString != null) {
                setDefaultAttributeValue(initObject(objectType, defaultAttributeValueString, false));
            }
        }
    }
    
    /**
     * Load the given class name with the given loader.
     */
    protected Class loadClass(String className, ClassLoader classLoader) { 
        try {
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                try {
                    return (Class) AccessController.doPrivileged(new PrivilegedClassForName(className, true, classLoader));
                } catch (PrivilegedActionException e) {
                    throw ValidationException.classNotFoundWhileConvertingClassNames(className, e.getException());
                }
            } else {
                return org.eclipse.persistence.internal.security.PrivilegedAccessHelper.getClassForName(className, true, classLoader);
            }
        } catch (Exception exception) {
            throw ValidationException.classNotFoundWhileConvertingClassNames(className, exception);
        }
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
     * Set from JPA processing where we deal with strings only to avoid
     * class loader conflicts.
     */
    public void setObjectTypeName(String objectTypeName) {
        this.objectTypeName = objectTypeName;
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
        if (fieldToAttributeValues == null) {
            fieldToAttributeValues = new HashMap(10);
        }
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
        addConversionValue("F", Boolean.FALSE);
        addConversionValue("T", Boolean.TRUE);
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
     * Used to initialize string based conversion values set from JPA processing.
     */    
    private Object initObject(Class type, String value, boolean isData) {
        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
            try {
                Constructor constructor = (Constructor) AccessController.doPrivileged(new PrivilegedGetConstructorFor(type, new Class[] {String.class}, false));
                return AccessController.doPrivileged(new PrivilegedInvokeConstructor(constructor, new Object[] {value}));
            } catch (PrivilegedActionException exception) {
                throwInitObjectException(exception, type, value, isData);
            }
        } else {
            try {
                Constructor constructor = PrivilegedAccessHelper.getConstructorFor(type, new Class[] {String.class}, false);
                return PrivilegedAccessHelper.invokeConstructor(constructor, new Object[] {value});
            } catch (Exception exception) {
                throwInitObjectException(exception, type, value, isData);
            }
        }
        
        return null; // keep compiler happy, will never hit.
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
     * INTERNAL:
     * Set from JPA processing where we deal with strings only to avoid
     * class loader conflicts.
     */
    public void setConverterName(String converterName) {
        this.converterName = converterName;
    }
    
    /**
     * INTERNAL:
     * Set from JPA processing where we deal with strings only to avoid
     * class loader conflicts.
     */
    public void setDataTypeName(String dataTypeName) {
        this.dataTypeName = dataTypeName;
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
     * Set from JPA processing where we deal with strings only to avoid
     * class loader conflicts.
     */
    public void setDefaultAttributeValueString(String defaultAttributeValueString) {
        this.defaultAttributeValueString = defaultAttributeValueString;
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
    
    /**
     * INTERNAL:
     */    
    protected void throwInitObjectException(Exception exception, Class type, String value, boolean isData) {
        if (isData) {
            throw ValidationException.errorInstantiatingConversionValueData(converterName, value, type, exception);
        } else {
            throw ValidationException.errorInstantiatingConversionValueObject(converterName, value, type, exception);
        }
    }
}
