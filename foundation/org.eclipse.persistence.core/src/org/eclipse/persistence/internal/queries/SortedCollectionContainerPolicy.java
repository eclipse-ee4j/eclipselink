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
package org.eclipse.persistence.internal.queries;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.Comparator;
import java.lang.reflect.Constructor;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedGetConstructorFor;
import org.eclipse.persistence.internal.security.PrivilegedInvokeConstructor;
import org.eclipse.persistence.internal.security.PrivilegedNewInstanceFromClass;

/**
 * <p><b>Purpose</b>: A SortedCollectionContainerPolicy is ContainerPolicy whose
 * container class implements the SortedInterface interface.
 * Added for BUG # 3233263
 * <p>
 * <p><b>Responsibilities</b>:
 * Provide the functionality to operate on an instance of a SortedSet.
 *
 * @see ContainerPolicy
 * @see MapContainerPolicy
 */
public class SortedCollectionContainerPolicy extends CollectionContainerPolicy {
    protected Comparator m_comparator = null;
    protected Class  comparatorClass = null ;
    protected String comparatorClassName = null;
    

    /**
     * INTERNAL:
     * Construct a new policy.
     */
    public SortedCollectionContainerPolicy() {
        super();
    }

    /**
     * INTERNAL:
     * Construct a new policy for the specified class.
     */
    public SortedCollectionContainerPolicy(Class containerClass) {
        super(containerClass);
    }

    
    /**
     * INTERNAL:
     * Construct a new policy for the specified class name.
     */
    public SortedCollectionContainerPolicy(String containerClassName) {
        super(containerClassName);
    }
    
    /**
     * INTERNAL:
     * Sets a comparator object for this policy to use when instantiating
     * a new SortedSet object.
     */
    public void setComparator(Comparator comparator) {
        m_comparator = comparator;
    }

    /**
     * INTERNAL:
     * Sets a comparator class for this policy to use when instantiating
     * a new SortedSet object.
     */
    public void setComparatorClass(Class comparatorClass) {
        if(Helper.classImplementsInterface(comparatorClass, java.util.Comparator.class)){
            m_comparator=(Comparator)Helper.getInstanceFromClass(comparatorClass);
        }else{
            throw ValidationException.invalidComparatorClass(comparatorClass.getName());
        }
        this.comparatorClass=comparatorClass;
    }
    
    /**
     * INTERNAL:
     * Sets a comparator class name for this policy to use when instantiating
     * a new SortedSet object.
     */
    public void setComparatorClassName(String comparatorClassName) {
        this.comparatorClassName=comparatorClassName;
    }
    
    
    /**
     * INTERNAL:
     * Return the stored comparator
     */
    public Comparator getComparator() {
        return m_comparator;
    }
    
    /**
     * INTERNAL:
     * Return the stored comparator class
     */
    public Class getComparatorClass() {
        return comparatorClass;
    }
    
    
    /**
     * INTERNAL:
     * return stored comparator class name
     */
    public String getComparatorClassName() {
        if (this.m_comparator!=null){
            return this.m_comparator.getClass().getName();
        }else if (this.comparatorClass!=null){
            return this.comparatorClass.getName();
        }else if (this.comparatorClassName!=null){
            return this.comparatorClassName;
        }else{
            return null;
        }
    }

    /**
     * INTERNAL
     * Override from ContainerPolicy. Need to maintain the comparator in the
     * new instance
     */
    public Object containerInstance() {
        try {
            if(m_comparator==null && comparatorClass!=null){
                if(Helper.classImplementsInterface(comparatorClass, java.util.Comparator.class)){
                    m_comparator=(Comparator)Helper.getInstanceFromClass(comparatorClass);
                }else{
                    throw ValidationException.invalidComparatorClass(comparatorClass.getName());
                }
            }
            if (m_comparator != null) {
                Object[] arguments = new Object[] { m_comparator };
                Class[] constructClass = new Class[] { Comparator.class };
                Constructor constructor = null;
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    try {
                        constructor = (Constructor)AccessController.doPrivileged(new PrivilegedGetConstructorFor(getContainerClass(), constructClass, false));
                        return AccessController.doPrivileged(new PrivilegedInvokeConstructor(constructor, arguments));
                    } catch (PrivilegedActionException exception) {
                        throw QueryException.couldNotInstantiateContainerClass(getContainerClass(), exception.getException());
                    }
                } else {
                    constructor = PrivilegedAccessHelper.getConstructorFor(getContainerClass(), constructClass, false);
                    return PrivilegedAccessHelper.invokeConstructor(constructor, arguments);
                }
            } else {
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    try {
                        return AccessController.doPrivileged(new PrivilegedNewInstanceFromClass(getContainerClass()));
                    } catch (PrivilegedActionException exception) {
                        throw QueryException.couldNotInstantiateContainerClass(getContainerClass(), exception.getException());
                    }
                } else {
                    return PrivilegedAccessHelper.newInstanceFromClass(getContainerClass());
                }
            }
        } catch (Exception ex) {
            throw QueryException.couldNotInstantiateContainerClass(getContainerClass(), ex);
        }
    }
    
    /**
     * INTERNAL:
     * Convert all the class-name-based settings in this SortedCollectionContainerPolicy to actual class-based
     * settings. This method is used when converting a project that has been built
     * with class names to a project with classes.
     * @param classLoader 
     */
    public void convertClassNamesToClasses(ClassLoader classLoader){
        super.convertClassNamesToClasses(classLoader);
        if(m_comparator==null){
             if(comparatorClass==null){
                 if(comparatorClassName!=null){
                      Class comparatorClass = Helper.getClassFromClasseName(comparatorClassName, classLoader);
                      if(Helper.classImplementsInterface(comparatorClass, java.util.Comparator.class)){
                          m_comparator=(Comparator)Helper.getInstanceFromClass(comparatorClass);
                      }else{
                          throw ValidationException.invalidComparatorClass(comparatorClassName);
                      }
                 }
             }else{
                 if(Helper.classImplementsInterface(comparatorClass, java.util.Comparator.class)){
                     m_comparator=(Comparator)Helper.getInstanceFromClass(comparatorClass);
                 }else{
                     throw ValidationException.invalidComparatorClass(comparatorClass.getName());
                 }
             }
        }
    }
    
}
