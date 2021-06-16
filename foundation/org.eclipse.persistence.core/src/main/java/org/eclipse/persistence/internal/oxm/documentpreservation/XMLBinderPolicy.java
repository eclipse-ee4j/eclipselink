/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.oxm.documentpreservation;

import java.util.IdentityHashMap;

import org.eclipse.persistence.oxm.documentpreservation.RelativePositionOrderingPolicy;

/**
 * INTERNAL:
 * <p><b>Purpose:</b>Provide an implementation of DocumentPreservationPolicy that maintains bidirectional
 * relationships between Java Objects and the XMLNodes they originated from.
 * <p><b>Responsibilities:</b>
 * <ul>
 * <li>Implement abstract methods from DocumentPreservationPolicy</li>
 * <li>Maintain a map of objects to nodes</li>
 * <li>Maintain the reverse map of nodes to objects</li>
 * </ul>
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
