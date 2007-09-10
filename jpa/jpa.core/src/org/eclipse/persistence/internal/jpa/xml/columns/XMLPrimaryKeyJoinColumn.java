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

import org.eclipse.persistence.internal.jpa.xml.XMLHelper;
import org.eclipse.persistence.internal.jpa.xml.XMLConstants;

import org.eclipse.persistence.internal.jpa.metadata.columns.MetadataPrimaryKeyJoinColumn;

import org.eclipse.persistence.internal.helper.DatabaseTable;

/**
 * Object to hold onto XML primary key join column metadata in TopLink database 
 * fields.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class XMLPrimaryKeyJoinColumn extends MetadataPrimaryKeyJoinColumn {
    /**
     * INTERNAL:
     */
    public XMLPrimaryKeyJoinColumn(DatabaseTable sourceTable, DatabaseTable targetTable) {
        super(sourceTable, targetTable);
    }
    
    /**
     * INTERNAL:
     */
    public XMLPrimaryKeyJoinColumn(Node node, XMLHelper helper, DatabaseTable sourceTable, DatabaseTable targetTable) {
        super(sourceTable, targetTable);

        if (node != null) {
            // Process the primary key field metadata.
            m_pkField.setName(helper.getNodeValue(node, XMLConstants.ATT_REFERENCED_COLUMN_NAME, DEFAULT_REFERENCED_COLUMN_NAME));
        
            // Process the foreign key field metadata.
            m_fkField.setName(helper.getNodeValue(node, XMLConstants.ATT_NAME, DEFAULT_NAME));
            m_fkField.setColumnDefinition(helper.getNodeValue(node, XMLConstants.ATT_COLUMN_DEFINITION, DEFAULT_COLUMN_DEFINITION));
        }
    }
    
    /**
     * INTERNAL:
     */
    public boolean loadedFromXML() {
        return true;
    }
}
