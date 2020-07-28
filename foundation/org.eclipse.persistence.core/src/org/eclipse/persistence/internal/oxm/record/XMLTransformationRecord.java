/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.oxm.record;

import org.eclipse.persistence.internal.oxm.NamespaceResolver;
import org.eclipse.persistence.internal.oxm.record.namespaces.UnmarshalNamespaceResolver;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.oxm.record.DOMRecord;

/**
 *  @version $Header: XMLTransformationRecord.java 09-aug-2007.15:35:19 dmccann Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */
public class XMLTransformationRecord extends DOMRecord {
    private UnmarshalRecord owningRecord;
    private NamespaceResolver resolver;
    public XMLTransformationRecord(UnmarshalRecord owner) {
        super();
        owningRecord = owner;
        initializeNamespaceMaps();
    }

    public XMLTransformationRecord(String rootName, UnmarshalRecord owner) {
        super(rootName);
        owningRecord = owner;
        session = (AbstractSession) owner.getSession();
        resolver = new NamespaceResolver();
        initializeNamespaceMaps();
    }
    public String resolveNamespacePrefix(String prefix) {
        return resolver.resolveNamespacePrefix(prefix);
    }

    public void initializeNamespaceMaps() {
        //When the transformation record is created, initialize the namespace resolver
        //to contain the namespaces from the current state of the owning record.
        //Start at the root and work down.
        UnmarshalNamespaceResolver unmarshalNamespaceResolver = owningRecord.getUnmarshalNamespaceResolver();
        for(String prefix : unmarshalNamespaceResolver.getPrefixes()) {
            resolver.put(prefix, unmarshalNamespaceResolver.getNamespaceURI(prefix));
        }
    }
}
