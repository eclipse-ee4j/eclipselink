/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Blaise Doughan - 2.5.1 - initial implementation
package org.eclipse.persistence.testing.jaxb.typevariable;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class ExtendedList8ChildTestCases extends JAXBWithJSONTestCases {

    private static final String XML = "org/eclipse/persistence/testing/jaxb/typevariable/extendedList.xml";
    private static final String JSON = "org/eclipse/persistence/testing/jaxb/typevariable/extendedList.json";

    public ExtendedList8ChildTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML);
        setControlJSON(JSON);
        setClasses(new Class[] {ExtendedList8ChildRoot.class});
    }

    @Override
    protected ExtendedList8ChildRoot getControlObject() {
        ExtendedList8Child list = new ExtendedList8Child();
        list.add(new ExtendedList8Impl());
        list.add(new ExtendedList8Impl());
        ExtendedList8ChildRoot control = new ExtendedList8ChildRoot();
        control.foo = list;
        return control;
    }

    public void testRI() throws Exception{
        JAXBContext ctx = JAXBContext.newInstance(new Class[]{ExtendedList8ChildRoot.class});
        System.out.println(ctx.getClass());
        Marshaller m = ctx.createMarshaller();
        m.marshal(getControlObject(), System.out);
    }

}
