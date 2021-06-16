/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.5.2 - initial implementation
package org.eclipse.persistence.testing.jaxb.json.characters;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;

import junit.framework.TestCase;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBContextProperties;

public class UTF8TestCases extends TestCase {

    private final static String CONTROL = "{\"escapeCharacterHolder\":{\"stringValue\":\"MOXy\u2019s\",\"characters\":[]}}";

    private JAXBContext jaxbContext;

    public UTF8TestCases(String name) throws Exception {
        super(name);
    }

    @Override
    public void setUp() throws Exception {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextProperties.MEDIA_TYPE, "application/json");
        jaxbContext = JAXBContextFactory.createContext(new Class[] {EscapeCharacterHolder.class}, properties);
    }

    protected EscapeCharacterHolder getControlObject() {
        EscapeCharacterHolder control = new EscapeCharacterHolder();
        control.stringValue = "MOXy\u2019s";
        return control;
    }

    public void testMarshalToOutputStream() throws Exception {
        Marshaller marshaller = jaxbContext.createMarshaller();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        marshaller.marshal(getControlObject(), outputStream);
        String test = new String(outputStream.toByteArray(), Charset.forName("UTF-8"));
        assertEquals(CONTROL, test);

    }

    public void testMarshalToWriter() throws Exception {
        Marshaller marshaller = jaxbContext.createMarshaller();
        StringWriter writer = new StringWriter();
        marshaller.marshal(getControlObject(), writer);
        String test = writer.toString();
        assertEquals(CONTROL, test);
    }

}
