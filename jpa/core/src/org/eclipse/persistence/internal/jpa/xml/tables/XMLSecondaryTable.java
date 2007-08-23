/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.xml.tables;

import org.w3c.dom.Node;

import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;

import org.eclipse.persistence.internal.jpa.xml.XMLHelper;
import org.eclipse.persistence.internal.jpa.xml.XMLConstants;

import org.eclipse.persistence.internal.jpa.xml.columns.XMLPrimaryKeyJoinColumns;

import org.eclipse.persistence.internal.jpa.metadata.tables.MetadataSecondaryTable;

import org.eclipse.persistence.internal.helper.DatabaseTable;

/**
 * Object to hold onto an XML secondary table metadata in a TopLink database 
 * table.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class XMLSecondaryTable extends MetadataSecondaryTable  {
    private Node m_node;
    private XMLHelper m_helper;
    
    /**
     * INTERNAL:
     */
    public XMLSecondaryTable(Node node, XMLHelper helper, MetadataLogger logger) {
        super(logger);
        
        m_node = node;
        m_helper = helper;
        m_name = m_helper.getNodeValue(node, XMLConstants.ATT_NAME);  
        m_schema = m_helper.getNodeValue(node, XMLConstants.ATT_SCHEMA);
        m_catalog = m_helper.getNodeValue(node, XMLConstants.ATT_CATALOG);
        
        processName();
        XMLTableHelper.processUniqueConstraints(node, helper, m_databaseTable);
    }
    
    /**
     * INTERNAL: (OVERRIDE)
     */
    protected void processPrimaryKeyJoinColumns(DatabaseTable sourceTable) {
        m_primaryKeyJoinColumns = new XMLPrimaryKeyJoinColumns(m_node, m_helper, sourceTable, m_databaseTable);
    }
}
