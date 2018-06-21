/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.descriptors;

import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.ComplexQueryResult;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;

/**
 * <b>Purpose</b>: Allows for a descriptor's implemented interfaces to be configured.
 * Generally Interface Descriptors are used for 1 of 2 reasons:<p>
 *      a. Interface descriptors can be used to query across a set of classes that do not share a table.<br>
 *      b. As a target of a variable one to one mapping.
 *
 * @since TopLink for Java 2.0
 */
public class InterfacePolicy implements Serializable, Cloneable {
    protected List<Class> parentInterfaces;
    protected List<String> parentInterfaceNames;
    protected List<ClassDescriptor> parentDescriptors;
    protected List<ClassDescriptor> childDescriptors;
    protected ClassDescriptor descriptor;
    protected Class implementorDescriptor;
    protected String implementorDescriptorClassName;

    /**
     * INTERNAL:
     * Create a new policy.
     * Only descriptor involved in interface should have a policy.
     */
    public InterfacePolicy() {
        this.childDescriptors = new ArrayList();
        this.parentInterfaces = new ArrayList(2);
        this.parentInterfaceNames = new ArrayList(2);
        this.parentDescriptors = new ArrayList(2);
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
        getChildDescriptors().add(childDescriptor);
    }

    /**
     * INTERNAL:
     * Add parent descriptor.
     */
    public void addParentDescriptor(ClassDescriptor parentDescriptor) {
        getParentDescriptors().add(parentDescriptor);
    }

    /**
     * PUBLIC:
     * Add the parent Interface class.
     *
     * This method should be called once for each parent Interface of the Descriptor.
     */
    public void addParentInterface(Class parentInterface) {
        getParentInterfaces().add(parentInterface);
    }

    public void addParentInterfaceName(String parentInterfaceName) {
        getParentInterfaceNames().add(parentInterfaceName);
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
    public List<ClassDescriptor> getChildDescriptors() {
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
    public List<ClassDescriptor> getParentDescriptors() {
        return parentDescriptors;
    }

    /**
     * INTERNAL:
     * Return the list of parent interfaces.
     */
    public List<Class> getParentInterfaces() {
        return parentInterfaces;
    }

    public List<String> getParentInterfaceNames() {
        if (parentInterfaceNames.isEmpty() && !parentInterfaces.isEmpty()) {
            for (int i = 0; i < parentInterfaces.size(); i++) {
                parentInterfaceNames.add(parentInterfaces.get(i).getName());
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
        List<Class> newParentInterfaces = new ArrayList(2);
        for (Iterator iterator = getParentInterfaceNames().iterator(); iterator.hasNext(); ) {
            String interfaceName = (String)iterator.next();
            Class interfaceClass = null;
            try {
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    try {
                        interfaceClass = AccessController.doPrivileged(new PrivilegedClassForName(interfaceName, true, classLoader));
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
        ReadAllQuery concreteQuery = (ReadAllQuery)prepareQuery(query);
        return query.getSession().executeQuery(concreteQuery, query.getTranslationRow());
    }

    /**
     * INTERNAL:
     * Select all objects for a concrete descriptor.
     */
    protected ObjectLevelReadQuery prepareQuery(ObjectLevelReadQuery query) {
        ObjectLevelReadQuery concreteQuery = null;
        Class javaClass = this.descriptor.getJavaClass();
        // PERF: First check the subclass query cache for the prepared query.
        boolean shouldPrepare = query.shouldPrepare();
        if (shouldPrepare) {
            concreteQuery = (ObjectLevelReadQuery)query.getConcreteSubclassQueries().get(javaClass);
        }
        if (concreteQuery == null) {
            concreteQuery = (ObjectLevelReadQuery)query.deepClone();
            concreteQuery.setReferenceClass(javaClass);
            concreteQuery.setDescriptor(this.descriptor);
            // Disable query cache from implementation queries.
            concreteQuery.setQueryResultsCachePolicy(null);
            concreteQuery.getExpressionBuilder().setQueryClassAndDescriptor(javaClass, this.descriptor);

            // Update the selection criteria if needed as well and don't lose
            // the translation row.
            if (concreteQuery.getQueryMechanism().getSelectionCriteria() != null) {
                //make sure query builder is used for the selection criteria as deepClone will create
                //two separate builders.
                concreteQuery.getExpressionBuilder().setSession(query.getSession().getRootSession(concreteQuery));
                concreteQuery.setSelectionCriteria(concreteQuery.getQueryMechanism().getSelectionCriteria().rebuildOn(concreteQuery.getExpressionBuilder()));
            }
            if (concreteQuery.hasAdditionalFields()) {
                List rebuiltFields = new ArrayList(concreteQuery.getAdditionalFields().size());
                for (Object field : concreteQuery.getAdditionalFields()) {
                    if (field instanceof Expression) {
                        rebuiltFields.add(((Expression)field).rebuildOn(concreteQuery.getExpressionBuilder()));
                    } else {
                        rebuiltFields.add(field);
                    }
                }
                concreteQuery.setAdditionalFields(rebuiltFields);
            }
            if (shouldPrepare) {
                concreteQuery.setTranslationRow(null);
                if (concreteQuery.isReadObjectQuery()) {
                    ((ReadObjectQuery)concreteQuery).clearSelectionId();
                }
                concreteQuery.checkPrepare(query.getSession(), query.getTranslationRow());
                query.getConcreteSubclassQueries().put(javaClass, concreteQuery);
                concreteQuery = (ObjectLevelReadQuery)concreteQuery.clone();
                concreteQuery.setTranslationRow(query.getTranslationRow());
            }
            concreteQuery.setIsExecutionClone(true);
        } else {
            concreteQuery = (ObjectLevelReadQuery)concreteQuery.clone();
            concreteQuery.setIsExecutionClone(true);
            concreteQuery.setTranslationRow(query.getTranslationRow());
        }

        return concreteQuery;
    }

    /**
     * INTERNAL:
     * Select all objects for an interface descriptor.
     * This is accomplished by selecting for all of the concrete classes and then merging the objects.
     */
    public Object selectAllObjectsUsingMultipleTableSubclassRead(ReadAllQuery query) throws DatabaseException {
        ContainerPolicy containerPolicy = query.getContainerPolicy();
        Object objects = containerPolicy.containerInstance();
        if (query.shouldIncludeData()) {
            ComplexQueryResult result = new ComplexQueryResult();
            result.setResult(objects);
            result.setData(new ArrayList());
            objects = result;
        }
        int size = this.childDescriptors.size();
        for (int index = 0; index < size; index++) {
            ClassDescriptor descriptor = this.childDescriptors.get(index);
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
        ReadObjectQuery concreteQuery = (ReadObjectQuery)prepareQuery(query);
        return query.getSession().executeQuery(concreteQuery, concreteQuery.getTranslationRow());
    }

    /**
     * INTERNAL:
     * Select one object of any concrete subclass.
     */
    public Object selectOneObjectUsingMultipleTableSubclassRead(ReadObjectQuery query) throws DatabaseException, QueryException {
        int size = this.childDescriptors.size();
        for (int index = 0; index < size; index++) {
            ClassDescriptor descriptor = this.childDescriptors.get(index);
            Object object = descriptor.getInterfacePolicy().selectOneObject(query);
            if (object != null) {
                return object;
            }
        }

        return null;
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
    public void setParentInterfaces(List<Class> parentInterfaces) {
        this.parentInterfaces = parentInterfaces;
    }

    public void setParentInterfaceNames(List<String> parentInterfaceNames) {
        this.parentInterfaceNames = parentInterfaceNames;
    }

    /**
     * INTERNAL:
     * Returns true if this descriptor should be ignored and the implementing
     * descriptor should be used instead.
     */
    public boolean usesImplementorDescriptor() {
        return (implementorDescriptor != null);
    }
}
