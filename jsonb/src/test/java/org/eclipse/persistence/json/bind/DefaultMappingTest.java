package org.eclipse.persistence.json.bind;

import org.junit.Test;

import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.bind.Jsonb;
import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

/**
 * Default mapping tests.
 *
 * @author Dmitry Kornilov
 */
public class DefaultMappingTest {

    @Test
    public void testMarshallPrimitives() {
        Jsonb jsonb = (new JsonBindingBuilder()).build();

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
        assertEquals("1.2", jsonb.toJson((java.lang.Number)1.2));

        // Boolean true
        assertEquals("true", jsonb.toJson(true));

        // Boolean false
        assertEquals("false", jsonb.toJson(false));

        // null
        assertEquals("null", jsonb.toJson(null));
    }

    //@Test
    public void testMarshallEscapedString() {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();
        assertEquals("\" \\\\ \\\" / \\b \\f \\n \\r \\t 9\"", jsonb.toJson(" \\ \" / \b \f \n \r \t \u0039"));
    }

    //@Test
    public void testMarshallJsonObject() {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();
        final JsonBuilderFactory factory = Json.createBuilderFactory(null);
        final JsonObject jsonObject = factory.createObjectBuilder()
                .add("name", "home")
                .add("city", "Prague")
                .build();

        assertEquals("{\"name\":\"home\",\"city\":\"Prague\"}", jsonb.toJson(jsonObject));
    }

    @Test
    public void testMarshallObject() {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();

        final Customer customer = new Customer();
        customer.setForename("John");
        customer.setSurname("Doe");

        assertEquals("{\"forename\":\"John\",\"surname\":\"Doe\"}", jsonb.toJson(customer));
    }
}
