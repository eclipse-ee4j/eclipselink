/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.4.1 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.collections;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.NavigableSet;
import java.util.Queue;
import java.util.TreeSet;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class QueueHolderTestCases extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/collections/containertype.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/collections/containertype.json";

    public QueueHolderTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[1];
        classes[0] = QueueHolder.class;
        setClasses(classes);
        jaxbMarshaller.setProperty(MarshallerProperties.JSON_ATTRIBUTE_PREFIX, "@");
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.JSON_ATTRIBUTE_PREFIX, "@");
        jaxbMarshaller.setProperty(MarshallerProperties.JSON_MARSHAL_EMPTY_COLLECTIONS, Boolean.FALSE);
    }

    @Override
    protected QueueHolder getControlObject() {
        QueueHolder ch = new QueueHolder();
        ch.setCollection1(newContainer());
        ch.getCollection1().add(10);

        ch.setCollection2(newContainer());
        ch.getCollection2().add("one");

        ch.setCollection3(newContainer());
        ch.getCollection3().add(20);

        ReferencedObject ref1 = new ReferencedObject();
        ref1.id = "1";
        ch.setCollection4(newContainer());
        ch.getCollection4().add(ref1);
        ch.getReferenced().add(ref1);

        ch.setCollection5(newContainer());
        ch.getCollection5().add(new QueueHolder());

        ch.setCollection6(newContainer());
        ch.getCollection6().add(new JAXBElement<String>(new QName("root"), String.class, "60"));

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

        ch.setCollection11(newContainer());
        ch.getCollection11().add("abc".getBytes());

        ch.setCollection12(newContainer());
        ch.getCollection12().add("abc");

        ch.setCollection13(newContainer());
        ch.getCollection13().add(123);

        ch.collection14 = newContainer();
        ch.collection14.add(123);

        return ch;
    }

    private Queue newContainer() {
        return new LinkedList();
    }

}