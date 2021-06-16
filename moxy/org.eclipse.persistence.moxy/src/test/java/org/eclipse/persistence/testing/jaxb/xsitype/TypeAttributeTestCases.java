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
//     Denise Smith - August 2013
package org.eclipse.persistence.testing.jaxb.xsitype;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class TypeAttributeTestCases extends JAXBWithJSONTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xsitype/foo.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xsitype/foo.json";
    private final static String XML_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/xsitype/fooWrite.xml";
    private final static String JSON_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/xsitype/fooWrite.json";
    public TypeAttributeTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setWriteControlDocument(XML_WRITE_RESOURCE);
        setWriteControlJSON(JSON_WRITE_RESOURCE);
        Class[] classes = new Class[2];
        classes[0] = Root.class;
        classes[1] = Foo.class;
        setClasses(classes);
    }

    protected Object getControlObject() {
        return new Root();
    }
}
