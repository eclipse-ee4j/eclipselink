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
 * Roman Grigoriadi
 ******************************************************************************/

package org.eclipse.persistence.json.bind.defaultmapping.specific;

import org.eclipse.persistence.json.bind.defaultmapping.specific.model.ClassWithUnsupportedFields;
import org.eclipse.persistence.json.bind.defaultmapping.specific.model.CustomUnsupportedInterface;
import org.eclipse.persistence.json.bind.defaultmapping.specific.model.SupportedTypes;
import org.junit.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.OptionalLong;

import static org.junit.Assert.*;

/**
 * @author Roman Grigoriadi
 */
public class UnmarshallingUnsupportedTypesTest {

    private final Jsonb jsonb = JsonbBuilder.create();

    @Test
    public void testUnmarshallToUnsupportedInterface() {
        ClassWithUnsupportedFields unsupported = new ClassWithUnsupportedFields();
        unsupported.customInterface = new CustomUnsupportedInterface() {

            private String value = "value1";
            @Override
            public String getValue() {
                return value;
            }

            @Override
            public void setValue(String value) {
                throw new IllegalStateException("Not supposed to be called.");
            }
        };
        String expected = "{\"customInterface\":{\"value\":\"value1\"}}";
        assertEquals(expected, jsonb.toJson(unsupported));
        try {
            jsonb.fromJson(expected, ClassWithUnsupportedFields.class);
            fail("Should report an error");
        } catch (JsonbException e) {
            assertTrue(e.getMessage().startsWith("Can't infer a type"));//TODO message catalog.
        }
    }

    @Test
    public void testPojoForMalformedJson() throws Exception {

        SupportedTypes supportedTypes = new SupportedTypes();
        LocalDateTime localDateTime = LocalDateTime.of(2015, 12, 28, 15, 57);
        ZoneId prague = ZoneId.of("Europe/Prague");
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, prague);
        supportedTypes.setInstant(Instant.from(zonedDateTime));
        supportedTypes.setOptionalLong(OptionalLong.of(11L));
        supportedTypes.setZonedDateTime(zonedDateTime);
        supportedTypes.setNestedPojo(new SupportedTypes.NestedPojo());
        supportedTypes.getNestedPojo().setIntegerValue(10);

        String json = "{\"instant\":\"2015-12-28T14:57:00Z\",\"nestedPojo\":{\"integerValue\":10},\"optionalLong\":11,\"zonedDateTime\":\"2015-12-28T15:57:00+01:00[Europe/Prague]\"}";
        assertEquals(json, jsonb.toJson(supportedTypes));

        SupportedTypes result = jsonb.fromJson(json, SupportedTypes.class);
        assertEquals(result.getInstant(), supportedTypes.getInstant());
        assertEquals(result.getZonedDateTime(), supportedTypes.getZonedDateTime());
        assertEquals(result.getOptionalLong(), supportedTypes.getOptionalLong());
        assertEquals(Integer.valueOf(10), result.getNestedPojo().getIntegerValue());

    }

    @Test
    public void testSupportedTypeAsObjectInJson() {
        //wrong, instant is wrapped with {}, unmarshalls to object.
        String json  = "{\"instant\":{\"instantWrongKey\":\"2015-12-28T14:57:00Z\"},\"optionalLong\":11}";
        assertFail(json, SupportedTypes.class, "JSON object not expected for unmarshalling into field");

        //wrong, zonedDateTime is wrapped with [], unmarshalls to collection.
        json  = "{\"zonedDateTime\":[\"2015-12-28T15:57:00+01:00[Europe/Prague]\"]}";
        assertFail(json, SupportedTypes.class, "JSON array not expected for unmarshalling into field");
    }

    @Test
    public void testPojoAsScalarValue() {
        //wrong, nestedPojo is a value.
        String json  = "{\"nestedPojo\":\"10\",\"optionalLong\":11}";
        assertFail(json, SupportedTypes.class, "Can't convert JSON value into:");
    }

    @Test
    public void testPojoAsArray() {
        //wrong, nestedPojo is a collection.
        String json  = "{\"nestedPojo\":[\"10\"],\"optionalLong\":11}";
        assertFail(json, SupportedTypes.class, "JSON array not expected for unmarshalling into field");
    }

    @Test
    public void testMissingFieldInModel() {
        String json  = "{\"nestedPojo\":{\"integerValue\":10,\"missingField\":5},\"optionalLong\":11}";
        SupportedTypes result = jsonb.fromJson(json, SupportedTypes.class);
        assertEquals(Integer.valueOf(10), result.getNestedPojo().getIntegerValue());
        assertEquals(11, result.getOptionalLong().getAsLong());
    }

    private void assertFail(String json, Class clazz, String msg) {
        try {
            jsonb.fromJson(json, clazz);
            fail();
        } catch (JsonbException e) {
            assertTrue(e.getMessage().startsWith(msg));
        }
    }
}
