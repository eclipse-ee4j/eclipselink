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

import org.eclipse.persistence.json.bind.internal.ProcessingContext;
import org.eclipse.persistence.json.bind.model.SerializerBindingModel;

import javax.json.bind.JsonbConfig;
import javax.json.bind.JsonbException;
import javax.json.bind.config.BinaryDataStrategy;
import javax.json.stream.JsonGenerator;
import java.util.Base64;

/**
 * Converts byte of array according to specific strategy
 *
 * @author David Kral
 */
public class ByteArrayTypeSerializer extends AbstractValueTypeSerializer<byte[]> {

    public ByteArrayTypeSerializer(SerializerBindingModel model) {
        super(byte[].class, model);
    }


    public String toString(byte[] object) {
        if ((boolean)ProcessingContext.getJsonbContext().getConfig().getProperty(JsonbConfig.STRICT_IJSON).orElse(false)) {
            return Base64.getUrlEncoder().encodeToString(object);
        } else if (ProcessingContext.getJsonbContext().getConfig().getProperty(JsonbConfig.BINARY_DATA_STRATEGY).isPresent()) {
            String strategy = (String) ProcessingContext.getJsonbContext().getConfig().getProperty(JsonbConfig.BINARY_DATA_STRATEGY).get();
            if (strategy == null){
                throw new JsonbException("Unsupported binary data strategy!");
            }
            switch(strategy) {
                case BinaryDataStrategy.BYTE:
                    break;
                case BinaryDataStrategy.BASE_64:
                    return Base64.getEncoder().encodeToString(object);
                case BinaryDataStrategy.BASE_64_URL:
                    return Base64.getUrlEncoder().encodeToString(object);
                default:
                    throw new JsonbException("Unsupported binary data strategy!");
            }
        }
        int iMax = object.length - 1;
        if (iMax == -1)
            return "[]";

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i++) {
            b.append(object[i]);
            if (i == iMax)
                return b.append(']').toString();
            b.append(",");
        }
    }

    @Override
    protected void serialize(byte[] obj, JsonGenerator generator, String key) {
        generator.write(key, toString(obj));
    }

    @Override
    protected void serialize(byte[] obj, JsonGenerator generator) {
        generator.write(toString(obj));
    }
}
