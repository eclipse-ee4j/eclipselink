/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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

import java.util.EnumSet;
import java.util.Iterator;

import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.oxm.mappings.Mapping;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper.PrivilegedExceptionCallable;
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

    // PrivilegedAccessHelper.getClassForName caller for PrivilegedAccessHelper.callDoPrivilegedWithException
    private static final class CallGetClassForName implements PrivilegedExceptionCallable<Class<?>> {

        private final ClassLoader classLoader;
        private final String m_enumClassName;

        private CallGetClassForName(final ClassLoader classLoader, final String m_enumClassName) {
            this.classLoader = classLoader;
            this.m_enumClassName = m_enumClassName;
        }

        @Override
        public Class<?> call() throws Exception {
            return org.eclipse.persistence.internal.security.PrivilegedAccessHelper.getClassForName(m_enumClassName, true, classLoader);
        }
    }

    // Handle exception in PrivilegedAccessHelper.callDoPrivilegedWithException
    private ValidationException getClassForNameException(final Exception ex) {
        return ValidationException.classNotFoundWhileConvertingClassNames(m_enumClassName, ex);
    }

    /**
     * INTERNAL:
     * Convert all the class-name-based settings in this converter to actual
     * class-based settings. This method is used when converting a project
     * that has been built with class names to a project with classes.
     * @param classLoader
     */
    public void convertClassNamesToClasses(ClassLoader classLoader) {
        CallGetClassForName callGetClassForName = new CallGetClassForName(classLoader, m_enumClassName);
        m_enumClass = PrivilegedAccessHelper.callDoPrivilegedWithException(
                callGetClassForName,
                this::getClassForNameException
        );
    }

    /**
     * INTERNAL:
     */
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
                        addConversionValue(theEnum.name(), theEnum);
                    }
                }
            }
        }

        super.initialize(mapping, session);
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
