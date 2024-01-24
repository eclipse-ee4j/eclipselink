/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith - initial API and implementation
package org.eclipse.persistence.testing.jaxb.xmlvalue;

import java.io.InputStream;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlValueByteArrayTestCases extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlvalue/bytesholder.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlvalue/bytesholder.json";
    private final static String JSON_SCHEMA_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlvalue/bytesholderschema.json";

    public XmlValueByteArrayTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class<?>[] classes = new Class<?>[1];
        classes[0] = BytesHolder.class;
        setClasses(classes);
    }

    @Override
    protected Object getControlObject() {
        BytesHolder holder = new BytesHolder();
        byte[] bytes = "ASTRING".getBytes();
        holder.theBytes = bytes;

        return holder;
    }

    public void testJSONSchemaGen() throws Exception{
        InputStream controlSchema = classLoader.getResourceAsStream(JSON_SCHEMA_RESOURCE);
        super.generateJSONSchema(controlSchema);
    }

}
