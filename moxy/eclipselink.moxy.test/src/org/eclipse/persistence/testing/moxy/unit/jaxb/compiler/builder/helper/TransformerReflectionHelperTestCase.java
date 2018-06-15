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
//  - Martin Vojtek - 2.6 - Initial implementation
package org.eclipse.persistence.testing.moxy.unit.jaxb.compiler.builder.helper;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;

import org.eclipse.persistence.exceptions.JAXBException;
import org.eclipse.persistence.internal.helper.TransformerHelper;
import org.eclipse.persistence.jaxb.compiler.builder.helper.TransformerReflectionHelper;
import org.eclipse.persistence.jaxb.javamodel.Helper;
import org.eclipse.persistence.jaxb.javamodel.JavaClass;
import org.eclipse.persistence.jaxb.javamodel.JavaMethod;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests TransformerReflection methods.
 *
 * @author Martin Vojtek
 *
 */
@RunWith(JMockit.class)
public class TransformerReflectionHelperTestCase {

    @Tested TransformerReflectionHelper transformerReflectionHelper;
    @Mocked Helper helper;

    @Before
    public void init() {
        this.transformerReflectionHelper = new TransformerReflectionHelper(helper);
    }

    @Test
    public void getReturnTypeForWriteTransformationMethodTransformer(final @Mocked TransformerHelper transformerHelper, final @Mocked JavaClass writerClass, final @Mocked JavaClass expectedReturnType) {
        final String transformerMethodName = "transformerMethodName";

        new MockUp<TransformerReflectionHelper>() {
            @Mock
            protected TransformerHelper getTransformerHelper() {
                return transformerHelper;
            }

            @Mock
            private JavaClass getReturnTypeForWriteTransformationMethod(String methodName, JavaClass writerClass, boolean isSetTransformerClass) throws JAXBException {
                return expectedReturnType;
            }
        };

        new Expectations() {{
            transformerHelper.getTransformerMethodName(); result = transformerMethodName;
        }};

        JavaClass returnType = Deencapsulation.invoke(transformerReflectionHelper, "getReturnTypeForWriteTransformationMethodTransformer", writerClass);

        assertSame(expectedReturnType, returnType);
    }

    @Test
    public void getReturnTypeForWriteTransformationMethod(final @Mocked JavaClass writerClass, final @Mocked JavaClass expectedReturnType) {

        final String methodName = "methodName";

        new MockUp<TransformerReflectionHelper>() {
            @Mock
            private JavaClass getReturnTypeForWriteTransformationMethod(String methodName, JavaClass writerClass, boolean isSetTransformerClass) throws JAXBException {
                return expectedReturnType;
            }
        };

        JavaClass returnType = Deencapsulation.invoke(transformerReflectionHelper, "getReturnTypeForWriteTransformationMethod", methodName, writerClass);

        assertSame(expectedReturnType, returnType);
    }

    @Test
    public void getReturnTypeForWriteTransformationMethod_isSetTransformerClass(final @Mocked TransformerHelper transformerHelper, final @Mocked JavaClass writerClass, final @Mocked JavaClass expectedReturnType, final @Mocked JavaMethod javaMethod) {

        final String methodName = "methodName";

        final boolean isSetTransformerClass = false;

        new Expectations(TransformerReflectionHelper.class) {{

            List<Class[]> classes = new ArrayList<>();
            Class[] classes1 = new Class[0];
            classes.add(classes1);
            Class[] classes2 = new Class[0];
            classes.add(classes2);

            Deencapsulation.invoke(transformerReflectionHelper, "getTransformerHelper"); result = transformerHelper;
            transformerHelper.getTransformerMethodParameters(isSetTransformerClass); result = classes;
            JavaClass[] javaClasses = new JavaClass[0];
            helper.getJavaClassArray(classes1); result = javaClasses;
            writerClass.getDeclaredMethod(methodName, javaClasses); result = null;

            helper.getJavaClassArray(classes2); result = javaClasses;
            writerClass.getDeclaredMethod(methodName, javaClasses); result = javaMethod;

            javaMethod.getReturnType(); result = expectedReturnType;
        }};

        JavaClass returnType = Deencapsulation.invoke(transformerReflectionHelper, "getReturnTypeForWriteTransformationMethod", methodName, writerClass, isSetTransformerClass);

        assertSame(expectedReturnType, returnType);
    }

    @Test
    public void getReturnTypeForWriteTransformationMethod_throwsJAXBException(final @Mocked TransformerHelper transformerHelper, final @Mocked JavaClass writerClass) {

        final String methodName = "methodName";

        final boolean isSetTransformerClass = false;

        new Expectations(TransformerReflectionHelper.class) {{

            List<Class[]> classes = new ArrayList<>();
            Class[] classes1 = new Class[0];
            classes.add(classes1);
            Class[] classes2 = new Class[0];
            classes.add(classes2);

            Deencapsulation.invoke(transformerReflectionHelper, "getTransformerHelper"); result = transformerHelper;
            transformerHelper.getTransformerMethodParameters(isSetTransformerClass); result = classes;
            JavaClass[] javaClasses = new JavaClass[0];
            helper.getJavaClassArray(classes1); result = javaClasses;
            writerClass.getDeclaredMethod(methodName, javaClasses); result = null;

            helper.getJavaClassArray(classes2); result = javaClasses;
            writerClass.getDeclaredMethod(methodName, javaClasses); result = null;

        }};

        try {
            Deencapsulation.invoke(transformerReflectionHelper, "getReturnTypeForWriteTransformationMethod", methodName, writerClass, isSetTransformerClass);
            fail("Expected JAXBException.");
        } catch (JAXBException expected) {

        }
    }
}
