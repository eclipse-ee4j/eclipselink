/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.oxm.documentpreservation;

import org.w3c.dom.Node;

import org.eclipse.persistence.oxm.documentpreservation.AppendNewElementsOrderingPolicy;
import org.eclipse.persistence.oxm.documentpreservation.DocumentPreservationPolicy;
import org.eclipse.persistence.oxm.mappings.XMLMapping;

/**
 * INTERNAL:
 * <p><b>Purpose:</b>Provide a DocumentPreservationPolicy to indicate that no document preservation
 * work should be done
 * <p><b>Responsibilities:</b><ul>
 * <li>Implement all abstract methods from DocumentPreservationPolicy to do nothing.
 * <li>Always use appendNodeOrdering policy
 * </ul>
 * 
 * @since Oracle TopLink 11g
 * @author mmacivor
 *
 */
public class NoDocumentPreservationPolicy extends DocumentPreservationPolicy {
    
    public NoDocumentPreservationPolicy() {
        setNodeOrderingPolicy(new AppendNewElementsOrderingPolicy());
    }
    
    public void addObjectToCache(Object obj, Node node) {
        //No op
    }

    public void addObjectToCache(Object obj, Node node, XMLMapping selfRecordMapping) {
        
    }
    
    public Node getNodeForObject(Object obj) {
        return null;
    }
    
    public Object getObjectForNode(Node node) {
        return getObjectForNode(node, null);
    }
    
    public Object getObjectForNode(Node node, XMLMapping selfRecordNodeValue) {
        return null;
    }
    
    public boolean shouldPreserveDocument() {
        return false;
    }
}
