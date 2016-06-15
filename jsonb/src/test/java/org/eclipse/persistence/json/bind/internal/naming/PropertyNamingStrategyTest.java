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

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.config.PropertyNamingStrategy;

import static org.junit.Assert.assertEquals;

/**
 * Tests naming strategies.
 *
 * @author Roman Grigoriadi
 */
public class PropertyNamingStrategyTest {

    private final NamingPojo pojo = new NamingPojo("abc", "def", "ghi");

    @Test
    public void testLowerCase() throws Exception {
        PropertyNamingStrategy strategy = new LowerCaseWithUnderscoresStrategy();
        assertEquals("camel_case_property", strategy.translateName("camelCaseProperty"));
        assertEquals("camelcase_property", strategy.translateName("CamelcaseProperty"));
        assertEquals("camel_case_property", strategy.translateName("CamelCaseProperty"));
        assertEquals("_camel_case_property", strategy.translateName("_camelCaseProperty"));
        assertEquals("_camel_case_property", strategy.translateName("_CamelCaseProperty"));

        Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CASE_WITH_UNDERSCORES));
        String lowercaseUnderscoresJson = "{\"_starting_with_underscore_property\":\"def\",\"caps_underscore_property\":\"ghi\",\"upper_cased_property\":\"abc\"}";
        assertEquals(lowercaseUnderscoresJson, jsonb.toJson(pojo));
        NamingPojo result = jsonb.fromJson(lowercaseUnderscoresJson, NamingPojo.class);
        assertResult(result);

    }

    @Test
    public void testLowerDashes() throws Exception {
        PropertyNamingStrategy strategy = new LowerCaseWithDashesStrategy();
        assertEquals("camel-case-property", strategy.translateName("camelCaseProperty"));
        assertEquals("camelcase-property", strategy.translateName("CamelcaseProperty"));
        assertEquals("camel-case-property", strategy.translateName("CamelCaseProperty"));
        assertEquals("-camel-case-property", strategy.translateName("-camelCaseProperty"));
        assertEquals("-camel-case-property", strategy.translateName("-CamelCaseProperty"));

        Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CASE_WITH_DASHES));
        String lowercaseDashesJson = "{\"_starting-with-underscore-property\":\"def\",\"caps_underscore_property\":\"ghi\",\"upper-cased-property\":\"abc\"}";
        assertEquals(lowercaseDashesJson, jsonb.toJson(pojo));
        NamingPojo result = jsonb.fromJson(lowercaseDashesJson, NamingPojo.class);
        assertResult(result);
    }

    @Test
    public void testUpperCase() {
        PropertyNamingStrategy upperCaseStrat = new UpperCamelCaseStrategy();
        assertEquals("UpperCamelCase", upperCaseStrat.translateName("upperCamelCase"));
        assertEquals("UpperCamelCase", upperCaseStrat.translateName("UpperCamelCase"));

        Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE));
        String upperCased = "{\"CAPS_UNDERSCORE_PROPERTY\":\"ghi\",\"UpperCasedProperty\":\"abc\",\"_startingWithUnderscoreProperty\":\"def\"}";
        assertEquals(upperCased, jsonb.toJson(pojo));
        NamingPojo result = jsonb.fromJson(upperCased, NamingPojo.class);
        assertResult(result);
    }

    @Test
    public void testUpperCaseWithSpaces() {
        PropertyNamingStrategy upperCaseWithSpacesStrat = new UpperCamelCaseWithSpacesStrategy();
        assertEquals("Upper Camel Case", upperCaseWithSpacesStrat.translateName("upperCamelCase"));
        assertEquals("Upper Camel Case", upperCaseWithSpacesStrat.translateName("UpperCamelCase"));

        Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE_WITH_SPACES));
        String upperCased = "{\"CAPS_UNDERSCORE_PROPERTY\":\"ghi\",\"Upper Cased Property\":\"abc\",\"_starting With Underscore Property\":\"def\"}";
        assertEquals(upperCased, jsonb.toJson(pojo));
        NamingPojo result = jsonb.fromJson(upperCased, NamingPojo.class);
        assertResult(result);
    }

    @Test
    public void testCaseInsensitive() {

        Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withPropertyNamingStrategy(PropertyNamingStrategy.CASE_INSENSITIVE));
        String upperCased = "{\"CAPS_UNDERSCORE_PROPERTY\":\"ghi\",\"_startingWithUnderscoreProperty\":\"def\",\"upperCasedProperty\":\"abc\"}";
        assertEquals(upperCased, jsonb.toJson(pojo));
        NamingPojo result = jsonb.fromJson("{\"caPS_unDERscore_prOPERty\":\"ghi\",\"_startingwithUndERSCorePrOPERTy\":\"def\",\"upPERCASedProPerty\":\"abc\"}", NamingPojo.class);
        assertResult(result);
    }

    @Test
    public void testCustom() {
        Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withPropertyNamingStrategy(new PropertyNamingStrategy() {
            @Override
            public String translateName(String propertyName) {
                return propertyName + "_" + propertyName.toUpperCase();
            }
        }));

        String custom = "{\"CAPS_UNDERSCORE_PROPERTY_CAPS_UNDERSCORE_PROPERTY\":\"ghi\",\"_startingWithUnderscoreProperty__STARTINGWITHUNDERSCOREPROPERTY\":\"def\",\"upperCasedProperty_UPPERCASEDPROPERTY\":\"abc\"}";
        assertEquals(custom, jsonb.toJson(pojo));
        NamingPojo result = jsonb.fromJson(custom, NamingPojo.class);
        assertResult(result);
    }

    private void assertResult(NamingPojo result) {
        assertEquals("abc", result.upperCasedProperty);
        assertEquals("def", result._startingWithUnderscoreProperty);
        assertEquals("ghi", result.CAPS_UNDERSCORE_PROPERTY);
    }


}
