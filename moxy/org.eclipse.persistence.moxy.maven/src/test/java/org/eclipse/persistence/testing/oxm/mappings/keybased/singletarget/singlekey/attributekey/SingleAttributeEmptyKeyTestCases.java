/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.mappings.keybased.singletarget.singlekey.attributekey;

import java.io.InputStream;
import org.eclipse.persistence.testing.oxm.mappings.keybased.KeyBasedMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.keybased.Root;
import org.eclipse.persistence.testing.oxm.mappings.keybased.singletarget.Employee;
import org.w3c.dom.Document;

public class SingleAttributeEmptyKeyTestCases extends KeyBasedMappingTestCases {
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/keybased/singletarget/singlekey/elementkey/instance-emptyvalue.xml";
    private static final String XML_WRITE_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/keybased/singletarget/singlekey/elementkey/writecontrolinstance-emptyvalue.xml";

    public SingleAttributeEmptyKeyTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setProject(new SingleAttributeKeyProject());
    }

    protected Object getControlObject() {
        Employee employee = new Employee();
        employee.id = "222";
        employee.name = "Joe Smith";
        Root root = new Root();
        root.employee = employee;
        return root;
    }

    protected Document getWriteControlDocument() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(XML_WRITE_RESOURCE);
        Document doc = parser.parse(inputStream);
        removeEmptyTextNodes(doc);
        inputStream.close();
        return doc;
    }
}
