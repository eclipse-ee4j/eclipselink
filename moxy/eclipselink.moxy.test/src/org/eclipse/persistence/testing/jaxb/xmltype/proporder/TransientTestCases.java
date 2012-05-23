/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmltype.proporder;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class TransientTestCases extends JAXBWithJSONTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmltype/proporder/input.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmltype/proporder/input.json";
    
    public TransientTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {ChildOfTransientRoot.class});
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }

    @Override
    protected ChildOfTransientRoot getControlObject() {
        ChildOfTransientRoot c = new ChildOfTransientRoot();
        c.setChildProp("CHILD");
        c.setParentProp("PARENT");
        return c;
    }

}
