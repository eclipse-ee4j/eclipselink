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
package org.eclipse.persistence.json.bind.defaultmapping.basic;

import org.eclipse.persistence.json.bind.JsonBindingBuilder;
import org.junit.Test;

import javax.json.bind.Jsonb;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

/**
 * Default mapping primitives tests.
 *
 * @author Dmitry Kornilov
 */
public class BasicTest {

    @Test
    public void testMarshallPrimitives() {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();

        // String
        assertEquals("\"some_string\"", jsonb.toJson("some_string"));

        // Character
        assertEquals("\"\uFFFF\"", jsonb.toJson('\uFFFF'));

        // Byte
        assertEquals("1", jsonb.toJson((byte)1));

        // Short
        assertEquals("1", jsonb.toJson((short)1));

        // Integer
        assertEquals("1", jsonb.toJson(1));

        // Long
        assertEquals("5", jsonb.toJson(5L));

        // Float
        assertEquals("1.2", jsonb.toJson(1.2f));

        // Double
        assertEquals("1.2", jsonb.toJson(1.2));

        // BigInteger
        assertEquals("1", jsonb.toJson(new BigInteger("1")));

        // BigDecimal
        assertEquals("1.2", jsonb.toJson(new BigDecimal("1.2")));

        // Number
        assertEquals("1.2", jsonb.toJson(1.2));

        // Boolean true
        assertEquals("true", jsonb.toJson(true));

        // Boolean false
        assertEquals("false", jsonb.toJson(false));

        // null
        assertEquals("null", jsonb.toJson(null));
    }

    @Test
    public void testMarshallIJson() {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();

        // Double.NEGATIVE_INFINITY
        assertEquals("\"NEGATIVE_INFINITY\"", jsonb.toJson(Double.NEGATIVE_INFINITY));

        // Double.POSITIVE_INFINITY
        assertEquals("\"POSITIVE_INFINITY\"", jsonb.toJson(Double.POSITIVE_INFINITY));

        // Double.NaN
        assertEquals("\"NaN\"", jsonb.toJson(Double.NaN));
    }

    @Test
    public void testMarshallEscapedString() {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();
        assertEquals("\" \\ \" / \b \f \n \r \t 9\"", jsonb.toJson(" \\ \" / \b \f \n \r \t \u0039"));
    }

    @Test
    public void testMarshallAppendable() {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();
        final Appendable appendable = new StringBuilder();
        jsonb.toJson(5L, appendable);
        assertEquals("5", appendable.toString());
    }

    @Test
    public void testMarshallOutputStream() throws IOException {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();

        try (final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            jsonb.toJson(5L, baos);
            assertEquals("5", baos.toString("UTF-8"));
        }
    }
}
