/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.2 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.oxm.reader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.persistence.internal.oxm.record.DOMReader;
import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.w3c.dom.Document;

public class DOMReaderTestCases extends ReaderTestCases {

    private Document document;

    public DOMReaderTestCases(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        document = db.parse(xmlInputStream);
    }

    public void testDOMReader() throws Exception {
        DOMReader domReader = new DOMReader();
        TestContentHandler testContentHandler = new TestContentHandler();
        domReader.setContentHandler(testContentHandler);
        domReader.parse(document);

        assertEquals(getControlEvents(), testContentHandler.getEvents());
    }

}