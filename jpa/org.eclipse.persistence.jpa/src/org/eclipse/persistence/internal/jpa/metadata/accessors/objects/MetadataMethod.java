/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
package org.eclipse.persistence.internal.jpa.metadata.accessors.objects;

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;

import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedGetMethod;

/**
 * An object to hold onto a valid EJB 3.0 decorated method.
 * 
 * @author Guy Pelletier
 * @since TopLink 10.1.3/EJB 3.0 Preview
 */
public class MetadataMethod extends MetadataAccessibleObject {
    public static final String IS_PROPERTY_METHOD_PREFIX = "is";
    public static final String GET_PROPERTY_METHOD_PREFIX = "get";
    private static final String SET_PROPERTY_METHOD_PREFIX = "set";
    private static final String SET_IS_PROPERTY_METHOD_PREFIX = "setIs";
    private static final int POSITION_AFTER_IS_PREFIX = IS_PROPERTY_METHOD_PREFIX.length();
    private static final int POSITION_AFTER_GET_PREFIX = GET_PROPERTY_METHOD_PREFIX.length();
    
    protected Method m_getMethod;
    protected Method m_setMethod;
    
    /**
     * INTERNAL:
     */
    public MetadataMethod(Method getMethod) {
        super(getMethod);
        
        m_getMethod = getMethod;
        m_setMethod = getSetMethod(getMethod, getMethod.getDeclaringClass());
        
        setName(getMethod.getName());
        setAttributeName(getAttributeNameFromMethodName(getMethod.getName()));
        setRelationType(getMethod.getGenericReturnType());
    }
    
    public MetadataMethod(Method getMethod, Method setMethod, String attributeName) {
        super(getMethod);
        
        m_getMethod = getMethod;
        m_setMethod = setMethod;
        
        setName(getMethod.getName());
        setAttributeName(attributeName);
        setRelationType(getMethod.getGenericReturnType());
    }
    
    /**
     * INTERNAL:
     * Method to convert a getXyz or isXyz method name to an xyz attribute name.
     * NOTE: The method name passed it may not actually be a method name, so
     * by default return the name passed in.
     */
    public static String getAttributeNameFromMethodName(String methodName) {
        String leadingChar = "";
        String restOfName = methodName;
        
        if (methodName.startsWith(GET_PROPERTY_METHOD_PREFIX)) {
            leadingChar = methodName.substring(POSITION_AFTER_GET_PREFIX, POSITION_AFTER_GET_PREFIX + 1);
            restOfName = methodName.substring(POSITION_AFTER_GET_PREFIX + 1);
        } else if (methodName.startsWith(IS_PROPERTY_METHOD_PREFIX)){
            leadingChar = methodName.substring(POSITION_AFTER_IS_PREFIX, POSITION_AFTER_IS_PREFIX + 1);
            restOfName = methodName.substring(POSITION_AFTER_IS_PREFIX + 1);
        }
        
        return leadingChar.toLowerCase().concat(restOfName);
    }
    
    /**
     * INTERNAL:
     * Method to convert a getMethod into a setMethod. This method could return 
     * null if the corresponding set method is not found.
     */ 
    public static Method getSetMethod(Method method, Class cls) {
        String getMethodName = method.getName();
        Class[] params = new Class[] { method.getReturnType() };
            
        if (getMethodName.startsWith(GET_PROPERTY_METHOD_PREFIX)) {
            // Replace 'get' with 'set'.
            return getMethod(SET_PROPERTY_METHOD_PREFIX + getMethodName.substring(3), cls, params);
        }
        
        // methodName.startsWith(IS_PROPERTY_METHOD_PREFIX)
        // Check for a setXyz method first, if it exists use it.
        Method setMethod = getMethod(SET_PROPERTY_METHOD_PREFIX + getMethodName.substring(2), cls, params);
        
        if (setMethod == null) {
            // setXyz method was not found try setIsXyz
            return getMethod(SET_IS_PROPERTY_METHOD_PREFIX + getMethodName.substring(2), cls, params);
        }
        
        return setMethod;
    }
    
    /**
     * INTERNAL:
     */
    public String getSetMethodName() {
        return m_setMethod.getName();
    }
    
    /**
     * INTERNAL:
     * If the methodName passed in is a declared method on cls, then return
     * the methodName. Otherwise return null to indicate it does not exist.
     */
    static Method getMethod(String methodName, Class cls, Class[] params) {
        try {
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                try {
                    return AccessController.doPrivileged(new PrivilegedGetMethod(cls, methodName, params, true));
                } catch (PrivilegedActionException exception) {
                    return null;
                }
            } else {
                return PrivilegedAccessHelper.getMethod(cls, methodName, params, true);
            }
        } catch (NoSuchMethodException e1) {
            return null;
        }
    }
    
    /**
     * INTERNAL:
     */
    public boolean hasParameters() {
        return m_getMethod.getParameterTypes().length > 0;
    }
    
    /**
     * INTERNAL:
     */
    public boolean hasSetMethod() {
        return m_setMethod != null;
    }
    
    /**
     * INTERNAL:
     */
    public boolean isValidPersistenceMethodName() {
        return getName().startsWith(GET_PROPERTY_METHOD_PREFIX) || getName().startsWith(IS_PROPERTY_METHOD_PREFIX);
    }
}
