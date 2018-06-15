/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Iaroslav Savytskyi - 2.6 - initial implementation
package org.eclipse.persistence.testing.jaxb.json.unmapped;

import junit.framework.TestCase;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBContextProperties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.Map;

/**
 * Created by yaroska on 02/03/15.
 */
public class JsonUnmappedTestCases extends TestCase {

    public void testLastNull() throws JAXBException, FileNotFoundException {
        Map<String, String> jaxbProperties = Collections.singletonMap(JAXBContextProperties.MEDIA_TYPE, "application/json");
        JAXBContext jc = JAXBContextFactory.createContext(new Class[]{Foo.class}, jaxbProperties);
        Unmarshaller um = jc.createUnmarshaller();

        String file = "org/eclipse/persistence/testing/jaxb/json/unmapped/unmapped.json";
        StreamSource jsonSource = new StreamSource(Thread.currentThread().getContextClassLoader().getResourceAsStream(file));
        JAXBElement<Foo> o = um.unmarshal(jsonSource, Foo.class);

        final Foo foo = o.getValue();
        assertNotNull("Unmarshalled element shouldn't be null", foo);
        String expected = "fooName";
        final String actual = foo.getName();
        assertEquals("Expected name is : " + expected + "; actual is : " + actual, expected, actual);
    }
}
