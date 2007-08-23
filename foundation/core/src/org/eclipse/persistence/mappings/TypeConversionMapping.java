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

import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.mappings.converters.*;

/**
 * <b>Purpose</b>: Type conversion mappings are used to explicitly map a database type to a
 * Java type.
 * Note this functionality has been somewhat replaced by TypeConversionConverter which can be
 * used to obtain the same functionality on DirectToField and DirectCollection mappings.
 *
 * @see TypeConversionConverter
 *
 * @author Sati
 * @since TopLink/Java 1.0
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  This class is replaced by
 *         {@link org.eclipse.persistence.mappings.converters.TypeConversionConverter}
 */
public class TypeConversionMapping extends DirectToFieldMapping {

    /**
     * PUBLIC:
     * Default constructor.
     */
    public TypeConversionMapping() {
        setConverter(new TypeConversionConverter(this));
    }

    /**
     * INTERNAL:
     * Return and cast the converter.
     */
    public TypeConversionConverter getTypeConversionConverter() {
        return (TypeConversionConverter)getConverter();
    }

    /**
     * PUBLIC:
     * Returns the field type. For this mapping both field classification and attribute classification are
     * specified. Field classification is used when writing to the database.
     */
    public Class getFieldClassification() {
        return getTypeConversionConverter().getDataClass();
    }

    /**
     * INTERNAL:
     * Return the field class name as a string for MW usage.
     */
    public String getFieldClassificationName() {
        return getTypeConversionConverter().getDataClassName();
    }

    /**
     * INTERNAL:
     * Return the classifiction for the field contained in the mapping.
     * This is used to convert the row value to a consistent java value.
     */
    public Class getFieldClassification(DatabaseField fieldToClassify) {
        return getTypeConversionConverter().getDataClass();
    }

    /**
     * INTERNAL:
     */
    public boolean isTypeConversionMapping() {
        return true;
    }

    /**
     * PUBLIC:
     * Set the field type. For this mapping both field classification and attribute classification are
     * specified. Field classification is used when writing to the database.
     */
    public void setFieldClassification(Class fieldType) {
        getTypeConversionConverter().setDataClass(fieldType);
    }

    /**
     * INTERNAL:
     * Return the field class name as a string for MW usage.
     */
    public void setFieldClassificationName(String className) {
        getTypeConversionConverter().setDataClassName(className);
    }

    /**
     * INTERNAL:
     * Need to override as attribute conversion is based on attribute-classification.
     */
    public Object getAttributeValue(Object fieldValue, AbstractSession session) {
        Object attributeValue = super.getAttributeValue(fieldValue, session);
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