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

/**
 * <p><b>Purpose:</b>Provide an interface that specifies how new XML Elements are added to an 
 * existing XML Document.
 * 
 * @see org.eclipse.persistence.oxm.documentpreservation.DocumentPreservationPolicy
 * @author mmacivor
 *
 */
public interface NodeOrderingPolicy {
    public void appendNode(Node parent, Node newChild, Node previousSibling);
}
