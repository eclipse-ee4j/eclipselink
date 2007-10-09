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
import org.eclipse.persistence.internal.jpa.metadata.tables.MetadataTable;

import org.eclipse.persistence.internal.jpa.metadata.xml.XMLHelper;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLConstants;

import org.w3c.dom.Node;

/**
 * Object to hold onto an XML table metadata in a TopLink database table.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class XMLTable extends MetadataTable {
    /**
     * INTERNAL:
     */
    public XMLTable(Node node, XMLHelper helper, MetadataLogger logger) {
        super(logger);
        
        m_name = helper.getNodeValue(node, XMLConstants.ATT_NAME);
        m_schema = helper.getNodeValue(node, XMLConstants.ATT_SCHEMA);
        m_catalog = helper.getNodeValue(node, XMLConstants.ATT_CATALOG);
        
        processName();
        XMLTableHelper.processUniqueConstraints(node, helper, m_databaseTable);
    }

    /**
     * INTERNAL:
     */
    public boolean loadedFromXML() {
        return true;
    }
}
