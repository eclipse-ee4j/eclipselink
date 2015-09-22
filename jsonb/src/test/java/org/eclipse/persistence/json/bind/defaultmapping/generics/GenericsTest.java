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
 *     Dmitry Kornilov - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.json.bind.defaultmapping.generics;

import org.eclipse.persistence.json.bind.JsonBindingBuilder;
import org.junit.Test;

import javax.json.bind.Jsonb;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

/**
 * This class contains JSONB default mapping generics tests.
 *
 * @author Dmitry Kornilov
 */
public class GenericsTest {
    @Test
    public void testMarshallGenericClass() {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();

        final GenericTestClass<String, Integer> myGenericClassField = new GenericTestClass<>();
        myGenericClassField.field1 = "value1";
        myGenericClassField.field2 = 3;

        assertEquals("{\"field1\":\"value1\",\"field2\":3}", jsonb.toJson(myGenericClassField));
    }

    @Test
    public void testMarshallCyclicGenericClass() {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();

        final MyCyclicGenericClass<CyclicSubClass> myCyclicGenericClass = new MyCyclicGenericClass<>();
        final CyclicSubClass cyclicSubClass = new CyclicSubClass();
        cyclicSubClass.subField = "subFieldValue";
        myCyclicGenericClass.field1 = cyclicSubClass;

        assertEquals("{\"field1\":{\"subField\":\"subFieldValue\"}}", jsonb.toJson(myCyclicGenericClass));
    }

    @Test
    public void testMarshallWithType() {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();

        List<Optional<String>> expected = Arrays.asList(Optional.empty(), Optional.ofNullable("first"), Optional.of("second"));
        //String json = jsonb.toJson(expected, DefaultMappingGenerics.class.getField("listOfOptionalStringField").getGenericType());

        // TODO according to Martin V this should not pass... but it is...
        String json = jsonb.toJson(expected);
        assertEquals("[null,\"first\",\"second\"]",json);
    }

    static class MyCyclicGenericClass<T extends MyCyclicGenericClass<? extends T>> {
        public T field1;

        public MyCyclicGenericClass() {}
    }

    static class CyclicSubClass extends MyCyclicGenericClass<CyclicSubClass> {
        public String subField;

        public CyclicSubClass() {}
    }
}

