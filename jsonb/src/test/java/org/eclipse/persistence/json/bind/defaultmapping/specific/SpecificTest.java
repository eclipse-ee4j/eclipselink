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
import org.junit.Test;

import javax.json.bind.Jsonb;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.Assert.assertEquals;

/**
 * Specific standard Java SE types tests: {@link BigDecimal}, {@link BigInteger}, {@link URL}, {@link URI}.
 *
 * @author Dmitry Kornilov
 */
public class SpecificTest {
    @Test
    public void testMarshallBigDecimal() {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();
        assertEquals("100", jsonb.toJson(BigDecimal.valueOf(100L)));
        assertEquals("100.1", jsonb.toJson(BigDecimal.valueOf(100.1D)));
    }

    @Test
    public void testMarshallBigInteger() {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();
        assertEquals("100", jsonb.toJson(BigInteger.valueOf(100)));
    }

    @Test
    public void testMarshallUri() throws URISyntaxException {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();
        assertEquals("\"http://www.oracle.com\"", jsonb.toJson(new URI("http://www.oracle.com")));
    }

    @Test
    public void testMarshallUrl() throws MalformedURLException {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();
        assertEquals("\"http://www.oracle.com\"", jsonb.toJson(new URL("http://www.oracle.com")));
    }
}
