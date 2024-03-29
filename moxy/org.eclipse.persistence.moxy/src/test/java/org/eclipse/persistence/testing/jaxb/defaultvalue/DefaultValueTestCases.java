/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith - 2.3.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.defaultvalue;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class DefaultValueTestCases extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/defaultvalue/emptyObject.xml";
    private final static String XML_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/defaultvalue/defaultWrite.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/defaultvalue/emptyObject.json";
    private final static String JSON_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/defaultvalue/defaultWrite.json";

    public DefaultValueTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setWriteControlDocument(XML_WRITE_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setWriteControlJSON(JSON_WRITE_RESOURCE);
        Class<?>[] classes = new Class<?>[1];
        classes[0] = TestObject.class;
        setClasses(classes);
    }

    @Override
    protected Object getControlObject() {
        TestObject testObject = new TestObject();
        return testObject;
    }

}
