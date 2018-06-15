/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     bdoughan - June 24/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.internal.oxm.record;

import javax.xml.stream.XMLStreamReader;

import org.xml.sax.InputSource;

/**
 * This class is used to expose an XMLStreamReader as an InputSource.
 */
public class XMLStreamReaderInputSource extends InputSource {

    XMLStreamReader xmlStreamReader;

    public XMLStreamReaderInputSource(XMLStreamReader xmlStreamReader) {
        this.xmlStreamReader = xmlStreamReader;
    }

    public XMLStreamReader getXmlStreamReader() {
        return xmlStreamReader;
    }

    public void setXmlStreamReader(XMLStreamReader xmlStreamReader) {
        this.xmlStreamReader = xmlStreamReader;
    }

}
