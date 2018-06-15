/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Martin Vojtek - 2.6 - initial version
package org.eclipse.persistence.jaxb.compiler.builder.helper;

import org.eclipse.persistence.exceptions.JAXBException;
import org.eclipse.persistence.internal.helper.TransformerHelper;
import org.eclipse.persistence.jaxb.javamodel.Helper;
import org.eclipse.persistence.jaxb.javamodel.JavaClass;
import org.eclipse.persistence.jaxb.javamodel.JavaMethod;

/**
 * Provides return type from write transformation method.
 *
 * @author Martin Vojtek
 *
 */
public class TransformerReflectionHelper {

    private final TransformerHelper transformerHelper;
    private final Helper helper;

    public TransformerReflectionHelper(Helper helper) {
        this.transformerHelper = new TransformerHelper();
        this.helper = helper;
    }

    public JavaClass getReturnTypeForWriteTransformationMethodTransformer(JavaClass writerClass) throws JAXBException {
        return getReturnTypeForWriteTransformationMethod(getTransformerHelper().getTransformerMethodName(), writerClass, true);
    }

    public JavaClass getReturnTypeForWriteTransformationMethod(String methodName, JavaClass writerClass) throws JAXBException {
        return getReturnTypeForWriteTransformationMethod(methodName, writerClass, false);
    }

    private JavaClass getReturnTypeForWriteTransformationMethod(String methodName, JavaClass writerClass, boolean isSetTransformerClass) throws JAXBException {
        JavaMethod javaMethod = null;
        for (Class[] methodParameters : getTransformerHelper().getTransformerMethodParameters(isSetTransformerClass)) {
            javaMethod = writerClass.getDeclaredMethod(methodName, helper.getJavaClassArray(methodParameters));
            if (null != javaMethod) {
                return javaMethod.getReturnType();
            }
        }

        throw JAXBException.noSuchWriteTransformationMethod(methodName);
    }

    protected TransformerHelper getTransformerHelper() {
        return transformerHelper;
    }

}
