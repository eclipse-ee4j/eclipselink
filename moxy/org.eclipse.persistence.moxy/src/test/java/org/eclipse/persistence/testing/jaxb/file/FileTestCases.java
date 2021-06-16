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
//     mkos - December 20/2013 - 1.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.file;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

import java.io.File;

public class FileTestCases extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/file/file.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/file/file.json";

    public FileTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[] {Foo.class};
        setClasses(classes);
    }


    @Override
    protected Object getControlObject() {
        Foo root = new Foo();
        root.file = new File("foo.tmp");
        return root;
    }



}
