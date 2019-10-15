/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.mappings.converters;

import java.security.AccessController;
import java.security.PrivilegedActionException;

import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedNewInstanceFromClass;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.foundation.AbstractDirectMapping;
import org.eclipse.persistence.sessions.Session;

/**
 * <b>Purpose</b>: Allows a class name to be converted to and from a new instance of the class.
 *
 * When using a ClassInstanceConverter, the database will store the Class name and the java object
 * model will contain an instance of that class initialized with its default constructor
 *
 * @author James Sutherland
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public class ClassInstanceConverter implements Converter {
    protected DatabaseMapping mapping;

    /**
     * PUBLIC:
     * Default constructor.
     */
    public ClassInstanceConverter() {
    }

    /**
     * INTERNAL:
     * Convert the class name to a class, then create an instance of the class.
     */
    @Override
    public Object convertDataValueToObjectValue(Object fieldValue, Session session) {
        Object attributeValue = null;
        if (fieldValue != null) {
            Class attributeClass = (Class) session.getDatasourcePlatform().convertObject(fieldValue, ClassConstants.CLASS);
            try {
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    try {
                        attributeValue = AccessController.doPrivileged(new PrivilegedNewInstanceFromClass(attributeClass));
                    } catch (PrivilegedActionException exception) {
                        throw ConversionException.couldNotBeConverted(fieldValue, attributeClass, exception.getException());
                    }
                } else {
                    attributeValue = PrivilegedAccessHelper.newInstanceFromClass(attributeClass);
                }
            } catch (Exception exception) {
                throw ConversionException.couldNotBeConverted(fieldValue, attributeClass, exception);
            }
        }

        return attributeValue;
    }

    /**
     *  INTERNAL:
     *  Convert to the field class.
     */
    @Override
    public Object convertObjectValueToDataValue(Object attributeValue, Session session) {
        if (attributeValue == null) {
            return null;
        }
        return attributeValue.getClass().getName();
    }

    /**
     * INTERNAL:
     * Set the mapping.
     */
    @Override
    public void initialize(DatabaseMapping mapping, Session session) {
        this.mapping = mapping;
        // CR#... Mapping must also have the field classification.
        if (getMapping().isDirectToFieldMapping()) {
            AbstractDirectMapping directMapping = (AbstractDirectMapping)getMapping();

            // Allow user to specify field type to override computed value. (i.e. blob, nchar)
            if (directMapping.getFieldClassification() == null) {
                directMapping.setFieldClassification(ClassConstants.STRING);
            }
        }
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
     * If the converter converts the value to a non-atomic value, i.e.
     * a value that can have its' parts changed without being replaced,
     * then it must return false, serialization can be non-atomic.
     */
    @Override
    public boolean isMutable() {
        return false;
    }
}
