/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.columns;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;

/**
 * Object to hold onto discriminator column metadata.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class DiscriminatorColumnMetadata {
	private DiscriminatorType m_discriminatorType;
	private Integer m_length;
	private String m_columnDefinition;
	private String m_name;
	
    /**
     * INTERNAL:
     */
    public DiscriminatorColumnMetadata() {}
    
    /**
     * INTERNAL:
     */
    public DiscriminatorColumnMetadata(DiscriminatorColumn discriminatorColumn) {    	
    	if (discriminatorColumn != null) {
    		m_columnDefinition = discriminatorColumn.columnDefinition();
    		m_discriminatorType = discriminatorColumn.discriminatorType();
    		m_length = discriminatorColumn.length();
    		m_name = discriminatorColumn.name();
    	}
    }

    /**
     * INTERNAL:
     */
    public String getColumnDefinition() {
    	return m_columnDefinition;
    }
    
    /**
     * INTERNAL:
     */
    public DiscriminatorType getDiscriminatorType() {
    	return m_discriminatorType;
    }
    
    /**
     * INTERNAL:
     */
    public Integer getLength() {
    	return m_length;
    }
    
    /**
     * INTERNAL:
     */
    public String getName() {
    	return m_name;
    }
    
    /**
     * INTERNAL:
     */
    public void setColumnDefinition(String columnDefinition) {
    	m_columnDefinition = columnDefinition;
    }
    
    /**
     * INTERNAL:
     */
    public void setDiscriminatorType(DiscriminatorType descriminatorType) {
    	m_discriminatorType = descriminatorType;
    }
    
    /**
     * INTERNAL:
     */
    public void setLength(Integer length) {
    	m_length = length;
    }
    
    /**
     * INTERNAL:
     */
    public void setName(String name) {
    	m_name = name;
    }
}
