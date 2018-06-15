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
//      dclarke - JPA-RS Incubator (Bug 362900)
package org.eclipse.persistence.jpars.test.internal;

import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.xml.bind.JAXBContext;

import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.jpa.rs.PersistenceContext;
import org.eclipse.persistence.jpa.rs.PersistenceFactoryBase;
import org.eclipse.persistence.jpars.test.util.ExamplePropertiesLoader;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.junit.BeforeClass;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

/**
 * Tests validating the
 *
 * @author dclarke
 * @since EclipseLink 2.4.0
 */
public class AuctionPersistenceContextTests {

    private static PersistenceContext context;

    @Test
    public void verifyJPAConfig() {
        assertNotNull(context);
        EntityManagerFactory emf = context.getEmf();
        assertNotNull(emf);

        DatabaseSession session = JpaHelper.getServerSession(emf);
        assertEquals(3, session.getDescriptors().size());
    }

    @Test
    public void verifyJaxbContext() {
        assertNotNull(context);
        JAXBContext jaxbContext = context.getJAXBContext();
        assertNotNull(jaxbContext);

        org.eclipse.persistence.jaxb.JAXBContext contextImpl = (org.eclipse.persistence.jaxb.JAXBContext) jaxbContext;

        assertEquals(4, contextImpl.getXMLContext().getSession(0).getDescriptors().size());
    }

    @BeforeClass
    public static void createContext() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        ExamplePropertiesLoader.loadProperties(properties);
        PersistenceFactoryBase factory = new PersistenceFactoryBase();
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("xmldocs/auction-persistence.xml");
        PersistenceContext context = factory.get("auction", new URI("http://localhost:9090/JPA-RS/"), null, properties);
    }
}
