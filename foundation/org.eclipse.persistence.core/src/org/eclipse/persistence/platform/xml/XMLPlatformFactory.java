/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 1998, 2018 IBM Corporation. All rights reserved.
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
//     08/29/2016 Jody Grassel
//       - 500441: Eclipselink core has System.getProperty() calls that are not potentially executed under doPriv()
package org.eclipse.persistence.platform.xml;

import java.security.AccessController;
import java.security.PrivilegedActionException;

import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedGetClassLoaderForClass;
import org.eclipse.persistence.internal.security.PrivilegedNewInstanceFromClass;

public class XMLPlatformFactory {
    public static final String XML_PLATFORM_PROPERTY = "eclipselink.xml.platform";
    public static final String XDK_PLATFORM_CLASS_NAME = "org.eclipse.persistence.platform.xml.xdk.XDKPlatform";
    public static final String JAXP_PLATFORM_CLASS_NAME = "org.eclipse.persistence.platform.xml.jaxp.JAXPPlatform";
    private static XMLPlatformFactory instance;
    private Class xmlPlatformClass;

    private XMLPlatformFactory() {
        super();
    }

    /**
     * INTERNAL:
     * Return the singleton instance of XMLPlatformContext.
     * @return the the singleton instance of XMLPlatformContext.
     * @throws XMLPlatformException
     */
    public static XMLPlatformFactory getInstance() throws XMLPlatformException {
        if (null == instance) {
            instance = new XMLPlatformFactory();
        }
        return instance;
    }

    /**
     * INTERNAL:
     * Return the implementation class for the XMLPlatform.
     * @return the implementation class for the XMLPlatform.
     * @throws XMLPlatformException
     */
    public Class getXMLPlatformClass() throws XMLPlatformException {
        if (null != xmlPlatformClass) {
            return xmlPlatformClass;
        }

        String newXMLPlatformClassName = PrivilegedAccessHelper.getSystemProperty(XML_PLATFORM_PROPERTY);
        if (null == newXMLPlatformClassName) {
            newXMLPlatformClassName = JAXP_PLATFORM_CLASS_NAME;
        }

        try {
            ClassLoader classLoader = null;
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
                try {
                    classLoader = AccessController.doPrivileged(new PrivilegedGetClassLoaderForClass(this.getClass()));
                } catch (PrivilegedActionException ex){
                    throw (RuntimeException) ex.getCause();
                }
            } else {
                classLoader = PrivilegedAccessHelper.getClassLoaderForClass(this.getClass());
            }
            // Loader may be null if the class was loaded by the root loader in some JVM's/configs.
            if (classLoader == null) {
                classLoader = Thread.currentThread().getContextClassLoader();
            }
            if (classLoader == null) {
                classLoader = ClassLoader.getSystemClassLoader();
            }
            Class newXMLPlatformClass = classLoader.loadClass(newXMLPlatformClassName);
            setXMLPlatformClass(newXMLPlatformClass);
            return xmlPlatformClass;
        } catch (ClassNotFoundException e) {
            throw XMLPlatformException.xmlPlatformClassNotFound(newXMLPlatformClassName, e);
        }
    }

    /**
     * PUBLIC:
     * Set the implementation of XMLPlatform.
     */
    public void setXMLPlatformClass(Class xmlPlatformClass) {
        this.xmlPlatformClass = xmlPlatformClass;
    }

    /**
     * INTERNAL:
     * Return the XMLPlatform based on the toplink.xml.platform System property.
     * @return an instance of XMLPlatform
     * @throws XMLPlatformException
     */
    public XMLPlatform getXMLPlatform() throws XMLPlatformException {
        try {
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                try{
                    return (XMLPlatform)AccessController.doPrivileged(new PrivilegedNewInstanceFromClass(getXMLPlatformClass()));
                }catch (PrivilegedActionException ex){
                    throw (RuntimeException) ex.getCause();
                }
            }else{
                return (XMLPlatform)PrivilegedAccessHelper.newInstanceFromClass(getXMLPlatformClass());

            }
        } catch (IllegalAccessException e) {
            throw XMLPlatformException.xmlPlatformCouldNotInstantiate(getXMLPlatformClass().getName(), e);
        } catch (InstantiationException e) {
            throw XMLPlatformException.xmlPlatformCouldNotInstantiate(getXMLPlatformClass().getName(), e);
        }
    }
}
