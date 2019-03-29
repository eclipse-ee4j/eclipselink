/*
 * Copyright (c) 2011, 2019 Oracle and/or its affiliates. All rights reserved.
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
//     Rick Barkhouse - 2.1 - initial implementation
package org.eclipse.persistence.jaxb.dynamic;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.core.sessions.CoreProject;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.dynamic.DynamicHelper;
import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.dynamic.DynamicTypeBuilder;
import org.eclipse.persistence.internal.descriptors.InstantiationPolicy;
import org.eclipse.persistence.internal.jaxb.JaxbClassLoader;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedGetContextClassLoader;
import org.eclipse.persistence.jaxb.compiler.Generator;
import org.eclipse.persistence.jaxb.dynamic.metadata.Metadata;
import org.eclipse.persistence.jaxb.dynamic.metadata.OXMMetadata;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.sessions.factories.XMLSessionConfigLoader;
import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;

/**
 * <p>
 * A specialized <code>JAXBContext</code> for marshalling and unmarshalling <code>DynamicEntities</code>.
 * </p>
 *
 * <p>
 * <code>DynamicJAXBContext</code> also provides methods to:
 * </p>
 * <ul>
 *      <li>get the <code>DynamicType</code> associated with a given Java name
 *      <li>get the <code>DynamicType</code> associated with a given XML name
 *      <li>create a new <code>DynamicEntity</code> given the Java name of its <code>DynamicType</code>
 *      <li>create a new <code>DynamicEntity</code> given the XML name of its <code>DynamicType</code>
 * </ul>
 *
 * <p>
 * New instances of <code>DynamicJAXBContext</code> must be created with <code>DynamicJAXBContextFactory</code>.
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
     * Obtain a reference to the <code>DynamicType</code> object for a given Java name.  If one has
     * not been generated, this method will return <code>null</code>.
     *
     * @param javaName
     *      A Java class name, used to look up its <code>DynamicType</code>.
     *
     * @return
     *      The <code>DynamicType</code> for this Java class name.
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
     * Create a new instance of <code>DynamicEntity</code> for a given Java name.  If a
     * <code>DynamicType</code> for this Java class name has not been generated, this
     * method will return <code>null</code>.
     *
     * @param javaName
     *      The Java class name to create a new <code>DynamicEntity</code> for.
     *
     * @return
     *      A new <code>DynamicEntity</code> for this Java class name.
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
     * Create a new instance of <code>DynamicEntity</code> for a given <code>DynamicType</code>.
     *
     * @param dynamicType
     *      The <code>DynamicType</code> to create a new <code>DynamicEntity</code> for.
     *
     * @return
     *      A new <code>DynamicEntity</code> for this <code>DynamicType</code>.
     */
    public DynamicEntity newDynamicEntity(DynamicType dynamicType) {
        return dynamicType.newDynamicEntity();
    }

    /**
     * Returns the constant named <code>constantName</code> from the enum class specified by <code>enumName</code>.
     *
     * @param enumName
     *      Java class name of an enum.
     * @param constantName
     *      Name of the constant to get from the specified enum.
     *
     * @return
     *      An <code>Object</code>, the constant from the specified enum.
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
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
                    try {
                        classLoader =  AccessController.doPrivileged(new PrivilegedGetContextClassLoader(Thread.currentThread()));
                    } catch (PrivilegedActionException pae) {
                        //should not be thrown but make sure that when it does, it's properly reported
                        throw new RuntimeException(pae);
                    }
                } else {
                    classLoader = PrivilegedAccessHelper.getContextClassLoader(Thread.currentThread());
                }
            }
            if (classLoader instanceof DynamicClassLoader) {
               dClassLoader = (DynamicClassLoader) classLoader;
            } else {
                final ClassLoader parent = classLoader;
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
                    dClassLoader = AccessController.doPrivileged(new PrivilegedAction<DynamicClassLoader>() {
                        @Override
                        public DynamicClassLoader run() {
                            return new DynamicClassLoader(new JaxbClassLoader(parent));
                        }
                    });
                } else {
                    ClassLoader jaxbLoader = new JaxbClassLoader(parent);
                    dClassLoader = new DynamicClassLoader(jaxbLoader);
                }
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

            List<Session> sessions = ctx.getSessions();
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
                constructorArg = schema;
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

            XMLContext ctx = new XMLContext(dp, classLoader, sessionEventListeners());
            state.setXMLContext(ctx);

            List<Session> sessions = ctx.getSessions();
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

            List<Session> sessions = xmlContext.getSessions();
            for (Object session : sessions) {
                state.getHelpers().add(new DynamicHelper((DatabaseSession) session));
            }

            return state;
        }
    }

}
