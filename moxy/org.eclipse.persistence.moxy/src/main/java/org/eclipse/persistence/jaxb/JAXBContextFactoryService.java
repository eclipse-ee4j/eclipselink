/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.jaxb;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import java.util.Map;

/**
 * <p>
 * <b>Purpose:</b>An EclipseLink specific JAXBContextFactory Java service implementation. This is wrapper class for {@link org.eclipse.persistence.jaxb.JAXBContextFactory}.
 * <p>
 * <b>Responsibilities:</b>
 * <ul>
 * <li>Create a JAXBContext from an array of Classes and a Properties object</li>
 * <li>Create a JAXBContext from a context path, classloader and a Properties object</li>
 * </ul>
 * <p>
 * This class is the Java service entry point into in EclipseLink's JAXB 2.1 Runtime.
 * It provides the required factory methods.
 *
 * @see jakarta.xml.bind.JAXBContextFactory
 * @see jakarta.xml.bind.JAXBContext
 */
public class JAXBContextFactoryService implements jakarta.xml.bind.JAXBContextFactory {

    /**
     * Create a JAXBContext on the array of Class objects.  The JAXBContext will
     * also be aware of classes reachable from the classes in the array.
     *
     * @param classesToBeBound
     *      List of java classes to be recognized by the new {@link jakarta.xml.bind.JAXBContext}.
     *      Classes in {@code classesToBeBound} that are in named modules must be in a package
     *      that is {@code open} to at least the {@code java.xml.bind} module.
     *      Can be empty, in which case a {@link JAXBContext} that only knows about
     *      spec-defined classes will be returned.
     * @param properties
     *      provider-specific properties. Can be null, which means the same thing as passing
     *      in an empty map.
     *
     * @return
     *      A new instance of a {@code JAXBContext}.
     *
     * @throws JAXBException
     *      if an error was encountered while creating the
     *      {@code JAXBContext}, such as (but not limited to):
     */
    public jakarta.xml.bind.JAXBContext createContext(Class[] classesToBeBound, Map properties) throws JAXBException {
        return JAXBContextFactory.createContext(classesToBeBound, properties);
    }

    /**
     * Create a JAXBContext on context path.  The JAXBContext will
     * also be aware of classes reachable from the classes on the context path.
     *
     * @param contextPath
     *      List of java package names that contain schema derived classes.
     *      Classes in {@code classesToBeBound} that are in named modules must be in a package
     *      that is {@code open} to at least the {@code java.xml.bind} module.
     * @param classLoader
     *      This class loader will be used to locate the implementation classes.
     * @param properties
     *      provider-specific properties. Can be null, which means the same thing as passing
     *      in an empty map.
     *
     * @return a new instance of a {@code JAXBContext}
     * 
     * @throws JAXBException if an error was encountered while creating the
     *                       {@code JAXBContext} such as
     */
    public jakarta.xml.bind.JAXBContext createContext(String contextPath, ClassLoader classLoader, Map properties) throws JAXBException {
        return JAXBContextFactory.createContext(contextPath, classLoader, properties);
    }
}
