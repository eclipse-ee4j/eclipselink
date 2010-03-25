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
 *     rbarkhouse - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.jaxb;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.dynamic.DynamicHelper;
import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.dynamic.DynamicTypeBuilder;
import org.eclipse.persistence.exceptions.JAXBException;
import org.eclipse.persistence.jaxb.compiler.Generator;
import org.eclipse.persistence.jaxb.javamodel.JavaClass;
import org.eclipse.persistence.jaxb.javamodel.xjc.XJCJavaClassImpl;
import org.eclipse.persistence.jaxb.javamodel.xjc.XJCJavaModelImpl;
import org.eclipse.persistence.jaxb.javamodel.xjc.XJCJavaModelInputImpl;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.sessions.factories.XMLSessionConfigLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JPackage;
import com.sun.tools.xjc.Plugin;
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
 * @see org.eclipse.persistence.jaxb.DynamicJAXBContextFactory
 * @see org.eclipse.persistence.dynamic.DynamicEntity
 * @see org.eclipse.persistence.dynamic.DynamicType
 *
 * @author rbarkhouse
 * @since EclipseLink 2.1
 */

public class DynamicJAXBContext extends org.eclipse.persistence.jaxb.JAXBContext {

    private ArrayList<DynamicHelper> helpers;

    private static final String SYSTEM_ID = "DynamicJAXBContextFactory:createFromXSD";

    DynamicJAXBContext() {
        this.helpers = new ArrayList();
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

        StringTokenizer st = new StringTokenizer(sessionNames, ":");
        ArrayList dynamicProjects = new ArrayList(st.countTokens());

        XMLSessionConfigLoader loader = new XMLSessionConfigLoader();

        while (st.hasMoreTokens()) {
            DatabaseSession dbSession =
                (DatabaseSession) SessionManager.getManager().getSession(loader, st.nextToken(), classLoader, false, true);
            Project p = DynamicTypeBuilder.loadDynamicProject(dbSession.getProject(), null, dynamicClassLoader);
            dynamicProjects.add(p);
        }

        this.xmlContext = new XMLContext(dynamicProjects);

        List sessions = this.xmlContext.getSessions();
        for (Object session : sessions) {
            this.helpers.add(new DynamicHelper((DatabaseSession) session));
        }
    }

    void initializeFromXSDNode(Node node, ClassLoader classLoader) {
        Element element;

        if (node.getNodeType() == Node.DOCUMENT_NODE) {
            element = ((Document) node).getDocumentElement();
        } else if (node.getNodeType() == Node.ELEMENT_NODE) {
            element = (Element) node;
        } else {
            throw JAXBException.cannotInitializeFromNode();
        }

        // Use XJC API to parse the schema and generate its JCodeModel
        SchemaCompiler sc = XJC.createSchemaCompiler();
        sc.parseSchema(SYSTEM_ID, element);
        S2JJAXBModel model = sc.bind();
        JCodeModel jCodeModel = model.generateCode(new Plugin[0], null);

        initializeFromXJC(jCodeModel, classLoader);
    }

    void initializeFromXSDInputSource(InputSource metadataSource, ClassLoader classLoader) {
        // Use XJC API to parse the schema and generate its JCodeModel
        SchemaCompiler sc = XJC.createSchemaCompiler();
        metadataSource.setSystemId(SYSTEM_ID);
        sc.parseSchema(metadataSource);
        S2JJAXBModel model = sc.bind();
        JCodeModel jCodeModel = model.generateCode(new Plugin[0], null);

        initializeFromXJC(jCodeModel, classLoader);
    }

    void initializeFromXJC(JCodeModel codeModel, ClassLoader classLoader) {
        if (classLoader == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
        }
        DynamicClassLoader dynamicClassLoader;
        if (classLoader instanceof DynamicClassLoader) {
           dynamicClassLoader = (DynamicClassLoader) classLoader;
        } else {
           dynamicClassLoader = new DynamicClassLoader(classLoader);
        }

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

        // Use the JavaModel to setup a Generator to generate an EclipseLink project
        XJCJavaModelImpl javaModel = new XJCJavaModelImpl(Thread.currentThread().getContextClassLoader(), codeModel, dynamicClassLoader);
        XJCJavaModelInputImpl javaModelInput = new XJCJavaModelInputImpl(jotClasses, javaModel);
        Generator g = new Generator(javaModelInput, null, dynamicClassLoader, null);

        Project p = null;
        Project dp = null;
        try {
            p = g.generateProject();
            dp = DynamicTypeBuilder.loadDynamicProject(p, null, dynamicClassLoader);
        } catch (Exception e) {
            throw JAXBException.errorCreatingDynamicJAXBContext(e);
        }

        this.xmlContext = new XMLContext(dp);

        List sessions = this.xmlContext.getSessions();
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

    private JavaClass[] createClassModelFromXJC(ArrayList<JDefinedClass> xjcClasses, JCodeModel jCodeModel, DynamicClassLoader dynamicClassLoader) {
        try {
            JavaClass[] elinkClasses = new JavaClass[xjcClasses.size()];

            int count = 0;
            for (JDefinedClass definedClass : xjcClasses) {
                XJCJavaClassImpl xjcClass = new XJCJavaClassImpl(definedClass, jCodeModel, dynamicClassLoader);
                elinkClasses[count] = xjcClass;
                count++;
            }

            return elinkClasses;
        } catch (Exception e) {
            throw JAXBException.errorCreatingDynamicJAXBContext(e);
        }
    }

}