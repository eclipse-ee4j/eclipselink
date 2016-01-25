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
 *     Dmitry Kornilov - initial implementation
 ******************************************************************************/
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

    public JsonbConfig getConfig() {
        return config;
    }

    public JsonProvider getProvider() {
        return provider;
    }

    @Override
    public Jsonb build() {
        if (config == null) {
            config = new JsonbConfig();
        }
        return new JsonBinding(this);
    }
}
