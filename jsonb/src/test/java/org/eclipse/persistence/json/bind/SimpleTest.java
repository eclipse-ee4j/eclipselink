package org.eclipse.persistence.json.bind;

import org.eclipse.persistence.json.bind.defaultmapping.basic.SingleValueTest;
import org.eclipse.persistence.json.bind.internal.JsonBindingBuilder;
import org.junit.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import static org.junit.Assert.assertEquals;

/**
 * @author Roman Grigoriadi
 */
public class SimpleTest {

    public static class StringWrapper {
        public String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }


    Jsonb jsonb = JsonbBuilder.create();

    @Test
    public void testSimpleSerialize() {
        Jsonb jsonb = (new JsonBindingBuilder()).build();
        final StringWrapper wrapper = new StringWrapper();
        wrapper.setValue("abc");
        jsonb.toJson(wrapper);
        final String val = jsonb.toJson(wrapper);
        assertEquals("{\"value\":\"abc\"}", val);
    }

    @Test
    public void testSimpleDeserialzier() {
        final StringWrapper stringWrapper = jsonb.fromJson("{\"value\":\"abc\"}", StringWrapper.class);
        assertEquals("abc", stringWrapper.getValue());
    }
}
