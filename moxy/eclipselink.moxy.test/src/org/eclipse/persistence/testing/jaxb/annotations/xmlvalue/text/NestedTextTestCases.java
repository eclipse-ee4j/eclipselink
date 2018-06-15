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
//     Blaise Doughan - 2.3.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlvalue.text;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.persistence.jaxb.json.JsonSchemaOutputResolver;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class NestedTextTestCases extends JAXBWithJSONTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlvalue/nestedText.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlvalue/nestedText.json";
    private static final String JSON_SCHEMA_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlvalue/nestedTextSchema.json";

    public NestedTextTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {Top.class});
        setControlDocument(XML_RESOURCE);
         setControlJSON(JSON_RESOURCE);
    }

    @Override
    protected Top getControlObject() {
        Bottom bottom = new Bottom();
        bottom.bottomAttr = "C";
        bottom.value = "ABC";

        Middle2 middle2 = new Middle2();
        middle2.middle2Attr = "B";
        middle2.bottom = bottom;
        bottom.middle2 = middle2;

        Middle1 middle1 = new Middle1();
        middle1.middle1Attr = "A";
        middle1.middle2 = middle2;
        middle2.middle1 = middle1;

        Top top = new Top();
        top.middle1 = middle1;
        middle1.top = top;
        return top;
    }
    public void testJSONSchemaGen() throws Exception{
        InputStream controlSchema = classLoader.getResourceAsStream(JSON_SCHEMA_RESOURCE);
        super.generateJSONSchema(controlSchema);
    }


}
