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
package org.eclipse.persistence.testing.moxy.unit.jaxb.compiler;

import static org.junit.Assert.assertNotNull;

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
}
