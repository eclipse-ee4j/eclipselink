/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
 *     01/05/2010-2.1 Guy Pelletier 
 *       - 211324: Add additional event(s) support to the EclipseLink-ORM.XML Schema
 *     12/01/2010-2.2 Guy Pelletier 
 *       - 331234: xml-mapping-metadata-complete overriden by metadata-complete specification 
 *     03/24/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 1)
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
public class EntityClassListenerMetadata extends EntityListenerMetadata {
    private EntityAccessor m_accessor;
    private MetadataDescriptor m_descriptor;
    
    /**
     * INTERNAL:
     */
    public EntityClassListenerMetadata(EntityAccessor accessor) {
        super(null, null, accessor);
    
        m_accessor = accessor;
        m_descriptor = accessor.getDescriptor();
    }
    
    /**
     * INTERNAL:
     */
    protected void initCallbackMethods(MappedSuperclassAccessor accessor) {
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
        Class accessorClass = getClass(m_accessor.getJavaClass(), loader);
        m_listener = new EntityClassListener(accessorClass);
        
        // Process the callback methods as defined in XML or annotations on the 
        // entity class first.
        // Init callback methods as specified in XML for the entity class 
        // before processing.
        initCallbackMethods(m_accessor);
        processCallbackMethods(getDeclaredMethods(accessorClass), m_accessor);
        
        // Process the callback methods as defined in XML or annotations 
        // on the mapped superclasses if not excluded second. 
        if (! m_descriptor.excludeSuperclassListeners()) {
            for (MappedSuperclassAccessor mappedSuperclass : mappedSuperclasses) {
                Class superClass = getClass(mappedSuperclass.getJavaClass(), loader);
                // Init callback methods as specified in XML for each mapped 
                // superclass before processing.
                initCallbackMethods(mappedSuperclass);
                processCallbackMethods(getDeclaredMethods(superClass), mappedSuperclass);
            }
        }
        
        // Add the listener only if we actually found callback methods.
        if (m_listener.hasCallbackMethods()) {
            m_descriptor.setEntityEventListener(m_listener);
        }
    }
}
