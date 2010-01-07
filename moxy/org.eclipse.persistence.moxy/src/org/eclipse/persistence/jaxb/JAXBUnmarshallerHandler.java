/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.jaxb;

import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshallerHandler;
import org.eclipse.persistence.platform.xml.SAXDocumentBuilder;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;

public class JAXBUnmarshallerHandler extends SAXDocumentBuilder implements UnmarshallerHandler {
    private JAXBUnmarshaller jaxbUnmarshaller;
    private boolean endDocumentTriggered;

    public JAXBUnmarshallerHandler(JAXBUnmarshaller newXMLUnmarshaller) {
        super();
        jaxbUnmarshaller = newXMLUnmarshaller;
    }

    public void endDocument() throws SAXException {
        endDocumentTriggered = true;
        super.endDocument();
    }

    public void startDocument() throws SAXException {
        endDocumentTriggered = false;
        super.startDocument();
    }

    public Object getResult() throws JAXBException, IllegalStateException {
        Document document = getDocument();

        if ((document == null) || !endDocumentTriggered) {
            throw new IllegalStateException();
        }
        return jaxbUnmarshaller.unmarshal(document);
    }
}
