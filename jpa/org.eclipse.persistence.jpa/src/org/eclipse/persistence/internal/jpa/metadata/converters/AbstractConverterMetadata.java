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
 *     03/27/2009-2.0 Guy Pelletier 
 *       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.converters;


import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;

/**
 * INTERNAL:
 * Abstract metadata converter.
 * 
 * @author Guy Pelletier
 * @since TopLink 11g
 */
public abstract class AbstractConverterMetadata extends MetadataConverter {
    private String m_name;
    
    /**
     * INTERNAL:
     */
    public AbstractConverterMetadata(String xmlElement) {
        super(xmlElement);
    }
    
    /**
     * INTERNAL:
     */
    public AbstractConverterMetadata(MetadataAnnotation converter, MetadataAccessibleObject accessibleObject) {
        super(converter, accessibleObject);
        
        m_name = (String) converter.getAttribute("name");
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public String getIdentifier() {
        return m_name;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getName() {
        return m_name;
    }
    
    /**
     * INTERNAL:
     */
    public boolean isStructConverter() {
        return false;
    }  
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setName(String name) {
        m_name = name;
    }
}
