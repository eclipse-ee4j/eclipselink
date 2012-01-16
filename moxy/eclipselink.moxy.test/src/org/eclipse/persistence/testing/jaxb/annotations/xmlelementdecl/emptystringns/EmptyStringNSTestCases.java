/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - January 2012
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmlelementdecl.emptystringns;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class EmptyStringNSTestCases extends JAXBTestCases {

    private static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlelementdecl/emptystringns.xml";

    public EmptyStringNSTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setClasses(new Class[] {TestObject.class, ObjectFactory.class});
    }

    @Override
    protected Object getControlObject() {
        ObjectFactory objectFactory = new ObjectFactory();
        TestObject testObject = objectFactory.createTestObject();    
        JAXBElement elem = new JAXBElement<TestObject>(new QName("testObject"), TestObject.class, testObject);
        
        return elem;
    }


}
