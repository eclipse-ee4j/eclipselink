/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *   Matt MacIvor - 2.4.2 - Inital Implementation
 ******************************************************************************/
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
        File f = new File("./org/eclipse/persistence/testing/jaxb/classloader/enum.jar");
        urls[0] = f.toURL();
        URLClassLoader classLoader = new URLClassLoader(urls);

        Class classAClass = classLoader.loadClass("org.eclipse.persistence.testing.jaxb.classloader.Root");
        
        classes[0] = classAClass;
        JAXBContext ctx = JAXBContextFactory.createContext(classes, null, classLoader);

    }
}
