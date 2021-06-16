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
package org.eclipse.persistence.internal.oxm.record.deferred;

import org.eclipse.persistence.internal.oxm.record.UnmarshalRecord;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * <p><b>Purpose</b>: Class to represent the startElement event
 * <p><b>Responsibilities</b>:<ul>
 * <li> Execute the startElement event on the given unmarshalRecord with the specified arguments
 * </ul>
 */
public class StartElementEvent extends SAXEvent {
    private String namespaceUri;
    private String localName;
    private String qname;
    private Attributes attrs;

    public StartElementEvent(String theNamespaceUri, String theLocalName, String theQname, Attributes theAttrs) {
        super();
        namespaceUri = theNamespaceUri;
        localName = theLocalName;
        qname = theQname;
        attrs = theAttrs;
    }

    @Override
    public void processEvent(UnmarshalRecord unmarshalRecord) throws SAXException {
        unmarshalRecord.startElement(namespaceUri, localName, qname, attrs);
    }
}
