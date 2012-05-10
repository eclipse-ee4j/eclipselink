/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.3.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.namespaceuri.twopackages;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.eclipse.persistence.testing.jaxb.namespaceuri.twopackages.a.A;
import org.eclipse.persistence.testing.jaxb.namespaceuri.twopackages.b.B;

public class ABTestCases extends JAXBWithJSONTestCases{
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/namespaceuri/twopackages/ab.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/namespaceuri/twopackages/ab.json";

    public ABTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {A.class, B.class});
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }

    @Override
    protected A getControlObject() {
        A a = new A();
        B b = new B();
        b.setC("C");
        a.setB(b);
        return a;
    }

}