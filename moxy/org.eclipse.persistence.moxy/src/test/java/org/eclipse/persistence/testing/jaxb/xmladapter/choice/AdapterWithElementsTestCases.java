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
