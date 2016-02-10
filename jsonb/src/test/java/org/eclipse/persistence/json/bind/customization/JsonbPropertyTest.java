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

package org.eclipse.persistence.json.bind.customization;

import org.eclipse.persistence.json.bind.customization.model.*;
import org.junit.Before;
import org.junit.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.config.PropertyOrderStrategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests parsing of {@link javax.json.bind.annotation.JsonbProperty} test.
 * @author Roman Grigoriadi
 */
public class JsonbPropertyTest {

    private Jsonb jsonb;

    @Before
    public void setUp() throws Exception {
        jsonb = JsonbBuilder.create();
    }

    @Test
    public void testPropertyName() throws Exception {

        JsonbPropertyName pojo = new JsonbPropertyName();
        pojo.setFieldAnnotatedName("FIELD_ANNOTATED");
        pojo.setMethodAnnotName("METHOD_ANNOTATED");
        pojo.setFieldOverridedWithMethodAnnot("OVERRIDDEN_GETTER");

        assertEquals("{\"fieldAnnotatedNameCustomized\":\"FIELD_ANNOTATED\",\"getterAnnotatedName\":\"METHOD_ANNOTATED\",\"getterOverriddenName\":\"OVERRIDDEN_GETTER\"}",
                jsonb.toJson(pojo));

        String toUnmarshall = "{\"fieldAnnotatedNameCustomized\":\"FIELD_ANNOTATED\",\"setterOverriddenName\":\"OVERRIDDEN_GETTER\",\"setterAnnotatedName\":\"METHOD_ANNOTATED\"}";
        JsonbPropertyName result = jsonb.fromJson(toUnmarshall, JsonbPropertyName.class);
        assertEquals("FIELD_ANNOTATED", result.getFieldAnnotatedName());
        assertEquals("METHOD_ANNOTATED", result.getMethodAnnotName());
        assertEquals("OVERRIDDEN_GETTER", result.getFieldOverridedWithMethodAnnot());
    }

    @Test
    public void testNameCollision() {
        JsonbPropertyNameCollision nameCollisionPojo = new JsonbPropertyNameCollision();
        tryClash(()->jsonb.toJson(nameCollisionPojo));
        tryClash(()->jsonb.fromJson("{}", JsonbPropertyNameCollision.class));
    }



    private void tryClash(Runnable clashCommand) {
        try {
            clashCommand.run();
            fail();
        } catch (Exception e) {
            assertTrue(e.getMessage().startsWith("Property pojoNameCollision clashes with property pojoName"));
        }
    }


    @Test
    public void testPropertyNillable() {
        JsonbPropertyNillable pojo = new JsonbPropertyNillable();
        assertEquals("{\"nullField\":null}", jsonb.toJson(pojo));
    }

    @Test
    public void testPropertySorting() {
        FieldOrder fieldOrder = new FieldOrder();
        Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withPropertyOrderStrategy(PropertyOrderStrategy.LEXICOGRAPHICAL));
        String expectedLexicographical = "{\"aField\":\"aValue\",\"bField\":\"bValue\",\"cField\":\"cValue\",\"dField\":\"dValue\"}";
        assertEquals(expectedLexicographical, jsonb.toJson(fieldOrder));

        jsonb = JsonbBuilder.create(new JsonbConfig().withPropertyOrderStrategy(PropertyOrderStrategy.REVERSE));
        String expectedReverse = "{\"dField\":\"dValue\",\"cField\":\"cValue\",\"bField\":\"bValue\",\"aField\":\"aValue\"}";
        assertEquals(expectedReverse, jsonb.toJson(fieldOrder));
    }

    @Test
    public void testPropertyCustomOrder() {
        FieldCustomOrder fieldCustomOrder = new FieldCustomOrder();
        Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withPropertyOrderStrategy(PropertyOrderStrategy.LEXICOGRAPHICAL));
        String expectedCustomOrder = "{\"aField\":\"aValue\",\"cField\":\"cValue\",\"dField\":\"dValue\",\"bField\":\"bValue\"}";
        assertEquals(expectedCustomOrder, jsonb.toJson(fieldCustomOrder));

        FieldCustomOrderWrapper fieldCustomOrderWrapper = new FieldCustomOrderWrapper();
        String expectedOrder = "{\"fieldCustomOrder\":{\"aField\":\"aValue\",\"cField\":\"cValue\",\"dField\":\"dValue\",\"bField\":\"bValue\"},\"intField\":1,\"stringField\":\"stringValue\"}";
        assertEquals(expectedOrder, jsonb.toJson(fieldCustomOrderWrapper));
    }

    @Test
    public void testPropertySetCustomOrder() {
        FieldSpecificOrder fieldSpecificOrder = new FieldSpecificOrder();
        String expectedSpecific = "{\"aField\":\"aValue\",\"dField\":\"dValue\"}";
        assertEquals(expectedSpecific, jsonb.toJson(fieldSpecificOrder));
    }

    @Test
    public void testPropertySortingWithNamingAnnotation() {
        FieldOrderNameAnnotation fieldOrderNameAnnotation = new FieldOrderNameAnnotation();
        Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withPropertyOrderStrategy(PropertyOrderStrategy.LEXICOGRAPHICAL));
        String expectedLexicographical = "{\"bField\":\"bValue\",\"cField\":\"cValue\",\"dField\":\"dValue\",\"zField\":\"aValue\"}";
        assertEquals(expectedLexicographical, jsonb.toJson(fieldOrderNameAnnotation));

        jsonb = JsonbBuilder.create(new JsonbConfig().withPropertyOrderStrategy(PropertyOrderStrategy.REVERSE));
        String expectedReverse = "{\"zField\":\"aValue\",\"dField\":\"dValue\",\"cField\":\"cValue\",\"bField\":\"bValue\"}";
        assertEquals(expectedReverse, jsonb.toJson(fieldOrderNameAnnotation));
    }

}
