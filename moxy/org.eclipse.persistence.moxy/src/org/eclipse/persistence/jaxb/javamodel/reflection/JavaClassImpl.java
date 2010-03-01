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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.jaxb.javamodel.reflection;

import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.jaxb.javamodel.JavaAnnotation;
import org.eclipse.persistence.jaxb.javamodel.JavaClass;
import org.eclipse.persistence.jaxb.javamodel.JavaConstructor;
import org.eclipse.persistence.jaxb.javamodel.JavaField;
import org.eclipse.persistence.jaxb.javamodel.JavaMethod;
import org.eclipse.persistence.jaxb.javamodel.JavaPackage;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collection;

/**
 * INTERNAL:
 * <p><b>Purpose:</b>A wrapper class for a JDK Class.  This implementation
 * of the EclipseLink JAXB 2.X Java model simply makes reflective calls on the
 * underlying JDK object.
 *
 * <p><b>Responsibilities:</b>
 * <ul>
 * <li>Provide access to the underlying JDK Class' name, package,
 * method/field names and parameters, annotations, etc.</li>
 * </ul>
 *
 * @since Oracle TopLink 11.1.1.0.0
 * @see org.eclipse.persistence.jaxb.javamodel.JavaClass
 * @see java.lang.Class
 */
public class JavaClassImpl implements JavaClass {

    protected ParameterizedType jType;
    protected Class jClass;
    private JavaModelImpl javaModelImpl;

    public JavaClassImpl(Class javaClass, JavaModelImpl javaModelImpl) {
        this.jClass = javaClass;
        this.javaModelImpl = javaModelImpl;
    }

    public JavaClassImpl(ParameterizedType javaType, Class javaClass, JavaModelImpl javaModelImpl) {
        this.jType = javaType;
        this.jClass = javaClass;
        this.javaModelImpl = javaModelImpl;
    }

    public Collection getActualTypeArguments() {
        ArrayList<JavaClass> argCollection = new ArrayList<JavaClass>();
        if (jType instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) jType;
            Type[] params = pType.getActualTypeArguments();
            for (Type type : params) {
                if (type instanceof ParameterizedType) {
                    ParameterizedType pt = (ParameterizedType) type;
                    argCollection.add(new JavaClassImpl(pt, (Class) pt.getRawType(), javaModelImpl));
                } else if(type instanceof WildcardType){
                    Type[] upperTypes = ((WildcardType)type).getUpperBounds();
                    if(upperTypes.length >0){
                        Type upperType = upperTypes[0];
                        if(upperType instanceof Class){
                            argCollection.add(javaModelImpl.getClass((Class) upperType));
                        }
                    }
                } else if (type instanceof Class) {
                    argCollection.add(javaModelImpl.getClass((Class) type));
                } else if(type instanceof GenericArrayType) {
                    Class genericTypeClass = (Class)((GenericArrayType)type).getGenericComponentType();
                    genericTypeClass = java.lang.reflect.Array.newInstance(genericTypeClass, 0).getClass();
                    argCollection.add(javaModelImpl.getClass(genericTypeClass));
                }
            }
        }
        return argCollection;
    }

    public String toString() {
        return getName();
    }

    /**
     * Assumes JavaType is a JavaClassImpl instance
     */
    public JavaAnnotation getAnnotation(JavaClass arg0) {
        if (arg0 != null) {
            Class annotationClass = ((JavaClassImpl) arg0).getJavaClass();
            if (jClass.isAnnotationPresent(annotationClass)) {
                return new JavaAnnotationImpl(jClass.getAnnotation(annotationClass));
            }
        }
        return null;
    }

    public Collection getAnnotations() {
        ArrayList<JavaAnnotation> annotationCollection = new ArrayList<JavaAnnotation>();
        Annotation[] annotations = jClass.getAnnotations();
        for (Annotation annotation : annotations) {
            annotationCollection.add(new JavaAnnotationImpl(annotation));
        }
        return annotationCollection;
    }

    public Collection getDeclaredClasses() {
        ArrayList<JavaClass> classCollection = new ArrayList<JavaClass>();
        Class[] classes = jClass.getDeclaredClasses();
        for (Class javaClass : classes) {
            classCollection.add(javaModelImpl.getClass(javaClass));
        }
        return classCollection;
    }

    public JavaField getDeclaredField(String arg0) {
        try {
            return new JavaFieldImpl(jClass.getDeclaredField(arg0), javaModelImpl);
        } catch (NoSuchFieldException nsfe) {
            return null;
        }
    }

    public Collection getDeclaredFields() {
        ArrayList<JavaField> fieldCollection = new ArrayList<JavaField>();
        Field[] fields = PrivilegedAccessHelper.getDeclaredFields(jClass);
               
        for (Field field : fields) {        	
            field.setAccessible(true);
            fieldCollection.add(new JavaFieldImpl(field, javaModelImpl));
        }
        return fieldCollection;
    }

    /**
     * Assumes JavaType[] contains JavaClassImpl instances
     */
    public JavaMethod getDeclaredMethod(String arg0, JavaClass[] arg1) {
        if (arg1 == null) {
            arg1 = new JavaClass[0];
        }
        Class[] params = new Class[arg1.length];
        for (int i=0; i<arg1.length; i++) {
            JavaClass jType = arg1[i];
            if (jType != null) {
                params[i] = ((JavaClassImpl) jType).getJavaClass();
            }
        }
        try {
            return new JavaMethodImpl(jClass.getDeclaredMethod(arg0, params), javaModelImpl);
        } catch (NoSuchMethodException nsme) {
            return null;
        }
    }

    public Collection getDeclaredMethods() {
        ArrayList<JavaMethod> methodCollection = new ArrayList<JavaMethod>();
        Method[] methods = jClass.getDeclaredMethods();
        for (Method method : methods) {
            methodCollection.add(new JavaMethodImpl(method, javaModelImpl));
        }
        return methodCollection;
    }

    public JavaConstructor getConstructor(JavaClass[] paramTypes) {
        if (paramTypes == null) {
            paramTypes = new JavaClass[0];
        }
        Class[] params = new Class[paramTypes.length];
        for (int i=0; i<paramTypes.length; i++) {
            JavaClass jType = paramTypes[i];
            if (jType != null) {
                params[i] = ((JavaClassImpl) jType).getJavaClass();
            }
        }
        try {
            Constructor constructor = PrivilegedAccessHelper.getConstructorFor(jClass, params, true);
            return new JavaConstructorImpl(constructor, javaModelImpl);
        } catch (NoSuchMethodException nsme) {
            return null;
        }
    }

    public JavaConstructor getDeclaredConstructor(JavaClass[] paramTypes) {
        if (paramTypes == null) {
            paramTypes = new JavaClass[0];
        }
        Class[] params = new Class[paramTypes.length];
        for (int i=0; i<paramTypes.length; i++) {
            JavaClass jType = paramTypes[i];
            if (jType != null) {
                params[i] = ((JavaClassImpl) jType).getJavaClass();
            }
        }
        try {
            return new JavaConstructorImpl(jClass.getDeclaredConstructor(params), javaModelImpl);
        } catch (NoSuchMethodException nsme) {
            return null;
        }
    }

    public Collection getConstructors() {
        Constructor[] constructors = this.jClass.getConstructors();
        ArrayList<JavaConstructor> constructorCollection = new ArrayList(constructors.length);
        for(Constructor next:constructors) {
            constructorCollection.add(new JavaConstructorImpl(next, javaModelImpl));
        }
        return constructorCollection;
    }

    public Collection getDeclaredConstructors() {
        Constructor[] constructors = this.jClass.getDeclaredConstructors();
        ArrayList<JavaConstructor> constructorCollection = new ArrayList(constructors.length);
        for(Constructor next:constructors) {
            constructorCollection.add(new JavaConstructorImpl(next, javaModelImpl));
        }
        return constructorCollection;
    }

    public JavaField getField(String arg0) {
        try {
            Field field = PrivilegedAccessHelper.getField(jClass, arg0, true);
            return new JavaFieldImpl(field, javaModelImpl);
        } catch (NoSuchFieldException nsfe) {
            return null;
        }
    }

    public Collection getFields() {
        ArrayList<JavaField> fieldCollection = new ArrayList<JavaField>();
        Field[] fields = PrivilegedAccessHelper.getFields(jClass);
        for (Field field : fields) {
            fieldCollection.add(new JavaFieldImpl(field, javaModelImpl));
        }
        return fieldCollection;
    }

    public Class getJavaClass() {
        return jClass;
    }

    /**
     * Assumes JavaType[] contains JavaClassImpl instances
     */
    public JavaMethod getMethod(String arg0, JavaClass[] arg1) {
        if (arg1 == null) {
            arg1 = new JavaClass[0];
        }
        Class[] params = new Class[arg1.length];
        for (int i=0; i<arg1.length; i++) {
            JavaClass jType = arg1[i];
            if (jType != null) {
                params[i] = ((JavaClassImpl) jType).getJavaClass();
            }
        }
        try {
            Method method = PrivilegedAccessHelper.getMethod(jClass, arg0, params, true);
            return new JavaMethodImpl(method, javaModelImpl);
        } catch (NoSuchMethodException nsme) {
            return null;
        }
    }

    public Collection getMethods() {
        ArrayList<JavaMethod> methodCollection = new ArrayList<JavaMethod>();
        Method[] methods = PrivilegedAccessHelper.getMethods(jClass);
        for (Method method : methods) {
            methodCollection.add(new JavaMethodImpl(method, javaModelImpl));
        }
        return methodCollection;
    }

    public String getName() {
        return jClass.getName();
    }

    public JavaPackage getPackage() {
        return new JavaPackageImpl(jClass.getPackage());
    }

    public String getPackageName() {
        if(jClass.getPackage() != null){
            return jClass.getPackage().getName();
        }else{
            String className = jClass.getCanonicalName();
            if(className !=null){
                int index = className.lastIndexOf(".");
                if(index > -1){
                    return className.substring(0, index);
                }
            }
        }
        return null;
    }

    public String getQualifiedName() {
        return jClass.getName();
    }

    public String getRawName() {
        return jClass.getCanonicalName();
    }

    public JavaClass getSuperclass() {
        return javaModelImpl.getClass(jClass.getSuperclass());
    }

    public boolean hasActualTypeArguments() {
        if (jType instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) jType;
            if (pType.getActualTypeArguments() != null && pType.getActualTypeArguments().length > 0) {
                return true;
            }
        }
        return false;
    }

    public JavaClass getOwningClass() {
        return javaModelImpl.getClass(jClass.getEnclosingClass());
    }

    public boolean isAnnotation() {
        return jClass.isAnnotation();
    }

    public boolean isArray() {
        return jClass.isArray();
    }

    /**
     * Assumes JavaType is a JavaClassImpl instance
     */
    public boolean isAssignableFrom(JavaClass arg0) {
        return jClass.isAssignableFrom(((JavaClassImpl) arg0).getJavaClass());
    }

    public boolean isEnum() {
        return jClass.isEnum();
    }

    public boolean isInterface() {
        return jClass.isInterface();
    }

    public boolean isMemberClass() {
        return jClass.isMemberClass();
    }

    public boolean isPrimitive() {
        return jClass.isPrimitive();
    }

    public boolean isAbstract() {
        return Modifier.isAbstract(getModifiers());
    }

    public boolean isPrivate() {
        return Modifier.isPrivate(getModifiers());
    }

    public boolean isProtected() {
        return Modifier.isProtected(getModifiers());
    }

    public boolean isPublic() {
        return Modifier.isPublic(getModifiers());
    }

    public boolean isStatic() {
        return Modifier.isStatic(getModifiers());
    }

    public int getModifiers() {
        return jClass.getModifiers();
    }

    public boolean isFinal() {
        return Modifier.isFinal(getModifiers());
    }

    public boolean isSynthetic() {
        return jClass.isSynthetic();
    }

//---------------- unimplemented methods ----------------//
    public JavaClass getComponentType() {
        if(!isArray()) {
            return null;
        }
        return javaModelImpl.getClass(this.jClass.getComponentType());
    }

    public JavaAnnotation getDeclaredAnnotation(JavaClass arg0) {
        return null;
    }

    public Collection getDeclaredAnnotations() {
        return null;
    }

}