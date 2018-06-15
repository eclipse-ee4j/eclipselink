/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//  - rbarkhouse - 08 March 2013 - 2.4.2 - Initial implementation
package org.eclipse.persistence.testing.jaxb.jaxbcontext.multielemref;

import java.io.ByteArrayOutputStream;

import javax.xml.bind.JAXBContext;

import junit.framework.TestCase;

import org.eclipse.persistence.exceptions.JAXBException;
import org.eclipse.persistence.jaxb.JAXBContextFactory;

public class MultipleXmlElementRefTestCases extends TestCase {

    public void testCreateJAXBContextWithMultipleXmlElementRefSameType() throws Exception {
        Exception caughtException = null;
        try {
            Class[] classes = new Class[] { Root.class, ChildOne.class, ChildTwo.class };
            JAXBContext ctx = JAXBContextFactory.createContext(classes, null, this.getClass().getClassLoader());

            Root root = new Root();

            ChildOne c1 = new ChildOne();
            c1.name = "C-ONE";
            ChildTwo c2 = new ChildTwo();
            c2.name = "C-two";
            ChildTwo c3 = new ChildTwo();
            c3.name = "C-333";

            root.thing = c1;
            root.things.add(c2);
            root.things.add(c3);

            ctx.createMarshaller().marshal(root, new ByteArrayOutputStream());
        } catch (javax.xml.bind.JAXBException e) {
            caughtException = e;
            Throwable nested = e.getLinkedException();
            if (nested instanceof JAXBException) {
                assertEquals(JAXBException.MULTIPLE_XMLELEMREF, ((JAXBException) nested).getErrorCode());
                return;
            }
        } catch (Exception e) {
            caughtException = e;
        }

        fail("A multiple XmlElementRef exception should have been thrown, but was: " + caughtException);
    }

    public void testCreateJAXBContextWithMultipleXmlElementRefDifferentType() throws Exception {
        try {
            Class[] classes = new Class[] { RootTwo.class, ChildOne.class, Data.class };
            JAXBContext ctx = JAXBContextFactory.createContext(classes, null, this.getClass().getClassLoader());

            RootTwo root = new RootTwo();

            ChildOne c = new ChildOne();
            c.name = "C-ONE";
            Data d = new Data();
            d.value = "FOO";

            root.thing = c;
            root.data = d;

            ctx.createMarshaller().marshal(root, new ByteArrayOutputStream());
        } catch (Exception e) {
            fail("No exception should be thrown for multiple XmlElementRef properties of different types, but caught: " + e);
        }
    }

}
