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

import org.eclipse.persistence.json.bind.internal.Marshaller;
import org.eclipse.persistence.json.bind.model.JsonBindingModel;

import javax.json.stream.JsonGenerator;
import java.util.OptionalLong;

/**
 * @author David Král
 */
public class OptionalLongTypeSerializer extends AbstractValueTypeSerializer<OptionalLong> {

    public OptionalLongTypeSerializer(JsonBindingModel model) {
        super(model);
    }

    @Override
    protected void serialize(OptionalLong obj, JsonGenerator generator, String key, Marshaller marshaller) {
        if (obj.isPresent()) {
            generator.write(key, obj.getAsLong());
        } else if (model.getCustomization().isNillable()) {
            generator.writeNull(key);
        }
    }

    @Override
    protected void serialize(OptionalLong obj, JsonGenerator generator, Marshaller marshaller) {
        if (obj.isPresent()) {
            generator.write(obj.getAsLong());
        } else if (model.getCustomization().isNillable()) {
            generator.writeNull();
        }
    }
}
