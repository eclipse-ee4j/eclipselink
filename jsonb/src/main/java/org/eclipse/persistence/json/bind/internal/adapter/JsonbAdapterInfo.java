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

package org.eclipse.persistence.json.bind.internal.adapter;

import javax.json.bind.adapter.JsonbAdapter;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * Wrapper for JsonbAdapter generic information and an adapter itself.
 *
 * @author Roman Grigoriadi
 */
public class JsonbAdapterInfo {

    private final Type fromType;

    private final Type toType;

    private final JsonbAdapter<?,?> adapter;

    /**
     * Adapter info with type to "adapt from", type to "adapt to" and an adapter itself.
     * @param fromType from not null
     * @param toType to not null
     * @param adapter adapter not null
     */
    public JsonbAdapterInfo(Type fromType, Type toType, JsonbAdapter<?, ?> adapter) {
        Objects.requireNonNull(fromType);
        Objects.requireNonNull(toType);
        Objects.requireNonNull(adapter);
        this.fromType = fromType;
        this.toType = toType;
        this.adapter = adapter;
    }

    /**
     * Represents a type from which to adapt into.
     * For example a type of a field in a class that we would like to adapt and map differently.
     * During marshalling object of Type is adapted to custom object and than mapped.
     * During unmarshalling object is unmarshalled into adapted type first, than converted to field type and set.
     *
     * @return Type from which to adapt
     */
    public Type getFromType() {
        return fromType;
    }

    /**
     * Represents a type to which to adapt into.
     *
     * During marshalling object property is adapted to this type and result is marshalled
     * During unmarshalling object is unmarshalled into this type first, than converted to field type and set.
     *
     * @return Type from which to adapt
     */
    public Type getToType() {
        return toType;
    }

    /**
     * Get actual adapter to adapt object value
     * @return adapter
     */
    public JsonbAdapter<?, ?> getAdapter() {
        return adapter;
    }
}
