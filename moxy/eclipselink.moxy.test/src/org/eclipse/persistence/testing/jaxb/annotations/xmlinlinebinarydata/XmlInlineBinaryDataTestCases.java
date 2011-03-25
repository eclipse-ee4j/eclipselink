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
 *     Blaise Doughan - 2.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmlinlinebinarydata;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

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
        System.out.println(a.toString());
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