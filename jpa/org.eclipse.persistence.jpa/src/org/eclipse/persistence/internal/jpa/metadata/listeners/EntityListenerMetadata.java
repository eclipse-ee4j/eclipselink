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
 *     04/02/2009-2.0 Guy Pelletier 
 *       - 270853: testBeerLifeCycleMethodAnnotationIgnored within xml merge testing need to be relocated
 *     01/05/2010-2.1 Guy Pelletier 
 *       - 211324: Add additional event(s) support to the EclipseLink-ORM.XML Schema
 *     04/27/2010-2.1 Guy Pelletier 
 *       - 309856: MappedSuperclasses from XML are not being initialized properly
 *     07/15/2010-2.2 Guy Pelletier 
 *       -311395 : Multiple lifecycle callback methods for the same lifecycle event
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.listeners;

import java.lang.reflect.Method;

import java.security.AccessController;
import java.security.PrivilegedActionException;

import java.util.HashSet;

import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

import org.eclipse.persistence.descriptors.DescriptorEventListener;

import org.eclipse.persistence.exceptions.ValidationException;

import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;

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

/**
 * A MetadataEntityListener and is placed on the owning entity's descriptor. 
 * Callback methods from an EntityListener require a signature on the method. 
 * Namely, they must have an Object parameter.
 * 
 * @author Guy Pelletier
 * @since TopLink 10.1.3/EJB 3.0 Preview
 */
public class EntityListenerMetadata extends ORMetadata implements Cloneable {
    // Note: Any metadata mapped from XML to this class must be compared in the equals method.

    private MetadataClass m_entityListenerClass;
    
    protected EntityListener m_listener;
    
    private String m_className;
    private String m_postLoad;
    private String m_postPersist;
    private String m_postRemove;
    private String m_postUpdate;
    private String m_prePersist;
    private String m_preRemove;
    private String m_preUpdate;

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public EntityListenerMetadata() {
        super("<entity-listener>");
    }

    /**
     * INTERNAL:
     */
    public EntityListenerMetadata(MetadataAnnotation entityListeners, MetadataClass entityListenerClass, MetadataAccessibleObject accessibleObject) {
        super(entityListeners, accessibleObject);
        
        m_entityListenerClass = entityListenerClass;
    }

    /**
     * INTERNAL:
     * This method should be called when dealing with default listeners.
     */
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
        if (objectToCompare instanceof EntityListenerMetadata) {
            EntityListenerMetadata entityListener = (EntityListenerMetadata) objectToCompare;
            
            if (! valuesMatch(m_className, entityListener.getClassName())) {
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
        HashSet candidateMethods = new HashSet();
        Class listenerClass = m_listener.getListenerClass();
        
        // Add all the declared methods ...
        Method[] declaredMethods = getDeclaredMethods(listenerClass);
        for (int i = 0; i < declaredMethods.length; i++) {
            candidateMethods.add(declaredMethods[i]);
        }
        
        // Now add any public methods from superclasses ...
        Method[] methods = getMethods(listenerClass);
        for (int i = 0; i < methods.length; i++) {
            if (candidateMethods.contains(methods[i])) {
                continue;
            }
            
            candidateMethods.add(methods[i]);
        }
        
        return (Method[]) candidateMethods.toArray(new Method[candidateMethods.size()]);
    }
    
    /**
     * INTERNAL:
     * Load a class from a given class name.
     */
    Class getClass(MetadataClass metadataClass, ClassLoader loader) {
        String classname = metadataClass.getName();
        
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
    Method[] getDeclaredMethods(Class cls) {
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
     */
    @Override
    public String getIdentifier() {
        return m_className;
    }
    
    /**
     * INTERNAL:
     */
    Object getInstance(Class cls) {
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
    Method[] getMethods(Class cls) {
        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
            try {
                return (Method[])AccessController.doPrivileged(new PrivilegedGetMethods(cls));
            } catch (PrivilegedActionException exception) {
                return null;
            }
        } else {
            return PrivilegedAccessHelper.getMethods(cls);
        }
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
    public void process(MetadataDescriptor descriptor, ClassLoader loader, boolean isDefaultListener) {
        // Make sure the entityListenerClass is initialized (default listeners
        // are cloned and m_entityListenerClass may be null)
        if (m_entityListenerClass == null) {
            m_entityListenerClass = getMetadataFactory().getMetadataClass(m_className);
        }

        DescriptorEventListener listener;
        Object entityListenerClassInstance = getInstance(getClass(m_entityListenerClass, loader));
        
        if (m_entityListenerClass.extendsInterface(DescriptorEventListener.class)) {
            listener = (DescriptorEventListener) entityListenerClassInstance;
        } else {
            // Initialize the listener class before processing the callback methods.
            m_listener = new EntityListener(entityListenerClassInstance, getClass(descriptor.getJavaClass(), loader));
            
            // Process the callback methods defined from XML and annotations.
            processCallbackMethods(getCandidateCallbackMethodsForEntityListener(), descriptor);
            
            listener = m_listener;
        }
        
        // Add the listener to the descriptor.
        if (isDefaultListener) {
            descriptor.addDefaultEventListener(listener);
        } else {
            descriptor.addEntityListenerEventListener(listener);
        }
    }
    
    /**
     * INTERNAL:
     * Process the the callback methods. The XML defined callback methods are
     * always added first, followed by those defined by annotations (only if not
     * already defined in XML)
     */
    protected void processCallbackMethods(Method[] methods, MetadataDescriptor descriptor) {    
        // 1 - Set the XML specified methods first.
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
        for (Method method : methods) {
            MetadataMethod metadataMethod = getMetadataClass(method.getDeclaringClass()).getMethod(method.getName(), method.getParameterTypes());
            // Metadata method can be null when dealing with jdk methods: equals, notify, toString, wait etc.. 
            if (metadataMethod != null) {
                if (metadataMethod.isAnnotationPresent(PostLoad.class, descriptor) && m_postLoad == null) {
                    setPostLoad(method);
                }
                
                if (metadataMethod.isAnnotationPresent(PostPersist.class, descriptor) && m_postPersist == null) {
                    setPostPersist(method);
                }
                
                if (metadataMethod.isAnnotationPresent(PostRemove.class, descriptor) && m_postRemove == null) {
                    setPostRemove(method);
                }
                
                if (metadataMethod.isAnnotationPresent(PostUpdate.class, descriptor) && m_postUpdate == null) {
                    setPostUpdate(method);
                }
                
                if (metadataMethod.isAnnotationPresent(PrePersist.class, descriptor) && m_prePersist == null) {
                    setPrePersist(method);
                }
                
                if (metadataMethod.isAnnotationPresent(PreRemove.class, descriptor) && m_preRemove == null) {
                    setPreRemove(method);
                }
                
                if (metadataMethod.isAnnotationPresent(PreUpdate.class, descriptor) && m_preUpdate == null) {
                    setPreUpdate(method);
                }
            }
        }
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setClassName(String className) {
        m_className = className;
    }

    /**
     * INTERNAL:
     * Set the post load event method on the listener.
     */
    protected void setPostLoad(Method method) {
        // bug 259404: PostClone is called for all objects when registered with the unitOfWork
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
        m_listener.setPostInsertMethod(method); 
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
        m_listener.setPostDeleteMethod(method);
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
