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

import org.eclipse.persistence.json.bind.internal.Unmarshaller;
import org.eclipse.persistence.json.bind.model.JsonBindingModel;

import java.lang.reflect.Type;

/**
 * @author David Král
 */
public class ByteTypeDeserializer extends AbstractNumberDeserializer<Byte> {

    public ByteTypeDeserializer(JsonBindingModel model) {
        super(Byte.class, model);
    }

    @Override
    protected Byte deserialize(String value, Unmarshaller unmarshaller, Type rtType) {
        return deserializeForamtted(value, true).map(num->Byte.parseByte(num.toString()))
                .orElseGet(()->Byte.parseByte(value));
    }
}
