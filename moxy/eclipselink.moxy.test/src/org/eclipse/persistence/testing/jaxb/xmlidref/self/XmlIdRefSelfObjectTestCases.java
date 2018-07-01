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
//     bdoughan - April 14/2010 - 2.1 - Initial implementation
package org.eclipse.persistence.testing.jaxb.xmlidref.self;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Vector;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

/**
 *
 */
public class XmlIdRefSelfObjectTestCases extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlidref/self.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlidref/self.json";


    public XmlIdRefSelfObjectTestCases(String name) throws Exception {
        super(name);
        Class[] classes = new Class[2];
        classes[0] = Root.class;
        classes[1] = Item.class;
        setClasses(classes);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }

    protected Object getControlObject() {
        Item item1 = new Item();
        item1.m_name = "test";

        Item item2 = new Item();
        item2.m_name = "test2";
        item1.m_parent = item2;


        Root obj = new Root();
        obj.items = new ArrayList<Item>();
        obj.items.add(item1);
        obj.items.add(item2);
        return obj;
    }


}
