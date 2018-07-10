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
//     dclarke - Dynamic Persistence
//       http://wiki.eclipse.org/EclipseLink/Development/Dynamic
//       (https://bugs.eclipse.org/bugs/show_bug.cgi?id=200045)
//     mnorman - tweaks to work from Ant command-line,
//               get database properties from System, etc.
//
package org.eclipse.persistence.testing.tests.dynamic.projectxml;

//javase imports
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

//JUnit4 imports
import org.junit.Test;

//EclipseLink imports
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.dynamic.DynamicTypeBuilder;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.sessions.DatabaseLogin;

/**
 * Test cases verifying invalid args passed to
 * {@link DynamicTypeBuilder#loadDynamicProject(String, DatabaseLogin)}
 */
public class InvalidConfig {

    public static final String PACKAGE_PREFIX =
        InvalidConfig.class.getPackage().getName();
    static final String INVALID_PROJECT_XML =
        PACKAGE_PREFIX.replace('.', '/') + "/bar.xml";

    @Test(expected=NullPointerException.class)
    public void nullResource() throws Exception {
        DynamicTypeBuilder.loadDynamicProject((String)null, null, null);
    }

    @Test(expected=NullPointerException.class)
    public void nullClassLoader() throws Exception {
        File temp = File.createTempFile("foo",".txt");
        temp.deleteOnExit();
        FileInputStream fis = new FileInputStream(temp);
        DynamicTypeBuilder.loadDynamicProject(fis, new DatabaseLogin(), null);
    }

    @Test(expected=XMLMarshalException.class)
    public void invalidResource() throws Exception {
        DynamicClassLoader dynamicClassLoader = new DynamicClassLoader(
            InvalidConfig.class.getClassLoader());
        InputStream is = dynamicClassLoader.getResourceAsStream(INVALID_PROJECT_XML);
        DynamicTypeBuilder.loadDynamicProject(is, new DatabaseLogin(),
            dynamicClassLoader);
    }
}
