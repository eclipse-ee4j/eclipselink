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
 *      tware - initial 
 ******************************************************************************/
package org.eclipse.persistence.jpars.test.bootstrap;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;


import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.jpa.rs.PersistenceContext;
import org.eclipse.persistence.jpa.rs.PersistenceFactory;
import org.eclipse.persistence.jpars.test.util.ExamplePropertiesLoader;
import org.eclipse.persistence.jpars.test.util.XMLFilePathBuilder;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.junit.Test;

/**
 * Test bootstrapping of a dynamically created persistence context
 * @author tware
 *
 */
public class TestBootstrap {

	@Test
	public void testBootstrap() {
		Map<String, Object> properties = new HashMap<String, Object>();
		ExamplePropertiesLoader.loadProperties(properties);	
		PersistenceFactory factory = null;
		try{
		    factory = new PersistenceFactory();
		    FileInputStream xmlStream = new FileInputStream(XMLFilePathBuilder.getXMLFileName("auction-persistence.xml"));
			factory.bootstrapPersistenceContext("auction", xmlStream, properties, true);
		} catch (Exception e){
		    e.printStackTrace();
			fail(e.toString());
		}
		
		PersistenceContext context = factory.getDynamicPersistenceContext("auction");
		
		assertTrue(context.getEmf() != null);
		assertTrue(context.getJAXBContext() != null);
		
		Session session = (ServerSession)JpaHelper.getServerSession(context.getEmf());
		assertTrue("JPA Session did not contain Auction.", session.getProject().getAliasDescriptors().containsKey("Auction"));
        assertTrue("JPA Session did not contain Bid.", session.getProject().getAliasDescriptors().containsKey("Bid"));
        assertTrue("JPA Session did not contain User.", session.getProject().getAliasDescriptors().containsKey("User"));
        
        session = (DatabaseSession)((JAXBContext)context.getJAXBContext()).getXMLContext().getSession(0);
        assertTrue("JAXB Session did not contain Auction.", session.getProject().getAliasDescriptors().containsKey("Auction"));
        assertTrue("JAXB Session did not contain Bid.", session.getProject().getAliasDescriptors().containsKey("Bid"));
        assertTrue("JAXB Session did not contain User.", session.getProject().getAliasDescriptors().containsKey("User"));
	
	}

}
