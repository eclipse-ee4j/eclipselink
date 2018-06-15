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
package org.eclipse.persistence.testing.oxm.mappings.anycollection.withgroupingelement;

/**
 *  @version $Header: AnyCollectionComplexChildrenTestCases.java 29-jun-2007.13:21:19 dmahar Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */

import java.util.Vector;

import org.eclipse.persistence.testing.oxm.mappings.XMLWithJSONMappingTestCases;

public class AnyCollectionComplexChildrenTestCases extends XMLWithJSONMappingTestCases {
    public AnyCollectionComplexChildrenTestCases(String name) throws Exception {
        super(name);
        setProject(new AnyCollectionWithGroupingElementProject());
        setControlDocument("org/eclipse/persistence/testing/oxm/mappings/anycollection/withgroupingelement/complex_children.xml");
        setControlJSON("org/eclipse/persistence/testing/oxm/mappings/anycollection/withgroupingelement/complex_children.json");
    }

    public Object getControlObject() {
        Root root = new Root();
        Vector any = new Vector();
        Child child = new Child();
        child.setContent("Child1");
        any.addElement(child);
        child = new Child();
        child.setContent("Child2");
        any.addElement(child);
        root.setAny(any);

        return root;
    }
}

