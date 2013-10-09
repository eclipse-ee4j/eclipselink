/*******************************************************************************
 * Copyright (c) 2012, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.5.2 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.json.characters;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

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
