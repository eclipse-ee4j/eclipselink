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
//     Denise Smith - initial API and implementation
package org.eclipse.persistence.testing.jaxb.xmlvalue;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;

import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlValueByteArrayTestCases extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlvalue/bytesholder.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlvalue/bytesholder.json";
    private final static String JSON_SCHEMA_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlvalue/bytesholderschema.json";

    public XmlValueByteArrayTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[1];
        classes[0] = BytesHolder.class;
        setClasses(classes);
    }

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
