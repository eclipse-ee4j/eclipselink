/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Denise Smith -February 2010 - 2.1
package org.eclipse.persistence.testing.oxm.mappings.binarydata;

import org.eclipse.persistence.testing.oxm.mappings.XMLWithJSONMappingTestCases;

public class BinaryDataInlineTestCases extends XMLWithJSONMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/binarydata/BinaryDataInline.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/binarydata/BinaryDataInline.json";

    public BinaryDataInlineTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setProject(new BinaryDataInlineProject());
    }

    protected Object getControlObject() {
        Employee emp = new Employee(123);
        emp.setPhoto(new byte[]{0,1,2,3});
        emp.setExtraPhoto(new byte[]{0,1,2,3});
        return emp;
    }

}

