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
package org.eclipse.persistence.json.bind.defaultmapping.specific;

import org.eclipse.persistence.json.bind.JsonBindingBuilder;
import org.eclipse.persistence.json.bind.defaultmapping.generics.model.ScalarValueWrapper;
import org.junit.Test;

import javax.json.bind.Jsonb;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

import static org.junit.Assert.assertEquals;

/**
 * Default mapping Optional* tests.
 *
 * @author Dmitry Kornilov
 */
public class OptionalTest {
    @Test
    public void testMarshallOptional() {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();
        assertEquals("{}", jsonb.toJson(new ScalarValueWrapper<>(OptionalInt.empty())));
        assertEquals("{}", jsonb.toJson(new ScalarValueWrapper<>(OptionalLong.empty())));
        assertEquals("{}", jsonb.toJson(new ScalarValueWrapper<>(OptionalDouble.empty())));
        assertEquals("{\"value\":10}", jsonb.toJson(new ScalarValueWrapper<>(OptionalInt.of(10))));
        assertEquals("{\"value\":100}", jsonb.toJson(new ScalarValueWrapper<>(OptionalLong.of(100L))));
        assertEquals("{\"value\":10.0}", jsonb.toJson(new ScalarValueWrapper<>(OptionalDouble.of(10.0D))));
    }

    @Test
    public void testMarshallOptionalObject() {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();
        assertEquals("{}", jsonb.toJson(new ScalarValueWrapper<>(Optional.empty())));
        assertEquals("{\"id\":1,\"name\":\"Cust1\"}", jsonb.toJson(Optional.of(new Customer(1, "Cust1"))));
    }

    @Test
    public void testMarshallOptionalIntArray() {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();

        final OptionalInt[] array = {OptionalInt.of(1), OptionalInt.of(2), OptionalInt.empty()};
        assertEquals("[1,2,null]", jsonb.toJson(array));
    }

    @Test
    public void testMarshallOptionalArray() {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();

        final Optional[] array = {Optional.of(new Customer(1, "Cust1")), Optional.of(new Customer(2, "Cust2")), Optional.empty()};
        assertEquals("[{\"id\":1,\"name\":\"Cust1\"},{\"id\":2,\"name\":\"Cust2\"},null]", jsonb.toJson(array));
    }

    public static class Customer {
        private int id;
        private String name;

        public Customer() {
        }

        public Customer(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
