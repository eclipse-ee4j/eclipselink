/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Matt MacIvor - October 2011 - 2.4
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.jaxbelement.subclass;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class JAXBElementSubclassTestCases extends JAXBWithJSONTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/jaxbelement/subclass/root.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/jaxbelement/subclass/root.json";

    public JAXBElementSubclassTestCases(String name) throws Exception {
        super(name);
        this.setClasses(new Class[] {SubClass.class, ObjectFactory.class});
        this.setControlDocument(XML_RESOURCE);
        this.setControlJSON(JSON_RESOURCE);
    }

    @Override
    protected Object getControlObject() {
        return new SubClass();
    }
}
