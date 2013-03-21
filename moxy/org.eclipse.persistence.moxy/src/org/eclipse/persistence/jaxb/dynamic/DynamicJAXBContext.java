/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
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

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import javax.xml.bind.JAXBException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.core.sessions.CoreProject;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.dynamic.*;
import org.eclipse.persistence.internal.descriptors.InstantiationPolicy;
import org.eclipse.persistence.internal.jaxb.JaxbClassLoader;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.jaxb.compiler.Generator;
import org.eclipse.persistence.jaxb.dynamic.metadata.Metadata;
import org.eclipse.persistence.jaxb.dynamic.metadata.OXMMetadata;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.sessions.factories.XMLSessionConfigLoader;
import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;

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

    DynamicJAXBContext(JAXBContextInput input) throws JAXBException {
        super(input);
    }

    public DynamicClassLoader getDynamicClassLoader() {
        return ((DynamicJAXBContextInput) contextInput).getClassLoader();
    }

    private ArrayList<DynamicHelper> getHelpers() {
        return ((DynamicJAXBContextState) contextState).getHelpers();
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
        for (DynamicHelper helper : getHelpers()) {
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
        for (DynamicHelper helper : getHelpers()) {
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

        Class<?> enumClass = getDynamicClassLoader().loadClass(enumName);
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

    // ========================================================================

    static class DynamicJAXBContextState extends JAXBContextState {
        private ArrayList<DynamicHelper> helpers;
        private DynamicClassLoader dClassLoader;

        public DynamicJAXBContextState(DynamicClassLoader loader) {
            super();
            helpers = new ArrayList<DynamicHelper>();
        }

        public DynamicJAXBContextState(XMLContext ctx) {
            super(ctx);
        }

        public ArrayList<DynamicHelper> getHelpers() {
            return helpers;
        }

        public void setHelpers(ArrayList<DynamicHelper> helpers) {
            this.helpers = helpers;
        }

        public DynamicClassLoader getDynamicClassLoader() {
            return dClassLoader;
        }

        public void setDynamicClassLoader(DynamicClassLoader dClassLoader) {
            this.dClassLoader = dClassLoader;
        }
    }

    // ========================================================================

    static abstract class DynamicJAXBContextInput extends org.eclipse.persistence.jaxb.JAXBContext.JAXBContextInput {
        public DynamicJAXBContextInput(Map properties, ClassLoader classLoader) {
            super(properties, classLoader);

            DynamicClassLoader dClassLoader = null;

            if (classLoader == null) {
                classLoader = Thread.currentThread().getContextClassLoader();
            }
            if (classLoader instanceof DynamicClassLoader) {
               dClassLoader = (DynamicClassLoader) classLoader;
            } else {
               ClassLoader jaxbLoader = new JaxbClassLoader(classLoader);
               dClassLoader = new DynamicClassLoader(jaxbLoader);
            }

            this.classLoader = dClassLoader;
        }

        public DynamicClassLoader getClassLoader() {
            return (DynamicClassLoader) this.classLoader;
        }
    }

    static class MetadataContextInput extends DynamicJAXBContextInput {
        public MetadataContextInput(Map properties, ClassLoader classLoader) {
            super(properties, classLoader);
        }

        @Override
        protected JAXBContextState createContextState() throws JAXBException {
            DynamicJAXBContextState state = new DynamicJAXBContextState((DynamicClassLoader) classLoader);

            Metadata oxmMetadata = new OXMMetadata((DynamicClassLoader) classLoader, properties);

            Generator g = new Generator(oxmMetadata.getJavaModelInput(), oxmMetadata.getBindings(), classLoader, null, false);

            CoreProject p = null;
            Project dp = null;
            try {
                p = g.generateProject();
                // Clear out InstantiationPolicy because it refers to ObjectFactory, which we won't be using
                List<ClassDescriptor> descriptors = p.getOrderedDescriptors();
                for (ClassDescriptor classDescriptor : descriptors) {
                    try {
                        if(null == classDescriptor.getJavaClass()) {
                            classDescriptor.setJavaClass(classLoader.getParent().loadClass(classDescriptor.getJavaClassName()));
                        }
                    } catch(ClassNotFoundException e) {
                        classDescriptor.setInstantiationPolicy(new InstantiationPolicy());
                    }
                }
                dp = DynamicTypeBuilder.loadDynamicProject((Project)p, null, (DynamicClassLoader) classLoader);
            } catch (Exception e) {
                throw new JAXBException(org.eclipse.persistence.exceptions.JAXBException.errorCreatingDynamicJAXBContext(e));
            }

            XMLContext ctx = new XMLContext(dp, classLoader, sessionEventListeners());
            state.setXMLContext(ctx);

            List<Session> sessions = (List<Session>) ctx.getSessions();
            for (Object session : sessions) {
                state.getHelpers().add(new DynamicHelper((DatabaseSession) session));
            }

            return state;
        }
    }

    static class SchemaContextInput extends DynamicJAXBContextInput {
        private Object schema = null;
        private EntityResolver entityResolver;

        public static final String SCHEMAMETADATA_CLASS_NAME = "org.eclipse.persistence.jaxb.dynamic.metadata.SchemaMetadata";

        public SchemaContextInput(Object schema, EntityResolver resolver, Map properties, ClassLoader classLoader) {
            super(properties, classLoader);
            this.schema = schema;
            this.entityResolver = resolver;
        }

        @Override
        protected JAXBContextState createContextState() throws JAXBException {
            DynamicJAXBContextState state = new DynamicJAXBContextState((DynamicClassLoader) classLoader);

            Metadata schemaMetadata = null;
            Object constructorArg;
            if (schema instanceof Node) {
                constructorArg = schema;
            } else if (schema instanceof InputStream) {
                constructorArg = new StreamSource((InputStream) schema);
            } else {
                constructorArg = (Source) schema;
            }

            try {
                Class<?> schemaMetadataClass = PrivilegedAccessHelper.getClassForName(SCHEMAMETADATA_CLASS_NAME);
                Class<?>[] constructorClassArgs = {DynamicClassLoader.class, Map.class, constructorArg.getClass(), EntityResolver.class};
                Constructor<?> constructor = PrivilegedAccessHelper.getConstructorFor(schemaMetadataClass, constructorClassArgs, true);
                Object[] contructorObjectArgs = {(DynamicClassLoader) classLoader, properties, constructorArg, entityResolver};
                schemaMetadata = (Metadata) PrivilegedAccessHelper.invokeConstructor(constructor, contructorObjectArgs);
            } catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                if (cause instanceof JAXBException) {
                    throw (JAXBException) cause;
                } else if (cause instanceof org.eclipse.persistence.exceptions.JAXBException) {
                	throw (org.eclipse.persistence.exceptions.JAXBException) cause;
                } else {
                    throw new JAXBException(e);
                }
            } catch (org.eclipse.persistence.exceptions.JAXBException e) {
                throw e;
            } catch (Exception e) {
                throw new JAXBException(e);
            }

            Generator g = new Generator(schemaMetadata.getJavaModelInput(), schemaMetadata.getBindings(), classLoader, null, false);

            Project p = null;
            Project dp = null;
            try {
                p = (Project) g.generateProject();
                // Clear out InstantiationPolicy because it refers to ObjectFactory, which we won't be using
                List<ClassDescriptor> descriptors = p.getOrderedDescriptors();
                for (ClassDescriptor classDescriptor : descriptors) {
                    classDescriptor.setInstantiationPolicy(new InstantiationPolicy());
                }
                dp = DynamicTypeBuilder.loadDynamicProject(p, null, (DynamicClassLoader) classLoader);
            } catch (Exception e) {
                throw new JAXBException(org.eclipse.persistence.exceptions.JAXBException.errorCreatingDynamicJAXBContext(e));
            }

            XMLContext ctx = new XMLContext(dp, (DynamicClassLoader) classLoader, sessionEventListeners());
            state.setXMLContext(ctx);

            List<Session> sessions = (List<Session>) ctx.getSessions();
            for (Object session : sessions) {
                state.getHelpers().add(new DynamicHelper((DatabaseSession) session));
            }

            return state;
        }
    }

    static class SessionsXmlContextInput extends DynamicJAXBContextInput {
        private String sessions;

        public SessionsXmlContextInput(String sessionNames, Map properties, ClassLoader classLoader) {
            super(properties, classLoader);
            this.sessions = sessionNames;
        }

        @Override
        protected JAXBContextState createContextState() throws JAXBException {
            DynamicJAXBContextState state = new DynamicJAXBContextState((DynamicClassLoader) classLoader);

            StringTokenizer st = new StringTokenizer(sessions, ":");
            ArrayList<Project> dynamicProjects = new ArrayList<Project>(st.countTokens());

            XMLSessionConfigLoader loader = new XMLSessionConfigLoader();

            while (st.hasMoreTokens()) {
                DatabaseSession dbSession =
                    (DatabaseSession) SessionManager.getManager().getSession(loader, st.nextToken(), classLoader, false, true);
                Project p = DynamicTypeBuilder.loadDynamicProject(dbSession.getProject(), null, (DynamicClassLoader) classLoader);
                dynamicProjects.add(p);
            }

            XMLContext xmlContext = new XMLContext(dynamicProjects);
            state.setXMLContext(xmlContext);

            List<Session> sessions = (List<Session>) xmlContext.getSessions();
            for (Object session : sessions) {
                state.getHelpers().add(new DynamicHelper((DatabaseSession) session));
            }

            return state;
        }
    }

}