package org.eclipse.persistence.json.bind.defaultmapping.basic;

import org.eclipse.persistence.json.bind.defaultmapping.basic.model.BooleanModel;
import org.eclipse.persistence.json.bind.defaultmapping.generics.model.GenericTestClass;
import org.junit.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.lang.reflect.Type;

import static org.junit.Assert.assertEquals;

/**
 * Tests serialization and deserialization of boolean values
 *
 * Created by Ehsan Zaery Moghaddam (zaerymoghaddam@gmail.com) on 9/17/16.
 */
public class BooleanTest {
    private Jsonb jsonb = JsonbBuilder.create();

    @Test
    public void testBooleanSerialization() throws Exception {
        BooleanModel booleanModel = new BooleanModel(true, false);

        String expected = "{\"field1\":\"true\",\"field2\":\"false\"}";
        assertEquals(expected, jsonb.toJson(booleanModel));
    }

    @Test
    public void testBooleanDeserializationFromBooleanAsStringValue() throws Exception {
        BooleanModel booleanModel = jsonb.fromJson("{\"field1\":\"true\",\"field2\":\"true\"}", BooleanModel.class);
        assertEquals(booleanModel.field1, true);
        assertEquals(booleanModel.field2, true);
    }

    @Test
    public void testBooleanDeserializationFromBooleanRawValue() throws Exception {
        BooleanModel booleanModel = jsonb.fromJson("{\"field1\":false,\"field2\":false}", BooleanModel.class);
        assertEquals(booleanModel.field1, false);
        assertEquals(booleanModel.field2, false);
    }
}
