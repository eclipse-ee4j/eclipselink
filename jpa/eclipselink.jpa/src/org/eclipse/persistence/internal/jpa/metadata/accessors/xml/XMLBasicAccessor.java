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

import org.eclipse.persistence.internal.jpa.metadata.accessors.BasicAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.xml.XMLClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.columns.MetadataColumn;
import org.eclipse.persistence.internal.jpa.metadata.columns.xml.XMLColumn;
import org.eclipse.persistence.internal.jpa.metadata.sequencing.xml.XMLGeneratedValue;
import org.eclipse.persistence.internal.jpa.metadata.sequencing.xml.XMLTableGenerator;
import org.eclipse.persistence.internal.jpa.metadata.sequencing.xml.XMLSequenceGenerator;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLHelper;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLConstants;
import org.eclipse.persistence.internal.helper.DatabaseField;

import org.w3c.dom.Node;

/**
 * An XML extended basic accessor.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class XMLBasicAccessor extends BasicAccessor implements XMLAccessor {
    private Node m_node;
    private XMLHelper m_helper;
    
    /**
     * INTERNAL:
     */
    public XMLBasicAccessor(MetadataAccessibleObject accessibleObject, Node node, XMLClassAccessor classAccessor) {
        super(accessibleObject, classAccessor);
        m_node = node;
        m_helper = classAccessor.getHelper();
    }

    /**
     * INTERNAL:
     */
    public String getCatalog() {
        return m_descriptor.getCatalog();
    }
    
    /**
     * INTERNAL: (Override from BasicAccessor)
     * Build a metadata column. If one isn't found in XML then look for an
     * annotation.
     */
    protected MetadataColumn getColumn(String loggingCtx) {
        Node node = m_helper.getNode(m_node, XMLConstants.COLUMN);
        
        if (node != null) {
            return new XMLColumn(node, m_helper, this);
        } else {
            return super.getColumn(loggingCtx);
        }
    }
    
    /**
     * INTERNAL:
     */
    public String getDocumentName() {
        return m_helper.getDocumentName();
    }
    
    /**
     * INTERNAL: (Override from DirectAccessor)
     */
    public String getEnumeratedType() {
        if (hasEnumerated()) {
            return m_helper.getNodeTextValue(m_node, XMLConstants.ENUMERATED);
        } else {
            return super.getEnumeratedType();
        }
    }
    
    /**
     * INTERNAL: (Override from BasicAccessor)
     */
    public String getFetchType() {
        return m_helper.getFetchTypeDefaultEAGER(m_node);
    }
    
    /**
     * INTERNAL:
     */
    public XMLHelper getHelper() {
        return m_helper;
    }
    
    /**
     * INTERNAL:
     */
    public String getSchema() {
        return m_descriptor.getSchema();
    }
    
    /**
     * INTERNAL: (Override from DirectAccessor)
     * 
     * Return the temporal type for this accessor. Assumes there is a temporal
     * node.
     */
    public String getTemporalType() {
        if (hasTemporal()) {
            return m_helper.getNodeTextValue(m_node, XMLConstants.TEMPORAL);
        } else {
            return super.getTemporalType();
        }
    }
    
    /**
     * INTERNAL: (Override from DirectAccessor)
     * 
	 * Method to check if m_node has an enumerated sub-element.
     */
	public boolean hasEnumerated() {
        Node node = m_helper.getNode(m_node, XMLConstants.ENUMERATED);
        
        if (node == null) {
            return super.hasEnumerated();
        } else {
            return true;
        }
    }
    
    /**
     * INTERNAL: (Override from DirectAccessor)
     * 
	 * Method to check if m_node has a temporal sub-element.
     */
	public boolean hasTemporal() {
        Node node = m_helper.getNode(m_node, XMLConstants.TEMPORAL);
        
        if (node == null) {
            return super.hasTemporal();
        } else {
            return true;
        }
    }
    
    /**
     * INTERNAL: (Override from BasicAccessor)
     * 
	 * Method to check if m_node represents a primary key.
	 */
	public boolean isId() {
        if (m_node.getLocalName().equals(XMLConstants.ID)) {
            return true;
        } else {
            return super.isId();   
        }
    }
    
    /**
     * INTERNAL: (Override from BasicAccessor)
     */
	public boolean isOptional() {
        return m_helper.isOptional(m_node);
    }
    
    /**
     * INTERNAL: (Override from DirectAccessor)
     * 
     * Return true if this accessor represents an BLOB/CLOB mapping, i.e. has a 
     * lob sub-element.
     */
	public boolean hasLob() {
        Node node = m_helper.getNode(m_node, XMLConstants.LOB);
        
        if (node == null) {
            return super.hasLob();
        } else {
            return true;
        }
    }
    
    /**
     * INTERNAL: (Override from BasicAccessor)
     * 
	 * Return true if this accessor represents an optimistic locking field.
     */
	public boolean isVersion() {
        if (m_node.getLocalName().equals(XMLConstants.VERSION)) {
            return true;
        } else {
            return super.isVersion();   
        }
    }
    
    /**
     * INTERNAL: (Override from BasicAccessor)
     */
    protected void processGeneratedValue(DatabaseField field) {
        Node node = m_helper.getNode(m_node, XMLConstants.GENERATED_VALUE);

        if (node == null) {
            super.processGeneratedValue(field);
        } else {
            // Ask the common processor to process what we found.
            processGeneratedValue(new XMLGeneratedValue(node, m_helper), field);
        }
    }
    
    /**
     * INTERNAL: (Override from NonRelationshipAccessor)
     * 
	 * Process this accessor's sequence-generator node into a common metadata 
     * sequence generator.
     */
    protected void processSequenceGenerator() {
        Node node = m_helper.getNode(m_node, XMLConstants.SEQUENCE_GENERATOR);
        
        if (node != null) {
            // Process the xml defined sequence generators first.
            processSequenceGenerator(new XMLSequenceGenerator(node, m_helper));
        }
        
        // Process the annotation defined sequence generators second.
        super.processSequenceGenerator();
    }
    
    /**
     * INTERNAL: (Override from NonRelationshipAccessor)
     * 
	 * Process this accessor's table-generator node into a common metadata table 
     * generator.
     */
    protected void processTableGenerator() {
        Node node = m_helper.getNode(m_node, XMLConstants.TABLE_GENERATOR);
        
        if (node != null) {
            // Process the xml defined table generators first.
            processTableGenerator(new XMLTableGenerator(node, this));
        }
        
        // Process the annotation defined sequence generators second.
        super.processTableGenerator();
    }
}
