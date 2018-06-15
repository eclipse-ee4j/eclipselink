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
//     Matt MacIvor - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.listofobjects;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

import junit.framework.TestCase;

public class JAXBArrayOfInnerEnumTestCases extends JAXBWithJSONTestCases {
    public static String XML_RESOURCE="org/eclipse/persistence/testing/jaxb/listofobjects/innerenum.xml";
    public static String JSON_RESOURCE="org/eclipse/persistence/testing/jaxb/listofobjects/innerenum.json";

    public JAXBArrayOfInnerEnumTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[]{InnerEnumWrapper.class});
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }

    @Override
    protected Object getControlObject() {
        return new InnerEnumWrapper();
    }

}
