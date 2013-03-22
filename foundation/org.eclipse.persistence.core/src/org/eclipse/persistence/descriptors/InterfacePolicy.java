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
package org.eclipse.persistence.descriptors;

import java.io.*;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.*;

import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.*;

/**
 * <b>Purpose</b>: Allows for a descriptor's implemented interfaces to be configured.
 * Generally Interface Descriptors are used for 1 of 2 reasons:<p>
 *      a. Interface descriptors can be used to query across a set of classes that do not share a table.<br>
 *      b. As a target of a variable one to one mapping.
 *
 * @since TopLink for Java 2.0
 */
public class InterfacePolicy implements Serializable {
    protected Vector parentInterfaces;
    protected Vector parentInterfaceNames;
    protected Vector parentDescriptors;
    protected Vector childDescriptors;
    protected ClassDescriptor descriptor;
    protected Class implementorDescriptor;
    protected String implementorDescriptorClassName;

    /**
     * INTERNAL:
     * Create a new policy.
     * Only descriptor involved in interface should have a policy.
     */
    public InterfacePolicy() {
        this.childDescriptors = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance();
        this.parentInterfaces = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(2);
        this.parentInterfaceNames = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(2);
        this.parentDescriptors = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(2);
    }

    /**
     * INTERNAL:
     * Create a new policy.
     * Only descriptor involved in interface should have a policy.
     */
    public InterfacePolicy(ClassDescriptor descriptor) {
        this();
        this.descriptor = descriptor;
    }

    /**
     * INTERNAL:
     * Add child descriptor to the parent descriptor.
     */
    public void addChildDescriptor(ClassDescriptor childDescriptor) {
        getChildDescriptors().addElement(childDescriptor);
    }

    /**
     * INTERNAL:
     * Add parent descriptor.
     */
    public void addParentDescriptor(ClassDescriptor parentDescriptor) {
        getParentDescriptors().addElement(parentDescriptor);
    }

    /**
     * PUBLIC:
     * Add the parent Interface class.
     *
     * This method should be called once for each parent Interface of the Descriptor.
     */
    public void addParentInterface(Class parentInterface) {
        getParentInterfaces().addElement(parentInterface);
    }

    public void addParentInterfaceName(String parentInterfaceName) {
        getParentInterfaceNames().addElement(parentInterfaceName);
    }

    /**
     * INTERNAL:
     * Return if there are any child descriptors.
     */
    public boolean hasChild() {
        return this.childDescriptors.size() > 0;
    }

    /**
     * INTERNAL:
     * Return all the child descriptors.
     */
    public Vector getChildDescriptors() {
        return childDescriptors;
    }

    protected ClassDescriptor getDescriptor() {
        return descriptor;
    }

    /**
     * INTERNAL:
     * Returns the implementor descriptor class.
     */
    public Class getImplementorDescriptor() {
        return implementorDescriptor;
    }

    /**
     * INTERNAL:
     * Returns the implementor descriptor class name.
     */
    public String getImplementorDescriptorClassName() {
        if ((implementorDescriptorClassName == null) && (implementorDescriptor != null)) {
            implementorDescriptorClassName = implementorDescriptor.getName();
        }
        return implementorDescriptorClassName;
    }

    /**
     * INTERNAL:
     * Return all the parent descriptors.
     */
    public Vector getParentDescriptors() {
        return parentDescriptors;
    }

    /**
     * INTERNAL:
     * Return the vector of parent interfaces.
     */
    public Vector getParentInterfaces() {
        return parentInterfaces;
    }

    public Vector getParentInterfaceNames() {
        if (parentInterfaceNames.isEmpty() && !parentInterfaces.isEmpty()) {
            for (int i = 0; i < parentInterfaces.size(); i++) {
                parentInterfaceNames.addElement(((Class)parentInterfaces.elementAt(i)).getName());
            }
        }
        return parentInterfaceNames;
    }

    /**
     * INTERNAL:
     * Convert all the class-name-based settings in this InheritancePolicy to actual class-based settings.
     * This method is used when converting a project that has been built with class names to a project with classes.
     * It will also convert referenced classes to the versions of the classes from the classLoader.
     */
    public void convertClassNamesToClasses(ClassLoader classLoader) {
        Vector newParentInterfaces = new Vector();
        for (Iterator iterator = getParentInterfaceNames().iterator(); iterator.hasNext(); ) {
            String interfaceName = (String)iterator.next();
            Class interfaceClass = null;
            try {
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    try {
                        interfaceClass = (Class)AccessController.doPrivileged(new PrivilegedClassForName(interfaceName, true, classLoader));
                    } catch (PrivilegedActionException exception) {
                        throw ValidationException.classNotFoundWhileConvertingClassNames(interfaceName, exception.getException());
                    }
                } else {
                    interfaceClass = org.eclipse.persistence.internal.security.PrivilegedAccessHelper.getClassForName(interfaceName, true, classLoader);
                }
            } catch (ClassNotFoundException exc){
                throw ValidationException.classNotFoundWhileConvertingClassNames(interfaceName, exc);
            }
            newParentInterfaces.add(interfaceClass);
        }
        this.parentInterfaces = newParentInterfaces;
    }
    
    /**
     * INTERNAL:
     * Set the vector to store parent interfaces.
     */
    public void initialize(AbstractSession session) {
    }

    /**
     * INTERNAL:
     * Check if it is a child descriptor.
     */
    public boolean isInterfaceChildDescriptor() {
        return ((!(parentInterfaces == null) && !parentInterfaces.isEmpty()) ||
            (!(parentInterfaceNames == null) && !parentInterfaceNames.isEmpty()));
    }

    /**
     * INTERNAL:
     */
    public boolean isTablePerClassPolicy() {
        return false;
    }
    
    /**
     * INTERNAL:
     * Select all objects for a concrete descriptor.
     */
    protected Object selectAllObjects(ReadAllQuery query) {
        ReadAllQuery concreteQuery = (ReadAllQuery) query.deepClone();
        concreteQuery.setReferenceClass(descriptor.getJavaClass());
        concreteQuery.setDescriptor(descriptor);
        
        // Avoid cloning the query again ...
        concreteQuery.setIsExecutionClone(true);
        concreteQuery.getExpressionBuilder().setQueryClassAndDescriptor(descriptor.getJavaClass(), descriptor);
            
        // Update the selection criteria if needed as well and don't lose 
        // the translation row.
        if (concreteQuery.getQueryMechanism().getSelectionCriteria() != null) {
            //make sure query builder is used for the selection criteria as deepClone will create
            //two separate builders.
            concreteQuery.setSelectionCriteria(concreteQuery.getQueryMechanism().getSelectionCriteria().rebuildOn(concreteQuery.getExpressionBuilder()));
            return query.getSession().executeQuery(concreteQuery, query.getTranslationRow());
        }
        
        return query.getSession().executeQuery(concreteQuery);
    }
    
    /**
     * INTERNAL:
     * Select all objects for an interface descriptor.
     * This is accomplished by selecting for all of the concrete classes and then merging the objects.
     */
    public Object selectAllObjectsUsingMultipleTableSubclassRead(ReadAllQuery query) throws DatabaseException {
        ContainerPolicy containerPolicy = query.getContainerPolicy();
        Object objects = containerPolicy.containerInstance(1);
        int size = this.childDescriptors.size();
        for (int index = 0; index < size; index++) {
            ClassDescriptor descriptor = (ClassDescriptor)this.childDescriptors.get(index);
            objects = containerPolicy.concatenateContainers(
                    objects, descriptor.getInterfacePolicy().selectAllObjects(query), query.getSession());
        }

        return objects;
    }
    
    /**
     * INTERNAL:
     * Select one object of any concrete subclass.
     */     
    protected Object selectOneObject(ReadObjectQuery query) throws DescriptorException {
        ReadObjectQuery concreteQuery = (ReadObjectQuery) query.clone();
        Class javaClass = descriptor.getJavaClass();
        concreteQuery.setReferenceClass(javaClass);
        concreteQuery.setDescriptor(descriptor);
        return query.getSession().executeQuery(concreteQuery, concreteQuery.getTranslationRow());
    }
    
    /**
     * INTERNAL:
     * Select one object of any concrete subclass.
     */
    public Object selectOneObjectUsingMultipleTableSubclassRead(ReadObjectQuery query) throws DatabaseException, QueryException {
        Object object = null;
        for (Enumeration childDescriptors = getChildDescriptors().elements(); childDescriptors.hasMoreElements() && (object == null);) {
            ClassDescriptor descriptor = (ClassDescriptor)childDescriptors.nextElement();
            object = descriptor.getInterfacePolicy().selectOneObject(query);
        }

        return object;
    }

    /**
     * INTERNAL:
     * Set the descriptor.
     */
    public void setDescriptor(ClassDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    /**
     * INTERNAL:
     * Sets the implementor descriptor class.
     */
    public void setImplementorDescriptor(Class implementorDescriptor) {
        this.implementorDescriptor = implementorDescriptor;
    }

    /**
     * INTERNAL:
     * Sets the implementor descriptor class name.
     */
    public void setImplementorDescriptorClassName(String implementorDescriptorClassName) {
        this.implementorDescriptorClassName = implementorDescriptorClassName;
    }

    /**
     * Set the Vector to store parent interfaces.
     */
    public void setParentInterfaces(Vector parentInterfaces) {
        this.parentInterfaces = parentInterfaces;
    }

    public void setParentInterfaceNames(Vector parentInterfaceNames) {
        this.parentInterfaceNames = parentInterfaceNames;
    }

    /**
     * INTERNAL:
     * Returns true if this descriptor should be ignored and the implenting
     * descriptor should be used instead.
     */
    public boolean usesImplementorDescriptor() {
        return (implementorDescriptor != null);
    }
}
