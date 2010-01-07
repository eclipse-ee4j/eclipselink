/*******************************************************************************
* Copyright (c) 1998, 2010 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
*     bdoughan - Feb 27/2009 - 1.1 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.oxm.mappings.anycollection.defaultnamespace;

import java.io.InputStream;
import java.util.Vector;
import junit.textui.TestRunner;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.w3c.dom.Document;

public class AnyCollectionComplexChildrenTestCases extends XMLMappingTestCases {

    public AnyCollectionComplexChildrenTestCases(String name) throws Exception {
        super(name);
        setProject(new DefaultNamespaceProject());
        setControlDocument("org/eclipse/persistence/testing/oxm/mappings/anycollection/defaultnamespace/complex_children.xml");
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
