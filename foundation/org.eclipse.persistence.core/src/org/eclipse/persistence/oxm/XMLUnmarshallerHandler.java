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
package org.eclipse.persistence.oxm;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.oxm.UnmarshallerHandler;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.platform.xml.SAXDocumentBuilder;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * <p>Class used to unmarshal SAX events to objects.
 *
 * <p>Create an XMLUnmarshallerHandler from an XMLUnmarshaller.<br>
 *  <em>Code Sample</em><br>
 *  <code>
 *  XMLContext context = new XMLContext("mySessionName");<br>
 *  XMLUnmarshaller unmarshaller = context.createUnmarshaller();<br>
 *  XMLUnmarshallerHandler unmarshallerHandler = unmarshaller.getUnmarshallerHandler();<br>
 *  <code>
 *
 * <p>Use the UnmarshallerHandler with an XMLReader<br>
 *  <em>Code Sample</em><br>
 *  <code>
 *  SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();<br>
 *  saxParserFactory.setNamespaceAware(true);<br>
 *  SAXParser saxParser = saxParserFactory.newSAXParser();<br>
 *  XMLReader xmlReader = saxParser.getXMLReader();<br>
 *  xmlReader.setContentHandler(xmlUnmarshallerHandler);<br>
 *  FileInputStream inputStream = new FileInputStream("MyFile.xml");<br>
 *  InputSource inputSource = new InputSource(inputStream);<br>
 *  xmlReader.parse(inputSource);<br>
 *  Object result = xmlUnmarshallerHandler.getResult();<br>
 *  <code>
 *
 * <p>XML that can be unmarshalled is XML which has a root tag that corresponds
 * to a default root element on an XMLDescriptor in the TopLink project associated
 * with the XMLContext.
 *
 * @see org.eclipse.persistence.oxm.XMLUnmarshaller
 */
public class XMLUnmarshallerHandler extends SAXDocumentBuilder implements UnmarshallerHandler {
    private XMLUnmarshaller xmlUnmarshaller;
    private boolean endDocumentTriggered;

    XMLUnmarshallerHandler(XMLUnmarshaller xmlUnmarshaller) {
        super();
        this.xmlUnmarshaller = xmlUnmarshaller;
    }

    public void endDocument() throws SAXException {
        endDocumentTriggered = true;
        super.endDocument();
    }

    public void startDocument() throws SAXException {
        endDocumentTriggered = false;
        super.startDocument();
    }

    /**
     * Returns the object that was unmarshalled from the SAX events.
     * @return the resulting object
     * @throws XMLMarshalException if an error occurred during unmarshalling
     */
    public Object getResult() {
        Document document = getDocument();

        if ((document == null) || !endDocumentTriggered) {
            throw XMLMarshalException.illegalStateXMLUnmarshallerHandler();
        }
        return xmlUnmarshaller.unmarshal(document);
    }
}
