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
 *     Rick Barkhouse - 2.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.jaxb.javamodel.oxm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.eclipse.persistence.jaxb.javamodel.JavaAnnotation;
import org.eclipse.persistence.jaxb.javamodel.JavaClass;
import org.eclipse.persistence.jaxb.javamodel.JavaConstructor;
import org.eclipse.persistence.jaxb.javamodel.JavaField;
import org.eclipse.persistence.jaxb.javamodel.JavaMethod;
import org.eclipse.persistence.jaxb.javamodel.JavaPackage;

import org.eclipse.persistence.jaxb.xmlmodel.JavaAttribute;
import org.eclipse.persistence.jaxb.xmlmodel.JavaType;
import org.eclipse.persistence.jaxb.xmlmodel.XmlAttribute;
import org.eclipse.persistence.jaxb.xmlmodel.XmlElement;

public class OXMJavaClassImpl implements JavaClass {

    private JavaType javaType;
    private String javaName;

    public OXMJavaClassImpl(JavaType aJavaType) {
        this.javaType = aJavaType;
    }

    public OXMJavaClassImpl(String aJavaTypeName) {
        this.javaName = aJavaTypeName;
    }

    public Collection<JavaClass> getActualTypeArguments() {
        return new ArrayList<JavaClass>();
    }

    public JavaClass getComponentType() {
        // TODO Auto-generated method stub
        return null;
    }

    public JavaConstructor getConstructor(JavaClass[] parameterTypes) {
        // TODO Auto-generated method stub
        return new OXMJavaConstructorImpl();
    }

    public Collection<JavaConstructor> getConstructors() {
        // TODO Auto-generated method stub
        ArrayList<JavaConstructor> constructors = new ArrayList<JavaConstructor>(1);
        constructors.add(new OXMJavaConstructorImpl());
        return constructors;
    }

    public Collection<JavaClass> getDeclaredClasses() {
        // TODO Auto-generated method stub
        return new ArrayList<JavaClass>();
    }

    public JavaConstructor getDeclaredConstructor(JavaClass[] parameterTypes) {
        // TODO Auto-generated method stub
        return new OXMJavaConstructorImpl();
    }

    public Collection<JavaConstructor> getDeclaredConstructors() {
        // TODO Auto-generated method stub
        return null;
    }

    public JavaField getDeclaredField(String arg0) {
        Collection<JavaField> allFields = getDeclaredFields();

        for (Iterator<JavaField> iterator = allFields.iterator(); iterator.hasNext();) {
            JavaField field = iterator.next();
            if (field.getName().equals(arg0)) {
                return field;
            }
        }

        return null;
    }

    public Collection<JavaField> getDeclaredFields() {
        List<JavaField> fieldsToReturn = new ArrayList<JavaField>();

        List<JAXBElement<? extends JavaAttribute>> fields = this.javaType.getJavaAttributes().getJavaAttribute();

        for (Iterator<JAXBElement<? extends JavaAttribute>> iterator = fields.iterator(); iterator.hasNext();) {
            JAXBElement<? extends JavaAttribute> jaxbElement = iterator.next();
            String fieldName = jaxbElement.getValue().getJavaAttribute();
            fieldsToReturn.add(new OXMJavaFieldImpl(fieldName, this));
        }

        return fieldsToReturn;
    }

    public JavaMethod getDeclaredMethod(String arg0, JavaClass[] arg1) {
        // TODO Auto-generated method stub
        return null;
    }

    public Collection<JavaMethod> getDeclaredMethods() {
        // TODO Auto-generated method stub
        return new ArrayList<JavaMethod>();
    }

    public JavaMethod getMethod(String arg0, JavaClass[] arg1) {
        // TODO Auto-generated method stub
        return null;
    }

    public Collection<JavaMethod> getMethods() {
        // TODO Auto-generated method stub
        return null;
    }

    public int getModifiers() {
        // TODO Auto-generated method stub
        return 0;
    }

    public String getName() {
        if (this.javaType != null) {
            return this.javaType.getName();
        }
        return this.javaName;
    }

    public JavaPackage getPackage() {
        return new OXMJavaPackageImpl(getPackageName());
    }

    public String getPackageName() {
        int lastDotIndex = getQualifiedName().lastIndexOf(".");
        if (lastDotIndex == -1) {
            return "";
        }

        return getQualifiedName().substring(0, lastDotIndex);
    }

    public String getQualifiedName() {
        return getName();
    }

    public String getRawName() {
        return getName();
    }

    public JavaClass getSuperclass() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean hasActualTypeArguments() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isAbstract() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isAnnotation() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isArray() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isAssignableFrom(JavaClass arg0) {
        String thisJavaName = "";
        if (this.javaName != null) {
            thisJavaName = this.javaName;
        } else {
            thisJavaName = this.javaType.getName();
        }

        return thisJavaName.equals(arg0.getName());
    }

    public boolean isEnum() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isFinal() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isInterface() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isMemberClass() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isPrimitive() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isPrivate() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isProtected() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isPublic() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isStatic() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isSynthetic() {
        // TODO Auto-generated method stub
        return false;
    }

    public JavaAnnotation getAnnotation(JavaClass arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public Collection<JavaAnnotation> getAnnotations() {
        // TODO Auto-generated method stub
        return null;
    }

    public JavaAnnotation getDeclaredAnnotation(JavaClass arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public Collection<JavaAnnotation> getDeclaredAnnotations() {
        // TODO Auto-generated method stub
        return null;
    }

}