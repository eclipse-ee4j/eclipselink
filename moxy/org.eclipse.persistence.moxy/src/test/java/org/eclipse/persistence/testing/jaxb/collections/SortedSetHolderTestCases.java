/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.4.1 - initial implementation
package org.eclipse.persistence.testing.jaxb.collections;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class SortedSetHolderTestCases extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/collections/containertype2.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/collections/containertype2.json";

    public SortedSetHolderTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[1];
        classes[0] = SortedSetHolder.class;
        setClasses(classes);
        jaxbMarshaller.setProperty(MarshallerProperties.JSON_ATTRIBUTE_PREFIX, "@");
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.JSON_ATTRIBUTE_PREFIX, "@");
        jaxbMarshaller.setProperty(MarshallerProperties.JSON_MARSHAL_EMPTY_COLLECTIONS, Boolean.FALSE);
    }

    @Override
    protected SortedSetHolder getControlObject() {
        SortedSetHolder ch = new SortedSetHolder();
        ch.setCollection1(newContainer());
        ch.getCollection1().add(10);

        ch.setCollection2(newContainer());
        ch.getCollection2().add("one");

        ch.setCollection3(newContainer());
        ch.getCollection3().add(20);

        ch.setCollection5(newContainer());
        ch.getCollection5().add(new SortedSetHolder());

        ReferencedObject ref2 = new ReferencedObject();
        ref2.id = "2";
        ch.setCollection7(newContainer());
        ch.getCollection7().add(ref2);
        ch.getReferenced().add(ref2);

        ReferencedObject ref4 = new ReferencedObject();
        ref4.id = "4";
        ch.setCollection8(newContainer());
        ch.getCollection8().add(ref4);
        ch.getReferenced().add(ref4);

        ch.setCollection9(newContainer());
        ch.getCollection9().add(CoinEnum.PENNY);

        ch.setCollection12(newContainer());
        ch.getCollection12().add("abc");

        ch.setCollection13(newContainer());
        ch.getCollection13().add(123);

        ch.collection14 = newContainer();
        ch.collection14.add(123);

        return ch;
    }

    private SortedSet newContainer() {
        return new TreeSet();
    }

}
