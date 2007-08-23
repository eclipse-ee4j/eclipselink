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

import org.w3c.dom.Node;

import org.eclipse.persistence.internal.jpa.xml.XMLHelper;
import org.eclipse.persistence.internal.jpa.xml.XMLConstants;

import org.eclipse.persistence.internal.jpa.metadata.sequencing.MetadataSequenceGenerator;

/**
 * Object to hold onto an XML sequence generator metadata.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class XMLSequenceGenerator extends MetadataSequenceGenerator {
    private Node m_node;
    private XMLHelper m_helper;
    
    /**
     * INTERNAL:
     */
    public XMLSequenceGenerator(Node node, XMLHelper helper) {
        super(helper.getDocumentName());
        
        m_node = node;
        m_helper = helper;
    }

    /**
     * INTERNAL:
     */
    public int getAllocationSize() {
        return m_helper.getNodeValue(m_node, XMLConstants.ATT_ALLOCATION_SIZE, 50);
    }
    
    /**
     * INTERNAL:
     */
    public int getInitialValue() {
        return m_helper.getNodeValue(m_node, XMLConstants.ATT_INITIAL_VALUE, 0);
    }
    
    /**
     * INTERNAL:
     */
    public String getName() {
        return m_helper.getNodeValue(m_node, XMLConstants.ATT_NAME);
    }
    
    /**
     * INTERNAL:
     */
    public String getSequenceName() {
        return m_helper.getNodeValue(m_node, XMLConstants.ATT_SEQUENCE_NAME, getName());
    }
    
    /**
     * INTERNAL:
     */
    public boolean loadedFromAnnotations() {
       return false; 
    }
    
    /**
     * INTERNAL:
     */
    public boolean loadedFromXML() {
       return true; 
    }
}
