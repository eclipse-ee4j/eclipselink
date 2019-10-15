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
//     Denise Smith - 2.3.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.defaultvalue;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class DefaultValueXmlValueEnumTestCases extends JAXBTestCases{
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/defaultvalue/emptyObject.xml";
    private final static String XML_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/defaultvalue/xmlValueEnumWrite.xml";

    public DefaultValueXmlValueEnumTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setWriteControlDocument(XML_WRITE_RESOURCE);
        Class[] classes = new Class[1];
        classes[0] = TestXmlValueEnumObject.class;
        setClasses(classes);
    }

    @Override
    protected Object getControlObject() {
        TestXmlValueEnumObject testObject = new TestXmlValueEnumObject();
        return testObject;
    }

}
