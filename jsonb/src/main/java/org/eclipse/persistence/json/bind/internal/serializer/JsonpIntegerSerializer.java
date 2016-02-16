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
 * Roman Grigoriadi
 ******************************************************************************/

package org.eclipse.persistence.json.bind.internal.serializer;

import javax.json.stream.JsonGenerator;
import java.util.Objects;

/**
 * Serializes integer.
 *
 * @author Roman Grigoriadi
 */
class JsonpIntegerSerializer extends AbstractJsonpSerializer<Integer> {

    @Override
    void writeValue(Integer value, JsonGenerator jsonGenerator) {
        jsonGenerator.write(value);
    }

    @Override
    void writeValue(String keyName, Integer value, JsonGenerator jsonGenerator) {
        jsonGenerator.write(keyName, value);
    }

    @Override
    <X> boolean supports(X value) {
        Objects.requireNonNull(value);
        return value instanceof Integer;
    }
}
