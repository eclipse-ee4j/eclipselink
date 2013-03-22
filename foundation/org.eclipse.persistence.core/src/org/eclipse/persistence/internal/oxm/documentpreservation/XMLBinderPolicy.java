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
package org.eclipse.persistence.internal.oxm.documentpreservation;

import java.util.IdentityHashMap;

import org.eclipse.persistence.oxm.documentpreservation.RelativePositionOrderingPolicy;

/**
 * INTERNAL:
 * <p><b>Purpose:</b>Provide an implementation of DocumentPreservationPolicy that maintains bidirectional
 * relationships between Java Objects and the XMLNodes they originated from.
 * <p><b>Responsibilities:</b><ul>
 * <li>Implement abstract methods from DocumentPreservationPolicy</li>
 * <li>Maintain a map of objects to nodes</li>
 * <li>Maintain the reverse map of nodes to objects</li>
 *
 * @author mmacivor
 *
 */
public class XMLBinderPolicy extends AbstractDocumentPreservationPolicy {

    public XMLBinderPolicy() {
        super();
        nodesToObjects = new IdentityHashMap();
        objectsToNodes = new IdentityHashMap();
        setNodeOrderingPolicy(new RelativePositionOrderingPolicy());
    }

}