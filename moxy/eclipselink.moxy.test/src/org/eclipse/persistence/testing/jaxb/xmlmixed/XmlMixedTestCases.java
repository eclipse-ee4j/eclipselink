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
 * Matt MacIvor - July 4th 2011
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlmixed;

import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

import junit.framework.TestCase;

public class XmlMixedTestCases extends JAXBTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlmixed/root.xml";

    public XmlMixedTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {Root.class});
        setControlDocument(XML_RESOURCE);
    }

    protected Object getControlObject() {
        Root root = new Root();
        root.setAttr("attribute value");
        root.setElem("element value");
        root.setObjects(new ArrayList<Object>());
        root.getObjects().add("Text Value");
        return root;
    }
}
