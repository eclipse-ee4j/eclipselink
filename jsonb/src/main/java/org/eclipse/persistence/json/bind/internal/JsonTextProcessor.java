/*******************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 * Dmitry Kornilov - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.json.bind.internal;

import org.eclipse.persistence.json.bind.internal.conversion.ConvertersMapTypeConverter;
import org.eclipse.persistence.json.bind.internal.conversion.TypeConverter;

/**
 * Common parent for marshalling and unmarshalling shared logic.
 *
 * @author Roman Grigoriadi
 */
public abstract class JsonTextProcessor {


    protected static final String DOUBLE_INFINITY = "INFINITY";
    protected static final String DOUBLE_NAN = "NaN";
    protected static final String NULL = "null";

    protected final MappingContext mappingContext;

    protected TypeConverter converter;

    public JsonTextProcessor(MappingContext mappingContext) {
        this.mappingContext = mappingContext;
        this.converter = ConvertersMapTypeConverter.getInstance();
    }

}
