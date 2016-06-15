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

package org.eclipse.persistence.json.bind.internal.unmarshaller;

import org.eclipse.persistence.json.bind.internal.ProcessingContext;
import org.eclipse.persistence.json.bind.internal.Unmarshaller;
import org.eclipse.persistence.json.bind.internal.properties.MessageKeys;
import org.eclipse.persistence.json.bind.internal.properties.Messages;
import org.eclipse.persistence.json.bind.internal.serializer.AbstractValueTypeDeserializer;
import org.eclipse.persistence.json.bind.model.JsonBindingModel;

import javax.json.bind.JsonbException;
import javax.json.bind.config.BinaryDataStrategy;
import java.lang.reflect.Type;
import java.util.Base64;

/**
 * Deserialize Base64 json string value into byte array.
 *
 * @author Roman Grigoriadi
 */
public class ByteArrayBase64Deserializer extends AbstractValueTypeDeserializer<byte[]> {

    public ByteArrayBase64Deserializer(JsonBindingModel model) {
        super(byte[].class, model);
    }

    @Override
    protected byte[] deserialize(String jsonValue, Unmarshaller unmarshaller, Type rtType) {
        return getDecoder().decode(jsonValue);
    }

    private Base64.Decoder getDecoder() {
        final String strategy = ProcessingContext.getJsonbContext().getBinaryDataStrategy();
        switch (strategy) {
            case BinaryDataStrategy.BASE_64:
                return Base64.getDecoder();
            case BinaryDataStrategy.BASE_64_URL:
                return Base64.getUrlDecoder();
            default:
                throw new JsonbException(Messages.getMessage(MessageKeys.INTERNAL_ERROR, "Invalid strategy: " + strategy));
        }
    }
}
