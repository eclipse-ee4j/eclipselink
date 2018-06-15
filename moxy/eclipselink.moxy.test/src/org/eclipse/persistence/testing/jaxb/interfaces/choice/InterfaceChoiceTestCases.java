/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Denise Smith - February 25, 2013
package org.eclipse.persistence.testing.jaxb.interfaces.choice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class InterfaceChoiceTestCases extends JAXBWithJSONTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/interfaces/choice.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/interfaces/choice.json";

    public InterfaceChoiceTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[]{Root.class});
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        initXsiType();
    }

    @Override
    protected Map<String, String> getAdditationalNamespaces() {
        Map<String, String> namespaces = new HashMap<>();
        namespaces.put("someNamespace", "ns0");
        return namespaces;
    }

    @Override
    protected Object getControlObject() {
        Root root = new Root();

        ArrayList<MyInterface> things = new ArrayList<MyInterface>();

        MyObject mo = new MyObject();
        Properties p = new Properties();
        p.put("formatted", true);
        mo.setProperties(p);

        MyOtherObject ro = new MyOtherObject();
        Properties rp = new Properties();
        rp.put("formatted", false);
        ro.setProperties(rp);

        things.add(mo);
        things.add(ro);

        root.setMyList(things);
        return root;
    }

}
