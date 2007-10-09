/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.tables.xml;

import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;

import org.eclipse.persistence.internal.jpa.metadata.columns.MetadataJoinColumns;
import org.eclipse.persistence.internal.jpa.metadata.columns.xml.XMLJoinColumns;

import org.eclipse.persistence.internal.jpa.metadata.tables.MetadataJoinTable;

import org.eclipse.persistence.internal.jpa.metadata.xml.XMLConstants;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLHelper;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Object to hold onto an XML join table metadata in a TopLink database table.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class XMLJoinTable extends MetadataJoinTable {
    protected XMLHelper m_helper;
    protected NodeList m_joinColumns;
    protected NodeList m_inverseJoinColumns;
    
    /**
     * INTERNAL:
     */
    public XMLJoinTable(Node node, XMLHelper helper, MetadataLogger logger) {
        super(logger);
        
        m_helper = helper;
        
        m_name = helper.getNodeValue(node, XMLConstants.ATT_NAME);
        m_schema = helper.getNodeValue(node, XMLConstants.ATT_SCHEMA);
        m_catalog = helper.getNodeValue(node, XMLConstants.ATT_CATALOG);
        
        m_joinColumns = helper.getNodes(node, XMLConstants.JOIN_COLUMN);
        m_inverseJoinColumns =  helper.getNodes(node, XMLConstants.INVERSE_JOIN_COLUMN);
            
        processName();
        XMLTableHelper.processUniqueConstraints(node, helper, m_databaseTable);
    }

    /**
     * INTERNAL:
     */
    public boolean loadedFromXML() {
        return true;
    }
    
    /**
     * INTERNAL: (OVERRIDE)
     */
    protected MetadataJoinColumns processInverseJoinColumns() {
        return new XMLJoinColumns(m_inverseJoinColumns, m_helper);
    }
    
    /**
     * INTERNAL: (OVERRIDE)
     */
    protected MetadataJoinColumns processJoinColumns() {
        return new XMLJoinColumns(m_joinColumns, m_helper);
    }
}
