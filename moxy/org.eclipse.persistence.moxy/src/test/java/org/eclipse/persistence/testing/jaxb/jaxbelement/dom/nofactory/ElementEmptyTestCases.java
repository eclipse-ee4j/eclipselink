/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith - October 2012
package org.eclipse.persistence.testing.jaxb.jaxbelement.dom.nofactory;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.eclipse.persistence.testing.jaxb.jaxbelement.dom.ObjectFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ElementEmptyTestCases extends JAXBWithJSONTestCases{

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/jaxbelement/dom/nofactory/element_empty.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/jaxbelement/dom/nofactory/element_empty.json";

    public ElementEmptyTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[]{});
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }

    @Override
    protected Object getControlObject() {
        ObjectFactory factory = new ObjectFactory();
        Document doc;
        try {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element elm = doc.createElementNS("AGroupDef/annotation", "rootchild");
            elm.setTextContent("");
            JAXBElement  obj = new JAXBElement<Object>(new QName("mynamespace", "mynewname"), Object.class, elm);
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
            fail("An exception was thrown.");
            return null;
        }
    }

    public boolean isUnmarshalTest(){
        return false;
    }


}
