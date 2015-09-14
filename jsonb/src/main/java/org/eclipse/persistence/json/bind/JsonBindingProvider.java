package org.eclipse.persistence.json.bind;

import javax.json.bind.JsonbBuilder;
import javax.json.bind.spi.JsonbProvider;

/**
 * JsonbProvider implementation.
 *
 * @author Dmitry Kornilov
 */
public class JsonBindingProvider extends JsonbProvider {
    @Override
    public JsonbBuilder create() {
        return new JsonBindingBuilder();
    }
}
