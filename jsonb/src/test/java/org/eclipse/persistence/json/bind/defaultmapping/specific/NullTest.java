package org.eclipse.persistence.json.bind.defaultmapping.specific;

import org.eclipse.persistence.json.bind.defaultmapping.specific.model.Street;
import org.junit.Before;
import org.junit.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import static org.junit.Assert.assertNull;

/**
 * @author Roman Grigoriadi
 */
public class NullTest {

    private Jsonb jsonb;

    @Before
    public void before() {
        jsonb = JsonbBuilder.create();
    }

    @Test
    public void testSetsNullIntoFields() {
        String json = "{\"name\":null,\"number\":null}";

        Street result = jsonb.fromJson(json, Street.class);
        //these have default initialization value
        assertNull(result.getName());
        assertNull(result.getNumber());
    }
}
