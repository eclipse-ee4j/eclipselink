/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Rick Barkhouse - 2.1 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.jaxb.dynamic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.bind.JAXBException;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.dynamic.DynamicHelper;
import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.dynamic.DynamicTypeBuilder;
import org.eclipse.persistence.internal.descriptors.InstantiationPolicy;
import org.eclipse.persistence.internal.jaxb.JaxbClassLoader;
import org.eclipse.persistence.jaxb.compiler.Generator;
import org.eclipse.persistence.jaxb.dynamic.metadata.Metadata;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.sessions.factories.XMLSessionConfigLoader;

/**
 * <p>
 * A specialized <tt>JAXBContext</tt> for marshalling and unmarshalling <tt>DynamicEntities</tt>.
 * </p>
 *
 * <p>
 * <tt>DynamicJAXBContext</tt> also provides methods to:
 * <ul>
 *      <li>get the <tt>DynamicType</tt> associated with a given Java name
 *      <li>get the <tt>DynamicType</tt> associated with a given XML name
 *      <li>create a new <tt>DynamicEntity</tt> given the Java name of its <tt>DynamicType</tt>
 *      <li>create a new <tt>DynamicEntity</tt> given the XML name of its <tt>DynamicType</tt>
 * </ul>
 * </p>
 *
 * <p>
 * New instances of <tt>DynamicJAXBContext</tt> must be created with <tt>DynamicJAXBContextFactory</tt>.
 * </p>
 *
 * @see javax.xml.bind.JAXBContext
 * @see org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContextFactory
 * @see org.eclipse.persistence.dynamic.DynamicEntity
 * @see org.eclipse.persistence.dynamic.DynamicType
 *
 * @author rbarkhouse
 * @since EclipseLink 2.1
 */

public class DynamicJAXBContext extends org.eclipse.persistence.jaxb.JAXBContext {

    private ArrayList<DynamicHelper> helpers;
    private DynamicClassLoader dClassLoader;

    DynamicJAXBContext(ClassLoader classLoader) {
        this.helpers = new ArrayList<DynamicHelper>();

        if (classLoader == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
        }
        if (classLoader instanceof DynamicClassLoader) {
           dClassLoader = (DynamicClassLoader) classLoader;
        } else {
           ClassLoader jaxbLoader = new JaxbClassLoader(classLoader);
           dClassLoader = new DynamicClassLoader(jaxbLoader);
        }
    }

    public DynamicClassLoader getDynamicClassLoader() {
        return dClassLoader;
    }

    /**
     * Obtain a reference to the <tt>DynamicType</tt> object for a given Java name.  If one has
     * not been generated, this method will return <tt>null</tt>.
     *
     * @param javaName
     *      A Java class name, used to look up its <tt>DynamicType</tt>.
     *
     * @return
     *      The <tt>DynamicType</tt> for this Java class name.
     */
    public DynamicType getDynamicType(String javaName) {
        for (DynamicHelper helper : this.helpers) {
            DynamicType type = helper.getType(javaName);
            if (type != null) {
                return type;
            }
        }
        return null;
    }

    /**
     * Create a new instance of <tt>DynamicEntity</tt> for a given Java name.  If a
     * <tt>DynamicType</tt> for this Java class name has not been generated, this
     * method will return <tt>null</tt>.
     *
     * @param javaName
     *      The Java class name to create a new <tt>DynamicEntity</tt> for.
     *
     * @return
     *      A new <tt>DynamicEntity</tt> for this Java class name.
     */
    public DynamicEntity newDynamicEntity(String javaName) throws IllegalArgumentException {
        IllegalArgumentException ex = null;
        for (DynamicHelper helper : this.helpers) {
            try {
                return helper.newDynamicEntity(javaName);
            } catch (IllegalArgumentException e) {
                ex = e;
            }
        }
        throw ex;
    }

    /**
     * Create a new instance of <tt>DynamicEntity</tt> for a given <tt>DynamicType</tt>.
     *
     * @param dynamicType
     *      The <tt>DynamicType</tt> to create a new <tt>DynamicEntity</tt> for.
     *
     * @return
     *      A new <tt>DynamicEntity</tt> for this <tt>DynamicType</tt>.
     */
    public DynamicEntity newDynamicEntity(DynamicType dynamicType) {
        return dynamicType.newDynamicEntity();
    }

    /**
     * Returns the constant named <tt>constantName</tt> from the enum class specified by <tt>enumName</tt>.
     *
     * @param enumName
     *      Java class name of an enum.
     * @param constantName
     *      Name of the constant to get from the specified enum.
     *
     * @return
     *      An <tt>Object</tt>, the constant from the specified enum.
     */
    public Object getEnumConstant(String enumName, String constantName) throws ClassNotFoundException, JAXBException {
        Object valueToReturn = null;

        Class<?> enumClass = dClassLoader.loadClass(enumName);
        Object[] enumConstants = enumClass.getEnumConstants();
        for (Object enumConstant : enumConstants) {
            if (enumConstant.toString().equals(constantName)) {
                valueToReturn = enumConstant;
            }
        }

        if (valueToReturn != null) {
            return valueToReturn;
        } else {
            throw new JAXBException(org.eclipse.persistence.exceptions.JAXBException.enumConstantNotFound(enumName + "." + constantName));
        }
    }

    @SuppressWarnings("unchecked")
    void initializeFromSessionsXML(String sessionNames, ClassLoader classLoader) {
        if (classLoader == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
        }
        if (classLoader instanceof DynamicClassLoader) {
           dClassLoader = (DynamicClassLoader) classLoader;
        } else {
           dClassLoader = new DynamicClassLoader(classLoader);
        }

        StringTokenizer st = new StringTokenizer(sessionNames, ":");
        ArrayList<Project> dynamicProjects = new ArrayList<Project>(st.countTokens());

        XMLSessionConfigLoader loader = new XMLSessionConfigLoader();

        while (st.hasMoreTokens()) {
            DatabaseSession dbSession =
                (DatabaseSession) SessionManager.getManager().getSession(loader, st.nextToken(), classLoader, false, true);
            Project p = DynamicTypeBuilder.loadDynamicProject(dbSession.getProject(), null, dClassLoader);
            dynamicProjects.add(p);
        }

        XMLContext xmlContext = new XMLContext(dynamicProjects);
        setXMLContext(xmlContext);

        List<Session> sessions = (List<Session>) xmlContext.getSessions();
        for (Object session : sessions) {
            this.helpers.add(new DynamicHelper((DatabaseSession) session));
        }
    }

    @SuppressWarnings("unchecked")
    void initializeFromMetadata(Metadata metadata, ClassLoader classLoader, Map<String, ?> properties) throws JAXBException {
        Generator g = new Generator(metadata.getJavaModelInput(), metadata.getBindings(), dClassLoader, null);

        Project p = null;
        Project dp = null;
        try {
            p = g.generateProject();
            // Clear out InstantiationPolicy because it refers to ObjectFactory, which we won't be using
            List<ClassDescriptor> descriptors = p.getOrderedDescriptors();
            for (ClassDescriptor classDescriptor : descriptors) {
                classDescriptor.setInstantiationPolicy(new InstantiationPolicy());
            }
            dp = DynamicTypeBuilder.loadDynamicProject(p, null, dClassLoader);
        } catch (Exception e) {
            throw new JAXBException(org.eclipse.persistence.exceptions.JAXBException.errorCreatingDynamicJAXBContext(e));
        }

        XMLContext xmlContext = new XMLContext(dp, dClassLoader);
        setXMLContext(xmlContext);

        List<Session> sessions = (List<Session>) xmlContext.getSessions();
        for (Object session : sessions) {
            this.helpers.add(new DynamicHelper((DatabaseSession) session));
        }
    }

}