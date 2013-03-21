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
*     mmacivor - September 18/2009 - 2.0 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.internal.oxm.record;

import javax.xml.stream.XMLEventReader;

import org.xml.sax.InputSource;

/**
 * This class is used to expose an XMLEventReader as an InputSource. 
 */
public class XMLEventReaderInputSource extends InputSource {
    XMLEventReader xmlEventReader;

    public XMLEventReaderInputSource(XMLEventReader xmlEventReader) {
        this.xmlEventReader = xmlEventReader;
    }

    public XMLEventReader getXmlEventReader() {
        return xmlEventReader;
    }

    public void setXmlEventReader(XMLEventReader xmlEventReader) {
        this.xmlEventReader = xmlEventReader;
    }    

}
