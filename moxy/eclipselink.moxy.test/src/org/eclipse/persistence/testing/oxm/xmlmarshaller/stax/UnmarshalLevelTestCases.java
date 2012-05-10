/*******************************************************************************
* Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
*     bdoughan - August 11/2009 - 2.0 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.oxm.xmlmarshaller.stax;

import java.io.InputStream;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.eclipse.persistence.internal.oxm.record.XMLStreamReaderInputSource;
import org.eclipse.persistence.internal.oxm.record.XMLStreamReaderReader;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.testing.oxm.OXTestCase;

public class UnmarshalLevelTestCases extends OXTestCase {

    private XMLInputFactory xmlInputFactory;
    
    public UnmarshalLevelTestCases(String name) {
        super(name);
    }

    public void setUp() {
        try {
            xmlInputFactory = XMLInputFactory.newInstance();
        } catch(FactoryConfigurationError e) {
            xmlInputFactory = null;
        }
    }

    public void testUnmarshalLevelDocument() throws Exception {
        if(null == xmlInputFactory) {
            return;
        }

        InputStream instream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/oxm/xmlmarshaller/stax/address.xml");
        XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(instream);

        XMLStreamReaderReader staxReader = new XMLStreamReaderReader();
        XMLContext xmlContext = new XMLContext(new AddressProject());
        XMLUnmarshaller xmlUnmarshaller = xmlContext.createUnmarshaller();
        staxReader.setErrorHandler(xmlUnmarshaller.getErrorHandler());
        XMLStreamReaderInputSource inputSource = new XMLStreamReaderInputSource(xmlStreamReader);
        xmlUnmarshaller.unmarshal(staxReader, inputSource);

        if(xmlStreamReader.getEventType() != XMLStreamReader.END_DOCUMENT) {
            fail("The last event type should have the been end document");
        }

        instream.close();
    }

    public void testUnmarshalLevelElement() throws Exception {
        if(null == xmlInputFactory) {
            return;
        }

        InputStream instream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/oxm/xmlmarshaller/stax/address.xml");
        XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(instream);
        xmlStreamReader.next();  // Advance event to start element

        XMLStreamReaderReader staxReader = new XMLStreamReaderReader();
        XMLContext xmlContext = new XMLContext(new AddressProject());
        XMLUnmarshaller xmlUnmarshaller = xmlContext.createUnmarshaller();
        staxReader.setErrorHandler(xmlUnmarshaller.getErrorHandler());
        XMLStreamReaderInputSource inputSource = new XMLStreamReaderInputSource(xmlStreamReader);
        xmlUnmarshaller.unmarshal(staxReader, inputSource);

        if(xmlStreamReader.getEventType() != XMLStreamReader.END_ELEMENT) {
            fail("The last event type should have the been end document");
        }
        if(!xmlStreamReader.getLocalName().equals("address")) {
            fail("The last local name should have been 'address'");
        }

        instream.close();
    }
    
    public void testUnmarshalLevelElementWithClass() throws Exception{
    	   if(null == xmlInputFactory) {
               return;
           }

           InputStream instream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/oxm/xmlmarshaller/stax/address.xml");
           XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(instream);
           xmlStreamReader.next();  // Advance event to start element

           XMLStreamReaderReader staxReader = new XMLStreamReaderReader();
           XMLContext xmlContext = new XMLContext(new AddressProject());
           XMLUnmarshaller xmlUnmarshaller = xmlContext.createUnmarshaller();
           staxReader.setErrorHandler(xmlUnmarshaller.getErrorHandler());
           XMLStreamReaderInputSource inputSource = new XMLStreamReaderInputSource(xmlStreamReader);
           xmlUnmarshaller.unmarshal(staxReader, inputSource, Address.class);          

           if(xmlStreamReader.getEventType() != XMLStreamReader.END_ELEMENT) {
               fail("The last event type should have the been end document");
           }
           if(!xmlStreamReader.getLocalName().equals("address")) {
               fail("The last local name should have been 'address'");
           }

           instream.close();
    }

}