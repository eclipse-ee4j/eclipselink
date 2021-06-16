/*
 * Copyright (c) 2015, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
package org.eclipse.persistence.testing.moxy.unit.jaxb.compiler;


import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.internal.jaxb.JaxbClassLoader;
import org.eclipse.persistence.internal.oxm.schema.SchemaModelProject;
import org.eclipse.persistence.internal.oxm.schema.model.Schema;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.compiler.Generator;
import org.eclipse.persistence.jaxb.compiler.Property;
import org.eclipse.persistence.jaxb.compiler.TypeInfo;
import org.eclipse.persistence.jaxb.compiler.builder.TransformerPropertyBuilder;
import org.eclipse.persistence.jaxb.javamodel.*;
import org.eclipse.persistence.jaxb.javamodel.reflection.JavaModelImpl;
import org.eclipse.persistence.jaxb.javamodel.reflection.JavaModelInputImpl;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.xmltransformation.Employee;
import org.eclipse.persistence.testing.moxy.unit.jaxb.compiler.builder.ChildSchemaGenerator;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * Tests SchemaGenerator methods.
 */
public class SchemaGeneratorTestCase {

    private static final Class<?>[] DOMAIN_CLASSES = new Class<?>[]{Employee.class};
    private static final String EMPLOYEE_CLASS_NAME = Employee.class.getTypeName();
    private static final String BINDINGS_DOC = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/xmltransformation/eclipselink-oxm.xml";
    private static final String XML_TRANSFORMATION_PROPERTY_NAME = "normalHours";

    @Test
    public void getTransformerPropertyBuilder() {
        JavaModelInputImpl jModelInput = new JavaModelInputImpl(DOMAIN_CLASSES, new JavaModelImpl(new JaxbClassLoader(Thread.currentThread().getContextClassLoader(), DOMAIN_CLASSES)));
        Helper helper = new Helper(jModelInput.getJavaModel());
        Generator generator = new Generator(jModelInput);
        TypeInfo typeInfo = generator.getAnnotationsProcessor().getTypeInfos().get(EMPLOYEE_CLASS_NAME);
        Property normalHoursProperty = typeInfo.getProperties().get(XML_TRANSFORMATION_PROPERTY_NAME);
        ChildSchemaGenerator childSchemaGenerator = new ChildSchemaGenerator(helper);
        TransformerPropertyBuilder transformerPropertyBuilder = childSchemaGenerator.getTransformerPropertyBuilder(normalHoursProperty, typeInfo);
        assertNotNull(transformerPropertyBuilder);
    }

    @Test
    public void addTransformerToSchema() {
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
        java.util.List<Property> props = transformerPropertyBuilder.buildProperties();
        // Indirect call of org.eclipse.persistence.jaxb.compiler.SchemaGenerator.addTransformerToSchema(.....) method.
        List<Schema> schemas = (List)generator.generateSchema();

        XMLContext context = new XMLContext(new SchemaModelProject());
        XMLMarshaller marshaller = context.createMarshaller();
        final StringWriter stringWriter = new StringWriter();
        marshaller.marshal(schemas.get(0), stringWriter);
        String outputSchema = stringWriter.toString();

        // Verify that XML-Transformation property is added to XML schema
        for (Property property: props) {
            assertTrue(outputSchema.contains(property.getPropertyName()));
        }
    }

    @Test
    public void javaxXmlRpcStringHolderTest() {
        //Prepare Helper
        JavaModelInputImpl jModelInput = new JavaModelInputImpl(DOMAIN_CLASSES, new JavaModelImpl(new JaxbClassLoader(Thread.currentThread().getContextClassLoader(), DOMAIN_CLASSES)));
        Helper helper = new Helper(jModelInput.getJavaModel());

        assertFalse(helper.isBuiltInJavaType(new JavaClass() { public String getRawName() { return "javax.xml.rpc.StringHolder";}
            public JavaClassInstanceOf instanceOf() { return null; }
            public boolean isSynthetic() { return false; }
            public Collection getActualTypeArguments() { return null; }
            public JavaClass getComponentType() { return null; }
            public String getQualifiedName() { return null; }
            public boolean hasActualTypeArguments() { return false; }
            public Collection getDeclaredClasses() { return null; }
            public JavaField getDeclaredField(String arg0) { return null; }
            public Collection getDeclaredFields() { return null; }
            public JavaMethod getDeclaredMethod(String arg0, JavaClass[] arg1) { return null; }
            public Collection getDeclaredMethods() { return null; }
            public JavaMethod getMethod(String arg0, JavaClass[] arg1) { return null; }
            public Collection getMethods() { return null ;}
            public JavaConstructor getConstructor(JavaClass[] parameterTypes) { return null; }
            public Collection getConstructors() { return null; }
            public JavaConstructor getDeclaredConstructor(JavaClass[] parameterTypes) { return null; }
            public Collection getDeclaredConstructors() { return null; }
            public int getModifiers() { return 0; }
            public String getName() { return null; }
            public JavaPackage getPackage() { return null; }
            public String getPackageName() { return null; }
            public JavaClass getSuperclass() { return null; }
            public Type[] getGenericInterfaces() { return null; }
            public Type getGenericSuperclass() { return null; }
            public boolean isAbstract() { return false; }
            public boolean isAnnotation() { return false; }
            public boolean isArray() { return false; }
            public boolean isAssignableFrom(JavaClass arg0) { return false; }
            public boolean isEnum() { return false; }
            public boolean isFinal() { return false; }
            public boolean isInterface() { return false; }
            public boolean isMemberClass() { return false; }
            public boolean isPrimitive() { return false; }
            public boolean isPrivate() { return false; }
            public boolean isProtected() { return false; }
            public boolean isPublic() { return false; }
            public boolean isStatic() { return false; }
            public JavaAnnotation getAnnotation(JavaClass arg0) { return null; }
            public Collection getAnnotations() { return null; }
            public JavaAnnotation getDeclaredAnnotation(JavaClass arg0) { return null; }
            public Collection getDeclaredAnnotations() { return null; }
        }));
    }
}
