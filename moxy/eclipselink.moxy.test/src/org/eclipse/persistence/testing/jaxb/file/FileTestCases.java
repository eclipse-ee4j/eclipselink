/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
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
