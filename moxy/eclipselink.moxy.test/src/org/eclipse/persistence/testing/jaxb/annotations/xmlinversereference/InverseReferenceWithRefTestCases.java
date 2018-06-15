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
// Matt MacIvor - 2.4 - initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlinversereference;

import java.util.ArrayList;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class InverseReferenceWithRefTestCases extends JAXBWithJSONTestCases {
    private static final String XML_RESOURCE =  "org/eclipse/persistence/testing/jaxb/annotations/xmlinversereference/parent.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlinversereference/parent.json";

    public InverseReferenceWithRefTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[]{ Parent.class, Child.class, ChildSubclass.class });
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }

    public Object getControlObject() {
        Parent parent = new Parent();
        parent.children = new ArrayList<Child>();

        Child child = new Child();
        child.parent = parent;
        parent.children.add(child);

        child = new ChildSubclass();
        child.parent = parent;
        parent.children.add(child);

        return parent;

    }
}
