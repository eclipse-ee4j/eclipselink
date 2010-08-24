/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.xml.bind.JAXBException;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.dynamic.DynamicHelper;
import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.dynamic.DynamicTypeBuilder;
import org.eclipse.persistence.internal.descriptors.InstantiationPolicy;
import org.eclipse.persistence.internal.jaxb.JaxbClassLoader;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.compiler.Generator;
import org.eclipse.persistence.jaxb.javamodel.JavaClass;
import org.eclipse.persistence.jaxb.javamodel.oxm.OXMJavaClassImpl;
import org.eclipse.persistence.jaxb.javamodel.oxm.OXMJavaModelImpl;
import org.eclipse.persistence.jaxb.javamodel.oxm.OXMJavaModelInputImpl;
import org.eclipse.persistence.jaxb.javamodel.xjc.XJCJavaClassImpl;
import org.eclipse.persistence.jaxb.javamodel.xjc.XJCJavaModelImpl;
import org.eclipse.persistence.jaxb.javamodel.xjc.XJCJavaModelInputImpl;
import org.eclipse.persistence.jaxb.xmlmodel.JavaType;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.sessions.factories.XMLSessionConfigLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

import com.sun.codemodel.ClassType;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JEnumConstant;
import com.sun.codemodel.JPackage;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.api.ErrorListener;
import com.sun.tools.xjc.api.S2JJAXBModel;
import com.sun.tools.xjc.api.SchemaCompiler;
import com.sun.tools.xjc.api.XJC;

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

    private static final String SYSTEM_ID = "";

    private Field JDEFINEDCLASS_ENUMCONSTANTS = null;

    DynamicJAXBContext() throws JAXBException {
        this.helpers = new ArrayList<DynamicHelper>();

        try {
            JDEFINEDCLASS_ENUMCONSTANTS = PrivilegedAccessHelper.getDeclaredField(JDefinedClass.class, "enumConstantsByName", true);
        } catch (Exception e) {
            throw new JAXBException(org.eclipse.persistence.exceptions.JAXBException.errorCreatingDynamicJAXBContext(e));
        }
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
     * Returns the contant named <tt>constantName</tt> from the enum class specified by <tt>enumName</tt>.
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
        DynamicClassLoader dynamicClassLoader;
        if (classLoader instanceof DynamicClassLoader) {
           dynamicClassLoader = (DynamicClassLoader) classLoader;
        } else {
           dynamicClassLoader = new DynamicClassLoader(classLoader);
        }

        dClassLoader = dynamicClassLoader;

        StringTokenizer st = new StringTokenizer(sessionNames, ":");
        ArrayList<Project> dynamicProjects = new ArrayList<Project>(st.countTokens());

        XMLSessionConfigLoader loader = new XMLSessionConfigLoader();

        while (st.hasMoreTokens()) {
            DatabaseSession dbSession =
                (DatabaseSession) SessionManager.getManager().getSession(loader, st.nextToken(), classLoader, false, true);
            Project p = DynamicTypeBuilder.loadDynamicProject(dbSession.getProject(), null, dynamicClassLoader);
            dynamicProjects.add(p);
        }

        this.xmlContext = new XMLContext(dynamicProjects);

        List<Session> sessions = (List<Session>) this.xmlContext.getSessions();
        for (Object session : sessions) {
            this.helpers.add(new DynamicHelper((DatabaseSession) session));
        }
    }

    @SuppressWarnings("unchecked")
    void initializeFromOXM(ClassLoader classLoader, Map<String, ?> properties) throws JAXBException {
        if (classLoader == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
        }
        DynamicClassLoader dynamicClassLoader;

        ClassLoader jaxbLoader = new JaxbClassLoader(classLoader);

        if (classLoader instanceof DynamicClassLoader) {
           dynamicClassLoader = (DynamicClassLoader) classLoader;
        } else {
           dynamicClassLoader = new DynamicClassLoader(jaxbLoader);
        }

        dClassLoader = dynamicClassLoader;

        Map<String, XmlBindings> bindings = JAXBContextFactory.getXmlBindingsFromProperties(properties, classLoader);

        JavaClass[] elinkClasses = createClassModelFromOXM(bindings);

        // Use the JavaModel to setup a Generator to generate an EclipseLink project
        OXMJavaModelImpl javaModel = new OXMJavaModelImpl(classLoader, elinkClasses);
        OXMJavaModelInputImpl javaModelInput = new OXMJavaModelInputImpl(elinkClasses, javaModel);
        Generator g = new Generator(javaModelInput, bindings, dynamicClassLoader, null);

        Project p = null;
        Project dp = null;
        try {
            p = g.generateProject();
            // Clear out InstantiationPolicy because it refers to ObjectFactory, which we won't be using
            Vector<ClassDescriptor> descriptors = (Vector<ClassDescriptor>) p.getOrderedDescriptors();
            for (ClassDescriptor classDescriptor : descriptors) {
                classDescriptor.setInstantiationPolicy(new InstantiationPolicy());
            }
            dp = DynamicTypeBuilder.loadDynamicProject(p, null, dynamicClassLoader);
        } catch (Exception e) {
            throw new JAXBException(org.eclipse.persistence.exceptions.JAXBException.errorCreatingDynamicJAXBContext(e));
        }

        this.xmlContext = new XMLContext(dp);

        List<Session> sessions = (List<Session>) this.xmlContext.getSessions();
        for (Object session : sessions) {
            this.helpers.add(new DynamicHelper((DatabaseSession) session));
        }
    }

    private JavaClass[] createClassModelFromOXM(Map<String, XmlBindings> bindings) throws JAXBException {
        List<OXMJavaClassImpl> oxmJavaClasses = new ArrayList<OXMJavaClassImpl>();

        Iterator<String> keys = bindings.keySet().iterator();

        while (keys.hasNext()) {
            XmlBindings b = bindings.get(keys.next());

            List<JavaType> javaTypes = b.getJavaTypes().getJavaType();
            for (Iterator<JavaType> iterator = javaTypes.iterator(); iterator.hasNext();) {
                JavaType type = iterator.next();
                oxmJavaClasses.add(new OXMJavaClassImpl(type));
            }
        }

        JavaClass[] javaClasses = new JavaClass[oxmJavaClasses.size()];
        for (int i = 0; i < javaClasses.length; i++) {
            javaClasses[i] = oxmJavaClasses.get(i);
        }

        return javaClasses;
    }

    void initializeFromXSDNode(Node node, ClassLoader classLoader, EntityResolver resolver, Map<String, ?> properties) throws JAXBException {
        Element element;

        if (node.getNodeType() == Node.DOCUMENT_NODE) {
            element = ((Document) node).getDocumentElement();
        } else if (node.getNodeType() == Node.ELEMENT_NODE) {
            element = (Element) node;
        } else {
            throw new JAXBException(org.eclipse.persistence.exceptions.JAXBException.cannotInitializeFromNode());
        }

        // Use XJC API to parse the schema and generate its JCodeModel
        SchemaCompiler sc = XJC.createSchemaCompiler();
        sc.setEntityResolver(resolver);
        sc.setErrorListener(new XJCErrorListener());
        sc.parseSchema(SYSTEM_ID, element);
        S2JJAXBModel model = sc.bind();

        if (model == null) {
            throw new JAXBException(org.eclipse.persistence.exceptions.JAXBException.xjcBindingError());
        }

        JCodeModel jCodeModel = model.generateCode(new Plugin[0], null);

        initializeFromXJC(jCodeModel, classLoader, properties);
    }

    void initializeFromXSDInputSource(InputSource metadataSource, ClassLoader classLoader, EntityResolver resolver, Map<String, ?> properties) throws JAXBException {
        // Use XJC API to parse the schema and generate its JCodeModel
        SchemaCompiler sc = XJC.createSchemaCompiler();
        if (metadataSource.getSystemId() == null) {
            metadataSource.setSystemId(SYSTEM_ID);
        }
        sc.setEntityResolver(resolver);
        sc.setErrorListener(new XJCErrorListener());
        sc.parseSchema(metadataSource);
        S2JJAXBModel model = sc.bind();

        if (model == null) {
            throw new JAXBException(org.eclipse.persistence.exceptions.JAXBException.xjcBindingError());
        }

        JCodeModel jCodeModel = model.generateCode(new Plugin[0], null);

        initializeFromXJC(jCodeModel, classLoader, properties);
    }

    @SuppressWarnings("unchecked")
    void initializeFromXJC(JCodeModel codeModel, ClassLoader classLoader, Map<String, ?> properties) throws JAXBException {
        if (classLoader == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
        }
        DynamicClassLoader dynamicClassLoader;

        ClassLoader jaxbLoader = new JaxbClassLoader(classLoader);

        if (classLoader instanceof DynamicClassLoader) {
           dynamicClassLoader = (DynamicClassLoader) classLoader;
        } else {
           dynamicClassLoader = new DynamicClassLoader(jaxbLoader);
        }

        dClassLoader = dynamicClassLoader;

        // Create EclipseLink JavaModel objects for each of XJC's JDefinedClasses
        ArrayList<JDefinedClass> classesToProcess = new ArrayList<JDefinedClass>();
        Iterator<JPackage> packages = codeModel.packages();
        while (packages.hasNext()) {
            JPackage pkg = packages.next();
            Iterator<JDefinedClass> classes = pkg.classes();
            while (classes.hasNext()) {
                JDefinedClass cls = classes.next();
                classesToProcess.add(cls);
            }
        }

        // Look for Inner Classes and add them
        ArrayList<JDefinedClass> innerClasses = new ArrayList<JDefinedClass>();
        for (int i = 0; i < classesToProcess.size(); i++) {
            innerClasses.addAll(getInnerClasses(classesToProcess.get(i)));
        }
        classesToProcess.addAll(innerClasses);

        JavaClass[] jotClasses = createClassModelFromXJC(classesToProcess, codeModel, dynamicClassLoader);

        // Look for bindings customization file
        Map<String, XmlBindings> bindings = null;
        if (properties != null) {
            bindings = JAXBContextFactory.getXmlBindingsFromProperties(properties, dClassLoader);
        }

        // Use the JavaModel to setup a Generator to generate an EclipseLink project
        XJCJavaModelImpl javaModel = new XJCJavaModelImpl(jaxbLoader, codeModel, dynamicClassLoader);
        XJCJavaModelInputImpl javaModelInput = new XJCJavaModelInputImpl(jotClasses, javaModel);
        Generator g = new Generator(javaModelInput, bindings, dynamicClassLoader, null);

        Project p = null;
        Project dp = null;
        try {
            p = g.generateProject();
            // Clear out InstantiationPolicy because it refers to ObjectFactory, which we won't be using
            Vector<ClassDescriptor> descriptors = (Vector<ClassDescriptor>) p.getOrderedDescriptors();
            for (ClassDescriptor classDescriptor : descriptors) {
                classDescriptor.setInstantiationPolicy(new InstantiationPolicy());
            }
            dp = DynamicTypeBuilder.loadDynamicProject(p, null, dynamicClassLoader);
        } catch (Exception e) {
            throw new JAXBException(org.eclipse.persistence.exceptions.JAXBException.errorCreatingDynamicJAXBContext(e));
        }

        this.xmlContext = new XMLContext(dp);

        List<Session> sessions = (List<Session>) this.xmlContext.getSessions();
        for (Object session : sessions) {
            this.helpers.add(new DynamicHelper((DatabaseSession) session));
        }
    }

    private HashSet<JDefinedClass> getInnerClasses(JDefinedClass xjcClass) {
        // Check this xjcClass for inner classes.  If one is found, search that one too.

        HashSet<JDefinedClass> classesToReturn = new HashSet<JDefinedClass>();
        Iterator<JDefinedClass> it = xjcClass.classes();

        while (it.hasNext()) {
            JDefinedClass innerClass = it.next();
            classesToReturn.add(innerClass);
            classesToReturn.addAll(getInnerClasses(innerClass));
        }

        return classesToReturn;
    }

    @SuppressWarnings("unchecked")
    private JavaClass[] createClassModelFromXJC(ArrayList<JDefinedClass> xjcClasses, JCodeModel jCodeModel, DynamicClassLoader dynamicClassLoader) throws JAXBException {
        try {
            JavaClass[] elinkClasses = new JavaClass[xjcClasses.size()];

            int count = 0;
            for (JDefinedClass definedClass : xjcClasses) {
                XJCJavaClassImpl xjcClass = new XJCJavaClassImpl(definedClass, jCodeModel, dynamicClassLoader);
                elinkClasses[count] = xjcClass;
                count++;

                // If this is an enum, trigger a dynamic class generation, because we won't
                // be creating a descriptor for it
                if (definedClass.getClassType().equals(ClassType.ENUM)) {
                    Map<String, JEnumConstant> enumConstants = (Map<String, JEnumConstant>) PrivilegedAccessHelper.getValueFromField(JDEFINEDCLASS_ENUMCONSTANTS, definedClass);
                    Object[] enumValues = enumConstants.keySet().toArray();
                    dynamicClassLoader.addEnum(definedClass.fullName(), enumValues);
                }
            }

            return elinkClasses;
        } catch (Exception e) {
            throw new JAXBException(org.eclipse.persistence.exceptions.JAXBException.errorCreatingDynamicJAXBContext(e));
        }
    }

    private class XJCErrorListener implements ErrorListener {

        public void error(SAXParseException arg0) {
            throw org.eclipse.persistence.exceptions.JAXBException.errorCreatingDynamicJAXBContext(arg0);
        }

        public void fatalError(SAXParseException arg0) {
            throw org.eclipse.persistence.exceptions.JAXBException.errorCreatingDynamicJAXBContext(arg0);
        }

        public void info(SAXParseException arg0) {
            throw org.eclipse.persistence.exceptions.JAXBException.errorCreatingDynamicJAXBContext(arg0);
        }

        public void warning(SAXParseException arg0) {
            throw org.eclipse.persistence.exceptions.JAXBException.errorCreatingDynamicJAXBContext(arg0);
        }

    }

}
