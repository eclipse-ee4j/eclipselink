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
 * <p><b>Purpose:</b>An implementation of NodeOrderingPolicy that ignores any new elements when
 * update a cached document. This is used for the JAXB 2.0 Binder implementation. 
 * @author mmacivor
 *
 */
public class IgnoreNewElementsOrderingPolicy implements NodeOrderingPolicy {
    public void appendNode(Node parent, Node newChild, Node previousSibling) {
        //no op
    }      
}
