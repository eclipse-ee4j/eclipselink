/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Matt MacIvor - 2.5.1 - Initial Implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlinlinebinarydata;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class InlineHexBinaryTestCases extends JAXBWithJSONTestCases {

    private static final String XML_RESOURCE="org/eclipse/persistence/testing/jaxb/annotations/xmlinlinebinary/hexbinary.xml";
    private static final String JSON_RESOURCE="org/eclipse/persistence/testing/jaxb/annotations/xmlinlinebinary/hexbinary.json";

    public InlineHexBinaryTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[]{HexRoot.class});
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected Object getControlObject() {
        HexRoot r = new HexRoot();
        r.bytes = "String to be encoded as hex".getBytes();
        return r;

    }

}
