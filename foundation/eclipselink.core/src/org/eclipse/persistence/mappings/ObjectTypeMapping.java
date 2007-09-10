/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.mappings;

import java.util.*;
import org.eclipse.persistence.mappings.converters.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * <b>Purpose</b>: Object type mappings are used to match a fixed number of database primitives
 * to Java objects. They are used when the values on the databae and in the java differ.
 * To create an object type mapping, simply specify the instance variable and field names involved,
 * together with a conversion value.
 * Note this functionality has been somewhat replaced by ObjectTypeConverter which can be
 * used to obtain the same functionality on DirectToField and DirectCollection mappings.
 *
 * @see ObjectTypeConverter
 *
 * @author Sati
 * @since Toplink for Java 1.0
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  This class is replaced by
 *         {@link org.eclipse.persistence.mappings.converters.ObjectTypeConverter}
 */
public class ObjectTypeMapping extends DirectToFieldMapping {

    /**
     * PUBLIC:
     * Default constructor.
     */
    public ObjectTypeMapping() {
        this.converter = new ObjectTypeConverter(this);
    }

    /**
     * PUBLIC:
     * Return the converter cast to ObjectTypeConverter.
     */
    public ObjectTypeConverter getObjectTypeConverter() {
        return (ObjectTypeConverter)getConverter();
    }

    /**
     * PUBLIC:
     * A type conversion value is a two-way mapping from the database to the object.
     * The database value will be substituted for the object value when read,
     * and the object value will be substituted for database value when written.
     * Note that each field/attribute value must have one and only one attribute/field value to maintain a two-way mapping.
     */
    public void addConversionValue(Object fieldValue, Object attributeValue) {
        getObjectTypeConverter().addConversionValue(fieldValue, attributeValue);
    }

    /**
     * PUBLIC:
     * An attribute only conversion value is a one-way mapping from the database to the object.
     * This can be used if multiple database values are desired to be mapped to the same object value.
     * Note that when written only the default value will be used for the attribute, not this value.
     */
    public void addToAttributeOnlyConversionValue(Object fieldValue, Object attributeValue) {
        getObjectTypeConverter().addToAttributeOnlyConversionValue(fieldValue, attributeValue);
    }

    /**
     * INTERNAL:
     * Get the attribute to field mapping.
     */
    public Map getAttributeToFieldValues() {
        return getObjectTypeConverter().getAttributeToFieldValues();
    }

    /**
     * PUBLIC:
     * The default value can be used if the database can possibly store additional values then those that
     * have been mapped.  Any value retreived from the database that is not mapped will be substitued for the default value.
     */
    public Object getDefaultAttributeValue() {
        return getObjectTypeConverter().getDefaultAttributeValue();
    }

    /**
     * INTERNAL:
     * Return a collection of the field to attribute value associations.
     */
    public Vector getFieldToAttributeValueAssociations() {
        return getObjectTypeConverter().getFieldToAttributeValueAssociations();
    }

    /**
     * INTERNAL:
     * Get the field to attribute mapping.
     */
    public Map getFieldToAttributeValues() {
        return getObjectTypeConverter().getFieldToAttributeValues();
    }

    /**
     * INTERNAL:
     */
    public boolean isObjectTypeMapping() {
        return true;
    }

    /**
     * PUBLIC:
     * This is a very specific protocol which maps fieldValues "T" and "F"
     * to true and false respectively.
     */
    public void mapBooleans() {
        getObjectTypeConverter().mapBooleans();
    }

    /**
     * PUBLIC:
     * This is a very specific protocol which maps fieldValues "F" and "M"
     * to "Female" and "Male" respectively.
     */
    public void mapGenders() {
        getObjectTypeConverter().mapGenders();
    }

    /**
     * PUBLIC:
     * This is a very specific protocol which maps fieldValues "Y" and "N"
     * to "Yes" and "No" respectively.
     */
    public void mapResponses() {
        getObjectTypeConverter().mapResponses();
    }

    /**
     * INTERNAL:
     * Set the attribute to field mapping.
     */
    public void setAttributeToFieldValues(Hashtable attributeToFieldValues) {
        getObjectTypeConverter().setAttributeToFieldValues(attributeToFieldValues);
    }

    /**
     * PUBLIC:
     * The default value can be used if the database can possibly store additional values then those that
     * have been mapped.  Any value retreived from the database that is not mapped will be substitued for the default value.
     */
    public void setDefaultAttributeValue(Object defaultAttributeValue) {
        getObjectTypeConverter().setDefaultAttributeValue(defaultAttributeValue);
    }

    /**
     * INTERNAL:
     * Set a collection of the field to attribute value associations.
     */
    public void setFieldToAttributeValueAssociations(Vector fieldToAttributeValueAssociations) {
        getObjectTypeConverter().setFieldToAttributeValueAssociations(fieldToAttributeValueAssociations);
    }

    /**
     * INTERNAL:
     * Set the field to attribute mapping.
     */
    public void setFieldToAttributeValues(Hashtable fieldToAttributeValues) {
        getObjectTypeConverter().setFieldToAttributeValues(fieldToAttributeValues);
    }

    /**
     * INTERNAL:
     * This overides the default behavoir to maintain compatibility with how
     * object-type-mapping handles null values.
     */
    public Object getFieldValue(Object attributeValue, AbstractSession session) {
        Object fieldValue;
        if (attributeValue == null) {
            fieldValue = getAttributeToFieldValues().get(Helper.getNullWrapper());
        } else {
            fieldValue = getAttributeToFieldValues().get(attributeValue);
            if (fieldValue == null) {
                throw DescriptorException.noAttributeValueConversionToFieldValueProvided(attributeValue, this);
            }
        }
        if ((getNullValue() != null) && (getNullValue().equals(fieldValue))) {
            return null;
        }
        Class fieldClassification = getFieldClassification(getField());

        try {
            return session.getPlatform(getDescriptor().getJavaClass()).convertObject(fieldValue, fieldClassification);
        } catch (ConversionException e) {
            throw ConversionException.couldNotBeConverted(this, getDescriptor(), e);
        }
    }

    /**
     * INTERNAL:
     * This overides the default behavoir to maintain compatibility with how
     * object-type-mapping handles null values.
     */
    public Object getAttributeValue(Object fieldValue, AbstractSession session) {
        Object attributeValue = null;

        if (fieldValue == null) {
            attributeValue = getFieldToAttributeValues().get(Helper.getNullWrapper());
        } else {
            try {
                fieldValue = session.getDatasourcePlatform().getConversionManager().convertObject(fieldValue, getFieldClassification());
            } catch (ConversionException e) {
                throw ConversionException.couldNotBeConverted(this, getDescriptor(), e);
            }

            attributeValue = getFieldToAttributeValues().get(fieldValue);
            if (attributeValue == null) {
                if (getDefaultAttributeValue() != null) {
                    return getDefaultAttributeValue();
                }

                // CR#3779
                throw DescriptorException.noFieldValueConversionToAttributeValueProvided(fieldValue, field, this);
            }
        }
        if (fieldValue == null) {// Translate default null value
            attributeValue = getNullValue();
        }

        try {
            attributeValue = session.getDatasourcePlatform().convertObject(attributeValue, getAttributeClassification());
        } catch (ConversionException e) {
            throw ConversionException.couldNotBeConverted(this, getDescriptor(), e);
        }

        if (attributeValue == null) {// Translate default null value, conversion may have produced null.
            attributeValue = getNullValue();
        }
        return attributeValue;
    }
}