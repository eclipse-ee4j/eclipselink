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

import org.eclipse.persistence.json.bind.internal.JsonbContext;
import org.eclipse.persistence.json.bind.internal.properties.MessageKeys;
import org.eclipse.persistence.json.bind.internal.properties.Messages;

import javax.json.bind.JsonbConfig;
import javax.json.bind.JsonbException;
import javax.json.stream.JsonGenerator;
import java.io.UnsupportedEncodingException;
import java.util.Objects;

/**
 * Serializes String.
 *
 * @author Roman Grigoriadi
 */
public class JsonpStringSerializer extends AbstractJsonpSerializer<String> {

    @Override
    protected void writeValue(String value, JsonGenerator jsonGenerator) {
        jsonGenerator.write(getValue(value));
    }

    @Override
    protected void writeValue(String keyName, String value, JsonGenerator jsonGenerator) {
        jsonGenerator.write(keyName, getValue(value));
    }

    public String getValue(String value){
        if ((boolean) JsonbContext.getInstance().getConfig().getProperty(JsonbConfig.STRICT_IJSON).orElse(false)) {
            try {
                String newString = new String(value.getBytes("UTF-8"), "UTF-8");
                if (!newString.equals(value)) {
                    throw new JsonbException(Messages.getMessage(MessageKeys.UNPAIRED_SURROGATE));
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return value;
    }

    @Override
    <X> boolean supports(X value) {
        Objects.requireNonNull(value);
        return value instanceof String;
    }
}
