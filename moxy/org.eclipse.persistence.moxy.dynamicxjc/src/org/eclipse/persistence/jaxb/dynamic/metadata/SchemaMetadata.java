/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.2 - initial implementation
package org.eclipse.persistence.jaxb.dynamic.metadata;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContextFactory;
import org.eclipse.persistence.jaxb.javamodel.JavaClass;
import org.eclipse.persistence.jaxb.javamodel.JavaModelInput;
import org.eclipse.persistence.jaxb.javamodel.xjc.XJCJavaClassImpl;
import org.eclipse.persistence.jaxb.javamodel.xjc.XJCJavaModelImpl;
import org.eclipse.persistence.jaxb.javamodel.xjc.XJCJavaModelInputImpl;
import org.eclipse.persistence.platform.xml.XMLPlatformException;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.platform.xml.XMLTransformer;
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

public class SchemaMetadata extends Metadata {

    private static final String DEFAULT_SYSTEM_ID = "sysid";

    private SchemaCompiler schemaCompiler;
    private Field JDEFINEDCLASS_ENUMCONSTANTS = null;

    private List<InputSource> externalBindings;

    @SuppressWarnings("unchecked")
    public SchemaMetadata(DynamicClassLoader dynamicClassLoader, Map<String, Object> properties) throws JAXBException {
        super(dynamicClassLoader, properties);
        try {
            JDEFINEDCLASS_ENUMCONSTANTS = PrivilegedAccessHelper.getDeclaredField(JDefinedClass.class, "enumConstantsByName", true);
        } catch (Exception e) {
            throw new JAXBException(org.eclipse.persistence.exceptions.JAXBException.errorCreatingDynamicJAXBContext(e));
        }

        try {
            if (properties != null) {
                Object propValue = properties.get(DynamicJAXBContextFactory.EXTERNAL_BINDINGS_KEY);
                if (propValue != null) {
                    externalBindings = new ArrayList<InputSource>();
                    if (propValue instanceof List<?>) {
                        List<Source> xjbSources = (List<Source>) propValue;
                        for (Source source : xjbSources) {
                            externalBindings.add(createInputSourceFromSource(source));
                        }
                    } else {
                        Source xjbSource = (Source) propValue;
                        InputSource xjbInputSource = createInputSourceFromSource(xjbSource);
                        externalBindings.add(xjbInputSource);
                    }
                }
            }
        } catch (ClassCastException cce) {
            throw new JAXBException(org.eclipse.persistence.exceptions.JAXBException.xjbNotSource());
        }


    }

    public SchemaMetadata(DynamicClassLoader dynamicClassLoader, Map<String, Object> properties, Source metadataSource, EntityResolver resolver) throws JAXBException {
        this(dynamicClassLoader, properties);
        try {
            InputSource schemaInputSource = createInputSourceFromSource(metadataSource);

            if (schemaInputSource.getSystemId() == null) {
                schemaInputSource.setSystemId(DEFAULT_SYSTEM_ID);
            }

            // Use XJC API to parse the schema and generate its JCodeModel
            schemaCompiler = XJC.createSchemaCompiler();
            schemaCompiler.setEntityResolver(resolver);
            schemaCompiler.setErrorListener(new XJCErrorListener());

            if (externalBindings != null) {
                for (InputSource xjbSource : externalBindings) {
                    schemaCompiler.parseSchema(xjbSource);
                }
            }

            schemaCompiler.parseSchema(schemaInputSource);
        } catch (XMLPlatformException xpe) {
            // This will occur when trying to refreshMetadata from a closed stream (non-XML Node metadata)
            if (xpe.getCause() instanceof TransformerException) {
                TransformerException te = (TransformerException) xpe.getCause();
                if (te.getCause() instanceof IOException) {
                    throw org.eclipse.persistence.exceptions.JAXBException.cannotRefreshMetadata();
                }
            }
        } catch (Exception e) {
            throw new JAXBException(org.eclipse.persistence.exceptions.JAXBException.errorCreatingDynamicJAXBContext(e));
        }
    }

    public SchemaMetadata(DynamicClassLoader dynamicClassLoader, Map<String, Object> properties, Node node, EntityResolver resolver) throws JAXBException {
        this(dynamicClassLoader, properties);

        Element element;
        if (node.getNodeType() == Node.DOCUMENT_NODE) {
            element = ((Document) node).getDocumentElement();
        } else if (node.getNodeType() == Node.ELEMENT_NODE) {
            element = (Element) node;
        } else {
            throw new JAXBException(org.eclipse.persistence.exceptions.JAXBException.cannotInitializeFromNode());
        }

        // Use XJC API to parse the schema and generate its JCodeModel
        schemaCompiler = XJC.createSchemaCompiler();
        schemaCompiler.setEntityResolver(resolver);
        schemaCompiler.setErrorListener(new XJCErrorListener());
        schemaCompiler.parseSchema(DEFAULT_SYSTEM_ID, element);
    }

    public JavaModelInput getJavaModelInput() throws JAXBException {
        S2JJAXBModel model = schemaCompiler.bind();

        if (model == null) {
            throw new JAXBException(org.eclipse.persistence.exceptions.JAXBException.xjcBindingError());
        }

        JCodeModel codeModel = model.generateCode(new Plugin[0], null);

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
        XJCJavaModelImpl javaModel = new XJCJavaModelImpl(codeModel, dynamicClassLoader);
        XJCJavaModelInputImpl javaModelInput = new XJCJavaModelInputImpl(jotClasses, javaModel);

        for (JavaClass javaClass : jotClasses) {
            ((XJCJavaClassImpl) javaClass).setJavaModel(javaModel);
            javaModel.getJavaModelClasses().put(javaClass.getQualifiedName(), javaClass);
        }

        return javaModelInput;
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

    private static InputSource createInputSourceFromSource(Source aSource) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        StreamResult result = new StreamResult(baos);
        XMLTransformer t = XMLPlatformFactory.getInstance().getXMLPlatform().newXMLTransformer();
        t.transform(aSource, result);
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        InputSource inputSource = new InputSource(bais);
        inputSource.setSystemId(aSource.getSystemId());

        return inputSource;
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
