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
//     Denise Smith - November 11, 2009
package org.eclipse.persistence.testing.jaxb.multiplepackage;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.eclipse.persistence.testing.jaxb.multiplepackage.packagea.ClassA;
import org.eclipse.persistence.testing.jaxb.multiplepackage.packagea.Root;
import org.eclipse.persistence.testing.jaxb.multiplepackage.packageb.ClassB;

public class MultiplePackageInfoTestCases extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/multiplepackage/root.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/multiplepackage/root.json";

    public MultiplePackageInfoTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[3];
        classes[0] = ClassA.class;
        classes[1] = ClassB.class;
        classes[2] = Root.class;

        jaxbContext = JAXBContextFactory.createContext(classes, null);
        xmlContext =((org.eclipse.persistence.jaxb.JAXBContext)jaxbContext).getXMLContext();
        setProject(xmlContext.getSession(0).getProject());
        jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbUnmarshaller = jaxbContext.createUnmarshaller();
    }

    protected Object getControlObject() {
        Root myRoot = new Root();

        ClassA classA = new ClassA();
        classA.id = 10;

        ClassB classB = new ClassB();
        classB.id = 20;

        myRoot.theClassA = classA;
        myRoot.theClassB = classB;
        return myRoot;
    }
}
