/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.xml.sequencing;

import org.eclipse.persistence.internal.jpa.metadata.sequencing.MetadataGeneratedValue;

import org.eclipse.persistence.internal.jpa.xml.XMLHelper;
import org.eclipse.persistence.internal.jpa.xml.XMLConstants;

import org.w3c.dom.Node;

/**
 * Metadata object to hold generated value information.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class XMLGeneratedValue extends MetadataGeneratedValue {
    protected Node m_node;
    protected XMLHelper m_helper;
    
    /**
     * INTERNAL:
     */
    public XMLGeneratedValue(Node node, XMLHelper helper) {
        m_node = node;
        m_helper = helper;
    }
    
    /**
     * INTERNAL: (OVERRIDE)
     */
    public String getGenerator() {
        return m_helper.getNodeValue(m_node, XMLConstants.ATT_GENERATOR);
    }
        
    /**
     * INTERNAL: (OVERRIDE)
     */
    public String getStrategy() {
        return m_helper.getNodeValue(m_node, XMLConstants.ATT_STRATEGY, DEFAULT_STRATEGY);
    }
}
