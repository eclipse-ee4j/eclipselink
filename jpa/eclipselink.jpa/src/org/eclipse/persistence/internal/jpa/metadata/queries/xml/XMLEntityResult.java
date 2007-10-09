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

import org.eclipse.persistence.internal.jpa.metadata.xml.XMLHelper;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLConstants;

import org.eclipse.persistence.internal.jpa.metadata.queries.MetadataFieldResult;
import org.eclipse.persistence.internal.jpa.metadata.queries.MetadataEntityResult;

/**
 * Object to hold onto an xml entity result metadata.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class XMLEntityResult extends MetadataEntityResult {
    protected Node m_node;
    protected XMLHelper m_helper;

    /**
     * INTERNAL:
     */
    public XMLEntityResult(Node node, XMLHelper helper) {
        m_node = node;
        m_helper = helper;
    }
    
    /**
     * INTERNAL:
     */
    public String getDiscriminatorColumn() {
        return m_helper.getNodeValue(m_node, XMLConstants.ATT_DISCRIMINATOR_COLUMN, "");
    }
    
    /**
     * INTERNAL:
     * Note this attribute is required but we send in the default void.class
     * object to ensure we go through the correct class loading.
     */
    public Class getEntityClass() {
        return m_helper.getNodeValue(m_node, XMLConstants.ATT_ENTITY_CLASS, void.class);
    }
    
    /**
     * INTERNAL:
     */
    public List<MetadataFieldResult> getFieldResults() {
        if (m_fieldResults == null) {
            m_fieldResults = new ArrayList<MetadataFieldResult>();
            NodeList fieldResultNodes = m_helper.getNodes(m_node, XMLConstants.FIELD_RESULT);
            
            if (fieldResultNodes != null) {
                for (int i = 0; i < fieldResultNodes.getLength(); i++) {
                    m_fieldResults.add(new XMLFieldResult(fieldResultNodes.item(i), m_helper));
                }
            }
        }
        
        return m_fieldResults;
    }
}
