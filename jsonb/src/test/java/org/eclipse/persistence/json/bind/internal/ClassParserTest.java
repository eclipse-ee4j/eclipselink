/*******************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 ******************************************************************************/

package org.eclipse.persistence.json.bind.internal;

import org.eclipse.persistence.json.bind.defaultmapping.modifiers.model.FieldModifiersClass;
import org.eclipse.persistence.json.bind.defaultmapping.modifiers.model.MethodModifiersClass;
import org.eclipse.persistence.json.bind.internal.cdi.DefaultConstructorCreator;
import org.eclipse.persistence.json.bind.model.ClassModel;
import org.eclipse.persistence.json.bind.model.JsonbAnnotatedElement;
import org.junit.Before;
import org.junit.Test;

import javax.json.bind.JsonbConfig;
import javax.json.spi.JsonProvider;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.junit.Assert.*;

/**
 * Test for ClassParser component.
 *
 * @author Roman Grigoriadi
 */
public class ClassParserTest {

    private ClassParser classParser;

    private JsonbContext jsonbContext;

    private AnnotationIntrospector introspector;

    @Before
    public void before() {
        jsonbContext = new JsonbContext(new JsonbConfig(), JsonProvider.provider());
        classParser = new ClassParser(jsonbContext);
        introspector = new AnnotationIntrospector(jsonbContext);
    }

    @Test
    public void testDefaultMappingFieldModifiers() {
        final JsonbAnnotatedElement<Class<?>> clsElement = introspector.collectAnnotations(FieldModifiersClass.class);
        ClassModel model = new ClassModel(FieldModifiersClass.class, introspector.introspectCustomization(clsElement), null, null);
        classParser.parseProperties(model, clsElement);
        assertTrue(model.getPropertyModel("finalString").isReadable());
        assertFalse(model.getPropertyModel("finalString").isWritable());
        assertFalse(model.getPropertyModel("staticString").isReadable());
        assertFalse(model.getPropertyModel("staticString").isWritable());
        assertFalse(model.getPropertyModel("transientString").isReadable());
        assertFalse(model.getPropertyModel("transientString").isWritable());

    }

    @Test
    public void testDefaultMappingMethodModifiers() {
        final JsonbAnnotatedElement<Class<?>> clsElement = introspector.collectAnnotations(MethodModifiersClass.class);
        ClassModel model = new ClassModel(FieldModifiersClass.class, introspector.introspectCustomization(clsElement), null, null);
        classParser.parseProperties(model, clsElement);
        assertFalse(model.getPropertyModel("publicFieldWithPrivateMethods").isReadable());
        assertFalse(model.getPropertyModel("publicFieldWithPrivateMethods").isWritable());
        assertTrue(model.getPropertyModel("publicFieldWithoutMethods").isReadable());
        assertTrue(model.getPropertyModel("publicFieldWithoutMethods").isWritable());
        assertTrue(model.getPropertyModel("getterWithoutFieldValue").isReadable());
        assertTrue(model.getPropertyModel("getterWithoutFieldValue").isWritable());


        MethodModifiersClass object = new MethodModifiersClass();
        final AtomicReference<String> accepted = new AtomicReference<>();
        Consumer<String> withoutFieldConsumer = accepted::set;
        object.setSetterWithoutFieldConsumer(withoutFieldConsumer);
        model.getPropertyModel("getterWithoutFieldValue").setValue(object, "ACCEPTED_VALUE");
        assertEquals("ACCEPTED_VALUE", accepted.get());
    }

}
