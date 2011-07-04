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
 * Matt MacIvor - July 4th 2011
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmladapter.choice;

import java.util.ArrayList;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class AdapterWithElementsTestCases extends JAXBTestCases {
    public AdapterWithElementsTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        Class[] classes = new Class[] {Foo.class, BarA.class, BarB.class, BarC.class};
        setClasses(classes);
    }

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmladapter/choice.xml";

    protected Object getControlObject() {
        // TODO Auto-generated method stub
        Foo foo = new Foo();
        foo.singleChoice = new BarA();
        
        foo.collectionChoice = new ArrayList<Object>();
        
        foo.collectionChoice.add(new BarB());
        foo.collectionChoice.add("test string");
        BarC barC = new BarC();
        barC.a = "a";
        barC.b = "b";
        foo.collectionChoice.add(barC);
        foo.collectionChoice.add(new Integer(123));
        
        return foo;
    }
    
}
