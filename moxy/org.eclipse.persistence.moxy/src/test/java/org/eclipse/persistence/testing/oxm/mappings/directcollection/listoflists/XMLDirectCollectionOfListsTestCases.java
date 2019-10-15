/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;

import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class XMLDirectCollectionOfListsTestCases extends XMLMappingTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/directcollection/listoflists/DirectCollectionOfLists.xml";

    public XMLDirectCollectionOfListsTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setProject(new RootProject());
    }

    protected Object getControlObject() {
        Root root = new Root();

        ArrayList<Double> values1 = new ArrayList<Double>();
        values1.add(Double.valueOf(1.2));
        values1.add(Double.valueOf(3.4));
        values1.add(Double.valueOf(5.6));

        ArrayList<Double> values2 = new ArrayList<Double>();
        values2.add(Double.valueOf(-7.8));
        values2.add(Double.valueOf(-9.0));
        values2.add(Double.valueOf(-1.2));

        ArrayList<ArrayList<Double>> itemCollection = new ArrayList<ArrayList<Double>>();
        itemCollection.add(values1);
        itemCollection.add(values2);

        root.items = itemCollection;

        return root;
    }

}
