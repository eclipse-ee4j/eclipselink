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
 * Object to hold onto a custom converter metadata.
 * 
 * @author Guy Pelletier
 * @since TopLink 11g
 */
public class MetadataConverter extends MetadataAbstractConverter {
    Converter m_converter;
    
    /**
     * INTERNAL:
     * 
     * Constructor used from XMLConverter.
     */
    protected MetadataConverter() {
        m_converter = null;
    }
    
    /**
     * INTERNAL:
     */
    public MetadataConverter(Converter converter) {
        m_converter = converter;    
    }
    
    /**
     * INTERNAL: (Overridden in XMLConverter)
     * 
     * Converter class is a required attribute, so it can not be null.
     */
    public Class getConverterClass() {
        return m_converter.converterClass();
    }
    
    /**
     * INTERNAL:
     */
    public String getConverterClassName() {
        return getConverterClass().getName();
    }
    
    /**
     * INTERNAL: (Overridden in XMLConverter)
     * 
     * Name is a required attribute, so it can not be null.
     */
    public String getName() {
        return m_converter.name();
    }
    
    /**
     * INTERNAL:
     * 
     * Process this converter for the given mapping.
     */
    public void process(DatabaseMapping mapping, DirectAccessor accessor) {
        accessor.setConverterClassName(mapping, getConverterClassName());
    }
}
