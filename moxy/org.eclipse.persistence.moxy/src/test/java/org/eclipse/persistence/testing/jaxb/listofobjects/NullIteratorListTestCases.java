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
//     Blaise Doughan - 2.4 - initial implementation
package org.eclipse.persistence.testing.jaxb.listofobjects;

import java.io.StringWriter;

import jakarta.xml.bind.Binder;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
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
