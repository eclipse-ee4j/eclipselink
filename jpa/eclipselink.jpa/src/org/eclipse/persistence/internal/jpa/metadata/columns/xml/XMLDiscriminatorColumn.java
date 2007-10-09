/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.columns.xml;

import org.eclipse.persistence.internal.jpa.metadata.columns.MetadataDiscriminatorColumn;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLHelper;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLConstants;

import org.w3c.dom.Node;

/**
 * Object to hold onto an xml discriminator column metadata.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class XMLDiscriminatorColumn extends MetadataDiscriminatorColumn {
    protected Node m_node;
    protected XMLHelper m_helper;
    
    /**
     * INTERNAL:
     */
    public XMLDiscriminatorColumn(Node node, XMLHelper helper) {
        m_node = node;
        m_helper = helper;
    }
    
    /**
     * INTERNAL:
     */
    public String getColumnDefinition() {
        return m_helper.getNodeValue(m_node, XMLConstants.ATT_COLUMN_DEFINITION, DEFAULT_COLUMN_DEFINITION);
    }
    
    /**
     * INTERNAL:
     */
    public String getDiscriminatorType() {
        return m_helper.getNodeValue(m_node, XMLConstants.ATT_DISCRIMINATOR_TYPE, DEFAULT_DISCRIMINATOR_TYPE);
    }
    
    /**
     * INTERNAL:
     */
    public int getLength() {
        return m_helper.getNodeValue(m_node, XMLConstants.ATT_LENGTH, DEFAULT_LENGTH);
    }
    
    /**
     * INTERNAL:
     */
    public String getName() {
        return m_helper.getNodeValue(m_node, XMLConstants.ATT_NAME);
    }
}
