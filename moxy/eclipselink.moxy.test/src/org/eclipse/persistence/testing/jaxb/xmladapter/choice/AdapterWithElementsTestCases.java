/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// Matt MacIvor - July 4th 2011
package org.eclipse.persistence.testing.jaxb.xmladapter.choice;

import java.util.ArrayList;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class AdapterWithElementsTestCases extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmladapter/choice.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmladapter/choice.json";

    public AdapterWithElementsTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[] {Foo.class, BarA.class, BarB.class, BarC.class};
        setClasses(classes);
    }

    protected Object getControlObject() {
        Foo foo = new Foo();
        foo.singleChoice = new BarA();

        foo.collectionChoice = new ArrayList<Object>();

        foo.collectionChoice.add(new BarB());
        foo.collectionChoice.add("test string");
        BarC barC = new BarC();
        barC.a = "a";
        barC.b = "b";
        foo.collectionChoice.add(barC);
        foo.collectionChoice.add(new Integer(123));

        return foo;
    }

}
