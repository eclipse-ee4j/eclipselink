/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.converters;

import java.lang.reflect.AnnotatedElement;

import org.eclipse.persistence.annotations.Converter;

import org.eclipse.persistence.internal.jpa.metadata.MetadataHelper;
import org.eclipse.persistence.internal.jpa.metadata.accessors.DirectAccessor;

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
     */
    public ConverterMetadata() {
    	setLoadedFromXML();
    }
    
    /**
     * INTERNAL:
     */
    public ConverterMetadata(Converter converter, AnnotatedElement annotatedElement) {
    	setLoadedFromAnnotation();
    	setLocation(annotatedElement);
    	
    	setName(converter.name());
    	
    	m_className = converter.converterClass().getName();        
    }
    
    /**
     * INTERNAL:
     */
    public boolean equals(Object objectToCompare) {
    	if (objectToCompare instanceof ConverterMetadata) {
    		ConverterMetadata converter = (ConverterMetadata) objectToCompare;
    		
    		if (! MetadataHelper.valuesMatch(getName(), converter.getName())) {
    			return false;
    		}
    		
    		return MetadataHelper.valuesMatch(m_className, converter.getClassName());
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
     * Process this converter for the given mapping.
     */
    public void process(DatabaseMapping mapping, DirectAccessor accessor) {
        accessor.setConverterClassName(mapping, getClassName());
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setClassName(String className) {
        m_className = className;
    }
}
