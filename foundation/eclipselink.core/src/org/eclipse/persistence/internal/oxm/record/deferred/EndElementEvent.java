/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/ 
package org.eclipse.persistence.internal.oxm.record.deferred;

import org.eclipse.persistence.oxm.record.UnmarshalRecord;
import org.xml.sax.SAXException;

/**
 * <p><b>Purpose</b>: Class to represent the endElement event
 * <p><b>Responsibilities</b>:<ul>
 * <li> Execute the endElement event on the given unmarshalRecord with the specified arguments 
 * </ul>
 */
public class EndElementEvent extends SAXEvent {
    private String namespaceUri;
    private String localName;
    private String qname;

    public EndElementEvent(String theNamespaceUri, String theLocalName, String theQname) {
        super();
        namespaceUri = theNamespaceUri;
        localName = theLocalName;
        qname = theQname;
    }

    public void processEvent(UnmarshalRecord unmarshalRecord) throws SAXException {
        unmarshalRecord.endElement(namespaceUri, localName, qname);
    }
}
