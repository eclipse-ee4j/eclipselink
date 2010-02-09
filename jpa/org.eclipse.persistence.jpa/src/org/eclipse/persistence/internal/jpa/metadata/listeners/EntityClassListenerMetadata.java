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
 *     06/20/2008-1.0 Guy Pelletier 
 *       - 232975: Failure when attribute type is generic
 *     04/02/2009-2.0 Guy Pelletier 
 *       - 270853: testBeerLifeCycleMethodAnnotationIgnored within xml merge testing need to be relocated      
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.listeners;

import java.util.List;

import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EntityAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.MappedSuperclassAccessor;

/**
 * A metadata class to facilitate the processing of lifecycle methods on an
 * entity class (and its mapped superclasses).
 * 
 * @author Guy Pelletier
 * @since TopLink 10.1.3/EJB 3.0 Preview
 */
public class EntityClassListenerMetadata extends EntityListenerMetadata {
    private EntityAccessor m_accessor;
    private MetadataDescriptor m_descriptor;
    
    /**
     * INTERNAL: 
     */
    public EntityClassListenerMetadata(EntityAccessor accessor) {
        super(null, null, accessor.getAccessibleObject());
    
        m_accessor = accessor;
        m_descriptor = accessor.getDescriptor();
        
        // Set any XML defined call back method names.
        setPostLoad(accessor.getPostLoad());
        setPostPersist(accessor.getPostPersist());
        setPostRemove(accessor.getPostRemove());
        setPostUpdate(accessor.getPostUpdate());
        setPrePersist(accessor.getPrePersist());
        setPreRemove(accessor.getPreRemove());
        setPreUpdate(accessor.getPreUpdate());
    }
    
    /**
     * INTERNAL: 
     */
    public void process(List<MappedSuperclassAccessor> mappedSuperclasses, ClassLoader loader) {
        // Create the listener.
        Class accessorClass = getClassForName(m_accessor.getJavaClass().getName(), loader);
        m_listener = new EntityClassListener(accessorClass);
        
        // Process the callback methods as defined in XML or annotations on the 
        // entity class first.
        processCallbackMethods(getDeclaredMethods(accessorClass), m_descriptor);
        
        // Process the callback methods as defined in XML or annotations 
        // on the mapped superclasses if not excluded second. 
        if (! m_descriptor.excludeSuperclassListeners()) {
            for (MappedSuperclassAccessor mappedSuperclass : mappedSuperclasses) {
                Class superClass = getClassForName(mappedSuperclass.getJavaClass().getName(), loader);
                processCallbackMethods(getDeclaredMethods(superClass), m_descriptor);
            }
        }
        
        // Add the listener only if we actually found callback methods.
        if (m_listener.hasCallbackMethods()) {
            m_descriptor.setEntityEventListener(m_listener);
        }
    }
}
