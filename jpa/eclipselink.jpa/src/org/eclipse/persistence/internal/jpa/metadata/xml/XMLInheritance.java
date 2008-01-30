/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.xml;

import javax.persistence.InheritanceType;

/**
 * Object to represent an inheritance root defined in XML.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 1.0
 */
public class XMLInheritance {
	private InheritanceType m_strategy;
	
	/**
     * INTERNAL:
     */
	public XMLInheritance() {}

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public InheritanceType getStrategy() {
        return m_strategy;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setStrategy(InheritanceType strategy) {
    	m_strategy = strategy;
    }
}
    