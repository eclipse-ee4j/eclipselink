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
 *     04/27/2010-2.1 Guy Pelletier 
 *       - 309856: MappedSuperclasses from XML are not being initialized properly
 *     03/24/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 1)
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.cache;

import org.eclipse.persistence.descriptors.ClassDescriptor;

import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;

/**
 * Object to hold onto cache interceptor metadata.
 * 
 * Key notes:
 * - any metadata mapped from XML to this class must be compared in the
 *   equals method.
 * - when loading from annotations, the constructor accepts the metadata
 *   accessor this metadata was loaded from. Used it to look up any 
 *   'companion' annotation needed for processing.
 * - methods should be preserved in alphabetical order.
 * 
 * @author Gordon Yorke
 * @since EclipseLink 1.0
 */
public class CacheInterceptorMetadata extends ORMetadata {
    protected String m_interceptorClassName;

    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public CacheInterceptorMetadata() {
        super("<cache-interceptor>");
    }
    
    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public CacheInterceptorMetadata(MetadataAnnotation cacheInterceptor, MetadataAccessor accessor) {
        super(cacheInterceptor, accessor);
        
        m_interceptorClassName = (String)cacheInterceptor.getAttribute("value");
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof CacheInterceptorMetadata) {
            CacheInterceptorMetadata cacheInterceptor = (CacheInterceptorMetadata) objectToCompare;
            return valuesMatch(m_interceptorClassName, cacheInterceptor.getInterceptorClassName());
        }
        
        return false;
    }
    
    /**
     * INTERNAL
     * Used for OX mapping.
     */
    public String getInterceptorClassName() {
        return m_interceptorClassName;
    }
    
    /**
     * INTERNAL:
     */
    public void process(MetadataDescriptor descriptor, MetadataClass javaClass) {
        // Set the cache flag on the metadata Descriptor.
        descriptor.setHasCacheInterceptor();
        
        // Process the cache metadata.
        ClassDescriptor classDescriptor = descriptor.getClassDescriptor();
        
        classDescriptor.setCacheInterceptorClassName(m_interceptorClassName);
    }

    /**
     * INTERNAL
     * Used for OX mapping.
     */
    public void setInterceptorClassName(String interceptorClass) {
        m_interceptorClassName = interceptorClass;
    }
}
