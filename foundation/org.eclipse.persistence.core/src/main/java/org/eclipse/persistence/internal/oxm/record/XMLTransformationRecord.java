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
    @Override
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
