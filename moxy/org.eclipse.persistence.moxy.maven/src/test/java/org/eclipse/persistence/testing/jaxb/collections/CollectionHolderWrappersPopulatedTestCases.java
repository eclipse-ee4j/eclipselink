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
//  - rbarkhouse - 27 January 2012 - 2.3.3 - Initial implementation
package org.eclipse.persistence.testing.jaxb.collections;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.oxm.platform.XMLPlatform;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.w3c.dom.Document;

public class CollectionHolderWrappersPopulatedTestCases extends JAXBWithJSONTestCases
{

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/collections/emptycollectionholderwrapperspopulated.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/collections/emptycollectionholderwrapperspopulated.json";

    public CollectionHolderWrappersPopulatedTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);

        Class[] classes = new Class[1];
        classes[0] = CollectionHolderWrappers.class;
        setClasses(classes);
        initXsiType();
    }

    @Override
    protected Object getControlObject() {
        CollectionHolderWrappers obj  = new CollectionHolderWrappers();
        List<Integer> numbers = new ArrayList<Integer>();
        obj.collection1 = new ArrayList<Integer>();
        obj.collection1.add(10);
        obj.collection1.add(20);

        obj.collection2 = new ArrayList<Object>(obj.collection1);

        obj.collection3 = new ArrayList<Object>();
        obj.collection3.add(new CollectionHolderWrappers());

        obj.collection4 = new ArrayList<CollectionHolderWrappers>();
        obj.collection4.add(new CollectionHolderWrappers());

        obj.collection5 = new ArrayList<JAXBElement<String>>();
        obj.collection5.add(new JAXBElement<String>(new QName("root"), String.class, "abcvalue"));

        obj.collection6 = new ArrayList<CoinEnum>();
        obj.collection6.add(CoinEnum.DIME);

        obj.collection7 = new ArrayList<byte[]>();
        obj.collection7.add(new String("abc").getBytes());
        obj.collection7.add(new String("def").getBytes());
        return obj;
    }

    public boolean shouldRemoveWhitespaceFromControlDocJSON(){
        return false;
    }

    @Override
    public Object getReadControlObject() {
        CollectionHolderWrappers obj = (CollectionHolderWrappers)getControlObject();
        obj.collection3 = new ArrayList<Object>();

        Document doc = XMLPlatformFactory.getInstance().getXMLPlatform().createDocument();
        obj.collection3.add(doc.createElementNS("", "collectionHolderWrappers"));
        return obj;
    }
}

