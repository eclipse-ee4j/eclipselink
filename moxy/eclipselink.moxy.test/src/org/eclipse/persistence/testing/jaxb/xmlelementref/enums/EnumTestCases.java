/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Blaise Doughan - 2.3.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmlelementref.enums;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class EnumTestCases extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelementref/enum.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelementref/enum.json";

    public EnumTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {Root.class, Registry.class});
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }

    @Override
    protected Root getControlObject() {
        Root root = new Root();
        Registry registry = new Registry();
        root.setStringOrEnumSingle(registry.createEnum(MyEnum.BAR));
        root.getStringOrEnum().add(registry.createString("A"));
        root.getStringOrEnum().add(registry.createEnum(MyEnum.FOO));
        root.getStringOrEnum().add(registry.createString("B"));
        root.getStringOrEnum().add(registry.createEnum(MyEnum.BAR));
        return root;
    }

    @Override
    protected Object getJSONReadControlObject() {
        Root root = new Root();
        Registry registry = new Registry();
        root.setStringOrEnumSingle(registry.createEnum(MyEnum.BAR));
        root.getStringOrEnum().add(registry.createString("A"));
        root.getStringOrEnum().add(registry.createString("B"));
        root.getStringOrEnum().add(registry.createEnum(MyEnum.FOO));
        root.getStringOrEnum().add(registry.createEnum(MyEnum.BAR));
        return root;
    }

    public void testSchemaGen() throws Exception {
        List<InputStream> controlSchemas = new ArrayList<InputStream>();
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("org/eclipse/persistence/testing/jaxb/xmlelementref/enum.xsd");
        controlSchemas.add(is);
        super.testSchemaGen(controlSchemas);
    }
}
