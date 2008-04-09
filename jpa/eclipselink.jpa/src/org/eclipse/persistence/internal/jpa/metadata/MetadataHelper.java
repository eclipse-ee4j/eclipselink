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
package org.eclipse.persistence.internal.jpa.metadata;

import java.lang.annotation.Annotation;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.security.AccessController;
import java.security.PrivilegedActionException;

import org.eclipse.persistence.exceptions.ValidationException;

import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.internal.security.PrivilegedGetDeclaredFields;
import org.eclipse.persistence.internal.security.PrivilegedGetDeclaredMethods;
import org.eclipse.persistence.internal.security.PrivilegedNewInstanceFromClass;

/**
 * Common helper methods for the metadata processing.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class MetadataHelper {
    public static final String PERSISTENCE_PACKAGE_PREFIX = "javax.persistence";
    
    /**
     * INTERNAL: XMLEntityMappings calls this one
     * Load a class from a given class name.
     */
    static Class getClassForName(String classname, ClassLoader loader) {
        try {
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                try {
                    return (Class) AccessController.doPrivileged(new PrivilegedClassForName(classname, true, loader));
                } catch (PrivilegedActionException exception) {
                    throw ValidationException.unableToLoadClass(classname, exception.getException());
                }
            } else {
                return PrivilegedAccessHelper.getClassForName(classname, true, loader);
            }
        } catch (ClassNotFoundException exception) {
            throw ValidationException.unableToLoadClass(classname, exception);
        }
    }
    
    /**
     * INTERNAL:
     * Create a new instance of the class given.
     */
    static Object getClassInstance(Class cls) {
        try {
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                try {
                    return AccessController.doPrivileged(new PrivilegedNewInstanceFromClass(cls));
                } catch (PrivilegedActionException exception) {
                    throw ValidationException.errorInstantiatingClass(cls, exception.getException());
                }
            } else {
                return PrivilegedAccessHelper.newInstanceFromClass(cls);
            }
        } catch (IllegalAccessException exception) {
            throw ValidationException.errorInstantiatingClass(cls, exception);
        } catch (InstantiationException exception) {
            throw ValidationException.errorInstantiatingClass(cls, exception);
        }
    }
    
    /**
     * INTERNAL:
     * Create a new instance of the class name.
     */
    static Object getClassInstance(String className, ClassLoader loader) {
        return getClassInstance(getClassForName(className, loader));
    }
    
    /**
     * INTERNAL:
	 * Get the declared methods from a class using the doPriveleged security
     * access. This call returns all methods (private, protected, package and
     * public) on the give class ONLY. It does not traverse the superclasses.
     */
	static Method[] getDeclaredMethods(Class cls) {
        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
            try {
                return (Method[])AccessController.doPrivileged(new PrivilegedGetDeclaredMethods(cls));
            } catch (PrivilegedActionException exception) {
                // we will not get here, there are no checked exceptions in this call
                return null;
            }
        } else {
            return org.eclipse.persistence.internal.security.PrivilegedAccessHelper.getDeclaredMethods(cls);
        }
	}

    /**
     * INTERNAL:
	 * Get the declared fields from a class using the doPriveleged security
     * access.
     */
	static Field[] getFields(Class cls) {
        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
            try {
                return (Field[])AccessController.doPrivileged(new PrivilegedGetDeclaredFields(cls));
            } catch (PrivilegedActionException exception) {
                // no checked exceptions are thrown, so we should not get here
                return null;
            }
        } else {
            return PrivilegedAccessHelper.getDeclaredFields(cls);
        }
	}  
	
    /**
     * INTERNAL:
     * Helper method to return a field name from a candidate field name and a 
     * default field name.
     * 
     * Requires the context from where this method is called to output the 
     * correct logging message when defaulting the field name.
     *
     * In some cases, both the name and defaultName could be "" or null,
     * therefore, don't log a message and return name.
     */
    public static String getName(String name, String defaultName, String context, MetadataLogger logger, String location) {
        // Check if a candidate was specified otherwise use the default.
        if (name != null && !name.equals("")) {
            return name;
        } else if (defaultName == null || defaultName.equals("")) {
            return "";
        } else {
            // Log the defaulting field name based on the given context.
            logger.logConfigMessage(context, location, defaultName);
            return defaultName;
        }
    }
    
    /**
     * INTERNAL:
     * Helper method to return a string value if specified, otherwise returns
     * the default value. 
     */
    public static Integer getValue(Integer value, Integer defaultValue) {
        // Check if a candidate was specified otherwise use the default.
        if (value == null) {
            return defaultValue;
        } else {
            // TODO: log a defaulting message
            return value;
        } 
    }
    
    /**
     * INTERNAL:
     * Helper method to return a string value if specified, otherwise returns
     * the default value.
     */
    public static String getValue(String value, String defaultValue) {
        // Check if a candidate was specified otherwise use the default.
        if (value != null && ! value.equals("")) {
            return value;
        } else {
            // TODO: log a defaulting message
            return defaultValue;
        }
    }

    /**
     * INTERNAL:
     */
    static boolean havePersistenceAnnotationsDefined(AnnotatedElement[] annotatedElements) {
        for (AnnotatedElement annotatedElement : annotatedElements) {
            for (Annotation annotation : annotatedElement.getDeclaredAnnotations()) {
                if (annotation.annotationType().getName().startsWith(PERSISTENCE_PACKAGE_PREFIX)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /** 
     * INTERNAL:
     * Indicates whether the specified annotation is present on the specified 
     * class. NOTE: Calling this method directly does not take any metadata
     * complete flag into consideration. Look at the other isAnnotationPresent
     * methods that take a descriptor. 
     */
    public static boolean isAnnotationPresent(Class annotation, AnnotatedElement annotatedElement) {
        for (Object declaredAnnotation : annotatedElement.getDeclaredAnnotations()) {
            if ((declaredAnnotation.toString().substring(1, declaredAnnotation.toString().indexOf("("))).equalsIgnoreCase(annotation.getName())){
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     */
    public static boolean valuesMatch(Object value1, Object value2) {
    	if ((value1 == null && value2 != null) || (value2 == null && value1 != null)) {
    		return false;
    	} else if (value1 == null && value2 == null) {
    		return true;
    	} else {
    		return value1.equals(value2);
    	}
    }
}
