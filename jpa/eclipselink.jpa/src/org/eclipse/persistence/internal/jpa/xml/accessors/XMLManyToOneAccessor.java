/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.xml.accessors;

import java.util.List;

import org.eclipse.persistence.internal.jpa.metadata.accessors.ManyToOneAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.columns.MetadataJoinColumns;

import org.eclipse.persistence.internal.jpa.xml.XMLHelper;
import org.eclipse.persistence.internal.jpa.xml.accessors.XMLClassAccessor;
import org.eclipse.persistence.internal.jpa.xml.columns.XMLJoinColumns;

import org.w3c.dom.Node;

/**
 * An xml extended many to one relationship accessor.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class XMLManyToOneAccessor extends ManyToOneAccessor {    
    protected Node m_node;
    protected XMLHelper m_helper;
    
    /**
     * INTERNAL:
     */
    public XMLManyToOneAccessor(MetadataAccessibleObject accessibleObject, Node node, XMLClassAccessor classAccessor) {
        super(accessibleObject, classAccessor);
        m_node = node;
        m_helper = classAccessor.getHelper();
    }
    
    /**
     * INTERNAL: (Override from ManyToOneAccessor)
     */
    public List<String> getCascadeTypes() {
        return m_helper.getCascadeTypes(m_node);
    }
    
    /**
     * INTERNAL: (Override from ManyToOneAccessor)
     */
    public String getFetchType() {
        return m_helper.getFetchTypeDefaultEAGER(m_node);
    }
    
    /**
     * INTERNAL: (Override from RelationshipAccessor)
     */    
    protected MetadataJoinColumns getJoinColumns() {
        if (m_helper.nodeHasJoinColumns(m_node)) {
            return new XMLJoinColumns(m_node, m_helper);
        } else {
            return super.getJoinColumns();
        }
    }
    
    /**
     * INTERNAL: (Override from ManyToOneAccessor)
     */
    public Class getTargetEntity() {
        return m_helper.getTargetEntity(m_node);
    }
    
    /**
     * INTERNAL: (Override from ManyToOneAccessor)
     */
    public boolean isOptional() {
        return m_helper.isOptional(m_node);
    }
}
