/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     rbarkhouse - 2009-10-14 11:21:57 - initial implementation
package org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.converter;

import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class CompositeObjectSelfConverterTestCases extends XMLMappingTestCases {

    private final static String INSTANCE_XML = "org/eclipse/persistence/testing/oxm/mappings/compositeobject/self/converter/MyXML.xml";

    public CompositeObjectSelfConverterTestCases(String name) throws Exception {
        super(name);
        setControlDocument(INSTANCE_XML);
        setProject(new MyProject());
    }

    protected Object getControlObject() {
        MyObject myObj = new MyObject();

        String[] value = new String[] { "ABC", "123", "456" };

        myObj.setValue(value);

        return myObj;
    }

}
