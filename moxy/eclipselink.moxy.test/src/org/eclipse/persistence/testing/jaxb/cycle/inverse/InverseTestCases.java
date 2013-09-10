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
package org.eclipse.persistence.testing.jaxb.cycle.inverse;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class InverseTestCases extends JAXBWithJSONTestCases{

    private static final String XML = "org/eclipse/persistence/testing/jaxb/cycle/inverse/input.xml";
    private static final String JSON = "org/eclipse/persistence/testing/jaxb/cycle/inverse/input.json";

    public InverseTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {Root.class});
        setControlDocument(XML);
        setControlJSON(JSON);
    }

    @Override
    protected Root getControlObject() {
        Foo foo = new Foo();
        
        Bar bar1 = new Bar();
        bar1.id = 1;
        foo.bar.add(bar1);
        bar1.foo = foo;
        
        Bar bar2 = new Bar();
        bar2.id = 2;
        foo.bar.add(bar2);
        bar2.foo = foo;
        

        Root root = new Root();
        root.foo = foo;
        return root;
    }

    @Override
    public boolean isUnmarshalTest() {
        return false;
    }

}
