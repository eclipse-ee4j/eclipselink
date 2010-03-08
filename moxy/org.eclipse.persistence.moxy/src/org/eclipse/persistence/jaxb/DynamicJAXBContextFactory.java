/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     rbarkhouse - 2010-03-04 12:22:11 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.jaxb;

import java.util.Map;

import javax.xml.bind.JAXBException;

/**
 * <p>
 * DynamicJAXBContextFactory allows the user to create a DynamicJAXBContext without having
 * realized Java classes available on the classpath.  During context creation, the user's
 * metadata will be analyzed, and in-memory classes will be generated.
 * </p>
 *
 * <p>
 * Objects that are returned by EclipseLink unmarshal methods will be subclasses of DynamicEntity.
 * DynamicEntities offer a simple get(propertyName) / set(propertyName, propertyValue) API to
 * manipulate their data.
 * </p>
 *
 * <p>
 * Example:
 * </p>
 *
 * <p><code>
 * String sessionName = "mynamespace.Employee"; <br>
 * ClassLoader loader = Thread.currentThread().getContextClassLoader(); <br><br>
 *
 * DynamicJAXBContext dContext = DynamicJAXBContextFactory.createContext(sessionName, null); <br><br>
 *
 * DynamicEntity employee = dContext.newDynamicEntity("mynamespace.Employee"); <br>
 * employee.set("firstName", "Bob"); <br>
 * employee.set("lastName", "Barker"); <br>
 * dContext.createMarshaller().(employee, System.out);
 * </code></p>
 *
 * @see javax.xml.bind.JAXBContext
 * @see org.eclipse.persistence.jaxb.DynamicJAXBContext
 * @see org.eclipse.persistence.dynamic.DynamicEntity
 * @see org.eclipse.persistence.dynamic.DynamicType
 *
 * @author rbarkhouse
 * @since EclipseLink 2.1
 */
public class DynamicJAXBContextFactory {

    /**
     * Create a <tt>DynamicJAXBContext</tt>, using an EclipseLink <tt>sessions.xml</tt> as the metadata source.
     * The <tt>sessionNames</tt> parameter is a colon-delimited list of session names within the
     * <tt>sessions.xml</tt> file.  <tt>Descriptors</tt> in this session's <tt>Project</tt> must <i>not</i>
     * have <tt>javaClass</tt> set, but <i>must</i> have <tt>javaClassName</tt> set.
     *
     * @param sessionNames
     *      A colon-delimited <tt>String</tt> specifying the session names from the <tt>sessions.xml</tt> file.
     * @param classLoader
     *      The application's current class loader, which will be used to first lookup
     *      classes to see if they exist before new <tt>DynamicTypes</tt> are generated.  Can be
     *      <tt>null</tt>, in which case <tt>Thread.currentThread().getContextClassLoader()</tt> will be used.
     *
     * @return
     *      A new instance of <tt>DynamicJAXBContext</tt>.
     *
     * @throws JAXBException
     *      if an error was encountered while creating the <tt>DynamicJAXBContext</tt>.
     */
    public static DynamicJAXBContext createContext(String sessionNames, ClassLoader classLoader, Map<String, ?> props) throws JAXBException {
        DynamicJAXBContext dContext = new DynamicJAXBContext();
        dContext.initializeFromSessionsXML(sessionNames, classLoader);
        return dContext;
    }

    /**
     * Unsupported Operation.  DynamicJAXBConexts can not be created from concrete classes.  Use the standard
     * JAXBContext to create a context from existing Classes.
     * 
     * @see org.eclipse.persistence.jaxb.JAXBContext
     */
    public static DynamicJAXBContext createContext(Class[] classes, Map props) throws JAXBException {
        throw new JAXBException(org.eclipse.persistence.exceptions.JAXBException.cannotCreateDynamicContextFromClasses());
    }
    
}