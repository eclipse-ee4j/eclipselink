/*******************************************************************************
 * Copyright (c) 2016 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * <p>
 * Contributors:
 * Roman Grigoriadi
 ******************************************************************************/

package org.eclipse.persistence.json.bind.internal.unmarshaller;

import org.eclipse.persistence.json.bind.internal.ProcessingContext;
import org.eclipse.persistence.json.bind.internal.ReflectionUtils;
import org.eclipse.persistence.json.bind.internal.Unmarshaller;
import org.eclipse.persistence.json.bind.internal.naming.PropertyNamingStrategy;
import org.eclipse.persistence.json.bind.internal.properties.MessageKeys;
import org.eclipse.persistence.json.bind.internal.properties.Messages;

import javax.json.bind.JsonbException;
import javax.json.stream.JsonParser;
import java.lang.reflect.Type;

/**
 * @author Roman Grigoriadi
 */
public abstract class AbstractUnmarshallerItem<T> extends AbstractItem<T> implements UnmarshallerItem<T> {

    private final Unmarshaller ctx;

    private final JsonParser parser;

    private String currentFieldName;

    private JsonParser.Event currentValueType;

    /**
     * Create instance of current item with its builder.
     *
     * @param builder
     */
    protected AbstractUnmarshallerItem(UnmarshallerItemBuilder builder) {
        super(builder);
        this.ctx = builder.getDeserializationContext();
        this.parser = builder.getParser();
    }

    /**
     * Determine class mappings and create an instance of a new item.
     * Currently processed item is pushed to stack, for waiting till new object is finished.
     */
    private void onObjectStarted(JsonValueType jsonValueType) {
        UnmarshallerItem<?> item = newItem(getLastPropertyName(), jsonValueType);
        item.deserialize();
    }

    @Override
    public boolean hasNext() {
        return !parsed() && parser.hasNext();
    }

    @Override
    public JsonParser.Event next() {
        if (parsed()) {
            throw new JsonbException(Messages.getMessage(MessageKeys.END_OF_JSON_STRUCTURE));
        }
        currentValueType = convertEvent(parser.next());
        return currentValueType;
    }

    private JsonParser.Event convertEvent(JsonParser.Event event) {
        switch (event) {
            case KEY_NAME:
                return JsonParser.Event.KEY_NAME;
            case START_ARRAY:
                return JsonParser.Event.START_ARRAY;
            case START_OBJECT:
                return JsonParser.Event.START_OBJECT;
            case END_ARRAY:
                return JsonParser.Event.END_ARRAY;
            case END_OBJECT:
                return JsonParser.Event.END_OBJECT;
            case VALUE_FALSE:
                return JsonParser.Event.VALUE_FALSE;
            case VALUE_TRUE:
                return JsonParser.Event.VALUE_TRUE;
            case VALUE_NULL:
                return JsonParser.Event.VALUE_NULL;
            case VALUE_NUMBER:
                return JsonParser.Event.VALUE_NUMBER;
            case VALUE_STRING:
                return JsonParser.Event.VALUE_STRING;
            default:
                throw new UnsupportedOperationException("Unsupported event: " + event);
        }
    }

    /**
     * True if JSON document structure for current element has reached its end.
     * @return true if parsed
     */
    protected boolean parsed() {
        return currentValueType == JsonParser.Event.END_OBJECT || currentValueType == JsonParser.Event.END_ARRAY;
    }


    /**
     * Drives JSONP {@link JsonParser} to deserialize json document.
     *
     * @return instance of a type for this item
     */
    @Override
    public final void deserialize() {
        ctx.setCurrent(this);
        deserializeInternal();
        ctx.setCurrent((UnmarshallerItem<?>) getWrapper());
    }

    protected void deserializeInternal() {
        while (hasNext()) {
            final JsonParser.Event event = next();
            switch (event) {
                case START_OBJECT:
                case START_ARRAY:
                    onObjectStarted(JsonValueType.of(event));
                    break;
                case END_OBJECT:
                case END_ARRAY:
                    if (getWrapper() != null) {// root
                        ((UnmarshallerItem) getWrapper()).appendItem(this);
                    }
                    return;
                case VALUE_FALSE:
                    onValue(Boolean.FALSE.toString(), JsonValueType.of(event));
                    break;
                case VALUE_TRUE:
                    onValue(Boolean.TRUE.toString(), JsonValueType.of(event));
                    break;
                case VALUE_STRING:
                case VALUE_NUMBER:
                    onValue(parser.getString(), JsonValueType.of(event));
                    break;
                case VALUE_NULL:
                    onValue(null, JsonValueType.NULL);
                    break;
                case KEY_NAME:
                    currentFieldName = parser.getString();
                    break;
                default:
                    throw new JsonbException(Messages.getMessage(MessageKeys.NOT_VALUE_TYPE, event));
            }
        }
    }

    /**
     * Create supported type from JSON value.
     * @param value A JSON value.
     */
    private void onValue(String value, JsonValueType type) {
        appendValue(getLastPropertyName(), value, type);
    }

    /**
     * Last key in JSON document for current item structure.
     * @return last json key
     */
    protected String getLastPropertyName() {
        final PropertyNamingStrategy namingStrategy = ProcessingContext.getJsonbContext().getPropertyNamingStrategy();
        return namingStrategy != null ? namingStrategy.toModelPropertyName(currentFieldName) : currentFieldName;
    }

    protected UnmarshallerItemBuilder newUnmarshallerItemBuilder() {
        return new UnmarshallerItemBuilder(ctx, parser).withWrapper(this);
    }

    protected Unmarshaller getContext() {
        return ctx;
    }

    protected JsonParser getParser() {
        return parser;
    }

    protected UnmarshallerItem<?> newCollectionOrMapItem(String fieldName, Type valueType, JsonValueType jsonValueType) {
        Type actualValueType = ReflectionUtils.resolveType(this, valueType);
        actualValueType = actualValueType != Object.class ? actualValueType : jsonValueType.getConversionType();
        return newUnmarshallerItemBuilder().withType(actualValueType).withJsonKeyName(fieldName).withJsonValueType(jsonValueType).build();
    }

    @Override
    public JsonParser.Event getLastEvent() {
        if (currentValueType == null && getWrapper() != null) {
            currentValueType = ((UnmarshallerItem<?>) getWrapper()).getLastEvent();
        }
        return currentValueType;
    }
}
