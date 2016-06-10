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

import org.eclipse.persistence.json.bind.model.SerializerBindingModel;

import javax.json.stream.JsonGenerator;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * @author David Král
 */
public class InstantTypeSerializer extends AbstractValueTypeSerializer<Instant> {

    public InstantTypeSerializer(SerializerBindingModel model) {
        super(Instant.class, model);
    }

    @Override
    protected void serialize(Instant obj, JsonGenerator generator, String key) {
        generator.write(key, formatInstant(obj));
    }

    @Override
    protected void serialize(Instant obj, JsonGenerator generator) {
        generator.write(formatInstant(obj));
    }

    private String formatInstant(Instant obj) {
        return DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC).format(obj);
    }
}
