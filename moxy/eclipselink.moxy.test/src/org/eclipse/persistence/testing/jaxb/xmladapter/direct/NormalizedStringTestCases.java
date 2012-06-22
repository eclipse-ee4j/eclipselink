/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.3.2 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmladapter.direct;

import java.io.InputStream;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.w3c.dom.Document;

public class NormalizedStringTestCases extends JAXBTestCases {

    private static final String XML_RESOURCE_READ = "org/eclipse/persistence/testing/jaxb/xmladapter/normalizedstring_read.xml"; 
    private static final String XML_RESOURCE_WRITE = "org/eclipse/persistence/testing/jaxb/xmladapter/normalizedstring_write.xml"; 

    public NormalizedStringTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE_READ);
        setClasses(new Class[] {NormalizedStringRoot.class});
    }

    @Override
    protected Object getControlObject() {
        NormalizedStringRoot root = new NormalizedStringRoot();
        root.attributeField = "     A   a   ";
        root.setAttributeProperty("   B   b   ");
        root.elementField = "   C   c   ";
        root.setElementProperty("   D   d   ");
        return root;
    }

    @Override
    protected Document getWriteControlDocument() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE_WRITE);
        Document writeDocument = parser.parse(inputStream);
        inputStream.close();
        return writeDocument;
    }

}
