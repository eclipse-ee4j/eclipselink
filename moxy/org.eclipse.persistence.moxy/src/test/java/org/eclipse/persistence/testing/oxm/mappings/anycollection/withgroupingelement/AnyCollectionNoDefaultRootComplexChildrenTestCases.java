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
 *  @version $Header: AnyCollectionNoDefaultRootComplexChildrenTestCases.java 29-jun-2007.13:21:27 dmahar Exp $
 *  @author  mfobrien
 *  @since   10.1.3.1.0
 */
import java.util.Vector;

import junit.textui.TestRunner;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.oxm.mappings.XMLAnyCollectionMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class AnyCollectionNoDefaultRootComplexChildrenTestCases extends XMLMappingTestCases {
    public AnyCollectionNoDefaultRootComplexChildrenTestCases(String name) throws Exception {
        super(name);
        Project p = new AnyCollectionNoDefaultRootWithGroupingElementProject();
        ((XMLAnyCollectionMapping)p.getDescriptor(Root.class).getMappingForAttributeName("any")).useCollectionClass(java.util.ArrayList.class);
        setProject(p);
        setControlDocument("org/eclipse/persistence/testing/oxm/mappings/anycollection/withgroupingelement/no_default_root_element_children.xml");
        expectsMarshalException = true;
    }

    public boolean isUnmarshalTest() {
        return false;
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
        // add object vector to root object
        root.setAny(any);
        return root;
    }

    @Override
    public void assertMarshalException(Exception exception) throws Exception {
        if(exception.getClass() == XMLMarshalException.class) {
            XMLMarshalException xmlMarshalException = (XMLMarshalException) exception;
            if(XMLMarshalException.DEFAULT_ROOT_ELEMENT_NOT_SPECIFIED == xmlMarshalException.getErrorCode()) {
                return;
            }
        }
        throw exception;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.anycollection.withgroupingelement.AnyCollectionNoDefaultRootComplexChildrenTestCases" };
        TestRunner.main(arguments);
    }

}
