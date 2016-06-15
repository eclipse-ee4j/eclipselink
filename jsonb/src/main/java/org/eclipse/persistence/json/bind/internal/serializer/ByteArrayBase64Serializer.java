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
import org.eclipse.persistence.json.bind.internal.properties.MessageKeys;
import org.eclipse.persistence.json.bind.internal.properties.Messages;
import org.eclipse.persistence.json.bind.model.SerializerBindingModel;

import javax.json.bind.JsonbException;
import javax.json.bind.config.BinaryDataStrategy;
import javax.json.stream.JsonGenerator;
import java.util.Base64;

/**
 * Serializes byte array with Base64.
 *
 * @author Roman Grigoriadi
 */
public class ByteArrayBase64Serializer extends AbstractValueTypeSerializer<byte[]> {

    /**
     * New instance.
     *
     * @param clazz clazz to work with
     * @param model
     */
    public ByteArrayBase64Serializer(Class<byte[]> clazz, SerializerBindingModel model) {
        super(clazz, model);
    }

    @Override
    protected void serialize(byte[] obj, JsonGenerator generator, String key) {
        generator.write(key, getEncoder().encodeToString(obj));
    }

    @Override
    protected void serialize(byte[] obj, JsonGenerator generator) {
        generator.write(getEncoder().encodeToString(obj));
    }

    private Base64.Encoder getEncoder() {
        final String strategy = ProcessingContext.getJsonbContext().getBinaryDataStrategy();
        switch (strategy) {
            case BinaryDataStrategy.BASE_64:
                return Base64.getEncoder();
            case BinaryDataStrategy.BASE_64_URL:
                return Base64.getUrlEncoder();
            default:
                throw new JsonbException(Messages.getMessage(MessageKeys.INTERNAL_ERROR, "Invalid strategy: " + strategy));
        }
    }

}
