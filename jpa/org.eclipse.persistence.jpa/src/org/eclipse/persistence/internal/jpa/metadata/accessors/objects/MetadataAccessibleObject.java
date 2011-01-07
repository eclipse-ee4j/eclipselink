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
 *     03/08/2010-2.1 Guy Pelletier 
 *       - 303632: Add attribute-type for mapping attributes to EclipseLink-ORM
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors.objects;

import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;

/**
 * INTERNAL:
 * Parent object that is used to hold onto a valid JPA decorated method
 * field, class or file.
 * 
 * @author Guy Pelletier
 * @since TopLink 10.1.3/EJB 3.0 Preview
 */
public abstract class MetadataAccessibleObject {
    protected MetadataFactory m_factory;
    
    /**
     * INTERNAL:
     */
    public MetadataAccessibleObject(MetadataFactory factory) {
        m_factory = factory;
    }
    
    /**
     * INTERNAL:
     * Return the attribute name of this accessible object.
     */
    public abstract String getAttributeName();
    
    /**
     * INTERNAL:
     */
    public MetadataLogger getLogger() {
        return m_factory.getLogger();
    }

    /**
     * INTERNAL:
     */
    public MetadataFactory getMetadataFactory() {
        return m_factory;
    }
    
    /**
     * INTERNAL:
     */
    public MetadataClass getMetadataClass(String className) {
        return m_factory.getMetadataClass(className);
    }

    /**
     * INTERNAL:
     */
    public void setMetadataFactory(MetadataFactory factory) {
        m_factory = factory;
    }
}
