/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmlinlinebinary;

import java.io.InputStream;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.sax.SAXSource;

import org.eclipse.persistence.internal.oxm.record.XMLStreamReaderInputSource;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import junit.framework.TestCase;

public class LargeInlineBinaryTestCases extends OXTestCase {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlinlinebinary/largeInlineBinaryData.xml";

    public LargeInlineBinaryTestCases(String name) {
        super(name);
    }

    public void testOptimizedUnmarshal() throws Exception {
        if(null != XML_INPUT_FACTORY) {
            InputStream instream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
            XMLStreamReader xmlStreamReader = XML_INPUT_FACTORY.createXMLStreamReader(instream);

            XMLReader xmlReader = new LargeInlineBinaryDataXMLStreamReaderReader();
            InputSource inputSource = new XMLStreamReaderInputSource(xmlStreamReader);
            SAXSource saxSource = new SAXSource(xmlReader, inputSource);

            JAXBContext jc = JAXBContextFactory.createContext(new Class[] {LargeInlineBinaryRoot.class}, null);
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            LargeInlineBinaryRoot root = (LargeInlineBinaryRoot) unmarshaller.unmarshal(saxSource);

            assertSame(LargeInlineBinaryDataXMLStreamReaderReader.DATA_HANDLER, root.getDataHandler());
            assertSame(LargeInlineBinaryDataXMLStreamReaderReader.IMAGE, root.getImage());
            assertSame(LargeInlineBinaryDataXMLStreamReaderReader.SOURCE, root.getSource());
            assertSame(LargeInlineBinaryDataXMLStreamReaderReader.MIME_MULTIPART, root.getMimeMultipart());
            assertSame(LargeInlineBinaryDataXMLStreamReaderReader.DATA_HANDLER, root.getDataHandlerList().get(0));
            assertSame(LargeInlineBinaryDataXMLStreamReaderReader.IMAGE, root.getImageList().get(0));
            assertSame(LargeInlineBinaryDataXMLStreamReaderReader.SOURCE, root.getSourceList().get(0));
            assertSame(LargeInlineBinaryDataXMLStreamReaderReader.MIME_MULTIPART, root.getMimeMultipartList().get(0));
        }
    }

}
