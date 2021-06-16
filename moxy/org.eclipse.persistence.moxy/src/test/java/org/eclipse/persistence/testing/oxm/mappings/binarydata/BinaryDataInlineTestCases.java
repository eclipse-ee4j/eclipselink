/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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

