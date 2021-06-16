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
//     rbarkhouse - 2009-10-06 10:56:35 - initial implementation
package org.eclipse.persistence.testing.oxm.mappings.anycollection.reuse;

import java.net.URL;
import java.util.LinkedList;
import java.util.Stack;
import java.util.Vector;

import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class AnyCollectionReuseTestCases extends XMLMappingTestCases {

    public AnyCollectionReuseTestCases(String name) throws Exception {
        super(name);
        setProject(new ReuseProject());
        setControlDocument("org/eclipse/persistence/testing/oxm/mappings/anycollection/defaultnamespace/complex_children.xml");
    }

    public Object getControlObject() {
        Root root = new Root();
        Vector any = new Stack();

        Child child = new Child();
        child.setContent("Child1");
        any.addElement(child);

        child = new Child();
        child.setContent("Child2");
        any.addElement(child);

        root.setAny(any);
        return root;
    }

    public void testContainerReused() throws Exception {
        URL url = ClassLoader.getSystemResource(resourceName);
        Root testObject = (Root) xmlUnmarshaller.unmarshal(url);

        assertEquals("This mapping's container was not reused.", Stack.class, testObject.getAny().getClass());
    }


}
