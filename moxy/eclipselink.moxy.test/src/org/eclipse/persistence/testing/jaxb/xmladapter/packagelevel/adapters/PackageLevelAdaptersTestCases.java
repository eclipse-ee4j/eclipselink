/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
// Denise Smith - September 10 /2009
package org.eclipse.persistence.testing.jaxb.xmladapter.packagelevel.adapters;

import org.eclipse.persistence.internal.jaxb.JaxbClassLoader;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class PackageLevelAdaptersTestCases extends JAXBWithJSONTestCases{
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmladapter/packageleveltest.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmladapter/packageleveltest.json";

    public PackageLevelAdaptersTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);

        this.classLoader = new JaxbClassLoader(Thread.currentThread().getContextClassLoader());
           jaxbContext = JAXBContextFactory.createContext("org.eclipse.persistence.testing.jaxb.xmladapter.packagelevel.adapters", classLoader);
        xmlContext =((org.eclipse.persistence.jaxb.JAXBContext)jaxbContext).getXMLContext();
        setProject(xmlContext.getSession(0).getProject());
        jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbUnmarshaller = jaxbContext.createUnmarshaller();
    }

    protected Object getControlObject() {
        TestObject tester = new TestObject();
        ClassB classB = new ClassB();
        classB.setSomeValue("singleValue");
        tester.setClassBObject(classB);

        ClassB classB1 = new ClassB();
        classB1.setSomeValue("listValue1");
        tester.getClassBCollection().add(classB1);

        ClassB classB2 = new ClassB();
        classB2.setSomeValue("listValue2");
        tester.getClassBCollection().add(classB2);

        return tester;
    }
}
