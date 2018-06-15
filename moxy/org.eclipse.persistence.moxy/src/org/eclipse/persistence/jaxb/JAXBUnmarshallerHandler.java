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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.jaxb;

import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshallerHandler;
import org.eclipse.persistence.platform.xml.SAXDocumentBuilder;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;

/**
 *  <p>Implementation of UnmarshallerHandler to be used by JAXBUnmarshaller. </p>
 *  @see org.eclipse.persistence.jaxb.JAXBUnmarshaller
 *  @see javax.xml.bind.UnmarshallerHandler
 */
public class JAXBUnmarshallerHandler extends SAXDocumentBuilder implements UnmarshallerHandler {
    private JAXBUnmarshaller jaxbUnmarshaller;
    private boolean endDocumentTriggered;

    /**
     * Create a new JAXBUnmarshallerHandler with the specified JAXBUnmarshaller
     * @param newXMLUnmarshaller the JAXBUnmarshaller.
     */
    public JAXBUnmarshallerHandler(JAXBUnmarshaller newXMLUnmarshaller) {
        super();
        jaxbUnmarshaller = newXMLUnmarshaller;
    }

    /**
     * Event that is called at the end of processing the document.
     */
    @Override
    public void endDocument() throws SAXException {
        endDocumentTriggered = true;
        super.endDocument();
    }

    /**
     * Event that is called at the start of processing the document.
     */
    @Override
    public void startDocument() throws SAXException {
        endDocumentTriggered = false;
        super.startDocument();
    }

    /**
     * Return the unmarhalled document.  If the document is null or the endDocument
     * was never called then an IllegalStateExcpetion will be thrown.
     */
    @Override
    public Object getResult() throws JAXBException, IllegalStateException {
        Document document = getDocument();

        if ((document == null) || !endDocumentTriggered) {
            throw new IllegalStateException();
        }
        return jaxbUnmarshaller.unmarshal(document);
    }
}
