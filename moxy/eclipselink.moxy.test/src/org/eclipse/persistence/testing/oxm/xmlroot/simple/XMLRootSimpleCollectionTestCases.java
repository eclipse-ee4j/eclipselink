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
package org.eclipse.persistence.testing.oxm.xmlroot.simple;

import java.util.ArrayList;
import java.util.List;

import junit.textui.TestRunner;

import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class XMLRootSimpleCollectionTestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/xmlroot/simple/listofnumbers.xml";

    public XMLRootSimpleCollectionTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setProject(new XMLRootSimpleCollectionProject());
    }

    public static void main(String[] args) {
        String[] arguments = {
                "-c",
                "org.eclipse.persistence.testing.oxm.xmlroot.simple.XMLRootSimpleCollectionTestCases" };
        TestRunner.main(arguments);
    }

    public Object getControlObject() {
        RootObjectWithSimpleCollection testObject = new RootObjectWithSimpleCollection();
        List theList = new ArrayList();
        theList.add(1);
        theList.add(2);
        theList.add(3);

        testObject.setTheList(theList);
        return testObject;
    }

}
