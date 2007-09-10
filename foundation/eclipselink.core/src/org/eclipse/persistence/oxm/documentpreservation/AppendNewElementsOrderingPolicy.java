/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.oxm.documentpreservation;

import org.w3c.dom.Node;

import org.eclipse.persistence.oxm.documentpreservation.NodeOrderingPolicy;

/**
 * INTERNAL:
 * <p><b>Purpose:</b>Provide an implementation of NodeOrderingPolicy that simply appends the new child
 * element to the parent. This is the default for DescriptorLevelDocumentPreservationPolicy and 
 * NoDocumentPreservationPolicy
 * 
 * @see org.eclipse.persistence.oxm.documentpreservation.NodeOrderingPolicy
 * @author mmacivor
 *
 */
public class AppendNewElementsOrderingPolicy implements NodeOrderingPolicy {
    public void appendNode(Node parent, Node newChild, Node previousSibling) {
        parent.appendChild(newChild);
    }    
}

