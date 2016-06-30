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

import org.eclipse.persistence.json.bind.customization.model.JsonbTransientCollision;
import org.eclipse.persistence.json.bind.customization.model.JsonbTransientValue;
import org.junit.Before;
import org.junit.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbException;
import javax.json.bind.annotation.JsonbProperty;

import static org.junit.Assert.*;

/**
 * @author Roman Grigoriadi
 */
public class JsonbTransientTest {

    private Jsonb jsonb;

    @Before
    public void setUp() throws Exception {
        jsonb = JsonbBuilder.create();
    }

    @Test
    public void testJsonbTransientProperty() {
        JsonbTransientValue pojo = new JsonbTransientValue();
        pojo.setTransientProperty("TRANSIENT");
        pojo.setProperty("non transient");

        assertEquals("{\"property\":\"non transient\"}", jsonb.toJson(pojo));
    }

    @Test
    public void testTransientCollidesWithOtherAnnotation() throws Exception {
        JsonbTransientCollision pojo = new JsonbTransientCollision();
        try {
            jsonb.toJson(pojo);
            fail();
        } catch (JsonbException e) {
            assertTrue(e.getMessage().startsWith(String.format("JsonbTransient annotation collides with %s", JsonbProperty.class)));
        }
    }
}
