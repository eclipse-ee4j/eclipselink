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
package org.eclipse.persistence.testing.jaxb.annotations.xmlinlinebinarydata;

import java.io.StringWriter;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;

import org.eclipse.persistence.jaxb.JAXBContextFactory;

import junit.framework.TestCase;

public class XmlInlineBinaryDataTestCases extends TestCase {

    private JAXBContext jc;

    public XmlInlineBinaryDataTestCases(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        jc = JAXBContextFactory.createContext(new Class[] {FieldRoot.class, PropertyRoot.class}, null);
    }

    public void testFieldAnnotation() throws Exception {
        Marshaller marshaller = jc.createMarshaller();
        TestAttachmentMarshaller attachmentMarshaller = new TestAttachmentMarshaller();
        marshaller.setAttachmentMarshaller(attachmentMarshaller);

        FieldRoot fieldRoot = new FieldRoot();
        fieldRoot.setA("A".getBytes());
        fieldRoot.setB("B".getBytes());
        fieldRoot.setC("C".getBytes());
        StringWriter a = new StringWriter();
        marshaller.marshal(fieldRoot, a);
        assertEquals(2, attachmentMarshaller.getAttachmentCount());
    }

    public void testPropertyAnnotation() throws Exception {
        Marshaller marshaller = jc.createMarshaller();
        TestAttachmentMarshaller attachmentMarshaller = new TestAttachmentMarshaller();
        marshaller.setAttachmentMarshaller(attachmentMarshaller);

        PropertyRoot propertyRoot = new PropertyRoot();
        propertyRoot.setA("A".getBytes());
        propertyRoot.setB("B".getBytes());
        propertyRoot.setC("C".getBytes());
        marshaller.marshal(propertyRoot, new StringWriter());
        assertEquals(2, attachmentMarshaller.getAttachmentCount());
    }

}
