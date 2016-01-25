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

package org.eclipse.persistence.json.bind.internal.naming;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests naming strategies.
 *
 * @author Roman Grigoriadi
 */
public class PropertyNamingStrategyTest {

    @Test
    public void testLowerUnderscore() throws Exception {

        PropertyNamingStrategy strategy = new LowerCaseWithUnderscoresStrategy();
        assertEquals("camel_case_property", strategy.toJsonPropertyName("camelCaseProperty"));
        assertEquals("camelcase_property", strategy.toJsonPropertyName("CamelcaseProperty"));
        assertEquals("camel_case_property", strategy.toJsonPropertyName("CamelCaseProperty"));
        assertEquals("_camel_case_property", strategy.toJsonPropertyName("_camelCaseProperty"));
        assertEquals("_camel_case_property", strategy.toJsonPropertyName("_CamelCaseProperty"));

        assertEquals("camelCaseProperty", strategy.toModelPropertyName("camel_case_property"));
        assertEquals("_camelcaseProperty", strategy.toModelPropertyName("_camelcase_property"));

    }
}
