/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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

        String newXMLPlatformClassName = System.getProperty(XML_PLATFORM_PROPERTY);
        if (null == newXMLPlatformClassName) {
            newXMLPlatformClassName = JAXP_PLATFORM_CLASS_NAME;
        }

        try {
            ClassLoader classLoader = null;
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
                try {
                    classLoader = (ClassLoader)AccessController.doPrivileged(new PrivilegedGetClassLoaderForClass(this.getClass()));
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
