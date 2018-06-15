/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.mappings.anycollection.withoutgroupingelement;

import java.util.ArrayList;
import java.util.Vector;
import junit.textui.TestRunner;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class AnyCollectionNonRootTestCases extends XMLMappingTestCases {
    public AnyCollectionNonRootTestCases(String name) throws Exception {
        super(name);
        setProject(new AnyCollectionWithGroupingElementNonRootProject());
        setControlDocument("org/eclipse/persistence/testing/oxm/mappings/anycollection/withoutgroupingelement/complex_children_non_root.xml");
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.anycollection.withoutgroupingelement.AnyCollectionNonRootTestCases" };
        TestRunner.main(arguments);
    }

    public Object getControlObject() {
        Wrapper wrapper = new Wrapper();

        ArrayList roots = new ArrayList();
        Root root = new Root();
        Vector any = new Vector();
        Child child = new Child();
        child.setContent("Child1");
        any.addElement(child);
        child = new Child();
        child.setContent("Child2");
        any.addElement(child);
        root.setAny(any);

        roots.add(root);
        wrapper.setRoots(roots);
        return wrapper;
    }
}
