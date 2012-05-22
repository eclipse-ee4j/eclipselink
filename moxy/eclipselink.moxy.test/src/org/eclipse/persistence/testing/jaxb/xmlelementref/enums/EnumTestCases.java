/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.3.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlelementref.enums;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class EnumTestCases extends JAXBTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelementref/enum.xml";

    public EnumTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {Root.class, Registry.class});
        setControlDocument(XML_RESOURCE);
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

}