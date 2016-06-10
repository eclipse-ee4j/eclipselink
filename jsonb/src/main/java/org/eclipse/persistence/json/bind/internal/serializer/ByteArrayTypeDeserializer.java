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
import org.eclipse.persistence.json.bind.internal.Unmarshaller;
import org.eclipse.persistence.json.bind.model.JsonBindingModel;

import javax.json.bind.JsonbConfig;
import javax.json.bind.JsonbException;
import javax.json.bind.config.BinaryDataStrategy;
import java.lang.reflect.Type;
import java.util.Base64;

/**
 * Converts byte of array according to specific strategy
 *
 * @author David Kral
 */
public class ByteArrayTypeDeserializer extends AbstractValueTypeDeserializer<byte[]> {

    public ByteArrayTypeDeserializer(JsonBindingModel model) {
        super(byte[].class, model);
    }

    @Override
    public byte[] deserialize(String jsonValue, Unmarshaller unmarshaller, Type rtType) {
        if ((boolean) ProcessingContext.getJsonbContext().getConfig().getProperty(JsonbConfig.STRICT_IJSON).orElse(false)) {
            return Base64.getUrlDecoder().decode(jsonValue);
        } else if (ProcessingContext.getJsonbContext().getConfig().getProperty(JsonbConfig.BINARY_DATA_STRATEGY).isPresent()) {
            String strategy = (String) ProcessingContext.getJsonbContext().getConfig().getProperty(JsonbConfig.BINARY_DATA_STRATEGY).get();
            if (strategy == null){
                throw new JsonbException("Unsupported binary data strategy!");
            }
            switch(strategy) {
                case BinaryDataStrategy.BYTE:
                    break;
                case BinaryDataStrategy.BASE_64:
                    return Base64.getDecoder().decode(jsonValue);
                case BinaryDataStrategy.BASE_64_URL:
                    return Base64.getUrlDecoder().decode(jsonValue);
                default:
                    throw new JsonbException("Unsupported binary data strategy!");
            }
        }
        String[] byteValues = jsonValue.split(",");
        byte[] bytes = new byte[byteValues.length];

        for (int i=0, len=bytes.length; i<len; i++) {
            bytes[i] = Byte.parseByte(byteValues[i].trim());
        }
        return bytes;
    }
}
