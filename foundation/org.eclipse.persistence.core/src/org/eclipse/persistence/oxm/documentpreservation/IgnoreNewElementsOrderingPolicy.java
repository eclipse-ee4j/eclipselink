/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.oxm.documentpreservation;

import org.w3c.dom.Node;

import org.eclipse.persistence.oxm.documentpreservation.NodeOrderingPolicy;

/**
 * INTERNAL:
 * <p><b>Purpose:</b>An implementation of NodeOrderingPolicy that ignores any new elements when
 * updating a cached document. This is used for the JAXB 2.0 Binder implementation. 
 *
 * @see org.eclipse.persistence.oxm.documentpreservation.NodeOrderingPolicy
 * @author mmacivor
 * @author rbarkhouse - changed to subclass (NodeOrderingPolicy changed to abstract superclass) 
 */
public class IgnoreNewElementsOrderingPolicy extends NodeOrderingPolicy {

    public void appendNode(Node parent, Node newChild, Node previousSibling) {
        // No-op
    }      

}
