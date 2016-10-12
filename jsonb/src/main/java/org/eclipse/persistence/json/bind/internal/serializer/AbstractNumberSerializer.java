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
import org.eclipse.persistence.json.bind.internal.Marshaller;
import org.eclipse.persistence.json.bind.model.JsonBindingModel;

import javax.json.stream.JsonGenerator;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.function.Consumer;

/**
 * Common serializer for numbers, using number format.
 *
 * @author Roman Grigoriadi
 */
public abstract class AbstractNumberSerializer<T extends Number> extends AbstractValueTypeSerializer<T> {

    public AbstractNumberSerializer(Class<T> clazz, JsonBindingModel model) {
        super(model);
    }

    @Override
    protected final void serialize(T obj, JsonGenerator generator, String key, Marshaller marshaller) {
        if (!serializeFormatted(obj, formatted -> generator.write(key, formatted), marshaller.getJsonbContext())) {
            serializeNonFormatted(obj, generator, key);
        }
    }

    /**
     * Serialize raw number when NumberFormat is not present.
     * @param obj number
     * @param generator generator to use
     * @param key json key
     */
    protected abstract void serializeNonFormatted(T obj, JsonGenerator generator, String key);


    @Override
    protected void serialize(T obj, JsonGenerator generator, Marshaller marshaller) {
        if (!serializeFormatted(obj, generator::write, marshaller.getJsonbContext())) {
            serializeNonFormatted(obj, generator);
        }
    }

    /**
     * Serialize raw number when NumberFormat is not present.
     * @param obj number
     * @param generator generator to use
     */
    protected abstract void serializeNonFormatted(T obj, JsonGenerator generator);

    private boolean serializeFormatted(T obj, Consumer<String> formattedConsumer, JsonbContext jsonbContext) {
        final JsonbNumberFormatter numberFormat = model.getCustomization().getNumberFormat();
        if (numberFormat != null) {
            //TODO perf consider synchronizing on format instance or per thread cache.
            final NumberFormat format = NumberFormat.getInstance(jsonbContext.getLocale(numberFormat.getLocale()));
            ((DecimalFormat)format).applyPattern(numberFormat.getFormat());
            formattedConsumer.accept(format.format(obj));
            return true;
        }
        return false;
    }

}
