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

import org.eclipse.persistence.internal.jpa.metadata.columns.MetadataJoinColumn;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLHelper;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLConstants;

import org.w3c.dom.Node;

/**
 * Object to hold onto xml join column metadata in a TopLink database fields.
 * 
 * @author Guy Pelletier
 * @since TopLink 10.1.3/EJB 3.0 Preview
 */
public class XMLJoinColumn extends MetadataJoinColumn {
    /**
     * INTERNAL:
     * Called for association override.
     */
    public XMLJoinColumn(Node node, XMLHelper helper) {
        // Process the primary key field metadata.
        m_pkField.setName(helper.getNodeValue(node, XMLConstants.ATT_REFERENCED_COLUMN_NAME, DEFAULT_REFERENCED_COLUMN_NAME));
        
        // Process the foreign key field metadata.
        m_fkField.setName(helper.getNodeValue(node, XMLConstants.ATT_NAME, DEFAULT_NAME));
        m_fkField.setTableName(helper.getNodeValue(node, XMLConstants.ATT_TABLE, DEFAULT_TABLE));
        m_fkField.setUnique(helper.getNodeValue(node, XMLConstants.ATT_UNIQUE, DEFAULT_UNIQUE));
        m_fkField.setNullable(helper.getNodeValue(node, XMLConstants.ATT_NULLABLE, DEFAULT_NULLABLE));
        m_fkField.setUpdatable(helper.getNodeValue(node, XMLConstants.ATT_UPDATABLE, DEFAULT_UPDATABLE));
        m_fkField.setInsertable(helper.getNodeValue(node, XMLConstants.ATT_INSERTABLE, DEFAULT_INSERTABLE));
        m_fkField.setColumnDefinition(helper.getNodeValue(node, XMLConstants.ATT_COLUMN_DEFINITION, DEFAULT_COLUMN_DEFINITION));
    }
    
    /**
     * INTERNAL:
     */
    public boolean loadedFromXML() {
        return true;
    }
}
