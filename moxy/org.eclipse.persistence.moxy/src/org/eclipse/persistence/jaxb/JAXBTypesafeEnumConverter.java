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
 ******************************************************************************/
package org.eclipse.persistence.jaxb;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.sessions.Session;
import java.lang.reflect.Method;

/**
 * <b>Purpose</b>: JAXBTypesafeEnumConverter is used to allow mapping to type safe
 * enums according to the JAXB 1.0 spec.  Object values are not modified by the converter
 * when writing data values.  Data values are read in and the "fromString" method on
 * the enumeration class is invoked with the data value as a parameter.  The return value from
 * the "fromString" method will be an instance of the enum class specified on the mapping.
 */
public class JAXBTypesafeEnumConverter implements Converter {
    String enumClassName;
    Class enumClass;
    Method fromStringMethod;

    /**
    * PUBLIC:
    * Default constructor.
    */
    public JAXBTypesafeEnumConverter() {
    }

    /**
    * INTERNAL:
    * Return the attribute value.
    */
    public Object convertObjectValueToDataValue(Object objectValue, Session session) {
        return objectValue;
    }

    /**
    * INTERNAL:
    * The fromString value on the enum class must be invoked with the field value
     * specified as an argument.  The result returned should be an instance of the enum class.
    */
    public Object convertDataValueToObjectValue(Object dataValue, Session session) {
        Object result = null;

        try {
            result = PrivilegedAccessHelper.invokeMethod(fromStringMethod, enumClass, new Object[] { dataValue }); 
        } catch (Exception ex) {
            throw XMLMarshalException.errorInvokingFromStringMethod(ex, enumClass.getName());
        }

        return result;
    }

    public boolean isMutable() {
        return true;
    }

    /**
      * INTERNAL:
      * Set the enum class.
      */
    public void initialize(DatabaseMapping mapping, Session session) {
        if (getEnumClass() == null) {
            if (getEnumClassName() == null) {
                throw XMLMarshalException.enumClassNotSpecified();
            } else {
                try {
                    enumClass = session.getDatasourcePlatform().getConversionManager().getLoader().loadClass(enumClassName);
                } catch (Exception e) {
                    throw XMLMarshalException.invalidEnumClassSpecified(e, enumClassName);
                }
            }
        }
        try {
            fromStringMethod = PrivilegedAccessHelper.getMethod(enumClass, "fromString", new Class[] { String.class },true);
        } catch (Exception ex) {
            throw XMLMarshalException.errorInvokingFromStringMethod(ex, enumClass.getName());
        }
    }

    /**
    * PUBLIC:
    * Get the name of the enum class which contains the fromValue method.
    */
    public String getEnumClassName() {
        return enumClassName;
    }

    /**
      * PUBLIC:
      * Set the name of the enum class to know which class to invoke the fromValue method on.
      */
    public void setEnumClassName(String newClassName) {
        enumClassName = newClassName;
    }

    /**
      * PUBLIC:
      * Set the enum class to know which class to invoke the fromValue method on.
      */
    public void setEnumClass(Class enumClass) {
        this.enumClass = enumClass;
    }

    /**
    * PUBLIC:
    * Get the class which was set as the enum class which contains the fromValue method.
    */
    public Class getEnumClass() {
        return enumClass;
    }
}
