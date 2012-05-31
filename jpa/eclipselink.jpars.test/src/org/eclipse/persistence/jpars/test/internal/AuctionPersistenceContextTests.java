/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      dclarke - JPA-RS Incubator (Bug 362900)  
 ******************************************************************************/
package org.eclipse.persistence.jpars.test.internal;

import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.xml.bind.JAXBContext;

import junit.framework.Assert;

import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.jpa.rs.PersistenceContext;
import org.eclipse.persistence.jpa.rs.PersistenceFactory;
import org.eclipse.persistence.jpars.test.util.ExamplePropertiesLoader;
import org.eclipse.persistence.sessions.server.Server;
import org.junit.BeforeClass;
import org.junit.Test;

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
        Assert.assertNotNull(context);
        EntityManagerFactory emf = context.getEmf();
        Assert.assertNotNull(emf);
        
        Server session = JpaHelper.getServerSession(emf);
        Assert.assertEquals(3, session.getDescriptors().size());
    }
    
    @Test
    public void verifyJaxbContext() {
        Assert.assertNotNull(context);
        JAXBContext jaxbContext = context.getJAXBContext();
        Assert.assertNotNull(jaxbContext);
        
        org.eclipse.persistence.jaxb.JAXBContext contextImpl = (org.eclipse.persistence.jaxb.JAXBContext) jaxbContext;
        
        Assert.assertEquals(4, contextImpl.getXMLContext().getSession(0).getDescriptors().size());
    }

    @BeforeClass
    public static void createContext() throws Exception {
        Map<String, Object> properties = new HashMap<String, Object>();
        ExamplePropertiesLoader.loadProperties(properties);
        PersistenceFactory factory = new PersistenceFactory();
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("xmldocs/auction-persistence.xml"); 
        PersistenceContext context = factory.get("auction", new URI("http://localhost:8080/JPA-RS/"), properties);
    }
}
