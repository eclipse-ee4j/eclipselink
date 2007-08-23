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

import java.lang.reflect.AnnotatedElement;

import org.eclipse.persistence.internal.jpa.metadata.columns.MetadataColumn;

import org.eclipse.persistence.internal.jpa.xml.accessors.XMLBasicAccessor;

import org.eclipse.persistence.internal.jpa.xml.XMLConstants;
import org.eclipse.persistence.internal.jpa.xml.XMLHelper;

import org.w3c.dom.Node;

/**
 * Object to hold onto an xml column metadata in a TopLink database field.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class XMLColumn extends MetadataColumn {
    /**
     * INTERNAL:
     * Called for attribute overrides from a class accessor or embedded 
     * accessor. If there is no column node, the database field values will 
     * default.
     */
    public XMLColumn(Node node, XMLHelper helper, AnnotatedElement annotatedElement) {
        super(helper.getNodeValue(node, XMLConstants.ATT_NAME), annotatedElement);
        
        processColumnNode(helper.getNode(node, XMLConstants.COLUMN), helper);
    }
    
    /**
     * INTERNAL:
     * Called for basic accessors. If there is no column node, the database 
     * field values will default.
     */
    public XMLColumn(Node node, XMLHelper helper, XMLBasicAccessor accessor) {
        super(accessor.getAttributeName(), accessor.getAnnotatedElement());
        
        processColumnNode(node, helper);
    }
    
    /**
     * INTERNAL:
     */
    public boolean loadedFromXML() {
        return true;
    }
    
    /**
     * INTERNAL:
     */
    protected void processColumnNode(Node node, XMLHelper helper) {
        if (node != null) {
            // Process the name attribute node.
            m_databaseField.setName(helper.getNodeValue(node, XMLConstants.ATT_NAME, DEFAULT_NAME));
        
            // Process the table attribute node.
            m_databaseField.setTableName(helper.getNodeValue(node, XMLConstants.ATT_TABLE, DEFAULT_TABLE));
        
            // Process the insertable attribute node.
            m_databaseField.setInsertable(helper.getNodeValue(node, XMLConstants.ATT_INSERTABLE, DEFAULT_INSERTABLE));
        
            // Process the updatable attribute node.
            m_databaseField.setUpdatable(helper.getNodeValue(node, XMLConstants.ATT_UPDATABLE, DEFAULT_UPDATABLE));
        
            // Process the unique attribute node.
            m_databaseField.setUnique(helper.getNodeValue(node, XMLConstants.ATT_UNIQUE, DEFAULT_UNIQUE));
        
            // Process the nullable attribute node.
            m_databaseField.setNullable(helper.getNodeValue(node, XMLConstants.ATT_NULLABLE, DEFAULT_NULLABLE));
        
            // Process the column-definition attribute node.
            m_databaseField.setColumnDefinition(helper.getNodeValue(node, XMLConstants.ATT_COLUMN_DEFINITION, DEFAULT_COLUMN_DEFINITION));
        
            // Process the length attribute node.
            m_databaseField.setLength(helper.getNodeValue(node, XMLConstants.ATT_LENGTH, DEFAULT_LENGTH));
        
            // Process the precision attribute node.
            m_databaseField.setPrecision(helper.getNodeValue(node, XMLConstants.ATT_PRECISION, DEFAULT_PRECISION));
        
            // Process the scale attribute node.
            m_databaseField.setScale(helper.getNodeValue(node, XMLConstants.ATT_SCALE, DEFAULT_SCALE));
        }
    }
}
