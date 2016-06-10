package org.eclipse.persistence.json.bind.defaultmapping.basic;

import org.eclipse.persistence.json.bind.internal.JsonBindingBuilder;
import org.eclipse.persistence.json.bind.internal.properties.MessageKeys;
import org.eclipse.persistence.json.bind.internal.properties.Messages;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbConfig;
import javax.json.bind.JsonbException;
import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

/**
 * @author David Kral
 */
@Ignore
public class SingleValueTest {

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
        //assertEquals("null", jsonb.toJson(null));
    }

    @Test
    public void testSingleValue() {
        Jsonb jsonb = (new JsonBindingBuilder()).build();
        assertEquals("5", jsonb.toJson(5));

        jsonb = (new JsonBindingBuilder().withConfig(new JsonbConfig().withStrictIJSON(true))).build();
        try {
            jsonb.toJson(5);
            Assert.fail();
        } catch (JsonbException exception){
            assertEquals(Messages.getMessage(MessageKeys.IJSON_ENABLED_SINGLE_VALUE), exception.getMessage());
        }
    }

}
