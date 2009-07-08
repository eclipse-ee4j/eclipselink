/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
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
package org.eclipse.persistence.internal.jpa.metadata.accessors.objects;

import java.lang.reflect.AnnotatedElement;
import java.net.URL;

import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;

/**
 * INTERNAL:
 * Parent object that is used to hold onto a valid JPA decorated method
 * field, class or file.
 * 
 * @author Guy Pelletier
 * @since TopLink 10.1.3/EJB 3.0 Preview
 */
public abstract class MetadataAccessibleObject {
    private MetadataLogger m_logger;
    
    // Location could be 2 things:
    // 1 - URL to a mapping file
    // 2 - Annotated element (Class, Method or Field)
    private Object m_location;
    
    private XMLEntityMappings m_entityMappings;
    
    /**
     * INTERNAL:
     */
    public MetadataAccessibleObject(AnnotatedElement annotatedElement, MetadataLogger logger) {
        m_location = annotatedElement;
        m_logger = logger;
    }
    
    /**
     * INTERNAL:
     */
    public MetadataAccessibleObject(URL mappingFile, XMLEntityMappings entityMappings) {
        m_location = mappingFile;
        m_logger = entityMappings.getLogger();
        m_entityMappings = entityMappings;
    }
    
    /**
     * INTERNAL:
     */
    public XMLEntityMappings getEntityMappings() {
        return m_entityMappings;
    }
    
    /**
     * INTERNAL:
     * Return the element of this accessible object.
     */
    public abstract Object getElement();
    
    /**
     * INTERNAL:
     */
    public Object getLocation() {
        return m_location;
    }
    
    /**
     * INTERNAL:
     */
    public MetadataLogger getLogger() {
        return m_logger;
    }
    
    /**
     * INTERNAL:
     * Set the location if one is not already set.
     */
    public void setLocation(Object location) {
        if (m_location == null) {
            m_location = location;
        }
    }
}
