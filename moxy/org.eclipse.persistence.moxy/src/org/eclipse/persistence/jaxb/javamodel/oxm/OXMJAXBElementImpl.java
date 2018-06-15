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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.persistence.jaxb.javamodel.*;

/**
 * INTERNAL:
 * <p>
 * <b>Purpose:</b> Specialized <code>JavaClass</code> used to represent a
 * <code>JAXBElement</code>.  Used when bootstrapping a <code>DynamicJAXBContext</code>
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
public class OXMJAXBElementImpl implements JavaClass {

    private String parameterType;
    private JavaModel javaModel;

    public OXMJAXBElementImpl(String paramType, JavaModel model) {
        this.parameterType = paramType;
        this.javaModel = model;
    }

    @Override
    public Collection<JavaClass> getActualTypeArguments() {
        ArrayList<JavaClass> args = new ArrayList<JavaClass>(1);
        args.add(this.javaModel.getClass(this.parameterType));
        return args;
    }

    @Override
    public JavaClass getComponentType() {
        return null;
    }

    @Override
    public JavaConstructor getConstructor(JavaClass[] parameterTypes) {
        return null;
    }

    @Override
    public Collection<JavaConstructor> getConstructors() {
        return null;
    }

    @Override
    public Collection<JavaClass> getDeclaredClasses() {
        return null;
    }

    @Override
    public JavaConstructor getDeclaredConstructor(JavaClass[] parameterTypes) {
        return null;
    }

    @Override
    public Collection<JavaConstructor> getDeclaredConstructors() {
        return null;
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
        return null;
    }

    @Override
    public JavaMethod getMethod(String arg0, JavaClass[] arg1) {
        return null;
    }

    @Override
    public Collection<JavaMethod> getMethods() {
        return null;
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
        return null;
    }

    @Override
    public String getPackageName() {
        return null;
    }

    @Override
    public String getQualifiedName() {
        return JAVAX_XML_BIND_JAXBELEMENT;
    }

    @Override
    public String getRawName() {
        return getQualifiedName();
    }

    @Override
    public JavaClass getSuperclass() {
        return null;
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
        return false;
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
        return JavaClassInstanceOf.OXM_JAXB_ELEMENT_IMPL;
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

    private static final String JAVAX_XML_BIND_JAXBELEMENT = "javax.xml.bind.JAXBElement";

}
