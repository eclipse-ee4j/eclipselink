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

import org.eclipse.persistence.json.bind.customization.model.JsonbPropertyName;
import org.eclipse.persistence.json.bind.customization.model.JsonbPropertyNameCollision;
import org.eclipse.persistence.json.bind.customization.model.JsonbPropertyNillable;
import org.junit.Before;
import org.junit.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import static org.junit.Assert.*;

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

}
