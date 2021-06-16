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
