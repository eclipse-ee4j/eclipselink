/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Denise Smith - September 10 /2009
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmladapter.classlevel;

import org.eclipse.persistence.internal.jaxb.JaxbClassLoader;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class ClassLevelAdapterTestCases extends JAXBTestCases{
	private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmladapter/classleveltest.xml";
    
    public ClassLevelAdapterTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);        

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