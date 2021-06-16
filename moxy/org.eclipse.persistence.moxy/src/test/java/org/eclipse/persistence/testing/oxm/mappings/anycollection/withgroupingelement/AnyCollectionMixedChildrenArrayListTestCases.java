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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.mappings.anycollection.withgroupingelement;


/**
 *  @version $Header: AnyCollectionMixedChildrenArrayListTestCases.java 29-jun-2007.13:21:21 dmahar Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */
import java.util.Vector;
import org.eclipse.persistence.oxm.mappings.XMLAnyCollectionMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class AnyCollectionMixedChildrenArrayListTestCases extends XMLMappingTestCases {
    public AnyCollectionMixedChildrenArrayListTestCases(String name) throws Exception {
        super(name);
        Project p = new AnyCollectionWithGroupingElementProject();
        ((XMLAnyCollectionMapping)p.getDescriptor(Root.class).getMappingForAttributeName("any")).useCollectionClass(java.util.ArrayList.class);
        ;
        setProject(p);
        setControlDocument("org/eclipse/persistence/testing/oxm/mappings/anycollection/withgroupingelement/mixed_children.xml");
    }

    public Object getControlObject() {
        Root root = new Root();
        Vector any = new Vector();
        Child child = new Child();
        child.setContent("Child1");
        any.addElement(child);
        any.addElement("Root Text!!");
        child = new Child();
        child.setContent("Child2");
        any.addElement(child);
        any.addElement("More Text!!");
        root.setAny(any);
        return root;
    }
}
