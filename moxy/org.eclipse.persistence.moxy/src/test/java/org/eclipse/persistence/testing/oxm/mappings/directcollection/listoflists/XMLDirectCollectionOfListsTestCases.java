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
