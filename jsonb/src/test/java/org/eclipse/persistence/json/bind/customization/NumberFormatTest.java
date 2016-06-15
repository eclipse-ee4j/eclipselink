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

import org.eclipse.persistence.json.bind.customization.model.NumberFormatPojo;
import org.junit.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

/**
 * Tests number format.
 * @author Roman Grigoriadi
 */
public class NumberFormatTest {
    private Jsonb jsonb = JsonbBuilder.create();



    @Test
    public void testSerialize() {
        NumberFormatPojo pojo = new NumberFormatPojo();
        pojo.bigDecimal = BigDecimal.TEN;
        pojo.bigInteger = BigInteger.ONE;
        pojo.aDouble = .1d;
        pojo.aFloat = .35f;
        pojo.aLong = Long.MAX_VALUE;
        pojo.integer = Integer.MAX_VALUE;
        pojo.aShort = 1;
        pojo.aByte = 127;

        String expectedJson = "{\"aByte\":\"127\",\"aDouble\":\"000.10000000\",\"aFloat\":\"000.34999999\",\"aLong\":\"9223372036854775807\",\"aShort\":\"00001\",\"bigDecimal\":\"00000010.000000\",\"bigInteger\":\"00000001\",\"integer\":\"2147483647.0\"}";

        assertEquals(expectedJson, jsonb.toJson(pojo));

    }

    @Test
    public void testDeserialzier() {
        String expectedJson = "{\"aByte\":\"127\",\"aDouble\":\"000.10000000\",\"aFloat\":\"000.34999999\",\"aLong\":\"9223372036854775807\",\"aShort\":\"00001\",\"bigDecimal\":\"00000010.000000\",\"bigInteger\":\"00000001\",\"integer\":\"2147483647.0\"}";
        NumberFormatPojo pojo = jsonb.fromJson(expectedJson, NumberFormatPojo.class);

        assertEquals(BigDecimal.TEN, pojo.bigDecimal);
        assertEquals(BigInteger.ONE, pojo.bigInteger);
        assertEquals(new Byte((byte) 127), pojo.aByte);
        assertEquals(new Double(.1d), pojo.aDouble);
        assertEquals(new Float(.35f), pojo.aFloat);
        assertEquals((Integer)Integer.MAX_VALUE, pojo.integer);
        assertEquals(new Short((short) 1), pojo.aShort);
        assertEquals((Long)Long.MAX_VALUE, pojo.aLong);
    }

}
