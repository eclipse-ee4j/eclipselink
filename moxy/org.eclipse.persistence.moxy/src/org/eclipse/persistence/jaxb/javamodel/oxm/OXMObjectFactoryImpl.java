/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Rick Barkhouse - 2.2 - Initial implementation
package org.eclipse.persistence.jaxb.javamodel.oxm;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.jaxb.javamodel.AnnotationProxy;
import org.eclipse.persistence.jaxb.javamodel.JavaAnnotation;
import org.eclipse.persistence.jaxb.javamodel.JavaClass;
import org.eclipse.persistence.jaxb.javamodel.JavaClassInstanceOf;
import org.eclipse.persistence.jaxb.javamodel.JavaConstructor;
import org.eclipse.persistence.jaxb.javamodel.JavaField;
import org.eclipse.persistence.jaxb.javamodel.JavaMethod;
import org.eclipse.persistence.jaxb.javamodel.JavaModel;
import org.eclipse.persistence.jaxb.javamodel.JavaPackage;
import org.eclipse.persistence.jaxb.javamodel.reflection.JavaAnnotationImpl;
import org.eclipse.persistence.jaxb.xmlmodel.XmlRegistry;
import org.eclipse.persistence.jaxb.xmlmodel.XmlRegistry.XmlElementDecl;

/**
 * INTERNAL:
 * <p>
 * <b>Purpose:</b> Specialized <code>JavaClass</code> implementation wrapping
 * MOXy's <code>xmlmodel.XmlRegistry</code>, used to represent a JAXB
 * <code>ObjectFactory</code>.  Used when bootstrapping a <code>DynamicJAXBContext</code>
 * from XML Bindings.
 * </p>
 *
 * <p>
 * <b>Responsibilities:</b>
 * </p>
 * <ul>
 *    <li>Provide Class information to the <code>JavaModel</code>.</li>
 * </ul>
 *
 * @since EclipseLink 2.2
 *
 * @see org.eclipse.persistence.jaxb.javamodel.JavaClass
 * @see org.eclipse.persistence.jaxb.javamodel.oxm.OXMJAXBElementImpl
 */
public class OXMObjectFactoryImpl implements JavaClass {

    private XmlRegistry registry;
    private JavaModel javaModel;
    private ArrayList<JavaMethod> methods = new ArrayList<JavaMethod>();
    private ArrayList<JavaAnnotation> annotations = new ArrayList<JavaAnnotation>();

    public OXMObjectFactoryImpl(XmlRegistry xmlRegistry) {
        this.registry = xmlRegistry;
    }

    public void init() {
        // Build annotations and methods
        List<XmlElementDecl> decls = this.registry.getXmlElementDecl();
        for (Iterator<XmlElementDecl> iterator = decls.iterator(); iterator.hasNext();) {
            XmlElementDecl xmlElementDecl = iterator.next();

            String paramType = xmlElementDecl.getType();

            JavaClass jaxbElementClass = new OXMJAXBElementImpl(paramType, this.javaModel);
            methods.add(new OXMJavaMethodImpl(xmlElementDecl.getJavaMethod(), jaxbElementClass, this));

            HashMap<String, Object> components = new HashMap<String, Object>();
            components.put(NAME, xmlElementDecl.getName());
            components.put(NAMESPACE, xmlElementDecl.getNamespace());
            components.put(SUBSTITUTION_HEAD_NAME, xmlElementDecl.getSubstitutionHeadName());
            components.put(SUBSTITUTION_HEAD_NAMESPACE, xmlElementDecl.getSubstitutionHeadNamespace());

            Annotation anno = AnnotationProxy.getProxy(components, javax.xml.bind.annotation.XmlElementDecl.class,
                    this.javaModel.getClassLoader(), XMLConversionManager.getDefaultManager());
            annotations.add(new JavaAnnotationImpl(anno));
        }
    }

    @Override
    public Collection<JavaClass> getActualTypeArguments() {
        return new ArrayList<JavaClass>();
    }

    @Override
    public JavaClass getComponentType() {
        return null;
    }

    @Override
    public JavaConstructor getConstructor(JavaClass[] parameterTypes) {
        return new OXMJavaConstructorImpl(this);
    }

    @Override
    public Collection<JavaConstructor> getConstructors() {
        ArrayList<JavaConstructor> constructors = new ArrayList<JavaConstructor>(1);
        constructors.add(new OXMJavaConstructorImpl(this));
        return constructors;
    }

    @Override
    public Collection<JavaClass> getDeclaredClasses() {
        return new ArrayList<JavaClass>();
    }

    @Override
    public JavaConstructor getDeclaredConstructor(JavaClass[] parameterTypes) {
        return new OXMJavaConstructorImpl(this);
    }

    @Override
    public Collection<JavaConstructor> getDeclaredConstructors() {
        ArrayList<JavaConstructor> constructors = new ArrayList<JavaConstructor>(1);
        constructors.add(new OXMJavaConstructorImpl(this));
        return constructors;
    }

    @Override
    public JavaField getDeclaredField(String arg0) {
        return null;
    }

    @Override
    public Collection<JavaField> getDeclaredFields() {
        return null;
    }

    @Override
    public JavaMethod getDeclaredMethod(String arg0, JavaClass[] arg1) {
        return null;
    }

    @Override
    public Collection<JavaMethod> getDeclaredMethods() {
        return methods;
    }

    @Override
    public JavaMethod getMethod(String arg0, JavaClass[] arg1) {
        return null;
    }

    @Override
    public Collection<JavaMethod> getMethods() {
        return methods;
    }

    @Override
    public int getModifiers() {
        return 0;
    }

    @Override
    public String getName() {
        return getQualifiedName();
    }

    @Override
    public JavaPackage getPackage() {
        return new OXMJavaPackageImpl(getPackageName());
    }

    @Override
    public String getPackageName() {
        int lastDotIndex = getQualifiedName().lastIndexOf(DOT);
        if (lastDotIndex == -1) {
            return EMPTY_STRING;
        }

        return getQualifiedName().substring(0, lastDotIndex);
    }

    @Override
    public String getQualifiedName() {
        return this.registry.getName();
    }

    @Override
    public String getRawName() {
        return getQualifiedName();
    }

    @Override
    public JavaClass getSuperclass() {
        return this.javaModel.getClass(JAVA_LANG_OBJECT);
    }

    @Override
    public Type[] getGenericInterfaces() {
        return new Type[0];
    }

    @Override
    public Type getGenericSuperclass() {
        return null;
    }

    @Override
    public boolean hasActualTypeArguments() {
        return false;
    }

    @Override
    public boolean isAbstract() {
        return false;
    }

    @Override
    public boolean isAnnotation() {
        return false;
    }

    @Override
    public boolean isArray() {
        return false;
    }

    @Override
    public boolean isAssignableFrom(JavaClass arg0) {
        return false;
    }

    @Override
    public boolean isEnum() {
        return false;
    }

    @Override
    public boolean isFinal() {
        return false;
    }

    @Override
    public boolean isInterface() {
        return false;
    }

    @Override
    public boolean isMemberClass() {
        return false;
    }

    @Override
    public boolean isPrimitive() {
        return false;
    }

    @Override
    public boolean isPrivate() {
        return false;
    }

    @Override
    public boolean isProtected() {
        return false;
    }

    @Override
    public boolean isPublic() {
        return true;
    }

    @Override
    public boolean isStatic() {
        return false;
    }

    @Override
    public boolean isSynthetic() {
        return false;
    }

    @Override
    public JavaClassInstanceOf instanceOf() {
        return JavaClassInstanceOf.OXM_OBJECT_FACTORY_IMPL;
    }

    @Override
    public JavaAnnotation getAnnotation(JavaClass arg0) {
        return null;
    }

    @Override
    public Collection<JavaAnnotation> getAnnotations() {
        return null;
    }

    @Override
    public JavaAnnotation getDeclaredAnnotation(JavaClass arg0) {
        return null;
    }

    @Override
    public Collection<JavaAnnotation> getDeclaredAnnotations() {
        return null;
    }

    public JavaModel getJavaModel() {
        return javaModel;
    }

    public void setJavaModel(JavaModel javaModel) {
        this.javaModel = javaModel;
    }

    private static String EMPTY_STRING = "";
    private static char DOT = '.';
    private static String JAVA_LANG_OBJECT = "java.lang.Object";
    private static String NAME = "name";
    private static String NAMESPACE = "namespace";
    private static String SUBSTITUTION_HEAD_NAME = "substitutionHeadName";
    private static String SUBSTITUTION_HEAD_NAMESPACE = "substitutionHeadNamespace";

}
