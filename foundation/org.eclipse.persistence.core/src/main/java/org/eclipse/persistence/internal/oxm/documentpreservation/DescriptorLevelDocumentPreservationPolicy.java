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

import java.util.WeakHashMap;

import org.eclipse.persistence.oxm.documentpreservation.AppendNewElementsOrderingPolicy;

/**
 * INTERNAL:
 * <p><b>Purpose:</b>Provide an implementation of DocumentPreservation Policy that accesses the
 * session cache to store Objects and their associated nodes.
 * <p><b>Responsibilities:</b>
 * <ul>
 * <li>Add new objects and their associated nodes into the Session cache, wrapping them in a weak
 * reference.</li>
 * <li>Lookup the node for a given Object</li>
 * </ul>
 *
 * @author mmacivor
 * @since TopLink 11g
 */

public class DescriptorLevelDocumentPreservationPolicy extends AbstractDocumentPreservationPolicy {

    public DescriptorLevelDocumentPreservationPolicy() {
        super();
        nodesToObjects = new WeakHashMap();
        objectsToNodes = new WeakHashMap();
        this.setNodeOrderingPolicy(new AppendNewElementsOrderingPolicy());
    }

}
