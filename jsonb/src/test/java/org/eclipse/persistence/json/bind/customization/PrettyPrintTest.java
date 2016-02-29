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

import org.junit.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * Tests pretty print to JSONP propagation
 *
 * @author Roman Grigoriadi
 */
public class PrettyPrintTest {

    @Test
    public void testPrettyPrint() {
        final JsonbConfig config = new JsonbConfig();
        config.setProperty(JsonbConfig.FORMATTING, Boolean.TRUE);
        final Jsonb jsonb = JsonbBuilder.create(config);
        assertEquals("\n[\n    \"first\",\n    \"second\"\n]", jsonb.toJson(Arrays.asList("first", "second")));
    }

    @Test
    public void testPrettyPrintFalse() {
        final JsonbConfig config = new JsonbConfig();
        config.setProperty(JsonbConfig.FORMATTING, Boolean.FALSE);
        final Jsonb jsonb = JsonbBuilder.create(config);
        assertEquals("[\"first\",\"second\"]", jsonb.toJson(Arrays.asList("first", "second")));
    }

}
