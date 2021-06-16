/*
 * Copyright (c) 2015, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation
package org.eclipse.persistence.testing.moxy.unit.jaxb.compiler.builder;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.exceptions.JAXBException;
import org.eclipse.persistence.internal.jaxb.JaxbClassLoader;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.compiler.Generator;
import org.eclipse.persistence.jaxb.compiler.Property;
import org.eclipse.persistence.jaxb.compiler.TypeInfo;
import org.eclipse.persistence.jaxb.compiler.builder.TransformerPropertyBuilder;
import org.eclipse.persistence.jaxb.javamodel.Helper;
import org.eclipse.persistence.jaxb.javamodel.reflection.JavaModelImpl;
import org.eclipse.persistence.jaxb.javamodel.reflection.JavaModelInputImpl;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings;

import org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.xmltransformation.Employee;

import org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.xmltransformation.NormalHoursAttributeTransformer;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.xmltransformation.StartTimeTransformer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Tests TransformerPropertyBuilder methods.
 *
 */
public class TransformerPropertyBuilderTestCase {

    private static final Class<?>[] DOMAIN_CLASSES = new Class<?>[]{Employee.class};
    private static final String EMPLOYEE_CLASS_NAME = Employee.class.getTypeName();
    private static final String BINDINGS_DOC = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/xmltransformation/eclipselink-oxm.xml";
    private static final String XML_TRANSFORMATION_PROPERTY_NAME = "normalHours";

    @Test
    public void testTransformerPropertyBuilder() {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(BINDINGS_DOC);
        HashMap<String, Source> metadataSourceMap = new HashMap<>();
        metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.xmltransformation", new StreamSource(inputStream));
        Map<String, Map<String, Source>> properties = new HashMap<>();
        properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, metadataSourceMap);
        Map<String, XmlBindings> bindings = JAXBContextFactory.getXmlBindingsFromProperties(properties, Thread.currentThread().getContextClassLoader());
        JavaModelInputImpl jModelInput = new JavaModelInputImpl(DOMAIN_CLASSES, new JavaModelImpl(new JaxbClassLoader(Thread.currentThread().getContextClassLoader(), DOMAIN_CLASSES)));
        Helper helper = new Helper(jModelInput.getJavaModel());
        Generator generator = new Generator(jModelInput, bindings, Thread.currentThread().getContextClassLoader(), "", false);
        TypeInfo typeInfo = generator.getAnnotationsProcessor().getTypeInfos().get(EMPLOYEE_CLASS_NAME);
        Property normalHoursProperty = typeInfo.getProperties().get(XML_TRANSFORMATION_PROPERTY_NAME);
        ChildSchemaGenerator childSchemaGenerator = new ChildSchemaGenerator(helper);
        TransformerPropertyBuilder transformerPropertyBuilder = childSchemaGenerator.getTransformerPropertyBuilder(normalHoursProperty, typeInfo);
        // Indirect call of org.eclipse.persistence.jaxb.compiler.builder.TransformerPropertyBuilder.getPropertyName(...) and org.eclipse.persistence.jaxb.compiler.builder.TransformerPropertyBuilder.buildPropertyFromTransformer(...)
        // Indirect call of org.eclipse.persistence.jaxb.compiler.builder.TransformerPropertyBuilder.getTransformerJavaClass(...) with booth options writeTransformer.isSetTransformerClass() true,false
        List<Property> props = transformerPropertyBuilder.buildProperties();
        assertEquals(2, props.size());
        // Verification of result of org.eclipse.persistence.jaxb.compiler.builder.TransformerPropertyBuilder.getPropertyName(...)
        // Verify property names
        assertEquals("start-time", props.get(0).getPropertyName());
        assertEquals("end-time", props.get(1).getPropertyName());
        // Verification of result of org.eclipse.persistence.jaxb.compiler.builder.TransformerPropertyBuilder.getReturnTypeFromTransformer(...)
        // Verify property types
        assertEquals(String.class.getName(), props.get(0).getType().getName());
        assertEquals(String.class.getName(), props.get(1).getType().getName());

        assertEquals(NormalHoursAttributeTransformer.class.getName(), normalHoursProperty.getXmlTransformation().getXmlReadTransformer().getTransformerClass());
        assertEquals(StartTimeTransformer.class.getName() , normalHoursProperty.getXmlTransformation().getXmlWriteTransformer().get(0).getTransformerClass());
        assertEquals(String.class.getName(), normalHoursProperty.getGenericType().getName());

        // Indirect call of org.eclipse.persistence.jaxb.compiler.builder.TransformerPropertyBuilder.getTransformerJavaClass(...) with invalid TransformerClass name
        try {
            normalHoursProperty.getXmlTransformation().getXmlWriteTransformer().get(0).setTransformerClass("xxx.xxx.WrongClassName");
            props = transformerPropertyBuilder.buildProperties();
            fail("Expected JAXBException.");
        } catch (JAXBException expected) {
            assertEquals(50054, expected.getErrorCode());
        }
    }
}
