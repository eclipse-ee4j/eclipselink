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
 *     Blaise Doughan - 2.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmltransient.splitpackage;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;

import junit.framework.TestCase;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmltransient.splitpackage.b.Foo;

public class SplitPackageTestCases extends TestCase{

    private static String BINDING_FILE_A = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmltransient/splitpackage/a/binding-a.xml"; 

    public SplitPackageTestCases(String name) throws Exception{
        super(name);
    }
    
    public void testJAXBContextCreation() throws Exception {
        InputStream bindingFileA = SplitPackageTestCases.class.getClassLoader().getResourceAsStream(BINDING_FILE_A);
        Map<String, Object> properties = new HashMap<String, Object>(1);
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, bindingFileA);
        JAXBContext jaxbContext = JAXBContextFactory.createContext(new Class[] {Foo.class}, properties);
    }

}