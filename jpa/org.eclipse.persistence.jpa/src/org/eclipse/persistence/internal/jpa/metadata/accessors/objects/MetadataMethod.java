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
 *     09/23/2008-1.1 Guy Pelletier 
 *       - 241651: JPA 2.0 Access Type support
 *     10/01/2008-1.1 Guy Pelletier 
 *       - 249329: To remain JPA 1.0 compliant, any new JPA 2.0 annotations should be referenced by name
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors.objects;

import java.util.Arrays;
import java.util.List;

import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.metadata.MetadataConstants;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;

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

    /** Class that the method is defined in. */
    protected MetadataClass metadataClass;
    
    /** Class name of method return type. */
    protected String returnType;

    /** List of class names of method parameters. */
    protected List<String> parameters;
    
    /** Corresponding set method, if the method is an accessor get method. */
    protected MetadataMethod setMethod;
    
    /** Used to store multiple methods with the same name in a class. */
    protected MetadataMethod next;
        
    /**
     * Create the method from the class metadata.
     */
    public MetadataMethod(MetadataClass metadataClass, MetadataLogger logger) {
        super(logger);
        this.metadataClass = metadataClass;
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
     * Method to convert a getMethod into a setMethod. This method could return 
     * null if the corresponding set method is not found.
     */ 
    public MetadataMethod getSetMethod() {
        if (this.setMethod == null) {
            this.setMethod = getSetMethod(getMetadataClass());
        }
        return this.setMethod;
    }
    
    /**
     * INTERNAL:
     * Method to convert a getMethod into a setMethod.
     */ 
    public void setSetMethod(MetadataMethod method) {
        this.setMethod = method;
    }
    
    /**
     * INTERNAL:
     * Method to convert a getMethod into a setMethod. This method could return 
     * null if the corresponding set method is not found.
     */ 
    public MetadataMethod getSetMethod(MetadataClass cls) {
        String getMethodName = getName();
        List<String> params = Arrays.asList(new String[] {getReturnType()});
        MetadataMethod setMethod = null;
        
        if (getMethodName.startsWith(GET_PROPERTY_METHOD_PREFIX)) {
            // Replace 'get' with 'set'.
            setMethod = cls.getMethod(SET_PROPERTY_METHOD_PREFIX + getMethodName.substring(3), params);
        } else {        
            // methodName.startsWith(IS_PROPERTY_METHOD_PREFIX)
            // Check for a setXyz method first, if it exists use it.
            setMethod = cls.getMethod(SET_PROPERTY_METHOD_PREFIX + getMethodName.substring(2), params);
            
            if (setMethod == null) {
                // setXyz method was not found try setIsXyz
                setMethod = cls.getMethod(SET_IS_PROPERTY_METHOD_PREFIX + getMethodName.substring(2), params);
            }
        }
        
        return setMethod;
    }
    
    /**
     * INTERNAL:
     */
    public String getSetMethodName() {
        return getSetMethod().getName();
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
        return getParameters().size() > 0;
    }
    
    /**
     * INTERNAL:
     */
    public boolean hasSetMethod() {
        return getSetMethod() != null;
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
     * will validate against any declared annotations on the method.  If the 
     * mustBeExplicit flag is true, then we are processing the inverse of an 
     * explicit access setting and the property must have an Access(PROPERTY) 
     * setting to be processed. Otherwise, it is ignored.
     */
    public boolean isValidPersistenceMethod(boolean mustBeExplicit, MetadataDescriptor descriptor) {
        if (isValidPersistenceElement(mustBeExplicit, MetadataConstants.PROPERTY, descriptor)) {
            return ! isALifeCycleCallbackMethod() && isValidPersistenceMethod(descriptor, hasDeclaredAnnotations(descriptor));
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     * Return true is this method is a valid persistence method. User decorated
     * is used to indicate that the method either had persistence annotations
     * defined on it or that it was specified in XML.
     */
    public boolean isValidPersistenceMethod(MetadataDescriptor descriptor, boolean userDecorated) {
        if (! isValidPersistenceElement(getModifiers()) || ! isValidPersistenceMethod()) {
            // So it's not a valid method, did the user decorate it with
            // annotations or specify it in XML?
            if (userDecorated) {
                // Let's figure out which exception we should throw then ...
                if (hasParameters()) {
                    throw ValidationException.mappingMetadataAppliedToMethodWithArguments(this, descriptor.getJavaClass());
                } else if (!hasSetMethod()) {
                    throw ValidationException.noCorrespondingSetterMethodDefined(descriptor.getJavaClass(), this);
                } else {
                    // General, catch all remaining exception cases. 
                    throw ValidationException.mappingMetadataAppliedToInvalidAttribute(this, descriptor.getJavaClass());
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

    public MetadataMethod getNext() {
        return next;
    }

    public void setNext(MetadataMethod next) {
        this.next = next;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
        setType(returnType);
    }

    public List<String> getParameters() {
        return parameters;
    }

    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }

    public MetadataClass getMetadataClass() {
        return metadataClass;
    }

    public void setMetadataClass(MetadataClass metadataClass) {
        this.metadataClass = metadataClass;
    }
}
