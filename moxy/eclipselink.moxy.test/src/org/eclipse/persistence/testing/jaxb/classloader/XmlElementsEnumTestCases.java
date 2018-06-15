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
//   Matt MacIvor - 2.4.2 - Inital Implementation
package org.eclipse.persistence.testing.jaxb.classloader;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

import javax.xml.bind.JAXBContext;

import junit.framework.TestCase;

import org.eclipse.persistence.jaxb.JAXBContextFactory;

public class XmlElementsEnumTestCases extends TestCase {

    public void testCreateContext() throws Exception {
        Class[] classes = new Class[1];

        URL[] urls = new URL[1];
        urls[0] = Thread.currentThread().getContextClassLoader().getResource("./org/eclipse/persistence/testing/jaxb/classloader/enum.jar");
        URLClassLoader classLoader = new URLClassLoader(urls);

        Class classAClass = classLoader.loadClass("org.eclipse.persistence.testing.jaxb.classloader.Root");

        classes[0] = classAClass;
        JAXBContext ctx = JAXBContextFactory.createContext(classes, null, classLoader);

    }
}
