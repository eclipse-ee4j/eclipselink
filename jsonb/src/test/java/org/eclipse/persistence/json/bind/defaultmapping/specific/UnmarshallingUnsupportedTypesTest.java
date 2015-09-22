package org.eclipse.persistence.json.bind.defaultmapping.specific;

import org.eclipse.persistence.json.bind.defaultmapping.specific.model.ClassWithUnsupportedFields;
import org.eclipse.persistence.json.bind.defaultmapping.specific.model.CustomUnsupportedInterface;
import org.junit.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbException;

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

}
