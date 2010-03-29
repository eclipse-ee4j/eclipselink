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
 *     Oracle - initial API and implementation from Oracle TopLink
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping files
 *     09/23/2008-1.1 Guy Pelletier 
 *       - 241651: JPA 2.0 Access Type support
 *     10/01/2008-1.1 Guy Pelletier 
 *       - 249329: To remain JPA 1.0 compliant, any new JPA 2.0 annotations should be referenced by name
 *     03/29/2010-2.1 Guy Pelletier 
 *       - 267217: Add Named Access Type to EclipseLink-ORM
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors.objects;

import java.util.ArrayList;
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
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.jpa.metadata.MetadataConstants;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;

/**
 * INTERNAL:
 * An object to hold onto a valid JPA decorated method.
 * 
 * @author Guy Pelletier
 * @since TopLink 10.1.3/EJB 3.0 Preview
 */
public class MetadataMethod extends MetadataAnnotatedElement {

    /** Class that the method is defined in. */
    protected MetadataClass m_metadataClass;
    
    /** Class name of method return type. */
    protected String m_returnType;

    /** List of class names of method parameters. */
    protected List<String> m_parameters;
    
    /** Corresponding set method, if the method is an accessor get method. */
    protected MetadataMethod m_setMethod;
    
    /** Used to store multiple methods with the same name in a class. */
    protected MetadataMethod m_next;

    /** Constants used for default virtual access setter and getter methods */
    public static final String DEFAULT_VIRTUAL_ACCESS_GET_METHOD = "get";
    public static final String DEFAULT_VIRTUAL_ACCESS_SET_METHOD = "set";
    
    /**
     * Create the method from the class metadata.
     */
    public MetadataMethod(MetadataFactory factory, MetadataClass metadataClass) {
        super(factory);
        m_metadataClass = metadataClass;
        m_parameters = new ArrayList<String>();
    }
    
    /**
     * INTERNAL:
     */
    public void addParameter(String parameter) {
        m_parameters.add(parameter);
    }
    
    /**
     * INTERNAL:
     */
    public MetadataClass getMetadataClass() {
        return m_metadataClass;
    }
    
    /**
     * INTERNAL:
     */
    public MetadataMethod getNext() {
        return m_next;
    }
    
    /**
     * INTERNAL:
     */
    public List<String> getParameters() {
        return m_parameters;
    }
    
    /**
     * INTERNAL:
     */
    public String getReturnType() {
        return m_returnType;
    }
    
    /**
     * INTERNAL:
     * Method to convert a getMethod into a setMethod. This method could return 
     * null if the corresponding set method is not found.
     */ 
    public MetadataMethod getSetMethod() {
        if (m_setMethod == null) {
            m_setMethod = getSetMethod(getMetadataClass());
        }
        return m_setMethod;
    }
    
    /**
     * INTERNAL:
     * Method to convert a getMethod into a setMethod.
     */ 
    public void setSetMethod(MetadataMethod method) {
        m_setMethod = method;
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
        
        if (getMethodName.startsWith(Helper.GET_PROPERTY_METHOD_PREFIX)) {
            // Replace 'get' with 'set'.
            setMethod = cls.getMethod(Helper.SET_PROPERTY_METHOD_PREFIX + getMethodName.substring(3), params);
        } else {        
            // methodName.startsWith(IS_PROPERTY_METHOD_PREFIX)
            // Check for a setXyz method first, if it exists use it.
            setMethod = cls.getMethod(Helper.SET_PROPERTY_METHOD_PREFIX + getMethodName.substring(2), params);
            
            if (setMethod == null) {
                // setXyz method was not found try setIsXyz
                setMethod = cls.getMethod(Helper.SET_IS_PROPERTY_METHOD_PREFIX + getMethodName.substring(2), params);
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
                    // General, catch all remaining cases and log a warning message.
                    getLogger().logConfigMessage(MetadataLogger.IGNORE_MAPPING_METADATA, this, descriptor.getJavaClass());
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
        return (getName().startsWith(Helper.GET_PROPERTY_METHOD_PREFIX) || getName().startsWith(Helper.IS_PROPERTY_METHOD_PREFIX)) && hasAttributeName();
    }

    /**
     * INTERNAL:
     */
    public void setMetadataClass(MetadataClass metadataClass) {
        m_metadataClass = metadataClass;
    }
    
    /**
     * INTERNAL:
     */
    public void setNext(MetadataMethod next) {
        m_next = next;
    }
    
    /**
     * INTERNAL:
     */
    public void setParameters(List<String> parameters) {
        m_parameters = parameters;
    }
    
    /**
     * INTERNAL:
     */
    public void setReturnType(String returnType) {
        m_returnType = returnType;
        setType(returnType);
    }
}
