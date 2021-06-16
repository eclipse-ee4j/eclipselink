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
//    Denise Smith - June 2013
package org.eclipse.persistence.testing.jaxb.xmlelementref;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class ByteArrayCollectionTestCases  extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelementref/bytearray.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelementref/bytearray.json";

    public ByteArrayCollectionTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[2];
        classes[0] = FooObjectFactory.class;
        classes[1] = Foo.class;
        setClasses(classes);
    }

    protected Object getControlObject() {
        Foo foo = new Foo();

        List theList = new ArrayList();
        byte[]  bytes = "abc".getBytes();
        byte[]  bytes2 = "def".getBytes();
        byte[]  bytes3 = "ghi".getBytes();
        byte[]  bytes4 = "jkl".getBytes();
        List byteArrayList = new ArrayList();
        byteArrayList.add(bytes);
        byteArrayList.add(bytes2);

        List byteArrayList2 = new ArrayList();
        byteArrayList2.add(bytes3);
        byteArrayList2.add(bytes4);

        JAXBElement elem1 = new JAXBElement<List>(new QName("things"), List.class, byteArrayList);
        JAXBElement elem2 = new JAXBElement<List>(new QName("things"), List.class, byteArrayList2);

        theList.add(elem1);
        theList.add(elem2);

        foo.things = theList;

        return foo;
     }
}
