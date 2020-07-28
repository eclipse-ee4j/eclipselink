/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith - February 2012
package org.eclipse.persistence.testing.jaxb.innerclasses.notincontext;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class InnerClassNotInContextTestCases extends JAXBWithJSONTestCases{
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/innerclasses/notincontext.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/innerclasses/notincontext.json";

    public InnerClassNotInContextTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[1];
        classes[0] = TestObjectWrapper.class;
        setClasses(classes);
    }

    @Override
    protected Object getControlObject() {
        TestObjectWrapper tow = new TestObjectWrapper();
        TestObject testObject = new TestObject();
        testObject.testString = "testStringValue";
        tow.testObject = testObject;
        return tow;
    }


}
