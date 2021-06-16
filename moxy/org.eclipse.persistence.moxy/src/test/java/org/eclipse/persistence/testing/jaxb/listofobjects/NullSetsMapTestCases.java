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
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.w3c.dom.Document;

import junit.framework.TestCase;

public class NullSetsMapTestCases extends TestCase {

    public void testMarshal() throws JAXBException {
        JAXBContext jc = JAXBContextFactory.createContext(new Class[] {NullSetsMapRoot.class}, null);

        NullSetsMapRoot nsmr = new NullSetsMapRoot();
        nsmr.setNullSetsMap(new NullSetsMap());

        Marshaller marshaller = jc.createMarshaller();
        marshaller.marshal(nsmr, new StringWriter());
    }

    public void testBinderMarshal() throws Exception {
        JAXBContext jc = JAXBContextFactory.createContext(new Class[] {NullSetsMapRoot.class}, null);

        NullSetsMapRoot nsmr = new NullSetsMapRoot();
        nsmr.setNullSetsMap(new NullSetsMap());

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.newDocument();
        Binder binder = jc.createBinder();
        binder.marshal(nsmr, document);
    }

}
