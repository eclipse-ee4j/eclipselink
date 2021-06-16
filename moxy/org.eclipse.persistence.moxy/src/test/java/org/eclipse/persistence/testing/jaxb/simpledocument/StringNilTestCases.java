/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
// mmacivor - April 8th/2010 - 2.1 - Initial implementation
package org.eclipse.persistence.testing.jaxb.simpledocument;

import java.util.HashMap;
import java.util.Map;

import jakarta.xml.bind.JAXBElement;

import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class StringNilTestCases extends JAXBWithJSONTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/simpledocument/string_nil.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/simpledocument/string_nil.json";

    public StringNilTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);

        Class[] classes = new Class[1];
        classes[0] = StringObjectFactory.class;
        setClasses(classes);
    }

    protected Object getControlObject() {
        JAXBElement value = new StringObjectFactory().createStringRoot();
        value.setValue(null);
        value.setNil(true);
        return value;
    }

    public Map getProperties(){
        Map props = new HashMap();

        Map namespaces = new HashMap();
        namespaces.put("myns","ns0");

        props.put(JAXBContextProperties.NAMESPACE_PREFIX_MAPPER, namespaces);

        return props;
}

}
