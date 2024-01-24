/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     rbarkhouse - 2009-05-05 14:32:00 - initial implementation
package org.eclipse.persistence.testing.oxm.mappings.directcollection.listoflists;

import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

import java.util.ArrayList;

public class XMLDirectCollectionOfListsTestCases extends XMLMappingTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/directcollection/listoflists/DirectCollectionOfLists.xml";

    public XMLDirectCollectionOfListsTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setProject(new RootProject());
    }

    @Override
    protected Object getControlObject() {
        Root root = new Root();

        ArrayList<Double> values1 = new ArrayList<>();
        values1.add(1.2);
        values1.add(3.4);
        values1.add(5.6);

        ArrayList<Double> values2 = new ArrayList<>();
        values2.add(-7.8);
        values2.add(-9.0);
        values2.add(-1.2);

        ArrayList<ArrayList<Double>> itemCollection = new ArrayList<>();
        itemCollection.add(values1);
        itemCollection.add(values2);

        root.items = itemCollection;

        return root;
    }

}
