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
 *     Rick Barkhouse - 2.2 - Initial implementation
 ******************************************************************************/
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
 * <ul>
 *    <li>Provide Class information to the <code>JavaModel</code>.</li>
 * </ul>
 * </p>
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
            XmlElementDecl xmlElementDecl = (XmlElementDecl) iterator.next();

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

    public Collection<JavaClass> getActualTypeArguments() {
        return new ArrayList<JavaClass>();
    }

    public JavaClass getComponentType() {
        return null;
    }

    public JavaConstructor getConstructor(JavaClass[] parameterTypes) {
        return new OXMJavaConstructorImpl(this);
    }

    public Collection<JavaConstructor> getConstructors() {
        ArrayList<JavaConstructor> constructors = new ArrayList<JavaConstructor>(1);
        constructors.add(new OXMJavaConstructorImpl(this));
        return constructors;
    }

    public Collection<JavaClass> getDeclaredClasses() {
        return new ArrayList<JavaClass>();
    }

    public JavaConstructor getDeclaredConstructor(JavaClass[] parameterTypes) {
        return new OXMJavaConstructorImpl(this);
    }

    public Collection<JavaConstructor> getDeclaredConstructors() {
        ArrayList<JavaConstructor> constructors = new ArrayList<JavaConstructor>(1);
        constructors.add(new OXMJavaConstructorImpl(this));
        return constructors;
    }

    public JavaField getDeclaredField(String arg0) {
        return null;
    }

    public Collection<JavaField> getDeclaredFields() {
        return null;
    }

    public JavaMethod getDeclaredMethod(String arg0, JavaClass[] arg1) {
        return null;
    }

    public Collection<JavaMethod> getDeclaredMethods() {
        return methods;
    }

    public JavaMethod getMethod(String arg0, JavaClass[] arg1) {
        return null;
    }

    public Collection<JavaMethod> getMethods() {
        return methods;
    }

    public int getModifiers() {
        return 0;
    }

    public String getName() {
        return getQualifiedName();
    }

    public JavaPackage getPackage() {
        return new OXMJavaPackageImpl(getPackageName());
    }

    public String getPackageName() {
        int lastDotIndex = getQualifiedName().lastIndexOf(DOT);
        if (lastDotIndex == -1) {
            return EMPTY_STRING;
        }

        return getQualifiedName().substring(0, lastDotIndex);
    }

    public String getQualifiedName() {
        return this.registry.getName();
    }

    public String getRawName() {
        return getQualifiedName();
    }

    public JavaClass getSuperclass() {
        return this.javaModel.getClass(JAVA_LANG_OBJECT);
    }

    public Type getGenericSuperclass() {
        return null;
    }

    public boolean hasActualTypeArguments() {
        return false;
    }

    public boolean isAbstract() {
        return false;
    }

    public boolean isAnnotation() {
        return false;
    }

    public boolean isArray() {
        return false;
    }

    public boolean isAssignableFrom(JavaClass arg0) {
        return false;
    }

    public boolean isEnum() {
        return false;
    }

    public boolean isFinal() {
        return false;
    }

    public boolean isInterface() {
        return false;
    }

    public boolean isMemberClass() {
        return false;
    }

    public boolean isPrimitive() {
        return false;
    }

    public boolean isPrivate() {
        return false;
    }

    public boolean isProtected() {
        return false;
    }

    public boolean isPublic() {
        return true;
    }

    public boolean isStatic() {
        return false;
    }

    public boolean isSynthetic() {
        return false;
    }

    public JavaAnnotation getAnnotation(JavaClass arg0) {
        return null;
    }

    public Collection<JavaAnnotation> getAnnotations() {
        return null;
    }

    public JavaAnnotation getDeclaredAnnotation(JavaClass arg0) {
        return null;
    }

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
    private static String DOT = ".";
    private static String JAVA_LANG_OBJECT = "java.lang.Object";
    private static String NAME = "name";
    private static String NAMESPACE = "namespace";
    private static String SUBSTITUTION_HEAD_NAME = "substitutionHeadName";
    private static String SUBSTITUTION_HEAD_NAMESPACE = "substitutionHeadNamespace";

}