/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Radek Felcman - 2.7.4 - initial implementation
package org.eclipse.persistence.testing.jaxb.json.type;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.testing.jaxb.json.type.modelns.*;
import org.junit.Test;

import javax.xml.bind.*;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.*;

/**
 * Tests to marshall, unmarshal JSON with JSON_TYPE_ATTRIBUTE_NAME marshall and unmarshall property.
 * Package with entity classes contains package-info.java file with namespaces declaration.
 *
 * @author Radek Felcman
 */
public class TypePropertyCustomNameNamespaceTestCases extends junit.framework.TestCase {

    private static final ObjectFactory OF = new ObjectFactory();

    private static final JAXBElement<KooSuper> KOO_SUPER = OF.createKoo(new KooSuper());
    private static final JAXBElement<KooSuper> KOO_1 = OF.createKoo(new Koo1());
    private static final JAXBElement<Tel> TEL_KOO_SUPER = OF.createTel(new Tel(new KooSuper()));
    private static final JAXBElement<Tel> TEL_KOO_1 = OF.createTel(new Tel(new Koo1()));

    private static final String JSON_ROOT_KOO_SUPER_WITH = "{\"koo-Root\":{\"_type\":\"KS\"}}";
    private static final String JSON_ROOT_KOO_SUPER_WITHOUT = "{\"koo-Root\":{}}";
    private static final String JSON_ROOT_KOO_1 = "{\"koo-Root\":{\"_type\":\"K1\"}}";
    private static final String JSON_TEL_KOO_SUPER_WITH = "{\"tel-Root\":{\"koo\":{\"_type\":\"KS\"}}}";
    private static final String JSON_TEL_KOO_SUPER_WITHOUT = "{\"tel-Root\":{\"koo\":{}}}";
    private static final String JSON_TEL_KOO_1 = "{\"tel-Root\":{\"koo\":{\"_type\":\"K1\"}}}";


    public TypePropertyCustomNameNamespaceTestCases() {
        super(TypePropertyCustomNameNamespaceTestCases.class.getSimpleName());
    }

    @Test
    public void testMarshallJsonRootKooSuperWithout() throws Exception {
        assertEquals(JSON_ROOT_KOO_SUPER_WITHOUT, marshal(KOO_SUPER));
    }

    @Test
    public void testMarshallJsonRootKoo1() throws Exception {
        assertEquals(JSON_ROOT_KOO_1, marshal(KOO_1));
    }

    @Test
    public void testMarshallJsonTelKooSuperWithout() throws Exception {
        assertEquals(JSON_TEL_KOO_SUPER_WITHOUT, marshal(TEL_KOO_SUPER));
    }

    @Test
    public void testMarshallJsonTelKoo1() throws Exception {
        assertEquals(JSON_TEL_KOO_1, marshal(TEL_KOO_1));
    }

    @Test
    public void testUnmarshallJsonRootKooSuperWith() throws Exception {
        assertEquals(KooSuper.class.getName(), unmarshal(JSON_ROOT_KOO_SUPER_WITH).getValue().getClass().getName());
    }

    @Test
    public void testUnmarshallJsonRootKooSuperWithout() throws Exception {
        assertEquals(KooSuper.class.getName(), unmarshal(JSON_ROOT_KOO_SUPER_WITHOUT).getValue().getClass().getName());
    }

    @Test
    public void testUnmarshallJsonRootKoo1() throws Exception {
        assertEquals(Koo1.class.getName(), unmarshal(JSON_ROOT_KOO_1).getValue().getClass().getName());
    }

    @Test
    public void testUnmarshallJsonTelKooSuperWith() throws Exception {
        assertEquals(KooSuper.class.getName(), Tel.class.cast(unmarshal(JSON_TEL_KOO_SUPER_WITH).getValue()).getKoo().getClass().getName());
    }

    @Test
    public void testUnmarshallJsonTelKooSuperWithout() throws Exception {
        assertEquals(KooSuper.class.getName(), Tel.class.cast(unmarshal(JSON_TEL_KOO_SUPER_WITHOUT).getValue()).getKoo().getClass().getName());
    }

    @Test
    public void testUnmarshallJsonTelKoo1() throws Exception {
        assertEquals(Koo1.class.getName(), Tel.class.cast(unmarshal(JSON_TEL_KOO_1).getValue()).getKoo().getClass().getName());
    }

    private static String marshal(final JAXBElement<?> value) throws Exception {
        final StringWriter stringWriter = new StringWriter();
        final Class<?>[] types = new Class<?>[]{ObjectFactory.class, Tel.class, KooSuper.class, Koo1.class};
        final Map<Object, Object> conf = new LinkedHashMap<>();
        conf.put(JAXBContextProperties.MEDIA_TYPE, "application/json");
        final JAXBContext innerCtx = JAXBContextFactory.createContext(types, conf);
        final Marshaller marshaller = innerCtx.createMarshaller();
        marshaller.setProperty(JAXBContextProperties.JSON_TYPE_ATTRIBUTE_NAME, "_type");
        marshaller.marshal(value, stringWriter);
        return stringWriter.toString();
    }

    private static JAXBElement<?> unmarshal(final String value) throws Exception {
        final Class<?>[] types = new Class<?>[]{ObjectFactory.class, Tel.class, KooSuper.class, Koo1.class};
        final Map<Object, Object> conf = new LinkedHashMap<>();
        conf.put(JAXBContextProperties.MEDIA_TYPE, "application/json");
        final JAXBContext innerCtx = JAXBContextFactory.createContext(types, conf);
        final Unmarshaller unmarshaller = innerCtx.createUnmarshaller();
        unmarshaller.setProperty(JAXBContextProperties.JSON_TYPE_ATTRIBUTE_NAME, "_type");
        return (JAXBElement<?>) unmarshaller.unmarshal(new ByteArrayInputStream(value.getBytes()));
    }
}
