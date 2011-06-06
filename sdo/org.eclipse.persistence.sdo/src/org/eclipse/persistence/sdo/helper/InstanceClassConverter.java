/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.sdo.helper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import org.eclipse.persistence.exceptions.SDOException;
import org.eclipse.persistence.internal.security.*;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.sessions.Session;

/**
 * <p><b>Purpose</b>: A converter used in conjunction with sdoJava:instanceClass
 * The customClass on the converter must be set and that class must have
 * a Constructor that takes a String argument and a toString method.
 * Used when the javaClass open content property is set.
 */
public class InstanceClassConverter implements Converter {
    private Class customClass;

    public InstanceClassConverter() {
    }

    /**
     * Convert the value as required during marshal.
     * @param objectValue
     * @param session
     * @return String value of the given object value, value returned from objectValue.toString
     */
    public Object convertObjectValueToDataValue(Object objectValue, Session session) {
        if (objectValue != null) {
            return objectValue.toString();
        }
        return null;
    }

    /**
     * Convert the value from XML as required during unmarshal
     * @param dataValue
     * @param session
     * @return Convert the value from XML by invoking the constructor that takes a spring parameter
     */
    public Object convertDataValueToObjectValue(Object dataValue, Session session) {
        if (dataValue != null) {
            Class[] params = new Class[1];
            params[0] = String.class;
            Constructor ctor = null;
            try {
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
                    ctor = (Constructor)AccessController.doPrivileged(new PrivilegedGetDeclaredConstructorFor(customClass, params, true));
                } else {
                    ctor = PrivilegedAccessHelper.getDeclaredConstructorFor(customClass, params, true);
                }
                Object[] args = new Object[1];
                args[0] = dataValue.toString();
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
                    return AccessController.doPrivileged(new PrivilegedInvokeConstructor(ctor, args));
                } else {
                    return PrivilegedAccessHelper.invokeConstructor(ctor, args);
                }
            } catch (PrivilegedActionException e) {
                throw SDOException.noConstructorWithString(e, customClass.getName());
            } catch (NoSuchMethodException e) {
                throw SDOException.noConstructorWithString(e, customClass.getName());
            } catch (IllegalAccessException e) {
                throw SDOException.noConstructorWithString(e, customClass.getName());
            } catch (InvocationTargetException e) {
                throw SDOException.noConstructorWithString(e, customClass.getName());
            } catch (InstantiationException e) {
                throw SDOException.noConstructorWithString(e, customClass.getName());
            }
        }

        //throw exception if not string.
        return null;
    }

    public boolean isMutable() {
        return true;
    }

    public void initialize(DatabaseMapping mapping, Session session) {
    }

    public void setCustomClass(Class customClass) {
        this.customClass = customClass;
    }

    public Class getCustomClass() {
        return customClass;
    }
}
