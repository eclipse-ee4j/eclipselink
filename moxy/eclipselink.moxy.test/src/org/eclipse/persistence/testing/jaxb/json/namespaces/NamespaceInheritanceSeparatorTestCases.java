/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith - 2.4
package org.eclipse.persistence.testing.jaxb.json.namespaces;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;

public class NamespaceInheritanceSeparatorTestCases extends NamespaceInheritanceTestCases{

    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/namespaces/employee_separator.json";
    private final static String JSON_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/namespaces/employee_different_separator.json";

    public NamespaceInheritanceSeparatorTestCases(String name) throws Exception {
        super(name);
        setControlJSON(JSON_RESOURCE);
        setWriteControlJSON(JSON_WRITE_RESOURCE);
        setClasses(new Class[]{Employee.class});
    }

    public void setUp() throws Exception{
        super.setUp();
        jsonMarshaller.setProperty(MarshallerProperties.JSON_NAMESPACE_SEPARATOR, '#');
        jsonUnmarshaller.setProperty(UnmarshallerProperties.JSON_NAMESPACE_SEPARATOR, '#');
    }
}
