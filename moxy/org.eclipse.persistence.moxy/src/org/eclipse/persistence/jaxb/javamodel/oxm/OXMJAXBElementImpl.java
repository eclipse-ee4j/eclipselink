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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.persistence.jaxb.javamodel.JavaAnnotation;
import org.eclipse.persistence.jaxb.javamodel.JavaClass;
import org.eclipse.persistence.jaxb.javamodel.JavaConstructor;
import org.eclipse.persistence.jaxb.javamodel.JavaField;
import org.eclipse.persistence.jaxb.javamodel.JavaMethod;
import org.eclipse.persistence.jaxb.javamodel.JavaModel;
import org.eclipse.persistence.jaxb.javamodel.JavaPackage;

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
public class OXMJAXBElementImpl implements JavaClass {

    private String parameterType;
    private JavaModel javaModel;

    public OXMJAXBElementImpl(String paramType, JavaModel model) {
        this.parameterType = paramType;
        this.javaModel = model;
    }

    public Collection<JavaClass> getActualTypeArguments() {
        ArrayList<JavaClass> args = new ArrayList<JavaClass>(1);
        args.add(this.javaModel.getClass(this.parameterType));
        return args;
    }

    public JavaClass getComponentType() {
        return null;
    }

    public JavaConstructor getConstructor(JavaClass[] parameterTypes) {
        return null;
    }

    public Collection<JavaConstructor> getConstructors() {
        return null;
    }

    public Collection<JavaClass> getDeclaredClasses() {
        return null;
    }

    public JavaConstructor getDeclaredConstructor(JavaClass[] parameterTypes) {
        return null;
    }

    public Collection<JavaConstructor> getDeclaredConstructors() {
        return null;
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
        return null;
    }

    public JavaMethod getMethod(String arg0, JavaClass[] arg1) {
        return null;
    }

    public Collection<JavaMethod> getMethods() {
        return null;
    }

    public int getModifiers() {
        return 0;
    }

    public String getName() {
        return getQualifiedName();
    }

    public JavaPackage getPackage() {
        return null;
    }

    public String getPackageName() {
        return null;
    }

    public String getQualifiedName() {
        return JAVAX_XML_BIND_JAXBELEMENT;
    }

    public String getRawName() {
        return getQualifiedName();
    }

    public JavaClass getSuperclass() {
        return null;
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
        return false;
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

    private static final String JAVAX_XML_BIND_JAXBELEMENT = "javax.xml.bind.JAXBElement";

}