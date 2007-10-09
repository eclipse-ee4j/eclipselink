/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.sequencing.xml;

import java.util.List;
import java.util.ArrayList;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.eclipse.persistence.internal.jpa.metadata.accessors.xml.XMLAccessor;
import org.eclipse.persistence.internal.jpa.metadata.sequencing.MetadataTableGenerator;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLConstants;

/**
 * Object to hold onto an xml table generator metadata.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class XMLTableGenerator extends MetadataTableGenerator {
    protected Node m_node;
    protected XMLAccessor m_accessor;
    
    /**
     * INTERNAL:
     */
    public XMLTableGenerator(Node node, XMLAccessor accessor) {
        super(accessor.getDocumentName());
        
        m_node = node;
        m_accessor = accessor;
    }
    
    /**
     * INTERNAL:
     */
    public int getAllocationSize() {
        return m_accessor.getHelper().getNodeValue(m_node, XMLConstants.ATT_ALLOCATION_SIZE, 50);
    }
    
    /**
     * INTERNAL:
     */
    public String getCatalog() {
        return m_accessor.getHelper().getNodeValue(m_node, XMLConstants.ATT_CATALOG, m_accessor.getCatalog());
    }
    
    /**
     * INTERNAL:
     */
    public int getInitialValue() {
        return m_accessor.getHelper().getNodeValue(m_node, XMLConstants.ATT_INITIAL_VALUE, 0);
    }
    
    /**
     * INTERNAL:
     */
    public String getName() {
        return m_accessor.getHelper().getNodeValue(m_node, XMLConstants.ATT_NAME);
    }
    
    /**
     * INTERNAL:
     */
    public String getPkColumnName() {
        return m_accessor.getHelper().getNodeValue(m_node, XMLConstants.ATT_PK_COLUMN_NAME);
    }
    
    /**
     * INTERNAL:
     */
    public String getPkColumnValue() {
        return m_accessor.getHelper().getNodeValue(m_node, XMLConstants.ATT_PK_COLUMN_VALUE);
    }
    
    /**
     * INTERNAL:
     */
    public String getSchema() {
        return m_accessor.getHelper().getNodeValue(m_node, XMLConstants.ATT_SCHEMA, m_accessor.getSchema());
    }
    
    /**
     * INTERNAL:
     */
    public String getTable() {
        return m_accessor.getHelper().getNodeValue(m_node, XMLConstants.ATT_TABLE);
    }
    
    /**
     * INTERNAL:
     */
    public List<String[]> getUniqueConstraints() {
        if (m_uniqueConstraints == null) {
            m_uniqueConstraints = new ArrayList<String[]>();
            NodeList uniqueConstraintNodes = m_accessor.getHelper().getNodes(m_node, XMLConstants.UNIQUE_CONSTRAINTS);
        
            if (uniqueConstraintNodes != null) {
                for (int i = 0; i < uniqueConstraintNodes.getLength(); i++) {
                    NodeList columnNameNodes = m_accessor.getHelper().getTextColumnNodes(uniqueConstraintNodes.item(i));
                
                    if (columnNameNodes != null) {
                        List<String> columnNames = new ArrayList<String>(columnNameNodes.getLength());
                        for (int k = 0; k < columnNameNodes.getLength(); k++) {
                            String columnName = columnNameNodes.item(k).getNodeValue();
                        
                            if (columnName != null && !columnName.equals("")) {
                                columnNames.add(columnName);
                            }
                        }
                        if (columnNames.size() > 0) {
                            m_uniqueConstraints.add(columnNames.toArray(new String[0]));
                        }
                    }
                }
            }
        }
        
        return m_uniqueConstraints;
    }
    
    /**
     * INTERNAL:
     */
    public String getValueColumnName() {
        return m_accessor.getHelper().getNodeValue(m_node, XMLConstants.ATT_VALUE_COLUMN_NAME);
    }
    
    /**
     * INTERNAL:
     */
    public boolean loadedFromAnnotations() {
       return false; 
    }
    
    /**
     * INTERNAL:
     */
    public boolean loadedFromXML() {
       return true; 
    }
}
