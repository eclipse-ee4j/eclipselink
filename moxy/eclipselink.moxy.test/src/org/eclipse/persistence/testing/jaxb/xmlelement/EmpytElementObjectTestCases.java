/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.4.2 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmlelement;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class EmpytElementObjectTestCases extends JAXBTestCases {

    private static String CONTROL_DOCUMENT_READ = "org/eclipse/persistence/testing/jaxb/xmlelement/empty_element_read.xml";
    private static String CONTROL_DOCUMENT_WRITE = "org/eclipse/persistence/testing/jaxb/xmlelement/empty_element_write.xml";

    public EmpytElementObjectTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {EmptyElementObjectRoot.class});
        setControlDocument(CONTROL_DOCUMENT_READ);
        setWriteControlDocument(CONTROL_DOCUMENT_WRITE);
    }

    @Override
    protected EmptyElementObjectRoot getControlObject() {
        EmptyElementObjectRoot root = new EmptyElementObjectRoot();
        root.xsiBoolean = Boolean.FALSE;
        root.xsiInteger = 0;
        root.xsiString = "";

        root.items.add(Boolean.FALSE);
        root.items.add(0);
        root.items.add("");

        return root;
    }

}
