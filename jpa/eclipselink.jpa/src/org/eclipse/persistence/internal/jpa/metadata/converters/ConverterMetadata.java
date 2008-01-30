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

import org.eclipse.persistence.annotations.Converter;

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
    private Class m_converterClass;
    
    /**
     * INTERNAL:
     */
    public ConverterMetadata() {}
    
    /**
     * INTERNAL:
     */
    public ConverterMetadata(Converter converter) {
    	m_converterClass = converter.converterClass();
    	
        setName(converter.name());
    }
    
    /**
     * INTERNAL:
     * 
     * Converter class is a required attribute, so it can not be null.
     */
    public Class getConverterClass() {
        return m_converterClass;
    }
    
    /**
     * INTERNAL:
     */
    public String getConverterClassName() {
        return getConverterClass().getName();
    }
    
    /**
     * INTERNAL:
     * 
     * Process this converter for the given mapping.
     */
    public void process(DatabaseMapping mapping, DirectAccessor accessor) {
        accessor.setConverterClassName(mapping, getConverterClassName());
    }
    
    /**
     * INTERNAL:
     */
    public void setConverterClass(Class converterClass) {
        m_converterClass = converterClass;
    }
}
