/*
 * Copyright (c) 2015, 2020 Oracle and/or its affiliates. All rights reserved.
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
//  - Martin Vojtek - 2.6 - Initial implementation
package org.eclipse.persistence.testing.moxy.unit.jaxb.compiler;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.ArrayList;

import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;

import org.eclipse.persistence.internal.oxm.schema.model.ComplexType;
import org.eclipse.persistence.internal.oxm.schema.model.Schema;
import org.eclipse.persistence.internal.oxm.schema.model.TypeDefParticle;
import org.eclipse.persistence.jaxb.compiler.Property;
import org.eclipse.persistence.jaxb.compiler.SchemaGenerator;
import org.eclipse.persistence.jaxb.compiler.TypeInfo;
import org.eclipse.persistence.jaxb.compiler.builder.TransformerPropertyBuilder;
import org.eclipse.persistence.jaxb.javamodel.Helper;
import org.eclipse.persistence.jaxb.javamodel.JavaClass;
import org.eclipse.persistence.jaxb.javamodel.JavaMethod;
import org.eclipse.persistence.jaxb.javamodel.JavaField;
import org.eclipse.persistence.jaxb.javamodel.JavaModel;
import org.eclipse.persistence.jaxb.javamodel.JavaConstructor;
import org.eclipse.persistence.jaxb.javamodel.JavaPackage;
import org.eclipse.persistence.jaxb.javamodel.JavaAnnotation;
import org.eclipse.persistence.jaxb.javamodel.JavaClassInstanceOf;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests SchemaGenerator methdos.
 *
 * @author Martin Vojtek
 *
 */
@RunWith(JMockit.class)
public class SchemaGeneratorTestCase {

    @Test
    public void getTransformerPropertyBuilder(final @Mocked Helper helper, final @Mocked Property property, final @Mocked TypeInfo typeInfo) {
        SchemaGenerator schemaGenerator = new SchemaGenerator(helper);

        TransformerPropertyBuilder transformerPropertyBuilder = Deencapsulation.invoke(schemaGenerator, "getTransformerPropertyBuilder", property, typeInfo);
        assertNotNull(transformerPropertyBuilder);
    }

    @Test
    public void addTransformerToSchema(final @Mocked Helper helper, final @Mocked Property property, final @Mocked TypeInfo typeInfo, final @Mocked TypeDefParticle typeDefParticle, final @Mocked ComplexType complexType, final @Mocked Schema schema, final @Mocked TransformerPropertyBuilder transformerPropertyBuilder) {
        final SchemaGenerator schemaGenerator = new SchemaGenerator(helper);
        final java.util.List<Property> props = new ArrayList<>();

        new Expectations(SchemaGenerator.class) {{
            Deencapsulation.invoke(schemaGenerator, "getTransformerPropertyBuilder", property, typeInfo); result = transformerPropertyBuilder;
            transformerPropertyBuilder.buildProperties(); result = props;
            schemaGenerator.addToSchemaType(typeInfo, props, typeDefParticle, complexType, schema);
         }};

        Deencapsulation.invoke(schemaGenerator, "addTransformerToSchema", property, typeInfo, typeDefParticle, complexType, schema);
    }

    @Test
    public void javaxXmlRpcStringHolderTest(final @Mocked Helper helper) {
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
