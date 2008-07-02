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
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping files
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors.objects;

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;

import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedGetMethod;

/**
 * INTERNAL:
 * An object to hold onto a valid EJB 3.0 decorated method.
 * 
 * @author Guy Pelletier
 * @since TopLink 10.1.3/EJB 3.0 Preview
 */
public class MetadataMethod extends MetadataAnnotatedElement {
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
     * Called from CMP3Policy ... doesn't need a logger.
     */
    public MetadataMethod(Method getMethod, Class declaringClass) {
        super(getMethod);
        
        init(getMethod, declaringClass);
    }
    
    /**
     * INTERNAL:
     */
    public MetadataMethod(Method getMethod, MetadataLogger logger) {
        super(getMethod, logger);
        
        init(getMethod, getMethod.getDeclaringClass());
    }
    
    /**
     * INTERNAL:
     */
    public MetadataMethod(Method getMethod, Method setMethod, String attributeName, XMLEntityMappings entityMappings) {
        super(getMethod, entityMappings);
        
        m_getMethod = getMethod;
        m_setMethod = setMethod;
        
        setName(getMethod.getName());
        setAttributeName(attributeName);
        setRelationType(getMethod.getGenericReturnType());
    }
    
    /**
     * INTERNAL:
     */
    public MetadataMethod(Method getMethod, XMLEntityMappings entityMappings) {
        super(getMethod, entityMappings);
        
        init(getMethod, getMethod.getDeclaringClass()); 
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
        
        // We're looking at method named 'get' or 'set', therefore,
        // there is no attribute name, set it to "" string for now.
        if (methodName.equals(GET_PROPERTY_METHOD_PREFIX) || methodName.equals(IS_PROPERTY_METHOD_PREFIX)) {
            return "";
        } else if (methodName.startsWith(GET_PROPERTY_METHOD_PREFIX)) {
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
     */
    protected Method getMethod() {
        return (Method) getAnnotatedElement();
    }
    
    /**
     * INTERNAL:
     * Method to convert a getMethod into a setMethod. This method could return 
     * null if the corresponding set method is not found.
     */ 
    public Method getSetMethod() {
        return m_setMethod;
    }
    
    /**
     * INTERNAL:
     * Method to convert a getMethod into a setMethod. This method could return 
     * null if the corresponding set method is not found.
     */ 
    public Method getSetMethod(Method method, Class cls) {
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
     * If the methodName passed in is a declared method on cls, then return
     * the methodName. Otherwise return null to indicate it does not exist.
     */
    protected Method getMethod(String methodName, Class cls, Class[] params) {
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
    public String getSetMethodName() {
        return m_setMethod.getName();
    }
    
    /**
     * INTERNAL:
     */
    public boolean hasAttributeName() {
        return ! getAttributeName().equals("");
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
    protected void init(Method getMethod, Class declaringClass) {
        m_getMethod = getMethod;
        m_setMethod = getSetMethod(getMethod, declaringClass);
        
        setName(getMethod.getName());
        setAttributeName(getAttributeNameFromMethodName(getMethod.getName()));
        setRelationType(getMethod.getGenericReturnType());
    }
    
    /**
     * INTERNAL:
     * Return true if it has a valid name (starts with get or is) and has a
     * property name (getXyz or isXyz) and does not have parameters and has
     * an associated set method.
     */
    protected boolean isALifeCycleCallbackMethod() {
        return isAnnotationPresent(PostLoad.class) ||
               isAnnotationPresent(PostPersist.class) ||
               isAnnotationPresent(PostRemove.class) ||
               isAnnotationPresent(PostUpdate.class) ||
               isAnnotationPresent(PrePersist.class) ||
               isAnnotationPresent(PreRemove.class) ||
               isAnnotationPresent(PreUpdate.class);
    }
    
    /**
     * INTERNAL:
     * Return true if it has a valid name (starts with 'get' or 'is') and has a
     * property name (get'Xyz' or is'Xyz') and does not have parameters and has
     * an associated set method.
     */
    protected boolean isValidPersistenceMethod() {
        return isValidPersistenceMethodName() && ! hasParameters() && hasSetMethod();
    }
    
    /**
     * INTERNAL:
     * Return true is this method is a valid persistence method. This method
     * will validate against any declared annotations on the method.
     */
    public boolean isValidPersistenceMethod(MetadataDescriptor descriptor) {
        return ! isALifeCycleCallbackMethod() && isValidPersistenceMethod(descriptor, hasDeclaredAnnotations(descriptor));
    }
    
    /**
     * INTERNAL:
     * Return true is this method is a valid persistence method. User decorated
     * is used to indicate that the method either had persistence annotations
     * defined on it or that it was specified in XML.
     */
    public boolean isValidPersistenceMethod(MetadataDescriptor descriptor, boolean userDecorated) {
        if (! isValidPersistenceElement(getMethod().getModifiers()) || ! isValidPersistenceMethod()) {
            // So it's not a valid method, did the user decorate it with
            // annotations or specify it in XML?
            if (userDecorated) {
                // Let's figure out which exception we should throw then ...
                if (hasParameters()) {
                    throw ValidationException.mappingMetadataAppliedToMethodWithArguments(getMethod(), descriptor.getJavaClass());
                } else if (!hasSetMethod()) {
                    throw ValidationException.noCorrespondingSetterMethodDefined(descriptor.getJavaClass(), getMethod());
                } else {
                    // General, catch all remaining exception cases. 
                    throw ValidationException.mappingMetadataAppliedToInvalidAttribute(getMethod(), descriptor.getJavaClass());
                }
            }
            
            return false;
        }
        
        return true;
    }
    
    /**
     * INTERNAL:
     */
    public boolean isValidPersistenceMethodName() {
        return (getName().startsWith(GET_PROPERTY_METHOD_PREFIX) || getName().startsWith(IS_PROPERTY_METHOD_PREFIX)) && hasAttributeName();
    }
}
