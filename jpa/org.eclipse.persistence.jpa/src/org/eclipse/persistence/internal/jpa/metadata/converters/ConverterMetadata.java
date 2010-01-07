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

import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.MappingAccessor;

import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;

import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;

import org.eclipse.persistence.mappings.DatabaseMapping;

/**
 * INTERNAL:
 * Object to hold onto a custom converter metadata.
 * 
 * @author Guy Pelletier
 * @since TopLink 11g
 */
public class ConverterMetadata extends AbstractConverterMetadata {
    private String m_className;
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public ConverterMetadata() {
        super("<converter>");
    }
    
    /**
     * INTERNAL:
     */
    public ConverterMetadata(MetadataAnnotation converter, MetadataAccessibleObject accessibleObject) {
        super(converter, accessibleObject);

        m_className = (String)converter.getAttribute("converterClass");        
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof ConverterMetadata) {
            ConverterMetadata converter = (ConverterMetadata) objectToCompare;
            
            if (! valuesMatch(getName(), converter.getName())) {
                return false;
            }
            
            return valuesMatch(m_className, converter.getClassName());
        }
        
        return false;
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
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);
        
        m_className = initXMLClassName(m_className).getName();
    }
    
    /**
     * INTERNAL:
     * Process this converter for the given mapping.
     */
    public void process(DatabaseMapping mapping, MappingAccessor accessor, MetadataClass referenceClass, boolean isForMapKey) {
        setConverterClassName(mapping, getClassName(), isForMapKey);
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setClassName(String className) {
        m_className = className;
    }
}
