/*
 * Copyright (c) 1998, 2026 Oracle and/or its affiliates. All rights reserved.
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
//     05/16/2008-1.0M8 Guy Pelletier
//       - 218084: Implement metadata merging functionality between mapping files
//     04/02/2009-2.0 Guy Pelletier
//       - 270853: testBeerLifeCycleMethodAnnotationIgnored within xml merge testing need to be relocated
//     01/05/2010-2.1 Guy Pelletier
//       - 211324: Add additional event(s) support to the EclipseLink-ORM.XML Schema
//     04/27/2010-2.1 Guy Pelletier
//       - 309856: MappedSuperclasses from XML are not being initialized properly
//     07/15/2010-2.2 Guy Pelletier
//       -311395 : Multiple lifecycle callback methods for the same lifecycle event
//     12/01/2010-2.2 Guy Pelletier
//       - 331234: xml-mapping-metadata-complete overriden by metadata-complete specification
//     03/24/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 1)
package org.eclipse.persistence.internal.jpa.metadata.listeners;

import java.lang.reflect.Method;

import java.security.AccessController;
import java.security.PrivilegedActionException;

import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.eclipse.persistence.descriptors.DescriptorEventListener;

import org.eclipse.persistence.exceptions.ValidationException;

import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;

import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataMethod;

import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;

import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.internal.security.PrivilegedGetDeclaredMethods;
import org.eclipse.persistence.internal.security.PrivilegedGetMethods;
import org.eclipse.persistence.internal.security.PrivilegedNewInstanceFromClass;

import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_POST_DELETE;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_POST_INSERT;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_POST_UPSERT;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_PRE_DELETE;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_PRE_INSERT;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_PRE_MERGE;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_PRE_UPSERT;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_POST_LOAD;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_POST_PERSIST;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_POST_REMOVE;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_POST_UPDATE;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_PRE_PERSIST;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_PRE_REMOVE;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_PRE_UPDATE;

/**
 * A MetadataEntityListener and is placed on the owning entity's descriptor.
 * Callback methods from an EntityListener require a signature on the method.
 * Namely, they must have an Object parameter.
 * <p>
 * Key notes:
 * - any metadata mapped from XML to this class must be compared in the
 *   equals method.
 * - when loading from annotations, the constructor accepts the metadata
 *   accessor this metadata was loaded from. Used it to look up any
 *   'companion' annotation needed for processing.
 * - methods should be preserved in alphabetical order.
 *
 * @author Guy Pelletier
 * @since TopLink 10.1.3/EJB 3.0 Preview
 */
public class EntityListenerMetadata extends ORMetadata implements Cloneable {
    private MetadataClass m_entityListenerClass;

    protected EntityListener m_listener;
    private int m_listenerType;
    private String m_className;
    private String m_postCreate;
    private String m_postDelete;
    private String m_postInsert;
    private String m_postUpsert;
    private String m_preDelete;
    private String m_preClose;
    private String m_preInsert;
    private String m_preMerge;
    private String m_preUpsert;
    private String m_postLoad;
    private String m_postPersist;
    private String m_postRemove;
    private String m_postUpdate;
    private String m_prePersist;
    private String m_preRemove;
    private String m_preUpdate;

    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public EntityListenerMetadata() {
        super("<entity-listener>");
    }

    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public EntityListenerMetadata(MetadataAnnotation entityListeners, MetadataClass entityListenerClass, MetadataAccessor accessor) {
        super(entityListeners, accessor);

        m_entityListenerClass = entityListenerClass;
    }

    /**
     * INTERNAL:
     * This method should be called when dealing with default listeners.
     */
    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException error) {
            throw new InternalError(error.getMessage());
        }
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof EntityListenerMetadata entityListener) {

            if (! valuesMatch(m_className, entityListener.getClassName())) {
                return false;
            }

            if (! valuesMatch(m_postCreate, entityListener.getPostCreate())
                    || ! valuesMatch(m_preClose, entityListener.getPreClose())) {
                return false;
            }

            if (! valuesMatch(m_postDelete, entityListener.getPostDelete())) {
                return false;
            }

            if (! valuesMatch(m_postInsert, entityListener.getPostInsert())) {
                return false;
            }

            if (! valuesMatch(m_postUpsert, entityListener.getPostUpsert())) {
                return false;
            }

            if (! valuesMatch(m_preDelete, entityListener.getPreDelete())) {
                return false;
            }

            if (! valuesMatch(m_preInsert, entityListener.getPreInsert())) {
                return false;
            }

            if (! valuesMatch(m_preMerge, entityListener.getPreMerge())) {
                return false;
            }

            if (! valuesMatch(m_preUpsert, entityListener.getPreUpsert())) {
                return false;
            }

            if (! valuesMatch(m_postLoad, entityListener.getPostLoad())) {
                return false;
            }

            if (! valuesMatch(m_postPersist, entityListener.getPostPersist())) {
                return false;
            }

            if (! valuesMatch(m_postRemove, entityListener.getPostRemove())) {
                return false;
            }

            if (! valuesMatch(m_postUpdate, entityListener.getPostUpdate())) {
                return false;
            }

            if (! valuesMatch(m_prePersist, entityListener.getPrePersist())) {
                return false;
            }

            if (! valuesMatch(m_preRemove, entityListener.getPreRemove())) {
                return false;
            }

            return valuesMatch(m_preUpdate, entityListener.getPreUpdate());
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (m_className != null ? m_className.hashCode() : 0);
        result = 31 * result + (m_postCreate != null ? m_postCreate.hashCode() : 0);
        result = 31 * result + (m_preClose != null ? m_preClose.hashCode() : 0);
        result = 31 * result + (m_postDelete != null ? m_postDelete.hashCode() : 0);
        result = 31 * result + (m_postInsert != null ? m_postInsert.hashCode() : 0);
        result = 31 * result + (m_postUpsert != null ? m_postUpsert.hashCode() : 0);
        result = 31 * result + (m_preDelete != null ? m_preDelete.hashCode() : 0);
        result = 31 * result + (m_preInsert != null ? m_preInsert.hashCode() : 0);
        result = 31 * result + (m_preMerge != null ? m_preMerge.hashCode() : 0);
        result = 31 * result + (m_preUpsert != null ? m_preUpsert.hashCode() : 0);
        result = 31 * result + (m_postLoad != null ? m_postLoad.hashCode() : 0);
        result = 31 * result + (m_postPersist != null ? m_postPersist.hashCode() : 0);
        result = 31 * result + (m_postRemove != null ? m_postRemove.hashCode() : 0);
        result = 31 * result + (m_postUpdate != null ? m_postUpdate.hashCode() : 0);
        result = 31 * result + (m_prePersist != null ? m_prePersist.hashCode() : 0);
        result = 31 * result + (m_preRemove != null ? m_preRemove.hashCode() : 0);
        result = 31 * result + (m_preUpdate != null ? m_preUpdate.hashCode() : 0);
        return result;
    }

    /**
     * INTERNAL:
     * Find the method in the list where method.getName() == methodName.
     */
    protected Method getCallbackMethod(String methodName, Method[] methods) {
        Method method = getMethod(methodName, methods);

        if (method == null) {
            throw ValidationException.invalidCallbackMethod(m_listener.getListenerClass(), methodName);
        }

        return method;
    }

    /**
     * INTERNAL:
     * Returns a list of methods from the given class, which can have private,
     * protected, package and public access, AND will also return public
     * methods from superclasses.
     */
    Method[] getCandidateCallbackMethodsForEntityListener() {
        List<Method> candidateMethods = new ArrayList<>();
        List<Class<?>> hierarchy = new ArrayList<>();
        for (Class<?> type = m_listener.getListenerClass(); type != Object.class && type != null;
                type = type.getSuperclass()) {
            hierarchy.add(type);
        }
        Collections.reverse(hierarchy);
        for (Class<?> type : hierarchy) {
            Collections.addAll(candidateMethods, getDeclaredMethods(type));
        }
        return candidateMethods.toArray(new Method[0]);
    }

    /**
     * INTERNAL:
     * Load a class from a given class name.
     */
    Class<?> getClass(MetadataClass metadataClass, ClassLoader loader) {
        String classname = metadataClass.getName();

        try {
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                try {
                    return AccessController.doPrivileged(new PrivilegedClassForName<>(classname, true, loader));
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
     * Used for OX mapping.
     */
    public String getClassName() {
        return m_className;
    }

    /**
     * INTERNAL:
     * Get the declared methods from a class using the doPriveleged security
     * access. This call returns all methods (private, protected, package and
     * public) on the given class ONLY. It does not traverse the superclasses.
     */
    Method[] getDeclaredMethods(Class<?> cls) {
        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
            return AccessController.doPrivileged(new PrivilegedGetDeclaredMethods(cls));
        } else {
            return PrivilegedAccessHelper.getDeclaredMethods(cls);
        }
    }

    /**
     * INTERNAL:
     */
    @Override
    public String getIdentifier() {
        return m_className;
    }

    /**
     * INTERNAL:
     */
    protected Object getInstance(Class<?> cls) {
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
        } catch (IllegalAccessException | InstantiationException exception) {
            throw ValidationException.errorInstantiatingClass(cls, exception);
        }
    }

    /** INTERNAL: The qualified name is available without loading the real class. */
    public String getListenerClassName() {
        return m_entityListenerClass == null ? getFullyQualifiedClassName(m_className) : m_entityListenerClass.getName();
    }

    /**
     * INTERNAL:
     * Find the method in the list where method.getName() == methodName.
     */
    Method getMethod(String methodName, Method[] methods) {
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];

            if (method.getName().equals(methodName)) {
                return method;
            }
        }

        return null;
    }

    /**
     * INTERNAL:
     * Get the methods from a class using the doPriveleged security access.
     * This call returns only public methods from the given class and its
     * superclasses.
     */
    Method[] getMethods(Class<?> cls) {
        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
            return AccessController.doPrivileged(new PrivilegedGetMethods(cls));
        } else {
            return PrivilegedAccessHelper.getMethods(cls);
        }
    }

    /** INTERNAL: Freeze the effective XML and annotation callback declarations. */
    public PersistenceUnitListener getPersistenceUnitListener(boolean annotations) {
        MetadataClass listenerClass = getMetadataClass(getListenerClassName(), false);
        if (m_postCreate == null && m_preClose == null
                && (!annotations || !PersistenceUnitListener.hasCallbackAnnotations(listenerClass))) {
            return null;
        }
        return new PersistenceUnitListener(listenerClass.getName(), annotations, m_postCreate, m_preClose);
    }

    /** INTERNAL: Used for XML loading. */
    public String getPostCreate() {
        return m_postCreate;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getPostLoad() {
        return m_postLoad;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getPostPersist() {
        return m_postPersist;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getPostRemove() {
        return m_postRemove;
    }

    /**
     * INTERNAL:
     * Used for OX mapping
     */
    public String getPostUpdate() {
        return m_postUpdate;
    }

    /** INTERNAL: Used for XML loading. */
    public String getPreClose() {
        return m_preClose;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getPrePersist() {
        return m_prePersist;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getPreRemove() {
        return m_preRemove;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getPreUpdate() {
        return m_preUpdate;
    }

    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);

        m_entityListenerClass = initXMLClassName(m_className);
    }

    /**
     * INTERNAL:
     */
    public void setListenerType(int listenerType) {
        m_listenerType = listenerType;
    }

    public void process(ClassAccessor classAccessor, ClassLoader loader, boolean isDefaultListener) {
        // Make sure the entityListenerClass is initialized (default listeners
        // are cloned and m_entityListenerClass may be null)
        if (m_entityListenerClass == null) {
            m_entityListenerClass = getMetadataFactory().getMetadataClass(m_className);
        }
        JPAEntityListenerHolder holder = new JPAEntityListenerHolder();
        holder.setIsDefaultListener(isDefaultListener);
        holder.listenerType = m_listenerType;

        holder.listenerClassName = m_entityListenerClass.getName();

        if (m_entityListenerClass.extendsInterface(DescriptorEventListener.class)) {
            holder.listener = (DescriptorEventListener)getInstance(getClass(m_entityListenerClass, loader));
        } else {
            // Initialize the listener class before processing the callback methods.
            m_listener = new EntityListener(getClass(m_entityListenerClass, loader), getClass(classAccessor.getDescriptorJavaClass(), loader));
            // Process the callback methods defined from XML and annotations.
            processCallbackMethods(getCandidateCallbackMethodsForEntityListener(), classAccessor);
            holder.convertToSerializableMethods(m_listener.getAllEventMethods());
            holder.listener = m_listener;
            m_listener.setOwningSession(getProject().getSession());
        }
        classAccessor.getDescriptor().getClassDescriptor().getEventManager().addEntityListenerHolder(holder);
    }

    /**
     * INTERNAL:
     * Validate annotation callbacks for every parameter type, registering only
     * callbacks compatible with the entity currently being processed.
     */
    private void processCallbackMethod(String event, Method method,
            Map<Class<?>, EntityListener<?>> otherEntityListeners, Consumer<Method> register) {
        Class<?>[] parameters = method.getParameterTypes();
        if (!(m_listener instanceof EntityClassListener) && parameters.length == 1
                && !parameters[0].isPrimitive()
                && !parameters[0].isAssignableFrom(m_listener.getEntityClass())) {
            // Shared listeners may declare callbacks for unrelated entity types.
            // Validate their modifiers and duplicate event/type declarations too,
            // but do not register them for the entity currently being processed.
            otherEntityListeners.computeIfAbsent(parameters[0],
                    type -> new EntityListener<>(m_listener.getListenerClass(), type))
                    .addEventMethod(event, method);
        } else {
            register.accept(method);
        }
    }

    /**
     * INTERNAL:
     * Process the callback methods. The XML defined callback methods are
     * always added first, followed by those defined by annotations (only if not
     * already defined in XML).
     */
    protected void processCallbackMethods(Method[] methods, ClassAccessor classAccessor) {
        // 1 - Set the XML specified methods first.
        if (m_preUpsert != null) {
            setPreUpsert(getCallbackMethod(m_preUpsert, methods));
        }

        if (m_preMerge != null) {
            setPreMerge(getCallbackMethod(m_preMerge, methods));
        }

        if (m_preInsert != null) {
            setPreInsert(getCallbackMethod(m_preInsert, methods));
        }

        if (m_preDelete != null) {
            setPreDelete(getCallbackMethod(m_preDelete, methods));
        }

        if (m_postUpsert != null) {
            setPostUpsert(getCallbackMethod(m_postUpsert, methods));
        }

        if (m_postInsert != null) {
            setPostInsert(getCallbackMethod(m_postInsert, methods));
        }

        if (m_postDelete != null) {
            setPostDelete(getCallbackMethod(m_postDelete, methods));
        }

        if (m_postLoad != null) {
            setPostLoad(getCallbackMethod(m_postLoad, methods));
        }

        if (m_postPersist != null) {
            setPostPersist(getCallbackMethod(m_postPersist, methods));
        }

        if (m_postRemove != null) {
            setPostRemove(getCallbackMethod(m_postRemove, methods));
        }

        if (m_postUpdate != null) {
            setPostUpdate(getCallbackMethod(m_postUpdate, methods));
        }

        if (m_prePersist != null) {
            setPrePersist(getCallbackMethod(m_prePersist, methods));
        }

        if (m_preRemove != null) {
            setPreRemove(getCallbackMethod(m_preRemove, methods));
        }

        if (m_preUpdate != null) {
            setPreUpdate(getCallbackMethod(m_preUpdate, methods));
        }

        // 2 - Set any annotation defined methods second. We should only add
        // add them if they were not overridden in XML.
        Map<Class<?>, EntityListener<?>> otherEntityListeners = new HashMap<>();
        for (Method method : methods) {
            if (EntityListener.isMethodOverridden(method, m_listener.getListenerClass())) {
                continue;
            }
            // Bug 495587 - Ignoring bridge methods
            if (!method.isBridge()) {
                MetadataMethod metadataMethod = getMetadataClass(method.getDeclaringClass().getName(), false).getMethod(method.getName(), method.getParameterTypes());
                // Metadata method can be null when dealing with jdk methods: equals, notify, toString, wait etc..
                if (metadataMethod != null) {
                    if (metadataMethod.isAnnotationPresent(JPA_PRE_UPSERT, classAccessor) && m_preUpsert == null) {
                        processCallbackMethod(JPA_PRE_UPSERT, method, otherEntityListeners, this::setPreUpsert);
                    }

                    if (metadataMethod.isAnnotationPresent(JPA_PRE_MERGE, classAccessor) && m_preMerge == null) {
                        processCallbackMethod(JPA_PRE_MERGE, method, otherEntityListeners, this::setPreMerge);
                    }

                    if (metadataMethod.isAnnotationPresent(JPA_PRE_INSERT, classAccessor) && m_preInsert == null) {
                        processCallbackMethod(JPA_PRE_INSERT, method, otherEntityListeners, this::setPreInsert);
                    }

                    if (metadataMethod.isAnnotationPresent(JPA_PRE_DELETE, classAccessor) && m_preDelete == null) {
                        processCallbackMethod(JPA_PRE_DELETE, method, otherEntityListeners, this::setPreDelete);
                    }

                    if (metadataMethod.isAnnotationPresent(JPA_POST_UPSERT, classAccessor) && m_postUpsert == null) {
                        processCallbackMethod(JPA_POST_UPSERT, method, otherEntityListeners, this::setPostUpsert);
                    }

                    if (metadataMethod.isAnnotationPresent(JPA_POST_INSERT, classAccessor) && m_postInsert == null) {
                        processCallbackMethod(JPA_POST_INSERT, method, otherEntityListeners, this::setPostInsert);
                    }

                    if (metadataMethod.isAnnotationPresent(JPA_POST_DELETE, classAccessor) && m_postDelete == null) {
                        processCallbackMethod(JPA_POST_DELETE, method, otherEntityListeners, this::setPostDelete);
                    }

                    if (metadataMethod.isAnnotationPresent(JPA_POST_LOAD, classAccessor) && m_postLoad == null) {
                        processCallbackMethod(JPA_POST_LOAD, method, otherEntityListeners, this::setPostLoad);
                    }

                    if (metadataMethod.isAnnotationPresent(JPA_POST_PERSIST, classAccessor) && m_postPersist == null) {
                        processCallbackMethod(JPA_POST_PERSIST, method, otherEntityListeners, this::setPostPersist);
                    }

                    if (metadataMethod.isAnnotationPresent(JPA_POST_REMOVE, classAccessor) && m_postRemove == null) {
                        processCallbackMethod(JPA_POST_REMOVE, method, otherEntityListeners, this::setPostRemove);
                    }

                    if (metadataMethod.isAnnotationPresent(JPA_POST_UPDATE, classAccessor) && m_postUpdate == null) {
                        processCallbackMethod(JPA_POST_UPDATE, method, otherEntityListeners, this::setPostUpdate);
                    }

                    if (metadataMethod.isAnnotationPresent(JPA_PRE_PERSIST, classAccessor) && m_prePersist == null) {
                        processCallbackMethod(JPA_PRE_PERSIST, method, otherEntityListeners, this::setPrePersist);
                    }

                    if (metadataMethod.isAnnotationPresent(JPA_PRE_REMOVE, classAccessor) && m_preRemove == null) {
                        processCallbackMethod(JPA_PRE_REMOVE, method, otherEntityListeners, this::setPreRemove);
                    }

                    if (metadataMethod.isAnnotationPresent(JPA_PRE_UPDATE, classAccessor) && m_preUpdate == null) {
                        processCallbackMethod(JPA_PRE_UPDATE, method, otherEntityListeners, this::setPreUpdate);
                    }
                }
            }
        }
    }

    /**
     * INTERNAL: Used for XML loading.
     */
    public String getPostDelete() {
        return m_postDelete;
    }

    public void setPostDelete(String methodName) {
        m_postDelete = methodName;
    }

    protected void setPostDelete(Method method) {
        m_listener.setPostDeleteMethod(method);
    }

    /**
     * INTERNAL: Used for XML loading.
     */
    public String getPostInsert() {
        return m_postInsert;
    }

    public void setPostInsert(String methodName) {
        m_postInsert = methodName;
    }

    protected void setPostInsert(Method method) {
        m_listener.setPostInsertMethod(method);
    }

    /**
     * INTERNAL: Used for XML loading.
     */
    public String getPostUpsert() {
        return m_postUpsert;
    }

    public void setPostUpsert(String methodName) {
        m_postUpsert = methodName;
    }

    protected void setPostUpsert(Method method) {
        m_listener.setPostUpsertMethod(method);
    }

    /**
     * INTERNAL: Used for XML loading.
     */
    public String getPreDelete() {
        return m_preDelete;
    }

    public void setPreDelete(String methodName) {
        m_preDelete = methodName;
    }

    protected void setPreDelete(Method method) {
        m_listener.setPreDeleteMethod(method);
    }

    /**
     * INTERNAL: Used for XML loading.
     */
    public String getPreInsert() {
        return m_preInsert;
    }

    public void setPreInsert(String methodName) {
        m_preInsert = methodName;
    }

    protected void setPreInsert(Method method) {
        m_listener.setPreInsertMethod(method);
    }

    /**
     * INTERNAL: Used for XML loading.
     */
    public String getPreMerge() {
        return m_preMerge;
    }

    public void setPreMerge(String methodName) {
        m_preMerge = methodName;
    }

    protected void setPreMerge(Method method) {
        m_listener.setPreMergeMethod(method);
    }

    /**
     * INTERNAL: Used for XML loading.
     */
    public String getPreUpsert() {
        return m_preUpsert;
    }

    public void setPreUpsert(String methodName) {
        m_preUpsert = methodName;
    }

    protected void setPreUpsert(Method method) {
        m_listener.setPreUpsertMethod(method);
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setClassName(String className) {
        m_className = className;
    }

    /** INTERNAL: Used for XML loading. */
    public void setPostCreate(String methodName) {
        m_postCreate = methodName;
    }

    /**
     * INTERNAL:
     * Set the post load event method on the listener.
     */
    protected void setPostLoad(Method method) {
        // bug 259404: PostClone is called for all objects when registered with the unitOfWork
        m_listener.setPostLoadMethod(method);
        m_listener.setPostCloneMethod(method);
        m_listener.setPostRefreshMethod(method);
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setPostLoad(String postLoad) {
        m_postLoad = postLoad;
    }

    /**
     * INTERNAL:
     * Set the post persist event method on the listener.
     */
    protected void setPostPersist(Method method) {
        m_listener.setPostPersistMethod(method);
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setPostPersist(String postPersist) {
        m_postPersist = postPersist;
    }

    /**
     * INTERNAL:
     * Set the post remove event method on the listener.
     */
    protected void setPostRemove(Method method) {
        m_listener.setPostRemoveMethod(method);
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setPostRemove(String postRemove) {
        m_postRemove = postRemove;
    }

    /**
     * INTERNAL:
     * * Set the post update event method on the listener.
     */
    protected void setPostUpdate(Method method) {
        m_listener.setPostUpdateMethod(method);
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setPostUpdate(String postUpdate) {
        m_postUpdate = postUpdate;
    }

    /** INTERNAL: Used for XML loading. */
    public void setPreClose(String methodName) {
        m_preClose = methodName;
    }

    /**
     * INTERNAL:
     * Set the pre persist event method on the listener.
     */
    protected void setPrePersist(Method method) {
        m_listener.setPrePersistMethod(method);
    }

    /**
     * INTERNAL:
     * Used for OX mapping
     */
    public void setPrePersist(String prePersist) {
        m_prePersist = prePersist;
    }

    /**
     * INTERNAL:
     * Set the pre remove event method on the listener.
     */
    protected void setPreRemove(Method method) {
        m_listener.setPreRemoveMethod(method);
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setPreRemove(String preRemove) {
        m_preRemove = preRemove;
    }

    /**
     * INTERNAL:
     * Set the pre update event method on the listener.
     */
    protected void setPreUpdate(Method method) {
        m_listener.setPreUpdateWithChangesMethod(method);
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setPreUpdate(String preUpdate) {
        m_preUpdate = preUpdate;
    }
}
