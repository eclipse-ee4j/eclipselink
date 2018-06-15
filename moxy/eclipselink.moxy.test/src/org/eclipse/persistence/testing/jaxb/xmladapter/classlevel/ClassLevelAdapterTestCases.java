/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// Denise Smith - September 10 /2009
package org.eclipse.persistence.testing.jaxb.xmladapter.classlevel;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class ClassLevelAdapterTestCases extends JAXBWithJSONTestCases{
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmladapter/classleveltest.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmladapter/classleveltest.json";

    public ClassLevelAdapterTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);

        Class[] classes = new Class[2];
        classes[0] = TestObject.class;
        classes[1] = ClassA.class;
        setClasses(classes);
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

        ClassBSubClass subClassTest = new ClassBSubClass();
        subClassTest.setSomeValue("subclassValue");
        subClassTest.setExtraString("extra");
        tester.setSubClassObject(subClassTest);

        ClassB classB3 = new ClassB();
        classB3.setSomeValue("differentValue");
        tester.anotherClassBObject = classB3;

        return tester;
    }
}
