/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.5.1 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.typevariable;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class ExtendedList8TestCases extends JAXBWithJSONTestCases {

    private static final String XML = "org/eclipse/persistence/testing/jaxb/typevariable/extendedList.xml";
    private static final String JSON = "org/eclipse/persistence/testing/jaxb/typevariable/extendedList.json";
    
    public ExtendedList8TestCases(String name) throws Exception {
    	super(name);
    	setControlDocument(XML);
    	setControlJSON(JSON);
        setClasses(new Class[] {ExtendedList8Root.class});
    }

    @Override
    protected ExtendedList8Root getControlObject() {
    	ExtendedList8<ExtendedList8Root> list = new ExtendedList8<ExtendedList8Root>();
    	list.add(new ExtendedList8Root());
    	list.add(new ExtendedList8Root());
    	ExtendedList8Root control = new ExtendedList8Root();
    	control.foo = list;
    	return control;
    }

}
