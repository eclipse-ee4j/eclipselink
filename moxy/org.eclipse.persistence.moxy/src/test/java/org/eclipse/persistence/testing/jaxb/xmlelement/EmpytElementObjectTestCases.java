/*
 * Copyright (c) 2013, 2021 Oracle and/or its affiliates. All rights reserved.
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
