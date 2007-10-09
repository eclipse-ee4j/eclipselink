/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.queries.xml;

import java.util.List;
import java.util.ArrayList;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.eclipse.persistence.internal.jpa.metadata.queries.MetadataEntityResult;
import org.eclipse.persistence.internal.jpa.metadata.queries.MetadataSQLResultSetMapping;

import org.eclipse.persistence.internal.jpa.metadata.xml.XMLHelper;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLConstants;

/**
 * Object to hold onto an XML sql result set mapping metadata.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class XMLSQLResultSetMapping extends MetadataSQLResultSetMapping {
    protected Node m_node;
    protected XMLHelper m_helper;

    /**
     * INTERNAL:
     */
    public XMLSQLResultSetMapping(Node node, XMLHelper helper) {
        m_node = node;
        m_helper = helper;
    }
    
   /**
     * INTERNAL:
     */
    public List<MetadataEntityResult> getEntityResults() {
        if (m_entityResults == null) {
            m_entityResults = new ArrayList<MetadataEntityResult>();
            NodeList entityResultNodes = m_helper.getNodes(m_node, XMLConstants.ENTITY_RESULT);
            
            if (entityResultNodes != null) {
                for (int i = 0; i < entityResultNodes.getLength(); i++) {
                    m_entityResults.add(new XMLEntityResult(entityResultNodes.item(i), m_helper));
                }
            }
        }
        
        return m_entityResults;
    }
    
    /**
     * INTERNAL:
     */
    public List<String> getColumnResults() {
        if (m_columnResults == null) {
            m_columnResults = new ArrayList<String>();    
            NodeList columnResultList = m_helper.getNodes(m_node, XMLConstants.COLUMN_RESULT);
            
            if (columnResultList != null) {
                for (int i = 0; i < columnResultList.getLength(); i++) {
                    m_columnResults.add(m_helper.getNodeValue(columnResultList.item(i), XMLConstants.ATT_NAME));
                }
            }
        }
        
        return m_columnResults;
    }
    
    /**
     * INTERNAL:
     */
    public String getName() {
        return m_helper.getNodeValue(m_node, XMLConstants.ATT_NAME);
    }
}
