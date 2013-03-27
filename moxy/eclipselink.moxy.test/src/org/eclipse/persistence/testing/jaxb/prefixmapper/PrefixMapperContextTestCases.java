/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Matt MacIvor - 2.4.1
 ******************************************************************************/

package org.eclipse.persistence.testing.jaxb.prefixmapper;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.oxm.XMLConstants;

import junit.framework.TestCase;

public class PrefixMapperContextTestCases extends TestCase {
    private static final String CONTROL_XML = "<newPrefix:employeeContext xmlns:ns0=\"extraUri\" xmlns:somePrefix=\"my.uri\" xmlns:newPrefix=\"someuri\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + 
              "<firstName>Jon</firstName><lastName>Doe</lastName><acmeNS:employeeId>123</acmeNS:employeeId>" + 
              "</newPrefix:employeeContext>";
    
    public void testMarshalWithContextualNamespaces() throws Exception  {
        JAXBContext ctx = JAXBContextFactory.createContext(new Class[]{EmployeeContext.class}, null);
        Marshaller m = ctx.createMarshaller();
        m.setProperty(MarshallerProperties.NAMESPACE_PREFIX_MAPPER, new ContextPrefixMapper());
        m.setProperty(XMLConstants.JAXB_FRAGMENT, new Boolean(true));
        EmployeeContext emp = new EmployeeContext();
        emp.firstName = "Jon";
        emp.lastName = "Doe";
        emp.employeeId = 123;
        StringWriter writer = new StringWriter();
        m.marshal(emp, writer);
        
        assertTrue("Expected: " + CONTROL_XML + " But was: " + writer.toString(), writer.toString().equals(CONTROL_XML));
        
    }

}
