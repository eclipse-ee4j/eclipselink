package org.eclipse.persistence.json.bind;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.spi.JsonProvider;

/**
 * JsonbBuilder implementation.
 *
 * @author Dmitry Kornilov
 */
public class JsonBindingBuilder implements JsonbBuilder {
    private JsonbConfig config;
    private JsonProvider provider;

    @Override
    public JsonbBuilder withConfig(JsonbConfig config) {
        this.config = config;
        return this;
    }

    @Override
    public JsonbBuilder withProvider(JsonProvider jsonpProvider) {
        this.provider = jsonpProvider;
        return this;
    }

    @Override
    public Jsonb build() {
        return new JsonBinding(this);
    }
}
