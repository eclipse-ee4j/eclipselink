/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  - rbarkhouse - -01 March 2012 - 2.4 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmladapter.inheritance;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class AdapterWithInheritanceTestCases extends JAXBTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmladapter/inheritance/root.xml";

    public AdapterWithInheritanceTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        Class[] classes = new Class[] {Root.class};
        setClasses(classes);
    }

    protected Object getControlObject() {
        Root root = new Root();
        return root;
    }

    public Object getReadControlObject() {
        Root root = new Root();
        root.foo = new Foo();
        return root;
    }

}