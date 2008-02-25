/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
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
	private Enum m_discriminatorType;
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
    public DiscriminatorColumnMetadata(Object discriminatorColumn) {    	
    	if (discriminatorColumn != null) {
    		m_columnDefinition =  (String)MetadataHelper.invokeMethod("columnDefinition", discriminatorColumn, (Object[])null);
    		m_discriminatorType = (Enum)MetadataHelper.invokeMethod("discriminatorType", discriminatorColumn, (Object[])null); 
    		m_length = (Integer)MetadataHelper.invokeMethod("length", discriminatorColumn, (Object[])null);
    		m_name = (String)MetadataHelper.invokeMethod("name", discriminatorColumn, (Object[])null); 
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
    public Enum getDiscriminatorType() {
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
    public void setDiscriminatorType(Enum descriminatorType) {
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
