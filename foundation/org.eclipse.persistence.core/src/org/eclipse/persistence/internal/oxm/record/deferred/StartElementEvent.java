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

    public void processEvent(UnmarshalRecord unmarshalRecord) throws SAXException {
        unmarshalRecord.startElement(namespaceUri, localName, qname, attrs);
    }
}
