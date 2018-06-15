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
package org.eclipse.persistence.testing.moxy.unit.jaxb.compiler.builder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Invocation;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;

import org.eclipse.persistence.exceptions.JAXBException;
import org.eclipse.persistence.jaxb.compiler.Property;
import org.eclipse.persistence.jaxb.compiler.TypeInfo;
import org.eclipse.persistence.jaxb.compiler.XMLProcessor;
import org.eclipse.persistence.jaxb.compiler.builder.TransformerPropertyBuilder;
import org.eclipse.persistence.jaxb.compiler.builder.helper.TransformerReflectionHelper;
import org.eclipse.persistence.jaxb.javamodel.Helper;
import org.eclipse.persistence.jaxb.javamodel.JavaClass;
import org.eclipse.persistence.jaxb.xmlmodel.XmlTransformation;
import org.eclipse.persistence.jaxb.xmlmodel.XmlTransformation.XmlWriteTransformer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests TransformerPropertyBuilder methods.
 *
 * @author Martin Vojtek
 *
 */
@RunWith(JMockit.class)
public class TransformerPropertyBuilderTestCase {

    @Mocked Property property;
    @Mocked TypeInfo typeInfo;
    @Mocked Helper helper;
    String attributeToken = "testAttributeToken";
    @Tested TransformerPropertyBuilder builder;

    @Before
    public void init() {
        builder = new TransformerPropertyBuilder(property, typeInfo, helper, attributeToken);
    }

    @Test
    public void buildProperties(final @Mocked XmlTransformation xmlTransformation, final @Mocked XmlWriteTransformer transformer1, final @Mocked XmlWriteTransformer transformer2) {

        final Property property1 = new Property();
        final Property property2 = new Property();

        new MockUp<TransformerPropertyBuilder>() {
            @Mock
            private String getPropertyName(Invocation invocation, Property property, XmlWriteTransformer writeTransformer) {
                return property.getPropertyName() + invocation.getInvocationCount();
            }

            @Mock
            private Property buildPropertyFromTransformer(Invocation invocation, String pname, TypeInfo typeInfo, XmlWriteTransformer writeTransformer) {
                return invocation.getInvocationCount() == 1 ? property1 : property2;
            }
        };

        new Expectations() {{
            property.getXmlTransformation(); result = xmlTransformation;
            List<XmlWriteTransformer> transformers = new ArrayList<>();
            transformers.add(transformer1);
            transformers.add(transformer2);
            xmlTransformation.getXmlWriteTransformer(); result = transformers;
        }};

        List<Property> properties = builder.buildProperties();

        assertEquals(2, properties.size());
        assertSame(property1, properties.get(0));
        assertSame(property2, properties.get(1));
    }

    @Test
    public void getPropertyName(final @Mocked XmlWriteTransformer transformer, final @Mocked XMLProcessor xmlProcessor) {

        final String expectedResult = "expectedResult";

        new Expectations(TransformerPropertyBuilder.class) {{
            String xmlPath = "xmlPath";
            transformer.getXmlPath(); result = xmlPath;
            XMLProcessor.getNameFromXPath(xmlPath, property.getPropertyName(), false); result = expectedResult;
        }};

        String propertyName = Deencapsulation.invoke(builder, "getPropertyName", property, transformer);

        assertSame(expectedResult, propertyName);
    }

    @Test
    public void buildPropertyFromTransformer(final @Mocked TypeInfo typeInfo, final @Mocked XmlWriteTransformer transformer, final @Mocked JavaClass type, final @Mocked Property propety) {

        final String xmlPath = "xmlPath";
        final String pname = "customPname";

        new MockUp<TransformerPropertyBuilder>() {
            @Mock
            private JavaClass getReturnTypeFromTransformer(TypeInfo typeInfo, XmlWriteTransformer writeTransformer) {
                return type;
            }

        };

        new Expectations() {{
            transformer.getXmlPath(); result = xmlPath;
        }};

        final Property resultProperty = Deencapsulation.invoke(builder, "buildPropertyFromTransformer", pname, typeInfo, transformer);

        new Verifications() {{
            resultProperty.setPropertyName(pname);
            resultProperty.setXmlPath(xmlPath);
            resultProperty.setSchemaName(new QName(pname));
            resultProperty.setType(type);
        }};
    }

    @Test
    public void getReturnTypeFromTransformer_isSetTransformerClass(final @Mocked TypeInfo typeInfo, final @Mocked XmlWriteTransformer transformer, final @Mocked JavaClass javaClass, final @Mocked TransformerReflectionHelper transformerReflectionHelper, final @Mocked JavaClass resultJavaClass) {
        new MockUp<TransformerPropertyBuilder>() {
            @Mock
            private JavaClass getTransformerJavaClass(TypeInfo typeInfo, XmlWriteTransformer writeTransformer) {
                return javaClass;
            }

            @Mock
            private TransformerReflectionHelper getTransformerReflectionHelper() {
                return transformerReflectionHelper;
            }
        };

        new Expectations() {{
            transformer.isSetTransformerClass(); result = true;
            transformerReflectionHelper.getReturnTypeForWriteTransformationMethodTransformer(javaClass); result = resultJavaClass;
        }};

        final JavaClass returnType = Deencapsulation.invoke(builder, "getReturnTypeFromTransformer", typeInfo, transformer);

        assertSame(resultJavaClass, returnType);
    }

    @Test
    public void getReturnTypeFromTransformer_isNotSetTransformerClass(final @Mocked TypeInfo typeInfo, final @Mocked XmlWriteTransformer transformer, final @Mocked JavaClass javaClass, final @Mocked TransformerReflectionHelper transformerReflectionHelper, final @Mocked JavaClass resultJavaClass) {

        new Expectations(TransformerPropertyBuilder.class) {{
            Deencapsulation.invoke(builder, "getTransformerJavaClass", typeInfo, transformer); result = javaClass;
            transformer.isSetTransformerClass(); result = false;

            Deencapsulation.invoke(builder, "getTransformerReflectionHelper"); result = transformerReflectionHelper;

            String transformerMethod = "transformerMethod";
            transformer.getMethod(); result = transformerMethod;

            transformerReflectionHelper.getReturnTypeForWriteTransformationMethod(transformerMethod, javaClass); result = resultJavaClass;
        }};

        final JavaClass returnType = Deencapsulation.invoke(builder, "getReturnTypeFromTransformer", typeInfo, transformer);

        assertSame(resultJavaClass, returnType);
    }

    @Test
    public void getTransformerJavaClass_isSetTransformerClass(final @Mocked TypeInfo typeInfo, final @Mocked XmlWriteTransformer transformer, final @Mocked JavaClass resultJavaClass) {
        new Expectations(TransformerPropertyBuilder.class) {{
            transformer.isSetTransformerClass(); result = true;
            String transformerClass = "transformerClass";
            transformer.getTransformerClass(); result = transformerClass;
            helper.getJavaClass(transformerClass); result = resultJavaClass;
        }};

        final JavaClass returnType = Deencapsulation.invoke(builder, "getTransformerJavaClass", typeInfo, transformer);

        assertSame(resultJavaClass, returnType);
    }

    @Test
    public void getTransformerJavaClass_isSetTransformerClass_throwsJAXBExcpetion(final @Mocked TypeInfo typeInfo, final @Mocked XmlWriteTransformer transformer, final @Mocked JavaClass resultJavaClass) {
        new Expectations(TransformerPropertyBuilder.class) {{
            transformer.isSetTransformerClass(); result = true;
            String transformerClass = "transformerClass";
            transformer.getTransformerClass(); result = transformerClass;
            helper.getJavaClass(transformerClass); result = JAXBException.transformerClassNotFound(transformerClass);
            transformer.getTransformerClass(); result = transformerClass;
        }};

        try {
            Deencapsulation.invoke(builder, "getTransformerJavaClass", typeInfo, transformer);
            fail("JAXBException should be thrown");
        } catch(JAXBException exception) {

        }
    }

    @Test
    public void getTransformerJavaClass_isNotSetTransformerClass(final @Mocked TypeInfo typeInfo, final @Mocked XmlWriteTransformer transformer, final @Mocked JavaClass resultJavaClass) {
        new Expectations(TransformerPropertyBuilder.class) {{
            transformer.isSetTransformerClass(); result = false;
            helper.getJavaClass(typeInfo.getJavaClassName()); result = resultJavaClass;
        }};

        final JavaClass returnType = Deencapsulation.invoke(builder, "getTransformerJavaClass", typeInfo, transformer);

        assertSame(resultJavaClass, returnType);
    }

}
