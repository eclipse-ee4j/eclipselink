/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors.xml;

import java.util.List;

import org.eclipse.persistence.internal.jpa.metadata.accessors.ManyToManyAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;

import org.eclipse.persistence.internal.jpa.metadata.tables.MetadataJoinTable;

import org.eclipse.persistence.internal.jpa.metadata.tables.xml.XMLJoinTable;

import org.eclipse.persistence.internal.jpa.metadata.xml.XMLConstants;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLHelper;

import org.w3c.dom.Node;

/**
 * A an extended many to many relationship accessor.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class XMLManyToManyAccessor extends ManyToManyAccessor {    
    protected Node m_node;
    protected XMLHelper m_helper;
    
    /**
     * INTERNAL:
     */
    public XMLManyToManyAccessor(MetadataAccessibleObject accessibleObject, Node node, XMLClassAccessor classAccessor) {
        super(accessibleObject, classAccessor);
        m_node = node;
        m_helper = classAccessor.getHelper();
    }
    
    /**
     * INTERNAL: (Override from ManyToManyAccessor)
     */
    public List<String> getCascadeTypes() {
        return m_helper.getCascadeTypes(m_node);
    }
    
    /**
     * INTERNAL: (Override from ManyToManyAccessor)
     */
    public String getFetchType() {
        return m_helper.getFetchTypeDefaultLAZY(m_node);
    }
    
    /**
     * INTERNAL: (Override from CollectionAccessor)
     */
    public MetadataJoinTable getJoinTable() {
        Node node = m_helper.getNode(m_node, XMLConstants.JOIN_TABLE);
        
        if (node == null) {
            return super.getJoinTable();
        } else {
            return new XMLJoinTable(node, m_helper, m_logger);
        }
    }
    
    /**
     * INTERNAL: (Override from CollectionAccessor)
     * 
     * Checks for a map-key node and returns its value if there is one. 
     * Otherwise, ask the parent to look for an annotation.
     */
    public String getMapKey() { 
        Node mapKeyNode = m_helper.getNode(m_node, XMLConstants.MAPKEY);
        String mapKeyValue = m_helper.getNodeValue(m_node, XMLConstants.MAPKEY);
        
        if (mapKeyNode == null) {
            return super.getMapKey();
        } else {
            return mapKeyValue;
        }
    }
    
    /**
     * INTERNAL: (Override from ManyToManyAccessor)
     */
    public String getMappedBy() {
        return m_helper.getMappedBy(m_node);
    }
    
    /**
     * INTERNAL: (Override from CollectionAccessor)
     * 
     * If the order value is not specified, "" is returned.
     */
    public String getOrderBy() {
        if (hasOrderBy()) {
            return m_helper.getNodeTextValue(m_node, XMLConstants.ORDER_BY);
        } else {
            return super.getOrderBy();
        }
    } 
    
    /**
     * INTERNAL: (Override from ManyToManyAccessor)
     */
    public Class getTargetEntity() {
        return m_helper.getTargetEntity(m_node);
    }
    
    /**
     * INTERNAL: (Override from CollectionAccessor)
	 * Checks for an order-by node. If one isn't found, as the parent to look
     * for an annotation.
     */
	public boolean hasOrderBy() {
		Node orderByNode = m_helper.getNode(m_node, XMLConstants.ORDER_BY);
        
        if (orderByNode == null) {
            return super.hasOrderBy();
        } else {
            return true;
        }
	}
}
