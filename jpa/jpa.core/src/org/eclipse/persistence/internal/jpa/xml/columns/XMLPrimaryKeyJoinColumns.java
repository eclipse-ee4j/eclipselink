/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.xml.columns;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.eclipse.persistence.internal.jpa.xml.XMLHelper;
import org.eclipse.persistence.internal.jpa.xml.XMLConstants;

import org.eclipse.persistence.internal.jpa.metadata.columns.MetadataPrimaryKeyJoinColumns;

import org.eclipse.persistence.internal.helper.DatabaseTable;

/**
 * Object to hold onto XML primary key join column metadata.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class XMLPrimaryKeyJoinColumns extends MetadataPrimaryKeyJoinColumns {    
    /**
     * INTERNAL:
     */
    public XMLPrimaryKeyJoinColumns(Node node, XMLHelper helper, DatabaseTable sourceTable, DatabaseTable targetTable) {
        super(sourceTable, targetTable);

        // Process the primary-key-join-column nodes.        
        NodeList nodes = helper.getNodes(node, XMLConstants.PK_JOIN_COLUMN);

        if (nodes != null) {
            for (int i = 0; i < nodes.getLength(); i++) {
                m_pkJoinColumns.add(new XMLPrimaryKeyJoinColumn(nodes.item(i), helper, sourceTable, targetTable));
            }
		}
    }
    
    /**
     * INTERNAL:
     */
    public boolean loadedFromXML() {
        return true;
    }
}
