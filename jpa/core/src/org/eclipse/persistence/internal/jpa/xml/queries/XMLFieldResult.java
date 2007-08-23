/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.xml.queries;

import org.w3c.dom.Node;

import org.eclipse.persistence.internal.jpa.xml.XMLHelper;
import org.eclipse.persistence.internal.jpa.xml.XMLConstants;

import org.eclipse.persistence.internal.jpa.metadata.queries.MetadataFieldResult;

/**
 * Object to hold onto an xml field result metadata.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class XMLFieldResult extends MetadataFieldResult {
    protected Node m_node;
    protected XMLHelper m_helper;

    /**
     * INTERNAL:
     */
    public XMLFieldResult(Node node, XMLHelper helper) {
        m_node = node;
        m_helper = helper;
    }
    
    /**
     * INTERNAL:
     */
    public String getColumn() {
        return m_helper.getNodeValue(m_node, XMLConstants.ATT_COLUMN);
    }
    
    /**
     * INTERNAL:
     */
    public String getName() {
        return m_helper.getNodeValue(m_node, XMLConstants.ATT_NAME);
    }
}
