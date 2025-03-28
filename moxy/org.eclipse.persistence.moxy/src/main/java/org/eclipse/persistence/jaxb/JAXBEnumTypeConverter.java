/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.jaxb;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.EnumSet;
import java.util.Iterator;

import jakarta.xml.bind.annotation.XmlEnumValue;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.oxm.mappings.Mapping;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.ObjectTypeConverter;
import org.eclipse.persistence.sessions.Session;

/**
 * INTERNAL:
 * <p><b>Purpose</b>:Provide a means to Convert an Enumeration type to/from either a string representation
 * of the enum facet or a user defined value.
 *
 * <p><b>Responsibilities:</b></p>
 * <ul>
 * <li>Initialize the conversion values to be the Enum facets</li>
 * <li>Don't overwrite any existing, user defined conversion value</li>
 * </ul>
 */
public class JAXBEnumTypeConverter extends ObjectTypeConverter {
    private Class m_enumClass;
    private String m_enumClassName;
    private boolean m_usesOrdinalValues;

    /**
     * PUBLIC:
     */
    public JAXBEnumTypeConverter(Mapping mapping, String enumClassName, boolean usesOrdinalValues) {
        super((DatabaseMapping)mapping);

        m_enumClassName = enumClassName;
        m_usesOrdinalValues = usesOrdinalValues;
    }

    /**
     * INTERNAL:
     * Convert all the class-name-based settings in this converter to actual
     * class-based settings. This method is used when converting a project
     * that has been built with class names to a project with classes.
     */
    @Override
    public void convertClassNamesToClasses(ClassLoader classLoader){
        m_enumClass = PrivilegedAccessHelper.callDoPrivilegedWithException(
                () -> org.eclipse.persistence.internal.security.PrivilegedAccessHelper.getClassForName(m_enumClassName, true, classLoader),
                (ex) -> ValidationException.classNotFoundWhileConvertingClassNames(m_enumClassName, ex)
        );
    }

    /**
     * INTERNAL:
     */
    @Override
    public void initialize(DatabaseMapping mapping, Session session) {
        Iterator<Enum> i = EnumSet.allOf(m_enumClass).iterator();
        while (i.hasNext()) {
            Enum theEnum = i.next();
            if (this.getAttributeToFieldValues().get(theEnum) == null) {
                Object existingVal = this.getAttributeToFieldValues().get(theEnum.name());
                if (existingVal != null) {
                    this.getAttributeToFieldValues().remove(theEnum.name());
                    addConversionValue(existingVal, theEnum);
                } else {
                    // if there's no user defined value, create a default
                    if (m_usesOrdinalValues) {
                        addConversionValue(theEnum.ordinal(), theEnum);
                    } else {
                        addConversionValue(getEnumValue(theEnum), theEnum);
                    }
                }
            }
        }

        super.initialize(mapping, session);
    }

    private String getEnumValue(Enum theEnum) {
        try {
            return PrivilegedAccessHelper.callDoPrivilegedWithException(() -> {
                Field field = theEnum.getClass().getField(theEnum.name());
                XmlEnumValue annotation = field.getAnnotation(XmlEnumValue.class);
                return annotation != null ? annotation.value() : theEnum.name();
            });
        } catch (Exception exc) {
           return theEnum.name();
        }
    }

    /**
     * PUBLIC:
     * Returns true if this converter uses ordinal values for the enum
     * conversion.
     */
    public boolean usesOrdinalValues() {
        return m_usesOrdinalValues;
    }

    @Override
    public Object convertDataValueToObjectValue(Object fieldValue, Session session) {
        try {
            return super.convertDataValueToObjectValue(fieldValue, session);
        } catch (DescriptorException e) {
            if(DescriptorException.NO_FIELD_VALUE_CONVERSION_TO_ATTRIBUTE_VALUE_PROVIDED == e.getErrorCode()) {
                if(fieldValue instanceof String) {
                    fieldValue = ((String) fieldValue).trim();
                    return super.convertDataValueToObjectValue(fieldValue, session);
                }
            }
            throw e;
        }
    }

}
