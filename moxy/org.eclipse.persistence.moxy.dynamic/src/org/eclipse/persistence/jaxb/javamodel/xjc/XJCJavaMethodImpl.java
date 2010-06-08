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
 *     Rick Barkhouse = 2.1 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.jaxb.javamodel.xjc;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.exceptions.JAXBException;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.jaxb.javamodel.JavaAnnotation;
import org.eclipse.persistence.jaxb.javamodel.JavaClass;
import org.eclipse.persistence.jaxb.javamodel.JavaMethod;

import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JType;

public class XJCJavaMethodImpl implements JavaMethod {

    private JMethod xjcMethod;
    private JCodeModel jCodeModel;
    private DynamicClassLoader dynamicClassLoader;

    private static Field JMETHOD_ANNOTATIONS = null;
    private static Field JMETHOD_OUTER = null;
    static {
        try {
            JMETHOD_ANNOTATIONS = PrivilegedAccessHelper.getDeclaredField(JMethod.class, "annotations", true);
            JMETHOD_OUTER = PrivilegedAccessHelper.getDeclaredField(JMethod.class, "outer", true);
        } catch (Exception e) {
            throw JAXBException.errorCreatingDynamicJAXBContext(e);
        }
    }

    public XJCJavaMethodImpl(JMethod javaMethod, JCodeModel codeModel, DynamicClassLoader loader) {
        this.xjcMethod = javaMethod;
        this.jCodeModel = codeModel;
        this.dynamicClassLoader = loader;
    }

    @SuppressWarnings("unchecked")
    public JavaAnnotation getAnnotation(JavaClass aClass) {
        if (aClass != null) {
            Collection<JAnnotationUse> annotations = null;

            try {
                annotations = (Collection<JAnnotationUse>) PrivilegedAccessHelper.getValueFromField(JMETHOD_ANNOTATIONS, xjcMethod);
            } catch (Exception e) {
            }

            if (annotations == null) {
                return null;
            }

            for (JAnnotationUse annotationUse : annotations) {
                XJCJavaAnnotationImpl xjcAnnotation = new XJCJavaAnnotationImpl(annotationUse, dynamicClassLoader);
                if (xjcAnnotation.getJavaAnnotationClass().getCanonicalName().equals(aClass.getQualifiedName())) {
                    return xjcAnnotation;
                }
            }
            // Didn't find annotation so return null
            return null;
        }
        // aClass was null so return null
        return null;
    }

    @SuppressWarnings("unchecked")
    public Collection<JavaAnnotation> getAnnotations() {
        ArrayList<JavaAnnotation> annotationsList = new ArrayList<JavaAnnotation>();

        Collection<JAnnotationUse> annotations = null;

        try {
            annotations = (Collection<JAnnotationUse>) PrivilegedAccessHelper.getValueFromField(JMETHOD_ANNOTATIONS, xjcMethod);
        } catch (Exception e) {
        }

        if (annotations == null) {
            return annotationsList;
        }

        for (JAnnotationUse annotationUse : annotations) {
            XJCJavaAnnotationImpl xjcAnnotation = new XJCJavaAnnotationImpl(annotationUse, dynamicClassLoader);
            annotationsList.add(xjcAnnotation);
        }
        return annotationsList;
    }

    public String getName() {
        return xjcMethod.name();
    }

    public JavaClass[] getParameterTypes() {
        JType[] params = xjcMethod.listParamTypes();
        JavaClass[] paramArray = new JavaClass[params.length];

        for (int i = 0; i < params.length; i++) {
            paramArray[i] = new XJCJavaClassImpl((JDefinedClass) params[i], jCodeModel, dynamicClassLoader);
        }
        return paramArray;
    }

    public JavaClass getResolvedType() {
        try {
            return new XJCJavaClassImpl(jCodeModel._class(xjcMethod.type().fullName()), jCodeModel, dynamicClassLoader);
        } catch (JClassAlreadyExistsException ex) {
            return new XJCJavaClassImpl(jCodeModel._getClass(xjcMethod.type().fullName()), jCodeModel, dynamicClassLoader);
        }
    }

    @SuppressWarnings("unchecked")
    public JavaClass getReturnType() {
        JType type = xjcMethod.type();

        try {
            Field argsField = PrivilegedAccessHelper.getDeclaredField(type.getClass(), "args", true);
            List<JClass> args = (List<JClass>) PrivilegedAccessHelper.getValueFromField(argsField, type);
            type = args.get(0);
        } catch (Exception e) {
        }

        try {
            return new XJCJavaClassImpl(jCodeModel._class(type.fullName()), jCodeModel, dynamicClassLoader);
        } catch (JClassAlreadyExistsException ex) {
            return new XJCJavaClassImpl(jCodeModel._getClass(type.fullName()), jCodeModel, dynamicClassLoader);
        }
    }

    public boolean hasActualTypeArguments() {
        try {
            JavaClass[] allParams = getParameterTypes();

            for (JavaClass type : allParams) {
                Class<?> paramClass = Class.forName(type.getPackageName() + "." + type.getName());
                if (paramClass.newInstance() instanceof ParameterizedType) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public Collection<Object> getActualTypeArguments() {
        throw new UnsupportedOperationException("getActualTypeArguments");
    }

    public int getModifiers() {
        return xjcMethod.mods().getValue();
    }

    public JavaClass getOwningClass() {
        JDefinedClass ownerXJCClass = null;

        try {
            ownerXJCClass = (JDefinedClass) PrivilegedAccessHelper.getValueFromField(JMETHOD_OUTER, xjcMethod);
        } catch (Exception e) {
            return null;
        }

        return new XJCJavaClassImpl(ownerXJCClass, jCodeModel, dynamicClassLoader);
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

    public boolean isFinal() {
        return Modifier.isFinal(getModifiers());
    }

    public boolean isSynthetic() {
        throw new UnsupportedOperationException("isSynthetic");
    }

    public JavaAnnotation getDeclaredAnnotation(JavaClass arg0) {
        throw new UnsupportedOperationException("getDeclaredAnnotation");
    }

    public Collection<JavaAnnotation> getDeclaredAnnotations() {
        throw new UnsupportedOperationException("getDeclaredAnnotations");
    }

}