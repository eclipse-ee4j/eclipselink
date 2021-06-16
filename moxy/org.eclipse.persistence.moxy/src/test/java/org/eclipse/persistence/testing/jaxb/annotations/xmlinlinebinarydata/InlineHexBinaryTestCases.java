/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Matt MacIvor - 2.5.1 - Initial Implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlinlinebinarydata;

import java.io.StringWriter;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;

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
