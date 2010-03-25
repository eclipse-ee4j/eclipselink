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

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.exceptions.JAXBException;
import org.eclipse.persistence.jaxb.javamodel.JavaAnnotation;
import org.eclipse.persistence.jaxb.javamodel.JavaClass;
import org.eclipse.persistence.jaxb.javamodel.JavaPackage;

import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JPackage;

public class XJCJavaPackageImpl implements JavaPackage {

    protected JPackage xjcPackage;
    private DynamicClassLoader dynamicClassLoader;

    public XJCJavaPackageImpl(JPackage jPackage, DynamicClassLoader loader) {
        this.xjcPackage = jPackage;
        this.dynamicClassLoader = loader;
    }

    public JavaAnnotation getAnnotation(JavaClass aClass) {
        if (aClass != null) {

            Collection<JAnnotationUse> annotations = null;
            try {
                annotations = (Collection<JAnnotationUse>) XJCJavaModelHelper.getFieldValueByReflection(xjcPackage, "annotations");
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

    public Collection getAnnotations() {
        ArrayList<JavaAnnotation> annotationsList = new ArrayList<JavaAnnotation>();

        Collection<JAnnotationUse> annotations = null;
        try {
            annotations = (Collection<JAnnotationUse>) XJCJavaModelHelper.getFieldValueByReflection(xjcPackage, "annotations");
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
        if (xjcPackage != null){
            return xjcPackage.name();
        }
        return null;
    }

    public String getQualifiedName() {
        return getName();
    }

    public JavaAnnotation getDeclaredAnnotation(JavaClass arg0) {
        throw new UnsupportedOperationException("getDeclaredAnnotation");
    }

    public Collection getDeclaredAnnotations() {
        throw new UnsupportedOperationException("getDeclaredAnnotations");
    }

}