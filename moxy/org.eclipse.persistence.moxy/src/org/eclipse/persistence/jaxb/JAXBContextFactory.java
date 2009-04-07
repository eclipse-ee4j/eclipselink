/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
package org.eclipse.persistence.jaxb;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.xml.bind.JAXBException;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.jaxb.compiler.*;
import org.eclipse.persistence.jaxb.javamodel.reflection.JavaModelImpl;
import org.eclipse.persistence.jaxb.javamodel.reflection.JavaModelInputImpl;
import org.eclipse.persistence.internal.jaxb.JaxbClassLoader;
import org.eclipse.persistence.internal.jaxb.SessionEventListener;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.sessions.Project;


/**
 * INTERNAL:
 * <p><b>Purpose:</b>A TopLink specific JAXBContextFactory. This class can be specified in a 
 * jaxb.properties file to make use of TopLink's JAXB 2.0 implementation. 
 * <p><b>Responsibilities:</b><ul>
 * <li>Create a JAXBContext from an array of Classes and a Properties object</li>
 * <li>Create a JAXBContext from a context path and a classloader</li>
 * </ul>
 * <p>This class is the entry point into in TopLink's JAXB 2.0 Runtime. It provides the required
 * factory methods and is invoked by javax.xml.bind.JAXBContext.newInstance() to create new 
 * instances of JAXBContext. When creating a JAXBContext from a contextPath, the list of classes
 * is derived either from an ObjectFactory class (schema-to-java) or a jaxb.index file (java-to-schema).
 * 
 * @author mmacivor
 * @since Oracle TopLink 11.1.1.0.0
 * @see javax.xml.bind.JAXBContext
 * @see org.eclipse.persistence.jaxb.JAXBContext
 * @see org.eclipse.persistence.jaxb.compiler.Generator
 */
public class JAXBContextFactory {
    public static javax.xml.bind.JAXBContext createContext(Class[] classesToBeBound, java.util.Map properties) throws JAXBException {
        ClassLoader loader = null;
        if (classesToBeBound.length > 0) {
            loader = classesToBeBound[0].getClassLoader();
        }
        return createContext(classesToBeBound, properties, loader);
    }

    public static javax.xml.bind.JAXBContext createContext(Class[] classesToBeBound, java.util.Map properties, ClassLoader classLoader) throws JAXBException {
        javax.xml.bind.JAXBContext jaxbContext = null;
        XMLContext xmlContext = null;
        JaxbClassLoader loader = new JaxbClassLoader(classLoader);
        
        try {
            Generator generator = new Generator(new JavaModelInputImpl(classesToBeBound, new JavaModelImpl(loader)));
            	
            Project proj = generator.generateProject();
            ConversionManager conversionManager = null;
            if (classLoader != null) {
                conversionManager = new ConversionManager();
                conversionManager.setLoader(loader);
            } else {
                conversionManager = ConversionManager.getDefaultManager();
            }
            proj.convertClassNamesToClasses(conversionManager.getLoader());
            // need to make sure that the java class is set properly on each 
            // descriptor when using java classname - req'd for JOT api implementation 
            for (Iterator<ClassDescriptor> descriptorIt = proj.getOrderedDescriptors().iterator(); descriptorIt.hasNext();) {
                ClassDescriptor descriptor = descriptorIt.next();
                if (descriptor.getJavaClass() == null) {
                    descriptor.setJavaClass(conversionManager.convertClassNameToClass(descriptor.getJavaClassName()));
                }
            }
            
            // disable instantiation policy validation during descriptor initialization
            SessionEventListener eventListener = new SessionEventListener();
            eventListener.setShouldValidateInstantiationPolicy(false);
            
            xmlContext = new XMLContext(proj, loader, eventListener);
            jaxbContext = new org.eclipse.persistence.jaxb.JAXBContext(xmlContext, generator);
        } catch (Exception ex) {
            throw new JAXBException(ex.getMessage() ,ex);
        }
        return jaxbContext;

    }

    public static javax.xml.bind.JAXBContext createContext(String contextPath, ClassLoader classLoader) throws JAXBException {
        try {
            XMLContext xmlContext = new XMLContext(contextPath, classLoader);
            return new org.eclipse.persistence.jaxb.JAXBContext(xmlContext);
        } catch (ValidationException vex) {
            if(vex.getErrorCode() != ValidationException.NO_SESSIONS_XML_FOUND) {
                //If something went wrong other than not finding a sessions.xml re-throw the exception
                throw new JAXBException(vex);
            }
        } catch (Exception ex) {
            throw new JAXBException(ex);
        }
        ArrayList classes = new ArrayList();
        StringTokenizer tokenizer = new StringTokenizer(contextPath, ":");
        while (tokenizer.hasMoreElements()) {
            String path = tokenizer.nextToken();
            try {
                Class objectFactory = classLoader.loadClass(path + ".ObjectFactory");
                if(isJAXB2ObjectFactory(objectFactory)) {
                    classes.add(objectFactory);
                }
            } catch (Exception ex) {
                //if there's no object factory, don't worry about it. Check for jaxb.index next
            }
            try {
                //try to load package info just to be safe
                classLoader.loadClass(path + ".package-info");
            } catch (Exception ex) {
            }
            //Next check for a jaxb.index file in case there's one available
            InputStream jaxbIndex = classLoader.getResourceAsStream(path.replace('.', '/') + "/jaxb.index");
            if (jaxbIndex != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(jaxbIndex));
                try {
                    String line = reader.readLine();
                    while (line != null) {
                        String className = path + "." + line.trim();
                        try {
                            classes.add(classLoader.loadClass(className));
                        } catch (Exception ex) {
                            //just ignore for now if the class isn't available.
                        }
                        line = reader.readLine();
                    }
                } catch (Exception ex) {
                }
            }
        }
        if(classes.size() == 0) {
            throw new JAXBException(org.eclipse.persistence.exceptions.JAXBException.noObjectFactoryOrJaxbIndexInPath(contextPath));
        }
        Class[] classArray = new Class[classes.size()];
        for (int i = 0; i < classes.size(); i++) {
            classArray[i] = (Class) classes.get(i);
        }
        return createContext(classArray, null, classLoader);
    }
    private static boolean isJAXB2ObjectFactory(Class objectFactoryClass) {
        try {
            Class xmlRegistry = PrivilegedAccessHelper.getClassForName("javax.xml.bind.annotation.XmlRegistry");
            if(objectFactoryClass.isAnnotationPresent(xmlRegistry)) {
                return true;
            }
            return false;
        } catch(Exception ex) {
            return false;
        }
    }
}
