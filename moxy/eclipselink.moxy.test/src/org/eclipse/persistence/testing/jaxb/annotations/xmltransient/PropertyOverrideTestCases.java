/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  -Matt MacIvor - Initial Implementation - 2.4.1
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmltransient;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class PropertyOverrideTestCases extends JAXBWithJSONTestCases{

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmltransient/childclass.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmltransient/childclass.json";

    public PropertyOverrideTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {ChildClass.class});
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }

    @Override
    protected Object getControlObject() {
        ChildClass root = new ChildClass();
        root.setFirstName("Jane");
        root.setLastName("Doe");
        return root;
    }

}