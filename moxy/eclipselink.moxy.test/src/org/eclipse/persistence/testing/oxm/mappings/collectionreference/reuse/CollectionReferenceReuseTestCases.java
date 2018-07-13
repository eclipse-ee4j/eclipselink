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
//     rbarkhouse - 2009-10-07 13:24:58 - initial implementation
package org.eclipse.persistence.testing.oxm.mappings.collectionreference.reuse;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.persistence.testing.oxm.mappings.XMLWithJSONMappingTestCases;

public class CollectionReferenceReuseTestCases extends XMLWithJSONMappingTestCases {

    public CollectionReferenceReuseTestCases(String name) throws Exception {
        super(name);
        setProject(new CollectionReferenceReuseProject());
        setControlDocument("org/eclipse/persistence/testing/oxm/mappings/collectionreference/reuse/Mappings.xml");
        setControlJSON("org/eclipse/persistence/testing/oxm/mappings/collectionreference/reuse/Mappings.json");

    }

    public Object getControlObject() {
        Employee emp = new Employee();

        emp.id = 3208;
        emp.name = "Dave Matthews";

        Address home = new Address();
        home.id = 1198;
        home.info = "727 Main Street, Anytown NY, 78228";
        home.type = "home";

        Address work = new Address();
        work.id = 1453;
        work.info = "30 Capital Ave Suite 100, Big Town NY, 78441";
        work.type = "work";

        Address other = new Address();
        other.id = 1676;
        other.info = "Site 17 RR#4, Boonies NY, 78112";
        other.type = "other";

        List<Address> addresses = new LinkedList<Address>();
        addresses.add(home);
        addresses.add(work);
        addresses.add(other);
        emp.addresses = addresses;

        Root root = new Root();
        root.employee = emp;
        root.addresses = addresses;

        return root;
    }

    public void testContainerReused() throws Exception {
        URL url = ClassLoader.getSystemResource(resourceName);
        Root testObject = (Root) xmlUnmarshaller.unmarshal(url);

        assertEquals("This mapping's container was not reused.", LinkedList.class, testObject.employee.addresses.getClass());
    }

}
