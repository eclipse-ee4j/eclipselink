/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.4 - initial implementation
 ******************************************************************************/
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

public class NullContainerTestCases extends TestCase {

    public void testMarshal() throws Exception {
        JAXBContext jc = JAXBContextFactory.createContext(new Class[] {NullContainerRoot.class}, null);

        NullContainerRoot ncr = new NullContainerRoot();

        Marshaller marshaller = jc.createMarshaller();
        marshaller.marshal(ncr, new StringWriter());
    }

    public void testBinderMarshal() throws Exception {
        JAXBContext jc = JAXBContextFactory.createContext(new Class[] {NullContainerRoot.class}, null);

        NullContainerRoot ncr = new NullContainerRoot();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.newDocument();
        Binder binder = jc.createBinder();
        binder.marshal(ncr, document);
    }

}