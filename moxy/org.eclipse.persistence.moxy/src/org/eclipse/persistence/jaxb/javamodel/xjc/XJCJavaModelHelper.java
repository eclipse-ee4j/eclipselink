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
 *     rbarkhouse - 2009-12-18 13:04:58 - EclipseLink 2.1 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.jaxb.javamodel.xjc;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;

public class XJCJavaModelHelper {

    private static Map<Identifier, Field> fieldCache = new HashMap<Identifier, Field>();
    private static Map<Identifier, Method> methodCache = new HashMap<Identifier, Method>();

    public static Object getFieldValueByReflection(Object anObject, String aFieldName) throws Exception {
        return getFieldValueByReflection(anObject, anObject.getClass(), aFieldName);
    }

    public static Object getFieldValueByReflection(Object anObject, Class aClass, String aFieldName) throws Exception {
        Field field = fieldCache.get(aClass);

        if (field == null) {
            field = PrivilegedAccessHelper.getDeclaredField(aClass, aFieldName, true);
            fieldCache.put(new Identifier(aClass, aFieldName), field);
        }

        Object fieldValue = PrivilegedAccessHelper.getValueFromField(field, anObject);
        return fieldValue;
    }

    public static Object invokeMethodByReflection(Object anObject, String aMethodName, Class[] paramTypes) throws Exception {
        Method method = methodCache.get(anObject.getClass());

        if (method == null) {
            method = PrivilegedAccessHelper.getMethod(anObject.getClass(), aMethodName, paramTypes, true);
            methodCache.put(new Identifier(anObject.getClass(), aMethodName), method);
        }

        Object methodValue = PrivilegedAccessHelper.invokeMethod(method, anObject);
        return methodValue;
    }

    private static class Identifier {
        Class aClass;
        String aName;

        public Identifier(Class c, String s) {
            this.aClass = c;
            this.aName = s;
        }
    }

}