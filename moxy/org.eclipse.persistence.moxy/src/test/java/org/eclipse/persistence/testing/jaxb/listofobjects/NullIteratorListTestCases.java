/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Blaise Doughan - 2.4 - initial implementation
package org.eclipse.persistence.testing.jaxb.listofobjects;

import java.io.StringWriter;

import javax.xml.bind.Binder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.w3c.dom.Document;

import junit.framework.TestCase;

public class NullIteratorListTestCases extends TestCase {

    public void testMarshal() throws Exception {
        JAXBContext jc = JAXBContextFactory.createContext(new Class[] {NullIteratorListRoot.class}, null);

        NullIteratorListRoot nilr = new NullIteratorListRoot();
        nilr.setElementList(new NullIteratorList());
        nilr.setChoiceList(new NullIteratorList());
        nilr.setAnyList(new NullIteratorList());

        Marshaller marshaller = jc.createMarshaller();
        marshaller.marshal(nilr, new StringWriter());
    }

    public void testBinderMarshal() throws Exception {
        JAXBContext jc = JAXBContextFactory.createContext(new Class[] {NullIteratorListRoot.class}, null);

        NullIteratorListRoot nilr = new NullIteratorListRoot();
        nilr.setElementList(new NullIteratorList());
        nilr.setChoiceList(new NullIteratorList());
        nilr.setAnyList(new NullIteratorList());

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.newDocument();
        Binder binder = jc.createBinder();
        binder.marshal(nilr, document);
    }

}
