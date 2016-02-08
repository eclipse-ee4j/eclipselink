/*******************************************************************************
 * Copyright (c) 2016 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.persistence.json.bind.adapters;

import org.eclipse.persistence.json.bind.adapters.model.Box;
import org.eclipse.persistence.json.bind.adapters.model.BoxToCratePropagatedIntegerStringAdapter;
import org.eclipse.persistence.json.bind.adapters.model.GenericBox;
import org.junit.Before;
import org.junit.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbException;
import javax.json.bind.adapter.JsonbAdapter;
import javax.json.bind.annotation.JsonbTypeAdapter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Roman Grigoriadi
 */
public class JsonbTypeAdapterTest {

    private Jsonb jsonb;

    public static class BoxToStringAdapter implements JsonbAdapter<Box, String> {

        @Override
        public Box adaptTo(String obj) throws Exception {
            String[] strs = obj.split(":");
            return new Box(strs[0], Integer.parseInt(strs[1]));
        }

        @Override
        public String adaptFrom(Box obj) throws Exception {
            return obj.getBoxStrField()+":"+obj.getBoxIntegerField();
        }
    }

    public static class IncompatibleAdapterPojo<T,X> {

        @JsonbTypeAdapter(BoxToStringAdapter.class)
        public String str;

    }

    public static class AnnotatedPojo<T,X> {

        @JsonbTypeAdapter(BoxToStringAdapter.class)
        public Box box;
        @JsonbTypeAdapter(BoxToCratePropagatedIntegerStringAdapter.class)
        public GenericBox<T> tBox;
        @JsonbTypeAdapter(BoxToCratePropagatedIntegerStringAdapter.class)
        public GenericBox<X> xBox;
    }

    @Before
    public void setUp() throws Exception {
        jsonb = JsonbBuilder.create();
    }

    @Test
    public void testIncompatibleAdapter() throws Exception {

        IncompatibleAdapterPojo incompatibleAdapterFieldPojo = new IncompatibleAdapterPojo();
        incompatibleAdapterFieldPojo.str = "STR";
        try {
            jsonb.toJson(incompatibleAdapterFieldPojo);
            fail();
        } catch (JsonbException e) {
            assertTrue(e.getMessage().startsWith("Adapter of runtime type class"));
            assertTrue(e.getMessage().contains("does not match property type "));
        }

    }

    @Test
    public void testGenericFieldsMatch() throws Exception {
        AnnotatedPojo<Integer, String> annotatedPojo = new AnnotatedPojo<>();
        annotatedPojo.tBox = new GenericBox<>("T_BOX", 110);
        annotatedPojo.xBox = new GenericBox<>("X_BOX", "STR");
        String marshalledJson = jsonb.toJson(annotatedPojo, new AnnotatedPojo<Integer, String>() {}.getClass());
        assertEquals("{\"tBox\":{\"crateStrField\":\"T_BOX\",\"adaptedT\":{\"x\":[\"110\"]}},\"xBox\":{\"strField\":\"X_BOX\",\"x\":\"STR\"}}", marshalledJson);

        AnnotatedPojo<Integer,String> result = jsonb.fromJson("{\"tBox\":{\"crateStrField\":\"T_BOX\",\"adaptedT\":{\"x\":[\"110\"]}},\"xBox\":{\"strField\":\"X_BOX\",\"x\":\"STR\"}}", new AnnotatedPojo<Integer,String>(){}.getClass());
        assertEquals("T_BOX", result.tBox.getStrField());
        assertEquals(Integer.valueOf(110), result.tBox.getX());
        assertEquals("X_BOX", result.xBox.getStrField());
        assertEquals("STR", result.xBox.getX());
    }

    @Test
    public void testAnnotatedTbox() throws Exception {

        AnnotatedPojo pojo = new AnnotatedPojo();
        pojo.box = new Box("STR", 101);
        String marshalledJson = jsonb.toJson(pojo);
        assertEquals("{\"box\":\"STR:101\"}", marshalledJson);

        AnnotatedPojo<?, ?> result = jsonb.fromJson("{\"box\":\"STR:110\"}", AnnotatedPojo.class);
        assertEquals("STR", result.box.getBoxStrField());
        assertEquals(Integer.valueOf(110), result.box.getBoxIntegerField());
    }

}
