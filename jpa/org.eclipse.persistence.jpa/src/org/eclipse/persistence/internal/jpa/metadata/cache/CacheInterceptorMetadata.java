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
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.cache;

import org.eclipse.persistence.descriptors.ClassDescriptor;

import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;

/**
 * Object to hold onto cache interceptor metadata.
 * 
 * @author Gordon Yorke
 * @since EclipseLink 1.0
 */
public class CacheInterceptorMetadata extends ORMetadata {
    protected String m_interceptorClassName;

    /**
     * INTERNAL:
     */
    public CacheInterceptorMetadata() {
        super("<cache-interceptor>");
    }
    
    /**
     * INTERNAL:
     */
    public CacheInterceptorMetadata(MetadataAnnotation cacheInterceptor, MetadataAccessibleObject accessibleObject) {
        super(cacheInterceptor, accessibleObject);
        
        m_interceptorClassName = (String)cacheInterceptor.getAttribute("value");
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

    public String getInterceptorClassName() {
        return m_interceptorClassName;
    }

    public void setInterceptorClassName(String interceptorClass) {
        m_interceptorClassName = interceptorClass;
    }
}
