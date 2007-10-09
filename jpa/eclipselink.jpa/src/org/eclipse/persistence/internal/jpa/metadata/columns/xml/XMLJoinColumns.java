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

import org.eclipse.persistence.internal.jpa.metadata.columns.MetadataJoinColumns;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLHelper;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLConstants;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Object to hold onto xml join column metadata.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class XMLJoinColumns extends MetadataJoinColumns {
    /**
     * INTERNAL:
     */
    public XMLJoinColumns(Node node, XMLHelper helper) {
        this(helper.getNodes(node, XMLConstants.JOIN_COLUMN), helper);
    }
    
    /**
     * INTERNAL:
     */
    public XMLJoinColumns(NodeList nodes, XMLHelper helper) {
        for (int i = 0; i < nodes.getLength(); i++) {
            m_joinColumns.add(new XMLJoinColumn(nodes.item(i), helper));
        }
    }
    
    /**
     * INTERNAL:
     */
    public boolean loadedFromXML() {
        return true;
    }
}
