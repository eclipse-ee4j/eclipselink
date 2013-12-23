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

public class ExtendedList10TestCases extends JAXBWithJSONTestCases {

    private static final String XML = "org/eclipse/persistence/testing/jaxb/typevariable/extendedList.xml";
    private static final String JSON = "org/eclipse/persistence/testing/jaxb/typevariable/extendedList.json";
    
    public ExtendedList10TestCases(String name) throws Exception {
    	super(name);
    	setControlDocument(XML);
    	setControlJSON(JSON);
        setClasses(new Class[] {ExtendedList10Root.class});
    }

    @Override
    protected ExtendedList10Root getControlObject() {
    	ExtendedList10<Integer, ExtendedList10Root, Float> list = new ExtendedList10<Integer, ExtendedList10Root, Float>();
    	list.add(new ExtendedList10Root());
    	list.add(new ExtendedList10Root());
    	ExtendedList10Root control = new ExtendedList10Root();
    	control.foo = list;
    	return control;
    }

}
