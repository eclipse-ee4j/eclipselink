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

import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;

import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProcessor;

import org.eclipse.persistence.internal.jpa.xml.XMLHelper;

import org.w3c.dom.Node;

/**
 * An XML extended mapped superclass accessor.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class XMLMappedSuperclassAccessor extends XMLClassAccessor {
    /**
     * INTERNAL:
     */
    public XMLMappedSuperclassAccessor(MetadataAccessibleObject accessibleObject, Node node, XMLHelper helper, MetadataProcessor processor, MetadataDescriptor descriptor) {
        super(accessibleObject, node, helper, processor, descriptor);
    }
    
    /**
     * INTERNAL:
     * Process the items of interest on a mapped superclass.
     */
    public void process() {
        processMappedSuperclass();
    }
}
    