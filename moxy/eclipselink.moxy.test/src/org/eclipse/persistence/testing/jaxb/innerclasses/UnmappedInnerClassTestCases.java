/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Blaise Doughan - 2.3.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.innerclasses;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class UnmappedInnerClassTestCases extends JAXBWithJSONTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/innerclasses/unmappedinner.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/innerclasses/unmappedinner.json";

    public UnmappedInnerClassTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {UnmappedInnerClassRoot.class});
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }

    @Override
    protected UnmappedInnerClassRoot getControlObject() {
        UnmappedInnerClassRoot root = new UnmappedInnerClassRoot();
        root.name = "FOO";
        return root;
    }

}
