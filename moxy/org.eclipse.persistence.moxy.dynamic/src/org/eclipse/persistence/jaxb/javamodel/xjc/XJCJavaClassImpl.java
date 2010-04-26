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
 *     rbarkhouse - 2009-12-18 13:04:58 - EclipseLink 2.1 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.jaxb.javamodel.xjc;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.exceptions.JAXBException;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.jaxb.javamodel.JavaAnnotation;
import org.eclipse.persistence.jaxb.javamodel.JavaClass;
import org.eclipse.persistence.jaxb.javamodel.JavaConstructor;
import org.eclipse.persistence.jaxb.javamodel.JavaField;
import org.eclipse.persistence.jaxb.javamodel.JavaMethod;
import org.eclipse.persistence.jaxb.javamodel.JavaPackage;

import com.sun.codemodel.ClassType;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMods;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JPrimitiveType;
import com.sun.codemodel.JType;
import com.sun.codemodel.JTypeVar;

public class XJCJavaClassImpl implements JavaClass {

    private JDefinedClass xjcClass;
    private JClass xjcRefClass;
    private JCodeModel jCodeModel;
    private DynamicClassLoader dynamicClassLoader;

    private static Field JDEFINEDCLASS_ANNOTATIONS = null;
    private static Field JDEFINEDCLASS_MODS = null;
    private static Field JDEFINEDCLASS_SUPERCLASS = null;
    private static Field JTYPEVAR_BOUND = null;
    private static final Map<String, JPrimitiveType> jPrimitiveTypes = new HashMap<String, JPrimitiveType>();
    static {
        JCodeModel tempCodeModel = new JCodeModel();
        jPrimitiveTypes.put("java.lang.Boolean", tempCodeModel.BOOLEAN);
        jPrimitiveTypes.put("java.lang.Byte", tempCodeModel.BYTE);
        jPrimitiveTypes.put("java.lang.Character", tempCodeModel.CHAR);
        jPrimitiveTypes.put("java.lang.Double", tempCodeModel.DOUBLE);
        jPrimitiveTypes.put("java.lang.Float", tempCodeModel.FLOAT);
        jPrimitiveTypes.put("java.lang.Integer", tempCodeModel.INT);
        jPrimitiveTypes.put("java.lang.Long", tempCodeModel.LONG);
        jPrimitiveTypes.put("java.lang.Short", tempCodeModel.SHORT);

        try {
            JDEFINEDCLASS_ANNOTATIONS = PrivilegedAccessHelper.getDeclaredField(JDefinedClass.class, "annotations", true);
            JDEFINEDCLASS_MODS = PrivilegedAccessHelper.getDeclaredField(JDefinedClass.class, "mods", true);
            JDEFINEDCLASS_SUPERCLASS = PrivilegedAccessHelper.getDeclaredField(JDefinedClass.class, "superClass", true);
            JTYPEVAR_BOUND = PrivilegedAccessHelper.getDeclaredField(JTypeVar.class, "bound", true);
        } catch (Exception e) {
            throw JAXBException.errorCreatingDynamicJAXBContext(e);
        }
    }

    public XJCJavaClassImpl(JClass jRefClass, JCodeModel codeModel, DynamicClassLoader loader) {
        this.xjcClass = null;
        this.xjcRefClass = jRefClass;
        this.jCodeModel = codeModel;
        this.dynamicClassLoader = loader;
    }

    public XJCJavaClassImpl(JDefinedClass jDefinedClass, JCodeModel codeModel, DynamicClassLoader loader) {
        this.xjcClass = jDefinedClass;
        this.xjcRefClass = null;
        this.jCodeModel = codeModel;
        this.dynamicClassLoader = loader;
    }

    // ========================================================================

    public Collection<JavaClass> getActualTypeArguments() {
        JTypeVar[] typeParams = null;

        if (xjcRefClass != null) {
            typeParams = xjcRefClass.typeParams();
        } else {
            typeParams = xjcClass.typeParams();
        }

        ArrayList<JavaClass> typeArguments = new ArrayList<JavaClass>(typeParams.length);

        for (int i = 0; i < typeParams.length; i++) {
            JTypeVar var = typeParams[i];

            JClass xjcBoundClass = null;

            try {
                xjcBoundClass = (JClass) PrivilegedAccessHelper.getValueFromField(JTYPEVAR_BOUND, var);
            } catch (Exception e) {
                return null;
            }

            XJCJavaClassImpl boundClass = new XJCJavaClassImpl(xjcBoundClass, jCodeModel, dynamicClassLoader);
            typeArguments.add(boundClass);
        }
        return typeArguments;
    }

    public JavaClass getComponentType() {
        if (!isArray()) {
            return null;
        }
        throw new UnsupportedOperationException("getComponentType");
    }

    public JavaConstructor getConstructor(JavaClass[] parameterTypes) {
        JType[] xjcParameterTypes = new JType[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            JavaClass pType = parameterTypes[i];
            String className = pType.getQualifiedName();
            JType xjcType = null;

            if (pType.isPrimitive()) {
                xjcType = jPrimitiveTypes.get(className);
            } else {
                xjcType = jCodeModel._getClass(className);
            }
            xjcParameterTypes[i] = xjcType;
        }

        JMethod constructor = xjcClass.getConstructor(xjcParameterTypes);

        return new XJCJavaConstructorImpl(constructor, jCodeModel, dynamicClassLoader);
    }

    @SuppressWarnings("unchecked")
    public Collection<JavaConstructor> getConstructors() {
        ArrayList<JavaConstructor> constructors = new ArrayList<JavaConstructor>();
        Iterator<JMethod> it = xjcClass.constructors();

        while (it.hasNext()) {
            constructors.add(new XJCJavaConstructorImpl(it.next(), jCodeModel, dynamicClassLoader));
        }

        return constructors;
    }

    public Collection<JavaClass> getDeclaredClasses() {
        ArrayList<JavaClass> declaredClasses = new ArrayList<JavaClass>();

        if (xjcRefClass != null) {
            return declaredClasses;
        }

        Iterator<JDefinedClass> it  = xjcClass.classes();

        while (it.hasNext()) {
            declaredClasses.add(new XJCJavaClassImpl(it.next(), jCodeModel, dynamicClassLoader));
        }

        return declaredClasses;
    }

    public JavaConstructor getDeclaredConstructor(JavaClass[] parameterTypes) {
        return getConstructor(parameterTypes);
    }

    public Collection<JavaConstructor> getDeclaredConstructors() {
        return getConstructors();
    }

    public JavaField getDeclaredField(String fieldName) {
        JFieldVar xjcField = xjcClass.fields().get(fieldName);

        return new XJCJavaFieldImpl(xjcField, jCodeModel, dynamicClassLoader);
    }

    public Collection<JavaField> getDeclaredFields() {
        Collection<JFieldVar> xjcFields = xjcClass.fields().values();
        ArrayList<JavaField> fields = new ArrayList<JavaField>(xjcFields.size());

        for (JFieldVar jField : xjcFields) {
            fields.add(new XJCJavaFieldImpl(jField, jCodeModel, dynamicClassLoader));
        }

        return fields;
    }

    public JavaMethod getDeclaredMethod(String name, JavaClass[] args) {
        return getMethod(name, args);
    }

    public Collection<JavaMethod> getDeclaredMethods() {
        return getMethods();
    }

    public JavaMethod getMethod(String name, JavaClass[] args) {
        Collection<JMethod> xjcMethods = xjcClass.methods();

        for (JMethod xjcMethod : xjcMethods) {
            JType[] params = xjcMethod.listParamTypes();
            boolean argsAreEqual = argsAreEqual(args, params);

            if (xjcMethod.name().equals(name) && argsAreEqual) {
                return new XJCJavaMethodImpl(xjcMethod, jCodeModel, dynamicClassLoader);
            }
        }
        return null;
    }

    private boolean argsAreEqual(JavaClass[] elinkArgs, JType[] xjcArgs) {
        if (elinkArgs == null && xjcArgs == null) {
            return true;
        }
        if (elinkArgs != null && xjcArgs == null) {
            return false;
        }
        if (elinkArgs == null && xjcArgs != null) {
            return false;
        }
        if (elinkArgs.length != xjcArgs.length) {
            return false;
        }
        for (int i = 0; i < elinkArgs.length; i++) {
            JavaClass elinkClass = elinkArgs[i];
            JType xjcClass = xjcArgs[i];
            if (!elinkClass.getName().equals(xjcClass.name())) {
                return false;
            }
        }
        return true;
    }

    public Collection<JavaMethod> getMethods() {
        Collection<JMethod> xjcMethods = xjcClass.methods();
        ArrayList<JavaMethod> elinkMethods = new ArrayList<JavaMethod>(xjcMethods.size());

        for (JMethod xjcMethod : xjcMethods) {
            elinkMethods.add(new XJCJavaMethodImpl(xjcMethod, jCodeModel, dynamicClassLoader));
        }

        return elinkMethods;
    }

    public int getModifiers() {
        JMods xjcMods = null;

        try {
            xjcMods = (JMods) PrivilegedAccessHelper.getValueFromField(JDEFINEDCLASS_MODS, xjcClass);
        } catch (Exception e) {
            return 0;
        }

        return xjcMods.getValue();
    }

    public String getName() {
        return getQualifiedName();
    }

    public JavaPackage getPackage() {
        if (xjcRefClass != null) {
            JPackage pkg = null;
            try {
                // Cannot cache this field because JReferencedClass is a protected class.
                Field _packageField = PrivilegedAccessHelper.getDeclaredField(xjcRefClass.getClass(), "_package", true);
                pkg = (JPackage) PrivilegedAccessHelper.getValueFromField(_packageField, xjcRefClass);
            } catch (Exception e) {
                return null;
            }
            return new XJCJavaPackageImpl(pkg, dynamicClassLoader);
        }
        return new XJCJavaPackageImpl(xjcClass.getPackage(), dynamicClassLoader);
    }

    public String getPackageName() {
        if (xjcRefClass != null) {
            JPackage pkg = null;
            try {
                // Cannot cache this method because JReferencedClass is a protected class.
                Method _packageMethod = PrivilegedAccessHelper.getDeclaredMethod(xjcRefClass.getClass(), "_package", new Class[] {});
                pkg = (JPackage) PrivilegedAccessHelper.invokeMethod(_packageMethod, xjcRefClass);
            } catch (Exception e) {
                return null;
            }
            return pkg.name();
        }
        return xjcClass._package().name();
    }

    public String getQualifiedName() {
        if (xjcRefClass != null) {
            String name = null;
            try {
                // binaryName includes package name
                // Cannot cache this method because JReferencedClass is a protected class.
                Method binaryNameMethod = PrivilegedAccessHelper.getDeclaredMethod(xjcRefClass.getClass(), "binaryName", new Class[] {});
                name = (String) PrivilegedAccessHelper.invokeMethod(binaryNameMethod, xjcRefClass);
            } catch (Exception e) {
                return null;
            }
            return name;
        }
        return getPackageName() + "." + xjcClass.name();
    }

    public String getRawName() {
        return getQualifiedName();
    }

    public JavaClass getSuperclass() {
        if (xjcRefClass != null) {
            return null;
        }

        try {
            JClass superClass = (JClass) PrivilegedAccessHelper.getValueFromField(JDEFINEDCLASS_SUPERCLASS, xjcClass);

            if (superClass instanceof JDefinedClass) {
                return new XJCJavaClassImpl((JDefinedClass) superClass, jCodeModel, dynamicClassLoader);
            } else {
                return new XJCJavaClassImpl(superClass, jCodeModel, dynamicClassLoader);
            }

        } catch (Exception e) {
            return null;
        }
    }

    public boolean hasActualTypeArguments() {
        if (xjcRefClass != null) {
            return xjcRefClass.typeParams().length > 0;
        }
        return xjcClass.typeParams().length > 0;
    }

    public boolean isAbstract() {
        return xjcClass.isAbstract();
    }

    public boolean isAnnotation() {
        if (xjcRefClass != null) {
            return false;
        }
        return xjcClass.isAnnotationTypeDeclaration();
    }

    public boolean isArray() {
        if (xjcRefClass != null) {
            boolean isArray = false;
            try {
                // Cannot cache this field because JReferencedClass is a protected class.
                Field isArrayField = PrivilegedAccessHelper.getDeclaredField(xjcRefClass.getClass(), "isArray", true);
                isArray = (Boolean) PrivilegedAccessHelper.getValueFromField(isArrayField, xjcRefClass);
            } catch (Exception e) {
                return false;
            }
            return isArray;
        }
        return this.xjcClass.isArray();
    }

    public boolean isAssignableFrom(JavaClass javaClass) {
        if (javaClass == null) {
            return false;
        }
        JClass someClass = null;

        XJCJavaClassImpl javaClassImpl = (XJCJavaClassImpl) javaClass;
        if (javaClassImpl.isXJCRefClass()) {
            someClass = javaClassImpl.xjcRefClass;
        } else {
            someClass = javaClassImpl.xjcClass;
        }

        return xjcClass.isAssignableFrom(someClass);
    }

    public boolean isEnum() {
        return xjcClass.getClassType().equals(ClassType.ENUM);
    }

    public boolean isFinal() {
        return Modifier.isFinal(getModifiers());
    }

    public boolean isInterface() {
        if (xjcRefClass != null) {
            boolean isInterface = false;
            try {
                // Cannot cache this field because JReferencedClass is a protected class.
                Field isInterfaceField = PrivilegedAccessHelper.getDeclaredField(xjcRefClass.getClass(), "isInterface", true);
                isInterface = (Boolean) PrivilegedAccessHelper.getValueFromField(isInterfaceField, xjcRefClass);
            } catch (Exception e) {
                return false;
            }
            return isInterface;
        }
        return xjcClass.isInterface();
    }

    public boolean isMemberClass() {
        throw new UnsupportedOperationException("isMemberClass");
    }

    public boolean isPrimitive() {
        if (xjcRefClass != null) {
            Object primitiveType = null;
            try {
                // Cannot cache this method because JReferencedClass is a protected class.
                Method getPrimitiveTypeMethod = PrivilegedAccessHelper.getDeclaredMethod(xjcRefClass.getClass(), "getPrimitiveType", new Class[] {});
                primitiveType = PrivilegedAccessHelper.invokeMethod(getPrimitiveTypeMethod, xjcRefClass);
            } catch (Exception e) {
                return false;
            }
            return (primitiveType != null);
        }
        return xjcClass.isPrimitive();
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

    public boolean isSynthetic() {
        throw new UnsupportedOperationException("isSynthetic");
    }

    @SuppressWarnings("unchecked")
    public JavaAnnotation getAnnotation(JavaClass aClass) {
        if (xjcRefClass != null) {
            return null;
        }

        if (aClass != null) {

            Collection<JAnnotationUse> annotations = null;
            try {
                annotations = (Collection<JAnnotationUse>) PrivilegedAccessHelper.getValueFromField(JDEFINEDCLASS_ANNOTATIONS, xjcClass);
            } catch (Exception e) {
            }

            if (annotations == null) {
                return null;
            }



            for (JAnnotationUse annotationUse : annotations) {
                XJCJavaAnnotationImpl xjcAnnotation = new XJCJavaAnnotationImpl(annotationUse, dynamicClassLoader);

                String myAnnotationClass = xjcAnnotation.getJavaAnnotationClass().getCanonicalName();
                String annotationClass = aClass.getQualifiedName();

                if (myAnnotationClass.equals(annotationClass)) {
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
            annotations = (Collection<JAnnotationUse>) PrivilegedAccessHelper.getValueFromField(JDEFINEDCLASS_ANNOTATIONS, xjcClass);
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

    public JavaAnnotation getDeclaredAnnotation(JavaClass arg0) {
        throw new UnsupportedOperationException("getDeclaredAnnotation");
    }

    public Collection<JavaAnnotation> getDeclaredAnnotations() {
        throw new UnsupportedOperationException("getDeclaredAnnotations");
    }

    boolean isXJCRefClass() {
        return xjcRefClass != null;
    }

}