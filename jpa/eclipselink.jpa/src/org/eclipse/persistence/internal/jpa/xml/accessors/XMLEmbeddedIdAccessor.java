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

import org.eclipse.persistence.internal.jpa.metadata.accessors.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.EmbeddedIdAccessor;

import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;

import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;

import org.eclipse.persistence.internal.jpa.xml.XMLConstants;
import org.eclipse.persistence.internal.jpa.xml.XMLHelper;

import org.eclipse.persistence.internal.jpa.xml.columns.XMLColumn;

import org.eclipse.persistence.mappings.AggregateObjectMapping;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * An XML extended embedded id relationship accessor.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class XMLEmbeddedIdAccessor extends EmbeddedIdAccessor {    
    protected Node m_node;
    protected XMLHelper m_helper;
    
    /**
     * INTERNAL:
     */
    public XMLEmbeddedIdAccessor(MetadataAccessibleObject accessibleObject, Node node, XMLClassAccessor classAccessor) {
        super(accessibleObject, classAccessor);
        m_node = node;
        m_helper = classAccessor.getHelper();
    }
    
    /**
     * INTERNAL: (Overrride from EmbeddedAccesor)
     * 
     * Currently if the embedded is specified in XML with no attribute
     * overrides, we do NOT search the class for attribute overrides. It is
     * assumed that they are to be defaulted.
     */
    protected void processAttributeOverrides(AggregateObjectMapping mapping) {
        NodeList nodes = m_helper.getNodes(m_node, XMLConstants.ATTRIBUTE_OVERRIDE);
        
    	if (nodes != null) {
    		for (int i = 0; i < nodes.getLength(); i++) {
                processAttributeOverride(mapping, new XMLColumn(nodes.item(i), m_helper, getAnnotatedElement()));
    		}
    	}
    }
    
    /**
     * INTERNAL: (Overrride from EmbeddedAccesor)
     * 
     * Fast track processing a ClassAccessor for the given descriptor. 
     * Inheritance root classes and embeddables may be fast tracked.
     * 
     * NOTE: The class passed in may not have any XML representation. If so,
     * pass up to the parent.
     */
    protected ClassAccessor processAccessor(MetadataDescriptor descriptor) {
        Node node = m_helper.locateEntityNode(descriptor.getJavaClass());
        
        if (node != null) {
            XMLClassAccessor accessor = new XMLClassAccessor(new MetadataClass(descriptor.getJavaClass()), node, m_helper, m_processor, descriptor);
            descriptor.setClassAccessor(accessor);
            accessor.process();
            accessor.setIsProcessed();
            return accessor;
        } else {
            return super.processAccessor(descriptor);
        }
    }
}
