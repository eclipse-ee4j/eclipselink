/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// Denise Smith - September 10 /2009
package org.eclipse.persistence.testing.jaxb.classloader;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

import org.eclipse.persistence.jaxb.JAXBContextFactory;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.eclipse.persistence.testing.jaxb.jaxbcontext.withjaxbindex.ClassA;

public class DifferentClassLoaderTestCases extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/classloader/classloader.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/classloader/classloader.json";

    private Class rootClass;

    public DifferentClassLoaderTestCases(String name) throws Exception {
        super(name);

        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);

        Class[] classes = new Class[2];

        URL[] urls = new URL[1];
        urls[0] = Thread.currentThread().getContextClassLoader().getResource("./org/eclipse/persistence/testing/jaxb/classloader/test.jar");
        URLClassLoader classLoaderA = new URLClassLoader(urls);


        Class classAClass = classLoaderA.loadClass("org.eclipse.persistence.testing.jaxb.classloader.ClassA");
        ClassLoader test = classAClass.getClassLoader();
        classes[1] = classAClass;
        rootClass = classAClass;

        classes[0] = ClassB.class;

        jaxbContext = JAXBContextFactory.createContext(classes, null);
        xmlContext = ((org.eclipse.persistence.jaxb.JAXBContext)jaxbContext).getXMLContext();
        setProject(xmlContext.getSession(0).getProject());

        jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbUnmarshaller = jaxbContext.createUnmarshaller();
    }

    protected Object getControlObject() {
        try{
                    Object classA = rootClass.newInstance();
            return classA;
        }catch(Exception e){

                    e.printStackTrace();
                    fail("An error occurred during getControlObject");
                    return null;
        }
    }
}
