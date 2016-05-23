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
 * Dmitry Kornilov
 * Roman Grigoriadi
 ******************************************************************************/
package org.eclipse.persistence.json.bind.internal;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.spi.JsonProvider;
import java.util.Optional;

/**
 * JsonbBuilder implementation.
 *
 * @author Dmitry Kornilov
 */
public class JsonBindingBuilder implements JsonbBuilder {
    private Optional<JsonbConfig> config = Optional.of(new JsonbConfig());
    private Optional<JsonProvider> provider = Optional.empty();

    @Override
    public JsonbBuilder withConfig(JsonbConfig config) {
        this.config = Optional.of(config);
        return this;
    }

    @Override
    public JsonbBuilder withProvider(JsonProvider jsonpProvider) {
        this.provider = Optional.of(jsonpProvider);
        return this;
    }

    public JsonbConfig getConfig() {
        return config.get();
    }

    public Optional<JsonProvider> getProvider() {
        return provider;
    }

    @Override
    public Jsonb build() {
        return new JsonBinding(this);
    }
}
