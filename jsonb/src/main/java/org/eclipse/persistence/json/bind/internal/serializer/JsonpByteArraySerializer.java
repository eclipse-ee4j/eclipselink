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
 * David Kral
 ******************************************************************************/

package org.eclipse.persistence.json.bind.internal.serializer;

import org.eclipse.persistence.json.bind.internal.JsonbContext;

import javax.json.bind.JsonbConfig;
import javax.json.bind.JsonbException;
import javax.json.bind.config.BinaryDataStrategy;
import javax.json.stream.JsonGenerator;
import java.util.Base64;
import java.util.Objects;

/**
 * Serializes byte array
 *
 * @author David Kral
 */
class JsonpByteArraySerializer extends AbstractJsonpSerializer<byte[]> {

    @Override
    void writeValue(byte[] value, JsonGenerator jsonGenerator) {
        writeValue(null, value, jsonGenerator);
    }

    @Override
    void writeValue(String keyName, byte[] value, JsonGenerator jsonGenerator) {
        String array;
        if ((boolean) JsonbContext.getInstance().getConfig().getProperty(JsonbConfig.STRICT_IJSON).orElse(false)) {
            array = Base64.getUrlEncoder().encodeToString(value);
        } else if (JsonbContext.getInstance().getConfig().getProperty(JsonbConfig.BINARY_DATA_STRATEGY).isPresent()) {
            String strategy = (String) JsonbContext.getInstance().getConfig().getProperty(JsonbConfig.BINARY_DATA_STRATEGY).get();
            if (strategy == null) {
                throw new JsonbException("Unsupported binary data strategy!");
            }
            switch (strategy) {
                case BinaryDataStrategy.BYTE:
                    marshalByteArray(keyName, value, jsonGenerator);
                    return;
                case BinaryDataStrategy.BASE_64:
                    array = Base64.getEncoder().encodeToString(value);
                    break;
                case BinaryDataStrategy.BASE_64_URL:
                    array = Base64.getUrlEncoder().encodeToString(value);
                    break;
                default:
                    throw new JsonbException("Unsupported binary data strategy!");
            }
        } else {
            marshalByteArray(keyName, value, jsonGenerator);
            return;
        }
        if (keyName != null) {
            jsonGenerator.write(keyName, array);
        }else {
            jsonGenerator.write(array);
        }
    }

    private void marshalByteArray(String keyName, byte[] value, JsonGenerator jsonGenerator) {
        int iMax = value.length - 1;
        if (keyName != null) {
            jsonGenerator.writeStartArray(keyName);
        }else {
            jsonGenerator.writeStartArray();
        }
        if (iMax == -1) {
            jsonGenerator.writeEnd();
        } else {
            for (int i = 0; ; i++) {
                jsonGenerator.write(value[i]);
                if (i == iMax) {
                    jsonGenerator.writeEnd();
                    return;
                }
            }
        }
    }

    @Override
    <X> boolean supports(X value) {
        Objects.requireNonNull(value);
        return value instanceof byte[];
    }
}
